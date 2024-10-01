<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0" />
	<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
	<title>Sign Up</title>
	<link rel="icon" href="../image/98x98.png">
	<!-- stylesheet -->
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
	<!-- javascripts -->
	<script src="../js/core/jquery.min.js"></script>
	<script src="../js/jquery.minshowpop.js"></script>
	<script src="../js/jquery.formshowpop.js"></script>
	<script src="../js/jquery.popupoverlay.js"></script>
	<script src="../js/commonValidate2.js"></script>

	<script src="../js/signup.js"></script>
	<link href="../css/signup.css" rel="stylesheet">

</head>

<body class="off-canvas-sidebar body_color">
	<div id="popup" style="display: none;">
		<div class="modal-dialog" style="width: 400px;">
			<!-- Modal content-->
			<div class="modal-content"
				style="background-color: transparent; border-radius: 13px; -webkit-box-shadow: 0px 0px 0px 0px; -moz-box-shadow: 0px 0px 0px 0px; box-shadow: 0px 0px 0px 0px; box-shadow: 0px;">
				<div id="1" class="modal-body"
					style="background-color: #ffffff; border-radius: 13px; -webkit-box-shadow: 0px 0px 0px 0px; -moz-box-shadow: 0px 0px 0px 0px; box-shadow: 0px 0px 0px 0px; box-shadow: 0px;">

					<table class="detailbox table98" cellpadding="20">
						<tr>
							<th colspan="2" width="16%" height="30" align="left" style="background-color: #496cb6; color: #ffffff; border-top-right-radius: 13px;border-top-left-radius: 13px;border-bottom-right-radius: 13px;
                                 border-bottom-left-radius: 13px;padding: 0px 0px 0px 8px;font-size: 14px;">Select Sub
								Category</th>
						</tr>
						<tr>
							<td colspan="2" height="30" align="left">
								<div id="radiodiv">

								</div>
							</td>
						</tr>
						<tr>
							<td colspan="2" height="30" align="left">
								<div id="radioError">
								</div>
							</td>
						</tr>
						<tr>
							<td colspan="2"><input type="submit" value="Done" onclick="selectSubcategory()"
									class="btn btn-success btn-sm"
									style="margin-left:38%;width:21%;height:100%;margin-top:1%;" /></td>
						</tr>
					</table>

				</div>
			</div>
		</div>
	</div>

	<div class="wrapper wrapper-full-page">
		<div class="page-header register-page header-filter" filter-color="black">
			<!-- style="background-image: url('../../assets/img/register.jpg')" -->
			<div class="d-flex flex-column flex-root">
				<!--begin::Login-->
				<div class="login login-4 login-signin-on d-flex flex-row-fluid" id="kt_login">
					<div class="d-flex flex-center flex-row-fluid bgi-size-cover bgi-position-top bgi-no-repeat" style="background-image: url('../assets/media/bg/bg-3.jpg');">
						<div class="login-form text-center p-7 position-relative overflow-hidden">
						<div class="card card-signup" style="
							overflow: auto;">
							<div class="d-flex flex-center mb-15">
								<a href="#">
									<img src="../image/66x46.png" class="max-h-75px" alt="" />
								</a>
							</div>
							  <div class="mb-20">
								<h3>Sign Up</h3>
								<div class="text-muted font-weight-bold">Enter your details to create your account</div>
							  </div>
							<!-- <h2 class="card-title text-center">Register</h2> -->
							<div class="card-body">
								<div class="row">

									<div class="col-md-12 mr-auto">


										<!-- <tr><td align="center" valign="bottom" class="sigNew"><img src="../image/IRCT IPAY.png"/></td></tr> -->

										<div class="signupbox">
											
											<s:form action="signup" id="formname"
												onsubmit="return validateMyForm(event);">
												<s:token />


												<span id="error2"></span>
												<s:actionmessage />

												<div class="form-group has-default">
													<div class="dropdown bootstrap-select show-tick dropup"
														style="width: 100%;">

														<button type="button"
															class="btn dropdown-toggle bs-placeholder select-with-transition"
															data-toggle="dropdown" role="button"
															title="Sign Up As Merchant" aria-expanded="false">
															<div class="filter-option">
																<s:actionmessage />
																<div class="filter-option-inner">
																	<s:select name="userRoleType" id="userRoleType"
																		headerKey="1"
																		list="#{'merchant':'Sign up as Merchant','reseller':'Sign up as Reseller'}"
																		class="form-control" style="background: white !important;border-bottom: 1px solid #e5e5e5;color: #000000;"> </s:select>

																</div>
															</div>
															<!-- <div class="ripple-container"></div> -->
														</button>

													</div>
												</div>
												 <div id="tdIndustryType" >
												<div class="form-group has-default">
													<div class="dropdown bootstrap-select show-tick dropup"
														style="width: 100%;">

														<button type="button"
															class="btn dropdown-toggle bs-placeholder select-with-transition"
															data-toggle="dropdown" role="button" title="Select Category"
															aria-expanded="false">
															<div class="filter-option">
																<div class="filter-option-inner">
																	<s:select name="industryCategory"
																		id="industryCategory" headerKey=""
																		headerValue="Select Category*"
																		value="%{industryCategory}"
																		list="industryCategoryList"
																		class="form-control"
																		onchange="categoryChange()" onblur="categoryOnBlur()" style="background: white !important;border-bottom: 1px solid #e5e5e5;color: #000000;" autocomplete="off">
																	</s:select>
																</div>
															</div>

														</button>

													</div>
												</div>
												<p class="errorSec errorCategory"><span>Please Select Category</span>
												</p>
												<!-- <div class="ripple-container"></div> -->
												<div id="subcategorydiv" style="display: none;" disabled="true">
													<div class="form-group has-default">
														<div class="input-group">
															<div class="input-group-prepend">

															</div>
													<s:textfield id="subcategory" name="industrySubCategory"
														value="%{industrySubCategory}" class="form-control"
														placeholder="Sub category*" autocomplete="off"
														readonly="readonly"></s:textfield>
														</div>
														</div>
												</div>
												</div>

												<div class="form-group has-default">
													<div class="input-group">
														<div class="input-group-prepend">

														</div>
														<s:textfield id="businessName" maxlength="256"
															name="businessName" class="form-control"
															placeholder="Business Name*" autocomplete="off" ondrop="return false;"
															onkeydown="return businessNameValid(event);"
															onkeypress="businessNameonBlur()" />

													</div>
												</div>
												<p class="errorSec errorBusninessName"><span>Please Enter Valid Business
														Name</span></p>
												<div class="form-group has-default">
													<div class="input-group">
														<div class="input-group-prepend">

														</div>
														<s:textfield id="emailId" name="emailId" class="form-control"
															placeholder="Email*" maxlength="50" onkeypress="emailOnBlur()"
															autocomplete="off" />

													</div>
												</div>
												<p class="errorSec errorEmail" id="emailError"><span>Please Enter Valid Email</span></p>

												<div class="form-group has-default ">
													<div class="input-group">
														<div class="input-group-prepend">

														</div>
														<s:textfield id="mobile"
															onkeydown="return numOnlyInTextInput(event);"
															onblur="phoneNoOnBlur()" name="mobile" class="form-control"
															maxlength="10" placeholder="Phone*" autocomplete="nope" />

													</div>
												</div>
												<p class="errorSec errorPhone"><span>Please Enter Valid Phone
														Number</span></p>

												<div class="form-group has-default bmd-form-group">
													<div class="input-group">
														<div class="input-group-prepend">

														</div>

														<s:textfield id="password" name="password" type="password"
															class="form-control" placeholder="Password*" maxlength="32"
															autocomplete="nope" onblur="passwordOnBlur()" />

													</div>
												</div>
												<p class="errorSec errorPassword"><span>Please Enter Valid
														Password</span></p>

												<div class="form-group has-default bmd-form-group">
													<div class="input-group">
														<div class="input-group-prepend">

														</div>
														<s:textfield id="confirmPassword" name="confirmPassword"
															type="password" class="form-control"
															placeholder="Confirm Password*" autocomplete="off"
															onblur="confirmPasswordBlur()" maxlength="32" />

													</div>
												</div>
												<p class="errorSec passwordNotMatch"><span>Password Doesn't Match</span>
												</p>

												<div class="form-group has-default bmd-form-group">
												<div class="rederror" id="error4"><s:fielderror class="redvalid">
															<s:param>invalidOtp</s:param>
													</s:fielderror><br></div>
														<div class="input-group">
															<div class="input-group-prepend">
														<!-- <div class="input-group-text"> -->
															<s:textfield name="otp" maxlength="6" type="text"
																class="form-control" id="otp" placeholder="Enter OTP here"
																autocomplete="off" 
																 />
														<!-- </div> -->
															<span class="rederror errorEmail" id="otpError">Please Enter
																OTP</span>
															<span class="gen_otp"><a href="#" onClick="genrateOTP()"
																	id="generateOtpBtn">Generate
																	OTP</a></span>
															<p class="otpMsg" id="otpMsg"></p>
															</div>
														</div>
													</div>

												<!-- <div class="form-group has-default bmd-form-group">
													<div class="input-group">
														<div class="input-group-prepend">

														</div>
														<s:textfield name="captcha" maxlength="6" type="text" class="form-control"
															id="captcha" placeholder="Enter Captcha Code"
															autocomplete="off" onblur="captchaOnBlur()"
                              onKeyDown="if(event.keyCode === 9) captchaOnBlur();" onkeypress="if(event.keyCode != 9) return isNumberKey(event);" />
														<span><img id="captchaImage"
																class="captchaImage" /></span><span>
															<s:textfield id="Btnaccescd" type="button"
																class="refreshbutton" onclick="refresh()" value="" />
														</span>
							<p class="errorSec errorCaptcha" id="firstTabCaptcha">Invalid Captcha</p>
                            <p class="errorSec errorEmptyCaptcha" id="captchaEmpty">Please enter displayed captcha</p>
													</div>

												</div>
												<div class="rederror" id="error3"></div> -->


												<div class="text-center">
													<s:submit value="Sign Up" method="submit" onchange="emailCheck()"
													onClick='checkOtpValidation(event)' class="btn btn-primary btn-round mt-4"> </s:submit>

												</div>
												<div class="text-center" style="margin-top: 15px;">
													<label class="form-check-label">

														Already have an account?
														<s:a action="index">Login here</s:a>
													</label>
												</div>



											</s:form>

											</table>


										</div>


									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	 <!--Start Modal to generate Otp error-->
	 <div class="modal fade" id="modaltogenerateotpSignUp" role="dialog">
		<div class="modal-dialog">

		  <!-- Modal content-->
		  <div class="modal-content">
			<div class="modal-header">

			</div>
			<div class="modal-body">
			  <p class="enter_otp">Please enter valid details to generate OTP.</p>
			</div>
			<div class="modal-footer" id="modal_footer">
			  <button type="button" class="btn btn-primary btn-round mt-4 submit_btn" data-dismiss="modal">Ok</button>
			</div>
		  </div>

		</div>
	  </div>
	  <!--End Modal to generate Otp error-->

	  <!--Start Modal to generate Otp error-->
	  <div class="modal fade" id="modaltosendOTPSignUp" role="dialog">
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
	  <div class="modal fade" id="invaliduserSignUpmodal" role="dialog">
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
    <script src="../js/customsignup.js"></script>

	<script src="../js/core/popper.min.js"></script>
	<script src="../js/core/bootstrap-material-design.min.js"></script>
	<script src="../js/plugins/perfect-scrollbar.jquery.min.js"></script>
	<!--  Google Maps Plugin    -->
	<!-- <script src="https://maps.googleapis.com/maps/api/js?key=YOUR_KEY_HERE"></script> -->
	<!-- Chartist JS -->
	<script src="../js/plugins/chartist.min.js"></script>
	<!--  Notifications Plugin    -->
	<script src="../js/plugins/bootstrap-notify.js"></script>
	<!-- Control Center for Material Dashboard: parallax effects, scripts for the example pages etc -->
	<script src="../js/material-dashboard.js?v=2.1.0" type="text/javascript"></script>
	<!-- Material Dashboard DEMO methods, don't include it in your project! -->
	<!-- <script src="../../assets/demo/demo.js"></script> -->
	<script>var HOST_URL = "https://preview.keenthemes.com/metronic/theme/html/tools/preview";</script>
	<!--begin::Global Config(global config for global JS scripts)-->
	<script>var KTAppSettings = { "breakpoints": { "sm": 576, "md": 768, "lg": 992, "xl": 1200, "xxl": 1200 }, "colors": { "theme": { "base": { "white": "#ffffff", "primary": "#1BC5BD", "secondary": "#E5EAEE", "success": "#1BC5BD", "info": "#6993FF", "warning": "#FFA800", "danger": "#F64E60", "light": "#F3F6F9", "dark": "#212121" }, "light": { "white": "#ffffff", "primary": "#1BC5BD", "secondary": "#ECF0F3", "success": "#C9F7F5", "info": "#E1E9FF", "warning": "#FFF4DE", "danger": "#FFE2E5", "light": "#F3F6F9", "dark": "#D6D6E0" }, "inverse": { "white": "#ffffff", "primary": "#ffffff", "secondary": "#212121", "success": "#ffffff", "info": "#ffffff", "warning": "#ffffff", "danger": "#ffffff", "light": "#464E5F", "dark": "#ffffff" } }, "gray": { "gray-100": "#F3F6F9", "gray-200": "#ECF0F3", "gray-300": "#E5EAEE", "gray-400": "#D6D6E0", "gray-500": "#B5B5C3", "gray-600": "#80808F", "gray-700": "#464E5F", "gray-800": "#1B283F", "gray-900": "#212121" } }, "font-family": "Poppins" };</script>
	<!--end::Global Config-->
	<!--begin::Global Theme Bundle(used by all pages)-->
	<!-- <script src="../assets/plugins/global/plugins.bundle.js"></script> -->
	<script src="../assets/plugins/custom/prismjs/prismjs.bundle.js"></script>
	<script src="../assets/js/scripts.bundle.js"></script>

</body>

</html>
