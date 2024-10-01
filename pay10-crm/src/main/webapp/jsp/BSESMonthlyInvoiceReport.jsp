<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="/struts-tags" prefix="s"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.Locale"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.text.Format"%>

<html dir="ltr" lang="en-US">

<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>BSES Monthly Invoice Report</title>
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

<style type="text/css">
.dt-buttons {
	margin-top: 35px !important;
}

.svg-icon {
	margin-top: 1vh !important;
}
</style>
<style>
.dt-buttons {
	display: none;
}
</style>

<script>
	function sumbitButton() {

		var selectedMerchant = document.getElementById("merchant").value;
		var selectedYear = document.getElementById("yearInput").value;
		var selectedMonth = document.getElementById("monthInput").value;

		if (selectedMerchant == "" || selectedMerchant == ''
				|| selectedMerchant == undefined) {
			alert("Please Select Merchant");
		}
		if (selectedYear == "" || selectedYear == ''
				|| selectedYear == undefined) {
			alert("Please Select Year");

		}
		if (selectedMonth == "" || selectedMonth == ''
				|| selectedMonth == undefined) {
			alert("Please Select Month");

		} else {
			var data = {
				"merchant" : selectedMerchant,
				"year" : selectedYear,
				"month" : selectedMonth
			}

			$
					.ajax({
						type : "POST",
						url : "GetBSESMonthlyInvoiceReport",
						data : data,
						timeout : 0,
						success : function(responseData, status) {
							debugger
							var obj = responseData.aaData;

							datatablefunction(obj);

						},
						error : function(data) {
							console
									.log("Network error, charging detail may not be saved");
						}
					});
		}

	}
</script>

<script type="text/javascript">
	var value = $("#colorPattern td").text();
	if (value < 0) {
		$("#colorPattern td").addClass("red");
	}
</script>
<script type="text/javascript">
	$(document).ready(function() {
		$("#merchant").select2();
	});
	function downloadData() {

		debugger
		var token = document.getElementsByName("token")[0].value;
		$("#merchant1").val(document.getElementById("merchant").value);
		$("#year").val(document.getElementById("yearInput").value);
		$("#month").val(document.getElementById("monthInput").value);
		var token = document.getElementsByName("token")[0].value;
		$("#downloadInvoice").submit();

	}
</script>
<style>
@media ( min-width : 992px) {
	.col-lg-3 {
		max-width: 30% !important;
	}
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
				<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">BSES
					Monthly Invoice Report</h1>
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
					<li class="breadcrumb-item text-muted">Account & Finance</li>
					<!--end::Item-->
					<!--begin::Item-->
					<li class="breadcrumb-item"><span
						class="bullet bg-gray-200 w-5px h-2px"></span></li>
					<!--end::Item-->
					<!--begin::Item-->
					<li class="breadcrumb-item text-dark">BSES Monthly Invoice
						Report</li>
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
						<div class="card-body">
							<!--begin::Input group-->
							<div class="row g-9 mb-8">
								<div class="card-body ">
									<div class="row my-3 align-items-center">
										<div class="col-lg-3 my-2">
											<label class="d-flex align-items-center fs-6 fw-bold mb-2">
												<span class="required">Merchant</span>
											</label>
											<s:select name="merchant" id="merchant" value="merchant"
												headerKey="All" class="form-select form-select-solid"
												headerValue="ALL" list="merchantList" listKey="payId"
												listValue="businessName" autocomplete="off" />

										</div>

										<div class="col-lg-2 my-2">
											<label for="yearInput"
												class="d-flex align-items-center fs-6 fw-bold mb-2"><span
												class="required">Select a year:</span></label> <select
												id="yearInput"
												class="form-select form-select-solid adminMerchants"></select>
										</div>
										<div class="col-lg-2 my-2">
											<label for="monthInput"
												class="d-flex align-items-center fs-6 fw-bold mb-2"><span
												class="required">Select a month:</span></label> <select
												id="monthInput"
												class="form-select form-select-solid adminMerchants"><option
													value="01">January</option>
												<option value="02">February</option>
												<option value="03">March</option>
												<option value="04">April</option>
												<option value="05">May</option>
												<option value="06">June</option>
												<option value="07">July</option>
												<option value="08">August</option>
												<option value="09">September</option>
												<option value="10">October</option>
												<option value="11">November</option>
												<option value="12">December</option></select>
										</div>

										<div class="col-lg-1 my-2">
											<label class="d-flex align-items-center fs-6 fw-bold mb-2">
												<span class="">&nbsp;</span>
											</label>
											<button type="button" class="btn btn-primary"
												id="SubmitButton" onclick="sumbitButton()">Submit</button>
										</div>
										<div class="col-lg-1 my-2">
											<label class="d-flex align-items-center fs-6 fw-bold mb-2">
												<span class="">&nbsp;</span>
											</label>
											<button type="button" class="btn btn-primary"
												onclick="downloadData()">Download</button>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>


		<div id="kt_content_container" class="container-xxl">
			<div class="row my-5">
				<div class="col">
					<div class="card">
						<div class="card-body">
							<!--begin::Input group-->
							<div class="row g-9 mb-8">
								<div class="row g-9 mb-8 justify-content-end">
									<div class="row g-9 mb-8">
										<div>
											<table id="example"
												class="table table-striped table-row-bordered gy-5 gs-7 "
												style="width: 100%">
												<thead>
													<tr class="boxheadingsmall fw-bold fs-6 text-gray-800">
														<th class="min-w-90px">Pg Ref Num</th>
														<th class="col">PayId</th>
														<th scope="col">Merchant Name</th>
														<th scope="col">Transaction Date</th>
														<th scope="col">Order Id</th>
														<th scope="col">Payment Method</th>
														<th scope="col">Txn Type</th>
														<th scope="col">Mop Type</th>
														<th scope="col">UDF9</th>
														<th scope="col">UDF10</th>
														<th scope="col">UDF11</th>
														<th scope="col">Amount</th>
														<th scope="col">Invoice value</th>
														<th scope="col">Invoice GST</th>
														<th scope="col">Total Invoice value</th>
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
		</div>

	</div>

	<script>
		// Get the current year
		const currentYear = new Date().getFullYear();

		// Get the select elements
		const yearInput = document.getElementById('yearInput');
		const monthInput = document.getElementById('monthInput');

		// Populate the year select element with options starting from the current year
		for (let year = currentYear; year <= currentYear + 10; year++) {
			const option = document.createElement('option');
			option.value = year;
			option.textContent = year;
			yearInput.appendChild(option);
		}
	</script>

	<script type="text/javascript">
		$('#example').dataTable({
			scrollY : true,
			scrollX : true,
		});
		$('a.toggle-vis').on('click', function(e) {
			debugger
			e.preventDefault();
			table = $('#example').DataTable();
			// Get the column API object
			var column1 = table.column($(this).attr('data-column'));
			// Toggle the visibility
			column1.visible(!column1.visible());
			if ($(this)[0].classList[1] == 'activecustom') {
				$(this).removeClass('activecustom');
			} else {
				$(this).addClass('activecustom');
			}
		});

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
			if (x == 'excel') {
				document.querySelector('.buttons-excel').click();
			}

		}

		function datatablefunction(data) {
			var buttonCommon = {
				exportOptions : {
					format : {
						body : function(data, column, row, node) {
							// Strip $ from salary column to make it numeric
							return (column === 1 ? "'" + data : data);
						}
					}
				}
			};
			$("#example").DataTable().destroy();
			$('#example').dataTable({
				'data' : data,

				dom : 'BTrtlpi',
				buttons : [ $.extend(true, {}, buttonCommon, {
					extend : 'copyHtml5',
					exportOptions : {
						columns : [ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 ]
					},
				}), $.extend(true, {}, buttonCommon, {
					extend : 'csvHtml5',
					title : 'Frm_Report',
					exportOptions : {
						columns : [ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 ],
					},
				}), {
					extend : 'pdfHtml5',
					orientation : 'landscape',
					pageSize : 'legal',
					//footer : true,
					title : 'Frm_Report',
					exportOptions : {
						columns : [ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 ]
					},
					customize : function(doc) {
						doc.defaultStyle.alignment = 'center';
						doc.styles.tableHeader.alignment = 'center';
					}
				}, {
					extend : 'colvis',
					columns : [ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10 ]
				}, ],
				"scrollY" : true,
				"scrollX" : true,
				'columns' : [ {
					'data' : 'pgRefNo'

				}, {
					'data' : 'payId'

				}, {
					'data' : 'merchantName'
				},
				{
					'data' : 'transactionDate'

				}, {
					'data' : 'orderId'

				}, {
					'data' : 'paymentType'

				}, {
					'data' : 'txnType'

				}, {
					'data' : 'mopType'

				}, {
					'data' : 'udf9'

				}, {
					'data' : 'udf10'

				}, {
					'data' : 'udf11'

				}, {
					'data' : 'amount'

				}, {
					'data' : 'invoiceValue'

				}, {
					'data' : 'invoiceGst'

				}, {
					'data' : 'totalInvoiceValue'

				} ]
			});
		}
	</script>

	<form action="DownloadBSESMonthlyInvoiceReport" id="downloadInvoice"
		method="post">
		<input id="merchant1" name="merchant" type="hidden"> <input
			id="year" name="year" type="hidden"> <input id="month"
			name="month" type="hidden">
		<s:hidden name="token" value="%{#session.customToken}"></s:hidden>
		<s:hidden name="struts.token.name" value="token"></s:hidden>
	</form>
</body>

</html>