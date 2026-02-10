package fr.eni.ludotech.dal;

import fr.eni.ludotech.bo.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GenreRepository extends JpaRepository<Genre, Integer> {
}
