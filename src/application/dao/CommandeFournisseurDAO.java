package application.dao;

import application.modeles.CommandeFournisseur;
import application.modeles.Fournisseur;
import application.modeles.LigneCommandeFournisseur;
import application.modeles.Produit;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommandeFournisseurDAO {
    private Connection connection;

    public CommandeFournisseurDAO(Connection connection) { this.connection = connection; }

    public ArrayList<CommandeFournisseur> getAll() throws SQLException {
        ArrayList<CommandeFournisseur> list = new ArrayList<>();
        String query = "SELECT c.*, f.id as f_id, f.nom, f.telephone, f.email, f.adresse FROM COMMANDE_FOURNISSEUR c JOIN FOURNISSEUR f ON c.fournisseur_id = f.id ORDER BY dateCommande DESC";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                Fournisseur f = new Fournisseur(rs.getInt("f_id"), rs.getString("nom"), rs.getString("telephone"), rs.getString("email"), rs.getString("adresse"));
                CommandeFournisseur cmd = new CommandeFournisseur(rs.getInt("id"), f, rs.getTimestamp("dateCommande").toLocalDateTime(), rs.getString("statut"));
                cmd.getLignes().addAll(getLignesForCommande(cmd.getId()));
                list.add(cmd);
            }
        }
        return list;
    }

    private ArrayList<LigneCommandeFournisseur> getLignesForCommande(int cmdId) throws SQLException {
        ArrayList<LigneCommandeFournisseur> lignes = new ArrayList<>();
        String query = "SELECT l.*, p.id as p_id, p.nom, p.prixVente, p.seuilMinimal FROM LIGNE_COMMANDE_FOURNISSEUR l JOIN PRODUIT p ON l.produit_id = p.id WHERE commande_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, cmdId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Produit p = new Produit(rs.getInt("p_id"), rs.getString("nom"), rs.getDouble("prixVente"), rs.getInt("seuilMinimal"));
                LigneCommandeFournisseur lc = new LigneCommandeFournisseur(rs.getInt("id"), null, p, rs.getInt("quantite"), rs.getDouble("prixAchat"));
                lignes.add(lc);
            }
        }
        return lignes;
    }

    public int save(CommandeFournisseur cmd) throws SQLException {
        String query = "INSERT INTO COMMANDE_FOURNISSEUR (fournisseur_id, dateCommande, statut) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, cmd.getFournisseur().getId());
            stmt.setTimestamp(2, Timestamp.valueOf(cmd.getDate()));
            stmt.setString(3, cmd.getStatut().toString());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int cmdId = rs.getInt(1);
                saveLignes(cmd.getLignes(), cmdId);
                return cmdId;
            }
            return -1;
        }
    }

    private void saveLignes(List<LigneCommandeFournisseur> lignes, int cmdId) throws SQLException {
        String query = "INSERT INTO LIGNE_COMMANDE_FOURNISSEUR (commande_id, produit_id, quantite, prixAchat) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            for (LigneCommandeFournisseur lc : lignes) {
                stmt.setInt(1, cmdId);
                stmt.setInt(2, lc.getProduit().getId());
                stmt.setInt(3, lc.getQuantite());
                stmt.setDouble(4, lc.getPrixAchat());
                stmt.addBatch();
            }
            stmt.executeBatch();
        }
    }

    public void update(CommandeFournisseur cmd) throws SQLException {
        String query = "UPDATE COMMANDE_FOURNISSEUR SET fournisseur_id=?, statut=? WHERE id=?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, cmd.getFournisseur().getId());
            stmt.setString(2, cmd.getStatut().toString());
            stmt.setInt(3, cmd.getId());
            stmt.executeUpdate();
        }
        // Delete old lines, save new
        String deleteLines = "DELETE FROM LIGNE_COMMANDE_FOURNISSEUR WHERE commande_id=?";
        try (PreparedStatement stmt = connection.prepareStatement(deleteLines)) {
            stmt.setInt(1, cmd.getId());
            stmt.executeUpdate();
        }
        saveLignes(cmd.getLignes(), cmd.getId());
    }
}