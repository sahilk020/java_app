<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="/struts-tags" prefix="s"%>
<html>
<head>
<meta http-equiv="cache-control" content="no-cache">
<!-- tells browser not to cache -->
<meta http-equiv="expires" content="0">
<!-- says that the cache expires 'now' -->
<meta http-equiv="pragma" content="no-cache">
<!-- says not to use cached stuff, if there is any -->
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>FRM Report</title>
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
<style type="text/css">
.card-list-toggle {
	cursor: pointer;
	padding: 8px 12px;
	border: 1px solid #496cb6;
	position: relative;
	background: linear-gradient(60deg, #425185, #4a9b9b);
}

.card-list-toggle:before {
	position: absolute;
	right: 10px;
	top: 7px;
	content: "\f078";
	font-family: 'FontAwesome';
	font-size: 15px;
}

.card-list-toggle.active:before {
	content: "\f077";
}

.card-list {
	display: none;
}

.btn:focus {
	outline: 0 !important;
}

.sub-headingCls {
	margin-top: 20px;
	margin-bottom: 0px;
	font-size: 12px;
	font-weight: 700;
	color: #0271bb;
}

label {
	font-size: 14px;
	font-weight: 500;
	color: grey;
}

.col-sm-6.col-lg-3 {
	margin-bottom: 10px;
}
</style>

<style>
.product-spec input[type=text] {
	width: 35px;
}

.btn {
	text-transform: capitalize;
}

.form-control {
	display: block;
	width: 100% !important;
	height: 28px;
	padding: 3px 4px;
	font-size: 14px;
	line-height: 1.42857143;
	color: #555;
	background-color: #fff;
	background-image: none;
	border: 1px solid #ccc;
	border-radius: 4px;
	-webkit-box-shadow: inset 0 1px 1px rgba(0, 0, 0, .075);
	box-shadow: inset 0 1px 1px rgba(0, 0, 0, .075);
	-webkit-transition: border-color ease-in-out .15s, -webkit-box-shadow
		ease-in-out .15s;
	-o-transition: border-color ease-in-out .15s, box-shadow ease-in-out
		.15s;
	transition: border-color ease-in-out .15s, box-shadow ease-in-out .15s;
}

#submitBtn {
	margin-top: 25px;
}

#saveMsg {
	color: #8ef58c;
	background: #86b787;
}

#saveMsg ul li {
	list-style: none !important;
}

.dt-buttons {
	display: none;
}
</style>
<script type="text/javascript">
	$(document).ready(function() {

		var urls = new URL(window.location.href);
		var domain = urls.origin;
		$.ajax({
			type : "GET",
			url : domain + "/crmws/fraud/transaction/fetch",
			//url:"https://uat.pay10.com/crmws/fraud/transaction/fetch",
			contentType : "application/json",
			success : function(response) {
				renderTable(response.data);

			}
		});
	});
</script>
</head>
<body>
	<!-- <div id="loader-wrapper"
		style="width: 100%; height: 100%; display: none;">
		<div id="loader"></div>
	</div> -->
	<div class="content d-flex flex-column flex-column-fluid"
		id="kt_content">
		<div style="overflow: scroll !important;">
			<!-- <table width="100%" border="0" cellspacing="0" cellpadding="0"
			class="txnf">
			<tr>
				<td>
					<div>
						<input type="button" value="Update Service Tax" id="updateServiceTax" class="btn btn-success btn-md" style="display: inline;margin-top:1%;width:16%;margin-left:1%;font-size: 15px;margin-bottom:1%;"></input>
					</div>
				</td>
			</tr>
		</table> -->
			<!-- Added By Sweety -->

			<!--begin::Root-->
			<div class="d-flex flex-column flex-root">
				<!--begin::Page-->
				<div class="page d-flex flex-row flex-column-fluid">
					<!--begin::Content-->
					<div class="content d-flex flex-column flex-column-fluid"
						id="kt_content">
						<div class="toolbar" id="kt_toolbar">
							<!--begin::Container-->
							<div id="kt_toolbar_container"
								class="container-fluid d-flex flex-stack">
								<!--begin::Page title-->
								<div data-kt-swapper="true" data-kt-swapper-mode="prepend"
									data-kt-swapper-parent="{default: '#kt_content_container', 'lg': '#kt_toolbar_container'}"
									class="page-title d-flex align-items-center me-3 flex-wrap mb-5 mb-lg-0 lh-1">
									<!--begin::Title-->
									<h1
										class="d-flex align-items-center text-dark fw-bold my-1 fs-3">
										FRM Report</h1>
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
										<li class="breadcrumb-item text-muted">Fraud Prevention</li>
										<!--end::Item-->
										<!--begin::Item-->
										<li class="breadcrumb-item"><span
											class="bullet bg-gray-200 w-5px h-2px"></span></li>
										<!--end::Item-->
										<!--begin::Item-->
										<li class="breadcrumb-item text-dark">FRM Report</li>
										<!--end::Item-->
									</ul>
									<!--end::Breadcrumb-->
								</div>
								<!--end::Page title-->

							</div>
						</div>





						<!--begin::Post-->
						<div class="post d-flex flex-column-fluid" id="kt_post">
							<!--begin::Container-->
							<div id="kt_content_container" class="container-xxl">
<!-- 								<table width="100%" align="left" cellpadding="0" cellspacing="0" -->
<!-- 									class="formbox"> -->
<!-- 									<tr> -->
<!-- 										<td align="left" style="padding: 10px;"><br /> <br /> -->
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
																				<a class="toggle-vis" data-column="2">Subject</a> <a
																					class="toggle-vis" data-column="3">Notification</a>
																				<a class="toggle-vis" data-column="4">Merchant
																					Name</a> <a class="toggle-vis" data-column="5">PG RefNum
																					</a> <a class="toggle-vis" data-column="6">Payment
																					Type</a> <a class="toggle-vis" data-column="7">Mop
																					Type</a> <a class="toggle-vis" data-column="8">Block
																					Type</a>
																					<a class="toggle-vis" data-column="9">Date</a>




																			</div>
																		</div>
																	</div>
																</div>
																<div class="row g-9 mb-8">
																	<table id="datatable"
																		class="display table table-striped table-row-bordered gy-5 gs-7">
																		
																		<thead>
																			<tr class="boxheadingsmall">
																				<th style="display: none">Id</th>
																				<th style="display: none">OrderId</th>
																				<th>Subject</th>
																				<th>Notification</th>
																				<th>Merchant Name</th>
																				<th>PG RefNum</th>
																				<th>Payment Type</th>
																				<th>Mop Type</th>
																				<th>Block Type</th>
																				<th>Date</th>
																				<th>Action</th>

																			</tr>
																		</thead>
																	</table>
																	</div>
<!-- 																</td> -->
<!-- 									</tr> -->
<!-- 								</table> -->
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
			//enableBaseOnAccess();
		});
		/* function enableBaseOnAccess() {
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
		} */
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
	<script>
		function renderTable(data) {
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
								
								dom : 'BTrtlpi',
								buttons : [
										$.extend(true, {}, buttonCommon, {
											extend : 'copyHtml5',
											exportOptions : {
												columns : [ 0, 1, 2, 3, 4, 5,
														6, 7, 8, 9,10 ]
											},
										}),
										$.extend(true, {}, buttonCommon, {
											extend : 'csvHtml5',
											title : 'Frm_Report',
											exportOptions : {
												columns : [ 0, 1, 2, 3, 4, 5,
														6, 7, 8, 9,10 ],
											},
										}),
										{
											extend : 'pdfHtml5',
											orientation : 'landscape',
											pageSize : 'legal',
											//footer : true,
											title : 'Frm_Report',
											exportOptions : {
												columns : [ 0, 1, 2, 3, 4, 5,
														6, 7, 8, 9,10 ]
											},
											customize : function(doc) {
												doc.defaultStyle.alignment = 'center';
												doc.styles.tableHeader.alignment = 'center';
											}
										},

										{
											extend : 'colvis',
											columns : [ 0, 1, 2, 3, 4, 5, 6, 7,
													8, 9,10 ]
										}, ],
										"scrollY": true,
										"scrollX": true,
								'columns' : [
										{
											'data' : 'id',
											'visible' : false,

										},
										{
											'data' : 'orderId',
											'visible' : false,

										},
										{
											'data' : 'subject',

										},
										{
											'data' : 'fraudType',

										},

										{
											'data' : 'merchantName',

										},
										{
											'data' : 'txnId',

										},
										{
											'data' : 'paymentType',

										},
										{
											'data' : 'mopType',

										},
										{
											'data' : 'blockType',

										},
										{
											'data' : 'date',

										},

										{
											"mData" : null,
											"sClass" : "center",
											render : function(data) {
												var notifyMerchant = data.action.notifyMerchant;
												var block = data.action.block;
												var ignore = data.action.ignore;
												var classforNotify = notifyMerchant ? "btn btn-sm btn-primary disabled"
														: "btn btn-sm btn-primary";
												var classforBlock = block ? "btn btn-sm btn-danger disabled"
														: "btn btn-sm btn-danger";
												var classforIgnore = ignore ? "btn btn-sm btn-warning disabled"
														: "btn btn-sm btn-warning";
												var notify = '<a data-toggle="tooltip" data-placement="top" data-original-title="Edit" id="notify" class="'
														+ classforNotify
														+ '" onclick=\'notify('
														+ JSON.stringify(data)
														+ ',"NOTIFYMERCHANT")\'>Notify</a>'
														+ '<a data-toggle="tooltip" data-placement="top" data-original-title="Edit" id="block"   style="margin-top: 2px;margin-left: 2px;" class="'
														+ classforBlock
														+ '" onclick=\'notify('
														+ JSON.stringify(data)
														+ ',"BLOCK")\'>Block</a>'
														+ '<a data-toggle="tooltip" data-placement="top" data-original-title="Edit"  id="ignore" style="margin-top: 2px;margin-left: 2px;" class="'
														+ classforIgnore
														+ '" onclick=\'notify('
														+ JSON.stringify(data)
														+ ',"IGNORE")\'>Ignore</a>';
												return notify;
											}
										} ]
							});
		}

		var urls = new URL(window.location.href);
		var domain = urls.origin;
		var url = domain + "/crmws/fraud/transaction/action/update";
		// var url="https://uat.pay10.com/crmws/fraud/transaction/action/update";
		function notify(data, actionType) {
			var json = JSON.parse(JSON.stringify(data));

			$.ajax({
				url : url,
				contentType : 'application/json',
				type : "POST",
				data : JSON.stringify({
					id : json.id,
					fraudType : json.fraudType,
					merchantName : json.merchantName,
					orderId : json.orderId,
					subject : json.subject,
					txnId : json.txnId,
					paymentType : json.paymentType,
					blockType : json.blockType,
					mopType : json.mopType,
					actionType : actionType,
				}),
				success : function(data) {
					alert(actionType.charAt(0).toUpperCase()
							+ actionType.slice(1).toLowerCase()
							+ " Successfully");
					window.location.reload();
				},
				error : function(status) {
					alert("Unable to" + actionType.charAt(0).toUpperCase()
							+ actionType.slice(1).toLowerCase());
				}
			});
		}
	</script>
</body>
</html>



