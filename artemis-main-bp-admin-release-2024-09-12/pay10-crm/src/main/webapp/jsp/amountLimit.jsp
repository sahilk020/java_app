
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="/struts-tags" prefix="s"%>
<html dir="ltr" lang="en-US">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Transaction Limit Report</title>


<link rel="shortcut icon" href="assets/media/images/paylogo.svg" />
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

<script src="../assets/plugins/global/plugins.bundle.js"></script>
<script src="../assets/js/scripts.bundle.js"></script>
<link href="../css/select2.min.css" rel="stylesheet" />
<script src="../js/jquery.select2.js" type="text/javascript"></script>

<script src="../js/amountLimit.js"></script>
<script type="text/javascript" src="../js/sweetalert.js"></script>
<link rel="stylesheet" href="../css/sweetalert.css">


<style type="text/css">
/* .cust {width: 24%!important; margin:0 5px !important; 
			} */
.samefnew {
	width: 19.5% !important;
	margin: 5px 5px !important; . samefnew-btn { width : 12%;
	float: left;
	font: bold 11px arial;
	color: #333;
	line-height: 22px;
	margin-top: 5px;
}
/*tr td.my_class{color:#000 !important; cursor: default !important; text-decoration: none;}*/
.cust .form-control, .samefnew .form-control {
	margin: 0px !important;
	width: 95%;
}

.select2-container {
	width: 100% !important;
}

.clearfix:after {
	display: block;
	visibility: hidden;
	line-height: 0;
	height: 0;
	clear: both;
	content: '.';
}

#popup {
	position: fixed;
	top: 0px;
	left: 0px;
	background: rgba(0, 0, 0, 0.7);
	width: 100%;
	height: 100%;
	z-index: 999;
	display: none;
}

.innerpopupDv {
	width: 600px;
	margin: 60px auto;
	background: #fff;
	padding-left: 5px;
	padding-right: 15px;
	border-radius: 10px;
}

.btn-custom {
	margin-top: 5px;
	height: 27px;
	border: 1px solid #5e68ab;
	background: #5e68ab;
	padding: 5px;
	font: bold 12px Tahoma;
	color: #fff;
	cursor: pointer;
	border-radius: 5px;
}

#loader-wrapper .loader-section.section-left, #loader-wrapper .loader-section.section-right
	{
	background: rgba(225, 225, 225, 0.6) !important;
	width: 50% !important;
}

.invoicetable {
	float: none;
}

.innerpopupDv h2 {
	font-size: 12px;
	padding: 5px;
}

.my_class1 {
	color: #0040ff !important;
	text-decoration: none !important;
	cursor: pointer;
	*cursor: hand;
}

.my_class {
	color: white !important;
}

.text-class {
	text-align: center !important;
}

.download-btn {
	background-color: #496cb6;
	display: block;
	width: 100%;
	height: 30px;
	padding: 3px 4px;
	font-size: 14px;
	line-height: 1.42857143;
	color: #fff;
	border: 1px solid #ccc;
	border-radius: 4px;
	margin-top: 30px;
}

#loading {
	width: 100%;
	height: 100%;
	top: 0px;
	left: 0px;
	position: fixed;
	display: block;
	z-index: 99
}

#loading-image {
	position: absolute;
	top: 40%;
	left: 55%;
	z-index: 100;
	width: 10%;
}

#loadingInner {
	width: 100%;
	height: 100%;
	top: 0px;
	left: 0px;
	position: fixed;
	display: block;
	z-index: 99
}

#loading-image-inner {
	position: absolute;
	top: 33%;
	left: 48%;
	z-index: 100;
	width: 5%;
}
</style>

<script type="text/javascript">
	// 	function	getDateByMerchant(){

	// 		 var merchants = document.getElementById("merchants").value;
	// 		alert("data"+merchants);
	// 	}

	// 				var urls = new URL(window.location.href);
	// 				var domain = urls.origin;

	// 				 $.ajax({
	// 						type : "GET",
	// 						url : domain+"/crmws/acquirer/getMappedAcquirer",
	// 						timeout : 0,
	// 						data : {
	// 							"emailId" : merchants,
	// 						},
	// 						success : function(data, status) {
	// 							var acquirer= JSON.stringify(data);
	// 						    var acquirerList= JSON.parse(acquirer);
	// 							//var s = '<option value="">Select acquirer</option>';
	// 							var s;
	// 							for (var i = 0; i<acquirerList.length; i++) {

	// 								s += '<option value="' + acquirerList[i] + '">' + acquirerList[i]
	// 										+ '</option>';
	// 							}
	// 							document.getElementById("acquirerDropdown").style.display = "block";

	// 							$("#acquirer").html(s);
	// 						}
	// 					});
	// 		});
</script>

</head>

<body id="mainBody"
	class="header-fixed header-tablet-and-mobile-fixed toolbar-enabled toolbar-fixed toolbar-tablet-and-mobile-fixed aside-enabled aside-fixed">
	<div class="content d-flex flex-column flex-column-fluid"
		id="kt_content">
		<div id="loader-wrapper"
			style="width: 100%; height: 100%; display: none;"></div>
		<div class="toolbar" id="kt_toolbar">
			<!--begin::Container-->
			<div id="kt_toolbar_container"
				class="container-fluid d-flex flex-stack">
				<!--begin::Page title-->
				<div data-kt-swapper="true" data-kt-swapper-mode="prepend"
					data-kt-swapper-parent="{default: '#kt_content_container', 'lg': '#kt_toolbar_container'}"
					class="page-title d-flex align-items-center me-3 flex-wrap mb-5 mb-lg-0 lh-1">
					<!--begin::Title-->
					<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">Transaction
						Limit Report</h1>
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
						<li class="breadcrumb-item text-muted">Manage Acquirers</li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item"><span
							class="bullet bg-gray-200 w-5px h-2px"></span></li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item text-dark">Transaction Limit
							Report</li>
						<!--end::Item-->
					</ul>
					<!--end::Breadcrumb-->
				</div>
				<!--end::Page title-->

			</div>
			<!--end::Container-->
		</div>


		<div class="post d-flex flex-column-fluid" id="kt_post">
			<div id="kt_content_container" class="container-xxl">


				<div class="row my-5">
					<div class="col">
						<div class="card ">
							<div class="card-body ">
								<div class="container">
									<div class="row">
										<s:form id="resellerPayoutForm">
											<!-- 							<div class="row g-9 mb-8"> -->
											<!-- 										begin::Col -->
											<!-- 											<div class="col-md-4 fv-row" style="margin-top: 20px;"> -->
											<!-- 								<label	class="d-flex align-items-center fs-6 fw-bold mb-2"> -->
											<%-- 									<span class="">Merchant</span> --%>
											<!-- 													</label> -->
											<%-- 													<s:select name="merchants" --%>
											<%-- 														class="form-select form-select-solid" id="merchants" --%>
											<%-- 														headerKey="" headerValue="Select Merchant" listKey="payId" --%>
											<%-- 														listValue="businessName" list="merchantList" --%>
											<%-- 														autocomplete="off" onchange="getDateByMerchant();" /> --%>
											<!-- 												</div> -->
											<!-- 												</div> -->

											<hr>
											<br>
											<div class="row g-9 mb-8">



												<div class="col-md-4 fv-row" style="margin-top: 20px;">
													<label class="d-flex align-items-center fs-6 fw-bold mb-2">
														<span class=""> Daily Limits :</span>
													</label>
													<!--end::Label-->
													<input type="number"
														placeholder="Please enter the Daily Limits"
														class="form-control form-control-solid" id="DailyLimits"
														name="DailyLimits" />
												</div>


												<div class="col-md-4 fv-row" style="margin-top: 20px;">
													<label class="d-flex align-items-center fs-6 fw-bold mb-2">
														<span class=""> Weekly Limits :</span>
													</label>
													<!--end::Label-->
													<input type="number"
														placeholder="Please enter the Weekly Limits"
														class="form-control form-control-solid" id="WeeklyLimits"
														name="WeeklyLimits" />
												</div>

												<div class="col-md-4 fv-row" style="margin-top: 20px;">
													<label class="d-flex align-items-center fs-6 fw-bold mb-2">
														<span class=""> Monthly Limits :</span>
													</label>
													<!--end::Label-->
													<input type="number"
														placeholder="Please enter the Monthly Limits"
														class="form-control form-control-solid" id="MonthlyLimits"
														name="MonthlyLimits" />
												</div>

												<div class="col-md-4 fv-row" style="margin-top: 20px;">
													<label class="d-flex align-items-center fs-6 fw-bold mb-2">
														<span class=""> Transaction Amount Limit :</span>
													</label>
													<!--end::Label-->
													<input type="number"
														placeholder="Please enter the Transaction Amount Limit"
														class="form-control form-control-solid"
														id="TransactionLimits" name="TransactionLimits" />
												</div>

												<div class="col-md-4 fv-row" style="margin-top: 20px;">
													<label class="d-flex align-items-center fs-6 fw-bold mb-2">
														<span class="">Max Transaction Per VPA :</span>
													</label>
													<!--end::Label-->
													<input type="number"
														placeholder="Please enter the Max Transaction Per VPA"
														class="form-control form-control-solid" id="maxTxnVpa"
														name="maxTxnVpa" />
												</div>
												<br>
											</div>
											<div class="col-md-4 fv-row">
												<div class="txtnew">

													<input type="button" id="submitBtn" value="Add"
														onclick="addRule();" class="btn btn-primary" />
												</div>
											</div>
										</s:form>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>



				<div class="modal" id="payoutAccept" tabindex="-1" role="dialog"
					aria-labelledby="myModalLabel" aria-hidden="true">
					<div class="modal-dialog">
						<div class="modal-content">
							<div class="modal-header" align="center"></div>
							<div class="modal-body">
								Please Update the details.
								<table class="table">
									<tr>
										<th>Daily Limits</th>
										<td id="batchNoTd">
										<td id="batchaccept"><input type="number"
											placeholder="Please enter the Daily Limits"
											class="form-control form-control-solid" id="DailyLimitsx"
											name="DailyLimitsx" /></td>
										</td>
									</tr>

									<tr>
										<th>Weekly Limits</th>
										<td id="resellerIdTd">
										<td id="resellerIdaccept"><input type="number"
											placeholder="Please enter the Weekly Limits"
											class="form-control form-control-solid" id="WeeklyLimitsx"
											name="WeeklyLimitsx" /></td>
										</td>
									</tr>
									<tr>
										<th>Max Txn Vpa</th>
										<td id="resellerIdTd">
										<td id="resellerIdaccept"><input type="number"
											placeholder="Please enter the Max Transaction Per VPA"
											class="form-control form-control-solid" id="maxTxnVpax"
											name="maxTxnVpax" /></td>
										</td>
									</tr>
									<tr>
										<th>Transaction Limits</th>
										<td id="resellerIdTd">
										<td id="resellerIdaccept"><input type="number"
											placeholder="Please enter the Max Transaction Per VPA"
											class="form-control form-control-solid"
											id="TransactionLimitsx" name="TransactionLimitsx" /></td>
										</td>
									</tr>
									<tr>
										<th>Monthly Limits</th>
										<td id="resellerIdTd">
										<td id="resellerIdaccept"><input type="number"
											placeholder="Please enter the Monthly Limits"
											class="form-control form-control-solid" id="MonthlyLimitsx"
											name="MonthlyLimitsx" /></td>
										</td>
									</tr>
									<tr>
										<td hidden id="resellerIdaccept">
										<td id="resellerIdaccept">
										<input type="hidden" id="id" class="input-control " autocomplete="off" />
										</td>
										</td>
									</tr>
								</table>
							</div>
							<div align="center">
								<button type="button" class="btn btn-primary"
									id="btnPayoutConf1" onClick="addRuleupdate()">Submit</button>
								<button type="button" class="btn btn-danger" id="btnPayoutConf"
									data-dismiss="modal" onclick="closePopup1();">Cancel</button>
							</div>
						</div>
					</div>
				</div>


				<div class="row my-5">
					<div class="col">
						<div class="card">
							<div class="card-body">
								<div style="overflow: scroll !important;">

									<br /> <br />
									<div class="scrollD">
										<table id="datatable"
											class="table table-striped table-row-bordered gy-5 gs-7"
											cellspacing="0" width="100%">
											<thead>
												<tr class="fw-bold fs-6 text-gray-800">
													<th></th>
													<th>Daily Limit</th>
													<th>Weekly Limit</th>
													<th>Monthly Limit</th>
													<th>Transaction Limit</th>
													<th>Created Date</th>
													<th>Created By</th>
													<th>Status</th>
													<th>Max VPA Txn Limit</th>
													<th>EDIT</th>
													<th>DELETE</th>

													<th>Create By</th>


													<th></th>
												</tr>
											</thead>
										</table>
									</div>
									<!-- </td>
				</tr>
			</table> -->
								</div>
							</div>
						</div>
					</div>
				</div>


			</div>
		</div>
	</div>


	<script src="../assets/plugins/custom/datatables/datatables.bundle.js"></script>
</body>
</html>
