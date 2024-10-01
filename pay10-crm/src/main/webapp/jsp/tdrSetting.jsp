<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib prefix="s" uri="/struts-tags"%>
<html>

<head>
<title>TDR Setting</title>
<!-- <link href="../css/default.css" rel="stylesheet" type="text/css" />
		<link rel="stylesheet" type="text/css" media="all" href="../css/daterangepicker-bs3.css" />
		<link href="../css/Jquerydatatableview.css" rel="stylesheet" />
		<link href="../css/jquery-ui.css" rel="stylesheet" />
		<link href="../css/Jquerydatatable.css" rel="stylesheet" />
		<script src="../js/jquery.min.js" type="text/javascript"></script>
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

		<link href="../css/select2.min.css" rel="stylesheet" />
		<script src="../js/jquery.select2.js" type="text/javascript"></script> -->

<!-- <title>Exception Report</title> -->
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
<link rel="stylesheet" href="../css/tdrSetting.css">

</head>

<body id="kt_body"
	class="header-fixed header-tablet-and-mobile-fixed toolbar-enabled toolbar-fixed toolbar-tablet-and-mobile-fixed aside-enabled aside-fixed"
	style="-kt-toolbar-height: 55px;  -kt-toolbar-height-tablet-and-mobile: 55px">
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
					   Pay-in TDR And Surcharge Setting</h1>
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
						<li class="breadcrumb-item text-muted">Merchant Billing</li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item"><span
							class="bullet bg-gray-200 w-5px h-2px"></span></li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item text-dark">Pay-in TDR And Surcharge Setting</li>
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
				<s:form id="chargingdetailform" action="tdrSettingAction"
					method="post">
					<div class="row my-5">
						<div class="col">
							<div class="card">
							
							
							
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
							
							
							
							
							
							
								<div class="card-body">
									<div class="row my-3 align-items-center">

										<div class="col-md-3 fv-row">
											<label class="d-flex align-items-center fs-6 fw-bold mb-2">
												<span class="">Select Merchant</span>
											</label>
											<s:select headerValue="Select Merchant" headerKey=""
												name="emailId" class="form-select form-select-solid"
												id="merchants" list="listMerchant" listKey="emailId"
												listValue="businessName" autocomplete="off"
												style="margin-left: -4px;" />

										</div>
										<div class="col-md-3 fv-row" id="acquirerDropdown">
											<label class="d-flex align-items-center fs-6 fw-bold mb-2">
												<span class="">Select Acquirer</span>
											</label>
											<s:select class="form-select form-select-solid"
											    headerKey="" headerValue="Select Acquirer"
												list="acquirerList" name="acquirer" id="acquirer"
												autocomplete="off" />

										</div>

										<div class="col-md-3 fv-row" id="currencyListDropdown">
											<label class="d-flex align-items-center fs-6 fw-bold mb-2">
												<span class="">Select Currency</span>
											</label>
											<s:select class="form-select form-select-solid"
												 name="currencyData" id="currency"
												headerKey="" headerValue="Select Currency"
												 autocomplete="off" list="currencyMap" listKey="code" listValue="name"
												style="margin-left: -4px;" />

										</div>
										<div class="col-md-3 fv-row">
											<label class="d-flex align-items-center fs-6 fw-bold mb-2">
												<span class="">Select Region</span>
											</label>
											<s:select class="form-select form-select-solid" headerKey=""
												headerValue="Select Region"
												list="@com.pay10.commons.user.AccountCurrencyRegions@values()"
												name="paymentRegion" id="paymentRegion" autocomplete="off" />
										</div>

										<div class="col-md-3 fv-row"  style="margin-top: 14px;">
											<label
												class="d-flex align-items-center fs-6 fw-bold mb-2">
												<span class="">Select Type</span>
											</label>
											<s:select class="form-select form-select-solid" headerKey=""
												headerValue="Select Type"
												list="@com.pay10.commons.user.CardHolderTypes@values()"
												name="cardHolderType" id="cardHolderType" autocomplete="off" />
										</div>

									</div>
								</div>
							</div>
						</div>
					</div>
					<s:hidden name="token" value="%{#session.customToken}"></s:hidden>
				</s:form>



				<div class="row my-5">
					<div class="col">
						<div class="card">
							<div class="card-body">
								<div class="scrollmenu">
									<div class="row my-3 align-items-center">
										<s:iterator value="AaData">
											<table class="table table-striped table-row-bordered  gs-7"
												style="margin-top: 5vh;">
												<thead>

													<tr class="gra">

														<td class="key" colspan="22"><s:property value="key" />
														</td>

													</tr>

													<s:if test="key=='NET_BANKING'">

														<s:if test="netbankingMaster==false">
															<tr>
																<td colspan="22">
																	<h5>Update All</h5>
																</td>

															</tr>
															<tr>
																<td>PayId</td>
<!-- 																<td>Currency</td> -->
																<td>Acquirer</td>
																<td>Payment Type</td>
																<td>Mop Type</td>
																<td>Min. Amount Per Transaction</td>
																<td>Max. Amount Per Transaction </td>
																<td>From Date</td>
																<td style="display:none"></td>
																<td>Acquirer Preference</td>
																<td>Acquirer Tdr</td>
																<td>Acquirer Min. Amount Per Transaction</td>
																<td>Acquirer Max. Amount Per Transaction </td>
																<td>Merchant Preference</td>
																<td>Merchant Tdr</td>
																<td>Merchant Min. Amount Per Transaction</td>
																<td>Merchant Max. Amount Per Transaction </td>
																<td>Enable Surcharge</td>
																<td>Local Tax Rate</td>
																<td>Edit</td>
																<td>Save</td>
																<td>Cancel</td>


															</tr>
															<tr>
																<td><input type="text" disabled class="selectField"
																	value="<s:property value=" payId" />">
<!-- 																<td><input type="text" disabled class="selectField" -->
<%-- 																	value="<s:property value=" currency" />"> --%>
																<td><input type="text" disabled class="selectField"
																	value="<s:property value=" acquirer" />">
																<td><input type="text" disabled class="selectField"
																	value="<s:property value=" paymentType" />">
																<td><input type="text" disabled class="selectField"
																	value="All"></td>
																<td><input type="number" disabled value="0"></td>
																<td><input type="number" disabled value="0"></td>
																<td><input type="datetime-local" disabled></td>
																<td style="display:none"></td>
																<td>
																<s:select
																	list="#{'FLAT':'FLAT', 'PERCENTAGE':'PERCENTAGE'}"
																	name="bankPreference" disabled="true" />
																</td>
																<td><input type="number" disabled value="0"></td>
																<td><input type="number" disabled value="0"></td>
																<td><input type="number" disabled value="0"></td>
																<td>
																<s:select
																	list="#{'FLAT':'FLAT', 'PERCENTAGE':'PERCENTAGE'}"
																	name="bankPreference" disabled="true" />
																</td>
																<td><input type="number" disabled value="0"></td>
																<td><input type="number" disabled value="0"></td>
																<td><input type="number" disabled value="0"></td>
																<td><input type="checkbox" disabled></td>
																<td><input type="text" disabled class="selectField" value="<s:property value=" igst" />">
																<td>
																	<button class="btn btn-primary btn-xs ml-2 "
																		onclick="editRows(this)">Edit</button>
																</td>
																<td>
																	<button class="btn btn-primary btn-xs ml-2 "
																		onclick="updateAll(this)">Save</button>
																</td>
																<td>
																	<button class="btn btn-primary btn-xs ml-2 "
																		onclick="cancelRows(this)">Cancel</button>
																</td>

															</tr>

														</s:if>

														<tr>
															<td colspan="22">
																<h5>Update individual bank</h5>
															</td>

														</tr>
														<tr>
															
															<td style="display: none;">id</td>
															<td>PayId</td>
															<td>Currency</td>
															<td>Acquirer</td>
															<td>Payment Type</td>
															<td>Mop Type</td>
															<td>Min. Amount Per Transaction</td>
															<td>Max. Amount Per Transaction </td>
															<td>From Date</td>
															<td style="display:none"></td>
															<td>Acquirer Preference</td>
															<td>Acquirer Tdr</td>
															<td>Acquirer Min. Amount Per Transaction</td>
															<td>Acquirer Max. Amount Per Transaction </td>
															<td>Merchant Preference</td>
															<td>Merchant Tdr</td>
															<td>Merchant Min. Amount Per Transaction</td>
															<td>Merchant Max. Amount Per Transaction </td>
															<td>Enable Surcharge</td>
															<td>Local Tax Rate</td>
															<td>Edit</td>
															<td>Save</td>
															<td>Cancel</td>
															<td>Delete Row</td>
															<td>Add Row</td>

														</tr>
													</s:if>
													<s:else>

														<tr>
															
															<td style="display: none;">id</td>
															<td>PayId</td>
															<td>Currency</td>
															<td>Acquirer</td>
															<td>Payment Type</td>
															<td>Mop Type</td>
															<td>Min. Amount Per Transaction</td>
															<td>Max. Amount Per Transaction </td>
															<td>From Date</td>
															<td style="display:none"></td>

															<td>Acquirer Preference</td>
															<td>Acquirer Tdr</td>
															<td>Acquirer Min. Amount Per Transaction</td>
															<td>Acquirer Max. Amount Per Transaction </td>
															<td>Merchant Preference</td>
															<td>Merchant Tdr</td>
															<td>Merchant Min. Amount Per Transaction</td>
															<td>Merchant Max. Amount Per Transaction </td>
															<td>Enable Surcharge</td>
															<td>Local Tax Rate</td>
															<td>Edit</td>
															<td>Save</td>
															<td>Cancel</td>
															<td>Delete Row</td>
															<td>Add Row</td>

														</tr>

													</s:else>
												</thead>
												<tbody>
													<s:iterator value="value">

														<tr>
															
															<td style="display: none;"><input type="hidden"
																value="<s:property value=" id" />"></td>
															<td><input type="text" disabled class="selectField"
																value="<s:property value=" payId" />"></td>
															<td><input type="text" disabled class="selectField"
																	value="<s:property value=" currency" />"></td>
															<td><input type="text" disabled class="selectField"
																value="<s:property value=" acquirerName" />"></td>
															<td><input type="text" disabled class="selectField"
																value="<s:property value=" paymentType" />"></td>
															<td><input type="text" disabled class="selectField"
																value="<s:property value=" mopType" />"></td>
															<td><input type="number" disabled
																value="<s:property value="
																		minTransactionAmount" />">
															</td>
															<td><input type="number" disabled
																value="<s:property value="
																		maxTransactionAmount" />">
															</td>


															<td><input type="datetime-local" disabled
																value="<s:property value=" fDate" />"></td>

															<td style="display:none"></td>

															<td><s:select
																	list="#{'FLAT':'FLAT', 'PERCENTAGE':'PERCENTAGE'}"
																	name="bankPreference" disabled="true" /></td>
															<td><input type="number" disabled
																value="<s:property value=" bankTdr" />"></td>
															<td><input type="number" disabled
																value="<s:property value=" bankMinTdrAmt" />"></td>
															<td><input type="number" disabled
																value="<s:property value=" bankMaxTdrAmt" />"></td>
															<td><s:select
																	list="#{'FLAT':'FLAT', 'PERCENTAGE':'PERCENTAGE'}"
																	name="merchantPreference" disabled="true" /></td>
															<td><input type="number" disabled
																value="<s:property value=" merchantTdr" />"></td>
															<td><input type="number" disabled
																value="<s:property value="
																		merchantMinTdrAmt" />">
															</td>
															<td><input type="number" disabled
																value="<s:property value="
																		merchantMaxTdrAmt" />">
															</td>
															<td><s:checkbox class="enableSurcharge" name="enableSurcharge"
																	disabled="true" /></td>
															<td><input type="text" disabled class="selectField"
																value="<s:property value=" igst" />"></td>
															<td>
																<button class="btn btn-primary btn-xs ml-2 "
																	onclick="editRows(this)">Edit</button>
															</td>
															<td>
																<button class="btn btn-primary btn-xs ml-2 "
																	onclick="saveRows(this)">Save</button>
															</td>
															<td>
																<button class="btn btn-primary btn-xs ml-2 "
																	onclick="cancelRows(this)">Cancel</button>
															</td>
															<td><i class="fa-solid fa-minus fa-lg"
																onclick="deleteRow(this)"></i></td>
															<td><i class="fa-solid fa-plus fa-lg"
																onclick="cloneRow(this)"></i></td>
															<!-- <td>
																<button class="btn btn-primary btn-xs ml-2 "
																	onclick="cloneRow(this)">Add Row</button>
															</td>
															<td>
																<button class="btn btn-primary btn-xs ml-2 "
																	onclick="deleteRow(this)">Delete Row</button>
															</td> -->
														</tr>



													</s:iterator>
												</tbody>
											</table>

											<!-- <table>
												<s:iterator value="value">
													<tr>
														<td>
															<s:property />
														</td>
													</tr>
												</s:iterator>
											</table> -->
										</s:iterator>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>


	</div>
	<script src="../js/tdrSetting.js"></script>
	<script type="text/javascript">
		$(document).ready(function() {
			$("#merchants").select2();
			$("#acquirer").select2();
			$("#paymentRegion").select2();
			$("#cardHolderType").select2();
			$("#currency").select2();

		});

		function saveRows(currentObject) {
			$(currentObject).closest('tr').find('button')
					.prop('disabled', true);
			var merchants = $("select#merchants").val();
			var paymentRegion = $("select#paymentRegion").val();
			var cardHolderType = $("select#cardHolderType").val();
			var id = currentObject.closest("tr").cells[0].children[0].value;
			var payId = currentObject.closest("tr").cells[1].children[0].value;
			var currency = currentObject.closest("tr").cells[2].children[0].value;
			var acquirerName = currentObject.closest("tr").cells[3].children[0].value;
			var paymentType = currentObject.closest("tr").cells[4].children[0].value;
			var mopType = currentObject.closest("tr").cells[5].children[0].value;
			var minTransactionAmount = currentObject.closest("tr").cells[6].children[0].value;
			var maxTransactionAmount = currentObject.closest("tr").cells[7].children[0].value;

			var fDate = currentObject.closest("tr").cells[8].children[0].value;

			var bankPreference = currentObject.closest("tr").cells[10].children[0].children[0].children[0].value;
			var bankTdr = currentObject.closest("tr").cells[11].children[0].value;
			var bankMinTdrAmt = currentObject.closest("tr").cells[12].children[0].value;
			var bankMaxTdrAmt = currentObject.closest("tr").cells[13].children[0].value;
			var merchantPreference = currentObject.closest("tr").cells[14].children[0].children[0].children[0].value;
			var merchantTdr = currentObject.closest("tr").cells[15].children[0].value;
			var merchantMinTdrAmt = currentObject.closest("tr").cells[16].children[0].value;
			var merchantMaxTdrAmt = currentObject.closest("tr").cells[17].children[0].value;
			var enableSurcharge = currentObject.closest("tr").cells[18].children[0].children[0].children[0].checked;
			var igst = currentObject.closest("tr").cells[19].children[0].value;
			var userType = "<s:property value='%{#session.USER.UserType.name()}'/>";
			var loginUserEmailId = "<s:property value='%{#session.USER.emailId}'/>";
			var token = document.getElementsByName("token")[0].value;

			data = {
				"tdr.id" : id,
				"emailId" : merchants,
				"tdr.fDate" : fDate,
				"tdr.paymentRegion" : paymentRegion,
				"tdr.type" : cardHolderType,
				"tdr.payId" : payId,
				"tdr.currency" : currency,
				"tdr.merchantPreference" : merchantPreference,
				"tdr.acquirerName" : acquirerName,
				"tdr.paymentType" : paymentType,
				"tdr.mopType" : mopType,
				"tdr.minTransactionAmount" : minTransactionAmount,
				"tdr.maxTransactionAmount" : maxTransactionAmount,
				"tdr.bankPreference" : bankPreference,
				"tdr.bankTdr" : bankTdr,
				"tdr.bankMinTdrAmt" : bankMinTdrAmt,
				"tdr.bankMaxTdrAmt" : bankMaxTdrAmt,
				"tdr.merchantTdr" : merchantTdr,
				"tdr.merchantMinTdrAmt" : merchantMinTdrAmt,
				"tdr.merchantMaxTdrAmt" : merchantMaxTdrAmt,
				"tdr.enableSurcharge" : enableSurcharge,
				"tdr.igst" : igst,
				"userType" : userType,
				"loginUserEmailId" : loginUserEmailId,
				"token" : token,
				"struts.token.name" : "token"
			}
			
			if (parseFloat(maxTransactionAmount)<1) {
				alert("Max. Amount Per Transaction Cannot be less than 1");
				$(currentObject).closest('tr').find('button').prop('disabled',
						false);
				return false;
			}
// 			if (parseFloat(maxTransactionAmount)<0) {
// 				alert("Max Transaction Amount Cannot be Negative");
// 				$(currentObject).closest('tr').find('button').prop('disabled',
// 						false);
// 				return false;
// 			}
			
			if (parseFloat(minTransactionAmount)<1) {
				alert("Min Transaction Amount Cannot be less than 1");
				$(currentObject).closest('tr').find('button').prop('disabled',
						false);
				return false;
			}
// 			if (parseFloat(minTransactionAmount)<0) {
// 				alert("Min Transaction Amount Cannot be Negative");
// 				$(currentObject).closest('tr').find('button').prop('disabled',
// 						false);
// 				return false;
// 			}
			
			if (parseFloat(minTransactionAmount)>parseFloat(maxTransactionAmount)) {
				alert("Min. Amount Per Transaction cannot be equal or greater than Max. Amount Per Transaction");
				$(currentObject).closest('tr').find('button').prop('disabled',
						false);
				return false;
			}

			if (fDate == '') {
				alert("Select Enter From Date");
				$(currentObject).closest('tr').find('button').prop('disabled',false);
				return false;
			}

			if(parseFloat(bankTdr)==0){
					alert("Acquirer Tdr Cannot be Zero");
					$(currentObject).closest('tr').find('button').prop('disabled',
							false);
					return false;
				}

			if(parseFloat(bankTdr)<0){
					alert("Acquirer Tdr Cannot be Negative");
					$(currentObject).closest('tr').find('button').prop('disabled',
							false);
					return false;
				}
			if (parseFloat(bankMinTdrAmt)<0 ) {
					alert("Acquirer Min. Amount Per Transaction cannot be Less than or equal to be 0");
					$(currentObject).closest('tr').find('button').prop('disabled',
							false);
					return false;
				}
			if (parseFloat(bankMaxTdrAmt) <0) {
					alert("Acquirer Max. Amount Per Transaction cannot be Less than or equal to be 0");
					$(currentObject).closest('tr').find('button').prop('disabled',
							false);
					return false;
				}


			if (parseFloat(bankMinTdrAmt)> parseFloat(bankMaxTdrAmt) ) {
					alert("Acquirer Min. Amount Per Transaction cannot be equal or greater than Acquirer Max. Amount Per Transaction");
					$(currentObject).closest('tr').find('button').prop('disabled',
							false);
					return false;
				}
				if(parseFloat(merchantTdr)==0){
					alert("Merchant Tdr Cannot be Zero");
					$(currentObject).closest('tr').find('button').prop('disabled',
							false);
					return false;
				}
				if(parseFloat(merchantTdr)<0){
					alert("Merchant Tdr Cannot be Negative");
					$(currentObject).closest('tr').find('button').prop('disabled',
							false);
					return false;
				}

				if (parseFloat(merchantMinTdrAmt)<0 ) {
					alert("Merchant Min. Amount Per Transaction cannot be Less than or equal to be 0");
					$(currentObject).closest('tr').find('button').prop('disabled',
							false);
					return false;
				}
				if (parseFloat(merchantMaxTdrAmt)<0  ) {
					alert("Merchant Min. Amount Per Transaction cannot be Less than or equal to be 0");
					$(currentObject).closest('tr').find('button').prop('disabled',
							false);
					return false;
				}

				if (parseFloat(merchantMinTdrAmt)>parseFloat(merchantMaxTdrAmt)  ) {
					alert("Merchant Min. Amount Per Transaction cannot be equal or greater than Merchant Max. Amount Per Transaction");
					$(currentObject).closest('tr').find('button').prop('disabled',
							false);
					return false;
				}

			if (bankPreference != "FLAT") {
				if(parseFloat(bankTdr)==0){
					alert("Acquirer Tdr Cannot be Zero");
					$(currentObject).closest('tr').find('button').prop('disabled',
							false);
					return false;
				}
				if(parseFloat(bankTdr)<0){
					alert("Acquirer Tdr Cannot be Negative");
					$(currentObject).closest('tr').find('button').prop('disabled',
							false);
					return false;
				}

				if (parseFloat(bankMinTdrAmt)<0 ) {
					alert("Acquirer Min. Amount Per Transaction cannot be Less than or equal to be 0");
					$(currentObject).closest('tr').find('button').prop('disabled',
							false);
					return false;
				}

				if (parseFloat(bankMaxTdrAmt) <0) {
					alert("Acquirer Max. Amount Per Transaction cannot be Less than or equal to be 0");
					$(currentObject).closest('tr').find('button').prop('disabled',
							false);
					return false;
				}


				if (parseFloat(bankMinTdrAmt)> parseFloat(bankMaxTdrAmt) ) {
					alert("Acquirer Min. Amount Per Transaction cannot be equal or greater than Acquirer Max. Amount Per Transaction");
					$(currentObject).closest('tr').find('button').prop('disabled',
							false);
					return false;
				}
			}else{
				if(parseFloat(bankTdr)==0){
					alert("Acquirer Tdr Cannot be Zero");
					$(currentObject).closest('tr').find('button').prop('disabled',
							false);
					return false;
				}
				if(parseFloat(bankTdr)<0){
					alert("Acquirer Tdr Cannot be Negative");
					$(currentObject).closest('tr').find('button').prop('disabled',
							false);
					return false;
				}
				if (parseFloat(bankMinTdrAmt)<0 ) {
					alert("Acquirer Min. Amount Per Transaction cannot be Less than or equal to be 0");
					$(currentObject).closest('tr').find('button').prop('disabled',
							false);
					return false;
				}

				if (parseFloat(bankMaxTdrAmt) <0) {
					alert("Acquirer Max. Amount Per Transaction cannot be Less than or equal to be 0");
					$(currentObject).closest('tr').find('button').prop('disabled',
							false);
					return false;
				}


				if (parseFloat(bankMinTdrAmt)> parseFloat(bankMaxTdrAmt) ) {
					alert("Acquirer Min. Amount Per Transaction cannot be equal or greater than Acquirer Max. Amount Per Transaction");
					$(currentObject).closest('tr').find('button').prop('disabled',
							false);
					return false;
				}

			}

			if (merchantPreference != "FLAT") {
				if(parseFloat(merchantTdr)==0){
					alert("Merchant Tdr Cannot be Zero");
					$(currentObject).closest('tr').find('button').prop('disabled',
							false);
					return false;
				}
				if(parseFloat(merchantTdr)<0){
					alert("Merchant Tdr Cannot be Negative");
					$(currentObject).closest('tr').find('button').prop('disabled',
							false);
					return false;
				}

				if (parseFloat(merchantMinTdrAmt)<0 ) {
					alert("Merchant Min. Amount Per Transaction cannot be Less than or equal to be 0");
					$(currentObject).closest('tr').find('button').prop('disabled',
							false);
					return false;
				}
				if (parseFloat(merchantMaxTdrAmt)<0  ) {
					alert("Merchant Min. Amount Per Transaction cannot be Less than or equal to be 0");
					$(currentObject).closest('tr').find('button').prop('disabled',
							false);
					return false;
				}

				if (parseFloat(merchantMinTdrAmt)>parseFloat(merchantMaxTdrAmt)  ) {
					alert("Merchant Min. Amount Per Transaction cannot be equal or greater than Merchant Max. Amount Per Transaction");
					$(currentObject).closest('tr').find('button').prop('disabled',
							false);
					return false;
				}
			}else{
				if(parseFloat(merchantTdr)==0){
					alert("Merchant Tdr Cannot be Zero");
					$(currentObject).closest('tr').find('button').prop('disabled',
							false);
					return false;
				}
				if(parseFloat(merchantTdr)<0){
					alert("Merchant Tdr Cannot be Negative ");
					$(currentObject).closest('tr').find('button').prop('disabled',
							false);
					return false;
				}
				if (parseFloat(merchantMinTdrAmt)<0 ) {
					alert("Merchant Min. Amount Per Transaction cannot be Less than or equal to be 0");
					$(currentObject).closest('tr').find('button').prop('disabled',
							false);
					return false;
				}
				if (parseFloat(merchantMaxTdrAmt)<0  ) {
					alert("Merchant Min. Amount Per Transaction cannot be Less than or equal to be 0");
					$(currentObject).closest('tr').find('button').prop('disabled',
							false);
					return false;
				}

				if (parseFloat(merchantMinTdrAmt)>parseFloat(merchantMaxTdrAmt)  ) {
					alert("Merchant Min. Amount Per Transaction cannot be equal or greater than Merchant Max. Amount Per Transaction");
					$(currentObject).closest('tr').find('button').prop('disabled',
							false);
					return false;
				}
			}



			const fromDate = new Date(fDate);


			$
					.ajax({
						type : "POST",
						url : "editTdrSetting",
						data : data,
						timeout : 0,
						success : function(responseData, status) {

							alert(responseData.responseMessage);

							window.location.reload();
						},
						error : function(data) {
							console
									.log("Network error, charging detail may not be saved");
						}
					});

			flag = false;
			$(currentObject).closest('tr').find('button').prop('disabled',
					false);
		}

		function updateAll(currentObject) {
			debugger

			$(currentObject).closest('tr').find('button')
					.prop('disabled', true);
			var merchants = $("select#merchants").val();
			var paymentRegion = $("select#paymentRegion").val();
			var cardHolderType = $("select#cardHolderType").val();
			var currency = $("select#currency").val();


			var payId = currentObject.closest("tr").cells[0].children[0].value;
			//var currency = currentObject.closest("tr").cells[1].children[0].value;
			var acquirerName = currentObject.closest("tr").cells[1].children[0].value;
			var paymentType = currentObject.closest("tr").cells[2].children[0].value;
			var mopType = currentObject.closest("tr").cells[3].children[0].value;
			var minTransactionAmount = currentObject.closest("tr").cells[4].children[0].value;
			var maxTransactionAmount = currentObject.closest("tr").cells[5].children[0].value;

			var fDate = currentObject.closest("tr").cells[6].children[0].value;


			var bankPreference =currentObject.closest("tr").cells[8].children[0].children[0].children[0].value;
			//var bankPreference = currentObject.closest("tr").cells[8].children[0].children[0].value;
			var bankTdr = currentObject.closest("tr").cells[9].children[0].value;
			var bankMinTdrAmt = currentObject.closest("tr").cells[10].children[0].value;
			var bankMaxTdrAmt = currentObject.closest("tr").cells[11].children[0].value;

			var merchantPreference = currentObject.closest("tr").cells[12].children[0].children[0].children[0].value;
			//var merchantPreference = currentObject.closest("tr").cells[12].children[0].children[0].value;
			var merchantTdr = currentObject.closest("tr").cells[13].children[0].value;
			var merchantMinTdrAmt = currentObject.closest("tr").cells[14].children[0].value;
			var merchantMaxTdrAmt = currentObject.closest("tr").cells[15].children[0].value;
			var enableSurcharge = currentObject.closest("tr").cells[16].children[0].checked;
			var igst = currentObject.closest("tr").cells[17].children[0].value;
			var userType = "<s:property value='%{#session.USER.UserType.name()}'/>";
			var loginUserEmailId = "<s:property value='%{#session.USER.emailId}'/>";
			var token = document.getElementsByName("token")[0].value;

			data = {
				"emailId" : merchants,
				"tdr.fDate" : fDate,
				"tdr.paymentRegion" : paymentRegion,
				"tdr.type" : cardHolderType,
				"tdr.payId" : payId,
				"tdr.currency" : currency,
				"tdr.merchantPreference" : merchantPreference,
				"tdr.acquirerName" : acquirerName,
				"tdr.paymentType" : paymentType,
				"tdr.mopType" : mopType,
				"tdr.minTransactionAmount" : minTransactionAmount,
				"tdr.maxTransactionAmount" : maxTransactionAmount,
				"tdr.bankPreference" : bankPreference,
				"tdr.bankTdr" : bankTdr,
				"tdr.bankMinTdrAmt" : bankMinTdrAmt,
				"tdr.bankMaxTdrAmt" : bankMaxTdrAmt,
				"tdr.merchantTdr" : merchantTdr,
				"tdr.merchantMinTdrAmt" : merchantMinTdrAmt,
				"tdr.merchantMaxTdrAmt" : merchantMaxTdrAmt,
				"tdr.enableSurcharge" : enableSurcharge,
				"tdr.igst" : igst,
				"userType" : userType,
				"loginUserEmailId" : loginUserEmailId,
				"token" : token,
				"struts.token.name" : "token"
			}
			if (parseFloat(maxTransactionAmount)<1) {
				alert("Max Transaction Amount Cannot be less than 1");
				$(currentObject).closest('tr').find('button').prop('disabled',
						false);
				return false;
			}
// 			if (parseFloat(maxTransactionAmount)<0) {
// 				alert("Max Transaction Amount Cannot be Negative");
// 				$(currentObject).closest('tr').find('button').prop('disabled',
// 						false);
// 				return false;
// 			}

			if (parseFloat(minTransactionAmount)<1) {
				alert("Min Transaction Amount Cannot be less than 1");
				$(currentObject).closest('tr').find('button').prop('disabled',
						false);
				return false;
			}
// 			if (parseFloat(minTransactionAmount)<0) {
// 				alert("Min Transaction Amount Cannot be Negative");
// 				$(currentObject).closest('tr').find('button').prop('disabled',
// 						false);
// 				return false;
// 			}

			if (parseFloat(minTransactionAmount)>parseFloat(maxTransactionAmount)) {
				alert("Min. Amount Per Transaction cannot be equal or greater than Max. Amount Per Transaction");
				$(currentObject).closest('tr').find('button').prop('disabled',
						false);
				return false;
			}

			if (bankPreference != "FLAT") {
				if(parseFloat(bankTdr)==0){
					alert("Acquirer Tdr Cannot be Zero");
					$(currentObject).closest('tr').find('button').prop('disabled',
							false);
					return false;
				}
				if(parseFloat(bankTdr)<0){
					alert("Acquirer Tdr Cannot be Negative");
					$(currentObject).closest('tr').find('button').prop('disabled',
							false);
					return false;
				}

				if (parseFloat(bankMinTdrAmt)<0 ) {
					alert("Acquirer Min. Amount Per Transaction cannot be Less than or equal to be 0");
					$(currentObject).closest('tr').find('button').prop('disabled',
							false);
					return false;
				}

				if (parseFloat(bankMaxTdrAmt) <0) {
					alert("Acquirer Max. Amount Per Transaction cannot be Less than or equal to be 0");
					$(currentObject).closest('tr').find('button').prop('disabled',
							false);
					return false;
				}


				if (parseFloat(bankMinTdrAmt)> parseFloat(bankMaxTdrAmt) ) {
					alert("Acquirer Min. Amount Per Transaction cannot be equal or greater than Acquirer Max. Amount Per Transaction");
					$(currentObject).closest('tr').find('button').prop('disabled',
							false);
					return false;
				}
			}else{
				if(parseFloat(bankTdr)==0){
					alert("Acquirer Tdr Cannot be Zero");
					$(currentObject).closest('tr').find('button').prop('disabled',
							false);
					return false;
				}
				if(parseFloat(bankTdr)<0){
					alert("Acquirer Tdr Cannot be Negative");
					$(currentObject).closest('tr').find('button').prop('disabled',
							false);
					return false;
				}
				if (parseFloat(bankMinTdrAmt)<0 ) {
					alert("Acquirer Min. Amount Per Transaction cannot be Less than or equal to be 0");
					$(currentObject).closest('tr').find('button').prop('disabled',
							false);
					return false;
				}

				if (parseFloat(bankMaxTdrAmt) <0) {
					alert("Acquirer Max. Amount Per Transaction cannot be Less than or equal to be 0");
					$(currentObject).closest('tr').find('button').prop('disabled',
							false);
					return false;
				}


				if (parseFloat(bankMinTdrAmt)> parseFloat(bankMaxTdrAmt) ) {
					alert("Acquirer Min. Amount Per Transaction cannot be equal or greater than Acquirer Max. Amount Per Transaction");
					$(currentObject).closest('tr').find('button').prop('disabled',
							false);
					return false;
				}

			}

			if (merchantPreference != "FLAT") {
				if(parseFloat(merchantTdr)==0){
					alert("Merchant Tdr Cannot be Zero");
					$(currentObject).closest('tr').find('button').prop('disabled',
							false);
					return false;
				}
				if(parseFloat(merchantTdr)<0){
					alert("Merchant Tdr Cannot be Negative");
					$(currentObject).closest('tr').find('button').prop('disabled',
							false);
					return false;
				}

				if (parseFloat(merchantMinTdrAmt)<0 ) {
					alert("Merchant Min. Amount Per Transaction cannot be Less than or equal to be 0");
					$(currentObject).closest('tr').find('button').prop('disabled',
							false);
					return false;
				}
				if (parseFloat(merchantMaxTdrAmt)<0  ) {
					alert("Merchant Min. Amount Per Transaction cannot be Less than or equal to be 0");
					$(currentObject).closest('tr').find('button').prop('disabled',
							false);
					return false;
				}

				if (parseFloat(merchantMinTdrAmt)>parseFloat(merchantMaxTdrAmt)  ) {
					alert("Merchant Min. Amount Per Transaction cannot be equal or greater than Merchant Max. Amount Per Transaction");
					$(currentObject).closest('tr').find('button').prop('disabled',
							false);
					return false;
				}
			}else{
				if(parseFloat(merchantTdr)==0){
					alert("Merchant Tdr Cannot be Zero");
					$(currentObject).closest('tr').find('button').prop('disabled',
							false);
					return false;
				}
				if(parseFloat(merchantTdr)<0){
					alert("Merchant Tdr Cannot be Negative ");
					$(currentObject).closest('tr').find('button').prop('disabled',
							false);
					return false;
				}
				if (parseFloat(merchantMinTdrAmt)<0 ) {
					alert("Merchant Min. Amount Per Transaction cannot be Less than or equal to be 0");
					$(currentObject).closest('tr').find('button').prop('disabled',
							false);
					return false;
				}
				if (parseFloat(merchantMaxTdrAmt)<0  ) {
					alert("Merchant Min. Amount Per Transaction cannot be Less than or equal to be 0");
					$(currentObject).closest('tr').find('button').prop('disabled',
							false);
					return false;
				}

				if (parseFloat(merchantMinTdrAmt)>parseFloat(merchantMaxTdrAmt)  ) {
					alert("Merchant Min. Amount Per Transaction cannot be equal or greater than Merchant Max. Amount Per Transaction");
					$(currentObject).closest('tr').find('button').prop('disabled',
							false);
					return false;
				}
			}			if (fDate == '') {
				alert("Enter Enter From Date");
				$(currentObject).closest('tr').find('button').prop('disabled',
						false);
				return false;
			}
			
			
			if (fDate == '') {
				alert("Enter Enter From Date");
				$(currentObject).closest('tr').find('button').prop('disabled',false);
				return false;
			}
			
			const fromDate = new Date(fDate);

			$
					.ajax({
						type : "POST",
						url : "editTdrSettingAll",
						data : data,
						timeout : 0,
						success : function(responseData, status) {

							alert(responseData.responseMessage);

							window.location.reload();
						},
						error : function(data) {
							console
									.log("Network error, charging detail may not be saved");
						}
					});

			flag = false;
			$(currentObject).closest('tr').find('button').prop('disabled',
					false);
		}
	</script>
	
	<script>
		
		document.getElementById('poCheckbox').addEventListener('change', function () {
			if (this.checked) {
				window.location.href = 'payoutTdrSetting'; } });
		</script>
</body>

</html>