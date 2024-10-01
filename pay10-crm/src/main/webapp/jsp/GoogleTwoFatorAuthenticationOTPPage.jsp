<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<base href="" />
<title>BPGATE</title>
<meta charset="utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1" />
<link rel="shortcut icon" href="../assets/media/images/paylogo.svg" />
<!--begin::Fonts-->
<link rel="stylesheet"
	href="https://fonts.googleapis.com/css?family=Inter:300,400,500,600,700" />
<!--end::Fonts-->
<!--begin::Vendor Stylesheets(used by this page)-->
<link
	href="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.css"
	rel="stylesheet" type="text/css" />
<link href="../assets/plugins/custom/datatables/datatables.bundle.css"
	rel="stylesheet" type="text/css" />
<!--end::Vendor Stylesheets-->
<!--begin::Global Stylesheets Bundle(used by all pages)-->
<link href="../assets/plugins/global/plugins.bundle.css"
	rel="stylesheet" type="text/css" />
<link href="../assets/css/style.bundle.css" rel="stylesheet"
	type="text/css" />
<link href="../assets/css/custom/custom-style.css" rel="stylesheet"
	type="text/css" />


<%-- <script src="../js/index.js"></script> --%>
<script src="../js/jquery.js"></script>
<script src="../js/jquery.min.js"></script>
<script src="../assets/plugins/global/plugins.bundle.js"></script>
<script src="../assets/js/scripts.bundle.js"></script>
<!--begin::Vendors Javascript(used by this page)-->
<script
	src="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.js"></script>
<script src="https://cdn.amcharts.com/lib/5/index.js"></script>
<script src="https://cdn.amcharts.com/lib/5/xy.js"></script>
<script src="https://cdn.amcharts.com/lib/5/percent.js"></script>
<script src="https://cdn.amcharts.com/lib/5/radar.js"></script>
<script src="https://cdn.amcharts.com/lib/5/themes/Animated.js"></script>
<script src="https://cdn.amcharts.com/lib/5/map.js"></script>
<script src="https://cdn.amcharts.com/lib/5/geodata/worldLow.js"></script>
<script src="https://cdn.amcharts.com/lib/5/geodata/continentsLow.js"></script>
<script src="https://cdn.amcharts.com/lib/5/geodata/usaLow.js"></script>
<script
	src="https://cdn.amcharts.com/lib/5/geodata/worldTimeZonesLow.js"></script>
<script
	src="https://cdn.amcharts.com/lib/5/geodata/worldTimeZoneAreasLow.js"></script>
<script src="../assets/plugins/custom/datatables/datatables.bundle.js"></script>
<!--end::Vendors Javascript-->
<!--begin::Custom Javascript(used by this page)-->
<script src="../assets/js/widgets.bundle.js"></script>
<script src="../assets/js/custom/widgets.js"></script>
<script src="../assets/js/custom/apps/chat/chat.js"></script>
<script src="../assets/js/custom/utilities/modals/upgrade-plan.js"></script>
<script src="../assets/js/custom/utilities/modals/create-app.js"></script>
<script src="../assets/js/custom/utilities/modals/users-search.js"></script>
<script src="../js/aes.js"></script>
<!--end::Custom Javascript-->
<!--end::Javascript-->

<%-- <script src="../js/commanValidate.js"></script> --%>
<%-- <script>
jQuery.noConflict(true);

</script> --%>


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

#mismatchmsg {
	font-size: 16px;
	color: #496cb6;
	text-align: center;
	font-weight: 500;
}

.none {
	display: none !important;
}
/* #loader-wrapper{
	display: none;
} */
</style>
<script>
      function submitTOTP(){    	 

debugger
    	    var totp=$('#totp').val();
			var emaildid=$('#totpemail').val();
			//  'admin@pay10.com';
			var urls = new URL(window.location.href);
		  var domain = urls.origin;
    	  jQuery.ajax({
	  type: "POST",

		url : domain+'/crmws/google2FA/validate/key/'+emaildid+'/'+totp,
		
		success : function(data){
			
			var responsedata= data;
			debugger
			 if(responsedata){
				if(responsedata=="Authenticated"){
				fields = "";
				debugger
				Swal.fire({
		            text: responsedata,
		            icon: "success",
		            buttonsStyling: !1,
		            confirmButtonText: "Ok, got it!",
		            customClass: {
		                confirmButton: "btn btn-primary"
		            }
		        }).then((function(t) {
					debugger
                    if (t.isConfirmed) {
                    	document.getElementById('totp').value = '',

						$('#TOTPAuthenticatedSubmit').click();
            			// location.replace = "TOTPAuthenticated";
                        
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
	           
	        }
	        ));
		}
			
		
	});
};
</script>
</head>
<body>

	<form method="post" action="TOTPAuthenticated">
		<input type="submit" id="TOTPAuthenticatedSubmit"
			style="display: none;">
	</form>

	<!--begin::Post-->
	<div class="post d-flex flex-column-fluid" id="kt_post"
		style="background: linear-gradient(120deg, #202F4B, #202F4B,#202F4B,#202F4B,#202F4B, #202F4B,#202F4B, #202F4B) !important">
		<!--begin::Container-->
		<div id="kt_content_container" class="container-xxl">
			<div class="row h-100 align-items-center">
				<div class="col">
					<div class="card">
						<div class="card-body">
							<div class="row g-9 mb-8">
								<p class="text-gray-600 fs-5 fw-semibold">
									<span style="color: #202f4b;" class="fs-3">TOTP
										Authenticator</span>: Kindly initiate the Google Authentication
									process and enter the corresponding Time-based One-Time
									Password (TOTP) for verification. In case you don't have access to the TOTP please contact your system administrator.
								</p>
							</div>
							<div class="col-12 d-flex justify-content-center mt-4 fs-2"
								style="color: #202f4b;">
								<p class="fw-bold">Verification</p>
							</div>
							<div class="row justify-content-center">
								<div class="col-md-6">

									<!--begin::Form-->
									<s:form id="changepassword" class="form" action="#"
										autocomplete="off">
										<s:token />
										<s:actionmessage class="success success-text" theme="simple" />
										<span id="errorchangepass"
											style="font-size: 11px; color: red; position: absolute; top: 29px;"></span>
										<s:hidden id="totpemail" name="totpemail"
											value="%{#session.TOTPMAIL}"></s:hidden>
										<!--begin::Input group--->
										<div class="fv-row mb-10">
											<label class="required form-label fs-6 mb-2">Enter
												TOTP</label> <input
												onKeyDown="if(event.keyCode === 32)return false;"
												class="form-control form-control-lg form-control-solid"
												type="text" placeholder="" id="totp" name="totp"
												autocomplete="off" maxlength="6" /> <span class="error2"
												id="errorTOTP"></span>
										</div>

										<!--end::Input group--->

										<div class="row justify-content-between">
											<!-- <div class="col-md-5 my-3"> -->
											<div class="">
												<button type="submit" id="updatepassword"
													class="btn w-100 btn-primary">
													<span class="indicator-label"> Authenticate TOTP</span> <span
														class="indicator-progress">Please wait... <span
														class="spinner-border spinner-border-sm align-middle ms-2"></span></span>
												</button>
											</div>
											<!-- <div class="col-md-5 my-3">
                                                                    <button type="button" class="btn w-100 cancel btn-secondary">Cancel</button>
                                                                </div> -->
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
                                    'totp': {
                                        validators: {
                                            notEmpty: {
                                                message: 'TOTP is required'
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
                                
                                    e && e.validate().then((function (g) {
                                        console.log("validated!"),
                                            "Valid" == g ? (t.setAttribute("data-kt-indicator", "on"),
                                                t.disabled = !0,
                                                setTimeout((function () {
                                                	submitTOTP();
                                                    t.removeAttribute("data-kt-indicator"),
                                                    t.disabled = !1;
                                                    
                                                     
                                            
                                                }
                                                ), 2e3)) : 
                                               ( errorText ="",
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
	<script>
		$(document)
				.ready(
						function() {
							var urls = new URL(window.location.href);
							var domain = urls.origin;
							
							$
									.ajax({
										type : 'POST',
										url : domain+'/crm/2FAOTP',
										success : function() {
											
										}

									});
						});
	</script>

</body>
</html>