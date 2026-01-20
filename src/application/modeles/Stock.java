package application.modeles;

public class Stock {
	private Produit produit;
	private int quantiteDisponible;
	
	public Stock(Produit produit, int quantiteDisponible) {
		this.produit = produit;
		this.quantiteDisponible = quantiteDisponible;
	}
	
	
	public Produit getProduit() {return produit;}
	
	public int getQuantiteDisponible() {return quantiteDisponible;}
	public void setQuantiteDisponible(int quantiteDisponible) {this.quantiteDisponible = quantiteDisponible;}
	

	public boolean estLowStock() {
		return quantiteDisponible <= produit.getSeuilMinimal();
	}
}
