package application.services;

import java.sql.Connection;
import java.sql.SQLException;

import application.dao.*;
import application.modeles.Client;
import application.modeles.CommandeFournisseur;
import application.modeles.Fournisseur;
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

    public static void initData() throws SQLException {
        Connection conn = DatabaseConnection.getConnection();

        ClientDAO cDao = new ClientDAO(conn);
        clients.setAll(cDao.getAllClients());
        
        if (clients.isEmpty()) {
            clients.add(new Client(0, "Anonyme", "", ""));
        }

        StockDAO sDao = new StockDAO(conn);
        stockGlobal.setAll(sDao.getAllStocks());

        VenteDAO vDao = new VenteDAO(conn);
        historiqueVentes.setAll(vDao.getAllVentes());
        
        FournisseurDAO fDao = new FournisseurDAO(conn);
        fournisseurs.setAll(fDao.getAll());

        
        //CommandeFournisseurDAO cfDao = new CommandeFournisseurDAO(conn);
        //commandesFournisseur.setAll(cfDao.getAll());
    }

    public static ObservableList<Stock> getStockGlobal() { return stockGlobal; }
    public static ObservableList<Vente> getHistoriqueVentes() { return historiqueVentes; }
    public static ObservableList<Fournisseur> getFournisseurs() { return fournisseurs; }
    public static ObservableList<CommandeFournisseur> getCommandesFournisseur() { return commandesFournisseur; }
    public static ObservableList<Client> getClients() { return clients; }

    public static void refreshStocks() throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        StockDAO sDao = new StockDAO(conn);
        stockGlobal.setAll(sDao.getAllStocks());
    }

    public static void refreshVentes() throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        VenteDAO vDao = new VenteDAO(conn);
        historiqueVentes.setAll(vDao.getAllVentes());
    }

    public static void refreshFournisseurs() throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        FournisseurDAO fDao = new FournisseurDAO(conn);
        fournisseurs.setAll(fDao.getAll());
    }

    public static void refreshClients() throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        ClientDAO cDao = new ClientDAO(conn);
        clients.setAll(cDao.getAllClients());
    }

    // refreshCommandesFournisseur()
    public static void refreshCommandesFournisseur() throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        CommandeFournisseurDAO cfDao = new CommandeFournisseurDAO(conn);
        commandesFournisseur.setAll(cfDao.getAll());
    }
}