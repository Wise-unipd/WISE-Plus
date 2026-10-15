<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <c:import url="include/head.jsp"/>
    <title>WISE+ — Sign in</title>
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
                Welcome <em>back</em> to the cinema you remember.
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
                <div class="wordmark  wise-auth-logo-mobile--visible">
                    <span>WISE</span>
                    <span class="wordmark-plus">+</span>
                </div>
            </a>
        </header>


        <div class="wise-auth-form-wrap">
            <div class="wise-auth-form-eyebrow">
                <span class="wise-mono">WISE+</span>
                <span class="wise-auth-form-eyebrow-rule"></span>
            </div>
            <h2 class="wise-auth-form-title">Sign in.</h2>
            <p class="wise-auth-form-deck">
                Pick up where you left off — your watchlist and rentals are waiting.
            </p>

            <c:if test="${not empty param.error}">
                <p class="wise-strength-label wise-auth-error-text" role="alert">
                    Wrong email or password. Please try again.
                </p>
            </c:if>
            <c:import url="/jsp/include/message.jsp"/>


            <form id="login-form" class="wise-form" data-auth-form action="<c:url value='/login'/>" method="post">
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

                <div class="wise-field">
                    <label class="wise-field-label" for="password">
                        <span>Password</span>
                        <a href="<c:url value='/login-help'/>"
                           class="wise-lost-password">
                            Forgot the password?
                        </a>
                    </label>
                    <div class="wise-input-wrap">
                        <input class="wise-input wise-input--has-icon"
                               type="password" id="password" name="password"
                               placeholder="••••••••"
                               required autocomplete="current-password"
                               title="Please enter your password."/>
                        <button type="button" class="wise-input-icon"
                                data-action="toggle-password"
                                data-toggle-for="password"
                                aria-label="Toggle password visibility">
                            <svg data-id="pw-icon" width="14" height="14" viewBox="0 0 24 24"
                                 fill="none" stroke="currentColor" stroke-width="1.6">
                                <path d="M2 12s4-7 10-7 10 7 10 7-4 7-10 7S2 12 2 12z"/>
                                <circle cx="12" cy="12" r="3"/>
                            </svg>
                        </button>
                    </div>
                    <span class="wise-strength-label wise-error-text hidden" data-error-for="password"></span>
                </div>


                <button type="submit" class="wise-auth-submit">
                    Sign in
                    <i class="fa-solid fa-arrow-right"></i>
                </button>

            </form>

            <div class="wise-auth-foot-help">
                Don't have an account?
                <a href="<c:url value='/register'/>">Register here!</a>
            </div>
        </div>
    </div>
</div>

<c:import url="include/foot.jsp"/>

<script src="<c:url value='/js/auth.js'/>"></script>
<script src="<c:url value='/js/form-validation.js'/>"></script>
<script>
    document.addEventListener("DOMContentLoaded", () => {
        new WiseValidator('#login-form');
    });
</script>

</body>
</html>
