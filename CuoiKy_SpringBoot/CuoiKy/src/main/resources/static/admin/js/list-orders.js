document.addEventListener('DOMContentLoaded', function() {
    // Xử lý xóa đơn hàng
    let orderIdToDelete = null;
    const modal = new bootstrap.Modal(document.getElementById('confirmDeleteModal'));
    
    // Mở modal xác nhận xóa
    document.querySelectorAll('.btn-delete').forEach(button => {
        button.addEventListener('click', function() {
            orderIdToDelete = this.getAttribute('data-id');
            document.getElementById('orderIdToDelete').textContent = this.getAttribute('data-orderid');
            modal.show();
        });
    });
    
    // Xử lý khi người dùng xác nhận xóa
    document.getElementById('confirmDeleteBtn').addEventListener('click', function() {
        if (orderIdToDelete) {
            // Gửi yêu cầu xóa
            fetch(`/admin/orders/${orderIdToDelete}/delete`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json'
                }
            })
            .then(response => response.json())
            .then(data => {
                modal.hide();
                if (data.success) {
                    // Xóa thành công
                    window.location.reload();
                } else {
                    // Xóa thất bại
                    alert(`Lỗi: ${data.message}`);
                }
            })
            .catch(error => {
                modal.hide();
                alert(`Lỗi: ${error.message}`);
            });
        }
    });
}); 