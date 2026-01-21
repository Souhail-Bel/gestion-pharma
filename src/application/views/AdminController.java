package application.views;

import application.modeles.StatutCommande;
import application.modeles.Vente;
import application.modeles.CommandeFournisseur;
import application.modeles.Fournisseur;
import application.modeles.LigneVente;
import application.services.DataService;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class AdminController {

    @FXML private Label lblChiffreAffaires;
    @FXML private Label lblLowStock;
    @FXML private Label lblPendingOrders;
    @FXML private BarChart<String, Number> chartSales;
    
    
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

    @FXML private TableView<FournisseurStat> tableFournisseurStats;
    @FXML private TableColumn<FournisseurStat, String> colF_Nom;
    @FXML private TableColumn<FournisseurStat, Integer> colF_Cmds;
    @FXML private TableColumn<FournisseurStat, String> colF_Total; 

    
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
    	
        colF_Nom.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().fournisseur.getNom()));
        colF_Cmds.setCellValueFactory(cell -> new SimpleIntegerProperty(cell.getValue().nombreCommandes).asObject());

        colF_Total.setCellValueFactory(cell -> 
            new SimpleStringProperty(String.format("%.3f TND", cell.getValue().totalDepense))
        );

        
        // calcul
        ObservableList<FournisseurStat> stats = FXCollections.observableArrayList();
        for (Fournisseur f : DataService.getFournisseurs()) {
            // qte
        	long count = DataService.getCommandesFournisseur().stream()
                    .filter(c -> c.getFournisseur().getId() == f.getId())
                    .count();

        	// somme
            double total = DataService.getCommandesFournisseur().stream()
                    .filter(c -> c.getFournisseur().getId() == f.getId())
                    .mapToDouble(CommandeFournisseur::getTotal)
                    .sum();

            if (count > 0) {
                stats.add(new FournisseurStat(f, (int)count, total));
            }
        }
        tableFournisseurStats.setItems(stats);


        // table detail
        colD_Id.setCellValueFactory(new PropertyValueFactory<>("id"));
        colD_Date.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getDate().toString()));
        colD_Statut.setCellValueFactory(new PropertyValueFactory<>("statut"));
        
        colD_Total.setCellValueFactory(cell -> 
             new SimpleStringProperty(String.format("%.3f TND", cell.getValue().getTotal()))
        );

        // format items
        colD_Items.setCellValueFactory(cell -> {
            String summary = cell.getValue().getLignes().stream()
                .map(l -> l.getProduit().getNom() + " x" + l.getQuantite())
                .collect(Collectors.joining(", "));
            return new SimpleStringProperty(summary);
        });


        // listener pour détail (selon fournisseur selectionné)
        tableFournisseurStats.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                Fournisseur selected = newVal.fournisseur;
                
                ObservableList<CommandeFournisseur> filteredList = DataService.getCommandesFournisseur().stream()
                    .filter(c -> c.getFournisseur().getId() == selected.getId())
                    .collect(Collectors.toCollection(FXCollections::observableArrayList));
                
                tableDetailsCommande.setItems(filteredList);
            }
        });
    }

    private void calculateStats() {
    	// chiffre d'affaires
        double aujourdhuiTotal = DataService.getHistoriqueVentes().stream()
                .filter(v -> v.getDate().toLocalDate().equals(LocalDate.now()))
                .mapToDouble(Vente::getTotal)
                .sum();
        
        lblChiffreAffaires.setText(String.format("%.2f TND", aujourdhuiTotal));

        // alertes stock
        long lowStockCount = DataService.getStockGlobal().stream()
                .filter(s -> s.getQuantiteDisponible() <= s.getProduit().getSeuilMinimal())
                .count();
        
        lblLowStock.setText(String.valueOf(lowStockCount));

        // fournisseurs en attente
        long pendingCount = DataService.getCommandesFournisseur().stream()
                .filter(c -> c.getStatut() == StatutCommande.CREATED || c.getStatut() == StatutCommande.MODIFIED)
                .count();
        
        lblPendingOrders.setText(String.valueOf(pendingCount));
    }

    private void setupChart() {
        // produit vendu
        Map<String, Integer> productSales = new HashMap<>();

        for (Vente v : DataService.getHistoriqueVentes()) {
            for (LigneVente line : v.getLignes()) {
                String name = line.getProduit().getNom();
                productSales.put(name, productSales.getOrDefault(name, 0) + line.getQuantite());
            }
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Unités Vendues");

        // top 5
        productSales.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(5)
                .forEach(e -> series.getData().add(new XYChart.Data<>(e.getKey(), e.getValue())));

        chartSales.getData().add(series);
    }
}