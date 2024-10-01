<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="/struts-tags" prefix="s"%>
<html dir="ltr" lang="en-US">
<head>
<title>Reseller Merchants List</title>
<script src="../assets/plugins/global/plugins.bundle.js"></script>
<script src="../assets/js/scripts.bundle.js"></script>



<script type="text/javascript">
	$(document).ready(function() {

		// Initialize select2
		//$("#merchant").select2();
	});
</script>

<script type="text/javascript">
	$(document)
			.ready(
					function() {

						renderTable();
						$("#submit")
								.click(
										function(env) {

											var merchantVal = "All";
											var resellerId = document
													.getElementById("resl").value;
											if (resellerId == null
													|| resellerId == ""
													|| resellerId == "Select Reseller") {
												alert("Please Select Reseller");
												return false;
											}
											$('#loader-wrapper').show();
											reloadTable();
										});

						$(function() {

							var table = $('#smartRouterReportDataTable')
									.DataTable();
							$('#smartRouterReportDataTable')
									.on(
											'click',
											'td.my_class1',
											function() {
												var rowIndex = table.cell(this)
														.index().row;
												var rowData = table.row(
														rowIndex).data();

											});
						});
					});

	function renderTable() {
		var merchantaPayId = "All";

		var table = new $.fn.dataTable.Api('#smartRouterReportDataTable');
		var token = document.getElementsByName("token")[0].value;
		//$('#loader-wrapper').hide();

		var buttonCommon = {
			exportOptions : {
				format : {
					body : function(data, column, row, node) {
						// Strip $ from salary column to make it numeric
						return column === 0 ? "'" + data : (column === 1 ? "'"
								+ data : data);
					}
				}
			}
		};

		$('#smartRouterReportDataTable')
				.dataTable(
						{
							"footerCallback" : function(row, data, start, end,
									display) {
								var api = this.api(), data;

								// Remove the formatting to get integer data for summation
								var intVal = function(i) {
									return typeof i === 'string' ? i.replace(
											/[\,]/g, '') * 1
											: typeof i === 'number' ? i : 0;
								};

							},
							"columnDefs" : [ {
								className : "dt-body-right",
								"targets" : [ 0, 1, 2, 3, 4 ]
							} ],

							dom : 'Bfrtip',
							buttons : [
									$.extend(true, {}, buttonCommon, {
										extend : 'copyHtml5',
										exportOptions : {
											columns : [ ':visible' ]
										},
									}),
									$.extend(true, {}, buttonCommon, {
										extend : 'csvHtml5',
										title : 'ResellerMerchants',
										exportOptions : {

											columns : [ ':visible' ]
										},
									}),
									{
										extend : 'pdfHtml5',
										orientation : 'landscape',
										pageSize : 'legal',
										//footer : true,
										title : 'ResellerMerchants',
										exportOptions : {
											columns : [ ':visible' ]
										},
										customize : function(doc) {
											doc.defaultStyle.alignment = 'center';
											doc.styles.tableHeader.alignment = 'center';
										}
									}, {
										extend : 'print',
										//footer : true,
										title : 'ResellerMerchants',
										exportOptions : {
											columns : [ ':visible' ]
										}
									}, {
										extend : 'colvis',
										columns : [ 0, 1, 2, 3, 4 ]
									} ],

							"ajax" : {

								"url" : "resellerMerchantData",
								"type" : "POST",
								"data" : function(d) {
									return generatePostData(d);
								}
							},
							"fnDrawCallback" : function() {
								$("#submit").removeAttr("disabled");
								$('#loader-wrapper').hide();
							},
							"searching" : false,
							"ordering" : false,
							"processing" : true,
							"serverSide" : false,
							"paginationType" : "full_numbers",
							"lengthMenu" : [ [ 10, 25, 50 ], [ 10, 25, 50 ] ],
							"order" : [ [ 2, "desc" ] ],

							"columnDefs" : [ {
								"type" : "html-num-fmt",
								"targets" : 4,
								"orderable" : true,
								"targets" : [ 0, 1, 2, 3, 4 ]
							} ],

							"columns" : [ {
								"data" : "registrationDate",
								"className" : "text-class"
							}, {
								"data" : "resellerName",
								"className" : "text-class"
							}, {
								"data" : "businessName",
								"className" : "text-class"
							}, {
								"data" : "payId",
								"className" : "text-class"
							}, {
								"data" : "resellerId",
								"className" : "text-class"
							}, {
								"data" : "status",
								"className" : "text-class"
							} ]
						});
	}

	function reloadTable() {
		$("#submit").attr("disabled", true);
		var tableObj = $('#smartRouterReportDataTable');
		var table = tableObj.DataTable();
		table.ajax.reload();
	}

	function generatePostData(d) {
		var token = document.getElementsByName("token")[0].value;
		var resellerId = document.getElementById("resl").value;
		var payId = "All";

		var obj = {

			resellerId : resellerId,
			payId : payId,
			draw : d.draw,
			length : d.length,
			start : d.start,
			token : token,
			"struts.token.name" : "token",
		};
		return obj;
	}
</script>

<style type="text/css">
#loader-wrapper .loader-section.section-left, #loader-wrapper .loader-section.section-right
	{
	background: rgba(225, 225, 225, 0.6) !important;
	width: 50% !important;
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

table.dataTable tbody th, table.dataTable tbody td {
	padding: 4px 5px !important;
}

table.display thead th {
	padding: 6px 3px 6px 6px !important;
}

.text-class {
	text-align: center !important;
}

#submit {
	margin-top: 25px;
}
</style>
</head>
<body id="mainBody">
	<!-- <div id="loader-wrapper"
		style="width: 100%; height: 100%; display: none;">
		<div id="loader"></div>
	</div> -->



	<div style="overflow: scroll !important;">
		<table id="mainTable" width="100%" border="0" align="center"
			cellpadding="0" cellspacing="0" class="txnf">
			<tr>
				<!-- <td colspan="5" align="left"><h2>Reseller Merchant List</h2></td> -->
			</tr>
			<tr>
				<td colspan="5" align="left" valign="top">
					<!-- Added By Sweety --> <!--begin::Root-->
					<div class="d-flex flex-column flex-root">
						<!--begin::Page-->
						<div class="page d-flex flex-row flex-column-fluid">
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
											<h1
												class="d-flex align-items-center text-dark fw-bold my-1 fs-3">
												Reseller Merchants</h1>
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
												<li class="breadcrumb-item text-dark">Reseller
													Merchants</li>
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
										<div class="row my-5">
											<div class="col">
												<div class="card">
													<div class="card-body">

														<!--begin::Input group-->
														<div class="row g-9 mb-8">

															<div class="col-md-4 fv-row">
																<label
																	class="d-flex align-items-center fs-6 fw-bold mb-2">

																	Reseller 
																</label>

																<s:select name="reslList"
																	class="form-select form-select-solid" id="resl"
																	headerKey="ALL" headerValue="ALL" list="listReseller" data-control="select2"
																	listKey="payId" listValue="businessName"
																	autocomplete="off" />
															</div>

															<div class="col-md-4 fv-row">
																<input type="button" id="submit" value="Submit"
																	class="btn btn-primary btn-xs" mt-4submit_btn">
															</div>
														</div>
													</div>
												</div>
											</div>
										</div>
								
							</div>
						</div>
					</div>
				</td>
			</tr>
			<tr>
			<!-- 	<td colspan="5" align="left"><h2>&nbsp;</h2></td> -->
			</tr>
			<tr>
				<td align="left" style="padding: 10px;">
					<!-- <div id="kt_content_container" class="container-xxl"> -->
						<div style="overflow: scroll !important;">
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
														<option value="print">PRINT</option>
													</select>
												</div>
												<div class="col-lg-4 col-sm-12 col-md-6">
														<div class="dropdown1">
                                                    <button class="form-select form-select-solid actions dropbtn1">Customize Columns</button>
                                                    <div class="dropdown-content1">
														<a class="toggle-vis" data-column="0">Registration Date</a>
													<a class="toggle-vis" data-column="1">Reseller Name</a>
														<a class="toggle-vis" data-column="2">Merchant Name</a>
														<a class="toggle-vis" data-column="3">Merchant PayId</a>
													<a class="toggle-vis" data-column="4">Reseller PayId</a>
												</div>
												</div>
												</div>
											</div>

											
											<div class="row g-9 mb-8">
                                             <div class="table-responsive dataTables_wrapper dt-bootstrap4 no-footer">
													<table id="smartRouterReportDataTable"
														class="display table table-striped table-row-bordered gy-5 gs-7"
														cellspacing="0" width="100%">
														<thead>
															<tr class="boxheadingsmall fw-bold fs-6 text-gray-800">
																<th style="text-align: center;" data-orderable="false">Registration
																	Date</th>
																<th style="text-align: center;" data-orderable="false">Reseller
																	Name</th>
																<th style="text-align: center;" data-orderable="false">Merchant
																	Name</th>
																<th style="text-align: center;" data-orderable="false">Merchant
																	Pay ID</th>
																<th style="text-align: center;" data-orderable="false">Reseller
																	Id</th>
																<th style="text-align: center;" data-orderable="false">Status</th>
															</tr>
														</thead>
														<tfoot>
															<tr class="boxheadingsmall">
																<th></th>
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

			if (x == 'print') {
				document.querySelector('.buttons-print').click();
			}

		}
	</script>
		<script type="text/javascript">
$('a.toggle-vis').on('click', function (e) {
    e.preventDefault();
    table = $('#smartRouterReportDataTable').DataTable();
    // Get the column API object
    var column1 = table.column($(this).attr('data-column'));
    // Toggle the visibility
    column1.visible(!column1.visible());
    if($(this)[0].classList[1]=='activecustom'){
        $(this).removeClass('activecustom');
    }
    else{
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