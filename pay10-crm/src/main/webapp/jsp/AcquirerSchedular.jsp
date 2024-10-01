
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="/struts-tags" prefix="s"%>
<html dir="ltr" lang="en-US">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>Acquirer Schedular Details</title>
		
		
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
		
				<script src="../js/AcquirerSchadular.js"></script>
				<script type="text/javascript" src="../js/sweetalert.js"></script>
				<link rel="stylesheet" href="../css/sweetalert.css">
		
		
		
		
		
		<%-- <link href="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.css" rel="stylesheet" type="text/css" />
<link href="../assets/plugins/custom/datatables/datatables.bundle.css" rel="stylesheet" type="text/css" />
<link href="../assets/plugins/global/plugins.bundle.css" rel="stylesheet" type="text/css" />
<link href="../assets/css/custom/custom-style.css" rel="stylesheet" type="text/css" />
		<link href="../assets/css/style.bundle.css" rel="stylesheet" type="text/css" />
		
		<script src="../js/jquery.minshowpop.js"></script>
		<script src="../js/jquery.formshowpop.js"></script>
		<script src="../js/commanValidate.js"></script>
		<link rel="stylesheet" type="text/css" media="all"
			href="../css/daterangepicker-bs3.css" />
		<link href="../css/jquery-ui.css" rel="stylesheet" />
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
		<script type="text/javascript" src="../js/sweetalert.js"></script>
<link rel="stylesheet" href="../css/sweetalert.css">
		<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/limonte-sweetalert2/6.6.9/sweetalert2.min.css">
  <script src="https://cdnjs.cloudflare.com/ajax/libs/limonte-sweetalert2/6.6.9/sweetalert2.min.js"></script>
		<!--  loader scripts -->
		<script src="../js/loader/modernizr-2.6.2.min.js"></script>
			<script src="../js/loader/main.js"></script>
		<link rel="stylesheet" href="../css/loader/normalize.css" />
		<!-- <link rel="stylesheet" href="../css/loader/main.css" /> -->
		<link rel="stylesheet" href="../css/loader/customLoader.css" />
		<link href="../css/select2.min.css" rel="stylesheet" />
		<script src="../js/jquery.select2.js" type="text/javascript"></script>
		<script src="../js/AcquirerSchadular.js"></script>
		<script src="../assets/js/scripts.bundle.js"></script> --%>
		<style type="text/css">
			/* .cust {width: 24%!important; margin:0 5px !important; 
			} */
			.samefnew{
			width: 19.5%!important;
			margin: 5px 5px !important;
		
			.samefnew-btn{
			width: 12%;
			float: left;
			font: bold 11px arial;
			color: #333;
			line-height: 22px;
			margin-top: 5px;
			}
			/*tr td.my_class{color:#000 !important; cursor: default !important; text-decoration: none;}*/
			
			.cust .form-control, .samefnew .form-control{
			margin:0px !important;
			width: 95%;
			}
			.select2-container{
			width: 100% !important;
			}
			.clearfix:after{
			display: block;
			visibility: hidden;
			line-height: 0;
			height: 0;
			clear: both;
			content: '.';
			}
			#popup{
			position: fixed;
			top:0px;
			left: 0px;
			background: rgba(0,0,0,0.7);
			width: 100%;
			height: 100%;
			z-index:999; 
			display: none;
			}
			.innerpopupDv{
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
			#loader-wrapper .loader-section.section-left, #loader-wrapper .loader-section.section-right{
			background: rgba(225,225,225,0.6) !important;
			width: 50% !important;
			}
			.invoicetable{
			float: none;
			}
			.innerpopupDv h2{
			font-size: 12px;
			padding: 5px;
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
			.text-class{
			text-align: center !important;
			}
			.download-btn {
			background-color:#496cb6;
			display: block;
			width: 100%;
			height: 30px;
			padding: 3px 4px;
			font-size: 14px;
			line-height: 1.42857143;
			color: #fff;
			border: 1px solid #ccc;
			border-radius: 4px;
			margin-top:30px;
			}
			
			#loading {width: 100%;height: 100%;top: 0px;left: 0px;position: fixed;display: block; z-index: 99}
			#loading-image {position: absolute;top: 40%;left: 55%;z-index: 100; width:10%;} 
			#loadingInner {width: 100%;height: 100%;top: 0px;left: 0px;position: fixed;display: block; z-index: 99}
			#loading-image-inner {position: absolute;top: 33%;left: 48%;z-index: 100; width:5%;} 
		</style>
		<script>
		function searchdata(){
			
		
			 var acquirer = document.getElementById("acquirer3").value;
		    $.ajax({

		        type: "GET",
		        url: "serachDetails",

		        data: {
		        	
		"acquirer":acquirer,
		        },
		        success: function(response) {
		          var responseObj = JSON.parse(JSON.stringify(response));
		       //   renderTable(responseObj.searchSchadular);
		        },
		      });
		}
		</script>
	</head>
	
	<body id="mainBody" class="header-fixed header-tablet-and-mobile-fixed toolbar-enabled toolbar-fixed toolbar-tablet-and-mobile-fixed aside-enabled aside-fixed">
	<div class="content d-flex flex-column flex-column-fluid" id="kt_content">
	 <div id="loader-wrapper" style="width: 100%; height: 100%; display:none;">			
	 </div>	
	   <div class="toolbar" id="kt_toolbar">
			<!--begin::Container-->
			<div id="kt_toolbar_container" class="container-fluid d-flex flex-stack">
				<!--begin::Page title-->
				<div data-kt-swapper="true" data-kt-swapper-mode="prepend" data-kt-swapper-parent="{default: '#kt_content_container', 'lg': '#kt_toolbar_container'}" class="page-title d-flex align-items-center me-3 flex-wrap mb-5 mb-lg-0 lh-1">
					<!--begin::Title-->
					<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">Acquirer Wise Scheduler</h1>
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
						<li class="breadcrumb-item text-dark">Acquirer Wise Scheduler</li>
						<!--end::Item-->
					</ul>
					<!--end::Breadcrumb-->
				</div>
				<!--end::Page title-->
				
			</div>
			<!--end::Container-->
		</div>
		<div class="modal" id="data-add" tabindex="-1" role="dialog"
			aria-labelledby="myModalLabel" aria-hidden="true">			
			 <div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header" align="center"></div>
					<div class="modal-body">
						Please Add the Record for Acquirer.
						<table class="table">
							<tr>						
								<th>Acquirer</th>
								<td id="batchNoTd">
								<td id="batchaccept"><s:select   name="acquirer"
									
									class="form-select form-select-solid" id="acquirer"
								    headerKey=""
									headerValue="Select Acquirer"
									list="@com.pay10.commons.util.AcquirerTypeUI@values()"
									listKey="name" listValue="name"  autocomplete="off"/></td>
								</td>
							</tr>							
							<tr>
								<th>Start Time</th>
								<td id="resellerIdTd">
								<td id="resellerIdaccept"><input id="Maxtime"
								onpaste="return false;"	 class="form-control form-control-solid" autocomplete="off" placeholder="Enter the time"
									type="text" maxlength="5"  onkeypress="return event.charCode >= 48 && event.charCode <= 57" name="resellerId"></td>
								</td>
							</tr>
							<tr>
								<th>Max Time</th>
								<td id="resellerIdTd">
								<td id="resellerIdaccept"><input id="Mintime" placeholder="Enter the time"
									onpaste="return false;" class="form-control form-control-solid" autocomplete="off"
									 type="text" maxlength="5"  onkeypress="return event.charCode >= 48 && event.charCode <= 57" name="resellerId"></td>
								</td>
							</tr>
							<tr>								
							<td hidden id="resellerIdaccept"><input id="id"
								 class="input-control " autocomplete="off"
								type="text" name="resellerId"></td>								
							</tr>
						</table>
					</div>
					<div align="center">
						<button type="button" class="btn btn-primary"
							id="btnPayoutConf1" onClick="save()">Submit</button>
						<button type="button" class="btn  btn-danger"
							id="btnPayoutConf3" data-dismiss="modal" onclick="closePopup();">Cancel</button>
					</div>
				</div>
			</div>
		</div>
		<div class="modal" id="payoutAccept" tabindex="-1" role="dialog"
			aria-labelledby="myModalLabel" aria-hidden="true">			
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header" align="center"></div>
					<div class="modal-body">
						Please Update the details.
						<table class="table">
							<tr>
								<th>Acquirer</th>
								<td id="batchNoTd">
								<td id="batchaccept">
								<s:select name="acquirer"
									
									class="form-select form-select-solid" id="acquirer1" 
									headerKey=""
									headerValue="Select Acquirer"
									list="@com.pay10.commons.util.AcquirerTypeUI@values()"
									listKey="name" listValue="name"  autocomplete="off"/>
								</td>
								</td>
							</tr>
							
							<tr>
								<th>Start Time</th>
								<td id="resellerIdTd">
								<td id="resellerIdaccept"><input id="Maxtime1"
								onpaste="return false;"	class="form-control form-control-solid" autocomplete="off"
									type="text" maxlength="5"  onkeypress="return event.charCode >= 48 && event.charCode <= 57" name="resellerId"></td>
								</td>
							</tr>
							<tr>
								<th>Max Time</th>
								<td id="resellerIdTd">
								<td id="resellerIdaccept"><input id="Mintime1"
								onpaste="return false;"	 class="form-control form-control-solid" autocomplete="off"
									 type="text" maxlength="5"  onkeypress="return event.charCode >= 48 && event.charCode <= 57" name="resellerId"></td>
								</td>
							</tr>
							<tr>								
							<td hidden id="resellerIdaccept">
							<input id="id"
							       class="input-control " autocomplete="off"/>
							</td>								
							</tr>
						</table>
					</div>
					<div align="center">
					<button type="button" class="btn btn-primary"
							id="btnPayoutConf1" onClick="save1()">Submit</button>
						<button type="button" class="btn btn-danger"
							id="btnPayoutConf" data-dismiss="modal" onclick="closePopup1();">Cancel</button>
					</div>
				</div>
			</div>
		</div>
	<div class="post d-flex flex-column-fluid" id="kt_post">
	<div id="kt_content_container" class="container-xxl">
	
	
	  <div class="row my-5">
       <div class="col">	
		<div class="card ">
			<div class="card-body ">
				<div class="container">
					<div class="row">
						<s:form id="resellerPayoutForm">					
							<div class="col-md-4 fv-row">
								<label	class="d-flex align-items-center fs-6 fw-bold mb-2">
									<span class="">Acquirer :</span>
										</label>
											<!--end::Label-->
											<s:select headerKey="" headerValue="Select Acquirer"
											data-control="select2"
											class="form-select form-select-solid"
											list="@com.pay10.commons.util.AcquirerTypeUI@values()"
											listValue="name" listKey="name" name="acquirer"
											id="acquirer3" autocomplete="off" value="" />

										</div>
						         	<br>										
							<div class="col-md-4 fv-row">
								<div class="txtnew">
									<input type="button" id="submit" value="Search" onClick="searchdata()"
										class="btn btn-primary" />
									<input type="button" id="submitBtn" value="Add" onclick="openPopUp();" 
										class="btn btn-primary" />
								</div>
							</div>
						</s:form>
					</div>
				</div>
			</div>
		</div>
		</div>
		</div>
		
		<div class="row my-5">
       <div class="col">
        <div class="card">
        <div class="card-body">
		<div style="overflow: scroll !important;">
		<!-- <table width="100%" align="left" cellpadding="0" cellspacing="0"
				class="formbox table table-striped table-row-bordered gy-5 gs-7">
				  <tr>
					<td align="left" style="padding: 10px;"> -->
						<br /> <br />
						<div class="scrollD">
							<table id="datatable" class="table table-striped table-row-bordered gy-5 gs-7" cellspacing="0" width="100%">
								<thead>
									<tr class="fw-bold fs-6 text-gray-800">
										<th>Acquirer</th>
										<th> id </th>
										<th>Start Time (in Min)</th>
										<th>End Time (in Min)</th>
										<th>Edit</th>
										<th>Delete</th>
										<th>
										</th>										
									</tr>
								</thead>
							</table>
						</div>
					<!-- </td>
				</tr>
			</table> -->
		</div>
		</div>
		</div>
		</div>
		</div>
		</div>
		</div>
		</div>
		
<%-- 		
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
<script src="https://cdn.amcharts.com/lib/5/geodata/worldTimeZoneAreasLow.js"></script> --%>
		<%-- <script>
		function searchdata(){
		
			 var acquirer = document.getElementById("acquirer3").value;
		    $.ajax({

		        type: "GET",
		        url: "serachDetails",

		        data: {
		        	
		"acquirer":acquirer,
		        },
		        success: function(response) {
		          var responseObj = JSON.parse(JSON.stringify(response));
		          renderTable(responseObj.searchSchadular);
		        },
		      });
		}
		</script> --%>
			<script src="../assets/plugins/custom/datatables/datatables.bundle.js"></script>
	</body>
</html>
