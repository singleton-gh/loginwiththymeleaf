package sn.sonaged.login1.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/departementDashboard")
public class DepartementDashboardController
{

    @GetMapping("/{departementId}")
    public String showDepartementDashboard(@PathVariable Long departementId, Model model) {
        // Récupérer les données spécifiques au département
        model.addAttribute("departementId", departementId);
        return "departement-dashboard"; // Retourne la vue departement-dashboard.html
    }
}