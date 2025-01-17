package sn.sonaged.login1.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/regionDashboard")
public class RegionDashboardController
{

    @GetMapping("/{regionId}")
    public String showRegionDashboard(@PathVariable Long regionId, Model model)
    {
        // Récupérer les données spécifiques à la région
        model.addAttribute("regionId", regionId);

        return "region-dashboard"; // Retourne la vue region-dashboard.html
    }
}