<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <c:import url="/jsp/include/head.jsp"/>
    <title>Movie Catalog | Wise</title>
    <meta name="description" content="Explore our movie collections.">
    <link rel="stylesheet" href="<c:url value="/css/card.css"/>">
    <link rel="stylesheet" href="<c:url value="/css/cards_row.css"/>">
    <link rel="stylesheet" href="<c:url value="/css/catalog_page.css"/>">
</head>
<body>

<c:import url="/jsp/include/header.jsp"/>

<main>
    <div class="catalog-page-title-container">
        <h1 class="wise-page-title">Movie Catalog</h1>
    </div>

    <c:if test='${!message.error}'>
        <c:forEach var="collection" items="${collections}">
            <section class="catalog-section">
                <c:set var="category" value="${collection.category}"/>

                <div class="catalog-section-head">
                    <h2 class="catalog-category-title"><c:out value="${category.name}"/></h2>

                    <a href='<c:url value="/auth/category"><c:param name="id" value="${category.id}"/></c:url>'
                    class="wise-clean-link wise-mono catalog-more-link">
                        <span>Show more</span>
                        <i class="fa-solid fa-angle-right"></i>
                    </a>
                </div>

                <div class="wise-card-row-container">
                    <button class="nav-btn prev" data-action="scroll-carousel" data-direction="-1" aria-label="Scroll left">&#10094;</button>
                    <button class="nav-btn next" data-action="scroll-carousel" data-direction="1" aria-label="Scroll right">&#10095;</button>

                    <ul class="wise-card-row" tabindex="0" aria-label="Movies Carousel">
                        <c:forEach var="movie" items="${collection.movies}">
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
            </section>
        </c:forEach>
    </c:if>
</main>

<c:import url="/jsp/include/back-to-top.jsp" />
<c:import url="/jsp/include/footer.jsp"/>
<c:import url="/jsp/include/foot.jsp"/>

<script src="<c:url value='/js/scroll_row_card.js'/>"></script>
<script src="<c:url value='/js/back_to_top.js'/>"></script>
</body>
</html>