package fr.epf.restaurant.model;

import java.math.BigDecimal;

public class LigneCommandeFournisseur {

    private Long id;
    private Long commandeFournisseurId;
    private Long ingredientId;
    private Ingredient ingredient;
    private double quantiteCommandee;
    private BigDecimal prixUnitaire;

    public LigneCommandeFournisseur() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long idValue) {
        this.id = idValue;
    }

    public Long getCommandeFournisseurId() {
        return commandeFournisseurId;
    }

    public void setCommandeFournisseurId(Long commandeFournisseurIdValue) {
        this.commandeFournisseurId = commandeFournisseurIdValue;
    }

    public Long getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(Long ingredientIdValue) {
        this.ingredientId = ingredientIdValue;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredientValue) {
        this.ingredient = ingredientValue;
    }

    public double getQuantiteCommandee() {
        return quantiteCommandee;
    }

    public void setQuantiteCommandee(double quantiteCommandeeValue) {
        this.quantiteCommandee = quantiteCommandeeValue;
    }

    public BigDecimal getPrixUnitaire() {
        return prixUnitaire;
    }

    public void setPrixUnitaire(BigDecimal prixUnitaireValue) {
        this.prixUnitaire = prixUnitaireValue;
    }
}