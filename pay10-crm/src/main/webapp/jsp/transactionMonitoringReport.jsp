<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
	<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
	<%@ taglib uri="/struts-tags" prefix="s" %>
		<html dir="ltr" lang="en-US">

		<head>
			<title>Transaction Monitoring Summary</title>
			

				<link href="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.css" rel="stylesheet"
				type="text/css" />
			<link href="../assets/plugins/custom/datatables/datatables.bundle.css" rel="stylesheet" type="text/css" />
			<link href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css" rel="stylesheet" />
			<link href="../assets/plugins/global/plugins.bundle.css" rel="stylesheet" type="text/css" />

			<link href="../assets/css/style.bundle.css" rel="stylesheet" type="text/css" />
			<link href="../assets/css/custom/custom-style.css" rel="stylesheet" type="text/css" />
			<script src="../assets/js/scripts.bundle.js"></script>

			<script src="../js/commanValidate.js"></script>

			<script src="../assets/plugins/global/plugins.bundle.js"></script>
			<script src="../assets/js/scripts.bundle.js"></script>
			<script src="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.js"></script>
			<script src="../assets/plugins/custom/datatables/datatables.bundle.js"></script>
			<script src="../assets/js/widgets.bundle.js"></script>
			<script src="../assets/js/custom/widgets.js"></script>
			<script src="../assets/js/custom/apps/chat/chat.js"></script>
			<script src="../assets/js/custom/utilities/modals/upgrade-plan.js"></script>
			<script src="../assets/js/custom/utilities/modals/create-app.js"></script>
			<script src="../assets/js/custom/utilities/modals/users-search.js"></script>

			<script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js"></script>

			<script type="text/javascript" src="../js/canvasChart/canvas.min.js"></script> 

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
						height: 180px;
						overflow-y: scroll;
						position: Absolute;
						background: #fff;
						z-index: 1;
						padding: 10px;
					}

					#checkboxes label {
						width: 74%;
					}

					#checkboxes input {
						width: 18%;
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

					.panel-right h3 {
						font-size: 13px !important;
					}

					@media (min-width : 768px) {
						.col-sm-5ths {
							width: 20%;
							float: left;
						}
					}

					@media (min-width : 992px) {
						.col-md-5ths {
							width: 24%;
							float: left;
						}
					}

					@media (min-width : 1200px) {
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
						border-radius: 3px;
						
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
						width: 7%;
					}

					.nav-tabs .nav-item .nav-link.active {
						background-color: rgb(68, 97, 138) !important;
					}

					.nav-tabs .nav-item .nav-link {
						background-color: #202f4bbf !important;
						color: #fff;
					}

					.nav-tabs .nav-item .nav-link.active {
                        background-color: #202f4b !important;
                        color: #fff;
					}
					button.dt-button,
					div.dt-button,
					a.dt-button {
						font-size: 14px;
					}
					.chartContainerMerchant{
						border: 0px;
															background: white;
															/* width: 105px; */
															z-index: 9;
															margin: -11px 15px;


														}
.chartContainer1{
margin-top: -11px;
    border: 0px;
    background: #fff;
    width: 105px !important;
    z-index: 9;
    position: relative;
    margin-left: 93%;
}

.canvasjs-chart-toolbar button {
  width: 100px !important;
  border: 1px solid black !important;
}

.dt-buttons {
	display: none;
}
				</style>


<script type="text/javascript">
	$(document).ready(function () {

		/* $(function () {
			$("#fromDate1").flatpickr({
				maxDate: new Date(),
				dateFormat: "Y-m-d",
				defaultDate: "today",
				defaultDate: "today",
			});
			$("#toDate1").flatpickr({
				maxDate: new Date(),
				dateFormat: "Y-m-d",
				defaultDate: "today",
				maxDate: new Date()
			});

		});

		$(function () {
			$("#fromDate2").flatpickr({
				maxDate: new Date(),
				dateFormat: "Y-m-d",
				defaultDate: "today",
				defaultDate: "today",
			});
			$("#toDate2").flatpickr({
				maxDate: new Date(),
				dateFormat: "Y-m-d",
				defaultDate: "today",
				maxDate: new Date()
			});

		}); */

		$(function () {
			$('#dataTable1').DataTable();
			$('#dataTable2').DataTable();
			$('#dataTable3').DataTable();
		});

        $("#fromDate1").val(moment().format('YYYY-MM-DDT00:00:00'));
        $("#toDate1").val(moment().format('YYYY-MM-DDThh:mm:00'));
        $("#fromDate2").val(moment().format('YYYY-MM-DDT00:00:00'));
        $("#toDate2").val(moment().format('YYYY-MM-DDThh:mm:00'));
        $("#fromDate3").val(moment().format('YYYY-MM-DDT00:00:00'));
        $("#toDate3").val(moment().format('YYYY-MM-DDThh:mm:00'));

		let dateInput1 = document.getElementById("fromDate1");
		dateInput1.max = new Date().toISOString().slice(0,new Date().toISOString().lastIndexOf(":"));
		let todateInput1 = document.getElementById("toDate1");
		todateInput1.max = new Date().toISOString().slice(0,new Date().toISOString().lastIndexOf(":"));

		let dateInput2 = document.getElementById("fromDate2");
		dateInput2.max = new Date().toISOString().slice(0,new Date().toISOString().lastIndexOf(":"));
		let todateInput2 = document.getElementById("toDate2");
		todateInput2.max = new Date().toISOString().slice(0,new Date().toISOString().lastIndexOf(":"));


		let dateInput3 = document.getElementById("fromDate3");
		dateInput3.max = new Date().toISOString().slice(0,new Date().toISOString().lastIndexOf(":"));
		let todateInput3 = document.getElementById("toDate3");
		todateInput3.max = new Date().toISOString().slice(0,new Date().toISOString().lastIndexOf(":"));

	});
		</script>


		</head>

		<body id="mainBody" class="content flex-column" >

			<div class="post d-flex flex-column-fluid" id="kt_post">
				<!--begin::Toolbar-->
				<div class="toolbar" id="kt_toolbar">
					<!--begin::Container-->
					<div id="kt_toolbar_container" class="container-fluid d-flex flex-stack">
						<!--begin::Page title-->
						<div data-kt-swapper="true" data-kt-swapper-mode="prepend"
							data-kt-swapper-parent="{default: '#kt_content_container', 'lg': '#kt_toolbar_container'}"
							class="page-title d-flex align-items-center me-3 flex-wrap mb-5 mb-lg-0 lh-1">
							<!--begin::Title-->
							<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">Transaction
								Monitoring Summary</h1>
							<!--end::Title-->
							<!--begin::Separator-->
							<span class="h-20px border-gray-200 border-start mx-4"></span>
							<!--end::Separator-->
							<!--begin::Breadcrumb-->
							<ul class="breadcrumb breadcrumb-separatorless fw-semibold fs-7 my-1">
								<!--begin::Item-->
								<li class="breadcrumb-item text-muted"><a href="home"
										class="text-muted text-hover-primary">Dashboard</a></li>
								<!--end::Item-->
								<!--begin::Item-->
								<li class="breadcrumb-item"><span class="bullet bg-gray-200 w-5px h-2px"></span></li>
								<!--end::Item-->
								<!--begin::Item-->
								<li class="breadcrumb-item text-muted">Analytics</li>
								<!--end::Item-->
								<!--begin::Item-->
								<li class="breadcrumb-item"><span class="bullet bg-gray-200 w-5px h-2px"></span></li>
								<!--end::Item-->
								<!--begin::Item-->
								<li class="breadcrumb-item text-dark">Transaction Monitoring
									Summary</li>
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



				<div class="content d-flex flex-column flex-column-fluid" id="kt_content">

					<div class="post d-flex flex-column-fluid" id="kt_post">
						<div id="kt_content_container" class="container-xxl">
							<div class="row my-5">
								<div class="col">
									<div class="card">
										<div class="card-body ">

											<div class="row">

												<ul class="nav nav-tabs">

													<li class="nav-item ml-2"><a class="nav-link active"
															data-toggle="pill" href="#acquirertxnsum" role="tab"
															aria-selected="true">Acquirer Wise Txn Summary</a></li>

													<li class="nav-item ml-2"><a class="nav-link" data-toggle="pill"
															href="#acquirermertxnsum" role="tab"
															aria-selected="false">Acquirer Wise Merchant Txn Summary</a>
													</li>

													<li class="nav-item ml-2"><a class="nav-link" data-toggle="pill"
															href="#totalcountamount" role="tab"
															aria-selected="false">Total Count & Amount</a></li>

												</ul>
											</div>


											<div class="tab-content mt-3">


												<!-- First div start here -->
												<div class="tab-pane fade show active" id="acquirertxnsum">
													<div class="row">
														<div class="col-md-3 fv-row">
															<label
																class="d-flex align-items-center fs-6 fw-semibold mb-2">
																<span class="">Date From:</span>
															</label>
															<!--end::Label-->
															<div class="position-relative d-flex align-items-center">
																<!--begin::Icon-->
																<!--begin::Svg Icon | path: icons/duotune/general/gen014.svg-->
																<span
																	class="svg-icon svg-icon-2 position-absolute mx-4">
																	<svg width="24" height="24" viewBox="0 0 24 24"
																		fill="none" xmlns="http://www.w3.org/2000/svg">
																		<path opacity="0.3"
																			d="M21 22H3C2.4 22 2 21.6 2 21V5C2 4.4 2.4 4 3 4H21C21.6 4 22 4.4 22 5V21C22 21.6 21.6 22 21 22Z"
																			fill="currentColor" />
																		<path
																			d="M6 6C5.4 6 5 5.6 5 5V3C5 2.4 5.4 2 6 2C6.6 2 7 2.4 7 3V5C7 5.6 6.6 6 6 6ZM11 5V3C11 2.4 10.6 2 10 2C9.4 2 9 2.4 9 3V5C9 5.6 9.4 6 10 6C10.6 6 11 5.6 11 5ZM15 5V3C15 2.4 14.6 2 14 2C13.4 2 13 2.4 13 3V5C13 5.6 13.4 6 14 6C14.6 6 15 5.6 15 5ZM19 5V3C19 2.4 18.6 2 18 2C17.4 2 17 2.4 17 3V5C17 5.6 17.4 6 18 6C18.6 6 19 5.6 19 5Z"
																			fill="currentColor" />
																		<path
																			d="M8.8 13.1C9.2 13.1 9.5 13 9.7 12.8C9.9 12.6 10.1 12.3 10.1 11.9C10.1 11.6 10 11.3 9.8 11.1C9.6 10.9 9.3 10.8 9 10.8C8.8 10.8 8.59999 10.8 8.39999 10.9C8.19999 11 8.1 11.1 8 11.2C7.9 11.3 7.8 11.4 7.7 11.6C7.6 11.8 7.5 11.9 7.5 12.1C7.5 12.2 7.4 12.2 7.3 12.3C7.2 12.4 7.09999 12.4 6.89999 12.4C6.69999 12.4 6.6 12.3 6.5 12.2C6.4 12.1 6.3 11.9 6.3 11.7C6.3 11.5 6.4 11.3 6.5 11.1C6.6 10.9 6.8 10.7 7 10.5C7.2 10.3 7.49999 10.1 7.89999 10C8.29999 9.90003 8.60001 9.80003 9.10001 9.80003C9.50001 9.80003 9.80001 9.90003 10.1 10C10.4 10.1 10.7 10.3 10.9 10.4C11.1 10.5 11.3 10.8 11.4 11.1C11.5 11.4 11.6 11.6 11.6 11.9C11.6 12.3 11.5 12.6 11.3 12.9C11.1 13.2 10.9 13.5 10.6 13.7C10.9 13.9 11.2 14.1 11.4 14.3C11.6 14.5 11.8 14.7 11.9 15C12 15.3 12.1 15.5 12.1 15.8C12.1 16.2 12 16.5 11.9 16.8C11.8 17.1 11.5 17.4 11.3 17.7C11.1 18 10.7 18.2 10.3 18.3C9.9 18.4 9.5 18.5 9 18.5C8.5 18.5 8.1 18.4 7.7 18.2C7.3 18 7 17.8 6.8 17.6C6.6 17.4 6.4 17.1 6.3 16.8C6.2 16.5 6.10001 16.3 6.10001 16.1C6.10001 15.9 6.2 15.7 6.3 15.6C6.4 15.5 6.6 15.4 6.8 15.4C6.9 15.4 7.00001 15.4 7.10001 15.5C7.20001 15.6 7.3 15.6 7.3 15.7C7.5 16.2 7.7 16.6 8 16.9C8.3 17.2 8.6 17.3 9 17.3C9.2 17.3 9.5 17.2 9.7 17.1C9.9 17 10.1 16.8 10.3 16.6C10.5 16.4 10.5 16.1 10.5 15.8C10.5 15.3 10.4 15 10.1 14.7C9.80001 14.4 9.50001 14.3 9.10001 14.3C9.00001 14.3 8.9 14.3 8.7 14.3C8.5 14.3 8.39999 14.3 8.39999 14.3C8.19999 14.3 7.99999 14.2 7.89999 14.1C7.79999 14 7.7 13.8 7.7 13.7C7.7 13.5 7.79999 13.4 7.89999 13.2C7.99999 13 8.2 13 8.5 13H8.8V13.1ZM15.3 17.5V12.2C14.3 13 13.6 13.3 13.3 13.3C13.1 13.3 13 13.2 12.9 13.1C12.8 13 12.7 12.8 12.7 12.6C12.7 12.4 12.8 12.3 12.9 12.2C13 12.1 13.2 12 13.6 11.8C14.1 11.6 14.5 11.3 14.7 11.1C14.9 10.9 15.2 10.6 15.5 10.3C15.8 10 15.9 9.80003 15.9 9.70003C15.9 9.60003 16.1 9.60004 16.3 9.60004C16.5 9.60004 16.7 9.70003 16.8 9.80003C16.9 9.90003 17 10.2 17 10.5V17.2C17 18 16.7 18.4 16.2 18.4C16 18.4 15.8 18.3 15.6 18.2C15.4 18.1 15.3 17.8 15.3 17.5Z"
																			fill="currentColor" />
																	</svg>
																</span>
																<!--end::Svg Icon-->
																<!--end::Icon-->
																<!--begin::Datepicker-->
																<input
																	class="form-control form-control-solid ps-12 flatpickr-input"
																	placeholder="Select a date" name="dateFrom" type="datetime-local"
																	id="fromDate1" />
																<!--end::Datepicker-->
															</div>
														</div>


														<div class="col-md-3 fv-row">
															<label
																class="d-flex align-items-center fs-6 fw-semibold mb-2">
																<span class="">Date To:</span>
															</label>
															<!--end::Label-->
															<div class="position-relative d-flex align-items-center">
																<!--begin::Icon-->
																<!--begin::Svg Icon | path: icons/duotune/general/gen014.svg-->
																<span
																	class="svg-icon svg-icon-2 position-absolute mx-4">
																	<svg width="24" height="24" viewBox="0 0 24 24"
																		fill="none" xmlns="http://www.w3.org/2000/svg">
																		<path opacity="0.3"
																			d="M21 22H3C2.4 22 2 21.6 2 21V5C2 4.4 2.4 4 3 4H21C21.6 4 22 4.4 22 5V21C22 21.6 21.6 22 21 22Z"
																			fill="currentColor" />
																		<path
																			d="M6 6C5.4 6 5 5.6 5 5V3C5 2.4 5.4 2 6 2C6.6 2 7 2.4 7 3V5C7 5.6 6.6 6 6 6ZM11 5V3C11 2.4 10.6 2 10 2C9.4 2 9 2.4 9 3V5C9 5.6 9.4 6 10 6C10.6 6 11 5.6 11 5ZM15 5V3C15 2.4 14.6 2 14 2C13.4 2 13 2.4 13 3V5C13 5.6 13.4 6 14 6C14.6 6 15 5.6 15 5ZM19 5V3C19 2.4 18.6 2 18 2C17.4 2 17 2.4 17 3V5C17 5.6 17.4 6 18 6C18.6 6 19 5.6 19 5Z"
																			fill="currentColor" />
																		<path
																			d="M8.8 13.1C9.2 13.1 9.5 13 9.7 12.8C9.9 12.6 10.1 12.3 10.1 11.9C10.1 11.6 10 11.3 9.8 11.1C9.6 10.9 9.3 10.8 9 10.8C8.8 10.8 8.59999 10.8 8.39999 10.9C8.19999 11 8.1 11.1 8 11.2C7.9 11.3 7.8 11.4 7.7 11.6C7.6 11.8 7.5 11.9 7.5 12.1C7.5 12.2 7.4 12.2 7.3 12.3C7.2 12.4 7.09999 12.4 6.89999 12.4C6.69999 12.4 6.6 12.3 6.5 12.2C6.4 12.1 6.3 11.9 6.3 11.7C6.3 11.5 6.4 11.3 6.5 11.1C6.6 10.9 6.8 10.7 7 10.5C7.2 10.3 7.49999 10.1 7.89999 10C8.29999 9.90003 8.60001 9.80003 9.10001 9.80003C9.50001 9.80003 9.80001 9.90003 10.1 10C10.4 10.1 10.7 10.3 10.9 10.4C11.1 10.5 11.3 10.8 11.4 11.1C11.5 11.4 11.6 11.6 11.6 11.9C11.6 12.3 11.5 12.6 11.3 12.9C11.1 13.2 10.9 13.5 10.6 13.7C10.9 13.9 11.2 14.1 11.4 14.3C11.6 14.5 11.8 14.7 11.9 15C12 15.3 12.1 15.5 12.1 15.8C12.1 16.2 12 16.5 11.9 16.8C11.8 17.1 11.5 17.4 11.3 17.7C11.1 18 10.7 18.2 10.3 18.3C9.9 18.4 9.5 18.5 9 18.5C8.5 18.5 8.1 18.4 7.7 18.2C7.3 18 7 17.8 6.8 17.6C6.6 17.4 6.4 17.1 6.3 16.8C6.2 16.5 6.10001 16.3 6.10001 16.1C6.10001 15.9 6.2 15.7 6.3 15.6C6.4 15.5 6.6 15.4 6.8 15.4C6.9 15.4 7.00001 15.4 7.10001 15.5C7.20001 15.6 7.3 15.6 7.3 15.7C7.5 16.2 7.7 16.6 8 16.9C8.3 17.2 8.6 17.3 9 17.3C9.2 17.3 9.5 17.2 9.7 17.1C9.9 17 10.1 16.8 10.3 16.6C10.5 16.4 10.5 16.1 10.5 15.8C10.5 15.3 10.4 15 10.1 14.7C9.80001 14.4 9.50001 14.3 9.10001 14.3C9.00001 14.3 8.9 14.3 8.7 14.3C8.5 14.3 8.39999 14.3 8.39999 14.3C8.19999 14.3 7.99999 14.2 7.89999 14.1C7.79999 14 7.7 13.8 7.7 13.7C7.7 13.5 7.79999 13.4 7.89999 13.2C7.99999 13 8.2 13 8.5 13H8.8V13.1ZM15.3 17.5V12.2C14.3 13 13.6 13.3 13.3 13.3C13.1 13.3 13 13.2 12.9 13.1C12.8 13 12.7 12.8 12.7 12.6C12.7 12.4 12.8 12.3 12.9 12.2C13 12.1 13.2 12 13.6 11.8C14.1 11.6 14.5 11.3 14.7 11.1C14.9 10.9 15.2 10.6 15.5 10.3C15.8 10 15.9 9.80003 15.9 9.70003C15.9 9.60003 16.1 9.60004 16.3 9.60004C16.5 9.60004 16.7 9.70003 16.8 9.80003C16.9 9.90003 17 10.2 17 10.5V17.2C17 18 16.7 18.4 16.2 18.4C16 18.4 15.8 18.3 15.6 18.2C15.4 18.1 15.3 17.8 15.3 17.5Z"
																			fill="currentColor" />
																	</svg>
																</span>
																<!--end::Svg Icon-->
																<!--end::Icon-->
																<!--begin::Datepicker-->
																<input
																	class="form-control form-control-solid ps-12 flatpickr-input"
																	placeholder="Select a date" name="dateTo" type="datetime-local"
																	id="toDate1" />
																<!--end::Datepicker-->
															</div>
														</div>



														<div class="col-md-3 fv-row">
															<label
																class="d-flex align-items-center fs-6 fw-semibold mb-2">
																<span class="">Aquirer:</span>
															</label>

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
																	listValue="name" listKey="code" name="name"
																	value="name" class="myCheckBox" id="acquirerType" />
															</div>
															<input type="hidden" id="acquirerType"
																name="acquirerType" />
														</div>

														<div class="col-md-3 fv-row mt-7">
															<button type="button" class="btn btn-primary"
																id="fetch" onclick="fetchAcquirer();">Fetch</button>

																<button type="button" class="btn btn-primary"
																id="fetchDownload" onclick="fetchAcquirerDownload();">Download</button>

														</div>
													</div>
													<br>
													<div class="row" id="firstGraph" style="display:none;">

														<div id="chartContainer" style="height: 400px; width: 100%;"></div>
														<button class="chartContainer1"></button>
													</div>
													<br>

													<div class="row">

										<!-- New Table code start here -->

											<div class="table-responsive">
												<table id="dataTable1"
													class="table table-striped table-row-bordered gy-5 gs-7 dataTable no-footer">
													<thead>
														<tr class="fw-bold fs-6 text-gray-800">
															<th class="min-w-90px">Acquirer</th>
															<th class="min-w-90px">Captured</th>
															<th class="min-w-90px">Captured %</th>
															<th class="min-w-90px">Failed</th>
															<th class="min-w-90px">Failed %</th>
															<th class="min-w-90px">Request Accepted</th>
															<th class="min-w-90px">Request Accepted %</th>
															<th class="min-w-90px">Grand Total</th>
														</tr>
													</thead>
													<tfoot>
														<tr class="fw-bold fs-6 text-gray-800">
															<th class="min-w-90px"></th>
															<th class="min-w-90px"></th>
															<th class="min-w-90px"></th>
															<th class="min-w-90px"></th>
															<th class="min-w-90px"></th>
															<th class="min-w-90px"></th>
															<th class="min-w-90px"></th>
															<th class="min-w-90px"></th>
														</tr>
													</tfoot>
												</table>
											</div>

										<!-- New Table code end here -->


													</div>
													<br>


												</div>
												<!-- First div end here -->


												<!-- Second div start here -->
												<div class="tab-pane fade" id="acquirermertxnsum">

													<div class="row">
														<div class="col-md-3 fv-row">
															<label
																class="d-flex align-items-center fs-6 fw-semibold mb-2">
																<span class="">Date From:</span>
															</label>
															<!--end::Label-->
															<div class="position-relative d-flex align-items-center">
																<!--begin::Icon-->
																<!--begin::Svg Icon | path: icons/duotune/general/gen014.svg-->
																<span
																	class="svg-icon svg-icon-2 position-absolute mx-4">
																	<svg width="24" height="24" viewBox="0 0 24 24"
																		fill="none" xmlns="http://www.w3.org/2000/svg">
																		<path opacity="0.3"
																			d="M21 22H3C2.4 22 2 21.6 2 21V5C2 4.4 2.4 4 3 4H21C21.6 4 22 4.4 22 5V21C22 21.6 21.6 22 21 22Z"
																			fill="currentColor" />
																		<path
																			d="M6 6C5.4 6 5 5.6 5 5V3C5 2.4 5.4 2 6 2C6.6 2 7 2.4 7 3V5C7 5.6 6.6 6 6 6ZM11 5V3C11 2.4 10.6 2 10 2C9.4 2 9 2.4 9 3V5C9 5.6 9.4 6 10 6C10.6 6 11 5.6 11 5ZM15 5V3C15 2.4 14.6 2 14 2C13.4 2 13 2.4 13 3V5C13 5.6 13.4 6 14 6C14.6 6 15 5.6 15 5ZM19 5V3C19 2.4 18.6 2 18 2C17.4 2 17 2.4 17 3V5C17 5.6 17.4 6 18 6C18.6 6 19 5.6 19 5Z"
																			fill="currentColor" />
																		<path
																			d="M8.8 13.1C9.2 13.1 9.5 13 9.7 12.8C9.9 12.6 10.1 12.3 10.1 11.9C10.1 11.6 10 11.3 9.8 11.1C9.6 10.9 9.3 10.8 9 10.8C8.8 10.8 8.59999 10.8 8.39999 10.9C8.19999 11 8.1 11.1 8 11.2C7.9 11.3 7.8 11.4 7.7 11.6C7.6 11.8 7.5 11.9 7.5 12.1C7.5 12.2 7.4 12.2 7.3 12.3C7.2 12.4 7.09999 12.4 6.89999 12.4C6.69999 12.4 6.6 12.3 6.5 12.2C6.4 12.1 6.3 11.9 6.3 11.7C6.3 11.5 6.4 11.3 6.5 11.1C6.6 10.9 6.8 10.7 7 10.5C7.2 10.3 7.49999 10.1 7.89999 10C8.29999 9.90003 8.60001 9.80003 9.10001 9.80003C9.50001 9.80003 9.80001 9.90003 10.1 10C10.4 10.1 10.7 10.3 10.9 10.4C11.1 10.5 11.3 10.8 11.4 11.1C11.5 11.4 11.6 11.6 11.6 11.9C11.6 12.3 11.5 12.6 11.3 12.9C11.1 13.2 10.9 13.5 10.6 13.7C10.9 13.9 11.2 14.1 11.4 14.3C11.6 14.5 11.8 14.7 11.9 15C12 15.3 12.1 15.5 12.1 15.8C12.1 16.2 12 16.5 11.9 16.8C11.8 17.1 11.5 17.4 11.3 17.7C11.1 18 10.7 18.2 10.3 18.3C9.9 18.4 9.5 18.5 9 18.5C8.5 18.5 8.1 18.4 7.7 18.2C7.3 18 7 17.8 6.8 17.6C6.6 17.4 6.4 17.1 6.3 16.8C6.2 16.5 6.10001 16.3 6.10001 16.1C6.10001 15.9 6.2 15.7 6.3 15.6C6.4 15.5 6.6 15.4 6.8 15.4C6.9 15.4 7.00001 15.4 7.10001 15.5C7.20001 15.6 7.3 15.6 7.3 15.7C7.5 16.2 7.7 16.6 8 16.9C8.3 17.2 8.6 17.3 9 17.3C9.2 17.3 9.5 17.2 9.7 17.1C9.9 17 10.1 16.8 10.3 16.6C10.5 16.4 10.5 16.1 10.5 15.8C10.5 15.3 10.4 15 10.1 14.7C9.80001 14.4 9.50001 14.3 9.10001 14.3C9.00001 14.3 8.9 14.3 8.7 14.3C8.5 14.3 8.39999 14.3 8.39999 14.3C8.19999 14.3 7.99999 14.2 7.89999 14.1C7.79999 14 7.7 13.8 7.7 13.7C7.7 13.5 7.79999 13.4 7.89999 13.2C7.99999 13 8.2 13 8.5 13H8.8V13.1ZM15.3 17.5V12.2C14.3 13 13.6 13.3 13.3 13.3C13.1 13.3 13 13.2 12.9 13.1C12.8 13 12.7 12.8 12.7 12.6C12.7 12.4 12.8 12.3 12.9 12.2C13 12.1 13.2 12 13.6 11.8C14.1 11.6 14.5 11.3 14.7 11.1C14.9 10.9 15.2 10.6 15.5 10.3C15.8 10 15.9 9.80003 15.9 9.70003C15.9 9.60003 16.1 9.60004 16.3 9.60004C16.5 9.60004 16.7 9.70003 16.8 9.80003C16.9 9.90003 17 10.2 17 10.5V17.2C17 18 16.7 18.4 16.2 18.4C16 18.4 15.8 18.3 15.6 18.2C15.4 18.1 15.3 17.8 15.3 17.5Z"
																			fill="currentColor" />
																	</svg>
																</span>
																<!--end::Svg Icon-->
																<!--end::Icon-->
																<!--begin::Datepicker-->
																<input
																	class="form-control form-control-solid ps-12 flatpickr-input"
																	placeholder="Select a date" name="dateFrom" type="datetime-local"
																	id="fromDate2" />
																<!--end::Datepicker-->
															</div>
														</div>


														<div class="col-md-3 fv-row">
															<label
																class="d-flex align-items-center fs-6 fw-semibold mb-2">
																<span class="">Date To:</span>
															</label>
															<!--end::Label-->
															<div class="position-relative d-flex align-items-center">
																<!--begin::Icon-->
																<!--begin::Svg Icon | path: icons/duotune/general/gen014.svg-->
																<span
																	class="svg-icon svg-icon-2 position-absolute mx-4">
																	<svg width="24" height="24" viewBox="0 0 24 24"
																		fill="none" xmlns="http://www.w3.org/2000/svg">
																		<path opacity="0.3"
																			d="M21 22H3C2.4 22 2 21.6 2 21V5C2 4.4 2.4 4 3 4H21C21.6 4 22 4.4 22 5V21C22 21.6 21.6 22 21 22Z"
																			fill="currentColor" />
																		<path
																			d="M6 6C5.4 6 5 5.6 5 5V3C5 2.4 5.4 2 6 2C6.6 2 7 2.4 7 3V5C7 5.6 6.6 6 6 6ZM11 5V3C11 2.4 10.6 2 10 2C9.4 2 9 2.4 9 3V5C9 5.6 9.4 6 10 6C10.6 6 11 5.6 11 5ZM15 5V3C15 2.4 14.6 2 14 2C13.4 2 13 2.4 13 3V5C13 5.6 13.4 6 14 6C14.6 6 15 5.6 15 5ZM19 5V3C19 2.4 18.6 2 18 2C17.4 2 17 2.4 17 3V5C17 5.6 17.4 6 18 6C18.6 6 19 5.6 19 5Z"
																			fill="currentColor" />
																		<path
																			d="M8.8 13.1C9.2 13.1 9.5 13 9.7 12.8C9.9 12.6 10.1 12.3 10.1 11.9C10.1 11.6 10 11.3 9.8 11.1C9.6 10.9 9.3 10.8 9 10.8C8.8 10.8 8.59999 10.8 8.39999 10.9C8.19999 11 8.1 11.1 8 11.2C7.9 11.3 7.8 11.4 7.7 11.6C7.6 11.8 7.5 11.9 7.5 12.1C7.5 12.2 7.4 12.2 7.3 12.3C7.2 12.4 7.09999 12.4 6.89999 12.4C6.69999 12.4 6.6 12.3 6.5 12.2C6.4 12.1 6.3 11.9 6.3 11.7C6.3 11.5 6.4 11.3 6.5 11.1C6.6 10.9 6.8 10.7 7 10.5C7.2 10.3 7.49999 10.1 7.89999 10C8.29999 9.90003 8.60001 9.80003 9.10001 9.80003C9.50001 9.80003 9.80001 9.90003 10.1 10C10.4 10.1 10.7 10.3 10.9 10.4C11.1 10.5 11.3 10.8 11.4 11.1C11.5 11.4 11.6 11.6 11.6 11.9C11.6 12.3 11.5 12.6 11.3 12.9C11.1 13.2 10.9 13.5 10.6 13.7C10.9 13.9 11.2 14.1 11.4 14.3C11.6 14.5 11.8 14.7 11.9 15C12 15.3 12.1 15.5 12.1 15.8C12.1 16.2 12 16.5 11.9 16.8C11.8 17.1 11.5 17.4 11.3 17.7C11.1 18 10.7 18.2 10.3 18.3C9.9 18.4 9.5 18.5 9 18.5C8.5 18.5 8.1 18.4 7.7 18.2C7.3 18 7 17.8 6.8 17.6C6.6 17.4 6.4 17.1 6.3 16.8C6.2 16.5 6.10001 16.3 6.10001 16.1C6.10001 15.9 6.2 15.7 6.3 15.6C6.4 15.5 6.6 15.4 6.8 15.4C6.9 15.4 7.00001 15.4 7.10001 15.5C7.20001 15.6 7.3 15.6 7.3 15.7C7.5 16.2 7.7 16.6 8 16.9C8.3 17.2 8.6 17.3 9 17.3C9.2 17.3 9.5 17.2 9.7 17.1C9.9 17 10.1 16.8 10.3 16.6C10.5 16.4 10.5 16.1 10.5 15.8C10.5 15.3 10.4 15 10.1 14.7C9.80001 14.4 9.50001 14.3 9.10001 14.3C9.00001 14.3 8.9 14.3 8.7 14.3C8.5 14.3 8.39999 14.3 8.39999 14.3C8.19999 14.3 7.99999 14.2 7.89999 14.1C7.79999 14 7.7 13.8 7.7 13.7C7.7 13.5 7.79999 13.4 7.89999 13.2C7.99999 13 8.2 13 8.5 13H8.8V13.1ZM15.3 17.5V12.2C14.3 13 13.6 13.3 13.3 13.3C13.1 13.3 13 13.2 12.9 13.1C12.8 13 12.7 12.8 12.7 12.6C12.7 12.4 12.8 12.3 12.9 12.2C13 12.1 13.2 12 13.6 11.8C14.1 11.6 14.5 11.3 14.7 11.1C14.9 10.9 15.2 10.6 15.5 10.3C15.8 10 15.9 9.80003 15.9 9.70003C15.9 9.60003 16.1 9.60004 16.3 9.60004C16.5 9.60004 16.7 9.70003 16.8 9.80003C16.9 9.90003 17 10.2 17 10.5V17.2C17 18 16.7 18.4 16.2 18.4C16 18.4 15.8 18.3 15.6 18.2C15.4 18.1 15.3 17.8 15.3 17.5Z"
																			fill="currentColor" />
																	</svg>
																</span>
																<!--end::Svg Icon-->
																<!--end::Icon-->
																<!--begin::Datepicker-->
																<input
																	class="form-control form-control-solid ps-12 flatpickr-input"
																	placeholder="Select a date" name="dateTo" type="datetime-local"
																	id="toDate2" />
																<!--end::Datepicker-->
															</div>
														</div>


														<div class="col-md-3 fv-row">
															<label class="d-flex align-items-center fs-6 fw-bold mb-2">
																<span class="required">Acquirer</span>
															</label>
															<s:select headerKey="Select Acquirer"
																headerValue="Select Acquirer"
																list="@com.pay10.commons.util.AcquirerTypeUI@values()"
																id="acquirer"
																class="form-select form-select-solid adminMerchants"
																name="acquirer" value="acquirer" listValue="name"
																listKey="code" />
														</div>



														<div class="col-md-3 fv-row mt-7">
															<button type="button" class="btn btn-primary"
																id="merchantSummary"
																onclick="fetchMerchantSummary();">Fetch</button>
																<button type="button" class="btn btn-primary"
																id="merchantSummaryDownload"
																onclick="fetchMerchantSummaryDownload();">Download</button>

														</div>
													</div>
													<br>
													<div class="row"  id="secondGraph" style="display:none;">
														<div id="chartContainerMerchant" style="height: 400px; width: 100%;"></div>
														<button class="chartContainer1"></button>
													</div>
													<br>
													<div class="row">

														<!-- New Table code start here -->

											<div class="table-responsive">
												<table id="dataTable2"
													class="table table-striped table-row-bordered gy-5 gs-7 dataTable no-footer">
													<thead>
														<tr class="fw-bold fs-6 text-gray-800">
															<th class="min-w-90px">Merchant</th>
															<th class="min-w-90px">Captured</th>
															<th class="min-w-90px">Captured %</th>
															<th class="min-w-90px">Failed</th>
															<th class="min-w-90px">Failed %</th>
															<th class="min-w-90px">Request Accepted</th>
															<th class="min-w-90px">Request Accepted %</th>
															<th class="min-w-90px">Grand Total</th>
														</tr>
													</thead>
													<tfoot>
														<tr class="fw-bold fs-6 text-gray-800">
															<th class="min-w-90px"></th>
															<th class="min-w-90px"></th>
															<th class="min-w-90px"></th>
															<th class="min-w-90px"></th>
															<th class="min-w-90px"></th>
															<th class="min-w-90px"></th>
															<th class="min-w-90px"></th>
															<th class="min-w-90px"></th>
														</tr>
													</tfoot>
												</table>
											</div>

										<!-- New Table code end here -->


													</div>
													<br>


												</div>
												<!-- Second div end here -->


												<!-- Third div start here -->
												<div class="tab-pane fade" id="totalcountamount">
													<div class="row">
														<div class="col-md-3 fv-row">
															<label
																class="d-flex align-items-center fs-6 fw-semibold mb-2">
																<span class="">Date From:</span>
															</label>
															<!--end::Label-->
															<div class="position-relative d-flex align-items-center">
																<!--begin::Icon-->
																<!--begin::Svg Icon | path: icons/duotune/general/gen014.svg-->
																<span
																	class="svg-icon svg-icon-2 position-absolute mx-4">
																	<svg width="24" height="24" viewBox="0 0 24 24"
																		fill="none" xmlns="http://www.w3.org/2000/svg">
																		<path opacity="0.3"
																			d="M21 22H3C2.4 22 2 21.6 2 21V5C2 4.4 2.4 4 3 4H21C21.6 4 22 4.4 22 5V21C22 21.6 21.6 22 21 22Z"
																			fill="currentColor" />
																		<path
																			d="M6 6C5.4 6 5 5.6 5 5V3C5 2.4 5.4 2 6 2C6.6 2 7 2.4 7 3V5C7 5.6 6.6 6 6 6ZM11 5V3C11 2.4 10.6 2 10 2C9.4 2 9 2.4 9 3V5C9 5.6 9.4 6 10 6C10.6 6 11 5.6 11 5ZM15 5V3C15 2.4 14.6 2 14 2C13.4 2 13 2.4 13 3V5C13 5.6 13.4 6 14 6C14.6 6 15 5.6 15 5ZM19 5V3C19 2.4 18.6 2 18 2C17.4 2 17 2.4 17 3V5C17 5.6 17.4 6 18 6C18.6 6 19 5.6 19 5Z"
																			fill="currentColor" />
																		<path
																			d="M8.8 13.1C9.2 13.1 9.5 13 9.7 12.8C9.9 12.6 10.1 12.3 10.1 11.9C10.1 11.6 10 11.3 9.8 11.1C9.6 10.9 9.3 10.8 9 10.8C8.8 10.8 8.59999 10.8 8.39999 10.9C8.19999 11 8.1 11.1 8 11.2C7.9 11.3 7.8 11.4 7.7 11.6C7.6 11.8 7.5 11.9 7.5 12.1C7.5 12.2 7.4 12.2 7.3 12.3C7.2 12.4 7.09999 12.4 6.89999 12.4C6.69999 12.4 6.6 12.3 6.5 12.2C6.4 12.1 6.3 11.9 6.3 11.7C6.3 11.5 6.4 11.3 6.5 11.1C6.6 10.9 6.8 10.7 7 10.5C7.2 10.3 7.49999 10.1 7.89999 10C8.29999 9.90003 8.60001 9.80003 9.10001 9.80003C9.50001 9.80003 9.80001 9.90003 10.1 10C10.4 10.1 10.7 10.3 10.9 10.4C11.1 10.5 11.3 10.8 11.4 11.1C11.5 11.4 11.6 11.6 11.6 11.9C11.6 12.3 11.5 12.6 11.3 12.9C11.1 13.2 10.9 13.5 10.6 13.7C10.9 13.9 11.2 14.1 11.4 14.3C11.6 14.5 11.8 14.7 11.9 15C12 15.3 12.1 15.5 12.1 15.8C12.1 16.2 12 16.5 11.9 16.8C11.8 17.1 11.5 17.4 11.3 17.7C11.1 18 10.7 18.2 10.3 18.3C9.9 18.4 9.5 18.5 9 18.5C8.5 18.5 8.1 18.4 7.7 18.2C7.3 18 7 17.8 6.8 17.6C6.6 17.4 6.4 17.1 6.3 16.8C6.2 16.5 6.10001 16.3 6.10001 16.1C6.10001 15.9 6.2 15.7 6.3 15.6C6.4 15.5 6.6 15.4 6.8 15.4C6.9 15.4 7.00001 15.4 7.10001 15.5C7.20001 15.6 7.3 15.6 7.3 15.7C7.5 16.2 7.7 16.6 8 16.9C8.3 17.2 8.6 17.3 9 17.3C9.2 17.3 9.5 17.2 9.7 17.1C9.9 17 10.1 16.8 10.3 16.6C10.5 16.4 10.5 16.1 10.5 15.8C10.5 15.3 10.4 15 10.1 14.7C9.80001 14.4 9.50001 14.3 9.10001 14.3C9.00001 14.3 8.9 14.3 8.7 14.3C8.5 14.3 8.39999 14.3 8.39999 14.3C8.19999 14.3 7.99999 14.2 7.89999 14.1C7.79999 14 7.7 13.8 7.7 13.7C7.7 13.5 7.79999 13.4 7.89999 13.2C7.99999 13 8.2 13 8.5 13H8.8V13.1ZM15.3 17.5V12.2C14.3 13 13.6 13.3 13.3 13.3C13.1 13.3 13 13.2 12.9 13.1C12.8 13 12.7 12.8 12.7 12.6C12.7 12.4 12.8 12.3 12.9 12.2C13 12.1 13.2 12 13.6 11.8C14.1 11.6 14.5 11.3 14.7 11.1C14.9 10.9 15.2 10.6 15.5 10.3C15.8 10 15.9 9.80003 15.9 9.70003C15.9 9.60003 16.1 9.60004 16.3 9.60004C16.5 9.60004 16.7 9.70003 16.8 9.80003C16.9 9.90003 17 10.2 17 10.5V17.2C17 18 16.7 18.4 16.2 18.4C16 18.4 15.8 18.3 15.6 18.2C15.4 18.1 15.3 17.8 15.3 17.5Z"
																			fill="currentColor" />
																	</svg>
																</span>
																<!--end::Svg Icon-->
																<!--end::Icon-->
																<!--begin::Datepicker-->
																<input
																	class="form-control form-control-solid ps-12"
																	placeholder="Select a date" name="dateFrom"
																	 type="datetime-local" id="fromDate3"/>
																<!--end::Datepicker-->
															</div>
														</div>


														<div class="col-md-3 fv-row">
															<label
																class="d-flex align-items-center fs-6 fw-semibold mb-2">
																<span class="">Date To:</span>
															</label>
															<!--end::Label-->
															<div class="position-relative d-flex align-items-center">
																<!--begin::Icon-->
																<!--begin::Svg Icon | path: icons/duotune/general/gen014.svg-->
																<span
																	class="svg-icon svg-icon-2 position-absolute mx-4">
																	<svg width="24" height="24" viewBox="0 0 24 24"
																		fill="none" xmlns="http://www.w3.org/2000/svg">
																		<path opacity="0.3"
																			d="M21 22H3C2.4 22 2 21.6 2 21V5C2 4.4 2.4 4 3 4H21C21.6 4 22 4.4 22 5V21C22 21.6 21.6 22 21 22Z"
																			fill="currentColor" />
																		<path
																			d="M6 6C5.4 6 5 5.6 5 5V3C5 2.4 5.4 2 6 2C6.6 2 7 2.4 7 3V5C7 5.6 6.6 6 6 6ZM11 5V3C11 2.4 10.6 2 10 2C9.4 2 9 2.4 9 3V5C9 5.6 9.4 6 10 6C10.6 6 11 5.6 11 5ZM15 5V3C15 2.4 14.6 2 14 2C13.4 2 13 2.4 13 3V5C13 5.6 13.4 6 14 6C14.6 6 15 5.6 15 5ZM19 5V3C19 2.4 18.6 2 18 2C17.4 2 17 2.4 17 3V5C17 5.6 17.4 6 18 6C18.6 6 19 5.6 19 5Z"
																			fill="currentColor" />
																		<path
																			d="M8.8 13.1C9.2 13.1 9.5 13 9.7 12.8C9.9 12.6 10.1 12.3 10.1 11.9C10.1 11.6 10 11.3 9.8 11.1C9.6 10.9 9.3 10.8 9 10.8C8.8 10.8 8.59999 10.8 8.39999 10.9C8.19999 11 8.1 11.1 8 11.2C7.9 11.3 7.8 11.4 7.7 11.6C7.6 11.8 7.5 11.9 7.5 12.1C7.5 12.2 7.4 12.2 7.3 12.3C7.2 12.4 7.09999 12.4 6.89999 12.4C6.69999 12.4 6.6 12.3 6.5 12.2C6.4 12.1 6.3 11.9 6.3 11.7C6.3 11.5 6.4 11.3 6.5 11.1C6.6 10.9 6.8 10.7 7 10.5C7.2 10.3 7.49999 10.1 7.89999 10C8.29999 9.90003 8.60001 9.80003 9.10001 9.80003C9.50001 9.80003 9.80001 9.90003 10.1 10C10.4 10.1 10.7 10.3 10.9 10.4C11.1 10.5 11.3 10.8 11.4 11.1C11.5 11.4 11.6 11.6 11.6 11.9C11.6 12.3 11.5 12.6 11.3 12.9C11.1 13.2 10.9 13.5 10.6 13.7C10.9 13.9 11.2 14.1 11.4 14.3C11.6 14.5 11.8 14.7 11.9 15C12 15.3 12.1 15.5 12.1 15.8C12.1 16.2 12 16.5 11.9 16.8C11.8 17.1 11.5 17.4 11.3 17.7C11.1 18 10.7 18.2 10.3 18.3C9.9 18.4 9.5 18.5 9 18.5C8.5 18.5 8.1 18.4 7.7 18.2C7.3 18 7 17.8 6.8 17.6C6.6 17.4 6.4 17.1 6.3 16.8C6.2 16.5 6.10001 16.3 6.10001 16.1C6.10001 15.9 6.2 15.7 6.3 15.6C6.4 15.5 6.6 15.4 6.8 15.4C6.9 15.4 7.00001 15.4 7.10001 15.5C7.20001 15.6 7.3 15.6 7.3 15.7C7.5 16.2 7.7 16.6 8 16.9C8.3 17.2 8.6 17.3 9 17.3C9.2 17.3 9.5 17.2 9.7 17.1C9.9 17 10.1 16.8 10.3 16.6C10.5 16.4 10.5 16.1 10.5 15.8C10.5 15.3 10.4 15 10.1 14.7C9.80001 14.4 9.50001 14.3 9.10001 14.3C9.00001 14.3 8.9 14.3 8.7 14.3C8.5 14.3 8.39999 14.3 8.39999 14.3C8.19999 14.3 7.99999 14.2 7.89999 14.1C7.79999 14 7.7 13.8 7.7 13.7C7.7 13.5 7.79999 13.4 7.89999 13.2C7.99999 13 8.2 13 8.5 13H8.8V13.1ZM15.3 17.5V12.2C14.3 13 13.6 13.3 13.3 13.3C13.1 13.3 13 13.2 12.9 13.1C12.8 13 12.7 12.8 12.7 12.6C12.7 12.4 12.8 12.3 12.9 12.2C13 12.1 13.2 12 13.6 11.8C14.1 11.6 14.5 11.3 14.7 11.1C14.9 10.9 15.2 10.6 15.5 10.3C15.8 10 15.9 9.80003 15.9 9.70003C15.9 9.60003 16.1 9.60004 16.3 9.60004C16.5 9.60004 16.7 9.70003 16.8 9.80003C16.9 9.90003 17 10.2 17 10.5V17.2C17 18 16.7 18.4 16.2 18.4C16 18.4 15.8 18.3 15.6 18.2C15.4 18.1 15.3 17.8 15.3 17.5Z"
																			fill="currentColor" />
																	</svg>
																</span>
																<!--end::Svg Icon-->
																<!--end::Icon-->
																<!--begin::Datepicker-->
																<input
																	class="form-control form-control-solid ps-12 "
																	placeholder="Select a date" name="dateTo"
																	id="toDate3" type="datetime-local" />
																<!--end::Datepicker-->
															</div>
														</div>




														<div class="col-md-3 fv-row mt-7">
															<button type="button" class="btn btn-primary"
																id="count" onclick="fetchCountAndAmount();">Fetch
																Count</button>

																<button type="button" class="btn btn-primary"
																id="countDownload" onclick="fetchCountAndAmountDownload();">Download</button>

														</div>
													</div>

													<br>

													<div class="row" id="thirdGraph" style="display:none;">
														<div id="chartContainerCount" style="height: 400px; width: 100%;"></div>
														<button class="chartContainer1"></button>
													</div>

													<br>
													<div class="row">

														<div class="table-responsive">
												<table id="dataTable3"
													class="table table-striped table-row-bordered gy-5 gs-7 dataTable no-footer">
													<thead>
														<tr class="fw-bold fs-6 text-gray-800">
															<th class="min-w-90px">Acquirer</th>
															<th class="min-w-90px">Total Amount</th>
															<th class="min-w-90px">Total Count</th>
														</tr>
													</thead>
													<tfoot>
														<tr class="fw-bold fs-6 text-gray-800">
															<th class="min-w-90px"></th>
															<th class="min-w-90px"></th>
															<th class="min-w-90px"></th>
														</tr>
													</tfoot>
												</table>
											</div>


													</div>
													<br>

												</div>
												<!-- Third div end here -->


											</div>


										</div>
										<!-- Card body end -->

									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
<!-- First tab download code start here -->
<form action="acquirerWiseSummaryDownload">
<input type="hidden" id="a" name="fromDate">
<input type="hidden" id="b" name="toDate">
<input type="hidden" id="c" name="acquirerType">
<input type="submit" style="display:none;" id="submitAcquirer">
</form>
<!-- First tab download code end here -->


<!-- Second tab download code start here -->
<form action="merchantWiseSummaryDownload">
<input type="hidden" id="aa" name="fromDate">
<input type="hidden" id="bb" name="toDate">
<input type="hidden" id="cc" name="acquirer">
<input type="submit" style="display:none;" id="submitMerchant">
</form>
<!-- Second tab download code end here -->


<!-- Third tab download code start here -->
<form action="countAndAmountDownload">
<input type="hidden" id="aaa" name="fromDate">
<input type="hidden" id="bbb" name="toDate">
<input type="submit" style="display:none;" id="submitCount">
</form>
<!-- Third tab download code end here -->

			</div>


			<script src="../assets/plugins/global/plugins.bundle.js"></script>
			<script src="../assets/js/scripts.bundle.js"></script>
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
					debugger
					var allInputCheckBox = document
						.getElementsByClassName("myCheckBox");

					var allSelectedAquirer = [];
					for (var i = 0; i < allInputCheckBox.length; i++) {

						if (allInputCheckBox[i].checked) {
							allSelectedAquirer.push(allInputCheckBox[i].value);
						}
					}

					document.getElementById('acquirerType').value=allSelectedAquirer;

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
<!-- First tab script code start here -->
	<script type="text/javascript">
	function fetchAcquirerDownload(){
	document.getElementById("fetchDownload").disabled = true;
	var fromDate = $("#fromDate1").val();
	var toDate = $("#toDate1").val();
	var acquirer = $("#acquirerType").val();

	if(fromDate==''){
		alert("Select From Date");
		document.getElementById("fetchDownload").disabled = false;
		return false;
		}


		if(toDate==''){
		alert("Select To Date");
		document.getElementById("fetchDownload").disabled = false;
		return false;
		}

		var initialTimeFormat = moment(fromDate);
		var endTimeFormat = moment(toDate);

		var difference = endTimeFormat.diff(initialTimeFormat,"hours");

		if(difference>360){
			alert("No. of days can not be more than 15");
			document.getElementById("fetchDownload").disabled = false;
			return false;
		}


	if(acquirer==''){
		acquirer='ALL';
	}

	$("#a").val(fromDate);
	$("#b").val(toDate);
	$("#c").val(acquirer);
	document.getElementById("fetchDownload").disabled = false;
	$("#submitAcquirer").click();


	}
	</script>

	<script type="text/javascript">
	function fetchAcquirer() {
		document.getElementById("fetch").disabled = true;
		var fromDate = $("#fromDate1").val();
		var toDate = $("#toDate1").val();
		var acquirer = $("#acquirerType").val();

		if(fromDate==''){
			alert("Select From Date");
			document.getElementById("fetch").disabled = false;
			return false;
			}


			if(toDate==''){
			alert("Select To Date");
			document.getElementById("fetch").disabled = false;
			return false;
			}

			var initialTimeFormat = moment(fromDate);
			var endTimeFormat = moment(toDate);

			var difference = endTimeFormat.diff(initialTimeFormat,"hours");

			if(difference>360){
				alert("No. of days can not be more than 15");
				document.getElementById("fetch").disabled = false;
				return false;
			}

		if(acquirer==''){
		acquirer='ALL';
		}

		$.post("acquirerWiseSummary", {
			fromDate: fromDate,
			toDate: toDate,
			acquirerType: acquirer,
			}, function (result) {
				debugger
						document.getElementById("fetch").disabled = false;
						var data = result.acquirerWiseSummary;
						if(Object.keys(data).length==0){
						alert("No records found!");
							return false;
						}

						renderTableAcquirer(result.aaData);

						var totalCaptured=0;
						var totalCapturedPercentage=0.0;
						var totalFailed=0;
						var totalFailedPercentage=0.0;
						var totalPending=0;
						var totalPendingPercentage=0.0;
						var grandtotal=0;

						var totalCountRow=0;

						var acquirerNames=[];
						var capturedData=[];
						var pendingData=[];
						var failedData=[];

						$.each(data, function (index, value) {
							totalCaptured+=value.captured;
							totalCapturedPercentage+=value.capturedPercentage;
							totalFailed+=value.failed;
							totalFailedPercentage+=value.failedPercentage;
							totalPending+=value.pending;
							totalPendingPercentage+=value.pendingPercentage;
							grandtotal+=value.grandTotal;
							totalCountRow++;
							acquirerNames.push(value.acquirerName);
							capturedData.push(value.captured);
							pendingData.push(value.pending);
							failedData.push(value.failed);
						});


						acquirerWiseChart(acquirerNames,capturedData,failedData,pendingData);

					});

				}

				function convert(str) {
					var date = new Date(str), mnth = ("0" + (date.getMonth() + 1))
						.slice(-2), day = ("0" + date.getDate()).slice(-2);
					return [day, mnth, date.getFullYear()].join("-");
				}

</script>

<!-- First tab script code end here -->

<!-- Third tab script code start here -->

<script type="text/javascript">
function fetchCountAndAmountDownload(){
	document.getElementById("countDownload").disabled = true;
	var fromDate = $("#fromDate3").val();
	var toDate = $("#toDate3").val();

	if(fromDate==''){
	alert("Select From Date");
	document.getElementById("countDownload").disabled = false;
	return false;
	}


	if(toDate==''){
	alert("Select To Date");
	document.getElementById("countDownload").disabled = false;
	return false;
	}

	var initialTimeFormat = moment(fromDate);
	var endTimeFormat = moment(toDate);

	var difference = endTimeFormat.diff(initialTimeFormat,"hours");

	if(difference>360){
		alert("No. of days can not be more than 15");
		document.getElementById("countDownload").disabled = false;
		return false;
	}

	$("#aaa").val(fromDate);
	$("#bbb").val(toDate);
	document.getElementById("countDownload").disabled = false;
	$("#submitCount").click();
}
</script>

	<script type="text/javascript">
				function fetchCountAndAmount() {
					document.getElementById("count").disabled = true;
					var fromDate = $("#fromDate3").val();
					var toDate = $("#toDate3").val();

					if(fromDate==''){
					alert("Select From Date");
					document.getElementById("count").disabled = false;
					return false;
					}


					if(toDate==''){
					alert("Select To Date");
					document.getElementById("count").disabled = false;
					return false;
					}

					var initialTimeFormat = moment(fromDate);
					var endTimeFormat = moment(toDate);

					var difference = endTimeFormat.diff(initialTimeFormat,"hours");

					if(difference>360){
						alert("No. of days can not be more than 15");
						document.getElementById("count").disabled = false;
						return false;
					}

					$.post("acquirerWiseCountAndAmount", {
						fromDate: fromDate,
						toDate: toDate,
					}, function (result) {

						document.getElementById("count").disabled = false;
						var data = result.acquirerCountAndAmount;
						if(Object.keys(data).length==0){
						alert("No records found!");

							return false;
						}

						renderTableCount(result.aaDataCount);

						var totalAmount=0.0;
						var totalCount=0.0;
						var grandtotal=0;

						var totalAmountData=[];
						var totalCountData=[];
						var acquirerNames=[];

						$.each(data, function (index, value) {
							totalAmount+=value.totalAmount;
							totalCount+=value.totalCount;

							acquirerNames.push(value.acquirerName);
							totalAmountData.push(value.totalAmount);
							totalCountData.push(value.totalCount);

						});


						accountCountAndChart(acquirerNames,totalAmountData,totalCountData);

					});

				}
			</script>
<!-- Third tab script code end here -->

<!-- Second tab script code start here -->

<script type="text/javascript">
function fetchMerchantSummaryDownload(){
	document.getElementById("merchantSummaryDownload").disabled = true;
	var fromDate = $("#fromDate2").val();
	var toDate = $("#toDate2").val();
	var acquirer=$("#acquirer").val();

	if(fromDate==''){
		alert("Select From Date");
		document.getElementById("merchantSummaryDownload").disabled = false;
		return false;
		}


		if(toDate==''){
		alert("Select To Date");
		document.getElementById("merchantSummaryDownload").disabled = false;
		return false;
		}

		var initialTimeFormat = moment(fromDate);
		var endTimeFormat = moment(toDate);

		var difference = endTimeFormat.diff(initialTimeFormat,"hours");

		if(difference>360){
			alert("No. of days can not be more than 15");
			document.getElementById("merchantSummaryDownload").disabled = false;
			return false;
		}

	if(acquirer=='Select Acquirer'){
	alert("Select Acquirer");
	document.getElementById("merchantSummaryDownload").disabled = false;
	return false;
	}

	$("#aa").val(fromDate);
	$("#bb").val(toDate);
	$("#cc").val(acquirer);

	document.getElementById("merchantSummaryDownload").disabled = false;
	$("#submitMerchant").click();

}
</script>

	<script type="text/javascript">

				function fetchMerchantSummary() {
					document.getElementById("merchantSummary").disabled = true;
					var fromDate = $("#fromDate2").val();
					var toDate = $("#toDate2").val();
					var acquirer=$("#acquirer").val();

					if(fromDate==''){
						alert("Select From Date");
						document.getElementById("merchantSummary").disabled = false;
						return false;
						}


						if(toDate==''){
						alert("Select To Date");
						document.getElementById("merchantSummary").disabled = false;
						return false;
						}

						var initialTimeFormat = moment(fromDate);
						var endTimeFormat = moment(toDate);

						var difference = endTimeFormat.diff(initialTimeFormat,"hours");

						if(difference>360){
							alert("No. of days can not be more than 15");
							document.getElementById("merchantSummary").disabled = false;
							return false;
						}


					if(acquirer=='Select Acquirer'){
					alert("Select Acquirer");
					document.getElementById("merchantSummary").disabled = false;
					return false;
					}

					$.post("merchantWiseSummary", {
						fromDate: fromDate,
						toDate: toDate,
						acquirer: acquirer,
					}, function (result) {

						document.getElementById("merchantSummary").disabled = false;
						var data = result.merchantWiseSummary;
						if(Object.keys(data).length==0){
						alert("No records found!");
							return false;
						}

						renderTableMerchant(result.aaData);

						var totalCaptured=0;
						var totalCapturedPercentage=0.0;
						var totalFailed=0;
						var totalFailedPercentage=0.0;
						var totalPending=0;
						var totalPendingPercentage=0.0;
						var grandtotal=0;

						var totalCountRow=0;

						var merchantNames=[];
						var capturedData=[];
						var pendingData=[];
						var failedData=[];

						$.each(data, function (index, value) {
							totalCaptured+=value.captured;
							totalCapturedPercentage+=value.capturedPercentage;
							totalFailed+=value.failed;
							totalFailedPercentage+=value.failedPercentage;
							totalPending+=value.pending;
							totalPendingPercentage+=value.pendingPercentage;
							grandtotal+=value.grandTotal;
							totalCountRow++;
							merchantNames.push(value.merchantName);
							capturedData.push(value.captured);
							pendingData.push(value.pending);
							failedData.push(value.failed);
						});

						merchantWiseChart(merchantNames,capturedData,failedData,pendingData,acquirer);

					});
				}
</script>
	<!-- Second tab script code start here -->

<!-- All chart code start here -->

<script>
	function acquirerWiseChart(acquirerNames,captures,faileds,pendings){
	var capturedData=[];
	var pendingData=[];
	var failedData=[];

	for(var i=0;i<acquirerNames.length;i++){
			capturedData.push({y:captures[i],label:acquirerNames[i]});
			failedData.push({y:faileds[i],label:acquirerNames[i]});
			pendingData.push({y:pendings[i],label:acquirerNames[i]});
	}


	var chart = new CanvasJS.Chart("chartContainer", {
	animationEnabled: true,
	title:{
		text: "Count of Status by Acquirer and Status",
		fontSize: 25
	},
	 	exportEnabled:true,
	    exportFileName:"Acquirer Wise Chart",
	axisY: {
		title: "Count of Status",
		titleFontColor: "#4F81BC",
		lineColor: "#4F81BC",
		labelFontColor: "#4F81BC",
		tickColor: "#4F81BC"
	},

	toolTip: {
		shared: true
	},
	legend: {
		cursor:"pointer",
		itemclick: toggleDataSeries
	},
	data: [{
		type: "column",
		name: "Captured",
		legendText: "Captured",
		showInLegend: true,
		dataPoints:capturedData
	},

	{
		type: "column",
		name: "Failed",
		legendText: "Failed",
		showInLegend: true,
		dataPoints:failedData
	},
	{
		type: "column",
		name: "Request Accepted",
		legendText: "Request Accepted",
		showInLegend: true,
		dataPoints:pendingData
	}]
});

	$("#firstGraph").show();

	chart.render();
	$('.canvasjs-chart-toolbar button>img').remove();
	$('.canvasjs-chart-toolbar button').text('Export');

function toggleDataSeries(e) {
	if (typeof(e.dataSeries.visible) === "undefined" || e.dataSeries.visible) {
		e.dataSeries.visible = false;
	}
	else {
		e.dataSeries.visible = true;
	}
	chart.render();
	$('.canvasjs-chart-toolbar button>img').remove();
	$('.canvasjs-chart-toolbar button').text('Export');
}
}
</script>

<script>

function merchantWiseChart(merchantNames,captures,faileds,pendings,acquirerName){

	var capturedData=[];
	var pendingData=[];
	var failedData=[];

	for(var i=0;i<merchantNames.length;i++){
			capturedData.push({y:captures[i],label:merchantNames[i]});
			failedData.push({y:faileds[i],label:merchantNames[i]});
			pendingData.push({y:pendings[i],label:merchantNames[i]});
	}

var chart = new CanvasJS.Chart("chartContainerMerchant", {
	animationEnabled: true,

	title:{
		text:"Count of total Amount",
		fontSize: 25
	},
	exportEnabled:true,
    exportFileName:"Merchant Wise Chart",
	axisX:{
		title: "Merchant Name",
		interval: 1
	},
	axisY2:{
		interlacedColor: "rgba(1,77,101,.2)",
		title: acquirerName + " Status"
	},
	toolTip: {
		shared: true
	},

	data: [{
		type: "bar",
		name: "Captured",
		showInLegend: true,
		axisYType: "secondary",
		dataPoints: capturedData
	},
	{
		type: "bar",
		name: "Failed",
		showInLegend: true,
		axisYType: "secondary",
		dataPoints: failedData
	},
	{
		type: "bar",
		name: "Request Accepted",
		showInLegend: true,
		axisYType: "secondary",
		dataPoints: pendingData
	}
]
});
$("#secondGraph").show();
chart.render();
$('.canvasjs-chart-toolbar button>img').remove();
$('.canvasjs-chart-toolbar button').text('Export');

}
</script>

<script>
function accountCountAndChart(acquirerNames,totalAmount,totalCount){
	debugger
	var totalAmountData=[];
	var totalCountData=[];

	for(var i=0;i<acquirerNames.length;i++){
		totalAmountData.push({y:totalAmount[i],label:acquirerNames[i]});
		totalCountData.push({y:totalCount[i],label:acquirerNames[i]});
	}

var chart = new CanvasJS.Chart("chartContainerCount", {
	animationEnabled: true,

	title:{
		text:"Count of total Amount by Acquirer",
		fontSize: 25
	},
	exportEnabled:true,
    exportFileName:"Count Of Total Amount",
	axisX:{
		title: "Acquirers Name",
		interval: 1
	},
	axisY2:{
		interlacedColor: "rgba(1,77,101,.2)",
		title: "Count of Total Amount"
	},
	toolTip: {
		shared: true
	},
	
	data: [
	{
		type: "bar",
		name: "Count",
		showInLegend: true,
		color:"#4F81BC",
		dataPoints: totalCountData
	},
]
});
$("#thirdGraph").show();
chart.render();
$('.canvasjs-chart-toolbar button>img').remove();
$('.canvasjs-chart-toolbar button').text('Export');

}
let dateInput = document.getElementById("fromDate3");
dateInput.max = new Date().toISOString().slice(0,new Date().toISOString().lastIndexOf(":"));
let todateInput = document.getElementById("toDate3");
todateInput.max = new Date().toISOString().slice(0,new Date().toISOString().lastIndexOf(":"));

function hideCheckbox()
{
		debugger
		var checkboxes = document.getElementById("checkboxes");
		if (!expanded) {
			
		} else {
			checkboxes.style.display = "none";
			expanded = false;
		}
}
</script>
<!-- All chart code end here -->

<script type="text/javascript">
function renderTableAcquirer(data) {
	debugger
	  var getindex = 0;
	  var table = new $.fn.dataTable.Api('#dataTable1');

	  var buttonCommon = {
	    exportOptions: {
	      format: {
	        body: function(data, column, row, node) {
	          return (column === 1 ? "'" + data : data);
	        }
	      }
	    }
	  };

	  $("#dataTable1").DataTable().destroy();

	  $('#dataTable1').dataTable({
			"footerCallback": function (row, data, start, end, display) {
				var api = this.api(), data;

				// Remove the formatting to get integer data for summation
				var intVal = function (i) {
					return typeof i === 'string' ? i.replace(/[\,]/g, '') * 1 : typeof i === 'number' ? i : 0;
				};
				$( api.column(0).footer()).html('Total');
				// Total over this page
				captured = api.column(1, {
					page: 'current'
				}).data().reduce(function (a, b) {
					return intVal(a) + intVal(b);
				}, 0);

				// Update footer
				$(api.column(1).footer()).html(
					'' + inrFormat(captured.toFixed(2) + ' ' + ' '));

				capturedPer = api.column(2, {
					page: 'current'
				}).data().reduce(function (a, b) {
					return intVal(a) + intVal(b);
				}, 0);

				// Update footer
				$(api.column(2).footer()).html(
					'' + inrFormat(capturedPer.toFixed(2)+ ' ' + ' '));
				
				failed = api.column(3, {
					page: 'current'
				}).data().reduce(function (a, b) {
					return intVal(a) + intVal(b);
				}, 0);
				
				$(api.column(3).footer()).html(
						'' + inrFormat(failed.toFixed(2) + ' ' + ' '));

				// Total over this page
				failedPer = api.column(4, {
					page: 'current'
				}).data().reduce(function (a, b) {
					return intVal(a) + intVal(b);
				}, 0);

				// Update footer
				$(api.column(4).footer()).html(
					'' + inrFormat(failedPer.toFixed(2) + ' ' + ' '));

				pending = api.column(5, {
					page: 'current'
				}).data().reduce(function (a, b) {
					return intVal(a) + intVal(b);
				}, 0);

				// Update footer
				$(api.column(5).footer()).html(
					'' + inrFormat(pending.toFixed(2) + ' ' + ' '));

				
				pendingPer = api.column(6, {
					page: 'current'
				}).data().reduce(function (a, b) {
					return intVal(a) + intVal(b);
				}, 0);

				// Update footer
				$(api.column(6).footer()).html(
					'' + inrFormat(pendingPer.toFixed(2) + ' ' + ' '));


				grandTotal = api.column(7, {
					page: 'current'
				}).data().reduce(function (a, b) {
					return intVal(a) + intVal(b);
				}, 0);

				// Update footer
				$(api.column(7).footer()).html(
					'' + inrFormat(grandTotal.toFixed(2) + ' ' + ' '));


			},
	    'data': data,

	    dom: 'BTrftlpi',
	    buttons: [
	      $.extend(true, {}, buttonCommon, {
	        extend: 'copyHtml5',
	        exportOptions: {
	          columns: [0, 1, 2, 3, 4, 5, 6]
	        },
	      }),
	      $.extend(true, {}, buttonCommon, {
	        extend: 'csvHtml5',
	        title: 'Acquirer Wise Summary Report',
	        exportOptions: {
	          columns: [0, 1, 2, 3, 4, 5, 6],
	        },
	      }),
	      {
	        extend: 'pdfHtml5',
	        orientation: 'landscape',
	        pageSize: 'legal',
	        title: 'Acquirer Wise Summary Report',
	        exportOptions: {
	          columns: [0, 1, 2, 3, 4, 5, 6]
	        },
	        customize: function(doc) {
	          doc.defaultStyle.alignment = 'center';
	          doc.styles.tableHeader.alignment = 'center';
	        }
	      },

	      {
	        extend: 'colvis',
	        columns: [0, 1, 2, 3, 4, 5, 6]
	      }],
	  	"scrollY" : true,
		"scrollX" : true,
	    'columns': [
	      {
	        'data': 'acquirerName',
	        'className': 'text-class'
	      },
	      
	      {
	        'data': 'captured',
	        'className': 'text-class'
	      },

	      {
	        'data': 'capturedPercentage',
	        'className': 'text-class'
	      },

	      {
	        'data': 'failed',
	        'className': 'text-class'
	      },

	      {
	        'data': 'failedPercentage',
	        'className': 'text-class'
	      },
	      {
	        'data': 'pending',
	        'className': 'text-class'
	      },

	      {
	        'data': 'pendingPercentage',
	        'className': 'text-class'
	      },

	      {
		     'data': 'grandTotal',
		     'className': 'text-class'
		   }]
	  });
	}

</script>

<script type="text/javascript">
function renderTableMerchant(data) {
	debugger
	  var getindex = 0;
	  var table = new $.fn.dataTable.Api('#dataTable2');

	  var buttonCommon = {
	    exportOptions: {
	      format: {
	        body: function(data, column, row, node) {
	          return (column === 1 ? "'" + data : data);
	        }
	      }
	    }
	  };

	  $("#dataTable2").DataTable().destroy();

	  $('#dataTable2').dataTable({
			"footerCallback": function (row, data, start, end, display) {
				var api = this.api(), data;

				// Remove the formatting to get integer data for summation
				var intVal = function (i) {
					return typeof i === 'string' ? i.replace(/[\,]/g, '') * 1 : typeof i === 'number' ? i : 0;
				};
				$( api.column(0).footer()).html('Total');
				// Total over this page
				captured = api.column(1, {
					page: 'current'
				}).data().reduce(function (a, b) {
					return intVal(a) + intVal(b);
				}, 0);

				// Update footer
				$(api.column(1).footer()).html(
					'' + inrFormat(captured.toFixed(2) + ' ' + ' '));

				capturedPer = api.column(2, {
					page: 'current'
				}).data().reduce(function (a, b) {
					return intVal(a) + intVal(b);
				}, 0);

				// Update footer
				$(api.column(2).footer()).html(
					'' + inrFormat(capturedPer.toFixed(2)+ ' ' + ' '));
				
				failed = api.column(3, {
					page: 'current'
				}).data().reduce(function (a, b) {
					return intVal(a) + intVal(b);
				}, 0);
				
				$(api.column(3).footer()).html(
						'' + inrFormat(failed.toFixed(2) + ' ' + ' '));

				// Total over this page
				failedPer = api.column(4, {
					page: 'current'
				}).data().reduce(function (a, b) {
					return intVal(a) + intVal(b);
				}, 0);

				// Update footer
				$(api.column(4).footer()).html(
					'' + inrFormat(failedPer.toFixed(2) + ' ' + ' '));

				pending = api.column(5, {
					page: 'current'
				}).data().reduce(function (a, b) {
					return intVal(a) + intVal(b);
				}, 0);

				// Update footer
				$(api.column(5).footer()).html(
					'' + inrFormat(pending.toFixed(2) + ' ' + ' '));

				
				pendingPer = api.column(6, {
					page: 'current'
				}).data().reduce(function (a, b) {
					return intVal(a) + intVal(b);
				}, 0);

				// Update footer
				$(api.column(6).footer()).html(
					'' + inrFormat(pendingPer.toFixed(2) + ' ' + ' '));


				grandTotal = api.column(7, {
					page: 'current'
				}).data().reduce(function (a, b) {
					return intVal(a) + intVal(b);
				}, 0);

				// Update footer
				$(api.column(7).footer()).html(
					'' + inrFormat(grandTotal.toFixed(2) + ' ' + ' '));


			},
	    'data': data,

	    dom: 'BTrftlpi',
	    buttons: [
	      $.extend(true, {}, buttonCommon, {
	        extend: 'copyHtml5',
	        exportOptions: {
	          columns: [0, 1, 2, 3, 4, 5, 6]
	        },
	      }),
	      $.extend(true, {}, buttonCommon, {
	        extend: 'csvHtml5',
	        title: 'Merchant Wise Summary Report',
	        exportOptions: {
	          columns: [0, 1, 2, 3, 4, 5, 6],
	        },
	      }),
	      {
	        extend: 'pdfHtml5',
	        orientation: 'landscape',
	        pageSize: 'legal',
	        title: 'Merchant Wise Summary Report',
	        exportOptions: {
	          columns: [0, 1, 2, 3, 4, 5, 6]
	        },
	        customize: function(doc) {
	          doc.defaultStyle.alignment = 'center';
	          doc.styles.tableHeader.alignment = 'center';
	        }
	      },

	      {
	        extend: 'colvis',
	        columns: [0, 1, 2, 3, 4, 5, 6]
	      }],
	  	"scrollY" : true,
		"scrollX" : true,
	    'columns': [
	      {
	        'data': 'merchantName',
	        'className': 'text-class'
	      },
	      
	      {
	        'data': 'captured',
	        'className': 'text-class'
	      },

	      {
	        'data': 'capturedPercentage',
	        'className': 'text-class'
	      },

	      {
	        'data': 'failed',
	        'className': 'text-class'
	      },

	      {
	        'data': 'failedPercentage',
	        'className': 'text-class'
	      },
	      {
	        'data': 'pending',
	        'className': 'text-class'
	      },

	      {
	        'data': 'pendingPercentage',
	        'className': 'text-class'
	      },

	      {
		    'data': 'grandTotal',
		    'className': 'text-class'
		   }]
	  });
	}

</script>

<script type="text/javascript">
function renderTableCount(data) {
	debugger
	  var getindex = 0;
	  var table = new $.fn.dataTable.Api('#dataTable3');

	  var buttonCommon = {
	    exportOptions: {
	      format: {
	        body: function(data, column, row, node) {
	          return (column === 1 ? "'" + data : data);
	        }
	      }
	    }
	  };

	  $("#dataTable3").DataTable().destroy();

	  $('#dataTable3').dataTable({
			"footerCallback": function (row, data, start, end, display) {
				var api = this.api(), data;

				// Remove the formatting to get integer data for summation
				var intVal = function (i) {
					return typeof i === 'string' ? i.replace(/[\,]/g, '') * 1 : typeof i === 'number' ? i : 0;
				};
				$( api.column(0).footer()).html('Total');
				// Total over this page
				totalAmount = api.column(1, {
					page: 'current'
				}).data().reduce(function (a, b) {
					return intVal(a) + intVal(b);
				}, 0);

				// Update footer
				$(api.column(1).footer()).html(
					'' + inrFormat(totalAmount.toFixed(2) + ' ' + ' '));

				totalCount = api.column(2, {
					page: 'current'
				}).data().reduce(function (a, b) {
					return intVal(a) + intVal(b);
				}, 0);

				// Update footer
				$(api.column(2).footer()).html(
					'' + inrFormat(totalCount.toFixed(2)+ ' ' + ' '));

			},
	    'data': data,

	    dom: 'BTrftlpi',
	    buttons: [
	      $.extend(true, {}, buttonCommon, {
	        extend: 'copyHtml5',
	        exportOptions: {
	          columns: [0, 1, 2]
	        },
	      }),
	      $.extend(true, {}, buttonCommon, {
	        extend: 'csvHtml5',
	        title: 'Count Wise Summary Report',
	        exportOptions: {
	          columns: [0, 1, 2],
	        },
	      }),
	      {
	        extend: 'pdfHtml5',
	        orientation: 'landscape',
	        pageSize: 'legal',
	        title: 'Count Wise Summary Report',
	        exportOptions: {
	          columns: [0, 1, 2]
	        },
	        customize: function(doc) {
	          doc.defaultStyle.alignment = 'center';
	          doc.styles.tableHeader.alignment = 'center';
	        }
	      },

	      {
	        extend: 'colvis',
	        columns: [0, 1, 2]
	      }],
	  	"scrollY" : true,
		"scrollX" : true,
	    'columns': [
	      {
	        'data': 'acquirerName',
	        'className': 'text-class'
	      },
	      
	      {
	        'data': 'totalAmount',
	        'className': 'text-class'
	      },

	      {
	        'data': 'totalCount',
	        'className': 'text-class'
	      }]
	  });
	}

</script>

	</body>

	</html>