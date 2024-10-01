<%@page import="com.pay10.crm.actionBeans.TransactionStatusBean"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="/struts-tags" prefix="s"%>
<html dir="ltr" lang="en-US">

<head>
<title>Force Captured Report</title>
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

.svg-icon {
	margin-top: 1vh !important;
}
.select2-container--bootstrap5 .select2-selection--multiple:not(.form-select-sm):not(.form-select-lg) .select2-selection__choice .select2-selection__choice__display {
    margin-left: 0.9rem;
    font-size: 0.99rem;
}
.select2-container .select2-selection--multiple .select2-selection__rendered {
    display: block !important; ;
    overflow: scroll !important; ;
     padding-left: 1px !important; 
     text-overflow: unset; 
    white-space: nowrap;
}
</style>
<script type="text/javascript">
var urls = new URL(window.location.href);
var domain = urls.origin;
	$(document).ready(function() {
		$(".adminMerchants").select2();

		$("#rnsBulkUpload").click(function(){
			$.ajax({
				url: domain + "/crmws/ExceptionList/rnsBulkUpload",
				type: 'POST',
				headers: {
					"Content-Type": "application/json"
				},
				data: JSON.stringify({
					"pgrefno": pgref
				}),
				success: function (data) {
					alert(data.respmessage);
					window.location.reload();
				},
				error: function (data, textStatus, jqXHR) {
					
					if (data.responseText
						&& JSON.parse(data.responseText).respmessage) {
						var responseText = JSON.parse(data.responseText)
						alert(responseText.respmessage);
					} else {
						alert("Error while Updating RNS transactions");
					}
					window.location.reload();

				}
			});
			});
		$("#refundBulkUpload").click(function(){
			$.ajax({
				url: domain + "/crmws/ExceptionList/refundbulkUpload",
				type: 'POST',
				headers: {
					"Content-Type": "application/json"
				},
				data: JSON.stringify({
					"pgrefno": pgref
				}),
				success: function (data) {
					alert(data.multipleResponse);
				
					window.location.reload();
				},
				error: function (data, textStatus, jqXHR) {
					if (data.responseText
						&& JSON.parse(data.responseText).respmessage) {
						var responseText = JSON.parse(data.responseText)
						alert(responseText.respmessage);
					} else {
						alert("Error while refund transactions");
					}
					window.location.reload();

				}
			});
		});
		
	});
	function RNS(id) {
		$
				.ajax({
					url : domain + "/crmws/ExceptionList/RNS/" + id,
					type : 'POST',
					
					success : function(data) {
						alert(data.respmessage);
						window.location.reload();
					},
					error : function(data, textStatus, jqXHR) {

						if (data.responseText
								&& JSON.parse(data.responseText).respmessage) {
							var responseText = JSON.parse(data.responseText)
							alert(responseText.respmessage);
						} else {
							alert("RnS status not updated successfully!");
						}
						window.location.reload();

					}
				});
	}
	function Refund(id) {
		$
				.ajax({
					url : domain + "/crmws/ExceptionList/Refund/" + id,
					type : 'POST',
					success : function(data) {
						alert(data.respmessage);
						window.location.reload();
					},
					error : function(data, textStatus, jqXHR) {

						if (data.responseText
								&& JSON.parse(data.responseText).respmessage) {
							var responseText = JSON.parse(data.responseText)
							alert(responseText.respmessage);
						} else {
							alert("Bank/Acquirer favour status not updated successfully!");
						}
						window.location.reload();

					}
				});
	}
	var pgref = [];
	function SendChecked(id) {
		var checkedNum = $('input[name="checkbox_pg_num[' + id + ']"]:checked').length;
		if (checkedNum == 1) {
			pgref.push(id);
			console.log(pgref);
		} else {
			pgref.pop(id);
			console.log(pgref);
		}
	}
</script>
<script>
	var result;
	var results;



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
						Force Captured Report</h1>
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
						<li class="breadcrumb-item text-dark">Force Captured Report</li>
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
								<s:form id="form" class="mt-3">

									<div class="row my-3 align-items-center">

										<div class="col-lg-3 my-2">
											<label class="d-flex align-items-center fs-6 fw-bold mb-2">
												<span class="required">Type</span>
											</label> <select class="form-select form-select-solid adminMerchants"
												id="type">
												<option value="" selected>Please Select</option>
												<option value="RNS">RNS</option>
												<option value="REFUND">REFUND</option>
											</select>
										</div>
										<div class="col-lg-3 my-2">
											<label class="d-flex align-items-center fs-6 fw-bold mb-2">
												<span class="required">FILE</span>
											</label> <input type="file" name="file" id="file"
												class="form-select form-select-solid">
										</div>


										<div class="col-lg-3 my-2">
											<label class="d-flex align-items-center fs-6 fw-bold mb-2">
												<span class="">&nbsp</span>
											</label>
											<button type="button" class="btn btn-primary" id="btnSubmit"
												onclick="submitCSV()">Submit</button>
										</div>
										<div class="col-lg-3 my-2">
											<label class="d-flex align-items-center fs-6 fw-bold mb-2">
												<span class="">&nbsp</span>
											</label>
											<a href="../assets/forceCapture.xlsx"
															download="ForceCapture" class="btn btn-primary btn-xs">Sample
															File Download</a>
										</div>


									</div>

								</s:form>

								<div class="row my-3 align-items-center">

									<div class="col-lg-3 my-2">
										<label class="d-flex align-items-center fs-6 fw-bold mb-2">
											<span class="required">Merchant</span>
										</label>
										<s:select name="merchant"
											class="form-select form-select-solid adminMerchants"
											id="merchant" headerKey="ALL" headerValue="ALL"
											list="merchantList" listKey="payId" listValue="businessName"
											autocomplete="off"  multiple="true"/>
									</div>
									<div class="col-lg-3 my-2">
										<label class="d-flex align-items-center fs-6 fw-bold mb-2">
											<span class="required">Acquirer</span>
										</label>
										<s:select headerKey="ALL" headerValue="ALL"
											list="@com.pay10.commons.util.AcquirerTypeUI@values()"
											id="acquirer"
											class="form-select form-select-solid adminMerchants"
											name="acquirer" value="acquirer" listValue="name"
											listKey="code"  multiple="true"/>
									</div>
									<div class="col-lg-3 my-2">
										<label class="d-flex align-items-center fs-6 fw-bold mb-2">
											<span class="required">Date From</span>
										</label>
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
											type="text">
										<!--end::Datepicker-->
									</div>

									<div class="col-lg-3 my-2">
										<label class="d-flex align-items-center fs-6 fw-bold mb-2">
											<span class="required">Date To</span>
										</label>
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
											type="text">
										<!--end::Datepicker-->
									</div>


								</div>
								<div style="text-align: end;">
									<button type="button" class="btn btn-primary" id="done"
										onclick="reloadTable()">Submit</button>
										<button type="button" class="btn btn-primary" id="download"
										onclick="downloadExceptionList()">Download</button>
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
														<a class="toggle-vis" data-column="0">Txn Id</a> <a
															class="toggle-vis" data-column="1">Pg ref no</a> <a
															class="toggle-vis" data-column="2">Merchant</a> <a
															class="toggle-vis" data-column="3">Date</a> <a
															class="toggle-vis" data-column="4">Order Id</a> <a
															class="toggle-vis" data-column="5">Refund Order Id</a> <a
															class="toggle-vis" data-column="6">Mop Type</a> <a
															class="toggle-vis" data-column="7">Payment Method</a> <a
															class="toggle-vis" data-column="8">Txn Type</a> <a
															class="toggle-vis" data-column="9">Status</a> <a
															class="toggle-vis" data-column="10">Base Amount</a> <a
															class="toggle-vis" data-column="11">Total Amount</a> <a
															class="toggle-vis" data-column="12">Pay Id</a> <a
															class="toggle-vis" data-column="13">Customer Email</a> <a
															class="toggle-vis" data-column="14">Customer Ph
															Number</a> <a class="toggle-vis" data-column="15">Udf 4</a>
													</div>
												</div>
											</div>
										</div>
										<div class="row g-9 mb-8">
											<div class="table-responsive">
												<table id="txnResultDataTable"
													class="table table-striped table-row-bordered gy-5 gs-7 dataTable no-footer">
													<thead>
														<tr class="fw-bold fs-6 text-gray-800">
															
															<th class="min-w-90px">Txn Id</th>
															<th class="min-w-90px">Pg Ref Num</th>
															<th class="min-w-90px">Merchant</th>
															<th class="min-w-90px">Date</th>
															<th class="min-w-90px">Order Id</th>
															<th class="min-w-90px">Refund Order Id</th>
															<th class="min-w-90px">Mop Type</th>
															<th class="min-w-90px">Payment Method</th>
															<th class="min-w-90px">Txn Type</th>
															<th class="min-w-90px">Status</th>
															<th class="min-w-90px">Base Amount</th>
															<th class="min-w-90px">Total Amount</th>
															<th class="min-w-90px">Pay ID</th>
															<th class="min-w-90px">Customer Email</th>
															<th class="min-w-90px">Customer Ph Number</th>
															<th class="min-w-90px">Acquirer Type</th>
															<th class="min-w-90px">Ip Address</th>
															<th class="min-w-90px">card Mask</th>
															<th class="min-w-90px">RRN Number</th>
															<th class="min-w-90px">SplitPayment</th>
															
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
															<th class="min-w-90px"></th>
															<th class="min-w-90px"></th>
															<th class="min-w-90px"></th>
															<th class="min-w-90px"></th>
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
										</div>
									</div>
								</div>
							</div>
						</div>

<!-- 						<div class="row my-5" style="text-align: center;"> -->
<!-- 							<div class="col-lg-12"> -->
<!-- 								<button class="btn btn-primary btn-xs" -->
<!-- 									style="padding: px; padding-left: 100px; padding-right: 100px;" id="rnsBulkUpload">RNS</button> -->

<!-- 								<button class="btn btn-primary btn-xs ml-2" -->
<!-- 									style="padding: px; padding-left: 100px; padding-right: 100px;" id="refundBulkUpload">Refund</button> -->
<!-- 							</div> -->
<!-- 						</div> -->

					</div>
				</div>
				<form action="DownloadExceptionList" id="downloadException">
					<input id="payId" name="payId" type="hidden">
					<input id="acq" name="acq" type="hidden">
					<input id="dFrom" name="dFrom" type="hidden">
					<input id="dTo" name="dTo" type="hidden">
				</form>
			</div>
		</div>
	</div>
	<script src="../assets/plugins/custom/datatables/datatables.bundle.js"></script>
	<script type="text/javascript">
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
		$("#dateFrom").flatpickr({
			maxDate : new Date(),
			dateFormat : 'Y-m-d',
			defaultDate : "today"
		});
		$("#dateTo").flatpickr({
			maxDate : new Date(),
			dateFormat : 'Y-m-d',
			defaultDate : "today"
		});
		function convert(str) {
			var date = new Date(str), mnth = ("0" + (date.getMonth() + 1))
					.slice(-2), day = ("0" + date.getDate()).slice(-2);
			//return [date.getFullYear(), mnth, day].join("-");
			return [ day, mnth, date.getFullYear() ].join("-");
		}
	</script>
	<script type="text/javascript">
		$("#txnResultDataTable").DataTable({
			scrollY : true,
			scrollX : true

		});
		function reloadTable() {
			dateFrom = document.getElementById("dateFrom").value;
			dateTo = document.getElementById("dateTo").value;
			var transFrom = convert(dateFrom);
			var transTo = convert(dateTo);
			var transFrom1 = new Date(Date.parse(document
					.getElementById("dateFrom").value));
			var transTo1 = new Date(Date.parse(document
					.getElementById("dateTo").value));
			if (transFrom1 == null || transTo1 == null) {
				alert('Enter date value');
				return false;
			}

			if (transFrom1 > transTo1) {
				alert('From date must be before the to date');
				$('#loader-wrapper').hide();
				$('#dateFrom').focus();
				return false;
			}
			if (transTo1 - transFrom1 > 31 * 86400000) {
				alert('No. of days can not be more than 31');
				$('#loader-wrapper').hide();
				$('#dateFrom').focus();
				return false;
			} else {

				//dataTable();
				debugger
				var payIds=$("#merchant").val()


				
				// var result;
				for (var i = 0; i < payIds.length; i++) {
				
				 if(i==0){
					result=  payIds[i];
				 }else{
					result= result+","+ payIds[i];
				 }
				 
				}
				console.log(result);


				var acquirers=$("#acquirer").val()
				// var results;
			
				for (var i = 0; i < acquirers.length; i++) {
					if(i==0){
					results=  acquirers[i];
				 }else{
					results= results+","+ acquirers[i];
				 }
	
				}

				console.log(results);



				if(payIds.length>1)
				{
					var data = payIds.indexOf("ALL");
					if(data!=-1){
					alert("Either you can select ALL or multiple Merchant")
					return false;
					}
					
				}


				if(acquirers.length>1)
				{
					var element = acquirers.indexOf("ALL");
					if(element!=-1){
					alert("Either you can select ALL or multiple Acquirer")
					return false;
					}
					
				}
				


		
				

				

			

				
			
			


				
				var tableObj = $('#txnResultDataTable');
				var table = tableObj.DataTable();
				table.ajax.reload();
			}
		}
		function generatePostData(d) {
			debugger
			var token = document.getElementsByName("token")[0].value;
			var payId="";
			if(result == null || result == "" || result == undefined)
			{
				
				payId="ALL";
			}
			else
			{
				payId=result;
			}
			//var payId = $("#merchant").val();


			var acquirer="";
			if(results == null || results == "" || results == undefined)
			{
				acquirer = "ALL";
			}
			else{
				acquirer=results;

			}
			
			//var acquirer = document.getElementById("acquirer").value;

			var dateFrom = document.getElementById("dateFrom").value;
			var dateTo = document.getElementById("dateTo").value;

			var obj = {
				payId : payId,
				acquirer : acquirer,
				dateFrom : dateFrom,
				dateTo : dateTo,
				draw : d.draw,
				length : d.length,
				start : d.start,
				token : token,
				"struts.token.name" : "token",
			};

			return obj;
		}

		debugger
		$('#txnResultDataTable')

				.dataTable(

						{
							"footerCallback" : function(row, data, start, end,
									display) {
								var api = this.api(), data;

								// Remove the formatting to get integer data for summation
								var intVal = function(i) {
									return typeof i === 'string' ? i.replace(
											/[\,]/g, '') * 1
											: typeof i === 'number' ? i : 0;
								};

								// Total over all pages
								total = api.column(10).data().reduce(
										function(a, b) {
											return intVal(a) + intVal(b);
										}, 0);

								// Total over this page
								pageTotal = api.column(10, {
									page : 'current'
								}).data().reduce(function(a, b) {
									return intVal(a) + intVal(b);
								}, 0);

								// Update footer
								$(api.column(10).footer()).html(
										''
												+ inrFormat(pageTotal
														.toFixed(2)
														+ ' ' + ' '));

								// Total over all pages
								total = api.column(11).data().reduce(
										function(a, b) {
											return intVal(a) + intVal(b);
										}, 0);

								// Total over this page
								pageTotal = api.column(11, {
									page : 'current'
								}).data().reduce(function(a, b) {
									return intVal(a) + intVal(b);
								}, 0);

								// Update footer
								$(api.column(11).footer()).html(
										''
												+ inrFormat(pageTotal
														.toFixed(2)
														+ ' ' + ' '));

							},
							"columnDefs" : [ {
								className : "dt-body-right",
								"targets" : [ 1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
										11, 12, 13, 14, 15 ]
							} ],

							dom : 'Brtipl',
							buttons : [
									{
										extend : 'print',
										exportOptions : {
											columns : ':visible'
										}
									},
									{
										extend : 'pdfHtml5',
										orientation : 'landscape',
										pageSize : 'legal',
										//footer : true,
										title : 'Search Transactions',
										exportOptions : {
											columns : [ ':visible' ]
										},
										customize : function(doc) {
											doc.defaultStyle.alignment = 'center';
											doc.styles.tableHeader.alignment = 'center';
											doc.defaultStyle.fontSize = 8;
										}
									},
									{
										extend : 'copy',
										exportOptions : {
											columns : [ 0, 1, 2, 3, 4, 5, 6, 7,
													8, 9, 10, 11, 12, 13, 14,
													15, 16, 17, 18, 19, 20 ]
										}
									},
									{
										extend : 'csv',
										exportOptions : {
											columns : [ 0, 1, 2, 3, 4, 5, 6, 7,
													8, 9, 10, 11, 12, 13, 14,
													15, 16, 17, 18, 19 ]
										}
									},
									{
										extend : 'pdf',
										exportOptions : {
											columns : [ 0, 1, 2, 3, 4, 5, 6, 7,
													8, 9, 10, 11, 12, 13, 14,
													15, 16, 17, 18, 19, 20, 21,
													22 ]
										}
									}, 'colvis', 'excel', 'print', ],
							scrollY : true,
							scrollX : true,
							searchDelay : 500,
							processing : false,
							destroy : true,
							serverSide : true,
							order : [ [ 5, 'desc' ] ],
							stateSave : true,

							"ajax" : {

								"url" : "exceptionListReport",
								"type" : "POST",
								"timeout" : 0,
								"data" : function(d) {
									return generatePostData(d);
								}
							},
							"fnDrawCallback" : function() {
								$("#submit").removeAttr("disabled");
								$('#loader-wrapper').hide();
							},
							"searching" : false,
							"ordering" : false,
							"processing" : true,
							"serverSide" : true,
							"paginationType" : "full_numbers",
							"lengthMenu" : [ [ 10, 25, 50, 100 ],
									[ 10, 25, 50, 100 ] ],
							"order" : [ [ 2, "desc" ] ],

							"columnDefs" : [ {
								"type" : "html-num-fmt",
								"targets" : 4,
								"orderable" : true,
								"targets" : [ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
										11, 12, 13, 14, 15, 16, 17 ]
							} ],

							"columns" : [
									
									{
										"data" : "transactionIdString",
										"className" : "txnId my_class1 text-class",
										"width" : "2%"
									},
									{
										"data" : "pgRefNum",
										"className" : "payId text-class"

									},
									{
										"data" : "merchants",
										"className" : "text-class"
									},
									{
										"data" : "dateFrom",
										"className" : "text-class"
									},
									{
										"data" : "orderId",
										"className" : "orderId text-class"
									},
									{
										"data" : "refundOrderId",
										"className" : "orderId text-class"
									},
									{
										"data" : "mopType",
										"className" : "mopType text-class"
									},
									{
										"data" : "paymentMethods",
										"render" : function(data, type, full) {
											return full['paymentMethods'] + ' '
													+ '-' + ' '
													+ full['mopType'];
										},
										"className" : "text-class"
									},
									{
										"data" : "txnType",
										"className" : "txnType text-class",
									},
									{
										"data" : "status",
										"className" : "status text-class"
									},
									{
										"data" : "amount",
										"className" : "text-class",
										"render" : function(data) {
											return inrFormat(data);
										}
									},
									{
										"data" : "totalAmount",
										"className" : "text-class",
										"visible" : false,
										"render" : function(data) {
											return inrFormat(data);
										}
									},
									{
										"data" : "payId",
										"visible" : false

									},
									{
										"data" : "customerEmail",
										"className" : "text-class"
									},
									{
										"data" : "customerPhone",
										"className" : "text-class"
									},
									{
										data : "acquirerType"
									},
									{
										data : "ipaddress"
									},
									{
										data : "cardMask"
									},
									{
										data : "rrn"
									},
									{
										data : "splitPayment"
									}]

						});
	
		function downloadExceptionList(){
			dateFrom = document.getElementById("dateFrom").value;
			dateTo = document.getElementById("dateTo").value;
			var transFrom = convert(dateFrom);
			var transTo = convert(dateTo);
			var transFrom1 = new Date(Date.parse(document
					.getElementById("dateFrom").value));
			var transTo1 = new Date(Date.parse(document
					.getElementById("dateTo").value));
			if (transFrom1 == null || transTo1 == null) {
				alert('Enter date value');
				return false;
			}

			if (transFrom1 > transTo1) {
				alert('From date must be before the to date');
				$('#loader-wrapper').hide();
				$('#dateFrom').focus();
				return false;
			}
			if (transTo1 - transFrom1 > 31 * 86400000) {
				alert('No. of days can not be more than 31');
				$('#loader-wrapper').hide();
				$('#dateFrom').focus();
				return false;
			} else {
				
				var payId = document.getElementById("merchant").value;
				var acquirer = document.getElementById("acquirer").value;

				var dateFrom = document.getElementById("dateFrom").value;
				var dateTo = document.getElementById("dateTo").value;
				
				$("#payId").val(payId);
				$("#acq").val(acquirer);
				$("#dFrom").val(dateFrom);
				$("#dTo").val(dateTo);
				
				$("#downloadException").submit();
			}
		}
	</script>
	<script>
		function submitCSV() {
			debugger
	
			var type=$("#type").val();
	
			if(type=="" || type==null || type==undefined){
				alert("Please Select Type Before Submit");
				return false;
			}
	
			$('#btnSubmit').prop('disabled', true);
			var urls = new URL(window.location.href);
			var domain = urls.origin;
			var file = $('#file').val();
			if (!(/\.(xlsx|xls|xlsm)$/i).test(file)) {
				alert('Please upload valid excel file .xlsx, .xlsm, .xls only.');
				$(file).val('');
			} else {
	
				var form = $('#form')[0];
				var data = new FormData(form);
	
				$.ajax({
					url: domain + "/crmws/ExceptionList/ExceptionListsBulkUpload/"+type,
					//url:   "http://localhost:8080/ExceptionList/ExceptionListsBulkUpload/"+type,
					type: 'POST',
					enctype: 'multipart/form-data',
					data: data,
					processData: false,
					contentType: false,
					cache: false,
					success: function (data) {
						alert(data.respmessage);
						window.location.reload();
						$('#btnSubmit').prop('disabled', false);
					},
					error: function (data, textStatus, jqXHR) {
						$('#btnSubmit').prop('disabled', false);
						if (data.responseText
							&& JSON.parse(data.responseText).respmessage) {
							var responseText = JSON.parse(data.responseText)
							alert(responseText.respmessage);
						} else {
							alert("Error while uploading file");
						}
	
	
					}
				});
			}
		}
	</script>
</body>

</html>