$(document).ready(function() {
    // Kích hoạt tooltips
    $('[data-toggle="tooltip"]').tooltip();
    
    // Khởi tạo DataTables
    $('#usersTable').DataTable({
        "paging": false,
        "info": false,
        "searching": false
    });
});

function deleteUser(username) {
    if (confirm('Bạn có chắc chắn muốn xóa người dùng "' + username + '"?')) {
        // Lấy CSRF token từ meta tags
        var token = $("meta[name='_csrf']").attr("content");
        var header = $("meta[name='_csrf_header']").attr("content");

        fetch('/admin/users/' + username + '/delete', {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
                [header]: token
            }
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                window.location.reload();
            } else {
                alert('Có lỗi xảy ra khi xóa người dùng: ' + data.message);
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Có lỗi xảy ra khi xóa người dùng!');
        });
    }
}

// Xuất Excel (cần thêm table2excel.js)
document.getElementById('exportToExcel').addEventListener('click', function() {
    var table2excel = new Table2Excel();
    table2excel.export(document.querySelector('#usersTable'), 'DanhSachNguoiDung');
}); 