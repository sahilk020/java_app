$(document)
		.ready(
				function() {

					$(function() {

						$(document)
								.ready(
										function() {

											$
													.ajax({

														type : "GET",
														// url :
														// domain+"/crmws/tpap/transaction/limit/fetch",
														// todo
														url : "https://uat.pay10.com/crmws/tpap/transaction/limit/fetch",

														data : {

														},
														success : function(
																response) {

															var responseObj = JSON
																	.parse(JSON
																			.stringify(response));
															renderTable(responseObj.data);
														},
													});
										});
					});
				});

function renderTable(data) {
	var getindex = 0;
	var table = new $.fn.dataTable.Api('#dataTable');
	debugger
	var buttonCommon = {
		exportOptions : {
			format : {
				body : function(data, column, row, node) {
					// Strip $ from salary column to make it numeric
					return (column === 1 ? "'" + data : data);
				}
			}
		}
	};

	$("#datatable").DataTable().destroy();

	$('#datatable')
			.dataTable(
					{

						'data' : data,

						'columns' : [

								{

									'data' : 'id',
									'className' : 'displayNone'
								},

								{
									'data' : 'dailyLimit',
									'className' : 'tds text-class'
								},

								{

									'data' : 'weeklyLimit',
									'className' : 'tableId  text-class displayNone'
								},
								{
									'data' : 'monthlyLimit',
									'className' : 'batch_No text-class'
								},

								{
									'data' : 'transactionLimit',
									'className' : 'settlement_Date text-class'
								},

								{
									'data' : 'createdDate',
									'className' : 'tds text-class'
								},
								{
									'data' : 'createdBy',
									'className' : 'tds text-class'
								},

								{
									'data' : 'status',
									'className' : 'tds text-class'
								},
								{
									'data' : 'maxVPATxnLimit',
									'className' : 'tds text-class'
								},
								{
									"data" : null,
									"sClass" : "center",
									"bSortable" : false,
									"mRender" : function() {
										return '<button class="btn  btn-primary"  name="payoutDetails" value="Payout" data-toggle="modal"  data-target="#payoutAccept" onclick = "editDetails(this);">Edit</button>';
									}
								},
								{
									"data" : null,
									"sClass" : "center",
									"bSortable" : false,
									"mRender" : function() {

										return '<button class="btn btn-primary"   name="merchantDelete" id="myBtn" onclick = "delet(this)" >Delete</button>';

									}
								},

								{

									"data" : null,
									"visible" : false,
									"className" : "displayNone",
									"mRender" : function(row) {
										return "\u0027" + row.id;

									}
								}

						]
					});
}

function delet(val) {

	debugger

	var table = $('#datatable').DataTable();
	$('#datatable tbody')
			.on(
					'click',
					'td',
					function() {

						var rows = table.rows();
						var columnVisible = table.cell(this).index().columnVisible;
						if (columnVisible == 10) {
							var rowIndex = table.cell(this).index().row;
							var id = table.cell(rowIndex, 0).data();

							swal(
									{
										title : "Are you sure want to delete this Rule?",
										type : "warning",
										showCancelButton : true,
										confirmButtonColor : "#DD6B55",
										confirmButtonText : "Yes, delete it!",
										closeOnConfirm : false
									},
									function(isConfirm) {
										if (!isConfirm)
											return;

										$
												.ajax({
													type : "DELETE",
													// url :
													// domain+"/crmws/tpap/transaction/limit/inactive/"+id,,
													// todo

													url : "https://uat.pay10.com/crmws/tpap/transaction/limit/inactive/"
															+ id,
													timeout : 0,
													data : {
														"id" : id
													},
													success : function(data) {
														debugger
														swal(
																{
																	title : 'Deleted Successful!',
																	type : "success"
																},
																function() {
																	window.location
																			.reload();
																});
													},
													error : function(data) {

														window.location
																.reload();
													}
												});
									});
						}

					})

}

function editDetails(val) {

	let tableIndex = $(val).parent().index();
	var row = val;

	var cells = val.parentElement.parentElement.cells;

	document.getElementById("DailyLimitsx").value = cells[1].textContent;
	document.getElementById("WeeklyLimitsx").value = cells[2].textContent;
	document.getElementById("MonthlyLimitsx").value = cells[3].textContent;
	document.getElementById("TransactionLimitsx").value = cells[4].textContent;
	document.getElementById("maxTxnVpax").value = cells[8].textContent;

	/*
	 * document.getElementById("id").value = $('.tableId').eq(tableIndex +
	 * 2).html();
	 */
	document.getElementById("id").value = cells[0].textContent;
	openPopUp2();
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
function addRule() {
	var DailyLimits = document.getElementById("DailyLimits").value;
	var WeeklyLimits = document.getElementById("WeeklyLimits").value;
	var MonthlyLimits = document.getElementById("MonthlyLimits").value;
	var TransactionLimits = document.getElementById("TransactionLimits").value;
	var maxTxnVpa = document.getElementById("maxTxnVpa").value;
	debugger

	var param = {
		"dailyLimit" : DailyLimits,
		"weeklyLimit" : WeeklyLimits,
		"monthlyLimit" : MonthlyLimits,
		"transactionLimit" : TransactionLimits,
		"maxVpaTxnLimit" : maxTxnVpa,
	}
	$.ajax({

		type : "POST",
		contentType : "application/json;charset=utf-8",
		// url : domain+"/crmws/tpap/transaction/add", todo

		url : "https://uat.pay10.com/crmws/tpap/transaction/add",

		data : JSON.stringify(param),

		success : function(response) {

			alert(response.message);
			window.location.reload();

		},
	});

}

function addRuleupdate() {
	var DailyLimits = document.getElementById("DailyLimitsx").value;
	var WeeklyLimits = document.getElementById("WeeklyLimitsx").value;
	var MonthlyLimits = document.getElementById("MonthlyLimitsx").value;
	var TransactionLimits = document.getElementById("TransactionLimitsx").value;
	var maxTxnVpa = document.getElementById("maxTxnVpax").value;
	var id = document.getElementById("id").value;

	debugger

	var param = {
		"id" : id,

		"dailyLimit" : DailyLimits,
		"weeklyLimit" : WeeklyLimits,
		"monthlyLimit" : MonthlyLimits,
		"transactionLimit" : TransactionLimits,
		"maxVpaTxnLimit" : maxTxnVpa,
	}
	$.ajax({

		type : "POST",
		contentType : "application/json;charset=utf-8",
		// url : domain+"/crmws/tpap/transaction/add", todo

		url : " https://uat.pay10.com/crmws/tpap/transaction/limit/update",

		data : JSON.stringify(param),

		success : function(response) {

			alert(response.message);
			window.location.reload();

		},
	});

}
