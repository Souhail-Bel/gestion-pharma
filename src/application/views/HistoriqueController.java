package application.views;

import application.modeles.LigneVente;
import application.modeles.Vente;
import application.services.DataService;
import javafx.beans.property.SimpleStringProperty;
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

public class HistoriqueController {

    // table ventes
    @FXML private TableView<Vente> tableVentes;
    @FXML private TableColumn<Vente, Integer> colId;
    @FXML private TableColumn<Vente, String> colDate;
    @FXML private TableColumn<Vente, String> colClient;
    @FXML private TableColumn<Vente, String> colTotal; // Changed to String for currency formatting

    // table details
    @FXML private Label lblDetailInfo;
    @FXML private TableView<LigneVente> tableDetails;
    @FXML private TableColumn<LigneVente, String> colDetProduit;
    @FXML private TableColumn<LigneVente, Integer> colDetQte;
    @FXML private TableColumn<LigneVente, String> colDetTotal; // Changed to String for currency formatting
    
    @FXML private DatePicker datePickerStart;
    @FXML private DatePicker datePickerEnd;
    @FXML private TextField txtSearchClient;
    
    private FilteredList<Vente> filteredData;
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {
        setupProduitsTable();
        setupDetailsTable();

        // charger histroique
        filteredData = new FilteredList<>(DataService.getHistoriqueVentes(), p -> true);
        
        // Listeners for filters
        txtSearchClient.textProperty().addListener((obs, oldVal, newVal) -> miseAJourFiltre());
        datePickerStart.valueProperty().addListener((obs, oldVal, newVal) -> miseAJourFiltre());
        datePickerEnd.valueProperty().addListener((obs, oldVal, newVal) -> miseAJourFiltre());
        
        // Wrap filtered list in sorted list
        SortedList<Vente> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableVentes.comparatorProperty());
        
        tableVentes.setItems(sortedData);

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
            new SimpleStringProperty(cell.getValue().getClient() != null ? cell.getValue().getClient().getNom() : "Anonyme"));
            
        colTotal.setCellValueFactory(cell ->
            new SimpleStringProperty(String.format("%.3f TND", cell.getValue().getTotal())));
    }

    private void setupDetailsTable() {
        // Fixed: Use Lambda to access nested Product object safely
        colDetProduit.setCellValueFactory(cell -> 
            new SimpleStringProperty(cell.getValue().getProduit().getNom()));
            
        colDetQte.setCellValueFactory(new PropertyValueFactory<>("quantite"));
        
        colDetTotal.setCellValueFactory(cell -> 
            new SimpleStringProperty(String.format("%.3f TND", cell.getValue().getPrixUnitaire())));
    }

    private void showDetails(Vente vente) {
        if (vente != null) {
            String employeName = (vente.getEmploye() != null) ? vente.getEmploye().getUsername() : "Inconnu";
            String clientName = (vente.getClient() != null) ? vente.getClient().getNom() : "Anonyme";
            
            lblDetailInfo.setText("Client : " + clientName + "\nVendu par : " + employeName);
            tableDetails.getItems().setAll(vente.getLignes());
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
    
    private void miseAJourFiltre() {
        filteredData.setPredicate(vente -> {
            // par client
            String query = txtSearchClient.getText();
            if(query != null && !query.isEmpty()) {
                String clientName = vente.getClient() != null ? vente.getClient().getNom().toLowerCase() : "";
                if(!clientName.contains(query.toLowerCase()))
                    return false;
            }
            
            // par periode
            if (vente.getDate() == null) return false;
            LocalDate dateVente = vente.getDate().toLocalDate();
            
            if(datePickerStart.getValue() != null && dateVente.isBefore(datePickerStart.getValue()))
                return false;
            
            if(datePickerEnd.getValue() != null && dateVente.isAfter(datePickerEnd.getValue()))
                return false;
            
            return true;
        });
    }
}