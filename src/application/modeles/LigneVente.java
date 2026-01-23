package application.modeles;

public class LigneVente {

    private int id;
    private Vente vente; // parent
    private Produit produit;
    private int quantite;
    private double prixUnitaire;

    public LigneVente(int id, Produit produit, int quantite) {
        this.id = id;
        this.vente = null;
        this.produit = produit;
        this.quantite = quantite;
        this.prixUnitaire = produit.getPrixVente();
    }

    public LigneVente(int id, Vente vente, Produit produit, int quantite) {
        this.id = id;
        this.vente = vente;
        this.produit = produit;
        this.quantite = quantite;
        this.prixUnitaire = produit.getPrixVente();
    }

    /**
     * Calcule le sous-total de cette ligne (prix unitaire * quantité)
     */
    public double getSousTotal() {
        return prixUnitaire * quantite;
    }

    public int getProduitId() {
        return produit.getId();
    }

    public String getNomProduit() {
        return produit.getNom();
    }

    public double getPrixUnitaire() {
        return prixUnitaire;
    }

    public int getQuantite() {
        return quantite;
    }

    public Produit getProduit() {
        return produit;
    }

    public void setVente(Vente v) {
        this.vente = v;
    }

    public void setQuantite(int q) {
        this.quantite = q;
    }
}
