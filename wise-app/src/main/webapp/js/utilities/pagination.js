/**
 * A utility object to manage pagination logic, data slicing, and UI rendering across different data tables.
 */
const PaginationHelper = {
    limit: 10,

    /**
     * Slices the full array of data to return only the items for the current page.
     * @param {Array} dataArray The complete array of items to paginate.
     * @param {number} page The target page number to retrieve.
     */
    getPaginatedData: function(dataArray, page) {
        const startIndex = (page - 1) * this.limit;
        return dataArray.slice(startIndex, startIndex + this.limit);
    },

    /**
     * Builds the pagination buttons and injects them into the specified container.
     * @param {number} currentPage The currently active page number.
     * @param {number} totalItems The total number of items across all pages.
     * @param {string} containerSelector The CSS selector for the DOM element where buttons will be injected.
     */
    renderPages: function(currentPage, totalItems, containerSelector) {
        const paginationContainer = document.querySelector(containerSelector);
        if (!paginationContainer) return;

        const paginationList = paginationContainer.querySelector('ol, ul');
        const totalPages = Math.ceil(totalItems / this.limit);

        if (totalPages <= 1) {
            if (paginationList) paginationList.replaceChildren();
            paginationContainer.classList.add('hidden');
            return;
        }

        const rawPages = Array.from({ length: 5 }, (_, i) => currentPage - 2 + i);
        const pages = [...new Set([1, ...rawPages, totalPages].filter(p => p >= 1 && p <= totalPages))];
        const fragment = document.createDocumentFragment();

        if (currentPage > 1) {
            const item = document.createElement('li');
            item.innerHTML = `<button type="button" data-id="change-page-btn" data-page="${currentPage - 1}" class="wise-btn wise-btn--ghost wise-pagination__scroll-btn">Previous</button>`;
            fragment.appendChild(item);
        }

        // Iterate through the calculated page numbers to build the individual buttons
        pages.forEach((page, index) => {
            const item = document.createElement('li');
            const isActive = page === currentPage;
            item.innerHTML = `<button type="button" data-id="change-page-btn" data-active="${isActive}" data-page="${page}" class="wise-btn ${isActive ? 'wise-btn--primary' : 'wise-btn--ghost'}">${page}</button>`;
            fragment.appendChild(item);

            const nextPage = pages[index + 1] ?? null;
            if (nextPage && nextPage !== page + 1) {
                const sep = document.createElement('li');
                sep.innerHTML = `<span class="wise-pagination__separator">...</span>`;
                fragment.appendChild(sep);
            }
        });

        // Append the "Next" button if the user is not currently on the final page
        if (currentPage < totalPages) {
            const item = document.createElement('li');
            item.innerHTML = `<button type="button" data-id="change-page-btn" data-page="${currentPage + 1}" class="wise-btn wise-btn--ghost wise-pagination__scroll-btn">Next</button>`;
            fragment.appendChild(item);
        }

        if (paginationList) paginationList.replaceChildren(fragment);
        paginationContainer.classList.remove('hidden');
    },

    /**
     * Handles the click event, extracts the target page, triggers the specific render callback, and scrolls up.
     * @param {Event} event The click event triggered by the user.
     * @param {Function} renderCallback The specific function to call to render the requested page.
     */
    handlePageClick: async function(event, renderCallback) {
        const button = event.target.closest('[data-id="change-page-btn"]');

        if (button) {
            const currentPage = parseInt(button.dataset.page, 10);
            await renderCallback(currentPage);

            const anchor = document.getElementById('table-anchor');
            if (anchor) anchor.scrollIntoView({ behavior: 'smooth', block: 'start' });
        }
    }
};
