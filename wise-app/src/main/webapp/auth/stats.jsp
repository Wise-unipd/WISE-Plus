<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <c:import url="/jsp/include/head.jsp"/>
    <title>WISE - Application Statistics</title>
    <link rel="stylesheet" href="<c:url value='/css/dashboard.css'/>">
    <link rel="stylesheet" href="<c:url value='/css/table.css'/>">
    <link rel="stylesheet" href="<c:url value='/css/users.css'/>">
    <link rel="stylesheet" href="<c:url value='/css/stats.css'/>">
</head>
<body>
<c:import url="/jsp/include/header.jsp"/>

<main class="wise-container dash-container">
    <header class="dash-header">
        <a href="<c:url value='/auth/staff/dashboard'/>" class="wise-btn wise-btn--ghost go-back">
            <i class="fa-solid fa-circle-arrow-left"></i><span>Back to Dashboard</span>
        </a>
        <h1 class="wise-page-title">Application Statistics</h1>
        <p class="wise-page-description">Overview of the application's performance and usage metrics.</p>
    </header>

    <c:if test="${not empty kpi}">
        <section class="stats-section-container">
            <h2 class="dash-section-title">Key Performance Indicators</h2>
            <div class="stats-kpi-grid">
                <div class="dash-section stats-kpi-card">
                    <span class="wise-mono stats-kpi-label">Total Movies</span>
                    <span class="stats-kpi-value"><c:out value="${kpi.total_movies}"/></span>
                </div>
                <div class="dash-section stats-kpi-card">
                    <span class="wise-mono stats-kpi-label">Active Users</span>
                    <span class="stats-kpi-value"><c:out value="${kpi.active_users}"/></span>
                </div>
                <div class="dash-section stats-kpi-card">
                    <span class="wise-mono stats-kpi-label">Total Editors</span>
                    <span class="stats-kpi-value"><c:out value="${kpi.total_editors}"/></span>
                </div>
                <div class="dash-section stats-kpi-card">
                    <span class="wise-mono stats-kpi-label">Active Rents</span>
                    <span class="stats-kpi-value"><c:out value="${kpi.active_rents}"/></span>
                </div>
                <div class="dash-section stats-kpi-card">
                    <span class="wise-mono stats-kpi-label">Total Rents</span>
                    <span class="stats-kpi-value"><c:out value="${kpi.total_rents}"/></span>
                </div>
                <div class="dash-section stats-kpi-card">
                    <span class="wise-mono stats-kpi-label">Total Revenue</span>
                    <span class="stats-kpi-value"><fmt:formatNumber value="${kpi.total_revenue}" minFractionDigits="2" maxFractionDigits="2"/>€</span>
                </div>
            </div>
        </section>
    </c:if>

    <div class="stats-tables-grid">
        <c:if test="${not empty rentalTrends}">
            <section>
                <h2 class="dash-section-title">Rental Trends (per Month)</h2>
                <div class="wise-table-container">
                    <table class="wise-table">
                        <thead class="wise-table__head">
                            <tr class="wise-table__row">
                                <th>Month</th>
                                <th class="wise-table__number">Rents</th>
                            </tr>
                        </thead>
                        <tbody class="wise-table__body">
                            <fmt:setLocale value="en_US" scope="page"/>
                            <c:forEach var="trend" items="${rentalTrends}">
                                <tr class="wise-table__row">
                                    <td>
                                        <fmt:formatDate value="${trend.month}" pattern="MMMM yyyy"/>
                                    </td>
                                    <td class="wise-table__number"><c:out value="${trend.rents}"/></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </section>
        </c:if>

        <c:if test="${not empty mostRentedFilms}">
            <section>
                <h2 class="dash-section-title">Top 10 Most Rented Films (Last 30 Days)</h2>
                <div class="wise-table-container">
                    <table class="wise-table">
                        <thead class="wise-table__head">
                            <tr class="wise-table__row">
                                <th>Movie</th>
                                <th class="wise-table__number">Rent Count</th>
                            </tr>
                        </thead>
                        <tbody class="wise-table__body">
                            <c:forEach var="pair" items="${mostRentedFilms}">
                                <tr class="wise-table__row">
                                    <td class="wise-table__entity-main"><c:out value="${pair.value0.title}"/></td>
                                    <td class="wise-table__number"><c:out value="${pair.value1}"/></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </section>
        </c:if>
    </div>
</main>

<c:import url="/jsp/include/footer.jsp"/>
<c:import url="/jsp/include/back-to-top.jsp"/>
<c:import url="/jsp/include/foot.jsp"/>

<script src="<c:url value='/js/back_to_top.js'/>"></script>

</body>
</html>
