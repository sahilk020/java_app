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
<title>Cash Deposit</title>
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

.dt-buttons {
	display: none;
}
</style>

<style>
@media ( min-width : 992px) {
	.col-lg-3 {
		max-width: 30% !important;
	}
}

.errMsg {
	color: red !important;
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
				<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">Cash Deposit</h1>
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
					<li class="breadcrumb-item text-muted">Cash Deposit</li>
					<!--end::Item-->
					<!--begin::Item-->
					<li class="breadcrumb-item"><span
						class="bullet bg-gray-200 w-5px h-2px"></span></li>
					<!--end::Item-->
					<!--begin::Item-->
					<li class="breadcrumb-item text-dark">Cash Deposit</li>
					<!--end::Item-->
				</ul>
				<!--end::Breadcrumb-->
			</div>
			<!--end::Page title-->

		</div>
		<!--end::Container-->
	</div>


	<div style="overflow: auto !important;">

		<div style="overflow: auto !important;">

			<div id="kt_content_container" class="container-xxl">
				<div class="row my-5">
					<div class="col">
						<div class="card">
							<div class="card-body">
								<!--begin::Input group-->
								<div class="row g-9 mb-8">

									<div class="card-body ">

										<form id="CashDepost" action="CashDeposits" method="post">

											<div class="row my-3 align-items-center">

												<s:if test="%{#session.USER.UserType.name()!='MERCHANT'}">
												<div class=" col-lg-3 my-2" >
													<label class="d-flex align-items-center fs-6 fw-bold mb-2">
														<span class="required">Merchant Id</span>
													</label>
													<s:select name="payId" headerKey="Select Merchant" headerValue="Select Merchant"
														class="form-select form-select-solid adminMerchants"
														id="payId"
														 list="payIdList" />
													<div class="errMsg" id="errorMsgPayId"></div>

												</div>
												</s:if>

												<s:else>
												<s:hidden id="payId" name="payId" value="%{#session.USER.payId}" />
												</s:else>

												<div class=" col-lg-3 my-2">
													<label class="d-flex align-items-center fs-6 fw-bold mb-2">
														<span class="required">Currency</span>
													</label>
														<s:select headerKey="Select Currency" headerValue="Select Currency"
														list="currencyList" class="form-select form-select-solid"
														id="currency" listKey="code" listValue="name"
														name="currency" value="currency" />
													<div class="errMsg" id="errorMsgCurrency"></div>
												</div>

												<div class="col-lg-3 my-2">
													<label class="d-flex align-items-center fs-6 fw-bold mb-2">
														<span class="required">Amount</span>
													</label> <input type="number" id="amount" name="amount"
														class="form-control">
													<div class="errMsg" id="errorMsgAmount"></div>

												</div>

												<div class="col-lg-3 my-2">
													<label
														class="d-flex align-items-center fs-6 fw-semibold mb-2">
														<span class="required">Date</span>
													</label>
													<!--end::Label-->
													<div class="position-relative d-flex align-items-center">
														<!--begin::Icon-->
														<!--begin::Svg Icon | path: icons/duotune/general/gen014.svg-->
														<span class="svg-icon svg-icon-2 position-absolute mx-4">
															<svg width="24" height="24" viewBox="0 0 24 24"
																fill="none" xmlns="http://www.w3.org/2000/svg"> <path
																opacity="0.3"
																d="M21 22H3C2.4 22 2 21.6 2 21V5C2 4.4 2.4 4 3 4H21C21.6 4 22 4.4 22 5V21C22 21.6 21.6 22 21 22Z"
																fill="currentColor"></path> <path
																d="M6 6C5.4 6 5 5.6 5 5V3C5 2.4 5.4 2 6 2C6.6 2 7 2.4 7 3V5C7 5.6 6.6 6 6 6ZM11 5V3C11 2.4 10.6 2 10 2C9.4 2 9 2.4 9 3V5C9 5.6 9.4 6 10 6C10.6 6 11 5.6 11 5ZM15 5V3C15 2.4 14.6 2 14 2C13.4 2 13 2.4 13 3V5C13 5.6 13.4 6 14 6C14.6 6 15 5.6 15 5ZM19 5V3C19 2.4 18.6 2 18 2C17.4 2 17 2.4 17 3V5C17 5.6 17.4 6 18 6C18.6 6 19 5.6 19 5Z"
																fill="currentColor"></path> <path
																d="M8.8 13.1C9.2 13.1 9.5 13 9.7 12.8C9.9 12.6 10.1 12.3 10.1 11.9C10.1 11.6 10 11.3 9.8 11.1C9.6 10.9 9.3 10.8 9 10.8C8.8 10.8 8.59999 10.8 8.39999 10.9C8.19999 11 8.1 11.1 8 11.2C7.9 11.3 7.8 11.4 7.7 11.6C7.6 11.8 7.5 11.9 7.5 12.1C7.5 12.2 7.4 12.2 7.3 12.3C7.2 12.4 7.09999 12.4 6.89999 12.4C6.69999 12.4 6.6 12.3 6.5 12.2C6.4 12.1 6.3 11.9 6.3 11.7C6.3 11.5 6.4 11.3 6.5 11.1C6.6 10.9 6.8 10.7 7 10.5C7.2 10.3 7.49999 10.1 7.89999 10C8.29999 9.90003 8.60001 9.80003 9.10001 9.80003C9.50001 9.80003 9.80001 9.90003 10.1 10C10.4 10.1 10.7 10.3 10.9 10.4C11.1 10.5 11.3 10.8 11.4 11.1C11.5 11.4 11.6 11.6 11.6 11.9C11.6 12.3 11.5 12.6 11.3 12.9C11.1 13.2 10.9 13.5 10.6 13.7C10.9 13.9 11.2 14.1 11.4 14.3C11.6 14.5 11.8 14.7 11.9 15C12 15.3 12.1 15.5 12.1 15.8C12.1 16.2 12 16.5 11.9 16.8C11.8 17.1 11.5 17.4 11.3 17.7C11.1 18 10.7 18.2 10.3 18.3C9.9 18.4 9.5 18.5 9 18.5C8.5 18.5 8.1 18.4 7.7 18.2C7.3 18 7 17.8 6.8 17.6C6.6 17.4 6.4 17.1 6.3 16.8C6.2 16.5 6.10001 16.3 6.10001 16.1C6.10001 15.9 6.2 15.7 6.3 15.6C6.4 15.5 6.6 15.4 6.8 15.4C6.9 15.4 7.00001 15.4 7.10001 15.5C7.20001 15.6 7.3 15.6 7.3 15.7C7.5 16.2 7.7 16.6 8 16.9C8.3 17.2 8.6 17.3 9 17.3C9.2 17.3 9.5 17.2 9.7 17.1C9.9 17 10.1 16.8 10.3 16.6C10.5 16.4 10.5 16.1 10.5 15.8C10.5 15.3 10.4 15 10.1 14.7C9.80001 14.4 9.50001 14.3 9.10001 14.3C9.00001 14.3 8.9 14.3 8.7 14.3C8.5 14.3 8.39999 14.3 8.39999 14.3C8.19999 14.3 7.99999 14.2 7.89999 14.1C7.79999 14 7.7 13.8 7.7 13.7C7.7 13.5 7.79999 13.4 7.89999 13.2C7.99999 13 8.2 13 8.5 13H8.8V13.1ZM15.3 17.5V12.2C14.3 13 13.6 13.3 13.3 13.3C13.1 13.3 13 13.2 12.9 13.1C12.8 13 12.7 12.8 12.7 12.6C12.7 12.4 12.8 12.3 12.9 12.2C13 12.1 13.2 12 13.6 11.8C14.1 11.6 14.5 11.3 14.7 11.1C14.9 10.9 15.2 10.6 15.5 10.3C15.8 10 15.9 9.80003 15.9 9.70003C15.9 9.60003 16.1 9.60004 16.3 9.60004C16.5 9.60004 16.7 9.70003 16.8 9.80003C16.9 9.90003 17 10.2 17 10.5V17.2C17 18 16.7 18.4 16.2 18.4C16 18.4 15.8 18.3 15.6 18.2C15.4 18.1 15.3 17.8 15.3 17.5Z"
																fill="currentColor"></path> </svg>
														</span>
														<!--end::Svg Icon-->
														<!--end::Icon-->
														<!--begin::Datepicker-->
														<input
															class="form-control form-control-solid ps-12 flatpickr-input"
															placeholder="Select a date" name="createDate"
															id="createDate" type="text" readonly="readonly">
														<!--end::Datepicker-->
													</div>
												</div>
												<div class="col-lg-3 my-2">
													<label class="d-flex align-items-center fs-6 fw-bold mb-2">
														<span class="required">Transaction Id</span>
													</label> <input type="number" id="txnId" name="txnId"
														class="form-control">
													<div class="errMsg" id="errorMsgTxnId"></div>


												</div>
												<div class="col-lg-3 my-2">
													<label class="d-flex align-items-center fs-6 fw-bold mb-2">
														<span class="required">Remark</span>
													</label> <input type="text" id="remark" name="remark"
														class="form-control">
													<div class="errMsg" id="errorMsgRemark"></div>

												</div>
												<div class="col-lg-1 my-2">
													<label class="d-flex align-items-center fs-6 fw-bold mb-2">
														<span class="">&nbsp;</span>
													</label>
													<button type="button" class="btn btn-primary"
														onclick="sumbitButton()">Submit</button>
												</div>

											</div>
										</form>
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
															<th class="col">Pay Id</th>
															<th scope="col">Amount</th>
															<th scope="col">Date</th>
															<th scope="col">Transaction Id</th>
															<th scope="col">Status</th>
															<th scope="col">Currency</th>
															<th scope="col">Remark</th>
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
	</div>




	<script type="text/javascript">
		$(document).ready(function() {

			<s:if test="%{#session.USER.UserType.name()!='MERCHANT'}">
			$("#payId").select2();
		</s:if>

			$(function() {
				$("#createDate").flatpickr({
					maxDate : new Date(),
					dateFormat : "Y-m-d",
					defaultDate : "today",
					defaultDate : "today",
				});
			});

			$(function() {
				renderTable();
			});
		});

		function renderTable() {
			$("#example").DataTable().destroy();
			var token = document.getElementsByName("token")[0].value;
			var buttonCommon = {
				exportOptions : {
					format : {
						body : function(data, column, row, node) {
							return data;
						}
					}
				}
			};
			$('#example').DataTable({
				dom : 'BTftlpi',
				'columnDefs' : [ {
					'searchable' : false
				} ],
				"ajax" : {
					"url" : "CashDepositReport",
					"type" : "POST",

				},
				"bProcessing" : true,
				"bLengthChange" : true,
				"bAutoWidth" : false,
				"iDisplayLength" : 10,
				"order" : [ [ 2, "desc" ] ],
				"aoColumns" : [ {
					"mData" : "payId"
				}, {
					"mData" : "amount"
				}, {
					"mData" : "createDate"
				}, {
					"mData" : "txnId"
				}, {
					"mData" : "status"
				}, {
					"mData" : "currency"
				},{
					"mData" : "remark"
				}

				]
			});
		}

		function generatePostData() {

			var obj = {

			}
			return obj;
		}
	</script>

	<script src="../assets/plugins/custom/datatables/datatables.bundle.js"></script>
	<script type="text/javascript">

		function sumbitButton() {

			var payId = $("#payId").val();
			var amount = $("#amount").val();
			var txnId = $("#txnId").val();
			var remark = $("#remark").val();
			var currency = $("#currency").val();


			if (payId == "Select Merchant") {
				alert("Please Select Merchant");
				return false;
			}
			if (currency == "Select Currency") {
				alert("Please Select Currency");
				return false;
			}

			if (amount == "e" || amount == "E") {
                alert("Character not allowed");
                return false;
            }

			if (amount == "") {
				alert("Please Enter Amount");
				return false;
			}
			if (amount == 0) {
				alert("Amount should not be Zero");
				return false;
			}


			if (amount < 0) {
				alert("Amount should not be Negative");
				return false;
			}


			var reg = new RegExp(/^\d+\.?\d*$/);
            if(!reg.test(amount)){
            	alert("Invalid Amount");
            	return false;
            }

            var reg = new RegExp(/^\d+$/);
            if(!reg.test(amount)){
                alert("Decimal is not allowed in Amount");
                return false;
            }


			if (txnId == "") {
				alert("Please Enter Transaction Id");
				return false;
			}

			if (txnId == 0) {
				alert("Transaction ID should not be Zero");
				return false;
			}

			if (txnId < 0) {
				alert("Transaction ID should not be Negative");
				return false;
			}

			if (txnId.length < 6) {
				alert("Length should greater than 6 in Transaction ID");
				return false;
			}


			var reg = new RegExp(/^\d+$/);
			if(!reg.test(txnId)){
				alert("Invalid Transaction ID");
				return false;
			}

			if (remark == "") {
				alert("Please Enter Remark");
				return false;
			}



			document.getElementById("CashDepost").submit();
			alert("Cash Deposit Request Sent Successfully");

		}
	</script>

</body>
</html>
