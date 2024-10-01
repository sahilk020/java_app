<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="/struts-tags" prefix="s"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Acquirer Routing Rules</title>

<%-- <script type="text/javascript" src="../js/jquery.min.js"></script>

<script src="../js/core/popper.min.js"></script>
<script src="../js/core/bootstrap-material-design.min.js"></script>
<script src="../js/plugins/perfect-scrollbar.jquery.min.js"></script>
<!-- Control Center for Material Dashboard: parallax effects, scripts for the example pages etc -->
<script src="../js/material-dashboard.js?v=2.1.0" type="text/javascript"></script>
<script type="text/javascript" src="../js/sweetalert.js"></script>
<script type="text/javascript" src="../js/offus.js"></script>
<link rel="stylesheet" href="../css/sweetalert.css">
<link href="../css/select2.min.css" rel="stylesheet" />
<script src="../js/jquery.select2.js" type="text/javascript"></script>
<link href="../css/bootstrap.min.css" rel="stylesheet">
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css" integrity="sha384-1BmE4kWBq78iYhFldvKuhfTAU6auU8tT94WrHftjDbrCEXSU1oBoqyl2QvZ6jIW3" crossorigin="anonymous">
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.min.js" integrity="sha384-QJHtvGhmr9XOIpI6YVutG+2QOK9T+ZnN4kzFN1RtK3zEFEIsxhlmWl5/YESvpZ13" crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-ka7Sk0Gln4gmtz2MlQnikT1wXgYsOg+OMhuP+IlRH9sENBO0LRn5q+8nbTov4+1p" crossorigin="anonymous"></script> --%>
<!-- Added By Sweety -->
<meta charset="utf-8" />
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css">
<script src="https://cdn.jsdelivr.net/npm/jquery@3.7.1/dist/jquery.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/popper.js@1.16.1/dist/umd/popper.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/js/bootstrap.bundle.min.js"></script>


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
	<link rel="stylesheet" href="../css/sweetalert.css">
	
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
<script type="text/javascript" src="../js/sweetalert.js"></script>
<script type="text/javascript" src="../js/offus.js"></script>
<link rel="stylesheet" href="../css/sweetalert.css">
<!-- Ended -->
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

[class^="acqcss"] .wwlbl .label {
	color: #666;
	font-size: 12px;
	padding: 2px 0 8px;
	display: block;
	margin: 0;
	text-align: left;
	font-weight: 600;
}

[class^="acqcss"] .wwlbl+br {
	display: none;
}

/*
.card-list-toggle {
	cursor: pointer;
    padding: 8px 12px;
    border: 1px solid #f7d488;
    position: relative;
    background: linear-gradient(60deg, #f8cc6ccf, #f03921e0);
    margin-left: 8px;
    margin-right: 8px;
} */

.card-list-toggle {
    cursor: pointer;
    padding: 8px 12px;
    /* border: 1px solid #f7d488; */
    position: relative;
    background-color: #202f4b
    margin-left: 8px;
    margin-right: 8px;
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

[class*="acqcss"]>div {
	clear: both;
	border-bottom: 1px solid #ccc;
	padding: 0 8px 5px;
	margin: 0 -8px 8px;
}

[class*="acqcss"]>div:last-child {
	border-bottom: none;
	padding-bottom: 0;
	margin-bottom: 0;
}

[class^="acqcss"] input[disabled] {
	color: #bbb;
	display: none;
}

[class^="acqcss"] input[disabled]+label {
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
	margin-left:2px;
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

.acqcss input[type="radio"] {
	vertical-align: top;
	float: left;
	margin: 2px 5px 0 0;
}

.acqcss label {
	vertical-align: middle;
	display: block;
	font-weight: normal;
}

<!--
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
	display: inline-flex;
	font-weight: normal;
}

#onus_section .checkbox, #offus_section .checkbox {
	margin: 0;
}


div#wwgrp_currency{
	margin-top:3px;
}

input#currency{
	margin-right:6px;
}
.select2-container {
	width: 200px !important;
}

.btn:focus {
	outline: 0 !important;
}

#but:hover {
	background-color: red;
	color: white;
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

#dvpy {
	display: inline-block;
}

#acqdiv {
	display: inline-block;
}

table.product-spec th {
	font-size: 14px;
	padding: 8px;
	background: #fbfcfd;
	color: #262424;
}

.offusFormTable {
	display: block;
	margin-left: 8px;
	margin-right: 10px;
}

table.product-spec {
	border: 1px solid #eaeaea;
	border-bottom: 1px solid #dedede;
	background: #eee;
	margin-left: 8px;
	margin-right: 10px;
}

div#wwctrl_region{
	display: flex;
}
div#wwctrl_typeCard {
    display: flex;
}
div.wwgrp {
    margin-right: 3px;
}
table.product-spec {
    border: 1px solid #eaeaea;
    border-bottom: 1px solid #dedede;
     background: #ffff;
    margin-left: 8px;
    margin-right: 10px;
}

td{
padding:8px;
}

.modal{
	margin-top: 200px;
}


</style>
<script type="text/javascript">
	$(document).ready(function() {
		document.getElementById("loading").style.display = "none";

		$('#offus_section .card-list-toggle').trigger('click');
	});
</script>

<script type="text/javascript">
	$(document).ready(function() {

		// Initialize select2
		$("#offus_merchant").select2();
		$("#selectMerchant").select2();

	});
</script>

<script type="text/javascript">
	$(document).ready(function() {

		// Initialize select2
		$("#onus_merchant").select2();
	});
</script>
<script>

</script>
<script>

</script>
</head>
<body>
<s:actionmessage class="success success-text" />
	<div>
		<div id="loading" style="text-align: center;">
			<img id="loading-image" style="width: 70px; height: 70px;"
				src="../image/sand-clock-loader.gif" alt="Sending SMS..." />
		</div>

	<div class="content flex-column" id="kt_content">
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
								<h1
									class="d-flex align-items-center text-dark fw-bold my-1 fs-3">
									Pay-In Rule Engine</h1>
								<!--end::Title-->
								<!--begin::Separator-->
								<span class="h-20px border-gray-200 border-start mx-4"></span>
								<!--end::Separator-->
								<!--begin::Breadcrumb-->
								<ul
									class="breadcrumb breadcrumb-separatorless fw-semibold fs-7 my-1">
									<!--begin::Item-->
									<li class="breadcrumb-item text-muted"><a
										href="home" class="text-muted text-hover-primary">Dashboard</a></li>
									<!--end::Item-->
									<!--begin::Item-->
									<li class="breadcrumb-item"><span
										class="bullet bg-gray-200 w-5px h-2px"></span></li>
									<!--end::Item-->
									<!--begin::Item-->
									<li class="breadcrumb-item text-muted">Merchant Setup</li>
									<!--end::Item-->
									<!--begin::Item-->
									<li class="breadcrumb-item"><span
										class="bullet bg-gray-200 w-5px h-2px"></span></li>
									<!--end::Item-->
									<!--begin::Item-->
									<li class="breadcrumb-item text-dark">Pay-In Rule Engine</li>
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
	<!--begin::Input group-->

	<div class="card" style=" margin-bottom: 20px;">
										<div class="card-body">
												<div class="row my-5 mb-3">

													 <div class="col-md-12">
        <div class="form-check form-check-inline">
            <input class="form-check-input" type="checkbox" id="pgCheckbox" checked disabled>
            <label class="form-check-label" for="pgCheckbox" >Pay-in</label>
        </div>
        <div class="form-check form-check-inline">
            <input class="form-check-input" type="checkbox" id="poCheckbox">
            <label class="form-check-label" for="poCheckbox">Pay-out</label>
        </div>
    </div>
    </div></div></div>

	<div class="card">
										<div class="card-body">
									<div class="row g-9 mb-8">
										<!--begin::Col-->
											<div class="col-md-2">
													<label
														class="d-flex align-items-center fs-6 fw-bold">
														<span class="" style="font-weight: bold;">Merchant :</span>
													</label>

												<!-----Add Merchant Filter To show Rule List----->
													<s:select name="merchants"
														class="form-select form-select-solid" id="selectMerchant"
														headerKey="" headerValue="Select Merchant" listKey="payId"
														listValue="businessName" list="merchantList"
														autocomplete="off" />
												</div>
										</div>

											<div class="row g-9 mb-8">
											<div class="col-md-12">
											<div id="onus_section">
			<div class="card-list-toggle" style="display: none;">
				<strong style="color: #262424;">ON US</strong>
			</div>
			<div class="card-list">
				<s:hidden name="menuAccessForRule" id="menuAccessForRule" value=""></s:hidden>
				<div class="table-responsive" id="onUs_default">

					<table width="100%" border="0" align="center"
						class="product-spec onus_table"
						style="display: none; margin-left: 8px; margin-right: 10px;">
						<tr class="boxheading">
							<th align="150" valign="middle">Merchant</th>
							<th width="200" align="left" valign="middle">Acquirer</th>
							<th width="100" align="left" valign="middle">Currency</th>
							<th width="200" align="left" valign="middle">Payment Type</th>
							<th width="150" align="left" valign="middle">Mop</th>
							<th width="170" align="left" valign="middle">Transaction Type</th>
							<th width="150" align="left" valign="middle">Action</th>
						</tr>
					</table>

					<div class="merchantFilter">
						<label class="labelClass">Create Rules :</label>
						<s:select name="merchants" disabled="disabled"
						    headerKey="" headerValue="Select Merchant"
							class="form-control form-select form-select-solid" id="onus_merchant" listKey="payId"
							listValue="businessName" list="merchantList" autocomplete="off" style="" />
					</div>

					<table width="100%" border="0" align="center"
						class="product-spec onusFormTable">
						<tr class="boxheading">
							<th width="100" align="left" valign="middle">Currency</th>
							<th width="200" align="left" valign="middle">Acquirer</th>
							<th width="200" align="left" valign="middle">Payment Type</th>
							<th width="150" align="left" valign="middle">Mop</th>
							<th width="170" align="left" valign="middle">Transaction Type</th>
							<th width="150" align="left" valign="middle">Action</th>
						</tr>

						<td><s:iterator value="currencyMap">
								<div class="checkbox">
									<label style="display: flex;"> <s:checkbox name="currency"
											fieldValue="%{value}" value="false"></s:checkbox> <s:property
											value="%{value}" />
									</label>
								</div>
							</s:iterator></td>
						<td>

							<div id="dvAcq"></div>

						</td>
						<td>
							<div id="dvpy"></div> <%-- 							<s:iterator value="@com.pay10.commons.util.PaymentType@values()" status="piteratorstatus" var="payment"> --%>
							<!-- 								<div class="checkbox"><label> --> <%-- 									<div> --%>
							<%-- 										<s:checkbox name="paymentType" fieldValue="%{code}"></s:checkbox> --%>
							<%-- 										<s:property/> --%> <%-- 									</div> --%> <!-- 								</label></div> -->
							<%-- 							</s:iterator> --%>

						</td>
						<td align="left" valign="top">
							<div id="dvmop"></div> <%-- 							<s:iterator value="mopList"> --%>
							<!-- 								<div class="checkbox"><label> --> <%-- 									<div> --%>
							<%-- 										<s:checkbox name="mopType" id="%{top+'boxOnUs'}" fieldValue="%{code}" label="%{name}"></s:checkbox> --%>
							<%-- 									</div> --%> <!-- 								</label></div> --> <%-- 							</s:iterator> --%>
						</td>
						<td><s:iterator
								value="transactionTypeList" status="iteratorstatus" var="txn">
								<div class="checkbox">
									<label> <div style="display: flex;">
											<s:checkbox name="txnType" fieldValue="%{top}"
												id="%{top+'boxOnUs'}"></s:checkbox>
											<s:property />
										</div>
									</label>
								</div>
							</s:iterator></td>
						<td>
							<button type="submit" id="onus_submit"
								class="btn btn-primary disabled mb-2" disabled="disabled"
								onclick="getOnUs()">Save</button>

							<button type="button" id="onus_reset"
								class="btn btn-primary disabled">Reset</button>
						</td>
						</tr>
					</table>
				</div>
			</div>
		</div>


		</div>
		</div>



		<div class="row g-9 mb-8">
											<div class="col-md-12">
			<div id="offus_section">
			<div class="card-list-toggle" style="border:#234b7a; background-color: #234b7a;">
				<strong style="color: #ffffff;">Pay-In Rule Engine Setting</strong>
			</div>

			<div class="card-list">

				<div class="table-responsive">
					<div id="del" style="position: relative; left: 8px; top: 20px;"></div>
					<tr class="boxtext">
						<div id="del1"></div>


						<table width="100%" border="0" align="center"
							class="product-spec offus_table" style="display: none">

							<tr >
								<th width="100" align="left" valign="middle">Merchant</th>
								<th width="100" align="left" valign="middle">Currency</th>
								<th width="100" align="left" valign="middle">Payment Type</th>
								<th width="150" align="left" valign="middle">Mop</th>
								<th width="170" align="left" valign="middle">Transaction
									Type</th>
								<th align="left" valign="middle">Acquirer</th>
								<th align="left" valign="middle">Region</th>
								<th align="left" valign="middle">Type</th>
								<th width="150" align="left" valign="middle">Action</th>
							</tr>

						</table>
						<div class="offusFormTable" id="tohide">
							<div class="merchantFilter">
								<label class="labelClass" style="font-weight: bold;">Create Rules :</label>
								<s:select class="form-select form-select-solid" name="merchants" disabled="disabled" headerValue="Select Merchant" headerKey=""
									 id="offus_merchant" listKey="payId"
									listValue="businessName" list="merchantList" autocomplete="off" />
							</div>

							<table width="100%" border="0" align="center"
								class="product-spec">
								<tr class="boxheading">
									<th align="left" valign="middle">Acquirer</th>
									<th width="100" align="left" valign="middle">Currency</th>
									<th width="200" align="left" valign="middle">Payment Type</th>
									<th width="100" align="left" valign="middle">Mop</th>
									<th width="170" align="left" valign="middle">Transaction Type</th>
									<th width="left" valign="middle">Region</th>
									<th align="left" valign="middle">Type</th>
									<th width="150" align="left" valign="middle">Action</th>
								</tr>
								<tr class="boxtext">
									<!-- ACQUIRER -->
									<td align="left" valign="top" style="width: 10%;">

										<div class='AcquirerList'>

											<div class='Acquirer1'>
												<label for="Acquirer1" id="Acqui"></label>
												<div id="acqdiv"></div>
											</div>
										</div>
										<button type="button" class="btn btn-primary acquirerCloneBtn"
											style="display: none;">Add</button>

										<div id="blok" style="display: none;">
											<div class='AcquirerListraw'>

												<div class='Acquirer1'>
													<label for="Acquirer1" id="Acqui"></label>
													<div id="acqdivraw"></div>
												</div>
											</div>
											<button type="button"
												class="btn btn-primary acquirerCloneBtn"
												style="display: none;">Add</button>
										</div>
									</td>

									<!-- CURRENCY -->
									<td align="left" valign="top" style="width: 20%;">
										<div id="currencyList">

										</div>
									</td>

									<!-- PAYMENT-TYPE -->
									<td align="left" valign="top" style="width: 20%;">
										<div class="acquirerclass" id="paytype1"></div>

									</td>

									<!-- MOP-TYPE -->
									<td align="left" valign="top" style="width: 20%;">
										<div id="mopid1"></div>
									</td>

									<!-- TRANSACTION-TYPE -->
									<td align="left" valign="top"><s:iterator
											value="transactionTypeList" status="iteratorstatus" var="txn">
											<div class="checkbox">
												<label> <%-- <s:property value="%{#iteratorstatus.count}"/> --%>
													<div style="
													display: flex;
												">
														<s:checkbox name="txnType" fieldValue="%{top}"
															id="%{top+'boxOffUs'}"></s:checkbox>
														<s:property />
													</div>
												</label>
											</div>
										</s:iterator></td>

									<!-- PAYMENT-REGION -->
									<td align="left" valign="top" style="width: 13%;">
										<div class="OtherList">
											<div>
												<s:radio list="#{'INTERNATIONAL':'International','DOMESTIC':'Domestic'}"
													name="region" id="region" />
											</div>
										</div>


									</td>

									<!-- TYPE -->
									<td align="left" valign="top" style="width: 13%;">
										<div class="OtherList">
											<div>
												<s:radio
													list="#{'CONSUMER':'Consumer','COMMERCIAL':'Commercial'}"
													name="typeCard" id="typeCard" />
											</div>
										</div>


									</td>

									<!-- ACTION -->
									<td align="left" valign="top" style="width: 13%;">
										<button type="submit" id="offus_submit" style="width: 81px;"
											class="btn btn-primary disabled mb-2" disabled="disabled"
											onclick="getOffUs()">Save</button>

										<button type="button" id="offus_reset" style="width: 81px;display:none;"
											class="btn btn-primary disabled mb-2">Reset</button>
									</td>
								</tr>
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
			<!--end::Container-->
		</div>
				<s:hidden name="token" value="%{#session.customToken}"></s:hidden>

		<!--end::Post-->
	</div>


		<!-- The Modal -->
		<div class="modal fade" id="myModal">
		  <div class="modal-dialog modal-lg">
			<div class="modal-content">

			  <!-- Modal Header -->
			  <div class="modal-header">
				<h4 class="modal-title">Rule Engine Acquirer Smart Routing</h4>
				<button type="button" class="close" data-dismiss="modal">&times;</button>
			  </div>

			  <!-- Modal body -->
			  <div class="modal-body">
				<div class="col-sm-6 col-lg-8 ">
					<label class=" align-items-center fw-semibold"> Select Acquirer</label><br>

					  <select name="acquirerFinal" class="form-select form-select-solid"  id="acquirerFinal"
						  autocomplete="off" multiple>
					</select>

					<div class="" style="margin-top: 5px;">
						<input class="form-control form-control-solid" id="selectedAcquirerList" disabled="true"  style="width: 550px;">
					</div>


			   </div>
			  </div>

			  <!-- Modal footer -->
			  <div class="modal-footer">
				<button type="button" class="btn btn-primary updateRuleData"  onclick="updateRuleEngine()" >Update</button>
				<button type="button" class="btn btn-danger" data-dismiss="modal">Close</button>
			  </div>

			</div>
		  </div>
		</div>



	<script type="text/javascript">
$(document).ready(function() {
	if ($('#ruleEngine').hasClass("active")) {
		var menuAccess = document.getElementById("menuAccessByROLE").value;
		var accessMap = JSON.parse(menuAccess);
		var access = accessMap["ruleEngine"];
		document.getElementById("menuAccessForRule").value= access;
		if (access.includes("Add")) {
			$("#offus_submit").removeAttr("disabled");
			$("#onus_submit").removeAttr("disabled");
		}
		if (access.includes("View")) {
			$("#onus_merchant").removeAttr("disabled");
			$("#offus_merchant").removeAttr("disabled");
		}
	}
});
</script>

<script>
		document.getElementById('poCheckbox').addEventListener('change',
				function() {
					if (this.checked) {
						window.location.href = 'ruleEnginePayout';
					}
				});
	</script>
	<%-- </s:iterator> --%>
</body>
</html>