package application.views;

import application.dao.FournisseurDAO;
import application.modeles.Fournisseur;
import application.resources.DatabaseConnection;
import application.services.DataService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.sql.SQLException;

public class AjoutFournisseurController {
    @FXML private TextField txtNom, txtTel, txtEmail, txtAdresse;
    @FXML private Label lblError;
    @FXML private Label lblTitle;

    private boolean editMode = false;
    private Fournisseur fournisseurToEdit;

    @FXML
    private void handleSave(ActionEvent event) throws SQLException {
        if (!validateInput()) return;
        
        FournisseurDAO dao = new FournisseurDAO(DatabaseConnection.getConnection());
        
        if (editMode) {
            fournisseurToEdit.setNom(txtNom.getText());
            fournisseurToEdit.setTelephone(txtTel.getText());
            fournisseurToEdit.setEmail(txtEmail.getText());
            fournisseurToEdit.setAdresse(txtAdresse.getText());
            dao.update(fournisseurToEdit);
        } else {
            Fournisseur f = new Fournisseur(0, txtNom.getText(), txtTel.getText(), txtEmail.getText(), txtAdresse.getText());
            dao.save(f);
        }
        
        DataService.refreshFournisseurs();
        fermerFenetre();
    }

    @FXML
    private void handleCancel(ActionEvent event) { fermerFenetre(); }

    private void fermerFenetre() { ((Stage) txtNom.getScene().getWindow()).close(); }

    private boolean validateInput() {
        if (txtNom.getText().isEmpty()) { afficherErreur("Nom obligatoire."); return false; }
        if (!txtTel.getText().matches("\\d{8}")) { afficherErreur("Téléphone doit être 8 chiffres."); return false; }
        if (!txtEmail.getText().matches("^[\\w-_.+]*[\\w-_.]@([\\w]+[.])+[\\w]+$")) { afficherErreur("Email invalide."); return false; }
        return true;
    }

    private void afficherErreur(String msg) { lblError.setText(msg); lblError.setVisible(true); }

    public void setFournisseurData(Fournisseur f) {
        editMode = true;
        fournisseurToEdit = f;
        txtNom.setText(f.getNom());
        txtTel.setText(f.getTelephone());
        txtEmail.setText(f.getEmail());
        txtAdresse.setText(f.getAdresse());
        lblTitle.setText("Modifier Fournisseur");
    }
}