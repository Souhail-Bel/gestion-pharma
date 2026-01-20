package application.modeles;

public class Produit {
	private int id;
	private String nom;
	private double prixVente;
	private int seuilMinimal;
	
	public Produit(int id, String nom, double prixVente, int quantiteDisponible, int seuilMinimal) {
		this.id = id;
		this.nom = nom;
		this.prixVente = prixVente;
		this.seuilMinimal = seuilMinimal;
	}
	

	public int getId() {return id;}
	public String getNom() {return nom;}
	public double getPrixVente() {return prixVente;}
	public int getSeuilMinimal() {return seuilMinimal;}
	
	@Override
	public String toString() {
		return nom;
	}
}
