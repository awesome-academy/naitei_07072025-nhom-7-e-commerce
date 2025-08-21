// ===============================
// Search Product Function
// ===============================
function searchProduct() {
    const query = document.getElementById('searchInput').value.trim();
    const type = document.getElementById('searchType').value;

    if (query === "") {
        alert("Vui lòng nhập từ khóa tìm kiếm.");
        return;
    }

    const isId = !isNaN(query);
    const numericId = parseInt(query);

    if (type === "products") {
        if (isId) {
            window.location.href = `/products/ID/${numericId}`;
        } else {
            window.location.href = `/products/search?name=${encodeURIComponent(query)}`;
        }
    } else if (type === "customers") {
        if (isId) {
            window.location.href = `/customers/ID/${numericId}`;
        } else {
            window.location.href = `/customers/search?name=${encodeURIComponent(query)}`;
        }
    } else if (type === "orders") {
        if (isId) {
            window.location.href = `/orders/ID/${numericId}`;
        } else {
            window.location.href = `/orders/search?name=${encodeURIComponent(query)}`;
        }
    } else if (type === "suppliers") {
        if (isId) {
            window.location.href = `/suppliers/ID/${numericId}`;
        } else {
            window.location.href = `/suppliers/search?name=${encodeURIComponent(query)}`;
        }
    }
}

// ===============================
// Category Search Function
// ===============================
document.addEventListener('DOMContentLoaded', function() {
    const searchButton = document.getElementById('searchButton');
    const categoryInput = document.getElementById('searchInputByCategory');
    const minPriceInput = document.getElementById('searchMinValue');
    const maxPriceInput = document.getElementById('searchMaxValue');

    if (searchButton) {
        searchButton.addEventListener('click', function() {
            const category = categoryInput.value.trim();
            const minPrice = minPriceInput.value.trim();
            const maxPrice = maxPriceInput.value.trim();

            if (category && minPrice && maxPrice) {
                window.location.href = `/admin/searchcatalogprice?category=${encodeURIComponent(category)}&minPrice=${minPrice}&maxPrice=${maxPrice}&showTable=true`;
            } else {
                alert('Vui lòng nhập đầy đủ thông tin');
            }
        });
    }
});

// ===============================
// Confirm Delete Supplier
// ===============================
function confirmDelete(supplierId) {
    $('#deleteModal').modal('show');
    $('#confirmDeleteBtn').off('click').on('click', function() {
        window.location.href = '/suppliers/delete/' + supplierId;
    });
}

// ===============================
// Notification (Success / Error)
// ===============================
$(document).ready(function() {
    // Nếu bạn có gán biến từ Thymeleaf thì cần inline trong template
    var successMessage = typeof successMessageVar !== 'undefined' ? successMessageVar : '';
    var errorMessage = typeof errorMessageVar !== 'undefined' ? errorMessageVar : '';

    if (successMessage) {
        showNotification('success', successMessage);
    }
    if (errorMessage) {
        showNotification('error', errorMessage);
    }

    // ===============================
    // Fix Navbar Layout
    // ===============================
    $('.dropdown-toggle').dropdown();
    $('.navbar-custom-menu .nav').css({
        'display': 'flex',
        'flex-direction': 'row',
        'align-items': 'center'
    });
    $('.navbar-custom-menu .nav > li').css({
        'display': 'inline-block',
        'float': 'left'
    });
});

// ===============================
// Show Notification Function
// ===============================
function showNotification(type, message) {
    // Tùy bạn dùng toastr / sweetalert / custom
    console.log(type + ': ' + message);
}
