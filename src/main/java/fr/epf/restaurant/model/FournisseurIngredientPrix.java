package fr.epf.restaurant.model;

import java.math.BigDecimal;

public class FournisseurIngredientPrix {

    private Long fournisseurId;
    private String fournisseurNom;
    private Long ingredientId;
    private String ingredientNom;
    private BigDecimal prixUnitaire;

    public FournisseurIngredientPrix() {
    }

    public Long getFournisseurId() {
        return fournisseurId;
    }

    public void setFournisseurId(Long fournisseurIdValue) {
        this.fournisseurId = fournisseurIdValue;
    }

    public String getFournisseurNom() {
        return fournisseurNom;
    }

    public void setFournisseurNom(String fournisseurNomValue) {
        this.fournisseurNom = fournisseurNomValue;
    }

    public Long getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(Long ingredientIdValue) {
        this.ingredientId = ingredientIdValue;
    }

    public String getIngredientNom() {
        return ingredientNom;
    }

    public void setIngredientNom(String ingredientNomValue) {
        this.ingredientNom = ingredientNomValue;
    }

    public BigDecimal getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(BigDecimal prixUnitaireValue) {
        this.prixUnitaire = prixUnitaireValue;
    }
}