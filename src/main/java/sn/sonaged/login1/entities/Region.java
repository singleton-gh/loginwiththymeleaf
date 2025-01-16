package sn.sonaged.login1.entities;



import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Entity
@Getter @Setter
@Table(name ="region")
public class Region
{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;

    @JsonManagedReference // Permet de sérialiser les départements
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "region", orphanRemoval = true)
    private List<Departement> departements;

    @OneToMany(mappedBy = "region")
    private List<User> responsablesRegionaux;

}