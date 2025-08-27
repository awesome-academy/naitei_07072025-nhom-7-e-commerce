$(document).ready(function() {
    // Password strength checker
    $('#newPassword').on('input', function() {
        var password = $(this).val();
        var strength = checkPasswordStrength(password);

        $('#passwordStrength').css('width', strength.percentage + '%')
                             .removeClass('bg-red bg-yellow bg-green bg-blue')
                             .addClass('bg-' + strength.color);

        $('#strengthText').text(strength.text).removeClass().addClass('text-' + strength.color);

        // Real-time password pattern validation
        validatePasswordPattern(password);
    });

    // Confirm password validation
    $('#confirmNewPassword').on('input', function() {
        var newPassword = $('#newPassword').val();
        var confirmPassword = $(this).val();

        // Remove existing client-side error spans
        $(this).next('.text-danger:not(.server-error)').remove();

        if (newPassword !== confirmPassword && confirmPassword !== '') {
            $(this).addClass('form-control-error');
            if ($(this).next('.text-danger:not(.server-error)').length === 0) {
                $(this).after('<span class="text-danger">Passwords do not match</span>');
            }
        } else {
            $(this).removeClass('form-control-error');
            $(this).next('.text-danger:not(.server-error)').remove();
        }
    });

    // Real-time password pattern validation
    function validatePasswordPattern(password) {
        var $passwordField = $('#newPassword');
        var pattern = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{8,}$/;

        // Remove existing client-side error spans
        $passwordField.next('.text-danger:not(.server-error)').remove();

        if (password.length > 0 && !pattern.test(password)) {
            $passwordField.addClass('form-control-error');
            if ($passwordField.next('.text-danger:not(.server-error)').length === 0) {
                $passwordField.after('<span class="text-danger">Password must be at least 8 characters with lowercase, uppercase, and digits</span>');
            }
        } else {
            $passwordField.removeClass('form-control-error');
        }
    }

    // Form submission with client-side validation
    $('#changePasswordForm').on('submit', function(e) {
        // Clear previous client-side errors
        $('.text-danger:not(.server-error)').remove();
        $('.form-control').removeClass('form-control-error');

        var newPassword = $('#newPassword').val();
        var confirmPassword = $('#confirmNewPassword').val();
        var oldPassword = $('#oldPassword').val();

        // Basic client-side validation
        var isValid = true;

        if (!oldPassword.trim()) {
            showFieldError('#oldPassword', 'Current password is required');
            isValid = false;
        }

        if (!newPassword.trim()) {
            showFieldError('#newPassword', 'New password is required');
            isValid = false;
        }

        if (!confirmPassword.trim()) {
            showFieldError('#confirmNewPassword', 'Confirm password is required');
            isValid = false;
        }

        // Password pattern validation
        var pattern = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{8,}$/;
        if (newPassword && !pattern.test(newPassword)) {
            showFieldError('#newPassword', 'Password must be at least 8 characters with lowercase, uppercase, and digits');
            isValid = false;
        }

        // Password match validation
        if (newPassword && confirmPassword && newPassword !== confirmPassword) {
            showFieldError('#confirmNewPassword', 'Passwords do not match');
            isValid = false;
        }

        if (!isValid) {
            e.preventDefault();
            showAlert('danger', 'Please fix the errors below');
            return false;
        }

        // Show loading state
        $('#changePasswordBtn').prop('disabled', true)
                              .html('<i class="fa fa-spinner fa-spin"></i> Changing...');

        // Let the form submit normally to server
    });

    // Function to show field-specific errors
    function showFieldError(fieldSelector, message) {
        var $field = $(fieldSelector);
        $field.addClass('form-control-error');

        // Remove existing client-side error message
        $field.next('.text-danger:not(.server-error)').remove();

        // Add new error message
        $field.after('<span class="text-danger">' + message + '</span>');
    }

    // Clear form when modal is hidden
    $('#changePasswordModal').on('hidden.bs.modal', function () {
        $('#changePasswordForm')[0].reset();
        $('.text-danger:not(.server-error)').remove();
        $('.form-control').removeClass('form-control-error');
        $('#alertContainer').hide();
        $('#passwordStrength').css('width', '0%');
        $('#strengthText').text('Enter a password to see strength').removeClass();

        // Reset button state
        $('#changePasswordBtn').prop('disabled', false)
                              .html('<i class="fa fa-save"></i> Change Password');
    });
});

function checkPasswordStrength(password) {
    var strength = {
        percentage: 0,
        color: 'red',
        text: 'Very Weak'
    };

    if (password.length === 0) {
        strength.text = 'Enter a password to see strength';
        strength.color = 'muted';
        return strength;
    }

    var score = 0;

    // Length
    if (password.length >= 8) score += 25;
    if (password.length >= 12) score += 25;

    // Character types
    if (/[a-z]/.test(password)) score += 10;
    if (/[A-Z]/.test(password)) score += 15;
    if (/[0-9]/.test(password)) score += 15;
    if (/[^a-zA-Z0-9]/.test(password)) score += 10;

    strength.percentage = Math.min(100, score);

    if (score < 30) {
        strength.color = 'red';
        strength.text = 'Very Weak';
    } else if (score < 50) {
        strength.color = 'yellow';
        strength.text = 'Weak';
    } else if (score < 75) {
        strength.color = 'blue';
        strength.text = 'Good';
    } else {
        strength.color = 'green';
        strength.text = 'Strong';
    }

    return strength;
}

function showAlert(type, message) {
    $('#alertMessage').text(message);
    $('#alertContainer .alert').removeClass('alert-success alert-danger alert-warning alert-info')
                               .addClass('alert-' + type);
    $('#alertContainer').slideDown();
}
