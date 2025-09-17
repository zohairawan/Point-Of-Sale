package utils;

import com.mysql.cj.jdbc.MysqlDataSource;
import storage.Products;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class PointOfSaleDB {

    public static List<Products> getTableAsList() {
        Properties properties = new Properties();
        String config = "src/main/resources/point-of-sale.properties";
        try {
            properties.load(Files.newInputStream(Path.of(config), StandardOpenOption.READ));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setServerName(properties.getProperty("serverName"));
        dataSource.setPort(Integer.parseInt(properties.getProperty("port")));
        dataSource.setDatabaseName(properties.getProperty("databaseName"));

        String query = "SELECT * FROM products";
        List<Products> products = new ArrayList<>();
        try (Connection connection = dataSource.getConnection(
                properties.getProperty("user"),
                System.getenv("MYSQL_PASS"));
             Statement statement = connection.createStatement()
        ) {
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()) {
                products.add(new Products(
                                resultSet.getInt("plu"),
                                resultSet.getString("name"),
                                resultSet.getDouble("price"),
                                resultSet.getInt("calculation_code")));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        products.forEach(System.out::println);
        return products;
    }
}