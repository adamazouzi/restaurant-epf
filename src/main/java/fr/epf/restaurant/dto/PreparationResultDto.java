package fr.epf.restaurant.dto;

import fr.epf.restaurant.model.CommandeClient;

import java.util.List;

public record PreparationResultDto(
        CommandeClient commande,
        List<AlerteStockDto> alertes
) {
}