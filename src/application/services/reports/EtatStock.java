package application.services.reports;

import application.dao.StockDAO;
import application.modeles.Stock;
import application.resources.DatabaseConnection;

import java.sql.SQLException;
import java.util.ArrayList;

public class EtatStock {
    private StockDAO stockDAO = new StockDAO(DatabaseConnection.getConnection());

    public EtatStock() throws SQLException {
    }

    public ArrayList<Stock> getEtatStock() throws SQLException {
        return stockDAO.getAllStocks();
    }
}
