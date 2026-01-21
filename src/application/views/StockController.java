package application.views;

import java.util.List;

import application.modeles.Stock;
import application.services.DataService;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class StockController {

    @FXML private Button btnAddProduct;
    @FXML private Button btnNext;
    @FXML private Button btnPrev;

    @FXML private TableView<Stock> tableStock;
    @FXML private TableColumn<Stock, Integer> colId;
    @FXML private TableColumn<Stock, String> colNom;
    @FXML private TableColumn<Stock, Double> colPrix;
    @FXML private TableColumn<Stock, Integer> colQuantite;
    @FXML private TableColumn<Stock, Integer> colSeuil;
    @FXML private TableColumn<Stock, String> colStatut;
    
    @FXML private Label lblPageInfo;
    @FXML private TextField searchField;
    
    private FilteredList<Stock> filteredData;
    private SortedList<Stock> sortedData;
    
    private static final int PRODUITS_PAR_PAGE = 12;
    private int currPage = 0;
    
    
    @FXML
    public void initialize() {
    	setupTableColumns();
    	
    	filteredData = new FilteredList<>(DataService.getStockGlobal(), p -> true);
    	
    	// fonction rechercher
    	searchField.textProperty().addListener(
    			(observable, val_0, new_val) -> {
    				filteredData.setPredicate(stock -> {
    					if (new_val == null || new_val.isEmpty()) return true;
    					
    					String new_val_lower = new_val.toLowerCase();
    					
    					return stock.getProduit().getNom().toLowerCase().contains(new_val_lower);
    				});
    				
    				currPage = 0;
    				miseAJourTable();
    			}
    	);
    	
    	// tri de données filtrées
    	sortedData = new SortedList<>(filteredData);
    	sortedData.comparatorProperty().bind(tableStock.comparatorProperty());
    	miseAJourTable();
    	
    	
    	// double clic fait un edit de produit
    	tableStock.setRowFactory(tv -> {
    	    TableRow<Stock> row = new TableRow<>();
    	    row.setOnMouseClicked(event -> {
    	        if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
    	            Stock rowData = row.getItem();
    	            handleEditProduit(rowData);
    	        }
    	    });
    	    return row;
    	});
    }
    
    private void miseAJourTable() {
    	int nombreProduits = sortedData.size();
    	int nombrePages = (int) Math.ceil((double) nombreProduits / PRODUITS_PAR_PAGE);
    	
    	if(nombrePages == 0) nombrePages = 1;
    	
    	// clamp
    	if (currPage >= nombrePages) currPage = nombrePages - 1;
    	if(currPage < 0) currPage = 0;
    	
    	int idx_debut = currPage * PRODUITS_PAR_PAGE;
    	int idx_fin = Math.min(idx_debut + PRODUITS_PAR_PAGE, nombreProduits);
    	
    	List<Stock> pageProduits = sortedData.subList(idx_debut, idx_fin);
    	tableStock.setItems(FXCollections.observableArrayList(pageProduits));
    	
    	lblPageInfo.setText("Page " + (currPage + 1) + " / " + nombrePages);
    	
    	btnPrev.setDisable(currPage == 0);
    	btnNext.setDisable(currPage >= nombrePages-1);
    }
    
    
    // populer le table
    private void setupTableColumns() {
    	
    	colQuantite.setCellValueFactory(new PropertyValueFactory<>("quantiteDisponible"));
    	
    	colId.setCellValueFactory(cellData -> 
        new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getProduit().getId()));
    	
    	colNom.setCellValueFactory(cellData -> 
        new javafx.beans.property.SimpleStringProperty(cellData.getValue().getProduit().getNom()));
    	
        colSeuil.setCellValueFactory(cellData -> 
        new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getProduit().getSeuilMinimal()));
    	
        colPrix.setCellValueFactory(cellData -> 
        new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getProduit().getPrixVente()));
    	
    	colStatut.setCellValueFactory(cellData -> {
    		boolean estLowStock = cellData.getValue().difference()<0;
    		return new javafx.beans.property.SimpleStringProperty(estLowStock ? "ALERTE STOCK" : "OK");
    	});
    	
    	
    	// surcharge pour modifier le style pour le stock bas
    	colStatut.setCellFactory(col -> new TableCell<Stock, String>() {
    		@Override
    		protected void updateItem(String item, boolean empty) {
    			super.updateItem(item, empty);
    			
    			if(empty || item == null) {
    				setText(null);
    				setStyle("");
    			} else {
    				setText(item);
    				
    				if("ALERTE STOCK".equals(item)) {
			    		setTextFill(Color.RED);
			    		setStyle("-fx-font-weight: bold;");
			    	} else {
			    		setTextFill(Color.GREEN);
			    		setStyle("");
			    	}
    				
    			}
    		}
    	});
    }

    @FXML
    void handleAjouterProduit(ActionEvent event) {
    	try {
    		FXMLLoader loader = new FXMLLoader(getClass().getResource("AjoutProduitLayout.fxml"));
    		Parent root = loader.load();
    		
    		Stage stage = new Stage();
    		// bloquer main window
    		stage.initModality(Modality.APPLICATION_MODAL);
    		stage.initStyle(StageStyle.UNDECORATED);
    		stage.setScene(new Scene(root));
    		stage.showAndWait();
    		
    		tableStock.refresh();
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }

    
    private void handleEditProduit(Stock stock) {
    	try {
    		FXMLLoader loader = new FXMLLoader(getClass().getResource("AjoutProduitLayout.fxml"));
    		Parent root = loader.load();
    		
    		AjoutProduitController ctrl = loader.getController();
    		ctrl.setStockData(stock);
    		
    		Stage stage = new Stage();
    		// bloquer main window
    		stage.initModality(Modality.APPLICATION_MODAL);
    		stage.initStyle(StageStyle.UNDECORATED);
    		stage.setScene(new Scene(root));
    		stage.showAndWait();
    		
    		tableStock.refresh();
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
    @FXML
    void handlePrevPage(ActionEvent event) {
    	if(currPage > 0) {
    		currPage--;
    		miseAJourTable();
    	}
    }

    @FXML
    void handleNextPage(ActionEvent event) {
    	int nombreTotalProduits = sortedData.size();
    	int nombreMaxPages = (int) Math.ceil((double) nombreTotalProduits / PRODUITS_PAR_PAGE) - 1;
    	if(currPage < nombreMaxPages) {
    		currPage++;
    		miseAJourTable();
    	}
    }

}