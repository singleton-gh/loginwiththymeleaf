package sn.sonaged.login1.service; // Assurez-vous que le package correspond à votre structure


import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import sn.sonaged.login1.repository.UserRepository;

import java.util.ArrayList;

@Service
public class JwtUserDetailsService implements UserDetailsService
{
    public JwtUserDetailsService(UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }

    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String matricule) throws UsernameNotFoundException
        {
            // Recherchez l'utilisateur par son matricule
            sn.sonaged.login1.entities.User user = userRepository.findByMatricule(matricule)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with matricule: " + matricule));

            // Retournez un objet UserDetails (utilisé par Spring Security)
            return new User(user.getMatricule(), user.getPassword(), new ArrayList<>());
        }
}