<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="/struts-tags" prefix="s"%>
<html>

<head>
<meta http-equiv="cache-control" content="no-cache">
<!-- tells browser not to cache -->
<meta http-equiv="expires" content="0">
<!-- says that the cache expires 'now' -->
<meta http-equiv="pragma" content="no-cache">
<!-- says not to use cached stuff, if there is any -->
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Reseller Commission</title>
<script src="../assets/plugins/global/plugins.bundle.js"></script>
<script src="../assets/js/scripts.bundle.js"></script>
<script type="text/javascript" src="../js/jquery.min.js"></script>
<script src="../js/jquery.select2.js" type="text/javascript"></script>
<script src="../js/resellerCommission.js"></script>



<style type="text/css">
.card-list-toggle {
	padding: 8px 12px;
	background: linear-gradient(60deg, #425185, #4a9b9b);
}

.card-list-toggle.active:before {
	display: none !important;
}

.card-list-toggle.active:after {
	display: none !important;
}

.card-list {
	display: none;
}

.btn:focus {
	outline: 0 !important;
}

.sub-headingCls {
	margin-top: 20px;
	margin-bottom: 0px;
	font-size: 12px;
	font-weight: 700;
	color: #0271bb;
}

label {
	font-size: 14px;
	font-weight: 500;
	color: grey;
}

.col-sm-6.col-lg-3 {
	margin-bottom: 10px;
}

#buttonshow {
	margin-top: 25px;
}

.cancelBtn {
	padding-right: 65px;
	height: 45px;
	width: 80px;
}

.edit1 {
	padding-right: 65px;
	height: 45px;
	width: 80px;
}

.btn.btn-primary.btn-xs {
	padding-right: 65px;
	height: 45px;
	width: 80px;
}

#edit2 {
	padding-right: 65px;
	height: 45px;
	width: 80px;
}
</style>
<script type="text/javascript">
				$(document).ready(function (e) {
					var userType = "<s:property value='%{#session.USER.UserGroup.group}'/>";
					if (userType == 'Reseller') {
						$('#resellers').hide();
						var merchantId = document.getElementById("departmentsDropdown").value;
						if (merchantId == '' || merchantId == null) {
							getMerchant();
						}
					}
				});
			</script>
<script type="text/javascript">
				$(document).ready(function () {

					//$("#merchants").select2();
					//$("#departmentsDropdown").select2();



					$('.card-list-toggle').on('click', function () {
						$(this).toggleClass('active');
						$(this).next('.card-list').slideToggle();
					});
				});

				var editMode;
				function editCurrentRow(divId, curr_row, ele) {

					var div = document.getElementById(divId);

					var table = div.getElementsByTagName("table")[0];

					var merchantId = document.getElementById("merchants").value;
					var acquirer = document.getElementById("departmentsDropdown").value;

					var rows = table.rows;
					var currentRowNum = Number(curr_row);
					var currentRow = rows[currentRowNum];
					console.log(currentRow);
					var cells = currentRow.cells;
					var cell0 = cells[0];
					var cell1 = cells[1];
					var cell2 = cells[2];
					var cell3 = cells[3];
					var cell4 = cells[4];
					var cell5 = cells[5];
					var cell6 = cells[6];
					var cell7 = cells[7];
					var cell0Value = cell0.innerText.trim();
					var cell1Value = cell1.innerText.trim();
					var cell2Value = cell2.innerText.trim();
					var cell3Value = cell3.innerText.trim();
					var cell4Value = cell4.innerText.trim();
					var cell5Value = cell5.innerText.trim();
					var cell6Value = cell6.innerText.trim();
					var id = cells[7].innerText.trim();
					if (ele.value == "edit") {
						var commissiontype = cell4.querySelector('option[selected=true]').value;
						if (editMode) {
							alert('Please edit the current row to proceed');
							return;
						}

						ele.value = "save";
						ele.className = "btn btn-primary btn-xs";


						cell2.innerHTML = "<input type='number' id='cell2Val' class='chargingplatform' min='0'  onkeypress='return isPositiveNumber(event)'   step='0.0' value=" + cell2Value + "></input>";

						cell3.innerHTML = "<input type='number' id='cell3Val'  class='chargingplatform' min='0'  onkeypress='return isPositiveNumber(event)' ' step='0.0' value=" + cell3Value + "></input>";
						cell6.innerHTML = "<input type='number' id='cell6Val'  class='chargingplatform' min='0'  onkeypress='return isPositiveNumber(event)'  step='0.0' value=" + cell6Value + "></input>";

						cell4.innerHTML = "";
						if (commissiontype == 'true') {
							var selectElement = '<select id="cell4Val" class="selectbox-class"><option value="true" selected="true">Higher</option><option value="false">Lower</option></select>';
							cell4.innerHTML = selectElement;
						} else {
							var selectElement = '<select id="cell4Val" class="selectbox-class"><option value="true">Higher</option><option value="false" selected="true">Lower</option></select>';
							cell4.innerHTML = selectElement;
						}
						let selectBoxes = document.getElementsByClassName('selectbox-class');
						for (let i = 0; i < selectBoxes.length; i++) {
							$(selectBoxes[i]).select2();
						}
						cell5.innerHTML = "<input type='number' id='cell5Val'  class='chargingplatform' min='0' onkeypress='return isPositiveNumber(event)' step='0.0' value=" + cell5Value + "></input>";
						editMode = true;
					}

					else {
						var baserate = parseFloatWithDecimal(document.getElementById('cell2Val').value, 2);
						var merchant_mdr = parseFloatWithDecimal(document.getElementById('cell3Val').value, 2);
						var commission_percent = parseFloatWithDecimal(document.getElementById('cell5Val').value, 2);
						var commission_amount = parseFloatWithDecimal(document.getElementById('cell6Val').value, 2);
						var commissiontype = cell4.querySelector('span[id]').innerHTML == 'Higher';
						if (baserate > '100.00') {
							alert("base rate percentage can not be greater than 100")
							return false;
						}
						if (merchant_mdr > '100.00') {
							alert("merchant_mdr percentage can not be greater than 100")
							return false;
						}
						if (commission_percent > '100.00') {
							alert("commission_percent can not be greater than 100")
							return false;
						}
						cell2.innerHTML = baserate;
						cell3.innerHTML = merchant_mdr;
						cell5.innerHTML = commission_percent;
						cell6.innerHTML = commission_amount;

						editMode = false;
						$('#loader-wrapper').show();
						var userType = "<s:property value='%{#session.USER.UserType.name()}'/>";
						var added_by = "<s:property value='%{#session.USER.EmailId}'/>";
						ele.value = "edit";
						ele.className = "btn btn-primary btn-xs";
						var token = document.getElementsByName("token")[0].value;
						$.ajax({
							type: "POST",
							url: "editcommisionDetail",
							timeout: 0,
							data: {
								"id": id, "emailId": merchantId, "baserate": baserate, "merchant_mdr": merchant_mdr, "commissiontype": commissiontype, "commission_percent": commission_percent, "commission_amount": commission_amount
								, "added_by": added_by
							},
							success: function (data) {
								var response = ((data["Invalid request"] != null) ? (data["Invalid request"].response[0]) : (data.response));
								if (null != response) {
									alert(response);
								}
								cells[7].innerHTML = data["id"];


								$('#loader-wrapper').hide();
							},
							error: function (data) {
								alert("Network error, commission detail may not be saved");
							}
						});
					}
				}

				function cancel(curr_row, ele) {
					var parentEle = ele.parentNode;

					if (editMode) {
						$('#loader-wrapper').show();
						window.location.reload();
					}
				}

			</script>


<style>
.product-spec input[type=text] {
	width: 35px;
}

.btn {
	text-transform: capitalize;
}

.form-control {
	display: block;
	width: 100% !important;
	height: 28px;
	padding: 3px 4px;
	font-size: 14px;
	line-height: 1.42857143;
	color: #555;
	background-color: #fff;
	background-image: none;
	border: 1px solid #ccc;
	border-radius: 4px;
	-webkit-box-shadow: inset 0 1px 1px rgba(0, 0, 0, .075);
	box-shadow: inset 0 1px 1px rgba(0, 0, 0, .075);
	-webkit-transition: border-color ease-in-out .15s, -webkit-box-shadow
		ease-in-out .15s;
	-o-transition: border-color ease-in-out .15s, box-shadow ease-in-out
		.15s;
	transition: border-color ease-in-out .15s, box-shadow ease-in-out .15s;
}

table.product-spec th {
	font-size: 14px;
	padding: 8px;
	background: #fbfcfd;
	color: #262424;
}

table.boxheading tr {
	background: #fbfcfd;
	color: #262424;
}

table td input {
	width: 55px;
}
</style>
</head>

<body>

	<div class="content d-flex flex-column flex-column-fluid"
		id="kt_content">
		<s:actionmessage class="error error-new-text" />


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
					<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">
						Reseller Commission</h1>
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
						<li class="breadcrumb-item text-muted">Reseller Setup</li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item"><span
							class="bullet bg-gray-200 w-5px h-2px"></span></li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item text-dark">Reseller Commission</li>
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

		<form id="getchangpaymenttype" action="getchangpaytype" method="post">



			<div class="post d-flex flex-column-fluid" id="kt_post">
				<div id="kt_content_container" class="container-xxl">
					<div class="row my-5">
						<div class="col">
							<div class="card">
								<div class="card-body ">

									<!--begin::Input group-->
									<div class="row g-9 mb-8">
										<!--begin::Col-->

										<div class="col-md-3 fv-row" id="resellers">
											<label class="d-flex align-items-center fs-6 fw-bold mb-2"
												style="float: left;"> <span class="">Select
													Reseller</span></label>

											<s:if test="%{#session.USER.UserGroup.group =='Reseller'}">
												<s:select name="merchantpayId"
													class="form-select form-select-solid" id="merchants"
													list="listReseller" listKey="payId"
													listValue="businessName" onchange="getMerchant();"
													autocomplete="off" style="margin-left: -4px;" />
											</s:if>
											<s:else>
												<s:select headerValue="Select Reseller" headerKey=""
													name="merchantpayId" class="form-select form-select-solid "
													id="merchants" list="listReseller" listKey="payId"
													listValue="businessName" onchange="getMerchant();"
													autocomplete="off" style="margin-left: -4px;" />
											</s:else>

										</div>

										<div class="col-md-3 fv-row" id="tomerchant">
											<label class="d-flex align-items-center fs-6 fw-bold mb-2">
												<span>Select Merchant</span>
											</label>

											<s:select class="form-select form-select-solid"
												id="departmentsDropdown" name="merchantname"
												list="merchantlist" listKey="payId" listValue="businessName" onchange="getCurrencyList();"
												autocomplete="off" style="margin-left: -4px;" />
										</div>

										<div class="col-md-3 fv-row" id="currencyListDropdown">
											<label class="d-flex align-items-center fs-6 fw-bold mb-2">
												<span class="">Select Currency</span>
											</label>
											<s:select class="form-select form-select-solid"
												name="currencyData" id="currency" autocomplete="off"
												list="listCurrency" listKey="code" listValue="name" value="currencyData"
												style="margin-left: -4px;" />

										</div>

										<div class="col-md-2 fv-row">
											<button type="button" class="btn btn-primary btn-xs"
												id="buttonshow" onClick="getDetails();"
												style="display: block;">view</button>
										</div>


									</div>




								</div>
							</div>
						</div>
					</div>

					<div class="row my-5">
						<div class="col">
							<div class="card">
								<div class="card-body ">
									<div id="datatable"
										class="table table-striped table-row-bordered gy-5 gs-7">
										<s:iterator value="ResellercommisionMap" status="pay" var="x">
											<br>
											<div class="text-primary card-list-toggle"
												style="background: #fbfcfd;">
												<strong> <s:property value="key" />
												</strong>
											</div>
											<div class="scrollD">
												<div id="<s:property value="key" />Div">
													<table width="100%" border="0" align="center"
														class="display table table-striped table-row-bordered gy-5 gs-7">
														<tr class="boxheading fw-bold fs-6 text-gray-800">

															<th width="4%" align="left" valign="middle">Mop</th>
															<th width="6%" align="left" valign="middle">Transaction</th>

															<th width="7%" align="left" valign="middle"
																style="display: none">Base Rate</th>
															<th width="6%" align="left" valign="middle"
																style="display: none">Merchant MDR</th>
															<th width="5%" align="left" valign="middle">
																Reseller Commission Type</th>
															<th width="5%" align="left" valign="middle">
																Reseller Commission %</th>
															<th width="5%" align="left" valign="middle">
																Reseller Commission FC</th>
															<th width="2%" align="left" valign="middle"
																style="display: none">id</th>
															<th width="5%" align="left" valign="middle">update</th>
															<th width="5%" align="left" valign="middle"><span
																id="cancelLabel">Cancel</span></th>
														</tr>
														<s:iterator value="value" status="itStatus">
															<tr class="boxtext">
																<td align="left" valign="middle"><s:property
																		value="mop" /></td>
																<td align="left" valign="middle"><s:property
																		value="transactiontype" /></td>
																<td align="left" valign="middle" style="display: none">
																	<s:property value="baserate" />
																</td>
																<td align="left" valign="middle" style="display: none">
																	<s:property value="merchant_mdr" />
																</td>

																<td align="center" valign="middle"><select
																	id="commissiontype" disabled="disabled"
																	name="commissiontype" value="commissiontype">
																		<option value="true"
																			<s:if test="commissiontype eq true">
																					selected
																					= true
																					</s:if>>Higher
																		</option>
																		<option value="false"
																			<s:if test="commissiontype eq false">
																					selected
																					= true
																					</s:if>>Lower
																		</option>
																</select></td>
																<td align="left" valign="middle"><s:property
																		value="commission_percent" /></td>
																<td align="left" valign="middle"><s:property
																		value="commission_amount" /></td>

																<td align="center" valign="middle" style="display: none">
																	<s:property value="id" />
																</td>

																<td align="center" valign="middle">
																	<div>
																		<s:if
																			test="%{#session.USER.UserGroup.group =='Reseller'}">
																			<s:textfield id="edit%{#itStatus.count}"
																				name="editTDR" value="edit" type="button"
																				disabled="true"
																				onclick="editCurrentRow('%{key +'Div'}','%{#itStatus.count}', this)"
																				class="btn btn-primary btn-xs edit1"
																				autocomplete="off">
																			</s:textfield>
																		</s:if>
																		<s:else>
																			<s:textfield id="edit%{#itStatus.count}"
																				name="editTDR" value="edit" type="button"
																				onclick="editCurrentRow('%{key +'Div'}','%{#itStatus.count}', this)"
																				class="btn btn-primary btn-xs edit1"
																				autocomplete="off">
																			</s:textfield>
																		</s:else>
																	</div>
																</td>

																<td align="center" valign="middle"><s:textfield
																		id="cancelBtn%{#itStatus.count}" value="cancel"
																		type="button"
																		onclick="cancel('%{#itStatus.count}',this)"
																		class="btn btn-danger btn-xs cancelBtn"
																		autocomplete="off">
																	</s:textfield></td>
															</tr>
														</s:iterator>
													</table>
												</div>
											</div>
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


	</div>
	</div>








	<s:hidden name="token" value="%{#session.customToken}"></s:hidden>
	</form>

	</div>

	<script>

				function isPositiveNumber(evt) {
					var charCode = (evt.which) ? evt.which : event.keyCode
					if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46) {
						return false;
					}
					return true;
				}

				function parseFloatWithDecimal(str, val) {
					str = str.toString();
					if (str.includes(".")) {
						str = str.slice(0, (str.indexOf(".")) + val + 1);
					}
					return Number(str);
				}

			</script>
	<script>
				$('#datatable').on('draw.dt', function () {
					enableBaseOnAccess();
				});
				function enableBaseOnAccess() {
					setTimeout(function () {
						if ($('#getchangpaytype').hasClass("active")) {
							var menuAccess = document.getElementById("menuAccessByROLE").value;
							var accessMap = JSON.parse(menuAccess);
							var access = accessMap["getchangpaytype"];
							if (access.includes("Commission")) {
								var edits = document.getElementsByName("editTDR");
								for (var i = 0; i < edits.length; i++) {
									var edit = edits[i];
									edit.disabled = false;
								}
							}
						}
					}, 500);
				}
			</script>
</body>

</html>