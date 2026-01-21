package application.modeles;

import java.time.LocalDate;

public class AlerteStock {
	
	public enum Etat {
		NOUVEAU,
		TRAITE, // traité
	}
	
	private int id;
	private Produit produit;
	private LocalDate date;
	private String message;
	private Etat etat;
	
	public AlerteStock(int id, Produit produit, LocalDate date, String message, String etat_str) {
		this.id = id;
		this.produit = produit;
		this.date = date;
		this.message = message;
		try {
			this.etat = Etat.valueOf(etat_str.toUpperCase());
		} catch (IllegalArgumentException e) {
			this.etat = Etat.NOUVEAU;
		}
	}
	
	public Produit getProduit() {return produit;}
	public LocalDate getDate() {return date;}
	public String getMessage() {return this.message;}
	public Etat getEtat() {return etat;}
}
