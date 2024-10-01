<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
	<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
	<%@ taglib uri="/struts-tags" prefix="s" %>
		<html dir="ltr" lang="en-US">

		<head>
			<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
			<title>Search Nodal Transactions</title>
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
			<link href="../css/select2.min.css" rel="stylesheet" />
			<script src="../js/jquery.select2.js" type="text/javascript"></script>

			<script>
				$(document).ready(function () {
					$("#payId").select2();
					$(".merchantPayId").select2();
				});


			</script>



			<script type="text/javascript">
				$(document).ready(function () {
					document.getElementById("loadingInner").style.display = "none";

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
						$('#txnResultDataTable').on('click', 'td.my_class1', function () {
							var rowIndex = table.cell(this).index().row;
							var rowData = table.row(rowIndex).data();
							popup(rowData.txnId);

						});
					});
				});

				function renderTable() {
					var getindex = 0;
					var txnId = document.getElementById("txnId").value;
					var table = new $.fn.dataTable.Api('#txnResultDataTable');

					var transFrom = $.datepicker.parseDate('dd-mm-yy', $('#dateFrom').val());
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
									return column === 0 ? "'" + data : (column === 1 ? "'" + data : data);
								}
							}
						}
					};

					$('#txnResultDataTable').dataTable(
						{
							"footerCallback": function (row, data, start, end, display) {
								var api = this.api(), data;


							},
							"columnDefs": [{
								className: "dt-body-right",
								"targets": [0, 1, 2, 3, 4, 5, 6, 7, 8]
							}],
							dom: 'BTrftlpi',
							buttons: [
								$.extend(true, {}, buttonCommon, {
									extend: 'copyHtml5',
									exportOptions: {
										columns: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11]
									},
								}),
								$.extend(true, {}, buttonCommon, {
									extend: 'csvHtml5',
									title: 'Search Nodal Transactions_' + getCurrentTimeStamp(),
									exportOptions: {

										columns: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11]
									},
								}),
								{
									extend: 'pdfHtml5',
									orientation: 'landscape',
									pageSize: 'legal',
									//footer : true,
									title: 'Search Nodal Transactions_' + getCurrentTimeStamp(),
									exportOptions: {
										columns: [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11]
									},
									customize: function (doc) {
										doc.defaultStyle.alignment = 'center';
										doc.styles.tableHeader.alignment = 'center';
									}
								},
								// {
								// 	extend : 'print',
								// 	//footer : true,
								// 	title : 'Search Nodal Transactions',
								// 	exportOptions : {
								// 		columns : [':visible']
								// 	}
								// },
								{
									extend: 'colvis',
									columns: [0, 2, 3, 4, 5, 6, 7, 9]
								}],

							"ajax": {

								"url": "nodalTransactionsSearch",
								"timeout": 0,
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

							"columnDefs": [
								{
									"type": "html-num-fmt",
									"targets": 4,
									"orderable": true,
									"targets": [0, 1, 2, 3, 4, 5, 6, 7, 8, 9]
								}
							],

							"columns": [{
								"data": "txnId",
								"className": "txn_Id my_class1 text-class"

							},
							// {
							// 	"data" : "oid",
							// 	"className" : "oid text-class"

							// },
							{
								"data": "orderId",
								"className": "order_Id text-class"
							},

							{
								"data": "merchantBusinessName",
								"className": "merchant_Business_Name text-class"
							},
							{
								"data": "rrn",
								"className": "text-class"
							},
							{
								"data": "beneficiaryName",
								"className": "merchant_Provided_Name text-class",
								"visible": false
							},

							{
								"data": "beneficiaryCode",
								"className": "beneficiary_Code text-class",
								"visible": false
							},
							{
								"data": "beneAccNo",
								"className": "beneficiary_Code text-class",

							},
							{
								"data": "paymentType",
								"className": "payment_Type text-class",
								"visible": false
							},

							{
								"data": "acquirer",
								"className": "acquirer text-class"
							},
							{
								"data": "createdDate",
								"className": "created_Date text-class"
							},

							{
								"data": "amount",
								"className": "amount text-class",
								"render": function (data) {
									return inrFormat(data);
								}
							}, {
								"data": "status",
								"className": "status text-class"
							},

							// {
							// 	"data" : "comments",
							// 	"className" : "comments text-class"
							// },
							{
								"data": null,
								"className": "center",
								"width": '8%',
								"orderable": false,
								"mRender": function (row) {
									if ((row.txnType).toUpperCase() == "STATUS" && (((row.status).toUpperCase() == "FAILED") || ((row.status).toUpperCase() == "SETTLED") || ((row.status).toUpperCase() == "REJECTED") || ((row.status).toUpperCase() == "DENIED"))) {
										return '<button class="btn btn-info btn-xs btn-block" id="btnRefresh' + getindex + '" onclick="refreshStatus(this,' + getindex++ + ')" style="display:none;">Get Status </button>';
									} else {
										return '<button class="btn btn-info btn-xs btn-block" id="btnRefresh' + getindex + '" onclick="refreshStatus(this,' + getindex++ + ')" >Get Status </button>';
									}
								}
							}]
						});



				}

				// function renderButton(row,getindex){


				// }

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
					var txnId = document.getElementsByName("txnId")[0].value.trim();
					var orderId = document.getElementsByName("orderId")[0].value.trim();
					var paymentType = document.getElementById("paymentType").value;
					var status = document.getElementById("status").value;
					var payId = document.getElementById("payId").value;
					var beneType = document.getElementById("beneType").value;


					if (paymentType == '') {
						paymentType = 'ALL'
					}
					if (status == '') {
						status = 'ALL'
					}
					if (txnId == '') {
						txnId = 'ALL'
					}
					if (orderId == '') {
						orderId = 'ALL'
					}
					if (payId == '') {
						payId = 'ALL'
					}
					if (beneType == '') {
						beneType = 'ALL'
					}



					var obj = {
						paymentType: paymentType,
						status: status,
						txnId: txnId,
						orderId: orderId,
						payId: payId,
						beneType: beneType,
						dateFrom: document.getElementById("dateFrom").value,
						dateTo: document.getElementById("dateTo").value,
						draw: d.draw,
						length: d.length,
						start: d.start,
						token: token,
						"struts.token.name": "token",
					};

					return obj;
				}

				function popup(txnId) {
					//	console.log('Loading Transaction Details')
					var token = document.getElementsByName("token")[0].value;
					var myData = {
						token: token,
						"struts.token.name": "token",
						"txnId": txnId,
					}
					$.ajax({
						url: "fetchTransferHistoryAction",
						timeout: 0,
						type: "POST",
						data: myData,
						success: function (responseObj) {
							//var responseObj =  response.aaData;
							//console.log(responseObj);
							if (responseObj.response == "" && responseObj.response != undefined) {
								$('#sec1 td').eq(0).text(responseObj.merchantBusinessName ? responseObj.merchantBusinessName : 'Not Available');
								$('#sec1 td').eq(1).text(responseObj.merchantProvidedName ? responseObj.merchantProvidedName : 'Not Available');

								$('#sec2 td').eq(1).text(responseObj.beneType ? responseObj.beneType : 'Not Available');
								$('#sec2 td').eq(0).text(responseObj.merchantProvidedId ? responseObj.merchantProvidedId : 'Not Available');
								$('#sec2 td').eq(2).text(responseObj.acquirer ? responseObj.acquirer : 'Not Available');

								$('#sec3 td').eq(1).text(responseObj.mobileNo ? responseObj.mobileNo : 'Not Available');
								$('#sec3 td').eq(0).text(responseObj.emailId ? responseObj.emailId : 'Not Available');

								// $('#sec4 td').eq(0).text(responseObj.txnDate ? responseObj.txnDate : 'Not Available');

								$('#sec5 td').eq(0).text(responseObj.beneAccountNo ? responseObj.beneAccountNo : 'Not Available');
								$('#sec5 td').eq(1).text(responseObj.ifscCode ? responseObj.ifscCode : 'Not Available');

								$('#sec6 td').eq(0).text(responseObj.bankName ? responseObj.bankName : 'Not Available')
								$('#sec6 td').eq(1).text(responseObj.amount ? responseObj.amount : 'Not Available');


								$('#sec7 td').eq(0).text(responseObj.paymentType ? responseObj.paymentType : 'Not Available');
								$('#sec7 td').eq(1).text(responseObj.txnDate ? responseObj.txnDate : 'Not Available')


								$('#sec8 td').eq(0).text(responseObj.utr ? responseObj.utr : 'Not Available');

								$('#sec8 td').eq(1).text(responseObj.orderId ? responseObj.orderId : 'Not Available');




								$('#popup').show();
							} else {
								document.getElementById("historyPayout").innerHTML = responseObj.response;
								$('#transferHistoryStatus').modal('show');
								//alert(responseObj.response);
							}
						},
						error: function (xhr, textStatus, errorThrown) {
							alert('request failed');
						}
					});

				};


				function refreshStatus(val, getindex) {
					let tableIndex = $(val).parent().parent().index();

					var row = val;
					var cells = val.parentElement.parentElement.cells;
					var rowData = val.parentElement.parentElement;
					var oid = $('.oid').eq(tableIndex + 2).html();
					//var oid = rowData.children[1].innerText.trim();
					var orderId = $('.order_Id').eq(tableIndex + 2).html();
					//var custId = rowData.children[3].innerText.trim();
					var acquirer = $('.acquirer').eq(tableIndex + 2).html();
					var token = document.getElementsByName("token")[0].value;

					document.getElementById("btnRefresh" + getindex).disabled = true;
					document.getElementById("loadingInner").style.display = "block";
					//$('#loader-wrapper').show();
					$
						.ajax({
							url: "refreshTransactionStatus",
							timeout: 0,
							type: "POST",
							data: {
								oid: oid,
								orderId: orderId,
								//custId : custId,
								token: token,
								acquirer: acquirer,
								"struts.token.name": "token",
							},
							success: function (data) {

								var response = ((data["Invalid request"] != null) ? (data["Invalid request"].response[0]) : (data.response));
								if (null != response) {
									document.getElementById("loadingInner").style.display = "none";
									document.getElementById("refresh").innerHTML = data.response;
									$('#refreshStatusmodal').modal('show');
									//alert(response);
									document.getElementById("btnRefresh" + getindex).disabled = false;
								}
								//reloadTable();
							},
							error: function (data) {
								$('#loader-wrapper').hide();
								document.getElementById("loadingInner").style.display = "none";
								document.getElementById("btnRefresh" + getindex).disabled = false;
								reloadTable();
							}
						});
				};
			</script>

			<script>
				// function checkRefNo(){
				// 	var refValue = document.getElementById("pgRefNum").value;
				// 	var refNoLength = refValue.length;
				// 	if((refNoLength <16) && (refNoLength >0)){
				// 		document.getElementById("submit").disabled = true;
				// 		document.getElementById("validRefNo").style.display = "block";
				// 	}
				// 	else if(refNoLength == 0){
				// 		document.getElementById("submit").disabled = false;
				// 		document.getElementById("validRefNo").style.display = "none";
				// 	}else{
				// 		document.getElementById("submit").disabled = false;
				//         document.getElementById("validRefNo").style.display = "none";
				// 	} 
				// }

				function valdTxnId() {
					var aadharElement = document.getElementById("txnId");
					var value = aadharElement.value.trim();
					if (value.length > 0) {
						var aadhar = aadharElement.value;
						var aadharexp = /^[0-9]{16,16}$/;
						if (!aadhar.match(aadharexp)) {
							document.getElementById('invalid-txnId').innerHTML = "Please enter valid TXN Id";
							document.getElementById("submit").disabled = true;
							return false;
						} else {
							document.getElementById('invalid-txnId').innerHTML = "";
							document.getElementById("submit").disabled = false;
							return true;
						}
					} else {
						document.getElementById('invalid-txnId').innerHTML = "";
						document.getElementById("submit").disabled = false;
						return true;
					}
				}
			</script>

			<!-- <style type="text/css">
/* .cust {width: 24%!important; margin:0 5px !important; 
} */
.samefnew{
	width: 19.5%!important;
    margin: 5px 5px !important;
    /*font: bold 10px arial !important;*/
}
/* .btn {padding: 3px 7px!important; font-size: 12px!important; } */
.samefnew-btn{
    width: 12%;
    float: left;
    font: bold 11px arial;
    color: #333;
    line-height: 22px;
    margin-top: 5px;
}
/*tr td.my_class{color:#000 !important; cursor: default !important; text-decoration: none;}*/
tr td.my_class1{
	cursor: pointer;
	text-decoration: none !important;
}
tr td.my_class1:hover{
	cursor: pointer !important;
	text-decoration: none !important;
}

tr th.my_class1:hover{
	color: #fff !important;
}

.cust .form-control, .samefnew .form-control{
	margin:0px !important;
	width: 95%;
}
.select2-container{
	width: 100% !important;
}
.clearfix:after{
	display: block;
	visibility: hidden;
	line-height: 0;
	height: 0;
	clear: both;
	content: '.';
}
#popup{
	position: fixed;
	top:0px;
	left: 0px;
	background: rgba(0,0,0,0.7);
	width: 100%;
	height: 100%;
	z-index:999; 
	display: none;
}
.innerpopupDv{
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
#loader-wrapper .loader-section.section-left, #loader-wrapper .loader-section.section-right{
	background: rgba(225,225,225,0.6) !important;
	width: 50% !important;
}
.invoicetable{
	float: none;
}
.innerpopupDv h2{
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
.text-class{
	text-align: center !important;
}
.download-btn {
	background-color:#496cb6;
	display: block;
    width: 100%;
    height: 30px;
    padding: 3px 4px;
    font-size: 14px;
    line-height: 1.42857143;
    color: #fff;
    border: 1px solid #ccc;
    border-radius: 4px;
	margin-top:30px;
}
#loading {width: 100%;height: 100%;top: 0px;left: 0px;position: fixed;display: block; z-index: 99}
#loading-image {position: absolute;top: 40%;left: 55%;z-index: 100; width:10%;} 

#loadingInner {width: 100%;height: 100%;top: 0px;left: 0px;position: fixed;display: block; z-index: 99}
#loading-image-inner {position: absolute;top: 33%;left: 48%;z-index: 100; width:5%;} 

</style> -->
		</head>

		<body id="mainBody">



			<div id="loader-wrapper" style="width: 100%; height: 100%; display:none;">
				<div id="loader"></div>
			</div>
			<div id="loadingInner" display="none">
				<img id="loading-image-inner" src="../image/sand-clock-loader.gif" alt="BUSY..." />

			</div>

			<div class="content d-flex flex-column flex-column-fluid" id="kt_content">
				<div class="toolbar" id="kt_toolbar">
					<!--begin::Container-->
					<div id="kt_toolbar_container" class="container-fluid d-flex flex-stack">
						<!--begin::Page title-->
						<div data-kt-swapper="true" data-kt-swapper-mode="prepend"
							data-kt-swapper-parent="{default: '#kt_content_container', 'lg': '#kt_toolbar_container'}"
							class="page-title d-flex align-items-center me-3 flex-wrap mb-5 mb-lg-0 lh-1">
							<!--begin::Title-->
							<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">
								Transaction History</h1>
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
								<li class="breadcrumb-item text-muted">Merchnat Payout
								</li>
								<!--end::Item-->
								<!--begin::Item-->
								<li class="breadcrumb-item"><span class="bullet bg-gray-200 w-5px h-2px"></span>
								</li>
								<!--end::Item-->
								<!--begin::Item-->
								<li class="breadcrumb-item text-dark">Transaction History
								</li>
								<!--end::Item-->
							</ul>
							<!--end::Breadcrumb-->
						</div>
						<!--end::Page title-->

					</div>
					<!--end::Container-->
				</div>


				<div class="modal" id="popup" style="overflow-y: auto;">
					<!-- <div > -->
					<!--<div class="wrapper " style="max-height: 590px;"> -->


					<!-- Navbar -->

					<!-- End Navbar -->
					<div class="content innerpopupDv">


						<!-- Navbar -->

						<!-- End Navbar -->
						<!-- <div class="content"> -->
						<div class="container-fluid">

							<div class="row">


								<div class="card ">
									<div class="card-header ">
										<h4 class="card-title" id="cardTitle">Transfer History Information
											<!-- <small class="description">Vertical Tabs</small> -->
										</h4>
										<button style="position: absolute;
								left: 93%;border: none;
								background: none;" id="closeBtn1"><img style="width:20px" src="../image/red_cross.jpg"></button>
									</div>
									<div class="card-body ">
										<div class="row">
											<div class="col-md-12 ml-auto mr-auto">
												<div class="col-md-12">
													<!--
											  color-classes: "nav-pills-primary", "nav-pills-info", "nav-pills-success", "nav-pills-warning","nav-pills-danger"
										  -->
													<ul class="nav nav-pills nav-pills-rose nav-pills-icons flex-column"
														role="tablist" style="display: inline-block;">

														<li class="col-md-6 nav-item" id="listitem">
															<a class="nav-link active" data-toggle="tab" href="#link110"
																role="tablist">
																<i class="material-icons">people</i> Beneficiary
																Information
															</a>
														</li>

														<li class="col-md-6 nav-item" id="listitem">
															<a class="nav-link" data-toggle="tab" href="#link111"
																role="tablist">
																<i class="material-icons">money</i> Bank Information
															</a>
														</li>

													</ul>
												</div>
												<div class="col-md-12">
													<div class="tab-content">
														<div class="tab-pane active" id="link110">

															<!-- <h4 style="margin-left:10px;color: #4c55a0 !important;font-weight:600">Personal Details</h4> -->
															<div class="card-body">

																<table width="100%" class="invoicetable">
																	<tr>
																		<th align="left" valign="middle">Merchant Name
																		</th>
																		<th align="left" valign="middle">Beneficiary
																			Name
																		</th>

																	</tr>

																	<tr id="sec1">
																		<td align="left" valign="middle"></td>
																		<td align="left" valign="middle"></td>

																	</tr>
																	<tr>
																		<th align="left" valign="middle">Beneficiary
																			Code
																		</th>
																		<th align="left" valign="middle">Beneficiary
																			Type
																		</th>

																	</tr>

																	<tr id="sec2">
																		<td align="left" valign="middle"></td>
																		<td align="left" valign="middle"></td>

																	</tr>

																	<tr>
																		<th align="left" valign="middle">Email Id</th>
																		<th align="left" valign="middle">Mobile Number
																		</th>


																	</tr>
																	<tr id="sec3">
																		<td align="left" valign="middle"></td>
																		<td align="left" valign="middle"></td>

																	</tr>



																</table>

																<!-- <label for="cardMask" class="bmd-label-floating">Card Mask</label>
												 <input type="email" class="form-control" id="cardMask"> -->

															</div>





														</div>
														<div class="tab-pane" id="link111">



															<!-- <h4 style="margin-left:10px;color: #4c55a0 !important;font-weight:600">Bank Details</h4> -->
															<div class="card-body">

																<table width="100%" class="invoicetable">


																	<tr>
																		<th align="left" valign="middle">Beneficiary
																			Account
																			Number</th>
																		<th align="left" valign="middle">IFSC Code</th>



																	</tr>

																	<tr id="sec5">
																		<td align="left" valign="middle"></td>
																		<td align="left" valign="middle"></td>

																	</tr>
																	<tr>
																		<th align="left" valign="middle">Bank Name</th>
																		<th align="left" valign="middle">Amount</th>


																	</tr>
																	<tr id="sec6">
																		<td align="left" valign="middle"></td>
																		<td align="left" valign="middle"></td>

																	</tr>
																	<tr>
																		<th align="left" valign="middle">Payment Type
																		</th>
																		<th align="left" valign="middle">Txn Date</th>

																	</tr>
																	<tr id="sec7">
																		<td align="left" valign="middle"></td>
																		<td align="left" valign="middle"></td>

																	</tr>
																	<tr>
																		<th align="left" valign="middle">UTR Reference
																			Number</th>
																		<th align="left" valign="middle">Order Id</th>


																	</tr>
																	<tr id="sec8">
																		<td align="left" valign="middle"></td>
																		<td align="left" valign="middle"></td>

																	</tr>


																	<!-- <tr>
													  <th height="25" colspan="4" align="left" valign="middle"><span>Address</span></th>
			  
													</tr>
													<tr id="address6">
													  <td height="25" colspan="4" align="left" valign="middle"></td>
			  
													</tr> -->
																</table>

																<!-- <label for="cardMask" class="bmd-label-floating">Card Mask</label>
											  <input type="email" class="form-control" id="cardMask"> -->

															</div>




														</div>

													</div>
													<div class="card-footer " style="float: right;">
														<button class="btn btn-danger" id="closeBtn">Close<div
																class="ripple-container"></div></button>
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
						<!-- </div> -->


					</div>

				</div>
				<div class="post d-flex flex-column-fluid" id="kt_post">
					<!--begin::Container-->
					<div id="kt_content_container" class="container-xxl">
						<div class="row my-5">
							<div class="col">
								<div class="card">
									<div class="card-header card-header-rose card-header-text">
										<div class="card-text">
											<h4 class="card-title">Ledger Nodal</h4>
										</div>
									</div>
									<div class="card-body">
										<div class="row my-3 align-items-center">
											<div class="col-auto my-2 merchant-text">
												<div class="col-sm-6 col-md-6">
													<input type="button" id="getNodalBalance"
														value="Check Available Balance"
														class="btn btn-primary submit_btn">
												</div>
												<div class="col-sm-6 col-md-6">
													<input type="text" class="form-select form-select-solid"
														style="margin-top: 10px;" value="" id="nodalBalance" readonly>
													<span id="nodalBalanceError" style="color:red;"></span>
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>


				<!-- <div class="card ">
				<div class="card-header card-header-rose card-header-text">
					<div class="card-text">
						<h4 class="card-title">Ledger Nodal</h4>
					</div>
				</div>
				<div class="card-body ">

					<div class="container" style="display: grid;"> -->
				<!-- <div class="col-sm-6 col-md-6"></div> -->
				<!-- <div class="col-sm-6 col-md-6">
							<input type="button" id="getNodalBalance" value="Check Available Balance"
								class="btn btn-primary submit_btn">
						</div>
						<div class="col-sm-6 col-md-6">
							<input type="text" class="input-control" style="margin-top: 10px;" value=""
								id="nodalBalance" readonly>
							<span id="nodalBalanceError" style="color:red;"></span>
						</div>

					</div>
				</div>
			</div> -->
				<div class="post d-flex flex-column-fluid" id="kt_post">
					<!--begin::Container-->
					<div id="kt_content_container" class="container-xxl">
						<div class="row my-5">
							<div class="col">
								<div class="card">
									<div class="card ">
										<div class="card-header card-header-rose card-header-text">
											<div class="card-text">
												<h4 class="card-title">Search Transactions</h4>
											</div>
										</div>
										<div class="card-body ">
											<div class="container">
												<div class="row">
													<div class="form-group col-md-3 txtnew col-sm-3 col-xs-4">
														<label>Merchant</label>
														<s:select name="payId" headerKey="ALL"
															headerValue="Select Merchant"
															class="form-select form-select-solid merchantPayId payId"
															id="payId" list="listMerchant" listKey="payId"
															listValue="businessName" autocomplete="off" />
													</div>

													<div class="col-sm-6 col-lg-3">
														<label> Beneficiary Type</label>
														<s:select class="form-select form-select-solid merchantPayId"
															headerKey="ALL" headerValue="Select Beneficiary Type"
															list="@com.pay10.commons.util.BeneficiaryTypes@values()"
															listValue="name" listKey="code" id="beneType"
															autocomplete="off" value="%{nodal.beneType}" />
													</div>
													<div class="col-sm-6 col-lg-3 ">
														<label>Txn Id :</label> <br>
														<div class="txtnew ">
															<s:textfield id="txnId"
																class="form-select form-select-solid " name="txnId"
																type="text" value="" autocomplete="off"
																onkeyup="valdTxnId()" minlength="1" maxlength="16">
															</s:textfield>

														</div>
														<span id="invalid-txnId" style="color:red;"></span>
													</div>

													<div class="col-sm-6 col-lg-3 ">
														<label>Order ID :</label><br>
														<div class="txtnew">
															<s:textfield id="orderId"
																class="form-select form-select-solid " name="orderId"
																type="text" value="" autocomplete="off"
																onkeypress="return validateOrderId(event);">
															</s:textfield>
														</div>
													</div>



													<div class=" col-sm-6 col-lg-3 ">
														<label>Status :</label><br>
														<div class="txtnew">
															<s:select headerKey="" headerValue="ALL"
																class="form-select form-select-solid merchantPayId"
																list="@com.pay10.commons.util.NodalStatusType@values()"
																listValue="name" listKey="name" name="status"
																id="status" autocomplete="off" value="" />
														</div>
													</div>

													<div class="col-sm-6 col-lg-3 ">
														<label>Payment Type :</label><br>
														<div class="txtnew">
															<s:select headerKey="" headerValue="ALL"
																class="form-select form-select-solid merchantPayId"
																list="@com.pay10.commons.util.NodalPaymentTypesYesBankFT3@values()"
																listValue="name" listKey="name" name="paymentType"
																id="paymentType" autocomplete="off" value="" />
														</div>
													</div>






													<div class="col-sm-6 col-lg-3 ">
														<label>Date From :</label><br>
														<div class="txtnew">
															<s:textfield type="text" id="dateFrom" name="dateFrom"
																class="input-control" autocomplete="off"
																readonly="true" />
														</div>
													</div>

													<div class="col-sm-6 col-lg-3 ">
														<label>Date To :</label><br>
														<div class="txtnew">
															<s:textfield type="text" id="dateTo" name="dateTo"
																class="input-control" autocomplete="off"
																readonly="true" />
														</div>
													</div>

													<div class="col-sm-6 col-lg-3">

														<div class="txtnew">
															<input type="button" id="submit" value="Submit"
																class="btn btn-primary  mt-4 submit_btn">

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
				<div class="post d-flex flex-column-fluid" id="kt_post">
					<!--begin::Container-->
					<div id="kt_content_container" class="container-xxl">
						<div class="row my-5">
							<div class="col">
								<div class="card">
				<div style="overflow:scroll !important;">
					<table id="mainTable" width="100%" border="0" align="center" cellpadding="0" cellspacing="0"
						class="txnf">
						<!-- <tr>
			<td colspan="5" align="left"><h2>Search Nodal Transactions</h2></td>
		</tr> -->
						<tr>
							<td colspan="3" align="left" valign="top">
								<div class="MerchBx">

									<div class="col-md-12">

									</div>
							</td>
						</tr>
						<tr>
							<td colspan="3" align="left" valign="top">
								<div class="MerchBx">

									<div class="col-md-12">

									</div>
								</div>


							</td>
						</tr>
						<tr>
							<td colspan="5" align="left">
								<h2>&nbsp;</h2>
							</td>
						</tr>
						
						<tr>
							<td align="left" style="padding: 10px;">
								<div class="scrollD">
									<table id="txnResultDataTable" class="display nowrap" cellspacing="0" width="100%">
										<thead>
											<tr class="boxheadingsmall">
												<th style='text-align: center' class="my_class">Txn Id</th>
												<!-- <th style='text-align: center'>OID</th> -->
												<th style='text-align: center'>Order ID</th>
												<th style='text-align: center'>Merchant Name</th>
												<th style='text-align: center'>UTR Ref No.</th>
												<th style='text-align: center'>Beneficiary Name</th>
												<th style='text-align: center'>Beneficiary Code</th>
												<th style='text-align: center'>Beneficiary Account No.</th>
												<th style='text-align: center'>Payment Type</th>

												<th style='text-align: center'>Acquirer</th>
												<th style='text-align: center'>Txn Date</th>

												<th style='text-align: center'>Amount</th>
												<th style='text-align: center'>Status</th>

												<!-- <th style='text-align: center'>Comments</th> -->
												<th style='text-align: center'>Action</th>

											</tr>
										</thead>
										<tfoot>
											<tr class="boxheadingsmall">
												<th></th>

												<th></th>
												<th></th>
												<!-- <th></th> -->
												<!-- <th></th> -->
												<th></th>
												<th></th>
												<th></th>
												<th></th>
												<th></th>
												<!-- <th></th> -->
												<th></th>
												<th></th>
												<th></th>
												<th></th>
												<th></th>

											</tr>
										</tfoot>
									</table>
								</div>
							</td>
						</tr>

					</table>
				</div>
				</div>
				</div>
				</div>
				</div>
				</div>
				
				<div class="modal fade" id="transferHistoryStatus" role="dialog">
					<div class="modal-dialog">

						<!-- Modal content-->
						<div class="modal-content">
							<div class="modal-header">

							</div>
							<div class="modal-body">
								<p class="payout" id="historyPayout"></p>
							</div>
							<div class="modal-footer" id="modal_footer">
								<button type="button" class="btn btn-primary btn-round mt-4 submit_btn"
									data-dismiss="modal">Ok</button>
							</div>
						</div>

					</div>
				</div>
				<div class="modal fade" id="refreshStatusmodal" role="dialog">
					<div class="modal-dialog">

						<!-- Modal content-->
						<div class="modal-content">
							<div class="modal-header">

							</div>
							<div class="modal-body">
								<p class="payout" id="refresh"></p>
							</div>
							<div class="modal-footer" id="modal_footer">
								<button type="button" class="btn btn-primary btn-round mt-4 submit_btn"
									data-dismiss="modal">Ok</button>
							</div>
						</div>

					</div>
				</div>

				<script type="text/javascript">
					$('#getNodalBalance').click(function () {
						$('#nodalBalanceError').html("");
						$('#nodalBalance').val("");
						//$('#loader-wrapper').show();
						document.getElementById("loadingInner").style.display = "block";
						$("#getNodalBalance").prop("disabled", true);
						$.ajax({
							url: "yesBankFT3NodalBalance",
							type: "post",
							data: {
								acquirer: "YESBANKFT3"
							},
							success: function (data) {
								//	$('#loader-wrapper').hide();
								document.getElementById("loadingInner").style.display = "none";
								$("#getNodalBalance").prop("disabled", false);
								//console.log(data);
								if (data.response.toLowerCase() == 'success') {
									$('#nodalBalance').val(data.currencyCode + " " + inrFormat(data.balance));
								} else {
									$('#nodalBalanceError').html("Unable to fetch balance. Please try again later.");
								}

							},
							error: function (error) {
								//$('#loader-wrapper').hide();
								document.getElementById("loadingInner").style.display = "none";
								$("#getNodalBalance").prop("disabled", false);
								//console.log(error);
								$('#nodalBalanceError').html("Unable to fetch balance. Please try again later.");
							}
						});
					});
				</script>
				<script>
					$(document).ready(function () {

						$('#closeBtn').click(function () {
							$('#popup').hide();
						});
						$('#closeBtn1').click(function () {
							$('#popup').hide();
						});


					});
				</script>
		</body>

		</html>