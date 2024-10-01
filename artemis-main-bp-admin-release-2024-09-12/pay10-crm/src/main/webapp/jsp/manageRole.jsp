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
span.select2-selection.select2-selection--multiple.form-select.form-select-solid{
	height: 25px !important;
    width: 250px !important;
    overflow: auto !important;

}
.select2-selection__choice__display{
	font-size: 11px !important;
}
.select2-container .select2-selection--multiple .select2-selection__rendered{
	white-space: unset !important;
}
#checkboxes1 {
	width: 210px;

	display: none;
	border: 1px #dadada solid;
	height:300px;
	overflow-y: scroll;
	position:Absolute;
	background:#fff;
	z-index:1;
	margin-left:5px;
}

#checkboxes1 label {
  width: 74%;
}
#checkboxes1 input {
  width:18%;

}

.selectBox {

	position: relative;
	}

.selectBox select {
	width: 95%;
}

.overSelect {
	position: absolute;
	left: 0;
	right: 0;
	top: 0;
	bottom: 0;
	}
</style>
<script>
$(document).ready(function() {
	//	$("#merchant").select2();
});



var expanded = false;
				function showCheckboxes(e, uid) {
					var checkboxes = document.getElementById("checkboxes" + uid);
					if (!expanded) {
						expanded = true;
						if (uid == 1) {
							document.getElementById("checkboxes1").style.display = "block";
							document.getElementById("checkboxes2").style.display = "none";
							document.getElementById("checkboxes3").style.display = "none";
							document.getElementById("checkboxes4").style.display = "none";
						}
						if (uid == 2) {
							document.getElementById("checkboxes1").style.display = "none";
							document.getElementById("checkboxes2").style.display = "block";
							document.getElementById("checkboxes3").style.display = "none";
							document.getElementById("checkboxes4").style.display = "none";
						}
						if (uid == 3) {
							document.getElementById("checkboxes3").style.display = "block";
							document.getElementById("checkboxes1").style.display = "none";
							document.getElementById("checkboxes2").style.display = "none";
							document.getElementById("checkboxes4").style.display = "none";
						}
						if (uid == 4) {
							document.getElementById("checkboxes4").style.display = "block";
							document.getElementById("checkboxes3").style.display = "none";
							document.getElementById("checkboxes1").style.display = "none";
							document.getElementById("checkboxes2").style.display = "none";
						}
					} else {
						checkboxes.style.display = "none";
						expanded = false;
					}
					e.stopPropagation();

				}

				function getCheckBoxValue(uid) {
					debugger
					var allInputCheckBox = document.getElementsByClassName("myCheckBox" + uid);
					var labels = document.getElementsByTagName('LABEL');
				var allSelectedAquirer = [];
				var allLabel=[];
					for (var i = 0; i < allInputCheckBox.length; i++) {

						if (allInputCheckBox[i].checked) {
							allSelectedAquirer.push(allInputCheckBox[i].value);
							allLabel.push(allInputCheckBox[i].labels[0].innerHTML);
						}

					}
					var test = document.getElementById('selectBox' + uid);
					document.getElementById('selectBox' + uid).setAttribute('title', allLabel.join());
					if (allLabel.join().length > 28) {
						var res = allLabel.join().substring(0, 27);
						document.querySelector("#selectBox" + uid + " option").innerHTML = res + '...............';
					} else if (allLabel.join().length == 0) {
						document.querySelector("#selectBox" + uid + " option").innerHTML = 'ALL';
					} else {
						document.querySelector("#selectBox" + uid + " option").innerHTML = allLabel.join();
					}
				}

</script>
</head>

<body>
	<div class="modal" id="payoutAccept" tabindex="-1" role="dialog"
			aria-labelledby="myModalLabel" aria-hidden="true">

			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header" align="center"></div>
					<div class="modal-body">
						<div class="col-md-6 addfildn">

                            <label
                                class='d-flex align-items-center fs-6 fw-semibold mb-2'
                                style="padding: 0; font-size: 13px; font-weight: 600;">User*</label>
                            <!-- <div class="txtnew">
                                <s:select name="merchant" class="form-select form-select-solid" id="merchant" list="merchantList"
                               headerKey="" headerValue="Select User"  listKey="payId" listValue="businessName" autocomplete="off"  multiple="true" />
                                <em id="merchant-error" class="error invalid-feedback"
                                    style="display: inline;"></em>
                            </div> -->
							<div class="txtnew">
								<div class="selectBox" id="selectBox1"
									onclick="showCheckboxes(event,1)" title="">
									<select class="form-select form-select-solid">
										<option>Please select</option>
									</select>
									<div class="overSelect"></div>
								</div>
								<div id="checkboxes1"  onclick="getCheckBoxValue(1)">
									<s:checkboxlist name="merchant" id="merchant"  list="merchantList" headerKey="" headerValue="Select User"
									listKey="payId" listValue="businessName"  class="myCheckBox1"  />
								</div>
							</div>

                        </div>
                        <input type="hidden" name="roleId" id="roleId">
					</div>
					<div align="right" style="padding-right:10px">
						<button type="button" class="btn btn-lg btn-primary"
							id="btnPayoutConf" onClick='assignedPermissions()'>Submit</button>
						<button type="button" class="btn btn-lg btn-danger"
							id="btnPayoutConf" data-dismiss="modal" onClick="hideModal()">Cancel</button>
					</div>
					<br> <br>
				</div>
			</div>
		</div>
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
															headerValue="Select User Group"   list="userGroups"
															listKey="id" listValue="group" autocomplete="off" />
														<em id="userGroupId-error" class="error invalid-feedback"
															style="display: inline;"></em>
													</div>

												</div>

												<div class="col-md-3 addfildn">
                                                    <!--begin::Label-->
                                                   <label
                                                    class='d-flex align-items-center fs-6 fw-semibold mb-2'
                                                    style="padding: 0; font-size: 13px; font-weight: 600;">Application*</label>

                                                    <div class="txtnew">
                                                        <s:select name="aaData"
                                                            class="form-select form-select-solid mt-1"
                                                            id="applicationId" headerKey=""
                                                            headerValue="Select Application" list="aaData"
                                                            listKey="id" listValue="name" autocomplete="off" data-control="select-2"/>
                                                        <em id="applicationId-error" class="error invalid-feedback"
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
													class="toggle-vis" data-column="2">Role Description</a> <a
													class="toggle-vis" data-column="3">Active</a> <a
													class="toggle-vis" data-column="4">Creation DateTime</a>

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
														<th>Active</th>
														<th>Edit</th>
                                                        <th>Assign</th>
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
		<s:form name="role" action="editUserRoleAction">
			<s:hidden name="id" id="id" value=""></s:hidden>
			<s:hidden name="token" value="%{#session.customToken}"></s:hidden>
		</s:form>

		<script type="text/javascript">
				$(document).ready(function () {
	                $("#userGroupId").select2();
                    $("#applicationId").select2();
						renderTable1();


				});
				function assigned(val){

                  document.getElementById("roleId").value=val;
                  $('#payoutAccept').modal('show');
                }

				function decodeVal(text) {
					return $('<div/>').html(text).text();
				}

				function addRole(e) {
				debugger
					var token = document.getElementsByName("token")[0].value;
					var roleName = document.getElementById("roleName").value;
					var desc = document.getElementById("description").value;
					var groupId = document.getElementById("userGroupId").value;
					var applicationId=document.getElementById("applicationId").value;
					var isActive = document.getElementById("isActive").checked;
					if (roleName == null || roleName == '') {
						document.getElementById("roleName-error").innerHTML = "Please enter rolename";
						document.getElementById("roleName").focus();
						e.preventDefault();
						return;
					} else if (!/^[a-z A-Z]+$/.test(roleName)) {
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
					} else if (!/^[a-z A-Z]+$/.test(desc)) {
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

					 if (applicationId == null || applicationId == '') {
                        document.getElementById("applicationId-error").innerHTML = "Please select the application";
                        document.getElementById("applicationId").focus();
                        e.preventDefault();
                        return;
                        } else {
                            document.getElementById("applicationId-error").innerHTML = "";
                        }
					$.ajax({
						type: "POST",
						url: "addUserRoleAction",
						data: {
							roleName: roleName,
							description: desc,
							userGroupId: groupId,
							applicationId:applicationId,
							active: isActive,
							token: token,
							"struts.token.name": "token"
						},

						success: function (data, status) {
							alert("Role created successfully. Please set the permission");

							window.location.reload();
						},
						error: function (status) {
							alert("Failed to create role.");
						}
					});
				}
				function generatePostData() {
					var token = document.getElementsByName("token")[0].value;
					var obj = {
						token: token,
					};

					return obj;
				}



				function renderTable1(){
					debugger
					var token = document.getElementsByName("token")[0].value;
					var table = $('#rollList').DataTable(
									{
										dom: 'BTftlpi',
										buttons: [
											{
												extend: 'print',
												 exportOptions : {
														columns : [ 0, 1, 2, 3, 4 ]
													}
											},
											'colvis',
											{
											extend: 'copy',
											 exportOptions : {
																columns : [ 0, 1, 2, 3, 4 ]
															}
											},

								          	{
												extend:'pdf',
													exportOptions : {
														columns : [ 0, 1, 2, 3, 4 ]
													}
												} ,
												{
													extend:'csv',
														exportOptions : {
															columns : [ 0, 1, 2, 3, 4 ]
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
											"url": "roleUserDetailsAction",
											"type": "POST",
											"data": generatePostData,
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
										"mData": "active"
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
                                            render: function (data , type, row, meta) {
                                            return "<input type='button' name='reufndBtn' value='Assign'"+
                                                "data-toggle='modal' data-target='#payoutAccept' onclick = 'assigned("+data.id+")' "+
                                                "class='btn btn-sm btn-danger' /> ";
                                            }
                                 },

									 {
										"mData": "id",
										"visible": false,
										"className": "displayNone"
									}]

									});


						$(document).ready(function () {
						var table = $('#rollList').DataTable();
						$('#rollList tbody').on('click', 'td', function () {
							var rows = table.rows();
							var columnVisible = table.cell(this).index().columnVisible;
							var rowIndex = table.cell(this).index().row;
							var id = table.cell(rowIndex, 6).data();
							document.getElementById('id').value = id;
							var token = document.getElementsByName("token")[0].value;
							if (columnVisible == 4) {
								document.role.submit();
							}

						});
					});
					$('#formValidation').submit(function (e) {
						addRole(e);
					});



				}

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

            function hideModal(){
            $('#payoutAccept').modal('hide');
            }
            function assignedPermissions(){

				var allInputCheckBox = document.getElementsByClassName("myCheckBox1");
					var labels = document.getElementsByTagName('LABEL');
				var allSelectedAquirer = [];
				var allLabel=[];
					for (var i = 0; i < allInputCheckBox.length; i++) {

						if (allInputCheckBox[i].checked) {
							allSelectedAquirer.push(allInputCheckBox[i].value);
							allLabel.push(allInputCheckBox[i].labels[0].innerHTML);
						}

					}

            var payId=allSelectedAquirer.toString();

            var roleId=$('#roleId').val();

               $.ajax({
           						type: "POST",
                                    url: "assignedUserRoleAction",
           						 data: {
           							payId: payId,
           							roleId: roleId,

           						},

           						success: function (data, status) {
           							alert("Role assigned successfully" );

           							window.location.reload();
           						},
           						error: function (status) {
           							alert("Failed to assigned role.");
           						}
           					});
            }
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