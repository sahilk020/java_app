<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<base href=""/>
		<title>BPGATE</title>
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

   
		<%-- <script src="../js/index.js"></script> --%>
		<script src="../js/jquery.js"></script> 
		<script src="../js/jquery.min.js"></script> 
		<script src="../assets/plugins/global/plugins.bundle.js"></script>
	<script src="../assets/js/scripts.bundle.js"></script>
	<!--begin::Vendors Javascript(used by this page)-->
		<script src="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.js"></script>
		<script src="https://cdn.amcharts.com/lib/5/index.js"></script>
		<script src="https://cdn.amcharts.com/lib/5/xy.js"></script>
		<script src="https://cdn.amcharts.com/lib/5/percent.js"></script>
		<script src="https://cdn.amcharts.com/lib/5/radar.js"></script>
		<script src="https://cdn.amcharts.com/lib/5/themes/Animated.js"></script>
		<script src="https://cdn.amcharts.com/lib/5/map.js"></script>
		<script src="https://cdn.amcharts.com/lib/5/geodata/worldLow.js"></script>
		<script src="https://cdn.amcharts.com/lib/5/geodata/continentsLow.js"></script>
		<script src="https://cdn.amcharts.com/lib/5/geodata/usaLow.js"></script>
		<script src="https://cdn.amcharts.com/lib/5/geodata/worldTimeZonesLow.js"></script>
		<script src="https://cdn.amcharts.com/lib/5/geodata/worldTimeZoneAreasLow.js"></script>
		<script src="../assets/plugins/custom/datatables/datatables.bundle.js"></script>
		<!--end::Vendors Javascript-->
		<!--begin::Custom Javascript(used by this page)-->
		<script src="../assets/js/widgets.bundle.js"></script>
		<script src="../assets/js/custom/widgets.js"></script>
		<script src="../assets/js/custom/apps/chat/chat.js"></script>
		<script src="../assets/js/custom/utilities/modals/upgrade-plan.js"></script>
		<script src="../assets/js/custom/utilities/modals/create-app.js"></script>
		<script src="../assets/js/custom/utilities/modals/users-search.js"></script>
		<!--end::Custom Javascript-->
		<!--end::Javascript-->
	
<%-- <script src="../js/commanValidate.js"></script> --%> 
<%-- <script>
jQuery.noConflict(true);

</script> --%>

<script src="../js/aes.js"></script>
<style type="text/css">
/* Manual css s:actionmessage for this page */
.errorMessage {
	font: normal 11px arial;
	color: #ff0000;
	display: block;
	margin: -15px 0px 3px 0px;
	padding: 0px 0px 0px 0px;
}

.error2 {
	color: red;
}
#mismatchmsg{
	font-size: 16px;
    color: #496cb6;
    text-align: center;
    font-weight: 500;
}
.none{
	display: none !important;
}
/* #loader-wrapper{
	display: none;
} */
</style>
<script>
      function changPassSubmit(){    	 
    	    var oldP=$('#oldPassword').val();
			var newP=$('#newPassword').val();
			var conP=$('#confirmNewPassword').val();
			 
			var oldPass=CryptoJS.AES.encrypt(oldP, 'thisisaverysecretkey');
		
			var newPass=CryptoJS.AES.encrypt(newP, 'thisisaverysecretkey');
				
			var confirmNewPass=CryptoJS.AES.encrypt(conP, 'thisisaverysecretkey');
			//console.log(confirmNewPass.toString());
			
			document.getElementById('oldPassword').value=oldPass;
			document.getElementById('newPassword').value=newPass;
			document.getElementById('confirmNewPassword').value=confirmNewPass;  
			
    	  jQuery.ajax({
	  type: "POST",

		url : 'changePassword',
		
		data : {
			token : document.getElementsByName("token")[0].value,
			oldPassword : document.getElementById('oldPassword').value,
			newPassword : document.getElementById('newPassword').value,
			confirmNewPassword : document.getElementById('confirmNewPassword').value
		},
		success : function(data){
			
			var responsedata= data.response;
			var jsonObj=data["Invalid request"];
			if(jsonObj!=null){
			var oldpass=	jsonObj['oldPassword'];
			var newpass= jsonObj['newPassword'];
			var confirmpass= jsonObj['confirmNewPassword'];

			
			var errText = null;
			if(oldpass!=null){
				
				errText="Old Password : "+oldpass;
		
			}else if(newpass!=null){
				errText="New Password : "+newpass;
			
			}else if(confirmpass!=null){
				errText="Confirm Password : "+confirmpass;
			
			} 
	if(errText!=null)
		{
		Swal.fire({
            text: errText,
            icon: "error",
            buttonsStyling: !1,
            confirmButtonText: "Ok, got it!",
            customClass: {
                confirmButton: "btn btn-primary"
            }
        }).then((function(t) {
            if (t.isConfirmed) {
            	document.getElementById('oldPassword').value = '',
    			document.getElementById('newPassword').value='',
    			document.getElementById('confirmNewPassword').value=''
                
            }
        }
        ));
		}else if(newPassword.value!=confirmNewPassword.value || confirmNewPassword.value!=newPassword.value){
			
			Swal.fire({
	            text: "Password Criteria Mismatch please enter valid password.",
	            icon: "error",
	            buttonsStyling: !1,
	            confirmButtonText: "Ok, got it!",
	            customClass: {
	                confirmButton: "btn btn-primary"
	            }
	        }).then((function(t) {
	            if (t.isConfirmed) {
	            	document.getElementById('oldPassword').value = '',
	    			document.getElementById('newPassword').value='',
	    			document.getElementById('confirmNewPassword').value=''	                
	            }
	        }
	        ));
				
				
			}
			
		}else if(responsedata){
				if(responsedata.indexOf("Password has been successfully changed")!== -1){
				fields = "";
				Swal.fire({
		            text: responsedata,
		            icon: "success",
		            buttonsStyling: !1,
		            confirmButtonText: "Ok, got it!",
		            customClass: {
		                confirmButton: "btn btn-primary"
		            }
		        }).then((function(t) {
                    if (t.isConfirmed) {
                    	document.getElementById('oldPassword').value = '',
            			document.getElementById('newPassword').value='',
            			document.getElementById('confirmNewPassword').value='',
            			location.href = "loginResult";
                        
                    }
                }
                ));
				
			}else{
				fields = "";
					Swal.fire({
			            text: responsedata,
			            icon: "error",
			            buttonsStyling: !1,
			            confirmButtonText: "Ok, got it!",
			            customClass: {
			                confirmButton: "btn btn-primary"
			            }
			        }).then((function(t) {
			            if (t.isConfirmed) {
			            	document.getElementById('oldPassword').value = '',
			    			document.getElementById('newPassword').value='',
			    			document.getElementById('confirmNewPassword').value=''
			                
			            }
			        }
			        ));
			}
				
			}
			
			else if(newPassword.value == 0  || confirmNewPassword.value == 0 || oldPassword.value == 0){
				fields = "";
				Swal.fire({
		            text: "Please Enter all Mandatory Fields",
		            icon: "error",
		            buttonsStyling: !1,
		            confirmButtonText: "Ok, got it!",
		            customClass: {
		                confirmButton: "btn btn-primary"
		            }
		        }).then((function(t) {
		            if (t.isConfirmed) {
		            	document.getElementById('oldPassword').value = '',
		    			document.getElementById('newPassword').value='',
		    			document.getElementById('confirmNewPassword').value=''
		                
		            }
		        }
		        ));
			} else if(oldPassword.value!=oldpass.value){
				fields = "";
				Swal.fire({
		            text: "Please Enter the Valid Old Password",
		            icon: "error",
		            buttonsStyling: !1,
		            confirmButtonText: "Ok, got it!",
		            customClass: {
		                confirmButton: "btn btn-primary"
		            }
		        }).then((function(t) {
		            if (t.isConfirmed) {
		            	document.getElementById('oldPassword').value = '',
		    			document.getElementById('newPassword').value='',
		    			document.getElementById('confirmNewPassword').value=''
		                
		            }
		        }
		        ));
			} 
		},
		error : function(data){
			Swal.fire({
	            text: "Sorry, looks like there are some errors detected, please try again.",
	            icon: "error",
	            buttonsStyling: !1,
	            confirmButtonText: "Ok, got it!",
	            customClass: {
	                confirmButton: "btn btn-primary"
	            }
	        }).then((function(t) {
	            if (t.isConfirmed) {
	            	document.getElementById('oldPassword').value = '',
	    			document.getElementById('newPassword').value='',
	    			document.getElementById('confirmNewPassword').value=''
	                
	            }
	        }
	        ));
		}
			
		
	});
};
</script>


	 <%-- <script type="text/javascript">
	 jQuery(document).ready(function() {
		 jQuery('#updatepassword').attr("disabled", true);
			var fields = "#oldPassword, #newPassword, #confirmNewPassword";

			jQuery(fields).on('keypress', function() {
    if (allFilled()) {
    	jQuery('#updatepassword').removeAttr('disabled');
    } else {
    	jQuery('#updatepassword').attr('disabled', 'disabled');
    }
});

function allFilled() {
    var filled = true;
    jQuery(fields).each(function() {
        if (jQuery(this).val() == '') {
            filled = false;
        }
    });
    return filled;
}
        });
		</script>
		<script>
		function refresh(){
			oldPassword.value = null;
			newPassword.value = null;
			confirmNewPassword.value = null;

		}
		</script> --%>

</head>
<body>

				<div class="content d-flex flex-column flex-column-fluid" id="kt_content">
						<!--begin::Toolbar-->
						<div class="toolbar" id="kt_toolbar">
							<!--begin::Container-->
							<div id="kt_toolbar_container" class="container-fluid d-flex flex-stack">
								<!--begin::Page title-->
								<div data-kt-swapper="true" data-kt-swapper-mode="prepend" data-kt-swapper-parent="{default: '#kt_content_container', 'lg': '#kt_toolbar_container'}" class="page-title d-flex align-items-center me-3 flex-wrap mb-5 mb-lg-0 lh-1">
									<!--begin::Title-->
									<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">Change Password</h1>
									<!--end::Title-->
									<!--begin::Separator-->
									<span class="h-20px border-gray-200 border-start mx-4"></span>
									<!--end::Separator-->
									<!--begin::Breadcrumb-->
									<ul class="breadcrumb breadcrumb-separatorless fw-semibold fs-7 my-1">
										<!--begin::Item-->
										<li class="breadcrumb-item text-muted">
											<a href="home" class="text-muted text-hover-primary">Dashboard</a>
										</li>
										<!--end::Item-->
										<!--begin::Item-->
										<li class="breadcrumb-item">
											<span class="bullet bg-gray-200 w-5px h-2px"></span>
										</li>
										<!--end::Item-->
										<!--begin::Item-->
										<li class="breadcrumb-item text-dark">Change Password</li>
										<!--end::Item-->
									</ul>
									<!--end::Breadcrumb-->
								</div>
								<!--end::Page title-->
								
							</div>
							<!--end::Container-->
						</div>
						<!--end::Toolbar-->
						<!--begin::Post-->
						<div class="post d-flex flex-column-fluid" id="kt_post">
							<!--begin::Container-->
							<div id="kt_content_container" class="container-xxl">
                                <div class="row h-100 align-items-center">
                                    <div class="col">
                                        <div class="card">
                                            <div class="card-body">
                                                <div class="row g-9 mb-8">
                                                    <p class="text-gray-600 fs-5 fw-semibold">
                                                        <span style="color: #202f4b;" class="fs-3">Password Criteria</span>: Password must be minimum 8 and maximum 32 characters long, with special characters (! @ , _ + / =) , at least one upper case and one lower case alphabet and at least one numeric digit. Your new password must not be the same as any of your last four passwords.
                                                    </p>
                                                </div>
                                                <div class="col-12 d-flex justify-content-center mt-4 fs-2" style="color: #202f4b;">
                                                    <p class="fw-bold">Change Password</p>
                                                </div>
                                                <div class="row justify-content-center">
                                                    <div class="col-md-6">
                                                        
                                                        <!--begin::Form-->
                                                        <s:form id="changepassword" class="form" action="#" autocomplete="off">
                                                        <s:token/>
                                                        <s:actionmessage class="success success-text" theme="simple"/>
                                                        <span id="errorchangepass" style="font-size: 11px;
				  color: red;
				  position: absolute;
				  top: 29px;"></span>
                                                            <!--begin::Input group--->
                                                            <div class="fv-row mb-10">
                                                                <label class="required form-label fs-6 mb-2">Old Password</label>

                                                                <input onKeyDown="if(event.keyCode === 32)return false;" class="form-control form-control-lg form-control-solid" type="password" placeholder="" id="oldPassword" name="oldPassword" autocomplete="off" />
                                                            <span  class="error2" id="erroroldpass"></span>
                                                            </div>
                                                            
                                                            <!--end::Input group--->

                                                            <!--begin::Input group-->
                                                            <div class="mb-10 fv-row" data-kt-password-meter="true">
                                                                <!--begin::Wrapper-->
                                                                <div class="mb-1">
                                                                    <!--begin::Label-->
                                                                    <label class="form-label fw-semibold fs-6 mb-2 required">
                                                                        New Password
                                                                    </label>
                                                                    <!--end::Label-->

                                                                    <!--begin::Input wrapper-->
                                                                    <div class="position-relative mb-3">
                                                                        <input onKeyDown="if(event.keyCode === 32)return false;" class="form-control form-control-lg form-control-solid" type="password" placeholder="" name="newPassword" id="newPassword" autocomplete="off" />
<span  class="error2"  id="errornewpass"></span>
                                                                        <span class="btn btn-sm btn-icon position-absolute translate-middle top-50 end-0 me-n2" data-kt-password-meter-control="visibility">
                                                                            <i class="bi bi-eye-slash fs-2"></i>

                                                                            <i class="bi bi-eye fs-2 d-none"></i>
                                                                        </span>
                                                                    </div>
                                                                    <!--end::Input wrapper-->

                                                                    <!--begin::Meter-->
                                                                    <div class="d-flex align-items-center mb-3" data-kt-password-meter-control="highlight">
                                                                        <div class="flex-grow-1 bg-secondary bg-active-success rounded h-5px me-2"></div>
                                                                        <div class="flex-grow-1 bg-secondary bg-active-success rounded h-5px me-2"></div>
                                                                        <div class="flex-grow-1 bg-secondary bg-active-success rounded h-5px me-2"></div>
                                                                        <div class="flex-grow-1 bg-secondary bg-active-success rounded h-5px"></div>
                                                                    </div>
                                                                    <!--end::Meter-->
                                                                </div>
                                                                <!--end::Wrapper-->
                                                            </div>
                                                            <!--end::Input group--->

                                                            <!--begin::Input group--->
                                                            <div class="fv-row mb-10">
                                                                <label class="form-label fw-semibold fs-6 mb-2 required">Confirm Password</label>

                                                                <input onKeyDown="if(event.keyCode === 32)return false;" class="form-control form-control-lg form-control-solid" type="password" placeholder="" name="confirmNewPassword" id="confirmNewPassword" autocomplete="off" />
                                                            <span class="error2" id="errorconfirmpass"></span>
                                                            </div>
                                                            <!--end::Input group--->

                                                            <div class="row justify-content-between">
                                                                <div class="col-md-5 my-3">
                                                                    <button type="submit" id="updatepassword" class="btn w-100 btn-primary">
                                                                    <span class="indicator-label">Save</span>
                                                                    <span class="indicator-progress">Please wait...
                                                                        <span
                                                                            class="spinner-border spinner-border-sm align-middle ms-2"></span></span>
                                                                </button>
                                                                </div>
                                                                <div class="col-md-5 my-3">
                                                                    <button type="button" class="btn w-100 cancel btn-secondary">Cancel</button>
                                                                </div>
                                                            </div>
                                                        </s:form>

                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
							</div>
							<!--end::Container-->
						</div>
						<!--end::Post-->
					</div>
<!--Start Modal for Invalid user alert-->
  
  <script>
            "use strict";
            var KTCareersApply = function () {
                var t, e, i,passwordMeter,errorText,elements;
	            var count =0;
                var validatePassword = function() {
                    return  (passwordMeter.getScore() === 100);
                }
                return {
                    init: function () {
                    	
                        i = document.querySelector("#changepassword"),
                        passwordMeter = KTPasswordMeter.getInstance(i.querySelector('[data-kt-password-meter="true"]')),
                        t = document.getElementById("updatepassword"),
                        e = FormValidation.formValidation(i, {
                                fields: {
                                    'oldPassword': {
                                        validators: {
                                            notEmpty: {
                                                message: 'Current password is required'
                                            }
                                        }
                                    },
                                    'newPassword': {
                                        validators: {
                                            notEmpty: {
                                                message: 'Password is required'
                                            },
                                            callback: {
                                                message: 'Please enter valid password',
                                                callback: function (input) {
                                                    if (input.value.length > 0) {
                                                        return validatePassword();
                                                    }
                                                }
                                            }
                                        }
                                    },
                                    'confirmNewPassword': {
                                        validators: {
                                            notEmpty: {
                                                message: 'Password confirmation is required'
                                            },
                                            identical: {
                                                compare: function () {
                                                    return i.querySelector('[name="newPassword"]').value;
                                                },
                                                message: 'Password and its confirm are not the same'
                                            }
                                        }
                                    },
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
                        t.addEventListener("click", (function (r) {
                                r.preventDefault(),
                                e.revalidateField('newPassword');
                                    e && e.validate().then((function (g) {
                                        console.log("validated!"),
                                            "Valid" == g ? (t.setAttribute("data-kt-indicator", "on"),
                                                t.disabled = !0,
                                                setTimeout((function () {
                                                	changPassSubmit(),
                                                    t.removeAttribute("data-kt-indicator"),
                                                    t.disabled = !1,
                                                     i.querySelector('[name="oldPassword"]').value = "",
                                                     i.querySelector('[name="newPassword"]').value = "",
                                                     i.querySelector('[name="confirmNewPassword"]').value = "",
                                                     passwordMeter.reset();
                                            
                                                }
                                                ), 2e3)) :
                                                	(
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
                        							  }).then((function (t) {
                                                     KTUtil.scrollTop() 
                                                }
                                                ))
                                                )
                                    }
                                    ));
                                    
                            }
                            ));

                        
                    }
                }
            }();
            KTUtil.onDOMContentLoaded((function () {
                KTCareersApply.init()
            }
            ));
        </script>
  

</body>



</html>