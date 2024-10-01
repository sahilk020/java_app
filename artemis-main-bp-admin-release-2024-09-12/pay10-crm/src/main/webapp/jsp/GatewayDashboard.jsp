<%@page import="java.util.ArrayList" %>
	<%@page import="java.util.Collections" %>
		<%@page import="java.util.Arrays" %>
			<%@page import="com.pay10.commons.util.AcquirerTypeUI" %>
				<%@page import="com.pay10.crm.actionBeans.GateWayDashboardBean" %>
					<%@page import="java.util.List" %>
						<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"
							%>
							<!DOCTYPE html
								PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
							<%@ taglib uri="/struts-tags" prefix="s" %>
								<html dir="ltr" lang="en-US">

								<head>
									<title>Gateway Dashboard</title>
									<link rel="shortcut icon" href="assets/media/images/paylogo.svg" />
									<!--begin::Fonts-->
									<link rel="stylesheet"
										href="https://fonts.googleapis.com/css?family=Inter:300,400,500,600,700" />
									<!--end::Fonts-->
									<!--begin::Vendor Stylesheets(used by this page)-->
									<link href="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.css"
										rel="stylesheet" type="text/css" />
									<link href="../assets/plugins/custom/datatables/datatables.bundle.css"
										rel="stylesheet" type="text/css" />
									<!--end::Vendor Stylesheets-->
									<!--begin::Global Stylesheets Bundle(used by all pages)-->
									<link href="../assets/plugins/global/plugins.bundle.css" rel="stylesheet"
										type="text/css" />
									<link href="../assets/css/style.bundle.css" rel="stylesheet" type="text/css" />
									<link href="../assets/css/custom/custom-style.css" rel="stylesheet"
										type="text/css" />

									<script src="../assets/plugins/global/plugins.bundle.js"></script>
									<script src="../assets/js/scripts.bundle.js"></script>
									<link href="../css/select2.min.css" rel="stylesheet" />
									<script src="../js/jquery.select2.js" type="text/javascript"></script>
									<!-- <style>
.leftdiv {
	display: none;
}

.card-body div {
	/* text-align: center; */
	padding-left: 19px;
	font-size: 16px;
	color: black;
	font-weight: bolder;
	border: 2px solid #45758f;
	border-radius: 7px;
	margin-bottom: 10px;
	font-size: 16px;
	padding-right: 19px !important;
}

.headstyle {
	font-weight: bolder !important;
	font-size: 16px !important;
	color: white !important;
}

.table thead {
	background: linear-gradient(60deg, #425185, #4a9b9b) !important;
}

.card {
	border: 2px solid #45758f !important;
	box-shadow: 8px 8px 9px #45758f !important;
}

#loading {
	width: 80% !important;
	height: 80% !important;
	top: 30vh !important;
	left: 50% !important;
	position: fixed;
	display: block;
	opacity: 0.7;
	z-index: 99;
	text-align: center;
}

#loading-image {
	position: absolute;
	top: 4% !important;
	left: 10% !important;
	z-index: 100;
}

#ultag li {
	border-bottom: 1px solid #558fa126;
}

#ultag li:hover {
	border-bottom: 1px solid #558fa126;
	font-weight: bolder;
	margin-bottom: 3px;
	color: #2196f3 !important;
	font-size: 21px;
}

.current {
	border-bottom: 1px solid #558fa126 !important;
	font-weight: bolder !important;
	margin-bottom: 3px !important;
	color: #2196f3 !important;
	font-size: 21px !important;
}

.csv {
	width: 10% !important;
	float: right !important;
	margin-left: 10px !important;
	border-radius: 6px !important;
	height: 36px !important;
	font-weight: bold !important;
	background: #9c7c7c00 !important;
	border: 2px solid #45758f !important;
}

.csv:active {
	background-color: #45758f !important;
	color: white !important;
}
</style> -->
									<style>
										span.select2-selection.select2-selection--single.form-select.form-select-solid.merchantPayId {
											padding: 6px 92px !important;
										}
									</style>
									<script type="text/javascript">
										$(document).ready(function () {
											$("#test").select2();
										});
									</script>
									<script>
										$(document).ready(function () {
											$("#loading").hide();
											$("#ultag > li").click(function () {
												$("#ultag > li").removeClass('current');
												$(this).addClass('current');
											});

											$('#test').on('change', function () {
												var acquirer = $("#test").val();
												debugger

												if (acquirer === 1 || acquirer == 1 || acquirer == '1') {

												} else {
													$("#acq").val(acquirer);
													$("#loading").show();
													$(".leftdiv").css("display", "none");

													$
														.post(
															"GatewayDashboard",
															{
																acquirer: acquirer
															},
															function (data, status) {

																var mop = JSON.parse(data);
																if (mop === '000') {
																	$("#loading").hide();
																	alert("Payment Type Not Mapped in this Acquirer or Acquirer not found");
																} else {
																	$("#loading").hide();
																	$(".leftdiv").fadeIn(1000);
																	$("tbody tr").remove();
																	$("#acquirer").text(acquirer);
																	for (let i = 0; i < mop.length; i++) {
																		const getAcquirerWiseData = mop[i];
																		$("tbody")
																			.append(
																				"<tr><td>"
																				+ getAcquirerWiseData.paymentType
																				+ "</td><td>"
																				+ getAcquirerWiseData.success
																				+ "</td><td>"
																				+ getAcquirerWiseData.failed
																				+ "</td><td>"
																				+ getAcquirerWiseData.inqueue
																				+ "</td><td>"
																				+ getAcquirerWiseData.successRatio
																				+ "%</td><td>"
																				+ getAcquirerWiseData.lastTrnReceived
																				+ "</td><td>"
																				+ getAcquirerWiseData.Status
																				+ "</td></tr>")
																	}
																}
															});
												}

											});
										});

									</script>
									<script type="text/javascript">
										function downloadcsv() {
											var acquirer = $("#test").val();
											if (acquirer != null && acquirer != '' && acquirer != "" && acquirer != 1 && acquirer != '1') {
												document.getElementById("PreMisSettlement").submit();
											} else {
												alert("Please Select Acquirer");
											}
										}
									</script>
								</head>

								<body>
									<% ArrayList<String> acquirerTypeUIs = (ArrayList<String>)
											request.getAttribute("acquirerTypeUIs");
											%>
											<div class="content d-flex flex-column flex-column-fluid" id="kt_content">
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
															<h1
																class="d-flex align-items-center text-dark fw-bold my-1 fs-3">
																Gateway Dashboard</h1>
															<!--end::Title-->
															<!--begin::Separator-->
															<span
																class="h-20px border-gray-200 border-start mx-4"></span>
															<!--end::Separator-->
															<!--begin::Breadcrumb-->
															<ul
																class="breadcrumb breadcrumb-separatorless fw-semibold fs-7 my-1">
																<!--begin::Item-->
																<li class="breadcrumb-item text-muted"><a href="home"
																		class="text-muted text-hover-primary">Dashboard</a>
																</li>
																<!--end::Item-->
																<!--begin::Item-->
																<li class="breadcrumb-item"><span
																		class="bullet bg-gray-200 w-5px h-2px"></span>
																</li>
																<!--end::Item-->
																<!--begin::Item-->
																<li class="breadcrumb-item text-muted">Analytics
																</li>
																<!--end::Item-->
																<!--begin::Item-->
																<li class="breadcrumb-item"><span
																		class="bullet bg-gray-200 w-5px h-2px"></span>
																</li>
																<!--end::Item-->
																<!--begin::Item-->
																<li class="breadcrumb-item text-dark"> Gateway Dashboard
																</li>
																<!--end::Item-->
															</ul>
															<!--end::Breadcrumb-->
														</div>
														<!--end::Page title-->

													</div>
													<!--end::Container-->
												</div>



												<div class="post d-flex flex-column-fluid" id="kt_post">
													<!--begin::Container-->
													<div id="kt_content_container" class="container-xxl">
														<div class="row my-5">
															<div class="col">
																<div class="card">
																	<div class="card-body">
																		<div class="row my-3 align-items-center">
																			<!-- <div class="col-md-3 fv-row"> -->
																			<div class="col-auto my-2 merchant-text">
																				<p class="text-center m-0 w-100">
																					<b>Acquirer</b>
																				</p class="text-sm-center m-0">
																			</div>
																			<div class="col-auto my-2">

																				<select
																					class="form-select form-select-solid merchantPayId"
																					id="test">
																					<option value="1" selected>Select
																						Acquirer
																					</option>
																					<% for (String acquirerTypeUI :
																						acquirerTypeUIs) { %>
																						<option
																							value='<%=acquirerTypeUI%>'>
																							<%=acquirerTypeUI%>
																						</option>
																						<% } %>
																				</select>
																			</div>

																		</div>
																	</div>
																</div>
															</div>
														</div>
													<!-- </div>
												</div>

												<div class="post d-flex flex-column-fluid" id="kt_post">
													
													<div id="kt_content_container" class="container-xxl"> -->
														<div class="row my-5">
															<div class="col">
																<div class="card">
																	<div class="card-body">
																		<div class="row my-3 align-items-center">
																			<!-- <div class="col-md-3 fv-row"> -->
																			<form action="GatewayDashboardDownload"
																				class="box-content" method="post"
																				id="PreMisSettlement">
																				<input type="hidden" id="acq"
																					name="acquirertype">
																				<button type="button"
																					onclick="downloadcsv()"
																					
																					class="csv btn btn-primary btn-xs">CSV</button>
																			</form>
																			

																		
																				<table
																					class="table table-striped table-row-bordered gy-5 gs-7"
																					id="datatable">
																					<thead
																						style="border-top: 1px solid #dbdbdb;">
																						<tr>
																							<th class="headstyle">Payment
																								Type</th>
																							<th class="headstyle">
																								Success</th>
																							<th class="headstyle">Failed
																							</th>
																							<th class="headstyle">In
																								Queue</th>
																							<th class="headstyle">
																								Success Ratio
																							</th>
																							<th class="headstyle">Last
																								Txn.
																								Received</th>
																							<th class="headstyle">Status
																							</th>
																						</tr>
																					</thead>
																					<tbody>
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


								</body>

								</html>