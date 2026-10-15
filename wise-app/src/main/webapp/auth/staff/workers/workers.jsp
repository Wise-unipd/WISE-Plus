<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <c:import url="/jsp/include/head.jsp"/>
    <title>Manage Workers</title>
    <meta name="description" content="Wise movie rental app - Workers management page">
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

    <div id="table-anchor" class="wise-table-anchor"></div>

    <h2 class="wise-page-title">Manage Workers</h2>

    <c:import url="/jsp/include/message.jsp"/>

    <div class="main-actions">
        <a href="<c:url value='/auth/staff/workers/add-worker'/>" class="wise-btn wise-btn--primary">
            Add New Worker <i class="fa-solid fa-circle-plus"></i>
        </a>
    </div>

    <c:choose>
        <c:when test="${empty workers}">
            <div class="wise-no-entity">No workers found.</div>
        </c:when>
        <c:otherwise>
            <div class="wise-table-container wise-table-container--auto">
                <table class="wise-table"
                       data-entity-table>
                    <thead class="wise-table__head">
                    <tr class="wise-table__row">
                        <th>ID</th>
                        <th>First Name</th>
                        <th>Last Name</th>
                        <th>Actions</th>
                    </tr>
                    </thead>
                    <tbody class="wise-table__body">
                    <c:forEach var="worker" items="${workers}">
                        <tr class="wise-table__row">
                            <td class="wise-table__entity-id"><c:out value="${worker.id}"/></td>
                            <td><c:out value="${worker.name}"/></td>
                            <td class="wise-table__entity-main"><c:out value="${worker.surname}"/></td>
                            <td>
                                <div class="wise-table__action-container">
                                    <a href="<c:url value='/auth/staff/workers/edit-worker'><c:param name='id' value='${worker.id}'/></c:url>"
                                       class="wise-table__action-btn wise-table__action-btn--normal" title="Edit">
                                        <i class="fa-solid fa-pen"></i>
                                    </a>
                                    <button type="button"
                                            data-delete-btn
                                            data-entity-id="${worker.id}"
                                            data-entity-name="<c:out value='${worker.name}'/> <c:out value='${worker.surname}'/>"
                                            class="wise-table__action-btn wise-table__action-btn--private"
                                            title="Delete">
                                        <i class="fa-solid fa-trash"></i>
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

<c:import url="/jsp/include/delete-modal.jsp">
    <c:param name="entityLabel" value="worker" />
    <c:param name="formAction" value="/auth/staff/workers/delete-worker" />
</c:import> 
<c:import url="/jsp/include/back-to-top.jsp" />
<c:import url="/jsp/include/footer.jsp"/>
<c:import url="/jsp/include/foot.jsp"/>

<script src="<c:url value='/js/back_to_top.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/utilities/pagination.js'/>"></script>
<script type="text/javascript" src="<c:url value='/js/movie_entity_pagination.js'/>"></script>
<script src="<c:url value='/js/utilities/delete_modal.js'/>"></script>
<script src="<c:url value='/js/movie-entity-dashboard.js'/>"></script>

</body>
</html>