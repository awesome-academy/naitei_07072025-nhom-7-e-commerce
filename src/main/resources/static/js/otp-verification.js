$(document).ready(function() {
    // OTP Input Handling
    $('.otp-input').on('input', function() {
        var value = $(this).val();

        // Only allow numbers
        if (!/^\d$/.test(value)) {
            $(this).val('');
            return;
        }

        // Move to next input
        if (value.length === 1) {
            $(this).next('.otp-input').focus();
        }

        // Combine all OTP values
        var combinedOtp = '';
        $('.otp-input').each(function() {
            combinedOtp += $(this).val();
        });
        $('#combinedOtp').val(combinedOtp);

        // Enable verify button if all 6 digits entered
        if (combinedOtp.length === 6) {
            $('#verifyBtn').prop('disabled', false);
        } else {
            $('#verifyBtn').prop('disabled', true);
        }
    });

    // Backspace handling
    $('.otp-input').on('keydown', function(e) {
        if (e.keyCode === 8 && $(this).val() === '') {
            $(this).prev('.otp-input').focus();
        }
    });

    // Paste handling
    $('.otp-input').first().on('paste', function(e) {
        e.preventDefault();
        var pastedData = e.originalEvent.clipboardData.getData('text').replace(/\D/g, '');

        if (pastedData.length === 6) {
            for (var i = 0; i < 6; i++) {
                $('.otp-input').eq(i).val(pastedData[i] || '');
            }
            $('#combinedOtp').val(pastedData);
            $('#verifyBtn').prop('disabled', false);
        }
    });

    // Countdown Timer (5 minutes = 300 seconds)
    var timeLeft = 300;

    function updateTimer() {
        var minutes = Math.floor(timeLeft / 60);
        var seconds = timeLeft % 60;
        $('#timer').text(
            (minutes < 10 ? '0' : '') + minutes + ':' +
            (seconds < 10 ? '0' : '') + seconds
        );

        if (timeLeft <= 0) {
            $('#countdown').html('<span class="text-danger"><i class="fa fa-exclamation-triangle"></i> Code has expired</span>');
            $('#verifyBtn').prop('disabled', true);
            $('#resendBtn').prop('disabled', false).removeClass('btn-default').addClass('btn-warning');
            return;
        }

        timeLeft--;
        setTimeout(updateTimer, 1000);
    }

    updateTimer();

    // Resend OTP
    $('#resendBtn').on('click', function() {
        var btn = $(this);
        btn.prop('disabled', true).html('<i class="fa fa-spinner fa-spin"></i> Sending...');

        $.post('/auth/admin/resend-otp', { email: $('input[name="email"]').val() })
        .done(function(response) {
            // Reset timer
            timeLeft = 300;
            $('#countdown').html('<i class="fa fa-clock-o"></i> Code expires in: <span id="timer">05:00</span>');
            updateTimer();

            // Clear OTP inputs
            $('.otp-input').val('');
            $('#combinedOtp').val('');
            $('#verifyBtn').prop('disabled', true);

            // Show success message
            $('.alert').remove();
            $('.login-box-msg').after('<div class="alert alert-success alert-dismissible"><button type="button" class="close" data-dismiss="alert">×</button>New verification code has been sent to your email!</div>');

            btn.prop('disabled', true).removeClass('btn-warning').addClass('btn-default').html('<i class="fa fa-refresh"></i> Resend Code');
        })
        .fail(function() {
            $('.alert').remove();
            $('.login-box-msg').after('<div class="alert alert-danger alert-dismissible"><button type="button" class="close" data-dismiss="alert">×</button>Failed to resend code. Please try again.</div>');
            btn.prop('disabled', false).html('<i class="fa fa-refresh"></i> Resend Code');
        });
    });

    // Auto-focus first input
    $('.otp-input').first().focus();

    $('input').iCheck({
        checkboxClass: 'icheckbox_square-blue',
        radioClass: 'iradio_square-blue',
        increaseArea: '20%' /* optional */
    });
});
