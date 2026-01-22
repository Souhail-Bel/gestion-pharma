package application.dao;
import application.modeles.Employe;
import java.sql.*;

public class EmployeDAO {
    private Connection connection;

    public EmployeDAO(Connection connection) {
        this.connection = connection;
    }
    public Employe findById(int id) throws SQLException {
        String query = "SELECT * FROM EMPLOYE WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Employe(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("role")
                );
            } else {
                return null;
            }
        }
    }

    public Employe findEmployeByUsername(String username) throws SQLException {
        String query = "SELECT * FROM EMPLOYE WHERE username = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Employe(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("prenom"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("role")
                );
            } else {
                return null;
            }
        }
    }



}
