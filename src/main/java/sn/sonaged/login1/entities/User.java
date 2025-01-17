package sn.sonaged.login1.entities;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Entity
@Table(name = "user", uniqueConstraints = {@UniqueConstraint(columnNames = "matricule") // Ajoutez cette ligne
})
public class User implements UserDetails
    {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        private String nom;
        private String prenom;
        private String matricule;
        private String password;
        private String sexe;
        private String adresse;
        private String email;
        private String cni;
        private String telephone;
        private String telephoneGfu;
        private String resetToken; //Génération du token en cas de réinitialisation du mot de passe

        @Enumerated(EnumType.STRING)
        private Role role;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "commune_id") // Assurez-vous que le nom de la colonne correspond à votre base de données
        private Commune commune;


        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "departement_id") // Assurez-vous que le nom de la colonne correspond à votre base de données
        private Departement departement;


        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "region_id") // Assurez-vous que le nom de la colonne correspond à votre base de données
        private Region region;




        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getNom() {
            return nom;
        }

        public void setNom(String nom) {
            this.nom = nom;
        }

        public String getPrenom() {
            return prenom;
        }

        public void setPrenom(String prenom) {
            this.prenom = prenom;
        }

        public String getMatricule() {
            return matricule;
        }

        public void setMatricule(String matricule) {
            this.matricule = matricule;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getSexe() {
            return sexe;
        }

        public void setSexe(String sexe) {
            this.sexe = sexe;
        }

        public String getAdresse() {
            return adresse;
        }

        public void setAdresse(String adresse) {
            this.adresse = adresse;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getCni() {
            return cni;
        }

        public void setCni(String cni) {
            this.cni = cni;
        }

        public String getTelephone() {
            return telephone;
        }

        public void setTelephone(String telephone) {
            this.telephone = telephone;
        }

        public String getTelephoneGfu() {
            return telephoneGfu;
        }

        public void setTelephoneGfu(String telephoneGfu) {
            this.telephoneGfu = telephoneGfu;
        }

        public String getResetToken() {
            return resetToken;
        }

        public void setResetToken(String resetToken) {
            this.resetToken = resetToken;
        }

        public Role getRole() {
            return role;
        }

        public void setRole(Role role) {
            this.role = role;
        }

        public Commune getCommune() {
            return commune;
        }

        public void setCommune(Commune commune) {
            this.commune = commune;
        }

        public Departement getDepartement() {
            return departement;
        }

        public void setDepartement(Departement departement) {
            this.departement = departement;
        }

        public Region getRegion() {
            return region;
        }

        public void setRegion(Region region) {
            this.region = region;
        }


            @Override
            public Collection<? extends GrantedAuthority> getAuthorities()
            {
                // Retournez les autorités de l'utilisateur (rôles)
                return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role.name()));
            }

            @Override
            public String getUsername() {
                return this.matricule; // Retourne le matricule comme nom d'utilisateur
            }

            @Override
            public boolean isAccountNonExpired() {
                return true; // Le compte n'est jamais expiré
            }

            @Override
            public boolean isAccountNonLocked() {
                return true; // Le compte n'est jamais verrouillé
            }

            @Override
            public boolean isCredentialsNonExpired() {
                return true; // Les informations d'identification ne sont jamais expirées
            }

            @Override
            public boolean isEnabled() {
                return true; // Le compte est toujours activé
            }
    }
