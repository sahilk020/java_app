<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib prefix="s" uri="/struts-tags" %>
	<html dir="ltr" lang="en-US">

	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>Merchant Accounts</title>
		<meta charset="utf-8" />
		<meta name="viewport" content="width=device-width, initial-scale=1" />
		<link rel="shortcut icon" href="assets/media/images/paylogo.svg" />
		<!--begin::Fonts-->
		<link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Inter:300,400,500,600,700" />
		<!--end::Fonts-->
		<!--begin::Vendor Stylesheets(used by this page)-->
		<link href="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.css" rel="stylesheet" type="text/css" />
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

		<script type="text/javascript">
			$(document).ready(function () {
				$("#industryTypes").select2();
			});
		</script>
		<style>
			span.select2-selection.select2-selection--single.form-select.form-select-solid.merchantPayId {
				padding: 6px 92px !important;
			}
		</style>
		<style>
			span.select2-selection.select2-selection--single.form-select.form-select-solid.actions {
				/* padding: 9px 0px 25px 40px; */
			}

			.buttons-html5 {
				display: none !important;
			}

			.btn-group {
				display: none !important;
			}
		</style>
	</head>

	<body>
		<div class="content d-flex flex-column flex-column-fluid" id="kt_content">
			<!--begin::Toolbar-->
			<div class="toolbar" id="kt_toolbar">
				<!--begin::Container-->
				<div id="kt_toolbar_container" class="container-fluid d-flex flex-stack">
					<!--begin::Page title-->
					<div data-kt-swapper="true" data-kt-swapper-mode="prepend"
						data-kt-swapper-parent="{default: '#kt_content_container', 'lg': '#kt_toolbar_container'}"
						class="page-title d-flex align-items-center me-3 flex-wrap mb-5 mb-lg-0 lh-1">
						<!--begin::Title-->
						<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">
							Merchant Accounts</h1>
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
							<li class="breadcrumb-item text-muted">Merchant Setup</li>
							<!--end::Item-->
							<!--begin::Item-->
							<li class="breadcrumb-item"><span class="bullet bg-gray-200 w-5px h-2px"></span></li>
							<!--end::Item-->
							<!--begin::Item-->
							<li class="breadcrumb-item text-dark"> Merchant Accounts</li>
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
											<p class="text-center m-0 w-100"><b>Business Type</b></p
												class="text-sm-center m-0">
										</div>
										<div class="col-auto my-2">
											<s:select headerKey="ALL" headerValue="ALL" name="industryTypes"
												id="industryTypes" class="form-select form-select-solid merchantPayId"
												list="industryTypes" value="ALL" onchange="handleChange();" />
										</div>

									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="post d-flex flex-column-fluid" id="kt_post">
				<!--begin::Container-->
				<div id="kt_content_container" class="container-xxl">
					<div style="overflow:scroll !important;">
						<div class="row my-5">
							<div class="col">
								<div class="card">
									<div class="card-body">
										<!--begin::Input group-->
										<div class="row g-9 mb-8 justify-content-end">
											<div class="col-lg-2 col-sm-12 col-md-6">
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
														<a class="toggle-vis" data-column="0">Pay Id</a>
														<a class="toggle-vis" data-column="1">Business Name</a>
														<a class="toggle-vis" data-column="2">Email</a>
														<a class="toggle-vis" data-column="3">Status Type</a>
														<a class="toggle-vis" data-column="4">User Type</a>
														<a class="toggle-vis" data-column="5">Contact</a>
														<a class="toggle-vis" data-column="6">Registration Date</a>
													</div>
												</div>
											</div>
										</div>
										<div class="row g-9 mb-8">
											<table id="datatable"
												class="table table-striped table-row-bordered gy-5 gs-7">
												<thead>
													<tr class="fw-bold fs-6 text-gray-800">
														<th scope="col">Pay Id</th>
														<th scope="col">Business Name</th>
														<th scope="col">Email</th>
														<th scope="col">Status</th>
														<th scope="col">User Type</th>
														<th scope="col">Contact</th>
														<th scope="col">Registration Date</th>
														<th scope="col">Edit</th>
														<!-- <th scope="col">Pay Id</th> -->
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

		<s:form name="merchant" action="merchantSetup">
			<s:hidden name="payId" id="hidden" value="" />
			<s:hidden name="token" value="%{#session.customToken}"></s:hidden>
		</s:form>

		<script type="text/javascript">
			$(document).ready(function () {
				$(function () {
					renderTable();
					enableBaseOnAccess();
				});
			});
			function handleChange() {
				reloadTable();
			}
			function decodeVal(text) {
				return $('<div/>').html(text).text();
			}
			function renderTable() {
				debugger
				var token = document.getElementsByName("token")[0].value;
				/* var buttonCommon = {
					exportOptions: {
						format: {
							body: function (data, column, row, node) {
								// Strip $ from salary column to make it numeric
								if (column == 6) {

								}
								return column === 0 ? "'" + data : column === 2 ? data.replace("&#x40;", "@") : data;
							}
						}
					}
				}; */

				var buttonCommon = {
					    exportOptions: {
					        format: {
					            body: function (data, column, row, node) {
					                // Custom formatting for CSV export
					               
					                    // Convert all columns to strings and remove string quotes
					                    if (typeof data === 'string') {
					                        // Replace '&#x40;' with '@' for emailId column

					                    	if (row === 2) {
					                            return data.replace(/&#x40;|'/g, function(match) {
					                                return match === '&#x40;' ? '@' : ''; // Replace '&#x40;' with '@' and remove single quotes
					                            });
					                        }					                    } else if (data instanceof Date) {
					                        // Remove seconds from the date
					                        return data.toISOString().replace(/:\d{2}\.\d{3}Z$/, 'Z');
					                    } else {
					                        return data;
					                    }
					                }
				
					            }
					       
					    }
					};
				var buttonCommon = {
					    exportOptions: {
					        format: {
					            body: function (data, column, row, node) {
					                // Convert all columns to strings and remove string quotes
					                if (typeof data === 'string') {
					                    // Replace '&#x40;' with '@' for emailId column
					                    if (row === 2) {
					                        return data.replace(/&#x40;|'/g, function(match) {
					                            return match === '&#x40;' ? '@' : ''; // Replace '&#x40;' with '@' and remove single quotes
					                        });
					                    } else {
					                        return data.replace(/'/g, ''); // Remove single quotes from all other string columns
					                    }
					                } else if (data instanceof Date) {
					                    // Remove seconds from the date
					                    return data.toISOString().replace(/:\d{2}\.\d{3}Z$/, 'Z');
					                }
					                // Default formatting for other formats or non-string columns
					                return data;
					            }
					        }
					    }
					};
				$('#datatable').dataTable({
					dom: 'BTftlpi',
					
					'columnDefs': [{
						'searchable': false,
						'targets': [7]
					}],

					buttons: [
						$.extend(true, {}, buttonCommon, {
							extend: 'copyHtml5',
							exportOptions: {
								columns: [':visible :not(:last-child)']
							}
						}),
						$.extend(true, {}, buttonCommon, {
							extend: 'csvHtml5',
							exportOptions: {
								columns: [':visible :not(:last-child)']
							}
						}),
						{
							extend: 'pdfHtml5',
							title: 'Merchant List',
							orientation: 'landscape',
							exportOptions: {
								columns: [':visible :not(:last-child)']
							},
							customize: function (doc) {
								doc.defaultStyle.alignment = 'center';
								doc.styles.tableHeader.alignment = 'center';
							}
						},
						// Disabled print button.
						/* {extend : 'print',title : 'Merchant List',exportOptions : {columns: [':visible :not(:last-child)']}}, */
						{
							extend: 'colvis',
							//collectionLayout: 'fixed two-column',
							columns: [0, 1, 2, 3, 4, 5, 6]
						}],
					"ajax": {
						"url": "merchantDetailsAction",
						"type": "POST",
						"data": generatePostData
					},
					"bProcessing": true,
					"bLengthChange": true,
					"bAutoWidth": false,
					"scrollY":true,
					"scrollX":true,
					"iDisplayLength": 10,
					"order": [[1, "asc"]],
					"aoColumns": [{
						"mData": "payId"
					}, {
						"mData": "businessName"
					}, {
						"mData": "emailId"
					}, {
						"mData": "status"
					}, {
						"mData": "userType"
					}, {
						"mData": "mobile"
					}, {
						"mData": "registrationDate"
					}, {
						"mData": null,
						"sClass": "center",
						"bSortable": false,
						"mRender": function () {
							return '<h1><button class="btn btn-primary btn-xs" disabled="disabled" name="merchantEdit" id="merchantEdit" onclick="ajaxindicatorstart1()">Edit</button></h1>';
						}
					}]
				});
				$(document).ready(function () {
					var table = $('#datatable').DataTable();
					$('#datatable tbody').on('click', 'td', function () {
						var rows = table.rows();
						var columnVisible = table.cell(this).index().columnVisible;
						var rowIndex = table.cell(this).index().row;
						if (columnVisible == 7) {
							var payId = table.cell(rowIndex, 0).data();
							if (payId.length > 20) {
								return false;
							}
							for (var i = 0; i < payId.length; i++) {
								var code = payId.charCodeAt(i);
								if (code < 48 || code > 57) {
									return false;
								}
							}
							document.getElementById('hidden').value = payId;
							if ($("#merchantEdit").attr("disabled") == undefined) {
								document.merchant.submit();
							}
						}
					});
				});
			}
			function reloadTable() {
				var tableObj = $('#datatable');
				var table = tableObj.DataTable();
				table.ajax.reload();
			}
			function generatePostData() {
				var token = document.getElementsByName("token")[0].value;
				var businessType = null;
				if (null != document.getElementById("industryTypes")) {
					businessType = document.getElementById("industryTypes").value;
				} else {
					businessType = 'ALL';
				}
				var obj = {
					token: token,
					businessType: businessType,
				};

				return obj;
			}
			$('#datatable').on('draw.dt', function () {
				enableBaseOnAccess();
			});
			function enableBaseOnAccess() {
				setTimeout(function () {
					if ($('#merchantList').hasClass("active")) {
						var menuAccess = document.getElementById("menuAccessByROLE").value;
						var accessMap = JSON.parse(menuAccess);
						var access = accessMap["merchantList"];
						if (access.includes("Update")) {
							var edits = document.getElementsByName("merchantEdit");
							for (var i = 0; i < edits.length; i++) {
								var edit = edits[i];
								edit.disabled = false;
							}
						}
						if (access.includes("Delete")) {
							var deletes = document.getElementsByName("merchantDelete");
							for (var i = 0; i < deletes.length; i++) {
								var deletesBtn = deletes[i];
								deletesBtn.disabled = false;
							}
						}
					}
				}, 500);
			}
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


		<script src="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.js"></script>
		<script src="https://cdn.amcharts.com/lib/5/index.js"></script>
		<script src="https://cdn.amcharts.com/lib/5/xy.js"></script>
		<script src="https://cdn.amcharts.com/lib/5/percent.js"></script>
		<script src="https://cdn.amcharts.com/lib/5/radar.js"></script>
		<script src="https://cdn.amcharts.com/lib/5/themes/Animated.js"></script>
		<script src="https://cdn.amcharts.com/lib/5/map.js"></script>
		<script src="https://cdn.amcharts.com/lib/5/geodata/worldLow.js"></script>
		<script src="https://cdn.amcharts.com/lib/5/geodata/continentsLow.js"></script>
		<script src="https://cdn.amcharts.com/lib/5/geodata/usaLow.js"></script>
		<script src="https://cdn.amcharts.com/lib/5/geodata/worldTimeZonesLow.js"></script>
		<script src="https://cdn.amcharts.com/lib/5/geodata/worldTimeZoneAreasLow.js"></script>
		<script src="../assets/plugins/custom/datatables/datatables.bundle.js"></script>
		<!--end::Vendors Javascript-->
		<!--begin::Custom Javascript(used by this page)-->
		<script src="../assets/js/widgets.bundle.js"></script>
		<script src="../assets/js/custom/widgets.js"></script>
		<script src="../assets/js/custom/apps/chat/chat.js"></script>
		<script src="../assets/js/custom/utilities/modals/upgrade-plan.js"></script>
		<script src="../assets/js/custom/utilities/modals/create-app.js"></script>
		<script src="../assets/js/custom/utilities/modals/users-search.js"></script>

		<script type="text/javascript">
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

		</script>
	</body>

	</html>