document.addEventListener('DOMContentLoaded', function() {
    // Add event listeners
    document.getElementById('search-input').addEventListener('input', handleTyping);
    console.log("Event listener added to Search Input.");
    document.getElementById('pagination-area').addEventListener('click', handlePaginationAreaClick);
    console.log("Event listener added to Pagination Area.");

    // Run an initial search when the page first loads
    console.log("Running initial catalog load...");
    searchMovies(query, 1);
});

let query = '';
const limit = 10;

// Variables for the typing timer
let typingTimer;
const doneTypingInterval = 300; // Time to wait after the user stops typing (in milliseconds)

// This function runs every single time a key is pressed
function handleTyping(event) {
    query = event.currentTarget.value.trim();

    // Clear the previous timer
    clearTimeout(typingTimer);

    // Start a new timer. When the timer finishes, it runs searchMovies()
    typingTimer = setTimeout(() => {
        searchMovies(query, 1);
    }, doneTypingInterval);
}

// Set parameters to change page
async function handlePaginationAreaClick(event) {
    const button = event.target.closest('[data-id="change-page-btn"]');
    
    if (button) {
        const currentPage = parseInt(button.dataset.page, 10);
        await searchMovies(query, currentPage);
        const anchor = document.getElementById('table-anchor');
        anchor.scrollIntoView({
            behavior: 'smooth',
            block: 'start'
        });
    }
}

// Make AJAX call to backend
async function searchMovies(query, currentPage) {
    console.log("Searching for: " + query);

    const url = `${CONTEXT_PATH}/rest/movies?q=${encodeURIComponent(query)}&limit=${limit}&offset=${
        (currentPage - 1) * limit
    }`;

    // Get the JWT token from the non-HttpOnly cookie
    const token = getCookie('JWT_TOKEN');
    const headers = { 'Accept': 'application/json' };
    if (token) headers['Authorization'] = 'Bearer ' + token;

    try {
        // Send an HTTP request
        const response = await fetch(url, {
            method: 'GET',
            headers: headers
        });

        // Read the contents of the server response
        const data = await response.json();

        if (!response.ok) {
            console.log(`Request unsuccessfull: HTTP status = ${response.status}.`);
            renderMovieSearchUI('ERROR');
            return;
        }

        // Extract information
        const movies = data.movies;

        // Check if no movies were found
        if (!movies || movies.length === 0) {
            renderMovieSearchUI('EMPTY');
            return;
        }

        console.log("Search successful. Rendering data...");
        renderMovieSearchUI('SUCCESS', { movies, totalCount: data.totalCount, currentPage})
    } catch (error) {
        console.log("Error during search:", error);
        renderMovieSearchUI('ERROR');
    }
}

// Handle movie search UI rendering
function renderMovieSearchUI(state, payload = {}) {
    const table = document.getElementById('movie-table');
    const tbody = table.querySelector('tbody');
    const paginationContainer = document.querySelector('#pagination-area');
    const paginationList = paginationContainer.querySelector('ol');

    switch (state) {
        case 'SUCCESS':
            renderMovies(payload.movies);
            renderPages(payload.currentPage, payload.totalCount);
            break
        
        case 'EMPTY':
            tbody.replaceChildren();
            paginationList.replaceChildren();
            paginationContainer.classList.add('hidden');
            break;
        
        case 'ERROR':
            const tr = document.createElement('tr');
            const columnCount = table.querySelectorAll('thead th').length || 7;

            tr.innerHTML = `
                <td colspan="${columnCount}" class="wise-mono wise-table__empty-msg wise-table__error-msg">
                    Error loading movies.
                </td>
            `;

            tbody.replaceChildren(tr);
            paginationList.replaceChildren();
            paginationContainer.classList.add('hidden');
            break;
    }
}

// Update the DOM-side movie table
function renderMovies(movies) {
    const table = document.getElementById('movie-table');
    const tbody = table.querySelector('tbody');

    // Create a fragment and attach it to the live DOM when ready (just one reflow and render)
    const fragment = document.createDocumentFragment();

    // Loop through the movie array
    movies.forEach(movie => {
        const row = createMovieRow(movie);

        // Add the item to the fragment
        fragment.appendChild(row);
    });

    // Attach the fragment to the live DOM
    tbody.replaceChildren(fragment);
}

// Update the DOM-side pagination list
function renderPages(currentPage, totalMovies) {
    const paginationContainer = document.querySelector('#pagination-area');
    const paginationList = paginationContainer.querySelector('ol');
    const totalPages = Math.ceil(totalMovies / limit);
    
    // You don't need to handle pagination logic if there is only one page
    if (totalPages <= 1) {
        paginationList.replaceChildren();
        paginationContainer.classList.add('hidden');
        return; 
    }
    
    // Determine which page numbers to show in the pagination list
    const rawPages = Array.from({ length: 5 }, (_, i) => currentPage - 2 + i);
    const pages = [...new Set([1, ...rawPages, totalPages].filter(p => p >= 1 && p <= totalPages))];

    // Create a fragment and attach it to the live DOM when ready (just one reflow and render)
    const fragment = document.createDocumentFragment();

    // Show the 'Previous page' button
    if (currentPage > 1) {
        const item = document.createElement('li');
        item.innerHTML = `
            <button type="button" 
                    data-id="change-page-btn" 
                    data-page="${currentPage - 1}"
                    class="wise-btn wise-btn--ghost wise-pagination__scroll-btn">
                Previous
            </button>
        `;
        // Add the item to the fragment
        fragment.appendChild(item);
    }

    // Show the inner pagination list
    pages.forEach((page, index) => {
        const item = document.createElement('li');
        const isActive = page === currentPage;
        item.innerHTML = `
            <button type="button" 
                    data-id="change-page-btn" 
                    data-active="${isActive}" 
                    data-page="${page}"
                    class="wise-btn ${isActive ? 'wise-btn--primary' : 'wise-btn--ghost'}">
                ${page}
            </button>
        `;
        // Add the item to the fragment
        fragment.appendChild(item);

        // Check if there is a jump from one page number to another (and if so, replace it with '...')
        const nextPage = pages[index + 1] ?? null;
        if (nextPage && nextPage !== page + 1) {
            const item = document.createElement('li');
            item.innerHTML = `
                <span class="wise-pagination__separator">...</span>
            `;
            // Add the item to the fragment
            fragment.appendChild(item);
        }
    });

    // Show the 'Next page' button
    if (currentPage < totalPages) {
        const item = document.createElement('li');
        item.innerHTML = `
            <button type="button" 
                    data-id="change-page-btn"
                    data-page="${currentPage + 1}"
                    class="wise-btn wise-btn--ghost wise-pagination__scroll-btn">
                Next
            </button>
        `;
        // Add the item to the fragment
        fragment.appendChild(item);
    }

    // Attach the fragment to the live DOM
    paginationList.replaceChildren(fragment);
    paginationContainer.classList.remove('hidden');
}

// Create a 'Movie' element ready for rendering
function createMovieRow(movie) {
    const tr = document.createElement('tr');
    tr.classList.add('wise-table__row');

    // HTML-style
    tr.innerHTML = `
        <td class="wise-table__entity-id">${movie.id}</td>
        <td class="wise-table__entity-main"></td>
        <td>${movie.releaseYear}</td>
        <td>${Number(movie.basePriceEur).toFixed(2)}&euro;</td>
        <td>
            ${movie.active 
                ? '<span class="wise-table__entity-status wise-table__entity-status--active">Active</span>' 
                : '<span class="wise-table__entity-status wise-table__entity-status--inactive">Inactive</span>'
            }
        </td>
        <td>
            <div data-dropdown class="wise-dropdown-container">
                <button type="button" data-dropdown-toggle-btn class="wise-dropdown-toggle">
                    <span>Choose Relation</span><i class="fa-solid fa-caret-down"></i>
                </button>
                <div data-dropdown-menu data-open="false" class="wise-dropdown-menu wise-dropdown-menu--narrow">
                    <a href="${CONTEXT_PATH}/auth/staff/movies/link-credits-to-movie?id=${movie.id}"
                       class="wise-dropdown-link wise-clean-link">
                       Credits
                    </a>
                    <a href="${CONTEXT_PATH}/auth/staff/movies/link-genres-to-movie?id=${movie.id}"
                       class="wise-dropdown-link wise-clean-link">
                       Genres
                    </a>
                    <a href="${CONTEXT_PATH}/auth/staff/movies/link-audios-to-movie?id=${movie.id}"
                       class="wise-dropdown-link wise-clean-link">
                       Audios
                    </a>
                    <a href="${CONTEXT_PATH}/auth/staff/movies/link-subtitles-to-movie?id=${movie.id}"
                       class="wise-dropdown-link wise-clean-link">
                       Subtitles
                    </a>
                    <a href="${CONTEXT_PATH}/auth/staff/movies/link-categories-to-movie?id=${movie.id}"
                       class="wise-dropdown-link wise-clean-link">
                       Categories
                    </a>
                </div>
            </div> 
        </td>
        <td>
            <div class="wise-table__action-container">
                <a href="${CONTEXT_PATH}/auth/staff/movies/save-movie?id=${movie.id}"
                   class="wise-table__action-btn wise-table__action-btn--normal">
                    <i class="fa-solid fa-pen-to-square"></i>
                </a>
                <button type="button"
                        data-toggle-btn
                        data-entity-id="${movie.id}"
                        data-entity-name=""
                        data-entity-active="${movie.active}"
                        class="wise-table__action-btn ${movie.active ? 'wise-table__action-btn--private' : 'wise-table__action-btn--public'}"
                        title="${movie.active ? 'Disable' : 'Enable'}">
                    <i class="fa-solid ${movie.active ? 'fa-lock' : 'fa-globe'}"></i>
                </button>
            </div>
        </td>
    `;

    // Safe insert of potentially risky values
    tr.querySelector('.wise-table__entity-main').textContent = movie.title;
    tr.querySelector('[data-toggle-btn]').dataset.entityName = `${movie.title} (${movie.releaseYear})`;

    return tr;
}
