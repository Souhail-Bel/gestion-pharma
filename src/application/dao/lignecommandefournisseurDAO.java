package application.dao;

import application.services.reports.PerformanceFournissuers;

import java.sql.Connection;
import java.util.ArrayList;

public class lignecommandefournisseurDAO {
    private Connection connection;
    public lignecommandefournisseurDAO(Connection connection) {
        this.connection = connection;
    }
    public ArrayList<PerformanceFournissuers> FournisseursWork() {
        String querry = "SELECT FOURNISSEUR.id,COUNT(DISTINCT LIGNECOMMANDEFOURNISSEUR.commande_id) AS total_commandes,SUM(LIGNECOMMANDEFOURNISSEUR.prix_achat * LIGNECOMMANDEFOURNISSEUR.quantite) AS total_achats " +
                "FROM FOURNISSEUR" +
                "JOIN COMMANDEFOURNISSEUR ON FOURNISSEUR.id = COMMANDEFOURNISSEUR.fournisseur_id " +
                "JOIN LIGNECOMMANDEFOURNISSEUR ON COMMANDEFOURNISSEUR.id = LIGNECOMMANDEFOURNISSEUR.commande_id " +
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

