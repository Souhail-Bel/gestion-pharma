package application.views;

import java.io.IOException;
import java.net.URL;

import application.exceptions.AccesRefuseException;
import application.modeles.Employe;

import javafx.event.ActionEvent;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;

import javafx.animation.*;

import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Duration;

public class UI_Controller {
    
	private static Employe currUtilisateur;
	
	public static void setUtilisateur(Employe usr) {
		currUtilisateur = usr;
	}
	
	public static Employe getUtilisateur() {
		return currUtilisateur;
	}

	
	@FXML private VBox sidebar;
	@FXML private Pane dimmer;
	@FXML private Label lblTitle;
	@FXML private Label lblWelcome;
	
	private final double COLLAPSED_WIDTH = 60.0;
	private final double EXPANDED_WIDTH = 220.0;
	
    @FXML private Button btnAdmin;
    @FXML private Button btnDashboard;
    @FXML private Button btnFournisseurs;
    @FXML private Button btnLogout;
    @FXML private Button btnStock;
    @FXML private Button btnVente;
    @FXML private StackPane contentArea;
    @FXML private BorderPane mainBorderPane;

    
    
    @FXML
    public void initialize() {
    	
    	if(currUtilisateur != null)
    		lblWelcome.setText("Bienvenue, " + currUtilisateur.getNom());
    	
    	sidebar.setPrefWidth(COLLAPSED_WIDTH);
    	lblTitle.setVisible(false);
    	
    	sidebar.setOnMouseEntered(e -> {
    		expandSidebar();
    	});
    	
    	sidebar.setOnMouseExited(e -> {
    		collapseSidebar();
    	});
    }
    
    private void expandSidebar() {
    	animateSidebar(EXPANDED_WIDTH);
    	
    	// dim
    	dimmer.setVisible(true);
    	dimmer.setMouseTransparent(false);
    	FadeTransition ft = new FadeTransition(Duration.millis(250), dimmer);
    	ft.setFromValue(dimmer.getOpacity());
    	ft.setToValue(0.5);
    	ft.play();
    	
    	lblTitle.setVisible(true);
    }
    
    private void collapseSidebar() {
    	animateSidebar(COLLAPSED_WIDTH);
    	
    	// undim
    	FadeTransition ft = new FadeTransition(Duration.millis(250), dimmer);
    	ft.setFromValue(dimmer.getOpacity());
    	ft.setToValue(0.0);
    	ft.setOnFinished(e -> {
    		dimmer.setVisible(false);
    		dimmer.setMouseTransparent(true);
    	});
    	ft.play();
    	
    	lblTitle.setVisible(false);
    }
    
    private void animateSidebar(double newWidth) {
    	Timeline tm = new Timeline();
    	KeyValue keyvWidth = new KeyValue(sidebar.prefWidthProperty(), newWidth);
    	KeyFrame kf = new KeyFrame(Duration.millis(200), keyvWidth);
    	tm.getKeyFrames().add(kf);
    	tm.play();
    }
    
    
    @FXML
    void handleLogout(ActionEvent event) {
    	System.out.println("Logging out...");
    }

    @FXML
    void handleShowAdmin(ActionEvent event) throws AccesRefuseException {
    	if(currUtilisateur != null && currUtilisateur.estAdmin()) {
    		loadView("AdminLayout.fxml");
    	} else {
    		throw new AccesRefuseException(currUtilisateur.getUsername() + "n'est pas Admin!");
    	}
    }

    @FXML
    void handleShowDashboard(ActionEvent event) {
    	loadView("DashboardLayout.fxml");
    }

    @FXML
    void handleShowFournisseurs(ActionEvent event) {
    	loadView("FournisseursLayout.fxml");
    }

    @FXML
    void handleShowStock(ActionEvent event) {
    	loadView("StockLayout.fxml");
    }

    @FXML
    void handleShowVente(ActionEvent event) {
    	loadView("VenteLayout.fxml");
    }
    
    @FXML
    void handleShowHistorique(ActionEvent event) {
    	loadView("HistoriqueLayout.fxml");
    }
    
    private void loadView(String fxmlFile) {
    	try {
    		// NOTE: les views doivent etre dans le meme package "application.views"
    		// getClass() utilisé pour chercher dans ce package
    		URL fxmlURL = getClass().getResource(fxmlFile);
    		
    		if (fxmlURL == null) {
    			throw new IOException();
    		}
    		
    		FXMLLoader loader = new FXMLLoader(fxmlURL);
    		Parent view = loader.load();
    		
    		contentArea.getChildren().clear();
    		contentArea.getChildren().add(view);
    		
    	} catch (IOException e) {
    		e.printStackTrace();
			System.err.println("Couldn't load FXML '"+fxmlFile+"'");
    	}
    }
    
    

}
