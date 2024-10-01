<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Add Nodal Credit Amount</title>

<!--------CSS Stylesheet------->
<link href="../css/select2.min.css" rel="stylesheet" />
<link href="../css/default.css" rel="stylesheet" type="text/css" />
<link href="../css/Jquerydatatableview.css" rel="stylesheet" />
<link href="../css/Jquerydatatable.css" rel="stylesheet" />

<!--------JS Script----------------->
<script src="../js/jquery.min.js"></script>
<script src="../js/jquery-ui.js"></script>

<!----Calender MultiDates------>
<link rel="stylesheet" type="text/css" href="../css/jquery-ui.css"></link>
<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.10.1/jquery.min.js"></script>
<script src="../js/jquery-ui.js"></script>
<script src="../js/jquery-ui-1.11.1.js"></script>
<script src="../js/jquery-ui.multidatespicker.js"></script>
<script src="../js/jquery.dataTables.js"></script>

<script>
 $(document).ready(function(){
   document.getElementById("loading").style.display = "none";
  // Initialize select2
  //$("#merchant").select2();
});
</script>

<style>
.my_class:hover{
  color: white !important;
}
.selectBox {
  position: relative;
}
.selectBox select {
  width: 95%;
}
.download-btn {
	background-color:green;
	display: block;
    width: 100%;
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
#wwctrl_nodalType {
	margin-top: 8% !important;
}
.actionMessage li {
    color: black !important;
    background-color: #ccebda !important;
    border-color: #ccebda !important;
}
.midSec{
	width: 300px;
	height: 120px;
	position: fixed;
	top: 50%;
	left: 50%;
	margin-top: -60px;
	margin-left: -150px;
	background: #fff;
	padding: 20px;
	box-sizing: border-box;
	text-align: center;
	font-size: 15px;
	border-radius: 10px;
}
.midSec button{
	padding: 3px 20px;
    font-size: 15px;
}
.midSec div{
	margin-top: 5%;
}
.staticValue{
	font-size:17px !important; 
	text-align:center; color:#496cb6;
}
.readValue{
	margin-left:-100% !important; 
	font-size:17px !important;
}
.ui-state-highlight a, .ui-widget-content .ui-state-highlight a {
	border: 1px solid #2694e8;
    background: #3baae3 url(../image/ui-bg_glass_50_3baae3_1x400.png) 50% 50% repeat-x;
    font-weight: bold;
    color: #ffffff;
}
.ui-state-active, .ui-widget-content .ui-state-active{
	border: 1px solid #aed0ea;
	background: #d7ebf9 url("../image/ui-bg_glass_80_d7ebf9_1x400.png") 50% 50% repeat-x
	color: #2779aa;
}

input:focus,
	select:focus,
	textarea:focus,
	button:focus {
		outline: none;
	}
	.box1 .media-heading{
		text-align: center;
		font-size: 14px;
	}
	.box1 .media-left{
		padding: 10px 14px;
		width: 70px;
		height: 58px;
	}
	.box1 .media-body{
		width: 80%;
		background: #f4f2f2;
		padding-top: 4%;
	}
	.cardTypeTxnDetails{
		background: #f8f8f8;
	}
	.mytable, .mytable thead th{
		text-align: center;
		font-size: 13px;
	}
	.mytable thead th{
		font-size: 14px;
	}
	.mytable thead{
		background: #428bca;
		color: #fff;
	}
	.mainDiv.txnf h3{
		color: #496cb6;
		font-size: 16px;
		margin-top: 20px;
	}
	label{
		color: #333;
		font-size: 13px;
		padding-left: 4%;
	}
	#PaymentTypePerformance{
		padding: 11px;
	}

	@keyframes spin {
	  0% { transform: rotate(0deg); }
	  100% { transform: rotate(360deg); }
	}
.table>tbody>tr>td{
	padding:12px !important;
}
.card-list-toggle{cursor: pointer; padding: 8px 12px; border: 1px solid #ccc; position: relative; background:#ddd;}
.card-list-toggle:before{position: absolute; right: 10px; top: 7px; content:"\f078"; font-family:'FontAwesome'; font-size: 15px;}
.card-list-toggle.active:before{content:"\f077";}
.card-list{display: none;}
.btn:focus{
		outline: 0 !important;
}
.dataTables_info{
	display:none !important;
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
				<td align="left"><h2>Add Nodal Credit Amount</h2>
					<div class="container">
						<div class="row padding10" style="margin-top:20px;">
							<div class="form-group col-md-3 txtnew col-sm-4 col-xs-6">
								<label for="merchant" style="margin-left: 2px;" class="aboveHead">Merchant:</label> <br />
								<s:if test="%{#session.USER.UserType.name()=='ADMIN' || #session.USER.UserType.name()=='SUBADMIN' ||    #session.USER_TYPE.name()=='SUPERADMIN'}">
									<s:select name="merchant" class="form-control" id="merchant" list="merchantList"
										headerKey="Select Merchant" headerValue="Select Merchant" listKey="emailId" listValue="businessName" autocomplete="off" />
								</s:if>
								<s:else>
									<s:select name="merchant" class="form-control" id="merchant"
										headerKey="" headerValue="ALL" list="merchantList"
										listKey="emailId" listValue="businessName" autocomplete="off" />
								</s:else>
							</div>


							<div class="form-group  col-md-3 col-sm-4 txtnew  col-xs-6">
									<label for="aquirer" >Acquirer</label> <br />
										 <s:select list="@com.pay10.commons.util.AcquirerTypeUI@values()" 
										 headerKey="Select Acquirer" headerValue="Select Acquirer"
										 name="acquirer" id="acquirer" value="name" class="form-control"/>
							</div>
						
							<div class="form-group  col-md-3 col-sm-4 txtnew  col-xs-6">
								<label for="payment" >Payment Type:</label> <br />
									<select  name="paymentMethod" id="paymentMethod" autocomplete="off" class="form-control">
										<option>Select Payment</option>
										<option>CC/DC</option>
										<option>Wallet</option>
										<option>UPI</option>
									</select>
							</div>
							
							<div class="form-group  col-md-3 col-sm-4 txtnew  col-xs-6">
								<label for="capture Date" >Capture Date Range:</label> <br />
									<s:textfield type="text" readonly="true" id="captureDateRange" name="captureDateRange"
								      class="form-control date" autocomplete="off"/>
							</div>

							<div class="form-group col-md-3 txtnew col-sm-4 col-xs-6">
							   <label for="merchant" style="margin-left: 2px;" class="aboveHead">Settlement Date:</label> <br />
							   <s:textfield type="text" readonly="true" id="settlementDate" name="settlementDate"
								class="form-control" autocomplete="off"/>
						    </div>
						
						</div>
						
						<div class="row padding10">
						 <div class="form-group  col-md-9 col-sm-4 txtnew  col-xs-6">
						  <label for="ignorePgRefList" class="aboveHead" style="margin-left:-20px !important;">PG Ref Num Ignore List:</label> <br />
							   <s:textarea type="text" id="ignorePgRefList" name="ignorePgRefList" style="height:100px !important;"
								class="form-control" autocomplete="off"/>
						  </div>
						</div>
						
						<div>
							<div class="form-group col-md-2 col-sm-3 txtnew col-xs-6 col-md-offset-5">
									<button id="viewAmountDetails" class="download-btn" onclick="openAnotherDiv()">View Amount</button>
							</div>
						</div>
						
					</div>

				</td>
			</tr>
				<tr>
					<td align="left" style="border-bottom: 1px solid #eaeaea;">&nbsp;</td>
				</tr>
			
		</table>
		
	<div id="anotherDiv" style="display:none;">
		
		
		        <div class="scrollD">
					<table id="viewDetailsDatatable" align="center" class="display" cellspacing="0" width="100%">
							<thead>
								<tr class="boxheadingsmall" style="font-size: 11px;">
									<th style="text-align:center;" data-orderable="false">Capture Date</th>
									<th style="text-align:center;" data-orderable="false">Sale Count</th>
									<th style="text-align:center;" data-orderable="false">Sale Amount</th>
									<th style="text-align:center;" data-orderable="false">Refund Count</th>
									<th style="text-align:center;" data-orderable="false">Refund Amount</th>
									<th style="text-align:center;" data-orderable="false">Net Amount</th>
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
							</tr>
						</tfoot>
					</table>
				</div>
		
		<table width="100%" align="left" cellpadding="0" cellspacing="0" class="txnf">
			<tr>
				<td align="left">
					<div class="container">
						<div class="row padding10" style="margin-top:20px;">
							<div class="form-group  col-md-4 col-sm-4 col-sm-offset-2 txtnew  col-xs-6">
									<label for="amount" >Credit Amount:</label> <br />
										<input type="text" class="form-control" id="creditAmount" autocomplete="off" onkeypress="return isNumberKey(event,this)"> 
							</div>
						
							<div class="form-group  col-md-4 col-sm-4 txtnew  col-xs-6">
								<label for="date">Credit Date:</label> <br />
									<s:textfield type="text" readonly="true" id="nodalCreditDate" name="nodalCreditDate"
								       class="form-control" autocomplete="off"/>
							</div>
						</div>
						
						<div class="row padding10">
							<div class="form-group  col-md-4 col-sm-4 col-sm-offset-2 txtnew  col-xs-6">
									<label for="amount" >Debit Amount:</label> <br />
										<input type="text" class="form-control" id="debitAmount" autocomplete="off" onkeypress="return isNumberKey(event,this)"> 
							</div>
						
							<div class="form-group  col-md-4 col-sm-4 txtnew  col-xs-6">
								<label for="date">Debit Date:</label> <br />
									<s:textfield type="text" readonly="true" id="nodalDebitDate" name="nodalDebitDate"
								     class="form-control" autocomplete="off"/>
							</div>
						</div>
						
						<div>
							<div class="form-group col-md-2 col-sm-3 txtnew col-xs-6 col-md-offset-5">
									<button id="submitDetails" class="download-btn" onclick="updateNodalDate()">Submit</button>
							</div>
						</div>
						
					</div>

				</td>
			</tr>
				<tr>
					<td align="left" style="border-bottom: 1px solid #eaeaea;">&nbsp;</td>
				</tr>
			
		</table>
		
		
	</div>
	
<script>
function openAnotherDiv(){
	document.getElementById("anotherDiv").style.display = "block";
}


function updateNodalDate(){
	
	var saleCount = 0;
	var refundCount = 0;
	var table = document.getElementById("viewDetailsDatatable");
	var rows = table.rows;	
	var length = rows.length;
	
	for(count = 1; count < length-1; count++) {
              saleCount = parseInt(saleCount) + parseInt(document.getElementById("viewDetailsDatatable").rows[count].cells[1].innerHTML);
			  refundCount = parseInt(refundCount) + parseInt(document.getElementById("viewDetailsDatatable").rows[count].cells[3].innerHTML);
			  
            }    
			
	var creditAmount = document.getElementById("creditAmount").value;
	var nodalCreditDate = document.getElementById("nodalCreditDate").value;
	var debitAmount = document.getElementById("debitAmount").value;
	var nodalDebitDate = document.getElementById("nodalDebitDate").value;
	var captureDate = document.getElementById("captureDateRange").value;
	var acquirer = document.getElementById("acquirer").value;
	var merchant = document.getElementById("merchant").value;
	var paymentMethod = document.getElementById("paymentMethod").value;
	var ignorePgRefList = document.getElementById("ignorePgRefList").value;
	var settlementDate = document.getElementById("settlementDate").value;
	var token = document.getElementsByName("token")[0].value;
	
	document.getElementById("loading").style.display = "block";	
		$
		.ajax({
			url : "updateNodalCreditDate",
			timeout : 0,
			type : "POST",
			data : {
				"saleCount" : saleCount,
				"refundCount" : refundCount,
				"actualNodalCredit" : creditAmount,
				"nodalCreditDate" : nodalCreditDate,
				"actualNodalDebit" : debitAmount,
				"nodalDebitDate" : nodalDebitDate,
				"captureDateRange" : captureDate,
				"acquirer" : acquirer,
				"merchantEmailId" : merchant,
				"paymentType" : paymentMethod,
				"token" : token,
				"settlementDate":settlementDate,
				"ignorePgRefList" : ignorePgRefList,
			},
			success : function(data) {
				alert("Nodal credit date updated ");
				document.getElementById("loading").style.display = "none";
				window.location.reload();
			},
			error : function(data) {
				
				alert("Unable to update nodal date");
				document.getElementById("loading").style.display = "none";
				window.location.reload();
			}
		});
}
</script>

<!-----------------DatePicker-------------------->
<script type="text/javascript">
 $(document).ready(function(){
	 <!---Multi Date Picker---->
		$('#captureDateRange').multiDatesPicker({
			numberOfMonths: 2,
			multidate: true,
			dateFormat: "dd/mm/yy",
			minDate : new Date(2018, 9, 1),
            maxDate : new Date()
		});
		
	$.datepicker._selectDateOverload = $.datepicker._selectDate;
	$.datepicker._selectDate = function (id, dateStr) {
		var target = $(id);
		var inst = this._getInst(target[0]);
		inst.inline = true;
		$.datepicker._selectDateOverload(id, dateStr);
		inst.inline = false;
		if (target[0].multiDatesPicker != null) {
			target[0].multiDatesPicker.changed = false;
		} else {
			target.multiDatesPicker.changed = false;
		}
			 this._updateDatepicker(inst);
						 
	};
	<!----Single Date Selection------>
			$("#settlementDate").datepicker({
				prevText : "click for previous months",
				nextText : "click for next months",
				showOtherMonths : true,
				dateFormat : 'dd/mm/yy',
				selectOtherMonths : false,
				maxDate : new Date(),
				autoHide: true
			});
			
			$("#nodalCreditDate").datepicker({
				prevText : "click for previous months",
				nextText : "click for next months",
				showOtherMonths : true,
				dateFormat : 'dd-mm-yy',
				selectOtherMonths : false,
				maxDate : new Date()
			});
			
			$("#nodalDebitDate").datepicker({
				prevText : "click for previous months",
				nextText : "click for next months",
				showOtherMonths : true,
				dateFormat : 'dd-mm-yy',
				selectOtherMonths : false,
				maxDate : new Date()
			});
});		
</script>	

<!-------------Datatable Code---------->
<script type="text/javascript">
	$(document).ready(function() {

		$("#viewAmountDetails").click(function(env) {
			var merchant = document.getElementById("merchant").value;
			if(merchant == "" || merchant== "Select Merchant"){
				 alert("Please select Merchant");
				 document.getElementById("anotherDiv").style.display= "none";
				 return;
			 }
			 var acquirer = document.getElementById("acquirer").value;
		    if(acquirer == "" || acquirer== "Select Acquirer"){
				 alert("Please select acquirer");
				 document.getElementById("anotherDiv").style.display= "none";
				 return false;
			 }
			 
		 var paymentMethod = document.getElementById("paymentMethod").value;
			 if(paymentMethod == "" || paymentMethod== "Select Payment"){
					 alert("Please select Payment Method");
					 document.getElementById("anotherDiv").style.display= "none";
					 return false;
			 }
				 
		 var captureDateRange =  document.getElementById("captureDateRange").value;
		     if(captureDateRange == "" || captureDateRange== null){
					 alert("Please select Capture Date(s)");
					 document.getElementById("anotherDiv").style.display= "none";
					 return false;
			 }
		 var settlementDate = document.getElementById("settlementDate").value;
		     if(settlementDate == "" || settlementDate== null){
					 alert("Please select Settlement Date");
					 document.getElementById("anotherDiv").style.display= "none";
					 return false;
			 }
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
	
  $('#viewDetailsDatatable').dataTable(
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

								// Total sale amount
								total = api.column(2).data().reduce(
										function(a, b) {
											return intVal(a) + intVal(b);
										}, 0);

								// Total sale amount
								pageTotal = api.column(2, {
									page : 'current'
								}).data().reduce(function(a, b) {
									return intVal(a) + intVal(b);
								}, 0);

								// Total sale amount Update footer
								$(api.column(2).footer()).html(
										'' + pageTotal.toFixed(2) + ' ' + ' ');

								// Total refund count
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

								// Total refund amount
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
										
								
								// Total net amount
								total = api.column(5).data().reduce(
										function(a, b) {
											return intVal(a) + intVal(b);
										}, 0);

								// Total net amount
								pageTotal = api.column(5, {
									page : 'current'
								}).data().reduce(function(a, b) {
									return intVal(a) + intVal(b);
								}, 0);

								// Total net amount Update footer
								$(api.column(5).footer()).html(
										'' + pageTotal.toFixed(2) + ' ' + ' ');
										
																		
							},
							
								dom : 'BTrftlpi',

							"ajax" :{
								
								"url" : "viewNodalAmount",
								"type" : "POST",
								"data": function (d){
									return generatePostData(d);
									
								}
							},
							"fnDrawCallback" : function() {
							    $("#viewAmountDetails").removeAttr("disabled");
								document.getElementById("loading").style.display = "none";
								
								var saleTotal = document.getElementById("viewDetailsDatatable").rows[document.getElementById("viewDetailsDatatable").rows.length -1].cells[2].innerHTML.trim();
			
								var refundTotal = document.getElementById("viewDetailsDatatable").rows[document.getElementById("viewDetailsDatatable").rows.length -1].cells[4].innerHTML.trim();
			
								
								document.getElementById("creditAmount").value = saleTotal;
								document.getElementById("debitAmount").value = refundTotal;
							},
							 "searching": false,
							 "ordering": false,
							 "processing": true,
						     "serverSide": true,
							 "paging": false,
						     "destroy": true,  
						       "columnDefs": [
										{
										"className": "dt-center", 
										"targets": "_all"
								}],
							"columns": [
									{ "data": "captureDate" },
									{ "data": "saleCount" },
									{ "data": "saleAmount" },
									{ "data": "refundCount" },
									{ "data": "refundAmount" },
									{ "data": "netAmount" }
								]
						
						});
		
	}

	function reloadTable() {
		$("#viewAmountDetails").attr("disabled", true);
		document.getElementById("loading").style.display = "block";
		//var tableObj = $('#viewDetailsDatatable');
		//var table = tableObj.DataTable();
		//table.ajax.reload();
		$('#viewDetailsDatatable').DataTable().ajax.reload();
		
	}

	function generatePostData(d) {
		var merchant = document.getElementById("merchant").value; 
		var acquirer = document.getElementById("acquirer").value;
		var paymentMethod = document.getElementById("paymentMethod").value;
		var captureDateRange =  document.getElementById("captureDateRange").value;
		var settlementDate = document.getElementById("settlementDate").value;
		var ignorePgRefList = document.getElementById("ignorePgRefList").value;
		var token  = document.getElementsByName("token")[0].value;
		document.getElementById("anotherDiv").style.display= "block";
		
		var obj = {
			"merchantEmailId": merchant,
            "acquirer": acquirer,
            "paymentType": paymentMethod,
            "captureDateRange": captureDateRange,
            "settlementDate":settlementDate,
            "ignorePgRefList" : ignorePgRefList,
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

<!----------Validation to Enter Amount--------->
<script>
function isNumberKey(evt, element) {
  var charCode = (evt.which) ? evt.which : event.keyCode
  if (charCode > 31 && (charCode < 44 || charCode > 57) && !(charCode == 46 || charCode == 8))
    return false;
  else {
    var len = $(element).val().length;
    var index = $(element).val().indexOf('.');
    if (index > 0 && charCode == 46) {
      return false;
    }
    if (index > 0) {
      var CharAfterdot = (len + 1) - index;
      if (CharAfterdot > 3) {
        return false;
      }
    }

  }
  var typedChar = String.fromCharCode(charCode);

    // Allow numeric characters
    if (/\d/.test(typedChar)) {
        return;
    }

    // Allow the minus sign (-) if the user enters it first
    if (typedChar == "-" && this.value == "") {
        return;
    }
  return true;
}
</script>

</body>
</html>