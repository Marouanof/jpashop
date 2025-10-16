package org.example.jpashop.service;

import org.example.jpashop.entity.Produit;
import org.example.jpashop.util.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.List;

public class ProduitService {  // ⬅️ Supprimer @ApplicationScoped

    // CRUD Basique
    public Produit trouverParId(Long id) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.find(Produit.class, id);
        } finally {
            em.close();
        }
    }

    public List<Produit> tousLesProduits() {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery("SELECT p FROM Produit p ORDER BY p.nom", Produit.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public Produit sauvegarder(Produit produit) {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(produit);
            tx.commit();
            return produit;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public Produit mettreAJour(Produit produit) {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Produit result = em.merge(produit);
            tx.commit();
            return result;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    public void supprimer(Long id) {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Produit produit = em.find(Produit.class, id);
            if (produit != null) {
                em.remove(produit);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    // Méthodes métier spécifiques
    public List<Produit> rechercherParNom(String nom) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery("SELECT p FROM Produit p WHERE p.nom LIKE :nom", Produit.class)
                    .setParameter("nom", "%" + nom + "%")
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Produit> produitsParCategorie(String categorie) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery("SELECT p FROM Produit p WHERE p.categorie = :categorie", Produit.class)
                    .setParameter("categorie", categorie)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<Produit> produitsEnStock() {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery("SELECT p FROM Produit p WHERE p.stock > 0", Produit.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<String> toutesLesCategories() {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery("SELECT DISTINCT p.categorie FROM Produit p WHERE p.categorie IS NOT NULL", String.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}
