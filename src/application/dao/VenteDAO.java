package application.dao;

import application.modeles.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class VenteDAO {
    private Connection connection;

    public VenteDAO(Connection connection) {
        this.connection = connection;
    }

    public ArrayList<Vente> getAllVentes() throws SQLException {
        ArrayList<Vente> ventes = new ArrayList<>();
        String query = "SELECT * FROM VENTE ORDER BY dateVente DESC";

        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                Vente vente = new Vente(
                    rs.getInt("id"),
                    rs.getTimestamp("dateVente").toLocalDateTime(),
                    rs.getInt("client_id"),
                    rs.getInt("employe_id"),
                    rs.getDouble("total")
                );
                loadLignesForVente(vente);
                
                ventes.add(vente);
            }
        }
        return ventes;
    }

    private void loadLignesForVente(Vente vente) throws SQLException {
        String query = "SELECT lv.*, p.nom, p.prixVente " +
                       "FROM LIGNE_VENTE lv " +
                       "JOIN PRODUIT p ON lv.produit_id = p.id " +
                       "WHERE lv.vente_id = ?";
                       
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, vente.getId());
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {

                Produit p = new Produit(
                    rs.getInt("produit_id"),
                    rs.getString("nom"),
                    rs.getDouble("prixVente"), 
                    0
                );

                LigneVente ligne = new LigneVente(
                    rs.getInt("id"),
                    vente,
                    p,
                    rs.getInt("quantite")
                );
                
                vente.addLigne(ligne);
            }
        }
    }
    
    
    public double getTotalVentes() throws SQLException {
        String query = "SELECT SUM(total) FROM VENTE";
        
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            if (rs.next()) {
                return rs.getDouble(1);
            }
        }
        return 0.0;
    }
    
    
    
    
    public void save(Vente v) throws SQLException {
        String query = "INSERT INTO VENTE (client_id, employe_id, dateVente, total) VALUES (?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, v.getClient().getId());
            stmt.setInt(2, v.getEmploye().getId());
            stmt.setTimestamp(3, Timestamp.valueOf(v.getDate()));
            stmt.setDouble(4, v.getTotal());
            
            stmt.executeUpdate();
            

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int venteId = rs.getInt(1);
                saveLignes(v.getLignes(), venteId);
            }
        }
    }
    
    
    private void saveLignes(java.util.List<LigneVente> lignes, int venteId) throws SQLException {
        String query = "INSERT INTO LIGNE_VENTE (vente_id, produit_id, quantite, prixUnitaire) VALUES (?, ?, ?, ?)";
        String updateStock = "UPDATE STOCK SET quantiteDisponible = quantiteDisponible - ? WHERE produit_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query);
             PreparedStatement stockStmt = connection.prepareStatement(updateStock)) {
            
            for (LigneVente lv : lignes) {
                stmt.setInt(1, venteId);
                stmt.setInt(2, lv.getProduit().getId());
                stmt.setInt(3, lv.getQuantite());
                stmt.setDouble(4, lv.getPrixUnitaire());
                stmt.addBatch();

                stockStmt.setInt(1, lv.getQuantite());
                stockStmt.setInt(2, lv.getProduit().getId());
                stockStmt.addBatch();
            }
            stmt.executeBatch();
            stockStmt.executeBatch();
        }
    }
    
    
}