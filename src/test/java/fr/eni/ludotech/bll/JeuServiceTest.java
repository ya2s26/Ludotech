package fr.eni.ludotech.bll;

import fr.eni.ludotech.bo.Genre;
import fr.eni.ludotech.bo.Jeu;
import fr.eni.ludotech.dal.GenreRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class JeuServiceTest {

    @Autowired
    private JeuService jeuService;

    @Autowired
    private GenreRepository genreRepository;

    @Test
    void ajoutJeuAvecGenres() {
        Genre strategie = new Genre();
        strategie.setLibelle("Strategie");
        Genre cooperation = new Genre();
        cooperation.setLibelle("Cooperation");

        strategie = genreRepository.save(strategie);
        cooperation = genreRepository.save(cooperation);

        Jeu jeu = new Jeu();
        jeu.setTitre("Pandemic");
        jeu.setGenres(Set.of(strategie, cooperation));

        Jeu saved = jeuService.addJeu(jeu);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getGenres()).hasSize(2);
    }
}
