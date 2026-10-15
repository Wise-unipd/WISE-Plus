<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <c:import url="/jsp/include/head.jsp"/>
    <title>Link Credits to Movie</title>
    <meta name="description" content="Wise movie rental app - Link credits to movie page">
    <link rel="stylesheet" href="<c:url value='/css/link-entity-to-movie.css'/>">
</head>
<body>

<c:import url="/jsp/include/header.jsp"/>

<main class="wise-container">

    <a href="<c:url value='/auth/staff/movies'/>"
       class="wise-btn wise-btn--ghost go-back">
        <i class="fa-solid fa-circle-arrow-left"></i><span>Back to Movie Management</span>
    </a>

    <div class="info-bar">
        <span class="wise-mono ${movie.active 
              ? 'movie--public' 
              : 'movie--private'}">
            <c:choose>
                <c:when test="${movie.active}">
                    Public
                </c:when>
                <c:otherwise>
                    Private
                </c:otherwise>
            </c:choose>
        </span>
        <span class="year"><c:out value="${movie.releaseYear}"/></span>
    </div>

    <h2 class="wise-page-title"><c:out value="${movie.title}"/></h2>

    <h3 class="wise-mono section-title">Link Credits:</h3>

    <div class="link-entity-container">
        <input class="wise-input" type="hidden" id="selected-worker-id" name="selected-worker-id" value=""> 
        <div class="wise-field">
            <label class="wise-field-label" for="search-input">
                <span>Search and select a worker</span>
            </label>
            <div class="search-container" id="search-container">
                <span class="search-container__icon">
                    <i class="fa-solid fa-magnifying-glass"></i>
                </span>
                <input type="text"
                       id="search-input"
                       placeholder="Search worker..."
                       aria-label="Search worker"
                       autocomplete="off"
                       class="search-container__input">
                <div id="suggestions" class="suggestions hidden">
                    <ul></ul>
                </div>
            </div>
        </div>
        <c:if test="${not empty movieRoles}">
            <div class="wise-field">
                <label class="wise-field-label" for="movie-roles">
                    <span>Select a movie role</span>
                </label>
                <select class="wise-input" id="movie-roles" name="movie-roles">
                    <c:forEach var="role" items="${movieRoles}">
                        <option value="${role.id}">
                            <c:out value="${role.name}"/>
                        </option>
                    </c:forEach>
                </select>
            </div>
        </c:if>
        <button type="button" 
                id="add-credit-btn" 
                class="wise-btn wise-btn--primary wise-btn--block" 
                disabled>
            <span>Add</span>
            <i class="fa-solid fa-plus"></i>
        </button>
    </div>

    <h3 class="wise-mono section-title">Credits already associated:</h3>
    <div class="credit-container">
        <ul id="credit-list" 
            data-movie-id="${movie.id}"
            class="entity-list">
            <c:forEach var="credit" items="${credits}">
                <c:set var="worker" value="${credit.worker}"/>
                <c:set var="role" value="${credit.role}"/>
                <li data-worker-id="${worker.id}"
                    data-role-id="${role.id}"
                    class="entity credit-item">
                    <div class="credit-item__content">
                        <span class="wise-mono" data-credit-role># <c:out value="${role.name}"/></span>
                        <p class="credit-item__content__full-name">
                            <span data-credit-name><c:out value="${worker.name}"/></span>
                            <strong data-credit-surname><c:out value="${worker.surname}"/></strong>
                        </p>
                    </div>  
                    <button type="button" 
                            data-unlink-btn
                            class="wise-icon-btn credit-item__unlink-btn" 
                            aria-label="Unlink movie credit">
                        <i class="fa-solid fa-link-slash"></i>
                    </button>
                </li>
            </c:forEach>
        </ul>
    </div>
</main>

<c:import url="/jsp/include/back-to-top.jsp" />
<c:import url="/jsp/include/toast.jsp"/>
<c:import url="/jsp/include/footer.jsp"/>
<c:import url="/jsp/include/foot.jsp"/>

<script src="<c:url value='/js/back_to_top.js'/>"></script>
<script type="text/javascript" src='<c:url value="/js/utilities/toast.js"/>'></script>
<script type="text/javascript" src='<c:url value="/js/link_credits_to_movie.js"/>'></script>
</body>
</html>