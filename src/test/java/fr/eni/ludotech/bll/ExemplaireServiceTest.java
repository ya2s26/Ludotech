package fr.eni.ludotech.bll;

import fr.eni.ludotech.bo.Exemplaire;
import fr.eni.ludotech.bo.Jeu;
import fr.eni.ludotech.dal.ExemplaireRepository;
import fr.eni.ludotech.dal.JeuRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class ExemplaireServiceTest {

    @Autowired
    private ExemplaireService exemplaireService;

    @Autowired
    private ExemplaireRepository exemplaireRepository;

    @Autowired
    private JeuRepository jeuRepository;

    @Test
    public void testFindByCodeBarre_CasPositif() {
        // Arrange - Créer un jeu et un exemplaire
        Jeu jeu = new Jeu();
        jeu.setTitre("Monopoly");
        jeu = jeuRepository.save(jeu);

        Exemplaire exemplaire = new Exemplaire();
        exemplaire.setReference("REF001");
        exemplaire.setCodeBarre("123456789");
        exemplaire.setJeu(jeu);
        exemplaire.setLouable(true);
        exemplaire.setLoue(false);
        exemplaireRepository.save(exemplaire);

        // Act
        Exemplaire result = exemplaireService.findByCodeBarre("123456789");

        // Assert
        assertNotNull(result);
        assertEquals("123456789", result.getCodeBarre());
        assertEquals("REF001", result.getReference());
        assertEquals("Monopoly", result.getJeu().getTitre());
        assertTrue(result.isLouable());
        assertFalse(result.isLoue());
    }

    @Test
    public void testFindByCodeBarre_CasNegatif() {
        // Act & Assert
        assertThrows(ExemplaireNotFoundException.class, () -> {
            exemplaireService.findByCodeBarre("CODE_INEXISTANT");
        });
    }

    @Test
    public void testFindByCodeBarre_CodeBarreNull() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            exemplaireService.findByCodeBarre(null);
        });
    }

    @Test
    public void testFindByCodeBarre_CodeBarreVide() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            exemplaireService.findByCodeBarre("   ");
        });
    }

    @Test
    public void testAjouterExemplaire_CasPositif() {
        // Arrange - Créer un jeu
        Jeu jeu = new Jeu();
        jeu.setTitre("Catan");
        jeu.setTarifJournalier(BigDecimal.valueOf(4.0));
        jeu = jeuRepository.save(jeu);

        // Act
        Exemplaire exemplaire = exemplaireService.ajouterExemplaire(
            jeu.getId(),
            "REF-CATAN-001",
            "BAR-CATAN-001",
            true
        );

        // Assert
        assertNotNull(exemplaire);
        assertNotNull(exemplaire.getId());
        assertEquals(jeu.getId(), exemplaire.getJeu().getId());
        assertEquals("Catan", exemplaire.getJeu().getTitre());
        assertEquals("REF-CATAN-001", exemplaire.getReference());
        assertEquals("BAR-CATAN-001", exemplaire.getCodeBarre());
        assertTrue(exemplaire.isLouable());
        assertFalse(exemplaire.isLoue());
    }

    @Test
    public void testAjouterExemplaire_ExemplaireNonLouable() {
        // Arrange
        Jeu jeu = new Jeu();
        jeu.setTitre("Jeu Endommagé");
        jeu.setTarifJournalier(BigDecimal.valueOf(3.0));
        jeu = jeuRepository.save(jeu);

        // Act
        Exemplaire exemplaire = exemplaireService.ajouterExemplaire(
            jeu.getId(),
            "REF-DAMAGED-001",
            "BAR-DAMAGED-001",
            false
        );

        // Assert
        assertNotNull(exemplaire);
        assertFalse(exemplaire.isLouable());
        assertFalse(exemplaire.isLoue());
    }

    @Test
    public void testAjouterExemplaire_JeuIdNull() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            exemplaireService.ajouterExemplaire(null, "REF001", "BAR001", true);
        });
    }

    @Test
    public void testAjouterExemplaire_JeuInexistant() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            exemplaireService.ajouterExemplaire(99999, "REF001", "BAR001", true);
        });
    }

    @Test
    public void testAjouterExemplaire_ReferenceNull() {
        // Arrange
        Jeu jeu = new Jeu();
        jeu.setTitre("Test");
        jeu = jeuRepository.save(jeu);

        final Integer jeuId = jeu.getId();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            exemplaireService.ajouterExemplaire(jeuId, null, "BAR001", true);
        });
    }

    @Test
    public void testAjouterExemplaire_ReferenceVide() {
        // Arrange
        Jeu jeu = new Jeu();
        jeu.setTitre("Test");
        jeu = jeuRepository.save(jeu);

        final Integer jeuId = jeu.getId();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            exemplaireService.ajouterExemplaire(jeuId, "   ", "BAR001", true);
        });
    }

    @Test
    public void testAjouterExemplaire_CodeBarreNull() {
        // Arrange
        Jeu jeu = new Jeu();
        jeu.setTitre("Test");
        jeu = jeuRepository.save(jeu);

        final Integer jeuId = jeu.getId();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            exemplaireService.ajouterExemplaire(jeuId, "REF001", null, true);
        });
    }

    @Test
    public void testAjouterExemplaire_CodeBarreVide() {
        // Arrange
        Jeu jeu = new Jeu();
        jeu.setTitre("Test");
        jeu = jeuRepository.save(jeu);

        final Integer jeuId = jeu.getId();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            exemplaireService.ajouterExemplaire(jeuId, "REF001", "   ", true);
        });
    }

    @Test
    public void testAjouterExemplaire_CodeBarreDuplique() {
        // Arrange - Créer un jeu et un premier exemplaire
        Jeu jeu = new Jeu();
        jeu.setTitre("Risk");
        jeu.setTarifJournalier(BigDecimal.valueOf(5.0));
        jeu = jeuRepository.save(jeu);

        exemplaireService.ajouterExemplaire(jeu.getId(), "REF001", "BAR-DUPLICATE", true);

        final Integer jeuId = jeu.getId();

        // Act & Assert - Tenter d'ajouter un exemplaire avec le même code-barre
        assertThrows(IllegalArgumentException.class, () -> {
            exemplaireService.ajouterExemplaire(jeuId, "REF002", "BAR-DUPLICATE", true);
        });
    }

    @Test
    public void testAjouterExemplaire_PlusieurExemplairesMemeJeu() {
        // Arrange - Créer un jeu
        Jeu jeu = new Jeu();
        jeu.setTitre("7 Wonders");
        jeu.setTarifJournalier(BigDecimal.valueOf(3.0));
        jeu = jeuRepository.save(jeu);

        // Act - Ajouter plusieurs exemplaires du même jeu
        Exemplaire ex1 = exemplaireService.ajouterExemplaire(jeu.getId(), "REF-7W-001", "BAR-7W-001", true);
        Exemplaire ex2 = exemplaireService.ajouterExemplaire(jeu.getId(), "REF-7W-002", "BAR-7W-002", true);
        Exemplaire ex3 = exemplaireService.ajouterExemplaire(jeu.getId(), "REF-7W-003", "BAR-7W-003", false);

        // Assert
        assertNotNull(ex1);
        assertNotNull(ex2);
        assertNotNull(ex3);

        assertEquals(jeu.getId(), ex1.getJeu().getId());
        assertEquals(jeu.getId(), ex2.getJeu().getId());
        assertEquals(jeu.getId(), ex3.getJeu().getId());

        assertNotEquals(ex1.getCodeBarre(), ex2.getCodeBarre());
        assertNotEquals(ex2.getCodeBarre(), ex3.getCodeBarre());

        assertTrue(ex1.isLouable());
        assertTrue(ex2.isLouable());
        assertFalse(ex3.isLouable());
    }
}
