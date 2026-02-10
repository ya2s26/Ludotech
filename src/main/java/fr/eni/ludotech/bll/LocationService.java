package fr.eni.ludotech.bll;

import fr.eni.ludotech.bo.Client;
import fr.eni.ludotech.bo.Exemplaire;
import fr.eni.ludotech.bo.Location;
import fr.eni.ludotech.dal.ClientRepository;
import fr.eni.ludotech.dal.ExemplaireRepository;
import fr.eni.ludotech.dal.LocationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@Transactional
public class LocationService {

    private final LocationRepository locationRepository;
    private final ExemplaireRepository exemplaireRepository;
    private final ClientRepository clientRepository;

    public LocationService(LocationRepository locationRepository,
                          ExemplaireRepository exemplaireRepository,
                          ClientRepository clientRepository) {
        this.locationRepository = locationRepository;
        this.exemplaireRepository = exemplaireRepository;
        this.clientRepository = clientRepository;
    }

    public Location creerLocation(Integer clientId, String codeBarre, LocalDate dateDebut, LocalDate dateFin) {
        // Validation
        if (clientId == null) {
            throw new IllegalArgumentException("Client id obligatoire");
        }
        if (codeBarre == null || codeBarre.trim().isEmpty()) {
            throw new IllegalArgumentException("Code-barre obligatoire");
        }
        if (dateDebut == null) {
            throw new IllegalArgumentException("Date de début obligatoire");
        }
        if (dateFin == null) {
            throw new IllegalArgumentException("Date de fin obligatoire");
        }
        if (dateFin.isBefore(dateDebut)) {
            throw new IllegalArgumentException("La date de fin doit être après la date de début");
        }

        // Récupérer le client
        Client client = clientRepository.findById(clientId)
            .orElseThrow(() -> new ClientNotFoundException(clientId));

        // Récupérer l'exemplaire par code-barre
        Exemplaire exemplaire = exemplaireRepository.findByCodeBarre(codeBarre.trim())
            .orElseThrow(() -> new ExemplaireNotFoundException(codeBarre));

        // Vérifier que l'exemplaire est louable
        if (!exemplaire.isLouable()) {
            throw new IllegalArgumentException("L'exemplaire n'est pas louable");
        }

        // Vérifier que l'exemplaire n'est pas déjà loué
        if (exemplaire.isLoue()) {
            throw new IllegalArgumentException("L'exemplaire est déjà loué");
        }

        // Créer la location
        Location location = new Location();
        location.setClient(client);
        location.setExemplaire(exemplaire);
        location.setDateDebut(dateDebut);
        location.setDateFin(dateFin);

        // Marquer l'exemplaire comme loué
        exemplaire.setLoue(true);
        exemplaireRepository.save(exemplaire);

        return locationRepository.save(location);
    }
}
