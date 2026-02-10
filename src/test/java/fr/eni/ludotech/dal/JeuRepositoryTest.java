package fr.eni.ludotech.dal;

import fr.eni.ludotech.bo.Genre;
import fr.eni.ludotech.bo.Jeu;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class JeuRepositoryTest {

    @Autowired
    private JeuRepository jeuRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Test
    void ajoutJeuAvecGenres() {
        Genre g1 = new Genre();
        g1.setLibelle("Strategie");
        Genre g2 = new Genre();
        g2.setLibelle("Cooperation");

        g1 = genreRepository.save(g1);
        g2 = genreRepository.save(g2);

        Jeu jeu = new Jeu();
        jeu.setTitre("Pandemic");
        jeu.setGenres(Set.of(g1, g2));

        Jeu saved = jeuRepository.save(jeu);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getGenres()).hasSize(2);
    }
}
