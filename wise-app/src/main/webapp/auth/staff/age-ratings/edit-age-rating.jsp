<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <c:import url="/jsp/include/head.jsp"/>
    <title>Edit Age Rating</title>
    <meta name="description" content="Wise movie rental app - Edit age rating">
</head>
<body>

<c:import url="/jsp/include/header.jsp"/>

<main class="wise-container">
    <div class="wise-form-wrap">
        <h2 class="wise-form-title">Edit Age Rating</h2>
        <p class="wise-form-deck">Fill out the form below to edit <c:out value="${ageRating.name}"/>.</p>

        <c:import url="/jsp/include/message.jsp"/>

        <form id="age-rating-edit-form" 
              class="wise-form" 
              data-custom-inputs
              action='<c:url value="/auth/staff/age-ratings/edit-age-rating"/>' 
              method="post">

            <input class="wise-input" type="hidden" name="id" value="${ageRating.id}">

            <div class="wise-field">
                <label class="wise-field-label" for="name">
                    <span>Name</span>
                </label>
                <input class="wise-input" type="text" id="name" name="name" 
                       placeholder="18+"
                       required
                       maxlength="50"
                       value="${not empty param.name ? param.name : ageRating.name}">
                <span class="wise-strength-label wise-error-text hidden" data-error-for="name"></span>
            </div>

            <div class="wise-field">
                <label class="wise-field-label" for="description">
                    <span>Description</span>
                </label>
                <textarea class="wise-input" id="description" name="description" 
                          placeholder="Brief description of the age rating..."
                          required><c:out value='${not empty param.description ? param.description : ageRating.description}'/></textarea>
                <span class="wise-strength-label wise-error-text hidden" data-error-for="description"></span>
            </div>

            <div class="wise-field">
                <label class="wise-field-label" for="age">
                    <span>Age (between 0 and 18)</span>
                </label>
                <div class="wise-form__field-wrap" data-stepper-container>
                    <button type="button"
                            data-stepper-action="less"
                            class="wise-form__stepper-btn"
                            aria-label="Less">
                        −
                    </button>

                    <input class="wise-form__stepper-input" type="number" id="age" name="age" 
                           min="0" 
                           max="18"
                           step="1"
                           required
                           value="${not empty param.age ? param.age : ageRating.age}">

                    <button type="button"
                            data-stepper-action="more"
                            class="wise-form__stepper-btn"
                            aria-label="More">
                        +
                    </button>
                </div>
                <span class="wise-strength-label wise-error-text hidden" data-error-for="age"></span>
            </div>
    
            <div class="wise-field-row">
                <button type="submit" class="wise-btn wise-btn--primary wise-btn--lg wise-btn--block">Update</button>
                <a href="<c:url value='/auth/staff/age-ratings'/>">
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
<script src='<c:url value="/js/form.js"/>'></script>
<script src='<c:url value="/js/form-validation.js"/>'></script>
<script>
    // Initialize the form-validation of the class by passing the ID of the form
    document.addEventListener("DOMContentLoaded", () => {
        new WiseValidator('#age-rating-edit-form');
    });
</script>
</body>
</html>