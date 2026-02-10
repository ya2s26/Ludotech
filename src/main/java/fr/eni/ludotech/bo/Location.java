package fr.eni.ludotech.bo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne(optional = false)
    @JoinColumn(name = "exemplaire_id")
    private Exemplaire exemplaire;

    private LocalDate dateDebut;

    private LocalDate dateFin;

    private LocalDate dateRetourEffectif;
}
