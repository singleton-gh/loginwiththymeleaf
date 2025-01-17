package sn.sonaged.login1.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import sn.sonaged.login1.entities.Role;
import sn.sonaged.login1.entities.User;
import sn.sonaged.login1.repository.UserRepository;

@Component
public class DataInitializer implements CommandLineRunner
{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder)
    {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception
    {
        // Vérifier si un utilisateur existe déjà
        if (userRepository.findByMatricule("aa").isEmpty())
        {
            // Créer un utilisateur administrateur
            User admin = new User();
            admin.setMatricule("aa");
            admin.setPassword(passwordEncoder.encode("ok")); // Encoder le mot de passe
            admin.setRole(Role.ADMIN); // Définir le rôle
            admin.setEmail("admin@example.com");

            // Sauvegarder l'utilisateur dans la base de données
            userRepository.save(admin);
            System.out.println("Utilisateur admin créé avec succès.");


        }
    }
}