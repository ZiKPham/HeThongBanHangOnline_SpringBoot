// Form validation
document.addEventListener('DOMContentLoaded', function() {
    'use strict'
    
    // Fetch all the forms we want to apply custom Bootstrap validation styles to
    const forms = document.querySelectorAll('.needs-validation')
    
    // Loop over them and prevent submission
    forms.forEach(form => {
        const inputs = form.querySelectorAll('input[required]')
        
        // Xử lý hiển thị/ẩn thông báo lỗi
        const showError = (input, show) => {
            const errorElement = document.getElementById(input.id + '-error')
            if (errorElement) {
                if (show) {
                    errorElement.classList.add('error')
                    input.classList.add('is-invalid')
                    input.classList.remove('is-valid')
                } else {
                    errorElement.classList.remove('error')
                    input.classList.remove('is-invalid')
                    if (input.value.trim()) {
                        input.classList.add('is-valid')
                    }
                }
            }
        }

        // Xử lý sự kiện blur cho các input
        inputs.forEach(input => {
            input.addEventListener('blur', function() {
                showError(this, !this.value.trim())
            })

            input.addEventListener('input', function() {
                if (this.value.trim()) {
                    showError(this, false)
                }
            })
        })

        // Validation cho form đổi mật khẩu
        if (form.getAttribute('action') === '/account/update-password') {
            const currentPassword = form.querySelector('#currentPassword')
            const newPassword = form.querySelector('#newPassword')
            const confirmPassword = form.querySelector('#confirmPassword')

            // Kiểm tra mật khẩu hiện tại
            if (currentPassword) {
                currentPassword.addEventListener('input', function() {
                    showError(this, !this.value.trim())
                })
            }

            // Kiểm tra độ dài mật khẩu mới
            if (newPassword) {
                newPassword.addEventListener('input', function() {
                    const isValid = this.value.length >= 6
                    showError(this, !isValid)
                })
            }

            // Kiểm tra mật khẩu xác nhận
            if (confirmPassword && newPassword) {
                const validateConfirmPassword = () => {
                    const isValid = confirmPassword.value === newPassword.value && confirmPassword.value.trim() !== ''
                    showError(confirmPassword, !isValid)
                }

                confirmPassword.addEventListener('input', validateConfirmPassword)
                newPassword.addEventListener('input', validateConfirmPassword)
            }
        }

        // Xử lý submit form
        form.addEventListener('submit', async function(event) {
            event.preventDefault()
            
            // Kiểm tra các trường required
            let isValid = true
            inputs.forEach(input => {
                if (!input.value.trim()) {
                    showError(input, true)
                    isValid = false
                }
            })

            // Kiểm tra đặc biệt cho form đổi mật khẩu
            if (form.getAttribute('action') === '/account/update-password') {
                const newPassword = form.querySelector('#newPassword')
                const confirmPassword = form.querySelector('#confirmPassword')

                if (newPassword && newPassword.value.length < 6) {
                    showError(newPassword, true)
                    isValid = false
                }

                if (confirmPassword && confirmPassword.value !== newPassword.value) {
                    showError(confirmPassword, true)
                    isValid = false
                }
            }

            if (!isValid) {
                return
            }

            try {
                const formData = new FormData(form)
                const response = await fetch(form.action, {
                    method: 'POST',
                    body: formData
                })

                if (response.ok) {
                    showNotification('Thao tác thành công!', 'success')
                    if (form.getAttribute('action') === '/account/update-password') {
                        form.reset()
                        inputs.forEach(input => {
                            input.classList.remove('is-valid', 'is-invalid')
                            showError(input, false)
                        })
                    }
                } else {
                    const data = await response.json()
                    showNotification(data.message || 'Có lỗi xảy ra, vui lòng thử lại!', 'danger')
                }
            } catch (error) {
                showNotification('Có lỗi xảy ra, vui lòng thử lại sau!', 'danger')
            }
        })
    })
    
    // Toggle password visibility
    document.querySelectorAll('.toggle-password').forEach(button => {
        button.addEventListener('click', function() {
            const targetId = this.getAttribute('data-target')
            const passwordInput = document.getElementById(targetId)
            const icon = this.querySelector('i')
            
            if (passwordInput.type === 'password') {
                passwordInput.type = 'text'
                icon.classList.remove('fa-eye')
                icon.classList.add('fa-eye-slash')
            } else {
                passwordInput.type = 'password'
                icon.classList.remove('fa-eye-slash')
                icon.classList.add('fa-eye')
            }
        })
    })

    // Xử lý đóng thông báo tự động sau 5 giây
    const alerts = document.querySelectorAll('.alert')
    alerts.forEach(alert => {
        setTimeout(() => {
            const closeButton = alert.querySelector('.btn-close')
            if (closeButton) {
                closeButton.click()
            }
        }, 5000)
    })
})

// Thêm animation cho thông báo
const showNotification = (message, type = 'success') => {
    const notification = document.createElement('div')
    notification.className = `alert alert-${type} alert-dismissible fade show position-fixed top-0 end-0 m-3`
    notification.style.zIndex = '1050'
    notification.innerHTML = `
        <span>${message}</span>
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    `

    document.body.appendChild(notification)

    // Tự động đóng sau 5 giây
    setTimeout(() => {
        notification.querySelector('.btn-close').click()
    }, 5000)
} 