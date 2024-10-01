<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
<!-- <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta name="viewport" content="width=device-width, initial-scale=1" /> -->
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
<title>ResellerCommission Report</title>

<script src="../js/jquery-1.9.0.js"></script>
<script src="../js/resellerCommissionReport.js"></script>

<script type="text/javascript">
	$(document).ready(function() {

		// Initialize select2
		$("#resellerId").select2();
		$("#merchantDropdown").select2();
		$("#paymentDropdown").select2();
		
		document.getElementById("loading").style.display = "none"
	});
</script>
<script type="text/javascript">
	$(document)
			.ready(
					function() {
						var userType = "<s:property value='%{#session.USER.UserGroup.group}'/>";
						console.log(userType)
						if (userType == 'Reseller') {
							$('#reseller').hide();
							var resellerid = document
									.getElementById("resellerId").value;
							getMerchant(resellerid);
							var payId = document
									.getElementById("merchantDropdown").value;

							getPaymentType(payId);

						}

					});
</script>

<style>
#ui-datepicker-div {
	z-index: 10 !important;
}

.card-stats .card-header.card-header-icon i {
	font-size: 20px !important;
}

.card [class*="card-header-"] .card-icon, .card [class*="card-header-"] .card-text
	{
	padding: 0px !important;
}

#cardIcon {
	padding: 15px !important;
}

#materialIcons {
	font-size: 36px !important;
}

@media ( min-width : 1496px) {
	.card.card-stats {
		min-height: 89px !important;
	}
}

@media ( min-width : 1300px) {
	.card.card-stats {
		min-height: 110px !important;
	}
}

@media ( min-width : 992px) {
	.card.card-stats {
		min-height: 133px !important;
	}
}

.table td {
	padding: 0.75rem;
	vertical-align: top;
	border-top: 1px solid rgba(0, 0, 0, 0.06);
	font-size: 16px;
	font-weight: 400;
	color: #000000 !important;
}

.table-totalrow {
 font-weight: bold;
    font-size: 20px;
   /*  background: radial-gradient(black, transparent); */
    background: #f9c14a59;
	  color:black !important;
   
}

p#TotalTxnAmount, p#TotalTransactionCount, p#TotalCommAmount,
	#total-text {
	 color:black !important;
	font-weight: bold;
	font-size: 20px;
}

#submit{
margin-top:25px;
}
</style>
</head>

<body>
	<div style="overflow: scroll !important;">
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
									Reseller Commission Report</h1>
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
									<li class="breadcrumb-item text-dark">Reseller Commission
										Report</li>
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

							<s:form>
								<!-- 	<div id="loading" style="text-align: center;">
			<img id="loading-image" style="width: 70px; height: 70px;"
				src="../image/sand-clock-loader.gif" alt="Sending SMS..." />
		</div> -->


								<!-- <h2>Performance Report</h2> -->
								<div class="row my-5">
									<div class="col">
										<div class="card">
											<div class="card-body">
												<!--begin::Input group-->
												<div class="row g-9 mb-8">
													<!--begin::Col-->
													<div class="col-md-3 fv-row">
														<label class="d-flex align-items-center fs-6 fw-bold mb-2">
															<span class="">Date From</span>
														</label>

														<s:textfield type="text" id="dateFrom" name="dateFrom"
															class="form-select form-select-solid" autocomplete="off"
															readonly="true" />

													</div>
													<div class="col-md-3 fv-row">
														<label class="d-flex align-items-center fs-6 fw-bold mb-2">
															<span class="">Date To</span></label>

															<div class="txtnew">
																<s:textfield type="text" id="dateTo" name="dateTo"
																	class="form-select form-select-solid" autocomplete="off"
																	readonly="true" style="width: 103% !important;" />
															</div>
													</div>
													<div class="col-md-3 fv-row" id="reseller">
														<label class="d-flex align-items-center fs-6 fw-bold mb-2">
														<span class="">Select Reseller</span></label>
															<s:if
																test="%{#session.USER.UserGroup.group =='Reseller'}">
																<s:select name="resellerId" class="form-select form-select-solid"
																	id="resellerId" onchange="getMerchant(this.value);"
																	list="resellerList" listKey="payId"
																	listValue="businessName" autocomplete="off" />
															</s:if>
															<s:else>
																<s:select name="resellerId" class="form-select form-select-solid"
																	id="resellerId" headerKey=""
																	headerValue="Select Reseller"
																	onchange="getMerchant(this.value);" list="resellerList"
																	listKey="payId" listValue="businessName" data-control="select-2"
																	autocomplete="off" />
															</s:else>
														
													</div>
													</div>
													<div class="row g-9 mb-8">
													<div class="col-md-3 fv-row" id="tomerchant">
														<label class="d-flex align-items-center fs-6 fw-bold mb-2">
														<span class="">Select Merchant</span></label>
															<s:select class="form-select form-select-solid" id="merchantDropdown"
																name="merchantname"
																onchange="getPaymentType(this.value);"
																list="merchantlist" listKey="payId"
																listValue="businessName" autocomplete="off" data-control="select-2"
																style="margin-left: -4px;" />
													
													</div>

													<div class="col-md-3 fv-row" id="topayment">
													<label class="d-flex align-items-center fs-6 fw-bold mb-2">
													<span class="">Select PaymentType</span></label>
															<s:select class="form-select form-select-solid" id="paymentDropdown"
																name="paymentType" list="paymentTypeList"
																listValue="paymentType" autocomplete="off" data-control="select-2"
																style="margin-left: -4px;" />
														
													</div>

													<div class="col-sm-6 col-lg-3">

														<div class="txtnew">
															<!-- <button id="submit1" class="btn btn-primary  mt-4 submit_btn">Submit</button> -->
															<input type="button" id="submit" value="Submit"
																onClick="getDetails();"
																class="btn btn-primary btn-xs">

														</div>

													</div>
</div>
												
											</div>


										</div>
									</div>
								</div>

								
										<div class="col-md-1"></div>
										<div class="col-md-10">
											<div class="cardTypeTxnDetails" id="showForAll"
												align="center">
													<div class="row my-5">
									<div class="col">
										<div class="card">
											<div class="card-body">
												<table class="table mytable table-hover ">
													<thead>
														<tr class="table-totalrow">
															<th>Payment Type</th>
															<th>Transaction Amount</th>
															<th>Transactions Count</th>
															<th>Commission Amount</th>

														</tr>
													</thead>
													<tbody>
														<tr id="cc">
															<td>Credit Card</td>
															<td><p id="CCTxnAmount" class="media-heading"></p></td>
															<td><p id="CCTotalCount" class="media-heading"></p></td>
															<td><p id="ccTotalAmount" class="media-heading"></p></td>
														</tr>
														<tr id="dc">
															<td>Debit Card</td>
															<td><p id="DCTxnAmount" class="media-heading"></p></td>
															<td><p id="DCTotalCount" class="media-heading"></p></td>
															<td><p id="dcTotalAmount" class="media-heading"></p></td>
														</tr>
														<tr id="up">
															<td>UPI</td>
															<td><p id="UPTxnAmount" class="media-heading"></p></td>
															<td><p id="UPTotalCount" class="media-heading"></p></td>
															<td><p id="upTotalAmount" class="media-heading"></p></td>
														</tr>
														<tr id="wl">
															<td>Wallet</td>
															<td><p id="WLTxnAmount" class="media-heading"></p></td>
															<td><p id="WLTotalCount" class="media-heading"></p></td>
															<td><p id="wlTotalAmount" class="media-heading"></p></td>
														</tr>
														<tr id="nb">
															<td>Net Banking</td>
															<td><p id="NBTxnAmount" class="media-heading"></p></td>
															<td><p id="NBTotalCount" class="media-heading"></p></td>
															<td><p id="nbTotalAmount" class="media-heading"></p></td>
														</tr>
														<tr class="table-totalrow">
															<td id="total-text">Total</td>
															<td><p id="TotalTxnAmount" class="media-heading"></p></td>
															<td><p id="TotalTransactionCount"
																	class="media-heading"></p></td>
															<td><p id="TotalCommAmount" class="media-heading"></p></td>
														</tr>

													</tbody>
												</table>
												</div>
												</div>
												</div>
											</div>

										</div>
									<!-- </div> -->
								<!-- </div> -->
						</div>
						</s:form>
					</div>
					</div>
					</div>
					</div>
					</div>
					</div>
					
					<script
						src="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.js"></script>
					<script src="../assets/plugins/global/plugins.bundle.js"></script>
					<script src="../assets/js/scripts.bundle.js"></script>
					<script>
					
	  var fromDate = new Date();
	  fromDate.setMonth(fromDate.getMonth() -2);
	  fromDate.setDate(fromDate.getDate() -2);
	$("#dateFrom").flatpickr({
		minDate : fromDate,
		maxDate: new Date(),
		defaultDate: "today"
		
	});
	$("#dateTo").flatpickr({
		minDate : fromDate,
		maxDate: new Date(),
		defaultDate: "today"
	});
	</script>
</body>
</html>
