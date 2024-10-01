<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
	<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
	<%@ taglib uri="/struts-tags" prefix="s" %>
		<html dir="ltr" lang="en-US">

		<head>
			<title>Transaction History</title>

			<!-- <link href="../css/default.css" rel="stylesheet" type="text/css" /> -->

			<link href="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.css" rel="stylesheet"
				type="text/css" />
			<link href="../assets/plugins/custom/datatables/datatables.bundle.css" rel="stylesheet" type="text/css" />
			<link href="../assets/plugins/global/plugins.bundle.css" rel="stylesheet" type="text/css" />
			<link rel="stylesheet" href="../css/transactionResult.css">

			<link href="../assets/css/style.bundle.css" rel="stylesheet" type="text/css" />
			<link href="../assets/css/custom/custom-style.css" rel="stylesheet" type="text/css" />
			<script src="../assets/js/scripts.bundle.js"></script>

			<script src="../js/commanValidate.js"></script>

			<script src="../assets/plugins/global/plugins.bundle.js"></script>
			<script src="../assets/js/scripts.bundle.js"></script>
			<script src="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.js"></script>
			<script src="../assets/plugins/custom/datatables/datatables.bundle.js"></script>
			<script src="../assets/js/widgets.bundle.js"></script>
			<script src="../assets/js/custom/widgets.js"></script>
			<script src="../assets/js/custom/apps/chat/chat.js"></script>
			<script src="../assets/js/custom/utilities/modals/upgrade-plan.js"></script>
			<script src="../assets/js/custom/utilities/modals/create-app.js"></script>
			<script src="../assets/js/custom/utilities/modals/users-search.js"></script>
			<link href="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.css" rel="stylesheet"
				type="text/css" />
			<link href="../assets/plugins/custom/datatables/datatables.bundle.css" rel="stylesheet" type="text/css" />
			<link href="../assets/plugins/global/plugins.bundle.css" rel="stylesheet" type="text/css" />
			<link href="../assets/css/style.bundle.css" rel="stylesheet" type="text/css" />
			<link href="../assets/css/custom/custom-style.css" rel="stylesheet" type="text/css" />
			<script src="../js/loader/main.js"></script>
			<script src="../assets/plugins/global/plugins.bundle.js"></script>
			<script src="../assets/js/scripts.bundle.js"></script>
			<script src="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.js"></script>
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

			<script type="text/javascript">

				function downloadReport() {

					var merchantPayId = document.getElementById("merchant").value;
					var transactionType = document.getElementById("transactionType").value;
					var paymentType = document.getElementById("selectBox3").title;
					var status = document.getElementById("selectBox4").title;
					var currency = document.getElementById("currency").value;
					var dateFrom = convert(document.getElementById("dateFrom").value);
					var dateTo = convert(document.getElementById("dateTo").value);
					var acquirer = '';

					var transactionId = document.getElementById("pgRefNum").value;
					var orderId = document.getElementById("orderId").value;
					var customerEmail = document.getElementById("customerEmail").value;
					var customerPhone = document.getElementById("custPhone").value;
					var mopType = document.getElementById("mopType").value;


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

					dateFrom += " 00:00";
					dateTo += " 23:59";

					document.getElementById("merchantPayIdH").value = merchantPayId;
					document.getElementById("transactionTypeH").value = transactionType;
					document.getElementById("paymentTypeH").value = paymentType;
					document.getElementById("statusH").value = status;
					document.getElementById("currencyH").value = currency;
					document.getElementById("dateFromH").value = dateFrom;
					document.getElementById("dateToH").value = dateTo;
					document.getElementById("acquirerH").value = acquirer;

					document.getElementById("transactionIdH").value = transactionId;
					document.getElementById("orderIdH").value = orderId;
					document.getElementById("customerEmailH").value = customerEmail;
					document.getElementById("customerPhoneH").value = customerPhone;
					document.getElementById("mopTypeH").value = mopType;

					document.getElementById("downloadPaymentReport").submit();
				}


				$(document).ready(function () {

					// Initialize select2
					$(".adminMerchants").select2();
				});



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
						renderTable();
						// 						setTimeout(totalAmountofAlltxns, 1000);
					});

					// $("#submit").click(function(env) {
					// 	reloadTable();
					// 	setTimeout(totalAmountofAlltxns,1000);
					// });

					$(function () {
						//var datepick = $.datepicker;
						var table = $('#txnResultDataTable').DataTable();
						/* $('#txnResultDataTable').on('click', 'td', function () {
							var rowIndex = table.cell(this).index().row;
							var rowData = table.row(rowIndex).data();

							popup(rowData.transactionIdString, rowData.oId, rowData.orderId, rowData.txnType, rowData.pgRefNum);
						}); */
					}

					);
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
					if (transTo1 - transFrom1 > 31 * 86400000) {
						$('#loader-wrapper').hide();
						alert('No. of days can not be more than 31');
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
								pageTotal = api.column(11, {
									page: 'current'
								}).data().reduce(function (a, b) {
									return intVal(a) + intVal(b);
								}, 0);

								// Update footer
								$(api.column(11).footer()).html(
									'' + inrFormat(pageTotal.toFixed(2) + ' ' + ' '));


								// Total over all pages
								total = api.column(12).data().reduce(
									function (a, b) {
										return intVal(a) + intVal(b);
									}, 0);

								// Total over this page
								pageTotal = api.column(12, {
									page: 'current'
								}).data().reduce(function (a, b) {
									return intVal(a) + intVal(b);
								}, 0);

								// Update footer
								$(api.column(12).footer()).html(
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
								'colvis', 'copy', 'csv', 'excel', 'pdf', 'print',
							],
							scrollY: true,
							scrollX: true,
							searchDelay: 500,
							processing: false,
							serverSide: true,
							order: [[5, 'desc']],
							stateSave: true,
							buttons: [
								$.extend(true, {}, buttonCommon, {
									extend: 'copyHtml5',
									exportOptions: {
										columns: [':visible']
									},
								}),
								$.extend(true, {}, buttonCommon, {
									extend: 'csvHtml5',
									title: 'SearchPayment_Transactions_' + (new Date().getFullYear()) + (monthVal > 9 ? monthVal : '0' + monthVal) + (new Date().getDate() > 9 ? new Date().getDate() : '0' + new Date().getDate()) + (new Date().getHours() > 9 ? new Date().getHours() : '0' + new Date().getHours()) + (new Date().getMinutes() > 9 ? new Date().getMinutes() : '0' + new Date().getMinutes()) + (new Date().getSeconds() > 9 ? new Date().getSeconds() : '0' + new Date().getSeconds()),
									exportOptions: {
										columns: [':visible'],
										format: {
											body: function (data, row, column, node) {

												if (row == 11) { // check to remove email special chars
													var newData = data.replace("&#x40;", "@");

													return newData;
												}

												return data;

											}
										}

									},
								}),
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
									}
								},
								// Disabled print button.
								/*{extend : 'print',//footer : true,title : 'Search Transactions',exportOptions : {columns : [':visible']}},*/
								{
									extend: 'colvis',
									columns: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14]
								}],

							dom: 'Brtipl',
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
							serverSide: true,
							order: [[5, 'desc']],
							stateSave: true,

							"ajax": {

								"url": "GRSTransactionHistoryList",
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
									"targets": [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14]
								}
							],

							"columns": [
								{
									"data": null,
									'render': function (data, type, full,
										meta) {
										return '<img src="../image/GRS_ICON.png" class="rounded float-left" style="width:40px;height:30px;cursor:grab;" >';
									}
								}, {
									"data": "transactionIdString",
									"className": "",
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
									"className": "orderId text-class displaynone"
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
									"className": "text-class",
									"render": function (data) {
										return inrFormat(data);
									}
								}, {
									"data": "payId",
									"className": "text-class"

								}, {
									"data": "customerEmail",
									"className": "text-class"
								}, {
									"data": "customerPhone",
									"className": "text-class"
								}]

						});


					$(function () {
						//var datepick = $.datepicker;
						var needToShowAcqFields = $("#txnResultDataTable").val();
                        var userType = $("#userTypeName").val();
                        var tableName = "txnResultDataTable"
						var table = $('#' + tableName).DataTable();
						$('#' + tableName).on('click', 'td', function () {
							var rowIndex = table.cell(this).index().row;
							var colIndex=table.cell(this).index().column;
							var rowData = table.row(rowIndex).data();
if(colIndex==0){
	createGRS(rowData.pgRefNum);
}
							});
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
				function createGRS(pgrefnumber) {
					$('#myModal').show();

					const timestamp = new Date().getTime().toString();

					// Generate a random number between 100000 and 999999 (6-digit number)
					const randomNum = Math.floor(Math.random() * 900000) + 100000;

					// Concatenate the timestamp and random number to get an 18-digit unique number
					const uniqueNumber = timestamp + randomNum;


					$("#gdPgrefNumber").val(pgrefnumber);
					$("#grsId").val(uniqueNumber);
					$("#gd").val("");
					$("#documentError").text("");
					$("#documentError1").text("");
					//$("#file").val("");
					$("#grsTittle").val("");
					$("#fileUpload").val("");

				}
				function reloadTable() {
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
						alert('From date must be before the to date');
						$('#loader-wrapper').hide();
						$('#dateFrom').focus();
						return false;
					}
					if (transTo1 - transFrom1 > 31 * 86400000) {
						alert('No. of days can not be more than 31');
						$('#loader-wrapper').hide();
						$('#dateFrom').focus();
						return false;
					}
					else {
						$("#submit").attr("disabled", true);
						var tableObj = $('#txnResultDataTable');
						var table = tableObj.DataTable();
						table.ajax.reload();
					}
				}

				function generatePostData(d) {
					debugger
					var token = document.getElementsByName("token")[0].value;
					var merchantEmailId = document.getElementById("merchant").value;
					var transactionType = document.getElementById("transactionType").value;
					var paymentType = document.getElementById("selectBox3").title;
					var status = document.getElementById("selectBox4").title;
					var currency = document.getElementById("currency").value;
					var mopType = document.getElementById("mopType").value;
					var acquirer = '';

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
					var obj = {
						transactionId: document.getElementById("pgRefNum").value,
						orderId: document.getElementById("orderId").value,
						customerEmail: document.getElementById("customerEmail").value,
						customerPhone: document.getElementById("custPhone").value,
						merchantEmailId: merchantEmailId,
						transactionType: transactionType,
						paymentType: paymentType,
						status: status,
						mopType: mopType,
						acquirer: acquirer,
						currency: currency,
						dateFrom: dateFrom,
						dateTo: dateTo,
						draw: d.draw,
						length: d.length,
						start: d.start,
						token: token,
						"struts.token.name": "token",
					};

					return obj;
				}

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
				.card .card-header .card-title,
				.card .card-header .card-title .card-label {
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

				.card .card-header .card-title1,
				.card .card-header .card-title1 .card-label {
					font-weight: 500;
					font-size: 1.275rem;
				}

				.card .card-header-primary .card-icon,
				.card .card-header-primary:not(.card-header-icon):not(.card-header-text),
				.card .card-header-primary .card-text {
					box-shadow: 0 4px 20px 0px rgba(0, 0, 0, 0.14), 0 7px 10px -5px rgba(156, 39, 176, 0.4);
				}

				.card [class*="card-header-"] .card-icon,
				.card [class*="card-header-"] .card-text {
					border-radius: 3px;
					padding: 15px;
					margin-top: -20px;
					margin-right: 15px;
					float: left;
				}

				.error {
					color: red;
				}

				.success {
					color: green;
				}

				.card .card-header-rose .card-icon,
				.card .card-header-rose:not(.card-header-icon):not(.card-header-text),
				.card .card-header-rose .card-text {
					box-shadow: unset;
				}

				.card .card-header-rose .card-icon,
				.card .card-header-rose .card-text,
				.card .card-header-rose:not(.card-header-icon):not(.card-header-text),
				.card.bg-rose,
				.card.card-rotate.bg-rose .front,
				.card.card-rotate.bg-rose .back {
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
					height: 180px;
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
					background: #202F4B;
				}

				/* Chrome, Safari, Edge, Opera */
				input::-webkit-outer-spin-button,
				input::-webkit-inner-spin-button {
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

				.nav-pills.nav-pills-rose .nav-item .nav-link.active,
				.nav-pills.nav-pills-rose .nav-item .nav-link.active:focus,
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
				
				.displaynone{
					display:none
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



			<div class="content d-flex flex-column flex-column-fluid" id="kt_content">
				<!--begin::Toolbar-->
				<div class="toolbar" id="kt_toolbar">
					<!--begin::Container-->
					<div id="kt_toolbar_container" class="container-fluid d-flex flex-stack">
						<!--begin::Page title-->

						<!--end::Page title-->

					</div>
					<!--end::Container-->
				</div>
				<!--end::Toolbar-->
				<!--begin::Post-->
				<div class="post d-flex flex-column-fluid" id="kt_post">
					<!--begin::Container-->
					<div id="kt_content_container" class="container-xxl">
						<div data-kt-swapper="true" data-kt-swapper-mode="prepend"
							data-kt-swapper-parent="{default: '#kt_content_container', 'lg': '#kt_toolbar_container'}"
							class="page-title d-flex align-items-center me-3 flex-wrap mb-5 mb-lg-0 lh-1">
							<!--begin::Title-->
							<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">Transaction
								History</h1>
							<!--end::Title-->
							<!--begin::Separator-->
							<span class="h-20px border-gray-200 border-start mx-4"></span>
							<!--end::Separator-->
							<!--begin::Breadcrumb-->
							<ul class="breadcrumb breadcrumb-separatorless fw-semibold fs-7 my-1">
								<!--begin::Item-->
								<li class="breadcrumb-item text-muted"><a href="home"
										class="text-muted text-hover-primary">Dashboard</a></li>
								<!--end::Item-->
								<!--begin::Item-->
								<li class="breadcrumb-item"><span class="bullet bg-gray-200 w-5px h-2px"></span></li>
								<!--end::Item-->
								<!--begin::Item-->
								<li class="breadcrumb-item text-muted">Grievance Redressal
									System</li>
								<!--end::Item-->
								<!--begin::Item-->
								<li class="breadcrumb-item"><span class="bullet bg-gray-200 w-5px h-2px"></span></li>
								<!--end::Item-->
								<!--begin::Item-->
								<li class="breadcrumb-item text-dark">Transaction History</li>
								<!--end::Item-->
							</ul>
							<!--end::Breadcrumb-->
						</div>
					</div>
				</div>
				<!-- <form action="" class="form mb-15 fv-plugins-bootstrap5 fv-plugins-framework" method="post" id="search_payment_form"> -->
				<div class="row my-5">
					<div class="col">
						<form action="" class="form mb-15" method="post" id="captured_form">
							<div class="card">
								<div class="card-body">
									<!--begin::Input group-->
									<div class="row g-9 mb-8">
										<!--begin::Col-->
										<div class="col-md-4 fv-row fv-plugins-icon-container">
											<label class="d-flex align-items-center fs-6 fw-semibold mb-2">
												<span class="">PG Ref Number</span>
											</label>
											<!--end::Label-->
											<!-- <input type="text" class="form-control form-control-solid" name="pgrefnumber"> -->

											<!-- <s:textfield id="pgRefNum" class="form-control form-control-solid"
													name="pgRefNum" type="text" value="" autocomplete="off"
													maxlength="16" onblur="validPgRefNum();" ondrop="return false;" onKeyDown="if(event.keyCode === 32)return false;" onpaste="removeSpaces(this);"></s:textfield> -->
											<s:textfield type="number" min="0" class="form-control form-control-solid"
												id="pgRefNum" name="pgRefNum" maxlength="17"
												onkeydown="inputKeydownevent(event,'pgRefNum')"
												oninput="if(value.length>16)value=value.slice(0,16)" />

										</div>
										<div class="col-md-4 fv-row">
											<label class="d-flex align-items-center fs-6 fw-semibold mb-2">
												<span class="">Order ID</span>
											</label>
											<!--end::Label-->
											<!-- <input type="text" class="form-control form-control-solid" name="orderid"> -->
											<!-- <s:textfield id="orderId" class="form-control form-control-solid" name="orderId"
										type="text" value="" autocomplete="off"
										onKeyDown="validateOrderIdvalue(this);" onkeypress="return validateOrderId(event);"  ondrop="return false;" onpaste="validateOrderIdvalue(this);" maxlength="50"></s:textfield> -->
											<s:textfield type="text" class="form-control form-control-solid"
												id="orderId" name="orderId"
												onkeydown="inputKeydownevent(event,'orderId')"
												oninput="if(value.length>30)value=value.slice(0,30)" />

										</div>

										<div class="col-md-4 fv-row fv-plugins-icon-container">
											<label class="d-flex align-items-center fs-6 fw-semibold mb-2">
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
											<label class="d-flex align-items-center fs-6 fw-semibold mb-2">
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
											<label class="d-flex align-items-center fs-6 fw-semibold mb-2">
												<span class="">Currency</span>
											</label>
											<!--end::Label-->
											<!-- <select name="merchant" data-control="select2" data-placeholder="All" class="form-select form-select-solid select2-hidden-accessible" data-hide-search="true" data-select2-id="select2-data-13-06ua" tabindex="-1" aria-hidden="true" data-kt-initialized="1">
													<option value="All" data-select2-id="select2-data-15-wujq">All</option>
												</select><span class="select2 select2-container select2-container--bootstrap5" dir="ltr" data-select2-id="select2-data-14-tuwf" style="width: 100%;"><span class="selection"><span class="select2-selection select2-selection--single form-select form-select-solid" role="combobox" aria-haspopup="true" aria-expanded="false" tabindex="0" aria-disabled="false" aria-labelledby="select2-merchant-da-container" aria-controls="select2-merchant-da-container"><span class="select2-selection__rendered" id="select2-merchant-da-container" role="textbox" aria-readonly="true" title="All">All</span><span class="select2-selection__arrow" role="presentation"><b role="presentation"></b></span></span></span><span class="dropdown-wrapper" aria-hidden="true"></span></span> -->

											<div class="">
												<s:select name="currency" id="currency" headerValue="ALL" headerKey=""
													list="currencyMap" class="form-select form-select-solid" />
											</div>

										</div>
										<div class="col-md-4 fv-row">
											<label class="d-flex align-items-center fs-6 fw-semibold mb-2">
												<span class="">Payment Method</span>
											</label>
											<!--end::Label-->
											<!-- <select class="form-select form-select-sm form-select-solid select2-hidden-accessible" data-control="select2" data-close-on-select="false" data-placeholder="All" data-allow-clear="true" multiple="" data-select2-id="select2-data-4-136v" tabindex="-1" aria-hidden="true" data-kt-initialized="1">
														<option value="All">All</option>
														<option value="CC">Credit Card</option>
														<option value="DC">Debit Card</option>
														<option value="NB">Net Banking</option>
														<option value="WL">Wallet</option>
														<option value="UP">UPI</option>
													</select>
													<span class="select2 select2-container select2-container--bootstrap5" dir="ltr" data-select2-id="select2-data-5-hpxx" style="width: 100%;"><span class="selection"><span class="select2-selection select2-selection--multiple form-select form-select-sm form-select-solid" role="combobox" aria-haspopup="true" aria-expanded="false" tabindex="-1" aria-disabled="false"><ul class="select2-selection__rendered" id="select2-nkvb-container"></ul><span class="select2-search select2-search--inline"><textarea class="select2-search__field" type="search" tabindex="0" autocorrect="off" autocapitalize="none" spellcheck="false" role="searchbox" aria-autocomplete="list" autocomplete="off" aria-label="Search" aria-describedby="select2-nkvb-container" placeholder="All" style="width: 100%;"></textarea></span></span></span><span class="dropdown-wrapper" aria-hidden="true"></span></span> -->

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
														id="paymentMethod" autocomplete="off"
														onchange="getMoptypes()" value="paymentMethod" />
												</div>
											</div>

										</div>
									</div>
									<div class="row g-9 mb-8">
										<div class="col-md-4 fv-row">
											<label class="d-flex align-items-center fs-6 fw-semibold mb-2">
												<span class="">MOP Type</span>
											</label>
											
											<!--end::Label-->
											<!-- <select class="form-select form-select-sm form-select-solid select2-hidden-accessible" data-control="select2" data-close-on-select="false" data-placeholder="All" data-allow-clear="true" multiple="" data-select2-id="select2-data-6-qqf5" tabindex="-1" aria-hidden="true" data-kt-initialized="1">
														<option value="All">All</option>
														<option value="Visa">Visa</option>
														<option value="MasterCard">MasterCard</option>
														<option value="Rupay">Rupay</option>
														<option value="UPI">UPI</option>
														<option value="SBI">State Bank Of India</option>
														<option value="Hdfc">Hdfc Bank</option>
														<option value="Icici"> Icici Bank</option>
														<option value="Kotak">Kotak Bank</option>
														<option value="Yes">Yes Bank</option>
														<option value="Axis">Axis Bank</option>
														<option value="Others">Others</option>
													</select><span class="select2 select2-container select2-container--bootstrap5" dir="ltr" data-select2-id="select2-data-7-1lmc" style="width: 100%;"><span class="selection"><span class="select2-selection select2-selection--multiple form-select form-select-sm form-select-solid" role="combobox" aria-haspopup="true" aria-expanded="false" tabindex="-1" aria-disabled="false"><ul class="select2-selection__rendered" id="select2-dipb-container"></ul><span class="select2-search select2-search--inline"><textarea class="select2-search__field" type="search" tabindex="0" autocorrect="off" autocapitalize="none" spellcheck="false" role="searchbox" aria-autocomplete="list" autocomplete="off" aria-label="Search" aria-describedby="select2-dipb-container" placeholder="All" style="width: 100%;"></textarea></span></span></span><span class="dropdown-wrapper" aria-hidden="true"></span></span> -->



											<select class="moptype-select2 form-control form-control-solid"
												name="mopType" id="mopType" multiple="multiple">
												<option>ALL</option>
											</select>

										</div>
										<div class="col-md-4 fv-row">
											<label class="d-flex align-items-center fs-6 fw-semibold mb-2">
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
												class="form-select form-select-sm form-select-solid" list="txnTypelist"
												listValue="name" listKey="code" name="transactionType"
												id="transactionType" autocomplete="off" value="name" />


										</div>
										<div class="col-md-4 fv-row">
											<label class="d-flex align-items-center fs-6 fw-semibold mb-2">
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
												<div class="" id="selectBox4" onclick="showCheckboxes(event,4)"
													title="ALL">
													<select class="form-select form-select-sm form-select-solid">
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
											<label class="d-flex align-items-center fs-6 fw-semibold mb-2">
												<span class="">Date From</span>
											</label>
											<!--end::Label-->
											<div class="position-relative d-flex align-items-center">
												<!--begin::Icon-->
												<!--begin::Svg Icon | path: icons/duotune/general/gen014.svg-->
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
													type="text" readonly="readonly">
												<!--end::Datepicker-->
											</div>
										</div>
										<!--begin::Col-->
										<div class="col-md-4 fv-row">
											<label class="d-flex align-items-center fs-6 fw-semibold mb-2">
												<span class="">Date To</span>
											</label>
											<!--end::Label-->
											<div class="position-relative d-flex align-items-center">
												<!--begin::Icon-->
												<!--begin::Svg Icon | path: icons/duotune/general/gen014.svg-->
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
													placeholder="Select a date" name="dateTo" id="dateTo" type="text"
													readonly="readonly">
												<!--end::Datepicker-->
											</div>
										</div>
										<div class="col-md-4 fv-row">

												<label class="d-flex align-items-center fs-6 fw-semibold mb-2">
													<span class="">Merchant</span>
												</label>

											<!--end::Label-->
											<!-- <select name="merchant" data-control="select2" data-placeholder="Ravi Kiran" class="form-select form-select-solid select2-hidden-accessible" data-select2-id="select2-data-1-6qur" tabindex="-1" aria-hidden="true" data-kt-initialized="1">
														<option value="Ravi Kiran" data-select2-id="select2-data-3-vvxv">Ravi Kiran</option>
													</select> -->

											<s:if
												test="%{#session.USER.UserType.name()=='ADMIN' || #session.USER.UserType.name()=='SUBADMIN' || #session.USER_TYPE.name()=='SUPERADMIN'|| #session.USER_TYPE.name()=='RESELLER'}">
												<s:select name="merchant"
													class="form-select form-select-solid adminMerchants" id="merchant"
													headerKey="ALL" headerValue="ALL" list="merchantList"
													listKey="emailId" listValue="businessName" autocomplete="off" />

											</s:if>
											<s:else>
												<s:select name="merchant" class="form-select form-select-solid"
													id="merchant" list="merchantList" listKey="emailId"
													listValue="businessName" autocomplete="off" />
											</s:else>

											<!-- <span class="select2 select2-container select2-container--bootstrap5" dir="ltr" data-select2-id="select2-data-2-38m7" style="width: 100%;"><span class="selection"><span class="select2-selection select2-selection--single form-select form-select-solid" role="combobox" aria-haspopup="true" aria-expanded="false" tabindex="0" aria-disabled="false" aria-labelledby="select2-merchant-ua-container" aria-controls="select2-merchant-ua-container"><span class="select2-selection__rendered" id="select2-merchant-ua-container" role="textbox" aria-readonly="true" title="Ravi Kiran">Ravi Kiran</span><span class="select2-selection__arrow" role="presentation"><b role="presentation"></b></span></span></span><span class="dropdown-wrapper" aria-hidden="true"></span></span> -->
										</div>
									</div>
									<div class="row g-9 mb-8 justify-content-md-end">

										<div
											class="col-md-8 fv-row d-flex justify-content-center align-items-end justify-content-md-end pb-2 g-9">
											<input type="button" id="submit" class="btn mx-5 btn-secondary"
												value="VIEW">

										</div>
									</div>
								</div>
							</div>
						</form>
					</div>
				</div>

				<div class="row my-5 mt-4">
					<div class="col">
						<div class="card">
							<div class="card-body">
								<!--begin::Input group-->
<!-- 								<div class="row g-9 mb-8 justify-content-end"> -->
<!-- 									<div class="col-lg-4 col-sm-12 col-md-6"> -->
<%-- 										<select name="currency" data-control="select2" data-placeholder="Actions" --%>
<%-- 											id="actions11" class="form-select form-select-solid actions" --%>
<%-- 											data-hide-search="true" onchange="myFunction();"> --%>
<!-- 											<option value="">Actions</option> -->
<!-- 											<option value="copy">Copy</option> -->
<!-- 											<option value="csv">CSV</option> -->
<!-- 											<option value="pdf">PDF</option> -->
<%-- 										</select> --%>
<!-- 									</div> -->
<!-- 									<div class="col-lg-4 col-sm-12 col-md-6"> -->
<!-- 										<div class="dropdown1"> -->
<!-- 											<button class="form-select form-select-solid actions dropbtn1">Customize -->
<!-- 												Columns</button> -->
<!-- 											<div class="dropdown-content1"> -->
<!-- 												<a class="" data-column="0">Txn Id</a> <a class="toggle-vis" -->
<!-- 													data-column="1">Pg ref no</a> <a class="toggle-vis" -->
<!-- 													data-column="2">Merchant</a> <a class="toggle-vis" -->
<!-- 													data-column="3">Date</a> <a class="toggle-vis" data-column="4">Order -->
<!-- 													Id</a> <a class="toggle-vis" data-column="5">Refund Order Id</a> <a -->
<!-- 													class="toggle-vis" data-column="6">Mop Type</a> <a -->
<!-- 													class="toggle-vis" data-column="7">Payment Method</a> <a -->
<!-- 													class="toggle-vis" data-column="8">Txn Type</a> <a -->
<!-- 													class="toggle-vis" data-column="9">Status</a> <a class="toggle-vis" -->
<!-- 													data-column="10">Base Amount</a> <a class="toggle-vis" -->
<!-- 													data-column="11">Total Amount</a> <a class="toggle-vis" -->
<!-- 													data-column="12">Pay Id</a> <a class="toggle-vis" -->
<!-- 													data-column="13">Customer Email</a> <a class="toggle-vis" -->
<!-- 													data-column="14">Customer Ph Number</a> <a class="toggle-vis" -->
<!-- 													data-column="15">Udf 4</a> -->
<!-- 											</div> -->
<!-- 										</div> -->
<!-- 									</div> -->
<!-- 								</div> -->
								<div class="row g-9 mb-8">
									<div class="table-responsive">
										<table id="txnResultDataTable"
											class="table table-striped table-row-bordered gy-5 gs-7 dataTable no-footer">
											<thead>
												<tr class="fw-bold fs-6 text-gray-800">
													<th>Create Grievance</th>
													<th class="min-w-90px">Txn Id</th>
													<th class="min-w-90px">Pg Ref Num</th>
													<th class="min-w-90px">Merchant</th>
													<th class="min-w-90px">Date</th>
													<th class="min-w-90px">Order Id</th>
													<th class="min-w-90px displaynone">Refund Order Id</th>
													<th class="min-w-90px">Mop Type</th>
													<th class="min-w-90px">Payment Method</th>
													<th class="min-w-90px">Txn Type</th>
													<th class="min-w-90px">Status</th>
													<th class="min-w-90px">Base Amount</th>
													<th class="min-w-90px">Total Amount</th>
													<th class="min-w-90px">Pay ID</th>
													<th class="min-w-90px">Customer Email</th>
													<th class="min-w-90px">Customer Ph Number</th>



												</tr>
											</thead>
											<tfoot>
												<tr class="fw-bold fs-6 text-gray-800">
													<th></th>
													<!-- <th class="min-w-90px"></th> -->
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
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>




			</div>


			<script type="text/javascript">
				function getMoptypes() {
					var paymentType = document.getElementById("selectBox3").title;
					console.log(paymentType);
					var merchantemail = document.getElementById("merchant").value;
					if (paymentType == "" || paymentType == null) {
						paymentType = "ALL";
					}
					$.ajax({
						type: "GET",
						url: "GetMoptype",
						timeout: 0,
						data: {
							"merchantemail": merchantemail,
							"payment": paymentType,
							"struts.token.name": "token",
						},
						success: function (data) {
							console.log(data.moplist);
							var mopList = data.moplist;
							var html = '';
							for (var i = 0; i < mopList.length; i++) {
								console.log(mopList[i]);
								html += "<option value='" + mopList[i].name + "'>" + mopList[i].name + "</option>";
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

					$(document).click(function () {
						expanded = false;
						//$('#checkboxes2').hide();
						$('#checkboxes3').hide();
						$('#checkboxes4').hide();
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
						if (uid == 2) {
							//document.getElementById("checkboxes2").style.display = "block";
							document.getElementById("checkboxes3").style.display = "none";
							document.getElementById("checkboxes4").style.display = "none";
						}
						if (uid == 3) {
							document.getElementById("checkboxes3").style.display = "block";
							//document.getElementById("checkboxes2").style.display = "none";
							document.getElementById("checkboxes4").style.display = "none";
						}
						if (uid == 4) {
							document.getElementById("checkboxes4").style.display = "block";
							document.getElementById("checkboxes3").style.display = "none";
							//document.getElementById("checkboxes2").style.display = "none";
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

			<script src="../assets/plugins/custom/datatables/datatables.bundle.js"></script>


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
														if (input.value.match(/[!\@\^\_\&\/\\#,\|+()$~%.'":*?<>{}]/)) {
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
												if (transTo1 - transFrom1 > 31 * 86400000) {
													alert('No. of days can not be more than 31');
													$('#loader-wrapper').hide();
													$('#dateFrom').focus();
													return false;
												}
												else {
													reloadTable();
													//setTimeout(totalAmountofAlltxns, 1000);
												}

												// 						reloadTable();

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
				var baseUrl = 'https://chat.pay10.com/';
				$('a.toggle-vis').on('click', function (e) {
					debugger
					e.preventDefault();
					table = $('#txnResultDataTable').DataTable();
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
			<div class="modal" tabindex="-1" role="dialog" id="myModal" style="top: 20vh;">
				<div class="modal-dialog modal-lg" role="document">
					<div class="modal-content">
						<div class="modal-header">
							<h5 class="modal-title">Create Grievance</h5>
							<button type="button" class="close" data-dismiss="modal" aria-label="Close"
								onclick="closeModal()">
								<span aria-hidden="true">&times;</span>
							</button>
						</div>
						<div class="modal-body">
							<label class="d-flex align-items-center fs-6 fw-semibold mb-2">
								<span class="">Title</span>
							</label> <input type="text" id="grsTittle" name="grsTittle" style="min-width: 100%;"> <label
								class="d-flex align-items-center fs-6 fw-semibold mb-2" style="margin-top: 10px;"> <span
									class="">Grievance
									Description</span>
							</label>
							<textarea name="gd" id="gd" style="min-width: 100%; height: 80px !important;"
								maxlength="500"></textarea>
								<label class="d-flex align-items-center fs-6 fw-semibold mb-2" style="margin-top: 10px;">
								<span class="">Upload
									Reference File</span>
							</label> <input type="file" id="fileUpload" name="fileUpload" style="min-width: 100%;"
								accept=".jpeg, .png, .pdf"> <input type="hidden" id="grsId" name="grsId"
								style="width: 87%; text-align: center;" disabled> <span id="documentError"
								class="error"></span> <span id="documentError1" class="success"></span>
							<input type="hidden" id="gdPgrefNumber" name="gdPgrefNumber">
							<input type="hidden" id="grsId" name="grsId" style="width: 87%; text-align: center;"
								disabled>

							<!-- <input type="file" name="file" id="file"
								accept="image/gif, image/jpeg,image/png,application/pdf,application/msword,application/vnd.openxmlformats-officedocument.wordprocessingml.document,application/vnd.ms-excel"
								class="hide_accept_chargeback"> -->
							<div>
								<!-- <span id="documentError" class="error"></span> <span id="documentError1"
									class="success"></span> -->
							</div>
						</div>
						<div class="modal-footer">
							<button type="button" class="btn btn-primary" style="margin-right: 10px;" id="grsSumbit"
								onclick="submitGRS()">Submit</button>
							<button type="button" class="btn btn-secondary" data-dismiss="modal"
								onclick="closeModal()">Close</button>
						</div>
					</div>
				</div>
			</div>
			<script>
				function closeModal() {
					$("#myModal").hide();
				}

				function getBase64(file) {
					return new Promise((resolve, reject) => {
						const reader = new FileReader();
						reader.onloadend = () => {
							resolve(reader.result.split(',')[1]); // Get Base64 part only
						};
						reader.onerror = reject;
						reader.readAsDataURL(file); // Convert to Data URL
					});
				}

			async function submitGRS() {
					var grsDesc = $("#gd").val();
                    var grsTittle = $("#grsTittle").val();
                    var pgRefNumber = $("#gdPgrefNumber").val();
                    var grdID = $("#grsId").val();

                    if(grsTittle == "" || grsTittle == null || grsTittle == undefined){
                    	alert("Grievance Title is Mandatory");
                    	return false;
                    }

                    if(grsDesc == "" || grsDesc == null || grsDesc == undefined){
                    	alert("Grievance Description is Mandatory");
                        return false;
                    }


					var urls = new URL(window.location.href);
					var domain = urls.origin;
					var userEmailId = "<s:property value='%{#session.USER.emailId}'/>";
					
					var fileData = "";
					var fileName = "";
					var fileInput = $("#fileUpload")[0];
					if (fileInput.files.length > 0) {
						const file = fileInput.files[0];
						fileData = await getBase64(file);
						fileName = file.name; // Include file name
					}
					
					var obj = {
						"grievanceRedressalSystemDescription": grsDesc,
						"grievanceRedressalSystemTittle": grsTittle,
						"grievanceRedressalSystemPgrefNumber": pgRefNumber,
						"grievanceRedressalSystemId": grdID,
						"userEmailId": userEmailId,
						"file": fileData,
						"filename": fileName
					};


					$.ajax({
						url: domain + "/crmws/GRS/SaveGrievance",
						//url:"http://localhost:8080/GRS/SaveGrievance",
						type: 'POST',
						data: JSON.stringify(obj),
						contentType: "application/json",
						success: function (data) {
							if (data.respmessage != "Grievance Successfully Created") {
								$("#documentError").text(data.respmessage);
								$("#grsSumbit").prop("disabled", false);
								alert(data.respmessage);
							} else {
								$("#documentError1").text(data.respmessage);


								// const file = document.getElementById("file").files[0];
								// var reader = new FileReader();
								// reader.readAsDataURL(file);
								// reader.onload = function () {
								// 	var fileData = reader.result  .replace('data:', '').replace(/^.+,/, '');;
								// 	var filename = file.name;
								// 	var contentType = file.type;

									// Convert the fileData to a base64-encoded string
									// var base64Data = fileData;

									// Send the base64-encoded data along with filename and contentType

									// $.ajax({
									// 	url: baseUrl + 'api/document/upload/', // Replace with your Java backend endpoint
									// 	type: 'POST',
									// 	contentType: 'application/json',
									// 	data: JSON.stringify({
									// 		filename: filename,
									// 		contentType: contentType,
									// 		senderType: "<s:property value='%{#session.USER.UserGroup.group}'/>",
									// 		sender: "<s:property value='%{#session.USER.emailId}'/>",
									// 		data: base64Data,
									// 		roomId: grdID
									// 	}),
									// 	success: function (response) {
									// 		alert("File Uploaded Successfully");
									// 		closeModal();
									// 	},
									// 	error: function (error) {
									// 		alert("File  Not Uploaded Successfully : " + error.responseText);
									// 	}
									// });
								// };
								alert(data.respmessage);
								$("#grsSumbit").prop("disabled", false);
								closeModal();
                                location.reload();
							}
						},
						error: function (data, textStatus, jqXHR) {
							$("#documentError").text(data.respmessage);
							$("#grsSumbit").prop("disabled", false);

						}
					});

				}
				window.onload = function() {
				    // Hide the checkbox with value "Request Accepted"
				    $('#checkboxes4 input[name="status"][value="Request Accepted"]').hide();

				    // Hide the label associated with the checkbox
				    $('#checkboxes4 input[name="status"][value="Request Accepted"]').each(function() {
				        $(this).nextAll('label[for="' + this.id + '"]').hide();
				    });
				};

			</script>
		</body>

		</html>