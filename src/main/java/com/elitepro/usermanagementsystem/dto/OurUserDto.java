package com.elitepro.usermanagementsystem.dto;

/*
 * Package dto :
 * DTO = Data Transfer Object
 *
 * Cette classe sert à transporter les données
 * entre le Controller et le Service.
 *
 * Elle est utilisée :
 * - Pour les requêtes (Request)
 * - Pour les réponses (Response)
 *
 * Ici OurUserDto signifie : Request / Response combiné.
 */

import com.elitepro.usermanagementsystem.entity.OurUsers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;

import java.util.List;


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
 * Évite d’écrire du code répétitif.
 */
@Data

/*
 * @JsonInclude(JsonInclude.Include.NON_NULL)
 *
 * Annotation Jackson.
 *
 * Indique que les champs ayant la valeur null
 * ne seront PAS inclus dans la réponse JSON.
 *
 * Exemple :
 * Si "error" est null,
 * il ne sera pas visible dans la réponse.
 *
 * 👉 Rend les réponses API plus propres.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)

/*
 * @JsonIgnoreProperties(ignoreUnknown = true)
 *
 * Lors de la désérialisation (JSON → Objet Java),
 * si le JSON contient des champs inconnus,
 * ils seront ignorés au lieu de provoquer une erreur.
 *
 * Utile pour :
 * - Compatibilité frontend
 * - Évolution de l’API
 */
@JsonIgnoreProperties(ignoreUnknown = true)

public class OurUserDto {

    /*
     * ================================
     * CHAMPS DE STATUT API
     * ================================
     */

    // Code de statut personnalisé (200, 404, 500, etc.)
    private int statusCode;

    // Message d'erreur si une exception survient
    private String error;

    // Message général (succès, info, etc.)
    private String message;


    /*
     * ================================
     * CHAMPS JWT (AUTHENTIFICATION)
     * ================================
     */

    // Token JWT principal (Access Token)
    private String token;

    // Refresh Token pour générer un nouveau JWT
    private String refreshToken;

    // Temps d’expiration du token (ex: 24Hrs)
    private String expirationTime;


    /*
     * ================================
     * INFORMATIONS UTILISATEUR
     * ================================
     */

    // Nom de l'utilisateur
    private String name;

    // Ville
    private String city;

    // Rôle (ROLE_USER, ROLE_ADMIN)
    private String role;

    // Email (utilisé comme username)
    private String email;

    // Mot de passe (utilisé uniquement lors du login/register)
    // ⚠️ Ne doit jamais être renvoyé en réponse !
    private String password;


    /*
     * ================================
     * OBJETS COMPLETS UTILISATEUR
     * ================================
     */

    /*
     * Objet utilisateur unique.
     *
     * Utilisé pour :
     * - Retourner un utilisateur spécifique
     * - Retourner les infos du profil connecté
     */
    private OurUsers ourUsers;


    /*
     * Liste d'utilisateurs.
     *
     * Utilisé pour :
     * - Endpoint getAllUsers()
     */
    private List<OurUsers> ourUsersList;

}

