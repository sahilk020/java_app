<%@page import="com.pay10.commons.util.Constants"%>
<%@page import="com.pay10.commons.user.User"%>
<%@page import="com.pay10.crm.actionBeans.TransactionStatusBean"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="/struts-tags" prefix="s"%>
<html dir="ltr" lang="en-US">
<% User sessionUser = (User) session.getAttribute(Constants.USER.getValue()); %>
<head>
<title>Create Plan</title>
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
<link href="../css/select2.min.css" rel="stylesheet" />
<script src="../js/jquery.select2.js" type="text/javascript"></script>
<style type="text/css">
.dt-buttons.btn-group.flex-wrap {
	display: none;
}

.error{
color: red
}

#txnResultDataTable thead th{
	font-weight: bold;
	
}
</style>
<script type="text/javascript">
	$(document).ready(function() {
		$(".adminMerchants").select2();

	});
</script>



</head>

<body id="kt_body"
	class="header-fixed header-tablet-and-mobile-fixed toolbar-enabled toolbar-fixed toolbar-tablet-and-mobile-fixed aside-enabled aside-fixed"
	style="-kt-toolbar-height: 55px; - -kt-toolbar-height-tablet-and-mobile: 55px">
	<div class="content d-flex flex-column flex-column-fluid"
		id="kt_content">
		<div class="toolbar" id="kt_toolbar">
			<!--begin::Container-->
			<div id="kt_toolbar_container"
				class="container-fluid d-flex flex-stack">
				<div data-kt-swapper="true" data-kt-swapper-mode="prepend"
					data-kt-swapper-parent="{default: '#kt_content_container', 'lg': '#kt_toolbar_container'}"
					class="page-title d-flex align-items-center me-3 flex-wrap mb-5 mb-lg-0 lh-1">
					<!--begin::Title-->
					<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">
						Create Plan</h1>
					<!--end::Title-->
					<!--begin::Separator-->
					<span class="h-20px border-gray-200 border-start mx-4"></span>
					<!--end::Separator-->
					<!--begin::Breadcrumb-->
					<ul
						class="breadcrumb breadcrumb-separatorless fw-semibold fs-7 my-1">
						<!--begin::Item-->
						<li class="breadcrumb-item text-muted"><a href="home"
							class="text-muted text-hover-primary">SI</a></li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item"><span
							class="bullet bg-gray-200 w-5px h-2px"></span></li>
						<!--end::Item-->
						<!--begin::Item-->
						<!-- 						<li class="breadcrumb-item text-muted">Reports</li> -->
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item"><span
							class="bullet bg-gray-200 w-5px h-2px"></span></li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item text-dark">Create Plan</li>
						<!--end::Item-->
					</ul>
					<!--end::Breadcrumb-->
				</div>
				<!--end::Page title-->

			</div>
		</div>
		

		<div class="post d-flex flex-column-fluid" id="kt_post">
			<!--begin::Container-->
			<div id="kt_content_container" class="container-xxl">
				<div class="row my-5">
					<div class="col">
						<div class="card">
							<div class="card-body">

								<div class="row my-3 align-items-center">
									<div class="col-lg-4 my-2">
										<label class="d-flex align-items-center fs-6 fw-bold mb-2">
											<span class="required">Merchant</span>
										</label>
										<s:if
														test="%{#session.USER.UserType.name()=='ADMIN' || #session.USER.UserType.name()=='SUBADMIN' || #session.USER_TYPE.name()=='SUPERADMIN'|| #session.USER_TYPE.name()=='RESELLER'}">
										<s:select name="merchant"
											class="form-select form-select-solid adminMerchants"
											id="merchant" headerKey=""
											headerValue="Please Select Merchant" list="merchantList"
											listKey="payId" listValue="businessName" autocomplete="off" />
											<span id="merchantError" class="error"></span>
											</s:if>
											
										<s:else>
										<s:select name="merchant"
											class="form-select form-select-solid adminMerchants"
											id="merchant" list="merchantList"
											listKey="payId" listValue="businessName" autocomplete="off" />
											<span id="merchantError" class="error"></span>
										</s:else>
									</div>

									<div class="col-lg-4 my-2">
										<label class="d-flex align-items-center fs-6 fw-bold mb-2">
											<span class="required">Plan Name</span>
										</label>
										<s:textfield id="planName"
											class="form-control form-control-solid" name="planName"
											type="text" value="" autocomplete="off" ></s:textfield>
											<span id='planError' class='error'></span>
									</div>

									<div class=" col-lg-4 my-2">
										<label class="d-flex align-items-center fs-6 fw-bold mb-2">
											<span class="required">Plan Description</span>
										</label>
										<s:textfield id="planDescription"
											class="form-control form-control-solid"
											name="planDescription" type="text" value=""
											autocomplete="off"></s:textfield>
											<span id='planDescriptionError' class='error'></span>
									</div>


								</div>






								<!-- 									<div class="col-lg-4 my-2"> -->
								<!-- 										<label class="d-flex align-items-center fs-6 fw-bold mb-2"> -->
								<%-- 											<span class="required">Period</span> --%>
								<!-- 										</label> -->
								<%-- 										<s:textfield id="period" --%>
								<%-- 											class="form-control form-control-solid" name="period" --%>
								<%-- 											type="text" value="" autocomplete="off"></s:textfield> --%>
								<!-- 									</div> -->

								<!-- 									<div class=" col-lg-4 my-2"> -->
								<!-- 										<label class="d-flex align-items-center fs-6 fw-bold mb-2"> -->
								<%-- 											<span class="required">Interval</span> --%>
								<!-- 										</label> -->
								<%-- 										<s:textfield id="interval" --%>
								<%-- 											class="form-control form-control-solid" name="interval" --%>
								<%-- 											type="text" value="" autocomplete="off"></s:textfield> --%>
								<!-- 									</div> -->

								<div class=" row my-3 align-items-center">
									<div class="col-lg-4 my-2">
										<label class="d-flex align-items-center fs-6 fw-bold mb-2">
											<span class="required">Type</span>
										</label>
										<s:textfield id="type" class="form-control form-control-solid"
											name="type" type="text" value="" autocomplete="off"></s:textfield>
											<span id='typeError' class='error'></span>
									</div>

									<div class="col-lg-4 my-2">
										<label class="d-flex align-items-center fs-6 fw-bold mb-2">
											<span class="required">Unit</span>
										</label>
										<s:textfield id="unit" class="form-control form-control-solid"
											name="unit" type="text" value="" autocomplete="off"></s:textfield>
											<span id='unitError' class='error'></span>
									</div>

									
									<div class="col-lg-4 my-2">
										<label class="d-flex align-items-center fs-6 fw-bold mb-2">
											<span class="required">Unit Amount</span>
										</label>
										<s:textfield id="unitAmount"
											class="form-control form-control-solid" name="unitAmount"
											type="text" value="" autocomplete="off"></s:textfield>
											<span id='unitamountError' class='error'></span>
									</div>
								
	</div>
							
								
									
								

								<div class=" row my-3 align-items-center">

									<div class=" col-lg-4 my-2">
										<label class="d-flex align-items-center fs-6 fw-bold mb-2">
											<span class="required">Create Date</span>
										</label> <span class="svg-icon svg-icon-2 position-absolute mx-4">
											<svg width="24" height="24" viewBox="0 0 24 24" fill="none"
												xmlns="http://www.w3.org/2000/svg"> <path
												opacity="0.3"
												d="M21 22H3C2.4 22 2 21.6 2 21V5C2 4.4 2.4 4 3 4H21C21.6 4 22 4.4 22 5V21C22 21.6 21.6 22 21 22Z"
												fill="currentColor"></path> <path
												d="M6 6C5.4 6 5 5.6 5 5V3C5 2.4 5.4 2 6 2C6.6 2 7 2.4 7 3V5C7 5.6 6.6 6 6 6ZM11 5V3C11 2.4 10.6 2 10 2C9.4 2 9 2.4 9 3V5C9 5.6 9.4 6 10 6C10.6 6 11 5.6 11 5ZM15 5V3C15 2.4 14.6 2 14 2C13.4 2 13 2.4 13 3V5C13 5.6 13.4 6 14 6C14.6 6 15 5.6 15 5ZM19 5V3C19 2.4 18.6 2 18 2C17.4 2 17 2.4 17 3V5C17 5.6 17.4 6 18 6C18.6 6 19 5.6 19 5Z"
												fill="currentColor"></path> <path
												d="M8.8 13.1C9.2 13.1 9.5 13 9.7 12.8C9.9 12.6 10.1 12.3 10.1 11.9C10.1 11.6 10 11.3 9.8 11.1C9.6 10.9 9.3 10.8 9 10.8C8.8 10.8 8.59999 10.8 8.39999 10.9C8.19999 11 8.1 11.1 8 11.2C7.9 11.3 7.8 11.4 7.7 11.6C7.6 11.8 7.5 11.9 7.5 12.1C7.5 12.2 7.4 12.2 7.3 12.3C7.2 12.4 7.09999 12.4 6.89999 12.4C6.69999 12.4 6.6 12.3 6.5 12.2C6.4 12.1 6.3 11.9 6.3 11.7C6.3 11.5 6.4 11.3 6.5 11.1C6.6 10.9 6.8 10.7 7 10.5C7.2 10.3 7.49999 10.1 7.89999 10C8.29999 9.90003 8.60001 9.80003 9.10001 9.80003C9.50001 9.80003 9.80001 9.90003 10.1 10C10.4 10.1 10.7 10.3 10.9 10.4C11.1 10.5 11.3 10.8 11.4 11.1C11.5 11.4 11.6 11.6 11.6 11.9C11.6 12.3 11.5 12.6 11.3 12.9C11.1 13.2 10.9 13.5 10.6 13.7C10.9 13.9 11.2 14.1 11.4 14.3C11.6 14.5 11.8 14.7 11.9 15C12 15.3 12.1 15.5 12.1 15.8C12.1 16.2 12 16.5 11.9 16.8C11.8 17.1 11.5 17.4 11.3 17.7C11.1 18 10.7 18.2 10.3 18.3C9.9 18.4 9.5 18.5 9 18.5C8.5 18.5 8.1 18.4 7.7 18.2C7.3 18 7 17.8 6.8 17.6C6.6 17.4 6.4 17.1 6.3 16.8C6.2 16.5 6.10001 16.3 6.10001 16.1C6.10001 15.9 6.2 15.7 6.3 15.6C6.4 15.5 6.6 15.4 6.8 15.4C6.9 15.4 7.00001 15.4 7.10001 15.5C7.20001 15.6 7.3 15.6 7.3 15.7C7.5 16.2 7.7 16.6 8 16.9C8.3 17.2 8.6 17.3 9 17.3C9.2 17.3 9.5 17.2 9.7 17.1C9.9 17 10.1 16.8 10.3 16.6C10.5 16.4 10.5 16.1 10.5 15.8C10.5 15.3 10.4 15 10.1 14.7C9.80001 14.4 9.50001 14.3 9.10001 14.3C9.00001 14.3 8.9 14.3 8.7 14.3C8.5 14.3 8.39999 14.3 8.39999 14.3C8.19999 14.3 7.99999 14.2 7.89999 14.1C7.79999 14 7.7 13.8 7.7 13.7C7.7 13.5 7.79999 13.4 7.89999 13.2C7.99999 13 8.2 13 8.5 13H8.8V13.1ZM15.3 17.5V12.2C14.3 13 13.6 13.3 13.3 13.3C13.1 13.3 13 13.2 12.9 13.1C12.8 13 12.7 12.8 12.7 12.6C12.7 12.4 12.8 12.3 12.9 12.2C13 12.1 13.2 12 13.6 11.8C14.1 11.6 14.5 11.3 14.7 11.1C14.9 10.9 15.2 10.6 15.5 10.3C15.8 10 15.9 9.80003 15.9 9.70003C15.9 9.60003 16.1 9.60004 16.3 9.60004C16.5 9.60004 16.7 9.70003 16.8 9.80003C16.9 9.90003 17 10.2 17 10.5V17.2C17 18 16.7 18.4 16.2 18.4C16 18.4 15.8 18.3 15.6 18.2C15.4 18.1 15.3 17.8 15.3 17.5Z"
												fill="currentColor"></path> </svg>
										</span>
										<!--end::Svg Icon-->
										<!--end::Icon-->
										<!--begin::Datepicker-->
										<input
											class="form-control form-control-solid ps-12 flatpickr-input"
											placeholder="Select a date" name=createAt " id="createAt"
											type="text">
										
										<!--end::Datepicker-->
											
									</div>
									<div class="col-lg-4 my-2">
										<label class="d-flex align-items-center fs-6 fw-bold mb-2">
											<span class="required">Update Date</span>
										</label> <span class="svg-icon svg-icon-2 position-absolute mx-4">
											<svg width="24" height="24" viewBox="0 0 24 24" fill="none"
												xmlns="http://www.w3.org/2000/svg"> <path
												opacity="0.3"
												d="M21 22H3C2.4 22 2 21.6 2 21V5C2 4.4 2.4 4 3 4H21C21.6 4 22 4.4 22 5V21C22 21.6 21.6 22 21 22Z"
												fill="currentColor"></path> <path
												d="M6 6C5.4 6 5 5.6 5 5V3C5 2.4 5.4 2 6 2C6.6 2 7 2.4 7 3V5C7 5.6 6.6 6 6 6ZM11 5V3C11 2.4 10.6 2 10 2C9.4 2 9 2.4 9 3V5C9 5.6 9.4 6 10 6C10.6 6 11 5.6 11 5ZM15 5V3C15 2.4 14.6 2 14 2C13.4 2 13 2.4 13 3V5C13 5.6 13.4 6 14 6C14.6 6 15 5.6 15 5ZM19 5V3C19 2.4 18.6 2 18 2C17.4 2 17 2.4 17 3V5C17 5.6 17.4 6 18 6C18.6 6 19 5.6 19 5Z"
												fill="currentColor"></path> <path
												d="M8.8 13.1C9.2 13.1 9.5 13 9.7 12.8C9.9 12.6 10.1 12.3 10.1 11.9C10.1 11.6 10 11.3 9.8 11.1C9.6 10.9 9.3 10.8 9 10.8C8.8 10.8 8.59999 10.8 8.39999 10.9C8.19999 11 8.1 11.1 8 11.2C7.9 11.3 7.8 11.4 7.7 11.6C7.6 11.8 7.5 11.9 7.5 12.1C7.5 12.2 7.4 12.2 7.3 12.3C7.2 12.4 7.09999 12.4 6.89999 12.4C6.69999 12.4 6.6 12.3 6.5 12.2C6.4 12.1 6.3 11.9 6.3 11.7C6.3 11.5 6.4 11.3 6.5 11.1C6.6 10.9 6.8 10.7 7 10.5C7.2 10.3 7.49999 10.1 7.89999 10C8.29999 9.90003 8.60001 9.80003 9.10001 9.80003C9.50001 9.80003 9.80001 9.90003 10.1 10C10.4 10.1 10.7 10.3 10.9 10.4C11.1 10.5 11.3 10.8 11.4 11.1C11.5 11.4 11.6 11.6 11.6 11.9C11.6 12.3 11.5 12.6 11.3 12.9C11.1 13.2 10.9 13.5 10.6 13.7C10.9 13.9 11.2 14.1 11.4 14.3C11.6 14.5 11.8 14.7 11.9 15C12 15.3 12.1 15.5 12.1 15.8C12.1 16.2 12 16.5 11.9 16.8C11.8 17.1 11.5 17.4 11.3 17.7C11.1 18 10.7 18.2 10.3 18.3C9.9 18.4 9.5 18.5 9 18.5C8.5 18.5 8.1 18.4 7.7 18.2C7.3 18 7 17.8 6.8 17.6C6.6 17.4 6.4 17.1 6.3 16.8C6.2 16.5 6.10001 16.3 6.10001 16.1C6.10001 15.9 6.2 15.7 6.3 15.6C6.4 15.5 6.6 15.4 6.8 15.4C6.9 15.4 7.00001 15.4 7.10001 15.5C7.20001 15.6 7.3 15.6 7.3 15.7C7.5 16.2 7.7 16.6 8 16.9C8.3 17.2 8.6 17.3 9 17.3C9.2 17.3 9.5 17.2 9.7 17.1C9.9 17 10.1 16.8 10.3 16.6C10.5 16.4 10.5 16.1 10.5 15.8C10.5 15.3 10.4 15 10.1 14.7C9.80001 14.4 9.50001 14.3 9.10001 14.3C9.00001 14.3 8.9 14.3 8.7 14.3C8.5 14.3 8.39999 14.3 8.39999 14.3C8.19999 14.3 7.99999 14.2 7.89999 14.1C7.79999 14 7.7 13.8 7.7 13.7C7.7 13.5 7.79999 13.4 7.89999 13.2C7.99999 13 8.2 13 8.5 13H8.8V13.1ZM15.3 17.5V12.2C14.3 13 13.6 13.3 13.3 13.3C13.1 13.3 13 13.2 12.9 13.1C12.8 13 12.7 12.8 12.7 12.6C12.7 12.4 12.8 12.3 12.9 12.2C13 12.1 13.2 12 13.6 11.8C14.1 11.6 14.5 11.3 14.7 11.1C14.9 10.9 15.2 10.6 15.5 10.3C15.8 10 15.9 9.80003 15.9 9.70003C15.9 9.60003 16.1 9.60004 16.3 9.60004C16.5 9.60004 16.7 9.70003 16.8 9.80003C16.9 9.90003 17 10.2 17 10.5V17.2C17 18 16.7 18.4 16.2 18.4C16 18.4 15.8 18.3 15.6 18.2C15.4 18.1 15.3 17.8 15.3 17.5Z"
												fill="currentColor"></path> </svg>
										</span>
										<!--end::Svg Icon-->
										<!--end::Icon-->
										<!--begin::Datepicker-->
										<input
											class="form-control form-control-solid ps-12 flatpickr-input"
											placeholder="Select a date" name=updateAt " id="updateAt"
											type="text">
										<!--end::Datepicker-->
										
									</div>
									<div class=" col-lg-4 my-2">
										<label class="d-flex align-items-center fs-6 fw-bold mb-2">
											<span class="required">Amount</span>
										</label>
										<s:textfield id="amount"
											class="form-control form-control-solid" name="amount"
											type="text" value="" autocomplete="off"></s:textfield>
											<span id='amountError' class='error'></span>
											
									</div>
									</div>
								
									
							


								<div class=" row my-3 align-items-center">
									<div class=" col-lg-4 my-2">
										<label class="d-flex align-items-center fs-6 fw-bold mb-2">
											<span class="required">Additional Parameter 1</span>
										</label>
										<s:textfield id="ap1" class="form-control form-control-solid"
											name="ap1" type="text" value="" autocomplete="off"></s:textfield>
											<span id='ap1Error' class='error'></span>
									</div>


									<div class=" col-lg-4 my-2">
										<label class="d-flex align-items-center fs-6 fw-bold mb-2">
											<span class="required">Additional Parameter 2</span>
										</label>
										<s:textfield id="ap2" class="form-control form-control-solid"
											name="ap2" type="text" value="" autocomplete="off"></s:textfield>
											<span id='ap2Error' class='error'></span>
									</div>

									
									
									<div class=" col-lg-4 my-2">
										<label class="d-flex align-items-center fs-6 fw-bold mb-2">
											<span class="required">Additional Parameter 3</span>
										</label>
										<s:textfield id="ap3" class="form-control form-control-solid"
											name="ap3" type="text" value="" autocomplete="off"></s:textfield>
											<span id='ap3Error' class='error'></span>
									</div>


								</div>
								<div class=" row my-3 align-items-center">
								<div class="col-lg-4 my-2">
										<label class="d-flex align-items-center fs-6 fw-bold mb-2">
											<span class="required">Currency</span>
										</label>
										<s:textfield id="currency"
											class="form-control form-control-solid" name="currency"
											type="text" value="INR" autocomplete="off"></s:textfield>
											<span id='currencyError' class='error'></span>
									</div>
							
								

									



								<div style="text-align: end;">
									<button type="button" class="btn btn-primary" id="done"
										onclick="save()">Save</button>
								</div>
							</div>
						</div>
					</div>
				</div>

				<div class="card">
					<div class="card-body ">

						<div class="row my-5 mt-4">
							<div class="col">
								<div class="card">
									<div class="card-body">
										<!--begin::Input group-->
										<div class="row g-9 mb-8 justify-content-end">
											<div class="col-lg-4 col-sm-12 col-md-6">
												<select name="currency" data-control="select2"
													data-placeholder="Actions" id="actions11"
													class="form-select form-select-solid actions"
													data-hide-search="true" onchange="myFunction();">
													<option value="">Actions</option>
													<option value="copy">Copy</option>
													<option value="csv">CSV</option>
													<option value="pdf">PDF</option>
												</select>
											</div>
											<div class="col-lg-4 col-sm-12 col-md-6">
												<div class="dropdown1">
													<button
														class="form-select form-select-solid actions dropbtn1">Customize
														Columns</button>
													<div class="dropdown-content1">
														<a class="toggle-vis" data-column="0">Merchant</a> <a
															class="toggle-vis" data-column="1">Plan Name </a> <a
															class="toggle-vis" data-column="2">
															<th class="min-w-90px">Plan Description</th>
														</a> <a class="toggle-vis" data-column="3">Period</a> <a
															class="toggle-vis" data-column="4">Interval </a> <a
															class="toggle-vis" data-column="5">Amount</a> <a
															class="toggle-vis" data-column="6">Currency</a> <a
															class="toggle-vis" data-column="7">Additional
															Parameter 1</a> <a class="toggle-vis" data-column="8">Additional
															Parameter 2</a>
													</div>
												</div>
											</div>
										</div>
										<div class="row g-9 mb-8">
											<div class="table-responsive">
												<table id="txnResultDataTable"
													class="table table-striped table-row-bordered gy-5 gs-7 dataTable no-footer">
													<!-- 													<thead> -->
													<!-- 														<tr class="fw-bold fs-6 text-gray-800"> -->
													<!-- 															<th class="min-w-90px">Merchant</th> -->
													<!-- 															<th class="min-w-90px">Plan Name</th> -->
													<!-- 															<th class="min-w-90px">Plan Discription</th> -->
													<!-- 															<th class="min-w-90px">Period</th> -->
													<!-- 															<th class="min-w-90px">Interval</th> -->
													<!-- 															<th class="min-w-90px">Amount</th> -->
													<!-- 															<th class="min-w-90px">Currency</th> -->
													<!-- 															<th class="min-w-90px">Additional Parameter 1</th> -->
													<!-- 															<th class="min-w-90px">Additional Parameter 2</th> -->
													<!-- 														</tr> -->
													<!-- 													</thead> -->

													<!-- 													<tfoot> -->
													<!-- 														<tr class="fw-bold fs-6 text-gray-800"> -->
													<!-- 															<th class="min-w-90px"></th> -->
													<!-- 															<th class="min-w-90px"></th> -->
													<!-- 															<th class="min-w-90px"></th> -->
													<!-- 															<th class="min-w-90px"></th> -->
													<!-- 															<th class="min-w-90px"></th> -->
													<!-- 															<th class="min-w-90px"></th> -->
													<!-- 															<th class="min-w-90px"></th> -->
													<!-- 															<th class="min-w-90px"></th> -->
													<!-- 															<th class="min-w-90px"></th> -->
													<!-- 														</tr> -->
													<!-- 													</tfoot> -->
												</table>
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
	</div>
	<script src="../assets/plugins/custom/datatables/datatables.bundle.js"></script>
	<script type="text/javascript">
		$("#updateAt").flatpickr({
			//maxDate : new Date(),
			dateFormat : 'Y-m-d',
			defaultDate : "today"
		});
		$("#createAt").flatpickr({
			maxDate : new Date(),
			dateFormat : 'Y-m-d',
			defaultDate : "today"
		});

		$('a.toggle-vis').on('click', function(e) {
			debugger
			e.preventDefault();
			table = $('#txnResultDataTable').DataTable();
			// Get the column API object
			var column1 = table.column($(this).attr('data-column'));
			// Toggle the visibility
			column1.visible(!column1.visible());
			if ($(this)[0].classList[1] == 'activecustom') {
				$(this).removeClass('activecustom');
			} else {
				$(this).addClass('activecustom');
			}
		});

		function myFunction() {
			var x = document.getElementById("actions11").value;
			if (x == 'csv') {
				document.querySelector('.buttons-csv').click();
			}
			if (x == 'copy') {
				document.querySelector('.buttons-copy').click();
			}
			if (x == 'pdf') {
				document.querySelector('.buttons-pdf').click();
			}

			// document.querySelector('.buttons-excel').click();
			// document.querySelector('.buttons-print').click();

		}

		function convert(str) {
			var date = new Date(str), mnth = ("0" + (date.getMonth() + 1))
					.slice(-2), day = ("0" + date.getDate()).slice(-2);
			//return [date.getFullYear(), mnth, day].join("-");
			return [ day, mnth, date.getFullYear() ].join("-");
		}

		var request;
		function save() {
			debugger
			
			var valid = true;
			
			if(!$("#merchant").val())
			{
			document.getElementById("merchantError").innerHTML="Merchant Name Is Required";
			valid = false;
			}else{
				document.getElementById("merchantError").innerHTML="";
			}
			if(!$("#planName").val())
				{
				document.getElementById("planError").innerHTML="Plan Name Is Required";
				valid = false;
				}else{
					document.getElementById("planError").innerHTML="";
				}
			
			if(!$("#planDescription").val())
			{
			document.getElementById("planDescriptionError").innerHTML="Plan Description Is Required";
			valid = false;
			}else{
				document.getElementById("planDescriptionError").innerHTML="";
			}
			
			if(!$("#type").val())
			{
			document.getElementById("typeError").innerHTML="Type Is Required";
			valid = false;
			}else{
				document.getElementById("typeError").innerHTML="";
			}
			if(!$("#unit").val())
			{
			document.getElementById("unitError").innerHTML="Unit Is Required";
			valid = false;
			}else{
				document.getElementById("unitError").innerHTML="";
			}
			
			if(!$("#unitAmount").val())
			{
			document.getElementById("unitamountError").innerHTML="Unit Amount Is Required";
			valid = false;
			}else{
				document.getElementById("unitamountError").innerHTML="";
			}
			
			if(!$("#amount").val())
			{
			document.getElementById("amountError").innerHTML="Amount Is Required";
			valid = false;
			}else{
				document.getElementById("amountError").innerHTML="";
			}
			
			if(!$("#ap1").val())
			{
			document.getElementById("ap1Error").innerHTML="Additional Prop1 Is Required";
			valid = false;
			}else{
				document.getElementById("ap1Error").innerHTML="";
			}
			if(!$("#ap2").val())
			{
			document.getElementById("ap2Error").innerHTML="Additional Prop2 Is Required";
			valid = false;
			}else{
				document.getElementById("ap2Error").innerHTML="";
			}
			if(!$("#ap3").val())
			{
			document.getElementById("ap3Error").innerHTML="Additional Prop3 Is Required";
			valid = false;
			}else{
				document.getElementById("ap3Error").innerHTML="";
			}
			
			
			
			
			if(valid){
			request = {
				"active" : true,
				"name" : $("#planName").val(),
				"description" : $("#planDescription").val(),
				"status" : "Active",
				"unit_amount" : $("#unitamount").val(),
				"amount" : $("#amount").val(),
				"currency" : $("#currency").val(),
				"type" : $("#type").val(),
				"unit" : $("#unit").val(),
				"sac_code" : "SAC001",
				"hsn_code" :"HSN001",
				"tax_rate" : "18",
				"tax_group_id" :"1",
				//"status" : $("#activeS").val(),
				"create_at" : $("#createAt").val(),
				"tax_inclusive" : true,
				"tax_id" : "1",
				"update_at" : $("#updateAt").val(),

				"additionalParams" : {
					"additionalProp1" : $("#ap1").val(),
					"additionalProp2" : $("#ap2").val(),
					"additionalProp3" : $("#ap3").val(),
				}

			}
			var urls = new URL(window.location.href);
	        var domain = urls.origin;
			$.ajax({
				//url : "http://localhost:8080/api/v1/plans",
				url:domain+"/siws/api/v1/plans/",
				type : 'POST',
				headers : {
					'accept' : '*/*',
					'Content-Type' : 'application/json',
					'Cookie' : 'JSESSIONID=4A38E87577BDC90D95675E11D4445BC5',
					'merchantId' :  "<s:property value='%{#session.USER.payId}'/>",
					'customerId' : 1234,
					'key' : 1234,
				},
				data : JSON.stringify(request),
				success : function(data) {
					alert(data.message);
					
					/* $.ajax({
						//url : "http://localhost:8080/api/v1/plans/",
						url:domain+"/siws/api/v1/plans/",
						type : 'GET',
						headers : {
							'accept' : 
							'Content-Type' : 'application/json',
							'Cookie' : 'JSESSIONID=4A38E87577BDC90D95675E11D4445BC5',
							'merchantId' :  "<s:property value='%{#session.USER.payId}'/>",
							'customerId' : 1234,
							'key' : 1234,
						},

						success : function(data) {
							debugger
							//var request=data.payLoad;

							var res = data.payLoad;
							var result = [];
							for (var i = 0; i < res.length; i++) {
								
								result.push([ res[i].name, res[i].active, res[i].amount,
										res[i].createDate, res[i].created_at,
										res[i].created_at, res[i].created_at,
										res[i].currency, res[i].description,
										res[i].hsn_code, res[i].planId,
										res[i].sac_code, res[i].status,
										res[i].tax_group_id, res[i].tax_id,
										res[i].tax_inclusive, res[i].tax_rate,
										res[i].type,
										res[i].unit,
										res[i].unit_amount,
										res[i].updated_at ]);
							}
							
							rendertable(result);

						},
						error : function(data, textStatus, jqXHR) {
							alert(data.message);

						}
					}); */

				},
				error : function(data, textStatus, jqXHR) {
					alert(data.message);

				}
			});
			}
		}
		//	"key":"test",
            var urls = new URL(window.location.href);
	        var domain = urls.origin;
		debugger
		$.ajax({
			//url : "http://localhost:8080/api/v1/plans/",
			url:domain+"/siws/api/v1/plans/",
			type : 'GET',
			headers : {
				'accept' : '*/*',
				'Content-Type' : 'application/json',
				'Cookie' : 'JSESSIONID=4A38E87577BDC90D95675E11D4445BC5',
				'merchantId' :  "<s:property value='%{#session.USER.payId}'/>",
				'customerId' : 1234,
				'key' : 1234,
			},

			success : function(data) {
				debugger
				//var request=data.payLoad;

				var res = data.payLoad;
				var result = [];
				for (var i = 0; i < res.length; i++) {
					
					result.push([ res[i].name, res[i].active, res[i].amount,
							res[i].createDate, res[i].created_at,
							res[i].created_at, res[i].created_at,
							res[i].currency, res[i].description,
							res[i].hsn_code, res[i].planId,
							res[i].sac_code, res[i].status,
							res[i].tax_group_id, res[i].tax_id,
							res[i].tax_inclusive, res[i].tax_rate,
							res[i].type,
							res[i].unit,
							res[i].unit_amount,
							res[i].updated_at ]);
				}
				
				rendertable(result);

			},
			error : function(data, textStatus, jqXHR) {
				alert(data.message);

			}
		});

		/* var data = [
			  ["John Doe", "johndoe@example.com", "New York", "USA"],
			  ["Jane Doe", "janedoe@example.com", "Los Angeles", "USA"],
			  ["Bob Smith", "bobsmith@example.com", "Toronto", "Canada"],
			  ["Alice Brown", "alicebrown@example.com", "Sydney", "Australia"]
			]; */
		// Define the table columns
		var columns = [ {
			title : "Name"
		}, {
			title : "Active"
		},

		{
			title : "Amount"
		}, {
			title : "Create Date"
		}, {
			title : "Created At"
		}, {
			title : "Currency"
		}, {
			title : "Description"
		}, {
			title : "HSN Code"
		}, {
			title : "Plan Id"
		}, {
			title : "SAC Code"
		}, {
			title : "Status"
		}, {
			title : "Tax Group Id"
		}, {
			title : "Tax Id"
		}, {
			title : "Tax Inclusive"
		}, {
			title : "Tax Rate"
		}, {
			title : "Type"
		}, {
			title : "Unit"
		}, {
			title : "Unit Amount"
		}, {
			title : "Updated At"
		}, ];
		// Create the DataTable with the nested array data

		function rendertable(data) {
			$(document).ready(function() {
				$('#txnResultDataTable').DataTable({
					data : data,
					columns : columns
				});
			});

		}
	</script>


</body>

</html>