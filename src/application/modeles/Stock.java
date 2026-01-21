package application.modeles;

import application.dao.ProduitDAO;
import java.sql.Connection;
import java.sql.SQLException;

public class Stock {
    private Produit produit;
    private int quantiteDisponible;

    public Stock(int produitID, int quantiteDisponible, Connection conn) throws SQLException {
        ProduitDAO pdao = new ProduitDAO(conn);
        this.produit = pdao.findByID(produitID);
        this.quantiteDisponible = quantiteDisponible;
    }

    public Stock(Produit produit, int quantiteDisponible) {
        this.produit = produit;
        this.quantiteDisponible = quantiteDisponible;
    }
    
    public Produit getProduit() { return produit; }
    
    public int getQuantiteDisponible() { return quantiteDisponible; }
    public void setQuantiteDisponible(int quantiteDisponible) { this.quantiteDisponible = quantiteDisponible; }
    

    public boolean estLowStock() {
        return quantiteDisponible <= produit.getSeuilMinimal();
    }
}