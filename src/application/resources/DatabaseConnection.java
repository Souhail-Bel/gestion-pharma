package application.resources;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String DB_NAME = System.getProperty("db.name", "Pharmacie");
    private static final String URL = "jdbc:mysql://localhost:3306/" + DB_NAME;
    private static final String USER = "pharma_user";
    private static final String PASSWORD = "securepass";


    private DatabaseConnection() {}

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError("MySQL JDBC driver not found on classpath: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
