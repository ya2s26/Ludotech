package fr.eni.ludotech.dal;

import fr.eni.ludotech.bo.Facture;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FactureRepository extends JpaRepository<Facture, Integer> {
}
