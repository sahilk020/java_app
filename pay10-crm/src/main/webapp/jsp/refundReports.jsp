<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib prefix="s" uri="/struts-tags"%>
<html>
<head>

<style>

.heading{
   text-align: center;
    color: black;
    font-weight: bold;
    font-size: 22px;
}
.samefnew {
    width: 19.5% !important;
    float: left;
	font-weight:600;
	font-size:12px;
    color: #333;
    line-height: 22px;
    margin: 0 0 0 10px;
}
.cust {
    width: 20% !important;
    float: left;
    font: bold 13px arial !important;
    color: #333;
    line-height: 22px;
    margin: 0 0 0 0px !important;
}
.submit-button{
	width:10% !important;
	height:28px !important;
	margin-top:-4px !important;
}
.MerchBx {
    min-width: 99% !important;
    margin: 15px;
    margin-top: 25px !important;
    padding: 0;
}

table.dataTable thead .sorting {
    background: none !important;
}
.sorting_asc {
    background:none !important;
}
table.dataTable thead .sorting_desc {
    background: none !important;
}
table.dataTable thead .sorting {
     cursor: default !important;
}
table.dataTable thead .sorting_desc, table.dataTable thead .sorting {
    cursor: default !important;
}
table.dataTable.display tbody tr.odd {
    background-color: #e6e6ff !important;
}
table.dataTable.display tbody tr.odd > .sorting_1{
	 background-color: #e6e6ff !important;
}
</style>

<title>Refund Report</title>
	<link href="../css/Jquerydatatableview.css" rel="stylesheet" />
	<link href="../css/Jquerydatatable.css" rel="stylesheet" />
	<link rel="stylesheet" href="../css/loader.css">
	<link href="../css/default.css" rel="stylesheet" type="text/css" />
	<link href="../css/jquery-ui.css" rel="stylesheet" />
	<script src="../js/jquery.js"></script>
	<script src="../js/jquery.dataTables.js"></script>
	<script src="../js/jquery-ui.js"></script>
	<script type="text/javascript" src="../js/moment.js"></script>
	<script type="text/javascript" src="../js/daterangepicker.js"></script>
	<link href="../css/loader.css" rel="stylesheet" type="text/css" />
	<script src="../js/jquery.popupoverlay.js"></script>
	<script type="text/javascript" src="../js/dataTables.buttons.js"></script>
	<script type="text/javascript" src="../js/pdfmake.js"></script>
	<link href="../fonts/css/font-awesome.min.css" rel="stylesheet">
	<script src="../js/commanValidate.js"></script>
	<link href="../css/select2.min.css" rel="stylesheet" />
    <script src="../js/jquery.select2.js" type="text/javascript"></script>
<!--  loader scripts -->
<script src="../js/loader/modernizr-2.6.2.min.js"></script>
<script src="../js/loader/main.js"></script>
<link rel="stylesheet" href="../css/loader/normalize.css" />
<link rel="stylesheet" href="../css/loader/main.css" />
<link rel="stylesheet" href="../css/loader/customLoader.css" />
	
<script type="text/javascript">
$(document).ready(function(){
 
  // Initialize select2
  $("#merchant").select2();
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
			var table = $('#refundReportDatatable').DataTable();
			$('#refundReportDatatable tbody').on('click', 'td', function() {

				popup(table, this);
			});
		});
	});
					
				
	function renderTable() {
		var merchantEmailId = document.getElementById("merchant").value;
		var table = new $.fn.dataTable.Api('#refundReportDatatable');
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
		$('#refundReportDatatable')
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
								total = api.column(6).data().reduce(
										function(a, b) {
											return intVal(a) + intVal(b);
										}, 0);

								// Total over this page
								pageTotal = api.column(6, {
									page : 'current'
								}).data().reduce(function(a, b) {
									return intVal(a) + intVal(b);
								}, 0);

								// Update footer
								$(api.column(6).footer()).html(
										'' + pageTotal.toFixed(2) + ' ' + ' ');

								// Total over all pages
								total = api.column(7).data().reduce(
										function(a, b) {
											return intVal(a) + intVal(b);
										}, 0);

								// Total over this page refund
								pageTotal = api.column(7, {
									page : 'current'
								}).data().reduce(function(a, b) {
									return intVal(a) + intVal(b);
								}, 0);

								// Update footer
								$(api.column(7).footer()).html(
										'' + pageTotal.toFixed(2) + ' ' + ' ');

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
										'' + pageTotal.toFixed(2) + ' ' + ' ');

								// Total over all pages
								total = api.column(9).data().reduce(
										function(a, b) {
											return intVal(a) + intVal(b);
										}, 0);

								// Total over this page
								pageTotal = api.column(9, {
									page : 'current'
								}).data().reduce(function(a, b) {
									return intVal(a) + intVal(b);
								}, 0);

								// Update footer
								$(api.column(9).footer()).html(
										'' + pageTotal.toFixed(2) + ' ' + ' ');
							},

							"columnDefs" : [ {
								className : "dt-body-right",
								"targets" : [ 0, 1, 2, 3, 4, 5, 8, 9 ,10,11,12]
							} ],
							dom : 'BTftlpi',
							buttons : [
									{
										extend : 'copyHtml5',
										//footer : true,
										exportOptions : {
											columns : [':visible']
										}
									},
									{
										extend : 'csvHtml5',
										//footer : true,
										title : 'Refund Report',
										exportOptions : {
											columns : [':visible']
										}
									},
									{
										extend : 'pdfHtml5',
										//footer : true,
										orientation : 'landscape',
										title : 'Refund Report',
										exportOptions : {
											columns : [':visible']
										}
									},
									{
										extend : 'print',
										//footer : true,
										title : 'Refund Report',
										exportOptions : {
											columns : [':visible']
										}
									},
									{
										extend : 'colvis',
										//           collectionLayout: 'fixed two-column',
										columns : [0, 1, 2, 3, 4, 5, 6, 7, 8, 9 , 10, 11, 12]
									} ],
							"ajax" : {
								"url" : "refundReportAction",
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
								"targets" : [ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9,10,11,12]
							} ],
							"columns" : [
									
									{
										"data" : "origTxnId",
										"width" : '11%'
									},
									{
										"data" : "origTxnDate",
										"width" : '8%'
									},
									{
										"data" : "pgRefNum",
									},
									{
										"data" : "refundDate",
										"width" : '9%'
									},
									{
										"data" : "customerName",
										
									},{
										"data" : "orderId",

									},
								
								{
										"data" : "customerEmail",
									

									},
									
									{
										"data" : "paymentMethods"

									},
									{
										"data" : "currency"

									},
									{
										"data" : "origAmount",
										"width" : '7%',
									},
									
									{
										"data" : "refundAmount",
										"width" : '7%',
									},
									
									{
										"data" : "refundFlag",
										"width" : '7%',
									},
									{
										"data" : "refundStatus",
										"width" : '7%',
									},
									
									 ]
						});
		//TODO fix CSS
	/*$("#merchants").select2({
		//data: payId
		});*/
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

		var tableObj = $('#refundReportDatatable');
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
	var merchantEmailId = document.getElementById("merchant").value;
	var currency = document.getElementById("currency").value;
	var pgRefNum = document.getElementById("pgRefNum").value;
		if(merchant==''){
			merchant='ALL'
		}
		
		if(currency==''){
			currency='ALL'
		}

		var obj = {
			dateFrom : document.getElementById("dateFrom").value,
			dateTo : document.getElementById("dateTo").value,
			merchantEmailId : merchantEmailId,
			currency : currency,
			aquirer : aquirer.toString(),
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

<script>
function checkRefNo(){
	var refValue = document.getElementById("pgRefNum").value;
	var refNoLength = refValue.length;
	if((refNoLength <16) && (refNoLength >0)){
		document.getElementById("Submit").disabled = true;
		document.getElementById("Submit").style.backgroundColor = "#b3e6b3";
		document.getElementById("validRefNo").style.display = "block";
	}
	else if(refNoLength == 0){
		document.getElementById("Submit").disabled = false;
		document.getElementById("Submit").style.backgroundColor = "#39ac39";
		document.getElementById("validRefNo").style.display = "none";
	}else{
		document.getElementById("Submit").disabled = false;
		document.getElementById("Submit").style.backgroundColor = "#39ac39";
        document.getElementById("validRefNo").style.display = "none";
	}
}
</script>

<style type="text/css">
.form-control{
	margin-left:0 !important;
}
.samefnew{
	margin:0 !important;
}
.submit-btn {
	background-color:#39ac39;
	display: block;
    width: 100%;
    height: 30px;
    padding: 3px 4px;
    font-size: 14px;
	margin-left: -140%;
    line-height: 1.42857143;
    color: #fff;
    border: 1px solid #ccc;
    border-radius: 4px;
	margin-top:40%;
	margin-bottom: 40px;
}
</style>

</head>
<body id="mainBody">
    <div id="loader-wrapper" style="width: 100%; height: 100%; display:none;">
	  <div id="loader"></div>
    </div>
	
	<h2 class="pageHeading" style="margin-bottom: -20% !important;">Refund Report</h2>
	<br>
	<br>
	 <table class="table98 padding0">
        
        <tr>
          <td align="center">&nbsp;</td>
          <td height="10" align="center">
    
	
     
            <table id="mainTable" width="100%" border="0" align="center" cellpadding="0" cellspacing="0" class="txnf">
		
		    <tr>
			    <td colspan="5" align="left" valign="top">
				<div class="MerchBx">

                  	<span id="validRefNo" style="color:red; display:none; margin-bottom:2px;">Please Enter 16 Digit PG Ref No.</span>
					<div class="samefnew">
						PG REF NUM:<br>
						<div class="txtnew">
										<s:textfield id="pgRefNum" class="form-control"
											name="pgRefNum" type="text" value="" autocomplete="off"
											onkeypress="javascript:return isNumber (event)" maxlength="16" onblur="checkRefNo()"></s:textfield>
						</div>

					</div>
					
					<div class="samefnew">
						Merchant:<br>
						<div class="txtnew">
							<s:if
								test="%{#session.USER.UserType.name()=='ADMIN' || #session.USER.UserType.name()=='SUBADMIN' || #session.USER_TYPE.name()=='SUPERADMIN'}">
								<s:select name="merchant" class="form-control" id="merchant"
									headerKey="ALL" headerValue="ALL" list="merchantList"
									listKey="emailId" listValue="businessName" autocomplete="off" />
							</s:if>
							<s:else>
								<s:select name="merchant" class="form-control" id="merchant"
									headerKey="ALL" headerValue="ALL" list="merchantList"
									listKey="emailId" listValue="businessName" autocomplete="off" />
							</s:else>
						</div>
					</div>
					
					<div class="samefnew">
						Acquirer<br>
						<div class="txtnew">
							<s:select headerKey="ALL" headerValue="ALL" class="form-control" list="@com.pay10.commons.util.AcquirerTypeUI@values()" 
                              listValue="name" listKey="code" id="aquirer" name="aquirer" value="aquirer" />
						</div>
					</div>

					<div class="samefnew">
						Currency:<br>
						<div class="txtnew">
							<s:select name="currency" id="currency" headerValue="ALL"
								headerKey="ALL" list="currencyMap" class="form-control" />
						</div>
					</div>
					
					<div class="samefnew">
						Date From:<br>
						<div class="txtnew">
							<s:textfield type="text" id="dateFrom" name="dateFrom" class="form-control" autocomplete="off" readonly="true" />
						</div>
					</div>

					<div class="samefnew" style="margin-top:12px !important;">
						Date To:<br>
						<div class="txtnew">
							<s:textfield type="text" id="dateTo" name="dateTo" class="form-control" autocomplete="off" readonly="true" />
						</div>
					</div>
					
					<div class="samefnew-btn">
						&nbsp;<br>
						<div class="form-group col-md-2 col-sm-3 txtnew col-xs-6 col-md-offset-5">
							   <button class="submit-btn" id="submit">Submit</button>
							</div>
					</div>

					
				</div>
			</td>
		</tr>
		<br/><br/><br/>
		
		 <tr>
						<td align="left" valign="top" style="padding: 10px; border-right: 1px solid #e0d2d2c4;"><br>
							<div class="scrollD">
								<table id="refundReportDatatable" align="center" class="display" cellspacing="0" width="100%">
									<thead>
										<tr class="boxheadingsmall" style="font-size: 11px;">
											<th style="text-align:left;" data-orderable="false">Original Txn Id</th>
											<th style="text-align:left;" data-orderable="false">Original Txn Date</th>
											<th style="text-align:left;" data-orderable="false">PG Ref Num</th>
											<th style="text-align:left;" data-orderable="false">Refund Date</th>
										    <th style="text-align:left;" data-orderable="false">Merchant Name</th>
											<th style="text-align:left;" data-orderable="false">Order Id</th>
											<th style="text-align:left;" data-orderable="false">Customer Email</th>
											<th style="text-align:left;" data-orderable="false">Payment Method</th>
											<th style="text-align:left;" data-orderable="false">Currency</th>
											<th style="text-align:left;" data-orderable="false">Original Amount</th>
											<th style="text-align:left;" data-orderable="false">Refund Amount</th>
											<th style="text-align:left;" data-orderable="false">Refund Flag</th>
											<th style="text-align:left;" data-orderable="false">Refund Status</th>
										</tr>
									</thead>
								
								</table>
							</div>
						</td>
					</tr>
		
		
	    </table>
                
    
        </td>
    </table>
</body>

</html>