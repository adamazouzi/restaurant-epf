package fr.epf.restaurant.service;

import fr.epf.restaurant.TestConfig;
import fr.epf.restaurant.dto.AlerteStockDto;
import fr.epf.restaurant.dto.RecommandationDto;
import fr.epf.restaurant.exception.RessourceNonTrouveeException;
import fr.epf.restaurant.model.Ingredient;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class StockServiceTest {

    @Test
    void findAllIngredients_retourneDesIngredients() {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(TestConfig.class)) {

            StockService service = context.getBean(StockService.class);

            List<Ingredient> ingredients = service.findAllIngredients();

            assertNotNull(ingredients);
            assertFalse(ingredients.isEmpty());
        }
    }

    @Test
    void findIngredientById_retourneIngredientExistant() {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(TestConfig.class)) {

            StockService service = context.getBean(StockService.class);

            Ingredient ingredient = service.findIngredientById(1L);

            assertNotNull(ingredient);
            assertEquals(1L, ingredient.getId());
        }
    }

    @Test
    void findIngredientById_leveExceptionSiInexistant() {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(TestConfig.class)) {

            StockService service = context.getBean(StockService.class);

            assertThrows(RessourceNonTrouveeException.class,
                    () -> service.findIngredientById(999L));
        }
    }

    @Test
    void getRecommandation_retourneUneRecommandation() {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(TestConfig.class)) {

            StockService service = context.getBean(StockService.class);

            RecommandationDto recommandation = service.getRecommandation(1L);

            assertNotNull(recommandation);
            assertEquals(1L, recommandation.ingredientId());
            assertNotNull(recommandation.fournisseurNom());
        }
    }

    @Test
    void findIngredientsSousAlerte_retourneUneListeNonNulle() {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(TestConfig.class)) {

            StockService service = context.getBean(StockService.class);

            List<AlerteStockDto> alertes = service.findIngredientsSousAlerte();

            assertNotNull(alertes);
        }
    }
}