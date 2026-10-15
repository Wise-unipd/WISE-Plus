<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>


<header class="wise-header">
    <%-- Gestisco con un flex... in questi casi torna comodo. Avendo poche icone al momento non dovrebbe servire un componente mobile dedicato --%>
    <div class="wise-header-inner">

        <%-- Lato sx--%>
        <div class="wise-nav">
            <%--Il loghetto (l'ho fatto testuale al posto dlel'immagine. Secondo me più elegante, ma possiamo anche rimettere il vecchio--%>
            <a href="<c:url value='/auth/catalog'/>" class="wise-clean-link">
                <div class="wordmark">
                    <span>WISE</span>
                    <span class="wordmark-plus">+</span>
                </div>
            </a>

            <%--Uso una property impostata a livello di filtro, per sapere quale pagina sto visualizzando e nel caso faccio "l'active"--%>
            <nav class="wise-nav-links">
                <a href="<c:url value='/auth/catalog'/>"
                   class="wise-nav-link wise-nav-links-mobile-hidden ${currentPage == '/auth/catalog' ? 'wise-nav-link--active' : ''}">
                    Catalog
                </a>

                <c:if test="${AccessingUser.userRole.id >= 1 || AppUser.userRole.id >= 1}">
                    <a href="<c:url value='/auth/staff/dashboard'/>"
                       class="wise-nav-link wise-nav-links-mobile-hidden ${currentPage == '/auth/staff/dashboard' ? 'wise-nav-link--active' : ''}">
                        Dashboard
                    </a>
                </c:if>

                <c:if test="${AppUser.userRole.id >= 1}">
                    <a href="<c:url value='/auth/staff/dashboard'/>"
                       class="wise-icon-btn wise-icon-btn--ghost wise-nav-links-mobile-visible ${currentPage == '/auth/staff/dashboard' ? 'wise-nav-link--active-icon' : ''}">
                        <i class="fa-solid fa-gear wise-icon-btn-icon-size"></i>
                    </a>
                </c:if>
            </nav>
        </div>

        <%-- Lato DX--%>
        <div class="wise-nav-actions">
            <a href="<c:url value='/auth/search'/>"
               class="wise-icon-btn wise-icon-btn--ghost ${currentPage == '/auth/search' ? 'wise-nav-link--active-icon' : ''}"
               aria-label="Search">

                <i class="fa-brands fa-sistrix wise-icon-btn-icon-size"></i>
            </a>

            <a href="<c:url value='/auth/wishlist'/>"
               class="wise-icon-btn wise-icon-btn--ghost ${currentPage == '/auth/wishlist' ? 'wise-nav-link--active-icon' : ''}"
               aria-label="Wishlist">
                <i class="fa-regular fa-heart wise-icon-btn-icon-size"></i>
            </a>


            <div class="wise-divider"></div>
            <a href="<c:url value='/auth/profile'/>"
               class="wise-profile wise-clean-link">

                <span class="wise-avatar">
                    ${AppUser.email.substring(0,1).toUpperCase()}
                </span>

                <span class="wise-profile-name">
                    Profile
                </span>
            </a>

            <a href="<c:url value='/logout'/>"
               class="wise-logout wise-clean-link">
                Logout
            </a>
        </div>
    </div>
</header>
