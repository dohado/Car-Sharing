package com.dohado.carsharing;

import com.dohado.carsharing.dao.*;
import com.dohado.carsharing.dao.impl.CarDaoImpl;
import com.dohado.carsharing.dao.impl.CompanyDaoImpl;
import com.dohado.carsharing.dao.impl.CustomerDaoImpl;
import com.dohado.carsharing.dao.model.Car;
import com.dohado.carsharing.dao.model.Company;
import com.dohado.carsharing.dao.model.Customer;
import com.dohado.carsharing.utils.db.CreateUpdateTablesScriptsProcessor;
import com.dohado.carsharing.utils.db.DBClient;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class App {
    private static final Scanner scanner = new Scanner(System.in);
    private static final DBClient DB_CLIENT = DBClient.getDBClient();
    private static final CustomerDao customerDao = new CustomerDaoImpl();
    private static final CompanyDao companyDao = new CompanyDaoImpl();
    private static final CarDao carDao = new CarDaoImpl();


    public static void main(String[] args) {
        CreateUpdateTablesScriptsProcessor.executeCreate();

        boolean exit = false;
        while (!exit) {
            printMainMenu();
            int chosenPosition = scanner.nextInt();
            scanner.nextLine();
            switch (chosenPosition) {
                case 0 -> exit = true;
                case 1 -> loginAsManager();
                case 2 -> loginAsCustomer();
                case 3 -> createCustomer();
            }
        }
        DB_CLIENT.closeConnection();
    }

    private static void printMainMenu() {
        System.out.println("""
                
                1. Log in as a manager
                2. Log in as a customer
                3. Create a customer
                0. Exit""");
    }

    private static void printManagerLoginMenu() {
        System.out.println("""

                1. Company list
                2. Create a company
                0. Back""");
    }

    private static void printCarMenu(Company company) {
        System.out.println("\n" + company.getName() + " company\n" +
                "1. Car list\n" +
                "2. Create a car\n" +
                "0. Back");
    }

    private static void printRentingMenu() {
        System.out.println("""
                
                1. Rent a car
                2. Return a rented car
                3. My rented car
                0. Back
                """);
    }

    private static void loginAsManager() {
        boolean exit = false;
        while (!exit) {
            printManagerLoginMenu();
            int option = scanner.nextInt();
            scanner.nextLine();
            switch (option) {
                case 1 -> chooseCompany().ifPresent(App::carList);
                case 2 -> {
                    System.out.println("Enter the company name:");
                    if (companyDao.setCompany(scanner.nextLine()) > 0) {
                        System.out.println("The company was created!");
                    } else {
                        System.out.println("Something went wrong. Please try to add company again");
                    }
                }
                case 0 -> exit = true;
            }
        }
    }

    private static void carList(Company company) {
        boolean exit = false;
        while (!exit) {
            printCarMenu(company);
            int option = scanner.nextInt();
            scanner.nextLine();
            switch (option) {
                case 1 -> {
                    System.out.println(company.getName() + " cars:");
                    List<Car> cars = carDao.getCarsByCompany(company);
                    if (!cars.isEmpty()) {
                        cars.forEach(car ->
                                System.out.printf("%d. %s%n", (cars.indexOf(car) + 1), car));
                    } else {
                        System.out.println("The car list is empty!");
                    }
                }
                case 2 -> {
                    System.out.println("Enter the car name:");
                    if (carDao.insertCar(new Car(0, scanner.nextLine(), company.getID())) > 0) {
                        System.out.println("The car was created!");
                    } else {
                        System.out.println("Something went wrong. Please try to add car again");
                    }
                }
                case 0 -> exit = true;
            }
        }
    }

    private static void createCustomer() {
        System.out.println("\nEnter the customer name:");
        customerDao.insertCustomer(scanner.nextLine());
        System.out.println("The customer was added!\n");
    }

    private static void loginAsCustomer() {
        List<Customer> customerList = customerDao.getAllCustomers();
        if (customerList == null || customerList.isEmpty()) {
            System.out.println("\nThe customer list is empty!\n");
        } else {
            int count = 0;
            int customerPosition = -1;
            do {
                if (count > 0) {
                    System.out.println("\nPlease enter a valid customer position");
                }
                System.out.println("\nChoose a customer");
                customerList.forEach(customer -> System.out.printf("%d. %s%n", customerList.indexOf(customer) + 1,
                        customer));
                System.out.println("0. Back");
                customerPosition = scanner.nextInt();
                scanner.nextLine();
                count++;
            } while (customerPosition > customerList.size() || customerPosition < 0);
            if (customerPosition > 0) {
                rentingProcessing(customerList.get(customerPosition - 1));
            }
        }

    }

    private static void rentingProcessing(Customer customer) {
        boolean exit = false;
        while (!exit) {
            printRentingMenu();
            int option = scanner.nextInt();
            scanner.nextLine();
            switch (option) {
                case 1 -> {
                    if (customer.getRented_car_id() > 0) {
                        System.out.println("You've already rented a car!");
                    } else {
                        chooseCompany().ifPresent(company -> assignCar(company, customer));
                    }
                }
                case 2 -> {
                    if (customer.getRented_car_id() == 0) {
                        System.out.println("You didn't rent a car!");
                    } else {
                        customerDao.setRentedCarIdToNull(customer.getId());
                        customer.setRented_car_id(0);
                        System.out.println("You've returned a rented car!");
                    }
                }
                case 3 -> {
                    if (customer.getRented_car_id() == 0) {
                        System.out.println("You didn't rent a car!\n");
                    } else {
                        Car car = carDao.getCarById(customer.getRented_car_id()).orElseThrow();
                        Company company = companyDao.getCompanyById(car.getCompanyId()).orElseThrow();
                        System.out.printf("Your rented car:%n%s from %s company%n", car, company);
                    }
                }
                case 0 -> exit = true;
            }
        }
    }

    private static Optional<Company> chooseCompany() {
        Optional<List<Company>> optionalCompanies = companyDao.getAllCompanies();
        if (optionalCompanies.isPresent()) {
            System.out.println("\nChoose a company:");
            List<Company> companies = optionalCompanies.get();
            companies.forEach(company ->
                    System.out.printf("%d. %s%n", (companies.indexOf(company) + 1), company));
            System.out.println("0. Back");
            int companyPosition = scanner.nextInt();
            if (companyPosition > 0) {
                return Optional.of(companies.get(companyPosition - 1));
            }
        } else {
            System.out.println("The company list is empty!");
        }
        return Optional.empty();
    }

    private static void assignCar(Company company, Customer customer) {
        List<Car> carList = carDao.getCompanyAvailableCars(company);
        if (carList.isEmpty()) {
            System.out.printf("No available cars in the %s company%n", company.getName());
        } else {
            System.out.println("\nChoose a car:");
            carList.forEach(car ->
                    System.out.printf("%d. %s%n", (carList.indexOf(car) + 1), car));
            System.out.println("0. Back");
            int chosenCar = scanner.nextInt();
            if (chosenCar > 0) {
                customerDao.setRentedCarId(customer.getId(), carList.get(chosenCar - 1).getID());
                customer.setRented_car_id(carList.get(chosenCar - 1).getID());
                System.out.printf("You rented '%s'%n", carList.get(chosenCar - 1).getName());
            }
        }
    }


}