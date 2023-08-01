package com.dohado.carsharing.dao;

import com.dohado.carsharing.dao.model.Customer;

import java.util.List;

public interface CustomerDao {

    int insertCustomer(String name);

    List<Customer> getAllCustomers();

    int setRentedCarIdToNull(int customerId);

    int setRentedCarId(int customerId, int carId);

}
