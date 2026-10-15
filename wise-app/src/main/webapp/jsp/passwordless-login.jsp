<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <c:import url="include/head.jsp"/>
    <title>WISE+ — Magic link</title>
    <link rel="stylesheet" href="<c:url value='/css/auth.css'/>">
</head>
<body>

<div class="wise-auth">
    <aside class="wise-auth-show wise-auth-logo-mobile--hidden">
        <header class="wise-auth-show-head">
            <a href="<c:url value='/'/>" class="wise-clean-link">
                <div class="wordmark">
                    <span>WISE</span>
                    <span class="wordmark-plus">+</span>
                </div>
            </a>
        </header>

        <div class="wise-auth-show-body">
            <h1 class="wise-auth-headline">
                One tap. <em>You're in.</em>
            </h1>
        </div>

        <footer class="wise-auth-show-foot">
            <div class="wise-auth-stats">
                <div>
                    <div class="wise-auth-stat-num"><c:out value="${kpi.total_movies}"/></div>
                    <div class="wise-auth-stat-lbl">Titles</div>
                </div>
                <div>
                    <div class="wise-auth-stat-num"><c:out value="${kpi.total_editors}"/></div>
                    <div class="wise-auth-stat-lbl">Editors</div>
                </div>
            </div>
        </footer>
    </aside>

    <div class="wise-auth-form-col">
        <header class="wise-auth-form-head">
            <span>Need help? <a href="mailto:wise.unipd@gmail.com?subject=Contact%20Support">Contact support</a></span>
            <a href="<c:url value='/'/>" class="wise-clean-link">
                <div class="wordmark wise-auth-logo-mobile--visible">
                    <span>WISE</span>
                    <span class="wordmark-plus">+</span>
                </div>
            </a>
        </header>

        <div class="wise-auth-form-wrap">
            <c:choose>
                <c:when test="${not empty message && !message.error}">
                    <div class="wise-magic-sent">
                        <div class="wise-magic-icon">
                            <i class="fa-regular fa-envelope"></i>
                        </div>
                        <h2 class="wise-magic-title">Check your inbox.</h2>
                        <p class="wise-magic-body">
                            We sent a link to<br/>
                            <span class="wise-magic-email"><c:out value="${param.email}"/></span><br/>
                            <span class="wise-magic-text">
                                Link expires in 15 minutes. You can close this tab.
                            </span>
                        </p>
                        <a href="<c:url value='/login-help'/>" class="wise-auth-alt-btn">
                            Use a different email
                        </a>
                        <div class="wise-auth-foot-help">
                            Prefer a password?
                            <a href="<c:url value='/login'/>">Sign in the classic way!</a>
                        </div>
                    </div>
                </c:when>

                <c:otherwise>
                    <div class="wise-auth-form-eyebrow">
                        <span class="wise-mono">WISE+</span>
                        <span class="wise-auth-form-eyebrow-rule"></span>
                    </div>
                    <h2 class="wise-auth-form-title">Magic link.</h2>
                    <p class="wise-auth-form-deck">
                        No password to remember. We'll email a single-use link
                        that expires in 15 minutes.
                    </p>

                    <c:import url="/jsp/include/message.jsp"/>

                    <form id="passwordless-form" class="wise-form" action="<c:url value='/login-help'/>" method="post">
                        <div class="wise-field">
                            <label class="wise-field-label" for="email">
                                <span>Email</span>
                            </label>
                            <input class="wise-input" type="email" id="email" name="email"
                                   placeholder="me@mail.com"
                                   required autocomplete="email"
                                   title="Please enter a valid email address."/>
                            <span class="wise-strength-label wise-error-text hidden" data-error-for="email"></span>
                        </div>

                        <button type="submit" class="wise-auth-submit">
                            Send magic link
                            <i class="fa-solid fa-arrow-right"></i>
                        </button>
                    </form>

                    <div class="wise-auth-foot-help">
                        Prefer a password?
                        <a href="<c:url value='/login'/>">Sign in the classic way!</a>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>

<c:import url="include/foot.jsp"/>

<script src="<c:url value='/js/form-validation.js'/>"></script>
<script>
    document.addEventListener("DOMContentLoaded", () => {
        new WiseValidator('#passwordless-form');
    });
</script>

</body>
</html>
