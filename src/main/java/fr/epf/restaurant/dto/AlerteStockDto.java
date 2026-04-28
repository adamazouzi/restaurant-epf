package fr.epf.restaurant.dto;

public record AlerteStockDto(
        Long ingredientId,
        String nom,
        String unite,
        double stockActuel,
        double seuilAlerte,
        double quantiteACommander
) {
}