<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="/struts-tags" prefix="s"%>
<html dir="ltr" lang="en-US">
<head>
<title>Reseller Details</title>

<%-- <script src="../js/jquery-ui.js"></script> --%>

<!-- <link href="../assets/plugins/custom/datatables/datatables.bundle.css"
	rel="stylesheet" type="text/css" />
end::Vendor Stylesheets
begin::Global Stylesheets Bundle(used by all pages)
<link href="../assets/plugins/global/plugins.bundle.css"
	rel="stylesheet" type="text/css" />
<link href="../assets/css/style.bundle.css" rel="stylesheet"
	type="text/css" />
<link href="../assets/css/custom/custom-style.css" rel="stylesheet"
	type="text/css" /> -->
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
<script src="../js/resellerDailyUpdate.js"></script>

<style type="text/css">
.selectBox {
	position: relative;
}

.selectBox select {
	width: 95%;
}

#checkboxes1 {
	display: none;
	border: 1px #dadada solid;
	height: 300px;
	overflow-y: scroll;
	position: Absolute;
	background: #fff;
	z-index: 1;
	margin-left: 5px;
}

#checkboxes1 label {
	width: 74%;
}

#checkboxes1 input {
	width: 18%;
}

#section {
	height: 300px;
	padding-top: 90px;
}

#checkboxes2 {
	display: none;
	border: 1px #dadada solid;
	height: 300px;
	overflow-y: scroll;
	position: Absolute;
	background: #fff;
	z-index: 1;
	margin-left: 5px;
}

#checkboxes2 label {
	width: 74%;
}

#checkboxes2 input {
	width: 18%;
}

#checkboxes3 {
	display: none;
	border: 1px #dadada solid;
	height: 300px;
	overflow-y: scroll;
	position: Absolute;
	background: #fff;
	z-index: 1;
	margin-left: 5px;
}

#checkboxes3 label {
	width: 74%;
}

#checkboxes3 input {
	width: 18%;
}

#checkboxes4 {
	display: none;
	border: 1px #dadada solid;
	height: 300px;
	overflow-y: scroll;
	position: Absolute;
	background: #fff;
	z-index: 1;
	margin-left: 5px;
}

#checkboxes4 label {
	width: 74%;
}

#checkboxes4 input {
	width: 18%;
}

.overSelect {
	position: absolute;
	left: 0;
	right: 0;
	top: 0;
	bottom: 0;
}

#cardTitle {
	display: inline-flex;
	font-size: 20px;
	font-weight: 500;
}

.nav-pills .nav-item .nav-link {
	font-size: 16px !important;
	font-weight: 100 !important;
}

button.dt-button, div.dt-button, a.dt-button {
	font-size: 14px;
}

#totalTxnsAmount th {
	color: #496cb6;
}

#totalSettledTxnsAmount th {
	color: #496cb6;
}

#submit {
	margin-top: 25px;
}
</style>
<script src="../assets/js/scripts.bundle.js"></script>
<script src="../assets/plugins/global/plugins.bundle.js"></script>
<script
	src="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.js"></script>
<script type="text/javascript">
	$(document)
			.ready(
					function(e) {
						debugger
						var userType= "<s:property value='%{#session.USER.UserGroup.group}'/>";
						
						
						if (userType == 'SMA') {
							document.getElementById('userType').value = userType;
							document.getElementById('userType').disabled=true;
							var resellerId= document.getElementById('resellerId').value;
							getMerchant(resellerId);
							
						}

						if (userType == 'MA') {
							document.getElementById('userType').value=userType;
							document.getElementById('userType').disabled=true;
							var resellerId= document.getElementById('resellerId').value;
							getMerchant(resellerId);
						}
						if (userType == 'Agent') {
							document.getElementById('userType').value=userType;
							document.getElementById('userType').disabled=true;
							var resellerId= document.getElementById('resellerId').value;
							getMerchant(resellerId);
						}
					});
</script>
</head>
<body id="mainBody">
	<!-- <div id="loader-wrapper"
		style="width: 100%; height: 100%; display: none;">
		<div id="loader"></div>
	</div> -->

	<div class="content d-flex flex-column flex-column-fluid"
		id="kt_content">
		<!-- Added By Sweety -->
		<div style="overflow: scroll !important;">
			<!--begin::Root-->
			<!-- <div class="d-flex flex-column flex-root">
			begin::Page
			<div class="page d-flex flex-row flex-column-fluid"> -->
			<!--begin::Content-->
			<!-- <div class="content d-flex flex-column flex-column-fluid"
					id="kt_content"> -->
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
							Reseller Daily Update</h1>
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
							<li class="breadcrumb-item text-dark">Reseller Daily Update</li>
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
					<s:form id="ResellerUpdate" action="resellerDailyUpdateAction"
						method="post">

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
												<s:if test="%{#session.USER.UserGroup.group =='SMA'}">
													<s:select name="usertype" id="userType"
														data-control="select2" headerKey="1" list="#{'SMA':'SMA'}"
														class=" form-select form-select-solid"
														onchange="getResellerList(this.value);">
													</s:select>
												</s:if>
												<s:elseif test="%{#session.USER.UserGroup.group =='MA'}">
													<s:select name="usertype" id="userType"
														data-control="select2" headerKey="1" list="#{'MA':'MA'}"
														class=" form-select form-select-solid"
														onchange="getResellerList(this.value);">
													</s:select>
												</s:elseif>
												<s:elseif test="%{#session.USER.UserGroup.group =='Agent'}">
													<s:select name="usertype" id="userType"
														data-control="select2" headerKey="1" list="#{'Agent':'Agent'}"
														class=" form-select form-select-solid"
														onchange="getResellerList(this.value);">
													</s:select>
												</s:elseif>
												<s:else>
													<s:select name="usertype" id="userType"
														data-control="select2" headerKey="1"
														list="#{'Select User Type':'Select User Type','SMA':'SMA','MA':'MA','Agent':'Agent'}"
														class=" form-select form-select-solid"
														onchange="getResellerList(this.value);">
													</s:select>
												</s:else>
											</div>

											<div class="col-md-3 fv-row" id="toreseller">
												<label class="d-flex align-items-center fs-6 fw-bold mb-2">
													<span class="">Select Reseller</span>
												</label>
												<s:if
													test="%{#session.USER.UserGroup.group =='SMA' ||#session.USER.UserGroup.group =='MA' || #session.USER.UserGroup.group =='Agent'}">
													<s:select name="resellername"
														class="form-select form-select-solid payId"
														id="resellerId" list="resellerList" listKey="payId"
														listValue="businessName" autocomplete="off" />
												</s:if>
												<s:else>
													<s:select name="resellername"
														class="form-select form-select-solid" id="resellerId"
														onchange="getMerchant(this.value);" headerKey=""
														headerValue="Select Reseller" list="resellerList"
														listKey="payId" listValue="businessName"
														autocomplete="off" data-control="select-2" />
												</s:else>
											</div>
											<div class="col-md-3 fv-row" id="tomerchant">

												<label class="d-flex align-items-center fs-6 fw-bold mb-2">
													<span class="">Select Merchant</span>
												</label>

												<s:select class="form-select form-select-solid"
													id="merchantDropdown" name="merchantname"
													onchange="getPaymentType(this.value);" list="merchantlist"
													listKey="payId" listValue="businessName" autocomplete="off"
													style="margin-left: -4px;" />

											</div>

											<div class="col-md-3 fv-row" id="topayment">
												<label class="d-flex align-items-center fs-6 fw-bold mb-2">
													<span class="">Select Payment Type</span>
												</label>
												<s:select class="form-select form-select-solid"
													id="paymentDropdown" name="paymentType"
													onchange="getMopType();" list="paymentTypeList"
													listValue="paymentType" autocomplete="off"
													style="margin-left: -4px;" />

											</div>
										</div>
										<div class="row g-9 mb-8">

											<div class="col-md-3 fv-row" id="tomoptype">
												<label class="d-flex align-items-center fs-6 fw-bold mb-2">
													<span class="">Select MOP Type</span>
												</label>

												<s:select class="form-select form-select-solid"
													onchange="getCurrencyList();" id="moptypeDropdown"
													name="mopType" list="mopTypeList" listValue="mopType"
													autocomplete="off" style="margin-left: -4px;" />
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

											<div class="col-md-3 fv-row">
												<label class="d-flex align-items-center fs-6 fw-bold mb-2">
													<span class="">Date From</span>
												</label>
												<div class="position-relative d-flex align-items-center">
													<s:textfield type="text" id="dateFrom" name="dateFrom"
														class="form-control form-control-solid ps-12"
														autocomplete="off" onkeyup="checkFromDate()"
														maxlength="10" />
													<span id="dateError" style="color: red; display: none;">Please
														Enter Valid Date </span> <span id="showErr1"
														style="color: red; display: none;">Please Don't
														Enter Future Date</span>
												</div>
											</div>
											<div class="col-md-3 fv-row">
												<label class="d-flex align-items-center fs-6 fw-bold mb-2">
													<span class="">Date To</span>
												</label>

												<div class="position-relative d-flex align-items-center">
													<s:textfield type="text" id="dateTo" name="dateTo"
														class="form-control form-control-solid ps-12"
														autocomplete="off" onkeyup="checkToDate()" maxlength="10" />
													<span id="dateError1" style="color: red; display: none;">Please
														Enter Valid Date </span> <span id="showErr2"
														style="color: red; display: none;">Please Don't
														Enter Future Date</span>

												</div>
											</div>
											<div class="col-md-2 fv-row" style="margin-right: 2px;">
												<input type="button" id="submit" value="View"
													onClick="getDetails()" class="btn btn-primary btn-xs">

											</div>
										</div>
									</div>
								</div>
							</div>
						</div>

					</s:form>



					<!-- </div> -->


					<div class="row my-5">
						<div class="col">
							<div class="card">
								<div class="card-body">
									<div
										class="row my-3 align-items-center d-flex justify-content-start">
										<table width="100%" align="left" cellpadding="0"
											cellspacing="0" class="formbox">
											<tr>
												<td align="left" style="padding: 10px;"><br /> <br />
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
																	<a class="toggle-vis" data-column="0">Reseller</a>
																	<a class="toggle-vis" data-column="1">Merchant</a>
																	<a class="toggle-vis" data-column="2">Payment Type</a>
																	<a class="toggle-vis" data-column="3">MOP Type</a>
																	<a class="toggle-vis" data-column="4">Amount</a>
																	<a class="toggle-vis" data-column="5">Sale Amount</a>
																	<a class="toggle-vis" data-column="6">Total Refund</a>
																	<a class="toggle-vis" data-column="7">Commission Amount</a>
																	<a class="toggle-vis" data-column="8">Date</a>
																</div>

															</div>
														</div>
														<div class="scrollD">
															<table id="datatable"
																class="display table table-striped table-row-bordered gy-5 gs-7"
																cellspacing="0" width="100%">
																<thead>
																	<tr class="boxheadingsmall">
																		<th>Reseller</th>
																		<th>Merchant</th>
                                                                        <th>Payment Type</th>
																		<th>MOP Type</th>
                                                                        <th>Amount</th>
																		<th>Sale Amount</th>
																		<th>Total Refund</th>
																	    <th>Commission Amount</th>
																	    <th>Date</th>
                                                                    </tr>
																</thead>
															</table>
														</div>
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
		</div>
	</div>

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
