<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.Locale"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.text.Format"%>


<html dir="ltr" lang="en-US">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Status Wise Line Chart</title>



<script type="text/javascript" src="../js/jquery.min.js"></script>
<script src="../js/core/popper.min.js"></script>
<script src="../js/core/bootstrap-material-design.min.js"></script>
<script src="../js/plugins/perfect-scrollbar.jquery.min.js"></script>
<!-- Control Center for Material Dashboard: parallax effects, scripts for the example pages etc -->
<script src="../js/material-dashboard.js?v=2.1.0" type="text/javascript"></script>
<%-- <script src="../assets/plugins/global/plugins.bundle.js"></script>
<script src="../assets/js/scripts.bundle.js"></script> --%>
<link href="../fonts/css/font-awesome.min.css" rel="stylesheet">

<link href="../css/select2.min.css" rel="stylesheet" />

<script type="text/javascript" src="../js/sweetalert.js"></script>
<script type="text/javascript" src="../js/offus.js"></script>
<link rel="stylesheet" href="../css/sweetalert.css">

<script type="text/javascript" src="../js/canvasChart/canvas.min.js"></script>
<script src="../js/jquery.select2.js" type="text/javascript"></script>

<style type="text/css">
li.nav-item.ml-2 {
    margin-left: 10px !important;
}

[class^="Acquirer"] .wwlbl .label {
	color: #666;
	font-size: 12px;
	padding: 2px 0 8px;
	display: block;
	margin: 0;
	text-align: left;
	font-weight: 600;
}

[class^="Acquirer"] .wwlbl+br {
	display: none;
}

.card-list-toggle {
	cursor: pointer;
	padding: 8px 12px;
	border: 1px solid #496cb6;
	position: relative;
	background: linear-gradient(60deg, #425185, #4a9b9b);
}

.card-list-toggle:before {
	position: absolute;
	right: 10px;
	top: 7px;
	content: "\f078";
	font-family: 'FontAwesome';
	font-size: 15px;
}

.dt-buttons {
	margin-top: 35px !important;
}

div#example_filter {
	margin-top: 35px !important;
}

.dt-button-collection a.dt-button.buttons-columnVisibility {
	background: unset;
	background-color: rgb(143, 162, 214) !important;
}

.dt-button-collection a.dt-button.buttons-columnVisibility.active {
	background-color: #2d4c5c !important;
}

.card-list-toggle.active:before {
	content: "\f077";
}

.card-list {
	display: none;
}

.acquirerRemoveBtn {
	float: left;
}

[class*="AcquirerList"]>div {
	clear: both;
	border-bottom: 1px solid #ccc;
	padding: 0 8px 5px;
	margin: 0 -8px 8px;
}

[class*="AcquirerList"]>div:last-child {
	border-bottom: none;
	padding-bottom: 0;
	margin-bottom: 0;
}

[class^="AcquirerList"] input[disabled] {
	color: #bbb;
	display: none;
}

[class^="AcquirerList"] input[disabled]+label {
	color: #bbb;
	display: none;
}

[class*="OtherList"]>div {
	clear: both;
	border-bottom: 1px solid #ccc;
	padding: 0 8px 5px;
	margin: 0 -8px 8px;
}

[class*="OtherList"]>div:last-child {
	border-bottom: none;
	padding-bottom: 0;
	margin-bottom: 0;
}

[class^="OtherList"] input[disabled] {
	color: #bbb;
	display: none;
}

[class^="OtherList"] input[disabled]+label {
	color: #bbb;
	display: none;
}

.sweet-alert .sa-icon {
	margin-bottom: 30px;
}

.sweet-alert .lead.text-muted {
	font-size: 14px;
}

.sweet-alert .btn {
	font-size: 12px;
	padding: 8px 30px;
	margin: 0 5px;
}

table.product-spec.disabled {
	cursor: not-allowed;
	opacity: 0.5;
}

table.product-spec.disabled .btn {
	pointer-events: none;
}

.merchantFilter {
	padding: 15px 0;
	width: 200px;
}

.AcquirerList input[type="radio"] {
	vertical-align: top;
	float: left;
	margin: 2px 5px 0 0;
}

.AcquirerList label {
	vertical-align: middle;
	display: block;
	font-weight: normal;
}

<!--
.Acquirer1 input[type="radio"] {
	vertical-align: top;
	float: left;
	margin: 2px 5px 0 0;
}

.Acquirer1 label {
	vertical-align: middle;
	display: block;
	font-weight: normal;
}

-->
.boxtext td div input[type="radio"] {
	vertical-align: top;
	float: left;
	margin: 2px 5px 0 0;
}

.boxtext td div label {
	vertical-align: middle;
	display: block;
	font-weight: normal;
}

#onus_section .checkbox, #offus_section .checkbox {
	margin: 0;
}

.checkbox .wwgrp input[type="checkbox"] {
	margin-left: 0;
}

.checkbox label .wwgrp input[type="checkbox"] {
	margin-left: -20px;
}

.select2-container {
	width: 200px !important;
}

.btn:focus {
	outline: 0 !important;
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

@media ( min-width : 768px) {
	.navbar>.container .navbar-brand, .navbar>.container-fluid .navbar-brand
		{
		margin-left: 0px !important;
	}
}

.dropdown-menu>li>a:focus, .dropdown-menu>li>a:hover {
	color: #ffffff !important;
	text-decoration: none;
	background-color: #496cb6 !important;
}

input#reportrange {
	border: unset;
}
input#reportrange1 {
	border: unset;
}
input#reportrange2 {
	border: unset;
}

canvasjs-chart-credit{
display:none
}

</style>
<%
DecimalFormat d = new DecimalFormat("0.00");
Date d1 = new Date();
SimpleDateFormat df = new SimpleDateFormat("dd-MM-YYYY");
String currentDate = df.format(d1);
%>


<link href="../css/Jquerydatatableview.css" rel="stylesheet" />
<link href="../css/Jquerydatatable.css" rel="stylesheet" />
<!-- <link href="../css/default.css" rel="stylesheet" type="text/css" /> -->
<link href="../css/jquery-ui.css" rel="stylesheet" />
<link rel="stylesheet" href="../css/daterangepickerdashboard.css">
<link href="../assets/css/style.bundle.css" rel="stylesheet"
	type="text/css" />
<link href="../assets/css/custom/custom-style.css" rel="stylesheet"
	type="text/css" />
<script src="../assets/plugins/global/plugins.bundle.js"></script>
<script src="../assets/js/scripts.bundle.js"></script>
<script src="../js/jquery.dataTables.js"></script>
<script src="../js/jquery.popupoverlay.js"></script>

<script type="text/javascript" src="../js/dataTables.buttons.js"></script>
<script type="text/javascript" src="../js/pdfmake.js"></script>

<script type="text/javascript" src="../js/momentdashboard.min.js"></script>
<script type="text/javascript"
	src="../js/daterangepickerdashboard.min.js"></script>
	<script type="text/javascript" src="../js/canvasChart/multiSeriesLineChartDataPopulate.js"></script>


<script type="text/javascript">
	$(function() {
		$("select").select2();
		var start=null;
		var end=null;
		//debugger;
		const dateRange = '<%=(String) request.getAttribute("dateRange")%>';
		if(dateRange!='null'){
			//07/13/2022 - 07/15/2022
			const a=dateRange.split(" ");
			const startdate=a[0].split("/");
			const enddate=a[2].split("/");
			start= moment().set({'month':parseInt(startdate[0])-1,'date':parseInt(startdate[1]),'year':parseInt(startdate[2])});
			end = moment().set({'month':parseInt(enddate[0])-1,'date':parseInt(enddate[1]),'year':parseInt(enddate[2])});

		}else{
			start= moment().subtract(29, 'days');
			end = moment();
		}


		function cb(start, end) {
			$('#reportrange span').html(
					start.format('DD/MM/YYYY') + ' - '
							+ end.format('DD/MM/YYYY'));
		}

		$('#reportrange').daterangepicker(
				{
					startDate : start,
					endDate : end,
					maxDate: new Date(),
					dateLimit : {
						days : 30
					},

					ranges : {
						'Today' : [ moment(), moment() ],
						'Yesterday' : [ moment().subtract(1, 'days'),
								moment().subtract(1, 'days') ],
						'Last 7 Days' : [ moment().subtract(6, 'days'),
								moment() ],
						'Last 30 Days' : [ moment().subtract(29, 'days'),
								moment() ],
						'This Month' : [ moment().startOf('month'),
								moment().endOf('month') ],
						'Last Month' : [
								moment().subtract(1, 'month').startOf('month'),
								moment().subtract(1, 'month').endOf('month') ]
					}
				}, cb);

		cb(start, end);

	});



	$(function() {
		var start=null;
		var end=null;
		//debugger;
		const dateRange = '<%=(String) request.getAttribute("dateRange")%>';
		if(dateRange!='null'){
			//07/13/2022 - 07/15/2022
			const a=dateRange.split(" ");
			const startdate=a[0].split("/");
			const enddate=a[2].split("/");
			start= moment().set({'month':parseInt(startdate[0])-1,'date':parseInt(startdate[1]),'year':parseInt(startdate[2])});
			end = moment().set({'month':parseInt(enddate[0])-1,'date':parseInt(enddate[1]),'year':parseInt(enddate[2])});

		}else{
			start= moment().subtract(29, 'days');
			end = moment();
		}


		function cb(start, end) {
			$('#reportrange span').html(
					start.format('DD/MM/YYYY') + ' - '
							+ end.format('DD/MM/YYYY'));

							$('#reportrange1 span').html(
					start.format('DD/MM/YYYY') + ' - '
							+ end.format('DD/MM/YYYY'));

							$('#reportrange2 span').html(
					start.format('DD/MM/YYYY') + ' - '
							+ end.format('DD/MM/YYYY'));
		}

		$('#reportrange').daterangepicker(
				{
					startDate : start,
					endDate : end,
					maxDate: new Date(),
					dateLimit : {
						days : 30
					},

					ranges : {
						'Today' : [ moment(), moment() ],
						'Yesterday' : [ moment().subtract(1, 'days'),
								moment().subtract(1, 'days') ],
						'Last 7 Days' : [ moment().subtract(6, 'days'),
								moment() ],
						'Last 30 Days' : [ moment().subtract(29, 'days'),
								moment() ],
						'This Month' : [ moment().startOf('month'),
								moment().endOf('month') ],
						'Last Month' : [
								moment().subtract(1, 'month').startOf('month'),
								moment().subtract(1, 'month').endOf('month') ]
					}
				}, cb);
				$('#reportrange1').daterangepicker(
				{
					startDate : start,
					endDate : end,
					maxDate: new Date(),
					dateLimit : {
						days : 30
					},

					ranges : {
						'Today' : [ moment(), moment() ],
						'Yesterday' : [ moment().subtract(1, 'days'),
								moment().subtract(1, 'days') ],
						'Last 7 Days' : [ moment().subtract(6, 'days'),
								moment() ],
						'Last 30 Days' : [ moment().subtract(29, 'days'),
								moment() ],
						'This Month' : [ moment().startOf('month'),
								moment().endOf('month') ],
						'Last Month' : [
								moment().subtract(1, 'month').startOf('month'),
								moment().subtract(1, 'month').endOf('month') ]
					}
				}, cb);
				$('#reportrange2').daterangepicker(
				{
					startDate : start,
					endDate : end,
					maxDate: new Date(),
					dateLimit : {
						days : 30
					},

					ranges : {
						'Today' : [ moment(), moment() ],
						'Yesterday' : [ moment().subtract(1, 'days'),
								moment().subtract(1, 'days') ],
						'Last 7 Days' : [ moment().subtract(6, 'days'),
								moment() ],
						'Last 30 Days' : [ moment().subtract(29, 'days'),
								moment() ],
						'This Month' : [ moment().startOf('month'),
								moment().endOf('month') ],
						'Last Month' : [
								moment().subtract(1, 'month').startOf('month'),
								moment().subtract(1, 'month').endOf('month') ]
					}
				}, cb);
		cb(start, end);

	});

</script>
<script>

function fetchStatusWiseChart(chartType){

	var obj;
	var chartName='Transactions By';

if(chartType=='status'){
	obj= {

				"status"      : $("#status").val(),
				"range"  : $("#reportrange").val(),
				"type"  :"STATUS_CHART",

			  }
chartName =chartName+' Status';
}else if(chartType=='acquirer'){
	obj= {

				"acquirer"      : $("#acquirer").val(),
				"range"  : $("#reportrange1").val(),
				"type"  :"ACQUIRER_CHART",
				"statusType" : $("#statusType1").val()

			  }

			  chartName =chartName+' Acquirer';
}else if(chartType=='paymentType'){
	obj= {

				"paymentType"      : $("#paymentMethods").val(),
				"range"  : $("#reportrange2").val(),
				"type"  :"PAYMENT_TYPE_CHART",
				"statusType" : $("#statusType2").val()

			  }

			  chartName =chartName+' Payment Type';
}



	//populateData("Pay10 Transaction Chart By Status")


	var id = '<s:property value="id"/>';
	var urls = new URL(window.location.href);
	var domain = urls.origin;
	$.ajax({
	type : "POST",
    url: domain+"/crmws/dashboard/lineChartData",
     data: JSON.stringify(obj),
     contentType: "application/json",
     success : function(data) {
    	 populateData(data,chartName);
      }
	   });
	  }

$(window).on('load', function() {
    // Code to execute after complete page content load
    $("#fetch").trigger('click'); // Trigger click event on button with ID "fetch"
});




	</script>

<style>

.tab-content>.tab-pane{
border:unset !important;
}
.nav-tabs .nav-item .nav-link.active {
    background-color: rgb(68 97 138) !important;
}
.nav-tabs .nav-item .nav-link{
	background-color:#202f4bbf !important;
}
.nav-tabs .nav-item .nav-link.active {
    background-color:#202f4b !important;
    color: #fff;
}

#fetch{
 margin-top:36px;
 }
 #fetch1{
 margin-top:36px;
 }
 #fetch2{
 margin-top:36px;
 }

</style>
</head>
<body>

	 <div style="overflow: auto !important;">
<!--<div class="content flex-column" id="kt_content"> -->
<div class="content d-flex flex-column flex-column-fluid" id="kt_content">
		<!--begin::Toolbar-->
		<div class="toolbar" id="kt_toolbar">
			<!--begin::Container-->
			<div id="kt_toolbar_container" class="container-fluid d-flex flex-stack">
				<!--begin::Page title-->
				<div data-kt-swapper="true" data-kt-swapper-mode="prepend" data-kt-swapper-parent="{default: '#kt_content_container', 'lg': '#kt_toolbar_container'}" class="page-title d-flex align-items-center me-3 flex-wrap mb-5 mb-lg-0 lh-1">
					<!--begin::Title-->
					<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">Transaction Analytics</h1>
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
						<li class="breadcrumb-item text-muted">Analytics</li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item">
							<span class="bullet bg-gray-200 w-5px h-2px"></span>
						</li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item text-dark">Transaction Analytics</li>
						<!--end::Item-->
					</ul>
					<!--end::Breadcrumb-->
				</div>
				<!--end::Page title-->

			</div>
			<!--end::Container-->
		</div>


		<div class="content d-flex flex-column flex-column-fluid" id="kt_content">

		<div class="post d-flex flex-column-fluid" id="kt_post">
		<div id="kt_content_container" class="container-xxl">
		<div class="row my-5">
		<div class="col">
		<div class="card">
		<div class="card-body ">

		<div class="row">
			<div class="col-12 col-md-12 col-lg-12">
			  <ul class="nav nav-tabs">
				<li class="nav-item ml-2">
				  <a class="nav-link active" data-toggle="pill" href="#statuswise" role="tab" aria-controls="pills-statuswise" aria-selected="true" onclick="resetChart('1')">Status Wise</a>
				</li>
				<li class="nav-item ml-2">
				  <a class="nav-link" data-toggle="pill" href="#acquirerwise" role="tab" aria-controls="pills-acquirerwise" aria-selected="false" onclick="resetChart('2')">Acquirer Wise</a>
				</li>
				<li class="nav-item ml-2">
				  <a class="nav-link" data-toggle="pill" href="#mopwise" role="tab" aria-controls="pills-mopwise" aria-selected="false" onclick="resetChart('3')">MopType Wise</a>
				</li>

			  </ul>
			  <div class="tab-content mt-3">
			  <div class="tab-pane fade show active" id="statuswise" role="tabpanel" aria-labelledby="statuswise-tab">
			<form id="files" method="post" enctype="multipart/form-data">

						<div class="row">
							<div class="col-lg-4 col-md-6 col-sm-12">
								<label class="d-flex align-items-center fs-6 fw-bold mb-2">Select Date </label><br>
								<div
									style="border: 1px solid #4a57f959; background: #fff; cursor: pointer; font-size: 14px; position: relative; display: inline-flex;width: 100%;">
									<input type="text" id="reportrange" name="dateRange" class="form-control form-control-solid ps-12">
									<!-- <i class="fa fa-calendar mt-1"></i><i
										class="fa fa-caret-down mt-1"></i> --> <span id="dateRange"
										style="font-size: 14px; color: black; margin-top: 5px;">
									</span>
								</div>
							</div>

							<div class="col-sm-6 col-lg-3">

									<label class="d-flex align-items-center fs-6 fw-bold mb-2" for="">Status</label><br />
									<div class="txtnew">
										<s:select headerKey="ALL" headerValue="ALL" class="form-control form-control-solid"
										list="@com.pay10.commons.util.StatusTypeUI@values()"
										listValue="uiName" listKey="code" name="status"
										id="status" autocomplete="off" value="" />
									</div>
								  </div>
								  <div class="col mt-2">
									<button type="button" class="btn btn-primary submitbtn" onclick="fetchStatusWiseChart('status')";
										id="fetch">FETCH</button>


								</div>
						</div>

					</form>
					<!-- <div id="chartContainer" style="height: 500px; width: 100%; margin-top: 10px;"></div> -->

			</div>

			  <div class="tab-pane fade" id="acquirerwise" role="tabpanel" aria-labelledby="profile-tab">
				<form id="files1" method="post" enctype="multipart/form-data">

					<div class="row">
						<div class="col-lg-4 col-md-6 col-sm-12">
							<label class="d-flex align-items-center fs-6 fw-bold mb-2">Select Date </label><br>
							<div
								style="border: 1px solid #4a57f959; background: #fff;s font-size: 14px; position: relative; display: inline-flex;width: 100%">
								<input type="text" id="reportrange1" name="dateRange" class="form-control form-control-solid ps-12">
								<!-- <i class="fa fa-calendar mt-1"></i>&nbsp; <i
									class="fa fa-caret-down mt-1"></i> --> <span id="dateRange"
									style="font-size: 14px; color: black; margin-top: 5px;">
								</span>
							</div>
						</div>

						<div class="col-sm-6 col-lg-3">
							<label  class="d-flex align-items-center fs-6 fw-bold mb-2">Acquirer </label><br>
							<div class="form-group">
								<s:select headerKey="ALL" headerValue="ALL"
									list="@com.pay10.commons.util.AcquirerTypeUI@values()"
									id="acquirer" class="form-control form-control-solid" name="acquirer"
									value="acquirer" listValue="name" listKey="code" />
							</div>

							  </div>

							  <div class="col-sm-6 col-lg-3">
							<label  class="d-flex align-items-center fs-6 fw-bold mb-2">Status Type</label><br>
							<div class="form-group">
								<s:select
									list="@com.pay10.commons.util.StatusTypeReports@values()"
									id="statusType1" class="form-control form-control-solid" name="statusType"
									value="statusType" listValue="code" listKey="code" />
							</div>
							  </div>

							  <div class="col mt-2">
								<button type="button" class="btn btn-primary submitbtn" onclick="fetchStatusWiseChart('acquirer')";
									id="fetch1">FETCH</button>


							</div>
					</div>

				</form>
				<!-- <div id="chartContainer1" style="height: 500px; width: 100%; margin-top: 10px;"></div>		 -->
				  </div>

			  <div class="tab-pane fade" id="mopwise" role="tabpanel" aria-labelledby="mopwise-tab">
				<form id="files2" method="post" enctype="multipart/form-data">

					<div class="row">
						<div class="col-lg-4 col-md-6 col-sm-12">
							<label class="d-flex align-items-center fs-6 fw-bold mb-2">Select Date </label><br>
							<div
								style="border:1px solid #4a57f959; background: #fff;s font-size: 14px; position: relative; display: inline-flex;width: 100%">
								<input type="text" id="reportrange2" name="dateRange" class="form-control form-control-solid ps-12">
								<!-- <i class="fa fa-calendar mt-1"></i>&nbsp; <i
									class="fa fa-caret-down mt-1"></i> --> <span id="dateRange"
									style="font-size: 14px; color: black; margin-top: 5px;">
								</span>
							</div>
						</div>

						<div class="col-sm-6 col-lg-3">

							<label class="d-flex align-items-center fs-6 fw-bold mb-2" for="">Payment Type</label><br />
								<div class="form-group">
									<s:select class="form-control form-control-solid"
										list="@com.pay10.commons.util.PaymentTypeUI@values()" listKey="code"
										 listValue="name"
										id="paymentMethods" headerKey="All" headerValue="ALL" />
								</div>
							  </div>

							   <div class="col-sm-6 col-lg-3">
							<label class="d-flex align-items-center fs-6 fw-bold mb-2">Status Type</label><br>
							<div class="form-group">
								<s:select

									list="@com.pay10.commons.util.StatusTypeReports@values()"
									id="statusType2" class="form-control form-control-solid" name="statusType"
									value="statusType" listValue="code" listKey="code" />
							</div>

							  </div>
							  <div class="col mt-2">
								<button type="button" class="btn btn-primary submitbtn" onclick="fetchStatusWiseChart('paymentType')";
									id="fetch2">FETCH</button>


							</div>
					</div>

				</form>
						  </div>
						  <div id="chartContainer" style="height: 500px; width: 100%; margin-top: 10px;"></div>

			 
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
</div>
 </div>
</body>
</html>