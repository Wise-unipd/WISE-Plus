<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<div id="toast"
     role="alert"
     aria-live="polite"
     aria-atomic="true"
     class="toast">
     <div class="toast__message">
          <span class="toast__message__icon"><i id="toast-status-icon" aria-hidden="true" class="fa-solid"></i></span>
          <div class="toast__message__content">
               <span id="toast-status" class="toast__message__content__status"></span>
               <p id="toast-message-text" class="toast__message__content__text"></p>
          </div>
          <button type="button"
                  id="toast-close-btn"
                  aria-label="Close toast"
                  class="toast__message__close-btn">
               <span class="toast__message__icon"><i aria-hidden="true" class="fa-solid fa-xmark"></i></span>
          </button>
     </div>
</div>
