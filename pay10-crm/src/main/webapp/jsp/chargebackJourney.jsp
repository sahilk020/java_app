<%@page import="com.pay10.crm.actionBeans.TransactionStatusBean"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="/struts-tags" prefix="s"%>
<html dir="ltr" lang="en-US">
<head>
<title>CB Journey Report</title>
<link rel="shortcut icon" href="assets/media/images/paylogo.svg" />
<link rel="stylesheet"
	href="https://fonts.googleapis.com/css?family=Inter:300,400,500,600,700" />
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
<script src="../assets/plugins/global/plugins.bundle.js"></script>
<script src="../assets/js/scripts.bundle.js"></script>
<script src="../assets/plugins/custom/datatables/datatables.bundle.js"></script>



<link href="../css/select2.min.css" rel="stylesheet" />
<script src="../js/jquery.select2.js" type="text/javascript"></script>

<style type="text/css">
.svg-icon {
	margin-top: 1vh !important;
}

.dt-buttons.btn-group.flex-wrap {
	display: none;
}

.grab {
	cursor: -webkit-grab;
	cursor: grab;
	color: blue;
}
</style>
<script type="text/javascript">
	$(document).ready(function() {
		$(".adminMerchants").select2();
	});
</script>
<script type="text/javascript">
	function submitForm() {

		debugger

		var transFrom = new Date(Date
				.parse(document.getElementById('dateFrom').value));
		var transTo = new Date(Date
				.parse(document.getElementById('dateTo').value));

		var merchant = $("#merchant").val();
		if (merchant == "" || merchant == null) {
			alert("Please Select Merchant")
			return false;
		}

		if (transFrom == null || transTo == null) {
			alert('Enter date value');
			return false;
		} else if (transFrom > transTo) {
			alert('From date must be before the to date');
			return false;
		} else if (transTo - transFrom > 30 * 86400000) {
			alert('No. of days can not be more than 30');
			return false;
		} else {

			var merchant = $("#merchant").val();
			var pgRefNum = $("#pgRefNum").val();
			var dateFrom = $("#dateFrom").val();
			var dateTo = $("#dateTo").val();
			var cbCaseID = $("#cbCaseID").val();

			var cbCaseIDflag = 0;
			var pgRefNumflag = 0;

			if (cbCaseID != null && cbCaseID != '' && cbCaseID != 'undefined') {
				cbCaseIDflag = 1;
			}

			if (pgRefNum != null && pgRefNum != '' && pgRefNum != 'undefined') {
				pgRefNumflag = 1;
			}

			if (pgRefNumflag == 1 && cbCaseIDflag == 1) {
				alert("Please Provide CB Case ID either PG REF Number");

				return false;
			} else {
				// 				$.post("downloadCBJourneyReport", {
				// 					merchant : merchant,
				// 					pgRefNum : pgRefNum,
				// 					dateFrom : dateFrom,
				// 					dateTo : dateTo,
				// 					cbCaseID : cbCaseID
				// 				}, function(data, status) {
				// 					$('#btnSubmit').prop('disabled', false);
				// 					alert("Data: " + data + "\nStatus: " + status);
				// 				});
				$("#DownloadCBJourneyReport").submit();

			}
		}
	}
</script>
</head>
<body id="kt_body"
	class="header-fixed header-tablet-and-mobile-fixed toolbar-enabled toolbar-fixed toolbar-tablet-and-mobile-fixed aside-enabled aside-fixed"
	style="-kt-toolbar-height: 55px; - -kt-toolbar-height-tablet-and-mobile: 55px">
	<div class="content d-flex flex-column flex-column-fluid"
		id="kt_content">
		<div class="toolbar" id="kt_toolbar">
			<!--begin::Container-->
			<div id="kt_toolbar_container"
				class="container-fluid d-flex flex-stack">
				<div data-kt-swapper="true" data-kt-swapper-mode="prepend"
					data-kt-swapper-parent="{default: '#kt_content_container', 'lg': '#kt_toolbar_container'}"
					class="page-title d-flex align-items-center me-3 flex-wrap mb-5 mb-lg-0 lh-1">
					<!--begin::Title-->
					<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">
						Chargeback Journey Report</h1>
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
						<li class="breadcrumb-item text-muted">Dispute Management</li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item"><span
							class="bullet bg-gray-200 w-5px h-2px"></span></li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item text-dark">Chargeback Journey
							Report</li>
						<!--end::Item-->
					</ul>
					<!--end::Breadcrumb-->
				</div>
				<!--end::Page title-->

			</div>
		</div>
		<div class="post d-flex flex-column-fluid" id="kt_post">
			<!--begin::Container-->
			<div id="kt_content_container" class="container-xxl">
				<div class="row my-5">
					<div class="col">
						<div class="card">
							<div class="card-body">
								<form action="cbJourneyDownloadAction" class="box-content"
									method="post" id="DownloadCBJourneyReport">
									<div class="row my-3 align-items-center">
										<div class="col-lg-4 my-2">
											<label class="d-flex align-items-center fs-6 fw-bold mb-2">
												<span class="required">Merchant</span>
											</label>
											<s:select name="merchant"
												class="form-select form-select-solid adminMerchants"
												id="merchant" headerKey=""
												headerValue="Please select Merchant" list="merchantList"
												listKey="payId" listValue="businessName" autocomplete="off" />
										</div>
										<div class="col-lg-4 my-2">
											<label class="d-flex align-items-center fs-6 fw-bold mb-2">
												<span class="required">Date From</span>
											</label>
											<!--begin::Icon-->
											<!--begin::Svg Icon | path: icons/duotune/general/gen014.svg-->
											<span class="svg-icon svg-icon-2 position-absolute mx-4">
												<svg width="24" height="24" viewBox="0 0 24 24" fill="none"
													xmlns="http://www.w3.org/2000/svg"> <path
													opacity="0.3"
													d="M21 22H3C2.4 22 2 21.6 2 21V5C2 4.4 2.4 4 3 4H21C21.6 4 22 4.4 22 5V21C22 21.6 21.6 22 21 22Z"
													fill="currentColor"></path> <path
													d="M6 6C5.4 6 5 5.6 5 5V3C5 2.4 5.4 2 6 2C6.6 2 7 2.4 7 3V5C7 5.6 6.6 6 6 6ZM11 5V3C11 2.4 10.6 2 10 2C9.4 2 9 2.4 9 3V5C9 5.6 9.4 6 10 6C10.6 6 11 5.6 11 5ZM15 5V3C15 2.4 14.6 2 14 2C13.4 2 13 2.4 13 3V5C13 5.6 13.4 6 14 6C14.6 6 15 5.6 15 5ZM19 5V3C19 2.4 18.6 2 18 2C17.4 2 17 2.4 17 3V5C17 5.6 17.4 6 18 6C18.6 6 19 5.6 19 5Z"
													fill="currentColor"></path> <path
													d="M8.8 13.1C9.2 13.1 9.5 13 9.7 12.8C9.9 12.6 10.1 12.3 10.1 11.9C10.1 11.6 10 11.3 9.8 11.1C9.6 10.9 9.3 10.8 9 10.8C8.8 10.8 8.59999 10.8 8.39999 10.9C8.19999 11 8.1 11.1 8 11.2C7.9 11.3 7.8 11.4 7.7 11.6C7.6 11.8 7.5 11.9 7.5 12.1C7.5 12.2 7.4 12.2 7.3 12.3C7.2 12.4 7.09999 12.4 6.89999 12.4C6.69999 12.4 6.6 12.3 6.5 12.2C6.4 12.1 6.3 11.9 6.3 11.7C6.3 11.5 6.4 11.3 6.5 11.1C6.6 10.9 6.8 10.7 7 10.5C7.2 10.3 7.49999 10.1 7.89999 10C8.29999 9.90003 8.60001 9.80003 9.10001 9.80003C9.50001 9.80003 9.80001 9.90003 10.1 10C10.4 10.1 10.7 10.3 10.9 10.4C11.1 10.5 11.3 10.8 11.4 11.1C11.5 11.4 11.6 11.6 11.6 11.9C11.6 12.3 11.5 12.6 11.3 12.9C11.1 13.2 10.9 13.5 10.6 13.7C10.9 13.9 11.2 14.1 11.4 14.3C11.6 14.5 11.8 14.7 11.9 15C12 15.3 12.1 15.5 12.1 15.8C12.1 16.2 12 16.5 11.9 16.8C11.8 17.1 11.5 17.4 11.3 17.7C11.1 18 10.7 18.2 10.3 18.3C9.9 18.4 9.5 18.5 9 18.5C8.5 18.5 8.1 18.4 7.7 18.2C7.3 18 7 17.8 6.8 17.6C6.6 17.4 6.4 17.1 6.3 16.8C6.2 16.5 6.10001 16.3 6.10001 16.1C6.10001 15.9 6.2 15.7 6.3 15.6C6.4 15.5 6.6 15.4 6.8 15.4C6.9 15.4 7.00001 15.4 7.10001 15.5C7.20001 15.6 7.3 15.6 7.3 15.7C7.5 16.2 7.7 16.6 8 16.9C8.3 17.2 8.6 17.3 9 17.3C9.2 17.3 9.5 17.2 9.7 17.1C9.9 17 10.1 16.8 10.3 16.6C10.5 16.4 10.5 16.1 10.5 15.8C10.5 15.3 10.4 15 10.1 14.7C9.80001 14.4 9.50001 14.3 9.10001 14.3C9.00001 14.3 8.9 14.3 8.7 14.3C8.5 14.3 8.39999 14.3 8.39999 14.3C8.19999 14.3 7.99999 14.2 7.89999 14.1C7.79999 14 7.7 13.8 7.7 13.7C7.7 13.5 7.79999 13.4 7.89999 13.2C7.99999 13 8.2 13 8.5 13H8.8V13.1ZM15.3 17.5V12.2C14.3 13 13.6 13.3 13.3 13.3C13.1 13.3 13 13.2 12.9 13.1C12.8 13 12.7 12.8 12.7 12.6C12.7 12.4 12.8 12.3 12.9 12.2C13 12.1 13.2 12 13.6 11.8C14.1 11.6 14.5 11.3 14.7 11.1C14.9 10.9 15.2 10.6 15.5 10.3C15.8 10 15.9 9.80003 15.9 9.70003C15.9 9.60003 16.1 9.60004 16.3 9.60004C16.5 9.60004 16.7 9.70003 16.8 9.80003C16.9 9.90003 17 10.2 17 10.5V17.2C17 18 16.7 18.4 16.2 18.4C16 18.4 15.8 18.3 15.6 18.2C15.4 18.1 15.3 17.8 15.3 17.5Z"
													fill="currentColor"></path> </svg>
											</span>
											<!--end::Svg Icon-->
											<!--end::Icon-->
											<!--begin::Datepicker-->
											<input
												class="form-control form-control-solid ps-12 flatpickr-input"
												placeholder="Select a date" name="dateFrom" id="dateFrom"
												type="text">
											<!--end::Datepicker-->
										</div>

										<div class="col-lg-4 my-2">
											<label class="d-flex align-items-center fs-6 fw-bold mb-2">
												<span class="required">Date To</span>
											</label>
											<!--begin::Icon-->
											<!--begin::Svg Icon | path: icons/duotune/general/gen014.svg-->
											<span class="svg-icon svg-icon-2 position-absolute mx-4">
												<svg width="24" height="24" viewBox="0 0 24 24" fill="none"
													xmlns="http://www.w3.org/2000/svg"> <path
													opacity="0.3"
													d="M21 22H3C2.4 22 2 21.6 2 21V5C2 4.4 2.4 4 3 4H21C21.6 4 22 4.4 22 5V21C22 21.6 21.6 22 21 22Z"
													fill="currentColor"></path> <path
													d="M6 6C5.4 6 5 5.6 5 5V3C5 2.4 5.4 2 6 2C6.6 2 7 2.4 7 3V5C7 5.6 6.6 6 6 6ZM11 5V3C11 2.4 10.6 2 10 2C9.4 2 9 2.4 9 3V5C9 5.6 9.4 6 10 6C10.6 6 11 5.6 11 5ZM15 5V3C15 2.4 14.6 2 14 2C13.4 2 13 2.4 13 3V5C13 5.6 13.4 6 14 6C14.6 6 15 5.6 15 5ZM19 5V3C19 2.4 18.6 2 18 2C17.4 2 17 2.4 17 3V5C17 5.6 17.4 6 18 6C18.6 6 19 5.6 19 5Z"
													fill="currentColor"></path> <path
													d="M8.8 13.1C9.2 13.1 9.5 13 9.7 12.8C9.9 12.6 10.1 12.3 10.1 11.9C10.1 11.6 10 11.3 9.8 11.1C9.6 10.9 9.3 10.8 9 10.8C8.8 10.8 8.59999 10.8 8.39999 10.9C8.19999 11 8.1 11.1 8 11.2C7.9 11.3 7.8 11.4 7.7 11.6C7.6 11.8 7.5 11.9 7.5 12.1C7.5 12.2 7.4 12.2 7.3 12.3C7.2 12.4 7.09999 12.4 6.89999 12.4C6.69999 12.4 6.6 12.3 6.5 12.2C6.4 12.1 6.3 11.9 6.3 11.7C6.3 11.5 6.4 11.3 6.5 11.1C6.6 10.9 6.8 10.7 7 10.5C7.2 10.3 7.49999 10.1 7.89999 10C8.29999 9.90003 8.60001 9.80003 9.10001 9.80003C9.50001 9.80003 9.80001 9.90003 10.1 10C10.4 10.1 10.7 10.3 10.9 10.4C11.1 10.5 11.3 10.8 11.4 11.1C11.5 11.4 11.6 11.6 11.6 11.9C11.6 12.3 11.5 12.6 11.3 12.9C11.1 13.2 10.9 13.5 10.6 13.7C10.9 13.9 11.2 14.1 11.4 14.3C11.6 14.5 11.8 14.7 11.9 15C12 15.3 12.1 15.5 12.1 15.8C12.1 16.2 12 16.5 11.9 16.8C11.8 17.1 11.5 17.4 11.3 17.7C11.1 18 10.7 18.2 10.3 18.3C9.9 18.4 9.5 18.5 9 18.5C8.5 18.5 8.1 18.4 7.7 18.2C7.3 18 7 17.8 6.8 17.6C6.6 17.4 6.4 17.1 6.3 16.8C6.2 16.5 6.10001 16.3 6.10001 16.1C6.10001 15.9 6.2 15.7 6.3 15.6C6.4 15.5 6.6 15.4 6.8 15.4C6.9 15.4 7.00001 15.4 7.10001 15.5C7.20001 15.6 7.3 15.6 7.3 15.7C7.5 16.2 7.7 16.6 8 16.9C8.3 17.2 8.6 17.3 9 17.3C9.2 17.3 9.5 17.2 9.7 17.1C9.9 17 10.1 16.8 10.3 16.6C10.5 16.4 10.5 16.1 10.5 15.8C10.5 15.3 10.4 15 10.1 14.7C9.80001 14.4 9.50001 14.3 9.10001 14.3C9.00001 14.3 8.9 14.3 8.7 14.3C8.5 14.3 8.39999 14.3 8.39999 14.3C8.19999 14.3 7.99999 14.2 7.89999 14.1C7.79999 14 7.7 13.8 7.7 13.7C7.7 13.5 7.79999 13.4 7.89999 13.2C7.99999 13 8.2 13 8.5 13H8.8V13.1ZM15.3 17.5V12.2C14.3 13 13.6 13.3 13.3 13.3C13.1 13.3 13 13.2 12.9 13.1C12.8 13 12.7 12.8 12.7 12.6C12.7 12.4 12.8 12.3 12.9 12.2C13 12.1 13.2 12 13.6 11.8C14.1 11.6 14.5 11.3 14.7 11.1C14.9 10.9 15.2 10.6 15.5 10.3C15.8 10 15.9 9.80003 15.9 9.70003C15.9 9.60003 16.1 9.60004 16.3 9.60004C16.5 9.60004 16.7 9.70003 16.8 9.80003C16.9 9.90003 17 10.2 17 10.5V17.2C17 18 16.7 18.4 16.2 18.4C16 18.4 15.8 18.3 15.6 18.2C15.4 18.1 15.3 17.8 15.3 17.5Z"
													fill="currentColor"></path> </svg>
											</span>
											<!--end::Svg Icon-->
											<!--end::Icon-->
											<!--begin::Datepicker-->
											<input
												class="form-control form-control-solid ps-12 flatpickr-input"
												placeholder="Select a date" name="dateTo" id="dateTo"
												type="text">
											<!--end::Datepicker-->
										</div>
									</div>
									<div class="row my-3 align-items-center">
										<div class="col-lg-4 my-2">
											<label class="d-flex align-items-center fs-6 fw-bold mb-2">
												<span class="">CB Case ID <sup style="color: red;">(c*)</sup></span>
											</label>
											<s:textfield id="cbCaseId"
												class="form-control form-control-solid" name="cbCaseId"
												type="text" value="" autocomplete="off" maxlength="16"></s:textfield>
										</div>


										<div class="col-lg-4 my-2">
											<label class="d-flex align-items-center fs-6 fw-bold mb-2">
												<span class="">PG REF Number <sup style="color: red;">(c*)</sup></span>
											</label>
											<s:textfield id="pgRefNo"
												class="form-control form-control-solid" name="pgRefNo"
												type="text" value="" autocomplete="off" maxlength="16"></s:textfield>
										</div>
										<div class="col-lg-1 my-2">
											<label class="d-flex align-items-center fs-6 fw-bold mb-2">
												<span class="">&nbsp;</span>
											</label>
											<button type="button" class="btn btn-primary" id="done"
												onclick="reloadTable()">Submit</button>
										</div>
										<div class="col-lg-1 my-2">
											<label class="d-flex align-items-center fs-6 fw-bold mb-2">
												<span class="">&nbsp;</span>
											</label>
											<button type="button" class="btn btn-primary"
												onclick="submitForm()">Download</button>
										</div>
								</form>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>

	<div class="post d-flex flex-column-fluid" id="kt_post">
		<!--begin::Container-->
		<div id="kt_content_container" class="container-xxl">
			<div class="row my-5">
				<div class="col">
					<div class="card">
						<div class="card-body">
							<div class="row g-9 mb-8">
								<div class="table-responsive">
									<table id="txnResultDataTable"
										class="table table-striped table-row-bordered gy-5 gs-7 dataTable no-footer">
										<thead>
											<tr class="fw-bold fs-6 text-gray-800">

												<th class="min-w-90px">Pay Id</th>
												<th class="min-w-90px">CB Case Id</th>
												<th class="min-w-90px">Txn Amount</th>
												<th class="min-w-90px">PG Case Id</th>
												<th class="min-w-90px">Merchant Txn Id</th>
												<th class="min-w-90px">Order Id</th>
												<th class="min-w-90px">Pg Ref Num</th>
												<th class="min-w-90px">Bank Txn Id</th>
												<th class="min-w-90px">Cb Amount</th>
												<th class="min-w-90px">Cb Reason</th>
												<th class="min-w-90px">Cb Reason Code</th>
												<th class="min-w-90px">Cb Intimation Date</th>
												<th class="min-w-90px">Cb Deadline Date</th>
												<th class="min-w-90px">Mode Of Payment</th>
												<th class="min-w-90px">Acquirer Name</th>
												<th class="min-w-90px">Settlement Date</th>
												<th class="min-w-90px">Date Of Txn</th>
												<th class="min-w-90px">Customer Name</th>
												<th class="min-w-90px">Customer Phone</th>
												<th class="min-w-90px">Email</th>
												<th class="min-w-90px">Notification Email</th>
												<th class="min-w-90px">Status</th>


											</tr>
										</thead>
										<tfoot>
											<tr class="fw-bold fs-6 text-gray-800">
												<th class="min-w-90px"></th>
												<th class="min-w-90px"></th>
												<th class="min-w-90px"></th>
												<th class="min-w-90px"></th>
												<th class="min-w-90px"></th>
												<th class="min-w-90px"></th>
												<th class="min-w-90px"></th>
												<th class="min-w-90px"></th>
												<th class="min-w-90px"></th>
												<th class="min-w-90px"></th>
												<th class="min-w-90px"></th>
												<th class="min-w-90px"></th>
												<th class="min-w-90px"></th>
												<th class="min-w-90px"></th>
												<th class="min-w-90px"></th>
												<th class="min-w-90px"></th>
												<th class="min-w-90px"></th>
												<th class="min-w-90px"></th>
												<th class="min-w-90px"></th>
												<th class="min-w-90px"></th>
												<th class="min-w-90px"></th>
												<th class="min-w-90px"></th>


											</tr>
										</tfoot>
									</table>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>



	<script type="text/javascript">
		$("#dateFrom").flatpickr({
			maxDate : new Date(),
			dateFormat : 'Y-m-d',
			defaultDate : "today"
		});
		$("#dateTo").flatpickr({
			maxDate : new Date(),
			dateFormat : 'Y-m-d',
			defaultDate : "today"
		});
		function convert(str) {
			var date = new Date(str), mnth = ("0" + (date.getMonth() + 1))
					.slice(-2), day = ("0" + date.getDate()).slice(-2);
			//return [date.getFullYear(), mnth, day].join("-");
			return [ day, mnth, date.getFullYear() ].join("-");
		}

		function reloadTable() {
			dateFrom = document.getElementById("dateFrom").value;
			dateTo = document.getElementById("dateTo").value;
			var transFrom = convert(dateFrom);
			var transTo = convert(dateTo);
			var transFrom1 = new Date(Date.parse(document
					.getElementById("dateFrom").value));
			var transTo1 = new Date(Date.parse(document
					.getElementById("dateTo").value));
			if (transFrom1 == null || transTo1 == null) {
				alert('Enter date value');
				return false;
			}
			if (transFrom1 > transTo1) {
				alert('From date must be before the to date');
				$('#loader-wrapper').hide();
				$('#dateFrom').focus();
				return false;
			}
			if (transTo1 - transFrom1 > 31 * 86400000) {
				alert('No. of days can not be more than 31');
				$('#loader-wrapper').hide();
				$('#dateFrom').focus();
				return false;
			} else {
				//dataTable();

				var merchant = $("#merchant").val();
				var pgRefNum = $("#pgRefNum").val();
				var dateFrom = $("#dateFrom").val();
				var dateTo = $("#dateTo").val();
				var cbCaseID = $("#cbCaseID").val();

				var cbCaseIDflag = 0;
				var pgRefNumflag = 0;

				if (cbCaseID != null && cbCaseID != ''
						&& cbCaseID != 'undefined') {
					cbCaseIDflag = 1;
				}

				if (pgRefNum != null && pgRefNum != ''
						&& pgRefNum != 'undefined') {
					pgRefNumflag = 1;
				}

				if (pgRefNumflag == 1 && cbCaseIDflag == 1) {
					alert("Please Provide CB Case ID either PG REF Number");

					return false;
				} else {

					var tableObj = $('#txnResultDataTable');
					var table = tableObj.DataTable();
					table.ajax.reload();

				}

			}
		}
		function generatePostData(d) {

			var token = document.getElementsByName("token")[0].value;

			var merchant = $("#merchant").val();
			var pgRefNum = $("#pgRefNo").val();
			var dateFrom = $("#dateFrom").val();
			var dateTo = $("#dateTo").val();
			var cbCaseID = $("#cbCaseId").val();

			var obj = {
				merchant : merchant,
				pgRefNum : pgRefNum,
				dateTo : dateTo,
				dateFrom : dateFrom,
				cbCaseID : cbCaseID,
				draw : d.draw,
				length : d.length,
				start : d.start,
				token : token,
				"struts.token.name" : "token",
			};

			return obj;
		}

		debugger
		$('#txnResultDataTable')

				.dataTable(

						{
							"columnDefs" : [ {
								className : "dt-body-right",
								"targets" : [ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
										11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
										21, 22 ]
							} ],

							dom : 'Brtipl',
							buttons : [
									{
										extend : 'print',
										exportOptions : {
											columns : ':visible'
										}
									},
									{
										extend : 'pdfHtml5',
										orientation : 'landscape',
										pageSize : 'legal',
										//footer : true,
										title : 'Search Transactions',
										exportOptions : {
											columns : [ ':visible' ]
										},
										customize : function(doc) {
											doc.defaultStyle.alignment = 'center';
											doc.styles.tableHeader.alignment = 'center';
											doc.defaultStyle.fontSize = 8;
										}
									},
									{
										extend : 'copy',
										exportOptions : {
											columns : [ 0, 1, 2, 3, 4, 5, 6, 7,
													8, 9, 10, 11, 12, 13, 14,
													15, 16, 17, 18, 19, 20, 21 ]
										}
									},
									{
										extend : 'csv',
										exportOptions : {
											columns : [ 0, 1, 2, 3, 4, 5, 6, 7,
													8, 9, 10, 11, 12, 13, 14,
													15, 16, 17, 18, 19, 20, 21 ]
										}
									},
									{
										extend : 'pdf',
										exportOptions : {
											columns : [ 0, 1, 2, 3, 4, 5, 6, 7,
													8, 9, 10, 11, 12, 13, 14,
													15, 16, 17, 18, 19, 20, 21 ]
										}
									}, 'colvis', 'excel', 'print', ],
							scrollY : true,
							scrollX : true,
							searchDelay : 500,
							processing : false,
							destroy : true,
							serverSide : true,
							order : [ [ 5, 'desc' ] ],
							stateSave : true,

							"ajax" : {

								"url" : "CBViewList",
								"type" : "POST",
								"timeout" : 0,
								"data" : function(d) {
									return generatePostData(d);
								}
							},
							"fnDrawCallback" : function() {
								$("#submit").removeAttr("disabled");
								$('#loader-wrapper').hide();
							},
							"searching" : false,
							"ordering" : false,
							"processing" : true,
							"serverSide" : true,
							"paginationType" : "full_numbers",
							"lengthMenu" : [ [ 10, 25, 50, 100 ],
									[ 10, 25, 50, 100 ] ],
							"order" : [ [ 2, "desc" ] ],

							"columnDefs" : [ {
								"type" : "html-num-fmt",
								"targets" : 4,
								"orderable" : true,
								"targets" : [ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
										11, 12, 13, 14, 15, 16, 17, 18, 19, 20,
										21 ]
							} ],

							"columns" : [

									{
										"data" : "merchantPayId",
										"className" : "text-class"
									},
									{
										"data" : null,
										'render' : function(data, type, full,
												meta) {
											return '<p  class="grab" style="cursor: grab" )">'
													+ ""
													+ data.cbCaseId
													+ ""
													+ '</p>';
										}
									}, {
										"data" : "txnAmount",
										"className" : "text-class"
									}, {
										"data" : "pgCaseId",
										"className" : "text-class"

									}, {
										"data" : "merchantTxnId",
										"className" : "text-class"
									}, {
										"data" : "orderId",
										"className" : "text-class"
									}, {
										"data" : "pgRefNo",
										"className" : "text-class"
									}, {
										"data" : "bankTxnId",
										"className" : "text-class"
									}, {
										"data" : "cbAmount",
										"className" : "text-class"
									}, {
										"data" : "cbReason",
										"className" : "text-class"
									}, {
										"data" : "cbReasonCode",
										"className" : "text-class"
									}, {
										"data" : "cbIntimationDate",
										"className" : "text-class"
									}, {
										"data" : "cbDdlineDate",
										"className" : "text-class"
									}, {
										"data" : "modeOfPayment",
										"className" : "text-class"
									}, {
										"data" : "acqName",
										"className" : "text-class"
									}, {
										"data" : "settlemtDate",
										"className" : "text-class"
									}, {
										"data" : "dtOfTxn",
										"className" : "text-class"
									}, {
										"data" : "customerName",
										"className" : "text-class"
									}, {
										"data" : "customerPhone",
										"className" : "text-class"
									}, {
										"data" : "email",
										"className" : "text-class"
									}, {
										"data" : "nemail",
										"className" : "text-class"
									}, {
										"data" : "status",
										"className" : "text-class"
									} ]
						});
		$(function() {
			//var datepick = $.datepicker;
			var needToShowAcqFields = $("#txnResultDataTable").val();
			var userType = $("#userTypeName").val();
			var tableName = "txnResultDataTable"
			var table = $('#' + tableName).DataTable();
			$('#' + tableName).on('click', 'td', function() {
				var rowIndex = table.cell(this).index().row;
				var colIndex = table.cell(this).index().column;
				var rowData = table.row(rowIndex).data();
				if (colIndex == 1) {
					getChargebackHistory(rowData.cbCaseId);
				}
			});
		});
		$("#myModal").hide();
		function getChargebackHistory(cbcaseId) {
			$.post("CbReportList", {
				cbCaseID : cbcaseId
			}, function(data, status) {
				var obj=data.transactionList;
				//data.transactionList[0].status
				//data.transactionList[0].date
				$("#trail tbody tr").remove();

				for (let i=0; i<obj.length; i++){
					var o = obj[i] ;
					if(o.status=="CLOSED"){
					$("#trail tbody").append("<tr><td>"+o.date+"</td><td>"+o.status+"</td></tr>");
					}
				}
				for (let i=0; i<obj.length; i++){
					var o = obj[i] ;
					if(o.status=="POD"){
					$("#trail tbody").append("<tr><td>"+o.date+"</td><td>"+o.status+"</td></tr>");
					}
				}
				for (let i=0; i<obj.length; i++){
					var o = obj[i] ;
					if(o.status=="ACCEPTED"){
					$("#trail tbody").append("<tr><td>"+o.date+"</td><td>"+o.status+"</td></tr>");
					}
				}
				for (let i=0; i<obj.length; i++){
					var o = obj[i] ;
					if(o.status=="INITIATED"){
					$("#trail tbody").append("<tr><td>"+o.date+"</td><td>"+o.status+"</td></tr>");
					}
				}
				$("#myModal").show();		
			});
			
		}
		function closeModal() {
			$("#myModal").hide();
		}
	</script>

	<div class="modal" tabindex="-1" role="dialog" id="myModal"
		style="top: 15vh; left: 5%;">
		<div class="modal-dialog modal-lg" role="document"
			style="box-shadow: 2px 2px 2px 2px #f9b562 !important;">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title">Chargeback Audit Trail</h5>
<!-- 					<button type="button" class="close" data-dismiss="modal" -->
<!-- 						aria-label="Close" onclick="closeModal()"> -->
<%-- 						<span aria-hidden="true">&times;</span> --%>
<!-- 					</button> -->
				</div>
				<div class="modal-body">

					<table id="trail" class="table table-striped ">
						<thead>
							<tr>

								<th scope="col" style="font-size: 16px; font-weight: bold;">DATE</th>
								<th scope="col" style="font-size: 16px; font-weight: bold;">STATUS</th>
							</tr>
						</thead>
						<tbody>

						</tbody>
					</table>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-secondary"
						data-dismiss="modal" onclick="closeModal()">Close</button>
				</div>
			</div>
		</div>
	</div>


</body>

</html>