package org.example.jpashop.controller;

import org.example.jpashop.entity.Internaute;
import org.example.jpashop.service.InternauteService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/internaute")
public class InternauteController extends HttpServlet {

    private InternauteService internauteService;
    @Override
    public void init() throws ServletException {
        this.internauteService = new InternauteService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if (action == null) {
            action = "login";
        }

        switch (action) {
            case "login":
                afficherLogin(request, response);
                break;
            case "register":
                afficherRegister(request, response);
                break;
            case "logout":
                deconnecter(request, response);
                break;
            case "profile":
                afficherProfile(request, response);
                break;
            default:
                afficherLogin(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if ("login".equals(action)) {
            connecter(request, response);
        } else if ("register".equals(action)) {
            inscrire(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/internaute");
        }
    }

    private void afficherLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
    }

    private void afficherRegister(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
    }

    private void afficherProfile(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Internaute internaute = (Internaute) session.getAttribute("internaute");

        if (internaute == null) {
            response.sendRedirect(request.getContextPath() + "/internaute");
            return;
        }

        request.getRequestDispatcher("/WEB-INF/views/profile.jsp").forward(request, response);
    }

    private void connecter(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String motDePasse = request.getParameter("motDePasse");

        try {
            Internaute internaute = internauteService.connecter(email, motDePasse);

            if (internaute != null) {
                HttpSession session = request.getSession();
                session.setAttribute("internaute", internaute);
                response.sendRedirect(request.getContextPath() + "/produits");
            } else {
                request.setAttribute("error", "Email ou mot de passe incorrect");
                request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
            }
        } catch (Exception e) {
            request.setAttribute("error", "Erreur lors de la connexion");
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
        }
    }

    private void inscrire(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String nom = request.getParameter("nom");
        String email = request.getParameter("email");
        String motDePasse = request.getParameter("motDePasse");
        String adresse = request.getParameter("adresse");

        try {
            Internaute nouvelleInternaute = new Internaute(nom, email, motDePasse, adresse);
            internauteService.inscrire(nouvelleInternaute);

            request.setAttribute("success", "Inscription réussie ! Vous pouvez maintenant vous connecter.");
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);

        } catch (Exception e) {
            request.setAttribute("error", "Erreur lors de l'inscription: " + e.getMessage());
            request.getRequestDispatcher("/WEB-INF/views/register.jsp").forward(request, response);
        }
    }

    private void deconnecter(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession();
        session.invalidate();
        response.sendRedirect(request.getContextPath() + "/produits");
    }
}
