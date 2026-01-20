package application.views;

import application.modeles.LigneVente;
import application.modeles.Vente;
import application.services.DataService;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

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

    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    public void initialize() {
        setupProduitsTable();
        setupDetailsTable();

        // charger histroique
        tableVentes.setItems(DataService.getHistoriqueVentes());

        // afficher details selon selection
        tableVentes.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> showDetails(newVal)
        );
    }

    private void setupProduitsTable() {
    	//colId.setCellValueFactory(cellData -> 
        //new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getProduit().getId()));
    	
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
}