<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<c:if test="${not empty message}">
    <div id="status-msg" class="wise-status-msg ${message.error ? 'wise-status-msg--error' : 'wise-status-msg--success'}" role="alert">
        <div class="wise-status-msg__wrapper">
            <div class="wise-status-msg__content">
                <span class="wise-status-msg__icon">
                    <i aria-hidden="true" class="fa-solid ${message.error ? 'fa-circle-exclamation' : 'fa-check'}"></i>
                </span>
                <p class="wise-status-msg__content__text"><c:out value="${message.message}"/></p>
            </div>
            <button type="button"
                    id="status-msg-close-btn"
                    aria-label="Close status message"
                    class="wise-status-msg__close-btn">
                <span class="wise-status-msg__icon"><i aria-hidden="true" class="fa-solid fa-xmark"></i></span>
            </button>
        </div>
    </div>
</c:if>
