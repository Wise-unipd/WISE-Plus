document.getElementById('searchInput').addEventListener('input', handleTyping);
document.getElementById('checkEmail').addEventListener('change', handleTyping);
document.getElementById('checkTitle').addEventListener('change', handleTyping);
document.getElementById('pagination-area').addEventListener('click', (e) => PaginationHelper.handlePageClick(e, renderTablePage));

let allRents = [];
let typingTimer;
let doneTypingInterval = 300;

/**
 * Handles the typing event in the search input field.
 * Implements a debounce mechanism to avoid excessive API calls.
 */
function handleTyping() {
    const query = document.getElementById('searchInput').value;
    clearTimeout(typingTimer);
    typingTimer = setTimeout(function () {
        searchRents(query);
    }, doneTypingInterval);
}

/**
 * Searches for rentals by sending an AJAX request to the REST API.
 *
 * @param {string} query The search query.
 */
function searchRents(query) {

    const searchEmail = document.getElementById('checkEmail').checked;
    const searchTitle = document.getElementById('checkTitle').checked;
    const tbody = document.querySelector('#rentals-table tbody');
    const paginationContainer = document.querySelector('#pagination-area');
    const paginationList = paginationContainer.querySelector('ol');

    // check that at least one of the two checkbox should be checked
    if (!searchEmail && !searchTitle) {
        tbody.innerHTML = '<tr><td colspan="8" class="wise-mono rentals-empty-msg">Check at least one option for searching.</td></tr>';
        paginationList.replaceChildren();
        paginationContainer.classList.add('hidden');
        return;
    }

    // Construct the API endpoint with URL-encoded parameters
    const url = CONTEXT_PATH + '/rest/rentals?q=' + encodeURIComponent(query) +
        '&searchEmail=' + searchEmail +
        '&searchTitle=' + searchTitle;

    const token = getCookie('JWT_TOKEN');
    const headers = { 'Accept': 'application/json' };
    if (token) headers['Authorization'] = 'Bearer ' + token;

    fetch(url, { method: 'GET', headers: headers })
        .then(response => {
            if (!response.ok) throw new Error('Network error');
            return response.json();
        })
        .then(data => {
            allRents = data['resource-list'] || [];
            const now = new Date().getTime();

            // Sort the fetched rentals:
            // 1st priority: Active rentals.
            // 2nd priority: Alphabetical order by movie title.
            allRents.sort((a, b) => {
                const timeA = a.expiration ? new Date(a.expiration).getTime() : 0;
                const timeB = b.expiration ? new Date(b.expiration).getTime() : 0;
                const isActiveA = timeA > now;
                const isActiveB = timeB > now;

                if (isActiveA && !isActiveB) return -1;
                if (!isActiveA && isActiveB) return 1;

                const titleA = (a.movieTitle || "").toLowerCase();
                const titleB = (b.movieTitle || "").toLowerCase();

                if (titleA < titleB) return -1;
                if (titleA > titleB) return 1;
                return 0;
            });

            // Render the first page after fresh data is sorted
            renderTablePage(1);
        })
        .catch(error => {
            tbody.innerHTML = '<tr><td colspan="8" class="wise-mono wise-table__empty-msg wise-table__error-msg">Error loading rentals.</td></tr>';
            paginationList.replaceChildren();
            paginationContainer.classList.add('hidden');
        });
}

/**
 * Builds and injects the HTML for a specific page of the data table.
 * @param {number} page The current page number to be rendered.
 */
function renderTablePage(page) {
    const tbody = document.querySelector('#rentals-table tbody');
    const paginatedRents = PaginationHelper.getPaginatedData(allRents, page);
    const fragment = document.createDocumentFragment();
    const now = new Date().getTime();

    paginatedRents.forEach(rent => {
        const tr = document.createElement('tr');
        tr.className = 'wise-table__row';

        // Determine if the rental is currently active to assign the correct badge styles
        const expirationTime = rent.expiration ? new Date(rent.expiration).getTime() : 0;
        const isActive = expirationTime > now;
        const statusText = isActive ? "Active" : "Expired";
        const statusClass = isActive ? "wise-table__entity-status--active" : "wise-table__entity-status--inactive";

        // Format timestamps to local strings, handling null/missing dates safely
        const formattedCheckout = rent.checkout ? new Date(rent.checkout).toLocaleString() : '';
        const formattedExpiration = rent.expiration ? new Date(rent.expiration).toLocaleString() : 'N/A';

        // Construct the row data
        tr.innerHTML = `
            <td class="wise-table__entity-id">${rent.id}</td>
            <td data-rent-email></td>
            <td data-rent-first-name></td>
            <td data-rent-last-name></td>
            <td class="wise-table__entity-main" data-rent-movie-title></td>
            <td>${formattedCheckout}</td>
            <td>${formattedExpiration}</td>
            <td><span class="wise-table__entity-status ${statusClass}">${statusText}</span></td>
        `;

        // Safe insert of potentially risky values
        tr.querySelector('[data-rent-email]').textContent = rent.email;
        tr.querySelector('[data-rent-first-name]').textContent = rent.firstName;
        tr.querySelector('[data-rent-last-name]').textContent = rent.lastName;
        tr.querySelector('[data-rent-movie-title]').textContent = rent.movieTitle;

        fragment.appendChild(tr);
    });

    // Replace the current table contents with the newly generated fragment
    tbody.replaceChildren(fragment);

    // Trigger the external helper to rebuild the pagination buttons
    PaginationHelper.renderPages(page, allRents.length, '#pagination-area');
}

document.addEventListener('DOMContentLoaded', (event) => {
    console.info("DOM fully loaded and parsed. Initializing rentals catalog.");
    searchRents('');
});