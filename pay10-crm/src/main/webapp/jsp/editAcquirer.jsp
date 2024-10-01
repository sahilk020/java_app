<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
	<%@ taglib prefix="s" uri="/struts-tags" %>
		<%@taglib prefix="s" uri="/struts-tags" %>
			<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
			<html>

			<head>
				<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
				<title>Modify Acquirer</title>


				<link rel="shortcut icon" href="assets/media/images/paylogo.svg" />
				<!--begin::Fonts-->
				<link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Inter:300,400,500,600,700" />
				<!--end::Fonts-->
				<!--begin::Vendor Stylesheets(used by this page)-->
				<link href="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.css" rel="stylesheet"
					type="text/css" />
				<link href="../assets/plugins/custom/datatables/datatables.bundle.css" rel="stylesheet"
					type="text/css" />

				<!--end::Vendor Stylesheets-->
				<!--begin::Global Stylesheets Bundle(used by all pages)-->
				<link href="../assets/plugins/global/plugins.bundle.css" rel="stylesheet" type="text/css" />
				<link href="../assets/css/style.bundle.css" rel="stylesheet" type="text/css" />
				<link href="../assets/css/custom/custom-style.css" rel="stylesheet" type="text/css" />

				<script src="../assets/plugins/global/plugins.bundle.js"></script>
				<script src="../assets/js/scripts.bundle.js"></script>
				<link href="../css/select2.min.css" rel="stylesheet" />
				<script src="../js/jquery.select2.js" type="text/javascript"></script>


				<script language="JavaScript">




				</script>
				<script type="text/javascript">

					$(document).ready(function () {
						var actionMessage = $("#saveMessage").text().trim();
						console.log(actionMessage);

						if (actionMessage) {
							if (actionMessage == "Acquirer details updated successfully") {
								Swal.fire({
									text: "Acquirer updated successfully",
									icon: "success",
									timer: 1000,
									showCancelButton: false,
									showConfirmButton: false

								}).then(function () {
									window.location.href = "searchAcquirer";
								});
							}
						}
						$('#btnEditUser').click(function () {
							console.log("clicked");
							validation();
							if (!validation()) {
								console.log("submit failed");
								return false;
							}
							debugger;
							var answer = confirm("Are you sure you want to edit acquirer details?");
							if (answer != true) {
								return false;
							} else {

								document.getElementById("frmEditUser").submit();
							}

							/* $('#loader-wrapper').show(); */
						});


					});
					function firstNameValidation() {
						//console.log("test "+$("#firstName").val());
						var name = $("#firstName").val();
						if (!(/^[a-zA-Z]+$/).test(name)) {
							//	console.log("Invalid");
							$("#firstNameError").show();

							return false;
						}
						else {
							$("#firstNameError").hide();
							return true;
						}
					}
					function lastNameValidation() {
						var lastName = $("#lastName").val();
						if (!(/^[a-zA-Z]+$/).test(lastName)) {
							$("#lastNameError").show();
							return false;
						}
						else {
							$("#lastNameError").hide();
							return true;
						}
					}
					function businessNameValidation() {
						var businessName = $("#businessName").val();
						if (businessName == null || businessName == "") {
							$("#businessNameError").show();
							//businessName.focus();
							return false;
						}
						else {
							$("#businessNameError").hide();
							return true;
						}
					}
					function roleIdValidation() {
						$(".errorSec").hide();
						if ($("#roleId").val() == null || $("#roleId").val() == "") {
							$("#roleError").show();

							return false;
						}
						else {
							$("#roleError").hide();
							return true;
						}
					}
					function userGroupValidation() {
						console.log("user group validation");
						console.log($("#userGroupId").val())
						if ($("#userGroupId").val() == null || $("#userGroupId").val() == "") {
							$("#userGroupError").show();
							console.log("user group return")
							return false;
						}
						else {
							$("#userGroupError").hide();
							return true;
						}
					}
					function validation() {
						console.log("Testing");
						if (!userGroupValidation() || !firstNameValidation() || !lastNameValidation() || !businessNameValidation() || !roleIdValidation()) {
							console.log("Validation false");
							return false;
						}
						else {
							return true;
						}

					}

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

				</script>

				<style>
					.btn:focus {
						outline: 0 !important;
					}
				</style>

			</head>

			<body>
				<div class="content d-flex flex-column flex-column-fluid" id="kt_content">

					<div class="toolbar" id="kt_toolbar">
						<!--begin::Container-->
						<div id="kt_toolbar_container" class="container-fluid d-flex flex-stack">
							<!--begin::Page title-->
							<div data-kt-swapper="true" data-kt-swapper-mode="prepend"
								data-kt-swapper-parent="{default: '#kt_content_container', 'lg': '#kt_toolbar_container'}"
								class="page-title d-flex align-items-center me-3 flex-wrap mb-5 mb-lg-0 lh-1">
								<!--begin::Title-->
								<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">Edit Acquirer</h1>
								<!--end::Title-->
								<!--begin::Separator-->
								<span class="h-20px border-gray-200 border-start mx-4"></span>
								<!--end::Separator-->
								<!--begin::Breadcrumb-->
								<ul class="breadcrumb breadcrumb-separatorless fw-semibold fs-7 my-1">
									<!--begin::Item-->
									<li class="breadcrumb-item text-muted">
										<a href="home" class="text-muted text-hover-primary">Dashboard</a>
									</li>
									<!--end::Item-->
									<!--begin::Item-->
									<li class="breadcrumb-item">
										<span class="bullet bg-gray-200 w-5px h-2px"></span>
									</li>
									<!--end::Item-->
									<!--begin::Item-->
									<li class="breadcrumb-item text-muted">Manage Acquirers</li>
									<!--end::Item-->
									<!--begin::Item-->
									<li class="breadcrumb-item">
										<span class="bullet bg-gray-200 w-5px h-2px"></span>
									</li>
									<!--end::Item-->
									<!--begin::Item-->
									<li class="breadcrumb-item text-muted">Acquirer List</li>
									<li class="breadcrumb-item">
										<span class="bullet bg-gray-200 w-5px h-2px"></span>
									</li>
									<li class="breadcrumb-item text-dark">Edit Acquirer</li>
									<!--end::Item-->
								</ul>
								<!--end::Breadcrumb-->
							</div>
							<!--end::Page title-->

						</div>
						<!--end::Container-->
					</div>

					<!--  <div id="loader-wrapper" style="width: 100%; height: 100%; display:none;">
	  <div id="loader"></div>
    </div> -->

					<table width="100%" border="0" cellspacing="0" cellpadding="0" class="txnf">
						<!-- <tr>
			<td align="left"><h2>Edit Acquirer</h2></td>
		</tr> -->
						<tr>
							<td align="left" valign="top">
								<div id="saveMessage">
									<s:actionmessage class="success success-text" id="actionMessageId" />
								</div>
							</td>
						</tr>

						<tr>
							<td align="left" valign="top">
								<div class="addu">
									<s:form action="editAcquirerDetails" id="frmEditUser">
										<div class="card ">
											<!-- <div class="card-header card-header-rose card-header-icon">
							  <div class="card-icon">
								<i class="material-icons">mail_outline</i>
							  </div>
							  <h4 class="card-title" style="
							  color: #0271bb;
							  font-weight: 500;">Edit Acquirer</h4>
							</div> -->

											<div class="post d-flex flex-column-fluid" id="kt_post">
												<div id="kt_content_container" class="container-xxl">

													<div class="row my-5">
														<div class="col">
															<div class="card">
																<div class="card-body ">
																	<div class="row g-9 mb-8 justify-content-center">
																		<div class="col-6">
																			<div class="adduT">
																				<label
																					class="d-flex align-items-center fs-6 fw-semibold mb-2">First
																					Name<span
																						style="color:red; margin-left:3px;">*</span></label><br>
																				<!-- First Name<span style="color:red; margin-left:3px;">*</span><br> -->
																				<div class="txtnew">
																					<s:textfield name="firstName"
																						id="firstName"
																						onkeyup="firstNameValidation()"
																						class="  form-control form-control-solid"
																						maxlength="20"
																						autocomplete="off" />
																				</div>
																			</div>
																			<span id="firstNameError"
																				style="color:red; display:none;">Enter
																				valid First Name</span>

																			<br>
																			<div class="adduT">
																				<label
																					class="d-flex align-items-center fs-6 fw-semibold mb-2">Last
																					Name<span
																						style="color:red; margin-left:3px;">*</span></label><br>
																				<!-- Last Name<span style="color:red; margin-left:3px;">*</span><br> -->
																				<div class="txtnew">
																					<s:textfield name="lastName"
																						id="lastName"
																						class="  form-control form-control-solid"
																						maxlength="20"
																						autocomplete="off"
																						onkeyup="lastNameValidation()" />
																				</div>
																				<span id="lastNameError"
																					style="color:red; display:none;">Enter
																					valid Last Name</span>
																			</div>
																			<br>
																			<div class="adduT">
																				<label
																					class="d-flex align-items-center fs-6 fw-semibold mb-2">Business
																					Name<span
																						style="color:red; margin-left:3px;">*</span></label><br>
																				<!-- Business Name<span style="color:red; margin-left:3px;">*</span><br> -->
																				<div class="txtnew">
																					<s:textfield name="businessName"
																						id="businessName"
																						class="  form-control form-control-solid"
																						maxlength="20"
																						autocomplete="off" />
																				</div>
																				<span id="businessNameError"
																					style="color:red; display:none;">Enter
																					valid Business Name</span>
																			</div>
																			<br>
																			<div class="adduT" style="display: none;">
																				<label
																					class="d-flex align-items-center fs-6 fw-semibold mb-2">Email<span
																						style="color:red; margin-left:3px;">*</span></label><br>
																				<!-- Email<span style="color:red; margin-left:3px;">*</span><br> -->
																				<div class="txtnew">
																					<s:hidden name="emailId"
																						id="emailAddress"
																						onkeyup="emailValidation()"
																						class="  form-control form-control-solid"
																						autocomplete="off"
																						readonly="true" />
																				</div>
																				<span id="emailAddressError"
																					style="color:red;">Enter valid Email
																					ID</span>
																			</div>

																			<br>


																			<div class="adduT">
																				<label
																					class="d-flex align-items-center fs-6 fw-semibold mb-2">User
																					Group<span
																						style="color:red; margin-left:3px;">*</span></label>
																				<div class="txtnew">
																					<s:select name="userGroupId"
																						class="form-select form-select-solid"
																						id="userGroupId" headerKey=""
																						headerValue="Select User Group"
																						list="userGroups" listKey="id"
																						listValue="group"
																						autocomplete="off" />
																				</div>
																				<span id="userGroupError"
																					style="color:red; display:none;">Please
																					Select User Group</span>
																			</div>
																			<br>
																			<div class="adduT" id="roleIdDiv">
																				<label
																					class="d-flex align-items-center fs-6 fw-semibold mb-2">Role<span
																						style="color:red; margin-left:3px;">*</span></label>
																				<div class="txtnew">
																					<s:select name="roleId"
																						class="form-select form-select-solid"
																						id="roleId" headerKey=""
																						headerValue="Select Role"
																						list="roles" listKey="id"
																						listValue="roleName"
																						autocomplete="off" />
																				</div>
																				<span id="roleError"
																					style="color:red; display:none;">Please
																					Select Role</span>

																			</div>

																			<div class="adduT"
																				style="padding-top: 50px; ">
																				<s:submit id="btnEditUser"
																					name="btnEditUser" value="Save"
																					type="button"
																					cssClass="btn btn-primary btn-md">
																				</s:submit>
																			</div>
																		</div>
																	</div>
																</div>
															</div>
														</div>
													</div>
												</div>
											</div>
											<div class="card-footer text-right">

											</div>
										</div>
										<s:hidden name="payId" id="payId" />
										<s:hidden name="token" value="%{#session.customToken}"></s:hidden>
									</s:form>
									<div class="clear"></div>
								</div>
							</td>


						</tr>
						<tr>
							<td align="left" valign="top">&nbsp;</td>
						</tr>
					</table>
				</div>
				<script type="text/javascript">


					$(document).ready(function () {

						$("#userGroupId").select2();
						$("#roleId").select2();
					});
				</script>
			</body>

			</html>