package fr.eni.ludotech.rest;

import fr.eni.ludotech.bll.RelanceService;
import fr.eni.ludotech.rest.dto.RelanceDTO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class RelanceRestController {

    private final RelanceService relanceService;

    public RelanceRestController(RelanceService relanceService) {
        this.relanceService = relanceService;
    }

    @PostMapping("/relances/client/{clientId}")
    public RelanceDTO relancerClient(@PathVariable Integer clientId,
                                     @RequestParam(defaultValue = "EMAIL") String typeRelance) {
        return relanceService.relancerClient(clientId, typeRelance);
    }

    @PostMapping("/relances/tous")
    public List<RelanceDTO> relancerTousLesClientsEnRetard(@RequestParam(defaultValue = "EMAIL") String typeRelance) {
        return relanceService.relancerTousLesClientsEnRetard(typeRelance);
    }
}
