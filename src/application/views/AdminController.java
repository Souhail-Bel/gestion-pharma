package application.views;

import application.modeles.StatutCommande;
import application.modeles.Vente;
import application.modeles.LigneVente;
import application.services.DataService;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class AdminController {

    @FXML private Label lblChiffreAffaires;
    @FXML private Label lblLowStock;
    @FXML private Label lblPendingOrders;
    @FXML private BarChart<String, Number> chartSales;

    @FXML
    public void initialize() {
        calculateStats();
        setupChart();
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