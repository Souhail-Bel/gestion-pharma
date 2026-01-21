package application.resources;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    // Database name can be provided via system property "db.name" or falls back to "gestion_pharma"
    private static final String DB_NAME = System.getProperty("db.name", "gestion_pharma");
    private static final String URL = "jdbc:mysql://localhost:3306/Pharmacie";
    private static final String USER = "root";
    private static final String PASSWORD = "Klonoa";

    // Private constructor to prevent object creation
    private DatabaseConnection() {}

    static {
        // Ensure the MySQL driver is available on the classpath early
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            // Fail fast if driver missing
            throw new ExceptionInInitializerError("MySQL JDBC driver not found on classpath: " + e.getMessage());
        }
    }

    /**
     * Returns a live connection or throws SQLException if it fails.
     * Callers should handle SQLException and not assume non-null.
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
