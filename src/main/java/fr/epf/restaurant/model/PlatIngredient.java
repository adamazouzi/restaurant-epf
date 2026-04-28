package fr.epf.restaurant.model;

public class PlatIngredient {

    private Long platId;
    private Long ingredientId;
    private String ingredientNom;
    private String ingredientUnite;
    private double quantiteRequise;

    public PlatIngredient() {
    }

    public Long getPlatId() {
        return platId;
    }

    public void setPlatId(Long platIdValue) {
        this.platId = platIdValue;
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

    public String getIngredientUnite() {
        return ingredientUnite;
    }

    public void setIngredientUnite(String ingredientUniteValue) {
        this.ingredientUnite = ingredientUniteValue;
    }

    public double getQuantiteRequise() {
        return quantiteRequise;
    }

    public void setQuantiteRequise(double quantiteRequiseValue) {
        this.quantiteRequise = quantiteRequiseValue;
    }
}