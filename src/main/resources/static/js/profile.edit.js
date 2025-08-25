$(document).ready(function() {
        // Auto dismiss alerts after 5 seconds
        setTimeout(function() {
            $('.alert').fadeOut('slow');
        }, 5000);

        // Phone number formatting
        $('#phone').on('input', function() {
            var value = $(this).val().replace(/\D/g, '');
            if (value.length > 10) {
                value = value.substring(0, 10);
            }
            $(this).val(value);
        });

        // Form validation
        $('form').on('submit', function(e) {
            var fullName = $('#fullName').val().trim();

            if (fullName === '') {
                e.preventDefault();
                $('#fullName').closest('.form-group').addClass('has-error');

                // Show error message if not exists
                if ($('#fullName').siblings('.help-block').length === 0) {
                    $('#fullName').after('<span class="help-block"><i class="fa fa-exclamation-circle"></i> Họ tên không được để trống</span>');
                }

                // Focus on error field
                $('#fullName').focus();
                return false;
            }

            // Show loading state
            $(this).find('button[type="submit"]').html('<i class="fa fa-spinner fa-spin"></i> Đang lưu...').prop('disabled', true);
        });

        // Remove error state when user types
        $('input, textarea').on('input', function() {
            $(this).closest('.form-group').removeClass('has-error');
            $(this).siblings('.help-block').remove();
        });
    });
