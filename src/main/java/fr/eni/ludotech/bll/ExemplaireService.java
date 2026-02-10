package fr.eni.ludotech.bll;

import fr.eni.ludotech.bo.Exemplaire;
import fr.eni.ludotech.bo.Jeu;
import fr.eni.ludotech.dal.ExemplaireRepository;
import fr.eni.ludotech.dal.JeuRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ExemplaireService {

    private final ExemplaireRepository exemplaireRepository;
    private final JeuRepository jeuRepository;

    public ExemplaireService(ExemplaireRepository exemplaireRepository, JeuRepository jeuRepository) {
        this.exemplaireRepository = exemplaireRepository;
        this.jeuRepository = jeuRepository;
    }

    public Exemplaire findByCodeBarre(String codeBarre) {
        if (codeBarre == null || codeBarre.trim().isEmpty()) {
            throw new IllegalArgumentException("Code-barre obligatoire");
        }
        return exemplaireRepository.findByCodeBarre(codeBarre.trim())
            .orElseThrow(() -> new ExemplaireNotFoundException(codeBarre));
    }

    public Exemplaire ajouterExemplaire(Integer jeuId, String reference, String codeBarre, boolean louable) {
        // Validation
        if (jeuId == null) {
            throw new IllegalArgumentException("Jeu id obligatoire");
        }
        if (codeBarre == null || codeBarre.trim().isEmpty()) {
            throw new IllegalArgumentException("Code-barre obligatoire");
        }
        if (reference == null || reference.trim().isEmpty()) {
            throw new IllegalArgumentException("Référence obligatoire");
        }

        // Vérifier que le jeu existe
        Jeu jeu = jeuRepository.findById(jeuId)
            .orElseThrow(() -> new IllegalArgumentException("Jeu non trouvé avec l'id: " + jeuId));

        // Vérifier que le code-barre n'existe pas déjà
        if (exemplaireRepository.findByCodeBarre(codeBarre.trim()).isPresent()) {
            throw new IllegalArgumentException("Un exemplaire avec ce code-barre existe déjà: " + codeBarre);
        }

        // Créer l'exemplaire
        Exemplaire exemplaire = new Exemplaire();
        exemplaire.setJeu(jeu);
        exemplaire.setReference(reference.trim());
        exemplaire.setCodeBarre(codeBarre.trim());
        exemplaire.setLouable(louable);
        exemplaire.setLoue(false);

        return exemplaireRepository.save(exemplaire);
    }
}
