package org.example.jpashop.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ligne_paniers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LignePanier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer quantite;

    @Column(name = "sous_total")
    private Double sousTotal;

    @ManyToOne
    @JoinColumn(name = "panier_id")
    private Panier panier;

    @ManyToOne
    @JoinColumn(name = "produit_id")
    private Produit produit;

    public LignePanier(Panier panier, Produit produit, Integer quantite) {
        this.panier = panier;
        this.produit = produit;
        this.quantite = quantite;
        calculerSousTotal();
    }

    public void calculerSousTotal() {
        if (produit != null && quantite != null) {
            this.sousTotal = produit.getPrix() * quantite;
        }
    }
}
