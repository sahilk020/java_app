<%@page import="com.pay10.crm.actionBeans.TransactionStatusBean"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="/struts-tags" prefix="s"%>
<html dir="ltr" lang="en-US">

<head>
<title>Create Application</title>

<link href="../assets/plugins/global/plugins.bundle.css"
	rel="stylesheet" type="text/css" />
<link href="../assets/css/style.bundle.css" rel="stylesheet"
	type="text/css" />
<link href="../assets/css/custom/custom-style.css" rel="stylesheet"
	type="text/css" />
<script src="../js/loader/main.js"></script>
<script src="../assets/plugins/global/plugins.bundle.js"></script>
<script src="../assets/js/scripts.bundle.js"></script>
<script src="../assets/js/widgets.bundle.js"></script>
<script src="../assets/js/custom/widgets.js"></script>
<script src="../assets/js/custom/apps/chat/chat.js"></script>
<script src="../assets/js/custom/utilities/modals/upgrade-plan.js"></script>
<script src="../assets/js/custom/utilities/modals/create-app.js"></script>
<script src="../assets/js/custom/utilities/modals/users-search.js"></script>
<link href="../css/select2.min.css" rel="stylesheet" />
<script src="../js/jquery.select2.js" type="text/javascript"></script>
<script src="../js/jquery.min.js" type="text/javascript"></script>
<style type="text/css">
.dt-buttons.btn-group.flex-wrap {
	display: none;
}

.error {
	color: red
}

</style>
</head>

<body>

	<div class="content d-flex flex-column flex-column-fluid"
		id="kt_content">

		<div class="toolbar" id="kt_toolbar">
			<!--begin::Container-->
			<div id="kt_toolbar_container"
				class="container-fluid d-flex flex-stack">
				<!--begin::Page title-->
				<div data-kt-swapper="true" data-kt-swapper-mode="prepend"
					data-kt-swapper-parent="{default: '#kt_content_container', 'lg': '#kt_toolbar_container'}"
					class="page-title d-flex align-items-center me-3 flex-wrap mb-5 mb-lg-0 lh-1">
					<!--begin::Title-->
					<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">Create Application
					</h1>
					<!--end::Title-->
					<!--begin::Separator-->
					<span class="h-20px border-gray-200 border-start mx-4"></span>
					<!--end::Separator-->
					<!--begin::Breadcrumb-->
					<ul
						class="breadcrumb breadcrumb-separatorless fw-semibold fs-7 my-1">
						<!--begin::Item-->
						<li class="breadcrumb-item text-muted"><a href="home"
							class="text-muted text-hover-primary">Dashboard</a></li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item"><span
							class="bullet bg-gray-200 w-5px h-2px"></span></li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item text-muted">Shield</li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item"><span
							class="bullet bg-gray-200 w-5px h-2px"></span></li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item text-dark">Create Application</li>
						<!--end::Item-->
					</ul>
					<!--end::Breadcrumb-->
				</div>
				<!--end::Page title-->

			</div>
			<!--end::Container-->
		</div>

		<s:form action="" id="addForm">
			<s:token />
			<div class="post d-flex flex-column-fluid" id="kt_post">
				<!--begin::Container-->
				<div id="kt_content_container" class="container-xxl">
					<!--begin::Input group-->
					<div class="card">
						<div class="card-body">
							<div class="row g-9 mb-8">
								<!--begin::Col-->

								<div class="col-md-4">
									<!--begin::Label-->
									<label class="d-flex align-items-center fs-6 fw-semibold mb-2">
										<span class="">Name<b class=error>*</b></span>
									</label>

									<div class="txtnew">
										<s:textfield id="name" name="name"
											class="form-control form-control-solid" placeholder="Enter Application Name"
											autocomplete="off"/>
										<span id='nameError' class='error'></span>
									</div>

								</div>

								<div class="col-md-4">
									<!--begin::Label-->
									<label class="d-flex align-items-center fs-6 fw-semibold mb-2">
										<span class="">URL<b class=error>*</b></span>
									</label>

									<div class="txtnew">
										<s:textfield id="baseUrl" name="url"
											class="form-control form-control-solid"
											placeholder="Enter URL" autocomplete="off" maxlength="500" />
										<span id='baseUrlError' class='error'></span>
									</div>

								</div>
								<div class="col-md-3">
                                <!--begin::Label-->
                                <label class="d-flex align-items-center fs-6 fw-semibold mb-2">
                                    <span class="">Image<b class=error>*</b></span>
                                </label>

                                <div class="txtnew">
                                    <input
                                        type="file" id="p_image" name="image"
                                        onchange="encodeImgtoBase64(this)"
                                        class="form-control form-control-solid"
                                        accept="image/png,image/jpeg,image/jpg" />
                                </div>
                                <span id="ImageError" class="error"></span>
                            </div>
							</div>
								<s:hidden name="image" id="image" />
								<div class="col-md-2"
									style="margin-left:600px; margin-top: 30px;">

									<button type="button" class="btn btn-primary"
										onclick="createApplication();">submit</button>
								</div>


						</div>
					</div>
				</div>
		</s:form>
	</div>
	<script type="text/javascript">
		function createApplication() {
			var valid = true;
             validateName();
		    var baseUrl=$("#baseUrl").val();
		    var urlRegex="^((https?://)|(http://))+(www\.)?[a-zA-Z0-9]+([-.][a-zA-Z0-9]+)*\.[a-zA-Z]{2,}(/.*)?$";
		    if(baseUrl=='' || baseUrl=="" || baseUrl==null){
		     document.getElementById("baseUrlError").innerHTML = "URL is required";
            valid = false;
		    }
		    else if(!baseUrl.match(urlRegex)){
		     document.getElementById("baseUrlError").innerHTML = "URL must be start from http or https";
              valid = false;
		    }
		    else{
		    document.getElementById("baseUrlError").innerHTML = "";
		    }
			var fileInput = $("#p_image");
			var file = fileInput[0].files[0];
			if (!file) {
			    document.getElementById("ImageError").innerHTML = "Logo is required";
			    valid = false;
			} else {
			    var allowedExtensions = /(\.jpg|\.jpeg|\.png)$/i;
			    if (!allowedExtensions.exec(file.name)) {
			        document.getElementById("ImageError").innerHTML = "Invalid file type. Only JPG,JPEG and PNG are allowed.";
			        valid = false;
			    } else {
			        document.getElementById("ImageError").innerHTML = "";
			    }
			}
			if (valid) {
				var url = "";
				var urls = new URL(window.location.href);
				var domain = urls.origin;
				var data = {
                       name : $("#name").val(),
                        url : $("#baseUrl").val(),
                        image: $('#image').val(),
				}
				console.log(JSON.stringify(data));

				$.ajax({

					type : "POST",
					url : "addApplication",
					data : data,
					success : function(response) {

							alert("Application Added successfully");
							 window.location.reload();


					}

				});
			}
		}
	</script>
	<script type="text/javascript">
	function validateName() {
			var nameField = document.getElementById("name");
			var nameValue = nameField.value;

			if (nameValue === null || nameValue === '') {
				document.getElementById("nameError").innerHTML = "Name is required";
			} else if (!/^[a-zA-Z]+$/.test(nameValue)) {
				nameField.value = nameValue.replace(/[^a-zA-Z]/g, "");
				document.getElementById("nameError").innerHTML = "Name should contain only alphabetic characters";
			} else {
				document.getElementById("nameError").innerHTML = "";
			}
		}
		function encodeImgtoBase64(element) {
			var img = element.files[0];
			var reader = new FileReader();
			reader.onloadend = function() {
				$("#image").val(reader.result);
			}
			reader.readAsDataURL(img);
		}
	</script>

</body>
</html>
