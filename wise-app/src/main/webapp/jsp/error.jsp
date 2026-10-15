<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <c:import url="include/head.jsp"/>
    <title>
        Wise | 
        <c:choose>
            <c:when test="${pageContext.errorData.statusCode == 404}">Page Not Found</c:when>
            <c:when test="${pageContext.errorData.statusCode == 403}">Access Denied</c:when>
            <c:otherwise>System Error</c:otherwise>
        </c:choose>
    </title>
    <link rel="stylesheet" href="<c:url value='/css/welcome_page.css'/>">
</head>
<body>

<main>
    <section class="wise-hero-welcome">
        <div class="wise-hero-welcome__info">

            <a href="<c:url value='/auth/catalog'/>" class="wise-clean-link">
                <div class="wordmark">
                    <span>WISE</span>
                    <span class="wordmark-plus">+</span>
                </div>
            </a>

            <span class="wise-mono wise-hero-welcome__eyebrow">
                Error Code: <c:out value="${pageContext.errorData.statusCode}"/>
            </span>

            <h1 class="wise-hero-welcome__title">
                <c:choose>
                    <c:when test="${pageContext.errorData.statusCode == 404}">
                        Lost Your <span class="wise-hero-welcome__title__break">Way?</span>
                    </c:when>
                    <c:when test="${pageContext.errorData.statusCode == 403}">
                        Restricted <span class="wise-hero-welcome__title__break">Area</span>
                    </c:when>
                    <c:otherwise>
                        Something Went <span class="wise-hero-welcome__title__break">Wrong</span>
                    </c:otherwise>
                </c:choose>
            </h1>

            <p class="wise-hero-welcome-message">
                <c:choose>
                    <c:when test="${pageContext.errorData.statusCode == 404}">
                        We can't find the page you are looking for. Let's get you back to the cinema.
                    </c:when>
                    <c:when test="${pageContext.errorData.statusCode == 403}">
                        Sorry, you don't have permission to access this resource.
                    </c:when>
                    <c:otherwise>
                        An unexpected error occurred. Please try again later or contact us.
                    </c:otherwise>
                </c:choose>
            </p>

            <div class="wise-hero-welcome__cta">
                <a href='<c:url value="/auth/catalog"/>' class="wise-btn wise-btn--primary wise-btn--lg wise-btn--block wise-clean-link">
                    Go to Catalog
                </a>
            </div>

        </div>
    </section>
</main>

<c:import url="include/footer.jsp"/>
<c:import url="include/foot.jsp"/>

</body>
</html>