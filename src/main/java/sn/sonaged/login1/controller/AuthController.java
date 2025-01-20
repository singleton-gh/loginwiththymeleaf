package sn.sonaged.login1.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailSendException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import sn.sonaged.login1.entities.Role;
import sn.sonaged.login1.entities.User;
import sn.sonaged.login1.exception.CustomAuthenticationException;
import sn.sonaged.login1.repository.UserRepository;
import sn.sonaged.login1.service.AuthService;
import sn.sonaged.login1.service.PasswordResetService;
import sn.sonaged.login1.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Controller
public class AuthController
{

    private final UserService userService;
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final PasswordResetService passwordResetService;

    Logger log = LoggerFactory.getLogger(AuthController.class);

    public AuthController(UserService userService, AuthService authService, PasswordEncoder passwordEncoder, UserRepository userRepository, PasswordResetService passwordResetService)
    {
        this.userService = userService;
        this.authService = authService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.passwordResetService = passwordResetService;
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
    public String resetPassword()
    {
        log.info("Affichage de la page d'inscription");
        return "reset-password";
    }

    // Traite la soumission du formulaire de réinitialisation
    @PostMapping("/auth/reset-password")
    public String resetPassword(@RequestParam String email, @RequestParam String matricule, Model model)
    {
        // Valider l'email avant de continuer
        if (!isValidEmail(email))
        {
            model.addAttribute("error", "L'adresse email fournie est invalide.");
            return "reset-password"; // Retourne à la page de réinitialisation avec un message d'erreur
        }

        // Vérifier si l'utilisateur existe dans la base de données
        User user = userService.findByEmailAndMatricule(email, matricule);
        if (user == null)
        {
            model.addAttribute("error", "Aucun utilisateur trouvé avec cet email et ce matricule.");
            return "reset-password";
        }

        // Envoyer le lien de réinitialisation par email
        try
        {
            passwordResetService.sendResetLink(email);
            model.addAttribute("success", "Un lien de réinitialisation a été envoyé à votre adresse email.");
        } catch (MailSendException e)
        {
            model.addAttribute("error", "Une erreur s'est produite lors de l'envoi de l'email. Veuillez réessayer.");
            log.error("Échec de l'envoi de l'email à l'adresse : " + email, e);
        }
        return "reset-password";
    }

    // Méthode pour valider l'email
    private boolean isValidEmail(String email)
    {
        String regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }


    @GetMapping("/auth/reset-password")
    public String showResetPasswordForm(@RequestParam String token, Model model)
    {
        if (passwordResetService.isTokenValid(token))
        {
            model.addAttribute("token", token);
            return "update-password";
        } else
        {
            log.error("Token invalide ou expiré : " + token);
            model.addAttribute("error", "Invalid or expired token.");
            return "login";
        }
    }


    /*
    @PostMapping("/auth/update-password")
    public String updatePassword(@RequestParam("token") String token,@RequestParam("newPassword") String newPassword,Model model)
    {
        try
        {
            boolean updated = passwordResetService.updatePassword(token, newPassword);
            if (updated)
            {
                model.addAttribute("message", "Le mot de passe a été mis à jour avec succès.");
            } else
            {
                model.addAttribute("error", "La mise à jour du mot de passe a échoué.");
            }
        } catch (CustomAuthenticationException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "update-password";
    }


     */

    @PostMapping("/auth/update-password")
    public String updatePassword(@RequestParam("token") String token,
                                 @RequestParam("newPassword") String newPassword,
                                 @RequestParam("confirmPassword") String confirmPassword,
                                 RedirectAttributes redirectAttributes) {
        // Vérifier que les mots de passe correspondent
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Les mots de passe ne correspondent pas.");
            return "redirect:/auth/reset-password?token=" + token;
        }

        // Vérifier la longueur du mot de passe
        if (newPassword.length() < 8) {
            redirectAttributes.addFlashAttribute("error", "Le mot de passe doit contenir au moins 8 caractères.");
            return "redirect:/auth/reset-password?token=" + token;
        }

        try {
            // Extraire le matricule du token
            String matricule = passwordResetService.extractMatricule(token);

            // Récupérer l'utilisateur à partir du matricule
            User user = userRepository.findByMatricule(matricule)
                    .orElseThrow(() -> new CustomAuthenticationException("Utilisateur non trouvé avec le matricule : " + matricule));

            // Mettre à jour le mot de passe
            boolean updated = passwordResetService.updatePassword(token, newPassword);
            if (updated) {
                // Envoyer un email de confirmation
                passwordResetService.sendPasswordUpdateConfirmation(user.getEmail()); // Utiliser l'email de l'utilisateur

                // Ajouter un message de succès
                redirectAttributes.addFlashAttribute("message", "Mot de passe mis à jour. Un email de confirmation vous a été envoyé.");
                return "redirect:/login"; // Rediriger vers la page de connexion
            } else {
                redirectAttributes.addFlashAttribute("error", "La mise à jour du mot de passe a échoué.");
                return "redirect:/auth/reset-password?token=" + token;
            }
        } catch (CustomAuthenticationException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/auth/reset-password?token=" + token;
        }
    }

}