package com.dohado.carsharing.dao.impl;

import com.dohado.carsharing.dao.model.Customer;
import com.dohado.carsharing.utils.db.DBClient;
import com.dohado.carsharing.dao.CustomerDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CustomerDaoImpl implements CustomerDao {

    private final DBClient dbClient = DBClient.getDBClient();

    @Override
    public int insertCustomer(String name) {
        return dbClient.executeUpdate("INSERT INTO CUSTOMER (NAME)" +
                "VALUES ('" + name + "');");
    }

    @Override
    public List<Customer> getAllCustomers() {
        List<Customer> customerList = new ArrayList<>();
        Optional<ResultSet> optionalResultSet = dbClient.executeQuery("SELECT * FROM CUSTOMER ORDER BY ID");
        if (optionalResultSet.isPresent()) {
            ResultSet resultSet = optionalResultSet.get();
            try {
                while (resultSet.next()) {
                    customerList.add(new Customer(resultSet.getInt("ID"), resultSet.getString("NAME"),
                            resultSet.getInt("RENTED_CAR_ID")));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return List.copyOf(customerList);
    }

    @Override
    public int setRentedCarIdToNull(int customerId) {
        return dbClient.executeUpdate("UPDATE CUSTOMER SET RENTED_CAR_ID = NULL WHERE ID = " + customerId + ";");
    }

    @Override
    public int setRentedCarId(int customerId, int carId) {
        return dbClient.executeUpdate("UPDATE CUSTOMER SET RENTED_CAR_ID = " + carId + " WHERE ID = "
                + customerId + ";");
    }
}
