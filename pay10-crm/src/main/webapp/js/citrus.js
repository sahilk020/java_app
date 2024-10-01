<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%
	String error = request.getParameter("error");
	if (error == null || error == "null") {
		error = "";
	}
%>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1" />
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
<title>login</title>
<link rel="stylesheet" type="text/css" href="<s:url value="../css/default.css"/>" />
<script src="../js/login.js"></script>
<script src="../js/captcha.js"></script>
 
<style type="text/css">
/* Manual css s:actionmessage for this page */
.errorMessage {
	font: normal 11px arial;
	color: #ff0000;
	position: absolute;
	top: 145px;
}
</style>
<script>
	if (self == top) {
		var theBody = document.getElementsByTagName('body')[0];
		theBody.style.display = "block";
	} else {
		top.location = self.location;
	}
</script>
</head>
<body class="bodyColor" onload="callMerchantEnv();return generateCaptcha();">
<span class="actionMessage"><li>Your session has been expired!</li></span>
	<s:form  name="loginForm" action="login" method="post" onselectstart="return false"
		oncontextmenu="return false;">
		<s:token />
		<br />
		<span id="merchantEnviroment"></span>
		<table width="100" border="0" align="center" cellpadding="0" cellspacing="0">
			<tr>
				<td height="60">&nbsp; </td>
			</tr>
			<tr>
				<td><div class="signupbox">
						<table width="300" border="0" cellspacing="0" cellpadding="0">
							<tr>
								<td align="center" class="signup-headingbg2"><img src="../image/IRCT IPAY.png"
									width="180" /></td>
							</tr>
							<tr>
								<td align="center"><table width="90%" border="0" align="center" cellpadding="0"
										cellspacing="0">
										<tr>
											<td align="left" valign="middle">&nbsp;</td>
										</tr>
										<tr>
											<td><span><s:actionmessage /></span></td>
										</tr>
										<tr>
											<td align="left" height="50" valign="middle"><span class="user_icon"></span> <s:textfield
													name="emailId" type="text" cssClass="signuptextfieldicon" id="emailId"
													placeholder="User Id" autocomplete="off" required="required" onkeypress="emailCheck()" /><span
												id="error2"></span></td>
										</tr>
										<tr>
											<td align="left" height="50" valign="middle"><span class="pswrd_icon"></span> <s:textfield
													name="password" type="password" cssClass="signuptextfieldicon" id="password"
													placeholder="Password" autocomplete="off" required="required" onkeypress="passCheck()" /><span
												id="error2"></span></td>
										</tr>
									<tr>
													<td height="50" align="left">
														<div class="rederror" id="error3"></div>
														<table width="100%" cellpadding="0" cellspacing="0">
															<tr>
																<td width="55%" align="left"><s:textfield
																		label="Code" name="captcha" id="captcha" size="20"
																		maxlength="10" /></td>
																<td width="6%" align="left">&nbsp;</td>
																<td width="7%" align="right"><img
																	src="../Captcha.jpg" border="0" id='captchaImage'></td>



															</tr>
														</table>



													</td>
												</tr>
										<tr>
											<td align="left" height="60" valign="middle"><s:fielderror class="redvalid">
													<s:param>userId</s:param>
													<s:param>password</s:param>
												</s:fielderror> <s:submit cssClass="signupbutton btn-primary" key="submit" value="Submit"
													></s:submit></td>
										</tr>
										<tr>
											<td colspan="2" align="center" valign="middle">
												<table width="100%" border="0" align="center" cellpadding="4" cellspacing="0">
													<tr>
														<td align="left" valign="middle"><s:a action="merchantSignup">Create an account?</s:a>
														</td>
														<td align="right" valign="middle"><s:a action="forgetPassword">Forgot Password?</s:a></td>
													</tr>
												</table>
											</td>
										</tr>
										<tr>
											<td align="left" colspan="2" class="lightbluebg" height="1"></td>
										</tr>
										<tr>
											<td align="left" colspan="2">&nbsp;</td>
										</tr>
									</table></td>
							</tr>
						</table>
					</div></td>
			</tr>
		</table>
	</s:form>
</body>
</html>