<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib prefix="s" uri="/struts-tags"%>
<html dir="ltr" lang="en-US">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Reseller Accounts</title>
<link rel="shortcut icon" href="assets/media/images/paylogo.svg" />
<!--begin::Fonts-->
<link rel="stylesheet"
	href="https://fonts.googleapis.com/css?family=Inter:300,400,500,600,700" />
<!--end::Fonts-->
<!--begin::Vendor Stylesheets(used by this page)-->
<!-- <link href="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.css" rel="stylesheet"
						type="text/css" /> -->
<link href="../assets/plugins/custom/datatables/datatables.bundle.css"
	rel="stylesheet" type="text/css" />
<!--end::Vendor Stylesheets-->
<!--begin::Global Stylesheets Bundle(used by all pages)-->
<link href="../assets/plugins/global/plugins.bundle.css"
	rel="stylesheet" type="text/css" />
<link href="../assets/css/style.bundle.css" rel="stylesheet"
	type="text/css" />
<link href="../assets/css/custom/custom-style.css" rel="stylesheet"
	type="text/css" />
<script src="../assets/plugins/global/plugins.bundle.js"></script>
<script src="../assets/js/scripts.bundle.js"></script>
</head>
<body>
	<div style="overflow: scroll !important;">
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
									Reseller List</h1>
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
									<li class="breadcrumb-item text-dark">Reseller List</li>
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
												<div class="row my-3 align-items-center d-flex justify-content-start">
							<table width="100%" align="left" cellpadding="0" cellspacing="0"
								class="formbox">
								<tr>
									<td align="left" style="padding: 10px;"><br />
									<br />
									<div class="row g-9 mb-8 justify-content-end">
											<div class="col-lg-2 col-sm-12 col-md-6">
												<select name="currency"
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
                                                    <button class="form-select form-select-solid actions dropbtn1">Customize Columns</button>
                                                    <div class="dropdown-content1">
												 <a class="toggle-vis" data-column="0">Pay Id</a>
												 <a class="toggle-vis" data-column="1">Business Name</a>
												 <a class="toggle-vis" data-column="2">Email</a>
												 <a class="toggle-vis" data-column="3">Status</a>
												 <a class="toggle-vis" data-column="4">User Type</a>
												 <a class="toggle-vis" data-column="5">Contact</a>
												 <a class="toggle-vis" data-column="6">Registration Date</a>
												</div>
												</div>											 
											
											  </div>
										</div>
										<div class="scrollD">
											<table id="datatable"
												class="display table table-striped table-row-bordered gy-5 gs-7"
												cellspacing="0" width="100%">
												<thead>
													<tr class="boxheadingsmall">
														<th>Pay Id</th>
														<th>Business Name</th>
														<th>Email</th>
														<th>Status</th>
														<th>User Type</th>
														<th>Contact</th>
														<th>Registration Date</th>
														<th>Edit</th>
														<th>Pay Id</th>
													</tr>
												</thead>
											</table>
										</div></td>
								</tr>
							</table>
							</div>
							</div>
							</div></div></div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<s:form name="merchant" action="resellerSetup">
			<s:hidden name="payId" id="hidden" value="" />
			<s:hidden name="token" value="%{#session.customToken}"></s:hidden>
		</s:form>
	</div>
	<script type="text/javascript">
	$(document).ready(function() {
		$(function() {
		renderTable();
		enableBaseOnAccess();
		});
	});
	function handleChange() {
		reloadTable();
	}
	function decodeVal(text) {
		return $('<div/>').html(text).text();
	}
	function renderTable() {
			var token  = document.getElementsByName("token")[0].value;
			var buttonCommon = {
                exportOptions: {
                    format: {
                        body: function (data, column, row, node) {
                            // Convert all columns to strings and remove string quotes
                            if (typeof data === 'string') {
                                // Replace '&#x40;' with '@' for emailId column
                                if (row === 2) {
                                    return data.replace(/&#x40;|'/g, function(match) {
                                        return match === '&#x40;' ? '@' : ''; // Replace '&#x40;' with '@' and remove single quotes
                                    });
                                } else {
                                    return data.replace(/'/g, ''); // Remove single quotes from all other string columns
                                }
                            } else if (data instanceof Date) {
                                // Remove seconds from the date
                                return data.toISOString().replace(/:\d{2}\.\d{3}Z$/, 'Z');
                            }
                            // Default formatting for other formats or non-string columns
                            return data;
                        }
                    }
                }
            };
			$('#datatable').dataTable({
				dom : 'BTftlpi',
				
				'columnDefs':[{
                    'searchable':false,
                    'targets' :[7]
                }],
				buttons : [
				$.extend( true, {}, buttonCommon, {
					extend : 'copyHtml5',
					exportOptions : {
						columns: [':visible :not(:last-child)']
					}
				} ),
				$.extend( true, {}, buttonCommon, {
					extend : 'csvHtml5',
					exportOptions : {
						columns: [':visible :not(:last-child)']
					}
				} ),
				{
					extend : 'pdfHtml5',
					title : 'Reseller List',
					orientation: 'landscape',
					pageSize: 'LEGAL',
					exportOptions : {
						columns: [':visible :not(:last-child)']
					},
					customize: function (doc) {
					    doc.defaultStyle.alignment = 'center';
     					doc.styles.tableHeader.alignment = 'center';
					  }
				},
				// Disabled print button.
				/* {extend : 'print',title : 'Merchant List',exportOptions : {columns: [':visible :not(:last-child)']}}, */
				{
					extend : 'colvis',
					//collectionLayout: 'fixed two-column',
					columns : [0, 1, 2, 3, 4, 5, 6]
				}],			
				"ajax" : {
					"url" : "resellerDetailsAction",
					"type" : "POST",
					"data" : generatePostData
				},
				"bProcessing" : true,
				"bLengthChange" : true,
				"bAutoWidth" : false,
				"iDisplayLength" : 10,
				"order": [[ 1, "asc" ]],
				"aoColumns" : [ {
					"mData" : "payId"
				}, {
					"mData" : "businessName"
				}, {
					"mData" : "emailId"
				}, {
					"mData" : "status"
				},{
					"mData" : "userType"
				},	{
					"mData" : "mobile"
				},{
					"mData" : "registrationDate"
				}, {
					"mData" : null,
					"sClass" : "center",
					"bSortable" : false,
					"mRender" : function() {
						return '<button class="btn btn-primary btn-xs" disabled="disabled" name="resellerEdit" id="resellerEdit" onclick="ajaxindicatorstart1()">Edit</button>';
					}
				},{
					"data" : null,
					"visible" : false,
					"className" : "displayNone",
					"mRender" : function(row) {
			              return "\u0027" + row.payId;
			       }
				} ]
			});
			$(document).ready(function() {
				var table = $('#datatable').DataTable();
					$('#datatable tbody').on('click','td',function(){
						var rows = table.rows();
						var columnVisible = table.cell(this).index().columnVisible;
						var rowIndex = table.cell(this).index().row;
						if (columnVisible == 7) {
							var payId = table.cell(rowIndex, 0).data();
							if(payId.length>20){
								return false;
							}
							for(var i =0;i<payId.length;i++){
								var code  = payId.charCodeAt(i);
								if (code < 48 || code > 57){
									return false;
								}
						    }
						document.getElementById('hidden').value = payId;
						if ($("#resellerEdit").attr("disabled") == undefined){
						    document.merchant.submit();
						}
						}
				});
			});
		}
		function reloadTable() {
			var tableObj = $('#datatable');
			var table = tableObj.DataTable();
			table.ajax.reload();
		}
		function generatePostData() {
			var token = document.getElementsByName("token")[0].value;
			var businessType = "ALL";
			
			var obj = {				
					token : token,
					businessType : businessType,
			};
			return obj;
		}
		$('#datatable').on( 'draw.dt', function () {
			enableBaseOnAccess();
		} );
		function enableBaseOnAccess() {
			setTimeout(function(){
				if ($('#resellerList').hasClass("active")) {
					var menuAccess = document.getElementById("menuAccessByROLE").value;
					var accessMap = JSON.parse(menuAccess);
					var access = accessMap["resellerList"];
					if (access.includes("Update")) {
						var edits = document.getElementsByName("resellerEdit");
						for (var i = 0; i < edits.length; i++) {
							var edit = edits[i];
							edit.disabled=false;
						}
					}
				}
			},500);
		}
	</script>
	<script src="../assets/plugins/custom/datatables/datatables.bundle.js"></script>
	<script>
function myFunction() {
	var x = document.getElementById("actions11").value;
	if(x=='csv'){
		document.querySelector('.buttons-csv').click();
	}
	if(x=='copy'){
		document.querySelector('.buttons-copy').click();
	}
	if(x=='pdf'){
		document.querySelector('.buttons-pdf').click();
	}
}
</script>
<style>
	.dt-buttons{
display: none;
	}
</style>
	<script type="text/javascript">
$('a.toggle-vis').on('click', function (e) {
    e.preventDefault();
    table = $('#datatable').DataTable();
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
</body>
</html>