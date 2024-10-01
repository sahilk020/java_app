<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@taglib prefix="s" uri="/struts-tags" %>
	<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title>Merchant Mapping</title>
	<!-- <link href="../css/bootstrap.min.css" rel="stylesheet"> -->
	<link href="../fonts/css/font-awesome.min.css" rel="stylesheet">
</link>
<link href="../css/default.css" rel="stylesheet" type="text/css" />
<link href="../css/jquery-ui.css" rel="stylesheet" />
<link href="../css/loader.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="../js/jquery.min.js"></script>
<script src="../js/core/popper.min.js"></script>
<script src="../js/core/bootstrap-material-design.min.js"></script>
<script src="../js/plugins/perfect-scrollbar.jquery.min.js"></script>


<link rel="shortcut icon" href="assets/media/images/paylogo.svg" />
<!--begin::Fonts-->
<link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Inter:300,400,500,600,700" />
<!--end::Fonts-->
<!--begin::Vendor Stylesheets(used by this page)-->
<link href="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.css" rel="stylesheet" type="text/css" />
<link href="../assets/plugins/custom/datatables/datatables.bundle.css" rel="stylesheet" type="text/css" />
<!--end::Vendor Stylesheets-->
<!--begin::Global Stylesheets Bundle(used by all pages)-->
<link href="../assets/plugins/global/plugins.bundle.css" rel="stylesheet" type="text/css" />
<link href="../assets/css/style.bundle.css" rel="stylesheet" type="text/css" />
<script src="../assets/plugins/global/plugins.bundle.js"></script>
<script src="../assets/js/scripts.bundle.js"></script>
<link href="../css/select2.min.css" rel="stylesheet" />
<script src="../js/jquery.select2.js" type="text/javascript"></script>
<script src="../js/mapping.js"></script>

		<script type="text/javascript">
			function showMe(it, box) {
				var vis = (box.checked) ? "block" : "none";
				var ele = document.getElementById(it);
				ele.style.display = vis;
				deselectAllCheckboxesWithinDiv(ele.id);
			}

			function hidefields(currencyBox) {
				var currencyDiv = currencyBox.checked;
				if (currencyDiv == true) {
					document.getElementById("err2").style.display = "none";
				}
				else {
					document.getElementById("err2").style.display = "block";
				}
				var vis = (currencyBox.checked) ? "block" : "none";
				var fieldsDiv = document.getElementById('boxdiv' + currencyBox.id)
				fieldsDiv.style.display = vis;
			}

			function getMops() {
				var merchantId = document.getElementById("merchants").value;
				var acquirer = document.getElementById("acquirer").value;

				if (merchantId == "" || merchantId == null || acquirer == "") {
					document.getElementById("err").style.display = "block";
					return false;
				}
				document.mopSetupForm.submit();
			}

			function resetAcq() {
				//document.getElementById("paymentCheck").style.display = "none";
				document.getElementById("merchants").style.border = "1px solid #ccc";
				var acquirerDropDown = document.getElementById("acquirer");
				acquirerDropDown.selectedIndex = 0;
				refresh();
			}

			$(document).ready(
					function () {
						if (document.getElementById("merchants").value == '' && document.getElementById("acquirer").value == '') {
							document.getElementById("btnsubmit").style.display = "none";
						}
						else {
							document.getElementById("btnsubmit").style.display = "block";
						}
						getMapping();
						var mainDiv = document.getElementById('id+checkBoxes');
						mainDiv.style.display = "none";
						$("#btnsubmit")
							.click(function (evt) {
									if (document.getElementById("merchants").value == '') {
										alert("Select a Merchant to save");
										return false;
									}
									// if (document.getElementById("user").value == '-1') {
									// 	alert("Select a UserType to save");
									// 	return false;
									// }
									if (document.getElementById("acquirer").value == '') {
										alert("Select an Acquirer to save");
										return false;
									}
									var index, element;
									var paymentTypeCheckedFlag = false;
									var form = document
										.getElementById("mopSetupForm");
									var num_of_ele = form.elements.length;
									var mappingString = "";
									var count = 0;
									for (index = 0; index < num_of_ele; ++index) {
										element = form.elements[index];
										if (element.type.toUpperCase() == "CHECKBOX"
											&& element.checked && !element.classList.contains("ignore-check")) {
											var mapStringVal;
											//flag to check if just Paymentype selected
									if ((element.id.endsWith('Credit Cardbox') || element.id.endsWith('Debit Cardbox')|| element.id.endsWith('Prepaid Cardbox')  || element.id.endsWith('QR CODEbox')
												 || element.id.endsWith('UPIbox')  || element.id.endsWith('EMIbox')
												  || element.id.endsWith('AutoDebitbox')  || element.id.indexOf('id') > -1)) {
												paymentTypeCheckedFlag = true;
											}
									if (!(element.id.endsWith('Credit Cardbox') || element.id.endsWith('Debit Cardbox') || element.id.endsWith('Prepaid Cardbox') || element.id.endsWith('Net Bankingbox') || element.id.endsWith('UPIbox')  || element.id.endsWith('QR CODEbox')  || element.id.endsWith('AutoDebitbox')
												|| element.id.endsWith('Walletbox') || element.id.endsWith('EMIbox')  || (element.id.indexOf('id') > -1 && element.id.indexOf('id') < 4))) {
												if (count == 0 || mappingString == "")
													mapStringVal = element.id;
												else {
													mapStringVal = ','
													+ element.id;
												}
												count++;
												if (mapStringVal == 'international' || mapStringVal == 'domestic') {
													mapStringVal = "";
												}
												mappingString = mappingString.concat(mapStringVal);
											}

										}
									}
									var merchantId = document
										.getElementById("merchants").value;
									var acquirer = document
										.getElementById("acquirer").value;
									var currencyboxes = document
										.getElementsByName("currency");
									var accountCurrencySet = [];
									var k = 0;
									var userType = "<s:property value='%{#session.USER.UserType.name()}'/>";
									var emailId = "<s:property value='%{#session.USER.EmailId}'/>";

									for (j = 0; j < currencyboxes.length; j++) {
										boxelement = currencyboxes[j];
										if (boxelement.checked) {
											var password = document.getElementById("idpassword+" + boxelement.value).value;
											var txnKey = document.getElementById("idtxnkey+" + boxelement.value).value;
											var merchant = document.getElementById("idmerchantid+" + boxelement.value).value;


											var adf1 = document.getElementById("idadf1+" + boxelement.value).value;
											var adf2 = document.getElementById("idadf2+" + boxelement.value).value;
											var adf3 = document.getElementById("idadf3+" + boxelement.value).value;
											var adf4 = document.getElementById("idadf4+" + boxelement.value).value;
											var adf5 = document.getElementById("idadf5+" + boxelement.value).value;
											var adf6 = document.getElementById("idadf6+" + boxelement.value).value;
											var adf7 = document.getElementById("idadf7+" + boxelement.value).value;
											var adf8 = document.getElementById("idadf8+" + boxelement.value).value;
											var adf9 = document.getElementById("idadf9+" + boxelement.value).value;
											var adf10 = document.getElementById("idadf10+" + boxelement.value).value;
											var adf11 = document.getElementById("idadf11+" + boxelement.value).value;
                                            var international = document.getElementById("idinternational+" + boxelement.value);
											var domestic = document.getElementById("iddomestic+" + boxelement.value);

											if (merchant == "" && adf5 == "") {
												alert("Merchant Id / ADF 5 , atleast one is mandatory for mapping currency");
												return false;
											}

											if (acquirer == "CITRUS" || acquirer == "FSS") {
												var directTxn = document.getElementById("id3dflag+" + boxelement.value).checked;
											}

											var internationalChecked = international.checked;
											var domesticChecked = domestic.checked;
											if (internationalChecked == false && domesticChecked == false) {
												alert("Select Atleast one Transaction Region !");
												return false;
											}

											var currencyCode = boxelement.value;
											var accountCurrency = { international : internationalChecked, domestic : domesticChecked, currencyCode: currencyCode, password: password, txnKey: txnKey, adf1: adf1, adf2: adf2, adf3: adf3, adf4: adf4, adf5: adf5, adf6: adf6, adf7: adf7, adf8: adf8, adf9: adf9, adf10: adf10, adf11: adf11, merchantId: merchant, directTxn: directTxn };
											accountCurrencySet.push(accountCurrency);

										}
									}


									var data = {
                                        merchantEmailId: merchantId,
                                        acquirer: acquirer,
                                        userType: userType,
                                        mapString: mappingString,
                                        emailId: emailId,
                                        accountCurrencySet: JSON.stringify(accountCurrencySet),
                                        token: token,
                                        "struts.token.name": "token",
                                    }

									console.log(data);

									// return;

									if (merchantId == null || merchantId == undefined || merchantId == "" || acquirer == "" || (accountCurrencySet.length == 0 && mappingString != "")) {
										alert("Map atleast one currency !");
										document.getElementById("err2").style.display = "block";
										evt.preventDefault();
									} else if (mappingString === "" && paymentTypeCheckedFlag) {
										alert("Please select payment types")
										evt.preventDefault();
									} else {
										var token = document.getElementsByName("token")[0].value;
										//to show new loader -Harpreet
										$.ajaxSetup({
											global: false,
											beforeSend: function () {
												toggleAjaxLoader();
											},
											complete: function () {
												toggleAjaxLoader();
											}
										});
										$.ajax({
											type: "POST",
											url: "mopSetUp",
											timeout: 0,
											data: {
												merchantEmailId: merchantId,
												acquirer: acquirer,
												userType: userType,
												mapString: mappingString,
												emailId: emailId,
												international: internationalChecked,
												domestic: domesticChecked,
												accountCurrencySet: JSON.stringify(accountCurrencySet),
												token: token,
												"struts.token.name": "token",

											},
											success: function (
												data,
												status) {
												var response = data.response;
												alert(response);
												/* 		window.location
																.reload(); */
											},
											error: function (status) {
												alert("Error, mapping not saved!!!! ");
												window.location.reload();
											}
										})
									}
								});
					});
		</script>
		<script>
			$(document).ready(function () {
				// $("#submit").click(function (e) {

				// }),
					e.preventDefault();
				$.ajaxSetup({
					global: false,
					beforeSend: function () {
						toggleAjaxLoader();
					},
					complete: function () {
						toggleAjaxLoader();
					}
				});
			});
		</script>
		<style>
			ul.filters {
				list-style: none;
			}
			ul.filters .fa-check:before{
				content: unset;
			}
		</style>
	</head>

	<body>
		<div class="content d-flex flex-column flex-column-fluid" id="kt_content">
			<!--begin::Toolbar-->
			<div class="toolbar" id="kt_toolbar">
				<!--begin::Container-->
				<div id="kt_toolbar_container" class="container-fluid d-flex flex-stack">
					<!--begin::Page title-->
					<div data-kt-swapper="true" data-kt-swapper-mode="prepend"
						data-kt-swapper-parent="{default: '#kt_content_container', 'lg': '#kt_toolbar_container'}"
						class="page-title d-flex align-items-center me-3 flex-wrap mb-5 mb-lg-0 lh-1">
						<!--begin::Title-->
						<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">Pay-in Merchant Mapping</h1>
						<!--end::Title-->
						<!--begin::Separator-->
						<span class="h-20px border-gray-200 border-start mx-4"></span>
						<!--end::Separator-->
						<!--begin::Breadcrumb-->
						<ul class="breadcrumb breadcrumb-separatorless fw-semibold fs-7 my-1">
							<!--begin::Item-->
							<li class="breadcrumb-item text-muted"><a href="home"
									class="text-muted text-hover-primary">Dashboard</a></li>
							<!--end::Item-->
							<!--begin::Item-->
							<li class="breadcrumb-item"><span class="bullet bg-gray-200 w-5px h-2px"></span></li>
							<!--end::Item-->
							<!--begin::Item-->
							<li class="breadcrumb-item text-muted">Merchant Billing</li>
							<!--end::Item-->
							<!--begin::Item-->
							<li class="breadcrumb-item"><span class="bullet bg-gray-200 w-5px h-2px"></span></li>
							<!--end::Item-->
							<!--begin::Item-->
							<li class="breadcrumb-item text-dark">Pay-in Merchant Mapping</li>
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
					<div class="transactions"></div>
					<s:form theme="simple" id="mopSetupForm" name="mopSetupForm" action="mopSetUpAction">
						<table width="100%" border="0" align="center" cellpadding="0" cellspacing="0">
							<tr>
								<td colspan="4" class="txtnew" align="left" style="padding: 10px;">
									<div id="err" class="actionMessage" style="display: none; margin-bottom: 10px;">
										<ul>
											<li>Please select user, merchant and acquirer to proceed</li>
										</ul>
									</div>
									<div id="err2" class="actionMessage" style="display: none; margin-bottom: 10px;">
										<ul>
											<li>Atleast one currency is required for Merchant Mapping</li>
										</ul>
									</div>
									<div id="success" class="success success-text" style="display: none;">Mappings saved successfully</div>
								</td>
							</tr>
							<tr>
								<td>
									<div class="wd70">
										<table>
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

													<div class="row my-5 mb-4">
														<!--begin::Col-->
														<!-- <div class="col-md-4 fv-row">
															<label class="d-flex align-items-center fs-6 fw-bold mb-2">
																<span class="">Select User</span>
															</label>
															<s:select label="User" list="#{'1':'Merchant'}"
																class="form-select form-select-solid" id="user"
																name="user" value="1" />

														</div> -->


														<div class="col-md-4 fv-row">
															<label class="d-flex align-items-center fs-6 fw-bold mb-2">
																<span class="">Merchant</span>
															</label>
															<s:select label="Merchant" name="merchantEmailId"
																class="form-select form-select-solid" id="merchants"
																headerKey="" headerValue="Select Merchant"
																list="listMerchant" listKey="emailId"
																listValue="businessName" value="%{merchantEmailId}"
																onchange="resetAcq()" autocomplete="off"
																data-control="select2" />
														</div>
														<div class="col-md-4 fv-row">
															<label class="d-flex align-items-center fs-6 fw-bold mb-2">
																<span class="">Acquirer</span>
															</label>
															<!--end::Label-->
															<s:select label="Merchant" name="acquirer"
																class="form-select form-select-solid" id="acquirer"
																headerKey="" headerValue="Select Acquirer"
																list="@com.pay10.commons.util.AcquirerTypeUI@values()"
																listKey="code" listValue="name" onchange="getMops()"
																autocomplete="off" />
														</div>
													</div>
												</div>
											</div>
											<tr>
												<td style="width:100%">

													<div class="">
														<div class="">
															<div class="row my-5">
																<div class="col-md-12">
																	<div id="id+checkBoxes" class="filters mb-2" style="display: none">
																		<s:set value="0" var="listCounter" />
																		<s:iterator value="currencies"  var="currency">
																			<s:set var="currencyKey" value="#currency.key"/>
    																		<s:set var="currencyValue" value="#currency.value"/>
																			<s:checkbox name="currency" id="%{'id+' +key}" fieldValue="%{key}"
																				value="false" onclick="hidefields(this)"></s:checkbox>
																			<b style="font-size: 15px;font-weight: 600;">
																				<s:property value="value" />
																			</b>
																			<div id="boxdivid+<s:property value="key" />" style="display: none">
																				<div id="elements<s:property value="key" />">
																					<div class="row  mb-4">
																						<div class="col-sm-6 col-lg-4">
																							<label class="d-flex align-items-center fs-6 fw-bold mb-2">Merchant ID</label>
																							<s:textfield name="merchantId" id="%{'idmerchantid+' +key}" value="%{merchantId}" placeholder="MerchantId" autocomplete="off" class="form-control form-control-solid"></s:textfield>
																						</div>
																						<div class="col-sm-6 col-lg-4">
																							<label class="d-flex align-items-center fs-6 fw-bold mb-2">Txn Key</label>
																							<s:textfield name="txnKey" id="%{'idtxnkey+' +key}" value="%{txnKey}" placeholder="Txn Key" autocomplete="off" class="form-control form-control-solid"></s:textfield>
																						</div>
																						<div class="col-sm-6 col-lg-4">
																							<label class="d-flex align-items-center fs-6 fw-bold mb-2">Password</label>
																							<s:textfield name="password" id="%{'idpassword+' +key}" value="%{password}" placeholder="Password" autocomplete="off" class="form-control form-control-solid"></s:textfield>
																						</div>
																						<div class="col-sm-6 col-lg-4">
																							<s:if test="%{acquirer  in {'FSS','CITRUS'}}">
																							<label class="d-flex align-items-center fs-6 fw-bold mb-2">Non 3DS</label>>
																								<s:checkbox name="directTxn" id="%{'id3dflag+' +key}" style="display:inline;" type="text"	value="%{directTxn}"></s:checkbox>
																							</s:if>
																						</div>
																					</div>
																					<div class="row  mb-4">
																						<div class="col-sm-6 col-lg-4">
																							<label
																								class="d-flex align-items-center fs-6 fw-bold mb-2">Adf1</label>
																							<s:textfield name="adf1" id="%{'idadf1+' +key}" value="%{adf1}" placeholder="" autocomplete="off" class="adf-control form-control form-control-solid"></s:textfield>
																						</div>
																						<div class="col-sm-6 col-lg-4">
																							<label class="d-flex align-items-center fs-6 fw-bold mb-2">Adf2</label>
																							<s:textfield name="adf2" id="%{'idadf2+' +key}" value="%{adf2}" placeholder="" autocomplete="off" class="adf-control form-control form-control-solid"></s:textfield>
																						</div>
																						<div class="col-sm-6 col-lg-4">
																							<label class="d-flex align-items-center fs-6 fw-bold mb-2">Adf3</label>
																							<s:textfield name="adf3" id="%{'idadf3+' +key}" value="%{adf3}" placeholder="" autocomplete="off" class="adf-control form-control form-control-solid"></s:textfield>
																						</div>
																					</div>
																					<div class="row  mb-4">
																						<div class="col-sm-6 col-lg-4">
																							<label class="d-flex align-items-center fs-6 fw-bold mb-2">Adf4</label>
																							<s:textfield name="adf4" id="%{'idadf4+' +key}" value="%{adf4}" placeholder="" autocomplete="off" class="adf-control form-control form-control-solid"> </s:textfield>
																						</div>
																						<div class="col-sm-6 col-lg-4">
																							<label class="d-flex align-items-center fs-6 fw-bold mb-2">Adf5</label>
																							<s:textfield name="adf5" id="%{'idadf5+' +key}" value="%{adf5}"	placeholder="" autocomplete="off"	class="adf-control form-control form-control-solid"></s:textfield>
																						</div>
																						<div class="col-sm-6 col-lg-4">
																							<label class="d-flex align-items-center fs-6 fw-bold mb-2">Adf6</label>
																							<s:textfield name="adf6" id="%{'idadf6+' +key}" value="%{adf6}" placeholder="" autocomplete="off" class="adf-control form-control form-control-solid"></s:textfield>
																						</div>
																					</div>
																					<div class="row  mb-4">
																						<div class="col-sm-6 col-lg-4">
																							<label class="d-flex align-items-center fs-6 fw-bold mb-2">Adf7</label>
																							<s:textfield name="adf7" id="%{'idadf7+' +key}" value="%{adf7}" placeholder="" autocomplete="off" class="adf-control form-control form-control-solid"></s:textfield>
																						</div>
																						<div class="col-sm-6 col-lg-4">
																							<label class="d-flex align-items-center fs-6 fw-bold mb-2">Adf8</label>
																							<s:textfield name="adf8" id="%{'idadf8+' +key}" value="%{adf8}" placeholder="" autocomplete="off" class="adf-control form-control form-control-solid"></s:textfield>
																						</div>
																						<div class="col-sm-6 col-lg-4">
																							<label class="d-flex align-items-center fs-6 fw-bold mb-2">Adf9</label>
																							<s:textfield name="adf9" id="%{'idadf9+' +key}" value="%{adf9}"	placeholder="" autocomplete="off" class="adf-control form-control form-control-solid"></s:textfield>
																						</div>
																					</div>
																					<div class="row  mb-4">
																						<div class="col-sm-6 col-lg-4">
																							<label class="d-flex align-items-center fs-6 fw-bold mb-2">Adf10</label>
																							<s:textfield name="adf10" id="%{'idadf10+' +key}" value="%{adf10}" placeholder="" autocomplete="off" class="adf-control form-control form-control-solid"></s:textfield>
																						</div>
																						<div class="col-sm-6 col-lg-4">
																							<label class="d-flex align-items-center fs-6 fw-bold mb-2">Adf11</label>
																							<s:textfield name="adf11" id="%{'idadf11+' +key}" value="%{adf11}" placeholder="" autocomplete="off" class="adf-control form-control form-control-solid"></s:textfield>
																						</div>
																						<br>

																						<div class="mt-4">
																							<span style="display: inline;">Transaction Region : </span>
																							<s:checkbox name="international" cssClass="transaction-region" id="%{'idinternational+' +key}" style="display:inline;" value="international" checked="%{international}"></s:checkbox>
																							<span style="display: inline;" class="checkbox-align" >International</span>
																							<s:checkbox name="domestic" cssClass="transaction-region" id="%{'iddomestic+' +key}" style="display:inline;" value="domestic" checked="%{domestic}"></s:checkbox>
																							<span style="display: inline;" class="checkbox-align ">Domestic</span>
																						</div>
																						<br>
																						<div class="col-sm-12 col-lg-12 mt-4">
																							<s:iterator value="%{merchantMapping[key]}"  var="merchantEntry" status="itStatus">
																							<s:set var="merchantEntryKey" value="#merchantEntry.key"/>
    																						<s:set var="merchantEntryValue" value="#merchantEntry.value"/>

																								<ul class="filters">
																									<li>
																										<label class="d-inline align-items-center fs-6 fw-bold mb-2">
																											<s:checkbox name="paymentSelectedList" type="checkbox" id="%{#currencyKey+#merchantEntryKey+'box'}" value="false" onclick="showMe('%{#currencyKey+#merchantEntryKey}', this)" cssClass="roundedTwo"></s:checkbox>
																												<span class="icon"><i class="fa fa-check"></i></span>
																											<s:property value="key" />
																										</label></li>
																									<li>
																										<div id="<s:property value="%{#currencyKey+#merchantEntryKey}" />" style="display:none; padding-left:20px; padding-top:1px; padding-bottom:1px; width :400px;">
																											<s:if
																												test="%{key=='Net Banking'}">
																												<label class="d-inline align-items-center fs-6 fw-bold mb-2">
																													<s:checkbox name="selectAll" value="false" id="%{#currencyKey+#merchantEntryKey+'selectAllButton'}" cssClass="ignore-check" fieldValue="selectAll" label="All" onclick="selectAllCheckboxesWithinDiv('%{#currencyKey+#merchantEntryKey}')"></s:checkbox>
																													<span class="redsmalltext"><b>Select All</b></span>
																												</label>
																												<br />
																											</s:if>

																											<s:iterator value="value" status="mopStatus">
																												<s:if
																													test="%{[1].key!=('Net Banking')}">
																													<label class="d-inline align-items-center fs-6 fw-bold mb-2 d-flex">
																													<s:checkbox name="mopSelectedList" id="%{#currencyKey+'-'+[1].key+'-'+code}" cssClass="ignore-check" fieldValue="%{name}" label="%{name}" value="false" onclick="showMe('%{#currencyKey+[1].key+'-'+code}', this)"></s:checkbox> <span class="redsmalltext"><b>
																														<s:property value="name" />
																															</b>
																														</span>&nbsp;&nbsp;
																													</label>
																													<div id='<s:property value="%{#currencyKey+[1].key+'-'+code}"/>' style="display:none; padding-left:20px; padding-top:10px; padding-bottom:10px; width :400px;">
																														<s:iterator value="transList" status="txnStatus">
																															<label class="d-inline align-items-center fs-6 fw-bold mb-2">
																															<s:checkbox id="%{#currencyKey+'-'+[2].key+'-'+[1].code+'-'+code}" name="txnSelectedList" fieldValue="%{name}" label="%{name}" value="false"></s:checkbox>
																																<span class="black9"> <s:property value="name" /></span>&nbsp;&nbsp;
																															</label>
																														</s:iterator>
																													</div>
																												</s:if>
																												<s:else>
																													<div cssStyle="padding-left:0px; padding-top:5px; padding-bottom:5px; float:left; width :400px;">
																														<label class="netbankinglabel d-flex">
																															<s:checkbox name="mopSelectedList" id="%{#currencyKey+'-'+key+'-'+code}" fieldValue="%{name}" label="%{name}" value="false"></s:checkbox>
																															<span class="black9"><b> <s:property value="name" /></b></span>&nbsp;&nbsp;
																														</label>
																													</div>
																												</s:else>
																											</s:iterator>
																										</div>
																									</li>
																								</ul>
																							</s:iterator>
																							<hr>
																						</div>
																					</div>

																				</div>
																			</div>
																			<br />

																		</s:iterator>
																		<br />
																		<br />
																		<div height="40" valign="bottom" colspan="4" align="center">
																			<s:textfield type="button" value="Save" class="btn btn-primary  mt-4 submit_btn"	id="btnsubmit" theme="simple" />
																		</div>
																	</div>
																</div>
															</div>
														</div>
													</div>
									</div>
								</td>
								<td align="left">&nbsp;</td>
							</tr>
							<tr>
								<td width="96" height="5" align="left"></td>
								<td width="195" height="5" align="left"></td>
								<td colspan="4" align="left"></td>
							</tr>
							<tr>
								<td height="30" colspan="4" align="left">&nbsp;</td>
							</tr>
						</table>
						<br /><br />
						<div class="clear"></div>
				</div>
				</td>
				</tr>
				</table>
				<s:hidden name="token" value="%{#session.customToken}"></s:hidden>
				</s:form>
			</div>
		</div>
		<script type="text/javascript">
			$(document).ready(function () {
				if ($('#mopSetUpAction').hasClass("active")) {
					var menuAccess = document.getElementById("menuAccessByROLE").value;
					var accessMap = JSON.parse(menuAccess);
					var access = accessMap["mopSetUpAction"];
					if (access.includes("Update")) {
						$("#merchants").removeAttr("disabled");
						$("#acquirer").removeAttr("disabled");
					}
				}
			});
		</script>
		<script type="text/javascript">
			// Initialize select2
			$("#merchants").select2();
			function toggleAjaxLoader(){
	$('body').toggleClass('loaded');
}
		</script>
		<script>
		
		document.getElementById('poCheckbox').addEventListener('change', function () { 
			if (this.checked) {
				window.location.href = 'mopSetUpActionPO'; } }); 
		</script>
		
	</body>

	</html>