package fr.eni.ludotech.rest;

import fr.eni.ludotech.bll.ClientService;
import fr.eni.ludotech.bo.Adresse;
import fr.eni.ludotech.bo.Client;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ClientRestController {

    private final ClientService clientService;

    public ClientRestController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping("/clients")
    public Client create(@RequestBody Client client) {
        return clientService.createClient(client);
    }

    @DeleteMapping("/clients/{id}")
    public void delete(@PathVariable Integer id) {
        clientService.deleteClient(id);
    }

    @PutMapping("/clients/{id}")
    public Client update(@PathVariable Integer id, @RequestBody Client client) {
        client.setId(id);
        return clientService.updateClient(client);
    }

    @PatchMapping("/clients/{id}/adresse")
    public Client updateAdresse(@PathVariable Integer id, @RequestBody Adresse adresse) {
        return clientService.updateClientAdresse(id, adresse);
    }

    @GetMapping("/clients")
    public List<Client> findByNomPrefix(@RequestParam String nom) {
        return clientService.findClientsByNomPrefix(nom);
    }

    @GetMapping("/clients/{id}")
    public Client findById(@PathVariable Integer id) {
        return clientService.findClientById(id);
    }
}
