package application.services;

import application.dao.EmployeeDAO;
import application.exceptions.AccesRefuseException;
import application.modeles.Employe;
import application.resources.DatabaseConnection;
import java.sql.SQLException;

public class AuthService {

    private EmployeeDAO edao;

    public AuthService() {
        try {
            this.edao = new EmployeeDAO(DatabaseConnection.getConnection());
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur de connexion à la base de données");
        }
    }

    public Employe login(String username, String password) throws AccesRefuseException, SQLException {
        Employe employe = edao.findEmployeByUsername(username);

        if (employe == null) {
            throw new AccesRefuseException("Nom d'utilisateur inconnu.");
        }

        String inputHash = SecurityUtils.hashPassword(password);
        
        if (!inputHash.equals(employe.getPassword())) {
            throw new AccesRefuseException("Mot de passe incorrect.");
        }

        return employe;
    }
}