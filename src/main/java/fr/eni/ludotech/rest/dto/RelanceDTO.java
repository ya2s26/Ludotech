package fr.eni.ludotech.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RelanceDTO {
    private Integer id;
    private Integer clientId;
    private String clientNom;
    private String clientPrenom;
    private String clientEmail;
    private String clientTelephone;
    private List<LocationEnRetardDTO> locationsEnRetard;
    private String message;
    private String typeRelance;
}
