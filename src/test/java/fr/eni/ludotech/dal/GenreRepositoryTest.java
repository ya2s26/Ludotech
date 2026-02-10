package fr.eni.ludotech.dal;

import fr.eni.ludotech.bo.Genre;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class GenreRepositoryTest {

    @Autowired
    private GenreRepository genreRepository;

    @Test
    void ajoutGenre() {
        Genre genre = new Genre();
        genre.setLibelle("Strategie");

        Genre saved = genreRepository.save(genre);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getLibelle()).isEqualTo("Strategie");
    }
}
