package com.dohado.carsharing.dao.impl;

import com.dohado.carsharing.utils.db.DBClient;
import com.dohado.carsharing.dao.VersionDao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VersionDaoImpl implements VersionDao {

    private final DBClient dbClient = DBClient.getDBClient();

    @Override
    public List<String> getVersions() {
        List<String> versions = new ArrayList<>();
        Optional<ResultSet> optionalResultSet = dbClient.executeQuery("SELECT * FROM VERSION ORDER BY ID;");
        if (optionalResultSet.isPresent()) {
            ResultSet resultSet = optionalResultSet.get();
            try {
                while (resultSet.next()) {
                    versions.add(resultSet.getString("VERSION_NAME"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return versions;
    }

    @Override
    public int insertNewVersion(String versionName) {
        return dbClient.executeUpdate("INSERT INTO VERSION (VERSION_NAME)" +
                "VALUES ('" + versionName + "');");
    }
}
