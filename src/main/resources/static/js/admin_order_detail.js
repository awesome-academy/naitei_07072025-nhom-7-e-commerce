$(document).ready(function() {
    function toggleReasonBlock(selector) {
        var form = $(selector).closest('form');
        if (!form.length) return;

        var reasonBlock = form.find('.reason-block');
        if (!reasonBlock.length) return;

        var selectedStatus = $(selector).val();

        if (selectedStatus === 'CANCELLED' || selectedStatus === 'REJECTED') {
            reasonBlock.removeClass('d-none').show();
        } else {
            reasonBlock.addClass('d-none').hide();
        }
    }

    $(document).on('change', '.status-selector', function() {
        toggleReasonBlock(this);
    });

    $('.status-selector').each(function() {
        if ($(this).closest('.modal').length === 0) {
            toggleReasonBlock(this);
        }
    });
	
    $('.modal').on('show.bs.modal', function() {
        var selectorInModal = $(this).find('.status-selector');
        if (selectorInModal.length) {
            toggleReasonBlock(selectorInModal);
        }
    });

});
