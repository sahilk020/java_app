<!DOCTYPE html
	PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@taglib prefix="s" uri="/struts-tags"%>
<html>

<head>
<title>Invoice Event</title>

<link rel="shortcut icon" href="../assets/media/images/paylogo.svg" />
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
.hide {
	display: none;
}

.show {
	display: block;
}

div#message {
	border: 2px solid green;
	padding: 12px 30px 0px 30px;
	border-radius: 10px;
}
</style>
<style>
input[type=number]::-webkit-inner-spin-button, input[type=number]::-webkit-outer-spin-button
	{
	-webkit-appearance: none;
	-moz-appearance: none;
	appearance: none;
	margin: 0;
}

#message{
width: 900px;;
}
#message ul li{
 list-style: none !important;
}
</style>

<script type="text/javascript">
$(document).ready(function(){

  // Initialize select2
	$(".merchants").select2();
});
</script>
<script type="text/javascript">
$(document).ready(function() {
	checkedState();
	generateInvoiceNum();
});
</script>
<script type="text/javascript">

	$(document).ready(function() {

		$("#tnc").change(function() {
			var ttnncc = document.getElementById("tnc").checked;

			if (ttnncc == true) {
				//$("#btnSave").prop('disabled', false);
				$("#single_invoice_submit").show();
			} else {
				//$("#btnSave").prop('disabled', true);
				$("#single_invoice_submit").hide();
			}

		});


		var ttnnccread = document.getElementById("tnc").checked;

		disableInvoiceNo(ttnnccread, true);


	});

</script>
</head>

<body>
	<!--begin::Content-->
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
					<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">Single
						Invoice</h1>
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
						<li class="breadcrumb-item text-muted">Quick Pay Invoice</li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item"><span
							class="bullet bg-gray-200 w-5px h-2px"></span></li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item text-dark">Single Invoice</li>
						<!--end::Item-->
					</ul>
					<!--end::Breadcrumb-->
				</div>
				<!--end::Page title-->

			</div>
			<!--end::Container-->
		</div>
		<!--end::Toolbar-->
		<!--begin::Post-->
		<div class="post d-flex flex-column-fluid" id="kt_post">
			<!--begin::Container-->
			<div id="kt_content_container" class="container-xxl">
				<form action="saveSingleInvoice" class="form mb-15" method="post"
					id="single_invoice_form" enctype="multipart/form-data">
					<%-- <div class="row">
							<!-- <div class="col-md-12"> -->
							<div class="col-sm-6 col-lg-3">
								<div class="col-md-12 fv-row">
									<s:if
										test="%{#session.USER.UserType.name()=='ADMIN' || #session.USER.UserType.name()=='SUBADMIN'  || #session.USER.UserType.name()=='SUBADMIN' || #session.USER_TYPE.name()=='SUPERADMIN'}">
										<label class="d-flex align-items-center fs-6 fw-semibold mb-2">Select
											Merchant:</label>

										<s:select name="merchantPayId" class="form-select form-select-solid"
											id="merchantPayId" list="merchantList" listKey="payId"
											listValue="businessName" autocomplete="off" />
									</s:if>
									<s:else>
										<label class="d-flex align-items-center fs-6 fw-semibold mb-2">
											Merchant:</label>
										<s:select name="merchantPayId" class="form-select form-select-solid"
											id="merchantPayId" list="merchantList" listKey="payId"
											listValue="businessName" autocomplete="off" />
									</s:else>
								</div> <span id="merchantErr" class="invocspan"></span>
							</div>
							<!-- </div> -->
				</div> --%>


					<div class="row my-5" id="mmm">
						<div class="col">
							<div class="card">
								<div class="card-body">
									<div class="row my-3 align-items-center">
										<div class="col-auto my-2 merchant-text">
											<%-- <div id="message" colspan="3" align="left" ">
											<s:iterator value="actionMessages">
												<s:property />
											</s:iterator>
										</div> --%>
											<div id="message" colspan="3" align="center">
												<s:actionmessage id="msg" />
											</div>
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

									<s:if
										test="%{#session.USER.UserType.name()=='ADMIN' || #session.USER.UserType.name()=='SUBADMIN'  || #session.USER.UserType.name()=='SUBADMIN' || #session.USER_TYPE.name()=='SUPERADMIN'}">

										<div class="row my-3 align-items-center ">
											<div class="col-auto my-2 merchant-text ">
												<p class="text-center m-0 w-100">
													<b>Merchant</b>
												</p>
											</div>
											<div class="col-md-4 fv-row">
												<s:select name="merchantPayId"
													class="form-select form-select-solid merchants"
													id="merchantPayId" list="merchantList" listKey="payId"
													onchange="javascript: $('#invoiceNo').val('');checkedState();generateInvoiceNum();" listValue="businessName"
													autocomplete="off" />



											</div>
										</div>
									</s:if>
									<s:else>

										<div class="row my-3 align-items-center ">
											<div class="col-auto my-2 merchant-text ">
												<p class="text-center m-0 w-100">
													<b>Merchant</b>
												</p>
											</div>
											<div class="col-auto my-2 ">
												<s:select name="merchantPayId"
													class="form-select form-select-solid" id="merchantPayId"
													list="merchantList" listKey="payId"
													listValue="businessName" autocomplete="off" />

											</div>
										</div>
									</s:else>

								</div>
							</div>
						</div>
					</div>

					<div class="row my-5">
						<div class="col">
							<h4
								class="page-heading d-flex text-dark fw-bold fs-4 flex-column justify-content-center my-5">
								Customer Information:</h4>
							<div class="card">
								<div class="card-body">
									<!--begin::Input group-->
									<div class="row g-9 mb-8">
										<!--begin::Col-->
										<div class="col-md-4 fv-row">
											<label
												class="d-flex align-items-center fs-6 fw-semibold mb-2">
												<span class="required">Invoice Number</span>
											</label>
											<!--end::Label-->
											<s:textfield type="text"
												class="form-control form-control-solid" id="invoiceNo"
												name="invoiceNo" value="%{invoice.invoiceNo}" maxlength="45"
												minlength="1"/>
										</div>
										<div class="col-md-4 fv-row">
											<label
												class="d-flex align-items-center fs-6 fw-semibold mb-2">
												<span class="required">Name</span>
											</label>
											<!--end::Label-->
											<s:textfield type="text" maxlength="32" minlength="2"
												class="form-control form-control-solid" id="name"
												value="%{invoice.name}" name="name" autocomplete="off"
												onkeypress="if(event.keyCode === 32)return true;return lettersOnly(event,this);" />
										</div>
										<div class="col-md-4 fv-row">
											<label
												class="d-flex align-items-center fs-6 fw-semibold mb-2">
												<span class="required">Phone</span>
											</label>
											<!--end::Label-->
											<s:textfield type="text" id="phone" name="phone"
												maxlength="10" minlength="10" value="%{invoice.phone}"
												class="form-control form-control-solid" autocomplete="off" />
										</div>
									</div>
									<div class="row g-9 mb-8">
										<div class="col-12 fv-row">
											<label
												class="d-flex align-items-center fs-6 fw-semibold mb-2">
												<span class="">Address</span>
											</label>
											<s:textarea type="text" maxlength="150" minlength="3"
												class="form-control form-control-solid" id="address"
												value="%{invoice.address}" name="address" autocomplete="off"
												cols="30" rows="2" />
										</div>
									</div>
									<div class="row g-9 mb-8">
										<!--begin::Col-->
										<div class="col-md-4 fv-row">
											<label
												class="d-flex align-items-center fs-6 fw-semibold mb-2">
												<span class="required">Email ID</span>
											</label>
											<!--end::Label-->
											<s:textfield type="text" id="emailId" name="email"
												maxlength="100" minlength="8" value="%{invoice.email}"
												class="form-control form-control-solid" autocomplete="off" />
										</div>
										<div class="col-md-4 fv-row">
											<label
												class="d-flex align-items-center fs-6 fw-semibold mb-2">
												<span class="">City</span>
											</label>
											<!--end::Label-->
											<s:textfield type="text" maxlength="100" minlength="2"
												class="form-control form-control-solid" id="city"
												value="%{invoice.city}" name="city" autocomplete="off" />
										</div>
										<div class="col-md-4 fv-row">
											<label
												class="d-flex align-items-center fs-6 fw-semibold mb-2">
												<span class="">State</span>
											</label>
											<!--end::Label-->
											<s:textfield type="text" maxlength="100" minlength="2"
												class="form-control form-control-solid" id="state"
												value="%{invoice.state}" name="state" autocomplete="off" />
										</div>
									</div>
									<div class="row g-9 mb-8">
										<!--begin::Col-->
										<div class="col-md-4 fv-row">
											<label
												class="d-flex align-items-center fs-6 fw-semibold mb-2">
												<span class="">Country</span>
											</label>

											<s:select
												list="@com.pay10.commons.util.BinCountryMapperType@values()"
												name="country" id="country" listKey="name" listValue="name"
												class="form-control form-control-solid"
												value="defaultCountry" maxlength="100" minlength="2">
											</s:select>
										</div>
										<div class="col-md-4 fv-row">
											<label
												class="d-flex align-items-center fs-6 fw-semibold mb-2">
												<span class="required">Zip Code</span>
											</label>
											<!--end::Label-->
											<s:textfield type="text"
												class="form-control form-control-solid" id="zip"
												minlength="6" maxlength="10" value="%{invoice.zip}"
												name="zip" autocomplete="off" />
										</div>

										<div class="col-md-4 fv-row">
											<label
												class="d-flex align-items-center fs-6 fw-semibold mb-2">
												<span class="">Return URL</span>
											</label>
											<!--end::Label-->
											<s:textfield type="text"
												class="form-control form-control-solid" id="returnUrl"
												minlength="8" maxlength="300" value="%{invoice.returnUrl}"
												name="returnUrl" autocomplete="off" />
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
					<div class="row my-5">
						<div class="col">
							<h4
								class="page-heading d-flex text-dark fw-bold fs-4 flex-column justify-content-center my-5">
								Product Information:</h4>
							<div class="card">
								<div class="card-body">
									<!--begin::Input group-->
									<div class="row g-9 mb-8">
										<!--begin::Col-->
										<div class="col-md-4 fv-row">
											<label
												class="d-flex align-items-center fs-6 fw-semibold mb-2">
												<span class="required">Name</span>
											</label>
											<!--end::Label-->
											<s:textfield type="text"
												class="form-control form-control-solid" id="productName"
												minlength="1" maxlength="50" value="%{invoice.productName}"
												name="productName" autocomplete="off" />
										</div>
										<div class="col-md-4 fv-row">
											<label
												class="d-flex align-items-center fs-6 fw-semibold mb-2">
												<span class="">Description</span>
											</label>
											<!--end::Label-->
											<s:textfield type="text"
												class="form-control form-control-solid" id="productDesc"
												value="%{invoice.productDesc}" name="productDesc"
												autocomplete="off" minlength="2" maxlength="250" />
										</div>
										<div class="col-md-4 fv-row">
											<label
												class="d-flex align-items-center fs-6 fw-semibold mb-2">
												<span class="required">Expire in days</span>
											</label>
											<!--end::Label-->
											<!-- <input type="text" class="form-control form-control-solid"
																placeholder="0" name="expireindays" /> -->
											<s:textfield type="text"
												class="form-control form-control-solid" id="expiresDay"
												name="expiresDay" autocomplete="off"
												value="%{invoice.expiresDay}" maxlength="2" minlength="1"
												onblur="dayTimeValidation()" onkeydown="dayTimeValidation()"
												onkeyup="dayTimeValidation()" />
										</div>
									</div>
									<div class="row g-9 mb-8">
										<!--begin::Col-->
										<div class="col-md-4 fv-row">
											<label
												class="d-flex align-items-center fs-6 fw-semibold mb-2">
												<span class="required">Expire in Hours</span>
											</label>
											<!--end::Label-->
											<s:textfield type="text"
												class="form-control form-control-solid" id="expiresHour"
												name="expiresHour" autocomplete="off"
												value="%{invoice.expiresHour}" maxlength="2" minlength="1"
												onblur="dayTimeValidation()" onkeydown="dayTimeValidation()"
												onkeyup="dayTimeValidation()" />
										</div>
										<div class="col-md-4 fv-row">
											<label
												class="d-flex align-items-center fs-6 fw-semibold mb-2">

												<span class="">Currency</span>
											</label>


											<s:select name="currencyCode" id="currencyCode" maxlength="3"
												minlength="3" list="currencyMap" listKey="key"
												listValue="value" class="form-select form-select-solid"
												onchange="FieldValidator.valdCurrCode(false);_ValidateField();"
												autocomplete="off" onkeypress="return isNumberKey(event)" />
										</div>

										<div class="col-md-4 fv-row">
                                            <label
                                                class="d-flex align-items-center fs-6 fw-semibold mb-2">
                                                <span class="">Payment Region</span>
                                            </label>
                                            <s:select class="form-select form-select-solid"
                                                list="@com.pay10.commons.user.AccountCurrencyRegions@values()"
                                                name="region" value="%{invoice.region}" id="region" autocomplete="off" />
                                        </div>


										<div class="col-md-4 fv-row">
											<label
												class="d-flex align-items-center fs-6 fw-semibold mb-2">
												<span class="">GST</span>
											</label>
											<!--end::Label-->
											<s:select id="gst" name="gst"
												class="form-control form-control-solid" headerKey="0"
												headerValue="No GST" minlength="1" maxlength="3"
												list="#{'5':'5%', '12':'12%', '18':'18%', '28':'28%'}"
												value="%{invoice.gst}" onchange="sum()"
												onkeypress="return isNumberKey(event)" />
										</div>
									</div>
									<%-- <div class="row g-9 mb-8">
									<!--begin::Col-->
									<div class="col-md-6 fv-row">
										<label class="d-flex align-items-center fs-6 fw-semibold mb-2">
											<span class="">Quantity</span>
										</label>
										<!--end::Label-->
										<input type="text" class="form-control form-control-solid"
											placeholder="Enter Quantity" name="quantity" />
									</div>
									<div class="col-md-6 fv-row">
										<label class="d-flex align-items-center fs-6 fw-semibold mb-2">
											<span class="required">Amount</span>
										</label>
										<!--end::Label-->
										<input type="text" class="form-control form-control-solid" placeholder="Amount"
											name="amount" />
									</div>
							</div>
							<div class="row g-9 mb-8">
								<!--begin::Col-->
								<div class="col-md-12 fv-row">
									<label class="d-flex align-items-center fs-6 fw-semibold mb-2">
										<span class="">Total Amount</span>
									</label>
									<!--end::Label-->
									<input type="text" class="form-control form-control-solid" name="totalamount" />
								</div>
							</div> --%>

									<div class="row g-9 mb-8">
										<!--begin::Col-->
										<div class="col-md-6 fv-row">
											<label
												class="d-flex align-items-center fs-6 fw-semibold mb-2">
												<span class="required">Quantity</span>
											</label>
											<!--end::Label-->
											<!-- <input min="0" type="number" class="form-control form-control-solid" placeholder="Enter Quantity" name="quantity" value="1"/> -->
											<s:textfield type="text"
												class="form-control form-control-solid"
												value="%{invoice.quantity}" maxlength="6" id="quantity"
												name="quantity"
												onkeyup="sum();FieldValidator.valdQty(false);_ValidateField();"
												autocomplete="off" onkeypress="return isNumberKey(event)" />
										</div>
										<div class="col-md-6 fv-row">
											<label
												class="d-flex align-items-center fs-6 fw-semibold mb-2">
												<span class="required">Amount</span>
											</label>
											<!--end::Label-->
											<!-- <input type="text" class="form-control form-control-solid"
															placeholder="Amount" name="amount" /> -->

											<s:textfield type="number"
												class="form-control form-control-solid"
												onkeyup="sum();return isNumberKeyAmount(event);if (this.value.length == 0 && e.which == 48 )
														return false;"
												maxlength="10" id="amount" name="amount"
												value="%{invoice.amount}" autocomplete="off"
												placeholder="0.00" />

										</div>
									</div>
									<div class="row g-9 mb-8">
										<!--begin::Col-->
										<div class="col-md-12 fv-row">
											<label
												class="d-flex align-items-center fs-6 fw-semibold mb-2">
												<span class="">Total Amount</span>
											</label>
											<!--end::Label-->
											<!-- <input type="text" class="form-control form-control-solid" name="totalamount" /> -->
											<%-- <s:textfield type="text" class="form-control form-control-solid"
										onkeypress="gstReverseSet()" id="proInvTotalAmount" minlength="1" maxlength="10"
										onkeydown="gstReverseSet()" name="totalAmount" autocomplete="off"
										ondrop="return isNumberKeyAmount(event)" placeholder="0.00" /> --%>

											<s:textfield type="text"
												class="form-control form-control-solid"
												onkeypress="gstReverseSet()" id="totalAmount" minlength="1"
												maxlength="10" name="totalAmount" autocomplete="off"
												onkeydown="gstReverseSet()"
												ondrop="return false;return isNumberKeyAmount(event)"
												placeholder="0.00" />
										</div>
									</div>

									<div class="row g-9 mb-8">
										<!--begin::Col-->
										<div class="col-md-12 fv-row" style="display: none;">
											<label
												class="d-flex align-items-center fs-6 fw-semibold mb-2"
												style="display: none;"> <span class="">Service
													Charge</span>
											</label>
											<s:textfield type="text" class="textFL_merch"
												value="%{invoice.serviceCharge}" id="serviceCharge"
												name="serviceCharge" placeholder="0.00" maxlength="5"
												onkeyup="sum();FieldValidator.valdSrvcChrg(false);_ValidateField();"
												onkeypress="return isNumberKey(event)" autocomplete="off" />
										</div>
									</div>


									<div class="row g-9 mb-8" id="promoLinkdiv">
										<!--begin::Col-->
										<div class="col-md-8 fv-row">
											<input id="promoLink" type="text"
												class="form-control form-control-solid"
												value='<s:property value="%{invoice.invoiceUrl}" />'>
										</div>
										<div class="col-md-4 fv-row">
											<button type="button" id="copyBtn"
												class="btn btn-primary  mb-2" onclick="copyToClipboard()">Copy
												Payment Link</button>
										</div>
									</div>

									<div class="row g-9 mb-8" id="createBtnDiv">
										<div class="col-md-8 fv-row">
											<input type="button" id="crtBtn"
												class="btn btn-primary"
												value="Create New Invoice" onclick="reloadPage()" />
										</div>
									</div>
									<!-- Added by Sweety -->
									<div id="abc">

										<div style="display: inline-flex; margin-left: 19px;">
											<s:checkbox type="checkbox" name="merchantConsent" id="tnc"
												onclick="javascript: disableInvoiceNo(this.checked);"
												value="%{merchantConsent}"></s:checkbox>
											&nbsp; I accept all &nbsp;<a onclick="modalShow();">
												Terms and conditions*</a>
										</div>

									</div>
									<div class="row g-9 mb-8" id="btnSaveSendDiv">
										<!--begin::Col-->
										<div class="col-md-12 fv-row d-flex justify-content-md-center">
											<button type="button" id="single_invoice_submit"
												class="btn w-lg-25 w-md-75 w-100 btn-primary">
												<span class="indicator-label">Save & Send</span> <span
													class="indicator-progress">Please wait... <span
													class="spinner-border spinner-border-sm align-middle ms-2"></span></span>
											</button>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
					<div>
						<s:hidden id="invoiceType" name="invoiceType"></s:hidden>
						<s:hidden name="token" value="%{#session.customToken}"></s:hidden>
						<s:token name="requestToken" />
					</div>

				</form>
			</div>
			<!--end::Container-->
		</div>
		<!--end::Post-->
	</div>




	<script type="text/javascript">
			$(document).ready(function () {


				if ($('#singleInvoicePage').hasClass("active")) {
					var menuAccess = document.getElementById("menuAccessByROLE").value;
					var accessMap = JSON.parse(menuAccess);
					var access = accessMap["singleInvoicePage"];
					if (access.includes("Add")) {
						$("#btnSave").removeClass("disabled");
					}
				}
			});
		</script>
	<script>
			function lettersOnly(e, t) {
				try {
					if (window.event) {
						var charCode = window.event.keyCode;
					}
					else if (e) {
						var charCode = e.which;
					}
					else { return true; }
					if ((charCode > 64 && charCode < 91) || (charCode > 96 && charCode < 123) || charCode == 8 || charCode == 32)
						return true;
					else
						return false;
				}
				catch (err) {
					alert(err.Description);
				}
			}
			"use strict";
			var KTCareersApply = function () {
				var t, e, i;
				return {
					init: function () {
						i = document.querySelector("#single_invoice_form"),
							t = document.getElementById("single_invoice_submit"),
							e = FormValidation.formValidation(i, {
								fields: {
									invoiceNo: {
										validators: {
											notEmpty: {
												message: "Invoice No is required."
											},
											// stringLength: {
											// 	max: 45,
											// 	min: 1,
											// 	message: 'Enter valid Invoice no.',
											// },
											callback: {
												callback: function (input) {
													if (!input.value.match(/^[0-9a-zA-Z\b\_-\s\+.]{0,45}$/)) {
														return {
															valid: false,
															message: 'Enter valid Invoice no.',
															btnDisable: true
														};
													} else {
														return {
															valid: true,
															btnDisable: false
														}
													}
												}
											}
										}
									},
									name: {
										validators: {
											notEmpty: {
												message: "Name is required."
											},
											stringLength: {
												max: 32,
												min: 2,
												message: 'Name should be 2 to 32 characters.',
											},
											callback: {
												callback: function (input) {
													if (!input.value.match(/^[ A-Za-z]*$/)) {
														return {
															valid: false,
															message: 'Please enter valid name.',
															btnDisable: true
														};
													} else {
														return {
															valid: true,
															btnDisable: false
														}
													}
												}
											}
										}
									},
									phone: {
										validators: {
											notEmpty: {
												message: "Phone Number is required"
											},
											callback: {
												callback: function (input) {
													if (!input.value.match(/^[6-9]\d{9}$/)) {
														return {
															valid: false,
															message: 'Please enter valid phone number.',
															btnDisable: true
														};
													} else {
														return {
															valid: true,
															btnDisable: false
														}
													}
												}
											}
										}
									},
									email: {
										validators: {
											notEmpty: {
												message: "Email is required"
											},
											stringLength: {
												max: 60,
												message: 'Email Id should be less than 60 characters.',
												btnDisable: true
											},
											callback: {
												callback: function (input) {
													if (input.value.length == 0) {
														document.getElementsByClassName("invalid-feedback")[4].style.display = 'none';
													} else {
														document.getElementsByClassName("invalid-feedback")[4].style.display = 'block';

														if (!input.value.match("^[\\w!#$%&’*+/=?`{|}~^-]+(?:\\.[\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$")) {
															return {
																valid: false,
																message: 'Please enter a valid email.',
																btnDisable: true
															};
														}
														else {
															return {
																valid: true,
																btnDisable: false
															}
														}

													}

												}
											}
										}
									},
									zip: {
										validators: {
											notEmpty: {
												message: "Zip code is required"
											},

											callback: {
												callback: function (input) {
													if (!input.value.match(/^[0-9]{6,13}$/)) {
														return {
															valid: false,
															message: 'Please enter valid zip code.',
															btnDisable: true
														};
													} else {
														return {
															valid: true,
															btnDisable: false
														}
													}

												}
											}
										}
									},

									productName: {
										validators: {
											notEmpty: {
												message: "Product name is required."
											},
											callback: {
												callback: function (input) {
													if (!input.value.match(/^[ A-Za-z0-9]*$/)) {
														return {
															valid: false,
															message: 'Please enter valid product name.',
															btnDisable: true
														};
													} else {
														return {
															valid: true,
															btnDisable: false
														}
													}
												}
											}
										}
									},
									quantity: {
										validators: {
											//  notEmpty: {
											//  	message: "Quantity  is required."
											// },
											callback: {
												callback: function (input) {
													if (input.value < 1) {
														return {
															valid: false,
															message: 'Quantity should be greater than zero.',
															btnDisable: true
														};
													} else {
														return {
															valid: true,
															btnDisable: false
														}
													}
												}
											}
										}
									},

									amount: {
										validators: {
											//  notEmpty: {
											//  	message: "Amount  is required."
											// },

											callback: {
												callback: function (input) {
													if (input.value < 1) {
														return {
															valid: false,
															message: 'Amount should be greater than zero.',
															btnDisable: true
														};
													} else {
														//myFunction();

														return {
															valid: true,
															btnDisable: false
														}
													}
												},
											}
										}
									},

									totalAmount: {
										validators: {

										}
									},

									expiresDay: {
										validators: {

										}
									},
									expiresHour: {
										validators: {

										}
									},

								},
								plugins: {
									trigger: new FormValidation.plugins.Trigger,
									bootstrap: new FormValidation.plugins.Bootstrap5({
										rowSelector: ".fv-row",
										eleInvalidClass: "",
										eleValidClass: ""
									})
								}
							});
						t.addEventListener("click", (function (i) {
							i.preventDefault(),

								e && e.validate().then((function (e) {
									if (e == 'Invalid') {
										document.getElementsByClassName("invalid-feedback")[4].style.display = 'block';
									}
									else {
										document.getElementsByClassName("invalid-feedback")[4].style.display = 'none';
										submitFormAndVaidate();
									}
								})
								)
						}
						))
					}
				}
			}();
			KTUtil.onDOMContentLoaded((function () {
				KTCareersApply.init()
			}
			));

			// before form submit and all validation error resolve
			function submitFormAndVaidate() {
				if (dayTimeValidation()) {
					document.getElementById("single_invoice_form").submit();
				}

			}

			// day time validation
			function dayTimeValidation() {
				var expDayElement = document.getElementById("expiresDay");
				var expHorElement = document.getElementById("expiresHour");
				var days = expDayElement.value.trim();
				var hours = expHorElement.value.trim();
				if (days <= 0 && hours <= 0) {
					document.getElementsByClassName("invalid-feedback")[6].innerHTML = "Enter valid no. of days (Max:31)";
					document.getElementsByClassName("invalid-feedback")[6].display = "block";
					document.getElementsByClassName("invalid-feedback")[7].innerHTML = "Enter valid no. of hours.";
					document.getElementsByClassName("invalid-feedback")[7].display = "block";
					return false;
				} else if (days > 0 || hours > 0) {

					//if days or hour is true
					if (days > 31 || days < 0) {
						document.getElementsByClassName("invalid-feedback")[6].innerHTML = "Enter valid no. of days (Max:31)";
						document.getElementsByClassName("invalid-feedback")[6].display = "block";
						return false;
					} else if (hours > 23 || hours < 0) {
						document.getElementsByClassName("invalid-feedback")[7].innerHTML = "Enter valid no. of hours.";
						document.getElementsByClassName("invalid-feedback")[7].display = "block";
						return false;
					} else {
						document.getElementsByClassName("invalid-feedback")[6].innerHTML = "";
						document.getElementsByClassName("invalid-feedback")[6].display = "none";
						document.getElementsByClassName("invalid-feedback")[7].innerHTML = "";
						document.getElementsByClassName("invalid-feedback")[7].display = "block";
						return true;
					}
				}
			}


			// for amount and gst sum
			function sum() {
				var x = document.getElementById("totalAmount");
				if (x.value > 1000000) {
					document.getElementsByClassName("invalid-feedback")[8].innerHTML = "Total amount should be less than 1000000";
					document.getElementsByClassName("invalid-feedback")[8].style.display = 'block';

				}
				else {
					document.getElementsByClassName("invalid-feedback")[8].style.display = 'none';

				}
				var amounts = document.getElementById('amount').value;
				if (amounts == "") {
					amounts = "0.00";
				}
				var gstVal = document.getElementById('gst').value;
				if (serviceChargeVal == "" || serviceChargeVal == "0") {
					serviceChargeVal = "0";
				}

				var serviceChargeVal = document.getElementById('serviceCharge').value;
				if (serviceChargeVal == "" || serviceChargeVal == ".") {
					serviceChargeVal = "0";
				}

				var gstCal = parseFloat(amounts) + parseFloat(parseFloat(amounts) * parseFloat(gstVal)) / 100;
				var Quantity = document.getElementById('quantity').value;
				var result = parseFloat(Quantity) * parseFloat(gstCal);

				if (!isNaN(result)) {
					document.getElementById('totalAmount').value = parseFloat(result + parseFloat(serviceChargeVal)).toFixed(2);

				}

			}

			//  for set gst on totalAmount
			function gstReverseSet() {
				setTimeout(function () {
					var totalAmounts = document.getElementById('totalAmount').value;
					if (totalAmounts == "") {
						totalAmounts = "0.00";
					}
					var amtQuantity = document.getElementById('quantity').value;
					if (amtQuantity == "" || amtQuantity == ".") {
						amtQuantity = "0.00";
					}
					var gstAmounts = document.getElementById('gst').value;
					if (gstAmounts == "28") {
						gstAmounts = "1.28";
					} else if (gstAmounts == "18") {
						gstAmounts = "1.18";
					} else if (gstAmounts == "12") {
						gstAmounts = "1.12";
					} else if (gstAmounts == "5") {
						gstAmounts = "1.05";
					} else {
						gstAmounts = "1";
					}
					if (!isNaN(totalAmounts)) {

						document.getElementById('amount').value = parseFloat((totalAmounts / (gstAmounts * amtQuantity)).toFixed(2));

					}
				}, 100);

			}
			function isNumberKey(evt) {
				var charCode = (evt.which) ? evt.which : event.keyCode
				if (charCode > 31 && (charCode < 48 || charCode > 57))
					return false;
				return true;
			}
			function isNumberKeyAmount(evt) {
				var charCode = (evt.which) ? evt.which : event.keyCode
				if (charCode > 31 && (charCode < 48 || charCode > 57))
					return false;
				return true;
			}
			$(document).ready(function () {
				if (window.location.pathname.substr(9, window.location.pathname.length) == "saveSingleInvoice") {

					var urlCopyLink = document.getElementById(promoLink).value;

					if (urlCopyLink == "") {
						document.getElementById('promoLinkdiv').style = "display:none";
					}
					else {
						document.getElementById('promoLinkdiv').style = "display:block";
						var ArrStr = '<s:property value="actionMessages"/>';
						var str = ArrStr.toString();
					}
					$('#merchant').change(function () {
						changeCurrencyMap();
						$('#spanMerchant').hide();
						$('#currencyCodeloc').hide();
					});

					$('#serviceCharge').on('keyup', function () {
						if (this.value[0] === '.') {
							this.value = '0' + this.value;
						}
					});
				}
			});
			function copyToClipboard() {
				var textBox = document.getElementById("promoLink");
				textBox.select();
				document.execCommand("copy");
			}
		</script>
	<script>

	var check = $("#promoLink").val();
	var check1 = $("#message").val();
	var actionMessages = '<s:property value="actionMessages"/>';

	if (check == 'undefined' || check == null || check == '') {
		$(document).ready(function () {

			$("#promoLink").hide();
			$("#copyBtn").hide();
			$("#crtBtn").hide();

		});

	} else {
		$(document).ready(function () {

			$("#promoLink").show();
			$("#copyBtn").show();
			$("#crtBtn").show();
			document.getElementById('btnSaveSendDiv').style = "display:none";
		});
	}


	if ((check == 'undefined' || check == null || check == '') && actionMessages == '[]') {
		$(document).ready(function () {
			$("#mmm").hide();
			$("#mmm").hide();
		});

	} else {
		$(document).ready(function () {
			$("#mmm").show();
			$("#mmm").show();

		});
	}



</script>
	<script>
function checkedState() {

	var payid = $("#merchantPayId").val();
	var invoiceNo=$("#invoiceNo").val();

	  $.ajax({

	    type: "POST",
	    url: "promotionalInvoiceMessage",

	    data: {
	    	"payid" : payid
	    },
	    success: function(result) {
	     if (result.tncStatus==true) {
			$("#abc").show();
			$("#single_invoice_submit").hide();
		} else {
			$("#abc").hide();
			$("#single_invoice_submit").show();
			var urlCopyLink= $('#promoLink').val();
			if(urlCopyLink!=""){
				document.getElementById('single_invoice_submit').style="display:none";
				}
		}
		if (result.invoiceMessage != null
				&& result.invoiceMessage != "") {
			$("#messageText").text(result.invoiceMessage);
			$("#messageText1").text(result.invoiceMessage);
			var test=$("#messageText").text();
			test = test.replace("#invoiceNo", invoiceNo);
	        $("#messageText").text(test);

		} else {
			$("#messageText").text(
					"There is no Terms and condition");
		}

	    }
	  });
	}
function generateInvoiceNum(){
	var invoiceNo=$("#invoiceNo").val();
	if (invoiceNo && invoiceNo !== null && invoiceNo !== '') {
		return;
	}
	var payid = $("#merchantPayId").val();
 	var date=new Date();
 date= date.getMilliseconds();
	$.post("getInvoiceNumber", {
		payid : payid,
	}, function(result) {
	  var businessName=result.businessName;
	  var invoiceNumber= businessName.concat("-pay10","-",date);
	  document.getElementById("invoiceNo").value=invoiceNumber;
		$('#invoiceNo').attr('readonly', true);
	});
}
</script>

	<div class="modal" id="myModal" role="dialog">
		<input type="hidden" id="messageText1" />
		<div class="modal-dialog">

			<!-- Modal content-->
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal"
						onclick="hideModal();">&times;</button>
					<h4 class="modal-title">Terms & Conditions for Invoice Payment</h4>
				</div>
				<div class="modal-body">
					<p id="messageText"></p>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal"
						onclick="hideModal();">Close</button>
				</div>
			</div>

		</div>
	</div>
	<script>
function modalShow(){
	 $('#myModal').show();
	 }

	 function hideModal(){
	 $('#myModal').hide();
	 }
</script>

	<script>
function reloadPage() {
		var url = new URL(window.location.href).origin
			+ "/crmadmin/jsp/singleInvoicePage";
		window.location.replace(url);
}

//Added By Sweety

function disableInvoiceNo(checked, tncDisable) {
	var text=$("#messageText1").text();

	if(checked){
		if (tncDisable) {
			document.getElementById("tnc").disabled = true;
		}
		$('#invoiceNo').attr('readonly', true);

	}
	else{
		document.getElementById("tnc").disabled = false;
		$('#invoiceNo').attr('readonly', false);
	}
	var x = document.getElementById("invoiceNo").readOnly;
	if(x){

		addInvoiceNo(x,text);
	}

}

function addInvoiceNo(x,text){
	var invoiceNo= $('#invoiceNo').val();
	text = text.replace("#invoiceNo", invoiceNo);
	     $("#messageText").text(text);
}

</script>
<!-- Code added by deep -->
<script type="text/javascript">

	$(document).ready(function() {


		$("#tnc").change(function() {
			var ttnncc = document.getElementById("tnc").checked;

			if (ttnncc == true) {

				$("#btnSave").show();
			} else {
				//$("#btnSave").prop('disabled', true);
				$("#btnSave").hide();
			}

		});


		var ttnnccread = document.getElementById("tnc").checked;
		disableInvoiceNo(ttnnccread, true);


	});


</script>
<!-- Modal -->
  <div class="modal fade" id="myModal" role="dialog">
  	<input type="hidden" id="messageText1"/>
    <div class="modal-dialog">

      <!-- Modal content-->
      <div class="modal-content">
        <div class="modal-header">
          <button type="button" class="close" data-dismiss="modal">&times;</button>
          <h4 class="modal-title">Terms & Condition for Invoice Payment</h4>
        </div>
        <div class="modal-body">
          <p id="messageText"></p>

        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
        </div>
      </div>

    </div>
  </div>
  <script>
function checkedState() {

	var payid = $("#merchantPayId").val();
	var invoiceNo=$("#invoiceNo").val();

	$.post("promotionalInvoiceMessage", {
		payid : payid,
	}, function(result) {
		debugger
		if (result.tncStatus == true) {
			$("#abc").show();
			$("#btnSave").hide();
			$('#invoiceNo').attr('readonly', true);
			generateInvoiceNum();
		} else {
			$("#abc").hide();
			$("#btnSave").show();
			$('#invoiceNo').attr('readonly', false);
			var urlCopyLink= $('#promoLink').val();
			if(urlCopyLink!=""){
				document.getElementById('btnSave').style="display:none";
				}
		}
		if (result.invoiceMessage != null
				&& result.invoiceMessage != "") {
			$("#messageText").text(result.invoiceMessage);
			$("#messageText1").text(result.invoiceMessage);
			var test=$("#messageText").text();
		    test = test.replace("#invoiceNo", invoiceNo);
	        $("#messageText").text(test);
		} else {
			$("#messageText").text(
					"There is no Terms and condition");
		}
	});
}

function generateInvoiceNum(){
	debugger
	var invoiceNo=$("#invoiceNo").val();
	if (invoiceNo && invoiceNo !== null && invoiceNo !== '') {
		return;
	}
	var payid = $("#merchantPayId").val();
 	var date=new Date();
        date= date.getTime();
	$.post("getInvoiceNumber", {
		payid : payid,
	}, function(result) {
		var businessName=result.businessName;
		  console.log(businessName);
		  var bussinessarr = businessName.split(" ");
		  if(bussinessarr[0].length < 8)
			  {
			  businessName = businessName.substring(0,8);
			  }else{
		  		businessName=bussinessarr[0];
			  }
		  console.log(businessName);

	  var invoiceNumber= businessName.concat("-pay10","-",date);
	  document.getElementById("invoiceNo").value=invoiceNumber;
	});
}
</script>

</body>

</html>