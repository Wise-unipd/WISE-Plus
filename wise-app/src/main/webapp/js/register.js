document.addEventListener("DOMContentLoaded", () => {
    const passwordInput = document.getElementById('password');

    if (passwordInput) {
        passwordInput.addEventListener('input', (event) => {
            updateStrength(event.target.value);
        });
    }

    new WiseValidator('#register-form', {
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

function passwordStrength(p) {
    let score = 0;
    if (p.length >= 8) score++;
    if (/[A-Z]/.test(p) && /[a-z]/.test(p)) score++;
    if (/[0-9]/.test(p)) score++;
    if (/[^A-Za-z0-9]/.test(p)) score++;
    return score;
}

function updateStrength(value) {
    const score = value.length ? passwordStrength(value) : 0;

    for (let i = 1; i <= 4; i++) {
        const bar = document.getElementById('bar-' + i);
        bar.className = 'wise-strength-bar' + (score >= i ? ' is-on-' + score : '');
    }

    // Trigger validation on the confirm field if the main password changes
    const confirmInput = document.getElementById('confirmPassword');
    if (confirmInput && confirmInput.value) {
        confirmInput.dispatchEvent(new Event('input', { bubbles: true }));
    }
}

