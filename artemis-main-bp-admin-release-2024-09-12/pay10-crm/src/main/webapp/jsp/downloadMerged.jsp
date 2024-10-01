<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<%@ taglib uri="/struts-tags" prefix="s"%>
<html>

<head>
<meta charset="ISO-8859-1">
<title>Download Page</title>
<link rel="shortcut icon" href="assets/media/images/paylogo.svg" />
<!--begin::Fonts-->
<link rel="stylesheet"
	href="https://fonts.googleapis.com/css?family=Inter:300,400,500,600,700" />
<!--end::Fonts-->
<!--begin::Vendor Stylesheets(used by this page)-->
<!-- <link href="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.css" rel="stylesheet"
						type="text/css" /> -->
<link href="../assets/plugins/custom/datatables/datatables.bundle.css"
	rel="stylesheet" type="text/css" />
<!--end::Vendor Stylesheets-->
<!--begin::Global Stylesheets Bundle(used by all pages)-->
<link href="../assets/plugins/global/plugins.bundle.css"
	rel="stylesheet" type="text/css" />
<link href="../assets/css/style.bundle.css" rel="stylesheet"
	type="text/css" />
<link href="../assets/css/custom/custom-style.css" rel="stylesheet"
	type="text/css" />
<link rel="stylesheet" type="text/css" href="../css/material-icons.css">
<link rel="stylesheet" href="../css/material-font-awesome.min.css">
<script src="../assets/plugins/global/plugins.bundle.js"></script>
<script src="../assets/js/scripts.bundle.js"></script>
<script src="../js/jquery.popupoverlay.js"></script>
<link href="https://fonts.googleapis.com/icon?family=Material+Icons"
	rel="stylesheet">
<link href="../css/select2.min.css" rel="stylesheet" />
<script src="../js/jquery.select2.js" type="text/javascript"></script>

<script src="../js/jquery.dataTables.js"></script>
<script src="../js/jquery.popupoverlay.js"></script>
<script src="../js/plugins/perfect-scrollbar.jquery.min.js"></script>

<style>
.switch {
	position: relative;
	display: inline-block;
	width: 30px;
	height: 17px;
}

i.material-icons {
	margin-left: 5px !important;
	mcursor: pointer;
}

.switch input {
	display: none;
}

.slider {
	position: absolute;
	cursor: pointer;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	background-color: #ccc;
	-webkit-transition: .4s;
	transition: .4s;
}

.slider:before {
	position: absolute;
	content: "";
	height: 13px;
	width: 13px;
	left: 2px;
	bottom: 2px;
	background-color: white;
	-webkit-transition: .4s;
	transition: .4s;
}

input:checked+.slider {
	background-color: #2196F3;
}

input:focus+.slider {
	box-shadow: 0 0 1px #2196F3;
}

input:checked+.slider:before {
	-webkit-transform: translateX(13px);
	-ms-transform: translateX(13px);
	transform: translateX(13px);
}

/* Rounded sliders */
.slider.round {
	border-radius: 17px;
}

.slider.round:before {
	border-radius: 50%;
}

.mycheckbox {
	/* Your style here */
	
}

.switch {
	display: table-cell;
	vertical-align: middle;
	padding: 10px;
}

input.cmn-toggle-jwr:checked+label:after {
	margin-left: 1.5em;
}

table .toggle.btn {
	min-width: 48px;
	min-height: 28px;
}

table .btn {
	/* margin-bottom: 4px; */
	/* margin-right: 5px; */
	/* padding: 1px 12px;
				font-size: 11px; */
	
}

table .toggle-off.btn {
	padding: 0;
	margin: 0;
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

.error {
	font-family: "Times New Roman";
	color: red;
	width: 100%;
	margin-top: 8px;
}
</style>

</head>
<body>

	<div class="content d-flex flex-column flex-column-fluid"
		id="kt_content">
		<div class="post d-flex flex-column-fluid" id="kt_post">
			<!--begin::Container-->
			<div id="kt_content_container" class="container-xxl">
				<div class="row my-5">
					<div class="col">
						<div class="card">
							<div class="card-body">
								<div class="toolbar" id="kt_toolbar">
									<!--begin::Container-->
									<div id="kt_toolbar_container"
										class="container-fluid d-flex flex-stack">
										<!--begin::Page title-->
										<div data-kt-swapper="true" data-kt-swapper-mode="prepend"
											data-kt-swapper-parent="{default: '#kt_content_container', 'lg': '#kt_toolbar_container'}"
											class="page-title d-flex align-items-center me-3 flex-wrap mb-5 mb-lg-0 lh-1">
											<!--begin::Title-->
											<h1
												class="d-flex align-items-center text-dark fw-bold my-1 fs-3">
												Download Merged</h1>
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
												<li class="breadcrumb-item text-muted">PDF Merger</li>
												<!--end::Item-->
												<!--begin::Item-->
												<li class="breadcrumb-item"><span
													class="bullet bg-gray-200 w-5px h-2px"></span></li>
												<!--end::Item-->
												<!--begin::Item-->
												<li class="breadcrumb-item text-dark">Download Merged Files</li>
												<!--end::Item-->
											</ul>
											<!--end::Breadcrumb-->
										</div>
										<!--end::Page title-->

									</div>
									<!--end::Container-->
								</div>
								<div id="downloadMergedFilesView"></div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<script>
		$(document)
				.ready(
						function() {
							var name = "<s:property value='%{#session.USER.emailId}'/>";
							var urls = new URL(window.location.href);
							var domain = urls.origin;
							$
									.ajax({
										type : "GET",
										url: domain + "/crmws/pdf/getAllMergedFiles/" + name,
										//url : "http://localhost:8080/crmws/pdf/getAllMergedFiles/"
											//	+ name,
										success : function(data, status) {
											var response = JSON.parse(JSON
													.stringify(data));
											if (response.payLoad != null
													&& response.payLoad != '') {
												for (var i = 0; i < response.payLoad.length; i++) {
													let fileName = response.payLoad[i];
													let downloadBtn = fileName
															+ '<i class="material-icons" onclick="downloadfile(\''
															+ fileName
															+ '\');"> &nbsp;&nbsp;&nbsp;cloud_download </i>&nbsp;'
													+'<i class="material-icons" onclick="removefile(\''
															+ fileName
															+ '\');">cancel </i><br/>';
													$(
															"#downloadMergedFilesView")
															.append(downloadBtn);
												}
											}
										}
									});
						});
		function downloadfile(fileCode) {
			var urls = new URL(window.location.href);
			var domain = urls.origin;
			
			//window.open('http://localhost:8080/crmws/pdf/downloadFile/'
				//	+ fileCode);
			window.open(domain + '/crmws/pdf/downloadFile/' + fileCode);
		}

		function removefile(fileCode) {

			var urls = new URL(window.location.href);
			var domain = urls.origin;
			$.ajax({
				type : "GET",
				url : domain+'/crmws/pdf/deleteFile/' + fileCode,
				//url : 'http://localhost:8080/crmws/pdf/deleteFile/' + fileCode,
				success : function(data, status) {
					/* var response = JSON.stringify(data); */
					alert("Files deleted successfully!");
					window.location.reload();
				},
				error : function(status) {
					alert("Files not deleted successfully!");
				}
			});
		}
	</script>

</body>
</html>