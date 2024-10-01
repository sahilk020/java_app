<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta name="viewport" content="width=device-width, initial-scale=1" />
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
<title>Sign Up</title>
<link href="../css/default.css" rel="stylesheet" type="text/css" />
<script src="../js/jquery.min.js" type="text/javascript"></script>

<script src="../js/jquery.minshowpop.js"></script>
<script src="../js/jquery.formshowpop.js"></script>
<script src="../js/commanValidate.js"></script>
<script src="../js/core/popper.min.js"></script>
<script src="../js/core/bootstrap-material-design.min.js"></script>
<script src="../js/plugins/perfect-scrollbar.jquery.min.js"></script>
<!-- Control Center for Material Dashboard: parallax effects, scripts for the example pages etc -->
<script src="../js/material-dashboard.js?v=2.1.0" type="text/javascript"></script>

<script src="../js/captcha.js"></script>
<link href="../css/fonts.css"  />
<script>
  function handleChange(){
    var tenant = document.getElementById("companyName").value;
   // console.log(companyName);
    // console.log('tenantId : ',tenant.split(',')[0]);
    // console.log('tenantNumber : ',tenant.split(',')[1]);
    // console.log('companyName : ',tenant.split(',')[2]);
    document.getElementById('tenantId').value = tenant.split(',')[0];
    document.getElementById('tenantNumber').value = tenant.split(',')[1];
    document.getElementById('tenantCompanyName').value = tenant.split(',')[2];
    // console.log(document.getElementById('tenantId').value)
    // console.log(document.getElementById('tenantNumber').value)
    // console.log(document.getElementById('test').value)
  }

</script>
<script>
	if (self == top) {
		var theBody = document.getElementsByTagName('body')[0];
		theBody.style.display = "block";
	} else {
		top.location = self.location;
	}
</script>
</head>
<body>
  <table width="100%" border="0" cellspacing="0" cellpadding="0">
    <tr>
      <td align="center" valign="top"><table width="100%" border="0"
						align="center" cellpadding="0" cellspacing="0" class="txnf">
          <!-- <tr>
            <td align="left"><h2 style="margin-bottom:0px;">Create A New Account </h2></td>
          </tr>     -->
          <tr>
            <td align="left"><s:actionmessage class="success success-text" theme="simple"/></td>
          </tr>
          <tr>
            <td align="left" valign="top">
              <div class="addu">
              <s:form action="signupAdmin" id="formname" >
                <s:hidden name="companyName" id="tenantCompanyName" value="" />	
                <s:hidden name="tenantId" id="tenantId" value="" />
                <s:hidden name="tenantNumber" id="tenantNumber" value="" />
                  <s:token/>
                  <div class="card ">
                    <div class="card-header card-header-rose card-header-icon">
                      <div class="card-icon">
                        <i class="material-icons">mail_outline</i>
                      </div>
                      <h4 class="card-title" style="
                      color: #0271bb;
                      font-weight: 500;">Create A New Account</h4>
                    </div>
                    <span id="error2" style="color:#ff0000; font-size:11px;"></span>
                  <!-- <div class="adduR"> -->
                    <div class="card-body ">
                      <div class="form-group">
                        <label  class="bmd-label-floating"> Admin Name</label>
                   <s:textfield	id="businessName" name="businessName" class="input-control" placeholder="User Name" autocomplete="off" onkeypress="return ValidateBussinessName(event);"/>
                  
                </div>
                
                  <div class="form-group">
                    <label  class="bmd-label-floating"> Phone</label>
                    <s:textfield id="mobile" name="mobile" class="input-control" placeholder="Phone" autocomplete="off" onkeypress="javascript:return isNumber (event)"/>
                  </div>
                  <div class="form-group">
                    <label  class="bmd-label-floating"> Email</label>
                      <s:textfield id="emailId" name="emailId" class="input-control" placeholder="Email" autocomplete="off" onblur="isValidEmail()"/>
                    </div>
                    <span id="invalid-id" style="color:red;"></span>
                  <div class="form-group">
                    <label  class="bmd-label-floating"> Company Name</label>
                    <s:select  class="input-control adminMerchants" id="companyName"
                     list="companyList" onclick="handleChange()"
                    listKey = "tenantId + ',' + tenantNumber + ',' + companyName"  listValue="companyName" value="defaultCompanyName" autocomplete="off" />
                  </div>
                
                  <div class="form-group"> 
                    <label  class="bmd-label-floating"> Password</label>
                    <s:textfield id="password" name="password" type="password" class="input-control" placeholder="Password" onblur="passCheck()" autocomplete="off"/>
                  </div>
                  <span id="wrong-password" style="color:red;"></span>
                  <div class="form-group">
                    <label  class="bmd-label-floating"> Confirm Password</label>
                    <s:textfield	id="confirmPassword" name="confirmPassword" type="password" class="input-control" placeholder="Confirm Password" onblur="passCheck()" autocomplete="off"/>
                  </div>
                </div>
                
                  <div class="card-footer text-right">
               
                  <s:submit value="Sign Up" method ="submit" class="btn  btn-block btn-primary"> </s:submit>
                
                </div>
                <div class="clear"></div>
             
              </s:form>
              </div>
            </div></td>
          </tr>
          <tr>
            <td align="center" valign="top">&nbsp;</td>
          </tr>
        </table></td>
    </tr>
  </table>
  <script>
			$(document).ready(function(){
				
				var fields = {
						
						password : {
							tooltip: "Password must be minimum 8 and <br> maximum 32 characters long, with <br> special characters (! @ , _ + / =) , <br> at least one uppercase and  one <br>lower case alphabet.",
							position: 'right',
							backgroundColor: "#6ad0f6",
							color: '#FFFFFF'
							},
						};
				
				//Include Global Color 
				$("#formname").formtoolip(fields, { backgroundColor: "#000000" , color : "#FFFFFF", fontSize : 14, padding : 10, borderRadius :  5});
					
				});
</script>
</body>
</html>