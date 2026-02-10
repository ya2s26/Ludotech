package fr.eni.ludotech.rest;

import fr.eni.ludotech.bll.FactureService;
import fr.eni.ludotech.bll.LocationService;
import fr.eni.ludotech.bo.Location;
import fr.eni.ludotech.rest.dto.FactureDTO;
import fr.eni.ludotech.rest.dto.LocationRequestDTO;
import fr.eni.ludotech.rest.dto.RetourLocationRequestDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LocationRestController {

    private final LocationService locationService;
    private final FactureService factureService;

    public LocationRestController(LocationService locationService, FactureService factureService) {
        this.locationService = locationService;
        this.factureService = factureService;
    }

    @PostMapping("/locations")
    public Location creerLocation(@RequestBody LocationRequestDTO request) {
        return locationService.creerLocation(
            request.getClientId(),
            request.getCodeBarre(),
            request.getDateDebut(),
            request.getDateFin()
        );
    }

    @PostMapping("/locations/retour")
    public FactureDTO enregistrerRetour(@RequestBody RetourLocationRequestDTO request) {
        return factureService.enregistrerRetourEtGenererFacture(request.getCodeBarres());
    }
}
