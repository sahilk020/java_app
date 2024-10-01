$(document)
		.ready(
				function() {
					//document.getElementById("loadingInner").style.display = "none";

					$(function() {

						var fromDate = new Date();
						fromDate.setMonth(fromDate.getMonth() - 6);
						fromDate.setDate(fromDate.getDate() - 6);

						$("#dateFrom").flatpickr({
							minDate : fromDate,
							maxDate : new Date(),
							dateFormat : 'Y-m-d',
							defaultDate : "today"

						});
						$("#dateTo").flatpickr({
							minDate : fromDate,
							maxDate : new Date(),
							dateFormat : 'Y-m-d',
							defaultDate : "today"
						});
					});

					$(function() {
						var today = new Date();
						var fromDate = new Date();
						fromDate.setMonth(fromDate.getMonth() - 1);
						fromDate.setDate(fromDate.getDate() - 1);
						//$('#dateTo').val($.datepicker.formatDate('yy-mm-dd', today));
						//$('#dateFrom').val($.datepicker.formatDate('yy-mm-dd', fromDate));

						$(document)
								.ready(
										function() {
											var resellerpayId = document
													.getElementById("selectReseller").value;
											var fromDate = document
													.getElementById("dateFrom").value;
											var toDate = document
													.getElementById("dateTo").value;
											var userType = document
													.getElementById("userType").value;
											var currency = document
													.getElementById("currency").value;
											var user=document.getElementById("userGroup").value;
												

											$
													.ajax({

														type : "POST",
														url : "resellerPayoutDetails",

														data : {

															"resellerId" : resellerpayId,
															"fromDate" : fromDate,
															"toDate" : toDate,
															"userType" : userType,
															"currency" : currency,
														},
														success : function(
																response) {
															debugger
															var responseObj = JSON
																	.parse(JSON
																			.stringify(response));

															if (user == 'SMA'
																	|| user == 'MA'
																	|| user == 'Agent') {
																renderResellerTable(responseObj.resellerPayout);

															} else {
																renderTable(responseObj.resellerPayout);
															}
														},
													});
										});
					});
				});

function search() {

	var resellerpayId = document.getElementById("selectReseller").value;
	var fromDate = document.getElementById("dateFrom").value;
	var toDate = document.getElementById("dateTo").value;
	var userType = document.getElementById("userType").value;
	var currency = document.getElementById("currency").value;
	var user=document.getElementById("userGroup").value;

	if (userType == '' || userType == null  || userType=="Select User Type") {
		alert("Please Select User Type");
		return false;
	}
	if (resellerpayId == '' || resellerpayId == null) {
		alert("Please Select Reseller");
		return false;
	}

	if (currency == '' || currency == null) {
		alert("Please Select Currency");
		return false;
	}

	$.ajax({

		type : "GET",
		url : "resellerPayoutDetails",

		data : {

			"resellerId" : resellerpayId,
			"fromDate" : fromDate,
			"toDate" : toDate,
			"userType" : userType,
			"currency" : currency,
		},
		success : function(response) {
			var responseObj = JSON.parse(JSON.stringify(response));
			if (user == 'SMA' || user == 'MA' || user == 'Agent') {
				renderResellerTable(responseObj.resellerPayout);

			} else {
				renderTable(responseObj.resellerPayout);
			}

		}
	});
}

function renderTable(data) {
	var getindex = 0;
	var table = new $.fn.dataTable.Api('#dataTable');
	var transFrom = document.getElementById("dateFrom").value;
	var transTo = document.getElementById("dateTo").value;

	if (transFrom == null || transTo == null) {
		alert('Enter date value');
		return false;
	}

	if (transFrom > transTo) {
		alert('From date must be before the to date');
		$('#loader-wrapper').hide();
		$('#dateFrom').focus();
		return false;
	}

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

						dom : 'BTrftlpi',
						buttons : [ $.extend(true, {}, buttonCommon, {
							extend : 'copyHtml5',
							exportOptions : {
								columns : [ 0, 2, 3, 4, 5, 6, 7, 8 ]
							},
						}), $.extend(true, {}, buttonCommon, {
							extend : 'csvHtml5',
							title : 'Reseller Payout',
							exportOptions : {
								columns : [ 0, 2, 3, 4, 5, 6, 7, 8 ],
							},
						}), {
							extend : 'pdfHtml5',
							orientation : 'landscape',
							pageSize : 'legal',
							//footer : true,
							title : 'Reseller Payout',
							exportOptions : {
								columns : [ 0, 2, 3, 4, 5, 6, 7, 8 ]
							},
							customize : function(doc) {
								doc.defaultStyle.alignment = 'center';
								doc.styles.tableHeader.alignment = 'center';
							}
						},

						{
							extend : 'colvis',
							columns : [ 0, 2, 3, 4, 5, 6, 7, 8 ]
						} ],

						'columns' : [
								{
									'data' : 'payoutId',
									'className' : 'batch_No text-class'
								},
								{
									'data' : 'id',
									'visible' : false,
									'className' : 'displayNone'
								},
								{
									'data' : 'resellerId',
									'visible' : false,
									'className' : 'reseller_Id text-class'

								},
								{
									'data' : 'resellerName',
								//'className': 'reseller_Id text-class'
								},

								{
									'data' : 'settlementDate',
									'className' : 'settlement_Date text-class'
								},

								{
									'data' : 'tds',
									'className' : 'tds text-class'
								},

								{
									'data' : 'totalCommission',
									'className' : 'total_Commission text-class'
								},
								{
									'data' : 'totalamount',
									'className' : 'total_Amount text-class'
								},

								{
									'data' : 'utrNo',
									'className' : 'utr_No text-class'
								},

								{
									'data' : 'status',

								},

								{
                                    'data' : 'creationDate',

                                },

								{
									"mData" : null,
									"sClass" : "center",
									render : function(data) {
										var status = data.status == "Settled";
										var classforNotify = status ? "btn btn-sm btn-primary disabled"
												: "btn btn-sm btn-primary";
										var notify = '<a data-placement="top" data-original-title="Edit" name="payoutDetails" value="Payout" data-toggle="modal" data-target="#payoutAccept"   class="'
												+ classforNotify
												+ '" onclick=\'editDetails(this,'
												+ data.resellerId
												+ ')\'>Payout</a>';

										return notify;

									}
								} ]
					});
}
function renderResellerTable(data) {
	var getindex = 0;
	var table = new $.fn.dataTable.Api('#rDatatable');
	var transFrom = document.getElementById("dateFrom").value;
	var transTo = document.getElementById("dateTo").value;

	if (transFrom == null || transTo == null) {
		alert('Enter date value');
		return false;
	}

	if (transFrom > transTo) {
		alert('From date must be before the to date');
		$('#loader-wrapper').hide();
		$('#dateFrom').focus();
		return false;
	}

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

	$("#rDatatable").DataTable().destroy();

	console.log(data);
	$('#rDatatable').dataTable({

		'data' : data,

		dom : 'BTrftlpi',
		buttons : [ $.extend(true, {}, buttonCommon, {
			extend : 'copyHtml5',
			exportOptions : {
				columns : [ 0, 2, 3, 4, 5, 6 ]
			},
		}), $.extend(true, {}, buttonCommon, {
			extend : 'csvHtml5',
			title : 'Reseller Payout',
			exportOptions : {
				columns : [ 0, 2, 3, 4, 5, 6 ],
			},
		}), {
			extend : 'pdfHtml5',
			orientation : 'landscape',
			pageSize : 'legal',
			//footer : true,
			title : 'Reseller Payout',
			exportOptions : {
				columns : [ 0, 2, 3, 4, 5, 6 ]
			},
			customize : function(doc) {
				doc.defaultStyle.alignment = 'center';
				doc.styles.tableHeader.alignment = 'center';
			}
		},

		{
			extend : 'colvis',
			columns : [ 0, 2, 3, 4, 5, 6 ]
		} ],

		'columns' : [ {
			'data' : 'payoutId',
			'className' : 'batch_No text-class'
		}, {
			'data' : 'id',
			'visible' : false,
			'className' : 'displayNone'
		}, {
			'data' : 'resellerId',
			'visible' : false,
			'className' : 'reseller_Id text-class'

		}, {
			'data' : 'resellerName',
		},

		{
			'data' : 'settlementDate',
			'className' : 'settlement_Date text-class'
		},

		{
			'data' : 'tds',
			'className' : 'tds text-class'
		},

		{
			'data' : 'totalCommission',
			'className' : 'total_Commission text-class'
		}, {
			'data' : 'totalamount',
			'className' : 'total_Amount text-class'
		},

		{
			'data' : 'utrNo',
			'className' : 'utr_No text-class'
		},

		{
			'data' : 'status',

		},
        {
            'data' : 'creationDate',

        }]
	});
}

function editDetails(val, idreseller) {
	$('#loadingInner').css('display', 'none');
	debugger
	let tableIndex = $(val).parent().parent().index() - 1;

	var row = val;

	//alert(JSON.stringify(row));
	var cells = val.parentElement.parentElement.cells;
	document.getElementById("batchNo").value = $('.batch_No')
			.eq(tableIndex + 2).html();
	document.getElementById("resellerId").value = idreseller;
	let settlementDate = $('.settlement_Date').eq(tableIndex + 2).html();
	if (settlementDate == undefined || settlementDate == null
			|| settlementDate == '') {
		var today = new Date();
		var dd = String(today.getDate()).padStart(2, '0');
		var mm = String(today.getMonth() + 1).padStart(2, '0'); //January is 0!
		var yyyy = today.getFullYear();

		today = yyyy + '-' + mm + '-' + dd;

		settlementDate = today;
		//settlementDate = $.datepicker.flatpicker('Y-m-d', date);
	}
	document.getElementById("settlementDate").value = settlementDate;
	document.getElementById("tds").value = $('.tds').eq(tableIndex + 2).html();
	document.getElementById("totalCommission").value = $('.total_Commission')
			.eq(tableIndex + 2).html();
	document.getElementById("utrNo").value = $('.utr_No').eq(tableIndex + 2)
			.html();
	$('#payoutAccept').modal('show');
}

function hideModal() {
	$('#payoutAccept').modal('hide');
}

function updatePayout(operation) {

	var batchNo = document.getElementById("batchNo").value;
	var resellerId = document.getElementById("resellerId").value;
	var settlementDate = document.getElementById("settlementDate").value;
	var totalCommission = document.getElementById("totalCommission").value;
	var tds = parseFloatWithDecimal(document.getElementById("tds").value, 2);
	var utrNo = document.getElementById("utrNo").value;

	if (utrNo < 0) {

		alert("Please enter correct utrNo!");
		return false;
	}

	if (tds < 0) {
		alert("Enter tds greater than or equal to zero!");
		return false;
	}

	document.getElementById("btnPayoutConf").disabled = true;

	$.ajax({
		type : "POST",
		url : "updatePayoutDetails",
		timeout : 0,
		data : {
			"batchNo" : batchNo,
			"resellerId" : resellerId,
			"settlementDate" : settlementDate,
			"totalCommission" : totalCommission,
			"utrNo" : utrNo,
			"tds" : tds
		},
		success : function(data) {

			document.getElementById("loader").style.display = "block";
			alert("Payout updated successfully");
			window.location.reload();
		},
		error : function(data) {
			//document.getElementById("loadingInner").style.display = "none";
			document.getElementById("btnPayoutConf").disabled = false;
			alert("Unable to update payout");
		}

	});
}

function reloadTable() {

	var datepick = $.datepicker;
	var transFrom = $.datepicker.parseDate('yy-mm-dd', $('#dateFrom').val());
	var transTo = $.datepicker.parseDate('yy-mm-dd', $('#dateTo').val());
	if (transFrom == null || transTo == null) {
		alert('Enter date value');
		return false;
	}

	if (transFrom > transTo) {
		alert('From date must be before the to date');
		$('#loader-wrapper').hide();
		$('#dateFrom').focus();
		return false;
	}

	/* $("#submit").attr("disabled", true); */

	var tableObj;
	var table;
	if (userType == 'SMA' || userType == 'MA' || userType == 'Agent') {
		tableObj = $('#rDatatable');
		table = tableObj.DataTable();
		

	} else {
		tableObj = $('#dataTable');
		table = tableObj.DataTable();
	}
	table.ajax.reload();
}

function isPositiveNumber(evt) {
	var charCode = (evt.which) ? evt.which : event.keyCode
	if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46) {
		return false;
	}
	return true;
}

function parseFloatWithDecimal(str, val) {
	str = str.toString();
	if (str.includes(".")) {
		str = str.slice(0, (str.indexOf(".")) + val + 1);
	}
	return Number(str);
}

function getResellerList(userType) {

	$.ajax({

		type : "GET",
		url : "getResellerPayoutListByuserType",
		timeout : 0,
		data : {

			"userType" : userType,

		},
		success : function(data, status) {
			// var response= JSON.stringify(data);
			var s = '<option value="">Select Reseller</option>';
			// alert(s)
			for (var i = 0; i < data.resellerarreList.length; i++) {
				s += '<option value="' + data.resellerarreList[i] + '">'
						+ data.resellerarrename[i] + '</option>';
				// console.log(s)
				// alert(s)
			}
			document.getElementById("resellers").style.display = "block";

			$("#selectReseller").html(s);

		}
	});
}

function getCurrencyList() {

	var userType = document.getElementById("userType").value;
	var resellerId = document.getElementById("selectReseller").value;

	$
			.ajax({

				type : "GET",
				url : "getUserPayoutCurrencyList",
				timeout : 0,
				data : {
					"userType" : userType,
					"resellerId" : resellerId,

				},

				success : function(data) {
					var s = '<option value="">Select Currency</option>';
					for (var i = 0; i < data.currencyarrename.length; i++) {
						s += '<option value="' + data.currencyCodearreList[i]
								+ '">' + data.currencyarrename[i] + '</option>';
					}
					document.getElementById("currencyListDropdown").style.display = "block";

					$("#currency").html(s);

				}
			});
}
