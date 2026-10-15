<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <c:import url="/jsp/include/head.jsp"/>
    <title>${empty movie ? 'Add New Movie' : 'Edit Movie'}</title>
    <meta name="description" content="Wise movie rental app - ${empty movie ? 'Add new movie' : 'Edit movie'}">
</head>
<body>

<c:import url="/jsp/include/header.jsp"/>

<main class="wise-container">

    <div class="wise-form-wrap">
        <h2 class="wise-form-title">${empty movie ? 'Add a Movie' : 'Edit Movie'}</h2>
        <p class="wise-form-deck">
            <c:choose>
                <c:when test="${empty movie}">
                    Fill out the form below to add a new movie to the database. 
                </c:when>
                <c:otherwise>
                    Fill out the form below to edit <c:out value="${movie.title}"/> (<c:out value="${movie.releaseYear}"/>).
                </c:otherwise>
            </c:choose>
        </p>

        <c:import url="/jsp/include/message.jsp"/>

        <form id="movie-save-form" 
              class="wise-form" 
              data-custom-inputs
              action="<c:url value='/auth/staff/movies/save-movie'/>" 
              method="post" 
              enctype="multipart/form-data">

            <c:if test="${not empty movie}">
                <input class="wise-input" type="hidden" name="id" value="${movie.id}">
            </c:if>

            <div class="wise-field">
                <label class="wise-field-label" for="title">
                    <span>Title</span>
                </label>
                <input class="wise-input" type="text" id="title" name="title" 
                       placeholder="Pulp Fiction"
                       required
                       maxlength="200"
                       value="<c:out value='${not empty param.title ? param.title : movie.title}'/>">
                <span class="wise-strength-label wise-error-text hidden" data-error-for="title"></span>
            </div>

            <div class="wise-field">
                <label class="wise-field-label" for="synopsis">
                    <span>Synopsis</span>
                </label>
                <textarea class="wise-input" id="synopsis" name="synopsis" 
                          placeholder="Brief description of the movie..."
                          required><c:out value='${not empty param.synopsis ? param.synopsis : movie.synopsis}'/></textarea>
                <span class="wise-strength-label wise-error-text hidden" data-error-for="synopsis"></span>
            </div>

            <div class="wise-field-row">
                <div class="wise-field">
                    <label class="wise-field-label" for="releaseYear">
                        <span>Release Year</span>
                    </label>
                    <div class="wise-form__field-wrap" data-stepper-container>
                        <button type="button"
                                data-stepper-action="less"
                                class="wise-form__stepper-btn"
                                aria-label="Less">
                            −
                        </button>

                        <input class="wise-form__stepper-input" type="number" id="releaseYear" name="releaseYear" 
                               min="1888" 
                               max="2100"
                               step="1"
                               required
                               value="${not empty param.releaseYear 
                               ? param.releaseYear 
                               : (not empty movie.releaseYear ? movie.releaseYear : 2000)}">

                        <button type="button"
                                data-stepper-action="more"
                                class="wise-form__stepper-btn"
                                aria-label="More">
                            +
                        </button>
                    </div>
                    <span class="wise-strength-label wise-error-text hidden" data-error-for="releaseYear"></span>
                </div>
                <div class="wise-field">
                    <label class="wise-field-label" for="runningTime">
                        <span>Running Time (minutes)</span>
                    </label>
                    <div class="wise-form__field-wrap" data-stepper-container>
                        <button type="button"
                                data-stepper-action="less"
                                class="wise-form__stepper-btn"
                                aria-label="Less">
                            −
                        </button>

                        <input class="wise-form__stepper-input" type="number" id="runningTime" name="runningTime" 
                               min="1"
                               step="1"
                               max="999"
                               required
                               value="${not empty param.runningTime 
                               ? param.runningTime 
                               : (not empty movie.runningTime ? movie.runningTime : 90)}">

                        <button type="button"
                                data-stepper-action="more"
                                class="wise-form__stepper-btn"
                                aria-label="More">
                            +
                        </button>
                    </div>
                    <span class="wise-strength-label wise-error-text hidden" data-error-for="runningTime"></span>
                </div>
            </div>

            <div class="wise-field-row">
                <div class="wise-field">
                    <label class="wise-field-label" for="basePriceEur">
                        <span>Base Price (&euro;)</span>
                    </label>
                    <div class="wise-form__field-wrap" data-stepper-container>
                        <button type="button"
                                data-stepper-action="less"
                                class="wise-form__stepper-btn"
                                aria-label="Less">
                            −
                        </button>

                        <input class="wise-form__stepper-input" type="number" id="basePriceEur" name="basePriceEur" 
                               min="0"
                               step="0.01" 
                               required
                               value="${not empty param.basePriceEur 
                               ? param.basePriceEur 
                               : (not empty movie.basePriceEur ? movie.basePriceEur : 3.99)}">

                        <button type="button"
                                data-stepper-action="more"
                                class="wise-form__stepper-btn"
                                aria-label="More">
                            +
                        </button>
                    </div>
                    <span class="wise-strength-label wise-error-text hidden" data-error-for="basePriceEur"></span>
                </div>
                <div class="wise-field">
                    <label class="wise-field-label" for="rating">
                        <span>Age Rating</span>
                    </label>
                    <select class="wise-input" id="rating" name="rating" required>
                        <option value="" disabled ${empty param.rating and empty movie.ageRating ? 'selected' : ''}>Choose one</option>
                        <c:forEach var="ratingObj" items="${ageRatings}">
                            <option value="${ratingObj.id}" ${(not empty param.rating ? param.rating : movie.ageRating.id) == ratingObj.id ? 'selected' : ''}>
                                ${ratingObj.name}
                            </option>
                        </c:forEach>
                    </select>
                    <span class="wise-strength-label wise-error-text hidden" data-error-for="rating"></span>
                </div>
            </div>

            <div class="wise-field">
                <label class="wise-field-label" for="trailer">
                    <span>Trailer (YouTube ID ONLY)</span>
                </label>
                <input class="wise-input" type="text" id="trailer" name="trailer" 
                       placeholder="dQw4w9WgXcQ"
                       required
                       pattern="^[a-zA-Z0-9_\-]{11}$"
                       title="Please enter exactly the 11-character YouTube ID."
                       value="${not empty param.trailer ? param.trailer : movie.trailer}">
                <span class="wise-strength-label wise-error-text hidden" data-error-for="trailer"></span>
            </div>

            <div class="wise-field-row wise-form__preview-container">
                <c:if test="${not empty movie.posterPic}">
                    <div class="wise-field">
                        <span class="wise-field-label">Current Poster</span>
                        <div class="wise-form__poster-container">
                            <img src="<c:url value='/uploads/${movie.posterPic}'/>" 
                                 class="wise-form__poster"
                                 alt="Current Poster of ${movie.title}">
                        </div>
                    </div>
                </c:if>
                <div class="wise-field hidden" data-preview-for="posterPic">
                    <span class="wise-field-label">New Poster (preview)</span>
                    <div class="wise-form__poster-container">
                        <img src="" 
                             class="wise-form__poster"
                             alt="New Poster">
                    </div>
                </div>
            </div>

            <div class="wise-field">
                <label class="wise-field-label" for="posterPic">
                    <span>${empty movie ? 'Poster Image (Vertical, 2:3)' : 'New Poster Image (Vertical, 2:3)'}</span>
                    <c:if test="${not empty movie}">
                        <span class="wise-field-optional">Optional</span>
                    </c:if>
                </label>
                <div class="wise-form__field-wrap" data-file-container>
                    <input class="wise-form__file-input" type="file" id="posterPic" name="posterPic" 
                       accept=".png, .jpg, .jpeg, image/png, image/jpeg" 
                       ${empty movie ? 'required' : ''}>
                    <button type="button" class="wise-form__clear-btn hidden" data-file-action="clear">
                        <i class="fa-solid fa-circle-xmark"></i>
                    </button>
                </div>
                <span class="wise-strength-label wise-error-text hidden" data-error-for="posterPic"></span>
            </div>

            <div class="wise-field-row">
                <button type="submit" class="wise-btn wise-btn--primary wise-btn--lg wise-btn--block">
                    ${empty movie ? 'Create Movie' : 'Update Movie'}
                </button>

                <%-- Cancel button returning to the list --%>
                <a href="<c:url value='/auth/staff/movies'/>">
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
<script src='<c:url value="/js/movie-media.js"/>'></script>

<script>
    document.addEventListener("DOMContentLoaded", () => {
        // Initialize validator with custom rule map
        new WiseValidator('#movie-save-form', {
            customRules: {
                // Key corresponds directly with the HTML input element ID
                posterPic: validateMoviePoster
            }
        });
    });
</script>