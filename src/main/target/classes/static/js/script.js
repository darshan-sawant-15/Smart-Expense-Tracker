/**
 * 
 */
console.log("script running");

const toggleSideBar = () => {
	if ($(".sidebar").is(":visible")) {
		$(".sidebar").css("display", "none");
		$(".navbar-brand i").css("display", "");
	}
	else {
		$(".sidebar").css("display", "block");
		$(".navbar-brand i").css("display", "none")
	}
}
function deleteItem(id) {
	Swal.fire({
		title: 'Are you sure?',
		text: "You won't be able to revert this!",
		icon: 'warning',
		showCancelButton: true,
		confirmButtonColor: '#3085d6',
		cancelButtonColor: '#d33',
		confirmButtonText: 'Yes, delete it!'
	}).then((result) => {
		if (result.isConfirmed) {
			window.location = "/user/expense/delete-expense/"+id;
		}
	})
}
