package application.services;

import java.text.DecimalFormat;

import application.modeles.CommandeFournisseur;
import application.modeles.Fournisseur;
import application.modeles.Produit;
import application.modeles.Stock;
import application.modeles.Vente;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DataService {
	private static final ObservableList<Stock> stockGlobal = FXCollections.observableArrayList();
	private static ObservableList<Vente> historiqueVentes = FXCollections.observableArrayList();
	
	private static final ObservableList<Fournisseur> fournisseurs = FXCollections.observableArrayList();
	private static final ObservableList<CommandeFournisseur> commandesFournisseur = FXCollections.observableArrayList();
	
	static {
		initData();
	}
	
	public static ObservableList<Stock> getStockGlobal() {
		return stockGlobal;
	}
	
	public static ObservableList<Vente> getHistoriqueVentes() {
		return historiqueVentes;
	}
	
	public static ObservableList<Fournisseur> getFournisseurs() {
		return fournisseurs;
	}
	
	public static ObservableList<CommandeFournisseur> getCommandesFournisseur() {
		return commandesFournisseur;
	}
	
	// (int id, String nom, double prixVente, int seuilMinimal)
	private static void initData() {
		for(int i=1; i < 50; i++) {
			Produit p = new Produit(i, "Produit N°"+i, (int) (Math.random() * 50), (int) (Math.random() * 20));
			stockGlobal.add(new Stock(p, (int) (Math.random() * 100)));
		}
		
		for(int i=1; i < 20; i++) {
			Fournisseur f = new Fournisseur(
					i, "Fournisseur N°" + i,
					(new DecimalFormat("00000000")).format((int)(Math.random()*100000000)),
					"fournisseur."+i+"@email.com",
					"Avenue N°"+i);
			fournisseurs.add(f);
		}
	}
}
