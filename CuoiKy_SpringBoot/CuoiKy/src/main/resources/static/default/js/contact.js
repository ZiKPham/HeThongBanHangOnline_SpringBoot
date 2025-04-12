// Contact Page JavaScript
document.addEventListener('DOMContentLoaded', function() {
    // Add animation classes when elements come into view
    const animateElements = document.querySelectorAll('.animated');
    
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add('fadeInUp');
                observer.unobserve(entry.target);
            }
        });
    }, {
        threshold: 0.1
    });
    
    animateElements.forEach(element => {
        observer.observe(element);
    });
    
    // Animation for contact info items
    const contactInfoItems = document.querySelectorAll('.contact-info-item');
    
    contactInfoItems.forEach((item, index) => {
        // Thêm delay tăng dần cho mỗi item
        setTimeout(() => {
            item.classList.add('fadeInUp');
        }, 200 * index);
    });
    
    // Form validation
    const contactForm = document.querySelector('.contact-form');
    
    if (contactForm) {
        contactForm.addEventListener('submit', function(e) {
            let isValid = true;
            const name = document.getElementById('name');
            const email = document.getElementById('email');
            const phone = document.getElementById('phone');
            const subject = document.getElementById('subject');
            const message = document.getElementById('message');
            
            // Reset validation messages
            const errorMessages = document.querySelectorAll('.error-message');
            errorMessages.forEach(msg => msg.remove());
            
            // Validate name
            if (name.value.trim().length < 2) {
                showError(name, 'Vui lòng nhập họ tên hợp lệ (ít nhất 2 ký tự).');
                isValid = false;
            }
            
            // Validate email
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (!emailRegex.test(email.value.trim())) {
                showError(email, 'Vui lòng nhập địa chỉ email hợp lệ.');
                isValid = false;
            }
            
            // Validate phone if provided
            if (phone.value.trim() !== '') {
                const phoneRegex = /^(0|\+84|84)[3|5|7|8|9][0-9]{8}$/;
                if (!phoneRegex.test(phone.value.trim().replace(/\s/g, ''))) {
                    showError(phone, 'Vui lòng nhập số điện thoại hợp lệ.');
                    isValid = false;
                }
            }
            
            // Validate subject
            if (subject.value.trim().length < 5) {
                showError(subject, 'Vui lòng nhập tiêu đề (ít nhất 5 ký tự).');
                isValid = false;
            }
            
            // Validate message
            if (message.value.trim().length < 10) {
                showError(message, 'Vui lòng nhập nội dung tin nhắn (ít nhất 10 ký tự).');
                isValid = false;
            }
            
            if (!isValid) {
                e.preventDefault();
            } else {
                // Thêm hiệu ứng khi gửi thành công
                contactForm.classList.add('submitting');
            }
        });
    }
    
    // Add form field animations
    const formInputs = document.querySelectorAll('.form-control');
    
    formInputs.forEach(input => {
        // Animation khi focus vào input
        input.addEventListener('focus', function() {
            this.parentElement.classList.add('focused');
        });
        
        // Animation khi blur khỏi input
        input.addEventListener('blur', function() {
            this.parentElement.classList.remove('focused');
            if (this.value.trim() !== '') {
                this.classList.add('has-value');
            } else {
                this.classList.remove('has-value');
            }
        });
    });
    
    // Hiệu ứng hover cho các thẻ info
    const contactCards = document.querySelectorAll('.contact-card');
    
    contactCards.forEach(card => {
        card.addEventListener('mouseenter', function() {
            this.style.transform = 'translateY(-5px)';
        });
        
        card.addEventListener('mouseleave', function() {
            this.style.transform = 'translateY(0)';
        });
    });
    
    // Utility function to show error message
    function showError(input, message) {
        const errorDiv = document.createElement('div');
        errorDiv.className = 'error-message text-danger mt-1 small';
        errorDiv.textContent = message;
        input.classList.add('is-invalid');
        
        // Insert after the input
        input.parentNode.insertBefore(errorDiv, input.nextSibling);
        
        // Remove error on input
        input.addEventListener('input', function() {
            this.classList.remove('is-invalid');
            if (this.nextElementSibling && this.nextElementSibling.classList.contains('error-message')) {
                this.nextElementSibling.remove();
            }
        }, { once: true });
    }
}); 