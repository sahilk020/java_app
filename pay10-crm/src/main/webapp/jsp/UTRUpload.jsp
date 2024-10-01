<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
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

<title>Acquirer UTR Report</title>



<script>
	$(document).ready(
			function() {

				$('.nav-tabs > li > a').click(
						function(event) {
							event.preventDefault();

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
					<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">UTR
						Upload</h1>
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
						<li class="breadcrumb-item text-muted">UTR Upload</li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item"><span
							class="bullet bg-gray-200 w-5px h-2px"></span></li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item text-dark">UTR Upload</li>
						<!--end::Item-->
					</ul>
					<!--end::Breadcrumb-->
				</div>
				<!--end::Page title-->

			</div>
			<!--end::Container-->
		</div>




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
											onclick="showHide('1')">Sale UTR </a> <a
											class="nav-item nav-link" id="nav-profile-tab"
											data-toggle="tab" href="#nav-profile" role="tab"
											aria-controls="nav-profile" aria-selected="false"
											onclick="showHide('2')">Chargeback UTR </a>
									</div>
									</nav>
									<!-- first tab -->
									<div class="tab-content" id="nav-tabContent">

										<div class="tab-pane" id="nav-home" role="tabpanel"
											aria-labelledby="nav-home-tab">
											<form id="filea" name="filea" method="post"
												action="mprUploadAction" enctype="multipart/form-data">
												<div class="row">
													<div class="col-md-3  file-group">
														<div class="file-input"
															style="display: block; overflow-y: hidden; overflow-x: hidden;">
															<input name="file" id="fileafield" type="file"
																accept=".xlsx, .xls, .csv" class="file-input__input">


															<label class="file-input__label" for="file-input">
																<img src="../assets/media/images/folder-svg.svg" alt="">
																<p class="m-0" id="filename"></p> <span>Browse</span>
															</label> <span id="filealabel" style="color: red; display: none;">Please
																Select File to Upload</span>
														</div>
													</div>

													<div class="col-md-3 fv-row mt-7">
														<button type="button" id="filea" onclick="a()"
															class="btn w-100 w-md-100 btn-primary">
															<span class="indicator-label">Upload UTR</span> <span
																class="indicator-progress">Please wait... <span
																class="spinner-border spinner-border-sm align-middle ms-2"></span></span>
														</button>

													</div>

													<div class="col-md-3 fv-row mt-7">
														<button type="button" id="downloadReport"
															class="btn w-100 w-md-100 btn-primary"
															onclick="checkValidationDownload(event)">
															<span class="indicator-label">Download</span> <span
																class="indicator-progress">Please wait... <span
																class="spinner-border spinner-border-sm align-middle ms-2"></span></span>
														</button>

													</div>
												</div>
											</form>
										</div>


										<!-- second tab -->
										<div class="tab-pane" id="nav-profile" role="tabpanel"
											aria-labelledby="nav-profile-tab">

											<form id="fileb" name="fileb" method="post"
												action="mprDownloadAction">

												<div class="row">
													<div class="col-lg-3 my-2">

														<label class="d-flex align-items-center fs-6 fw-bold mb-2">
															<span class="required">Type</span>
														</label> <select
															class="form-select form-select-solid adminMerchants"
															id="type">
															<option value="" selected>Please Select</option>
															<option value="Chargeback Initiated">Chargeback
																Initiated</option>
															<option value="Chargeback Reversal">Chargeback
																Reversal</option>
														</select>

													</div>
													<div class="col-md-3  file-group">
														<div class="file-input"
															style="display: block; overflow-y: hidden; overflow-x: hidden;">
															<input name="file" id="filebfield" type="file"
																accept=".xlsx, .xls, .csv" class="file-input__input">


															<label class="file-input__label" for="file-input">
																<img src="../assets/media/images/folder-svg.svg" alt="">
																<p class="m-0" id="filename"></p> <span>Browse</span>
															</label> <span id="fileblabel" style="color: red; display: none;">Please
																Select File to Upload</span>
														</div>
													</div>

													<div class="col-md-3 fv-row mt-7">
														<button type="button" onclick="b()"
															class="btn w-100 w-md-100 btn-primary">
															<span class="indicator-label">Upload ChargeBack
																UTR</span> <span class="indicator-progress">Please
																wait... <span
																class="spinner-border spinner-border-sm align-middle ms-2"></span>
															</span>
														</button>

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












					<script type="text/javascript">
						function showHide(value) {

							var tab1 = document.getElementById("nav-home-tab");
							var tab2 = document
									.getElementById("nav-profile-tab");
							var element1 = document.getElementById("nav-home");
							var element2 = document
									.getElementById("nav-profile");
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
					</script>

					<script>
						function a() {
							debugger
							var file = $('#fileafield').val();
							//var regex = /\.(xlsx|xls|xlsm)$/i;
							if (file.includes(".xlsx")
									|| file.includes(".xlsm")
									|| file.includes(".xls")) {
								var form = document.getElementById('filea');
								var url = 'bulkUploadUTR';
								var data = new FormData(form);
								saveFile(data, url);
							} else {
								alert('Please upload valid excel file .xlsx, .xlsm, .xls only.');
								$('#fileafield').val('');
							}

						}

						function b() {
							var type = $("#type").val();
							if (type != "" || type != '' || type != undefined) {
								var file = $('#filebfield').val();
								//var regex = /\.(xlsx|xls|xlsm)$/i;
								if (file.includes(".xlsx")
										|| file.includes(".xlsm")
										|| file.includes(".xls")) {

									var form = document.getElementById('fileb');
									var url = 'bulkChargebackUtr';
									var data = new FormData(form);
									saveFile1(data, url);
								} else {
									alert('Please upload valid excel file .xlsx, .xlsm, .xls only.');
									$('#filebfield').val('');
								}
							} else {
								alert("Please Select Type");
							}
						}

						function saveFile1(data, url) {
							var urls = new URL(window.location.href);
							var domain = urls.origin;

							$.ajax({
								url : domain + "/crmws/" + url+"/"+$("#type").val(),
								type : 'POST',
								enctype : 'multipart/form-data',
								data : data,
								processData : false,
								contentType : false,
								cache : false,
								success : function(data) {
									alert(data.respmessage);
									//window.location.reload();
								},
								error : function(data, textStatus, jqXHR) {
									alert(data.respmessage);
									//window.location.reload();
								}
							});
						}
						function saveFile(data, url) {
							var urls = new URL(window.location.href);
							var domain = urls.origin;

							$.ajax({
								url : domain + "/crmws/" + url,
								type : 'POST',
								enctype : 'multipart/form-data',
								data : data,
								processData : false,
								contentType : false,
								cache : false,
								success : function(data) {
									alert(data.respmessage);
									//window.location.reload();
								},
								error : function(data, textStatus, jqXHR) {
									alert(data.respmessage);
									//window.location.reload();
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
					<script
						src="https://cdn.amcharts.com/lib/5/geodata/continentsLow.js"></script>
					<script src="https://cdn.amcharts.com/lib/5/geodata/usaLow.js"></script>
					<script
						src="https://cdn.amcharts.com/lib/5/geodata/worldTimeZonesLow.js"></script>
					<script
						src="https://cdn.amcharts.com/lib/5/geodata/worldTimeZoneAreasLow.js"></script>
					<script
						src="../assets/plugins/custom/datatables/datatables.bundle.js"></script>
</body>
</html>