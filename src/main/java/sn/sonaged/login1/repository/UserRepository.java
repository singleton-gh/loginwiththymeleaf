package sn.sonaged.login1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sn.sonaged.login1.entities.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>
{
    Optional<User> findByMatricule(String matricule);
    Optional<User> findByEmail(String email); // Retourne un Optional<User>
    Optional<User> findByResetToken(String resetToken); // Retourne un Optional<User>

//Reinitialisation mot de passe
    User findByEmailAndMatricule(String matricule,String email);
}