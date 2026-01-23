package application.views;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import application.dao.FournisseurDAO;
import application.dao.StockDAO;
import application.exceptions.AccesRefuseException;
import application.modeles.Employe;
import application.modeles.Stock;
import application.resources.DatabaseConnection;
import application.services.DataService;
import javafx.event.ActionEvent;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
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
	
	private final double COLLAPSED_WIDTH = 75.0;
	private final double EXPANDED_WIDTH = 220.0;
	
    @FXML private Button btnDashboard;
    @FXML private Button btnFournisseurs;
    @FXML private Button btnLogout;
    @FXML private Button btnStock;
    @FXML private Button btnVente;
    @FXML private Button btnHistorique;
    @FXML private StackPane contentArea;
    @FXML private BorderPane mainBorderPane;

    
    @FXML private ImageView imgUserIcon;
    
    
    @FXML
    public void initialize() {
    	
    	if(currUtilisateur != null) {

            
    		String icoName = currUtilisateur.estAdmin() ? "ico_admin.png" : "ico_usr.png";
    		setIconDefaut(icoName);
    		

    		java.util.Map<String, String> GL2_Groupe_14 = new java.util.HashMap<>();
            GL2_Groupe_14.put("souhail ben belhassen", "souhail-bel");		// GL 2/2
            GL2_Groupe_14.put("akrem medimagh", "lost4onin");				// GL 2/1
            GL2_Groupe_14.put("ahmed el euch", "eleuchahmed");				// GL 2/1
            GL2_Groupe_14.put("adam bhouri", "adam-bh ");					// GL 2/1
            GL2_Groupe_14.put("mohamed aziz gadhgadhi", "aziz-gadh");		// GL 2/1
    		
            String ghUser = GL2_Groupe_14.get(currUtilisateur.getPrenom() + " " + currUtilisateur.getNom());
            
            if(ghUser != null) {
	            String url = "https://github.com/" + ghUser + ".png";
	            // asynchrone depuis du web
	            Image onImg = new Image(url, true);
	            onImg.progressProperty().addListener((obs, oldVal, newVal) -> {
	            	if(newVal.doubleValue() == 1.0 && !onImg.isError()) {
	            		imgUserIcon.setImage(onImg);
	            		imageCirculaire(imgUserIcon);
	            	}
	            });
            }
    		
    		if (!currUtilisateur.estAdmin()) {
                btnDashboard.setVisible(false);
                btnDashboard.setManaged(false);
            }
    		
    		
    		// alerte l'admin
    		if (currUtilisateur.estAdmin()) {
    			Connection conn;
    	    	try {
    	    	    conn = DatabaseConnection.getConnection();
    	    	    StockDAO sDAO = new StockDAO(conn);
    	    	    
    	    	    List<Stock> stocks = sDAO.getAllStocks();
    	    	    if(stocks != null) {
    	    	    	boolean stockLow = stocks.stream().anyMatch(Stock::estLowStock);
    	    	    	
    	    	    	if (stockLow)
    	    	    		Platform.runLater(() -> {
    	    	    			new Alert(Alert.AlertType.WARNING, "Alertes stock bas ! Vérifiez le dashboard.").show();
    	    	    		});
    	    	    }
    	    	} catch(Exception e) {
    	    		e.printStackTrace();
    	    	}
    		}


    		lblWelcome.setText("@" + currUtilisateur.getUsername());
            Label warmwelcome = new Label("Bonjour, " + currUtilisateur.getNom());
            warmwelcome.setStyle("-fx-font-size: 48px; -fx-font-weight: bold; -fx-text-fill: #94a3b8;");
            contentArea.getChildren().add(warmwelcome);
        }
    	
    	
    	try {
    	    DataService.initData();
    	} catch (SQLException e) {
    	    e.printStackTrace();
    	}
    	
    	
    	sidebar.setPrefWidth(COLLAPSED_WIDTH);
    	lblTitle.setVisible(false);
    	
    	javafx.application.Platform.runLater(() -> {
            sidebar.setPrefWidth(COLLAPSED_WIDTH);
            lblTitle.setVisible(false);
            
            for (Node node : sidebar.getChildren()) {
                if (node instanceof Button) {
                	Button btn = (Button) node;
                    btn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY); 
                    btn.setAlignment(Pos.CENTER);
                }
            }
        });
    	
    	sidebar.setOnMouseEntered(e -> {
    		expandSidebar();
    	});
    	
    	sidebar.setOnMouseExited(e -> {
    		collapseSidebar();
    	});
    }
    
    
    
    private void setIconDefaut(String path) {
    	try { imgUserIcon.setImage(new Image(getClass().getResourceAsStream("../res/icons/" + path))); }
		catch (Exception e) { System.err.println("User icône " + path + " n'est pas chargé"); }
    }
    
    
    private void imageCirculaire(ImageView imgView) {
    	double m_x = imgView.getFitWidth()/2;
    	double m_y = imgView.getFitHeight()/2;
    	
    	Circle clip = new Circle(m_x, m_y, m_y );
    	
    	imgView.setClip(clip);
    	StackPane.setAlignment(imgView, Pos.CENTER);
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
    	
    	for (Node node : sidebar.getChildren()) {
            if (node instanceof Button) {
                Button btn = (Button) node;
                btn.setContentDisplay(ContentDisplay.LEFT); //icones + texte
                btn.setAlignment(Pos.CENTER_LEFT);
            }
        }
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
    	
    	for (Node node : sidebar.getChildren()) {
            if (node instanceof Button) {
                Button btn = (Button) node;
                btn.setContentDisplay(ContentDisplay.GRAPHIC_ONLY); //icones
                btn.setAlignment(Pos.CENTER);
            }
        }
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
    	
    	try {
    		FXMLLoader loader = new FXMLLoader(getClass().getResource("LoginLayout.fxml"));
    		Parent root = loader.load();
    		
    		Stage stage = (Stage) btnLogout.getScene().getWindow();
    		
    		Scene scene = new Scene(root);
    		scene.getStylesheets().add(getClass().getResource("/application/application.css").toExternalForm());
    		
    		stage.setScene(scene);
    		stage.centerOnScreen();
    		stage.show();
    		
    		setUtilisateur(null);
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    }

    @FXML
    void handleShowDashboard(ActionEvent event) throws AccesRefuseException {
    	if(currUtilisateur != null && currUtilisateur.estAdmin()) {
    		loadView("AdminLayout.fxml");
    	} else {
    		throw new AccesRefuseException(currUtilisateur.getUsername() + "n'est pas Admin!");
    	}
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
    		
    		if ("AdminLayout.fxml".equals(fxmlFile) && !currUtilisateur.estAdmin()) {
    			throw new AccesRefuseException("Accès admin requis!");
    		}
    		
    		
    		URL fxmlURL = getClass().getResource(fxmlFile);
    		
    		if (fxmlURL == null) throw new IOException();
    		
    		FXMLLoader loader = new FXMLLoader(fxmlURL);
    		Parent view = loader.load();
    		
    		contentArea.getChildren().clear();
    		contentArea.getChildren().add(view);
    		
    	} catch (IOException e) {
    		e.printStackTrace();
			System.err.println("Couldn't load FXML '"+fxmlFile+"'");
    	} catch (AccesRefuseException e) {
    		new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
    	}
    }
    
    

}
