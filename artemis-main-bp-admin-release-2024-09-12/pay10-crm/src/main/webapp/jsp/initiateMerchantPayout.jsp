<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Initiate Merchant Payout Date</title>

<link href="../css/Jquerydatatableview.css" rel="stylesheet" />
<link href="../css/Jquerydatatable.css" rel="stylesheet" />
<link href="../css/default.css" rel="stylesheet" type="text/css" />
<link href="../css/jquery-ui.css" rel="stylesheet" />
<script src="../js/jquery.min.js"></script>
<script src="../js/jquery.dataTables.js"></script>
<script src="../js/jquery-ui.js"></script>
<script src="../js/jquery.popupoverlay.js"></script> 
<script type="text/javascript" src="../js/dataTables.buttons.js"></script>  
<script type="text/javascript" src="../js/pdfmake.js"></script>
<link href="../css/select2.min.css" rel="stylesheet" />
<script src="../js/jquery.select2.js" type="text/javascript"></script>


<script type="text/javascript">
$(document).ready(function(){
  document.getElementById("loading").style.display = "none";
  // Initialize select2
  $("#merchant").select2();
});
</script>
	
<script>
 var expanded = false;
function showCheckboxes(e) {
  var checkboxes = document.getElementById("checkboxes");
  if (!expanded) {
    checkboxes.style.display = "block";
    expanded = true;
  } else {
    checkboxes.style.display = "none";
    expanded = false;
  }
   e.stopPropagation();
}


var selectedAcquirer ;
function getCheckBoxValue(){
	 var allInputCheckBox = document.getElementsByClassName("myCheckBox");
  		
		var allSelectedAquirer = []
  		for(var i=0; i<allInputCheckBox.length; i++){
  			
  			if(allInputCheckBox[i].checked){
  				allSelectedAquirer.push(allInputCheckBox[i].value);	
  			}
  		}
  		document.getElementById('selectBox').setAttribute('title', allSelectedAquirer.join());
  		if(allSelectedAquirer.join().length>28){
  			var res = allSelectedAquirer.join().substring(0,27);
  			document.querySelector("#selectBox option").innerHTML = res+'...............';
  		}else if(allSelectedAquirer.join().length==0){
  			document.querySelector("#selectBox option").innerHTML = 'Select Acquirer';
  		}else{
  			document.querySelector("#selectBox option").innerHTML = allSelectedAquirer.join();
  		}
		selectedAcquirer = allSelectedAquirer.join();
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

<style>
  .divalignment{
	  margin-top: -30px !important;
  }
  
  .case-design{
	  text-decoration:none;
	  cursor: default !important;
  }
  .my_class:hover{
	  color: white !important;
  }
 .multiselect {
    width: 210px;
	display:block;
	margin-left:-20px;	
 }
  .selectBox {
  position: relative;
 }
#checkboxes {
  display: none;
  border-radius: 5px;
  border: 1px #dadada solid;
  height:300px;
  overflow-y: scroll;
  position:Absolute;
  background:#fff;
  z-index:1;
  margin-left:2px;
  margin-right:10px;
}
#checkboxes label {
  width: 74%;
}
#checkboxes input {
  width:18%;
}
.selectBox select {
  width: 95%;
}
.overSelect {
  position: absolute;
  left: 0;
  right: 0;
  top: 0;
  bottom: 0;
}
.download-btn {
	background-color:#496cb6;
    width: 115%;
    height: 30px;
    padding: 3px 4px;
    font-size: 15px;
    line-height: 1.42857143;
    color: #fff;
    border: 1px solid #ccc;
    border-radius: 4px;
	margin-top:15px;
}
.payout-btn{
	background-color:green;
	display: block;
    width: 135%;
    height: 30px;
    padding: 3px 4px;
    font-size: 14px;
    line-height: 1.42857143;
    color: #fff;
    border: 1px solid #ccc;
    border-radius: 4px;
	margin-top:5px;
}
.form-control{
	margin-left: 0!important;
	width: 100% !important;
}
.padding10{
	padding: 10px;
}
.markedCheck{
	color: black;
	font-size:13px;
}
#outer{
  width:100%;
  text-align: center;
}
.inner{
  display: inline-block;
}
#loading {width: 100%;height: 100%;top: 0px;left: 0px;position: fixed;display: block; z-index: 99}
#loading-image {position: absolute;top: 40%;left: 55%;z-index: 100; width:10%;}
</style>

</head>
<body>
    <div id="loading" style="text-align: center;">
		<img id="loading-image" style="width:70px;height:70px;" src="../image/sand-clock-loader.gif" alt="Sending SMS..." />
	</div>
<table width="100%" align="left" cellpadding="0" cellspacing="0" class="txnf">
		<tr>
			<td align="left"><h2>Initiate Merchant Payout Date</h2>
				<div class="container">
					<div class="row padding10">
						<div class="form-group col-md-2 txtnew col-sm-3 col-xs-6">
							<label for="merchant" style="margin-left: 2px;">Merchant:</label> <br />
							<s:if
								test="%{#session.USER.UserType.name()=='ADMIN' || #session.USER.UserType.name()=='SUBADMIN'  || #session.USER.UserType.name()=='SUBADMIN' || #session.USER_TYPE.name()=='SUPERADMIN'}">
								<s:select name="merchant" class="form-control" id="merchant"
									headerKey="Select Merchant" headerValue="Select Merchant" list="merchantList"
									listKey="payId" listValue="businessName" autocomplete="off" />
							</s:if>
							<s:else>
								<s:select name="merchant" class="form-control" id="merchant"
									headerKey="" headerValue="" list="merchantList"
									listKey="payId" listValue="businessName" autocomplete="off" />
							</s:else>
						</div>

						<div class="form-group  col-md-3 col-sm-4 txtnew  col-xs-6">
							<label for="aquirer" >Acquirer:</label> <br />
								<div>
									<div class="selectBox" id="selectBox" onclick="showCheckboxes(event)">
										<select class="form-control">
											<option>Select Acquirer</option>
										</select>
										<div class="overSelect"></div>
									</div>
									<div id="checkboxes" onclick="getCheckBoxValue()">
										<s:checkboxlist headerKey="Select Acquirer(s)" headerValue="Select Acquirer(s)" list="@com.pay10.commons.util.AcquirerTypeUI@values()" 
										listValue="name" listKey="code" name="acquirer" id = "acquirer" value="name" class="myCheckBox"/>
									</div>
								</div>
						</div>
					
						<div class="form-group  col-md-3 col-sm-4 txtnew  col-xs-6">
							<label for="email" >Currency:</label> <br />
							<s:select name="currency" id="currency" headerValue="Select Currency"
									headerKey="Select Currency" list="currencyMap" class="form-control" style="margin-left:17px; width:75% !important;"/>
						</div>

						<div class="form-group  col-md-2 col-sm-3 txtnew col-xs-6">
							<label for="dateFrom" style="margin-left:-40% !important;">Capture Date From:</label> <br />
							<s:textfield type="text" readonly="true" id="dateFrom" 
								name="dateFrom" class="form-control" autocomplete="off" style="margin-left: -40% !important; width:118% !important;"/>
						</div>
					
						<div class="form-group  col-md-2 col-sm-3 txtnew col-xs-6">
							<label for="dateTo" style="margin-left: -25% !important;">Capture Date To:</label> <br />
							<s:textfield type="text" readonly="true" id="dateTo" name="dateTo" 
								class="form-control" autocomplete="off" style="margin-left:-24% !important; width: 118% !important;"/>
						</div>
					</div>
					
					<div class="row">
						<div id="outer">
								<div class="inner">
								    <button class="download-btn" id="previewReportBtn" style="margin-left:-10px !important;">Preview</button>
								</div>
								<div class="inner">
								    <button class="download-btn" id="downloadReportBtn" onclick="checkDiv()">Download Preview</button>
								</div>
						</div> 
					</div>
					
				</div>
		<!-------Datatable Div Code----->
				<div id="previewDiv" style="display:none; margin-top:5% !important;">
					<div class="scrollD">
						<table id="previewDatatable" align="center" class="display" cellspacing="0" width="100%">
								<thead>
									<tr class="boxheadingsmall" style="font-size: 11px;">
										<th style="text-align:center;" data-orderable="false">Sale Count</th>
										<th style="text-align:center;" data-orderable="false">Refund Count</th>
										<th style="text-align:center;" data-orderable="false">Gross Transaction Amt</th>
										<th style="text-align:center;" data-orderable="false">Total iPay Comm Amt (Inc GST)</th>
										<th style="text-align:center;" data-orderable="false">Total Acq Comm Amt (Inc GST)</th>
										<th style="text-align:center;" data-orderable="false">Total Amt Payable to Merchant A/c</th>
										<th style="text-align:center;" data-orderable="false">Total Payout from Nodal Acc</th>
									</tr>
								</thead>
								
								<tfoot>
								<tr class="boxheadingsmall">
									<th style="text-align:center;"></th>
									<th style="text-align:center;"></th>
									<th style="text-align:center;"></th>
									<th style="text-align:center;"></th>
									<th style="text-align:center;"></th>
									<th style="text-align:center;"></th>
									<th style="text-align:center;"></th>
								</tr>
							</tfoot>
						</table>
					</div>
					
					<div style="margin-top: 5%;">
					    <div class="form-group  col-md-2 col-sm-3 txtnew col-xs-6 col-md-offset-5">
							<label for="payoutDate">Payout Initiation Date:</label> <br />
							<s:textfield type="text" readonly="true" id="payoutDate" name="payoutDate" 
								class="form-control" autocomplete="off"/>
						</div>
					</div>
					
					<div>
					   <div class="form-group col-md-2 col-sm-3 txtnew col-xs-6" style="margin-top:8%; margin-left:-18%;">
						<button class="payout-btn" id="initiatePayoutBtn" onclick="initiatePayout()">Initiate Payout & Download</button>
					   </div>
					</div>
				</div>
				
			</td>
		</tr>
		<tr>
			<td align="left" style="border-bottom: 1px solid #eaeaea;">&nbsp;</td>
		</tr>
		<tr>
		</tr>
</table>


<!-----------------DatePicker-------------------->
<script type="text/javascript">

	$(document).ready(function() {
		
		$(function() {
			$("#dateFrom").datepicker({
				prevText : "click for previous months",
				nextText : "click for next months",
				showOtherMonths : true,
				dateFormat : 'dd/mm/yy',
				selectOtherMonths : false,
				maxDate : new Date()
			});
			$("#dateTo").datepicker({
				prevText : "click for previous months",
				nextText : "click for next months",
				showOtherMonths : true,
				dateFormat : 'dd/mm/yy',
				selectOtherMonths : false,
				maxDate : new Date()
			});
			$("#payoutDate").datepicker({
				prevText : "click for previous months",
				nextText : "click for next months",
				showOtherMonths : true,
				dateFormat : 'dd/mm/yy',
				selectOtherMonths : false,
				maxDate : new Date()
			});
		});
		$(function() {
			var today = new Date();
			$('#dateTo').val($.datepicker.formatDate('dd-mm-yy', today));
			$('#dateFrom').val($.datepicker.formatDate('dd-mm-yy', today));	
			$('#payoutDate').val($.datepicker.formatDate('dd-mm-yy', today));	

		});
		
	});	
</script>

<script type="text/javascript">
	$(document).ready(function() {

		$("#previewReportBtn").click(function(env) {
			var merchant = document.getElementById("merchant").value;
			 if(merchant == "" || merchant== "Select Merchant"){
				 alert("Please select Merchant");
				 document.getElementById("previewDiv").style.display = "none";
				 return false;
			 }
			 
			 var acquirer = selectedAcquirer;
				if(acquirer == "" || acquirer== "Select Acquirer" || acquirer == undefined){
					 alert("Please select acquirer");
					 document.getElementById("previewDiv").style.display = "none";
					 return false;
				 }
				 
			 var currency = document.getElementById("currency").value;
				 if(currency == "" || currency== "Select Currency"){
						 alert("Please select Currency");
						 document.getElementById("previewDiv").style.display = "none";
						 return false;
				 }
					 
			 var dateFrom =  document.getElementById("dateFrom").value;
				 if(dateFrom == "" || dateFrom== null){
						 alert("Please select Capture From Date");
						 document.getElementById("previewDiv").style.display = "none";
						 return false;
				 }
			 var dateTo = document.getElementById("dateTo").value;
				 if(dateTo == "" || dateTo== null){
						 alert("Please select Capture To Date");
						 document.getElementById("previewDiv").style.display = "none";
						 return false;
				 }
			 
			 var token  = document.getElementsByName("token").value;
			
			renderTable();
			reloadTable();		
		});
	});

	function renderTable() {
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
	
  $('#previewDatatable').dataTable(
						{
							"footerCallback" : function(tfoot, data, start, end, display) {
								var api = this.api(), data;

								// Remove the formatting to get integer data for summation
								var intVal = function(i) {
									return typeof i === 'string' ? i.replace(
											/[\,]/g, '') * 1
											: typeof i === 'number' ? i : 0;
								};

								// Total Sale Count
								total = api.column(1).data().reduce(
										function(a, b) {
											return intVal(a) + intVal(b);
										}, 0);

								// Total Sale Count
								pageTotal = api.column(1, {
									page : 'current'
								}).data().reduce(function(a, b) {
									return intVal(a) + intVal(b);
								}, 0);

								// Total Sale Count Update footer
								$(api.column(1).footer()).html(
										'' + pageTotal.toFixed(2) + ' ' + ' ');

								// Total Refund Count
								total = api.column(2).data().reduce(
										function(a, b) {
											return intVal(a) + intVal(b);
										}, 0);

								// Total Refund Count
								pageTotal = api.column(2, {
									page : 'current'
								}).data().reduce(function(a, b) {
									return intVal(a) + intVal(b);
								}, 0);

								// Total Refund Count Update footer
								$(api.column(2).footer()).html(
										'' + pageTotal.toFixed(2) + ' ' + ' ');

								// Total acquirer Surcharge
								total = api.column(3).data().reduce(
										function(a, b) {
											return intVal(a) + intVal(b);
										}, 0);

								// Total refund count
								pageTotal = api.column(3, {
									page : 'current'
								}).data().reduce(function(a, b) {
									return intVal(a) + intVal(b);
								}, 0);

								// Total refund count Update footer
								$(api.column(3).footer()).html(
										'' + pageTotal.toFixed(2) + ' ' + ' ');

								// Total Pg Surcharge
								total = api.column(4).data().reduce(
										function(a, b) {
											return intVal(a) + intVal(b);
										}, 0);

								// Total refund amount
								pageTotal = api.column(4, {
									page : 'current'
								}).data().reduce(function(a, b) {
									return intVal(a) + intVal(b);
								}, 0);

								// Total refund amount Update footer
								$(api.column(4).footer()).html(
										'' + pageTotal.toFixed(2) + ' ' + ' ');
																		
							},
							"columnDefs": [{ 
								className: "dt-body-right",
								"targets": [0, 1, 2, 3, 4]
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
											title : 'Initiate Merchant Payout',
											exportOptions : {
												
												columns : [':visible']
											},
										} ),
									{
										extend : 'pdfHtml5',
										orientation : 'landscape',
										pageSize: 'legal',
										//footer : true,
										title : 'Initiate Merchant Payout',
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
										title : 'Initiate Merchant Payout',
										exportOptions : {
											columns : [':visible']
										}
									},
									{
										extend : 'colvis',
										columns : [ 0, 1, 2, 3, 4]
									} ],

							"ajax" :{
								
								"url" : "initiateMerchantPayoutPreview",
								"timeout" : 0,
								"type" : "POST",
								"data": function (d){
									return generatePostData(d);
									
								}
							},
							"fnDrawCallback" : function() {
									$("#previewReportBtn").removeAttr("disabled");
								    document.getElementById("loading").style.display = "none";
							},
							 "searching": false,
							 "ordering": false,
							 "processing": true,
							 "destroy": true,
						        "serverSide": false,
						        "paginationType": "full_numbers", 
						        "lengthMenu": [[10, 25, 50], [10, 25, 50]],
						        
								 "columnDefs": [
										{
										"className": "dt-center", 
										"targets": "_all"
								}],
								
							"columns": [
									{ "data": "saleCount" },
									{ "data": "refundCount" },
									{ "data": "grossTxnAmount" },
									{ "data": "totalIpayComm" },
									{ "data": "totalAcqComm" },
									{ "data": "totalMerchantAmt" },
									{ "data": "totalPayable" },
								]
						});
		
	}

	function reloadTable() {
		$("#previewReportBtn").attr("disabled", true);
		document.getElementById("loading").style.display = "block";
		//var tableObj = $('#viewDetailsDatatable');
		//var table = tableObj.DataTable();
		//table.ajax.reload();
		$('#previewDatatable').DataTable().ajax.reload();
	}

	function generatePostData(d) {
		 var merchant = document.getElementById("merchant").value;
		 var acquirer = selectedAcquirer;
		 var currency = document.getElementById("currency").value; 
		 var dateFrom =  document.getElementById("dateFrom").value;
		 var dateTo = document.getElementById("dateTo").value;
		 var token = document.getElementsByName("token")[0].value;
		 document.getElementById("previewDiv").style.display = "block";
		 
		var obj = {
			"merchantPayId": merchant,
            "acquirer": acquirer,
            "currency": currency,
            "captureDateFrom": dateFrom,
            "captureDateTo":dateTo,
            "token" : token,
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
function checkDiv(){
	
	var merchant = document.getElementById("merchant").value;
	
	if(merchant == "" || merchant== "Select Merchant"){
				 alert("Please select Merchant");
				 document.getElementById("anotherDiv").style.display= "none";
				 return false;
			 }
	
	if( selectedAcquirer.length == 0 ){
				 alert("Please select an acquirer");
				 document.getElementById("anotherDiv").style.display= "none";
				 return false;
			 }			 
	var acquirer = selectedAcquirer;
	var currency = document.getElementById("currency").value;
	
	if(currency == "" || currency== "Select Currency" ){
				 alert("Please select a currency");
				 document.getElementById("anotherDiv").style.display= "none";
				 return false;
			 }
			 
	var dateFrom = document.getElementById("dateFrom").value;
	
	if(dateFrom == "" || dateFrom== "Select Date" ){
				 alert("Please select date from ");
				 document.getElementById("anotherDiv").style.display= "none";
				 return false;
			 }
			 
	var dateTo = document.getElementById("dateTo").value;
	
	if(dateTo == "" || dateTo== "Select Date" ){
				 alert("Please select date to");
				 document.getElementById("anotherDiv").style.display= "none";
				 return false;
			 }
	
	document.getElementById("merchantPayIdForm").value = merchant;
	document.getElementById("acquirerForm").value =	acquirer;
	document.getElementById("currencyForm").value = currency;
	document.getElementById("captureDateFromForm").value = dateFrom;
	document.getElementById("captureDateToForm").value = dateTo;     
	document.getElementById("markInitiatedForm").value = false;  
	document.getElementById("downloadReportAction").submit();
	
}

function initiatePayout(){
	
	 
	var merchant = document.getElementById("merchant").value;
	
	if(merchant == "" || merchant== "Select Merchant"){
				 alert("Please select Merchant");
				 document.getElementById("anotherDiv").style.display= "none";
				 return false;
			 }
	
	if( selectedAcquirer.length == 0 ){
				 alert("Please select an acquirer");
				 document.getElementById("anotherDiv").style.display= "none";
				 return false;
			 }			 
	var acquirer = selectedAcquirer;
	var currency = document.getElementById("currency").value;
	
	if(currency == "" || currency== "Select Currency" ){
				 alert("Please select a currency");
				 document.getElementById("anotherDiv").style.display= "none";
				 return false;
			 }
			 
	var dateFrom = document.getElementById("dateFrom").value;
	
	if(dateFrom == "" || dateFrom== "Select Date" ){
				 alert("Please select date from ");
				 document.getElementById("anotherDiv").style.display= "none";
				 return false;
			 }
			 
	var dateTo = document.getElementById("dateTo").value;
	
	if(dateTo == "" || dateTo== "Select Date" ){
				 alert("Please select date to");
				 document.getElementById("anotherDiv").style.display= "none";
				 return false;
	
			 }
			 
	var payoutDate = document.getElementById("payoutDate").value;
	if(payoutDate == "" || payoutDate== "Select Date" ){
				 alert("Please select payout Date");
				 document.getElementById("anotherDiv").style.display= "none";
				 return false;
			 }
	
	document.getElementById("merchantPayIdFormInitiate").value = merchant;
	document.getElementById("acquirerFormInitiate").value =	acquirer;
	document.getElementById("currencyFormInitiate").value = currency;
	document.getElementById("captureDateFromFormInitiate").value = dateFrom;
	document.getElementById("captureDateToFormInitiate").value = dateTo;     
	document.getElementById("payoutDateFormInitiate").value = payoutDate;     
	document.getElementById("markInitiatedFormInitiate").value = true;  
	document.getElementById("loading").style.display = "block";	
	document.getElementById("initiatePayout").submit();
	document.getElementById("loading").style.display = "block";
	alert("Nodal Payout initiated Successfully ");
	//window.location.reload();
}
</script>

<form id="downloadReportAction" name="downloadReportActionName" action="downloadNodalMISReportAction" >

	<s:hidden name="token" value="%{#session.customToken}"></s:hidden>
	<s:hidden name="merchantPayId" id ="merchantPayIdForm"  value= "" ></s:hidden>
	<s:hidden name="acquirer" id ="acquirerForm" value=""></s:hidden>
	<s:hidden name="currency" id ="currencyForm" value=""></s:hidden>
	<s:hidden name="captureDateFrom" id ="captureDateFromForm" value=""></s:hidden>
	<s:hidden name="markInitiated" id ="markInitiatedForm" value=""></s:hidden>
	<s:hidden name="captureDateTo" id ="captureDateToForm" value=""></s:hidden>
	
</form>

<form id="initiatePayout" name="downloadReportActionName" action="downloadNodalMISReportAction" >

	
	<s:hidden name="token" value="%{#session.customToken}"></s:hidden>
	<s:hidden name="merchantPayId" id ="merchantPayIdFormInitiate"  value= "" ></s:hidden>
	<s:hidden name="acquirer" id ="acquirerFormInitiate" value=""></s:hidden>
	<s:hidden name="currency" id ="currencyFormInitiate" value=""></s:hidden>
	<s:hidden name="markInitiated" id ="markInitiatedFormInitiate" value=""></s:hidden>
	<s:hidden name="captureDateFrom" id ="captureDateFromFormInitiate" value=""></s:hidden>
	<s:hidden name="captureDateTo" id ="captureDateToFormInitiate" value=""></s:hidden>
	<s:hidden name="payoutDate" id ="payoutDateFormInitiate" value=""></s:hidden>
	
</form>
</body>
</html>