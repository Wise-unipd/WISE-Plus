document.addEventListener('DOMContentLoaded', () => {

    new WiseValidator('#extend-payment-form', {
        customRules: {
            // Key matches the 'id' attribute of the input field
            expiryDate: validateCardExpiry
        }
    });

    const form = document.getElementById('extend-payment-form');
    const daysInput = document.getElementById('days');
    const expirationElement = document.getElementById('new_expiration');
    const totPriceElement = document.getElementById('tot_price');

    const basePrice = parseFloat(form.dataset.basePrice) || 0;
    // Fetch the timestamp passed from the Servlet
    const currentExpirationMs = parseInt(form.dataset.currentExpiration, 10);

    function updateTotals() {
        const days = parseInt(daysInput.value) || 0;

        // Update Price
        totPriceElement.textContent = (basePrice * days).toFixed(2) + "€";

        // Update Expiration Date
        if (!isNaN(currentExpirationMs)) {
            // days * 24 hours * 60 minutes * 60 seconds * 1000 milliseconds
            const addedMs = days * 24 * 60 * 60 * 1000;
            const newExpDate = new Date(currentExpirationMs + addedMs);

            // Extract date and time parts (we won't display the time)
            const day = String(newExpDate.getDate()).padStart(2, '0');
            const month = String(newExpDate.getMonth() + 1).padStart(2, '0');
            const year = newExpDate.getFullYear();
            expirationElement.textContent = `${day}/${month}/${year}`;
        }
    }

    // Run once on load to set the initial values
    updateTotals();

    // Run every time the input changes
    daysInput.addEventListener('input', updateTotals);
});