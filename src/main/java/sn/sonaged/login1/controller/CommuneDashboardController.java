package sn.sonaged.login1.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/communeDashboard")
public class CommuneDashboardController
{

    @GetMapping("/{communeId}")
    public String showCommuneDashboard(@PathVariable Long communeId, Model model)
    {
        // Récupérer les données spécifiques à la commune
        model.addAttribute("communeId", communeId);
        return "commune-dashboard"; // Retourne la vue commune-dashboard.html
    }
}