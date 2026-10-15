document.addEventListener('DOMContentLoaded', function () {
    const forms = document.querySelectorAll('[data-custom-inputs]');

    forms.forEach(form => {
        form.addEventListener('click', handleFormClick);
    });

    const fileInputs = document.querySelectorAll('input[type="file"]');

    fileInputs.forEach(fileInput => {
        fileInput.addEventListener('change', handleFileChange);
        // NEW: Listen for validation failures from WiseValidator
        fileInput.addEventListener('fileRejected', (e) => {
            const target = e.target;

            clearFileInputData(target);
            resetFileUIState(target);
        });
    });
});

// Clean file data
function clearFileInputData(fileInput) {
    fileInput.value = '';

    // Avoid silent background bugs on some browsers
    if (fileInput.files) {
        fileInput.files = new DataTransfer().files;
    }
}

// Restore UI file state
function resetFileUIState(fileInput) {
    const clearBtn = fileInput.closest('[data-file-container]')?.querySelector('[data-file-action="clear"]');
    if (clearBtn) clearBtn.classList.add('hidden');
    resetPreviewState(fileInput.id);
}

// Handle clicks in the form
function handleFormClick(event) {
    const target = event.target;

    const stepperBtn = target.closest('[data-stepper-action]');
    if (stepperBtn) {
        inputStepping(stepperBtn);
        return;
    }

    const clearBtn = target.closest('[data-file-action="clear"]');
    if (clearBtn) {
        fileCleaning(clearBtn);
        return;
    }
}

// Perform input stepping
function inputStepping(button) {
    const action = button.dataset.stepperAction;
    const input = button.closest('[data-stepper-container]')?.querySelector('input');
    if (!input) return;

    try {
        if (action === 'less') {
            input.stepDown();
        } else {
            input.stepUp();
        }
    } catch (error) {
        console.warn("Error during input step:", error);
    }

    // Essential for form validation
    input.dispatchEvent(new Event('input', { bubbles: true }));
}

// Helper function to hide and clean preview images
function resetPreviewState(fileInputId) {
    const preview = document.querySelector(`[data-preview-for="${fileInputId}"]`);
    const img = preview?.querySelector('img');

    if (!img || !preview) return;

    // RAM cleaning
    if (img.src.startsWith('blob:')) {
        URL.revokeObjectURL(img.src);
    }

    img.src = '';

    preview.classList.add('hidden');
}

// Perform file cleaning
function fileCleaning(button) {
    const fileInput = button.closest('[data-file-container]')?.querySelector('input');
    if (!fileInput) return;

    clearFileInputData(fileInput);

    // Essential for form validation and preview reset
    fileInput.dispatchEvent(new Event('change', { bubbles: true }));
}

// Handle file uploads and show basic preview rendering
function handleFileChange(event) {
    const fileInput = event.currentTarget;
    if (!fileInput) return;

    const clearBtn = fileInput.closest('[data-file-container]')?.querySelector('[data-file-action="clear"]');
    if (!clearBtn) return;

    // If there is an active file, render a preview
    if (fileInput.files && fileInput.files.length > 0) {
        clearBtn.classList.remove('hidden');

        const uploadedFile = fileInput.files[0];
        const preview = document.querySelector(`[data-preview-for="${fileInput.id}"]`);
        const img = preview?.querySelector('img');

        if (img && preview) {
            const objectUrl = URL.createObjectURL(uploadedFile);
            img.src = objectUrl;
            preview.classList.remove('hidden');
        }
    } else {
        resetFileUIState(fileInput);
    }
}