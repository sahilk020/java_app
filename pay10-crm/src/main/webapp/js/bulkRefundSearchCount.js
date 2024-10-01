$(document)
		.ready(
				function() {
					
					
					

					$(function() {

						$(document)
								.ready(
										function() {

											var fromDate=document.getElementById("kt_datepicker_1").value;
											var toDate=document.getElementById("kt_datepicker_2").value;
											var filename=document.getElementById("Filename").value;

											var finalFromDate=fromDate+" 00:00:00"
											var finalToDate=toDate+" 23:59:58"
											
											var urls = new URL(window.location.href);
											var domain = urls.origin;

											$.ajax({

														type : "GET",
														url : domain+"/crmws/refund/searchRefundCount?FromDate="+finalFromDate+"&toDate="+finalToDate+"&filename="+filename+"",

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



function searchdata() {
	var fromDate=document.getElementById("kt_datepicker_1").value;
	var toDate=document.getElementById("kt_datepicker_2").value;
	var filename=document.getElementById("Filename").value;

	var finalFromDate=fromDate+" 00:00:00"
	var finalToDate=toDate+" 23:59:58"
	
	var urls = new URL(window.location.href);
	var domain = urls.origin;

	$.ajax({

				type : "GET",
				url : domain+"/crmws/refund/searchRefundCount?FromDate="+finalFromDate+"&toDate="+finalToDate+"&filename="+filename+"",

				data : {
					
				},
				success : function(
						response) {

					var responseObj = JSON
							.parse(JSON
									.stringify(response));
					if(responseObj.data.length !=0){
					renderTable(responseObj.data);
					}else{
						alert("no data is found");
						window.location.reload();

						

					}
				},
			});
}


function renderTable(data) {
//	var getindex = 0;
	var table = new $.fn.dataTable.Api('#dataTable');
//	debugger
//	var buttonCommon = {
//		exportOptions : {
//			format : {
//				body : function(data, column, row, node) {
//					// Strip $ from salary column to make it numeric
//					return (column === 1 ? "'" + data : data);
//				}
//			}
//		}
//	};

	$('#datatable').DataTable().destroy();

	$('#datatable').dataTable({

						'data' : data,

						'columns' : [
							
							{
								'data' : 'fileName',
								'className' : 'tds text-class'
							},
								
								{
	
									'data' : 'successTXN',
									'className' : 'tableId  text-class displayNone'
								},
								{
									'data' : 'failedTXN',
									'className' : 'batch_No text-class'
								},
								

								{
									'data' : 'pending',
									'className' : 'settlement_Date text-class'
								},
								
								{
									'data' : 'totalTxn',
									'className' : 'settlement_Date text-class'
								},
								
//								 {
//
//									"data" : null,
//									"visible" : false,
//									"className" : "displayNone",
//									"mRender" : function(row) {
//										return "\u0027" + row.id;
//
//									}
//								}

						]
					});
}







