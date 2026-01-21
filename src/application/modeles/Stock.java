package application.modeles;
import application.dao.produitDAO;
import application.dao.stockDAO;
import application.exceptions.StockInsuffisantException;
import application.resources.DatabaseConnection;

import java.sql.SQLException;
import java.time.LocalDate;
public class Stock {
	private Produit produit;
	private int quantiteDisponible;

	public Stock(int produitID, int quantiteDisponible) throws SQLException {
		produitDAO pdao = new produitDAO(DatabaseConnection.getConnection());
		this.produit = pdao.FindByID(produitID);
		this.quantiteDisponible = quantiteDisponible;
	}
	public static void newstock(int produitID,int quantiteDisponible) throws SQLException {
		stockDAO sdao=new stockDAO(DatabaseConnection.getConnection());
		sdao.Register(new Stock(produitID,quantiteDisponible));
	}
	public void addstock(int quantite) throws StockInsuffisantException, SQLException {
		if(quantiteDisponible<quantite)
			throw new StockInsuffisantException("Stock Insuffisant");
		else{
			quantiteDisponible-=quantite;
			stockDAO sdao=new stockDAO(DatabaseConnection.getConnection());
			sdao.updateStock(produit.getId(),quantiteDisponible);
		}
	}
	public Produit getProduit() {return produit;}

	public int getQuantiteDisponible() {return quantiteDisponible;}
	public void setQuantiteDisponible(int quantiteDisponible) {this.quantiteDisponible = quantiteDisponible;}


	public int difference() {
		return quantiteDisponible-produit.getSeuilMinimal();
	}
}
