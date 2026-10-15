document.addEventListener('DOMContentLoaded', function () {

    const rentBtn = document.getElementById('rent-btn');
    if (rentBtn) {
        rentBtn.addEventListener('click', rentMovie);
        console.log("Event listener added to Rent Button.");
    }

    document.getElementById('like-btn')
        .addEventListener('click', MovieLikeOrWishlist);
    console.log("Event listener added to Like Button.");

    document.getElementById('wishlist-btn')
        .addEventListener('click', MovieLikeOrWishlist);
    console.log("Event listener added to Wishlist Button.");

    const rentDaysInput = document.getElementById('rent-days');
    if (rentDaysInput) {
        rentDaysInput.addEventListener('input', updateTotalPrice);
        console.log("Event listener added to Rent Days Input.");
    }

    const lessDaysBtn = document.getElementById('less-days-btn');
    if (lessDaysBtn) {
        lessDaysBtn.addEventListener('click', function () {
            const input = document.getElementById('rent-days');
            input.value = Math.max(1, parseInt(input.value || 1) - 1);
            input.dispatchEvent(new Event('input', {bubbles: true}));
        });
        console.log("Event listener added to Less Days Button.");
    }

    const moreDaysBtn = document.getElementById('more-days-btn');
    if (moreDaysBtn) {
        moreDaysBtn.addEventListener('click', function () {
            const input = document.getElementById('rent-days');
            input.value = Math.min(30, parseInt(input.value || 1) + 1);
            input.dispatchEvent(new Event('input', {bubbles: true}));
        });
        console.log("Event listener added to More Days Button.");
    }
});

// Function to dynamically update the total price displayed within the button based on the number of days selected
function updateTotalPrice() {
    const days = parseInt(document.getElementById('rent-days').value) || 1;
    const basePrice = parseFloat(document.getElementById('rent-btn').dataset.price);
    const total = (days * basePrice).toFixed(2);
    document.getElementById('total-price').textContent = total;
}

// Function to rent movies
async function rentMovie() {
    const btn = document.getElementById('rent-btn');
    const movieId = btn.dataset.movieId;
    const days = parseInt(document.getElementById('rent-days').value) || 1; //days to rent

    window.location.href = `${CONTEXT_PATH}/auth/payment?movieId=${movieId}&days=${days}`;
}

// Return the initialization object for the fetch operation
function getInitObjForLikeOrWishlist(method) {
    // Get the JWT token from the non-HttpOnly cookie
    const token = getCookie('JWT_TOKEN');
    const headers = {
        'Accept': 'application/json'
    };

    if (token) {
        headers['Authorization'] = 'Bearer ' + token;
    }

    // If the movie is not liked or not in the wishlist
    if (method === 'POST') {
        // POST with body
        headers['Content-Type'] = 'application/json';
        return {
            method: method, headers: headers, body: JSON.stringify({})
        }
    } else {
        // DELETE without body
        return {
            method: method, headers: headers
        }
    }
}

// Dynamically manages "like" or "wishlist" toggle operations
async function MovieLikeOrWishlist(event) {
    const button = event.currentTarget;
    const icon = button.querySelector('[data-role="icon"]');

    // Guard Clause: avoid repeated requests without control
    if (button.disabled) return;

    // Block further clicks while the request is being processed (with visual feedback)
    button.disabled = true;
    button.classList.add('is-loading');

    // Get movie ID for request
    const movieId = button.dataset.movieId;
    console.log(`Movie ID: ${movieId}.`);

    // Dynamically choose the URL to make the request to
    const url = (button.id === 'like-btn') ? `${CONTEXT_PATH}/rest/movie/${movieId}/like` : `${CONTEXT_PATH}/rest/wishlist/movie/${movieId}`;
    console.log(`Request URL: ${url}.`);

    const isAdded = icon.dataset.value === 'true';

    // POST to add, DELETE to remove
    const method = !isAdded ? 'POST' : 'DELETE';

    try {
        console.log(`Performing the HTTP ${method} request.`);

        // Send an HTTP request
        const response = await fetch(url, getInitObjForLikeOrWishlist(method));
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

        // Change data-* and class attributes
        icon.dataset.value = !isAdded ? 'true' : 'false';
        icon.classList.toggle('fa-regular');
        icon.classList.toggle('fa-solid');
        button.classList.toggle('is-active');

        console.log(`HTTP ${method} request successfully performed and processed.`);
    } catch (error) {
        console.error("Network error:", error);
        Toast.open("Network error occurred. Please try again", true);
    } finally {
        // Restore user interface
        button.disabled = false;
        button.classList.remove('is-loading');
    }
} 
