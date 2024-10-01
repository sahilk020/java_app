<%@page import="com.pay10.crm.actionBeans.TransactionStatusBean"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="/struts-tags" prefix="s"%>
<html dir="ltr" lang="en-US">
<head>
<title>CB Journey Report</title>
<link href="../css/default.css" rel="stylesheet" type="text/css" />
<link rel="stylesheet" type="text/css" media="all"
	href="../css/daterangepicker-bs3.css" />
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
<link rel="stylesheet" href="../css/loader/customLoader.css" />

<script type="text/javascript">
	$(document).ready(function() {
		$(".adminMerchants").select2();

		$(function() {
			$("#dateFrom").datepicker({
				prevText : "click for previous months",
				nextText : "click for next months",
				showOtherMonths : true,
				dateFormat : 'dd-mm-yy',
				selectOtherMonths : false,
				maxDate : new Date()
			});
			$("#dateTo").datepicker({
				prevText : "click for previous months",
				nextText : "click for next months",
				showOtherMonths : true,
				dateFormat : 'dd-mm-yy',
				selectOtherMonths : false,
				maxDate : new Date()
			});
		});
		$(function() {
			var today = new Date();
			$('#dateTo').val($.datepicker.formatDate('dd-mm-yy', today));
			$('#dateFrom').val($.datepicker.formatDate('dd-mm-yy', today));

		});
	});
</script>
<script type="text/javascript">
	function submitForm() {

		debugger

		var transFrom = $.datepicker
				.parseDate('dd-mm-yy', $('#dateFrom').val());
		var transTo = $.datepicker.parseDate('dd-mm-yy', $('#dateTo').val());
		if (transFrom == null || transTo == null) {
			alert('Enter date value');
			return false;
		} else if (transFrom > transTo) {
			alert('From date must be before the to date');
			$('#dateFrom').focus();
			return false;
		} else if (transTo - transFrom > 31 * 86400000) {
			alert('No. of days can not be more than 31');
			$('#dateFrom').focus();
			return false;
		} else {
			$('#btnSubmit').prop('disabled', true);
			var merchant = $("#merchant").val();
			var pgRefNum = $("#pgRefNum").val();
			var dateFrom = $("#dateFrom").val();
			var dateTo = $("#dateTo").val();
			var cbCaseID = $("#cbCaseID").val();

			var cbCaseIDflag = 0;
			var pgRefNumflag = 0;

			if (cbCaseID != null && cbCaseID != '' && cbCaseID != 'undefined') {
				cbCaseIDflag = 1;
			}

			if (pgRefNum != null && pgRefNum != '' && pgRefNum != 'undefined') {
				pgRefNumflag = 1;
			}

			if (pgRefNumflag == 1 && cbCaseIDflag == 1) {
				alert("Please Provide CB Case ID either PG REF Number");
				$('#btnSubmit').prop('disabled', false);
				return false;
			} else {
				$.post("downloadCBJourneyReport", {
					merchant : merchant,
					pgRefNum : pgRefNum,
					dateFrom : dateFrom,
					dateTo : dateTo,
					cbCaseID : cbCaseID
				}, function(data, status) {
					$('#btnSubmit').prop('disabled', false);
					alert("Data: " + data + "\nStatus: " + status);
				});
			}
		}
	}
</script>
</head>
<body id="mainBody">
	<div class="container-fluid">
		<div class="card ">
			<div class="card-header card-header-rose card-header-text">
				<div class="card-text">
					<h4 class="card-title">CB Journey Report</h4>
				</div>
			</div>
			<div id="message" style="text-align: center;">
				<span id="msg"></span>
			</div>
			<div class="card-body ">
				<div class="container">

					<div class="row">
						<div class="col-lg-4">
							<label>Merchant</label><br>
							<div class="txtnew">
								<s:select name="payId" class="input-control adminMerchants"
									id="merchant" headerKey="All" headerValue="ALL"
									list="merchantList" listKey="payId" listValue="businessName"
									autocomplete="off" />
							</div>
						</div>
						<div class="col-lg-4">
							<label>Date From</label><br>
							<div class="txtnew">

								<s:textfield type="text" readonly="true" id="dateFrom"
									name="dateFrom" class="input-control" autocomplete="off" />
							</div>
						</div>
						<div class="col-lg-4">
							<label>Date To</label><br>
							<div class="txtnew">

								<s:textfield type="text" readonly="true" id="dateTo"
									name="dateTo" class="input-control" autocomplete="off" />
							</div>
						</div>
					</div>

					<div class="row mt-3">
						<div class="col-lg-4">
							<label>CB Case ID <sup>(c*)</sup></label><br>
							<div class="txtnew">
								<s:textfield id="cbCaseID" class="input-control" name="cbCaseID"
									type="text" value="" autocomplete="off" maxlength="16"></s:textfield>
							</div>
						</div>
						<div class="col-lg-4">
							<label>PG REF Number <sup>(c*)</sup></label><br>
							<div class="txtnew">
								<s:textfield id="pgRefNum" class="input-control" name="pgRefNum"
									type="text" value="" autocomplete="off" maxlength="16"></s:textfield>
							</div>
						</div>
					</div>

				</div>

				<div style="text-align: center; margin-top: 2%;">
					<button type="button" class="btn btn-primary" id="btnSubmit"
						onclick="submitForm()">Download</button>
				</div>
			</div>
		</div>
	</div>

</body>
</html>