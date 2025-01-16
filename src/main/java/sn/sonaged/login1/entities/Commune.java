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

}