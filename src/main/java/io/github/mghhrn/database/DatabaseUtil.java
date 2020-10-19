package io.github.mghhrn.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static io.github.mghhrn.CuriousCrawler.ROOT_STORAGE_PATH;

public class DatabaseUtil {

    private static final String databaseRootPath = ROOT_STORAGE_PATH + File.separator + "db";

    /*
     * In Linux systems it will be: "jdbc:sqlite:/tmp/curious-crawler/db/crawler.db"
     */
    private static final String databaseUrl = "jdbc:sqlite:" + databaseRootPath + File.separator + "crawler.db";

    private static final String dropProductTableSql = "DROP TABLE IF EXISTS product";
    private static final String createProductTableSql = "CREATE TABLE IF NOT EXISTS product ( \n" +
                                                            "name TEXT NOT NULL, \n" +
                                                            "price TEXT NOT NULL, \n" +
                                                            "description TEXT, \n" +
                                                            "extra_information TEXT )";

    public static void initializeDatabase() {
        createDirectoryIfNotExisted(ROOT_STORAGE_PATH);
        createDirectoryIfNotExisted(databaseRootPath);

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

    public static void createDirectoryIfNotExisted(String directoryPath) {
        File directory = new File(directoryPath);
        if (!directory.exists()) {
            directory.mkdir();
        }
    }
}
