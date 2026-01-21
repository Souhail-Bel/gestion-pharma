package application.modeles;

public class LigneCommandeFournisseur {
	private int id;
	private CommandeFournisseur commande; // parent
	private Produit produit;
	private int quantite;
	private double prixAchat;
	
	public LigneCommandeFournisseur(int id, CommandeFournisseur commande, Produit produit, int quantite, double prixAchat) {
		this.id = id;
		this.commande = commande;
        this.produit = produit;
        this.quantite = quantite;
        this.prixAchat = prixAchat;
    }
	
	public Produit getProduit() {return produit;}
	public double getTotal() {return quantite * prixAchat;}
	public int getQuantite() {return quantite; }


	public void setCommande(CommandeFournisseur cmd) {
		this.commande = cmd;
	}
}
