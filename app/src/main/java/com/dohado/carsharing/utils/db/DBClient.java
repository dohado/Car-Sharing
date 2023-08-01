package com.dohado.carsharing.utils.db;

import org.h2.jdbcx.JdbcDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

public class DBClient {

    private static volatile DBClient singleInstance;
    private static final String DB_URL = "jdbc:h2:./DB/carsharing;Mode=MySQL";
    private Connection connection;

    private DBClient() {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setURL(DB_URL);
        try {
            connection = dataSource.getConnection();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static DBClient getDBClient() {
        DBClient innerInstance = singleInstance;
        if (innerInstance != null) {
            return innerInstance;
        }

        synchronized (DBClient.class) {
            if (singleInstance == null) {
                singleInstance = new DBClient();
            }
            return singleInstance;
        }
    }

    public int executeUpdate(String query) {
        try (Statement statement = connection.createStatement()) {
            return statement.executeUpdate(query);
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public Optional<ResultSet> executeQuery(String query) {
        try {
            Statement statement = connection.createStatement();
            return Optional.ofNullable(statement.executeQuery(query));
        } catch (SQLException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public void closeConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            System.out.println("Error during connection closing.");
            e.printStackTrace();
        }
    }

}
