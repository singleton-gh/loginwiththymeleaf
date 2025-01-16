package sn.sonaged.login1.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import sn.sonaged.login1.entities.User;


@Controller
public class DashboardController
{

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication)
    {
        User user = (User) authentication.getPrincipal();
        switch (user.getRole())
        {
            case RESPONSABLE_COMMUNAL:
                return "redirect:/dashboard";
            case RESPONSABLE_DEPARTEMENTAL:
                return "redirect:/departement-dashboard";
            case RESPONSABLE_REGIONAL:
                return "redirect:/region-dashboard";
            default:
                return "redirect:/admin-dashboard";
        }
    }
}