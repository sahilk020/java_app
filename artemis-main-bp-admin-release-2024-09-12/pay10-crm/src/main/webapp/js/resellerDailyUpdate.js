$(document)
		.ready(
				function() {

					$(function() {
						$("#dateFrom").flatpickr({
					
							maxDate: new Date(),
							dateFormat : 'Y-m-d',
							defaultDate: "today"
							
						});
						$("#dateTo").flatpickr({
							
							maxDate: new Date(),
							dateFormat : 'Y-m-d',
							defaultDate: "today"
						});
					});

					$(function() {
					
						$(document)
								.ready(
										function() {

											var reseller = document
													.getElementById("resellerId").value;
											var merchantId = document
													.getElementById("merchantDropdown").value;
											var paymentType = document
													.getElementById("paymentDropdown").value;
											var mopType = document
													.getElementById("moptypeDropdown").value;
											var date1 = document
													.getElementById("dateFrom").value;
											var date2 = document
													.getElementById("dateTo").value;

											$
													.ajax({

														type : "GET",
														url : "resellerDailyUpdates",

														data : {

															"resellerId" : reseller,
															"merchantname" : merchantId,
															"paymentType" : paymentType,
															"mopType" : mopType,
															"dateFrom" : date1,
															"dateTo" : date2,
														},
														success : function(
																response) {

															var responseObj = JSON
																	.parse(JSON
																			.stringify(response.resellerDailyUpdate));
															renderTable(responseObj);

														},
													});

										});
						

					});
				});

function getResellerList(userType) {

	$.ajax({

		type : "GET",
		url : "getResellerListByuserType",
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
			document.getElementById("toreseller").style.display = "block";

			$("#resellerId").html(s);

		}
	});
}
function getMerchant(resellerId) {
	
	var userType=document.getElementById("userType").value;

	$.ajax({

		type : "GET",
		url : "getResellerMerchant",
		timeout : 0,
		data : {
			"userType" : userType,
			"resellerId" : resellerId,

		},
		success : function(data, status) {
			// var response= JSON.stringify(data);
			var s = '<option value="">Select Merchant</option>';
			// alert(s)
			for (var i = 0; i < data.merchantarreList.length; i++) {
				s += '<option value="' + data.merchantarreList[i] + '">'
						+ data.merchantarrename[i] + '</option>';
				// console.log(s)
				// alert(s)
			}
			document.getElementById("tomerchant").style.display = "block";

			$("#merchantDropdown").html(s);

		}
	});
}
function getDetails() {
	var userType = document.getElementById("userType").value;
	var reseller = document.getElementById("resellerId").value;
	var merchantId = document.getElementById("merchantDropdown").value;
	var paymentType = document.getElementById("paymentDropdown").value;
	var mopType = document.getElementById("moptypeDropdown").value;
	var date1 = document.getElementById("dateFrom").value;
	var date2 = document.getElementById("dateTo").value;
	var currency = document.getElementById("currency").value;
	
	if (userType == "" || userType == null || userType=="Select User Type") {

		alert("Please Select User Type");
		return false;
	}

	if (reseller == "" || reseller == null) {

		alert("Please Select Reseller");
		return false;
	}
	if (merchantId == '' || merchantId == null) {
		alert("Please Select Merchant");
		return false;
	}
	if (paymentType == '' || paymentType == null) {

		alert("Please Select Payment Type");
		return false;
	}
	if (mopType == '' || mopType == null) {

		alert("Please Select Mop Type");
		return false;
	}
	if (currency == "" || currency == null) {

		alert("Please Select Currency");
		return false;
	}
	else {

		$.ajax({

			type : "GET",
			url : "resellerDailyUpdates",

			data : {

				"resellerId" : reseller,
				"merchantname" : merchantId,
				"paymentType" : paymentType,
				"mopType" : mopType,
				"dateFrom" : date1,
				"dateTo" : date2,
				"userType":userType,
				"currency":currency
			},
			success : function(response) {

				var responseObj = JSON.parse(JSON
						.stringify(response.resellerDailyUpdate));
				renderTable(responseObj);
			},
		});
	}
}
function renderTable(responseObj) {

	var table = new $.fn.dataTable.Api('#datatable');
	var token = document.getElementsByName("token")[0].value;

	var transFrom = document.getElementById("dateFrom").value;
	var transTo =  document.getElementById("dateTo").value;

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
		exportOptions: {
			format: {
				body: function (data, column, row, node) {
					if (typeof data === 'string') {
						return data.replace(/'/g, ''); // Remove single quotes from all other string columns
					} else if (data instanceof Date) {
						// Remove seconds from the date
						return data.toISOString().replace(/:\d{2}\.\d{3}Z$/, 'Z');
					}
					// Default formatting for other formats or non-string columns
					return data;
				}
			}
		}
	};
	$("#datatable").DataTable().destroy();
	$('#datatable').dataTable({
		dom : 'BTftlpi',

		'columnDefs' : [ {
			'searchable' : false,
			'targets' : [ 6 ]
		} ],

		buttons : [ $.extend(true, {}, buttonCommon, {
			extend : 'copyHtml5',
			exportOptions : {
				columns : [ 0, 1, 2, 3, 4, 5, 6,7,8 ]
			}
		}), $.extend(true, {}, buttonCommon, {
			extend : 'csvHtml5',
			title : 'Reseller Daily Update',
			exportOptions : {
				columns : [ 0, 1, 2, 3, 4, 5, 6,7,8]
			}
		}), {
			extend : 'pdfHtml5',
			title : 'Reseller Daily Update',
			orientation : 'landscape',
			exportOptions : {
				columns : [ 0, 1, 2, 3, 4, 5, 6,7,8 ]
			},
			customize : function(doc) {
				doc.defaultStyle.alignment = 'center';
				doc.styles.tableHeader.alignment = 'center';
			}
		}, {
			extend : 'colvis',
			// collectionLayout: 'fixed two-column',
			columns : [ 0, 1, 2, 3, 4, 5, 6,7,8 ]
		} ],

		"bProcessing" : true,
		"bLengthChange" : true,
		"bAutoWidth" : false,
		"iDisplayLength" : 10,
		"order" : [ [ 7, "desc" ] ],
		"aaData" : responseObj,
		"aoColumns" : [ {
			"mData" : "reseller_payId"
		}, {
			"mData" : "merchant_payId"
		}, {
            "mData" : "transType"
        }, {
            "mData" : "MOP"
        }, {
			"mData" : "amount"
		}, {
			"mData" : "saleamount"
		}, {
			"mData" : "totalRefund"
		}, {
            "mData" : "commisionamount"
        }, {
            "mData" : "transDate"
        }]
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
	if (transTo - transFrom > 31 * 86400000) {
		alert('No. of days can not be more than 31');
		$('#loader-wrapper').hide();
		$('#dateFrom').focus();
		return false;
	}
	var tableObj = $('#datatable');
	var table = tableObj.DataTable();
	table.ajax.reload();
}

function getPaymentType(payId) {

	$.ajax({
		type : "GET",
		url : "getMerchantPaymentType",
		data : {
			"merchantname" : payId,
		},
		success : function(data, status) {
			var paymentType = data.paymentTypeList;
			var s = '<option value="">Select Payment Type</option>';
			for (var i = 0; i < paymentType.length; i++) {
				s += '<option value="' + paymentType[i] + '">' + paymentType[i]
						+ '</option>';
			}
			document.getElementById("topayment").style.display = "block";

			$("#paymentDropdown").html(s);
			
		}
	});
}

function getMopType() {

	var paymentType = document.getElementById("paymentDropdown").value;
	var payId = document.getElementById("merchantDropdown").value;

	$.ajax({

		type : "GET",
		url : "getMerchantMopType",
		data : {
			"merchantname" : payId,
			"paymentType" : paymentType,
		},

		success : function(data, status) {
			var mopType = data.mopTypeList;
			var s = '<option value="">Select MOP Type</option>';
			for (var i = 0; i < mopType.length; i++) {
				s += '<option value="' + mopType[i] + '">' + mopType[i]
						+ '</option>';
			}
			document.getElementById("tomoptype").style.display = "block";

			$("#moptypeDropdown").html(s);
		}
	});
}

function getCurrencyList() {
	
	var resellerId= document.getElementById("resellerId").value;
	var merchantPayId= document.getElementById("merchantDropdown").value;
	var userType= document.getElementById("userType").value;
	  $.ajax({
	      
	        type : "GET",
	        url : "getUserDailyUpdateCurrencyList",
	        timeout : 0,
	        data : {
	          "userType":userType,
	            "resellerId": resellerId,
                 "merchantname": merchantPayId,
	            
	          },
	          
	        success : function(data) {
	            var s = '<option value="">Select Currency</option>';  
	                   for (var i = 0; i < data.currencyarrename.length; i++) {  
	                       s += '<option value="' + data.currencyCodearreList[i] + '">' + data.currencyarrename[i] + '</option>';  
	                   } 
	              document.getElementById("currencyListDropdown").style.display = "block";

	                   $("#currency").html(s); 
	                   
	                 
	        }
	     });  
}