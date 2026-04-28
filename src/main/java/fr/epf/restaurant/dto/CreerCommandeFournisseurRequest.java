package fr.epf.restaurant.dto;

import java.util.List;

public record CreerCommandeFournisseurRequest(
        Long fournisseurId,
        List<LigneCommandeFournisseurRequest> lignes
) {
}