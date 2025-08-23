$(function () {
    /** -------------------------
     *  FORM PROGRESS
     -------------------------- */
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

// Enhanced form handling (Modern vanilla JS)
document.addEventListener('DOMContentLoaded', function() {
    const loginForm = document.getElementById('loginForm');
    const submitBtn = document.querySelector('.btn-signin');
    const container = document.querySelector('.login-container');

    // Auto-dismiss alerts after 5 seconds (Enhanced)
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => {
        setTimeout(() => {
            alert.style.opacity = '0';
            alert.style.transform = 'translateY(-10px)';
            setTimeout(() => alert.remove(), 300);
        }, 5000);
    });

    // Form submission handling
    if (loginForm && submitBtn && container) {
        loginForm.addEventListener('submit', function(e) {
            container.classList.add('loading');
            submitBtn.disabled = true;
        });
    }

    // Input focus effects
    const inputs = document.querySelectorAll('.form-control');
    inputs.forEach(input => {
        input.addEventListener('focus', function() {
            this.parentElement.style.transform = 'scale(1.02)';
        });

        input.addEventListener('blur', function() {
            this.parentElement.style.transform = 'scale(1)';
        });
    });

    // Add floating labels effect
    inputs.forEach(input => {
        const placeholder = input.getAttribute('placeholder');

        input.addEventListener('focus', function() {
            if (!this.value) {
                this.setAttribute('placeholder', '');
            }
        });

        input.addEventListener('blur', function() {
            if (!this.value) {
                this.setAttribute('placeholder', placeholder);
            }
        });
    });

    // Keyboard shortcuts
    document.addEventListener('keydown', function(e) {
        // Ctrl + Enter to submit
        if (e.ctrlKey && e.key === 'Enter') {
            if (loginForm) {
                loginForm.submit();
            }
        }
        // Escape to clear form
        if (e.key === 'Escape') {
            inputs.forEach(input => input.value = '');
            if (inputs[0]) {
                inputs[0].focus();
            }
        }
    });

    // Show password toggle functionality
    function togglePassword() {
        const passwordInput = document.querySelector('input[type="password"]');
        const icon = document.querySelector('.fa-lock');

        if (passwordInput && icon) {
            if (passwordInput.type === 'password') {
                passwordInput.type = 'text';
                icon.classList.remove('fa-lock');
                icon.classList.add('fa-lock-open');
            } else {
                passwordInput.type = 'password';
                icon.classList.remove('fa-lock-open');
                icon.classList.add('fa-lock');
            }
        }
    }

    // Add double-click to toggle password visibility
    const lockIcon = document.querySelector('.fa-lock');
    if (lockIcon) {
        lockIcon.addEventListener('dblclick', togglePassword);
    }

    // Alert close button functionality
    const closeButtons = document.querySelectorAll('.alert .close');
    closeButtons.forEach(button => {
        button.addEventListener('click', function() {
            this.parentElement.remove();
        });
    });

    // Enhanced input animations
    inputs.forEach(input => {
        // Add ripple effect on click
        input.addEventListener('click', function(e) {
            const ripple = document.createElement('div');
            ripple.style.position = 'absolute';
            ripple.style.borderRadius = '50%';
            ripple.style.background = 'rgba(102, 126, 234, 0.6)';
            ripple.style.transform = 'scale(0)';
            ripple.style.animation = 'ripple 0.6s linear';
            ripple.style.left = (e.offsetX - 10) + 'px';
            ripple.style.top = (e.offsetY - 10) + 'px';
            ripple.style.width = '20px';
            ripple.style.height = '20px';

            this.style.position = 'relative';
            this.appendChild(ripple);

            setTimeout(() => {
                ripple.remove();
            }, 600);
        });
    });
});

// CSS for ripple animation (injected via JS)
const style = document.createElement('style');
style.textContent = `
    @keyframes ripple {
        to {
            transform: scale(4);
            opacity: 0;
        }
    }
`;
document.head.appendChild(style);
