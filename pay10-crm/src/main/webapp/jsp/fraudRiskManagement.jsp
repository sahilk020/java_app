<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Fraud Risk Management</title>
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
<script src="../js/jquery.min.js" type="text/javascript"></script>
<script
	src="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.js"></script>
<script src="../assets/plugins/global/plugins.bundle.js"></script>
<script src="../assets/js/scripts.bundle.js"></script>
<style>
.error {
	font-family: "Times New Roman";
	color: red;
	width: 100%;
	margin-top: 8px;
}
</style>
</head>

<body>
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
				<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">Pay-in
					FRM</h1>
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
					<li class="breadcrumb-item text-muted">Merchant Billing</li>
					<!--end::Item-->
					<!--begin::Item-->
					<li class="breadcrumb-item"><span
						class="bullet bg-gray-200 w-5px h-2px"></span></li>
					<!--end::Item-->
					<!--begin::Item-->
					<li class="breadcrumb-item text-dark">Pay-in FRM</li>
					<!--end::Item-->
				</ul>
				<!--end::Breadcrumb-->
			</div>
			<!--end::Page title-->

		</div>
		<!--end::Container-->
	</div>

	<div style="overflow: auto !important;">
		<div id="kt_content_container" class="container-xxl">
			<div class="row my-5">
				<div class="col">
					<div class="card">
						<div class="card" style="margin-bottom: 20px;">
							<div class="card-body">
								<div class="row my-5 mb-3">
									<div class="col-md-12">
										<div class="form-check form-check-inline">
											<input class="form-check-input" type="checkbox"
												id="pgCheckbox" checked disabled> <label
												class="form-check-label" for="pgCheckbox">Pay-in</label>
										</div>
										<div class="form-check form-check-inline">
											<input class="form-check-input" type="checkbox"
												id="poCheckbox"> <label class="form-check-label"
												for="poCheckbox">Pay-out</label>
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
	<div class="post d-flex flex-column-fluid" id="kt_post">
		<!--begin::Container-->
		<div id="kt_content_container" class="container-xxl">
			<div class="row my-5">
				<div class="col">
					<div class="card">
						<div class="card-body ">
							<div class="row my-3 g-9 align-items-center ">
									<div class="col-lg-4 my-2">
										<label class="d-flex align-items-center fs-6 fw-bold mb-2">
											<span class="">Merchant </span>
										</label>

										<s:select name="merchantPayId"
											class="form-select form-select-solid" id="merchantPayId"
											headerKey=" " headerValue="Select Merchant"
											list="listMerchants" listKey="payId" listValue="businessName"
											data-control="select2" onchange="getCurrencyList()" />
										<span id="error_merchantPayId" class="error"> </span>
									</div>

									<div class="col-lg-4 my-2" id="currencyListDropdown">
										<label class="d-flex align-items-center fs-6 fw-bold mb-2">
											<span class="">Currency </span>
										</label>

										<s:select name="selectedCurrency"
											class="form-select form-select-solid" id="currency"
											headerKey=" " headerValue="Select Currency"
											list="currencyList" listKey="code" listValue="name"
											data-control="select2"
											onchange="getDetails();setTicketLimit();" />
										<span id="error_currencyName" class="error"> </span>
									</div>

									<div class="col-lg-4 my-2" id="paymentListDropdown">
										<label class="d-flex align-items-center fs-6 fw-bold mb-2">
											<span class="">Payment Type </span>
										</label>

										<s:select name="selectedPaymentType"
											headerValue="Select Payment Type" headerKey=" "
											class="form-select form-select-solid" list="paymentList"
											listKey="code" listValue="name" data-control="select2"
											id="paymentType" autocomplete="off"
											onchange="setDataAsPerMop(this.value);setVolumeData(this.value);" />
										<span id="error_paymentMethod" class="error"> </span>

									</div>

								</div>
								<div class="row my-3 g-9 align-items-center ">
									<div class="col-lg-4 my-2">
										<label class="d-flex align-items-center fs-6 fw-bold mb-2">
											<span class="">Min. Amount Per Transaction : <i
												class="currencySymbol"
												style="font-size: large; font-weight: bolder; color: black;"></i></span>
										</label>

										<s:textfield id="minTicketSize" name="minTicketSize"
											class="form-control form-control-solid"
											placeholder="Min. Ticket Size" autocomplete="off" />
										<span id="error_minTicketSize" class="error"> </span>

									</div>

									<div class="col-lg-4 my-2">
										<label class="d-flex align-items-center fs-6 fw-bold mb-2">
											<span class="">Max. Amount Per Transaction : <i
												class="currencySymbol"
												style="font-size: large; font-weight: bolder; color: black;"></i></span>
										</label>

										<s:textfield id="maxTicketSize" name="maxTicketSize"
											class="form-control form-control-solid"
											placeholder="Max. Ticket Size" autocomplete="off" />
										<span id="error_maxTicketSize" class="error"> </span>
									</div>

									<div class="col-lg-4 my-2">
										<label class="d-flex align-items-center fs-6 fw-bold mb-2">
											<span class="">Merchant Profile</span>
										</label> <select name="merchantProfile"
											class="form-select form-select-solid" id="merchantProfile"
											data-control="select2">
											<option value="">Select Risk for merchant</option>
											<option value="low">Low Risk</option>
											<option value="high">High Risk</option>
											<option value="moderate">Moderate</option>
										</select> <span id="error_merchantProfile" class="error"> </span>
									</div>


								</div>
								<!-- Add second row here -->
								<div class="row my-3 g-9 align-items-center ">
									<div class="col-lg-4 my-2">
										<label class="d-flex align-items-center fs-6 fw-bold mb-2">
											<span class="">Max. Amount Limit Per Day : <i
												class="currencySymbol"
												style="font-size: large; font-weight: bolder; color: black;"></i></span>
										</label>

										<s:textfield id="mopDaily" name="mopDaily"
											class="form-control form-control-solid"
											placeholder="Daily Transaction Limit" autocomplete="off" />
										<span id="error_mopDaily" class="error"></span> <label
											class="d-flex align-items-center fs-6 fw-bold mb-2">
											<span class="">Remaining : <i id="dailyBalance"
												style="color: rgb(43, 43, 43);"></i></span>
										</label> 

									</div>
									<div class="col-lg-4 my-2">
										<label class="d-flex align-items-center fs-6 fw-bold mb-2">
											<span class="">Max. Amount Limit Per Week : <i
												class="currencySymbol"
												style="font-size: large; font-weight: bolder; color: black;"></i></span>
										</label>
										<s:textfield id="mopWeekly" name="mopWeekly"
											class="form-control form-control-solid"
											placeholder="Weekly Transaction Limit" autocomplete="off" />
										<span id="error_mopWeekly" class="error"> </span> <label
											class="d-flex align-items-center fs-6 fw-bold mb-2">
											<span class="">Remaining : <i id="weeklyBalance"
												style="color: rgb(43, 43, 43);"></i></span>
										</label>
									</div>
									<div class="col-lg-4 my-2">
										<label class="d-flex align-items-center fs-6 fw-bold mb-2">
											<span class="">Max. Amount Limit Per Month : <i
												class="currencySymbol"
												style="font-size: large; font-weight: bolder; color: black;"></i></span>
										</label>
										<s:textfield id="mopMonthly" name="mopMonthly"
											class="form-control form-control-solid"
											placeholder="Monthly Transaction Limit" autocomplete="off" />
										<span id="error_mopMonthly" class="error"> </span> <label
											class="d-flex align-items-center fs-6 fw-bold mb-2">
											<span class="">Remaining : <i id="monthlyBalance"
												style="color: rgb(43, 43, 43);"></i></span>
										</label>
									</div>
								</div>
								<!-- ended second row here -->
								<!-- add third row here -->
								<div class="row my-3 g-9 align-items-center ">

									<div class="col-lg-4 my-2">
										<label class="d-flex align-items-center fs-6 fw-bold mb-2">
											<span class="">Max. Transaction Count Per Day</span>
										</label>

										<s:textfield id="daily" name="daily"
											class="form-control form-control-solid"
											placeholder="Daily Volume" autocomplete="off" />
										<span id="error_daily" class="error"> </span> <label
											class="d-flex align-items-center fs-6 fw-bold mb-2">
											<span class="">Remaining : <i id="dailyVolumeRemain"
												style="color: rgb(43, 43, 43);"></i></span>
										</label>
									</div>

									<div class="col-lg-4 my-2">
										<label class="d-flex align-items-center fs-6 fw-bold mb-2">
											<span class="">Max. Transaction Count Per Week </span>
										</label>
										<s:textfield id="weekly" name="weekly"
											class="form-control form-control-solid"
											placeholder="Weekly Volume" autocomplete="off" />
										<span id="error_weekly" class="error"> </span> <label
											class="d-flex align-items-center fs-6 fw-bold mb-2">
											<span class="">Remaining : <i id="weeklyVolumeRemain"
												style="color: rgb(43, 43, 43);"></i></span>
										</label>
									</div>

									<div class="col-lg-4 my-2">
										<label class="d-flex align-items-center fs-6 fw-bold mb-2">
											<span class="">Max. Transaction Count Per Month </span>
										</label>
										<s:textfield id="monthly" name="monthly"
											class="form-control form-control-solid"
											placeholder="Monthly Volume" autocomplete="off" />
										<span id="error_monthly" class="error"> </span> <label
											class="d-flex align-items-center fs-6 fw-bold mb-2">
											<span class="">Remaining : <i id="monthlyVolumeRemain"
												style="color: rgb(43, 43, 43);"></i></span>
										</label>
									</div>
								</div>

								<!-- Ended third row here -->
								<div class="row my-3 g-9 align-items-center ">

									<input type="hidden" name="volumeId" id="volumeId"> <input
										type="hidden" name="ticketId" id="ticketId"> <input
										type="hidden" name="id" id="id">
									<div class="col-md-2 fv-row">
										<input type="button" value="Save" onclick="saveDetails();"
											id="saveBtn" class="btn btn-primary"
											style="margin-top: 25px;">

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
	</div>
	</div>


	
	<script>
		function getCurrencyList() {
			$
					.post(
							"getCurrencyListTdr",
							{
								merchantPayId : $("#merchantPayId").val()

							},
							function(response) {
								var res = JSON.stringify(response);
								console.log(response.currencyList)

								var s = '<option value="">Select Currency</option>';
								for ( let key in response.currencyList) {

									s += '<option value="' + key + '">'
											+ response.currencyList[key]
											+ '</option>'
									console.log("Object ::::" + key + " Map "
											+ response.currencyList[key])

								}
								document.getElementById("currencyListDropdown").style.display = "block";

								$("#currency").html(s);

							});

		}

		var frmByMop = [];
		function getDetails() {
			var token = document.getElementsByName("token")[0].value;
			$
					.post(
							"getFrmDetails",
							{
								merchantPayId : $("#merchantPayId").val(),
								selectedCurrency : $("#currency").val()
							},
							function(response) {
								var res = response.fraudObj;
								frmByMop = res;
								console.log(response.paymentList);

								$(".currencySymbol").text(
										response.displaySymbol)

								console.log(response.displaySymbol);

								if (frmByMop.length < 1) {
									$("#mopDaily").val('');
									$("#mopWeekly").val('');
									$("#mopMonthly").val('');
									$('#select2-merchantProfile-container')
											.text('Select Risk for merchant');
									$('span#select2-paymentMethod-container')
											.text('Select Payment Type');

								}
								var s = '<option value="">Select Payment Type</option>';
								for ( let key in response.paymentList) {

									s += '<option value="' + key + '">'
											+ response.paymentList[key]
											+ '</option>'
									console.log("Object ::::" + key + " Map "
											+ response.paymentList[key])

								}
								document.getElementById("paymentListDropdown").style.display = "block";

								$("#paymentType").html(s);

							});
		}

		function setDataAsPerMop(paymentType) {
			var count = 0;
			for (var i = 0; i < frmByMop.length; i++) {
				var resObj = frmByMop[i];
				//alert(JSON.stringify(resObj))

				if (resObj.paymentType === paymentType) {

					$("#mopDaily").val(resObj.dailyAmount);
					$("#mopWeekly").val(resObj.weeklyAmount);
					$("#mopMonthly").val(resObj.monthlyAmount);
					$("#id").val(resObj.id);
					count = 1;

					console.log(resObj.displayDailyQuota);
					$("#dailyBalance").text(resObj.displayDailyQuota);
					$("#weeklyBalance").text(resObj.displayWeeklyQuota);
					$("#monthlyBalance").text(resObj.displayMonthlyQuota);

				}
			}
			if (count == 0) {
				$("#mopDaily").val('100');
				$("#mopWeekly").val('1000');
				$("#mopMonthly").val('10000');
				$("#id").val('');

				$("#dailyBalance").text('100');
				$("#weeklyBalance").text('1000');
				$("#monthlyBalance").text('10000');
			}
			;

		}
	</script>
	<script>
		function setVolumeData(paymentType) {
			var payType = paymentType;
			console.log("paymentType ::" + payType)

			$.post("getFrmDetailsByVolume", {
				merchantPayId : $("#merchantPayId").val(),
				selectedCurrency : $("#currency").val(),
				selectedPaymentType : payType,
			}, function(response) {

				var res = JSON
						.parse(JSON.stringify(response.fraudDataByVolume));
				var frmByVolume = [];
				frmByVolume = res;
				if (frmByVolume.length < 1) {
					$("#daily").val('10');
					$("#weekly").val('100');
					$("#monthly").val('1000');

					$("#dailyVolumeRemain").text('10');
					$("#weeklyVolumeRemain").text('100');
					$("#monthlyVolumeRemain").text('1000');
				} else {

					for (var i = 0; i < frmByVolume.length; i++) {
						var resObj = frmByVolume[i];
						$("#daily").val(resObj.dailyAmount);
						$("#weekly").val(resObj.weeklyAmount);
						$("#monthly").val(resObj.monthlyAmount);
						$("#volumeId").val(resObj.id);

						console.log(resObj);
						$("#dailyVolumeRemain").text(
								resObj.displayDailyVolumeQuota);
						$("#weeklyVolumeRemain").text(
								resObj.displayWeeklyVolumeQuota);
						$("#monthlyVolumeRemain").text(
								resObj.displayMonthlyVolumeQuota);
						break;
					}
				}

			});
		}
	</script>
	<script>
		function setTicketLimit() {
			$.post("getFrmDetailsByTicket", {
				merchantPayId : $("#merchantPayId").val(),
				selectedCurrency : $("#currency").val()
			}, function(response) {
				var res = JSON
						.parse(JSON.stringify(response.fraudDataByTicket));
				var frmByTicket = [];
				frmByTicket = res;
				if (frmByTicket.length < 1) {
					var minTicketSize = $("#minTicketSize").val(" ");
					var maxTicketSize = $("#maxTicketSize").val(" ");
					$("#minTicketSize").val('1');
					$("#maxTicketSize").val('1000');
				} else {
					for (var i = 0; i < frmByTicket.length; i++) {
						var resObj = frmByTicket[i];
						$("#minTicketSize").val(resObj.minTransactionAmount);
						$("#maxTicketSize").val(resObj.maxTransactionAmount);
						$("#merchantProfile").val(resObj.merchantProfile);
						$('#select2-merchantProfile-container').text(
								resObj.merchantProfile);
						$("#ticketId").val(resObj.id);
						break;
					}
				}
			});
		}
	</script>
	<script>
		function validateField() {
			var regex = /^[0-9_]+$/;
			var numRegex = /^[+-]?(\d*\.)?\d+$/;
			var minLimit = parseFloat(document.getElementById("minTicketSize").value);
			var maxLimit = parseFloat(document.getElementById("maxTicketSize").value);
			var daily = parseFloat(document.getElementById("daily").value);
			var weekly = parseFloat(document.getElementById("weekly").value);
			var monthly = parseFloat(document.getElementById("monthly").value);
			var mopDaily = parseInt(document.getElementById("mopDaily").value);
			var mopWeekly = parseInt(document.getElementById("mopWeekly").value);
			var mopMonthly = parseInt(document.getElementById("mopMonthly").value);

			if ($.trim($('#minTicketSize').val()).length == 0) {
				error_minTicketSize = 'Enter min. Amount Per Transaction';
				$('#error_minTicketSize').text(error_minTicketSize);
				$('#minTicketSize').addClass('error');
			} else if (!numRegex.test($('#minTicketSize').val())) {
				error_minTicketSize = ' only numbers  are  allowed';
				$('#error_minTicketSize').text(error_minTicketSize);
				$('#minTicketSize').addClass('error');
			} else if (minLimit < 1) {
				error_minTicketSize = 'Enter value greater than zero';
				$('#error_minTicketSize').text(error_minTicketSize);
				$('#minTicketSize').addClass('error');
			} else if (maxLimit < minLimit) {

				error_minTicketSize = 'min. Amount Per Transaction limit cannot be greater than max. Amount Per Transaction limit';
				$('#error_minTicketSize').text(error_minTicketSize);
				$('#minTicketSize').addClass('error');
			} else {
				error_minTicketSize = '';
				$('#error_minTicketSize').text(error_minTicketSize);
				$('#minTicketSize').removeClass('error');
			}

			if ($.trim($('#maxTicketSize').val()).length == 0) {
				error_maxTicketSize = 'Enter max. Amount Per Transaction';
				$('#error_maxTicketSize').text(error_maxTicketSize);
				$('#maxTicketSize').addClass('error');
			} else if (!numRegex.test($('#maxTicketSize').val())) {
				error_maxTicketSize = ' only numbers  are  allowed';
				$('#error_maxTicketSize').text(error_maxTicketSize);
				$('#maxTicketSize').addClass('error');
			} else if (maxLimit < 1) {
				error_maxTicketSize = 'Enter value greater than zero';
				$('#error_maxTicketSize').text(error_minTicketSize);
				$('#maxTicketSize').addClass('error');
			} else if (maxLimit < minLimit) {
				error_maxTicketSize = 'max. Amount Per Transaction limit cannot be lesser than min. Amount Per Transaction limit';
				$('#error_maxTicketSize').text(error_maxTicketSize);
				$('#maxTicketSize').addClass('error');
			} else {
				error_maxTicketSize = '';
				$('#error_maxTicketSize').text(error_maxTicketSize);
				$('#maxTicketSize').removeClass('error');
			}

			if ($.trim($('#daily').val()).length == 0) {
				error_daily = 'Enter Max. Transaction Count Per Day';
				$('#error_daily').text(error_daily);
				$('#daily').addClass('error');
			} else if (!numRegex.test($('#daily').val())) {
				error_daily = ' only numbers  are  allowed';
				$('#error_daily').text(error_daily);
				$('#daily').addClass('error');
			} else if (daily < 1) {
				error_daily = 'Max. Transaction Count Per Day limit should be greater than 0';
				$('#error_daily').text(error_daily);
				$('#daily').addClass('error');
			} else if (daily > weekly || daily > monthly) {
				error_daily = 'Max. Transaction Count Per Day limit can not be  greater than Max. Transaction Count Per week/month limit';
				$('#error_daily').text(error_daily);
				$('#daily').addClass('error');
			} else {
				error_daily = '';
				$('#error_daily').text(error_daily);
				$('#daily').removeClass('error');
			}

			if ($.trim($('#weekly').val()).length == 0) {
				error_weekly = 'Enter Max. Transaction Count Per Week';
				$('#error_weekly').text(error_weekly);
				$('#weekly').addClass('error');
			} else if (!numRegex.test($('#weekly').val())) {
				error_weekly = ' only numbers  are  allowed';
				$('#error_weekly').text(error_weekly);
				$('#weekly').addClass('error');
			} else if (weekly < 1) {
				error_weekly = 'Max. Transaction Count Per Week limit should be greater than 0';
				$('#error_weekly').text(error_weekly);
				$('#weekly').addClass('error');
			} else if (daily > weekly) {
				error_weekly = 'Max. Transaction Count Per Week limit can not be  lesser than Max. Transaction Count Per Day limit';
				$('#error_weekly').text(error_weekly);
				$('#weekly').addClass('error');
			} else if (weekly > monthly) {
				error_weekly = 'Max. Transaction Count Per Week limit can not be  greater than Max. Transaction Count Per Month limit';
				$('#error_weekly').text(error_weekly);
				$('#weekly').addClass('error');
			} else {
				error_weekly = '';
				$('#error_weekly').text(error_weekly);
				$('#weekly').removeClass('error');
			}

			if ($.trim($('#monthly').val()).length == 0) {
				error_monthly = 'Enter Max. Transaction Count Per Month';
				$('#error_monthly').text(error_monthly);
				$('#monthly').addClass('error');
			} else if (!numRegex.test($('#monthly').val())) {
				error_monthly = ' only numbers  are  allowed';
				$('#error_monthly').text(error_monthly);
				$('#monthly').addClass('error');
			} else if (monthly < 1) {
				error_monthly = 'Max. Transaction Count Per Month limit should be greater than 0';
				$('#error_monthly').text(error_monthly);
				$('#monthly').addClass('error');
			} else if (monthly < daily || monthly < weekly) {
				error_monthly = 'Max. Transaction Count Per Month limit can not be  lesser than Max. Transaction Count Per Day/week limit';
				$('#error_monthly').text(error_monthly);
				$('#monthly').addClass('error');
			} else {
				error_monthly = '';
				$('#error_monthly').text(error_monthly);
				$('#monthly').removeClass('error');
			}

			if ($.trim($('#mopDaily').val()).length == 0) {
				error_mopDaily = 'Enter Max. Amount Limit Per Day';
				$('#error_mopDaily').text(error_mopDaily);
				$('#mopDaily').addClass('error');
			} else if (!regex.test($('#mopDaily').val())) {
				error_mopDaily = ' only numbers  are  allowed';
				$('#error_mopDaily').text(error_mopDaily);
				$('#mopDaily').addClass('error');
			} else if (mopDaily < 1) {
				error_mopDaily = 'Max. Amount Limit Per Day should be greater than 0';
				$('#error_mopDaily').text(error_mopDaily);
				$('#mopDaily').addClass('error');
			} else if (mopDaily > mopWeekly || mopDaily > mopMonthly) {
				error_mopDaily = 'Max. Amount Limit Per Day limit cannot be greater than Max. Amount Limit Per week/month limit';
				$('#error_mopDaily').text(error_mopDaily);
				$('#mopDaily').addClass('error');
			} else {
				error_mopDaily = '';
				$('#error_mopDaily').text(error_mopDaily);
				$('#mopDaily').removeClass('error');
			}

			if ($.trim($('#mopWeekly').val()).length == 0) {
				error_mopWeekly = 'Enter Max. Amount Limit Per Week';
				$('#error_mopWeekly').text(error_mopWeekly);
				$('#mopWeekly').addClass('error');
			} else if (!regex.test($('#mopWeekly').val())) {
				error_mopWeekly = ' only numbers  are  allowed';
				$('#error_mopWeekly').text(error_mopWeekly);
				$('#mopWeekly').addClass('error');
			} else if (mopWeekly < 1) {
				error_mopWeekly = ' Max. Amount Limit Per Week should be greater than 0';
				$('#error_mopWeekly').text(error_mopWeekly);
				$('#mopWeekly').addClass('error');
			} else if (mopWeekly < mopDaily) {
				error_mopWeekly = ' Max. Amount Limit Per Week cannnot be lesser than Max. Amount Limit Per Day';
				$('#error_mopWeekly').text(error_mopWeekly);
				$('#mopWeekly').addClass('error');
			} else if (mopWeekly > mopMonthly) {
				error_mopWeekly = ' Max. Amount Limit Per Week  cannnot be greater than Max. Amount Limit Per Month';
				$('#error_mopWeekly').text(error_mopWeekly);
				$('#mopWeekly').addClass('error');
			} else {
				error_mopWeekly = '';
				$('#error_mopWeekly').text(error_mopWeekly);
				$('#mopWeekly').removeClass('error');
			}

			if ($.trim($('#mopMonthly').val()).length == 0) {
				error_mopMonthly = 'Enter Max. Amount Limit Per Month';
				$('#error_mopMonthly').text(error_mopMonthly);
				$('#mopMonthly').addClass('error');
			} else if (!regex.test($('#mopMonthly').val())) {
				error_mopMonthly = ' only numbers  are  allowed';
				$('#error_mopMonthly').text(error_mopMonthly);
				$('#mopMonthly').addClass('error');
			} else if (mopMonthly < 1) {
				error_mopMonthly = 'Max. Amount Limit Per Month should be greater than 0';
				$('#error_mopMonthly').text(error_mopMonthly);
				$('#mopMonthly').addClass('error');
			} else if (mopMonthly < mopDaily || mopMonthly < mopWeekly) {
				error_mopMonthly = 'Max. Amount Limit Per Month can not be lesser than Max. Amount Limit Per Day/week limit';
				$('#error_mopMonthly').text(error_mopMonthly);
				$('#mopMonthly').addClass('error');
			} else {
				error_mopMonthly = '';
				$('#error_mopMonthly').text(error_mopMonthly);
				$('#mopMonthly').removeClass('error');
			}

			// Merchant PayId Validation
			if ($.trim($('#merchantPayId').val()).length == 0
					|| $('#merchantPayId').val() == ''
					|| $('#merchantPayId').val() == null) {
				error_merchantPayId = 'Please Select Merchant';
				$('#error_merchantPayId').text(error_merchantPayId);
				$('#merchantPayId').addClass('error');
			} else {
				error_merchantPayId = '';
				$('#error_merchantPayId').text(error_merchantPayId);
				$('#merchantPayId').removeClass('error');
			}

			// Currency Validation
			if ($.trim($('#currency').val()).length == 0
					|| $('#currency').val() == ''
					|| $('#currency').val() == null) {
				error_currencyName = 'Please Select Currency';
				$('#error_currencyName').text(error_currencyName);
				$('#currencyName').addClass('error');
			} else {
				error_currencyName = '';
				$('#error_currencyName').text(error_currencyName);
				$('#currencyName').removeClass('error');
			}

			// Payment Type Validation
			if ($.trim($('#paymentType').val()).length == 0
					|| $('#paymentType').val() == ''
					|| $('#paymentType').val() == null) {
				error_paymentMethod = 'Please Select Payment Type';
				$('#error_paymentMethod').text(error_paymentMethod);
				$('#paymentMethod').addClass('error');
			} else {
				error_paymentMethod = '';
				$('#error_paymentMethod').text(error_paymentMethod);
				$('#paymentMethod').removeClass('error');
			}

			// Merchant Profile Validation
			if ($.trim($('#merchantProfile').val()).length == 0) {
				error_merchantProfile = 'Please Select Merchant Profile';
				$('#error_merchantProfile').text(error_merchantProfile);
				$('#merchantProfile').addClass('error');
			} else {
				error_merchantProfile = '';
				$('#error_merchantProfile').text(error_merchantProfile);
				$('#merchantProfile').removeClass('error');
			}

			if (error_mopMonthly != '' || error_mopWeekly != ''
					|| error_mopDaily != '' || error_daily != ''
					|| error_weekly != '' || error_monthly != ''
					|| error_maxTicketSize != '' || error_minTicketSize != ''
					|| error_merchantProfile !== ''
					|| error_paymentMethod != '' || error_merchantPayId != '') {
				return false;
			} else {
				return true;
			}
		}
	</script>

<script>
	function saveDetails() {

		var token = document.getElementsByName("token")[0].value;
		if (!validateField()) {
			return false;
		} else {

			$.post("saveFrmDetails", {
				id : $("#id").val(),
				ticketId : $("#ticketId").val(),
				volumeId : $("#volumeId").val(),
				merchantPayId : $("#merchantPayId").val(),
				minTicketSize : $("#minTicketSize").val(),
				maxTicketSize : $("#maxTicketSize").val(),
				daily : $("#daily").val(),
				weekly : $("#weekly").val(),
				monthly : $("#monthly").val(),
				merchantProfile : $("#merchantProfile").val(),
				selectedCurrency : $("#currency").val(),
				mop : $("#paymentType").val(),
				mopDaily : $("#mopDaily").val(),
				mopWeekly : $("#mopWeekly").val(),
				mopMonthly : $("#mopMonthly").val()
			}, function(result) {
				alert("FRM Details Added  Successfully");
				window.location.reload();
			});
		}
	}
</script>

	<script>
		document.getElementById('poCheckbox').addEventListener('change',
				function() {
					if (this.checked) {
						window.location.href = 'PoFrm';
					}
				});
	</script>
</body>
</html>