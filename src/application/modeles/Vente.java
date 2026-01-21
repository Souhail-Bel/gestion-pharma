package application.modeles;
import application.dao.employeeDAO;
import application.dao.clientDAO;
import application.resources.DatabaseConnection;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Vente {
    private int id;
    private LocalDateTime date;
    private Client Client;
    private Employe Employe;
    private double total;
    private List<LigneVente> lignes = new ArrayList<>();
    private clientDAO cdao = new clientDAO(DatabaseConnection.getConnection());
    private employeeDAO edao = new employeeDAO(DatabaseConnection.getConnection());
    public Vente(int id, LocalDateTime date,int ClientID,int EmployeID , double total) throws SQLException {

        this.id = id;
        this.date = date;
        this.Client = cdao.FindByID(ClientID);
        this.Employe = edao.FindByID(EmployeID);
        this.total = total;
    }

    public void addLigne(LigneVente ligne) {
        lignes.add(ligne);
        total += ligne.getSousTotal();
    }
    public int getId() {return id;}
    public LocalDateTime getDate() { return date; }
    public Client getClient() {return Client;}
    public Employe getEmploye() { return Employe; }
    public double getTotal() {return total;}
    public List<LigneVente> getLignes() { return lignes; }


}