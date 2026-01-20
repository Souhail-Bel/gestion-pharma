package application.views;

import application.modeles.*;
import application.services.DataService;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;

public class FournisseursController {

    // commandes
    @FXML private TableView<CommandeFournisseur> tableCommandes;
    @FXML private TableColumn<CommandeFournisseur, Integer> colCmdId;
    @FXML private TableColumn<CommandeFournisseur, String> colCmdDate;
    @FXML private TableColumn<CommandeFournisseur, String> colCmdFournisseur;
    @FXML private TableColumn<CommandeFournisseur, String> colCmdStatut;
    @FXML private TableColumn<CommandeFournisseur, Void> colCmdAction;

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
        
        
        // modifier seulement received/cancelled
        tableCommandes.setRowFactory(tv -> {
            TableRow<CommandeFournisseur> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    CommandeFournisseur rowData = row.getItem();
                    if (rowData.getStatut() == StatutCommande.CREATED || rowData.getStatut() == StatutCommande.MODIFIED) {
                    	ouvrirCommandeForm(rowData);
                    } else {
                        new Alert(Alert.AlertType.INFORMATION, "Impossible de modifier une commande déjà traitée.").show();
                    }
                }
            });
            return row;
        });
    }

    private void setupCommandeTable() {
        tableCommandes.setItems(DataService.getCommandesFournisseur());

        colCmdId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCmdDate.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDate().format(fmt)));
        colCmdFournisseur.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getFournisseur().getNom()));
        
        colCmdStatut.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getStatut().toString()));
        colCmdStatut.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(item);
                if (item == null || empty) {
                    setStyle("");
                } else if ("RECEIVED".equals(item)) {
                    setTextFill(javafx.scene.paint.Color.GREEN);
                    setStyle("-fx-font-weight: bold;");
                } else if ("CANCELED".equals(item)) {
                    setTextFill(javafx.scene.paint.Color.RED);
                } else {
                    setTextFill(javafx.scene.paint.Color.ORANGE); // CREATED / MODIFIED
                }
            }
        });


        colCmdAction.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("📥 Réceptionner");

            {
                btn.setStyle("-fx-background-color: #2E7D32; -fx-text-fill: white; -fx-font-size: 10px;");
                btn.setOnAction(event -> {
                    CommandeFournisseur cmd = getTableView().getItems().get(getIndex());
                    handleReception(cmd);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    // afficher button if not received/canceled
                    CommandeFournisseur cmd = getTableView().getItems().get(getIndex());
                    boolean editable = (cmd.getStatut() == StatutCommande.CREATED || cmd.getStatut() == StatutCommande.MODIFIED);
                    setGraphic(editable ? btn : null);
                }
            }
        });
    }

    private void setupFournisseurTable() {
        tableFournisseurs.setItems(DataService.getFournisseurs());
        colFourNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colFourTel.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        colFourEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colFourAdress.setCellValueFactory(new PropertyValueFactory<>("adresse"));
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
            
            tableCommandes.refresh(); // Refresh list after close
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleAjoutFournisseur(ActionEvent event) {
        // Simple Quick Add (Like we did for Client)
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
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Confirmer la réception de la commande #" + commande.getId(), ButtonType.YES, ButtonType.NO);
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
}