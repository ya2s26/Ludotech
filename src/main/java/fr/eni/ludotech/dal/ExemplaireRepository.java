package fr.eni.ludotech.dal;

import fr.eni.ludotech.bo.Exemplaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ExemplaireRepository extends JpaRepository<Exemplaire, Integer> {

    @Query("SELECT COUNT(e) FROM Exemplaire e WHERE e.jeu.id = :jeuId AND e.louable = true AND e.loue = false")
    long countExemplairesDisponibles(Integer jeuId);

    Optional<Exemplaire> findByCodeBarre(String codeBarre);
}
