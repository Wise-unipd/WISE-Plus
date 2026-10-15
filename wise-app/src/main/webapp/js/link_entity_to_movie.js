document.addEventListener('DOMContentLoaded', function () {
    const entityList = document.getElementById('entity-list');
    if (entityList) {
        entityList.addEventListener('click', handleEntityListClick);
        console.log("Event listener added to Entity List.");
    }
});

// Handle clicks in the list
function handleEntityListClick(event) {
    const target = event.target;
    // Check if the add/delete button has been clicked
    if (target.closest('[data-toggle-entity-button]')) {
        toggleEntity(event);
    }
}

// Return the initialization object for the fetch operation
function getInitObjForToggleEntity(method) {
    // Get the JWT token from the non-HttpOnly cookie
    const token = getCookie('JWT_TOKEN');
    const headers = {
        'Accept': 'application/json'
    };

    if (token) {
        headers['Authorization'] = 'Bearer ' + token;
    }

    // Whether the entity is associated or not with the movie
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

// Manage the toggle operation
async function toggleEntity(event) {
    const target = event.target;
    const button = target.closest('[data-toggle-entity-button]');
    const list = document.getElementById('entity-list');
    const item = button.closest('[data-entity-id]');
    const icon = button.querySelector('[data-toggle-entity-icon]');

    // Guard Clause: avoid repeated requests without control
    if (button.disabled) return;

    // Block further clicks while the request is being processed (with visual feedback)
    button.disabled = true;
    button.classList.add('is-loading');

    // Get movie ID for request
    const movieId = list.dataset.movieId;
    console.log(`Movie ID: ${movieId}.`);

    const entityType = list.dataset.entityType;

    // Get entity ID for request
    const entityId = item.dataset.entityId;
    console.log(`${entityType.charAt(0).toUpperCase() + entityType.slice(1)} ID: ${entityId}.`);

    const url = `${CONTEXT_PATH}/rest/movie/${movieId}/${entityType}/${entityId}`;
    console.log(`Request URL: ${url}.`);

    const isLinked = icon.dataset.value === 'true';

    // POST to add, DELETE to remove
    const method = !isLinked ? 'POST' : 'DELETE';

    try {
        console.log(`Performing the HTTP ${method} request.`);

        // Send an HTTP request
        const response = await fetch(url, getInitObjForToggleEntity(method));
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
        icon.dataset.value = !isLinked ? 'true' : 'false';
        icon.classList.toggle('fa-circle-minus');
        icon.classList.toggle('fa-circle-plus');
        button.classList.toggle('wise-btn--primary');
        button.classList.toggle('wise-btn--ghost');

        console.log(`HTTP ${method} request successfully performed and processed.`);
    } catch (error) {
        console.error("Network error:", error);
        Toast.open("Network error occurred. Please try again.", true);
    } finally {
        // Restore user interface
        button.disabled = false;
        button.classList.remove('is-loading');
    }
}