package fr.epf.restaurant.service;

import fr.epf.restaurant.dao.ClientDao;
import fr.epf.restaurant.dao.CommandeClientDao;
import fr.epf.restaurant.dao.PlatDao;
import fr.epf.restaurant.dto.AlerteStockDto;
import fr.epf.restaurant.dto.CreerCommandeClientRequest;
import fr.epf.restaurant.dto.LigneCommandeClientRequest;
import fr.epf.restaurant.dto.PreparationResultDto;
import fr.epf.restaurant.exception.CommandeInvalideException;
import fr.epf.restaurant.exception.RessourceNonTrouveeException;
import fr.epf.restaurant.exception.StatutInvalideException;
import fr.epf.restaurant.model.Client;
import fr.epf.restaurant.model.CommandeClient;
import fr.epf.restaurant.model.CommandeClientStatut;
import fr.epf.restaurant.model.LigneCommandeClient;
import fr.epf.restaurant.model.Plat;
import fr.epf.restaurant.model.PlatIngredient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class CommandeClientService {

    private final CommandeClientDao commandeClientDao;
    private final ClientDao clientDao;
    private final PlatDao platDao;
    private final StockService stockService;

    public CommandeClientService(CommandeClientDao commandeClientDao,
                                 ClientDao clientDao,
                                 PlatDao platDao,
                                 StockService stockService) {
        this.commandeClientDao = commandeClientDao;
        this.clientDao = clientDao;
        this.platDao = platDao;
        this.stockService = stockService;
    }

    public List<CommandeClient> findAll(String statut) {
        List<CommandeClient> commandes = commandeClientDao.findAll(statut);
        for (CommandeClient commande : commandes) {
            enrichirCommande(commande);
        }
        return commandes;
    }

    public CommandeClient findById(Long id) {
        CommandeClient commande = commandeClientDao.findById(id);
        if (commande == null) {
            throw new RessourceNonTrouveeException(
                    "Commande client introuvable avec l'id " + id
            );
        }
        enrichirCommande(commande);
        return commande;
    }

    @Transactional
    public CommandeClient creer(CreerCommandeClientRequest request) {
        validerCreationCommande(request);

        Client client = clientDao.findById(request.clientId());
        if (client == null) {
            throw new RessourceNonTrouveeException(
                    "Client introuvable avec l'id " + request.clientId()
            );
        }

        for (LigneCommandeClientRequest ligne : request.lignes()) {
            Plat plat = platDao.findById(ligne.platId());
            if (plat == null) {
                throw new RessourceNonTrouveeException(
                        "Plat introuvable avec l'id " + ligne.platId()
                );
            }
        }

        Long commandeId = commandeClientDao.insertCommande(request.clientId());

        for (LigneCommandeClientRequest ligne : request.lignes()) {
            commandeClientDao.insertLigne(commandeId, ligne.platId(), ligne.quantite());
        }

        return findById(commandeId);
    }

    @Transactional
    public PreparationResultDto preparer(Long id) {
        CommandeClient commande = findById(id);

        if (commande.getStatut() != CommandeClientStatut.EN_ATTENTE) {
            throw new StatutInvalideException(
                    "La commande client " + id + " doit être EN_ATTENTE pour passer en préparation"
            );
        }

        Map<Long, Double> besoinsParIngredient = calculerBesoinsIngredients(commande);

        stockService.verifierStockSuffisant(besoinsParIngredient);
        stockService.consommerStock(besoinsParIngredient);

        commandeClientDao.updateStatut(id, CommandeClientStatut.EN_PREPARATION);

        CommandeClient commandeMiseAJour = findById(id);

        List<Long> ingredientIds = new ArrayList<>(besoinsParIngredient.keySet());
        List<AlerteStockDto> alertes = stockService.findAlertesPourIngredients(ingredientIds);

        return new PreparationResultDto(commandeMiseAJour, alertes);
    }

    @Transactional
    public CommandeClient servir(Long id) {
        CommandeClient commande = findById(id);

        if (commande.getStatut() != CommandeClientStatut.EN_PREPARATION) {
            throw new StatutInvalideException(
                    "La commande client " + id + " doit être EN_PREPARATION pour être servie"
            );
        }

        commandeClientDao.updateStatut(id, CommandeClientStatut.SERVIE);
        return findById(id);
    }

    @Transactional
    public void delete(Long id) {
        CommandeClient commande = commandeClientDao.findById(id);
        if (commande == null) {
            throw new RessourceNonTrouveeException(
                    "Commande client introuvable avec l'id " + id
            );
        }
        commandeClientDao.delete(id);
    }

    private void validerCreationCommande(CreerCommandeClientRequest request) {
        if (request == null) {
            throw new CommandeInvalideException("La requête de création est obligatoire");
        }
        if (request.clientId() == null) {
            throw new CommandeInvalideException("Le clientId est obligatoire");
        }
        if (request.lignes() == null || request.lignes().isEmpty()) {
            throw new CommandeInvalideException("Une commande doit contenir au moins une ligne");
        }

        for (LigneCommandeClientRequest ligne : request.lignes()) {
            if (ligne.platId() == null) {
                throw new CommandeInvalideException("Chaque ligne doit contenir un platId");
            }
            if (ligne.quantite() == null || ligne.quantite() <= 0) {
                throw new CommandeInvalideException("La quantité d'une ligne doit être strictement positive");
            }
        }
    }

    private void enrichirCommande(CommandeClient commande) {
        Client client = clientDao.findById(commande.getClientId());
        commande.setClient(client);

        if (commande.getLignes() == null) {
            return;
        }

        for (LigneCommandeClient ligne : commande.getLignes()) {
            Plat plat = platDao.findById(ligne.getPlatId());
            if (plat != null) {
                plat.setIngredients(platDao.findIngredientsByPlatId(plat.getId()));
            }
            ligne.setPlat(plat);
        }
    }

    private Map<Long, Double> calculerBesoinsIngredients(CommandeClient commande) {
        Map<Long, Double> besoinsParIngredient = new LinkedHashMap<>();

        for (LigneCommandeClient ligne : commande.getLignes()) {
            Plat plat = platDao.findById(ligne.getPlatId());
            if (plat == null) {
                throw new RessourceNonTrouveeException(
                        "Plat introuvable avec l'id " + ligne.getPlatId()
                );
            }

            List<PlatIngredient> ingredientsDuPlat = platDao.findIngredientsByPlatId(plat.getId());

            for (PlatIngredient platIngredient : ingredientsDuPlat) {
                double quantiteTotale = platIngredient.getQuantiteRequise() * ligne.getQuantite();

                besoinsParIngredient.merge(
                        platIngredient.getIngredientId(),
                        quantiteTotale,
                        Double::sum
                );
            }
        }

        return besoinsParIngredient;
    }
}