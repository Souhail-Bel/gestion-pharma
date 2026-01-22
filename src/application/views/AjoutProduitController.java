package application.views;

import application.dao.ProduitDAO;
import application.dao.StockDAO;
import application.modeles.Produit;
import application.modeles.Stock;
import application.resources.DatabaseConnection;
import application.services.DataService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
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
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            ProduitDAO pDao = new ProduitDAO(conn);
            StockDAO sDao = new StockDAO(conn);
            
            if (editMode) {
                Produit p = stockToEdit.getProduit();
                p.setNom(txtNom.getText());
                p.setPrixVente(Double.parseDouble(txtPrix.getText()));
                p.setSeuilMinimal(spinSeuil.getValue());
                pDao.update(p);
                stockToEdit.setQuantiteDisponible(spinStock.getValue());
                
                sDao.updateQuantite(p.getId(), stockToEdit.getQuantiteDisponible());
            } else {
                Produit p = new Produit(0, txtNom.getText(), Double.parseDouble(txtPrix.getText()), spinSeuil.getValue());
                int prodId = pDao.register(p);
                if (prodId != -1) {
                    Stock newStock = new Stock(p, spinStock.getValue());
                    sDao.register(newStock);
                }
            }
            DataService.refreshStocks();
        } catch (SQLException e) {
            afficherErreur("DB error: " + e.getMessage());
            e.printStackTrace();
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