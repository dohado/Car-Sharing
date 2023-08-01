package com.dohado.carsharing.dao.impl;

import com.dohado.carsharing.dao.model.Company;
import com.dohado.carsharing.utils.db.DBClient;
import com.dohado.carsharing.dao.CompanyDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CompanyDaoImpl implements CompanyDao {
    DBClient dbClient = DBClient.getDBClient();

    @Override
    public Optional<List<Company>> getAllCompanies() {
        List<Company> companies = new ArrayList<>();
        String query = "SELECT * FROM COMPANY ORDER BY ID;";
        Optional<ResultSet> optionalResultSet = dbClient.executeQuery(query);
        if (optionalResultSet.isEmpty()) return Optional.empty();
        ResultSet resultSet = optionalResultSet.get();
        try {
            while (resultSet.next()) {
                companies.add(new Company(resultSet.getInt("ID"), resultSet.getString("NAME")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return companies.isEmpty() ? Optional.empty() : Optional.of(new ArrayList<>(companies));
    }

    @Override
    public Optional<Company> getCompanyById(int id) {
        String query = "SELECT * FROM COMPANY WHERE ID = %d;".formatted(id);
        Optional<ResultSet> optionalResultSet = dbClient.executeQuery(query);
        if (optionalResultSet.isEmpty()) return Optional.empty();
        ResultSet resultSet = optionalResultSet.get();
        Company company = null;
        try {
            while (resultSet.next()) {
                company = new Company(resultSet.getInt("ID"), resultSet.getString("NAME"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(company);
    }

    @Override
    public int setCompany(String name) {
        return dbClient.executeUpdate("INSERT INTO COMPANY (NAME) " +
                "VALUES ('" + name + "');");
    }
}
