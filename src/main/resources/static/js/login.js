$(function () {
        // Initialize iCheck
        $('input[type="checkbox"]').iCheck({
            checkboxClass: 'icheckbox_square-blue',
            radioClass: 'iradio_square-blue',
            increaseArea: '20%'
        });

        // Form validation effects
        $('.form-control').on('focus', function() {
            $(this).parent().addClass('focused');
        });

        $('.form-control').on('blur', function() {
            if ($(this).val() === '') {
                $(this).parent().removeClass('focused');
            }
        });

        // Auto dismiss alerts after 5 seconds
        setTimeout(function() {
            $('.alert').fadeOut('slow');
        }, 5000);
    });
