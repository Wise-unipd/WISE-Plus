<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <c:import url="/jsp/include/head.jsp"/>
    <title>Worker Filmography</title>
    <meta name="description" content="Wise movie rental app - Movie person page">
    <link rel="stylesheet" href="<c:url value="/css/card.css"/>">
    <link rel="stylesheet" href="<c:url value="/css/cards_grid.css"/>">
</head>
<body>

<c:import url="/jsp/include/header.jsp"/>

<main class="wise-container">
    <c:set var="workerCompleteName" value="${worker.name} ${worker.surname}"/>

    <c:choose>
        <c:when test="${empty worker.name}">
            <p class="wise-no-entity">No worker found.</p>
        </c:when>
        <c:when test="${empty movies}">
            <h1 class="wise-page-title"><c:out value="${workerCompleteName}"/></h1>
            <p class="wise-page-description">
                Discover movies in which <c:out value="${workerCompleteName}"/> appears as an actor or director.
            </p>
            
            <div class="wise-empty-container">
                <p class="wise-no-entity">No movies found.</p>
            </div>
        </c:when>
        <c:otherwise>
            <h1 class="wise-page-title"><c:out value="${workerCompleteName}"/></h1>
            <p class="wise-page-description">
                Discover movies in which <c:out value="${workerCompleteName}"/> appears as an actor or director.
            </p>

            <div id="wise-filmography">
                <ul id="movie-list" class="cards-grid">
                    <c:forEach var="movie" items="${movies}">
                        <li class="wise-card-item">
                            <a href='<c:url value="/auth/movie"><c:param name="id" value="${movie.id}"/></c:url>'
                                class="wise-card wise-clean-link">
                                <img class="wise-card__poster"
                                        src='<c:url value="/uploads/${movie.posterPic}"/>'
                                        alt='Poster of <c:out value="${movie.title}"/>'>

                                <div class="wise-card__overlay">
                                    <p class="wise-card__title"><c:out value="${movie.title}"/></p>
                                    <p class="wise-card__price"><fmt:formatNumber value="${movie.basePriceEur}" minFractionDigits="2" maxFractionDigits="2"/>€/day</p>
                                    <span class="wise-card__show-btn">Show</span>
                                </div>
                            </a>
                        </li>
                    </c:forEach>
                </ul>
            </div>
        </c:otherwise>
    </c:choose>
</main>

<c:import url="/jsp/include/back-to-top.jsp" />
<c:import url="/jsp/include/footer.jsp"/>
<c:import url="/jsp/include/foot.jsp"/>

<script src="<c:url value='/js/back_to_top.js'/>"></script>
</body>
</html>