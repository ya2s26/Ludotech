package fr.eni.ludotech.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class SecurityTestController {

    @GetMapping("/public")
    public String publicEndpoint() {
        return "public";
    }

    @GetMapping("/private")
    public String privateEndpoint() {
        return "private";
    }
}

