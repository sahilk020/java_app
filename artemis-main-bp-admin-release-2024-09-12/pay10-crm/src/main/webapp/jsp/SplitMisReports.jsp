<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Split Mis Report</title>

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
	
	
<script src="../js/jquery.min.js"></script>
<script src="../js/core/popper.min.js"></script>
<script src="../js/core/bootstrap-material-design.min.js"></script>
<script src="../js/plugins/perfect-scrollbar.jquery.min.js"></script>
<!-- Control Center for Material Dashboard: parallax effects, scripts for the example pages etc -->
<script src="../js/material-dashboard.js?v=2.1.0" type="text/javascript"></script>
<script src="../js/jquery.dataTables.js"></script>
<script src="../js/jquery-ui.js"></script>
<script src="../js/jquery.popupoverlay.js"></script>
<script type="text/javascript" src="../js/dataTables.buttons.js"></script>
<script type="text/javascript" src="../js/pdfmake.js"></script>
<link href="../css/select2.min.css" rel="stylesheet" />
<script src="../assets/js/scripts.bundle.js"></script>
<script src="../assets/plugins/global/plugins.bundle.js"></script>

<script type="text/javascript">
	$(document).ready(function() {

		// Initialize select2
		//$("#merchant").select2();
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
	width:300px;
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
#downloadReport{
margin-top: 25px;
}
</style>


</head>
<body >
	<div class="content d-flex flex-column flex-column-fluid" id="kt_content">
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
					<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">Split
						MIS Report</h1>
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
						<li class="breadcrumb-item text-dark">Split MIS Report</li>
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
				<form id="misReportQuery" name="misReportQuery"
					action="SplitMisReportQuery">
					
						<!-- <h2>MIS Report</h2> -->
						<div class="row my-5">
							<div class="col">
								<div class="card">
									<div class="card-body">
										<!--begin::Input group-->
										<div class="row g-9 mb-8">
											<!--begin::Col-->
											<div class="col-md-4 fv-row">
												<label class="d-flex align-items-center fs-6 fw-bold mb-2">Merchant</label>
												<s:if
													test="%{#session.USER.UserType.name()=='ADMIN' || #session.USER.UserType.name()=='SUBADMIN'  || #session.USER.UserType.name()=='SUBADMIN' || #session.USER_TYPE.name()=='SUPERADMIN'}">
													
													<s:select name="merchant"
														class="form-select form-select-solid" id="merchant"
														headerKey="ALL" headerValue="ALL" list="merchantList"
														listKey="payId" listValue="businessName"
														autocomplete="off" />
												</s:if>
												<s:else>
													<s:select name="merchant"
														class="form-select form-select-solid" id="merchant"
														headerKey="" headerValue="" list="merchantList"
														listKey="payId" listValue="businessName"
														autocomplete="off" />
												</s:else>
											</div>


											<div class="col-md-4 fv-row">

												<label class="d-flex align-items-center fs-6 fw-bold mb-2">Aquirer</label>
												<div>
													<div class="selectBox" id="selectBox"
														onclick="showCheckboxes(event)">
														<select class="form-select form-select-solid">
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

											<div class="col-md-4 fv-row">
												<label class="d-flex align-items-center fs-6 fw-bold mb-2">Currency</label>
												
												<s:select name="currency" id="currency" headerValue="ALL"
													headerKey="ALL" list="currencyMap" class="form-select form-select-solid" />
											</div>


											<div class="col-md-4 fv-row">
												<label class="d-flex align-items-center fs-6 fw-bold mb-2">Date
													From:</label>
												<s:textfield type="text" readonly="true" id="dateFrom"
													name="dateFrom" class="form-select form-select-solid ps-12"
													 autocomplete="off" />
											</div>

											<div class="col-md-4 fv-row">
												<label class="d-flex align-items-center fs-6 fw-bold mb-2">Date
													To:</label>
												<s:textfield type="text" readonly="true" id="dateTo"
													name="dateTo" class="form-select form-select-solid ps-12"
													 autocomplete="off" />
											</div>

                                           <div class="col-md-4 fv-row">
												<button type="button" class="btn btn-primary"
													id="downloadReport">Download</button>
											</div>
										</div>
										
											
								

									</div>
								</div>
							</div>
						</div>

					
				</form>
			</div>
		</div>

				<script type="text/javascript">
				$("#downloadReport").click(function() {
					debugger
					
					var datearray = document.getElementById('dateFrom').value.split("-");
					var newdate=datearray[1] + '/' + datearray[0] + '/' + datearray[2];
					
					var datearray2 = document.getElementById('dateTo').value.split("-");
					var newdate2=datearray2[1] + '/' + datearray2[0] + '/' + datearray2[2];
					 var transFrom = new Date(newdate);
					    var transTo = new Date(newdate2);
					      
					    // To calculate the time difference of two dates
					    var Difference_In_Time = transTo.getTime() - transFrom.getTime();
					      
					    // To calculate the no. of days between two dates
					    var Difference_In_Days = Difference_In_Time / (1000 * 3600 * 24);
					      
					 
			 	if (transFrom == null || transTo == null) {
						alert('Enter date value');
						return false;
					}

					if (transFrom > transTo) {
						alert('From date must be before the to date');
						$('#dateFrom').focus();
						return false;
					}
					if (Difference_In_Days>= 7) {
						alert('No. of days can not be more than 7');
						$('#dateFrom').focus();
						
						return false;
					}
					else{
						$("#misReportQuery").submit();
						} 
					});

				

					$(document).ready(
							function() {

								
								
								 // var fromDate = new Date();
								  //fromDate.setMonth(fromDate.getMonth() -2);
								  //fromDate.setDate(fromDate.getDate() -2);
								$("#dateFrom").flatpickr({
								//	minDate : fromDate,
									maxDate: new Date(),
									dateFormat: "d-m-Y",
									defaultDate: "today"
									
								});
								$("#dateTo").flatpickr({
									//minDate : fromDate,
									maxDate: new Date(),
									dateFormat: "d-m-Y",
									defaultDate: "today"
								});
								
							});
					
				</script>
</body>
</html>