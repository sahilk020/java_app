<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Modify Sub Merchant</title>
<link href="../css/default.css" rel="stylesheet" type="text/css" />
<script src="../js/jquery.js"></script>
<script src="../js/commanValidate.js"></script>
<script src="../js/core/popper.min.js"></script>
<script src="../js/core/bootstrap-material-design.min.js"></script>
<script src="../js/plugins/perfect-scrollbar.jquery.min.js"></script>
<!-- Control Center for Material Dashboard: parallax effects, scripts for the example pages etc -->
<script src="../js/material-dashboard.js?v=2.1.0" type="text/javascript"></script>

<script type="text/javascript">	

 $(document).ready( function() {
    
	$('#btnEditUser').click(function() {	
			var answer = confirm("Are you sure you want to edit user details?");
				if (answer != true) {
					return false;
				} else {
					/*  $.ajax({
							url : 'editUserDetails',
							type : "POST",
							data : {
									firstName : document.getElementById('firstName').value ,						
									lastName:document.getElementById('lastName').value,
									mobile : document.getElementById('mobile').value,
									emailAddress : document.getElementById('emailAddress').value,
									isActive: document.getElementById('isActive').value,
								},									
									success:function(data){		       	    		       	    		
					       	   		var res = data.response;		       	    	
					          		},
										error:function(data){	
					           	    		alert("Unable to edit user details");
										}
									});		  */
		            }
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
</head>
<body>
<table width="100%" border="0" cellspacing="0" cellpadding="0" class="txnf">
  <!-- <tr>
    <td align="left"><h2>Edit SubUser</h2></td>
  </tr> -->
  <tr>
    <td align="left" valign="top"><div id="saveMessage">
        <s:actionmessage class="success success-text" />
      </div></td>
  </tr>
  <tr>
    <td align="left" valign="top"><div class="addu">
        <s:form action="editSubUserDetails" id="frmEditUser" >
			<div class="card ">
				<div class="card-header card-header-rose card-header-icon">
				  <div class="card-icon">
					<i class="material-icons">mail_outline</i>
				  </div>
				  <h4 class="card-title" style="
				  color: #0271bb;
				  font-weight: 500;">Edit Sub Merchant</h4>
				</div>
	
				<div class="card-body ">
          <div class="adduT">
			<label class="bmd-label-floating">First Name</label><br>
			  <!-- First Name<br> -->
            <div class="txtnew">
              <s:textfield	name="firstName" id="firstName" cssClass="signuptextfield" autocomplete="off" />
            </div>
          </div>
          <div class="adduT">
			<label class="bmd-label-floating">Last Name</label><br>
			  <!-- Last Name<br> -->
            <div class="txtnew">
              <s:textfield	name="lastName" id="lastName" cssClass="signuptextfield" autocomplete="off"/>
            </div>
          </div>
          <div class="adduT">
			<label class="bmd-label-floating">Phone</label><br>
			  <!-- Phone<br> -->
            <div class="txtnew">
              <s:textfield name="mobile" id="mobile" cssClass="signuptextfield" onkeypress="javascript:return isNumber (event)"  maxlength="10" autocomplete="off" />
            </div>
          </div>
          <div class="adduT">
			<label class="bmd-label-floating">Email</label><br>
			  <!-- Email<br> -->
            <div class="txtnew">
              <s:textfield name="emailId" id="emailAddress" cssClass="signuptextfield" autocomplete="off" readonly="true" />
            </div>
          </div>
          
         <div class="adduT">
			<label class="bmd-label-floating">Is Active ?</label><br>
			 <!-- Is Active ? -->
            <s:checkbox	name="isActive" id="isActive" />
            <label class="labelfont" for="1"></label>
            
          </div>
          <div class="adduT">
            <label  class="bmd-label-floating">User Group</label>
			<div class="txtnew">
				<s:select name="userGroupId" class="input-control" id="userGroupId"
						headerKey="" headerValue="Select User Group" list="userGroups"
						listKey="id" listValue="group" autocomplete="off" />
			</div>
          </div>
          <div class="adduT" id="roleIdDiv">
            <label  class="bmd-label-floating">Role</label>
			<div class="txtnew">
				<s:select name="roleId" class="input-control" id="roleId"
						headerKey="" headerValue="Select Role" list="roles"
						listKey="id" listValue="roleName" autocomplete="off" />
			</div>
          </div>
          <div class="adduT" style="padding-top:10px">
            <s:submit id="btnEditUser" name ="btnEditUser" value="Save" type="button" cssClass="btn btn-success btn-md"> </s:submit>
		  </div>
		  </div>
		  </div>
          <s:hidden name="payId" id="payId"  /> 
         <s:hidden name="token" value="%{#session.customToken}"></s:hidden>
        </s:form>
        <div class="clear"></div>
      </div></td>
  </tr>
  <tr>
    <td align="left" valign="top">&nbsp;</td>
  </tr>
</table>
</body>
</html>