package com.elitepro.usermanagementsystem.entity;

/*
 * Package entity :
 * Contient les classes mappées à la base de données (tables).
 * Chaque classe annotée avec @Entity correspond à une table SQL.
 */

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;


/*
 * @Entity
 *
 * Indique que cette classe est une entité JPA.
 * Elle sera mappée automatiquement à une table en base de données.
 */
@Entity

/*
 * @Table(name = "ourusers")
 *
 * Permet de spécifier explicitement le nom de la table
 * dans la base de données.
 *
 * Ici la table s'appelle : ourusers
 */
@Table(name = "ourusers")

/*
 * @Data (Lombok)
 *
 * Génère automatiquement :
 * - Getters
 * - Setters
 * - toString()
 * - equals()
 * - hashCode()
 *
 * Cela évite d’écrire du code boilerplate.
 */
@Data

/*
 * implements UserDetails
 *
 * Cette interface appartient à Spring Security.
 *
 * Elle permet d’utiliser cette entité directement
 * comme utilisateur authentifié dans Spring Security.
 *
 * Spring Security exige certaines méthodes obligatoires
 * pour gérer l'authentification.
 */
public class OurUsers implements UserDetails {

    /*
     * ================================
     * IDENTIFIANT UNIQUE
     * ================================
     */

    /*
     * @Id
     *
     * Indique que ce champ est la clé primaire.
     */
    @Id

    /*
     * @GeneratedValue(strategy = GenerationType.IDENTITY)
     *
     * Indique que la base de données génère automatiquement
     * la valeur de l'ID (auto-increment).
     *
     * GenerationType.IDENTITY :
     * Utilise l'auto-incrément natif de la base (MySQL, PostgreSQL, etc.)
     */
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    /*
     * ================================
     * INFORMATIONS UTILISATEUR
     * ================================
     */

    // Email de l'utilisateur (utilisé comme username pour login)
    private String email;

    // Nom complet
    private String name;

    // Mot de passe (stocké encodé avec BCrypt)
    private String password;

    // Ville
    private String city;


    /*
     * Rôle utilisateur
     *
     * Exemple :
     * ROLE_ADMIN
     * ROLE_USER
     *
     * Important : Spring Security fonctionne
     * généralement avec le préfixe "ROLE_"
     */
    private String role;

    /*
     * ======================================================
     * MÉTHODES OBLIGATOIRES DE UserDetails
     * ======================================================
     */

    /*
     * getAuthorities()
     *
     * Retourne la liste des rôles (permissions)
     * associés à l'utilisateur.
     *
     * GrantedAuthority :
     * Interface représentant une autorité accordée.
     *
     * SimpleGrantedAuthority :
     * Implémentation simple basée sur un String (ex: ROLE_ADMIN).
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        // On transforme le rôle String en objet GrantedAuthority
        return List.of(new SimpleGrantedAuthority(role));
    }


    /*
     * getUsername()
     *
     * Retourne le "username" utilisé pour l'authentification.
     *
     * Ici on utilise l'email comme identifiant unique.
     */
    @Override
    public String getUsername() {
        return email;
    }


    @Override
    public String getPassword() {
        return password;
    }


    /*
     * isAccountNonExpired()
     *
     * Indique si le compte n'est pas expiré.
     *
     * true = compte valide
     * false = compte expiré
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }


    /*
     * isAccountNonLocked()
     *
     * Indique si le compte n'est pas verrouillé.
     *
     * Exemple d’usage :
     * - Trop de tentatives de connexion échouées
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }


    /*
     * isCredentialsNonExpired()
     *
     * Indique si les credentials (mot de passe)
     * ne sont pas expirés.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }


    /*
     * isEnabled()
     *
     * Indique si le compte est activé.
     *
     * Peut être utilisé pour :
     * - Vérification email
     * - Désactivation admin
     */
    @Override
    public boolean isEnabled() {
        return true;
    }


}

