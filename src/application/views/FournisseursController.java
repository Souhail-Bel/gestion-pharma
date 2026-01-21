package application.views;

import java.time.format.DateTimeFormatter;
import application.modeles.CommandeFournisseur;
import application.modeles.Fournisseur;
import application.modeles.LigneCommandeFournisseur;
import application.modeles.StatutCommande;
import application.modeles.Stock;
import application.services.DataService;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class FournisseursController {

    // commandes
    @FXML private TableView<CommandeFournisseur> tableCommandes;
    @FXML private TableColumn<CommandeFournisseur, Integer> colCmdId;
    @FXML private TableColumn<CommandeFournisseur, String> colCmdDate;
    @FXML private TableColumn<CommandeFournisseur, String> colCmdFournisseur;
    @FXML private TableColumn<CommandeFournisseur, String> colCmdStatut;
    @FXML private TableColumn<CommandeFournisseur, Void> colCmdAction;
    
    @FXML private TableColumn<CommandeFournisseur, String> colCmdTotal; 

    // fournisseurs
    @FXML private TextField txtSearchFournisseur;
    @FXML private TableView<Fournisseur> tableFournisseurs;
    @FXML private TableColumn<Fournisseur, String> colFourNom;
    @FXML private TableColumn<Fournisseur, String> colFourTel;
    @FXML private TableColumn<Fournisseur, String> colFourEmail;
    @FXML private TableColumn<Fournisseur, String> colFourAdress;

    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

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
        

        if(colCmdTotal != null) {
            colCmdTotal.setCellValueFactory(cell -> new SimpleStringProperty(String.format("%.3f TND", cell.getValue().getTotal())));
        }
        
        // coleurs
        colCmdStatut.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getStatut().toString()));
        colCmdStatut.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(item);
                if (item == null || empty) {
                    setStyle("");
                    setTextFill(Color.BLACK);
                } else if ("RECEIVED".equals(item)) {
                    setTextFill(Color.GREEN);
                    setStyle("-fx-font-weight: bold;");
                } else if ("CANCELED".equals(item)) {
                    setTextFill(Color.RED);
                    setStyle("-fx-font-weight: normal;");
                } else {
                    setTextFill(Color.ORANGE); // CREATED / MODIFIED
                    setStyle("-fx-font-weight: bold;");
                }
            }
        });

        // ajouter 3 buttons
        colCmdAction.setCellFactory(param -> new TableCell<>() {
            private final Button btnEdit = new Button("✎");
            private final Button btnReceive = new Button("✔");
            private final Button btnCancel = new Button("✖");
            private final HBox pane = new HBox(8, btnCancel, btnEdit, btnReceive);

            {
                // annuler
                btnCancel.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-cursor: hand; -fx-background-radius:4;");
                btnCancel.setTooltip(new Tooltip("Annuler"));
                btnCancel.setOnAction(e -> handleAnnulation(getTableView().getItems().get(getIndex())));

                // modifer
                btnEdit.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-cursor: hand; -fx-background-radius:4;");
                btnEdit.setTooltip(new Tooltip("Modifier"));
                btnEdit.setOnAction(e -> ouvrirCommandeForm(getTableView().getItems().get(getIndex())));

                // recevoir
                btnReceive.setStyle("-fx-background-color: #22c55e; -fx-text-fill: white; -fx-cursor: hand; -fx-background-radius:4;");
                btnReceive.setTooltip(new Tooltip("Réceptionner"));
                btnReceive.setOnAction(e -> handleReception(getTableView().getItems().get(getIndex())));

                pane.setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    CommandeFournisseur cmd = getTableView().getItems().get(getIndex());

                    boolean isActive = (cmd.getStatut() == StatutCommande.CREATED || cmd.getStatut() == StatutCommande.MODIFIED);
                    setGraphic(isActive ? pane : null);
                }
            }
        });
    }
    
    private void handleAnnulation(CommandeFournisseur commande) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, 
                "Voulez-vous vraiment annuler la commande #" + commande.getId() + " ?", 
                ButtonType.YES, ButtonType.NO);
                
        if (confirm.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
            commande.setStatut(StatutCommande.CANCELED);
            tableCommandes.refresh();
        }
    }
    
    @FXML
    void handleNouvelleCommande(ActionEvent event) {
        ouvrirCommandeForm(null);
    }

    void ouvrirCommandeForm(CommandeFournisseur commande) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("NouvelleCommandeLayout.fxml"));
            Parent root = loader.load();
            
            NouvelleCommandeController ctrl = loader.getController();
            if (commande != null) {
                ctrl.setCurrCommande(commande);
            }
            
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle(commande == null ? "Nouvelle Commande" : "Modifier Commande");
            stage.showAndWait();
            
            tableCommandes.refresh(); 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleAjoutFournisseur(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nouveau Fournisseur");
        dialog.setHeaderText("Ajouter un fournisseur...");
        dialog.setContentText("Nom de l'entreprise :");
        
        dialog.showAndWait().ifPresent(nom -> {
            if (!nom.isEmpty()) {
                int id = DataService.getFournisseurs().size() + 1;
                DataService.getFournisseurs().add(new Fournisseur(id, nom, "", "", ""));
            }
        });
    }

    private void handleReception(CommandeFournisseur commande) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Confirmer la réception (Stock sera mis à jour) ?", ButtonType.YES, ButtonType.NO);
        if (confirm.showAndWait().orElse(ButtonType.NO) != ButtonType.YES) return;

        for (LigneCommandeFournisseur ligne : commande.getLignes()) {
            for (Stock s : DataService.getStockGlobal()) {
                if (s.getProduit().getId() == ligne.getProduit().getId()) {
                    s.setQuantiteDisponible(s.getQuantiteDisponible() + ligne.getQuantite());
                    break;
                }
            }
        }

        commande.setStatut(StatutCommande.RECEIVED);
        tableCommandes.refresh();
    }
    
    private void setupFournisseurTable() {
        tableFournisseurs.setItems(DataService.getFournisseurs());
        colFourNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colFourTel.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        colFourEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colFourAdress.setCellValueFactory(new PropertyValueFactory<>("adresse"));
    }
}