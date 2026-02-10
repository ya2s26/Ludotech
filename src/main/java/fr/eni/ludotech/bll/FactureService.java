package fr.eni.ludotech.bll;

import fr.eni.ludotech.bo.*;
import fr.eni.ludotech.dal.ExemplaireRepository;
import fr.eni.ludotech.dal.FactureRepository;
import fr.eni.ludotech.dal.LocationRepository;
import fr.eni.ludotech.rest.dto.FactureDTO;
import fr.eni.ludotech.rest.dto.LigneFactureDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class FactureService {

    private final LocationRepository locationRepository;
    private final ExemplaireRepository exemplaireRepository;
    private final FactureRepository factureRepository;

    public FactureService(LocationRepository locationRepository,
                         ExemplaireRepository exemplaireRepository,
                         FactureRepository factureRepository) {
        this.locationRepository = locationRepository;
        this.exemplaireRepository = exemplaireRepository;
        this.factureRepository = factureRepository;
    }

    public FactureDTO enregistrerRetourEtGenererFacture(List<String> codeBarres) {
        if (codeBarres == null || codeBarres.isEmpty()) {
            throw new IllegalArgumentException("Au moins un code-barre doit être fourni");
        }

        LocalDate dateRetour = LocalDate.now();
        List<Location> locations = new ArrayList<>();
        Client client = null;

        // Récupérer toutes les locations en cours
        for (String codeBarre : codeBarres) {
            if (codeBarre == null || codeBarre.trim().isEmpty()) {
                throw new IllegalArgumentException("Code-barre invalide");
            }

            Location location = locationRepository.findLocationEnCoursByCodeBarre(codeBarre.trim())
                .orElseThrow(() -> new IllegalArgumentException("Aucune location en cours pour le code-barre: " + codeBarre));

            // Vérifier que tous les exemplaires sont loués par le même client
            if (client == null) {
                client = location.getClient();
            } else if (!client.getId().equals(location.getClient().getId())) {
                throw new IllegalArgumentException("Tous les exemplaires doivent être loués par le même client");
            }

            locations.add(location);
        }

        // Créer la facture
        Facture facture = new Facture();
        facture.setClient(client);
        facture.setDateFacture(dateRetour);

        BigDecimal montantTotal = BigDecimal.ZERO;

        // Traiter chaque location
        for (Location location : locations) {
            // Marquer la location comme retournée
            location.setDateRetourEffectif(dateRetour);

            // Marquer l'exemplaire comme disponible
            Exemplaire exemplaire = location.getExemplaire();
            exemplaire.setLoue(false);
            exemplaireRepository.save(exemplaire);

            // Créer la ligne de facture
            LigneFacture ligne = new LigneFacture();
            ligne.setLocation(location);

            Jeu jeu = exemplaire.getJeu();
            ligne.setDescription("Location " + jeu.getTitre());

            // Calculer le nombre de jours
            long nombreJours = ChronoUnit.DAYS.between(location.getDateDebut(), dateRetour) + 1;
            ligne.setNombreJours((int) nombreJours);

            // Utiliser le tarif du jeu
            BigDecimal tarifJournalier = jeu.getTarifJournalier() != null ? jeu.getTarifJournalier() : BigDecimal.valueOf(5.0);
            ligne.setTarifJournalier(tarifJournalier);

            // Calculer le montant
            BigDecimal montant = tarifJournalier.multiply(BigDecimal.valueOf(nombreJours));
            ligne.setMontant(montant);

            facture.addLigne(ligne);
            montantTotal = montantTotal.add(montant);
        }

        facture.setMontantTotal(montantTotal);
        facture = factureRepository.save(facture);

        // Convertir en DTO
        return convertToDTO(facture);
    }

    private FactureDTO convertToDTO(Facture facture) {
        FactureDTO dto = new FactureDTO();
        dto.setId(facture.getId());
        dto.setClientId(facture.getClient().getId());
        dto.setClientNom(facture.getClient().getNom());
        dto.setClientPrenom(facture.getClient().getPrenom());
        dto.setDateFacture(facture.getDateFacture());
        dto.setMontantTotal(facture.getMontantTotal());

        List<LigneFactureDTO> lignesDTO = new ArrayList<>();
        for (LigneFacture ligne : facture.getLignes()) {
            Location location = ligne.getLocation();
            LigneFactureDTO ligneDTO = new LigneFactureDTO(
                location.getExemplaire().getJeu().getTitre(),
                location.getExemplaire().getCodeBarre(),
                location.getDateDebut(),
                location.getDateFin(),
                location.getDateRetourEffectif(),
                ligne.getNombreJours(),
                ligne.getTarifJournalier(),
                ligne.getMontant()
            );
            lignesDTO.add(ligneDTO);
        }
        dto.setLignes(lignesDTO);

        return dto;
    }
}
