package application.services.reports;

import application.dao.VenteDAO;
import application.modeles.Vente;
import application.resources.DatabaseConnection;

import java.sql.SQLException;
import java.util.ArrayList;

public class ChiffreAffaires {
    private VenteDAO VenteDAO;
    public ChiffreAffaires() throws SQLException {
        this.VenteDAO = new VenteDAO(DatabaseConnection.getConnection());
    }
    public double getChiffreAffaires() throws SQLException {
        return VenteDAO.getTotalVentes();
    }
    public ArrayList<Vente> getAllVentes() throws SQLException {
        return VenteDAO.getAllVentes();
    }

}
