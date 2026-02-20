package com.elitepro.usermanagementsystem.service;

/*
 * Package service :
 * Contient la couche métier (Business Logic).
 * Cette couche fait le lien entre le Controller et le Repository.
 */

import com.elitepro.usermanagementsystem.dto.OurUserDto;
import com.elitepro.usermanagementsystem.entity.OurUsers;
import com.elitepro.usermanagementsystem.repository.OurUsersRepository;
import com.elitepro.usermanagementsystem.utils.JWTUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/*
 * @Service
 *
 * Annotation Spring qui indique que cette classe est un composant métier.
 *
 * Elle permet :
 * - La détection automatique par Spring (Component Scan)
 * - L'injection automatique dans d'autres classes (@Autowired)
 * - La séparation claire entre logique métier et contrôleurs
 *
 * @Service est une spécialisation de @Component.
 */
@Service
public class UsersManagementService {

    /*
     * @Autowired
     *
     * Permet à Spring d'injecter automatiquement la dépendance.
     * Spring va chercher un Bean correspondant dans son contexte.
     */

    @Autowired
    private OurUsersRepository ourUsersRepository; // Accès base de données

    @Autowired
    private JWTUtils jwtUtils; // Classe utilitaire pour générer et valider les JWT

    @Autowired
    private AuthenticationManager authenticationManager;
    // Utilisé pour authentifier l'utilisateur (email + password)

    @Autowired
    private PasswordEncoder passwordEncoder;
    // Encode les mots de passe (ex: BCrypt) pour la sécurité


    /*
     * ================================
     * MÉTHODE REGISTER (INSCRIPTION)
     * ================================
     */
    public OurUserDto register(OurUserDto registrationRequest) {

        OurUserDto resp = new OurUserDto();

        try {
            // Création d'un nouvel objet utilisateur
            OurUsers ourUser = new OurUsers();

            // Mapping des données reçues du DTO vers l'entité
            ourUser.setEmail(registrationRequest.getEmail());
            ourUser.setCity(registrationRequest.getCity());
            ourUser.setRole(registrationRequest.getRole());
            ourUser.setName(registrationRequest.getName());

            // Encodage du mot de passe avant sauvegarde
            ourUser.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));

            // Sauvegarde en base
            OurUsers ourUsersResult = ourUsersRepository.save(ourUser);

            // Vérification que l'utilisateur a bien été sauvegardé
            if (ourUsersResult.getId() > 0) {
                resp.setOurUsers(ourUsersResult);
                resp.setMessage("User Saved Successfully");
                resp.setStatusCode(200);
            }

        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setError(e.getMessage());
        }

        return resp;
    }


    /*
     * ================================
     * MÉTHODE LOGIN (AUTHENTIFICATION)
     * ================================
     */
    public OurUserDto login(OurUserDto loginRequest) {

        OurUserDto response = new OurUserDto();

        try {
            /*
             * AuthenticationManager :
             * Vérifie si email + password sont corrects.
             *
             * UsernamePasswordAuthenticationToken :
             * Objet contenant les credentials.
             */
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            // Récupération de l'utilisateur en base
            var user = ourUsersRepository
                    .findByEmail(loginRequest.getEmail())
                    .orElseThrow();

            // Génération du JWT principal
            var jwt = jwtUtils.generateToken(user);

            // Génération du Refresh Token
            var refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), user);

            response.setStatusCode(200);
            response.setToken(jwt);
            response.setRole(user.getRole());
            response.setRefreshToken(refreshToken);
            response.setExpirationTime("24Hrs");
            response.setMessage("Successfully Logged In");

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
        }

        return response;
    }


    /*
     * ================================
     * MÉTHODE REFRESH TOKEN
     * ================================
     */
    public OurUserDto refreshToken(OurUserDto refreshTokenRequest) {

        OurUserDto response = new OurUserDto();

        try {
            // Extraction de l'email contenu dans le token
            String ourEmail = jwtUtils.extractUsername(refreshTokenRequest.getToken());

            // Récupération utilisateur
            OurUsers users = ourUsersRepository
                    .findByEmail(ourEmail)
                    .orElseThrow();

            // Vérification validité du refresh token
            if (jwtUtils.isTokenValid(refreshTokenRequest.getToken(), users)) {

                // Génération d’un nouveau access token
                var jwt = jwtUtils.generateToken(users);

                response.setStatusCode(200);
                response.setToken(jwt);
                response.setRefreshToken(refreshTokenRequest.getToken());
                response.setExpirationTime("24Hr");
                response.setMessage("Successfully Refreshed Token");
            }

            return response;

        } catch (Exception e) {
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
            return response;
        }
    }


    /*
     * ================================
     * RÉCUPÉRER TOUS LES UTILISATEURS
     * ================================
     */
    public OurUserDto getAllUsers() {

        OurUserDto ourUserDto = new OurUserDto();

        try {
            List<OurUsers> result = ourUsersRepository.findAll();

            if (!result.isEmpty()) {
                ourUserDto.setOurUsersList(result);
                ourUserDto.setStatusCode(200);
                ourUserDto.setMessage("Successful");
            } else {
                ourUserDto.setStatusCode(404);
                ourUserDto.setMessage("No users found");
            }

        } catch (Exception e) {
            ourUserDto.setStatusCode(500);
            ourUserDto.setMessage("Error occurred: " + e.getMessage());
        }

        return ourUserDto;
    }


    /*
     * ================================
     * RÉCUPÉRER UTILISATEUR PAR ID
     * ================================
     */
    public OurUserDto getUsersById(Integer id) {

        OurUserDto ourUserDto = new OurUserDto();

        try {
            OurUsers usersById = ourUsersRepository
                    .findById(id)
                    .orElseThrow(() -> new RuntimeException("User Not found"));

            ourUserDto.setOurUsers(usersById);
            ourUserDto.setStatusCode(200);
            ourUserDto.setMessage("Users with id '" + id + "' found successfully");

        } catch (Exception e) {
            ourUserDto.setStatusCode(500);
            ourUserDto.setMessage("Error occurred: " + e.getMessage());
        }

        return ourUserDto;
    }


    /*
     * ================================
     * SUPPRESSION UTILISATEUR
     * ================================
     */
    public OurUserDto deleteUser(Integer userId) {

        OurUserDto ourUserDto = new OurUserDto();

        try {
            Optional<OurUsers> userOptional = ourUsersRepository.findById(userId);

            if (userOptional.isPresent()) {
                ourUsersRepository.deleteById(userId);
                ourUserDto.setStatusCode(200);
                ourUserDto.setMessage("User deleted successfully");
            } else {
                ourUserDto.setStatusCode(404);
                ourUserDto.setMessage("User not found for deletion");
            }

        } catch (Exception e) {
            ourUserDto.setStatusCode(500);
            ourUserDto.setMessage("Error occurred while deleting user: " + e.getMessage());
        }

        return ourUserDto;
    }


    /*
     * ================================
     * MISE À JOUR UTILISATEUR
     * ================================
     */
    public OurUserDto updateUser(Integer userId, OurUsers updatedUser) {

        OurUserDto ourUserDto = new OurUserDto();

        try {
            Optional<OurUsers> userOptional = ourUsersRepository.findById(userId);

            if (userOptional.isPresent()) {

                OurUsers existingUser = userOptional.get();

                // Mise à jour des champs
                existingUser.setEmail(updatedUser.getEmail());
                existingUser.setName(updatedUser.getName());
                existingUser.setCity(updatedUser.getCity());
                existingUser.setRole(updatedUser.getRole());

                // Mise à jour du mot de passe seulement si présent
                if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                    existingUser.setPassword(
                            passwordEncoder.encode(updatedUser.getPassword())
                    );
                }

                OurUsers savedUser = ourUsersRepository.save(existingUser);

                ourUserDto.setOurUsers(savedUser);
                ourUserDto.setStatusCode(200);
                ourUserDto.setMessage("User updated successfully");

            } else {
                ourUserDto.setStatusCode(404);
                ourUserDto.setMessage("User not found for update");
            }

        } catch (Exception e) {
            ourUserDto.setStatusCode(500);
            ourUserDto.setMessage("Error occurred while updating user: " + e.getMessage());
        }

        return ourUserDto;
    }


    /*
     * ================================
     * RÉCUPÉRER MES INFORMATIONS
     * ================================
     */
    public OurUserDto getMyInfo(String email) {

        OurUserDto ourUserDto = new OurUserDto();

        try {
            Optional<OurUsers> userOptional = ourUsersRepository.findByEmail(email);

            if (userOptional.isPresent()) {
                ourUserDto.setOurUsers(userOptional.get());
                ourUserDto.setStatusCode(200);
                ourUserDto.setMessage("successful");
            } else {
                ourUserDto.setStatusCode(404);
                ourUserDto.setMessage("User not found");
            }

        } catch (Exception e) {
            ourUserDto.setStatusCode(500);
            ourUserDto.setMessage("Error occurred while getting user info: " + e.getMessage());
        }

        return ourUserDto;
    }
}

