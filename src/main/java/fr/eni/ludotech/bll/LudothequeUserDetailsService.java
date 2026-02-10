package fr.eni.ludotech.bll;

// Classe utilitaire pour interactions éventuelles avec la table utilisateur.
// Elle n'implémente plus UserDetailsService pour éviter d'exposer un second bean
// de type UserDetailsService. La résolution des utilisateurs se fait désormais
// directement dans `SecurityConfig` via `UtilisateurRepository`.

public class LudothequeUserDetailsService {

    // Reste volontairement vide - usage futur si besoin

}
