package fr.epf.restaurant.controller;

import fr.epf.restaurant.dao.FournisseurDao;
import fr.epf.restaurant.dto.FournisseurCatalogueDto;
import fr.epf.restaurant.exception.RessourceNonTrouveeException;
import fr.epf.restaurant.model.Fournisseur;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/fournisseurs")
public class FournisseurController {

    private final FournisseurDao fournisseurDao;

    public FournisseurController(FournisseurDao fournisseurDao) {
        this.fournisseurDao = fournisseurDao;
    }

    @GetMapping
    public List<Fournisseur> lister() {
        return fournisseurDao.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Fournisseur creer(@RequestBody Fournisseur fournisseur) {
        Long id = fournisseurDao.create(fournisseur);
        return fournisseurDao.findById(id);
    }

    @GetMapping("/{id}/catalogue")
    public List<FournisseurCatalogueDto> getCatalogue(@PathVariable Long id) {
        Fournisseur fournisseur = fournisseurDao.findById(id);
        if (fournisseur == null) {
            throw new RessourceNonTrouveeException(
                    "Fournisseur introuvable avec l'id " + id
            );
        }
        return fournisseurDao.findCatalogueByFournisseurId(id);
    }
}