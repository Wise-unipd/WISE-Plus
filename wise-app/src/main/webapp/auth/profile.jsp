<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <c:import url="/jsp/include/head.jsp"/>
    <title>WISE - My Profile</title>
    <link rel="stylesheet" href="<c:url value='/css/table.css'/>">
    <link rel="stylesheet" href="<c:url value='/css/profile.css'/>">
</head>
<body>

<c:import url="/jsp/include/header.jsp"/>

<main class="wise-container">
    <header class="profile-header">
    </header>

    <c:if test="${param.success == 'profile'}">
        <p class="wise-strength-label success-message" role="alert">
            <i class="fa-solid fa-circle-check"></i> Profile successfully updated.
        </p>
    </c:if>

    <c:if test="${param.success == 'password'}">
        <p class="wise-strength-label success-message" role="alert">
            <i class="fa-solid fa-circle-check"></i> Password successfully updated.
        </p>
    </c:if>

    <c:import url="/jsp/include/message.jsp"/>

    <div class="profile-layout">
        <!-- Sidebar -->
        <aside class="profile-sidebar">
            <nav class="wise-profile-nav">
                <button id="btn-profile-info" data-section="profile-info" class="wise-profile-sidebar-btn active">
                    <i class="fa-solid fa-user-gear sidebar-icon"></i> Profile Info
                </button>
                <button id="btn-rents" data-section="rents" class="wise-profile-sidebar-btn">
                    <i class="fa-solid fa-clock-rotate-left sidebar-icon"></i> Rentals History
                </button>
                <c:if test="${AccessingUser.id == AppUser.id}">
                    <button id="btn-password-update" data-section="password-update" class="wise-profile-sidebar-btn">
                        <i class="fa-solid fa-shield-halved sidebar-icon"></i> Password Update
                    </button>
                </c:if>
            </nav>
        </aside>

        <!-- Content Area -->
        <div class="profile-content">
            <!-- Section: Profile Info -->
            <div id="section-profile-info" class="wise-profile-section-content">
                <div class="wise-profile-card">
                    <h2 class="wise-profile-section-title">Personal Details</h2>
                    <form id="profile-form" action="${pageContext.request.contextPath}/auth/profile-update" method="POST" class="wise-form">
                        <input type="hidden" name="id" value="${AppUser.id}"/>

                        <div class="wise-field">
                            <label class="wise-field-label" for="email">Email</label>
                            <input type="email" id="email" name="email" value="<c:out value='${AppUser.email}'/>" readonly class="wise-input wise-input--readonly"/>
                        </div>

                        <div class="profile-grid-2">
                            <div class="wise-field">
                                <label class="wise-field-label" for="name">First Name</label>
                                <input type="text" id="name" name="firstName" value="<c:out value='${AppUser.firstName}'/>" readonly 
                                       data-default="<c:out value='${AppUser.firstName}'/>"
                                       class="wise-input wise-profile-editable-field"
                                       required maxlength="50" pattern="^[\p{L}\s\-]{1,50}$" title="Only letters, spaces, and hyphens are allowed."/>
                                <span class="wise-strength-label wise-error-text hidden" data-error-for="name"></span>
                            </div>

                            <div class="wise-field">
                                <label class="wise-field-label" for="surname">Last Name</label>
                                <input type="text" id="surname" name="lastName" value="<c:out value='${AppUser.lastName}'/>" readonly
                                       data-default="<c:out value='${AppUser.lastName}'/>"
                                       class="wise-input wise-profile-editable-field"
                                       required maxlength="50" pattern="^[\p{L}\s\-]{1,50}$" title="Only letters, spaces, and hyphens are allowed."/>
                                <span class="wise-strength-label wise-error-text hidden" data-error-for="surname"></span>
                            </div>
                        </div>

                        <div class="wise-field">
                            <label class="wise-field-label" for="role">Role</label>
                            <input type="text" id="role" name="role" value="<c:out value='${AppUser.userRole.description}'/>" readonly class="wise-input wise-input--readonly"/>
                        </div>

                        <c:if test="${AccessingUser.id == AppUser.id}">
                            <div class="profile-form-actions">
                                <div id="view-profile-button">
                                    <button type="button" class="wise-btn wise-btn--primary" data-action="enable-profile-edit">
                                        <i class="fa-solid fa-pen-to-square"></i> Edit Profile
                                    </button>
                                </div>

                                <div id="edit-profile-buttons" class="profile-form-actions hidden">
                                    <button type="submit" class="wise-btn wise-btn--primary">Save Changes</button>
                                    <button type="button" class="wise-btn wise-btn--ghost" data-action="cancel-profile-edit">Cancel</button>
                                </div>
                            </div>
                        </c:if>
                    </form>
                </div>
            </div>

            <!-- Section: Rentals History -->
            <div id="section-rents" class="wise-profile-section-content hidden">
                <div class="wise-profile-card">
                    <h2 class="wise-profile-section-title">Rentals History</h2>
                    <c:choose>
                        <c:when test="${empty rentals}">
                            <p class="wise-profile-empty-state">No rentals yet.</p>
                        </c:when>
                        <c:otherwise>
                            <div class="wise-table-container">
                                <table class="wise-table">
                                    <thead class="wise-table__head">
                                    <tr class="wise-table__row">
                                        <th>Movie</th>
                                        <th>Checkout</th>
                                        <th>Amount</th>
                                        <th>Expiration</th>
                                        <th>Refunded</th>
                                        <c:choose>
                                            <c:when test="${AccessingUser.userRole.id >= 1 && AccessingUser.id != AppUser.id}">
                                                <th>Actions</th>
                                            </c:when>
                                            <c:when test="${AccessingUser.id == AppUser.id}">
                                                <th>Watch</th>
                                            </c:when>
                                        </c:choose>
                                    </tr>
                                    </thead>
                                    <tbody class="wise-table__body">
                                    <c:forEach var="rent" items="${rentals}">
                                        <c:set var="financialData" value="${refundableAmounts[rent.id]}"/>
                                        <c:set var="maxRefundable" value="${not empty financialData ? financialData[0] : 0.0}"/>
                                        <c:set var="totalRefunded" value="${not empty financialData ? financialData[1] : 0.0}"/>

                                        <tr class="wise-table__row">
                                            <td class="wise-table__entity-main"><c:out value="${rent.movieTitle}"/></td>
                                            <td><fmt:formatDate value="${rent.checkout}" pattern="dd/MM/yyyy, HH:mm:ss" /></td>
                                            <td class="wise-mono wise-table__amount"><fmt:formatNumber value="${rent.amount}" minFractionDigits="2" maxFractionDigits="2"/>&euro;</td>
                                            <td><fmt:formatDate value="${rent.expiration}" pattern="dd/MM/yyyy, HH:mm:ss" /></td>
                                            <td>
                                                <c:choose>
                                                    <c:when test="${totalRefunded > 0.0}">
                                                        <span class="wise-table__entity-status wise-table__entity-status--active">Yes</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="wise-table__entity-status wise-table__entity-status--inactive">No</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </td>

                                            <c:if test="${AccessingUser.userRole.id >= 1 && AccessingUser.id != AppUser.id}">
                                                <td>
                                                    <c:if test="${maxRefundable > 0.0}">
                                                        <button type="button" class="wise-btn wise-btn--primary wise-btn--sm" data-action="open-refund-modal" data-rent-id="${rent.id}" data-max-amount="${maxRefundable}" data-user-id="${AppUser.id}">
                                                            Refund
                                                        </button>
                                                    </c:if>
                                                </td>
                                            </c:if>

                                            <c:if test="${AccessingUser.id == AppUser.id}">
                                                <td>
                                                    <c:if test="${rent.expiration != null && rent.expiration.time > now.time}">
                                                        <a href="<c:url value="/auth/watch/${rent.movieId}"/>" class="wise-clean-link">
                                                            <button type="button" class="wise-btn wise-btn--primary wise-btn--sm">Watch</button>
                                                        </a>
                                                    </c:if>
                                                </td>
                                            </c:if>
                                        </tr>
                                    </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </c:otherwise>
                    </c:choose>

                    <c:if test="${AccessingUser.id == AppUser.id}">
                        <div class="refund-info-box">
                            <h3 class="refund-info-title">Need a refund?</h3>
                            <p class="refund-info-text">
                                Write an email to <a href="mailto:wise.unipd@gmail.com?subject=Refund%20Request" class="refund-info-link">wise.unipd@gmail.com</a>
                                specifying the rented movie and your motivations for the request.
                            </p>
                        </div>
                    </c:if>
                </div>
            </div>

            <!-- Section: Password Update -->
            <c:if test="${AccessingUser.id == AppUser.id}">
                <div id="section-password-update" class="wise-profile-section-content hidden">
                    <div class="wise-profile-card">
                        <h2 class="wise-profile-section-title">Change Password</h2>
                        <form id="password-form" action="${pageContext.request.contextPath}/auth/change-password" method="POST" class="wise-form">
                            <input type="hidden" name="id" value="${AppUser.id}"/>

                            <div class="wise-field">
                                <label class="wise-field-label" for="password">New Password</label>
                                <input type="password" id="password" name="password" readonly class="wise-input wise-profile-editable-password"
                                       required minlength="8" title="Password must be at least 8 characters long."/>
                                <span class="wise-strength-label wise-error-text hidden" data-error-for="password"></span>
                            </div>

                            <div class="wise-field">
                                <label class="wise-field-label" for="confirmPassword">Confirm New Password</label>
                                <input type="password" id="confirmPassword" name="confirmPassword" readonly class="wise-input wise-profile-editable-password" required/>
                                <span class="wise-strength-label wise-error-text hidden" data-error-for="confirmPassword"></span>
                            </div>

                            <div class="profile-form-actions">
                                <div id="view-password-button">
                                    <button type="button" class="wise-btn wise-btn--primary" data-action="enable-password-edit">
                                        <i class="fa-solid fa-key"></i> Change Password
                                    </button>
                                </div>

                                <div id="edit-password-buttons" class="profile-form-actions hidden">
                                    <button type="submit" class="wise-btn wise-btn--primary">Update Password</button>
                                    <button type="button" class="wise-btn wise-btn--ghost" data-action="cancel-password-edit">Cancel</button>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </c:if>
        </div>
    </div>

    <div id="refundOverlay" class="wise-modal">
        <div class="wise-modal-content">
            <h3 class="wise-modal-title">Issue Refund</h3>

            <form id="refund-form" data-custom-inputs action="${pageContext.request.contextPath}/auth/refund-rent" method="POST" class="wise-form">
                <input type="hidden" name="targetUserId" id="modalTargetUserId" value=""/>
                <input type="hidden" name="rentId" id="modalRentId" value=""/>

                <div class="wise-field">
                    <label class="wise-field-label" for="refundAmount">Amount to Refund (€)</label>

                    <div class="wise-form__field-wrap" data-stepper-container>
                        <button type="button" data-stepper-action="less" class="wise-form__stepper-btn" aria-label="Less">−</button>

                        <input class="wise-form__stepper-input" type="number" id="refundAmount" name="amount" min="0.01" step="0.01" required>

                        <button type="button" data-stepper-action="more" class="wise-form__stepper-btn" aria-label="More">+</button>
                    </div>
                    <span class="wise-strength-label wise-error-text hidden" data-error-for="refundAmount"></span>
                </div>

                <div class="wise-modal-actions">
                    <button type="submit" class="wise-btn wise-btn--primary">Confirm Refund</button>
                    <button type="button" class="wise-btn wise-btn--ghost" data-action="close-refund-modal">Cancel</button>
                </div>
            </form>
        </div>
    </div>

</main>

<c:import url="/jsp/include/footer.jsp"/>
<c:import url="/jsp/include/back-to-top.jsp" />
<c:import url="/jsp/include/foot.jsp"/>

<script src="<c:url value='/js/form.js'/>"></script>
<script src="<c:url value='/js/form-validation.js'/>"></script>
<script src="<c:url value='/js/profile.js'/>"></script>
<script src="<c:url value='/js/back_to_top.js'/>"></script>

</body>
</html>
