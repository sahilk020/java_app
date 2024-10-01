<%@page import="com.pay10.crm.actionBeans.TransactionStatusBean"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="/struts-tags" prefix="s"%>
<html dir="ltr" lang="en-US">

<head>
<title>Update WebStore </title>
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

.error {
	color: red
}

#txnResultDataTable thead th {
	font-weight: bold;
}
</style>
<script type="text/javascript">
	$(document).ready(function() {
		
		
		$(function () {
			$("#dateFrom").flatpickr({
				maxDate: new Date(),
				dateFormat: "Y-m-d",
				defaultDate: "today",
				defaultDate: "today",
			});
			$("#dateTo").flatpickr({
				maxDate: new Date(),
				dateFormat: "Y-m-d",
				defaultDate: "today",
				maxDate: new Date()
			});
			$("#kt_datatable_vertical_scroll").DataTable({
				scrollY: true,
				scrollX: true

			});
		});
		$(".adminMerchants").select2();
	});
</script>

</head>
<body>

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
					<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">WebStore Edit</h1>
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
						<li class="breadcrumb-item text-muted">WebStore Edit</li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item"><span
							class="bullet bg-gray-200 w-5px h-2px"></span></li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item text-dark">Update Order</li>
						<!--end::Item-->
					</ul>
					<!--end::Breadcrumb-->
				</div>
				<!--end::Page title-->

			</div>
			<!--end::Container-->
		</div>




		<s:form action="signupMerchant" id="formname">
			<s:token />
			<div class="post d-flex flex-column-fluid" id="kt_post">
				<!--begin::Container-->
				<div id="kt_content_container" class="container-xxl">
					<!--begin::Input group-->
					<div class="card">
						<div class="card-body">
							<div class="row g-9 mb-8">
								<!--begin::Col-->

								<div class="card-header card-header-rose card-header-icon">

									<h4 class="card-title"
										style="font-size: 16px; color: #0271bb; font-weight: 500;">Update WebStrore Product
								</h4>
								</div>


							</div>


<div class="row my-3 align-items-center">
									
							
								<div class="col-md-4">
								
									<!--begin::Label-->
									<label class="d-flex align-items-center fs-6 fw-semibold mb-2">
										<span class="required">UserId</span>
									</label>
									<div class="txtnew" id="usersid1">
										<s:textfield id="usersid" name="usersid"
											class="form-control form-control-solid" placeholder="Usersid*"
											autocomplete="off" maxlength="100" />
									</div>
									<p class="errorSec errorBusninessName">Please Enter Users ID</p>
								</div>
								
								<div class="col-md-4">
									<!--begin::Label-->
									<label class="d-flex align-items-center fs-6 fw-semibold mb-2">
										<span class="">Name</span>
									</label>

									<div class="txtnew" id="name">
										<s:textfield id="name" name="name"
											class="form-control form-control-solid"
											placeholder="name*" autocomplete="off" maxlength="100" />
									</div>
									<p class="errorSec errorAliasName">Please Enter your Name</p>
								</div>
								
								<div class="col-md-4">
									<!--begin::Label-->
									<label class="d-flex align-items-center fs-6 fw-semibold mb-2">
										<span class="">Description</span>
									</label>

									<div class="txtnew" id="description">
										<s:textfield id="description" name="description"
											class="form-control form-control-solid"
											placeholder="description*" autocomplete="off" maxlength="100" />
									</div>
									<p class="errorSec errorAliasName">Please Enter Description</p>
								</div>
								<div class="row my-3 align-items-center">
								<div class="col-md-4">
									<!--begin::Label-->
									<label class="d-flex align-items-center fs-6 fw-semibold mb-2">
										<span class="">Price</span>
									</label>

									<div class="txtnew" id="price">
										<s:textfield id="price" name="price"
											class="form-control form-control-solid"
											placeholder="price*" autocomplete="off" maxlength="100" />
									</div>
									<p class="errorSec errorAliasName">Please Enter Price Details</p>
								</div>
								
									<div class="col-md-4">
									<!--begin::Label-->
									<label class="d-flex align-items-center fs-6 fw-semibold mb-2">
										<span class="">Discount Price</span>
									</label>

									<div class="txtnew" id="discountprice">
										<s:textfield id="discountprice" name="discountprice"
											class="form-control form-control-solid"
											placeholder="discountprice*" autocomplete="off" maxlength="100" />
									</div>
									<p class="errorSec errorAliasName">Please Enter DiscountPrice Details</p>
								</div>
									<div class="col-md-4">
  <!--begin::Label-->
  <label class="d-flex align-items-center fs-6 fw-semibold mb-2">
    <span class="">Image</span>
  </label>
  <div class="txtnew" id="Image">
    <input type="file" id="Image" name="Image" class="form-control form-control-solid" accept="image/*" />
  </div>
  <p class="errorSec errorAliasName">Please provide an image file in JPG format</p>
</div>
								<div class="row my-3 align-items-center">
								<div class="col-md-4">
									<!--begin::Label-->
									<label class="d-flex align-items-center fs-6 fw-semibold mb-2">
										<span class="">UUID</span>
									</label>

									<div class="txtnew" id="uuid">
										<s:textfield id="uuid" name="uuid"
											class="form-control form-control-solid"
											placeholder="uuid*" autocomplete="off" maxlength="100" />
									</div>
									<p class="errorSec errorAliasName">Please Enter UUID Details</p>
								</div>
									<div class="card-footer text-right" style="width: 470px;">
									<s:submit value="Edit" method="submit"
										id="addBtn"
										class="btn w-100 w-md-25 btn-primary "
										style="padding: 11px;
						font-size: 14px;">
									</s:submit>
									
									</div>
								</div>
				
								
							




										
										
										
										
										



							

								
								

							
						</div>
					</div>
				</div>
			</div>
			</div>
		</s:form>
		
		
		
        
    


	</div>





</body>
</html>
