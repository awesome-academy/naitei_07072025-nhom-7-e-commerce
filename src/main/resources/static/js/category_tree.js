$(document).ready(function() {
    $('.toggle-children').on('click', function() {
        var icon = $(this);
        var parentRow = icon.closest('tr');
        var parentId = parentRow.data('id');
        $('tr.child-of-' + parentId).toggle();
        icon.toggleClass('fa-plus-square-o fa-minus-square-o');
    });
});
