package application.modeles;

import java.sql.SQLException;

import application.dao.ProduitDAO;
import application.resources.DatabaseConnection;

public class Stock {

    private int produitId;
    private Produit produit;

    private int quantiteDisponible;

    public Stock(int produitId, int quantiteDisponible) {
        this.produitId = produitId;
        this.quantiteDisponible = quantiteDisponible;
    }

    public Stock(Produit p, int quantiteDisponible) {
        this.produitId = (p != null) ? p.getId() : 0;
        this.produit = p;
        this.quantiteDisponible = quantiteDisponible;
    }

    public int getProduitId() {
        return produitId;
    }

    public int getQuantiteDisponible() {
        return quantiteDisponible;
    }

    public void setQuantiteDisponible(int quantiteDisponible) {
        this.quantiteDisponible = quantiteDisponible;
    }

    /**
     * Vérifie si le stock est en-dessous du seuil minimal Retourne vrai si la
     * quantité disponible <= seuil minimal du produit
     */
    public boolean estLowStock() {
        Produit p = getProduit();
        if (p == null) {
            return false;
        }
        return quantiteDisponible <= produit.getSeuilMinimal();
    }

    /**
     * Récupère le produit associé au stock Charge le produit depuis la base de
     * données si nécessaire (lazy loading)
     */
    public Produit getProduit() {
        if (produit == null && produitId > 0) {
            ProduitDAO pDAO;
            try {
                pDAO = new ProduitDAO(DatabaseConnection.getConnection());
                produit = pDAO.findById(produitId);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return produit;
    }
}
