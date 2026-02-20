package com.elitepro.usermanagementsystem.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;

/**
 * Classe utilitaire pour :
 * - Générer des JWT (Access & Refresh)
 * - Extraire des informations d’un JWT
 * - Valider un JWT
 */
@Component
public class JWTUtils {

    // Clé secrète utilisée pour signer et vérifier les tokens
    private SecretKey key;

    /**Durée d’expiration du token (en millisecondes)
    / 86400000 ms = 24 heures*/
    private static final long EXPIRATION_TIME = 86400000;

    /**
     * Constructeur
     * Initialise la clé secrète utilisée pour la signature HMAC SHA256
     */
    public JWTUtils(){

        // Clé secrète sous forme de String (⚠ en production → mettre dans application.properties)
        String secreteString = "843567893696976453275974432697R634976R738467TR678T34865R6834R8763T478378637664538745673865783678548735687R3";

        // Conversion de la clé en tableau de bytes
        // Ici on suppose qu'elle est encodée en Base64
        byte[] keyBytes = Base64.getDecoder().decode(secreteString.getBytes(StandardCharsets.UTF_8));

        // Création d’une clé HMAC SHA256 à partir des bytes
        this.key = new SecretKeySpec(keyBytes, "HmacSHA256");
    }

    /**
     * Génère un Access Token
     *
     * @param userDetails informations de l’utilisateur authentifié
     * @return JWT signé
     */
    public String generateToken(UserDetails userDetails){

        return Jwts.builder()

                // Définit le "subject" (identité principale du token)
                .subject(userDetails.getUsername())

                // Date de création du token
                .issuedAt(new Date(System.currentTimeMillis()))

                // Date d’expiration (maintenant + durée définie)
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))

                // Signature du token avec la clé secrète
                .signWith(key)

                // Génère la chaîne finale du JWT
                .compact();
    }

    /**
     * Génère un Refresh Token avec des claims personnalisés
     *
     * @param claims informations supplémentaires à ajouter dans le payload
     * @param userDetails utilisateur concerné
     * @return JWT refresh signé
     */
    public String generateRefreshToken(HashMap<String, Object> claims, UserDetails userDetails){

        return Jwts.builder()

                // Ajout des claims personnalisés
                .claims(claims)

                // Définit le subject (email / username)
                .subject(userDetails.getUsername())

                // Date de création
                .issuedAt(new Date(System.currentTimeMillis()))

                // Date d’expiration
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))

                // Signature
                .signWith(key)

                // Finalisation du token
                .compact();
    }

    /**
     * Extrait le username (subject) depuis un JWT
     *
     * @param token JWT
     * @return username contenu dans le token
     */
    public String extractUsername(String token){
        return extractClaims(token, Claims::getSubject);
    }

    /**
     * Méthode générique pour extraire n’importe quel claim du token
     *
     * @param token JWT
     * @param claimsTFunction fonction permettant d’extraire un champ spécifique
     * @param <T> type de retour (String, Date, etc.)
     * @return valeur du claim demandé
     */
    private <T> T extractClaims(String token, Function<Claims, T> claimsTFunction){

        // Parser le token
        // Vérifier la signature avec la clé
        // Extraire le payload
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        // Appliquer la fonction demandée (ex: getSubject, getExpiration)
        return claimsTFunction.apply(claims);
    }

    /**
     * Vérifie si le token est valide pour un utilisateur donné
     *
     * Conditions :
     * - Username correspond
     * - Token non expiré
     */
    public boolean isTokenValid(String token, UserDetails userDetails){

        final String username = extractUsername(token);

        return (username.equals(userDetails.getUsername())
                && !isTokenExpired(token));
    }

    /**
     * Vérifie si le token est expiré
     *
     * @param token JWT
     * @return true si expiré
     */
    public boolean isTokenExpired(String token){

        // On extrait la date d’expiration et on compare à la date actuelle
        return extractClaims(token, Claims::getExpiration)
                .before(new Date());
    }
}










//package com.elitepro.usermanagementsystem.service;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Component;
//
//import javax.crypto.SecretKey;
//import javax.crypto.spec.SecretKeySpec;
//import java.nio.charset.StandardCharsets;
//import java.util.Base64;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.function.Function;
//
//@Component
//public class JWTUtils {
////    @Value("${jwt.secret}")
////    private String secret;
////    @Value("${jwt.expiration}")
////    private long expiration;
//    private SecretKey key;
//    private static final long EXPIRATION_TIME = 86400000; //24 heures
//
//    public JWTUtils(){
//        String secreteString = "843567893696976453275974432697R634976R738467TR678T34865R6834R8763T478378637664538745673865783678548735687R3";
//        byte[] keyBytes = Base64.getDecoder().decode(secreteString.getBytes(StandardCharsets.UTF_8));
//        this.key = new SecretKeySpec(keyBytes, "HmacSHA256");
//    }
//
//    public String generateToken(UserDetails userDetails){
//        return Jwts.builder()  //builder() permet de construire un token JWT étape par étape.
//                .subject(userDetails.getUsername())
//                .issuedAt(new Date(System.currentTimeMillis())) //C’est la date de création du token
//                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) //Cela signifie que le token expirera dans 1 heure
//                .signWith(key) /*key est ta clé secrète HMAC SHA256 (définie dans ton constructeur)
//                Le token est signé avec HS256. Cela garantit :que le token n’a pas été modifié
//                que seul ton serveur peut le générer*/
//                .compact();  //Transforme tout ça en une chaîne JWT finale.
//    }
//
//    public  String generateRefreshToken(HashMap<String, Object> claims, UserDetails userDetails){
//        return Jwts.builder()
//                .claims(claims)
//                .subject(userDetails.getUsername())
//                .issuedAt(new Date(System.currentTimeMillis()))
//                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
//                .signWith(key)
//                .compact();
//    }
//
//    public  String extractUsername(String token){
//        return  extractClaims(token, Claims::getSubject);
//    }
//
//    private <T> T extractClaims(String token, Function<Claims, T> claimsTFunction){
//        return claimsTFunction.apply(Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload());
//    }
//
//    public  boolean isTokenValid(String token, UserDetails userDetails){
//        final String username = extractUsername(token);
//        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
//    }
//
//    public  boolean isTokenExpired(String token){
//        return extractClaims(token, Claims::getExpiration).before(new Date());
//    }
//}
