<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <c:import url="/jsp/include/head.jsp"/>
    <title>Profiles</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/table.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/pagination.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/users.css">
</head>
<body>

<c:import url="/jsp/include/header.jsp"/>

<main class="wise-container users-container">

    <div id="table-anchor" class="wise-table-anchor"></div>

    <header class="users-header">
        <a href="<c:url value='/auth/staff/dashboard'/>" class="wise-btn wise-btn--ghost go-back">
            <i class="fa-solid fa-circle-arrow-left"></i><span>Back to Dashboard</span>
        </a>
        <h1 class="wise-page-title">Registered Users</h1>
        <div class="users-search-bar">
            <input type="text" id="searchInput" class="users-search-input" placeholder="Search Users by Name or Email..." autocomplete="off" autofocus>
        </div>
    </header>

    <c:import url="/jsp/include/message.jsp"/>

    <div id="resultsContainer">
        <div class="wise-table-container w-full">
            <table id="users-table" class="wise-table min-w-full">
                <thead class="wise-table__head">
                <tr class="wise-table__row">
                    <th>ID</th>
                    <th>Email</th>
                    <th>First Name</th>
                    <th>Last Name</th>
                    <th>Role</th>
                    <th>Created</th>
                    <th>Last Update</th>
                    <th>Status</th>
                    <th>Actions</th>
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

    <div id="toggleUserModal" class="wise-modal">
        <div class="wise-modal-content">
            <h3 class="wise-modal-title">Confirm Action</h3>
            <p class="wise-modal-text">Are you sure you want to <strong id="modalActionText"></strong> this account?</p>
            <div class="wise-modal-actions">
                <button type="button" id="modalCancelBtn" class="wise-btn wise-btn--ghost">Cancel</button>
                <button type="button" id="modalConfirmBtn" class="wise-btn wise-btn--primary">Confirm</button>
            </div>
        </div>
    </div>

</main>

<c:import url="/jsp/include/footer.jsp"/>
<c:import url="/jsp/include/back-to-top.jsp"/>
<c:import url="/jsp/include/foot.jsp"/>

<script src="<c:url value='/js/back_to_top.js'/>"></script>

<script>
    const LOGGED_USER_ROLE_ID = ${not empty AppUser ? AppUser.userRole.id : 'undefined'};
    const LOGGED_USER_ID = ${not empty AppUser ? AppUser.id : 'undefined'};
</script>
<script src="${pageContext.request.contextPath}/js/search_users_ajax.js"></script>
<script src="${pageContext.request.contextPath}/js/utilities/pagination.js"></script>
</body>
</html>