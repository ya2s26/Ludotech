package fr.eni.ludotech.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LocationEnRetardDTO {
    private Integer locationId;
    private String jeuTitre;
    private String codeBarre;
    private LocalDate dateFin;
    private Integer joursDeRetard;
}
