package application.views;

import application.dao.CommandeFournisseurDAO;
import application.dao.StockDAO;
import application.modeles.*;
import application.services.DataService;
import application.services.PDFService;
import application.resources.DatabaseConnection;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
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

import java.sql.Connection;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FournisseursController {

    @FXML private TableView<CommandeFournisseur> tableCommandes;
    @FXML private TableColumn<CommandeFournisseur, Integer> colCmdId;
    @FXML private TableColumn<CommandeFournisseur, String> colCmdDate;
    @FXML private TableColumn<CommandeFournisseur, String> colCmdFournisseur;
    @FXML private TableColumn<CommandeFournisseur, String> colCmdTotal;
    @FXML private TableColumn<CommandeFournisseur, String> colCmdStatut;
    @FXML private TableColumn<CommandeFournisseur, CommandeFournisseur> colCmdAction;

    @FXML private Button btnCmdPrev;
    @FXML private Button btnCmdNext;
    @FXML private Label lblCmdPageInfo;
    @FXML private TextField txtSearchFournisseur;
    @FXML private TextField txtSearchAnnuaire;

    @FXML private TableView<Fournisseur> tableFournisseurs;
    @FXML private TableColumn<Fournisseur, String> colFourNom, colFourTel, colFourEmail, colFourAdress;

    private FilteredList<CommandeFournisseur> filteredData;
    private SortedList<CommandeFournisseur> sortedData;
    
    private static final int ITEMS_PAR_PAGE = 12;
    private int currPage = 0;

    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initialize() {
        try {
            DataService.refreshCommandesFournisseur();
            //DataService.refreshFournisseurs();
        } catch (Exception e) {
            e.printStackTrace();
        }

        setupCommandeTable();
        setupFournisseurTable();
        setupPaginationAndSearch(); 
    }

    private void setupCommandeTable() {
    	
    	colCmdAction.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));
    	
        colCmdId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCmdDate.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDate().format(fmt)));
        colCmdFournisseur.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getFournisseur().getNom()));
        colCmdTotal.setCellValueFactory(cell -> new SimpleStringProperty(String.format("%.3f TND", cell.getValue().getTotal())));
        colCmdStatut.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getStatut().toString()));

        tableCommandes.setRowFactory(tv -> new TableRow<>() {
            @Override protected void updateItem(CommandeFournisseur item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) setStyle("");
                else if (item.getStatut() == StatutCommande.RECEIVED) setStyle("-fx-background-color: #dcfce7;");
                else if (item.getStatut() == StatutCommande.CANCELED) setStyle("-fx-background-color: #fee2e2;");
                else if (item.getStatut() == StatutCommande.MODIFIED) setStyle("-fx-background-color: #e0f2fe;");
                else setStyle("");
            }
        });

        colCmdAction.setCellFactory(param -> new TableCell<>() {
            private final Button btnEdit = new Button("✎");
            private final Button btnReceive = new Button("✔");
            private final Button btnCancel = new Button("✖");
            private final Button btnDetails = new Button("👁");
            private final HBox pane = new HBox(5, btnDetails, btnEdit, btnReceive, btnCancel);
            private final HBox paneDone = new HBox(5, btnDetails);

            {
                btnCancel.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white; -fx-cursor: hand;");
                btnEdit.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-cursor: hand;");
                btnReceive.setStyle("-fx-background-color: #22c55e; -fx-text-fill: white; -fx-cursor: hand;");
                btnDetails.setStyle("-fx-background-color: #64748b; -fx-text-fill: white; -fx-cursor: hand;");

               
                
                pane.setAlignment(Pos.CENTER);
                paneDone.setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(CommandeFournisseur cf, boolean empty) {
                super.updateItem(cf, empty);
                if (cf == null || empty) setGraphic(null);
                else {

                	 btnCancel.setOnAction(e -> handleAnnuler(cf));
                     btnEdit.setOnAction(e -> ouvrirCommandeForm(cf));
                     btnReceive.setOnAction(e -> handleReception(cf));
                     btnDetails.setOnAction(e -> showDetails(cf));
                	
                    if (cf.getStatut() == StatutCommande.CREATED || cf.getStatut() == StatutCommande.MODIFIED) setGraphic(pane);
                    else setGraphic(paneDone);
                }
            }
        });
    }

    private void handleAnnuler(CommandeFournisseur c) {
        c.setStatut(StatutCommande.CANCELED);
        tableCommandes.refresh(); 
    }

    private void handleReception(CommandeFournisseur cf) {
        if (cf.getStatut() == StatutCommande.RECEIVED) return;
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION, "Confirmer la réception ?", ButtonType.YES, ButtonType.NO);
        if (confirmation.showAndWait().orElse(ButtonType.NO) == ButtonType.NO) return;

        try {
            Connection conn = DatabaseConnection.getConnection();
            StockDAO sDAO = new StockDAO(conn);
            CommandeFournisseurDAO cfDAO = new CommandeFournisseurDAO(conn);

            for(LigneCommandeFournisseur l : cf.getLignes()) {
                //sDAO.register(new Stock(l.getProduit(), l.getQuantite()));
            	sDAO.augmenter(l.getProduit().getId(), l.getQuantite());
            }
            cf.setStatut(StatutCommande.RECEIVED);
            cfDAO.updateStatut(cf.getId(), StatutCommande.RECEIVED);

            DataService.refreshStocks();
            tableCommandes.refresh(); 
            new Alert(Alert.AlertType.INFORMATION, "Stock mis à jour !").show();
        } catch (SQLException ex) {
            new Alert(Alert.AlertType.ERROR, "Erreur DB: " + ex.getMessage()).show();
        }
    }

    @FXML void handleNouvelleCommande(ActionEvent e) { ouvrirCommandeForm(null); }

    void ouvrirCommandeForm(CommandeFournisseur c) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("NouvelleCommandeLayout.fxml"));
            Parent root = loader.load();

            if (c != null) {
                NouvelleCommandeController ctrl = loader.getController();
                ctrl.initData(c);
            }

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.showAndWait();

            setupPaginationAndSearch(); 
            
        } catch(Exception e) { e.printStackTrace(); }
    }

    private void setupFournisseurTable() {
        //tableFournisseurs.setItems(DataService.getFournisseurs());
        colFourNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colFourTel.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        colFourEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colFourAdress.setCellValueFactory(new PropertyValueFactory<>("adresse"));
        
        refreshAnnuaire("");
        
        txtSearchAnnuaire.textProperty().addListener((obs, oldVal, newVal) -> {
        	refreshAnnuaire(newVal);
        });
    }
    
    private void refreshAnnuaire(String keyword) {
    	List<Fournisseur> res = DataService.searchFournisseurs(keyword);
    	tableFournisseurs.setItems(FXCollections.observableArrayList(res));
    }

    @FXML
    void handleAjoutFournisseur(ActionEvent ae) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AjoutFournisseurLayout.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.showAndWait();
            
            refreshAnnuaire(txtSearchAnnuaire.getText());
        } catch(Exception e) { e.printStackTrace(); }
    }

    private void setupPaginationAndSearch() {
        filteredData = new FilteredList<>(DataService.getCommandesFournisseur(), p -> true);

        txtSearchFournisseur.textProperty().addListener((obs, oldVal, newVal) -> {
        	
        	
            filteredData.setPredicate(cmd -> {
                if (newVal == null || newVal.isEmpty()) return true;

                String lowerCaseFilter = newVal.toLowerCase();
                
                boolean matchStatut = cmd.getStatut().toString().toLowerCase().contains(lowerCaseFilter);
                
                if(matchStatut) return true;
                
                Fournisseur f = cmd.getFournisseur();
                
                if(f != null) {
                	if(f.getNom() != null && f.getNom().toLowerCase().contains(newVal))
                		return true;

                	if(f.getTelephone() != null && f.getTelephone().toLowerCase().contains(newVal))
                		return true;

                	if(f.getEmail() != null && f.getEmail().toLowerCase().contains(newVal))
                		return true;
                }

                return false;
            });
            
            currPage = 0;
            miseAJourTable();
        });

        sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tableCommandes.comparatorProperty());
        miseAJourTable();
    }

    private void miseAJourTable() {
        int nombreTotalElements = sortedData.size();
        int nombrePages = (int) Math.ceil((double) nombreTotalElements / ITEMS_PAR_PAGE);

        if (nombrePages == 0) nombrePages = 1;

        if (currPage >= nombrePages) currPage = nombrePages - 1;
        if (currPage < 0) currPage = 0;

        int idx_debut = currPage * ITEMS_PAR_PAGE;
        int idx_fin = Math.min(idx_debut + ITEMS_PAR_PAGE, nombreTotalElements);

        List<CommandeFournisseur> pageElements = sortedData.subList(idx_debut, idx_fin);
        tableCommandes.setItems(FXCollections.observableArrayList(pageElements));

        lblCmdPageInfo.setText("Page " + (currPage + 1) + " / " + nombrePages);
        btnCmdPrev.setDisable(currPage == 0);
        btnCmdNext.setDisable(currPage >= nombrePages - 1);
    }

    @FXML
    void handlePrevPage(ActionEvent event) {
        if (currPage > 0) {
            currPage--;
            miseAJourTable();
        }
    }

    @FXML
    void handleNextPage(ActionEvent event) {
        int nombreTotal = sortedData.size();
        int maxPage = (int) Math.ceil((double) nombreTotal / ITEMS_PAR_PAGE) - 1;
        if (currPage < maxPage) {
            currPage++;
            miseAJourTable();
        }
    }

    private void showDetails(CommandeFournisseur cmd) {
         Dialog<Void> dialog = new Dialog<>();
         dialog.setTitle("Détails Commande #" + cmd.getId());
         dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
         
         TableView<LigneCommandeFournisseur> tableDetails = new TableView<>();
         TableColumn<LigneCommandeFournisseur, String> cProd = new TableColumn<>("Produit");
         cProd.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getProduit().getNom()));
         TableColumn<LigneCommandeFournisseur, Integer> cQty = new TableColumn<>("Qté");
         cQty.setCellValueFactory(new PropertyValueFactory<>("quantite"));
         TableColumn<LigneCommandeFournisseur, String> cPrix = new TableColumn<>("Prix Achat");
         cPrix.setCellValueFactory(new PropertyValueFactory<>("prixAchat"));
         TableColumn<LigneCommandeFournisseur, String> cSTotal = new TableColumn<>("S/Total");
         cSTotal.setCellValueFactory(new PropertyValueFactory<>("sousTotal"));
         
         tableDetails.getColumns().addAll(cProd, cQty, cPrix, cSTotal);
         tableDetails.setItems(FXCollections.observableArrayList(cmd.getLignes()));
         
         dialog.getDialogPane().setContent(tableDetails);
         dialog.showAndWait();
    }
    
    
    
    @FXML
    private void exportCmdFourPDF(ActionEvent ae) {
    	if(sortedData == null || sortedData.isEmpty()) return;
    	
    	ObservableList<CommandeFournisseur> currView = tableCommandes.getItems();
    	tableCommandes.setItems(sortedData);
    	
    	
		try {
			PDFService.exportTableViewToPDF(tableCommandes, "Historique Commandes Fournisseurs", (Stage) tableCommandes.getScene().getWindow(), "Actions");
    	} catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erreur lors de l'export PDF").show();
        }
    }
    
    
    
}