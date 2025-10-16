package org.example.jpashop.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.example.jpashop.entity.Internaute;
import org.example.jpashop.util.JpaUtil;

public class InternauteService {

    public Internaute trouverParId(Long id) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.find(Internaute.class, id);
        } finally {
            em.close();
        }
    }

    public Internaute trouverParEmail(String email) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery("SELECT i FROM Internaute i WHERE i.email = :email", Internaute.class)
                    .setParameter("email", email)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        } finally {
            em.close();
        }
    }

    public Internaute inscrire(Internaute internaute) {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            System.out.println("🚀 DEBUT inscription: " + internaute.getEmail());
            tx.begin();

            // Vérifier si l'email existe déjà
            if (trouverParEmail(internaute.getEmail()) != null) {
                throw new RuntimeException("Un compte avec cet email existe déjà");
            }

            System.out.println("✅ Persist: " + internaute.getEmail());
            em.persist(internaute);
            tx.commit();
            System.out.println("🎉 COMMIT réussi: " + internaute.getEmail());
            return internaute;
        } catch (Exception e) {
            System.out.println("❌ ERREUR inscription: " + e.getMessage());
            if (tx.isActive()) {
                tx.rollback();
                System.out.println("↩️ Rollback effectué");
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public Internaute connecter(String email, String motDePasse) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery("SELECT i FROM Internaute i WHERE i.email = :email AND i.motDePasse = :motDePasse", Internaute.class)
                    .setParameter("email", email)
                    .setParameter("motDePasse", motDePasse)
                    .getSingleResult();
        } catch (Exception e) {
            return null;
        } finally {
            em.close();
        }
    }

    public Internaute mettreAJour(Internaute internaute) {
        EntityManager em = JpaUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Internaute result = em.merge(internaute);
            tx.commit();
            return result;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            throw e;
        } finally {
            em.close();
        }
    }
}
