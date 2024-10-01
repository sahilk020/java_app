<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
	<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
	<%@ taglib uri="/struts-tags" prefix="s" %>
		<html dir="ltr" lang="en-US">

		<head>
			<title>Smart Router Audit</title>

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


			<style>
				.dt-buttons {
					display: none;
				}
			</style>

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

						populateDataTable1();
					});


				});


			</script>


			<script type="text/javascript">

				function populateDataTable1() {

					debugger
					//alert("on submit");
					var buttonCommon = {
					    exportOptions: {
					        format: {
					            body: function (data, column, row, node) {
									console.log(data+" "+typeof(data));
					                // Convert all columns to strings and remove string quotes
									if (row === 11) {
					                    // Remove seconds from the date
										console.log(data+" is date")
					                    return new Date(data).toISOString().replace(/:\d{2}\.\d{3}Z$/, 'Z');
					                }
					                // Default formatting for other formats or non-string columns
					                return data;
					            }
					        }
					    }
					};
					var token = document.getElementsByName("token")[0].value;
					var table = $('#smartRouterReportDataTable').DataTable(
						{
							dom: 'BTrftlpi',
							buttons: [
							$.extend(true, {}, buttonCommon, {
								extend: 'copy',
								exportOptions: {
									 columns: ':visible'
								}
								}),

								$.extend(true, {}, buttonCommon, {
								extend: 'csv',
								exportOptions: {
									 columns: ':visible'
								}
								})
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
								"url": "smartRouterAuditAction",
								"type": "POST",
								"data": function (d) {
									return generatePostData(d);

								}
							},
							"bProcessing": true,
							"bLengthChange": true,
							"bDestroy": true,


							"aoColumns": [
								{ data: "id" },
								{ data: "merchantName" },
								{ data: "paymentType" },
								{ data: "mopType" },
								{ data: "region" },
								{ data: "txnType" },
								{ data: "acquirer" },
								{ data: "loadPercent" },
								{ data: "status" },
								{ data: "minValue" },
								{ data: "maxValue" },
								{ data: "updatedDate" },
								{ data: "requestedBy" }
							]

						});


				}

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


		</head>

		<body id="mainBody"
			class="header-fixed header-tablet-and-mobile-fixed toolbar-enabled toolbar-fixed toolbar-tablet-and-mobile-fixed aside-enabled aside-fixed">

			<div class="toolbar" id="kt_toolbar">
				<!--begin::Container-->
				<div id="kt_toolbar_container" class="container-fluid d-flex flex-stack">
					<!--begin::Page title-->
					<div data-kt-swapper="true" data-kt-swapper-mode="prepend"
						data-kt-swapper-parent="{default: '#kt_content_container', 'lg': '#kt_toolbar_container'}"
						class="page-title d-flex align-items-center me-3 flex-wrap mb-5 mb-lg-0 lh-1">
						<!--begin::Title-->
						<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">Smart Router Audit Report</h1>
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
							<li class="breadcrumb-item text-dark">Smart Router Audit Report</li>
							<!--end::Item-->
						</ul>
						<!--end::Breadcrumb-->
					</div>
					<!--end::Page title-->

				</div>
				<!--end::Container-->
			</div>


			<div style="overflow:scroll !important;">

				<div class="post d-flex flex-column-fluid" id="kt_post">
					<!--begin::Container-->
					<div id="kt_content_container" class="container-xxl">
						<div class="row my-5">
							<div class="col">
								<div class="card">
									<div class="card-body">
										<div class="container ">
											<div class="row">

												<div class="col-sm-6 col-lg-3">
													<label
														class="d-flex align-items-center fs-6 fw-semibold mb-2">Merchant
														:
													</label><br>
													<div class="txtnew">
														<s:if
															test="%{#session.USER.UserType.name()=='ADMIN' || #session.USER.UserType.name()=='SUBADMIN' || #session.USER_TYPE.name()=='SUPERADMIN'}">
															<s:select name="merchant"
																class="form-select form-select-solid " id="merchant"
																headerKey="" headerValue="Select Merchant"
																list="merchantList" listKey="payId"
																listValue="businessName" autocomplete="off" />
														</s:if>
														<s:else>
															<s:select name="merchant"
																class="form-select form-select-solid" id="merchant"
																list="merchantList" listKey="payId"
																listValue="businessName" autocomplete="off" />
														</s:else>
													</div>
												</div>
												<div class="col-sm-6 col-lg-3">
													<label
														class="d-flex align-items-center fs-6 fw-semibold mb-2">Payment
														Method
														:</label><br>

													<div class="txtnew">
														<select name="paymentMethod" id="paymentMethod"
															autocomplete="off" class="form-select form-select-solid">
															<option>Select Payment Method</option>
															<option>Credit Card</option>
															<option>Debit Card</option>
															<option>Net Banking</option>
															<option>UPI</option>
															<option>Wallet</option>
														</select>
													</div>
												</div>
												<div class="col-sm-6 col-lg-3">
													<label class="d-flex align-items-center fs-6 fw-semibold mb-2">Card
														Holder Type
														:</label><br>
													<div class="txtnew">
														<select name="cardHolderType" id="cardHolderType"
															autocomplete="off" class="form-select form-select-solid">

															<option>CONSUMER</option>
															<option>COMMERCIAL</option>

														</select>
													</div>
												</div>

												<div class="col-sm-6 col-lg-3">

													<input type="button" id="submit" disabled="disabled" value="Submit"
														class="btn btn-primary  mt-13">

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

			<!-- another one from here -->


			<div class="post d-flex flex-column-fluid" id="kt_post">
				<!--begin::Container-->
				<div id="kt_content_container" class="container-xxl">
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

											</select>
										</div>
										<div class="col-lg-4 col-sm-12 col-md-6">
											<div class="dropdown1">
												<button class="form-select form-select-solid actions dropbtn1">Customize Columns</button>
												<div class="dropdown-content1">
													<a class="toggle-vis" data-column="0">Rule Id</a>
													<a class="toggle-vis" data-column="1">Pay Id</a>
													<a class="toggle-vis" data-column="2">Payment Method</a>
													<a class="toggle-vis" data-column="3">Mop Type</a>
													<a class="toggle-vis" data-column="4">Region</a>
													<a class="toggle-vis" data-column="5">Type</a>
													<a class="toggle-vis" data-column="6">Acquirer</a>
													<a class="toggle-vis" data-column="7">Load</a>
													<a class="toggle-vis" data-column="8">Status</a>
													<a class="toggle-vis" data-column="9">Min Val</a>
													<a class="toggle-vis" data-column="10">Max Val</a>
													<a class="toggle-vis" data-column="11">Updated Date</a>
													<a class="toggle-vis" data-column="12">Requested By</a>
											</div>
											</div>
										</div>
									</div>


									<div class="row g-9 mb-8">

										<div class="table-responsive dataTables_wrapper dt-bootstrap4 no-footer">

											<table id="smartRouterReportDataTable"
												class="table table-striped table-row-bordered gy-5 gs-7" cellspacing="0"
												width="100%">
												<thead>
													<tr class="boxheadingsmall fw-bold fs-6 text-gray-800"
														style="font-size: 11px;">
														<th style="text-align:center;" data-orderable="false">Rule Id
														</th>
														<th style="text-align:center;" data-orderable="false">Pay Id
														</th>
														<th style="text-align:center;" data-orderable="false">Payment
															Method</th>
														<th style="text-align:center;" data-orderable="false">Mop Type
														</th>
														<th style="text-align:center;" data-orderable="false">Region
														</th>
														<th style="text-align:center;" data-orderable="false">Type</th>
														<th style="text-align:center;" data-orderable="false">Acquirer
														</th>
														<th style="text-align:center;" data-orderable="false">Load %
														</th>
														<th style="text-align:center;" data-orderable="false">Status
														</th>
														<th style="text-align:center;" data-orderable="false">Min Val
														</th>
														<th style="text-align:center;" data-orderable="false">Max Val
														</th>
														<th style="text-align:center;" data-orderable="false">Updated Date
														</th>
														<th style="text-align:center;" data-orderable="false">Requested
															By</th>
													</tr>
												</thead>
												<tbody align="center">
													<tr class="odd">
														<td valign="top" colspan="12" class="dataTables_empty">No data
															available in table</td>
													</tr>
												</tbody>
												<tfoot>
													<tr class="boxheadingsmall fw-bold fs-6 text-gray-800">
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
					if ($('#smartRouterAudit').hasClass("active")) {
						var menuAccess = document.getElementById("menuAccessByROLE").value;
						var accessMap = JSON.parse(menuAccess);
						var access = accessMap["smartRouterAudit"];
						if (access.includes("View")) {
							$("#submit").removeAttr("disabled");
						}
					}
				});
			</script>


<script type="text/javascript">
	$('a.toggle-vis').on('click', function (e) {
		debugger
		e.preventDefault();
		var table = $('#smartRouterReportDataTable').DataTable();
		// Get the column API object
		var column1 = table.column($(this).attr('data-column'));
		// Toggle the visibility
		column1.visible(!column1.visible());
		if($(this)[0].classList[1]=='activecustom'){
			$(this).removeClass('activecustom');
		}
		else{
			$(this).addClass('activecustom');
		}
	});


	function myFunction() {
		var x = document.getElementById("actions11").value;
    		if(x=='csv'){
    			document.querySelector('.buttons-csv').click();
    		}
    		if(x=='copy'){
    			document.querySelector('.buttons-copy').click();
    		}
        if(x=='excel'){
          document.querySelector('.buttons-excel').click();
    		}
	
}
	
	</script>

		</body>

		</html>