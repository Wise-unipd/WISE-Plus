<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <c:import url="/jsp/include/head.jsp"/>
    <title>Add New Language</title>
    <meta name="description" content="Wise movie rental app - Add new language">
</head>
<body>

<c:import url="/jsp/include/header.jsp"/>

<main class="wise-container">

    <div class="wise-form-wrap">
        <h2 class="wise-form-title">Add a New Language</h2>
        <p class="wise-form-deck">Fill out the form below to add a new language to the database.</p>

        <c:import url="/jsp/include/message.jsp"/>

        <form id="language-add-form"
              class="wise-form"
              action="<c:url value='/auth/staff/languages/add-language'/>"
              method="post">

            <div class="wise-field">
                <label class="wise-field-label" for="name">
                    <span>Name</span>
                </label>
                <input class="wise-input" type="text" id="name" name="name"
                       placeholder="English"
                       required
                       maxlength="50"
                       pattern="^[\p{L}\s\-]{1,50}$"
                       title="Only letters, spaces, and hyphens are allowed (max 50 characters)."
                       value="<c:out value='${param.name}'/>"/>
                <span class="wise-strength-label wise-error-text hidden" data-error-for="name"></span>
            </div>

            <div class="wise-field-row">
                <button type="submit" class="wise-btn wise-btn--primary wise-btn--lg wise-btn--block">Save Language</button>

                <a href="<c:url value='/auth/staff/languages'/>">
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
    document.addEventListener("DOMContentLoaded", () => {
        new WiseValidator('#language-add-form');
    });
</script>

</body>
</html>