function searchProduct() {
    const searchType = document.getElementById('searchType').value;
    const searchInput = document.getElementById('searchInput').value.trim();

    if (!searchInput) {
        alert('Vui lòng nhập từ khóa tìm kiếm!');
        return;
    }

    // Redirect to appropriate search page based on type
    window.location.href = `/${searchType}?search=${encodeURIComponent(searchInput)}`;
}

// Allow Enter key to trigger search
document.addEventListener('DOMContentLoaded', function () {
    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        searchInput.addEventListener('keypress', function (e) {
            if (e.key === 'Enter') {
                searchProduct();
            }
        });
    }
});

function loadDashboardStats() {
    // Implement AJAX calls to get real statistics
    // Example:
    /*
    fetch('/api/dashboard/stats')
        .then(response => response.json())
        .then(data => {
            document.getElementById('totalCustomers').textContent = data.customers || 0;
            document.getElementById('totalProducts').textContent = data.products || 0;
            document.getElementById('totalOrders').textContent = data.orders || 0;
            document.getElementById('totalSuppliers').textContent = data.suppliers || 0;
        });
    */
}
