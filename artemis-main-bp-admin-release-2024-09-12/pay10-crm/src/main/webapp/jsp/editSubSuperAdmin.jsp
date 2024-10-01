<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Modify SubAdmin</title>
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

<script language="JavaScript">	
$(document).ready( function () {
	handleChange();
});
	function handleChange(){

   	  var str = '<s:property value="permissionString"/>';
   	  var permissionArray = str.split("-");
   	  for(j=0;j<permissionArray.length;j++){
			selectPermissions(permissionArray[j]);
		}
    }
	
	function selectPermissions(permissionType){		
		var permissionCheckbox = document.getElementById(permissionType);
		if(permissionCheckbox==null){
			return;
		}
		permissionCheckbox.checked = true;
	}
</script>

<style>
.btn:focus{
		outline: 0 !important;
	}
</style>

</head>
<body>
   <div id="loader-wrapper" style="width: 100%; height: 100%; display:none;">
	  <div id="loader"></div>
    </div>
	
	<table width="100%" border="0" cellspacing="0" cellpadding="0"
		class="txnf">
		<!-- <tr>
			<td align="left"><h2>Edit Sub-admin</h2></td>
		</tr> -->
		<tr>
			<td align="left" valign="top"><div id="saveMessage">
					<s:actionmessage class="success success-text" />
				</div></td>
		</tr>
		<tr>
			<td align="left" valign="top"><div class="addu">
					<s:form action="saveSubSuperAdminDetails" id="frmEditUser">
						<div class="card ">
							<div class="card-header card-header-rose card-header-icon">
							  <div class="card-icon">
								<i class="material-icons">mail_outline</i>
							  </div>
							  <h4 class="card-title" style="
							  color: #0271bb;
							  font-weight: 500;">Edit Sub Super Admin</h4>
							</div>
				
							<div class="card-body ">
						<div class="adduT">
							<label class="bmd-label-floating">First Name<span style="color:red; margin-left:3px;">*</span></label>
							<br>
							<div class="txtnew">
								<s:textfield name="firstName" id="firstName"
									cssClass="signuptextfield" autocomplete="off" />
							</div>
						</div>
						<div class="adduT">
							<label class="bmd-label-floating">Last Name<span style="color:red; margin-left:3px;">*</span></label><br>
							<div class="txtnew">
								<s:textfield name="lastName" id="lastName"
									cssClass="signuptextfield" autocomplete="off" />
							</div>
						</div>
						<div class="adduT">
							<label class="bmd-label-floating">Phone<span style="color:red; margin-left:3px;">*</span></label><br>
							<div class="txtnew">
								<s:textfield name="mobile" id="mobile"
									cssClass="signuptextfield"
									onkeypress="javascript:return isNumber (event)" maxlength="13"
									autocomplete="off" />
							</div>
						</div>
						<div class="adduT">
							<label class="bmd-label-floating">Email<span style="color:red; margin-left:3px;">*</span></label><br>
							<div class="txtnew">
								<s:textfield name="emailId" id="emailAddress"
									cssClass="signuptextfield" autocomplete="off" readonly="true" />
							</div>
						</div>

						<div class="adduT">
							<label class="bmd-label-floating">Is Active ?</label><br>
							<s:checkbox name="isActive" id="isActive" />
							<label class="labelfont" for="1"></label>

						</div>

						<div class="adduT" id="privilegesDiv">
							<label class="bmd-label-floating">Account Privileges<span style="color:red; margin-left:3px;">*</span></label><br>
							<!-- Account Privileges <span style="color:red; margin-left:3px;">*</span><br> -->

                    <table width="100%" cellpadding="0" cellspacing="0" class="whiteroundtble">
                      <s:iterator value="listPermissionType" status="itStatus">
					  <s:if test="%{id==47 || id==42 || id==13 || id==43 || id==44 || id==45 || id==37 || id==38}">
                        <tr>
							<s:if test="%{id == 13}">
                          <td  width="5%" align="left">&nbsp;</td>
							</s:if>
							<s:else>
				          <td  width="5%" align="left">&nbsp;</td>
							</s:else>
                          <td width="5%" height="30" align="left"><s:checkbox name="lstPermissionType"
                                        id="%{permission}" fieldValue="%{permission}"
                                         value="false" autocomplete="off"></s:checkbox></td>
                          <td width="85%" align="left"><label class="labelfont" for="1">
						  		
                              <s:property
                                            value="permission" />
                            </label></td>
                        </tr>
						</s:if>
                      </s:iterator>
                    </table>
					
					 <table width="100%" cellpadding="0" cellspacing="0" class="whiteroundtble">
                      <s:iterator value="listPermissionType" status="itStatus">
					  <s:if test="%{id==14 || id==28 || id==23 || id==24 || id==25 || id==26 || id==46 }">
                        <tr>
						<s:if test="%{id == 14}">
                          <td  width="5%" align="left">&nbsp;</td>
						  </s:if>
						  <s:else>
				          <td  width="5%" align="left">&nbsp;</td>
							</s:else>
                          <td width="5%" height="30" align="left"><s:checkbox name="lstPermissionType"
                                        id="%{permission}" fieldValue="%{permission}"
                                         value="false" autocomplete="off"></s:checkbox></td>
                          <td width="85%" align="left"><label class="labelfont" for="1">
						  		
                              <s:property
                                            value="permission" />
                            </label></td>
                        </tr>
						</s:if>
                      </s:iterator>
					  
                    </table>
					
					<table width="100%" cellpadding="0" cellspacing="0" class="whiteroundtble">
                      <s:iterator value="listPermissionType" status="itStatus">
				       <s:if test="%{id==9 || id ==10}">
                        <tr>
						  <s:if test="%{id == 9}">
                          <td  width="5%" align="left">&nbsp;</td>
						  </s:if>
						  	<s:else>
				          <td  width="5%" align="left">&nbsp;</td>
							</s:else>
                          <td width="5%" height="30" align="left"><s:checkbox name="lstPermissionType"
                                        id="%{permission}" fieldValue="%{permission}"
                                         value="false" autocomplete="off"></s:checkbox></td>
                          <td width="85%" align="left"><label class="labelfont" for="1">
						  		
                              <s:property
                                            value="permission" />
                            </label></td>
                        </tr>
						</s:if>
                      </s:iterator>
                    </table> 
                    
                    <table style="width:100%;" class="whiteroundtble">
                      <s:iterator value="listPermissionType" status="itStatus">
				       <s:if test="%{id==21 || id ==22}">
                        <tr>
						  <%-- <s:if test="%{id == 22}">
                          <td  width="5%" align="left">&nbsp;</td>
						  </s:if>
						  	<s:else>
				          <td  width="5%" align="left">&nbsp;</td>
							</s:else> --%>
							 <td style=" width:5%; align:left;">&nbsp;</td>
                          <td style="width:5%; height:30px; align:left;">
                          	<s:checkbox name="lstPermissionType" id="%{permission}" fieldValue="%{permission}" value="false" autocomplete="off">
                          	</s:checkbox>
                          </td>
                          <td style="width:85%; align:left;">
                          	<label class="labelfont" for="1">
						  		<s:property value="permission" />
                            </label>
                          </td>
                        </tr>
						</s:if>
                      </s:iterator>
                    </table> 
					
					
                    <table width="100%" cellpadding="0" cellspacing="0" class="whiteroundtble">
                      <s:iterator value="listPermissionType" status="itStatus">
                       <s:if test="%{id!=47 && id!=42 && id!=13 && id!=43 && id!=44 && id!=45 && id!=37 && id!=38 && id!=14 && id!=28 && id!=23 && id!=24 && id!=25 && id!=26 && id!=46 && id!=9 && id !=10 && id!=21 && id !=22}">
						<tr>
                          <td  width="5%" align="left">&nbsp;</td>
						  
                          <td width="5%" height="30" align="left"><s:checkbox name="lstPermissionType"
                                        id="%{permission}" fieldValue="%{permission}"
                                         value="false" autocomplete="off"></s:checkbox></td>
                          <td width="85%" align="left"><label class="labelfont" for="1">
						  		
                              <s:property
                                            value="permission" />
                            </label></td>
                        </tr>
						</s:if>
                      </s:iterator>
                    </table>
							<div class="clear"></div>
						</div>
						</div>
						<div class="card-footer text-right">
						<div class="adduT" style="padding-top: 10px">
							<s:submit id="btnEditUser" name="btnEditUser" value="Save"
								type="button" cssClass="btn btn-success btn-md">
							</s:submit>
						</div>
						</div>
						</div>
						<s:hidden name="payId" id="payId" />
						<s:hidden name="token" value="%{#session.customToken}"></s:hidden>
						<s:hidden name="isEditSubSuperAdmin" id="isEditSubSuperAdmin" value="true" /> 
					
					</s:form>
					<div class="clear"></div>
				</div></td>
		</tr>
		<tr>
			<td align="left" valign="top">&nbsp;</td>
		</tr>
	</table>
	
<div class="modal fade" id="merchantBilling" role="dialog">
    <div class="modal-dialog">

      <!-- Modal content-->
      <div class="modal-content">
        <div class="modal-header">

        </div>
        <div class="modal-body">
          <p class="enter_otp">View Merchant Billing is a required permission for permissions Create Mapping , Create Surcharge , Create TDR , Create Service Tax , Create Merchant Mapping or Create Reseller Mapping . Please Select View Merchant Billing to continue</p>
        </div>
        <div class="modal-footer" id="modal_footer">
          <button type="button" class="btn btn-primary btn-round mt-4 submit_btn" data-dismiss="modal">Ok</button>
        </div>
      </div>

    </div>
  </div>
  
  <div class="modal fade" id="merchantSetup" role="dialog">
    <div class="modal-dialog">

      <!-- Modal content-->
      <div class="modal-content">
        <div class="modal-header">

        </div>
        <div class="modal-body">
          <p class="enter_otp">View MerchantSetup is a required permission for permission Edit Merchant Details,Create Merchant,View Merchant Account,Smart Router Audit Trail,Rule Engine Audit Trail,Smart Router,Rule Engine. Please Select View MerchantSetup to continue</p>
        </div>
        <div class="modal-footer" id="modal_footer">
          <button type="button" class="btn btn-primary btn-round mt-4 submit_btn" data-dismiss="modal">Ok</button>
        </div>
      </div>

    </div>
  </div>
  
  <div class="modal fade" id="viewInvoice" role="dialog">
    <div class="modal-dialog">

      <!-- Modal content-->
      <div class="modal-content">
        <div class="modal-header">

        </div>
        <div class="modal-body">
          <p class="enter_otp">View Invoice is a required permission for permission Create Invoice. Please Select View Invoice to continue"</p>
        </div>
        <div class="modal-footer" id="modal_footer">
          <button type="button" class="btn btn-primary btn-round mt-4 submit_btn" data-dismiss="modal">Ok</button>
        </div>
      </div>

    </div>
  </div>
	


</body>
</html>