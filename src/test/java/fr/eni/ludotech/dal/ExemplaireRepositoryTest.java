package fr.eni.ludotech.dal;

import fr.eni.ludotech.bo.Exemplaire;
import fr.eni.ludotech.bo.Jeu;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ExemplaireRepositoryTest {

    @Autowired
    private JeuRepository jeuRepository;

    @Autowired
    private ExemplaireRepository exemplaireRepository;

    @Test
    void ajoutExemplaire() {
        Jeu jeu = new Jeu();
        jeu.setTitre("Catan");
        Jeu savedJeu = jeuRepository.save(jeu);

        Exemplaire exemplaire = new Exemplaire();
        exemplaire.setReference("CAT-001");
        exemplaire.setJeu(savedJeu);

        Exemplaire saved = exemplaireRepository.save(exemplaire);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getJeu().getId()).isEqualTo(savedJeu.getId());
    }
}
