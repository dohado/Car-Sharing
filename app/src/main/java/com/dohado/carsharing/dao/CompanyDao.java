package com.dohado.carsharing.dao;

import com.dohado.carsharing.dao.model.Company;

import java.util.List;
import java.util.Optional;

public interface CompanyDao {

    Optional<List<Company>> getAllCompanies();

    Optional<Company> getCompanyById(int id);

    int setCompany(String name);

}
