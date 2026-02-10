package fr.eni.ludotech.rest;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JeuPublicAccessTest {

    @LocalServerPort
    private int port;

    @Test
    public void testJeuxEndpointIsPublic_NoRedirectToLogin() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:" + port + "/jeux"))
                .GET()
                .build();

        // Use followRedirects to see the final status
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("=== JEU PUBLIC ACCESS TEST ===");
        System.out.println("Status code: " + response.statusCode());
        System.out.println("Response body (first 200 chars): " + response.body().substring(0, Math.min(200, response.body().length())));
        System.out.println("===============================");

        // L'endpoint /jeux doit être accessible (pas de redirection vers login)
        // On n'accepte pas les codes 302, 303, 307 (redirection)
        assertThat(response.statusCode())
                .as("The /jeux endpoint should be public (not redirected to login). Status: %d", response.statusCode())
                .isNotIn(302, 303, 307);
    }
}

