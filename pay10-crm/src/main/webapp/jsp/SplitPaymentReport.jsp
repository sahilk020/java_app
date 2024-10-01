<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="/struts-tags" prefix="s"%>
<html dir="ltr" lang="en-US">
<head>
<title>Split Payment Report</title>




<link rel="shortcut icon" href="assets/media/images/paylogo.svg" />
<!--begin::Fonts-->
<link rel="stylesheet"
	href="https://fonts.googleapis.com/css?family=Inter:300,400,500,600,700" />
<!--end::Fonts-->
<!--begin::Vendor Stylesheets(used by this page)-->
<link
	href="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.css"
	rel="stylesheet" type="text/css" />
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

<script src="../assets/plugins/global/plugins.bundle.js"></script>
<script src="../assets/js/scripts.bundle.js"></script>
<link href="../css/select2.min.css" rel="stylesheet" />
<script src="../js/jquery.select2.js" type="text/javascript"></script>





<%-- 
<link href="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.css" rel="stylesheet" type="text/css" />
<link href="../assets/plugins/custom/datatables/datatables.bundle.css" rel="stylesheet" type="text/css" />
<link href="../assets/plugins/global/plugins.bundle.css" rel="stylesheet" type="text/css" />
<link href="../assets/css/style.bundle.css" rel="stylesheet" type="text/css" />
<link href="../assets/css/custom/custom-style.css" rel="stylesheet" type="text/css" />

<script src="../js/jquery.min.js" type="text/javascript"></script>
<script src="../js/core/popper.min.js"></script>
<script src="../js/core/bootstrap-material-design.min.js"></script>
<script src="../js/plugins/perfect-scrollbar.jquery.min.js"></script>
<!-- Control Center for Material Dashboard: parallax effects, scripts for the example pages etc -->
<script src="../js/material-dashboard.js?v=2.1.0" type="text/javascript"></script>
<script src="../js/moment.js" type="text/javascript"></script>
<script src="../js/daterangepicker.js" type="text/javascript"></script>
<script src="../js/jquery.dataTables.js"></script>
<script src="../js/jquery-ui.js"></script>
<script src="../js/commanValidate.js"></script>
<script src="../js/jquery.popupoverlay.js"></script>
<script src="../js/dataTables.buttons.js" type="text/javascript"></script>
<script src="../js/pdfmake.js" type="text/javascript"></script>

<script src="../js/jszip.min.js" type="text/javascript"></script>
<script src="../js/vfs_fonts.js" type="text/javascript"></script>
<script src="../js/buttons.colVis.min.js" type="text/javascript"></script>

<script src="../js/jquery.select2.js" type="text/javascript"></script>
<!--  loader scripts -->
<script src="../js/loader/modernizr-2.6.2.min.js"></script>
<script src="../js/loader/main.js"></script> --%>


<script type="text/javascript">
	$(document).ready(function() {
		//$("#merchant").select2();
	});
	$(document).ready(function() {

		// Initialize select2
		$(".adminMerchants").select2();
	});
</script>

<!-- search -->
<script type="text/javascript">
	$(document).ready(function () {
	$("#merchant").select2();
	$("#acquirer").select2();
	
		});
</script>

<script type="text/javascript">
$(document).ready(function() {
    $('#example').DataTable( {
        dom: 'Bfrtip',
        buttons: [
        	{
                
                extend: 'copy',
                text: 'COPY',
                title:'Summary Report',
				exportOptions : {
					columns : ':visible'
				},
                
            },  {
             
                extend: 'csv',
                text: 'CSV',
                title:'Summary Report',
                exportOptions : {
					columns : ':visible'
				},
            },{
             
                extend: 'pdf',
                text: 'PDF',
                title:'Summary Report',
                exportOptions : {
					columns : ':visible'
				},
               
            },  {
             
                extend: 'print',
                text: 'PRINT',
                title:'Summary Report',
                exportOptions : {
					columns : ':visible'
				},
            },{
                extend: 'colvis',
                columnText: function ( dt, idx, title ) 
                {
                    return (idx+1)+': '+title;
                }
            }
        ]
    } );
} );
</script>
<script type="text/javascript">
	function summitdetail() {
		debugger
// 		$('#loader-wrapper').show();
// 		var res=reloadTable();
// 		if(res!=='false'){
	        var datecheck=document.getElementById("kt_datepicker_1").value;
	        if(datecheck!=''){
			document.getElementById("PreMisSettlement").submit();
	        }else{
				alert("Please select Date");
			}
// 		}


	}
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
	background: none !important; .
	card-list-toggle {cursor: pointer;
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
</style>
</head>
<body id="mainBody">
<div class="content d-flex flex-column flex-column-fluid" id="kt_content">
<div class="toolbar" id="kt_toolbar">
							<!--begin::Container-->
							<div id="kt_toolbar_container" class="container-fluid d-flex flex-stack">
								<!--begin::Page title-->
								<div data-kt-swapper="true" data-kt-swapper-mode="prepend" data-kt-swapper-parent="{default: '#kt_content_container', 'lg': '#kt_toolbar_container'}" class="page-title d-flex align-items-center me-3 flex-wrap mb-5 mb-lg-0 lh-1">
									<!--begin::Title-->
									<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">Split Payment Report</h1>
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
										<li class="breadcrumb-item text-muted">Payout Generate</li>
										<!--end::Item-->
										<!--begin::Item-->
										<li class="breadcrumb-item">
											<span class="bullet bg-gray-200 w-5px h-2px"></span>
										</li>
										<!--end::Item-->
										<!--begin::Item-->
										<li class="breadcrumb-item text-dark">Split Payment Report</li>
										<!--end::Item-->
									</ul>
									<!--end::Breadcrumb-->
								</div>
								<!--end::Page title-->
								
							</div>
							<!--end::Container-->
						</div>



	<div id="loader-wrapper"
		style="width: 100%; height: 100%; display: none;">
		<div id="loader"></div>
	</div>
	<div class="post d-flex flex-column-fluid" id="kt_post">
	<div id="kt_content_container" class="container-xxl">

	<div style="overflow: scroll !important;">
		<table id="mainTable" width="100%" border="0" align="center"
			cellpadding="0" cellspacing="0" class="txnf">
			<tr>
				<!-- <td colspan="5" align="left"><h2>Refund Captured</h2></td> -->
			</tr>
			<tr>
				<td colspan="5" align="left" valign="top">
					<div class="MerchBx">
					
					<div class="row my-5">
						<div class="col">						
							<div class="card ">								
								<div class="card-body ">
									<div class="container">
									
																				<%-- <s:if
													test="%{#session.USER.UserType.name()=='ADMIN' || #session.USER.UserType.name()=='SUBADMIN' || #session.USER_TYPE.name()=='SUPERADMIN' || #session.USER_TYPE.name()=='RESELLER'}"> --%>
											<form action="DownloadPreMisSettlementReportSplit" class="box-content"
												method="post" id="PreMisSettlement">
												<div class="row">
												<div class="col-md-4 fv-row">
											<label
												class="d-flex align-items-center fs-6 fw-semibold mb-2">
												<span class="">Merchant:</span>
											</label>
											<!--end::Label-->
											<s:select name="payId" 	data-control="select2"
												data-placeholder="Select Merchant"
												class="form-select form-select-solid"
												data-hide-search="true" id="merchant"
																headerKey="All" headerValue="ALL" list="merchantList"
																listKey="payId" listValue="businessName"
																autocomplete="off" />
											
										</div>
										<div class="col-md-4 fv-row">
											<label
												class="d-flex align-items-center fs-6 fw-semibold mb-2">
												<span class="">Acquirer:</span>
											</label>
											<!--end::Label-->

											<s:select headerKey="All" headerValue="All"
											data-control="select2"
												data-placeholder="Select Acquirer"
												class="form-select form-select-solid"
												data-hide-search="true"
											list="@com.pay10.commons.util.AcquirerTypeUI@values()"
							listKey="name" listValue="code"
											 name="acquirer" id="acquirer" autocomplete="off" value="" />

										</div>	
										
										<div class="col-md-4 fv-row">
											<label
												class="d-flex align-items-center fs-6 fw-semibold mb-2">
												<span class="">Date:</span>
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
												<input type="date" class="form-control form-control-solid ps-12"
													placeholder="Select a date" name="dateFrom"
													id="kt_datepicker_1" />
												<!--end::Datepicker-->
											</div>
										</div>
										&nbsp
										<div>
									<button type="button"  id="downloadReport" class="btn w-100 w-md-25 btn-primary" onclick="summitdetail()">
													<span class="indicator-label">Download</span>
													<span class="indicator-progress">Please wait...
														<span
															class="spinner-border spinner-border-sm align-middle ms-2"></span></span>
												</button>
												</div>			
											</div>	
											</form>

											<%-- </s:if> --%>
										
									</div>
									</div>
								</div>
							</div>
						</div>
					</div>

				</td>
			</tr>
			<tr>
				<td colspan="5" align="left"><h2>&nbsp;</h2></td>
			</tr>
		</table>
	</div>
	</div>
	</div>
	</div>
	
	<script type="text/javascript">

	debugger
	const dateFromm = '<%=(String)request.getAttribute("dateFrom")%>';
	if(dateFromm!='null'){
		 $("#dateFrom").val(dateFromm);
	}
	</script>
	<!--begin::Global Javascript Bundle(used by all pages)-->
<%-- <script src="../assets/plugins/global/plugins.bundle.js"></script>
<script src="../assets/js/scripts.bundle.js"></script> --%>
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
<!--end::Custom Javascript-->
<!--end::Javascript-->
<script>
	$("#kt_datepicker_1").flatpickr({
			maxDate: new Date(),
			dateFormat: "Y-m-d",
			defaultDate: "today"
		});
	$("#kt_datepicker_2").flatpickr({
			maxDate: new Date(),
			dateFormat: "Y-m-d",
			defaultDate: "today"
		});
	// $("#kt_datatable_vertical_scroll").DataTable({
	// 	scrollY: true,
	// 	scrollX: true,
		
	// });

</script>	
</body>
</html>