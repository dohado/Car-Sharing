package com.dohado.carsharing.utils.db;

import com.dohado.carsharing.dao.VersionDao;
import com.dohado.carsharing.dao.impl.VersionDaoImpl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

public class CreateUpdateTablesScriptsProcessor {

    private static final DBClient DB_CLIENT = DBClient.getDBClient();
    private static final VersionDao versionDao = new VersionDaoImpl();
    private static final File createScripts =
            new File("./src/main/resources/createscripts");
    private static final int SCRIPT_SUCCESSFULLY_EXECUTED = 1;
    private static final int SCRIPT_FAILED = -1;

    public static void executeCreate() {
        initialiseVersionTable();
        if (createScripts.isDirectory()) {
            File[] files = createScripts.listFiles();
            if (files != null && files.length != 0) {
                Arrays.sort(files, Comparator.comparing((File a) -> {
                    try {
                        return (FileTime) Files.getAttribute(a.toPath(), "lastModifiedTime");
                    } catch (IOException e) {
                        e.printStackTrace();
                        return FileTime.fromMillis(System.currentTimeMillis());
                    }
                }));
                StringBuilder result = new StringBuilder();
                List<String> versions = versionDao.getVersions();
                for (File file : files) {
                    if (!versions.contains(file.getName())) {
                        try (Scanner scanner = new Scanner(file)) {
                            scanner.useDelimiter(";");
                            int scriptResult = SCRIPT_SUCCESSFULLY_EXECUTED;
                            while (scanner.hasNext()) {
                                if (DB_CLIENT.executeUpdate(scanner.next().trim()) == -1) {
                                    scriptResult = SCRIPT_FAILED;
                                }
                            }
                            if (scriptResult == SCRIPT_SUCCESSFULLY_EXECUTED) {
                                if (versionDao.insertNewVersion(file.getName()) > -1) {
                                    result.append("Script ").append(file.getName()).append(" executed\n");
                                }
                            }
                            Thread.sleep(1000L);
                        } catch (FileNotFoundException | InterruptedException e) {
                            System.out.println("File not found");
                            e.printStackTrace();
                        }
                    }
                }
                System.out.println(result.toString().equals("") ? "No new scripts executed\n" : result.toString());
            }
        } else {
            System.out.println("No scripts found to initialize the DB tables\n");
        }
    }

    private static void initialiseVersionTable() {
        DB_CLIENT.executeUpdate("CREATE TABLE IF NOT EXISTS VERSION" +
                "(ID INTEGER AUTO_INCREMENT," +
                "VERSION_NAME VARCHAR(64)," +
                "CONSTRAINT pk_version_id PRIMARY KEY(ID)," +
                "CONSTRAINT uq_version_name UNIQUE(VERSION_NAME))");
    }


}
