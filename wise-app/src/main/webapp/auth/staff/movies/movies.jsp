<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <c:import url="/jsp/include/head.jsp"/>
    <title>Manage Movies</title>
    <meta name="description" content="Wise movie rental app - Movie management page">
    <link rel="stylesheet" href="<c:url value='/css/dropdown.css'/>"/>
    <link rel="stylesheet" href="<c:url value='/css/pagination.css'/>"/>
    <link rel="stylesheet" href="<c:url value='/css/table.css'/>"/>
    <link rel="stylesheet" href="<c:url value='/css/movie-dashboard.css'/>"/>
</head>
<body>

<c:import url="/jsp/include/header.jsp"/>

<main class="wise-container">

    <a href="<c:url value='/auth/staff/dashboard'/>"
       class="wise-btn wise-btn--ghost go-back">
        <i class="fa-solid fa-circle-arrow-left"></i><span>Back to the Dashboard</span>
    </a>

    <div id="table-anchor" class="wise-table-anchor"></div>

    <h2 class="wise-page-title">Manage Movies</h2>

    <c:import url="/jsp/include/message.jsp"/>

    <div class="main-actions">
        <a href="<c:url value='/auth/staff/movies/save-movie'/>"
           class="wise-btn wise-btn--primary">
            Add New Movie
            <i class="fa-solid fa-circle-plus"></i>
        </a>
        <div class="search-container">
            <span class="search-container__icon">
                <i class="fa-solid fa-magnifying-glass"></i>
            </span>
            <input type="text"
                   id="search-input"
                   placeholder="Search movies by title, actor or genre..."
                   aria-label="Search movie"
                   autocomplete="off"
                   class="search-container__input">
        </div>
    </div>

    <div class="wise-table-container w-full">
        <table id="movie-table"
                data-entity-table
                class="wise-table min-w-full wise-table--high-lower-spacing">
            <thead class="wise-table__head">
            <tr class="wise-table__row">
                <th>ID</th>
                <th>Title</th>
                <th>Year</th>
                <th>Price</th>
                <th>Status</th>
                <th>Relations</th>
                <th>Actions</th>
            </tr>
            </thead>
            <tbody class="wise-table__body"></tbody>
        </table>
        <div id="pagination-area" class="wise-pagination hidden">
            <ol class="wise-pagination__list"></ol>
        </div>
    </div>
</main>

<c:import url="/jsp/include/toggle-modal.jsp">
    <c:param name="entityLabel" value="movie" />
    <c:param name="formAction" value="/auth/staff/movies/toggle-movie" />
</c:import> 
<c:import url="/jsp/include/back-to-top.jsp" />
<c:import url="/jsp/include/footer.jsp"/>
<c:import url="/jsp/include/foot.jsp"/>

<script src="<c:url value='/js/back_to_top.js'/>"></script>
<script src='<c:url value="/js/dropdown.js"/>'></script>
<script src="${pageContext.request.contextPath}/js/search_movies.js"></script>
<script src="<c:url value='/js/utilities/toggle_modal.js'/>"></script>
<script src="<c:url value='/js/movie-entity-dashboard.js'/>"></script>
</body>
</html>