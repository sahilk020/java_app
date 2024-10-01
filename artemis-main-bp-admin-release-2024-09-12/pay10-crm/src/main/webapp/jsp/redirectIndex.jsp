<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html
  PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%
	String error = request.getParameter("error");
	if (error == null || error == "null") {
		error = "";
	}
%>

<html lang="en" dir="ltr">
	<!--begin::Head-->
	<head><base href="" >
		<meta charset="utf-8" />
		<head><base href=""/>
		<title>BestPay</title>
		<meta charset="utf-8" />
		<meta name="viewport" content="width=device-width, initial-scale=1" />
		<link rel="shortcut icon" href="../assets/media/images/paylogo.svg" />
		<!--begin::Fonts-->
		<link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Inter:300,400,500,600,700" />
		<!--end::Fonts-->
		<!--begin::Vendor Stylesheets(used by this page)-->
		<link href="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.css" rel="stylesheet" type="text/css" />
		<link href="../assets/plugins/custom/datatables/datatables.bundle.css" rel="stylesheet" type="text/css" />
		<!--end::Vendor Stylesheets-->
		<!--begin::Global Stylesheets Bundle(used by all pages)-->
		<link href="../assets/plugins/global/plugins.bundle.css" rel="stylesheet" type="text/css" />
		<link href="../assets/css/style.bundle.css" rel="stylesheet" type="text/css" />
		<link href="../assets/css/custom/custom-style.css" rel="stylesheet" type="text/css" />
		<script src="../js/login.js"></script>

		<script src="../js/jquery.js"></script>
		<script src="../js/index.js"></script>
		<script src="../assets/plugins/global/plugins.bundle.js"></script>
	<script src="../assets/js/scripts.bundle.js"></script>
	</head>
	<style>
		#wwctrl_kt_login_signin_submit{
			text-align: center !important;
		}
/* 		.errorMessage {
  font-size: 11px;
  font-weight: 600;
  color: #ff0000;
  display: block;
  list-style: none;
  position: absolute;
top: 52px;
} */
/* .redvalid {
    font-size: 14px;
    font-weight: 400;
    color: #ff0000;
    display: block;
    list-style: none;
    position: absolute;
	top: 175px;
	left: 0px;
} */

.redvalid {
    font-size: 0.999rem;
    font-weight: 400;
    color: #f1416c;
    display: block;
    list-style: none;
    /* position: absolute; */
    top: 175px;
    left: 0px;
}



/* .error {
  font-size: 14px;
  font-weight: 400;
  color: #ff0000;
  display: none;
  margin: 0;
  padding: 0;
  width: 100%;
  height: auto;
  position: relative;
  text-align: left;
  margin-bottom: 5px;
}

#error2 {
  color: red;
} */
	</style>
	<!--end::Head-->
	<!--begin::Body-->
	<body id="kt_body">
		<!--begin::Main-->
<div class="signin d-flex align-items-center" style="height: 100vh;">
        <div class="container text-center">
            <div class="row justify-content-center">
                <div class="col-lg-8 col-md-6 col-sm-12">
                    <img src="../assets/media/images/paylogo.svg" alt="Pay10 Logo" class="logo-sign">
                    <div class="card my-4">
                      <div class="card-body my-3 p-lg-15 p-sm-8 p-md-8">
                          <div class="row">
                              <div class="col p-lg-15 p-sm-8 p-md-8">
                                  <div class="d-flex flex-column align-items-center justify-content-center">
                                      <h3>Sign In to BestPay </h3>
                                      <p>Enter the details to login to your  account</p>
                                  </div>
                                  <s:form name=" " action="login" method="post" class="form w-100" 
                                  novalidate="novalidate" id="kt_sign_in_form" onselectstart="return false" oncontextmenu="return false;">
                                      <s:token />
					
							  <span>
								<s:actionmessage /></span>
                                      <!--end::Separator-->
                                      <!--begin::Input group=-->
                                      <div class="fv-row mb-8 form-group d-flex flex-column align-items-start ">
                                          <!--begin::Email-->
                                          <label class="d-flex align-items-center mb-2">
                                              <span class="required">Email ID</span>
                                          </label>
                                          <input type="text" placeholder="Email ID" name="emailId" id="emailId" class="form-control form-control-solid"
                                          autocomplete="off"
                             			 onKeyDown="if(event.keyCode === 32)return false" autofocus />
                             			<%--  <span id="error2"></span> --%>
                                          <!--end::Email-->
                                      </div>
                                     <!--  <p class="error errorEmail">Please Enter Valid User id</p> -->
                                       <input type="hidden" name="loginType" value="pwd" />
                                      <!--end::Input group=-->
                                      <div class="fv-row mb-3 form-group d-flex flex-column align-items-start ">
                                          <!--begin::Password-->
                                          <div class="w-100 d-flex justify-content-between align-items-center">
                                              <label class="d-flex align-items-center mb-2">
                                                  <span class="required">Password</span>
                                              </label>


                                          </div>
                                          <input type="password" placeholder="Password" id="password" name="password" class="form-control form-control-solid"
                                           autocomplete="off" maxlength="32" />

                                          <!--end::Password-->
                                         <%--  <p class="error errorPassword" style="margin-bottom:10px !important;">Please Enter Valid
									Password
								  </p>
								  <span id="error2"></span> --%>
								  <input type="hidden" value="pwd" name="data" />
                                      </div>
                                      <div class="text-align-initial">
                                        <s:a action="forgetPassword"  id="kt_login_forgot" class="text-hover-primary">Forgot Password?</s:a>
                                    </div>

                                      <!--end::Input group=-->
                                      <!--begin::Submit button-->
                                      <div class="mt-10" >
                                          <button type="submit" id="kt_sign_in_submit" class="btn fs-5 fw-semibold p-2"
                                         >
                                              <!--begin::Indicator label-->
                                              <span class="indicator-label">LOGIN</span>
                                              <!--end::Indicator label-->
                                              <!--begin::Indicator progress-->
                                              <span class="indicator-progress">Please wait...
                                              <span class="spinner-border spinner-border-sm align-middle ms-2"></span></span>
                                              <!--end::Indicator progress-->
                                          </button>
                                      </div>
                                      <s:fielderror class="redvalid">
									<s:param>emailId</s:param>
									<s:param>password</s:param>
									</s:fielderror>
                                      <!--end::Submit button-->
                                  </s:form>
                              </div>
                          </div>
                      </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
			<!--end::Login-->
		</div>
		
		  <!--End Modal to generate Otp error-->
		
		  <!--Start Modal to generate Otp error-->
		  <!--End Modal to generate Otp error-->
		  <!--Start Modal for Invalid user alert-->
		
		  <!--End Modal to Invalid user alert-->
		
      <script src="../js/customindex.js"></script>
		<script>
        "use strict";
        var KTSigninGeneral = function() {
            var e, t, i,errorText,elements;
            var count =0;
            return {
                init: function() {
                    e = document.querySelector("#kt_sign_in_form"),
                    t = document.querySelector("#kt_sign_in_submit"),
                    i = FormValidation.formValidation(e, {
                        fields: {
                        	emailId: {
                                validators: {
                                    regexp: {
                                        regexp: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
                                        message: "The value is not a valid email address"
                                    },
                                    notEmpty: {
                                        message: "Email address is required"
                                    }
                                }
                            },
                            password: {
                                validators: {
                                	/*  regexp: {
                                         regexp: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!@,_+/=]).{8,32}$/g,
                                         message: "The value is not a valid password"
                                     }, */
                                    notEmpty: {
                                        message: "Password is required"
                                    }
                                }
                            }
                        },
                        plugins: {
                            trigger: new FormValidation.plugins.Trigger,
                            bootstrap: new FormValidation.plugins.Bootstrap5({
                                rowSelector: ".fv-row",
                                eleInvalidClass: "",
                                eleValidClass: ""
                            })
                        }
                    }),
                    t.addEventListener("click", (function(n) {
                        n.preventDefault(),
                        i.validate().then((function(g) {
                            "Valid" == g ? (t.setAttribute("data-kt-indicator", "on"),
                            t.disabled = !0,
                            setTimeout((function() {
                                t.removeAttribute("data-kt-indicator"),
                                t.disabled = !1,
                                e.submit();
                           /*      Swal.fire({
                                    text: "You have successfully logged in!",
                                    icon: "success",
                                    buttonsStyling: !1,
                                    confirmButtonText: "Ok, got it!",
                                    customClass: {
                                        confirmButton: "btn btn-primary"
                                    }
                                }).then((function(t) {
                                    if (t.isConfirmed) {
                                        e.querySelector('[name="emailId"]').value = "",
                                        e.querySelector('[name="password"]').value = "";
                                        var i = e.getAttribute("data-kt-redirect-url");
                                        alert(i);
                                        i && (location.href = i)
                                        
                                    }
                                }
                                )) */
                            }
                            ), 2e3)) :( 
                            	errorText ="",
                            	elements = document.getElementsByClassName("invalid-feedback"),
                        		elements.forEach(function(f){
                            	if(f.querySelector("div") && f.querySelector("div")!="undefined" && f.querySelector("div")!=null){
                            		errorText=errorText+f.querySelector("div").innerHTML+". ";
                                	
                            	}
                            	}),
                                Swal.fire({
                                text: errorText,
                                icon: "error",
                                buttonsStyling: !1,
                                confirmButtonText: "Ok, got it!",
                                customClass: {
                                    confirmButton: "btn btn-primary"
                                }
                            })
                        )
                        }
                        ))
                    }
                    ))
                }
            }
        }();
        KTUtil.onDOMContentLoaded((function() {
            KTSigninGeneral.init()
        }
        ));

    </script>
	</body>
	<!--end::Body-->
</html>