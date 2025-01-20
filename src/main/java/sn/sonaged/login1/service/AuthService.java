package sn.sonaged.login1.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sn.sonaged.login1.util.JwtUtil;
import sn.sonaged.login1.entities.User;
import sn.sonaged.login1.repository.UserRepository;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService
{

    private  UserRepository userRepository;
    private  JwtUtil jwtUtil;
    private  PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder)
    {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    public String authenticate(String matricule, String password) throws Exception
    {
        User user = userRepository.findByMatricule(matricule)
                .orElseThrow(() -> new Exception("User not found with matricule: " + matricule));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new Exception("Incorrect password");
        }

        // Utilisez les autorités de l'utilisateur
        return jwtUtil.generateToken(user);
    }

 /*

    // Méthode pour réinitialiser le mot de passe
    public void resetPassword(String email)
    {
        // Trouver l'utilisateur par email
        User user = (User) userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        // Générer un token de réinitialisation
        String resetToken = UUID.randomUUID().toString();
        user.setResetToken(resetToken);
        userRepository.save(user);

        // Envoyer un email avec le lien de réinitialisation (à implémenter)
        sendResetPasswordEmail(user.getEmail(), resetToken);
    }

    // Méthode pour envoyer un email de réinitialisation (simulée ici)
    private void sendResetPasswordEmail(String email, String resetToken) {
        // Simuler l'envoi d'un email
        System.out.println("Reset password link sent to: " + email);
        System.out.println("Reset token: " + resetToken);
    }

*/
    /**
     * Récupère le rôle de l'utilisateur.
     */
    public String getUserRole(String matricule) throws Exception
    {
        User user = userRepository.findByMatricule(matricule)
                .orElseThrow(() -> new Exception("Utilisateur non trouvé"));

        return user.getRole().name(); // Supposons que le rôle est stocké dans l'entité User
    }

    public User getUserByMatricule(String matricule)
    {
        return userRepository.findByMatricule(matricule)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with matricule: " + matricule));
    }
}