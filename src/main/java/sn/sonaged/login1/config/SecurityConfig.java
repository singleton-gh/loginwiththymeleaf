package sn.sonaged.login1.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
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
                .csrf(AbstractHttpConfigurer::disable) // Désactiver CSRF pour les formulaires simples
                .authorizeHttpRequests
                        (auth -> auth
                        .requestMatchers("/register", "/login", "/auth/login", "/css/**", "/js/**").permitAll() // Autoriser l'accès public
                        .anyRequest().authenticated() // Toutes les autres requêtes nécessitent une authentification
                         )
                .formLogin(form -> form
                        .loginPage("/login") // Page de connexion personnalisée
                        .defaultSuccessUrl
                            ("/dashboard", true) // Redirection après une connexion réussie
                            .permitAll()
                             )
                .logout
                    (logout -> logout
                    .logoutSuccessUrl("/login") // Redirection après une déconnexion
                    .permitAll()
                    )
                .sessionManagement
                    (session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // Crée une session si nécessaire
                    );

        return http.build();
    }

    @Bean
        public PasswordEncoder passwordEncoder()
        {
            return new BCryptPasswordEncoder();
        }

    @Bean
        public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception
        {
            return authenticationConfiguration.getAuthenticationManager();
        }
}