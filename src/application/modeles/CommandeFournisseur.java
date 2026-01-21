package application.modeles;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CommandeFournisseur {
	
	
	private int id;
	private Fournisseur fournisseur;
	private LocalDateTime date;
	private StatutCommande statut;
	private List<LigneCommandeFournisseur> lignes = new ArrayList<>();
	
	public CommandeFournisseur(int id, Fournisseur fournisseur,LocalDateTime date, String statut_str) {

		this.id = id;
		this.fournisseur = fournisseur;
		this.date = java.time.LocalDateTime.now();
		
		try {
			this.statut = StatutCommande.valueOf(statut_str.toUpperCase());
		} catch (IllegalArgumentException e) {
			this.statut = StatutCommande.CREATED;
		}
	}

	public int getId() { return id; }
	public Fournisseur getFournisseur() {return fournisseur;}
	public LocalDateTime getDate() {return date;}
	public StatutCommande getStatut() {return statut;}
	public List<LigneCommandeFournisseur> getLignes() {return lignes;}
	
	public double getTotal() {
		if (lignes == null || lignes.isEmpty()) return 0.0;
		
		double somme = 0.0;
		for(LigneCommandeFournisseur lc : this.lignes)
			somme += lc.getQuantite() + lc.getPrixAchat();
		
		return somme;
	}
	
	public void setStatut(StatutCommande statut) {
		this.statut = statut;
	}

}
