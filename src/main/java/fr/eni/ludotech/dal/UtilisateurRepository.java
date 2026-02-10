package fr.eni.ludotech.dal;

import fr.eni.ludotech.bo.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Integer> {
    Optional<Utilisateur> findByUsername(String username);
}

