package fr.eni.ludotech.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LigneFactureDTO {
    private String jeuTitre;
    private String codeBarre;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private LocalDate dateRetourEffectif;
    private Integer nombreJours;
    private BigDecimal tarifJournalier;
    private BigDecimal montant;
}
