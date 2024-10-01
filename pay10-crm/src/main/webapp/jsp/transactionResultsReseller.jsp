<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="/struts-tags" prefix="s"%>
<html dir="ltr" lang="en-US">
<head>
<title>Search Payment</title>

<!-- <link href="../css/default.css" rel="stylesheet" type="text/css" /> -->
<link rel="stylesheet" type="text/css" media="all" href="../css/daterangepicker-bs3.css" />
<link href="../css/Jquerydatatableview.css" rel="stylesheet" />
<link href="../css/jquery-ui.css" rel="stylesheet" />
<link href="../css/Jquerydatatable.css" rel="stylesheet" />
<script src="../js/jquery.min.js" type="text/javascript"></script>
<script src="../js/core/popper.min.js"></script>
<script src="../js/core/bootstrap-material-design.min.js"></script>
<script src="../js/plugins/perfect-scrollbar.jquery.min.js"></script>
<!-- Control Center for Material Dashboard: parallax effects, scripts for the example pages etc -->
<script src="../js/material-dashboard.js?v=2.1.0" type="text/javascript"></script>
<script src="../js/moment.js" type="text/javascript"></script>
<script src="../js/daterangepicker.js" type="text/javascript"></script>
<script src="../js/jquery.dataTables.js"></script>
<script src="../js/jquery-ui.js"></script>
<script src="../js/commanValidate.js"></script>
<script src="../js/jquery.popupoverlay.js"></script>
<script src="../js/dataTables.buttons.js" type="text/javascript"></script>
<script src="../js/pdfmake.js" type="text/javascript"></script>
<script src="../js/jszip.min.js" type="text/javascript"></script>
<script src="../js/vfs_fonts.js" type="text/javascript"></script>
<script src="../js/buttons.colVis.min.js" type="text/javascript"></script>

<link href="../css/select2.min.css" rel="stylesheet" />
<script src="../js/jquery.select2.js" type="text/javascript"></script>
<!--  loader scripts -->
<script src="../js/loader/modernizr-2.6.2.min.js"></script>
<script src="../js/loader/main.js"></script>
<link rel="stylesheet" href="../css/loader/normalize.css" />
<link rel="stylesheet" href="../css/loader/main.css" />
<link rel="stylesheet" href="../css/loader/customLoader.css" />

<script type="text/javascript">

function downloadReport(){

	var	merchantPayId = document.getElementById("merchant").value;
	var	transactionType = document.getElementById("transactionType").value;
	var	paymentType = document.getElementById("selectBox3").title;
	var status = document.getElementById("selectBox4").title;
	var currency = document.getElementById("currency").value;
	var dateFrom = document.getElementById("dateFrom").value;
	var dateTo = document.getElementById("dateTo").value;
    var acquirer = '';
	
	var transactionId = document.getElementById("pgRefNum").value;
	var	orderId = document.getElementById("orderId").value;
	var	customerEmail = document.getElementById("customerEmail").value;
	var	customerPhone = document.getElementById("custPhone").value;		
	var	mopType = document.getElementById("selectBox2").title;
	
	
		if(merchantPayId==''){
			merchantPayId='ALL'
		}
		if(transactionType==''){
			transactionType='ALL'
		}
		if(paymentType==''){
			paymentType='ALL'
		}
		if(status==''){
			status='ALL'
		}
		if(currency==''){
			currency='ALL'
		}
		if(mopType==''){
			mopType='ALL'
		}
		if(acquirer==''){
			acquirer='ALL'
		}
		
	dateFrom += " 00:00";	
	dateTo += " 23:59";
	
	document.getElementById("merchantPayIdH").value = merchantPayId;
	document.getElementById("transactionTypeH").value = transactionType;
	document.getElementById("paymentTypeH").value = paymentType;
	document.getElementById("statusH").value = status;
	document.getElementById("currencyH").value = currency;
	document.getElementById("dateFromH").value = dateFrom;
	document.getElementById("dateToH").value = dateTo;
	document.getElementById("acquirerH").value = acquirer;
	
	document.getElementById("transactionIdH").value = transactionId;
	document.getElementById("orderIdH").value = orderId;
	document.getElementById("customerEmailH").value = customerEmail;
	document.getElementById("customerPhoneH").value = customerPhone;
	document.getElementById("mopTypeH").value = mopType;

	document.getElementById("downloadPaymentReport").submit();
}


$(document).ready(function(){
 
  // Initialize select2
  $(".adminMerchants").select2();
});
</script>



<script type="text/javascript">
	$(document).ready(function() {

		$(function() {
			$("#dateFrom").datepicker({
				changeMonth: true,
			    changeYear: true,
				prevText : "click for previous months",
				nextText : "click for next months",
				showOtherMonths : true,
				dateFormat : 'dd-mm-yy',
				selectOtherMonths : true,
				minDate: new Date(2018, 1 - 1, 1),
				maxDate : new Date()
			});
			$("#dateTo").datepicker({
				changeMonth: true,
			    changeYear: true,
				prevText : "click for previous months",
				nextText : "click for next months",
				showOtherMonths : true,
				dateFormat : 'dd-mm-yy',
				selectOtherMonths : true,
				minDate: new Date(2018, 1 - 1, 1),
				maxDate : new Date()
			});
		});

		$(function() {
			var today = new Date();
			$('#dateTo').val($.datepicker.formatDate('dd-mm-yy', today));
			$('#dateFrom').val($.datepicker.formatDate('dd-mm-yy', today));
			renderTable();
			setTimeout(totalAmountofAlltxns,1000);
		});

		$("#submit").click(function(env) {
			 <!--------Restrict for empty date field----->
			var date1 = document.getElementById("dateFrom").value;
			if(date1 == "" || date1== null){
				 alert("Please select Date");
				 return;
			}
			var date2 = document.getElementById("dateTo").value;
			if(date2 == "" || date2== null){
				 alert("Please select Date");
				 return;
			}
			
			
			$('#loader-wrapper').show();
			reloadTable();
			setTimeout(totalAmountofAlltxns,1000);
		});

		$(function(){
			var datepick = $.datepicker;
			var table = $('#txnResultDataTable').DataTable();
			$('#txnResultDataTable').on('click', 'td.my_class1', function() {
				var rowIndex = table.cell(this).index().row;
				var rowData = table.row(rowIndex).data();
				
				popup(rowData.transactionIdString,rowData.oId,rowData.orderId,rowData.txnType,rowData.pgRefNum);
			});
		});
	});

	function renderTable() {
		var monthVal = parseInt(new Date().getMonth())+1;
		  var merchantEmailId = document.getElementById("merchant").value;
		var table = new $.fn.dataTable.Api('#txnResultDataTable');
		
		var transFrom = $.datepicker.parseDate('dd-mm-yy', $('#dateFrom').val());
		var transTo = $.datepicker.parseDate('dd-mm-yy', $('#dateTo').val());
		if (transFrom == null || transTo == null) {
			alert('Enter date value');
			return false;
		}

		if (transFrom > transTo) {
			$('#loader-wrapper').hide();
			alert('From date must be before the to date');
			$('#dateFrom').focus();
			return false;
		}
		if (transTo - transFrom > 31 * 86400000) {
			$('#loader-wrapper').hide();
			alert('No. of days can not be more than 31');
			$('#dateFrom').focus();
			return false;
		}
		var token = document.getElementsByName("token")[0].value;
        //$('#loader-wrapper').hide();
		
		 var buttonCommon = {
        exportOptions: {
            format: {
                body: function ( data, column, row, node ) {
                    // Strip $ from salary column to make it numeric
                    return column === 0 ? "'"+data : (column === 1 ? "'" + data: data);
                }
            }
        }
    };
	
		$('#txnResultDataTable').dataTable(
						{
							"footerCallback" : function(row, data, start, end, display) {
								var api = this.api(), data;

								// Remove the formatting to get integer data for summation
								var intVal = function(i) {
									return typeof i === 'string' ? i.replace(/[\,]/g, '') * 1: typeof i === 'number' ? i : 0;
								};

								// Total over all pages
								total = api.column(10).data().reduce(
										function(a, b) {
											return intVal(a) + intVal(b);
										}, 0);

								// Total over this page
								pageTotal = api.column(10, {
									page : 'current'
								}).data().reduce(function(a, b) {
									return intVal(a) + intVal(b);
								}, 0);

								// Update footer
								$(api.column(10).footer()).html(
										'' + inrFormat(pageTotal.toFixed(2) + ' ' + ' '));
										
										
																		// Total over all pages
								total = api.column(11).data().reduce(
										function(a, b) {
											return intVal(a) + intVal(b);
										}, 0);

								// Total over this page
								pageTotal = api.column(11, {
									page : 'current'
								}).data().reduce(function(a, b) {
									return intVal(a) + intVal(b);
								}, 0);

								// Update footer
								$(api.column(11).footer()).html(
										'' + inrFormat(pageTotal.toFixed(2) + ' ' + ' '));
										
										
										
							},
							"columnDefs": [{ 
								className: "dt-body-right",
								"targets": [1,2,3,4,5,6,7,8,9,10,11,12,13,14,15]
							}],
								dom : 'BTrftlpi',
								buttons : [
										$.extend( true, {}, buttonCommon, {
											extend: 'copyHtml5',											
											exportOptions : {											
												columns : [':visible']
											},
										} ),
									$.extend( true, {}, buttonCommon, {
											extend: 'csvHtml5',
											title : 'SearchPayment_Transactions_'+(new Date().getFullYear())+(monthVal>9?monthVal:'0'+monthVal)+(new Date().getDate()>9?new Date().getDate():'0'+new Date().getDate())+(new Date().getHours()>9?new Date().getHours():'0'+new Date().getHours())+(new Date().getMinutes()>9?new Date().getMinutes():'0'+new Date().getMinutes())+(new Date().getSeconds() >9?new Date().getSeconds():'0'+new Date().getSeconds()),
											exportOptions : {
												
												columns : [':visible']
											},
										} ),
									{
										extend : 'pdfHtml5',
										orientation : 'landscape',
										pageSize: 'legal',
										//footer : true,
										title : 'Search Transactions',
										exportOptions : {
											columns: [':visible']
										},
										customize: function (doc) {
										    doc.defaultStyle.alignment = 'center';
					     					doc.styles.tableHeader.alignment = 'center';
										  }
									},
									{
										extend : 'print',
										//footer : true,
										title : 'Search Transactions',
										exportOptions : {
											columns : [':visible']
										}
									},
									{
										extend : 'colvis',
										columns : [ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14]
									} ],

							"ajax" :{
								
								"url" : "transactionSearchAction",
								"type" : "POST",
								"timeout": 0,
								"data": function (d){
									return generatePostData(d);
								}
							},
							"fnDrawCallback" : function() {
									 $("#submit").removeAttr("disabled");
									 $('#loader-wrapper').hide();
							},
							 "searching": false,
							 "ordering": false,
							 "processing": true,
						        "serverSide": true,
						        "paginationType": "full_numbers", 
						        "lengthMenu": [[10, 25, 50, 100], [10, 25, 50, 100]],
								"order" : [ [ 2, "desc" ] ],
						       
						        "columnDefs": [
						            {
						            "type": "html-num-fmt", 
						            "targets": 4,
						            "orderable": true, 
						            "targets": [0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16]
						            }
						        ],
 
							"columns" : [ {
								"data" : "transactionIdString",
								"className" : "txnId my_class1 text-class",
								"width": "2%" 
							},  {
								"data" : "pgRefNum",
								"className" : "payId text-class"
								
							},{
								"data" : "merchants",
								"className" : "text-class"
							}, {
								"data" : "dateFrom",
								"className" : "text-class"
							}, {
								"data" : "orderId",
								"className" : "orderId text-class"
							}, {
								"data" : "refundOrderId",
								"className" : "orderId text-class"
							}, {
								"data" : "mopType",
								"className" : "txnType text-class"
							}, {
								"data" : "paymentMethods",
								"render" : function(data, type, full) {
									return full['paymentMethods'] + ' ' + '-'
											+ ' ' + full['mopType'];
								},
								"className" : "text-class"
							}, {
								"data" : "txnType",
								"className" : "txnType text-class",
							}, {
								"data" : "status",
								"className" : "status text-class"
							}, {
								"data" : "amount",
								"className" : "text-class",
								"render" : function(data){
									return inrFormat(data);
								}
							}
							, {
								"data" : "totalAmount",
								"className" : "text-class",
								"visible" : false,
								"render" : function(data){
									return inrFormat(data);
								}
							},{
								"data" : "payId",
								"visible" : false
								
							},{
								"data" : "customerEmail",
								"className" : "text-class"
							}, {
								"data" : "customerPhone",
								"className" : "text-class"
							},
							
							{
								"data" : "customerEmail",
								"visible" : false
							},  {
								"data" : null,
								"visible" : false,
								"className" : "displayNone",
								"mRender" : function(row) {
									return "\u0027" + row.transactionIdString;
								}
							}, {
								"data" : "customerEmail",
								"visible" : false,
								"className" : "displayNone"
							}, {
								"data" : "customerEmail",
								"visible" : false,
								"className" : "displayNone"
							},
							 {
								"data" : "oId",
								"visible" : false,
								"className" : "displayNone"
							}]
						});
						
		$(document).ready(function() {

					var table = $('#txnResultDataTable').DataTable();
				$('#txnResultDataTable').on('click','.center',function(){
					var columnIndex = table.cell(this).index().column;
					var rowIndex = table.cell(this).index().row;
					var rowNodes = table.row(rowIndex).node();
					var rowData = table.row(rowIndex).data();
					var txnType1 = rowData.txnType;
					var status1 = rowData.status;	
				
					if ((txnType1=="SALE" && status1=="Captured")||(txnType1=="AUTHORISE" && status1=="Approved")||(txnType1=="SALE" && status1=="Settled")) {						
						var payId1 =  rowData.pgRefNum;										
						var orderId1 = rowData.orderId; 					 
						var txnId1 = Number(rowData.transactionIdString); 
						document.getElementById('payIdc').value = payId1;
						document.getElementById('orderIdc').value = orderId1;
						document.getElementById('txnIdc').value = txnId1;
					    document.chargeback.submit();
					}
			});
		});
		
			
		
	}

	function reloadTable() {
		var datepick = $.datepicker;
		var transFrom = $.datepicker
				.parseDate('dd-mm-yy', $('#dateFrom').val());
		var transTo = $.datepicker.parseDate('dd-mm-yy', $('#dateTo').val());
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
		$("#submit").attr("disabled", true);
		var tableObj = $('#txnResultDataTable');
		var table = tableObj.DataTable();
		table.ajax.reload();
	}

	function generatePostData(d) {
		var token = document.getElementsByName("token")[0].value;
		var merchantEmailId = document.getElementById("merchant").value;
		var	transactionType = document.getElementById("transactionType").value;
		var paymentType = document.getElementById("selectBox3").title;
		var status = document.getElementById("selectBox4").title;
		var currency = document.getElementById("currency").value;
		var mopType = document.getElementById("selectBox2").title;	
		var acquirer = '';
		
		if(merchantEmailId==''){
			merchantEmailId='ALL'
		}
		if(transactionType==''){
			transactionType='ALL'
		}
		if(paymentType==''){
			paymentType='ALL'
		}
		if(status==''){
			status='ALL'
		}
		if(currency==''){
			currency='ALL'
		}
		if(mopType==''){
			mopType='ALL'
		}
		if(acquirer==''){
			acquirer='ALL'
		}
		
		var obj = {
			transactionId : document.getElementById("pgRefNum").value,
			orderId : document.getElementById("orderId").value,
			customerEmail : document.getElementById("customerEmail").value,
			customerPhone : document.getElementById("custPhone").value,
			merchantEmailId : merchantEmailId,
			transactionType : transactionType,
			paymentType : paymentType,
			status : status,
			mopType : mopType,
			acquirer:acquirer,
			currency : currency,
			dateFrom : document.getElementById("dateFrom").value,
			dateTo : document.getElementById("dateTo").value,
			draw : d.draw,
			length :d.length,
			start : d.start, 
			token : token,
			"struts.token.name" : "token",
		};

		return obj;
	}

function totalAmountofAlltxns(){
		
		var token = document.getElementsByName("token")[0].value;
		var merchantEmailId = document.getElementById("merchant").value;
		var	transactionType = document.getElementById("transactionType").value;
		var paymentType = document.getElementById("selectBox3").title;
		var status = document.getElementById("selectBox4").title;
		var currency = document.getElementById("currency").value;
		var mopType = document.getElementById("selectBox2").title;	

		
		if(merchantEmailId==''){
			merchantEmailId='ALL'
		}
		if(transactionType==''){
			transactionType='ALL'
		}
		if(paymentType==''){
			paymentType='ALL'
		}
		if(status==''){
			status='ALL'
		}
		if(currency==''){
			currency='ALL'
		}		
		if(mopType==''){
			mopType='ALL'
		}

		var obj = {
			transactionId : document.getElementById("pgRefNum").value,
			orderId : document.getElementById("orderId").value,
			customerEmail : document.getElementById("customerEmail").value,
			customerPhone : document.getElementById("custPhone").value,
			mopType:mopType,
			merchantEmailId : merchantEmailId,
			transactionType : transactionType,
			paymentType : paymentType,
			status : status,
			currency : currency,
			dateFrom : document.getElementById("dateFrom").value,
			dateTo : document.getElementById("dateTo").value, 
			token : token,
			"struts.token.name" : "token",
		}
		

		
		$.ajax({
		    	url: "totalAmountOfAllTxnsAction",
				timeout : 0,
		    	type : "POST",
		    	data :obj,
		    	success: function(response){
					var responseObj =  response.totalFinalResult;

					$( "#totalTxnsAmount" ).empty();
					$( "#totalSettledTxnsAmount" ).empty();

					
					var table = document.getElementById("totalTxnsAmount");
					var tr = document.createElement('tr');
					var tr1 = document.createElement('tr');
					var tr2 = document.createElement('tr');
					var tr3 = document.createElement('tr');
					var tr4 = document.createElement('tr');
					var tr5 = document.createElement('tr');
					var tr5a = document.createElement('tr');
					var tr6 = document.createElement('tr');
					var tr7 = document.createElement('tr');
					var tr8 = document.createElement('tr');
					
					var th1 = document.createElement('th');
					var th2 = document.createElement('th');
					var th3 = document.createElement('th');
					var th4 = document.createElement('th');
					th1.appendChild(document.createTextNode("Transaction Type"));
					th2.appendChild(document.createTextNode("Status"));
					th3.appendChild(document.createTextNode("No Of Transactions"));
					th4.appendChild(document.createTextNode("Total Amount of All Transactions"));					
					tr.appendChild(th1);
					tr.appendChild(th2);
					tr.appendChild(th3);
					tr.appendChild(th4);
					
					table.appendChild(tr);
					
					var td1a = document.createElement('td');
					var td2a = document.createElement('td');
					var td3a = document.createElement('td');
					var td4a = document.createElement('td');
						

					td1a.appendChild(document.createTextNode('SALE'));
					td2a.appendChild(document.createTextNode('Captured'));
					td3a.appendChild(document.createTextNode(responseObj.totalSaleSuccCount ? responseObj.totalSaleSuccCount  : '0'));
					td4a.appendChild(document.createTextNode(inrFormat(responseObj.totalSaleSuccAmount ? responseObj.totalSaleSuccAmount  : '0.00')));					
					tr1.appendChild(td1a);
					tr1.appendChild(td2a);
					tr1.appendChild(td3a);
					tr1.appendChild(td4a);
					table.appendChild(tr1);
					
					
					var td1b = document.createElement('td');
					var td2b = document.createElement('td');
					var td3b = document.createElement('td');
					var td4b = document.createElement('td');
					
					td1b.appendChild(document.createTextNode(''));
					td2b.appendChild(document.createTextNode('Pending'));
					td3b.appendChild(document.createTextNode(responseObj.totalSalePendingCount ? responseObj.totalSalePendingCount  : '0'));
					td4b.appendChild(document.createTextNode(inrFormat(responseObj.totalSalePendingAmount ? responseObj.totalSalePendingAmount : '0.00')));					
					tr2.appendChild(td1b);
					tr2.appendChild(td2b);
					tr2.appendChild(td3b);
					tr2.appendChild(td4b);
					
					table.appendChild(tr2);
					
					var td1c = document.createElement('td');
					var td2c = document.createElement('td');
					var td3c = document.createElement('td');
					var td4c = document.createElement('td');
					
					td1c.appendChild(document.createTextNode(''));
					td2c.appendChild(document.createTextNode('Failed'));
					td3c.appendChild(document.createTextNode(responseObj.totalSalefailCount ? responseObj.totalSalefailCount  : '0'));
					td4c.appendChild(document.createTextNode(inrFormat(responseObj.totalSalefailAmount ? responseObj.totalSalefailAmount : '0.00')));					
					tr3.appendChild(td1c);
					tr3.appendChild(td2c);
					tr3.appendChild(td3c);
					tr3.appendChild(td4c);
					
					table.appendChild(tr3);
					
					var td1e = document.createElement('td');
					var td2e = document.createElement('td');
					var td3e = document.createElement('td');
					var td4e = document.createElement('td');
					
					td1e.appendChild(document.createTextNode(''));
					td2e.appendChild(document.createTextNode('Cancelled'));
					td3e.appendChild(document.createTextNode(responseObj.totalSaleCancelledCount ? responseObj.totalSaleCancelledCount  : '0'));
					td4e.appendChild(document.createTextNode(inrFormat(responseObj.totalSaleCancelledAmount ? responseObj.totalSaleCancelledAmount : '0.00')));					
					tr5.appendChild(td1e);
					tr5.appendChild(td2e);
					tr5.appendChild(td3e);
					tr5.appendChild(td4e);
					
					table.appendChild(tr5);
					
					var td1ez = document.createElement('td');
					var td2ez = document.createElement('td');
					var td3ez = document.createElement('td');
					var td4ez = document.createElement('td');
					
					td1ez.appendChild(document.createTextNode(''));
					td2ez.appendChild(document.createTextNode('Invalid'));
					td3ez.appendChild(document.createTextNode(responseObj.totalSaleInvalidCount ? responseObj.totalSaleInvalidCount  : '0'));
					td4ez.appendChild(document.createTextNode(inrFormat(responseObj.totalSaleInvalidAmount ? responseObj.totalSaleInvalidAmount : '0.00')));
						
					tr5a.appendChild(td1ez);
					tr5a.appendChild(td2ez);
					tr5a.appendChild(td3ez);
					tr5a.appendChild(td4ez);
					
					table.appendChild(tr5a);

					
					var td1f = document.createElement('td');
					var td2f = document.createElement('td');
					var td3f = document.createElement('td');
					var td4f = document.createElement('td');
						
					td1f.appendChild(document.createTextNode('REFUND'));
					td2f.appendChild(document.createTextNode('Captured'));
					td3f.appendChild(document.createTextNode(responseObj.totalRefundSuccCount ? responseObj.totalRefundSuccCount  : '0'));
					td4f.appendChild(document.createTextNode(inrFormat(responseObj.totalRefundSuccAmount ? responseObj.totalRefundSuccAmount : '0.00')));					
					tr6.appendChild(td1f);
					tr6.appendChild(td2f);
					tr6.appendChild(td3f);
					tr6.appendChild(td4f);
					table.appendChild(tr6);
					
					var td1g = document.createElement('td');
					var td2g = document.createElement('td');
					var td3g = document.createElement('td');
					var td4g = document.createElement('td');
					
					td1g.appendChild(document.createTextNode(''));
					td2g.appendChild(document.createTextNode('Remaining Other Status'));
					td3g.appendChild(document.createTextNode(responseObj.totalRefundFailCount ? responseObj.totalRefundFailCount : '0'));
					td4g.appendChild(document.createTextNode(inrFormat(responseObj.totalRefundFailAmount ? responseObj.totalRefundFailAmount : '0.00')));					
					tr7.appendChild(td1g);
					tr7.appendChild(td2g);
					tr7.appendChild(td3g);
					tr7.appendChild(td4g);
					
					table.appendChild(tr7);
					
					
					var td1h = document.createElement('td');
					var td2h = document.createElement('td');
					var td3h = document.createElement('td');
					var td4h = document.createElement('td');
					
					td1h.appendChild(document.createTextNode('Total'));
					td2h.appendChild(document.createTextNode('ALL'));
					td3h.appendChild(document.createTextNode(responseObj.totaltxns ? responseObj.totaltxns : '0'));
					td4h.appendChild(document.createTextNode(inrFormat(responseObj.totalTxnAmount ? responseObj.totalTxnAmount : '0.00')));
					
					tr8.appendChild(td1h);
					tr8.appendChild(td2h);
					tr8.appendChild(td3h);
					tr8.appendChild(td4h);
					
					table.appendChild(tr8);


					// Settled Related Code 
					
					var table = document.getElementById("totalSettledTxnsAmount");
					var tr = document.createElement('tr');
					var tr1 = document.createElement('tr');
					var tr2 = document.createElement('tr');
					var tr3 = document.createElement('tr');
					var tr4 = document.createElement('tr');
					var tr5 = document.createElement('tr');
					var tr6 = document.createElement('tr');
					var tr7 = document.createElement('tr');
					var tr8 = document.createElement('tr');
					var tr9 = document.createElement('tr');
					var tr10 = document.createElement('tr');
					var tr12 = document.createElement('tr');
					var tr13 = document.createElement('tr');
					
					var th1 = document.createElement('th');
					var th2 = document.createElement('th');
					var th3 = document.createElement('th');
					var th4 = document.createElement('th');
					th1.appendChild(document.createTextNode("Transaction Type"));
					th2.appendChild(document.createTextNode("Payment Type"));
					th3.appendChild(document.createTextNode("No Of Transactions"));
					th4.appendChild(document.createTextNode("Total Amount of All Transactions"));					
					tr.appendChild(th1);
					tr.appendChild(th2);
					tr.appendChild(th3);
					tr.appendChild(th4);
					
					table.appendChild(tr);
					
					var td1a = document.createElement('td');
					var td2a = document.createElement('td');
					var td3a = document.createElement('td');
					var td4a = document.createElement('td');
						

					td1a.appendChild(document.createTextNode('SALE'));
					td2a.appendChild(document.createTextNode('Credit Card'));
					td3a.appendChild(document.createTextNode(responseObj.totalSaleCCcount ? responseObj.totalSaleCCcount  : '0'));
					td4a.appendChild(document.createTextNode(inrFormat(responseObj.totalSaleCCAmount ? responseObj.totalSaleCCAmount : '0.00')));					
					tr1.appendChild(td1a);
					tr1.appendChild(td2a);
					tr1.appendChild(td3a);
					tr1.appendChild(td4a);
					table.appendChild(tr1);
					
					var td1b = document.createElement('td');
					var td2b = document.createElement('td');
					var td3b = document.createElement('td');
					var td4b = document.createElement('td');
					
					td1b.appendChild(document.createTextNode(''));
					td2b.appendChild(document.createTextNode('Debit Card'));
					td3b.appendChild(document.createTextNode(responseObj.totalSaleDCcount ? responseObj.totalSaleDCcount  : '0'));
					td4b.appendChild(document.createTextNode(inrFormat(responseObj.totalSaleDCAmount ? responseObj.totalSaleDCAmount : '0.00')));					
					tr2.appendChild(td1b);
					tr2.appendChild(td2b);
					tr2.appendChild(td3b);
					tr2.appendChild(td4b);
					
					table.appendChild(tr2);
					
					var td1c = document.createElement('td');
					var td2c = document.createElement('td');
					var td3c = document.createElement('td');
					var td4c = document.createElement('td');
					
					td1c.appendChild(document.createTextNode(''));
					td2c.appendChild(document.createTextNode('Net Banking'));
					td3c.appendChild(document.createTextNode(responseObj.totalSaleNBcount ? responseObj.totalSaleNBcount  : '0'));
					td4c.appendChild(document.createTextNode(inrFormat(responseObj.totalSaleNBAmount ? responseObj.totalSaleNBAmount : '0.00')));					
					tr3.appendChild(td1c);
					tr3.appendChild(td2c);
					tr3.appendChild(td3c);
					tr3.appendChild(td4c);
					
					table.appendChild(tr3);
					
					var td1d = document.createElement('td');
					var td2d = document.createElement('td');
					var td3d = document.createElement('td');
					var td4d = document.createElement('td');
					
					td1d.appendChild(document.createTextNode(''));
					td2d.appendChild(document.createTextNode('Wallet'));
					td3d.appendChild(document.createTextNode(responseObj.totalSalewalletcount ? responseObj.totalSalewalletcount  : '0'));
					td4d.appendChild(document.createTextNode(inrFormat(responseObj.totalSalewalletAmount ? responseObj.totalSalewalletAmount : '0.00')));					
					tr4.appendChild(td1d);
					tr4.appendChild(td2d);
					tr4.appendChild(td3d);
					tr4.appendChild(td4d);
					
					table.appendChild(tr4);
					
					var td1e = document.createElement('td');
					var td2e = document.createElement('td');
					var td3e = document.createElement('td');
					var td4e = document.createElement('td');
					
					td1e.appendChild(document.createTextNode(''));
					td2e.appendChild(document.createTextNode('UPI'));
					td3e.appendChild(document.createTextNode(responseObj.totalSaleUPIcount ? responseObj.totalSaleUPIcount  : '0'));
					td4e.appendChild(document.createTextNode(inrFormat(responseObj.totalSaleUPIAmount ? responseObj.totalSaleUPIAmount : '0.00')));					
					tr5.appendChild(td1e);
					tr5.appendChild(td2e);
					tr5.appendChild(td3e);
					tr5.appendChild(td4e);
					
					table.appendChild(tr5);
					
					var td1x = document.createElement('td');
					var td2x = document.createElement('td');
					var td3x = document.createElement('td');
					var td4x = document.createElement('td');
					
					td1x.appendChild(document.createTextNode('Total SALE'));
					td2x.appendChild(document.createTextNode('ALL Payments'));
					td3x.appendChild(document.createTextNode(responseObj.totalSaleCount ? responseObj.totalSaleCount  : '0'));
					td4x.appendChild(document.createTextNode(inrFormat(responseObj.totalSaleAmount ? responseObj.totalSaleAmount : '0.00')));					
					tr12.appendChild(td1x);
					tr12.appendChild(td2x);
					tr12.appendChild(td3x);
					tr12.appendChild(td4x);
					
					table.appendChild(tr12);
					
					var td1f = document.createElement('td');
					var td2f = document.createElement('td');
					var td3f = document.createElement('td');
					var td4f = document.createElement('td');
						

					td1f.appendChild(document.createTextNode('REFUND'));
					td2f.appendChild(document.createTextNode('Credit Card'));
					td3f.appendChild(document.createTextNode(responseObj.totalRfCCcount ? responseObj.totalRfCCcount  : '0'));
					td4f.appendChild(document.createTextNode(inrFormat(responseObj.totalRfCCAmount ? responseObj.totalRfCCAmount : '0.00')));					
					tr6.appendChild(td1f);
					tr6.appendChild(td2f);
					tr6.appendChild(td3f);
					tr6.appendChild(td4f);
					table.appendChild(tr6);
					
					var td1g = document.createElement('td');
					var td2g = document.createElement('td');
					var td3g = document.createElement('td');
					var td4g = document.createElement('td');
					
					td1g.appendChild(document.createTextNode(''));
					td2g.appendChild(document.createTextNode('Debit Card'));
					td3g.appendChild(document.createTextNode(responseObj.totalRfDCcount ? responseObj.totalRfDCcount : '0'));
					td4g.appendChild(document.createTextNode(inrFormat(responseObj.totalRfDCAmount ? responseObj.totalRfDCAmount : '0.00')));					
					tr7.appendChild(td1g);
					tr7.appendChild(td2g);
					tr7.appendChild(td3g);
					tr7.appendChild(td4g);
					
					table.appendChild(tr7);
					
					var td1h = document.createElement('td');
					var td2h = document.createElement('td');
					var td3h = document.createElement('td');
					var td4h = document.createElement('td');
					
					td1h.appendChild(document.createTextNode(''));
					td2h.appendChild(document.createTextNode('Net Banking'));
					td3h.appendChild(document.createTextNode(responseObj.totalRfNBcount ? responseObj.totalRfNBcount : '0'));
					td4h.appendChild(document.createTextNode(inrFormat(responseObj.totalRfNBAmount ? responseObj.totalRfNBAmount : '0.00')));					
					tr8.appendChild(td1h);
					tr8.appendChild(td2h);
					tr8.appendChild(td3h);
					tr8.appendChild(td4h);
					
					table.appendChild(tr8);
					
					var td1i = document.createElement('td');
					var td2i = document.createElement('td');
					var td3i = document.createElement('td');
					var td4i = document.createElement('td');
					
					td1i.appendChild(document.createTextNode(''));
					td2i.appendChild(document.createTextNode('Wallet'));
					td3i.appendChild(document.createTextNode(responseObj.totalRfwalletcount ? responseObj.totalRfwalletcount : '0'));
					td4i.appendChild(document.createTextNode(inrFormat(responseObj.totalRfwalletAmount ? responseObj.totalRfwalletAmount : '0.00')));					
					tr9.appendChild(td1i);
					tr9.appendChild(td2i);
					tr9.appendChild(td3i);
					tr9.appendChild(td4i);
					
					table.appendChild(tr9);
					
					var td1j = document.createElement('td');
					var td2j = document.createElement('td');
					var td3j = document.createElement('td');
					var td4j = document.createElement('td');
					
					td1j.appendChild(document.createTextNode(''));
					td2j.appendChild(document.createTextNode('UPI'));
					td3j.appendChild(document.createTextNode(responseObj.totalRfUPIcount ? responseObj.totalRfUPIcount : '0'));
					td4j.appendChild(document.createTextNode(inrFormat(responseObj.totalRfUPIAmount ? responseObj.totalRfUPIAmount : '0.00')));					
					tr10.appendChild(td1j);
					tr10.appendChild(td2j);
					tr10.appendChild(td3j);
					tr10.appendChild(td4j);
					
					table.appendChild(tr10);
					
					var td1y = document.createElement('td');
					var td2y = document.createElement('td');
					var td3y = document.createElement('td');
					var td4y = document.createElement('td');
					
					td1y.appendChild(document.createTextNode('Total Refund'));
					td2y.appendChild(document.createTextNode('ALL Payments'));
					td3y.appendChild(document.createTextNode(responseObj.totalRfCount ? responseObj.totalRfCount  : '0'));
					td4y.appendChild(document.createTextNode(inrFormat(responseObj.totalRfAmount ? responseObj.totalRfAmount : '0.00')));					
					tr13.appendChild(td1y);
					tr13.appendChild(td2y);
					tr13.appendChild(td3y);
					tr13.appendChild(td4y);
					
					table.appendChild(tr13);
					
					
					var tr11 = document.createElement('tr');
					var td1k = document.createElement('td');
					var td2k = document.createElement('td');
					var td3k = document.createElement('td');
					var td4k = document.createElement('td');
					
					td1k.appendChild(document.createTextNode('Total'));
					td2k.appendChild(document.createTextNode('ALL'));
					td3k.appendChild(document.createTextNode(responseObj.totalSettleCount ? responseObj.totalSettleCount : '0'));
					td4k.appendChild(document.createTextNode(inrFormat(responseObj.totalSettleAmount ? responseObj.totalSettleAmount : '0.00')));
					
					tr11.appendChild(td1k);
					tr11.appendChild(td2k);
					tr11.appendChild(td3k);
					tr11.appendChild(td4k);
					
					table.appendChild(tr11);

					
		    	},
		    	error: function(xhr, textStatus, errorThrown){
			       
			    }
		});		
}

	function popup(txnId, oId,orderId,txnType,pgRefNum) {
		
		var token = document.getElementsByName("token")[0].value;
		var myData = {
			token : token,
			"struts.token.name" : "token",
			"transactionId":txnId,
			"oId":oId,
			"orderId":orderId,
			"txnType":txnType,
			"pgRefNum":pgRefNum
		}
		$.ajax({
		    	url: "customerAddressAction",
		    	type : "POST",
		    	data :myData,
		    	success: function(response){
					var responseObj =  response.aaData;
					var transObj = response.trailData[0];
					var txt = document.createElement("textarea");	
					/* Start Billing details  */	
					$('#sec1 td').eq(0).text(responseObj.custName ? responseObj.custName : 'Not Available');
                    $('#sec1 td').eq(1).text(responseObj.custPhone ? responseObj.custPhone : 'Not Available');
                    
                    $('#sec2 td').eq(0).text(responseObj.custCity ? responseObj.custCity : 'Not Available');
                    $('#sec2 td').eq(1).text(responseObj.custState ? responseObj.custState : 'Not Available');

					$('#sec7 td').eq(0).text(responseObj.custCountry ? responseObj.custCountry : 'Not Available');
					$('#sec7 td').eq(1).text(responseObj.custZip ? responseObj.custZip : 'Not Available');

					$('#address1 td').text(responseObj.custStreetAddress1 ? responseObj.custStreetAddress1 : 'Not Available');
					$('#address2 td').text(responseObj.custStreetAddress2 ? responseObj.custStreetAddress2 : 'Not Available');
					/* End Billing details  */
						/* Start Payment details  */	
					var totalMerchantTdr;
					var totalMerchantGst;
					
					totalMerchantGst = parseInt(responseObj.acquirerGst ? responseObj.acquirerGst :0) + parseInt(responseObj.pgGst ? responseObj.pgGst :0);
					totalMerchantTdr = parseInt(responseObj.acquirerTdr ? responseObj.acquirerTdr :0) + parseInt(responseObj.pgTdr ? responseObj.pgTdr :0);	
					
					$('#sec3chn td').eq(0).text(responseObj.cardHolderName ? responseObj.cardHolderName : 'Not Available');
					
					$('#sec3 td').eq(0).text(responseObj.cardMask ? responseObj.cardMask : 'Not Available');
                    $('#sec3 td').eq(1).text(responseObj.issuer ? responseObj.issuer : 'Not Available');
                    
					$('#sec4 td').eq(0).text(responseObj.mopType ? responseObj.mopType : 'Not Available');
					
					$('#sec5 td').eq(0).text(totalMerchantTdr ? totalMerchantTdr+".00" : 'Not Available');
                    $('#sec5 td').eq(1).text(totalMerchantGst ? totalMerchantGst+".00" : 'Not Available');
                    

					$('#address5 td').text(responseObj.pgTxnMsg ? responseObj.pgTxnMsg : 'Not Available');
					
					
                    /* End Payment details  */
					/*Added by CS on 27 Dec for Shipping Details */
					$('#sec8 td').eq(0).text(responseObj.custShipName ? responseObj.custShipName : 'Not Available');
					$('#sec8 td').eq(1).text(responseObj.custShipPhone ? responseObj.custShipPhone : 'Not Available');

					$('#sec9 td').eq(0).text(responseObj.custShipCity ? responseObj.custShipCity : 'Not Available');
					$('#sec9 td').eq(1).text(responseObj.custShipState ? responseObj.custShipState : 'Not Available');
					
					$('#sec10 td').eq(0).text(responseObj.custShipCountry ? responseObj.custShipCountry : 'Not Available');
					$('#sec10 td').eq(1).text(responseObj.custShipZip ? responseObj.custShipZip : 'Not Available');
					

					$('#address3 td').text(responseObj.custShipStreetAddress1 ? responseObj.custShipStreetAddress1 : 'Not Available');
					$('#address4 td').text(responseObj.custShipStreetAddress2 ? responseObj.custShipStreetAddress2 : 'Not Available');

					/*Start Transaction details */
					
					$( "#transactionDetails" ).empty();
					var table = document.getElementById("transactionDetails");
					var tr = document.createElement('tr');
					var th1 = document.createElement('th');
					var th2 = document.createElement('th');
					var th3 = document.createElement('th');
					var th4 = document.createElement('th');
					th1.appendChild(document.createTextNode("Order ID"));
					th2.appendChild(document.createTextNode("Transaction Type"));
					th3.appendChild(document.createTextNode("Date"));
					th4.appendChild(document.createTextNode("Status"));
					tr.appendChild(th1);
					tr.appendChild(th2);
					tr.appendChild(th3);
					tr.appendChild(th4);
					
					table.appendChild(tr);
					
					var totalRefundamount = 0;
					var totalSaleamount = 0;					
					var internalCustip;
					
					var curAmount;
					var curStatus;
					var curOrderId;
					var curPgRefNum;
					var curArn;
					var curRrn;
					
					var curTxnType;
					for(var i = 0; i < response.trailData.length; i++){
						
						var tr2 = document.createElement('tr');
						
						var td1 = document.createElement('td');
						var td2 = document.createElement('td');
						var td3 = document.createElement('td');
						var td4 = document.createElement('td');
						
						
						if(response.trailData[i].txnType == "REFUND"){
							txt.innerHTML = (response.trailData[i].refundOrderId ? response.trailData[i].refundOrderId : 'Not Available');
						td1.appendChild(document.createTextNode(txt.value));
						}
						if(response.trailData[i].txnType == "SALE"){
							txt.innerHTML = (response.trailData[i].orderId ? response.trailData[i].orderId : 'Not Available');
						td1.appendChild(document.createTextNode(txt.value));
						}
						
						td2.appendChild(document.createTextNode(response.trailData[i].txnType ? response.trailData[i].txnType : 'Not Available'));
						td3.appendChild(document.createTextNode(response.trailData[i].createDate ? response.trailData[i].createDate : 'Not Available'));
						td4.appendChild(document.createTextNode(response.trailData[i].status ? response.trailData[i].status : 'Not Available'));
						
						tr2.appendChild(td1);
						tr2.appendChild(td2);
						tr2.appendChild(td3);
						tr2.appendChild(td4);
						
						table.appendChild(tr2);
						if(response.trailData[i].txnType == "REFUND" && response.trailData[i].status == "Captured"){							
							totalRefundamount += (response.trailData[i].amount ? parseInt(response.trailData[i].amount) : 0);
						}
						
						if(response.trailData[i].txnType == "SALE" && response.trailData[i].status == "Captured"){							
							totalSaleamount += (response.trailData[i].amount ? parseInt(response.trailData[i].amount) : 0);
						}
						
						if(response.trailData[i].transactionId == txnId){							
							curAmount = (response.trailData[i].amount ? response.trailData[i].amount : 0);
							curStatus = (response.trailData[i].currentStatus ? response.trailData[i].currentStatus : 0);
							if(response.trailData[i].txnType == "SALE"){
								curOrderId = (response.trailData[i].orderId ? response.trailData[i].orderId : 0);
							}else{
								curOrderId = (response.trailData[i].refundOrderId ? response.trailData[i].refundOrderId : 0);
							}
							
							curPgRefNum = (response.trailData[i].pgRefNum ? response.trailData[i].pgRefNum : 0);
							curArn = (response.trailData[i].arn ? response.trailData[i].arn : 0);
							curRrn = (response.trailData[i].rrn ? response.trailData[i].rrn : 0);
							curTxnType = (response.trailData[i].txnType ? response.trailData[i].txnType : 'Not Available');
						}
						
						internalCustip = (response.trailData[i].internalCustIP ? response.trailData[i].internalCustIP : 0);
					}
					
					$('#sec14 td').eq(0).text(transObj.acqId ? transObj.acqId : 'Not Available');
					$('#sec14 td').eq(1).text(inrFormat(totalRefundamount ? totalRefundamount+".00" : 'Not Available'));

					$('#sec16 td').eq(0).text(internalCustip ? internalCustip : 'Not Available');
					$('#sec16 td').eq(1).text(inrFormat(totalSaleamount ? totalSaleamount+".00" : 'Not Available'));
					
					if(curTxnType == "SALE"){
							$('#sec16 td').eq(1).css( "display", "none" );
							$('#sec16h th').eq(1).css( "display", "none" );
					}
					if(curTxnType == "REFUND"){
							$('#sec16 td').eq(1).css( "display", "block" );
							$('#sec16h th').eq(1).css( "display", "block" );
					}

					
                    $('#sec11 td').eq(0).text(inrFormat(curAmount ? curAmount : 'Not Available'));
					$('#sec11 td').eq(1).text(curStatus ? curStatus : 'Not Available');

					txt.innerHTML = (curOrderId ? curOrderId : 'Not Available');
	
					$('#sec12 td').eq(0).text(txt.value);
					$('#sec12 td').eq(1).text(curPgRefNum ? curPgRefNum : 'Not Available');

					$('#sec13 td').eq(0).text(curArn ? curArn : 'Not Available');
					$('#sec13 td').eq(1).text(curRrn ? curRrn : 'Not Available');


                    /*End Transaction details */
					
				$('#popup').show();
		    	},
		    	error: function(xhr, textStatus, errorThrown){
			       
			    }
		});

	};
</script>

<script>

function validPgRefNum(){
	
	var pgRefValue = document.getElementById("pgRefNum").value;
	var regex = /^(?!0{16})[0-9\b]{16}$/;
	if(pgRefValue.trim() != ""){
		if(!regex.test(pgRefValue)) {
			document.getElementById("validValue").style.display= "block";
			document.getElementById("submit").disabled = true;
			document.getElementById("download").disabled = true;
        }
		else {
			if(document.getElementById("validEamilValue").style.display != "block" && document.getElementById("validPhoneValue").style.display != "block" && document.getElementById("validOrderIdValue").style.display != "block"){
				document.getElementById("submit").disabled = false;
				document.getElementById("download").disabled = false;				
			}
			document.getElementById("validValue").style.display= "none";
		 }
	}
	else {
			if(document.getElementById("validEamilValue").style.display != "block" && document.getElementById("validPhoneValue").style.display != "block"&& document.getElementById("validOrderIdValue").style.display != "block"){
				document.getElementById("submit").disabled = false;
				document.getElementById("download").disabled = false;				
			}
	    document.getElementById("validValue").style.display= "none";
    }
}

function validateCustomerEmail(emailField){
  
	var reg = /^([A-Za-z0-9_\-\.\+])+\@([A-Za-z0-9_\-\+])+\.([A-Za-z]{2,4})$/;
  if (emailField.value !== "") {
    if (reg.test(emailField.value) == false) 
    {
		document.getElementById("validEamilValue").style.display= "block";
		document.getElementById("submit").disabled = true;
		document.getElementById("download").disabled = true;
	}else{
			if(document.getElementById("validValue").style.display != "block" && document.getElementById("validPhoneValue").style.display != "block"&& document.getElementById("validOrderIdValue").style.display != "block"){
				document.getElementById("submit").disabled = false;
				document.getElementById("download").disabled = false;				
			}
	    document.getElementById("validEamilValue").style.display= "none";
	}
  }else{
			if(document.getElementById("validValue").style.display != "block" && document.getElementById("validPhoneValue").style.display != "block"&& document.getElementById("validOrderIdValue").style.display != "block"){
				document.getElementById("submit").disabled = false;
				document.getElementById("download").disabled = false;				
			}
	    document.getElementById("validEamilValue").style.display= "none";
  }

}

function validateCustomerPhone(phoneField){
	var phreg =/^([0]|\+91)?[- ]?[56789]\d{9}$/;
	// mobileRegex = /^[789]\d{9}/ --> this regex we are using the sign up page
  if (phoneField.value !== "") {

    if (phreg.test(phoneField.value) == false) 
    {
		document.getElementById("validPhoneValue").style.display= "block";
		document.getElementById("submit").disabled = true;
		document.getElementById("download").disabled = true;
	}else{
			if(document.getElementById("validEamilValue").style.display != "block" && document.getElementById("validValue").style.display != "block"&& document.getElementById("validOrderIdValue").style.display != "block"){
				document.getElementById("submit").disabled = false;
				document.getElementById("download").disabled = false;				
			}
	    document.getElementById("validPhoneValue").style.display= "none";

	}
  }else{
			if(document.getElementById("validEamilValue").style.display != "block" && document.getElementById("validValue").style.display != "block"&& document.getElementById("validOrderIdValue").style.display != "block"){
				document.getElementById("submit").disabled = false;
				document.getElementById("download").disabled = false;				
			}
	    document.getElementById("validPhoneValue").style.display= "none";
  }

}

function removeSpaces(fieldVal){
	setTimeout(function() {
	var nospacepgRefVal = fieldVal.value.replace(/ /g, "");
	fieldVal.value = nospacepgRefVal;
	}, 400);
}

function validateOrderIdvalue(orderId){
setTimeout(function() {		
	var orderIdreg =/^[0-9a-zA-Z\b\_-\s\+.]+$/;
  if (orderId.value !== "") {
    if (orderIdreg.test(orderId.value) == false) 
    {
		document.getElementById("validOrderIdValue").style.display= "block";
		document.getElementById("submit").disabled = true;
		document.getElementById("download").disabled = true;
	}else{
			if(document.getElementById("validEamilValue").style.display != "block" && document.getElementById("validValue").style.display != "block"&& document.getElementById("validPhoneValue").style.display != "block"){
				document.getElementById("submit").disabled = false;
				document.getElementById("download").disabled = false;				
			}
	    document.getElementById("validOrderIdValue").style.display= "none";

	}
  }else{
			if(document.getElementById("validEamilValue").style.display != "block" && document.getElementById("validValue").style.display != "block"&& document.getElementById("validPhoneValue").style.display != "block"){
				document.getElementById("submit").disabled = false;
				document.getElementById("download").disabled = false;				
			}
	    document.getElementById("validOrderIdValue").style.display= "none";
  }
}, 400);
}


</script>


<link rel="stylesheet" href="../css/transactionResult.css">

<style type="text/css">


.selectBox {
	position: relative;
}
.selectBox select {
	width: 95%;
}


#checkboxes2 {
	display: none;
	border: 1px #dadada solid;
	height:180px;
	overflow-y: scroll;
	position:Absolute;
	background:#fff;
	z-index:1;
	padding: 10px;
}

#checkboxes2 label {
  width: 74%;
}
#checkboxes2 input {
  width:18%;

}

#checkboxes3 {
	display: none;
	border: 1px #dadada solid;
    height: 180px;
    /* width: 142px; */
    overflow-y: scroll;
    position: Absolute;
    background: #fff;
    z-index: 1;
    padding: 10px;
    
}

#checkboxes3 label {
  width: 74%;
}
#checkboxes3 input {
  width:18%;

}

#checkboxes4 {
	display: none;
	border: 1px #dadada solid;
    height: 180px;
    /* width: 142px; */
    overflow-y: scroll;
    position: Absolute;
    background: #fff;
    z-index: 1;
    padding: 10px;
    
}

#checkboxes4 label {
  width: 74%;
}
#checkboxes4 input {
  width:18%;

}

.overSelect {
  position: absolute;
  left: 0;
  right: 0;
  top: 0;
  bottom: 0;
}
#totalTxnsAmount th{
	color:#496cb6;
}
#totalSettledTxnsAmount th{
	color:#496cb6;
}

</style>
</head>
<body id="mainBody">
    <div id="loader-wrapper" style="width: 100%; height: 100%; display:none;">
	  <div id="loader"></div>
    </div>
	
	 <div class="modal" id="popup" style="overflow-y: auto;">
		<!-- <div > -->
				<!--<div class="wrapper " style="max-height: 590px;"> -->
    
    
					<!-- Navbar -->
				   
					<!-- End Navbar -->
					<div class="content innerpopupDv" >
					  <div class="container-fluid" >
					   
						<div class="row">
						  
						  
							<div class="card ">
							  <div class="card-header " style="display: inline-flex;">
								<h4 class="card-title">Customer Transaction Information
								  <!-- <small class="description">Vertical Tabs</small> -->
								</h4>
								<button style="position: absolute;
								left: 93%;
								border: none;
								background: none;"  id="closeBtn1"><img style="width:20px" src="../image/red_cross.jpg"></button>
								
							  </div>
							  <div class="card-body ">
								<div class="row">
									<div class="col-md-12 ml-auto mr-auto">
								  <div class="col-lg-4 col-md-6">
									<!--
											  color-classes: "nav-pills-primary", "nav-pills-info", "nav-pills-success", "nav-pills-warning","nav-pills-danger"
										  -->
									<ul class="nav nav-pills nav-pills-rose nav-pills-icons flex-column"  role="tablist">
									  <li class="nav-item" id="listitem">
										<a class="nav-link active" data-toggle="tab" href="#link110" role="tablist">
										  <i class="material-icons">money</i> Transaction Information
										</a>
									  </li>
									  <li class="nav-item" id="listitem">
										<a class="nav-link" data-toggle="tab" href="#link111" role="tablist">
										  <i class="material-icons">payment</i> Payment Information
										</a>
									  </li>
									  <li class="nav-item" id="listitem">
										<a class="nav-link" data-toggle="tab" href="#link112" role="tablist">
										  <i class="material-icons">person</i> Customer Information
										</a>
									  </li>
									</ul>
								  </div>
								  <div class="col-md-8">
									<div class="tab-content">
									  <div class="tab-pane active" id="link110">
										
										<h4 style="margin-left:10px;color: #4c55a0 !important;font-weight:600">Transaction Information</h4>
											   <div class="card-body">
											   
												   <table width="100%" class="invoicetable">				
													 <tr>
													   <th  align="left" valign="middle">Amount</th>
													   <th  align="left" valign="middle">Status</th>
													  
													  </tr>
												   
													  <tr id="sec11">
													   <td align="left" valign="middle"></td>
													   <td align="left" valign="middle"></td>
													  
													 </tr>
													 <tr>
													   <th  align="left" valign="middle">Order Id</th>
													   <th  align="left" valign="middle">PG Ref No.</th>
													  
													  </tr>
													   <tr id="sec12">
													   <td align="left" valign="middle"></td>
													   <td align="left" valign="middle"></td>
													   
													 </tr>
													 <tr>
														<th  align="left" valign="middle">ARN No.</th>
														<th  align="left" valign="middle">RRN No.</th>
													   
													   </tr>
														<tr id="sec13">
														<td align="left" valign="middle"></td>
														<td align="left" valign="middle"></td>
														
													  </tr>
													 <tr id="sec14h">
														<th  align="left" valign="middle">ACQ ID.</th>
														<th  align="left" valign="middle">Total Refunded Amount</th>
				
													  </tr>
													  <tr id="sec14">
														<td align="left" valign="middle"></td>
														<td align="left" valign="middle"></td>				
													  </tr>
													  <tr id="sec16h">
														<th  align="left" valign="middle">IP Address.</th>
														<th  align="left" valign="middle">SALE Amount.</th>
													  </tr>
													  <tr id="sec16">
														<td align="left" valign="middle"></td>
														<td align="left" valign="middle"></td>														
													  </tr>
													
											   </table>
											  
												 <!-- <label for="cardMask" class="bmd-label-floating">Card Mask</label>
												 <input type="email" class="form-control" id="cardMask"> -->
											   
											   </div>
											   <div id="accordion" role="tablist">
												<div class="card-collapse">
												  <div class="card-header" role="tab" id="headingOne">
													<h5 class="mb-0">
													  <a id="infotabs" style="display: inline-flex;width: 100%;" data-toggle="collapse" href="#collapseThree" aria-expanded="true" aria-controls="collapseOne" class="collapsed">
														<h4 style="margin-left:10px;color: #4c55a0 !important;font-weight:600;width: 100%;">Trail</h4>
														<i class="material-icons">keyboard_arrow_down</i>
													  </a>
													</h5>
												  </div>
												  <div id="collapseThree" class="collapse show" role="tabpanel" aria-labelledby="headingOne" data-parent="#accordion" style="">
													<div class="card-body">
												
												<table width="100%" class="table table-responsive invoicetable" id="transactionDetails">				
												 <tr>
												 
												   <th  align="left" valign="middle">Order ID</th>
												   <th  align="left" valign="middle">Transaction Type</th>
												   <th  align="left" valign="middle">Date</th>
												   <th  align="left" valign="middle">Status</th>
	
												   
												  </tr>
											   
												  <tr id="sec15">
												   <td align="left" valign="middle"></td>
												   <td align="left" valign="middle"></td>
												   <td align="left" valign="middle"></td>
  												   <td align="left" valign="middle"></td>
												 </tr>
												 <tr id="sec15">
												   <td align="left" valign="middle"></td>
												   <td align="left" valign="middle"></td>
												   <td align="left" valign="middle"></td>
   												   <td align="left" valign="middle"></td>
												 </tr>
												 
												  
										   </table>
										   
													</div>
												  </div>
												</div>
												</div>
											   <!-- <h4 style="margin-left:10px;color: #4c55a0 !important;font-size:15px;font-weight:600">Trail</h4> -->
											
											 
											 
											
										   
									  </div>
									  <div class="tab-pane" id="link111">
										
										  
										 
										 <h4 style="margin-left:10px;color: #4c55a0 !important;font-weight:600">Payment Information</h4>
											<div class="card-body">
											
												<table width="100%" class="invoicetable">

												<tr>
													<th  height="25" colspan="4" align="left" valign="middle">Card Holder Name</th>
												   
												   </tr>
												   <tr id="sec3chn">
													<td height="25" colspan="4" align="left" valign="middle"></td>
												   
												  </tr>	
												  
												  <tr>
													<th  align="left" valign="middle">Card Mask</th>
													<th  align="left" valign="middle">Issuer</th>
												   
												   </tr>
												
												   <tr id="sec3">
													<td align="left" valign="middle"></td>
													<td align="left" valign="middle"></td>
												   
												  </tr>
												  <tr>
													<th  height="25" colspan="4" align="left" valign="middle">MopType</th>
												   
												   </tr>
													<tr id="sec4">
													<td height="25" colspan="4" align="left" valign="middle"></td>
													
												  </tr>
												  <tr>
													<th align="left" valign="middle">Total TDR</th>
													<th align="left" valign="middle">Total GST</th>
													
													</tr>
												  <tr id="sec5">
													<td align="left" valign="middle"></td>
													<td align="left" valign="middle"></td>
													
													</tr>

													<tr>
													  <th height="25" colspan="4" align="left" valign="middle"><span>Acquirer Response</span></th>
			  
													</tr>
													<tr id="address5">
													  <td height="25" colspan="4" align="left" valign="middle"></td>
			  
													</tr>
													<!-- <tr>
													  <th height="25" colspan="4" align="left" valign="middle"><span>Address</span></th>
			  
													</tr>
													<tr id="address6">
													  <td height="25" colspan="4" align="left" valign="middle"></td>
			  
													</tr> -->
											</table>
					
											  <!-- <label for="cardMask" class="bmd-label-floating">Card Mask</label>
											  <input type="email" class="form-control" id="cardMask"> -->
											
											</div>
										 
										  
										  
										 
										</div>
									  <div class="tab-pane" id="link112">
										
										  <div id="accordion" role="tablist">
											<div class="card-collapse">
											  <div class="card-header" role="tab" id="headingOne">
												<h5 class="mb-0">
												  <a id="infotabs" data-toggle="collapse" style="display: inline-flex;
												  width: 100%;" href="#collapseOne" aria-expanded="true" aria-controls="collapseOne" class="collapsed">
													<h4 style="margin-left:10px;color: #4c55a0 !important;font-weight:600;width: 100%;">Billing Address</h4>  
													<i class="material-icons">keyboard_arrow_down</i>
												  </a>
												</h5>
											  </div>
											  <div id="collapseOne" class="collapse show" role="tabpanel" aria-labelledby="headingOne" data-parent="#accordion" style="">
												<div class="card-body">
												
													  <table width="100%"  class="invoicetable">				
														<tr>
														  <th  align="left" valign="middle">Name</th>
														  <th  align="left" valign="middle">Phone No</th>
														 
														 </tr>
													  
														 <tr id="sec1">
														  <td align="left" valign="middle"></td>
														  <td align="left" valign="middle"></td>
														 
														</tr>
														<tr>
														  <th align="left" valign="middle">City</th>
														  <th align="left" valign="middle">State</th>
														 
														 </tr>
														  <tr id="sec2">
														  <td align="left" valign="middle"></td>
														  <td align="left" valign="middle"></td>
														 
														</tr>
														<tr>
													   
														  <th align="left" valign="middle">Country</th>
														  <th align="left" valign="middle">Zip</th>
														 </tr>
														  <tr id="sec7">
														  
														  <td align="left" valign="middle"></td>
														  <td align="left" valign="middle"></td>
														</tr>
														<tr>
														   <th height="25" colspan="4" align="left" valign="middle"><span>Address1</span></th>
														  </tr>
														<tr id="address1">
														   <td height="25" colspan="4" align="left" valign="middle"></td>
														  </tr>
														  <tr>
														   <th height="25" colspan="4" align="left" valign="middle"><span>Address2</span></th>
														  </tr>
														<tr id="address2">
														   <td height="25" colspan="4" align="left" valign="middle"></td>
														  </tr>
													   
														
												  </table>
													
												</div>
											  </div>
											</div>
											<div class="card-collapse">
											  <div class="card-header" role="tab" id="headingTwo">
												<h5 class="mb-0">
												  <a  id="infotabs" class="collapsed" style="display: inline-flex;
												  width: 100%;" data-toggle="collapse" href="#collapseTwo" aria-expanded="false" aria-controls="collapseTwo">
													<h4 style="margin-left:10px;color: #4c55a0 !important;font-weight:600;width:100%">Shipping Address</h4> 
													<i class="material-icons">keyboard_arrow_down</i>
												  </a>
												</h5>
											  </div>
											  <div id="collapseTwo" class="collapse" role="tabpanel" aria-labelledby="headingTwo" data-parent="#accordion">
												<div class="card-body">
												 
													  <table width="100%"  class="invoicetable">				
														<tr>
														  <th  align="left" valign="middle">Name</th>
														  <th  align="left" valign="middle">Phone No</th>
														 
														 </tr>
													  
														 <tr id="sec8">
														  <td align="left" valign="middle"></td>
														  <td align="left" valign="middle"></td>
														 
														</tr>
														<tr>
														  <th align="left" valign="middle">City</th>
														  <th align="left" valign="middle">State</th>
														 
														 </tr>
														  <tr id="sec9">
														  <td align="left" valign="middle"></td>
														  <td align="left" valign="middle"></td>
														 
														</tr>
														<tr>
													   
														  <th align="left" valign="middle">Country</th>
														  <th align="left" valign="middle">Zip</th>
														 </tr>
														  <tr id="sec10">
														  
														  <td align="left" valign="middle"></td>
														  <td align="left" valign="middle"></td>
														</tr>
														<tr>
														   <th height="25" colspan="4" align="left" valign="middle"><span>Address1</span></th>
														  </tr>
														<tr id="address3">
														   <td height="25" colspan="4" align="left" valign="middle"></td>
														  </tr>
														  <tr>
														   <th height="25" colspan="4" align="left" valign="middle"><span>Address2</span></th>
														  </tr>
														<tr id="address4">
														   <td height="25" colspan="4" align="left" valign="middle"></td>
														  </tr>
													   
														
												  </table>
													
												</div>
											  </div>
											</div>
										   
										  </div>
			  
			  
									   
										
									  </div>
									</div>
									<div class="card-footer " style="float: right;">
									  <button class="btn btn-danger" id="closeBtn">Close<div class="ripple-container"></div></button>
									  <!-- <button type="submit" class="btn btn-fill btn-rose" id="closeBtn">Close</button> -->
									</div>
									<!-- <div style="text-align: center;"><button class="btn btn-danger" id="closeBtn">Close</button></div>	 -->
								  </div>
								</div>
							  </div>
							</div>
						  </div>
						</div>
						
					  </div>
					</div>
				   
				  
				<!-- </div>									       
		</div>-->
	</div> 

	
   <div style="overflow:scroll !important;">
	<table id="mainTable" width="100%" border="0" align="center"
		cellpadding="0" cellspacing="0" class="txnf">
		<!-- <tr>
			<td colspan="5" align="left"><h2>Search Payments</h2></td>
		</tr> -->
		<tr>
		
			<td colspan="5" align="left" valign="top"><div class="MerchBx">
				<div class="col-md-12">
					<div class="card ">
					  <div class="card-header card-header-rose card-header-text">
						<div class="card-text">
						  <h4 class="card-title">Search Payments</h4>
						</div>
					  </div>
					  <div class="card-body ">
						<div class="container">
						  <div class="row">
							 
								  <div class="col-sm-6 col-lg-3" >
									<label>PG REF Number :</label><br>
									<div class="txtnew">
										<s:textfield id="pgRefNum" class="input-control"
								name="pgRefNum" type="text" value="" autocomplete="off"
								maxlength="16" onblur="validPgRefNum();" ondrop="return false;" onKeyDown="if(event.keyCode === 32)return false;" onpaste="removeSpaces(this);"></s:textfield>
						</div>
						<span id="validRefNo" style="color:red; display:none;">Please Enter 16 Digit PG Ref No.</span>
						<span id="validValue" style="color:red; display:none;">Please Enter Valid PG Ref No.</span>
								  </div>
								  <div class="col-sm-6 col-lg-3" >
									<label>Order ID:</label><br>
									
									<div class="txtnew">
										<s:textfield id="orderId" class="input-control" name="orderId"
								type="text" value="" autocomplete="off"
								onKeyDown="validateOrderIdvalue(this);" onkeypress="return validateOrderId(event);"  ondrop="return false;" onpaste="validateOrderIdvalue(this);" maxlength="50"></s:textfield>
									</div>
									<span id="validOrderIdValue" style="color:red; display:none;">Please Enter Valid orderId</span>
								  </div>
								  <div class="col-sm-6 col-lg-3">
									<label>Customer Email :</label><br>
									<div class="txtnew">
										<s:textfield id="customerEmail" class="input-control"
								name="customerEmail" type="text" value="" autocomplete="off" ondrop="return false;"
								onblur="validateCustomerEmail(this);" onKeyDown="if(event.keyCode === 32)return false;" onpaste="removeSpaces(this);"></s:textfield>
									</div>
								<span id="validEamilValue" style="color:red; display:none;">Please Enter Valid Email.</span>	
								</div>
								<div class="col-sm-6 col-lg-3">
									<label>Customer Phone :</label><br>
									<div class="txtnew">
										<s:textfield id="custPhone" class="input-control noSpace"
								name="custPhone" type="text" value="" autocomplete="off" onblur="validateCustomerPhone(this);" onKeyDown="if(event.keyCode === 32)return false;" ondrop="return false;" onpaste="removeSpaces(this);"  maxlength="14"></s:textfield>
						</div>
						<span id="validPhoneValue" style="color:red; display:none;">Please Enter Valid Phone No.</span>
								</div>
								<div class="col-sm-6 col-lg-3">
									<label>Merchant</label><br>
									<div class="txtnew">
										<s:if
								test="%{#session.USER.UserType.name()=='ADMIN' || #session.USER.UserType.name()=='SUBADMIN' || #session.USER_TYPE.name()=='SUPERADMIN'}">
								<s:select name="merchant" class="input-control adminMerchants" id="merchant"
									headerKey="" headerValue="ALL" list="merchantList"
									listKey="emailId" listValue="businessName" autocomplete="off" />
							</s:if>
							<s:else>
								<s:select name="merchant" class="input-control" id="merchant" list="merchantList"
									listKey="emailId" listValue="businessName" autocomplete="off" />
							</s:else>
									</div>

								</div>
								<div class="col-sm-6 col-lg-3">
									<label>Payment Method </label><br>
									<div class="txtnew">
										<div class="selectBox" id="selectBox3" onclick="showCheckboxes(event,3)" title="ALL">
											<select class="input-control">
												<option>ALL</option>
											</select>
											<div class="overSelect"></div>
								</div>
								<div id="checkboxes3" onclick="getCheckBoxValue(3)">
										
									<s:checkboxlist headerKey="ALL" headerValue="ALL" class="myCheckBox3"
										list="@com.pay10.commons.util.PaymentTypeUI@values()"
										listValue="name" listKey="code" name="paymentMethod"
										id="paymentMethod" autocomplete="off" value="paymentMethod" />
								
								</div>
									</div>

								</div>
							 
									<div class="col-sm-6 col-lg-3">
								  <label>Mop Type</label><br>
								  <div class="txtnew">
									<div class="selectBox" id="selectBox2" onclick="showCheckboxes(event,2)" title="ALL">
										<select class="input-control">
											<option>ALL</option>
										</select>
										<div class="overSelect"></div>
									</div>
									<div id="checkboxes2" onclick="getCheckBoxValue(2)">
									
									<s:checkboxlist name="mopType" id="mopType" headerValue="ALL"
							headerKey="ALL" list="@com.pay10.commons.util.MopTypeUI@values()"
							listValue="name" listKey="uiName" class="myCheckBox2" value="mopType"/>
							
									</div>
									</div>
								</div>
								<div class="col-sm-6 col-lg-3" >
								  <label>Transaction Type</label><br>
								  
								  <div class="txtnew">
									<s:select headerKey="" headerValue="ALL" class="input-control"
								list="txnTypelist"
								listValue="name" listKey="code" name="transactionType"
								id="transactionType" autocomplete="off" value="name" />
								</div>
								</div>
								<div class="col-sm-6 col-lg-3">
								  <label>Status</label><br>
								  <div class="txtnew">
									<div class="selectBox" id="selectBox4" onclick="showCheckboxes(event,4)" title="ALL">
										<select class="input-control">
											<option>ALL</option>
										</select>
										<div class="overSelect"></div>
									</div>
									<div id="checkboxes4" onclick="getCheckBoxValue(4)">
						<s:checkboxlist headerKey="ALL" headerValue="ALL" list="@com.pay10.commons.util.StatusTypeUI@values()" name="status" id="status" value="status" listKey="code" listValue="name" class="myCheckBox4" />
									</div>
								</div>
							  </div>
							  <div class="col-sm-6 col-lg-3">
								  <label>Currency</label><br>
								  <div class="txtnew">
									<s:select name="currency" id="currency" headerValue="ALL"
									headerKey="" list="currencyMap" class="input-control" />
								</div>

							  </div>
							  <div class="col-sm-6 col-lg-3">
								  <label>Date From: </label><br>
								  <div class="txtnew">
									<s:textfield type="text" id="dateFrom" name="dateFrom" class="input-control" autocomplete="off"  onkeyup="checkFromDate()" maxlength="10" onblur="compareDate()"/>
							<span id="dateError" style="color:red; display:none;">Please Enter Valid Date </span>
							<span id="showErr1" style="color:red; display:none;">Please Don't Enter Future Date</span>
								</div>

							  </div>
							  <div class="col-sm-6 col-lg-3">
								  <label>Date To :</label><br>
								  <div class="txtnew">
									<s:textfield type="text" id="dateTo" name="dateTo" class="input-control" autocomplete="off" 
							onkeyup="checkToDate()" maxlength="10" onblur="compareDate()"/>
							<span id="dateError1" style="color:red; display:none;">Please Enter Valid Date </span>
							<span id="showErr2" style="color:red; display:none;">Please Don't Enter Future Date</span>
								</div>

							  </div>
						
							
	
								<div class="col-sm-6 col-lg-3" style="display: inline-flex;">
		
									<div class="txtnew" style="margin-right: 15px;">
										<input type="button" id="submit" value="View"
										
											class="btn btn-primary  mt-4 submit_btn">
											
									</div>
									<div class="txtnew">
										<input type="button" id="download" value="Download"
											class="btn btn-primary  mt-4 submit_btn" onclick="downloadReport()">
											
									</div>
								
					</div>
					
							
							
							
						  </div>
						  </div>
						  
					
					  </div>
					</div>
				  </div>
					
					
				</div>

			</td>
		</tr>
		<tr>
			<td colspan="5" align="left"><h2>&nbsp;</h2></td>
		</tr>
		

	</table>
	<tr>
		<td align="left" style="padding: 10px;">
			<div class="scrollD">
				<table id="txnResultDataTable" class="display nowrap" cellspacing="0"
					width="100%">
					<thead>
						<tr class="boxheadingsmall">
							<th style='text-align: center' class="my_class">Txn Id</th>
							<th style='text-align: center'>Pg Ref Num</th>
							<th style='text-align: center'>Merchant</th>
							<th style='text-align: center'>Date</th>
							<th style='text-align: center'>Order Id</th>
							<th style='text-align: center'>Refund Order Id</th>
							<th style='text-align: center'>Mop Type</th>
							<th style='text-align: center'>Payment Method</th>
							<th style='text-align: center'>Txn Type</th>
							<th style='text-align: center'>Status</th>
							
							<th style='text-align: center' >Base Amount</th>
						 <th style='text-align: center' >Total Amount</th>
							 <th style='text-align: center' >Pay ID</th>
							 <th style='text-align: center' >Customer Email</th>
							 <th style='text-align: center' >Customer Ph Number</th>								
							
														
							<th></th>
						</tr>
					</thead>
					<tfoot>
						<tr class="boxheadingsmall">
							<th></th>
							<th></th>
							<th></th>
							<th></th>
							<th></th>
							<th></th>
							<th></th>
							<th></th>
							<th></th>
							<th></th>
							<th></th>
							<th></th>
							
							<th></th>
						 <th></th>
							<th></th>
							<th></th>
					
							
							<th></th>
						
						</tr>
					</tfoot>
				</table>
			</div>
		</td>
	</tr>
	<s:form id="downloadPaymentReport" name="downloadPaymentsReportAction" action="downloadPaymentsReportAction">
		<s:hidden name="currency" id="currencyH" value="" />
		<s:hidden name="dateFrom" id="dateFromH" value="" />
		<s:hidden name="dateTo" id="dateToH" value="" />
		<s:hidden name="merchantPayId" id="merchantPayIdH" value="" />
		<s:hidden name="transactionType" id="transactionTypeH" value="" />
		<s:hidden name="paymentType" id="paymentTypeH" value="" />		
		<s:hidden name="status" id="statusH" value="" />
		<s:hidden name="acquirer" id="acquirerH" value="" />
		<s:hidden name="transactionId" id="transactionIdH" value="" />
		<s:hidden name="orderId" id="orderIdH" value="" />
		<s:hidden name="customerEmail" id="customerEmailH" value="" />		
		<s:hidden name="customerPhone" id="customerPhoneH" value="" />
		<s:hidden name="mopType" id="mopTypeH" value="" />
		<s:hidden name="token" value="%{#session.customToken}" />		
	</s:form>


  </div>
 
<div class="card-body">
	<br>
	<div class="card ">
		<div class="card-header card-header-rose card-header-text">
		  <div class="card-text">
			<h4 class="card-title">Initiated </h4>
		  </div>
		</div>												
		<div class="card-body ">
			<div class="container">
			  <div class="row">												
	<!-- <label>Initiated :</label><br>												 -->
	<table width="100%" class="table table-responsive" id="totalTxnsAmount">																 												  
	</table>
	</div>
	</div>
	</div>
	</div>

<br>
<div class="card ">
	<div class="card-header card-header-rose card-header-text">
	  <div class="card-text">
		<h4 class="card-title">Settled </h4>
	  </div>
	</div>	
	<div class="card-body ">
		<div class="container">
		  <div class="row">
<!-- <label>Settled :</label><br>												 -->
<table width="100%" class="table table-responsive" id="totalSettledTxnsAmount">																 												  
</table>
</div>
</div>
</div>
</div>
<br>
<s:a action='settledTransactionSearch' class="btn btn-primary  mt-4 submit_btn">Settled Report
<!-- <label style="display:inline-block;margin-right:10px;"></label> -->
</s:a>

<!-- <label style="display:inline-block;margin-right:90px;">Settled Dashboard</label> -->

</div> 
		
		<s:form name="chargeback" action="chargebackAction">
		<s:hidden name="orderId" id="orderIdc" value="" />
		<s:hidden name="payId" id="payIdc" value="" />
		
		<s:hidden name="txnId" id="txnIdc" value="" />
		<s:hidden name="token" value="%{#session.customToken}" />
	</s:form>
	
	
	<s:form name="refundDetails" action="refundConfirmAction">
		<s:hidden name="orderId" id="orderIdr" value="" />
		<s:hidden name="payId" id="payIdr" value="" />
		<s:hidden name="transactionId" id="txnIdr" value="" />
		<s:hidden name="token" value="%{#session.customToken}" />
	</s:form>
	
	
	
<script type="text/javascript">
$(document).ready(function(){
	
	$(document).click(function(){
		expanded = false;
		$('#checkboxes2').hide();
		$('#checkboxes3').hide();		
		$('#checkboxes4').hide();		
	});
	$('#checkboxes2').click(function(e){
		e.stopPropagation();
	});
	$('#checkboxes3').click(function(e){
		e.stopPropagation();
	});
	$('#checkboxes4').click(function(e){
		e.stopPropagation();
	});
	$('#closeBtn').click(function(){
		$('#popup').hide();
	});
	$('#closeBtn1').click(function(){
		$('#popup').hide();
	});

});

<!--------Validation to remove space on paste in ORDER Id---->
$(document).on('paste', '#orderId', function(e) {
  e.preventDefault();
  // prevent copying action
  var withoutSpaces = e.originalEvent.clipboardData.getData('Text');
  //withoutSpaces = withoutSpaces.replace(/\s+/g, '');
  withoutSpaces = withoutSpaces.trim();
  $(this).val(withoutSpaces);
});
</script>

<!------------Validation to check Date Format on Type------->
<script>

var expanded = false;

function showCheckboxes(e,uid) {
  var checkboxes = document.getElementById("checkboxes"+uid);
  if (!expanded) {
    checkboxes.style.display = "block";
    expanded = true;
  } else {
    checkboxes.style.display = "none";
    expanded = false;
  }
   e.stopPropagation();

}

function getCheckBoxValue(uid){
	 var allInputCheckBox = document.getElementsByClassName("myCheckBox"+uid);
  		
  		var allSelectedAquirer = [];
  		for(var i=0; i<allInputCheckBox.length; i++){
  			
  			if(allInputCheckBox[i].checked){
  				allSelectedAquirer.push(allInputCheckBox[i].value);	
  			}
  		}
		var test = document.getElementById('selectBox'+uid);
  		document.getElementById('selectBox'+uid).setAttribute('title', allSelectedAquirer.join());
  		if(allSelectedAquirer.join().length>28){
  			var res = allSelectedAquirer.join().substring(0,27);
  			document.querySelector("#selectBox"+uid+" option").innerHTML = res+'...............';
  		}else if(allSelectedAquirer.join().length==0){
  			document.querySelector("#selectBox"+uid+" option").innerHTML = 'ALL';
  		}else{
  			document.querySelector("#selectBox"+uid+" option").innerHTML = allSelectedAquirer.join();
  		}
}

var currentDate;

function checkFromDate(){
	var dateRegex = /^(0?[1-9]|[12][0-9]|3[01])[\/\-](0?[1-9]|1[012])[\/\-]\d{4}$/;
	
	var fromDateVal = document.getElementById("dateFrom").value;
	if (fromDateVal.trim() != "" && fromDateVal.trim() != null){		  
		if (!fromDateVal.match(dateRegex)) {
			document.getElementById("dateError").style.display = "block";
			document.getElementById("submit").disabled = true;
			return false;
		} else {
			document.getElementById("dateError").style.display = "none";
			document.getElementById("submit").disabled = false;
			return true;
		}	      
	}  else {
			document.getElementById("dateError").style.display = "none";
			document.getElementById("submit").disabled = false;
			return true;
		}
}

function createDate(strDate) {
	var arrDate = strDate.split("-");
	return arrDate[1] + "-" + arrDate[0] + "-" + arrDate[2];
}
function compareDate(){
	var firstDate = new Date(createDate(document.getElementById("dateFrom").value));
	var toDate = new Date(createDate(document.getElementById("dateTo").value));
	currentDate = new Date();
	if(checkFromDate() && checkToDate()) {
		if(firstDate > currentDate){
			document.getElementById("showErr1").style.display = "block";
			document.getElementById("submit").disabled = true;
			return false;
		} else { 
			document.getElementById("showErr1").style.display = "none";
			document.getElementById("submit").disabled = false;		
			return true;
		} 
	}
	if(checkFromDate() && checkToDate()) {
		if(toDate > currentDate){
			document.getElementById("showErr2").style.display = "block";
			document.getElementById("submit").disabled = true;
			return false;
		}
		else{
			document.getElementById("showErr2").style.display = "none";
			document.getElementById("submit").disabled = false;
			return true;
		}
	}
}
	
function checkToDate(){
	var dateRegex = /^(0?[1-9]|[12][0-9]|3[01])[\/\-](0?[1-9]|1[012])[\/\-]\d{4}$/;
	
	var toDateVal = document.getElementById("dateTo").value;
	if (toDateVal.trim() != "" && toDateVal.trim() != null){	   
		if (!toDateVal.match(dateRegex)) {
			document.getElementById("dateError1").style.display = "block";
			document.getElementById("submit").disabled = true;
			return false;
		} else {
			document.getElementById("dateError1").style.display = "none";
			document.getElementById("submit").disabled = false;
			return true;
		}
	} else {
			document.getElementById("dateError1").style.display = "none";
			document.getElementById("submit").disabled = false;
			return true;
	}		 
}  


	  
</script>
	
</body>
</html>