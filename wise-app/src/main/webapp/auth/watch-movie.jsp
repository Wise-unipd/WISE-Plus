<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<c:set var="movie" value="${movieDetails.movie}"/>

<!DOCTYPE html>
<html lang="en">
<head>
    <c:import url="/jsp/include/head.jsp"/>
    <title>Watching: <c:out value="${movie.title}"/></title>
    <link rel="stylesheet" href="<c:url value='/css/watch-movie.css'/>">
</head>
<body>

<c:import url="/jsp/include/header.jsp"/>

<main class="wise-container watch-container">

    <div class="watch-top-bar">
        <a href="<c:url value='/auth/movie'><c:param name='id' value='${movie.id}'/></c:url>"
           class="wise-btn wise-btn--ghost">
            &larr; Go Back
        </a>
        <div class="watch-expiry">
            <span class="wise-mono">Expires:</span>
            <span class="wise-mono watch-expiry-date">
                <fmt:formatDate value="${expiration}" pattern="dd/MM/yyyy, HH:mm:ss" />
            </span>
        </div>
    </div>

    <h1 class="wise-page-title watch-title"><c:out value="${movie.title}"/></h1>

    <section class="watch-theater">
        <iframe
                class="watch-iframe"
                src="https://www.youtube.com/embed/${movie.trailer}?autoplay=1"
                title="<c:out value='${movie.title}'/> Player"
                allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share"
                allowfullscreen>
        </iframe>
    </section>

</main>

<c:import url="/jsp/include/back-to-top.jsp"/>
<c:import url="/jsp/include/footer.jsp"/>
<c:import url="/jsp/include/foot.jsp"/>

<script src="<c:url value='/js/back_to_top.js'/>"></script>

</body>
</html>