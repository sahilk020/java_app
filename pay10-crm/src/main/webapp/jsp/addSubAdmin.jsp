<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Add User</title>

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

<script language="JavaScript">
	$(document)
			.ready(
					function() {

						$("#userGroupId").select2();

						$('#btnEditUser')
								.click(
										function() {

											var fName = document
													.getElementById("fname").value;
											var lName = document
													.getElementById("lname").value;
											if (fName == null || fName == '') {
												document
														.getElementById("fname-error").innerHTML = "Please provide first name";
												document
														.getElementById("fname")
														.focus();
												return false;
											} if(fName.length < 2 ){
												document
														.getElementById("fname-error").innerHTML = "First name should be atleast of 2 characters.";
												document
														.getElementById("fname")
														.focus();
												return false;

											}else {
												document
														.getElementById("fname-error").innerHTML = "";
											}

											if (lName == null || lName == '' ) {
												document
														.getElementById("lname-error").innerHTML = "Please provide last name";
												document
														.getElementById("lname")
														.focus();
												return false;
											} if(lName.length < 2){

												document
														.getElementById("lname-error").innerHTML = "Last name should be atleast of 2 characters.";
												document
														.getElementById("lname")
														.focus();
												return false;
												
											}else {
												document
														.getElementById("lname-error").innerHTML = "";
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
											var email = document
													.getElementById("emailId").value;
											if (!/^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/
													.test(email)) {
												document
														.getElementById("emailId-error").innerHTML = "Please provide valid emailId";
												document.getElementById(
														"emailId").focus();
												return false;
											} else {
												document
														.getElementById("emailId-error").innerHTML = "";
											}
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
											var segmentName = document
													.getElementById("segmentName").value;
											if (segmentName == null
													|| segmentName == '') {
												document
														.getElementById("segmentName-error").innerHTML = "Please select the segment";
												document.getElementById(
														"segmentName").focus();
												return false;
											} else {
												document
														.getElementById("segmentName-error").innerHTML = "";
											}
											var answer = confirm("Do you want to create Sub-admin ?");
											if (answer != true) {
												return false;
											} else {
												$('#loader-wrapper').show();
												document.getElementById(
														"frmEditUser").submit();
											}

										});
					});

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

	$(function() {
		$("#userGroupId").change(function() {
			var groupId = this.value;
			var token = document.getElementsByName("token")[0].value;
			$.ajax({
				type : "POST",
				url : "segmentByGroup",
				data : {
					groupId : groupId,
					token : token,
					"struts.token.name" : "token"
				},
				success : function(data, status) {
					document.getElementById("segmentIdDiv").innerHTML = data;
				},
				error : function(status) {
					//alert("Network error please try again later!!");
					return false;
				}
			});
		});
	});
</script>
<style type="text/css">
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

.btn:focus {
	outline: 0 !important;
}
</style>
</head>

<body class="bodyColor post d-flex flex-column-fluid">


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
				<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">Add
					Sub-Admin</h1>
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
					<li class="breadcrumb-item text-dark">Add Sub-Admin</li>
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


	<br>
	<s:if test="%{responseObject.responseCode=='000'}">
		<tr>
			<td align="left" valign="top">
				<div id="saveMessage" style="color: rgb(109, 218, 54); text-align: center;">
					<s:actionmessage class="success success-text" />
				</div>
			</td>
		</tr>

	</s:if>
	<s:else>
		<div class="error-text">
			<s:actionmessage />
		</div>
	</s:else>

	<div class="post d-flex flex-column-fluid" id="kt_post">
		<!--begin::Container-->
		<div id="kt_content_container" class="container-xxl">
			<div class="row my-5">
				<div class="col">
					<div class="card">
						<div class="card-body">

							<s:form action="addSubAdmin" id="frmAddUser">
								<s:token />

								<!-- <div class=" card-header-rose card-header-icon" style="text-align: center;">
														<h4 class="card-title" style="color: #f78000;">Add User</h4>
													</div> -->
								<!-- <br> -->

								<div class="row">

									<div class="col-md-4 fv-row">
										<label class="d-flex align-items-center fs-6 fw-semibold mb-2">
											First Name</label>
										<s:textfield name="firstName" id="fname"
											class="form-control form-control-solid" maxlength="50"
											autocomplete="off"
											onkeypress="noSpace(event,this);return isCharacterKey(event);" />

										<em id="fname-error" class="error invalid-feedback"
											style="display: inline; color: red;"></em>
									</div>

									<div class="col-md-4 fv-row">
										<label class="d-flex align-items-center fs-6 fw-semibold mb-2">
											Last Name</label>
										<s:textfield name="lastName" id="lname"
											class="form-control form-control-solid" maxlength="50"
											autocomplete="off"
											onkeypress="noSpace(event,this);return isCharacterKey(event);" />

										<em id="lname-error" class="error invalid-feedback"
											style="display: inline; color: red;"></em>
									</div>

									<div class="col-md-4 fv-row">
										<label class="d-flex align-items-center fs-6 fw-semibold mb-2">
											Phone</label>
										<s:textfield name="mobile" id="mobile"
											class="form-control form-control-solid" autocomplete="off"
											onkeypress="javascript:return isNumber (event)"
											maxlength="15" minlength="10" />
										<em id="mobile-error" class="error invalid-feedback"
											style="display: inline; color: red;"></em>
									</div>
								</div>



								<div class="row">
									<div class="col-md-4 fv-row">
										<label class="d-flex align-items-center fs-6 fw-semibold mb-2">
											Email</label>
										<s:textfield name="emailId" id="emailId"
											class="form-control form-control-solid" autocomplete="off" />
										<em id="emailId-error" class="error invalid-feedback"
											style="display: inline; color: red;"></em>
									</div>
									<br>
									<br>
									<div class="form-group">
										<label class="bmd-label-floating"> Need To Show
											Acquirer fields in report</label>
										<s:checkbox name="needToShowAcqFieldsInReport"
											id="needToShowAcqFieldsInReport" />
										<em id="needToShowAcqFieldsInReport-error"
											class="error invalid-feedback" style="display: inline;"></em>
									</div>

									<div class="col-md-4 fv-row">
										<label class="d-flex align-items-center fs-6 fw-semibold mb-2">User
											Group</label>
										<div class="txtnew">
											<s:select name="userGroupId"
												class="form-select form-select-solid" id="userGroupId"
												headerKey="" headerValue="Select User Group"
												list="userGroups" listKey="id" listValue="group"
												autocomplete="off" />
											<em id="userGroupId-error" class="error invalid-feedback"
												style="display: inline; color: red;"></em>
										</div>
									</div>

									<div class="col-md-4 fv-row" id="roleIdDiv"></div>

								</div>


								<br>
								<div class="row">
									<div class="col-md-4 fv-row" id="segmentIdDiv"></div>

									<s:submit id="btnEditUser" name="btnEditUser" value="Save"
										method="submit" class="btn btn-primary col-md-3 fv-row">
									</s:submit>

								</div>

								<s:hidden name="token" value="%{#session.customToken}"></s:hidden>
							</s:form>
						</div>
					</div>
				</div>
			</div>
		</div>




	</div>

	<div class="clear"></div>


</body>

</html>