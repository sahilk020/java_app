<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
	<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
	<%@ taglib uri="/struts-tags" prefix="s" %>
		<html dir="ltr" lang="en-US">

		<head>
			<title>Rule Engine Audit</title>

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

			<script type="text/javascript">
				$(document).ready(function () {

					// Initialize select2
					$("#merchant").select2();
					$("#paymentMethod").select2();
					$("#cardHolderType").select2();
				});
			</script>

			<script type="text/javascript">
				$(document).ready(function () {

					renderTable();
					$("#submit").click(function (env) {

						var merchantVal = document.getElementById("merchant").value;
						if (merchantVal == null || merchantVal == "" || merchantVal == "Select Merchant") {
							alert("Please Select Merchant");
							return false;
						}
						var paymentMethod = document.getElementById("paymentMethod").value;
						if (paymentMethod == null || paymentMethod == "" || paymentMethod == "Select Payment Method") {
							alert("Please Select Payment Method");
							return false;
						}
						$('#loader-wrapper').show();
						populateDataTable1();
					});

					// $(function () {

					// 	var table = $('#smartRouterReportDataTable').DataTable();
					// 	$('#smartRouterReportDataTable').on('click', 'td.my_class1', function () {
					// 		var rowIndex = table.cell(this).index().row;
					// 		var rowData = table.row(rowIndex).data();

					// 	});
					// });
				});

				function renderTable() {
					var merchantaPayId = document.getElementById("merchant").value;

					var table = new $.fn.dataTable.Api('#smartRouterReportDataTable');
					var token = document.getElementsByName("token")[0].value;
					//$('#loader-wrapper').hide();

					var buttonCommon = {
						exportOptions: {
							format: {
								body: function (data, column, row, node) {
									// Strip $ from salary column to make it numeric
									return column === 0 ? "'" + data : (column === 1 ? "'" + data : data);
								}
							}
						}
					};

					$('#smartRouterReportDataTable').dataTable(
						{
							"footerCallback": function (row, data, start, end, display) {
								var api = this.api(), data;

								// Remove the formatting to get integer data for summation
								var intVal = function (i) {
									return typeof i === 'string' ? i.replace(/[\,]/g, '') * 1 : typeof i === 'number' ? i : 0;
								};

							},
							"columnDefs": [{
								className: "dt-body-right",
								"targets": [1, 2, 3, 4, 5, 6, 7, 8, 9]
							}],
							dom: 'BTrftlpi',
							buttons: [
								$.extend(true, {}, buttonCommon, {
									extend: 'copyHtml5',
									exportOptions: {
										columns: [':visible']
									},
								}),
								$.extend(true, {}, buttonCommon, {
									extend: 'csvHtml5',
									title: 'Search Transactions',
									exportOptions: {

										columns: [':visible']
									},
								}),
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
										doc.defaultStyle.alignment = 'center';
										doc.styles.tableHeader.alignment = 'center';
									}
								},
								{
									extend: 'print',
									//footer : true,
									title: 'Search Transactions',
									exportOptions: {
										columns: [':visible']
									}
								},
								{
									extend: 'colvis',
									columns: [0, 1, 2, 3, 4, 5, 7, 8, 9]
								}],

							"ajax": {

								"url": "ruleEngineAuditAction",
								"type": "POST",
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
							"serverSide": false,
							"paginationType": "full_numbers",
							"lengthMenu": [[10, 25, 50], [10, 25, 50]],
							"order": [[2, "desc"]],

							"columnDefs": [
								{
									"type": "html-num-fmt",
									"targets": 4,
									"orderable": true,
									"targets": [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
								}
							],

							"columns": [{ "data": "id" },
							{ "data": "merchantName" },
							{ "data": "acquirer" },
							{ "data": "txnType" },
							{ "data": "currency" },
							{ "data": "mopType" },
							{ "data": "paymentType" },
							{ "data": "region" },
							{ "data": "status" },
							{ "data": "createdDate" },
							{ "data": "requestedBy" }]
						});
				}

				// function reloadTable() {
				// 	$("#submit").attr("disabled", true);
				// 	var tableObj = $('#smartRouterReportDataTable');
				// 	var table = tableObj.DataTable();
				// 	table.ajax.reload();
				// }

				function generatePostData(d) {
					var token = document.getElementsByName("token")[0].value;
					var merchantEmailId = document.getElementById("merchant").value;
					var cardHolderType = document.getElementById("cardHolderType").value;
					var paymentType = document.getElementById("paymentMethod").value;

					var obj = {

						merchantPayId: merchantEmailId,
						cardHolderType: cardHolderType,
						paymentType: paymentType,

						draw: d.draw,
						length: d.length,
						start: d.start,
						token: token,
						"struts.token.name": "token",
					};
					return obj;
				}

			</script>

			<style type="text/css">
				/*tr td.my_class{color:#000 !important; cursor: default !important; text-decoration: none;}*/
				tr td.my_class1 {
					cursor: pointer;
					text-decoration: none !important;
				}

				tr td.my_class1:hover {
					cursor: pointer !important;
					text-decoration: none !important;
				}

				tr th.my_class1:hover {
					color: #fff !important;
				}

				#loader-wrapper .loader-section.section-left,
				#loader-wrapper .loader-section.section-right {
					background: rgba(225, 225, 225, 0.6) !important;
					width: 50% !important;
				}


				table.dataTable.display tbody tr.odd {
					background-color: #e6e6ff !important;
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



				table.display thead th {
					padding: 6px 3px 6px 6px !important;
				}
			</style>

			<style>
				.dt-buttons {
					display: none;
				}
			</style>
		</head>

		<body id="mainBody">


			<!--begin::Toolbar-->
			<div class="toolbar" id="kt_toolbar">
				<!--begin::Container-->
				<div id="kt_toolbar_container" class="container-fluid d-flex flex-stack">
					<!--begin::Page title-->
					<div data-kt-swapper="true" data-kt-swapper-mode="prepend"
						data-kt-swapper-parent="{default: '#kt_content_container', 'lg': '#kt_toolbar_container'}"
						class="page-title d-flex align-items-center me-3 flex-wrap mb-5 mb-lg-0 lh-1">
						<!--begin::Title-->
						<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">Rule Engine Audit Report</h1>
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
							<li class="breadcrumb-item text-muted">Merchant Setup</li>
							<!--end::Item-->
							<!--begin::Item-->
							<li class="breadcrumb-item">
								<span class="bullet bg-gray-200 w-5px h-2px"></span>
							</li>
							<!--end::Item-->
							<!--begin::Item-->
							<li class="breadcrumb-item text-dark">Rule Engine Audit Report</li>
							<!--end::Item-->
						</ul>
						<!--end::Breadcrumb-->
					</div>
					<!--end::Page title-->

				</div>
				<!--end::Container-->
			</div>
			<!--end::Toolbar-->

			<div style="overflow:scroll !important;">

				<!-- <table id="mainTable" width="100%" border="0" align="center" cellpadding="0" cellspacing="0"
					class="txnf"> -->


				<div class="post d-flex flex-column-fluid" id="kt_post">
					<!--begin::Container-->
					<div id="kt_content_container" class="container-xxl">

						<div class="">
							<div class="">
								<div class="row my-5">
									<div class="col">
										<div class="card">
											<div class="card-body">
												<!--begin::Input group-->
												<div class="row g-9 mb-8">
													<div class="col-sm-6 col-lg-3">
														<label
															class="d-flex align-items-center fs-6 fw-semibold mb-2">Merchant
														</label><br>
														<div class="">
															<s:if
																test="%{#session.USER.UserType.name()=='ADMIN' || #session.USER.UserType.name()=='SUBADMIN' || #session.USER_TYPE.name()=='SUPERADMIN'}">
																<s:select name="merchant"
																	class="form-select form-select-solid adminMerchants"
																	id="merchant" headerKey=""
																	headerValue="Select Merchant" list="merchantList"
																	listKey="payId" listValue="businessName"
																	autocomplete="off" />
															</s:if>
															<s:else>
																<s:select name="merchant"
																	class="select2 select2-container form-select form-select-solid adminMerchants"
																	id="merchant" list="merchantList" listKey="payId"
																	listValue="businessName" autocomplete="off" />
															</s:else>
														</div>
													</div>
													<div class="col-sm-6 col-lg-3">
														<label
															class="d-flex align-items-center fs-6 fw-semibold mb-2">Payment
															Method </label><br>

														<div class="">
															<select name="paymentMethod" id="paymentMethod"
																autocomplete="off"
																class="form-select form-select-solid merchant">
																<option>Select Payment Method</option>
																<option>Credit Card</option>
																<option>Debit Card</option>
																<option>Net Banking</option>
																<option>UPI</option>
																<option>QR CODE</option>
																<option>WALLET</option>
																<option>EMI</option>
															</select>
														</div>
													</div>
													<div class="col-sm-6 col-lg-3">
														<label
															class="d-flex align-items-center fs-6 fw-semibold mb-2">Card
															Holder Type </label><br>
														<div class="">
															<select name="cardHolderType" id="cardHolderType"
																autocomplete="off"
																class="form-select form-select-solid merchant">

																<option>CONSUMER</option>
																<option>COMMERCIAL</option>

															</select>
														</div>
													</div>





													<div class="col-sm-6 col-lg-3">

														<input type="button" id="submit" value="Submit"
															class="btn btn-primary submit_btn disabled mt-13">


													</div>




												</div>
											</div>


										</div>
									</div>
								</div>
							</div>



							<!-- <div class="samefnew-btn">
						&nbsp;<br>
						<div class="txtnew">
							<input type="button" id="submit" value="Submit"
								class="btn btn-sm btn-block btn-success">

						</div>
					</div> -->
						</div>


						<td align="left" style="padding: 10px;">
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
															data-hide-search="true" onchange="myFunction();">
															<option value="">Actions</option>
															<option value="copy">Copy</option>
															<option value="csv">CSV</option>
														</select>
													</div>
													<div class="col-lg-4 col-sm-12 col-md-6">
														<div class="dropdown1">
															<button class="form-select form-select-solid actions dropbtn1">Customize Columns</button>
															<div class="dropdown-content1">
														<a class="toggle-vis" data-column="0">Rule Id</a>
														<a class="toggle-vis" data-column="1">Pay Id</a>
														<a class="toggle-vis" data-column="2">Acquirer Map</a>
														<a class="toggle-vis" data-column="3">Card Type</a>
														<a class="toggle-vis" data-column="4">Currency</a>
														<a class="toggle-vis" data-column="5">Mop Type</a>
														<a class="toggle-vis" data-column="6">Payment Type</a>
														<a class="toggle-vis" data-column="7">Payment Region</a>
														<a class="toggle-vis" data-column="8">Status</a>
														<a class="toggle-vis" data-column="9">Updated Date</a>
														<a class="toggle-vis" data-column="10">Updated by</a>

														</div>
														</div>
													</div>
												</div>

												<div class="">

													<div class="row g-9 mb-8">
														<div
															class="table-responsive dataTables_wrapper dt-bootstrap4 no-footer">
															<table id="smartRouterReportDataTable"
																class="display nowrap table table-striped table-row-bordered gy-5 gs-7"
																cellspacing="0" width="100%">
																<thead>
																	<tr
																		class="boxheadingsmall fw-bold fs-6 text-gray-800">
																		<th style="text-align:center;"
																			data-orderable="false">Rule Id</th>
																		<th style="text-align:center;"
																			data-orderable="false">Pay Id</th>
																		<th style="text-align:center;"
																			data-orderable="false">Acquirer Map</th>
																		<th style="text-align:center;"
																			data-orderable="false">Card Type</th>
																		<th style="text-align:center;"
																			data-orderable="false">Currency</th>
																		<th style="text-align:center;"
																			data-orderable="false">Mop Type</th>
																		<th style="text-align:center;"
																			data-orderable="false">Payment Type</th>
																		<th style="text-align:center;"
																			data-orderable="false">Payment Region</th>
																		<th style="text-align:center;"
																			data-orderable="false">Status</th>
																		<th style="text-align:center;"
																			data-orderable="false">Updated Date</th>
																		<th style="text-align:center;"
																			data-orderable="false">Requested By</th>
																	</tr>
																</thead>
																<tfoot>
																	<tr class="boxheadingsmall ">
																		<th></th>
																		<th></th>
																		<th></th>
																		<th></th>
																		<th></th>
																		<th></th>
																		<th></th>
																		<th></th>
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
							</div>



							<script type="text/javascript">
								$(document).ready(function () {
									if ($('#ruleEngineAudit').hasClass("active")) {
										var menuAccess = document.getElementById("menuAccessByROLE").value;
										var accessMap = JSON.parse(menuAccess);
										var access = accessMap["ruleEngineAudit"];
										if (access.includes("View")) {
											$("#submit").removeClass("disabled");
										}
									}
								});
							</script>


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



<script type="text/javascript">

function populateDataTable1() {

		debugger
		//alert("on submit");
		var token = document.getElementsByName("token")[0].value;
		table = $('#smartRouterReportDataTable')
			.DataTable(
				{
					dom: 'Brtip',
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
					"ajax": {
						"url": "ruleEngineAuditAction",
						"type": "POST",
						"data": function (d) {
							return generatePostData(d);

						}
					},
					"bProcessing": true,
					"bLengthChange": true,
					"bDestroy": true,


					"aoColumns": [{ "data": "id" },
							{ data: "merchantName" },
							{ data: "acquirer" },
							{ data: "txnType" },
							{ data: "currency" },
							{ data: "mopType" },
							{ data: "paymentType" },
							{ data: "region" },
							{ data: "status" },
							{ data: "createdDate" },
							{ data: "requestedBy" }]
					
				});


	}



	function generatePostData(d) {
		var token = document.getElementsByName("token")[0].value;
		var merchantEmailId = document.getElementById("merchant").value;
		var	cardHolderType = document.getElementById("cardHolderType").value;
		var	paymentType = document.getElementById("paymentMethod").value;
		
		var obj = {
		
			merchantPayId : merchantEmailId,
			cardHolderType : cardHolderType,
			paymentType : paymentType,
		
			draw : d.draw,
			length :d.length,
			start : d.start, 
			token : token,
			"struts.token.name" : "token",
		};
		return obj;
	}


</script>

		</body>

		</html>