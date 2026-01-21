package application;
import application.modeles.*;
import application.dao.*;
import application.resources.*;

import java.sql.Connection;
import java.sql.SQLException;


public class test {
    public static void main(String[] args) {
        try {

            Connection conn = DatabaseConnection.getConnection();
            ProduitDAO pDao = new ProduitDAO(conn);
            StockDAO sDao = new StockDAO(conn);

            System.out.println(sDao.getAllStocks());


    } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
