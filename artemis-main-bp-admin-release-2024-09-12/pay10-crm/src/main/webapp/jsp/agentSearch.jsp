<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib prefix="s" uri="/struts-tags"%>
<html>
<head>

<style>
.dt-buttons.btn-group.flex-wrap {
	display: none;
}

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

#searchButton{
margin-top: 25px;
}
</style>

<title>Agent Search</title>
<link rel="shortcut icon" href="assets/media/images/paylogo.svg" />
<!--begin::Fonts-->
<link rel="stylesheet"
	href="https://fonts.googleapis.com/css?family=Inter:300,400,500,600,700" />
<!--end::Fonts-->
<!--begin::Vendor Stylesheets(used by this page)-->
<link
	href="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.css"
	rel="stylesheet" type="text/css" />
<link href="../assets/plugins/custom/datatables/datatables.bundle.css"
	rel="stylesheet" type="text/css" />

<!--end::Vendor Stylesheets-->
<!--begin::Global Stylesheets Bundle(used by all pages)-->
<link href="../assets/plugins/global/plugins.bundle.css"
	rel="stylesheet" type="text/css" />
<link href="../assets/css/style.bundle.css" rel="stylesheet"
	type="text/css" />
<link href="../assets/css/custom/custom-style.css" rel="stylesheet"
	type="text/css" />

<script src="../assets/plugins/global/plugins.bundle.js"></script>
<script src="../assets/js/scripts.bundle.js"></script>
<link href="../css/select2.min.css" rel="stylesheet" />
<script src="../js/jquery.select2.js" type="text/javascript"></script>

<script type="text/javascript">
$(document).ready(function(){
   /* document.getElementById("loading").style.display = "none"; */
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
		/*  document.getElementById("loading").style.display = "block"; */
		 
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
				    "url": "agentSearchAction",
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
								/* document.getElementById("loading").style.display = "none"; */
								
                                callback(data);
                            },
					    error:function(data) {
						       /*  document.getElementById("loading").style.display = "none"; */
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
	var reg = /^([A-Za-z0-9_\-\.\+])+\@([A-Za-z0-9_\-\+])+\.([A-Za-z]{2,4})$/;
	//var reg = /^([A-Za-z0-9_\-\.])+\@([A-Za-z0-9_\-\.])+\.([A-Za-z]{2,4})$/;
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
		document.getElementById("searchButton").style.backgroundColor = "#39ac39";
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

$('#searchTransactionDatatable').on( 'draw.dt', function () {
	enableBaseOnAccess();
} );
function enableBaseOnAccess() {
		setTimeout(function(){
			if ($('#searchAgent').hasClass("active")) {
				var menuAccess = document.getElementById("menuAccessByROLE").value;
				var accessMap = JSON.parse(menuAccess);
				var access = accessMap["searchAgent"];
				if (access.includes("Update")) {
					var edits = document.getElementsByName("merchantEdit");
					for (var i = 0; i < edits.length; i++) {
						var edit = edits[i];
						edit.disabled=false;
					}
				}
			}
		},500);
}
</script>

</head>
<body id="mainBody">
	<div class="content d-flex flex-column flex-column-fluid"
		id="kt_content">
		<!-- <div id="loading" style="text-align: center;">
		<img id="loading-image" style="width:70px;height:70px;" src="../image/sand-clock-loader.gif" alt="Sending SMS..." />
	</div> -->

		<!--begin::Toolbar-->
		<div class="toolbar" id="kt_toolbar">
			<!--begin::Container-->
			<div id="kt_toolbar_container"
				class="container-fluid d-flex flex-stack">
				<!--begin::Page title-->
				<div data-kt-swapper="true" data-kt-swapper-mode="prepend"
					data-kt-swapper-parent="{default: '#kt_content_container', 'lg': '#kt_toolbar_container'}"
					class="page-title d-flex align-items-center me-3 flex-wrap mb-5 mb-lg-0 lh-1">
					<!--begin::Title-->
					<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">Agent
						Search</h1>
					<!--end::Title-->
					<!--begin::Separator-->
					<span class="h-20px border-gray-200 border-start mx-4"></span>
					<!--end::Separator-->
					<!--begin::Breadcrumb-->
					<ul
						class="breadcrumb breadcrumb-separatorless fw-semibold fs-7 my-1">
						<!--begin::Item-->
						<li class="breadcrumb-item text-muted"><a href="home"
							class="text-muted text-hover-primary">Dashboard</a></li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item"><span
							class="bullet bg-gray-200 w-5px h-2px"></span></li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item text-muted">Agent Access</li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item"><span
							class="bullet bg-gray-200 w-5px h-2px"></span></li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item text-dark">Agent Search</li>
						<!--end::Item-->
					</ul>
					<!--end::Breadcrumb-->
				</div>
				<!--end::Page title-->

			</div>
			<!--end::Container-->
		</div>
		<!--end::Toolbar-->

		<div class="post d-flex flex-column-fluid" id="kt_post">
			<div id="kt_content_container" class="container-xxl">
				<div style="overflow: scroll !important;" class="container-xxl">
					<!-- <h2 class="heading" style="margin-bottom: -30px !important;">Agent
			Search</h2> -->
<div class="row my-5">

												
													<div class="col">
														<div class="card">
															<div class="card-body">
					<table class="table98 padding0">

						<!-- <tr>
				<td align="center">&nbsp;</td>

			</tr> -->

						<tr>
							<!-- <td align="center">&nbsp;</td> -->
							<td height="0" align="center" cellpadding="0" cellspacing="0">


								<table id="mainTable" width="100%" border="0" align="center"
									cellpadding="0" cellspacing="0" class="txnf">

									<tr>

										<td colspan="0" align="left" valign="top"><div
												class="MerchBx">


																<div class="row g-9 mb-8">
																	<div class="col-md-3 fv-row">
																		<label
																			class="d-flex align-items-center fs-6 fw-bold mb-2">
																			<span class="">Order ID</span>
																		</label> <input type="text" id="orderid" value=""
																			class="form-control form-control-solid"> </input>

																	</div>
																	<div class="col-md-3 fv-row">
																		<label class="d-flex align-items-center fs-6 fw-bold mb-2">
																		<span class="">Acquirer ID</span></label>

																			<input type="text" id="acqId" value=""
																				class="form-control form-control-solid"
																				onblur="checkAcqId()"> </input>
																		
																		<span id="validAcqErr"
																			style="color: red; display: none; margin-left: 5px;">Please
																			Enter Valid Acq Id</span>
																	</div>
																	<div class="col-md-3 fv-row">
																		<label
																			class="d-flex align-items-center fs-6 fw-bold mb-2">
																			<span class="">PG REF Number</span></label>
																		
																			<input type="text" id="pgref" value=""
																				class="form-control form-control-solid"
																				onblur="checkRefNo()" autocomplete="off"
																				onkeypress="javascript:return isNumber (event)"
																				maxlength="16"> </input>
																	
																		<span id="validRefNo"
																			style="color: red; display: none; margin-left: 5px;">Please
																			Enter 16 Digit PG Ref No.</span>
																	</div>
																	<div class="col-md-3 fv-row">
																		<label
																			class="d-flex align-items-center fs-6 fw-bold mb-2">
																			<span class="">Customer Email</span></label>
																		
																			<input type="text" id="customerEmail" value=""
																				class="form-control form-control-solid"
																				onblur="checkEmail()" autocomplete="off"> </input>
																		
																		<span id="validcustomerEmail"
																			style="color: red; display: none; margin-left: 5px;">Please
																			Enter Valid Email</span>
																	</div>
																
																	<div class="col-md-3 fv-row">
																		<label
																			class="d-flex align-items-center fs-6 fw-bold mb-2">
																			<span class="">Mobile</span>
																			</label>
																		
																			<input type="text" id="customerPhone" value=""
																				class="form-control form-control-solid"
																				onblur="checkPhoneNumber()" autocomplete="off">
																			</input>
																		
																		<span id="validcustomerPhone"
																			style="color: red; display: none; margin-left: 5px;">Please
																			Enter Valid Phone Number</span>
																	</div>



																	<div class="col-md-3 fv-row">

																	
																			<input type="button" id="searchButton" value="Submit"
																				class="btn btn-primary">

																		</div>
																

																</div>

															</div>
														</div>



											</td>
										
											</div>
									</tr>




								</table>


							</td>
					</table>
					</div>
					</div>
					</div>
					</div>
					
					<tr>
						<td align="left" valign="top"
							style="padding: 0px; border-right: 1px solid #e0d2d2c4;"><br>

							<!-- <div class="row my-5"> -->
							<div class="row">
								<div class="col-md-12">
									<div class="card ">

										<div class="card-body ">
											<div class="scrollD " style="margin-top: 15px !important;">
												<div class="row g-9 mb-8">

													<div class="row g-9 mb-8 justify-content-end">
														<div class="col-lg-2 col-sm-12 col-md-6">
															<select name="currency" data-control="select2"
																data-placeholder="Actions" id="actions11"
																class="form-select form-select-solid actions dropbtn1"
																data-hide-search="true" onchange="myFunctions();">
																<option value="">Actions</option>
																<option value="copy">Copy</option>
																<option value="csv">CSV</option>
																<option value="pdf">PDF</option>

															</select>
														</div>
														<div class="col-lg-4 col-sm-12 col-md-6">
															<div class="dropdown1">
																<button
																	class="form-select form-select-solid actions dropbtn1">Customize
																	Columns</button>
																<div class="dropdown-content1">
																	<a class="toggle-vis" data-column="2">Merchant</a> <a
																		class="toggle-vis" data-column="3">Order Id</a> <a
																		class="toggle-vis" data-column="4">Date</a> <a
																		class="toggle-vis" data-column="5">Payment Type</a> <a
																		class="toggle-vis" data-column="6">Mop Type</a> <a
																		class="toggle-vis" data-column="7">Txn Type</a>
																	<a class="toggle-vis" data-column="8">Card Number
																		</a> <a class="toggle-vis" data-column="9">Status</a> 
																		<a class="toggle-vis" data-column="10">Amount</a> 	
                                                                    <a class="toggle-vis" data-column="11">Total Amount</a>
                                                                    <a class="toggle-vis" data-column="12">Customer Name</a>
                                                                    <a class="toggle-vis" data-column="13">RRN</a>
                                                                    <a class="toggle-vis" data-column="14">ACQ ID</a>
                                                                    <a class="toggle-vis" data-column="15">PG Response MSG</a>
                                                                    <a class="toggle-vis" data-column="16">Acquirer Response MSG</a>
                                                                    <a class="toggle-vis" data-column="17">Request Date</a>
                                                                    <a class="toggle-vis" data-column="18">Refund Order Id</a>
                                                                    <a class="toggle-vis" data-column="19">PG Response Code</a>
                                                                    <a class="toggle-vis" data-column="21">customer Ph Number</a>
                                                                    <a class="toggle-vis" data-column="22">Acquirer</a>
																</div>
															</div>
														</div>
													</div>








													<%-- 
						<div class="row g-9 mb-8 justify-content-end">
							<div class="col-lg-4 col-sm-12 col-md-6">
							  <select name="currency" data-control="select2" data-placeholder="Actions"
								class="form-select form-select-solid actions" data-hide-search="true">
								<option value="">Actions</option>
								<option value="copy">Copy</option>
								<option value="csv" >CSV</option>
								 <option value="pdf">PDF</option>        
								
								
							  </select>
							</div>
							<div class="col-lg-4 col-sm-12 col-md-6">
							  <select name="currency" data-control="select2" data-placeholder="Customize Columns"
								class="form-select form-select-solid actions" data-hide-search="true">
							   <option value="">Customize Columns</option>
								<option value="">Case ID</option>
								<option value="">Chargeback Id</option>
								<option value="">Merchant Name</option>
								<option value="">Order Id</option>
								 <option value="">PG REF NUM</option>
								 <option value="">Chargeback Type</option>
								 <option value="">Chargeback Status</option>
								  <option value="">Merchant Amount</option>
								   <option value="">Due Date</option>
																			   
								   <option value="">Creation Date</option>
						   
																				
							  </select>
							</div>
						  </div> --%>
													<div
														class="table-responsive dataTables_wrapper dt-bootstrap4 no-footer">
														<table id="searchTransactionDatatable" align="center"
															class=" table table-striped table-row-bordered gy-5 gs-7"
															cellspacing="0" width="100%">
															<thead>
																<tr class="boxheadingsmall fw-bold fs-6 text-gray-800"
																	style="font-size: 11px;">
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
													</div>
												</div>
											</div>

										</div>
									</div>
								</div>
							</div></td>
					</tr>
				</div>
			</div>
		</div>
	</div>
	<script type="text/javascript">
			$('a.toggle-vis').on('click', function(e) {
				/* debugger */
				e.preventDefault();
				table = $('#searchTransactionDatatable').DataTable();
				// Get the column API object
				var column1 = table.column($(this).attr('data-column'));
				// Toggle the visibility
				column1.visible(!column1.visible());
				if ($(this)[0].classList[1] == 'activecustom') {
					$(this).removeClass('activecustom');
				} else {
					$(this).addClass('activecustom');
				}
			});
		</script>

	<script type="text/javascript">
		function myFunctions() {
			debugger
			var x = document.getElementById("actions11").value;
			if (x == 'csv') {
				document.querySelector('.buttons-csv').click();
			}
			if (x == 'copy') {
				document.querySelector('.buttons-copy').click();
			}
			if (x == 'pdf') {
				document.querySelector('.buttons-pdf').click();
			}

			// document.querySelector('.buttons-excel').click();
			// document.querySelector('.buttons-print').click();

		}
	</script>
	<script src="../assets/plugins/custom/datatables/datatables.bundle.js"></script>
</body>
</html>
