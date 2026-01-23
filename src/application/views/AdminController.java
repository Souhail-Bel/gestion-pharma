package application.views;

import application.dao.FournisseurDAO;
import application.modeles.*;
import application.services.DataService;
import application.services.PDFService;
import application.services.reports.ChiffreAffaires;
import application.services.reports.PerformanceFournissuers;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class AdminController {

    @FXML private VBox globalBox;
    @FXML private Label lblChiffreAffaires, lblLowStock, lblPendingOrders;
    @FXML private BarChart<String, Number> chartSales;
    @FXML private BarChart<String, Number> chartPerfFournisseurs;

    @FXML private TableView<PerformanceFournissuers> tableSupplierPerf;
    @FXML private TableColumn<PerformanceFournissuers, String> colPerfNom;
    @FXML private TableColumn<PerformanceFournissuers, Integer> colPerfOrders;
    @FXML private TableColumn<PerformanceFournissuers, Double> colPerfAmount;

    @FXML private TextField txtSearchFournisseur;
    @FXML private TableView<Fournisseur> tableGestionFournisseurs;
    @FXML private TableColumn<Fournisseur, String> colGestNom, colGestTel, colGestEmail, colGestAdresse;
    @FXML private TableColumn<Fournisseur, Void> colGestAction;

    @FXML private TableView<Stock> tableAlertes;
    @FXML private TableColumn<Stock, String> colAlerteProduit;
    @FXML private TableColumn<Stock, Integer> colAlerteQte, colAlerteSeuil;
    @FXML private TableColumn<Stock, Void> colAlerteAction;
    
    @FXML private javafx.scene.layout.HBox boxAlertOhno;
    @FXML private javafx.scene.layout.HBox boxAlertAllesGut;

    @FXML
    public void initialize() {
        try {
            DataService.initData();
            calculateStats();
            setupVueGlobale();
            setupPerfFournisseurs();
            setupGestionFournisseurs();
            setupAlertes();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        initialize();
    }

    @FXML
    private void ouvrirAjoutFournisseur(ActionEvent event) {
        ouvrirEditFournisseur(new Fournisseur(0, "", "", "", ""));
    }

    private void calculateStats() throws SQLException {
        ChiffreAffaires ca = new ChiffreAffaires();
        lblChiffreAffaires.setText(String.format("%.2f TND", ca.getChiffreAffaires()));
        lblPendingOrders.setText(String.valueOf(DataService.getCommandesFournisseur().stream()
        		.filter(c -> c.getStatut() == StatutCommande.CREATED
        				|| c.getStatut() == StatutCommande.MODIFIED)
        		.count()
        ));

        long low = DataService.getStockGlobal().stream().filter(Stock::estLowStock).count();
        lblLowStock.setText(String.valueOf(low));
    }

    private void setupVueGlobale() throws SQLException {
        chartSales.getData().clear();
        ChiffreAffaires ca = new ChiffreAffaires();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        Map<LocalDate, Double> dailySales = new HashMap<>();
        try {
            dailySales = ca.getAllVentes().stream()
                    .filter(v -> v.getDate() != null)
                    .collect(Collectors.groupingBy(v -> v.getDate().toLocalDate(),
                            Collectors.summingDouble(Vente::getTotal)));
        } catch (NullPointerException e) {
            System.err.println("NULL date dans ventes");
        }

        if (dailySales.isEmpty()) series.getData().add(new XYChart.Data<>("Aucun données", 0.0));
        else
            dailySales.forEach((date, total) -> series.getData().add(new XYChart.Data<>(date.toString(), total)));

        chartSales.getData().add(series);
    }

    private void setupPerfFournisseurs() throws SQLException {
        chartPerfFournisseurs.getData().clear();
        PerformanceFournissuers perf = new PerformanceFournissuers(0, 0, 0);
        ObservableList<PerformanceFournissuers> perfList = FXCollections.observableArrayList(perf.getPerformanceData());
        
        tableSupplierPerf.setItems(perfList);
        
        
        colPerfNom.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getFournisseurNom()));
        colPerfOrders.setCellValueFactory(new PropertyValueFactory<>("totalOrders"));
        colPerfAmount.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        
        if (perfList.isEmpty()) series.getData().add(new XYChart.Data<>("Aucun données", 0.0));
        else
            perfList.forEach(p -> series.getData().add(new XYChart.Data<>(p.getFournisseurNom(), p.getTotalAmount())));

        chartPerfFournisseurs.getData().add(series);
    }

    private void setupGestionFournisseurs() {
        FilteredList<Fournisseur> filteredList = new FilteredList<>(DataService.getFournisseurs(), p -> true);
        tableGestionFournisseurs.setItems(filteredList);

        colGestNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colGestTel.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        colGestEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colGestAdresse.setCellValueFactory(new PropertyValueFactory<>("adresse"));
        
        colGestAction.setCellFactory(param -> new TableCell<>() {
            private final Button editBtn = new Button("✎");
            {
                editBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #0288d1; -fx-font-weight: bold; -fx-font-size: 14px; -fx-cursor: hand;");
                editBtn.setOnAction(e -> ouvrirEditFournisseur(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : editBtn);
            }
        });

        txtSearchFournisseur.textProperty().addListener((obs, oldVal, newVal) -> {
            filteredList.setPredicate(f -> {
                if (newVal == null || newVal.isEmpty()) return true;
                String minusVal = newVal.toLowerCase();
                return f.getNom().toLowerCase().contains(minusVal)
                		|| f.getEmail().toLowerCase().contains(minusVal)
                		|| f.getTelephone().toLowerCase().contains(minusVal)
                		|| f.getAdresse().toLowerCase().contains(minusVal);
            });
        });
    }
    
    
    
    @FXML
    private void exportFournisseursPDF(ActionEvent event) {
    	try {
        PDFService.exportTableViewToPDF(tableGestionFournisseurs, "Liste Fournisseurs", (Stage) globalBox.getScene().getWindow());
    	} catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Erreur lors de l'export PDF").show();
        }
    }
    
    

    private void setupAlertes() {
        ObservableList<Stock> lowStocks = DataService.getStockGlobal().filtered(Stock::estLowStock);
        tableAlertes.setItems(lowStocks);
        
        
        boolean allesGut = lowStocks.isEmpty();
        
        boxAlertOhno.setVisible(!allesGut);
        boxAlertOhno.setManaged(!allesGut);

        boxAlertAllesGut.setVisible(allesGut);
        boxAlertAllesGut.setManaged(allesGut);
        
        colAlerteProduit.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getProduit().getNom()));
        colAlerteQte.setCellValueFactory(new PropertyValueFactory<>("quantiteDisponible"));
        
        colAlerteQte.setCellFactory(column -> new TableCell<>() {
            @Override protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); } 
                else {
                    setText(item.toString());
                    if (item == 0) setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    else setStyle("-fx-text-fill: #e65100;");
                }
            }
        });

        colAlerteSeuil.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getProduit().getSeuilMinimal()).asObject());
        
        colAlerteAction.setCellFactory(param -> new TableCell<>() {
            private final Button btnOrder = new Button("COMMANDER");
            { 
                btnOrder.setStyle("-fx-background-color: #FFEBEE; -fx-text-fill: #D32F2F; -fx-font-weight: bold; -fx-background-radius: 5;");
                btnOrder.setOnAction(e -> commandeLowStock(getTableView().getItems().get(getIndex()))); 
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : btnOrder);
            }
        });
    }

    private void ouvrirEditFournisseur(Fournisseur f) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AjoutFournisseurLayout.fxml"));
            Parent root = loader.load();
            
            AjoutFournisseurController ctrl = loader.getController();
            ctrl.setFournisseurData(f);
            
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.showAndWait();
            
            DataService.refreshFournisseurs();
            tableGestionFournisseurs.refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void commandeLowStock(Stock s) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("NouvelleCommandeLayout.fxml"));
            Parent root = loader.load();
            
            NouvelleCommandeController ctrl = loader.getController();
            ctrl.preFill(s.getProduit());
            
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
        	e.printStackTrace(); }
    }
}