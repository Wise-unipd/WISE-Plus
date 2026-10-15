<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <c:import url="/jsp/include/head.jsp"/>
    <title>Add New Worker</title>
    <meta name="description" content="Wise movie rental app - Add new worker">
</head>
<body>

<c:import url="/jsp/include/header.jsp"/>

<main class="wise-container">

    <div class="wise-form-wrap">
        <h2 class="wise-form-title">Add a Worker</h2>
        <p class="wise-form-deck">Fill out the form below to add a new actor or director to the database.</p>

        <c:import url="/jsp/include/message.jsp"/>

        <form id="worker-add-form" 
              class="wise-form" 
              action="<c:url value='/auth/staff/workers/add-worker'/>" 
              method="POST">

            <div class="wise-field">
                <label class="wise-field-label" for="name">
                    <span>First Name</span>
                </label>
                <input class="wise-input" type="text" id="name" name="name"
                       placeholder="Jane"
                       required
                       maxlength="50"
                       pattern="^[\p{L}\s\-']{1,50}$"
                       title="Only letters, spaces, hyphens, and apostrophes are allowed."
                       value="<c:out value='${param.name}'/>">
                <span class="wise-strength-label wise-error-text hidden" data-error-for="name"></span>
            </div>

            <div class="wise-field">
                <label class="wise-field-label" for="surname">
                    <span>Last Name</span>
                </label>
                <input class="wise-input" type="text" id="surname" name="surname"
                       placeholder="Doe"
                       required
                       maxlength="50"
                       pattern="^[\p{L}\s\-']{1,50}$"
                       title="Only letters, spaces, hyphens, and apostrophes are allowed."
                       value="<c:out value='${param.surname}'/>"/>
                <span class="wise-strength-label wise-error-text hidden" data-error-for="surname"></span>
            </div>

            <div class="wise-field-row">
                <button type="submit" class="wise-btn wise-btn--primary wise-btn--lg wise-btn--block">Save Worker</button>

                <%-- Cancel button returning to the list --%>
                <a href="<c:url value='/auth/staff/workers'/>">
                    <button type="button" class="wise-btn wise-btn--ghost wise-btn--lg wise-btn--block">Cancel</button>
                </a>
            </div>

        </form>
    </div>
</main>

<c:import url="/jsp/include/back-to-top.jsp" />
<c:import url="/jsp/include/footer.jsp"/>
<c:import url="/jsp/include/foot.jsp"/>

<script src="<c:url value='/js/back_to_top.js'/>"></script>
<script src='<c:url value="/js/form-validation.js"/>'></script>
<script>
    // Initialize the form-validation of the class by passing the ID of the form
    document.addEventListener("DOMContentLoaded", () => {
        new WiseValidator('#worker-add-form');
    });
</script>

</body>
</html>