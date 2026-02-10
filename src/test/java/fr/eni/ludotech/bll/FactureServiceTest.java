package fr.eni.ludotech.bll;

import fr.eni.ludotech.bo.*;
import fr.eni.ludotech.dal.*;
import fr.eni.ludotech.rest.dto.FactureDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class FactureServiceTest {

    @Autowired
    private FactureService factureService;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private JeuRepository jeuRepository;

    @Autowired
    private ExemplaireRepository exemplaireRepository;

    @Autowired
    private LocationRepository locationRepository;

    @Test
    public void testEnregistrerRetourEtGenererFacture_UnExemplaire() {
        // Arrange
        Client client = creerClient("Dupont", "Jean");
        Jeu jeu = creerJeu("Monopoly", BigDecimal.valueOf(5.0));
        Exemplaire exemplaire = creerExemplaire("123456789", jeu);

        LocalDate dateDebut = LocalDate.now().minusDays(3);
        LocalDate dateFin = LocalDate.now().plusDays(4);
        Location location = creerLocation(client, exemplaire, dateDebut, dateFin);

        // Act
        FactureDTO facture = factureService.enregistrerRetourEtGenererFacture(Arrays.asList("123456789"));

        // Assert
        assertNotNull(facture);
        assertNotNull(facture.getId());
        assertEquals(client.getId(), facture.getClientId());
        assertEquals("Dupont", facture.getClientNom());
        assertEquals("Jean", facture.getClientPrenom());
        assertEquals(LocalDate.now(), facture.getDateFacture());

        assertEquals(1, facture.getLignes().size());
        assertEquals("Monopoly", facture.getLignes().get(0).getJeuTitre());
        assertEquals("123456789", facture.getLignes().get(0).getCodeBarre());
        assertEquals(4, facture.getLignes().get(0).getNombreJours()); // 4 jours (aujourd'hui inclus)
        assertEquals(BigDecimal.valueOf(5.0), facture.getLignes().get(0).getTarifJournalier());
        assertEquals(BigDecimal.valueOf(20.0), facture.getLignes().get(0).getMontant());
        assertEquals(BigDecimal.valueOf(20.0), facture.getMontantTotal());

        // Vérifier que l'exemplaire est maintenant disponible
        Exemplaire exemplaireApres = exemplaireRepository.findById(exemplaire.getId()).get();
        assertFalse(exemplaireApres.isLoue());

        // Vérifier que la location a une date de retour
        Location locationApres = locationRepository.findById(location.getId()).get();
        assertEquals(LocalDate.now(), locationApres.getDateRetourEffectif());
    }

    @Test
    public void testEnregistrerRetourEtGenererFacture_PlusieurExemplaires() {
        // Arrange
        Client client = creerClient("Martin", "Sophie");
        Jeu jeu1 = creerJeu("Catan", BigDecimal.valueOf(4.0));
        Jeu jeu2 = creerJeu("7 Wonders", BigDecimal.valueOf(3.0));

        Exemplaire exemplaire1 = creerExemplaire("111111111", jeu1);
        Exemplaire exemplaire2 = creerExemplaire("222222222", jeu2);

        LocalDate dateDebut = LocalDate.now().minusDays(2);
        LocalDate dateFin = LocalDate.now().plusDays(5);

        creerLocation(client, exemplaire1, dateDebut, dateFin);
        creerLocation(client, exemplaire2, dateDebut, dateFin);

        // Act
        FactureDTO facture = factureService.enregistrerRetourEtGenererFacture(
            Arrays.asList("111111111", "222222222")
        );

        // Assert
        assertNotNull(facture);
        assertEquals(client.getId(), facture.getClientId());
        assertEquals(2, facture.getLignes().size());

        // Jeu 1: 3 jours * 4€ = 12€
        // Jeu 2: 3 jours * 3€ = 9€
        // Total: 21€
        assertEquals(BigDecimal.valueOf(21.0), facture.getMontantTotal());

        // Vérifier que les deux exemplaires sont disponibles
        assertFalse(exemplaireRepository.findById(exemplaire1.getId()).get().isLoue());
        assertFalse(exemplaireRepository.findById(exemplaire2.getId()).get().isLoue());
    }

    @Test
    public void testEnregistrerRetourEtGenererFacture_CodeBarreInexistant() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            factureService.enregistrerRetourEtGenererFacture(Arrays.asList("CODE_INEXISTANT"));
        });
    }

    @Test
    public void testEnregistrerRetourEtGenererFacture_ClientsDifferents() {
        // Arrange
        Client client1 = creerClient("Client1", "Test1");
        Client client2 = creerClient("Client2", "Test2");

        Jeu jeu = creerJeu("Jeu Test", BigDecimal.valueOf(5.0));

        Exemplaire exemplaire1 = creerExemplaire("333333333", jeu);
        Exemplaire exemplaire2 = creerExemplaire("444444444", jeu);

        LocalDate dateDebut = LocalDate.now().minusDays(1);
        LocalDate dateFin = LocalDate.now().plusDays(6);

        creerLocation(client1, exemplaire1, dateDebut, dateFin);
        creerLocation(client2, exemplaire2, dateDebut, dateFin);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            factureService.enregistrerRetourEtGenererFacture(Arrays.asList("333333333", "444444444"));
        });
    }

    @Test
    public void testEnregistrerRetourEtGenererFacture_ListeVide() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            factureService.enregistrerRetourEtGenererFacture(Arrays.asList());
        });
    }

    // Méthodes utilitaires
    private Client creerClient(String nom, String prenom) {
        Client client = new Client();
        client.setNom(nom);
        client.setPrenom(prenom);
        client.setEmail(nom.toLowerCase() + "@test.com");
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
        exemplaire.setLoue(true); // En cours de location
        return exemplaireRepository.save(exemplaire);
    }

    private Location creerLocation(Client client, Exemplaire exemplaire, LocalDate dateDebut, LocalDate dateFin) {
        Location location = new Location();
        location.setClient(client);
        location.setExemplaire(exemplaire);
        location.setDateDebut(dateDebut);
        location.setDateFin(dateFin);
        return locationRepository.save(location);
    }
}
