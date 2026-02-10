package fr.eni.ludotech.bo;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Facture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "client_id")
    private Client client;

    private LocalDate dateFacture;

    @OneToMany(mappedBy = "facture", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LigneFacture> lignes = new ArrayList<>();

    private BigDecimal montantTotal;

    public void addLigne(LigneFacture ligne) {
        lignes.add(ligne);
        ligne.setFacture(this);
    }
}
