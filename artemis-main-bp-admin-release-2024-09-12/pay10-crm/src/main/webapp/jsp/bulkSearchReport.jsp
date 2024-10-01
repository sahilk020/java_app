<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
	<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
	<%@ taglib uri="/struts-tags" prefix="s" %>
		<html dir="ltr" lang="en-US">

		<head>
			<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
			<title>Bulk Search transaction</title>


			<link rel="shortcut icon" href="assets/media/images/paylogo.svg" />
			<!--begin::Fonts-->
			<link href="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.css" rel="stylesheet"
				type="text/css" />
			<link href="../assets/plugins/custom/datatables/datatables.bundle.css" rel="stylesheet" type="text/css" />
			<link href="../assets/plugins/global/plugins.bundle.css" rel="stylesheet" type="text/css" />
			<link href="../assets/css/style.bundle.css" rel="stylesheet" type="text/css" />
			<link href="../assets/css/custom/custom-style.css" rel="stylesheet" type="text/css" />
			<script src="../js/loader/main.js"></script>
			<script src="../assets/plugins/global/plugins.bundle.js"></script>
			<script src="../assets/js/scripts.bundle.js"></script>
			<link href="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.css" rel="stylesheet"
				type="text/css" />
			<link href="../assets/plugins/custom/datatables/datatables.bundle.css" rel="stylesheet" type="text/css" />
			<script src="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.js"></script>
			<script src="../assets/plugins/custom/datatables/datatables.bundle.js"></script>
			<script src="../assets/js/widgets.bundle.js"></script>
			<script src="../assets/js/custom/widgets.js"></script>
			<script src="../assets/js/custom/apps/chat/chat.js"></script>
			<script src="../assets/js/custom/utilities/modals/upgrade-plan.js"></script>
			<script src="../assets/js/custom/utilities/modals/create-app.js"></script>
			<script src="../assets/js/custom/utilities/modals/users-search.js"></script>
			<link href="../css/select2.min.css" rel="stylesheet" />
			<script src="../js/jquery.select2.js" type="text/javascript"></script>

			<script type="text/javascript" src="../js/sweetalert.js"></script>
			<link rel="stylesheet" href="../css/sweetalert.css">

			<script type="text/javascript">

				function downloaddata() {
					var rrn = document.getElementById("rrnid").value;
					var pgRefNum = document.getElementById("pgRefNumid").value;
					var orderId = document.getElementById("orderIdid").value;


					document.getElementById("orderIdi").value = orderId;
					document.getElementById("pgRefNumi").value = pgRefNum;
					document.getElementById("rrni").value = rrn;


					document.getElementById("downloadPaymentReport").submit()


				}
				function searchdata() {
					var rrn = document.getElementById("rrnid").value;
					var pgRefNum = document.getElementById("pgRefNumid").value;
					var orderId = document.getElementById("orderIdid").value;

					$.ajax({

						type: "GET",
						url: "BulkDetails",
						data: {
							"rrn": rrn,
							"pgRefNum": pgRefNum,
							"orderId": orderId,



						},
						success: function (
							response) {

							var responseObj = JSON
								.parse(JSON
									.stringify(response));
							if (responseObj.data.length != 0) {
								renderTable(responseObj.data);
							} else {
								alert("no data is found");
								window.location.reload();



							}
						},
					});
				}





				function renderTable(data) {
					var getindex = 0;
					var table = new $.fn.dataTable.Api('#dataTable');

					var buttonCommon = {
						exportOptions: {
							format: {
								body: function (data, column, row, node) {
									// Strip $ from salary column to make it numeric
									return (column === 1 ? "'" + data : data);
								}
							}
						}
					};

					$("#datatable").DataTable().destroy();

					$('#datatable')
						.dataTable(
							{

								'data': data,

								'columns': [

									{
										'data': 'transactionIdString',
										'className': 'tds text-class'
									},

									{

										'data': 'pgRefNum',
										'className': 'tableId  text-class displayNone'
									},
									{
										'data': 'merchants',
										'className': 'batch_No text-class'
									},


									{
										'data': 'dateFrom',
										'className': 'settlement_Date text-class'
									},

									{
										'data': 'orderId',
										'className': 'tds text-class'
									},
									// {
									// 	'data': 'refundOrderId',
									// 	'className': 'tds text-class',
									// 	"visible": false
									// },

									{
										'data': 'mopType',
										'className': 'tds text-class'
									},
									{
										'data': 'paymentMethods',
										'className': 'tds text-class'
									},
									{
										'data': 'txnType',
										'className': 'tds text-class'
									},
									{
										'data': 'status',
										'className': 'tds text-class'
									},
									{
										'data': 'amount',
										'className': 'tds text-class'
									},
									{
										'data': 'customerEmail',
										'className': 'tds text-class'
									},
									{
										'data': 'customerPhone',
										'className': 'tds text-class'
									},
									{
										'data': 'acquirerType',
										'className': 'tds text-class'
									},
									{
										'data': 'ipaddress',
										'className': 'tds text-class'
									},
									{
										'data': 'cardMask',
										'className': 'tds text-class'
									},
									{
										'data': 'rrn',
										'className': 'tds text-class'
									},

									{
										'data': 'cardHolderType',
										'className': 'tds text-class'
									},
									{
										'data': 'transactRef',
										'className': 'tds text-class',
										"visible": false

									},




									{

										"data": null,
										"visible": false,
										"className": "displayNone",
										"mRender": function (row) {
											return "\u0027" + row.id;

										}
									}

								]
							});
				}

			</script>

			<style type="text/css">
				/* .cust {width: 24%!important; margin:0 5px !important; 
			} */
				.samefnew {
					width: 19.5% !important;
					margin: 5px 5px !important;

					.samefnew-btn {
						width: 12%;
						float: left;
						font: bold 11px arial;
						color: #333;
						line-height: 22px;
						margin-top: 5px;
					}

					/*tr td.my_class{color:#000 !important; cursor: default !important; text-decoration: none;}*/

					.cust .form-control,
					.samefnew .form-control {
						margin: 0px !important;
						width: 95%;
					}

					.select2-container {
						width: 100% !important;
					}

					.clearfix:after {
						display: block;
						visibility: hidden;
						line-height: 0;
						height: 0;
						clear: both;
						content: '.';
					}

					#popup {
						position: fixed;
						top: 0px;
						left: 0px;
						background: rgba(0, 0, 0, 0.7);
						width: 100%;
						height: 100%;
						z-index: 999;
						display: none;
					}

					.innerpopupDv {
						width: 600px;
						margin: 60px auto;
						background: #fff;
						padding-left: 5px;
						padding-right: 15px;
						border-radius: 10px;
					}

					.btn-custom {
						margin-top: 5px;
						height: 27px;
						border: 1px solid #5e68ab;
						background: #5e68ab;
						padding: 5px;
						font: bold 12px Tahoma;
						color: #fff;
						cursor: pointer;
						border-radius: 5px;
					}

					#loader-wrapper .loader-section.section-left,
					#loader-wrapper .loader-section.section-right {
						background: rgba(225, 225, 225, 0.6) !important;
						width: 50% !important;
					}

					.invoicetable {
						float: none;
					}

					.innerpopupDv h2 {
						font-size: 12px;
						padding: 5px;
					}

					.my_class1 {
						color: #0040ff !important;
						text-decoration: none !important;
						cursor: pointer;
						*cursor: hand;
					}

					.my_class {
						color: white !important;
					}

					.text-class {
						text-align: center !important;
					}

					.download-btn {
						background-color: #496cb6;
						display: block;
						width: 100%;
						height: 30px;
						padding: 3px 4px;
						font-size: 14px;
						line-height: 1.42857143;
						color: #fff;
						border: 1px solid #ccc;
						border-radius: 4px;
						margin-top: 30px;
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

					#loadingInner {
						width: 100%;
						height: 100%;
						top: 0px;
						left: 0px;
						position: fixed;
						display: block;
						z-index: 99
					}

					#loading-image-inner {
						position: absolute;
						top: 33%;
						left: 48%;
						z-index: 100;
						width: 5%;
					}
			</style>
			<script type="text/javascript">

			</script>
		</head>

		<body id="mainBody"
			class="header-fixed header-tablet-and-mobile-fixed toolbar-enabled toolbar-fixed toolbar-tablet-and-mobile-fixed aside-enabled aside-fixed">

			<s:form id="downloadPaymentReport" name="DownloadBulkDetailsReports" action="DownloadBulkDetailsReports">

				<s:hidden name="orderId" id="orderIdi" value="" />

				<s:hidden name="pgRefNum" id="pgRefNumi" value="" />
				<s:hidden name="rrn" id="rrni" value="" />

			</s:form>
			<div class="content d-flex flex-column flex-column-fluid" id="kt_content">
				<div id="loader-wrapper" style="width: 100%; height: 100%; display:none;">
				</div>
				<div class="toolbar" id="kt_toolbar">
					<!--begin::Container-->
					<div id="kt_toolbar_container" class="container-fluid d-flex flex-stack">
						<!--begin::Page title-->
						<div data-kt-swapper="true" data-kt-swapper-mode="prepend"
							data-kt-swapper-parent="{default: '#kt_content_container', 'lg': '#kt_toolbar_container'}"
							class="page-title d-flex align-items-center me-3 flex-wrap mb-5 mb-lg-0 lh-1">
							<!--begin::Title-->
							<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">Bulk Search transaction
							</h1>
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
								<li class="breadcrumb-item text-muted">Search Payments</li>
								<!--end::Item-->
								<!--begin::Item-->
								<li class="breadcrumb-item"><span class="bullet bg-gray-200 w-5px h-2px"></span>
								</li>
								<!--end::Item-->
								<!--begin::Item-->
								<li class="breadcrumb-item text-dark">Bulk Search Transaction </li>
								<!--end::Item-->
							</ul>
							<!--end::Breadcrumb-->
						</div>
						<!--end::Page title-->

					</div>
					<!--end::Container-->
				</div>
				<div class="post d-flex flex-column-fluid" id="kt_post">
					<div id="kt_content_container" class="container-xxl">


						<div class="row my-5">
							<div class="col">
								<div class="card ">
									<div class="card-body ">
										<div class="container">
											<div class="row">
												<s:form id="resellerPayoutForm">
													<div class="row g-9 mb-8">
														<!--begin::Col-->
														<div class="col-md-4 fv-row fv-plugins-icon-container">
															<label
																class="d-flex align-items-center fs-6 fw-semibold mb-2">
																<span class="">PG Ref Number</span>
															</label>
															<!--end::Label-->
															<!-- <input type="text" class="form-control form-control-solid" name="pgrefnumber"> -->

															<!-- <s:textfield id="pgRefNum" class="form-control form-control-solid"
													name="pgRefNum" type="text" value="" autocomplete="off"
													maxlength="16" onblur="validPgRefNum();" ondrop="return false;" onKeyDown="if(event.keyCode === 32)return false;" onpaste="removeSpaces(this);"></s:textfield> -->
															<s:textfield type="text" min="0"
																class="form-control form-control-solid" id="pgRefNumid"
																name="pgRefNumid" />

														</div>
														<div class="col-md-4 fv-row">
															<label
																class="d-flex align-items-center fs-6 fw-semibold mb-2">
																<span class="">Order ID</span>
															</label>

															<s:textfield type="text"
																class="form-control form-control-solid" id="orderIdid"
																name="orderIdid" />

														</div>


														<div class="col-md-4 fv-row">
															<label
																class="d-flex align-items-center fs-6 fw-semibold mb-2">
																<span class="">RRN</span>
															</label>
															<s:textfield type="text"
																class="form-control form-control-solid" id="rrnid"
																name="rrnid" />

														</div>




													</div>

													<br>
													<div class="col-md-4 fv-row">
														<div class="txtnew">
															<input type="button" id="submit" value="Search"
																onClick="searchdata()" class="btn btn-primary" />
															<input type="button" id="download" value="download"
																onClick="downloaddata()" class="btn btn-primary" />
														</div>
													</div>
												</s:form>
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
										<div style="overflow: scroll !important;">

											<br /> <br />
											<div class="scrollD">
												<table id="datatable"
													class="table table-striped table-row-bordered gy-5 gs-7"
													cellspacing="0" width="100%">
													<thead>
														<tr class="fw-bold fs-6 text-gray-800">
															<th> Txn ID </th>
															<th>Pg Ref Num</th>
															<th>Merchant</th>
															<th>Date</th>
															<th>OrderId</th>
															<!-- <th>Refund OrderId </th> -->
															<th>Mop Type</th>

															<th>Payment Method</th>
															<th>Txn Type</th>
															<th>Status</th>
															<th>Base Amount </th>
															<th>Customer Email </th>
															<th>Customer Ph Number</th>
															<th>Acquirer Type </th>
															<th>Ip Address </th>
															<th>card Mask </th>
															<th>RRN Number </th>
															<th>Card Holder Type </th>
															<th>Transaction Ref Number </th>

															<th>Create By</th>


															<th>
															</th>
														</tr>
													</thead>
												</table>
											</div>
											<!-- </td>
				</tr>
			</table> -->

										</div>
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