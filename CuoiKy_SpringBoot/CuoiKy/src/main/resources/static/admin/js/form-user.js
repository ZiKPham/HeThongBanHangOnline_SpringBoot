// Form validation
(function() {
    'use strict';
    window.addEventListener('load', function() {
        var forms = document.getElementsByClassName('needs-validation');
        var validation = Array.prototype.filter.call(forms, function(form) {
            form.addEventListener('submit', function(event) {
                if (form.checkValidity() === false) {
                    event.preventDefault();
                    event.stopPropagation();
                } else if (document.getElementById('newPassword').value !== document.getElementById('confirmPassword').value) {
                    event.preventDefault();
                    event.stopPropagation();
                    alert('Mật khẩu và xác nhận mật khẩu không khớp!');
                    document.getElementById('newPassword').classList.add('is-invalid');
                    document.getElementById('confirmPassword').classList.add('is-invalid');
                }
                form.classList.add('was-validated');
            }, false);
        });
    }, false);
})();

// Toggle password visibility
function togglePasswordVisibility(fieldId) {
    var passwordField = document.getElementById(fieldId);
    var eyeIcon = passwordField.nextElementSibling.querySelector('i');
    
    if (passwordField.type === "password") {
        passwordField.type = "text";
        eyeIcon.classList.remove("fa-eye");
        eyeIcon.classList.add("fa-eye-slash");
    } else {
        passwordField.type = "password";
        eyeIcon.classList.remove("fa-eye-slash");
        eyeIcon.classList.add("fa-eye");
    }
} 