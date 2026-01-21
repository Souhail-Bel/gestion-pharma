package application.services.reports;

import application.dao.LigneCommandeFournisseurDAO;
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
    public PerformanceFournissuers(int fournisseurId, String nom, int totalOrders, double totalAmount) {
        this.fournisseurId = fournisseurId;
        this.fournisseurNom = nom;
        this.totalOrders = totalOrders;
        this.totalAmount = totalAmount;
    }
    public ArrayList<PerformanceFournissuers> getPerformanceData() throws SQLException {
        LigneCommandeFournisseurDAO fdao = new LigneCommandeFournisseurDAO(DatabaseConnection.getConnection());
        return fdao.FournisseursWork();
    }
    
    public String getFournisseurNom() { return fournisseurNom; }
	public int getFournisseurId() { return fournisseurId; }
	
	public int getTotalOrders() { return totalOrders; }
	public double getTotalAmount() {return totalAmount; }

}