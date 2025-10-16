package org.example.jpashop.controller;

import org.example.jpashop.entity.Internaute;
import org.example.jpashop.entity.Panier;
import org.example.jpashop.service.PanierService;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/panier")
public class PanierController extends HttpServlet {

    private PanierService panierService;
    @Override
    public void init() throws ServletException {
        this.panierService = new PanierService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Internaute internaute = (Internaute) session.getAttribute("internaute");

        if (internaute == null) {
            response.sendRedirect(request.getContextPath() + "/internaute?action=login");
            return;
        }

        String action = request.getParameter("action");

        if (action == null) {
            action = "view";
        }

        switch (action) {
            case "view":
                afficherPanier(request, response, internaute);
                break;
            case "history":
                historiqueCommandes(request, response, internaute);
                break;
            default:
                afficherPanier(request, response, internaute);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Internaute internaute = (Internaute) session.getAttribute("internaute");

        if (internaute == null) {
            response.sendRedirect(request.getContextPath() + "/internaute?action=login");
            return;
        }

        String action = request.getParameter("action");

        switch (action) {
            case "add":
                ajouterProduit(request, response, internaute);
                break;
            case "remove":
                supprimerProduit(request, response, internaute);
                break;
            case "update":
                modifierQuantite(request, response, internaute);
                break;
            case "validate":
                validerPanier(request, response, internaute);
                break;
            case "clear":
                viderPanier(request, response, internaute);
                break;
            default:
                afficherPanier(request, response, internaute);
        }
    }

    private void afficherPanier(HttpServletRequest request, HttpServletResponse response,
                                Internaute internaute) throws ServletException, IOException {
        Panier panier = panierService.obtenirPanierActif(internaute);
        request.setAttribute("panier", panier);
        request.getRequestDispatcher("/WEB-INF/views/panier.jsp").forward(request, response);
    }

    private void historiqueCommandes(HttpServletRequest request, HttpServletResponse response,
                                     Internaute internaute) throws ServletException, IOException {
        request.setAttribute("commandes", panierService.historiqueCommandes(internaute));
        request.getRequestDispatcher("/WEB-INF/views/historique-commandes.jsp").forward(request, response);
    }

    private void ajouterProduit(HttpServletRequest request, HttpServletResponse response,
                                Internaute internaute) throws IOException {
        try {
            Long produitId = Long.parseLong(request.getParameter("produitId"));
            int quantite = Integer.parseInt(request.getParameter("quantite"));

            panierService.ajouterAuPanier(internaute, produitId, quantite);

            response.sendRedirect(request.getContextPath() + "/panier?action=view");

        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/produits?error=" + e.getMessage());
        }
    }

    private void supprimerProduit(HttpServletRequest request, HttpServletResponse response,
                                  Internaute internaute) throws IOException {
        try {
            Long produitId = Long.parseLong(request.getParameter("produitId"));
            panierService.supprimerDuPanier(internaute, produitId);

            response.sendRedirect(request.getContextPath() + "/panier?action=view");

        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/panier?action=view&error=" + e.getMessage());
        }
    }

    private void modifierQuantite(HttpServletRequest request, HttpServletResponse response,
                                  Internaute internaute) throws IOException {
        try {
            Long produitId = Long.parseLong(request.getParameter("produitId"));
            int quantite = Integer.parseInt(request.getParameter("quantite"));

            panierService.modifierQuantite(internaute, produitId, quantite);

            response.sendRedirect(request.getContextPath() + "/panier?action=view");

        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/panier?action=view&error=" + e.getMessage());
        }
    }

    private void validerPanier(HttpServletRequest request, HttpServletResponse response,
                               Internaute internaute) throws IOException {
        try {
            panierService.validerPanier(internaute);
            response.sendRedirect(request.getContextPath() + "/panier?action=view&success=Commande validée avec succès");

        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/panier?action=view&error=" + e.getMessage());
        }
    }

    private void viderPanier(HttpServletRequest request, HttpServletResponse response,
                             Internaute internaute) throws IOException {
        try {
            panierService.viderPanier(internaute);
            response.sendRedirect(request.getContextPath() + "/panier?action=view");

        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/panier?action=view&error=" + e.getMessage());
        }
    }
}
