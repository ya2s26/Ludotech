package fr.eni.ludotech.dal;

import fr.eni.ludotech.bo.Jeu;
import fr.eni.ludotech.dal.projection.JeuDisponibiliteProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface JeuRepository extends JpaRepository<Jeu, Integer> {

    @Query(value = "SELECT jeu_id as jeuId, titre, nombre_exemplaires_disponibles as nombreExemplairesDisponibles " +
                   "FROM fn_jeux_avec_disponibilite()",
           nativeQuery = true)
    List<JeuDisponibiliteProjection> findAllJeuxAvecDisponibilite();
}
