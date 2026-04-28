package fr.epf.restaurant.service;

import fr.epf.restaurant.dao.IngredientDao;
import fr.epf.restaurant.dto.AlerteStockDto;
import fr.epf.restaurant.dto.IngredientPrixDto;
import fr.epf.restaurant.dto.RecommandationDto;
import fr.epf.restaurant.exception.RessourceNonTrouveeException;
import fr.epf.restaurant.exception.StockInsuffisantException;
import fr.epf.restaurant.model.FournisseurIngredientPrix;
import fr.epf.restaurant.model.Ingredient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class StockService {

    private final IngredientDao ingredientDao;

    public StockService(IngredientDao ingredientDao) {
        this.ingredientDao = ingredientDao;
    }

    public List<Ingredient> findAllIngredients() {
        return ingredientDao.findAll();
    }

    public Ingredient findIngredientById(Long ingredientId) {
        Ingredient ingredient = ingredientDao.findById(ingredientId);
        if (ingredient == null) {
            throw new RessourceNonTrouveeException(
                    "Ingrédient introuvable avec l'id " + ingredientId
            );
        }
        return ingredient;
    }

    public List<AlerteStockDto> findIngredientsSousAlerte() {
        List<Ingredient> ingredients = ingredientDao.findSousAlerte();
        List<AlerteStockDto> alertes = new ArrayList<>();

        for (Ingredient ingredient : ingredients) {
            alertes.add(toAlerteDto(ingredient));
        }

        return alertes;
    }

    public List<IngredientPrixDto> findPrixByIngredientId(Long ingredientId) {
        findIngredientById(ingredientId);
        return ingredientDao.findPrixByIngredientId(ingredientId);
    }

    public RecommandationDto getRecommandation(Long ingredientId) {
        Ingredient ingredient = findIngredientById(ingredientId);

        FournisseurIngredientPrix meilleureOffre =
                ingredientDao.findMeilleureOffrePourIngredient(ingredientId);

        if (meilleureOffre == null) {
            throw new RessourceNonTrouveeException(
                    "Aucune offre fournisseur trouvée pour l'ingrédient " + ingredientId
            );
        }

        return new RecommandationDto(
                ingredient.getId(),
                ingredient.getNom(),
                meilleureOffre.getFournisseurId(),
                meilleureOffre.getFournisseurNom(),
                meilleureOffre.getPrixUnitaire(),
                calculerQuantiteRecommandee(ingredient)
        );
    }

    public void verifierStockSuffisant(Map<Long, Double> besoinsParIngredient) {
        for (Map.Entry<Long, Double> entry : besoinsParIngredient.entrySet()) {
            Long ingredientId = entry.getKey();
            double quantiteNecessaire = entry.getValue();

            Ingredient ingredient = findIngredientById(ingredientId);

            if (ingredient.getStockActuel() < quantiteNecessaire) {
                throw new StockInsuffisantException(
                        "Stock insuffisant pour l'ingrédient '"
                                + ingredient.getNom()
                                + "' : stock actuel="
                                + ingredient.getStockActuel()
                                + ", requis="
                                + quantiteNecessaire
                );
            }
        }
    }

    public void consommerStock(Map<Long, Double> besoinsParIngredient) {
        for (Map.Entry<Long, Double> entry : besoinsParIngredient.entrySet()) {
            Long ingredientId = entry.getKey();
            double quantiteNecessaire = entry.getValue();

            Ingredient ingredient = findIngredientById(ingredientId);
            double nouveauStock = ingredient.getStockActuel() - quantiteNecessaire;
            ingredientDao.updateStock(ingredientId, nouveauStock);
        }
    }

    public void ajouterAuStock(Long ingredientId, double quantiteAjoutee) {
        Ingredient ingredient = findIngredientById(ingredientId);
        double nouveauStock = ingredient.getStockActuel() + quantiteAjoutee;
        ingredientDao.updateStock(ingredientId, nouveauStock);
    }

    public List<AlerteStockDto> findAlertesPourIngredients(List<Long> ingredientIds) {
        List<AlerteStockDto> alertes = new ArrayList<>();

        for (Long ingredientId : ingredientIds) {
            Ingredient ingredient = findIngredientById(ingredientId);
            if (ingredient.getStockActuel() < ingredient.getSeuilAlerte()) {
                alertes.add(toAlerteDto(ingredient));
            }
        }

        return alertes;
    }

    private AlerteStockDto toAlerteDto(Ingredient ingredient) {
        return new AlerteStockDto(
                ingredient.getId(),
                ingredient.getNom(),
                ingredient.getUnite(),
                ingredient.getStockActuel(),
                ingredient.getSeuilAlerte(),
                calculerQuantiteRecommandee(ingredient)
        );
    }

    private double calculerQuantiteRecommandee(Ingredient ingredient) {
        if (ingredient.getSeuilAlerte() > ingredient.getStockActuel()) {
            return 2 * (ingredient.getSeuilAlerte() - ingredient.getStockActuel());
        }
        return ingredient.getSeuilAlerte();
    }
}