package fr.epf.restaurant.controller;

import fr.epf.restaurant.dto.CreerCommandeFournisseurRequest;
import fr.epf.restaurant.model.CommandeFournisseur;
import fr.epf.restaurant.service.CommandeFournisseurService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/commandes/fournisseur")
public class CommandeFournisseurController {

    private final CommandeFournisseurService service;

    public CommandeFournisseurController(CommandeFournisseurService service) {
        this.service = service;
    }

    @GetMapping
    public List<CommandeFournisseur> lister() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public CommandeFournisseur getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommandeFournisseur creer(
            @RequestBody CreerCommandeFournisseurRequest request
    ) {
        return service.creer(request);
    }

    @PutMapping("/{id}/envoyer")
    public CommandeFournisseur envoyer(@PathVariable Long id) {
        return service.envoyer(id);
    }

    @PutMapping("/{id}/recevoir")
    public CommandeFournisseur recevoir(@PathVariable Long id) {
        return service.recevoir(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimer(@PathVariable Long id) {
        service.delete(id);
    }
}