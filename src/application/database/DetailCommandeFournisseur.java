package application.database;

import application.modeles.StatutCommande;

import java.time.LocalDateTime;

public class DetailCommandeFournisseur {
    private int id;
    private String nom_fournisseur;
    private LocalDateTime date;
    private StatutCommande statut;
    private double total;
    public DetailCommandeFournisseur(int id,String nom_fournisseur,LocalDateTime date,StatutCommande statut,double total){
        this.id=id;
        this.nom_fournisseur=nom_fournisseur;
        this.date=date;
        this.statut=statut;
        this.total=total;
    }
}
