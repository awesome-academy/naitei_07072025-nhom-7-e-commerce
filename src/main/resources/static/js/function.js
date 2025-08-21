(function(){
	$(document).on('click', 'input#check-all', function(event) {
		var check = $(this).is(':checked');
		if (check) {
			$('input.item-check').prop({
				checked: true
			});
		}else{
			$('input.item-check').prop({
				checked: false
			});
		}
	});

}(jQuery));
