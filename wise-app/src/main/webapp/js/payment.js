// Function that holds our specific expiration date logic.
// Returns an error message string if invalid, or null if valid.
function validateCardExpiry(value) {
    const parts = value.split('/');
    if (parts.length !== 2) return null; // Let the regex pattern handle structural failures

    const month = parseInt(parts[0], 10);
    const year = parseInt('20' + parts[1], 10);

    const now = new Date();
    const currentMonth = now.getMonth() + 1;
    const currentYear = now.getFullYear();

    if (year < currentYear || (year === currentYear && month < currentMonth)) {
        return 'This credit card has expired.';
    }

    return null; // Passes custom check
}

// Keep cosmetic UI formatting inside payment.js
document.addEventListener("DOMContentLoaded", () => {
    const cardNumberInput = document.getElementById('cardNumber');

    if (cardNumberInput) {
        cardNumberInput.addEventListener('input', function (e) {
            const input = e.target;
            const cursorPosition = input.selectionStart;
            const originalValue = input.value;
            const digitsBeforeCursor = originalValue.slice(0, cursorPosition).replace(/\D/g, '').length;

            let cleanValue = originalValue.replace(/\D/g, '');
            let formattedValue = cleanValue.replace(/(.{4})/g, '$1 ').trim();

            input.value = formattedValue;

            let newCursorPos = 0;
            let digitCount = 0;
            for (let i = 0; i < formattedValue.length; i++) {
                if (digitCount === digitsBeforeCursor) break;
                if (/\d/.test(formattedValue[i])) digitCount++;
                newCursorPos++;
            }

            input.setSelectionRange(newCursorPos, newCursorPos);
        });
    }
});