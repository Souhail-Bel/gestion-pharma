package application.views;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Optional;

import application.exceptions.CommandeInvalideException;
import application.exceptions.StockInsuffisantException;
import application.modeles.Client;
import application.modeles.Employe;
import application.modeles.LigneVente;
import application.modeles.Stock;
import application.modeles.Vente;
import application.services.DataService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
// import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableCell;
import javafx.scene.layout.GridPane;

public class VenteController {

    @FXML private Button btnAjouter;
    @FXML private Button btnValider;

    @FXML private TableColumn<Stock, String> colCatNom;
    @FXML private TableColumn<Stock, Double> colCatPrix;
    @FXML private TableColumn<Stock, Integer> colCatStock;
    
    @FXML private TableColumn<LigneVente, String> colPanierNom;
    @FXML private TableColumn<LigneVente, Integer> colPanierQte;
    @FXML private TableColumn<LigneVente, Double> colPanierTotal;
    @FXML private TableColumn<LigneVente, Void> colAction;

    @FXML private ComboBox<Client> comboClient;

    @FXML private Label lblError;
    @FXML private Label lblTotal;
    

    @FXML private TableView<Stock> tableCatalogue;
    @FXML private TableView<LigneVente> tablePanier;
    
    private ObservableList<LigneVente> panierList = FXCollections.observableArrayList();
    
    @FXML private TextField txtSearch;
    @FXML private TextField txtSearchClient;

    
    @FXML
    public void initialize() {
    	remplirTable();
    	
    	tablePanier.setItems(panierList);
    	comboClient.setItems(DataService.getClients());
    	
    	//if(!clientList.isEmpty()) comboClient.getSelectionModel().select(0);
    	if (!DataService.getClients().isEmpty())
            comboClient.getSelectionModel().select(0);
    	
    	// ajouter un "X" pour supprimer un ligne de vente
    	colAction.setCellFactory(param -> new TableCell<>() {
    	    private final Button btn = new Button("X");

    	    {
    	        btn.setStyle("-fx-background-color: #ffebee; -fx-text-fill: #c62828; -fx-font-weight: bold; -fx-cursor: hand;");
    	        btn.setOnAction(event -> {
    	            LigneVente item = getTableView().getItems().get(getIndex());
    	            handleRetirerArticle(item);
    	        });
    	    }

    	    @Override
    	    protected void updateItem(Void item, boolean empty) {
    	        super.updateItem(item, empty);
    	        if (empty) {
    	            setGraphic(null);
    	        } else {
    	            setGraphic(btn);
    	        }
    	    }
    	});
    	
    	setupRechercheClient();
    }
    
    
    @FXML
    void handleAjouter(ActionEvent event) {
    	lblError.setVisible(false);
    	Stock selected = tableCatalogue.getSelectionModel().getSelectedItem();
    	
    	if(selected == null) return;
    	
    	try {
    		ajouterProduitAuPanier(selected, 1);
    		
    		tablePanier.refresh();
    		miseAJourTotal();
    	} catch (StockInsuffisantException e) {
    		lblError.setText("Erreur: " + e.getMessage());
    		lblError.setVisible(true);
    	}
    }
    
    @FXML
    void handleRetirerArticle(LigneVente selected) {
    	
    	if(selected != null) {
    		panierList.remove(selected);
    		miseAJourTotal();
    	}
    	
    }

    @FXML
    void handleValiderVente(ActionEvent event) {
    		lblError.setVisible(false);
    		
    		try {
    			validerCommande();
    			
    			int v_Id = DataService.getHistoriqueVentes().size() + 1;
    			Client c = comboClient.getValue();
    			Employe e = UI_Controller.getUtilisateur();
    			
    			Vente vente = new Vente(v_Id, c, e);
    			
    			for(LigneVente lv : panierList) {
    				vente.addLigne(lv);
    				
    				for (Stock stockItem : DataService.getStockGlobal()) {
    					if(stockItem.getProduit().getId() == lv.getProduit().getId()) {
    						int ancQte = stockItem.getQuantiteDisponible();
    						int venduQte = lv.getQuantite();
    						
    						stockItem.setQuantiteDisponible(ancQte - venduQte);
    						
    						break;
    					}
    				}
    			}
    			
    			DataService.getHistoriqueVentes().add(vente);
    			
    			Alert alert = new Alert(
    					Alert.AlertType.INFORMATION,
    					"Vente enregistrée!"
    			);
    			alert.showAndWait();
    			
    			panierList.clear();
    			miseAJourTotal();
    			tableCatalogue.refresh();
    			
    		} catch (CommandeInvalideException e) {
    			lblError.setText(e.getMessage());
    			lblError.setVisible(true);
    		} catch (SQLException e) {
                throw new RuntimeException(e);
            }
    }
    
    
    
    private void ajouterProduitAuPanier(Stock stockItem, int qteDemandee) throws StockInsuffisantException {
    	int qteDansPanier = 0;
    	LigneVente currLigne = null;
    	
    	for(LigneVente lv : panierList) {
    		if(lv.getProduit().getId() == stockItem.getProduit().getId()) {
    			qteDansPanier = lv.getQuantite();
    			currLigne = lv;
    			break;
    		}
    	}
    	
    	if((qteDansPanier + qteDemandee) > stockItem.getQuantiteDisponible()) {
    		throw new StockInsuffisantException("Stock insuffisant pour " + stockItem.getProduit().getNom());
    	}
    	
    	// mettre a jour ou ajouter
    	if(currLigne != null) {
    		currLigne.setQuantite(qteDansPanier + qteDemandee);
    	} else {
    		panierList.add(new LigneVente(stockItem.getProduit(), qteDemandee));
    	}
    }
    
    private void validerCommande() throws CommandeInvalideException {
    	if(panierList.isEmpty()) {
    		throw new CommandeInvalideException("Panier vide.");
    	}
    	
    	if (comboClient.getValue() == null) {
    		throw new CommandeInvalideException("Sélectionnez un client.");
    	}
    }
    
    
    
    private void miseAJourTotal() {
    	double total = 0.0;
    	for(LigneVente lv : panierList)
    		total += lv.getSousTotal();
    	
    	lblTotal.setText(String.format("%.2f TND", total));
    }

    
    private void remplirTable() {
    	colCatNom.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getProduit().getNom()));
        colCatPrix.setCellValueFactory(cell -> new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getProduit().getPrixVente()));
        colCatStock.setCellValueFactory(new PropertyValueFactory<>("quantiteDisponible"));

        colPanierNom.setCellValueFactory(new PropertyValueFactory<>("nomProduit"));
        colPanierQte.setCellValueFactory(new PropertyValueFactory<>("quantite"));
        colPanierTotal.setCellValueFactory(new PropertyValueFactory<>("sousTotal"));
        
        tableCatalogue.setItems(DataService.getStockGlobal());
    }
    
    
    
    
    
    
    private void setupRechercheClient() {
    	FilteredList<Client> filteredClients = new FilteredList<>(DataService.getClients(), p -> true);
        
        comboClient.setItems(filteredClients);

        // filtre selon nom ou telephone
        txtSearchClient.textProperty().addListener((obs, ancVal, nvVal) -> {
            filteredClients.setPredicate(client -> {
                if (nvVal == null || nvVal.isEmpty()) return true;
                
                String nvValLower = nvVal.toLowerCase();
                
                return client.getNom().toLowerCase().contains(nvValLower) 
                    || client.getTelephone().contains(nvValLower);
            });
            
            if (!filteredClients.isEmpty()) {
                comboClient.show();
            }
        });
    }
    

    
    @FXML
    void handleNouveauClient(ActionEvent event) {
    	/*
    	TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Nouveau Client");
        dialog.setHeaderText("Création");
        dialog.setContentText("Nom du client :");
    
        dialog.showAndWait().ifPresent(nom -> {
        	if(!nom.trim().isEmpty()) {
        		Client nv = new Client(0, nom, "");
        		clientList.add(nv);
        		comboClient.getSelectionModel().select(nv);
        	}
        });*/
    	
        Dialog<Client> dialog = new Dialog<>();
        dialog.setTitle("Nouveau Client");
        dialog.setHeaderText("Ajouter un client...");

        ButtonType confButtonType = new ButtonType("Ajouter", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField username = new TextField();
        username.setPromptText("Nom Prénom");
        TextField phone = new TextField();
        phone.setPromptText("Téléphone");

        Label lblErreurDialog = new Label();
        lblErreurDialog.setStyle("-fx-text-fill: red;");
        
        grid.add(new Label("Nom :"), 0, 0);
        grid.add(username, 1, 0);
        grid.add(new Label("Tél :"), 0, 1);
        grid.add(phone, 1, 1);
        
        grid.add(lblErreurDialog, 1, 2);

        dialog.getDialogPane().setContent(grid);
        javafx.application.Platform.runLater(username::requestFocus);
        
        
        Button btnAjouterClientDialog = (Button) dialog.getDialogPane().lookupButton(confButtonType);
        
        
        btnAjouterClientDialog.addEventFilter(ActionEvent.ACTION, ae -> {
        	String num = phone.getText();
        	
        	if(num == null || !num.matches("\\d{8}")) {
        		ae.consume(); //  empecher de fermer
        		
        		lblErreurDialog.setText("Numéro invalide");
        		phone.setStyle("-fx-border-color: red;");
        	} else if (username.getText().isEmpty()) {
        		ae.consume();

        		lblErreurDialog.setText("Nom obligatoire");
        		username.setStyle("-fx-border-color: red;");
        	}
        });
        
        

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == confButtonType) {
                int newId = DataService.getClients().size() + 1; 
                return new Client(newId, username.getText(), phone.getText());
            }
            return null;
        });

        Optional<Client> result = dialog.showAndWait();

        result.ifPresent(newClient -> {
        	DataService.getClients().add(newClient);
            txtSearchClient.setText(""); 
            comboClient.getSelectionModel().select(newClient); 
        });
    }
}
