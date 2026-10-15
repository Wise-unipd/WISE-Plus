// Centralize the initialization, opening and closing of the toast
const Toast = {
    TOAST_DURATION: 3000,
    ANIMATION_RESET_DELAY: 150,
    toast: null,
    statusBox: null,
    statusIcon: null,
    messageBox: null,
    timer: null,
    
    /**
     * Retrieve elements and attach events.
     */
    init() {
        this.toast = document.getElementById('toast');
        if (!this.toast) return;

        this.statusBox = document.getElementById('toast-status');
        this.statusIcon = document.getElementById('toast-status-icon');
        this.messageBox = document.getElementById('toast-message-text');

        const closeBtn = document.getElementById('toast-close-btn');
        if (closeBtn) {
            closeBtn.addEventListener('click', () => this.close());
            console.log("Event listener added to Close Toast Button.");
        }
    },

    /**
     * Open the toast by filling in the required fields.
     */
    open(message, isError = false) {
        if (!this.toast) return;

        // Useful for handling toasts that are temporally close together
        const wasVisible = this.toast.classList.contains('toast--visible');

        // Clear the previous toast
        this.close();

        // Allow the browser to "reset" the animation
        setTimeout(() => {
            this.toast.classList.remove('toast--success', 'toast--error');
            this.toast.classList.add(isError ? 'toast--error' : 'toast--success');

            if (this.statusBox) {
                this.statusBox.textContent = isError ? 'Error Message' : 'Success Message';
            }

            if (this.statusIcon) {
                this.statusIcon.classList.remove('fa-check', 'fa-circle-exclamation');
                this.statusIcon.classList.add(isError ? 'fa-circle-exclamation' : 'fa-check');
            }

            if (this.messageBox) {
                this.messageBox.textContent = message;
            }

            this.toast.classList.add('toast--visible');

            // Start a new timer
            this.timer = setTimeout(() => this.close(), this.TOAST_DURATION);
        }, wasVisible ? this.ANIMATION_RESET_DELAY : 0);
    },

    /**
     * Close the toast.
     */
    close() {
        if (!this.toast) return;
        this.toast.classList.remove('toast--visible');
        clearTimeout(this.timer);
    }
};

document.addEventListener('DOMContentLoaded', function () {
    Toast.init();
});
