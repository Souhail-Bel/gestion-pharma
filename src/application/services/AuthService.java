package application.services;

import java.sql.SQLException;

import application.dao.EmployeDAO;
import application.exceptions.AccesRefuseException;
import application.modeles.Employe;
import application.resources.DatabaseConnection;

public class AuthService {

    private EmployeDAO edao;

    public AuthService() {
        try {
            this.edao = new EmployeDAO(DatabaseConnection.getConnection());
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur de connexion à la base de données");
        }
    }

    /**
     * Authentifie un employé avec son nom d'utilisateur et son mot de passe
     * Valide les credentials contre la base de données en utilisant le hash
     * SHA-256
     *
     * @param username Nom d'utilisateur de l'employé
     * @param password Mot de passe en clair de l'employé
     * @return L'objet Employe si l'authentification réussit
     * @throws AccesRefuseException Si le nom d'utilisateur est introuvable ou
     * le mot de passe est incorrect
     * @throws SQLException En cas d'erreur d'accès à la base de données
     */
    public Employe login(String username, String password) throws AccesRefuseException, SQLException {
        // Cherche l'employé par son nom d'utilisateur
        Employe employe = edao.findEmployeByUsername(username);

        if (employe == null) {
            throw new AccesRefuseException("Nom d'utilisateur inconnu.");
        }

        // Hache le mot de passe saisi pour comparaison
        String inputHash = SecurityUtils.hashPassword(password);

        // Vérifie que le mot de passe haché correspond à celui stocké en base
        if (!inputHash.equals(employe.getPassword())) {
            throw new AccesRefuseException("Mot de passe incorrect.");
        }

        return employe;
    }
}
