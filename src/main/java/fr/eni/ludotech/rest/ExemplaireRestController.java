package fr.eni.ludotech.rest;

import fr.eni.ludotech.bll.ExemplaireService;
import fr.eni.ludotech.bo.Exemplaire;
import fr.eni.ludotech.rest.dto.ExemplaireRequestDTO;
import org.springframework.web.bind.annotation.*;

@RestController
public class ExemplaireRestController {

    private final ExemplaireService exemplaireService;

    public ExemplaireRestController(ExemplaireService exemplaireService) {
        this.exemplaireService = exemplaireService;
    }

    @GetMapping("/exemplaires/search")
    public Exemplaire searchByCodeBarre(@RequestParam String codeBarre) {
        return exemplaireService.findByCodeBarre(codeBarre);
    }

    @PostMapping("/exemplaires")
    public Exemplaire ajouterExemplaire(@RequestBody ExemplaireRequestDTO request) {
        return exemplaireService.ajouterExemplaire(
            request.getJeuId(),
            request.getReference(),
            request.getCodeBarre(),
            request.isLouable()
        );
    }
}
