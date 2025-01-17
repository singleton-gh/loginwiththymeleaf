package sn.sonaged.login1.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import sn.sonaged.login1.entities.Role;
import sn.sonaged.login1.entities.User;
import sn.sonaged.login1.repository.UserRepository;
import sn.sonaged.login1.service.AuthService;
import sn.sonaged.login1.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;



@Controller
public class AuthController
{

    private final UserService userService;
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;


    Logger log = LoggerFactory.getLogger(AuthController.class);

    public AuthController(AuthService authService, UserService userService, UserRepository userRepository, PasswordEncoder passwordEncoder)
    {
        this.authService = authService;
        this.userService = userService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Affiche la page de connexion.
     */
    @GetMapping("/login")
    public String showLoginPage(@RequestParam(required = false) String error, Model model)
    {

        if (error != null)
        {
log.warn("Tentative de connexion échouée"); // Log un avertissement
            model.addAttribute("error", "Identifiants incorrects");
        }
log.info("Affichage de la page de connexion"); // Log une information
        return "login"; // Retourne la vue login.html
    }


    @PostMapping("/auth/login")
    public String handleLogin(@RequestParam String matricule, @RequestParam String password, Model model)
    {
        try
        {
            // Authentification de l'utilisateur
            String token = authService.authenticate(matricule, password);
            log.info("Token généré pour l'utilisateur : {}", matricule);

            // Redirection en fonction du rôle
            User user = userRepository.findByMatricule(matricule)
                    .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé"));

            switch (user.getRole())
            {
                case RESPONSABLE_COMMUNAL:
                    return "redirect:/communeDashboard/" + user.getCommune().getId();
                case RESPONSABLE_DEPARTEMENTAL:
                    return "redirect:/departementDashboard/" + user.getDepartement().getId();
                case RESPONSABLE_REGIONAL:
                    return "redirect:/regionDashboard/" + user.getRegion().getId();
                case ADMIN:
                    return "redirect:/adminDashboard";
                case SUPERADMIN:
                    return "redirect:/superAdminDashboard";
                default:
                    return "redirect:/access-denied"; // Rediriger vers une page d'accès refusé si le rôle n'est pas reconnu
            }
        } catch (Exception e)
        {
            log.error("Erreur lors de la connexion : {}", e.getMessage());
            model.addAttribute("error", "Identifiants incorrects");
            return "redirect:/login?error=true";
        }
    }
    /**
     * Affiche la page d'inscription.
     */
    @GetMapping("/register")
    public String showRegisterPage()
    {
log.info("Affichage de la page d'inscription"); // Log une information
        return "register"; // Retourne la vue register.html
    }

    /**
     * Traite la soumission du formulaire d'inscription.
     */
    @PostMapping("/register")
    public String handleRegister(
            @RequestParam String matricule,
            @RequestParam String password,
            @RequestParam String roleString, // Rôle passé en paramètre
            Model model)
    {
        // Vérifier si l'utilisateur existe déjà
        if (userRepository.findByMatricule(matricule).isPresent())
        {
            model.addAttribute("error", "Ce matricule est déjà utilisé.");
            return "register";
        }

        // Convertir la chaîne en Role
        Role role;
        try
        {
            role = Role.valueOf(roleString);
            log.info("Rôle converti avec succès : {}", role);
        } catch (IllegalArgumentException e) 
        {
            log.error("Rôle invalide : {}", roleString);
            model.addAttribute("error", "Rôle invalide.");
            return "register";
        }

        // Créer un nouvel utilisateur
        User user = new User();
        user.setMatricule(matricule);
        user.setPassword(passwordEncoder.encode(password)); // Encoder le mot de passe
        user.setRole(Role.valueOf(roleString)); // Définir le rôle

        // Sauvegarder l'utilisateur dans la base de données
        userRepository.save(user);
        log.info("Utilisateur enregistré avec succès : {}", matricule);

        // Rediriger vers la page de connexion
        return "redirect:/login";
    }
    
    
    @GetMapping("/reset")
    public String resetPass()
    {
        log.info("Affichage de la page d'inscription");
        return "reset-password";
    }
}