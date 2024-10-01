<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib prefix="s" uri="/struts-tags"%>
<html dir="ltr" lang="en-US">

<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Role List</title>
<!-- <script type="text/javascript" src="../js/jquery.min.js"></script>
<script src="../js/core/popper.min.js"></script>
<script src="../js/core/bootstrap-material-design.min.js"></script>
<script src="../js/plugins/perfect-scrollbar.jquery.min.js"></script> -->
<!-- Control Center for Material Dashboard: parallax effects, scripts for the example pages etc -->
<!-- <script src="../js/material-dashboard.js?v=2.1.0" type="text/javascript"></script>
<link href="../css/Jquerydatatableview.css" rel="stylesheet" />
<link href="../css/Jquerydatatable.css" rel="stylesheet" />
<link href="../css/default.css" rel="stylesheet" type="text/css" />
<link href="../css/jquery-ui.css" rel="stylesheet" />
<script src="../js/jquery.dataTables.js"></script>
<script type="text/javascript" src="../js/dataTables.buttons.js"></script>
<script type="text/javascript" src="../js/pdfmake.js"></script> -->
<!-- <link href="../fonts/css/font-awesome.min.css" rel="stylesheet">

		<link href="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.css" rel="stylesheet" type="text/css" />
		<link href="../assets/plugins/custom/datatables/datatables.bundle.css" rel="stylesheet" type="text/css" />
		<link href="../assets/plugins/global/plugins.bundle.css" rel="stylesheet" type="text/css" />
		<link href="../assets/css/style.bundle.css" rel="stylesheet" type="text/css" />
		<link href="../assets/css/custom/custom-style.css" rel="stylesheet" type="text/css" />


		<script src="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.js"></script>
		<script src="../assets/plugins/custom/datatables/datatables.bundle.js"></script>
		<script src="../assets/js/widgets.bundle.js"></script>
		<script src="../assets/js/custom/widgets.js"></script>
		<script src="../assets/js/custom/apps/chat/chat.js"></script>
		<script src="../assets/js/custom/utilities/modals/upgrade-plan.js"></script>
		<script src="../assets/js/custom/utilities/modals/create-app.js"></script>
		<script src="../assets/js/custom/utilities/modals/users-search.js"></script>
		<script src="../assets/plugins/global/plugins.bundle.js"></script>
		<script src="../assets/js/scripts.bundle.js"></script> -->


<link rel="shortcut icon" href="assets/media/images/paylogo.svg" />
<!--begin::Fonts-->
<link rel="stylesheet"
	href="https://fonts.googleapis.com/css?family=Inter:300,400,500,600,700" />
<!--end::Fonts-->
<!--begin::Vendor Stylesheets(used by this page)-->
<!-- <link href="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.css" rel="stylesheet"
						type="text/css" /> -->
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
<script src="../js/jquery.popupoverlay.js"></script>


<link href="../css/select2.min.css" rel="stylesheet" />
<script src="../js/jquery.select2.js" type="text/javascript"></script>
<style>
.dt-buttons {
	display: none;
}
</style>
</head>

<body>

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
					<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">Role
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
						<li class="breadcrumb-item text-dark">Role List</li>
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
							<div class="card-body ">
								<div class="row g-9 mb-8">
									<s:form id="formValidation">
										<s:hidden name="token" value="%{#session.customToken}"></s:hidden>
										<div class="indent raw col-md-12 formboxRR">
											<div class="row">
												<div class="col-md-3 addfildn">

													<label
														class='d-flex align-items-center fs-6 fw-semibold mb-2'
														style="padding: 0; font-size: 13px; font-weight: 600;">Role
														Name*</label>
													<s:textfield id="roleName" maxlength="50"
														class="form-control form-control-solid"
														style="margin-top:10px;    font-weight:500; font-size:14px;"
														name="roleName" type="text"
														onkeypress="return (event.charCode > 64 && event.charCode < 91) || (event.charCode > 96 && event.charCode < 123) || (event.charCode==32)">
													</s:textfield>
													<em id="roleName-error" class="error invalid-feedback"
														style="display: inline;"></em>

												</div>

												<div class="col-md-3 addfildn">

													<label
														class='d-flex align-items-center fs-6 fw-semibold mb-2'
														style="padding: 0; font-size: 13px; font-weight: 600;">Role
														Description*</label>
													<s:textfield id="description" maxlength="50"
														class="form-control form-control-solid"
														style="margin-top:10px;    font-weight:500; font-size:14px;"
														name="description" type="text"
														onkeypress="return (event.charCode > 64 && event.charCode < 91) || (event.charCode > 96 && event.charCode < 123) || (event.charCode==32)">
													</s:textfield>
													<em id="description-error" class="error invalid-feedback"
														style="display: inline;"></em>

												</div>

												<div class="col-md-3 addfildn">

													<label
														class='d-flex align-items-center fs-6 fw-semibold mb-2'
														style="padding: 0; font-size: 13px; font-weight: 600;">User
														Group*</label>
													<div class="txtnew">
														<s:select name="userGroupId"
															class="form-select form-select-solid mt-1"
															id="userGroupId" headerKey=""
															headerValue="Select User Group" list="userGroups"
															listKey="id" listValue="group" autocomplete="off" />
														<em id="userGroupId-error" class="error invalid-feedback"
															style="display: inline;"></em>
													</div>

												</div>

												<div class="col-md-1 addfildn">

													<label
														class='d-flex align-items-center fs-6 fw-semibold mb-2'
														style="padding: 0; font-size: 13px; font-weight: 600;">Active</label>
													<s:checkbox id="isActive"
														style="margin-top:10px;    font-weight:500; font-size:14px;"
														name="active"></s:checkbox>

												</div>

												<div class="col-md-1 addfildn">

													<s:submit method="submit" style="margin-right: 10px;"
														class="btn btn-primary btn-md mt-7" value="Save">
													</s:submit>
												</div>


											</div>
										</div>
									</s:form>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>

		<!-- <h2 class="pageHeading">Role List</h2> -->

		<div class="post d-flex flex-column-fluid" id="kt_post">
			<div id="kt_content_container" class="container-xxl">
				<div class="row my-5">
					<div class="col">
						<div class="card">
							<div class="card-body ">
								<div class="row g-9 mb-8 justify-content-end">
	                               <%--  <div class="col-lg-4 col-sm-12 col-md-6">
                                         <s:select name="payId" headerKey="ALL" headerValue="ALL" class="form-select form-select-solid" id="payId" list="merchantList"
                                        listKey="emailId" listValue="businessName" autocomplete="off" />
                                    </div> --%>
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
												<a class="toggle-vis" data-column="0">Sr. No.</a> <a
													class="toggle-vis" data-column="1">Role Name</a> <a
													class="toggle-vis" data-column="2">Role Description</a>
													<a class="toggle-vis" data-column="3">Created By</a>
													<a class="toggle-vis" data-column="4">Active</a> <a
													class="toggle-vis" data-column="5">Creation DateTime</a>

											</div>
										</div>
									</div>
								</div>


								<div class="scrollD">
									<div class="row g-9 mb-8">
										<div>
											<table id="rollList"
												class="display table table-striped table-row-bordered gy-5 gs-7"
												cellspacing="0" width="100%">
												<thead>
													<tr class="boxheadingsmall fw-bold fs-6 text-gray-800">
														<th>Sr no</th>
														<th>Role name</th>
														<th>Role Description</th>
														<th>Created By</th>
														<th>Active</th>
														<th>Creation DateTime</th>
														<th>Edit</th>
														<th>Delete</th>
														<th>ID</th>
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
		<s:form name="role" action="editRoleAction">
			<s:hidden name="id" id="id" value=""></s:hidden>
			<s:hidden name="token" value="%{#session.customToken}"></s:hidden>
		</s:form>

		<script type="text/javascript">
				$(document).ready(function () {
					$("#userGroupId").select2();

						renderTable1();

				});
				function decodeVal(text) {
					return $('<div/>').html(text).text();
				}

				function addRole(e) {
					var token = document.getElementsByName("token")[0].value;
					var roleName = document.getElementById("roleName").value;
					var desc = document.getElementById("description").value;
					var groupId = document.getElementById("userGroupId").value;
					var isActive = document.getElementById("isActive").checked;
					if (roleName == null || roleName == '') {
						document.getElementById("roleName-error").innerHTML = "Please enter rolename";
						document.getElementById("roleName").focus();
						e.preventDefault();
						return;
					} else if (!/^[a-zA-Z\\s]+/.test(roleName)) {
						document.getElementById("roleName-error").innerHTML = "Please add valid data";
						document.getElementById("roleName").focus();
						e.preventDefault();
						return;
					} else {
						document.getElementById("roleName-error").innerHTML = "";
					}
					if (desc == null || desc == '') {
						document.getElementById("description-error").innerHTML = "Please enter description";
						document.getElementById("description").focus();
						e.preventDefault();
						return;
					} else if (!/^[a-zA-Z\\s]+/.test(desc)) {
						document.getElementById("description-error").innerHTML = "Please add valid data";
						document.getElementById("description").focus();
						e.preventDefault();
						return;
					} else {
						document.getElementById("description-error").innerHTML = "";
					}
					if (groupId == null || groupId == '') {
						document.getElementById("userGroupId-error").innerHTML = "Please select the group";
						document.getElementById("userGroupId").focus();
						e.preventDefault();
						return;
					} else {
						document.getElementById("userGroupId-error").innerHTML = "";
					}
					console.log("Group Id"+groupId);
					$.ajax({
						type: "POST",
						url: "addRoleAction",
						data: {
							roleName: roleName,
							description: desc,
							userGroupId: groupId,
							active: isActive,
							token: token,
							"struts.token.name": "token"
						},
						success: function (data, status) {
							successPopup("Role created successfully. Please set the permission");
							alert("Role created successfully. Please set the permission");
							$('#formValidation').reset();
							window.location.reload();
						},
						error: function (status) {
							errorPopUp("Failed to create role.");

						}
					});
				}
				function generatePostData() {
					var token = document.getElementsByName("token")[0].value;
					var emailId=$("#payId").val();
					var obj = {
						token: token
					};

					return obj;
				}
				function errorPopUp(msg){
					Swal.fire({
					text: msg,
					icon: "error",
					buttonsStyling: !1,
					confirmButtonText: "Ok, got it!",
					customClass: {
					  confirmButton: "btn btn-primary"
					}
				  });
				}

				function successPopup(msg){
					Swal.fire({
  					 	icon: "success",
 					 	text:msg,
 						showConfirmButton: false,
 						timer: 1500
					});
				}



				function renderTable1(){
				 	var token = document.getElementsByName("token")[0].value;
					var emailId=$("#payId").val();
					if(emailId=="ALL"){
						emailId="";
					}
				// 	console.log(emailId);
				// 	console.log(token);
				  //	$("#rollList").empty();
					var table = $('#rollList').DataTable(
									{
										dom: 'BTftlpi',
										buttons: [
											{
												extend: 'print',
												 exportOptions : {
														columns : [ 0, 1, 2, 3, 4, 5 ]
													}
											},
											'colvis',
											{
											extend: 'copy',
											 exportOptions : {
																columns : [ 0, 1, 2, 3, 4, 5 ]
															}
											},

								          	{
												extend:'pdf',
													exportOptions : {
														columns : [ 0, 1, 2, 3, 4 , 5]
													}
												} ,
												{
													extend:'csv',
														exportOptions : {
															columns : [ 0, 1, 2, 3, 4, 5]
														}
													} , 'print','excel',

										],
										scrollY: true,
										scrollX: true,
										searchDelay: 500,
										processing: false,
										// serverSide: true,
										order: [[1, 'desc']],
										stateSave: true,
										dom: 'BTftlpi',
										paging: true,
										"ajax": {
											"url": "roleDetailsAction",
											"type": "POST",
											"data": {token:token,
												emailId:emailId
											}
										},
										"bProcessing": true,
										"bLengthChange": true,
										"bDestroy": true,
										"order": [[0, "asc"]],

										"aoColumns": [
									{
										"mData": null,
										render: (data, type, row, meta) => meta.row + 1
									},
									{
										"mData": "roleName"
									},
									{
										"mData": "description"
									},
									{
										"mData":"createdBy"
									},
							 		{
										"mData": "active"
									},
									{
										"mData": "createdAt"
									},
									{
										"mData": null,
										"sClass": "center",
										"bSortable": false,
										"mRender": function () {
											return '<button class="btn btn-primary btn-xs">Edit</button>';
										}
									},
									{
										"mData": null,
										"sClass": "center",
										"bSortable": false,
										"mRender": function () {
											return '<button class="btn btn-danger acquirerRemoveBtn">Delete</button>';
										}
									}, {
										"mData": "id",
										"visible": false,
										"className": "displayNone"
									}]

									});
								console.log(table);
						var table = $('#rollList').DataTable();

						console.log("Inside Ready");
						$('#rollList tbody').on('click', 'td', function () {
							console.log("Inside Click");
						 console.log("ROw: "+$(this)[0]._DT_CellIndex.row);
							//var rows = table.rows();
							//console.log($(this).index());
							//console.log($(this).row);
				 			//var columnVisible =table.cell(this).index().columnVisible;
							//var rowIndex = table.cell(this).index().row;
					 		var columnVisible=$(this)[0]._DT_CellIndex.column;
							var rowIndex=$(this)[0]._DT_CellIndex.row;
							var id = table.cell(rowIndex, 8).data();
							console.log("Id: "+id);
							debugger;
							document.getElementById('id').value = id;
							var token = document.getElementsByName("token")[0].value;
							console.log("Column Visible"+columnVisible);
							if (columnVisible == 6) {
								console.log("Submit");
								document.role.submit();
							}
							if (columnVisible == 7) {
								$.ajax({
									type: "POST",
									url: "mappedRole",
									data: {
										roleId: id,
										token: token,
										"struts.token.name": "token"
									},
									success: function (result) {
										var res = JSON.parse(JSON.stringify(result));
										var message = res.message;
										if (message != "success") {
											errorPopUp(message);
											//alert(message);
											return false;
										}
										var answer = confirm("Are you sure to want to delete role?");
										if (answer != true) {
											return false;
										} else {
											//$('#loader-wrapper').show();
											$.ajax({
												type: "POST",
												url: "deleteRoleAction",
												data: {
													id: id,
													token: token,
													"struts.token.name": "token"
												},
												success: function (data, status) {
													successPopup("Role deleted successfully.");
													//alert("Role deleted successfully.");
													window.location.reload();
												},
												error: function (status) {
													errorPopUp("Failed to delete role.");
													//alert("Failed to delete role.");
												}
											});
										}
									}
								});
							}
						});
				}
					$('#formValidation').submit(function (e) {
						addRole(e);
					});





			</script>



	</div>

	<script type="text/javascript">
	$('a.toggle-vis').on('click', function (e) {
	    debugger
	    e.preventDefault();
	    table = $('#rollList').DataTable();
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
			$("#payId").change(function(){
			 	renderTable1();
			});

		</script>
	<script
		src="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.js"></script>
	<script src="https://cdn.amcharts.com/lib/5/index.js"></script>
	<script src="https://cdn.amcharts.com/lib/5/xy.js"></script>
	<script src="https://cdn.amcharts.com/lib/5/percent.js"></script>
	<script src="https://cdn.amcharts.com/lib/5/radar.js"></script>
	<script src="https://cdn.amcharts.com/lib/5/themes/Animated.js"></script>
	<script src="https://cdn.amcharts.com/lib/5/map.js"></script>
	<script src="https://cdn.amcharts.com/lib/5/geodata/worldLow.js"></script>
	<script src="https://cdn.amcharts.com/lib/5/geodata/continentsLow.js"></script>
	<script src="https://cdn.amcharts.com/lib/5/geodata/usaLow.js"></script>
	<script
		src="https://cdn.amcharts.com/lib/5/geodata/worldTimeZonesLow.js"></script>
	<script
		src="https://cdn.amcharts.com/lib/5/geodata/worldTimeZoneAreasLow.js"></script>
	<script src="../assets/plugins/custom/datatables/datatables.bundle.js"></script>
	<!--end::Vendors Javascript-->
	<!--begin::Custom Javascript(used by this page)-->
	<script src="../assets/js/widgets.bundle.js"></script>
	<script src="../assets/js/custom/widgets.js"></script>
	<script src="../assets/js/custom/apps/chat/chat.js"></script>
	<script src="../assets/js/custom/utilities/modals/upgrade-plan.js"></script>
	<script src="../assets/js/custom/utilities/modals/create-app.js"></script>
	<script src="../assets/js/custom/utilities/modals/users-search.js"></script>



</body>

</html>