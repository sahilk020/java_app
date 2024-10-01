<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta name="viewport" content="width=device-width, initial-scale=1" />
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
<title>Reseller Registration</title>



<link rel="shortcut icon" href="assets/media/images/paylogo.svg" />
<!--begin::Fonts-->
<link rel="stylesheet"
	href="https://fonts.googleapis.com/css?family=Inter:300,400,500,600,700" />
<!--end::Fonts-->
<!--begin::Vendor Stylesheets(used by this page)-->
<link
	href="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.css"
	rel="stylesheet" type="text/css" />
<link href="../assets/plugins/custom/datatables/datatables.bundle.css"
	rel="stylesheet" type="text/css" />

<!--end::Vendor Stylesheets-->
<!--begin::Global Stylesheets Bundle(used by all pages)-->
<link href="../assets/plugins/global/plugins.bundle.css"
	rel="stylesheet" type="text/css" />
<link href="../assets/css/style.bundle.css" rel="stylesheet"
	type="text/css" />
<link href="../assets/css/custom/custom-style.css" rel="stylesheet"
	type="text/css" />
<script src="../js/aes.js"></script>
<script src="../assets/plugins/global/plugins.bundle.js"></script>
<script src="../assets/js/scripts.bundle.js"></script>
<link href="../css/select2.min.css" rel="stylesheet" />
<script src="../js/jquery.select2.js" type="text/javascript"></script>

<script src="../js/jquery.popupoverlay.js"></script>

<script src="../js/jquery.formshowpop.js"></script>

<script type="text/javascript">
										$(function () {

										/* 	if ($("#userRoleType").val() == "merchant") {
												$("#tdIndustryType").show();
												var industry = document.getElementById("industryCategory").value;
												if (industry) {
													$("#subcategorydiv").show();

												} else {
													$(".errorCategory").hide();
												}
											} else {
												$("#tdIndustryType").hide();
												$("#subcategorydiv").hide();
											} */
											$("#tdIndustryType").hide();
											$("#subcategorydiv").hide();
											
											$("#userRoleType").change(function () {
											/* 	if ($(this).val() == "merchant") {
													$("#tdIndustryType").show();
													var industry = document.getElementById("industryCategory").value;
													if (industry) {
														$("#subcategorydiv").show();
													} else {
														$(".errorCategory").hide();

													}

												} else {
													$("#tdIndustryType").hide();
													$("#subcategorydiv").hide();
												} */
												$("#tdIndustryType").hide();
												$("#subcategorydiv").hide();
											});

											$("#industryCategory").change(function () {
												var industry = this.value;
												var token = document.getElementsByName("token")[0].value;

												if (!industry) {
													$("#subcategorydiv").hide();
													var subCategoryText = document.getElementById("subcategory");
													var subcategory = document.getElementById("subcategory").value;
													subCategoryText.value = "";
													return false;
												}
												var token = document.getElementsByName("token")[0].value;
												$.ajax({
													type: "POST",
													url: "industrySubCategory",
													data: {
														industryCategory: industry,
														token: token,
														"struts.token.name": "token"
													},
													success: function (data, status) {
														var subCategoryListObj = data.subCategories;
														var subCategoryList = subCategoryListObj[0].split(',');
														var radioDiv = document.getElementById("radiodiv");
														radioDiv.innerHTML = "";
														for (var i = 0; i < subCategoryList.length; i++) {
															var subcategory = subCategoryList[i];
															var radioOption = document.createElement("INPUT");
															radioOption.setAttribute("type", "radio");
															radioOption.setAttribute("value", subcategory);
															radioOption.setAttribute("name", "subcategory");
															var labelS = document.createElement("SPAN");
															labelS.innerHTML = subcategory;
															radioDiv.appendChild(radioOption);
															radioDiv.appendChild(labelS);
														}
														$('#popup').popup({
															'blur': false,
															'escape': false
														}
														).popup('show');
													},
													error: function (status) {
														//alert("Network error please try again later!!");
														return false;
													}
												});
											});
										});
										function loadSubcategory() {
											var industry = document.getElementById("industryCategory").value;
											var token = document.getElementsByName("token")[0].value;

											$.ajax({
												type: "POST",
												url: "industrySubCategory",
												data: {
													industryCategory: industry,
													token: token,
													"struts.token.name": "token"
												},
												success: function (data, status) {
													var subCategoryListObj = data.subCategories;
													var subCategoryList = subCategoryListObj[0].split(',');
													var radioDiv = document.getElementById("radiodiv");
													radioDiv.innerHTML = "";
													for (var i = 0; i < subCategoryList.length; i++) {
														var subcategory = subCategoryList[i];
														var radioOption = document.createElement("INPUT");
														radioOption.setAttribute("type", "radio");
														radioOption.setAttribute("value", subcategory);
														radioOption.setAttribute("name", "subcategory");
														var labelS = document.createElement("SPAN");
														labelS.innerHTML = subcategory;
														radioDiv.appendChild(radioOption);
														radioDiv.appendChild(labelS);
													}

												},
												error: function (status) {
												}
											});
										}


										function selectSubcategory() {
											var checkedRadio = $('input[name="subcategory"]:checked').val();
											if (null == checkedRadio) {
												document.getElementById("radioError").innerHTML = "Please select a subcategory";
												return false;
											}
											document.getElementById("radioError").innerHTML = "";
											var subCategoryDiv = document.getElementById("subcategorydiv");
											var subCategoryText = document.getElementById("subcategory");
											subCategoryText.value = checkedRadio;
											subCategoryDiv.style.display = "block";
											$('#popup').popup('hide');
											//validation for required field
										}
										function showSubcategory() {
											var subcategory = document.getElementById("subcategory").value;
											var subcategoryDiv = document.getElementById("subcategorydiv");
											if (subcategory == '' || subcategory == null) {

												subcategoryDiv.style.display = "none";
											}
											else {

												subcategoryDiv.style.display = "block";

											}
										}
										$(document).ready(function () {
											loadSubcategory();
											$("#subcategory").click(function () {
												$('#popup').popup({
													'blur': false,
													'escape': false
												}
												).popup('show');

											});
											showSubcategory();
										});

										$(function () {
											$("#userGroupId").change(function () {
												var groupId = this.value;
												var token = document.getElementsByName("token")[0].value;
												$.ajax({
													type: "POST",
													url: "roleByGroup",
													data: {
														groupId: groupId,
														token: token,
														"struts.token.name": "token"
													},
													success: function (data, status) {
														document.getElementById("roleIdDiv").innerHTML = data;
													},
													error: function (status) {
														//alert("Network error please try again later!!");
														return false;
													}
												});
											});
										});

										$(function () {
											$("#userGroupId").change(function () {
												var groupId = this.value;
												var token = document.getElementsByName("token")[0].value;
												$.ajax({
													type: "POST",
													url: "segmentByGroup",
													data: {
														groupId: groupId,
														token: token,
														"struts.token.name": "token"
													},
													success: function (data, status) {
														document.getElementById("segmentIdDiv").innerHTML = data;
													},
													error: function (status) {
														//alert("Network error please try again later!!");
														return false;
													}
												});
											});
										});
									</script>

<script>
										$("businessName").keypress(function (e) {
											$(this).val($(this).val().replace(' ', ''));
										});

										//Deepak
										$("aliasName").keypress(function (e) {
											$(this).val($(this).val().replace(' ', ''));
										});
									</script>

<style>
div#wwctrl_subcategory {
	margin-top: -8px;
}

input[type="radio"] {
	margin-left: 10px !important;
}

#radiodiv span {
	font-size: 14px;
	margin-left: 4px;
	color: black !important;
}

#radioError {
	color: #ff0000 !important;
	margin-left: 13px;
	font-size: 11px;
}

#subcategory {
	color: #555;
	font-weight: 600;
	margin-top: 15px;
	width: 100%;
}

/* .mainDiv .signuptextfield{
	height: 40px !important;
	font-size: 14px;
	width: 100% !important;
} */
.error-text {
	color: #a94442;
	font-weight: bold;
	background-color: #f2dede;
	list-style-type: none;
	text-align: center;
	list-style-type: none;
	margin-top: 10px;
}

.error-text li {
	list-style-type: none;
}

#response {
	color: green;
}

.mainDiv .adduR {
	width: 402px;
	padding: 2% 0;
	border-radius: 5px;
	background: #fafafa;
	border-color: #e6e6e6;
}

.mainDiv .adduTR {
	width: 90%;
	float: none;
	margin: 0 auto;
	font-weight: 600;
	margin-bottom: 12px;
}

.mainDiv .adduTR:last-child {
	margin-bottom: 0;
}

.mainDiv .signuptextfield, .signupdropdwn {
	font-family: 'Open Sans', sans-serif;
	font-weight: 600;
}

.inputTitle {
	color: #7a7a7a;
	font-weight: 600;
	display: none;
}

#subcategorydiv.adduTR {
	width: 100%;
}

.errorSec {
	color: red;
	text-indent: 2px;
	font-size: 11px;
	display: none;
}

@media only screen and (max-width: 768px) {
	/* For mobile phones: */
	#formname {
		width: 100% !important;
	}
}

/* .bmd-label-floating{
	font-size: 16px;
    font-weight: 500;
    color: grey;
} */
/* label{
	font-size: 14px;
    font-weight: 500;
    color: grey;
} */
/* .col-sm-6.col-lg-3{
	margin-bottom:10px;
} */
</style>
</head>

<body>
	<div class="content d-flex flex-column flex-column-fluid"
		id="kt_content">


		<div id="popup" style="display: none;">
			<div class="modal-dialog" style="width: 400px;">

				<!-- Modal content-->
				<div class="modal-content"
					style="background-color: transparent; -webkit-box-shadow: 0px 0px 0px 0px; -moz-box-shadow: 0px 0px 0px 0px; box-shadow: 0px 0px 0px 0px;">
					<div id="1" class="modal-body"
						style="background-color: #ffffff; -webkit-box-shadow: 0px 0px 0px 0px; -moz-box-shadow: 0px 0px 0px 0px; box-shadow: 0px 0px 0px 0px; box-shadow: 0px;">

						<table class="detailbox table98" cellpadding="20">
							<tr>
								<th colspan="2" width="16%" height="30" align="left"
									style="background-color: #202F4B; color: #ffffff; padding: 0px 0px 0px 8px; font-size: 14px;">
									Select sub category</th>
							</tr>
							<tr>
								<td colspan="2" height="30" align="left" style="padding: 7px;">
									<div id="radiodiv"></div>
								</td>
							</tr>
							<tr>
								<td colspan="4" height="30" align="left">
									<div id="radioError"></div>
								</td>
							</tr>
							<tr>
								<td colspan="2" style="padding: 7px;"><input type="submit"
									value="Done" onclick="selectSubcategory()"
									class="btn w-100 w-md-25 btn-primary "
									style="margin-left: 38%; width: 21%; height: 100%; margin-top: 1%;" />
								</td>
							</tr>
						</table>

					</div>
				</div>
			</div>
		</div>
		<!-- -->

		<div class="toolbar" id="kt_toolbar">
			<!--begin::Container-->
			<div id="kt_toolbar_container"
				class="container-fluid d-flex flex-stack">
				<!--begin::Page title-->
				<div data-kt-swapper="true" data-kt-swapper-mode="prepend"
					data-kt-swapper-parent="{default: '#kt_content_container', 'lg': '#kt_toolbar_container'}"
					class="page-title d-flex align-items-center me-3 flex-wrap mb-5 mb-lg-0 lh-1">
					<!--begin::Title-->
					<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">Reseller Registration</h1>
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
						<li class="breadcrumb-item text-dark">Reseller Registration</li>
						<!--end::Item-->
					</ul>
					<!--end::Breadcrumb-->
				</div>
				<!--end::Page title-->

			</div>
			<!--end::Container-->
		</div>

		<div class="row my-3 align-items-center">
			<div class="col-12 my-2 merchant-text">
				<s:if test="%{responseObject.responseCode=='000'}">
					<div id="saveMessage" align="center">
						<s:actionmessage class="success success-text"
							style="background-color: #57a93540;" />
					</div>

				</s:if>
				<s:else>
					<div class="error-text" align="center">
						<s:actionmessage theme="simple" />
					</div>
				</s:else>
			</div>
		</div>


		<s:form action="signupReseller" id="formname"
			onsubmit="return validateMyForm(event);">
			<s:token />
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
										style="font-size: 16px; color: #0271bb; font-weight: 500;">Create
										New Reseller</h4>
								</div>


								<div class="col-md-4">
									<!--begin::Label-->
									<label class="d-flex align-items-center fs-6 fw-semibold mb-2">
										<span class="required">Select Type</span>
									</label>

									<div class="txtnew">
										<s:select name="userRoleType" id="userRoleType"
											data-control="select2" headerKey="1"
											list="#{'SMA':'Create a SMA','MA':'Create a MA','Agent':'Create a Agent'}"
											class=" form-select form-select-solid">
										</s:select>
									</div>
								</div>

								<div class="col-md-4">
									<!--begin::Label-->
									<label class="d-flex align-items-center fs-6 fw-semibold mb-2">
										<span class="required">Segment</span>
									</label>

									<div class="txtnew">
										<s:select name="segmentName" data-control="select2"
											class="form-select form-select-solid" id="segmentName"
											headerKey="" headerValue="Select Segment" list="segments"
											listKey="segment" listValue="segment" autocomplete="off" />
									</div>
                                    <p class="errorSec errorSegment" >Please Select Segment</p>
								</div>
								<div id="tdIndustryType" class="col-md-4">
									<!--begin::Label-->
									<label class="d-flex align-items-center fs-6 fw-semibold mb-2">
										<span class="required">Industry Type</span>
									</label>
									<div class="txtnew">
										<s:select data-control="select2" name="industryCategory"
											id="industryCategory" headerKey=""
											headerValue="Select Category*" list="industryCategoryList"
											value="%{industryCategory}" onchange="categoryChange()"
											class="form-select form-select-solid" autocomplete="off">
										</s:select>
									</div>
									<p class="errorSec errorCategory">Please Select Category</p>
								</div>
								<div class="col-md-4 d-none subCategories">

									<div id="subcategorydiv" class="adduTR "
										style="display: none; margin-top: 2.5rem !important;"
										disabled="true">
										<s:textfield id="subcategory" name="industrySubCategory"
											value="%{industrySubCategory}"
											class="form-control form-control-solid"
											placeholder="Sub category" autocomplete="off" readonly="true"></s:textfield>
									</div>

								</div>


							</div>



							<div class="row g-3 mb-3">
								<div class="col-md-4" style="display: none;">
									<!--begin::Label-->
									<label class="d-flex align-items-center fs-6 fw-semibold mb-2">
										<span class="">User Group</span>
									</label>

									<div class="txtnew">
										<s:select name="userGroupId" data-control="select2"
											class="form-select form-select-solid" id="userGroupId"
											headerKey="" headerValue="Select User Group"
											list="userGroups" listKey="id" listValue="group"
											autocomplete="off" />
									</div>
								</div>
								<div class="form-group col-md-4" id="roleIdDiv"></div>
								<div class="form-group col-md-4 mt-4" id="segmentIdDiv"></div>


							</div>




							<div class="row g-3 mb-3">
								<div class="col-md-4">
									<!--begin::Label-->
									<label class="d-flex align-items-center fs-6 fw-semibold mb-2">
										<span class="required">Business Name</span>
									</label>
									<div class="txtnew" id="businessField">
										<s:textfield id="businessName" name="businessName"
											class="form-control form-control-solid"
											placeholder="Business Name*" autocomplete="off"
											maxlength="100" onblur="businessNameonBlur()"
											onKeyup="businessNameonBlur()" />
									</div>
									<p class="errorSec errorBusninessName">Please Enter Valid
										Business Name</p>
								</div>
								<!-- deepak -->
								<div class="col-md-4">
									<!--begin::Label-->
									<label class="d-flex align-items-center fs-6 fw-semibold mb-2">
										<span class="required">Alias/DBA Name</span>
									</label>

									<div class="txtnew" id="businessField">
										<s:textfield id="aliasName" name="aliasName"
											class="form-control form-control-solid"
											placeholder="Alias/DBA Name*" autocomplete="off"
											maxlength="100" onblur="aliasNameonBlur()"
											onKeyup="aliasNameonBlur()" />
									</div>
									<p class="errorSec errorAliasName">Please Enter Valid
										Alias/DBA Name</p>
								</div>
								<div class="col-md-4">
									<!--begin::Label-->
									<label class="d-flex align-items-center fs-6 fw-semibold mb-2">
										<span class="required">Email</span>
									</label>
									<div class="txtnew" id="businessField">
										<s:textfield id="emailId" name="emailId"
											class="form-control form-control-solid" placeholder="Email*"
											automcomplete="off" maxlength="50" onblur="emailOnBlur()"
											onkeypress="emailOnBlur()" onkeyup="emailOnBlur()" />
									</div>
									<p class="errorSec errorEmail">Please Enter Valid Email</p>
								</div>
							</div>


							<div class="row g-5 mb-5">
								<div class="col-md-4">
									<!--begin::Label-->
									<label class="d-flex align-items-center fs-6 fw-semibold mb-2">
										<span class="required">Contact</span>
									</label>
									<div class="txtnew">
										<%-- <s:textfield id="mobile" onkeydown="return numOnlyInTextInput(event);"
													maxlength="10" name="mobile" class="form-control form-control-solid"
													placeholder="Phone" automcomplete="false" onblur="phoneNoOnBlur()"
													onkeypress="phoneNoOnBlur()" /> --%>

										<s:textfield id="mobile" maxlength="100" name="mobile"
											class="form-control form-control-solid" automcomplete="false" />


									</div>
									<p class="errorSec errorPhone" id="errorPhone">Please Enter
										Valid Contact</p>
								</div>
								<div class="col-md-4" style="display: none;">
									<label class="d-flex align-items-center fs-6 fw-semibold mb-2">
										<span class="required">Password</span>
									</label>
									<div class="txtnew">
										<s:textfield id="password" name="password" type="password"
											class="form-control form-control-solid"
											placeholder="Password*" onblur="passwordOnBlur()"
											onkeypress="passwordOnBlur()" maxlength="32"
											automcomplete="false" />
									</div>
									<p class="errorSec errorPassword">Please Enter Valid
										Password</p>
								</div>
								<div class="col-md-4" style="display: none;">
									<!--begin::Label-->
									<label class="d-flex align-items-center fs-6 fw-semibold mb-2">
										<span class="required">Confirm Password</span>
									</label>
									<div class="txtnew">
										<s:textfield id="confirmPassword" name="confirmPassword"
											type="password" class="form-control form-control-solid"
											placeholder="Confirm Password*"
											onblur="confirmPasswordBlur()"
											onKeyup="confirmPasswordBlur()" automcomplete="false"
											maxlength="32" />
									</div>
									<p class="errorSec passwordNotMatch">Password Doesn't Match</p>
								</div>
							</div>


						</div>
						<div class="card-footer text-right">
							<s:submit value="Sign Up" method="submit" id="addMerchantBtn"
								class="btn w-100 w-md-25 btn-primary disabled"
								style="padding: 11px;
						font-size: 14px;">
							</s:submit>

						</div>




					</div>
				</div>
			</div>



			<s:hidden name="response" id="response"
				value="%{responseObject.responseCode}" />
		</s:form>

	</div>


	<script>
				$(document).ready(function () {

					var fields = {

						password: {
							tooltip: "Password must be minimum 8 and <br> maximum 32 characters long, with <br> special characters (! @ , _ + / =) , <br> at least one uppercase, one <br>lower case alphabet and one <br>numeric number.",
							position: 'right',
							backgroundColor: "rgb(248 214 142)",
							color: '#FFFFFF'
						},
						//confirmPassword : {
						//tooltip: "Password must be minimum 8 and <br> maximum 32 characters long, with <br> special characters (! @ , _ + / =) , <br> at least one uppercase, one <br>lower case alphabet and one <br>numeric number.",
						//position: 'right',
						//backgroundColor: "#6ad0f6",
						//color: '#FFFFFF'
						//}
					};

					//Include Global Color
					$("#formname").formtoolip(fields, { backgroundColor: "#000000", color: "#FFFFFF", fontSize: 14, padding: 10, borderRadius: 5 });

				});
			</script>


	<script type="text/javascript">
				var industryCategory = document.getElementById('industryCategory'),
					errorCategory = document.getElementsByClassName('errorCategory')[0],
					businessName = document.getElementById('businessName'),
					aliasName = document.getElementById('aliasName'),//Deepak
					errorBusninessName = document.getElementsByClassName('errorBusninessName')[0],
					errorAliasName=document.getElementsByClassName('errorAliasName')[0],//Deepak
					emailId = document.getElementById('emailId'),
					errorEmail = document.getElementsByClassName('errorEmail')[0],
					mobileRegex = /^[789]\d{9}/,
					mobile = document.getElementById('mobile'),
					errorPhone = document.getElementsByClassName('errorPhone')[0],
					passwordRejex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!@,_+/=]).{8,32}$/g,
					password = document.getElementById('password'),
					errorPassword = document.getElementsByClassName('errorPassword')[0],
					confirmPassword = document.getElementById('confirmPassword'),
					passwordNotMatch = document.getElementsByClassName('passwordNotMatch')[0];


				function validateMyForm(e) {
					debugger
					var checkCategory = false,
						checkBusinessName = false,
						checkAliasName = false,//Deepak
						checkEmailId = false,
						checkPhone = false,
						checkPassword = false,
						checkSegment = false,
						checkConfirmPass = false,
						mobileVal = mobile.value.trim(),
						passwordVal = password.value.trim(),
						userType = document.getElementById('userRoleType').value.trim();

					/* if (userType == "merchant") {
						if (!industryCategory.value.trim()) {
							errorCategory.style.display = "block";
							checkCategory = false;
							e.preventDefault();
						} else {
							errorCategory.style.display = "none";
							checkCategory = true;
						}
					} */

					if (!businessName.value.trim() || businessName.value.trim().length < 2) {
						errorBusninessName.style.display = "block";
						checkBusinessName = false;
						e.preventDefault();
					} else {
						errorBusninessName.style.display = "none";
						checkBusinessName = true;
					}

					if (!aliasName.value.trim() || aliasName.value.trim().length < 2) {
						errorAliasName.style.display = "block";
						checkAliasName = false;
						e.preventDefault();
					} else {
						errorAliasName.style.display = "none";
						checkAliasName = true;
					}

                    segment = document.getElementById('segmentName');
                    errorSegment = document.getElementsByClassName('errorSegment')[0];

                    console.log(segment.value.trim());
                    if(segment.value.trim() == ""){
                        errorSegment.style.display = "block";
                        checkSegment = true;
						e.preventDefault();
                    }else{

                        errorSegment.style.display = "none";
                        checkSegment = false;
                    }


					/* if(!aliasName.value || aliasName.value.length<2){
						errorAliasName.style.display = "block";
						checkAliasName = false;
						e.preventDefault();
					}else{
						errorAliasName.style.display = "none";
						checkAliasName = true;
					} */

					if (!emailId.value.trim() || emailId.value.trim().length < 6 || !isValidEmail('emailId')) {
						errorEmail.style.display = "block";
						checkEmailId = false;
						e.preventDefault();
					} else {
						errorEmail.style.display = "none";
						checkEmailId = true;
					}

					if (mobileVal=="" || mobileVal.length < 2) {
						errorPhone.style.display = "block";
						checkPhone = false;
						e.preventDefault();
					} else {
						errorPhone.style.display = "none";
						checkPhone = true;
					}

			/* 		if (!passwordVal.match(passwordRejex)) {
						errorPassword.style.display = "block";
						checkPassword = false;
						//e.preventDefault();
					} else {
						errorPassword.style.display = "none";
						checkPassword = true;
					}

					if (password.value) {
						if (password.value != confirmPassword.value || !confirmPassword.value) {
							passwordNotMatch.style.display = "block";
							checkConfirmPass = false;
							e.preventDefault();
						} else {
							passwordNotMatch.style.display = "none";
							checkConfirmPass = true;
						}
					} */

					//var pass=CryptoJS.AES.encrypt(password.value, 'thisisaverysecretkey');
					//var confPass=CryptoJS.AES.encrypt(confirmPassword.value, 'thisisaverysecretkey');
					//document.getElementById('password').value=pass;
					//document.getElementById('confirmPassword').value=confPass;


					/*if(checkCategory && checkBusinessName && checkEmailId && checkPhone && checkPassword && checkConfirmPass && checkSegment){
						document.getElementById('formname').submit();
					}*/


				}

				function categoryChange() {
debugger

					if (industryCategory.value) {

						errorCategory.style.display = "none";
					} else {

						errorCategory.style.display = "block";

					}
					$(".subCategories").removeClass("d-none");
				}
				function businessNameonBlur() {
					if ((businessName.value.trim()).length > 0 && (businessName.value.trim()).length < 1) {
						errorBusninessName.style.display = "block";
					} else {
						errorBusninessName.style.display = "none";
						document.getElementById("wwerr_businessName").style.display = "none";
					}
				}

				function aliasNameonBlur() {
					if ((aliasName.value.trim()).length > 0 && (aliasName.value.trim()).length < 1) {
						errorAliasName.style.display = "block";
					} else {
						errorAliasName.style.display = "none";
						document.getElementById("wwerr_businessName").style.display = "none";
					}
				}

				function emailOnBlur() {
					if (!emailId.value || emailId.value.length < 6 || !isValidEmail('emailId')) {
						errorEmail.style.display = "block";
					} else {
						errorEmail.style.display = "none";
					}
				}

				function phoneNoOnBlur() {
					var mobileVal = document.getElementById("mobile").value.trim();
					if (mobileVal!="" && !mobileVal.length < 2) {
                        errorPhone.style.display = "block";
                        checkPhone = false;
                    } else {
                        errorPhone.style.display = "none";
                        checkPhone = true;
                    }
				}

				function passwordOnBlur() {
					var passwordVal = password.value;
					if (!passwordVal.match(passwordRejex)) {
						errorPassword.style.display = "block";
					} else {
						errorPassword.style.display = "none";
					}
				}

				function confirmPasswordBlur() {
					if (password.value) {
						if (password.value != confirmPassword.value || !confirmPassword.value) {
							passwordNotMatch.style.display = "block";
						} else {
							passwordNotMatch.style.display = "none";
						}
					}
				}

				function isValidEmail(inputId){
					var emailexp = /^[_A-Za-z0-9-]+(\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\.[A-Za-z0-9-]+)*(\.[_A-Za-z0-9-]+)$/;
					var emailElement = document.getElementById(inputId);
					var emailValue = emailElement.value;
					if (emailValue.trim() !== "") {
						if (emailValue.match(emailexp)) {
							return true;
						} else {
							return false;
						}
					} else {
						return false;
					}
				}

				$(document).ready(function () {
					if ($('#resellerCrmSignup').hasClass("active")) {
						var menuAccess = document.getElementById("menuAccessByROLE").value;
						var accessMap = JSON.parse(menuAccess);
						var access = accessMap["resellerCrmSignup"];
						if (access.includes("Add")) {
							$("#addMerchantBtn").removeClass("disabled");
						}
					}
				});
				
			</script>

	<script src="../assets/plugins/custom/datatables/datatables.bundle.js"></script>

</body>

</html>