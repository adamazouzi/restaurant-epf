package fr.epf.restaurant.dto;

import java.math.BigDecimal;

public record IngredientPrixDto(
        Long fournisseurId,
        String fournisseurNom,
        BigDecimal prixUnitaire
) {
}