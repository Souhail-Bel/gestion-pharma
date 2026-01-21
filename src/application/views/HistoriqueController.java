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
    @FXML private TableColumn<Vente, Double> colTotal;

    // table details
    @FXML private Label lblDetailInfo;
    @FXML private TableView<LigneVente> tableDetails;
    @FXML private TableColumn<LigneVente, String> colDetProduit;
    @FXML private TableColumn<LigneVente, Integer> colDetQte;
    @FXML private TableColumn<LigneVente, Double> colDetTotal;
    
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
        //tableVentes.setItems(DataService.getHistoriqueVentes());
        filteredData = new FilteredList<>(DataService.getHistoriqueVentes(), p -> true);
        
        txtSearchClient.textProperty().addListener((obs, oldVal, newVal) -> miseAJourFiltre());
        datePickerStart.valueProperty().addListener((obs, oldVal, newVal) -> miseAJourFiltre());
        datePickerEnd.valueProperty().addListener((obs, oldVal, newVal) -> miseAJourFiltre());
        
        SortedList<Vente> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableVentes.comparatorProperty());
        
        tableVentes.setItems(sortedData);

        // afficher details selon selection
        tableVentes.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> showDetails(newVal)
        );
    }

    private void setupProduitsTable() {
    	
        colId.setCellValueFactory(cell ->
        new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getId()));
        colDate.setCellValueFactory(cell -> 
            new SimpleStringProperty(cell.getValue().getDate().format(fmt)));
        colClient.setCellValueFactory(cell -> 
            new SimpleStringProperty(cell.getValue().getClient().getNom()));
        colTotal.setCellValueFactory(cell ->
        new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getTotal()));
    }

    private void setupDetailsTable() {
        colDetProduit.setCellValueFactory(new PropertyValueFactory<>("nomProduit"));
        colDetQte.setCellValueFactory(new PropertyValueFactory<>("quantite"));
        colDetTotal.setCellValueFactory(new PropertyValueFactory<>("sousTotal"));
    }

    private void showDetails(Vente vente) {
        if (vente != null) {
            lblDetailInfo.setText("Client : " + vente.getClient().getNom() + 
                                  "\nVendu par : " + vente.getEmploye().getNom());
            tableDetails.getItems().setAll(vente.getLignes());
        } else {
            lblDetailInfo.setText("");
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
    			if(!vente.getClient().getNom().toLowerCase().contains(query))
    				return false;
    		}
    		
    		// par periode
    		LocalDate dateVente = vente.getDate().toLocalDate();
    		
    		if(datePickerStart.getValue() != null && dateVente.isBefore(datePickerStart.getValue()))
    			return false;
    		
    		if(datePickerEnd.getValue() != null && dateVente.isAfter(datePickerEnd.getValue()))
    			return false;
    		
    		return true;
    	});
    }
}