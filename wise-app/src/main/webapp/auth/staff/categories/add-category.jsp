<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <c:import url="/jsp/include/head.jsp"/>
    <title>Add New Category</title>
    <meta name="description" content="Wise movie rental app - Add new category">
</head>
<body>

<c:import url="/jsp/include/header.jsp"/>

<main class="wise-container">

    <div class="wise-form-wrap">
        <h2 class="wise-form-title">Add a Category</h2>
        <p class="wise-form-deck">Fill out the form below to add a new category to the database.</p>

        <c:import url="/jsp/include/message.jsp"/>

        <form id="category-add-form" 
              class="wise-form" 
              action='<c:url value="/auth/staff/categories/add-category"/>' 
              method="POST">

            <div class="wise-field">
                <label class="wise-field-label" for="name">
                    <span>Name</span>
                </label>
                <input class="wise-input" type="text" id="name" name="name"
                       placeholder="Sci-Fi Collection"
                       required
                       maxlength="100"
                       value="<c:out value='${param.name}'/>"/>
            </div>

            <div class="wise-field">
                <label class="wise-field-label" for="description">
                    <span>Description</span>
                </label>
                <textarea class="wise-input" id="description" name="description"
                          placeholder="Provide a detailed description of this grouping..."
                          required><c:out value='${param.description}'/></textarea>
            </div>

            <div class="wise-field-row">
                <button type="submit" class="wise-btn wise-btn--primary wise-btn--lg wise-btn--block">Save Category</button>
                <a href="<c:url value='/auth/staff/categories'/>">
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
        new WiseValidator('#category-add-form');
    });
</script>
</body>
</html>