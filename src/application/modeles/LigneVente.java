package application.modeles;

public class LigneVente {
    private int id;
    private Vente vente; // parent
    private Produit produit;
    private int quantite;
    private double prixUnitaire;

    public LigneVente(Produit produit, int quantite) {
        this.produit = produit;
        this.quantite = quantite;
        this.prixUnitaire = produit.getPrixVente();
    }

    public double getSousTotal() {
        return prixUnitaire * quantite;
    }

    public String getNomProduit() { return produit.getNom(); }
    public double getPrixUnitaire() { return prixUnitaire; }
    public int getQuantite() { return quantite; }
    public void setQuantite(int q) { this.quantite = q; }
    public Produit getProduit() { return produit; }
}