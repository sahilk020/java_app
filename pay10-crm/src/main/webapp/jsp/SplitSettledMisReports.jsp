<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Split Settled Report</title>

<!-- <link href="../css/Jquerydatatableview.css" rel="stylesheet" />
<link href="../css/Jquerydatatable.css" rel="stylesheet" />
<link href="../css/default.css" rel="stylesheet" type="text/css" />
<link href="../css/jquery-ui.css" rel="stylesheet" /> -->
<script src="../js/jquery.min.js"></script>
<script src="../assets/plugins/global/plugins.bundle.js"></script>
<script src="../assets/js/scripts.bundle.js"></script>
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
<%-- <%-- <script src="../js/core/popper.min.js"></script>
<script src="../js/core/bootstrap-material-design.min.js"></script>
<script src="../js/plugins/perfect-scrollbar.jquery.min.js"></script>
<!-- Control Center for Material Dashboard: parallax effects, scripts for the example pages etc -->
<script src="../js/material-dashboard.js?v=2.1.0" type="text/javascript"></script>
<script src="../js/jquery.dataTables.js"></script>
<script src="../js/jquery-ui.js"></script>
<script src="../js/jquery.popupoverlay.js"></script> 
<script type="text/javascript" src="../js/dataTables.buttons.js"></script>  
<script type="text/javascript" src="../js/pdfmake.js"></script> --%>
<link href="../css/select2.min.css" rel="stylesheet" />
<script src="../js/jquery.select2.js" type="text/javascript"></script>

<script type="text/javascript">
	$(document).ready(function() {

		// Initialize select2
		$("#merchant").select2();
	});
</script>

<script>
	var expanded = false;
	function showCheckboxes(e) {
		var checkboxes = document.getElementById("checkboxes");
		if (!expanded) {
			checkboxes.style.display = "block";
			expanded = true;
		} else {
			checkboxes.style.display = "none";
			expanded = false;
		}
		e.stopPropagation();

	}

	function getCheckBoxValue() {
		var allInputCheckBox = document.getElementsByClassName("myCheckBox");

		var allSelectedAquirer = [];
		for (var i = 0; i < allInputCheckBox.length; i++) {

			if (allInputCheckBox[i].checked) {
				allSelectedAquirer.push(allInputCheckBox[i].value);
			}
		}

		document.getElementById('selectBox').setAttribute('title',
				allSelectedAquirer.join());
		if (allSelectedAquirer.join().length > 28) {
			var res = allSelectedAquirer.join().substring(0, 27);
			document.querySelector("#selectBox option").innerHTML = res
					+ '...............';
		} else if (allSelectedAquirer.join().length == 0) {
			document.querySelector("#selectBox option").innerHTML = 'ALL';
		} else {
			document.querySelector("#selectBox option").innerHTML = allSelectedAquirer
					.join();
		}
	}
</script>
<script type="text/javascript">
	$(document).ready(function() {
		$(document).click(function() {
			expanded = false;
			$('#checkboxes').hide();
		});
		$('#checkboxes').click(function(e) {
			e.stopPropagation();
		});

	});
</script>


<style>
.divalignment {
	margin-top: -30px !important;
}

.case-design {
	text-decoration: none;
	cursor: default !important;
}

.my_class:hover {
	color: white !important;
}

.multiselect {
	width: 210px;
	display: block;
	margin-left: -20px;
}

.selectBox {
	position: relative;
}

#checkboxes {
	display: none;
	border-radius: 5px;
	border: 1px #dadada solid;
	height: 300px;
	overflow-y: scroll;
	position: Absolute;
	background: #fff;
	z-index: 1;
	margin-left: 2px;
	margin-right: 10px;
}

#checkboxes label {
	width: 74%;
}

#checkboxes input {
	width: 18%;
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

.download-btn {
	background-color: #2b6dd1;
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

.form-control {
	margin-left: 0 !important;
	width: 100% !important;
}

.padding10 {
	padding: 10px;
}

.disableBtn {
	background-color: #cccccc;
	color: black;
}

.smclasshide {
	display: none;
}
</style>


</head>
<body>
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
						Split Settled MIS Report</h1>
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
						<li class="breadcrumb-item text-muted">Account & Finance</li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item"><span
							class="bullet bg-gray-200 w-5px h-2px"></span></li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item text-dark">Split Settled MIS Report</li>
						<!--end::Item-->
					</ul>
					<!--end::Breadcrumb-->
				</div>
				<!--end::Page title-->

			</div>
		</div>
		<div class="post d-flex flex-column-fluid" id="kt_post">
			<!--begin::Container-->
			<div id="kt_content_container" class="container-xxl">
				<div class="row my-5">
					<div class="col">
						<div class="card">
							<div class="card-body">
								<form id="misReportQuery" name="misReportQuery"
									action="SplitSettledMisReportQuery">
									<div class="row g-9 mb-8">
										<div class="col-md-3 fv-row">
											<label class="d-flex align-items-center fs-6 fw-bold mb-2">Merchant</label>
											<s:if
												test="%{#session.USER.UserType.name()=='ADMIN' || #session.USER.UserType.name()=='SUBADMIN'  || #session.USER.UserType.name()=='SUBADMIN' || #session.USER_TYPE.name()=='SUPERADMIN'}">
												<s:select name="merchant"
													class="form-control form-control-solid" id="merchant"
													headerKey="ALL" headerValue="ALL" list="merchantList"
													listKey="payId" listValue="businessName" autocomplete="off" />
											</s:if>
											<s:else>
												<s:select name="merchant"
													class="form-control form-control-solid" id="merchant"
													headerKey="" headerValue="" list="merchantList"
													listKey="payId" listValue="businessName" autocomplete="off" />
											</s:else>
										</div>


										<div class="col-md-3 fv-row smclasshide">


											<label class="d-flex align-items-center fs-6 fw-bold mb-2">Aquirer</label>
											<div>
												<div class="selectBox" id="selectBox"
													onclick="showCheckboxes(event)">
													<select class="form-control form-control-solid">
														<option>ALL</option>
													</select>
													<div class="overSelect"></div>
												</div>
												<div id="checkboxes" onclick="getCheckBoxValue()">
													<s:checkboxlist headerKey="ALL" headerValue="ALL"
														list="@com.pay10.commons.util.AcquirerTypeUI@values()"
														listValue="name" listKey="code" name="name" value="name"
														class="myCheckBox" />
												</div>
											</div>

										</div>

										<div class="col-md-3 fv-row">
											<label class="d-flex align-items-center fs-6 fw-bold mb-2">Currency</label>
											<s:select name="currency" id="currency" headerValue="ALL"
												headerKey="ALL" list="currencyMap"
												class="form-control form-control-solid" />
										</div>


										<div class="col-md-3 fv-row">
										<label class="d-flex align-items-center fs-6 fw-bold mb-2">Date From</label> 
											<s:textfield type="text" readonly="true" id="dateFrom"
												name="dateFrom" class="form-control form-control-solid"
												onchange="handleChange();" autocomplete="off" />
										</div>

										<div class="col-md-3 fv-row">
											<label class="d-flex align-items-center fs-6 fw-bold mb-2">Date To</label>
											<s:textfield type="text" readonly="true" id="dateTo"
												name="dateTo" class="form-control form-control-solid"
												onchange="handleChange();" autocomplete="off" />
										</div>


									</div>
									<div class="row">
										<div class="col-sm-6 col-lg-3">
											<button class="btn btn-primary  mt-4 submit_btn"
												id="downloadReport">Download</button>
										</div>
									</div>
								</form>
							</div>
						</div>
					</div>
				</div>

			</div>
		</div>
	</div>


	<script src="../assets/plugins/custom/datatables/datatables.bundle.js"></script>
	<script
		src="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.js"></script>
	<script type="text/javascript">
		function handleChange() {
			$("#downloadReport").attr("disabled", false);
			$("#downloadReport").removeClass("disableBtn");
			var transFrom = $.datepicker.parseDate('dd-mm-yy', $('#dateFrom')
					.val());
			var transTo = $.datepicker
					.parseDate('dd-mm-yy', $('#dateTo').val());
			if (transFrom == null || transTo == null) {
				alert('Enter date value');
				return false;
			}

			if (transFrom > transTo) {
				alert('From date must be before the to date');
				$('#dateFrom').focus();
				return false;
			}
			if (transTo - transFrom > 7 * 86400000) {
				alert('No. of days can not be more than 7');
				$('#dateFrom').focus();
				$("#downloadReport").attr("disabled", true);
				$("#downloadReport").addClass("disableBtn");
				return false;
			}
		}

		$(document).ready(function() {

			$(function() {
				
				$("#dateFrom").flatpickr({
					maxDate : new Date(),
					dateFormat : 'd-m-Y',
					defaultDate : "today"
				});
				
				$("#dateTo").flatpickr({
						maxDate : new Date(),
						dateFormat : 'd-m-Y',
						defaultDate : "today"
					
				});
			});
			$(function() {
				var today = new Date();
				$('#dateTo').val($.datepicker.formatDate('dd-mm-yy', today));
				$('#dateFrom').val($.datepicker.formatDate('dd-mm-yy', today));

			});
		});
	</script>



</body>
</html>