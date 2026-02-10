package fr.eni.ludotech.config;

import fr.eni.ludotech.dal.UtilisateurRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.context.annotation.Primary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/public", "/h2-console/**", "/jeux").permitAll()
                .requestMatchers("/clients/**").hasRole("EMPLOYE")
                .anyRequest().authenticated()
            )
            .formLogin(Customizer.withDefaults())
            .httpBasic(Customizer.withDefaults());

        // allow frames (for H2 console)
        http.headers(headers -> headers.frameOptions(frame -> frame.disable()));

        return http.build();
    }

    @Bean
    @Primary
    public UserDetailsService users(UtilisateurRepository utilisateurRepository, PasswordEncoder passwordEncoder) {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();

        // Utilisateur USER (accès restreint)
        manager.createUser(User.withUsername("user")
                .password(passwordEncoder.encode("password"))
                .roles("USER")
                .build());

        // Utilisateur EMPLOYE (accès aux clients)
        manager.createUser(User.withUsername("employe")
                .password(passwordEncoder.encode("employe123"))
                .roles("EMPLOYE")
                .build());

        // Composite: try DB first, then in-memory
        return username -> {
            // try DB
            return utilisateurRepository.findByUsername(username)
                    .map(u -> {
                        var authorities = java.util.Arrays.stream((u.getRoles() == null ? "" : u.getRoles()).split(","))
                                .map(String::trim)
                                .filter(s -> !s.isEmpty())
                                .map(r -> new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + r))
                                .collect(java.util.stream.Collectors.toList());
                        logger.info("User '{}' loaded from DB", username);
                        return (UserDetails) new org.springframework.security.core.userdetails.User(u.getUsername(), u.getPassword(), authorities);
                    })
                    .orElseGet(() -> {
                        logger.info("User '{}' not found in DB, fallback to in-memory", username);
                        try {
                            UserDetails ud = manager.loadUserByUsername(username);
                            logger.info("User '{}' loaded from in-memory", username);
                            return ud;
                        } catch (UsernameNotFoundException ex2) {
                            logger.info("User '{}' not found in in-memory either", username);
                            throw new UsernameNotFoundException("User not found: " + username);
                        }
                    });
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
