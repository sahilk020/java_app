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
<title>User Commission</title>
 <script src="../assets/plugins/global/plugins.bundle.js"></script>
<script src="../assets/js/scripts.bundle.js"></script>
<script type="text/javascript" src="../js/jquery.min.js"></script>
<script src="../js/jquery.select2.js" type="text/javascript"></script> 
<script src="../js/userCommission.js"></script>



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
					//var userType = "<s:property value='%{#session.USER.UserGroup.group}'/>";
					//if (userType == 'Reseller') {
						/* $('#resellers').hide();
						var merchantId = document.getElementById("merchantDropdown").value;
						if (merchantId == '' || merchantId == null) {
							getMerchant();
						}
					} */
				});
			</script>
<script type="text/javascript">
				$(document).ready(function () {

					//$("#smaPayId").select2();
					//$("#maDropdown").select2();
					//$("#agentDropdown").select2();
					//$("#merchantDropdown").select2();



					$('.card-list-toggle').on('click', function () {
						$(this).toggleClass('active');
						$(this).next('.card-list').slideToggle();
					});
				});

				var editMode;
				function editCurrentRow(divId, curr_row, ele) {
                 debugger
					var div = document.getElementById(divId);

					var table = div.getElementsByTagName("table")[0];

					var smaPayId = document.getElementById("smaPayId").value;
					var maPayId = document.getElementById("maDropdown").value;
					var agentPayId = document.getElementById("agentDropdown").value;
					var merchantPayId = document.getElementById("merchantDropdown").value;
					

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
					var cell8 = cells[8];
					var cell9 = cells[9];
					var cell10 = cells[10];
					var cell11 = cells[11];
					
					var cell0Value = cell0.innerText.trim();
					var cell1Value = cell1.innerText.trim();
					var cell2Value = cell2.innerText.trim();
					var cell3Value = cell3.innerText.trim();
					var cell4Value = cell4.innerText.trim();
					var cell5Value = cell5.innerText.trim();
					var cell6Value = cell6.innerText.trim();
					var cell7Value = cell7.innerText.trim();
					var cell8Value = cell8.innerText.trim();
					var cell9Value = cell9.innerText.trim();
					var cell10Value = cell10.innerText.trim();
					
					var id = cells[9].innerText.trim();
					if (ele.value == "edit") {
						var commissiontype = cell2.querySelector('option[selected=true]').value;
						if (editMode) {
							alert('Please edit the current row to proceed');
							return;
						}

						ele.value = "save";
						ele.className = "btn btn-primary btn-xs";


						cell3.innerHTML = "<input type='number' id='cell3Val' class='chargingplatform' min='0'  onkeypress='return isPositiveNumber(event)'   step='0.0' value=" + cell3Value + "></input>";

						cell4.innerHTML = "<input type='number' id='cell4Val'  class='chargingplatform' min='0'  onkeypress='return isPositiveNumber(event)' ' step='0.0' value=" + cell4Value + "></input>";
						cell5.innerHTML = "<input type='number' id='cell5Val'  class='chargingplatform' min='0'  onkeypress='return isPositiveNumber(event)'  step='0.0' value=" + cell5Value + "></input>";
						cell6.innerHTML = "<input type='number' id='cell6Val' class='chargingplatform' min='0'  onkeypress='return isPositiveNumber(event)'   step='0.0' value=" + cell6Value + "></input>";

						cell7.innerHTML = "<input type='number' id='cell7Val'  class='chargingplatform' min='0'  onkeypress='return isPositiveNumber(event)' ' step='0.0' value=" + cell7Value + "></input>";
						cell8.innerHTML = "<input type='number' id='cell8Val'  class='chargingplatform' min='0'  onkeypress='return isPositiveNumber(event)'  step='0.0' value=" + cell8Value + "></input>";
						

						cell2.innerHTML = "";
						if (commissiontype == 'true') {
							var selectElement = '<select id="cell2Val" class="selectbox-class"><option value="true" selected="true">Higher</option><option value="false">Lower</option></select>';
							cell2.innerHTML = selectElement;
						} else {
							var selectElement = '<select id="cell2Val" class="selectbox-class"><option value="true">Higher</option><option value="false" selected="true">Lower</option></select>';
							cell2.innerHTML = selectElement;
						}
						let selectBoxes = document.getElementsByClassName('selectbox-class');
						for (let i = 0; i < selectBoxes.length; i++) {
							$(selectBoxes[i]).select2();
						}
						//cell5.innerHTML = "<input type='number' id='cell5Val'  class='chargingplatform' min='0' onkeypress='return isPositiveNumber(event)' step='0.0' value=" + cell5Value + "></input>";
						editMode = true;
					}

					else {
						var sma_commission_percent = parseFloatWithDecimal(document.getElementById('cell3Val').value, 2);
						var sma_commission_amount = parseFloatWithDecimal(document.getElementById('cell4Val').value, 2);
						var ma_commission_percent = parseFloatWithDecimal(document.getElementById('cell5Val').value, 2);
						var ma_commission_amount = parseFloatWithDecimal(document.getElementById('cell6Val').value, 2);
						var agent_commission_percent = parseFloatWithDecimal(document.getElementById('cell7Val').value, 2);
						var agent_commission_amount = parseFloatWithDecimal(document.getElementById('cell8Val').value, 2);
						var commissiontype = cell2.querySelector('span[id]').innerHTML == 'Higher';
						$("#cell2Val").attr("disabled", true);
						if (sma_commission_percent > '100.00' || ma_commission_percent > '100.00' || agent_commission_percent > '100.00' ) {
							alert("Percentage can not be greater than 100")
							return false;
						}
						cell3.innerHTML = sma_commission_percent;
						cell4.innerHTML = sma_commission_amount;
						cell5.innerHTML = ma_commission_percent;
						cell6.innerHTML = ma_commission_amount;
						cell7.innerHTML = agent_commission_percent;
						cell8.innerHTML = agent_commission_amount;

						editMode = false;
						$('#loader-wrapper').show();
						var userType = "<s:property value='%{#session.USER.UserType.name()}'/>";
						var added_by = "<s:property value='%{#session.USER.EmailId}'/>";
						ele.value = "edit";
						ele.className = "btn btn-primary btn-xs";
						var token = document.getElementsByName("token")[0].value;
						$.ajax({
							type: "POST",
							url: "editUserCommissionDetail",
							timeout: 0,
							data: {
								"id": id, "sma_commission_percent": sma_commission_percent, "sma_commission_amount": sma_commission_amount, "ma_commission_percent": ma_commission_percent,"ma_commission_amount": ma_commission_amount, "commissiontype": commissiontype, "agent_commission_percent": agent_commission_percent, "agent_commission_amount": agent_commission_amount
								, "added_by": added_by
							},
							success: function (data) {
								var response = ((data["Invalid request"] != null) ? (data["Invalid request"].response[0]) : (data.response));
								if (null != response) {
									alert(response);
								}
								cells[9].innerHTML = data["id"];


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
						User Commission</h1>
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
						<li class="breadcrumb-item text-dark">User Commission</li>
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

		<form id="getchangpaymenttype" action="updateUserCommission"
			method="post">



			<div class="post d-flex flex-column-fluid" id="kt_post">
				<div id="kt_content_container" class="container-xxl">
					<div class="row my-5">
						<div class="col">
							<div class="card">
								<div class="card-body ">

									<!--begin::Input group-->
									<div class="row g-9 mb-8">
										<!--begin::Col-->

										<div class="col-md-3 fv-row" id="smas">
											<label class="d-flex align-items-center fs-6 fw-bold mb-2"
												style="float: left;"> <span class="">Select
													SMA</span></label>

											<s:select headerValue="Select SMA" headerKey=""
												name="smaPayId" class="form-select form-select-solid "
												id="smaPayId" list="listSma" listKey="payId"
												listValue="businessName" onchange="getMa();"
												autocomplete="off" style="margin-left: -4px;"  data-control="select-2" />

										</div>

										<div class="col-md-3 fv-row" id="toMa">
											<label class="d-flex align-items-center fs-6 fw-bold mb-2">
												<span>Select MA</span>
											</label>

											<s:select class="form-select form-select-solid"
												id="maDropdown" name="maPayId" list="listMa" listKey="payId"
												listValue="businessName" autocomplete="off"
												style="margin-left: -4px;" onchange="getAgent();"  data-control="select-2" />
										</div>

										<div class="col-md-3 fv-row" id=toAgent>
											<label class="d-flex align-items-center fs-6 fw-bold mb-2">
												<span>Select Agent</span>
											</label>

											<s:select class="form-select form-select-solid"
												id="agentDropdown" name="agentPayId" list="listAgent"
												listKey="payId" listValue="businessName" autocomplete="off"
												style="margin-left: -4px;" onchange="getMerchant();"  data-control="select-2" />
										</div>

										<div class="col-md-3 fv-row" id="tomerchant">
											<label class="d-flex align-items-center fs-6 fw-bold mb-2">
												<span>Select Merchant</span>
											</label>

											<s:select class="form-select form-select-solid"
												id="merchantDropdown" name="merchantPayId"
												list="merchantlist" listKey="payId" listValue="businessName" onchange="getCurrencyList();"
												autocomplete="off" style="margin-left: -4px;"  data-control="select-2" />
										</div>

										<div class="col-md-3 fv-row" id="currencyListDropdown">
											<label class="d-flex align-items-center fs-6 fw-bold mb-2">
												<span class="">Select Currency</span>
											</label>
												<s:select headerKey="Select Currency"
														headerValue="Select Currency"
														class="form-select form-select-solid"
														list="currencyMapList"
														name="currencyData"
														id="currency" autocomplete="off" />

										</div>

										<div class="col-md-2 fv-row">
											<button type="button" class="btn btn-primary btn-xs"
												id="buttonshow" onClick="getDetails();"
												style="display: block;">View</button>
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
										<s:iterator value="UsercommisionMap" status="pay" var="x">
											<br>
											<div class="text-primary card-list-toggle"
												style="background: #fbfcfd;">
												<strong> <s:property value="key" />
												</strong>
											</div>
											<div class="scrollD">
												<div id="<s:property value="key" />Div"  style="overflow-x: scroll;">
													<table width="100%" border="0" align="center"
														class="display table table-striped table-row-bordered gy-5 gs-7">
														<tr class="boxheading fw-bold fs-6 text-gray-800">

															<th align="left" valign="middle">Payment Type</th>
															<th align="left" valign="middle">MOP</th>

															<!-- <th width="7%" align="left" valign="middle"
																style="display: none">Base Rate</th>
															<th width="6%" align="left" valign="middle"
																style="display: none">Merchant MDR</th> -->
															<th align="left" valign="middle">
																Commission Type</th>

														<s:if
                                                        	test="%{#session.USER.UserType.name() =='ADMIN' || #session.USER.UserGroup.group =='SMA'}">
															<th align="left" valign="middle">SMA
																Commission %</th>
															<th align="left" valign="middle">SMA
																Commission FC</th>
														</s:if>
														<s:if
                                                            test="%{#session.USER.UserType.name() =='ADMIN' || #session.USER.UserGroup.group =='MA'}">
                                                            <th align="left" valign="middle">MA
																Commission %</th>
															<th align="left" valign="middle">MA
																Commission FC</th>
														</s:if>
														<s:if
                                                            test="%{#session.USER.UserType.name() =='ADMIN' || #session.USER.UserGroup.group =='Agent'}">
															<th align="left" valign="middle">
																Agent Commission %</th>
															<th align="left" valign="middle">
																Agent Commission FC</th>
														</s:if>
															<th align="left" valign="middle"
																style="display: none">id</th>
														<s:if
                                                        	test="%{#session.USER.UserType.name() =='ADMIN'}">
															<th align="left" valign="middle">Update</th>
															<th align="left" valign="middle"><span
																id="cancelLabel">Cancel</span></th>
														</s:if>
														</tr>
														<s:iterator value="value" status="itStatus">
															<tr class="boxtext">
																<td align="left" valign="middle"><s:property
																		value="transactiontype" /></td>
																<td align="left" valign="middle"><s:property
																        value="mop" /></td>

																<%-- <td align="left" valign="middle" style="display: none">
																	<s:property value="baserate" />
																</td>
																<td align="left" valign="middle" style="display: none">
																	<s:property value="merchant_mdr" />
																</td> --%>

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

														<s:if
                                                            test="%{#session.USER.UserType.name() =='ADMIN' || #session.USER.UserGroup.group =='SMA'}">
																<td align="left" valign="middle"><s:property
																		value="sma_commission_percent" /></td>
																<td align="left" valign="middle"><s:property
																		value="sma_commission_amount" /></td>
														</s:if>

														<s:if
                                                            test="%{#session.USER.UserType.name() =='ADMIN' || #session.USER.UserGroup.group =='MA'}">
																		<td align="left" valign="middle"><s:property
																		value="ma_commission_percent" /></td>
																<td align="left" valign="middle"><s:property
																		value="ma_commission_amount" /></td>
														</s:if>

														<s:if
                                                            test="%{#session.USER.UserType.name() =='ADMIN' || #session.USER.UserGroup.group =='Agent'}">
																		<td align="left" valign="middle"><s:property
																		value="agent_commission_percent" /></td>
																<td align="left" valign="middle"><s:property
																		value="agent_commission_amount" /></td>
														</s:if>

																<td align="center" valign="middle" style="display: none">
																	<s:property value="id" />
																</td>

																<td align="center" valign="middle">
																	<div>
																		<s:if
																			test="%{#session.USER.UserType.name() =='ADMIN'}">
																			<s:textfield id="edit%{#itStatus.count}"
																				name="editTDR" value="edit" type="button"
																				onclick="editCurrentRow('%{key +'Div'}','%{#itStatus.count}', this)"
																				class="btn btn-primary btn-xs edit1"
																				autocomplete="off">
																			</s:textfield>
																		</s:if>
																	</div>
																</td>

																<td align="center" valign="middle">
																	<s:if
                                                                        test="%{#session.USER.UserType.name() =='ADMIN'}">
                                                                        <s:textfield
                                                                            id="cancelBtn%{#itStatus.count}" value="cancel"
                                                                            type="button"
                                                                            onclick="cancel('%{#itStatus.count}',this)"
                                                                            class="btn btn-danger btn-xs cancelBtn"
                                                                            autocomplete="off">
                                                                        </s:textfield></td>
																	</s:if>
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