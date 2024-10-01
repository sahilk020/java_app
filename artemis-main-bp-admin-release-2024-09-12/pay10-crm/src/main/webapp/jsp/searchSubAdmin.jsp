<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
	<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
	<%@ taglib uri="/struts-tags" prefix="s" %>
		<html dir="ltr" lang="en-US">

		<head>
			<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
			<title>Search SubAdmin</title>

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
			   
				.displayNone {
					display: none;
				}

				.dt-buttons {
					display: none;
				}


				.btn:focus {
					outline: 0 !important;
				}
			</style>


			<script>
				$(document).ready(function () {

					populateDataTable1();
					enableBaseOnAccess();
					$("#submit").click(
						function (env) {
							$('#searchAgentDataTable').empty();
							populateDataTable1();

						});
				});

			</script>

			<script type="text/javascript">
				function decodeVal(text) {
					return $('<div/>').html(text).text();
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
			<script>
				$(function () {
					$(':text').on('input', function () {
						if ($(':text').filter(function () { return !!this.value; }).length > 0) {
							$('#searchButton').prop('disabled', false);
						} else {
							$('#searchButton').prop('disabled', true);
						}
					});
				});
			</script>
		</head>

		<body>

			<div class="content d-flex flex-column flex-column-fluid" id="kt_content">
				<div>
					<span align="left">
						<s:actionmessage />
					</span>
				</div>

				<!--begin::Toolbar-->
				<div class="toolbar" id="kt_toolbar">
					<!--begin::Container-->
					<div id="kt_toolbar_container" class="container-fluid d-flex flex-stack">
						<!--begin::Page title-->
						<div data-kt-swapper="true" data-kt-swapper-mode="prepend"
							data-kt-swapper-parent="{default: '#kt_content_container', 'lg': '#kt_toolbar_container'}"
							class="page-title d-flex align-items-center me-3 flex-wrap mb-5 mb-lg-0 lh-1">
							<!--begin::Title-->
							<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">Sub-Admin List</h1>
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
								<li class="breadcrumb-item text-muted">Manage Users</li>
								<!--end::Item-->
								<!--begin::Item-->
								<li class="breadcrumb-item">
									<span class="bullet bg-gray-200 w-5px h-2px"></span>
								</li>
								<!--end::Item-->
								<!--begin::Item-->
								<li class="breadcrumb-item text-dark"> Sub-Admin List</li>
								<!--end::Item-->
							</ul>
							<!--end::Breadcrumb-->
						</div>
						<!--end::Page title-->

					</div>
					<!--end::Container-->
				</div>
				<!--end::Toolbar-->


				<div class="post d-flex flex-column-fluid" id="kt_post">
					<!--begin::Container-->
					<div id="kt_content_container" class="container-xxl">
						<div class="row my-5">
							<div class="col">
								<div class="card">
									<div class="card-body">
										<div class="row g-9 mb-8">
											<div >
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
															<button
																class="form-select form-select-solid actions dropbtn1">Customize
																Columns</button>
															<div class="dropdown-content1">
																<a class="toggle-vis" data-column="0">Email</a>
																<a class="toggle-vis" data-column="1">First Name</a>
																<a class="toggle-vis" data-column="2">Last Name</a>
																<a class="toggle-vis" data-column="3">Phone</a>
																<a class="toggle-vis" data-column="4">Is Active</a>
																<a class="toggle-vis" data-column="5">User Group</a>
																<a class="toggle-vis" data-column="6">Segment Type</a>
																<a class="toggle-vis" data-column="7">Registration Date</a>
															</div>
														</div>
													</div>
												</div>

												<table	id="searchAgentDataTable"class="table table-striped table-row-bordered gy-5 gs-7">
													<thead>
														<tr class=" fw-bold fs-6 text-gray-800">
															<th>Email</th>
															<th>First Name</th>
															<th>Last Name</th>
															<th>Phone</th>
															<th>Is Active</th>
															<th>User Group</th>
															<th>Segment Type</th>
															<th>Registration Date</th>
															<th>Action</th>
															<th></th>
														</tr>
													</thead>
													<tbody>
														<tr class="odd">
														<td valign="top" colspan="12" class="dataTables_empty">No data available in table</td>
														</tr>
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


				<s:form name="agentDetails" action="editAgentCallAction">
					<s:hidden name="emailId" id="emailAddress" value="" />
					<s:hidden name="firstName" id="firstName" value="" />
					<s:hidden name="lastName" id="lastName" value="" />
					<s:hidden name="mobile" id="mobile" value="" />
					<s:hidden name="isActive" id="isActive" value="" />
					<s:hidden name="needToShowAcqFieldsInReport" id="needToShowAcqFieldsInReport" value="" />
					<s:hidden name="token" value="%{#session.customToken}"></s:hidden>
				</s:form>


			</div>

			<script type="text/javascript">
				$('#searchAgentDataTable').on('draw.dt', function () {
					enableBaseOnAccess();
				});

				function enableBaseOnAccess() {
					setTimeout(function () {
						if ($('#searchSubAdmin').hasClass("active")) {
							var menuAccess = document.getElementById("menuAccessByROLE").value;
							var accessMap = JSON.parse(menuAccess);
							var access = accessMap["searchSubAdmin"];
							if (access.includes("Update")) {
								var edits = document.getElementsByName("subAdminEdit");
								for (var i = 0; i < edits.length; i++) {
									var edit = edits[i];
									edit.disabled = false;
								}
							}
						}
					}, 500);
				}
			</script>

			<script type="text/javascript">
				$('a.toggle-vis').on('click', function (e) {
					debugger
					e.preventDefault();
					var table = $('#searchAgentDataTable').DataTable();
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

				}
			</script>

			<script type="text/javascript">
				function populateDataTable1() {
					debugger
					var token = document.getElementsByName("token")[0].value;

					var table = $('#searchAgentDataTable')
						.DataTable(
							{
								dom: 'Brtip',
								buttons: [
									{
										extend: 'csv',
										exportOptions: {
										//	columns: [0,1,2,3,4,5,6,7]
											columns: [':visible :not(:last-child)']
										}
									},
									{
										extend: 'copy',
										exportOptions: {
										//	columns: [0,1,2,3,4,5,6,7]
											columns: [':visible :not(:last-child)']
										}
									},
									'colvis', 'copy', 'csv', 'excel', 'pdf', 'print',
								],
								scrollY: true,
								scrollX: true,
								searchDelay: 500,
								processing: false,
								dom: 'Brtipl',
								paging: true,
								order: [[7, 'desc']],
								"ajax": {
									"url": "searchSubAdminAction",
									"type": "POST",
									"data": {
										token: token,
										"struts.token.name": "token",
									},
								},
								"bProcessing": true,
								"bLengthChange": true,
								"bDestroy": true,


								"aoColumns": [
									{ data: "agentEmailId" },
									{ data: "agentFirstName" },
									{ data: "agentLastName" },
									{ data: "agentMobile" },
									{ data: "agentIsActive" },
									{ data: "groupName" },
									{ data: "segment" },
									{ data: "registrationDate" },
									
									{
										data: null, render: function () {
											return '<button class="btn btn-primary btn-xs" disabled="disabled" name="subAdminEdit" id="subAdminEdit">Edit</button>';
										}
									},
									{ data: "payId", visible: false }
								]

							});

							

					$(function () {

						var table = $('#searchAgentDataTable').DataTable();
						$('#searchAgentDataTable tbody')
							.on(
								'click',
								'td',
								function () {

									var columnVisible = table.cell(this).index().columnVisible;
									var rowIndex = table.cell(this).index().row;
									var row = table.row(rowIndex).data();

									var emailAddress = table.cell(rowIndex, 0).data();
									var firstName = table.cell(rowIndex, 1).data();
									var lastName = table.cell(rowIndex, 2).data();
									var mobile = table.cell(rowIndex, 3).data();
									var isActive = table.cell(rowIndex, 4).data();

									if (columnVisible == 8) {
										document.getElementById('emailAddress').value = decodeVal(emailAddress);
										document.getElementById('firstName').value = firstName;
										document.getElementById('lastName').value = lastName;
										document.getElementById('mobile').value = mobile;
										document.getElementById('isActive').value = isActive;
										if ($("#subAdminEdit").attr("disabled") == undefined) {
											document.agentDetails.submit();
										}
									}
								});
					});

				}

			</script>
			
		</body>

		</html>