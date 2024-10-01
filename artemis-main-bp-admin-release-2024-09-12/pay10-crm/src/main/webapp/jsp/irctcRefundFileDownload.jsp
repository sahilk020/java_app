<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="/struts-tags" prefix="s"%>
<html dir="ltr" lang="en-US">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>IRCTC Refund File Download</title>
<script src="../js/jquery.min.js" type="text/javascript"></script>
<script src="../js/commanValidate.js"></script>
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
<script src="../js/commanValidate.js"></script>

<script type="text/javascript" src="../js/sweetalert.js"></script>
<link rel="stylesheet" href="../css/sweetalert.css">
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/limonte-sweetalert2/6.6.9/sweetalert2.min.css">
<script
	src="https://cdnjs.cloudflare.com/ajax/libs/limonte-sweetalert2/6.6.9/sweetalert2.min.js"></script>
<!--  loader scripts -->
<%-- <script src="../js/loader/modernizr-2.6.2.min.js"></script>
			<script src="../js/loader/main.js"></script> --%>
<%-- <link rel="stylesheet" href="../css/loader/normalize.css" />
<!-- <link rel="stylesheet" href="../css/loader/main.css" /> -->
<link rel="stylesheet" href="../css/loader/customLoader.css" />
<link href="../css/select2.min.css" rel="stylesheet" />
<script src="../js/jquery.select2.js" type="text/javascript"></script> --%>
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
tr td.my_class1 {
	cursor: pointer;
	text-decoration: none !important;
}

tr td.my_class1:hover {
	cursor: pointer !important;
	text-decoration: none !important;
}

tr th.my_class1:hover {
	color: #fff !important;
}

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

table.dataTable.display tbody tr.odd {
	background-color: #e6e6ff !important;
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

table.dataTable.display tbody tr.odd {
	background-color: #e6e6ff !important;
}
</style>
</head>
<body id="mainBody">
	<div id="loader-wrapper"
		style="width: 100%; height: 100%; display: none;"></div>
	<div style="overflow: scroll !important;">
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
					<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">
						Refund File Download</h1>
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
						<li class="breadcrumb-item text-muted">IRCTC Refund</li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item"><span
							class="bullet bg-gray-200 w-5px h-2px"></span></li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item text-dark">IRCTC Refund File Download</li>
						<!--end::Item-->
					</ul>
					<!--end::Breadcrumb-->
				</div>
				<!--end::Page title-->

			</div>
		</div>
	
		<!--end::Container-->
		<div class="post d-flex flex-column-fluid" id="kt_post">
			<!--begin::Container-->
			<div id="kt_content_container" class="container-xxl">
				<div class="row my-5">
					<div class="col">
						<div class="card">
							<div class="card-body">
								<!--begin::Input group-->
								<div class="row g-9 mb-8">
									<!--begin::Col-->
									<div class="col-md-3 fv-row">
										<label class="d-flex align-items-center fs-6 fw-bold mb-2">
											<span class="">Please select the date</span>
										</label>
										<div class="position-relative d-flex align-items-center">
											<s:textfield type="text" id="dateFrom" name="dateFrom"
												class="form-control form-control-solid ps-12"
												autocomplete="off" readonly="true" />

										</div>
									</div>

									<div class="col-md-2 fv-row">
										<input type="button" id="submit" value="view"
											onclick="viewDate()" class="btn btn-primary  mt-6 submit_btn">


									</div>

								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
	   </div>
		</div>
		<div style="overflow: scroll !important;">
			<table width="100%" align="left" cellpadding="0" cellspacing="0"
				class="formbox">
				<tr>
					<td align="left" style="padding: 10px;"><br /> <br />
						<div id="kt_content_container" class="container-xxl">
							<!-- 	<div style="overflow: scroll !important;"> -->
							<div class="row my-5">
								<div class="col">
									<div class="card">
										<div class="card-body">
											<div class="scrollD">
												<table id="datatable"
													class="display table table-striped table-row-bordered gy-5 gs-7"
													cellspacing="0" width="100%">
													<thead>
														<tr class="boxheadingsmall">
															<th>PayId</th>
															<th>id</th>
															<th>Date</th>
															<th>FileName</th>
															<th>Download</th>
															<th></th>

														</tr>
													</thead>
												</table>
											</div></td>
				</tr>
			</table>
		</div>

	<script
		src="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.js"></script>
	<script src="../assets/plugins/global/plugins.bundle.js"></script>
	<script src="../assets/js/scripts.bundle.js"></script>
	<script src="../assets/plugins/custom/datatables/datatables.bundle.js"></script>

	<script type="text/javascript">
		function handleChange() {
			$('#loader-wrapper').show();
			reloadTable();
		}

		$(document).ready(
				
				function() {
					
					$(function() {
						$("#dateFrom").flatpickr({
							maxDate : new Date(),
							dateFormat : 'd-m-Y',
							defaultDate : "today"
						});

					});
					$(function() {
						/* 	var today = new Date();
							$('#dateTo').val(
									$.datepicker.formatDate('dd-mm-yy',
											today));
							$('#dateFrom').val(
									$.datepicker.formatDate('dd-mm-yy',
											today)); */
						renderTable();

					});

					$(function() {
						var datepick = $.datepicker;
						var table = $('#chargebackDataTable').DataTable();
						$('#chargebackDataTable tbody').on('click',
								'.my_class', function() {
									submitForm(table, this);

								});
					});
				});
	</script>
	<script>
		function viewDate() {
			
			var date = document.getElementById("dateFrom").value;
	
			$.ajax({

				type : "GET",
				url : "irctcRefundFileDownload1",

				data : {
					"date" : date
				},
				success : function(response) {
					var responseObj = JSON.parse(JSON.stringify(response));
					renderTable(responseObj.offlineRefund);
				},
			});
		}

		function renderTable(data) {
			var getindex = 0;
			var table = new $.fn.dataTable.Api('#dataTable');

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

			$("#datatable").DataTable().destroy();

			$('#datatable')
					.dataTable(
							{

								'data' : data,

								'columns' : [
										{
											'data' : 'payId',
											'className' : 'batch_No text-class'
										},
										{

											'data' : 'id',
											'className' : 'tableId  text-class displayNone'
										},

										{
											'data' : 'date',
											'className' : 'settlement_Date text-class'
										},

										{
											'data' : 'fileName',
											'className' : 'tds text-class'
										},

										{
											"data" : null,
											"sClass" : "center",
											"bSortable" : false,
											"mRender" : function() {

												//'<a class="backGroundColorWhite" href="http://localhost:8080/crmws/downloadFile"> DOWNLOAD</a>';
												return '<h1><button class="btn btn-info danger"  name="merchantDelete" id="myBtn" onclick = "downloadFile5(this)" >download</button></h1>';

											}
										}, {

											"data" : null,
											"visible" : false,
											"className" : "displayNone",
											"mRender" : function(row) {
												return "\u0027" + row.id;

											}
										}

								]
							});
		}
		function download(val) {

			var table = $('#datatable').DataTable();
			$('#datatable tbody')
					.on(
							'click',
							'td',
							function() {
								//alert("--------------");
								var rows = table.rows();
								var columnVisible = table.cell(this).index().columnVisible;
								var rowIndex = table.cell(this).index().row;
								/* alert("--------------"+table.cell(rowIndex, 0).data());
								alert("--------------"+table.cell(rowIndex, 1).data());
								alert("--------------"+table.cell(rowIndex, 2).data());
								alert("--------------"+table.cell(rowIndex, 3).data()); */

								var id = table.cell(rowIndex, 0).data();
								var acquirerName = table.cell(rowIndex, 1)
										.data();
								var date = table.cell(rowIndex, 2).data();
								var fileName = table.cell(rowIndex, 3).data();
								var filepath = "D:\\sbi_refund_files\\SBICARD_20230109120339.xlsx";
								//var filepath = "/home/radhey/RR/refundFiles/"+fileName;

								//alert(id + acquirerName + date + fileName+filepath);

								var urls = new URL(window.location.href);
								var domain = urls.origin;

								$.ajax({

									type : "POST",
									url : domain + "/crmws/download/refund",
									//url : "refundFileDownloads",
									data : {
										"filename" : fileName,
										"filepath" : filepath
									},
									cache : false,

									success : function(response) {

									},

								});
							})

		}
	</script>



	<script type="text/javascript">
		function downloadFile5(param) {
			//alert("===========fileName ---- ");
			var table = $('#datatable').DataTable();
			var fileName = "NA";
			$('#datatable tbody')
					.on(
							'click',
							'td',
							function() {
								//alert("--------------");
								var rows = table.rows();
								var columnVisible = table.cell(this).index().columnVisible;
								var rowIndex = table.cell(this).index().row;
								/* alert("--------------"+table.cell(rowIndex, 0).data());
								alert("--------------"+table.cell(rowIndex, 1).data());
								alert("--------------"+table.cell(rowIndex, 2).data());
								alert("--------------"+table.cell(rowIndex, 3).data()); */

								var id = table.cell(rowIndex, 0).data();
								var acquirerName = table.cell(rowIndex, 1)
										.data();
								var date = table.cell(rowIndex, 2).data();
								fileName = table.cell(rowIndex, 3).data();
								var filepath = "D:\\sbi_refund_files\\SBICARD_20230109120339.xlsx";
								//alert("step 1 "+fileName);
								//alert("step 2 "+table.cell(rowIndex, 3).data());

								//alert("step 3 "+fileName);

								//alert("step 5 "+fileName);

								var urls = new URL(window.location.href);
								var domain = urls.origin;
								//var filenames = "Test_1.xlsx";
								var url = "fileName=" + fileName;
								//alert("step 6 "+url);
								window.open(domain+ '/crmws/irctc/refund/downloadFile?' + url);
								location.reload();

							})

		}
	</script>
</body>
</html>
