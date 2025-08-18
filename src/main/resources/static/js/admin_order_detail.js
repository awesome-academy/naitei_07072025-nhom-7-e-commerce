document.addEventListener("DOMContentLoaded", function() {
    const allStatusSelectors = document.querySelectorAll('.status-selector');
    function toggleReasonBlock(selector) {
        const form = selector.closest('form');
        if (!form) return;

        const reasonBlock = form.querySelector('.reason-block');
        if (!reasonBlock) return;

        const selectedStatus = selector.value;
        if (selectedStatus === 'CANCELLED' || selectedStatus === 'REJECTED') {
            reasonBlock.classList.remove('d-none');
        } else {
            reasonBlock.classList.add('d-none'); 
        }
    }
    allStatusSelectors.forEach(function(selector) {
        toggleReasonBlock(selector); 
        selector.addEventListener('change', function() {
            toggleReasonBlock(this);
        });
    });
});
