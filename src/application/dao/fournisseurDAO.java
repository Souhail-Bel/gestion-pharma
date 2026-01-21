package application.dao;

import application.modeles.Fournisseur;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class fournisseurDAO {
    private Connection connection;
    public fournisseurDAO(Connection connection){this.connection=connection;}
    public Fournisseur FindByNAME(String nom) throws SQLException {
        String query = "SELECT * FROM FOURNISSEUR WHERE nom = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, nom);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Fournisseur(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("telephone"),
                        rs.getString("email"),
                        rs.getString("adresse")
                );
            } else {
                return null;
            }
        }
    }
}
