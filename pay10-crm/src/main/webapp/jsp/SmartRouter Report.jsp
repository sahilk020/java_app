<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
	<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
	<%@ taglib uri="/struts-tags" prefix="s" %>
		<html>

		<head>
			<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
			<title>Acquirer Routing Rules</title>
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
<style type="text/css">
.bold{
	color: black !important;
	font-weight: bolder !important;
}
</style>
<script src="../js/jquery.select2.js" type="text/javascript"></script>
			<script type="text/javascript">
				$(document).ready(function () {
					$(".merchantPayId").select2();
				});
			</script>

						<script type="text/javascript">
				$(document).ready(function () {
					var url = new URL(window.location.href).origin
					+ "/crmws/SmartReport/user/MERCHANT";
					debugger
					/* document.getElementById("loading").style.display = "none"; */
					$.ajax({
						type: "GET",
						dataType: "json",
						url: url,
						data: "",
						success: function (data) {
							var s = '<option value="-1">Please Select a Merchnat</option>';
							for (var i = 0; i < data.length; i++) {
								s += '<option value=' + data[i].payId + '>' + data[i].businessName + '</option>';
							}

							$("#departmentsDropdown").html(s);

						}
					});

				});
			</script>
			<script type="text/javascript">
				$(function () {
					debugger
					var url = new URL(window.location.href).origin
					+ "/crmws/SmartReport/router";
					$('#departmentsDropdown').on('change', function () {
						/* document.getElementById("loading").style.display = "block"; */
						$.ajax({
							type: 'GET',
							dataType: 'json',
							url: url,
							data: { payId: $('#departmentsDropdown').val() },
							success: function (data) {

								/* document.getElementById("loading").style.display = "none"; */
								$('#data').html("");
								var a = '<table  id="dataview" class="table table table-striped table-row-bordered gy-5 gs-7"> <tr><td style="display: none;"><br /><br /> <div> <thead class="bold"><tr><th >Merchant</th><th>Acquirer</th><th>MopType</th><th>PaymentType</th><th>TransactionType</th><th>PaymentsRegion </th><th>FailedCount</th><th>Priority</th><th>MinAmount</th><th>MaxAmount</th></tr></thead></table>';
								for (var i = 0; i < data.length; i++) {
									a += ' <tr><td>' + data[i].merchant + '</td><td>' + data[i].acquirer + '</td><td>' + data[i].mopType + '</td><td>' + data[i].paymentType + '</td><td>' + data[i].transactionType + '</td><td>' + data[i].paymentsRegion + '</td><td>' + data[i].failedCount + '</td><td> ' + data[i].priority + '</td><td> ' + data[i].minAmount + '</td><td> ' + data[i].maxAmount + '</td></tr>';
								}
								$('#data').html(a);
							},
							error: function (jqXHR, textStatus, errorThrown) {
								alert(errorThrown);
								alert("error data")
							}
						});
					});
				});

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
						<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">
							SmartRouter Report</h1>
						<!--end::Title-->
						<!--begin::Separator-->
						<span class="h-20px border-gray-200 border-start mx-4"></span>
						<!--end::Separator-->
						<!--begin::Breadcrumb-->
						<ul class="breadcrumb breadcrumb-separatorless fw-semibold fs-7 my-1">
							<!--begin::Item-->
							<li class="breadcrumb-item text-muted"><a href="home"
									class="text-muted text-hover-primary">Dashboard</a>
							</li>
							<!--end::Item-->
							<!--begin::Item-->
							<li class="breadcrumb-item"><span class="bullet bg-gray-200 w-5px h-2px"></span>
							</li>
							<!--end::Item-->
							<!--begin::Item-->
							<li class="breadcrumb-item text-muted">Transaction Report
							</li>
							<!--end::Item-->
							<!--begin::Item-->
							<li class="breadcrumb-item"><span class="bullet bg-gray-200 w-5px h-2px"></span>
							</li>
							<!--end::Item-->
							<!--begin::Item-->
							<li class="breadcrumb-item text-dark"> SmartRouter Report
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
												<b>Select Merchant:</b>
											</p class="text-sm-center m-0">
										</div>
										<div class="col-auto my-2">
											<select class="form-select form-select-solid merchantPayId"
												id="departmentsDropdown" name="departmentsDropdown"
												autocomplete="off"></select>
										</div>

									</div>
								</div>
							</div>
						</div>
					</div>

					<div class="row my-5">
						<div class="col">
							<div class="card">
								<div class="card-body">
									<div class="row my-3 align-items-center">
										<s:actionmessage class="success success-text" />
									<!-- 	<div id="loading" style="text-align: center;">
											<img id="loading-image" style="width:70px;height:70px;"
												src="../image/sand-clock-loader.gif" alt="Sending SMS..." />
										</div> -->



										<div class="scrollD">

											<table id="data"
												class="table table table-striped table-row-bordered gy-5 gs-7">
												<thead>
													<tr class="bold">
														<th>Merchant</th>
														<th>Acquirer</th>
														<th>MopType</th>
														<th>PaymentType</th>
														<th>TransactionType</th>
														<th>PaymentsRegion </th>
														<th>FailedCount</th>
														<th>Priority</th>
														<th>MinAmount</th>
														<th>MaxAmount</th>
													</tr>
												</thead>
											</table>


										</div>

									</div>
								</div>
							</div>
						</div>
					</div>
					



					<s:hidden name="token" value="%{#session.customToken}"></s:hidden>
					</div>
					</div>
					</div>	
					
				<script src="../assets/plugins/custom/datatables/datatables.bundle.js"></script>	
		</body>

		</html>