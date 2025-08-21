document.addEventListener("DOMContentLoaded", function () {
    const searchInput = document.getElementById("productSearch");
    const table = document.getElementById("productTable");
    const rows = table.getElementsByTagName("tbody")[0].getElementsByTagName("tr");

    if (searchInput) {
        searchInput.addEventListener("keyup", function () {
            const filter = searchInput.value.toLowerCase();

            for (let i = 0; i < rows.length; i++) {
                let cells = rows[i].getElementsByTagName("td");
                let match = false;

                for (let j = 0; j < cells.length; j++) {
                    if (cells[j].textContent.toLowerCase().includes(filter)) {
                        match = true;
                        break;
                    }
                }

                rows[i].style.display = match ? "" : "none";
            }
        });
    }
});

function changeSize(size) {
    // lấy biến Thymeleaf từ window
    var sortField = window.sortField;
    var sortDirection = window.sortDirection;

    let url = "/admin/products?page=0&size=" + size;
    if (sortField && sortDirection) {
        url += "&sortField=" + sortField + "&sortDirection=" + sortDirection;
    }
    window.location.href = url;
}

window.changeSize = changeSize;
