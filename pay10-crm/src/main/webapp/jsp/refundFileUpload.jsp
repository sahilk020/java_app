<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="/struts-tags" prefix="s"%>
<html dir="ltr" lang="en-US">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Refund File Upload</title>
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
<script src="../assets/plugins/global/plugins.bundle.js"></script>
<script src="../assets/js/scripts.bundle.js"></script>
<script type="text/javascript" src="../js/sweetalert.js"></script>
<link rel="stylesheet" href="../css/sweetalert.css">
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/limonte-sweetalert2/6.6.9/sweetalert2.min.css">
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/limonte-sweetalert2/6.6.9/sweetalert2.min.js"></script>
<!--  loader scripts -->
<%-- <script src="../js/loader/modernizr-2.6.2.min.js"></script>
			<script src="../js/loader/main.js"></script> --%>
<link rel="stylesheet" href="../css/loader/normalize.css" />
<!-- <link rel="stylesheet" href="../css/loader/main.css" /> -->
<link rel="stylesheet" href="../css/loader/customLoader.css" />
<link href="../css/select2.min.css" rel="stylesheet" />
<script src="../js/jquery.select2.js" type="text/javascript"></script>
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
</style>
</head>
<body id="mainBody">
	
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
						Upload Refund File</h1>
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
						<li class="breadcrumb-item text-dark">Upload Refund File</li>
						<!--end::Item-->
					</ul>
					<!--end::Breadcrumb-->
				</div>
				<!--end::Page title-->

			</div>
		</div>		
		<!--end::Container-->
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
						<div id="chargeBackFilesDiv"></div>
						<input type="hidden" name="id" id="id" /> <input type="hidden"
							name="payId" id="payId" /> <input type="hidden" name="caseId"
							id="caseId" /> 
							<div class="col-md-3 fv-row">
							<label id="fileUpload"
							class="hide_accept_chargeback">File Upload:</label>&nbsp;&nbsp;&nbsp;&nbsp;
						<input type="file" name="file" id="file"
							accept="image/gif, image/jpeg,image/png,application/pdf,application/msword,application/vnd.openxmlformats-officedocument.wordprocessingml.document,application/vnd.ms-excel"
							class="form-control hide_accept_chargeback"> <span id="documentError"
							class="error"></span>
							</div>
							<div class="col-md-3 fv-row">
						<button type="submit"
							class="btn btn-primary btn-xs hide_accept_chargeback"
							style="margin-top: 20px;" name="submitFile" id="submitFile">Upload
							Document</button>
					     </div>
								
							</div>
						</div>
					</div>
				</div>
			</div>
	   </div>
	</div>


	<script type="text/javascript">
		$(document).ready(function() {
			$("#submitFile").click(function() {
				var fd = new FormData();
				var files = $('#file')[0].files[0];
				fd.append('file', files);

				var urls = new URL(window.location.href);
				var domain = urls.origin;

				$.ajax({
					//url: 'http://localhost:8080/crmws/refund/upload',
					url : domain + "/crmws/refund/upload",
					type : 'post',
					data : fd,
					contentType : false,
					processData : false,
					success : function(response) {
						if (response != 0) {
							alert(response);
							location.reload();
						} else {
							alert(response);
							location.reload();

						}
					},
				});
			});
		});
	</script>





</body>
</html>
