package application.views;

import application.modeles.*;
import application.services.DataService;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class AdminController {

    @FXML private Label lblChiffreAffaires, lblLowStock, lblPendingOrders;
    @FXML private BarChart<String, Number> chartSales;

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

    @FXML
    public void initialize() {
        calculateStats();
        setupChart();
        setupFournisseurStats();
    }
    
    private void setupFournisseurStats() {
        // 1. Setup Master Columns
        colF_Nom.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().fournisseur.getNom()));
        colF_Cmds.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().nombreCommandes).asObject());
        colF_Total.setCellValueFactory(cell -> new SimpleStringProperty(String.format("%.3f TND", cell.getValue().totalDepense)));

        // 2. Load Data
        ObservableList<FournisseurStat> stats = FXCollections.observableArrayList();
        for (Fournisseur f : DataService.getFournisseurs()) {
            long count = DataService.getCommandesFournisseur().stream()
                    .filter(c -> c.getFournisseur().getId() == f.getId()).count();
            double total = DataService.getCommandesFournisseur().stream()
                    .filter(c -> c.getFournisseur().getId() == f.getId()).mapToDouble(CommandeFournisseur::getTotal).sum();

            if (count > 0) stats.add(new FournisseurStat(f, (int)count, total));
        }
        tableFournisseurStats.setItems(stats);

        // 3. Setup Detail Columns
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

        // 4. Add Listener for Master-Detail interaction
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
        // Basic dashboard stats
        double today = DataService.getHistoriqueVentes().stream()
                .filter(v -> v.getDate().toLocalDate().equals(LocalDate.now()))
                .mapToDouble(Vente::getTotal).sum();
        lblChiffreAffaires.setText(String.format("%.3f TND", today));

        long low = DataService.getStockGlobal().stream()
                .filter(s -> s.getQuantiteDisponible() <= s.getProduit().getSeuilMinimal()).count();
        lblLowStock.setText(String.valueOf(low));

        long pending = DataService.getCommandesFournisseur().stream()
                .filter(c -> c.getStatut() == StatutCommande.CREATED || c.getStatut() == StatutCommande.MODIFIED).count();
        lblPendingOrders.setText(String.valueOf(pending));
    }

    private void setupChart() {
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
    }
}