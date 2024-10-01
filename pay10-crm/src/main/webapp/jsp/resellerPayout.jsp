<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="/struts-tags" prefix="s"%>
<html dir="ltr" lang="en-US">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Reseller Payout Details</title>
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
<script src="../js/jquery.min.js" type="text/javascript"></script>
<script src="../js/resellerPayout.js"></script>
<%-- <script src="../assets/js/widgets.bundle.js"></script>
<script src="../assets/js/custom/widgets.js"></script>
<script src="../assets/js/custom/apps/chat/chat.js"></script>
<script src="../assets/js/custom/utilities/modals/upgrade-plan.js"></script>
<script src="../assets/js/custom/utilities/modals/create-app.js"></script>
<script src="../assets/js/custom/utilities/modals/users-search.js"></script> --%>

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

#submit {
	
}
</style>
<script type="text/javascript">
	$(document).ready(function() {

		// Initialize select2
		$("#selectReseller").select2();

		//document.getElementById("loading").style.display = "none"
	});
</script>
<script type="text/javascript">
	$(document)
			.ready(
					function(e) {
						var userType = "<s:property value='%{#session.USER.UserGroup.group}'/>";
						if (userType == 'SMA') {
							document.getElementById('userType').value=userType;
							document.getElementById('userType').disabled=true;
							getCurrencyList();
							
						}

						if (userType == 'MA') {
							document.getElementById('userType').value=userType;
							document.getElementById('userType').disabled=true;
							getCurrencyList();
							//$('#resellers').hide();
						}
						if (userType == 'Agent') {
							document.getElementById('userType').value=userType;
							document.getElementById('userType').disabled=true;
							getCurrencyList();
							//$('#resellers').hide();
						}
					});
</script>
</head>
<body id="mainBody">
	<div style="overflow: scroll !important;">
		<div id="loader-wrapper"
			style="width: 100%; height: 100%; display: none;"></div>
		<div class="modal" id="payoutAccept" tabindex="-1" role="dialog"
			aria-labelledby="myModalLabel" aria-hidden="true">
			<div id="loadingInner" display="none">
				<img id="loading-image-inner" src="../image/sand-clock-loader.gif"
					alt="BUSY..." />
				<div id="loader" display="none"></div>
			</div>
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header" align="center"></div>
					<div class="modal-body">
						Please Verify the details before submission.
						<table class="table">
							<tr>
								<th>Batch No</th>
								<td id="batchNoTd">
								<td id="batchaccept"><input id="batchNo" readonly="true"
									class="input-control" autocomplete="off" type="text"
									name="batchNo"></td>
								</td>
							</tr>
							<tr>
								<th>Reseller Id</th>
								<td id="resellerIdTd">
								<td id="resellerIdaccept"><input id="resellerId"
									readonly="true" class="input-control" autocomplete="off"
									type="text" name="resellerId"></td>
								</td>
							</tr>
							<tr>
								<th>SettlementDate</th>
								<td id="settlementDateTd">
								<td id="settlementDateaccept"><input id="settlementDate"
									class="input-control" autocomplete="off" type="text"
									disabled="disabled" name="settlementDate"></td>
								</td>
							</tr>
							<tr>
								<th>Total Commission</th>
								<td id="totalCommissionTd">
								<td id="totalCommissionaccept"><input id="totalCommission"
									readonly="true" class="input-control" autocomplete="off"
									type="text" name="totalCommission"></td>
								<br>
								</td>
							</tr>
							<tr>
								<th>UTR No</th>
								<td id="utrNoTd">
								<td id="utraccept"><input id="utrNo" class="input-control"
									autocomplete="off" type="text" name="utrNo"
									OnKeypress="javascript:return isAlphaNumeric(event,this.value);"></td>
								</td>
							</tr>
							<tr>
								<th>TDS</th>
								<td id="tdsTd">
								<td id="tdsaccept"><input id="tds" class="input-control"
									autocomplete="off" type="number" name="tds"></td>
								</td>
							</tr>
						</table>
					</div>
					<div align="center">
						<button type="button" class="btn btn-lg btn-primary"
							id="btnPayoutConf" onClick='updatePayout("accept")'>Submit</button>
						<button type="button" class="btn btn-lg btn-danger"
							id="btnPayoutConf" data-dismiss="modal" onClick="hideModal()">Cancel</button>
					</div>
					<br> <br>
				</div>
			</div>
		</div>
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
							Reseller Payout</h1>
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
							<li class="breadcrumb-item text-dark">Reseller Payout</li>
							<!--end::Item-->
						</ul>
						<!--end::Breadcrumb-->
					</div>
					<!--end::Page title-->

				</div>
			</div>
			<!--end::Container-->
			<!-- <div class="card-body ">
				<div class="container">
					<div class="row"> -->
			<div class="post d-flex flex-column-fluid" id="kt_post">
				<!--begin::Container-->
				<div id="kt_content_container" class="container-xxl">
					<s:form id="resellerPayoutForm">
						<div class="row my-5">
							<div class="col">
								<div class="card">
									<div class="card-body">
										<!--begin::Input group-->
										<div class="row g-9 mb-8">
											<!--begin::Col-->
											<div class="col-md-3 fv-row">
												<label class="d-flex align-items-center fs-6 fw-bold mb-2">
													<span class="">Select User Type</span>
												</label>
												<s:select name="usertype" id="userType"
													data-control="select2" headerKey="1"
													list="#{'Select User Type':'Select User Type','SMA':'SMA','MA':'MA','Agent':'Agent'}"
													class=" form-select form-select-solid"
													onchange="getResellerList(this.value);">
												</s:select>

											</div>
											<div class="col-md-3 fv-row" id="resellers">
												<label class="d-flex align-items-center fs-6 fw-bold mb-2">
													<span>Reseller</span>
												</label>
												<s:if test="%{#session.USER.UserGroup.group =='SMA' ||#session.USER.UserGroup.group =='MA' || #session.USER.UserGroup.group =='Agent'}">
													<s:select name="resellerpayId"
														class="form-select form-select-solid payId"
														id="selectReseller" list="listReseller"
														listKey="payId" listValue="businessName"
														autocomplete="off" />
												</s:if>
												<s:else>
													<s:select name="resellerpayId" headerKey=""
														headerValue="Select Reseller"
														class="form-select form-select-solid payId"
														id="selectReseller" list="listReseller"
														listKey="payId" listValue="businessName" onchange="getCurrencyList();"
														autocomplete="off" data-control="select-2" />
												</s:else>
											</div>
											
											<div class="col-md-3 fv-row" id="currencyListDropdown">
												<label class="d-flex align-items-center fs-6 fw-bold mb-2">
													<span class="">Select Currency</span>
												</label>
												<s:select headerKey="Select Currency"
													headerValue="Select Currency"
													class="form-select form-select-solid"
													list="currencyMapList" name="currency" id="currency"
													autocomplete="off" />

											</div>
											</div>
											<div class="row g-9 mb-8">
											<div class="col-md-3 fv-row">
												<label class="d-flex align-items-center fs-6 fw-bold mb-2">
													<span class="">Date From</span>
												</label>
												<div class="position-relative d-flex align-items-center">
													<s:textfield type="text" id="dateFrom" name="dateFrom"
														class="form-control form-control-solid ps-12"
														autocomplete="off" readonly="true" />

												</div>
											</div>
											<div class="col-md-3 fv-row">
												<label class="d-flex align-items-center fs-6 fw-bold mb-2">
													<span class="">Date To</span>
												</label>
												<div class="position-relative d-flex align-items-center">
													<s:textfield type="text"
														class="form-control form-control-solid ps-12" id="dateTo"
														name="dateTo" autocomplete="off" readonly="true" />
												</div>
											</div>
											<div class="col-md-2 fv-row">
												<input type="button" class="btn btn-primary btn-xs"
													id="submit" value="Search" onClick="search()"
													style="margin-top: 25px;">

											</div>

											<input type="hidden" id="userGroup"
												value='<s:property value="#session.USER.UserGroup.group"/>'>


										</div>
									</div>
								</div>
							</div>
						</div>
				</div>
				</s:form>
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
											<div class="row g-9 mb-8 justify-content-end">
												<div class="col-lg-2 col-sm-12 col-md-6">
													<select name="currency" data-placeholder="Actions"
														id="actions11"
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
															<a class="toggle-vis" data-column="0">Batch No.</a> <a
																class="toggle-vis" data-column="2">Reseller Id</a> <a
																class="toggle-vis" data-column="3">Settlement Date</a> <a
																class="toggle-vis" data-column="4">TDS</a> <a
																class="toggle-vis" data-column="5">Total Commission</a>
															<a class="toggle-vis" data-column="6">Total Amount</a>
                                                            <a class="toggle-vis" data-column="6">Date</a>
														</div>
													</div>
												</div>
											</div>
											<div class="scrollD">
												<s:if test="%{#session.USER.UserGroup.group =='SMA' || #session.USER.UserGroup.group =='MA' ||#session.USER.UserGroup.group =='Agent'}">
													<table id="rDatatable"
														class="display table table-striped table-row-bordered gy-5 gs-7"
														cellspacing="0" width="100%">
														<thead>
															<tr class="boxheadingsmall">
																<th>Batch No.</th>
																<th></th>
																<th>ResellerId</th>
																<th>ResellerName</th>
																<th>Settlement Date</th>
																<th>TDS</th>
																<th>Total Commission</th>
																<th>Total Amount</th>
																<th>UTR No.</th>
																<th>Status</th>
																<th>Date</th>
															</tr>
														</thead>
													</table>
												</s:if>
												<s:else>
													<table id="datatable"
														class="display table table-striped table-row-bordered gy-5 gs-7"
														cellspacing="0" width="100%">
														<thead>
															<tr class="boxheadingsmall">
																<th>Batch No.</th>
																<th></th>
																<th style="display: none;">ResellerId</th>
																<th>ResellerName</th>
																<th>Settlement Date</th>
																<th>TDS</th>
																<th>Total Commission</th>
																<th>Total Amount</th>
																<th>UTR No.</th>
																<th>Status</th>
																<th>Date</th>
																<th>Action</th>
															</tr>
														</thead>
													</table>
												</s:else>
											</div></td>
				</tr>
			</table>
		</div>
	</div>
	</div>
	</div>
	</div>
	</div>

	</div>
	<script
		src="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.js"></script>
	<script src="../assets/plugins/global/plugins.bundle.js"></script>
	<script src="../assets/js/scripts.bundle.js"></script>

	<script>
		$('#datatable').on('draw.dt', function() {
			enableBaseOnAccess();
		});
		function enableBaseOnAccess() {
			setTimeout(function() {
				if ($('#resellerPayoutAction').hasClass("active")) {
					var menuAccess = document
							.getElementById("menuAccessByROLE").value;
					var accessMap = JSON.parse(menuAccess);
					var access = accessMap["resellerPayoutAction"];
					if (access.includes("Payout")) {
						var payouts = document
								.getElementsByName("payoutDetails");
						for (var i = 0; i < payouts.length; i++) {
							var payout = payouts[i];
							payout.disabled = false;
						}
					}
				}
			}, 500);
		}
	</script>
	<script src="../assets/plugins/custom/datatables/datatables.bundle.js"></script>
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

		}
	</script>


	<script type="text/javascript">
		$('a.toggle-vis').on('click', function(e) {
			e.preventDefault();
			table = $('#datatable').DataTable();
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
	</script>
	<style>
.dt-buttons {
	display: none;
}
</style>
</body>
</html>
