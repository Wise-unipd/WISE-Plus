document.addEventListener("DOMContentLoaded", () => {
    cleanURL();

    // On page load, read the storage and open the correct tab
    const activeSection = sessionStorage.getItem('activeProfileSection') || 'profile-info';
    showSection(activeSection);

    // Sidebar navigation
    document.querySelectorAll('.wise-profile-sidebar-btn').forEach(btn => {
        btn.addEventListener('click', (event) => {
            const targetBtn = event.target.closest('.wise-profile-sidebar-btn');
            const sectionId = targetBtn.dataset.section;
            showSection(sectionId);
        });
    });

    // Event delegation for action buttons
    document.addEventListener('click', (event) => {
        const btn = event.target.closest('[data-action]');
        if (!btn) return;
        
        const action = btn.dataset.action;

        switch (action) {
            case 'enable-profile-edit':
                enableProfileEditMode();
                break;
            case 'cancel-profile-edit':
                cancelProfileEditMode();
                break;
            case 'enable-password-edit':
                enablePasswordEditMode();
                break;
            case 'cancel-password-edit':
                cancelPasswordEditMode();
                break;
            case 'open-refund-modal':
                openRefundModal(
                    btn.dataset.rentId,
                    parseFloat(btn.dataset.maxAmount),
                    btn.dataset.userId
                );
                break;
            case 'close-refund-modal':
                const refundModal = document.getElementById('refundOverlay');
                refundModal.classList.remove('is-active');
                break;
        }
    });

    // Validate Profile Update Form
    new WiseValidator('#profile-form');

    // Validate Refund Modal Form
    new WiseValidator('#refund-form');

    // Validate Password Form
    new WiseValidator('#password-form', {
        customRules: {
            'confirmPassword': (value, field) => {
                const passwordValue = document.getElementById('password').value;
                if (value !== passwordValue) {
                    return 'Passwords do not match.';
                }
                return null;
            }
        }
    });
});

/**
 * Cleans the current URL (removes status messages)
 */
function cleanURL() {
    // Get the current URL
    const url = new URL(window.location.href);

    // Delete parameters
    url.searchParams.delete('success');
    url.searchParams.delete('error');

    // Update the address bar without reloading the page
    window.history.replaceState({}, '', url.toString());
}

/**
 * Handles section switching in the sidebar.
 * @param sectionId The ID of the section to show.
 */
function showSection(sectionId) {
    // Hide all sections
    document.querySelectorAll('.wise-profile-section-content').forEach(section => {
        section.classList.add('hidden');
    });

    // Show target section
    document.getElementById('section-' + sectionId).classList.remove('hidden');

    // Update active state in sidebar
    document.querySelectorAll('.wise-profile-sidebar-btn').forEach(btn => {
        btn.classList.remove('active');
    });
    document.getElementById('btn-' + sectionId).classList.add('active');

    // Save the active section to session storage
    sessionStorage.setItem('activeProfileSection', sectionId);
}

/**
 * Enables profile editing by removing the 'readonly' attribute
 * from designated inputs and swapping the action buttons.
 */
function enableProfileEditMode() {
    document.querySelectorAll('.wise-profile-editable-field').forEach(input => {
        input.removeAttribute('readonly');
    });
    document.getElementById('view-profile-button').classList.add('hidden');
    document.getElementById('edit-profile-buttons').classList.remove('hidden');
}

/**
 * Cancels profile editing, restores the 'readonly' state,
 * and resets the input values back to their original state using JSTL.
 */
function cancelProfileEditMode() {
    document.querySelectorAll('.wise-profile-editable-field').forEach(input => {
        input.setAttribute('readonly', 'true');

        if (input.dataset.default) {
            input.value = input.dataset.default;
        }
    });

    document.getElementById('view-profile-button').classList.remove('hidden');
    document.getElementById('edit-profile-buttons').classList.add('hidden');
}

/**
 * Enables password editing mode.
 */
function enablePasswordEditMode() {
    document.querySelectorAll('.wise-profile-editable-password').forEach(input => {
        input.removeAttribute('readonly');
    });
    document.getElementById('view-password-button').classList.add('hidden');
    document.getElementById('edit-password-buttons').classList.remove('hidden');
}

/**
 * Cancels password editing, restores 'readonly', and clears the typed passwords.
 */
function cancelPasswordEditMode() {
    document.querySelectorAll('.wise-profile-editable-password').forEach(input => {
        input.setAttribute('readonly', 'true');
        input.value = ""; // Clear out any typed text for security
    });

    document.getElementById('view-password-button').classList.remove('hidden');
    document.getElementById('edit-password-buttons').classList.add('hidden');
}

/**
 * Opens the Refund Modal and populates the hidden fields and max limits.
 * @param rentId The ID of the rental being refunded.
 * @param maxAmount The maximum available limit for this specific rental.
 * @param targetUserId The ID of the user receiving the refund.
 */
function openRefundModal(rentId, maxAmount, targetUserId) {
    document.getElementById('modalRentId').value = rentId;
    document.getElementById('modalTargetUserId').value = targetUserId;

    var amountInput = document.getElementById('refundAmount');

    // .toFixed(2) solves floating-point precision issues
    var formattedAmount = maxAmount.toFixed(2);

    // bound the input field so the user cannot type a higher number
    amountInput.max = formattedAmount;
    amountInput.value = formattedAmount;

    document.getElementById('refundOverlay').classList.add('is-active');
}
