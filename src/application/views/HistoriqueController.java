package application.views;

import application.modeles.LigneVente;
import application.modeles.Vente;
import application.services.DataService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HistoriqueController {

    // table ventes
    @FXML private TableView<Vente> tableVentes;
    @FXML private TableColumn<Vente, Integer> colId;
    @FXML private TableColumn<Vente, String> colDate;
    @FXML private TableColumn<Vente, String> colClient;
    @FXML private TableColumn<Vente, String> colTotal;
    
    // table details
    @FXML private Label lblDetailInfo;
    @FXML private TableView<LigneVente> tableDetails;
    @FXML private TableColumn<LigneVente, String> colDetProduit;
    @FXML private TableColumn<LigneVente, Integer> colDetQte;
    @FXML private TableColumn<LigneVente, String> colDetTotal;

    
    @FXML private DatePicker datePickerStart;
    @FXML private DatePicker datePickerEnd;
    @FXML private TextField txtSearchClient;
    
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {
        setupProduitsTable();
        setupDetailsTable();

        
        chargerDonnees();
        
        // Listeners for filters
        txtSearchClient.textProperty().addListener((obs, oldVal, newVal) -> chargerDonnees());
        datePickerStart.valueProperty().addListener((obs, oldVal, newVal) -> chargerDonnees());
        datePickerEnd.valueProperty().addListener((obs, oldVal, newVal) -> chargerDonnees());

        // afficher details selon selection
        tableVentes.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> showDetails(newVal)
        );
    }

    private void setupProduitsTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        colDate.setCellValueFactory(cell -> 
            new SimpleStringProperty(cell.getValue().getDate().format(fmt)));
            
        colClient.setCellValueFactory(cell -> 
            new SimpleStringProperty(cell.getValue().getClient() != null ? cell.getValue().getClient().toString() : "Anonyme"));
            
        colTotal.setCellValueFactory(cell ->
            new SimpleStringProperty(String.format("%.3f TND", cell.getValue().getTotal())));
    }

    private void setupDetailsTable() {
        colDetProduit.setCellValueFactory(cell -> 
            new SimpleStringProperty(cell.getValue().getProduit().getNom()));
            
        colDetQte.setCellValueFactory(new PropertyValueFactory<>("quantite"));
        
        colDetTotal.setCellValueFactory(cell -> 
            new SimpleStringProperty(String.format("%.3f TND", cell.getValue().getPrixUnitaire())));
    }

    private void showDetails(Vente vente) {
        if (vente != null) {
        	
        	if (vente.getLignes().isEmpty()) {
                DataService.loadVenteDetails(vente);
            }
        	
            String employeName = (vente.getEmploye() != null) ? vente.getEmploye().getUsername() : "Inconnu";
            String clientName = (vente.getClient() != null) ? vente.getClient().toString() : "Anonyme";
            
            lblDetailInfo.setText("Client : " + clientName + "\nVendu par : " + employeName);
            tableDetails.setItems(FXCollections.observableArrayList(vente.getLignes()));
        } else {
            lblDetailInfo.setText("Sélectionnez une vente...");
            tableDetails.getItems().clear();
        }
    }
    
    @FXML
    private void handleClearFilters() {
        txtSearchClient.clear();
        datePickerStart.setValue(null);
        datePickerEnd.setValue(null);
    }
    
    
    private void chargerDonnees() {
    	LocalDate dateMin = datePickerStart.getValue();
    	LocalDate dateMax = datePickerEnd.getValue();
    	String clientNom = txtSearchClient.getText();
    	
    	List<Vente> res = DataService.searchVentes(dateMin, dateMax, clientNom);
    	
    	tableVentes.setItems(FXCollections.observableArrayList(res));
    }
}