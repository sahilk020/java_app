<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib prefix="s" uri="/struts-tags" %>
	<html>

	<head>
		<title>Invoice Search</title>
		<link rel="shortcut icon" href="../assets/media/images/paylogo.svg" />
		<!--begin::Fonts-->
		<link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Inter:300,400,500,600,700" />
		<!--end::Fonts-->
		<!--begin::Vendor Stylesheets(used by this page)-->
		<link href="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.css" rel="stylesheet" type="text/css" />
		<link href="../assets/plugins/custom/datatables/datatables.bundle.css" rel="stylesheet" type="text/css" />
		<!--end::Vendor Stylesheets-->
		<!--begin::Global Stylesheets Bundle(used by all pages)-->
		<link href="../assets/plugins/global/plugins.bundle.css" rel="stylesheet" type="text/css" />
		<link href="../assets/css/style.bundle.css" rel="stylesheet" type="text/css" />
		<link href="../assets/css/custom/custom-style.css" rel="stylesheet" type="text/css" />
		<script src="../assets/plugins/global/plugins.bundle.js"></script>
		<script src="../assets/js/scripts.bundle.js"></script>
		<script src="../js/messi.js"></script>
		<link href="../css/messi.css" rel="stylesheet" />

		<style>
			.hyper {
				color: blue !important;
				cursor: pointer;
			}

			.invoice-width {
				width: 65px !important;
				text-decoration: none !important;
				color: white !important;
				border-bottom-style: hidden !important;
			}

			.no-width {
				width: 75px !important;
			}

			.form-control {
				margin-left: 0 !important;
			}

			table.dataTable.display tbody tr.odd {
				background-color: #e6e6ff !important;
			}

			table.dataTable.display tbody tr.odd>.sorting_1 {
				background-color: #e6e6ff !important;
			}

			.text-class {
				text-align: center !important;
			}

			table.dataTable thead .sorting:before,
			table.dataTable thead .sorting_asc:before,
			table.dataTable thead .sorting_desc:before,
			table.dataTable thead .sorting_asc_disabled:before,
			table.dataTable thead .sorting_desc_disabled:before {
				top: 2px;
				right: 1em;
				content: none !important;

			}

			table.dataTable thead .sorting:after,
			table.dataTable thead .sorting_asc:after,
			table.dataTable thead .sorting_desc:after,
			table.dataTable thead .sorting_asc_disabled:after,
			table.dataTable thead .sorting_desc_disabled:after {
				top: 2px;
				right: 0.5em;
				content: none !important;
			}
		</style>

		<script type="text/javascript">
			var popallow = true;
			// download buttons
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
			}
			//  start to load table on load
			$(document).ready(function () {
				renderTable();
				$(function () {

					$('#kt_datatable_vertical_scroll tbody').on('click', 'td', function () {
						popup($('#kt_datatable_vertical_scroll').DataTable(), this);
					});
				});
			});

			function renderTable() {
				//to show new loader -Harpreet
				$.ajaxSetup({
					global: false,
					beforeSend: function () {
						toggleAjaxLoader();
					},
					complete: function () {
						toggleAjaxLoader();
					}
				});
				var table = new $.fn.dataTable.Api('#kt_datatable_vertical_scroll');
				$.ajaxSetup({
					global: false,
					beforeSend: function () {
						//$(".modal").show();
					},
					complete: function () {
						$(".modal").hide();
					}
				});

				var token = document.getElementsByName("token")[0].value;

				$('#kt_datatable_vertical_scroll')
					.dataTable({
						dom: 'Brtipl',
						buttons: [
							{
								extend: 'print',
								exportOptions: {
									columns: ':visible'
								}
							},{
								extend: 'csvHtml5',
								title: 'Invoice Search',
								exportOptions: {
									columns: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9]
								}
							},{
				                extend: 'pdfHtml5',
								orientation: 'landscape',
		                		pageSize: 'LEGAL',
		                		exportOptions: {
									columns: [0,1, 2,3,4,5,6,7,8,9]
								}
				            },
							'colvis', 'copy', 'csv', 'excel', 'pdf', 'print',
						],

						"scrollY": true,
						"scrollX": true,

						searchDelay: 500,
						processing: false,
						serverSide: true,
						order: [[5, 'desc']],
						stateSave: true,
						paging: true,
						select: {
							style: 'multi',
							selector: 'td:first-child input[type="checkbox"]',
							className: 'row-selected'
						},
						"footerCallback": function (row, data, start, end, display) {
							var api = this.api(), data;

							// Remove the formatting to get integer data for summation
							var intVal = function (i) {
								return typeof i === 'string' ? i.replace(/[\,]/g, '') * 1 : typeof i === 'number' ? i : 0;
							};

							// Total over all pages
							total = api.column(1).data().reduce(
								function (a, b) {
									return intVal(a) + intVal(b);
								}, 0);

							// Total over this page
							pageTotal = api.column(1, {
								page: 'current'
							}).data().reduce(function (a, b) {
								return intVal(a) + intVal(b);
							}, 0);

							// Update footer
							//$(api.column(10).footer()).html(
							//	'' + inrFormat(pageTotal.toFixed(2) + ' ' + ' '));

							// Total over all pages
							total = api.column(1).data().reduce(
								function (a, b) {
									return intVal(a) + intVal(b);
								}, 0);

							// Total over this page
							pageTotal = api.column(1, {
								page: 'current'
							}).data().reduce(function (a, b) {
								return intVal(a) + intVal(b);
							}, 0);
						},
						ajax: {
							url: "invoiceSearchAction",
							//url: "http://localhost:9000/crm/jsp/invoiceSearchAction",
							type: 'remote',
							method: 'POST',
							data: function (d) {
								return generatePostData(d);
							},
						},
						"bProcessing": true,
						"bLengthChange": true,
						"bDestroy": true,
						"iDisplayLength": 10,
						"order": [[1, "desc"]],
						columns: [
							{
								data: "invoiceId",
								"className": "hyper"
							},
							{ data: "invoiceNo" },
							{ data: "createDate" },
							{ data: "businessName" },
							{ data: "name" },
							{ data: "email" },
							{ data: "currencyCode" },
							{ data: "amount" },
							{ data: "invoiceType" },
							{ data: "invoiceStatus" },
							{
								data: null,
								orderable: false,
								render: function (data) {
									if (data.invoiceType == "SINGLE_PAYMENT") {
										if (data.invoiceStatus === "PAID") {
											return "NA"
										} else {
											return '<button class="btn btn-primary btn-xs">Email and SMS</button>';
										}
									}
									else {
										return '<button class="btn btn-primary btn-xs">QR Code</button>';
									}
								}
							}
						],
					});
			}

			function generatePostData(d) {
				debugger
				var obj = {
					invoiceNo: document.getElementById("invoicenumber").value,
					customerEmail: "",
					payId: document.getElementById("merchant").value,
					currency: "356",
					invoiceType: "ALL",
					dateFrom: convert(document.getElementById("kt_datepicker_1").value),
					dateTo: convert(document.getElementById("kt_datepicker_2").value),
					length: d.length,
					draw: d.draw,
					start: d.start,
					token: token,
					"struts.token.name": "token"
				};
				return obj;
			}
			//end after load table on load


			function afterSubmitCall() {
				//to show new loader -Harpreet
				$.ajaxSetup({
					global: false,
					beforeSend: function () {
						toggleAjaxLoader();
					},
					complete: function () {
						toggleAjaxLoader();
					}
				});
				// var table = new $.fn.dataTable.Api('#kt_datatable_vertical_scroll');
				$.ajaxSetup({
					global: false,
					beforeSend: function () {
						//$(".modal").show();
					},
					complete: function () {
						$(".modal").hide();
					}
				});

				var token = document.getElementsByName("token")[0].value;

				$('#kt_datatable_vertical_scroll').dataTable({
					dom: 'Brtipl',
					buttons: [
						{
							extend: 'print',
							exportOptions: {
								columns: ':visible'
							}
						},{
							extend: 'csvHtml5',
							title: 'Invoice Search',
							exportOptions: {
								columns: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9]
							}
						},{
			                extend: 'pdfHtml5',
							orientation: 'landscape',
	                		pageSize: 'LEGAL',
	                		exportOptions: {
								columns: [0,1, 2,3,4,5,6,7,8,9]
							}
			            },
						'colvis', 'copy', 'csv', 'excel', 'pdf', 'print',
					],

					"scrollY": true,
					"scrollX": true,

					searchDelay: 500,
					processing: false,
					serverSide: true,
					order: [[1, 'desc']],
					stateSave: true,
					paging: true,
					select: {
						style: 'multi',
						selector: 'td:first-child input[type="checkbox"]',
						className: 'row-selected'
					},
					"footerCallback": function (row, data, start, end, display) {
						var api = this.api(), data;

						// Remove the formatting to get integer data for summation
						var intVal = function (i) {
							return typeof i === 'string' ? i.replace(/[\,]/g, '') * 1 : typeof i === 'number' ? i : 0;
						};

						// Total over all pages
						total = api.column(1).data().reduce(
							function (a, b) {
								return intVal(a) + intVal(b);
							}, 0);

						// Total over this page
						pageTotal = api.column(1, {
							page: 'current'
						}).data().reduce(function (a, b) {
							return intVal(a) + intVal(b);
						}, 0);

						// Update footer
						//$(api.column(10).footer()).html(
						//	'' + inrFormat(pageTotal.toFixed(2) + ' ' + ' '));


						// Total over all pages
						total = api.column(1).data().reduce(
							function (a, b) {
								return intVal(a) + intVal(b);
							}, 0);

						// Total over this page
						pageTotal = api.column(1, {
							page: 'current'
						}).data().reduce(function (a, b) {
							return intVal(a) + intVal(b);
						}, 0);


					},
					ajax: {
						url: "invoiceSearchAction",
						//url: "http://localhost:9000/crm/jsp/invoiceSearchAction",
						type: 'remote',
						method: 'POST',
						data: function (d) {
							return generatePostData1(d);

						},

					},
					"bProcessing": true,
					"bLengthChange": true,
					"bDestroy": true,
					"iDisplayLength": 10,
					"order": [[1, "desc"]],
					columns: [
						{
							data: "invoiceId",
							"className": "hyper"
						},
						{ data: "invoiceNo" },
						{ data: "createDate" },
						{ data: "businessName" },
						{ data: "name" },
						{ data: "email" },
						{ data: "currencyCode" },
						{ data: "amount" },
						{ data: "invoiceType" },
						{ data: "invoiceStatus" },
						{
							data: null,
							orderable: false,
							render: function (data) {
								if (data.invoiceType == "SINGLE_PAYMENT") {
									if (data.invoiceStatus === "PAID") {
										return "NA"
									} else {
										return '<button class="btn btn-primary btn-xs">Email and SMS</button>';
									}
								}
								else {
									return '<button class="btn btn-primary btn-xs">QR Code</button>';
								}
							}
						}
					],

				});
			}
			function generatePostData1(d) {
				debugger
				var token = document.getElementsByName("token")[0].value;
				var obj = {
					invoiceNo: document.getElementById("invoicenumber").value,
					customerEmail: document.getElementById("customeremail").value,
					payId: document.getElementById("merchant").value,
					currency: document.getElementById("currency").value,
					invoiceType: document.getElementById("invoicetype").value,
					dateFrom: convert(document.getElementById("kt_datepicker_1").value),
					dateTo: convert(document.getElementById("kt_datepicker_2").value),
					length: d.length,
					draw: d.draw,
					start: d.start,
					token: token,
					"struts.token.name": "token",
				};
				return obj;
			}

			function popup(table, index) {

				var rows = table.rows();
				var columnVisible = table.cell(index).index().columnVisible;
				var columnNumber = table.cell(index).index().column;
				var token = document.getElementsByName("token")[0].value;
				var rowIndex = table.cell(index).index().row;
				var invId = table.cell(rowIndex, 0).data();
				var invType = table.cell(rowIndex, 8).data();

				if (columnVisible == 0 || columnVisible == 1) {
					if (popallow) {
						popallow = true;
						var token = document.getElementsByName("token")[0].value;
						$.ajax({
							url: "invoicePopup",
							type: "POST",
							data: {
								svalue: invId,
								token: token,
								"struts.token.name": "token",
							},
							success: function (data) {
								var message = data;
								new Messi(message, {
									center: true,
									buttons: [{
										id: 0,
										label: 'Close',
										val: 'X'
									}],
									callback: function (val) {
										$('.modal')
											.remove();
										var table = $('#kt_datatable_vertical_scroll')
											.DataTable();
										table.ajax.reload();
									},
									//width : '820px',
									modal: true


								});
								popallow = true;
							}
						});
					}
				} else if (columnVisible == 10) {
					if (invType == "SINGLE_PAYMENT") {
						var answer = confirm("Are you sure you want to send invoice link?");
						if (answer != true) {
							return false;
						}
						var token = document.getElementsByName("token")[0].value;
						$.ajaxSetup({
							global: false,
							beforeSend: function () {
								//$(".modal").show(); // Commented code because it was giving black screen.

							},
							complete: function () {
								$(".modal").hide();
							}
						});
						$.ajax({
							url: "invoiceSMSAction",
							type: "POST",
							data: {
								invoiceId: invId,
								token: token,
								"struts.token.name": "token",
							},
							success: function (data) {
								alert('Invoice link has been send successfully through SMS and Email !!');
							},
							error: function (data) {
								alert('Unable to send invoice link to SMS and  Email !!');
							}
						});

					} else {
						var answer = confirm("Are you sure you want to download QR Code");
						if (answer != true) {
							return false;
						}

						document.getElementById('invoiceId').value = invId;
						document.downloadqrcode.submit();

					}

				}
			}

			function decodeDiv() {
				var divArray = document.getElementsByTagName('div');
				for (var i = 0; i < divArray.length; ++i) {
					var div = divArray[i];
					if (div.id.indexOf('param-') > -1) {
						var val = div.innerHTML;
						div.innerHTML = decodeVal(val);
					}
				}
			}

			function decodeVal(value) {
				var txt = document.createElement("textarea");
				txt.innerHTML = value;
				return txt.value;
			}



		</script>

		<script>

			function trimString(str) {
				str.value = str.value.trim();
				return str.value;
				//in case you want to value in return. 
			}

			function validateEmail(customerEmail) {
				var reg = /^([A-Za-z0-9_\-\.\+])+\@([A-Za-z0-9_\-\+])+\.([A-Za-z]{2,4})$/;
				if (customerEmail.value !== "") {
					trimString(customerEmail);
					if (reg.test(customerEmail.value) == false) {
						alert('Please Enter valid Email Address');
						return false;
					}
				}

				return true;

			}

		</script>


	</head>

	<body id="mainBody">

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
						<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">Invoice Search</h1>
						<!--end::Title-->
						<!--begin::Separator-->
						<span class="h-20px border-gray-200 border-start mx-4"></span>
						<!--end::Separator-->
						<!--begin::Breadcrumb-->
						<ul class="breadcrumb breadcrumb-separatorless fw-semibold fs-7 my-1">
							<!--begin::Item-->
							<li class="breadcrumb-item text-muted">
								<a href="home" class="text-muted text-hover-primary">Dashboard</a>
							</li>
							<!--end::Item-->
							<!--begin::Item-->
							<li class="breadcrumb-item">
								<span class="bullet bg-gray-200 w-5px h-2px"></span>
							</li>
							<!--end::Item-->
							<!--begin::Item-->
							<li class="breadcrumb-item text-muted">Quick Pay Invoice</li>
							<!--end::Item-->
							<!--begin::Item-->
							<li class="breadcrumb-item">
								<span class="bullet bg-gray-200 w-5px h-2px"></span>
							</li>
							<!--end::Item-->
							<!--begin::Item-->
							<li class="breadcrumb-item text-dark">Invoice Search</li>
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
					<form action="" class="form mb-15" method="post" id="invoice_search_form">
						<div class="row my-5">
							<div class="col">
								<!-- <h4
								class="page-heading d-flex text-dark fw-bold fs-4 flex-column justify-content-center my-5">
								Invoice Search</h4> -->
								<div class="card">
									<div class="card-body">
										<!--begin::Input group-->
										<div class="row g-9 mb-8">
											<!--begin::Col-->
											<div class="col-md-4 fv-row">
												<label class="d-flex align-items-center fs-6 fw-bold mb-2">
													<span class="">Invoice Number</span>
												</label>
												<!--end::Label-->
												<input type="text" class="form-control form-control-solid"
													name="invoicenumber" id="invoicenumber"
													onkeydown="inputKeydownevent(event,'invoicenumber')" />
											</div>
											<div class="col-md-4 fv-row">
												<label class="d-flex align-items-center fs-6 fw-bold mb-2">
													<span class="">Customer Email</span>
												</label>
												<!--end::Label-->
												<input type="text" class="form-control form-control-solid"
													name="emailid" id="customeremail"
													onkeydown="inputKeydownevent(event,'customerEmail')" />

											</div>
											<%-- <s:if
												test="%{#session.USER.UserType.name()=='ADMIN' || #session.USER.UserType.name()=='SUBADMIN' || #session.USER_TYPE.name()=='SUPERADMIN'}">
												<div class="col-md-4 fv-row">
													<label>Merchants</label><br>
													<div class="txtnew">
														<s:select name="merchants"
															class="d-flex align-items-center fs-6 fw-semibold mb-2"
															id="merchants" headerKey="ALL" headerValue="ALL"
															list="merchantList" listKey="payId" listValue="businessName"
															autocomplete="off" />
													</div>
												</div>
												</s:if>
												<s:else>
													<div class="col-md-4 fv-row">
														<label>Merchant:</label><br>
														<div class="txtnew">
															<s:select name="merchants"
																class="d-flex align-items-center fs-6 fw-semibold mb-2"
																id="merchants" list="merchantList" listKey="payId"
																listValue="businessName" autocomplete="off" />
														</div>
													</div>
												</s:else> --%>
												<div class="col-md-4 fv-row">
													<label class="d-flex align-items-center fs-6 fw-bold mb-2">
														<span class="">Currency</span>
													</label>
													<!--end::Label-->
													<s:select name="currency" id="currency" headerKey="ALL"
														list="currencyMap" class="form-select form-select-solid"
														autocomplete="off" />
												</div>

										</div>
										<div class="row g-9 mb-8">
											<!--begin::Col-->


											<div class="col-md-4 fv-row">
												<label class="d-flex align-items-center fs-6 fw-bold mb-2">
													<span class="">Invoice Type</span>
												</label>
												<!--end::Label-->
												<select name="invoicetype" data-control="select2" data-placeholder="All"
													id="invoicetype" class="form-select form-select-solid"
													data-hide-search="true">
													<option value="ALL">ALL</option>
													<option value="SINGLE_PAYMENT">SINGLE_PAYMENT</option>
													<option value="PROMOTIONAL_PAYMENT">PROMOTIONAL_PAYMENT</option>
												</select>
											</div>
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
											<!--begin::Col-->
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
										</div>
										<div class="row g-9 mb-8">
											<div class="col-md-4 fv-row">
												<s:if
													test="%{#session.USER.UserType.name()=='ADMIN' || #session.USER.UserType.name()=='SUBADMIN' || #session.USER_TYPE.name()=='SUPERADMIN'|| #session.USER_TYPE.name()=='RESELLER'}">
													<label class="d-flex align-items-center fs-6 fw-bold mb-2">
														<span class="">Merchant</span>
													</label>

													<s:select name="merchants" id="merchant" headerKey="ALL"
														headerValue="ALL" list="merchantList" listKey="payId"
														listValue="businessName" autocomplete="off"
														class="form-select form-select-solid merchants" />

												</s:if>
												<s:else>

													<s:select name="merchants" id="merchant" list="merchantList"
														listKey="payId" listValue="businessName" autocomplete="off"
														class="d-none merchants" />
												</s:else>

											</div>
											<div
												class="col-md-8 fv-row d-flex justify-content-center align-items-end justify-content-md-end">
												<button type="submit" id="invoice_search_submit"
													class="btn w-lg-25 w-md-75 w-100 btn-primary">
													<span class="indicator-label">Submit</span>
													<span class="indicator-progress">Please wait...
														<span
															class="spinner-border spinner-border-sm align-middle ms-2"></span></span>
												</button>
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
											<select name="currency" data-control="select2" data-placeholder="Actions"
												id="actions11" class="form-select form-select-solid actions"
												data-hide-search="true" onchange="myFunction();">
												<option value="">Actions</option>
												<option value="copy">Copy</option>
												<option value="csv">CSV</option>
												<option value="pdf">PDF</option>
											</select>
										</div>
										<div class="col-lg-4 col-sm-12 col-md-6">
											<div class="dropdown1">
												<button class="form-select form-select-solid actions dropbtn1">Customize
													Columns</button>
												<div class="dropdown-content1">
													<a class="toggle-vis" data-column="0">Invoice Id</a>
													<a class="toggle-vis" data-column="1">Invoice No</a>
													<a class="toggle-vis" data-column="2">Created Date</a>
													<a class="toggle-vis" data-column="3">Merchant</a>
													<a class="toggle-vis" data-column="4">Customer Name</a>
													<a class="toggle-vis" data-column="5">Customer Email</a>
													<a class="toggle-vis" data-column="6">Currency</a>
													<a class="toggle-vis" data-column="7">Amount</a>
													<a class="toggle-vis" data-column="8">Invoice Type</a>
													<a class="toggle-vis" data-column="9">Status</a>
												</div>
											</div>
										</div>
									</div>
									<div class="row g-9 mb-8">
										<table id="kt_datatable_vertical_scroll"
											class="table table-striped table-row-bordered gy-5 gs-7">
											<thead>
												<tr class="fw-bold fs-6 text-gray-800">
													<th>Invoice ID</th>
													<th scope="col">Invoice Number</th>
													<th class="min-w-100px">Created Date</th>
													<th scope="col">Merchant</th>
													<th scope="col">Customer Name</th>
													<th scope="col">Customer Email</th>
													<th scope="col">Currency</th>
													<th scope="col">Amount</th>
													<th scope="col">Invoice Type</th>
													<th scope="col">Status</th>
													<th class="min-w-150px">Action</th>

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


		<s:form name="downloadqrcode" action="invoiceQRCodeAction">
			<s:hidden name="invoiceId" id="invoiceId" value="" />
			<s:hidden name="token" value="%{#session.customToken}"></s:hidden>
		</s:form>


		<script>var hostUrl = "assets/";</script>
		<!--begin::Global Javascript Bundle(used by all pages)-->

		<!--end::Global Javascript Bundle-->
		<!--begin::Vendors Javascript(used by this page)-->
		<script src="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.js"></script>

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
				maxDate: new Date(),
				dateFormat: "Y-m-d",
				defaultDate: "today"
			});
			$("#kt_datepicker_2").flatpickr({
				maxDate: new Date(),
				dateFormat: "Y-m-d",
				defaultDate: "today"
			});


		</script>

		<script type="text/javascript">

			"use strict";
			var KTCareersApply = function () {
				var t, e, i;
				return {
					init: function () {
						i = document.querySelector("#invoice_search_form"),
							t = document.getElementById("invoice_search_submit"),
							e = FormValidation.formValidation(i, {
								fields: {
									invoicenumber: {
										validators: {
											callback: {
												callback: function (input) {
													if (input.value.match(/[!\@\^\_\&\/\\#,\|+()$~%.'":*?<>{}]/)) {
														return {
															valid: false,
															message: 'Special characters not allowed',
														};
													} else {
														return {
															valid: true
														}
													}
												}
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
													}
													else {

														if (!input.value.match(/^\w+@[a-zA-Z_]+?\.[a-zA-Z]{2,3}$/)) {
															// document.getElementsByClassName("invalid-feedback")[2].style.display='block';

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




										}
										else {
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
												afterSubmitCall();
											}

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

			function convert(str) {
				var date = new Date(str),
					mnth = ("0" + (date.getMonth() + 1)).slice(-2),
					day = ("0" + date.getDate()).slice(-2);
				//return [date.getFullYear(), mnth, day].join("-");
				return [day, mnth, date.getFullYear()].join("-");
			}
			var transFrom;
			var transTo;
			var dateFrom;
			var dateTo;
			var token;


			"use strict";

			var KTDatatablesServerSide = function () {
				// Shared variables
				var table;
				var dt;
				var filterPayment;



				// Search Datatable --- official docs reference: https://datatables.net/reference/api/search()
				var handleSearchDatatable = function () {
					const filterSearch = document.querySelector('[data-kt-docs-table-filter="search"]');
					filterSearch.addEventListener('keyup', function (e) {
						dt.search(e.target.value).draw();
					});
				}

				// Filter Datatable
				var handleFilterDatatable = () => {
					// Select filter options
					filterPayment = document.querySelectorAll('[data-kt-docs-table-filter="payment_type"] [name="payment_type"]');
					const filterButton = document.querySelector('[data-kt-docs-table-filter="filter"]');


				}

				// Delete customer
				var handleDeleteRows = () => {
					// Select all delete buttons
					const deleteButtons = document.querySelectorAll('[data-kt-docs-table-filter="delete_row"]');

					deleteButtons.forEach(d => {
						// Delete button on click
						d.addEventListener('click', function (e) {
							e.preventDefault();

							// Select parent row
							const parent = e.target.closest('tr');

						})
					});
				}

				// Reset Filter
				var handleResetForm = () => {
					// Select reset button
					const resetButton = document.querySelector('[data-kt-docs-table-filter="reset"]');

					// Reset datatable
					resetButton.addEventListener('click', function () {
						// Reset payment type
						filterPayment[0].checked = true;

						// Reset datatable --- official docs reference: https://datatables.net/reference/api/search()
						dt.search('').draw();
					});
				}

				// Init toggle toolbar
				var initToggleToolbar = function () {
					// Toggle selected action toolbar
					// Select all checkboxes
					const container = document.querySelector('#kt_datatable_vertical_scroll');
					const checkboxes = container.querySelectorAll('[type="checkbox"]');

					// Select elements
					const deleteSelected = document.querySelector('[data-kt-docs-table-select="delete_selected"]');

					// Toggle delete selected toolbar
					checkboxes.forEach(c => {
						// Checkbox on click event
						c.addEventListener('click', function () {
							setTimeout(function () {
								toggleToolbars();
							}, 50);
						});
					});


				}

				// Toggle toolbars
				var toggleToolbars = function () {
					// Define variables
					const container = document.querySelector('#kt_datatable_vertical_scroll');
					const toolbarBase = document.querySelector('[data-kt-docs-table-toolbar="base"]');
					const toolbarSelected = document.querySelector('[data-kt-docs-table-toolbar="selected"]');
					const selectedCount = document.querySelector('[data-kt-docs-table-select="selected_count"]');

					// Select refreshed checkbox DOM elements
					const allCheckboxes = container.querySelectorAll('tbody [type="checkbox"]');

					// Detect checkboxes state & count
					let checkedState = false;
					let count = 0;

					// Count checked boxes
					allCheckboxes.forEach(c => {
						if (c.checked) {
							checkedState = true;
							count++;
						}
					});
				}

				// Public methods
				return {
					init: function () {
						// initDatatable();
						//handleSearchDatatable();
						initToggleToolbar();
						handleFilterDatatable();
						handleDeleteRows();
						handleResetForm();
					}
				}
			}();
		</script>
		<script type="text/javascript">
			$('a.toggle-vis').on('click', function (e) {
				debugger
				e.preventDefault();
				table = $('#kt_datatable_vertical_scroll').DataTable();
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
		<style>
			.dt-buttons {
				display: none;
			}
		</style>
	</body>

	</html>