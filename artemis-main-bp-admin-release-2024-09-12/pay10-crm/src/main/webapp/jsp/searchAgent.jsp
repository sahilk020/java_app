<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="/struts-tags" prefix="s"%>
<html dir="ltr" lang="en-US">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Search Agent</title>


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
<script src="../js/jquery.select2.js" type="text/javascript"></script>








<%-- 
<link href="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.css" rel="stylesheet" type="text/css" />
<link href="../assets/plugins/custom/datatables/datatables.bundle.css" rel="stylesheet" type="text/css" />
<link href="../assets/plugins/global/plugins.bundle.css" rel="stylesheet" type="text/css" />
<link href="../assets/css/style.bundle.css" rel="stylesheet" type="text/css" />
<link href="../assets/css/custom/custom-style.css" rel="stylesheet" type="text/css" />

<!-- <link href="../css/Jquerydatatableview.css" rel="stylesheet" />
<link href="../css/Jquerydatatable.css" rel="stylesheet" />
<link href="../css/default.css" rel="stylesheet" type="text/css" />
<link href="../css/jquery-ui.css" rel="stylesheet" /> -->

<script type="text/javascript" src="../js/jquery.min.js"></script>
<script src="../js/jquery.js"></script>

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
.displayNone {
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

.dt-buttons.btn-group.flex-wrap {
	display: none;
}
</style>
<script type="text/javascript">
	function decodeVal(text) {
		return $('<div/>').html(text).text();
	}
	$(document).ready(function() {
		populateDataTable();

		$("#submit").click(function(env) {
			/* var table = $('#authorizeDataTable')
					.DataTable(); */
			$('#searchAgentDataTable').empty();

			populateDataTable();

		});
	});

	function populateDataTable() {
		var token = document.getElementsByName("token")[0].value;
		$('#searchAgentDataTable')
				.DataTable(
						{
							dom : 'BTftlpi',
							buttons : [
									{
										extend : 'copyHtml5',
										exportOptions : {
											columns : [ ':visible :not(:last-child)' ]
										}
									},
									{
										extend : 'csvHtml5',
										title : 'Search User',
										exportOptions : {
											columns : [ ':visible :not(:last-child)' ]
										}
									},
									{
										extend : 'pdfHtml5',
										title : 'Search User',
										exportOptions : {
											columns : [ ':visible :not(:last-child)' ]
										},
										customize : function(doc) {
											//doc.content[1].table.widths = Array(doc.content[1].table.body[0].length + 1).join('*').split('');
											doc.defaultStyle.alignment = 'center';
											doc.styles.tableHeader.alignment = 'center';
										}
									},
									{
										extend : 'print',
										title : 'Search User',
										exportOptions : {
											columns : [ ':visible :not(:last-child)' ]
										}
									}, {
										extend : 'colvis',
										//           collectionLayout: 'fixed two-column',
										columns : [ 0, 1, 2, 3, 4 ]
									} ],
							"ajax" : {
								"url" : "searchAgentAction",
								"type" : "POST",
								"data" : {

									token : token,
									"struts.token.name" : "token",
								}
							},
							"bProcessing" : true,
							"bLengthChange" : true,
							"bDestroy" : true,
							"iDisplayLength" : 10,
							"order" : [ [ 1, "desc" ] ],
							"aoColumns" : [
									{
										"mData" : "agentEmailId",
										"sWidth" : '25%',
									},
									{
										"mData" : "agentFirstName",
										"sWidth" : '20%'
									},
									{
										"mData" : "agentLastName",
										"sWidth" : '20%'
									},
									{
										"mData" : "agentMobile",
										"sWidth" : '20%'
									},
									{
										"mData" : "agentIsActive",
										"sWidth" : '10%'
									},
									{
										"mData" : null,
										"sClass" : "center",
										"bSortable" : false,
										"mRender" : function() {
											return '<button class="btn btn-primary" id="agentEdit" disabled="disabled" name="agentEdit" >Edit</button>';
										}
									}, {
										"mData" : "payId",
										"sWidth" : '25%',
										"visible" : false,
									} ]
						});

		$(function() {

			var table = $('#searchAgentDataTable').DataTable();
			$('#searchAgentDataTable tbody')
					.on(
							'click',
							'td',
							function() {

								var columnVisible = table.cell(this).index().columnVisible;
								var rowIndex = table.cell(this).index().row;
								var row = table.row(rowIndex).data();

								var emailAddress = table.cell(rowIndex, 0)
										.data();
								var firstName = table.cell(rowIndex, 1).data();
								var lastName = table.cell(rowIndex, 2).data();
								var mobile = table.cell(rowIndex, 3).data();
								var isActive = table.cell(rowIndex, 4).data();

								if (columnVisible == 5) {
									document.getElementById('emailAddress').value = decodeVal(emailAddress);
									document.getElementById('firstName').value = firstName;
									document.getElementById('lastName').value = lastName;
									document.getElementById('mobile').value = mobile;
									document.getElementById('isActive').value = isActive;
									if ($("#agentEdit").attr("disabled") == undefined) {
										document.agentDetails.submit();
									}
								}
							});
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
<body id="kt_body"
	class="header-fixed header-tablet-and-mobile-fixed toolbar-enabled toolbar-fixed toolbar-tablet-and-mobile-fixed aside-enabled aside-fixed"
	style="-kt-toolbar-height: 55px; - -kt-toolbar-height-tablet-and-mobile: 55px">
	<div class="content d-flex flex-column flex-column-fluid"
		id="kt_content">
		<div class="toolbar" id="kt_toolbar">
			<!--begin::Container-->
			<div id="kt_toolbar_container"
				class="container-fluid d-flex flex-stack">
				<!--begin::Page title-->
				<div data-kt-swapper="true" data-kt-swapper-mode="prepend"
					data-kt-swapper-parent="{default: '#kt_content_container', 'lg': '#kt_toolbar_container'}"
					class="page-title d-flex align-items-center me-3 flex-wrap mb-5 mb-lg-0 lh-1">
					<!--begin::Title-->
					<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">Agent
						List</h1>
					<!--end::Title-->
					<!--begin::Separator-->
					<span class="h-20px border-gray-200 border-start mx-4"></span>
					<!--end::Separator-->
					<!--begin::Breadcrumb-->
					<ul
						class="breadcrumb breadcrumb-separatorless fw-semibold fs-7 my-1">
						<!--begin::Item-->
						<li class="breadcrumb-item text-muted"><a href="home"
							class="text-muted text-hover-primary">Dashboard</a></li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item"><span
							class="bullet bg-gray-200 w-5px h-2px"></span></li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item text-muted">Manage Users</li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item"><span
							class="bullet bg-gray-200 w-5px h-2px"></span></li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item text-dark">Agent List</li>
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
						<div class="card">
							<div class="card-body">
							<div class="row g-9 mb-8 justify-content-end">
												<div class="col-lg-3 col-sm-12 col-md-6">
													<select name="currency" data-control="select2"
														data-placeholder="Actions" id="actions11"
														class="form-select form-select-solid actions dropbtn1"
														data-hide-search="true" onchange="myFunctions();">
														<option value="">Actions</option>
														<option value="copy">Copy</option>
														<option value="csv">CSV</option>
														<option value="pdf">PDF</option>
														<option value="print">PRINT</option>
													</select>
												</div>
												<div class="col-lg-4 col-sm-12 col-md-6">
													<div class="dropdown1">
														<button
															class="form-select form-select-solid actions dropbtn1">Customize
															Columns</button>
														<div class="dropdown-content1">
															<a class="toggle-vis" data-column="0">Email</a> <a
																class="toggle-vis" data-column="1">First Name</a> <a
																class="toggle-vis" data-column="2">Last Name</a> <a
																class="toggle-vis" data-column="3">Phone</a> <a
																class="toggle-vis" data-column="4">Is Active</a> <a
																class="toggle-vis" data-column="5">Action</a>

														</div>
													</div>
												</div>
											</div>
											
				<div class="row my-3 align-items-center">
				<table width="100%" align="left" cellpadding="0" cellspacing="0" class="txnf">
						<tr>
							<td align="left"><s:actionmessage /></td>
								</tr>
										
										<!-- <tr> -->

											
				<!-- <td align="left"> -->
				
				<%-- <table width="100%" border="0"	align="center" cellpadding="0" cellspacing="0">
					<tr>
					<td colspan="5" align="left" valign="top">&nbsp;</td>
					</tr>
					<tr>
					<td colspan="5" align="center" valign="top">
						<table	width="100%" border="0" cellpadding="0" cellspacing="0">
																<tr>
									<td width="1%" align="left" valign="middle" class="labelfont">&nbsp;</td>
									<td width="" align="left" valign="middle" class="labelfont">Email</td>									
									<td width="2%" align="left" valign="middle" class="labelfont">&nbsp;</td>
									<td width="" align="left" valign="middle" class="labelfont">Phone No.
										</td>
								</tr>
								<tr>
									<td align="left" valign="middle">&nbsp;</td>
									<td align="left" valign="middle">&nbsp;</td>
									<td align="left" valign="middle">&nbsp;</td>
									<td align="left" valign="middle">&nbsp;</td>
								</tr>
								<tr>
									<td align="left" valign="middle">&nbsp;</td>
									<td align="left" valign="top"><div class="txtnew">
								<s:textfield id="emailId" class="form-control" name="emailId"
									type="text" value="" autocomplete="off" onkeypress="return Validate(event);"></s:textfield>
							</div></td>									
									<td align="left" valign="top">&nbsp;</td>
									<td align="left" valign="top"><div class="txtnew">
								<s:textfield id="phoneNo" class="form-control" name="phoneNo"
									type="text" value="" autocomplete="off"></s:textfield>
							</div></td>
									
									<td align="center" valign="top"><s:submit value="Submit"
											id="submit" align="center" class="btn btn-success" /></td>
								</tr>
								</table>
							</td>
						</tr>
					</table> --%>
				<!-- </td>
			</tr> -->
										<tr>
											<td align="left" style="padding: 10px;">
												<div class="scrollD">
													<table width="100%" border="0" cellpadding="0"
														cellspacing="0" id="searchAgentDataTable"
														class="table table-striped table-row-bordered gy-5 gs-7">
														<thead>
															<!-- class="boxheadingsmall" -->
															<tr class="fw-bold fs-6 text-gray-800">
																<th>Email</th>
																<th>First Name</th>
																<th>Last Name</th>
																<th>Phone</th>
																<th>Is Active</th>
																<th>Action</th>
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
		
		<s:form name="agentDetails" action="editNewAgentCallAction"
			class="d-flex align-items-center fs-6 fw-semibold mb-2">
			<s:hidden name="emailId" id="emailAddress" value="" />
			<s:hidden name="firstName" id="firstName" value="" />
			<s:hidden name="lastName" id="lastName" value="" />
			<s:hidden name="mobile" id="mobile" value="" />
			<s:hidden name="isActive" id="isActive" value="" />
			<s:hidden name="token" value="%{#session.customToken}"></s:hidden>
		</s:form>
		</div>
		<script type="text/javascript">
			$('a.toggle-vis').on('click', function(e) {
				debugger
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
			$('#searchAgentDataTable').on('draw.dt', function() {
				enableBaseOnAccess();
			});
			function enableBaseOnAccess() {
				setTimeout(
						function() {
							if ($('#searchAgent').hasClass("active")) {
								var menuAccess = document
										.getElementById("menuAccessByROLE").value;
								var accessMap = JSON.parse(menuAccess);
								var access = accessMap["searchAgent"];
								if (access.includes("Update")) {
									var edits = document
											.getElementsByName("agentEdit");
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
		function myFunctions() {
			debugger
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
	<script src="../assets/plugins/custom/datatables/datatables.bundle.js"></script>
</body>
</html>