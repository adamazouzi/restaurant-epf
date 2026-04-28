package fr.epf.restaurant.dto;

import java.math.BigDecimal;

public record LigneCommandeFournisseurRequest(
        Long ingredientId,
        Double quantite,
        BigDecimal prixUnitaire
) {
}