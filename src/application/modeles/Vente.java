package application.modeles;
import application.dao.ClientDAO;
import application.dao.EmployeDAO;
import application.resources.DatabaseConnection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Vente {
  private int id;
  private LocalDateTime date;

  private int clientId;
  private int employeId;

  private Client client;
  private Employe employe;

  private double total;
  private List<LigneVente> lignes = new ArrayList<>();

  public Vente(int id, LocalDateTime date, int clientId, int employIde,
               double total) {

    this.id = id;
    this.date = date;
    this.clientId = clientId;
    this.employeId = employIde;
    this.total = total;
  }

  public Vente(Client c, Employe e) {
    this.id = 0;
    this.date = LocalDateTime.now();
    this.clientId = (c != null) ? c.getId() : 0;
    this.employeId = (e != null) ? e.getId() : 0;
    this.client = c;
    this.employe = e;
    this.total = 0.0;
  }

  public void addLigne(LigneVente ligne) { lignes.add(ligne); }
  public int getId() { return id; }
  public LocalDateTime getDate() { return date; }
  public int getClientId() { return clientId; }
  public int getEmployeId() { return employeId; }
  public double getTotal() { return total; }
  public List<LigneVente> getLignes() { return lignes; }

  public Client getClient() {
    if (client == null && clientId > 0) {
      ClientDAO cDAO;
      try {
        cDAO = new ClientDAO(DatabaseConnection.getConnection());
        client = cDAO.findById(clientId);
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    return client;
  }

  public Employe getEmploye() {
    if (employe == null && employeId > 0) {
      EmployeDAO cDAO;
      try {
        cDAO = new EmployeDAO(DatabaseConnection.getConnection());
        employe = cDAO.findById(clientId);
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    return employe;
  }
}
