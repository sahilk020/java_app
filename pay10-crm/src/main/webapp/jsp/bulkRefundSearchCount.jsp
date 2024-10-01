
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="/struts-tags" prefix="s"%>
<html dir="ltr" lang="en-US">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>Bulk Refund Summary Report</title>
		
		
		<link rel="shortcut icon" href="assets/media/images/paylogo.svg" />
			<!--begin::Fonts-->
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
<link
				href="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.css"
				rel="stylesheet" type="text/css" />
			<link href="../assets/plugins/custom/datatables/datatables.bundle.css"
				rel="stylesheet" type="text/css" />
<script
	src="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.js"></script>
<script src="../assets/plugins/custom/datatables/datatables.bundle.js"></script>
<script src="../assets/js/widgets.bundle.js"></script>
<script src="../assets/js/custom/widgets.js"></script>
<script src="../assets/js/custom/apps/chat/chat.js"></script>
<script src="../assets/js/custom/utilities/modals/upgrade-plan.js"></script>
<script src="../assets/js/custom/utilities/modals/create-app.js"></script>
				<script src="../js/bulkRefundSearchCount.js"></script>

<script src="../assets/js/custom/utilities/modals/users-search.js"></script>
			<link href="../css/select2.min.css" rel="stylesheet" />
			<script src="../js/jquery.select2.js" type="text/javascript"></script>
						<script src="../js/bulkRefundSearchCount.js"></script>
		
				<script type="text/javascript" src="../js/sweetalert.js"></script>
				<link rel="stylesheet" href="../css/sweetalert.css">
		
		
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
		<script type="text/javascript">
$(document).ready(function() {
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

});
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
					<div data-kt-swapper="true" data-kt-swapper-mode="prepend"
						data-kt-swapper-parent="{default: '#kt_content_container', 'lg': '#kt_toolbar_container'}"
						class="page-title d-flex align-items-center me-3 flex-wrap mb-5 mb-lg-0 lh-1">
						<!--begin::Title-->
						<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3"> Bulk Refund Summary Report </h1>
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
							<li class="breadcrumb-item text-muted">Account & Finance</li>
							<!--end::Item-->
							<!--begin::Item-->
							<li class="breadcrumb-item"><span class="bullet bg-gray-200 w-5px h-2px"></span>
							</li>
							<!--end::Item-->
							<!--begin::Item-->
							<li class="breadcrumb-item text-dark"> Bulk Refund Summary Report</li>
							<!--end::Item-->
						</ul>
						<!--end::Breadcrumb-->
					</div>
					<!--end::Page title-->

				</div>
				<!--end::Container-->
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
								<label class="d-flex align-items-center fs-6 fw-bold mb-2">
												<span class="">Refund From</span>
											</label>
											<!--end::Label-->
											<div class="position-relative d-flex align-items-center">
												<!--begin::Icon-->
												<!--begin::Svg Icon | path: icons/duotune/general/gen014.svg-->
												<span class="svg-icon svg-icon-2 position-absolute mx-4">
													<svg width="24" height="24" viewBox="0 0 24 24" fill="none"
														xmlns="http://www.w3.org/2000/svg"> <path
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
													placeholder="Select a date" name="dateFrom"
													id="kt_datepicker_1" />
												<!--end::Datepicker-->
											</div>

										</div>
									
								<div class="col-md-4 fv-row">
											<label class="d-flex align-items-center fs-6 fw-bold mb-2">
												<span class="">Refund To</span>
											</label>
											<!--end::Label-->
											<div class="position-relative d-flex align-items-center">
												<!--begin::Icon-->
												<!--begin::Svg Icon | path: icons/duotune/general/gen014.svg-->
												<span class="svg-icon svg-icon-2 position-absolute mx-4">
													<svg width="24" height="24" viewBox="0 0 24 24" fill="none"
														xmlns="http://www.w3.org/2000/svg"> <path
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
													placeholder="Select a date" name="dateTo"
													id="kt_datepicker_2">
												<!--end::Datepicker-->
											</div>
									

										</div>
										
										<div class="col-md-4 ">
											<label class="d-flex align-items-center fs-6 fw-bold mb-2">
												<span class="">File Name</span>
											</label>
											<!--end::Label-->
											<div class="position-relative d-flex align-items-center">
												<!--begin::Icon-->
												<!--begin::Svg Icon | path: icons/duotune/general/gen014.svg-->
												<input type="text"
														class="form-control form-control-solid" id="Filename"
														name="Filename" 
														
														 />
											
											</div>
									

										</div>
										
						         	<br>										
							<div class="col-md-4 fv-row">
								<div class="txtnew">
									<input type="button" id="submit" value="Search" onClick="searchdata()"
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
		
						<br /> <br />
						<div class="scrollD">
							<table id="datatable" class="table table-striped table-row-bordered gy-5 gs-7" cellspacing="0" width="100%">
								<thead>
									<tr class="fw-bold fs-6 text-gray-800">
									    <th> FileName </th>
										<th>Success Transaction</th>
										<th>Failed Transaction</th>
										<th>Pending Transaction</th>	
									<th>Total Transaction</th>																							
																																
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
		

			<script src="../assets/plugins/custom/datatables/datatables.bundle.js"></script>
	</body>
</html>
