class WiseValidator {
    /**
     * @param {string} formSelector - id or class of the form
     * @param {Object} [options] - Optional custom validation rules
     */
    constructor(formSelector, options = {}) {
        this.form = document.querySelector(formSelector);
        if (!this.form) return;

        this.customRules = options.customRules || {};

        this.fields = this.form.querySelectorAll(
            'input[pattern], input[required], textarea[required], input[type="file"], select[required]'
        );
        this.init();
    }

    init() {
        this.fields.forEach(field => {
            // Get error label
            const errorLabel = document.querySelector(`[data-error-for="${field.id}"]`);
            
            if (!errorLabel) return;

            const eventType = (field.type === 'file' || field.tagName === 'SELECT') ? 'change' : 'input';
            field.addEventListener(eventType, async () => await this.validateField(field, errorLabel));
        });

        // Block submission synchronously first
        this.form.addEventListener('submit', (e) => {
            e.preventDefault();

            // Then run the async checks
            (async () => {
                let isValid = true;

                // Loop sequentially with for...of to correctly await each field
                for (const field of this.fields) {
                    const errorLabel = this.form.querySelector(`[data-error-for="${field.id}"]`);
                    if (!errorLabel) continue;

                    const fieldValid = await this.validateField(field, errorLabel);
                    if (!fieldValid) {
                        isValid = false;
                    }
                }

                if (isValid) {
                    // If everything is good, bypass the listener and submit
                    this.form.submit();
                } else {
                    const firstInvalid = this.form.querySelector('.is-invalid');
                    if (firstInvalid) firstInvalid.focus();
                }
            })();
        });
    }

    async validateField(field, errorLabel) {
        if (field.validity && field.validity.badInput) {
            this.showError(field, errorLabel, 'Please enter a valid number.');
            return false;
        }

        const value = field.value.trim();

        // Checks missing required fields
        if (field.required && !value) {
            this.showError(field, errorLabel, 'This field is required.');
            return false;
        }

        if (field.validity) {
            if (field.validity.rangeUnderflow) {
                const minVal = field.getAttribute('min');
                this.showError(field, errorLabel, `Value must be greater than or equal to ${minVal}.`);
                return false;
            }
            if (field.validity.rangeOverflow) {
                const maxVal = field.getAttribute('max');
                this.showError(field, errorLabel, `Value must be less than or equal to ${maxVal}.`);
                return false;
            }
        }

        const pattern = field.getAttribute('pattern');
        if (pattern && value) {
            // Flag 'u' (unicode) because regex uses \p{L}
            const regex = new RegExp(pattern, 'u');
            if (!regex.test(value)) {
                // Title contains the message to show to the user
                const msg = field.getAttribute('title') || 'Invalid format.';
                this.showError(field, errorLabel, msg);
                return false;
            }
        }

        // Await custom validation rules (sync or async)
        if (this.customRules[field.id] && value) {
            const customErrorMessage = await this.customRules[field.id](value, field);
            if (customErrorMessage) {
                if (field.type === 'file') {
                    field.dispatchEvent(new CustomEvent('fileRejected', { bubbles: true }));
                }
                this.showError(field, errorLabel, customErrorMessage);
                return false;
            }
        }

        this.clearError(field, errorLabel);
        return true;
    }

    showError(field, errorLabel, message) {
        // Same style of login
        errorLabel.textContent = message;
        errorLabel.classList.remove('hidden');
        field.classList.add('is-invalid');
    }

    clearError(field, errorLabel) {
        errorLabel.classList.add('hidden');
        errorLabel.textContent = '';
        field.classList.remove('is-invalid');
    }
}