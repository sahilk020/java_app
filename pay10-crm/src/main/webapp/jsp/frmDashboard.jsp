<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="/struts-tags" prefix="s"%>
<html dir="ltr" lang="en-US">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>FRM Dashboard</title>
<link
	href="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.css"
	rel="stylesheet" type="text/css" />
<link href="../assets/plugins/custom/datatables/datatables.bundle.css"
	rel="stylesheet" type="text/css" />
<link href="../assets/plugins/global/plugins.bundle.css"
	rel="stylesheet" type="text/css" />
<link href="../assets/css/style.bundle.css" rel="stylesheet"
	type="text/css" />
<link href="../assets/css/custom/custom-style.css" rel="stylesheet"
	type="text/css" />
<script src="../js/jquery.min.js" type="text/javascript"></script>
<script src="../js/frmDashboard.js"></script>
<%-- <script src="../assets/js/widgets.bundle.js"></script>
<script src="../assets/js/custom/widgets.js"></script>
<script src="../assets/js/custom/apps/chat/chat.js"></script>
<script src="../assets/js/custom/utilities/modals/upgrade-plan.js"></script>
<script src="../assets/js/custom/utilities/modals/create-app.js"></script>
<script src="../assets/js/custom/utilities/modals/users-search.js"></script> --%>

<style type="text/css">
/* .cust {width: 24%!important; margin:0 5px !important; 
			} */
.samefnew {
	width: 19.5% !important;
	margin: 5px 5px !important; . samefnew-btn { width : 12%;
	float: left;
	font: bold 11px arial;
	color: #333;
	line-height: 22px;
	margin-top: 5px;
}
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

.cust .form-control, .samefnew .form-control {
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

#loader-wrapper .loader-section.section-left, #loader-wrapper .loader-section.section-right
	{
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

#submit {
	
}
</style>
<script type="text/javascript">
	$(document).ready(function() {

		// Initialize select2
		$("#selectReseller").select2();

		//document.getElementById("loading").style.display = "none"
	});
</script>
</head>
<body id="mainBody">
	<div style="overflow: scroll !important;">
		<div id="loader-wrapper"
			style="width: 100%; height: 100%; display: none;"></div>
			<!--begin::Root-->
		<div class="d-flex flex-column flex-root">
		<!--begin::Content-->
		<div class="content d-flex flex-column flex-column-fluid"
			id="kt_content">
			<!--begin::Toolbar-->
			<div class="toolbar" id="kt_toolbar">
				<!--begin::Container-->
				<div id="kt_toolbar_container"
					class="container-fluid d-flex flex-stack">
					<!--begin::Page title-->
					<div data-kt-swapper="true" data-kt-swapper-mode="prepend"
						data-kt-swapper-parent="{default: '#kt_content_container', 'lg': '#kt_toolbar_container'}"
						class="page-title d-flex align-items-center me-3 flex-wrap mb-5 mb-lg-0 lh-1">
						<!--begin::Title-->
						<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">
							FRM Dashboard</h1>
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
							<li class="breadcrumb-item text-muted">Fraud Prevention</li>
							<!--end::Item-->
							<!--begin::Item-->
							<li class="breadcrumb-item"><span
								class="bullet bg-gray-200 w-5px h-2px"></span></li>
							<!--end::Item-->
							<!--begin::Item-->
							<li class="breadcrumb-item text-dark">FRM Dashboard</li>
							<!--end::Item-->
						</ul>
						<!--end::Breadcrumb-->
					</div>
					<!--end::Page title-->

				</div>
			</div>
			<!--end::Container-->
			<!-- <div class="card-body ">
				<div class="container">
					<div class="row"> -->
			<div class="post d-flex flex-column-fluid" id="kt_post">
				<!--begin::Container-->
				<div id="kt_content_container" class="container-xxl">
						<div class="row my-5">
							<div class="col">
								<div class="card">
									<div class="card-body">
										<!--begin::Input group-->
										<div class="row g-9 mb-8">
											<!--begin::Col-->								
											<div class="col-md-3 fv-row">
												<label class="d-flex align-items-center fs-6 fw-bold mb-2">
													<span class="">Date From</span>
												</label>
												<div class="position-relative d-flex align-items-center">
													<s:textfield type="text" id="dateFrom" name="dateFrom"
														class="form-control form-control-solid ps-12"
														autocomplete="off" readonly="true" />

												</div>
											</div>
											<div class="col-md-3 fv-row">
												<label class="d-flex align-items-center fs-6 fw-bold mb-2">
													<span class="">Date To</span>
												</label>
												<div class="position-relative d-flex align-items-center">
													<s:textfield type="text"
														class="form-control form-control-solid ps-12" id="dateTo"
														name="dateTo" autocomplete="off" readonly="true" />
												</div>
											</div>
											<div class="col-md-2 fv-row">
												<input type="button" class="btn btn-primary btn-xs" id="submit"
													value="search" onClick="search()"
													style="margin-top: 25px;">

											</div>

										</div>
									</div>
								</div>
							</div>
						</div>
				</div>
			</div>
	

		<div style="overflow: scroll !important;">
			<table width="100%" align="left" cellpadding="0" cellspacing="0"
				class="formbox">
				<tr>
					<td align="left" style="padding: 10px;"><br /> <br />
						<div id="kt_content_container" class="container-xxl">
						<!-- 	<div style="overflow: scroll !important;"> -->
								<div class="row my-5">
									<div class="col">
										<div class="card">
											<div class="card-body">
												<div class="row g-9 mb-8 justify-content-end">
													<div class="col-lg-2 col-sm-12 col-md-6">
														<select name="currency" data-placeholder="Actions"
															id="actions11"
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
																<a class="toggle-vis" data-column="0">Serial No.</a>
																<a class="toggle-vis" data-column="1">Most Active Merchant By Volume</a> 
																<a class="toggle-vis" data-column="2">Most Active Merchant By Count</a>
																<a class="toggle-vis" data-column="3">High FRM rules breach Merchants</a> 
																<a class="toggle-vis" data-column="4">Least Performer TSR</a>
													

															</div>
														</div>
													</div>
												</div>
												<div class="scrollD">
													<table id="datatable"
														class="display table table-striped table-row-bordered gy-5 gs-7"
														cellspacing="0" width="100%">
														<thead>
															<tr class="fw-bold fs-6 text-gray-800 boxheadingsmall">
																<th>Serial No.</th>
																<th>Most Active Merchant By Volume</th>
																<th>Most Active Merchant By Count</th>
																<th>High FRM rules breach Merchants</th>
																<th>Least Performer TSR</th>
															</tr>
														</thead>
													</table>
												</div></td>
				</tr>
			</table>
		</div>
	</div>
	</div>
	</div>
	<script
		src="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.js"></script>
	<script src="../assets/plugins/global/plugins.bundle.js"></script>
	<script src="../assets/js/scripts.bundle.js"></script>

	<script>
		$('#datatable').on('draw.dt', function() {
			//enableBaseOnAccess();
		});
		function enableBaseOnAccess() {
			setTimeout(function() {
				if ($('#resellerPayoutAction').hasClass("active")) {
					var menuAccess = document
							.getElementById("menuAccessByROLE").value;
					var accessMap = JSON.parse(menuAccess);
					var access = accessMap["resellerPayoutAction"];
					if (access.includes("Payout")) {
						var payouts = document
								.getElementsByName("payoutDetails");
						for (var i = 0; i < payouts.length; i++) {
							var payout = payouts[i];
							payout.disabled = false;
						}
					}
				}
			}, 500);
		}
	</script>
	<script src="../assets/plugins/custom/datatables/datatables.bundle.js"></script>
	<script>
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
		$('a.toggle-vis').on('click', function(e) {
			e.preventDefault();
			table = $('#datatable').DataTable();
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
	<style>
.dt-buttons {
	display: none;
}
</style>
</body>
</html>
