/**
 * Movie Poster file validations (Type, Size, and Aspect Ratio).
 * Returns a Promise that resolves with an error message string, or null if valid.
 */
function validateMoviePoster(value, field) {
    return new Promise((resolve) => {
        if (!field.files || field.files.length === 0) {
            return resolve(null);
        }

        const file = field.files[0];
        const maxSize = 5 * 1024 * 1024; // 5MB limit
        const allowedTypes = ['image/jpeg', 'image/png'];

        // File Type Check
        if (!allowedTypes.includes(file.type)) {
            return resolve('Unsupported file type. Please upload a JPEG or PNG.');
        }

        // File Size Check
        if (file.size > maxSize) {
            return resolve('The image size cannot exceed 5MB.');
        }

        // Asynchronous Aspect Ratio Check
        const objectUrl = URL.createObjectURL(file);
        const tempImg = new Image();

        tempImg.onload = function () {
            const width = tempImg.naturalWidth;
            const height = tempImg.naturalHeight;
            const ratio = width / height;

            URL.revokeObjectURL(objectUrl);

            // Expecting vertical 2:3 ratio
            if (Math.abs(ratio - 0.68) > 0.05) {
                return resolve('Please upload a vertical image with a 2:3 aspect ratio.');
            }

            // Everything is completely valid!
            return resolve(null);
        };

        tempImg.onerror = function () {
            URL.revokeObjectURL(objectUrl);
            return resolve('Failed to read the image file format properties.');
        };

        tempImg.src = objectUrl;
    });
}