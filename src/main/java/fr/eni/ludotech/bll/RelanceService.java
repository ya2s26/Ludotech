package fr.eni.ludotech.bll;

import fr.eni.ludotech.bo.Client;
import fr.eni.ludotech.bo.Location;
import fr.eni.ludotech.bo.Relance;
import fr.eni.ludotech.dal.ClientRepository;
import fr.eni.ludotech.dal.LocationRepository;
import fr.eni.ludotech.dal.RelanceRepository;
import fr.eni.ludotech.rest.dto.LocationEnRetardDTO;
import fr.eni.ludotech.rest.dto.RelanceDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class RelanceService {

    private final ClientRepository clientRepository;
    private final LocationRepository locationRepository;
    private final RelanceRepository relanceRepository;

    public RelanceService(ClientRepository clientRepository,
                         LocationRepository locationRepository,
                         RelanceRepository relanceRepository) {
        this.clientRepository = clientRepository;
        this.locationRepository = locationRepository;
        this.relanceRepository = relanceRepository;
    }

    public RelanceDTO relancerClient(Integer clientId, String typeRelance) {
        if (clientId == null) {
            throw new IllegalArgumentException("Client id obligatoire");
        }
        if (typeRelance == null || typeRelance.trim().isEmpty()) {
            typeRelance = "EMAIL"; // Par défaut
        }

        // Récupérer le client
        Client client = clientRepository.findById(clientId)
            .orElseThrow(() -> new ClientNotFoundException(clientId));

        // Récupérer les locations en retard pour ce client
        LocalDate dateActuelle = LocalDate.now();
        List<Location> locationsEnRetard = locationRepository.findLocationsEnRetardByClient(clientId, dateActuelle);

        if (locationsEnRetard.isEmpty()) {
            throw new IllegalArgumentException("Aucune location en retard pour ce client");
        }

        // Créer les DTOs des locations en retard
        List<LocationEnRetardDTO> locationsDTO = new ArrayList<>();
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("Bonjour ").append(client.getPrenom()).append(" ").append(client.getNom()).append(",\n\n");
        messageBuilder.append("Nous vous contactons concernant les locations suivantes en retard :\n\n");

        for (Location location : locationsEnRetard) {
            long joursRetard = ChronoUnit.DAYS.between(location.getDateFin(), dateActuelle);

            LocationEnRetardDTO locationDTO = new LocationEnRetardDTO(
                location.getId(),
                location.getExemplaire().getJeu().getTitre(),
                location.getExemplaire().getCodeBarre(),
                location.getDateFin(),
                (int) joursRetard
            );
            locationsDTO.add(locationDTO);

            messageBuilder.append("- ").append(location.getExemplaire().getJeu().getTitre())
                .append(" (retour prévu le ").append(location.getDateFin())
                .append(", ").append(joursRetard).append(" jour(s) de retard)\n");

            // Créer une relance pour chaque location
            Relance relance = new Relance();
            relance.setClient(client);
            relance.setLocation(location);
            relance.setDateRelance(LocalDateTime.now());
            relance.setTypeRelance(typeRelance.toUpperCase());
            relance.setStatut("ENVOYEE");
            relance.setMessage(messageBuilder.toString());
            relanceRepository.save(relance);
        }

        messageBuilder.append("\nMerci de retourner ces jeux dès que possible.\n\n");
        messageBuilder.append("Cordialement,\nL'équipe Ludotech");

        // Créer le DTO de réponse
        RelanceDTO relanceDTO = new RelanceDTO();
        relanceDTO.setClientId(client.getId());
        relanceDTO.setClientNom(client.getNom());
        relanceDTO.setClientPrenom(client.getPrenom());
        relanceDTO.setClientEmail(client.getEmail());
        relanceDTO.setClientTelephone(client.getTelephone());
        relanceDTO.setLocationsEnRetard(locationsDTO);
        relanceDTO.setMessage(messageBuilder.toString());
        relanceDTO.setTypeRelance(typeRelance.toUpperCase());

        return relanceDTO;
    }

    public List<RelanceDTO> relancerTousLesClientsEnRetard(String typeRelance) {
        if (typeRelance == null || typeRelance.trim().isEmpty()) {
            typeRelance = "EMAIL";
        }

        LocalDate dateActuelle = LocalDate.now();
        List<Location> toutesLocationsEnRetard = locationRepository.findAllLocationsEnRetard(dateActuelle);

        // Grouper par client
        List<Client> clientsEnRetard = toutesLocationsEnRetard.stream()
            .map(Location::getClient)
            .distinct()
            .toList();

        List<RelanceDTO> relances = new ArrayList<>();
        for (Client client : clientsEnRetard) {
            try {
                RelanceDTO relance = relancerClient(client.getId(), typeRelance);
                relances.add(relance);
            } catch (Exception e) {
                // Logger l'erreur mais continuer avec les autres clients
                System.err.println("Erreur lors de la relance du client " + client.getId() + ": " + e.getMessage());
            }
        }

        return relances;
    }
}
