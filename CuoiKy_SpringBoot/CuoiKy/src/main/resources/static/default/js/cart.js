// Cart Page JavaScript
document.addEventListener('DOMContentLoaded', function() {
    // Xử lý tăng số lượng
    const increaseButtons = document.querySelectorAll('.increase-btn');
    increaseButtons.forEach(button => {
        button.addEventListener('click', function() {
            const cartId = this.getAttribute('data-id');
            const maxQuantity = parseInt(this.getAttribute('data-max'));
            const inputElement = document.querySelector(`.quantity-input[data-id="${cartId}"]`);
            let currentValue = parseInt(inputElement.value);
            
            if (currentValue < maxQuantity) {
                inputElement.value = currentValue + 1;
                updateCartItem(cartId, currentValue + 1);
            }
        });
    });
    
    // Xử lý giảm số lượng
    const decreaseButtons = document.querySelectorAll('.decrease-btn');
    decreaseButtons.forEach(button => {
        button.addEventListener('click', function() {
            const cartId = this.getAttribute('data-id');
            const inputElement = document.querySelector(`.quantity-input[data-id="${cartId}"]`);
            let currentValue = parseInt(inputElement.value);
            
            if (currentValue > 1) {
                inputElement.value = currentValue - 1;
                updateCartItem(cartId, currentValue - 1);
            } else {
                // Khi số lượng giảm xuống dưới 1, tự động xóa sản phẩm
                removeCartItem(cartId);
            }
        });
    });
    
    // Hàm xóa sản phẩm trong giỏ hàng
    function removeCartItem(cartId) {
        window.location.href = `/shop/cart/remove/${cartId}`;
    }
    
    // Hàm cập nhật số lượng thông qua AJAX
    function updateCartItem(cartId, quantity) {
        fetch(`/shop/cart/update?cartId=${cartId}&quantity=${quantity}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
                'X-Requested-With': 'XMLHttpRequest'
            }
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                // Nếu server báo sản phẩm đã bị xóa
                if (data.removed) {
                    // Tải lại trang để cập nhật giao diện
                    window.location.reload();
                    return;
                }
                
                // Cập nhật lại trang sau khi thành công
                window.location.reload();
            } else {
                alert('Có lỗi xảy ra khi cập nhật giỏ hàng: ' + (data.message || 'Không thể cập nhật số lượng'));
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Có lỗi xảy ra khi cập nhật giỏ hàng');
        });
    }

    // Thêm data-label cho các ô td trong chế độ mobile
    function addDataLabelsToTable() {
        const table = document.querySelector('table');
        if (!table) return;

        const headers = Array.from(table.querySelectorAll('thead th')).map(th => th.textContent.trim());
        const rows = table.querySelectorAll('tbody tr');

        rows.forEach(row => {
            const cells = row.querySelectorAll('td');
            cells.forEach((cell, index) => {
                if (index < headers.length) {
                    cell.setAttribute('data-label', headers[index]);
                }
            });
        });
    }

    // Gọi hàm thêm data-label
    addDataLabelsToTable();

    // Thêm hiệu ứng hover cho nút
    const buttons = document.querySelectorAll('.btn');
    buttons.forEach(button => {
        button.addEventListener('mouseenter', function() {
            this.style.transform = 'translateY(-2px)';
        });
        
        button.addEventListener('mouseleave', function() {
            this.style.transform = 'translateY(0)';
        });
    });

    // Thêm hiệu ứng focus cho input số lượng
    const quantityInputs = document.querySelectorAll('.quantity-input');
    quantityInputs.forEach(input => {
        input.addEventListener('focus', function() {
            this.parentElement.style.boxShadow = '0 0 0 0.25rem rgba(255, 102, 0, 0.25)';
        });
        
        input.addEventListener('blur', function() {
            this.parentElement.style.boxShadow = 'none';
        });
    });
});
