<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="/struts-tags" prefix="s"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.Locale"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.text.Format"%>


<html dir="ltr" lang="en-US">

<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Admin Settlement Request</title>

<link
	href="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.css"
	rel="stylesheet" type="text/css" />
<link href="../assets/plugins/custom/datatables/datatables.bundle.css"
	rel="stylesheet" type="text/css" />
<link href="../assets/plugins/global/plugins.bundle.css"
	rel="stylesheet" type="text/css" />
<link href="../assets/css/style.bundle.css" rel="stylesheet"
	type="text/css" />
<link href="../assets/css/custom/custom-style.css" rel="stylesheet"
	type="text/css" />
<!-- <script src="../js/loader/main.js"></script> -->
<script src="../assets/plugins/global/plugins.bundle.js"></script>
<script src="../assets/js/scripts.bundle.js"></script>
<script
	src="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.js"></script>
<script src="../assets/plugins/custom/datatables/datatables.bundle.js"></script>
<script src="../assets/js/widgets.bundle.js"></script>
<script src="../assets/js/custom/widgets.js"></script>
<script src="../assets/js/custom/apps/chat/chat.js"></script>
<script src="../assets/js/custom/utilities/modals/upgrade-plan.js"></script>
<script src="../assets/js/custom/utilities/modals/create-app.js"></script>
<script src="../assets/js/custom/utilities/modals/users-search.js"></script>
<script src="../js/commanValidate.js"></script>

<style type="text/css">
[class^="Acquirer"] .wwlbl .label {
	color: #666;
	font-size: 12px;
	padding: 2px 0 8px;
	display: block;
	margin: 0;
	text-align: left;
	font-weight: 600;
}

[class^="Acquirer"] .wwlbl+br {
	display: none;
}

.card-list-toggle {
	cursor: pointer;
	padding: 8px 12px;
	border: 1px solid #496cb6;
	position: relative;
	background: linear-gradient(60deg, #425185, #4a9b9b);
}

.card-list-toggle:before {
	position: absolute;
	right: 10px;
	top: 7px;
	content: "\f078";
	font-family: 'FontAwesome';
	font-size: 15px;
}

.dt-buttons {
	margin-top: 35px !important;
}

div#example_filter {
	margin-top: 35px !important;
}

.dt-button-collection a.dt-button.buttons-columnVisibility {
	background: unset;
	background-color: rgb(143, 162, 214) !important;
}

.dt-button-collection a.dt-button.buttons-columnVisibility.active {
	background-color: #2d4c5c !important;
}

.card-list-toggle.active:before {
	content: "\f077";
}

.card-list {
	display: none;
}

.acquirerRemoveBtn {
	float: left;
}

[class*="AcquirerList"]>div {
	clear: both;
	border-bottom: 1px solid #ccc;
	padding: 0 8px 5px;
	margin: 0 -8px 8px;
}

[class*="AcquirerList"]>div:last-child {
	border-bottom: none;
	padding-bottom: 0;
	margin-bottom: 0;
}

[class^="AcquirerList"] input[disabled] {
	color: #bbb;
	display: none;
}

[class^="AcquirerList"] input[disabled]+label {
	color: #bbb;
	display: none;
}

[class*="OtherList"]>div {
	clear: both;
	border-bottom: 1px solid #ccc;
	padding: 0 8px 5px;
	margin: 0 -8px 8px;
}

[class*="OtherList"]>div:last-child {
	border-bottom: none;
	padding-bottom: 0;
	margin-bottom: 0;
}

[class^="OtherList"] input[disabled] {
	color: #bbb;
	display: none;
}

[class^="OtherList"] input[disabled]+label {
	color: #bbb;
	display: none;
}

.sweet-alert .sa-icon {
	margin-bottom: 30px;
}

.sweet-alert .lead.text-muted {
	font-size: 14px;
}

.sweet-alert .btn {
	font-size: 12px;
	padding: 8px 30px;
	margin: 0 5px;
}

table.product-spec.disabled {
	cursor: not-allowed;
	opacity: 0.5;
}

table.product-spec.disabled .btn {
	pointer-events: none;
}

.merchantFilter {
	padding: 15px 0;
	width: 200px;
}

.AcquirerList input[type="radio"] {
	vertical-align: top;
	float: left;
	margin: 2px 5px 0 0;
}

.AcquirerList label {
	vertical-align: middle;
	display: block;
	font-weight: normal;
}

<
!--
.Acquirer1 input[type="radio"] {
	vertical-align: top;
	float: left;
	margin: 2px 5px 0 0;
}

.Acquirer1 label {
	vertical-align: middle;
	display: block;
	font-weight: normal;
}

-->
.boxtext td div input[type="radio"] {
	vertical-align: top;
	float: left;
	margin: 2px 5px 0 0;
}

.boxtext td div label {
	vertical-align: middle;
	display: block;
	font-weight: normal;
}

#onus_section .checkbox, #offus_section .checkbox {
	margin: 0;
}

.checkbox .wwgrp input[type="checkbox"] {
	margin-left: 0;
}

.checkbox label .wwgrp input[type="checkbox"] {
	margin-left: -20px;
}

.select2-container {
	width: 200px !important;
}

.btn:focus {
	outline: 0 !important;
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

@media ( min-width : 768px) {
	.navbar>.container .navbar-brand, .navbar>.container-fluid .navbar-brand
		{
		margin-left: 0px !important;
	}
}

.dropdown-menu>li>a:focus, .dropdown-menu>li>a:hover {
	color: #ffffff !important;
	text-decoration: none;
	background-color: #496cb6 !important;
}

input#reportrange {
	border: unset;
}
</style>
<style>
.dt-buttons {
	display: none;
}
</style>
<%
DecimalFormat d = new DecimalFormat("0.00");
Date d1 = new Date();
SimpleDateFormat df = new SimpleDateFormat("dd-MM-YYYY");
String currentDate = df.format(d1);
%>

<script type="text/javascript">
	
</script>

<style>
@media ( min-width : 992px) {
	.col-lg-3 {
		max-width: 30% !important;
	}
}
</style>

<script>
	$(document).ready(function() {
		//$("#currency").select2();
		//$("#merchant").select2();
	});
</script>
</head>

<body>

	<div class="content d-flex flex-column flex-column-fluid"
		id="kt_content">
		<div class="toolbar" id="kt_toolbar">
			<!--begin::Container-->
			<div id="kt_toolbar_container"
				class="container-fluid d-flex flex-stack">
				<!--begin::Page title-->

				<!--end::Page title-->

			</div>
			<!--end::Container-->
		</div>

		<div class="post d-flex flex-column-fluid" id="kt_post">
			<!--begin::Container-->
			<div id="kt_content_container" class="container-xxl">

				<div data-kt-swapper="true" data-kt-swapper-mode="prepend"
					data-kt-swapper-parent="{default: '#kt_content_container', 'lg': '#kt_toolbar_container'}"
					class="page-title d-flex align-items-center me-3 flex-wrap mb-5 mb-lg-0 lh-1">
					<!--begin::Title-->
					<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">Admin Approve Settlement</h1>

					<span class="h-20px border-gray-200 border-start mx-4"></span>

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
						<li class="breadcrumb-item text-muted">Admin Approve Settlement</li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item"><span
							class="bullet bg-gray-200 w-5px h-2px"></span></li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item text-dark">Admin Approve Settlement</li>
						<!--end::Item-->
					</ul>
					<!--end::Breadcrumb-->
				</div>
				<!--end::Page title-->
			<%-- 	<div class="row my-5">
					<div class="col">
						<div class="card">
							<div class="card-body">

								<div class="row g-9 mb-8">
									<!--begin::Col-->
									<div class="col">

										<div class="card-body ">
											<div class="row">

												<div class="col">
													<label
														class="d-flex align-items-center fs-6 fw-semibold mb-2">Merchant</label>
													<s:select name="merchant"
														class="form-select form-select-solid" id="merchant" listKey="emailId"
															listValue="businessName" list="merchantList" />
												</div>

												<div class="col">
													<label
														class="d-flex align-items-center fs-6 fw-semibold mb-2">Currency</label>
													<s:select headerKey="ALL" headerValue="ALL"
														list="currencyList" class="form-select form-select-solid"
														id="currency" listKey="code" listValue="name"
														name="currency" value="currency" />
												</div>

												<div class="col">
												<label
														class="d-flex align-items-center fs-6 fw-semibold mb-2">Amount</label>
													<s:textfield id="amount"
															class="form-control form-control-solid" name="amount"
															type="text" value="" autocomplete="off"
															ondrop="return false;" maxlength="50"></s:textfield>
													</div>
												<s:hidden name="token" value="%{#session.customToken}" />

												<!-- <div class="col-6 mt-7">
													<button type="button" class="btn btn-primary submitbtn"
														id="SubmitButton" onclick="submit()">Submit</button>
												</div> -->
											</div>

											<br>

											<!-- <div class="row" style="width: 50%;">
											<label class="d-flex align-items-center fs-6 fw-semibold mb-2">Available Balance</label>
												<div class="col" id="merchantBalanace">XXXX</div>
											</div>
											 -->
										</div>

									</div>
								</div>

							</div>
						</div>
					</div>
				</div> --%>
			</div>
		</div>

	</div>


	<div id="kt_content_container" class="container-xxl">
		<div class="row my-5">
			<div class="col">
				<div class="card">
					<div class="card-body">
						<!--begin::Input group-->
						<div class="row g-9 mb-8">
							<!--begin::Col-->
							<div class="col">

								<div class="row g-9 mb-8">

									<div class="table-responsive dataTables_wrapper dt-bootstrap4 no-footer">
										<table id="settlement"
											class="display table table-striped table-row-bordered gy-5 gs-7"
											style="width: 100%">
											<thead>
												<tr class="boxheadingsmall fw-bold fs-6 text-gray-800">
													<th>Merchant Name</th>
													<th>Pay ID</th>
													<th>Currency</th>
													<th>Submitted Date</th>
													<th>Updated Date</th>
													<th>Amount</th>
													<th>Status</th>
													<th>Action</th>
												</tr>
											</thead>
										</table>
									</div>

								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>


	<!-- Open Model Code Here -->
	<div id="myModal" class="modal fade" tabindex="-1">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <h5 class="modal-title">Approve Request</h5>
                    <button type="button" class="closeBtn" data-dismiss="modal">&times;</button>
                </div>
                <div class="modal-body">
					<div class="row">
						<label class="d-flex align-items-center fs-6 fw-semibold mb-2">Transaction
							ID<i style="color: red;">*</i></label> <input id="txnId" class="form-control form-control-solid"
							name="txnId" type="number" value="" autocomplete="off" />

							<input id="uid" class="form-control form-control-solid"
							name="uid" type="hidden" value="" autocomplete="off" />

							<input id="payId" class="form-control form-control-solid"
							name="payId" type="hidden" value="" autocomplete="off" />

							<input id="currency" class="form-control form-control-solid"
							name="currency" type="hidden" value="" autocomplete="off" />
					</div>
					<br>
					<!-- <div class="row">
						<label class="d-flex align-items-center fs-6 fw-semibold mb-2">Choose
							File</label> <input id="file" class="form-control form-control-solid"
							name="file" type="file" value="" autocomplete="off" />
					</div> -->

                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary closeBtn" data-dismiss="modal">Close</button>
                    <button type="button" class="btn btn-primary" onclick="updateSettlement()">Submit</button>
                </div>
            </div>
        </div>
    </div>
    <!-- Open Model Code Here -->

	<script type="text/javascript">

	$(document).ready(function(){

		renderTableAcquirer();

		$(".closeBtn").click(function(){
			$("#myModal").modal('hide');
		});

	/* 		var merchant = $('#merchant').val();
			$.post("merchantFundAction", {
				merchant : merchant
			}, function(result) {
				if (result != null) {
					$("#merchantBalanace").text(result.balance);
				}
			});

		}); */

/* 		$('#merchant').on('change', function() {
			var merchant = $('#merchant').val();
			$.post("merchantFundAction", {
				merchant : merchant
			}, function(result) {
				if (result != null) {
					$("#merchantBalanace").text(result.balance);
				}
			});*/

		});

		/* function submit() {
			 var amount = $("#amount").val();

			if (amount == "") {
				alert("Please Enter Amount");
				return false;
			}

			renderTableAcquirer();

		} */

		function renderTableAcquirer() {

			$("#settlement").DataTable().destroy();

			var token = document.getElementsByName("token")[0].value;
			var buttonCommon = {
				exportOptions : {
					format : {
						body : function(data, column, row, node) {
							return data;
						}
					}
				}
			};

			$('#settlement')
					.DataTable(
							{
								dom : 'BTftlpi',
								scrollY : true,
								scrollX : true,

								"ajax" : {
									"url" : "settlementAdminRequestDetails",
									"type" : "POST",
									"data" : function(d) {
										return generateSettlementData(d);
									}
								},

								"searching" : false,
								"ordering" : true,
								"processing" : true,
								"serverSide" : false,
								"paginationType" : "full_numbers",
								"lengthMenu" : [ [ 10, 25, 50, 100 ],
										[ 10, 25, 50, 100 ] ],
								"order" : [ [ 4, "desc" ] ],

								"aoColumns" : [
										{
											"mData" : "merchantName"
										},
										{
											"mData" : "payId"
										},
										{
											"mData" : "currency"
										},
										{
											"mData" : "submitDate"
										},
										{
											"mData" : "settlementDate"
										},
										{
											"mData" : "amount"
										},
										{
											"mData" : "status"
										},
										{
											mData : null,
											render : function(data, type, row) {
												return '<button class="approve-button btn btn-primary" data-row-id="' + row.payId + '" data-row-uid="' + row.uid + '">Approve</button>&nbsp;&nbsp;'
														+ '<button class="reject-button btn btn-danger" data-row-id="' + row.payId + '" data-row-uid="' + row.uid + '">Reject</button>';
											}
										},

								]


							});



		$('#settlement').on('click', '.approve-button', function() {
			var rowData = $("#settlement").DataTable().row($(this).closest('tr')).data();
			$("#payId").val(rowData.payId);
			$("#currency").val(rowData.currency);
			$("#uid").val(rowData.uid);
			$("#myModal").modal('show');
			console.log(rowData.uid);

		});

		$('#settlement').on('click', '.reject-button', function() {
				var rowId = $(this).data('row-id');
				var rowData = $("#settlement").DataTable().row($(this).closest('tr')).data();
				$("#payId").val(rowData.payId);
				$("#currency").val(rowData.currency);
				$("#uid").val(rowData.uid);
				console.log(rowData.uid);
				rejectSettlement();
		});

		}

		function generateSettlementData(d) {
			//var merchant = $("#merchant").val();
			//var currency = $("#currency").val();

			var token = document.getElementsByName("token")[0].value;

			var obj = {
				//merchant : merchant,
				//currency : currency,
				//amount : amount,
				token : token,
				"struts.token.name" : "token",
			}
			return obj;
		}
	</script>

	<script type="text/javascript">
		function updateSettlement() {
		var txnId=$("#txnId").val();

		if (txnId == "") {
				alert("Please enter transaction ID");
				return false;
		}

		if (txnId.length < 6) {
				alert("Transaction ID should be greater than 6");
				return false;
		}

		if (txnId.length > 30 ) {
				alert("Transaction ID should be less than 30");
				return false;
		}

			var jform = new FormData();
			jform.append('txnId', $('#txnId').val());
			jform.append('uid', $('#uid').val());
			jform.append('payId', $('#payId').val());
			jform.append('currency', $('#currency').val());
			// jform.append('file', $('#file').get(0).files[0]);

			console.log(jform);
			for (var pair of jform.entries()) {
    		console.log(pair[0]+ ', ' + pair[1]);
			}

			$.ajax({
				url : 'settlementAdminApproveDetail',
				type : 'POST',
				data : jform,
				dataType : 'json',
				// mimeType : 'multipart/form-data',
				contentType : false,
				cache : false,
				processData : false,
				success : function(data, status, jqXHR) {
					$("#txnId").val("");
					if (data != null) {
						alert(data.fileUploadMsg);
						location.reload();
					}
					// renderTableAcquirer();

					$("#myModal").modal('hide');
				},
				error : function(jqXHR, status, error) {
					alert("There is some problem");
				}
			});

		}
	</script>

	<script type="text/javascript">

	function rejectSettlement() {
		var payId = $('#payId').val();
		var currency = $('#currency').val();
		var uid = $('#uid').val();
		$.post("settlementAdminRejectDetail", {
			payId : payId,
			currency:currency,
			uid : uid,
		}, function(result) {
			if (result != null) {
				alert(result.fileUploadMsg);
				location.reload();
			}
			// renderTableAcquirer();
			// location.reload();

			$("#myModal").modal('hide');
		});

	}
	</script>

</body>

</html>