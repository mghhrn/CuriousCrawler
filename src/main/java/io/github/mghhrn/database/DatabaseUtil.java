package io.github.mghhrn.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseUtil {

    private static final String rootPath = "/tmp/curious-crawler";
    private static final String databaseDirectoryPath = rootPath + "/db";
    private static final String databaseUrl = "jdbc:sqlite:/tmp/curious-crawler/db/crawler.db";

    private static final String dropProductTableSql = "DROP TABLE IF EXISTS product";
    private static final String createProductTableSql = "CREATE TABLE IF NOT EXISTS product ( \n" +
                                                            "name TEXT NOT NULL, \n" +
                                                            "price TEXT NOT NULL, \n" +
                                                            "description TEXT, \n" +
                                                            "extra_information TEXT )";

    public static void initializeDatabase() {

        createDirectoryIfNotExisted(rootPath);
        createDirectoryIfNotExisted(databaseDirectoryPath);

        try (Connection conn = DriverManager.getConnection(databaseUrl);
                Statement statement = conn.createStatement()) {
            statement.executeUpdate(dropProductTableSql);
            statement.executeUpdate(createProductTableSql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(databaseUrl);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return connection;
    }

    private static void createDirectoryIfNotExisted(String directoryPath) {
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdir();
        }
    }
}
