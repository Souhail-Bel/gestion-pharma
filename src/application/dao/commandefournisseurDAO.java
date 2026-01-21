package application.dao;

import application.database.DetailCommandeFournisseur;
import application.modeles.CommandeFournisseur;
import application.modeles.StatutCommande;
import application.resources.DatabaseConnection;
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
    public void register(CommandeFournisseur comF){
        String query="INSERT INTO COMMANDEFOURNISSEUR(fournisseur_id,statut) VALUES(?,?);";
        try {
            if (FindByID(comF.getId()) == null) {
                PreparedStatement stmt = connection.prepareStatement(query);
                stmt.setInt(1,comF.getFournisseur().getId());
                stmt.setString(2, String.valueOf(comF.getStatut()));
                stmt.executeQuery();
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }
    public CommandeFournisseur FindByID(int id) throws SQLException {
        String query = "SELECT * FROM COMMANDEFOURNISSEUR,FOURNISSEUR WHERE COMMANDEFOURNISSEUR.id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            fournisseurDAO fDao=new fournisseurDAO(DatabaseConnection.getConnection());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new CommandeFournisseur(
                        rs.getInt("COMMANDEFOURNISSEUR.id"),
                        fDao.FindByNAME(rs.getString("FOURNISSEUR.nom")),
                        rs.getTimestamp("COMMANDEFOURNISSEUR.dateCommande").toLocalDateTime(),
                        rs.getString("COMMANDEFOURNISSEUR.statut")
                );
            } else {
                return null;
            }
        }
    }
    public void updateDB(CommandeFournisseur comF){
        String query="UPDATE COMMANDEFOURNISSEUR SET (fournisseur_id=?,statut=?) WHERE id=?;";
        try{
            PreparedStatement stmt=connection.prepareStatement(query);
            stmt.setInt(1,comF.getFournisseur().getId());
            stmt.setString(2, String.valueOf(comF.getStatut()));;
            stmt.setInt(3,comF.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
