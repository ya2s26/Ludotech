package fr.eni.ludotech.bll;

import fr.eni.ludotech.bo.Adresse;
import fr.eni.ludotech.bo.Client;
import fr.eni.ludotech.dal.ClientRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class ClientServiceTest {

    @Autowired
    private ClientService clientService;

    @Autowired
    private ClientRepository clientRepository;

    @Test
    void findClientsByNomPrefix() {
        clientRepository.save(buildClient("Dupont", "Alice"));
        clientRepository.save(buildClient("Durand", "Benoit"));
        clientRepository.save(buildClient("Martin", "Claire"));

        List<Client> results = clientService.findClientsByNomPrefix("Du");

        assertThat(results).hasSize(2);
        assertThat(results).allMatch(c -> c.getNom().toLowerCase().startsWith("du"));
    }

    @Test
    void updateClientComplet() {
        Client original = buildClient("Dupont", "Alice");
        original = clientRepository.save(original);

        Client update = new Client();
        update.setId(original.getId());
        update.setNom("Dumas");
        update.setPrenom("Aline");
        update.setEmail("aline.dumas@example.test");
        update.setTelephone("0611223344");

        Adresse adresse = new Adresse();
        adresse.setRue("20 avenue des Tests");
        adresse.setCodePostal("35000");
        adresse.setVille("Rennes");
        update.setAdresse(adresse);

        Client saved = clientService.updateClient(update);

        assertThat(saved.getNom()).isEqualTo("Dumas");
        assertThat(saved.getPrenom()).isEqualTo("Aline");
        assertThat(saved.getAdresse()).isNotNull();
        assertThat(saved.getAdresse().getVille()).isEqualTo("Rennes");
    }

    @Test
    void updateClientCompletClientNonTrouve() {
        Client update = new Client();
        update.setId(999999);
        update.setNom("Inconnu");

        assertThrows(ClientNotFoundException.class, () -> clientService.updateClient(update));
    }

    @Test
    void updateAdresseOnly() {
        Client original = buildClient("Dupont", "Alice");
        original = clientRepository.save(original);

        Adresse nouvelleAdresse = new Adresse();
        nouvelleAdresse.setRue("5 place des Jeux");
        nouvelleAdresse.setCodePostal("44000");
        nouvelleAdresse.setVille("Nantes");

        Client saved = clientService.updateClientAdresse(original.getId(), nouvelleAdresse);

        assertThat(saved.getNom()).isEqualTo("Dupont");
        assertThat(saved.getAdresse()).isNotNull();
        assertThat(saved.getAdresse().getRue()).isEqualTo("5 place des Jeux");
        assertThat(saved.getAdresse().getCodePostal()).isEqualTo("44000");
        assertThat(saved.getAdresse().getVille()).isEqualTo("Nantes");
    }

    private Client buildClient(String nom, String prenom) {
        Client client = new Client();
        client.setNom(nom);
        client.setPrenom(prenom);
        client.setEmail(prenom.toLowerCase() + "." + nom.toLowerCase() + "@example.test");
        client.setTelephone("0600000000");

        Adresse adresse = new Adresse();
        adresse.setRue("1 rue des Tests");
        adresse.setCodePostal("44000");
        adresse.setVille("Nantes");
        client.setAdresse(adresse);

        return client;
    }
}
