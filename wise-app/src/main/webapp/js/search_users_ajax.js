document.getElementById('searchInput').addEventListener('input', handleTyping);
document.getElementById('pagination-area').addEventListener('click', (e) => PaginationHelper.handlePageClick(e, renderTablePage));

let allUsers = [];
let typingTimer;
let doneTypingInterval = 300;
let pendingToggleUser = null;
let pendingToggleStatus = null;

/**
 * Handles the typing event in the search input field.
 * Implements a debounce mechanism to avoid excessive API calls.
 */
function handleTyping() {
    const query = document.getElementById('searchInput').value;
    clearTimeout(typingTimer);
    typingTimer = setTimeout(function () {
        searchUsers(query);
    }, doneTypingInterval);
}

/**
 * Searches for users by sending an AJAX request to the REST API.
 * @param {string} query The search query.
 */
function searchUsers(query) {
    const tbody = document.querySelector('#users-table tbody');
    const paginationContainer = document.querySelector('#pagination-area');
    const paginationList = paginationContainer.querySelector('ol');

    const url = CONTEXT_PATH + '/rest/profiles?q=' + encodeURIComponent(query);
    const token = getCookie('JWT_TOKEN');
    const headers = { 'Accept': 'application/json' };
    if (token) headers['Authorization'] = 'Bearer ' + token;

    fetch(url, { method: 'GET', headers: headers })
        .then(response => {
            if (!response.ok) throw new Error('Network error');
            return response.json();
        })
        .then(data => {
            allUsers = data['resource-list'] || [];

            // Sort the fetched users:
            // 1st priority: Active users.
            // 2nd priority: Descending order by Role ID.
            // 3rd priority: Alphabetical order by email address.
            allUsers.sort((a, b) => {
                const aActive = (a.active === true || String(a.active).toLowerCase() === 'true');
                const bActive = (b.active === true || String(b.active).toLowerCase() === 'true');
                if (aActive !== bActive) return aActive ? -1 : 1;

                const aRoleId = (a.userRole && a.userRole.id !== undefined) ? Number(a.userRole.id) : -1;
                const bRoleId = (b.userRole && b.userRole.id !== undefined) ? Number(b.userRole.id) : -1;
                if (aRoleId !== bRoleId) return bRoleId - aRoleId;

                const aEmail = a.email ? String(a.email).toLowerCase() : '';
                const bEmail = b.email ? String(b.email).toLowerCase() : '';
                return aEmail.localeCompare(bEmail);
            });

            // Render the first page after fresh data is sorted
            renderTablePage(1);
        })
        .catch(error => {
            tbody.innerHTML = '<tr><td colspan="9" class="wise-mono wise-table__empty-msg wise-table__error-msg">Error loading users.</td></tr>';
            paginationList.replaceChildren();
            paginationContainer.classList.add('hidden');
        });
}

/**
 * Builds and injects the HTML for a specific page of the data table.
 * * @param {number} page The current page number to be rendered.
 */
function renderTablePage(page) {
    const tbody = document.querySelector('#users-table tbody');

    const paginatedUsers = PaginationHelper.getPaginatedData(allUsers, page);
    const fragment = document.createDocumentFragment();

    paginatedUsers.forEach(user => {
        const tr = document.createElement('tr');
        tr.className = 'wise-table__row';

        // Determine if the user account is active
        const isActive = (user.active === true || String(user.active).toLowerCase() === 'true');
        const statusText = isActive ? "Active" : "Inactive";
        const statusClass = isActive ? "wise-table__entity-status--active" : "wise-table__entity-status--inactive";

        // Format timestamps to DD/MM/YYYY, HH:mm:ss
        const dateOpts = { day: '2-digit', month: '2-digit', year: 'numeric', hour: '2-digit', minute: '2-digit', second: '2-digit', hour12: false };
        const createdDate = user.created ? new Date(user.created).toLocaleString('en-GB', dateOpts) : '';
        const updateDate = user.lastUpdate ? new Date(user.lastUpdate).toLocaleString('en-GB', dateOpts) : '';
        const roleDesc = user.userRole ? user.userRole.description : '';

        tr.innerHTML = `
            <td class="wise-table__entity-id">${user.id}</td>
            <td class="wise-table__entity-main" data-user-email></td>
            <td data-user-first-name></td>
            <td data-user-last-name></td>
            <td>${roleDesc}</td>
            <td>${createdDate}</td>
            <td>${updateDate}</td>
            <td><span class="wise-table__entity-status ${statusClass}">${statusText}</span></td>
            <td class="wise-table__action-container" id="actions-${user.id}"></td>
        `;

        // Safe insert of potentially risky values
        tr.querySelector('[data-user-email]').textContent = user.email;
        tr.querySelector('[data-user-first-name]').textContent = user.firstName;
        tr.querySelector('[data-user-last-name]').textContent = user.lastName;


        fragment.appendChild(tr);

        // Append the "View Profile" action button
        const actionsTd = tr.querySelector(`#actions-${user.id}`);
        actionsTd.innerHTML += `
            <a href="${CONTEXT_PATH}/auth/profile/${user.id}" class="wise-table__action-btn wise-table__action-btn--normal" title="View Profile">
                <i class="fa-solid fa-eye"></i>
            </a>
        `;

        // Generate action buttons only for Admin users
        if (typeof LOGGED_USER_ROLE_ID !== 'undefined' && LOGGED_USER_ROLE_ID === 2) {
            if (typeof LOGGED_USER_ID !== 'undefined' && String(user.id) !== String(LOGGED_USER_ID)) {

                actionsTd.innerHTML += `
                    <a href="${CONTEXT_PATH}/auth/profile/change-role/${user.id}" class="wise-table__action-btn wise-table__action-btn--normal" title="Set Role">
                        <i class="fa-solid fa-user-tie"></i>
                    </a>
                `;

                const toggleBtn = document.createElement('button');
                toggleBtn.className = `wise-table__action-btn ${isActive ? 'wise-table__action-btn--private' : 'wise-table__action-btn--public'}`;
                toggleBtn.title = isActive ? 'Deactivate Account' : 'Activate Account';
                toggleBtn.innerHTML = `<i class="fa-solid ${isActive ? 'fa-ban' : 'fa-check'}"></i>`;
                toggleBtn.addEventListener('click', () => openConfirmModal(user, isActive));
                actionsTd.appendChild(toggleBtn);
            }
        }
    });

    tbody.replaceChildren(fragment);
    PaginationHelper.renderPages(page, allUsers.length, '#pagination-area');
}

/**
 * Opens the confirmation modal for activating or deactivating a user account.
 * @param {Object} user The user object targeted for the status toggle.
 * @param {boolean} isActive The current active status of the user.
 */
function openConfirmModal(user, isActive) {
    pendingToggleUser = user;
    pendingToggleStatus = isActive;

    document.getElementById('modalActionText').textContent = isActive ? 'deactivate' : 'activate';
    document.getElementById('toggleUserModal').classList.add('is-active');
}

/**
 * Closes the confirmation modal and clears the pending toggle states.
 */
function closeConfirmModal() {
    pendingToggleUser = null;
    pendingToggleStatus = null;
    document.getElementById('toggleUserModal').classList.remove('is-active');
}

/**
 * Executes the request to toggle a user's active status after modal confirmation.
 */
function executeUserToggle() {
    if (!pendingToggleUser) return;

    const params = new URLSearchParams();
    params.append('id', pendingToggleUser.id);
    params.append('firstName', pendingToggleUser.firstName);
    params.append('lastName', pendingToggleUser.lastName);
    params.append('active', pendingToggleStatus ? 'false' : 'true');

    const headers = { 'Content-Type': 'application/x-www-form-urlencoded' };
    const token = getCookie('JWT_TOKEN');
    if (token) headers['Authorization'] = 'Bearer ' + token;

    fetch(CONTEXT_PATH + '/auth/profile-update', {
        method: 'POST',
        headers: headers,
        credentials: 'same-origin',
        body: params.toString()
    })
        .then(async response => {
            if (response.url.includes('/login')) {
                alert('You are not logged in. Please refresh.');
                return;
            }
            if (response.ok) {
                if (!response.redirected && response.url.endsWith('/profile')) {
                    console.error("Server rejected the update.");
                    return;
                }
                closeConfirmModal();
                searchUsers(document.getElementById('searchInput').value);
            }
        })
        .catch(error => console.error('Error toggling user status:', error));
}

document.addEventListener('DOMContentLoaded', () => {
    console.info("Initializing users catalog.");
    searchUsers('');

    document.getElementById('modalCancelBtn').addEventListener('click', closeConfirmModal);
    document.getElementById('modalConfirmBtn').addEventListener('click', executeUserToggle);
});
