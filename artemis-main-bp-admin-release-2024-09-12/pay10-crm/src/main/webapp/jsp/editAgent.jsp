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


<script type="text/javascript">

$(document).ready(function(){
	var google2FAKeyFlag="<s:property value='%{google2FAKey}'/>";
        if(google2FAKeyFlag=="true"){
           console.log("api key true");
           $("#resetTFA").attr("disabled",true);
        }
        else{
          console.log("api key false");
          $("#resetTFA").attr("disabled",false);
        }
        console.log("Flag "+google2FAKeyFlag);



	setTimeout(function(){
		var responseCode = $("#response").value;
		if(responseCode)
			{
			if(responseCode==='000')
				{

				Swal.fire({
					text: "Form has been successfully submitted!",
					icon: "success",
					buttonsStyling: !1,
					confirmButtonText: "Ok, got it!",
					customClass: {
					  confirmButton: "btn btn-primary"
					}
				});
			}else{

				Swal.fire({
					text: "Sorry, looks like there are some errors detected, please try again.",
					icon: "error",
					buttonsStyling: !1,
					confirmButtonText: "Ok, got it!",
					customClass: {
					  confirmButton: "btn btn-primary"
					}
				  });

				}
			}
		}, 3000);


});

$(function() {
		$("#userGroupId").change(function() {
			var groupId = this.value;
			var token  = document.getElementsByName("token")[0].value;
			$.ajax({
				type : "POST",
				url : "roleByGroup",
				data : {
					groupId : groupId,
					token:token,
					"struts.token.name": "token"
				},
				success : function(data, status) {
					document.getElementById("roleIdDiv").innerHTML=data;
				},
				error : function(status) {
					//alert("Network error please try again later!!");
					return false;
				}
			});
		});
	});
</script>

<style type="text/css">.error-text{color:#a94442;font-weight:bold;background-color:#f2dede;list-style-type:none;text-align:center;list-style-type: none;margin-top:10px;
}.error-text li { list-style-type:none; }
#response{color:green;}
</style>
</head>
<body  id="kt_body" class="header-fixed header-tablet-and-mobile-fixed toolbar-enabled toolbar-fixed toolbar-tablet-and-mobile-fixed aside-enabled aside-fixed" style="--kt-toolbar-height:55px;--kt-toolbar-height-tablet-and-mobile:55px">

<div class="content d-flex flex-column flex-column-fluid" id="kt_content">
						<!--begin::Toolbar-->
					<div class="toolbar" id="kt_toolbar">
							<!--begin::Container-->
							<div id="kt_toolbar_container" class="container-fluid d-flex flex-stack">
								<!--begin::Page title-->
								<div data-kt-swapper="true" data-kt-swapper-mode="prepend" data-kt-swapper-parent="{default: '#kt_content_container', 'lg': '#kt_toolbar_container'}" class="page-title d-flex align-items-center me-3 flex-wrap mb-5 mb-lg-0 lh-1">
									<!--begin::Title-->
									<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">User List</h1>
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
										<li class="breadcrumb-item text-muted">Manage Users</li>
										<!--end::Item-->
										<!--begin::Item-->
										<li class="breadcrumb-item">
											<span class="bullet bg-gray-200 w-5px h-2px"></span>
										</li>
										<!--end::Item-->

                                        <!--begin::Item-->
										<li class="breadcrumb-item text-muted">User List</li>
										<!--end::Item-->
										<!--begin::Item-->
										<li class="breadcrumb-item">
											<span class="bullet bg-gray-200 w-5px h-2px"></span>
										</li>
										<!--end::Item-->
										<!--begin::Item-->
										<li class="breadcrumb-item text-dark">Edit Submerchant</li>
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
                                        <div class="row content-row submerchant">
                                            <div class="col p-0">
                                                <div class="card">
                                                    <div class="card-body">
                                                        <div class="container-fluid">
                                                            <div class="row justify-content-center">
                                                               <div><s:actionmessage></s:actionmessage></div>
                                                                <div class="col-lg-6 col-md-12 form-wrap">
                                                                    <s:form id="add_sub_merchant" class="form" action="editSubUserDetails">
                                                                    <s:hidden name="token" value="%{#session.customToken}"></s:hidden>
                                                                        <!--begin::Input group-->
                                                                        <div class="d-flex flex-column mb-8 fv-row">
                                                                            <!--begin::Label-->
                                                                            <label class="d-flex align-items-center fs-6 fw-semibold mb-2">
                                                                                <span class="required">First Name</span>
                                                                            </label>
                                                                            <!--end::Label-->
                                                                            <s:textfield type="text" class="form-control form-control-solid" placeholder="Enter First Name" name="firstName" id = "fname" />
                                                                        </div>
                                                                        <div class="d-flex flex-column mb-8 fv-row">
                                                                            <!--begin::Label-->
                                                                            <label class="d-flex align-items-center fs-6 fw-semibold mb-2">
                                                                                <span class="required">Last Name</span>
                                                                            </label>
                                                                            <!--end::Label-->
                                                                            <s:textfield type="text" class="form-control form-control-solid" placeholder="Enter Last Name" name="lastName" id = "lname" />
                                                                        </div>
                                                                        <div class="d-flex flex-column mb-8 fv-row">
                                                                            <!--begin::Label-->
                                                                            <label class="d-flex align-items-center fs-6 fw-semibold mb-2">
                                                                                <span class="required">Phone</span>
                                                                            </label>
                                                                            <!--end::Label-->
                                                                            <s:textfield type="number" min="0" class="form-control form-control-solid" name="mobile" id="mobile" />
                                                                        </div>
                                                                        <div class="d-flex flex-column mb-8 fv-row">
                                                                            <!--begin::Label-->
                                                                            <label class="d-flex align-items-center fs-6 fw-semibold mb-2">
                                                                                <span class="required">Email</span>
                                                                            </label>
                                                                            <!--end::Label-->
                                                                            <s:textfield type="text" class="form-control form-control-solid"  name="emailId" id = "emailId" />
                                                                        </div>
                                                                        <!--end::Input group-->
                                                                        <!--begin::Input group-->
                                                                        <div class="mb-15 fv-row">
                                                                            <!--begin::Wrapper-->
                                                                            <div class="d-flex flex-stack">
                                                                                <div class="d-flex align-items-center">
                                                                                    <!--begin::Checkbox-->
                                                                                    <label class="form-check form-check-custom form-check-solid me-10">
                                                                                        <s:checkbox class="form-check-input h-20px w-20px"  name="isActive" id="isActive" />
                                                                                        <span class="form-check-label fw-semibold">Is Active</span>
                                                                                    </label>
                                                                                    <!--end::Checkbox-->
                                                                                </div>
																				<div class="d-inline-block">
																					<label class="form-check form-check-custom form-check-solid me-10">
                                                                                        <s:checkbox class="form-check-input h-20px w-20px"  name="isTfaEnable" id="isTfaEnable" value="%{user.tfaFlag}" />
                                                                                        <span class="form-check-label fw-semibold">2FA Enable</span>
                                                                                    </label>

																				</div>

                                                                                <!--end::Checkboxes-->
                                                                            </div>
                                                                            <!--end::Wrapper-->
                                                                        </div>

																		<div class="d-flex flex-column mb-8 fv-row">
                                                                            <!--begin::Label-->
                                                                            <label class="d-flex align-items-center fs-6 fw-semibold mb-2">
                                                                                <span class="">User Group</span>
                                                                            </label>
                                                                            <!--end::Label-->
                                                                            <s:select name="userGroupId" class="form-select form-select-solid" data-control="select2" data-hide-search="true" data-placeholder="Select User Group"
                                                                             id="userGroupId"
																			headerKey="" headerValue="Select User Group" list="userGroups"
																			listKey="id" listValue="group" autocomplete="off" />

                                                                        </div>
                                                                        <div id="roleIdDiv">

                                                                        <div class=" flex-column mb-8 fv-row">
                                                                            <!--begin::Label-->
                                                                            <label class="d-flex align-items-center fs-6 fw-semibold mb-2">
                                                                                <span class="">Role</span>
                                                                            </label>
                                                                            <!--end::Label-->
                                                                            <s:select class="form-select form-select-solid" data-control="select2" data-hide-search="true" data-placeholder="Select Role" id="roleId" name="roleId" list="roles"
																			listKey="id" listValue="roleName" autocomplete="off"
																			style="margin-top:10px; font-weight:normal; font-size:14px; border: none; width:100% !important;"></s:select>
                                                                        </div>
			  															</div>

                                                                        <!--end::Input group-->
                                                                        <!--begin::Actions-->
																		<div class="text-center">
																			<button id="resetTFA" class="btn w-100  btn-primary" style="margin-bottom: 10px;" type="button" onclick="resetTFAuthentication()">Reset 2FA</button>
                                                                        </div>

                                                                        <div class="text-center">
                                                                            <button type="submit" id="add_sub_merchant_submit" class="btn btn-primary w-100" style="margin-top: 5px;" >
                                                                                <span class="indicator-label">Save</span>
                                                                                <span class="indicator-progress">Please wait...
                                                                                <span class="spinner-border spinner-border-sm align-middle ms-2"></span></span>
                                                                            </button>
                                                                        </div>
                                                                        <!--end::Actions-->
                                                                    </s:form>
                                                                    <s:hidden name="response" id="response" value="%{responseObject.responseCode}" />
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
							<!--end::Container-->
						</div>
						<!--end::Post-->
					</div>

					<script>
			"use strict";
			var KTCareersApply = function () {
			  var t, e, i,errorText,elements;
	            var count =0;
			  return {
				init: function () {
				  i = document.querySelector("#add_sub_merchant"),
					t = document.getElementById("add_sub_merchant_submit"),
					e = FormValidation.formValidation(i, {
					  fields: {
						  firstName: {
						  validators: {
							notEmpty: {
							  message: "First Name is required"
							},
							stringLength: {
							  max: 50,
							  message: 'First name must be less than 50 characters',
							},
							callback: {
							  callback: function (input) {
								if (input.value.search(/[!\@\^\_\&\/\\#,\|+()$~%.'":*?<>{}]/) > 0) {
								  return {
									valid: false,
									message: 'Special characters not allowed in first name',
								  };
								} else {
								  return {
									valid: true
								  }
								}
							  }
							}
						  }
						},
						lastName: {
						  validators: {
							notEmpty: {
							  message: "Last Name is required"
							},
							stringLength: {
							  max: 50,
							  message: 'Last name must be less than 50 characters',
							},
							callback: {
							  callback: function (input) {
								if (input.value.search(/[!\@\^\_\&\/\\#,\|+()$~%.'":*?<>{}]/) > 0) {
								  return {
									valid: false,
									message: 'Special characters not allowed in last name',
								  };
								} else {
								  return {
									valid: true
								  }
								}
							  }
							}
						  }
						},

						mobile: {
						  validators: {
							notEmpty: {
							  message: "Phone Number is required"
							},
							stringLength: {
							  max: 10,
							  min:10,
							  message: 'Please enter valid phone number',
							}
						  }
						},
						emailId: {
						  validators: {
							emailAddress: {
							  message: 'Enter valid email address'
							},
							notEmpty: {
							  message: 'Email ID is required'
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
					t.addEventListener("click", (function (n) {
					  n.preventDefault(),
						e && e.validate().then((function (g) {
						  console.log("validated!"),
							"Valid" == g ? (t.setAttribute("data-kt-indicator", "on"),
							  t.disabled = !0,
							   setTimeout((function () {
								t.removeAttribute("data-kt-indicator"),
								  t.disabled = !1,
								  i.submit();
								  /*Swal.fire({
									text: "Form has been successfully submitted!",
									icon: "success",
									buttonsStyling: !1,
									confirmButtonText: "Ok, got it!",
									customClass: {
									  confirmButton: "btn btn-primary"
									}
								  }).then((function (t) {
									t.isConfirmed
								  }
								  )) */
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
						))
					}
					))
				}
			  }
			}();
			KTUtil.onDOMContentLoaded((function () {
			  KTCareersApply.init()
			}
			));
				function resetTFAuthentication(){
                                        			//	var payIdData=$("#payId").val();

                                        				var emailIdData=$("#emailId").val();
                                        				if(emailIdData)
                                        			 	var token = document.getElementsByName("token")[0].value;
                                        		 	$.ajax({
                                        				type:"POST",
                                        				url:"merchantResetTFA",
                                        				data:{
                                        					emailId:emailIdData,
                                         					token: token,
                                        					"struts.token.name": "token"
                                        				},
                                        				success:function(response){
                    										$("#resetTFA").attr('disabled',true);

                    						 				console.log(response);

                                                            Swal.fire({
                                          					 	icon: "success",
                                         						title: "2FA Reset Succesfully Done",
                                         						showConfirmButton: false,
                                         						timer: 1500
                                        					});
                                        				},
                                        				error:function(response){
                                        					Swal.fire({
                                          						icon: "error",
                                          						title: "Oops...",
                                          						text: "2FA Reset Failed",
                                         						showConfirmButton: false,
                                         						timer: 1500
                                        					});
                                                         	console.log(response);
                                        				}


                                        			});
                                        		}
		</script>
</body>
</html>