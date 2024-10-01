<!DOCTYPE html
	PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@taglib prefix="s" uri="/struts-tags" %>
	<html>

	<head>
		<title>Summary Report</title>


		<link rel="shortcut icon" href="assets/media/images/paylogo.svg" />
		<!--begin::Fonts-->
		<link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Inter:300,400,500,600,700" />
		<!--end::Fonts-->
		<!--begin::Vendor Stylesheets(used by this page)-->
		<!-- <link href="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.css" rel="stylesheet"
						type="text/css" /> -->
		<link href="../assets/plugins/custom/datatables/datatables.bundle.css" rel="stylesheet" type="text/css" />
		<!--end::Vendor Stylesheets-->
		<!--begin::Global Stylesheets Bundle(used by all pages)-->
		<link href="../assets/plugins/global/plugins.bundle.css" rel="stylesheet" type="text/css" />
		<link href="../assets/css/style.bundle.css" rel="stylesheet" type="text/css" />
		<link href="../assets/css/custom/custom-style.css" rel="stylesheet" type="text/css" />

		<script src="../assets/plugins/global/plugins.bundle.js"></script>
		<script src="../assets/js/scripts.bundle.js"></script>
		<script src="../js/jquery.popupoverlay.js"></script>


		<link href="../css/select2.min.css" rel="stylesheet" />
		<script src="../js/jquery.select2.js" type="text/javascript"></script>
		<style>
			.dt-buttons.btn-group.flex-wrap{
				display: none;
			}
			svg {
				margin-top: 6px;
			}
			#checkboxes {
	display: none;
	border: 1px #DADADA solid;
	height:180px;
	overflow-y: scroll;
	position:absolute;
	background:#fff;
	z-index:1;
	padding: 10px;
}
#checkboxes1 label {
  width: 74%;
}
#checkboxes1 input {
  width:18%;
}
div#checkboxes1{
position: absolute;
    overflow-y: auto;
    border: 1px solid #dadada;
    display: block;
    height: 180px;
    background: #ffff;
    z-index: 1;
    padding: 10px;
    width: 31%;
	display: none;
}
		</style>
		<script type="text/javascript">
			$(document).ready(function () {
					$("#merchants").select2();
					$("#mopType").select2();
					$("#paymentMethods").select2();
					$("#cardHolderType").select2();
					$("#transactionType").select2();
					$("#paymentsRegion").select2();
					$("#currency").select2();

					document.getElementById("loadingInner").style.display = "none";
					document.getElementById("loading").style.display = "none";
					$(document).click(function () {
						expanded = false;
						$('#checkboxes1').hide();
					});
					$('#checkboxes1').click(function (e) {
						e.stopPropagation();
					});
					populateDataTable1();

				});

		</script>

		<script type="text/javascript">

					function populateDataTable1() {

							debugger
							//alert("on submit");
							var token = document.getElementsByName("token")[0].value;
							table = $('#summaryReportDataTable')
								.DataTable(
									{
										dom: 'Brtip',
										buttons: [
											{
												extend: 'print',
												exportOptions: {
													columns: ':visible'
												}
											},
											'colvis', 'copy', 'csv', 'excel', 'pdf', 'print',
										],
										scrollY: true,
										scrollX: true,
										searchDelay: 500,
										processing: false,
										// serverSide: true,
										order: [[1, 'desc']],
										stateSave: true,
										dom: 'Brtipl',
										paging: true,
										"ajax": {
											"url": "summaryReportAction",
											"type": "POST",
											"data": function (d) {
												return generatePostData(d);

											}
										},
										"bProcessing": true,
										"bLengthChange": true,
										"bDestroy": true,
										"aoColumns": [
											{ data: 'srNo' },
											{ data: 'pgRefNum' },
											{ data: 'orderId' },
											{ data: 'acquirerType' },
											{ data: 'businessName' },
											{
												data: 'amount',
												render: function (data) {
													return inrFormat(data);
												}
											},
											{
												data: 'totalAmount', render: function (data) {
													return inrFormat(data);
												}
											},
											{ data: 'txnType' },
											{ data: 'transactionCaptureDate' },
											{ data: 'dateFrom' },
											{
												data: 'netMerchantPayableAmount', render: function (data) {
													return inrFormat(data);
												}
											},
											{ data: 'paymentMethods' },
											{ data: 'mopType' },
											{ data: 'currency' },
											{
												data: 'acquirerSurchargeAmount', render: function (data) {
													return inrFormat(data);
												}
											},
											{
												data: 'acquirerGst', render: function (data) {
													return inrFormat(data);
												}
											},

											{
												data: 'pgSurchargeAmount', render: function (data) {
													return inrFormat(data);
												}
											},

											{
												data: 'pgGst', render: function (data) {
													return inrFormat(data);
												}
											},

											{ data: 'acqId' },

											{ data: 'rrn' },

											{ data: 'transactionRegion' },

											{ data: 'cardMask' },

											{ data: 'postSettledFlag' },

											{
												data: "refundButtonName",
												render: function (data) {
													return `<input type="button" name="surchargeBtn" value=` + data.replace(/ /g, '&nbsp;') + `
											data-toggle="modal" data-target="#refundAccept" onclick = "setValues(this)"
											class="btn btn-lg btn-success" /> `;

												}
											}

										]
									});


						}
					


			function generatePostData(d) {
				debugger
				var token = document.getElementsByName("token")[0].value;
				var acquirer = document.getElementById("selectBox1").title;
				var merchantEmailId = document.getElementById("merchants").value;
				var paymentMethods = document.getElementById("paymentMethods").value;
				var mopType = document.getElementById("mopType").value;
				var currency = document.getElementById("currency").value;
				var paymentsRegion = document.getElementById("paymentsRegion").value;
				var cardHolderType = document.getElementById("cardHolderType").value;
				var pgRefNum = document.getElementById("pgRefNum").value;
				var orderId = document.getElementById("orderId").value;
				var transactionType = document.getElementById("transactionType").value;
				
				if (merchantEmailId == '') {
					merchantEmailId = 'ALL'
				}
				if (paymentMethods == '') {
					paymentMethods = 'ALL'
				}

				if (currency == '') {
					currency = 'ALL'
				}
				if (paymentsRegion == '') {
					paymentsRegion = 'ALL'
				}
				if (cardHolderType == '') {
					cardHolderType = 'ALL'
				}
				if (transactionType == '') {
					transactionType = 'ALL'
				}
				if (acquirer == '') {
					acquirer = 'ALL'
				}

				var obj = {

					paymentMethods: paymentMethods,
					dateFrom: document.getElementById("dateFrom").value,
					dateTo: document.getElementById("dateTo").value,
					phoneNo: document.getElementById("phoneNo").value,
					merchantEmailId: merchantEmailId,
					currency: currency,
					mopType: mopType,
					acquirer: acquirer,
					transactionType: transactionType,
					paymentsRegion: paymentsRegion,
					cardHolderType: cardHolderType,
					pgRefNum: pgRefNum,
					orderId: orderId,
					draw: 1,
					length: 10000,
					start: 0,
					token: token,
					"struts.token.name": "token",
				};

				return obj;
			}

		</script>

		<script type="text/javascript">
			var totalRefund = "0.00";
			function setValues(val) {
				debugger
				var row = val.parentElement.parentElement.cells;

				var token = document.getElementsByName("token")[0].value;
				var myData = {
					token: token,
					"struts.token.name": "token",
					"orderId": row[2].innerText
				}
				
				var txnType = row[7].innerText;

				$.ajax({
					url: "totalRefundByOrderIdAction",
					timeout: 0,
					type: "POST",
					data: myData,
					success: function (response) {
						debugger
						
						totalRefund = response.totalRefund;

						document.getElementById("pgRefConf").value = row[1].innerText;
						document.getElementById("orderIDConf").value = row[2].innerText;
						document.getElementById("refundOrderIDConf").value = '';
						var amtRefund = inrFormat(parseFloat(totalRefund.replace(/,/g, '')));
						var amtAvailable = inrFormat(parseFloat(row[5].innerText.replace(/,/g, '')) - parseFloat(totalRefund.replace(/,/g, '')));
						document.getElementById("amtRef").value = amtRefund.toString().includes('.') ? inrFormat(parseFloat(totalRefund.replace(/,/g, ''))) : inrFormat(parseFloat(totalRefund.replace(/,/g, ''))) + ".00";
						document.getElementById("amtAvail").value = amtAvailable.toString().includes('.') ? inrFormat(parseFloat(row[5].innerText.replace(/,/g, '')) - parseFloat(totalRefund.replace(/,/g, ''))) : inrFormat(parseFloat(row[5].innerText.replace(/,/g, '')) - parseFloat(totalRefund.replace(/,/g, ''))) + ".00";
						document.getElementById("amtConf").value = document.getElementById("amtAvail").value;
						document.getElementById("currencyConf").value = row[13].innerText;

						if (parseFloat("0.00") == parseFloat(amtAvailable) || txnType == "REFUND") {
							document.getElementById("btnRefundConf").disabled = true;
						} else {
							document.getElementById("btnRefundConf").disabled = false;
						}
						$('#refundAccept').modal('show');

					},
					error: function (xhr, textStatus, errorThrown) {
						$('#refundAccept').modal('hide');
						alert("Something Went Wrong");
					}
				});


			}

			function hidemodal(){
				$('#refundAccept').modal('hide');
			}

		</script>

		<script type="text/javascript">
			$(document).ready(function () {
				$(document).click(function () {
					expanded = false;
					$('#checkboxes').hide();
				});
				$('#checkboxes').click(function (e) {
					e.stopPropagation();
				});


			});
		</script>

		<script>
			var expanded = false;

			function showCheckboxes(e, uid) {
				var checkboxes = document.getElementById("checkboxes" + uid);
				if (!expanded) {
					checkboxes.style.display = "block";
					expanded = true;
				} else {
					checkboxes.style.display = "none";
					expanded = false;
				}
				e.stopPropagation();

			}

			function valdPhoneNo() {
				var phoneElement = document.getElementById("phoneNo");
				var value = phoneElement.value.trim();
				if (value.length > 0) {
					var phone = phoneElement.value;
					var phoneexp = /^[0-9]{8,13}$/;
					if (!phone.match(phoneexp)) {
						document.getElementById('invalid-phone').innerHTML = "Please enter valid Phone";

						return false;
					} else {
						document.getElementById('invalid-phone').innerHTML = "";
						return true;
					}
				} else {
					phoneElement.focus();
					document.getElementById('invalid-phone').innerHTML = "";
					return true;
				}
			}

			function getCheckBoxValue(uid) {
				var allInputCheckBox = document.getElementsByClassName("myCheckBox" + uid);

				var allSelectedAquirer = [];
				for (var i = 0; i < allInputCheckBox.length; i++) {

					if (allInputCheckBox[i].checked) {
						allSelectedAquirer.push(allInputCheckBox[i].value);
					}
				}
				var test = document.getElementById('selectBox' + uid);
				document.getElementById('selectBox' + uid).setAttribute('title', allSelectedAquirer.join());
				if (allSelectedAquirer.join().length > 28) {
					var res = allSelectedAquirer.join().substring(0, 27);
					document.querySelector("#selectBox" + uid + " option").innerHTML = res + '...............';
				} else if (allSelectedAquirer.join().length == 0) {
					document.querySelector("#selectBox" + uid + " option").innerHTML = 'ALL';
				} else {
					document.querySelector("#selectBox" + uid + " option").innerHTML = allSelectedAquirer.join();
				}
			}

		

			function checkRefNo() {
				var refValue = document.getElementById("pgRefNum").value;
				var refNoLength = refValue.length;
				if ((refNoLength < 16) && (refNoLength > 0)) {
					document.getElementById("submit").disabled = true;
					document.getElementById("validRefNo").style.display = "block";
				}
				else if (refNoLength == 0) {
					document.getElementById("submit").disabled = false;
					document.getElementById("validRefNo").style.display = "none";
				} else {
					document.getElementById("submit").disabled = false;
					document.getElementById("validRefNo").style.display = "none";
				}
			}

			function checkOrderId() {
				var refValue = document.getElementById("orderId").value;
				var refNoLength = refValue.length;
				if (refNoLength == 0) {
					document.getElementById("submit").disabled = false;
					document.getElementById("validRefNo").style.display = "none";
				} else {
					document.getElementById("submit").disabled = false;
					document.getElementById("validRefNo").style.display = "none";
				}
			}
			function validateOrderIdvalue(orderId) {
				setTimeout(function () {
					//var orderIdreg =/^[0-9a-zA-Z\b\_-\s\+?.*?]+$/;
					var orderIdreg = /^[0-9a-zA-Z\b\_-\s\+.]+$/;
					if (orderId.value !== "") {
						if (orderIdreg.test(orderId.value) == false) {
							document.getElementById("validOrderIdValue").style.display = "block";
							document.getElementById("submit").disabled = true;
							document.getElementById("download").disabled = true;
						}
					} else {
						document.getElementById("validOrderIdValue").style.display = "none";
					}

				}, 400);
			}
			function validateRefundOrderIdvalue(refundOrderIDConf) {
				setTimeout(function () {
					//var orderIdreg =/^[0-9a-zA-Z\b\_-\s\+?.*?]+$/;
					var refundOrderIdreg = /^[0-9a-zA-Z\b\_-\s\+.]+$/;
					if (refundOrderIDConf.value !== "") {
						if (refundOrderIdreg.test(refundOrderIDConf.value) == false) {
							document.getElementById("validRefundOrderIdValue").style.display = "block";
							document.getElementById("btnRefundConf").disabled = true;
							document.getElementById("download").disabled = true;
						}
					} else {
						document.getElementById("validRefundOrderIdValue").style.display = "none";
						document.getElementById("btnRefundConf").disabled = false;
					}

				}, 400);
			}


			function refundFunction(operation) {


				var ordId = document.getElementById("orderIDConf").value;
				var refOrdId = document.getElementById("refundOrderIDConf").value;
				var pgRef = document.getElementById("pgRefConf").value;
				var amt = document.getElementById("amtConf").value.replace(/,/g, '').trim();
				var currency = document.getElementById("currencyConf").value;
				var token = document.getElementsByName("token")[0].value;
				var availAmt = document.getElementById("amtAvail").value.replace(/,/g, '').trim();

				if (amt == '' || amt < 0) {

					alert("Please enter a valid Refund amount!");
					return false;
				}

				if (parseFloat(amt) > parseFloat(availAmt)) {
					alert("Please check the Amount field, Refund amount More than the Available amount");
					return false;
				}



				document.getElementById("loadingInner").style.display = "block";
				document.getElementById("btnRefundConf").disabled = true;
				$.ajax({
					type: "POST",
					url: "refundFromCrm",
					timeout: 0,
					data: { "orderId": ordId, "currencyCode": currency, "refundOrderId": refOrdId, "refundAmount": amt, "pgRefNum": pgRef, "token": token, "struts.token.name": "token", },
					success: function (data) {

						document.getElementById("loadingInner").style.display = "none";
						document.getElementById("btnRefundConf").disabled = false;
						var response = ((data["Invalid request"] != null) ? (data["Invalid request"].response[0]) : (data.response));
						if (null != response) {
							alert(response);
						}

						window.location.reload();
					},
					error: function (data) {

						document.getElementById("loadingInner").style.display = "none";
						document.getElementById("btnRefundConf").disabled = false;
						alert("Unable to process refund");
					}

				});
			}


		</script>
	</head>

	<body>
		<div id="loading" style="text-align: center;">
			<img id="loading-image" style="width: 70px; height: 70px;" src="../image/sand-clock-loader.gif"
				alt="Sending SMS..." />
		</div>


		<div class="modal" id="refundAccept" tabindex="-1" role="dialog" aria-labelledby="myModalLabel"
			aria-hidden="true">
			<div id="loadingInner" display="none">
				<img id="loading-image-inner" src="../image/sand-clock-loader.gif" alt="BUSY..." />
			</div>
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header" align="center">Confirm Refund
						Instruction</div>
					<div class="modal-body">
						Please Verify the details before submission.

						<table class="table">
							<tr>
								<th>Pg Ref Num</th>
								<td id="pgRefConfTd">
								<td id="otpMerchSuraccept"><input id="pgRefConf" readonly="true" class="input-control"
										autocomplete="off" type="text" name="pgRefConf"></td>
								</td>
							</tr>
							<tr>
								<th>Order ID</th>
								<td id="orderIDConfTd">
								<td id="otpMerchSuraccept"><input id="orderIDConf" readonly="true" class="input-control"
										autocomplete="off" type="text" name="orderIDConf"></td>
								</td>
							</tr>

							<tr>
								<th>Currency</th>
								<td id="currencyConfTd">
								<td id="otpMerchSuraccept"><input id="currencyConf" readonly="true"
										class="input-control" autocomplete="off" type="text" name="currencyConf"></td>
								</td>
							</tr>
							<tr>
								<th>Refund Order ID</th>
								<td id="refundOrderIDConfTd">
								<td id="otpMerchSuraccept"><input id="refundOrderIDConf"
										onKeyDown="validateRefundOrderIdvalue(this);"
										onkeypress="return validateRefundOrderId(event);" ondrop="return false;"
										onpaste="validateRefundOrderIdvalue(this);" placeholder="< Optional >"
										class="input-control" autocomplete="off" type="text" name="refundOrderIDConf">
								</td>
								<span id="validRefundOrderIdValue" style="color: red; display: none;">Please Enter Valid
									OrderId.</span>
								</td>
							</tr>
							<tr>
								<th>Refunded Amount</th>
								<td id="amtRefTd">
								<td id="otpMerchSuraccept"><input id="amtRef" readonly="true" class="input-control"
										autocomplete="off" type="text" name="amtRef"></td>
								</td>
							</tr>
							<tr>
								<th>Available Amount</th>
								<td id="amtAvailTd">
								<td id="otpMerchSuraccept"><input id="amtAvail" readonly="true" class="input-control"
										autocomplete="off" type="text" name="amtAvail"></td>
								</td>
							</tr>
							<tr>
								<th>Enter Refund Amount</th>
								<td id="amtConfTd">
								<td id="otpMerchSuraccept"><input id="amtConf" type="number" min="" max="9999999999.99"
										step="0.01" class="input-control" autocomplete="off" name="amtConf"
										onkeypress="return isNumberKeyAmount(event)"></td>
								</td>
							</tr>


						</table>

					</div>

					<div align="center">
						<button type="button" class="btn btn-lg btn-success" id="btnRefundConf"
							onClick='refundFunction("accept")'>Submit</button>
						<button type="button" class="btn btn-lg btn-danger" id="btnRefundCancel"
							data-dismiss="modal" onclick="hidemodal()">Cancel</button>
					</div>
					<br><br>
				</div>
			</div>
			
		</div>


		<div class="content d-flex flex-column flex-column-fluid" id="kt_content">
			<!--begin::Toolbar-->
			<div class="toolbar" id="kt_toolbar">
				<!--begin::Container-->
				<div id="kt_toolbar_container" class="container-fluid d-flex flex-stack">
					<!--begin::Page title-->
					<div data-kt-swapper="true" data-kt-swapper-mode="prepend"
						data-kt-swapper-parent="{default: '#kt_content_container', 'lg': '#kt_toolbar_container'}"
						class="page-title d-flex align-items-center me-3 flex-wrap mb-5 mb-lg-0 lh-1">
						<!--begin::Title-->
						<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">
							Summary Report</h1>
						<!--end::Title-->
						<!--begin::Separator-->
						<span class="h-20px border-gray-200 border-start mx-4"></span>
						<!--end::Separator-->
						<!--begin::Breadcrumb-->
						<ul class="breadcrumb breadcrumb-separatorless fw-semibold fs-7 my-1">
							<!--begin::Item-->
							<li class="breadcrumb-item text-muted"><a href="home"
									class="text-muted text-hover-primary">Dashboard</a>
							</li>
							<!--end::Item-->
							<!--begin::Item-->
							<li class="breadcrumb-item"><span class="bullet bg-gray-200 w-5px h-2px"></span>
							</li>
							<!--end::Item-->
							<!--begin::Item-->
							<li class="breadcrumb-item text-muted">Transaction
								Reports
							</li>
							<!--end::Item-->
							<!--begin::Item-->
							<li class="breadcrumb-item"><span class="bullet bg-gray-200 w-5px h-2px"></span>
							</li>
							<!--end::Item-->
							<!--begin::Item-->
							<li class="breadcrumb-item text-dark">Summary Report
							</li>
							<!--end::Item-->
						</ul>
						<!--end::Breadcrumb-->
					</div>
					<!--end::Page title-->

				</div>
				<!--end::Container-->
			</div>

			<div class="post d-flex flex-column-fluid" id="kt_post">
				<!--begin::Container-->
				<div id="kt_content_container" class="container-xxl">
					<div class="row my-5">
						<div class="col">
							<div class="card">
								<div class="card-body">
									<form action="" method="post" id="setteled_report_form">
										<div class="row my-3 align-items-center">
											<div class="col-md-4 fv-row">
												<label class="d-flex align-items-center fs-6 fw-bold mb-2">
													<span class="">PG REF NUMBER</span>
												</label>
												<!--end::Label-->
												<s:textfield id="pgRefNum" class="form-control form-control-solid"
													name="pgRefNum" type="text" value="" autocomplete="off"
													onkeypress="javascript:return isNumber (event)" maxlength="16"
													onblur="checkRefNo()"></s:textfield>
											</div>

											<div class="col-md-4 fv-row">
												<label class="d-flex align-items-center fs-6 fw-bold mb-2">
													<span class="">Order Id</span>
												</label>
												<!--end::Label-->
												<s:textfield id="orderId" class="form-control form-control-solid"
													name="orderId" type="text" value="" autocomplete="off"
													onKeyDown="validateOrderIdvalue(this);"
													onkeypress="return validateOrderId(event);" ondrop="return false;"
													onpaste="validateOrderIdvalue(this);" maxlength="50"></s:textfield>
											</div>


											<div class="col-md-4 fv-row">
												<label class="d-flex align-items-center fs-6 fw-bold mb-2">
													<span class="">PhoneNo</span>
												</label>
												<!--end::Label-->
												<s:textfield id="phoneNo" class="form-control form-control-solid"
													name="phoneNo" type="text" value="" autocomplete="off"
													onpaste="removeSpaces(this);" onkeyup="valdPhoneNo();"
													onkeypress="return validateOrderId(event);" ondrop="return false;">
												</s:textfield>
											</div>

										</div>


										<div class="row my-3 align-items-center">
											<div class="col-md-4 fv-row">
												<label class="d-flex align-items-center fs-6 fw-bold mb-2">
													<span class="">Merchant</span>
												</label>
												<!--end::Label-->
												<s:select name="merchants" class="form-select form-select-solid"
													id="merchants" headerKey="" headerValue="ALL" list="merchantList"
													listKey="emailId" listValue="businessName"
													autocomplete="off" />
											</div>

											<div class="col-md-4 fv-row">
												<label class="d-flex align-items-center fs-6 fw-bold mb-2">
													<span class="">Payment Method</span>
												</label>
												<!--end::Label-->
												<s:select name="paymentMethods" id="paymentMethods" headerValue="ALL"
													headerKey="ALL" list="@com.pay10.commons.util.PaymentType@values()"
													listValue="name" listKey="code"
													class="form-select form-select-solid" />
											</div>


											<div class="col-md-4 fv-row">
												<label class="d-flex align-items-center fs-6 fw-bold mb-2">
													<span class="">Mop Type</span>
												</label>
												<!--end::Label-->
												<s:select name="mopType" id="mopType" headerValue="ALL" headerKey="ALL"
													list="@com.pay10.commons.util.MopTypeUI@values()" listValue="name"
													listKey="code" class="form-select form-select-solid "
													 />
											</div>

										</div>


										<div class="row my-3 align-items-center">
											<div class="col-md-4 fv-row">
												<label class="d-flex align-items-center fs-6 fw-bold mb-2">
													<span class="">Transaction Type</span>
												</label>
												<!--end::Label-->
												<s:select headerKey="ALL" headerValue="ALL"
													class="form-select form-select-solid"
													list="#{'SALE':'SALE','REFUND':'REFUND'}" name="transactionType"
													id="transactionType" />
											</div>

											<div class="col-md-4 fv-row">
												<label class="d-flex align-items-center fs-6 fw-bold mb-2">
													<span class="">Transaction Region</span>
												</label>
												<!--end::Label-->
												<s:select headerKey="ALL" headerValue="ALL"
													class="form-select form-select-solid"
													list="#{'INTERNATIONAL':'International','DOMESTIC':'Domestic'}"
													name="paymentsRegion" id="paymentsRegion" />
											</div>


											<div class="col-md-4 fv-row">
												<label class="d-flex align-items-center fs-6 fw-bold mb-2">
													<span class="">Card Holder Type</span>
												</label>
												<!--end::Label-->
												<s:select headerKey="ALL" headerValue="ALL"
													class="form-select form-select-solid"
													list="#{'CONSUMER':'Consumer','COMMERCIAL':'Commercial'}"
													name="cardHolderType" id="cardHolderType" />
											</div>

										</div>


										<div class="row my-3 align-items-center">
											<div class="col-md-4 fv-row">
												<label class="d-flex align-items-center fs-6 fw-bold mb-2">
													<span class="">Acquirer</span>
												</label>
												<!--end::Label-->
												<div class="selectBox" id="selectBox1" onclick="showCheckboxes(event,1)"
													>
													<select class="form-select form-select-solid">
														<option>ALL</option>
													</select>
													<div class="overSelect"></div>
												</div>
												<div id="checkboxes1" onclick="getCheckBoxValue(1)">
													<s:checkboxlist headerKey="ALL" headerValue="ALL"
														list="@com.pay10.commons.util.AcquirerTypeUI@values()"
														id="acquirer" class="myCheckBox1 " listKey="code"
														listValue="name" name="acquirer" value="acquirer" />
												</div>
											</div>

											<div class="col-md-4 fv-row">
												<label class="d-flex align-items-center fs-6 fw-bold mb-2">
													<span class="">Currency</span>
												</label>
												<!--end::Label-->
												<s:select name="currency" id="currency" headerValue="ALL"
													headerKey="ALL" list="currencyMap"
													class="form-select form-select-solid"  />
											</div>


											<div class="col-md-4 fv-row">
												<label class="d-flex align-items-center fs-6 fw-bold mb-2">
													<span class="">Capture Date From</span>
												</label>
												<!--end::Label-->

												<span class="svg-icon svg-icon-2 position-absolute mx-4">
													<svg width="24" height="24" viewBox="0 0 24 24" fill="none"
														xmlns="http://www.w3.org/2000/svg">
														<path opacity="0.3"
															d="M21 22H3C2.4 22 2 21.6 2 21V5C2 4.4 2.4 4 3 4H21C21.6 4 22 4.4 22 5V21C22 21.6 21.6 22 21 22Z"
															fill="currentColor"></path>
														<path
															d="M6 6C5.4 6 5 5.6 5 5V3C5 2.4 5.4 2 6 2C6.6 2 7 2.4 7 3V5C7 5.6 6.6 6 6 6ZM11 5V3C11 2.4 10.6 2 10 2C9.4 2 9 2.4 9 3V5C9 5.6 9.4 6 10 6C10.6 6 11 5.6 11 5ZM15 5V3C15 2.4 14.6 2 14 2C13.4 2 13 2.4 13 3V5C13 5.6 13.4 6 14 6C14.6 6 15 5.6 15 5ZM19 5V3C19 2.4 18.6 2 18 2C17.4 2 17 2.4 17 3V5C17 5.6 17.4 6 18 6C18.6 6 19 5.6 19 5Z"
															fill="currentColor"></path>
														<path
															d="M8.8 13.1C9.2 13.1 9.5 13 9.7 12.8C9.9 12.6 10.1 12.3 10.1 11.9C10.1 11.6 10 11.3 9.8 11.1C9.6 10.9 9.3 10.8 9 10.8C8.8 10.8 8.59999 10.8 8.39999 10.9C8.19999 11 8.1 11.1 8 11.2C7.9 11.3 7.8 11.4 7.7 11.6C7.6 11.8 7.5 11.9 7.5 12.1C7.5 12.2 7.4 12.2 7.3 12.3C7.2 12.4 7.09999 12.4 6.89999 12.4C6.69999 12.4 6.6 12.3 6.5 12.2C6.4 12.1 6.3 11.9 6.3 11.7C6.3 11.5 6.4 11.3 6.5 11.1C6.6 10.9 6.8 10.7 7 10.5C7.2 10.3 7.49999 10.1 7.89999 10C8.29999 9.90003 8.60001 9.80003 9.10001 9.80003C9.50001 9.80003 9.80001 9.90003 10.1 10C10.4 10.1 10.7 10.3 10.9 10.4C11.1 10.5 11.3 10.8 11.4 11.1C11.5 11.4 11.6 11.6 11.6 11.9C11.6 12.3 11.5 12.6 11.3 12.9C11.1 13.2 10.9 13.5 10.6 13.7C10.9 13.9 11.2 14.1 11.4 14.3C11.6 14.5 11.8 14.7 11.9 15C12 15.3 12.1 15.5 12.1 15.8C12.1 16.2 12 16.5 11.9 16.8C11.8 17.1 11.5 17.4 11.3 17.7C11.1 18 10.7 18.2 10.3 18.3C9.9 18.4 9.5 18.5 9 18.5C8.5 18.5 8.1 18.4 7.7 18.2C7.3 18 7 17.8 6.8 17.6C6.6 17.4 6.4 17.1 6.3 16.8C6.2 16.5 6.10001 16.3 6.10001 16.1C6.10001 15.9 6.2 15.7 6.3 15.6C6.4 15.5 6.6 15.4 6.8 15.4C6.9 15.4 7.00001 15.4 7.10001 15.5C7.20001 15.6 7.3 15.6 7.3 15.7C7.5 16.2 7.7 16.6 8 16.9C8.3 17.2 8.6 17.3 9 17.3C9.2 17.3 9.5 17.2 9.7 17.1C9.9 17 10.1 16.8 10.3 16.6C10.5 16.4 10.5 16.1 10.5 15.8C10.5 15.3 10.4 15 10.1 14.7C9.80001 14.4 9.50001 14.3 9.10001 14.3C9.00001 14.3 8.9 14.3 8.7 14.3C8.5 14.3 8.39999 14.3 8.39999 14.3C8.19999 14.3 7.99999 14.2 7.89999 14.1C7.79999 14 7.7 13.8 7.7 13.7C7.7 13.5 7.79999 13.4 7.89999 13.2C7.99999 13 8.2 13 8.5 13H8.8V13.1ZM15.3 17.5V12.2C14.3 13 13.6 13.3 13.3 13.3C13.1 13.3 13 13.2 12.9 13.1C12.8 13 12.7 12.8 12.7 12.6C12.7 12.4 12.8 12.3 12.9 12.2C13 12.1 13.2 12 13.6 11.8C14.1 11.6 14.5 11.3 14.7 11.1C14.9 10.9 15.2 10.6 15.5 10.3C15.8 10 15.9 9.80003 15.9 9.70003C15.9 9.60003 16.1 9.60004 16.3 9.60004C16.5 9.60004 16.7 9.70003 16.8 9.80003C16.9 9.90003 17 10.2 17 10.5V17.2C17 18 16.7 18.4 16.2 18.4C16 18.4 15.8 18.3 15.6 18.2C15.4 18.1 15.3 17.8 15.3 17.5Z"
															fill="currentColor"></path>
													</svg>
												</span>
												<!--end::Svg Icon-->
												<!--end::Icon-->
												<!--begin::Datepicker-->
												<input class="form-control form-control-solid ps-12 flatpickr-input"
													placeholder="Select a date" name="dateFrom" id="dateFrom"
													type="text">
												<!--onchange="handleChange();"-->
											</div>

										</div>




										<div class="row my-3 align-items-center d-flex justify-content-start">
											<div class="col-md-4 fv-row">
												<label class="d-flex align-items-center fs-6 fw-bold mb-2">
													<span class="">Capture Date To</span>
												</label>
												<span class="svg-icon svg-icon-2 position-absolute mx-4">
													<svg width="24" height="24" viewBox="0 0 24 24" fill="none"
														xmlns="http://www.w3.org/2000/svg">
														<path opacity="0.3"
															d="M21 22H3C2.4 22 2 21.6 2 21V5C2 4.4 2.4 4 3 4H21C21.6 4 22 4.4 22 5V21C22 21.6 21.6 22 21 22Z"
															fill="currentColor"></path>
														<path
															d="M6 6C5.4 6 5 5.6 5 5V3C5 2.4 5.4 2 6 2C6.6 2 7 2.4 7 3V5C7 5.6 6.6 6 6 6ZM11 5V3C11 2.4 10.6 2 10 2C9.4 2 9 2.4 9 3V5C9 5.6 9.4 6 10 6C10.6 6 11 5.6 11 5ZM15 5V3C15 2.4 14.6 2 14 2C13.4 2 13 2.4 13 3V5C13 5.6 13.4 6 14 6C14.6 6 15 5.6 15 5ZM19 5V3C19 2.4 18.6 2 18 2C17.4 2 17 2.4 17 3V5C17 5.6 17.4 6 18 6C18.6 6 19 5.6 19 5Z"
															fill="currentColor"></path>
														<path
															d="M8.8 13.1C9.2 13.1 9.5 13 9.7 12.8C9.9 12.6 10.1 12.3 10.1 11.9C10.1 11.6 10 11.3 9.8 11.1C9.6 10.9 9.3 10.8 9 10.8C8.8 10.8 8.59999 10.8 8.39999 10.9C8.19999 11 8.1 11.1 8 11.2C7.9 11.3 7.8 11.4 7.7 11.6C7.6 11.8 7.5 11.9 7.5 12.1C7.5 12.2 7.4 12.2 7.3 12.3C7.2 12.4 7.09999 12.4 6.89999 12.4C6.69999 12.4 6.6 12.3 6.5 12.2C6.4 12.1 6.3 11.9 6.3 11.7C6.3 11.5 6.4 11.3 6.5 11.1C6.6 10.9 6.8 10.7 7 10.5C7.2 10.3 7.49999 10.1 7.89999 10C8.29999 9.90003 8.60001 9.80003 9.10001 9.80003C9.50001 9.80003 9.80001 9.90003 10.1 10C10.4 10.1 10.7 10.3 10.9 10.4C11.1 10.5 11.3 10.8 11.4 11.1C11.5 11.4 11.6 11.6 11.6 11.9C11.6 12.3 11.5 12.6 11.3 12.9C11.1 13.2 10.9 13.5 10.6 13.7C10.9 13.9 11.2 14.1 11.4 14.3C11.6 14.5 11.8 14.7 11.9 15C12 15.3 12.1 15.5 12.1 15.8C12.1 16.2 12 16.5 11.9 16.8C11.8 17.1 11.5 17.4 11.3 17.7C11.1 18 10.7 18.2 10.3 18.3C9.9 18.4 9.5 18.5 9 18.5C8.5 18.5 8.1 18.4 7.7 18.2C7.3 18 7 17.8 6.8 17.6C6.6 17.4 6.4 17.1 6.3 16.8C6.2 16.5 6.10001 16.3 6.10001 16.1C6.10001 15.9 6.2 15.7 6.3 15.6C6.4 15.5 6.6 15.4 6.8 15.4C6.9 15.4 7.00001 15.4 7.10001 15.5C7.20001 15.6 7.3 15.6 7.3 15.7C7.5 16.2 7.7 16.6 8 16.9C8.3 17.2 8.6 17.3 9 17.3C9.2 17.3 9.5 17.2 9.7 17.1C9.9 17 10.1 16.8 10.3 16.6C10.5 16.4 10.5 16.1 10.5 15.8C10.5 15.3 10.4 15 10.1 14.7C9.80001 14.4 9.50001 14.3 9.10001 14.3C9.00001 14.3 8.9 14.3 8.7 14.3C8.5 14.3 8.39999 14.3 8.39999 14.3C8.19999 14.3 7.99999 14.2 7.89999 14.1C7.79999 14 7.7 13.8 7.7 13.7C7.7 13.5 7.79999 13.4 7.89999 13.2C7.99999 13 8.2 13 8.5 13H8.8V13.1ZM15.3 17.5V12.2C14.3 13 13.6 13.3 13.3 13.3C13.1 13.3 13 13.2 12.9 13.1C12.8 13 12.7 12.8 12.7 12.6C12.7 12.4 12.8 12.3 12.9 12.2C13 12.1 13.2 12 13.6 11.8C14.1 11.6 14.5 11.3 14.7 11.1C14.9 10.9 15.2 10.6 15.5 10.3C15.8 10 15.9 9.80003 15.9 9.70003C15.9 9.60003 16.1 9.60004 16.3 9.60004C16.5 9.60004 16.7 9.70003 16.8 9.80003C16.9 9.90003 17 10.2 17 10.5V17.2C17 18 16.7 18.4 16.2 18.4C16 18.4 15.8 18.3 15.6 18.2C15.4 18.1 15.3 17.8 15.3 17.5Z"
															fill="currentColor"></path>
													</svg>
												</span>
												<!--end::Svg Icon-->
												<!--end::Icon-->
												<!--begin::Datepicker-->
												<input class="form-control form-control-solid ps-12 flatpickr-input"
													placeholder="Select a date" name="dateTo" id="dateTo" type="text">
												<!--onchange="handleChange();"-->
											</div>

											<div class="col-md-4 fv-row">
												<div class="txtnew" style="margin-right: 5px; float: left;">
													<s:submit id="submit" value="View"
														class="btn btn-primary  mt-4 submit_btn" />
												</div>
											</div>


											<div class="col-md-4 fv-row">
												<div class="txtnew" style="float: left;">
													<button type="button" id="download"  onclick="testMe();"
														class="btn btn-primary  mt-4 submit_btn">Download</button>
												</div>

											</div>

										</div>

									</form>

								</div>
							</div>
						</div>
					</div>
<div class="row my-5">
	<div class="col">
<div class="card">
	<div class="card-body">

		<div class="row g-9 mb-8 justify-content-end">

			<div class="col-lg-4 col-sm-12 col-md-6">
				<select name="currency" data-control="select2"
					data-placeholder="Actions" id="actions11"
					class="form-select form-select-solid actions"
					data-hide-search="true" onchange="myFunction();">
					<option value="">Actions</option>
					<option value="copy">Copy</option>
					<option value="csv">CSV</option>
					<option value="pdf">PDF</option>
				</select>
			</div>
			<div class="col-lg-4 col-sm-12 col-md-6">
				<div class="dropdown1">
					<button class="form-select form-select-solid actions dropbtn1">Customize Columns</button>
					<div class="dropdown-content1">
				<a class="toggle-vis" data-column="6">Net Amount</a>
				<a class="toggle-vis" data-column="6">Txn Type</a>
				<a class="toggle-vis" data-column="6">Capture Date</a>
				<a class="toggle-vis" data-column="7">Settlement Date</a>
				<a class="toggle-vis" data-column="8">Settlement Amount</a>
				<a class="toggle-vis" data-column="9">Payment Method</a>
				<a class="toggle-vis" data-column="10">Mop Type</a>
				<a class="toggle-vis" data-column="11">Currency</a>
				<a class="toggle-vis" data-column="12">Acquirer TDR/SC</a>
				<a class="toggle-vis" data-column="13">Acquirer GST</a>
				<a class="toggle-vis" data-column="14">PG TDR/SC</a>
				<a class="toggle-vis" data-column="15">PG GST</a>
				<a class="toggle-vis" data-column="16">ACQ Id</a>
				<a class="toggle-vis" data-column="17">RRN NO.</a>
				<a class="toggle-vis" data-column="18">Payment Region</a>
				
				</div>
				</div>
			</div>
		</div>


					<div class="scrollD">
						<table id="summaryReportDataTable" class="table table-striped table-row-bordered gy-5 gs-7" align="center" cellspacing="0" width="100%">
							<thead>
								<tr class="boxheadingsmall fw-bold fs-6 text-gray-800" style="font-size: 11px;">

									<th style="text-align: center;">S No.</th>
									<th style="text-align: center;">Pg Ref Num</th>
									<th style="text-align: center;">Order Id</th>
									<th style="text-align: center;">Acquirer</th>
									<th style="text-align: center;">Merchant</th>
									<th style="text-align: center;">Txn Amount</th>
									<th style="text-align: center;">Net Amount</th>
									<th style="text-align: center;">Txn Type</th>
									<th style="text-align: center;">Capture Date</th>
									<th style="text-align: center;">Settlement Date</th>
									<th style="text-align: center;">Settlement Amount</th>
									<th style="text-align: center;">Payment Method</th>
									<th style="text-align: center;">Mop Type</th>
									<th style="text-align: center;">Currency</th>
									<th style="text-align: center;">Acquirer TDR/SC</th>
									<th style="text-align: center;">Acquirer GST</th>
									<th style="text-align: center;">PG TDR/SC</th>
									<th style="text-align: center;">PG GST</th>
									<th style="text-align: center;">ACQ Id</th>
									<th style="text-align: center;">RRN NO.</th>
									<th style="text-align: center;">Payment Region</th>
									<th style="text-align: center;">Card Mask</th>
									<th style="text-align: center;">Post Settled Flag</th>
									<th style="text-align: center;">Refund</th>




								</tr>
							</thead>
							<tfoot>
								<tr class="boxheadingsmall">
									<th style='text-align: left;'></th>
									<th style='text-align: left;'></th>
									<th style='text-align: left;'></th>
									<th style='text-align: left;'></th>
									<th style='text-align: left;'></th>
									<th style='text-align: left;'></th>
									<th style='text-align: left;'></th>
									<th style='text-align: left;'></th>
									<th style='text-align: left;'></th>
									<th style='text-align: left;'></th>
									<th style='text-align: left;'></th>
									<th style='text-align: left;'></th>
									<th style='text-align: left;'></th>
									<th style='text-align: left;'></th>
									<th style='text-align: left;'></th>
									<th style='text-align: left;'></th>
									<th style='text-align: left;'></th>
									<th style='text-align: left;'></th>
									<th style='text-align: left;'></th>
									<th style='text-align: left;'></th>
									<th style='text-align: left;'></th>
									<th style='text-align: left;'></th>
									<th style='text-align: left;'></th>
									<th style='text-align: left;'></th>
								</tr>
							</tfoot>
						</table>
					</div>

				</div>
			</div>
				</div>
			</div>

				</div>
			</div>
		</div>


		<script>
			$('.collapseStart').slideToggle("fast");
			$('.newDiv').click(function () {
				$(this).parent().find('.collapseStart').slideToggle("fast");
			});

			function isNumberKeyAmount(evt) {
				const charCode = (event.which) ? event.which : event.keyCode;
				if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode != 46) {
					return false;
					return true;
				}
			}

			$('#amtConf').keypress(function (e) {
				if (this.value.length == 0 && e.which == 48) {
					return false;
				}
			});

			$('input#amtConf').blur(function () {
				var num = parseFloat($(this).val());
				var cleanNum = num.toFixed(2);
				$(this).val(cleanNum);
				if (num / cleanNum < 1) {
					$('#error2').text('Please enter only 2 decimal places, we have truncated extra points');
				}
			});

		</script>

		<s:form id="downloadSummaryReportAction" name="downloadSummaryReportAction"
			action="downloadSummaryReportAction">
			<s:hidden name="token" value="%{#session.customToken}"></s:hidden>
			<s:hidden name="currency" id="currencyFrm" value=""></s:hidden>
			<s:hidden name="dateFrom" id="dateFromFrm" value=""></s:hidden>
			<s:hidden name="dateTo" id="dateToFrm" value=""></s:hidden>
			<s:hidden name="merchantPayId" id="merchantPayIdFrm" value=""></s:hidden>
			<s:hidden name="paymentType" id="paymentTypeFrm" value=""></s:hidden>
			<s:hidden name="mopType" id="mopTypeFrm" value=""></s:hidden>
			<s:hidden name="transactionType" id="transactionTypeFrm" value=""></s:hidden>
			<s:hidden name="status" id="statusFrm" value=""></s:hidden>
			<s:hidden name="acquirer" id="acquirerFrm" value=""></s:hidden>
			<s:hidden name="paymentMethods" id="paymentMethodsFrm" value=""></s:hidden>
			<s:hidden name="paymentsRegion" id="paymentsRegionFrm" value=""></s:hidden>
			<s:hidden name="cardHolderType" id="cardHolderTypeFrm" value=""></s:hidden>
			<s:hidden name="pgRefNum" id="pgRefNumFrm" value=""></s:hidden>
			<s:hidden name="orderId" id="orderIdFrm" value=""></s:hidden>
		</s:form>

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
		<script>
			$("#dateTo").flatpickr({
				maxDate: new Date(),
				dateFormat: 'Y-m-d',
				defaultDate: "today"
			});
			$("#dateFrom").flatpickr({
				maxDate: new Date(),
				dateFormat: 'Y-m-d',
				defaultDate: "today"
			});		
		</script>



<script>

function handleChange() {
		debugger
	var transFrom = document.getElementById('dateFrom').value;
	var transTo =document.getElementById('dateTo').value;
	var dateFrom=new Date(Date.parse(transFrom));
	var dateTo=new Date(Date.parse(transTo));
		if (dateFrom == null || dateTo == null) {
			alert('Enter date value');
			return false;
		}
		else if (dateFrom > dateTo) {
			alert('From date must be before the to date');
			$('#dateFrom').focus();
			
			
			return false;
		}
		else if (dateTo - dateFrom > 31 * 86400000) {
			alert('No. of days can not be more than 31');
			$('#dateFrom').focus();
			return false;
		}
		else{
			populateDataTable1();
		}
 }

</script>

<script>
	"use strict";
	var KTCareersApply = function () {
		debugger
		var t, e, i;
		return {
			init: function () {
				i = document.querySelector("#setteled_report_form"),
					t = document.getElementById("submit"),
					e = FormValidation.formValidation(i, {
						fields: {
							pgrefnumber: {
											validators: {
												//notEmpty: {
												// 	message: "PG Ref No is required"
												// },
												stringLength: {
													max: 16,
													min: 16,
													message: 'Please Enter 16 Digit PG Ref No.',
												}
											}
										},
						
							emailid: {
											 validators: {
											 	//notEmpty: {
											// 		message: "Name is required"
											 //	},
												 stringLength: {
												 	max: 60,
												 	message: 'email Id should be less than 60 characters.',
													 btnDisable:true
												},
												callback: {
													callback: function (input) {
														if (input.value.length==0) {
															document.getElementsByClassName("invalid-feedback")[2].style.display='none';
														}else{
															document.getElementsByClassName("invalid-feedback")[2].style.display='block';

															if (!input.value.match(/^([A-Za-z0-9_\-\.\+])+\@([A-Za-z0-9_\-\+])+\.([A-Za-z]{2,4})$/)) {
															return {
																valid: false,
																message: 'Please enter a valid email.',
																btnDisable:true
															};
														} 
														else {
															return {
																valid: true,
																btnDisable:false
															}
														}

														}
														
													}
												}
											}
										},
										orderid: {
											 validators: {
											 	//notEmpty: {
											// 		message: "Name is required"
											 //	},
												 stringLength: {
												 	max: 30,
												 	message: 'Order Id should be less than 30 characters.',
													 btnDisable:true
												},
												callback: {
													callback: function (input) {
														if (input.value.match(/[!\@\^\_\&\/\\#,\|+()$~%.'":*?<>{}]/)) {
															return {
																valid: false,
																message: 'Special characters not allowed.',
																btnDisable:true
															};
														} else {
															return {
																valid: true,
																btnDisable:false
															}
														}
													}
												}
											}
										},
										phonenumber: {
											validators: {
												// notEmpty: {
												// 	message: "Phone Number is required"
												//},
												
												stringLength: {
													max: 10,
													min: 10,
													message: 'Please enter valid phone number.',
													btnDisable:true
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
					});
					t.addEventListener("click", (function (i) {
										 i.preventDefault(),
									
										e && e.validate().then((function (e) {
											if(e=='Invalid'){
												debugger

				

			}
			else{
				debugger
				handleChange();
			//	document.getElementById("btnRefundConf").disabled = false;
				
				
			}
												}
									))
									
									// i.preventDefault(),
									
									// 	e && e.validate().then((function (e) {
									// 		populateDataTable();
										
									// 				}

									//	))
								}
								))
			}
		}
	}();
	KTUtil.onDOMContentLoaded((function () {
		KTCareersApply.init()
	}
	));
</script>

<script>
	function inrFormat(temp) { // nStr is the input string
		// temp = 1000000.00
		if (temp == 0) {
			return 0;
		}
		//console.log(" Hiiii " + temp);
		if (temp == undefined || temp == null || temp == '') {
			return 0;
		}
		numArr = temp.toString().split('.');
		nStr = numArr[0];
		nStr += '';
		x = nStr.split('.');
		x1 = x[0];
		x2 = x.length > 1 ? '.' + x[1] : '';
		var rgx = /(\d+)(\d{3})/;
		var z = 0;
		var len = String(x1).length;
		var num = parseInt((len / 2) - 1);

		while (rgx.test(x1)) {
			if (z > 0) {
				x1 = x1.replace(rgx, '$1' + ',' + '$2');
			} else {
				x1 = x1.replace(rgx, '$1' + ',' + '$2');
				rgx = /(\d+)(\d{2})/;
			}
			z++;
			num--;
			if (num == 0) {
				break;
			}
		}
		let result = x1 + x2;
		//  console.log('' + num[1])
		if (numArr[1] != undefined) {
			result += '.' + numArr[1];
		}
		// console.log(result);
		return result;
	}


	function testMe() {
				
				var acquirer = document.getElementById("selectBox1").title;
				document.getElementById("currencyFrm").value = document.getElementById("currency").value;
				document.getElementById("dateFromFrm").value = changeDateFormat(document.getElementById("dateFrom").value);
				document.getElementById("dateToFrm").value = changeDateFormat(document.getElementById("dateTo").value);
				document.getElementById("merchantPayIdFrm").value = document.getElementById("merchants").value;
				document.getElementById("paymentTypeFrm").value = document.getElementById("paymentMethods").value;
				document.getElementById("mopTypeFrm").value = document.getElementById("mopType").value;
				document.getElementById("transactionTypeFrm").value = document.getElementById("transactionType").value;
				//document.getElementById("statusFrm").value = document.getElementById("pgRefNum").value;
				document.getElementById("acquirerFrm").value = acquirer;
				document.getElementById("paymentsRegionFrm").value = document.getElementById("paymentsRegion").value;
				document.getElementById("cardHolderTypeFrm").value = document.getElementById("cardHolderType").value;
				document.getElementById("pgRefNumFrm").value = document.getElementById("pgRefNum").value;
				document.getElementById("orderIdFrm").value = document.getElementById("orderId").value;
				document.getElementById("downloadSummaryReportAction").submit();

			}

			function changeDateFormat(input){
				console.log(input);
			    var date = new Date(Date.parse(input));
			    let day = date.getDate();
			    let month = date.getMonth()+1;
                let year = date.getFullYear();
                if (day < 10) {
                    day = '0'+day;
                }
                if (month < 10) {
                    month = '0'+month;
                }
                let format1 = day+'-'+month+'-'+year;
                console.log(format1);
                return format1;
			}
</script>

<script type="text/javascript">
	$('a.toggle-vis').on('click', function (e) {
		debugger
		e.preventDefault();
		table = $('#summaryReportDataTable').DataTable();
		// Get the column API object
		var column1 = table.column($(this).attr('data-column'));
		// Toggle the visibility
		column1.visible(!column1.visible());
		if($(this)[0].classList[1]=='activecustom'){
			$(this).removeClass('activecustom');
		}
		else{
			$(this).addClass('activecustom');
		}
	});
	
	
	function myFunction() {
	var x = document.getElementById("actions11").value;
	if(x=='csv'){
		document.querySelector('.buttons-csv').click();
	}
	if(x=='copy'){
		document.querySelector('.buttons-copy').click();
	}
	if(x=='pdf'){
		document.querySelector('.buttons-pdf').click();
	}

	// document.querySelector('.buttons-excel').click();
	// document.querySelector('.buttons-print').click();


}

	</script>


	</body>

	</html>