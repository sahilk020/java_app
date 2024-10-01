<%@page import="com.pay10.commons.util.SaltFactory"%>
<%@page import="com.pay10.commons.user.User"%>
<%@page import="com.pay10.commons.util.PropertiesManager"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Reseller Details</title>
<link href="../css/default.css" rel="stylesheet" type="text/css" />
<link href="../css/custom.css" rel="stylesheet" type="text/css" />
<link href="../assets/css/style.bundle.css" rel="stylesheet"
	type="text/css" />
<script src="../js/continents.js" type="text/javascript"></script>
<script src="../js/jquery.js"></script>
<script src="../js/core/popper.min.js"></script>
<script src="../js/core/bootstrap-material-design.min.js"></script>
<script src="../js/plugins/perfect-scrollbar.jquery.min.js"></script>
<!-- Control Center for Material Dashboard: parallax effects, scripts for the example pages etc -->
<script src="../js/material-dashboard.js?v=2.1.0" type="text/javascript"></script>
<script src="../js/follw.js"></script>
<script src="../js/commanValidate.js"></script>
<script src="../js/jquery.popupoverlay.js"></script>
<script src="../js/core/bootstrap-material-design.min.js"></script>
<script src="../js/plugins/perfect-scrollbar.jquery.min.js"></script>
<script src="../assets/plugins/global/plugins.bundle.js"></script>
<script src="../assets/js/scripts.bundle.js"></script>
<script type="text/javascript">
	function CollapseAll(theClass, id) {
		var alldivTags = new Array();
		alldivTags = document.getElementsByTagName("div");
		for (i = 0; i < alldivTags.length; i++) {
			if (alldivTags[i].className == theClass && alldivTags[i].id != id) {
				$('#' + alldivTags[i].id).slideUp('slow');
				document.getElementById('Head' + alldivTags[i].id).className = 'acordion-gray';
			}
		}
		if (document.getElementById('Head' + id).className
				.search('acordion-open') != -1) {
			document.getElementById('Head' + id).className = 'acordion-gray';
		} else {
			document.getElementById('Head' + id).className = 'acordion-open acordion-gray';
		}
	}

	function destlayer() { //v6.0
		var i, p, v, obj, args = destlayer.arguments;
		for (i = 0; i < (args.length - 2); i += 3)
			if ((obj = MM_findObj(args[i])) != null) {
				v = args[i + 2];
				if (obj.style) {
					obj = obj.style;
					v = (v == 'show') ? 'visible' : (v == 'hide') ? 'hidden'
							: v;
				}
				obj.visibility = v;
			}
	}
	$(document).ready(function() {
		//toggleEnable("ALL");
	});

	function toggleEnable(flag, toggleAction) {
		if (flag == 'ALL') {
			document.getElementById('processingmode').disabled = true;
			document.getElementById('comments').disabled = true;
			document.getElementById('whiteListIpAddress').disabled = true;

			document.getElementById('payId').disabled = true;
			document.getElementById('userId').disabled = true;
			document.getElementById('firstName').disabled = true;
			document.getElementById('lastName').disabled = true;
			document.getElementById('companyName').disabled = true;
			document.getElementById('website').disabled = true;
			document.getElementById('contactPerson').disabled = true;
			document.getElementById('emailId').disabled = true;
			document.getElementById('businessType').disabled = true;
			document.getElementById('merchantType').disabled = true;
			document.getElementById('noOfTransactions').disabled = true;
			document.getElementById('amountOfTransactions').disabled = true;
			document.getElementById('resellerId').disabled = true;
			document.getElementById('productDetail').disabled = true;
			document.getElementById('registrationDate').disabled = true;

			document.getElementById('mobile').disabled = true;
			document.getElementById('telephoneNo').disabled = true;
			document.getElementById('fax').disabled = true;
			document.getElementById('address').disabled = true;
			document.getElementById('city').disabled = true;
			document.getElementById('state').disabled = true;
			document.getElementById('country').disabled = true;
			document.getElementById('postalCode').disabled = true;

			document.getElementById('bankName').disabled = true;
			document.getElementById('ifscCode').disabled = true;
			document.getElementById('accHolderName').disabled = true;
			document.getElementById('currency').disabled = true;
			document.getElementById('branchName').disabled = true;
			document.getElementById('panCard').disabled = true;
			document.getElementById('accountNo').disabled = true;
			document.getElementById('defaultCurrency').disabled = true;
			document.getElementById('btnSave').style.visibility = "hidden";
			document.getElementById('btnCancel').style.visibility = "hidden";
		} else {
			if (flag == 'Action') {
				document.getElementById('processingmode').disabled = toggleAction;
				document.getElementById('comments').disabled = toggleAction;
				document.getElementById('whiteListIpAddress').disabled = toggleAction;
				if (toggleAction == false) {
					document.getElementById('btnEdit').style.visibility = "hidden";
					document.getElementById('btnSave').style.visibility = "visible";
					document.getElementById('btnCancel').style.visibility = "visible";
				} else {
					document.getElementById('btnEdit').style.visibility = "visible";
					document.getElementById('btnSave').style.visibility = "hidden";
					document.getElementById('btnCancel').style.visibility = "hidden";
				}

			} else if (flag == 'MerchantDetails') {
				document.getElementById('payId').disabled = false;
				document.getElementById('userId').disabled = false;
				document.getElementById('firstName').disabled = false;
				document.getElementById('lastName').disabled = false;
				document.getElementById('companyName').disabled = false;
				document.getElementById('website').disabled = false;
				document.getElementById('contactPerson').disabled = false;
				document.getElementById('emailId').disabled = false;
				document.getElementById('businessType').disabled = false;
				document.getElementById('merchantType').disabled = false;
				document.getElementById('noOfTransactions').disabled = false;
				document.getElementById('amountOfTransactions').disabled = false;
				document.getElementById('resellerId').disabled = false;
				document.getElementById('productDetail').disabled = false;
				document.getElementById('registrationDate').disabled = false;
			} else if (flag == 'ContactDetails') {
				document.getElementById('mobile').disabled = false;
				document.getElementById('telephoneNo').disabled = false;
				document.getElementById('fax').disabled = false;
				document.getElementById('address').disabled = false;
				document.getElementById('city').disabled = false;
				document.getElementById('state').disabled = false;
				document.getElementById('country').disabled = false;
				document.getElementById('postalCode').disabled = false;
			} else if (flag == 'BankDetails') {
				document.getElementById('bankName').disabled = false;
				document.getElementById('ifscCode').disabled = false;
				document.getElementById('accHolderName').disabled = false;
				document.getElementById('currency').disabled = false;
				document.getElementById('branchName').disabled = false;
				document.getElementById('panCard').disabled = false;
				document.getElementById('accountNo').disabled = false;
				document.getElementById('defaultCurrency').disabled = false;
			}
		}

	}

	function saveAction() {
		debugger
		var bankName = document.getElementById("bankName").value;
		var ifscCode=  document.getElementById('ifscCode').value;
		var accHolderName= document.getElementById('accHolderName').value;
		var branchName= document.getElementById('branchName').value;
		var accountNo= document.getElementById('accountNo').value;
		var settlementCycle= $('input[name="cycle"]:checked').val();
		var firstName = document.getElementById("firstName").value.trim();
        var lastName = document.getElementById("lastName").value.trim();
        var nameRegex = /^[a-z ,.'-]+$/i;
        document.getElementById("firstName").value = firstName;
        document.getElementById("lastName").value = lastName;
        if(!(firstName && nameRegex.test(firstName))){
            alert("Please Enter First Name");
            event.preventDefault();
            return false;
        }

        if(!(lastName && nameRegex.test(lastName))){
            alert("Please Enter Last Name");
            event.preventDefault();
            return false;
        }
	/* 	if(bankName == '' || bankName== null ){
			alert("Please fill settlement details");
			 event.preventDefault();
			return false;
		}
		if(ifscCode == '' || ifscCode== null ){
			alert("Please fill ifscCode");
			 event.preventDefault();
			return false;
		}
		if(accHolderName == '' || accHolderName== null ){
			alert("Please fill accHolderName ");
			 event.preventDefault();
			return false;
		}
		if(branchName == '' || branchName== null ){
			alert("Please fill branchName");
			 event.preventDefault();
			return false;
		}
		if(accountNo == '' || accountNo== null ){
			alert("Please fill accountNo");
			 event.preventDefault();
			return false;
		} */
		if(settlementCycle == '' || settlementCycle== null || settlementCycle=='undefined' ){
			alert("Please fill Settlement Cycle");
			 event.preventDefault();
			return false;
		}
		else{
			document.resellerSaveAction.submit();
			$('#loader-wrapper').show();
		}
	}


</script>
<script type="text/javascript">
	function showDivs(prefix, chooser) {
		for (var i = 0; i < chooser.options.length; i++) {
			var div = document
					.getElementById(prefix + chooser.options[i].value);
			div.style.display = 'none';
		}

		var selectedvalue = chooser.options[chooser.selectedIndex].value;

		if (selectedvalue == "PL") {
			displayDivs(prefix, "PL");
		} else if (selectedvalue == "PF") {
			displayDivs(prefix, "PF");
		} else if (selectedvalue == "PR") {
			displayDivs(prefix, "PR");
		} else if (selectedvalue == "CSA") {
			displayDivs(prefix, "CSA");
		} else if (selectedvalue == "LLL") {
			displayDivs(prefix, "LLL");
		} else if (selectedvalue == "RI") {
			displayDivs(prefix, "RI");
		} else if (selectedvalue == "AP") {
			displayDivs(prefix, "AP");
		} else if (selectedvalue == "T") {
			displayDivs(prefix, "T");
		}

	}

	function displayDivs(prefix, suffix) {
		var div = document.getElementById(prefix + suffix);
		div.style.display = 'block';
	}
</script>
<script>
	var _validFileExtensions = [ ".jpg", ".pdf", ".png" ];
	function Validate(oForm) {
		var arrInputs = oForm.getElementsByTagName("input");
		for (var i = 0; i < arrInputs.length; i++) {
			var oInput = arrInputs[i];
			if (oInput.type == "file") {
				var sFileName = oInput.value;
				if (sFileName.length > 0) {
					var blnValid = false;
					for (var j = 0; j < _validFileExtensions.length; j++) {
						var sCurExtension = _validFileExtensions[j];
						if (sFileName.substr(
								sFileName.length - sCurExtension.length,
								sCurExtension.length).toLowerCase() == sCurExtension
								.toLowerCase()) {
							blnValid = true;
							break;
						}
					}

					if (!blnValid) {
						alert("Sorry, " + sFileName
								+ " is invalid, allowed extensions are: "
								+ _validFileExtensions.join(", "));
						return false;
					}
				}
			}
		}

		return true;
	}
</script>

<!-- comment this code as mapped action class is not exist for the invoke struts API. -->
<%-- <script>
	$(document)
			.ready(
					function() {

						var token = document.getElementsByName("token")[0].value;
						$
								.ajax({
									url : 'checkFileExistAdmin',
									type : "POST",
									data : {
										payId : document
												.getElementById("payId").value,

										token : token,
										"struts.token.name" : "token",
									},

									success : function(data) {
										var fileList = new Array;
										filelist = data.fileName;
										if (filelist != null) {
											filelist = filelist.split(",");
											for (i = 0; i < filelist.length; i++) {
												document
														.getElementById(filelist[i]).style.visibility = "visible";
											}
										}
									},
								});
					});
</script> --%>

<style>
.errorMessage {
	color: #ff0000;
	text-align: right;
}
/* Focus & populated label styling */
 .fl_wrap.focused .fl_label,
.fl_label,
/* and graceful degradation */
.no-js  .fl_label {
  /* Can change */
  top: 0;
  font-size: 11px; font-weight:normal;
  color: #1ba6af; border:none;
}
 .fl_wrap.focused .fl_label {
  /* Can change */
  color: #1ba6af; border:none;
}
</style>

</head>
<body>
<div class="toolbar" id="kt_toolbar">
			<!--begin::Container-->
			<div id="kt_toolbar_container"
				class="container-fluid d-flex flex-stack">
				<!--begin::Page title-->
				<div data-kt-swapper="true" data-kt-swapper-mode="prepend"
					data-kt-swapper-parent="{default: '#kt_content_container', 'lg': '#kt_toolbar_container'}"
					class="page-title d-flex align-items-center me-3 flex-wrap mb-5 mb-lg-0 lh-1">
					<!--begin::Title-->
					<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">Reseller Details</h1>
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
						<li class="breadcrumb-item text-dark">Reseller Details</li>
						<!--end::Item-->
					</ul>
					<!--end::Breadcrumb-->
				</div>
				<!--end::Page title-->

			</div>
			<!--end::Container-->
		</div>
		<div class="post d-flex flex-column-fluid" id="kt_post">
					<div id="kt_content_container" class="container-xxl">
		<div class="row my-5">
						<div class="col">
							<div class="card">
								<div class="card-body">
	<table width="100%" border="0" align="center" cellpadding="0"
		cellspacing="0">
		<tr>
			<td align="left" valign="middle"><s:form
					action="resellerSaveAction" id="resellerSaveAction" method="post" autocomplete="off"
					class="FlowupLabels" theme="css_xhtml" validate="true">
					<s:hidden name="token" value="%{#session.customToken}"></s:hidden>
					<table width="100%" border="0" cellspacing="0" cellpadding="0">
						<tr>
							<td align="left" valign="top" bgcolor="#0271BB"><table
									width="100%" border="0" cellpadding="0" cellspacing="0">
									<tr>
										<td align="left" width="68%" valign="middle"
											style="font: bold 14px verdana; color: #fff;background-color: #202F4B; padding: 4px 0 4px 8px;">
										<h4 style="margin-top:15px;color: #fff!important;">Reseller Details</h4></td>
										<td align="left" width="68%" valign="middle"
											style="font: bold 14px verdana; color: #fff;background-color: #202F4B; padding: 4px 0 4px 8px;">
														<h4
															style="margin-right: 450px;margin-top: 15px;color: #fff!important;">
															<s:property
																value="user.businessName" />
														</h4>
													</td>
										<td width="5%" align="center" valign="middle"
											bgcolor="#202F4B">
											<s:submit id="btnSave" name="btnSave"
												class="btn btn-primary btn-md" style="background-color: blue" value="Save"
												onclick="saveAction();">
											</s:submit></td>
										<td width="5%" align="left" valign="middle" bgcolor="#202F4B">
											<button type="button" id="btnCancel" name="btnCancel"
												class="btn btn-danger btn-md"
												onClick="window.location.reload();">Cancel</button>
										</td>
										<td width="2%" align="left" valign="middle" bgcolor="#202F4B">&nbsp;</td>
									</tr>
								</table></td>
						</tr>
						<tr>
							<td align="left" valign="bottom" height="30"><div
									id="saveMessage">
									<s:actionmessage class="success success-text" />
								</div></td>
						</tr>
						<tr>
							<td align="center" valign="top"><table width="98%"
									border="0" cellspacing="0" cellpadding="0">
									<tr>
										<td align="center" valign="middle"><div
												id="page-content">
												<div class="acordion-gray" id="HeadActionDiv">
													<H4 class="pagehead"
														onClick="CollapseAll('hide-this','ActionDiv');$('#ActionDiv').slideToggle('slow');">
														Action</H4>
												</div>
												<div id="ActionDiv">
													<div class="indent">
														<table width="100%" border="0" cellpadding="7"
															cellspacing="0" class="formboxRR">
															<tr>
																<td align="left" valign="top" class="nfbxf"><div
																		class="addfildn">
																		<div class="rkb" style="width: 25%">
																			<div class="addfildn">
																				Status<br />
																				<s:select class="form-select form-select-solid" headerValue="ALL"
																					list="@com.pay10.commons.util.UserStatusType@values()"
																					id="status" name="userStatus"
																					value="%{user.userStatus}" />
																			</div>
																			<div class="clear"></div>
																		</div>
																		<div class="rkb" style="width: 70%">
																			<div class="">
																				<div class="fl_wrap" style="height: 125px;">
																					<label class='fl_label'>Comments</label>
																					<s:textarea id="comments" class="fl_input" rows="5"
																						name="comments" type="text"
																						value="%{user.comments}" autocomplete="off"
																						style="height:95px; resize: none; outline-width:0"
																						theme="simple"></s:textarea>
																				</div>
																			</div>
																			<div class="clear"></div>
																		</div>

																		<div class="clear"></div>
																	</div></td>
															</tr>
														</table>
													</div>
												</div>

												<div class="acordion-gray" id="HeadMerchantDetailsDiv">
													<H4 class="pagehead"
														onClick="CollapseAll('hide-this','MerchantDetailsDiv');$('#MerchantDetailsDiv').slideToggle('slow');">
														Reseller Details</H4>
												</div>
												<div id="MerchantDetailsDiv">
													<div class="indent">
														<table width="100%" border="0" cellspacing="0"
															cellpadding="7" class="formboxRR">
															<tr>
																<td align="left" valign="top">&nbsp;</td>
															</tr>
															<tr>
															<tr>
																<td align="left" valign="top"><div class="addfildn">
																		<div class="rkb">
																			<div class="addfildn" style="display:none;">
																				<div class="fl_wrap">
																					<label class='fl_label'>Pay ID</label>
																					<s:textfield id="payId" class="fl_input"
																						name="payId" type="text" value="%{user.payId}"
																						readonly="true"></s:textfield>
																				</div>
																			</div>
																			<div class="addfildn">
																				<div class="fl_wrap">
																					<label class='fl_label'>Reseller ID</label>
																					<s:textfield id="resellerId" class="fl_input"
																						name="resellerId" type="text"
																						value="%{user.resellerId}" readonly="true"></s:textfield>
																				</div>
																			</div>
																			<div class="addfildn">
																				<div class="fl_wrap">
																					<label class='fl_label'>First Name</label>
																					<div class="txtnew">
																						<s:textfield class="fl_input" id="firstName"
																							name="firstName" type="text"
																							value="%{user.firstName}" autocomplete="off"
																							onKeyPress="return ValidateAlpha(event);"></s:textfield>
																					</div>
																				</div>
																			</div>


																			<div class="addfildn">
																				<div class="fl_wrap">
																					<label class='fl_label'>Company Name</label>
																					<s:textfield class="fl_input" id="companyName"
																						name="companyName" type="text"
																						value="%{user.companyName}" autocomplete="off"></s:textfield>
																				</div>
																			</div>
																			<div class="addfildn">
																				<div class="fl_wrap">
                                                                                    <label class='fl_label'
                                                                                        style="padding: 0; font-size: 13px; font-weight: 600;">Registration
                                                                                        Date</label>
                                                                                    <s:textfield id="registrationDate"
                                                                                        style="margin-top:10px; font-weight:normal; font-size:14px;width: -webkit-fill-available;border: none;"
                                                                                        value="%{rDate}"
                                                                                        type="text"
                                                                                        readonly="true">
                                                                                    </s:textfield>
                                                                                    <s:hidden name="registrationDate" value="%{user.registrationDate}"></s:hidden>
																				</div>
																			</div>
																			<div class="addfildn">
																				<div class="fl_wrap">
																				<label class='fl_label'
                                                                                    style="padding: 0; font-size: 13px; font-weight: 600;">Activation
                                                                                        Date</label>
                                                                                    <s:textfield id="activationDate"
                                                                                        style="margin-top:10px; font-weight:normal; font-size:14px;width: -webkit-fill-available;border: none;"
                                                                                        type="text"
                                                                                        value="%{aDate}"
                                                                                        readonly="true">
                                                                                    </s:textfield>
                                                                                    <s:hidden name="activationDate" value="%{user.activationDate}"></s:hidden>
																				</div>
																			</div>
																			<div class="addfildn" style="display:none;">
																				<div class="fl_wrap">
																					<h6>Request URL</h6>
																					<%=new PropertiesManager().getSystemProperty("RequestURL")%>
																				</div>
																			</div>

																			<div class="clear"></div>
																		</div>

																		<div class="rkb">
																			<div class="addfildn">
																				<div class="fl_wrap">
																					<label class='fl_label'>Business Name</label>
																					<s:textfield class="fl_input" id="businessName"
																						name="businessName" type="text"
																						value="%{user.businessName}" readonly="true"
																						autocomplete="off"
																						onKeyPress="return ValidateAlpha(event);"></s:textfield>
																				</div>
																			</div>

																			<div class="addfildn">
																				<div class="fl_wrap">
																					<label class='fl_label'>Last Name</label>
																					<s:textfield class="fl_input" id="lastName"
																						name="lastName" type="text"
																						value="%{user.lastName}" autocomplete="off"
																						onKeyPress="return ValidateAlpha(event);"></s:textfield>
																				</div>
																			</div>

																			<div class="addfildn" style="display:none;">
																				<div class="fl_wrap">
																					<label class='fl_label'>Business Type</label>
																					<s:textfield class="fl_input" id="businessType"
																						name="businessType" type="text"
																						value="%{user.businessType}" autocomplete="off"
																						onKeyPress="return ValidateAlpha(event);"></s:textfield>
																				</div>
																			</div>

																			<div class="addfildn">
																				<div class="fl_wrap">
																					<label class='fl_label'>Email ID</label>
																					<s:textfield class="fl_input" id="emailId"
																						name="emailId" type="text" value="%{user.emailId}"
																						readonly="true"></s:textfield>
																				</div>
																			</div>

																			<div class="addfildn" style="display:none;">
																				<div class="fl_wrap">
																					<h6>Salt</h6>

																					<s:property value="salt" />
																				</div>
																			</div>

																			<div class="clear"></div>
																		</div>

																		<div class="clear"></div>
																	</div></td>
															</tr>

														</table>
													</div>
												</div>
												<div class="acordion-gray" id="HeadContactDetailsDiv" style="display:none;">
													<H4 class="pagehead"
														onClick="CollapseAll('hide-this','ContactDetailsDiv');$('#ContactDetailsDiv').slideToggle('slow');">
														Contact Details</H4>
												</div>
												<div id="ContactDetailsDiv" style="display:none;">
													<div class="indent">
														<table width="100%" border="0" cellspacing="0"
															cellpadding="7" class="formboxRR">
															<tr>
																<td>&nbsp;</td>
															</tr>
															<tr>
																<td align="left" valign="top"><div class="addfildn">
																		<div class="rkb">
																			<div class="addfildn">
																				<div class="fl_wrap">
																					<label class='fl_label'>Mobile</label>
																					<s:textfield class="fl_input" id="mobile"
																						name="mobile" type="text" value="%{user.mobile}"
																						autocomplete="off"
																						onkeypress="javascript:return isNumber (event)"></s:textfield>
																				</div>
																			</div>
																			<div class="addfildn">
																				<div class="fl_wrap">
																					<label class='fl_label'>Telephone No</label>
																					<s:textfield class="fl_input" id="telephoneNo"
																						name="telephoneNo" type="text"
																						value="%{user.telephoneNo}" autocomplete="off"
																						onkeypress="javascript:return isNumber (event)"></s:textfield>
																				</div>
																			</div>
																			<div class="addfildn">
																				<div class="fl_wrap">
																					<label class='fl_label'>City</label>
																					<s:textfield class="fl_input" id="city" name="city"
																						type="text" value="%{user.city}"
																						onKeyPress="return ValidateAlpha(event);"></s:textfield>
																				</div>
																			</div>
																			<div class="addfildn">
																				<div class="fl_wrap">
																					<label class='fl_label'>Country</label>
																					<s:textfield class="fl_input" id="country"
																						name="country" type="text" value="%{user.country}"
																						onKeyPress="return ValidateAlpha(event);"></s:textfield>
																				</div>
																			</div>

																			<div class="clear"></div>
																		</div>


																		<div class="rkb">
																			<div class="addfildn"
																				style="float: left; margin-bottom: 23px;">
																				Disable/Enable Transaction SMS:
																				<div class="txtnew"
																					style="float: left; margin: 0 10px 0 0">
																					<s:checkbox name="transactionSmsFlag"
																						value="%{user.transactionSmsFlag}" />
																					<br>

																				</div>
																			</div>
																			<div class="addfildn">
																				<div class="fl_wrap">
																					<label class='fl_label'>Address</label>
																					<s:textfield class="fl_input" id="address"
																						name="address" type="text" value="%{user.address}"></s:textfield>
																				</div>
																			</div>
																			<div class="addfildn">
																				<div class="fl_wrap">
																					<label class='fl_label'>State</label>
																					<s:textfield class="fl_input" id="state"
																						name="state" type="text" value="%{user.state}"
																						onKeyPress="return ValidateAlpha(event);"></s:textfield>
																				</div>
																			</div>
																			<div class="addfildn">
																				<div class="fl_wrap">
																					<label class='fl_label'>Postal Code</label>
																					<s:textfield class="fl_input" id="postalCode"
																						name="postalCode" type="text"
																						value="%{user.postalCode}" autocomplete="off"
																						onkeypress="return ValidateMerchantAccountSetup(event);"></s:textfield>
																				</div>
																			</div>


																			<div class="clear"></div>
																		</div>



																		<div class="clear"></div>
																	</div></td>
															</tr>

														</table>
													</div>
												</div>

												<div class="acordion-gray" id="HeadBankDetailsDiv" style="display:hide;" >
													<H4 class="pagehead "
														onClick="CollapseAll('hide-this','BankDetailsDiv');$('#BankDetailsDiv').slideToggle('slow');">
														Settlement Details</H4>
												</div>
												<div id="BankDetailsDiv">
													<div class="indent">

														<table width="100%" border="0" cellspacing="0"
															cellpadding="7" class="formboxRR">
															<tr>
																<td>&nbsp;</td>
															</tr>
															<tr>
																<td align="left" valign="top"><div class="addfildn">
																		<div class="rkb">
																			<div class="addfildn">
																				<div class="fl_wrap">
																					<label class='fl_label'>Bank Name</label>
																					<s:textfield class="fl_input" type="text"
																						id="bankName" name="bankName"
																						value="%{user.bankName}"
																						onKeyPress="return ValidateAlpha(event);" 	onkeyup="bankNameValidation()"></s:textfield>
																				</div>
																				<span id="bankNameId"
																					style="color: red; display: none;">Please
																					Enter valid Bank Name</span> 
																			</div>

																			<div class="addfildn">
																				<div class="fl_wrap">
																					<label class='fl_label'>AC. Holder Name</label>
																					<s:textfield class="fl_input" ttype="text"
																						id="accHolderName" name="accHolderName"
																						value="%{user.accHolderName}"
																						OnKeypress="javascript:return isAlphaNumeric(event,this.value);" onkeyup="acHolderNameValidation()"></s:textfield>
																				</div>
																					<span id="accHolderNameId"
																					style="color: red; display: none;">Please
																					Enter valid AC. Holder Name</span>
																			</div>

																			<div class="addfildn">
																				<div class="fl_wrap">
																					<label class='fl_label'>Branch Name</label>
																					<s:textfield class="fl_input" ttype="text"
																						id="branchName" name="branchName"
																						value="%{user.branchName}" autocomplete="off"
																						OnKeypress="javascript:return isAlphaNumeric(event,this.value);"></s:textfield>
																				</div>
																			</div>

																			<div class="addfildn">
																				<div class="fl_wrap">
																					<label class='fl_label'>Account No</label>
																					<s:textfield class="fl_input" type="text"
																						id="accountNo" name="accountNo"
																						value="%{user.accountNo}" autocomplete="off"
																						onkeypress="javascript:return isNumber (event)" maxlength="20" ></s:textfield>
																				</div>
																				<span id="accountNoId"
																					style="color: red; display: none;">Please
																					Enter valid Account No.</span>
																			</div>
																			<div class="addfildn">
																				<div class="fl_wrap">
																					<label class='fl_label'
																						style="padding: 0; font-size: 15px; font-weight: 600;">Settlement Cycle </label>
																				<div class="form-check form-check-inline"style="padding-top: 12px;padding-top: 100;">
																					<s:radio name="cycle" class=""
																					style="margin-right: 12px;"
 																					id="cycle"   value="%{user.cycle}"
 																					list="listCycle" listKey="id" listValue="cycle" 
																					autocomplete="off" /> </div>
																				</div>
																			</div>




																			<div class="clear"></div>
																		</div>

																		<div class="rkb">
																			<div class="addfildn">
																				<div class="fl_wrap">
																					<label class='fl_label'>IFSC Code</label>
																					<s:textfield class="fl_input" type="text"
																						id="ifscCode" name="ifscCode"
																						value="%{user.ifscCode}" autocomplete="off"
																						onkeypress="return ValidateMerchantAccountSetup(event);" onkeyup="ifscCodeValidation()"></s:textfield>
																				</div>
																				<span id="ifscCodeId"
																					style="color: red; display: none;">Please
																					Enter valid IFSC Code.</span>
																			</div>
																			<div class="addfildn" style="display:none;">
																				<div class="fl_wrap">
																					<label class='fl_label'>Currency</label>
																					<s:textfield class="fl_input" type="text"
																						id="currency" name="currency"
																						value="%{user.currency}" autocomplete="off"
																						onkeypress="return ValidateMerchantAccountSetup(event);"></s:textfield>
																				</div>
																			</div>
																			<div class="addfildn" style="display:none;">
																				<div class="fl_wrap ">
																					<label class='fl_label'>Pan Card</label>
																					<s:textfield class="fl_input" type="text"
																						id="panCard" name="panCard"
																						value="%{user.panCard}" autocomplete="off"
																						onkeypress="return ValidateMerchantAccountSetup(event);"></s:textfield>
																				</div>
																			</div>
																			<!--  modified by shashi for Default Currency -->
																			<div class="addfildn" style="display:none;">

																				<s:select name="defaultCurrency"
																					id="defaultCurrency" list="currencyMap"
																					style="width:100px; display:inline;"
																					class="form-control" />


																			</div>

																			<div class="clear"></div>
																		</div>

																		<div class="clear"></div>
																	</div></td>
															</tr>

														</table>
													</div>
												</div>



												<div class="acordion-gray" id="HeadBusinessDetailsDiv" style="display:none;">
													<H4 class="pagehead"
														onClick="CollapseAll('hide-this','BusinessDetailsDiv');$('#BusinessDetailsDiv').slideToggle('slow');">
														Business Details</H4>
												</div>

												<div id="BusinessDetailsDiv" style="display:none;">
													<div class="indent">
														<table width="99%" border="0" align="center"
															cellpadding="0" cellspacing="0" class="formboxRR">
															<tr>
																<td height="30" align="left" valign="middle">&nbsp;</td>
															</tr>
															<tr>
																<td align="left" valign="middle"><div
																		class="addfildn">
																		<div class="rkb">
																			<div class="addfildn">
																				<div class="fl_wrap">
																					<label class='fl_label'>Organisation Type</label>
																					<s:textfield class="fl_input" type="text"
																						id="organisationType" name="organisationType"
																						value="%{user.organisationType}"
																						autocomplete="off"
																						OnKeypress="javascript:return isAlphaNumeric(event,this.value);"></s:textfield>
																				</div>
																			</div>
																			<div class="addfildn">
																				<div class="fl_wrap">
																					<label class='fl_label'>Multicurrency
																						Payments Required?</label>
																					<s:textfield class="fl_input" type="text"
																						id="multiCurrency" name="multiCurrency"
																						value="%{user.multiCurrency}" autocomplete="off"
																						onKeyPress="return ValidateAlpha(event);"></s:textfield>
																				</div>
																			</div>
																			<div class="addfildn">
																				<div class="fl_wrap">
																					<label class='fl_label'>Operation Address</label>
																					<s:textfield class="fl_input" ttype="text"
																						id="operationAddress" name="operationAddress"
																						value="%{user.operationAddress}"
																						autocomplete="off"
																						OnKeypress="javascript:return isAlphaNumeric(event,this.value);"></s:textfield>
																				</div>
																			</div>
																			<div class="addfildn">
																				<div class="fl_wrap">
																					<label class='fl_label'>Operation Address
																						City</label>
																					<s:textfield class="fl_input" type="text"
																						id="operationCity" name="operationCity"
																						value="%{user.operationCity}" autocomplete="off"
																						onKeyPress="return ValidateAlpha(event);"></s:textfield>
																				</div>
																			</div>
																			<div class="addfildn">
																				<div class="fl_wrap">
																					<label class='fl_label'>Date of
																						Establishment</label>
																					<s:textfield class="fl_input" type="text"
																						id="dateOfEstablishment"
																						name="dateOfEstablishment"
																						value="%{user.dateOfEstablishment}"
																						autocomplete="off"
																						onkeydown="return DateFormat(this, event.keyCode)"
																						maxlength="10"></s:textfield>
																				</div>
																			</div>
																			<div class="addfildn">
																				<div class="fl_wrap">
																					<label class='fl_label'>PAN</label>
																					<s:textfield class="fl_input" type="text" id="pan"
																						name="pan" value="%{user.pan}" autocomplete="off"
																						OnKeypress="javascript:return isAlphaNumeric(event,this.value);"></s:textfield>
																				</div>
																			</div>
																			<div class="addfildn">
																				<div class="fl_wrap">
																					<label class='fl_label'>Expected number of
																						transaction</label>
																					<s:textfield class="fl_input" type="text"
																						id="noOfTransactions" name="noOfTransactions"
																						value="%{user.noOfTransactions}"
																						autocomplete="off"
																						onkeypress="javascript:return isNumber (event)"></s:textfield>
																				</div>
																			</div>
																			<div class="addfildn">
																				<div class="fl_wrap">
																					<label class='fl_label'>Expected amount of
																						transaction</label>
																					<s:textfield class="fl_input" type="text"
																						id="amountOfTransactions"
																						name="amountOfTransactions"
																						value="%{user.amountOfTransactions}"
																						onkeypress="javascript:return isNumber1 (event)"
																						autocomplete="off"></s:textfield>
																				</div>
																			</div>
																			<div class="addfildn">

																				<div style="float: left; margin: 6px 10px 0 0"></div>
																				<div class="clear"></div>
																			</div>

																			<div class="clear"></div>
																		</div>
																		<div class="rkb">
																			<div class="addfildn">
																				<div class="fl_wrap">
																					<label class='fl_label'>Website URL</label>
																					<s:textfield class="fl_input" type="text"
																						id="website" name="website"
																						value="%{user.website}"></s:textfield>
																				</div>
																			</div>
																			<div class="addfildn">
																				<div class="fl_wrap">
																					<label class='fl_label'>Business Model</label>
																					<s:textfield class="fl_input" type="text"
																						id="businessModel" name="businessModel"
																						value="%{user.businessModel}" autocomplete="off"
																						OnKeypress="javascript:return isAlphaNumeric(event,this.value);"></s:textfield>
																				</div>
																			</div>
																			<div class="addfildn">
																				<div class="fl_wrap">
																					<label class='fl_label'>Operation Address
																						State</label>
																					<s:textfield class="fl_input" type="text"
																						id="operationState" name="operationState"
																						value="%{user.operationState}" autocomplete="off"
																						onKeyPress="return ValidateAlpha(event);"></s:textfield>
																				</div>
																			</div>
																			<div class="addfildn">
																				<div class="fl_wrap">
																					<label class='fl_label'>Operation Address
																						Pincode</label>
																					<s:textfield class="fl_input" type="text"
																						id="operationPostalCode"
																						name="operationPostalCode"
																						value="%{user.operationPostalCode}"></s:textfield>
																				</div>
																			</div>
																			<div class="addfildn">
																				<div class="fl_wrap">
																					<label class='fl_label'>CIN</label>
																					<s:textfield class="fl_input" type="text" id="cin"
																						name="cin" value="%{user.cin}" autocomplete="off"
																						OnKeypress="javascript:return isAlphaNumeric(event,this.value);"></s:textfield>
																				</div>
																			</div>
																			<div class="addfildn">
																				<div class="fl_wrap">
																					<label class='fl_label'>Name on PAN Card</label>
																					<s:textfield class="fl_input" type="text"
																						id="panName" name="panName"
																						value="%{user.panName}" autocomplete="off"
																						OnKeypress="javascript:return isAlphaNumeric(event,this.value);"></s:textfield>
																				</div>
																			</div>
																			<div class="addfildn">
																				<div class="fl_wrap">
																					<label class='fl_label'>Merchant
																						Transaction Email</label>
																					<s:textfield class="fl_input" type="text"
																						id="transactionEmailId" name="transactionEmailId"
																						value="%{user.transactionEmailId}"></s:textfield>
																				</div>
																			</div>
																			<div class="clear"></div>
																		</div>
																		<div class="clear"></div>
																		<div class="tranjuctionCon"  style = "display:none">
																			<h2>Emailer Flag</h2>
																			<div class="">
																				<div class="tranjuctionCon4">
																					Transaction Emailer
																					<div style="float: left; margin: 0 7px 0 0">
																						<s:checkbox name="transactionEmailerFlag"
																							value="%{user.transactionEmailerFlag}" />
																					</div>
																				</div>
																			</div>
																		</div>
																		<div class="tranjuctionCon" style = "display:none">
																			<h2>Transactional Flag</h2>
																			<div class="tranjuctionCon3">
																				<div class="tranjuctionCon5">
																					Express Pay
																					<div style="float: left; margin: 0 7px 0 0">
																						<s:checkbox name="expressPayFlag"
																							value="%{user.expressPayFlag}" />
																					</div>
																				</div>
																				<div class="tranjuctionCon5">
																					Merchant Hosted Page
																					<div style="float: left; margin: 0 7px 0 0">
																						<s:checkbox name="merchantHostedFlag"
																							value="%{user.merchantHostedFlag}" />
																					</div>
																				</div>
																				<div class="tranjuctionCon5">
																					Iframe Payment
																					<div style="float: left; margin: 0 7px 0 0">
																						<s:checkbox name="iframePaymentFlag"
																							value="%{user.iframePaymentFlag}" />
																					</div>
																				</div>
																				<div class="tranjuctionCon5">
																					Authentication Emailer
																					<div style="float: left; margin: 0 7px 0 0">
																						<s:checkbox
																							name="transactionAuthenticationEmailFlag"
																							value="%{user.transactionAuthenticationEmailFlag}" />
																					</div>
																				</div>
																				<div class="tranjuctionCon5">
																					Customer Transaction Emailer
																					<div style="float: left; margin: 0 7px 0 0">
																						<s:checkbox name="transactionCustomerEmailFlag"
																							value="%{user.transactionCustomerEmailFlag}" />
																					</div>
																				</div>
																				<div class="tranjuctionCon5">
																					Retry Transaction
																					<div style="float: left; margin: 0 7px 0 0">
																						<s:checkbox name="retryTransactionCustomeFlag"
																							value="%{user.retryTransactionCustomeFlag}" />
																					</div>
																				</div>
																				<div class="clear"></div>
																			</div>

																			<div class="tranjuctionCon4">
																				Number of Retry<br>
																				<s:select name="attemptTrasacation"
																					id="attemptTrasacation" headerKey="1"
																					list="#{'1':'1','2':'2','3':'3','4':'4','5':'5'}"
																					style="width:100%;" class="textFL_merch5"
																					value="%{user.attemptTrasacation}">
																				</s:select>
																			</div>
																			<div class="clear"></div>
																		</div>
																		<div class="clear"></div>
																	</div></td>
															</tr>
														</table>
													</div>
												</div>
											<!--Security Section-->

												<div class="acordion-gray" id="HeadSecurityDiv">
													<H4 class="pagehead"
													onClick="CollapseAll('hide-this','SecurityDiv');$('#SecurityDiv').slideToggle('slow');">
													Security</H4>
												</div>

												<div id="SecurityDiv">
													<div class="indent">
														<table width="99%" border="0" align="center"
														cellpadding="0" cellspacing="0" class="formboxRR">
															<tr>
																<td height="30" align="left" valign="middle">
																&nbsp;</td>
															</tr>
															<tr>
																<td align="left" valign="middle">
																	<div class="tranjuctionCon">
																		<h4 class="trans-btm">2FA Security</h4>
																		<div class="clear"></div>
																			<div class="tranjuctionCon5">
																				<div class="addfildn">
																					<div class="fl_wrap" style="border: none !important">
																						<label class='fl_label' style="padding: 0; font-weight: 600;">Enable 2FA</label>
																						<s:checkbox style="margin-top: 5px;" name="tfaFlag"
																						value="%{user.tfaFlag}"
																						id="tfa"/>
																					</div>
																				</div>
																			</div>
																	</div>
																</td>
															</tr>
														</table>
													</div>
												</div>

												<!--Security Section-->



											</div></td>
									</tr>
								</table></td>
						</tr>
					</table>
				</s:form> <br />
			<table width="98%" align="center" border="0" cellspacing="0">
					<tr>
						<td align="center" valign="top"><div id="page-content"
								class="documentstop" style="padding-top:10px;">

								<div class="acordion-gray" id="HeadDocumentDetailsDiv1" style="display:none;">
									<H4 class="pagehead"
										onClick="CollapseAll('hide-this','DocumentDetailsDiv1');$('#DocumentDetailsDiv1').slideToggle('slow');">
										Document Details</H4>
								</div>
								<div id="DocumentDetailsDiv1">
									<div class="indent">
										<s:form action="uploadAdmin" enctype="multipart/form-data"
											onsubmit="return Validate(this);">
											<table width="100%" border="0" cellspacing="0"
												cellpadding="7" class="formbox">
												<tr>
													<td><s:hidden name="payId" id="payId"
															value="%{user.payId}" /></td>
												</tr>
												<tr>
													<td align="left" valign="top"><br> <br>
														<table width="98%" align="center" border="0"
															cellspacing="0" cellpadding="0">
															<tr>
																<td align="right" colspan="2" height="30"
																	class="redsmalltext">Upload only .pdf nd .jpg
																	formats</td>
															</tr>
															<tr>

																<td width="70%" height="30" align="left"
																	bgcolor="#f6f8f8" style="padding: 7px;"><s:select
																		name="ddlBusinessType" onChange="showDivs('div',this)"
																		class="form-control"
																		list="#{'PL':'Private Ltd/Public Ltd Companies','PF':'Partnership Firms','PR':'Proprietorship Firms','CSA':'Clubs / Societies / Associations','LLL':'LLP-Limited Liability Partnership','RI':'Resident Individuals','AP':'Address Proofs','T':'Trust'}"></s:select></td>

															</tr>
															<tr>
																<td align="left" class="text1">&nbsp;</td>
															</tr>
															<tr>
																<td align="left" class="text1">
																	<div id="divPL" style="display: block;">
																		<table width="100%" border="0" cellspacing="0"
																			cellpadding="0" class="product-specbig">
																			<tr>
																				<td colspan="3" align="left" valign="middle"
																					class="blbxf"><strong>Certified
																						Copies Of:</strong></td>
																			</tr>
																			<tr>
																				<td width="70%" align="left" valign="middle"><strong>Articles
																						Of Association (AOA) </strong></td>
																				<td width="30%" align="left" valign="middle">
																					<div class="row text-left">
																						<div class="col-md-10 text-right">
																							<div class="input-group">
																								<span class="input-group-btn"> <input
																									type="text" class="inputfieldsmall" readonly>
																									<span
																									class="file-input btn btn-success btn-file btn-small">
																										<span class="glyphicon glyphicon-folder-open"></span>
																										&nbsp;&nbsp;Choose file <s:file name="PL_AOA"
																											type="file" />
																								</span>
																								</span>
																							</div>
																						</div>
																						<div class="col-md-1 text-left">
																							<div id="ArticleOfAssociation"
																								style="visibility: hidden">
																								<s:url var="fileDownload" namespace="/"
																									action="jsp/downloadDocument" escapeAmp="false">
																									<s:param name="payId">
																										<s:property value="%{user.payId}" />
																									</s:param>
																									<s:param name="fileName">ArticleOfAssociation</s:param>
																								</s:url>
																								<s:a href="%{fileDownload}"
																									class="btn btn-sm btn-primary">
																									<i class="glyphicon glyphicon-download-alt"></i>
																								</s:a>
																							</div>
																						</div>

																					</div>
																				</td>

																			</tr>
																			<tr>
																				<td align="left" valign="middle"><strong>Memorandum
																						Of Association (MOA)</strong></td>
																				<td align="right" valign="middle"><div
																						class="row">
																						<div class="col-md-10 text-right">
																							<div class="input-group">
																								<span class="input-group-btn"> <input
																									type="text" class="inputfieldsmall" readonly>
																									<span
																									class="file-input btn btn-success btn-file btn-small">
																										<span class="glyphicon glyphicon-folder-open"></span>
																										&nbsp;&nbsp;Choose file <s:file name="PL_MOA"
																											type="file" />
																								</span>
																								</span>
																							</div>
																						</div>
																						<div class="col-md-1 text-right">
																							<div id="MemorandumOfAssociation"
																								style="visibility: hidden">
																								<s:url var="fileDownload" namespace="/"
																									action="jsp/downloadDocument" escapeAmp="false">
																									<s:param name="payId">
																										<s:property value="%{user.payId}" />
																									</s:param>
																									<s:param name="fileName">MemorandumOfAssociation</s:param>
																								</s:url>
																								<s:a href="%{fileDownload}"
																									class="btn btn-sm btn-primary">
																									<i class="glyphicon glyphicon-download-alt"></i>
																								</s:a>
																							</div>
																						</div>

																					</div></td>
																			</tr>
																			<tr>
																				<td align="left" valign="middle"><strong>Certification
																						Of Incorporation </strong></td>
																				<td align="right" valign="middle"><div
																						class="row">
																						<div class="col-md-10 text-right">
																							<div class="input-group">
																								<span class="input-group-btn"> <input
																									type="text" class="inputfieldsmall" readonly>
																									<span
																									class="file-input btn btn-success btn-file btn-small">
																										<span class="glyphicon glyphicon-folder-open"></span>
																										&nbsp;&nbsp;Choose file <s:file name="PL_COI"
																											type="file" />
																								</span></span>
																							</div>
																						</div>
																						<div class="col-md-1 text-right">
																							<div id="CertificationOfIncorporation"
																								style="visibility: hidden">
																								<s:url var="fileDownload" namespace="/"
																									action="jsp/downloadDocument" escapeAmp="false">
																									<s:param name="payId">
																										<s:property value="%{user.payId}" />
																									</s:param>
																									<s:param name="fileName">CertificationOfIncorporation</s:param>
																								</s:url>
																								<s:a href="%{fileDownload}"
																									class="btn btn-sm btn-primary">
																									<i class="glyphicon glyphicon-download-alt"></i>
																								</s:a>
																							</div>
																						</div>
																					</div></td>
																			</tr>
																			<tr>
																				<td align="left" valign="middle"><strong>Certification
																						Of Commencement of Business (in case of Public Ltd
																						Cos) </strong></td>
																				<td align="right" valign="middle"
																					class="profilepage"><div class="row">
																						<div class="col-md-10 text-right">
																							<div class="input-group">
																								<span class="input-group-btn"> <input
																									type="text" class="inputfieldsmall" readonly>
																									<span
																									class="file-input btn btn-success btn-file btn-small">
																										<span class="glyphicon glyphicon-folder-open"></span>
																										&nbsp;&nbsp;Choose file <s:file
																											name="PL_COCOB" type="file" />
																								</span></span>
																							</div>
																						</div>
																						<div class="col-md-1 text-right">
																							<div id="CertificationOfCommencement"
																								style="visibility: hidden">
																								<s:url var="fileDownload" namespace="/"
																									action="jsp/downloadDocument" escapeAmp="false">
																									<s:param name="payId">
																										<s:property value="%{user.payId}" />
																									</s:param>
																									<s:param name="fileName">CertificationOfCommencement</s:param>
																								</s:url>
																								<s:a href="%{fileDownload}"
																									class="btn btn-sm btn-primary">
																									<i class="glyphicon glyphicon-download-alt"></i>
																								</s:a>
																							</div>
																						</div>
																					</div></td>
																			</tr>
																			<tr>
																				<td align="left" valign="middle"><strong>PAN
																						of the Company.(In case PAN not furnished, a
																						separate declaration for its non allotment to be
																						obtained.) </strong></td>
																				<td align="right" valign="middle"
																					class="profilepage"><div class="row">
																						<div class="col-md-10 text-right">
																							<div class="input-group">
																								<span class="input-group-btn"> <input
																									type="text" class="inputfieldsmall" readonly>
																									<span
																									class="file-input btn btn-success btn-file btn-small">
																										<span class="glyphicon glyphicon-folder-open"></span>
																										&nbsp;&nbsp;Choose file <s:file name="PL_POC"
																											type="file" />
																								</span></span>
																							</div>
																						</div>
																						<div class="col-md-1 text-right">
																							<div id="PANoftheCompany"
																								style="visibility: hidden">
																								<s:url var="fileDownload" namespace="/"
																									action="jsp/downloadDocument" escapeAmp="false">
																									<s:param name="payId">
																										<s:property value="%{user.payId}" />
																									</s:param>
																									<s:param name="fileName">PANoftheCompany</s:param>
																								</s:url>
																								<s:a href="%{fileDownload}"
																									class="btn btn-sm btn-primary">
																									<i class="glyphicon glyphicon-download-alt"></i>
																								</s:a>
																							</div>
																						</div>
																					</div></td>
																			</tr>
																			<tr>
																				<td align="left" valign="middle"><strong>Identification
																						Documents of Authorized Signatories (as listed
																						separately under &ldquo;Resident
																						Individuals&rdquo;) </strong></td>
																				<td align="right" valign="middle"
																					class="profilepage">
																					<div class="row">
																						<div class="col-md-10 text-right">
																							<div class="input-group">
																								<span class="input-group-btn"> <input
																									type="text" class="inputfieldsmall" readonly>
																									<span
																									class="file-input btn btn-success btn-file btn-small">
																										<span class="glyphicon glyphicon-folder-open"></span>
																										&nbsp;&nbsp;Choose file <s:file
																											name="PL_IDOAS" type="file" />
																								</span></span>
																							</div>
																						</div>
																						<div class="col-md-1 text-right">
																							<div id="IdentificationDocuments"
																								style="visibility: hidden">
																								<s:url var="fileDownload" namespace="/"
																									action="jsp/downloadDocument" escapeAmp="false">
																									<s:param name="payId">
																										<s:property value="%{user.payId}" />
																									</s:param>
																									<s:param name="fileName">IdentificationDocuments</s:param>
																								</s:url>
																								<s:a href="%{fileDownload}"
																									class="btn btn-sm btn-primary">
																									<i class="glyphicon glyphicon-download-alt"></i>
																								</s:a>
																							</div>
																						</div>
																					</div>

																				</td>
																			</tr>
																			<tr>
																				<td align="left" valign="middle"><strong>Board
																						resolution</strong></td>
																				<td align="right" valign="middle">
																					<div class="row">
																						<div class="col-md-10 text-right">
																							<div class="input-group">
																								<span class="input-group-btn"> <input
																									type="text" class="inputfieldsmall" readonly>
																									<span
																									class="file-input btn btn-success btn-file btn-small">
																										<span class="glyphicon glyphicon-folder-open"></span>
																										&nbsp;&nbsp;Choose file <s:file name="PL_BR"
																											type="file" />
																								</span></span>
																							</div>
																						</div>
																						<div class="col-md-1 text-right">
																							<div id="Boardresolution"
																								style="visibility: hidden">
																								<s:url var="fileDownload" namespace="/"
																									action="jsp/downloadDocument" escapeAmp="false">
																									<s:param name="payId">
																										<s:property value="%{user.payId}" />
																									</s:param>
																									<s:param name="fileName">Boardresolution</s:param>
																								</s:url>
																								<s:a href="%{fileDownload}"
																									class="btn btn-sm btn-primary">
																									<i class="glyphicon glyphicon-download-alt"></i>
																								</s:a>
																							</div>
																						</div>
																					</div>

																				</td>
																			</tr>
																			<tr>
																				<td align="left" valign="middle"><strong>List
																						&amp; Personal Details of Directors </strong></td>
																				<td align="right" valign="middle"
																					class="profilepage">
																					<div class="row">
																						<div class="col-md-10 text-right">
																							<div class="input-group">
																								<span class="input-group-btn"> <input
																									type="text" class="inputfieldsmall" readonly>
																									<span
																									class="file-input btn btn-success btn-file btn-small">
																										<span class="glyphicon glyphicon-folder-open"></span>
																										&nbsp;&nbsp;Choose file <s:file
																											name="PL_LPDOD" type="file" />
																								</span></span>
																							</div>
																						</div>
																						<div class="col-md-1 text-right">
																							<div id="ListPersonalDetails"
																								style="visibility: hidden">
																								<s:url var="fileDownload" namespace="/"
																									action="jsp/downloadDocument" escapeAmp="false">
																									<s:param name="payId">
																										<s:property value="%{user.payId}" />
																									</s:param>
																									<s:param name="fileName">ListPersonalDetails</s:param>
																								</s:url>
																								<s:a href="%{fileDownload}"
																									class="btn btn-sm btn-primary">
																									<i class="glyphicon glyphicon-download-alt"></i>
																								</s:a>
																							</div>
																						</div>
																					</div>

																				</td>
																			</tr>
																			<tr>
																				<td align="left" valign="middle"><strong>Address
																						Proofs ( As listed under &ldquo;Address
																						Proof&rdquo;) </strong></td>
																				<td align="right" valign="middle"
																					class="profilepage">
																					<div class="row">
																						<div class="col-md-10 text-right">
																							<div class="input-group">
																								<span class="input-group-btn"> <input
																									type="text" class="inputfieldsmall" readonly>
																									<span
																									class="file-input btn btn-success btn-file btn-small">
																										<span class="glyphicon glyphicon-folder-open"></span>
																										&nbsp;&nbsp;Choose file <s:file name="PL_AP"
																											type="file" />
																								</span></span>
																							</div>
																						</div>
																						<div class="col-md-1 text-right">
																							<div id="AddressProofs"
																								style="visibility: hidden">
																								<s:url var="fileDownload" namespace="/"
																									action="jsp/downloadDocument" escapeAmp="false">
																									<s:param name="payId">
																										<s:property value="%{user.payId}" />
																									</s:param>
																									<s:param name="fileName">AddressProofs</s:param>
																								</s:url>
																								<s:a href="%{fileDownload}"
																									class="btn btn-sm btn-primary">
																									<i class="glyphicon glyphicon-download-alt"></i>
																								</s:a>
																							</div>
																						</div>
																					</div>

																				</td>
																			</tr>
																			<tr>
																				<td align="left" valign="middle"><strong>Bank
																						statement of last 6 months/Income Tax return</strong></td>
																				<td align="right" valign="middle"
																					class="profilepage">
																					<div class="row">
																						<div class="col-md-10 text-right">
																							<div class="input-group">
																								<span class="input-group-btn"> <input
																									type="text" class="inputfieldsmall" readonly>
																									<span
																									class="file-input btn btn-success btn-file btn-small">
																										<span class="glyphicon glyphicon-folder-open"></span>
																										&nbsp;&nbsp;Choose file <s:file name="PL_BS"
																											type="file" />
																								</span></span>
																							</div>
																						</div>
																						<div class="col-md-1 text-right">
																							<div id="BankStatement"
																								style="visibility: hidden">
																								<s:url var="fileDownload" namespace="/"
																									action="jsp/downloadDocument" escapeAmp="false">
																									<s:param name="payId">
																										<s:property value="%{user.payId}" />
																									</s:param>
																									<s:param name="fileName">BankStatement</s:param>
																								</s:url>
																								<s:a href="%{fileDownload}"
																									class="btn btn-sm btn-primary">
																									<i class="glyphicon glyphicon-download-alt"></i>
																								</s:a>
																							</div>
																						</div>
																					</div>

																				</td>
																			</tr>
																		</table>
																	</div>
																	<div id="divPF" style="display: none;">
																		<table width="100%" border="0" cellspacing="0"
																			cellpadding="0" class="product-specbig">
																			<tr>
																				<td colspan="3" align="left" valign="middle"
																					class="blbxf"><strong>Copies Of:</strong></td>
																			</tr>
																			<tr>
																				<td width="70%" align="left" valign="middle"><strong>Identification
																						Documents of all partners. (as listed separately
																						under "Resident Individuals") </strong></td>
																				<td width="30%" align="left" valign="middle">
																					<div class="row text-left">
																						<div class="col-md-10 text-right">
																							<div class="input-group">
																								<span class="input-group-btn"> <input
																									type="text" class="inputfieldsmall" readonly />
																									<span
																									class="file-input btn btn-success btn-file btn-small">
																										<span class="glyphicon glyphicon-folder-open"></span>
																										&nbsp;&nbsp;Choose file <s:file name="PF_ID"
																											type="file" />
																								</span></span>
																							</div>
																						</div>
																						<div class="col-md-1 text-left">
																							<div id="IdentificationDocumentsAllPartner"
																								style="visibility: hidden">
																								<s:url var="fileDownload" namespace="/"
																									action="jsp/downloadDocument" escapeAmp="false">
																									<s:param name="payId">
																										<s:property value="%{user.payId}" />
																									</s:param>
																									<s:param name="fileName">IdentificationDocumentsAllPartner</s:param>
																								</s:url>
																								<s:a href="%{fileDownload}"
																									class="btn btn-sm btn-primary">
																									<i class="glyphicon glyphicon-download-alt"></i>
																								</s:a>
																							</div>
																						</div>

																					</div>
																				</td>

																			</tr>
																			<tr>
																				<td align="left" valign="middle"><strong>Certified
																						true Copy of Stamped Partnership Deed / Form "A" (
																						Firm's Registration Details with Registrar of
																						Firms / Form " E" (List of Partners filed with
																						Registrar of Firms) / </strong></td>
																				<td align="right" valign="middle"><div
																						class="row">
																						<div class="col-md-10 text-right">
																							<div class="input-group">
																								<span class="input-group-btn"> <input
																									type="text" class="inputfieldsmall" readonly>
																									<span
																									class="file-input btn btn-success btn-file btn-small">
																										<span class="glyphicon glyphicon-folder-open"></span>
																										&nbsp;&nbsp;Choose file <s:file name="PF_CTC"
																											type="file" />
																								</span></span>
																							</div>
																						</div>
																						<div class="col-md-1 text-right">
																							<div id="CertifiedTrueCopy"
																								style="visibility: hidden">
																								<s:url var="fileDownload" namespace="/"
																									action="jsp/downloadDocument" escapeAmp="false">
																									<s:param name="payId">
																										<s:property value="%{user.payId}" />
																									</s:param>
																									<s:param name="fileName">CertifiedTrueCopy</s:param>
																								</s:url>
																								<s:a href="%{fileDownload}"
																									class="btn btn-sm btn-primary">
																									<i class="glyphicon glyphicon-download-alt"></i>
																								</s:a>
																							</div>
																						</div>

																					</div></td>
																			</tr>
																			<tr>
																				<td align="left" valign="middle"><strong>List
																						of Partners </strong></td>
																				<td align="right" valign="middle"><div
																						class="row">
																						<div class="col-md-10 text-right">
																							<div class="input-group">
																								<span class="input-group-btn"> <input
																									type="text" class="inputfieldsmall" readonly>
																									<span
																									class="file-input btn btn-success btn-file btn-small">
																										<span class="glyphicon glyphicon-folder-open"></span>
																										&nbsp;&nbsp;Choose file <s:file name="PF_LOP"
																											type="file" />
																								</span></span>
																							</div>
																						</div>
																						<div class="col-md-1 text-right">
																							<div id="ListOfPartners"
																								style="visibility: hidden">
																								<s:url var="fileDownload" namespace="/"
																									action="jsp/downloadDocument" escapeAmp="false">
																									<s:param name="payId">
																										<s:property value="%{user.payId}" />
																									</s:param>
																									<s:param name="fileName">ListOfPartners</s:param>
																								</s:url>
																								<s:a href="%{fileDownload}"
																									class="btn btn-sm btn-primary">
																									<i class="glyphicon glyphicon-download-alt"></i>
																								</s:a>
																							</div>
																						</div>
																					</div></td>
																			</tr>
																			<tr>
																				<td align="left" valign="middle"><strong>Partnership
																						Letter signed by all partners.</strong></td>
																				<td align="right" valign="middle"
																					class="profilepage"><div class="row">
																						<div class="col-md-10 text-right">
																							<div class="input-group">
																								<span class="input-group-btn"> <input
																									type="text" class="inputfieldsmall" readonly>
																									<span
																									class="file-input btn btn-success btn-file btn-small">
																										<span class="glyphicon glyphicon-folder-open"></span>
																										&nbsp;&nbsp;Choose file <s:file name="PF_PLS"
																											type="file" />
																								</span></span>
																							</div>
																						</div>
																						<div class="col-md-1 text-right">
																							<div id="PartnershipLetterSigned"
																								style="visibility: hidden">
																								<s:url var="fileDownload" namespace="/"
																									action="jsp/downloadDocument" escapeAmp="false">
																									<s:param name="payId">
																										<s:property value="%{user.payId}" />
																									</s:param>
																									<s:param name="fileName">PartnershipLetterSigned</s:param>
																								</s:url>
																								<s:a href="%{fileDownload}"
																									class="btn btn-sm btn-primary">
																									<i class="glyphicon glyphicon-download-alt"></i>
																								</s:a>
																							</div>
																						</div>
																					</div></td>
																			</tr>
																			<tr>
																				<td align="left" valign="middle"><strong>PAN
																						Card / Allotment Letter (In case PAN not
																						furnished, a separate declaration for its non
																						allotment to be obtained.) </strong></td>
																				<td align="right" valign="middle"
																					class="profilepage"><div class="row">
																						<div class="col-md-10 text-right">
																							<div class="input-group">
																								<span class="input-group-btn"> <input
																									type="text" class="inputfieldsmall" readonly>
																									<span
																									class="file-input btn btn-success btn-file btn-small">
																										<span class="glyphicon glyphicon-folder-open"></span>
																										&nbsp;&nbsp;Choose file <s:file name="PF_PC"
																											type="file" />
																								</span></span>
																							</div>
																						</div>
																						<div class="col-md-1 text-right">
																							<div id="PANCard" style="visibility: hidden">
																								<s:url var="fileDownload" namespace="/"
																									action="jsp/downloadDocument" escapeAmp="false">
																									<s:param name="payId">
																										<s:property value="%{user.payId}" />
																									</s:param>
																									<s:param name="fileName">PANCard</s:param>
																								</s:url>
																								<s:a href="%{fileDownload}"
																									class="btn btn-sm btn-primary">
																									<i class="glyphicon glyphicon-download-alt"></i>
																								</s:a>
																							</div>
																						</div>
																					</div></td>
																			</tr>
																			<tr>
																				<td align="left" valign="middle"><strong>Address
																						Proofs ( As listed under "Address Proof") </strong></td>
																				<td align="right" valign="middle"
																					class="profilepage">
																					<div class="row">
																						<div class="col-md-10 text-right">
																							<div class="input-group">
																								<span class="input-group-btn"> <input
																									type="text" class="inputfieldsmall" readonly>
																									<span
																									class="file-input btn btn-success btn-file btn-small">
																										<span class="glyphicon glyphicon-folder-open"></span>
																										&nbsp;&nbsp;Choose file <s:file name="PF_AP"
																											type="file" />
																								</span></span>
																							</div>
																						</div>
																						<div class="col-md-1 text-right">
																							<div id="AddressProofsPartnership"
																								style="visibility: hidden">
																								<s:url var="fileDownload" namespace="/"
																									action="jsp/downloadDocument" escapeAmp="false">
																									<s:param name="payId">
																										<s:property value="%{user.payId}" />
																									</s:param>
																									<s:param name="fileName">AddressProofsPartnership</s:param>
																								</s:url>
																								<s:a href="%{fileDownload}"
																									class="btn btn-sm btn-primary">
																									<i class="glyphicon glyphicon-download-alt"></i>
																								</s:a>
																							</div>
																						</div>
																					</div>

																				</td>
																			</tr>
																			<tr>
																				<td align="left" valign="middle"><strong>Identification
																						Documents of Authorized Signatories (as listed
																						separately under "Resident Individuals") along
																						with authority letter to sign the agreement</strong></td>
																				<td align="right" valign="middle">
																					<div class="row">
																						<div class="col-md-10 text-right">
																							<div class="input-group">
																								<span class="input-group-btn"> <input
																									type="text" class="inputfieldsmall" readonly>
																									<span
																									class="file-input btn btn-success btn-file btn-small">
																										<span class="glyphicon glyphicon-folder-open"></span>
																										&nbsp;&nbsp;Choose file <s:file
																											name="PF_IDOAS" type="file" />
																								</span></span>
																							</div>
																						</div>
																						<div class="col-md-1 text-right">
																							<div id="IdentificationDocumentsAuthorized"
																								style="visibility: hidden">
																								<s:url var="fileDownload" namespace="/"
																									action="jsp/downloadDocument" escapeAmp="false">
																									<s:param name="payId">
																										<s:property value="%{user.payId}" />
																									</s:param>
																									<s:param name="fileName">IdentificationDocumentsAuthorized</s:param>
																								</s:url>
																								<s:a href="%{fileDownload}"
																									class="btn btn-sm btn-primary">
																									<i class="glyphicon glyphicon-download-alt"></i>
																								</s:a>
																							</div>
																						</div>
																					</div>

																				</td>
																			</tr>
																			<tr>
																				<td align="left" valign="middle"><strong>Bank
																						statement of last 6 months/Income Tax return </strong></td>
																				<td align="right" valign="middle"
																					class="profilepage">
																					<div class="row">
																						<div class="col-md-10 text-right">
																							<div class="input-group">
																								<span class="input-group-btn"> <input
																									type="text" class="inputfieldsmall" readonly>
																									<span
																									class="file-input btn btn-success btn-file btn-small">
																										<span class="glyphicon glyphicon-folder-open"></span>
																										&nbsp;&nbsp;Choose file <s:file name="PF_BS"
																											type="file" />
																								</span></span>
																							</div>
																						</div>
																						<div class="col-md-1 text-right">
																							<div id="BankStatementPartnership"
																								style="visibility: hidden">
																								<s:url var="fileDownload" namespace="/"
																									action="jsp/downloadDocument" escapeAmp="false">
																									<s:param name="payId">
																										<s:property value="%{user.payId}" />
																									</s:param>
																									<s:param name="fileName">BankStatementPartnership</s:param>
																								</s:url>
																								<s:a href="%{fileDownload}"
																									class="btn btn-sm btn-primary">
																									<i class="glyphicon glyphicon-download-alt"></i>
																								</s:a>
																							</div>
																						</div>
																					</div>

																				</td>
																			</tr>
																			<tr>
																				<td align="left" valign="middle"><strong>License
																						under Shops &amp; Establishments Act /
																						Registration for Sales / Service tax / VAT /
																						Excise Registration / Import-Export Certificate
																						etc / Business License </strong></td>
																				<td align="right" valign="middle"
																					class="profilepage">
																					<div class="row">
																						<div class="col-md-10 text-right">
																							<div class="input-group">
																								<span class="input-group-btn"> <input
																									type="text" class="inputfieldsmall" readonly>
																									<span
																									class="file-input btn btn-success btn-file btn-small">
																										<span class="glyphicon glyphicon-folder-open"></span>
																										&nbsp;&nbsp;Choose file <s:file name="PF_LUS"
																											type="file" />
																								</span></span>
																							</div>
																						</div>
																						<div class="col-md-1 text-right">
																							<div id="LicenseUnderShop"
																								style="visibility: hidden">
																								<s:url var="fileDownload" namespace="/"
																									action="jsp/downloadDocument" escapeAmp="false">
																									<s:param name="payId">
																										<s:property value="%{user.payId}" />
																									</s:param>
																									<s:param name="fileName">LicenseUnderShop</s:param>
																								</s:url>
																								<s:a href="%{fileDownload}"
																									class="btn btn-sm btn-primary">
																									<i class="glyphicon glyphicon-download-alt"></i>
																								</s:a>
																							</div>
																						</div>
																					</div>

																				</td>
																			</tr>
																		</table>
																	</div>
																	<div id="divPR" style="display: none;">
																		<table width="100%" border="0" cellspacing="0"
																			cellpadding="0" class="product-specbig">
																			<tr>
																				<td colspan="3" align="left" valign="middle"
																					class="blbxf"><strong>Copies Of:</strong></td>
																			</tr>
																			<tr>
																				<td width="70%" align="left" valign="middle"><strong>Identification
																						document of the Proprietor (as listed separately
																						under &quot;Resident Individuals&quot;) </strong></td>
																				<td width="30%" align="left" valign="middle">
																					<div class="row text-left">
																						<div class="col-md-10 text-right">
																							<div class="input-group">
																								<span class="input-group-btn"> <input
																									type="text" class="inputfieldsmall" readonly>
																									<span
																									class="file-input btn btn-success btn-file btn-small">
																										<span class="glyphicon glyphicon-folder-open"></span>
																										&nbsp;&nbsp;Choose file <s:file name="PR_ID"
																											type="file" />
																								</span></span>
																							</div>
																						</div>
																						<div class="col-md-1 text-left">
																							<div id="IdentificationDocumentsProprietor"
																								style="visibility: hidden">
																								<s:url var="fileDownload" namespace="/"
																									action="jsp/downloadDocument" escapeAmp="false">
																									<s:param name="payId">
																										<s:property value="%{user.payId}" />
																									</s:param>
																									<s:param name="fileName">IdentificationDocumentsProprietor</s:param>
																								</s:url>
																								<s:a href="%{fileDownload}"
																									class="btn btn-sm btn-primary">
																									<i class="glyphicon glyphicon-download-alt"></i>
																								</s:a>
																							</div>
																						</div>

																					</div>
																				</td>

																			</tr>
																			<tr>
																				<td align="left" valign="middle"><strong>PAN
																						Card of Proprietor </strong></td>
																				<td align="right" valign="middle"><div
																						class="row">
																						<div class="col-md-10 text-right">
																							<div class="input-group">
																								<span class="input-group-btn"> <input
																									type="text" class="inputfieldsmall" readonly>
																									<span
																									class="file-input btn btn-success btn-file btn-small">
																										<span class="glyphicon glyphicon-folder-open"></span>
																										&nbsp;&nbsp;Choose file <s:file name="PR_PAN"
																											type="file" />
																								</span></span>
																							</div>
																						</div>
																						<div class="col-md-1 text-right">
																							<div id="PANCardProprietor"
																								style="visibility: hidden">
																								<s:url var="fileDownload" namespace="/"
																									action="jsp/downloadDocument" escapeAmp="false">
																									<s:param name="payId">
																										<s:property value="%{user.payId}" />
																									</s:param>
																									<s:param name="fileName">PANCardProprietor</s:param>
																								</s:url>
																								<s:a href="%{fileDownload}"
																									class="btn btn-sm btn-primary">
																									<i class="glyphicon glyphicon-download-alt"></i>
																								</s:a>
																							</div>
																						</div>

																					</div></td>
																			</tr>
																			<tr>
																				<td align="left" valign="middle"><strong>Proof
																						of Entity : License under Shops &amp;
																						Establishments Act / Registration for Sales /
																						Service tax / VAT / Excise Registration /
																						Import-Export Certificate etc / Business License /
																						Utility Bill in the name of the Firm indicating
																						name of the Proprietor / PAN Card or PAN Allotment
																						Letter (in the name of the Firm) </strong></td>
																				<td align="right" valign="middle"><div
																						class="row">
																						<div class="col-md-10 text-right">
																							<div class="input-group">
																								<span class="input-group-btn"> <input
																									type="text" class="inputfieldsmall" readonly>
																									<span
																									class="file-input btn btn-success btn-file btn-small">
																										<span class="glyphicon glyphicon-folder-open"></span>
																										&nbsp;&nbsp;Choose file <s:file name="PR_POE"
																											type="file" />
																								</span></span>
																							</div>
																						</div>
																						<div class="col-md-1 text-right">
																							<div id="ProofofEntity"
																								style="visibility: hidden">
																								<s:url var="fileDownload" namespace="/"
																									action="jsp/downloadDocument" escapeAmp="false">
																									<s:param name="payId">
																										<s:property value="%{user.payId}" />
																									</s:param>
																									<s:param name="fileName">ProofofEntity</s:param>
																								</s:url>
																								<s:a href="%{fileDownload}"
																									class="btn btn-sm btn-primary">
																									<i class="glyphicon glyphicon-download-alt"></i>
																								</s:a>
																							</div>
																						</div>
																					</div></td>
																			</tr>
																			<tr>
																				<td align="left" valign="middle"><strong>Address
																						Proofs ( As listed under "Address Proof") </strong></td>
																				<td align="right" valign="middle"
																					class="profilepage"><div class="row">
																						<div class="col-md-10 text-right">
																							<div class="input-group">
																								<span class="input-group-btn"> <input
																									type="text" class="inputfieldsmall" readonly>
																									<span
																									class="file-input btn btn-success btn-file btn-small">
																										<span class="glyphicon glyphicon-folder-open"></span>
																										&nbsp;&nbsp;Choose file <s:file name="PR_AP"
																											type="file" />
																								</span></span>
																							</div>
																						</div>
																						<div class="col-md-1 text-right">
																							<div id="AddressProofsProprietor"
																								style="visibility: hidden">
																								<s:url var="fileDownload" namespace="/"
																									action="jsp/downloadDocument" escapeAmp="false">
																									<s:param name="payId">
																										<s:property value="%{user.payId}" />
																									</s:param>
																									<s:param name="fileName">AddressProofsProprietor</s:param>
																								</s:url>
																								<s:a href="%{fileDownload}"
																									class="btn btn-sm btn-primary">
																									<i class="glyphicon glyphicon-download-alt"></i>
																								</s:a>
																							</div>
																						</div>
																					</div></td>
																			</tr>
																			<tr>
																				<td align="left" valign="middle"><strong>Bank
																						statement of last 6 months/Income Tax return </strong></td>
																				<td align="right" valign="middle"
																					class="profilepage"><div class="row">
																						<div class="col-md-10 text-right">
																							<div class="input-group">
																								<span class="input-group-btn"> <input
																									type="text" class="inputfieldsmall" readonly>
																									<span
																									class="file-input btn btn-success btn-file btn-small">
																										<span class="glyphicon glyphicon-folder-open"></span>
																										&nbsp;&nbsp;Choose file <s:file name="PR_BS"
																											type="file" />
																								</span></span>
																							</div>
																						</div>
																						<div class="col-md-1 text-right">
																							<div id="BankStatementProprietor"
																								style="visibility: hidden">
																								<s:url var="fileDownload" namespace="/"
																									action="jsp/downloadDocument" escapeAmp="false">
																									<s:param name="payId">
																										<s:property value="%{user.payId}" />
																									</s:param>
																									<s:param name="fileName">BankStatementProprietor</s:param>
																								</s:url>
																								<s:a href="%{fileDownload}"
																									class="btn btn-sm btn-primary">
																									<i class="glyphicon glyphicon-download-alt"></i>
																								</s:a>
																							</div>
																						</div>
																					</div></td>
																			</tr>
																			<tr>
																				<td align="left" valign="middle"><strong>License
																						under Shops &amp; Establishments Act /
																						Registration for Sales / Service tax / VAT /
																						Excise Registration / Import-Export Certificate
																						etc / Business License </strong></td>
																				<td align="right" valign="middle"
																					class="profilepage">
																					<div class="row">
																						<div class="col-md-10 text-right">
																							<div class="input-group">
																								<span class="input-group-btn"> <input
																									type="text" class="inputfieldsmall" readonly>
																									<span
																									class="file-input btn btn-success btn-file btn-small">
																										<span class="glyphicon glyphicon-folder-open"></span>
																										&nbsp;&nbsp;Choose file <s:file name="PR_LUS"
																											type="file" />
																								</span></span>
																							</div>
																						</div>
																						<div class="col-md-1 text-right">
																							<div id="LicenseUnderShopProprietor"
																								style="visibility: hidden">
																								<s:url var="fileDownload" namespace="/"
																									action="jsp/downloadDocument" escapeAmp="false">
																									<s:param name="payId">
																										<s:property value="%{user.payId}" />
																									</s:param>
																									<s:param name="fileName">LicenseUnderShopProprietor</s:param>
																								</s:url>
																								<s:a href="%{fileDownload}"
																									class="btn btn-sm btn-primary">
																									<i class="glyphicon glyphicon-download-alt"></i>
																								</s:a>
																							</div>
																						</div>
																					</div>

																				</td>
																			</tr>
																		</table>
																	</div>
																	<div id="divCSA" style="display: none;">
																		<table width="100%" border="0" cellspacing="0"
																			cellpadding="0" class="product-specbig">
																			<tr>
																				<td colspan="3" align="left" valign="middle"
																					class="blbxf"><strong>Certified
																						Copies Of:</strong></td>
																			</tr>
																			<tr>
																				<td width="70%" align="left" valign="middle"><strong>Bye
																						Laws </strong></td>
																				<td width="30%" align="left" valign="middle">
																					<div class="row text-left">
																						<div class="col-md-10 text-right">
																							<div class="input-group">
																								<span class="input-group-btn"> <input
																									type="text" class="inputfieldsmall" readonly>
																									<span
																									class="file-input btn btn-success btn-file btn-small">
																										<span class="glyphicon glyphicon-folder-open"></span>
																										&nbsp;&nbsp;Choose file <s:file name="CSA_BL"
																											type="file" />
																								</span></span>
																							</div>
																						</div>
																						<div class="col-md-1 text-left">
																							<div id="Laws" style="visibility: hidden">
																								<s:url var="fileDownload" namespace="/"
																									action="jsp/downloadDocument" escapeAmp="false">
																									<s:param name="payId">
																										<s:property value="%{user.payId}" />
																									</s:param>
																									<s:param name="fileName">Laws</s:param>
																								</s:url>
																								<s:a href="%{fileDownload}"
																									class="btn btn-sm btn-primary">
																									<i class="glyphicon glyphicon-download-alt"></i>
																								</s:a>
																							</div>
																						</div>

																					</div>
																				</td>

																			</tr>
																			<tr>
																				<td align="left" valign="middle"><strong>General
																						Body Resolution for Appointment of Office Bearers.
																				</strong></td>
																				<td align="right" valign="middle"><div
																						class="row">
																						<div class="col-md-10 text-right">
																							<div class="input-group">
																								<span class="input-group-btn"> <input
																									type="text" class="inputfieldsmall" readonly>
																									<span
																									class="file-input btn btn-success btn-file btn-small">
																										<span class="glyphicon glyphicon-folder-open"></span>
																										&nbsp;&nbsp;Choose file <s:file name="CSA_GBR"
																											type="file" />
																								</span></span>
																							</div>
																						</div>
																						<div class="col-md-1 text-right">
																							<div id="GeneralBodyResolution"
																								style="visibility: hidden">
																								<s:url var="fileDownload" namespace="/"
																									action="jsp/downloadDocument" escapeAmp="false">
																									<s:param name="payId">
																										<s:property value="%{user.payId}" />
																									</s:param>
																									<s:param name="fileName">GeneralBodyResolution</s:param>
																								</s:url>
																								<s:a href="%{fileDownload}"
																									class="btn btn-sm btn-primary">
																									<i class="glyphicon glyphicon-download-alt"></i>
																								</s:a>
																							</div>
																						</div>

																					</div></td>
																			</tr>
																			<tr>
																				<td align="left" valign="middle"><strong>PAN
																						Card </strong></td>
																				<td align="right" valign="middle"><div
																						class="row">
																						<div class="col-md-10 text-right">
																							<div class="input-group">
																								<span class="input-group-btn"> <input
																									type="text" class="inputfieldsmall" readonly>
																									<span
																									class="file-input btn btn-success btn-file btn-small">
																										<span class="glyphicon glyphicon-folder-open"></span>
																										&nbsp;&nbsp;Choose file <s:file name="CSA_PC"
																											type="file" />
																								</span></span>
																							</div>
																						</div>
																						<div class="col-md-1 text-right">
																							<div id="PANCardClub" style="visibility: hidden">
																								<s:url var="fileDownload" namespace="/"
																									action="jsp/downloadDocument" escapeAmp="false">
																									<s:param name="payId">
																										<s:property value="%{user.payId}" />
																									</s:param>
																									<s:param name="fileName">PANCardClub</s:param>
																								</s:url>
																								<s:a href="%{fileDownload}"
																									class="btn btn-sm btn-primary">
																									<i class="glyphicon glyphicon-download-alt"></i>
																								</s:a>
																							</div>
																						</div>
																					</div></td>
																			</tr>
																		</table>
																	</div>
																	<div id="divLLL" style="display: none;">
																		<table width="100%" border="0" cellspacing="0"
																			cellpadding="0" class="product-specbig">
																			<tr>
																				<td colspan="3" align="left" valign="middle"
																					class="blbxf"><strong>Copies Of:</strong></td>
																			</tr>
																			<tr>
																				<td width="70%" align="left" valign="middle"><strong>Registration
																						Certificate under LLP Act,2008</strong></td>
																				<td width="30%" align="left" valign="middle">
																					<div class="row text-left">
																						<div class="col-md-10 text-right">
																							<div class="input-group">
																								<span class="input-group-btn"> <input
																									type="text" class="inputfieldsmall" readonly>
																									<span
																									class="file-input btn btn-success btn-file btn-small">
																										<span class="glyphicon glyphicon-folder-open"></span>
																										&nbsp;&nbsp;Choose file <s:file name="LLL_RC"
																											type="file" />
																								</span></span>
																							</div>
																						</div>
																						<div class="col-md-1 text-left">
																							<div id="RegistrationCertificate"
																								style="visibility: hidden">
																								<s:url var="fileDownload" namespace="/"
																									action="jsp/downloadDocument" escapeAmp="false">
																									<s:param name="payId">
																										<s:property value="%{user.payId}" />
																									</s:param>
																									<s:param name="fileName">RegistrationCertificate</s:param>
																								</s:url>
																								<s:a href="%{fileDownload}"
																									class="btn btn-sm btn-primary">
																									<i class="glyphicon glyphicon-download-alt"></i>
																								</s:a>
																							</div>
																						</div>

																					</div>
																				</td>

																			</tr>
																			<tr>
																				<td align="left" valign="middle"><strong>LLP
																						Agreement Deed </strong></td>
																				<td align="right" valign="middle"><div
																						class="row">
																						<div class="col-md-10 text-right">
																							<div class="input-group">
																								<span class="input-group-btn"> <input
																									type="text" class="inputfieldsmall" readonly>
																									<span
																									class="file-input btn btn-success btn-file btn-small">
																										<span class="glyphicon glyphicon-folder-open"></span>
																										&nbsp;&nbsp;Choose file <s:file name="LLL_LAD"
																											type="file" />
																								</span></span>
																							</div>
																						</div>
																						<div class="col-md-1 text-right">
																							<div id="LLPAgreement" style="visibility: hidden">
																								<s:url var="fileDownload" namespace="/"
																									action="jsp/downloadDocument" escapeAmp="false">
																									<s:param name="payId">
																										<s:property value="%{user.payId}" />
																									</s:param>
																									<s:param name="fileName">LLPAgreement</s:param>
																								</s:url>
																								<s:a href="%{fileDownload}"
																									class="btn btn-sm btn-primary">
																									<i class="glyphicon glyphicon-download-alt"></i>
																								</s:a>
																							</div>
																						</div>

																					</div></td>
																			</tr>
																			<tr>
																				<td align="left" valign="middle"><strong>List
																						of Partners </strong></td>
																				<td align="right" valign="middle"><div
																						class="row">
																						<div class="col-md-10 text-right">
																							<div class="input-group">
																								<span class="input-group-btn"> <input
																									type="text" class="inputfieldsmall" readonly>
																									<span
																									class="file-input btn btn-success btn-file btn-small">
																										<span class="glyphicon glyphicon-folder-open"></span>
																										&nbsp;&nbsp;Choose file <s:file name="LLL_LOP"
																											type="file" />
																								</span></span>
																							</div>
																						</div>
																						<div class="col-md-1 text-right">
																							<div id="ListOfPartners"
																								style="visibility: hidden">
																								<s:url var="fileDownload" namespace="/"
																									action="jsp/downloadDocument" escapeAmp="false">
																									<s:param name="payId">
																										<s:property value="%{user.payId}" />
																									</s:param>
																									<s:param name="fileName">ListOfPartners</s:param>
																								</s:url>
																								<s:a href="%{fileDownload}"
																									class="btn btn-sm btn-primary">
																									<i class="glyphicon glyphicon-download-alt"></i>
																								</s:a>
																							</div>
																						</div>
																					</div></td>
																			</tr>
																			<tr>

																				<td align="left" valign="middle"><strong>Identification
																						documents of all partners/designated partners </strong></td>
																				<td width="30%" align="left" valign="middle"><div
																						class="row text-left">
																						<div class="col-md-10 text-right">
																							<div class="input-group">
																								<span class="input-group-btn"> <input
																									type="text" class="inputfieldsmall" readonly>
																									<span
																									class="file-input btn btn-success btn-file btn-small">
																										<span class="glyphicon glyphicon-folder-open"></span>
																										&nbsp;&nbsp;Choose file <s:file name="LLL_ID"
																											type="file" />
																								</span></span>
																							</div>
																						</div>
																						<div class="col-md-1 text-left">
																							<div id="IdentificationDocumentsLLP"
																								style="visibility: hidden">
																								<s:url var="fileDownload" namespace="/"
																									action="jsp/downloadDocument" escapeAmp="false">
																									<s:param name="payId">
																										<s:property value="%{user.payId}" />
																									</s:param>
																									<s:param name="fileName">IdentificationDocumentsLLP</s:param>
																								</s:url>
																								<s:a href="%{fileDownload}"
																									class="btn btn-sm btn-primary">
																									<i class="glyphicon glyphicon-download-alt"></i>
																								</s:a>
																							</div>
																						</div>
																					</div></td>
																			</tr>
																			<tr>
																				<td align="left" valign="middle"><strong>Authorization
																						letter signed by all partners </strong></td>
																				<td align="right" valign="middle"><div
																						class="row">
																						<div class="col-md-10 text-right">
																							<div class="input-group">
																								<span class="input-group-btn"> <input
																									type="text" class="inputfieldsmall" readonly>
																									<span
																									class="file-input btn btn-success btn-file btn-small">
																										<span class="glyphicon glyphicon-folder-open"></span>
																										&nbsp;&nbsp;Choose file <s:file name="LLL_AL"
																											type="file" />
																								</span></span>
																							</div>
																						</div>
																						<div class="col-md-1 text-right">
																							<div id="AuthorizationLetter"
																								style="visibility: hidden">
																								<s:url var="fileDownload" namespace="/"
																									action="jsp/downloadDocument" escapeAmp="false">
																									<s:param name="payId">
																										<s:property value="%{user.payId}" />
																									</s:param>
																									<s:param name="fileName">AuthorizationLetter</s:param>
																								</s:url>
																								<s:a href="%{fileDownload}"
																									class="btn btn-sm btn-primary">
																									<i class="glyphicon glyphicon-download-alt"></i>
																								</s:a>
																							</div>
																						</div>
																					</div></td>
																			</tr>
																			<tr>
																				<td align="left" valign="middle"><strong>PAN
																						card of company, If designated partner is company
																				</strong></td>
																				<td align="right" valign="middle"><div
																						class="row">
																						<div class="col-md-10 text-right">
																							<div class="input-group">
																								<span class="input-group-btn"> <input
																									type="text" class="inputfieldsmall" readonly>
																									<span
																									class="file-input btn btn-success btn-file btn-small">
																										<span class="glyphicon glyphicon-folder-open"></span>
																										&nbsp;&nbsp;Choose file <s:file name="LLL_PCC"
																											type="file" />
																								</span></span>
																							</div>
																						</div>
																						<div class="col-md-1 text-right">
																							<div id="PANCardCompany"
																								style="visibility: hidden">
																								<s:url var="fileDownload" namespace="/"
																									action="jsp/downloadDocument" escapeAmp="false">
																									<s:param name="payId">
																										<s:property value="%{user.payId}" />
																									</s:param>
																									<s:param name="fileName">PANCardCompany</s:param>
																								</s:url>
																								<s:a href="%{fileDownload}"
																									class="btn btn-sm btn-primary">
																									<i class="glyphicon glyphicon-download-alt"></i>
																								</s:a>
																							</div>
																						</div>
																					</div></td>
																			</tr>
																			<tr>
																				<td align="left" valign="middle"><strong>DIN
																						registration form </strong></td>
																				<td align="right" valign="middle"><div
																						class="row">
																						<div class="col-md-10 text-right">
																							<div class="input-group">
																								<span class="input-group-btn"> <input
																									type="text" class="inputfieldsmall" readonly>
																									<span
																									class="file-input btn btn-success btn-file btn-small">
																										<span class="glyphicon glyphicon-folder-open"></span>
																										&nbsp;&nbsp;Choose file <s:file name="LLL_DR"
																											type="file" />
																								</span></span>
																							</div>
																						</div>
																						<div class="col-md-1 text-right">
																							<div id="DINRegistration"
																								style="visibility: hidden">
																								<s:url var="fileDownload" namespace="/"
																									action="jsp/downloadDocument" escapeAmp="false">
																									<s:param name="payId">
																										<s:property value="%{user.payId}" />
																									</s:param>
																									<s:param name="fileName">DINRegistration</s:param>
																								</s:url>
																								<s:a href="%{fileDownload}"
																									class="btn btn-sm btn-primary">
																									<i class="glyphicon glyphicon-download-alt"></i>
																								</s:a>
																							</div>
																						</div>
																					</div></td>
																			</tr>
																			<tr>
																				<td align="left" valign="middle"><strong>Address
																						Proof </strong></td>
																				<td align="right" valign="middle"><div
																						class="row">
																						<div class="col-md-10 text-right">
																							<div class="input-group">
																								<span class="input-group-btn"> <input
																									type="text" class="inputfieldsmall" readonly>
																									<span
																									class="file-input btn btn-success btn-file btn-small">
																										<span class="glyphicon glyphicon-folder-open"></span>
																										&nbsp;&nbsp;Choose file <s:file name="LLL_AP"
																											type="file" />
																								</span></span>
																							</div>
																						</div>
																						<div class="col-md-1 text-right">
																							<div id="AddressProofsLLP"
																								style="visibility: hidden">
																								<s:url var="fileDownload" namespace="/"
																									action="jsp/downloadDocument" escapeAmp="false">
																									<s:param name="payId">
																										<s:property value="%{user.payId}" />
																									</s:param>
																									<s:param name="fileName">AddressProofsLLP</s:param>
																								</s:url>
																								<s:a href="%{fileDownload}"
																									class="btn btn-sm btn-primary">
																									<i class="glyphicon glyphicon-download-alt"></i>
																								</s:a>
																							</div>
																						</div>
																					</div></td>
																			</tr>
																			<tr>
																				<td align="left" valign="middle"><strong>Bank
																						Statement of last 6 months/IT return </strong></td>
																				<td align="right" valign="middle"><div
																						class="row">
																						<div class="col-md-10 text-right">
																							<div class="input-group">
																								<span class="input-group-btn"> <input
																									type="text" class="inputfieldsmall" readonly>
																									<span
																									class="file-input btn btn-success btn-file btn-small">
																										<span class="glyphicon glyphicon-folder-open"></span>
																										&nbsp;&nbsp;Choose file <s:file name="LLL_BS"
																											type="file" />
																								</span></span>
																							</div>
																						</div>
																						<div class="col-md-1 text-right">
																							<div id="BankStatementLLP"
																								style="visibility: hidden">
																								<s:url var="fileDownload" namespace="/"
																									action="jsp/downloadDocument" escapeAmp="false">
																									<s:param name="payId">
																										<s:property value="%{user.payId}" />
																									</s:param>
																									<s:param name="fileName">BankStatementLLP</s:param>
																								</s:url>
																								<s:a href="%{fileDownload}"
																									class="btn btn-sm btn-primary">
																									<i class="glyphicon glyphicon-download-alt"></i>
																								</s:a>
																							</div>
																						</div>
																					</div></td>
																			</tr>
																			<tr>
																				<td align="left" valign="middle"><strong>PAN
																						card of LLP </strong></td>
																				<td align="right" valign="middle"><div
																						class="row">
																						<div class="col-md-10 text-right">
																							<div class="input-group">
																								<span class="input-group-btn"> <input
																									type="text" class="inputfieldsmall" readonly>
																									<span
																									class="file-input btn btn-success btn-file btn-small">
																										<span class="glyphicon glyphicon-folder-open"></span>
																										&nbsp;&nbsp;Choose file <s:file name="LLL_PCL"
																											type="file" />
																								</span></span>
																							</div>
																						</div>
																						<div class="col-md-1 text-right">
																							<div id="PANOfLLP" style="visibility: hidden">
																								<s:url var="fileDownload" namespace="/"
																									action="jsp/downloadDocument" escapeAmp="false">
																									<s:param name="payId">
																										<s:property value="%{user.payId}" />
																									</s:param>
																									<s:param name="fileName">PANOfLLP</s:param>
																								</s:url>
																								<s:a href="%{fileDownload}"
																									class="btn btn-sm btn-primary">
																									<i class="glyphicon glyphicon-download-alt"></i>
																								</s:a>
																							</div>
																						</div>
																					</div></td>
																			</tr>
																			<tr>
																				<td align="left" valign="middle"><strong>License
																						under Shops &amp; Establishments Act /
																						Registration for Sales / Service tax / VAT /
																						Excise Registration / Import-Export Certificate
																						etc / Business License / </strong></td>
																				<td align="right" valign="middle"><div
																						class="row">
																						<div class="col-md-10 text-right">
																							<div class="input-group">
																								<span class="input-group-btn"> <input
																									type="text" class="inputfieldsmall" readonly>
																									<span
																									class="file-input btn btn-success btn-file btn-small">
																										<span class="glyphicon glyphicon-folder-open"></span>
																										&nbsp;&nbsp;Choose file <s:file name="LLL_LUS"
																											type="file" />
																								</span></span>
																							</div>
																						</div>
																						<div class="col-md-1 text-right">
																							<div id="LicenseUnderShopLLP"
																								style="visibility: hidden">
																								<s:url var="fileDownload" namespace="/"
																									action="jsp/downloadDocument" escapeAmp="false">
																									<s:param name="payId">
																										<s:property value="%{user.payId}" />
																									</s:param>
																									<s:param name="fileName">LicenseUnderShopLLP</s:param>
																								</s:url>
																								<s:a href="%{fileDownload}">Download</s:a>
																							</div>
																						</div>
																					</div></td>
																			</tr>
																		</table>

																	</div>
																	<div id="divRI" style="display: none;">
																		<table width="100%" border="0" cellspacing="0"
																			cellpadding="0" class="product-specbig">
																			<tr>
																				<td colspan="3" align="left" valign="middle"
																					class="blbxf"><strong>Certified
																						Copies of any of the following:</strong></td>
																			</tr>
																			<tr>
																				<td width="70%" align="left" valign="middle"><strong>Passport
																						(with name, photograph &amp; specimen signatures)
																				</strong></td>
																				<td width="30%" align="left" valign="middle">
																					<div class="row text-left">
																						<div class="col-md-10 text-right">
																							<div class="input-group">
																								<span class="input-group-btn"> <input
																									type="text" class="inputfieldsmall" readonly>
																									<span
																									class="file-input btn btn-success btn-file btn-small">
																										<span class="glyphicon glyphicon-folder-open"></span>
																										&nbsp;&nbsp;Choose file <s:file name="RI_P"
																											type="file" />
																								</span></span>
																							</div>
																						</div>
																						<div class="col-md-1 text-left">
																							<div id="Passport" style="visibility: hidden">
																								<s:url var="fileDownload" namespace="/"
																									action="jsp/downloadDocument" escapeAmp="false">
																									<s:param name="payId">
																										<s:property value="%{user.payId}" />
																									</s:param>
																									<s:param name="fileName">Passport</s:param>
																								</s:url>
																								<s:a href="%{fileDownload}"
																									class="btn btn-sm btn-primary">
																									<i class="glyphicon glyphicon-download-alt"></i>
																								</s:a>
																							</div>
																						</div>

																					</div>
																				</td>

																			</tr>
																			<tr>
																				<td align="left" valign="middle"><strong>PAN
																						Card (with name, photograph &amp; specimen
																						signatures) </strong></td>
																				<td align="right" valign="middle"><div
																						class="row">
																						<div class="col-md-10 text-right">
																							<div class="input-group">
																								<span class="input-group-btn"> <input
																									type="text" class="inputfieldsmall" readonly>
																									<span
																									class="file-input btn btn-success btn-file btn-small">
																										<span class="glyphicon glyphicon-folder-open"></span>
																										&nbsp;&nbsp;Choose file <s:file name="RI_PC"
																											type="file" />
																								</span></span>
																							</div>
																						</div>
																						<div class="col-md-1 text-right">
																							<div id="PANCardResident"
																								style="visibility: hidden">
																								<s:url var="fileDownload" namespace="/"
																									action="jsp/downloadDocument" escapeAmp="false">
																									<s:param name="payId">
																										<s:property value="%{user.payId}" />
																									</s:param>
																									<s:param name="fileName">PANCardResident</s:param>
																								</s:url>
																								<s:a href="%{fileDownload}"
																									class="btn btn-sm btn-primary">
																									<i class="glyphicon glyphicon-download-alt"></i>
																								</s:a>
																							</div>
																						</div>

																					</div></td>
																			</tr>
																			<tr>
																				<td align="left" valign="middle"><strong>Driving
																						License (with name, photograph &amp; specimen
																						signatures) </strong></td>
																				<td align="right" valign="middle"><div
																						class="row">
																						<div class="col-md-10 text-right">
																							<div class="input-group">
																								<span class="input-group-btn"> <input
																									type="text" class="inputfieldsmall" readonly>
																									<span
																									class="file-input btn btn-success btn-file btn-small">
																										<span class="glyphicon glyphicon-folder-open"></span>
																										&nbsp;&nbsp;Choose file <s:file name="RI_DL"
																											type="file" />
																								</span></span>
																							</div>
																						</div>
																						<div class="col-md-1 text-right">
																							<div id="DrivingLicense"
																								style="visibility: hidden">
																								<s:url var="fileDownload" namespace="/"
																									action="jsp/downloadDocument" escapeAmp="false">
																									<s:param name="payId">
																										<s:property value="%{user.payId}" />
																									</s:param>
																									<s:param name="fileName">DrivingLicense</s:param>
																								</s:url>
																								<s:a href="%{fileDownload}"
																									class="btn btn-sm btn-primary">
																									<i class="glyphicon glyphicon-download-alt"></i>
																								</s:a>
																							</div>
																						</div>
																					</div></td>
																			</tr>
																			<tr>
																				<td align="left" valign="middle"><strong>Bankers
																						Verification (photograph &amp; signatures) </strong></td>
																				<td width="30%" align="left" valign="middle"><div
																						class="row text-left">
																						<div class="col-md-10 text-right">
																							<div class="input-group">
																								<span class="input-group-btn"> <input
																									type="text" class="inputfieldsmall" readonly>
																									<span
																									class="file-input btn btn-success btn-file btn-small">
																										<span class="glyphicon glyphicon-folder-open"></span>
																										&nbsp;&nbsp;Choose file <s:file name="RI_BV"
																											type="file" />
																								</span></span>
																							</div>
																						</div>
																						<div class="col-md-1 text-left">
																							<div id="BankersVerification"
																								style="visibility: hidden">
																								<s:url var="fileDownload" namespace="/"
																									action="jsp/downloadDocument" escapeAmp="false">
																									<s:param name="payId">
																										<s:property value="%{user.payId}" />
																									</s:param>
																									<s:param name="fileName">BankersVerification</s:param>
																								</s:url>
																								<s:a href="%{fileDownload}"
																									class="btn btn-sm btn-primary">
																									<i class="glyphicon glyphicon-download-alt"></i>
																								</s:a>
																							</div>
																						</div>
																					</div></td>
																			</tr>
																		</table>

																	</div>
																	<div id="divAP" style="display: none;">
																		<table width="100%" border="0" cellspacing="0"
																			cellpadding="0" class="product-specbig">
																			<tr>
																				<td colspan="3" align="left" valign="middle"
																					class="blbxf"><strong>Certified
																						Copies of any of the following:</strong></td>
																			</tr>
																			<tr>
																				<td width="70%" align="left" valign="middle"><strong>PAN
																						Intimation Letter </strong></td>
																				<td width="30%" align="left" valign="middle">
																					<div class="row text-left">
																						<div class="col-md-10 text-right">
																							<div class="input-group">
																								<span class="input-group-btn"> <input
																									type="text" class="inputfieldsmall" readonly>
																									<span
																									class="file-input btn btn-success btn-file btn-small">
																										<span class="glyphicon glyphicon-folder-open"></span>
																										&nbsp;&nbsp;Choose file <s:file name="AP_PIL"
																											type="file" />
																								</span></span>
																							</div>
																						</div>
																						<div class="col-md-1 text-left">
																							<div id="PANIntimation"
																								style="visibility: hidden">
																								<s:url var="fileDownload" namespace="/"
																									action="jsp/downloadDocument" escapeAmp="false">
																									<s:param name="payId">
																										<s:property value="%{user.payId}" />
																									</s:param>
																									<s:param name="fileName">PANIntimation</s:param>
																								</s:url>
																								<s:a href="%{fileDownload}"
																									class="btn btn-sm btn-primary">
																									<i class="glyphicon glyphicon-download-alt"></i>
																								</s:a>
																							</div>
																						</div>

																					</div>
																				</td>

																			</tr>
																			<tr>
																				<td align="left" valign="middle"><strong>Current
																						Utility Bill (Electricity / Telephone / Water Bill
																						(not more than 3 months old) </strong></td>
																				<td align="right" valign="middle"><div
																						class="row">
																						<div class="col-md-10 text-right">
																							<div class="input-group">
																								<span class="input-group-btn"> <input
																									type="text" class="inputfieldsmall" readonly>
																									<span
																									class="file-input btn btn-success btn-file btn-small">
																										<span class="glyphicon glyphicon-folder-open"></span>
																										&nbsp;&nbsp;Choose file <s:file name="AP_CUB"
																											type="file" />
																								</span></span>
																							</div>
																						</div>
																						<div class="col-md-1 text-right">
																							<div id="CurrentUtilityBill"
																								style="visibility: hidden">
																								<s:url var="fileDownload" namespace="/"
																									action="jsp/downloadDocument" escapeAmp="false">
																									<s:param name="payId">
																										<s:property value="%{user.payId}" />
																									</s:param>
																									<s:param name="fileName">CurrentUtilityBill</s:param>
																								</s:url>
																								<s:a href="%{fileDownload}"
																									class="btn btn-sm btn-primary">
																									<i class="glyphicon glyphicon-download-alt"></i>
																								</s:a>
																							</div>
																						</div>

																					</div></td>
																			</tr>
																			<tr>
																				<td align="left" valign="middle"><strong>Municipal
																						Tax (not more than 3 months old) </strong></td>
																				<td align="right" valign="middle"><div
																						class="row">
																						<div class="col-md-10 text-right">
																							<div class="input-group">
																								<span class="input-group-btn"> <input
																									type="text" class="inputfieldsmall" readonly>
																									<span
																									class="file-input btn btn-success btn-file btn-small">
																										<span class="glyphicon glyphicon-folder-open"></span>
																										&nbsp;&nbsp;Choose file <s:file name="AP_MT"
																											type="file" />
																								</span></span>
																							</div>
																						</div>
																						<div class="col-md-1 text-right">
																							<div id="MunicipalTax" style="visibility: hidden">
																								<s:url var="fileDownload" namespace="/"
																									action="jsp/downloadDocument" escapeAmp="false">
																									<s:param name="payId">
																										<s:property value="%{user.payId}" />
																									</s:param>
																									<s:param name="fileName">MunicipalTax</s:param>
																								</s:url>
																								<s:a href="%{fileDownload}"
																									class="btn btn-sm btn-primary">
																									<i class="glyphicon glyphicon-download-alt"></i>
																								</s:a>
																							</div>
																						</div>
																					</div></td>
																			</tr>
																			<tr>
																				<td align="left" valign="middle"><strong>Existing
																						Bank's Statement with at least six months of
																						operation. </strong></td>
																				<td width="30%" align="left" valign="middle"><div
																						class="row text-left">
																						<div class="col-md-10 text-right">
																							<div class="input-group">
																								<span class="input-group-btn"> <input
																									type="text" class="inputfieldsmall" readonly>
																									<span
																									class="file-input btn btn-success btn-file btn-small">
																										<span class="glyphicon glyphicon-folder-open"></span>
																										&nbsp;&nbsp;Choose file <s:file name="AP_EBS"
																											type="file" />
																								</span></span>
																							</div>
																						</div>
																						<div class="col-md-1 text-left">
																							<div id="ExistingBanksStatement"
																								style="visibility: hidden">
																								<s:url var="fileDownload" namespace="/"
																									action="jsp/downloadDocument" escapeAmp="false">
																									<s:param name="payId">
																										<s:property value="%{user.payId}" />
																									</s:param>
																									<s:param name="fileName">ExistingBanksStatement</s:param>
																								</s:url>
																								<s:a href="%{fileDownload}"
																									class="btn btn-sm btn-primary">
																									<i class="glyphicon glyphicon-download-alt"></i>
																								</s:a>
																							</div>
																						</div>
																					</div></td>
																			</tr>
																			<tr>
																				<td align="left" valign="middle"><strong>Existing
																						Bank's Certificate confirming name, account no,
																						date of account opening and address. </strong></td>
																				<td align="left" valign="middle"><div
																						class="row text-left">
																						<div class="col-md-10 text-right">
																							<div class="input-group">
																								<span class="input-group-btn"> <input
																									type="text" class="inputfieldsmall" readonly>
																									<span
																									class="file-input btn btn-success btn-file btn-small">
																										<span class="glyphicon glyphicon-folder-open"></span>
																										&nbsp;&nbsp;Choose file <s:file name="AP_EBC"
																											type="file" />
																								</span></span>
																							</div>
																						</div>
																						<div class="col-md-1 text-left">
																							<div id="ExistingBanksCertificate"
																								style="visibility: hidden">
																								<s:url var="fileDownload" namespace="/"
																									action="jsp/downloadDocument" escapeAmp="false">
																									<s:param name="payId">
																										<s:property value="%{user.payId}" />
																									</s:param>
																									<s:param name="fileName">ExistingBanksCertificate</s:param>
																								</s:url>
																								<s:a href="%{fileDownload}"
																									class="btn btn-sm btn-primary">
																									<i class="glyphicon glyphicon-download-alt"></i>
																								</s:a>
																							</div>
																						</div>
																					</div></td>
																			</tr>
																			<tr>
																				<td align="left" valign="middle"><strong>Insurance
																						Policy. </strong></td>
																				<td align="left" valign="middle"><div
																						class="row text-left">
																						<div class="col-md-10 text-right">
																							<div class="input-group">
																								<span class="input-group-btn"> <input
																									type="text" class="inputfieldsmall" readonly>
																									<span
																									class="file-input btn btn-success btn-file btn-small">
																										<span class="glyphicon glyphicon-folder-open"></span>
																										&nbsp;&nbsp;Choose file <s:file name="AP_IP"
																											type="file" />
																								</span></span>
																							</div>
																						</div>
																						<div class="col-md-1 text-left">
																							<div id="InsurancePolicy"
																								style="visibility: hidden">
																								<s:url var="fileDownload" namespace="/"
																									action="jsp/downloadDocument" escapeAmp="false">
																									<s:param name="payId">
																										<s:property value="%{user.payId}" />
																									</s:param>
																									<s:param name="fileName">InsurancePolicy</s:param>
																								</s:url>
																								<s:a href="%{fileDownload}"
																									class="btn btn-sm btn-primary">
																									<i class="glyphicon glyphicon-download-alt"></i>
																								</s:a>
																							</div>
																						</div>
																					</div></td>
																			</tr>
																			<tr>
																				<td align="left" valign="middle"><strong>Any
																						other document as covered under &quot;Proof of
																						Entity&quot; for Proprietorship. </strong></td>
																				<td align="left" valign="middle"><div
																						class="row text-left">
																						<div class="col-md-10 text-right">
																							<div class="input-group">
																								<span class="input-group-btn"> <input
																									type="text" class="inputfieldsmall" readonly>
																									<span
																									class="file-input btn btn-success btn-file btn-small">
																										<span class="glyphicon glyphicon-folder-open"></span>
																										&nbsp;&nbsp;Choose file <s:file name="AP_AOD"
																											type="file" />
																								</span></span>
																							</div>
																						</div>
																						<div class="col-md-1 text-left">
																							<div id="AnyOtherDocument"
																								style="visibility: hidden">
																								<s:url var="fileDownload" namespace="/"
																									action="jsp/downloadDocument" escapeAmp="false">
																									<s:param name="payId">
																										<s:property value="%{user.payId}" />
																									</s:param>
																									<s:param name="fileName">AnyOtherDocument</s:param>
																								</s:url>
																								<s:a href="%{fileDownload}"
																									class="btn btn-sm btn-primary">
																									<i class="glyphicon glyphicon-download-alt"></i>
																								</s:a>
																							</div>
																						</div>
																					</div></td>
																			</tr>
																		</table>

																	</div>
																	<div id="divT" style="display: none;">
																		<table width="100%" border="0" cellspacing="0"
																			cellpadding="0" class="product-specbig">
																			<tr>
																				<td colspan="3" align="left" valign="middle"
																					class="blbxf"><strong>Copies Of:</strong></td>
																			</tr>
																			<tr>
																				<td width="70%" align="left" valign="middle"><strong>a)
																						Resolution from the board of Directors/Trustees on
																						organization's letter head &amp; duly signed by
																						atleast two trustees. </strong></td>
																				<td width="30%" align="left" valign="middle">
																					<div class="row text-left">
																						<div class="col-md-10 text-right">
																							<div class="input-group">
																								<span class="input-group-btn"> <input
																									type="text" class="inputfieldsmall" readonly>
																									<span
																									class="file-input btn btn-success btn-file btn-small">
																										<span class="glyphicon glyphicon-folder-open"></span>
																										&nbsp;&nbsp;Choose file <s:file name="T_RFB"
																											type="file" />
																								</span></span>
																							</div>
																						</div>
																						<div class="col-md-1 text-left">
																							<div id="ResolutionFromBoard"
																								style="visibility: hidden">
																								<s:url var="fileDownload" namespace="/"
																									action="jsp/downloadDocument" escapeAmp="false">
																									<s:param name="payId">
																										<s:property value="%{user.payId}" />
																									</s:param>
																									<s:param name="fileName">ResolutionFromBoard</s:param>
																								</s:url>
																								<s:a href="%{fileDownload}"
																									class="btn btn-sm btn-primary">
																									<i class="glyphicon glyphicon-download-alt"></i>
																								</s:a>
																							</div>
																						</div>

																					</div>
																				</td>

																			</tr>
																			<tr>
																				<td align="left" valign="middle"><strong>b)
																						Signature &amp; photograph verified by your banker
																				</strong></td>
																				<td align="right" valign="middle"><div
																						class="row">
																						<div class="col-md-10 text-right">
																							<div class="input-group">
																								<span class="input-group-btn"> <input
																									type="text" class="inputfieldsmall" readonly>
																									<span
																									class="file-input btn btn-success btn-file btn-small">
																										<span class="glyphicon glyphicon-folder-open"></span>
																										&nbsp;&nbsp;Choose file <s:file name="T_SP"
																											type="file" />
																								</span></span>
																							</div>
																						</div>
																						<div class="col-md-1 text-right">
																							<div id="SignatureAndPhoto"
																								style="visibility: hidden">
																								<s:url var="fileDownload" namespace="/"
																									action="jsp/downloadDocument" escapeAmp="false">
																									<s:param name="payId">
																										<s:property value="%{user.payId}" />
																									</s:param>
																									<s:param name="fileName">SignatureAndPhoto</s:param>
																								</s:url>
																								<s:a href="%{fileDownload}"
																									class="btn btn-sm btn-primary">
																									<i class="glyphicon glyphicon-download-alt"></i>
																								</s:a>
																							</div>
																						</div>

																					</div></td>
																			</tr>
																			<tr>
																				<td align="left" valign="middle"><strong>c)
																						Certificate issued under Companies Act or
																						registration with Charity Commissioners office
																						duly attested by Authorized signatory. </strong></td>
																				<td align="right" valign="middle"><div
																						class="row">
																						<div class="col-md-10 text-right">
																							<div class="input-group">
																								<span class="input-group-btn"> <input
																									type="text" class="inputfieldsmall" readonly>
																									<span
																									class="file-input btn btn-success btn-file btn-small">
																										<span class="glyphicon glyphicon-folder-open"></span>
																										&nbsp;&nbsp;Choose file <s:file name="T_CI"
																											type="file" />
																								</span></span>
																							</div>
																						</div>
																						<div class="col-md-1 text-right">
																							<div id="CertificateIssued"
																								style="visibility: hidden">
																								<s:url var="fileDownload" namespace="/"
																									action="jsp/downloadDocument" escapeAmp="false">
																									<s:param name="payId">
																										<s:property value="%{user.payId}" />
																									</s:param>
																									<s:param name="fileName">CertificateIssued</s:param>
																								</s:url>
																								<s:a href="%{fileDownload}"
																									class="btn btn-sm btn-primary">
																									<i class="glyphicon glyphicon-download-alt"></i>
																								</s:a>
																							</div>
																						</div>
																					</div></td>
																			</tr>
																			<tr>
																				<td align="left" valign="middle"><strong>d)
																						Attested copy of Trust Deed. </strong></td>
																				<td width="30%" align="left" valign="middle"><div
																						class="row text-left">
																						<div class="col-md-10 text-right">
																							<div class="input-group">
																								<span class="input-group-btn"> <input
																									type="text" class="inputfieldsmall" readonly>
																									<span
																									class="file-input btn btn-success btn-file btn-small">
																										<span class="glyphicon glyphicon-folder-open"></span>
																										&nbsp;&nbsp;Choose file <s:file name="T_AC"
																											type="file" />
																								</span></span>
																							</div>
																						</div>
																						<div class="col-md-1 text-left">
																							<div id="AttestedCopyDeed"
																								style="visibility: hidden">
																								<s:url var="fileDownload" namespace="/"
																									action="jsp/downloadDocument" escapeAmp="false">
																									<s:param name="payId">
																										<s:property value="%{user.payId}" />
																									</s:param>
																									<s:param name="fileName">AttestedCopyDeed</s:param>
																								</s:url>
																								<s:a href="%{fileDownload}"
																									class="btn btn-sm btn-primary">
																									<i class="glyphicon glyphicon-download-alt"></i>
																								</s:a>
																							</div>
																						</div>
																					</div></td>
																			</tr>
																			<tr>
																				<td align="left" valign="middle"><strong>e)
																						Duly attested PAN card of Trust &amp; Trustees. </strong></td>
																				<td align="left" valign="middle"><div
																						class="row text-left">
																						<div class="col-md-10 text-right">
																							<div class="input-group">
																								<span class="input-group-btn"> <input
																									type="text" class="inputfieldsmall" readonly>
																									<span
																									class="file-input btn btn-success btn-file btn-small">
																										<span class="glyphicon glyphicon-folder-open"></span>
																										&nbsp;&nbsp;Choose file <s:file name="T_DTP"
																											type="file" />
																								</span></span>
																							</div>
																						</div>
																						<div class="col-md-1 text-left">
																							<div id="DutyAttestedPan"
																								style="visibility: hidden">
																								<s:url var="fileDownload" namespace="/"
																									action="jsp/downloadDocument" escapeAmp="false">
																									<s:param name="payId">
																										<s:property value="%{user.payId}" />
																									</s:param>
																									<s:param name="fileName">DutyAttestedPan</s:param>
																								</s:url>
																								<s:a href="%{fileDownload}"
																									class="btn btn-sm btn-primary">
																									<i class="glyphicon glyphicon-download-alt"></i>
																								</s:a>
																							</div>
																						</div>
																					</div></td>
																			</tr>
																			<tr>
																				<td align="left" valign="middle"><strong>f)
																						Audited balance sheet &amp; P/L statement for last
																						2 years (with a special mention of donation
																						received) &amp; Current a/c statement for last one
																						year (Trust) duly attested. </strong></td>
																				<td align="left" valign="middle"><div
																						class="row text-left">
																						<div class="col-md-10 text-right">
																							<div class="input-group">
																								<span class="input-group-btn"> <input
																									type="text" class="inputfieldsmall" readonly>
																									<span
																									class="file-input btn btn-success btn-file btn-small">
																										<span class="glyphicon glyphicon-folder-open"></span>
																										&nbsp;&nbsp;Choose file <s:file name="T_ABS"
																											type="file" />
																								</span></span>
																							</div>
																						</div>
																						<div class="col-md-1 text-left">
																							<div id="AuditedBalanceSheet"
																								style="visibility: hidden">
																								<s:url var="fileDownload" namespace="/"
																									action="jsp/downloadDocument" escapeAmp="false">
																									<s:param name="payId">
																										<s:property value="%{user.payId}" />
																									</s:param>
																									<s:param name="fileName">AuditedBalanceSheet</s:param>
																								</s:url>
																								<s:a href="%{fileDownload}"
																									class="btn btn-sm btn-primary">
																									<i class="glyphicon glyphicon-download-alt"></i>
																								</s:a>
																							</div>
																						</div>
																					</div></td>
																			</tr>
																			<tr>
																				<td align="left" valign="middle"><strong>g)
																						Sales Tax or Income Tax registration &amp; / or
																						local Municipal registration duly attested. </strong></td>
																				<td align="left" valign="middle"><div
																						class="row text-left">
																						<div class="col-md-10 text-right">
																							<div class="input-group">
																								<span class="input-group-btn"> <input
																									type="text" class="inputfieldsmall" readonly>
																									<span
																									class="file-input btn btn-success btn-file btn-small">
																										<span class="glyphicon glyphicon-folder-open"></span>
																										&nbsp;&nbsp;Choose file <s:file name="T_ST"
																											type="file" />
																								</span></span>
																							</div>
																						</div>
																						<div class="col-md-1 text-left">
																							<div id="SalesTax" style="visibility: hidden">
																								<s:url var="fileDownload" namespace="/"
																									action="jsp/downloadDocument" escapeAmp="false">
																									<s:param name="payId">
																										<s:property value="%{user.payId}" />
																									</s:param>
																									<s:param name="fileName">SalesTax</s:param>
																								</s:url>
																								<s:a href="%{fileDownload}"
																									class="btn btn-sm btn-primary">
																									<i class="glyphicon glyphicon-download-alt"></i>
																								</s:a>
																							</div>
																						</div>
																					</div></td>
																			</tr>
																			<tr>
																				<td align="left" valign="middle"><strong>h)
																						Voided check </strong></td>
																				<td align="left" valign="middle"><div
																						class="row text-left">
																						<div class="col-md-10 text-right">
																							<div class="input-group">
																								<span class="input-group-btn"> <input
																									type="text" class="inputfieldsmall" readonly>
																									<span
																									class="file-input btn btn-success btn-file btn-small">
																										<span class="glyphicon glyphicon-folder-open"></span>
																										&nbsp;&nbsp;Choose file <s:file name="T_VC"
																											type="file" />
																								</span></span>
																							</div>
																						</div>
																						<div class="col-md-1 text-left">
																							<div id="VoidedCheck" style="visibility: hidden">
																								<s:url var="fileDownload" namespace="/"
																									action="jsp/downloadDocument" escapeAmp="false">
																									<s:param name="payId">
																										<s:property value="%{user.payId}" />
																									</s:param>
																									<s:param name="fileName">VoidedCheck</s:param>
																								</s:url>
																								<s:a href="%{fileDownload}"
																									class="btn btn-sm btn-primary">
																									<i class="glyphicon glyphicon-download-alt"></i>
																								</s:a>
																							</div>
																						</div>
																					</div></td>
																			</tr>
																			<tr>
																				<td align="left" valign="middle"><strong>i)
																						Percentage of credit card turnover as donations. </strong></td>
																				<td align="left" valign="middle"><div
																						class="row text-left">
																						<div class="col-md-10 text-right">
																							<div class="input-group">
																								<span class="input-group-btn"> <input
																									type="text" class="inputfieldsmall" readonly>
																									<span
																									class="file-input btn btn-success btn-file btn-small">
																										<span class="glyphicon glyphicon-folder-open"></span>
																										&nbsp;&nbsp;Choose file <s:file name="T_POC"
																											type="file" />
																								</span></span>
																							</div>
																						</div>
																						<div class="col-md-1 text-left">
																							<div id="CreditCardTurnover"
																								style="visibility: hidden">
																								<s:url var="fileDownload" namespace="/"
																									action="jsp/downloadDocument" escapeAmp="false">
																									<s:param name="payId">
																										<s:property value="%{user.payId}" />
																									</s:param>
																									<s:param name="fileName">CreditCardTurnover</s:param>
																								</s:url>
																								<s:a href="%{fileDownload}"
																									class="btn btn-sm btn-primary">
																									<i class="glyphicon glyphicon-download-alt"></i>
																								</s:a>
																							</div>
																						</div>
																					</div></td>
																			</tr>
																		</table>

																	</div>
																</td>
															</tr>
															<tr>
																<td height="50" align="left"><s:submit value="Save"
																		class="btn btn-md btn-success aligncenter"
																		onclick="return alert('Document Successful Upload.')">
																	</s:submit></td>
															</tr>
														</table>
												</tr>
												<s:hidden name="token" value="%{#session.customToken}"></s:hidden>
											</table>

										</s:form>
									</div>
								</div>

								<div class="acordion-gray" id="HeadLogoDiv" style="display:none;">
									<H4 class="pagehead"
										onClick="CollapseAll('hide-this','LogoDiv');$('#LogoDiv').slideToggle('slow');">Logo
										Upload</H4>
								</div>
								<div id="LogoDiv">
									<div class="indent">
										<s:form action="uploadLogoAdmin" enctype="multipart/form-data"
											onsubmit="return Validate(this);">
											<table width="100%" border="0" cellspacing="0"
												cellpadding="7" class="formbox">
												<tr>
													<td><s:hidden name="payId" id="payId"
															value="%{user.payId}" /></td>
												</tr>
												<tr>
													<td align="left" valign="top"><br> <br>
														<table width="60%" align="center" border="0">
															<tr>
																<td align="right" height="30" valign="middle"
																	class="redsmalltext">Upload only .jpg formats</td>
															</tr>
															<tr>
																<td><table width="100%" align="center" border="0"
																		cellspacing="0" cellpadding="0"
																		class="product-specbig">
																		<tr>
																			<td height="30" colspan="2" align="center"
																				valign="middle"><strong>Logo</strong></td>
																		</tr>
																		<tr>
																			<td width="50%" align="center" valign="middle">
																				<div class="input-group">
																					<span class="input-group-btn"> <input
																						type="text" class="inputfieldsmall" readonly>
																						<span
																						class="file-input btn btn-success btn-file btn-small">
																							<span class="glyphicon glyphicon-folder-open"></span>
																							&nbsp;&nbsp;Choose file <s:file name="UserLogo" />
																					</span></span>
																				</div>
																			</td>
																			<td width="50%" align="left" valign="middle"><s:submit
																					value="Save" class="btn btn-success aligncenter">
																				</s:submit></td>
																		</tr>
																	</table></td>
															</tr>
															<tr>
																<td></td>
															</tr>

														</table></td>
												</tr>
												<s:hidden name="token" value="%{#session.customToken}"></s:hidden>
											</table>

										</s:form>
									</div>
								</div>

								<script type="text/javascript">
									document.getElementById('HeadActionDiv').className = 'acordion-gray acordion-open';
								</script>

							</div></td>
					</tr>
				</table></td>
		</tr>
		<tr>
			<td align="center" valign="top">&nbsp;</td>
		</tr>

	</table>
	</div>
</div>
</div>
</div>
</div>
</div>

	<script>
		$(document).on(
				'change',
				'.btn-file :file',
				function() {
					var input = $(this), numFiles = input.get(0).files ? input
							.get(0).files.length : 1, label = input.val()
							.replace(/\\/g, '/').replace(/.*\//, '');
					input.trigger('fileselect', [ numFiles, label ]);
				});

		$(document)
				.ready(
						function() {
							$('.btn-file :file')
									.on(
											'fileselect',
											function(event, numFiles, label) {

												var input = $(this).parents(
														'.input-group').find(
														':text'), log = numFiles > 1 ? numFiles
														+ ' files selected'
														: label;

												if (input.length) {
													input.val(log);
												} else {
													if (log)
														alert(log);
												}

											});
						});
	</script>
	<script>
	/*Bank Details Validation*/
	function bankNameValidation() {
	var bankNamePattern = /^[a-z\s]+$/i;
	var bankName = document.getElementById("bankName").value;
	var bankName = bankName.trim();
	if (!bankName.match(bankNamePattern)) {
		document.getElementById("bankNameId").style.display = "block";
	} else {
		document.getElementById("bankNameId").style.display = "none";
	}
}

	function accountNoValidation() {
		var accountNoPattern =;
		var accountNo = document.getElementById("accountNo").value;
		var accountNo = accountNo.trim();
		if (!accountNo.match(accountNoPattern)) {
			document.getElementById("accountNoId").style.display = "block";
		} else {
			document.getElementById("accountNoId").style.display = "none";
		}
	}
	function ifscCodeValidation() {
		var ifscCodePattern = /^[A-Z]{4}0[A-Z0-9]{6}$/;
		var ifscCode = document.getElementById("ifscCode").value;
		var ifscCode = ifscCode.trim();
		if (!ifscCode.match(ifscCodePattern)) {
			document.getElementById("ifscCodeId").style.display = "block";
		} else {
			document.getElementById("ifscCodeId").style.display = "none";
		}
	}
	</script>
 <script src="../js/main.js"></script>
</body>
</html>