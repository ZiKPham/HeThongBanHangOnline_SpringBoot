// List Products Page JavaScript
document.addEventListener('DOMContentLoaded', function() {
    // Tự động submit form khi thay đổi giá trị của các select
    document.getElementById('brandFilter').addEventListener('change', function() {
        document.getElementById('filterForm').submit();
    });
    
    document.getElementById('categoryFilter').addEventListener('change', function() {
        document.getElementById('filterForm').submit();
    });
    
    document.getElementById('sortFilter').addEventListener('change', function() {
        document.getElementById('filterForm').submit();
    });
    
    document.getElementById('ramFilter').addEventListener('change', function() {
        document.getElementById('filterForm').submit();
    });
    
    // Xử lý hiệu ứng hover cho các sản phẩm
    const productCards = document.querySelectorAll('.product-card');
    
    productCards.forEach(card => {
        card.addEventListener('mouseenter', function() {
            this.style.transform = 'translateY(-8px)';
            this.style.boxShadow = '0 .8rem 2rem rgba(0, 0, 0, .2)';
        });
        
        card.addEventListener('mouseleave', function() {
            this.style.transform = 'translateY(0)';
            this.style.boxShadow = '0 .5rem 1.5rem rgba(0, 0, 0, .1)';
        });
    });
    
    // Ngăn chặn sự kiện click của nút thêm vào giỏ hàng lan ra ngoài card
    const addToCartForms = document.querySelectorAll('.product-card form');
    
    addToCartForms.forEach(form => {
        form.addEventListener('click', function(e) {
            e.stopPropagation();
        });
    });
    
    // Thêm hiệu ứng ripple khi click vào sản phẩm
    productCards.forEach(card => {
        card.addEventListener('click', createRippleEffect);
    });
    
    function createRippleEffect(e) {
        const card = this;
        
        const circle = document.createElement('div');
        const diameter = Math.max(card.clientWidth, card.clientHeight);
        const radius = diameter / 2;
        
        circle.style.width = circle.style.height = `${diameter}px`;
        circle.style.left = `${e.clientX - card.getBoundingClientRect().left - radius}px`;
        circle.style.top = `${e.clientY - card.getBoundingClientRect().top - radius}px`;
        circle.classList.add('ripple');
        
        const ripple = card.querySelector('.ripple');
        if (ripple) {
            ripple.remove();
        }
        
        card.appendChild(circle);
    }

    // Hỗ trợ bàn phím cho accessibility
    productCards.forEach(card => {
        card.addEventListener('keydown', function(e) {
            // Nếu phím Enter hoặc Space được nhấn
            if (e.key === 'Enter' || e.key === ' ') {
                e.preventDefault();
                card.click();
            }
        });
    });
}); 