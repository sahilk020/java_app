<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="/struts-tags" prefix="s"%>
<html dir="ltr" lang="en-US">

<head>
<title>Reseller Search Payment</title>

<!-- <link href="../css/default.css" rel="stylesheet" type="text/css" /> -->

<link
	href="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.css"
	rel="stylesheet" type="text/css" />
<link href="../assets/plugins/custom/datatables/datatables.bundle.css"
	rel="stylesheet" type="text/css" />
<link href="../assets/plugins/global/plugins.bundle.css"
	rel="stylesheet" type="text/css" />
<link rel="stylesheet" href="../css/transactionResult.css">

<link href="../assets/css/style.bundle.css" rel="stylesheet"
	type="text/css" />
<link href="../assets/css/custom/custom-style.css" rel="stylesheet"
	type="text/css" />
<script type="text/javascript" src="../js/bootstrap-clockpicker.js"></script>

<script src="../assets/js/scripts.bundle.js"></script>

<script src="../js/commanValidate.js"></script>

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

<script type="text/javascript">

				function downloadReport() {

					var merchantPayId = document.getElementById("merchant").value;
					var transactionType = document.getElementById("transactionType").value;
					var paymentType = document.getElementById("selectBox3") ? document
						.getElementById("selectBox3").title : '';
					var status = document.getElementById("selectBox4").title;
					var currency = document.getElementById("currency").value;
					var dateFrom = convert(document.getElementById("dateFrom").value);
					var dateTo = convert(document.getElementById("dateTo").value);
					var acquirer = document.getElementById("selectBox1") ? document.getElementById
						("selectBox1").title : '';
					var ipAddress = document.getElementById("ipAddress").value;
					var rrn = document.getElementById("rrn").value;

					var transactionId = document.getElementById("pgRefNum").value;
					var orderId = document.getElementById("orderId").value;
					var customerEmail = document.getElementById("customerEmail").value;
					var customerPhone = document.getElementById("custPhone").value;
					var mopType = document.getElementById("selectBox2") ? document.getElementById
						("selectBox2").title : '';


					/* Added by Deep Singh code start here*/

					var channelName = document.getElementById("selectBox5") ? document.getElementById
						("selectBox5").title : '';
					var minAmount = document.getElementById("minAmount").value;
					var maxAmount = document.getElementById("maxAmount").value;
					var columnName = document.getElementById("columnName").value;
					var logicalCondition = document.getElementById("logicalCondition").value;
					var searchText = document.getElementById("searchText").value;
					var newDespositor = $("#newDespositor")[0].checked;


					var columnName1 = document.getElementById("columnName1").value;
					var logicalCondition1 = document.getElementById("logicalCondition1").value;
					var searchText1 = document.getElementById("searchText1").value;



					var columnName2 = document.getElementById("columnName2").value;
					var logicalCondition2 = document.getElementById("logicalCondition2").value;
					var searchText2 = document.getElementById("searchText2").value;

					if (channelName == '') {
						channelName = 'ALL';
					}


					/* Added by Deep Singh code end here*/

					if (merchantPayId == '') {
						merchantPayId = 'ALL'
					}
					if (transactionType == '') {
						transactionType = 'ALL'
					}
					if (paymentType == '') {
						paymentType = 'ALL'
					}
					if (status == '') {
						status = 'ALL'
					}
					if (currency == '') {
						currency = 'ALL'
					}
					if (mopType == '') {
						mopType = 'ALL'
					}
					if (acquirer == '') {
						acquirer = 'ALL'
					}


					// 					dateFrom += " 00:00";
					// 					dateTo += " 23:59";

					var startTime = document.getElementById("startTime").value;
					var endTime = document.getElementById("endTime").value;

					startTime = startTime != "00:00" ? moment(startTime, ["h:mmA"]).format("HH:mm") : "00:00";
					endTime = endTime != "00:00" ? moment(endTime, ["h:mmA"]).format("HH:mm") : "23:59";


					document.getElementById("startTimeH").value = startTime;
					document.getElementById("endTimeH").value = endTime;

					document.getElementById("merchantPayIdH").value = merchantPayId;
					document.getElementById("transactionTypeH").value = transactionType;
					document.getElementById("paymentTypeH").value = paymentType;
					document.getElementById("statusH").value = status;
					document.getElementById("currencyH").value = currency;
					document.getElementById("dateFromH").value = dateFrom;
					document.getElementById("dateToH").value = dateTo;
					document.getElementById("acquirerH").value = acquirer;
					document.getElementById("ipAddressH").value = ipAddress
					document.getElementById("transactionIdH").value = transactionId;
					document.getElementById("orderIdH").value = orderId;
					document.getElementById("customerEmailH").value = customerEmail;
					document.getElementById("customerPhoneH").value = customerPhone;
					document.getElementById("mopTypeH").value = mopType;
					document.getElementById("rrnH").value = rrn;
					document.getElementById("newDespositorH").value = newDespositor;

					document.getElementById("channelNameH").value = channelName;
					document.getElementById("minAmountH").value = minAmount;
					document.getElementById("maxAmountH").value = maxAmount;
					document.getElementById("columnNameH").value = columnName;
					document.getElementById("logicalConditionH").value = logicalCondition;
					document.getElementById("searchTextH").value = searchText;

					document.getElementById("columnNameHH").value = columnName1;
					document.getElementById("logicalConditionHH").value = logicalCondition1;
					document.getElementById("searchTextHH").value = searchText1;

					document.getElementById("columnNameHHH").value = columnName2;
					document.getElementById("logicalConditionHHH").value = logicalCondition2;
					document.getElementById("searchTextHHH").value = searchText2;

					var needToShowFields = $("#needToShowAcqFieldsInReport").val();
					if ($("#userTypeName").val() != "SUBADMIN") {
						needToShowFields = true;
					}
					document.getElementById("needToShowAcquirerDetails").value = needToShowFields;

					document.getElementById("downloadPaymentReport").submit();
				}


				$(document).ready(function () {
					var columnName = document.getElementById("columnName").value;

					if (columnName == "") {

						document.getElementById("logicalCondition").innerHTML = '<option value="">Select logical Operator</option>';
						document.getElementById("logicalCondition").disabled = true;
						document.getElementById("searchText").disabled = true;
						return false;
					} else {
						document.getElementById("logicalCondition").disabled = false;
						document.getElementById("searchText").disabled = false;
					}
					// Initialize select2
					$(".adminMerchants").select2();
				});


	function getSMAMerchant(smaId) {


		$.ajax({

			type : "GET",
			url : "getSMATxnMerchant",
			timeout : 0,
			data : {

				"smaId" : smaId,

			},
			success : function(data, status) {

				var s = '<option value="">ALL</option>';
				for (var i = 0; i < data.merchantarreList.length; i++) {
					s += '<option value="' + data.merchantarreList[i] + '">'
							+ data.merchantarrename[i] + '</option>';
				}
				$("#merchant").html(s);

				var ma = '<option value="">ALL</option>';
				for (var i = 0; i < data.listMA.length; i++) {
					ma += '<option value="' + data.listMA[i].payId + '">'
							+ data.listMA[i].businessName + '</option>';
				}
				$("#maId").html(ma);

				var agent = '<option value="">ALL</option>';
				for (var i = 0; i < data.listAgent.length; i++) {
					agent += '<option value="' + data.listAgent[i].payId + '">'
							+ data.listAgent[i].businessName + '</option>';
				}
				$("#agentId").html(agent);

			}
		});
	}

	function getMAMerchant(maId) {


		$.ajax({

			type : "GET",
			url : "getMATxnMerchant",
			timeout : 0,
			data : {

				"maId" : maId,

			},
			success : function(data, status) {

				var s = '<option value="">ALL</option>';
				// alert(s)
				for (var i = 0; i < data.merchantarreList.length; i++) {
					s += '<option value="' + data.merchantarreList[i] + '">'
							+ data.merchantarrename[i] + '</option>';
					// console.log(s)

				}
				//document.getElementById("tomerchant").style.display = "block";

				$("#merchant").html(s);


				var agent = '<option value="">ALL</option>';
				for (var i = 0; i < data.listAgent.length; i++) {
					agent += '<option value="' + data.listAgent[i].payId + '">'
							+ data.listAgent[i].businessName + '</option>';
				}
				$("#agentId").html(agent);

			}
		});
	}

	function getAgentMerchant(agentId) {


		$.ajax({

			type : "GET",
			url : "getAgentTxnMerchant",
			timeout : 0,
			data : {

				"agentId" : agentId,

			},
			success : function(data, status) {

				var s = '<option value="">ALL</option>';
				// alert(s)
				for (var i = 0; i < data.merchantarreList.length; i++) {
					s += '<option value="' + data.merchantarreList[i] + '">'
							+ data.merchantarrename[i] + '</option>';
					// console.log(s)

				}
				//document.getElementById("tomerchant").style.display = "block";

				$("#merchant").html(s);

			}
		});
	}

				function totalAmountofAlltxns() {
					debugger
					var token = document.getElementsByName("token")[0].value;
					var merchantEmailId = document.getElementById("merchant").value;
					var transactionType = document.getElementById("transactionType").value;
					var paymentType = document.getElementById("selectBox3") ? document
						.getElementById("selectBox3").title : '';
					var status = document.getElementById("selectBox4").title;
					var currency = document.getElementById("currency").value;
					var mopType = document.getElementById("selectBox2") ? document.getElementById
						("selectBox2").title : '';
					var acquirer = document.getElementById("selectBox1") ? document.getElementById
						("selectBox1").title : '';
					/*added by vijaylakshmi*/
					var ipAddress = document.getElementById("ipAddress").value;
					var rrn = document.getElementById("rrn").value;
					var totalAmount = document.getElementById("totalAmount").value;

					var startTime = document.getElementById("startTime").value;
					var endTime = document.getElementById("endTime").value;

					startTime = startTime != "00:00" ? moment(startTime, ["h:mmA"]).format("HH:mm") : "00:00";
					endTime = endTime != "00:00" ? moment(endTime, ["h:mmA"]).format("HH:mm") : "23:59";

					if (merchantEmailId == '') {
						merchantEmailId = 'ALL'
					}
					if (transactionType == '') {
						transactionType = 'ALL'
					}
					if (paymentType == '') {
						paymentType = 'ALL'
					}
					if (status == '') {
						status = 'ALL'
					}
					if (currency == '') {
						currency = 'ALL'
					}
					if (mopType == '') {
						mopType = 'ALL'
					}

					if (acquirer == '') {
						acquirer = 'ALL'
					}
					/* Added by Deep Singh code start here*/

					var channelName = document.getElementById("selectBox5") ? document.getElementById
						("selectBox5").title : '';
					var minAmount = document.getElementById("minAmount").value;
					var maxAmount = document.getElementById("maxAmount").value;
					var columnName = document.getElementById("columnName").value;
					var logicalCondition = document.getElementById("logicalCondition").value;
					var searchText = document.getElementById("searchText").value;

					if (channelName == '') {
						channelName = 'ALL';
					}

					/* Added by Deep Singh code end here*/

					var dateFrom = document.getElementById("dateFrom").value;
					var dateTo = document.getElementById("dateTo").value;
					// dateFrom += " 00:00";
					// 			dateTo += " 23:59";
					dateFrom = convert(dateFrom);
					dateTo = convert(dateTo);

					var obj = {
						transactionId: document.getElementById("pgRefNum").value,
						orderId: document.getElementById("orderId").value,
						customerEmail: document.getElementById("customerEmail").value,
						customerPhone: document.getElementById("custPhone").value,
						mopType: mopType,
						acquirer: acquirer,
						merchantEmailId: merchantEmailId,
						transactionType: transactionType,
						paymentType: paymentType,
						status: status,
						currency: currency,
						dateFrom: dateFrom,
						dateTo: dateTo,
						ipAddress: ipAddress,
						rrn: rrn,
						totalAmount: totalAmount,
						startTime: startTime,
						endTime: endTime,
						token: token,
						"struts.token.name": "token",
						channelName: channelName,
						minAmount: minAmount,
						maxAmount: maxAmount,
						columnName: columnName,
						logicalCondition: logicalCondition,
						searchText: searchText,
					}


					$.ajax({
						url: "totalAmountOfAllTxnsActionAdmin",
						timeout: 0,
						type: "POST",
						data: obj,
						success: function (response) {
							var responseObj = response.totalFinalResult;

							$("#totalTxnsAmount").empty();
							$("#totalSettledTxnsAmount").empty();


							var table = document.getElementById("totalTxnsAmount");
							var tr = document.createElement('tr');
							tr.setAttribute('class', 'fw-bold fs-6 text-gray-800');
							var tr1 = document.createElement('tr');
							var tr2 = document.createElement('tr');
							var tr3 = document.createElement('tr');
							var tr4 = document.createElement('tr');
							var tr5 = document.createElement('tr');
							var tr5a = document.createElement('tr');
							var tr6 = document.createElement('tr');
							var tr7 = document.createElement('tr');
							var tr8 = document.createElement('tr');

							var th1 = document.createElement('th');
							var th2 = document.createElement('th');
							var th3 = document.createElement('th');
							var th4 = document.createElement('th');
							th1.appendChild(document.createTextNode("Transaction Type"));
							th2.appendChild(document.createTextNode("Status"));
							th3.appendChild(document.createTextNode("No Of Transactions"));
							th4.appendChild(document.createTextNode("Total Amount of All Transactions"));
							tr.appendChild(th1);
							tr.appendChild(th2);
							tr.appendChild(th3);
							tr.appendChild(th4);

							table.appendChild(tr);

							var td1a = document.createElement('td');
							var td2a = document.createElement('td');
							var td3a = document.createElement('td');
							var td4a = document.createElement('td');


							td1a.appendChild(document.createTextNode('SALE'));
							td2a.appendChild(document.createTextNode('Captured'));
							td3a.appendChild(document.createTextNode(responseObj.totalSaleSuccCount ? responseObj.totalSaleSuccCount : '0'));
							td4a.appendChild(document.createTextNode(inrFormat(responseObj.totalSaleSuccAmount ? responseObj.totalSaleSuccAmount : '0.00')));
							tr1.appendChild(td1a);
							tr1.appendChild(td2a);
							tr1.appendChild(td3a);
							tr1.appendChild(td4a);
							table.appendChild(tr1);


							var td1b = document.createElement('td');
							var td2b = document.createElement('td');
							var td3b = document.createElement('td');
							var td4b = document.createElement('td');

							td1b.appendChild(document.createTextNode(''));
							td2b.appendChild(document.createTextNode('Pending'));
							td3b.appendChild(document.createTextNode(responseObj.totalSalePendingCount ? responseObj.totalSalePendingCount : '0'));
							td4b.appendChild(document.createTextNode(inrFormat(responseObj.totalSalePendingAmount ? responseObj.totalSalePendingAmount : '0.00')));
							tr2.appendChild(td1b);
							tr2.appendChild(td2b);
							tr2.appendChild(td3b);
							tr2.appendChild(td4b);

							table.appendChild(tr2);

							var td1c = document.createElement('td');
							var td2c = document.createElement('td');
							var td3c = document.createElement('td');
							var td4c = document.createElement('td');

							td1c.appendChild(document.createTextNode(''));
							td2c.appendChild(document.createTextNode('Failed'));
							td3c.appendChild(document.createTextNode(responseObj.totalSalefailCount ? responseObj.totalSalefailCount : '0'));
							td4c.appendChild(document.createTextNode(inrFormat(responseObj.totalSalefailAmount ? responseObj.totalSalefailAmount : '0.00')));
							tr3.appendChild(td1c);
							tr3.appendChild(td2c);
							tr3.appendChild(td3c);
							tr3.appendChild(td4c);

							table.appendChild(tr3);

							var td1e = document.createElement('td');
							var td2e = document.createElement('td');
							var td3e = document.createElement('td');
							var td4e = document.createElement('td');

							td1e.appendChild(document.createTextNode(''));
							td2e.appendChild(document.createTextNode('Cancelled'));
							td3e.appendChild(document.createTextNode(responseObj.totalSaleCancelledCount ? responseObj.totalSaleCancelledCount : '0'));
							td4e.appendChild(document.createTextNode(inrFormat(responseObj.totalSaleCancelledAmount ? responseObj.totalSaleCancelledAmount : '0.00')));
							tr5.appendChild(td1e);
							tr5.appendChild(td2e);
							tr5.appendChild(td3e);
							tr5.appendChild(td4e);

							table.appendChild(tr5);

							var td1ez = document.createElement('td');
							var td2ez = document.createElement('td');
							var td3ez = document.createElement('td');
							var td4ez = document.createElement('td');

							td1ez.appendChild(document.createTextNode(''));
							td2ez.appendChild(document.createTextNode('Invalid'));
							td3ez.appendChild(document.createTextNode(responseObj.totalSaleInvalidCount ? responseObj.totalSaleInvalidCount : '0'));
							td4ez.appendChild(document.createTextNode(inrFormat(responseObj.totalSaleInvalidAmount ? responseObj.totalSaleInvalidAmount : '0.00')));

							tr5a.appendChild(td1ez);
							tr5a.appendChild(td2ez);
							tr5a.appendChild(td3ez);
							tr5a.appendChild(td4ez);

							table.appendChild(tr5a);


							var td1f = document.createElement('td');
							var td2f = document.createElement('td');
							var td3f = document.createElement('td');
							var td4f = document.createElement('td');

							td1f.appendChild(document.createTextNode('REFUND'));
							td2f.appendChild(document.createTextNode('Captured'));
							td3f.appendChild(document.createTextNode(responseObj.totalRefundSuccCount ? responseObj.totalRefundSuccCount : '0'));
							td4f.appendChild(document.createTextNode(inrFormat(responseObj.totalRefundSuccAmount ? responseObj.totalRefundSuccAmount : '0.00')));
							tr6.appendChild(td1f);
							tr6.appendChild(td2f);
							tr6.appendChild(td3f);
							tr6.appendChild(td4f);
							table.appendChild(tr6);

							var td1g = document.createElement('td');
							var td2g = document.createElement('td');
							var td3g = document.createElement('td');
							var td4g = document.createElement('td');

							td1g.appendChild(document.createTextNode(''));
							td2g.appendChild(document.createTextNode('Remaining Other Status'));
							td3g.appendChild(document.createTextNode(responseObj.totalRefundFailCount ? responseObj.totalRefundFailCount : '0'));
							td4g.appendChild(document.createTextNode(inrFormat(responseObj.totalRefundFailAmount ? responseObj.totalRefundFailAmount : '0.00')));
							tr7.appendChild(td1g);
							tr7.appendChild(td2g);
							tr7.appendChild(td3g);
							tr7.appendChild(td4g);

							table.appendChild(tr7);


							var td1h = document.createElement('td');
							var td2h = document.createElement('td');
							var td3h = document.createElement('td');
							var td4h = document.createElement('td');

							td1h.appendChild(document.createTextNode('Total'));
							td2h.appendChild(document.createTextNode('ALL'));
							td3h.appendChild(document.createTextNode(responseObj.totaltxns ? responseObj.totaltxns : '0'));
							td4h.appendChild(document.createTextNode(inrFormat(responseObj.totalTxnAmount ? responseObj.totalTxnAmount : '0.00')));

							tr8.appendChild(td1h);
							tr8.appendChild(td2h);
							tr8.appendChild(td3h);
							tr8.appendChild(td4h);

							table.appendChild(tr8);


							// Settled Related Code

							var table = document.getElementById("totalSettledTxnsAmount");
							var tr = document.createElement('tr');
							var tr1 = document.createElement('tr');
							var tr2 = document.createElement('tr');
							var tr3 = document.createElement('tr');
							var tr4 = document.createElement('tr');
							var tr5 = document.createElement('tr');
							var tr6 = document.createElement('tr');
							var tr7 = document.createElement('tr');
							var tr8 = document.createElement('tr');
							var tr9 = document.createElement('tr');
							var tr10 = document.createElement('tr');
							var tr12 = document.createElement('tr');
							var tr13 = document.createElement('tr');

							var th1 = document.createElement('th');
							var th2 = document.createElement('th');
							var th3 = document.createElement('th');
							var th4 = document.createElement('th');
							th1.appendChild(document.createTextNode("Transaction Type"));
							th2.appendChild(document.createTextNode("Payment Type"));
							th3.appendChild(document.createTextNode("No Of Transactions"));
							th4.appendChild(document.createTextNode("Total Amount of All Transactions"));
							tr.appendChild(th1);
							tr.appendChild(th2);
							tr.appendChild(th3);
							tr.appendChild(th4);

							table.appendChild(tr);

							var td1a = document.createElement('td');
							var td2a = document.createElement('td');
							var td3a = document.createElement('td');
							var td4a = document.createElement('td');


							td1a.appendChild(document.createTextNode('SALE'));
							td2a.appendChild(document.createTextNode('Credit Card'));
							td3a.appendChild(document.createTextNode(responseObj.totalSaleCCcount ? responseObj.totalSaleCCcount : '0'));
							td4a.appendChild(document.createTextNode(inrFormat(responseObj.totalSaleCCAmount ? responseObj.totalSaleCCAmount : '0.00')));
							tr1.appendChild(td1a);
							tr1.appendChild(td2a);
							tr1.appendChild(td3a);
							tr1.appendChild(td4a);
							table.appendChild(tr1);

							var td1b = document.createElement('td');
							var td2b = document.createElement('td');
							var td3b = document.createElement('td');
							var td4b = document.createElement('td');

							td1b.appendChild(document.createTextNode(''));
							td2b.appendChild(document.createTextNode('Debit Card'));
							td3b.appendChild(document.createTextNode(responseObj.totalSaleDCcount ? responseObj.totalSaleDCcount : '0'));
							td4b.appendChild(document.createTextNode(inrFormat(responseObj.totalSaleDCAmount ? responseObj.totalSaleDCAmount : '0.00')));
							tr2.appendChild(td1b);
							tr2.appendChild(td2b);
							tr2.appendChild(td3b);
							tr2.appendChild(td4b);

							table.appendChild(tr2);

							var td1c = document.createElement('td');
							var td2c = document.createElement('td');
							var td3c = document.createElement('td');
							var td4c = document.createElement('td');

							td1c.appendChild(document.createTextNode(''));
							td2c.appendChild(document.createTextNode('Net Banking'));
							td3c.appendChild(document.createTextNode(responseObj.totalSaleNBcount ? responseObj.totalSaleNBcount : '0'));
							td4c.appendChild(document.createTextNode(inrFormat(responseObj.totalSaleNBAmount ? responseObj.totalSaleNBAmount : '0.00')));
							tr3.appendChild(td1c);
							tr3.appendChild(td2c);
							tr3.appendChild(td3c);
							tr3.appendChild(td4c);

							table.appendChild(tr3);

							var td1d = document.createElement('td');
							var td2d = document.createElement('td');
							var td3d = document.createElement('td');
							var td4d = document.createElement('td');

							td1d.appendChild(document.createTextNode(''));
							td2d.appendChild(document.createTextNode('Wallet'));
							td3d.appendChild(document.createTextNode(responseObj.totalSalewalletcount ? responseObj.totalSalewalletcount : '0'));
							td4d.appendChild(document.createTextNode(inrFormat(responseObj.totalSalewalletAmount ? responseObj.totalSalewalletAmount : '0.00')));
							tr4.appendChild(td1d);
							tr4.appendChild(td2d);
							tr4.appendChild(td3d);
							tr4.appendChild(td4d);

							table.appendChild(tr4);

							var td1e = document.createElement('td');
							var td2e = document.createElement('td');
							var td3e = document.createElement('td');
							var td4e = document.createElement('td');

							td1e.appendChild(document.createTextNode(''));
							td2e.appendChild(document.createTextNode('UPI'));
							td3e.appendChild(document.createTextNode(responseObj.totalSaleUPIcount ? responseObj.totalSaleUPIcount : '0'));
							td4e.appendChild(document.createTextNode(inrFormat(responseObj.totalSaleUPIAmount ? responseObj.totalSaleUPIAmount : '0.00')));
							tr5.appendChild(td1e);
							tr5.appendChild(td2e);
							tr5.appendChild(td3e);
							tr5.appendChild(td4e);

							table.appendChild(tr5);

							var td1x = document.createElement('td');
							var td2x = document.createElement('td');
							var td3x = document.createElement('td');
							var td4x = document.createElement('td');

							td1x.appendChild(document.createTextNode('Total SALE'));
							td2x.appendChild(document.createTextNode('ALL Payments'));
							td3x.appendChild(document.createTextNode(responseObj.totalSaleCount ? responseObj.totalSaleCount : '0'));
							td4x.appendChild(document.createTextNode(inrFormat(responseObj.totalSaleAmount ? responseObj.totalSaleAmount : '0.00')));
							tr12.appendChild(td1x);
							tr12.appendChild(td2x);
							tr12.appendChild(td3x);
							tr12.appendChild(td4x);

							table.appendChild(tr12);

							var td1f = document.createElement('td');
							var td2f = document.createElement('td');
							var td3f = document.createElement('td');
							var td4f = document.createElement('td');


							td1f.appendChild(document.createTextNode('REFUND'));
							td2f.appendChild(document.createTextNode('Credit Card'));
							td3f.appendChild(document.createTextNode(responseObj.totalRfCCcount ? responseObj.totalRfCCcount : '0'));
							td4f.appendChild(document.createTextNode(inrFormat(responseObj.totalRfCCAmount ? responseObj.totalRfCCAmount : '0.00')));
							tr6.appendChild(td1f);
							tr6.appendChild(td2f);
							tr6.appendChild(td3f);
							tr6.appendChild(td4f);
							table.appendChild(tr6);

							var td1g = document.createElement('td');
							var td2g = document.createElement('td');
							var td3g = document.createElement('td');
							var td4g = document.createElement('td');

							td1g.appendChild(document.createTextNode(''));
							td2g.appendChild(document.createTextNode('Debit Card'));
							td3g.appendChild(document.createTextNode(responseObj.totalRfDCcount ? responseObj.totalRfDCcount : '0'));
							td4g.appendChild(document.createTextNode(inrFormat(responseObj.totalRfDCAmount ? responseObj.totalRfDCAmount : '0.00')));
							tr7.appendChild(td1g);
							tr7.appendChild(td2g);
							tr7.appendChild(td3g);
							tr7.appendChild(td4g);

							table.appendChild(tr7);

							var td1h = document.createElement('td');
							var td2h = document.createElement('td');
							var td3h = document.createElement('td');
							var td4h = document.createElement('td');

							td1h.appendChild(document.createTextNode(''));
							td2h.appendChild(document.createTextNode('Net Banking'));
							td3h.appendChild(document.createTextNode(responseObj.totalRfNBcount ? responseObj.totalRfNBcount : '0'));
							td4h.appendChild(document.createTextNode(inrFormat(responseObj.totalRfNBAmount ? responseObj.totalRfNBAmount : '0.00')));
							tr8.appendChild(td1h);
							tr8.appendChild(td2h);
							tr8.appendChild(td3h);
							tr8.appendChild(td4h);

							table.appendChild(tr8);

							var td1i = document.createElement('td');
							var td2i = document.createElement('td');
							var td3i = document.createElement('td');
							var td4i = document.createElement('td');

							td1i.appendChild(document.createTextNode(''));
							td2i.appendChild(document.createTextNode('Wallet'));
							td3i.appendChild(document.createTextNode(responseObj.totalRfwalletcount ? responseObj.totalRfwalletcount : '0'));
							td4i.appendChild(document.createTextNode(inrFormat(responseObj.totalRfwalletAmount ? responseObj.totalRfwalletAmount : '0.00')));
							tr9.appendChild(td1i);
							tr9.appendChild(td2i);
							tr9.appendChild(td3i);
							tr9.appendChild(td4i);

							table.appendChild(tr9);

							var td1j = document.createElement('td');
							var td2j = document.createElement('td');
							var td3j = document.createElement('td');
							var td4j = document.createElement('td');

							td1j.appendChild(document.createTextNode(''));
							td2j.appendChild(document.createTextNode('UPI'));
							td3j.appendChild(document.createTextNode(responseObj.totalRfUPIcount ? responseObj.totalRfUPIcount : '0'));
							td4j.appendChild(document.createTextNode(inrFormat(responseObj.totalRfUPIAmount ? responseObj.totalRfUPIAmount : '0.00')));
							tr10.appendChild(td1j);
							tr10.appendChild(td2j);
							tr10.appendChild(td3j);
							tr10.appendChild(td4j);

							table.appendChild(tr10);

							var td1y = document.createElement('td');
							var td2y = document.createElement('td');
							var td3y = document.createElement('td');
							var td4y = document.createElement('td');

							td1y.appendChild(document.createTextNode('Total Refund'));
							td2y.appendChild(document.createTextNode('ALL Payments'));
							td3y.appendChild(document.createTextNode(responseObj.totalRfCount ? responseObj.totalRfCount : '0'));
							td4y.appendChild(document.createTextNode(inrFormat(responseObj.totalRfAmount ? responseObj.totalRfAmount : '0.00')));
							tr13.appendChild(td1y);
							tr13.appendChild(td2y);
							tr13.appendChild(td3y);
							tr13.appendChild(td4y);

							table.appendChild(tr13);


							var tr11 = document.createElement('tr');
							var td1k = document.createElement('td');
							var td2k = document.createElement('td');
							var td3k = document.createElement('td');
							var td4k = document.createElement('td');

							td1k.appendChild(document.createTextNode('Total'));
							td2k.appendChild(document.createTextNode('ALL'));
							td3k.appendChild(document.createTextNode(responseObj.totalSettleCount ? responseObj.totalSettleCount : '0'));
							td4k.appendChild(document.createTextNode(inrFormat(responseObj.totalSettleAmount ? responseObj.totalSettleAmount : '0.00')));

							tr11.appendChild(td1k);
							tr11.appendChild(td2k);
							tr11.appendChild(td3k);
							tr11.appendChild(td4k);

							table.appendChild(tr11);


						},
						error: function (xhr, textStatus, errorThrown) {

						}
					});
				}

				//window.onload = totalAmountofAlltxns;
				function convert(str) {
					var date = new Date(str), mnth = ("0" + (date.getMonth() + 1))
						.slice(-2), day = ("0" + date.getDate()).slice(-2);
					//return [date.getFullYear(), mnth, day].join("-");
					return [day, mnth, date.getFullYear()].join("-");
				}
			</script>

<script type="text/javascript">
				$(document).ready(function () {

					$(function () {
						$("#dateFrom").flatpickr({
							maxDate: new Date(),
							dateFormat: "Y-m-d",
							defaultDate: "today",
							defaultDate: "today",
						});
						$("#dateTo").flatpickr({
							maxDate: new Date(),
							dateFormat: "Y-m-d",
							defaultDate: "today",
							maxDate: new Date()
						});
						$("#kt_datatable_vertical_scroll").DataTable({
							scrollY: true,
							scrollX: true

						});
					});

					$(function () {
						// var today = new Date();
						// $('#dateTo').val($.datepicker.formatDate('dd-mm-yy', today));
						// $('#dateFrom').val($.datepicker.formatDate('dd-mm-yy', today));
						var userType = $("#userTypeName").val();
						var needToShowAcqFields = $("#needToShowAcqFieldsInReport").val();
						if (needToShowAcqFields == 'true' || userType != 'SUBADMIN') {
							renderTable();
						} else {
							renderTableWithoutAcqFields();
						}
						setTimeout(totalAmountofAlltxns, 1000);
					});

					// $("#submit").click(function(env) {
					// 	reloadTable();
					// 	setTimeout(totalAmountofAlltxns,1000);
					// });

					$(function () {
						//var datepick = $.datepicker;
						var needToShowAcqFields = $("#needToShowAcqFieldsInReport").val();
						var userType = $("#userTypeName").val();
						var tableName = needToShowAcqFields == 'true' || userType != 'SUBADMIN' ?
							"txnResultDataTable" : "txnResultDataTableWithoutAcqFields";
						var table = $('#' + tableName).DataTable();
						$('#' + tableName).on('click', 'td', function () {
							var rowIndex = table.cell(this).index().row;
							var colIndex = table.cell(this).index().column;
							var rowData = table.row(rowIndex).data();
							if (colIndex == 0) {
								popup(rowData.transactionIdString, rowData.oId, rowData.orderId, rowData.txnType, rowData.pgRefNum);
							}
						});
					});
				});

				function renderTable() {
					var monthVal = parseInt(new Date().getMonth()) + 1;
					var merchantEmailId = document.getElementById("merchant").value;
					var table = new $.fn.dataTable.Api('#txnResultDataTable');

					dateFrom = document.getElementById("dateFrom").value;
					dateTo = document.getElementById("dateTo").value;
					var transFrom = convert(dateFrom);
					var transTo = convert(dateTo);
					var transFrom1 = new Date(Date.parse(document.getElementById("dateFrom").value));
					var transTo1 = new Date(Date.parse(document.getElementById("dateTo").value));


					var colName = document.getElementById("columnName").value;

					if (colName != "") {

						var logicalCond = document.getElementById("logicalCondition").value;
						var searchTxt = document.getElementById("searchText").value;

						if (logicalCond == "") {
							alert("please select logical condition");
							return false;
						}

						if (searchTxt == "") {
							alert("please enter search text");
							return false;
						}

					}

					if (transFrom1 == null || transTo1 == null) {
						alert('Enter date value');
						return false;
					}

					if (transFrom1 > transTo1) {
						$('#loader-wrapper').hide();
						alert('From date must be before the to date');
						$('#dateFrom').focus();
						return false;
					}
					if (transTo1 - transFrom1 > 180 * 86400000) {
						$('#loader-wrapper').hide();
						alert('No. of days can not be more than 180');
						$('#dateFrom').focus();
						return false;
					}
					var token = document.getElementsByName("token")[0].value;
					$('#loader-wrapper').hide();

					var buttonCommon = {
						exportOptions: {
							format: {
								body: function (data, column, row, node) {
									// Strip $ from salary column to make it numeric
									return column === 0 ? "'" + data : (column === 1 ? "'" + data : data);
								}
							}
						}
					};

					$('#txnResultDataTable').dataTable(
						{

							"footerCallback": function (row, data, start, end, display) {
								var api = this.api(), data;

								// Remove the formatting to get integer data for summation
								var intVal = function (i) {
									return typeof i === 'string' ? i.replace(/[\,]/g, '') * 1 : typeof i === 'number' ? i : 0;
								};

								// Total over all pages
								total = api.column(10).data().reduce(
									function (a, b) {
										return intVal(a) + intVal(b);
									}, 0);

								// Total over this page
								pageTotal = api.column(10, {
									page: 'current'
								}).data().reduce(function (a, b) {
									return intVal(a) + intVal(b);
								}, 0);

								// Update footer
								$(api.column(10).footer()).html(
									'' + inrFormat(pageTotal.toFixed(2) + ' ' + ' '));


								// Total over all pages
								total = api.column(11).data().reduce(
									function (a, b) {
										return intVal(a) + intVal(b);
									}, 0);

								// Total over this page
								pageTotal = api.column(11, {
									page: 'current'
								}).data().reduce(function (a, b) {
									return intVal(a) + intVal(b);
								}, 0);

								// Update footer
								$(api.column(11).footer()).html(
									'' + inrFormat(pageTotal.toFixed(2) + ' ' + ' '));



							},
							"columnDefs": [{
								className: "dt-body-right",
								"targets": [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15]
							}],




							dom: 'Brtipl',
							buttons: [
								{
									extend: 'print',
									exportOptions: {
										columns: ':visible'
									}
								},
								{
									extend: 'pdfHtml5',
									orientation: 'landscape',
									pageSize: 'legal',
									//footer : true,
									title: 'Search Transactions',
									exportOptions: {
										columns: [':visible']
									},
									customize: function (doc) {
										doc.defaultStyle.alignment = 'center';
										doc.styles.tableHeader.alignment = 'center';
										doc.defaultStyle.fontSize = 8;
									}
								},
								{
									extend: 'copy',
									exportOptions: {
										columns: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20]
									}
								},
								{
									extend: 'csv',
									exportOptions: {
										columns: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19]
									}
								},
								{
									extend: 'pdf',
									exportOptions: {
										columns: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22]
									}
								},
								'colvis', 'excel', 'print',
							],
							scrollY: true,
							scrollX: true,
							searchDelay: 500,
							processing: false,
							serverSide: true,
							order: [[5, 'desc']],
							stateSave: true,

							"ajax": {

								"url": "resellerTransactionSearchAction",
								"type": "POST",
								"timeout": 0,
								"data": function (d) {
									return generatePostData(d);
								}
							},
							"fnDrawCallback": function () {
								$("#submit").removeAttr("disabled");
								$('#loader-wrapper').hide();
							},
							"searching": false,
							"ordering": false,
							"processing": true,
							"serverSide": true,
							"paginationType": "full_numbers",
							"lengthMenu": [[10, 25, 50, 100], [10, 25, 50, 100]],
							"order": [[2, "desc"]],

							"columnDefs": [
								{
									"type": "html-num-fmt",
									"targets": 4,
									"orderable": true,
									"targets": [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17]
								}
							],

							"columns": [{
								"data": "transactionIdString",
								"className": "txnId my_class1 text-class",
								"width": "2%"
							}, {
								"data": "pgRefNum",
								"className": "payId text-class"

							}, {
								"data": "merchants",
								"className": "text-class"
							}, {
								"data": "dateFrom",
								"className": "text-class"
							}, {
								"data": "orderId",
								"className": "orderId text-class"
							}, {
								"data": "refundOrderId",
								"className": "orderId text-class"
							}, {
								"data": "mopType",
								"className": "mopType text-class"
							}, {
								"data": "paymentMethods",
								"render": function (data, type, full) {
									return full['paymentMethods'] + ' ' + '-'
										+ ' ' + full['mopType'];
								},
								"className": "text-class"
							}, {
								"data": "txnType",
								"className": "txnType text-class",
							}, {
								"data": "status",
								"className": "status text-class"
							}, {
								"data": "amount",
								"className": "text-class",
								"render": function (data) {
									return inrFormat(data);
								}
							}
								, {
								"data": "totalAmount",
								// 								"className": "text-class",
								// 								"visible": false,
								"render": function (data) {
									return inrFormat(data);
								}
							}, {
								"data": "payId",
								"visible": false

							},
							{
								"data": "currency",
								"className": "text-class"
							},
							{
								"data": "customerEmail",
								"className": "text-class"
							}, {
								"data": "customerPhone",
								"className": "text-class"
							},
							{ data: "acquirerType" },
							{ data: "ipaddress" },
							{ data: "cardMask" },
							{ data: "rrn" },
							{
								data: "cardHolderType",
								className: "text-class"
							},
							{
								data: "transactRef",
								className: "text-class"
							},
							{
								data: "pspName",
								className: "text-class"
							},

							{ data: "customerPhone", visible: false },
							{ data: null, visible: false, render: function (row) { return "\u0027" + row.transactionIdString; } },
							{ data: "customerPhone", visible: false },
							{ data: "customerPhone", visible: false },
							{ data: "oId", visible: false }]




						});

					$(document).ready(function () {

						var table = $('#txnResultDataTable').DataTable();
						$('#txnResultDataTable').on('click', '.center', function () {
							var columnIndex = table.cell(this).index().column;
							var rowIndex = table.cell(this).index().row;
							var rowNodes = table.row(rowIndex).node();
							var rowData = table.row(rowIndex).data();
							var txnType1 = rowData.txnType;
							var status1 = rowData.status;

							if ((txnType1 == "SALE" && status1 == "Captured") || (txnType1 == "AUTHORISE" && status1 == "Approved") || (txnType1 == "SALE" && status1 == "Settled")) {
								var payId1 = rowData.pgRefNum;
								var orderId1 = rowData.orderId;
								var txnId1 = Number(rowData.transactionIdString);
								document.getElementById('payIdc').value = payId1;
								document.getElementById('orderIdc').value = orderId1;
								document.getElementById('txnIdc').value = txnId1;
								document.chargeback.submit();
							}
						});
					});



				}

				function renderTableWithoutAcqFields() {
					var monthVal = parseInt(new Date().getMonth()) + 1;
					var merchantEmailId = document.getElementById("merchant").value;
					var table = new $.fn.dataTable.Api('#txnResultDataTableWithoutAcqFields');

					dateFrom = document.getElementById("dateFrom").value;
					dateTo = document.getElementById("dateTo").value;
					var transFrom = convert(dateFrom);
					var transTo = convert(dateTo);
					var transFrom1 = new Date(Date.parse(document.getElementById("dateFrom").value));
					var transTo1 = new Date(Date.parse(document.getElementById("dateTo").value));
					if (transFrom1 == null || transTo1 == null) {
						alert('Enter date value');
						return false;
					}

					if (transFrom1 > transTo1) {
						$('#loader-wrapper').hide();
						alert('From date must be before the to date');
						$('#dateFrom').focus();
						return false;
					}
					if (transTo1 - transFrom1 > 180 * 86400000) {
						$('#loader-wrapper').hide();
						alert('No. of days can not be more than 180');
						$('#dateFrom').focus();
						return false;
					}
					var token = document.getElementsByName("token")[0].value;
					$('#loader-wrapper').hide();

					var buttonCommon = {
						exportOptions: {
							format: {
								body: function (data, column, row, node) {
									// Strip $ from salary column to make it numeric
									return column === 0 ? "'" + data : (column === 1 ? "'" + data : data);
								}
							}
						}
					};

					$('#txnResultDataTableWithoutAcqFields').dataTable(
						{

							"footerCallback": function (row, data, start, end, display) {
								var api = this.api(), data;

								// Remove the formatting to get integer data for summation
								var intVal = function (i) {
									return typeof i === 'string' ? i.replace(/[\,]/g, '') * 1 : typeof i === 'number' ? i : 0;
								};

								// Total over all pages
								total = api.column(10).data().reduce(
									function (a, b) {
										return intVal(a) + intVal(b);
									}, 0);

								// Total over this page
								pageTotal = api.column(10, {
									page: 'current'
								}).data().reduce(function (a, b) {
									return intVal(a) + intVal(b);
								}, 0);

								// Update footer
								$(api.column(10).footer()).html(
									'' + inrFormat(pageTotal.toFixed(2) + ' ' + ' '));


								// Total over all pages
								total = api.column(11).data().reduce(
									function (a, b) {
										return intVal(a) + intVal(b);
									}, 0);

								// Total over this page
								pageTotal = api.column(11, {
									page: 'current'
								}).data().reduce(function (a, b) {
									return intVal(a) + intVal(b);
								}, 0);

								// Update footer
								$(api.column(11).footer()).html(
									'' + inrFormat(pageTotal.toFixed(2) + ' ' + ' '));



							},
							"columnDefs": [{
								className: "dt-body-right",
								"targets": [1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12,
									13, 14, 15, 16, 17, 18, 19]
							}],




							dom: 'Brtipl',
							buttons: [
								{
									extend: 'print',
									exportOptions: {
										columns: ':visible'
									}
								},
								{
									extend: 'pdfHtml5',
									orientation: 'landscape',
									pageSize: 'legal',
									//footer : true,
									title: 'Search Transactions',
									exportOptions: {
										columns: [':visible']
									},
									customize: function (doc) {
										doc.defaultStyle.alignment = 'center';
										doc.styles.tableHeader.alignment = 'center';
										doc.defaultStyle.fontSize = 8;
									}
								},
								{
									extend: 'copy',
									exportOptions: {
										columns: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19]
									}
								},
								{
									extend: 'csv',
									exportOptions: {
										columns: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19]
									}
								},
								{
									extend: 'pdf',
									exportOptions: {
										columns: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19]
									}
								},
								'colvis', 'excel', 'print',
							],
							scrollY: true,
							scrollX: true,
							searchDelay: 500,
							processing: false,
							serverSide: true,
							order: [[5, 'desc']],
							stateSave: true,

							"ajax": {

								"url": "resellerTransactionSearchAction",
								"type": "POST",
								"timeout": 0,
								"data": function (d) {
									return generatePostData(d);
								}
							},
							"fnDrawCallback": function () {
								$("#submit").removeAttr("disabled");
								$('#loader-wrapper').hide();
							},
							"searching": false,
							"ordering": false,
							"processing": true,
							"serverSide": true,
							"paginationType": "full_numbers",
							"lengthMenu": [[10, 25, 50, 100], [10, 25, 50, 100]],
							"order": [[2, "desc"]],

							"columnDefs": [
								{
									"type": "html-num-fmt",
									"targets": 4,
									"orderable": true,
									"targets": [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
										11, 12, 13, 14, 15, 16, 17, 18]
								}
							],

							"columns": [{
								"data": "transactionIdString",
								"className": "txnId my_class1 text-class",
								"width": "2%"
							}, {
								"data": "pgRefNum",
								"className": "payId text-class"

							}, {
								"data": "merchants",
								"className": "text-class"
							}, {
								"data": "dateFrom",
								"className": "text-class"
							}, {
								"data": "orderId",
								"className": "orderId text-class"
							}, {
								"data": "refundOrderId",
								"className": "orderId text-class"
							}, {
								"data": "txnType",
								"className": "txnType text-class",
							}, {
								"data": "status",
								"className": "status text-class"
							}, {
								"data": "amount",
								"className": "text-class",
								"render": function (data) {
									return inrFormat(data);
								}
							}
								, {
								"data": "totalAmount",
								//                 								"className": "text-class",
								//                 								"visible": false,
								"render": function (data) {
									return inrFormat(data);
								}
							}, {
								"data": "payId",
								"visible": false

							},
							{
								"data": "currency",
								"className": "text-class"
							},{
								"data": "customerEmail",
								"className": "text-class"
							}, {
								"data": "customerPhone",
								"className": "text-class"
							},
							{ data: "ipaddress" },
							{ data: "cardMask" },
							{ data: "rrn" },
							{
								data: "cardHolderType",
								className: "text-class"
							},
							{
								data: "transactRef",
								className: "text-class"
							},
							{
								data: "pspName",
								className: "text-class"
							},
							{ data: "customerPhone", visible: false },
							{ data: null, visible: false, render: function (row) { return "\u0027" + row.transactionIdString; } },
							{ data: "customerPhone", visible: false },
							{ data: "customerPhone", visible: false },
							{ data: "oId", visible: false }]
						});

					$(document).ready(function () {

						var table = $('#txnResultDataTableWithoutAcqFields').DataTable();
						$('#txnResultDataTableWithoutAcqFields').on('click', '.center', function () {
							var columnIndex = table.cell(this).index().column;
							var rowIndex = table.cell(this).index().row;
							var rowNodes = table.row(rowIndex).node();
							var rowData = table.row(rowIndex).data();
							var txnType1 = rowData.txnType;
							var status1 = rowData.status;

							if ((txnType1 == "SALE" && status1 == "Captured") || (txnType1 == "AUTHORISE" && status1 == "Approved") || (txnType1 == "SALE" && status1 == "Settled")) {
								var payId1 = rowData.pgRefNum;
								var orderId1 = rowData.orderId;
								var txnId1 = Number(rowData.transactionIdString);
								document.getElementById('payIdc').value = payId1;
								document.getElementById('orderIdc').value = orderId1;
								document.getElementById('txnIdc').value = txnId1;
								document.chargeback.submit();
							}
						});
					});



				}

				function reloadTable() {
					dateFrom = document.getElementById("dateFrom").value;
					dateTo = document.getElementById("dateTo").value;
					var transFrom = convert(dateFrom);
					var transTo = convert(dateTo);
					var transFrom1 = new Date(Date.parse(document.getElementById("dateFrom").value));
					var transTo1 = new Date(Date.parse(document.getElementById("dateTo").value));

					var colName = document.getElementById("columnName").value;

					if (colName != "") {

						var logicalCond = document.getElementById("logicalCondition").value;
						var searchTxt = document.getElementById("searchText").value;

						if (logicalCond == "") {
							alert("please select logical condition");
							return false;
						}

						if (searchTxt == "") {
							alert("please enter search text");
							return false;
						}

					}

					if (transFrom1 == null || transTo1 == null) {
						alert('Enter date value');
						return false;
					}

					if (transFrom1 > transTo1) {
						alert('From date must be before the to date');
						$('#loader-wrapper').hide();
						$('#dateFrom').focus();
						return false;
					}
					if (transTo1 - transFrom1 > 180 * 86400000) {
						alert('No. of days can not be more than 180');
						$('#loader-wrapper').hide();
						$('#dateFrom').focus();
						return false;
					}
					else {
						$("#submit").attr("disabled", true);
						var needToShowAcqFields = $("#needToShowAcqFieldsInReport").val();
						var userType = $("#userTypeName").val();
						var tableObj = needToShowAcqFields == 'true' || userType != 'SUBADMIN' ? $('#txnResultDataTable') : $('#txnResultDataTableWithoutAcqFields');
						var table = tableObj.DataTable();
						table.ajax.reload();
					}
				}

				function generatePostData(d) {
					var token = document.getElementsByName("token")[0].value;
					var merchantEmailId = document.getElementById("merchant").value;
					var transactionType = document.getElementById("transactionType").value;
					var paymentType = document.getElementById("selectBox3") ? document
						.getElementById("selectBox3").title : '';
					var smaId = document.getElementById("smaId").value;
					var maId = document.getElementById("maId").value;
					var agentId = document.getElementById("agentId").value;
					var status = document.getElementById("selectBox4").title;
					var currency = document.getElementById("currency").value;
					var mopType = document.getElementById("selectBox2") ? document.getElementById
						("selectBox2").title : '';
					var acquirer = document.getElementById("selectBox1") ? document.getElementById
						("selectBox1").title : '';
					var ipAddress = document.getElementById("ipAddress").value;
					var rrn = document.getElementById("rrn").value;
					var startTime = document.getElementById("startTime").value;
					var endTime = document.getElementById("endTime").value;
					var totalAmount = document.getElementById("totalAmount").value;
					startTime = startTime != "00:00" ? moment(startTime, ["h:mmA"]).format("HH:mm") : "00:00";
					endTime = endTime != "00:00" ? moment(endTime, ["h:mmA"]).format("HH:mm") : "23:59";

					/* Added by Deep Singh code start here*/

					var channelName = document.getElementById("selectBox5") ? document.getElementById
						("selectBox5").title : '';
					var minAmount = document.getElementById("minAmount").value;
					var maxAmount = document.getElementById("maxAmount").value;
					var columnName = document.getElementById("columnName").value;
					var logicalCondition = document.getElementById("logicalCondition").value;
					var searchText = document.getElementById("searchText").value;

					var columnName1 = document.getElementById("columnName1").value;
					var logicalCondition1 = document.getElementById("logicalCondition1").value;
					var searchText1 = document.getElementById("searchText1").value;



					var columnName2 = document.getElementById("columnName2").value;
					var logicalCondition2 = document.getElementById("logicalCondition2").value;
					var searchText2 = document.getElementById("searchText2").value;

					if (channelName == '') {
						channelName = 'ALL';
					}



					/* Added by Deep Singh code end here*/

					if (merchantEmailId == '') {
						merchantEmailId = 'ALL'
					}
					if (transactionType == '') {
						transactionType = 'ALL'
					}
					if (paymentType == '') {
						paymentType = 'ALL'
					}
					if (status == '') {
						status = 'ALL'
					}
					if (currency == '') {
						currency = 'ALL'
					}
					if (mopType == '') {
						mopType = 'ALL'
					}
					if (acquirer == '') {
						acquirer = 'ALL'
					}
					var dateFrom = document.getElementById("dateFrom").value;
					var dateTo = document.getElementById("dateTo").value;
					// dateFrom += " 00:00";
					// 			dateTo += " 23:59";
					dateFrom = convert(dateFrom);
					dateTo = convert(dateTo);
					var newDespositor = $("#newDespositor")[0].checked;
					var obj = {
						transactionId: document.getElementById("pgRefNum").value,
						orderId: document.getElementById("orderId").value,
						customerEmail: document.getElementById("customerEmail").value,
						customerPhone: document.getElementById("custPhone").value,
						merchantEmailId: merchantEmailId,
						transactionType: transactionType,
						paymentType: paymentType,
						status: status,
						newDespositor: newDespositor,
						mopType: mopType,
						acquirer: acquirer,
						currency: currency,
						dateFrom: dateFrom,
						dateTo: dateTo,
						startTime: startTime,
						endTime: endTime,
						draw: d.draw,
						length: d.length,
						start: d.start,
						ipAddress: ipAddress,
						totalAmount: totalAmount,
						rrn: rrn,
						token: token,
						"struts.token.name": "token",
						channelName: channelName,
						minAmount: minAmount,
						maxAmount: maxAmount,
						columnName: columnName,
						logicalCondition: logicalCondition,
						searchText: searchText,
						columnName1: columnName1,
						logicalCondition1: logicalCondition1,
						searchText1: searchText1,
						columnName2: columnName2,
						logicalCondition2: logicalCondition2,
						searchText2: searchText2,
						smaId: smaId,
						maId: maId,
						agentId: agentId
					};

					return obj;
				}



				function popup(txnId, oId, orderId, txnType, pgRefNum) {


					var token = document.getElementsByName("token")[0].value;

					var myData = {
						token: token,
						"struts.token.name": "token",
						"transactionId": txnId,
						"oId": oId,
						"orderId": orderId,
						"txnType": txnType,
						"pgRefNum": pgRefNum
					}
					$.ajax({
						url: "customerAddressActionAdmin",
						timeout: 0,
						type: "POST",
						data: myData,
						success: function (response) {
							var responseObj = response.aaData;
							var transObj = response.trailData[0];
							var txt = document.createElement("textarea");

							/* Start Billing details  */
							$('#sec1 td').eq(0).text(responseObj.custName ? responseObj.custName : 'Not Available');
							$('#sec1 td').eq(1).text(responseObj.custPhone ? responseObj.custPhone : 'Not Available');

							$('#sec2 td').eq(0).text(responseObj.custCity ? responseObj.custCity : 'Not Available');
							$('#sec2 td').eq(1).text(responseObj.custState ? responseObj.custState : 'Not Available');

							$('#sec7 td').eq(0).text(responseObj.custCountry ? responseObj.custCountry : 'Not Available');
							$('#sec7 td').eq(1).text(responseObj.custZip ? responseObj.custZip : 'Not Available');

							$('#address1 td').text(responseObj.custStreetAddress1 ? responseObj.custStreetAddress1 : 'Not Available');
							$('#address2 td').text(responseObj.custStreetAddress2 ? responseObj.custStreetAddress2 : 'Not Available');
							/* End Billing details  */
							/* Start Payment details  */

							$('#sec3chn td').eq(0).text(responseObj.cardHolderName ? responseObj.cardHolderName : 'Not Available');

							$('#sec3 td').eq(0).text(responseObj.cardMask ? responseObj.cardMask : 'Not Available');
							$('#sec3 td').eq(1).text(responseObj.issuer ? responseObj.issuer : 'Not Available');

							var needToShowAcqFields = $("#needToShowAcqFieldsInReport").val();
							var userType = $("#userTypeName").val();

							if (needToShowAcqFields == 'true' || userType != 'SUBADMIN') {
								$('#sec4 td').eq(0).text(responseObj.acquirerType ? responseObj.acquirerType : 'Not Available');
								$('#sec4 td').eq(1).text(responseObj.mopType ? responseObj.mopType : 'Not Available');
							}

							$('#sec5 td').eq(0).text(responseObj.pgTdr ? responseObj.pgTdr : 'Not Available');
							$('#sec5 td').eq(1).text(responseObj.pgGst ? responseObj.pgGst : 'Not Available');

							$('#sec6 td').eq(0).text(responseObj.acquirerTdr ? responseObj.acquirerTdr : 'Not Available');
							$('#sec6 td').eq(1).text(responseObj.acquirerGst ? responseObj.acquirerGst : 'Not Available');

							$('#udf6 td').eq(0).text(responseObj.udf6 ? responseObj.udf6 : 'Not Available');

							$('#udf6 td').eq(0).text(responseObj.udf6 ? responseObj.udf6 : 'Not Available');

							$('#address5 td').text(responseObj.pgTxnMsg ? responseObj.pgTxnMsg : 'Not Available');
							/* End Payment details  */
							/*Added by CS on 27 Dec for Shipping Details */
							$('#sec8 td').eq(0).text(responseObj.custShipName ? responseObj.custShipName : 'Not Available');
							$('#sec8 td').eq(1).text(responseObj.custShipPhone ? responseObj.custShipPhone : 'Not Available');

							$('#sec9 td').eq(0).text(responseObj.custShipCity ? responseObj.custShipCity : 'Not Available');
							$('#sec9 td').eq(1).text(responseObj.custShipState ? responseObj.custShipState : 'Not Available');

							$('#sec10 td').eq(0).text(responseObj.custShipCountry ? responseObj.custShipCountry : 'Not Available');
							$('#sec10 td').eq(1).text(responseObj.custShipZip ? responseObj.custShipZip : 'Not Available');


							$('#address3 td').text(responseObj.custShipStreetAddress1 ? responseObj.custShipStreetAddress1 : 'Not Available');
							$('#address4 td').text(responseObj.custShipStreetAddress2 ? responseObj.custShipStreetAddress2 : 'Not Available');
							/*End Shipping details */
							/*Start Transaction details */


							$("#transactionDetails").empty();
							var table = document.getElementById("transactionDetails");
							var tr = document.createElement('tr');
							var th1 = document.createElement('th');
							var th2 = document.createElement('th');
							var th3 = document.createElement('th');
							var th4 = document.createElement('th');
							th1.appendChild(document.createTextNode("Order ID"));
							th2.appendChild(document.createTextNode("Transaction Type"));
							th3.appendChild(document.createTextNode("Date"));
							th4.appendChild(document.createTextNode("Status"));
							tr.appendChild(th1);
							tr.appendChild(th2);
							tr.appendChild(th3);
							tr.appendChild(th4);

							table.appendChild(tr);

							var totalRefundamount = 0;
							var totalSaleamount = 0;
							var internalCustip;

							var curAmount;
							var curStatus;
							var curOrderId;
							var curPgRefNum;
							var curArn;
							var curRrn;

							var curTxnType;
							for (var i = 0; i < response.trailData.length; i++) {

								var tr2 = document.createElement('tr');

								var td1 = document.createElement('td');
								var td2 = document.createElement('td');
								var td3 = document.createElement('td');
								var td4 = document.createElement('td');


								if (response.trailData[i].txnType == "REFUND") {
									txt.innerHTML = (response.trailData[i].refundOrderId ? response.trailData[i].refundOrderId : 'Not Available');
									td1.appendChild(document.createTextNode(txt.value));
								}
								if (response.trailData[i].txnType == "SALE") {
									txt.innerHTML = (response.trailData[i].orderId ? response.trailData[i].orderId : 'Not Available');
									td1.appendChild(document.createTextNode(txt.value));
								}

								td2.appendChild(document.createTextNode(response.trailData[i].txnType ? response.trailData[i].txnType : 'Not Available'));
								td3.appendChild(document.createTextNode(response.trailData[i].createDate ? response.trailData[i].createDate : 'Not Available'));
								td4.appendChild(document.createTextNode(response.trailData[i].status ? response.trailData[i].status : 'Not Available'));

								tr2.appendChild(td1);
								tr2.appendChild(td2);
								tr2.appendChild(td3);
								tr2.appendChild(td4);

								table.appendChild(tr2);
								if (response.trailData[i].txnType == "REFUND" && response.trailData[i].status == "Captured") {
									totalRefundamount += (response.trailData[i].amount ? parseInt(response.trailData[i].amount) : 0);
								}

								if (response.trailData[i].txnType == "SALE" && response.trailData[i].status == "Captured") {
									totalSaleamount += (response.trailData[i].amount ? parseInt(response.trailData[i].amount) : 0);
								}

								if (response.trailData[i].transactionId == txnId) {
									curAmount = (response.trailData[i].amount ? response.trailData[i].amount : 0);
									curStatus = (response.trailData[i].currentStatus ? response.trailData[i].currentStatus : 0);
									if (response.trailData[i].txnType == "SALE") {
										curOrderId = (response.trailData[i].orderId ? response.trailData[i].orderId : 0);
									} else {
										curOrderId = (response.trailData[i].refundOrderId ? response.trailData[i].refundOrderId : 0);
									}

									curPgRefNum = (response.trailData[i].pgRefNum ? response.trailData[i].pgRefNum : 0);
									curArn = (response.trailData[i].arn ? response.trailData[i].arn : 0);
									curRrn = (response.trailData[i].rrn ? response.trailData[i].rrn : 0);
									curTxnType = (response.trailData[i].txnType ? response.trailData[i].txnType : 'Not Available');
								}

								internalCustip = (response.trailData[i].internalCustIP ? response.trailData[i].internalCustIP : 0);
							}

							$('#sec14 td').eq(0).text(transObj.acqId ? transObj.acqId : 'Not Available');
							$('#sec14 td').eq(1).text(inrFormat(totalRefundamount ? totalRefundamount + ".00" : 'Not Available'));

							$('#sec16 td').eq(0).text(internalCustip ? internalCustip : 'Not Available');
							$('#sec16 td').eq(1).text(inrFormat(totalSaleamount ? totalSaleamount + ".00" : 'Not Available'));

							if (curTxnType == "SALE") {
								$('#sec16 td').eq(1).css("display", "none");
								$('#sec16h th').eq(1).css("display", "none");
							}
							if (curTxnType == "REFUND") {
								$('#sec16 td').eq(1).css("display", "block");
								$('#sec16h th').eq(1).css("display", "block");
							}


							$('#sec11 td').eq(0).text(inrFormat(curAmount ? curAmount : 'Not Available'));
							$('#sec11 td').eq(1).text(curStatus ? curStatus : 'Not Available');


							txt.innerHTML = (curOrderId ? curOrderId : 'Not Available');

							$('#sec12 td').eq(0).text(txt.value);
							$('#sec12 td').eq(1).text(curPgRefNum ? curPgRefNum : 'Not Available');

							$('#sec13 td').eq(0).text(curArn ? curArn : 'Not Available');
							$('#sec13 td').eq(1).text(curRrn ? curRrn : 'Not Available');




							/*End Transaction details */


							// $('#address6 td').text(responseObj.custShipStreetAddress1 ? responseObj.custShipStreetAddress2 : 'Not Available');

							// $('#auth td').text(responseObj.internalTxnAuthentication ? responseObj.internalTxnAuthentication : 'Not Available');

							$('#popup').show();
						},
						error: function (xhr, textStatus, errorThrown) {

						}
					});



				};
			</script>

<script>

				function validPgRefNum() {

					var pgRefValue = document.getElementById("pgRefNum").value;
					var regex = /^(?!0{16})[0-9\b]{16}$/;
					if (pgRefValue.trim() != "") {
						if (!regex.test(pgRefValue)) {
							document.getElementById("validValue").style.display = "block";
							document.getElementById("submit").disabled = true;
							document.getElementById("download").disabled = true;
						}
						else {
							if (document.getElementById("validEamilValue").style.display != "block" && document.getElementById("validPhoneValue").style.display != "block" && document.getElementById("validOrderIdValue").style.display != "block") {
								document.getElementById("submit").disabled = false;
								document.getElementById("download").disabled = false;
							}
							document.getElementById("validValue").style.display = "none";
						}
					}
					else {
						if (document.getElementById("validEamilValue").style.display != "block" && document.getElementById("validPhoneValue").style.display != "block" && document.getElementById("validOrderIdValue").style.display != "block") {
							document.getElementById("submit").disabled = false;
							document.getElementById("download").disabled = false;
						}
						document.getElementById("validValue").style.display = "none";
					}
				}

				function validateCustomerEmail(emailField) {

					var reg = /^([A-Za-z0-9_\-\.\+])+\@([A-Za-z0-9_\-\+])+\.([A-Za-z]{2,4})$/;
					if (emailField.value !== "") {
						if (reg.test(emailField.value) == false) {
							document.getElementById("validEamilValue").style.display = "block";
							document.getElementById("submit").disabled = true;
							document.getElementById("download").disabled = true;
						} else {
							if (document.getElementById("validValue").style.display != "block" && document.getElementById("validPhoneValue").style.display != "block" && document.getElementById("validOrderIdValue").style.display != "block") {
								document.getElementById("submit").disabled = false;
								document.getElementById("download").disabled = false;
							}
							document.getElementById("validEamilValue").style.display = "none";
						}
					} else {
						if (document.getElementById("validValue").style.display != "block" && document.getElementById("validPhoneValue").style.display != "block" && document.getElementById("validOrderIdValue").style.display != "block") {
							document.getElementById("submit").disabled = false;
							document.getElementById("download").disabled = false;
						}
						document.getElementById("validEamilValue").style.display = "none";
					}

				}

				function validateCustomerPhone(phoneField) {
					var phreg = /^([0]|\+91)?[- ]?[56789]\d{9}$/;
					// mobileRegex = /^[789]\d{9}/ --> this regex we are using the sign up page
					if (phoneField.value !== "") {

						if (phreg.test(phoneField.value) == false) {
							document.getElementById("validPhoneValue").style.display = "block";
							document.getElementById("submit").disabled = true;
							document.getElementById("download").disabled = true;
						} else {
							if (document.getElementById("validEamilValue").style.display != "block" && document.getElementById("validValue").style.display != "block" && document.getElementById("validOrderIdValue").style.display != "block") {
								document.getElementById("submit").disabled = false;
								document.getElementById("download").disabled = false;
							}
							document.getElementById("validPhoneValue").style.display = "none";

						}
					} else {
						if (document.getElementById("validEamilValue").style.display != "block" && document.getElementById("validValue").style.display != "block" && document.getElementById("validOrderIdValue").style.display != "block") {
							document.getElementById("submit").disabled = false;
							document.getElementById("download").disabled = false;
						}
						document.getElementById("validPhoneValue").style.display = "none";
					}

				}

				function removeSpaces(fieldVal) {
					setTimeout(function () {
						var nospacepgRefVal = fieldVal.value.replace(/ /g, "");
						fieldVal.value = nospacepgRefVal;
					}, 400);
				}

				function validateOrderIdvalue(orderId) {
					setTimeout(function () {
						var orderIdreg = /^[0-9a-zA-Z\b\_-\s\+.]+$/;
						if (orderId.value !== "") {
							if (orderIdreg.test(orderId.value) == false) {
								document.getElementById("validOrderIdValue").style.display = "block";
								document.getElementById("submit").disabled = true;
								document.getElementById("download").disabled = true;
							} else {
								if (document.getElementById("validEamilValue").style.display != "block" && document.getElementById("validValue").style.display != "block" && document.getElementById("validPhoneValue").style.display != "block") {
									document.getElementById("submit").disabled = false;
									document.getElementById("download").disabled = false;
								}
								document.getElementById("validOrderIdValue").style.display = "none";

							}
						} else {
							if (document.getElementById("validEamilValue").style.display != "block" && document.getElementById("validValue").style.display != "block" && document.getElementById("validPhoneValue").style.display != "block") {
								document.getElementById("submit").disabled = false;
								document.getElementById("download").disabled = false;
							}
							document.getElementById("validOrderIdValue").style.display = "none";
						}
					}, 400);
				}


			</script>



<style type="text/css">
.switch {
	position: relative;
	display: inline-block;
	width: 60px;
	height: 34px;
}

.switch input {
	opacity: 0;
	width: 0;
	height: 0;
}

.slider {
	position: absolute;
	cursor: pointer;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	background-color: #ccc;
	-webkit-transition: .4s;
	transition: .4s;
}

.slider:before {
	position: absolute;
	content: "";
	height: 26px;
	width: 26px;
	left: 4px;
	bottom: 4px;
	background-color: white;
	-webkit-transition: .4s;
	transition: .4s;
}

input:checked+.slider {
	background-color: #64B5F6;
}

input:focus+.slider {
	box-shadow: 0 0 1px #64B5F6;
}

input:checked+.slider:before {
	-webkit-transform: translateX(26px);
	-ms-transform: translateX(26px);
	transform: translateX(26px);
}

/* Rounded sliders */
.slider.round {
	border-radius: 34px;
}

.slider.round:before {
	border-radius: 50%;
}

.card .card-header .card-title, .card .card-header .card-title .card-label
	{
	font-weight: 500;
	font-size: 1.275rem;
	color: #faf6f6;
}

.card .card-header {
	display: flex;
	justify-content: space-between;
	align-items: stretch;
	flex-wrap: wrap;
	min-height: unset !important;
	padding: 0px 2.25rem;
	color: var(- -kt-card-cap-color);
	background-color: var(- -kt-card-cap-bg);
	border-bottom: 1px solid var(- -kt-card-border-color);
}

.card .card-header .card-title1, .card .card-header .card-title1 .card-label
	{
	font-weight: 500;
	font-size: 1.275rem;
}

.card .card-header-primary .card-icon, .card .card-header-primary:not (.card-header-icon
	 ):not (.card-header-text ), .card .card-header-primary .card-text {
	box-shadow: 0 4px 20px 0px rgba(0, 0, 0, 0.14), 0 7px 10px -5px
		rgba(156, 39, 176, 0.4);
}

.card [class*="card-header-"] .card-icon, .card [class*="card-header-"] .card-text
	{
	border-radius: 3px;
	padding: 15px;
	margin-top: -20px;
	margin-right: 15px;
	float: left;
}

.card .card-header-rose .card-icon, .card .card-header-rose:not (.card-header-icon
	 ):not (.card-header-text ), .card .card-header-rose .card-text {
	box-shadow: unset;
}

.card .card-header-rose .card-icon, .card .card-header-rose .card-text,
	.card .card-header-rose:not (.card-header-icon ):not (.card-header-text
	 ), .card.bg-rose, .card.card-rotate.bg-rose .front, .card.card-rotate.bg-rose .back
	{
	background: linear-gradient(60deg, #f9c24eeb, #ef3421e8);
}

.selectBox {
	position: relative;
}

.selectBox select {
	width: 95%;
}

#checkboxes2 {
	display: none;
	border: 1px #dadada solid;
	height: 180px;
	overflow-y: scroll;
	position: absolute;
	background: #fff;
	z-index: 1;
	padding: 10px;
}

#checkboxes2 label {
	width: 74%;
}

#checkboxes2 input {
	width: 18%;
}

#checkboxes3 {
	display: none;
	border: 1px #dadada solid;
	height: auto;
	max-height: 180px;
	/* width: 142px; */
	overflow-y: scroll;
	position: absolute;
	background: #fff;
	z-index: 1;
	padding: 10px;
}

#checkboxes3 label {
	width: 74%;
}

#checkboxes3 input {
	width: 18%;
}

#checkboxes4 {
	display: none;
	border: 1px #dadada solid;
	height: 180px;
	/* width: 142px; */
	overflow-y: scroll;
	position: absolute;
	background: #fff;
	z-index: 1;
	padding: 10px;
}

#checkboxes4 label {
	width: 74%;
}

#checkboxes4 input {
	width: 18%;
}

.overSelect {
	position: absolute;
	left: 0;
	right: 0;
	top: 0;
	bottom: 0;
}

#checkboxes5 {
	display: none;
	border: 1px #dadada solid;
	height: 50px;
	/* width: 142px; */
	overflow-y: scroll;
	position: absolute;
	background: #fff;
	z-index: 1;
	padding: 10px;
}

#checkboxes5 input {
	width: 18%;
}

#checkboxes5 label {
	width: 74%;
}

label {
	color: black;
}

th {
	font-weight: bold !important;
}

.form-select.form-select-solid {
	background-color: #fdf8f8 !important;
	border-color: #fde9c0 !important;
	color: #5E6278 !important;
	transition: color 0.2s ease !important;
}

.form-control {
	border: 1px solid #f5f8fa;
	box-shadow: none !important;
	background-image: unset !important;
}

.dt-buttons.btn-group.flex-wrap {
	display: none;
}

.btn.btn-primary {
	background: #202f4b;
}

/* Chrome, Safari, Edge, Opera */
input::-webkit-outer-spin-button, input::-webkit-inner-spin-button {
	-webkit-appearance: none;
	margin: 0;
}

/* Firefox */
input[type=number] {
	-moz-appearance: textfield;
}

.table thead tr th {
	font-size: 0.95rem !important
}

.nav-pills.nav-pills-rose .nav-item .nav-link.active, .nav-pills.nav-pills-rose .nav-item .nav-link.active:focus,
	.nav-pills.nav-pills-rose .nav-item .nav-link.active:hover {
	background: linear-gradient(45deg, #234B7A, #ffffff);
	/* box-shadow: 0 4px 20px 0px rgb(0 0 0 / 14%), 0 7px 10px -5px #aca353; */
	color: #fff;
}

.nav-link.active i {
	color: #fdf8f8 !important;
}

a#infotabs {
	margin-left: unset !important;
}

.selectBox {
	position: relative;
}

.selectBox select {
	width: 95%;
}

#checkboxes1 {
	display: none;
	border: 1px #dadada solid;
	height: auto;
	max-height: 300px;
	overflow-y: scroll;
	position: Absolute;
	background: #fff;
	z-index: 1;
	margin-left: 5px;
}

#checkboxes1 label {
	width: 74%;
}

#checkboxes1 input {
	width: 18%;
}

span.clockpicker-span-hours.text-primary {
	color: #999999 !important;
}

span.clockpicker-span-minutes.text-primary {
	color: #999999 !important;
}

button.btn.btn-sm.btn-default.clockpicker-button.am-button {
	padding: 0.40625rem 0.25rem !important;
	line-height: 0.5;
}

button.btn.btn-sm.btn-default.clockpicker-button.pm-button {
	padding: 0.40625rem 0.25rem !important;
	line-height: 0.5;
}

.clockpicker-popover .popover-title {
	font-size: 21px !important;
	line-height: 23px !important;
}
</style>
<script>
				function myFunction() {
					var x = document.getElementById("actions11").value;
					if (x == 'csv') {
						document.querySelector('.buttons-csv').click();
					}
					if (x == 'copy') {
						document.querySelector('.buttons-copy').click();
					}
					if (x == 'pdf') {
						document.querySelector('.buttons-pdf').click();
					}

					// document.querySelector('.buttons-excel').click();
					// document.querySelector('.buttons-print').click();


				}

				function inputKeydownevent(event, input) {
					if (input == 'pgRefNum') {
						if (event.which != 8 && event.which != 0 && event.which < 48 || event.which > 57) {
							//event.preventDefault();
						}
					}
					if (input == 'orderId') {
						if (event.which != 8 && event.which != 0 && event.which < 48 || event.which > 57) {
							//event.preventDefault();
						}
					}
					if (input == 'customerEmail') {
					}
					if (input == 'phoneNo') {
						if (event.which != 8 && event.which != 0 && event.which < 48 || event.which > 57) {
							event.preventDefault();
						}
					}
				}

				function tabChangePopUp(id) {
					debugger
					if (id == "link110") {
						document.getElementById("txnInfo").classList.add("active");
						document.getElementById("customerInfo").classList.remove("active");
						document.getElementById("paymentInfo").classList.remove("active");

						document.getElementById("link110").classList.add("active");
						document.getElementById("link111").classList.remove("active");
						document.getElementById("link112").classList.remove("active");
					}
					if (id == "link111") {
						document.getElementById("paymentInfo").classList.add("active");
						document.getElementById("txnInfo").classList.remove("active");
						document.getElementById("customerInfo").classList.remove("active");

						document.getElementById("link111").classList.add("active");
						document.getElementById("link110").classList.remove("active");
						document.getElementById("link112").classList.remove("active");
					}
					if (id == "link112") {

						document.getElementById("customerInfo").classList.add("active");
						document.getElementById("paymentInfo").classList.remove("active");
						document.getElementById("txnInfo").classList.remove("active");

						document.getElementById("link112").classList.add("active");
						document.getElementById("link111").classList.remove("active");
						document.getElementById("link110").classList.remove("active");
					}
				}
			</script>
</head>

<body id="mainBody">
	<!-- <div id="loader-wrapper" style="width: 100%; height: 100%; display:none;">
	  <div id="loader"></div>
    </div> -->
	<s:set var="needToShowAcqFieldsInReportS"
		value="%{#session.USER.needToShowAcqFieldsInReport}" />
	<s:set var="userType" value="%{#session.USER.UserType.name()}" />
	<s:hidden id="needToShowAcqFieldsInReport"
		name="needToShowAcqFieldsInReport"
		value="%{needToShowAcqFieldsInReportS}"></s:hidden>
	<s:hidden id="userTypeName" name="userTypeName" value="%{userType}"></s:hidden>

	<div class="modal" id="popup" style="overflow-y: auto;">
		<!-- <div > -->
		<!--<div class="wrapper " style="max-height: 590px;"> -->


		<!-- Navbar -->

		<!-- End Navbar -->
		<div class="content innerpopupDv">
			<div>
				<div>
					<div class="card ">
						<div class="card-header"
							style="display: inline-flex; padding: 1rem;">
							<h4 class="card-title1">Customer Transaction Information</h4>
							<button
								style="position: relative; border: none; background: none; top: 2px;"
								id="closeBtn1">
								<i class="fa fa-times" aria-hidden="true"></i>
							</button>
						</div>
						<div class="card-body ">
							<div class="row">
								<div class="col-lg-4 col-md-4 col-sm-12">
									<!--
											  color-classes: "nav-pills-primary", "nav-pills-info", "nav-pills-success", "nav-pills-warning","nav-pills-danger"
										  -->
									<ul
										class="nav nav-pills nav-pills-rose nav-pills-icons flex-column"
										role="tablist">
										<li class="nav-item" id="listitem"><a
											class="nav-link active" data-toggle="tab" href="#link110"
											onclick="tabChangePopUp('link110')" role="tablist"
											id="txnInfo"> <i class="fa fa-money" aria-hidden="true">&#xf0d6;</i>
												Transaction Information

										</a></li>
										<li class="nav-item" id="listitem"><a class="nav-link"
											data-toggle="tab" href="#link111"
											onclick="tabChangePopUp('link111')" role="tablist"
											id="paymentInfo"> <i class="fa fa-credit-card"
												aria-hidden="true"></i> Payment Information
										</a></li>
										<li class="nav-item" id="listitem"><a class="nav-link"
											data-toggle="tab" href="#link112"
											onclick="tabChangePopUp('link112')" role="tablist"
											id="customerInfo"> <i class="fa fa-user"
												aria-hidden="true"></i> Customer Information
										</a></li>
									</ul>
								</div>
								<div class="col-lg-8 col-md-8 col-sm-12 ml-2">
									<div class="tab-content">
										<div class="tab-pane active" id="link110">
											<div class="card-header" role="tab" id="headingOne">
												<h5 class="mb-0">
													<a style="display: inline-flex; width: 100%;"
														true" aria-controls="collapseOne">

														<h4>Transaction Information</h4> <!-- <i
																		class="material-icons">keyboard_arrow_down</i> -->
													</a>
												</h5>
											</div>
											<div class="card-body">
												<table width="100%" class="">
													<tr>
														<th align="left" valign="middle">Amount</th>
														<th align="left" valign="middle">Status</th>

													</tr>

													<tr id="sec11">
														<td align="left" valign="middle"></td>
														<td align="left" valign="middle"></td>

													</tr>
													<tr>
														<th align="left" valign="middle">Order Id</th>
														<th align="left" valign="middle">PG Ref No.</th>

													</tr>
													<tr id="sec12">
														<td align="left" valign="middle"></td>
														<td align="left" valign="middle"></td>

													</tr>
													<tr>
														<th align="left" valign="middle">ARN No.</th>
														<th align="left" valign="middle">RRN No.</th>

													</tr>
													<tr id="sec13">
														<td align="left" valign="middle"></td>
														<td align="left" valign="middle"></td>

													</tr>
													<tr id="sec14h">
														<th align="left" valign="middle">ACQ ID.</th>
														<th align="left" valign="middle">Total Refunded
															Amount</th>

													</tr>
													<tr id="sec14">
														<td align="left" valign="middle"></td>
														<td align="left" valign="middle"></td>
													</tr>
													<tr id="sec16h">
														<th align="left" valign="middle">IP Address.</th>
														<th align="left" valign="middle">SALE Amount.</th>
													</tr>
													<tr id="sec16">
														<td align="left" valign="middle"></td>
														<td align="left" valign="middle"></td>
													</tr>

												</table>

												<!-- <label for="cardMask" class="bmd-label-floating">Card Mask</label>
												 <input type="email" class="form-control" id="cardMask"> -->

											</div>
											<div id="accordion" role="tablist">
												<div class="card-collapse">
													<div class="card-header" role="tab" id="headingOne">
														<h5 class="mb-0">
															<a id="infotabs"
																style="display: inline-flex; width: 100%;"
																data-toggle="collapse" href="#collapseThree"
																aria-expanded="true" aria-controls="collapseOne"
																class="collapsed">
																<h4>Trail</h4> <!-- <i class="fa fa-angle-down" aria-hidden="true"></i> -->

															</a>
														</h5>
													</div>
													<div id="collapseThree" class="collapse show"
														role="tabpanel" aria-labelledby="headingOne"
														data-parent="#accordion">
														<div class="card-body">
															<table width="100%" class="table table-responsive"
																id="transactionDetails">
																<tr>
																	<th align="left" valign="middle">Order ID</th>
																	<th align="left" valign="middle">Transaction Type</th>
																	<th align="left" valign="middle">Date/th>
																	<th align="left" valign="middle">Status</th>
																</tr>

																<tr id="sec15">
																	<td align="left" valign="middle"></td>
																	<td align="left" valign="middle"></td>
																	<td align="left" valign="middle"></td>
																	<td align="left" valign="middle"></td>
																</tr>
																<tr id="sec15">
																	<td align="left" valign="middle"></td>
																	<td align="left" valign="middle"></td>
																	<td align="left" valign="middle"></td>
																	<td align="left" valign="middle"></td>
																</tr>


															</table>

														</div>
													</div>
												</div>
											</div>
											<!-- <h4 style="margin-left:10px;color: #4c55a0 !important;font-size:15px;font-weight:600">Trail</h4> -->





										</div>
										<div class="tab-pane" id="link111">
											<div class="card-header" role="tab" id="headingOne">
												<h5 class="mb-0">
													<a style="display: inline-flex; width: 100%;">

														<h4>Payment Information</h4> <!-- <i
																		class="material-icons">keyboard_arrow_down</i> -->
													</a>
												</h5>
											</div>

											<div class="card-body">
												<table width="100%" class="">
													<tr>
														<th height="25" colspan="4" align="left" valign="middle">Card
															Holder Name</th>

													</tr>
													<tr id="sec3chn">
														<td height="25" colspan="4" align="left" valign="middle"></td>

													</tr>

													<tr>
														<th align="left" valign="middle">Card Mask</th>
														<th align="left" valign="middle">Issuer</th>

													</tr>

													<tr id="sec3">
														<td align="left" valign="middle"></td>
														<td align="left" valign="middle"></td>

													</tr>
													<s:if
														test="%{#session.USER.UserType.name() != 'SUBADMIN' || #session.USER.needToShowAcqFieldsInReport == true}">
														<tr>
															<th align="left" valign="middle">Acquirer</th>
															<th align="left" valign="middle">MOP Type</th>

														</tr>
														<tr id="sec4">
															<td align="left" valign="middle"></td>
															<td align="left" valign="middle"></td>

														</tr>
													</s:if>
													<tr>
														<th align="left" valign="middle">PG TDR</th>
														<th align="left" valign="middle">PG GST</th>

													</tr>
													<tr id="sec5">
														<td align="left" valign="middle"></td>
														<td align="left" valign="middle"></td>

													</tr>
													<tr>
														<th align="left" valign="middle">Acquirer TDR</th>
														<th align="left" valign="middle">Acquirer GST</th>


													</tr>


													<tr id="sec6">
														<td align="left" valign="middle"></td>
														<td align="left" valign="middle"></td>


													</tr>

													<tr class="udf6Box" style="display: none;">
														<th height="25" colspan="4" align="left" valign="middle"><span>UDF6</span></th>

													</tr>
													<tr id="udf6" class="udf6Box" style="display: none;">
														<td height="25" colspan="4" align="left" valign="middle"></td>

													</tr>

													<tr>
														<th height="25" colspan="4" align="left" valign="middle"><span>Acquirer
																Response</span></th>

													</tr>
													<tr id="address5">
														<td height="25" colspan="4" align="left" valign="middle"></td>

													</tr>
													<!-- <tr>
																  <th height="25" colspan="4" align="left" valign="middle"><span>Address</span></th>

																</tr>
																<tr id="address6">
																  <td height="25" colspan="4" align="left" valign="middle"></td>

																</tr> -->
												</table>
											</div>
										</div>
										<div class="tab-pane" id="link112">

											<div id="accordion" role="tablist">
												<div class="card-collapse">
													<div class="card-header" role="tab" id="headingOne">
														<h5 class="mb-0">
															<a id="infotabs" data-toggle="collapse"
																style="display: inline-flex; width: 100%;"
																href="#collapseOne" aria-expanded="true"
																aria-controls="collapseOne" class="collapsed">
																<h4>Billing Address</h4> <!-- <i
																				class="material-icons">keyboard_arrow_down</i> -->
															</a>
														</h5>
													</div>
													<div id="collapseOne" class="collapse show" role="tabpanel"
														aria-labelledby="headingOne" data-parent="#accordion">
														<div class="card-body">

															<table width="100%" class="">
																<tr>
																	<th align="left" valign="middle">Name</th>
																	<th align="left" valign="middle">Phone No</th>

																</tr>

																<tr id="sec1">
																	<td align="left" valign="middle"></td>
																	<td align="left" valign="middle"></td>

																</tr>
																<tr>
																	<th align="left" valign="middle">City</th>
																	<th align="left" valign="middle">State</th>

																</tr>
																<tr id="sec2">
																	<td align="left" valign="middle"></td>
																	<td align="left" valign="middle"></td>

																</tr>
																<tr>

																	<th align="left" valign="middle">Country</th>
																	<th align="left" valign="middle">Zip</th>
																</tr>
																<tr id="sec7">

																	<td align="left" valign="middle"></td>
																	<td align="left" valign="middle"></td>
																</tr>
																<tr>
																	<th height="25" colspan="4" align="left"
																		valign="middle"><span>Address1</span></th>
																</tr>
																<tr id="address1">
																	<td height="25" colspan="4" align="left"
																		valign="middle"></td>
																</tr>
																<tr>
																	<th height="25" colspan="4" align="left"
																		valign="middle"><span>Address2</span></th>
																</tr>
																<tr id="address2">
																	<td height="25" colspan="4" align="left"
																		valign="middle"></td>
																</tr>


															</table>

														</div>
													</div>
												</div>
												<div class="card-collapse">
													<div class="card-header" role="tab" id="headingTwo">
														<h5 class="mb-0">
															<a id="infotabs" class="collapsed"
																style="display: inline-flex; width: 100%;"
																data-toggle="collapse" href="#collapseTwo"
																aria-expanded="false" aria-controls="collapseTwo">
																<h4>Shipping Address</h4> <!-- <i
																				class="material-icons">keyboard_arrow_down</i> -->
															</a>
														</h5>
													</div>
													<div id="collapseTwo" class="" role="tabpanel"
														aria-labelledby="headingTwo" data-parent="#accordion">
														<div class="card-body">

															<table width="100%" class="">
																<tr>
																	<th align="left" valign="middle">Name</th>
																	<th align="left" valign="middle">Phone No</th>

																</tr>

																<tr id="sec8">
																	<td align="left" valign="middle"></td>
																	<td align="left" valign="middle"></td>

																</tr>
																<tr>
																	<th align="left" valign="middle">City</th>
																	<th align="left" valign="middle">State</th>

																</tr>
																<tr id="sec9">
																	<td align="left" valign="middle"></td>
																	<td align="left" valign="middle"></td>

																</tr>
																<tr>

																	<th align="left" valign="middle">Country</th>
																	<th align="left" valign="middle">Zip</th>
																</tr>
																<tr id="sec10">

																	<td align="left" valign="middle"></td>
																	<td align="left" valign="middle"></td>
																</tr>
																<tr>
																	<th height="25" colspan="4" align="left"
																		valign="middle"><span>Address1</span></th>
																</tr>
																<tr id="address3">
																	<td height="25" colspan="4" align="left"
																		valign="middle"></td>
																</tr>
																<tr>
																	<th height="25" colspan="4" align="left"
																		valign="middle"><span>Address2</span></th>
																</tr>
																<tr id="address4">
																	<td height="25" colspan="4" align="left"
																		valign="middle"></td>
																</tr>


															</table>

														</div>
													</div>
												</div>

											</div>




										</div>
									</div>
									<div style="float: right;">
										<button class="btn btn-danger" id="closeBtn">
											Close
											<div class="ripple-container"></div>
										</button>
										<!-- <button type="submit" class="btn btn-fill btn-rose" id="closeBtn">Close</button> -->
									</div>
									<!-- <div style="text-align: center;"><button class="btn btn-danger" id="closeBtn">Close</button></div>	 -->
								</div>

							</div>
						</div>
					</div>
				</div>

			</div>
		</div>


		<!-- </div>
		</div>-->
	</div>


	<div class="content d-flex flex-column flex-column-fluid"
		id="kt_content">
		<!--begin::Toolbar-->
		<div class="toolbar" id="kt_toolbar">
			<!--begin::Container-->
			<div id="kt_toolbar_container"
				class="container-fluid d-flex flex-stack">
				<!--begin::Page title-->

				<!--end::Page title-->

			</div>
			<!--end::Container-->
		</div>


		<!--end::Toolbar-->
		<!--begin::Post-->
		<div class="post d-flex flex-column-fluid" id="kt_post">
			<!--begin::Container-->
			<div id="kt_content_container" class="container-xxl"
				onclick="displayNone()">


				<!--begin::Page title-->
				<div data-kt-swapper="true" data-kt-swapper-mode="prepend"
					data-kt-swapper-parent="{default: '#kt_content_container', 'lg': '#kt_toolbar_container'}"
					class="page-title d-flex align-items-center me-3 flex-wrap mb-5 mb-lg-0 lh-1">
					<!--begin::Title-->
					<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">Reseller
						Search Payments</h1>
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
						<li class="breadcrumb-item text-dark">Reseller Search
							Payments</li>
						<!--end::Item-->
					</ul>
					<!--end::Breadcrumb-->
				</div>
				<!--end::Page title-->

				<!-- <form action="" class="form mb-15 fv-plugins-bootstrap5 fv-plugins-framework" method="post" id="search_payment_form"> -->
				<div class="row my-5">
					<div class="col">
						<form action="" class="form mb-15" method="post"
							id="captured_form">
							<div class="card">
								<div class="card-body">
									<!--begin::Input group-->
									<div class="row g-9 mb-8">
										<div class="col-md-4 fv-row fv-plugins-icon-container">
											<label
												class="d-flex align-items-center fs-6 fw-semibold mb-2">
												<span class="">New Depositor</span>
											</label>
											<!--end::Label-->
											<!-- <input type="text" class="form-control form-control-solid" name="pgrefnumber"> -->

											<!-- <s:textfield id="pgRefNum" class="form-control form-control-solid"
													name="pgRefNum" type="text" value="" autocomplete="off"
													maxlength="16" onblur="validPgRefNum();" ondrop="return false;" onKeyDown="if(event.keyCode === 32)return false;" onpaste="removeSpaces(this);"></s:textfield> -->
											<label class="switch"> <input type="checkbox"
												id="newDespositor"> <span class="slider round"></span>
											</label>

										</div>
									</div>
									<div class="row g-9 mb-8">
										<!--begin::Col-->
										<div class="col-md-4 fv-row fv-plugins-icon-container"
											style="display: none;">
											<label
												class="d-flex align-items-center fs-6 fw-semibold mb-2">
												<span class="">PG Ref Number</span>
											</label>
											<!--end::Label-->
											<!-- <input type="text" class="form-control form-control-solid" name="pgrefnumber"> -->

											<!-- <s:textfield id="pgRefNum" class="form-control form-control-solid"
													name="pgRefNum" type="text" value="" autocomplete="off"
													maxlength="16" onblur="validPgRefNum();" ondrop="return false;" onKeyDown="if(event.keyCode === 32)return false;" onpaste="removeSpaces(this);"></s:textfield> -->
											<s:textfield type="number" min="0"
												class="form-control form-control-solid" id="pgRefNum"
												name="pgRefNum" maxlength="17"
												onkeydown="inputKeydownevent(event,'pgRefNum')"
												oninput="if(value.length>16)value=value.slice(0,16)" />

										</div>
										<div class="col-md-4 fv-row" style="display: none;">
											<label
												class="d-flex align-items-center fs-6 fw-semibold mb-2">
												<span class="">Order ID</span>
											</label>
											<!--end::Label-->
											<!-- <input type="text" class="form-control form-control-solid" name="orderid"> -->
											<!-- <s:textfield id="orderId" class="form-control form-control-solid" name="orderId"
										type="text" value="" autocomplete="off"
										onKeyDown="validateOrderIdvalue(this);" onkeypress="return validateOrderId(event);"  ondrop="return false;" onpaste="validateOrderIdvalue(this);" maxlength="50"></s:textfield> -->
											<s:textfield type="text"
												class="form-control form-control-solid" id="orderId"
												name="orderId"
												onkeydown="inputKeydownevent(event,'orderId')"
												oninput="if(value.length>30)value=value.slice(0,30)" />

										</div>

										<div class="col-md-4 fv-row fv-plugins-icon-container"
											style="display: none;">
											<label
												class="d-flex align-items-center fs-6 fw-semibold mb-2">
												<span class="">Customer Email</span>
											</label>
											<!--end::Label-->
											<!-- <input type="email" class="form-control form-control-solid" name="emailid"> -->
											<!-- <s:textfield id="customerEmail" class="form-control form-control-solid"
										name="customerEmail" type="text" value="" autocomplete="off" ondrop="return false;"
										onblur="validateCustomerEmail(this);" onKeyDown="if(event.keyCode === 32)return false;" onpaste="removeSpaces(this);"></s:textfield> -->
											<s:textfield type="email" id="customerEmail"
												class="form-control form-control-solid" name="customerEmail"
												onkeydown="inputKeydownevent(event,'customerEmail')" />
											<!-- <div class="fv-plugins-message-container invalid-feedback"></div> -->
										</div>
									</div>
									<div class="row g-9 mb-8">
										<div class="col-md-4 fv-row fv-plugins-icon-container">
											<label
												class="d-flex align-items-center fs-6 fw-semibold mb-2">
												<span class="">Customer Phone</span>
											</label>
											<!--end::Label-->
											<!-- <input type="number" min="0" class="form-control form-control-solid" name="phonenumber"> -->
											<!-- <s:textfield id="custPhone" class="form-control form-control-solid"
										            name="custPhone" type="text" value="" autocomplete="off" onblur="validateCustomerPhone(this);" onKeyDown="if(event.keyCode === 32)return false;" ondrop="return false;" onpaste="removeSpaces(this);"  maxlength="14"></s:textfield> -->


											<input type="number" id="custPhone" min="0"
												class="form-control form-control-solid" name="custPhone"
												onkeydown="inputKeydownevent(event,'phoneNo')"
												oninput="if(value.length>10)value=value.slice(0,10)" />

										</div>
										<div class="col-md-4 fv-row">
											<label
												class="d-flex align-items-center fs-6 fw-semibold mb-2">
												<span class="">Currency</span>
											</label>
											<!--end::Label-->
											<!-- <select name="merchant" data-control="select2" data-placeholder="All" class="form-select form-select-solid select2-hidden-accessible" data-hide-search="true" data-select2-id="select2-data-13-06ua" tabindex="-1" aria-hidden="true" data-kt-initialized="1">
													<option value="All" data-select2-id="select2-data-15-wujq">All</option>
												    </select><span class="select2 select2-container select2-container--bootstrap5" dir="ltr" data-select2-id="select2-data-14-tuwf" style="width: 100%;"><span class="selection"><span class="select2-selection select2-selection--single form-select form-select-solid" role="combobox" aria-haspopup="true" aria-expanded="false" tabindex="0" aria-disabled="false" aria-labelledby="select2-merchant-da-container" aria-controls="select2-merchant-da-container"><span class="select2-selection__rendered" id="select2-merchant-da-container" role="textbox" aria-readonly="true" title="All">All</span><span class="select2-selection__arrow" role="presentation"><b role="presentation"></b></span></span></span><span class="dropdown-wrapper" aria-hidden="true"></span></span> -->

											<div class="">
												<s:select name="currency" id="currency" headerValue="ALL"
													headerKey="" list="currencyMap"
													class="form-select form-select-solid" />
											</div>

										</div>
										<s:if
											test="%{#session.USER.UserType.name() != 'SUBADMIN' || #session.USER.needToShowAcqFieldsInReport == true}">
											<div class="col-md-4 fv-row">
												<label
													class="d-flex align-items-center fs-6 fw-semibold mb-2">
													<span class="">Payment Method</span>
												</label>
												<!--end::Label-->
												<div class="">
													<div class="selectBox" id="selectBox3"
														onclick="showCheckboxes(event,3)" title="ALL">
														<select
															class="form-select form-select-sm form-select-solid">
															<option>ALL</option>
														</select>
														<div class="overSelect"></div>
													</div>
													<div id="checkboxes3" onclick="getCheckBoxValue(3)">

														<s:checkboxlist headerKey="ALL" headerValue="ALL"
															class="myCheckBox3"
															list="@com.pay10.commons.util.PaymentTypeUI@values()"
															listValue="name" listKey="code" name="paymentMethod"
															onchange="getMoptypes()"
															id="paymentMethod" autocomplete="off"
															value="paymentMethod" />

													</div>
												</div>
											</div>
										</s:if>
									</div>
									<div class="row g-9 mb-8">
										<s:if
											test="%{#session.USER.UserType.name() != 'SUBADMIN' || #session.USER.needToShowAcqFieldsInReport == true}">
											<div class="col-md-4 fv-row">
                                                <label class="d-flex align-items-center fs-6 fw-semibold mb-2">
                                                    <span class="">MOP Type</span>
                                                </label>
                                                <!--end::Label-->

                                                <select  class="moptype-select2 form-control form-control-solid" name="mopType" id="mopType" multiple="multiple">
                                                </select>

                                            </div>
										</s:if>
										<div class="col-md-4 fv-row">
											<label
												class="d-flex align-items-center fs-6 fw-semibold mb-2">
												<span class="">Transaction Type</span>
											</label>
											<!--end::Label-->
											<!-- <select name="merchant" data-control="select2" data-placeholder="All" class="form-select form-select-solid select2-hidden-accessible" data-hide-search="true" data-select2-id="select2-data-8-z3pr" tabindex="-1" aria-hidden="true" data-kt-initialized="1">
														<option value="All" data-select2-id="select2-data-10-s1n4">All</option>
														<option value="SALE">SALE</option>
														<option value="REFUND">REFUND</option>
													</select><span class="select2 select2-container select2-container--bootstrap5" dir="ltr" data-select2-id="select2-data-9-re63" style="width: 100%;"><span class="selection"><span class="select2-selection select2-selection--single form-select form-select-solid" role="combobox" aria-haspopup="true" aria-expanded="false" tabindex="0" aria-disabled="false" aria-labelledby="select2-merchant-wf-container" aria-controls="select2-merchant-wf-container"><span class="select2-selection__rendered" id="select2-merchant-wf-container" role="textbox" aria-readonly="true" title="All">All</span><span class="select2-selection__arrow" role="presentation"><b role="presentation"></b></span></span></span><span class="dropdown-wrapper" aria-hidden="true"></span></span>
												 -->

											<s:select headerKey="" headerValue="ALL"
												class="form-select form-select-sm form-select-solid"
												list="txnTypelist" listValue="name" listKey="code"
												name="transactionType" id="transactionType"
												autocomplete="off" value="name" />


										</div>
										<div class="col-md-4 fv-row">
											<label
												class="d-flex align-items-center fs-6 fw-semibold mb-2">
												<span class="">Status</span>
											</label>
											<!--end::Label-->
											<!-- <select class="form-select form-select-sm form-select-solid select2-hidden-accessible" data-control="select2" data-close-on-select="false" data-placeholder="All" data-allow-clear="true" multiple="" data-select2-id="select2-data-11-q2qa" tabindex="-1" aria-hidden="true" data-kt-initialized="1">
														<option value="All">All</option>
														<option value="Captured">Captured</option>
														<option value="Settled">Settled</option>
														<option value="Failed">Failed</option>
														<option value="Cancelled">Cancelled</option>
														<option value="Pending">Pending</option>
														<option value="Invalid">Invalid</option>
													</select><span class="select2 select2-container select2-container--bootstrap5" dir="ltr" data-select2-id="select2-data-12-eg4e" style="width: 100%;"><span class="selection"><span class="select2-selection select2-selection--multiple form-select form-select-sm form-select-solid" role="combobox" aria-haspopup="true" aria-expanded="false" tabindex="-1" aria-disabled="false"><ul class="select2-selection__rendered" id="select2-8g3e-container"></ul><span class="select2-search select2-search--inline"><textarea class="select2-search__field" type="search" tabindex="0" autocorrect="off" autocapitalize="none" spellcheck="false" role="searchbox" aria-autocomplete="list" autocomplete="off" aria-label="Search" aria-describedby="select2-8g3e-container" placeholder="All" style="width: 100%;"></textarea></span></span></span><span class="dropdown-wrapper" aria-hidden="true"></span></span> -->

											<div class="">
												<div class="" id="selectBox4"
													onclick="showCheckboxes(event,4)" title="ALL">
													<select
														class="form-select form-select-sm form-select-solid">
														<option>ALL</option>
													</select>
													<div class="overSelect"></div>
												</div>
												<div id="checkboxes4" onclick="getCheckBoxValue(4)">
													<s:checkboxlist headerKey="ALL" headerValue="ALL"
														list="@com.pay10.commons.util.StatusTypeUI@values()"
														name="status" id="status" value="status" listKey="code"
														listValue="name" class="myCheckBox4" />
												</div>
											</div>


										</div>
									</div>
									<div class="row g-9 mb-8">

										<div class="col-md-4 fv-row">
											<label
												class="d-flex align-items-center fs-6 fw-semibold mb-2">
												<span class="">Date From</span>
											</label>
											<!--end::Label-->
											<div class="position-relative d-flex align-items-center">
												<!--begin::Icon-->
												<!--begin::Svg Icon | path: icons/duotune/general/gen014.svg-->
												<span class="svg-icon svg-icon-2 position-absolute mx-4">
													<svg width="24" height="24" viewBox="0 0 24 24" fill="none"
														xmlns="http://www.w3.org/2000/svg"> <path
														opacity="0.3"
														d="M21 22H3C2.4 22 2 21.6 2 21V5C2 4.4 2.4 4 3 4H21C21.6 4 22 4.4 22 5V21C22 21.6 21.6 22 21 22Z"
														fill="currentColor"></path> <path
														d="M6 6C5.4 6 5 5.6 5 5V3C5 2.4 5.4 2 6 2C6.6 2 7 2.4 7 3V5C7 5.6 6.6 6 6 6ZM11 5V3C11 2.4 10.6 2 10 2C9.4 2 9 2.4 9 3V5C9 5.6 9.4 6 10 6C10.6 6 11 5.6 11 5ZM15 5V3C15 2.4 14.6 2 14 2C13.4 2 13 2.4 13 3V5C13 5.6 13.4 6 14 6C14.6 6 15 5.6 15 5ZM19 5V3C19 2.4 18.6 2 18 2C17.4 2 17 2.4 17 3V5C17 5.6 17.4 6 18 6C18.6 6 19 5.6 19 5Z"
														fill="currentColor"></path> <path
														d="M8.8 13.1C9.2 13.1 9.5 13 9.7 12.8C9.9 12.6 10.1 12.3 10.1 11.9C10.1 11.6 10 11.3 9.8 11.1C9.6 10.9 9.3 10.8 9 10.8C8.8 10.8 8.59999 10.8 8.39999 10.9C8.19999 11 8.1 11.1 8 11.2C7.9 11.3 7.8 11.4 7.7 11.6C7.6 11.8 7.5 11.9 7.5 12.1C7.5 12.2 7.4 12.2 7.3 12.3C7.2 12.4 7.09999 12.4 6.89999 12.4C6.69999 12.4 6.6 12.3 6.5 12.2C6.4 12.1 6.3 11.9 6.3 11.7C6.3 11.5 6.4 11.3 6.5 11.1C6.6 10.9 6.8 10.7 7 10.5C7.2 10.3 7.49999 10.1 7.89999 10C8.29999 9.90003 8.60001 9.80003 9.10001 9.80003C9.50001 9.80003 9.80001 9.90003 10.1 10C10.4 10.1 10.7 10.3 10.9 10.4C11.1 10.5 11.3 10.8 11.4 11.1C11.5 11.4 11.6 11.6 11.6 11.9C11.6 12.3 11.5 12.6 11.3 12.9C11.1 13.2 10.9 13.5 10.6 13.7C10.9 13.9 11.2 14.1 11.4 14.3C11.6 14.5 11.8 14.7 11.9 15C12 15.3 12.1 15.5 12.1 15.8C12.1 16.2 12 16.5 11.9 16.8C11.8 17.1 11.5 17.4 11.3 17.7C11.1 18 10.7 18.2 10.3 18.3C9.9 18.4 9.5 18.5 9 18.5C8.5 18.5 8.1 18.4 7.7 18.2C7.3 18 7 17.8 6.8 17.6C6.6 17.4 6.4 17.1 6.3 16.8C6.2 16.5 6.10001 16.3 6.10001 16.1C6.10001 15.9 6.2 15.7 6.3 15.6C6.4 15.5 6.6 15.4 6.8 15.4C6.9 15.4 7.00001 15.4 7.10001 15.5C7.20001 15.6 7.3 15.6 7.3 15.7C7.5 16.2 7.7 16.6 8 16.9C8.3 17.2 8.6 17.3 9 17.3C9.2 17.3 9.5 17.2 9.7 17.1C9.9 17 10.1 16.8 10.3 16.6C10.5 16.4 10.5 16.1 10.5 15.8C10.5 15.3 10.4 15 10.1 14.7C9.80001 14.4 9.50001 14.3 9.10001 14.3C9.00001 14.3 8.9 14.3 8.7 14.3C8.5 14.3 8.39999 14.3 8.39999 14.3C8.19999 14.3 7.99999 14.2 7.89999 14.1C7.79999 14 7.7 13.8 7.7 13.7C7.7 13.5 7.79999 13.4 7.89999 13.2C7.99999 13 8.2 13 8.5 13H8.8V13.1ZM15.3 17.5V12.2C14.3 13 13.6 13.3 13.3 13.3C13.1 13.3 13 13.2 12.9 13.1C12.8 13 12.7 12.8 12.7 12.6C12.7 12.4 12.8 12.3 12.9 12.2C13 12.1 13.2 12 13.6 11.8C14.1 11.6 14.5 11.3 14.7 11.1C14.9 10.9 15.2 10.6 15.5 10.3C15.8 10 15.9 9.80003 15.9 9.70003C15.9 9.60003 16.1 9.60004 16.3 9.60004C16.5 9.60004 16.7 9.70003 16.8 9.80003C16.9 9.90003 17 10.2 17 10.5V17.2C17 18 16.7 18.4 16.2 18.4C16 18.4 15.8 18.3 15.6 18.2C15.4 18.1 15.3 17.8 15.3 17.5Z"
														fill="currentColor"></path> </svg>
												</span>
												<!--end::Svg Icon-->
												<!--end::Icon-->
												<!--begin::Datepicker-->
												<input
													class="form-control form-control-solid ps-12 flatpickr-input"
													placeholder="Select a date" name="dateFrom" id="dateFrom"
													type="text" readonly="readonly">
												<!--end::Datepicker-->
											</div>
										</div>
										<!--begin::Col-->
										<div class="col-md-4 fv-row">
											<label
												class="d-flex align-items-center fs-6 fw-semibold mb-2">
												<span class="">Date To</span>
											</label>
											<!--end::Label-->
											<div class="position-relative d-flex align-items-center">
												<!--begin::Icon-->
												<!--begin::Svg Icon | path: icons/duotune/general/gen014.svg-->
												<span class="svg-icon svg-icon-2 position-absolute mx-4">
													<svg width="24" height="24" viewBox="0 0 24 24" fill="none"
														xmlns="http://www.w3.org/2000/svg"> <path
														opacity="0.3"
														d="M21 22H3C2.4 22 2 21.6 2 21V5C2 4.4 2.4 4 3 4H21C21.6 4 22 4.4 22 5V21C22 21.6 21.6 22 21 22Z"
														fill="currentColor"></path> <path
														d="M6 6C5.4 6 5 5.6 5 5V3C5 2.4 5.4 2 6 2C6.6 2 7 2.4 7 3V5C7 5.6 6.6 6 6 6ZM11 5V3C11 2.4 10.6 2 10 2C9.4 2 9 2.4 9 3V5C9 5.6 9.4 6 10 6C10.6 6 11 5.6 11 5ZM15 5V3C15 2.4 14.6 2 14 2C13.4 2 13 2.4 13 3V5C13 5.6 13.4 6 14 6C14.6 6 15 5.6 15 5ZM19 5V3C19 2.4 18.6 2 18 2C17.4 2 17 2.4 17 3V5C17 5.6 17.4 6 18 6C18.6 6 19 5.6 19 5Z"
														fill="currentColor"></path> <path
														d="M8.8 13.1C9.2 13.1 9.5 13 9.7 12.8C9.9 12.6 10.1 12.3 10.1 11.9C10.1 11.6 10 11.3 9.8 11.1C9.6 10.9 9.3 10.8 9 10.8C8.8 10.8 8.59999 10.8 8.39999 10.9C8.19999 11 8.1 11.1 8 11.2C7.9 11.3 7.8 11.4 7.7 11.6C7.6 11.8 7.5 11.9 7.5 12.1C7.5 12.2 7.4 12.2 7.3 12.3C7.2 12.4 7.09999 12.4 6.89999 12.4C6.69999 12.4 6.6 12.3 6.5 12.2C6.4 12.1 6.3 11.9 6.3 11.7C6.3 11.5 6.4 11.3 6.5 11.1C6.6 10.9 6.8 10.7 7 10.5C7.2 10.3 7.49999 10.1 7.89999 10C8.29999 9.90003 8.60001 9.80003 9.10001 9.80003C9.50001 9.80003 9.80001 9.90003 10.1 10C10.4 10.1 10.7 10.3 10.9 10.4C11.1 10.5 11.3 10.8 11.4 11.1C11.5 11.4 11.6 11.6 11.6 11.9C11.6 12.3 11.5 12.6 11.3 12.9C11.1 13.2 10.9 13.5 10.6 13.7C10.9 13.9 11.2 14.1 11.4 14.3C11.6 14.5 11.8 14.7 11.9 15C12 15.3 12.1 15.5 12.1 15.8C12.1 16.2 12 16.5 11.9 16.8C11.8 17.1 11.5 17.4 11.3 17.7C11.1 18 10.7 18.2 10.3 18.3C9.9 18.4 9.5 18.5 9 18.5C8.5 18.5 8.1 18.4 7.7 18.2C7.3 18 7 17.8 6.8 17.6C6.6 17.4 6.4 17.1 6.3 16.8C6.2 16.5 6.10001 16.3 6.10001 16.1C6.10001 15.9 6.2 15.7 6.3 15.6C6.4 15.5 6.6 15.4 6.8 15.4C6.9 15.4 7.00001 15.4 7.10001 15.5C7.20001 15.6 7.3 15.6 7.3 15.7C7.5 16.2 7.7 16.6 8 16.9C8.3 17.2 8.6 17.3 9 17.3C9.2 17.3 9.5 17.2 9.7 17.1C9.9 17 10.1 16.8 10.3 16.6C10.5 16.4 10.5 16.1 10.5 15.8C10.5 15.3 10.4 15 10.1 14.7C9.80001 14.4 9.50001 14.3 9.10001 14.3C9.00001 14.3 8.9 14.3 8.7 14.3C8.5 14.3 8.39999 14.3 8.39999 14.3C8.19999 14.3 7.99999 14.2 7.89999 14.1C7.79999 14 7.7 13.8 7.7 13.7C7.7 13.5 7.79999 13.4 7.89999 13.2C7.99999 13 8.2 13 8.5 13H8.8V13.1ZM15.3 17.5V12.2C14.3 13 13.6 13.3 13.3 13.3C13.1 13.3 13 13.2 12.9 13.1C12.8 13 12.7 12.8 12.7 12.6C12.7 12.4 12.8 12.3 12.9 12.2C13 12.1 13.2 12 13.6 11.8C14.1 11.6 14.5 11.3 14.7 11.1C14.9 10.9 15.2 10.6 15.5 10.3C15.8 10 15.9 9.80003 15.9 9.70003C15.9 9.60003 16.1 9.60004 16.3 9.60004C16.5 9.60004 16.7 9.70003 16.8 9.80003C16.9 9.90003 17 10.2 17 10.5V17.2C17 18 16.7 18.4 16.2 18.4C16 18.4 15.8 18.3 15.6 18.2C15.4 18.1 15.3 17.8 15.3 17.5Z"
														fill="currentColor"></path> </svg>
												</span>
												<!--end::Svg Icon-->
												<!--end::Icon-->
												<!--begin::Datepicker-->
												<input
													class="form-control form-control-solid ps-12 flatpickr-input"
													placeholder="Select a date" name="dateTo" id="dateTo"
													type="text" readonly="readonly">
												<!--end::Datepicker-->
											</div>
										</div>

										<div class="col-md-4 fv-row">
											<label
												class="d-flex align-items-center fs-6 fw-semibold mb-2">
												<span class="">Channel</span>
											</label>

											<div class="" id="selectBox5"
												onclick="showCheckboxes(event,5)" title="ALL">
												<select
													class="form-select form-select-sm form-select-solid">
													<option>ALL</option>
												</select>
												<div class="overSelect"></div>
											</div>
											<div id="checkboxes5" onclick="getCheckBoxValue(5)">
												<s:checkboxlist headerKey="ALL" headerValue="ALL"
													list="channelType" name="channelName" id="channelName"
													class="myCheckBox5" />
											</div>


										</div>
									</div>
										<div class="row g-9 mb-8">
											<div class="col-md-4 fv-row">
												<label
													class="d-flex align-items-center fs-6 fw-semibold mb-2">
													<span class="">SMA</span>
												</label>
												<s:if
													test="%{#session.USER.UserType.name()=='ADMIN' || #session.USER.UserType.name()=='SUBADMIN' || #session.USER.UserType.name()=='SUPERADMIN'}">
													<s:select name="smaId"
														class="form-select form-select-solid" id="smaId"
														onchange="getSMAMerchant(this.value);" headerKey="ALL"
														headerValue="ALL" list="listSMA" listKey="payId"
														listValue="businessName" autocomplete="off" />
												</s:if>

												<s:else>
													<s:select name="smaId"
														class="form-select form-select-solid" id="smaId"
														list="listSMA" listKey="payId"
														listValue="businessName" autocomplete="off" />
												</s:else>

												<!-- <span class="select2 select2-container select2-container--bootstrap5" dir="ltr" data-select2-id="select2-data-2-38m7" style="width: 100%;"><span class="selection"><span class="select2-selection select2-selection--single form-select form-select-solid" role="combobox" aria-haspopup="true" aria-expanded="false" tabindex="0" aria-disabled="false" aria-labelledby="select2-merchant-ua-container" aria-controls="select2-merchant-ua-container"><span class="select2-selection__rendered" id="select2-merchant-ua-container" role="textbox" aria-readonly="true" title="Ravi Kiran">Ravi Kiran</span><span class="select2-selection__arrow" role="presentation"><b role="presentation"></b></span></span></span><span class="dropdown-wrapper" aria-hidden="true"></span></span> -->
											</div>
											<div class="col-md-4 fv-row">
												<label
													class="d-flex align-items-center fs-6 fw-semibold mb-2">
													<span class="">MA</span>
												</label>
												<s:if
													test="%{#session.USER.UserType.name()=='ADMIN' || #session.USER.UserType.name()=='SUBADMIN' || #session.USER.UserType.name()=='SUPERADMIN'}">
													<s:select name="maId" class="form-select form-select-solid"
														id="maId" onchange="getMAMerchant(this.value);"
														headerKey="ALL" headerValue="ALL" list="listMA"
														listKey="payId" listValue="businessName"
														autocomplete="off" />
												</s:if>

												<s:else>
													<s:select name="maId" class="form-select form-select-solid"
														id="maId" list="listMA" listKey="payId"
														onchange="getMAMerchant(this.value);"
                                                        listValue="businessName" autocomplete="off" />
												</s:else>

												<!-- <span class="select2 select2-container select2-container--bootstrap5" dir="ltr" data-select2-id="select2-data-2-38m7" style="width: 100%;"><span class="selection"><span class="select2-selection select2-selection--single form-select form-select-solid" role="combobox" aria-haspopup="true" aria-expanded="false" tabindex="0" aria-disabled="false" aria-labelledby="select2-merchant-ua-container" aria-controls="select2-merchant-ua-container"><span class="select2-selection__rendered" id="select2-merchant-ua-container" role="textbox" aria-readonly="true" title="Ravi Kiran">Ravi Kiran</span><span class="select2-selection__arrow" role="presentation"><b role="presentation"></b></span></span></span><span class="dropdown-wrapper" aria-hidden="true"></span></span> -->
											</div>

											<div class="col-md-4 fv-row">
												<label
													class="d-flex align-items-center fs-6 fw-semibold mb-2">
													<span class="">Agent</span>
												</label>
												<s:if
													test="%{#session.USER.UserType.name()=='ADMIN' || #session.USER.UserType.name()=='SUBADMIN' || #session.USER.UserType.name()=='SUPERADMIN'}">
													<s:select name="agentId"
														class="form-select form-select-solid" id="agentId"
														onchange="getAgentMerchant(this.value);" headerKey="ALL"
														headerValue="ALL" list="listAgent" listKey="payId"
														listValue="businessName" autocomplete="off" />
												</s:if>

												<s:else>
													<s:select name="agentId"
														class="form-select form-select-solid" id="agentId"
														list="listAgent" listKey="payId"
														onchange="getAgentMerchant(this.value);"
														listValue="businessName" autocomplete="off" />
												</s:else>

												<!-- <span class="select2 select2-container select2-container--bootstrap5" dir="ltr" data-select2-id="select2-data-2-38m7" style="width: 100%;"><span class="selection"><span class="select2-selection select2-selection--single form-select form-select-solid" role="combobox" aria-haspopup="true" aria-expanded="false" tabindex="0" aria-disabled="false" aria-labelledby="select2-merchant-ua-container" aria-controls="select2-merchant-ua-container"><span class="select2-selection__rendered" id="select2-merchant-ua-container" role="textbox" aria-readonly="true" title="Ravi Kiran">Ravi Kiran</span><span class="select2-selection__arrow" role="presentation"><b role="presentation"></b></span></span></span><span class="dropdown-wrapper" aria-hidden="true"></span></span> -->
											</div>

										</div>
										<div class="row g-9 mb-8">
											<div class="col-md-4 fv-row">
												<label
													class="d-flex align-items-center fs-6 fw-semibold mb-2">
													<span class="">Merchant</span>
												</label>
												<s:if
													test="%{#session.USER.UserType.name()=='ADMIN' || #session.USER.UserType.name()=='SUBADMIN' || #session.USER.UserType.name()=='SUPERADMIN' ||  #session.USER.UserType.name()=='RESELLER' ||  #session.USER.UserType.name()=='SMA' ||  #session.USER.UserType.name()=='MA' ||  #session.USER.UserType.name()=='Agent'}">
													<s:select name="merchant"
														class="form-select form-select-solid adminMerchants"
														id="merchant" headerKey="ALL" headerValue="ALL"
														list="merchantList" listKey="emailId"
														listValue="businessName" autocomplete="off" />
												</s:if>
												<s:else>
													<s:select name="merchant"
														class="form-select form-select-solid" id="merchant"
														list="merchantList" listKey="emailId"
														listValue="businessName" autocomplete="off" />
												</s:else>

												<!-- <span class="select2 select2-container select2-container--bootstrap5" dir="ltr" data-select2-id="select2-data-2-38m7" style="width: 100%;"><span class="selection"><span class="select2-selection select2-selection--single form-select form-select-solid" role="combobox" aria-haspopup="true" aria-expanded="false" tabindex="0" aria-disabled="false" aria-labelledby="select2-merchant-ua-container" aria-controls="select2-merchant-ua-container"><span class="select2-selection__rendered" id="select2-merchant-ua-container" role="textbox" aria-readonly="true" title="Ravi Kiran">Ravi Kiran</span><span class="select2-selection__arrow" role="presentation"><b role="presentation"></b></span></span></span><span class="dropdown-wrapper" aria-hidden="true"></span></span> -->
											</div>
											<s:if
												test="%{#session.USER.UserType.name() != 'SUBADMIN' || #session.USER.needToShowAcqFieldsInReport == true}">
												<div class="col-md-4 fv-row fv-plugins-icon-container">
													<label
														class="d-flex align-items-center fs-6 fw-semibold mb-2">
														<span class="">Acquirer</span>
													</label>

													<s:if
														test="%{#session.USER.UserType.name()=='ADMIN'|| #session.USER.UserType.name()=='SUBADMIN' || #session.USER_TYPE.name()=='SUPERADMIN'}">


														<div class="txtnew">
															<div class="selectBox" id="selectBox1"
																onclick="showCheckboxes(event,1)" title="ALL">
																<select class="form-select form-select-solid">
																	<option>ALL</option>
																</select>
																<div class="overSelect"></div>
															</div>
															<div id="checkboxes1" onclick="getCheckBoxValue(1)">
																<s:checkboxlist headerKey="ALL" headerValue="ALL"
																	list="@com.pay10.commons.util.AcquirerTypeUI@values()"
																	id="acquirer" class="myCheckBox1" listKey="code"
																	listValue="name" name="acquirer" value="acquirer" />
															</div>
														</div>



													</s:if>
													<s:else>

														<div class="txtnew">
															<div class="selectBox" id="selectBox1"
																onclick="showCheckboxes(event,1)" title="ALL">
																<select class="form-select form-select-solid">
																	<option>ALL</option>
																</select>
																<div class="overSelect"></div>
															</div>
															<div id="checkboxes1" onclick="getCheckBoxValue(1)">
																<s:checkboxlist headerKey="ALL" headerValue="ALL"
																	list="@com.pay10.commons.util.AcquirerTypeUI@values()"
																	id="acquirer" class="myCheckBox1" listKey="code"
																	listValue="code" name="acquirer" value="acquirer" />
															</div>
														</div>


													</s:else>

												</div>
											</s:if>
											<div class="col-md-4 fv-row">
												<label
													class="d-flex align-items-center fs-6 fw-semibold mb-2">
													<span class="">IP Address</span>
												</label>

												<div class="txtnew">
													<s:textfield id="ipAddress"
														class="form-control form-control-solid" name="ipAddress"
														type="text" value="" autocomplete="off"
														ondrop="return false;" maxlength="50"></s:textfield>
												</div>
												<span id="validIpAddress" style="color: red; display: none;">Please
													Enter Valid IP Address.</span>
											</div>



											<div class="col-md-4 fv-row">
												<label
													class="d-flex align-items-center fs-6 fw-semibold mb-2">
													<span class="">Start Time</span>
												</label>

												<div class="input-group clockpicker ">
													<input type="text" class="form-control form-control-solid"
														id="startTime" name="startTime" value="00:00" readonly>
													<span class="input-group-addon"> <span
														class="glyphicon glyphicon-time"></span>
													</span>
												</div>

											</div>
											<div class="col-md-4 fv-row">
												<label
													class="d-flex align-items-center fs-6 fw-semibold mb-2">
													<span class="">End Time</span>
												</label>

												<div class="input-group clockpicker">
													<input type="text" id="endTime" name="endTime"
														class="form-control form-control-solid" value="00:00"
														readonly> <span class="input-group-addon">
														<span class="glyphicon glyphicon-time"></span>
													</span>
												</div>
											</div>

											<div class="col-md-4 fv-row">
												<label
													class="d-flex align-items-center fs-6 fw-semibold mb-2">
													<span class="">RRN</span>
												</label>

												<div class="txtnew">
													<s:textfield id="rrn"
														class="form-control form-control-solid" name="rrn"
														type="text" value="" autocomplete="off"
														ondrop="return false;" maxlength="50"></s:textfield>
												</div>

											</div>



										</div>

										<!-- Added a new row code start here -->
										<div class="row g-9 mb-8">

											<div class="col-md-4 fv-row">
												<label
													class="d-flex align-items-center fs-6 fw-semibold mb-2">
													<span class="">Total Amount</span>
												</label>

												<div class="txtnew">
													<s:textfield id="totalAmount"
														class="form-control form-control-solid" name="totalAmount"
														type="text" value="" autocomplete="off"
														ondrop="return false;" maxlength="50"></s:textfield>
												</div>
												<span id="validIpAddress" style="color: red; display: none;">Please
													Enter Valid IP Address.</span>
											</div>


											<div class="col-md-4 fv-row">
												<label
													class="d-flex align-items-center fs-6 fw-semibold mb-2">
													<span class="">Min Amount</span>
												</label>
												<div class="txtnew">
													<s:textfield id="minAmount"
														class="form-control form-control-solid" name="minAmount"
														type="number" value="" autocomplete="off"
														ondrop="return false;"></s:textfield>
												</div>
												<span id="validMinAmount" style="color: red; display: none;">Please
													Enter Min Amount.</span>

											</div>


											<div class="col-md-4 fv-row">
												<label
													class="d-flex align-items-center fs-6 fw-semibold mb-2">
													<span class="">Max Amount</span>
												</label>

												<div class="txtnew">
													<s:textfield id="maxAmount"
														class="form-control form-control-solid" name="maxAmount"
														type="number" value="" autocomplete="off"
														ondrop="return false;"></s:textfield>
												</div>
												<span id="validMaxAmount" style="color: red; display: none;">Please
													Enter Max Amount.</span>

											</div>

											<div class="col-md-4 fv-row">
												<label
													class="d-flex align-items-center fs-6 fw-semibold mb-2">
													<span class="">Column Name</span>
												</label>

												<div class="">

													<s:select name="columnName" id="columnName"
														headerValue="Select Column" headerKey=""
														list="filtersColumn" class="form-select form-select-solid" />
												</div>

											</div>

											<div class="col-md-4 fv-row">
												<label
													class="d-flex align-items-center fs-6 fw-semibold mb-2">
													<span class="">Logical Condition</span>
												</label>

												<div class="">
													<select id="logicalCondition"
														class="form-select form-select-solid"
														name="logicalCondition">
														<option value="">Select logical Operator</option>
													</select>
												</div>

											</div>


											<div class="col-md-4 fv-row">

												<label
													class="d-flex align-items-center fs-6 fw-semibold mb-2">
													<span class="">Search Text</span>
												</label>

												<div class="txtnew">
													<s:textfield id="searchText"
														class="form-control form-control-solid" name="searchText"
														type="text" value="" autocomplete="off"
														ondrop="return false;"></s:textfield>
												</div>
												<span id="validSearchText"
													style="color: red; display: none;">Please Enter
													Search Text.</span>

											</div>


											<div class="col-md-4 fv-row">
												<label
													class="d-flex align-items-center fs-6 fw-semibold mb-2">
													<span class="">Column Name</span>
												</label>

												<div class="">

													<s:select name="columnName1" id="columnName1"
														headerValue="Select Column" headerKey=""
														list="filtersColumn" class="form-select form-select-solid" />
												</div>

											</div>

											<div class="col-md-4 fv-row">
												<label
													class="d-flex align-items-center fs-6 fw-semibold mb-2">
													<span class="">Logical Condition</span>
												</label>

												<div class="">
													<select id="logicalCondition1"
														class="form-select form-select-solid"
														name="logicalCondition1">
														<option value="">Select logical Operator</option>
													</select>
												</div>

											</div>


											<div class="col-md-4 fv-row">

												<label
													class="d-flex align-items-center fs-6 fw-semibold mb-2">
													<span class="">Search Text</span>
												</label>

												<div class="txtnew">
													<s:textfield id="searchText1"
														class="form-control form-control-solid" name="searchText1"
														type="text" value="" autocomplete="off"
														ondrop="return false;"></s:textfield>
												</div>
												<span id="validSearchText1"
													style="color: red; display: none;">Please Enter
													Search Text.</span>

											</div>


											<div class="col-md-4 fv-row">
												<label
													class="d-flex align-items-center fs-6 fw-semibold mb-2">
													<span class="">Column Name</span>
												</label>

												<div class="">

													<s:select name="columnName2" id="columnName2"
														headerValue="Select Column" headerKey=""
														list="filtersColumn" class="form-select form-select-solid" />
												</div>

											</div>

											<div class="col-md-4 fv-row">
												<label
													class="d-flex align-items-center fs-6 fw-semibold mb-2">
													<span class="">Logical Condition</span>
												</label>

												<div class="">
													<select id="logicalCondition2"
														class="form-select form-select-solid"
														name="logicalCondition2">
														<option value="">Select logical Operator</option>
													</select>
												</div>

											</div>


											<div class="col-md-4 fv-row">

												<label
													class="d-flex align-items-center fs-6 fw-semibold mb-2">
													<span class="">Search Text</span>
												</label>

												<div class="txtnew">
													<s:textfield id="searchText2"
														class="form-control form-control-solid" name="searchText2"
														type="text" value="" autocomplete="off"
														ondrop="return false;"></s:textfield>
												</div>
												<span id="validSearchText2"
													style="color: red; display: none;">Please Enter
													Search Text.</span>

											</div>

										</div>
										<!-- Added a new row code end here -->


										<div class="row g-9 mb-8 justify-content-md-end">

											<div
												class="col-md-8 fv-row d-flex justify-content-center align-items-end justify-content-md-end pb-2 g-9">
												<input type="button" id="submit"
													class="btn mx-5 btn-secondary" value="VIEW">
												<div class="text-center">
													<button type="button" id="search_payment_submit"
														onclick="downloadReport()" class="btn btn-primary">
														<span class="indicator-label">DOWNLOAD</span> <span
															class="indicator-progress">Please wait... <span
															class="spinner-border spinner-border-sm align-middle ms-2"></span></span>
													</button>
												</div>
											</div>
										</div>
									</div>
								</div>
						</form>
					</div>
				</div>
				<div></div>
				<div class="row my-5 mt-4">
					<div class="col">
						<div class="card">
							<div class="card-body">
								<!--begin::Input group-->
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
											<button
												class="form-select form-select-solid actions dropbtn1">Customize
												Columns</button>
											<div class="dropdown-content1">
												<a class="toggle-vis" data-column="0">Txn Id</a> <a
													class="toggle-vis" data-column="1">Pg ref no</a> <a
													class="toggle-vis" data-column="2">Merchant</a> <a
													class="toggle-vis" data-column="3">Date</a> <a
													class="toggle-vis" data-column="4">Order Id</a> <a
													class="toggle-vis" data-column="5">Refund Order Id</a>
												<s:if
													test="%{#session.USER.UserType.name() != 'SUBADMIN' || #session.USER.needToShowAcqFieldsInReport == true}">
													<a class="toggle-vis" data-column="6">MOP Type</a>
													<a class="toggle-vis" data-column="7">Payment Method</a>
												</s:if>
												<a class="toggle-vis" data-column="8">Txn Type</a> <a
													class="toggle-vis" data-column="9">Status</a> <a
													class="toggle-vis" data-column="10">Base Amount</a> <a
													class="toggle-vis" data-column="11">Total Amount</a> <a
													class="toggle-vis" data-column="12">Pay Id</a> <a
													class="toggle-vis" data-column="13">Customer Email</a> <a
													class="toggle-vis" data-column="14">Customer Ph Number</a>
												<a class="toggle-vis" data-column="15">Udf 4</a>
											</div>
										</div>
									</div>
								</div>
								<div class="row g-9 mb-8">
									<div class="table-responsive">
										<s:if
											test="%{#session.USER.UserType.name() != 'SUBADMIN' || #session.USER.needToShowAcqFieldsInReport == true}">
											<table id="txnResultDataTable"
												class="table table-striped table-row-bordered gy-5 gs-7 dataTable no-footer">
												<thead>
													<tr class="fw-bold fs-6 text-gray-800">
														<th class="min-w-90px">Txn Id</th>
														<th class="min-w-90px">Pg Ref Num</th>
														<th class="min-w-90px">Merchant</th>
														<th class="min-w-90px">Date</th>
														<th class="min-w-90px">Order Id</th>
														<th class="min-w-90px">Refund Order Id</th>
														<th class="min-w-90px">MOP Type</th>
														<th class="min-w-90px">Payment Method</th>
														<th class="min-w-90px">Txn Type</th>
														<th class="min-w-90px">Status</th>
														<th class="min-w-90px">Base Amount</th>
														<th class="min-w-90px">Total Amount</th>
														<th class="min-w-90px">Pay ID</th>
														<th class="min-w-90px">Currency</th>

														<th class="min-w-90px">Customer Email</th>
														<th class="min-w-90px">Customer Ph Number</th>
														<th class="min-w-90px">Acquirer Type</th>
														<th class="min-w-90px">IP Address</th>
														<th class="min-w-90px">Card Mask</th>
														<th class="min-w-90px">RRN Number</th>
														<th>Card Holder Type</th>
														<th>Transaction Ref Number</th>
														<th>PSP NAME</th>

														<th class="min-w-90px"></th>
													</tr>
												</thead>
												<tfoot>
													<tr class="fw-bold fs-6 text-gray-800">
														<th class="min-w-90px"></th>
														<th class="min-w-90px"></th>
														<th class="min-w-90px"></th>
														<th class="min-w-90px"></th>
														<th class="min-w-90px"></th>
														<th class="min-w-90px"></th>
														<th class="min-w-90px"></th>
														<th class="min-w-90px"></th>
														<th class="min-w-90px"></th>
														<th class="min-w-90px"></th>

														<th class="min-w-90px"></th>
														<th class="min-w-90px"></th>
														<th class="min-w-90px"></th>
														<th class="min-w-90px"></th>
														<th class="min-w-90px"></th>
														<th class="min-w-90px"></th>
														<th class="min-w-90px"></th>
														<th class="min-w-90px"></th>
														<th class="min-w-90px"></th>
														<th class="min-w-90px"></th>
														<th class="min-w-90px"></th>
														<th class="min-w-90px"></th>

														<th class="min-w-90px"></th>
														<th class="min-w-90px"></th>
														<th class="min-w-90px"></th>
													</tr>
												</tfoot>
											</table>
										</s:if>
										<s:else>
											<table id="txnResultDataTableWithoutAcqFields"
												class="table table-striped table-row-bordered gy-5 gs-7 dataTable no-footer">
												<thead>
													<tr class="fw-bold fs-6 text-gray-800">
														<th class="min-w-90px">Txn Id</th>
														<th class="min-w-90px">Pg Ref Num</th>
														<th class="min-w-90px">Merchant</th>
														<th class="min-w-90px">Date</th>
														<th class="min-w-90px">Order Id</th>
														<th class="min-w-90px">Refund Order Id</th>
														<th class="min-w-90px">Txn Type</th>
														<th class="min-w-90px">Status</th>
														<th class="min-w-90px">Base Amount</th>
														<th class="min-w-90px">Total Amount</th>
														<th class="min-w-90px">Pay ID</th>
														<th class="min-w-90px">Currency</th>

														<th class="min-w-90px">Customer Email</th>
														<th class="min-w-90px">Customer Ph Number</th>
														<th class="min-w-90px">IP Address</th>
														<th class="min-w-90px">Card Mask</th>
														<th class="min-w-90px">RRN Number</th>
														<th>Card Holder Type</th>
														<th>Transaction Ref Number</th>
														<th>PSP NAME</th>
														<th>Transaction Ref Number</th>


														<th class="min-w-90px"></th>
													</tr>
												</thead>
												<tfoot>
													<tr class="fw-bold fs-6 text-gray-800">
														<th class="min-w-90px"></th>
														<th class="min-w-90px"></th>
														<th class="min-w-90px"></th>
														<th class="min-w-90px"></th>
														<th class="min-w-90px"></th>
														<th class="min-w-90px"></th>

														<th class="min-w-90px"></th>
														<th class="min-w-90px"></th>
														<th class="min-w-90px"></th>
														<th class="min-w-90px"></th>
														<th class="min-w-90px"></th>
														<th class="min-w-90px"></th>
														<th class="min-w-90px"></th>
														<th class="min-w-90px"></th>
														<th class="min-w-90px"></th>
														<th class="min-w-90px"></th>
														<th class="min-w-90px"></th>
														<th class="min-w-90px"></th>

														<th class="min-w-90px"></th>
														<th class="min-w-90px"></th>
														<th class="min-w-90px"></th>
														<th class="min-w-90px"></th>

													</tr>
												</tfoot>
											</table>
										</s:else>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>



				<div class="row my-5">
					<div class="col">
						<div class="card">
							<div class="card-header card-header-rose card-header-text">
								<div class="card-text">
									<h4 class="card-title">Initiated</h4>
								</div>
							</div>
							<div class="card-body">
								<!--begin::Input group-->
								<div class="row g-9 mb-8 justify-content-end">
									<div class="col-lg-12 col-sm-12 col-md-12">
										<table width="100%"
											class="table table-striped table-row-bordered gy-5 gs-7"
											id="totalTxnsAmount"></table>
									</div>

								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="row my-5">
					<div class="col">
						<div class="card">
							<div class="card-header card-header-rose card-header-text">
								<div class="card-text">
									<h4 class="card-title">Settled</h4>
								</div>
							</div>
							<div class="card-body">
								<!--begin::Input group-->
								<div class="row g-9 mb-8 justify-content-end">
									<div class="col-lg-12 col-sm-12 col-md-12">
										<table width="100%"
											class="table table-striped table-row-bordered gy-5 gs-7"
											id="totalSettledTxnsAmount"></table>
									</div>

								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<s:form id="downloadPaymentReport" name="downloadPaymentsReportAction"
			action="downloadPaymentsReportAction">
			<s:hidden name="currency" id="currencyH" value="" />
			<s:hidden name="dateFrom" id="dateFromH" value="" />
			<s:hidden name="dateTo" id="dateToH" value="" />
			<s:hidden name="merchantPayId" id="merchantPayIdH" value="" />
			<s:hidden name="transactionType" id="transactionTypeH" value="" />
			<s:hidden name="paymentType" id="paymentTypeH" value="" />
			<s:hidden name="status" id="statusH" value="" />
			<s:hidden name="acquirer" id="acquirerH" value="" />
			<s:hidden name="transactionId" id="transactionIdH" value="" />
			<s:hidden name="orderId" id="orderIdH" value="" />
			<s:hidden name="customerEmail" id="customerEmailH" value="" />
			<s:hidden name="customerPhone" id="customerPhoneH" value="" />
			<s:hidden name="mopType" id="mopTypeH" value="" />

			<s:hidden name="startTime" id="startTimeH" value="" />
			<s:hidden name="newDespositor" id="newDespositorH" value="" />
			<s:hidden name="endTime" id="endTimeH" value="" />

			<s:hidden name="ipAddress" id="ipAddressH" value="" />
			<s:hidden name="rrn" id="rrnH" value="" />
			<s:hidden name="totalAmount" id="totalAmountH" value="" />

			<s:hidden name="channelName" id="channelNameH" value="" />
			<s:hidden name="minAmount" id="minAmountH" value="" />
			<s:hidden name="maxAmount" id="maxAmountH" value="" />
			<s:hidden name="columnName" id="columnNameH" value="" />
			<s:hidden name="logicalCondition" id="logicalConditionH" value="" />
			<s:hidden name="searchText" id="searchTextH" value="" />

			<s:hidden name="columnName1" id="columnNameHH" value="" />
			<s:hidden name="logicalCondition1" id="logicalConditionHH" value="" />
			<s:hidden name="searchText1" id="searchTextHH" value="" />

			<s:hidden name="columnName2" id="columnNameHHH" value="" />
			<s:hidden name="logicalCondition2" id="logicalConditionHHH" value="" />
			<s:hidden name="searchText2" id="searchTextHHH" value="" />

			<s:hidden name="token" value="%{#session.customToken}" />
			<s:hidden name="needToShowAcquirerDetails"
				id="needToShowAcquirerDetails" value="" />
		</s:form>
		<s:a action='settledTransactionSearch'
			class="btn w-lg-25 w-md-75 w-100 btn-primary submit_btn d-none">
					Settled Report</s:a>
		<s:form name="chargeback" action="chargebackAction">
			<s:hidden name="orderId" id="orderIdc" value="" />
			<s:hidden name="payId" id="payIdc" value="" />
			<s:hidden name="txnId" id="txnIdc" value="" />
			<s:hidden name="token" value="%{#session.customToken}" />
		</s:form>
		<s:form name="refundDetails" action="refundConfirmAction">
			<s:hidden name="orderId" id="orderIdr" value="" />
			<s:hidden name="payId" id="payIdr" value="" />
			<s:hidden name="transactionId" id="txnIdr" value="" />
			<s:hidden name="token" value="%{#session.customToken}" />
		</s:form>
	</div>


	<script type="text/javascript">
	            function getMoptypes(){
                    var paymentType = document.getElementById("selectBox3").title;
                    console.log(paymentType);
                    var merchantemail = document.getElementById("merchant").value;
                    $.ajax({
                        type : "GET",
                        url : "GetMoptype",
                        timeout : 0,
                        data : {
                            "merchantemail":merchantemail,
                            "payment":paymentType,
                            "struts.token.name": "token",
                        },
                        success : function(data) {
                            console.log(data.moplist);
                            var mopList = data.moplist;
                            var html = '';
                            for(var i=0;i<mopList.length;i++){
                                console.log(mopList[i]);
                                html+=	"<option value='"+ mopList[i].name +"'>" + mopList[i].name+ "</option>";
                            }
                            console.log(html);
                            $("#mopType").html(html);
                            $("#mopType").select2({
                                placeholder: "ALL"
                            });
                        }
                    });
                }
				$(document).ready(function () {
					getMoptypes();

					 <s:if
                        test="%{#session.USER.UserType.name()=='SMA'}">
							console.log($("#smaId").val());
                            getSMAMerchant($("#smaId").val());
                    </s:if>
                    <s:if
                        test="%{#session.USER.UserType.name()=='MA'}">
							getMAMerchant($("#maId").val());
					</s:if>


					$(document).click(function () {
						expanded = false;
						//$('#checkboxes2').hide();
						$('#checkboxes3').hide();
						$('#checkboxes4').hide();
						$('#checkboxes5').hide();
					});
					/*$('#checkboxes2').click(function (e) {
						e.stopPropagation();
					});
					*/
					$('#checkboxes3').click(function (e) {
						e.stopPropagation();
					});
					$('#checkboxes4').click(function (e) {
						e.stopPropagation();
					});
					$('#checkboxes5').click(function (e) {
						e.stopPropagation();
					});
					$('#closeBtn').click(function () {
						$('#popup').hide();
					});
					$('#closeBtn1').click(function () {
						$('#popup').hide();
					});

				});

				// < !--------Validation to remove space on paste in ORDER Id
				$(document).on('paste', '#orderId', function (e) {
					e.preventDefault();
					// prevent copying action
					var withoutSpaces = e.originalEvent.clipboardData.getData('Text');
					//withoutSpaces = withoutSpaces.replace(/\s+/g, '');
					withoutSpaces = withoutSpaces.trim();
					$(this).val(withoutSpaces);
				});
			</script>

	<!------------Validation to check Date Format on Type------->
	<script>

				var expanded = false;
				function showCheckboxes(e, uid) {
					var checkboxes = document.getElementById("checkboxes" + uid);
					if (!expanded) {
						expanded = true;
						if (uid == 1) {
							document.getElementById("checkboxes1").style.display = "block";
							//document.getElementById("checkboxes2").style.display = "none";
							document.getElementById("checkboxes3").style.display = "none";
							document.getElementById("checkboxes4").style.display = "none";
							document.getElementById("checkboxes5").style.display = "none";
						}
						if (uid == 2) {
							document.getElementById("checkboxes1").style.display = "none";
							//document.getElementById("checkboxes2").style.display = "block";
							document.getElementById("checkboxes3").style.display = "none";
							document.getElementById("checkboxes4").style.display = "none";
							document.getElementById("checkboxes5").style.display = "none";
						}
						if (uid == 3) {
							document.getElementById("checkboxes3").style.display = "block";
							document.getElementById("checkboxes1").style.display = "none";
							//document.getElementById("checkboxes2").style.display = "none";
							document.getElementById("checkboxes4").style.display = "none";
							document.getElementById("checkboxes5").style.display = "none";
						}
						if (uid == 4) {
							document.getElementById("checkboxes4").style.display = "block";
							document.getElementById("checkboxes3").style.display = "none";
							document.getElementById("checkboxes1").style.display = "none";
							//document.getElementById("checkboxes2").style.display = "none";
							document.getElementById("checkboxes5").style.display = "none";
						}
						if (uid == 5) {
							document.getElementById("checkboxes4").style.display = "none";
							document.getElementById("checkboxes3").style.display = "none";
							document.getElementById("checkboxes1").style.display = "none";
							//document.getElementById("checkboxes2").style.display = "none";
							document.getElementById("checkboxes5").style.display = "block";
						}
					} else {
						checkboxes.style.display = "none";
						expanded = false;
					}
					e.stopPropagation();

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

				var currentDate;

				function checkFromDate() {
					var dateRegex = /^(0?[1-9]|[12][0-9]|3[01])[\/\-](0?[1-9]|1[012])[\/\-]\d{4}$/;

					var fromDateVal = document.getElementById("dateFrom").value;
					if (fromDateVal.trim() != "" && fromDateVal.trim() != null) {
						if (!fromDateVal.match(dateRegex)) {
							document.getElementById("dateError").style.display = "block";
							document.getElementById("submit").disabled = true;
							return false;
						} else {
							document.getElementById("dateError").style.display = "none";
							document.getElementById("submit").disabled = false;
							return true;
						}
					} else {
						document.getElementById("dateError").style.display = "none";
						document.getElementById("submit").disabled = false;
						return true;
					}
				}

				function createDate(strDate) {
					var arrDate = strDate.split("-");
					return arrDate[1] + "-" + arrDate[0] + "-" + arrDate[2];
				}
				function compareDate() {
					var firstDate = new Date(createDate(document.getElementById("dateFrom").value));
					var toDate = new Date(createDate(document.getElementById("dateTo").value));
					currentDate = new Date();
					if (checkFromDate() && checkToDate()) {
						if (firstDate > currentDate) {
							document.getElementById("showErr1").style.display = "block";
							document.getElementById("submit").disabled = true;
							return false;
						} else {
							document.getElementById("showErr1").style.display = "none";
							document.getElementById("submit").disabled = false;
							return true;
						}
					}
					if (checkFromDate() && checkToDate()) {
						if (toDate > currentDate) {
							document.getElementById("showErr2").style.display = "block";
							document.getElementById("submit").disabled = true;
							return false;
						}
						else {
							document.getElementById("showErr2").style.display = "none";
							document.getElementById("submit").disabled = false;
							return true;
						}
					}
				}

				function checkToDate() {
					var dateRegex = /^(0?[1-9]|[12][0-9]|3[01])[\/\-](0?[1-9]|1[012])[\/\-]\d{4}$/;

					var toDateVal = document.getElementById("dateTo").value;
					if (toDateVal.trim() != "" && toDateVal.trim() != null) {
						if (!toDateVal.match(dateRegex)) {
							document.getElementById("dateError1").style.display = "block";
							document.getElementById("submit").disabled = true;
							return false;
						} else {
							document.getElementById("dateError1").style.display = "none";
							document.getElementById("submit").disabled = false;
							return true;
						}
					} else {
						document.getElementById("dateError1").style.display = "none";
						document.getElementById("submit").disabled = false;
						return true;
					}
				}



			</script>
	<script src="../assets/plugins/global/plugins.bundle.js"></script>
	<script src="../assets/js/scripts.bundle.js"></script>
	<!--end::Global Javascript Bundle-->
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
	<!--end::Custom Javascript-->
	<!--end::Javascript-->

	<script>
				"use strict";
				var KTCareersApply = function () {
					var t, e, i;
					return {
						init: function () {
							i = document.querySelector("#captured_form"),
								t = document.getElementById("submit"),
								e = FormValidation.formValidation(i, {
									fields: {
										pgRefNum: {
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
										orderId: {
											validators: {
												//notEmpty: {
												// 		message: "Name is required"
												//	},
												stringLength: {
													max: 30,
													message: 'Order Id should be less than 30 characters.',
													btnDisable: true
												},
												callback: {
													callback: function (input) {
														if (input.value.match(/[!\@\^\&\/\\#,\|+()$~%.'":*?<>{}]/)) {
															return {
																valid: false,
																message: 'Special characters not allowed.',
																btnDisable: true
															};
														} else {
															return {
																valid: true,
																btnDisable: false
															}
														}
													}
												}
											}
										},
										customerEmail: {
											validators: {
												//notEmpty: {
												// 		message: "Name is required"
												//	},
												stringLength: {
													max: 60,
													message: 'email Id should be less than 60 characters.',
													btnDisable: true
												},
												callback: {
													callback: function (input) {
														if (input.value.length == 0) {
															document.getElementsByClassName("invalid-feedback")[2].style.display = 'none';
														} else {
															document.getElementsByClassName("invalid-feedback")[2].style.display = 'block';

															if (!input.value.match(/^([A-Za-z0-9_\-\.\+])+\@([A-Za-z0-9_\-\+])+\.([A-Za-z]{2,4})$/)) {
																return {
																	valid: false,
																	message: 'Please enter a valid email.',
																	btnDisable: true
																};
															}
															else {
																return {
																	valid: true,
																	btnDisable: false
																}
															}

														}

													}
												}
											}
										},

										custPhone: {
											validators: {
												// notEmpty: {
												// 	message: "Phone Number is required"
												//},

												stringLength: {
													max: 10,
													min: 10,
													message: 'Please enter valid phone number.',
													btnDisable: true
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
								t.addEventListener("click", (function (i) {
									i.preventDefault(),
										e && e.validate().then((function (e) {
											if (e == 'Invalid') {
											} else {
												var transFrom1 = new Date(Date.parse(document.getElementById("dateFrom").value));
												var transTo1 = new Date(Date.parse(document.getElementById("dateTo").value));
												if (transFrom1 == null || transTo1 == null) {
													alert('Enter date value');
													return false;
												}

												if (transFrom1 > transTo1) {
													alert('From date must be before the to date');
													$('#loader-wrapper').hide();
													$('#dateFrom').focus();
													return false;
												}
												if (transTo1 - transFrom1 > 180 * 86400000) {
													alert('No. of days can not be more than 180');
													$('#loader-wrapper').hide();
													$('#dateFrom').focus();
													return false;
												}
												else {
													reloadTable();
													setTimeout(totalAmountofAlltxns, 1000);
												}

												// 						reloadTable();
												// setTimeout(totalAmountofAlltxns, 1000);
												// 						totalAmountofAlltxns();
												//	renderTable();
											}
										}))
								}))
						}
					}
				}();
				KTUtil.onDOMContentLoaded((function () {
					KTCareersApply.init()
				}));
			</script>
	<!-- Ended -->
	<script type="text/javascript">
				$('a.toggle-vis').on('click', function (e) {
					e.preventDefault();
					var needToShowAcqFields = $("#needToShowAcqFieldsInReport").val();
					var userType = $("#userTypeName").val();
					table = needToShowAcqFields == 'true' || userType != 'SUBADMIN'
						? $('#txnResultDataTable').DataTable()
						: $('#txnResultDataTableWithoutAcqFields').DataTable();
					// Get the column API object
					var column1 = table.column($(this).attr('data-column'));
					// Toggle the visibility
					column1.visible(!column1.visible());
					if ($(this)[0].classList[1] == 'activecustom') {
						$(this).removeClass('activecustom');
					}
					else {
						$(this).addClass('activecustom');
					}
				});

			</script>
	<script type="text/javascript" src="../js/bootstrap-clockpicker.js"></script>
	<link rel="stylesheet" href="../css/bootstrap-clockpicker.css">
	<script type="text/javascript">

				$("#columnName").change(function () {
					logicalOperatorDisplay();
				});

				$("#columnName1").change(function () {
					logicalOperatorDisplay1();
				});

				$("#columnName2").change(function () {
					logicalOperatorDisplay2();
				});

				$('.clockpicker').clockpicker({
					placement: 'top',
					donetext: 'Done',
					twelvehour: true
				});
				function displayNone() {
					$('#checkboxes1').css('display', 'none');
				}
			</script>

	<script type="text/javascript">

				function logicalOperatorDisplay() {
					var columnName = document.getElementById("columnName").value;

					debugger
					if (columnName == "") {

						document.getElementById("logicalCondition").innerHTML = '<option value="">Select logical Operator</option>';
						document.getElementById("logicalCondition").disabled = true;
						document.getElementById("searchText").disabled = true;
						return false;
					} else {
						document.getElementById("logicalCondition").disabled = false;
						document.getElementById("searchText").disabled = false;
					}
					if (columnName == "PG_REF_NUM" || columnName == "TXN_ID" || columnName == "REFUND_ORDER_ID" || columnName == "REFUND_FLAG" || columnName == "ACQ_ID" ||
						columnName == "CARD_MASK" || columnName == "SETTLEMENT_FLAG" || columnName == "UDF4" || columnName == "UDF5" || columnName == "UDF6" || columnName == "CARD_HOLDER_TYPE"|| columnName == "ORDER_ID"|| columnName == "CUST_EMAIL") {
						forTextOperator();
					}
				};



				function logicalOperatorDisplay1() {
					var columnName = document.getElementById("columnName1").value;

					debugger
					if (columnName == "") {

						document.getElementById("logicalCondition1").innerHTML = '<option value="">Select logical Operator</option>';
						document.getElementById("logicalCondition1").disabled = true;
						document.getElementById("searchText1").disabled = true;
						return false;
					} else {
						document.getElementById("logicalCondition1").disabled = false;
						document.getElementById("searchText1").disabled = false;
					}
					if (columnName == "PG_REF_NUM" || columnName == "TXN_ID" || columnName == "REFUND_ORDER_ID" || columnName == "REFUND_FLAG" || columnName == "ACQ_ID" ||
						columnName == "CARD_MASK" || columnName == "SETTLEMENT_FLAG" || columnName == "UDF4" || columnName == "UDF5" || columnName == "UDF6" || columnName == "CARD_HOLDER_TYPE" || columnName == "ORDER_ID"|| columnName == "CUST_EMAIL") {
						forTextOperator1();
					}
				};


				function logicalOperatorDisplay2() {
					var columnName = document.getElementById("columnName2").value;

					debugger
					if (columnName == "") {

						document.getElementById("logicalCondition2").innerHTML = '<option value="">Select logical Operator</option>';
						document.getElementById("logicalCondition2").disabled = true;
						document.getElementById("searchText2").disabled = true;
						return false;
					} else {
						document.getElementById("logicalCondition2").disabled = false;
						document.getElementById("searchText2").disabled = false;
					}
					if (columnName == "PG_REF_NUM" || columnName == "TXN_ID" || columnName == "REFUND_ORDER_ID" || columnName == "REFUND_FLAG" || columnName == "ACQ_ID" ||
						columnName == "CARD_MASK" || columnName == "SETTLEMENT_FLAG" || columnName == "UDF4" || columnName == "UDF5" || columnName == "UDF6" || columnName == "CARD_HOLDER_TYPE" || columnName == "ORDER_ID"|| columnName == "CUST_EMAIL") {
						forTextOperator2();
					}
				};


				function forNumberOperator() {
					document.getElementById("logicalCondition").innerHTML = '<option value="">Select logical Operator</option><option value="$lt">Less than</option><option value="$lte">Less Than or Equal to</option><option value="$gt">Greater Than</option><option value="$gte">Greater Than or Equal to</option>';
				}

				function forTextOperator() {
					document.getElementById("logicalCondition").innerHTML = '<option value="">Select logical Operator</option><option value="$eq">Equals to</option><option value="$ne">Not Equals to</option>';
				}

				
				function forTextOperator1() {
					document.getElementById("logicalCondition1").innerHTML = '<option value="">Select logical Operator</option><option value="$eq">Equals to</option><option value="$ne">Not Equals to</option>';
				}

				function forTextOperator2() {
					document.getElementById("logicalCondition2").innerHTML = '<option value="">Select logical Operator</option><option value="$eq">Equals to</option><option value="$ne">Not Equals to</option>';
				}

			</script>

</body>

</html>