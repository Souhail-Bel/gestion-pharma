package application.services;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import application.dao.*;
import application.modeles.Client;
import application.modeles.CommandeFournisseur;
import application.modeles.Fournisseur;
import application.modeles.Stock;
import application.modeles.Vente;
import application.resources.DatabaseConnection;
import javafx.application.Platform;
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
        
        if (clients.isEmpty()) clients.add(new Client(0, "Anonyme", "", ""));


        StockDAO sDao = new StockDAO(conn);
        stockGlobal.setAll(sDao.getAllStocks());

        VenteDAO vDao = new VenteDAO(conn);
        historiqueVentes.setAll(vDao.getAllVentes());
        
        FournisseurDAO fDao = new FournisseurDAO(conn);
        fournisseurs.setAll(fDao.getAll());

        
        CommandeFournisseurDAO cfDao = new CommandeFournisseurDAO(conn);
        commandesFournisseur.setAll(cfDao.getAll());
    }

    public static ObservableList<Stock> getStockGlobal() { return stockGlobal; }
    public static ObservableList<Vente> getHistoriqueVentes() { return historiqueVentes; }
    public static ObservableList<Fournisseur> getFournisseurs() { return fournisseurs; }
    public static ObservableList<CommandeFournisseur> getCommandesFournisseur() { return commandesFournisseur; }
    public static ObservableList<Client> getClients() { return clients; }

    
    // refreshers
    // IMPORTANT à considerer pour les solutions multi-thread
    // ce project est petit
    // or, pour dans un entreprise, il faut utiliser Task
    // afin de séparer JavaFX (UI) thread et la connexion
    //
    // ou ajouter dans la file de UI threads avec runLater wrapper
    
    public static void refreshStocks() throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        StockDAO sDao = new StockDAO(conn);
        stockGlobal.setAll(sDao.getAllStocks());
        /*
        Platform.runLater(() -> {try {
			stockGlobal.setAll(sDao.getAllStocks());
		} catch (SQLException e) { e.printStackTrace(); }});
		*/
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

    public static void refreshCommandesFournisseur() throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        CommandeFournisseurDAO cfDao = new CommandeFournisseurDAO(conn);
        commandesFournisseur.setAll(cfDao.getAll());
    }
    

    // queries involving search
    
    public static List<Vente> searchVentes(LocalDate min, LocalDate max, String clientName) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            VenteDAO dao = new VenteDAO(conn);
            return dao.findByFilters(min, max, clientName);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    public static void loadVenteDetails(Vente v) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            new VenteDAO(conn).loadLignesForVente(v);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public static List<Fournisseur> searchFournisseurs(String keyword) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            FournisseurDAO dao = new FournisseurDAO(conn);
            return dao.findByFilter(keyword);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}