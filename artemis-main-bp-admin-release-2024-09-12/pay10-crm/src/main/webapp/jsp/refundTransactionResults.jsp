<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
	<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
	<%@ taglib uri="/struts-tags" prefix="s" %>
		<html dir="ltr" lang="en-US">

		<head>
			<style>
				/* setFontSizeGrid{
		   font-size: 2px !important;
	   
	   
	   
	   } */
				.table {
					font-size: 11px !important;
				}

				.dt-buttons.btn-group.flex-wrap {
					display: none !important;
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
			</style>
			<title>Refund Captured</title>
			<%-- <link href="../css/default.css" rel="stylesheet" type="text/css" />
			<link rel="stylesheet" type="text/css" media="all" href="../css/daterangepicker-bs3.css" />
			<link href="../css/Jquerydatatableview.css" rel="stylesheet" />
			<link href="../css/jquery-ui.css" rel="stylesheet" />
			<link href="../css/Jquerydatatable.css" rel="stylesheet" />
			<script src="../js/jquery.min.js" type="text/javascript"></script>
			<script src="../js/core/popper.min.js"></script>
			<script src="../js/core/bootstrap-material-design.min.js"></script>
			<script src="../js/plugins/perfect-scrollbar.jquery.min.js"></script>
			<!-- Control Center for Material Dashboard: parallax effects, scripts for the example pages etc -->
			<script src="../js/material-dashboard.js?v=2.1.0" type="text/javascript"></script>
			<script src="../js/moment.js" type="text/javascript"></script>
			<script src="../js/daterangepicker.js" type="text/javascript"></script>
			<script src="../js/jquery.dataTables.js"></script>
			<script src="../js/jquery-ui.js"></script>
			<script src="../js/commanValidate.js"></script>
			<script src="../js/jquery.popupoverlay.js"></script>
			<script src="../js/dataTables.buttons.js" type="text/javascript"></script>
			<script src="../js/pdfmake.js" type="text/javascript"></script>

			<script src="../js/jszip.min.js" type="text/javascript"></script>
			<script src="../js/vfs_fonts.js" type="text/javascript"></script>
			<script src="../js/buttons.colVis.min.js" type="text/javascript"></script>

			<link href="../css/select2.min.css" rel="stylesheet" />
			<script src="../js/jquery.select2.js" type="text/javascript"></script>
			<!--  loader scripts -->
			<script src="../js/loader/modernizr-2.6.2.min.js"></script>
			<script src="../js/loader/main.js"></script>
			<link rel="stylesheet" href="../css/loader/normalize.css" />
			<link rel="stylesheet" href="../css/loader/main.css" />
			<link rel="stylesheet" href="../css/loader/customLoader.css" /> --%>

			<!-- Added By Sweety -->
			<meta charset="utf-8" />
			<meta name="viewport" content="width=device-width, initial-scale=1" />
			<link rel="shortcut icon" href="assets/media/images/paylogo.svg" />
			<!--begin::Fonts-->
			<link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Inter:300,400,500,600,700" />
			<!--end::Fonts-->
			<!--begin::Vendor Stylesheets(used by this page)-->
			<link href="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.css" rel="stylesheet"
				type="text/css" />
			<link href="../assets/plugins/custom/datatables/datatables.bundle.css" rel="stylesheet" type="text/css" />
			<!--end::Vendor Stylesheets-->
			<!--begin::Global Stylesheets Bundle(used by all pages)-->
			<link href="../assets/plugins/global/plugins.bundle.css" rel="stylesheet" type="text/css" />
			<link href="../assets/css/style.bundle.css" rel="stylesheet" type="text/css" />
			<link href="../assets/css/custom/custom-style.css" rel="stylesheet" type="text/css" />

			<script src="../assets/plugins/global/plugins.bundle.js"></script>
			<script src="../assets/js/scripts.bundle.js"></script>

			<!-- End -->

			<script type="text/javascript">

				function getMopType(value, id) {
					var merchantemail = document.getElementById("merchant").value;
					var paytype = value;

					$.ajax({
						type: "GET",
						url: "GetMoptype",
						timeout: 0,
						data: {
							"merchantemail": merchantemail,
							"payment": paytype,
							"struts.token.name": "token",
						},
						success: function (data) {
							debugger
							var mopresult = [];

							mopresult = data.moplist;
							/* var mopdiv ="";
							   for(var i = 0; i < mopresult.length; i++){

								 mopdiv=mopdiv+" <option value='"+mopresult[i].uiName+"'>"+mopresult[i].name+"</option>"


							   } */
							$('#' + id).html("");
							const countriesDropDown = document.getElementById(id);
							let option = document.createElement("option");
							option.setAttribute('value', "ALL");

							let optionText = document.createTextNode("ALL");
							option.appendChild(optionText);

							countriesDropDown.appendChild(option);
							for (let key in mopresult) {
								let option = document.createElement("option");
								option.setAttribute('value', data.moplist[key].code);

								let optionText = document
									.createTextNode(data.moplist[key].name);
								option.appendChild(optionText);

								countriesDropDown.appendChild(option);
							}

							// document.getElementById("getid").innerHTML=mopdiv;

							// const select = document.querySelector('select');
							// select.options.add(new Option("+mopresult[i].name+", "+mopresult[i].uiName+"))

						}
					});
				}

				function handleChange() {
					debugger
					var transFrom = document.getElementById('kt_datepicker_1').value;
					var transTo = document.getElementById('kt_datepicker_2').value;
					var dateFrom = new Date(Date.parse(transFrom));
					var dateTo = new Date(Date.parse(transTo));
					if (dateFrom == null || dateTo == null) {
						alert('Enter date value');
						return false;
					}
					if (dateFrom > dateTo) {
						alert('From date must be before the to date');
						$('#kt_datepicker_1').focus();
						$("#kt_datepicker_2").flatpickr({
							showOtherMonths: true,
							dateFormat: 'Y-m-d',
							selectOtherMonths: false,
							defaultDate: 'today',
							maxDate: new Date()
						});
						return false;
					}
					if (dateTo - dateFrom > 31 * 86400000) {
						alert('No. of days can not be more than 31');
						$('#kt_datepicker_1').focus();
						$("#kt_datepicker_2").flatpickr({
							showOtherMonths: true,
							dateFormat: 'Y-m-d',
							selectOtherMonths: false,
							defaultDate: 'today',
							maxDate: new Date()
						});
						return false;
					}
					else {
						populateDataTable();
					}
				}
			</script>



			<script type="text/javascript">
				$(document).ready(function () {
					// Initialize select2
					$(".adminMerchants").select2();
					//$("#merchant").select2();

				});

				function inputKeydownevent(event, input) {
					var invalidChars = ["-", "+", "e"];
					if (input == 'pgRefNum') {
						if (invalidChars.includes(event.key)) {
							event.preventDefault();
						}
					}
					if (input == 'orderId') {
						if (invalidChars.includes(event.key)) {
							event.preventDefault();
						}
					}
					if (input == 'customerEmail') {
					}
					if (input == 'phoneNo') {
						if (invalidChars.includes(event.key)) {
							event.preventDefault();
						}
					}
				}

				function myFunction() {
					var x = document.getElementById("actions11").value;
					if (x == 'csv') {
						document.querySelector('.buttons-csv').click();
					}
					if (x == 'copy') {
						document.querySelector('.buttons-copy').click();
					}
					//	if (x == 'pdf') {
					//		document.querySelector('.buttons-pdf').click();
					//	}

					// document.querySelector('.buttons-excel').click();
					// document.querySelector('.buttons-print').click();


				}

			</script>


			<script type="text/javascript">
				window.onload = function () {
					var token = document.getElementsByName("token")[0].value;
					var transFrom = flatpickr("#kt_datepicker_1", {}).selectedDates[0];
					var transTo = flatpickr("#kt_datepicker_2", {}).selectedDates[0];
					var dateFrom = convert(transFrom);
					var dateTo = convert(transTo);
					$("#kt_datepicker_1").flatpickr({
						showOtherMonths: true,
						dateFormat: 'Y-m-d',
						selectOtherMonths: false,
						defaultDate: "today",
						maxDate: new Date()
					});
					$("#kt_datepicker_2").flatpickr({
						showOtherMonths: true,
						dateFormat: 'Y-m-d',
						selectOtherMonths: false,
						defaultDate: "today",
						maxDate: new Date()
					});
					$('#searchAgentDataTable')
						.DataTable(
							{
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
								//serverSide: true,
								order: [[5, 'desc']],
								//stateSave: true,
								paging: true,

								"ajax": {
									"url": "refundTransactionSearchAction",
									"type": "POST",
									"data": {

										transactionId: "",
										orderId: "",
										customerEmail: "",
										phoneNo: "",
										channel: "ALL",
										mopType: "ALL",
										merchantEmailId: "ALL",
										transactionType: "REFUND",
										paymentType: "ALL",
										status: "Captured",
										currency: "ALL",
										dateFrom: dateFrom,
										dateTo: dateTo,
										draw: "2",
										length: "1000",
										start: "0",
										token: token,
										"struts.token.name": "token",

									}
								},
								"bProcessing": true,
								"bLengthChange": true,
								"bDestroy": true,
								"iDisplayLength": 10,
								"order": [[1, "desc"]],
								"aoColumns": [

									// {
									// 	data : 'pgRefNum'


									// },
									// {
									// 	data : 'merchants'

									// },
									// {
									// 	data : 'dateFrom'

									// },
									// {
									// 	data : 'orderId'

									// },
									// {
									// 	data : 'mopType'

									// },
									// {
									// 	data : 'mopType'

									// },
									// {
									// 	data : 'txnType'

									// },
									// {
									// 	data : 'status'

									// },
									// {
									// 	data : 'customerEmail'

									// },
									// {
									// 	data : 'amount'

									// },
									// {
									// 	data : 'totalAmount'

									// }

									{ data: 'pgRefNum' },
									{ data: 'merchants' },
									{ data: 'dateFrom' },
									{ data: 'orderId' },
									{
										data: "paymentMethods",
										render: function (data, type, full) {
											return full['paymentMethods'] + ' ' + '-'
												+ ' ' + full['mopType'];
										}
									},
									{ data: 'txnType' },
									{ data: 'status' },
									{ data: 'customerEmail' },
									{ data: 'amount' },
									{ data: 'totalAmount' },
									//  { data: 'currency' },
									//  { data: 'refundOrderId' },
									//  { data: 'udf4' },
									//  { data: 'udf5' },
									//  { data: 'udf6' }

								]
							});
				}
			</script>

			<script type="text/javascript">

				function populateDataTable() {

					var token = document.getElementsByName("token")[0].value;

					transFrom = flatpickr("#kt_datepicker_1", {}).selectedDates[0];

					transTo = flatpickr("#kt_datepicker_2", {}).selectedDates[0];
					dateFrom = convert(transFrom);
					dateTo = convert(transTo);

					//var transFrom = $.datepicker.parseDate('dd-mm-yy', $('#kt_datepicker_1').val());
					//var transTo = $.datepicker.parseDate('dd-mm-yy', $('#kt_datepicker_2').val());



					$('#searchAgentDataTable')
						.DataTable(
							{
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
								// serverSide: true,
								order: [[5, 'desc']],
								//stateSave: true,
								paging: true,

								"ajax": {
									"url": "refundTransactionSearchAction",//refundTransactionSearchAction
									"type": "POST",
									"data": function (d) {

										return generatePostData(d);

									}
								},
								"bProcessing": true,
								"bLengthChange": true,
								"bDestroy": true,
								"iDisplayLength": 10,
								"order": [[1, "desc"]],
								"aoColumns": [


									// {
									// 				data : 'pgRefNum'


									// 			},
									// 			{
									// 				data : 'merchants'

									// 			},
									// 			{
									// 				data : 'dateFrom'

									// 			},
									// 			{
									// 				data : 'orderId'

									// 			},
									// 			{
									// 				data : 'mopType'

									// 			},
									// 			{
									// 				data : 'paymentMethods'

									// 			},
									// 			{
									// 				data : 'txnType'

									// 			},
									// 			{
									// 				data : 'status'

									// 			},
									// 			{
									// 				data : 'customerEmail'

									// 			},
									// 			{
									// 				data : 'amount'

									// 			},
									// 			{
									// 				data : 'totalAmount'

									// 			}
									{ data: 'pgRefNum' },
									{ data: 'merchants' },
									{ data: 'dateFrom' },
									{ data: 'orderId' },
									{
										data: "paymentMethods",
										render: function (data, type, full) {
											return full['paymentMethods'] + ' ' + '-'
												+ ' ' + full['mopType'];
										}
									},
									{ data: 'txnType' },
									{ data: 'status' },
									{ data: 'customerEmail' },
									{ data: 'amount' },
									{ data: 'totalAmount' },
									//  { data: 'currency' },
									//  { data: 'refundOrderId' },
									//  { data: 'udf4' },
									//  { data: 'udf5' },
									//  { data: 'udf6' }
								]
							});
				}
			</script>

			<script type="text/javascript">
				function convert(str) {
					var date = new Date(str),
						mnth = ("0" + (date.getMonth() + 1)).slice(-2),
						day = ("0" + date.getDate()).slice(-2);
					//return [date.getFullYear(), mnth, day].join("-");
					return [day, mnth, date.getFullYear()].join("-");
				}
			</script>
			<!-- <%-- <script type="text/javascript">

function populateDataTable() {
	alert("on submit");

	var token  = document.getElementsByName("token")[0].value;
	alert("token"+token);

	transFrom = flatpickr("#kt_datepicker_1", {}).selectedDates[0];
	alert("transFrom"+transFrom);
	 transTo = flatpickr("#kt_datepicker_2", {}).selectedDates[0];
	 dateFrom=convert(transFrom);
	 dateTo=convert(transTo);

	//var transFrom = $.datepicker.parseDate('dd-mm-yy', $('#kt_datepicker_1').val());
	//var transTo = $.datepicker.parseDate('dd-mm-yy', $('#kt_datepicker_2').val());

	alert("transFrom"+transFrom);

	$('#kt_datatable_vertical_scroll')
			.DataTable(
					{
						"ajax" : {
							"url" : "refundTransactionSearchAction",
							"type" : "POST",
							"data" : {

								transactionId : "",
								orderId : "",
								phoneNo :"",
								mopType :"",
								customerEmail : "",
								merchantEmailId : "",
								transactionType : "",
								paymentType : "",
								status : "",
								currency : "",
								dateFrom : dateFrom
								dateTo : dateTo
								draw: "2",
								length: "10",
								start: "0",
								token : token,
								"struts.token.name" : "token",

									}
						},
						"bProcessing" : true,
						"bLengthChange" : true,
						"bDestroy" : true,
						"iDisplayLength" : 10,
						"order" : [ [ 1, "desc" ] ],
						"aoColumns" : [
							{
								"data" : "pgRefNum"

							},{
								"data" : "merchants"
							}, {
								"data" : "dateFrom"
							}, {
								"data" : "orderId"
							}, {
								"data" : "mopType",
								"visible" : false
							}, {
								"data" : "paymentMethods",
								"render" : function(data, type, full) {
									return full['paymentMethods'] + ' ' + '-'
											+ ' ' + full['mopType'];
								}

							}, {
								"data" : "txnType"
							}, {
								"data" : "status"
							},{
								"data" : "customerEmail"
							}, {
								"data" : "amount"
								"render" : function(data){
									return inrFormat(data);
								}
							}
							, {
								"data" : "totalAmount"
								"render" : function(data){
									return inrFormat(data);
								}
							},{
								"data" : "payId"

							},



							{
								"data" : "productDesc"
							},  {
								"data" : null,
								"visible" : false
								"mRender" : function(row) {
									return "\u0027" + row.transactionId;
								}
							}, {
								"data" : "internalCardIssusserBank",
								"visible" : false
							}, {
								"data" : "internalCardIssusserCountry",
								"visible" : false

							},
							 {
								"data" : "oId",
								"visible" : false

							}]
					});
}
</script> --%> -->


			<script type="text/javascript">
				$(document).ready(function () {

					$(function () {
						$("#dateFrom").datepicker({
							prevText: "click for previous months",
							nextText: "click for next months",
							showOtherMonths: true,
							dateFormat: 'dd-mm-yy',
							selectOtherMonths: false,
							maxDate: new Date()
						});
						$("#dateTo").datepicker({
							prevText: "click for previous months",
							nextText: "click for next months",
							showOtherMonths: true,
							dateFormat: 'dd-mm-yy',
							selectOtherMonths: false,
							maxDate: new Date()
						});
					});

					$(function () {
						var today = new Date();
						$('#dateTo').val($.datepicker.formatDate('dd-mm-yy', today));
						$('#dateFrom').val($.datepicker.formatDate('dd-mm-yy', today));
						renderTable();
					});

					$("#submit").click(function (env) {
						$('#loader-wrapper').show();
						reloadTable();
					});

					$(function () {
						var datepick = $.datepicker;
						var table = $('#txnResultDataTable').DataTable();
						$('#txnResultDataTable').on('click', 'td.my_class', function () {
							var rowIndex = table.cell(this).index().row;
							var rowData = table.row(rowIndex).data();

							popup(rowData.oId);
						});
					});
				});

				function renderTable() {
					var monthVal = parseInt(new Date().getMonth()) + 1;
					var merchantEmailId = document.getElementById("merchant").value;
					var table = new $.fn.dataTable.Api('#txnResultDataTable');

					var transFrom = $.datepicker
						.parseDate('dd-mm-yy', $('#dateFrom').val());
					var transTo = $.datepicker.parseDate('dd-mm-yy', $('#dateTo').val());
					if (transFrom == null || transTo == null) {
						alert('Enter date value');
						return false;
					}

					if (transFrom > transTo) {
						alert('From date must be before the to date');
						$('#loader-wrapper').hide();
						$('#dateFrom').focus();
						return false;
					}
					if (transTo - transFrom > 31 * 86400000) {
						alert('No. of days can not be more than 31');
						$('#loader-wrapper').hide();
						$('#dateFrom').focus();
						return false;
					}
					var token = document.getElementsByName("token")[0].value;

					var buttonCommon = {
						exportOptions: {
							format: {
								body: function (data, column, row, node) {
									// Strip $ from salary column to make it numeric
									return column === 0 ? "'" + data : (column === 1 ? "'"
										+ data : data);
								}
							}
						}
					};

					$('#txnResultDataTable')
						.dataTable(
							{
								"footerCallback": function (row, data, start, end,
									display) {
									var api = this.api(), data;

									// Remove the formatting to get integer data for summation
									var intVal = function (i) {
										return typeof i === 'string' ? i.replace(
											/[\,]/g, '') * 1
											: typeof i === 'number' ? i : 0;
									};

									// Total over all pages
									total = api.column(13).data().reduce(
										function (a, b) {
											return intVal(a) + intVal(b);
										}, 0);

									// Total over this page
									pageTotal = api.column(13, {
										page: 'current'
									}).data().reduce(function (a, b) {
										return intVal(a) + intVal(b);
									}, 0);

									// Update footer
									$(api.column(13).footer()).html(
										''
										+ inrFormat(pageTotal
											.toFixed(2)
											+ ' ' + ' '));
								},
								"columnDefs": [{
									className: "dt-body-right",
									"targets": [1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
										11, 12]
								}],
								dom: 'BTrftlpi',
								buttons: [
									$.extend(true, {}, buttonCommon, {
										extend: 'copyHtml5',
										exportOptions: {
											columns: [':visible']
										},
									}),
									$
										.extend(
											true,
											{},
											buttonCommon,
											{
												extend: 'csvHtml5',
												title: 'RefundTransaction_Report_'
													+ (new Date()
														.getFullYear())
													+ (monthVal > 9 ? monthVal
														: '0'
														+ monthVal)
													+ (new Date()
														.getDate() > 9 ? new Date()
															.getDate()
														: '0'
														+ new Date()
															.getDate())
													+ (new Date()
														.getHours() > 9 ? new Date()
															.getHours()
														: '0'
														+ new Date()
															.getHours())
													+ (new Date()
														.getMinutes() > 9 ? new Date()
															.getMinutes()
														: '0'
														+ new Date()
															.getMinutes())
													+ (new Date()
														.getSeconds() > 9 ? new Date()
															.getSeconds()
														: '0'
														+ new Date()
															.getSeconds()),
												exportOptions: {

													columns: [':visible']
												},
											}),
									{
										extend: 'pdfHtml5',
										orientation: 'landscape',
										pageSize: 'legal',
										//footer : true,
										title: 'Refund Transaction Report',
										exportOptions: {
											columns: [':visible']
										},
										customize: function (doc) {
											doc.defaultStyle.alignment = 'center';
											doc.styles.tableHeader.alignment = 'center';
										}
									},
									// Disabled print button.
									/* {extend : 'print',//footer : true,title : 'Refund Transaction Report',exportOptions : {columns : [':visible']}}, */
									{
										extend: 'colvis',
										columns: [0, 1, 2, 3, 4, 6, 7, 8, 9,
											10]
									}],

								"ajax": {

									"url": "refundTransactionSearchAction",
									"type": "POST",
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
								"lengthMenu": [[10, 25, 50], [10, 25, 50]],
								"order": [[2, "desc"]],

								"columnDefs": [{
									"type": "html-num-fmt",
									"targets": 4,
									"orderable": true,
									"targets": [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
										11, 12, 13]
								}],

								"columns": [
									// 	 {
									// 	"data" : "transactionId",
									// 	"className" : "txnId my_class text-class",
									// 	"width": "60px !important;"
									// },
									{
										"data": "pgRefNum",
										"className": "payId text-class"

									},
									{
										"data": "merchants",
										"className": "text-class"
									},
									{
										"data": "dateFrom",
										"className": "text-class"
									},
									{
										"data": "orderId",
										"className": "orderId text-class"
									},
									{
										"data": "mopType",
										"visible": false,
										"className": "displayNone text-class"
									},
									{
										"data": "paymentMethods",
										"render": function (data, type, full) {
											return full['paymentMethods'] + ' '
												+ '-' + ' '
												+ full['mopType'];
										},
										"className": "text-class"
									},
									{
										"data": "txnType",
										"className": "txnType text-class",
									},
									{
										"data": "status",
										"className": "status text-class"
									},
									{
										"data": "customerEmail",
										"className": "text-class"
									},
									{
										"data": "amount",
										"className": "text-class",
										"render": function (data) {
											return inrFormat(data);
										}
									},
									{
										"data": "totalAmount",
										"className": "text-class",
										"render": function (data) {
											return inrFormat(data);
										}
									},
									{
										"data": "payId",
										"visible": false

									},

									{
										"data": "productDesc",
										"visible": false
									},
									{
										"data": null,
										"visible": false,
										"className": "displayNone",
										"mRender": function (row) {
											return "\u0027" + row.transactionId;
										}
									}, {
										"data": "internalCardIssusserBank",
										"visible": false,
										"className": "displayNone"
									}, {
										"data": "internalCardIssusserCountry",
										"visible": false,
										"className": "displayNone"
									}, {
										"data": "oId",
										"visible": false,
										"className": "displayNone"
									}]
							});

					$(document)
						.ready(
							function () {

								var table = $('#txnResultDataTable').DataTable();
								$('#txnResultDataTable')
									.on(
										'click',
										'.center',
										function () {
											var columnIndex = table.cell(
												this).index().column;
											var rowIndex = table.cell(this)
												.index().row;
											var rowNodes = table.row(
												rowIndex).node();
											var rowData = table.row(
												rowIndex).data();
											var txnType1 = rowData.txnType;
											var status1 = rowData.status;

											if ((txnType1 == "SALE" && status1 == "Captured")
												|| (txnType1 == "AUTHORISE" && status1 == "Approved")
												|| (txnType1 == "SALE" && status1 == "Settled")) {
												var payId1 = rowData.pgRefNum;
												var orderId1 = rowData.orderId;
												var txnId1 = Number(rowData.transactionId);
												document
													.getElementById('payIdc').value = payId1;
												document
													.getElementById('orderIdc').value = orderId1;
												document
													.getElementById('txnIdc').value = txnId1;
												document.chargeback
													.submit();
											}
										});
							});

				}

				function reloadTable() {
					var datepick = $.datepicker;
					var transFrom = $.datepicker
						.parseDate('dd-mm-yy', $('#dateFrom').val());
					var transTo = $.datepicker.parseDate('dd-mm-yy', $('#dateTo').val());
					if (transFrom == null || transTo == null) {
						alert('Enter date value');
						return false;
					}

					if (transFrom > transTo) {
						alert('From date must be before the to date');
						$('#loader-wrapper').hide();
						$('#dateFrom').focus();
						return false;
					}
					if (transTo - transFrom > 31 * 86400000) {
						alert('No. of days can not be more than 31');
						$('#loader-wrapper').hide();
						$('#dateFrom').focus();
						return false;
					}
					$("#submit").attr("disabled", true);
					var tableObj = $('#txnResultDataTable');
					var table = tableObj.DataTable();
					table.ajax.reload();
				}

				function generatePostData(d) {
					var token = document.getElementsByName("token")[0].value;
					var merchantEmailId = document.getElementById("merchant").value;
					var transactionType = "Ref";

					var status = "REFUND";
					//var currency = document.getElementById("currency").value;
					if (merchantEmailId == '') {
						merchantEmailId = 'ALL'
					}
					if (transactionType == '') {
						transactionType = 'ALL'
					}

					if (status == '') {
						status = 'ALL'
					}
					// 					if (currency == '') {
					// 						currency = 'ALL'
					// 					}



					var Channel = document.getElementById("Channel").value;
					var paymentType = document.getElementById("paymentType").value;
					var mopType = document.getElementById("mopType").value;


					if (Channel == '') {
						Channel = 'ALL'
					}

					if (paymentType == '') {
						paymentType = 'ALL'
					}
					if (mopType == '') {
						mopType = 'ALL'
					}



					var transFrom = flatpickr("#kt_datepicker_1", {}).selectedDates[0];
					var transTo = flatpickr("#kt_datepicker_2", {}).selectedDates[0];
					var dateFrom = convert(transFrom);
					var dateTo = convert(transTo);
					var obj = {
						transactionId: document.getElementById("pgRefNum").value,
						orderId: document.getElementById("orderId").value,
						phoneNo: document.getElementById("phoneNo").value,

						customerEmail: document.getElementById("customerEmail").value,
						merchantEmailId: merchantEmailId,
						transactionType: transactionType,
						channel: Channel,
						paymentType: paymentType,
						mopType: mopType,
						status: status,
						currency: "ALL",
						dateFrom: dateFrom,
						dateTo: dateTo,
						draw: "2",
						length: "1000",
						start: "0",
						token: token,
						"struts.token.name": "token",
					};

					return obj;
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

				function popup(txnId) {

					var token = document.getElementsByName("token")[0].value;
					var myData = {
						token: token,
						"struts.token.name": "token",
						"transactionId": txnId
					}
					$
						.ajax({
							url: "customerAddressAction",
							type: "POST",
							data: myData,
							success: function (response) {
								var responseObj = response.aaData;

								$('#sec1 td').eq(0).text(
									responseObj.custName ? responseObj.custName
										: 'Not Available');
								$('#sec1 td').eq(1).text(
									responseObj.custPhone ? responseObj.custPhone
										: 'Not Available');
								$('#sec1 td').eq(2).text(
									responseObj.custCity ? responseObj.custCity
										: 'Not Available');

								$('#sec2 td').eq(0).text(
									responseObj.custState ? responseObj.custState
										: 'Not Available');
								$('#sec2 td')
									.eq(1)
									.text(
										responseObj.custCountry ? responseObj.custCountry
											: 'Not Available');
								$('#sec2 td').eq(2).text(
									responseObj.custZip ? responseObj.custZip
										: 'Not Available');

								$('#address1 td')
									.text(
										responseObj.custStreetAddress1 ? responseObj.custStreetAddress1
											: 'Not Available');
								$('#address2 td')
									.text(
										responseObj.custStreetAddress2 ? responseObj.custStreetAddress2
											: 'Not Available');

								$('#sec3 td')
									.eq(0)
									.text(
										responseObj.custShipName ? responseObj.custShipName
											: 'Not Available');
								$('#sec3 td')
									.eq(1)
									.text(
										responseObj.custShipPhone ? responseObj.custShipPhone
											: 'Not Available');
								$('#sec3 td')
									.eq(2)
									.text(
										responseObj.custShipCity ? responseObj.custShipCity
											: 'Not Available');

								$('#sec4 td')
									.eq(0)
									.text(
										responseObj.custShipState ? responseObj.custShipState
											: 'Not Available');
								$('#sec4 td')
									.eq(1)
									.text(
										responseObj.custShipCountry ? responseObj.custShipCountry
											: 'Not Available');
								$('#sec4 td')
									.eq(2)
									.text(
										responseObj.custShipZip ? responseObj.custShipZip
											: '');

								$('#address3 td')
									.text(
										responseObj.custShipStreetAddress1 ? responseObj.custShipStreetAddress1
											: 'Not Available');
								$('#address4 td')
									.text(
										responseObj.custShipStreetAddress2 ? responseObj.custShipStreetAddress2
											: 'Not Available');

								$('#auth td')
									.text(
										responseObj.internalTxnAuthentication ? responseObj.internalTxnAuthentication
											: 'Not Available');

								$('#popup').show();
							},
							error: function (xhr, textStatus, errorThrown) {
								alert('request failed');
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
						} else {
							if (document.getElementById("validEamilValue").style.display != "block"
								&& document.getElementById("validOrderIdValue").style.display != "block") {
								document.getElementById("submit").disabled = false;
							}
							document.getElementById("validValue").style.display = "none";
						}
					} else {
						if (document.getElementById("validEamilValue").style.display != "block"
							&& document.getElementById("validOrderIdValue").style.display != "block") {
							document.getElementById("submit").disabled = false;
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
						} else {
							if (document.getElementById("validValue").style.display != "block"
								&& document.getElementById("validOrderIdValue").style.display != "block") {
								document.getElementById("submit").disabled = false;
							}
							document.getElementById("validEamilValue").style.display = "none";
						}
					} else {
						if (document.getElementById("validValue").style.display != "block"
							&& document.getElementById("validOrderIdValue").style.display != "block") {
							document.getElementById("submit").disabled = false;
						}
						document.getElementById("validEamilValue").style.display = "none";
					}

				}

				function removeSpaces(fieldVal) {
					setTimeout(function () {
						var nospacepgRefVal = fieldVal.value.replace(/ /g, "");
						fieldVal.value = nospacepgRefVal;
					}, 400);
				}

				function validateOrderIdvalue(orderId) {
					setTimeout(
						function () {
							var orderIdreg = /^[0-9a-zA-Z\b\_-\s\+?.*?]+$/;
							if (orderId.value !== "") {
								if (orderIdreg.test(orderId.value) == false) {
									document.getElementById("validOrderIdValue").style.display = "block";
									document.getElementById("submit").disabled = true;
								} else {
									if (document.getElementById("validEamilValue").style.display != "block"
										&& document.getElementById("validValue").style.display != "block") {
										document.getElementById("submit").disabled = false;
									}
									document.getElementById("validOrderIdValue").style.display = "none";

								}
							} else {
								if (document.getElementById("validEamilValue").style.display != "block"
									&& document.getElementById("validValue").style.display != "block") {
									document.getElementById("submit").disabled = false;
								}
								document.getElementById("validOrderIdValue").style.display = "none";
							}
						}, 400);
				}

				function validateOrderId(event) {
					var regex = /^[0-9a-zA-Z\b\_-\s\+?.*?]+$/;
					var key = String.fromCharCode(!event.charCode ? event.which
						: event.charCode);
					if (!regex.test(key)) {
						event.preventDefault();
						return false;
					}
				}

			</script>

			<script>
				function getTranscationReport() {
					var reportType = document.getElementById("transcation").value;
					if (!reportType) {
						return;
					}
					var urls = new URL(window.location.href);

					var domain = urls.origin + "/crm/jsp/" + reportType;
					document.getElementById("transction").action = domain;
					const form = document.getElementById('transction');
					form.submit();

				}


			</script>

		</head>

		<body id="mainBody">

			<form id="transction" name="refundDetails" action="">

			</form>
			<div id="loader-wrapper" style="width: 100%; height: 100%; display: none;">
				<div id="loader"></div>
			</div>

			<!--begin::Content-->
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
								Refund Captured</h1>
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
								<li class="breadcrumb-item text-muted">Transaction Reports</li>
								<!--end::Item-->
								<!--begin::Item-->
								<li class="breadcrumb-item"><span class="bullet bg-gray-200 w-5px h-2px"></span></li>
								<!--end::Item-->
								<!--begin::Item-->
								<li class="breadcrumb-item text-dark">Refund Captured</li>
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
						<form action="" class="form mb-15" method="post" id="sale_captured_form">
							<s:hidden name="token" value="%{#session.customToken}" />
							<div class="row my-5">
								<div class="col">
									<div class="card">
										<div class="card-body">
											<!--begin::Input group-->
											<div class="row g-9 mb-8">
												<!--begin::Col-->
												<div class="col-md-4 fv-row">
													<label class="d-flex align-items-center fs-6 fw-bold mb-2">
														<span class=""> Transcation Report .</span>
													</label>

													<select name="currency" id="transcation" data-control="select2"
														data-hide-search="true" onchange="getTranscationReport()"
														class="form-select form-select-solid">
														<option value="">Select TXN Report</option>
														<option selected value="refundTransactionSearch">Refund Captured
														</option>
														<option value="saleTransactionSearch">Sale Captured</option>
														<option value="settledTransactionSearch">Settled Report</option>


													</select>

												</div>



											</div>
											<div class="row g-9 mb-8">
												<!--begin::Col-->



												<div
													class="col-md-8 fv-row d-flex justify-content-center align-items-end justify-content-md-end">
													<%-- <button type="submit" id="sale_captured_submit"
														class="btn w-100 w-md-25 btn-primary">
														<span class="indicator-label">Submit</span>
														<span class="indicator-progress">Please wait...
															<span
																class="spinner-border spinner-border-sm align-middle ms-2"></span></span>
														</button> --%>


												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
						</form>
					</div>
				</div>
				<!--end::Toolbar-->
				<!--begin::Post-->
				<div class="post d-flex flex-column-fluid" id="kt_post">
					<!--begin::Container-->
					<div id="kt_content_container" class="container-xxl">
						<form action="" class="form mb-15" method="post" id="refund_captured_form">
							<div class="row my-5">
								<div class="col">
									<div class="card">
										<div class="card-body">
											<!--begin::Input group-->
											<div class="row g-9 mb-8">
												<!--begin::Col-->
												<div class="col-md-4 fv-row">
													<label class="d-flex align-items-center fs-6 fw-bold mb-2">
														<span class="">PG REF No.</span>
													</label> <input type="number" min="0"
														class="form-control form-control-solid" id="pgRefNum"
														name="pgrefnumber" maxlength="17"
														onkeydown="inputKeydownevent(event,'pgRefNum')"
														oninput="if(value.length>16)value=value.slice(0,16)" />
												</div>
												<div class="col-md-4 fv-row">
													<label class="d-flex align-items-center fs-6 fw-bold mb-2">
														<span class="">Order ID</span>
													</label> <input type="text" class="form-control form-control-solid"
														name="orderid" id="orderId"
														onkeydown="inputKeydownevent(event,'orderId')"
														oninput="if(value.length>30)value=value.slice(0,30)" />
												</div>
												<div class="col-md-4 fv-row">
													<label class="d-flex align-items-center fs-6 fw-bold mb-2">
														<span class="">Customer Email</span>
													</label>
													<!--end::Label-->
													<input type="email" id="customerEmail"
														class="form-control form-control-solid" name="emailid"
														onkeydown="inputKeydownevent(event,'customerEmail')" />
												</div>
											</div>
											<div class="row g-9 mb-8">
												<!--begin::Col-->
												<div class="col-md-4 fv-row" style="display: none;">
													<label class="d-flex align-items-center fs-6 fw-bold mb-2">
														<span class="">Phone Number</span>
													</label>
													<!--end::Label-->
													<input type="number" min="0" id="phoneNo"
														class="form-control form-control-solid" name="phonenumber"
														onkeydown="inputKeydownevent(event,'phoneNo')"
														oninput="if(value.length>10)value=value.slice(0,10)" />
												</div>
												<!-- <div class="col-md-4 fv-row">
											<label
												class="d-flex align-items-center fs-6 fw-bold mb-2">
												<span class="">Currency</span>
											</label>
											end::Label -->
												<!-- <s:select name="currency" id="currency" headerValue="ALL"
											headerKey="" list="currencyMap" class="form-select form-select-solid adminMerchants" /> -->
												<!-- </div> -->
												<div class="col-md-4 fv-row">
													<label class="d-flex align-items-center fs-6 fw-bold mb-2">
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
																	fill="currentColor" />
																<path
																	d="M6 6C5.4 6 5 5.6 5 5V3C5 2.4 5.4 2 6 2C6.6 2 7 2.4 7 3V5C7 5.6 6.6 6 6 6ZM11 5V3C11 2.4 10.6 2 10 2C9.4 2 9 2.4 9 3V5C9 5.6 9.4 6 10 6C10.6 6 11 5.6 11 5ZM15 5V3C15 2.4 14.6 2 14 2C13.4 2 13 2.4 13 3V5C13 5.6 13.4 6 14 6C14.6 6 15 5.6 15 5ZM19 5V3C19 2.4 18.6 2 18 2C17.4 2 17 2.4 17 3V5C17 5.6 17.4 6 18 6C18.6 6 19 5.6 19 5Z"
																	fill="currentColor" />
																<path
																	d="M8.8 13.1C9.2 13.1 9.5 13 9.7 12.8C9.9 12.6 10.1 12.3 10.1 11.9C10.1 11.6 10 11.3 9.8 11.1C9.6 10.9 9.3 10.8 9 10.8C8.8 10.8 8.59999 10.8 8.39999 10.9C8.19999 11 8.1 11.1 8 11.2C7.9 11.3 7.8 11.4 7.7 11.6C7.6 11.8 7.5 11.9 7.5 12.1C7.5 12.2 7.4 12.2 7.3 12.3C7.2 12.4 7.09999 12.4 6.89999 12.4C6.69999 12.4 6.6 12.3 6.5 12.2C6.4 12.1 6.3 11.9 6.3 11.7C6.3 11.5 6.4 11.3 6.5 11.1C6.6 10.9 6.8 10.7 7 10.5C7.2 10.3 7.49999 10.1 7.89999 10C8.29999 9.90003 8.60001 9.80003 9.10001 9.80003C9.50001 9.80003 9.80001 9.90003 10.1 10C10.4 10.1 10.7 10.3 10.9 10.4C11.1 10.5 11.3 10.8 11.4 11.1C11.5 11.4 11.6 11.6 11.6 11.9C11.6 12.3 11.5 12.6 11.3 12.9C11.1 13.2 10.9 13.5 10.6 13.7C10.9 13.9 11.2 14.1 11.4 14.3C11.6 14.5 11.8 14.7 11.9 15C12 15.3 12.1 15.5 12.1 15.8C12.1 16.2 12 16.5 11.9 16.8C11.8 17.1 11.5 17.4 11.3 17.7C11.1 18 10.7 18.2 10.3 18.3C9.9 18.4 9.5 18.5 9 18.5C8.5 18.5 8.1 18.4 7.7 18.2C7.3 18 7 17.8 6.8 17.6C6.6 17.4 6.4 17.1 6.3 16.8C6.2 16.5 6.10001 16.3 6.10001 16.1C6.10001 15.9 6.2 15.7 6.3 15.6C6.4 15.5 6.6 15.4 6.8 15.4C6.9 15.4 7.00001 15.4 7.10001 15.5C7.20001 15.6 7.3 15.6 7.3 15.7C7.5 16.2 7.7 16.6 8 16.9C8.3 17.2 8.6 17.3 9 17.3C9.2 17.3 9.5 17.2 9.7 17.1C9.9 17 10.1 16.8 10.3 16.6C10.5 16.4 10.5 16.1 10.5 15.8C10.5 15.3 10.4 15 10.1 14.7C9.80001 14.4 9.50001 14.3 9.10001 14.3C9.00001 14.3 8.9 14.3 8.7 14.3C8.5 14.3 8.39999 14.3 8.39999 14.3C8.19999 14.3 7.99999 14.2 7.89999 14.1C7.79999 14 7.7 13.8 7.7 13.7C7.7 13.5 7.79999 13.4 7.89999 13.2C7.99999 13 8.2 13 8.5 13H8.8V13.1ZM15.3 17.5V12.2C14.3 13 13.6 13.3 13.3 13.3C13.1 13.3 13 13.2 12.9 13.1C12.8 13 12.7 12.8 12.7 12.6C12.7 12.4 12.8 12.3 12.9 12.2C13 12.1 13.2 12 13.6 11.8C14.1 11.6 14.5 11.3 14.7 11.1C14.9 10.9 15.2 10.6 15.5 10.3C15.8 10 15.9 9.80003 15.9 9.70003C15.9 9.60003 16.1 9.60004 16.3 9.60004C16.5 9.60004 16.7 9.70003 16.8 9.80003C16.9 9.90003 17 10.2 17 10.5V17.2C17 18 16.7 18.4 16.2 18.4C16 18.4 15.8 18.3 15.6 18.2C15.4 18.1 15.3 17.8 15.3 17.5Z"
																	fill="currentColor" />
															</svg>
														</span>
														<!--end::Svg Icon-->
														<!--end::Icon-->
														<!--begin::Datepicker-->
														<input class="form-control form-control-solid ps-12"
															placeholder="Select a date" name="datefrom"
															id="kt_datepicker_1" />
														<!--end::Datepicker-->
													</div>
												</div>
												<div class="col-md-4 fv-row">
													<label class="d-flex align-items-center fs-6 fw-bold mb-2">
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
																	fill="currentColor" />
																<path
																	d="M6 6C5.4 6 5 5.6 5 5V3C5 2.4 5.4 2 6 2C6.6 2 7 2.4 7 3V5C7 5.6 6.6 6 6 6ZM11 5V3C11 2.4 10.6 2 10 2C9.4 2 9 2.4 9 3V5C9 5.6 9.4 6 10 6C10.6 6 11 5.6 11 5ZM15 5V3C15 2.4 14.6 2 14 2C13.4 2 13 2.4 13 3V5C13 5.6 13.4 6 14 6C14.6 6 15 5.6 15 5ZM19 5V3C19 2.4 18.6 2 18 2C17.4 2 17 2.4 17 3V5C17 5.6 17.4 6 18 6C18.6 6 19 5.6 19 5Z"
																	fill="currentColor" />
																<path
																	d="M8.8 13.1C9.2 13.1 9.5 13 9.7 12.8C9.9 12.6 10.1 12.3 10.1 11.9C10.1 11.6 10 11.3 9.8 11.1C9.6 10.9 9.3 10.8 9 10.8C8.8 10.8 8.59999 10.8 8.39999 10.9C8.19999 11 8.1 11.1 8 11.2C7.9 11.3 7.8 11.4 7.7 11.6C7.6 11.8 7.5 11.9 7.5 12.1C7.5 12.2 7.4 12.2 7.3 12.3C7.2 12.4 7.09999 12.4 6.89999 12.4C6.69999 12.4 6.6 12.3 6.5 12.2C6.4 12.1 6.3 11.9 6.3 11.7C6.3 11.5 6.4 11.3 6.5 11.1C6.6 10.9 6.8 10.7 7 10.5C7.2 10.3 7.49999 10.1 7.89999 10C8.29999 9.90003 8.60001 9.80003 9.10001 9.80003C9.50001 9.80003 9.80001 9.90003 10.1 10C10.4 10.1 10.7 10.3 10.9 10.4C11.1 10.5 11.3 10.8 11.4 11.1C11.5 11.4 11.6 11.6 11.6 11.9C11.6 12.3 11.5 12.6 11.3 12.9C11.1 13.2 10.9 13.5 10.6 13.7C10.9 13.9 11.2 14.1 11.4 14.3C11.6 14.5 11.8 14.7 11.9 15C12 15.3 12.1 15.5 12.1 15.8C12.1 16.2 12 16.5 11.9 16.8C11.8 17.1 11.5 17.4 11.3 17.7C11.1 18 10.7 18.2 10.3 18.3C9.9 18.4 9.5 18.5 9 18.5C8.5 18.5 8.1 18.4 7.7 18.2C7.3 18 7 17.8 6.8 17.6C6.6 17.4 6.4 17.1 6.3 16.8C6.2 16.5 6.10001 16.3 6.10001 16.1C6.10001 15.9 6.2 15.7 6.3 15.6C6.4 15.5 6.6 15.4 6.8 15.4C6.9 15.4 7.00001 15.4 7.10001 15.5C7.20001 15.6 7.3 15.6 7.3 15.7C7.5 16.2 7.7 16.6 8 16.9C8.3 17.2 8.6 17.3 9 17.3C9.2 17.3 9.5 17.2 9.7 17.1C9.9 17 10.1 16.8 10.3 16.6C10.5 16.4 10.5 16.1 10.5 15.8C10.5 15.3 10.4 15 10.1 14.7C9.80001 14.4 9.50001 14.3 9.10001 14.3C9.00001 14.3 8.9 14.3 8.7 14.3C8.5 14.3 8.39999 14.3 8.39999 14.3C8.19999 14.3 7.99999 14.2 7.89999 14.1C7.79999 14 7.7 13.8 7.7 13.7C7.7 13.5 7.79999 13.4 7.89999 13.2C7.99999 13 8.2 13 8.5 13H8.8V13.1ZM15.3 17.5V12.2C14.3 13 13.6 13.3 13.3 13.3C13.1 13.3 13 13.2 12.9 13.1C12.8 13 12.7 12.8 12.7 12.6C12.7 12.4 12.8 12.3 12.9 12.2C13 12.1 13.2 12 13.6 11.8C14.1 11.6 14.5 11.3 14.7 11.1C14.9 10.9 15.2 10.6 15.5 10.3C15.8 10 15.9 9.80003 15.9 9.70003C15.9 9.60003 16.1 9.60004 16.3 9.60004C16.5 9.60004 16.7 9.70003 16.8 9.80003C16.9 9.90003 17 10.2 17 10.5V17.2C17 18 16.7 18.4 16.2 18.4C16 18.4 15.8 18.3 15.6 18.2C15.4 18.1 15.3 17.8 15.3 17.5Z"
																	fill="currentColor" />
															</svg>
														</span>
														<!--end::Svg Icon-->
														<!--end::Icon-->
														<!--begin::Datepicker-->
														<input class="form-control form-control-solid ps-12"
															placeholder="Select a date" name="dateto"
															id="kt_datepicker_2" />
														<!--end::Datepicker-->
													</div>
												</div>
												<div class="col-md-4 fv-row">
													<label class="d-flex align-items-center fs-6 fw-bold mb-2">
														<span class="">Channel</span>
													</label>
													<!--end::Label-->
													<select name="Channel" id="Channel"
														class="form-select form-select-solid adminMerchants"
														onchange="getPaymentType()">
														<option value="Fiat">FIAT</option>
													</select>
												</div>
											</div>
											<div class="row g-9 mb-8">
												<!--begin::Col-->



												<div class="col-md-4 fv-row">
													<label class="d-flex align-items-center fs-6 fw-bold mb-2">
														<span class="">Payment Method</span>
													</label>
													<!--end::Label-->
													<select name="paymentType" id="paymentType"
														onchange="getMopType(this.value,'mopType')"
														class="form-select form-select-solid adminMerchants">

													</select>
												</div>
												<div class="col-md-4 fv-row" id="mopTypeCol">
													<label class="d-flex align-items-center fs-6 fw-bold mb-2">
														<span class="">MOP Type</span>
													</label>
													<!--end::Label-->

													<select name="mopType" id="mopType"
														class="form-select form-select-solid adminMerchants">
														<option value="ALL">ALL</option>
													</select>
												</div>
												<div class="col-md-4 fv-row">
													<s:if
														test="%{#session.USER.UserType.name()=='ADMIN' || #session.USER.UserType.name()=='SUBADMIN' || #session.USER_TYPE.name()=='SUPERADMIN' || #session.USER_TYPE.name()=='RESELLER'}">
														<label class="d-flex align-items-center fs-6 fw-bold mb-2">
															<span class="">Merchant</span>
														</label>
													</s:if>
													<s:if
														test="%{#session.USER.UserType.name()=='ADMIN' || #session.USER.UserType.name()=='SUBADMIN' || #session.USER_TYPE.name()=='SUPERADMIN' || #session.USER_TYPE.name()=='RESELLER'}">
														<s:select name="merchant"
															class="form-select form-select-solid adminMerchants"
															id="merchant" headerKey="" headerValue="ALL"
															list="merchantList" listKey="emailId"
															listValue="businessName" autocomplete="off" />
													</s:if>
													<s:else>
														<s:select name="merchant"
															class="form-select form-select-solid adminMerchants d-none"
															id="merchant" list="merchantList" listKey="emailId"
															listValue="businessName" autocomplete="off" />
													</s:else>

												</div>

											</div>
											<div class="row g-9 mb-8">
												<!--begin::Col-->


												<div
													class="col-md-12 fv-row d-flex justify-content-center align-items-center ">
													<%-- <button type="submit" id="refund_captured_submit"
														class="btn w-100 w-md-25 btn-primary">
														<span class="indicator-label">Submit</span> <span
															class="indicator-progress">Please wait... <span
																class="spinner-border spinner-border-sm align-middle ms-2"></span></span>
														</button> --%>

														<button type="button" class="btn w-100 w-md-25 btn-primary"
															id="btnRefundConf">Search</button>
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
						</form>
						<div class="row my-5">
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
													<!--<option value="pdf">PDF</option>-->
												</select>
											</div>
											<div class="col-lg-4 col-sm-12 col-md-6">
												<div class="dropdown1">
													<button
														class="form-select form-select-solid actions dropbtn1">Customize
														Columns</button>
													<div class="dropdown-content1">
														<a class="toggle-vis" data-column="0">Pg ref no</a>
														<a class="toggle-vis" data-column="1">Merchant Name</a>
														<a class="toggle-vis" data-column="2">Date</a>
														<a class="toggle-vis" data-column="3">Order Id</a>
														<a class="toggle-vis" data-column="4">Payment Method</a>
														<a class="toggle-vis" data-column="5">Txn Type</a>
														<a class="toggle-vis" data-column="6">Status</a>
														<a class="toggle-vis" data-column="7">Customer Email</a>
														<a class="toggle-vis" data-column="8">Base Amount</a>
														<a class="toggle-vis" data-column="9">Total Amount</a>

													</div>
												</div>
											</div>
										</div>
										<div class="row g-9 mb-8">
											<!-- <table id="kt_datatable_vertical_scroll"
										class="table table-striped table-row-bordered gy-5 gs-7">
										<thead>
											<tr class="fw-bold fs-6 text-gray-800">
												<th class="min-w-70px">Pg Ref Num</th>
												<th scope="col">Merchant Name</th>
												<th scope="col">Date</th>
												<th scope="col">Order ID</th>
												<th scope="col">Payment Method</th>
												<th scope="col">TXN Type</th>
												<th scope="col">Status</th>
												<th scope="col">Customer Email</th>
												<th scope="col">Base Amount</th>
												<th scope="col">Total Amount</th>
											</tr>
										</thead>
										<tbody>

										</tbody>
									</table> -->

											<table id="searchAgentDataTable"
												class="table table-striped table-row-bordered gy-5 gs-7">
												<thead>
													<tr class="fw-bold fs-6 text-gray-800">
														<th class="min-w-90px">PG REF No.</th>
														<th scope="col">Merchant Name</th>
														<th scope="col">Date</th>
														<th scope="col">Order ID</th>
														<th scope="col">Payment Method</th>
														<th scope="col">TXN Type</th>
														<th scope="col">Status</th>
														<th scope="col">Customer Email</th>
														<th scope="col">Base Amount</th>
														<th scope="col">Total Amount</th>
														<!-- <th scope="col" style="font-size: 12px !important;">Currency</th>
												<th scope="col" style="font-size: 12px !important;">Refund</th>
												<th scope="col" style="font-size: 12px !important;">UDF4</th>
												<th scope="col" style="font-size: 12px !important;">UDF5</th>
												<th scope="col" style="font-size: 12px !important;">UDF6</th> -->
														<s:if
															test="%{(#session.USER.UserType.name()=='ADMIN' || #session.USER.UserType.name()=='SUBADMIN')}">
															<!-- 									<th style="text-align:center;">Chargeback</th> -->
														</s:if>
														<s:else>
															<!-- 									<th style='text-align: center'></th> -->
														</s:else>
													</tr>
												</thead>

											</table>
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
			<!--end::Content-->

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

			<script type="text/javascript">
				$(document).ready(function () {
					$('#closeBtn').click(function () {
						$('#popup').hide();
					});
				});
			</script>

			<script>
				var hostUrl = "assets/";
			</script>
			<script>
				var urls = new URL(window.location.href);
				var domain = urls.origin;

				var channel = $("#Channel").val().toUpperCase();



				$.ajax({
					url: domain + "/crmws/PaymentMethod/PaymentType/" + channel,
					//url: "http://localhost:8081/PaymentMethod/PaymentType/" + channel,
					type: 'GET',

					contentType: "application/json",
					success: function (data) {
						const selectElement = document.getElementById('paymentType');
						const option = document.createElement('option');
						option.value = "ALL";
						option.text = "ALL";
						selectElement.appendChild(option);

						if (data.respmessage == "Successfully") {
							var data = data.multipleResponse;
							Object.keys(data).forEach(key => {
								const value = data[key];

								console.log(key + "\t" + value);

								const option = document.createElement('option');
								option.value = key;
								option.text = value;
								selectElement.appendChild(option);

							}
							);
						}
					},
					error: function (data, textStatus, jqXHR) {


					}
				});
				if (channel != "FIAT") {
					document.getElementById("mopTypeCol").style.display = "none";
				}
				function getPaymentType() {
					debugger
					var channel = $("#Channel").val().toUpperCase();



					const selectElement = document.getElementById('paymentType');

					// Remove all child elements (options) from the select tag
					while (selectElement.firstChild) {
						selectElement.removeChild(selectElement.firstChild);
					}

					$.ajax({
						url: domain + "/crmws/PaymentMethod/PaymentType/" + channel,
						//url: "http://localhost:8081/PaymentMethod/PaymentType/" + channel,
						type: 'GET',

						contentType: "application/json",
						success: function (data) {
							const selectElement = document.getElementById('paymentType');
							if (data.respmessage == "Successfully") {
								var data = data.multipleResponse;
								const option = document.createElement('option');
								option.value = "ALL";
								option.text = "ALL";
								selectElement.appendChild(option);
								Object.keys(data).forEach(key => {
									const value = data[key];

									console.log(key + "\t" + value);

									const option = document.createElement('option');
									option.value = key;
									option.text = value;
									selectElement.appendChild(option);

								}
								);
							}
						},
						error: function (data, textStatus, jqXHR) {


						}
					});

					if (channel != "FIAT") {
						document.getElementById("mopTypeCol").style.display = "none";
					} else {
						document.getElementById("mopTypeCol").style.display = "block";
					}
				}
			</script>
			<!--begin::Global Javascript Bundle(used by all pages)-->

			<!--end::Global Javascript Bundle-->
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
			<script>
				$("#kt_datepicker_1").flatpickr({
					showOtherMonths: true,
					dateFormat: 'Y-m-d',
					selectOtherMonths: false,
					defaultDate: "today",
					maxDate: new Date()
				});
				$("#kt_datepicker_2").flatpickr({
					showOtherMonths: true,
					dateFormat: 'Y-m-d',
					selectOtherMonths: false,
					defaultDate: "today",
					maxDate: new Date()
				});

				$("#kt_datatable_vertical_scroll").DataTable({
					scrollY: true,
					scrollX: true,

				});

			</script>
			<script>
				"use strict";
				var KTCareersApply = function () {
					var t, e, i;
					return {
						init: function () {
							i = document.querySelector("#refund_captured_form"),
								t = document.getElementById("btnRefundConf"),
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
										orderid: {
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
										phonenumber: {
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
								});
							t.addEventListener("click", (function (i) {
								i.preventDefault(),

									e && e.validate().then((function (e) {
										if (e == 'Invalid') {



										}
										else {

											//	document.getElementById("btnRefundConf").disabled = false;
											handleChange();


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
			<script type="text/javascript">
				$('a.toggle-vis').on('click', function (e) {
					debugger
					e.preventDefault();
					table = $('#searchAgentDataTable').DataTable();
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
		</body>

		</html>