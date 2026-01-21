package application.views;

import java.time.LocalDateTime;

import application.modeles.CommandeFournisseur;
import application.modeles.Fournisseur;
import application.modeles.LigneCommandeFournisseur;
import application.modeles.Stock;
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
    @FXML private TableColumn<LigneCommandeFournisseur, Void> colAction;

    @FXML private ComboBox<Fournisseur> comboFournisseur;

    @FXML private Label lblTitle;

    @FXML private TableView<Stock> tableCatalogue;
    @FXML private TableView<LigneCommandeFournisseur> tablePanier;

    @FXML private TextField txtQte;
    @FXML private TextField txtSearchProduit;

    private ObservableList<LigneCommandeFournisseur> panierList = FXCollections.observableArrayList();
    private CommandeFournisseur currCommande = null;
    
    @FXML
    public void initialize() {
    	comboFournisseur.setItems(DataService.getFournisseurs());
    	
    	
    	FilteredList<Stock> filteredData = new FilteredList<>(DataService.getStockGlobal(), p -> true);
    	tableCatalogue.setItems(filteredData);
    	colCatNom.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getProduit().getNom()));
        colCatStock.setCellValueFactory(new PropertyValueFactory<>("quantiteDisponible"));

        // rechercher les produits
        txtSearchProduit.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredData.setPredicate(stock -> {
                if (newVal == null || newVal.isEmpty()) return true;
                return stock.getProduit().getNom().toLowerCase().contains(newVal.toLowerCase());
            });
        });

        
        tablePanier.setItems(panierList);
        colPanierNom.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getProduit().getNom()));
        colPanierQte.setCellValueFactory(new PropertyValueFactory<>("quantite"));
        
        
        setupSupprimerButton();
    	
    }
    
    public void setCurrCommande(CommandeFournisseur cmd) {
    	this.currCommande = cmd;
    	
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
            if (qte <= 0) throw new NumberFormatException();

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
                    0,  currCommande, 
                    selectedStock.getProduit(), 
                    qte, 0.0
                );
            
            panierList.add(newLine);

        } catch (NumberFormatException e) {
            new Alert(Alert.AlertType.ERROR, "Quantité invalide").show();
        }
    }

    @FXML
    void handleValider(ActionEvent event) {
        if(comboFournisseur.getValue() == null || panierList.isEmpty()) {
            new Alert(Alert.AlertType.WARNING, "Veuillez sélectionner un fournisseur et des produits").show();
            return;
        }

        if (currCommande == null) {
            int newId = DataService.getCommandesFournisseur().size() + 1;
            Fournisseur fournisseur = comboFournisseur.getValue();
            LocalDateTime date = LocalDateTime.now();
            String statutStr = "CREATED";
            CommandeFournisseur newCmd = new CommandeFournisseur(newId, fournisseur, date, statutStr);

            newCmd.getLignes().addAll(panierList);
            
            for (LigneCommandeFournisseur ligne : panierList) {
                ligne.setCommande(newCmd);
           }
            
            DataService.getCommandesFournisseur().add(newCmd);

        } else {
            currCommande.getLignes().clear();
            currCommande.getLignes().addAll(panierList);
            
            for (LigneCommandeFournisseur ligne : panierList) {
                ligne.setCommande(currCommande);
           }
            
            if ("CREATED".equals(currCommande.getStatut().toString())) {
                currCommande.setStatut(application.modeles.StatutCommande.MODIFIED);
            }
        }

        fermerFenetre();
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
