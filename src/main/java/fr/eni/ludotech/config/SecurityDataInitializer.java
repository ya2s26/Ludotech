package fr.eni.ludotech.config;

import fr.eni.ludotech.bo.Utilisateur;
import fr.eni.ludotech.dal.UtilisateurRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;

@Component
@Profile("default")
public class SecurityDataInitializer implements CommandLineRunner {

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    public SecurityDataInitializer(UtilisateurRepository utilisateurRepository, PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (utilisateurRepository.findByUsername("user").isEmpty()) {
            Utilisateur u = new Utilisateur();
            u.setUsername("user");
            u.setPassword(passwordEncoder.encode("password"));
            u.setRoles("USER");
            utilisateurRepository.save(u);
        }
        if (utilisateurRepository.findByUsername("employe").isEmpty()) {
            Utilisateur e = new Utilisateur();
            e.setUsername("employe");
            e.setPassword(passwordEncoder.encode("employe123"));
            e.setRoles("EMPLOYE");
            utilisateurRepository.save(e);
        }
    }
}

