<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <c:import url="/jsp/include/head.jsp"/>
    <title>Movie Details</title>
    <meta name="description" content="Wise movie rental app - Movie details page">
    <link rel="stylesheet" href="<c:url value='/css/movie-details.css'/>">
</head>
<body>

<c:import url="/jsp/include/header.jsp"/>

<c:choose>
    <c:when test="${not empty movieDetails && !message.error}">
        <c:set var="movie" value="${movieDetails.movie}"/>

        <section class="wd-hero">

            <div class="wd-hero-poster">
                <img src="<c:url value='/uploads/${movie.posterPic}'/>"
                     alt='Poster of <c:out value="${movie.title}"/>'/>
            </div>


            <div class="wd-hero-info">
                <div class="wd-eyebrow-row">
                    <span class="wd-mono"><c:out value="${movie.releaseYear}"/></span>
                    <span class="wd-mono">
                        <c:out value="${movie.runningTime}"/> MIN
                    </span>
                    <span class="wd-rating"><c:out value="${movie.ageRating.name}"/></span>
                </div>

                <h1 class="wd-title"><c:out value="${movie.title}"/></h1>

                <p class="wd-director">Directed by
                    <c:forEach var="credit" items="${movieDetails.credits}">
                        <c:if test='${fn:toLowerCase(credit.role.name) == "director"}'>
                            <c:set var="dirWorker" value="${credit.worker}"/>
                            <a href="<c:url value='/auth/person'><c:param name='id' value='${dirWorker.id}'/></c:url>">
                                <c:out value="${dirWorker.name}"/> <c:out value="${dirWorker.surname}"/>
                            </a>
                        </c:if>
                    </c:forEach>
                </p>

                <div class="wd-tags">
                    <c:forEach var="genre" items="${movieDetails.genres}">
                        <span class="wd-tag"><c:out value="${genre.name}"/></span>
                    </c:forEach>
                </div>


                <dl class="wd-spec">
                    <div class="wd-spec-row">
                        <dt>Audio</dt>
                        <dd>
                            <c:forEach var="audio" items="${movieDetails.audios}" varStatus="s">
                                <c:out value="${audio.name}"/><c:if test="${!s.last}">, </c:if>
                            </c:forEach>
                        </dd>
                    </div>
                    <div class="wd-spec-row">
                        <dt>Subtitles</dt>
                        <dd>
                            <c:forEach var="subtitle" items="${movieDetails.subtitles}" varStatus="s">
                                <c:out value="${subtitle.name}"/><c:if test="${!s.last}">, </c:if>
                            </c:forEach>
                        </dd>
                    </div>
                    <div class="wd-spec-row">
                        <dt>Rating</dt>
                        <dd>
                            <c:out value="${movie.ageRating.name}"/> —
                            <c:out value="${movie.ageRating.description}"/>
                        </dd>
                    </div>
                </dl>


                <div class="wd-actions-row">
                    <button id="like-btn" data-movie-id="${movie.id}"
                            class="wd-action-icon  <c:if test='${isLiked}'> is-active</c:if>"
                            aria-label="Recommend" title="Recommend">
                        <i id="like-icon"
                           class="${isLiked ? 'fa-solid fa-thumbs-up' : 'fa-regular fa-thumbs-up'}"
                           data-role="icon" data-value="${isLiked}"></i>
                    </button>
                    <button id="wishlist-btn" data-movie-id="${movie.id}"
                            class="wd-action-icon <c:if test='${isInWishlist}'> is-active</c:if>"
                            aria-label="Add to wishlist" title="Add to wishlist">
                        <i id="wishlist-icon"
                           class="${isInWishlist ? 'fa-solid fa-heart' : 'fa-regular fa-heart'}"
                           data-role="icon" data-value="${isInWishlist}"></i>
                    </button>
                </div>
            </div>


            <div class="wd-rental<c:if test='${not empty activeRentExpiration}'> wd-rental--owned</c:if>">
                <c:choose>
                    <c:when test="${not empty activeRentExpiration}">
                        <div class="wd-rental-head">
                            <span class="wd-mono wd-rental-status">
                                Currently Rented
                            </span>
                        </div>
                        <div class="wd-rental-actions">
                            <a href="<c:url value='/auth/watch/${movie.id}'/>"
                               class="wise-btn wise-btn--primary wise-btn--lg wise-btn--block">
                                <i class="fa-solid fa-play"></i>
                                Watch Now
                            </a>
                        </div>
                        <div class="wd-rental-expiry">
                            <span class="wd-mono">Expires</span>
                            <strong class="wd-rental-time wd-mono">
                                <fmt:formatDate value="${activeRentExpiration}" pattern="dd/MM/yyyy, HH:mm:ss" />
                            </strong>
                        </div>
                        <div class="wd-rental-extend">
                            <a href="<c:url value='/auth/extend/${movie.id}'/>"
                               class="wise-btn wise-btn--ghost wise-btn--block">
                                Extend rental
                            </a>
                        </div>
                    </c:when>


                    <c:otherwise>
                        <div class="wd-rental-head">
                            <span class="wd-mono">Rent </span>
                            <span class="wd-mono wd-rental-price-day">
                                                <fmt:formatNumber value="${movie.basePriceEur}" minFractionDigits="2" maxFractionDigits="2"/>€/day
                                            </span>
                        </div>

                        <div class="wd-rental-stepper">
                            <label for="rent-days" class="wd-rental-extend-label wd-mono">
                                Number of days
                            </label>
                            <div class="wd-stepper-wrap">
                                <button type="button"
                                        id="less-days-btn"
                                        class="wd-stepper-btn"
                                        aria-label="Less">
                                    −
                                </button>

                                <input type="number" id="rent-days"
                                       min="1" max="30" value="1"
                                       class="wd-stepper-input"/>

                                <button type="button"
                                        id="more-days-btn"
                                        class="wd-stepper-btn"
                                        aria-label="More">
                                    +
                                </button>
                            </div>
                        </div>


                        <div class="wd-rental-total">
                            <span>Total</span>
                            <span class="wd-rental-total-amount">
                                                <span id="total-price"><fmt:formatNumber value="${movie.basePriceEur}" minFractionDigits="2" maxFractionDigits="2"/></span>€
                                            </span>
                        </div>

                        <button id="rent-btn"
                                data-movie-id="${movie.id}"
                                data-price="${movie.basePriceEur}"
                                class="wise-btn wise-btn--primary wise-btn--lg wise-btn--block">
                            Rent
                        </button>
                    </c:otherwise>

                </c:choose>
            </div>

        </section>


        <div class="wd-content-sections">
            <section>
                <section class="wd-section">
                    <header class="wd-section-head">
                        <h2 class="wd-section-title">Synopsis</h2>
                    </header>
                    <div class="wd-synopsis">
                        <p class="wd-synopsis-text"><c:out value="${movie.synopsis}"/></p>
                    </div>
                </section>


                <section class="wd-section">
                    <header class="wd-section-head">
                        <h2 class="wd-section-title">Cast</h2>
                    </header>
                    <div class="wd-cast">
                        <c:forEach var="credit" items="${movieDetails.credits}">
                            <c:if test='${fn:toLowerCase(credit.role.name) == "actor"}'>
                                <c:set var="worker" value="${credit.worker}"/>
                                <a class="wd-cast-item"
                                   href="<c:url value='/auth/person'><c:param name='id' value='${worker.id}'/></c:url>">
                                    <div class="wd-cast-name">
                                        <c:out value="${worker.name}"/> <c:out value="${worker.surname}"/>
                                    </div>
                                </a>
                            </c:if>
                        </c:forEach>
                    </div>
                </section>
            </section>


            <c:if test="${not empty movie.trailer}">
                <section class="wd-section wd-trailer-section w-full">
                    <header class="wd-section-head">
                        <h2 class="wd-section-title">Trailer</h2>
                    </header>
                    <div class="wd-trailer-wrap">
                        <iframe class="wd-trailer-iframe"
                                src="https://www.youtube.com/embed/${movie.trailer}"
                                title="YouTube video player"
                                allow="accelerometer; autoplay; clipboard-write; encrypted-media;
                                   gyroscope; picture-in-picture; web-share"
                                referrerpolicy="strict-origin-when-cross-origin"
                                allowfullscreen></iframe>
                    </div>
                </section>
            </c:if>

        </div>


    </c:when>

    <c:otherwise>
        <main class="wise-container">
            <p class="wise-no-entity">No movie found.</p>
        </main>
    </c:otherwise>
</c:choose>

<c:import url="/jsp/include/toast.jsp"/>
<c:import url="/jsp/include/footer.jsp"/>
<c:import url="/jsp/include/back-to-top.jsp" />
<c:import url="/jsp/include/foot.jsp"/>

<script src="<c:url value='/js/back_to_top.js'/>"></script>
<script type="text/javascript" src='<c:url value="/js/utilities/toast.js"/>'></script>
<script type="text/javascript" src="<c:url value='/js/movie_details.js'/>"></script>
</body>
</html>
