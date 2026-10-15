// Centralize the initialization, opening and closing of the delete modal
const DeleteModal = {
    modal: null,

    /**
     * Retrieve elements and attach events.
     */
    init() {
        this.modal = document.getElementById('delete-entity-modal');
        if (!this.modal) return;

        const cancelBtn = this.modal.querySelector('[data-cancel-btn]');
        if (cancelBtn) {
            cancelBtn.addEventListener('click', () => this.close());
            console.log("Event listener added to Delete Modal Cancel Button.");
        }
    },

    /**
     * Open the modal by filling in the required fields.
     */
    open(id, name) {
        if (!this.modal) return;

        this.modal.querySelector('[data-entity-name]').textContent = name;
        this.modal.querySelector('[data-input-id]').value = id;

        this.modal.classList.add('is-active');
    },

    /**
     * Close the modal.
     */
    close() {
        if (!this.modal) return;
        this.modal.classList.remove('is-active');
    }
};

document.addEventListener('DOMContentLoaded', function () {
    DeleteModal.init();
});
