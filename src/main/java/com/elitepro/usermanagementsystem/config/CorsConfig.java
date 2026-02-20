package com.elitepro.usermanagementsystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Classe de configuration CORS
 *
 * CORS (Cross-Origin Resource Sharing) permet à un frontend
 * (ex: Angular, React, Vue) situé sur un autre domaine/port
 * d’accéder à ton API backend.
 */
@Configuration // Indique que cette classe contient des configurations Spring
public class CorsConfig {

    /**
     * Bean WebMvcConfigurer
     *
     * Permet de personnaliser la configuration Spring MVC,
     * notamment la configuration CORS globale.
     */
    @Bean
    public WebMvcConfigurer webMvcConfigurer(){

        // On retourne une implémentation anonyme de l’interface WebMvcConfigurer
        return new WebMvcConfigurer() {

            /**
             * Méthode appelée par Spring pour configurer les règles CORS
             *
             * @param registry registre contenant toutes les règles CORS
             */
            @Override
            public void addCorsMappings(CorsRegistry registry) {

                registry

                        // Applique cette configuration à toutes les routes
                        // "/**" signifie : tous les endpoints
                        .addMapping("/**")

                        // Autorise uniquement ces méthodes HTTP
                        .allowedMethods("GET", "POST", "PUT", "DELETE")

                        // Autorise toutes les origines (⚠ non recommandé en production)
                        .allowedOrigins("*");
            }
        };
    }
}

