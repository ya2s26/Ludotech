package fr.eni.ludotech.bll;

import fr.eni.ludotech.bo.Adresse;
import fr.eni.ludotech.bo.Client;
import fr.eni.ludotech.dal.ClientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ClientService {

    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public List<Client> findClientsByNomPrefix(String prefix) {
        String value = prefix == null ? "" : prefix.trim();
        if (value.isEmpty()) {
            return List.of();
        }
        return clientRepository.findByNomStartingWithIgnoreCase(value);
    }

    public Client updateClient(Client client) {
        if (client == null || client.getId() == null) {
            throw new IllegalArgumentException("Client et id obligatoires");
        }
        Client existing = getClientOrThrow(client.getId());

        existing.setNom(client.getNom());
        existing.setPrenom(client.getPrenom());
        existing.setEmail(client.getEmail());
        existing.setTelephone(client.getTelephone());

        if (client.getAdresse() == null) {
            existing.setAdresse(null);
        } else {
            Adresse target = existing.getAdresse();
            if (target == null) {
                target = new Adresse();
                existing.setAdresse(target);
            }
            target.setRue(client.getAdresse().getRue());
            target.setCodePostal(client.getAdresse().getCodePostal());
            target.setVille(client.getAdresse().getVille());
        }

        return clientRepository.save(existing);
    }

    public Client updateClientAdresse(Integer clientId, Adresse nouvelleAdresse) {
        if (clientId == null || nouvelleAdresse == null) {
            throw new IllegalArgumentException("Client id et adresse obligatoires");
        }
        Client existing = getClientOrThrow(clientId);
        Adresse target = existing.getAdresse();
        if (target == null) {
            target = new Adresse();
            existing.setAdresse(target);
        }
        target.setRue(nouvelleAdresse.getRue());
        target.setCodePostal(nouvelleAdresse.getCodePostal());
        target.setVille(nouvelleAdresse.getVille());

        return clientRepository.save(existing);
    }

    public Client createClient(Client client) {
        if (client == null) {
            throw new IllegalArgumentException("Client obligatoire");
        }
        client.setId(null);
        return clientRepository.save(client);
    }

    public void deleteClient(Integer clientId) {
        if (clientId == null) {
            throw new IllegalArgumentException("Client id obligatoire");
        }
        Client existing = getClientOrThrow(clientId);
        clientRepository.delete(existing);
    }

    public Client findClientById(Integer clientId) {
        if (clientId == null) {
            throw new IllegalArgumentException("Client id obligatoire");
        }
        return getClientOrThrow(clientId);
    }

    private Client getClientOrThrow(Integer clientId) {
        return clientRepository.findById(clientId)
            .orElseThrow(() -> new ClientNotFoundException(clientId));
    }
}
