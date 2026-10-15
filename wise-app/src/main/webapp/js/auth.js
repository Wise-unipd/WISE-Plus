document.addEventListener("DOMContentLoaded", () => {
    const forms = document.querySelectorAll('[data-auth-form]');

    forms.forEach(form => {
        form.addEventListener('click', (event) => {
            const btn = event.target.closest('[data-action="toggle-password"]');
            if (btn) {
                togglePassword(event, btn);
            }
        });
    });
});

const EYE = '<path d="M2 12s4-7 10-7 10 7 10 7-4 7-10 7S2 12 2 12z"/><circle cx="12" cy="12" r="3"/>';
const EYE_OFF = '<path d="m3 3 18 18M10.6 10.6a2 2 0 0 0 2.8 2.8M9.4 5.1A10 10 0 0 1 22 12a17 17 0 0 1-2.5 3.5M6.6 6.6A17 17 0 0 0 2 12a16 16 0 0 0 14.5 7.4"/>';

function togglePassword(event, btn) {
    event.preventDefault();

    const input = document.getElementById(btn.dataset.toggleFor);
    const icon = btn.querySelector('[data-id="pw-icon"]');
    if (!input || !icon) return;

    const isText = input.type === 'text';
    input.type = isText ? 'password' : 'text';
    icon.innerHTML = isText ? EYE : EYE_OFF;
}
