package com.elitepro.usermanagementsystem.controller;

/*
 * Package controller :
 * Contient les classes qui exposent les endpoints REST.
 * Cette couche reçoit les requêtes HTTP du client (Angular, Postman, etc.)
 * et délègue le traitement à la couche Service.
 */

import com.elitepro.usermanagementsystem.dto.OurUserDto;
import com.elitepro.usermanagementsystem.entity.OurUsers;
import com.elitepro.usermanagementsystem.service.UsersManagementService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


/*
 * @RestController
 *
 * Annotation composée de :
 * - @Controller
 * - @ResponseBody
 *
 * Elle indique que cette classe expose des endpoints REST
 * et que les méthodes retournent directement des objets JSON
 * (pas des vues JSP/Thymeleaf).
 */
@RestController
public class UserManagementController {

    /*
     * @Autowired
     *
     * Injection automatique du service métier.
     * Spring va injecter automatiquement l’instance
     * de UsersManagementService au démarrage.
     */
    @Autowired
    private UsersManagementService usersManagementService;


    /*
     * ================================
     * ENDPOINT INSCRIPTION
     * ================================
     *
     * @PostMapping
     * Indique que cette méthode répond aux requêtes HTTP POST.
     *
     * URL : /auth/register
     *
     * @RequestBody
     * Convertit automatiquement le JSON reçu en objet Java (OurUserDto).
     */
    @PostMapping("/auth/register")
    public ResponseEntity<OurUserDto> register(@RequestBody OurUserDto reg){

        /*
         * ResponseEntity :
         * Permet de contrôler :
         * - Le status HTTP
         * - Le body de la réponse
         * - Les headers si nécessaire
         */
        return ResponseEntity.ok(usersManagementService.register(reg));
    }


    /*
     * ================================
     * ENDPOINT LOGIN
     * ================================
     */
    @PostMapping("/auth/login")
    public ResponseEntity<OurUserDto> login(@RequestBody OurUserDto req){
        return ResponseEntity.ok(usersManagementService.login(req));
    }


    /*
     * ================================
     * ENDPOINT REFRESH TOKEN
     * ================================
     *
     * Permet d’obtenir un nouveau JWT
     * en utilisant un refresh token valide.
     */
    @PostMapping("/auth/refresh")
    public ResponseEntity<OurUserDto> refreshToken(@RequestBody OurUserDto req){
        return ResponseEntity.ok(usersManagementService.refreshToken(req));
    }


    /*
     * ================================
     * RÉCUPÉRER TOUS LES UTILISATEURS
     * ================================
     *
     * GET request
     *
     * URL : /admin/get-all-users
     *
     * Généralement protégé par ROLE_ADMIN
     * dans la configuration Spring Security.
     */
    @GetMapping("/admin/get-all-users")
    public ResponseEntity<OurUserDto> getAllUsers(){
        return ResponseEntity.ok(usersManagementService.getAllUsers());
    }


    /*
     * ================================
     * RÉCUPÉRER UTILISATEUR PAR ID
     * ================================
     *
     * @PathVariable
     * Permet de récupérer la valeur dans l’URL.
     *
     * Exemple :
     * /admin/get-users/5
     * userId = 5
     */
    @GetMapping("/admin/get-users/{userId}")
    public ResponseEntity<OurUserDto> getUserByID(@PathVariable Integer userId){
        return ResponseEntity.ok(usersManagementService.getUsersById(userId));
    }


    /*
     * ================================
     * METTRE À JOUR UTILISATEUR
     * ================================
     *
     * @PutMapping
     * Utilisé pour modifier une ressource existante.
     *
     * @RequestBody :
     * Convertit le JSON en objet OurUsers.
     */

    @PutMapping("/admin/update/{userId}")
    public ResponseEntity<OurUserDto> updateUser(
            @PathVariable Integer userId,
            @RequestBody OurUsers reqres,
            Authentication authentication){

        return ResponseEntity.ok(
                usersManagementService.updateUser(userId, reqres, authentication)
        );
    }



//    @PutMapping("/admin/update/{userId}")
//    public ResponseEntity<OurUserDto> updateUser(
//            @PathVariable Integer userId,
//            @RequestBody OurUsers reqres){
//
//        return ResponseEntity.ok(
//                usersManagementService.updateUser(userId, reqres)
//        );
//    }


    /*
     * ================================
     * RÉCUPÉRER PROFIL CONNECTÉ
     * ================================
     *
     * URL : /adminuser/get-profile
     *
     * Ici on récupère l'utilisateur actuellement authentifié
     * grâce au SecurityContextHolder.
     */
    @GetMapping("/adminuser/get-profile")
    public ResponseEntity<OurUserDto> getMyProfile(){

        /*
         * SecurityContextHolder :
         * Contient les informations de sécurité
         * de l’utilisateur actuellement connecté.
         */
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        /*
         * getName() retourne généralement :
         * - l'email
         * - ou le username
         * selon la configuration de UserDetails.
         */
        String email = authentication.getName();

        // Appel du service pour récupérer les infos utilisateur
        OurUserDto response = usersManagementService.getMyInfo(email);

        /*
         * On retourne le status HTTP défini dans le service.
         */
        return ResponseEntity
                .status(response.getStatusCode())
                .body(response);
    }


    /*
     * ================================
     * SUPPRIMER UTILISATEUR
     * ================================
     *
     * @DeleteMapping
     * Utilisé pour supprimer une ressource.
     */
    @DeleteMapping("/admin/delete/{userId}")
    public ResponseEntity<OurUserDto> deleteUser(@PathVariable Integer userId){
        return ResponseEntity.ok(
                usersManagementService.deleteUser(userId)
        );
    }

}

