package fr.epf.restaurant.model;

public class LigneCommandeClient {

    private Long id;
    private Long commandeClientId;
    private Long platId;
    private Plat plat;
    private int quantite;

    public LigneCommandeClient() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long idValue) {
        this.id = idValue;
    }

    public Long getCommandeClientId() {
        return commandeClientId;
    }

    public void setCommandeClientId(Long commandeClientIdValue) {
        this.commandeClientId = commandeClientIdValue;
    }

    public Long getPlatId() {
        return platId;
    }

    public void setPlatId(Long platIdValue) {
        this.platId = platIdValue;
    }

    public Plat getPlat() {
        return plat;
    }

    public void setPlat(Plat platValue) {
        this.plat = platValue;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantiteValue) {
        this.quantite = quantiteValue;
    }
}