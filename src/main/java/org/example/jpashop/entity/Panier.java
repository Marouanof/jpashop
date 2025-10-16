package org.example.jpashop.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "paniers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Panier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation;

    @Column(length = 20)
    private String statut; // ACTIF, VALIDE, ANNULE

    private Double total;

    @ManyToOne
    @JoinColumn(name = "internaute_id")
    private Internaute internaute;

    @OneToMany(mappedBy = "panier", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<LignePanier> lignePaniers = new ArrayList<>();

    public Panier(Internaute internaute) {
        this.dateCreation = LocalDateTime.now();
        this.statut = "ACTIF";
        this.total = 0.0;
        this.internaute = internaute;
    }

    public void ajouterProduit(Produit produit, int quantite) {
        // Rechercher si le produit existe déjà
        Optional<LignePanier> ligneExistante = lignePaniers.stream()
                .filter(lp -> lp.getProduit().getId().equals(produit.getId()))
                .findFirst();

        if (ligneExistante.isPresent()) {
            // Mettre à jour la quantité
            LignePanier ligne = ligneExistante.get();
            ligne.setQuantite(ligne.getQuantite() + quantite);
            ligne.calculerSousTotal();
        } else {
            // Créer une nouvelle ligne
            LignePanier nouvelleLigne = new LignePanier(this, produit, quantite);
            lignePaniers.add(nouvelleLigne);
        }

        calculerTotal();
    }

    public void calculerTotal() {
        this.total = lignePaniers.stream()
                .mapToDouble(LignePanier::getSousTotal)
                .sum();
    }
}
