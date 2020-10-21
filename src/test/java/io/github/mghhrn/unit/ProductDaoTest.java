package io.github.mghhrn.unit;

import io.github.mghhrn.database.DatabaseUtil;
import io.github.mghhrn.database.ProductDao;
import io.github.mghhrn.entity.Product;
import org.junit.Test;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

public class ProductDaoTest {

    @Test
    public void given_aProduct_when_insertToDB_then_recordCanBeRetrievedFromDB() throws IOException, SQLException {
        DatabaseUtil.initializeDatabase();
        Product product = new Product("FictionalProduct", "$1000", "Jaw-dropping details!", "For more information refer to elder scripts");
        ProductDao.save(product);
        String selectQuery = "SELECT rowid, name, price FROM product WHERE name = ?";
        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(selectQuery)) {
            statement.setString(1, product.getName());
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                assertThat(rs.getInt("rowid")).isEqualTo(1);
                assertThat(rs.getString("name")).isEqualTo(product.getName());
                assertThat(rs.getString("price")).isEqualTo(product.getPrice());
            }
        } catch (SQLException e) {
            throw e;
        }
    }
}
