package io.github.mghhrn.database;

import io.github.mghhrn.entity.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ProductDao {

    private static final String insertProductSql = "INSERT INTO product(name, price, description, extra_information) VALUES (?, ?, ?, ?)";

    public static void save(Product product) {
        try (Connection connection = DatabaseUtil.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(insertProductSql)) {
            fillStatement(preparedStatement, product);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void fillStatement(PreparedStatement preparedStatement, Product product) throws SQLException {
        preparedStatement.setString(1, product.getName());
        preparedStatement.setString(2, product.getPrice());
        preparedStatement.setString(3, product.getDescription());
        preparedStatement.setString(4, product.getExtraInformation());
    }
}
