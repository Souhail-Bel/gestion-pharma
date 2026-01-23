package application.modeles;

public class Employe {

    public enum Role {
        ADMIN,
        EMPLOYE,
    }

    private int id;
    private String nom;
    private String prenom;
    private String username;
    private String password;
    private Role role;

    public Employe() {
    }

    ;
	
	/**
	 * Constructeur - Initialise un employé avec ses informations
	 * Convertit le rôle en chaîne en énumération Role (ADMIN ou EMPLOYE)
	 */
	public Employe(int id, String nom, String prenom, String username, String password, String role_str) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.username = username;
        this.password = password;

        try {
            this.role = Role.valueOf(role_str.toUpperCase());
        } catch (IllegalArgumentException e) {
            this.role = Role.EMPLOYE;
        }
    }

    public int getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    /**
     * Vérifie si l'employé a le rôle d'administrateur
     *
     * @return true si l'employé est admin, false sinon
     */
    public boolean estAdmin() {
        return this.role == Role.ADMIN;
    }

}
