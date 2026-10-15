// Function to scroll all the cards in the rows
function scrollRow(button, direction) {
    const container = button.closest('.wise-card-row-container');
    const row = container.querySelector('.wise-card-row');
    const scrollAmount = row.clientWidth * 0.8;

    row.scrollBy({
        left: scrollAmount * direction,
        behavior: 'smooth'
    });
}

// Dynamically update the visibility of the previous and next arrows
function updateNavButtons(container) {
    const row = container.querySelector('.wise-card-row');
    const prevBtn = container.querySelector('.nav-btn.prev');
    const nextBtn = container.querySelector('.nav-btn.next');

    if (!row || !prevBtn || !nextBtn) return;

    const scrollLeft = row.scrollLeft;
    const scrollWidth = row.scrollWidth;
    const clientWidth = row.clientWidth;

    // Hide 'prev' button if we are at the start (left boundary)
    // Using a tolerance of 2px to prevent fractional pixel discrepancies on high-DPI screens
    if (scrollLeft <= 2) {
        prevBtn.classList.add('hide-nav-btn');
    } else {
        prevBtn.classList.remove('hide-nav-btn');
    }

    // Hide 'next' button if we are at the end (right boundary)
    if (scrollLeft + clientWidth >= scrollWidth - 2) {
        nextBtn.classList.add('hide-nav-btn');
    } else {
        nextBtn.classList.remove('hide-nav-btn');
    }
}

// Initialize and bind events for all rows
document.querySelectorAll('.wise-card-row-container').forEach(container => {
    const row = container.querySelector('.wise-card-row');

    // Initial check on page load
    updateNavButtons(container);

    // Listen for scroll events to update arrows in real-time
    if (row) {
        row.addEventListener('scroll', () => {
            updateNavButtons(container);
        });
    }

    // Event delegation for action buttons
    container.addEventListener('click', (event) => {
        const btn = event.target.closest('[data-action="scroll-carousel"]');
        if (btn) {
            const direction = parseInt(btn.dataset.direction, 10);
            scrollRow(btn, direction);
        }

    });
});

// Update boundaries when the browser window is resized
window.addEventListener('resize', () => {
    document.querySelectorAll('.wise-card-row-container').forEach(container => {
        updateNavButtons(container);
    });
});