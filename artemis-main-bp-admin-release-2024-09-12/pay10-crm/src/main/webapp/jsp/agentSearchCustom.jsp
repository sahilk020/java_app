<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib prefix="s" uri="/struts-tags"%>
<html>
<head>

<style>
.heading {
	text-align: center;
	color: #496cb6;
	font-weight: bold;
	font-size: 22px;
}

.samefnew {
	width: 15.5% !important;
	float: left;
	font-size: 12px;
	font-weight: 600;
	color: #333;
	line-height: 22px;
	margin: 0 0 0 10px;
}

.cust {
	width: 22% !important;
	float: left;
	font-size: 12px;
	font-weight: 600;
	color: #333;
	line-height: 22px;
	margin: 0 0 0 0px !important;
}

.submit-button {
	width: 10% !important;
	height: 28px !important;
	margin-top: -4px !important;
}

.MerchBx {
	min-width: 92%;
	margin: 15px;
	margin-top: 25px !important;
	padding: 0;
}

table.dataTable thead .sorting {
	background: none !important;
}

.sorting_asc {
	background: none !important;
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

table.dataTable.display tbody tr.odd>.sorting_1 {
	background-color: #e6e6ff !important;
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

td.wrapData {
	white-space: nowrap;
	width: 100%;
}
</style>

<title>Agent Search</title>

<link href="../css/Jquerydatatableview.css" rel="stylesheet" />
<link href="../css/Jquerydatatable.css" rel="stylesheet" />
<link rel="stylesheet" href="../css/loader.css">
<link href="../css/default.css" rel="stylesheet" type="text/css" />

<link href="../css/jquery-ui.css" rel="stylesheet" />
<script src="../js/jquery.js"></script>
<script src="../js/jquery.dataTables.js"></script>
<script src="../js/jquery-ui.js"></script>
<!-- <script src="../js/jquery.min.js" type="text/javascript"></script> -->
<script src="../js/core/popper.min.js"></script>
<script src="../js/core/bootstrap-material-design.min.js"></script>
<script src="../js/plugins/perfect-scrollbar.jquery.min.js"></script>
<!-- Control Center for Material Dashboard: parallax effects, scripts for the example pages etc -->
<script src="../js/material-dashboard.js?v=2.1.0" type="text/javascript"></script>


<script type="text/javascript" src="../js/moment.js"></script>
<script type="text/javascript" src="../js/daterangepicker.js"></script>
<link href="../css/loader.css" rel="stylesheet" type="text/css" />
<script src="../js/jquery.popupoverlay.js"></script>
<script type="text/javascript" src="../js/dataTables.buttons.js"></script>
<script type="text/javascript" src="../js/pdfmake.js"></script>
<link href="../fonts/css/font-awesome.min.css" rel="stylesheet">
<script src="../js/messi.js"></script>
<link href="../css/messi.css" rel="stylesheet" />
<script src="../js/commanValidate.js"></script>


<script type="text/javascript">
$(document).ready(function(){
   document.getElementById("loading").style.display = "none";
});
</script>

<script type="text/javascript">

$(function() {
	
	var table = $('#searchTransactionDatatable').DataTable({
		
		dom: 'BTrftlpi',
	               destroy : true,
	               buttons : [
									{
										extend : 'copyHtml5',
										exportOptions : {
											columns : [':visible']
										}
									},
									{
										extend : 'csvHtml5',
										title : 'Agent Search Report',
										exportOptions : {
											columns : [':visible']
										}
									},
									{
										extend : 'pdfHtml5',
										orientation : 'landscape',
										title : 'Agent Search Report',
										exportOptions : {
											columns : [':visible']
										}
									},
									{
										extend : 'print',
										orientation : 'landscape',
										title : 'Agent Search Report',
										exportOptions : {
											columns : [':visible']
										}
									},
									{
										extend : 'colvis',
										columns : [2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20 , 21, 22]
									}
								],
				"searching": false,
                "paging": true,
                "lengthMenu": [ [10, 25, 50, 100, -1], [10, 25, 50, 100, "All"] ],
                "pagingType": "full_numbers",
                "pageLength": 10
	});
	
    $('#searchButton').on('click', function() {
		 var orderId = document.getElementById("orderid").value;
		 var pgRefId = document.getElementById("pgref").value;
		 var acquirerId = document.getElementById("acqId").value;
		 var customerEmail = document.getElementById("customerEmail").value;
		 var customerPhone = document.getElementById("customerPhone").value;
		 if ((orderId == "") && (pgRefId == "") && (acquirerId=="") && (customerEmail == "") && (customerPhone=="")){
			 alert("Please enter atleast one value !!")
			 return false;
		 }
		 var token  = document.getElementsByName("token")[0].value;
		 document.getElementById("loading").style.display = "block";
		 
        var table = $('#searchTransactionDatatable').DataTable({
			"columnDefs": [
				        {
							"className": "dt-center", 
						"targets": "_all"
						},
						{
							type: 'tDate',
							'targets' : [4]
						}
				],
            order: [[ 4, 'desc' ]],
			
			dom: 'BTrftlpi',
	               buttons : [
									{
										extend : 'copyHtml5',
										exportOptions : {
											columns : [':visible']
										}
									},
									{
										extend : 'csvHtml5',
										title : 'Search Transaction Report',
										exportOptions : {
											columns : [':visible']
										}
									},
									{
										extend : 'pdfHtml5',
										orientation : 'landscape',
										title : 'Search Transaction Report',
										exportOptions : {
											columns : [':visible']
										}
									},
									{
										extend : 'print',
										orientation : 'landscape',
										title : 'Search Transaction Report',
										exportOptions : {
											columns : [':visible']
										}
									},
									{
										extend : 'colvis',
										columns : [2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20 , 21, 22]
									}
								],
			"searching": false,
			"destroy": true,
            "paging": true,
            "lengthMenu": [ [10, 25, 50, 100, -1], [10, 25, 50, 100, "All"] ],
				     "columnDefs": [
										{
										"type": "html-num-fmt", 
										"targets": [3,4],
										class:"wrapData"
										}
									],
            "pagingType": "full_numbers",
            "pageLength": 10,
			ajax: function (data, callback, settings) {
         $.ajax({				
				    "url": "agentSearchActionCustom",
				    "type": "POST",
				    "data": {
						"orderId":orderId,
						"pgRefNum":pgRefId,
						"acquirerId":acquirerId,
						"customerEmail":customerEmail,
						"customerPhone":customerPhone,
						"struts.token.name": "token",
						},
					
					    success:function(data){
								document.getElementById("loading").style.display = "none";
                                callback(data);
                            },
					    error:function(data) {
						        document.getElementById("loading").style.display = "none";
					        }
		        });
				   
				  },
				  "columns": [
				        { "data": "transactionId" },
					    { "data": "pgRefNum" },
						{ "data": "merchant" },
			            { "data": "orderId"},
			            { "data": "tDate",
                          "width": "10%"
						},
			            { "data": "paymentType" },
			            { "data": "mopType" },
						{ "data": "txnType" },
			            { "data": "cardNum" },
			            { "data": "status" },
						{ "data": "amount" },
						{ "data": "totalAmount" },
						{ "data": "custName" },
					    { "data": "rrn" },
						{ "data": "acqId" },
						{ "data": "ipayResponseMessage" },
						{ "data": "acquirerTxnMessage" },
						{ "data": "requestDate" },
						{ "data": "refund_txn_id" },
						{ "data": "responseCode" },
						{ "data": "customerEmail" },
						{ "data": "customerPhone" },
						{ "data": "acquirer",
						  'render' : function(data, type, row, meta) {
			    					var userType = "<s:property value='%{#session.USER.UserType.name()}'/>";
									if (userType == "ADMIN" || userType == "SUBADMIN" || userType == "SUPERADMIN" || userType == "AGENT") {
										return data;
									}else if(userType == "MERCHANT"  || userType == "SUBUSER"){
								  		  return null;
								    }
							}
						},
						{ "data": "arn" },
						{ "data": "issuerBank" }
						
			        ]

        });
    });
});

</script>
<script>
function checkRefNo(){
	var refValue = document.getElementById("pgref").value;
	var refNoLength = refValue.length;
	if((refNoLength <16) && (refNoLength >0)){
		document.getElementById("searchButton").disabled = true;
		document.getElementById("searchButton").style.backgroundColor = "#b3e6b3";
		document.getElementById("validRefNo").style.display = "block";
	}
	else if(refNoLength == 0){
		document.getElementById("searchButton").disabled = false;
		document.getElementById("searchButton").style.backgroundColor = "#39ac39";
		document.getElementById("validRefNo").style.display = "none";
	}else{
		document.getElementById("searchButton").disabled = false;
		document.getElementById("searchButton").style.backgroundColor = "#39ac39";
        document.getElementById("validRefNo").style.display = "none";
	}
}

function checkEmail(){
	var reg = /^([A-Za-z0-9_\-\.])+\@([A-Za-z0-9_\-\.])+\.([A-Za-z]{2,4})$/;
	var emailField = document.getElementById("customerEmail").value;
    if (emailField !== "" && reg.test(emailField) == false) 
    {
		document.getElementById("searchButton").disabled = true;
		document.getElementById("searchButton").style.backgroundColor = "#b3e6b3";
		document.getElementById("validcustomerEmail").style.display = "block";
	}else{
		document.getElementById("searchButton").disabled = false;
		document.getElementById("searchButton").style.backgroundColor = "#39ac39";
        document.getElementById("validcustomerEmail").style.display = "none";

	}
}

function checkPhoneNumber(){
	var phreg =/^[0-9_]+$/;
	var phoneField = document.getElementById("customerPhone").value;
    if (phoneField !== "" && phreg.test(phoneField) == false) 
    {
		document.getElementById("searchButton").disabled = true;
		document.getElementById("searchButton").style.backgroundColor = "#b3e6b3";
		document.getElementById("validcustomerPhone").style.display = "block";
	}else{
		document.getElementById("searchButton").disabled = false;
		document.getElementById("searchButton").style.backgroundColor = "#39ac39";
        document.getElementById("validcustomerPhone").style.display = "none";
	}
}
	
function checkAcqId(){
	var AcqIdVal = document.getElementById("acqId").value;
	if(AcqIdVal == "0"){
		document.getElementById("searchButton").disabled = true;
		document.getElementById("validAcqErr").style.display = "block";
	}
	else{
		document.getElementById("searchButton").disabled = false;
		document.getElementById("validAcqErr").style.display = "none";
	}
}
</script>

</head>
<body id="mainBody">

	<div id="loading" style="text-align: center;">
		<img id="loading-image" style="width:70px;height:70px;" src="../image/sand-clock-loader.gif" alt="Sending SMS..." />
	</div>

	<div style="overflow: scroll !important;">
		<!-- <h2 class="heading" style="margin-bottom: -30px !important;">Agent
			Search</h2> -->
	
		<table class="table98 padding0">

			<tr>
				<td align="center">&nbsp;</td>

			</tr>

			<tr>
				<td align="center">&nbsp;</td>
				<td height="10" align="center">


					<table id="mainTable" width="100%" border="0" align="center"
						cellpadding="0" cellspacing="0" class="txnf">

						<tr>

							<td colspan="5" align="left" valign="top"><div
									class="MerchBx">
									
										<div class="col-md-12">
											<div class="card ">
											  <div class="card-header card-header-rose card-header-text">
												<div class="card-text">
												  <h4 class="card-title">Agent Search</h4>
												</div>
											  </div>
											  <div class="card-body ">
												<div class="container">
												  <div class="row">
													  
														  <div class="col-sm-6 col-lg-3">
															<label>Order ID:</label><br>
															<div class="txtnew">
																<input type="text" id="orderid" value="" class="input-control">
															</input>
														  </div>
														  </div>
														  <div class="col-sm-6 col-lg-3">
															<label>Acquirer ID:</label><br>
															
															<div class="txtnew">
																<input type="text" id="acqId" value="" class="input-control" onblur="checkAcqId()">
																	</input>
															</div>
															<span id="validAcqErr" style="color:red; display:none; margin-left:5px;">Please Enter Valid Acq Id</span>
														  </div>
														  <div class="col-sm-6 col-lg-3">
															<label>PG REF Number :</label><br>
															<div class="txtnew">
																<input type="text" id="pgref" value="" class="input-control" onblur="checkRefNo()" autocomplete="off"
																	onkeypress="javascript:return isNumber (event)" maxlength="16">
																</input>
															</div>
															<span id="validRefNo" style="color:red; display:none; margin-left:5px;">Please Enter 16 Digit PG Ref No.</span>
														</div>
														<div class="col-sm-6 col-lg-3">
															<label>Customer Email :</label><br>
															<div class="txtnew">
																<input type="text" id="customerEmail" value=""
																	class="input-control" onblur="checkEmail()"
																	autocomplete="off"> </input>
															</div>
															<span id="validcustomerEmail"
																style="color: red; display: none; margin-left: 5px;">Please
																Enter Valid Email</span>
														</div>
														<div class="col-sm-6 col-lg-3">
															<label>Mobile :</label><br>
															<div class="txtnew">
																<input type="text" id="customerPhone" value=""
																	class="input-control" onblur="checkPhoneNumber()"
																	autocomplete="off"> </input>
															</div>
															<span id="validcustomerPhone"
																style="color: red; display: none; margin-left: 5px;">Please
																Enter Valid Phone Number</span>
														</div>
														
													 
													 
													<div class="col-sm-6 col-lg-3">
													
														<div class="txtnew">
															<input type="button" id="searchButton" value="Submit"
																class="btn btn-primary  mt-4 submit_btn">
																
														</div>
													</div>
													
													
												  </div>
												  </div>
												  
											
											  </div>
											</div>
										  </div>
										

									

								
									

									</div>

								</div></td>
						</tr>

						


					</table>


				</td>
		</table>
		<tr>
			<td align="left" valign="top"
				style="padding: 10px; border-right: 1px solid #e0d2d2c4;"><br>
				<div class="scrollD" style="margin-top: 15px !important;">
					<table id="searchTransactionDatatable" align="center"
						class="display" cellspacing="0" width="100%">
						<thead>
							<tr class="boxheadingsmall" style="font-size: 11px;">
								<th style="text-align: center;" data-orderable="false">TXN
									ID</th>
								<th style="text-align: center;" data-orderable="false">PG
									Ref No</th>
								<th style="text-align: center;" data-orderable="false">Merchant</th>
								<th style="text-align: center;" data-orderable="false">Order
									ID</th>
								<th style="text-align: center;" data-orderable="false"
									nowrap>Date</th>
								<th style="text-align: center;" data-orderable="false">Payment
									Type</th>
								<th style="text-align: center;" data-orderable="false">MOP
									Type</th>
								<th style="text-align: center;" data-orderable="false">TXN
									Type</th>
								<th style="text-align: center;" data-orderable="false">Card
									Number</th>
								<th style="text-align: center;" data-orderable="false">Status</th>
								<th style="text-align: center;" data-orderable="false">Amount</th>
								<th style="text-align: center;" data-orderable="false">Total
									Amount</th>
								<th style="text-align: center;" data-orderable="false">Customer
									Name</th>
								<th style="text-align: center;" data-orderable="false">RRN</th>
								<th style="text-align: center;" data-orderable="false">ACQ
									ID</th>
								<th style="text-align: center;" data-orderable="false">PG
									Response MSG</th>
								<th style="text-align: center;" data-orderable="false">Acquirer
									Response MSG</th>
								<th style="text-align: center;" data-orderable="false">Request
									Date</th>
								<th style="text-align: center;" data-orderable="false">Refund
									Order Id</th>
								<th style="text-align: center;" data-orderable="false">PG
									Response Code</th>
								<th style="text-align: center;" data-orderable="false">Customer
									Email</th>
								<th style="text-align: center;" data-orderable="false">Customer
									Ph Number</th>

								<s:if
									test="%{#session.USER.UserType.name()=='ADMIN' || #session.USER.UserType.name()=='SUBADMIN' || #session.USER.UserType.name()=='SUPERADMIN' || #session.USER.UserType.name()=='AGENT'}">
									<th style="text-align: center;" data-orderable="false">Acquirer</th>
								</s:if>

								<s:else>
									<th></th>
								</s:else>

								<th style="text-align: center;" data-orderable="false">ARN</th>
								<th style="text-align: center;" data-orderable="false">Issuer
									Bank</th>
							</tr>
						</thead>

					</table>
				</div></td>
		</tr>
	</div>

</body>
</html>
