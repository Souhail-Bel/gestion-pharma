package application.services;
import application.exceptions.AccesRefuseException;
import application.modeles.Employe;
import application.dao.employeeDAO;
import application.resources.DatabaseConnection;

import java.sql.SQLException;

public class AuthService{
    private employeeDAO edao;
    public AuthService() throws SQLException {
        this.edao = new employeeDAO(DatabaseConnection.getConnection());
    }

    public Employe login(String username, String password) throws AccesRefuseException, SQLException {
        try {
            Employe employe = edao.findEmployeByUsername(username);
            if (employe == null ) {
                throw new AccesRefuseException("Invalid username or password.");
            }
            else if (!employe.getPassword().equals(password)) {
                throw new AccesRefuseException("Incorrect password.");}
        } catch (SQLException e) {
            throw new AccesRefuseException("Database error during authentication.");
        }
        return edao.findEmployeByUsername(username);
}
}

