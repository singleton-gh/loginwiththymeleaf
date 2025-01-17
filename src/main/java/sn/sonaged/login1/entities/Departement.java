package sn.sonaged.login1.entities;



import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Entity
@Table(name="departement")
@Getter @Setter
public class Departement
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;

    @JsonBackReference // Evite la boucle infinie ok
    @JoinColumn(name = "region_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private Region region;


    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "departement", orphanRemoval = true)
    @JsonBackReference // Evite la boucle infinie
    private List<Commune> communes;


    @OneToMany(mappedBy = "departement")
    private List<User> responsablesDepartementaux;


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

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public List<Commune> getCommunes() {
        return communes;
    }

    public void setCommunes(List<Commune> communes) {
        this.communes = communes;
    }

    public List<User> getResponsablesDepartementaux() {
        return responsablesDepartementaux;
    }

    public void setResponsablesDepartementaux(List<User> responsablesDepartementaux) {
        this.responsablesDepartementaux = responsablesDepartementaux;
    }
}