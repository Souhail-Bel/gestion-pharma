package application.dao;
import application.modeles.Vente;
import application.services.reports.ChiffreAffaires;

import java.sql.*;
import java.util.ArrayList;

public class venteDAO {
    private Connection connection;

    public venteDAO(Connection connection) {
        this.connection = connection;
    }
    public ArrayList<Vente> getAllVentes() throws SQLException {
        ArrayList<Vente> ventes = new ArrayList<>();
        String query = "SELECT * FROM VENTE";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                Vente vente = new Vente(
                    // Assuming Vente has a constructor that takes these parameters
                    rs.getInt("id"),
                    rs.getTimestamp("date").toLocalDateTime(),
                    rs.getInt("client_id"),
                    rs.getInt("employe_id"),
                    rs.getDouble("total")
                );
                ventes.add(vente);
            }
        }
        return ventes;

    }
    public double getTotalVentes() throws SQLException {
        double total = 0.0;
        String query = "SELECT SUM(total) AS total_ventes FROM VENTE";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                total = rs.getDouble("total_ventes");
            }
        }
        return total;
    }
}
