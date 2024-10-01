<%@page import="com.pay10.crm.actionBeans.TransactionStatusBean"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="/struts-tags" prefix="s"%>
<html dir="ltr" lang="en-US">

<head>
<title>Update WebStore</title>
<link
	href="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.css"
	rel="stylesheet" type="text/css" />
<link href="../assets/plugins/custom/datatables/datatables.bundle.css"
	rel="stylesheet" type="text/css" />
<link href="../assets/plugins/global/plugins.bundle.css"
	rel="stylesheet" type="text/css" />
<link href="../assets/css/style.bundle.css" rel="stylesheet"
	type="text/css" />
<link href="../assets/css/custom/custom-style.css" rel="stylesheet"
	type="text/css" />
<script src="../js/loader/main.js"></script>
<script src="../assets/plugins/global/plugins.bundle.js"></script>
<script src="../assets/js/scripts.bundle.js"></script>
<script
	src="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.js"></script>
<script src="../assets/plugins/custom/datatables/datatables.bundle.js"></script>
<script src="../assets/js/widgets.bundle.js"></script>
<script src="../assets/js/custom/widgets.js"></script>
<script src="../assets/js/custom/apps/chat/chat.js"></script>
<script src="../assets/js/custom/utilities/modals/upgrade-plan.js"></script>
<script src="../assets/js/custom/utilities/modals/create-app.js"></script>
<script src="../assets/js/custom/utilities/modals/users-search.js"></script>
<script src="../js/commanValidate.js"></script>
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

#txnResultDataTable thead th {
	font-weight: bold;
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
					<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">WebStore
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
						<li class="breadcrumb-item text-muted">WebStore Product</li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item"><span
							class="bullet bg-gray-200 w-5px h-2px"></span></li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item text-dark">Update Product</li>
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

								<!-- <div class="card-header card-header-rose card-header-icon">

									<h4 class="card-title"
										style="font-size: 16px; color: #0271bb; font-weight: 500;">Update
										WebStore Product</h4>
								</div> -->
								<input type="hidden" name="id" id="id" />
								<div class="col-md-4">
									<!--begin::Label-->
									<label class="d-flex align-items-center fs-6 fw-semibold mb-2">
										<span class="">Price<b class=error>*</b></span>
									</label>

									<div class="txtnew">
										<s:textfield id="price" name="price"
											class="form-control form-control-solid" placeholder="price*"
											autocomplete="off" maxlength="100" />
										<span id='priceError' class='error'></span>
									</div>

								</div>


								<div class="col-md-4">
									<!--begin::Label-->
									<label class="d-flex align-items-center fs-6 fw-semibold mb-2">
										<span class="">Name<b class=error>*</b></span>
									</label>

									<div class="txtnew">
										<s:textfield id="name" name="name"
											class="form-control form-control-solid" placeholder="name*"
											autocomplete="off" maxlength="100" />
										<span id='nameError' class='error'></span>
									</div>

								</div>

								<div class="col-md-4">
									<!--begin::Label-->
									<label class="d-flex align-items-center fs-6 fw-semibold mb-2">
										<span class="">Description<b class=error>*</b></span>
									</label>

									<div class="txtnew">
										<s:textfield id="description" name="description"
											class="form-control form-control-solid"
											placeholder="description*" autocomplete="off" maxlength="100" />
										<span id='descriptionError' class='error'></span>
									</div>

								</div>
							</div>
							<div class="row g-9 mb-8">
								<div class="col-md-4">
									<!--begin::Label-->
									<label class="d-flex align-items-center fs-6 fw-semibold mb-2">
										<span class="">Discount Price<b class=error>*</b></span>
									</label>

									<div class="txtnew">
										<s:textfield id="discountprice" name="discountprice"
											class="form-control form-control-solid"
											placeholder="discountprice*" autocomplete="off"
											maxlength="100" />
										<span id="discountpriceError" class="error"></span>
									</div>

								</div>
								<div class="col-md-4">
									<!--begin::Label-->
									<label class="d-flex align-items-center fs-6 fw-semibold mb-2">
										<span class="">Image<b class=error>*</b></span>
									</label>

									<div class="txtnew">
										<img name="imageSrc" id="imageSrc"	 width="70px" height="40px" /> 
										<input
											type="file" id="p_image" name="p_image"
											onchange="encodeImgtoBase64(this)"
											class="form-control form-control-solid"
											accept="image/png,image/jpeg,image/jpg" />
									</div>
									<span id="ImageError" class="error"></span>
								</div>
								<div class="col-md-4">
									<!--begin::Label-->
									<label class="d-flex align-items-center fs-6 fw-semibold mb-2">
										<span class="">Merchant<b class=error>*</b></span>
									</label>

									<div class="txtnew">
										<s:if test="%{#session.USER.UserGroup.group =='Merchant'}">
											<s:select name="payId" id="payId" headerKey=""
												headerValue="Select Merchant"
												class="form-select form-select-solid merchantPayId"
												listKey="uuId" listValue="businessName" list="merchants"
												data-control="select2" />
												<span id='payIdError' class='error'></span>
										</s:if>
										<s:else>
											<s:select name="payId" id="payId" headerKey=""
												headerValue="Select Merchant"
												class="form-select form-select-solid merchantPayId"
												listKey="uuId" listValue="businessName" list="merchants"
												data-control="select2" />
											<span id='payIdError' class='error'></span>
										</s:else>
									</div>

								</div>
								<div class="col-md-2">
									<!--begin::Label-->
									<label class="d-flex align-items-center fs-6 fw-semibold mb-2">
										<span class="">Product Status</span>
									</label>

									<div class="txtnew">
										<s:checkbox id="productStatus" name="productStatus"
											checked="checked"
											style="margin-top:10px;    font-weight:500; font-size:14px;"></s:checkbox>
									</div>

								</div>
								<s:hidden name="image" id="image" />
								<div class="col-md-2"
									style="margin-left: 50px; margin-top: 30px;">

									<button type="button" class="btn btn-primary"
										onclick="addProduct();">submit</button>
								</div>

							</div>
						</div>
					</div>
				</div>
		</s:form>
	</div>
	<script type="text/javascript">
		function addProduct() {
			var valid = true;
			validateName();
			if (!$("#price").val()) {
				document.getElementById("priceError").innerHTML = "Price is required";
				valid = false;
			} else if (isNaN($("#price").val())) {
				document.getElementById("priceError").innerHTML = "Price must be a number";
				valid = false;
			} else if (parseFloat($("#price").val()) < 0) {
				document.getElementById("priceError").innerHTML = "Price cannot be negative";
				valid = false;
			} else {
				document.getElementById("priceError").innerHTML = "";
			}

			if (!$("#description").val()) {
				document.getElementById("descriptionError").innerHTML = "Description is Required";
				valid = false;
			} else {
				document.getElementById("descriptionError").innerHTML = "";
			}

			if (!$("#discountprice").val()) {
				document.getElementById("discountpriceError").innerHTML = " Discount Price is required";
				valid = false;
			} else if (isNaN($("#discountprice").val())) {
				document.getElementById("discountpriceError").innerHTML = "Discount must be a number";
			} else if (parseFloat($("#discountprice").val()) < 0) {
				document.getElementById("discountpriceError").innerHTML = "Discount cannot be negative";
				valid = false;
			} else {

				// Get the  price and discounted price values
				var price = parseFloat($("#price").val());

				var discountprice = parseFloat($("#discountprice").val());

				// Compare the prices
				if (discountprice > price) {
					document.getElementById("discountpriceError").innerHTML = "Discounted price cannot be lesser/greater than the  price";
					valid = false;
				} else {
					document.getElementById("discountpriceError").innerHTML = "";
				}
			}
			if (!$("#payId").val()) {
				document.getElementById("payIdError").innerHTML = "Merchant is Required";
				valid = false;
			} else {
				document.getElementById("payIdError").innerHTML = "";
			}

			var fileInput = $("#p_image");
			var file = fileInput[0].files[0];
			if (!file) {
			    document.getElementById("ImageError").innerHTML = "Image is required";
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
				let price = $("#price").val()
				let name = $("#name").val()
				let description = $("#description").val()
				let discountprice = $("#discountprice").val()
				let payId = $("#payId").val()
				let Image = $("#p_image").val()

				var id = '<s:property value="id"/>';
				var url = "";
				var urls = new URL(window.location.href);
				var domain = urls.origin;
				var data = {
					"uuid" : $("#payId").val(),
					"name" : $("#name").val(),
					"description" : $("#description").val(),
					"image" : $('#image').val(),
					"price" : $("#price").val(),
					"discounted_price" : $("#discountprice").val(),
					"product_status" : document.getElementById("productStatus").checked,

				}
				//url = domain + "/crmws/webStoreApi?type=ADD";
				url = domain + "/crmws/product";
				if (id > 0) {

					url = domain + "/crmws/webStoreApi?type=UPDATE";
					data = {
						"uuid" : $("#payId").val(),
						"name" : $("#name").val(),
						"description" : $("#description").val(),
						"image" : $("#image").val(),
						"price" : $("#price").val(),
						"discounted_price" : $("#discountprice").val(),
						"product_status" : document
								.getElementById("productStatus").checked,
						"id" : id

					}
				}
				$.ajax({

					type : "POST",
					url : url,
					data : JSON.stringify(data),
					contentType : "application/json",
					url : url,
					success : function(response) {
						if (id > 0) {

							alert("Updated successfully");
							 window.location.href =domain+"/crm/jsp/webstoreProduct";
						
						} else {
							alert("Added successfully");
							 window.location.href =domain+"/crm/jsp/webstoreProduct";
						}

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
	<script type="text/javascript">
	var id = '<s:property value="id"/>';
		var imageSrc = $('#image').val();
		if(id>0){
		document.getElementById('imageSrc').src = imageSrc;
			$('#imageSrc').show();
			
			}
		else{
			$('#imageSrc').hide();
			$('#p_image').css("width", "270px");
			}
	</script>
	<style>
	input#p_image {
    display: inline-block;
    width: 190px;
    margin-left: 10px;
}

	</style>
</body>
</html>
