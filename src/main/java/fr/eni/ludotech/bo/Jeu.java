package fr.eni.ludotech.bo;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Jeu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    private String titre;

    private BigDecimal tarifJournalier;

    @ManyToMany
    @JoinTable(
        name = "jeu_genre",
        joinColumns = @JoinColumn(name = "jeu_id"),
        inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres = new HashSet<>();
}
