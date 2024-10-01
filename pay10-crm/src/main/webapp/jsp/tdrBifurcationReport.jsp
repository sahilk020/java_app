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
									<title>TDR Bifurcation Report</title>
									<link href="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.css"
										rel="stylesheet" type="text/css" />
									<link href="../assets/plugins/custom/datatables/datatables.bundle.css"
										rel="stylesheet" type="text/css" />
									<link href="../assets/plugins/global/plugins.bundle.css" rel="stylesheet"
										type="text/css" />
									<link href="../assets/css/style.bundle.css" rel="stylesheet" type="text/css" />
									<link href="../assets/css/custom/custom-style.css" rel="stylesheet"
										type="text/css" />
									<script src="../js/loader/main.js"></script>
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
									<link href="../css/select2.min.css" rel="stylesheet" />
									<script src="../js/jquery.select2.js" type="text/javascript"></script>


									<style type="text/css">
										.dt-buttons {
											margin-top: 35px !important;
										}

										.svg-icon {
											margin-top: 1vh !important;
										}
									</style>
									<% DecimalFormat d=new DecimalFormat("0.00"); Date d1=new Date(); SimpleDateFormat
										df=new SimpleDateFormat("dd-MM-YYYY"); String currentDate=df.format(d1); %>

										<style>
											.dt-buttons {
												display: none;
											}
										</style>
										<script>
											function sumbitButton() {
												var dateFrom = document.getElementById("dateFrom").value;
												var dateTo = document.getElementById("dateTo").value;

												settlementDateTo = document.getElementById("settlementDateTo").value;
												settlementDateFrom = document.getElementById("settlementDateFrom").value;

												var flag = false;
												var flag1 = false;
												if (dateFrom != '' || dateTo != '') {
													flag = true;
												}
												if (settlementDateTo != '' || settlementDateFrom != '') {
													flag1 = true;
												}

												if (flag == true && flag1 == true) {
													alert("You Cannot provide Transaction Date as well as Settlement Date");
													return false;
												}

												if (dateFrom == '' && dateTo == '' && settlementDateTo == ''
													&& settlementDateFrom == '') {
													alert("Please Select Transaction Date or Settlement Date");
													return false;
												}

												if (flag == true) {

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
														var tableObj = $('#example');
														var table = tableObj.DataTable();
														table.ajax.reload();
													}
												} else {
													var status = $("#status").val();

													if (status != "Settled") {
														alert("Please Select Status As Settled");
														return false;
													}

													var transFrom1 = new Date(Date.parse(document
														.getElementById("settlementDateFrom").value));
													var transTo1 = new Date(Date.parse(document
														.getElementById("settlementDateTo").value));
													if (transFrom1 == null || transTo1 == null) {
														alert('Enter date value');
														return false;
													}

													if (transFrom1 > transTo1) {
														alert('From date must be before the to date');
														$('#loader-wrapper').hide();
														$('#settlementDateFrom').focus();
														return false;
													}
													if (transTo1 - transFrom1 > 31 * 86400000) {
														alert('No. of days can not be more than 31');
														$('#loader-wrapper').hide();
														$('#transTo1').focus();
														return false;
													} else {
														var tableObj = $('#example');
														var table = tableObj.DataTable();
														table.ajax.reload();
													}
												}

											}
										</script>

										<script type="text/javascript">
											var value = $("#colorPattern td").text();
											if (value < 0) {
												$("#colorPattern td").addClass("red");
											}
										</script>
										<script type="text/javascript">
											$(document).ready(function () {
												$("#merchant").select2();
											});
										</script>
										<style>
											@media (min-width : 992px) {
												.col-lg-3 {
													max-width: 30% !important;
												}
											}
										</style>
								</head>

								<body>



									<!--begin::Toolbar-->
									<div class="toolbar" id="kt_toolbar">
										<!--begin::Container-->
										<div id="kt_toolbar_container" class="container-fluid d-flex flex-stack">
											<!--begin::Page title-->
											<div data-kt-swapper="true" data-kt-swapper-mode="prepend"
												data-kt-swapper-parent="{default: '#kt_content_container', 'lg': '#kt_toolbar_container'}"
												class="page-title d-flex align-items-center me-3 flex-wrap mb-5 mb-lg-0 lh-1">
												<!--begin::Title-->
												<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">TDR
													Bifurcation Report</h1>
												<!--end::Title-->
												<!--begin::Separator-->
												<span class="h-20px border-gray-200 border-start mx-4"></span>
												<!--end::Separator-->
												<!--begin::Breadcrumb-->
												<ul class="breadcrumb breadcrumb-separatorless fw-semibold fs-7 my-1">
													<!--begin::Item-->
													<li class="breadcrumb-item text-muted"><a href="home"
															class="text-muted text-hover-primary">Dashboard</a></li>
													<!--end::Item-->
													<!--begin::Item-->
													<li class="breadcrumb-item"><span
															class="bullet bg-gray-200 w-5px h-2px"></span></li>
													<!--end::Item-->
													<!--begin::Item-->
													<li class="breadcrumb-item text-muted">TDR Report</li>
													<!--end::Item-->
													<!--begin::Item-->
													<li class="breadcrumb-item"><span
															class="bullet bg-gray-200 w-5px h-2px"></span></li>
													<!--end::Item-->
													<!--begin::Item-->
													<li class="breadcrumb-item text-dark">TDR Bifurcation Report</li>
													<!--end::Item-->
												</ul>
												<!--end::Breadcrumb-->
											</div>
											<!--end::Page title-->

										</div>
										<!--end::Container-->
									</div>


									<div style="overflow: auto !important;">

										<div id="kt_content_container" class="container-xxl">
											<div class="row my-5">
												<div class="col">
													<div class="card">
														<div class="card-body">
															<!--begin::Input group-->
															<div class="row g-9 mb-8">

																<!-- <div class="card-header card-header-rose card-header-text">
					<div class="card-text">
						<h4 class="card-title">TDR Bifurcation Report</h4>
					</div>
				</div> -->



																<div class="card-body ">

																	<form id="IDTDRBifurcationReport">

																		<div class="row my-3 align-items-center">
																			<div class="col-lg-3 my-2">
																				<label
																					class="d-flex align-items-center fs-6 fw-bold mb-2">
																					<span
																						class="required">Merchant</span>
																				</label>
																				<s:select name="merchant" id="merchant"
																					value="merchant" headerKey="All"
																					class="form-select form-select-solid"
																					headerValue="ALL"
																					list="merchantList" listKey="payId"
																					listValue="businessName"
																					autocomplete="off" />

																			</div>
																			<div class="col-lg-3 my-2">
																				<label
																					class="d-flex align-items-center fs-6 fw-bold mb-2">
																					<span
																						class="required">Acquirer</span>
																				</label>
																				<s:select headerKey="ALL"
																					headerValue="ALL"
																					list="@com.pay10.commons.util.AcquirerTypeUI@values()"
																					id="acquirer"
																					class="form-select form-select-solid adminMerchants"
																					name="acquirer" value="acquirer"
																					listValue="name" listKey="code" />
																			</div>
																			<div class="col-lg-3 my-2">
																				<label
																					class="d-flex align-items-center fs-6 fw-bold mb-2">
																					<span class="required">Transaction
																						Date From</span>
																				</label>
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
																					type="text">
																				<!--end::Datepicker-->
																			</div>

																			<div class="col-lg-3 my-2">
																				<label
																					class="d-flex align-items-center fs-6 fw-bold mb-2">
																					<span class="required">Transaction
																						Date To</span>
																				</label>
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
																					name="dateTo" id="dateTo"
																					type="text">
																				<!--end::Datepicker-->
																			</div>




																		</div>
																		<div class="row my-3 align-items-center">


																			<div class="col-lg-3 my-2">
																				<label
																					class="d-flex align-items-center fs-6 fw-bold mb-2">
																					<span class="required">Settlement
																						Date From</span>
																				</label>
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
																					name="settlementDateFrom"
																					id="settlementDateFrom" type="text">
																				<!--end::Datepicker-->
																			</div>

																			<div class="col-lg-3 my-2">
																				<label
																					class="d-flex align-items-center fs-6 fw-bold mb-2">
																					<span class="required">Settlement
																						Date To</span>
																				</label>
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
																					name="settlementDateTo"
																					id="settlementDateTo" type="text">
																				<!--end::Datepicker-->
																			</div>
																			<div class="col-lg-3 my-2">
																				<label
																					class="d-flex align-items-center fs-6 fw-bold mb-2">
																					<span class="required">STATUS</span>
																				</label>
																				<s:select name="status"
																					class="form-select form-select-solid adminMerchants"
																					id="status" headerKey="All"
																					headerValue="ALL"
																					list="@com.pay10.commons.util.StatusTypeCustom@values()"
																					listKey="name" listValue="code"
																					autocomplete="off" />
																			</div>
																			<div class="col-lg-1 my-2">
																				<label
																					class="d-flex align-items-center fs-6 fw-bold mb-2">
																					<span class="">&nbsp;</span>
																				</label>
																				<button type="button"
																					class="btn btn-primary"
																					id="SubmitButton"
																					onclick="sumbitButton()">Submit</button>
																			</div>
																			<!-- 																			<div class="col-lg-1 my-2"> -->
																			<!-- 																				<label class="d-flex align-items-center fs-6 fw-bold mb-2"> -->
																			<%-- <span class="">&nbsp;</span> --%>
																				<!-- 																				</label> -->
																				<!-- 																				<button type="button" -->
																				<!-- 																					class="btn btn-primary" -->
																				<!-- 																					id="SubmitButton" onclick="downloadTDRBifurcation()">Download</button> -->
																				<!-- 																			</div> -->
																		</div>
																	</form>
																</div>

															</div>
														</div>
													</div>
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

																<div class="row g-9 mb-8 justify-content-end">

																	<div class="col-lg-4 col-sm-12 col-md-6">
																		<select name="currency" data-control="select2"
																			data-placeholder="Actions" id="actions11"
																			class="form-select form-select-solid actions"
																			data-hide-search="true"
																			onchange="myFunction();">
																			<option value="">Actions</option>
																			<option value="copy">Copy</option>
																			<option value="csv">CSV</option>
																			<!-- <option value="pdf">PDF</option> -->
																		</select>
																	</div>
																	<div class="col-lg-4 col-sm-12 col-md-6">
																		<div class="dropdown1">
																			<button
																				class="form-select form-select-solid actions dropbtn1">Customize
																				Columns</button>
																			<div class="dropdown-content1">
                                                                                <a class="toggle-vis" data-column="0">TxnId</a>
                                                                                <a class="toggle-vis" data-column="1">PgRefNo</a>
                                                                                <a class="toggle-vis" data-column="2">Merchant Name</a>
                                                                                <a class="toggle-vis" data-column="3">Acquirer</a>
                                                                                <a class="toggle-vis" data-column="4">Date</a>
                                                                                <a class="toggle-vis" data-column="5">Settlement Date</a>
                                                                                <a class="toggle-vis" data-column="6">OrderId</a>
                                                                                <a class="toggle-vis" data-column="7">Payment Method</a>
                                                                                <a class="toggle-vis" data-column="8">Status</a>
                                                                                <a class="toggle-vis" data-column="9">Transaction Region</a>
                                                                                <a class="toggle-vis" data-column="10">Base Amount</a>
                                                                                <a class="toggle-vis" data-column="11">Total Amount</a>
                                                                                <a class="toggle-vis" data-column="12">ACQ ID</a>
                                                                                <a class="toggle-vis" data-column="13">RRN</a>
                                                                                <a class="toggle-vis" data-column="14">Mop Type</a>
                                                                                <a class="toggle-vis" data-column="15">Merchant Preference</a>
                                                                                <a class="toggle-vis" data-column="16">Merchant TDR</a>
                                                                                <a class="toggle-vis" data-column="17">Merchant Min. Tdr. Amount</a>
                                                                                <a class="toggle-vis" data-column="18">Merchant Max. Tdr. Amount</a>
                                                                                <a class="toggle-vis" data-column="19">Bank Preference</a>
                                                                                <a class="toggle-vis" data-column="20">Bank TDR</a>
                                                                                <a class="toggle-vis" data-column="21">Bank Min. Tdr. Amount</a>
                                                                                <a class="toggle-vis" data-column="22">Bank Max. Tdr. Amount</a>
                                                                                <a class="toggle-vis" data-column="23">Bank Tdr. In AMOUNT</a>
                                                                                <a class="toggle-vis" data-column="24">Bank Tax In Amount</a>
                                                                                <a class="toggle-vis" data-column="25">Pg Tdr In Amount</a>
                                                                                <a class="toggle-vis" data-column="26">Pg Tax In Amount</a>
                                                                                <a class="toggle-vis" data-column="27">Surcharge Flag</a>
                                                                                <a class="toggle-vis" data-column="28">Amount Payable To Merchant</a>
                                                                                <a class="toggle-vis" data-column="29">Amount Received in Nodal</a>
                                                                                <a class="toggle-vis" data-column="30">Amount Received Nodal Bank</a>
                                                                                <a class="toggle-vis" data-column="31">Settlement TAT</a>
                                                                                <a class="toggle-vis" data-column="32">Account Holder Name</a>
                                                                                <a class="toggle-vis" data-column="33">Account Number</a>
                                                                                <a class="toggle-vis" data-column="34">IFSC Code</a>
                                                                                <a class="toggle-vis" data-column="35">Transaction Identifiers</a>
                                                                                <a class="toggle-vis" data-column="36">Liability Hold Remarks</a>
                                                                                <a class="toggle-vis" data-column="37">Liability Release Remarks</a>
                                                                                <a class="toggle-vis" data-column="38">UTR Number</a>
                                                                                <a class="toggle-vis" data-column="39">Settlement Period</a>
																			</div>
																		</div>
																	</div>

																	<div class="row g-9 mb-8">
																		<div>
																			<table id="example"
																				class="table table-striped table-row-bordered gy-5 gs-7 "
																				style="width: 100%">
																				<thead>
																					<tr
																						class="boxheadingsmall fw-bold fs-6 text-gray-800">
																						<th>TxnId</th>
																						<th>PgRefNo</th>
																						<th>Merchant Name</th>
																						<th>Acquirer</th>
																						<th>Date</th>
																						<th>Settlement Date</th>
																						<th>OrderId</th>
																						<th>Payment Method</th>
																						<th>Status</th>
																						<th>Transaction Region</th>
																						<th>Base Amount</th>
																						<th>Total Amount</th>
																						<th>ACQ ID</th>
																						<th>RRN</th>
																						<th>Mop Type</th>
																						<th>Merchant Preference</th>
																						<th>Merchant TDR</th>
																						<th>Merchant Min. Tdr. Amount</th>
																						<th>Merchant Max. Tdr. Amount</th>
																						<th>Bank Preference</th>
																						<th>Bank TDR</th>
																						<th>Bank Min. Tdr. Amount</th>
																						<th>Bank Max. Tdr. Amount</th>
																						<th>Bank Tdr. In AMOUNT</th>
																						<th>Bank Tax In Amount</th>
																						<th>Pg Tdr In Amount</th>
																						<th>Pg Tax In Amount</th>
																						<th>Surcharge Flag</th>
																						<th>Amount Payable To Merchant</th>
																						<th>Amount Received in Nodal</th>
																						<th>Amount Received Nodal Bank</th>
																						<th>Settlement TAT</th>
																						<th>Account Holder Name</th>
																						<th>Account Number</th>
																						<th>IFSC Code</th>
																						<th>Transaction Identifiers</th>
																						<th>Liability Hold Remarks</th>
																						<th>Liability Release Remarks</th>
																						<th>UTR Number</th>
																						<th>Settlement Period</th>
																					</tr>

																				</thead>


																				<tfoot>
																					<tr
																						class="fw-bold fs-6 text-gray-800">
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
																						<!-- <th class="min-w-90px"></th> -->
																						<!-- <th class="min-w-90px"></th> -->
																						<!-- <th class="min-w-90px"></th>
																						<th class="min-w-90px"></th>
																						<th class="min-w-90px"></th> -->
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

										</div>


										<script src="../assets/plugins/custom/datatables/datatables.bundle.js"></script>


										<script type="text/javascript">
											$("#dateFrom").flatpickr({
												maxDate: new Date(),
												dateFormat: 'Y-m-d',

											});
											$("#dateTo").flatpickr({
												maxDate: new Date(),
												dateFormat: 'Y-m-d',

											});
											$("#settlementDateTo").flatpickr({
												maxDate: new Date(),
												dateFormat: 'Y-m-d',

											});
											$("#settlementDateFrom").flatpickr({
												maxDate: new Date(),
												dateFormat: 'Y-m-d',

											});
											$('a.toggle-vis').on('click', function (e) {
												debugger
												e.preventDefault();
												table = $('#example').DataTable();
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

												// document.querySelector('.buttons-excel').click();
												// document.querySelector('.buttons-print').click();

											}


											function generatePostData(d) {
												debugger
												var token = document.getElementsByName("token")[0].value;

												var merchant = document.getElementById("merchant").value;
												var acquirer = document.getElementById("acquirer").value;

												var dateFrom = document.getElementById("dateFrom").value;
												var dateTo = document.getElementById("dateTo").value;

												var settlementDateFrom = document.getElementById("settlementDateFrom").value;
												var settlementDateTo = document.getElementById("settlementDateTo").value;

												var status = document.getElementById("status").value;


												var obj = {
													merchant: merchant,
													acquirer: acquirer,
													dateFrom: dateFrom,
													dateTo: dateTo,
													settlementDateFrom: settlementDateFrom,
													settlementDateTo: settlementDateTo,
													status: status,
													draw: d.draw,
													length: d.length,
													start: d.start,
													token: token,
													"struts.token.name": "token",
												};

												return obj;
											}


											debugger
											$('#example').dataTable(

												{

													"columnDefs": [{
														className: "dt-body-right",
														"targets": [1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
															11, 12, 13, 14, 15]
													}],

													dom: 'Brtipl',
													buttons: [
														{
															extend: 'print',
															exportOptions: {
																columns: ':visible'
															}
														},
														{
															extend: 'pdfHtml5',
															orientation: 'landscape',
															pageSize: 'legal',
															//footer : true,
															title: 'Search Transactions',
															exportOptions: {
																columns: [':visible']
															},
															customize: function (doc) {
																// doc.defaultStyle.alignment = 'center';
																// doc.styles.tableHeader.alignment = 'center';
																// doc.defaultStyle.fontSize = 8;
															}
														},
														{
															extend: 'copy',
															exportOptions: {
																columns: [0, 1, 2, 3, 4, 5, 6, 7,
																	8, 9, 10, 11, 12, 13, 14,
																	15, 16, 17, 18, 19, 20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39]
															}
														},
														{
															extend: 'csv',
															exportOptions: {
																columns: [0, 1, 2, 3, 4, 5, 6, 7,
																	8, 9, 10, 11, 12, 13, 14,
																	15, 16, 17, 18, 19, 20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39]
															}
														},
														{
															extend: 'pdf',
															exportOptions: {
																columns: [0, 1, 2, 3, 4, 5, 6, 7,
																	8, 9, 10, 11, 12, 13, 14,
																	15, 16, 17, 18, 19, 20, 21,
																	22]
															}
														}, 'colvis', 'excel', 'print',],
													scrollY: true,
													scrollX: true,
													searchDelay: 500,
													processing: false,
													destroy: true,
													serverSide: true,
													order: [[5, 'desc']],
													stateSave: true,

													"ajax": {

														"url": "GetTDRBifurcationReport",
														"type": "POST",
														"timeout": 0,
														"data": function (d) {
															return generatePostData(d);
														}
													},
													"fnDrawCallback": function () {
														$("#submit").removeAttr("disabled");
														$('#loader-wrapper').hide();
													},
													"searching": false,
													"ordering": false,
													"processing": true,
													"serverSide": true,
													"paginationType": "full_numbers",
													"lengthMenu": [[10, 25, 50, 100],
													[10, 25, 50, 100]],
													"order": [[2, "desc"]],

													"columnDefs": [{
														"type": "html-num-fmt",
														"targets": 4,
														"orderable": true,
														"targets": [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
															11, 12, 13, 14, 15, 16, 17]
													}],

													"columns": [
														{
															"data": "txnId",
															"className": "txnId my_class1 text-class",
															"width": "2%"
														}, {
															"data": "pgRefNo",
															"className": "payId text-class"

														}, {
															"data": "merchantName",
															"className": "text-class"
														}, {
															"data": "acquirer",
															"className": "text-class"
														}, {
															"data": "date",
															"className": "orderId text-class"
														}, {
															"data": "settlementDate",
															"className": "orderId text-class"
														}, {
															"data": "orderId",
															"className": "mopType text-class"
														}, {
															"data": "paymentMethod",
															"className": "text-class"
														}, {
															"data": "status",
															"className": "status text-class"
														}, {
															"data": "transactionRegion",
															"className": "text-class",
														}, {
															"data": "baseAmount",
															"className": "text-class",

															render: function (data) {
																if (data < 0) {
																	return '<span style="color:red;">' + data + '</span>';
																}

																else {

																	return ' <span style="color:black;">' + data + '</span>';

																};
															}
														},
														{
															"data": "totalAmount",
															"className": "text-class",

															render: function (data) {
																if (data < 0) {
																	return '<span style="color:red;">' + data + '</span>';
																}

																else {

																	return ' <span style="color:black;">' + data + '</span>';

																};
															}

														}, {
															"data": "acqId",
															"className": "text-class"
														}, {
															"data": "RRN",
															"className": "text-class"
														},{
															"data": "mopType",
															"className": "text-class"
														}, {
															"data": "merchantPreference",
															"className": "text-class"
														}, {
															"data": "merchantTDR",
															"className": "text-class",

															render: function (data) {
																if (data < 0) {
																	return '<span style="color:red;">' + data + '</span>';
																}

																else {

																	return ' <span style="color:black;">' + data + '</span>';

																};
															}

														}, {
															"data": "merchantMinTdramount",
															"className": "text-class",

															render: function (data) {
																if (data < 0) {
																	return '<span style="color:red;">' + data + '</span>';
																}

																else {

																	return ' <span style="color:black;">' + data + '</span>';

																};
															}
														}, {
															"data": "merchantMaxTdramount",
															"className": "text-class",

															render: function (data) {
																if (data < 0) {
																	return '<span style="color:red;">' + data + '</span>';
																}

																else {

																	return ' <span style="color:black;">' + data + '</span>';

																};
															}
														}, {
															"data": "bankPreference",
															"className": "text-class"
														}, {
															"data": "bankTDR",
															"className": "text-class",

															render: function (data) {
																if (data < 0) {
																	return '<span style="color:red;">' + data + '</span>';
																}

																else {

																	return ' <span style="color:black;">' + data + '</span>';

																};
															}
														}, {
															"data": "bankMinTdrAmount",
															"className": "text-class",

															render: function (data) {
																if (data < 0) {
																	return '<span style="color:red;">' + data + '</span>';
																}

																else {

																	return ' <span style="color:black;">' + data + '</span>';

																};
															}
														}, {
															"data": "bankMaxTdrAmount",
															"className": "text-class",

															render: function (data) {
																if (data < 0) {
																	return '<span style="color:red;">' + data + '</span>';
																}

																else {

																	return ' <span style="color:black;">' + data + '</span>';

																};
															}
														}, {
															"data": "bankTdrInAMOUNT",
															"className": "text-class",

															render: function (data) {
																if (data < 0) {
																	return '<span style="color:red;">' + data + '</span>';
																}

																else {

																	return ' <span style="color:black;">' + data + '</span>';

																};
															}
														}, {
															"data": "bankGstInAmount",
															"className": "text-class",

															render: function (data) {
																if (data < 0) {
																	return '<span style="color:red;">' + data + '</span>';
																}

																else {

																	return ' <span style="color:black;">' + data + '</span>';

																};
															}
														}, {
															"data": "pgTdrInAmount",
															"className": "text-class",

															render: function (data) {
																if (data < 0) {
																	return '<span style="color:red;">' + data + '</span>';
																}

																else {

																	return ' <span style="color:black;">' + data + '</span>';

																};
															}

														}, {
															"data": "pgGstInAmount",
															"className": "text-class",

															render: function (data) {
																if (data < 0) {
																	return '<span style="color:red;">' + data + '</span>';
																}

																else {

																	return ' <span style="color:black;">' + data + '</span>';

																};
															}

														},
														// {
														// 	"data": "igst18",
														// 	"className": "text-class",

														// 	render: function (data) {
														// 		if (data < 0) {
														// 			return '<span style="color:red;">' + data + '</span>';
														// 		}

														// 		else {

														// 			return ' <span style="color:black;">' + data + '</span>';

														// 		};
														// 	}
														// },
														{
															"data": "surchargeFlag",
															"className": "text-class"
														}, {
															"data": "amountPaybleToMerchant",
															"className": "text-class",

															render: function (data) {
																if (data < 0) {
																	return '<span style="color:red;">' + data + '</span>';
																}

																else {

																	return ' <span style="color:black;">' + data + '</span>';

																};
															}
														}, {
															"data": "amountreceivedInNodal",
															"className": "text-class",

															render: function (data) {
																if (data < 0) {
																	return '<span style="color:red;">' + data + '</span>';
																}

																else {

																	return ' <span style="color:black;">' + data + '</span>';

																};
															}
														}, {
															"data": "amountReceivedNodalBank",
															"className": "text-class"
														}, {
															"data": "settlementTat",
															"className": "text-class",

														}, {
															"data": "accountHolderName",
															"className": "text-class"
														}, {
															"data": "accountNumber",
															"className": "text-class"
														}, {
															"data": "ifscCode",
															"className": "text-class"
														}, {
															"data": "transactionIdentifer",
															"className": "text-class"
														}, {
															"data": "liabilityHoldRemakrs",
															"className": "text-class"
														}, {
															"data": "liabilityReleaseRemakrs",
															"className": "text-class"
														}, {
															"data": "utrNumber",
															"className": "text-class"
														}, {
															"data": "settlementPeriod",
															"className": "text-class"
														}
													]

												});


										</script>
								</body>

								</html>