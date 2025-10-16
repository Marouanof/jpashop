<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Boutique - E-Commerce</title>
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
            <a class="nav-link text-white active" href="${pageContext.request.contextPath}/produits">
                <i class="fas fa-store me-1"></i>Boutique
            </a>
            <c:choose>
                <c:when test="${not empty sessionScope.internaute}">
                    <a class="nav-link text-white" href="${pageContext.request.contextPath}/panier">
                        <i class="fas fa-shopping-cart me-1"></i>Panier
                    </a>
                    <a class="nav-link text-white" href="${pageContext.request.contextPath}/internaute?action=profile">
                        <i class="fas fa-user me-1"></i>${sessionScope.internaute.nom}
                    </a>
                    <a class="nav-link text-white" href="${pageContext.request.contextPath}/internaute?action=logout">
                        <i class="fas fa-sign-out-alt me-1"></i>Déconnexion
                    </a>
                </c:when>
                <c:otherwise>
                    <a class="nav-link text-white" href="${pageContext.request.contextPath}/internaute?action=login">
                        <i class="fas fa-sign-in-alt me-1"></i>Connexion
                    </a>
                    <a class="nav-link text-white" href="${pageContext.request.contextPath}/internaute?action=register">
                        <i class="fas fa-user-plus me-1"></i>Inscription
                    </a>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</nav>

<div class="container my-5">
    <!-- En-tête -->
    <div class="row mb-4">
        <div class="col">
            <h1 class="h2 fw-bold text-dark mb-2">
                <i class="fas fa-store text-primary me-2"></i>Notre Boutique
            </h1>
            <p class="text-muted">Découvrez notre sélection de produits</p>
        </div>
    </div>

    <!-- Barre de recherche -->
    <div class="card shadow-sm border-0 mb-4">
        <div class="card-body">
            <form action="${pageContext.request.contextPath}/produits" method="get">
                <input type="hidden" name="action" value="search">
                <div class="row g-3 align-items-center">
                    <div class="col-md-8">
                        <div class="input-group input-group-lg">
                                <span class="input-group-text bg-light border-0">
                                    <i class="fas fa-search text-muted"></i>
                                </span>
                            <input type="text" name="q" class="form-control border-0 bg-light"
                                   placeholder="Rechercher un produit..." value="${param.q}">
                        </div>
                    </div>
                    <div class="col-md-4">
                        <button type="submit" class="btn btn-primary btn-lg w-100">
                            <i class="fas fa-search me-2"></i>Rechercher
                        </button>
                    </div>
                </div>
            </form>
        </div>
    </div>

    <!-- Filtres par catégorie -->
    <div class="card shadow-sm border-0 mb-4">
        <div class="card-body">
            <h6 class="card-title text-dark mb-3">
                <i class="fas fa-filter text-primary me-2"></i>Filtrer par catégorie
            </h6>
            <div class="d-flex flex-wrap gap-2">
                <a href="${pageContext.request.contextPath}/produits"
                   class="btn btn-outline-primary ${empty param.categorie and empty param.q ? 'active' : ''}">
                    Tous les produits
                </a>
                <c:forEach var="categorie" items="${produitService.toutesLesCategories()}">
                    <a href="${pageContext.request.contextPath}/produits?action=byCategory&categorie=${categorie}"
                       class="btn btn-outline-primary ${param.categorie eq categorie ? 'active' : ''}">
                            ${categorie}
                    </a>
                </c:forEach>
            </div>
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

    <!-- Liste des produits -->
    <c:choose>
        <c:when test="${not empty produits}">
            <div class="row g-4">
                <c:forEach var="produit" items="${produits}">
                    <div class="col-xl-3 col-lg-4 col-md-6">
                        <div class="card h-100 shadow-sm border-0 product-card">
                            <div class="card-body p-4">
                                <!-- Image produit -->
                                <div class="text-center mb-3">
                                    <div class="bg-light rounded-circle d-flex align-items-center justify-content-center mx-auto"
                                         style="width: 120px; height: 120px;">
                                        <i class="fas fa-box text-primary fa-3x"></i>
                                    </div>
                                </div>

                                <!-- Catégorie -->
                                <div class="mb-2">
                                    <span class="badge bg-primary bg-opacity-10 text-primary">${produit.categorie}</span>
                                </div>

                                <!-- Nom et description -->
                                <h5 class="card-title text-dark fw-bold mb-2">${produit.nom}</h5>
                                <p class="card-text text-muted small mb-3">${produit.description}</p>

                                <!-- Prix et stock -->
                                <div class="d-flex justify-content-between align-items-center mb-3">
                                    <span class="h5 text-primary fw-bold mb-0">${produit.prix} dh</span>
                                    <c:choose>
                                        <c:when test="${produit.stock > 0}">
                                                <span class="badge bg-success">
                                                    <i class="fas fa-check me-1"></i>En stock
                                                </span>
                                        </c:when>
                                        <c:otherwise>
                                                <span class="badge bg-danger">
                                                    <i class="fas fa-times me-1"></i>Rupture
                                                </span>
                                        </c:otherwise>
                                    </c:choose>
                                </div>

                                <!-- Quantité en stock -->
                                <div class="mb-3">
                                    <small class="text-muted">
                                        <i class="fas fa-cubes me-1"></i>${produit.stock} unités disponibles
                                    </small>
                                </div>
                            </div>

                            <!-- Actions -->
                            <div class="card-footer bg-white border-0 pt-0">
                                <c:choose>
                                    <c:when test="${not empty sessionScope.internaute}">
                                        <c:if test="${produit.stock > 0}">
                                            <form action="${pageContext.request.contextPath}/panier" method="post">
                                                <input type="hidden" name="action" value="add">
                                                <input type="hidden" name="produitId" value="${produit.id}">
                                                <div class="row g-2 align-items-center">
                                                    <div class="col-7">
                                                        <input type="number" name="quantite" value="1"
                                                               min="1" max="${produit.stock}"
                                                               class="form-control form-control-sm">
                                                    </div>
                                                    <div class="col-5">
                                                        <button type="submit" class="btn btn-primary btn-sm w-100">
                                                            <i class="fas fa-cart-plus"></i>
                                                        </button>
                                                    </div>
                                                </div>
                                            </form>
                                        </c:if>
                                    </c:when>
                                    <c:otherwise>
                                        <a href="${pageContext.request.contextPath}/internaute?action=login"
                                           class="btn btn-outline-primary w-100">
                                            <i class="fas fa-sign-in-alt me-1"></i>Connectez-vous
                                        </a>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:when>

        <c:otherwise>
            <!-- Aucun produit -->
            <div class="text-center py-5">
                <div class="card shadow-sm border-0">
                    <div class="card-body py-5">
                        <i class="fas fa-search fa-4x text-muted mb-3"></i>
                        <h3 class="text-dark mb-3">Aucun produit trouvé</h3>
                        <p class="text-muted mb-4">Essayez de modifier vos critères de recherche</p>
                        <a href="${pageContext.request.contextPath}/produits" class="btn btn-primary">
                            <i class="fas fa-undo me-2"></i>Voir tous les produits
                        </a>
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
