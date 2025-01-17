package sn.sonaged.login1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sn.sonaged.login1.entities.Commune;
import java.util.Optional;

public interface CommuneRepository extends JpaRepository<Commune, Long>
{
    Commune findByNom(String nom);
}