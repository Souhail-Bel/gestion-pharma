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
	public static void addstock(int produitID,int quantiteDisponible) throws SQLException {
		stockDAO sdao=new stockDAO(DatabaseConnection.getConnection());;
		sdao.Register(new Stock(produitID,quantiteDisponible));
	}
/*	public void lowerStock(int quantite)throws StockInsuffisantException
	{
		if(quantiteDisponible<quantite){
			throw new StockInsuffisantException("Stock insuffisant");
		}else{
			quantiteDisponible-=quantite;
		}
	}*/
	public Produit getProduit() {return produit;}

	public int getQuantiteDisponible() {return quantiteDisponible;}
	public void setQuantiteDisponible(int quantiteDisponible) {this.quantiteDisponible = quantiteDisponible;}


	public int difference() {
		return quantiteDisponible-produit.getSeuilMinimal();
	}
}
