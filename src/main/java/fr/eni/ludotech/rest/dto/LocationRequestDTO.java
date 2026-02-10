package fr.eni.ludotech.rest.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class LocationRequestDTO {
    private Integer clientId;
    private String codeBarre;
    private LocalDate dateDebut;
    private LocalDate dateFin;
}
