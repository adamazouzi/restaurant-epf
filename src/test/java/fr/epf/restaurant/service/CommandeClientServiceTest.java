package fr.epf.restaurant.service;

import fr.epf.restaurant.TestConfig;
import fr.epf.restaurant.dto.CreerCommandeClientRequest;
import fr.epf.restaurant.dto.LigneCommandeClientRequest;
import fr.epf.restaurant.dto.PreparationResultDto;
import fr.epf.restaurant.exception.RessourceNonTrouveeException;
import fr.epf.restaurant.exception.StatutInvalideException;
import fr.epf.restaurant.model.CommandeClient;
import fr.epf.restaurant.model.CommandeClientStatut;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CommandeClientServiceTest {

    @Test
    void creerCommandeClient_valide() {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(TestConfig.class)) {

            CommandeClientService service = context.getBean(CommandeClientService.class);

            CreerCommandeClientRequest request = new CreerCommandeClientRequest(
                    1L,
                    List.of(new LigneCommandeClientRequest(1L, 1))
            );

            CommandeClient commande = service.creer(request);

            assertNotNull(commande);
            assertNotNull(commande.getId());
            assertEquals(CommandeClientStatut.EN_ATTENTE, commande.getStatut());
            assertEquals(1L, commande.getClientId());
        }
    }

    @Test
    void creerCommandeClient_clientInexistant_leveException() {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(TestConfig.class)) {

            CommandeClientService service = context.getBean(CommandeClientService.class);

            CreerCommandeClientRequest request = new CreerCommandeClientRequest(
                    999L,
                    List.of(new LigneCommandeClientRequest(1L, 1))
            );

            assertThrows(RessourceNonTrouveeException.class,
                    () -> service.creer(request));
        }
    }

    @Test
    void preparerCommande_valide() {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(TestConfig.class)) {

            CommandeClientService service = context.getBean(CommandeClientService.class);

            CommandeClient commande = service.creer(
                    new CreerCommandeClientRequest(
                            1L,
                            List.of(new LigneCommandeClientRequest(1L, 1))
                    )
            );

            PreparationResultDto resultat = service.preparer(commande.getId());

            assertNotNull(resultat);
            assertNotNull(resultat.commande());
            assertEquals(CommandeClientStatut.EN_PREPARATION, resultat.commande().getStatut());
        }
    }

    @Test
    void servirCommande_valide() {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(TestConfig.class)) {

            CommandeClientService service = context.getBean(CommandeClientService.class);

            CommandeClient commande = service.creer(
                    new CreerCommandeClientRequest(
                            1L,
                            List.of(new LigneCommandeClientRequest(1L, 1))
                    )
            );

            service.preparer(commande.getId());
            CommandeClient servie = service.servir(commande.getId());

            assertEquals(CommandeClientStatut.SERVIE, servie.getStatut());
        }
    }

    @Test
    void servirCommande_mauvaisStatut_leveException() {
        try (AnnotationConfigApplicationContext context =
                     new AnnotationConfigApplicationContext(TestConfig.class)) {

            CommandeClientService service = context.getBean(CommandeClientService.class);

            CommandeClient commande = service.creer(
                    new CreerCommandeClientRequest(
                            1L,
                            List.of(new LigneCommandeClientRequest(1L, 1))
                    )
            );

            assertThrows(StatutInvalideException.class,
                    () -> service.servir(commande.getId()));
        }
    }
}