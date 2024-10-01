<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html
  PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%
	String error = request.getParameter("error");
	if (error == null || error == "null") {
		error = "";
	}
%>
<!--
Template Name: Metronic - Bootstrap 4 HTML, React, Angular 10 & VueJS Admin Dashboard Theme
Author: KeenThemes
Website: http://www.keenthemes.com/
Contact: support@keenthemes.com
Follow: www.twitter.com/keenthemes
Dribbble: www.dribbble.com/keenthemes
Like: www.facebook.com/keenthemes
Purchase: https://1.envato.market/EA4JP
Renew Support: https://1.envato.market/EA4JP
License: You must have a valid license purchased only from themeforest(the above link) in order to legally use the theme for your project.
-->
<html lang="en">
	<!--begin::Head-->
	<head><base href="">
		<meta charset="utf-8" />
		<title>Login</title>
		<link rel="icon" href="../image/98x98.png">
		<meta name="description" content="Login page example" />
		<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no" />
		<link rel="canonical" href="https://keenthemes.com/metronic" />
		<!--begin::Fonts-->
		<link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Poppins:300,400,500,600,700" />
		<!--end::Fonts-->
		<!--begin::Page Custom Styles(used by this page)-->
		<link href="../assets/css/pages/login/classic/login-4.css" rel="stylesheet" type="text/css" />
		<!--end::Page Custom Styles-->
		<!--begin::Global Theme Styles(used by all pages)-->
		<link href="../assets/plugins/global/plugins.bundle.css" rel="stylesheet" type="text/css" />
		<link href="../assets/plugins/custom/prismjs/prismjs.bundle.css" rel="stylesheet" type="text/css" />
		<link href="../assets/css/style.bundle.css" rel="stylesheet" type="text/css" />
		<script src="../js/login.js"></script>
  
		<script src="../js/jquery.js"></script>
		<script src="../js/index.js"></script>
		<!--end::Global Theme Styles-->
		<!--begin::Layout Themes(used by all pages)-->
		<!--end::Layout Themes-->
		<!-- <link rel="shortcut icon" href="../assets/media/logos/favicon.ico" /> -->
	</head>
	<style>
		#wwctrl_kt_login_signin_submit{
			text-align: center !important;
		}
		.errorMessage {
  font-size: 11px;
  font-weight: 600;
  color: #ff0000;
  display: block;
  /* margin: -15px 0px 3px 0px;
  padding: 0px 0px 0px 0px; */
  list-style: none;
  position: absolute;
top: 52px;
}
.redvalid {
    font-size: 14px;
    font-weight: 400;
    color: #ff0000;
    display: block;
    list-style: none;
    position: absolute;
	top: 175px;
	left: 0px;
}



.error {
  font-size: 14px;
  font-weight: 400;
  color: #ff0000;
  display: none;
  margin: 0;
  padding: 0;
  width: 100%;
  height: auto;
  position: relative;
  /* margin-top: 0px; */
  text-align: left;
  margin-bottom: 5px;
}

#error2 {
  color: red;
}
	</style>
	<!--end::Head-->
	<!--begin::Body-->
	<body id="kt_body" onload="callMerchantEnv();" class="header-mobile-fixed subheader-enabled aside-enabled aside-fixed aside-secondary-enabled page-loading">
		<!--begin::Main-->
		<div class="d-flex flex-column flex-root">
			<!--begin::Login-->
			<div class="login login-4 login-signin-on d-flex flex-row-fluid" id="kt_login">
				<div class="d-flex flex-center flex-row-fluid bgi-size-cover bgi-position-top bgi-no-repeat" style="background-image: url('../assets/media/bg/bg-3.jpg');">
					<div class="login-form text-center p-7 position-relative overflow-hidden">
						<!--begin::Login Header-->
						<div class="d-flex flex-center mb-15">
							<a href="#">
								<img src="../image/66x46.png" class="max-h-75px" alt="" />
							</a>
						</div>
						<!--end::Login Header-->
						<!--begin::Login Sign in form-->
						<div class="login-signin">
							<div class="mb-20">
								<h3>Sign In To Pay10</h3>
								<div class="text-muted font-weight-bold">Enter your details to login to your account:</div>
							</div>
							<s:form name=" " action="login" method="post" onsubmit="return validateMyForm(event);" class="form" id="kt_login_signin_form"
							onselectstart="return false" oncontextmenu="return false;">
							<s:token />
					
							  <span>
								<s:actionmessage /></span>
							<!-- <form class="form" id="kt_login_signin_form"> -->
								<div class="form-group mb-5">
									<!-- <input class="form-control h-auto form-control-solid py-4 px-8" type="text" placeholder="Email" name="username" autocomplete="off" /> -->
								    <input name="emailId" type="text"  class="form-control h-auto form-control-solid py-4 px-8" onblur="emailOnBlur()"
                              id="emailId" placeholder="Email Id" autocomplete="off"
                              onKeyDown="if(event.keyCode === 32)return false;if(event.keyCode === 9) emailOnBlur();" autofocus /><!---->
                            <span id="error2"></span>
                        
					  </div>
					  <p class="error errorEmail">Please Enter Valid User id</p>
					  <input type="hidden" name="loginType" value="pwd" />
				
								</div>
								<div class="form-group mb-5">
									<input name="password" type="password" onblur="passwordOnBlur()" class="form-control h-auto form-control-solid py-4 px-8"
									id="password" placeholder="Password" autocomplete="off" maxlength="32"  />
								  <span id="error2"></span>
									<!-- <input class="form-control h-auto form-control-solid py-4 px-8" type="password" placeholder="Password" name="password" /> -->
								</div>
								<p class="error errorPassword" style="margin-bottom:10px !important;">Please Enter Valid
									Password
								  </p>
								  <input type="hidden" value="pwd" name="data" />
								<div class="form-group d-flex flex-wrap justify-content-between align-items-center">
									<!-- <div class="checkbox-inline">
										<label class="checkbox m-0 text-muted">
										<input type="checkbox" name="remember" />
										<span></span>Remember me</label>
									</div> -->
									<s:a action="forgetPassword"  id="kt_login_forgot" class="text-muted text-hover-primary">Forget Password ?</s:a>
								</div>
								<s:fielderror class="redvalid">
									<s:param>emailId</s:param>
									<s:param>password</s:param>
									</s:fielderror>
								  <s:submit id="kt_login_signin_submit" class="btn btn-primary font-weight-bold px-9 py-4 my-3 mx-4" key="submit" value="Submit"
									onkeypress="passCheck()" onchange="emailCheck()"></s:submit>
								<!-- <button id="kt_login_signin_submit" class="btn btn-primary font-weight-bold px-9 py-4 my-3 mx-4">Sign In</button> -->
							</s:form>
							<%-- <div class="mt-10">
								<span class="opacity-70 mr-4">Don't have an account yet?</span>
								<s:a action="merchantSignup" id="kt_login_signup" class="text-muted text-hover-primary font-weight-bold">Sign Up!</s:a>
							</div> --%>
						</div>
				
					</div>
				</div>
			</div>
			<!--end::Login-->
		</div>
		<div class="modal fade" id="modaltogenerateotp" role="dialog">
			<div class="modal-dialog">
		
			  <!-- Modal content-->
			  <div class="modal-content">
				<div class="modal-header">
		
				</div>
				<div class="modal-body">
				  <p class="enter_otp">Please enter email Id to generate OTP.</p>
				</div>
				<div class="modal-footer" id="modal_footer">
				  <button type="button" class="btn btn-primary btn-round mt-4 submit_btn" data-dismiss="modal">Ok</button>
				</div>
			  </div>
		
			</div>
		  </div>
		  <!--End Modal to generate Otp error-->
		
		  <!--Start Modal to generate Otp error-->
		  <div class="modal fade" id="modaltosendOTP" role="dialog">
			<div class="modal-dialog">
		
			  <!-- Modal content-->
			  <div class="modal-content">
				<div class="modal-header">
		
				</div>
				<div class="modal-body">
				  <p class="enter_otp" id="otpnumber"></p>
				</div>
				<div class="modal-footer" id="modal_footer">
				  <button type="button" class="btn btn-primary btn-round mt-4 submit_btn" data-dismiss="modal">Ok</button>
				</div>
			  </div>
		
			</div>
		  </div>
		  <!--End Modal to generate Otp error-->
		  <!--Start Modal for Invalid user alert-->
		  <div class="modal fade" id="invalidusermodal" role="dialog">
			<div class="modal-dialog">
		
			  <!-- Modal content-->
			  <div class="modal-content">
				<div class="modal-header">
		
				</div>
				<div class="modal-body">
				  <p class="enter_otp" id="invaliduser"></p>
				</div>
				<div class="modal-footer" id="modal_footer">
				  <button type="button" class="btn btn-primary btn-round mt-4 submit_btn" data-dismiss="modal">Ok</button>
				</div>
			  </div>
		
			</div>
		  </div>
		  <!--End Modal to Invalid user alert-->
		
      <script src="../js/customindex.js"></script>
      <script src="../js/core/popper.min.js"></script>

      <script src="../js/core/bootstrap-material-design.min.js"></script>
      <script src="../js/plugins/perfect-scrollbar.jquery.min.js"></script>
      <script src="../js/material-dashboard.js" type="text/javascript"></script>
		<!--end::Main-->
		<script>var HOST_URL = "https://preview.keenthemes.com/metronic/theme/html/tools/preview";</script>
		<!--begin::Global Config(global config for global JS scripts)-->
		<script>var KTAppSettings = { "breakpoints": { "sm": 576, "md": 768, "lg": 992, "xl": 1200, "xxl": 1200 }, "colors": { "theme": { "base": { "white": "#ffffff", "primary": "#1BC5BD", "secondary": "#E5EAEE", "success": "#1BC5BD", "info": "#6993FF", "warning": "#FFA800", "danger": "#F64E60", "light": "#F3F6F9", "dark": "#212121" }, "light": { "white": "#ffffff", "primary": "#1BC5BD", "secondary": "#ECF0F3", "success": "#C9F7F5", "info": "#E1E9FF", "warning": "#FFF4DE", "danger": "#FFE2E5", "light": "#F3F6F9", "dark": "#D6D6E0" }, "inverse": { "white": "#ffffff", "primary": "#ffffff", "secondary": "#212121", "success": "#ffffff", "info": "#ffffff", "warning": "#ffffff", "danger": "#ffffff", "light": "#464E5F", "dark": "#ffffff" } }, "gray": { "gray-100": "#F3F6F9", "gray-200": "#ECF0F3", "gray-300": "#E5EAEE", "gray-400": "#D6D6E0", "gray-500": "#B5B5C3", "gray-600": "#80808F", "gray-700": "#464E5F", "gray-800": "#1B283F", "gray-900": "#212121" } }, "font-family": "Poppins" };</script>
		<!--end::Global Config-->
		<!--begin::Global Theme Bundle(used by all pages)-->
		<!-- <script src="../assets/plugins/global/plugins.bundle.js"></script> -->
		<script src="../assets/plugins/custom/prismjs/prismjs.bundle.js"></script>
		<script src="../assets/js/scripts.bundle.js"></script>
		<!--end::Global Theme Bundle-->
		<!--begin::Page Scripts(used by this page)-->
		<!-- <script src="../assets/js/pages/custom/login/login-general.js"></script> -->
		<!--end::Page Scripts-->
	</body>
	<!--end::Body-->
</html>