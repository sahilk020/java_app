<%@page import="com.pay10.crm.actionBeans.TransactionStatusBean"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="/struts-tags" prefix="s"%>
<html lang="en">

<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>Merchant Summary Report</title>
<link rel="shortcut icon" href="assets/media/images/paylogo.svg" />
<!--begin::Fonts-->
<link rel="stylesheet"
	href="https://fonts.googleapis.com/css?family=Inter:300,400,500,600,700" />
<!--end::Fonts-->
<!-- <link href="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.css" rel="stylesheet"
						type="text/css" /> -->
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

<style>
span.select2-selection.select2-selection--single.form-select.form-select-solid.merchantPayId
	{
	padding: 13px 70px !important;
}

.daterangepicker .drp-selected {
	display: none !important;
}

.daterangepicker .ranges li:hover {
	color: #234B7A !important;
}

.daterangepicker .ranges li.active {
	background-color: #202f4b;
	color: #fff;
}

#dateRange {
	border: none;
	background: none;
}
div#reportrange {
    padding-top: 1.5px;
    padding-bottom: 1.5px;
}
textarea:focus, input:focus {
	outline: none;
}

.daterangepicker .drp-calendar td.in-range.available:not(.active):not(.off):n   ot(.today)
	{
	background-color: var(- -kt-component-hover-bg);
	color: #6b6c6c;
}

.daterangepicker .drp-calendar td:hover {
	background-color: var(- -kt-component-hover-bg);
	color: #747474;
}

.table thead tr th {
	font-size: 14px !important;
	font-weight: 900 !important;
	color: black !important;
	/* padding: 5px 8px !important; */
}

/* .table tbody tr th{
    padding: 6px 8px !important;
    font-size: 12px !important;
} */
.lineheight {
	line-height: 4px !important;
}

.totaltxn {
	background-color: #787676 !important;
	text-align: center;
	line-height: 4px !important;
	color: white !important;
}

.capturetxn {
	background-color: #58b58a !important;
	text-align: center;
	line-height: 4px !important;
	color: white !important;
}

.table tbody tr td {
	padding: 6px 8px !important;
	font-size: 12px !important;
	line-height: 12px;
	font-weight: 600 !important;
	color: black !important;
}

.harddeclinetxn {
	background-color: #e00000 !important;
	text-align: center;
	line-height: 4px !important;
	color: white !important;
}

.softdeclinetxn {
	background-color: #ff6004 !important;
	text-align: center;
	line-height: 4px !important;
	color: white !important;
}

.pendingtxn {
	background-color: #f4f81d !important;
	text-align: center;
	line-height: 4px !important;
	color: #7f7f7f !important;
}

.nbupiccdcwltxn {
	background-color: #44c8e9 !important;
	text-align: center;
	line-height: 4px !important;
	color: white !important;
}

.table tbody tr td {
	padding: 6px 8px !important;
	font-size: 10.5px !important;
	font-weight: 600 !important;
}

.card1 {
	background: linear-gradient(168deg, #91949469, transparent);
	border-radius: 10px !important;
	margin-top: 10px !important;
}

.card2 {
	background: linear-gradient(45deg, #c1f3b0d6, transparent);
	border-radius: 10px !important;
	margin-top: 10px !important;
}

.card3 {
	background: linear-gradient(133deg, #efb9b9, transparent);
	border-radius: 10px !important;
	margin-top: 10px !important;
}

.card4 {
	background: linear-gradient(189deg, #fab775, transparent);
	border-radius: 10px !important;
	margin-top: 10px !important;
}

.card5 {
	background: linear-gradient(189deg, #fff85f, transparent);
	border-radius: 10px !important;
	margin-top: 10px !important;
}

.card6 {
	background: linear-gradient(115deg, #3494a469, transparent);
	border-radius: 10px !important;
	margin-top: 10px !important;
}

.forwardicon {
	height: 15px;
	width: 15px;
	float: right;
	margin-right: 5px;
	margin-top: 3px;
}

button#SubmitButton {
	margin-top: 22px;
}

.font-weight {
	font-weight: bold;
}
</style>
<script type="text/javascript">
						var flag = 0;
						$(document).ready(function () {
							$("#moptype").select2();
							$("#paymentMethods").select2();
							$("#acquirer").select2();
							$("#merchant").select2();
						});
					</script>
</head>

<body>
	<!-- Header -->
	<div class="content d-flex flex-column flex-column-fluid"
		id="kt_content">
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
					<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">
						Merchant Summary Report</h1>
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
						<li class="breadcrumb-item text-muted">Analytics</li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item"><span
							class="bullet bg-gray-200 w-5px h-2px"></span></li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item text-dark">Merchant Summary Report</li>
						<!--end::Item-->
					</ul>
					<!--end::Breadcrumb-->
				</div>
				<!--end::Page title-->

			</div>
			<!--end::Container-->
		</div>

		<!-- select2-selection -->
		<div class="post d-flex flex-column-fluid" id="kt_post">
			<!--begin::Container-->
			<div id="kt_content_container" class="container-xxl">
				<div class="row my-5">
					<div class="col">
						<div class="card">
							<div class="card-body">
								<div
									class="row my-3 align-items-center d-flex justify-content-start">
									<!-- date -->
									<!-- <div class="col-md-3 fv-row"> -->
									<!-- 													<div class="col-auto my-2 merchant-text"> -->
									<!-- 														<p class="text-center m-0 w-100"><b>Date</b></p -->
									<!-- 															class="text-sm-center m-0"> -->
									<!-- 													</div> -->
									<div class="col-lg-6 col-md-6 col-sm-12 my-2">
										<label class="fs-6 fw-semibold mb-2">
											<p class="text-center m-0 w-100">
												<b>Date</b>
											</p
															class="text-sm-center m-0">
										</label>
										<div id="reportrange"
											class="form-select form-select-solid merchantPayId">
											<input type="text" id="dateRange" readonly="readonly"
												class="form-select form-select-solid merchantPayId">
										</div>
									</div>


									<!-- <div class="col-md-3 fv-row"> -->
									<!-- 													<div class="col-auto my-2 merchant-text"> -->
									<!-- 														<p class="text-center m-0 w-100"><b>Merchant</b></p -->
									<!-- 															class="text-sm-center m-0"> -->
									<!-- 													</div> -->
									<div class="col-auto my-2">
										<label class="fs-6 fw-semibold mb-2">
											<p class="text-center m-0 w-100">
												<b>Merchant</b>
											</p
															class="text-sm-center m-0">
										</label>
										<s:select name="payId" id="merchant" headerKey="All"
											headerValue="ALL" list="merchantList" listKey="payId"
											listValue="businessName" autocomplete="off"
											class="form-select form-select-solid merchantPayId" />
									</div>

									<!-- <div class="col-md-3 fv-row"> -->
									<!-- 													<div class="col-auto my-2 merchant-text"> -->
									<!-- 														<p class="text-center m-0 w-100"><b>Acquirer</b></p -->
									<!-- 															class="text-sm-center m-0"> -->
									<!-- 													</div> -->
									<div class="col-auto my-2">
										<label class="fs-6 fw-semibold mb-2">
											<p class="text-center m-0 w-100">
												<b>Acquirer</b>
											</p
															class="text-sm-center m-0">
										</label>
										<s:select headerKey="ALL" headerValue="ALL"
											list="@com.pay10.commons.util.AcquirerTypeUI@values()"
											id="acquirer"
											class="form-select form-select-solid merchantPayId"
											name="acquirer" value="acquirer" listValue="name"
											listKey="code" />
									</div>



									<!-- </div>
												<div class="row my-3 align-items-center"> -->

									<!-- <div class="col-md-3 fv-row"> -->
									<!-- 													<div class="col-auto my-2 merchant-text"> -->
									<!-- 														<p class="text-center m-0 w-100"><b>Payment Type</b></p -->
									<!-- 															class="text-sm-center m-0"> -->
									<!-- 													</div> -->
									<div class="col-auto my-2">
										<label class="fs-6 fw-semibold mb-2">
											<p class="text-center m-0 w-100">
												<b>Payment Type</b>
											</p
															class="text-sm-center m-0">
										</label>
										<s:select class="form-select form-select-solid merchantPayId"
											list="@com.pay10.commons.util.PaymentTypeUI@values()"
											listKey="code" listValue="name" id="paymentMethods"  onchange="getMopType(this.value,'moptype')" headerKey="All"
											headerValue="ALL" />
									</div>

									<!-- 													<div class="col-auto my-2 merchant-text"> -->
									<!-- 														<p class="text-center m-0 w-100"><b>Mode Of Payment</b></p -->
									<!-- 															class="text-sm-center m-0"> -->
									<!-- 													</div> -->
									<div class="col-auto my-2">
										<label class="fs-6 fw-semibold mb-2">
											<p class="text-center m-0 w-100">
												<b>Mode Of Payment</b>
											</p
															class="text-sm-center m-0">
										</label>
										<s:select class="form-select form-select-solid merchantPayId"
											list="@com.pay10.commons.util.MopType@values()"
											listKey="code" id="moptype" headerKey="All" headerValue="ALL" />
									</div>
									<!-- 													<div class="col-auto my-2 merchant-text"> -->
									<!-- 														<p class="text-center m-2 w-100"><b></b></p -->
									<!-- 															class="text-sm-center m-0"> -->
									<!-- 													</div> -->
									<div class="col-auto my-2">
										<button type="button" class="btn btn-primary submitbtn btn-sm"
											id="SubmitButton">Submit</button>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>

				<div class="card" style="padding: 10px 10px;">
					<div class="row">
						<div class="col-xl-6">
							<div class="border border-dark rounded card1">
								<div class=" border-dark border-bottom text-center font-weight">
									<span>Total Txn </span>
								</div>
								<table class="table table-striped table-bordered">
									<thead>
										<tr>
											<th scope="col" class="lineheight">Type</th>
											<th scope="col" class="lineheight">Count</th>
											<th scope="col" class="lineheight">Amount</th>
										</tr>
									</thead>
									<tbody>
										<tr>
											<td>CC</td>
											<td id="total_cc_count"></td>
											<td id="total_cc_amount"></td>
										</tr>
										<tr>
											<td>DC</td>
											<td id="total_dc_count"></td>
											<td id="total_dc_amount"></td>
										</tr>
										<tr>
											<td>NB</td>
											<td id="total_nb_count"></td>
											<td id="total_nb_amount"></td>
										</tr>

										<tr>
											<td>Wallet</td>
											<td id="total_wallet_count"></td>
											<td id="total_wallet_amount"></td>
										</tr>

										<tr>
											<td>UPI</td>
											<td id="total_upi_count"></td>
											<td id="total_upi_amount"></td>
										</tr>

										<tr>
											<td>Unknown</td>
											<td id="total_null_count"></td>
											<td id="total_null_amount"></td>
										</tr>
										<tr style="border-top: 1px solid black;">
											<td style="font-size: 15px !important;">Total</td>
											<td id="total_total_count"
												style="font-size: 15px !important;"></td>
											<td id="total_total_amount"
												style="font-size: 15px !important;"></td>
										</tr>
									</tbody>
								</table>
							</div>
						</div>
						<div class="col-xl-6">
							<div class="border border-dark rounded card1">
								<div class="border-dark border-bottom text-center font-weight">
									<span>Overall Summary </span>
								</div>
								<table class="table table-striped table-bordered lastrow">
									<thead>
										<tr>
											<th scope="col" class="lineheight">Status</th>
											<th scope="col" class="lineheight">Count</th>
											<th scope="col" class="lineheight">Volume</th>
										</tr>
									</thead>
									<tbody>
										<tr>
											<td>Capture</td>
											<td id="summary_overall_capture_count"></td>
											<td id="summary_overall_capture_volume"></td>
										</tr>
										<tr>
											<td></td>
											<td></td>
											<td></td>
										</tr>

										<tr>
											<td>Pending</td>
											<td id="summary_overall_pending_count"></td>
											<td id="summary_overall_pending_volume"></td>
										</tr>
										<tr>
											<td></td>
											<td></td>
											<td></td>
										</tr>

										<tr>
											<td>Hard Declined</td>
											<td id="summary_overall_hard_decline_count"></td>
											<td id="summary_overall_hard_decline_volume"></td>
										</tr>
										<tr>
											<td></td>
											<td></td>
											<td></td>
										</tr>
										<tr>
											<td></td>
											<td></td>
											<td></td>
										</tr>
										<tr>
											<td>Soft Declined</td>
											<td id="summary_overall_soft_decline_count"></td>
											<td id="summary_overall_soft_decline_volume"></td>
										</tr>
										<tr>
											<td></td>
											<td></td>
											<td></td>
										</tr>
										<tr>
											<td></td>
											<td></td>
											<td></td>
										</tr>
									</tbody>
								</table>
							</div>

						</div>
					</div>



					<div class="row">
						<div class="col-xl-3">
							<div class="border border-dark rounded card2">
								<div class="border-dark border-bottom text-center font-weight">
									<span>Capture <a
										href="javascript:getTransactionDetail('Captured')"><img
											src="../image/Forward_Icon.png" class="forwardicon"></a></span>
								</div>
								<table class="table table-striped table-bordered">
									<thead>
										<tr>
											<th scope="col" class="lineheight">Type</th>
											<th scope="col" class="lineheight">Count</th>
											<th scope="col" class="lineheight">Amount</th>
										</tr>
									</thead>
									<tbody>
										<tr>
											<td>CC</td>
											<td id="capture_cc_count"></td>
											<td id="capture_cc_amount"></td>
										</tr>
										<tr>
											<td>DC</td>
											<td id="capture_dc_count"></td>
											<td id="capture_dc_amount"></td>
										</tr>
										<tr>
											<td>NB</td>
											<td id="capture_nb_count"></td>
											<td id="capture_nb_amount"></td>
										</tr>

										<tr>
											<td>Wallet</td>
											<td id="capture_wallet_count"></td>
											<td id="capture_wallet_amount"></td>
										</tr>

										<tr>
											<td>UPI</td>
											<td id="capture_upi_count"></td>
											<td id="capture_upi_amount"></td>
										</tr>

										<tr>
											<td>Unknown</td>
											<td id="capture_null_count"></td>
											<td id="capture_null_amount"></td>
										</tr>
										<tr style="border-top: 1px solid black;">
											<td style="font-size: 15px !important;">Total</td>
											<td id="capture_total_count"
												style="font-size: 15px !important;"></td>
											<td id="capture_total_amount"
												style="font-size: 15px !important;"></td>
										</tr>
									</tbody>
								</table>
							</div>
						</div>

						<div class="col-xl-3">
							<div class="border border-dark rounded card3">
								<div class="border-dark border-bottom text-center font-weight">
									<span>Hard Declined <a
										href="javascript:getTransactionDetail('hardDecline')"><img
											src="../image/Forward_Icon.png" class="forwardicon"></a></span>
								</div>
								<table class="table table-striped table-bordered">
									<thead>
										<tr>
											<th scope="col" class="lineheight">Type</th>
											<th scope="col" class="lineheight">Count</th>
											<th scope="col" class="lineheight">Amount</th>
										</tr>
									</thead>
									<tbody>
										<tr>
											<td>CC</td>
											<td id="harddecline_cc_count"></td>
											<td id="harddecline_cc_amount"></td>
										</tr>
										<tr>
											<td>DC</td>
											<td id="harddecline_dc_count"></td>
											<td id="harddecline_dc_amount"></td>
										</tr>
										<tr>
											<td>NB</td>
											<td id="harddecline_nb_count"></td>
											<td id="harddecline_nb_amount"></td>
										</tr>

										<tr>
											<td>Wallet</td>
											<td id="harddecline_wallet_count"></td>
											<td id="harddecline_wallet_amount"></td>
										</tr>

										<tr>
											<td>UPI</td>
											<td id="harddecline_upi_count"></td>
											<td id="harddecline_upi_amount"></td>
										</tr>

										<tr>
											<td>Unknown</td>
											<td id="harddecline_null_count"></td>
											<td id="harddecline_null_amount"></td>
										</tr>
										<tr style="border-top: 1px solid black;">
											<td style="font-size: 15px !important;">Total</td>
											<td id="harddecline_total_count"
												style="font-size: 15px !important;"></td>
											<td id="harddecline_total_amount"
												style="font-size: 15px !important;"></td>
										</tr>
									</tbody>
								</table>

							</div>
						</div>




						<div class="col-xl-3">
							<div class="border border-dark rounded card4">
								<div class="border-dark border-bottom text-center font-weight">
									<span>Soft Declined <a
										href="javascript:getTransactionDetail('softDecline')"><img
											src="../image/Forward_Icon.png" class="forwardicon"></a></span>
								</div>
								<table class="table table-striped table-bordered">
									<thead>
										<tr>
											<th scope="col" class="lineheight">Type</th>
											<th scope="col" class="lineheight">Count</th>
											<th scope="col" class="lineheight">Amount</th>
										</tr>
									</thead>
									<tbody>
										<tr>
											<td>CC</td>
											<td id="softdecline_cc_count"></td>
											<td id="softdecline_cc_amount"></td>
										</tr>
										<tr>
											<td>DC</td>
											<td id="softdecline_dc_count"></td>
											<td id="softdecline_dc_amount"></td>
										</tr>
										<tr>
											<td>NB</td>
											<td id="softdecline_nb_count"></td>
											<td id="softdecline_nb_amount"></td>
										</tr>

										<tr>
											<td>Wallet</td>
											<td id="softdecline_wallet_count"></td>
											<td id="softdecline_wallet_amount"></td>
										</tr>

										<tr>
											<td>UPI</td>
											<td id="softdecline_upi_count"></td>
											<td id="softdecline_upi_amount"></td>
										</tr>

										<tr>
											<td>Unknown</td>
											<td id="softdecline_null_count"></td>
											<td id="softdecline_null_amount"></td>
										</tr>
										<tr style="border-top: 1px solid black;">
											<td style="font-size: 15px !important;">Total</td>
											<td id="softdecline_total_count"
												style="font-size: 15px !important;"></td>
											<td id="softdecline_total_amount"
												style="font-size: 15px !important;"></td>
										</tr>
									</tbody>
								</table>

							</div>
						</div>


						<div class="col-xl-3">
							<div class="border border-dark rounded card5">
								<div class="border-dark border-bottom text-center font-weight">
									<span>Pending <a
										href="javascript:getTransactionDetail('pending')"><img
											src="../image/Forward_Icon.png" class="forwardicon"></a></span>
								</div>
								<table class="table table-striped table-bordered">
									<thead>
										<tr>
											<th scope="col" class="lineheight">Type</th>
											<th scope="col" class="lineheight">Count</th>
											<th scope="col" class="lineheight">Amount</th>
										</tr>
									</thead>
									<tbody>
										<tr>
											<td>CC</td>
											<td id="pending_cc_count"></td>
											<td id="pending_cc_amount"></td>
										</tr>
										<tr>
											<td>DC</td>
											<td id="pending_dc_count"></td>
											<td id="pending_dc_amount"></td>
										</tr>
										<tr>
											<td>NB</td>
											<td id="pending_nb_count"></td>
											<td id="pending_nb_amount"></td>
										</tr>

										<tr>
											<td>Wallet</td>
											<td id="pending_wallet_count"></td>
											<td id="pending_wallet_amount"></td>
										</tr>

										<tr>
											<td>UPI</td>
											<td id="pending_upi_count"></td>
											<td id="pending_upi_amount"></td>
										</tr>

										<tr>
											<td>Unknown</td>
											<td id="pending_null_count"></td>
											<td id="pending_null_amount"></td>
										</tr>
										<tr style="border-top: 1px solid black;">
											<td style="font-size: 15px !important;">Total</td>
											<td id="pending_total_count"
												style="font-size: 15px !important;"></td>
											<td id="pending_total_amount"
												style="font-size: 15px !important;"></td>
										</tr>
									</tbody>
								</table>

							</div>
						</div>
					</div>
















					<div class="row">
						<div class="col-xl-3">
							<div class="border border-dark rounded card6">
								<div class="border-dark border-bottom text-center font-weight">
									<span>Summary(NB) </span>
								</div>
								<table class="table table-striped table-bordered lastrow">
									<thead>
										<tr>
											<th scope="col" class="lineheight">Status</th>
											<th scope="col" class="lineheight">Count</th>
											<th scope="col" class="lineheight">Volume</th>
										</tr>
									</thead>
									<tbody>
										<tr>
											<td>Capture</td>
											<td id="summary_nb_capture_count"></td>
											<td id="summary_nb_capture_volume"></td>
										</tr>
										<tr>
											<td>Pending</td>
											<td id="summary_nb_pending_count"></td>
											<td id="summary_nb_pending_volume"></td>
										</tr>
										<tr>
											<td>Hard Declined</td>
											<td id="summary_nb_hard_decline_count"></td>
											<td id="summary_nb_hard_decline_volume"></td>
										</tr>

										<tr>
											<td>Soft Declined</td>
											<td id="summary_nb_soft_decline_count"></td>
											<td id="summary_nb_soft_decline_volume"></td>
										</tr>


									</tbody>
								</table>
							</div>
						</div>

						<div class="col-xl-3">
							<div class="border border-dark rounded card6">
								<div class="border-dark border-bottom text-center font-weight">
									<span>Summary(UPI) </span>
								</div>
								<table class="table table-striped table-bordered lastrow">
									<thead>
										<tr>
											<th scope="col" class="lineheight">Status</th>
											<th scope="col" class="lineheight">Count</th>
											<th scope="col" class="lineheight">Volume</th>
										</tr>
									</thead>
									<tbody>
										<tr>
											<td>Capture</td>
											<td id="summary_upi_capture_count"></td>
											<td id="summary_upi_capture_volume"></td>
										</tr>
										<tr>
											<td>Pending</td>
											<td id="summary_upi_pending_count"></td>
											<td id="summary_upi_pending_volume"></td>
										</tr>
										<tr>
											<td>Hard Declined</td>
											<td id="summary_upi_hard_decline_count"></td>
											<td id="summary_upi_hard_decline_volume"></td>
										</tr>

										<tr>
											<td>Soft Declined</td>
											<td id="summary_upi_soft_decline_count"></td>
											<td id="summary_upi_soft_decline_volume"></td>
										</tr>


									</tbody>
								</table>
							</div>
						</div>




						<div class="col-xl-3">
							<div class="border border-dark rounded card6">
								<div class="border-dark border-bottom text-center font-weight">
									<span>Summary(CC/DC) </span>
								</div>
								<table class="table table-striped table-bordered lastrow">
									<thead>
										<tr>
											<th scope="col" class="lineheight">Status</th>
											<th scope="col" class="lineheight">Count</th>
											<th scope="col" class="lineheight">Volume</th>
										</tr>
									</thead>
									<tbody>
										<tr>
											<td>Capture</td>
											<td id="summary_cc_dc_capture_count"></td>
											<td id="summary_cc_dc_capture_volume"></td>
										</tr>
										<tr>
											<td>Pending</td>
											<td id="summary_cc_dc_pending_count"></td>
											<td id="summary_cc_dc_pending_volume"></td>
										</tr>
										<tr>
											<td>Hard Declined</td>
											<td id="summary_cc_dc_hard_decline_count"></td>
											<td id="summary_cc_dc_hard_decline_volume"></td>
										</tr>

										<tr>
											<td>Soft Declined</td>
											<td id="summary_cc_dc_soft_decline_count"></td>
											<td id="summary_cc_dc_soft_decline_volume"></td>
										</tr>


									</tbody>
								</table>
							</div>
						</div>


						<div class="col-xl-3">
							<div class="border border-dark rounded card6">
								<div class="border-dark border-bottom text-center font-weight">
									<span>Summary(Wallet) </span>
								</div>
								<table class="table table-striped table-bordered lastrow">
									<thead>
										<tr>
											<th scope="col" class="lineheight">Status</th>
											<th scope="col" class="lineheight">Count</th>
											<th scope="col" class="lineheight">Volume</th>
										</tr>
									</thead>
									<tbody>
										<tr>
											<td>Capture</td>
											<td id="summary_wallet_capture_count"></td>
											<td id="summary_wallet_capture_volume"></td>
										</tr>
										<tr>
											<td>Pending</td>
											<td id="summary_wallet_pending_count"></td>
											<td id="summary_wallet_pending_volume"></td>
										</tr>
										<tr>
											<td>Hard Declined</td>
											<td id="summary_wallet_hard_decline_count"></td>
											<td id="summary_wallet_hard_decline_volume"></td>
										</tr>

										<tr>
											<td>Soft Declined</td>
											<td id="summary_wallet_soft_decline_count"></td>
											<td id="summary_wallet_soft_decline_volume"></td>
										</tr>


									</tbody>
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

	</div>

	<script type="text/javascript">

	function getMopType(value,id){
            var merchantemail = document.getElementById("merchant").value;
      var paytype=value;



      $.ajax({
          type : "GET",
          url : "GetMoptype",
          timeout : 0,
          data : {
              "merchantemail":"ALL",
              "payment":paytype,
              "struts.token.name": "token",
            },
          success : function(data) {
            debugger
          var mopresult = [];

            mopresult = data.moplist;
                 $('#'+id).html("");
                 const countriesDropDown = document.getElementById(id);
                 let option = document.createElement("option");
                 option.setAttribute('value', "All");

                 let optionText = document.createTextNode("MOP TYPE");
                 option.appendChild(optionText);

                 countriesDropDown.appendChild(option);
                 for (let key in mopresult) {
                    option = document.createElement("option");
                    option.setAttribute('value', data.moplist[key].code);

                    optionText = document.createTextNode(data.moplist[key].name);
                    option.appendChild(optionText);

                    countriesDropDown.appendChild(option);
                  }

                 countriesDropDown.appendChild(option);
          }
        });
      }
	
						function getTransactionDetail(mode){
							if(flag==1){
								debugger
							var dateRange      = $("#dateRange").val();
							var merchant       = $("#merchant").val();
							var acquirer       = $("#acquirer").val();
							var paymentMethods = $("#paymentMethods").val();
							var moptype = $("#moptype").val();
								
						var url = new URL(window.location.href).origin
									+ "/crm/jsp/MerchantDashboardTransaction?mode=" + mode
									+ "&dateRange=" + dateRange + "&merchant=" + merchant+"&acquirer="+acquirer+"&paymentMethods="+paymentMethods+"&moptype="+moptype;
							window.open(url);
						}
					}
					</script>
	<script type="text/javascript">
						$(document).ready(function(){
							$('#loading').hide();
							$("#SubmitButton").click(function() {
								flag=1;
								debugger
								var dateRange      = $("#dateRange").val();
								var merchant       = $("#merchant").val();
								var acquirer       = $("#acquirer").val();
								var paymentMethods = $("#paymentMethods").val();
								var moptype = $("#moptype").val();
					
								$("#total_cc_count").text("");
								  $("#total_cc_amount").text("");
								  $("#total_dc_count").text("");
								  $("#total_dc_amount").text("");
								  $("#total_nb_count").text("");
								  $("#total_nb_amount").text("");
								  $("#total_wallet_count").text("");
								  $("#total_wallet_amount").text("");
								  $("#total_upi_count").text("");
								  $("#total_upi_amount").text("");
								  $("#total_null_count").text("");
								  $("#total_null_amount").text("");
								  $("#total_total_count").text("");
								  $("#total_total_amount").text("");
								  
								  $("#capture_cc_count").text("");
								  $("#capture_cc_amount").text("");
								  $("#capture_dc_count").text("");
								  $("#capture_dc_amount").text("");
								  $("#capture_nb_count").text("");
								  $("#capture_nb_amount").text("");
								  $("#capture_wallet_count").text("");
								  $("#capture_wallet_amount").text("");
								  $("#capture_upi_count").text("");
								  $("#capture_upi_amount").text("");
								  $("#capture_null_count").text("");
								  $("#capture_null_amount").text("");
								  $("#capture_total_count").text("");
								  $("#capture_total_amount").text("");
								  
								  $("#pending_cc_count").text("");
								  $("#pending_cc_amount").text("");
								  $("#pending_dc_count").text("");
								  $("#pending_dc_amount").text("");
								  $("#pending_nb_count").text("");
								  $("#pending_nb_amount").text("");
								  $("#pending_wallet_count").text("");
								  $("#pending_wallet_amount").text("");
								  $("#pending_upi_count").text("");
								  $("#pending_upi_amount").text("");
								  $("#pending_null_count").text("");
								  $("#pending_null_amount").text("");
								  $("#pending_total_count").text("");
								  $("#pending_total_amount").text("");
								
									
								  $("#harddecline_cc_count").text("");
								  $("#harddecline_cc_amount").text("");
								  $("#harddecline_dc_count").text("");
								  $("#harddecline_dc_amount").text("");
								  $("#harddecline_nb_count").text("");
								  $("#harddecline_nb_amount").text("");
								  $("#harddecline_wallet_count").text("");
								  $("#harddecline_wallet_amount").text("");
								  $("#harddecline_upi_count").text("");
								  $("#harddecline_upi_amount").text("");
								  $("#harddecline_null_count").text("");
								  $("#harddecline_null_amount").text("");
								  $("#harddecline_total_count").text("");
								  $("#harddecline_total_amount").text("");
								  
								  
								  $("#softdecline_cc_count").text("");
								  $("#softdecline_cc_amount").text("");
								  $("#softdecline_dc_count").text("");
								  $("#softdecline_dc_amount").text("");
								  $("#softdecline_nb_count").text("");
								  $("#softdecline_nb_amount").text("");
								  $("#softdecline_wallet_count").text("");
								  $("#softdecline_wallet_amount").text("");
								  $("#softdecline_upi_count").text("");
								  $("#softdecline_upi_amount").text("");
								  $("#softdecline_null_count").text("");
								  $("#softdecline_null_amount").text("");
								  $("#softdecline_total_count").text("");
								  $("#softdecline_total_amount").text("");
								  
								  $("#summary_nb_capture_count").text("");
								  $("#summary_nb_capture_volume").text("");
								  $("#summary_nb_pending_count").text("");
								  $("#summary_nb_pending_volume").text("");
								  $("#summary_nb_hard_decline_count").text("");
								  $("#summary_nb_hard_decline_volume").text("");
								  $("#summary_nb_soft_decline_count").text("");
								  $("#summary_nb_soft_decline_volume").text("");
								  
								  
								  $("#summary_upi_capture_count").text("");
								  $("#summary_upi_capture_volume").text("");
								  $("#summary_upi_pending_count").text("");
								  $("#summary_upi_pending_volume").text("");
								  $("#summary_upi_hard_decline_count").text("");
								  $("#summary_upi_hard_decline_volume").text("");
								  $("#summary_upi_soft_decline_count").text("");
								  $("#summary_upi_soft_decline_volume").text("");
								  
								  
								  $("#summary_cc_dc_capture_count").text("");
								  $("#summary_cc_dc_capture_volume").text("");
								  $("#summary_cc_dc_pending_count").text("");
								  $("#summary_cc_dc_pending_volume").text("");
								  $("#summary_cc_dc_hard_decline_count").text("");
								  $("#summary_cc_dc_hard_decline_volume").text("");
								  $("#summary_cc_dc_soft_decline_count").text("");
								  $("#summary_cc_dc_soft_decline_volume").text("");
								  
								  $("#summary_wallet_capture_count").text("");
								  $("#summary_wallet_capture_volume").text("");
								  $("#summary_wallet_pending_count").text("");
								  $("#summary_wallet_pending_volume").text("");
								  $("#summary_wallet_hard_decline_count").text("");
								  $("#summary_wallet_hard_decline_volume").text("");
								  $("#summary_wallet_soft_decline_count").text("");
								  $("#summary_wallet_soft_decline_volume").text("");
								  
								  $("#summary_overall_capture_count").text("");
								  $("#summary_overall_capture_volume").text("");
								  $("#summary_overall_pending_count").text("");
								  $("#summary_overall_pending_volume").text("");
								  $("#summary_overall_hard_decline_count").text("");
								  $("#summary_overall_hard_decline_volume").text("");
								  $("#summary_overall_soft_decline_count").text("");
								  $("#summary_overall_soft_decline_volume").text("");
					
								  $('#loading').show();
								
								$.post("getTotalTransaction",
										{
										  dateRange : dateRange,
										  merchant : merchant,
										  acquirer : acquirer,
										  paymentMethods : paymentMethods,
										  moptype:moptype
										},
										function(data,status){
										  debugger
										  $('#loading').hide();
										  var obj=JSON.parse(JSON.parse(data));
										  console.log(obj);
										  $("#total_cc_count").text(obj.total_cc_count==null?0:obj.total_cc_count);
										  $("#total_cc_amount").text(obj.total_cc_amount==null?0:obj.total_cc_amount);
										  $("#total_dc_count").text(obj.total_dc_count==null?0:obj.total_dc_count);
										  $("#total_dc_amount").text(obj.total_dc_amount==null?0:obj.total_dc_amount);
										  $("#total_nb_count").text(obj.total_nb_count==null?0:obj.total_nb_count);
										  $("#total_nb_amount").text(obj.total_nb_amount==null?0:obj.total_nb_amount);
										  $("#total_wallet_count").text(obj.total_wallet_count==null?0:obj.total_wallet_count);
										  $("#total_wallet_amount").text(obj.total_wallet_amount==null?0:obj.total_wallet_amount);
										  $("#total_upi_count").text(obj.total_upi_count==null?0:obj.total_upi_count);
										  $("#total_upi_amount").text(obj.total_upi_amount==null?0:obj.total_upi_amount);
										  $("#total_null_count").text(obj.total_null_count==null?0:obj.total_null_count);
										  $("#total_null_amount").text(obj.total_null_amount==null?0:obj.total_null_amount);
										  $("#total_total_count").text(obj.total_cc_count==null?0:obj.total_total_count);
										  $("#total_total_amount").text(obj.total_total_amount==null?0:obj.total_total_amount);
										  
										  $("#capture_cc_count").text(obj.capture_cc_count==null?0:obj.capture_cc_count);
										  $("#capture_cc_amount").text(obj.capture_cc_amount==null?0:obj.capture_cc_amount);
										  $("#capture_dc_count").text(obj.capture_dc_count==null?0:obj.capture_dc_count);
										  $("#capture_dc_amount").text(obj.capture_dc_amount==null?0:obj.capture_dc_amount);
										  $("#capture_nb_count").text(obj.capture_nb_count==null?0:obj.capture_nb_count);
										  $("#capture_nb_amount").text(obj.capture_nb_amount==null?0:obj.capture_nb_amount);
										  $("#capture_wallet_count").text(obj.capture_wallet_count==null?0:obj.capture_wallet_count);
										  $("#capture_wallet_amount").text(obj.capture_wallet_amount==null?0:obj.capture_wallet_amount);
										  $("#capture_upi_count").text(obj.capture_upi_count==null?0:obj.capture_upi_count);
										  $("#capture_upi_amount").text(obj.capture_upi_amount==null?0:obj.capture_upi_amount);
										  $("#capture_null_count").text("0");
										  $("#capture_null_amount").text("0");
										  $("#capture_total_count").text(obj.capture_total_count==null?0:obj.capture_total_count);
										  $("#capture_total_amount").text(obj.capture_total_amount==null?0:obj.capture_total_amount);
										  
										  $("#pending_cc_count").text(obj.pending_cc_count==null?0:obj.pending_cc_count);
										  $("#pending_cc_amount").text(obj.pending_cc_amount==null?0:obj.pending_cc_amount);
										  $("#pending_dc_count").text(obj.pending_dc_count==null?0:obj.pending_dc_count);
										  $("#pending_dc_amount").text(obj.pending_dc_amount==null?0:obj.pending_dc_amount);
										  $("#pending_nb_count").text(obj.pending_nb_count==null?0:obj.pending_nb_count);
										  $("#pending_nb_amount").text(obj.pending_nb_amount==null?0:obj.pending_nb_amount);
										  $("#pending_wallet_count").text(obj.pending_wallet_count==null?0:obj.pending_wallet_count);
										  $("#pending_wallet_amount").text(obj.pending_wallet_amount==null?0:obj.pending_wallet_amount);
										  $("#pending_upi_count").text(obj.pending_upi_count==null?0:obj.pending_upi_count);
										  $("#pending_upi_amount").text(obj.pending_upi_amount==null?0:obj.pending_upi_amount);
										  $("#pending_null_count").text(obj.pending_null_count==null?0:obj.pending_null_count);
										  $("#pending_null_amount").text(obj.pending_null_amount==null?0:obj.pending_null_amount);
										  $("#pending_total_count").text(obj.pending_total_count==null?0:obj.pending_total_count);
										  $("#pending_total_amount").text(obj.pending_total_amount==null?0:obj.pending_total_amount);
										
											
										  $("#harddecline_cc_count").text(obj.harddecline_cc_count==null?0:obj.harddecline_cc_count);
										  $("#harddecline_cc_amount").text(obj.harddecline_cc_amount==null?0:obj.harddecline_cc_amount);
										  $("#harddecline_dc_count").text(obj.harddecline_dc_count==null?0:obj.harddecline_dc_count);
										  $("#harddecline_dc_amount").text(obj.harddecline_dc_amount==null?0:obj.harddecline_dc_amount);
										  $("#harddecline_nb_count").text(obj.harddecline_nb_count==null?0:obj.harddecline_nb_count);
										  $("#harddecline_nb_amount").text(obj.harddecline_nb_amount==null?0:obj.harddecline_nb_amount);
										  $("#harddecline_wallet_count").text(obj.harddecline_wallet_count==null?0:obj.harddecline_wallet_count);
										  $("#harddecline_wallet_amount").text(obj.harddecline_wallet_amount==null?0:obj.harddecline_wallet_amount);
										  $("#harddecline_upi_count").text(obj.harddecline_upi_count==null?0:obj.harddecline_upi_count);
										  $("#harddecline_upi_amount").text(obj.harddecline_upi_amount==null?0:obj.harddecline_upi_amount);
										  $("#harddecline_null_count").text(obj.harddecline_null_count==null?0:obj.harddecline_null_count);
										  $("#harddecline_null_amount").text(obj.harddecline_null_amount==null?0:obj.harddecline_null_amount);
										  $("#harddecline_total_count").text(obj.harddecline_total_count==null?0:obj.harddecline_total_count);
										  $("#harddecline_total_amount").text(obj.harddecline_total_amount==null?0:obj.harddecline_total_amount);
										  
										  
										  $("#softdecline_cc_count").text(obj.softdecline_cc_count==null?0:obj.softdecline_cc_count);
										  $("#softdecline_cc_amount").text(obj.softdecline_cc_amount==null?0:obj.softdecline_cc_amount);
										  $("#softdecline_dc_count").text(obj.softdecline_dc_count==null?0:obj.softdecline_dc_count);
										  $("#softdecline_dc_amount").text(obj.softdecline_dc_amount==null?0:obj.softdecline_dc_amount);
										  $("#softdecline_nb_count").text(obj.softdecline_nb_count==null?0:obj.softdecline_nb_count);
										  $("#softdecline_nb_amount").text(obj.softdecline_nb_amount==null?0:obj.softdecline_nb_amount);
										  $("#softdecline_wallet_count").text(obj.softdecline_wallet_count==null?0:obj.softdecline_wallet_count);
										  $("#softdecline_wallet_amount").text(obj.softdecline_wallet_amount==null?0:obj.softdecline_wallet_amount);
										  $("#softdecline_upi_count").text(obj.softdecline_upi_count==null?0:obj.softdecline_upi_count);
										  $("#softdecline_upi_amount").text(obj.softdecline_upi_amount==null?0:obj.softdecline_upi_amount);
										  $("#softdecline_null_count").text(obj.softdecline_null_count==null?0:obj.softdecline_null_count);
										  $("#softdecline_null_amount").text(obj.softdecline_null_amount==null?0:obj.softdecline_null_amount);
										  $("#softdecline_total_count").text(obj.softdecline_total_count==null?0:obj.softdecline_total_count);
										  $("#softdecline_total_amount").text(obj.softdecline_total_amount==null?0:obj.softdecline_total_amount);
										  
										  $("#summary_nb_capture_count").text(obj.summary_nb_capture_count==null?"0":obj.summary_nb_capture_count);
										  $("#summary_nb_capture_volume").text(obj.summary_nb_capture_volume==null?"0":obj.summary_nb_capture_volume+"%");
										  $("#summary_nb_pending_count").text(obj.summary_nb_pending_count==null?"0":obj.summary_nb_pending_count);
										  $("#summary_nb_pending_volume").text(obj.summary_nb_pending_volume==null?"0":obj.summary_nb_pending_volume+"%");
										  $("#summary_nb_hard_decline_count").text(obj.summary_nb_hard_decline_count==null?"0":obj.summary_nb_hard_decline_count);
										  $("#summary_nb_hard_decline_volume").text(obj.summary_nb_hard_decline_volume==null?"0":obj.summary_nb_hard_decline_volume+"%");
										  $("#summary_nb_soft_decline_count").text(obj.summary_nb_soft_decline_count==null?"0":obj.summary_nb_soft_decline_count);
										  $("#summary_nb_soft_decline_volume").text(obj.summary_nb_soft_decline_volume==null?"0":obj.summary_nb_soft_decline_volume+"%");
										  
										  
										  $("#summary_upi_capture_count").text(obj.summary_upi_capture_count==null?"0":obj.summary_upi_capture_count);
										  $("#summary_upi_capture_volume").text(obj.summary_upi_capture_volume==null?"0":obj.summary_upi_capture_volume+"%");
										  $("#summary_upi_pending_count").text(obj.summary_upi_pending_count==null?"0":obj.summary_upi_pending_count);
										  $("#summary_upi_pending_volume").text(obj.summary_upi_pending_volume==null?"0":obj.summary_upi_pending_volume+"%");
										  $("#summary_upi_hard_decline_count").text(obj.summary_upi_hard_decline_count==null?"0":obj.summary_upi_hard_decline_count);
										  $("#summary_upi_hard_decline_volume").text(obj.summary_upi_hard_decline_volume==null?"0":obj.summary_upi_hard_decline_volume+"%");
										  $("#summary_upi_soft_decline_count").text(obj.summary_upi_soft_decline_count==null?"0":obj.summary_upi_soft_decline_count);
										  $("#summary_upi_soft_decline_volume").text(obj.summary_upi_soft_decline_volume==null?"0":obj.summary_upi_soft_decline_volume+"%");
										  
										  
										  $("#summary_cc_dc_capture_count").text(obj.summary_cc_dc_capture_count==null?"0":obj.summary_cc_dc_capture_count);
										  $("#summary_cc_dc_capture_volume").text(obj.summary_cc_dc_capture_volume==null?"0":obj.summary_cc_dc_capture_volume+"%");
										  $("#summary_cc_dc_pending_count").text(obj.summary_cc_dc_pending_count==null?"0":obj.summary_cc_dc_pending_count);
										  $("#summary_cc_dc_pending_volume").text(obj.summary_cc_dc_pending_volume==null?"0":obj.summary_cc_dc_pending_volume+"%");
										  $("#summary_cc_dc_hard_decline_count").text(obj.summary_cc_dc_hard_decline_count==null?"0":obj.summary_cc_dc_hard_decline_count);
										  $("#summary_cc_dc_hard_decline_volume").text(obj.summary_cc_dc_hard_decline_volume==null?"0":obj.summary_cc_dc_hard_decline_volume+"%");
										  $("#summary_cc_dc_soft_decline_count").text(obj.summary_cc_dc_soft_decline_count==null?"0":obj.summary_cc_dc_soft_decline_count);
										  $("#summary_cc_dc_soft_decline_volume").text(obj.summary_cc_dc_soft_decline_volume==null?"0":obj.summary_cc_dc_soft_decline_volume+"%");
										  
										  $("#summary_wallet_capture_count").text(obj.summary_wallet_capture_count==null?"0":obj.summary_wallet_capture_count);
										  $("#summary_wallet_capture_volume").text(obj.summary_wallet_capture_volume==null?"0":obj.summary_wallet_capture_volume+"%");
										  $("#summary_wallet_pending_count").text(obj.summary_wallet_pending_count==null?"0":obj.summary_wallet_pending_count);
										  $("#summary_wallet_pending_volume").text(obj.summary_wallet_pending_volume==null?"0":obj.summary_wallet_pending_volume+"%");
										  $("#summary_wallet_hard_decline_count").text(obj.summary_wallet_hard_decline_count==null?"0":obj.summary_wallet_hard_decline_count);
										  $("#summary_wallet_hard_decline_volume").text(obj.summary_wallet_hard_decline_volume==null?"0":obj.summary_wallet_hard_decline_volume+"%");
										  $("#summary_wallet_soft_decline_count").text(obj.summary_wallet_soft_decline_count==null?"0":obj.summary_wallet_soft_decline_count);
										  $("#summary_wallet_soft_decline_volume").text(obj.summary_wallet_soft_decline_volume==null?"0":obj.summary_wallet_soft_decline_volume+"%");
										  
										  $("#summary_overall_capture_count").text(obj.summary_overall_capture_count==null?"0":obj.summary_overall_capture_count);
										  $("#summary_overall_capture_volume").text(obj.summary_overall_capture_volume==null?"0":obj.summary_overall_capture_volume+"%");
										  $("#summary_overall_pending_count").text(obj.summary_overall_pending_count==null?"0":obj.summary_overall_pending_count);
										  $("#summary_overall_pending_volume").text(obj.summary_overall_pending_volume==null?"0":obj.summary_overall_pending_volume+"%");
										  $("#summary_overall_hard_decline_count").text(obj.summary_overall_hard_decline_count==null?"0":obj.summary_overall_hard_decline_count);
										  $("#summary_overall_hard_decline_volume").text(obj.summary_overall_hard_decline_volume==null?"0":obj.summary_overall_hard_decline_volume+"%");
										  $("#summary_overall_soft_decline_count").text(obj.summary_overall_soft_decline_count==null?"0":obj.summary_overall_soft_decline_count);
										  $("#summary_overall_soft_decline_volume").text(obj.summary_overall_soft_decline_volume==null?"0":obj.summary_overall_soft_decline_volume+"%");
										});
								});
						});
					</script>

	<script type="text/javascript">
						$(function () {

							var start = moment().subtract(29, 'days').set({ 'hour': 00, 'minute': 00 });
							var end = moment().set({ 'hour': 23, 'minute': 59 });

							function cb(start, end) {
								$('#reportrange input').val(
									start.format('DD/MM/YYYY HH:mm') + ' - '
									+ end.format('DD/MM/YYYY HH:mm'));
							}

							$('#reportrange').daterangepicker(
								{
									timePicker: true,
									timePicker24Hour: true,
									startDate: start,
									endDate: end,
									dateLimit: { days: 30 },

									ranges: {
										'Today': [moment().set({ 'hour': 00, 'minute': 00 }), moment().set({ 'hour': 23, 'minute': 59 })],
										'Yesterday': [moment().subtract(1, 'days').set({ 'hour': 00, 'minute': 00 }),
										moment().subtract(1, 'days').set({ 'hour': 23, 'minute': 59 })],
										'Last 7 Days': [moment().subtract(6, 'days').set({ 'hour': 00, 'minute': 00 }),
										moment().set({ 'hour': 23, 'minute': 59 })],
										'Last 30 Days': [moment().subtract(29, 'days').set({ 'hour': 00, 'minute': 00 }),
										moment().set({ 'hour': 23, 'minute': 59 })],
										'This Month': [moment().startOf('month').set({ 'hour': 00, 'minute': 00 }),
										moment().endOf('month').set({ 'hour': 23, 'minute': 59 })],
										'Last Month': [
											moment().subtract(1, 'month').startOf(
												'month').set({ 'hour': 00, 'minute': 00 }),
											moment().subtract(1, 'month')
												.endOf('month').set({ 'hour': 23, 'minute': 59 })]
									}
								}, cb);

							cb(start, end);

						});
					</script>


	<script
		src="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.js"></script>
	<script src="https://cdn.amcharts.com/lib/5/index.js"></script>
	<script src="https://cdn.amcharts.com/lib/5/xy.js"></script>
	<script src="https://cdn.amcharts.com/lib/5/percent.js"></script>
	<script src="https://cdn.amcharts.com/lib/5/radar.js"></script>
	<script src="https://cdn.amcharts.com/lib/5/themes/Animated.js"></script>
	<script src="https://cdn.amcharts.com/lib/5/map.js"></script>
	<script src="https://cdn.amcharts.com/lib/5/geodata/worldLow.js"></script>
	<script src="https://cdn.amcharts.com/lib/5/geodata/continentsLow.js"></script>
	<script src="https://cdn.amcharts.com/lib/5/geodata/usaLow.js"></script>
	<script
		src="https://cdn.amcharts.com/lib/5/geodata/worldTimeZonesLow.js"></script>
	<script
		src="https://cdn.amcharts.com/lib/5/geodata/worldTimeZoneAreasLow.js"></script>
	<script src="../assets/plugins/custom/datatables/datatables.bundle.js"></script>
	<!--end::Vendors Javascript-->
	<!--begin::Custom Javascript(used by this page)-->
	<script src="../assets/js/widgets.bundle.js"></script>
	<script src="../assets/js/custom/widgets.js"></script>
	<script src="../assets/js/custom/apps/chat/chat.js"></script>
	<script src="../assets/js/custom/utilities/modals/upgrade-plan.js"></script>
	<script src="../assets/js/custom/utilities/modals/create-app.js"></script>
	<script src="../assets/js/custom/utilities/modals/users-search.js"></script>


</body>

</html>