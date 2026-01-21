package application.services;

import java.sql.Connection;
import java.sql.SQLException;
import java.text.DecimalFormat;

import application.dao.*;
import application.modeles.Client;
import application.modeles.CommandeFournisseur;
import application.modeles.Fournisseur;
import application.modeles.Produit;
import application.modeles.Stock;
import application.modeles.Vente;
import application.resources.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DataService {
	private static final ObservableList<Stock> stockGlobal = FXCollections.observableArrayList();
	private static final ObservableList<Vente> historiqueVentes = FXCollections.observableArrayList();
	
	private static final ObservableList<Fournisseur> fournisseurs = FXCollections.observableArrayList();
	private static final ObservableList<CommandeFournisseur> commandesFournisseur = FXCollections.observableArrayList();
	
	private static final ObservableList<Client> clients = FXCollections.observableArrayList();
	
	static {
        try {
            initData();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
	
	public static ObservableList<Client> getClients() {
		return clients;
	}
	
	// (int id, String nom, double prixVente, int seuilMinimal)
	private static void initData() throws SQLException {
		Connection conn = DatabaseConnection.getConnection();

        clientDAO cDao = new clientDAO(conn);
        clients.setAll(cDao.getAllClients()); 
        
        if (clients.isEmpty()) {
            clients.add(new Client(0, "Anonyme", "", ""));
        }


        stockDAO sDao = new stockDAO(conn);
        stockGlobal.setAll(sDao.getAllStocks());

        venteDAO vDao = new venteDAO(conn);
        historiqueVentes.setAll(vDao.getAllVentes());
        
        
	}
}
