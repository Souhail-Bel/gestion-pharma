package application.dao;
import application.modeles.Employe;
import application.modeles.Produit;

import java.sql.*;

public class produitDAO {
    private Connection connection;

    public produitDAO(Connection connection) {
        this.connection = connection;
    }
    public void Register(Produit p){
        String query="INSERT INTO PRODUIT(nom,prixvente,seuilminimal) VALUES(?,?,?);";
        try {
            if (FindByNAME(p.getNom()) == null) {
                PreparedStatement stmt = connection.prepareStatement(query);
                stmt.executeQuery();
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }
    public int taille(){
        String query="SELECT COUNT(id) FROM PRODUIT";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(0);
            } else {
                return 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void updateDB(Produit p){
        String query="UPDATE PRODUIT SET (prixnente=?,seuilminimal=?) WHERE produit_id=?;";
        try{
            PreparedStatement stmt=connection.prepareStatement(query);
            stmt.setDouble(1,p.getPrixVente());
            stmt.setInt(2,p.getSeuilMinimal());
            stmt.setInt(3,p.getId());
            int rows=stmt.executeUpdate();
            if (rows==1)
                System.out.println("Mise à jour réussite");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public Produit FindByNAME(String nom) throws SQLException {
        String query = "SELECT * FROM PRODUIT WHERE nom = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, nom);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Produit(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getDouble("prixvente"),
                        rs.getInt("seuilminimal")
                );
            } else {
                return null;
            }
        }
    }
        public Produit FindByID(int id) throws SQLException {
        String query = "SELECT * FROM PRODUIT WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Produit(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getDouble("prixvente"),
                        rs.getInt("seuilminimal")
                );
            } else {
                return null;
            }
        }
    }

    public void delete(int id) {
        String query="DELETE FROM PRODUIT WHERE produit_id=?;";
        try{
            PreparedStatement stmt=connection.prepareStatement(query);
            stmt.setInt(1,id);
            int rows= stmt.executeUpdate();
            if(rows==1)
                System.out.println("Supression Réussite");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}