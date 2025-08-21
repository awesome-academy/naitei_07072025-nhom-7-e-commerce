function searchProduct() {
    const query = document.getElementById('searchInput').value.trim();
    const type = document.getElementById('searchType').value;

    if (query === "") {
        alert("Vui lòng nhập từ khóa tìm kiếm.");
        return;
    }

    // Check if query is a number
    const isId = !isNaN(query);
    const numericId = parseInt(query); // Convert to integer

    if (type === "products") {
        if (isId) {
            // If it's an ID, navigate to /products/ID/{id}
            window.location.href = `/products/ID/${numericId}`;
        } else {
            // If it's a product name, navigate to /products/search
            window.location.href = `/products/search?name=${encodeURIComponent(query)}`;
        }
    } else if (type === "customers") {
        if (isId) {
            // If it's an ID, navigate to /customers/ID/{id}
            window.location.href = `/customers/ID/${numericId}`;
        } else {
            // If it's a customer name, navigate to /customers/search
            window.location.href = `/customers/search?name=${encodeURIComponent(query)}`;
        }
    } else if (type === "orders") {
        if (isId) {
            // If it's an ID, navigate to /orders/ID/{id}
            window.location.href = `/orders/ID/${numericId}`;
        } else {
            // If it's a product name, navigate to /orders/search
            window.location.href = `/orders/search?name=${encodeURIComponent(query)}`;
        }
    } else if (type === "suppliers") {
        if (isId) {
            // If it's an ID, navigate to /suppliers/ID/{id}
            window.location.href = `/suppliers/ID/${numericId}`;
        } else {
            // If it's a supplier name, navigate to /suppliers/search
            window.location.href = `/suppliers/search?name=${encodeURIComponent(query)}`;
        }
    }
}

// Enter key support for search
document.addEventListener('DOMContentLoaded', function () {
    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        searchInput.addEventListener('keypress', function (event) {
            if (event.key === 'Enter') {
                searchProduct();
            }
        });
    }
});
