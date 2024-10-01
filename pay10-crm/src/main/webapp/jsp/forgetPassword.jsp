<%@ taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html
  PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
<base href=""/>
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

 <%--  <script src="../js/jquery.js"></script>
  <script src="../js/captcha.js"></script>
  <script src="../js/login.js"></script> --%>

  <script src="../js/jquery.js"></script>
		<script src="../js/index.js"></script>
		<script src="../assets/plugins/global/plugins.bundle.js"></script>
	<script src="../assets/js/scripts.bundle.js"></script>


  <link href="../css/forgetPassword.css" rel="stylesheet" type="text/css">
 <%--  <script src="../js/forgetPassword.js"></script> --%>

<style>
  .register-page .card-signup .form-group {
    margin: 25px 0 0 7px !important;
    padding-bottom: 0;
}
button#kt_password_reset_submit{
    width: 303px !important;
  font-family: 'Roboto', sans-serif;
  text-transform: uppercase;
  letter-spacing: 2.5px;
  font-weight: 500 !important;
  /* background: linear-gradient(45deg, #fa3f47, #fdca43); */
  /*background: linear-gradient(45deg, #041fff, #fdca43);*/
  background-color: #234B7A !important;
  border: none;
  /* border-radius: 11px; */
  box-shadow: 0px 8px 15px rgb(0 0 0 / 10%) !important;
  transition: all 0.3s ease 0s;
  cursor: pointer;
  outline: none !important;
}
  

.button:hover {
  background-color: #2EE59D;
  box-shadow: 0px 15px 20px rgba(46, 229, 157, 0.4);
  color: #fff;
  transform: translateY(-7px);
}

</style>
</head>

<body>

  <!-- <div id="loader-wrapper" style="width: 100%; height: 100%; display:none;">
    <div id="loader"></div>
  </div>
  <div id="loadingInner" display="none">
    <img id="loading-image-inner" src="../image/sand-clock-loader.gif"
         alt="BUSY..." />
  </div> -->

      <div class="signin d-flex align-items-center" style="height: 100vh;">
        <div class="container text-center">
            <div class="row justify-content-center">
                <div class="col-lg-8 col-md-6 col-sm-12"> 
                    <img src="../assets/media/images/paylogo1.svg" alt="Pay10 Logo" class="logo-sign">
                    <div class="card my-4">
                        <div class="card-body my-3 p-lg-15 p-sm-8 p-md-8">
                            <div class="row p-lg-15 p-sm-8 p-md-8">
                                <div class="col">
                                    <div class="d-flex flex-column align-items-center justify-content-center mb-4">
                                        <h3>Forgot Password</h3>
                                        <p>Enter your registered email address. We will send you a link to reset your password !</p>
                                    </div>
                                    <s:form class="form w-100" novalidate="novalidate" id="kt_password_reset_form" action="">
                                        <div class="fv-row mb-8 form-group d-flex flex-column align-items-start ">
                                            <label class="d-flex align-items-center mb-2">
                                                <span class="required">Email ID</span>
                                            </label>
                                            <input type="text" placeholder="Email ID" name="emailId" class="form-control form-control-solid"
                                            id="emailId"
                              				autocomplete="off"
                              				onKeyDown="if(event.keyCode === 32)return false;"  />
                                        </div>
                                        <div class="d-flex flex-wrap justify-content-center pb-lg-0">
                                            <button type="button" id="kt_password_reset_submit" class="btn p-2 w-100 fs-5 fw-semibold">
                                                <span class="indicator-label">SEND</span>
                                                <span class="indicator-progress">Please wait...
                                                <span class="spinner-border spinner-border-sm align-middle ms-2"></span></span>
                                            </button>                               
                                        </div>
                                    </s:form>
                                    
                                    
                                </div>
                                <div class="text-center" style="margin-top: 50px;">
                          	<label class="form-check-label">

                            Already have an account?
                            <s:a action="index">Login here</s:a>
                          </label>
                        </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
   

  <!--End Modal to Invalid user alert-->
<%--   <script src="../js/customforgot.js"></script> --%>
    <script>
        "use strict";
        var KTAuthResetPassword = function() {
            var t, e, i,errorText,elements;
            var count =0;
            return {
                init: function() {
                    t = document.querySelector("#kt_password_reset_form"),
                    e = document.querySelector("#kt_password_reset_submit"),
                    i = FormValidation.formValidation(t, {
                        fields: {
                            emailId: {
                                validators: {
                                    regexp: {
                                        regexp: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
                                        message: "Enter valid email address"
                                    },
                                    notEmpty: {
                                        message: "Email address is required"
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
                    e.addEventListener("click", (function(r) {
                        r.preventDefault(),
                        i.validate().then((function(g) {
                            "Valid" == g ? (e.setAttribute("data-kt-indicator", "on"),
                            e.disabled = !0,
                             setTimeout((function() {
                            	restPasswordLink(),
                                e.removeAttribute("data-kt-indicator"),
                                e.disabled = !1,
                                t.querySelector('[name="emailId"]').value = ""
                            }
                            ), 1500)
                            ): 
                            (errorText ="",
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
                        ));
                        
                    }
                    ))
                    
                }
            }
        }();
        KTUtil.onDOMContentLoaded((function() {
            KTAuthResetPassword.init()
        }
        ));

function restPasswordLink()
{
	var emailId = document.getElementById("emailId").value.trim();
	$.ajax({
	      type: "POST",
	      url: "resetPasswordEmailAction",
	      data: {"emailId": emailId, "struts.token.name": "token",},
	      success: function (data) {

	        //document.getElementById("loadingInner").style.display = "none";
	        // document.getElementById("loading").style.display = "none";
	        //$('#submit').attr("disabled", false);
	        //$('#generateResetPasswordLink').removeAttr("href");
	        //$('#generateResetPasswordLink').prop("onclick", null).off("click");
	        var flag = (data["Invalid request"] != null) ? false : true;
	        var response = (flag ?(data.response): (data["Invalid request"].response[0]));
	        if (null == response) {
	        	Swal.fire({
                    text: "Sorry, looks like there are some errors detected, please try again.",
                    icon: "error",
                    buttonsStyling: !1,
                    confirmButtonText: "Ok, got it!",
                    customClass: {
                        confirmButton: "btn btn-primary"
                    }
                });
	        }else{
		        var icontype = ((response.indexOf("Reset password link sent")!== -1)?"success":"error");
	        	Swal.fire({
                    text: response,
                    icon: icontype,
                    buttonsStyling: !1,
                    confirmButtonText: "Ok, got it!",
                    customClass: {
                        confirmButton: "btn btn-primary"
                    }
                });
		        }
	       /*  if (data.errorCode == "314")
	          window.location.href = "index";
	        $('#submit').attr("disabled", false); */
	      }
	    });
}
    </script>



</body>

</html>