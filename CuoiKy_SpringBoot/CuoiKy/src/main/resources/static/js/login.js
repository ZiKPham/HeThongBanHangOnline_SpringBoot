// Toggle password visibility
document.querySelector('.password-toggle').addEventListener('click', function() {
    const passwordInput = document.querySelector('#password');
    const icon = this.querySelector('i');
    
    if (passwordInput.type === 'password') {
        passwordInput.type = 'text';
        icon.classList.remove('fa-eye');
        icon.classList.add('fa-eye-slash');
    } else {
        passwordInput.type = 'password';
        icon.classList.remove('fa-eye-slash');
        icon.classList.add('fa-eye');
    }
});

// Form validation
const loginForm = document.getElementById('loginForm');
const username = document.getElementById('username');
const password = document.getElementById('password');
const usernameError = document.getElementById('username-error');
const passwordError = document.getElementById('password-error');

function showError(input, errorDiv) {
    input.classList.add('is-invalid');
    errorDiv.classList.add('show');
}

function hideError(input, errorDiv) {
    input.classList.remove('is-invalid');
    errorDiv.classList.remove('show');
}

function validateInput(input, errorDiv) {
    if (!input.value.trim()) {
        showError(input, errorDiv);
        return false;
    }
    hideError(input, errorDiv);
    return true;
}

loginForm.addEventListener('submit', function(e) {
    e.preventDefault();
    
    const isUsernameValid = validateInput(username, usernameError);
    const isPasswordValid = validateInput(password, passwordError);

    if (isUsernameValid && isPasswordValid) {
        this.submit();
    }
});

// Real-time validation
username.addEventListener('input', function() {
    validateInput(this, usernameError);
});

password.addEventListener('input', function() {
    validateInput(this, passwordError);
});

// Validate on blur
username.addEventListener('blur', function() {
    validateInput(this, usernameError);
});

password.addEventListener('blur', function() {
    validateInput(this, passwordError);
}); 