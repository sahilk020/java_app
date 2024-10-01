<%@ taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html
  PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
  <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
  <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0" />
  <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
  <title>Forgot Password</title>
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

  <script src="../js/jquery.js"></script>
  <script src="../js/captcha.js"></script>
  <script src="../js/login.js"></script>


  <link href="../css/forgetPassword.css" rel="stylesheet" type="text/css">
  <script src="../js/forgetPassword.js"></script>
  
<style>
  .register-page .card-signup .form-group {
    margin: 25px 0 0 7px !important;
    padding-bottom: 0;
}
</style>
</head>
<!-- <body onload="return generateCaptcha();"> -->

<body class="off-canvas-sidebar body_color">
  <!-- <div id="loader-wrapper" style="width: 100%; height: 100%;">
    <div id="loader"></div>
  </div> -->

  <div id="loader-wrapper" style="width: 100%; height: 100%; display:none;">
    <div id="loader"></div>
  </div>
  <div id="loadingInner" display="none">
    <img id="loading-image-inner" src="../image/sand-clock-loader.gif"
         alt="BUSY..." />
  </div>

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
                  <div class="mb-20">
                  <h3>Forgotten Password ?
                  </h3>
                  <div class="text-muted font-weight-bold">Enter your email to reset your password
                  </div>
                  </div>
              <!-- <h2 class="card-title text-center">Reset Your Password</h2> -->
             
           

                


                    <!-- <tr><td align="center" valign="bottom" class="sigNew"><img src="../image/IRCT IPAY.png"/></td></tr> -->

                    <div class="signupbox">
                      <!-- <div class="social text-center">

                        <a href="https://www.pay10.com/#"
                          class="btn btn-just-icon btn-round btn-facebook">
                          <i class="fa fa-linkedin"> </i>
                        </a>

                      </div> -->

                      <p id="dataValue" class="invalid_reset"></p>

                      <s:form id="resetPassword" action="validateOtpAction" autocomplete="off">
                        <s:token />

                        <span>
                          <s:actionmessage /></span>


                        <div class="form-group has-default">
                          <div class="input-group">
                           
                            <s:textfield name="emailId" type="text" class="form-control h-auto form-control-solid py-4 px-8" id="emailId"
                              placeholder="User Id" autocomplete="off" onblur="emailCheck()"
                              onKeyDown="if(event.keyCode === 32)return false;" /><span class="rederror errorEmail"
                              id="emailError">Please Enter User Id</span>
                          </div>
                        </div>

                        <div class="form-group has-default bmd-form-group">
                            <span class="gen_forget_otp"><a href="#" onClick="genrateResetPasswordLink()"
                                                            id="generateResetPasswordLink">Send Reset Password Link</a></span>
                          <div class="input-group" style="visibility: hidden">
                            <div class="input-group-prepend" style="width:100%">
                              <div style="display:none">
                              <s:textfield name="otp" maxlength="6" type="text" class="form-control" id="otp"
                                placeholder="Enter OTP here" autocomplete="off" onkeypress="checkOtp()"
                                onblur="removeOtpErr()" />
                              </div>
                              <span hidden class="rederror errorEmail" id="otpError">Please Enter OTP</span>

                              <span hidden class="gen_forget_otp"><a href="#" onClick="genrateOTP()"
                                  id="generateOtpBtn">Generate
                                  OTP</a></span>
                              <span class="gen_forget_otp"><a href="#" onClick="genrateResetPasswordLink()"
                                                              id="generateResetPasswordLink">Send Reset Password Link</a></span>
                              <p class="otpMsg" id="otpMsg"></p>
                            </div>
                          </div>
                        </div>


                        <!-- <div class="form-group has-default bmd-form-group">
                          <div class="input-group">
                            <div class="input-group-prepend">

                            </div>
                            <s:textfield name="captcha" type="text" class="form-control" id="captcha" maxlength="6"
                              placeholder="Enter Captcha Code" autocomplete="off" />
                            <span><img id="captchaImage" class="captchaImage" /></span><span>
                              <s:textfield id="Btnaccescd" type="button" class="refreshbutton" value="" />
                              <p class="rederror errorCaptcha" id="enterCaptcha">Please enter captcha</p>
                              <p class="rederror errorCaptcha">Invalid Captcha</p>
                            </span>
                            <div class="rederror" id="error3"></div>
                          </div>
                        </div> -->



                        <div style="visibility: hidden" class="text-center">
                          <s:submit id="submit" value="Submit" class="btn btn-primary btn-round mt-4"
                            onClick='checkOtpValidation(event'></s:submit>
                        </div>
                        <div class="text-center" style="margin-top: 15px;">
                          <label class="form-check-label">

                            Already have an account?
                            <s:a action="index">Login here</s:a>
                          </label>
                        </div>

                      </s:form>
                    </div>

                 
              
            </div>
          </div>
        </div>
      </div>
   

  <!--Start Modal to generate Otp error-->
  <div class="modal fade" id="modaltogenerateotpReset" role="dialog">
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
  <div class="modal fade" id="modaltosendOTPReset" role="dialog">
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
  <div class="modal fade" id="invalidusermodalReset" role="dialog">
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
  <script src="../js/customforgot.js"></script>
  <script src="../js/core/popper.min.js"></script>

  <script src="../js/core/bootstrap-material-design.min.js"></script>
  <script src="../js/plugins/perfect-scrollbar.jquery.min.js"></script>
  <script src="../js/material-dashboard.js" type="text/javascript"></script>
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