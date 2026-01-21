package application.modeles;

import application.dao.produitDAO;
import application.resources.DatabaseConnection;

import java.sql.SQLException;

public class Stock {
	private Produit produit;
	private int quantiteDisponible;
	produitDAO pdao = new produitDAO(DatabaseConnection.getConnection());

	public Stock(int ProduitID, int quantiteDisponible) throws SQLException {
		this.produit = pdao.FindByID(ProduitID);
		this.quantiteDisponible = quantiteDisponible;
	}
	
	
	public Produit getProduit() {return produit;}
	
	public int getQuantiteDisponible() {return quantiteDisponible;}
	public void setQuantiteDisponible(int quantiteDisponible) {this.quantiteDisponible = quantiteDisponible;}
	

	public boolean estLowStock() {
		return quantiteDisponible <= produit.getSeuilMinimal();
	}
}
