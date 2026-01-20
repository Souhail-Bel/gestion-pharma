package application.modeles;

import java.time.LocalDate;

import application.modeles.Employe.Role;

public class CommandeFournisseur {
	
	public enum Statut {
		CREE,
		MODIFIE,
		ANNULE,
		RECU,
	}
	
	private int id;
	private Fournisseur fournisseur;
	private LocalDate date;
	private Statut statut;
	
	public CommandeFournisseur(int id, Fournisseur fournisseur, LocalDate date, String statut_str) {
		
		this.id = id;
		this.fournisseur = fournisseur;
		this.date = date;
		
		try {
			this.statut = Statut.valueOf(statut_str.toUpperCase());
		} catch (IllegalArgumentException e) {
			this.statut = Statut.ANNULE;
		}
	}
	
	public Fournisseur getFournisseur() {return fournisseur;}
	public LocalDate getDate() {return date;}
	public Statut getStatut() {return statut;}
}
