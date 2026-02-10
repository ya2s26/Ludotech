package fr.eni.ludotech.bll;

public class ClientNotFoundException extends RuntimeException {
    public ClientNotFoundException(Integer clientId) {
        super("Client introuvable: " + clientId);
    }
}
