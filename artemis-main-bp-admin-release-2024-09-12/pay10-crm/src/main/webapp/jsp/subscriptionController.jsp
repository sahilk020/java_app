<%@page import="com.pay10.crm.actionBeans.TransactionStatusBean" %>
	<%@page import="java.util.List" %>
		<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
			<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
			<%@ taglib uri="/struts-tags" prefix="s" %>
				<html dir="ltr" lang="en-US">

				<head>
					<title>Subscription Controller</title>
					<link href="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.css" rel="stylesheet"
						type="text/css" />
					<link href="../assets/plugins/custom/datatables/datatables.bundle.css" rel="stylesheet"
						type="text/css" />
					<link href="../assets/plugins/global/plugins.bundle.css" rel="stylesheet" type="text/css" />
					<link href="../assets/css/style.bundle.css" rel="stylesheet" type="text/css" />
					<link href="../assets/css/custom/custom-style.css" rel="stylesheet" type="text/css" />
					<script src="../js/loader/main.js"></script>
					<script src="../assets/plugins/global/plugins.bundle.js"></script>
					<script src="../assets/js/scripts.bundle.js"></script>
					<script src="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.js"></script>
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
						.dt-buttons.btn-group.flex-wrap {
							display: none;
						}

						#txnResultDataTable thead th {
							font-weight: bold;

						}

						.error {
							color: red
						}

						.svg-icon {
							margin-top: 1vh !important;
						}

						.modal-dialog {
							outline: none !important;
							border: 2px solid orange;
							/* border-radius: 10px; */
						}

						/* #serviceType{
							z-index: 9998 !important;
						} */
						.modal {
							z-index: 9998;
							/* set to a lower value */
						}
					</style>

					<script type="text/javascript">
						$(document).ready(function () {
							$(".adminMerchants").select2();
						});
					</script>
				</head>

				<body id="kt_body"
					class="header-fixed header-tablet-and-mobile-fixed toolbar-enabled toolbar-fixed toolbar-tablet-and-mobile-fixed aside-enabled aside-fixed"
					style="-kt-toolbar-height: 55px; - -kt-toolbar-height-tablet-and-mobile: 55px">
					<div class="content d-flex flex-column flex-column-fluid" id="kt_content">
						<div class="toolbar" id="kt_toolbar">
							<!--begin::Container-->
							<div id="kt_toolbar_container" class="container-fluid d-flex flex-stack">
								<div data-kt-swapper="true" data-kt-swapper-mode="prepend"
									data-kt-swapper-parent="{default: '#kt_content_container', 'lg': '#kt_toolbar_container'}"
									class="page-title d-flex align-items-center me-3 flex-wrap mb-5 mb-lg-0 lh-1">
									<!--begin::Title-->
									<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">
										Create Subscription</h1>
									<!--end::Title-->
									<!--begin::Separator-->
									<span class="h-20px border-gray-200 border-start mx-4"></span>
									<!--end::Separator-->
									<!--begin::Breadcrumb-->
									<ul class="breadcrumb breadcrumb-separatorless fw-semibold fs-7 my-1">
										<!--begin::Item-->
										<li class="breadcrumb-item text-muted"><a href="home"
												class="text-muted text-hover-primary">SI</a></li>
										<!--end::Item-->
										<!--begin::Item-->

										<!--end::Item-->
										<!--begin::Item-->
										<!-- 										<li class="breadcrumb-item text-muted">Reports</li> -->
										<!--end::Item-->
										<!--begin::Item-->
										<li class="breadcrumb-item"><span class="bullet bg-gray-200 w-5px h-2px"></span>
										</li>
										<!--end::Item-->
										<!--begin::Item-->
										<li class="breadcrumb-item text-dark">Create Subscription</li>
										<!--end::Item-->
									</ul>
									<!--end::Breadcrumb-->
								</div>
								<!--end::Page title-->

							</div>
						</div>

						<div class="post d-flex flex-column-fluid" id="kt_post">
							<!--begin::Container-->
							<div id="kt_content_container" class="container-xxl">
								<div class="row my-5">
									<div class="col">
										<div class="card">
											<div class="card-body">
												<div class="row my-3 align-items-center">

													<div class="col-lg-4 my-2">
														<label class="d-flex align-items-center fs-6 fw-bold mb-2">
															<span class="required">Merchant</span>
														</label>

														<s:if
															test="%{#session.USER.UserType.name()=='ADMIN' || #session.USER.UserType.name()=='SUBADMIN' || #session.USER_TYPE.name()=='SUPERADMIN'|| #session.USER_TYPE.name()=='RESELLER'}">
															<!-- <s:select name="merchant"
													class="form-select form-select-solid adminMerchants"
													id="merchant" headerKey="" headerValue="ALL"
													list="merchantList" listKey="emailId"
													listValue="businessName" autocomplete="off"
													data-control="select2" /> -->

															<s:select name="merchant"
																class="form-select form-select-solid adminMerchants"
																id="merchant" headerKey=""
																headerValue="Please Select Merchant" list="merchantList"
																listKey="payId" listValue="businessName"
																autocomplete="off" />
															<span id="merchantError" class="error"></span>

														</s:if>
														<s:else>

															<s:select name="merchant"
																class="form-select form-select-solid adminMerchants"
																id="merchant" list="merchantList" listKey="payId"
																listValue="businessName" autocomplete="off" />
															<span id="merchantError" class="error"></span>

															<!-- <s:select name="merchant"
													class="form-select form-select-solid adminMerchants d-none"
													id="merchant" list="merchantList" listKey="emailId"
													data-control="select2" listValue="businessName"
													autocomplete="off" /> -->
														</s:else>


													</div>
													<div class=" col-lg-4 my-2">
														<label class="d-flex align-items-center fs-6 fw-bold mb-2">
															<span class="required">Plan Id</span>
														</label> <select name="planId" id="planId"
															class="form-select form-select-solid adminMerchants">
															<option value="" selected="selected">Please Select
																plan ID</option>


														</select> <span id="planIdError" class="error"></span>

													</div>

													<!-- <div class="col-lg-4 my-2">
										<label class="d-flex align-items-center fs-6 fw-bold mb-2">
											<span class="required">Current Start</span>
										</label>
										<s:textfield id="currentStart"
											class="form-control form-control-solid" name="currentStart"
											type="number" autocomplete="off"></s:textfield>
										<span id="currentStartError" class="error"></span>
									</div> -->
												
													<!-- <div class="col-lg-4 my-2">
										<label class="d-flex align-items-center fs-6 fw-bold mb-2">
											<span class="required">Quantity</span>
										</label>
										<s:textfield id="quantity"
											class="form-control form-control-solid" name="quantity"
											type="number" autocomplete="off"></s:textfield>
										<span id="quantityError" class="error"></span>
									</div> -->


													<div class=" col-lg-4 my-2">
														<label class="d-flex align-items-center fs-6 fw-bold mb-2">
															<span class="required">Start At</span>
														</label> <span
															class="svg-icon svg-icon-2 position-absolute mx-4">
															<svg width="24" height="24" viewBox="0 0 24 24" fill="none"
																xmlns="http://www.w3.org/2000/svg">
																<path opacity="0.3"
																	d="M21 22H3C2.4 22 2 21.6 2 21V5C2 4.4 2.4 4 3 4H21C21.6 4 22 4.4 22 5V21C22 21.6 21.6 22 21 22Z"
																	fill="currentColor"></path>
																<path
																	d="M6 6C5.4 6 5 5.6 5 5V3C5 2.4 5.4 2 6 2C6.6 2 7 2.4 7 3V5C7 5.6 6.6 6 6 6ZM11 5V3C11 2.4 10.6 2 10 2C9.4 2 9 2.4 9 3V5C9 5.6 9.4 6 10 6C10.6 6 11 5.6 11 5ZM15 5V3C15 2.4 14.6 2 14 2C13.4 2 13 2.4 13 3V5C13 5.6 13.4 6 14 6C14.6 6 15 5.6 15 5ZM19 5V3C19 2.4 18.6 2 18 2C17.4 2 17 2.4 17 3V5C17 5.6 17.4 6 18 6C18.6 6 19 5.6 19 5Z"
																	fill="currentColor"></path>
																<path
																	d="M8.8 13.1C9.2 13.1 9.5 13 9.7 12.8C9.9 12.6 10.1 12.3 10.1 11.9C10.1 11.6 10 11.3 9.8 11.1C9.6 10.9 9.3 10.8 9 10.8C8.8 10.8 8.59999 10.8 8.39999 10.9C8.19999 11 8.1 11.1 8 11.2C7.9 11.3 7.8 11.4 7.7 11.6C7.6 11.8 7.5 11.9 7.5 12.1C7.5 12.2 7.4 12.2 7.3 12.3C7.2 12.4 7.09999 12.4 6.89999 12.4C6.69999 12.4 6.6 12.3 6.5 12.2C6.4 12.1 6.3 11.9 6.3 11.7C6.3 11.5 6.4 11.3 6.5 11.1C6.6 10.9 6.8 10.7 7 10.5C7.2 10.3 7.49999 10.1 7.89999 10C8.29999 9.90003 8.60001 9.80003 9.10001 9.80003C9.50001 9.80003 9.80001 9.90003 10.1 10C10.4 10.1 10.7 10.3 10.9 10.4C11.1 10.5 11.3 10.8 11.4 11.1C11.5 11.4 11.6 11.6 11.6 11.9C11.6 12.3 11.5 12.6 11.3 12.9C11.1 13.2 10.9 13.5 10.6 13.7C10.9 13.9 11.2 14.1 11.4 14.3C11.6 14.5 11.8 14.7 11.9 15C12 15.3 12.1 15.5 12.1 15.8C12.1 16.2 12 16.5 11.9 16.8C11.8 17.1 11.5 17.4 11.3 17.7C11.1 18 10.7 18.2 10.3 18.3C9.9 18.4 9.5 18.5 9 18.5C8.5 18.5 8.1 18.4 7.7 18.2C7.3 18 7 17.8 6.8 17.6C6.6 17.4 6.4 17.1 6.3 16.8C6.2 16.5 6.10001 16.3 6.10001 16.1C6.10001 15.9 6.2 15.7 6.3 15.6C6.4 15.5 6.6 15.4 6.8 15.4C6.9 15.4 7.00001 15.4 7.10001 15.5C7.20001 15.6 7.3 15.6 7.3 15.7C7.5 16.2 7.7 16.6 8 16.9C8.3 17.2 8.6 17.3 9 17.3C9.2 17.3 9.5 17.2 9.7 17.1C9.9 17 10.1 16.8 10.3 16.6C10.5 16.4 10.5 16.1 10.5 15.8C10.5 15.3 10.4 15 10.1 14.7C9.80001 14.4 9.50001 14.3 9.10001 14.3C9.00001 14.3 8.9 14.3 8.7 14.3C8.5 14.3 8.39999 14.3 8.39999 14.3C8.19999 14.3 7.99999 14.2 7.89999 14.1C7.79999 14 7.7 13.8 7.7 13.7C7.7 13.5 7.79999 13.4 7.89999 13.2C7.99999 13 8.2 13 8.5 13H8.8V13.1ZM15.3 17.5V12.2C14.3 13 13.6 13.3 13.3 13.3C13.1 13.3 13 13.2 12.9 13.1C12.8 13 12.7 12.8 12.7 12.6C12.7 12.4 12.8 12.3 12.9 12.2C13 12.1 13.2 12 13.6 11.8C14.1 11.6 14.5 11.3 14.7 11.1C14.9 10.9 15.2 10.6 15.5 10.3C15.8 10 15.9 9.80003 15.9 9.70003C15.9 9.60003 16.1 9.60004 16.3 9.60004C16.5 9.60004 16.7 9.70003 16.8 9.80003C16.9 9.90003 17 10.2 17 10.5V17.2C17 18 16.7 18.4 16.2 18.4C16 18.4 15.8 18.3 15.6 18.2C15.4 18.1 15.3 17.8 15.3 17.5Z"
																	fill="currentColor"></path>
															</svg>
														</span>
														<!--end::Svg Icon-->
														<!--end::Icon-->
														<!--begin::Datepicker-->
														<input
															class="form-control form-control-solid ps-12 flatpickr-input"
															placeholder="Select a date" name="startAt" id="startAt"
															type="text" onblur="calMonth()">
														<!--end::Datepicker-->
													</div>
												</div>





												<div class=" row my-3 align-items-center">
													<div class="col-lg-4 my-2">
														<label class="d-flex align-items-center fs-6 fw-bold mb-2">
															<span class="required">End At</span>
														</label> <span
															class="svg-icon svg-icon-2 position-absolute mx-4">
															<svg width="24" height="24" viewBox="0 0 24 24" fill="none"
																xmlns="http://www.w3.org/2000/svg">
																<path opacity="0.3"
																	d="M21 22H3C2.4 22 2 21.6 2 21V5C2 4.4 2.4 4 3 4H21C21.6 4 22 4.4 22 5V21C22 21.6 21.6 22 21 22Z"
																	fill="currentColor"></path>
																<path
																	d="M6 6C5.4 6 5 5.6 5 5V3C5 2.4 5.4 2 6 2C6.6 2 7 2.4 7 3V5C7 5.6 6.6 6 6 6ZM11 5V3C11 2.4 10.6 2 10 2C9.4 2 9 2.4 9 3V5C9 5.6 9.4 6 10 6C10.6 6 11 5.6 11 5ZM15 5V3C15 2.4 14.6 2 14 2C13.4 2 13 2.4 13 3V5C13 5.6 13.4 6 14 6C14.6 6 15 5.6 15 5ZM19 5V3C19 2.4 18.6 2 18 2C17.4 2 17 2.4 17 3V5C17 5.6 17.4 6 18 6C18.6 6 19 5.6 19 5Z"
																	fill="currentColor"></path>
																<path
																	d="M8.8 13.1C9.2 13.1 9.5 13 9.7 12.8C9.9 12.6 10.1 12.3 10.1 11.9C10.1 11.6 10 11.3 9.8 11.1C9.6 10.9 9.3 10.8 9 10.8C8.8 10.8 8.59999 10.8 8.39999 10.9C8.19999 11 8.1 11.1 8 11.2C7.9 11.3 7.8 11.4 7.7 11.6C7.6 11.8 7.5 11.9 7.5 12.1C7.5 12.2 7.4 12.2 7.3 12.3C7.2 12.4 7.09999 12.4 6.89999 12.4C6.69999 12.4 6.6 12.3 6.5 12.2C6.4 12.1 6.3 11.9 6.3 11.7C6.3 11.5 6.4 11.3 6.5 11.1C6.6 10.9 6.8 10.7 7 10.5C7.2 10.3 7.49999 10.1 7.89999 10C8.29999 9.90003 8.60001 9.80003 9.10001 9.80003C9.50001 9.80003 9.80001 9.90003 10.1 10C10.4 10.1 10.7 10.3 10.9 10.4C11.1 10.5 11.3 10.8 11.4 11.1C11.5 11.4 11.6 11.6 11.6 11.9C11.6 12.3 11.5 12.6 11.3 12.9C11.1 13.2 10.9 13.5 10.6 13.7C10.9 13.9 11.2 14.1 11.4 14.3C11.6 14.5 11.8 14.7 11.9 15C12 15.3 12.1 15.5 12.1 15.8C12.1 16.2 12 16.5 11.9 16.8C11.8 17.1 11.5 17.4 11.3 17.7C11.1 18 10.7 18.2 10.3 18.3C9.9 18.4 9.5 18.5 9 18.5C8.5 18.5 8.1 18.4 7.7 18.2C7.3 18 7 17.8 6.8 17.6C6.6 17.4 6.4 17.1 6.3 16.8C6.2 16.5 6.10001 16.3 6.10001 16.1C6.10001 15.9 6.2 15.7 6.3 15.6C6.4 15.5 6.6 15.4 6.8 15.4C6.9 15.4 7.00001 15.4 7.10001 15.5C7.20001 15.6 7.3 15.6 7.3 15.7C7.5 16.2 7.7 16.6 8 16.9C8.3 17.2 8.6 17.3 9 17.3C9.2 17.3 9.5 17.2 9.7 17.1C9.9 17 10.1 16.8 10.3 16.6C10.5 16.4 10.5 16.1 10.5 15.8C10.5 15.3 10.4 15 10.1 14.7C9.80001 14.4 9.50001 14.3 9.10001 14.3C9.00001 14.3 8.9 14.3 8.7 14.3C8.5 14.3 8.39999 14.3 8.39999 14.3C8.19999 14.3 7.99999 14.2 7.89999 14.1C7.79999 14 7.7 13.8 7.7 13.7C7.7 13.5 7.79999 13.4 7.89999 13.2C7.99999 13 8.2 13 8.5 13H8.8V13.1ZM15.3 17.5V12.2C14.3 13 13.6 13.3 13.3 13.3C13.1 13.3 13 13.2 12.9 13.1C12.8 13 12.7 12.8 12.7 12.6C12.7 12.4 12.8 12.3 12.9 12.2C13 12.1 13.2 12 13.6 11.8C14.1 11.6 14.5 11.3 14.7 11.1C14.9 10.9 15.2 10.6 15.5 10.3C15.8 10 15.9 9.80003 15.9 9.70003C15.9 9.60003 16.1 9.60004 16.3 9.60004C16.5 9.60004 16.7 9.70003 16.8 9.80003C16.9 9.90003 17 10.2 17 10.5V17.2C17 18 16.7 18.4 16.2 18.4C16 18.4 15.8 18.3 15.6 18.2C15.4 18.1 15.3 17.8 15.3 17.5Z"
																	fill="currentColor"></path>
															</svg>
														</span>
														<!--end::Svg Icon-->
														<!--end::Icon-->
														<!--begin::Datepicker-->
														<input
															class="form-control form-control-solid ps-12 flatpickr-input"
															placeholder="Select a date" name="endAt" id="endAt"
															type="text" onblur="calMonth()">
													</div>
												

													<!-- 													<div class=" col-lg-4 my-2"> -->
													<!-- 														<label class="d-flex align-items-center fs-6 fw-bold mb-2"> -->
													<%-- <span class="required">Custom Notify</span> --%>
														<!-- 														</label> -->
														<%-- <s:textfield id="customNotify" --%>
															<%-- class="form-control form-control-solid"
																name="customNotify" --%>
																<%-- type="text" value="" autocomplete="off"> --%>
																	<%-- </s:textfield> --%>
																		<!-- 													</div> -->
																		<div class=" col-lg-4 my-2">
																			<label
																				class="d-flex align-items-center fs-6 fw-bold mb-2">
																				<span class="required">Remaining
																					Count</span>
																			</label>
																			<s:textfield id="remainingCount"
																				class="form-control form-control-solid"
																				name="remainingCount" type="number"
																				value="" autocomplete="off">
																			</s:textfield>
																			<span id="remainingCountError"
																				class="error"></span>
																		</div>
																		<!-- 									<div class=" col-lg-4 my-2"> -->
																		<!-- 										<label class="d-flex align-items-center fs-6 fw-bold mb-2"> -->
																		<%-- <span class="required">Addons</span> --%>
																			<%-- </label> <select name="addons"
																					id="addons" --%>
																					<%--
																						class="form-select form-select-solid adminMerchants">
																						--%>
																						<!-- 											<option value="plan1">plan1</option> -->
																						<!-- 											<option value="plan2">plan1</option> -->
																						<!-- 											<option value="plan3">plan2</option> -->
																						<%-- </select> --%>
																							<!-- 									</div> -->
																							<div class=" col-lg-4 my-2">
																								<label
																									class="d-flex align-items-center fs-6 fw-bold mb-2">
																									<span
																										class="required">Charge
																										At</span>

																								</label>
																								<s:textfield
																									id="chargeAt"
																									class="form-control form-control-solid"
																									name="chargeAt"
																									type="number"
																									value=""
																									autocomplete="off">
																								</s:textfield>
																								<span id="chargeAtError"
																									class="error"></span>


																							</div>
																						</div>



																						<div class=" row my-3 align-items-center">

																							<!-- <div class=" col-lg-4 my-2">
										<label class="d-flex align-items-center fs-6 fw-bold mb-2">
											<span class="required">Current End</span>
										</label>
										<s:textfield id="currentEnd"
											class="form-control form-control-solid" name="currentEnd"
											type="number" value="" autocomplete="off">
										</s:textfield>
										<span id="currentEndError" class="error"></span>
									</div> -->


												</div>





												<div class=" row my-3 align-items-center">



													<div class=" col-lg-4 my-2">
														<label class="d-flex align-items-center fs-6 fw-bold mb-2">
															<span class="required">Total Count</span>
														</label>
														<s:textfield id="totalCount"
															class="form-control form-control-solid" name="totalCount"
															type="number" value="" autocomplete="off"></s:textfield>
														<span id="totalCountError" class="error"></span>
													</div>
													<div class=" col-lg-4 my-2">
														<label class="d-flex align-items-center fs-6 fw-bold mb-2">
															<span class="required">Paid Count</span>
														</label>
														<s:textfield id="paidCount"
															class="form-control form-control-solid" name="paidCount"
															type="number" value="" autocomplete="off">
														</s:textfield>
														<span id="paidCountError" class="error"></span>
													</div>
													<div class=" col-lg-4 my-2">
														<label class="d-flex align-items-center fs-6 fw-bold mb-2">
															<span class="required">Created At</span>
														</label> <span
															class="svg-icon svg-icon-2 position-absolute mx-4">
															<svg width="24" height="24" viewBox="0 0 24 24" fill="none"
																opacity="0.3"
																d="M21 22H3C2.4 22 2 21.6 2 21V5C2 4.4 2.4 4 3 4H21C21.6 4 22 4.4 22 5V21C22 21.6 21.6 22 21 22Z"
																fill="currentColor"> </path>
																<path
																	d="M6 6C5.4 6 5 5.6 5 5V3C5 2.4 5.4 2 6 2C6.6 2 7 2.4 7 3V5C7 5.6 6.6 6 6 6ZM11 5V3C11 2.4 10.6 2 10 2C9.4 2 9 2.4 9 3V5C9 5.6 9.4 6 10 6C10.6 6 11 5.6 11 5ZM15 5V3C15 2.4 14.6 2 14 2C13.4 2 13 2.4 13 3V5C13 5.6 13.4 6 14 6C14.6 6 15 5.6 15 5ZM19 5V3C19 2.4 18.6 2 18 2C17.4 2 17 2.4 17 3V5C17 5.6 17.4 6 18 6C18.6 6 19 5.6 19 5Z"
																	fill="currentColor"></path>
																<path
																	d="M8.8 13.1C9.2 13.1 9.5 13 9.7 12.8C9.9 12.6 10.1 12.3 10.1 11.9C10.1 11.6 10 11.3 9.8 11.1C9.6 10.9 9.3 10.8 9 10.8C8.8 10.8 8.59999 10.8 8.39999 10.9C8.19999 11 8.1 11.1 8 11.2C7.9 11.3 7.8 11.4 7.7 11.6C7.6 11.8 7.5 11.9 7.5 12.1C7.5 12.2 7.4 12.2 7.3 12.3C7.2 12.4 7.09999 12.4 6.89999 12.4C6.69999 12.4 6.6 12.3 6.5 12.2C6.4 12.1 6.3 11.9 6.3 11.7C6.3 11.5 6.4 11.3 6.5 11.1C6.6 10.9 6.8 10.7 7 10.5C7.2 10.3 7.49999 10.1 7.89999 10C8.29999 9.90003 8.60001 9.80003 9.10001 9.80003C9.50001 9.80003 9.80001 9.90003 10.1 10C10.4 10.1 10.7 10.3 10.9 10.4C11.1 10.5 11.3 10.8 11.4 11.1C11.5 11.4 11.6 11.6 11.6 11.9C11.6 12.3 11.5 12.6 11.3 12.9C11.1 13.2 10.9 13.5 10.6 13.7C10.9 13.9 11.2 14.1 11.4 14.3C11.6 14.5 11.8 14.7 11.9 15C12 15.3 12.1 15.5 12.1 15.8C12.1 16.2 12 16.5 11.9 16.8C11.8 17.1 11.5 17.4 11.3 17.7C11.1 18 10.7 18.2 10.3 18.3C9.9 18.4 9.5 18.5 9 18.5C8.5 18.5 8.1 18.4 7.7 18.2C7.3 18 7 17.8 6.8 17.6C6.6 17.4 6.4 17.1 6.3 16.8C6.2 16.5 6.10001 16.3 6.10001 16.1C6.10001 15.9 6.2 15.7 6.3 15.6C6.4 15.5 6.6 15.4 6.8 15.4C6.9 15.4 7.00001 15.4 7.10001 15.5C7.20001 15.6 7.3 15.6 7.3 15.7C7.5 16.2 7.7 16.6 8 16.9C8.3 17.2 8.6 17.3 9 17.3C9.2 17.3 9.5 17.2 9.7 17.1C9.9 17 10.1 16.8 10.3 16.6C10.5 16.4 10.5 16.1 10.5 15.8C10.5 15.3 10.4 15 10.1 14.7C9.80001 14.4 9.50001 14.3 9.10001 14.3C9.00001 14.3 8.9 14.3 8.7 14.3C8.5 14.3 8.39999 14.3 8.39999 14.3C8.19999 14.3 7.99999 14.2 7.89999 14.1C7.79999 14 7.7 13.8 7.7 13.7C7.7 13.5 7.79999 13.4 7.89999 13.2C7.99999 13 8.2 13 8.5 13H8.8V13.1ZM15.3 17.5V12.2C14.3 13 13.6 13.3 13.3 13.3C13.1 13.3 13 13.2 12.9 13.1C12.8 13 12.7 12.8 12.7 12.6C12.7 12.4 12.8 12.3 12.9 12.2C13 12.1 13.2 12 13.6 11.8C14.1 11.6 14.5 11.3 14.7 11.1C14.9 10.9 15.2 10.6 15.5 10.3C15.8 10 15.9 9.80003 15.9 9.70003C15.9 9.60003 16.1 9.60004 16.3 9.60004C16.5 9.60004 16.7 9.70003 16.8 9.80003C16.9 9.90003 17 10.2 17 10.5V17.2C17 18 16.7 18.4 16.2 18.4C16 18.4 15.8 18.3 15.6 18.2C15.4 18.1 15.3 17.8 15.3 17.5Z"
																	fill="currentColor"></path>
															</svg>
														</span>
														<!--end::Svg Icon-->
														<!--end::Icon-->
														<!--begin::Datepicker-->
														<input
															class="form-control form-control-solid ps-12 flatpickr-input"
															placeholder="Select a date" name="createdAt" id="createdAt"
															type="text">
														<!--end::Datepicker-->
													</div>

												</div>



												<div class=" row my-3 align-items-center">
													<div class=" col-lg-4 my-2">
														<label class="d-flex align-items-center fs-6 fw-bold mb-2">
															<span class="required">Additional Param 1</span>
														</label>
														<s:textfield id="additionalParam1"
															class="form-control form-control-solid"
															name="additionalParam1" type="text" value=""
															autocomplete="off"></s:textfield>
														<span id="additionalParam1Error" class="error"></span>
													</div>

													<div class=" col-lg-4 my-2">
														<label class="d-flex align-items-center fs-6 fw-bold mb-2">
															<span class="required">Additional Param 2</span>
														</label>
														<s:textfield id="additionalParam2"
															class="form-control form-control-solid"
															name="additionalParam2" type="text" value=""
															autocomplete="off"></s:textfield>
														<span id="additionalParam2Error" class="error"></span>
													</div>
													<div class=" col-lg-4 my-2">
														<label class="d-flex align-items-center fs-6 fw-bold mb-2">
															<span class="required">Additional Param 3</span>
														</label>
														<s:textfield id="additionalParam3"
															class="form-control form-control-solid"
															name="additionalParam3" type="text" value=""
															autocomplete="off"></s:textfield>
														<span id="additionalParam3Error" class="error"></span>

													</div>


												</div>

												<div class=" row my-3 align-items-center"></div>

												<div class=" row my-3 align-items-center">



													<div class=" col-lg-4 my-2">
														<label class="d-flex align-items-center fs-6 fw-bold mb-2">
															<span class="required">Expired By</span>
														</label> <span
															class="svg-icon svg-icon-2 position-absolute mx-4">
															<svg width="24" height="24" viewBox="0 0 24 24" fill="none"
																xmlns="http://www.w3.org/2000/svg">
																<path opacity="0.3"
																	d="M21 22H3C2.4 22 2 21.6 2 21V5C2 4.4 2.4 4 3 4H21C21.6 4 22 4.4 22 5V21C22 21.6 21.6 22 21 22Z"
																	fill="currentColor"></path>
																<path
																	d="M6 6C5.4 6 5 5.6 5 5V3C5 2.4 5.4 2 6 2C6.6 2 7 2.4 7 3V5C7 5.6 6.6 6 6 6ZM11 5V3C11 2.4 10.6 2 10 2C9.4 2 9 2.4 9 3V5C9 5.6 9.4 6 10 6C10.6 6 11 5.6 11 5ZM15 5V3C15 2.4 14.6 2 14 2C13.4 2 13 2.4 13 3V5C13 5.6 13.4 6 14 6C14.6 6 15 5.6 15 5ZM19 5V3C19 2.4 18.6 2 18 2C17.4 2 17 2.4 17 3V5C17 5.6 17.4 6 18 6C18.6 6 19 5.6 19 5Z"
																	fill="currentColor"></path>
																<path
																	d="M8.8 13.1C9.2 13.1 9.5 13 9.7 12.8C9.9 12.6 10.1 12.3 10.1 11.9C10.1 11.6 10 11.3 9.8 11.1C9.6 10.9 9.3 10.8 9 10.8C8.8 10.8 8.59999 10.8 8.39999 10.9C8.19999 11 8.1 11.1 8 11.2C7.9 11.3 7.8 11.4 7.7 11.6C7.6 11.8 7.5 11.9 7.5 12.1C7.5 12.2 7.4 12.2 7.3 12.3C7.2 12.4 7.09999 12.4 6.89999 12.4C6.69999 12.4 6.6 12.3 6.5 12.2C6.4 12.1 6.3 11.9 6.3 11.7C6.3 11.5 6.4 11.3 6.5 11.1C6.6 10.9 6.8 10.7 7 10.5C7.2 10.3 7.49999 10.1 7.89999 10C8.29999 9.90003 8.60001 9.80003 9.10001 9.80003C9.50001 9.80003 9.80001 9.90003 10.1 10C10.4 10.1 10.7 10.3 10.9 10.4C11.1 10.5 11.3 10.8 11.4 11.1C11.5 11.4 11.6 11.6 11.6 11.9C11.6 12.3 11.5 12.6 11.3 12.9C11.1 13.2 10.9 13.5 10.6 13.7C10.9 13.9 11.2 14.1 11.4 14.3C11.6 14.5 11.8 14.7 11.9 15C12 15.3 12.1 15.5 12.1 15.8C12.1 16.2 12 16.5 11.9 16.8C11.8 17.1 11.5 17.4 11.3 17.7C11.1 18 10.7 18.2 10.3 18.3C9.9 18.4 9.5 18.5 9 18.5C8.5 18.5 8.1 18.4 7.7 18.2C7.3 18 7 17.8 6.8 17.6C6.6 17.4 6.4 17.1 6.3 16.8C6.2 16.5 6.10001 16.3 6.10001 16.1C6.10001 15.9 6.2 15.7 6.3 15.6C6.4 15.5 6.6 15.4 6.8 15.4C6.9 15.4 7.00001 15.4 7.10001 15.5C7.20001 15.6 7.3 15.6 7.3 15.7C7.5 16.2 7.7 16.6 8 16.9C8.3 17.2 8.6 17.3 9 17.3C9.2 17.3 9.5 17.2 9.7 17.1C9.9 17 10.1 16.8 10.3 16.6C10.5 16.4 10.5 16.1 10.5 15.8C10.5 15.3 10.4 15 10.1 14.7C9.80001 14.4 9.50001 14.3 9.10001 14.3C9.00001 14.3 8.9 14.3 8.7 14.3C8.5 14.3 8.39999 14.3 8.39999 14.3C8.19999 14.3 7.99999 14.2 7.89999 14.1C7.79999 14 7.7 13.8 7.7 13.7C7.7 13.5 7.79999 13.4 7.89999 13.2C7.99999 13 8.2 13 8.5 13H8.8V13.1ZM15.3 17.5V12.2C14.3 13 13.6 13.3 13.3 13.3C13.1 13.3 13 13.2 12.9 13.1C12.8 13 12.7 12.8 12.7 12.6C12.7 12.4 12.8 12.3 12.9 12.2C13 12.1 13.2 12 13.6 11.8C14.1 11.6 14.5 11.3 14.7 11.1C14.9 10.9 15.2 10.6 15.5 10.3C15.8 10 15.9 9.80003 15.9 9.70003C15.9 9.60003 16.1 9.60004 16.3 9.60004C16.5 9.60004 16.7 9.70003 16.8 9.80003C16.9 9.90003 17 10.2 17 10.5V17.2C17 18 16.7 18.4 16.2 18.4C16 18.4 15.8 18.3 15.6 18.2C15.4 18.1 15.3 17.8 15.3 17.5Z"
																	fill="currentColor"></path>
															</svg>
														</span>
														<!--end::Svg Icon-->
														<!--end::Icon-->
														<!--begin::Datepicker-->
														<input
															class="form-control form-control-solid ps-12 flatpickr-input"
															placeholder="Select a date" name="expiredBy" id="expiredBy"
															type="text">
														<!--end::Datepicker-->
													</div>

													<div class="col-lg-4 my-2">
														<label class="d-flex align-items-center fs-6 fw-bold mb-2">
															<span class="required">Changes Scheduled At</span>
														</label> <span
															class="svg-icon svg-icon-2 position-absolute mx-4">
															<svg width="24" height="24" viewBox="0 0 24 24" fill="none"
																xmlns="http://www.w3.org/2000/svg">
																<path opacity="0.3"
																	d="M21 22H3C2.4 22 2 21.6 2 21V5C2 4.4 2.4 4 3 4H21C21.6 4 22 4.4 22 5V21C22 21.6 21.6 22 21 22Z"
																	fill="currentColor"></path>
																<path
																	d="M6 6C5.4 6 5 5.6 5 5V3C5 2.4 5.4 2 6 2C6.6 2 7 2.4 7 3V5C7 5.6 6.6 6 6 6ZM11 5V3C11 2.4 10.6 2 10 2C9.4 2 9 2.4 9 3V5C9 5.6 9.4 6 10 6C10.6 6 11 5.6 11 5ZM15 5V3C15 2.4 14.6 2 14 2C13.4 2 13 2.4 13 3V5C13 5.6 13.4 6 14 6C14.6 6 15 5.6 15 5ZM19 5V3C19 2.4 18.6 2 18 2C17.4 2 17 2.4 17 3V5C17 5.6 17.4 6 18 6C18.6 6 19 5.6 19 5Z"
																	fill="currentColor"></path>
																<path
																	d="M8.8 13.1C9.2 13.1 9.5 13 9.7 12.8C9.9 12.6 10.1 12.3 10.1 11.9C10.1 11.6 10 11.3 9.8 11.1C9.6 10.9 9.3 10.8 9 10.8C8.8 10.8 8.59999 10.8 8.39999 10.9C8.19999 11 8.1 11.1 8 11.2C7.9 11.3 7.8 11.4 7.7 11.6C7.6 11.8 7.5 11.9 7.5 12.1C7.5 12.2 7.4 12.2 7.3 12.3C7.2 12.4 7.09999 12.4 6.89999 12.4C6.69999 12.4 6.6 12.3 6.5 12.2C6.4 12.1 6.3 11.9 6.3 11.7C6.3 11.5 6.4 11.3 6.5 11.1C6.6 10.9 6.8 10.7 7 10.5C7.2 10.3 7.49999 10.1 7.89999 10C8.29999 9.90003 8.60001 9.80003 9.10001 9.80003C9.50001 9.80003 9.80001 9.90003 10.1 10C10.4 10.1 10.7 10.3 10.9 10.4C11.1 10.5 11.3 10.8 11.4 11.1C11.5 11.4 11.6 11.6 11.6 11.9C11.6 12.3 11.5 12.6 11.3 12.9C11.1 13.2 10.9 13.5 10.6 13.7C10.9 13.9 11.2 14.1 11.4 14.3C11.6 14.5 11.8 14.7 11.9 15C12 15.3 12.1 15.5 12.1 15.8C12.1 16.2 12 16.5 11.9 16.8C11.8 17.1 11.5 17.4 11.3 17.7C11.1 18 10.7 18.2 10.3 18.3C9.9 18.4 9.5 18.5 9 18.5C8.5 18.5 8.1 18.4 7.7 18.2C7.3 18 7 17.8 6.8 17.6C6.6 17.4 6.4 17.1 6.3 16.8C6.2 16.5 6.10001 16.3 6.10001 16.1C6.10001 15.9 6.2 15.7 6.3 15.6C6.4 15.5 6.6 15.4 6.8 15.4C6.9 15.4 7.00001 15.4 7.10001 15.5C7.20001 15.6 7.3 15.6 7.3 15.7C7.5 16.2 7.7 16.6 8 16.9C8.3 17.2 8.6 17.3 9 17.3C9.2 17.3 9.5 17.2 9.7 17.1C9.9 17 10.1 16.8 10.3 16.6C10.5 16.4 10.5 16.1 10.5 15.8C10.5 15.3 10.4 15 10.1 14.7C9.80001 14.4 9.50001 14.3 9.10001 14.3C9.00001 14.3 8.9 14.3 8.7 14.3C8.5 14.3 8.39999 14.3 8.39999 14.3C8.19999 14.3 7.99999 14.2 7.89999 14.1C7.79999 14 7.7 13.8 7.7 13.7C7.7 13.5 7.79999 13.4 7.89999 13.2C7.99999 13 8.2 13 8.5 13H8.8V13.1ZM15.3 17.5V12.2C14.3 13 13.6 13.3 13.3 13.3C13.1 13.3 13 13.2 12.9 13.1C12.8 13 12.7 12.8 12.7 12.6C12.7 12.4 12.8 12.3 12.9 12.2C13 12.1 13.2 12 13.6 11.8C14.1 11.6 14.5 11.3 14.7 11.1C14.9 10.9 15.2 10.6 15.5 10.3C15.8 10 15.9 9.80003 15.9 9.70003C15.9 9.60003 16.1 9.60004 16.3 9.60004C16.5 9.60004 16.7 9.70003 16.8 9.80003C16.9 9.90003 17 10.2 17 10.5V17.2C17 18 16.7 18.4 16.2 18.4C16 18.4 15.8 18.3 15.6 18.2C15.4 18.1 15.3 17.8 15.3 17.5Z"
																	fill="currentColor"></path>
															</svg>
														</span>
														<!--end::Svg Icon-->
														<!--end::Icon-->
														<!--begin::Datepicker-->
														<input
															class="form-control form-control-solid ps-12 flatpickr-input"
															placeholder="Select a date" name="changesScheduledAt"
															id="changesScheduledAt" type="text">
													</div>
												</div>
												<div class=" row my-3 align-items-center"></div>
												<div style="text-align: end;">
													<button type="button" class="btn btn-primary" id="done"
														onclick="save()">Save</button>
												</div>
											</div>
										</div>
									</div>
								</div>

								<div class="card">
									<div class="card-body ">

										<div class="row my-5 mt-4">
											<div class="col">
												<div class="card">
													<div class="card-body">
														<!--begin::Input group-->
														<div class="row g-9 mb-8 justify-content-end">
															<div class="col-lg-4 col-sm-12 col-md-6">
																<select name="currency" data-control="select2"
																	data-placeholder="Actions" id="actions11"
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
																		<a class="toggle-vis"
																			data-column="0">Merchant</a> <a
																			class="toggle-vis" data-column="1">Plan Id
																		</a> <a class="toggle-vis" data-column="2">Total
																			Count</a> <a class="toggle-vis"
																			data-column="3">Quantity</a> <a
																			class="toggle-vis" data-column="4">Start At
																		</a> <a class="toggle-vis"
																			data-column="5">Expire By</a> <a
																			class="toggle-vis" data-column="6">Custom
																			Notify</a> <a class="toggle-vis"
																			data-column="7">Addons</a> <a
																			class="toggle-vis" data-column="8">Offer
																			Id</a> <a class="toggle-vis"
																			data-column="9">Notify Phone</a> <a
																			class="toggle-vis" data-column="10">Notify
																			Email</a> <a class="toggle-vis"
																			data-column="11">Additional Param
																			1</a> <a class="toggle-vis"
																			data-column="12">Additional
																			Param 2</a> <a class="toggle-vis"
																			data-column="13">Additional
																			Param 3</a> <a class="toggle-vis"
																			data-column="14">Key</a>
																	</div>
																</div>
															</div>
														</div>
														<div class="row g-9 mb-8">
															<div class="table-responsive">
																<table id="txnResultDataTable"
																	class="table table-striped table-row-bordered gy-5 gs-7 dataTable no-footer">
																	<!-- 													<thead> -->
																	<!-- 														<tr class="fw-bold fs-6 text-gray-800"> -->
																	<!-- 															<th class="min-w-90px">Merchant</th> -->
																	<!-- 															<th class="min-w-90px">Plan Id</th> -->
																	<!-- 															<th class="min-w-90px">Total Count</th> -->
																	<!-- 															<th class="min-w-90px">Quantity</th> -->
																	<!-- 															<th class="min-w-90px">Start At</th> -->
																	<!-- 															<th class="min-w-90px">Expire By</th> -->
																	<!-- 															<th class="min-w-90px">Custom Notify</th> -->
																	<!-- 															<th class="min-w-90px">Addons</th> -->
																	<!-- 															<th class="min-w-90px">offer Id</th> -->
																	<!-- 															<th class="min-w-90px">Notify Phone</th> -->
																	<!-- 															<th class="min-w-90px">Notify Email</th> -->
																	<!-- 															<th class="min-w-90px">Additional Param 1</th> -->
																	<!-- 															<th class="min-w-90px">Additional Param 2</th> -->
																	<!-- 															<th class="min-w-90px">Additional Param 3</th> -->
																	<!-- 															<th class="min-w-90px">Key</th> -->
																	<!-- 															<th class="min-w-90px">Action</th> -->
																	<!-- 														</tr> -->
																	<!-- 													</thead> -->
																	<!-- 													<tbody> -->
																	<!-- 														<tr class="fw-bold fs-6 text-gray-800"> -->
																	<!-- 															<th class="min-w-90px">Ajay</th> -->
																	<!-- 															<th class="min-w-90px">Amazon Prime</th> -->
																	<!-- 															<th class="min-w-90px">Standard</th> -->
																	<!-- 															<th class="min-w-90px">Free 1 Year</th> -->
																	<!-- 															<th class="min-w-90px">1100</th> -->
																	<!-- 															<th class="min-w-90px">356</th> -->
																	<!-- 															<th class="min-w-90px">Standard</th> -->
																	<!-- 															<th class="min-w-90px">8</th> -->
																	<!-- 															<th class="min-w-90px">768768686</th> -->
																	<!-- 															<th class="min-w-90px">6000</th> -->
																	<!-- 															<th class="min-w-90px">7hjgg</th> -->
																	<!-- 															<th class="min-w-90px">ghjg7</th> -->
																	<!-- 															<th class="min-w-90px">6578667678587</th> -->
																	<!-- 															<th class="min-w-90px">6897897878978</th> -->
																	<!-- 															<th class="min-w-90px">18</th> -->
																	<!-- 															<th class="min-w-90px"><button type="button" -->
																	<!-- 																	class="btn btn-primary" id="delete" onclick="Edit()">Edit</button></th> -->
																	<!-- 														</tr> -->
																	<!-- 													</tbody> -->
																	<!-- 													<tfoot> -->
																	<!-- 														<tr class="fw-bold fs-6 text-gray-800"> -->
																	<!-- 															<th class="min-w-90px"></th> -->
																	<!-- 															<th class="min-w-90px"></th> -->
																	<!-- 															<th class="min-w-90px"></th> -->
																	<!-- 															<th class="min-w-90px"></th> -->
																	<!-- 															<th class="min-w-90px"></th> -->
																	<!-- 															<th class="min-w-90px"></th> -->
																	<!-- 															<th class="min-w-90px"></th> -->
																	<!-- 															<th class="min-w-90px"></th> -->
																	<!-- 															<th class="min-w-90px"></th> -->
																	<!-- 															<th class="min-w-90px"></th> -->
																	<!-- 															<th class="min-w-90px"></th> -->
																	<!-- 															<th class="min-w-90px"></th> -->
																	<!-- 															<th class="min-w-90px"></th> -->
																	<!-- 															<th class="min-w-90px"></th> -->
																	<!-- 															<th class="min-w-90px"></th> -->
																	<!-- 															<th class="min-w-90px"></th> -->

																	<!-- 														</tr> -->
																	<!-- 													</tfoot> -->
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
					<div class="modal" tabindex="-1" style="margin-top: 25vh;">
						<div class="modal-dialog">
							<div class="modal-content">
								<div class="modal-header">
									<h5 class="modal-title">Service Activation</h5>
									<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"
										onclick="closeModal()"></button>
								</div>
								<div class="modal-body">
									<div class="row">
										<div class=" col-lg-12 my-2">
											<label class="d-flex align-items-center fs-6 fw-bold mb-2">
												<span class="required">Please Select Service</span>
											</label> <select name="serviceType" id="serviceType"
												class="form-select form-select-solid">
												<option value="Resume">Resume</option>
												<option value="Pause">Pause</option>
												<option value="Delete">Delete</option>
												<option value="Cancel">Cancel</option>

											</select>

										</div>

									</div>
								</div>
								<div class="modal-footer">
									<button type="button" class="btn btn-secondary" data-bs-dismiss="modal"
										onclick="closeModal()">Close</button>
									<button type="button" class="btn btn-primary" onclick="saveDetails()">Save
										changes</button>
								</div>
							</div>
						</div>
					</div>
					<script src="../assets/plugins/custom/datatables/datatables.bundle.js"></script>
					<script type="text/javascript">
						debugger
						var urls = new URL(window.location.href);
						var domain = urls.origin;
						$.ajax({

							url: domain + "/siws/api/v1/plans/",
							//url : "http://localhost:8080/api/v1/plans",
							type: 'GET',
							headers: {
								'accept': '*/*',
								'Content-Type': 'application/json',
								'Cookie': 'JSESSIONID=4A38E87577BDC90D95675E11D4445BC5',
								'merchantId': "<s:property value='%{#session.USER.payId}'/>",
								'customerId': 1234,
								'key': 1234,
							},

							success: function (data) {
								debugger
								var response = data.payLoad;
								//planId
								for (var i = 0; i < response.length; i++) {
									$("#planId").append(
										"<option value=" + response[i].planId + ">"
										+ response[i].name + "</option>");
								}

							},
							error: function (data, textStatus, jqXHR) {
								alert(data.message);

							}
						});

						$("#planId").select2();

						$("#startAt").flatpickr({
							maxDate: new Date(),
							dateFormat: 'Y-m-d',
							defaultDate: "today"
						});
						$("#endAt").flatpickr({

							dateFormat: 'Y-m-d',
							defaultDate: "today"
						});

						$("#createdAt").flatpickr({
							maxDate: new Date(),
							dateFormat: 'Y-m-d',
							defaultDate: "today"
						});

						$("#expiredBy").flatpickr({
							//maxDate : new Date(),
							dateFormat: 'Y-m-d',
							defaultDate: "today"
						});
						$("#changesScheduledAt").flatpickr({
							maxDate: new Date(),
							dateFormat: 'Y-m-d',
							defaultDate: "today"
						});

						$(".modal").hide();
						$('a.toggle-vis').on('click', function (e) {
							debugger
							e.preventDefault();
							table = $('#txnResultDataTable').DataTable();
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

							// document.querySelector('.buttons-excel').click();
							// document.querySelector('.buttons-print').click();

						}

						function save() {

							debugger
							var valid = true;
							if (!$("#merchant").val()) {
								document.getElementById("merchantError").innerHTML = "Merchant Name Is Required";
								valid = false;
							} else {
								document.getElementById("merchantError").innerHTML = "";
							}

							if (!$("#planId").val()) {
								document.getElementById("planIdError").innerHTML = "Plan Id Is Required";
								valid = false;
							} else {
								document.getElementById("planIdError").innerHTML = "";
							}
							// if (!$("#currentStart").val()) {
							// 	document.getElementById("currentStartError").innerHTML = "Current Start Is Required";
							// 	valid = false;
							// } else {
							// 	document.getElementById("currentStartError").innerHTML = "";
							// }

							// if (!$("#quantity").val()) {
							// 	document.getElementById("quantityError").innerHTML = "Quantity Is Required";
							// 	valid = false;
							// } else {
							// 	document.getElementById("quantityError").innerHTML = "";
							// }
							if (!$("#remainingCount").val()) {
								document.getElementById("remainingCountError").innerHTML = "Remaining Count Is Required";
								valid = false;
							} else {
								document.getElementById("remainingCountError").innerHTML = "";
							}
							if (!$("#chargeAt").val()) {
								document.getElementById("chargeAtError").innerHTML = "Charge At Is Required";
								valid = false;
							} else {
								document.getElementById("chargeAtError").innerHTML = "";
							}
							// if (!$("#currentEnd").val()) {
							// 	document.getElementById("currentEndError").innerHTML = "Current End Is Required";
							// 	valid = false;
							// } else {
							// 	document.getElementById("currentEndError").innerHTML = "";
							// }
							if (!$("#totalCount").val()) {
								document.getElementById("totalCountError").innerHTML = "Total Count Is Required";
								valid = false;
							} else {
								document.getElementById("totalCountError").innerHTML = "";
							}

							if (!$("#paidCount").val()) {
								document.getElementById("paidCountError").innerHTML = "Paid Count Is Required";
								valid = false;
							} else {
								document.getElementById("paidCountError").innerHTML = "";
							}
							if (!$("#additionalParam1").val()) {
								document.getElementById("additionalParam1Error").innerHTML = "Additional Param1 Is Required";
								valid = false;
							} else {
								document.getElementById("additionalParam2Error").innerHTML = "";
							}
							if (!$("#additionalParam2").val()) {
								document.getElementById("additionalParam2Error").innerHTML = "Additional Param2 Is Required";
								valid = false;
							} else {
								document.getElementById("additionalParam2Error").innerHTML = "";
							}
							if (!$("#additionalParam3").val()) {
								document.getElementById("additionalParam3Error").innerHTML = "Additional Param3 Is Required";
								valid = false;
							} else {
								document.getElementById("additionalParam3Error").innerHTML = "";
							}

							if (valid) {
								request = {

									"plan_id": $("#planId").val(),
									//"customer_id":$("#customerId").val(),
									"status": $("#active").val(),
									//"current_start" : $("#currentStart").val(),
									//"current_end" : $("#currentEnd").val(),
									//"quantity" : $("#quantity").val(),
									"charge_at": $("#chargeAt").val(),
									//"offer_id":$("#offerId").val(),
									"offer_id": "0",
									"start_at": $("#startAt").val(),
									"end_at": $("#endAt").val(),
									"auth_attempts": "001",
									"total_count": $("#totalCount").val(),
									"paid_count": "0",
									"created_at": $("#createdAt").val(),
									"expire_by": $("#expiredBy").val(),
									"short_url": "www.google.com",
									"change_scheduled_at": $("#changesScheduledAt").val(),

									"customer_notify": true,
									"has_scheduled_changes": true,

									"notes": {
										"additionalProp1": $("#additionalParam1").val(),
										"additionalProp2": $("#additionalParam2").val(),
										"additionalProp3": $("#additionalParam3").val(),
									}

								}
								var urls = new URL(window.location.href);
								var domain = urls.origin;
								$
									.ajax({

										url: domain + "/siws/api/v1/subscription/",
										//url : "http://localhost:8080/api/v1/subscription",
										type: 'POST',
										headers: {
											'accept': '*/*',
											'Content-Type': 'application/json',
											'Cookie': 'JSESSIONID=4A38E87577BDC90D95675E11D4445BC5',
											'merchantId': "<s:property value='%{#session.USER.payId}'/>",
											'customerId': 1234,
											'key': 1234,
										},
										data: JSON.stringify(request),
										success: function (data) {
											//window.load(data);
											//document.write(data);

											document.open();
											document.write(data);
											document.close();

										},
										error: function (data, textStatus, jqXHR) {

										}
									});
							}
						}
						function closeModal() {
							debugger
							$(".modal").hide();
						}
						var aa = "";
						var bb = "";
						function Edit(a, b) {
							$(".modal").show();
							aa = a;
							bb = b;
						}
						var request;
						function saveDetails() {

							var a = $("#serviceType").val();
							alert(a);
							var url = "";
							var urls = new URL(window.location.href);
							var domain = urls.origin;
							url = domain + "/siws/api/v1/subscription/";
							//url="http://localhost:8080/siws/api/v1/subscription/";

							if (a == "Resume") {
								url = url + "resume";
							} else if (a == "Pause") {
								url = url + "pause";
							} else if (a == "Cancel") {
								url = url + "cancel";
							} else {
								url = url + "delete";
							}

							$.ajax({
								url: url,
								type: 'POST',
								headers: {
									'accept': '*/*',
									'Content-Type': 'application/json',
									'Cookie': 'JSESSIONID=4A38E87577BDC90D95675E11D4445BC5',
									'merchantId': "<s:property value='%{#session.USER.payId}'/>",
									'subscriptionId': bb,
									'customerId': 1234,
									'key': 1234,
								},

								success: function (data) {
									//window.load(data);
									//document.write(data);
									alert(data);
									$(".modal").hide();

								},
								error: function (data, textStatus, jqXHR) {

								}
							});

						}
						var urls = new URL(window.location.href);
						var domain = urls.origin;
						$.ajax({
							//url : "http://localhost:8080/api/v1/subscription",
							url: domain + "/siws/api/v1/subscription/",
							type: 'GET',
							headers: {
								'accept': '*/*',
								'Content-Type': 'application/json',
								'Cookie': 'JSESSIONID=4A38E87577BDC90D95675E11D4445BC5',
								'merchantId': "<s:property value='%{#session.USER.payId}'/>",
								'customerId': 1234,
								'key': 1234,
							},
							success: function (data) {
								debugger

								var res = data.payLoad;
								var result = [];

								for (var i = 0; i < res.length; i++) {

									//var div = document.createElement('DIV'); 
									/* var html = $.parseHTML( '<button type="button" class="btn btn-primary" onclick('+"1063510831192553",res[i].subscriptionId+')>Primary</button>');
				
									div.append(html); */

									result.push([res[i].subscriptionId, res[i].auth_attempts,
									res[i].change_scheduled_at, res[i].charge_at,
									res[i].created_at, res[i].current_end,
									res[i].current_start, res[i].customer_id,
									res[i].customer_notify, res[i].end_at,
									res[i].ended_at, res[i].expire_by,
									res[i].has_scheduled_changes, res[i].offer_id,
									res[i].paid_count, res[i].plan_id, res[i].quantity,
									res[i].remaining_count, res[i].short_url,
									res[i].start_at, res[i].status, res[i].total_count,
									res[i].total_count]);
								}

								rendertable(result);

							},
							error: function (data, textStatus, jqXHR) {

							}
						});

						var columns = [{
							title: "SubscriptionId"
						}, {
							title: "Auth Attempts"
						}, {
							title: "Change Scheduled At"
						},

						{
							title: "ChargeAt"
						}, {
							title: "CreatedAt"
						}, {
							title: "Current End"
						}, {
							title: "Current Start"
						}, {
							title: "Customer Id"
						}, {
							title: "Customer Notify"
						}, {
							title: "End At"
						}, {
							title: "Ended At"
						}, {
							title: "Expire By"
						}, {
							title: "Has Scheduled Changes"
						}, {
							title: "OfferId"
						}, {
							title: "Paid Count"
						}, {
							title: "Plan Id"
						}, {
							title: "Quantity"
						}, {
							title: "Remaining Count"
						}, {
							title: "ShortUrl"
						}, {
							title: "StartAt"
						}, {
							title: "Status"
						}, {
							title: "Total Count"
						}, {
							title: "Action",
							render: function (data, type, row) {
								return renderActionData(data, row);
							}
						}];
						// Create the DataTable with the nested array data

						function rendertable(data) {
							$(document).ready(function () {
								$('#txnResultDataTable').DataTable({
									data: data,
									columns: columns
								});
							});

						}

						function renderActionData(data, row) {
							/* alert(data);
							alert(row); */

							return '<button type="button" class="btn btn-primary" onclick="Edit(1063510831192553,'
								+ row[0] + ')">Actions</button>';

						}
						function calMonth() {
							debugger
							var startDate = $("#startAt").val();
							var endDate = $("#endAt").val();

							$("#expiredBy").val(endDate);
							$("#changesScheduledAt").val(startDate);

							var diff = (new Date(endDate).getFullYear() - new Date(startDate)
								.getFullYear())
								* 12
								+ (new Date(endDate).getMonth() - new Date(startDate)
									.getMonth());
							diff = diff + 1;
							$("#remainingCount").val(diff);
							$("#totalCount").val(diff);
							$("#paidCount").val(0);

							$("#chargeAt").val(new Date(startDate).getDate());

						}
					</script>
				</body>

				</html>