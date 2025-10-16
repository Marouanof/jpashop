<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Mon Panier - E-Commerce</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
</head>
<body class="bg-light">
<!-- Navigation -->
<nav class="navbar navbar-expand-lg navbar-dark bg-primary shadow-sm">
    <div class="container">
        <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/">
            <i class="fas fa-shopping-bag me-2"></i>Youshop
        </a>
        <div class="navbar-nav ms-auto">
            <a class="nav-link text-white" href="${pageContext.request.contextPath}/produits">
                <i class="fas fa-store me-1"></i>Boutique
            </a>
            <a class="nav-link text-white active" href="${pageContext.request.contextPath}/panier">
                <i class="fas fa-shopping-cart me-1"></i>Panier
            </a>
            <a class="nav-link text-white" href="${pageContext.request.contextPath}/internaute?action=profile">
                <i class="fas fa-user me-1"></i>${sessionScope.internaute.nom}
            </a>
            <a class="nav-link text-white" href="${pageContext.request.contextPath}/internaute?action=logout">
                <i class="fas fa-sign-out-alt me-1"></i>Déconnexion
            </a>
        </div>
    </div>
</nav>

<div class="container my-5">
    <!-- En-tête -->
    <div class="row mb-4">
        <div class="col">
            <h1 class="h2 fw-bold text-dark">
                <i class="fas fa-shopping-cart text-primary me-2"></i>Mon Panier
            </h1>
            <p class="text-muted">Gérez vos articles avant la validation</p>
        </div>
    </div>

    <!-- Messages -->
    <c:if test="${not empty param.success}">
        <div class="alert alert-success d-flex align-items-center">
            <i class="fas fa-check-circle me-2"></i>
                ${param.success}
        </div>
    </c:if>
    <c:if test="${not empty param.error}">
        <div class="alert alert-danger d-flex align-items-center">
            <i class="fas fa-exclamation-circle me-2"></i>
                ${param.error}
        </div>
    </c:if>

    <c:choose>
        <c:when test="${not empty panier and not empty panier.lignePaniers}">
            <div class="row">
                <!-- Liste des articles -->
                <div class="col-lg-8">
                    <div class="card shadow-sm border-0">
                        <div class="card-header bg-white py-3">
                            <h5 class="card-title mb-0 text-dark">
                                <i class="fas fa-list me-2 text-primary"></i>Articles dans votre panier
                            </h5>
                        </div>
                        <div class="card-body p-0">
                            <c:forEach var="ligne" items="${panier.lignePaniers}">
                                <div class="border-bottom p-4">
                                    <div class="row align-items-center">
                                        <!-- Image produit -->
                                        <div class="col-md-2 text-center">
                                            <div class="bg-light rounded-circle d-flex align-items-center justify-content-center mx-auto"
                                                 style="width: 80px; height: 80px;">
                                                <i class="fas fa-box text-primary fa-2x"></i>
                                            </div>
                                        </div>

                                        <!-- Détails produit -->
                                        <div class="col-md-4">
                                            <h6 class="fw-bold text-dark mb-1">${ligne.produit.nom}</h6>
                                            <p class="text-muted small mb-2">${ligne.produit.description}</p>
                                            <span class="badge bg-light text-dark">${ligne.produit.categorie}</span>
                                        </div>

                                        <!-- Prix -->
                                        <div class="col-md-2">
                                            <span class="h6 text-dark fw-bold">${ligne.produit.prix} €</span>
                                        </div>

                                        <!-- Quantité -->
                                        <div class="col-md-2">
                                            <form action="${pageContext.request.contextPath}/panier" method="post" class="d-inline">
                                                <input type="hidden" name="action" value="update">
                                                <input type="hidden" name="produitId" value="${ligne.produit.id}">
                                                <div class="input-group input-group-sm">
                                                    <input type="number" name="quantite" value="${ligne.quantite}"
                                                           min="1" max="${ligne.produit.stock + ligne.quantite}"
                                                           class="form-control border-primary text-center">
                                                    <button type="submit" class="btn btn-outline-primary">
                                                        <i class="fas fa-sync-alt"></i>
                                                    </button>
                                                </div>
                                            </form>
                                        </div>

                                        <!-- Sous-total et actions -->
                                        <div class="col-md-2 text-end">
                                            <div class="mb-2">
                                                <strong class="h6 text-primary">${ligne.sousTotal} €</strong>
                                            </div>
                                            <form action="${pageContext.request.contextPath}/panier" method="post" class="d-inline">
                                                <input type="hidden" name="action" value="remove">
                                                <input type="hidden" name="produitId" value="${ligne.produit.id}">
                                                <button type="submit" class="btn btn-outline-danger btn-sm">
                                                    <i class="fas fa-trash"></i>
                                                </button>
                                            </form>
                                        </div>
                                    </div>
                                </div>
                            </c:forEach>
                        </div>
                    </div>
                </div>

                <!-- Résumé de commande -->
                <div class="col-lg-4">
                    <div class="card shadow-sm border-0 sticky-top" style="top: 20px;">
                        <div class="card-header bg-white py-3">
                            <h5 class="card-title mb-0 text-dark">
                                <i class="fas fa-receipt me-2 text-primary"></i>Résumé
                            </h5>
                        </div>
                        <div class="card-body">
                            <!-- Total -->
                            <div class="d-flex justify-content-between align-items-center mb-3">
                                <span class="text-muted">Sous-total</span>
                                <span class="fw-bold">${panier.total} €</span>
                            </div>
                            <div class="d-flex justify-content-between align-items-center mb-3">
                                <span class="text-muted">Livraison</span>
                                <span class="fw-bold text-success">Gratuite</span>
                            </div>
                            <hr>
                            <div class="d-flex justify-content-between align-items-center mb-4">
                                <span class="h5 text-dark fw-bold">Total</span>
                                <span class="h4 text-primary fw-bold">${panier.total} €</span>
                            </div>

                            <!-- Actions -->
                            <form action="${pageContext.request.contextPath}/panier" method="post" class="mb-3">
                                <input type="hidden" name="action" value="validate">
                                <button type="submit" class="btn btn-primary btn-lg w-100 py-3">
                                    <i class="fas fa-lock me-2"></i>Valider la commande
                                </button>
                            </form>

                            <a href="${pageContext.request.contextPath}/produits" class="btn btn-outline-secondary w-100">
                                <i class="fas fa-arrow-left me-2"></i>Continuer mes achats
                            </a>

                            <div class="text-center mt-3">
                                <a href="${pageContext.request.contextPath}/panier?action=history" class="text-decoration-none">
                                    <i class="fas fa-history me-1"></i>Voir l'historique
                                </a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </c:when>

        <c:otherwise>
            <!-- Panier vide -->
            <div class="row justify-content-center">
                <div class="col-md-6 text-center">
                    <div class="card shadow-sm border-0">
                        <div class="card-body py-5">
                            <div class="mb-4">
                                <i class="fas fa-shopping-cart fa-4x text-muted mb-3"></i>
                                <h3 class="text-dark mb-3">Votre panier est vide</h3>
                                <p class="text-muted mb-4">Découvrez nos produits et ajoutez-les à votre panier !</p>
                            </div>
                            <a href="${pageContext.request.contextPath}/produits" class="btn btn-primary btn-lg px-5">
                                <i class="fas fa-shopping-bag me-2"></i>Découvrir les produits
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<!-- Footer -->
<footer class="bg-dark text-light py-4 mt-5">
    <div class="container">
        <div class="row">
            <div class="col-md-6">
                <h5><i class="fas fa-shopping-bag me-2"></i>E-Commerce</h5>
                <p class="text-muted">Votre boutique en ligne de confiance</p>
            </div>
            <div class="col-md-6 text-end">
                <p class="text-muted mb-0">&copy; 2024 Youshop. Tous droits réservés.</p>
            </div>
        </div>
    </div>
</footer>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
