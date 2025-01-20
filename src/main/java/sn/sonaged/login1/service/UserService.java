package sn.sonaged.login1.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sn.sonaged.login1.entities.User;
import sn.sonaged.login1.repository.UserRepository;

import java.util.UUID;

@Service
public class UserService
{


    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder)
    {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void resetPassword(String email)
    {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        String resetToken = UUID.randomUUID().toString();
        user.setResetToken(resetToken);
        userRepository.save(user);

        // Envoyer un email avec le lien de réinitialisation contenant le token
    }

    public void updatePassword(String resetToken, String newPassword) throws Throwable
    {
        User user = (User) userRepository.findByResetToken(resetToken)
                .orElseThrow(() -> new RuntimeException("Invalid reset token"));

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetToken(null);
        userRepository.save(user);
    }

    public void save(User user)
    {
        userRepository.save(user);
    }

    public User findByEmailAndMatricule(String email, String matricule)
    {
        return userRepository.findByEmailAndMatricule(email, matricule);
    }
}