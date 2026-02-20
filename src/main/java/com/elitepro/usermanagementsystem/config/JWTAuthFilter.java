package com.elitepro.usermanagementsystem.config;

import com.elitepro.usermanagementsystem.utils.JWTUtils;
import com.elitepro.usermanagementsystem.service.OurUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component  /*Permet à Spring d’enregistrer automatiquement ce filtre comme Bean. Il sera injecté dans ta
SecurityConfig*/
public class JWTAuthFilter extends OncePerRequestFilter { /* 👉 C’est le filtre qui intercepte chaque requête
HTTP pour vérifier le JWT (Access Token)./ Avec l'héritage OncePerRequestFilter,le filtre s’exécute une seule
fois par requête*/

    @Autowired /*Injection des dépendances*/
    private JWTUtils jwtUtils; /*Classe utilitaire qui : ✔génère le token, ✔extrait le username et
     ✔valide le token*/

    @Autowired /*Injection des dépendances*/
    private OurUserDetailsService ourUserDetailsService; /*Implémentation personnalisée de UserDetailsService /
    Elle permet de charger l’utilisateur depuis la base de données*/

    @Override
    protected void doFilterInternal( /*Méthode principale du filtre / C’est la méthode appelée automatiquement
    à chaque requête HTTP */
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization"); /*Récupération du header Authorization
        On récupère Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...*/
        final String jwtToken;
        final String userEmail;

        if (authHeader == null || authHeader.isBlank()) { /*Vérification si le header existe / Si aucun token
        n’est envoyé : 👉 On laisse passer la requête👉 On ne bloque pas / Très important pour : login, register
        endpoints publics*/
            filterChain.doFilter(request, response);
            return;
        }

        jwtToken = authHeader.substring(7); /*Extraction du token / Pourquoi 7 ? Parce que :
        "Bearer " = 7 caractères. Donc on enlève : 'Bearer' et on garde uniquement le JWT.*/
        if (!authHeader.startsWith("Bearer ")) { /*Petite amélioration recommandée :*/
            filterChain.doFilter(request, response);
            return;
        }

        userEmail = jwtUtils.extractUsername(jwtToken); /*Extraction du username (email) / Dans ton JWT,
        le subject contient l’email / Donc ici on lit le json : {"sub": "clovis@gmail.com"}*/

        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) { /*Vérifier
        si l'utilisateur n'est pas déjà authentifié /Pourquoi ? Parce que Spring Security garde l’utilisateur
        connecté dans SecurityContextHolder. Si déjà authentifié → inutile de refaire l’authentification.*/
            UserDetails userDetails = ourUserDetailsService.loadUserByUsername(userEmail); /*Charger
            l'utilisateur depuis la base / Ici : ✔On vérifie que l’utilisateur existe toujours, ✔On récupère
            ses rôles et ✔On récupère ses permissions*/

            if (jwtUtils.isTokenValid(jwtToken, userDetails)) { /*Validation du token / On vérifie :✔Signature
            valide, ✔Non expiré et ✔Username correspond / Si tout est bon → on authentifie*/
                SecurityContext securityContext = SecurityContextHolder.createEmptyContext();

                UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken( /*Création
                du token d’authentification Spring / Ce token représente un utilisateur authentifié dans Spring.
                Paramètres : ✔Principal → userDetails, ✔Credentials → null (pas besoin du mot de passe) et
                ✔Authorities → rôles*/
                        userDetails, null, userDetails.getAuthorities()
                );

                token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); /*Ajouter détails
                de la requête / Ajoute : ✔IP, ✔session id et ✔infos supplémentaires*/

                /*Création du SecurityContext / Ici on dit à Spring : Cet utilisateur est maintenant
                authentifié pour cette requête.*/
                securityContext.setAuthentication(token);
                SecurityContextHolder.setContext(securityContext);
                /*Après ça : SecurityContextHolder.getContext().getAuthentication()*/
            }
        }
        filterChain.doFilter(request, response); /*Continuer la chaîne de filtres / Permet à la requête :
         ✔d’aller vers le contrôleur et ✔de passer les autres filtres*/
    }
}
