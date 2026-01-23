package application.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import application.modeles.Stock;

public class StockDAO {

    private Connection connection;

    public StockDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Récupère tous les stocks de la base de données
     */
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

    /**
     * Enregistre un nouveau stock dans la base de données Crée d'abord le
     * produit, puis le stock associé
     */
    public void register(Stock s) throws SQLException {
        ProduitDAO pDao = new ProduitDAO(connection);
        pDao.register(s.getProduit());
        String query = "INSERT INTO STOCK (produit_id,quantitedisponible) VALUES(?,?);";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, pDao.findByName(s.getProduit().getNom()).getId());
            stmt.setInt(2, s.getQuantiteDisponible());
            stmt.executeUpdate();
        }
    }

    /**
     * Recherche un stock par le nom du produit
     */
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

    /**
     * Recherche un stock par l'ID du produit
     */
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

    /**
     * Récupère la quantité disponible d'un produit spécifique
     */
    public int getQuantiteProduit(int prodId) throws SQLException {
        String query = "SELECT quantiteDisponible FROM STOCK WHERE produit_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, prodId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    /**
     * Met à jour la quantité disponible d'un produit
     */
    public void updateQuantite(int produitId, int nouvelleQuantite) throws SQLException {
        String query = "UPDATE STOCK SET quantiteDisponible = ? WHERE produit_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, nouvelleQuantite);
            stmt.setInt(2, produitId);
            stmt.executeUpdate();
        }
    }

    /**
     * Augmente la quantité disponible d'un produit Utilisé après un
     * réapprovisionnement
     */
    public void augmenter(int produitId, int quantiteAjoutee) throws SQLException {
        String query = "INSERT INTO STOCK (produit_id, quantiteDisponible) VALUES (?, ?) "
                + "ON DUPLICATE KEY UPDATE quantiteDisponible = quantiteDisponible + ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, produitId);
            stmt.setInt(2, quantiteAjoutee);
            stmt.setInt(3, quantiteAjoutee);
            stmt.executeUpdate();
        }
    }

    /**
     * Supprime un stock de la base de données
     */
    public void delete(int id) {
        String query = "DELETE FROM STOCK WHERE produit_id=?;";
        try {
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, id);
            int rows = stmt.executeUpdate();
            if (rows == 1) {
                System.out.println("Supression Réussite");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
