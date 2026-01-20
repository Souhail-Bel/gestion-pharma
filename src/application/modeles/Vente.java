package application.modeles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Vente {
    private int id;
    private LocalDateTime date;
    private Client client;
    private Employe employe;
    private double total;
    private List<LigneVente> lignes = new ArrayList<>();

    public Vente(Client client, Employe employe) {
        this.date = LocalDateTime.now();
        this.client = client;
        this.employe = employe;
        this.total = 0.0;
    }
    
    public void addLigne(LigneVente ligne) {
        lignes.add(ligne);
        total += ligne.getSousTotal();
    }

    
}