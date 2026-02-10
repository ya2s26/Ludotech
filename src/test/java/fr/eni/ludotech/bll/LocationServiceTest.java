package fr.eni.ludotech.bll;

import fr.eni.ludotech.bo.Client;
import fr.eni.ludotech.bo.Exemplaire;
import fr.eni.ludotech.bo.Jeu;
import fr.eni.ludotech.bo.Location;
import fr.eni.ludotech.dal.ClientRepository;
import fr.eni.ludotech.dal.ExemplaireRepository;
import fr.eni.ludotech.dal.JeuRepository;
import fr.eni.ludotech.dal.LocationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class LocationServiceTest {

    @Autowired
    private LocationService locationService;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ExemplaireRepository exemplaireRepository;

    @Autowired
    private JeuRepository jeuRepository;

    @Test
    public void testCreerLocation_CasPositif() {
        // Arrange - Créer un client
        Client client = new Client();
        client.setNom("Dupont");
        client.setPrenom("Jean");
        client.setEmail("jean.dupont@test.com");
        client = clientRepository.save(client);

        // Créer un jeu
        Jeu jeu = new Jeu();
        jeu.setTitre("Monopoly");
        jeu = jeuRepository.save(jeu);

        // Créer un exemplaire disponible
        Exemplaire exemplaire = new Exemplaire();
        exemplaire.setReference("REF001");
        exemplaire.setCodeBarre("123456789");
        exemplaire.setJeu(jeu);
        exemplaire.setLouable(true);
        exemplaire.setLoue(false);
        exemplaireRepository.save(exemplaire);

        LocalDate dateDebut = LocalDate.now();
        LocalDate dateFin = LocalDate.now().plusDays(7);

        // Act
        Location location = locationService.creerLocation(client.getId(), "123456789", dateDebut, dateFin);

        // Assert
        assertNotNull(location);
        assertNotNull(location.getId());
        assertEquals(client.getId(), location.getClient().getId());
        assertEquals("123456789", location.getExemplaire().getCodeBarre());
        assertEquals(dateDebut, location.getDateDebut());
        assertEquals(dateFin, location.getDateFin());
        assertNull(location.getDateRetourEffectif());

        // Vérifier que l'exemplaire est maintenant loué
        Exemplaire exemplaireApres = exemplaireRepository.findById(exemplaire.getId()).get();
        assertTrue(exemplaireApres.isLoue());
    }

    @Test
    public void testCreerLocation_ExemplaireDejaloue() {
        // Arrange - Créer un client
        Client client = new Client();
        client.setNom("Dupont");
        client.setPrenom("Jean");
        client.setEmail("jean.dupont@test.com");
        client = clientRepository.save(client);

        // Créer un jeu
        Jeu jeu = new Jeu();
        jeu.setTitre("Monopoly");
        jeu = jeuRepository.save(jeu);

        // Créer un exemplaire déjà loué
        Exemplaire exemplaire = new Exemplaire();
        exemplaire.setReference("REF002");
        exemplaire.setCodeBarre("987654321");
        exemplaire.setJeu(jeu);
        exemplaire.setLouable(true);
        exemplaire.setLoue(true); // Déjà loué
        exemplaireRepository.save(exemplaire);

        LocalDate dateDebut = LocalDate.now();
        LocalDate dateFin = LocalDate.now().plusDays(7);

        final Integer clientId = client.getId();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            locationService.creerLocation(clientId, "987654321", dateDebut, dateFin);
        });
    }

    @Test
    public void testCreerLocation_ExemplairePasLouable() {
        // Arrange - Créer un client
        Client client = new Client();
        client.setNom("Dupont");
        client.setPrenom("Jean");
        client.setEmail("jean.dupont@test.com");
        client = clientRepository.save(client);

        // Créer un jeu
        Jeu jeu = new Jeu();
        jeu.setTitre("Monopoly");
        jeu = jeuRepository.save(jeu);

        // Créer un exemplaire non louable
        Exemplaire exemplaire = new Exemplaire();
        exemplaire.setReference("REF003");
        exemplaire.setCodeBarre("111222333");
        exemplaire.setJeu(jeu);
        exemplaire.setLouable(false); // Non louable
        exemplaire.setLoue(false);
        exemplaireRepository.save(exemplaire);

        LocalDate dateDebut = LocalDate.now();
        LocalDate dateFin = LocalDate.now().plusDays(7);

        final Integer clientId = client.getId();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            locationService.creerLocation(clientId, "111222333", dateDebut, dateFin);
        });
    }

    @Test
    public void testCreerLocation_CodeBarreInexistant() {
        // Arrange - Créer un client
        Client client = new Client();
        client.setNom("Dupont");
        client.setPrenom("Jean");
        client.setEmail("jean.dupont@test.com");
        client = clientRepository.save(client);

        LocalDate dateDebut = LocalDate.now();
        LocalDate dateFin = LocalDate.now().plusDays(7);

        final Integer clientId = client.getId();

        // Act & Assert
        assertThrows(ExemplaireNotFoundException.class, () -> {
            locationService.creerLocation(clientId, "CODE_INEXISTANT", dateDebut, dateFin);
        });
    }

    @Test
    public void testCreerLocation_ClientInexistant() {
        LocalDate dateDebut = LocalDate.now();
        LocalDate dateFin = LocalDate.now().plusDays(7);

        // Act & Assert
        assertThrows(ClientNotFoundException.class, () -> {
            locationService.creerLocation(99999, "123456789", dateDebut, dateFin);
        });
    }

    @Test
    public void testCreerLocation_DateFinAvantDateDebut() {
        // Arrange - Créer un client
        Client client = new Client();
        client.setNom("Dupont");
        client.setPrenom("Jean");
        client.setEmail("jean.dupont@test.com");
        client = clientRepository.save(client);

        LocalDate dateDebut = LocalDate.now();
        LocalDate dateFin = LocalDate.now().minusDays(1); // Date de fin avant date de début

        final Integer clientId = client.getId();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            locationService.creerLocation(clientId, "123456789", dateDebut, dateFin);
        });
    }
}
