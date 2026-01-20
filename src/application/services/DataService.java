package application.services;

import application.modeles.Produit;
import application.modeles.Stock;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DataService {
	private static final ObservableList<Stock> stockGlobal = FXCollections.observableArrayList();
	
	static {
		initData();
	}
	
	public static ObservableList<Stock> getStockGlobal() {
		return stockGlobal;
	}
	
	// (int id, String nom, double prixVente, int seuilMinimal)
	private static void initData() {
		for(int i=1; i < 50; i++) {
			Produit p = new Produit(i, "Produit N°"+i, (int) (Math.random() * 50), (int) (Math.random() * 20));
			stockGlobal.add(new Stock(p, (int) (Math.random() * 100)));
		}
	}
}
