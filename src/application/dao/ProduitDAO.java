package application.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import application.modeles.Produit;

public class ProduitDAO {

    private Connection connection;

    public ProduitDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Récupère tous les produits, triés par nom
     */
    public ArrayList<Produit> getAll() throws SQLException {
        ArrayList<Produit> list = new ArrayList<>();
        String query = "SELECT * FROM PRODUIT ORDER BY nom";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                list.add(new Produit(rs.getInt("id"), rs.getString("nom"),
                        rs.getDouble("prixVente"),
                        rs.getInt("seuilMinimal")));
            }
        }
        return list;
    }

    /**
     * Enregistre un nouveau produit dans la base de données
     *
     * @return L'ID généré du nouveau produit
     */
    public int register(Produit p) throws SQLException {
        String query
                = "INSERT INTO PRODUIT (nom, prixVente, seuilMinimal) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(
                query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, p.getNom());
            stmt.setDouble(2, p.getPrixVente());
            stmt.setInt(3, p.getSeuilMinimal());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return -1;
        }
    }

    /**
     * Compte le nombre total de produits dans la base de données
     */
    public int taille() {
        String query = "SELECT COUNT(id) FROM PRODUIT";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            } else {
                return 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Met à jour les informations d'un produit existant
     */
    public void update(Produit p) throws SQLException {
        String query
                = "UPDATE PRODUIT SET nom=?, prixVente=?, seuilMinimal=? WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, p.getNom());
            stmt.setDouble(2, p.getPrixVente());
            stmt.setInt(3, p.getSeuilMinimal());
            stmt.setInt(4, p.getId());
            stmt.executeUpdate();
        }
    }

    /**
     * Recherche un produit par son nom
     */
    public Produit findByName(String nom) throws SQLException {
        String query = "SELECT * FROM PRODUIT WHERE nom = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, nom);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Produit(rs.getInt("id"), rs.getString("nom"),
                        rs.getDouble("prixvente"),
                        rs.getInt("seuilminimal"));
            } else {
                return null;
            }
        }
    }

    /**
     * Recherche un produit par son ID
     */
    public Produit findById(int id) throws SQLException {
        String query = "SELECT * FROM PRODUIT WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Produit(rs.getInt("id"), rs.getString("nom"),
                        rs.getDouble("prixVente"),
                        rs.getInt("seuilMinimal"));
            } else {
                return null;
            }
        }
    }

    public void delete(int id) {
        String query = "DELETE FROM PRODUIT WHERE produit_id=?;";
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
