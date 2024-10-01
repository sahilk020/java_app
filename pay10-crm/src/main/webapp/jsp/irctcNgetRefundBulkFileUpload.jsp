<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="/struts-tags" prefix="s"%>
<html dir="ltr" lang="en-US">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>IRCTC Nget Refund File Upload</title>




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

		<script type="text/javascript" src="../js/sweetalert.js"></script>
				<link rel="stylesheet" href="../css/sweetalert.css">
		






<%-- 
<link href="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.css" rel="stylesheet" type="text/css" />
<link href="../assets/plugins/custom/datatables/datatables.bundle.css" rel="stylesheet" type="text/css" />
<link href="../assets/plugins/global/plugins.bundle.css" rel="stylesheet" type="text/css" />
<link href="../assets/css/style.bundle.css" rel="stylesheet" type="text/css" />
<link href="../assets/css/custom/custom-style.css" rel="stylesheet" type="text/css" />

<!-- 
<link href="../css/Jquerydatatableview.css" rel="stylesheet" />
<link href="../css/Jquerydatatable.css" rel="stylesheet" />
<link href="../css/default.css" rel="stylesheet" type="text/css" />
<link href="../css/jquery-ui.css" rel="stylesheet" /> -->

<script type="text/javascript" src="../js/jquery.min.js"></script>
<script src="../js/jquery.js"></script>
<script src="../js/jquery.dataTables.js"></script>
<script src="../js/jquery-ui.js"></script>
<script type="text/javascript" src="../js/moment.js"></script>
<script type="text/javascript" src="../js/daterangepicker.js"></script>

<script src="../js/jquery.popupoverlay.js"></script>
<script type="text/javascript" src="../js/dataTables.buttons.js"></script>
<script type="text/javascript" src="../js/pdfmake.js"></script>
<script src="../js/core/popper.min.js"></script>
<script src="../js/core/bootstrap-material-design.min.js"></script>
<script src="../js/plugins/perfect-scrollbar.jquery.min.js"></script>
<!-- Control Center for Material Dashboard: parallax effects, scripts for the example pages etc -->
<script src="../js/material-dashboard.js?v=2.1.0" type="text/javascript"></script>
<script src="../assets/js/scripts.bundle.js"></script> --%>
<style>


.dt-buttons.btn-group.flex-wrap {
    display: none;
}
table.dataTable.display tbody tr.odd {
    background-color: #e6e6ff !important;
}
table.dataTable.display tbody tr.odd > .sorting_1{
	 background-color: #e6e6ff !important;
}
table.display td.center{
	text-align: left !important;
}
.btn:focus{
		outline: 0 !important;
	}
</style>
<script type="text/javascript">
function decodeVal(text){	
	  return $('<div/>').html(text).text();
	}
	
$(document).ready(function () {

	$(function () {
		$("#dateFrom").flatpickr({
			maxDate: new Date(),
			dateFormat: "Y-m-d",
			defaultDate: "today",
			defaultDate: "today",
		});
		$("#dateTo").flatpickr({
			maxDate: new Date(),
			dateFormat: "d-m-Y",
			defaultDate: "today",
			maxDate: new Date()
		});
		$("#kt_datatable_vertical_scroll").DataTable({
			scrollY: true,
			scrollX: true

		});
	});
});
// $(document).ready(
		
// 		function() {
							
// 			populateDataTable();
// 			enableBaseOnAccess();
// 			$("#submit").click(
// 					function(env) {
// 						/* var table = $('#authorizeDataTable')
// 								.DataTable(); */
// 						$('#searchAgentDataTable').empty();
						

// 						populateDataTable();

// 					});
// 		});	
					
function populateDataTable() {	
	
	var token  = document.getElementsByName("token")[0].value;
	var dateFrom=  document.getElementById("dateFrom").value
	var FileName=document.getElementById("FileName").value
	var refundType=document.getElementById("refundType").value
	var canorderId=document.getElementById("canorderId").value
	var orderId=document.getElementById("orderId").value
	var pgRefNum=document.getElementById("pgRefNum").value

	if(FileName||refundType||canorderId||orderId||pgRefNum){
		dateFrom="";
	}
	
	   var urls = new URL(window.location.href);
    var domain = urls.origin;
	$('#searchAgentDataTable')
			.DataTable(
					{
						dom : 'BTftlpi',
						buttons : [ {
							extend : 'copyHtml5',
							exportOptions : {
								columns : [':visible :not(:last-child)']
							}
						}, {
							extend : 'csvHtml5',
							title : 'Acquirer List',
							exportOptions : {
								columns : [':visible :not(:last-child)']
							}
						}, {
							extend : 'pdfHtml5',
							title : 'Acquirer List',
							orientation : 'landscape',
							exportOptions : {
								columns : [':visible :not(:last-child)']
							},
							customize: function (doc) {
							    doc.defaultStyle.alignment = 'center';
		     					doc.styles.tableHeader.alignment = 'center';
							  }
						}, {
							extend : 'print',
							title : 'Acquirer List',
							orientation : 'landscape',
							exportOptions : {
								columns : [':visible :not(:last-child)']
							}
						},{
							extend : 'colvis',
							//           collectionLayout: 'fixed two-column',
							columns : [ 0, 1, 2, 3,4,5,6,7,8,9]
						}],
						"ajax" : {
						"url" : domain+"/crmws/irctc/nget/refund/search?dateFrom="+dateFrom+"&FileName="+FileName+"&pgRefNum="+pgRefNum+"&orderId="+orderId+"&canorderId="+canorderId+"&refundType="+refundType,
						
						"type" : "POST",
							"data" : {

									token:token,
								    "struts.token.name": "token",
									}
						},
						"bProcessing" : true,
						"bLengthChange" : true,
						"bDestroy" : true,
						"iDisplayLength" : 10,
						"order" : [ [ 1, "desc" ] ],
						"aoColumns" : [										
										{
											"mData" : "orderId",
											"sWidth" : '25%',
										},
										{
											"mData" : "bankRefNum",
											"sWidth" : '22%'
										},
										{
											"mData" : "amount",
											"sWidth" : '22%'
										},
										{
											"mData" : "pgRefNO",
											"sWidth" : '22%'
										},
                                         {
											"mData" : "fileName",
											"sWidth" : '22%'
										}, 
										{
											"mData" : "RefundOrderId",
											"sWidth" : '22%'
										},{
											"mData" : "CreateBy",
											"sWidth" : '22%'
										},
										{
											"mData" : "status",
											"sWidth" : '22%'
										},{
											"mData" : "ResponseMessage",
											"sWidth" : '22%'
										},
																			{
											"mData" : "RefundOrderId",
											"sWidth" : '25%',
											"visible" : false,
										}]
					});

// 	 $(function() {

// 		var table = $('#searchAgentDataTable').DataTable();
// 		$('#searchAgentDataTable tbody')
// 				.on(
// 						'click',
// 						'td',
// 						function() {

// 							   var urls = new URL(window.location.href);
// 							    var domain = urls.origin;
// 							debugger
// 							var columnVisible = table.cell(this).index().columnVisible;
// 							var rowIndex = table.cell(this).index().row;
// 							var row = table.row(rowIndex).data();

// 							 var updateby =document.getElementById("email").value

// 							var id = table.cell(rowIndex, 6).data();
							 
							   
// 								swal({
// 									title: "Are you sure want to Delete this Rule?",
// 									type: "warning",
// 									showCancelButton: true,
// 									confirmButtonColor: "#DD6B55",
// 									confirmButtonText: "Yes, Delete it!",
// 									closeOnConfirm: false
// 									}, function (isConfirm) {
// 										if (!isConfirm) return;		
// 							    $.ajax({
// 									type : "POST",
// 									url : domain+"/crmws/AcquirerSwitch/deleteAcquirerDownTime",
// 									timeout : 0,
// // 									data : {
// // 										"updateBy":updateby,
// // 										"id":id
										
										
// // 									},
// 									success : function(data) {
// 										var response = data.response;
// 										swal({
// 										 title: 'Deleted Successful!',
// 										 type: "success"
// 										}, function(){
// 											window.location.reload();
// 										}); 
// 									},
// 									error : function(data) {
// 										window.location.reload();
// 									}
// 							    });
							
// 							})
	

													
// 						});
// 	});
}
</script>
<script type="text/javascript">
	function MM_openBrWindow(theURL, winName, features) { //v2.0
		window.open(theURL, winName, features);
	}

	function displayPopup() {
		document.getElementById('light3').style.display = 'block';
		document.getElementById('fade3').style.display = 'block';
	}
</script>

</head>
<body>

<div class="content d-flex flex-column flex-column-fluid"
					id="kt_content">

<div class="toolbar" id="kt_toolbar">
							<!--begin::Container-->
							<div id="kt_toolbar_container" class="container-fluid d-flex flex-stack">
								<!--begin::Page title-->
								<div data-kt-swapper="true" data-kt-swapper-mode="prepend" data-kt-swapper-parent="{default: '#kt_content_container', 'lg': '#kt_toolbar_container'}" class="page-title d-flex align-items-center me-3 flex-wrap mb-5 mb-lg-0 lh-1">
									<!--begin::Title-->
									<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">Upload
												IRCTC Nget Bulk Refund File</h1>
									<!--end::Title-->
									<!--begin::Separator-->
									<span class="h-20px border-gray-200 border-start mx-4"></span>
									<!--end::Separator-->
									<!--begin::Breadcrumb-->
									<ul class="breadcrumb breadcrumb-separatorless fw-semibold fs-7 my-1">
										<!--begin::Item-->
										<li class="breadcrumb-item text-muted">
											<a href="home" class="text-muted text-hover-primary">Dashboard</a>
										</li>
										<!--end::Item-->
										<!--begin::Item-->
										<li class="breadcrumb-item">
											<span class="bullet bg-gray-200 w-5px h-2px"></span>
										</li>
										<!--end::Item-->
                                        <!--begin::Item-->
										<li class="breadcrumb-item text-muted">IRCTC Refund</li>
										<!--end::Item-->
										<!--begin::Item-->
										<li class="breadcrumb-item">
											<span class="bullet bg-gray-200 w-5px h-2px"></span>
										</li>
										<!--end::Item-->
										<!--begin::Item-->
										<li class="breadcrumb-item text-dark">Upload IRCTC Nget Bulk
													Refund File</li>
										<!--end::Item-->
									</ul>
									<!--end::Breadcrumb-->
								</div>
								<!--end::Page title-->
								
							</div>
							<!--end::Container-->
						</div>
						<div class="row my-5">
       <div class="col">
								<div class="card ">
						<div class="card-body ">
				<div class="container">
					<div class="row">
						
						<s:form action="refundBulkFileUpload" id="form">
									<s:hidden name="id" id="id" />
									<input type="hidden" name="email" id="email"
										value="<s:property value='%{#session.USER.emailId}'/>" />

									<input type="file" name="file" id="file"
										accept="text/plain">
									<button type="button" class="btn btn-primary btn-xs"
										style="margin-top: 20px;" name="submitFile" id="submitFile"
										onClick="saveFile()">IRCTC Bulk Refund Upload</button>
									<span id="excelError" class="error"></span>
								</s:form>
								</div>
						</div></div></div></div></div>
						<div class="row my-5">
       <div class="col">	
		<div class="card ">
			<div class="card-body ">
				<div class="container">
					<div class="row">
						<s:form id="resellerPayoutForm">
						<input type="hidden" name="email" id="email" value="<s:property value='%{#session.USER.emailId}'/>" />		
						<div class="row g-9 mb-8">
												<!--begin::Col-->
												<div class="col-md-4 fv-row fv-plugins-icon-container">
													<label class="d-flex align-items-center fs-6 fw-semibold mb-2">
														<span class="">PG Ref Number</span>
													</label>
													<!--end::Label-->
													<!-- <input type="text" class="form-control form-control-solid" name="pgrefnumber"> -->

													<!-- <s:textfield id="pgRefNum" class="form-control form-control-solid"
													name="pgRefNum" type="text" value="" autocomplete="off"
													maxlength="16" onblur="validPgRefNum();" ondrop="return false;" onKeyDown="if(event.keyCode === 32)return false;" onpaste="removeSpaces(this);"></s:textfield> -->
													<s:textfield type="number" min="0"
														class="form-control form-control-solid" id="pgRefNum"
														name="pgRefNum" maxlength="17"
														onkeydown="inputKeydownevent(event,'pgRefNum')"
														oninput="if(value.length>16)value=value.slice(0,16)" />

												</div>
												<div class="col-md-4 fv-row">
													<label class="d-flex align-items-center fs-6 fw-semibold mb-2">
														<span class="">Order ID</span>
													</label>
													<!--end::Label-->
													<!-- <input type="text" class="form-control form-control-solid" name="orderid"> -->
													<!-- <s:textfield id="orderId" class="form-control form-control-solid" name="orderId"
										type="text" value="" autocomplete="off"
										onKeyDown="validateOrderIdvalue(this);" onkeypress="return validateOrderId(event);"  ondrop="return false;" onpaste="validateOrderIdvalue(this);" maxlength="50"></s:textfield> -->
													<s:textfield type="text" class="form-control form-control-solid"
														id="orderId" name="orderId"
														onkeydown="inputKeydownevent(event,'orderId')"
														oninput="if(value.length>30)value=value.slice(0,30)" />

												</div>

<div class="col-md-4 fv-row">
													<label class="d-flex align-items-center fs-6 fw-semibold mb-2">
														<span class="">CancellationID</span>
													</label>
													<!--end::Label-->
													<!-- <input type="text" class="form-control form-control-solid" name="orderid"> -->
													<!-- <s:textfield id="orderId" class="form-control form-control-solid" name="orderId"
										type="text" value="" autocomplete="off"
										onKeyDown="validateOrderIdvalue(this);" onkeypress="return validateOrderId(event);"  ondrop="return false;" onpaste="validateOrderIdvalue(this);" maxlength="50"></s:textfield> -->
													<s:textfield type="text" class="form-control form-control-solid"
														id="canorderId" name="canorderId"
														onkeydown="inputKeydownevent(event,'orderId')"
														oninput="if(value.length>30)value=value.slice(0,30)" />

												</div>
												
												
												<div class="col-md-4 fv-row" >
								<label	class="d-flex align-items-center fs-6 fw-bold mb-2">
									<span class="">RefundType :</span>
										</label>
											<!--end::Label-->
											<s:select headerKey="" 
																headerValue="Select Refund Type"
																class="form-select form-select-solid"
																list="#{'R':'Rejected','C':'Cancelled'}"
																name="cardHolderType" id="refundType" />

										</div>

<div class="col-md-4 fv-row">
													<label class="d-flex align-items-center fs-6 fw-semibold mb-2">
														<span class="">FileName</span>
													</label>
													<!--end::Label-->
													<!-- <input type="text" class="form-control form-control-solid" name="orderid"> -->
													<!-- <s:textfield id="orderId" class="form-control form-control-solid" name="orderId"
										type="text" value="" autocomplete="off"
										onKeyDown="validateOrderIdvalue(this);" onkeypress="return validateOrderId(event);"  ondrop="return false;" onpaste="validateOrderIdvalue(this);" maxlength="50"></s:textfield> -->
													<s:textfield type="text" class="form-control form-control-solid"
														id="FileName" name="FileName"
														onkeydown="inputKeydownevent(event,'orderId')"
														oninput="if(value.length>30)value=value.slice(0,30)" />

												</div>
												
												<div class="col-md-4 fv-row">
													<label class="d-flex align-items-center fs-6 fw-semibold mb-2">
														<span class="">TransactionDate</span>
													</label>
													<!--end::Label-->
													<div class="position-relative d-flex align-items-center">
														<!--begin::Icon-->
														<!--begin::Svg Icon | path: icons/duotune/general/gen014.svg-->
														<span class="svg-icon svg-icon-2 position-absolute mx-4">
															<svg width="24" height="24" viewBox="0 0 24 24" fill="none"
																xmlns="http://www.w3.org/2000/svg">
																<path opacity="0.3"
																	d="M21 22H3C2.4 22 2 21.6 2 21V5C2 4.4 2.4 4 3 4H21C21.6 4 22 4.4 22 5V21C22 21.6 21.6 22 21 22Z"
																	fill="currentColor"></path>
																<path
																	d="M6 6C5.4 6 5 5.6 5 5V3C5 2.4 5.4 2 6 2C6.6 2 7 2.4 7 3V5C7 5.6 6.6 6 6 6ZM11 5V3C11 2.4 10.6 2 10 2C9.4 2 9 2.4 9 3V5C9 5.6 9.4 6 10 6C10.6 6 11 5.6 11 5ZM15 5V3C15 2.4 14.6 2 14 2C13.4 2 13 2.4 13 3V5C13 5.6 13.4 6 14 6C14.6 6 15 5.6 15 5ZM19 5V3C19 2.4 18.6 2 18 2C17.4 2 17 2.4 17 3V5C17 5.6 17.4 6 18 6C18.6 6 19 5.6 19 5Z"
																	fill="currentColor"></path>
																<path
																	d="M8.8 13.1C9.2 13.1 9.5 13 9.7 12.8C9.9 12.6 10.1 12.3 10.1 11.9C10.1 11.6 10 11.3 9.8 11.1C9.6 10.9 9.3 10.8 9 10.8C8.8 10.8 8.59999 10.8 8.39999 10.9C8.19999 11 8.1 11.1 8 11.2C7.9 11.3 7.8 11.4 7.7 11.6C7.6 11.8 7.5 11.9 7.5 12.1C7.5 12.2 7.4 12.2 7.3 12.3C7.2 12.4 7.09999 12.4 6.89999 12.4C6.69999 12.4 6.6 12.3 6.5 12.2C6.4 12.1 6.3 11.9 6.3 11.7C6.3 11.5 6.4 11.3 6.5 11.1C6.6 10.9 6.8 10.7 7 10.5C7.2 10.3 7.49999 10.1 7.89999 10C8.29999 9.90003 8.60001 9.80003 9.10001 9.80003C9.50001 9.80003 9.80001 9.90003 10.1 10C10.4 10.1 10.7 10.3 10.9 10.4C11.1 10.5 11.3 10.8 11.4 11.1C11.5 11.4 11.6 11.6 11.6 11.9C11.6 12.3 11.5 12.6 11.3 12.9C11.1 13.2 10.9 13.5 10.6 13.7C10.9 13.9 11.2 14.1 11.4 14.3C11.6 14.5 11.8 14.7 11.9 15C12 15.3 12.1 15.5 12.1 15.8C12.1 16.2 12 16.5 11.9 16.8C11.8 17.1 11.5 17.4 11.3 17.7C11.1 18 10.7 18.2 10.3 18.3C9.9 18.4 9.5 18.5 9 18.5C8.5 18.5 8.1 18.4 7.7 18.2C7.3 18 7 17.8 6.8 17.6C6.6 17.4 6.4 17.1 6.3 16.8C6.2 16.5 6.10001 16.3 6.10001 16.1C6.10001 15.9 6.2 15.7 6.3 15.6C6.4 15.5 6.6 15.4 6.8 15.4C6.9 15.4 7.00001 15.4 7.10001 15.5C7.20001 15.6 7.3 15.6 7.3 15.7C7.5 16.2 7.7 16.6 8 16.9C8.3 17.2 8.6 17.3 9 17.3C9.2 17.3 9.5 17.2 9.7 17.1C9.9 17 10.1 16.8 10.3 16.6C10.5 16.4 10.5 16.1 10.5 15.8C10.5 15.3 10.4 15 10.1 14.7C9.80001 14.4 9.50001 14.3 9.10001 14.3C9.00001 14.3 8.9 14.3 8.7 14.3C8.5 14.3 8.39999 14.3 8.39999 14.3C8.19999 14.3 7.99999 14.2 7.89999 14.1C7.79999 14 7.7 13.8 7.7 13.7C7.7 13.5 7.79999 13.4 7.89999 13.2C7.99999 13 8.2 13 8.5 13H8.8V13.1ZM15.3 17.5V12.2C14.3 13 13.6 13.3 13.3 13.3C13.1 13.3 13 13.2 12.9 13.1C12.8 13 12.7 12.8 12.7 12.6C12.7 12.4 12.8 12.3 12.9 12.2C13 12.1 13.2 12 13.6 11.8C14.1 11.6 14.5 11.3 14.7 11.1C14.9 10.9 15.2 10.6 15.5 10.3C15.8 10 15.9 9.80003 15.9 9.70003C15.9 9.60003 16.1 9.60004 16.3 9.60004C16.5 9.60004 16.7 9.70003 16.8 9.80003C16.9 9.90003 17 10.2 17 10.5V17.2C17 18 16.7 18.4 16.2 18.4C16 18.4 15.8 18.3 15.6 18.2C15.4 18.1 15.3 17.8 15.3 17.5Z"
																	fill="currentColor"></path>
															</svg>
														</span>
														<!--end::Svg Icon-->
														<!--end::Icon-->
														<!--begin::Datepicker-->
														<input
															class="form-control form-control-solid ps-12 flatpickr-input"
															placeholder="Select a date" name="dateTo" id="dateFrom"
															type="text" readonly="readonly">
														<!--end::Datepicker-->
													</div>
												</div>
										
										<div class="col-md-6 fv-row" style="padding-left: 45px;margin: 21px;">
									<button type="button" class="btn btn-primary"
							id="btnPayoutConf1" onClick="populateDataTable()">Search</button>

										</div>
									
						</s:form>
					</div>
				</div>
			</div>
		</div>
		</div>
		</div>

<div class="post d-flex flex-column-fluid" id="kt_post">

			<div id="kt_content_container" class="container-xxl">
				
				<div class="row my-5">
					<div class="col">
						<div class="card">
							<div class="card-body">
	<table width="100%" align="left" cellpadding="0" cellspacing="0"
		class="txnf">
		<tr>
			<td align="left"><s:actionmessage /></td>
		</tr>
		<!-- <tr>
			<td align="left"><h2>Acquirer List</h2></td>
		</tr> -->
		<tr>
		
		
		
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
														<option value="print">PRINT</option>
													</select>
												</div>
												<div class="col-lg-4 col-sm-12 col-md-6">
													<div class="dropdown1">
														<button
															class="form-select form-select-solid actions dropbtn1">Customize
															Columns</button>
														<div class="dropdown-content1">
														
															<a class="toggle-vis" data-column="0">Order Id</a>
															 <a	class="toggle-vis" data-column="1">BankRefNum</a>
															  <a class="toggle-vis" data-column="2">Amount</a> 
															  <a class="toggle-vis" data-column="3">PgRefNum</a> 
															  <a class="toggle-vis" data-column="4">FileName</a>  
<a class="toggle-vis" data-column="5">RefundOrderId</a> 
															  <a class="toggle-vis" data-column="6">CreateBy</a>  
															  															  <a class="toggle-vis" data-column="7">Status</a>  
															  															  <a class="toggle-vis" data-column="6">ResponseMessage</a>  
															  

														</div>
													</div>
												</div>
											</div>
		
		
		
			<td align="left"><table width="100%" border="0" align="center"
					cellpadding="0" cellspacing="0">
					<tr>
						<td colspan="5" align="left" valign="top">&nbsp;</td>
					</tr>
					<tr>
						<td colspan="5" align="center" valign="top"><table
								width="100%" border="0" cellpadding="0" cellspacing="0">
							</table></td>
					</tr>
				</table></td>
		</tr>
		<tr>
			<td align="left" style="padding: 10px;">
				<div class="scrollD">
					<table width="100%" border="0" cellpadding="0" cellspacing="0"
						id="searchAgentDataTable"  class="table table-striped table-row-bordered gy-5 gs-7">
						<!-- class="display" -->
						<thead>
							<!-- <tr class="boxheadingsmall"> -->
							<tr class="fw-bold fs-6 text-gray-800">
								
	<th>Order Id</th>
								<th>BankRefNum </th>
									<th>Amount</th>
						        <th>PgRefNum</th>
								<th>FileName</th>
																<th>RefundOrderId</th>
																<th>CreateBy</th>
																								<th>Status</th>
																								<th>Response Message</th>
								
								<th></th>
							</tr>
						</thead>
					</table>
				</div>
			</td>
		</tr>
	</table>
	</div>
	</div>
	</div>
	</div>
	</div>
	</div>
	
	<s:form   class="d-flex align-items-center fs-6 fw-semibold mb-2" name="agentDetails" action="editAcquirerAction">
		<s:hidden name="emailId" id="emailAddress" value="" />
		<s:hidden name="firstName" id="firstName" value="" />
		<s:hidden name="lastName" id="lastName" value="" />
		<s:hidden name="businessName" id="businessName" value="" />
        <s:hidden name="accountNo" id="accountNo" value="" />
		<s:hidden name="token" value="%{#session.customToken}"></s:hidden>
	</s:form>
	</div>
<script type="text/javascript">
$('#searchAgentDataTable').on( 'draw.dt', function () {
	enableBaseOnAccess();
} );
function enableBaseOnAccess() {
	setTimeout(function(){
		if ($('#searchAcquirer').hasClass("active")) {
			var menuAccess = document.getElementById("menuAccessByROLE").value;
			var accessMap = JSON.parse(menuAccess);
			var access = accessMap["searchAcquirer"];
			if (access.includes("Update")) {
				var edits = document.getElementsByName("acqirerEdit");
				for (var i = 0; i < edits.length; i++) {
					var edit = edits[i];
					edit.disabled=false;
				}
			}
		}
	},500);
}
</script>
<script type="text/javascript">

function saveFile(){
	 var urls = new URL(window.location.href);
	 var domain = urls.origin;
   var file= $('#file').val();
   if (!(/\.(txt)$/i).test(file)) {
       alert('Please upload valid text file .txt only.');
       $(file).val('');
   }
   else{
     
   var form = $('#form')[0];
   var data = new FormData(form);

   $.ajax({
      url: domain+"/crmws/irctc/nget/refund/upload/bulk",
      type: 'POST',
      enctype: 'multipart/form-data',
      data: data,
        processData: false,
          contentType: false,
          cache: false,
          success: function (data) {
              alert(data);
              window.location.reload();
          },
          error: function(data, textStatus, jqXHR) {
        	  
  			if(data.responseText && JSON.parse(data.responseText).respmessage){
  				var responseText =JSON.parse(data.responseText) 
  				alert(responseText.respmessage);
  			}else{
  				alert("Error while uploading IRCTC Refund Bulk file");
  			}
  			window.location.reload();
  			
  		}
    });  
   }
 }
</script>
<script type="text/javascript">

function save(){
	debugger
	var acquire=  document.getElementById("acquirer3").value
	var payment=document.getElementById("paymentMethods").value
	if(!(acquire=="")){
		populateDataTable()
	}

}
						
						
			$('a.toggle-vis').on('click', function(e) {
				/* debugger */
				e.preventDefault();
				table = $('#searchAgentDataTable').DataTable();
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