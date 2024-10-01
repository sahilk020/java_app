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
<title>Acquirer Fund Approval</title>

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
		$("#acquirer").select2();
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
					<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">Acquirer Fund Approval</h1>

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
						<li class="breadcrumb-item text-muted">Wallet</li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item"><span
							class="bullet bg-gray-200 w-5px h-2px"></span></li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item text-dark">Acquirer Fund Approval</li>
						<!--end::Item-->
					</ul>
					<!--end::Breadcrumb-->
				</div>
				<!--end::Page title-->



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
										<table id="rebalanceTable"
											class="display table table-striped table-row-bordered gy-5 gs-7"
											style="width: 100%">
											<thead>
												<tr class="boxheadingsmall fw-bold fs-6 text-gray-800">
													<th>Acquirer PayId</th>
													<th>Acquirer Name</th>
													<th>Amount</th>
													<th>Order Id</th>
													<th>Transaction Type</th>
													<th>Narration</th>
													<th>Status</th>
													<th>Created By</th>
													<th>Created Date</th>
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

	<script type="text/javascript">

		$(document).ready(function() {

		// DataTable for Rebalance Acquirer
		renderTableAcquirer();

		});


		function renderTableAcquirer() {
		    $("#rebalanceTable").DataTable().destroy();
			var urls = new URL(window.location.href);
			var domain = urls.origin;

	$('#rebalanceTable').DataTable( { dom : 'BTftlpi',
		scrollY : true,
		scrollX : true,

		"ajax" : {
			"url" : domain+"/crmws/acquirerRebalace/action/transactionList/PENDING",
			"type" : "GET",

		},

		"searching" : false,
		"ordering" : false,
		"processing" : true,
		"serverSide" : false,
		"paginationType" : "full_numbers",
		"lengthMenu" : [ [ 10, 25, 50, 100 ],
				[ 10, 25, 50, 100 ] ],
		"order" : [ [ 8, "desc" ] ],

		"aoColumns" : [{
			"mData" : "toPayId"
		}, {
			"mData" : "toAcquirerName"
		}, {
			"mData" : "amount"
		},{
			"mData" : "orderId"
		}, {
			"mData" : "txnType"
		},{
			"mData" : "narration"
		}, {
			"mData" : "status"
		}, {
			"mData" : "createdBy"
		}, {
			"mData" : "createdDate"
		}, {
			mData : null,
			render : function(data, type, row) {
												return '<button class="approve-button btn btn-primary" style="width:108px" data-row-id="' + row.orderId + '" data-row-uid="' + row.amount + '" data-row-acquirerName="' + row.acquirerName + '">Approve</button>&nbsp;&nbsp;'
														+ '<button class="reject-button btn btn-danger"  style="width:108px" data-row-id="' + row.orderId + '" data-row-uid="' + row.amount + '" data-row-acquirerName="' + row.acquirerName + '">Reject</button>';
											}
										},

		]

	});

	$('#rebalanceTable').on('click', '.approve-button', function() {
				var rowData = $("#rebalanceTable").DataTable().row($(this).closest('tr')).data();
				var amountUi = rowData.amount;
				var orderIdUi = rowData.orderId;
				var acquireNameUi = rowData.toAcquirerName;
				console.log(rowData);
				console.log(amountUi);
				console.log(orderIdUi);
				console.log(acquireNameUi);
			// console.log(rowData.toCurrency);
			approveFundAdd(amountUi,orderIdUi,acquireNameUi);

		});

		$('#rebalanceTable').on('click', '.reject-button', function() {
				var rowData = $("#rebalanceTable").DataTable().row($(this).closest('tr')).data();
				var amountUi = rowData.amount;
				var orderIdUi = rowData.orderId;
				var acquireNameUi = rowData.toAcquirerName;
				console.log(rowData);
				console.log(amountUi);
				console.log(orderIdUi);
				console.log(acquireNameUi);
				// console.log(rowData.toCurrency);
				rejectFundAdd(amountUi,orderIdUi,acquireNameUi);

		});


}


 function approveFundAdd(amountUi,orderIdUi,acquireNameUi){

	var urls = new URL(window.location.href);
			var domain = urls.origin;

	payload ={

		"amount": amountUi,
    	"acquirerName": acquireNameUi,
    	"orderId": orderIdUi
	}

	$.ajax({
				url : domain+"/crmws/acquirerRebalace/action/fund/APPROVED",
				type : 'POST',
				data: JSON.stringify(payload),
                contentType: "application/json",
				success : function(data) {
						console.log(data);
						alert(data.message);
						//renderTableAcquirer();
						window.location.reload();
				},

			});
 }

 function rejectFundAdd(amountUi,orderIdUi,acquireNameUi){

	var urls = new URL(window.location.href);
			var domain = urls.origin;

payload ={

	"amount": amountUi,
	"acquirerName": acquireNameUi,
	"orderId": orderIdUi
}

$.ajax({
			url : domain+"/crmws/acquirerRebalace/action/fund/REJECTED",
			type : 'POST',
			data: JSON.stringify(payload),
                contentType: "application/json",
			success : function(data) {
					console.log(data);
					alert(data.message);
					//renderTableAcquirer();
					window.location.reload();
			},

		});
}


</script>

</body>

</html>