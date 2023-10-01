/**
 *
 */
console.log("script running");

const toggleSideBar = () => {
	if ($(".sidebar").is(":visible")) {
		if ($(window).width() > 767) {
			$(".dashboard").css("margin-left", "1%");
		}

		$(".sidebar").css("display", "none");
		$(".navbar-brand i").css("display", "");
	} else {
		if ($(window).width() > 767) {
			$(".dashboard").css("margin-left", "20%");
		}

		$(".sidebar").css("display", "block");
		$(".navbar-brand span i").css("display", "none");
	}
};

const closeSidebar = () => {
	if ($(".sidebar").is(":visible")) {
		$(".sidebar").css("display", "none");
		$(".navbar-brand i").css("display", "");
		$(".dashboard").css("margin-left", "1%");
	}
};

function deleteItem(path, id, currentPage) {
	Swal.fire({
		text: "Are you sure you want to delete this?",
		icon: "warning",
		showCancelButton: true,
		confirmButtonColor: "#3085d6",
		cancelButtonColor: "#d33",
		confirmButtonText: "Yes, delete it!",
	}).then((result) => {
		if (result.isConfirmed) {
			if (currentPage >= 0) {
				window.location = path + id + "/" + currentPage;
			} else {
				window.location = path + id;
			}
		}
	});
}

const enableEditing = (checkbox) => {
	console.log("Yeahhh");
	const amount = document.getElementById("amount");
	const setBtn = document.getElementById("setBtn");
	const editableGoal = document.getElementById("editable-goal");
	const nonEditableGoal = document.getElementById("non-editable-goal");
	if (!checkbox.checked) {
		amount.readOnly = true;
		setBtn.disabled = true;
		editableGoal.style.display = "none";
		nonEditableGoal.style.display = "";
	} else {
		amount.readOnly = false;
		setBtn.disabled = false;
		editableGoal.style.display = "";
		nonEditableGoal.style.display = "none";
	}
};

function searchExpenses(currentPage) {
	const query = $("#search-input").val();
	const month = $("#month").val();
	console.log(query + " " + month);
	const url = `http://localhost:8080/user/expense/search-expense/${query}/${month}`;
	if (query === "") {
		$(".search-result").hide();
	} else {
		fetch(url)
			.then((expenses) => {
				return expenses.json();
			})
			.then((expenses) => {
				console.log(expenses);
				var text = `<div class="list-group">`;
				expenses.forEach((element) => {
					text += `<a href='/user/expense/view-expense/${element.expId}/${currentPage}' class="list-group-item list-group-item-action">${element.description}</a>`;
				});
				text += `</div>`;
				console.log(text);
				$(".search-result").html(text);
				$(".search-result").show();
			});
	}
}
