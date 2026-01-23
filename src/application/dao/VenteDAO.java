package application.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import application.modeles.Client;
import application.modeles.Employe;
import application.modeles.LigneVente;
import application.modeles.Produit;
import application.modeles.Vente;

public class VenteDAO {

    private Connection connection;

    public VenteDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Récupère toutes les ventes, triées par date décroissante Charge aussi les
     * lignes (détails) de chaque vente
     */
    public ArrayList<Vente> getAllVentes() throws SQLException {
        ArrayList<Vente> ventes = new ArrayList<>();
        String query = "SELECT * FROM VENTE ORDER BY dateVente DESC";

        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                Vente vente = new Vente(rs.getInt("id"),
                        rs.getTimestamp("dateVente").toLocalDateTime(),
                        rs.getInt("client_id"), rs.getInt("employe_id"),
                        rs.getDouble("total"));
                loadLignesForVente(vente);

                ventes.add(vente);
            }
        }
        return ventes;
    }

    /**
     * Charge les lignes (articles vendus) associées à une vente Récupère les
     * produits avec les prix historiques
     */
    public void loadLignesForVente(Vente vente) throws SQLException {

        if (!vente.getLignes().isEmpty()) {
            return;
        }

        String query = "SELECT lv.*, p.nom "
                + "FROM LIGNE_VENTE lv "
                + "JOIN PRODUIT p ON lv.produit_id = p.id "
                + "WHERE lv.vente_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, vente.getId());
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                double prixHistorique = rs.getDouble("prixUnitaire");

                Produit p = new Produit(rs.getInt("produit_id"), rs.getString("nom"),
                        prixHistorique, 0);

                LigneVente ligne
                        = new LigneVente(rs.getInt("id"), vente, p, rs.getInt("quantite"));

                vente.addLigne(ligne);
            }
        }
    }

    /**
     * Calcule le montant total de toutes les ventes
     */
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

    /**
     * Sauvegarde une vente complète avec toutes ses lignes Met à jour
     * automatiquement le stock pour chaque article vendu
     */
    public void save(Vente v) throws SQLException {
        String query = "INSERT INTO VENTE (client_id, employe_id, dateVente, "
                + "total) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(
                query, Statement.RETURN_GENERATED_KEYS)) {
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

    private void saveLignes(List<LigneVente> lignes, int venteId)
            throws SQLException {
        String query = "INSERT INTO LIGNE_VENTE (vente_id, produit_id, quantite, "
                + "prixUnitaire) VALUES (?, ?, ?, ?)";
        String updateStock = "UPDATE STOCK SET quantiteDisponible = "
                + "quantiteDisponible - ? WHERE produit_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query); PreparedStatement stockStmt
                = connection.prepareStatement(updateStock)) {

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

    /**
     * Recherche des ventes selon des critères de filtrage Permet de chercher
     * par plage de dates et/ou nom de client
     */
    public List<Vente> findByFilters(LocalDate dateMin, LocalDate dateMax,
            String clientNom) throws SQLException {

        StringBuilder query = new StringBuilder(
                "SELECT v.id, v.dateVente, v.total, "
                + "c.id as client_id, c.nom as client_nom, c.prenom as client_prenom, "
                + "c.telephone, "
                + "e.id as emp_id, e.nom as emp_nom, e.prenom as emp_prenom, "
                + "e.username, e.password, e.role "
                + "FROM VENTE v "
                + "JOIN CLIENT c ON v.client_id = c.id "
                + "JOIN EMPLOYE e ON v.employe_id = e.id "
                + "WHERE 1=1 ");

        if (dateMin != null) {
            query.append("AND DATE(v.dateVente) >= ? ");
        }

        if (dateMax != null) {
            query.append("AND DATE(v.dateVente) <= ? ");
        }

        if (clientNom != null && !clientNom.trim().isEmpty()) {
            query.append("AND LOWER(c.nom) LIKE LOWER(?) ");
        }
        query.append("ORDER BY v.dateVente DESC");

        try (PreparedStatement stmt
                = connection.prepareStatement(query.toString())) {
            int paramIndex = 1;

            if (dateMin != null) {
                stmt.setDate(paramIndex++, Date.valueOf(dateMin));
            }

            if (dateMax != null) {
                stmt.setDate(paramIndex++, Date.valueOf(dateMax));
            }

            if (clientNom != null && !clientNom.trim().isEmpty()) {
                stmt.setString(paramIndex++, "%" + clientNom.trim() + "%");
            }

            List<Vente> ventes = new ArrayList<>();

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Client client = new Client(
                        rs.getInt("client_id"), rs.getString("client_nom"),
                        rs.getString("client_prenom"), rs.getString("telephone"));

                Employe employe
                        = new Employe(rs.getInt("emp_id"), rs.getString("emp_nom"),
                                rs.getString("emp_prenom"), rs.getString("username"),
                                rs.getString("password"), rs.getString("role"));

                Vente vente = new Vente(rs.getInt("id"),
                        rs.getTimestamp("dateVente").toLocalDateTime(),
                        client.getId(), employe.getId(), rs.getDouble("total"));
                ventes.add(vente);
            }
            return ventes;
        }
    }
}
