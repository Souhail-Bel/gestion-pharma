package application.views;

import application.exceptions.AccesRefuseException;
import application.modeles.Employe;
import application.services.AuthService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField txtUser;
    @FXML private PasswordField txtPass;
    @FXML private Label lblError;
    @FXML private Button btnLogin;

    @FXML private TextField txtPassVisible;
    @FXML private ToggleButton btnEye;
    
    private AuthService authService;

    public LoginController() {
        this.authService = new AuthService();
    }

    @FXML
    public void initialize() {
        btnLogin.setDefaultButton(true);
        
        txtUser.textProperty().addListener((obs, old, neu) -> {
            txtUser.setStyle("");
            lblError.setVisible(false);
        });
        txtPass.textProperty().addListener((obs, old, neu) -> {
            txtPass.setStyle("");
            lblError.setVisible(false);
        });
        
        
        txtPassVisible.textProperty().bindBidirectional(txtPass.textProperty());
        
        btnEye.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            if (isSelected) {
                txtPassVisible.setVisible(true);
                txtPass.setVisible(false);
                btnEye.setText("");
            } else {
                txtPassVisible.setVisible(false);
                txtPass.setVisible(true);
                btnEye.setText("👁");
            }
        });
        
        txtPassVisible.textProperty().addListener((obs, old, neu) -> {
            txtPassVisible.setStyle(""); 
            txtPass.setStyle("");
            lblError.setVisible(false);
        });
        
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String user = txtUser.getText();
        String pass = txtPass.getText();

        if (user.isEmpty() || pass.isEmpty()) {
            showError("Veuillez remplir tous les champs.");
            return;
        }

        try {
            Employe loggedInUser = authService.login(user, pass);

            UI_Controller.setUtilisateur(loggedInUser);
            loadMainLayout(event);

        } catch (AccesRefuseException e) {
            String msg = e.getMessage();
            showError(msg);
            
            String errorStyle = "-fx-border-color: #ef4444; -fx-border-width: 2px;";

            if (msg.toLowerCase().contains("utilisateur")) {
                txtUser.setStyle(errorStyle);
                txtUser.requestFocus();
            } else if (msg.toLowerCase().contains("passe")) {
                if (txtPass.isVisible()) txtPass.setStyle(errorStyle);
                else txtPassVisible.setStyle(errorStyle);
                
                txtPass.requestFocus();
            } else {
                txtUser.setStyle(errorStyle);
                txtPass.setStyle(errorStyle);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur de connexion (Base de données).");
        }
    }

    private void showError(String message) {
        lblError.setText(message);
        lblError.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
        lblError.setVisible(true);
    }

    private void loadMainLayout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/application/views/MainLayout.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Pharma Sys - Dashboard");
            stage.centerOnScreen();
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur critique: Impossible de charger le menu principal.");
        }
    }
}