package sn.sonaged.login1.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.web.SecurityFilterChain;





@Configuration
@EnableWebSecurity
public class SecurityConfig
{

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
    {
        http
                .csrf(AbstractHttpConfigurer::disable) // Désactiver CSRF pour les API stateless
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/register", "/login","/reset", "/auth/login", "/auth/reset-password", "/auth/update-password", "/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
                        .requestMatchers("/dashboard").authenticated() // Tous les utilisateurs authentifiés peuvent accéder à /dashboard
                        .requestMatchers("/admin/**").hasAnyRole("ADMIN", "SUPERADMIN") // Seuls ADMIN et SUPERADMIN peuvent accéder à /admin/**
                        .requestMatchers("/commune/**").hasRole("RESPONSABLE_COMMUNAL") // Seuls RESPONSABLE_COMMUNAL peuvent accéder à /commune/**
                        .requestMatchers("/departement/**").hasRole("RESPONSABLE_DEPARTEMENTAL") // Seuls RESPONSABLE_DEPARTEMENTAL peuvent accéder à /departement/**
                        .requestMatchers("/region/**").hasRole("RESPONSABLE_REGIONAL") // Seuls RESPONSABLE_REGIONAL peuvent accéder à /region/**
                        .anyRequest().authenticated() // Toutes les autres routes nécessitent une authentification
                )
                .formLogin(form -> form
                        .loginPage("/login") // Page de connexion personnalisée
                        .loginProcessingUrl("/auth/login") // URL de traitement de la connexion
                        .usernameParameter("matricule") // Utiliser "matricule" comme identifiant
                        .passwordParameter("password") // Utiliser "password" comme mot de passe
                        .defaultSuccessUrl("/dashboard", true) // Redirection après une connexion réussie
                        .failureUrl("/login?error=true") // Redirection en cas d'échec de la connexion
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout") // URL de déconnexion
                        .logoutSuccessUrl("/login?logout=true") // Redirection après une déconnexion réussie
                        .invalidateHttpSession(true) // Invalider la session
                        .deleteCookies("JSESSIONID") // Supprimer les cookies de session
                        .permitAll()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // Crée une session si nécessaire
                        .invalidSessionUrl("/login?invalid-session=true") // Rediriger en cas de session invalide
                        .maximumSessions(1) // Limite à une session par utilisateur
                        .maxSessionsPreventsLogin(false) // Permettre une nouvelle connexion et déconnecter l'ancienne session
                )
                .securityContext(securityContext -> securityContext
                        .requireExplicitSave(true) // Forcer la sauvegarde explicite du contexte de sécurité
                );

        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder(); // Utilisation de BCrypt pour le hachage des mots de passe
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception
    {
        return authenticationConfiguration.getAuthenticationManager(); // Gestionnaire d'authentification
    }


    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder)
    {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }
}