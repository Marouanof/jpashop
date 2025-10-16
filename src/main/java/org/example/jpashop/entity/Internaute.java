package org.example.jpashop.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "internautes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Internaute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(nullable = false, length = 255)
    private String motDePasse;

    @Column(length = 255)
    private String adresse;

    @OneToMany(mappedBy = "internaute", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Panier> paniers = new ArrayList<>();

    public Internaute(String nom, String email, String motDePasse, String adresse) {
        this.nom = nom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.adresse = adresse;
    }
}
