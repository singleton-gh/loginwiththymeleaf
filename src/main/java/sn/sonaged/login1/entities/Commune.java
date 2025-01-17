package sn.sonaged.login1.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name="commune")
@Getter @Setter
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "departement"})

public class Commune
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;


    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference // Evite la boucle infinie
    @JoinColumn(name = "departement_id")
    private Departement departement;


    @OneToMany(mappedBy = "commune")
    private List<User> responsablesCommunaux;

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

    public Departement getDepartement() {
        return departement;
    }

    public void setDepartement(Departement departement) {
        this.departement = departement;
    }

    public List<User> getResponsablesCommunaux() {
        return responsablesCommunaux;
    }

    public void setResponsablesCommunaux(List<User> responsablesCommunaux) {
        this.responsablesCommunaux = responsablesCommunaux;
    }
}