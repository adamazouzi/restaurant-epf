package fr.epf.restaurant.dto;

import java.math.BigDecimal;

public record RecommandationDto(
        Long ingredientId,
        String ingredientNom,
        Long fournisseurId,
        String fournisseurNom,
        BigDecimal prixUnitaire,
        double quantiteRecommandee
) {
}