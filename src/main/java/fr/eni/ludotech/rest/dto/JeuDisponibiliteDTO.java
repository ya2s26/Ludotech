package fr.eni.ludotech.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class JeuDisponibiliteDTO {
    private Integer id;
    private String titre;
    private Set<String> genres;
    private long nombreExemplairesDisponibles;
}
