package fr.eni.ludotech.bo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Exemplaire {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    private String reference;

    private String codeBarre;

    private boolean louable = true;

    private boolean loue = false;

    @ManyToOne(optional = false)
    @JoinColumn(name = "jeu_id")
    private Jeu jeu;
}
