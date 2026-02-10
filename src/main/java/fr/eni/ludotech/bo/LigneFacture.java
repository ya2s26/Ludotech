package fr.eni.ludotech.bo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class LigneFacture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "facture_id")
    private Facture facture;

    @OneToOne(optional = false)
    @JoinColumn(name = "location_id")
    private Location location;

    private String description;

    private Integer nombreJours;

    private BigDecimal tarifJournalier;

    private BigDecimal montant;
}
