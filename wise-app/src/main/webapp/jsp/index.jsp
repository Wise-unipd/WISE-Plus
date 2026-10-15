<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <c:import url="include/head.jsp"/>
    <title>Wise | Movie Rental Platform</title>
    <meta name="description" content="Home page of the Wise movie rental app">
    <link rel="stylesheet" href="<c:url value="/css/card.css"/>">
    <link rel="stylesheet" href="<c:url value="/css/cards_row.css"/>">
    <link rel="stylesheet" href="<c:url value="/css/welcome_page.css"/>">
</head>
<body>

<main>
    <section class="wise-hero-welcome">
        <div class="wise-hero-welcome__info">
            <a href="<c:url value='/auth/catalog'/>" class="wise-clean-link">
                <div class="wordmark">
                    <span>WISE</span>
                    <span class="wordmark-plus">+</span>
                </div>
            </a>
            <span class="wise-mono wise-hero-welcome__eyebrow">
                Unlimited Cinematic Experiences
            </span>

            <h1 class="wise-hero-welcome__title">
                Your Personal <span class="wise-hero-welcome__title__break">Movie Library</span>
            </h1>

            <p class="wise-hero-welcome-message">
                Join our community to start your journey.
            </p>

            <div class="wise-hero-welcome__cta">
                <a href='<c:url value="/register"/>' class="wise-btn wise-btn--primary wise-btn--lg wise-btn--block">Get Started</a>
                <a href='<c:url value="/login"/>' class="wise-btn wise-btn--ghost wise-btn--lg wise-btn--block">Sign In</a>
            </div>
        </div>
    </section>
    <c:if test="${not empty movies}">
        <section class="carousel-section">
            <div class="carousel-section-head">
                <h2 class="carousel-title">Trending Movies</h2>
                <i class="carousel-icon fa-solid fa-arrow-trend-up"></i>
            </div>
            <div class="wise-card-row-container">
                <button class="nav-btn prev" data-action="scroll-carousel" data-direction="-1" aria-label="Scroll left">&#10094;</button>
                <button class="nav-btn next" data-action="scroll-carousel" data-direction="1" aria-label="Scroll right">&#10095;</button>
                <ol class="wise-card-row" tabindex="0" aria-label="Trending Movies Carousel">
                    <c:forEach var="movie" items="${movies}" varStatus="loop">
                        <li class="wise-card-item">
                            <div class="ranking-position-container">
                                <span><c:out value="${loop.count}"/></span>
                            </div>
                            <div class="wise-card">
                                <img class="wise-card__poster"
                                    src='<c:url value="/uploads/${movie.posterPic}"/>'
                                    alt='Poster of <c:out value="${movie.title}"/>'>
                                <div class="wise-card__overlay">
                                    <p class="wise-card__title"><c:out value="${movie.title}"/></p>
                                    <a href='<c:url value="/register"/>' class="wise-card__show-btn wise-clean-link">Join to Rent</a>
                                </div>
                            </div>
                        </li>
                    </c:forEach>
                </ol>
            </div>
        </section>
    </c:if>
</main>

<c:import url="include/footer.jsp"/>
<c:import url="include/back-to-top.jsp" />
<c:import url="include/foot.jsp"/>

<script src="<c:url value='/js/back_to_top.js'/>"></script>
<script src="<c:url value='/js/scroll_row_card.js'/>"></script>
</body>
</html>