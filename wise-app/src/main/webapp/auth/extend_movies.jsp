<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <c:import url="/jsp/include/head.jsp"/>
    <title>WISE - Extend Rental</title>
    <meta name="description" content="Wise movie rental app - Extend Rental page">
    <link rel="stylesheet" href="<c:url value='/css/payment_page.css'/> ">
</head>
<body class="payment-page">

<c:import url="/jsp/include/header.jsp"/>

<main class="wise-container" >
    <div class="payment-wrapper">
        <div class="payment-card">

            <c:if test="${not empty movie}">

                <div class="payment-summary">
                    <h1 class="payment-summary__movie">Extend: <c:out value="${movie.title}"/></h1>
                    <p class="payment-summary__total">
                        <span><fmt:formatNumber value="${movie.basePriceEur}" minFractionDigits="2" maxFractionDigits="2"/></span>€/day
                    </p>
                </div>

                <c:import url="/jsp/include/message.jsp"/>

                <form id="extend-payment-form" 
                      action="<c:url value='/auth/extend/'/>"
                      method="post" 
                      class="wise-form" 
                      data-custom-inputs
                      data-base-price="${basePrice}"
                      data-current-expiration="${currentExpirationMs}">

                    <input type="hidden" name="movieId" value="${movie.id}"/>

                    <div class="wise-field">
                        <label class="wise-field-label" for="days">
                            <span>Days to Extend</span>
                        </label>
                        <div class="wise-form__field-wrap" data-stepper-container>
                            <button type="button" data-stepper-action="less" class="wise-form__stepper-btn" aria-label="Less">
                                −
                            </button>

                            <input class="wise-form__stepper-input" type="number" id="days" name="days"
                                   min="1" max="30" step="1" required
                                   value="1">

                            <button type="button" data-stepper-action="more" class="wise-form__stepper-btn" aria-label="More">
                                +
                            </button>
                        </div>
                        <span class="wise-strength-label wise-error-text hidden" data-error-for="days"></span>
                    </div>

                    <div class="payment-info-box">
                        <div class="payment-info-row">
                            <span>Total Price</span>
                            <strong id="tot_price"><fmt:formatNumber value="${movie.basePriceEur}" minFractionDigits="2" maxFractionDigits="2"/>€</strong>
                        </div>

                        <div class="payment-info-row">
                            <span>New Expiration Date</span>
                            <strong id="new_expiration"></strong>
                        </div>
                    </div>

                    <div class="wise-field">
                        <label class="wise-field-label" for="cardNumber">
                            <span>Card Number</span>
                        </label>
                        <input class="wise-input" type="text" id="cardNumber"
                               name="cardNumber" placeholder="4242 4242 4242 4242" required
                               pattern="^(?:\d{4}\s?){4}$|^\d{16}$"
                               maxlength="19"
                               title="Please enter a valid 16-digit credit card number."
                               value="<c:out value='${param.cardNumber}'/>">
                        <span class="wise-strength-label wise-error-text hidden" data-error-for="cardNumber"></span>
                    </div>

                    <div class="wise-field-row">
                        <div class="wise-field">
                            <label class="wise-field-label" for="expiryDate">
                                <span>Expiration Date</span>
                            </label>
                            <input class="wise-input" type="text" id="expiryDate"
                                   name="expiryDate" placeholder="MM/YY" required
                                   pattern="^(0[1-9]|1[0-2])/([0-9]{2})$"
                                   maxlength="5"
                                   title="Please enter a valid date in MM/YY format."
                                   value="<c:out value='${param.expiryDate}'/>">
                            <span class="wise-strength-label wise-error-text hidden" data-error-for="expiryDate"></span>
                        </div>
                        <div class="wise-field">
                            <label class="wise-field-label" for="cvv">
                                <span>CVV</span>
                            </label>
                            <input class="wise-input" type="text" id="cvv"
                                   name="cvv" placeholder="•••" required
                                   pattern="^[0-9]{3,4}$"
                                   maxlength="4"
                                   title="Please enter a valid 3 or 4 digit security code."
                                   value="<c:out value='${param.cvv}'/>">
                            <span class="wise-strength-label wise-error-text hidden" data-error-for="cvv"></span>
                        </div>
                    </div>

                    <div class="payment-actions">
                        <button type="submit" class="wise-btn wise-btn--primary wise-btn--lg">
                            Confirm Extension
                            <i class="fa-solid fa-arrow-right"></i>
                        </button>

                        <a href="<c:url value='/auth/movie'><c:param name='id' value='${movie.id}'/></c:url>"
                           class="wise-btn wise-btn--ghost wise-btn--lg">
                            Cancel
                        </a>
                    </div>
                </form>
            </c:if>

        </div>
    </div>
</main>

<c:import url="/jsp/include/footer.jsp"/>
<c:import url="/jsp/include/back-to-top.jsp" />
<c:import url="/jsp/include/foot.jsp"/>

<script src="<c:url value='/js/back_to_top.js'/>"></script>
<script src='<c:url value="/js/form.js"/>'></script>
<script src='<c:url value="/js/form-validation.js"/>'></script>
<script src='<c:url value="/js/payment.js"/>'></script>
<script src='<c:url value="/js/extend-rental.js"/>'></script>

</body>
</html>