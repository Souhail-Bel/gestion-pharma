package application.dao;

import java.sql.Connection;
import java.sql.SQLException;

import application.modeles.Stock;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
public class stockDAO {
    private Connection connection;
    public stockDAO(Connection connection) {
        this.connection = connection;
    }
    public ArrayList<Stock> getAllStocks() throws SQLException {
        String query = "SELECT * FROM STOCK";
        ArrayList<Stock> stocks = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Stock stock = new Stock(
                    rs.getInt("produit_id"),
                    rs.getInt("quantiteDisponible")
                );
                stocks.add(stock);
            }
        }
        return stocks;
    }
}
