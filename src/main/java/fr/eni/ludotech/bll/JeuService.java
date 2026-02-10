package fr.eni.ludotech.bll;

import fr.eni.ludotech.bo.Genre;
import fr.eni.ludotech.bo.Jeu;
import fr.eni.ludotech.dal.GenreRepository;
import fr.eni.ludotech.dal.JeuRepository;
import fr.eni.ludotech.dal.projection.JeuDisponibiliteProjection;
import fr.eni.ludotech.rest.dto.JeuDisponibiliteDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class JeuService {

    private final JeuRepository jeuRepository;
    private final GenreRepository genreRepository;

    public JeuService(JeuRepository jeuRepository, GenreRepository genreRepository) {
        this.jeuRepository = jeuRepository;
        this.genreRepository = genreRepository;
    }

    public Jeu addJeu(Jeu jeu) {
        if (jeu == null) {
            throw new IllegalArgumentException("Jeu obligatoire");
        }
        Set<Genre> managedGenres = new HashSet<>();
        if (jeu.getGenres() != null) {
            for (Genre genre : jeu.getGenres()) {
                if (genre == null || genre.getId() == null) {
                    throw new IllegalArgumentException("Genre id obligatoire");
                }
                Genre managed = genreRepository.findById(genre.getId())
                    .orElseThrow(() -> new GenreNotFoundException(genre.getId()));
                managedGenres.add(managed);
            }
        }
        jeu.setGenres(managedGenres);
        return jeuRepository.save(jeu);
    }

    public List<JeuDisponibiliteDTO> findAllJeuxAvecDisponibilite() {
        // Utilise la fonction stockée SQL Server pour récupérer les jeux avec disponibilité
        List<JeuDisponibiliteProjection> projections = jeuRepository.findAllJeuxAvecDisponibilite();

        return projections.stream()
            .map(projection -> {
                // Récupérer les genres du jeu
                Jeu jeu = jeuRepository.findById(projection.getJeuId()).orElse(null);
                Set<String> genreNoms = new HashSet<>();
                if (jeu != null && jeu.getGenres() != null) {
                    genreNoms = jeu.getGenres().stream()
                        .map(Genre::getLibelle)
                        .collect(Collectors.toSet());
                }

                return new JeuDisponibiliteDTO(
                    projection.getJeuId(),
                    projection.getTitre(),
                    genreNoms,
                    projection.getNombreExemplairesDisponibles()
                );
            })
            .collect(Collectors.toList());
    }
}
