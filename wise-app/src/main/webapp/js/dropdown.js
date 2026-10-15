document.addEventListener('click', event => {
    const target = event.target;
    
    const toggleBtn = target.closest('[data-dropdown-toggle-btn]');
    if (!toggleBtn) {
        closeAllDropdowns();
        return;
    }

    const menu = toggleBtn.closest('[data-dropdown]')?.querySelector('[data-dropdown-menu]');
    if (!menu) return;
    const wasOpen = menu.dataset.open === 'true';

    closeAllDropdowns();

    if (!wasOpen) {
        menu.classList.add('is-open');
        menu.dataset.open = 'true';
    }
}); 

document.addEventListener('keydown', event => {
    if (event.key === 'Escape') {
        closeAllDropdowns();
    }
});

// Helper function to easily close all open dropdowns
function closeAllDropdowns() {
    const menus = document.querySelectorAll('[data-dropdown-menu][data-open="true"]');
    menus.forEach((menu) => {
        menu.classList.remove('is-open');
        menu.dataset.open = 'false';
    });
}