<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0" />

<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
<title>Performance Report</title>
<!-- stylesheet -->
<link href="../css/jquery-ui.css" rel="stylesheet" />
<link href="../css/loader.css" rel="stylesheet" type="text/css" />
<script type="text/javascript" src="../js/jquery.min.js"></script>
<script src="../js/core/popper.min.js"></script>
<script src="../js/core/bootstrap-material-design.min.js"></script>
<script src="../js/plugins/perfect-scrollbar.jquery.min.js"></script>


<link rel="shortcut icon" href="assets/media/images/paylogo.svg" />
<!--begin::Fonts-->
<link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Inter:300,400,500,600,700" />
<!--end::Fonts-->
<!--begin::Vendor Stylesheets(used by this page)-->
<link href="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.css" rel="stylesheet" type="text/css" />
<link href="../assets/plugins/custom/datatables/datatables.bundle.css" rel="stylesheet" type="text/css" />
<!--end::Vendor Stylesheets-->
<!--begin::Global Stylesheets Bundle(used by all pages)-->
<link href="../css/select2.min.css" rel="stylesheet" />

<link href="../assets/plugins/global/plugins.bundle.css" rel="stylesheet" type="text/css" />
<link href="../assets/css/style.bundle.css" rel="stylesheet" type="text/css" />
<%-- <script src="../assets/plugins/global/plugins.bundle.js"></script>
<script src="../assets/js/scripts.bundle.js"></script> --%>

<script src="../js/jquery-1.9.0.js"></script>
<script src="../js/jquery-migrate-1.2.1.js"></script>
<script src="../js/jquery-ui.min.js" type="text/javascript"></script>
<script src="../js/core/popper.min.js"></script>
<script src="../js/core/bootstrap-material-design.min.js"></script>
<script src="../js/plugins/perfect-scrollbar.jquery.min.js"></script>
<!-- Control Center for Material Dashboard: parallax effects, scripts for the example pages etc -->
<script src="../js/material-dashboard.js?v=2.1.0" type="text/javascript"></script>
<!-- searchable select option -->

<!-- highcharts -->
<script src="../js/highcharts.js"></script>
<script src="../js/jquery.select2.js" type="text/javascript"></script>

<script src="../assets/js/scripts.bundle.js"></script>

<script src="../assets/plugins/global/plugins.bundle.js"></script>

<script type="text/javascript">
	$(document).ready(function() {

		// Initialize select2

/* 		document.getElementById("loading").style.display = "none"
 */
		$(function() {
			$("#dateFrom").flatpickr({
                            maxDate: new Date(),
                            dateFormat: "d-m-Y",
                            defaultDate: "today",
                            defaultDate: "today",
                        });
                        $("#dateTo").flatpickr({
                            maxDate: new Date(),
                            dateFormat: "d-m-Y",
                            defaultDate: "today",
                            maxDate: new Date()
                        });
		});

		$(function() {
			// var today = new Date();
			// $('#dateTo').val(today);
			// $('#dateFrom').val(today);
			statistics();
			/*renderTable();*/
		});
	})
</script>

<script>
	var expanded = false;
	function showCheckboxes(e) {
		var checkboxes = document.getElementById("checkboxes");
		if (!expanded) {
			checkboxes.style.display = "block";
			expanded = true;
		} else {
			checkboxes.style.display = "none";
			expanded = false;
		}
		e.stopPropagation();
	}

	function getCheckBoxValue() {
		var allInputCheckBox = document.getElementsByClassName("myCheckBox");

		var allSelectedAquirer = [];
		for (var i = 0; i < allInputCheckBox.length; i++) {

			if (allInputCheckBox[i].checked) {
				allSelectedAquirer.push(allInputCheckBox[i].value);
			}
		}
		document.getElementById('selectBox').setAttribute('title',
				allSelectedAquirer.join());
		if (allSelectedAquirer.join().length > 28) {
			var res = allSelectedAquirer.join().substring(0, 27);
			document.querySelector("#selectBox option").innerHTML = res
					+ '...............';
		} else if (allSelectedAquirer.join().length == 0) {
			document.querySelector("#selectBox option").innerHTML = 'ALL';
		} else {
			document.querySelector("#selectBox option").innerHTML = allSelectedAquirer
					.join();
		}
	}
</script>

<script type="text/javascript">
	$(document).ready(function() {
		$(document).click(function() {
			expanded = false;
			$('#checkboxes').hide();
		});
		$('#checkboxes').click(function(e) {
			e.stopPropagation();
		});
	});
</script>

<script type="text/javascript">
	function sendPerformanceSms(smsData) {

		if (confirm('Are you sure you want to Send SMS')) {

		} else {
			event.stopPropagation;
			return false;
		}
		var acquirer = [];
		var inputElements = document.getElementsByName('acquirer');
		for (var i = 0; inputElements[i]; ++i) {
			if (inputElements[i].checked) {
				acquirer.push(inputElements[i].value);

			}
		}
		var acquirerString = acquirer.join();

		var merchantEmailId = document.getElementById("merchants").value;
		var sel = document.getElementById("merchants");
		var merchantName = sel.options[sel.selectedIndex].text;
		var dateFrom = document.getElementById("dateFrom").value;
		var dateTo = document.getElementById("dateTo").value;
		var paymentMethods = document.getElementById("paymentMethods").value;
		var smsParam = smsData;

		if (merchantEmailId == '') {
			merchantEmailId = 'ALL'
		}
		if (paymentMethods == '') {
			paymentMethods = 'ALL'
		}

		if (acquirer == '') {
			acquirer = 'ALL'
		}

		if (merchantEmailId == 'ALL') {

			alert("Please select a merchant to send SMS ");
			return false;
		}

		if (smsData == 'capturedData') {

			alert("Sending captured data to merchant " + merchantName + " for "
					+ dateFrom + " to " + dateTo);
		} else if (smsData == 'capturedData') {

			alert("Sending Captured data  " + merchantName + " for " + dateFrom
					+ " to " + dateTo);
		} else {
			alert("Sending settled data to merchant " + merchantName + " for "
					+ dateFrom + " to " + dateTo);

		}
		var token = document.getElementsByName("token")[0].value;
/* 		document.getElementById("loading").style.display = "block"
 */
		$
				.ajax({
					url : "sendPerformanceSmsAction",
					timeout : 0,
					type : "POST",
					data : {
						paymentMethods : paymentMethods,
						dateFrom : dateFrom,
						dateTo : dateTo,
						merchantEmailId : merchantEmailId,
						token : token,
						acquirer : acquirerString,
						smsParam : smsParam,
					},
					success : function(data) {
/* 						document.getElementById("loading").style.display = "none"
 */						var response = ((data["Invalid request"] != null) ? (data["Invalid request"].response[0])
								: (data.response));
						if (null != response) {
							alert(response);
						}
					},
					error : function(data) {
/* 						document.getElementById("loading").style.display = "none"
 */					}
				});

	}

	function intChart(val1, val2, val3, val4, val5) {
		Highcharts.chart('chartBox', {
			chart : {
				plotBackgroundColor : null,
				plotBorderWidth : null,
				plotShadow : false,
				type : 'pie'
			},
			title : {
				text : 'Payment Types Comparison'
			},
			tooltip : {
				pointFormat : '{series.name}: <b>{point.percentage:.1f}%</b>'
			},
			plotOptions : {
				pie : {
					allowPointSelect : true,
					cursor : 'pointer',
					dataLabels : {
						enabled : false
					},
					showInLegend : true
				}
			},
			series : [ {
				name : 'Share',
				colorByPoint : true,
				data : [ {
					name : 'Credit Card',
					y : parseFloat(val1),
					sliced : false,
					selected : true
				}, {
					name : 'Debit Card',
					y : parseFloat(val2)
				}, {
					name : 'UPI',
					y : parseFloat(val3)
				}, {
					name : 'Wallet',
					y : parseFloat(val4)
				}, {
					name : 'Net Banking',
					y : parseFloat(val5)
				} ]
			} ]
		}, function(chart) { // on complete

			chart.renderer.text('', 140, 120).css({
				color : '#4572A7',
				fontSize : '13px'
			}).add();

		});
	}

	function intChartPaymentWise(val1, val2, val3, val4, val5, val6, val7) {
		Highcharts.chart('chartBox', {
			chart : {
				plotBackgroundColor : null,
				plotBorderWidth : null,
				plotShadow : false,
				type : 'pie'
			},
			title : {
				text : 'Payment Types Comparison'
			},
			tooltip : {
				pointFormat : '{series.name}: <b>{point.percentage:.1f}%</b>'
			},
			plotOptions : {
				pie : {
					allowPointSelect : true,
					cursor : 'pointer',
					dataLabels : {
						enabled : false
					},
					showInLegend : true
				}
			},
			series : [ {
				name : 'Share',
				colorByPoint : true,
				data : [ {
					name : 'Captured',
					y : parseFloat(val1),
					sliced : false,
					selected : true
				}, {
					name : 'Failed',
					y : parseFloat(val2)
				}, {
					name : 'Cancelled',
					y : parseFloat(val3)
				}, {
					name : 'Invalid',
					y : parseFloat(val4)
				} ]
			} ]
		}, function(chart) { // on complete

			chart.renderer.text('', 140, 120).css({
				color : '#4572A7',
				fontSize : '13px'
			}).add();

		});
	}

	function statistics() {

		var acquirer = [];
		var inputElements = document.getElementsByName('acquirer');
		for (var i = 0; inputElements[i]; ++i) {
			if (inputElements[i].checked) {
				acquirer.push(inputElements[i].value);

			}
		}
		var acquirerString = acquirer.join();

		var merchantEmailId = document.getElementById("merchants").value;
		var dateFrom = document.getElementById("dateFrom").value;
		var dateTo = document.getElementById("dateTo").value;
		var paymentMethods = document.getElementById("paymentMethods").value;

		if (merchantEmailId == '') {
			merchantEmailId = 'ALL'
		}
		if (paymentMethods == '') {
			paymentMethods = 'ALL'
		}

		if (acquirer == '') {
			acquirer = 'ALL'
		}

/* 		document.getElementById("loading").style.display = "block";
 */		var token = document.getElementsByName("token")[0].value;
		$
				.ajax({
					url : "analyticsDataAction",
					timeout : 0,
					type : "POST",
					data : {
						paymentMethods : paymentMethods,
						dateFrom : dateFrom,
						dateTo : dateTo,
						merchantEmailId : merchantEmailId,
						token : token,
						acquirer : acquirerString,
					},
					success : function(data) {

/* 						document.getElementById("loading").style.display = "none";
 */
						document.getElementById("totalTxnCount").innerHTML = inrFormat(data.analyticsData.totalTxnCount);
						document.getElementById("successTxnCount").innerHTML = inrFormat(data.analyticsData.successTxnCount);
						document.getElementById("successTxnPercent").innerHTML = data.analyticsData.successTxnPercent;
						document.getElementById("avgTkt").innerHTML = inrFormat(data.analyticsData.avgTkt);

						document.getElementById("CCTxnPercent").innerHTML = data.analyticsData.CCTxnPercent;
						document.getElementById("DCTxnPercent").innerHTML = data.analyticsData.DCTxnPercent;
						document.getElementById("UPTxnPercent").innerHTML = data.analyticsData.UPTxnPercent;
						document.getElementById("NBTxnPercent").innerHTML = data.analyticsData.NBTxnPercent;
						document.getElementById("WLTxnPercent").innerHTML = data.analyticsData.WLTxnPercent;

						document.getElementById("TotalTransactionCount").innerHTML = data.analyticsData.successTxnCount;
						document.getElementById("TotalPercentageShare").innerHTML = data.analyticsData.overallTotalPercent;
						document.getElementById("TotalTransactionAmount").innerHTML = inrFormat(data.analyticsData.totalCapturedTxnAmount);

						document.getElementById("CCTotalCount").innerHTML = data.analyticsData.totalCCCapturedCount;
						document.getElementById("totalCCSuccessTxnPercent").innerHTML = data.analyticsData.totalCCSuccessTxnPercent;
						document.getElementById("totalCCFailedTxnPercent").innerHTML = data.analyticsData.totalCCFailedTxnPercent;
						document.getElementById("totalCCCancelledTxnPercent").innerHTML = data.analyticsData.totalCCCancelledTxnPercent;
						document.getElementById("totalCCInvalidTxnPercent").innerHTML = data.analyticsData.totalCCInvalidTxnPercent;

						document.getElementById("DCTotalCount").innerHTML = data.analyticsData.totalDCCapturedCount;
						document.getElementById("totalDCSuccessTxnPercent").innerHTML = data.analyticsData.totalDCSuccessTxnPercent;
						document.getElementById("totalDCFailedTxnPercent").innerHTML = data.analyticsData.totalDCFailedTxnPercent;
						document.getElementById("totalDCCancelledTxnPercent").innerHTML = data.analyticsData.totalDCCancelledTxnPercent;
						document.getElementById("totalDCInvalidTxnPercent").innerHTML = data.analyticsData.totalDCInvalidTxnPercent;

						document.getElementById("UPTotalCount").innerHTML = data.analyticsData.totalUPCapturedCount;
						document.getElementById("totalUPSuccessTxnPercent").innerHTML = data.analyticsData.totalUPSuccessTxnPercent;
						document.getElementById("totalUPFailedTxnPercent").innerHTML = data.analyticsData.totalUPFailedTxnPercent;
						document.getElementById("totalUPCancelledTxnPercent").innerHTML = data.analyticsData.totalUPCancelledTxnPercent;
						document.getElementById("totalUPInvalidTxnPercent").innerHTML = data.analyticsData.totalUPInvalidTxnPercent;

						document.getElementById("WLTotalCount").innerHTML = data.analyticsData.totalWLCapturedCount;
						document.getElementById("totalWLSuccessTxnPercent").innerHTML = data.analyticsData.totalWLSuccessTxnPercent;
						document.getElementById("totalWLFailedTxnPercent").innerHTML = data.analyticsData.totalWLFailedTxnPercent;
						document.getElementById("totalWLCancelledTxnPercent").innerHTML = data.analyticsData.totalWLCancelledTxnPercent;
						document.getElementById("totalWLInvalidTxnPercent").innerHTML = data.analyticsData.totalWLInvalidTxnPercent;

						document.getElementById("NBTotalCount").innerHTML = data.analyticsData.totalNBCapturedCount;
						document.getElementById("totalNBSuccessTxnPercent").innerHTML = data.analyticsData.totalNBSuccessTxnPercent;
						document.getElementById("totalNBFailedTxnPercent").innerHTML = data.analyticsData.totalNBFailedTxnPercent;
						document.getElementById("totalNBCancelledTxnPercent").innerHTML = data.analyticsData.totalNBCancelledTxnPercent;
						document.getElementById("totalNBInvalidTxnPercent").innerHTML = data.analyticsData.totalNBInvalidTxnPercent;

						document.getElementById("captured").innerHTML = data.analyticsData.captured;
						document.getElementById("failed").innerHTML = data.analyticsData.failed;
						document.getElementById("cancelled").innerHTML = data.analyticsData.cancelled;
						document.getElementById("invalid").innerHTML = data.analyticsData.invalid;

						document.getElementById("capturedPercent").innerHTML = data.analyticsData.capturedPercent;
						document.getElementById("failedPercent").innerHTML = data.analyticsData.failedPercent;
						document.getElementById("cancelledPercent").innerHTML = data.analyticsData.cancelledPercent;
						document.getElementById("invalidPercent").innerHTML = data.analyticsData.invalidPercent;

						document.getElementById("capturedTotalAmount").innerHTML = data.analyticsData.capturedTotalAmount;
						document.getElementById("failedTotalAmount").innerHTML = data.analyticsData.failedTotalAmount;
						document.getElementById("cancelledTotalAmount").innerHTML = data.analyticsData.cancelledTotalAmount;
						document.getElementById("invalidTotalAmount").innerHTML = data.analyticsData.invalidTotalAmount;

						document.getElementById("unknownTxnCount").innerHTML = data.analyticsData.unknownTxnCount;

						document.getElementById("totalCCTxn").innerHTML = data.analyticsData.totalCCTxn;
						document.getElementById("totalDCTxn").innerHTML = data.analyticsData.totalDCTxn;
						document.getElementById("totalUPTxn").innerHTML = data.analyticsData.totalUPTxn;
						document.getElementById("totalWLTxn").innerHTML = data.analyticsData.totalWLTxn;
						document.getElementById("totalNBTxn").innerHTML = data.analyticsData.totalNBTxn;

						document.getElementById("ccTotalAmount").innerHTML = inrFormat(data.analyticsData.totalCCTxnAmount);
						document.getElementById("dcTotalAmount").innerHTML = inrFormat(data.analyticsData.totalDCTxnAmount);
						document.getElementById("upTotalAmount").innerHTML = inrFormat(data.analyticsData.totalUPTxnAmount);
						document.getElementById("wlTotalAmount").innerHTML = inrFormat(data.analyticsData.totalWLTxnAmount);
						document.getElementById("nbTotalAmount").innerHTML = inrFormat(data.analyticsData.totalNBTxnAmount);

						document.getElementById("merchantPgRatio").innerHTML = data.analyticsData.merchantPgRatio;
						document.getElementById("acquirerPgRatio").innerHTML = data.analyticsData.acquirerPgRatio;

						if (paymentMethods == 'ALL') {
							intChartPaymentWise("0.00", "0.00", "0.00", "0.00",
									"0.00", "0.00", "0.00");
							intChart(data.analyticsData.CCTxnPercent,
									data.analyticsData.DCTxnPercent,
									data.analyticsData.UPTxnPercent,
									data.analyticsData.WLTxnPercent,
									data.analyticsData.NBTxnPercent);
						} else {
							intChart("0.00", "0.00", "0.00", "0.00", "0.00");
							intChartPaymentWise(
									data.analyticsData.capturedPercent,
									data.analyticsData.failedPercent,
									data.analyticsData.cancelledPercent,
									data.analyticsData.invalidPercent);
						}
/* 						document.getElementById("loading").style.display = "none"
 */
					},
					error : function(data) {
/* 						document.getElementById("loading").style.display = "none"
 */					}
				});

	}
</script>

<script type="text/javascript">
	var expanded = false;
</script>

<script type="text/javascript">
	$(document)
			.ready(
					function() {
						//checkbox click event
						$(document).click(function() {
							expanded = false;
							$('#checkboxes').hide();
						});
						$('#checkboxes').click(function(e) {
							e.stopPropagation();
						});
						// Initialize select2
			
						//button click
						$('button')
								.click(
										function() {
											var dateFrom = document
													.getElementById('dateFrom').value
													.split('-'), dateTo = document
													.getElementById('dateTo').value
													.split('-'), myDateFrom = new Date(
													dateFrom[2], dateFrom[1],
													dateFrom[0]), //Year, Month, Date  
											myDateTo = new Date(dateTo[2],
													dateTo[1], dateTo[0]), //Year, Month, Date 
											oneDay = 24 * 60 * 60 * 1000; // hours*minutes*seconds*milliseconds

											if (myDateTo >= myDateFrom) {
												var diffDays = Math
														.round(Math
																.abs((myDateFrom
																		.getTime() - myDateTo
																		.getTime())
																		/ (oneDay)));
												if (diffDays > 31) {
													alert('No. of days can not be more than 31');
												} else {

													if ($('#paymentMethods')
															.val() == "") {
														$('#showForParticular')
																.hide();
														$('#showForAll').show();
														$(
																'#PaymentTypePerformance')
																.show();
													} else {
														$('#showForAll').hide();
														$(
																'#PaymentTypePerformance')
																.hide();
														$('#showForParticular')
																.show();
													}

													statistics();

												}
											} else {
												alert("'Date From' must be before the 'Date To'.");
											}

										});
					});
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
</script>
<style>



.float-right{
	float: right;
}
.float-left{
	float: left;
}
#showForParticular{
		display:none;
}
#checkboxes {
	  display: none;
	  border: 1px #dadada solid;
	  height: auto;
	  max-height:300px;
	  overflow-y: scroll;
	  position:absolute;
	  background:#fff;
	  z-index:10;
	  margin-left:5px;
	  width: 285px;
	 
}
#checkboxes label {
	  width: 74%;
}
#checkboxes input {
	  width:18%;
}
div#highcharts-2 {
	height: 473px !important;
}
#highcharts-2 .highcharts-background{
	height: 455px !important;
}
.select2-container--default .select2-selection--single {
    background-color: #fdf8f8;
    border: 1px solid #fde9c0;
	padding: 2.2px;
	border-radius: unset;
}
.select2-dropdown{
	z-index: 1 !important;
}
/* div#checkboxes{
	width:28%;
} */
.opacity1{
	border: unset;
	opacity: 0;
	visibility:hidden;
	box-shadow:unset;
}

.overSelect {
	position: absolute;
	left: 0;
	right: 0;
	top: 0;
	bottom: 0;
	}
.selectBox {
position:relative;
}
</style>


</head>

<body id="mainBody">
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
					<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">Performance Report</h1>
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
						<li class="breadcrumb-item text-muted">Analytics</li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item"><span class="bullet bg-gray-200 w-5px h-2px"></span></li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item text-dark">Performance Report</li>
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
<!-- 	<div id="loading" style="text-align: center;">
		<img id="loading-image" style="width: 70px; height: 70px;"
			src="../image/sand-clock-loader.gif" alt="Sending SMS..." />
	</div> -->

	<div>
		<!-- <h2>Performance Report</h2> -->
		<div class="">
			<div class="col-md-12">
				<div class="card">
					<div class="card-body ">
						<div class="container">
							<div class="row  mb-4 g-9">

								<div class="col-sm-12 col-md-4 col-lg-4">
									<label class="d-flex align-items-center fs-6 fw-bold mb-2">Date From</label>
									
										<s:textfield type="text" id="dateFrom" name="dateFrom"
											class="form-control form-control-solid flatpickr-input" autocomplete="off" readonly="true" />
								
								</div>
								<div class="col-sm-12 col-md-4 col-lg-4">
									<label class="d-flex align-items-center fs-6 fw-bold mb-2">Date To</label>

									
										<s:textfield type="text" id="dateTo" name="dateTo"
											class="form-control form-control-solid flatpickr-input" autocomplete="off" readonly="true"
											/>
									
								</div>
								<div class="col-sm-12 col-md-4 col-lg-4">
									<label class="d-flex align-items-center fs-6 fw-bold mb-2">Acquirer</label>
									
										<div class="selectBox" id="selectBox"
											onclick="showCheckboxes(event)" title="dummy Title">
											<select class="form-control form-control-solid">
												<option>ALL</option>
											</select>
											<div class="overSelect"></div>
										
										<div id="checkboxes" onclick="getCheckBoxValue()">
											<s:checkboxlist headerKey="ALL" headerValue="ALL"
												list="@com.pay10.commons.util.AcquirerTypeUI@values()"
												id="acquirer" class="myCheckBox" name="acquirer"
												value="acquirer" listValue="name" listKey="code" />
										</div>
									</div>
								</div>
								</div>
								<div class="row  mb-4 g-9">
								<div class="col-sm-12 col-md-4 col-lg-4">
									<label class="d-flex align-items-center fs-6 fw-bold mb-2">Merchant</label>
									
										<s:if
											test="%{#session.USER.UserType.name()=='ADMIN' || #session.USER.UserType.name()=='SUBADMIN' || #session.USER_TYPE.name()=='SUPERADMIN'}">
											<s:select name="merchant" class="form-select form-select-solid"
												id="merchants" headerKey="" headerValue="ALL"
												list="merchantList" listKey="emailId"
												listValue="businessName" autocomplete="off" />
												
										</s:if>
										<s:else>
											<s:select name="merchant" class="form-select form-select-solid"
												id="merchants" headerKey="" headerValue="ALL"
												list="merchantList" listKey="emailId"
												listValue="businessName" autocomplete="off" />
										</s:else>
									

								</div>

								<div class="col-sm-12 col-md-4 col-lg-4">
									<label class="d-flex align-items-center fs-6 fw-bold mb-2">Payment Type</label>
									
										<s:select headerKey="" headerValue="ALL" class="form-select form-select-solid"
											list="@com.pay10.commons.util.PaymentTypeUI@values()"
											listValue="name" listKey="code" name="paymentMethod"
											id="paymentMethods" autocomplete="off" value="" />
									

								</div>



								<div class="col-sm-12 col-md-4 col-lg-4">

									
										<button id="submit1" disabled="disabled" class="submit_btn btn w-sm-100 w-md-100 btn-primary mt-7">Submit</button>

								</div>






							</div>
						</div>


					</div>
				</div>
			</div>

			<!-- <div class='baseClass'>
			<button id="submit1" class="submitClass">Submit</button>
		</div> -->


			<div class="box1 row mt-4 mb-4">
				<div class="col-lg-3 col-md-6 col-sm-12">
					<div class="card bg-c-blue order-card p-3">
						<div class="card-block">
							<p>Total Transactions (Hits)</p>
							<h2 class="text-right"><i class="fa fa-check fa-1x f-left"></i><span class="float-right"><p id="totalTxnCount" class="media-heading"></p></span></h2>
							
						</div>
					</div>
				</div>
				<div class="col-lg-3 col-md-6 col-sm-12">
					<div class="card bg-c-blue order-card p-3">
						<div class="card-block">
							<p>Total Transactions (Captured)</p>
							<h2 class="text-right"><i class="fa fa-money-bill fa-1x f-left"></i><span class="float-right"><p id="successTxnCount" class="media-heading"></p></span></h2>
							
						</div>
					</div>
				</div>
				<div class="col-lg-3 col-md-6 col-sm-12">
					<div class="card bg-c-blue order-card p-3">
						<div class="card-block">
							<p>Captured Percentage</p>
							<h2 class="text-right"><i class="fa fa-percent fa-1x f-left"></i><span class="float-right"><p id="successTxnPercent" class="media-heading"></p>
							</span></h2>
							
						</div>
					</div>
				</div>
				<div class="col-lg-3 col-md-6 col-sm-12">
					<div class="card bg-c-blue order-card p-3">
						<div class="card-block">
							<p>Average Ticket Size</p>
							<h2 class="text-right"><i class="fa fa-ticket fa-1x f-left"></i><span class="float-right"><p id="avgTkt" class="media-heading"></p></span></h2>
							
						</div>
					</div>
				</div>
				</div>
			
			<div class="row box1">
				<div class="col-lg-3 col-md-6 col-sm-12" style="display: none;">
					<div class="card card-stats">
						<div class="card-header card-header-rose card-header-icon">
							<div class="card-icon">
								<i class="fa fa-check fa-5x"></i>
							</div>
							<p class="card-category">Merchant - PG Performance</p>
							<h3 class="card-title">
								<p id="merchantPgRatio" class="media-heading"></p>
							</h3>
						</div>
						<div class="card-footer">
							<div class="stats">
								<i class="fa fa-check">Merchant - PG Performance</i>
								<!-- <i class="material-icons">local_offer</i> Tracked from Google Analytics -->
							</div>
						</div>
					</div>
				</div>
				<!-- <div class="col-sm-6 col-lg-3">
				<div class="card card-stats">
				  <div class="card-header card-header-info card-header-icon">
					<div class="card-icon">
						<img class="media-object" src="../image/total-icon.png" alt="total">
					</div>
					<p class="card-category">Merchant - PG Performance</p>
					<h3 class="card-title">	<p id = "merchantPgRatio" class="media-heading"></p></h3>
				  </div>
				</div>
			  </div> -->
				<!-- <div class="col-sm-3">
				<div class="media">
				  <div class="media-left blueBg">
					
					  <img class="media-object" src="../image/total-icon.png" alt="total">
					
				  </div>
				  <div class="media-body">
					<p class="media-heading">Merchant - PG Performance</p>
					<p id = "merchantPgRatio" class="media-heading"></p>
					
				  </div>
				</div>
			</div> -->
				<div class="col-lg-3 col-md-6 col-sm-12" style="display: none;">
					<div class="card card-stats">
						<div class="card-header card-header-success card-header-icon">
							<div class="card-icon">
								<i class="fa fa-reply-all fa-5x"></i>
							</div>
							<p class="card-category">Acquirer - PG Performance</p>
							<h3 class="card-title">
								<p id="acquirerPgRatio" class="media-heading"></p>
							</h3>
						</div>
						<div class="card-footer">
							<div class="stats">
								<i class="fa fa-reply-all ">Acquirer - PG Performance</i>

							</div>
						</div>
					</div>
				</div>
				<!-- <div class="col-sm-6 col-lg-3">
				<div class="card card-stats">
				  <div class="card-header card-header-success card-header-icon">
					<div class="card-icon">
						<img class="media-object" src="../image/success-icon.png" alt="total">
					</div>
					<p class="card-category">Acquirer - PG Performance</p>
					<h3 class="card-title">	<p id = "acquirerPgRatio" class="media-heading"></p></h3>
				  </div>
				</div>
			  </div> -->
				<!-- <div class="col-sm-3">
				<div class="media">
				  <div class="media-left orangeBg">
					  <img class="media-object" src="../image/success-icon.png" alt="total">
				  </div>
				  <div class="media-body">
					<p class="media-heading">Acquirer - PG Performance</p>
					<p id = "acquirerPgRatio" class="media-heading"></p>
				  </div>
				</div>
			</div> -->

			</div>

			<div class="row box3 mt-4">
					<div class="col-md-6">
						<div class="card">
							<div class="card-body">
						<div class="chartBox" id="chartBox"></div>
					</div>
				</div>
					</div>
					<div class="col-md-6">
						<div class="card">
							<div class="card-body">
						<div class="cardTypeTxnDetails" id="showForAll">
							<table class="table table-striped table-row-bordered gy-5 gs-7">
								<thead>
									<tr class="fw-bold fs-6 text-gray-800">
										<th>Payment Type</th>
										<th>Transactions Count</th>
										<th>Percentage Share</th>
										<th>Total Amount</th>

									</tr>
								</thead>
								<tbody>
									<tr class="fw-bold fs-6 text-gray-800">
										<td>Credit Card</td>
										<td><p id="CCTotalCount" class="media-heading"></p></td>
										<td><p id="CCTxnPercent" class="media-heading"></p></td>
										<td><p id="ccTotalAmount" class="media-heading"></p></td>
									</tr>
									<tr class="fw-bold fs-6 text-gray-800">
										<td>Debit Card</td>
										<td><p id="DCTotalCount" class="media-heading"></p></td>
										<td><p id="DCTxnPercent" class="media-heading"></p></td>
										<td><p id="dcTotalAmount" class="media-heading"></p></td>
									</tr>
									<tr class="fw-bold fs-6 text-gray-800">
										<td>UPI</td>
										<td><p id="UPTotalCount" class="media-heading"></p></td>
										<td><p id="UPTxnPercent" class="media-heading"></p></td>
										<td><p id="upTotalAmount" class="media-heading"></p></td>
									</tr>
									<tr class="fw-bold fs-6 text-gray-800">
										<td>Wallet</td>
										<td><p id="WLTotalCount" class="media-heading"></p></td>
										<td><p id="WLTxnPercent" class="media-heading"></p></td>
										<td><p id="wlTotalAmount" class="media-heading"></p></td>
									</tr>
									<tr class="fw-bold fs-6 text-gray-800">
                                    	<td>Net Banking</td>
									    <td><p id="NBTotalCount" class="media-heading"></p></td>
									    <td><p id="NBTxnPercent" class="media-heading"></p></td>
									    <td><p id="nbTotalAmount" class="media-heading"></p></td>
									</tr>

									<tr class="fw-bold fs-6 text-gray-800">
										<td>Total</td>
										<td><p id="TotalTransactionCount" class="media-heading"></p></td>
										<td><p id="TotalPercentageShare" class="media-heading"></p></td>
										<td><p id="TotalTransactionAmount" class="media-heading"></p></td>
									</tr>

								</tbody>
							</table>
						</div>
						<div class="cardTypeTxnDetails" id="showForParticular">
							<table class="table table-striped table-row-bordered gy-5 gs-7">
								<thead>
									<tr class="fw-bold fs-6 text-gray-800">
										<th>Status</th>
										<th>Transactions (Count)</th>
										<th>Transactions (%)</th>
										<th>Total Amount</th>
									</tr>
								</thead>
								<tbody>
									<tr class="fw-bold fs-6 text-gray-800">
										<td>Captured</td>
										<td><p id="captured" class="media-heading"></p></td>
										<td><p id="capturedPercent" class="media-heading"></p></td>
										<td><p id="capturedTotalAmount" class="media-heading"></p></td>
									</tr>
									<tr class="fw-bold fs-6 text-gray-800">
										<td>Failed</td>
										<td><p id="failed" class="media-heading"></p></td>
										<td><p id="failedPercent" class="media-heading"></p></td>
										<td><p id="failedTotalAmount" class="media-heading"></p></td>
									</tr>
									<tr class="fw-bold fs-6 text-gray-800">
										<td>Cancelled</td>
										<td><p id="cancelled" class="media-heading"></p></td>
										<td><p id="cancelledPercent" class="media-heading"></p></td>
										<td><p id="cancelledTotalAmount" class="media-heading"></p></td>
									</tr>
									<tr class="fw-bold fs-6 text-gray-800">
										<td>Invalid</td>
										<td><p id="invalid" class="media-heading"></p></td>
										<td><p id="invalidPercent" class="media-heading"></p></td>
										<td><p id="invalidTotalAmount" class="media-heading"></p></td>
									</tr>

								</tbody>
							</table>
						</div>
						</div>
						</div>
					</div>
				
			</div>
			<!-- <div id="PaymentTypePerformance">
		<h3>Payment Type Performance</h3> -->
			<div class="row box3">
				<div class="col-md-12 mt-4">
					<div class="card">
						<div class="card-body">
					<div id="PaymentTypePerformance">
						<h3>Payment Type Performance</h3>

						<div class="paymentPerfomance">
							<table class="table table-striped table-row-bordered gy-5 gs-7">
								<thead>
									<tr class="fw-bold fs-6 text-gray-800">
										<th>Payment Type</th>
										<th>Total Txn</th>
										<th>Captured(%)</th>
										<th>Failed(%)</th>
										<th>Cancelled(%)</th>
										<th>Invalid(%)</th>
									</tr>
								</thead>
								<tbody>
									<tr class="fw-bold fs-6 text-gray-800">
										<td>Credit Card</td>
										<td><p id="totalCCTxn" class="media-heading"></p></td>
										<td><p id="totalCCSuccessTxnPercent"
												class="media-heading"></p></td>
										<td><p id="totalCCFailedTxnPercent" class="media-heading"></p></td>
										<td><p id="totalCCCancelledTxnPercent"
												class="media-heading"></p></td>
										<td><p id="totalCCInvalidTxnPercent"
												class="media-heading"></p></td>
									</tr>
									<tr class="fw-bold fs-6 text-gray-800">
										<td>Debit Card</td>
										<td><p id="totalDCTxn" class="media-heading"></p></td>
										<td><p id="totalDCSuccessTxnPercent"
												class="media-heading"></p></td>
										<td><p id="totalDCFailedTxnPercent" class="media-heading"></p></td>
										<td><p id="totalDCCancelledTxnPercent"
												class="media-heading"></p></td>
										<td><p id="totalDCInvalidTxnPercent"
												class="media-heading"></p></td>
									</tr>
									<tr class="fw-bold fs-6 text-gray-800">
										<td>UPI</td>
										<td><p id="totalUPTxn" class="media-heading"></p></td>
										<td><p id="totalUPSuccessTxnPercent"
												class="media-heading"></p></td>
										<td><p id="totalUPFailedTxnPercent" class="media-heading"></p></td>
										<td><p id="totalUPCancelledTxnPercent"
												class="media-heading"></p></td>
										<td><p id="totalUPInvalidTxnPercent"
												class="media-heading"></p></td>
									</tr>
									<tr class="fw-bold fs-6 text-gray-800">
										<td>Wallet</td>
										<td><p id="totalWLTxn" class="media-heading"></p></td>
										<td><p id="totalWLSuccessTxnPercent"
												class="media-heading"></p></td>
										<td><p id="totalWLFailedTxnPercent" class="media-heading"></p></td>
										<td><p id="totalWLCancelledTxnPercent"
												class="media-heading"></p></td>
										<td><p id="totalWLInvalidTxnPercent"
												class="media-heading"></p></td>
									</tr>

									<tr class="fw-bold fs-6 text-gray-800">
										<td>Net Banking</td>
										<td><p id="totalNBTxn" class="media-heading"></p></td>
										<td><p id="totalNBSuccessTxnPercent"
												class="media-heading"></p></td>
										<td><p id="totalNBFailedTxnPercent" class="media-heading"></p></td>
										<td><p id="totalNBCancelledTxnPercent"
												class="media-heading"></p></td>
										<td><p id="totalNBInvalidTxnPercent"
												class="media-heading"></p></td>
									</tr>


									<tr class="fw-bold fs-6 text-gray-800">
										<td>Others</td>
										<td><p id="unknownTxnCount" class="media-heading"></p></td>
										<td></td>
										<td></td>
										<td></td>
										<td></td>
										<!-- <td></td>
										<td></td>
										<td></td> -->


									</tr>
								</tbody>
							</table>
						</div>
					</div>
				</div>
			</div>

				</div>
			</div>
			<!-- </div> -->

		</div>
	</div>
	</div>
	</div>
</div>
	<script>
	$(document).ready(function() {
		if ($('#analyticsPerfomanceReport').hasClass("active")) {
			var menuAccess = document.getElementById("menuAccessByROLE").value;
			var accessMap = JSON.parse(menuAccess);
			var access = accessMap["analyticsPerfomanceReport"];
			if (access.includes("View")) {
				$("#submit1").removeAttr("disabled");
			}
		}
	});
	</script>

	<script type="text/javascript">
		// Initialize select2
		$(".form-select").select2();
		$("#dateFrom").flatpickr({
                            maxDate: new Date(),
                            dateFormat: "d-m-Y",
                            defaultDate: "today",
                            defaultDate: "today",
                        });
                        $("#dateTo").flatpickr({
                            maxDate: new Date(),
                            dateFormat: "d-m-Y",
                            defaultDate: "today",
                            maxDate: new Date()
                        });
	</script>
</body>
</html>
