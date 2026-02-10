package fr.eni.ludotech.bll;

import fr.eni.ludotech.bo.Client;
import fr.eni.ludotech.bo.Exemplaire;
import fr.eni.ludotech.bo.Jeu;
import fr.eni.ludotech.bo.Location;
import fr.eni.ludotech.dal.*;
import fr.eni.ludotech.rest.dto.RelanceDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class RelanceServiceTest {

    @Autowired
    private RelanceService relanceService;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private JeuRepository jeuRepository;

    @Autowired
    private ExemplaireRepository exemplaireRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private RelanceRepository relanceRepository;

    @Test
    public void testRelancerClient_AvecLocationsEnRetard() {
        // Arrange
        Client client = creerClient("Retardataire", "Jean");
        Jeu jeu = creerJeu("Monopoly", BigDecimal.valueOf(5.0));
        Exemplaire exemplaire = creerExemplaire("111111111", jeu);

        // Créer une location en retard (date de fin dans le passé)
        LocalDate dateDebut = LocalDate.now().minusDays(10);
        LocalDate dateFin = LocalDate.now().minusDays(3); // 3 jours de retard
        Location location = creerLocation(client, exemplaire, dateDebut, dateFin);

        // Act
        RelanceDTO relance = relanceService.relancerClient(client.getId(), "EMAIL");

        // Assert
        assertNotNull(relance);
        assertEquals(client.getId(), relance.getClientId());
        assertEquals("Retardataire", relance.getClientNom());
        assertEquals("Jean", relance.getClientPrenom());
        assertEquals("EMAIL", relance.getTypeRelance());

        assertEquals(1, relance.getLocationsEnRetard().size());
        assertEquals("Monopoly", relance.getLocationsEnRetard().get(0).getJeuTitre());
        assertEquals(3, relance.getLocationsEnRetard().get(0).getJoursDeRetard());

        assertNotNull(relance.getMessage());
        assertTrue(relance.getMessage().contains("Monopoly"));
        assertTrue(relance.getMessage().contains("3 jour(s) de retard"));

        // Vérifier qu'une relance a été enregistrée
        List<fr.eni.ludotech.bo.Relance> relances = relanceRepository.findAll();
        assertEquals(1, relances.size());
        assertEquals("ENVOYEE", relances.get(0).getStatut());
    }

    @Test
    public void testRelancerClient_SansLocationEnRetard() {
        // Arrange
        Client client = creerClient("Ponctuel", "Marie");
        Jeu jeu = creerJeu("Catan", BigDecimal.valueOf(4.0));
        Exemplaire exemplaire = creerExemplaire("222222222", jeu);

        // Créer une location non en retard (date de fin dans le futur)
        LocalDate dateDebut = LocalDate.now().minusDays(2);
        LocalDate dateFin = LocalDate.now().plusDays(5); // Pas encore en retard
        creerLocation(client, exemplaire, dateDebut, dateFin);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            relanceService.relancerClient(client.getId(), "EMAIL");
        });
    }

    @Test
    public void testRelancerClient_ClientInexistant() {
        // Act & Assert
        assertThrows(ClientNotFoundException.class, () -> {
            relanceService.relancerClient(99999, "EMAIL");
        });
    }

    @Test
    public void testRelancerTousLesClientsEnRetard() {
        // Arrange
        Client client1 = creerClient("Retardataire1", "Paul");
        Client client2 = creerClient("Retardataire2", "Sophie");
        Client client3 = creerClient("Ponctuel", "Marc");

        Jeu jeu = creerJeu("7 Wonders", BigDecimal.valueOf(3.0));

        Exemplaire exemplaire1 = creerExemplaire("333333333", jeu);
        Exemplaire exemplaire2 = creerExemplaire("444444444", jeu);
        Exemplaire exemplaire3 = creerExemplaire("555555555", jeu);

        // Client 1 et 2 en retard
        LocalDate dateDebut = LocalDate.now().minusDays(8);
        LocalDate dateFinRetard = LocalDate.now().minusDays(2);
        creerLocation(client1, exemplaire1, dateDebut, dateFinRetard);
        creerLocation(client2, exemplaire2, dateDebut, dateFinRetard);

        // Client 3 pas en retard
        LocalDate dateFinFutur = LocalDate.now().plusDays(5);
        creerLocation(client3, exemplaire3, dateDebut, dateFinFutur);

        // Act
        List<RelanceDTO> relances = relanceService.relancerTousLesClientsEnRetard("SMS");

        // Assert
        assertNotNull(relances);
        assertEquals(2, relances.size()); // Seulement les 2 clients en retard

        assertTrue(relances.stream().anyMatch(r -> r.getClientNom().equals("Retardataire1")));
        assertTrue(relances.stream().anyMatch(r -> r.getClientNom().equals("Retardataire2")));

        relances.forEach(r -> assertEquals("SMS", r.getTypeRelance()));
    }

    @Test
    public void testRelancerClient_PlusieurLocationsEnRetard() {
        // Arrange
        Client client = creerClient("MultiRetard", "Alice");
        Jeu jeu1 = creerJeu("Risk", BigDecimal.valueOf(6.0));
        Jeu jeu2 = creerJeu("Scrabble", BigDecimal.valueOf(2.0));

        Exemplaire exemplaire1 = creerExemplaire("666666666", jeu1);
        Exemplaire exemplaire2 = creerExemplaire("777777777", jeu2);

        // Deux locations en retard
        LocalDate dateDebut1 = LocalDate.now().minusDays(12);
        LocalDate dateFin1 = LocalDate.now().minusDays(5);
        creerLocation(client, exemplaire1, dateDebut1, dateFin1);

        LocalDate dateDebut2 = LocalDate.now().minusDays(7);
        LocalDate dateFin2 = LocalDate.now().minusDays(1);
        creerLocation(client, exemplaire2, dateDebut2, dateFin2);

        // Act
        RelanceDTO relance = relanceService.relancerClient(client.getId(), "COURRIER");

        // Assert
        assertNotNull(relance);
        assertEquals(2, relance.getLocationsEnRetard().size());
        assertEquals("COURRIER", relance.getTypeRelance());

        // Vérifier que le message contient les deux jeux
        assertTrue(relance.getMessage().contains("Risk"));
        assertTrue(relance.getMessage().contains("Scrabble"));
    }

    // Méthodes utilitaires
    private Client creerClient(String nom, String prenom) {
        Client client = new Client();
        client.setNom(nom);
        client.setPrenom(prenom);
        client.setEmail(nom.toLowerCase() + "@test.com");
        client.setTelephone("0612345678");
        return clientRepository.save(client);
    }

    private Jeu creerJeu(String titre, BigDecimal tarif) {
        Jeu jeu = new Jeu();
        jeu.setTitre(titre);
        jeu.setTarifJournalier(tarif);
        return jeuRepository.save(jeu);
    }

    private Exemplaire creerExemplaire(String codeBarre, Jeu jeu) {
        Exemplaire exemplaire = new Exemplaire();
        exemplaire.setCodeBarre(codeBarre);
        exemplaire.setReference("REF" + codeBarre);
        exemplaire.setJeu(jeu);
        exemplaire.setLouable(true);
        exemplaire.setLoue(true);
        return exemplaireRepository.save(exemplaire);
    }

    private Location creerLocation(Client client, Exemplaire exemplaire, LocalDate dateDebut, LocalDate dateFin) {
        Location location = new Location();
        location.setClient(client);
        location.setExemplaire(exemplaire);
        location.setDateDebut(dateDebut);
        location.setDateFin(dateFin);
        location.setDateRetourEffectif(null); // Pas encore retourné
        return locationRepository.save(location);
    }
}
