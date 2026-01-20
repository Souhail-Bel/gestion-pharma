package application.views;

import application.exceptions.AccesRefuseException;
import application.modeles.Employe;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField txtUser;
    @FXML private PasswordField txtPass;
    @FXML private Label lblError;

    @FXML
    private void handleLogin(ActionEvent event) {
        String user = txtUser.getText();
        String pass = txtPass.getText();

        try {
        	authentifier(user, pass);
        	loadDashboard();
        } catch (AccesRefuseException e) {
        	lblError.setText(e.getMessage());
        	lblError.setVisible(true);
        	txtUser.getParent().setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-color: red; -fx-border-width: 2;");
        } catch (Exception e) {
        	e.printStackTrace();
        	lblError.setText("Erreur inattendue.");
        }
    }
    
    private void authentifier(String user, String pass) throws AccesRefuseException {
    	if(user.isEmpty() || pass.isEmpty()) {
    		throw new AccesRefuseException("Veuillez remplir tous les champs.");
    	}
    	
    	// TODO comparer avec list des utilisateurs
        if (user.equals("admin") && pass.equals("admin")) {
        	// TODO get user
        	Employe utilisateur = new Employe(1, "Akrem", "M.", "cs_chair", "", "Admin");
        	UI_Controller.setUtilisateur(utilisateur);
        } else {
            throw new AccesRefuseException("Identifiants incorrects!");
        }
    }
    
    private void loadDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/views/MainLayout.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) txtUser.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/application/application.css").toExternalForm());
            
            stage.setTitle("Application Pharma");
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            lblError.setText("Erreur de chargement du menu.");
            lblError.setVisible(true);
        }
    }
}