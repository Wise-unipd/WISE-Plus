<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <c:import url="/jsp/include/head.jsp"/>
    <title>Change User Role</title>
    <meta name="description" content="Wise movie rental app - Change user role page">
    <link rel="stylesheet" href="<c:url value='/css/change-user-role.css'/>">
</head>
<body>

<c:import url="/jsp/include/header.jsp"/>

<main class="wise-container">

    <a href="<c:url value='/auth/staff/profiles'/>"
       class="wise-btn wise-btn--ghost go-back">
        <i class="fa-solid fa-circle-arrow-left"></i><span>Back to Profiles</span>
    </a>

    <h2 class="wise-page-title">Change Role for User ID: <c:out value="${targetUserId}"/></h2>

    <c:import url="/jsp/include/message.jsp"/>

    <form action="" method="post" class="change-role-form">

        <div class="wise-field">
            <label class="wise-field-label" for="newRoleId">
                <span>Select New Role</span>
            </label>
            <select name="newRoleId" id="newRoleId" class="wise-input" required>
                <option value="" disabled selected>-- Select a Role --</option>
                <option value="0">User</option>
                <option value="1">Staff</option>
                <option value="2">Admin</option>
            </select>
        </div>

        <button type="submit" class="wise-btn wise-btn--primary wise-btn--block">
            <span>Update Role</span>
            <i class="fa-solid fa-user-pen"></i>
        </button>

    </form>
</main>

<c:import url="/jsp/include/back-to-top.jsp" />
<c:import url="/jsp/include/footer.jsp" />
<c:import url="/jsp/include/foot.jsp"/>

<script src="<c:url value='/js/back_to_top.js'/>"></script>
</body>
</html>