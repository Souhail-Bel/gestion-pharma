package application.dao;

import java.sql.Connection;
import java.sql.SQLException;

import application.modeles.Stock;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
public class StockDAO {
    private Connection connection;
    
    public StockDAO(Connection connection) {
        this.connection = connection;
    }
    
    public ArrayList<Stock> getAllStocks() throws SQLException {
        String query = "SELECT * FROM STOCK";
        ArrayList<Stock> stocks = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Stock stock = new Stock(
                    rs.getInt("produit_id"),
                    rs.getInt("quantiteDisponible")
                );
                stocks.add(stock);
            }
        }
        return stocks;
    }

    public void register(Stock s) throws SQLException {
        String query = "INSERT INTO STOCK (produit_id,quantitedisponible) VALUES(?,?);";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, s.getProduitId());
            stmt.setInt(2, s.getQuantiteDisponible());
            stmt.executeUpdate();
        }
    }

    public Stock FindByName(String nom) throws SQLException {
        String query = "SELECT * FROM STOCK,PRODUIT WHERE nom = ? AND STOCK.produit_id=PRODUIT.id";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, nom);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Stock(
                        rs.getInt("produit_id"),
                        rs.getInt("quantiteDisponible")
                );
            } else {
                return null;
            }
        }
    }
    
    public Stock findById(int prodId) throws SQLException {
        String query = "SELECT * FROM STOCK WHERE produit_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, prodId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Stock(rs.getInt("produit_id"), rs.getInt("quantiteDisponible"));
            }
            return null;
        }
    }
    
    public int getQuantiteProduit(int prodId) throws SQLException {
        String query = "SELECT quantiteDisponible FROM STOCK WHERE produit_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, prodId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public void updateQuantite(int produitId, int nouvelleQuantite) throws SQLException {
        String query = "UPDATE STOCK SET quantiteDisponible = ? WHERE produit_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, nouvelleQuantite);
            stmt.setInt(2, produitId);
            stmt.executeUpdate();
        }
    }

    public void augmenter(int produitId, int quantiteAjoutee) throws SQLException {
        String query = "INSERT INTO STOCK (produit_id, quantiteDisponible) VALUES (?, ?) " +
                "ON DUPLICATE KEY UPDATE quantiteDisponible = quantiteDisponible + ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, produitId);
            stmt.setInt(2, quantiteAjoutee);
            stmt.setInt(3, quantiteAjoutee);
            stmt.executeUpdate();
        }
    }

    public void delete(int id) {
        String query = "DELETE FROM STOCK WHERE produit_id=?;";
        try {
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();
            if (rows == 1)
                System.out.println("Supression Réussite");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
