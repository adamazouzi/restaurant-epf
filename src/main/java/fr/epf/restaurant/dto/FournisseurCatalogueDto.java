package fr.epf.restaurant.dto;

import java.math.BigDecimal;

public record FournisseurCatalogueDto(
        Long ingredientId,
        String ingredientNom,
        String ingredientUnite,
        BigDecimal prixUnitaire
) {
}