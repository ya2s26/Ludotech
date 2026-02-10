package fr.eni.ludotech.bo;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @Column(nullable = false, length = 50)
    private String nom;

    @Column(nullable = false, length = 50)
    private String prenom;

    @Column(nullable = false, length = 50)
    private String email;

    private String telephone;

    @OneToOne(cascade = jakarta.persistence.CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "adresse_id")
    private Adresse adresse;
}

