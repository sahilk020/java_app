<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="/struts-tags" prefix="s"%>
<html>
<head>
<meta http-equiv="cache-control" content="no-cache">
<!-- tells browser not to cache -->
<meta http-equiv="expires" content="0">
<!-- says that the cache expires 'now' -->
<meta http-equiv="pragma" content="no-cache">
<!-- says not to use cached stuff, if there is any -->
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Reseller Mapping</title>

<link href="../assets/plugins/global/plugins.bundle.css"
	rel="stylesheet" type="text/css" />
<link href="../assets/css/style.bundle.css" rel="stylesheet"
	type="text/css" />
<link href="../assets/css/custom/custom-style.css" rel="stylesheet"
	type="text/css" />
<link rel="shortcut icon" href="../assets/media/images/paylogo.svg" />
<link rel="stylesheet"
	href="https://fonts.googleapis.com/css?family=Inter:300,400,500,600,700" />

<link
	href="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.css"
	rel="stylesheet" type="text/css" />

<script src="../assets/plugins/global/plugins.bundle.js"></script>
<script src="../assets/js/scripts.bundle.js"></script>
<style type="text/css">
.card-list-toggle {
	cursor: pointer;
	padding: 8px 12px;
	border: 1px solid #496cb6;
	position: relative;
	background: linear-gradient(60deg, #425185, #4a9b9b);
}

.card-list-toggle:before {
	position: absolute;
	right: 10px;
	top: 7px;
	content: "\f078";
	font-family: 'FontAwesome';
	font-size: 15px;
}

.card-list-toggle.active:before {
	content: "\f077";
}

.card-list {
	display: none;
}

.btn:focus {
	outline: 0 !important;
}

.sub-headingCls {
	margin-top: 20px;
	margin-bottom: 0px;
	font-size: 12px;
	font-weight: 700;
	color: #0271bb;
}

label {
	font-size: 14px;
	font-weight: 500;
	color: grey;
}

.col-sm-6.col-lg-3 {
	margin-bottom: 10px;
}
</style>

<style>
.product-spec input[type=text] {
	width: 35px;
}

.btn {
	text-transform: capitalize;
}

.form-control {
	display: block;
	width: 100% !important;
	height: 28px;
	padding: 3px 4px;
	font-size: 14px;
	line-height: 1.42857143;
	color: #555;
	background-color: #fff;
	background-image: none;
	border: 1px solid #ccc;
	border-radius: 4px;
	-webkit-box-shadow: inset 0 1px 1px rgba(0, 0, 0, .075);
	box-shadow: inset 0 1px 1px rgba(0, 0, 0, .075);
	-webkit-transition: border-color ease-in-out .15s, -webkit-box-shadow
		ease-in-out .15s;
	-o-transition: border-color ease-in-out .15s, box-shadow ease-in-out
		.15s;
	transition: border-color ease-in-out .15s, box-shadow ease-in-out .15s;
}
#submitBtn{
margin-top: 25px;
}

#saveMsg{
    color: #8ef58c;
    background: #86b787;
 
}
#saveMsg ul li{
 list-style: none !important;
}

</style>
<script type="text/javascript">
$(document).ready(function(){
 
  // Initialize select2
  $("#resellers").select2();
  $("#merchants").select2();
});
</script>
</head>
<body>
	<!-- <div id="loader-wrapper"
		style="width: 100%; height: 100%; display: none;">
		<div id="loader"></div>
	</div> -->
<div class="content d-flex flex-column flex-column-fluid" id="kt_content">
	<div style="overflow: scroll !important;">
		<!-- <table width="100%" border="0" cellspacing="0" cellpadding="0"
			class="txnf">
			<tr>
				<td>
					<div>
						<input type="button" value="Update Service Tax" id="updateServiceTax" class="btn btn-success btn-md" style="display: inline;margin-top:1%;width:16%;margin-left:1%;font-size: 15px;margin-bottom:1%;"></input>
					</div>
				</td>
			</tr>
		</table> -->
	<div id="saveMsg" align="center"><s:actionmessage class="success success-text" /></div>
		<!-- Added By Sweety -->

		<!--begin::Root-->
		<div class="d-flex flex-column flex-root">
			<!--begin::Page-->
			<div class="page d-flex flex-row flex-column-fluid">
				<!--begin::Content-->
				<div class="content d-flex flex-column flex-column-fluid"
					id="kt_content">
					<!--begin::Toolbar-->
					<div class="toolbar" id="kt_toolbar">
						<!--begin::Container-->
						<div id="kt_toolbar_container"
							class="container-fluid d-flex flex-stack">
							<!--begin::Page title-->
							<div data-kt-swapper="true" data-kt-swapper-mode="prepend"
								data-kt-swapper-parent="{default: '#kt_content_container', 'lg': '#kt_toolbar_container'}"
								class="page-title d-flex align-items-center me-3 flex-wrap mb-5 mb-lg-0 lh-1">
								<!--begin::Title-->
								<h1
									class="d-flex align-items-center text-dark fw-bold my-1 fs-3">
									Reseller Mapping</h1>
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
									<li class="breadcrumb-item text-muted">Reseller Setup</li>
									<!--end::Item-->
									<!--begin::Item-->
									<li class="breadcrumb-item"><span
										class="bullet bg-gray-200 w-5px h-2px"></span></li>
									<!--end::Item-->
									<!--begin::Item-->
									<li class="breadcrumb-item text-dark">Reseller Mapping</li>
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
							<s:form id="getchangpaymenttype" action="getresellerMapping"
								method="post">

								<table width="100%" border="0" cellspacing="0" cellpadding="0"
									class="txnf" style="margin-top: 1%;">
									<tr>

										<!-- <td width="21%"><h2 style="margin-bottom: 15px;" >Charging Platform</h2></td> -->
									</tr>
									<div class="row my-5">
										<div class="col">
											<div class="card">
												<div class="card-body">
													<!--begin::Input group-->
													<div class="row g-9 mb-8">

														<div class="col-md-4 fv-row">
															<label class="d-flex align-items-center fs-6 fw-bold mb-2">
															<span class="">Select Reseller </span>
															</label>

															
																<s:select headerValue="Select Reseller" headerKey=""
																	name="ResellerPayId" class="form-select form-select-solid"
																	id="resellers" list="listReseller" listKey="payId"
																	listValue="businessName" autocomplete="off" data-control="select-2"
																	style="margin-left: -4px;" />
															
														</div>

												
														<div class="col-md-4 fv-row">
															<label class="d-flex align-items-center fs-6 fw-bold mb-2">
															<span class="">Select Merchant </span>
															</label>
															
																<s:select headerValue="Select Merchant" headerKey=""
																	name="MechantpayId" class="form-select form-select-solid"
																	id="merchants" list="listMerchant" listKey="payId"
																	listValue="businessName" autocomplete="off" data-control="select-2"
																	style="margin-left: -4px;" />
														
														</div>
														<div class="col-md-4 fv-row">
															<input type="button" id="submitBtn" value="mapping"
																onclick="getMapping()" class="btn btn-primary btn-xs">

														</div>
													</div>
												</div>
											</div>


										</div>
									</div>

									<tr>

									</tr>
								</table>
								<s:hidden name="token" value="%{#session.customToken}"></s:hidden>
							</s:form>
						</div>
					</div>
				</div>
			</div>
		</div>



	</div>
	</div>
	<script>
		function getMapping() {

			var resellerVal = document.getElementById("resellers").value;
			var merchantVal = document.getElementById("merchants").value;

			if (resellerVal == '' || resellerVal == null) {
				alert("please select reseller");
				return false;

			}
			if (merchantVal == '' || merchantVal == null) {
				alert("please select merchant");
				return false;

			}
			var alreadyMapped = IsAlreadyMapped(merchantVal);
			if (!alreadyMapped) {
				$("#getchangpaymenttype").submit();
			}
		}

		function IsAlreadyMapped(merchantVal) {

			var test = "0";
			var urls = new URL(window.location.href);
			var domain = urls.origin;
			$
					.ajax({
						type : "GET",
						//url : "https://uat.pay10.com/crmws/reseller/mappingExists",
						url : domain + "/crmws/reseller/mappingExists",
						data : {
							"payId" : merchantVal,
						},
						async : false,
						success : function(response, status) {
							if (response) {
								alert("Reseller already mapped for the selected merchant");
								test = "1";
							} else {
								test = "0";
							}
						}
					});
			if (test == "1") {
				return true;
			} else if (test == "0") {
				return false;
			}
		}
	</script>
</body>
</html>