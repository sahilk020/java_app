<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
	<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
	<%@ taglib uri="/struts-tags" prefix="s" %>
		<html>

		<head>
			<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
			<title>Smart Router Configuration Report</title>
			<!--------StyleSheet------>
			<!-- <link href="../css/default.css" rel="stylesheet" type="text/css" />
<link href="../css/Jquerydatatableview.css" rel="stylesheet" />
<link href="../css/jquery-ui.css" rel="stylesheet" />
<link href="../css/Jquerydatatable.css" rel="stylesheet" />
<link href="../css/select2.min.css" rel="stylesheet" /> -->

			<!-------JavaScript------->
			<!-- <script src="../js/jquery.min.js" type="text/javascript"></script>
			<script src="../js/core/popper.min.js"></script>
			<script src="../js/core/bootstrap-material-design.min.js"></script>
			<script src="../js/plugins/perfect-scrollbar.jquery.min.js"></script> -->
			<!-- Control Center for Material Dashboard: parallax effects, scripts for the example pages etc -->
			<!-- <script src="../js/material-dashboard.js?v=2.1.0" type="text/javascript"></script>
			<script src="../js/jquery.dataTables.js"></script>
			<script src="../js/jquery-ui.js"></script>
			<script src="../js/dataTables.buttons.js" type="text/javascript"></script>
			<script src="../js/pdfmake.js" type="text/javascript"></script>
			<script src="../js/jszip.min.js" type="text/javascript"></script>
			<script src="../js/vfs_fonts.js" type="text/javascript"></script>
			<script src="../js/buttons.colVis.min.js" type="text/javascript"></script>
			<script src="../js/jquery.select2.js" type="text/javascript"></script> -->

			<!-- <link href="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.css" rel="stylesheet"
				type="text/css" />
			<link href="../assets/plugins/custom/datatables/datatables.bundle.css" rel="stylesheet" type="text/css" />
			<link href="../assets/plugins/global/plugins.bundle.css" rel="stylesheet" type="text/css" />
			<link href="../assets/css/style.bundle.css" rel="stylesheet" type="text/css" />
			<link href="../assets/css/custom/custom-style.css" rel="stylesheet" type="text/css" />
			<script src="../assets/plugins/global/plugins.bundle.js"></script>
			<script src="../assets/js/scripts.bundle.js"></script> -->

			<link href="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.css" rel="stylesheet"
				type="text/css" />
			<link href="../assets/plugins/custom/datatables/datatables.bundle.css" rel="stylesheet" type="text/css" />
			<link href="../assets/plugins/global/plugins.bundle.css" rel="stylesheet" type="text/css" />
			<link href="../assets/css/style.bundle.css" rel="stylesheet" type="text/css" />
			<link href="../assets/css/custom/custom-style.css" rel="stylesheet" type="text/css" />
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

			<script type="text/javascript">
				$(document).ready(function () {
					//document.getElementById("loading").style.display = "none";
					$("#merchants").select2();
					$("#paymentMethod").select2();
					$("#cardHolderType").select2();
					$("#smartRouterViewConfig").DataTable(
						{
							dom: 'Brtipl',
							buttons: [
								{
									extend: 'print',
									exportOptions: {
										columns: ':visible'
									}
								},
								'colvis', 'copy', 'csv', 'excel', 'pdf', 'print',
							],

							scrollY: true,
							scrollX: true,

							"bDestroy": true,
							"oLanguage": {
								"sEmptyTable": "No data available in table"
							}

						});

				});

			</script>
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
			</style>

			<script type="text/javascript">
				$(function () {

					debugger

					$('#submitButton').click(function () {


						var merchants = document.getElementById("merchants").value;
						if (merchants == "" || merchants == "Select Merchant") {
							alert("Please select Merchant");
							return false;
						}
						var paymentMethod = document.getElementById("paymentMethod").value;
						if (paymentMethod == "" || paymentMethod == "Select Payment Type") {
							alert("Please select Payment type");
							return false;
						}

						var cardHolderType = document.getElementById("cardHolderType").value;
						var token = document.getElementsByName("token")[0].value;
						//document.getElementById("loading").style.display = "block";

						table = $("#smartRouterViewConfig").DataTable(
							{
								dom: 'Brtipl',
								buttons: [
									{
										extend: 'print',
										exportOptions: {
											columns: ':visible'
										}
									},
									'colvis', 'copy', 'csv', 'excel', 'pdf', 'print',
								],

								scrollY: true,
								scrollX: true,
								searchDelay: 500,
								processing: false,
								// serverSide: true,
								order: [[1, 'desc']],
								stateSave: true,
								dom: 'Brtipl',
								paging: true,


								ajax: function (data, callback, settings) {
									$.ajax({
										"url": "viewSmartRouterConfigurationAction",
										"timeout": 0,
										"type": "POST",
										"data": {
											"merchants": merchants,
											"paymentMethod": paymentMethod,
											"cardHolderType": cardHolderType,
											"struts.token.name": "token"
										},
										success: function (data) {
											//document.getElementById("loading").style.display = "none";
											callback(data);
										},
										error: function (data) {
											//document.getElementById("loading").style.display = "none";
										}
									});
								},

								"bProcessing": true,
								"bLengthChange": true,
								"bDestroy": true,

								"aoColumns": [
									{ data: "merchant" },
									{ data: "paymentsRegion" },
									{ data: "cardHolderType" },
									{ data: "paymentType" },
									{ data: "mopType" },
									{ data: "acquirer" },
									{ data: "minAmount" },
									{ data: "maxAmount" },
									{ data: "loadPercentage" },

								]

							});

					});
				});
			</script>

			<style>
				.dt-buttons {
					display: none;
				}
			</style>

			<script type="text/javascript">
				$('a.toggle-vis').on('click', function (e) {
					debugger
					e.preventDefault();
					table = $('#smartRouterReportDataTable').DataTable();
					// Get the column API object
					var column1 = table.column($(this).attr('data-column'));
					// Toggle the visibility
					column1.visible(!column1.visible());
					if ($(this)[0].classList[1] == 'activecustom') {
						$(this).removeClass('activecustom');
					}
					else {
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
			</script>

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
						<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">Smart Router Configuration
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
							<li class="breadcrumb-item text-muted">VIew Configuration</li>
							<!--end::Item-->
							<!--begin::Item-->
							<li class="breadcrumb-item">
								<span class="bullet bg-gray-200 w-5px h-2px"></span>
							</li>
							<!--end::Item-->
							<!--begin::Item-->
							<li class="breadcrumb-item text-dark">Smart Router Configuration</li>
							<!--end::Item-->
						</ul>
						<!--end::Breadcrumb-->
					</div>
					<!--end::Page title-->

				</div>
				<!--end::Container-->
			</div>
			<!--end::Toolbar-->

			<div id="kt_content_container" class="container-xxl">
				<div class="row my-5">
					<div class="col">
						<div class="card">
							<div class="card-body">
								<!--begin::Input group-->
								<div class="row g-9 mb-8">

									<div class="col-sm-6 col-lg-3">
										<label class="d-flex align-items-center fs-6 fw-semibold mb-2">Merchant</label>
										<s:if
											test="%{#session.USER.UserType.name()=='ADMIN' || #session.USER.UserType.name()=='SUBADMIN' || #session.USER_TYPE.name()=='SUPERADMIN'}">
											<s:select name="merchant" class="form-select form-select-solid"
												id="merchants" headerKey="" headerValue="Select Merchant"
												list="merchantList" listKey="payId" listValue="businessName"
												autocomplete="off" />
										</s:if>
										<s:else>
											<s:select name="merchant" class="form-select form-select-solid"
												id="merchants" list="merchantList" listKey="payId"
												listValue="businessName" autocomplete="off" />
										</s:else>
									</div>


									<div class="col-sm-6 col-lg-3">
										<label class="d-flex align-items-center fs-6 fw-semibold mb-2">Payment
											Method</label>
										<s:select headerKey="" headerValue="Select Payment Type"
											class="form-select form-select-solid"
											list="@com.pay10.commons.util.PaymentType@values()" listValue="name"
											listKey="code" name="paymentMethod" id="paymentMethod" autocomplete="off"
											value="" />

										<input type="hidden" id="acquirerType" name="acquirerType" />
									</div>
									<div class="col-sm-6 col-lg-3">
										<label class="d-flex align-items-center fs-6 fw-semibold mb-2">Card
											Holder Type</label>
										<s:select headerKey="ALL" headerValue="ALL"
											class="form-select form-select-solid"
											list="#{'COMMERCIAL':'Commercial','CONSUMER':'Consumer'}"
											name="cardHolderType" id="cardHolderType" />

									</div>

									<div class="col-sm-6 col-lg-3">
										<button class="btn btn-primary  mt-7 submit_btn"
											id="submitButton">Submit</button>
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
									<!--begin::Col-->
									<div class="col">

										<div class="row g-9 mb-8">
											<div class="row g-9 mb-8 justify-content-end">
												<div class="col-lg-4 col-sm-12 col-md-6">
													<select name="currency" data-control="select2"
														data-placeholder="Actions" id="actions11"
														class="form-select form-select-solid actions"
														data-hide-search="true" onchange="myFunction();">
														<option value="">Actions</option>
														<option value="copy">Copy</option>
														<option value="csv">CSV</option>
														<option value="pdf">PDF</option>
													</select>
												</div>

											</div>
											<div class="row g-9 mb-8">
												<div
													class="table-responsive dataTables_wrapper dt-bootstrap4 no-footer">
													<table id="smartRouterViewConfig"
														class="display nowrap table table-striped table-row-bordered gy-5 gs-7"
														cellspacing="0" width="100%">
														<thead>
															<tr class="boxheadingsmall fw-bold fs-6 text-gray-800">
																<th >Merchant</th>
																<th >Payment
																	Region</th>
																<th >Card Holder
																	Type</th>
																<th >Payment Type
																</th>
																<th >Mop Type</th>
																<th >Acquirer</th>
																<th >Min Amount
																</th>
																<th >Max Amount
																</th>
																<th >Load %</th>
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
			</div>



		</body>

		</html>