package application.views;

import application.modeles.Client;
import application.modeles.LigneVente;
import application.modeles.Stock;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class VenteController {

    @FXML private Button btnAjouter;
    @FXML private Button btnValider;

    @FXML private TableColumn<Stock, String> colCatNom;
    @FXML private TableColumn<Stock, Double> colCatPrix;
    @FXML private TableColumn<Stock, Integer> colCatStock;
    
    @FXML private TableColumn<LigneVente, String> colPanierNom;
    @FXML private TableColumn<LigneVente, Integer> colPanierQte;
    @FXML private TableColumn<LigneVente, Double> colPanierTotal;

    @FXML private ComboBox<Client> comboClient;

    @FXML private Label lblError;
    @FXML private Label lblTotal;

    @FXML private TableView<Stock> tableCatalogue;
    @FXML private TableView<LigneVente> tablePanier;
    
    private ObservableList<Stock>
 catalogueList = FXCollections.observableArrayList();
    private ObservableList<LigneVente> panierList = FXCollections.observableArrayList();
    private ObservableList<Client> clientList = FXCollections.observableArrayList();
    
    @FXML private TextField txtSearch;

    
    @FXML
    public void initialize() {
    	remplirTable();
    	loadData();
    	
    	tablePanier.setItems(panierList);
    	comboClient.setItems(clientList);
    }
    
    
    @FXML
    void handleAjouter(ActionEvent event) {

    }

    @FXML
    void handleValiderVente(ActionEvent event) {

    }

    
    private void remplirTable() {
    	
    }
    
    private void loadData() {
    	
    }
}
