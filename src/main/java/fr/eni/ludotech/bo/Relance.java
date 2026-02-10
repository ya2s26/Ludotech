package fr.eni.ludotech.bo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Relance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne(optional = false)
    @JoinColumn(name = "location_id")
    private Location location;

    private LocalDateTime dateRelance;

    private String message;

    private String typeRelance; // EMAIL, SMS, COURRIER

    private String statut; // ENVOYEE, EN_ATTENTE, ECHEC
}
