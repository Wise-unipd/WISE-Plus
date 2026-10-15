<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<div id="delete-entity-modal" class="wise-modal">
    <div class="wise-modal-content">
        <h3 class="wise-modal-title">Confirm Action</h3>
        <p class="wise-modal-text">
            Are you sure you want to <strong>delete</strong> the <c:out value="${param.entityLabel}"/>: <span data-entity-name></span>?
        </p>
        
        <form data-modal-form action="<c:url value='${param.formAction}'/>" method="post">
            <input type="hidden" name="id" data-input-id value="">

            <div class="wise-modal-actions">
                <button type="button" data-cancel-btn class="wise-btn wise-btn--ghost">Cancel</button>
                <button type="submit" class="wise-btn wise-btn--primary">Confirm</button>
            </div>
        </form>
    </div>
</div>