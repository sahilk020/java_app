<%@page import="java.util.List" %>
	<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1" %>
		<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
		<%@ taglib uri="/struts-tags" prefix="s" %>
			<html dir="ltr" lang="en-US">

			<head>
				<title>
					<%=((String) request.getAttribute("mode")).toUpperCase()%> TRANSACTIONS
				</title>
				<link rel="shortcut icon" href="assets/media/images/paylogo.svg" />
				<!--begin::Fonts-->
				<link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Inter:300,400,500,600,700" />
				<!--end::Fonts-->
				<!--begin::Vendor Stylesheets(used by this page)-->
				<link href="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.css" rel="stylesheet"
					type="text/css" />
				<link href="../assets/plugins/custom/datatables/datatables.bundle.css" rel="stylesheet"
					type="text/css" />
				<!--end::Vendor Stylesheets-->
				<!--begin::Global Stylesheets Bundle(used by all pages)-->
				<link href="../assets/plugins/global/plugins.bundle.css" rel="stylesheet" type="text/css" />
				<link href="../assets/css/style.bundle.css" rel="stylesheet" type="text/css" />
				<link href="../assets/css/custom/custom-style.css" rel="stylesheet" type="text/css" />

				<script src="../assets/plugins/global/plugins.bundle.js"></script>
				<script src="../assets/js/scripts.bundle.js"></script>
				<link href="../css/select2.min.css" rel="stylesheet" />
				<script src="../js/jquery.select2.js" type="text/javascript"></script>

				<style>
					button.btn.btn-secondary.buttons-csv.buttons-html5 {
						background-color: var(--kt-brown-inverse) !important;
					}
				</style>
				<script type="text/javascript">
					$(document).ready(function () {
						$('#example').DataTable({
							dom: 'Bfrtip',
							buttons: [{

								extend: 'csv',
								text: 'CSV',
								title: 'Transactions',
								exportOptions: {
									columns: ':visible'
								},
							}]
						});
					});
				</script>
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
								<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">
									<%=((String) request.getAttribute("mode")).toUpperCase()%> TRANSACTIONS
								</h1>
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
									<li class="breadcrumb-item"><span class="bullet bg-gray-200 w-5px h-2px"></span>
									</li>
									<!--end::Item-->
									<!--begin::Item-->
									<li class="breadcrumb-item text-muted">Analytics</li>
									<!--end::Item-->
									<!--begin::Item-->
									<li class="breadcrumb-item"><span class="bullet bg-gray-200 w-5px h-2px"></span>
									</li>
									<!--end::Item-->
									<!--begin::Item-->
									<li class="breadcrumb-item text-dark">
										<%=((String) request.getAttribute("mode")).toUpperCase()%> TRANSACTIONS
									</li>
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
							<div class="row my-5">
								<div class="col">
									<div class="card">
										<div class="card-body">
											<div class="row my-3 align-items-center d-flex justify-content-start">
												<div class="table-responsive">
												<table id="example"
													class="table table-striped table-row-bordered gy-5 gs-7">
													<thead>
														<tr class="fw-bold fs-6 text-gray-800">
															<th>PAY_ID</th>
															<!-- 											<th>Merchant</th> -->
															<th style="width: 8% !important;">CUSTOMER EMAIL</th>
															<th>CUSTOMER PHONE</th>
															<th>PG REF NUM</th>
															<th>Transaction ID</th>
															<th style="width: 7% !important;">ORDER ID</th>
															<th style="width: 9% !important;">DATE</th>
															<th>Payment Method</th>
															<th>AMOUNT</th>
															<th>STATUS</th>
															<th style="width: 9% !important;">ACQUIRER TYPE</th>
															<th>IP</th>
															<!-- 											<th style="display: none">RRN</th> -->



														</tr>
													</thead>
													<tbody>
														<s:if test="{beans.size() > 0}">
															<s:iterator value="beans">
																<tr>
																	<td>
																		<s:property value="PAY_ID" />
																	</td>
																	<%-- <td>
																		<s:property value="businessName" />
																		</td> --%>
																		<td style="width: 8% !important;">
																			<s:property value="CUST_EMAIL" />
																		</td>
																		<td>
																			<s:property value="CUST_PHONE" />
																		</td>
																		<td>
																			<s:property value="PG_REF_NUM" />
																		</td>
																		<td>
																			<s:property value="TXN_ID" />
																		</td>
																		<td style="width: 7% !important;">
																			<s:property value="ORDER_ID" />
																		</td>
																		<td style="width: 9% !important;">
																			<s:property value="CREATE_DATE" />
																		</td>
																		<td>
																			<s:property value="PAYMENT_TYPE" />
																		</td>
																		<td>
																			<s:property value="AMOUNT" />
																		</td>
																		<td>
																			<s:property value="STATUS" />
																		</td>
																		<td style="width: 9% !important;">
																			<s:property value="ACQUIRER_TYPE" />
																		</td>
																		<td>
																			<s:property value="INTERNAL_CUST_IP" />
																		</td>
																		<%-- <td style="display: none">
																			<s:property value="RRN" />
																			</td> --%>
																</tr>
															</s:iterator>
														</s:if>
													</tbody>
													<tfoot>
														<tr>
															<td></td>
															<!-- 											<td></td> -->
															<td style="width: 8% !important;"></td>
															<td></td>
															<td></td>
															<td></td>
															<td style="width: 7% !important;"></td>
															<td style="width: 9% !important;"></td>
															<td></td>
															<td></td>
															<td></td>
															<td style="width: 9% !important;"></td>
															<td></td>
															<!-- 											<td style="display: none"></td> -->
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






				<script src="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.js"></script>
				<script src="https://cdn.amcharts.com/lib/5/index.js"></script>
				<script src="https://cdn.amcharts.com/lib/5/xy.js"></script>
				<script src="https://cdn.amcharts.com/lib/5/percent.js"></script>
				<script src="https://cdn.amcharts.com/lib/5/radar.js"></script>
				<script src="https://cdn.amcharts.com/lib/5/themes/Animated.js"></script>
				<script src="https://cdn.amcharts.com/lib/5/map.js"></script>
				<script src="https://cdn.amcharts.com/lib/5/geodata/worldLow.js"></script>
				<script src="https://cdn.amcharts.com/lib/5/geodata/continentsLow.js"></script>
				<script src="https://cdn.amcharts.com/lib/5/geodata/usaLow.js"></script>
				<script src="https://cdn.amcharts.com/lib/5/geodata/worldTimeZonesLow.js"></script>
				<script src="https://cdn.amcharts.com/lib/5/geodata/worldTimeZoneAreasLow.js"></script>
				<script src="../assets/plugins/custom/datatables/datatables.bundle.js"></script>
				<!--end::Vendors Javascript-->
				<!--begin::Custom Javascript(used by this page)-->
				<script src="../assets/js/widgets.bundle.js"></script>
				<script src="../assets/js/custom/widgets.js"></script>
				<script src="../assets/js/custom/apps/chat/chat.js"></script>
				<script src="../assets/js/custom/utilities/modals/upgrade-plan.js"></script>
				<script src="../assets/js/custom/utilities/modals/create-app.js"></script>
				<script src="../assets/js/custom/utilities/modals/users-search.js"></script>
			</body>

			</html>