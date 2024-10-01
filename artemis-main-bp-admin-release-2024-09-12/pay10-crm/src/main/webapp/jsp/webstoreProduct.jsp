<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="/struts-tags" prefix="s"%>
<html dir="ltr" lang="en-US">

<head>
<title>WebStore</title>

<link rel="shortcut icon" href="assets/media/images/paylogo.svg" />
<link rel="stylesheet"
	href="https://fonts.googleapis.com/css?family=Inter:300,400,500,600,700" />
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

<style>
.bold {
	font-weight: bolder !important;
	color: black !important;
}

div#datatable_wrapper {
	margin-top: 12px;
}

td.dataTables_empty {
	display: none !important;
}

button.btn.btn-success {
	margin-right: 20px !important;
}

button.btn.btn-secondary {
	background-color: #dfd2c8 !important;
}

#statusResponse {
	text-align: center;
	font-weight: bolder;
	font-size: 16px;
}
</style>
<script type="text/javascript">
	$(document).ready(function() {

		$(function() {
			$("#dateFrom").flatpickr({
				maxDate : new Date(),
				dateFormat : "Y-m-d",
				defaultDate : "today",
				defaultDate : "today",
			});
			$("#dateTo").flatpickr({
				maxDate : new Date(),
				dateFormat : "Y-m-d",
				defaultDate : "today",
				maxDate : new Date()
			});
			$("#kt_datatable_vertical_scroll").DataTable({
				scrollY : true,
				scrollX : true

			});
		});
		$(".adminMerchants").select2();
	});
</script>
<script>
	function add(id,name,description,price,discountprice,payId,productStatus,image) {
		$("#id").val(id);
		$("#name").val(name);
		$("#description").val(description);
		$("#price").val(price);
		$("#discountprice").val(discountprice);
		$("#payId1").val(payId);
		$("#productStatus").val(productStatus);
		$("#image").val(image);
		
		console .log("image "+$("#image").val());
		$('#addProduct').submit();
	}
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
					<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">WebStore</h1>
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
						<li class="breadcrumb-item text-muted">WebStore Product</li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item"><span
							class="bullet bg-gray-200 w-5px h-2px"></span></li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item text-dark">WebStore Product</li>
						<!--end::Item-->
					</ul>
					<!--end::Breadcrumb-->
				</div>
				<!--end::Page title-->

			</div>
			<!--end::Container-->
		</div>





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
									style="font-size: 16px; color: #0271bb; font-weight: 500;">Add
									New Product</h4>
							</div>


						</div>



						<%-- <s:form action="" id="formname">
							<s:token /> --%>
							<div class="row g-12 mb-8">
								<div class="col-md-3 fv-row">
									<!--begin::Label-->
									<label class="d-flex align-items-center fs-6 fw-semibold mb-2">
										<span class="">Merchant</span>
									</label>
									<s:if test="%{#session.USER.UserGroup.group =='Merchant'}">
											<s:select name="payId" id="payId" headerKey=""
												headerValue="Select Merchant"
												class="form-select form-select-solid merchantPayId"
												listKey="uuId" listValue="businessName" list="merchants"
												data-control="select2" />
										</s:if>
									<s:else>
									<div class="col-auto my-2">
										<s:select name="payId" id="payId" headerKey=" "
											headerValue="Select Merchant"
											class="form-select form-select-solid merchantPayId"
											listKey="uuId" listValue="businessName" list="merchants"
											data-control="select2" />
									</div>
									</s:else>

								</div>

								<div class="col-md-3 fv-row">
									<!--begin::Label-->
									<label class="d-flex align-items-center fs-6 fw-semibold mb-2">
										<span class="">Order Id</span>
									</label>

									<div class="txtnew" id="orderId">
										<s:textfield id="orderId" name="orderId"
											class="form-control form-control-solid"
											placeholder="OrderId*" autocomplete="off" maxlength="100" />
									</div>

								</div>


								<div class="col-md-3 fv-row">
									<label class="d-flex align-items-center fs-6 fw-semibold mb-2">
										<span class="">Date From</span>
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
											placeholder="Select a date" name="dateFrom" id="dateFrom"
											type="text" readonly="readonly">
										<!--end::Datepicker-->
									</div>
								</div>


								<div class="col-md-3 fv-row">
									<label class="d-flex align-items-center fs-6 fw-semibold mb-2">
										<span class="">Date To</span>
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
											placeholder="Select a date" name="dateTo" id="dateTo"
											type="text" readonly="readonly">
										<!--end::Datepicker-->
									</div>
								</div>
							</div>
						 <div class="col-md-3" >
							<button class="btn btn-secondary"style="margin-left: 660px;" onclick="view()">View</button>
						</div>
					<%-- 	<div class="col-md-3"
							style="margin-left: 750px; margin-top: -35px;">
							<button type="button" id="search_payment_submit"
								onclick="download()" class="btn btn-primary">
								<span class="indicator-label">DOWNLOAD</span> <span
									class="indicator-progress">Please wait... <span
									class="spinner-border spinner-border-sm align-middle ms-2"></span>
							</button>
						</div> --%>
							<%-- </s:form> --%>
					</div>
					<div class="row g-12 mb-8">
						<s:form action="webstoreOrder" id="addProduct">
						<s:hidden name="id" id="id" />
						<s:hidden name="name" id="name" />
						<s:hidden name="description" id="description" />
						<s:hidden name="price" id="price" />
						<s:hidden name="discountprice" id="discountprice" />
						<s:hidden name="payId1" id="payId1" />
						<s:hidden name="productStatus" id="productStatus"/>
						<s:hidden name="image" id="image"/>
							<div class="col-md-3">
								<button class="btn btn-secondary" onclick="add()" style="margin-left: 20px;margin-top: -112px;">Add</button>
							</div>
						</s:form>


					

					</div>
				</div>
				<!-- <span id="excelError" class="error"></span> -->

				<div class="row my-5">
					<div class="col">
						<div class="card">
							<div class="card-body">
								<div class="row my-3 align-items-center">


									<div class="col-lg-12 fv-row">

										<table
											class="table table-striped table-row-bordered gy-5 gs-7"
											id="datatable">
											<thead>
												<tr>
													<th class="bold">ID</th>
													<th class="bold">Name</th>
													<th class="bold">Description</th>
													<th class="bold">Image</th>
													<th class="bold">Price</th>
													<th class="bold">Discount Price</th>
													<th class="bold">Product Status</th>
													<th class="bold">Created At</th>
													<th class="bold">Updated At</th>
													<th class="bold">Action</th>
												</tr>
											</thead>
											<tbody>

											</tbody>
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


	<script
		src="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.js"></script>
	<script src="../assets/plugins/global/plugins.bundle.js"></script>
	<script src="../assets/js/scripts.bundle.js"></script>
	<script src="../assets/plugins/custom/datatables/datatables.bundle.js"></script>
	<script>

		function renderTable(data) {
			
			var buttonCommon = {
				exportOptions : {
					format : {

					}
				}
			};

			$("#datatable").DataTable().destroy();
			$('#datatable')
					.dataTable(
							{
								'data' : data,

								dom : 'BTrftlpi',

								"scrollY" : true,
								"scrollX" : true,
								'columns' : [
										{
											"data" : "id",

										},

										{
											'data' : 'name'
										},

										{
											'data' : 'description'
										},
										{
											"mData" : null,
											"sClass" : "center",
											"mRender" : function(data) {
												      var getImage=JSON.stringify(data.image)
												    
										             // return "<img src='https://webstore.pay10.com/uploads/2lWiXLdmF8GlBnIaion1_16892427461771897524.jpg' width='40px'>";}
												      return '<img src='+getImage+'  width=\'40px\'">';}
											            },
										
										{
											'data' : 'price'
										},

										{
											'data' : 'discounted_price'
										},
										{
											"mData" : null,
											"sClass" : "center",
											"mRender" : function(data) {
												    
												      return '<input type="checkbox" disabled=disabled class="minimal"' + (data.product_status==1 ? ' checked' : '') + '/>';}
											  
										
										},
										{
											'data' : 'created_at'
										},
										{
											'data' : 'updated_at'
										},
										{
											"mData" : null,
											"sClass" : "center",
											"mRender" : function(data) {
												var classforEdit ="btn btn-sm btn-primary";
												return '<a data-toggle="tooltip" data-placement="top" data-original-title="Edit"  id="editBtn" style="margin-top: 2px;margin-left: 2px;" class="'
														+ classforEdit
														+ '" onclick=\'getData('
														+  JSON.stringify(data)
														+ ')\'>Edit</a>';

											}
										} ]
							});
		}
	</script>
<script type="text/javascript">

function getData(data) {
	var id=data.id;
	var name=data.name;
	var description=data.description;
	var price=data.price;
	var discountprice=data.discounted_price;
	var payId=data.uuid;
	var image=data.image;
	if(data.product_status==1){
	var productStatus='true'
	}
	add(id,name,description,price,discountprice,payId,productStatus,image);
}
function view(){
	var urls = new URL(window.location.href);
	var domain = urls.origin;
	const arrayData = [];
	var data = {
			uuid:$('#payId').val()
	}
	$.ajax({

		type : "POST",
		data : JSON.stringify(data),
		contentType : "application/json",
		url : domain+"/crmws/webStoreApi?type=VIEW_PRODUCT",
		success : function(response) {

			var res = JSON.parse(response.response);
			const map = new Map(Object.entries(res));
			map.forEach(function(value, key) {
			
				arrayData.push(value);
			})

			renderTable(arrayData);

		}

	});	
}
</script>
	<style>
.dt-buttons {
	display: none;
}
</style>
</body>

</html>