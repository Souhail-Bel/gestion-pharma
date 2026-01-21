package application.modeles;

public class Client {
    private int id;
    private String nom;
    private String telephone;

    public Client(int id, String nom, String telephone) {
        this.id = id;
        this.nom = nom;
        this.telephone = telephone;
    }

    public int getId() { return id; }
    public String getNom() { return nom; }
    public String getTelephone() { return telephone; }

    @Override
    public String toString() { return nom; }
}