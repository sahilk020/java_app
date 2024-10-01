<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
	<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
	<%@ taglib uri="/struts-tags" prefix="s" %>
		<html>

		<head>
			<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
			<title>Chargeback Status Update</title>
			<link href="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.css" rel="stylesheet"
				type="text/css" />
			<link href="../assets/plugins/custom/datatables/datatables.bundle.css" rel="stylesheet" type="text/css" />
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


			<link href="../css/select2.min.css" rel="stylesheet" />
			<script src="../js/jquery.select2.js" type="text/javascript"></script>

			<style>
				.switch {
					position: relative;
					display: inline-block;
					width: 30px;
					height: 17px;
				}

				.switch input {
					display: none;
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
					height: 13px;
					width: 13px;
					left: 2px;
					bottom: 2px;
					background-color: white;
					-webkit-transition: .4s;
					transition: .4s;
				}

				input:checked+.slider {
					background-color: #2196F3;
				}

				.dt-buttons.btn-group.flex-wrap {
					display: none !important;
				}

				input:focus+.slider {
					box-shadow: 0 0 1px #2196F3;
				}

				input:checked+.slider:before {
					-webkit-transform: translateX(13px);
					-ms-transform: translateX(13px);
					transform: translateX(13px);
				}

				/* Rounded sliders */
				.slider.round {
					border-radius: 17px;
				}

				.slider.round:before {
					border-radius: 50%;
				}

				.mycheckbox {
					/* Your style here */

				}

				.switch {
					display: table-cell;
					vertical-align: middle;
					padding: 10px;
				}

				input.cmn-toggle-jwr:checked+label:after {
					margin-left: 1.5em;
				}

				table .toggle.btn {
					min-width: 48px;
					min-height: 28px;
				}

				table .btn {
					/* margin-bottom: 4px; */
					/* margin-right: 5px; */
					/* padding: 1px 12px;
    font-size: 11px; */

				}

				table .toggle-off.btn {
					padding: 0;
					margin: 0;
				}
			</style>


			<script type="text/javascript">

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

				button#btnSubmit {
					margin-top: 25px !important;
				}

				a.btn.btn-primary.btn-xs {
					margin-top: 26px !important;
				}

				.error {
					font-family: "Times New Roman";
					color: red;
					width: 100%;
					margin-top: 8px;
				}

				/* .btn, .btn.btn-default {
	background-color: #e1e0e0 !important;
} */
			</style>
		</head>
		<script>
			function submitCSV() {
				debugger
				$('#btnSubmit').prop('disabled', true);
				var urls = new URL(window.location.href);
				var domain = urls.origin;
				var file = $('#file').val();
				if (!(/\.(xlsx|xls|xlsm)$/i).test(file)) {
					alert('Please upload valid excel file .xlsx, .xlsm, .xls only.');
					$(file).val('');
				} else {

					var form = $('#form')[0];
					var data = new FormData(form);

					$.ajax({
						url: domain + "/crmws/chargebackStatusBulkUpload",
						type: 'POST',
						enctype: 'multipart/form-data',
						data: data,
						processData: false,
						contentType: false,
						cache: false,
						success: function (data) {
							alert(data.respmessage);
							window.location.reload();
							$('#btnSubmit').prop('disabled', false);
						},
						error: function (data, textStatus, jqXHR) {
							$('#btnSubmit').prop('disabled', false);
							if (data.responseText
								&& JSON.parse(data.responseText).respmessage) {
								var responseText = JSON.parse(data.responseText)
								alert(responseText.respmessage);
							} else {
								alert("Error while uploading file");
							}


						}
					});
				}
			}
		</script>

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
								Chargeback Status Update</h1>
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
								<li class="breadcrumb-item"><span class="bullet bg-gray-200 w-5px h-2px"></span></li>
								<!--end::Item-->
								<!--begin::Item-->
								<li class="breadcrumb-item text-muted">Dispute Management</li>
								<!--end::Item-->
								<!--begin::Item-->
								<li class="breadcrumb-item"><span class="bullet bg-gray-200 w-5px h-2px"></span></li>
								<!--end::Item-->
								<!--begin::Item-->
								<li class="breadcrumb-item text-dark">Chargeback Status
									Update</li>
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
										<s:form id="form" class="mt-3">
											<div class="row my-3 align-items-center">
												<div class="col-lg-3 my-2">
													<label class="d-flex align-items-center fs-6 fw-bold mb-2">
														<span class="">Bulk Upload</span>
													</label> <input type="file" name="file" id="file"
														name="liabilityHold" class="form-select form-select-solid">
												</div>
												<div class="col-lg-3 my-2">
													<label class="d-flex align-items-center fs-6 fw-bold mb-2">
														<span class="">&nbsp </span>
														<button type="button" class="btn btn-primary btn-xs"
															onclick="submitCSV()" id="btnSubmit">Submit</button>
												</div>
												<div class="col-lg-3 my-2">
													<label class="d-flex align-items-center fs-6 fw-bold mb-2">
														<span class=""></span> <a href="../assets/Chargeback.xlsx"
															download="ChargebackStatusUpdate"
															class="btn btn-primary btn-xs">Sample File Download</a>
												</div>
											</div>
										</s:form>
									</div>
								</div>
							</div>
						</div>

						<div class="row my-5">
							<div class="col">
								<div class="card">
									<div class="card-body">


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
											<div class="col-lg-4 col-sm-12 col-md-6">
												<div class="dropdown1">
													<button
														class="form-select form-select-solid actions dropbtn1">Customize
														Columns</button>
													<div class="dropdown-content1">
														<a class="toggle-vis" data-column="2">Merchant PayId</a> <a
															class="toggle-vis" data-column="3">CB CaseId</a> <a
															class="toggle-vis" data-column="5">Txn Amt</a> <a
															class="toggle-vis" data-column="6">PG CaseId</a> <a
															class="toggle-vis" data-column="7">Merchant TxnId</a> <a
															class="toggle-vis" data-column="8">PgRef No</a>
													</div>
												</div>
											</div>
										</div>
										<div class="row g-9 mt-9">
											<s:if test="%{#session.USER.UserGroup.group =='Merchant'}">
												<table id="chargebackdataTable"
													class="table table-striped table-row-bordered gy-5 gs-7">

													<thead>
														<tr class="boxheadingsmall">
															<th>Serial No.</th>
															<th></th>
															<th>Merchant PayId</th>
															<th>CB CaseId</th>
															<th>Txn Amt</th>
															<th>PG CaseId</th>
															<th>Merchant TxnId</th>
															<th>Order Id</th>
															<th>PgRef No</th>
															<th>Bank Txn Id</th>
															<th>Cb Amount</th>
															<th>Cb Reason</th>
															<th>Cb Reason Code</th>
															<th>Cb Intimation Date</th>
															<th>Cb DdlineDate</th>
															<th>Mode Of Payment</th>
															<th>Acquirer Name</th>
															<th>Settlemt Date</th>
															<th>Date Of Txn</th>
															<th>Customer Name</th>
															<th>Customer Phone</th>
															<th>Email</th>
															<th>Notification Email</th>
															<th>Status</th>
															<th>Action</th>
														</tr>
													</thead>
												</table>

											</s:if>
											<s:else>
												<table id="datatable"
													class="table table-striped table-row-bordered gy-5 gs-7">
													<thead>
														<tr class="boxheadingsmall">
															<th>Serial No.</th>
															<th></th>
															<th>Merchant</th>
															<th>Merchant PayId</th>
															<th>CB CaseId</th>
															<th>Txn Amt</th>
															<th>PG Case Id</th>
															<th>Merchant Txn Id</th>
															<th>Order Id</th>
															<th>PgRef No</th>
															<th>Bank Txn Id</th>
															<th>Cb Amount</th>
															<th>Cb Reason</th>
															<th>Cb Reason Code</th>
															<th>Cb Intimation Date</th>
															<th>Cb Ddline Date</th>
															<th>Mode Of Payment</th>
															<th>Acquirer Name</th>
															<th>Settlemt Date</th>
															<th>Date Of Txn</th>
															<th>Customer Name</th>
															<th>Customer Phone</th>
															<th>Email</th>
															<th>Notification Email</th>
															<th>Status</th>
															<th>Action</th>
														</tr>
													</thead>
												</table>

											</s:else>
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

				$('a.toggle-vis').on('click', function (e) {
					debugger
					e.preventDefault();
					table = $('#datatable').DataTable();
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


				var userType = "<s:property value='%{#session.USER.UserGroup.group}'/>";
				console.log(userType)
				if (userType == 'Admin' || userType == 'Sub Admin') {
					renderTable();
				} else {

					$('#create').hide();
					$('#file').hide();
					$('#submitFile').hide();
					renderchargebackTable();
				}


				function renderTable(data) {

					$("#datatable").DataTable().destroy();
					$('#datatable').dataTable({

						dom: 'BTftlpi',
						scrollY: true,
						scrollX: true,
						'columnDefs': [{
							'searchable': false

						}],
						"aaData": data,
						"bProcessing": true,
						"scrollY": true,
						"scrollX": true,
						"bLengthChange": true,
						"bAutoWidth": false,
						"iDisplayLength": 10,
						"order": [[0, "asc"]],
						"aoColumns": [

							{
								"mData": null,
								render: (data, type, row, meta) => meta.row + 1
							}, {
								"mData": "id",
								"visible": false,
								"className": "displayNone"
							}, {
					              "mData": "merchantName"
				            }, {
								"mData": "merchant_pay_id"
							}, {
								"mData": "cb_case_id"
							}, {
								"mData": "txn_amount"
							}, {
								"mData": "pg_case_id"
							}, {
								"mData": "merchant_txn_id"
							}, {
								"mData": "order_id"
							}, {
								"mData": "pgRefNo"
							}, {
								"mData": "bank_txn_id"
							}, {
								"mData": "cb_amount"
							}, {
								"mData": "cb_reason"
							}, {
								"mData": "cb_reason_code"
							}, {
								"mData": "cb_intimation_date"
							}, {
								"mData": "cb_deadline_date"
							}, {
								"mData": "mode_of_payment"
							}, {
								"mData": "acq_name"
							}, {
								"mData": "settlement_date"
							}, {
								"mData": "date_of_txn"
							}, {
								"mData": "customer_name"
							}, {
								"mData": "customer_phone"
							}, {
								"mData": "email"
							}, {
								"mData": "nemail"
							}, {
								"mData": "status"
							}, {
								"mData": null,
								"sClass": "center",
								"mRender": function (data) {
									let merchant = "";
									let acquirer = "";
									/*  let debited=""; 
									 let credited=""; */


									if (data.cbClosedInFavorMerchant == "MERCHANT_FAVOUR") {
										merchant = '<button class="btn btn-primary btn-xs"  name="updateCbFavourMerchant" id="updateCbFavourMerchant" onclick="updateCbFavourMerchant(' + data.cb_case_id + ')" disabled>Merchant</button>&nbsp';
									} else {
										merchant = '<button class="btn btn-primary btn-xs mt-2"  name="updateCbFavourMerchant" id="updateCbFavourMerchant" onclick="updateCbFavourMerchant(' + data.cb_case_id + ')" >Merchant</button>&nbsp';
									}

									if (data.cbClosedInFavorBank == "BANK_ACQ_FAVOUR") {
										acquirer = '<button class="btn btn-primary btn-xs"  name="updateCbFavourBankAcquirer" id="updateCbFavourBankAcquirer" onclick="updateCbFavourBankAcquirer(' + data.cb_case_id + ')" disabled>Bank/Acquirer</button>&nbsp';
									} else {
										acquirer = '<button class="btn btn-primary btn-xs mt-2"  name="updateCbFavourBankAcquirer" id="updateCbFavourBankAcquirer" onclick="updateCbFavourBankAcquirer(' + data.cb_case_id + ')" >Bank/Acquirer</button>&nbsp';
									}

									/* if (data.merchantAmoountDR=="MERCHANT_DEBITED") {
									  debited= '<button class="btn btn-primary btn-xs"  name="updateCbDebit" id="updateCbDebit" onclick="updateCbDebit('+data.cb_case_id+')" disabled>Amount Debited</button>&nbsp';
									} else {
									  debited= '<button class="btn btn-primary btn-xs"  name="updateCbDebit" id="updateCbDebit" onclick="updateCbDebit('+data.cb_case_id+')">Amount Debited</button>&nbsp';
									}
								
									if (data.merchantAmoountCR=="MERCHANT_CREDITED") {
									  credited= '<button class="btn btn-primary btn-xs"  name="updateCbCrebit" id="updateCbCredit" onclick="updateCbCredit('+data.cb_case_id+')" disabled>Amount Credited</button>&nbsp';
									} else {
									  credited= '<button class="btn btn-primary btn-xs"  name="updateCbCrebit" id="updateCbCredit" onclick="updateCbCredit('+data.cb_case_id+')">Amount Credited</button>&nbsp';
									} */
									return merchant + acquirer;
									/* +debited+credited; */

								}
							} 
        	]
      });
    }
			</script>
			<script>

				function renderchargebackTable(data) {

					$("#chargebackdataTable").DataTable().destroy();
					$('#chargebackdataTable').dataTable({
						dom: 'BTftlpi',
						scrollY: true,
						scrollX: true,
						'columnDefs': [{
							'searchable': false
						}],
						"aaData": data,
						"scrollY": true,
						"scrollX": true,
						"bProcessing": true,
						"bLengthChange": true,
						"bAutoWidth": false,
						"iDisplayLength": 10,
						"order": [[0, "asc"]],
						"aoColumns": [{
							"mData": null,
							render: (data, type, row, meta) => meta.row + 1
						}, {
							"mData": "id",
							"visible": false,
							"className": "displayNone"
						}, {
				              "mData": "merchantName"
			            }, {
							"mData": "merchant_pay_id"
						}, {
							"mData": "cb_case_id"
						}, {
							"mData": "txn_amount"
						}, {
							"mData": "pg_case_id"
						}, {
							"mData": "merchant_txn_id"
						}, {
							"mData": "order_id"
						}, {
							"mData": "pgRefNo"
						}, {
							"mData": "bank_txn_id"
						}, {
							"mData": "cb_amount"
						}, {
							"mData": "cb_reason"
						}, {
							"mData": "cb_reason_code"
						}, {
							"mData": "cb_intimation_date"
						}, {
							"mData": "cb_deadline_date"
						}, {
							"mData": "mode_of_payment"
						}, {
							"mData": "acq_name"
						}, {
							"mData": "settlement_date"
						}, {
							"mData": "date_of_txn"
						}, {
							"mData": "customer_name"
						}, {
							"mData": "customer_phone"
						}, {
							"mData": "email"
						}, {
							"mData": "nemail"
						}, {
							"mData": "status"
						}, {
							"mData": null,
							"sClass": "center",
							"mRender": function (data) {

								return '<h1><button class="btn btn-primary btn-xs"  name="edit" id="edit" onclick="getData(' + data.id + ')">Edit</button></h1>';

							}
						}]
					});

				} 
			</script>
			<script type="text/javascript">
				function redirect(id) {
					$("#id").val(id);
					$('#form').submit();
				}

			</script>

			<script>
				$(document).ready(function () {
					var urls = new URL(window.location.href);
					var domain = urls.origin;
					var userType = "<s:property value='%{#session.USER.UserGroup.group}'/>";
					var payId = "<s:property value='%{#session.USER.payId}'/>";
					if (userType != 'Merchant') {
						payId = '';
					}

					$.ajax({
						type: "GET",

						url: domain + "/crmws/listChargebackStatus?payId=" + payId,
						//url :"http://localhost:8080/crmws/list?payId=" + payId,
						success: function (data, status) {
							var response = JSON.parse(JSON.stringify(data));
							if (userType == 'Admin' || userType == 'Sub Admin') {
								renderTable(response);
							}
							else {
								renderchargebackTable(response);
							}
						},
						error: function (status) {
						}
					});

				});
			</script>
			<script>
				function updateCbFavourMerchant(id) {
					// alert(id);
					var urls = new URL(window.location.href);
					var domain = urls.origin;
					$.ajax({
						url: domain + "/crmws/updateCbFavourMerchant/" + id,
						type: 'POST',
						enctype: 'multipart/form-data',
						processData: false,
						contentType: false,
						cache: false,
						success: function (data) {
							alert("Merchant favour status updated successfully!");
							window.location.reload();
						},
						error: function (data, textStatus, jqXHR) {

							if (data.responseText && JSON.parse(data.responseText).respmessage) {
								var responseText = JSON.parse(data.responseText)
								alert(responseText.respmessage);
							} else {
								alert("Merchant favour status not updated successfully!");
							}
							window.location.reload();

						}
					});
					//  redirect(id);
				}
				function updateCbFavourBankAcquirer(id) {
					var urls = new URL(window.location.href);
					var domain = urls.origin;
					$.ajax({
						url: domain + "/crmws/updateCbFavourBankAcquirer/" + id,
						type: 'POST',
						enctype: 'multipart/form-data',
						processData: false,
						contentType: false,
						cache: false,
						success: function (data) {
							alert("Bank/Acquirer favour status updated successfully!");
							window.location.reload();
						},
						error: function (data, textStatus, jqXHR) {

							if (data.responseText && JSON.parse(data.responseText).respmessage) {
								var responseText = JSON.parse(data.responseText)
								alert(responseText.respmessage);
							} else {
								alert("Bank/Acquirer favour status not updated successfully!");
							}
							window.location.reload();

						}
					});
				}
				function updateCbDebit(id) {
					var urls = new URL(window.location.href);
					var domain = urls.origin;
					$.ajax({
						url: domain + "/crmws/updateCbDebit/" + id,
						type: 'POST',
						enctype: 'multipart/form-data',
						processData: false,
						contentType: false,
						cache: false,
						success: function (data) {
							alert("Amount debited status updated successfully!");
							window.location.reload();
						},
						error: function (data, textStatus, jqXHR) {

							if (data.responseText && JSON.parse(data.responseText).respmessage) {
								var responseText = JSON.parse(data.responseText)
								alert(responseText.respmessage);
							} else {
								alert("Amount debited status not updated successfully!");
							}
							window.location.reload();

						}
					});
				}

				function updateCbCredit(id) {
					var urls = new URL(window.location.href);
					var domain = urls.origin;
					$.ajax({
						url: domain + "/crmws/updateCbCredit/" + id,
						type: 'POST',
						enctype: 'multipart/form-data',
						processData: false,
						contentType: false,
						cache: false,
						success: function (data) {
							alert("Amount credited status updated successfully!");
							window.location.reload();
						},
						error: function (data, textStatus, jqXHR) {

							if (data.responseText && JSON.parse(data.responseText).respmessage) {
								var responseText = JSON.parse(data.responseText)
								alert(responseText.respmessage);
							} else {
								alert("Amount credited status not updated successfully!");
							}
							window.location.reload();

						}
					});
				}
			</script>
		</body>

		</html>