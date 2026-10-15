<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <c:import url="/jsp/include/head.jsp"/>
    <title>Movie Search</title>
    <meta name="description" content="Wise movie rental app - Movie search page">
    <link rel="stylesheet" href="<c:url value="/css/card.css"/>">
    <link rel="stylesheet" href="<c:url value="/css/cards_grid.css"/>">
</head>
<body>

<c:import url="/jsp/include/header.jsp"/>

<main class="wise-container">
    <h1 class="wise-page-title">Search movies</h1>

    <div class="search-container">
        <span class="search-container__icon">
            <i class="fa-solid fa-magnifying-glass"></i>
        </span>
        <input type="text" 
               id="search-input" 
               placeholder="Search movies by title, actor or genre..." 
               aria-label="Search movies" 
               autocomplete="off" 
               class="search-container__input">
    </div>

    <div class="wise-result-container">
        <ul id="movie-list"
            class="cards-grid cards-grid--no-padding"></ul>
        <div id="status-container" class="hidden">
            <p id="status-message" class="wise-no-entity"></p>
        </div>
        <button id="load-more-btn"
                class="wise-btn wise-btn--primary wise-btn--lg wise-btn--center hidden">
            Load More
        </button>
    </div>
</main>

<c:import url="/jsp/include/back-to-top.jsp" />
<c:import url="/jsp/include/footer.jsp"/>
<c:import url="/jsp/include/foot.jsp"/>

<script src="<c:url value='/js/back_to_top.js'/>"></script>
<script src="${pageContext.request.contextPath}/js/search.js"></script>
</body>
</html>