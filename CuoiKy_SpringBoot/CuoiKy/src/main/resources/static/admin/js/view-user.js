function deleteUser(username) {
    if (confirm('Bạn có chắc chắn muốn xóa người dùng "' + username + '"?\nHành động này không thể hoàn tác.')) {
        // Lấy CSRF token từ meta tags
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");
        
        // Hiển thị trạng thái loading
        var deleteBtn = $('[onclick*="deleteUser(\'' + username + '\')"]');
        var originalHtml = deleteBtn.html();
        deleteBtn.html('<i class="fas fa-spinner fa-spin"></i> Đang xử lý...');
        deleteBtn.prop('disabled', true);

        fetch('/admin/users/' + username + '/delete', {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
                [header]: token
            }
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Lỗi mạng - Mã trạng thái: ' + response.status);
            }
            return response.json();
        })
        .then(data => {
            if (data.success) {
                // Hiển thị thông báo thành công
                showAlert('success', 'Đã xóa người dùng thành công!');
                
                // Chuyển hướng sau 1.5 giây
                setTimeout(function() {
                    window.location.href = '/admin/users?success=Đã xóa người dùng thành công';
                }, 1500);
            } else {
                // Hiển thị thông báo lỗi
                showAlert('danger', 'Lỗi: ' + data.message);
                deleteBtn.html(originalHtml);
                deleteBtn.prop('disabled', false);
            }
        })
        .catch(error => {
            console.error('Error:', error);
            showAlert('danger', 'Có lỗi xảy ra khi xóa người dùng: ' + error.message);
            deleteBtn.html(originalHtml);
            deleteBtn.prop('disabled', false);
        });
    }
}

// Hàm hiển thị thông báo
function showAlert(type, message) {
    // Xóa thông báo cũ nếu có
    $('.alert').alert('close');
    
    // Tạo thông báo mới
    var alertHtml = 
        '<div class="alert alert-' + type + ' alert-dismissible fade show" role="alert">' +
            '<i class="fas fa-' + (type === 'success' ? 'check-circle' : 'exclamation-circle') + '"></i> ' +
            '<span>' + message + '</span>' +
            '<button type="button" class="close" data-dismiss="alert" aria-label="Close">' +
                '<span aria-hidden="true">&times;</span>' +
            '</button>' +
        '</div>';
    
    // Chèn thông báo vào đầu phần nội dung
    $(alertHtml).insertAfter('.d-sm-flex');
} 