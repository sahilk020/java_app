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
	function changPassSubmit() {

		debugger
		var oldP = $('#oldPassword').val();
		var newP = $('#newPassword').val();
		var conP = $('#confirmNewPassword').val();

		var oldPass = CryptoJS.AES.encrypt(oldP, 'thisisaverysecretkey');

		var newPass = CryptoJS.AES.encrypt(newP, 'thisisaverysecretkey');

		var confirmNewPass = CryptoJS.AES.encrypt(conP, 'thisisaverysecretkey');
		//console.log(confirmNewPass.toString());

		document.getElementById('oldPassword').value = oldPass;
		document.getElementById('newPassword').value = newPass;
		document.getElementById('confirmNewPassword').value = confirmNewPass;

		jQuery
				.ajax({
					type : "POST",

					url : 'changeExpirePassword',

					data : {
						token : document.getElementsByName("token")[0].value,
						oldPassword : document.getElementById('oldPassword').value,
						newPassword : document.getElementById('newPassword').value,
						confirmNewPassword : document
								.getElementById('confirmNewPassword').value,
						emailId : '${emailId}',
					},
					success : function(data) {

						var responsedata = data.response;
						var jsonObj = data["Invalid request"];
						if (jsonObj != null) {
							var oldpass = jsonObj['oldPassword'];
							var newpass = jsonObj['newPassword'];
							var confirmpass = jsonObj['confirmNewPassword'];

							var errText = null;
							if (oldpass != null) {

								errText = "Old Password : " + oldpass;

							} else if (newpass != null) {
								errText = "New Password : " + newpass;

							} else if (confirmpass != null) {
								errText = "Confirm Password : " + confirmpass;

							}
							if (errText != null) {
								Swal
										.fire(
												{
													text : errText,
													icon : "error",
													buttonsStyling : !1,
													confirmButtonText : "Ok, got it!",
													customClass : {
														confirmButton : "btn btn-primary"
													}
												})
										.then(
												(function(t) {
													if (t.isConfirmed) {
																document
																		.getElementById('oldPassword').value = '',
																document
																		.getElementById('newPassword').value = '',
																document
																		.getElementById('confirmNewPassword').value = ''

													}
												}));
							} else if (newPassword.value != confirmNewPassword.value
									|| confirmNewPassword.value != newPassword.value) {

								Swal
										.fire(
												{
													text : "Password Criteria Mismatch please enter valid password.",
													icon : "error",
													buttonsStyling : !1,
													confirmButtonText : "Ok, got it!",
													customClass : {
														confirmButton : "btn btn-primary"
													}
												})
										.then(
												(function(t) {
													if (t.isConfirmed) {
																document
																		.getElementById('oldPassword').value = '',
																document
																		.getElementById('newPassword').value = '',
																document
																		.getElementById('confirmNewPassword').value = ''
													}
												}));

							}

						} else if (responsedata) {
							if (responsedata
									.indexOf("Password has been successfully changed") !== -1) {
								fields = "";
								Swal
										.fire(
												{
													text : responsedata,
													icon : "success",
													buttonsStyling : !1,
													confirmButtonText : "Ok, got it!",
													customClass : {
														confirmButton : "btn btn-primary"
													}
												})
										.then(
												(function(t) {
													if (t.isConfirmed) {
																document
																		.getElementById('oldPassword').value = '',
																document
																		.getElementById('newPassword').value = '',
																document
																		.getElementById('confirmNewPassword').value = '',
																location.href = "loginResult";

													}
												}));

							} else {
								fields = "";
								Swal
										.fire(
												{
													text : responsedata,
													icon : "error",
													buttonsStyling : !1,
													confirmButtonText : "Ok, got it!",
													customClass : {
														confirmButton : "btn btn-primary"
													}
												})
										.then(
												(function(t) {
													if (t.isConfirmed) {
																document
																		.getElementById('oldPassword').value = '',
																document
																		.getElementById('newPassword').value = '',
																document
																		.getElementById('confirmNewPassword').value = ''

													}
												}));
							}

						}

						else if (newPassword.value == 0
								|| confirmNewPassword.value == 0
								|| oldPassword.value == 0) {
							fields = "";
							Swal
									.fire(
											{
												text : "Please Enter all Mandatory Fields",
												icon : "error",
												buttonsStyling : !1,
												confirmButtonText : "Ok, got it!",
												customClass : {
													confirmButton : "btn btn-primary"
												}
											})
									.then(
											(function(t) {
												if (t.isConfirmed) {
															document
																	.getElementById('oldPassword').value = '',
															document
																	.getElementById('newPassword').value = '',
															document
																	.getElementById('confirmNewPassword').value = ''

												}
											}));
						} else if (oldPassword.value != oldpass.value) {
							fields = "";
							Swal
									.fire(
											{
												text : "Please Enter the Valid Old Password",
												icon : "error",
												buttonsStyling : !1,
												confirmButtonText : "Ok, got it!",
												customClass : {
													confirmButton : "btn btn-primary"
												}
											})
									.then(
											(function(t) {
												if (t.isConfirmed) {
															document
																	.getElementById('oldPassword').value = '',
															document
																	.getElementById('newPassword').value = '',
															document
																	.getElementById('confirmNewPassword').value = ''

												}
											}));
						}
					},
					error : function(data) {
						Swal
								.fire(
										{
											text : "Sorry, looks like there are some errors detected, please try again.",
											icon : "error",
											buttonsStyling : !1,
											confirmButtonText : "Ok, got it!",
											customClass : {
												confirmButton : "btn btn-primary"
											}
										})
								.then(
										(function(t) {
											if (t.isConfirmed) {
														document
																.getElementById('oldPassword').value = '',
														document
																.getElementById('newPassword').value = '',
														document
																.getElementById('confirmNewPassword').value = ''

											}
										}));
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

	<!--begin::Post-->
	<div class="post d-flex flex-column-fluid" id="kt_post"
		style="background: linear-gradient(120deg, #041fff, #201ef3,#fab82e,#ffffff,#f4b82b, #fab82e,#201ef3, #041fff) !important;">
		<!--begin::Container-->
		<div id="kt_content_container" class="container-xxl">
			<div class="row h-100 align-items-center">
				<div class="col">
					<div class="card">
						<div class="card-body">
							<div class="row g-9 mb-8">
								<p class="text-gray-600 fs-5 fw-semibold">
									<span style="color: #202f4b;" class="fs-3">QR Code</span>:
									Kindly utilize your Google Authenticator application to scan
									the QR Code provided below for the purpose of one-time
									registration.
								</p>
							</div>
							<div class="col-12 d-flex justify-content-center mt-4 fs-2"
								style="color: #202f4b;">
								<p class="fw-bold">Two factor authorization QR code</p>
							</div>
							<div class="row justify-content-center">
								<div class="col-md-6">

									<!--begin::Form-->
									<s:form id="changepassword" class="form"
										action="2FAOTP"
										autocomplete="off">
										<s:token />
										<s:actionmessage class="success success-text" theme="simple" />
										<span id="errorchangepass"
											style="font-size: 11px; color: red; position: absolute; top: 29px;"></span>
										<s:hidden id="totpemail" name="totpemail"
											value="%{#session.TOTPMAIL}"></s:hidden>
										<!--begin::Input group--->
										<img id="base64Image" alt="Base64 Image"
											style="margin-left: 9vw;">
										<!--end::Input group--->

										<div class="row justify-content-between">
											<!-- <div class="col-md-5 my-3"> -->
											<div class="">
												<button type="submit" id="updatepassword"
													class="btn w-100 btn-primary">
													<span class="indicator-label"> Enter TOTP</span> <span
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
		$(document)
				.ready(
						function() {
							debugger
							var emaildid=$('#totpemail').val();
							var urls = new URL(window.location.href);
							var domain = urls.origin;

							$
									.ajax({
										type : 'GET',
										url : domain+'/crmws/google2FA/generate/'+emaildid,
										success : function(data) {
											debugger
											var base64Data = data.base64Image;
											document
													.getElementById('base64Image').src = 'data:image/png;base64,'
													+ base64Data;
										}

									});
						});
	</script>
</body>



</html>