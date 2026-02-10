package fr.eni.ludotech.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FactureDTO {
    private Integer id;
    private Integer clientId;
    private String clientNom;
    private String clientPrenom;
    private LocalDate dateFacture;
    private List<LigneFactureDTO> lignes;
    private BigDecimal montantTotal;
}
