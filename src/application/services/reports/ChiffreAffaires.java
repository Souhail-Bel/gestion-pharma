package application.services.reports;

import application.dao.venteDAO;
import application.modeles.Vente;
import application.resources.DatabaseConnection;

import java.sql.SQLException;
import java.util.ArrayList;

public class ChiffreAffaires {
    private venteDAO venteDAO;
    public ChiffreAffaires() throws SQLException {
        this.venteDAO = new venteDAO(DatabaseConnection.getConnection());
    }
    public double getChiffreAffaires() throws SQLException {
        return venteDAO.getTotalVentes();
    }
    public ArrayList<Vente> getAllVentes() throws SQLException {
        return venteDAO.getAllVentes();
    }

}
