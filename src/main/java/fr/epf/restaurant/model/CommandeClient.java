package fr.epf.restaurant.model;

import java.time.LocalDateTime;
import java.util.List;

public class CommandeClient {

    private Long id;
    private Long clientId;
    private Client client;
    private LocalDateTime dateCommande;
    private CommandeClientStatut statut;
    private List<LigneCommandeClient> lignes;

    public CommandeClient() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long idValue) {
        this.id = idValue;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientIdValue) {
        this.clientId = clientIdValue;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client clientValue) {
        this.client = clientValue;
    }

    public LocalDateTime getDateCommande() {
        return dateCommande;
    }

    public void setDateCommande(LocalDateTime dateCommandeValue) {
        this.dateCommande = dateCommandeValue;
    }

    public CommandeClientStatut getStatut() {
        return statut;
    }

    public void setStatut(CommandeClientStatut statutValue) {
        this.statut = statutValue;
    }

    public List<LigneCommandeClient> getLignes() {
        return lignes;
    }

    public void setLignes(List<LigneCommandeClient> lignesValue) {
        this.lignes = lignesValue;
    }
}