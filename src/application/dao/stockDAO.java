package application.dao;

import java.sql.Connection;
import java.sql.SQLException;

import application.modeles.Produit;
import application.modeles.Stock;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
public class stockDAO {
    private Connection connection;
    public stockDAO(Connection connection) {
        this.connection = connection;
    }
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

    public void Register(Stock s){
        String query="INSERT INTO STOCK(produit_id,quantiteDisponible) VALUES(?,?);";
        try {
            if (FindByNAME(s.getProduit().getNom()) == null) {
                PreparedStatement stmt = connection.prepareStatement(query);
                stmt.executeQuery();
            }
        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
    }
    public Stock FindByNAME(String nom) throws SQLException {
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
    public void updateStock(int id,int nouv_quantite){
        String query="UPDATE STOCK SET (quantiteDisponible=?) WHERE produit_id=?;";
        try{
            PreparedStatement stmt=connection.prepareStatement(query);
            int rows=stmt.executeUpdate();
            if (rows==1)
                System.out.println("Stockage réussit");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
