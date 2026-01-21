package application.modeles;

public class Client {
    private int id;
    private String nom;
    private String prenom;
    private String telephone;

    public Client(int id, String nom,String prenom, String telephone) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
    }

    public int getId() { return id; }
    public String getNom() { return nom; }
    public String getTelephone() { return telephone; }
    public String getPrenom() { return prenom; }

    @Override
    public String toString() { return nom; }
}