package application.views;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;

import application.dao.CommandeFournisseurDAO;
import application.dao.FournisseurDAO;
import application.dao.StockDAO;
import application.modeles.CommandeFournisseur;
import application.modeles.Fournisseur;
import application.modeles.LigneCommandeFournisseur;
import application.modeles.StatutCommande;
import application.modeles.Stock;
import application.resources.DatabaseConnection;
import application.services.DataService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class NouvelleCommandeController {

    @FXML private TableColumn<Stock,String > colCatNom;
    @FXML private TableColumn<Stock, Integer> colCatStock;
    
    @FXML private TableColumn<LigneCommandeFournisseur, String> colPanierNom;
    @FXML private TableColumn<LigneCommandeFournisseur, Integer> colPanierQte;
    @FXML private TableColumn<LigneCommandeFournisseur, Double> colPanierPrix;=
    @FXML private TableColumn<LigneCommandeFournisseur, Void> colAction;

    @FXML private ComboBox<Fournisseur> comboFournisseur;

    @FXML private Label lblTitle;

    @FXML private TableView<Stock> tableCatalogue;
    @FXML private TableView<LigneCommandeFournisseur> tablePanier;

    @FXML private TextField txtQte;
    @FXML private TextField txtPrixAchat; // Add this field to FXML for prixAchat input
    @FXML private TextField txtSearchProduit;

    private ObservableList<LigneCommandeFournisseur> panierList = FXCollections.observableArrayList();
    private CommandeFournisseur currCmd = null;
    private boolean editMode = false;
    
    private FilteredList<Stock> filteredData;
    private ObservableList<Stock> stockData;
    
    @FXML
    public void initialize() {
    	
    	stockData = DataService.getStockGlobal();
    	filteredData = new FilteredList<>(stockData, p -> true);
    	
    	tableCatalogue.setItems(filteredData);
    	colCatNom.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getProduit().getNom()));
        colCatStock.setCellValueFactory(new PropertyValueFactory<>("quantiteDisponible"));

        // fonction rechercher
        txtSearchProduit.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredData.setPredicate(stock -> {
                if (newVal == null || newVal.isEmpty()) return true;
                return stock.getProduit().getNom().toLowerCase().contains(newVal.toLowerCase());
            });
        });

        
        tablePanier.setItems(panierList);
        colPanierNom.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getProduit().getNom()));
        colPanierQte.setCellValueFactory(new PropertyValueFactory<>("quantite"));
        colPanierPrix.setCellValueFactory(new PropertyValueFactory<>("prixAchat")); // Add column in FXML if needed
        
        setupSupprimerButton();
    	
    	comboFournisseur.setItems(DataService.getFournisseurs());
    }
    
    public void setCommandeData(CommandeFournisseur cmd) {
    	editMode = true;
    	currCmd = cmd;
    	
    	lblTitle.setText("Modifier Commande #" + cmd.getId());
    	comboFournisseur.setValue(cmd.getFournisseur());
    	comboFournisseur.setDisable(true);
    	
    	panierList.setAll(cmd.getLignes());
    }
    
    
    @FXML
    void handleAjouterLigne(ActionEvent event) {
        Stock selectedStock = tableCatalogue.getSelectionModel().getSelectedItem();
        if (selectedStock == null) return;

        try {
        	int qte = Integer.parseInt(txtQte.getText());
            double prixAchat = Double.parseDouble(txtPrixAchat.getText());
            if (qte <= 0 || prixAchat < 0) throw new NumberFormatException();

            LigneCommandeFournisseur existingLine = null;
            for (LigneCommandeFournisseur line : panierList) {
                if (line.getProduit().getId() == selectedStock.getProduit().getId()) {
                    existingLine = line;
                    break;
                }
            }

            if (existingLine != null)
                panierList.remove(existingLine);
            
            LigneCommandeFournisseur newLine = new LigneCommandeFournisseur(
                    0, null, 
                    selectedStock.getProduit(), 
                    qte, prixAchat
                );
            
            panierList.add(newLine);

        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Quantité ou prix invalide").show();
        }
    }

    @FXML
    void handleValider(ActionEvent event) {
        if(comboFournisseur.getValue() == null || panierList.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner un fournisseur et des produits").show();
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            CommandeFournisseurDAO cfDao = new CommandeFournisseurDAO(conn);
            Fournisseur fournisseur = comboFournisseur.getValue();

            if (editMode) {
                currCmd.setFournisseur(fournisseur);
                currCmd.getLignes().clear();
                currCmd.getLignes().addAll(panierList);
                if (currCmd.getStatut() == StatutCommande.CREATED) {
                    currCmd.setStatut(StatutCommande.MODIFIED);
                }
                cfDao.update(currCmd);
                
                // If received, update stock (add UI for status change if needed, assume here or separate button)
                if (currCmd.getStatut() == StatutCommande.RECEIVED) {
                    StockDAO sDao = new StockDAO(conn);
                    for (LigneCommandeFournisseur lc : currCmd.getLignes()) {
                        Stock stock = sDao.findById(lc.getProduit().getId());
                        if (stock != null) {
                            stock.setQuantiteDisponible(stock.getQuantiteDisponible() + lc.getQuantite());
                            sDao.update(stock);
                        } else {
                            sDao.register(new Stock(lc.getProduit().getId(), lc.getQuantite(), conn));
                        }
                    }
                    DataService.refreshStocks();
                }
            } else {
                CommandeFournisseur newCmd = new CommandeFournisseur(0, fournisseur, LocalDateTime.now(), "CREATED");
                newCmd.getLignes().addAll(panierList);
                cfDao.save(newCmd);
            }
            
            DataService.refreshCommandesFournisseur();
            fermerFenetre();
        } catch (SQLException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erreur DB: " + e.getMessage()).show();
        }
    }

    @FXML
    void handleAnnuler(ActionEvent event) {
    	fermerFenetre();
    }
    
    
    private void fermerFenetre() {
    	((Stage) txtQte.getScene().getWindow()).close();
    }
    
    
    private void setupSupprimerButton() {
    	colAction.setCellFactory(param -> new TableCell<>() {
            private final Button btn = new Button("X");
            {
                btn.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                btn.setOnAction(e -> panierList.remove(getTableView().getItems().get(getIndex())));
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
    }

}