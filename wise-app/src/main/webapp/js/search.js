let query = '';
const limit = 20;

// Variables for the typing timer
let typingTimer;
const doneTypingInterval = 300; // Time to wait after the user stops typing (in milliseconds)

// Variables for the loading timer
let loadingTimer;
const doneLoadingInterval = 300; // Time to wait before the loading message appears (in milliseconds)

const movieSearchUI = {
    searchInput: document.getElementById('search-input'),
    movieList: document.getElementById('movie-list'),
    statusContainer: document.getElementById('status-container'),
    statusMessage: document.getElementById('status-message'),
    loadMoreBtn: document.getElementById('load-more-btn')
};

// Make AJAX call to backend
async function searchMovies(query, offset) {
    console.log("Searching for: " + query);

    // Start "smart loading"
    startLoadingTimer(offset);
    // Always hide the button (safety)
    movieSearchUI.loadMoreBtn.classList.add('hidden');

    const url = `${CONTEXT_PATH}/rest/search?q=${encodeURIComponent(query)}&limit=${limit}&offset=${offset}`;

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
            throw new Error(data.message || 'Server error.');
        }

        // Stop "smart loading"
        stopLoadingTimer();
        
        // Check if no movies were found
        const movies = data.movies;
        if (!movies || movies.length === 0) {
            renderMovieSearchUI('EMPTY', { offset });
        } else {
            console.log("Rendering data...");
            renderMovieSearchUI('SUCCESS', { movies, totalCount: data.totalCount, offset });
        }
    } catch (error) {
        // Stop "smart loading"
        stopLoadingTimer();
        console.log("Error during search:", error);
        renderMovieSearchUI('ERROR', { offset });
    }
}

// Handle movie search UI rendering
function renderMovieSearchUI(state, payload = {}) {
    const offset = payload.offset ?? 0;

    // Hide and clear movie list if search somehow failed
    if (offset === 0 && state !== 'SUCCESS') {
        movieSearchUI.movieList.replaceChildren();
        movieSearchUI.movieList.classList.add('hidden');
    }

    switch (state) {
        case 'LOADING':
            movieSearchUI.statusMessage.textContent = 'Loading...';
            movieSearchUI.statusContainer.classList.remove('hidden');
            break;
        
        case 'SUCCESS':
            movieSearchUI.statusContainer.classList.add('hidden');
            movieSearchUI.movieList.classList.remove('hidden');

            renderMovies(payload.movies, offset);

            // Show button if the are more movies
            if (offset + payload.movies.length < payload.totalCount) {
                movieSearchUI.loadMoreBtn.classList.remove('hidden');
            }
            break;
        
        case 'EMPTY':
            movieSearchUI.statusMessage.textContent = (offset === 0) ? 'No movies found.' : 'No more movies to load.';
            movieSearchUI.statusContainer.classList.remove('hidden');
            break;

        case 'ERROR':
            if (offset === 0) {
                movieSearchUI.statusMessage.textContent = 'Network error occurred or no movies found.';
            } else {
                movieSearchUI.statusMessage.textContent = 'Failed to load more movies. Please try again or reload the page.';
                movieSearchUI.loadMoreBtn.classList.remove('hidden');
            }
            movieSearchUI.statusContainer.classList.remove('hidden');
            break;
    }
}

// Update the DOM-side movie list
function renderMovies(movies, offset) {
    // Create a fragment and attach it to the live DOM when ready (just one reflow and render)
    const fragment = document.createDocumentFragment();

    // Loop through the movie array
    movies.forEach(movie => {
        // Avoid duplicates (safety)
        if (offset === 0 || !movieSearchUI.movieList.querySelector(`[data-movie-id="${movie.id}"]`)) {
            const item = createMovieCard(movie);

            // Add the item to the fragment
            fragment.appendChild(item);
        }
    });

    // Attach the fragment to the live DOM
    if (offset === 0) {
        movieSearchUI.movieList.replaceChildren(fragment);
    } else {
        movieSearchUI.movieList.appendChild(fragment);
    }
}

// This function runs every single time a key is pressed
function handleTyping(event) {
    query = event.currentTarget.value.trim();

    // Clear the previous timer
    clearTimeout(typingTimer);

    // Start a new timer. When the timer finishes, it runs searchMovies()
    typingTimer = setTimeout(() => {
        searchMovies(query, 0);
    }, doneTypingInterval);
}

// Set parameters lo load other movies
function handleLoadMore() {
    const offset = document.querySelectorAll('#movie-list > li').length;
    searchMovies(query, offset);
}

// Start waiting for the loading message to be displayed
function startLoadingTimer(offset) {
    // Clear the previous timer
    clearTimeout(loadingTimer);

    loadingTimer = setTimeout(() => {
        renderMovieSearchUI('LOADING', { offset });
    }, doneLoadingInterval);
}

// Stop waiting for the loading message to be displayed
function stopLoadingTimer() {
    // Clear the timer
    clearTimeout(loadingTimer);
}

// Create a 'Movie' element ready for rendering
function createMovieCard(movie) {
    // Create the <li> tag
    const item = document.createElement('li');
    item.classList.add('wise-card-item');
    item.dataset.movieId = movie.id;

    // HTML-style
    item.innerHTML = `
        <a href="${CONTEXT_PATH}/auth/movie?id=${movie.id}"
           class="wise-card wise-clean-link">
            <img class="wise-card__poster"
                 src="${CONTEXT_PATH}/uploads/${movie.posterPic}"
                 data-movie-poster
                 alt="">

            <div class="wise-card__overlay">
                <p class="wise-card__title" data-movie-title></p>
                <p class="wise-card__price">${Number(movie.basePriceEur).toFixed(2)}€/day</p>
                <span class="wise-card__show-btn">Show</span>
            </div>
        </a>
    `;

    // Safe insert of potentially risky values
    item.querySelector('[data-movie-title]').textContent = movie.title;
    item.querySelector('[data-movie-poster]').alt = `Poster of ${movie.title}`;

    return item;
}

document.addEventListener('DOMContentLoaded', function() {
    // Add event listeners
    movieSearchUI.searchInput.addEventListener('input', handleTyping);
    console.log("Event listener added to Search Input.");
    movieSearchUI.loadMoreBtn.addEventListener('click', handleLoadMore);
    console.log("Event listener added to Load More Button.");

    // Run an initial search when the page first loads
    console.log("Running initial catalog load...");
    searchMovies(query, 0);
});
