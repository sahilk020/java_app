<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Modify User</title>
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

<script type="text/javascript">
	$(document)
			.ready(
					function() {
						var google2FAKeyFlag = "<s:property value='%{google2FAKey}'/>";

						if (google2FAKeyFlag == "true") {
							console.log("api key true");
							console.log(google2FAKeyFlag);
							$("#resetTFA").attr("disabled", true);
						} else {
							console.log("api key false");
							$("#resetTFA").attr("disabled", false);
						}
						console.log("Flag " + google2FAKeyFlag);

						$("#roleId").select2();
						$("#userGroupId").select2();

						$('#btnEditUser')
								.click(
										function() {
											debugger
											var fName = document
													.getElementById("firstName").value;
											var lName = document
													.getElementById("lastName").value;
											if (fName == null || fName == '') {
												document
														.getElementById("firstName-error").innerHTML = "Please provide first name";
												document.getElementById(
														"firstName").focus();
												return false;
											}
											if (fName.length < 2) {
												document
														.getElementById("firstName-error").innerHTML = "First name should be atleast of 2 characters.";
												document.getElementById(
														"firstName").focus();
												return false;
											} else {
												document
														.getElementById("firstName-error").innerHTML = "";
											}

											if (lName == null || lName == '') {
												document
														.getElementById("lastName-error").innerHTML = "Please provide last name";
												document.getElementById(
														"lastName").focus();
												return false;
											}
											if (lName.length < 2) {
												document
														.getElementById("lastName-error").innerHTML = "Last name should be atleast of 2 characters";
												document.getElementById(
														"lastName").focus();
												return false;
											} else {
												document
														.getElementById("lastName-error").innerHTML = "";
											}
											const regex = /^[0-9()-_]+$/;
											var mobile = document
													.getElementById("mobile").value;
											if (mobile == null
													|| mobile.trim() === '') {
												document
														.getElementById("mobile-error").innerHTML = "Please provide a phone number";
												document.getElementById(
														"mobile").focus();
												return false;
											} else if (!regex.test(mobile)) {
												document
														.getElementById("mobile-error").innerHTML = "Please provide a valid phone number";
												document.getElementById(
														"mobile").focus();
												return false;
											} else if (mobile.replace(
													/[^0-9]/g, '').length < 10
													|| mobile.replace(
															/[^0-9]/g, '').length > 15) {
												document
														.getElementById("mobile-error").innerHTML = "Phone number must be between 10 to 15 digits";
												document.getElementById(
														"mobile").focus();
												return false;
											} else {
												document
														.getElementById("mobile-error").innerHTML = "";
											}

											var needToShowAcqFields = document
													.getElementById("needToShowAcqFieldsInReport").checked;
											$("#needToShowAcqFieldsInReport")
													.val(needToShowAcqFields);

											var groupId = document
													.getElementById("userGroupId").value;
											if (groupId == null
													|| groupId == '') {
												document
														.getElementById("userGroupId-error").innerHTML = "Please select the group";
												document.getElementById(
														"userGroupId").focus();
												return false;
											} else {
												document
														.getElementById("userGroupId-error").innerHTML = "";
											}
											var roleId = document
													.getElementById("roleId").value;
											if (roleId == null || roleId == '') {
												document
														.getElementById("roleId-error").innerHTML = "Please select the role";
												document.getElementById(
														"roleId").focus();
												return false;
											} else {
												document
														.getElementById("roleId-error").innerHTML = "";
											}
											var answer = confirm("Do you want to Update Sub Admin Permission ?");
											if (answer != true) {
												return false;
											} else {
												$('#loader-wrapper').show();
												document.getElementById(
														"frmEditUser").submit();
											}

										});
					});
	function resetTFAuthentication() {
		//	var payIdData=$("#payId").val();

		var emailIdData = $("#emailAddress").val();
		var token = document.getElementsByName("token")[0].value;
		$.ajax({
			type : "POST",
			url : "merchantResetTFA",
			data : {
				emailId : emailIdData,
				token : token,
				"struts.token.name" : "token"
			},
			success : function(response) {

				$("#resetTFA").attr('disabled', true);
				$("#tfaFlag").prop('checked', true);
				console.log(response);

				Swal.fire({
					icon : "success",
					title : "2FA Reset Succesfully Done",
					showConfirmButton : false,
					timer : 1500
				});
			},
			error : function(response) {
				Swal.fire({
					icon : "error",
					title : "Oops...",
					text : "2FA Reset Failed",
					showConfirmButton : false,
					timer : 1500
				});
				console.log(response);
			}

		});
	}
	$(function() {
		$("#userGroupId").change(function() {
			var groupId = this.value;
			var token = document.getElementsByName("token")[0].value;
			$.ajax({
				type : "POST",
				url : "roleByGroup",
				data : {
					groupId : groupId,
					token : token,
					"struts.token.name" : "token"
				},
				success : function(data, status) {
					document.getElementById("roleIdDiv").innerHTML = data;
				},
				error : function(status) {
					//alert("Network error please try again later!!");
					return false;
				}
			});
		});
	});
</script>

<style>
/* button#btnEditUser {
	margin-right: 63px;
} */
.btn:focus {
	outline: 0 !important;
}
</style>

</head>

<body>
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
					<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">Edit
						Sub-admin</h1>
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
						<li class="breadcrumb-item text-muted">Manage Users</li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item"><span
							class="bullet bg-gray-200 w-5px h-2px"></span></li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item text-dark">Edit Sub-admin</li>
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
			<div id="kt_content_container" class="container-xxl">
				<div id="saveMessage" class="row" style="text-align: center;">
					<s:actionmessage class="success success-text" />
				</div>
				<div class="row my-5">
					<div class="col">
						<div class="card">
							<div class="card-body">
								<div class="addu">
									<!--begin::Container-->
									<s:form action="editAgentDetails" id="frmEditUser">
										<div class="row">
											<div class="col-md-4 fv-row">
												<label
													class="d-flex align-items-center fs-6 fw-semibold mb-2">
													First Name</label>
												<s:textfield name="firstName" id="firstName"
													class="form-control form-control-solid" maxlength="50"
													autocomplete="off"
													onkeypress="noSpace(event,this);return isCharacterKey(event);" />

												<em id="firstName-error" class="error invalid-feedback"
													style="display: inline; color: red;"></em>
											</div>

											<div class="col-md-4 fv-row">
												<label
													class="d-flex align-items-center fs-6 fw-semibold mb-2">
													Last Name</label>
												<s:textfield name="lastName" id="lastName"
													class="form-control form-control-solid" maxlength="50"
													autocomplete="off"
													onkeypress="noSpace(event,this);return isCharacterKey(event);" />

												<em id="lastName-error" class="error invalid-feedback"
													style="display: inline; color: red;"></em>
											</div>

											<div class="col-md-4 fv-row">
												<label
													class="d-flex align-items-center fs-6 fw-semibold mb-2">Phone<span
													style="color: red; margin-left: 3px;">*</span>
												</label>

												<div class="txtnew">
													<s:textfield name="mobile" id="mobile"
														class="signuptextfield form-control form-control-solid"
														onkeypress="javascript:return isNumber (event)"
														maxlength="15" minlength="10" autocomplete="off" />
													<em id="mobile-error" class="error invalid-feedback"
														style="display: inline;"> </em>
												</div>
											</div>
										</div>

										<br>
										<br>
										<div class="row">
											<div class="col-md-4 fv-row">
												<label
													class="d-flex align-items-center fs-6 fw-semibold mb-2">Email<span
													style="color: red; margin-left: 3px;">*</span></label>
												<div class="txtnew">
													<s:textfield name="emailId" id="emailAddress"
														class="signuptextfield form-control form-control-solid"
														autocomplete="off" readonly="true" />
												</div>
											</div>
											<div class="col-md-4 fv-row">
												<label
													class="d-flex align-items-center fs-6 fw-semibold mb-2">User
													Group</label>
												<div class="txtnew">
													<s:select name="userGroupId"
														class="form-select form-select-solid" id="userGroupId"
														headerKey="" headerValue="Select User Group"
														list="userGroups" listKey="id" listValue="group"
														autocomplete="off" />
													<em id="userGroupId-error" class="error invalid-feedback"
														style="display: inline;"></em>
												</div>
											</div>
											<div class="col-md-4 fv-row" id="roleIdDiv">
												<label
													class="d-flex align-items-center fs-6 fw-semibold mb-2">Role</label>
												<div class="txtnew">
													<s:select name="roleId"
														class="form-select form-select-solid" id="roleId"
														headerKey="" headerValue="" list="roles" listKey="id"
														listValue="roleName" autocomplete="off" />
													<em id="roleId-error" class="error invalid-feedback"
														style="display: inline;"></em>
												</div>
											</div>
											<div class="col-md-4 fv-row" style="margin-top: 20px;">
												<label class="bmd-label-floating"> Need To Show
													Acquirer fields in report</label>
												<s:checkbox name="needToShowAcqFieldsInReport"
													id="needToShowAcqFieldsInReport" />
												<em id="needToShowAcqFieldsInReport-error"
													class="error invalid-feedback" style="display: inline;"></em>
											</div>

											<div class="col-md-4 fv-row" style="margin-top: 20px;">
												<label
													class="d-flex align-items-center fs-6 fw-semibold mb-2">Is
													Active ?</label>
												<s:checkbox name="isActive" id="isActive" />
												<label class="labelfont" for="1"></label>

											</div>
											<div class="col-md-4 fv-row" style="margin-top: 20px;">
												<label
													class="d-flex align-items-center fs-6 fw-semibold mb-2">
													2FA Enable</label>
												<s:checkbox name="tfaFlag" id="tfaFlag" />
												<label class="labelfont" for="1" value="%{user.tfaFlag}"></label>
											</div>
											<div class="col-md-4 fv-row">
												<label
													class="d-flex align-items-center fs-6 fw-semibold mb-2">
													2FA Reset</label>
											</div>
										</div>

										<div class="row" style="margin-top: 30px; float: left;">
											<div class="col">
												<button id="resetTFA" class="btn btn-primary" type="button"
													onclick="resetTFAuthentication()">Reset</button>
											</div>

											<div class="col">
												<s:submit id="btnEditUser" name="btnEditUser" value="Save"
													type="button" class="btn btn-primary">
												</s:submit>
											</div>
											<div class="col">
												<button id="cancel" class="btn btn-primary" type="button">Cancel</button>
											</div>
										</div>

										<s:hidden name="payId" id="payId" />
										<s:hidden name="token" value="%{#session.customToken}">
										</s:hidden>

									</s:form>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>

		</div>
	</div>
	<script>
		document.getElementById('cancel').addEventListener('click', function() {

			window.location.href = 'searchSubAdmin';
		});
	</script>


</body>

</html>