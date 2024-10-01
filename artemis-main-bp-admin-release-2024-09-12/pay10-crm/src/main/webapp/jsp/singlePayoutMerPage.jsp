<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="/struts-tags" prefix="s"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.Locale"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.text.Format"%>


<html dir="ltr" lang="en-US">

<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Request Payout</title>

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
<!-- <script src="../js/loader/main.js"></script> -->
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

<style type="text/css">
[class^="Acquirer"] .wwlbl .label {
	color: #666;
	font-size: 12px;
	padding: 2px 0 8px;
	display: block;
	margin: 0;
	text-align: left;
	font-weight: 600;
}

[class^="Acquirer"] .wwlbl+br {
	display: none;
}

.card-list-toggle {
	cursor: pointer;
	padding: 8px 12px;
	border: 1px solid #496cb6;
	position: relative;
	background: linear-gradient(60deg, #425185, #4a9b9b);
}

.card-list-toggle:before {
	position: absolute;
	right: 10px;
	top: 7px;
	content: "\f078";
	font-family: 'FontAwesome';
	font-size: 15px;
}

.dt-buttons {
	margin-top: 35px !important;
}

div#example_filter {
	margin-top: 35px !important;
}

.dt-button-collection a.dt-button.buttons-columnVisibility {
	background: unset;
	background-color: rgb(143, 162, 214) !important;
}

.dt-button-collection a.dt-button.buttons-columnVisibility.active {
	background-color: #2d4c5c !important;
}

.card-list-toggle.active:before {
	content: "\f077";
}

.card-list {
	display: none;
}

.acquirerRemoveBtn {
	float: left;
}

[class*="AcquirerList"]>div {
	clear: both;
	border-bottom: 1px solid #ccc;
	padding: 0 8px 5px;
	margin: 0 -8px 8px;
}

[class*="AcquirerList"]>div:last-child {
	border-bottom: none;
	padding-bottom: 0;
	margin-bottom: 0;
}

[class^="AcquirerList"] input[disabled] {
	color: #bbb;
	display: none;
}

[class^="AcquirerList"] input[disabled]+label {
	color: #bbb;
	display: none;
}

[class*="OtherList"]>div {
	clear: both;
	border-bottom: 1px solid #ccc;
	padding: 0 8px 5px;
	margin: 0 -8px 8px;
}

[class*="OtherList"]>div:last-child {
	border-bottom: none;
	padding-bottom: 0;
	margin-bottom: 0;
}

[class^="OtherList"] input[disabled] {
	color: #bbb;
	display: none;
}

[class^="OtherList"] input[disabled]+label {
	color: #bbb;
	display: none;
}

.sweet-alert .sa-icon {
	margin-bottom: 30px;
}

.sweet-alert .lead.text-muted {
	font-size: 14px;
}

.sweet-alert .btn {
	font-size: 12px;
	padding: 8px 30px;
	margin: 0 5px;
}

table.product-spec.disabled {
	cursor: not-allowed;
	opacity: 0.5;
}

table.product-spec.disabled .btn {
	pointer-events: none;
}

.merchantFilter {
	padding: 15px 0;
	width: 200px;
}

.AcquirerList input[type="radio"] {
	vertical-align: top;
	float: left;
	margin: 2px 5px 0 0;
}

.AcquirerList label {
	vertical-align: middle;
	display: block;
	font-weight: normal;
}

<
!--
.Acquirer1 input[type="radio"] {
	vertical-align: top;
	float: left;
	margin: 2px 5px 0 0;
}

.Acquirer1 label {
	vertical-align: middle;
	display: block;
	font-weight: normal;
}

-->
.boxtext td div input[type="radio"] {
	vertical-align: top;
	float: left;
	margin: 2px 5px 0 0;
}

.boxtext td div label {
	vertical-align: middle;
	display: block;
	font-weight: normal;
}

#onus_section .checkbox, #offus_section .checkbox {
	margin: 0;
}

.checkbox .wwgrp input[type="checkbox"] {
	margin-left: 0;
}

.checkbox label .wwgrp input[type="checkbox"] {
	margin-left: -20px;
}

.select2-container {
	width: 200px !important;
}

.btn:focus {
	outline: 0 !important;
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

@media ( min-width : 768px) {
	.navbar>.container .navbar-brand, .navbar>.container-fluid .navbar-brand
		{
		margin-left: 0px !important;
	}
}

.dropdown-menu>li>a:focus, .dropdown-menu>li>a:hover {
	color: #ffffff !important;
	text-decoration: none;
	background-color: #496cb6 !important;
}

input#reportrange {
	border: unset;
}
span.select2-selection.select2-selection--single.form-select.form-select-solid{
width:136% !important;
}
</style>
<style>
.dt-buttons {
	display: none;
}
</style>
<%
DecimalFormat d = new DecimalFormat("0.00");
Date d1 = new Date();
SimpleDateFormat df = new SimpleDateFormat("dd-MM-YYYY");
String currentDate = df.format(d1);
%>

<script type="text/javascript">
	
</script>

<style>
@media ( min-width : 992px) {
	.col-lg-3 {
		max-width: 30% !important;
	}
}
</style>

<script>
	$(document).ready(function() {
		$("#merchant").select2();
		$("#acquirer").select2();
		$("#currency").select2();
		$("#mode").select2();
		$("#channel").select2();
		$("#bank").select2();
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

				<!--end::Page title-->

			</div>
			<!--end::Container-->
		</div>

		<div class="post d-flex flex-column-fluid" id="kt_post">
			<!--begin::Container-->
			<div id="kt_content_container" class="container-xxl">

				<div data-kt-swapper="true" data-kt-swapper-mode="prepend"
					data-kt-swapper-parent="{default: '#kt_content_container', 'lg': '#kt_toolbar_container'}"
					class="page-title d-flex align-items-center me-3 flex-wrap mb-5 mb-lg-0 lh-1">
					<!--begin::Title-->
					<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">Request Payout</h1>

					<span class="h-20px border-gray-200 border-start mx-4"></span>

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
						<li class="breadcrumb-item text-muted">Payout</li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item"><span
							class="bullet bg-gray-200 w-5px h-2px"></span></li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item text-dark">Request Payout</li>
						<!--end::Item-->
					</ul>
					<!--end::Breadcrumb-->
				</div>
				<!--end::Page title-->
				<div class="row my-5">
					<div class="col">
						<div class="card">
							<div class="card-body">

								<div class="row g-9 mb-8">
									<!--begin::Col-->
									<div class="col">

										<div class="card-body ">

											<div class="row">

												<div class="col-lg-3" style="display: none;">
													<label
														class="d-flex align-items-center fs-6 fw-semibold mb-2">Merchant<i style="color: red;">*</i></label>
													<s:select name="merchant"
														class="form-select form-select-solid" id="merchant"
														listKey="emailId" listValue="businessName"
														list="merchantList" />
												</div>

												<div class="col-lg-3">
													<label
														class="d-flex align-items-center fs-6 fw-semibold mb-2">Currency<i style="color: red;">*</i></label>
													<s:select onchange="checkCurrency()" headerKey="ALL" headerValue="Select Currency"
														list="currencyList" class="form-select form-select-solid"
														id="currency" listKey="code" listValue="name"
														name="currency" value="currency" />
												</div>

												<div class="col-lg-3">
													<label
														class="d-flex align-items-center fs-6 fw-semibold mb-2">Bank<i style="color: red;">*</i></label>

														<s:select headerKey="Select Bank" headerValue="Select Bank"
														list="bankDetails" class="form-select form-select-solid"
														id="bank"
														name="bank" value="bank" />

												</div>

												<div class="col-lg-3">
													<label
														class="d-flex align-items-center fs-6 fw-semibold mb-2">Mode</label>
													<select disabled class="form-select form-select-solid" id="mode"
														name="mode">
														<option value="">Select Mode</option>
														<option value="IMPS">IMPS</option>
														<option value="NEFT">NEFT</option>
													</select>
												</div>

												<div class="col-lg-3">
													<label
														class="d-flex align-items-center fs-6 fw-semibold mb-2">Channel<i style="color: red;">*</i></label>
													<select class="form-select form-select-solid" id="channel"
														name="channel">
														<option value="">Select Channel</option>
														<option value="FIAT">FIAT</option>
													</select>
												</div>


											</div>

											<br>

											<div class="row">

											<div class="col-md-3">
													<label
														class="d-flex align-items-center fs-6 fw-semibold mb-2">Account Number<i style="color: red;">*</i></label>
													<s:textfield id="accountNumber"
														class="form-control form-control-solid" placeholder="Enter Account Number"
														name="accountNumber" type="text" maxlength="34" value="" onkeypress="return isNumberKey(event)"
														autocomplete="off"></s:textfield>
												</div>

												<div class="col-md-3">
													<label
														class="d-flex align-items-center fs-6 fw-semibold mb-2">Account
														Holder Name<i style="color: red;">*</i></label>
													<s:textfield id="accountHoldeName" placeholder="Enter Account Holder Name"
														class="form-control form-control-solid" onkeypress="return isTextKey(event)"
														name="accountHoldeName" type="text" value=""
														autocomplete="off"></s:textfield>
												</div>

												<div class="col-md-3">
													<label
														class="d-flex align-items-center fs-6 fw-semibold mb-2">IFSC<i style="color: red;">*</i></label>
													<s:textfield id="ifsc"
														class="form-control form-control-solid" placeholder="Enter IFSC"
														name="ifsc" type="text" value=""
														autocomplete="off"></s:textfield>
												</div>

												<div class="col-md-3">
													<label
														class="d-flex align-items-center fs-6 fw-semibold mb-2">Amount<i style="color: red;">*</i></label>
													<s:textfield id="amount"
														class="form-control form-control-solid" name="amount" placeholder="Enter Amount"
														type="number" value="" autocomplete="off"
														ondrop="return false;" maxlength="50"></s:textfield>
												</div>
												</div>
												<br>
												<div class="row">

												<div class="col-md-3">
													<label
														class="d-flex align-items-center fs-6 fw-semibold mb-2">City<i style="color: red;">*</i></label>
													<s:textfield id="city"
														class="form-control form-control-solid" name="city" onkeypress="return isTextKey(event)"
														type="text" value="" autocomplete="off" placeholder="Enter City"
														ondrop="return false;" maxlength="100"></s:textfield>
												</div>

												<div class="col-md-3">
													<label
														class="d-flex align-items-center fs-6 fw-semibold mb-2">Account Province<i style="color: red;">*</i></label>
													<s:textfield id="accountProvince"
														class="form-control form-control-solid" name="accountProvince" placeholder="Enter Account Province"
														type="text" value="" autocomplete="off" onkeypress="return isTextKey(event)"
														ondrop="return false;" maxlength="100"></s:textfield>
												</div>

												<div class="col-md-3">
													<label
														class="d-flex align-items-center fs-6 fw-semibold mb-2">Bank Branch Name<i style="color: red;">*</i></label>
													<s:textfield id="bankBranch"
														class="form-control form-control-solid" name="bankBranch" placeholder="Enter Branch Name"
														type="text" value="" autocomplete="off" onkeypress="return isTextKey(event)"
														ondrop="return false;" maxlength="100"></s:textfield>
												</div>

												<div class="col-md-3">
													<label
														class="d-flex align-items-center fs-6 fw-semibold mb-2">Email<i style="color: red;">*</i></label>
													<s:textfield id="email"
														class="form-control form-control-solid" name="email"
														type="text" value="" autocomplete="off" placeholder="Enter Email"
														ondrop="return false;" maxlength="100"></s:textfield>
												</div>

												</div>
												<br>
												<div class="row">
												<div class="col-md-3">
													<label
														class="d-flex align-items-center fs-6 fw-semibold mb-2">Phone Number<i style="color: red;">*</i></label>
													<s:textfield id="phone"
														class="form-control form-control-solid" name="phone" placeholder="Enter Phone Number"
														type="text" value="" autocomplete="off"  onkeypress="return isNumberKey(event)"
														ondrop="return false;" maxlength="10"></s:textfield>
												</div>

												<div class="col-md-3">
													<label
														class="d-flex align-items-center fs-6 fw-semibold mb-2">Return URL<i style="color: red;">*</i></label>
													<s:textfield id="returnURL"
														class="form-control form-control-solid" name="returnURL"
														type="text" value="" autocomplete="off" placeholder="Enter Return URL"
														ondrop="return false;" maxlength="100"></s:textfield>
												</div>

												<div class="col-md-3">
													<label
														class="d-flex align-items-center fs-6 fw-semibold mb-2">Remarks<i style="color: red;">*</i></label>
													<s:textfield id="remarks"
														class="form-control form-control-solid" name="remarks"
														type="text" value="" autocomplete="off" placeholder="Enter Remarks"
														ondrop="return false;" maxlength="100"></s:textfield>
												</div>

												<s:hidden name="token" value="%{#session.customToken}" />

												<div class="col-md-3 mt-7">
													<button type="button" class="btn btn-primary submitbtn"
														id="SubmitButton" onclick="submit()">Submit</button>
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

	</div>


	<!-- <div id="kt_content_container" class="container-xxl">
		<div class="row my-5">
			<div class="col">
				<div class="card">
					<div class="card-body">
						begin::Input group
						<div class="row g-9 mb-8">
							begin::Col
							<div class="col">

								<div class="row g-9 mb-8">

									<div class="table-responsive dataTables_wrapper dt-bootstrap4 no-footer">
										<table id="acquireBalance"
											class="display table table-striped table-row-bordered gy-5 gs-7"
											style="width: 100%">
											<thead>
												<tr class="boxheadingsmall fw-bold fs-6 text-gray-800">
													<th>Merchant Name</th>
													<th>Submitted Date</th>
													<th>Updated Date</th>
													<th>Currency</th>
                                                    <th>Amount</th>
													<th>Status</th>
												</tr>
											</thead>
										</table>
									</div>

								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div> -->

	<script type="text/javascript">

		$(document).ready(function(){
			// renderTableAcquirer();
		});

		function submit() {

		var merchant = $("#merchant").val();
		var currency = $("#currency").val();
		var bank = $("#bank").val();
		var mode = $("#mode").val();
		var channel = $("#channel").val();
		var accountNumber=$("#accountNumber").val();
		var holderName = $("#accountHoldeName").val();
		var ifsc = $("#ifsc").val();
		var amount = $("#amount").val();

		var city = $("#city").val();
		var accountProvince = $("#accountProvince").val();
		var bankBranch = $("#bankBranch").val();
		var email = $("#email").val();
		var phone = $("#phone").val();
		var returnURL = $("#returnURL").val();

		var remarks=$("#remarks").val();

		if (currency == "ALL") {
			alert("Please select Currency");
			return false;
		}

		if (bank == ""||bank=="Select Bank") {
			alert("Please select Bank");
			return false;
		}

		//if (mode == "") {
		//	alert("Please select Mode");
		//	return false;
		//}

		if (channel == "") {
			alert("Please select Channel");
			return false;
		}

		if (accountNumber == "") {
			alert("Please enter account number");
			return false;
		}

		if (accountNumber.length < 6) {
			alert("Length should be greater than 6");
			return false;
		}

		if (accountNumber.length > 36) {
			alert("Length should be less than 36");
			return false;
		}

		if (holderName == "") {
			alert("Please enter account holder name");
			return false;
		}

		const holderNameRegex = /^\s+$/;

		if (holderNameRegex.test(holderName)) {
			alert("Whitespace cannot be entered in Account Holder Name");
			return false;
		}

		if (ifsc == "") {
			alert("Please enter Ifsc code");
			return false;
		}


		var ifscregex = /^[A-Z]{4}0[A-Z0-9]{6}$/;
		if (!ifscregex.test(ifsc)) {
        	alert("Please enter valid IFSC code");
        	return false;
     	 }

     	 if (amount == "e" || amount == "E") {
            alert("Character not allowed");
            return false;
         }

     	var reg = new RegExp(/^\d+\.?\d*$/);
        if(!reg.test(amount)){
           alert("Invalid Amount");
           return false;
        }


		if (amount == "") {
			alert("Please enter amount");
			return false;
		}

		if(amount == 0){
			alert("Amount should be greater that Zero");
			return false;
		}

		if(amount < 0){
			alert("Amount should not be negative");
			return false;
		}

		var reg = new RegExp(/^\d+$/);
        if(!reg.test(amount)){
            alert("Decimal is not allowed in Amount");
            return false;
        }


		if (city == "") {
			alert("Please enter city");
			return false;
		}

		if (holderNameRegex.test(city)) {
			alert("Whitespace cannot be entered in City");
			return false;
		}

		if (accountProvince == "") {
			alert("Please enter account province");
			return false;
		}

		if (bankBranch == "") {
			alert("Please enter bank branch name");
			return false;
		}

		if (bankBranch.length < 3) {
			alert("Length should be greater");
			return false;
		}

		if (email == "") {
			alert("Please enter email id");
			return false;
		}

		if(!validateEmail(email)){
			alert("Please enter valid email");
			return false;
		}

		if (phone == "") {
			alert("Please enter phone number");
			return false;
		}

		if (returnURL == "") {
			alert("Please enter return URL");
			return false;
		}

		if (returnURL.length < 5) {
			alert("Please Enter correct URL");
			return false;
		}
		if (returnURL.length > 1024) {
        	alert("Please Enter correct URL");
        	return false;
        }

        var expression = /[-a-zA-Z0-9@:%_\+.~#?&//=]{2,256}\.[a-z]{2,4}\b(\/[-a-zA-Z0-9@:%_\+.~#?&//=]*)?/gi;
        var regex = new RegExp(expression);
        if(!regex.test(returnURL)){
            alert("Please Enter valid URL");
            return false;
        }

		if (remarks == "") {
			alert("Please enter Remarks");
			return false;
		}

		if (holderNameRegex.test(remarks)) {
			alert("Whitespace cannot be entered in Remark");
			return false;
		}


		$.post("createSettlement", {
				"settlementDTO.payId" : merchant,
				"settlementDTO.currency" : currency,
				"settlementDTO.bankName" : bank,
				"settlementDTO.channel" : channel,
				"settlementDTO.mode" : mode,
				"settlementDTO.accountNo" : accountNumber,
				"settlementDTO.accountHolderName" : holderName,
				"settlementDTO.ifsc" : ifsc,
				"settlementDTO.amount" : amount,
				"settlementDTO.city" : city,
				"settlementDTO.accountProvince" : accountProvince,
				"settlementDTO.bankBranch" : bankBranch,
				"settlementDTO.email" : email,
				"settlementDTO.phone" : phone,
				"settlementDTO.returnURL" : returnURL,
				"settlementDTO.remarks" : remarks,
			}, function(result) {
				alert(result.response);
				location.reload();
				//renderTableAcquirer();

			});

		}

		// function renderTableAcquirer() {

		// 	$("#acquireBalance").DataTable().destroy();

		// 	var token = document.getElementsByName("token")[0].value;
		// 	var buttonCommon = {
		// 		exportOptions : {
		// 			format : {
		// 				body : function(data, column, row, node) {
		// 					return data;
		// 				}
		// 			}
		// 		}
		// 	};

		// 	$('#acquireBalance').DataTable({
		// 		dom : 'BTftlpi',
		// 		scrollY : true,
		// 		scrollX : true,

		// 		"ajax" : {
		// 			"url" : "settlementMerchantRequest",
		// 			"type" : "POST",
		// 			"data" : function(d) {
		// 				return generateAcquirerData(d);
		// 			}
		// 		},

		// 		"searching" : false,
		// 		"ordering" : false,
		// 		"processing" : true,
		// 		"serverSide" : false,
		// 		"paginationType" : "full_numbers",
		// 		"lengthMenu" : [ [ 10, 25, 50, 100 ], [ 10, 25, 50, 100 ] ],
		// 		"order" : [ [ 2, "desc" ] ],

		// 		"aoColumns" : [  {
		// 			"mData" : "merchantName"
		// 		}, {
		// 			"mData" : "submitDate"
		// 		}, {
		// 			"mData" : "settlementDate"
		// 		}, {
		// 		    "mData" : "currency"
		// 		},{
		// 			"mData" : "amount"
		// 		},{
		// 			"mData" : "status"
		// 		},

		// 		]

		// 	});

		// }

		function generateAcquirerData(d) {
			var merchant = $("#merchant").val();
			var currency = $("#currency").val();
			var token = document.getElementsByName("token")[0].value;

			var obj = {
					merchant : merchant,
					currency:currency,
					token : token,
					"struts.token.name" : "token",
			}
			return obj;
		}

		function checkCurrency(){
  			if(document.getElementById('currency').value!='356')
    			document.getElementById('mode').disabled=true;
 			 else
    			document.getElementById('mode').disabled=false;
		}

		function isNumberKey(evt) {
     	var charCode = (evt.which) ? evt.which : evt.keyCode;
    	return (charCode >= 48 && charCode <= 57);
		}

		function isTextKey(event) {
			var regex = new RegExp("^[a-zA-Z ]+$");
    	var key = String.fromCharCode(!event.charCode ? event.which : event.charCode);
    		if (!regex.test(key)) {
       	 event.preventDefault();
        	return false;
   		 }
		}

		function validateEmail(email) {
    const emailRegex = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    return emailRegex.test(email);
}

	</script>

</body>

</html>