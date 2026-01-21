package application.dao;

import application.database.DetailCommandeFournisseur;
import application.modeles.CommandeFournisseur;
import application.modeles.StatutCommande;
import application.services.reports.PerformanceFournissuers;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class commandefournisseurDAO {
    private Connection connection;
    public commandefournisseurDAO(Connection connection){this.connection=connection;}
    public ArrayList<DetailCommandeFournisseur> getAllCommandes() throws SQLException{
        ArrayList<DetailCommandeFournisseur> commande=new ArrayList<>();
        String querry = "SELECT COMMANDEFOURNISSEUR.commande_id as id,FOURNISSEUR.nom,COMMANDEFOURNISSEUR.dateCommande as date,COMMANDEFOURNISSEUR.statut as statut,SUM(LigneCommandeFournisseur.prix_achat * LigneCommandeFournisseur.quantite) AS total " +
                "FROM FOURNISSEUR " +
                "JOIN COMMANDEFOURNISSEUR ON FOURNISSEUR.id = COMMANDEFOURNISSEUR.fournisseur_id " +
                "JOIN LIGNECOMMANDEFOURNISSEUR ON COMMANDEFOURNISSEUR.id = LIGNECOMMANDEFOURNISSEUR.commande_id " +
                "GROUP BY COMMANDEFOURNISSEUR.id " +
                "ORDER BY total_achats DESC;";
        try (var stmt = connection.createStatement();
             var rs = stmt.executeQuery(querry)) {
            while (rs.next()) {
                int commandeId = rs.getInt("id");
                String nom = rs.getString("FOURNISSEUR.nom");
                LocalDateTime date = rs.getTimestamp("date").toLocalDateTime();
                StatutCommande statut=StatutCommande.valueOf(rs.getString("statut").toUpperCase());
                double total=rs.getDouble("total");
                commande.add(new DetailCommandeFournisseur(commandeId,nom,date,statut,total));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return commande;
    }
    public int taille(){
        String query="SELECT COUNT(id) FROM COMMANDEFOURNISSEUR";
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

}
