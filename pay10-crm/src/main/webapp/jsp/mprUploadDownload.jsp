<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<!-- <link href="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.css" rel="stylesheet" type="text/css" />
<link href="../assets/plugins/custom/datatables/datatables.bundle.css" rel="stylesheet" type="text/css" />
<link href="../assets/plugins/global/plugins.bundle.css" rel="stylesheet" type="text/css" />
<link href="../assets/css/style.bundle.css" rel="stylesheet" type="text/css" />
<link href="../assets/css/custom/custom-style.css" rel="stylesheet" type="text/css" /> -->


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


<style>
.nav {
	margin-bottom: 18px;
	margin-left: 0;
	list-style: none;
}

.nav>li>a {
	display: block;
}

.nav-tabs {
	*zoom: 1;
}

.nav-tabs:before, .nav-tabs:after {
	display: table;
	content: "";
}

.nav-tabs:after {
	clear: both;
}

.nav-tabs>li {
	float: left;
}

.nav-tabs>li>a {
	padding-right: 12px;
	padding-left: 12px;
	margin-right: 2px;
	line-height: 14px;
}

.nav-tabs {
	border-bottom: 1px solid #ddd;
}

.nav-tabs>li {
	margin-bottom: -1px;
}

.nav-tabs>li>a {
	padding-top: 8px;
	padding-bottom: 8px;
	line-height: 18px;
	border: 1px solid transparent;
	-webkit-border-radius: 4px 4px 0 0;
	-moz-border-radius: 4px 4px 0 0;
	border-radius: 4px 4px 0 0;
}

.nav-tabs>li>a:hover {
	border-color: #eeeeee #eeeeee #dddddd;
}

.nav-tabs>.active>a, .nav-tabs>.active>a:hover {
	color: #555555;
	cursor: default;
	background-color: #ffffff;
	border: 1px solid #ddd;
	border-bottom-color: transparent;
}

li {
	line-height: 18px;
}

.tab-content.active {
	display: block;
}

.tab-content.hide {
	display: none;
}

.nav-tabs>li>a:hover {
	border-top: 0px solid transparent;
}

.uploadButton {
	border: none;
	background-color: #496cb6;
	border-radius: 5px;
	width: 25%;
	font-size: 18px;
	color: white;
}

.heading {
	text-align: center;
	color: black;
	font-weight: bold;
	font-size: 22px;
}

.txtnew label {
	/* display: inline-block; */
	/* max-width: 100%; */
	display: block;
	text-align: left;
}

.form-control {
	margin-left: 0 !important;
	width: 100% !important;
}

.file-group .file-input__input {
	top: 31px;
	width: 100%;
	height: 20.1px;
	opacity: 0;
	overflow: hidden;
	position: relative;
	z-index: 0;
	left: 26px;
}

.nav-tabs .nav-link {
	background-color: #202F4Bbf !important;
}

.nav-tabs .nav-link.active {
	background-color: #202F4B !important;
	color: #262424;
}
</style>

<title>Acquirer MPR Report</title>

<!-- <link rel="stylesheet" href="../css/loader.css">
	
	<script src="../js/jquery.js"></script>
	<script src="../js/jquery.dataTables.js"></script>
	<script src="../js/jquery-ui.js"></script>
	<script type="text/javascript" src="../js/moment.js"></script>
	<script type="text/javascript" src="../js/daterangepicker.js"></script>
	<script src="../js/jquery.popupoverlay.js"></script>
	<script type="text/javascript" src="../js/dataTables.buttons.js"></script>
	<script type="text/javascript" src="../js/pdfmake.js"></script> -->

<script>
	$(document).ready(
			function() {

				$('.nav-tabs > li > a').click(
						function(event) {
							event.preventDefault();//stop browser to take action for clicked anchor

							//get displaying tab content jQuery selector
							var active_tab_selector = $(
									'.nav-tabs > li.active > a').attr('href');

							//find actived navigation and remove 'active' css
							var actived_nav = $('.nav-tabs > li.active');
							actived_nav.removeClass('active');

							//add 'active' css into clicked navigation
							$(this).parents('li').addClass('active');

							//hide displaying tab content
							$(active_tab_selector).removeClass('active');
							$(active_tab_selector).addClass('hide');

							//show target tab content
							var target_tab_selector = $(this).attr('href');
							$(target_tab_selector).removeClass('hide');
							$(target_tab_selector).addClass('active');
						});
			});
</script>


</head>
<body id="mainBody">
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
					<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">Reco
						File Upload</h1>
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
						<li class="breadcrumb-item text-muted">Accounts & Finance</li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item"><span
							class="bullet bg-gray-200 w-5px h-2px"></span></li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item text-dark">Reco File Upload</li>
						<!--end::Item-->
					</ul>
					<!--end::Breadcrumb-->
				</div>
				<!--end::Page title-->

			</div>
			<!--end::Container-->
		</div>

		<!-- <h2 class="pageHeading">Acquirer MPR Report</h2>
	<br>
	<br> -->


		<div id="kt_content_container" class="container-xxl">
			<div class="row my-5">
				<div class="col">
					<div class="card">
						<div class="card-body">
							<!--begin::Input group-->
							<div class="row g-9 mb-8">
								<!--begin::Col-->
								<div class="col">

									<nav>
									<div class="nav nav-tabs" id="nav-tab" role="tablist">
										<a class="nav-item nav-link" id="nav-home-tab"
											data-toggle="tab" href="#nav-home" role="tab"
											aria-controls="nav-home" aria-selected="true"
											onclick="showHide('1')">MPR Upload</a> <a
											class="nav-item nav-link" id="nav-profile-tab"
											data-toggle="tab" href="#nav-profile" role="tab"
											aria-controls="nav-profile" aria-selected="false"
											onclick="showHide('2')">MPR Download</a>
									</div>
									</nav>
									<!-- first tab -->
									<div class="tab-content" id="nav-tabContent">

										<div class="tab-pane" id="nav-home" role="tabpanel"
											aria-labelledby="nav-home-tab">
											<form id="mprForm" name="mprForm" method="post"
												action="mprUploadAction" enctype="multipart/form-data">
											<div class="row">
												<div class="col-md-3 fv-row">
													<label
														class="d-flex align-items-center fs-6 fw-semibold mb-2">
														<span class="select2-selection__rendered">Acquirer:</span>
													</label>
													<!--end::Label-->
													<s:select headerKey="" headerValue="Select Acquirer"
											            data-control="select2"
														class="form-select form-select-solid"
														data-hide-search="true"
														list="@com.pay10.commons.util.AcquirerTypeUI@values()"
														listKey="name" listValue="code" name="acquirerName"
														id="acquirerName2" autocomplete="off" value="" />
													<span id="selectAcq" style="color: red; display: none;">Please
														Select Acquirer</span>
												</div>
												<%-- <div class="col-md-3 fv-row">
													<label
														class="d-flex align-items-center fs-6 fw-semibold mb-2">
														<span class="">Payment Type:</span>
													</label> <select data-control="select2" data-placeholder="Currency"
														class="form-select form-select-solid"
														data-hide-search="true" name="paymentType"
														id="paymentType">
														<option>Select Payment Type</option>
														<option value="Cards">Cards</option>
														<option value="UPI">UPI</option>
														<option value="NB">Netbanking</option>
													</select> <span id="selectPay" style="color: red; display: none;">Please
														Select Payment Type</span>

												</div> --%>
												<div class="col-md-3 fv-row">
													<label
														class="d-flex align-items-center fs-6 fw-semibold mb-2">
														<span class="">Date:</span>
													</label>
													<!--end::Label-->
													<div class="position-relative d-flex align-items-center">
														<!--begin::Icon-->
														<!--begin::Svg Icon | path: icons/duotune/general/gen014.svg-->
														<span class="svg-icon svg-icon-2 position-absolute mx-4">
															<svg width="24" height="24" viewBox="0 0 24 24"
																fill="none" xmlns="http://www.w3.org/2000/svg"> <path
																opacity="0.3"
																d="M21 22H3C2.4 22 2 21.6 2 21V5C2 4.4 2.4 4 3 4H21C21.6 4 22 4.4 22 5V21C22 21.6 21.6 22 21 22Z"
																fill="currentColor" /> <path
																d="M6 6C5.4 6 5 5.6 5 5V3C5 2.4 5.4 2 6 2C6.6 2 7 2.4 7 3V5C7 5.6 6.6 6 6 6ZM11 5V3C11 2.4 10.6 2 10 2C9.4 2 9 2.4 9 3V5C9 5.6 9.4 6 10 6C10.6 6 11 5.6 11 5ZM15 5V3C15 2.4 14.6 2 14 2C13.4 2 13 2.4 13 3V5C13 5.6 13.4 6 14 6C14.6 6 15 5.6 15 5ZM19 5V3C19 2.4 18.6 2 18 2C17.4 2 17 2.4 17 3V5C17 5.6 17.4 6 18 6C18.6 6 19 5.6 19 5Z"
																fill="currentColor" /> <path
																d="M8.8 13.1C9.2 13.1 9.5 13 9.7 12.8C9.9 12.6 10.1 12.3 10.1 11.9C10.1 11.6 10 11.3 9.8 11.1C9.6 10.9 9.3 10.8 9 10.8C8.8 10.8 8.59999 10.8 8.39999 10.9C8.19999 11 8.1 11.1 8 11.2C7.9 11.3 7.8 11.4 7.7 11.6C7.6 11.8 7.5 11.9 7.5 12.1C7.5 12.2 7.4 12.2 7.3 12.3C7.2 12.4 7.09999 12.4 6.89999 12.4C6.69999 12.4 6.6 12.3 6.5 12.2C6.4 12.1 6.3 11.9 6.3 11.7C6.3 11.5 6.4 11.3 6.5 11.1C6.6 10.9 6.8 10.7 7 10.5C7.2 10.3 7.49999 10.1 7.89999 10C8.29999 9.90003 8.60001 9.80003 9.10001 9.80003C9.50001 9.80003 9.80001 9.90003 10.1 10C10.4 10.1 10.7 10.3 10.9 10.4C11.1 10.5 11.3 10.8 11.4 11.1C11.5 11.4 11.6 11.6 11.6 11.9C11.6 12.3 11.5 12.6 11.3 12.9C11.1 13.2 10.9 13.5 10.6 13.7C10.9 13.9 11.2 14.1 11.4 14.3C11.6 14.5 11.8 14.7 11.9 15C12 15.3 12.1 15.5 12.1 15.8C12.1 16.2 12 16.5 11.9 16.8C11.8 17.1 11.5 17.4 11.3 17.7C11.1 18 10.7 18.2 10.3 18.3C9.9 18.4 9.5 18.5 9 18.5C8.5 18.5 8.1 18.4 7.7 18.2C7.3 18 7 17.8 6.8 17.6C6.6 17.4 6.4 17.1 6.3 16.8C6.2 16.5 6.10001 16.3 6.10001 16.1C6.10001 15.9 6.2 15.7 6.3 15.6C6.4 15.5 6.6 15.4 6.8 15.4C6.9 15.4 7.00001 15.4 7.10001 15.5C7.20001 15.6 7.3 15.6 7.3 15.7C7.5 16.2 7.7 16.6 8 16.9C8.3 17.2 8.6 17.3 9 17.3C9.2 17.3 9.5 17.2 9.7 17.1C9.9 17 10.1 16.8 10.3 16.6C10.5 16.4 10.5 16.1 10.5 15.8C10.5 15.3 10.4 15 10.1 14.7C9.80001 14.4 9.50001 14.3 9.10001 14.3C9.00001 14.3 8.9 14.3 8.7 14.3C8.5 14.3 8.39999 14.3 8.39999 14.3C8.19999 14.3 7.99999 14.2 7.89999 14.1C7.79999 14 7.7 13.8 7.7 13.7C7.7 13.5 7.79999 13.4 7.89999 13.2C7.99999 13 8.2 13 8.5 13H8.8V13.1ZM15.3 17.5V12.2C14.3 13 13.6 13.3 13.3 13.3C13.1 13.3 13 13.2 12.9 13.1C12.8 13 12.7 12.8 12.7 12.6C12.7 12.4 12.8 12.3 12.9 12.2C13 12.1 13.2 12 13.6 11.8C14.1 11.6 14.5 11.3 14.7 11.1C14.9 10.9 15.2 10.6 15.5 10.3C15.8 10 15.9 9.80003 15.9 9.70003C15.9 9.60003 16.1 9.60004 16.3 9.60004C16.5 9.60004 16.7 9.70003 16.8 9.80003C16.9 9.90003 17 10.2 17 10.5V17.2C17 18 16.7 18.4 16.2 18.4C16 18.4 15.8 18.3 15.6 18.2C15.4 18.1 15.3 17.8 15.3 17.5Z"
																fill="currentColor" /> </svg>
														</span>
														<!--end::Svg Icon-->
														<!--end::Icon-->
														<!--begin::Datepicker-->
														<input class="form-control form-control-solid ps-12"
															placeholder="Select a date" name="mprDate"
															id="kt_datepicker_1" />
														<!--end::Datepicker-->
													</div>
												</div>
												<div class="col-md-3  file-group">
													<div class="file-input"
														style="display: block; overflow-y: hidden; overflow-x: hidden;">
														<!-- <input type="file" name="file_input" id="file-input" class="file-input__input" onchange="getfilename(this)" accept=".csv, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel" /> -->
														<input name="file" id="my_file" type="file"
															accept=".xlsx, .xls, .csv" onchange="getfilename(this)"
															class="file-input__input">

														<!-- <span id="fileCSVErr"></span> -->

														<label class="file-input__label" for="file-input">
															<img src="../assets/media/images/folder-svg.svg" alt="">
															<p class="m-0" id="filename"></p> <span>Browse</span>
														</label> <span id="selectFile" style="color: red; display: none;">Please
															Select File to Upload</span>
													</div>
												</div>

												<div class="row">
													<div class="col-md-12 fv-row mt-7 text-end">
														<button type="button" id="submit1"
															class="btn w-100 w-md-25 btn-primary"
															onClick='checkValidation(event)'>
															<span class="indicator-label">Upload</span> <span
																class="indicator-progress">Please wait... <span
																class="spinner-border spinner-border-sm align-middle ms-2"></span></span>
														</button>



													</div>
												</div>

											</div>
											</form>
										</div>
										<!-- second tab -->
										<div class="tab-pane" id="nav-profile" role="tabpanel"
											aria-labelledby="nav-profile-tab">
											<!----------------------------SECOND TAB CONTENT------------------------->
											<form id="mprDownload" name="mprDownload" method="post"
												action="mprDownloadAction">

												<div class="row">
													<div class="col-md-3 fv-row">
														<label
															class="d-flex align-items-center fs-6 fw-semibold mb-2">
															<span class="">Acquirer:</span>
														</label>
														<!--end::Label-->

														<s:select headerKey="" headerValue="Select Acquirer"
															data-control="select2" data-placeholder="Select Acquirer"
															class="acquirerClass form-select form-select-solid"
															data-hide-search="true"
															list="@com.pay10.commons.util.AcquirerTypeUI@values()"
															listKey="name" listValue="code" name="acquirerName"
															id="acquirerName" autocomplete="off" value="" />
														<span id="downloadAcq" style="color: red; display: none;">Please
															Select Acquirer</span>

													</div>
													<div class="col-md-3 fv-row">
														<label
															class="d-flex align-items-center fs-6 fw-semibold mb-2">
															<span class="">Payment Type:</span>
														</label> <select data-control="select2"
															data-placeholder="Currency"
															class="form-select form-select-solid paymentClass"
															data-hide-search="true" name="paymentType"
															id="paymentType">
															<option>Select Payment Type</option>
															<option value="Cards">Cards</option>
															<option value="UPI">UPI</option>
															<option value="NB">Netbanking</option>
														</select> <span id="downloadPay" style="color: red; display: none;">Please
															Select Payment Type</span>

													</div>
													<div class="col-md-3 fv-row">
														<label
															class="d-flex align-items-center fs-6 fw-semibold mb-2">
															<span class="">Date:</span>
														</label>
														<!--end::Label-->
														<div class="position-relative d-flex align-items-center">
															<!--begin::Icon-->
															<!--begin::Svg Icon | path: icons/duotune/general/gen014.svg-->
															<span class="svg-icon svg-icon-2 position-absolute mx-4">
																<svg width="24" height="24" viewBox="0 0 24 24"
																	fill="none" xmlns="http://www.w3.org/2000/svg"> <path
																	opacity="0.3"
																	d="M21 22H3C2.4 22 2 21.6 2 21V5C2 4.4 2.4 4 3 4H21C21.6 4 22 4.4 22 5V21C22 21.6 21.6 22 21 22Z"
																	fill="currentColor" /> <path
																	d="M6 6C5.4 6 5 5.6 5 5V3C5 2.4 5.4 2 6 2C6.6 2 7 2.4 7 3V5C7 5.6 6.6 6 6 6ZM11 5V3C11 2.4 10.6 2 10 2C9.4 2 9 2.4 9 3V5C9 5.6 9.4 6 10 6C10.6 6 11 5.6 11 5ZM15 5V3C15 2.4 14.6 2 14 2C13.4 2 13 2.4 13 3V5C13 5.6 13.4 6 14 6C14.6 6 15 5.6 15 5ZM19 5V3C19 2.4 18.6 2 18 2C17.4 2 17 2.4 17 3V5C17 5.6 17.4 6 18 6C18.6 6 19 5.6 19 5Z"
																	fill="currentColor" /> <path
																	d="M8.8 13.1C9.2 13.1 9.5 13 9.7 12.8C9.9 12.6 10.1 12.3 10.1 11.9C10.1 11.6 10 11.3 9.8 11.1C9.6 10.9 9.3 10.8 9 10.8C8.8 10.8 8.59999 10.8 8.39999 10.9C8.19999 11 8.1 11.1 8 11.2C7.9 11.3 7.8 11.4 7.7 11.6C7.6 11.8 7.5 11.9 7.5 12.1C7.5 12.2 7.4 12.2 7.3 12.3C7.2 12.4 7.09999 12.4 6.89999 12.4C6.69999 12.4 6.6 12.3 6.5 12.2C6.4 12.1 6.3 11.9 6.3 11.7C6.3 11.5 6.4 11.3 6.5 11.1C6.6 10.9 6.8 10.7 7 10.5C7.2 10.3 7.49999 10.1 7.89999 10C8.29999 9.90003 8.60001 9.80003 9.10001 9.80003C9.50001 9.80003 9.80001 9.90003 10.1 10C10.4 10.1 10.7 10.3 10.9 10.4C11.1 10.5 11.3 10.8 11.4 11.1C11.5 11.4 11.6 11.6 11.6 11.9C11.6 12.3 11.5 12.6 11.3 12.9C11.1 13.2 10.9 13.5 10.6 13.7C10.9 13.9 11.2 14.1 11.4 14.3C11.6 14.5 11.8 14.7 11.9 15C12 15.3 12.1 15.5 12.1 15.8C12.1 16.2 12 16.5 11.9 16.8C11.8 17.1 11.5 17.4 11.3 17.7C11.1 18 10.7 18.2 10.3 18.3C9.9 18.4 9.5 18.5 9 18.5C8.5 18.5 8.1 18.4 7.7 18.2C7.3 18 7 17.8 6.8 17.6C6.6 17.4 6.4 17.1 6.3 16.8C6.2 16.5 6.10001 16.3 6.10001 16.1C6.10001 15.9 6.2 15.7 6.3 15.6C6.4 15.5 6.6 15.4 6.8 15.4C6.9 15.4 7.00001 15.4 7.10001 15.5C7.20001 15.6 7.3 15.6 7.3 15.7C7.5 16.2 7.7 16.6 8 16.9C8.3 17.2 8.6 17.3 9 17.3C9.2 17.3 9.5 17.2 9.7 17.1C9.9 17 10.1 16.8 10.3 16.6C10.5 16.4 10.5 16.1 10.5 15.8C10.5 15.3 10.4 15 10.1 14.7C9.80001 14.4 9.50001 14.3 9.10001 14.3C9.00001 14.3 8.9 14.3 8.7 14.3C8.5 14.3 8.39999 14.3 8.39999 14.3C8.19999 14.3 7.99999 14.2 7.89999 14.1C7.79999 14 7.7 13.8 7.7 13.7C7.7 13.5 7.79999 13.4 7.89999 13.2C7.99999 13 8.2 13 8.5 13H8.8V13.1ZM15.3 17.5V12.2C14.3 13 13.6 13.3 13.3 13.3C13.1 13.3 13 13.2 12.9 13.1C12.8 13 12.7 12.8 12.7 12.6C12.7 12.4 12.8 12.3 12.9 12.2C13 12.1 13.2 12 13.6 11.8C14.1 11.6 14.5 11.3 14.7 11.1C14.9 10.9 15.2 10.6 15.5 10.3C15.8 10 15.9 9.80003 15.9 9.70003C15.9 9.60003 16.1 9.60004 16.3 9.60004C16.5 9.60004 16.7 9.70003 16.8 9.80003C16.9 9.90003 17 10.2 17 10.5V17.2C17 18 16.7 18.4 16.2 18.4C16 18.4 15.8 18.3 15.6 18.2C15.4 18.1 15.3 17.8 15.3 17.5Z"
																	fill="currentColor" /> </svg>
															</span>
															<!--end::Svg Icon-->
															<!--end::Icon-->
															<!--begin::Datepicker-->
															<input class="form-control form-control-solid ps-12"
																placeholder="Select a date" name="mprDate"
																id="kt_datepicker_2" />
															<!--end::Datepicker-->
														</div>
													</div>
													<div class="col-md-3 fv-row mt-7">
														<button type="button" id="downloadReport"
															class="btn w-100 w-md-100 btn-primary"
															onClick='checkValidationDownload(event)'>
															<span class="indicator-label">Download</span> <span
																class="indicator-progress">Please wait... <span
																class="spinner-border spinner-border-sm align-middle ms-2"></span></span>
														</button>

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
			</div>
		</div>



	</div>





	<script type="text/javascript">
		function showHide(value) {

			var tab1 = document.getElementById("nav-home-tab");
			var tab2 = document.getElementById("nav-profile-tab");
			var element1 = document.getElementById("nav-home");
			var element2 = document.getElementById("nav-profile");
			if (value == "1") {
				element1.classList.add("active");
				tab1.classList.add("active");

				element2.classList.remove("active");
				tab2.classList.remove("active");

			}
			if (value == "2") {
				element2.classList.add("active");
				tab2.classList.add("active");

				element1.classList.remove("active");
				tab1.classList.remove("active");

			}
		}
		$(document).ready(
				function() {

					$(function() {
						$("#mprUploadDate").datepicker({
							prevText : "click for previous months",
							nextText : "click for next months",
							showOtherMonths : true,
							dateFormat : 'dd-mm-yy',
							selectOtherMonths : false,
							maxDate : new Date()
						});

					});
					$(function() {
						var today = new Date();
						$('#mprUploadDate').val(
								$.datepicker.formatDate('dd-mm-yy', today));
					});
				});
	</script>

	<script type="text/javascript">
		$(document).ready(
				function() {
					var tab1 = document.getElementById("nav-home-tab");
					var element1 = document.getElementById("nav-home");
					element1.classList.add("active");
					tab1.classList.add("active");
					$(function() {
						$("#mprDownlaoadDate").datepicker({
							prevText : "click for previous months",
							nextText : "click for next months",
							showOtherMonths : true,
							dateFormat : 'dd-mm-yy',
							selectOtherMonths : false,
							maxDate : new Date()
						});
					});
					$(function() {
						var today = new Date();
						$('#mprDownlaoadDate').val(
								$.datepicker.formatDate('dd-mm-yy', today));

					});
				});
	</script>

	<script>
		var _validFileExtensions = [ ".xlsx", ".xls", ".csv" ];

		function getfilename(oInput) {
			if (oInput.type == "file") {
				var sFileName = oInput.value;
				if (sFileName.length > 0) {
					var blnValid = false;
					for (var j = 0; j < _validFileExtensions.length; j++) {
						var sCurExtension = _validFileExtensions[j];
						if (sFileName.substr(
								sFileName.length - sCurExtension.length,
								sCurExtension.length).toLowerCase() == sCurExtension
								.toLowerCase()) {
							blnValid = true;
							//alert("file uploaded successfully")
							document.getElementById('filename').innerHTML = oInput.files[0].name;
							document.getElementById('filename').style.fontSize = '13px';
							break;
						}
					}

					if (!blnValid) {
						alert("Sorry, this is invalid, allowed extensions are: "
								+ _validFileExtensions.join(", ")
								+ " "
								+ "only");
						oInput.value = "";
						return false;
					}
				}
			}
			return true;
		}
	</script>
	<!--------For Frontend Validation--------->
	<script>
		function checkValidation(event) {
	
			var acquirerVal = document.getElementById("acquirerName2").value;
// 			var paymentVal = document.getElementById("paymentType").value;
			var file = document.getElementById("my_file").value;
			document.getElementById("selectAcq").style.display = "none";
			//document.getElementById("selectPay").style.display = "none";
			if (acquirerVal == "Select Acquirer" || acquirerVal == "") {
				document.getElementById("selectAcq").style.display = "block";
				event.preventDefault();
			}
// 			else if (paymentVal == "Select Payment Type" || paymentVal == "") {
// 				document.getElementById("selectPay").style.display = "block";
// 				event.preventDefault();
// 			}
			else if (file == "" || file == null) {
				document.getElementById("selectFile").style.display = "block";
				event.preventDefault();
			}

			else {
				document.getElementById("selectAcq").style.display = "none";
// 				document.getElementById("selectPay").style.display = "none";
				document.getElementById("mprForm").submit();

			}
		}
	</script>

	<script>
		function checkValidationDownload(event) {
			var acquirerDwn = $(".acquirerClass option:selected").text();
			var paymentDwn = $(".paymentClass option:selected").text();

			console.log(acquirerDwn);
			console.log(paymentDwn);

			document.getElementById("downloadAcq").style.display = "none";
			document.getElementById("downloadPay").style.display = "none";

			if (acquirerDwn == "Select Acquirer" || acquirerDwn == "") {
				document.getElementById("downloadAcq").style.display = "block";
				event.preventDefault();
			} else if (paymentDwn == "Select Payment Type" || paymentDwn == "") {
				document.getElementById("downloadPay").style.display = "block";
				event.preventDefault();
			} else {
				document.getElementById("downloadAcq").style.display = "none";
				document.getElementById("downloadPay").style.display = "none";
				document.getElementById("mprDownload").submit();
			}
		}

		$(document)
				.ready(
						function() {
							if (window.location.href
									.includes("displayUploadUI")) {
								var menuAccess = document
										.getElementById("menuAccessByROLE").value;
								var accessMap = JSON.parse(menuAccess);
								var access = accessMap["displayUploadUI"];
								if (access.includes("Upload")) {
									document.getElementById("MprUpload").classList
											.add("active");
									document.getElementById("MprUpload").classList
											.remove('hide');
									document.getElementById("uploadAction").style.display = "block";
								}
								if (access.includes("Download")) {
									if (!access.includes("Upload")) {
										document.getElementById("MprDownload").classList
												.add("active");
										document.getElementById("MprDownload").classList
												.remove('hide');
										document
												.getElementById("downloadAction").classList
												.add("active");
									}
									document.getElementById("downloadAction").style.display = "block";
								}
							}
						});
	</script>
	<!--begin::Global Javascript Bundle(used by all pages)-->

	<!--end::Global Javascript Bundle-->
	<!--begin::Vendors Javascript(used by this page)-->
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






	<!--end::Custom Javascript-->
	<!--end::Javascript-->
	<script>
		$("#kt_datepicker_1").flatpickr({
			maxDate : new Date(),
			dateFormat : "d-m-Y",
			defaultDate : "today"
		});
		$("#kt_datepicker_2").flatpickr({
			maxDate : new Date(),
			dateFormat : "d-m-Y",
			defaultDate : "today"
		});
		// $("#kt_datatable_vertical_scroll").DataTable({
		// 	scrollY: true,
		// 	scrollX: true,

		// });
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
			var allInputCheckBox = document
					.getElementsByClassName("myCheckBox");

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
	<script>
		$(document).ready(function() {
			$("#acquirerName").select2();
			$("#acquirerName2").select2();
		});
	</script>
</body>
</html>