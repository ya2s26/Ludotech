package fr.eni.ludotech.dal;

import fr.eni.ludotech.bo.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Integer> {

    @Query("SELECT l FROM Location l WHERE l.exemplaire.codeBarre = :codeBarre AND l.dateRetourEffectif IS NULL")
    Optional<Location> findLocationEnCoursByCodeBarre(String codeBarre);

    @Query("SELECT l FROM Location l WHERE l.client.id = :clientId AND l.dateRetourEffectif IS NULL AND l.dateFin < :dateActuelle")
    List<Location> findLocationsEnRetardByClient(Integer clientId, LocalDate dateActuelle);

    @Query("SELECT l FROM Location l WHERE l.dateRetourEffectif IS NULL AND l.dateFin < :dateActuelle")
    List<Location> findAllLocationsEnRetard(LocalDate dateActuelle);
}
