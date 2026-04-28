package fr.epf.restaurant.controller;

import fr.epf.restaurant.dto.CreerCommandeClientRequest;
import fr.epf.restaurant.dto.PreparationResultDto;
import fr.epf.restaurant.model.CommandeClient;
import fr.epf.restaurant.service.CommandeClientService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/commandes/client")
public class CommandeClientController {

    private final CommandeClientService service;

    public CommandeClientController(CommandeClientService service) {
        this.service = service;
    }

    @GetMapping
    public List<CommandeClient> lister(
            @RequestParam(value = "statut", required = false) String statut
    ) {
        return service.findAll(statut);
    }

    @GetMapping("/{id}")
    public CommandeClient getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CommandeClient creer(@RequestBody CreerCommandeClientRequest request) {
        return service.creer(request);
    }

    @PutMapping("/{id}/preparer")
    public PreparationResultDto preparer(@PathVariable Long id) {
        return service.preparer(id);
    }

    @PutMapping("/{id}/servir")
    public CommandeClient servir(@PathVariable Long id) {
        return service.servir(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimer(@PathVariable Long id) {
        service.delete(id);
    }
}