<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <c:import url="/jsp/include/head.jsp"/>
    <title>Rentals</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/table.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/pagination.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/rentals.css">
</head>
<body>

<c:import url="/jsp/include/header.jsp"/>

<main class="wise-container rentals-container">

    <div id="table-anchor" class="wise-table-anchor"></div>

    <header class="rentals-header">
        <a href="<c:url value='/auth/staff/dashboard'/>" class="wise-btn wise-btn--ghost go-back">
            <i class="fa-solid fa-circle-arrow-left"></i><span>Back to Dashboard</span>
        </a>
        <h1 class="wise-page-title">Rentals</h1>

        <div class="rentals-search-bar">
            <input type="text" id="searchInput" class="rentals-search-input" placeholder="Search Rental by Email or Movie Title" autocomplete="off" autofocus>

            <div class="rentals-search-filters">
                <label class="rentals-checkbox-label">
                    <input type="checkbox" id="checkEmail" checked> Email
                </label>
                <label class="rentals-checkbox-label">
                    <input type="checkbox" id="checkTitle" checked> Film Title
                </label>
            </div>
        </div>
    </header>

    <c:import url="/jsp/include/message.jsp"/>

    <div id="resultsContainer">
        <div class="wise-table-container w-full">
            <table id="rentals-table" class="wise-table min-w-full">
                <thead class="wise-table__head">
                <tr class="wise-table__row">
                    <th>ID</th>
                    <th>User Email</th>
                    <th>First Name</th>
                    <th>Last Name</th>
                    <th>Movie Title</th>
                    <th>Checkout</th>
                    <th>Expiration</th>
                    <th>Status</th>
                </tr>
                </thead>
                <tbody class="wise-table__body">
                </tbody>
            </table>

            <div id="pagination-area" class="wise-pagination hidden">
                <ol class="wise-pagination__list"></ol>
            </div>
        </div>
    </div>

</main>

<c:import url="/jsp/include/footer.jsp"/>
<c:import url="/jsp/include/back-to-top.jsp"/>
<c:import url="/jsp/include/foot.jsp"/>

<script src="<c:url value='/js/back_to_top.js'/>"></script>

<script src="${pageContext.request.contextPath}/js/search_rentals_ajax.js"></script>
<script src="${pageContext.request.contextPath}/js/utilities/pagination.js"></script>
</body>
</html>