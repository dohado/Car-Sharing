package com.dohado.carsharing.dao.impl;

import com.dohado.carsharing.dao.model.Car;
import com.dohado.carsharing.dao.model.Company;
import com.dohado.carsharing.utils.db.DBClient;
import com.dohado.carsharing.dao.CarDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CarDaoImpl implements CarDao {

    private final DBClient dbClient = DBClient.getDBClient();

    @Override
    public List<Car> getCarsByCompany(Company company) {
        String query = """
                SELECT * FROM CAR WHERE COMPANY_ID = %d ORDER BY ID;""".formatted(company.getID());
        Optional<ResultSet> optionalResultSet = dbClient.executeQuery(query);
        return optionalResultSet.map(this::carListBuilder).orElse(new ArrayList<>());
    }

    @Override
    public Optional<Car> getCarById(int id) {
        String query = "SELECT * FROM CAR WHERE ID = %d;".formatted(id);
        Optional<ResultSet> optionalResultSet = dbClient.executeQuery(query);
        if (optionalResultSet.isEmpty()) return Optional.empty();
        ResultSet resultSet = optionalResultSet.get();
        Car car = null;
        try {
            while (resultSet.next()) {
                car = new Car(resultSet.getInt("ID"), resultSet.getString("NAME"),
                        resultSet.getInt("COMPANY_ID"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(car);
    }

    @Override
    public int insertCar(Car car) {
        return dbClient.executeUpdate("INSERT INTO CAR (NAME, COMPANY_ID)" +
                "VALUES ('" + car.getName() + "', '" + car.getCompanyId() + "');");
    }

    @Override
    public List<Car> getCompanyAvailableCars(Company company) {
        Optional<ResultSet> optionalResultSet = dbClient.executeQuery("SELECT * FROM CAR " +
                "LEFT JOIN CUSTOMER ON CAR.ID = CUSTOMER.RENTED_CAR_ID " +
                "WHERE CAR.COMPANY_ID = '" + company.getID() + "' " +
                "AND CUSTOMER.ID IS NULL " +
                "ORDER BY CAR.ID;");
        return optionalResultSet.map(this::carListBuilder).orElse(new ArrayList<>());
    }

    private List<Car> carListBuilder(ResultSet resultSet) {
        List<Car> cars = new ArrayList<>();
        try {
            while (resultSet.next()) {
                cars.add(new Car(resultSet.getInt("ID"), resultSet.getString("NAME"),
                        resultSet.getInt("COMPANY_ID")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return List.copyOf(cars);
    }
}
