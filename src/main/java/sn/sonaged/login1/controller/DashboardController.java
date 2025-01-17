package sn.sonaged.login1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import sn.sonaged.login1.entities.Commune;
import sn.sonaged.login1.entities.User;
import sn.sonaged.login1.repository.UserRepository;


@Controller
public class DashboardController
{
    @Autowired
    UserRepository userRepository;

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication)
    {

        if (authentication != null && authentication.isAuthenticated())
        {
            // Récupérer l'utilisateur authentifié
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // Rediriger en fonction du rôle
            if (userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")))
            {
                return "dashboard";
            } else if (userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_SUPERADMIN")))
            {
                return "redirect:/superAdminDashboard";
            } else if (userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_RESPONSABLE_COMMUNAL")))
            {
                return "commune-dashboard";
            } else if (userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_RESPONSABLE_DEPARTEMENTAL")))
            {
                return "redirect:/departementDashboard";
            } else if (userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_RESPONSABLE_REGIONAL")))
            {
                return "redirect:/regionDashboard";
            } else
            {
                return "redirect:/access-denied"; // Rediriger vers une page d'accès refusé si le rôle n'est pas reconnu
            }
        } else
        {
            return "redirect:/login"; // Rediriger vers la page de connexion si l'utilisateur n'est pas authentifié
        }
    }
}