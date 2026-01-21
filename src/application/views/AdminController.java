package application.views;

import application.dao.FournisseurDAO;
import application.modeles.*;
import application.services.DataService;
import application.services.reports.PerformanceFournissuers;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class AdminController {

    @FXML private Label lblChiffreAffaires, lblLowStock, lblPendingOrders;
    @FXML private BarChart<String, Number> chartSales;

    @FXML private TableView<PerformanceFournissuers> tableSupplierPerf;
    @FXML private TableColumn<PerformanceFournissuers, String> colSuppNom;
    @FXML private TableColumn<PerformanceFournissuers, Integer> colSuppOrders;
    @FXML private TableColumn<PerformanceFournissuers, Double> colSuppTotal;

    // Inner class for the summary table
    public static class FournisseurStat {
        Fournisseur fournisseur;
        int nombreCommandes;
        double totalDepense;
        public FournisseurStat(Fournisseur f, int c, double t) {
            this.fournisseur = f;
            this.nombreCommandes = c;
            this.totalDepense = t;
        }
    }

    // Master Table (Suppliers)
    @FXML private TableView<FournisseurStat> tableFournisseurStats;
    @FXML private TableColumn<FournisseurStat, String> colF_Nom;
    @FXML private TableColumn<FournisseurStat, Integer> colF_Cmds;
    @FXML private TableColumn<FournisseurStat, String> colF_Total;

    // Detail Table (Orders)
    @FXML private TableView<CommandeFournisseur> tableDetailsCommande;
    @FXML private TableColumn<CommandeFournisseur, Integer> colD_Id;
    @FXML private TableColumn<CommandeFournisseur, String> colD_Date;
    @FXML private TableColumn<CommandeFournisseur, String> colD_Items;
    @FXML private TableColumn<CommandeFournisseur, String> colD_Total;
    @FXML private TableColumn<CommandeFournisseur, String> colD_Statut;

    // Alert Table
    @FXML private TableView<Stock> tableAlerts;
    @FXML private TableColumn<Stock, String> colAlertProduit;
    @FXML private TableColumn<Stock, Integer> colAlertQte;
    @FXML private TableColumn<Stock, Integer> colAlertSeuil;
    @FXML private Button btnTraiterAlertes;

    // Gestion Fournisseurs Table
    @FXML private TableView<Fournisseur> tableGestionFournisseurs;
    @FXML private TableColumn<Fournisseur, String> colGestNom;
    @FXML private TableColumn<Fournisseur, String> colGestTel;
    @FXML private TableColumn<Fournisseur, String> colGestEmail;
    @FXML private TableColumn<Fournisseur, String> colGestAdresse;
    @FXML private TableColumn<Fournisseur, Void> colGestAction;

    @FXML
    public void initialize() {
        calculateStats();
        setupChart();
        setupFournisseurStats();
        setupAlerts();
        setupGestionFournisseurs();
    }

    private void setupFournisseurStats() {
        colF_Nom.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().fournisseur.getNom()));
        colF_Cmds.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().nombreCommandes).asObject());
        colF_Total.setCellValueFactory(cell -> new SimpleStringProperty(String.format("%.2f TND", cell.getValue().totalDepense)));

        ObservableList<FournisseurStat> stats = FXCollections.observableArrayList();
        for (Fournisseur f : DataService.getFournisseurs()) {
            long count = DataService.getCommandesFournisseur().stream()
                    .filter(c -> c.getFournisseur().getId() == f.getId()).count();
            double total = DataService.getCommandesFournisseur().stream()
                    .filter(c -> c.getFournisseur().getId() == f.getId()).mapToDouble(CommandeFournisseur::getTotal).sum();

            if (count > 0) stats.add(new FournisseurStat(f, (int)count, total));
        }
        tableFournisseurStats.setItems(stats);

        colD_Id.setCellValueFactory(new PropertyValueFactory<>("id"));
        colD_Date.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDate().toString()));
        colD_Statut.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getStatut().toString()));
        colD_Total.setCellValueFactory(cell -> new SimpleStringProperty(String.format("%.3f TND", cell.getValue().getTotal())));

        colD_Items.setCellValueFactory(cell -> {
            String summary = cell.getValue().getLignes().stream()
                .map(l -> l.getProduit().getNom() + " x" + l.getQuantite())
                .collect(Collectors.joining(", "));
            return new SimpleStringProperty(summary);
        });

        tableFournisseurStats.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                ObservableList<CommandeFournisseur> details = DataService.getCommandesFournisseur().stream()
                    .filter(c -> c.getFournisseur().getId() == newVal.fournisseur.getId())
                    .collect(Collectors.toCollection(FXCollections::observableArrayList));
                tableDetailsCommande.setItems(details);
            }
        });
    }

    private void calculateStats() {
        double caf_auj = DataService.getHistoriqueVentes().stream()
                .filter(v -> v.getDate().toLocalDate().equals(LocalDate.now()))
                .mapToDouble(Vente::getTotal).sum();
        lblChiffreAffaires.setText(String.format("%.2f TND", caf_auj));

        long low = DataService.getStockGlobal().stream()
                .filter(s -> s.getQuantiteDisponible() <= s.getProduit().getSeuilMinimal()).count();
        lblLowStock.setText(String.valueOf(low));

        long pending = DataService.getCommandesFournisseur().stream()
                .filter(c -> c.getStatut() == StatutCommande.CREATED || c.getStatut() == StatutCommande.MODIFIED).count();
        lblPendingOrders.setText(String.valueOf(pending));
    }

    private void setupChart(){
        Map<String, Integer> salesMap = new HashMap<>();
        for (Vente v : DataService.getHistoriqueVentes()) {
            for (LigneVente l : v.getLignes()) {
                salesMap.merge(l.getProduit().getNom(), l.getQuantite(), Integer::sum);
            }
        }
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Unités");
        salesMap.entrySet().stream().sorted((a,b)->b.getValue().compareTo(a.getValue())).limit(5)
                .forEach(e -> series.getData().add(new XYChart.Data<>(e.getKey(), e.getValue())));
        chartSales.getData().add(series);

        PerformanceFournissuers perf = new PerformanceFournissuers(0,0,0);
        ObservableList<PerformanceFournissuers> perfData = FXCollections.observableArrayList();
        try {
            perfData.addAll(perf.getPerformanceData());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        tableSupplierPerf.setItems(perfData);
        colSuppNom.setCellValueFactory(cellData -> {
            try {
                Fournisseur f = new FournisseurDAO(application.resources.DatabaseConnection.getConnection()).findByID(cellData.getValue().getFournisseurId());
                return new SimpleStringProperty(f != null ? f.getNom() : "Inconnu");
            } catch (SQLException e) {
                e.printStackTrace();
                return new SimpleStringProperty("Erreur");
            }
        });
        colSuppOrders.setCellValueFactory(new PropertyValueFactory<>("totalOrders"));
        colSuppTotal.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
    }

    private void setupAlerts() {
        ObservableList<Stock> lowStocks = DataService.getStockGlobal().stream()
                .filter(Stock::estLowStock)
                .collect(Collectors.toCollection(FXCollections::observableArrayList));

        colAlertProduit.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getProduit().getNom()));
        colAlertQte.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getQuantiteDisponible()).asObject());
        colAlertSeuil.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().getProduit().getSeuilMinimal()).asObject());

        tableAlerts.setItems(lowStocks);
    }

    @FXML
    private void handleTraiterAlertes() {

        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Alertes traitées.");
        alert.show();
        tableAlerts.getItems().clear();
    }

    private void setupGestionFournisseurs() {
        tableGestionFournisseurs.setItems(DataService.getFournisseurs());

        colGestNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        colGestTel.setCellValueFactory(new PropertyValueFactory<>("telephone"));
        colGestEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colGestAdresse.setCellValueFactory(new PropertyValueFactory<>("adresse"));

        colGestAction.setCellFactory(param -> new TableCell<>() {
            private final Button editBtn = new Button("Modifier");

            {
                editBtn.setOnAction(e -> {
                    Fournisseur f = getTableView().getItems().get(getIndex());
                    ouvrirEditFournisseur(f);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(editBtn);
                }
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
}