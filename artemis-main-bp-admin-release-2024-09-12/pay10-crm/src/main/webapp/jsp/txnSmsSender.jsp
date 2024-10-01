<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
	<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
	<%@ taglib uri="/struts-tags" prefix="s" %>
		<html>

		<head>
			<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
			<title>Transaction SMS Sender</title>
			<!--------StyleSheet------>
			<!-- <link href="../css/default.css" rel="stylesheet" type="text/css" />
<link href="../css/jquery-ui.css" rel="stylesheet" />
<link href="../css/select2.min.css" rel="stylesheet" /> -->

			<!-------JavaScript------->
			<!-- <script src="../js/jquery.min.js" type="text/javascript"></script>
<script src="../js/core/popper.min.js"></script>
<script src="../js/core/bootstrap-material-design.min.js"></script> -->
			<script src="../js/plugins/perfect-scrollbar.jquery.min.js"></script>
			<!-- Control Center for Material Dashboard: parallax effects, scripts for the example pages etc -->
			<!-- <script src="../js/material-dashboard.js?v=2.1.0" type="text/javascript"></script>
<script src="../js/jquery-ui.js"></script>
<script src="../js/pdfmake.js" type="text/javascript"></script>
<script src="../js/jszip.min.js" type="text/javascript"></script>
<script src="../js/vfs_fonts.js" type="text/javascript"></script>
<script src="../js/jquery.select2.js" type="text/javascript"></script> -->





			<link rel="shortcut icon" href="assets/media/images/paylogo.svg" />
			<!--begin::Fonts-->
			<link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Inter:300,400,500,600,700" />
			<!--end::Fonts-->
			<!--begin::Vendor Stylesheets(used by this page)-->
			<link href="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.css" rel="stylesheet"
				type="text/css" />
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
					document.getElementById("loading").style.display = "none";
					//$("#merchants").select2();
				});
			</script>
			<style>
				input:focus,
				select:focus,
				textarea:focus,
				button:focus {
					outline: none;
				}

				.blueBg {
					background: #03a9f4;
				}

				.orangeBg {
					background: #f0ad4e;
				}

				.redBg {
					background: #f0433d;
				}

				.drkBlueBg {
					background: #355c7d;
				}

				/* .box2{
	margin-top: 11px;
	padding: 11px;
}
.box1, .box3, .box4{
	padding: 11px;
}
.box1 .media-heading{
		text-align: center;
		font-size: 14px;
}
.box1 .media-left{
		padding: 10px 14px;
		width: 70px;
		height: 58px;
}
.box1 .media-body{
		width: 80%;
		background: #f4f2f2;
		padding-top: 4%;
}
.box2 input, .box2 select, .box2 button{
		font-size: 13px;
}
.box4 button{
		background: #5db85b;
		color: #fff;
		border: none;
		display: block;
		width: 200px;
		padding: 6px;
		margin: 0 auto;
		font-size: 13px;
}
.box2 select, .box2 input{
	border:1px solid #d4d4d4;
} */
				.chartBox {
					background: #f8f8f8;
				}

				.cardTypeTxnDetails {
					background: #f8f8f8;
				}

				.mytable,
				.mytable thead th {
					text-align: center;
					font-size: 13px;
				}

				.mytable thead th {
					font-size: 14px;
				}

				.mytable thead {
					background: #428bca;
					color: #fff;
				}

				.mainDiv.txnf h3 {
					color: #496cb6;
					font-size: 16px;
					margin-top: 20px;
				}

				#showForParticular {
					display: none;
				}

				#checkboxes {
					display: none;
					border: 1px #dadada solid;
					height: 300px;
					overflow-y: scroll;
					position: Absolute;
					background: #fff;
					z-index: 1;
					margin-left: 5px;
				}

				#checkboxes label {
					width: 78%;
				}

				#checkboxes input {
					width: 18%;
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
					left: 45%;
					z-index: 100
				}

				.selectBox select {
					width: 100%;
				}

				.overSelect {
					position: absolute;
					left: 0;
					right: 0;
					top: 0;
					bottom: 0;
				}

				/* label{
		color: #333;
		font-size: 13px;
		padding-left: 4%;
} */
				#PaymentTypePerformance {
					padding: 11px;
				}

				.loader {
					border: 16px solid #f3f3f3;
					/* Light grey */
					border-top: 16px solid #3498db;
					/* Blue */
					border-radius: 50%;
					width: 120px;
					height: 120px;
					animation: spin 2s linear infinite;
				}

				@keyframes spin {
					0% {
						transform: rotate(0deg);
					}

					100% {
						transform: rotate(360deg);
					}
				}

				.baseClass {
					width: 100%;
					float: left;
					text-align: center;
				}

				.baseClass button {
					display: inline-block;
				}

				.baseClass1 {
					width: 100%;
					float: left;
					text-align: center;
					height: 50px !important;
				}

				.baseClass1 button {
					display: inline-block;
					height: 50px !important;
				}

				/* #submit1{
    float: left;
    background: #46a145;
    color: #fff;
    border: none;
    display: block;
    width: 190px;
    padding: 6px;
    margin-left: 2%;
    margin-top: 3%;
    margin-bottom: 3%;
    border-radius: 5px;
    font-size: 13px;
}
#submit1:hover{
	background: #5db85b;
}
.sbmtBtn{
	float: left;
    background: #46a145;
    color: #fff;
    border: none;
    display: block;
    width: 190px;
    padding: 6px;
    margin-left: 1%;
    margin-top: 3%;
    margin-bottom: 3%;
    border-radius: 5px;
    font-size: 13px;
}
.sbmtBtn:hover{
	background: #5db85b;
}
#captured2{
	background: #46a145;
    color: #fff;
    border: none;
    width: 200px;
    padding: 6px;
    margin-top: 3%;
    margin-bottom: 3%;
    border-radius: 5px;
    font-size: 13px;
    float: left;
    margin-left: 10%;
}

#settledTransBtn{
	line-height: 5px;
    font-size: 13px;
    width: 20%;
    background: #46a145;
    height: 30px;
    padding: 3px 4px;
    color: #fff;
    border: 1px solid #ccc;
    border-radius: 4px;
    margin-top: 35px;
    float: left;
    margin-left: 7.6%;
}
#settledTransBtn:hover{
	background: #5db85b;
}
#captured2:hover{
	background: #5db85b
}
#settled3{
    background: #46a145;
    color: #fff;
    border: none;
    display: block;
    width: 190px;
    padding: 6px;
    margin-top: 3%;
    margin-bottom: 3%;
    border-radius: 5px;
    font-size: 13px;
    float: left;
}
#settled3:hover{
	background:#5db85b;
}
#settled4{
	float: left;
    background: #46a145;
    color: #fff;
    border: none;
    display: block;
    width: 200px;
    padding: 6px;
    margin-top: 3%;
    margin-right: -80px;
    margin-bottom: 3%;
    border-radius: 5px;
    font-size: 13px;
}
#settled4:hover{
	background:#5db85b;
} */
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
					top: 45%;
					left: 50%;
					z-index: 100;
					width: 15%;
				}
			</style>
	<style>
		span.select2-selection.select2-selection--single.form-select.form-select-solid.merchantPayId {
			padding: 6px 92px !important;
		}
	</style>	
			<script type="text/javascript">
				$(document).ready(function () {
					// Initialize select2
					$("#merchants").select2();

					document.getElementById("loading").style.display = "none"
					$(function () {
						$("#dateFrom").datepicker({
							prevText: "click for previous months",
							nextText: "click for next months",
							showOtherMonths: true,
							dateFormat: 'dd-mm-yy',
							selectOtherMonths: false,
							maxDate: new Date()
						});
						$("#dateTo").datepicker({
							prevText: "click for previous months",
							nextText: "click for next months",
							showOtherMonths: true,
							dateFormat: 'dd-mm-yy',
							selectOtherMonths: false,
							maxDate: new Date()
						});
					});

					$(function () {
						var today = new Date();
						$('#dateTo').val($.datepicker.formatDate('dd-mm-yy', today));
						$('#dateFrom').val($.datepicker.formatDate('dd-mm-yy', today));

					});

				})
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
					document.getElementById('selectBox').setAttribute('title', allSelectedAquirer.join());
					if (allSelectedAquirer.join().length > 28) {
						var res = allSelectedAquirer.join().substring(0, 27);
						document.querySelector("#selectBox option").innerHTML = res + '...............';
					} else if (allSelectedAquirer.join().length == 0) {
						document.querySelector("#selectBox option").innerHTML = 'ALL';
					} else {
						document.querySelector("#selectBox option").innerHTML = allSelectedAquirer.join();
					}
				}
			</script>

			<script type="text/javascript">
				$(document).ready(function () {
					$(document).click(function () {
						expanded = false;
						$('#checkboxes').hide();
					});
					$('#checkboxes').click(function (e) {
						e.stopPropagation();
					});
				});
			</script>

			<script>
				function sendPerformanceSms(smsData) {

					if (confirm('Are you sure you want to Send SMS')) {

					}
					else {
						event.stopPropagation;
						return false;
					}

					var acquirer = [];
					var inputElements = document.getElementsByName('acquirer');
					for (var i = 0; inputElements[i]; ++i) {
						if (inputElements[i].checked) {
							acquirer.push(inputElements[i].value);

						}
					}
					var acquirerString = acquirer.join();

					var merchantEmailId = document.getElementById("merchants").value;
					var sel = document.getElementById("merchants");
					var merchantName = sel.options[sel.selectedIndex].text;
					var dateFrom = document.getElementById("dateFrom").value;
					var dateTo = document.getElementById("dateTo").value;
					var paymentMethods = document.getElementById("paymentMethods").value;
					var smsParam = smsData;

					if (merchantEmailId == '') {
						merchantEmailId = 'ALL'
					}
					if (paymentMethods == '') {
						paymentMethods = 'ALL'
					}

					if (acquirer == '') {
						acquirer = 'ALL'
					}

					if (merchantEmailId == 'ALL') {

						alert("Please select a merchant to send SMS ");
						return false;
					}

					if (smsData == 'capturedData') {
						alert("Sending captured data to merchant " + merchantName + " for " + dateFrom + " to " + dateTo);
					}
					else if (smsData == 'iPaycapturedData') {
						alert("Sending iPaycaptured data to iPay " + merchantName + " for " + dateFrom + " to " + dateTo);
					}
					else {
						alert("Sending settled data to merchant " + merchantName + " for " + dateFrom + " to " + dateTo);

					}

					var token = document.getElementsByName("token")[0].value;
					document.getElementById("loading").style.display = "block"

					$
						.ajax({
							url: "sendPerformanceSmsAction",
							timeout: 0,
							type: "POST",
							data: {
								paymentMethods: paymentMethods,
								dateFrom: dateFrom,
								dateTo: dateTo,
								merchantEmailId: merchantEmailId,
								token: token,
								acquirer: acquirerString,
								smsParam: smsParam,
							},
							success: function (data) {
								document.getElementById("loading").style.display = "none"
								var response = ((data["Invalid request"] != null) ? (data["Invalid request"].response[0]) : (data.response));
								if (null != response) {
									alert(response);
								}
							},
							error: function (data) {
								document.getElementById("loading").style.display = "none"
							}
						});

				}
			</script>

		</head>

		<body>

			<div id="loading" style="text-align: center;">
				<img id="loading-image" style="width:70px;height:70px;" src="../image/sand-clock-loader.gif"
					alt="Sending SMS..." />
			</div>



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
								Transaction SMS Sender</h1>
							<!--end::Title-->
							<!--begin::Separator-->
							<span class="h-20px border-gray-200 border-start mx-4"></span>
							<!--end::Separator-->
							<!--begin::Breadcrumb-->
							<ul class="breadcrumb breadcrumb-separatorless fw-semibold fs-7 my-1">
								<!--begin::Item-->
								<li class="breadcrumb-item text-muted"><a href="home"
										class="text-muted text-hover-primary">Dashboard</a>
								</li>
								<!--end::Item-->
								<!--begin::Item-->
								<li class="breadcrumb-item"><span class="bullet bg-gray-200 w-5px h-2px"></span>
								</li>
								<!--end::Item-->
								<!--begin::Item-->
								<li class="breadcrumb-item text-muted">Notification Engine
								</li>
								<!--end::Item-->
								<!--begin::Item-->
								<li class="breadcrumb-item"><span class="bullet bg-gray-200 w-5px h-2px"></span>
								</li>
								<!--end::Item-->
								<!--begin::Item-->
								<li class="breadcrumb-item text-dark"> Transaction SMS Sender
								</li>
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

											<div class="col-auto my-2 merchant-text">
												<p class="text-center m-0 w-100">
													<b>Date From</b>
												</p class="text-sm-center m-0">
											</div>
											<div class="col-auto my-2">
												<s:textfield type="text" id="dateFrom" name="dateFrom" class="input-control"
												autocomplete="off" readonly="true" />
											</div>
											<div class="col-auto my-2 merchant-text">
												<p class="text-center m-0 w-100">
													<b>Date To</b>
												</p class="text-sm-center m-0">
											</div>
											<div class="col-auto my-2">
												<s:textfield type="text" id="dateTo" name="dateTo" class="input-control"
												autocomplete="off" readonly="true" style="width: 103% !important;" />
											</div>
											<div class="col-auto my-2 merchant-text">
												<p class="text-center m-0 w-100">
													<b>Acquirer</b>
												</p class="text-sm-center m-0">
											</div>
											<div class="col-auto my-2">
												<div>
													<div class="selectBox" id="selectBox" onclick="showCheckboxes(event)"
														title="dummy Title">
														<select class="form-select form-select-solid merchantPayId">
															<option>ALL</option>
														</select>
														<div class="overSelect"></div>
													</div>
													<div id="checkboxes" onclick="getCheckBoxValue()">
														<s:checkboxlist headerKey="ALL" headerValue="ALL"
															list="@com.pay10.commons.util.AcquirerTypeUI@values()" id="acquirer"
															class="myCheckBox" name="acquirer" value="acquirer" listValue="name"
															listKey="code" />
													</div>
												</div>
											</div>
											<div class="col-auto my-2 merchant-text">
												<p class="text-center m-0 w-100">
													<b>Merchant</b>
												</p class="text-sm-center m-0">
											</div>
											<div class="col-auto my-2">
												<s:if
												test="%{#session.USER.UserType.name()=='ADMIN' || #session.USER.UserType.name()=='SUBADMIN' || #session.USER_TYPE.name()=='SUPERADMIN'}">
												<s:select name="merchant" class="form-select form-select-solid merchantPayId" id="merchants"
													headerKey="" headerValue="ALL" list="merchantList" listKey="emailId"
													listValue="businessName" autocomplete="off" />
											</s:if>
											<s:else>
												<s:select name="merchant" class="form-select form-select-solid merchantPayId" id="merchants"
													headerKey="" headerValue="ALL" list="merchantList" listKey="emailId"
													listValue="businessName" autocomplete="off" />
											</s:else>
											</div>
											<div class="col-auto my-2 merchant-text">
												<p class="text-center m-0 w-100">
													<b>Payment Type</b>
												</p class="text-sm-center m-0">
											</div>
											<div class="col-auto my-2">
												<button class="btn btn-primary  mt-4 submit_btn" id="captured2"
											onclick="sendPerformanceSms('capturedData');">Generate Captured SMS</button>
											</div>	
											<div class="col-auto my-2">
												<button class="btn btn-primary  mt-4 submit_btn" id="settled3"
											onclick="sendPerformanceSms('merchantData');">Generate Merchant SMS</button>
											</div>	
											<div class="col-auto my-2">
												<button class="btn btn-primary  mt-4 submit_btn" id="settled4"
											onclick="sendPerformanceSms('iPaycapturedData');">Generate iPay SMS</button>
											</div>	
											<div class="col-auto my-2">
												<button class="btn btn-primary  mt-4 submit_btn"
											onclick="sendPerformanceSms('settledData');" id="settledTransBtn">Settled
											Transaction data</button>
											</div>	
											
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			









		</html>