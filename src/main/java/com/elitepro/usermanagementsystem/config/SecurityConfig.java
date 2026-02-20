package com.elitepro.usermanagementsystem.config;

import com.elitepro.usermanagementsystem.service.OurUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Classe principale de configuration de Spring Security
 */
@Configuration // Indique que cette classe contient des Beans Spring
@EnableWebSecurity // Active la sécurité web de Spring Security
public class SecurityConfig {

    /**
     * Service personnalisé permettant de charger un utilisateur depuis la base de données
     */
    @Autowired
    private OurUserDetailsService ourUserDetailsService;

    /**
     * Filtre JWT personnalisé qui intercepte chaque requête pour vérifier le token
     */
    @Autowired
    private JWTAuthFilter jwtAuthFilter;


    /**
     * Configuration principale de la chaîne de filtres de sécurité
     *
     * @param httpSecurity objet permettant de configurer la sécurité HTTP
     * @return SecurityFilterChain configurée
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{

        httpSecurity

                // 🔹 Désactive la protection CSRF
                // Utile lorsque l’on utilise JWT (stateless API)
                .csrf(AbstractHttpConfigurer::disable)

                // 🔹 Active la configuration CORS par défaut
                .cors(Customizer.withDefaults())

                // 🔹 Configuration des règles d’autorisation
                .authorizeHttpRequests(request -> request

                        // Endpoints accessibles sans authentification
                        .requestMatchers("/auth/**", "/public/**").permitAll()

                        // Accessible uniquement aux utilisateurs ayant l’autorité ADMIN
                        .requestMatchers("/admin/**").hasAnyAuthority("ADMIN")

                        // Accessible uniquement aux utilisateurs ayant l’autorité USER
                        .requestMatchers("/user/**").hasAnyAuthority("USER")

                        // Accessible aux ADMIN et USER
                        .requestMatchers("/adminuser/**").hasAnyAuthority("ADMIN", "USER")

                        // Toute autre requête nécessite authentification
                        .anyRequest().authenticated()
                )

                // 🔹 Désactive les sessions (important pour JWT)
                // STATELESS signifie :
                // → Spring ne stocke rien en session
                // → Chaque requête doit contenir son JWT
                .sessionManagement(manager ->
                        manager.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 🔹 Définit le fournisseur d’authentification personnalisé
                .authenticationProvider(authenticationProvider())

                // 🔹 Ajoute le filtre JWT avant le filtre standard de login
                .addFilterBefore(
                        jwtAuthFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        // Construit et retourne la configuration finale
        return httpSecurity.build();
    }


    /**
     * Définit le fournisseur d’authentification
     *
     * DaoAuthenticationProvider :
     * - Utilise UserDetailsService
     * - Vérifie le mot de passe avec PasswordEncoder
     */
    @Bean
    public AuthenticationProvider authenticationProvider(){

        DaoAuthenticationProvider daoAuthenticationProvider =
                new DaoAuthenticationProvider();

        // Définit le service de chargement des utilisateurs
        daoAuthenticationProvider.setUserDetailsService(ourUserDetailsService);

        // Définit l’encodeur de mot de passe (BCrypt)
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());

        return daoAuthenticationProvider;
    }


    /**
     * Bean PasswordEncoder
     *
     * BCrypt est recommandé car :
     * - Il hache les mots de passe
     * - Il ajoute un salt automatique
     * - Il est sécurisé contre les attaques modernes
     */
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }


    /**
     * Bean AuthenticationManager
     *
     * Permet de gérer l’authentification lors du login :
     * authenticationManager.authenticate(...)
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration
    ) throws Exception {

        return authenticationConfiguration.getAuthenticationManager();
    }
}

