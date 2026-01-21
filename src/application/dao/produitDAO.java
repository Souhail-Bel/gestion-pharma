package application.dao;
import application.modeles.Employe;
import application.modeles.Produit;

import java.sql.*;

public class produitDAO {
    private Connection connection;

    public produitDAO(Connection connection) {
        this.connection = connection;
    }
    public Produit FindByID(int id) throws SQLException {
        String query = "SELECT * FROM PRODUIT WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Produit(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getDouble("prixVente"),
                        rs.getInt("seuilMinimal")
                );
            } else {
                return null;
            }
        }
    }
}