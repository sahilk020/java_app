<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<base href="../"/>
		<title>User List</title>
		
		<link rel="shortcut icon" href="../assets/media/images/paylogo.svg" />
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
		
		<script type="text/javascript" src="../js/jquery.min.js"></script>




<style>
/* .displayNone {
	display: none;
}
.dataTables_wrapper .dataTables_filter input{
	display: none !important;
}
.dataTables_wrapper .dataTables_filter label{
	display: none !important;
}
table.dataTable.display tbody tr.odd {
    background-color: #e6e6ff !important;
}
table.dataTable.display tbody tr.odd > .sorting_1{
	 background-color: #e6e6ff !important;
} */

.dt-buttons.btn-group.flex-wrap {
    display: none !important;
}
#kt_datatable_vertical_scroll_filter {
    display: none !important;
}
</style>
<script type="text/javascript">
function decodeVal(text){
	  return $('<div/>').html(text).text();
	}
$(document).ready(
		function() {
			populateDataTable();
			//enableBaseOnAccess();
			$("#submit").click(
					function(env) {
						/* var table = $('#authorizeDataTable')
								.DataTable(); */
						$('#searchUserDataTable').empty();


						populateDataTable();

					});

					$('a.toggle-vis').on('click', function(e) {
			        e.preventDefault();
			        let table = $('#searchUserDataTable').DataTable();
			        // Get the column API object
			        let column = table.column($(this).attr('data-column'));
			        // Toggle the visibility
			        column.visible(!column.visible());

			        $(this).toggleClass('activecustom');
			    });

		});

function populateDataTable() {
	var token  = document.getElementsByName("token")[0].value;
	$('#searchUserDataTable')
			.DataTable(
					{
						dom : 'BTftlpi',
						buttons : [ {
							extend : 'copyHtml5',
							exportOptions : {
								columns : [ ':visible' ]
							}
						}, {
							extend : 'csvHtml5',
							title : 'Search User',
							exportOptions : {
								columns : [ ':visible' ]
							}
						}, {
							extend : 'pdfHtml5',
							title : 'Search User',
							exportOptions : {
								columns : [ ':visible' ]
							}
						}, {
							extend : 'print',
							title : 'Search User',
							exportOptions : {
								columns : [ 0, 1, 2, 3, 4, 5 ]
							}
						},{
							extend : 'colvis',
							//           collectionLayout: 'fixed two-column',
							columns : [ 1, 2, 3, 4, 5]
						}],
						"ajax" : {
							"url" : "searchUserAction",
							"type" : "POST",
							"data" : {
								/* emailId : document
									.getElementById("emailId").value,
								phoneNo : document
									.getElementById("phoneNo").value */
									token:token,
								    "struts.token.name": "token",
									}
						},
						"bProcessing" : true,
						"bLengthChange" : true,
						"bDestroy" : true,
						"iDisplayLength" : 10,
						"order" : [ [ 1, "desc" ] ],
						"aoColumns" : [
										{
											"mData" : "emailId",
											"sWidth" : '25%',
										},
										{
											"mData" : "firstName",
											"sWidth" : '20%'
										},
										{
											"mData" : "lastName",
											"sWidth" : '20%'
										},
										{
											"mData" : "mobile",
											"sWidth" : '20%'
										},
										{
											"mData" : "isActive",
											"sWidth" : '10%'
										},
										{
											"mData" : null,
											"sClass" : "center",
											"bSortable" : false,
											"mRender" : function() {
												return '<button class="btn w-100 btn-primary" id="editSubUser" name="editSubUser">Edit</button>';
											}
										},
										{
											"mData" : "payId",
											"sWidth" : '25%',
											"visible" : false,
										}]
					});

	 $(function() {

		var table = $('#searchUserDataTable').DataTable();
		$('#searchUserDataTable tbody')
				.on(
						'click',
						'td',
						function() {
							var columnVisible = table.cell(this).index().columnVisible;
							var rowIndex = table.cell(this).index().row;
							var row = table.row(rowIndex).data();

							var emailAddress = table.cell(rowIndex, 0).data();
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
							 		document.userDetails.submit();

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
<body id="kt_body" class="header-fixed header-tablet-and-mobile-fixed toolbar-enabled toolbar-fixed toolbar-tablet-and-mobile-fixed aside-enabled aside-fixed" style="--kt-toolbar-height:55px;--kt-toolbar-height-tablet-and-mobile:55px">
	 <s:token />
	<div class="content d-flex flex-column flex-column-fluid" id="kt_content">
						<!--begin::Toolbar-->
						<div class="toolbar" id="kt_toolbar">
							<!--begin::Container-->
							<div id="kt_toolbar_container" class="container-fluid d-flex flex-stack">
								<!--begin::Page title-->
								<div data-kt-swapper="true" data-kt-swapper-mode="prepend" data-kt-swapper-parent="{default: '#kt_content_container', 'lg': '#kt_toolbar_container'}" class="page-title d-flex align-items-center me-3 flex-wrap mb-5 mb-lg-0 lh-1">
									<!--begin::Title-->
									<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">User List</h1>
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
										<li class="breadcrumb-item text-dark">User List</li>
										<!--end::Item-->
									</ul>
									<!--end::Breadcrumb-->
								</div>
								<!--end::Page title-->

							</div>
							<!--end::Container-->
						</div>
						<!--end::Toolbar-->
						<!--begin::Post-->
						<div class="post d-flex flex-column-fluid" id="kt_post">
							<!--begin::Container-->
							<div id="kt_content_container" class="container-xxl">

                                <div class="row my-5">
                                    <div class="col">
                                      <div class="card">
                                        <div class="card-body">
                                          <!--begin::Input group-->
                                          <div class="row g-9 mb-8 justify-content-end">
                                            <div class="col-lg-4 col-sm-12 col-md-6">
                                              <select name="currency" data-control="select2" data-placeholder="Actions"
                                                class="form-select form-select-solid actions" data-hide-search="true">
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
                                          <div class="row g-9 mb-8">
                                            <table id="searchUserDataTable"
                                              class="table table-striped table-row-bordered gy-5 gs-7">
                                              <thead>
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
                                        </div>
                                      </div>
                                    </div>
                                </div>
							</div>
							<!--end::Container-->
						</div>
							<!--end::Container-->
						</div>
						<!--end::Post-->

		<!--begin::Global Javascript Bundle(used by all pages)-->
		<script src="../assets/plugins/global/plugins.bundle.js"></script>
		<script src="../assets/js/scripts.bundle.js"></script>
		<!--end::Global Javascript Bundle-->
		<!--begin::Vendors Javascript(used by this page)-->
		<script src="../assets/plugins/custom/datatables/datatables.bundle.js"></script>
		<!--end::Vendors Javascript-->
		<!--end::Javascript-->

<s:form name="userDetails" action="editUserCallAction">
		<s:hidden name="emailId" id="emailAddress" value="" />
		<s:hidden name="firstName" id="firstName" value="" />
		<s:hidden name="lastName" id="lastName" value="" />
		<s:hidden name="mobile" id="mobile" value="" />
		<s:hidden name="isActive" id="isActive" value="" />
		<s:hidden name="token" value="%{#session.customToken}"></s:hidden>
	</s:form>
<script type="text/javascript">
$('#searchUserDataTable').on( 'draw.dt', function () {
	enableBaseOnAccess();
} );
function enableBaseOnAccess() {
		setTimeout(function(){
			if ($('#searchUser').hasClass("active")) {
				var menuAccess = document.getElementById("menuAccessByROLE").value;
				var accessMap = JSON.parse(menuAccess);
				var access = accessMap["searchAgent"];
				if (access.includes("Update")) {
					var edits = document.getElementsByName("agentEdit");
					for (var i = 0; i < edits.length; i++) {
						var edit = edits[i];
						edit.disabled=false;
					}
				}
			}
		},500);
}
</script>
<!--begin::Global Javascript Bundle(used by all pages)-->

		<!--end::Global Javascript Bundle-->
		<!--begin::Vendors Javascript(used by this page)-->
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
		<!--end::Custom Javascript-->
		<!--end::Javascript-->

</body>
</html>