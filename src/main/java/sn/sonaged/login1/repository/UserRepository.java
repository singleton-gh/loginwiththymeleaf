package sn.sonaged.login1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sn.sonaged.login1.entities.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>
{

    Optional<User> findByMatricule(String matricule);
    Optional<Object> findByEmail(String email);
    Optional findByResetToken(String resetToken);

}
