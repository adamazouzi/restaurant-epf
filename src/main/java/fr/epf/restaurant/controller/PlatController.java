package fr.epf.restaurant.controller;

import fr.epf.restaurant.dao.PlatDao;
import fr.epf.restaurant.exception.RessourceNonTrouveeException;
import fr.epf.restaurant.model.Plat;
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
@RequestMapping("/api/plats")
public class PlatController {

    private final PlatDao platDao;

    public PlatController(PlatDao platDao) {
        this.platDao = platDao;
    }

    @GetMapping
    public List<Plat> lister() {
        return platDao.findAll();
    }

    @GetMapping("/{id}")
    public Plat getById(@PathVariable Long id) {
        Plat plat = platDao.findById(id);
        if (plat == null) {
            throw new RessourceNonTrouveeException("Plat introuvable avec l'id " + id);
        }
        plat.setIngredients(platDao.findIngredientsByPlatId(id));
        return plat;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Plat creer(@RequestBody Plat plat) {
        Long id = platDao.create(plat);
        return platDao.findById(id);
    }
}