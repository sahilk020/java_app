	<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="/struts-tags" prefix="s"%>
<html dir="ltr" lang="en-US">
<head>
<title>Transaction</title>
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
<script src="../js/loader/main.js"></script>
<script src="../assets/plugins/global/plugins.bundle.js"></script>
<script src="../assets/js/scripts.bundle.js"></script>
<script
	src="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.js"></script>
<script src="../assets/plugins/custom/datatables/datatables.bundle.js"></script>
<script src="../assets/js/widgets.bundle.js"></script>
<script src="../assets/js/custom/widgets.js"></script>
<script src="../assets/js/custom/apps/chat/chat.js"></script>
<script src="../assets/js/custom/utilities/modals/upgrade-plan.js"></script>
<script src="../assets/js/custom/utilities/modals/create-app.js"></script>
<script src="../assets/js/custom/utilities/modals/users-search.js"></script>
<script src="../js/commanValidate.js"></script>
<script type="text/javascript">
$(document).ready(function() {
	
});

</script>

<style>
.adf-control {
	display: inline !important;
	width: 60% !important;
	height: 28px;
	padding: 3px 4px;
	font-size: 14px;
	line-height: 1.42857143;
	color: #555;
	background-color: #fff;
	background-image: none;
	border: 1px solid #ccc;
	border-radius: 4px;
}

.dataTables_wrapper {
	position: relative;
	clear: both;
	*zoom: 1;
	zoom: 1;
	margin-top: -30px;
}

.input-control {
	display: block;
	width: 107%;
	height: 28px;
	padding: 3px 4px;
	font-size: 14px;
	line-height: 1.42857143;
	color: #555;
	background-color: #fff;
	background-image: none;
	border: 1px solid #ccc;
	border-radius: 4px;
	margin-left: -5px;
}

.multiselect {
	width: 170px;
	display: block;
	margin-left: -10px;
}

.selectBox {
	position: relative;
}

#checkboxes {
	display: none;
	border: 1px #dadada solid;
	height: 300px;
	overflow-y: scroll;
	position: Absolute;
	background: #fff;
	z-index: 1;
	margin-left: 3px;
	margin-right: 5px;
}

#checkboxes label {
	width: 66%;
	font-weight: 600;
}

#checkboxes input {
	width: 30%;
}

.selectBox select {
	width: 100%;
}

#checkboxes1 {
	display: none;
	border: 1px #dadada solid;
	height: 300px;
	overflow-y: scroll;
	position: Absolute;
	background: #fff;
	z-index: 1;
	margin-left: 5px;
}

#checkboxes1 label {
	width: 74%;
}

#checkboxes1 input {
	width: 18%;
}

.overSelect {
	position: absolute;
	left: 0;
	right: 0;
	top: 0;
	bottom: 0;
}

table.dataTable thead th {
	padding: 4px 15px !important;
}

#summaryReportDataTable {
	text-align: center;
}

#summaryReportCountDataTable {
	text-align: center;
}

.dataTables_length select option:last-child {
	display: none !important;
}

.boxheadingsmall th {
	text-align: center !important;
}

.multiselect {
	width: 100%;
	margin-left: 0;
}

.selectBox select {
	width: 100%;
}

.input-control select option {
	width: 100%;
}

.submit-btn {
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
	margin-top: 15px;
	margin-bottom: -20px;
}

.odd {
	background-color: #e6e6ff !important;
}

table.dataTable thead .sorting {
	background: none !important;
}

.card-list-toggle {
	cursor: pointer;
	padding: 8px 12px;
	border: 1px solid #ccc;
	position: relative;
	background: #ddd;
}

.card-list-toggle:before {
	position: absolute;
	right: 10px;
	top: 7px;
	content: "\f078";
	font-family: 'FontAwesome';
	font-size: 15px;
}

.card-list-toggle.active:before {
	content: "\f077";
}

.card-list {
	display: none;
}

}
<!--
--



	


-CSS FOR COLLAPSE DROPDOWN DESIGN---->.select2-container--default {
	display: none;
}

.btnActive {
	background: #496cb6 !important;
	color: #fff !important;
}

.newteds .newround {
	border: none;
	padding: 8px 34px;
	background: #d2d2d2;
	color: #6b6b6b;
	margin-top: 10px;
}

.newteds .newround:last-child {
	margin-right: 186px;
}

#dvApprovedAmount {
	font-size: 13px; <!--
	padding: 8px 0 2px 0;
	-->
}

.col-xs-5ths, .col-sm-5ths, .col-md-5ths, .col-lg-5ths {
	position: relative;
	min-height: 1px;
	padding-right: 0px;
	padding-left: 40px;
}

.col-xs-5ths {
	width: 20%;
	float: left;
}

.panel-right h3 {
	font-size: 13px !important;
}

@media ( min-width : 768px) {
	.col-sm-5ths {
		width: 20%;
		float: left;
	}
}

@media ( min-width : 992px) {
	.col-md-5ths {
		width: 24%;
		float: left;
	}
}

@media ( min-width : 1200px) {
	.col-lg-5ths {
		width: 20%;
		float: left;
	}
}

.collapseHead {
	color: black;
	font-weight: 700;
	font-size: 13px;
}

.newDiv {
	width: 98%;
	height: 15px;
	background: #e6e6e6;
	margin-left: 12px;
	border: 1px solid #d9d9d9;
	border-radius: 3px; <!--
	padding: 6px 10px;
	-->
}

.arrowClass {
	float: right;
	margin-right: 15px;
	color: black;
}

.animateArrow {
	color: white;
	-webkit-transform: rotate(180deg);
	-moz-transform: rotate(180deg);
	-o-transform: rotate(180deg);
	-ms-transform: rotate(180deg);
	transform: rotate(180deg);
}

.refundButtonCls {
	background: #496cb6;
	border: none;
	text-align: right !important;
	color: white;
	border-radius: 3px;
	font-size: 12px;
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

.modal-content {
	padding: 10px 20px !important;
}

button#btnRefundConf {
	margin-left: -19px !important;
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
	width: 7%;
}

button.dt-button, div.dt-button, a.dt-button {
	font-size: 14px;
}
/* @media (max-width: 768px) {
		#ui-datepicker-div{
		  position: absolute !important;
		  top: 600px !important;
		  left:60px !important;
		}
		} */
.dt-buttons.btn-group.flex-wrap {
	display: none !important;
}

#kt_datatable_vertical_scroll_filter {
	display: none !important;
}
/* Chrome, Safari, Edge, Opera */
input::-webkit-outer-spin-button, input::-webkit-inner-spin-button {
	-webkit-appearance: none;
	margin: 0;
}

/* Firefox */
input[type=number] {
	-moz-appearance: textfield;
}


#kt_app_footer{
    position: fixed;
    bottom: 0;
}

</style>
<script>
	function getTranscationReport(){
		var reportType = document.getElementById("transcation").value;
		if(!reportType){
            return;
        }
		var urls = new URL(window.location.href);
		console.log("Report Type:"+reportType);
		console.log("URLS: "+urls);
		debugger;
		var domain = urls.origin+"/crm/jsp/"+reportType;
		console.log("domain"+domain);
		debugger;
		document.getElementById("transction").action = domain;
	    const form = document.getElementById('transction');
	    console.log("Before submit: "+form);
		form.submit();

			}

	
	</script>
</head>
<body id="mainBody">

<form id ="transction" name="refundDetails" action="">
		
	</form>
	
	<div class="modal" id="refundAccept" tabindex="-1" role="dialog"
		aria-labelledby="myModalLabel" aria-hidden="true">
		<!-- <div id="loadingInner" display="none">
			<img id="loading-image-inner" src="../image/sand-clock-loader.gif"
				alt="BUSY..." />
		</div> -->
		
	</div>

	<div class="content flex-column" id="kt_content">
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
					<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">Transaction</h1>
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
						<li class="breadcrumb-item text-muted">Transaction Reports</li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item"><span
							class="bullet bg-gray-200 w-5px h-2px"></span></li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item text-dark">Transaction</li>
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
				<form action="" class="form mb-15" method="post"
					id="sale_captured_form">
					<s:hidden name="token" value="%{#session.customToken}" />
					<div class="row my-5">
						<div class="col">
							<div class="card">
								<div class="card-body">
									<!--begin::Input group-->
									<div class="row g-9 mb-8">
										<!--begin::Col-->
										<div class="col-md-4 fv-row">
											<label class="d-flex align-items-center fs-6 fw-bold mb-2">
												<span class=""> Transcation Report .</span>
											</label>

											<select name="currency" id="transcation" data-control="select2" data-hide-search="true" onchange="getTranscationReport()" class="form-select form-select-solid">
												<option value="">Select TXN Report</option>
												<option value="saleTransactionSearch">Sale Captured</option>
												<option value="settledTransactionSearch">Settled Report</option>
												
											
											</select>

										</div>
										

									
									</div>
									<div class="row g-9 mb-8">
										<!--begin::Col-->
										
										
										
										<div
											class="col-md-8 fv-row d-flex justify-content-center align-items-end justify-content-md-end">
											<%-- <button type="submit" id="sale_captured_submit" class="btn w-100 w-md-25 btn-primary">
													<span class="indicator-label">Submit</span>
													<span class="indicator-progress">Please wait...
														<span
															class="spinner-border spinner-border-sm align-middle ms-2"></span></span>
												</button> --%>

										
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</form>
				<div class="row my-5">
					<div class="col">
						<div class="card">
							<div class="card-body">
								<!--begin::Input group-->
								<div class="row g-9 mb-8 justify-content-end">

									
									
								</div>
								
							</div>
						</div>
					</div>
				</div>
			</div>
			<!--end::Container-->
		</div>
		<!--end::Post-->
	</div>

	<s:form name="chargeback" action="chargebackAction">
		<s:hidden name="orderId" id="orderIdc" value="" />
		<s:hidden name="payId" id="payIdc" value="" />

		<s:hidden name="txnId" id="txnIdc" value="" />
		<s:hidden name="token" value="%{#session.customToken}" />
	</s:form>


	<s:form name="refundDetails" action="refundConfirmAction">
		<s:hidden name="orderId" id="orderIdr" value="" />
		<s:hidden name="payId" id="payIdr" value="" />
		<s:hidden name="transactionId" id="txnIdr" value="" />
		<s:hidden name="token" value="%{#session.customToken}" />
	</s:form>

	
</body>
</html>