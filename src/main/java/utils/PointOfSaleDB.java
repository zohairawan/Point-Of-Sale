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

    private static final String config = "src/main/resources/point-of-sale.properties";

    public static List<Products> getProductsTableAsList() {
        Properties properties = loadPropertiesFile(config);
        MysqlDataSource pointOfSaleDB = loadDataSource(properties);
        return getProducts(pointOfSaleDB, properties);
    }

    private static List<Products> getProducts(MysqlDataSource pointOfSaleDB, Properties properties) {
        String query = "SELECT * FROM products";
        List<Products> products = new ArrayList<>();
        try (Connection connection = pointOfSaleDB.getConnection(
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
        return products;
    }

    private static Properties loadPropertiesFile(String config) {
        Properties properties = new Properties();
        try {
            properties.load(Files.newInputStream(Path.of(config), StandardOpenOption.READ));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return properties;
    }

    private static MysqlDataSource loadDataSource(Properties properties) {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setServerName(properties.getProperty("serverName"));
        dataSource.setPort(Integer.parseInt(properties.getProperty("port")));
        dataSource.setDatabaseName(properties.getProperty("databaseName"));
        return dataSource;
    }
}