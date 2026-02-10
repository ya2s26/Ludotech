package fr.eni.ludotech.rest;

import fr.eni.ludotech.bll.JeuService;
import fr.eni.ludotech.rest.dto.JeuDisponibiliteDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class JeuRestController {

    private final JeuService jeuService;

    public JeuRestController(JeuService jeuService) {
        this.jeuService = jeuService;
    }

    @GetMapping("/jeux")
    public List<JeuDisponibiliteDTO> findAllAvecDisponibilite() {
        return jeuService.findAllJeuxAvecDisponibilite();
    }
}
