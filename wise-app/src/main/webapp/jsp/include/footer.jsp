<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<footer class="wise-footer">
    <a href="<c:url value='/auth/catalog'/>" class="wise-clean-link">
        <div class="wordmark">
            <span>WISE</span>
            <span class="wordmark-plus">+</span>
        </div>
    </a>
    <p class="wise-footer__copyright">
        <jsp:useBean id="now" class="java.util.Date"/>
        © <fmt:formatDate value="${now}" pattern="yyyy"/>
        WISE. All rights reserved.
    </p>

    <div class="wise-footer__links">
        <a href="<c:url value='/auth/catalog'/>" class="wise-footer__link wise-clean-link">Catalog</a>
        <a href="mailto:wise.unipd@gmail.com?subject=Contact%20Support" class="wise-footer__link wise-clean-link">Support</a>
    </div>

</footer>
