<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <c:import url="/jsp/include/head.jsp"/>
    <title>Manage Categories</title>
    <meta name="description" content="Wise movie rental app - Category management page">
    <link rel="stylesheet" href="<c:url value='/css/table.css'/>"/>
    <link rel="stylesheet" href="<c:url value='/css/movie-dashboard.css'/>"/>
    <link rel="stylesheet" href="<c:url value='/css/pagination.css'/>"/>
</head>
<body>

<c:import url="/jsp/include/header.jsp"/>

<main class="wise-container">
    <a href="<c:url value='/auth/staff/dashboard'/>" class="wise-btn wise-btn--ghost go-back">
        <i class="fa-solid fa-circle-arrow-left"></i><span>Back to the Dashboard</span>
    </a>

    <h2 class="wise-page-title">Manage Categories</h2>

    <c:import url="/jsp/include/message.jsp"/>

    <div class="main-actions">
        <a href="<c:url value='/auth/staff/categories/add-category'/>" class="wise-btn wise-btn--primary">
            Add New Category <i class="fa-solid fa-circle-plus"></i>
        </a>
    </div>

    <c:choose>
        <c:when test="${empty categories}">
            <div class="wise-no-entity">No categories found.</div>
        </c:when>
        <c:otherwise>
            <div class="wise-table-container w-full">
                <table class="wise-table min-w-full"
                       data-entity-table>
                    <thead class="wise-table__head">
                    <tr class="wise-table__row">
                        <th>ID</th>
                        <th>Name</th>
                        <th>Description</th>
                        <th>Status</th>
                        <th>Actions</th>
                    </tr>
                    </thead>
                    <tbody class="wise-table__body">
                    <c:forEach var="category" items="${categories}">
                        <tr class="wise-table__row">
                            <td class="wise-table__entity-id"><c:out value="${category.id}"/></td>
                            <td class="wise-table__entity-main"><c:out value="${category.name}"/></td>
                            <td>
                                <p class="wise-table__entity-desc">
                                    <c:out value="${category.description}"/>
                                </p>
                            </td>
                            <td>
                                <span class="wise-table__entity-status ${category.active ? 'wise-table__entity-status--active' : 'wise-table__entity-status--inactive'}">
                                    <c:out value="${category.active ? 'Active' : 'Inactive'}"/>
                                </span>
                            </td>
                            <td>
                                <div class="wise-table__action-container">
                                    <a href="<c:url value='/auth/staff/categories/edit-category'><c:param name='id' value='${category.id}'/></c:url>"
                                       class="wise-table__action-btn wise-table__action-btn--normal" title="Edit">
                                        <i class="fa-solid fa-pen"></i>
                                    </a>
                                    <button type="button"
                                            data-toggle-btn
                                            data-entity-id="${category.id}"
                                            data-entity-name="<c:out value='${category.name}'/>"
                                            data-entity-active="${category.active}"
                                            class="wise-table__action-btn ${category.active ? 'wise-table__action-btn--private' : 'wise-table__action-btn--public'}"
                                            title="${category.active ? 'Disable' : 'Enable'}">
                                        <i class="fa-solid ${category.active ? 'fa-eye-slash' : 'fa-eye'}"></i>
                                    </button>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>

                <div id="pagination-area" class="wise-pagination hidden">
                    <ol class="wise-pagination__list"></ol>
                </div>

            </div>
        </c:otherwise>
    </c:choose>
</main>

<c:import url="/jsp/include/toggle-modal.jsp">
    <c:param name="entityLabel" value="category" />
    <c:param name="formAction" value="/auth/staff/categories/toggle-category" />
</c:import> 
<c:import url="/jsp/include/back-to-top.jsp" />
<c:import url="/jsp/include/footer.jsp"/>
<c:import url="/jsp/include/foot.jsp"/>

<script src="<c:url value='/js/back_to_top.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/utilities/pagination.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/movie_entity_pagination.js'/>"></script>
<script src="<c:url value='/js/utilities/toggle_modal.js'/>"></script>
<script src="<c:url value='/js/movie-entity-dashboard.js'/>"></script>
</body>
</html>