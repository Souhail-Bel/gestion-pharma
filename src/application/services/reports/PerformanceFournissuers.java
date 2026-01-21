package application.services.reports;

import application.dao.lignecommandefournisseurDAO;
import application.resources.DatabaseConnection;

import java.sql.SQLException;
import java.util.ArrayList;

public class PerformanceFournissuers {
    private int fournisseurId;
    private String fournisseurNom;
    private int totalOrders;
    private double totalAmount;
    public PerformanceFournissuers(int fournisseurId, int totalOrders, double totalAmount) {
        this.fournisseurId = fournisseurId;
        this.totalOrders = totalOrders;
        this.totalAmount = totalAmount;
    }
    public ArrayList<PerformanceFournissuers> getPerformanceData() throws SQLException {
        lignecommandefournisseurDAO fdao = new lignecommandefournisseurDAO(DatabaseConnection.getConnection());
        return fdao.FournisseursWork();
    }

}
