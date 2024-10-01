<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
	<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
	<%@ taglib uri="/struts-tags" prefix="s" %>
		<html dir="ltr" lang="en-US">

		<head>
			<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
			<title>Acquirer Master Switch</title>




			<link rel="shortcut icon" href="assets/media/images/paylogo.svg" />
			<!--begin::Fonts-->
			<link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Inter:300,400,500,600,700" />
			<!--end::Fonts-->
			<!--begin::Vendor Stylesheets(used by this page)-->
			<link href="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.css" rel="stylesheet"
				type="text/css" />
			<link href="../assets/plugins/custom/datatables/datatables.bundle.css" rel="stylesheet" type="text/css" />

			<!--end::Vendor Stylesheets-->
			<!--begin::Global Stylesheets Bundle(used by all pages)-->
			<link href="../assets/plugins/global/plugins.bundle.css" rel="stylesheet" type="text/css" />
			<link href="../assets/css/style.bundle.css" rel="stylesheet" type="text/css" />
			<link href="../assets/css/custom/custom-style.css" rel="stylesheet" type="text/css" />

			<script src="../assets/plugins/global/plugins.bundle.js"></script>
			<script src="../assets/js/scripts.bundle.js"></script>
			<link href="../css/select2.min.css" rel="stylesheet" />
			<script src="../js/jquery.select2.js" type="text/javascript"></script>
			<script type="text/javascript" src="../js/sweetalert.js"></script>
			<link rel="stylesheet" href="../css/sweetalert.css">
			<style>
				.mytable thead {
					background: linear-gradient(60deg, #f7a600, #f71d00);
					color: #fff;
				}

				.swal-text {
					color: red !important;
				}

				.switch {
					position: relative;
					display: inline-block;
					width: 60px;
					height: 34px;
				}

				.switch input {
					opacity: 0;
					width: 0;
					height: 0;
				}

				.slider {
					position: absolute;
					cursor: pointer;
					top: 0;
					left: 0;
					right: 0;
					bottom: 0;
					background-color: #ccc;
					-webkit-transition: .4s;
					transition: .4s;
				}

				.slider:before {
					position: absolute;
					content: "";
					height: 26px;
					width: 26px;
					left: 4px;
					bottom: 4px;
					background-color: white;
					-webkit-transition: .4s;
					transition: .4s;
				}

				input:checked+.slider {
					background-color: #2196F3;
				}

				input:focus+.slider {
					box-shadow: 0 0 1px #2196F3;
				}

				input:checked+.slider:before {
					-webkit-transform: translateX(26px);
					-ms-transform: translateX(26px);
					transform: translateX(26px);
				}

				/* Rounded sliders */
				.slider.round {
					border-radius: 34px;
				}

				.slider.round:before {
					border-radius: 50%;
				}
			</style>
			<style>
				h1 {
					color: green;
				}

				/* toggle in label designing */
				.toggle {
					position: relative;
					display: inline-block;
					width: 80px;
					height: 33px;
					background-color: #e01717e0;
					border-radius: 30px;
					border: 1px solid #0a0000;
				}

				/* After slide changes */
				.toggle:after {
					content: '';
					position: absolute;
					width: 30px;
					height: 30px;
					border-radius: 50%;
					background-color: #f7f7f7;
					top: 1px;
					left: 1px;
					transition: all 0.5s;
				}

				/* Toggle text */
				p {
					font-size: 19px;

				}

				/* Checkbox checked effect */
				.checkbox:checked+.toggle::after {
					left: 49px;
				}

				/* Checkbox checked toggle label bg color */
				.checkbox:checked+.toggle {
					background-color: #2e9e0763;
				}

				/* Checkbox vanished */
				.checkbox {
					display: none;
				}
			</style>




			<%-- <link href="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.css" rel="stylesheet"
				type="text/css" />
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

				.dt-buttons.btn-group.flex-wrap {
					display: none;
				}

				table.dataTable.display tbody tr.odd {
					background-color: #e6e6ff !important;
				}

				table.dataTable.display tbody tr.odd>.sorting_1 {
					background-color: #e6e6ff !important;
				}

				table.display td.center {
					text-align: left !important;
				}

				.btn:focus {
					outline: 0 !important;
				}
			</style>
			<script type="text/javascript">
				function decodeVal(text) {
					return $('<div/>').html(text).text();
				}

				function getListpayment() {

					var acquirer = document.getElementById('acquirer3').value;
					if (acquirer == "") { } else {
						var urls = new URL(window.location.href);
						var domain = urls.origin;
						debugger
						$.ajax({
							type: "POST",
							url: domain + "/crmws/GlobalAcquirerSwitch/getGlobalPaymentType",
							timeout: 0,
							data: {

								"acquirer": acquirer,

							},

							success: function (data) {
								var acquirer = JSON.stringify(data.data);
								var acquirerList = JSON.parse(acquirer);
								//var s = '<option value="">Select acquirer</option>';
								var s = '<option value="">Select the Payment type </option>';
								for (var i = 0; i < acquirerList.length; i++) {

									s += '<option value="' + acquirerList[i].code + '">' + acquirerList[i].name
										+ '</option>';
								}

								$("#paymentMethods").html(s);
							}
						});
					}
				}

				function getList() {
					var payment = document.getElementById('paymentMethods').value;
					var acquirer = document.getElementById('acquirer3').value;




					if (payment == "" || acquirer == "") {


					} else {
						//$('#searchAgentDataTable').empty();

						document.getElementById("loading").style.display = "block";

						populateDataTable();
						document.getElementById("loading").style.display = "none";

						document.getElementById("autoRefund").checked = true;


					}

				}
				$(document).ready(

					function () {

						document.getElementById("loading").style.display = "none";


						document.getElementById("autoRefund").checked = false;
					});

				// 			populateDataTable();
				// 			//enableBaseOnAccess();
				// 			$("#submit").click(
				// 					function(env) {
				// 						/* var table = $('#authorizeDataTable')
				// 								.DataTable(); */
				// 						$('#searchAgentDataTable').empty();


				// 						populateDataTable();

				// 					});
				// 		});

				function populateDataTable() {

					var payment = document.getElementById('paymentMethods').value;
					var acquirer = document.getElementById('acquirer3').value;
					var token = document.getElementsByName("token")[0].value;
					var urls = new URL(window.location.href);
					var domain = urls.origin;

					$('#searchAgentDataTable')
						.DataTable(
							{
								dom: 'BTftlpi',
								buttons: [{
									extend: 'copyHtml5',
									exportOptions: {
										columns: [':visible :not(:last-child)']
									}
								}, {
									extend: 'csvHtml5',
									title: 'Acquirer List',
									exportOptions: {
										columns: [':visible :not(:last-child)']
									}
								}, {
									extend: 'pdfHtml5',
									title: 'Acquirer List',
									orientation: 'landscape',
									exportOptions: {
										columns: [':visible :not(:last-child)']
									},
									customize: function (doc) {
										doc.defaultStyle.alignment = 'center';
										doc.styles.tableHeader.alignment = 'center';
									}
								}, {
									extend: 'print',
									title: 'Acquirer List',
									orientation: 'landscape',
									exportOptions: {
										columns: [':visible :not(:last-child)']
									}
								}, {
									extend: 'colvis',
									//           collectionLayout: 'fixed two-column',
									columns: [0, 1, 2, 3]
								}],
								"ajax": {
									"url": domain + "/crmws/GlobalAcquirerSwitch/getGlobalMappedAcquirer?acquirerType=" + acquirer + "&paymentType=" + payment,
									"type": "POST",
									"data": {

										token: token,
										"struts.token.name": "token",
									},
									"dataSrc": function (res) {
										var count = res.data.length;
										var acquirerDownMessage = res.message;
										console.log(acquirerDownMessage);
										if (acquirerDownMessage == "Data Fetch Successfully") {
											acquirerDownMessage = "";
											$("#acquirerDownMessage").html(acquirerDownMessage)
										} else {
											acquirerDownMessage = acquirerDownMessage + ", To active acquirer switch ON the Status Toggle";
											$("#acquirerDownMessage").html(acquirerDownMessage)
										}

										if (count > 0) {
											document.getElementById("autoRefund").checked = true;

										} else {
											document.getElementById("autoRefund").checked = false;

										}

										return res.data;
									}
								},

								"bProcessing": true,
								"bLengthChange": true,
								"bDestroy": true,
								"iDisplayLength": 10,

								"order": [[1, "desc"]],
								"aoColumns": [


									{
										"mData": "merchantName",
										"sWidth": '25%'
									},


									{
										"mData": "payId",
										"sWidth": '25%'

									},

									{
										"mData": "mopType",
										"sWidth": '22%',
										"visible": false
									},

									{
										"mData": "payId",
										"sWidth": '25%',
										"visible": false,
									},

								]

							});


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

			<div class="content d-flex flex-column flex-column-fluid" id="kt_content">

				<div class="toolbar" id="kt_toolbar">
					<!--begin::Container-->
					<div id="kt_toolbar_container" class="container-fluid d-flex flex-stack">
						<!--begin::Page title-->
						<div data-kt-swapper="true" data-kt-swapper-mode="prepend"
							data-kt-swapper-parent="{default: '#kt_content_container', 'lg': '#kt_toolbar_container'}"
							class="page-title d-flex align-items-center me-3 flex-wrap mb-5 mb-lg-0 lh-1">
							<!--begin::Title-->
							<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">Acquirer Master Switch
							</h1>
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
								<li class="breadcrumb-item text-muted">Manage Acquirers</li>
								<!--end::Item-->
								<!--begin::Item-->
								<li class="breadcrumb-item">
									<span class="bullet bg-gray-200 w-5px h-2px"></span>
								</li>
								<!--end::Item-->
								<!--begin::Item-->
								<li class="breadcrumb-item text-dark">Acquirer Master Switch</li>
								<!--end::Item-->
							</ul>
							<!--end::Breadcrumb-->
						</div>
						<!--end::Page title-->

					</div>
					<!--end::Container-->
				</div>



				<div class="row my-5">

					<div id="loading" style="text-align: center;">
						<img id="loading-image" style="width: 70px; height: 70px;" src="../image/sand-clock-loader.gif"
							alt="Sending SMS..." />
					</div>
					<div class="col">
						<div class="card ">
							<div class="card-body ">
								<div class="container">
									<div class="row">
										<s:form id="resellerPayoutForm">
											<input type="hidden" name="email" id="email"
												value="<s:property value='%{#session.USER.emailId}'/>" />

											<div style="
    display: inline-flex;
">
												<div class="col-md-6 fv-row">
													<label class="d-flex align-items-center fs-6 fw-bold mb-2">
														<span class="">Acquirer :</span>
													</label>
													<!--end::Label-->
													<s:select headerKey="" headerValue="Select Acquirer"
														data-control="select2" class="form-select form-select-solid"
														list="@com.pay10.commons.util.AcquirerTypeUI@values()"
														listValue="name" listKey="name" name="acquirer" id="acquirer3"
														autocomplete="off" value="" onchange="getListpayment()" />

												</div>
												<div class="col-md-8 fv-row" style="padding-left: 70px;">
													<label class="d-flex align-items-center fs-6 fw-bold mb-2">
														<span class="">PaymentType :</span>
													</label>
													<!--end::Label-->
													<s:select headerKey="" headerValue="Select Payment Type"
														class="form-select form-select-solid"
														list="@com.pay10.commons.util.PaymentTypeUI@values()"
														listValue="name" listKey="code" name="paymentMethod"
														id="paymentMethods" autocomplete="off" onchange="getList()" />

												</div>

												<div class="col-md-8 fv-row" style="padding-left: 70px;">
													<label class="d-flex align-items-center fs-6 fw-bold mb-2">
														<span class="">Status :</span>
													</label>
													<label class="d-flex align-items-center fs-6 fw-bold mb-2">

														<span class="">OFF</span>

														<label class="switch"> <input
																onchange="hideAndShowForAuto(this)" type="checkbox"
																id="autoRefund"> <span class="slider round"></span>
														</label>
														<span class="">ON </span></label>



												</div>
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
										<table width="100%" align="left" cellpadding="0" cellspacing="0" class="txnf">
											<tr>
												<td align="left">
													<s:actionmessage />
												</td>
											</tr>
											<!-- <tr>
			<td align="left"><h2>Acquirer List</h2></td>
		</tr> -->
											<tr>



												<h5 id="acquirerDownMessage"
													style="text-align-last: center; color: #f71d00;"></h5>

												<br>
												<div class="row g-9 mb-8 justify-content-end">
													<div class="col-lg-2 col-sm-12 col-md-6">
														<select name="currency" data-control="select2"
															data-placeholder="Actions" id="actions11"
															class="form-select form-select-solid actions dropbtn1"
															data-hide-search="true" onchange="myFunctions();">
															<option value="">Actions</option>
															<option value="copy">Copy</option>
															<option value="csv">CSV</option>
															<!--	<option value="pdf">PDF</option>
														<option value="print">PRINT</option>-->
														</select>
													</div>
													<div class="col-lg-4 col-sm-12 col-md-6">
														<div class="dropdown1">
															<button
																class="form-select form-select-solid actions dropbtn1">Customize
																Columns</button>
															<div class="dropdown-content1">
																<a class="toggle-vis" data-column="0">PayId</a>
																<a class="toggle-vis" data-column="1">Merchant Name</a>
																<a class="toggle-vis" data-column="2">Mop Type</a>



															</div>
														</div>
													</div>
												</div>



												<td align="left">
													<table width="100%" border="0" align="center" cellpadding="0"
														cellspacing="0">
														<tr>
															<td colspan="5" align="left" valign="top">&nbsp;</td>
														</tr>
														<tr>
															<td colspan="5" align="center" valign="top">
																<table width="100%" border="0" cellpadding="0"
																	cellspacing="0">
																</table>
															</td>
														</tr>
													</table>
												</td>
											</tr>
											<tr>
												<td align="left" style="padding: 10px;">
													<div class="scrollD">
														<table width="100%" border="0" cellpadding="0" cellspacing="0"
															id="searchAgentDataTable"
															class="table table-striped table-row-bordered gy-5 gs-7">
															<!-- class="display" -->
															<thead>
																<!-- <tr class="boxheadingsmall"> -->
																<tr class="fw-bold fs-6 text-gray-800">

																	<th>Merchant Name</th>
																	<th>PayId</th>

																	<th></th>

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


			</div>
			<script type="text/javascript">
				$('#searchAgentDataTable').on('draw.dt', function () {
				});

			</script>
			<script type="text/javascript">

				function hideAndShowForAuto(event) {
					document.getElementById("loading").style.display = "none";

					var autoRefund = document.getElementById("autoRefund").checked;
					var payment = document.getElementById('paymentMethods').value;
					var acquirer = document.getElementById('acquirer3').value;
					var updateby = document.getElementById("email").value;
					var urls = new URL(window.location.href);
					var domain = urls.origin;
					if (payment == "" || acquirer == "") {
						if (autoRefund) {
							alert("Please Select Payment Type or Acquirer");
							document.getElementById("autoRefund").checked = false;

						} else {
							alert("Please Select Payment Type or Acquirer");

							document.getElementById("autoRefund").checked = true;

						}
						return
					}
					if (!autoRefund) {






						swal({

							title: "Are you sure you want to Inactive this rules?",
							text: "Note: Merchants mapped with a single Acquirer won't be able to transact after this action.",
							type: "warning",
							showCancelButton: true,
							confirmButtonColor: "#DD6B55",
							confirmButtonText: "Yes, Inactive it !",
							closeOnConfirm: false
						}, function (isConfirm) {
							if (!isConfirm) {
								document.getElementById("autoRefund").checked = true;
								return;
							}
							document.getElementById("loading").style.display = "block";

							$.ajax({
								type: "POST",
								url: domain + "/crmws/GlobalAcquirerSwitch/changeGlobalStatusInactive",
								timeout: 0,
								data: {
									"acquirer": acquirer,
									"paymentType": payment,
									"updateby": updateby,


								},
								success: function (data) {
									var response = data.response;
									document.getElementById("autoRefund").checked = false;
									document.getElementById("loading").style.display = "none";

									swal({
										title: ' Successful !',
										type: "success"
									}, function () {
										window.location.reload();
									});
								},
								error: function (data) {
									window.location.reload();
								}
							});

						})



					} else {
						debugger

						swal({
							title: "Are you sure you want to Active this rules ?",
							type: "warning",
							showCancelButton: true,
							confirmButtonColor: "#DD6B55",
							confirmButtonText: "Yes, Active it !",
							closeOnConfirm: false
						}, function (isConfirm) {
							if (!isConfirm) {
								document.getElementById("autoRefund").checked = true;
								return;
							}
							document.getElementById("loading").style.display = "block";

							$.ajax({
								type: "POST",
								url: domain + "/crmws/GlobalAcquirerSwitch/changeGlobalStatusActive",
								timeout: 0,
								data: {
									"acquirer": acquirer,
									"paymentType": payment,
									"updateby": updateby,



								},
								success: function (data) {
									if (data.data.length > 0) {
										var response = data.response;
										document.getElementById("autoRefund").checked = true;
										document.getElementById("loading").style.display = "none";

										swal({
											title: ' Successful!',
											type: "success"
										}, function () {
											populateDataTable()

										});
									} else {

										var response = data.response;
										document.getElementById("autoRefund").checked = false;

										swal({
											title: 'No rule found !',
											type: "success"
										}, function () {
											window.location.reload();
										});
									}
								},
								error: function (data) {
									window.location.reload();
								}
							});

						})










					}
				}
				$('a.toggle-vis').on('click', function (e) {
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
					/*if (x == 'pdf') {
						document.querySelector('.buttons-pdf').click();
					}*/


				}
			</script>
			<script src="../assets/plugins/custom/datatables/datatables.bundle.js"></script>

		</body>

		</html>