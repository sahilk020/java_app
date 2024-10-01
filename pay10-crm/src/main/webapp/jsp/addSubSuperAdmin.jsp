<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Add SubUser</title>
<link href="../css/default.css" rel="stylesheet" type="text/css" />
<script src="../js/jquery.minshowpop.js"></script>
<script src="../js/jquery.formshowpop.js"></script>
<script src="../js/commanValidate.js"></script>
<script src="../js/core/popper.min.js"></script>
<script src="../js/core/bootstrap-material-design.min.js"></script>
<script src="../js/plugins/perfect-scrollbar.jquery.min.js"></script>
<!-- Control Center for Material Dashboard: parallax effects, scripts for the example pages etc -->
<script src="../js/material-dashboard.js?v=2.1.0" type="text/javascript"></script>
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
<style type="text/css">.error-text{color:#a94442;font-weight:bold;background-color:#f2dede;list-style-type:none;text-align:center;list-style-type: none;margin-top:10px;
}.error-text li { list-style-type:none; }
#response{color:green;}
</style>
</head>
<body>

<table width="100%" border="0" cellspacing="0" cellpadding="0" class="txnf">
  <!-- <tr>
    <td align="left"><h2>Add New User</h2></td>
  </tr> -->
  
<s:if test="%{responseObject.responseCode=='000'}">
   <tr>
    <td align="left" valign="top"><div id="saveMessage">
        <s:actionmessage class="success success-text" />
      </div></td>
  </tr>
  
  </s:if>
<s:else><div class="error-text"><s:actionmessage/></div></s:else>
 
  <tr>
    <td align="left" valign="top">
      <div class="addu">
        <s:form action="saveSubSuperAdminDetails" id="frmAddUser" >
          <s:token/>
          <div class="card ">
            <div class="card-header card-header-rose card-header-icon">
              <div class="card-icon">
                <i class="material-icons">mail_outline</i>
              </div>
              <h4 class="card-title" style="
              color: #0271bb;
              font-weight: 500;">Add New Sub Super Admin</h4>
            </div>

            <div class="card-body ">
              <div class="form-group">
                <label  class="bmd-label-floating"> First Name</label><br>
                <s:textfield name="firstName" id = "fname" class="input-control" autocomplete="off" onkeypress="noSpace(event,this);return isCharacterKey(event);" />
           
              </div>
              <div class="form-group">
                <label  class="bmd-label-floating"> Last Name</label>
                <s:textfield	name="lastName" id = "lname"  class="input-control" autocomplete="off"  onkeypress="noSpace(event,this);return isCharacterKey(event);"/>
                
              </div>
              <div class="form-group">
                <label  class="bmd-label-floating"> Phone</label>
                <s:textfield name="mobile"  class="input-control" autocomplete="off" onkeypress="javascript:return isNumber (event)"
                maxlength="10"/>
                </div>
                <div class="form-group">
                  <label  class="bmd-label-floating"> Email</label>
                  <s:textfield name="emailId" class="input-control" autocomplete="off" />
                  </div>

				<div class="form-group">Is Active ?
				<s:checkbox    name="isActive" id="isActive" />
				<label class="labelfont" for="1"></label>
            
				</div> 
				
                <div class="form-group">
                  <div ><h3 style="float: left;">Account Privileges</h3><br>
            
                    <table width="100%" cellpadding="0" cellspacing="0" class="whiteroundtble">
                      <s:iterator value="listPermissionType" status="itStatus">
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
                      </s:iterator>
                    </table>
                    <div class="clear"></div>
                  </div>
                </div>
              
            </div>
            <div class="card-footer text-right">
            
              <s:submit  id="submit" value="Save" method ="submit" class="btn  btn-block btn-primary"> </s:submit>
            </div>

      
            
            <s:hidden name="token" value="%{#session.customToken}"></s:hidden>
        
          </div>
          </s:form>
        
        	<s:hidden name="response" id="response" value="%{responseObject.responseCode}" />	
        <div class="clear"></div>
         
          
          
          </td>
  </tr>
  <tr>
    <td align="left" valign="top">&nbsp;</td>
  </tr>
</table>

</body>
</html>