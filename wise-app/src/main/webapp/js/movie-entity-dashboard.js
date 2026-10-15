document.addEventListener('DOMContentLoaded', function () {
    cleanURL();

    const table = document.querySelector('[data-entity-table]');
    if (table) {
        table.addEventListener('click', handleEntityTableClick);
        console.log("Event listener added to Entity Table.");
    }
});

// Clean current URL (remove status messages)
function cleanURL() {
    // Get the current URL
    const url = new URL(window.location.href);

    // Delete parameters
    url.searchParams.delete('success');
    url.searchParams.delete('error');

    // Update the address bar without reloading the page
    window.history.replaceState({}, '', url.toString());   
}

// Handle clicks in the table
function handleEntityTableClick(event) {
    const target = event.target;

    const toggleBtn = target.closest('[data-toggle-btn]');
    if (toggleBtn) {
        ToggleModal.open(
            toggleBtn.dataset.entityId,
            toggleBtn.dataset.entityName,
            toggleBtn.dataset.entityActive === 'true'
        );
        return;
    }

    const deleteBtn = target.closest('[data-delete-btn]');
    if (deleteBtn) {
        DeleteModal.open(
            deleteBtn.dataset.entityId,
            deleteBtn.dataset.entityName
        );
        return;
    }
}
