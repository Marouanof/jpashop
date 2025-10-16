package org.example.jpashop.controller;

import org.example.jpashop.entity.Produit;
import org.example.jpashop.service.ProduitService;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/produits")
public class ProduitController extends HttpServlet {

    private ProduitService produitService;
    @Override
    public void init() throws ServletException {
        this.produitService = new ProduitService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if (action == null) {
            action = "list";
        }

        switch (action) {
            case "list":
                listerProduits(request, response);
                break;
            case "view":
                afficherProduit(request, response);
                break;
            case "search":
                rechercherProduits(request, response);
                break;
            case "byCategory":
                produitsParCategorie(request, response);
                break;
            default:
                listerProduits(request, response);
        }
    }

    private void listerProduits(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Produit> produits = produitService.tousLesProduits();
        request.setAttribute("produits", produits);
        request.getRequestDispatcher("/WEB-INF/views/produits.jsp").forward(request, response);
    }

    private void afficherProduit(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            Long produitId = Long.parseLong(request.getParameter("id"));
            Produit produit = produitService.trouverParId(produitId);

            if (produit != null) {
                request.setAttribute("produit", produit);
                request.getRequestDispatcher("/WEB-INF/views/produit-details.jsp").forward(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/produits");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/produits");
        }
    }

    private void rechercherProduits(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String searchTerm = request.getParameter("q");
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            List<Produit> produits = produitService.rechercherParNom(searchTerm);
            request.setAttribute("produits", produits);
            request.setAttribute("searchTerm", searchTerm);
        } else {
            List<Produit> produits = produitService.tousLesProduits();
            request.setAttribute("produits", produits);
        }
        request.getRequestDispatcher("/WEB-INF/views/produits.jsp").forward(request, response);
    }

    private void produitsParCategorie(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String categorie = request.getParameter("categorie");
        if (categorie != null && !categorie.trim().isEmpty()) {
            List<Produit> produits = produitService.produitsParCategorie(categorie);
            request.setAttribute("produits", produits);
            request.setAttribute("categorie", categorie);
        } else {
            List<Produit> produits = produitService.tousLesProduits();
            request.setAttribute("produits", produits);
        }
        request.getRequestDispatcher("/WEB-INF/views/produits.jsp").forward(request, response);
    }
}
