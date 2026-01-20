package application.views;

import application.modeles.Produit;
import application.modeles.Stock;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class AjoutProduitController {

    @FXML private Button btnCancel;
    @FXML private Button btnSave;

    @FXML private TextField txtNom;
    @FXML private TextField txtPrix;
    @FXML private TextField txtQuantite;
    @FXML  private TextField txtSeuil;
    
    private ObservableList<Stock> stockListRef;
    
    // setter, appelée par StockController
    public void setStockList(ObservableList<Stock> list) {
    	this.stockListRef = list;
    }
    
    private void fermerFenetre() {
    	Stage stage = (Stage) txtNom.getScene().getWindow();
    	stage.close();
    }
    
    private void afficherAlert(String titre, String contenu) {
    	Alert alert = new Alert(Alert.AlertType.ERROR);
    	alert.setTitle(titre);
    	alert.setHeaderText(null);
    	alert.setContentText(contenu);
    	alert.showAndWait();
    }

    @FXML
    void handleCancel(ActionEvent event) {
    	fermerFenetre();
    }

    @FXML
    void handleSave(ActionEvent event) {
    	try {
	    	String nom = txtNom.getText();
	    	double prix = Double.parseDouble(txtPrix.getText());
	    	int quantite = Integer.parseInt(txtQuantite.getText());
	    	int seuil = Integer.parseInt(txtSeuil.getText());
	    	
	    	// TODO check id thing
	    	int id = 999;
	    	Produit p = new Produit(id, nom, prix, seuil);
	    	Stock s = new Stock(p, quantite);
	    	
	    	if (stockListRef != null)
	    		stockListRef.add(s);
	    	
	    	fermerFenetre();
    	} catch (NumberFormatException e) {
    		afficherAlert("Erreur de format", "Veuillez vérifiez la validité des chiffres entrés");
    	}
    }

}
