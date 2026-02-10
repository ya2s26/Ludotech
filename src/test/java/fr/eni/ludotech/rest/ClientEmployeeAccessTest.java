package fr.eni.ludotech.rest;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ClientEmployeeAccessTest {

    @LocalServerPort
    private int port;

    @Test
    public void testClientsEndpoint_UnauthorizedWithoutCredentials() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/clients?nom=test"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Sans authentification, doit retourner 401 ou redirection
        assertThat(response.statusCode())
                .as("Unauthenticated user should not access /clients")
                .isIn(401, 302, 303, 307);
    }

    @Test
    public void testClientsEndpoint_ForbiddenWithUserRole() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        String auth = Base64.getEncoder().encodeToString("user:password".getBytes());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/clients?nom=test"))
                .header("Authorization", "Basic " + auth)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // User role ne doit pas pouvoir accéder (403 Forbidden)
        assertThat(response.statusCode())
                .as("User role should not have access to /clients (should be 403 Forbidden)")
                .isEqualTo(403);
    }

    @Test
    public void testClientsEndpoint_AllowedWithEmployeeRole() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        String auth = Base64.getEncoder().encodeToString("employe:employe123".getBytes());
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/clients?nom=test"))
                .header("Authorization", "Basic " + auth)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Employee role doit pouvoir accéder (200 ou 500 si erreur métier, mais pas 403 ou 401)
        assertThat(response.statusCode())
                .as("Employee role should have access to /clients endpoint")
                .isNotIn(403, 401, 302, 303, 307);
    }

    @Test
    public void testClientsEndpoint_EmployeeCanAccessDifferentMethods() throws Exception {
        String auth = Base64.getEncoder().encodeToString("employe:employe123".getBytes());
        HttpClient client = HttpClient.newHttpClient();

        // Test GET /clients/{id}
        HttpRequest getByIdRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/clients/1"))
                .header("Authorization", "Basic " + auth)
                .GET()
                .build();

        HttpResponse<String> getByIdResponse = client.send(getByIdRequest, HttpResponse.BodyHandlers.ofString());
        assertThat(getByIdResponse.statusCode())
                .as("Employee should be able to GET /clients/{id}")
                .isNotIn(403, 401);

        // Test GET /clients (search by nom)
        HttpRequest searchRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/clients?nom=John"))
                .header("Authorization", "Basic " + auth)
                .GET()
                .build();

        HttpResponse<String> searchResponse = client.send(searchRequest, HttpResponse.BodyHandlers.ofString());
        assertThat(searchResponse.statusCode())
                .as("Employee should be able to search clients")
                .isNotIn(403, 401);
    }
}

