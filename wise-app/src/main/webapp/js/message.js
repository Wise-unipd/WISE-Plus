document.addEventListener('DOMContentLoaded', function () {
    const closeStatusMsgBtn = document.getElementById('status-msg-close-btn');
    if (!closeStatusMsgBtn) return;

    closeStatusMsgBtn.addEventListener('click', () => {
        const statusMsg = document.getElementById('status-msg');
        if (!statusMsg) return;
        statusMsg.classList.add('hidden');
    });
    console.log("Event listener added to Close Status Message Button.");
});