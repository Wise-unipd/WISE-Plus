<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <c:import url="include/head.jsp"/>
    <title>WISE+ — Register</title>
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
                A library <em>worth</em> keeping.
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

            <div class="wise-auth-form-eyebrow">
                <span class="wise-mono">WISE+</span>
                <span class="wise-auth-form-eyebrow-rule"></span>
            </div>
            <h2 class="wise-auth-form-title">Register.</h2>
            <p class="wise-auth-form-deck">
                Two thousand titles, hand-curated and waiting for you.
            </p>

            <c:import url="/jsp/include/message.jsp"/>

            <form class="wise-form" data-auth-form action="<c:url value='/register'/>" method="post" id="register-form">

                <div class="wise-field-row">
                    <div class="wise-field">
                        <label class="wise-field-label" for="firstName">
                            <span>First name</span>
                            <span class="wise-field-optional">Optional</span>
                        </label>
                        <input class="wise-input" type="text" id="firstName" name="firstName"
                               placeholder="Jane" autocomplete="given-name"
                               pattern="^[\p{L}\s\-]{1,50}$"
                               title="Only letters, spaces, and hyphens are allowed."/>
                        <span class="wise-strength-label wise-error-text hidden" data-error-for="firstName"></span>
                    </div>
                    <div class="wise-field">
                        <label class="wise-field-label" for="lastName">
                            <span>Last name</span>
                            <span class="wise-field-optional">Optional</span>
                        </label>
                        <input class="wise-input" type="text" id="lastName" name="lastName"
                               placeholder="Doe" autocomplete="family-name"
                               pattern="^[\p{L}\s\-]{1,50}$"
                               title="Only letters, spaces, and hyphens are allowed."/>
                        <span class="wise-strength-label wise-error-text hidden" data-error-for="lastName"></span>
                    </div>
                </div>

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
                    </label>
                    <div class="wise-input-wrap">
                        <input class="wise-input wise-input--has-icon"
                               type="password" id="password" name="password"
                               placeholder="••••••••"
                               required autocomplete="new-password"
                               minlength="8"
                               title="Password must be at least 8 characters long."/>
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

                    <div class="wise-strength" aria-hidden="true">
                        <div class="wise-strength-bar" id="bar-1"></div>
                        <div class="wise-strength-bar" id="bar-2"></div>
                        <div class="wise-strength-bar" id="bar-3"></div>
                        <div class="wise-strength-bar" id="bar-4"></div>
                    </div>
                </div>

                <%-- Confirm --%>
                <div class="wise-field">
                    <label class="wise-field-label" for="confirmPassword">
                        <span>Confirm password</span>
                    </label>
                    <div class="wise-input-wrap">
                        <input class="wise-input wise-input--has-icon"
                               type="password" id="confirmPassword" name="confirmPassword"
                               placeholder="••••••••"
                               required autocomplete="new-password" /> 
                        <button type="button" class="wise-input-icon"
                                data-action="toggle-password"
                                data-toggle-for="confirmPassword"
                                aria-label="Toggle password visibility">
                            <svg data-id="pw-icon" width="14" height="14" viewBox="0 0 24 24"
                                fill="none" stroke="currentColor" stroke-width="1.6">
                                <path d="M2 12s4-7 10-7 10 7 10 7-4 7-10 7S2 12 2 12z"/>
                                <circle cx="12" cy="12" r="3"/>
                            </svg>
                        </button>
                    </div>
                    <span class="wise-strength-label wise-error-text hidden" data-error-for="confirmPassword"></span>
                </div>

                <button type="submit" class="wise-auth-submit" id="submit-btn">
                    <%--                    Todo: Usare button component già stilato altrove--%>
                    Create account
                    <i class="fa-solid fa-arrow-right"></i>
                </button>

            </form>

            <div class="wise-auth-foot-help">
                Already have an account?
                <a href="<c:url value='/login'/>">Sign in instead →</a>
            </div>
        </div>
    </div>
</div>

<c:import url="include/foot.jsp"/>

<script src="<c:url value='/js/auth.js'/>"></script>
<script src="<c:url value='/js/form-validation.js'/>"></script>
<script src="<c:url value='/js/register.js'/>"></script>

</body>
</html>
