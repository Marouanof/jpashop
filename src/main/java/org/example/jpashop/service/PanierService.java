package org.example.jpashop.service;

import org.example.jpashop.entity.*;
import org.example.jpashop.util.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.List;
import java.util.Optional;

public class PanierService {

    private ProduitService produitService;

    public PanierService() {
        this.produitService = new ProduitService();
    }

    public Panier obtenirPanierActif(Internaute internaute) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            List<Panier> result = em.createQuery(
                            "SELECT p FROM Panier p WHERE p.internaute.id = :internauteId AND p.statut = 'ACTIF'",
                            Panier.class)
                    .setParameter("internauteId", internaute.getId())
                    .getResultList();

            if (result.isEmpty()) {
                EntityTransaction tx = em.getTransaction();
                try {
                    tx.begin();
                    Panier nouveauPanier = new Panier(internaute);
                    em.persist(nouveauPanier);
                    tx.commit();
                    return nouveauPanier;
                } catch (Exception e) {
                    if (tx.isActive()) tx.rollback();
                    throw e;
                }
            }

            // 🔥 IMPORTANT: Charger les lignes du panier
            Panier panier = result.get(0);
            panier.getLignePaniers().size(); // Force le chargement lazy
            return panier;

        } finally {
            em.close();
        }
    }

    public void ajouterAuPanier(Internaute internaute, Long produitId, int quantite) {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            // 🔥 Récupérer le panier avec l'EntityManager actuel
            Panier panier = obtenirPanierActifAvecEm(em, internaute);
            Produit produit = produitService.trouverParId(produitId);

            if (produit == null) {
                throw new RuntimeException("Produit non trouvé");
            }

            if (produit.getStock() < quantite) {
                throw new RuntimeException("Stock insuffisant");
            }

            // Vérifier si le produit est déjà dans le panier
            Optional<LignePanier> ligneExistante = panier.getLignePaniers().stream()
                    .filter(lp -> lp.getProduit().getId().equals(produitId))
                    .findFirst();

            if (ligneExistante.isPresent()) {
                LignePanier ligne = ligneExistante.get();
                ligne.setQuantite(ligne.getQuantite() + quantite);
                ligne.calculerSousTotal();
                em.merge(ligne); // 🔥 Important: merge pour la persistance
            } else {
                LignePanier nouvelleLigne = new LignePanier(panier, produit, quantite);
                em.persist(nouvelleLigne);
                panier.getLignePaniers().add(nouvelleLigne);
            }

            panier.calculerTotal();
            em.merge(panier); // 🔥 Mettre à jour le panier
            tx.commit();

        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    // 🔥 Nouvelle méthode pour utiliser le même EntityManager
    private Panier obtenirPanierActifAvecEm(EntityManager em, Internaute internaute) {
        List<Panier> result = em.createQuery(
                        "SELECT p FROM Panier p WHERE p.internaute.id = :internauteId AND p.statut = 'ACTIF'",
                        Panier.class)
                .setParameter("internauteId", internaute.getId())
                .getResultList();

        if (result.isEmpty()) {
            Panier nouveauPanier = new Panier(internaute);
            em.persist(nouveauPanier);
            return nouveauPanier;
        }

        Panier panier = result.get(0);
        // Charger les lignes
        panier.getLignePaniers().size();
        return panier;
    }

    public void supprimerDuPanier(Internaute internaute, Long produitId) {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            Panier panier = obtenirPanierActifAvecEm(em, internaute);

            LignePanier ligneASupprimer = panier.getLignePaniers().stream()
                    .filter(lp -> lp.getProduit().getId().equals(produitId))
                    .findFirst()
                    .orElse(null);

            if (ligneASupprimer != null) {
                // 🔥 IMPORTANT: Fusionner l'entité détachée pour la rendre managée
                LignePanier managedLigne = em.merge(ligneASupprimer);
                em.remove(managedLigne);

                panier.getLignePaniers().remove(ligneASupprimer);
                panier.calculerTotal();
                em.merge(panier); // Mettre à jour le panier
            }

            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    // Modifier la quantité d'un produit
    public void modifierQuantite(Internaute internaute, Long produitId, int nouvelleQuantite) {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            if (nouvelleQuantite <= 0) {
                supprimerDuPanier(internaute, produitId);
                return;
            }

            Panier panier = obtenirPanierActif(internaute);
            Produit produit = produitService.trouverParId(produitId);

            if (produit.getStock() < nouvelleQuantite) {
                throw new RuntimeException("Stock insuffisant");
            }

            LignePanier ligne = panier.getLignePaniers().stream()
                    .filter(lp -> lp.getProduit().getId().equals(produitId))
                    .findFirst()
                    .orElse(null);

            if (ligne != null) {
                ligne.setQuantite(nouvelleQuantite);
                ligne.calculerSousTotal();
                panier.calculerTotal();
            }

            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void validerPanier(Internaute internaute) {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            // 🔥 Récupérer le panier avec le même EntityManager
            Panier panier = obtenirPanierActifAvecEm(em, internaute);

            if (panier.getLignePaniers().isEmpty()) {
                throw new RuntimeException("Le panier est vide");
            }

            // Vérifier les stocks avec des entités managées
            for (LignePanier ligne : panier.getLignePaniers()) {
                // 🔥 Recharger le produit avec l'EntityManager actuel pour avoir les données fraîches
                Produit produit = em.find(Produit.class, ligne.getProduit().getId());
                if (produit.getStock() < ligne.getQuantite()) {
                    throw new RuntimeException("Stock insuffisant pour: " + produit.getNom());
                }
            }

            // Réduire les stocks avec des entités managées
            for (LignePanier ligne : panier.getLignePaniers()) {
                Produit produit = em.find(Produit.class, ligne.getProduit().getId());
                if (!produit.reduireStock(ligne.getQuantite())) {
                    throw new RuntimeException("Erreur lors de la réduction du stock pour: " + produit.getNom());
                }
                em.merge(produit); // 🔥 Important: sauvegarder la modification
            }

            // Marquer le panier comme validé
            panier.setStatut("VALIDE");
            em.merge(panier); // 🔥 Sauvegarder le changement de statut

            // Créer un nouveau panier actif pour de futurs achats
            Panier nouveauPanier = new Panier(internaute);
            em.persist(nouveauPanier);

            tx.commit();

        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException("Erreur lors de la validation du panier: " + e.getMessage(), e);
        } finally {
            em.close();
        }
    }

    // Vider le panier
    public void viderPanier(Internaute internaute) {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            Panier panier = obtenirPanierActif(internaute);

            for (LignePanier ligne : panier.getLignePaniers()) {
                em.remove(ligne);
            }

            panier.getLignePaniers().clear();
            panier.setTotal(0.0);

            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    // Historique des commandes
    public List<Panier> historiqueCommandes(Internaute internaute) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery(
                            "SELECT p FROM Panier p WHERE p.internaute.id = :internauteId AND p.statut = 'VALIDE' ORDER BY p.dateCreation DESC",
                            Panier.class)
                    .setParameter("internauteId", internaute.getId())
                    .getResultList();
        } finally {
            em.close();
        }
    }
}
