package application.services.reports;

import application.dao.produitDAO;
import application.dao.stockDAO;
import application.modeles.Stock;
import application.resources.DatabaseConnection;

import java.sql.SQLException;
import java.util.ArrayList;

public class EtatStock {
    private produitDAO produitDAO= new produitDAO(DatabaseConnection.getConnection());
    private stockDAO stockDAO= new stockDAO(DatabaseConnection.getConnection());

    public EtatStock() throws SQLException {
    }

    public ArrayList<Stock> getEtatStock() throws SQLException {
        return stockDAO.getAllStocks();
    }
}
