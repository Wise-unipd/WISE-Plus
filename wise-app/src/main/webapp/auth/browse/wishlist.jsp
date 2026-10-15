<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <c:import url="/jsp/include/head.jsp"/>
    <title>Wishlist</title>
    <meta name="description" content="Wise movie rental app - Wishlist page">
    <link rel="stylesheet" href="<c:url value="/css/card.css"/>">
    <link rel="stylesheet" href="<c:url value="/css/cards_grid.css"/>">
</head>

<body>

<c:import url="/jsp/include/header.jsp"/>

<main class="wise-container">
    
    <h1 class="wise-page-title">Your Wishlist</h1>

    <div id="wise-wishlist">
        <c:choose>
            <c:when test="${empty movies}">
                <div class="wise-empty-container">
                    <p class="wise-no-entity">No movies found in your wishlist. Try adding something you like.</p>
                    <a href='<c:url value="/auth/catalog"/>' class="wise-btn wise-btn--primary wise-btn--lg wise-btn--center wise-clean-link">
                        Go to Catalog
                    </a>
                </div>
            </c:when>
            <c:otherwise>
                <ul id="movie-list" class="cards-grid">
                    <c:forEach var="movie" items="${movies}">
                        <li data-movie-id="${movie.id}" class="wise-card-item">
                            <a href='<c:url value="/auth/movie"><c:param name="id" value="${movie.id}"/></c:url>'
                                class="wise-card wise-clean-link">
                                <img class="wise-card__poster"
                                        src='<c:url value="/uploads/${movie.posterPic}"/>'
                                        alt='Poster of <c:out value="${movie.title}"/>'>

                                <button class="wise-card__heart wishlist-btn"
                                        data-wishlist-btn>
                                    <i class="wishlist-icon fa-solid fa-heart"></i>
                                </button>

                                <div class="wise-card__overlay">
                                    <p class="wise-card__title"><c:out value="${movie.title}"/></p>
                                    <p class="wise-card__price"><fmt:formatNumber value="${movie.basePriceEur}" minFractionDigits="2" maxFractionDigits="2"/>€/day</p>
                                    <span class="wise-card__show-btn">Show</span>
                                </div>
                            </a>
                        </li>
                    </c:forEach>
                </ul>
            </c:otherwise>
        </c:choose>
    </div>
</main>

<c:import url="/jsp/include/footer.jsp"/>
<c:import url="/jsp/include/back-to-top.jsp" />
<c:import url="/jsp/include/toast.jsp"/>

<c:import url="/jsp/include/foot.jsp"/>
<script src="<c:url value='/js/back_to_top.js'/>"></script>
<script src='<c:url value="/js/utilities/toast.js"/>'></script>
<script src='<c:url value="/js/wishlist.js"/>'></script>
</body>
</html>