$(document).ready(function () {

	$(function () {

		$(document).ready(function () {
			var urls = new URL(window.location.href);
			var domain = urls.origin;

			$.ajax({

				type: "GET",
				// url :
				// "https://uat.pay10.com/crmws/acquirer/downtime/configuration/list",
				url: domain + "/crmws/acquirer/downtime/configuration/list",

				data: {

				},
				success: function (response) {

					var responseObj = JSON.parse(JSON.stringify(response));
					renderTable(responseObj.data);
				},
			});
		});
	});
});

// function searchdata(){
//
// var acquirer = document.getElementById("acquirer").value;
// var paymentType = document.getElementById("paymentMethod").value;
// $.ajax({
//
// type: "GET",
// url: "serachDetails",
//
// data: {
//
// "acquirer":acquirer,
// "paymentType":paymentType,
// },
// success: function(response) {
// var responseObj = JSON.parse(JSON.stringify(response));
// renderTable(responseObj.searchSchadular);
// },
// });
// }
function save() {
	var acquirerName = document.getElementById("acquirerso").value;
	var paymentType = document.getElementById("PaymentTypeso").value;
	var failedCount = document.getElementById("failedCountso").value;
	debugger
	var timeSlab = document.getElementById("timeSlabso").value;

	if (acquirerName == "") {
		if (acquirerName == "") {
			alert('Please Select Acquirer !')
			return false;

		}
	}

	if (paymentType == "") {
		if (paymentType == "") {
			alert('Please Select paymentType !')
			return false;

		}
	} else {
		if (failedCount == "" || timeSlab == "") {
			alert('Enter data value !');
			return false;
		}

	}

	var param = {
		'acquirerName': acquirerName,
		'paymentType': paymentType,
		'failedCount': failedCount,
		'timeSlab': timeSlab
	};

	var urls = new URL(window.location.href);
	var domain = urls.origin;

	$.ajax({

		type: "GET",
		url: domain + "/crmws/acquirer/downtime/configuration/add",
		dataType: "json",
		contentType: "application/json;charset=utf-8",
		type: "POST",
		data: JSON.stringify(param),
		success: function (data) {
			closePopup();
			var response = data.message;
			swal({
				title: response,
				type: "success"
			}, function () {
				window.location.reload();
			});

		},
		error: function (data) {
			window.location.reload();
		}
	});
}

function renderTable(data) {
	var getindex = 0;
	var table = new $.fn.dataTable.Api('#dataTable');

	debugger
	var buttonCommon = {
		exportOptions: {
			format: {
				body: function (data, column, row, node) {
					// Strip $ from salary column to make it numeric
					return (column === 1 ? "'" + data : data);
				}
			}
		}
	};

	$("#datatable").DataTable().clear().destroy();

	$('#datatable')
		.dataTable(
			{

				'data': data,

				'columns': [

					{

						'data': 'id',
						'className': 'tableId  text-class displayNone'
					},
					{
						'data': 'acquirerName',
						'className': 'batch_No text-class'
					},

					{
						'data': 'paymentType',
						'className': 'settlement_Date text-class'
					},

					{
						'data': 'failedCount',
						'className': 'tds text-class'
					},
					{
						'data': 'timeSlab',
						'className': 'tds text-class'
					},

					{
						"data": null,
						"sClass": "center",
						"bSortable": false,
						"mRender": function () {
							return '<button class="btn  btn-primary"  name="payoutDetails" value="Payout" data-toggle="modal"  data-target="#payoutAccept" onclick = "editDetails(this);">Edit</button>';
						}
					},
					{
						"data": null,
						"sClass": "center",
						"bSortable": false,
						"mRender": function () {

							return '<button class="btn btn-primary"   name="merchantDelete" id="myBtn" onclick = "delet(this)" >Delete</button>';

						}
					}, {

						"data": null,
						"visible": false,
						"className": "displayNone",
						"mRender": function (row) {
							return "\u0027" + row.id;

						}
					}

				]
			});
}

function delet(val) {

	var table = $('#datatable').DataTable();
	$('#datatable tbody')
		.on(
			'click',
			'td',
			function () {
				debugger
				var rows = table.rows();
				var columnVisible = table.cell(this).index().columnVisible;
				if (columnVisible == 6) {
					var rowIndex = table.cell(this).index().row;
					var id = table.cell(rowIndex, 0).data();

					swal(
						{
							title: "Are you sure want to delete this Rule?",
							type: "warning",
							showCancelButton: true,
							confirmButtonColor: "#DD6B55",
							confirmButtonText: "Yes, delete it!",
							closeOnConfirm: false
						},
						function (isConfirm) {
							var urls = new URL(window.location.href);
							var domain = urls.origin;

							if (!isConfirm)
								return;
							$
								.ajax({
									type: "Delete",
									url: domain + "/crmws/acquirer/downtime/configuration/inactive/" + id,
									timeout: 0,
									data: {},

									success: function (data) {
										var response = data.response;
										swal(
											{
												title: 'Deleted Successful!',
												type: "success"
											},
											function () {
												window.location
													.reload();
											});
									},
									error: function (data) {
										window.location
											.reload();
									}
								});
						});
				}

			})

}

function save1() {

	var acquirerName = document.getElementById("acquirer1").value;
	var paymentType = document.getElementById("paymentType1").value;
	var failedCount = document.getElementById("failedCount1").value;

	var timeSlab = document.getElementById("timeSlab1").value;
	var id = document.getElementById("id1").value;

	debugger

	if (acquirerName == "" || paymentType == "" || failedCount == ""
		|| timeSlab == "") {
		alert('Enter date value');
		return false;
	}

	var param = {
		'acquirerName': acquirerName,
		'paymentType': paymentType,
		'failedCount': failedCount,
		'timeSlab': timeSlab,
		'id': id
	};

	var urls = new URL(window.location.href);
	var domain = urls.origin;
	$.ajax({

		type: "GET",
		url: domain + "/crmws/acquirer/downtime/configuration/update",
		dataType: "json",
		contentType: "application/json;charset=utf-8",
		type: "POST",
		data: JSON.stringify(param),
		success: function (data) {

			var response = data.message;
			swal({
				title: response,
				type: "success"
			}, function () {
				window.location.reload();
			});

		},
		error: function (data) {
			window.location.reload();
		}
	});
}

function getListpayment() {

	var acquirer = document.getElementById('acquirersc').value;
	if (acquirer == "") { } else {
		var urls = new URL(window.location.href);
		var domain = urls.origin;
		debugger
		$.ajax({
			type: "POST",
			url: domain + "/crmws/GlobalAcquirerSwitch/getGlobalPaymentType",
			timeout: 0,
			data: {

				"acquirer": acquirer,

			},

			success: function (data) {
				var acquirer = JSON.stringify(data.data);
				var acquirerList = JSON.parse(acquirer);
				//var s = '<option value="">Select acquirer</option>';
				var s = '<option value="">Select the Payment type </option>';
				for (var i = 0; i < acquirerList.length; i++) {

					s += '<option value="' + acquirerList[i].code + '">' + acquirerList[i].name
						+ '</option>';
				}

				$("#PaymentTypese").html(s);
			}
		});
	}
}

function getListpayment1() {

	var acquirer = document.getElementById('acquirerso').value;
	if (acquirer == "") { } else {
		var urls = new URL(window.location.href);
		var domain = urls.origin;
		debugger
		$.ajax({
			type: "POST",
			url: domain + "/crmws/GlobalAcquirerSwitch/getGlobalPaymentType",
			timeout: 0,
			data: {

				"acquirer": acquirer,

			},

			success: function (data) {
				var acquirer = JSON.stringify(data.data);
				var acquirerList = JSON.parse(acquirer);
				//var s = '<option value="">Select acquirer</option>';
				var s = '<option value="">Select the Payment type </option>';
				for (var i = 0; i < acquirerList.length; i++) {

					s += '<option value="' + acquirerList[i].code + '">' + acquirerList[i].name
						+ '</option>';
				}

				$("#PaymentTypeso").html(s);
			}
		});
	}
}

function searchdata() {

	var acquirerName = document.getElementById("acquirersc").value;
	var paymentType = document.getElementById("PaymentTypese").value;
	if (acquirerName == "" || acquirerName == null) {
		alert("Please select Acquirer Name");
		return false;
	}
	if (paymentType == "" || paymentType == null) {
		alert("Please select Payment Type");
		return false;
	}
	if (paymentType == "ALL") {
		paymentType = "";
	}
	var urls = new URL(window.location.href);
	var domain = urls.origin;

	$.ajax({

		type: "POST",
		url: domain + "/crmws/acquirer/downtime/configuration/listSearch",

		data: {

			"payment": paymentType,
			"acquirer": acquirerName,
		},
		success: function (response) {
			var responseObj = JSON.parse(JSON.stringify(response));
			renderTable(responseObj.data);
		},
	});
}

function openPopUp() {
	$('#data-add').show();
}

function openPopUp2() {
	$('#payoutAccept').show();
}

function closePopup() {

	$('#data-add').hide();

}
function closePopup1() {

	$('#payoutAccept').hide();
}

function editDetails(val) {

	let tableIndex = $(val).parent().index();
	var row = val;

	var cells = val.parentElement.parentElement.cells;

	/*
	 * document.getElementById("acquirer1").value = $('.batch_No').eq(tableIndex +
	 * 2).html(); document.getElementById("Maxtime1").value =
	 * $('.settlement_Date').eq(tableIndex + 2).html();
	 * document.getElementById("Mintime1").value = $('.tds').eq(tableIndex +
	 * 2).html();
	 */

	document.getElementById("acquirer1").value = cells[1].textContent;
	document.getElementById("failedCount1").value = cells[3].textContent;
	document.getElementById("timeSlab1").value = cells[4].textContent;
	document.getElementById("paymentType1").value = cells[2].textContent;

	/*
	 * document.getElementById("id").value = $('.tableId').eq(tableIndex +
	 * 2).html();
	 */
	document.getElementById("id1").value = cells[0].textContent;
	openPopUp2();
}
