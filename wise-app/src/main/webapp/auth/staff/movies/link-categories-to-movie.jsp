<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <c:import url="/jsp/include/head.jsp"/>
    <title>Link Categories to Movie</title>
    <meta name="description" content="Wise movie rental app - Link categories to movie page">
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

    <h3 class="wise-mono section-title">Link Categories:</h3>

    <div class="entities-container">
        <c:choose>
            <c:when test="${empty categories}">
                <p class="no-entity">No categories found.</p>
            </c:when>
            <c:otherwise>
                <ul id="entity-list" 
                    data-movie-id="${movie.id}" 
                    data-entity-type="category"
                    class="entity-list">
                    <c:forEach var="category" items="${categories}">
                        <c:set var="resource" value="${category.resource}"/>
                        <li data-entity-id="${resource.id}"
                            class="entity">
                            <button type="button"
                                    data-toggle-entity-button
                                    class="wise-btn toggle-entity-button ${category.selected 
                                    ? 'wise-btn--primary' 
                                    : 'wise-btn--ghost'}">
                                <span><c:out value="${resource.name}"/></span>
                                <i data-toggle-entity-icon  
                                   data-value="${category.selected}"
                                   class="${category.selected 
                                   ? 'fa-solid fa-circle-minus' 
                                   : 'fa-solid fa-circle-plus'}">
                                </i>
                            </button>
                        </li>
                    </c:forEach>
                </ul>
            </c:otherwise>
        </c:choose>
    </div>
</main>

<c:import url="/jsp/include/back-to-top.jsp" />
<c:import url="/jsp/include/toast.jsp"/>
<c:import url="/jsp/include/footer.jsp"/>
<c:import url="/jsp/include/foot.jsp"/>

<script src="<c:url value='/js/back_to_top.js'/>"></script>
<script type="text/javascript" src='<c:url value="/js/utilities/toast.js"/>'></script>
<script type="text/javascript" src='<c:url value="/js/link_entity_to_movie.js"/>'></script>
</body>
</html>