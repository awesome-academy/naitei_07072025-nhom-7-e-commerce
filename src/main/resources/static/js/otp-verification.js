function togglePasswordVisibility(inputId, iconElement) {
    const input = document.getElementById(inputId);

    if (input.type === 'password') {
        input.type = 'text';
        iconElement.classList.remove('fa-eye');
        iconElement.classList.add('fa-eye-slash');
    } else {
        input.type = 'password';
        iconElement.classList.remove('fa-eye-slash');
        iconElement.classList.add('fa-eye');
    }
}

// Main initialization when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    // Register page functionality
    initializeRegisterForm();
    
    // OTP page functionality
    initializeOTPForm();
});

function initializeRegisterForm() {
    const registerForm = document.getElementById('registerForm');
    if (!registerForm) return; // Exit if not on register page

    const submitBtn = document.getElementById('submitBtn');
    const container = document.querySelector('.register-container');
    const passwordInput = document.getElementById('password');
    const confirmPasswordInput = document.getElementById('confirmPassword');
    const strengthFill = document.getElementById('strength-fill');
    const strengthText = document.getElementById('strength-text');
    const passwordMatch = document.getElementById('password-match');
    const progressFill = document.getElementById('progress-fill');

    // Auto-dismiss alerts after 5 seconds
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => {
        setTimeout(() => {
            alert.style.opacity = '0';
            alert.style.transform = 'translateY(-10px)';
            setTimeout(() => alert.remove(), 300);
        }, 5000);
    });

    // Form progress tracking
    let currentProgress = 0;
    const inputs = registerForm.querySelectorAll('input[required]');

    function updateProgress() {
        const filledInputs = Array.from(inputs).filter(input => input.value.trim() !== '');
        const progress = (filledInputs.length / inputs.length) * 100;
        if (progressFill) progressFill.style.width = progress + '%';

        // Update step indicators
        const stepInfo = document.getElementById('step-info');
        const stepSecurity = document.getElementById('step-security');
        const stepVerify = document.getElementById('step-verify');

        if (progress > 0 && stepInfo) stepInfo.classList.add('active');
        if (progress > 40) {
            if (stepInfo) stepInfo.classList.add('completed');
            if (stepSecurity) stepSecurity.classList.add('active');
        }
        if (progress === 100) {
            if (stepSecurity) stepSecurity.classList.add('completed');
            if (stepVerify) stepVerify.classList.add('active');
        }
    }

    // Password strength checker
    function checkPasswordStrength(password) {
        let strength = 0;
        const checks = [
            password.length >= 8,
            /[a-z]/.test(password),
            /[A-Z]/.test(password),
            /[0-9]/.test(password),
            /[^A-Za-z0-9]/.test(password)
        ];

        strength = checks.filter(check => check).length;

        const levels = [
            { class: 'strength-weak', text: 'Very Weak', color: '#e74c3c' },
            { class: 'strength-weak', text: 'Weak', color: '#e67e22' },
            { class: 'strength-fair', text: 'Fair', color: '#f39c12' },
            { class: 'strength-good', text: 'Good', color: '#27ae60' },
            { class: 'strength-strong', text: 'Strong', color: '#2ecc71' }
        ];

        const level = levels[strength] || levels[0];
        if (strengthFill) strengthFill.className = `password-strength-fill ${level.class}`;
        if (strengthText) strengthText.innerHTML = `Password strength: <strong style="color: ${level.color}">${level.text}</strong>`;

        return strength;
    }

    // Password confirmation checker
    function checkPasswordMatch() {
        if (!passwordInput || !confirmPasswordInput || !passwordMatch) return false;

        const password = passwordInput.value;
        const confirmPassword = confirmPasswordInput.value;

        if (confirmPassword === '') {
            passwordMatch.style.display = 'none';
            return;
        }

        passwordMatch.style.display = 'flex';

        if (password === confirmPassword) {
            passwordMatch.className = 'password-match match-success';
            passwordMatch.innerHTML = '<i class="fas fa-check match-icon"></i><span>Passwords match</span>';
            return true;
        } else {
            passwordMatch.className = 'password-match match-error';
            passwordMatch.innerHTML = '<i class="fas fa-times match-icon"></i><span>Passwords do not match</span>';
            return false;
        }
    }

    // Event listeners for inputs
    inputs.forEach(input => {
        input.addEventListener('input', updateProgress);

        input.addEventListener('focus', function() {
            this.parentElement.style.transform = 'scale(1.02)';
        });

        input.addEventListener('blur', function() {
            this.parentElement.style.transform = 'scale(1)';
        });
    });

    // Password event listeners
    if (passwordInput) {
        passwordInput.addEventListener('input', function() {
            checkPasswordStrength(this.value);
            checkPasswordMatch();
        });
    }

    if (confirmPasswordInput) {
        confirmPasswordInput.addEventListener('input', checkPasswordMatch);
    }

    // Form submission
    registerForm.addEventListener('submit', function(e) {
        if (passwordInput && confirmPasswordInput) {
            const passwordStrength = checkPasswordStrength(passwordInput.value);
            const passwordsMatch = checkPasswordMatch();

            if (passwordStrength < 2) {
                e.preventDefault();
                alert('Please use a stronger password (at least 8 characters with mixed case, numbers, and symbols)');
                return;
            }

            if (!passwordsMatch) {
                e.preventDefault();
                alert('Passwords do not match. Please check and try again.');
                return;
            }
        }

        if (container) container.classList.add('loading');
        if (submitBtn) submitBtn.disabled = true;
    });

    // Real-time validation
    const emailInput = document.querySelector('input[type="email"]');
    const phoneInput = document.querySelector('input[name="phone"]');

    if (emailInput) {
        emailInput.addEventListener('blur', function() {
            const email = this.value;
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

            if (email && !emailRegex.test(email)) {
                this.style.borderColor = '#e74c3c';
            } else {
                this.style.borderColor = '#e1e5e9';
            }
        });
    }

    if (phoneInput) {
        phoneInput.addEventListener('input', function() {
            // Remove non-numeric characters
            this.value = this.value.replace(/[^0-9+\-\s]/g, '');
        });
    }

    // Keyboard shortcuts
    document.addEventListener('keydown', function(e) {
        if (e.ctrlKey && e.key === 'Enter') {
            registerForm.submit();
        }
        if (e.key === 'Escape') {
            inputs.forEach(input => input.value = '');
            inputs[0].focus();
            updateProgress();
        }
    });

    // Initialize progress
    updateProgress();
}

function initializeOTPForm() {
    const otpForm = document.getElementById('otpForm');
    if (!otpForm) return; // Exit if not on OTP page

    const otpInputs = document.querySelectorAll('.otp-input');
    const combinedOtpInput = document.getElementById('combinedOtp');
    const verifyBtn = document.getElementById('verifyBtn');
    const resendBtn = document.getElementById('resendBtn');
    const timerElement = document.getElementById('timer');
    const loadingOverlay = document.getElementById('loadingOverlay');

    let timeLeft = 300; // 5 minutes in seconds
    let timerInterval;

    // Auto-dismiss alerts
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => {
        setTimeout(() => {
            alert.style.opacity = '0';
            alert.style.transform = 'translateY(-10px)';
            setTimeout(() => alert.remove(), 300);
        }, 5000);
    });

    // Initialize timer
    function startTimer() {
        timerInterval = setInterval(() => {
            timeLeft--;
            updateTimerDisplay();

            if (timeLeft <= 0) {
                clearInterval(timerInterval);
                enableResendButton();
            }
        }, 1000);
    }

    function updateTimerDisplay() {
        const minutes = Math.floor(timeLeft / 60);
        const seconds = timeLeft % 60;
        const timeString = `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;

        if (timerElement) {
            timerElement.textContent = timeString;

            // Update timer styling based on remaining time
            timerElement.className = 'timer';
            if (timeLeft <= 60) {
                timerElement.classList.add('danger');
            } else if (timeLeft <= 120) {
                timerElement.classList.add('warning');
            }
        }
    }

    function enableResendButton() {
        if (resendBtn) {
            resendBtn.disabled = false;
            resendBtn.classList.add('active');
            resendBtn.innerHTML = '<i class="fas fa-redo"></i><span>Resend Code</span>';
        }
    }

    // OTP input handling
    otpInputs.forEach((input, index) => {
        input.addEventListener('input', function(e) {
            // Only allow numbers
            this.value = this.value.replace(/[^0-9]/g, '');

            if (this.value) {
                this.classList.add('filled');

                // Auto-focus next input
                if (index < otpInputs.length - 1) {
                    otpInputs[index + 1].focus();
                }
            } else {
                this.classList.remove('filled');
            }

            updateCombinedOtp();
        });

        input.addEventListener('keydown', function(e) {
            // Handle backspace
            if (e.key === 'Backspace' && !this.value && index > 0) {
                otpInputs[index - 1].focus();
                otpInputs[index - 1].value = '';
                otpInputs[index - 1].classList.remove('filled');
                updateCombinedOtp();
            }

            // Handle arrow keys
            if (e.key === 'ArrowLeft' && index > 0) {
                otpInputs[index - 1].focus();
            } else if (e.key === 'ArrowRight' && index < otpInputs.length - 1) {
                otpInputs[index + 1].focus();
            }
        });

        input.addEventListener('paste', function(e) {
            e.preventDefault();
            const pastedData = e.clipboardData.getData('text').replace(/[^0-9]/g, '');

            for (let i = 0; i < Math.min(pastedData.length, otpInputs.length - index); i++) {
                if (otpInputs[index + i]) {
                    otpInputs[index + i].value = pastedData[i];
                    otpInputs[index + i].classList.add('filled');
                }
            }

            updateCombinedOtp();
        });
    });

    function updateCombinedOtp() {
        const otp = Array.from(otpInputs).map(input => input.value).join('');
        if (combinedOtpInput) combinedOtpInput.value = otp;

        // Enable verify button when all inputs are filled
        if (verifyBtn) verifyBtn.disabled = otp.length !== 6;

        // Clear error styling
        otpInputs.forEach(input => input.classList.remove('error'));
    }

    function showError() {
        otpInputs.forEach(input => {
            input.classList.add('error');
            input.value = '';
            input.classList.remove('filled');
        });
        if (combinedOtpInput) combinedOtpInput.value = '';
        if (verifyBtn) verifyBtn.disabled = true;
        if (otpInputs[0]) otpInputs[0].focus();
    }

    // Form submission
    otpForm.addEventListener('submit', function(e) {
        e.preventDefault();

        if (!combinedOtpInput || combinedOtpInput.value.length !== 6) {
            showError();
            return;
        }

        // Show loading
        if (loadingOverlay) loadingOverlay.style.display = 'flex';

        // Simulate form submission (replace with actual submission)
        setTimeout(() => {
            // This would be the actual form submission
            this.submit();
        }, 1000);
    });

    // Resend button
    if (resendBtn) {
        resendBtn.addEventListener('click', function() {
            if (!this.disabled) {
                // Reset timer
                timeLeft = 300;
                this.disabled = true;
                this.classList.remove('active');
                this.innerHTML = '<i class="fas fa-redo"></i><span>Resending...</span>';

                // Clear inputs
                otpInputs.forEach(input => {
                    input.value = '';
                    input.classList.remove('filled', 'error');
                });
                if (combinedOtpInput) combinedOtpInput.value = '';
                if (verifyBtn) verifyBtn.disabled = true;

                setTimeout(() => {
                    this.innerHTML = '<i class="fas fa-redo"></i><span>Code Sent!</span>';
                    startTimer();
                    if (otpInputs[0]) otpInputs[0].focus();

                    setTimeout(() => {
                        this.innerHTML = '<i class="fas fa-redo"></i><span>Resend Code</span>';
                    }, 2000);
                }, 1500);
            }
        });
    }

    // Keyboard shortcuts for OTP
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Enter' && verifyBtn && !verifyBtn.disabled) {
            otpForm.submit();
        } else if (e.key === 'Escape') {
            otpInputs.forEach(input => {
                input.value = '';
                input.classList.remove('filled', 'error');
            });
            if (combinedOtpInput) combinedOtpInput.value = '';
            if (verifyBtn) verifyBtn.disabled = true;
            if (otpInputs[0]) otpInputs[0].focus();
        }
    });

    // Initialize OTP page
    startTimer();
    if (otpInputs[0]) otpInputs[0].focus();
    updateTimerDisplay();
}

// jQuery-based OTP functionality (from the second document)
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

    // Initialize timer if timer element exists
    if ($('#timer').length > 0 && typeof timeLeft === 'undefined') {
        updateTimer();
    }

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

    // Auto-focus first OTP input
    $('.otp-input').first().focus();

    // iCheck initialization (if available)
    if (typeof $.fn.iCheck !== 'undefined') {
        $('input').iCheck({
            checkboxClass: 'icheckbox_square-blue',
            radioClass: 'iradio_square-blue',
            increaseArea: '20%'
        });
    }
});// Register Page JavaScript

// Toggle password visibility function
function togglePasswordVisibility(inputId, iconElement) {
    const input = document.getElementById(inputId);

    if (input.type === 'password') {
        input.type = 'text';
        iconElement.classList.remove('fa-eye');
        iconElement.classList.add('fa-eye-slash');
    } else {
        input.type = 'password';
        iconElement.classList.remove('fa-eye-slash');
        iconElement.classList.add('fa-eye');
    }
}

// Main initialization when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    const registerForm = document.getElementById('registerForm');
    const submitBtn = document.getElementById('submitBtn');
    const container = document.querySelector('.register-container');
    const passwordInput = document.getElementById('password');
    const confirmPasswordInput = document.getElementById('confirmPassword');
    const strengthFill = document.getElementById('strength-fill');
    const strengthText = document.getElementById('strength-text');
    const passwordMatch = document.getElementById('password-match');
    const progressFill = document.getElementById('progress-fill');

    // Auto-dismiss alerts after 5 seconds
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => {
        setTimeout(() => {
            alert.style.opacity = '0';
            alert.style.transform = 'translateY(-10px)';
            setTimeout(() => alert.remove(), 300);
        }, 5000);
    });

    // Form progress tracking
    let currentProgress = 0;
    const inputs = registerForm.querySelectorAll('input[required]');

    function updateProgress() {
        const filledInputs = Array.from(inputs).filter(input => input.value.trim() !== '');
        const progress = (filledInputs.length / inputs.length) * 100;
        progressFill.style.width = progress + '%';

        // Update step indicators
        if (progress > 0) document.getElementById('step-info').classList.add('active');
        if (progress > 40) {
            document.getElementById('step-info').classList.add('completed');
            document.getElementById('step-security').classList.add('active');
        }
        if (progress === 100) {
            document.getElementById('step-security').classList.add('completed');
            document.getElementById('step-verify').classList.add('active');
        }
    }

    // Password strength checker
    function checkPasswordStrength(password) {
        let strength = 0;
        const checks = [
            password.length >= 8,
            /[a-z]/.test(password),
            /[A-Z]/.test(password),
            /[0-9]/.test(password),
            /[^A-Za-z0-9]/.test(password)
        ];

        strength = checks.filter(check => check).length;

        const levels = [
            { class: 'strength-weak', text: 'Very Weak', color: '#e74c3c' },
            { class: 'strength-weak', text: 'Weak', color: '#e67e22' },
            { class: 'strength-fair', text: 'Fair', color: '#f39c12' },
            { class: 'strength-good', text: 'Good', color: '#27ae60' },
            { class: 'strength-strong', text: 'Strong', color: '#2ecc71' }
        ];

        const level = levels[strength] || levels[0];
        strengthFill.className = `password-strength-fill ${level.class}`;
        strengthText.innerHTML = `Password strength: <strong style="color: ${level.color}">${level.text}</strong>`;

        return strength;
    }

    // Password confirmation checker
    function checkPasswordMatch() {
        const password = passwordInput.value;
        const confirmPassword = confirmPasswordInput.value;

        if (confirmPassword === '') {
            passwordMatch.style.display = 'none';
            return;
        }

        passwordMatch.style.display = 'flex';

        if (password === confirmPassword) {
            passwordMatch.className = 'password-match match-success';
            passwordMatch.innerHTML = '<i class="fas fa-check match-icon"></i><span>Passwords match</span>';
            return true;
        } else {
            passwordMatch.className = 'password-match match-error';
            passwordMatch.innerHTML = '<i class="fas fa-times match-icon"></i><span>Passwords do not match</span>';
            return false;
        }
    }

    // Event listeners for inputs
    inputs.forEach(input => {
        input.addEventListener('input', updateProgress);

        input.addEventListener('focus', function() {
            this.parentElement.style.transform = 'scale(1.02)';
        });

        input.addEventListener('blur', function() {
            this.parentElement.style.transform = 'scale(1)';
        });
    });

    // Password event listeners
    if (passwordInput) {
        passwordInput.addEventListener('input', function() {
            checkPasswordStrength(this.value);
            checkPasswordMatch();
        });
    }

    if (confirmPasswordInput) {
        confirmPasswordInput.addEventListener('input', checkPasswordMatch);
    }

    // Form submission
    registerForm.addEventListener('submit', function(e) {
        const passwordStrength = checkPasswordStrength(passwordInput.value);
        const passwordsMatch = checkPasswordMatch();

        if (passwordStrength < 2) {
            e.preventDefault();
            alert('Please use a stronger password (at least 8 characters with mixed case, numbers, and symbols)');
            return;
        }

        if (!passwordsMatch) {
            e.preventDefault();
            alert('Passwords do not match. Please check and try again.');
            return;
        }

        container.classList.add('loading');
        submitBtn.disabled = true;
    });

    // Real-time validation
    const emailInput = document.querySelector('input[type="email"]');
    const phoneInput = document.querySelector('input[name="phone"]');

    if (emailInput) {
        emailInput.addEventListener('blur', function() {
            const email = this.value;
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

            if (email && !emailRegex.test(email)) {
                this.style.borderColor = '#e74c3c';
            } else {
                this.style.borderColor = '#e1e5e9';
            }
        });
    }

    if (phoneInput) {
        phoneInput.addEventListener('input', function() {
            // Remove non-numeric characters
            this.value = this.value.replace(/[^0-9+\-\s]/g, '');
        });
    }

    // Keyboard shortcuts
    document.addEventListener('keydown', function(e) {
        if (e.ctrlKey && e.key === 'Enter') {
            registerForm.submit();
        }
        if (e.key === 'Escape') {
            inputs.forEach(input => input.value = '');
            inputs[0].focus();
            updateProgress();
        }
    });

    // Initialize progress
    updateProgress();
});

// jQuery-based OTP functionality (from the second document)
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

    // Initialize timer if timer element exists
    if ($('#timer').length > 0) {
        updateTimer();
    }

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

    // Auto-focus first OTP input
    $('.otp-input').first().focus();

    // iCheck initialization (if available)
    if (typeof $.fn.iCheck !== 'undefined') {
        $('input').iCheck({
            checkboxClass: 'icheckbox_square-blue',
            radioClass: 'iradio_square-blue',
            increaseArea: '20%'
        });
    }
});
