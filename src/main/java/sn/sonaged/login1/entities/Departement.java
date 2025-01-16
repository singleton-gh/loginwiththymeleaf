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
}