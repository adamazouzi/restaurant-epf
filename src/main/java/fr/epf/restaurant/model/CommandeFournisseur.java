package fr.epf.restaurant.model;

import java.time.LocalDateTime;
import java.util.List;

public class CommandeFournisseur {

    private Long id;
    private Long fournisseurId;
    private Fournisseur fournisseur;
    private LocalDateTime dateCommande;
    private CommandeFournisseurStatut statut;
    private List<LigneCommandeFournisseur> lignes;

    public CommandeFournisseur() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long idValue) {
        this.id = idValue;
    }

    public Long getFournisseurId() {
        return fournisseurId;
    }

    public void setFournisseurId(Long fournisseurIdValue) {
        this.fournisseurId = fournisseurIdValue;
    }

    public Fournisseur getFournisseur() {
        return fournisseur;
    }

    public void setFournisseur(Fournisseur fournisseurValue) {
        this.fournisseur = fournisseurValue;
    }

    public LocalDateTime getDateCommande() {
        return dateCommande;
    }

    public void setDateCommande(LocalDateTime dateCommandeValue) {
        this.dateCommande = dateCommandeValue;
    }

    public CommandeFournisseurStatut getStatut() {
        return statut;
    }

    public void setStatut(CommandeFournisseurStatut statutValue) {
        this.statut = statutValue;
    }

    public List<LigneCommandeFournisseur> getLignes() {
        return lignes;
    }

    public void setLignes(List<LigneCommandeFournisseur> lignesValue) {
        this.lignes = lignesValue;
    }
}