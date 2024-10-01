<%@page import="com.pay10.crm.actionBeans.TransactionStatusBean" %>
	<%@page import="java.util.List" %>
		<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
			<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
			<%@ taglib uri="/struts-tags" prefix="s" %>
				<html dir="ltr" lang="en-US">

				<head>
					<title>Chargeback Reason Template</title>
					<link href="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.css" rel="stylesheet"
						type="text/css" />
					<link href="../assets/plugins/custom/datatables/datatables.bundle.css" rel="stylesheet"
						type="text/css" />
					<link href="../assets/plugins/global/plugins.bundle.css" rel="stylesheet" type="text/css" />
					<link href="../assets/css/style.bundle.css" rel="stylesheet" type="text/css" />
					<link href="../assets/css/custom/custom-style.css" rel="stylesheet" type="text/css" />
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
					<style>
						.textarea {
							resize: none;
							vertical-align: middle;
							/* width: 350px !important; */
						}

						.height {
							height: 100px !important;
						}

						input:focus,
						textarea:focus,
						select:focus {
							outline: none !important;
						}

						.msgfailed {
							color: red;
							font-size: 30px;
							border: 2px solid #ff6666;
							padding: 2px 24px 2px 20px;
							border-radius: 10px;
						}

						.msgSuccess {
							color: rgb(13, 184, 90);
							font-size: 30px;
							border: 2px solid rgb(13, 184, 90);
							padding: 2px 24px 2px 20px;
							border-radius: 10px;
						}

						div#txnResultDataTable_wrapper {
							margin-top: 10px !important;
						}

						.bold {
							color: black !important;
							font-weight: bold !important;
						}
					</style>
					<script>
						$(document).ready(function () {
							callDatatable();
							$("#message").hide();

							$("#done").click(function () {
								var regex = /^[A-Z a-z 0-9]+$/;
								let cbCode = $("#cbReasonCode").val();
								let cbdesc = $("#cbReasonDescription").val()
								if (cbCode.length > 10) {
									// $("#msg").addClass("msgfailed");
									// $("#msg").text("CB Reason Code Length not More than 15 characters");
									// $("#message").show();
									// setTimeout(function () {
									// 	$("#msg").text("");
									// 	$("#message").hide();
									// }, 8000);
									alert("CB Reason Code Length not More than 10 characters");
									return false;
								} else if (cbdesc.length > 250) {
									// $("#msg").addClass("msgfailed");
									// $("#msg").text("CB Reason Description Length not More than 250 characters");
									// $("#message").show();
									// setTimeout(function () {
									// 	$("#msg").text("");
									// 	$("#message").hide();
									// }, 10000);
									alert("CB Reason Description Length not More than 250 characters");
									return false;
								} else if (!regex.test(cbCode)) {
									alert("Special Character not allowed");
								} else if (!regex.test(cbdesc)) {
									alert("Special Character not allowed");
								} else {

									var url = new URL(window.location.href).origin
										+ "/crmws/saveChargebackReason";

									var data = JSON
										.stringify({
											"cbReasonCode": cbCode,
											"cbReasonDescription": cbdesc
										});
									$.ajax({
										type: "POST",
										contentType: "application/json",
										url: url,
										data: data,
										dataType: 'json',

										success: function (result, status, xhr) {
											debugger

											if (result.respmessage === '1001') {
												alert("Please Enter CB Reason Code");
												$("#cbReasonCode").focus();
											}
											if (result.respmessage === '1002') {
												alert("Please Enter CB Reason Description");
												$("#cbReasonDescription").focus();
											}
											if (result.respmessage === '1000') {
												alert("Successfully Submitted");
												window.location.reload();
											}
											if (result.respmessage === '1003') {
												alert("Something went wrong");
											}
											if (result.respmessage === '1004') {
												alert("This Cb Reason Code is already mapped with Cb Reason Description ");
											}
										},
										error: function (xhr, status, error) {
											alert("xhr : " + xhr + "\nstatus : " + status + "\nerror : " + error);
										}
									});
									callDatatable();
								}

							});
						});


						function callDatatable() {


							var url = new URL(window.location.href).origin
								+ "/crmws/getAllChargebackReasons";

							var datas;

							$.ajax({
								type: "GET",

								url: url,

								success: function (result, status, xhr) {
									rendertable(result)
								},
								error: function (xhr, status, error) {
									alert("xhr : " + xhr + "\nstatus : " + status + "\nerror : " + error);
								}
							});
						}

						function rendertable(datas) {
							debugger
							var table = $('#txnResultDataTable').DataTable({
								dom: 'Trftlpi',
								'data': datas,
								"destroy": true,
								"searching": true,
								"aoColumns": [{
									"mData": null,
									render: (data, type, row, meta) => meta.row + 1
								},
								{
									'data': 'cbReasonCode',
								},
								{
									'data': 'cbReasonDescription',
								},
								{
									"mData": null,
									"bSortable": false,
									"mRender": function (data, type, row) {
										console.log(data);
										return '<button class="btn btn-danger"  onclick="openpopup(\'' + data.id + '\')">Delete</button>';
									}
								}]
							});

							$('#deleteModal .btn-secondary').on('click', function () {
								$('#deleteModal').modal('hide');
							});

							$('#deleteModal #crossIcon').on('click', function () {
								$('#deleteModal').modal('hide');
							});

							var chargebackReasonId;
						}

						function openpopup(id) {
							chargebackReasonId = id;
							$('#deleteModal').modal('show');
						}

						function confirmDelete() {

							setTimeout(function () {
								$('#deleteModal').modal('hide');
							}, 3000);

							var url = new URL(window.location.href).origin + "/crmws/deleteChargebackReasons/" + chargebackReasonId;

							$.ajax({
								type: "PUT",
								url: url,
								success: function (result) {
									console.log("@@@ result ::: ", result);
									$('#deleteModal').modal('hide');
									callDatatable();
								},
								error: function (xhr, status, error) {
									alert("Error deleting data: " + error);
								}
							});

						}
					</script>
				</head>


				<!-- Start Delete Confirmation Modal -->

				<div class="modal fade" id="deleteModal" tabindex="-1" role="dialog" aria-labelledby="deleteModalLabel"
					aria-hidden="true">
					<div class="modal-dialog modal-dialog-centered" role="document">
						<div class="modal-content">
							<div class="modal-header">
								<h5 class="modal-title" id="deleteModalLabel">Confirm Delete</h5>
								<button type="button" class="close btn btn-link" data-dismiss="modal" aria-label="Close"
									id="crossIcon">
									<span style="font-size: 25px;" aria-hidden="true">&times;</span>
								</button>
							</div>
							<div class="modal-body">
								Are you sure you want to delete this chargeback reason template?
							</div>
							<div class="modal-footer">
								<button type="button" class="btn btn-secondary" data-dismiss="modal">Cancel</button>
								<button type="button" class="btn btn-danger" onclick="confirmDelete()">Delete</button>
							</div>
						</div>
					</div>
				</div>


				<!-- End Delete Confirmation Modal -->

				<body id="kt_body"
					class="header-fixed header-tablet-and-mobile-fixed toolbar-enabled toolbar-fixed toolbar-tablet-and-mobile-fixed aside-enabled aside-fixed"
					style="-kt-toolbar-height: 55px; - -kt-toolbar-height-tablet-and-mobile: 55px">
					<div class="content d-flex flex-column flex-column-fluid" id="kt_content">
						<div class="toolbar" id="kt_toolbar">
							<!--begin::Container-->
							<div id="kt_toolbar_container" class="container-fluid d-flex flex-stack">
								<div data-kt-swapper="true" data-kt-swapper-mode="prepend"
									data-kt-swapper-parent="{default: '#kt_content_container', 'lg': '#kt_toolbar_container'}"
									class="page-title d-flex align-items-center me-3 flex-wrap mb-5 mb-lg-0 lh-1">
									<!--begin::Title-->
									<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">
										Chargeback Reason Template</h1>
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
										<li class="breadcrumb-item"><span class="bullet bg-gray-200 w-5px h-2px"></span>
										</li>
										<!--end::Item-->
										<!--begin::Item-->
										<li class="breadcrumb-item text-muted">Liability Management</li>
										<!--end::Item-->
										<!--begin::Item-->
										<li class="breadcrumb-item"><span class="bullet bg-gray-200 w-5px h-2px"></span>
										</li>
										<!--end::Item-->
										<!--begin::Item-->
										<li class="breadcrumb-item text-dark">Chargeback Reason
											Template</li>
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
												<div class="row my-3 align-items-center">

													<div class="col-lg-4 my-2">
														<label class="d-flex align-items-center fs-6 fw-bold mb-2">
															<span class="required">CB Reason Code</span>
														</label> <input id="cbReasonCode"
															class="form-control form-control-solid" name="cbReasonCode"
															type="text" maxlength="15"></input>
													</div>
													<div class="col-lg-4 my-2">
														<label class="d-flex align-items-center fs-6 fw-bold mb-2">
															<span class="required">CB Reason Description</span>
														</label>

														<textarea id="cbReasonDescription"
															class="form-control form-control-solid height"
															name="cbReasonDescription" type="text"
															maxlength="250"></textarea>
													</div>



												</div>
												<div style="text-align: center;">
													<button type="button" class="btn btn-primary"
														id="done">Submit</button>
												</div>
											</div>
										</div>
									</div>
								</div>

								<div class="card">
									<div class="card-body ">

										<div class="row">
											<div class="col-lg-12">

												<table id="txnResultDataTable"
													class="table table-striped table-row-bordered gy-5 gs-7 dataTable no-footer">
													<thead>
														<tr>
															<th class="bold">S.no</th>
															<th class="bold">CB Reason Code</th>
															<th class="bold">CB Reason Description</th>
															<th class="bold">Action</th>
														</tr>
													</thead>
													<tfoot>
														<tr class="fw-bold fs-6 text-gray-800">
															<th></th>
															<th></th>
															<th></th>
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
					<script src="../assets/plugins/custom/datatables/datatables.bundle.js"></script>
				</body>

				</html>