package application.modeles;

public class LigneCommandeFournisseur {

    private CommandeFournisseur commande;
    private Produit produit;
    private int quantite;
    private double prixAchat;

    /**
     * Constructeur - Initialise une ligne de commande fournisseur
     */
    public LigneCommandeFournisseur(CommandeFournisseur commande, Produit produit, int quantite, double prixAchat) {
        this.commande = commande;
        this.produit = produit;
        this.quantite = quantite;
        this.prixAchat = prixAchat;
    }

    public Produit getProduit() {
        return produit;
    }

    public String getProduitNom() {
        if (produit == null) {
            return "";
        }
        return produit.getNom();
    }

    public double getPrixAchat() {
        return prixAchat;
    }

    public int getQuantite() {
        return quantite;
    }

    /**
     * Calcule le sous-total de cette ligne (quantité * prix d'achat)
     */
    public double getSousTotal() {
        return quantite * prixAchat;
    }

    public void setCommande(CommandeFournisseur cmd) {
        this.commande = cmd;
    }
}
