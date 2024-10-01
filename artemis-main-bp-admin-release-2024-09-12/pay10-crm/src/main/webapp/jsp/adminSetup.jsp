<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Admin Setup</title>
<link href="../css/default.css" rel="stylesheet" type="text/css" />
<script src="../js/jquery.js"></script>
<script src="../js/commanValidate.js"></script>
<script src="../js/core/popper.min.js"></script>
<script src="../js/core/bootstrap-material-design.min.js"></script>
<script src="../js/plugins/perfect-scrollbar.jquery.min.js"></script>
<!-- Control Center for Material Dashboard: parallax effects, scripts for the example pages etc -->
<script src="../js/material-dashboard.js?v=2.1.0" type="text/javascript"></script>

<!--  loader scripts -->
<script src="../js/loader/modernizr-2.6.2.min.js"></script>
<script src="../js/loader/main.js"></script>
<link rel="stylesheet" href="../css/loader/normalize.css" />
<link rel="stylesheet" href="../css/loader/main.css" />
<link rel="stylesheet" href="../css/loader/customLoader.css" />
</head>
<body>
	<div id="loader-wrapper" style="width: 100%; height: 100%; display:none;">
		<div id="loader"></div>
	  </div>
	
		<table width="100%" border="0" align="center" cellpadding="0"
			cellspacing="0">
			   <s:actionmessage class="success success-text"/>   
			<tr>
				<td align="left" valign="top" >
					<div class="addu">
					<s:form action="adminSetupUpdate" method="post" autocomplete="off">
						<div class="card ">
							<div class="card-header card-header-rose card-header-icon">
							  <div class="card-icon">
								<i class="material-icons">mail_outline</i>
							  </div>
							  <h4 class="card-title" style="
							  color: #0271bb;
							  font-weight: 500;">Edit Admin</h4>
							</div>
							<div class="card-body ">
								<div class="adduT">
									Status<br />
									<s:select class="textFL_merch" headerValue="ALL"
										list="#{'ACTIVE': 'ACTIVE' , 'PENDING': 'PENDING' ,'TRANSACTION_BLOCKED': 'TRANSACTION_BLOCKED','SUSPENDED':'SUSPENDED','TERMINATED': 'TERMINATED'}"
										id="status" name="userStatus" value="%{user.userStatus}" />
								</div>
								<div class="adduT" style="display: none;">
									
										<label class="bmd-label-floating">Pay ID</label>
										<s:textfield id="payId" cssClass="signuptextfield" name="payId"
											type="text" value="%{user.payId}" readonly="true"></s:textfield>
									
								</div>
								<div class="adduT" >
									
									<label class="bmd-label-floating">Tenant Number</label>
									<s:textfield id="tenantNumber" cssClass="signuptextfield" name="tenantNumber"
										type="text" value="%{user.tenantNumber}" readonly="true"></s:textfield>
								
							</div>
							<div class="adduT" >
									
								<label class="bmd-label-floating">Company Name</label>
								<s:textfield id="companyName" cssClass="signuptextfield" name="companyName"
									type="text" value="%{user.companyName}" readonly="true"></s:textfield>
							
						</div>
								<div class="adduT">
									
										<label class="bmd-label-floating">Registration Date</label>
										<s:textfield cssClass="signuptextfield" id="registrationDate"
											name="registrationDate" type="text"
											value="%{user.registrationDate}" readonly="true"></s:textfield>
									
								</div>
								<div class="adduT" style="display: none;">
								
										<label class="bmd-label-floating">Activaction Date</label>
										<s:textfield cssClass="signuptextfield" id="activationDate"
											name="activationDate" type="text"
											value="%{user.activationDate}" readonly="true"></s:textfield>
									
								</div>	
								<div class="adduT">
									
										<label class="bmd-label-floating">User Name</label>
										<s:textfield cssClass="signuptextfield" id="businessName"
											name="businessName" type="text"
											value="%{user.businessName}" onkeypress="return ValidateBussinessName(event);"
											autocomplete="off"
											onKeyPress="return ValidateAlpha(event);"></s:textfield>
									
								</div>

							
								<div class="adduT">
									
										<label class="bmd-label-floating">Email ID</label>
										<s:textfield cssClass="signuptextfield" id="emailId" name="emailId"
											type="text" value="%{user.emailId}" onblur="isValidEmail()" readonly="true"></s:textfield>
									
								</div>
								<div class="adduT">
									
									<label class="bmd-label-floating">Phone</label>
									<s:textfield cssClass="signuptextfield" id="mobile" name="mobile" onkeypress="javascript:return isNumber (event)"
										type="text" value="%{user.mobile}" ></s:textfield>
								
							</div>
							</div>
							<div class="card-footer text-right">
								<div class="adduT" style="padding-top: 10px">
									<s:submit class="btn btn-success btn-md" value="Save"></s:submit>  
								</div>
								</div>
							</div>
							<s:hidden name="payId" id="payId" />
							<s:hidden name="userStatus " id="userStatus" />
						<s:hidden name="token" value="%{#session.customToken}"></s:hidden>
	
					</s:form>
				</div>
					</td>
			</tr>
		</table>	
	
</body>
</html>