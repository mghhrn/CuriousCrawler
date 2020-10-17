package io.github.mghhrn.database;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseUtil {

    private static final String databaseDirectoryPath = "/tmp/curious-crawler/db";
    private static final String databaseUrl = "jdbc:sqlite:/tmp/curious-crawler/db/crawler.db";

    private static final String dropProductTableSql = "DROP TABLE IF EXISTS product";
    private static final String createProductTableSql = "CREATE TABLE IF NOT EXISTS product ( \n" +
                                                            "name TEXT NOT NULL, \n" +
                                                            "price TEXT NOT NULL, \n" +
                                                            "description TEXT, \n" +
                                                            "extra_information TEXT )";

    public static void initializeDatabase() {
        File dbDirectory = new File(databaseDirectoryPath);
        if (!dbDirectory.exists()) {
            dbDirectory.mkdir();
        }
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
}
