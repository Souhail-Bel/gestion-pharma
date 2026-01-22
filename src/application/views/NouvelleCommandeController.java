package application.views;

import application.dao.CommandeFournisseurDAO;
import application.exceptions.CommandeInvalideException;
import application.modeles.*;
import application.resources.DatabaseConnection;
import application.services.DataService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDateTime;

public class NouvelleCommandeController {

    @FXML private Label lblTitle;
    @FXML private ComboBox<Fournisseur> comboFournisseur;
    @FXML private TextField txtSearchProduit, txtQte, txtPrixAchat;
    @FXML private TableView<Stock> tableCatalogue;
    @FXML private TableColumn<Stock, String> colCatNom;
    @FXML private TableColumn<Stock, Integer> colCatStock;
    @FXML private TableView<LigneCommandeFournisseur> tablePanier;
    @FXML private TableColumn<LigneCommandeFournisseur, String> colPanierNom;
    @FXML private TableColumn<LigneCommandeFournisseur, Integer> colPanierQte;
    @FXML private TableColumn<LigneCommandeFournisseur, Double> colPanierPrix;
    @FXML private TableColumn<LigneCommandeFournisseur, Void> colAction;

    private final ObservableList<LigneCommandeFournisseur> panierList = FXCollections.observableArrayList();
    
    private CommandeFournisseur currCf = null;

    @FXML
    public void initialize() {
        lblTitle.setText("Nouvelle Commande");
        comboFournisseur.setItems(DataService.getFournisseurs());

        FilteredList<Stock> filteredStocks = new FilteredList<>(DataService.getStockGlobal(), b -> true);
        txtSearchProduit.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredStocks.setPredicate(stock -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return stock.getProduit().getNom().toLowerCase().contains(lowerCaseFilter);
            });
        });
        tableCatalogue.setItems(filteredStocks);
        colCatNom.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getProduit().getNom()));
        colCatStock.setCellValueFactory(new PropertyValueFactory<>("quantiteDisponible"));

        tablePanier.setItems(panierList);
        colPanierNom.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getProduit().getNom()));
        colPanierQte.setCellValueFactory(new PropertyValueFactory<>("quantite"));
        colPanierPrix.setCellValueFactory(new PropertyValueFactory<>("prixAchat"));
        setupDeleteButton();
    }
    
    
    public void initData(CommandeFournisseur cf) {
    	if(cf != null) {
    		this.currCf = cf;
    		
    		lblTitle.setText("Modifier Commande #" + cf.getId());
    		
    		comboFournisseur.getItems().stream()
    			.filter(f -> f.getId() == cf.getFournisseur().getId())
    			.findFirst()
    			.ifPresent(comboFournisseur::setValue);
    		
    		
    		for(LigneCommandeFournisseur lc : cf.getLignes())
    			panierList.add(new LigneCommandeFournisseur(null, lc.getProduit(), lc.getQuantite(), lc.getPrixAchat()));
    		
    	}
    }
    

    @FXML
    void handleAjouterLigne(ActionEvent e) {
        Stock selected = tableCatalogue.getSelectionModel().getSelectedItem();
        if (selected == null) {
            alert(Alert.AlertType.WARNING, "Sélectionnez un produit.");
            return;
        }
        try {
            int qte = Integer.parseInt(txtQte.getText());
            double prix = Double.parseDouble(txtPrixAchat.getText());
            if (qte <= 0 || prix < 0) {
                throw new CommandeInvalideException("Quantité ou prix invalide.");
            }
            LigneCommandeFournisseur ligne = new LigneCommandeFournisseur(null, selected.getProduit(), qte, prix);
            panierList.add(ligne);
            txtQte.clear();
            txtPrixAchat.clear();
        } catch (NumberFormatException | CommandeInvalideException ex) {
            alert(Alert.AlertType.ERROR, ex.getMessage());
        }
    }

    @FXML
    void handleValider(ActionEvent e) {
        if (comboFournisseur.getValue() == null || panierList.isEmpty()) {
            alert(Alert.AlertType.WARNING, "Choisissez un fournisseur et ajoutez au moins un produit.");
            return;
        }
        try {
        	CommandeFournisseurDAO cfDao = new CommandeFournisseurDAO(DatabaseConnection.getConnection());
        	
        	
        	if(currCf == null) {
        		int newId = cfDao.taille()+1;
                CommandeFournisseur cmd = new CommandeFournisseur(newId, comboFournisseur.getValue(), LocalDateTime.now(), "CREATED");
                cmd.getLignes().addAll(panierList);
                cfDao.save(cmd);
        	} else { // edit
        		currCf.setFournisseur(comboFournisseur.getValue());
        		currCf.getLignes().clear();
        		currCf.getLignes().addAll(panierList);
        		
        		if(currCf.getStatut() == StatutCommande.CREATED)
        			currCf.setStatut(StatutCommande.MODIFIED);
        		
        		cfDao.update(currCf);
        		
        	}
        	
        	
            DataService.refreshCommandesFournisseur();
            close();
        } catch (SQLException ex) {
        	ex.printStackTrace();
            alert(Alert.AlertType.ERROR, "Erreur lors de la sauvegarde : " + ex.getMessage());
        }
    }

    @FXML
    void handleAnnuler(ActionEvent e) {
        close();
    }

    private void close() {
        ((Stage) lblTitle.getScene().getWindow()).close();
    }

    private void setupDeleteButton() {
        colAction.setCellFactory(param -> new TableCell<>() {
            final Button btn = new Button("X");
            {
                btn.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                btn.setOnAction(ev -> panierList.remove(getTableView().getItems().get(getIndex())));
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btn);
            }
        });
    }

    private void alert(Alert.AlertType type, String msg) {
        new Alert(type, msg).showAndWait();
    }

    public void preFill(Produit p) {
        tableCatalogue.getItems().stream()
                .filter(stock -> stock.getProduit().getId() == p.getId())
                .findFirst()
                .ifPresent(stock -> tableCatalogue.getSelectionModel().select(stock));
    }
}