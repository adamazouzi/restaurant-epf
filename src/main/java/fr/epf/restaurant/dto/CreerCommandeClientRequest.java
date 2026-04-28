package fr.epf.restaurant.dto;

import java.util.List;

public record CreerCommandeClientRequest(
        Long clientId,
        List<LigneCommandeClientRequest> lignes
) {
}