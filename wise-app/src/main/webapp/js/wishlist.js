// Handle clicks in the wishlist container
function handleWishlistClick(event) {
    const target = event.target;
    const heartBtn = target.closest('[data-wishlist-btn]');
    if (heartBtn) {
        event.preventDefault();
        event.stopPropagation();

        // animation pop
        heartBtn.classList.remove('popping');
        void heartBtn.offsetWidth;
        heartBtn.classList.add('popping');

        const movieCard = target.closest('[data-movie-id]');
        removeFromWishlist(movieCard);
    }
}

// Make AJAX call to backend
async function removeFromWishlist(movieCard) {
    // Get movie ID for request
    const movieId = movieCard.dataset.movieId;
    console.log(`Movie ID: ${movieId}.`);

    const url = `${CONTEXT_PATH}/rest/wishlist/movie/${movieId}`;
    console.log(`Request URL: ${url}.`);

    // Get the JWT token from the non-HttpOnly cookie
    const token = getCookie('JWT_TOKEN');
    const headers = { 'Accept': 'application/json' };
    if (token) headers['Authorization'] = 'Bearer ' + token;

    try {
        console.log(`Performing the HTTP DELETE request.`);

        // Send an HTTP request
        const response = await fetch(url, {
            method: 'DELETE',
            headers: headers
        });
        // Read the contents of the server response
        const result = await response.json();

        if (!response.ok) {
            console.log(`Request unsuccessfull: HTTP status = ${response.status}.`);
            console.log("Error:", result);
            Toast.open(result.message || 'Server error.', true);
            return;
        }

        console.log("Success:", result);
        Toast.open(result.message, false);

        // Remove the 'Movie' element from the DOM
        movieCard.remove();

        // Check if the wishlist is empty
        checkEmptyWishlist();

        console.log(`HTTP DELETE request successfully performed and processed.`);
    } catch (error) {
        console.error("Network error:", error);
        Toast.open("Network error occurred. Please try again.", true);
    }
}

// Check if the wishlist is empty (and update the UI accordingly)
function checkEmptyWishlist() {
    const wishlist = document.getElementById('movie-list');

    if (wishlist && wishlist.querySelectorAll('[data-movie-id]').length === 0) {
        const wishlistContainer = document.getElementById('wise-wishlist');

        if (wishlistContainer) {
            wishlistContainer.innerHTML = `
                <div class="wise-empty-container">
                    <p class="wise-no-entity">No movies found in your wishlist. Try adding something you like.</p>
                    <a href='${CONTEXT_PATH}/auth/catalog' class="wise-btn wise-btn--primary wise-btn--lg wise-btn--center wise-clean-link">
                        Go to Catalog
                    </a>
                </div>
            `;
        }
    }
}

document.addEventListener('DOMContentLoaded', function () {
    const wishlistContainer = document.getElementById('wise-wishlist');
    if (wishlistContainer) {
        wishlistContainer.addEventListener('click', handleWishlistClick);
        console.log("Event listener added to Wishlist Container.");
    }
});
