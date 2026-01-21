package application.modeles;

import application.exceptions.StockInsuffisantException;
import application.resources.DatabaseConnection;
import application.dao.produitDAO;

import java.sql.SQLException;

public class Produit {
	private int id;
	private String nom;
	private double prixVente;
	private int seuilMinimal;
	private Stock stock;

	public Produit(int id, String nom, double prixVente, int seuilMinimal) throws SQLException {
		this.id = id;
		this.nom = nom;
		this.prixVente = prixVente;
		this.seuilMinimal = seuilMinimal;
		stock=new Stock(this.id,0);
	}
	public static void addproduit(String nom,double prixVente,int seuilMinimal) throws SQLException {
		produitDAO pdao=new produitDAO(DatabaseConnection.getConnection());
		int id=pdao.taille()+1;
		pdao.Register(new Produit(id,nom,prixVente,seuilMinimal));
	}
	public void vendre(int quantite) throws StockInsuffisantException {
		stock.lowerStock(quantite);
	}

	public int getId() {return id;}
	public String getNom() {return nom;}
	public double getPrixVente() {return prixVente;}
	public int getSeuilMinimal() {return seuilMinimal;}

	@Override
	public String toString() {
		return nom;
	}
	public void setNom(String nom) { this.nom = nom; }
	public void setPrixVente(double prixVente) { this.prixVente = prixVente; }
	public void setSeuilMinimal(int seuilMinimal) { this.seuilMinimal = seuilMinimal; }
}
