package fr.epf.restaurant.service;

import fr.epf.restaurant.dao.CommandeFournisseurDao;
import fr.epf.restaurant.dao.FournisseurDao;
import fr.epf.restaurant.dao.IngredientDao;
import fr.epf.restaurant.dto.CreerCommandeFournisseurRequest;
import fr.epf.restaurant.dto.LigneCommandeFournisseurRequest;
import fr.epf.restaurant.exception.CommandeInvalideException;
import fr.epf.restaurant.exception.RessourceNonTrouveeException;
import fr.epf.restaurant.exception.StatutInvalideException;
import fr.epf.restaurant.model.CommandeFournisseur;
import fr.epf.restaurant.model.CommandeFournisseurStatut;
import fr.epf.restaurant.model.Fournisseur;
import fr.epf.restaurant.model.Ingredient;
import fr.epf.restaurant.model.LigneCommandeFournisseur;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommandeFournisseurService {

    private final CommandeFournisseurDao commandeFournisseurDao;
    private final FournisseurDao fournisseurDao;
    private final IngredientDao ingredientDao;
    private final StockService stockService;

    public CommandeFournisseurService(CommandeFournisseurDao commandeFournisseurDao,
                                      FournisseurDao fournisseurDao,
                                      IngredientDao ingredientDao,
                                      StockService stockService) {
        this.commandeFournisseurDao = commandeFournisseurDao;
        this.fournisseurDao = fournisseurDao;
        this.ingredientDao = ingredientDao;
        this.stockService = stockService;
    }

    public List<CommandeFournisseur> findAll() {
        List<CommandeFournisseur> commandes = commandeFournisseurDao.findAll();
        for (CommandeFournisseur commande : commandes) {
            enrichirCommande(commande);
        }
        return commandes;
    }

    public CommandeFournisseur findById(Long id) {
        CommandeFournisseur commande = commandeFournisseurDao.findById(id);
        if (commande == null) {
            throw new RessourceNonTrouveeException(
                    "Commande fournisseur introuvable avec l'id " + id
            );
        }
        enrichirCommande(commande);
        return commande;
    }

    @Transactional
    public CommandeFournisseur creer(CreerCommandeFournisseurRequest request) {
        validerCreationCommande(request);

        Fournisseur fournisseur = fournisseurDao.findById(request.fournisseurId());
        if (fournisseur == null) {
            throw new RessourceNonTrouveeException(
                    "Fournisseur introuvable avec l'id " + request.fournisseurId()
            );
        }

        for (LigneCommandeFournisseurRequest ligne : request.lignes()) {
            Ingredient ingredient = ingredientDao.findById(ligne.ingredientId());
            if (ingredient == null) {
                throw new RessourceNonTrouveeException(
                        "Ingrédient introuvable avec l'id " + ligne.ingredientId()
                );
            }
        }

        Long commandeId = commandeFournisseurDao.insertCommande(request.fournisseurId());

        for (LigneCommandeFournisseurRequest ligne : request.lignes()) {
            commandeFournisseurDao.insertLigne(
                    commandeId,
                    ligne.ingredientId(),
                    ligne.quantite(),
                    ligne.prixUnitaire()
            );
        }

        return findById(commandeId);
    }

    @Transactional
    public CommandeFournisseur envoyer(Long id) {
        CommandeFournisseur commande = findById(id);

        if (commande.getStatut() != CommandeFournisseurStatut.EN_ATTENTE) {
            throw new StatutInvalideException(
                    "La commande fournisseur " + id + " doit être EN_ATTENTE pour être envoyée"
            );
        }

        commandeFournisseurDao.updateStatut(id, CommandeFournisseurStatut.ENVOYEE);
        return findById(id);
    }

    @Transactional
    public CommandeFournisseur recevoir(Long id) {
        CommandeFournisseur commande = findById(id);

        if (commande.getStatut() != CommandeFournisseurStatut.ENVOYEE) {
            throw new StatutInvalideException(
                    "La commande fournisseur " + id + " doit être ENVOYEE pour être reçue"
            );
        }

        for (LigneCommandeFournisseur ligne : commande.getLignes()) {
            stockService.ajouterAuStock(ligne.getIngredientId(), ligne.getQuantiteCommandee());
        }

        commandeFournisseurDao.updateStatut(id, CommandeFournisseurStatut.RECUE);
        return findById(id);
    }

    @Transactional
    public void delete(Long id) {
        CommandeFournisseur commande = commandeFournisseurDao.findById(id);
        if (commande == null) {
            throw new RessourceNonTrouveeException(
                    "Commande fournisseur introuvable avec l'id " + id
            );
        }
        commandeFournisseurDao.delete(id);
    }

    private void validerCreationCommande(CreerCommandeFournisseurRequest request) {
        if (request == null) {
            throw new CommandeInvalideException("La requête de création est obligatoire");
        }
        if (request.fournisseurId() == null) {
            throw new CommandeInvalideException("Le fournisseurId est obligatoire");
        }
        if (request.lignes() == null || request.lignes().isEmpty()) {
            throw new CommandeInvalideException("Une commande fournisseur doit contenir au moins une ligne");
        }

        for (LigneCommandeFournisseurRequest ligne : request.lignes()) {
            if (ligne.ingredientId() == null) {
                throw new CommandeInvalideException("Chaque ligne doit contenir un ingredientId");
            }
            if (ligne.quantite() == null || ligne.quantite() <= 0) {
                throw new CommandeInvalideException("La quantité commandée doit être strictement positive");
            }
            if (ligne.prixUnitaire() == null || ligne.prixUnitaire().signum() < 0) {
                throw new CommandeInvalideException("Le prix unitaire doit être positif ou nul");
            }
        }
    }

    private void enrichirCommande(CommandeFournisseur commande) {
        Fournisseur fournisseur = fournisseurDao.findById(commande.getFournisseurId());
        commande.setFournisseur(fournisseur);

        if (commande.getLignes() == null) {
            return;
        }

        for (LigneCommandeFournisseur ligne : commande.getLignes()) {
            Ingredient ingredient = ingredientDao.findById(ligne.getIngredientId());
            ligne.setIngredient(ingredient);
        }
    }
}