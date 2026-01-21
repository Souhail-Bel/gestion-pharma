package application.dao;

import application.services.reports.PerformanceFournissuers;

import java.sql.Connection;
import java.util.ArrayList;

public class LigneCommandeFournisseurDAO {
    private Connection connection;
    public LigneCommandeFournisseurDAO(Connection connection) {
        this.connection = connection;
    }
    
    
    
    public ArrayList<PerformanceFournissuers> FournisseursWork() {
        String querry = "SELECT FOURNISSEUR.id,COUNT(LIGNE_COMMANDE_FOURNISSEUR.commande_id) AS total_commandes,SUM(LIGNE_COMMANDE_FOURNISSEUR.prixAchat * LIGNE_COMMANDE_FOURNISSEUR.quantite) AS total_achats " +
                "FROM FOURNISSEUR " +
                "JOIN COMMANDE_FOURNISSEUR ON FOURNISSEUR.id = COMMANDE_FOURNISSEUR.fournisseur_id " +
                "JOIN LIGNE_COMMANDE_FOURNISSEUR ON COMMANDE_FOURNISSEUR.id = LIGNE_COMMANDE_FOURNISSEUR.commande_id " +
                "GROUP BY FOURNISSEUR.id " +
                "ORDER BY total_achats DESC;";
        ArrayList<PerformanceFournissuers> p = new ArrayList<>();
        try (var stmt = connection.createStatement();
             var rs = stmt.executeQuery(querry)) {
            while (rs.next()) {
                int fournisseurId = rs.getInt("id");
                int totalCommandes = rs.getInt("total_commandes");
                double totalAchats = rs.getDouble("total_achats");
                PerformanceFournissuers perf = new PerformanceFournissuers(fournisseurId, totalCommandes, totalAchats);
                p.add(perf);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return p;
    }

    }

