package application.views;

import application.modeles.*;
import application.services.DataService;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import java.time.format.DateTimeFormatter;

public class FournisseursController {

    @FXML private TableView<CommandeFournisseur> tableCommandes;
    @FXML private TableColumn<CommandeFournisseur, Integer> colCmdId;
    @FXML private TableColumn<CommandeFournisseur, String> colCmdDate;
    @FXML private TableColumn<CommandeFournisseur, String> colCmdFournisseur;
    @FXML private TableColumn<CommandeFournisseur, String> colCmdTotal;
    @FXML private TableColumn<CommandeFournisseur, String> colCmdStatut;
    @FXML private TableColumn<CommandeFournisseur, Void> colCmdAction;

    @FXML private TableView<Fournisseur> tableFournisseurs;
    @FXML private TableColumn<Fournisseur, String> colFourNom, colFourTel, colFourEmail, colFourAdress;
    @FXML private TextField txtSearchFournisseur;

    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initialize() {
        setupCommandeTable();
        setupFournisseurTable();
    }

    private void setupCommandeTable() {
        tableCommandes.setItems(DataService.getCommandesFournisseur());

        colCmdId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCmdDate.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDate().format(fmt)));
        colCmdFournisseur.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getFournisseur().getNom()));
        colCmdTotal.setCellValueFactory(cell -> new SimpleStringProperty(String.format("%.3f TND", cell.getValue().getTotal())));
        colCmdStatut.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getStatut().toString()));

        // THE 3-BUTTON ACTION COLUMN
        colCmdAction.setCellFactory(param -> new TableCell<>() {
            private final Button btnEdit = new Button("✎");
            private final Button btnReceive = new Button("✔");
            private final Button btnCancel = new Button("✖");
            private final HBox pane = new HBox(8, btnCancel, btnEdit, btnReceive);

            {
                btnCancel.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-cursor: hand;");
                btnEdit.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-cursor: hand;");
                btnReceive.setStyle("-fx-background-color: #22c55e; -fx-text-fill: white; -fx-cursor: hand;");
                
                btnCancel.setOnAction(e -> handleAnnuler(getTableView().getItems().get(getIndex())));
                btnEdit.setOnAction(e -> ouvrirCommandeForm(getTableView().getItems().get(getIndex())));
                btnReceive.setOnAction(e -> handleReception(getTableView().getItems().get(getIndex())));
                pane.setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) { setGraphic(null); } 
                else {
                    CommandeFournisseur cmd = getTableView().getItems().get(getIndex());
                    boolean active = (cmd.getStatut() == StatutCommande.CREATED || cmd.getStatut() == StatutCommande.MODIFIED);
                    setGraphic(active ? pane : null);
                }
            }
        });
    }

    private void handleAnnuler(CommandeFournisseur c) {
        c.setStatut(StatutCommande.CANCELED);
        tableCommandes.refresh();
    }

    private void handleReception(CommandeFournisseur c) {
        // Logic to increase stock
        for(LigneCommandeFournisseur l : c.getLignes()) {
            for(Stock s : DataService.getStockGlobal()) {
                if(s.getProduit().getId() == l.getProduit().getId()) {
                    s.setQuantiteDisponible(s.getQuantiteDisponible() + l.getQuantite());
                }
            }
        }
        c.setStatut(StatutCommande.RECEIVED);
        tableCommandes.refresh();
    }

    @FXML void handleNouvelleCommande(ActionEvent e) { ouvrirCommandeForm(null); }
    
    void ouvrirCommandeForm(CommandeFournisseur c) {
        // Your logic to open the Dialog
        try {
             FXMLLoader loader = new FXMLLoader(getClass().getResource("NouvelleCommandeLayout.fxml"));
             Parent root = loader.load();
             // Pass 'c' to controller...
             Stage stage = new Stage();
             stage.setScene(new Scene(root));
             stage.showAndWait();
             tableCommandes.refresh();
        } catch(Exception e) { e.printStackTrace(); }
    }

    private void setupFournisseurTable() {
        tableFournisseurs.setItems(DataService.getFournisseurs());
        colFourNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colFourTel.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        colFourEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colFourAdress.setCellValueFactory(new PropertyValueFactory<>("adresse"));
    }
    
    @FXML void handleAjoutFournisseur(ActionEvent e) { /* Add logic */ }
}