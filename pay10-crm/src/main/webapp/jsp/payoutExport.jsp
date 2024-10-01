<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="/struts-tags" prefix="s" %>
	<%@page import="java.util.Calendar" %>
		<%@page import="java.text.SimpleDateFormat" %>
			<%@page import="java.util.Date" %>
				<%@page import="java.util.Locale" %>
					<%@page import="java.text.NumberFormat" %>
						<%@page import="java.text.DecimalFormat" %>
							<%@page import="java.text.Format" %>


								<html dir="ltr" lang="en-US">

								<head>
									<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
									<title>Transaction Ledger</title>

									<link href="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.css"
										rel="stylesheet" type="text/css" />
									<link href="../assets/plugins/custom/datatables/datatables.bundle.css"
										rel="stylesheet" type="text/css" />
									<link href="../assets/plugins/global/plugins.bundle.css" rel="stylesheet"
										type="text/css" />
									<link href="../assets/css/style.bundle.css" rel="stylesheet" type="text/css" />
									<link href="../assets/css/custom/custom-style.css" rel="stylesheet"
										type="text/css" />
									<!-- <script src="../js/loader/main.js"></script> -->
									<script src="../assets/plugins/global/plugins.bundle.js"></script>
									<script src="../assets/js/scripts.bundle.js"></script>
									<script src="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.js"></script>
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

										< !-- .Acquirer1 input[type="radio"] {
											vertical-align: top;
											float: left;
											margin: 2px 5px 0 0;
										}

										.Acquirer1 label {
											vertical-align: middle;
											display: block;
											font-weight: normal;
										}

										-->.boxtext td div input[type="radio"] {
											vertical-align: top;
											float: left;
											margin: 2px 5px 0 0;
										}

										.boxtext td div label {
											vertical-align: middle;
											display: block;
											font-weight: normal;
										}

										#onus_section .checkbox,
										#offus_section .checkbox {
											margin: 0;
										}

										.checkbox .wwgrp input[type="checkbox"] {
											margin-left: 0;
										}

										.checkbox label .wwgrp input[type="checkbox"] {
											margin-left: -20px;
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

										@media (min-width : 768px) {

											.navbar>.container .navbar-brand,
											.navbar>.container-fluid .navbar-brand {
												margin-left: 0px !important;
											}
										}

										.dropdown-menu>li>a:focus,
										.dropdown-menu>li>a:hover {
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
									<% DecimalFormat d=new DecimalFormat("0.00"); Date d1=new Date(); SimpleDateFormat
										df=new SimpleDateFormat("dd-MM-YYYY"); String currentDate=df.format(d1); %>


										<script type="text/javascript">

										</script>

										<style>
											@media (min-width : 992px) {
												.col-lg-3 {
													max-width: 30% !important;
												}
											}
										</style>

										<script>
											$(document).ready(function () {
												$("#payId").select2();
												//$("#reportType").select2();
												$("#poReportDisplay").show();
											});
										</script>
								</head>

								<body>

									<div class="content d-flex flex-column flex-column-fluid" id="kt_content">
										<!--begin::Toolbar-->
										<div class="toolbar" id="kt_toolbar">
											<!--begin::Container-->
											<div id="kt_toolbar_container" class="container-fluid d-flex flex-stack">
												<!--begin::Page title-->

												<!--end::Page title-->

											</div>
											<!--end::Container-->
										</div>


										<!--end::Toolbar-->
										<!--begin::Post-->
										<div class="post d-flex flex-column-fluid" id="kt_post">
											<!--begin::Container-->
											<div id="kt_content_container" class="container-xxl">


												<!--begin::Page title-->
												<div data-kt-swapper="true" data-kt-swapper-mode="prepend"
													data-kt-swapper-parent="{default: '#kt_content_container', 'lg': '#kt_toolbar_container'}"
													class="page-title d-flex align-items-center me-3 flex-wrap mb-5 mb-lg-0 lh-1">
													<!--begin::Title-->
													<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">
														Transaction Ledger</h1>
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
														<li class="breadcrumb-item text-muted">Search Payments</li>
														<!--end::Item-->
														<!--begin::Item-->
														<li class="breadcrumb-item"><span
																class="bullet bg-gray-200 w-5px h-2px"></span></li>
														<!--end::Item-->
														<!--begin::Item-->
														<li class="breadcrumb-item text-dark">Transaction Ledger</li>
														<!--end::Item-->
													</ul>
													<!--end::Breadcrumb-->
												</div>
												<!--end::Page title-->
												<div class="row my-5">
													<div class="col-12">
														<div class="card">
															<div class="card-body">

																<div class="row">

																	<!-- <div class="col-3">
																		<label
																			class="d-flex align-items-center fs-6 fw-semibold mb-2">Report
																			Type </label>
																		<select name="reportType" id="reportType"
																			class="form-select form-select-solid">
																			<option value="Report Type">Report Type
																			</option>
																			<option value="all">All</option>
																			<option value="po">Pay Out</option>
																			<option value="pg">Pay In</option>
																		</select>

																	</div> -->


																	<s:if
																		test="%{#session.USER.UserType.name() != 'MERCHANT'">
																		<div class="col-3">
																			<label
																				class="d-flex align-items-center fs-6 fw-semibold mb-2">Merchant
																			</label>
																			<s:select name="payId"
																				class="form-select form-select-solid"
																				id="payId" headerValue="All"
																				headerKey="All" list="payIdList" />
																		</div>
																	</s:if>

																	<s:else>
																		<div class="col-3">
																			<label
																				class="d-flex align-items-center fs-6 fw-semibold mb-2">Merchant
																			</label>
																			<s:select name="payId"
																				class="form-select form-select-solid"
																				id="payId" list="payIdList" />
																		</div>
																	</s:else>


																	<!-- Start Change by Pritam Ray -->
																	<div class="col-3">
																		<label
																			class="d-flex align-items-center fs-6 fw-semibold mb-2">Status</label>
																		<select name="status" id="status"
																			class="form-select form-select-solid">
																			<option value="All">All</option>
																			<option value="Request Accepted">Request Accepted</option>
																			<option value="Success">Captured</option>
																			<option value="Failed">Failed</option>
																		</select>
																	</div>

																	<div class="col-3">
																		<label
																			class="d-flex align-items-center fs-6 fw-semibold mb-2">Order
																			Id</label>
																		<input class="form-control form-control-solid"
																			placeholder="Enter Order Id" name="orderId"
																			id="orderId">
																	</div>

																	<div class="col-3">
																		<label
																			class="d-flex align-items-center fs-6 fw-semibold mb-2">PG
																			Ref No</label>
																		<input class="form-control form-control-solid"
																			placeholder="Enter PG Ref No"
																			name="pgRefNum" id="pgRefNum">
																	</div>

																</div>

																<div class="row mt-4">


																	<div class="col-3">
																		<label
																			class="d-flex align-items-center fs-6 fw-semibold mb-2">Account
																			No</label>
																		<input class="form-control form-control-solid"
																			placeholder="Enter Account No"
																			name="accountNo" id="accountNo">
																	</div>
																	<!-- End Change by Pritam Ray -->

																	<div class="col-3">
																		<label
																			class="d-flex align-items-center fs-6 fw-semibold mb-2">
																			<span class="">Date</span>
																		</label>
																		<!--end::Label-->
																		<div
																			class="position-relative d-flex align-items-center">
																			<!--begin::Icon-->
																			<!--begin::Svg Icon | path: icons/duotune/general/gen014.svg-->
																			<span
																				class="svg-icon svg-icon-2 position-absolute mx-4">
																				<svg width="24" height="24"
																					viewBox="0 0 24 24" fill="none"
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
																				placeholder="Select a date"
																				name="dateFrom" id="dateFrom"
																				type="text" readonly="readonly">
																			<!--end::Datepicker-->
																		</div>
																	</div>

																	<div class="col-3">
																		<label
																			class="d-flex align-items-center fs-6 fw-semibold mb-2">
																			<span class="">Date To</span>
																		</label>
																		<!--end::Label-->
																		<div
																			class="position-relative d-flex align-items-center">
																			<!--begin::Icon-->
																			<!--begin::Svg Icon | path: icons/duotune/general/gen014.svg-->
																			<span
																				class="svg-icon svg-icon-2 position-absolute mx-4">
																				<svg width="24" height="24"
																					viewBox="0 0 24 24" fill="none"
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
																				placeholder="Select a date"
																				name="dateTo" id="dateTo" type="text"
																				readonly="readonly">
																			<!--end::Datepicker-->
																		</div>
																	</div>

																	<s:hidden name="token"
																		value="%{#session.customToken}" />
																	<div class="col mt-7" id="submitButtonDiv"
																		style="text-align: left;">
																		<button type="button"
																			class="btn btn-primary submitbtn"
																			id="SubmitButton"
																			onclick="submit()">Submit</button>
																	</div>
																</div>

															</div>
														</div>
													</div>
												</div>
											</div>
										</div>

									</div>


									<div id="kt_content_container" class="container-xxl">
										<div class="row my-5">
											<div class="col-12">
												<div class="card">
													<div class="card-body">
														<!--begin::Input group-->
														<div class="row g-9 mb-8">
															<!--begin::Col-->
															<div class="col-12">

																<div class="row g-9 mb-8">

																	<div id="poReportDisplay" style="display: none;"
																		class="table-responsive dataTables_wrapper dt-bootstrap4 no-footer">
																		<table id="poReport"
																			class="display table table-striped table-row-bordered gy-5 gs-7"
																			style="width: 100%">
																			<thead>
																				<tr
																					class="boxheadingsmall fw-bold fs-6 text-gray-800">
																					<th>PG Ref No</th>
																					<th>Order Id</th>
																					<th>Pay Id</th>
																					<th>Payer Name</th>
																					<th>Payer Phone</th>
																					<th>Txn Type</th>
																					<th>Pay Type</th>
																					<th>Amount</th>
																					<th>Account No</th>
																					<th>Status</th>
																					<th>Response Message</th>
																					<th>Date</th>
																				</tr>
																			</thead>
																		</table>
																	</div>



																	<div id="pgReportDisplay" style="display: none;"
																		class="table-responsive dataTables_wrapper dt-bootstrap4 no-footer">
																		<table id="pgReport"
																			class="display table table-striped table-row-bordered gy-5 gs-7"
																			style="width: 100%">
																			<thead>
																				<tr
																					class="boxheadingsmall fw-bold fs-6 text-gray-800">
																					<th>Txn Id</th>
																					<th>PG Ref No</th>
																					<th>Date</th>
																					<th>Order Id</th>
																					<th>Mop Type</th>
																					<th>Payment Method</th>
																					<th>Txn Type</th>
																					<th>Status</th>
																					<th>Base Amount</th>
																					<th>Total Amount</th>
																					<th>Currency</th>
																					<th>Customer Email</th>
																					<th>Customer Phone</th>
																				</tr>
																			</thead>
																		</table>
																	</div>




																	<div id="pgpoReportDisplay" style="display: none;"
																		class="table-responsive dataTables_wrapper dt-bootstrap4 no-footer">
																		<table id="pgpoReport"
																			class="display table table-striped table-row-bordered gy-5 gs-7"
																			style="width: 100%">
																			<thead>
																				<tr
																					class="boxheadingsmall fw-bold fs-6 text-gray-800">
																					<th>PG Ref Num</th>
																					<th>Order Id</th>
																					<th>Customer Phone</th>
																					<th>Txn Type</th>
																					<th>Channel</th>
																					<th>PayIn Amount</th>
																					<th>PayOut Amount</th>
																					<th>Status</th>
																					<th>Date From</th>
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
													dateFormat: "Y-m-d",
													defaultDate: "today",
													maxDate: new Date()
												});
												$("#kt_datatable_vertical_scroll").DataTable({
													scrollY: true,
													scrollX: true

												});
											});
										});

										// $('#reportType').on('change', function () {
										// 	var data = this.value;
										// 	if (data == "all") {
										// 		$("#pgpoReportDisplay").show();
										// 		$("#pgReportDisplay").hide();
										// 		$("#poReportDisplay").hide();
										// 	} if (data == "po") {
										// 		$("#pgpoReportDisplay").hide();
										// 		$("#pgReportDisplay").hide();
										// 		$("#poReportDisplay").show();
										// 	} if (data == "pg") {
										// 		$("#pgpoReportDisplay").hide();
										// 		$("#poReportDisplay").hide();
										// 		$("#pgReportDisplay").show();
										// 	}
										// });

										function myFunction() {
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

										}

										$('a.toggle-vis').on('click', function (e) {
											debugger
											e.preventDefault();
											table = $('#pgReport').DataTable();
											var column1 = table.column($(this).attr('data-column'));
											column1.visible(!column1.visible());
											if ($(this)[0].classList[1] == 'activecustom') {
												$(this).removeClass('activecustom');
											} else {
												$(this).addClass('activecustom');
											}
										});
									</script>

									<script type="text/javascript">

										function submit() {
											// var reportType = $("#reportType").val();
											// var payId = $("#payId").val();
											// var dateFrom = $("#dateFrom").val();
											// var dateTo = $("#dateTo").val();

											// if (reportType == "Report Type") {
											// 	alert("Please select Report Type");
											// 	return false;
											// }

											// if (reportType == "pg") {
											// 	renderTableForPGReport();
											// }
											// if (reportType == 'po') {
											// 	renderTableForPOReport();
											// }
											// if (reportType == 'all') {
											// 	renderTableForPOPGReport();
											// }

											renderTableForPOReport();

										}

										//************************************PG REPORT BINDING START*****************************************************************
										function renderTableForPGReport() {
											var dateFrom = document.getElementById("dateFrom").value;
											var dateTo = document.getElementById("dateTo").value;
											var transFrom = convert(dateFrom);
											var transTo = convert(dateTo);
											var transFrom1 = new Date(Date.parse(document
												.getElementById("dateFrom").value));
											var transTo1 = new Date(Date.parse(document.getElementById("dateTo").value));
											if (transFrom1 == null || transTo1 == null) {
												alert('Enter date value');
												return false;
											}

											if (transFrom1 > transTo1) {
												$('#loader-wrapper').hide();
												alert('From date must be before the to date');
												$('#dateFrom').focus();
												return false;
											}
											if (transTo1 - transFrom1 > 30 * 86400000) {
												$('#loader-wrapper').hide();
												alert('No. of days can not be more than 30');
												$('#dateFrom').focus();
												return false;
											}

											$("#pgReport").DataTable().destroy();
											var token = document.getElementsByName("token")[0].value;
											var buttonCommon = {
												exportOptions: {
													format: {
														body: function (data, column, row, node) {
															return data;
														}
													}
												}
											};
											$('#pgReport')
												.DataTable(
													{
														dom: 'BTftlpi',
														scrollY: true,
														scrollX: true,

														"ajax": {
															"url": "pgReport",
															"type": "POST",
															"data": function (d) {
																return generatePostDataPG(d);
															}
														},

														"searching": false,
														"ordering": false,
														"processing": true,
														"serverSide": true,
														"paginationType": "full_numbers",
														"lengthMenu": [[10, 25, 50, 100],
														[10, 25, 50, 100]],
														"order": [[2, "desc"]],

														"aoColumns": [
															{
																"mData": "transactionIdString"
															},
															{
																"mData": "pgRefNum"
															},
															{
																"mData": "dateFrom"
															},
															{
																"mData": "orderId"
															},
															{
																"mData": "mopType"
															},
															{
																"mData": "paymentMethods",
																"render": function (data, type,
																	full) {
																	return full['paymentMethods']
																		+ ' ' + '-' + ' '
																		+ full['mopType'];
																},
															}, {
																"mData": "txnType"
															}, {
																"mData": "status"
															}, {
																"mData": "amount",
																"className": "text-class",
																"render": function (data) {
																	return inrFormat(data);
																}
															}, {
																"mData": "totalAmount",
																"render": function (data) {
																	return inrFormat(data);
																}
															}, {
																"mData": "currency"
															}, {
																"mData": "customerEmail"
															}, {
																"mData": "customerPhone"
															},

														]
													});
										}

										function convert(str) {
											var date = new Date(str), mnth = ("0" + (date.getMonth() + 1))
												.slice(-2), day = ("0" + date.getDate()).slice(-2);
											return [day, mnth, date.getFullYear()].join("-");
										}

										function generatePostDataPG(d) {
											var reportType = $("#reportType").val();
											var merchant = $("#payId").val();
											var dateFrom = $("#dateFrom").val();
											var dateTo = $("#dateTo").val();
											var token = document.getElementsByName("token")[0].value;

											dateFrom = convert(dateFrom);
											dateTo = convert(dateTo);

											var obj = {
												reportType: reportType,
												dateFrom: dateFrom,
												dateTo: dateTo,
												acquirer: 'ALL',
												transactionId: '',
												orderId: '',
												customerEmail: '',
												customerPhone: '',
												merchantEmailId: merchant,
												transactionType: 'ALL',
												paymentType: 'ALL',
												status: 'ALL',
												newDespositor: false,
												mopType: 'ALL',
												acquirer: 'ALL',
												currency: 'ALL',
												startTime: '00:00',
												endTime: '23:59',
												draw: d.draw,
												length: d.length,
												start: d.start,
												ipAddress: '',
												totalAmount: '',
												rrn: '',
												token: token,
												"struts.token.name": "token",
												channelName: 'ALL',
												minAmount: '',
												maxAmount: '',
												columnName: '',
												logicalCondition: '',
												searchText: '',
												columnName1: '',
												logicalCondition1: '',
												searchText1: '',
												columnName2: '',
												logicalCondition2: '',
												searchText2: '',

											}
											return obj;
										}

										//************************************PG REPORT BINDING END*****************************************************************

										//************************************PO REPORT BINDING START*****************************************************************
										function renderTableForPOReport() {
											var dateFrom = document.getElementById("dateFrom").value;
											var dateTo = document.getElementById("dateTo").value;
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
												$('#loader-wrapper').hide();
												alert('From date must be before the to date');
												$('#dateFrom').focus();
												return false;
											}
											if (transTo1 - transFrom1 > 30 * 86400000) {
												$('#loader-wrapper').hide();
												alert('No. of days can not be more than 30');
												$('#dateFrom').focus();
												return false;
											}

											$("#poReport").DataTable().destroy();

											var token = document.getElementsByName("token")[0].value;
											var buttonCommon = {
												exportOptions: {
													format: {
														body: function (data, column, row, node) {
															return data;
														}
													}
												}
											};


											$('#poReport').DataTable({
												dom: 'BTftlpi',
												scrollY: true,
												scrollX: true,

												"ajax": {
													"url": "poReport",
													"type": "POST",
													"data": function (d) {
														return generatePostDataPO(d);
													}
												},

												"searching": false,
												"ordering": false,
												"processing": true,
												"serverSide": true,
												"paginationType": "full_numbers",
												"lengthMenu": [[10, 25, 50, 100],
												[10, 25, 50, 100]],
												"order": [[2, "desc"]],

												"aoColumns": [{
													"mData": "pgRefNum"
												}, {
													"mData": "orderId"
												}, {
													"mData": "payId"
												}, {
													"mData": "payerName"
												}, {
													"mData": "customerPhone"
												}, {
													"mData": "txnType"
												}, {
													"mData": "mopType"
												}, {
													"mData": "amount",
													"className": "text-class",
													"render": function (data) {
														return inrFormat(data);
													}
												}, {
													"mData": "accountNo"
												},
												{
													"mData": "status"
												}, {
													"mData": "responseMessage"
												}, {
													"mData": "dateFrom"
												},

												]

											});

										}

										function generatePostDataPO(d) {
											var reportType = $("#reportType").val();
											var merchant = $("#payId").val();
											var dateFrom = $("#dateFrom").val();
											var dateTo = $("#dateTo").val();

											// Change By Pritam Ray
											var status = $("#status").val();
											var orderId = $("#orderId").val();
											var pgRefNum = $("#pgRefNum").val();
											var accountNo = $("#accountNo").val();

											var token = document.getElementsByName("token")[0].value;

											dateFrom = convert(dateFrom);
											dateTo = convert(dateTo);

											var obj = {
												reportType: reportType,
												dateFrom: dateFrom,
												dateTo: dateTo,
												merchantEmailId: merchant,
												startTime: '00:00',
												endTime: '23:59',
												draw: d.draw,
												length: d.length,
												start: d.start,
												token: token,
												"struts.token.name": "token",
												status: status,
												orderId: orderId,
												pgRefNum: pgRefNum,
												accountNo: accountNo
											}

											console.log("@@@ GeneratePostDataPO PayoutExportReport ::: ", obj);

											return obj;

										}

										//************************************PO REPORT BINDING END*****************************************************************

										//************************************POPG REPORT BINDING START*************************************************************

										function renderTableForPOPGReport() {
											var dateFrom = document.getElementById("dateFrom").value;
											var dateTo = document.getElementById("dateTo").value;
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
												$('#loader-wrapper').hide();
												alert('From date must be before the to date');
												$('#dateFrom').focus();
												return false;
											}
											if (transTo1 - transFrom1 > 30 * 86400000) {
												$('#loader-wrapper').hide();
												alert('No. of days can not be more than 30');
												$('#dateFrom').focus();
												return false;
											}

											$("#pgpoReport").DataTable().destroy();

											var token = document.getElementsByName("token")[0].value;
											var buttonCommon = {
												exportOptions: {
													format: {
														body: function (data, column, row, node) {
															return data;
														}
													}
												}
											};


											$('#pgpoReport').DataTable({
												dom: 'BTftlpi',
												scrollY: true,
												scrollX: true,

												"ajax": {
													"url": "popgReport",
													"type": "POST",
													"data": function (d) {
														return generatePostDataPOPG(d);
													}
												},

												"searching": false,
												"ordering": false,
												"processing": true,
												"serverSide": true,
												"paginationType": "full_numbers",
												"lengthMenu": [[10, 25, 50, 100],
												[10, 25, 50, 100]],
												"order": [[2, "desc"]],

												"aoColumns": [{
													"mData": "pgRefNum"
												}, {
													"mData": "orderId"
												}, {
													"mData": "customerphone"
												}, {
													"mData": "txnType"
												}, {
													"mData": "mopType"
												}, {
													"mData": "payInAmount",
													"className": "text-class",
													"render": function (data) {
														return inrFormat(data);
													}
												}, {
													"mData": "payOutAmount",
													"className": "text-class",
													"render": function (data) {
														return inrFormat(data);
													}
												}, {
													"mData": "status"
												}, {
													"mData": "dateFrom"
												}
												]

											});


										}

										function generatePostDataPOPG(d) {
											var reportType = $("#reportType").val();
											var merchant = $("#payId").val();
											var dateFrom = $("#dateFrom").val();
											var dateTo = $("#dateTo").val();
											var token = document.getElementsByName("token")[0].value;

											dateFrom = convert(dateFrom);
											dateTo = convert(dateTo);

											var obj = {
												reportType: reportType,
												dateFrom: dateFrom,
												dateTo: dateTo,
												merchantEmailId: merchant,
												startTime: '00:00',
												endTime: '23:59',
												draw: d.draw,
												length: d.length,
												start: d.start,
												token: token,
												"struts.token.name": "token",
												acquirer: 'ALL',
												transactionId: '',
												orderId: '',
												customerEmail: '',
												customerPhone: '',
												transactionType: 'ALL',
												paymentType: 'ALL',
												status: 'ALL',
												newDespositor: false,
												mopType: 'ALL',
												currency: 'ALL',
												ipAddress: '',
												totalAmount: '',
												rrn: '',
												channelName: 'ALL',
												minAmount: '',
												maxAmount: '',
												columnName: '',
												logicalCondition: '',
												searchText: '',
												columnName1: '',
												logicalCondition1: '',
												searchText1: '',
												columnName2: '',
												logicalCondition2: '',
												searchText2: '',
											}
											return obj;
										}

										//************************************POPG REPORT BINDING END***************************************************************
									</script>
								</body>

								</html>