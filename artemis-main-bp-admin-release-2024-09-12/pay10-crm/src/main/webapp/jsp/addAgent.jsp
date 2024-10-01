<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Add Agent</title>

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

<script src="../assets/plugins/global/plugins.bundle.js"></script>
<script src="../assets/js/scripts.bundle.js"></script>
<link href="../css/select2.min.css" rel="stylesheet" />
<script src="../js/jquery.select2.js" type="text/javascript"></script>


  
<script language="JavaScript">	
$(document).ready( function () {

	$('#btnEditUser').click(function() {
		var firstN = document.getElementById('firstN').value;
		var lName = document.getElementById('lName').value;
		var phoneNo = document.getElementById('phoneNo').value;
		var EmailIdInpt = document.getElementById('EmailIdInpt').value;
		
		$('.frstNameError, .lstNameError, .phoneNoError, .emailIdError').hide();


		if(firstN){
			$('.frstNameError').hide();
		}else{
			$('.frstNameError').show();
			//return false;
		}

		if(lName){
			$('.lstNameError').hide();
		}else{
			$('.lstNameError').show();
			//return false;
		}

		if(phoneNo && phoneNo.length >= 10){
			$('.phoneNoError').hide();
		}else{
			$('.phoneNoError').show();
			//return false;
		}

		if(isValidEmail()){
			$('.emailIdError').hide();
		}else{
			$('.emailIdError').show();
			return false;
		}
		
        /*  $('#loader-wrapper').show(); */
		 document.getElementById("frmEditUser").submit();
		
	      });
});
$(function() {
	$("#userGroupId").change(function() {
		var groupId = this.value;
		var token  = document.getElementsByName("token")[0].value;
		$.ajax({
			type : "POST",
			url : "roleByGroup",
			data : {
				groupId : groupId,
				token:token,
				"struts.token.name": "token"
			},
			success : function(data, status) {
				document.getElementById("roleIdDiv").innerHTML=data;
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

.errorMessage {
	display: none;
}

.btnSbmt {
	padding: 5px 10px !important;
	margin-right: 26px !important;
}

.errorInpt {
	font: 400 11px arial;
	color: red;
	margin: 0 0 0 12px !important;
	display: none;
}

.adduT {
	margin-bottom: 0 !important;
	margin-left: 12px !important;
}

.fixHeight {
	height: 10px;
}

.actionMessage {
	border: 1px solid transparent;
	border-radius: 0 !important;
	width: 100% !important;
	margin: 0 !important;
}

.btn:focus {
	outline: 0 !important;
}
</style>
</head>
<body class="bodyColor">



<div class="content d-flex flex-column flex-column-fluid" id="kt_content">
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
					Agent</h1>
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
					<li class="breadcrumb-item text-dark">Add Agent</li>
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
				
					<div class="row my-5">
						<div class="col">
							<div class="card">
								<div class="card-body">
	<!-- <div id="loader-wrapper"
		style="width: 100%; height: 100%; display: none;">
		<div id="loader"></div>
	</div> -->
	
		<table width="100%" border="0" cellspacing="0" cellpadding="0"
			class="txnf">
			<tr>
				<!-- <td align="left"><h2>Add Agent</h2></td> -->
			</tr>

			<s:if test="%{responseObject.responseCode=='000'}">
				<tr>
					<td align="left" valign="top"><div id="saveMessage">
							<s:actionmessage class="success success-text" />
						</div></td>
				</tr>

			</s:if>
			<s:else>
				<%-- <div class="error-text">
					<s:actionmessage />
				</div> --%>
			</s:else>


			<tr>

				<td align="left" valign="top">
					

						<s:form action="addAgent" id="frmAddUser">
							<s:token />
							<div class="card ">
								<!-- <div class="card-header card-header-rose card-header-icon">
									<div class="card-icon">
										<i class="material-icons">mail_outline</i>
									</div>
									 <h4 class="card-title" style="
              color: #0271bb;
              font-weight: 500;">Add Agent</h4>
								</div> -->
								
								

								<div class="card-body ">
									<div class="row g-9 mb-8 d-flex justify-content-center"> 
									 <div class="col-6">
									
										<label class="d-flex align-items-center fs-6 fw-semibold mb-2">
											First Name<span style="color: red; margin-left: 3px;">*</span>
										</label><br>
										<s:textfield name="firstName"
											class="form-control form-control-solid" id="firstN"
											autocomplete="off"
											onkeypress="return lettersOnly(event,this); noSpace(event,this);"
											onkeyup="removeError()" />

									
									
										<p id="fNameError" class="errorInpt frstNameError">Please
											Enter First Name.</p>
									<br>
									
										<label class="d-flex align-items-center fs-6 fw-semibold mb-2">
											Last Name<span style="color: red; margin-left: 3px;">*</span>
										</label>
										<s:textfield name="lastName"
											class="  form-control form-control-solid" id="lName"
											autocomplete="off"
											onkeypress="return lettersOnly(event,this); noSpace(event,this);"
											onkeyup="secondError()" />

									
									
										<p id="lNameError" class="errorInpt lstNameError">Please
											Enter Last Name</p>
										<br>
									
										<label class="d-flex align-items-center fs-6 fw-semibold mb-2">
											Phone<span style="color: red; margin-left: 3px;">*</span>
										</label>
										<s:textfield name="mobile"
											class="  form-control form-control-solid" id="phoneNo"
											autocomplete="off" onkeypress="return isNumberKey(event)"
											maxlength="10" />
									
									
										<p class="errorInpt phoneNoError">Please Enter Valid Phone
											Number.</p>
										<br>
									
										<label class="d-flex align-items-center fs-6 fw-semibold mb-2">
											Email<span style="color: red; margin-left: 3px;">*</span>
										</label>
										<s:textfield name="emailId"
											class="  form-control form-control-solid" id="EmailIdInpt"
											autocomplete="off" onkeypress="isValidEmail()" />
									
									
										<p id="emailError" class="errorInpt emailIdError">Please
											Enter Valid Email Id.</p>
										<br>

									
										<label class="d-flex align-items-center fs-6 fw-semibold mb-2">User
											Group</label>
										<div class="txtnew">
											<s:select name="userGroupId"
												class="form-select form-select-solid searchopt" id="userGroupId"
												headerKey="" headerValue="Select User Group"
												list="userGroups" listKey="id" listValue="group"
												autocomplete="off" />
										</div>
											<br>
									

									<div class="row g-9 mb-8 justify-content-end" id="roleIdDiv">
									</div>
									</div>
									</div>
								</div>



								<div class="card-footer text-right">

									<s:submit id="btnEditUser" name="btnEditUser" value="Save"
										class="btn  btn-primary">
									</s:submit>
								</div>
							</div>

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
	</div>
	</div>
	</div>
	</div>
	</div>
	</div>
	<script type="text/javascript">
	function lettersOnly(e, t) {
            try {
                if (window.event) {
                    var charCode = window.event.keyCode;
                }
                else if (e) {
                    var charCode = e.which;
                }
                else { return true; }
                if ((charCode > 64 && charCode < 91) || (charCode > 96 && charCode < 123) || charCode == 8)
                    return true;
                else
                    return false;
            }
            catch (err) {
                alert(err.Description);
            }
        }

function isNumberKey(evt){
    var charCode = (evt.which) ? evt.which : event.keyCode
    if (charCode > 31 && (charCode < 48 || charCode > 57))
        return false;
    return true;
}
</script>
	<script type="text/javascript">
  function isValidEmail() {
    var emailexp = /^[_A-Za-z0-9-]+(\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\.[A-Za-z0-9-]+)*(\.[_A-Za-z0-9-]+)$/;
    var emailValue = document.getElementById("EmailIdInpt").value;
      if (emailValue.trim() && emailValue.match(emailexp)) {
		  document.getElementById("emailError").style.display = "none";
              return true;
      } else{
          document.getElementById("emailError").style.display = "block";
      }
  }
</script>

	<script>
function removeError(){
	var firstValue = document.getElementById("firstN").value;
	if(firstValue.length >0){
		document.getElementById("fNameError").style.display = "none";
	}
	else{
		document.getElementById("fNameError").style.display = "block";
	}
}

function secondError(){
	var lastValue = document.getElementById("lName").value;
	if(lastValue.length > 0){
		document.getElementById("lNameError").style.display = "none";
	}
	else{
		document.getElementById("lNameError").style.display = "block";
	}
}

</script>
<script type="text/javascript">
	$(document).ready(function () {		
	$("#userGroupId").select2();
	$("#roleId").select2();
		});
</script>
</body>
</html>