$(document)
		.ready(
				function() {
					
					
					

					$(function() {

						$(document)
								.ready(
										function() {

											var fromDate=document.getElementById("kt_datepicker_1").value;
											var toDate=document.getElementById("kt_datepicker_2").value;
											var fileName=document.getElementById("fileName").value;
											var status=document.getElementById("status").value;
										
											var finalFromDate=fromDate+" 00:00:00"
											var finalToDate=toDate+" 23:59:58"
											var urls = new URL(window.location.href);
											var domain = urls.origin;


											$.ajax({

														type : "GET",
														url : domain+"/crmws/refund/searchRefund?FromDate="+finalFromDate+"&toDate="+finalToDate+"&fileName="+fileName+"&status="+status+"",

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
	var finalFromDate=fromDate+" 00:00:00"
	var finalToDate=toDate+" 23:59:58"
	var urls = new URL(window.location.href);
	var domain = urls.origin;
	
	var fileName=document.getElementById("fileName").value;
	var status=document.getElementById("status").value;



	$.ajax({

				type : "GET",
				url : domain+"/crmws/refund/searchRefund?FromDate="+finalFromDate+"&toDate="+finalToDate+"&fileName="+fileName+"&status="+status+"",

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
								'data' : 'payId',
								'className' : 'tds text-class'
							},
								
								{
	
									'data' : 'orderId',
									'className' : 'tableId  text-class displayNone'
								},
								{
									'data' : 'pgRefNO',
									'className' : 'batch_No text-class'
								},
								

								{
									'data' : 'amount',
									'className' : 'settlement_Date text-class'
								},

								{
									'data' : 'transactionType',
									'className' : 'tds text-class'
								},
								{
									'data' : 'createDate',
									'className' : 'tds text-class'
								},
								
								{
									'data' : 'status',
									'className' : 'tds text-class'
								},
								{
									'data' : 'fileName',
									'className' : 'tds text-class'
								},
								{
									'data' : 'refundOrderId',
									'className' : 'tds text-class'
								},
								{
									'data' : 'responseMessage',
									'className' : 'tds text-class'
								},
								{
									'data' : 'createBy',
									'className' : 'tds text-class'
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







