document.addEventListener('DOMContentLoaded', function() {
  const searchContainer = document.getElementById('search-container');
  if (searchContainer) {
    searchContainer.addEventListener('keydown', handleKeydown);
    console.log("Event listener added to Search Container.");
  }

  const searchInput = document.getElementById('search-input');
  if (searchInput) {
    searchInput.addEventListener('focus', handleFocus);
    searchInput.addEventListener('input', handleTyping);
    console.log("Event listeners added to Search Input.");
  }

  const suggestions = document.getElementById('suggestions');
  if (suggestions) {
    suggestions.addEventListener('click', handleSuggestionsClick);
    console.log("Event listener added to Suggestions.");
  }

  const addCreditBtn = document.getElementById('add-credit-btn');
  if (addCreditBtn) {
    addCreditBtn.addEventListener('click', addCredit);
    console.log("Event listener added to Add Credit Button.");
  }

  const creditList = document.getElementById('credit-list');
  if (creditList) {
    creditList.addEventListener('click', handleCreditListClick);
    console.log("Event listener added to Credit List.");
  }

  document.addEventListener('click', handleOutsideClick);
  console.log("Event listener added to Window Click.");
});

const limit = 5;

// Variables for the typing timer
let typingTimer;
const doneTypingInterval = 300; // Time to wait after the user stops typing (in milliseconds)

// Get headers common to all fetch operations
function getCommonHeaders() {
  // Get the JWT token from the non-HttpOnly cookie
  const token = getCookie('JWT_TOKEN');
  const headers = {
    'Accept': 'application/json'
  };

  if (token) {
    headers['Authorization'] = 'Bearer ' + token;
  }

  return headers;
}

// Handle special keyboard shortcuts inside the search input
function handleKeydown(event) {
  // Esc or Escape
  if (event.key === 'Escape') {
    const suggestions = document.getElementById('suggestions');
    const searchInput = document.getElementById('search-input');

    // Clear the previous timer
    clearTimeout(typingTimer);

    suggestions.classList.add('hidden');

    // Remove the focus
    searchInput.blur();
  }
}

// The search input gets focus (carry out a preliminary search)
function handleFocus(event) {
  const query = event.currentTarget.value.trim();

  // Clear the previous timer
  clearTimeout(typingTimer);

  searchWorkers(query);
}

// Handle clicks outside the search container
function handleOutsideClick(event) {
  const searchContainer = document.getElementById('search-container');
  const suggestions = document.getElementById('suggestions');

  // If the click occurred outside the search container
  if (!searchContainer.contains(event.target)) {
    // Clear the previous timer
    clearTimeout(typingTimer);

    suggestions.classList.add('hidden');
  }
}

// This function runs every single time the input element changes
function handleTyping(event) {
  const query = event.currentTarget.value.trim();

  // Cleaning process
  const workerInput = document.getElementById('selected-worker-id');
  const addCreditBtn = document.getElementById('add-credit-btn');
  workerInput.value = '';
  addCreditBtn.disabled = true;

  // Clear the previous timer
  clearTimeout(typingTimer);

  // Start a new timer. When the timer finishes, it runs searchWorkers()
  typingTimer = setTimeout(() => {
    searchWorkers(query);
  }, doneTypingInterval);
}

// Make AJAX call to backend
async function searchWorkers(query) {
  console.log("Searching for: " + query);

  let workers = [];

  const url = `${CONTEXT_PATH}/rest/workers?q=${encodeURIComponent(query)}&limit=${limit}`;

  const headers = getCommonHeaders();

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

    console.log("Search successful. Rendering suggestions...");
    workers = data['resource-list'] || [];
  } catch (error) {
    console.log("Error during search:", error);
  } finally {
    // Single call point
    renderResults(workers);
  }
}

// Update the suggestions
function renderResults(workers) {
  const suggestions = document.getElementById('suggestions');
  const list = suggestions.querySelector('ul');

  if (!workers || workers.length === 0) {
    suggestions.classList.add('hidden');
    list.replaceChildren();
    return;
  }

  // Create a fragment and attach it to the live DOM when ready (just one reflow and render)
  const fragment = document.createDocumentFragment();

  workers.forEach(worker => {
    const item = document.createElement('li');
    item.classList.add('suggestions__item');

    const button = document.createElement('button');
    button.type = 'button';
    button.classList.add('suggestions__item-btn');
    button.setAttribute('data-suggestion-btn', '');
    button.dataset.workerId = worker.id;
    button.textContent = `${worker.name} ${worker.surname}`;

    item.appendChild(button);

    // Add the item to the fragment
    fragment.appendChild(item);
  }); 

  // Attach the fragment to the live DOM
  list.replaceChildren(fragment);
  suggestions.classList.remove('hidden');
}

// Handle clicks in the suggestion container
function handleSuggestionsClick(event) {
  const target = event.target;

  const button = target.closest('[data-suggestion-btn]');
  if (button) {
    const workerId = button.dataset.workerId;
    const searchInput = document.getElementById('search-input');
    const workerInput = document.getElementById('selected-worker-id');
    const suggestions = document.getElementById('suggestions');
    const addCreditBtn = document.getElementById('add-credit-btn');

    searchInput.value = button.textContent.trim();
    workerInput.value = workerId;
    suggestions.classList.add('hidden');
    addCreditBtn.disabled = false;

    // Remove the focus
    searchInput.blur();
  }
}

// Make AJAX call to backend
async function addCredit(event) {
  const button = event.currentTarget;
  const creditList = document.getElementById('credit-list');
  const searchInput = document.getElementById('search-input');
  const workerInput = document.getElementById('selected-worker-id');
  const roleInput = document.getElementById('movie-roles');

  // Guard Clause: avoid repeated requests without control
  if (button.disabled) return;

  // Block further clicks
  button.disabled = true;

  // Get movie ID for request
  const movieId = creditList.dataset.movieId;
  console.log(`Movie ID: ${movieId}.`);

  // Get worker ID for request
  const workerId = workerInput.value;
  console.log(`Worker ID: ${workerId}.`);

  // Get role ID for request
  const roleId = roleInput.value;
  console.log(`Role ID: ${roleId}.`);

  const url = `${CONTEXT_PATH}/rest/movie/${movieId}/worker/${workerId}/role/${roleId}`;
  console.log(`Request URL: ${url}.`);

  const headers = getCommonHeaders();
  headers['Content-Type'] = 'application/json'; 

  try {
    console.log(`Performing the HTTP POST request.`);
    
    // Send an HTTP request
    const response = await fetch(url, {
      method: 'POST',
      headers: headers,
      body: JSON.stringify({})
    });
    // Read the contents of the server response
    const result = await response.json(); 

    if (!response.ok) {
      console.log(`Request unsuccessfull: HTTP status = ${response.status}.`);
      console.log("Error:", result);
      Toast.open(result.message || 'Server error.', true);
      button.disabled = false;
      return;
    }
    
    console.log("Success:", result);
    Toast.open("Credit added to movie.", false);

    // Prepend credit and clean search
    creditList.prepend(createCreditItem(result));
    workerInput.value = '';
    searchInput.value = '';

    console.log(`HTTP POST request successfully performed and processed.`);
  } catch(error) {
    console.error("Network error:", error);
    Toast.open("Network error occurred. Please try again.", true);
    button.disabled = false;
  }
}

// Create a 'Credit' element ready for rendering
function createCreditItem(credit) {
  const item = document.createElement('li');
  const worker = credit.worker;
  const role = credit.role;
  item.classList.add('entity', 'credit-item');
  item.dataset.workerId = worker.id;
  item.dataset.roleId = role.id;

  item.innerHTML = `
    <div class="credit-item__content">
      <span class="wise-mono" data-credit-role></span>
      <p class="credit-item__content__full-name">
        <span data-credit-name></span>
        <strong data-credit-surname></strong>
      </p>
    </div>  
    <button type="button" 
            data-unlink-btn
            class="wise-icon-btn credit-item__unlink-btn" 
            aria-label="Unlink movie credit">
      <i class="fa-solid fa-link-slash"></i>
    </button>
  `;

  item.querySelector('[data-credit-role]').textContent = `# ${role.name}`;
  item.querySelector('[data-credit-name]').textContent = worker.name;
  item.querySelector('[data-credit-surname]').textContent = worker.surname;

  return item;
}

// Handle clicks in the credit list
function handleCreditListClick(event) {
  const target = event.target;

  const button = target.closest('[data-unlink-btn]');
  if (button) {
    removeCredit(event);
  }
}

// Make AJAX call to backend
async function removeCredit(event) {
  const creditList = event.currentTarget;
  const item = event.target.closest('[data-worker-id][data-role-id]');

  // Get movie ID for request
  const movieId = creditList.dataset.movieId;
  console.log(`Movie ID: ${movieId}.`);

  // Get worker ID for request
  const workerId = item.dataset.workerId;
  console.log(`Worker ID: ${workerId}.`);

  // Get role ID for request
  const roleId = item.dataset.roleId;
  console.log(`Role ID: ${roleId}.`);

  const url = `${CONTEXT_PATH}/rest/movie/${movieId}/worker/${workerId}/role/${roleId}`;
  console.log(`Request URL: ${url}.`);

  const headers = getCommonHeaders();

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

    // Remove the credit
    item.remove();

    console.log(`HTTP DELETE request successfully performed and processed.`);
  } catch(error) {
    console.error("Network error:", error);
    Toast.open("Network error occurred. Please try again.", true);
  }
}