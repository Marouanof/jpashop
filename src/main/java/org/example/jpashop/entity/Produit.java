package org.example.jpashop.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "produits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Produit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private Double prix;

    @Column(nullable = false)
    private Integer stock;

    @Column(length = 50)
    private String categorie;

    @OneToMany(mappedBy = "produit")
    @ToString.Exclude
    private List<LignePanier> lignePaniers = new ArrayList<>();

    public Produit(String nom, String description, Double prix, Integer stock, String categorie) {
        this.nom = nom;
        this.description = description;
        this.prix = prix;
        this.stock = stock;
        this.categorie = categorie;
    }

    public boolean reduireStock(int quantite) {
        if (this.stock >= quantite) {
            this.stock -= quantite;
            return true;
        }
        return false;
    }
}
