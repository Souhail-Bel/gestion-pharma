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
	public static void addproduit(String nom,double prixVente,int seuilMinimal)  {
        produitDAO pdao= null;
        try {
            pdao = new produitDAO(DatabaseConnection.getConnection());
			int id=pdao.taille()+1;
			pdao.Register(new Produit(id,nom,prixVente,seuilMinimal));
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
	}
	//a method called by the employee to odify a product (if one of the variables is unchanged just call p.modifyproduit(p.getPrixVente,new_seuil);)
	public void modifyproduit(double prixVente,int seuilMinimal){
		this.prixVente=prixVente;
		this.seuilMinimal=seuilMinimal;
        try {
            produitDAO pdao=new produitDAO(DatabaseConnection.getConnection());
			pdao.updateDB(this);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
	//a method called by the employee after selecting a product or more (in that case iterate a list)
	public void deleteproduit(){
		try{
			produitDAO pdao=new produitDAO(DatabaseConnection.getConnection());
			pdao.delete(id);
			stock.deletestock();
		} catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
	//a method called by commandFournisseur for every LigneCommandeFournisseur
	public void recieve(int quantite) throws StockInsuffisantException, SQLException {
		stock.importstock(quantite);
	}
	//a method called by Vente for every LigneVente
	public void export(int quantite){
        try {
            stock.exportstock(quantite);
        } catch (StockInsuffisantException | SQLException e) {
            System.out.println(e.getMessage());
        }
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
