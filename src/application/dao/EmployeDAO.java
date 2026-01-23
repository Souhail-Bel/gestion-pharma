package application.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import application.modeles.Employe;

public class EmployeDAO {

    private Connection connection;

    public EmployeDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Recherche un employé par son ID
     */
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

    /**
     * Recherche un employé par son nom d'utilisateur Utilisé pour
     * l'authentification lors de la connexion
     */
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
