<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Summary Report</title>
<link href="../css/Jquerydatatableview.css" rel="stylesheet" />
<link href="../css/Jquerydatatable.css" rel="stylesheet" />
<link href="../css/default.css" rel="stylesheet" type="text/css" />
<link href="../css/jquery-ui.css" rel="stylesheet" />
<script src="../js/jquery.min.js"></script>
<script src="../js/core/popper.min.js"></script>
<script src="../js/core/bootstrap-material-design.min.js"></script>
<script src="../js/plugins/perfect-scrollbar.jquery.min.js"></script>
<!-- Control Center for Material Dashboard: parallax effects, scripts for the example pages etc -->
<script src="../js/material-dashboard.js?v=2.1.0" type="text/javascript"></script>
<script src="../js/jquery.dataTables.js"></script>
<script src="../js/jquery-ui.js"></script>
<script src="../js/commanValidate.js"></script>
<script type="text/javascript" src="../js/moment.js"></script>
<script type="text/javascript" src="../js/daterangepicker.js"></script>
<script src="../js/jquery.popupoverlay.js"></script>
<script type="text/javascript" src="../js/dataTables.buttons.js"></script>
<script type="text/javascript" src="../js/pdfmake.js"></script>
<script type="text/javascript" src="../js/summaryReport.js"></script>
<link href="../css/loader.css" rel="stylesheet" type="text/css" />
<script src="../js/select2.min.js"></script>
<link href="../css/select2.min.css" rel="stylesheet" />
<script src="../js/jquery.select2.js" type="text/javascript"></script>

<script type="text/javascript">
	
$(document).ready(function(){
 
  // Initialize select2
     document.getElementById("loadingInner").style.display = "none";
   document.getElementById("loading").style.display = "none";
  //$("#merchants").select2();
});
</script>

<script type="text/javascript">


	function handleChange() {
		
	}
	$(document)
			.ready(
					function() {
						$(function() {

							$("#dateFrom").datepicker({
								prevText : "click for previous months",
								nextText : "click for next months",
								showOtherMonths : true,
								dateFormat : 'dd-mm-yy',
								selectOtherMonths : false,
								maxDate : new Date()
							});
							$("#dateTo").datepicker({
								prevText : "click for previous months",
								nextText : "click for next months",
								showOtherMonths : true,
								dateFormat : 'dd-mm-yy',
								selectOtherMonths : false,
								maxDate : new Date()
							});
						});
						$(function() {
							var today = new Date();
							$('#dateTo').val(
									$.datepicker.formatDate('dd-mm-yy', today));
							$('#dateFrom').val(
									$.datepicker.formatDate('dd-mm-yy', today));
							renderTable();
						});


$("#submit").click(function(env) {
	$('#loader-wrapper').show();
			reloadTable();
		});
$(function() {
			var datepick = $.datepicker;
			var table = $('#summaryReportDataTable').DataTable();
			var tableCount = $('#summaryReportCountDataTable').DataTable();
			$('#summaryReportDataTable tbody').on('click', 'td', function() {

				popup(table, this);
			});
			$('#summaryReportCountDataTable tbody').on('click', 'td', function() {

				popup(tableCount, this);
			});
		});
		
		
		$(function() {
		
		});
	});
					
					
				
	function renderTable() {
		var monthVal = parseInt(new Date().getMonth())+1;
		var merchantEmailId = document.getElementById("merchants").value;
		var table = new $.fn.dataTable.Api('#summaryReportDataTable');
		//to show new loader -Harpreet
		$.ajaxSetup({
			global : false,
			beforeSend : function() {
				toggleAjaxLoader();
			},
			complete : function() {
				toggleAjaxLoader();
			}
		});
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
		var token = document.getElementsByName("token")[0].value;
		
		 var buttonCommon = {
			        exportOptions: {
			            format: {
			                body: function ( data, column, row, node ) {
			                    // Strip $ from salary column to make it numeric
			                    return column === 0 ? "'"+data : (column === 3 ? "'" + data: data);
			                }
			            }
			        }
			    };
				
		$('#summaryReportDataTable')
				.dataTable(
						{
							//		dom : 'T<"clear">lfrtip',
							"footerCallback" : function(row, data, start, end,
									display) {
								var api = this.api(), data;

								// Remove the formatting to get integer data for summation
								var intVal = function(i) {
									return typeof i === 'string' ? i.replace(
											/[\,]/g, '') * 1
											: typeof i === 'number' ? i : 0;
								};

								// Total over all pages
								total = api.column(3).data().reduce(
										function(a, b) {
											return intVal(a) + intVal(b);
										}, 0);

								// Total over this page
								pageTotal = api.column(3, {
									page : 'current'
								}).data().reduce(function(a, b) {
									return intVal(a) + intVal(b);
								}, 0);

								// Update footer
								$(api.column(3).footer()).html(
										'' + inrFormat(pageTotal.toFixed(2) + ' ' + ' '));

								// Total over all pages
								total = api.column(4).data().reduce(
										function(a, b) {
											return intVal(a) + intVal(b);
										}, 0);

								// Total over this page refund
								pageTotal = api.column(4, {
									page : 'current'
								}).data().reduce(function(a, b) {
									return intVal(a) + intVal(b);
								}, 0);

								// Update footer
								$(api.column(4).footer()).html(
										'' + inrFormat(pageTotal.toFixed(2) + ' ' + ' '));

								// Total over all pages
								total = api.column(8).data().reduce(
										function(a, b) {
											return intVal(a) + intVal(b);
										}, 0);

								// Total over this page
								pageTotal = api.column(8, {
									page : 'current'
								}).data().reduce(function(a, b) {
									return intVal(a) + intVal(b);
								}, 0);

								// Update footer
								$(api.column(8).footer()).html(
										'' + inrFormat(pageTotal.toFixed(2) + ' ' + ' '));

								// Total over all pages
								total = api.column(12).data().reduce(
										function(a, b) {
											return intVal(a) + intVal(b);
										}, 0);

								// Total over this page
								pageTotal = api.column(12, {
									page : 'current'
								}).data().reduce(function(a, b) {
									return intVal(a) + intVal(b);
								}, 0);

								// Update footer
								$(api.column(12).footer()).html(
										'' + inrFormat(pageTotal.toFixed(2) + ' ' + ' '));
										
																		// Total over all pages
								total = api.column(13).data().reduce(
										function(a, b) {
											return intVal(a) + intVal(b);
										}, 0);

								// Total over this page
								pageTotal = api.column(13, {
									page : 'current'
								}).data().reduce(function(a, b) {
									return intVal(a) + intVal(b);
								}, 0);

								// Update footer
								$(api.column(13).footer()).html(
										'' + inrFormat(pageTotal.toFixed(2) + ' ' + ' '));
										
																		// Total over all pages
								total = api.column(13).data().reduce(
										function(a, b) {
											return intVal(a) + intVal(b);
										}, 0);

								// Total over this page
								pageTotal = api.column(13, {
									page : 'current'
								}).data().reduce(function(a, b) {
									return intVal(a) + intVal(b);
								}, 0);

								// Update footer
								$(api.column(13).footer()).html(
										'' + inrFormat(pageTotal.toFixed(2) + ' ' + ' '));
							},

							"columnDefs" : [ {
								className : "dt-body-right",
								"targets" : [ 0, 1, 2, 3, 4, 5, 8, 9 ]
							} ],
							dom : 'BTftlpi',
							buttons : [
								$.extend( true, {}, buttonCommon, {
										extend : 'copyHtml5',
										//footer : true,
										exportOptions : {
											columns : [':visible']
										}
									}),
									$.extend( true, {}, buttonCommon, {
										extend : 'csvHtml5',
										//footer : true,
										title : 'Summary_Report_'+(new Date().getFullYear())+(monthVal>9?monthVal:'0'+monthVal)+(new Date().getDate()>9?new Date().getDate():'0'+new Date().getDate())+(new Date().getHours()>9?new Date().getHours():'0'+new Date().getHours())+(new Date().getMinutes()>9?new Date().getMinutes():'0'+new Date().getMinutes())+(new Date().getSeconds() >9?new Date().getSeconds():'0'+new Date().getSeconds()),
										exportOptions : {
											columns : [':visible']
										}
									}),
									
									{
										extend : 'colvis',
										//           collectionLayout: 'fixed two-column',
										columns : [ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 ]
									} ],
							"ajax" : {
								"url" : "summaryReportAction",
								"type" : "POST",
								"data" : function(d) {
									return generatePostData(d);
								}
							},
							"fnDrawCallback" : function() {

									 $("#submit").removeAttr("disabled");
									 $('#loader-wrapper').hide();
							},
							"searching" : false,
							"processing" : true,
							"serverSide" : true,
							"paginationType" : "full_numbers",
							"lengthMenu" : [ [ 10, 25, 50, -1 ],
									[ 10, 25, 50, "All" ] ],
							//"order" : [ [ 1, "desc" ] ],
							"order" : [],
							"columnDefs" : [ {
								"type" : "html-num-fmt",
								"targets" : 4,
								"orderable" : false,
								"targets" : [ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13]
							} ],
							"columns" : [
									
									{
										"data" : "srNo",
										"width" : "20%",
									},
									{
										"data" : "pgRefNum",
										"width" : "20%",
									},
									{
										"data" : "orderId",
										"width" : "30%"
									},
									{
										"data" : "amount",
										"render" : function(data){
									return inrFormat(data);
								}
									},
									{
										"data" : "totalAmount",
										"render" : function(data){
									return inrFormat(data);
								}
									},
									{
										"data" : "txnType",
									},
									{
										"data" : "transactionCaptureDate",
									},
									{
										"data" : "dateFrom",
									},
									{
										"data" : "netMerchantPayableAmount",
										"render" : function(data){
									return inrFormat(data);
								}
										
									},{
										"data" : "paymentMethods",
									},
								
									{
										"data" : "mopType",
									},
									
									{
										"data" : "currency"
									},
									{
										"data" : "totalGstOnMerchant",
										"render" : function(data){
									return inrFormat(data);
								}

									},
									{
										"data" : "merchantTdrCalculate",
										"render" : function(data){
									return inrFormat(data);
								}

									},
									{
										"data" : "acqId"

									},
									{
										"data" : "rrn",
									},
									{
										"data" : "transactionRegion",
									},
									{
										"data" : "cardMask",
									},
									{
										"data" : "postSettledFlag",
									},
									{
										"data": "refundButtonName",
										render: function (data) {
										return `<input type="button" name="reufndBtn" value=`+data.replace(/ /g, '&nbsp;')+`
											data-toggle="modal" data-target="#refundAccept" onclick = "setValues(this)"
											class="btn btn-lg btn-success" /> `;
													
										}
									}
									
									 ]
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
			alert('From date must be before the  To date');
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

		var tableObj = $('#summaryReportDataTable');
		var table = tableObj.DataTable();
		table.ajax.reload();

		
	}
	
	function refundFunction(operation) {
		
		
		var ordId = document.getElementById("orderIDConf").value;
		var refOrdId = document.getElementById("refundOrderIDConf").value;
		var pgRef = document.getElementById("pgRefConf").value;
		var amt = document.getElementById("amtConf").value.replace(/,/g, '').trim();
		var currency = document.getElementById("currencyConf").value;
		var token = document.getElementsByName("token")[0].value;
		var availAmt = document.getElementById("amtAvail").value.replace(/,/g, '').trim();
		
		if (amt == '' || amt < 0){
			
			alert("Please enter a valid Refund amount!");
			return false;
		}
		
		if(parseFloat(amt) > parseFloat(availAmt)){
			alert("Please check the Amount field, Refund amount More than the Available amount");
			return false;
		}
		
	
		document.getElementById("loadingInner").style.display = "block";
		document.getElementById("btnRefundConf").disabled = true;
		 $.ajax({
			type: "POST",
			url:"refundFromCrm",
			timeout : 0,
			data:{"orderId":ordId,"currencyCode":currency,"refundOrderId":refOrdId,"refundAmount":amt,"pgRefNum":pgRef,"token":token,"struts.token.name": "token",},
			success:function(data){
				
				document.getElementById("loadingInner").style.display = "none";
				document.getElementById("btnRefundConf").disabled = false;
				var response = ((data["Invalid request"] != null) ? (data["Invalid request"].response[0]) : (data.response));
				if(null!=response){
					alert(response);			
				}
			
				window.location.reload();
		    },
			error:function(data){
				
				document.getElementById("loadingInner").style.display = "none";
				document.getElementById("btnRefundConf").disabled = false;
				alert("Unable to process refund");
			}
		    
		}); 
	}
	
		function reloadCountTable() {
		var datepick = $.datepicker;
		var transFrom = $.datepicker
				.parseDate('dd-mm-yy', $('#dateFrom').val());
		var transTo = $.datepicker.parseDate('dd-mm-yy', $('#dateTo').val());
		if (transFrom == null || transTo == null) {
			alert('Enter date value');
			return false;
		}

		if (transFrom > transTo) {
			alert('From date must be before the  To date');
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

		var tableObj = $('#summaryReportCountDataTable');
		var table = tableObj.DataTable();
		table.ajax.reload();
	}
		function generatePostData(d) {
				var token = document.getElementsByName("token")[0].value;

		var aquirer=[];
		var inputElements = document.getElementsByName('aquirer');
		for(var i=0; inputElements[i]; ++i){
			  if(inputElements[i].checked){	
				   aquirer.push( inputElements[i].value);
				  
			  }
		}		
		var merchantEmailId = document.getElementById("merchants").value;
		var	paymentMethods = document.getElementById("paymentMethods").value;
		var	mopType = document.getElementById("mopType").value;
		var currency = document.getElementById("currency").value;
		var paymentsRegion = document.getElementById("paymentsRegion").value;
		var cardHolderType = document.getElementById("cardHolderType").value;
		var pgRefNum = document.getElementById("pgRefNum").value;
		var transactionType = document.getElementById("transactionType").value;
		if(merchantEmailId==''){
			merchantEmailId='ALL'
		}
		if(paymentMethods==''){
			paymentMethods='ALL'
		}
		
		if(currency==''){
			currency='ALL'
		}
		if(paymentsRegion==''){
			paymentsRegion='ALL'
		}
		if(cardHolderType==''){
			cardHolderType='ALL'
		}
		if(transactionType==''){
			transactionType='ALL'
		}

		var obj = {

			paymentMethods : paymentMethods,
			dateFrom : document.getElementById("dateFrom").value,
			dateTo : document.getElementById("dateTo").value,
			phoneNo :document.getElementById("phoneNo").value,
			merchantEmailId : merchantEmailId,
			currency : currency,
			mopType : mopType,
			transactionType : transactionType,
			paymentsRegion : paymentsRegion,
			cardHolderType : cardHolderType,
			pgRefNum : pgRefNum,
			draw : d.draw,
			length :d.length,
			start : d.start, 
			token : token,
			"struts.token.name" : "token",
		};

		return obj;
	}

</script>

<script type="text/javascript">
var totalRefund = "0.00";
function setValues(val) {
	
	var row = val.parentElement.parentElement.cells;
	
		var token = document.getElementsByName("token")[0].value;
		var myData = {
			token : token,
			"struts.token.name" : "token",
			"orderId":row[2].innerText
		}
		
		var txnType = row[5].innerText;
		
		$.ajax({
		    	url: "totalRefundByOrderIdAction",
				timeout : 0,
		    	type : "POST",
		    	data :myData,
		    	success: function(response){
					totalRefund	= response.totalRefund;
	
	document.getElementById("pgRefConf").value = row[1].innerText;
	document.getElementById("orderIDConf").value = row[2].innerText;
	document.getElementById("refundOrderIDConf").value = '';
	var amtRefund = inrFormat(parseFloat(totalRefund.replace(/,/g, '')));
	var amtAvailable = inrFormat(parseFloat(row[3].innerText.replace(/,/g, ''))-parseFloat(totalRefund.replace(/,/g, '')));
	
	document.getElementById("amtRef").value = amtRefund.toString().includes('.') ? inrFormat(parseFloat(totalRefund.replace(/,/g, ''))) : inrFormat(parseFloat(totalRefund.replace(/,/g, '')))+".00";
	document.getElementById("amtAvail").value = amtAvailable.toString().includes('.') ? inrFormat(parseFloat(row[3].innerText.replace(/,/g, ''))-parseFloat(totalRefund.replace(/,/g, ''))) : inrFormat(parseFloat(row[3].innerText.replace(/,/g, ''))-parseFloat(totalRefund.replace(/,/g, '')))+".00";
	
	document.getElementById("amtConf").value = document.getElementById("amtAvail").value;
	document.getElementById("currencyConf").value = row[11].innerText;
	
		if(parseFloat("0.00") == parseFloat(amtAvailable) || txnType == "REFUND"){
			document.getElementById("btnRefundConf").disabled = true;
		}else{
			document.getElementById("btnRefundConf").disabled = false;
		}
		    	},
		    	error: function(xhr, textStatus, errorThrown){
			       alert("Something Went Wrong");
			    }
		});
		

}	



</script>

<script type="text/javascript">
$(document).ready(function(){
	$(document).click(function(){
		expanded = false;
		$('#checkboxes').hide();
	});
	$('#checkboxes').click(function(e){
		e.stopPropagation();
	});


});
</script>

<script>

function testMe(){
	
	document.getElementById("currencyFrm").value = document.getElementById("currency").value;
	document.getElementById("dateFromFrm").value = document.getElementById("dateFrom").value;
	document.getElementById("dateToFrm").value = document.getElementById("dateTo").value;
	document.getElementById("merchantPayIdFrm").value = document.getElementById("merchants").value;
	document.getElementById("paymentTypeFrm").value = document.getElementById("paymentMethods").value;
	document.getElementById("mopTypeFrm").value = document.getElementById("mopType").value;
	document.getElementById("transactionTypeFrm").value = document.getElementById("transactionType").value;
	//document.getElementById("statusFrm").value = document.getElementById("pgRefNum").value;
	//document.getElementById("acquirerFrm").value = document.getElementById("pgRefNum").value;
	document.getElementById("paymentsRegionFrm").value = document.getElementById("paymentsRegion").value;
	document.getElementById("cardHolderTypeFrm").value = document.getElementById("cardHolderType").value;
	document.getElementById("pgRefNumFrm").value = document.getElementById("pgRefNum").value;
	
	document.getElementById("downloadSummaryReportAction").submit(); 
	
}

function valdPhoneNo() {
    var phoneElement = document.getElementById("phoneNo");
    var value = phoneElement.value.trim();
    if (value.length > 0) {
        var phone = phoneElement.value;
        var phoneexp = /^[0-9]{8,13}$/;
        if (!phone.match(phoneexp)) {
            document.getElementById('invalid-phone').innerHTML = "Please enter valid Phone";

            return false;
        } else {
            document.getElementById('invalid-phone').innerHTML = "";
            return true;
        }
    } else {
        phoneElement.focus();
        document.getElementById('invalid-phone').innerHTML = "";
        return true;
    }
}

function checkRefNo(){
	var refValue = document.getElementById("pgRefNum").value;
	var refNoLength = refValue.length;
	if((refNoLength <16) && (refNoLength >0)){
		document.getElementById("submit").disabled = true;
		document.getElementById("validRefNo").style.display = "block";
	}
	else if(refNoLength == 0){
		document.getElementById("submit").disabled = false;
		document.getElementById("validRefNo").style.display = "none";
	}else{
		document.getElementById("submit").disabled = false;
        document.getElementById("validRefNo").style.display = "none";
	}
}
function validPgRefNum(){
	
	var pgRefValue = document.getElementById("pgRefConf").value;
	var regex = /^(?!0{16})[0-9\b]{16}$/;
	if(pgRefValue.trim() != ""){
		if(!regex.test(pgRefValue)) {
			document.getElementById("validValue").style.display= "block";
			document.getElementById("submit").disabled = true;
			document.getElementById("download").disabled = true;
        }
		
	}

}
function validateOrderIdvalue(orderId){
setTimeout(function() {	
	//var orderIdreg =/^[0-9a-zA-Z\b\_-\s\+?.*?]+$/;
	var orderIdreg = /^[0-9a-zA-Z\b\_-\s\+.]+$/;
  if (orderId.value !== "") {
    if (orderIdreg.test(orderId.value) == false) 
    {
		document.getElementById("validOrderIdValue").style.display= "block";
		document.getElementById("submit").disabled = true;
		document.getElementById("download").disabled = true;
	}
  }else{
	document.getElementById("validOrderIdValue").style.display= "none";
  }

}, 400);
}
function validateRefundOrderIdvalue(refundOrderIDConf){
setTimeout(function() {	
	//var orderIdreg =/^[0-9a-zA-Z\b\_-\s\+?.*?]+$/;
	var refundOrderIdreg = /^[0-9a-zA-Z\b\_-\s\+.]+$/;
  if (refundOrderIDConf.value !== "") {
    if (refundOrderIdreg.test(refundOrderIDConf.value) == false) 
    {
		document.getElementById("validRefundOrderIdValue").style.display= "block";
		document.getElementById("btnRefundConf").disabled = true;
		document.getElementById("download").disabled = true;
	}
  }else{
	document.getElementById("validRefundOrderIdValue").style.display= "none";
	document.getElementById("btnRefundConf").disabled = false;
  }

}, 400);
}
</script>

<style>
.dataTables_wrapper {
	position: relative;
	clear: both;
	*zoom: 1;
	zoom: 1;
	margin-top: -30px;
}

.input-control {
	display: block;
	width: 107%;
	height: 28px;
	padding: 3px 4px;
	font-size: 14px;
	line-height: 1.42857143;
	color: #555;
	background-color: #fff;
	background-image: none;
	border: 1px solid #ccc;
	border-radius: 4px;
	margin-left: -5px;
}

.multiselect {
	width: 170px;
	display: block;
	margin-left: -10px;
}

.selectBox {
	position: relative;
}

#checkboxes {
	display: none;
	border: 1px #dadada solid;
	height: 300px;
	overflow-y: scroll;
	position: Absolute;
	background: #fff;
	z-index: 1;
	margin-left: 3px;
	margin-right: 5px;
}

#checkboxes label {
	width: 66%;
	font-weight: 600;
}

#checkboxes input {
	width: 30%;
}

.selectBox select {
	width: 100%;
}

.overSelect {
	position: absolute;
	left: 0;
	right: 0;
	top: 0;
	bottom: 0;
}

table.dataTable thead th {
	padding: 4px 15px !important;
}

#summaryReportDataTable {
	text-align: center;
}

#summaryReportCountDataTable {
	text-align: center;
}

.dataTables_length select option:last-child {
	display: none !important;
}

.boxheadingsmall th {
	text-align: center !important;
}

.multiselect {
	width: 100%;
	margin-left: 0;
}

.selectBox select {
	width: 100%;
}

.input-control select option {
	width: 100%;
}

.submit-btn {
	background-color: #496cb6;
	display: block;
	width: 100%;
	height: 30px;
	padding: 3px 4px;
	font-size: 14px;
	line-height: 1.42857143;
	color: #fff;
	border: 1px solid #ccc;
	border-radius: 4px;
	margin-top: 15px;
	margin-bottom: -20px;
}

.odd {
	background-color: #e6e6ff !important;
}

table.dataTable thead .sorting {
	background: none !important; .
	card-list-toggle {cursor: pointer;
	padding: 8px 12px;
	border: 1px solid #ccc;
	position: relative;
	background: #ddd;
}

.card-list-toggle:before {
	position: absolute;
	right: 10px;
	top: 7px;
	content: "\f078";
	font-family: 'FontAwesome';
	font-size: 15px;
}

.card-list-toggle.active:before {
	content: "\f077";
}

.card-list {
	display: none;
}

}
<!--
--
-CSS FOR COLLAPSE DROPDOWN DESIGN---->.select2-container--default {
	display: none;
}

.btnActive {
	background: #496cb6 !important;
	color: #fff !important;
}

.newteds .newround {
	border: none;
	padding: 8px 34px;
	background: #d2d2d2;
	color: #6b6b6b;
	margin-top: 10px;
}

.newteds .newround:last-child {
	margin-right: 186px;
}

#dvApprovedAmount {
	font-size: 13px; <!--
	padding: 8px 0 2px 0;
	-->
}

.col-xs-5ths, .col-sm-5ths, .col-md-5ths, .col-lg-5ths {
	position: relative;
	min-height: 1px;
	padding-right: 0px;
	padding-left: 40px;
}

.col-xs-5ths {
	width: 20%;
	float: left;
}

.panel-right h3 {
	font-size: 13px !important;
}

@media ( min-width : 768px) {
	.col-sm-5ths {
		width: 20%;
		float: left;
	}
}

@media ( min-width : 992px) {
	.col-md-5ths {
		width: 24%;
		float: left;
	}
}

@media ( min-width : 1200px) {
	.col-lg-5ths {
		width: 20%;
		float: left;
	}
}

.collapseHead {
	color: black;
	font-weight: 700;
	font-size: 13px;
}

.newDiv {
	width: 98%;
	height: 15px;
	background: #e6e6e6;
	margin-left: 12px;
	border: 1px solid #d9d9d9;
	border-radius: 3px; <!--
	padding: 6px 10px;
	-->
}

.arrowClass {
	float: right;
	margin-right: 15px;
	color: black;
}

.animateArrow {
	color: white;
	-webkit-transform: rotate(180deg);
	-moz-transform: rotate(180deg);
	-o-transform: rotate(180deg);
	-ms-transform: rotate(180deg);
	transform: rotate(180deg);
}

#loading {
	width: 100%;
	height: 100%;
	top: 0px;
	left: 0px;
	position: fixed;
	display: block;
	z-index: 99
}

#loading-image {
	position: absolute;
	top: 40%;
	left: 55%;
	z-index: 100;
	width: 10%;
}

#loadingInner {
	width: 100%;
	height: 100%;
	top: 0px;
	left: 0px;
	position: fixed;
	display: block;
	z-index: 99
}

#loading-image-inner {
	position: absolute;
	top: 33%;
	left: 48%;
	z-index: 100;
	width: 7%;
}

.txtnew_button {
	padding: 20px;
}

#submit {
	font-size: 16px;
}

#download {
	font-size: 16px;
}
</style>
</head>
<body>

	<div id="loading" style="text-align: center;">
		<img id="loading-image" style="width: 70px; height: 70px;"
			src="../image/sand-clock-loader.gif" alt="Sending SMS..." />
	</div>
	<div style="overflow: scroll !important;">

		<div id="loader-wrapper"
			style="width: 100%; height: 100%; display: none;">
			<div id="loader"></div>
		</div>

		<div class="modal" id="refundAccept" tabindex="-1" role="dialog"
			aria-labelledby="myModalLabel" aria-hidden="true">
			<div id="loadingInner" display="none">
				<img id="loading-image-inner" src="../image/sand-clock-loader.gif"
					alt="BUSY..." />
			</div>
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header" align="center">Confirm Refund
						Instruction</div>
					<div class="modal-body">
						Please Verify the details before submission.

						<table class="table">
							<tr>
								<th>Pg Ref Num</th>
								<td id="pgRefConfTd">
									<td id="otpMerchSuraccept"><input id="pgRefConf"
										readonly="true" class="input-control" autocomplete="off"
										type="text" name="pgRefConf" maxlength="16"
										onblur="validPgRefNum();" ondrop="return false;"
										onKeyDown="if(event.keyCode === 32)return false;"
										onpaste="removeSpaces(this);"></td>
								</td>
							</tr>
							<tr>
								<th>Order ID</th>
								<td id="orderIDConfTd">
									<td id="otpMerchSuraccept"><input id="orderIDConf"
										readonly="true" class="input-control" autocomplete="off"
										type="text" name="orderIDConf"></td>
								</td>
							</tr>
							<tr>
								<th>Currency</th>
								<td id="currencyConfTd">
									<td id="otpMerchSuraccept"><input id="currencyConf"
										readonly="true" class="input-control" autocomplete="off"
										type="text" name="currencyConf"></td>
								</td>
							</tr>
							<tr>
								<th>Refund Order ID</th>
								<td id="refundOrderIDConfTd">
									<td id="otpMerchSuraccept"><input id="refundOrderIDConf"
										onKeyDown="validateRefundOrderIdvalue(this);"
										onkeypress="return validateRefundOrderId(event);"
										ondrop="return false;"
										onpaste="validateRefundOrderIdvalue(this);"
										placeholder="< Optional >" class="input-control"
										autocomplete="off" type="text" name="refundOrderIDConf"></td>
									<span id="validRefundOrderIdValue"
									style="color: red; display: none;">Please Enter Valid
										OrderId.</span>
								</td>
							</tr>
							<tr>
								<th>Refunded Amount</th>
								<td id="amtRefTd">
									<td id="otpMerchSuraccept"><input id="amtRef"
										readonly="true" class="input-control" autocomplete="off"
										type="text" name="amtRef"></td>
								</td>
							</tr>
							<tr>
								<th>Available Amount</th>
								<td id="amtAvailTd">
									<td id="otpMerchSuraccept"><input id="amtAvail"
										readonly="true" class="input-control" autocomplete="off"
										type="text" name="amtAvail"></td>
								</td>
							</tr>
							<tr>
								<th>Enter Refund Amount</th>
								<td id="amtConfTd">
									<td id="otpMerchSuraccept"><input id="amtConf"
										type="number" min="" max="9999999999.99" step="0.01"
										class="input-control" autocomplete="off" name="amtConf"
										onkeypress="return isNumberKeyAmount(event)"></td>
								</td>
							</tr>


						</table>

					</div>

					<div align="center">
						<button type="button" class="btn btn-lg btn-success"
							id="btnRefundConf" onClick='refundFunction("accept")'>Submit</button>
						<button type="button" class="btn btn-lg btn-danger"
							id="btnRefundCancel" data-dismiss="modal">Cancel</button>
					</div>
				</div>
			</div>
		</div>


		<table width="100%" border="0" cellspacing="0" cellpadding="0"
			style="border: 1px solid #ddd;">
			<tr>
				<div class="col-md-12">
					<div class="card ">
						<div class="card-header card-header-rose card-header-text">
							<div class="card-text">
								<h4 class="card-title">Summary Report</h4>
							</div>
						</div>
						<div class="card-body ">
							<div class="container">
								<div class="row">

									<div class="col-sm-6 col-lg-3">
										<label for="acquirer" style="float: left;">PG REF NUM:</label><br />
										<div class="txtnew">
											<s:textfield id="pgRefNum" class="input-control"
												name="pgRefNum" type="text" value="" autocomplete="off"
												onkeypress="javascript:return isNumber (event)"
												maxlength="16" onblur="checkRefNo()"></s:textfield>
										</div>

									</div>

									<div class="col-sm-6 col-lg-3">
										<div class="txtnew">
											<label for="acquirer">Order Id:</label><br />
											<s:textfield id="orderId" class="input-control"
												name="orderId" type="text" value="" autocomplete="off"
												onKeyDown="validateOrderIdvalue(this);"
												onkeypress="return validateOrderId(event);"
												ondrop="return false;" onpaste="validateOrderIdvalue(this);"
												maxlength="50"></s:textfield>
										</div>
										<span id="validOrderIdValue"
											style="color: red; display: none;">Please Enter Valid
											OrderId.</span>
									</div>
									<div class="col-sm-6 col-lg-3">

										<label for="acquirer" style="float: left;">Merchant</label><br />
										<div class="txtnew">

											<s:select name="merchants" class="input-control"
												id="merchants" list="merchantList" listKey="emailId"
												listValue="businessName" autocomplete="off" />
										</div>
									</div>


									<div class="col-sm-6 col-lg-3">
										<div class="txtnew">
											<label for="acquirer">PhoneNo:</label><br />
											<s:textfield id="phoneNo" class="input-control"
												name="phoneNo" type="text" value="" autocomplete="off"
												onpaste="removeSpaces(this);" onkeyup="valdPhoneNo();"
												onkeypress="return validateOrderId(event);"
												ondrop="return false;"></s:textfield>
										</div>
										<span id="invalid-phone" style="color: red;"></span>
									</div>

									<div class="col-sm-6 col-lg-3">
										<label for="paymentMethod" style="float: left;">Payment
											Method</label> <br />
										<div class="txtnew">
											<s:select name="paymentMethods" id="paymentMethods"
												headerValue="ALL" headerKey="ALL"
												list="@com.pay10.commons.util.PaymentType@values()"
												listValue="name" listKey="code" class="input-control"
												onchange="handleChange();" />
										</div>

									</div>
									<div class="col-sm-6 col-lg-3">
										<label for="mopType" style="float: left;">Mop Type</label> <br />
										<div class="txtnew">
											<s:select name="mopType" id="mopType" headerValue="ALL"
												headerKey="ALL"
												list="@com.pay10.commons.util.MopTypeUI@values()"
												listValue="name" listKey="code" class="input-control"
												onchange="handleChange();" />
										</div>

									</div>
									<div class="col-sm-6 col-lg-3">
										<label for="transactionType" style="float: left;">Transaction
											Type</label> <br />
										<div class="txtnew">
											<s:select headerKey="ALL" headerValue="ALL"
												class="input-control"
												list="#{'SALE':'SALE','REFUND':'REFUND'}"
												name="transactionType" id="transactionType" />
										</div>
									</div>
									<div class="col-sm-6 col-lg-3">
										<label for="transactionRegion" style="float: left;">Transaction
											Region</label> <br />
										<div class="txtnew">
											<s:select headerKey="ALL" headerValue="ALL"
												class="input-control"
												list="#{'INTERNATIONAL':'International','DOMESTIC':'Domestic'}"
												name="paymentsRegion" id="paymentsRegion" />
										</div>
									</div>
									<div class="col-sm-6 col-lg-3">
										<label for="cardHolderType" style="float: left;">Card
											Holder Type</label> <br />
										<div class="txtnew">
											<s:select headerKey="ALL" headerValue="ALL"
												class="input-control"
												list="#{'CONSUMER':'Consumer','COMMERCIAL':'Commercial'}"
												name="cardHolderType" id="cardHolderType" />
										</div>
									</div>

									<div class="col-sm-6 col-lg-3">
										<label for="currency" style="float: left;">Currency</label> <br />
										<div class="txtnew">
											<s:select name="currency" id="currency" headerValue="ALL"
												headerKey="ALL" list="currencyMap" class="input-control"
												onchange="handleChange();" />
										</div>
									</div>

									<div class="col-sm-6 col-lg-3">
										<label for="dateFrom" style="float: left;">Capture
											Date From</label> <br />
										<div class="txtnew">
											<s:textfield type="text" readonly="true" id="dateFrom"
												name="dateFrom" class="input-control" autocomplete="off" />
										</div>
									</div>










									<div class="col-sm-6 col-lg-3">
										<label for="dateTo" style="float: left;">Capture Date
											To</label> <br />
										<div class="txtnew">
											<s:textfield type="text" readonly="true" id="dateTo"
												name="dateTo" class="input-control" autocomplete="off" />
										</div>
									</div>
									<div class="col-sm-6 col-lg-3"></div>

									<div class="col-sm-6 col-lg-3">
										<div class="txtnew" style="margin-right: 5px; float: left;">
											<s:submit id="submit" value="View"
												class="btn btn-primary  mt-4 submit_btn" />
										</div>
									</div>
									<div class="col-sm-6 col-lg-3">
										<div class="txtnew" style="float: left;">
											<s:submit id="download" value="Download" onclick="testMe()"
												class="btn btn-primary  mt-4 submit_btn" />
										</div>

										<!-- <div class="col-sm-6 col-lg-3"> -->

										<!-- <div class="txtnew">
									<s:submit id="download" value="Download" onclick="testMe()"
										class="btn btn-primary  mt-4 submit_btn" />
								</div> -->
									</div>



									<!-- <div class="col-sm-6 col-lg-3">
						
							<div class="txtnew_button">
								<s:submit id="submit" value="View"
									class="btn btn-sm btn-block btn-primary" />
							</div>
						</div>
						
						  <div class="col-sm-6 col-lg-3">
							
							<div class="txtnew_button">
								<s:submit id="download" value="Download" onclick="testMe()"
									class="btn btn-sm btn-block btn-primary" />
							</div>
						 </div> -->

								</div>




							</div>
						</div>


					</div>
				</div>
				<td align="center" valign="top"><table width="100%" border="0"
						align="center" cellpadding="0" cellspacing="0" class="txnf"
						style="border: none;">

						<tr>
							<!-- <td colspan="5"><h2>Summary Report</h2> -->



							</div>




							</td>
						</tr>



					</table></td>
			</tr>




			<tr>

				<td align="left" valign="top" style="padding: 10px;"><br>
						<div class="scrollD">
							<table id="summaryReportDataTable" align="center" cellspacing="0"
								width="100%">
								<thead>
									<tr class="boxheadingsmall" style="font-size: 11px;">

										<th style="text-align: center;">S No.</th>
										<th style="text-align: center;">Pg Ref Num</th>
										<th style="text-align: center;">Order Id</th>
										<th style="text-align: center;">Txn Amount</th>
										<th style="text-align: center;">Net Amount</th>
										<th style="text-align: center;">Txn Type</th>
										<th style="text-align: center;">Capture Date</th>
										<th style="text-align: center;">Settlement Date</th>
										<th style="text-align: center;">Merchant Amount</th>
										<th style="text-align: center;">Payment Method</th>
										<th style="text-align: center;">Mop Type</th>
										<th style="text-align: center;">Currency</th>
										<th style="text-align: center;">TDR / SC</th>
										<th style="text-align: center;">GST</th>
										<th style="text-align: center;">ACQ Id</th>
										<th style="text-align: center;">RRN NO.</th>
										<th style="text-align: center;">Payment Region</th>
										<th style="text-align: center;">Card Mask</th>
										<th style="text-align: center;">Post Settled Flag</th>
										<th style="text-align: center;">Refund</th>




									</tr>
								</thead>
								<tfoot>
									<tr class="boxheadingsmall">
										<th style='text-align: left;'></th>
										<th style='text-align: left;'></th>
										<th style='text-align: left;'></th>
										<th style='text-align: left;'></th>
										<th style='text-align: left;'></th>
										<th style='text-align: left;'></th>
										<th style='text-align: left;'></th>
										<th style='text-align: left;'></th>
										<th style='text-align: left;'></th>
										<th style='text-align: left;'></th>
										<th style='text-align: left;'></th>
										<th style='text-align: left;'></th>
										<th style='text-align: left;'></th>
										<th style='text-align: left;'></th>
										<th style='text-align: left;'></th>
										<th style='text-align: left;'></th>
										<th style='text-align: left;'></th>
										<th style='text-align: left;'></th>
										<th style='text-align: left;'></th>
										<th style='text-align: left;'></th>
									</tr>
								</tfoot>
							</table>
						</div></td>
			</tr>
		</table>
		</td>
		</tr>
		</table>

	</div>

	<script>
$('.collapseStart').slideToggle("fast");
  $('.newDiv').click(function() {
    $(this).parent().find('.collapseStart').slideToggle("fast");
	//document.getElementById("arrow").style.display = "none";
	//document.getElementById("arrowUp").style.display = "block";
  });
function isNumberKeyAmount(evt) {
		const charCode = (event.which) ? event.which : event.keyCode;
          if (charCode > 31 &&  (charCode < 48 || charCode > 57) && charCode!=46 ) {
            return false;
			return true;
          }
}

  $('#amtConf').keypress(function(e){ 
	if (this.value.length == 0 && e.which == 48 ){
	 return false;
	 }
  });
  
  $('input#amtConf').blur(function(){
        var num = parseFloat($(this).val());
        var cleanNum = num.toFixed(2);
        $(this).val(cleanNum);
        if(num/cleanNum < 1){
            $('#error2').text('Please enter only 2 decimal places, we have truncated extra points');
            }
  });
  
</script>

	<s:form id="downloadSummaryReportAction"
		name="downloadSummaryReportAction"
		action="downloadSummaryReportAction">
		<s:hidden name="token" value="%{#session.customToken}"></s:hidden>
		<s:hidden name="currency" id="currencyFrm" value=""></s:hidden>
		<s:hidden name="dateFrom" id="dateFromFrm" value=""></s:hidden>
		<s:hidden name="dateTo" id="dateToFrm" value=""></s:hidden>
		<s:hidden name="merchantPayId" id="merchantPayIdFrm" value=""></s:hidden>
		<s:hidden name="paymentType" id="paymentTypeFrm" value=""></s:hidden>
		<s:hidden name="mopType" id="mopTypeFrm" value=""></s:hidden>
		<s:hidden name="transactionType" id="transactionTypeFrm" value=""></s:hidden>
		<s:hidden name="status" id="statusFrm" value=""></s:hidden>
		<s:hidden name="acquirer" id="acquirerFrm" value=""></s:hidden>
		<s:hidden name="paymentMethods" id="paymentMethodsFrm" value=""></s:hidden>
		<s:hidden name="paymentsRegion" id="paymentsRegionFrm" value=""></s:hidden>
		<s:hidden name="cardHolderType" id="cardHolderTypeFrm" value=""></s:hidden>
		<s:hidden name="pgRefNum" id="pgRefNumFrm" value=""></s:hidden>
	</s:form>



</body>
</html>
