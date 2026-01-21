package application.views;

import application.modeles.Produit;
import application.modeles.Stock;
import application.services.DataService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.SQLException;

public class AjoutProduitController {

    @FXML private Label lblTitle;
    @FXML private TextField txtNom;
    @FXML private TextField txtPrix;
    @FXML private Spinner<Integer> spinStock;
    @FXML private Spinner<Integer> spinSeuil;
    @FXML private Label lblError;

    private boolean editMode = false;
    private Stock stockToEdit = null;

    @FXML
    public void initialize() {
        spinStock.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10000, 0));
        spinSeuil.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1000, 5));
        
        txtPrix.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*(\\.\\d*)?")) {
                txtPrix.setText(oldValue);
            }
        });
    }


    public void setStockData(Stock stock) {
        this.stockToEdit = stock;
        this.editMode = true;
        
        lblTitle.setText("Modifier Produit");
        txtNom.setText(stock.getProduit().getNom());
        txtPrix.setText(String.valueOf(stock.getProduit().getPrixVente()));
        spinStock.getValueFactory().setValue(stock.getQuantiteDisponible());
        spinSeuil.getValueFactory().setValue(stock.getProduit().getSeuilMinimal());
    }

    @FXML
    private void handleSave(ActionEvent event) throws SQLException {
        if (!validateInput()) return;

        String nom = txtNom.getText().trim();
        
        // verifier dupliqué
        if(!editMode) {
        	boolean existe = DataService.getStockGlobal().stream()
        			.anyMatch(s -> s.getProduit().getNom().equalsIgnoreCase(nom));
        	
        	if(existe) {
        		afficherErreur("Produit existe déjà! Modifez-le.");
        		return;
        	}
        }
        
        double prix = Double.parseDouble(txtPrix.getText());
        int quantite = spinStock.getValue();
        int seuil = spinSeuil.getValue();

        if (editMode) {
            stockToEdit.getProduit().setNom(nom);
            stockToEdit.getProduit().setPrixVente(prix);
            stockToEdit.getProduit().setSeuilMinimal(seuil);
            stockToEdit.setQuantiteDisponible(quantite);
        } else {
            int newId = DataService.getStockGlobal().size() + 1;
            
            Produit p = new Produit(newId, nom, prix, seuil);
            Stock s = new Stock(p.getId(), quantite);
            
            DataService.getStockGlobal().add(s);
        }

        fermerFenetre();
    }

    @FXML
    private void handleCancel(ActionEvent event) {
    	fermerFenetre();
    }

    private void fermerFenetre() {
        Stage stage = (Stage) txtNom.getScene().getWindow();
        stage.close();
    }

    private boolean validateInput() {
        if (txtNom.getText().isEmpty()) {
        	afficherErreur("Le nom est obligatoire.");
            return false;
        }
        try {
            double p = Double.parseDouble(txtPrix.getText());
            if (p < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
        	afficherErreur("Le prix doit être un nombre valide.");
            return false;
        }
        return true;
    }
    
    private void afficherErreur(String msg) {
        lblError.setText(msg);
        lblError.setVisible(true);
    }
}