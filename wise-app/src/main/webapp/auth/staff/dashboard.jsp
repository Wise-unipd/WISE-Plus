<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <c:import url="/jsp/include/head.jsp"/>
    <title>WISE - Dashboard</title>
    <meta name="description" content="Wise movie rental app - Dashboard">
    <link rel="stylesheet" href="<c:url value='/css/dashboard.css'/>">
</head>
<body>

<c:import url="/jsp/include/header.jsp"/>

<main class="wise-container dash-container">
    <header class="dash-header">
        <h1 class="wise-page-title">
            <c:choose>
                <c:when test="${AppUser.userRole.id == 2}">Admin Dashboard</c:when>
                <c:otherwise>Staff Dashboard</c:otherwise>
            </c:choose>
        </h1>
    </header>

    <div class="dash-grid">
        <section class="dash-section">
            <h2 class="dash-section-title">User</h2>
            <div class="dash-link-group">
                <a href='<c:url value="/auth/staff/profiles"/>' class="wise-btn wise-btn--ghost">Profiles</a>
            </div>
        </section>

        <section class="dash-section">
            <h2 class="dash-section-title">Rentals</h2>
            <div class="dash-link-group">
                <a href='<c:url value="/auth/staff/rentals"/>' class="wise-btn wise-btn--ghost">Rentals</a>
            </div>
        </section>

        <section class="dash-section">
            <h2 class="dash-section-title">Movie</h2>
            <div class="dash-link-group">
                <a href='<c:url value="/auth/staff/movies"/>' class="wise-btn wise-btn--ghost">Movies</a>
                <a href='<c:url value="/auth/staff/categories"/>' class="wise-btn wise-btn--ghost">Categories</a>
                <a href='<c:url value="/auth/staff/workers"/>' class="wise-btn wise-btn--ghost">Workers</a>
                <a href='<c:url value="/auth/staff/genres"/>' class="wise-btn wise-btn--ghost">Genres</a>
                <a href='<c:url value="/auth/staff/languages"/>' class="wise-btn wise-btn--ghost">Languages</a>
                <a href='<c:url value="/auth/staff/age-ratings"/>' class="wise-btn wise-btn--ghost">Age Ratings</a>
            </div>
        </section>

        <c:if test="${AppUser.userRole.id == 2}">
            <section class="dash-section">
                <h2 class="dash-section-title">Admin Tools</h2>
                <div class="dash-link-group">
                    <a href='<c:url value="/auth/staff/stats"/>' class="wise-btn wise-btn--ghost">Application Statistics</a>
                </div>
            </section>
        </c:if>
    </div>
</main>

<c:import url="/jsp/include/back-to-top.jsp" />
<c:import url="/jsp/include/footer.jsp" />
<c:import url="/jsp/include/foot.jsp"/>

<script src="<c:url value='/js/back_to_top.js'/>"></script>
</body>
</html>
