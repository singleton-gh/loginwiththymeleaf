package sn.sonaged.login1.service; // Assurez-vous que le package correspond à votre structure


import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import sn.sonaged.login1.entities.User;
import sn.sonaged.login1.repository.UserRepository;


@Service
public class JwtUserDetailsService implements UserDetailsService
{
    private UserRepository userRepository;

    public JwtUserDetailsService(UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }



    @Override
    public UserDetails loadUserByUsername(String matricule) throws UsernameNotFoundException
    {
        User user = userRepository.findByMatricule(matricule)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé avec le matricule : " + matricule));

        return new org.springframework.security.core.userdetails.User(
                user.getMatricule(),
                user.getPassword(),
                user.getAuthorities() // Passez les autorités de l'utilisateur
        );
    }
}