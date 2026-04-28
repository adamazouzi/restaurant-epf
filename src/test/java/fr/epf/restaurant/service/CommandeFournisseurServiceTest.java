package fr.epf.restaurant.service;

import fr.epf.restaurant.TestConfig;
import fr.epf.restaurant.dto.CreerCommandeFournisseurRequest;
import fr.epf.restaurant.dto.LigneCommandeFournisseurRequest;
import fr.epf.restaurant.exception.RessourceNonTrouveeException;
import fr.epf.restaurant.model.CommandeFournisseur;
import fr.epf.restaurant.model.CommandeFournisseurStatut;
import fr.epf.restaurant.model.Ingredient;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CommandeFournisseurServiceTest {

    @Test
    void creerCommandeFournisseur_valide() {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(TestConfig.class)) {

            CommandeFournisseurService service = context.getBean(CommandeFournisseurService.class);

            CreerCommandeFournisseurRequest request = new CreerCommandeFournisseurRequest(
                    1L,
                    List.of(new LigneCommandeFournisseurRequest(
                            1L,
                            10.0,
                            new BigDecimal("0.50")
                    ))
            );

            CommandeFournisseur commande = service.creer(request);

            assertNotNull(commande);
            assertNotNull(commande.getId());
            assertEquals(CommandeFournisseurStatut.EN_ATTENTE, commande.getStatut());
        }
    }

    @Test
    void creerCommandeFournisseur_fournisseurInexistant_leveException() {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(TestConfig.class)) {

            CommandeFournisseurService service = context.getBean(CommandeFournisseurService.class);

            CreerCommandeFournisseurRequest request = new CreerCommandeFournisseurRequest(
                    999L,
                    List.of(new LigneCommandeFournisseurRequest(
                            1L,
                            10.0,
                            new BigDecimal("0.50")
                    ))
            );

            assertThrows(RessourceNonTrouveeException.class,
                    () -> service.creer(request));
        }
    }

    @Test
    void recevoirCommande_metAJourLeStock() {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(TestConfig.class)) {

            CommandeFournisseurService commandeService =
                    context.getBean(CommandeFournisseurService.class);
            StockService stockService = context.getBean(StockService.class);

            Ingredient avant = stockService.findIngredientById(1L);

            CommandeFournisseur commande = commandeService.creer(
                    new CreerCommandeFournisseurRequest(
                            1L,
                            List.of(new LigneCommandeFournisseurRequest(
                                    1L,
                                    10.0,
                                    new BigDecimal("0.50")
                            ))
                    )
            );

            commandeService.envoyer(commande.getId());
            CommandeFournisseur recue = commandeService.recevoir(commande.getId());

            Ingredient apres = stockService.findIngredientById(1L);

            assertEquals(CommandeFournisseurStatut.RECUE, recue.getStatut());
            assertEquals(avant.getStockActuel() + 10.0, apres.getStockActuel());
        }
    }
}