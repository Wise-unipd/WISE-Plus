<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<div id="toggle-entity-modal" class="wise-modal">
    <div class="wise-modal-content">
        <h3 class="wise-modal-title">Confirm Action</h3>
        <p class="wise-modal-text">
            Are you sure you want to <strong data-action-text></strong> the <c:out value="${param.entityLabel}"/>: <span data-entity-name></span>?
        </p>
        
        <form data-modal-form action="<c:url value='${param.formAction}'/>" method="post">
            <input type="hidden" name="id" data-input-id value="">
            <input type="hidden" name="active" data-input-active value="">

            <div class="wise-modal-actions">
                <button type="button" data-cancel-btn class="wise-btn wise-btn--ghost">Cancel</button>
                <button type="submit" class="wise-btn wise-btn--primary">Confirm</button>
            </div>
        </form>
    </div>
</div>