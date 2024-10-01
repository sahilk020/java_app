<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib prefix="s" uri="/struts-tags" %>
<html dir="ltr" lang="en-US">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Merchant Accounts</title>


<link rel="shortcut icon" href="assets/media/images/paylogo.svg" />
<!--begin::Fonts-->
<link rel="stylesheet"
	href="https://fonts.googleapis.com/css?family=Inter:300,400,500,600,700" />
<!--end::Fonts-->
<!--begin::Vendor Stylesheets(used by this page)-->
<link
	href="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.css"
	rel="stylesheet" type="text/css" />
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
<link href="../css/select2.min.css" rel="stylesheet" />
<script src="../js/jquery.select2.js" type="text/javascript"></script>

 <script src="../assets/plugins/custom/datatables/datatables.bundle.js"></script> 

</head>

<script type="text/javascript">
$(document).ready(function(){
 
  // Initialize select2
  $("#merchant").select2();
});
</script>

<style>
.dt-buttons.btn-group.flex-wrap {
    display: none;
}
.select2-container{
	margin-left: -10px !important;
}
.select2-dropdown{
	margin-left: 10px !important;
}
.merchant-div{
	width: 120%;
	margin-left:-10px;
}
table.dataTable.display tbody tr.odd {
    background-color: #e6e6ff !important;
}
table.dataTable.display tbody tr.odd > .sorting_1{
	 background-color: #e6e6ff !important;
}
.dataTables_wrapper .dataTables_filter input{
	width: 75% !important;
}
</style>

<body id="kt_body"
	class="header-fixed header-tablet-and-mobile-fixed toolbar-enabled toolbar-fixed toolbar-tablet-and-mobile-fixed aside-enabled aside-fixed"
	style="-kt-toolbar-height: 55px; - -kt-toolbar-height-tablet-and-mobile: 55px" >
<div class="content d-flex flex-column flex-column-fluid"
		id="kt_content">

<div class="toolbar" id="kt_toolbar">
							<!--begin::Container-->
							<div id="kt_toolbar_container" class="container-fluid d-flex flex-stack">
								<!--begin::Page title-->
								<div data-kt-swapper="true" data-kt-swapper-mode="prepend" data-kt-swapper-parent="{default: '#kt_content_container', 'lg': '#kt_toolbar_container'}" class="page-title d-flex align-items-center me-3 flex-wrap mb-5 mb-lg-0 lh-1">
									<!--begin::Title-->
									<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">Merchant sub user</h1>
									<!--end::Title-->
									<!--begin::Separator-->
									<span class="h-20px border-gray-200 border-start mx-4"></span>
									<!--end::Separator-->
									<!--begin::Breadcrumb-->
									<ul class="breadcrumb breadcrumb-separatorless fw-semibold fs-7 my-1">
										<!--begin::Item-->
										<li class="breadcrumb-item text-muted">
											<a href="home" class="text-muted text-hover-primary">Dashboard</a>
										</li>
										<!--end::Item-->
										<!--begin::Item-->
										<li class="breadcrumb-item">
											<span class="bullet bg-gray-200 w-5px h-2px"></span>
										</li>
										<!--end::Item-->
                                        <!--begin::Item-->
										<li class="breadcrumb-item text-muted">Manage Users</li>
										<!--end::Item-->
										<!--begin::Item-->
										<li class="breadcrumb-item">
											<span class="bullet bg-gray-200 w-5px h-2px"></span>
										</li>
										<!--end::Item-->
										<!--begin::Item-->
										<li class="breadcrumb-item text-dark">Merchant sub user</li>
										<!--end::Item-->
									</ul>
									<!--end::Breadcrumb-->
								</div>
								<!--end::Page title-->
								
							</div>
							<!--end::Container-->
						</div>
	<!-- <h2 class="pageHeading">Merchant sub user</h2> -->
	<div id="kt_content_container" class="container-xxl">
				
				<div class="row my-5">
					<div class="col">
						<div class="card">
							<div class="card-body">
								<div class="row my-3 align-items-center">
	<label style="margin-top: 22px;">Merchant List:</label> <br />
	<div class="col-sm-6 col-lg-3">
						
						<s:if test="%{#session.USER.UserType.name()=='ADMIN' || #session.USER.UserType.name()=='SUBADMIN'}">
						 <s:select name="merchants" class="form-select form-select-solid"
									id="merchant" headerKey="ALL" headerValue="ALL"
									listKey="emailId" listValue="businessName"
									list="merchantList" autocomplete="off" onchange="handleChange();" />
						</s:if>
						<s:else>
						</s:else>
					</div>
					</div></div></div></div></div></div>
	<div id="kt_content_container" class="container-xxl">
				
				<div class="row my-5">
					<div class="col">
						<div class="card">
							<div class="card-body">
							<div class="row g-9 mb-8 justify-content-end">
												<div class="col-lg-3 col-sm-12 col-md-6">
													<select name="currency" data-control="select2"
														data-placeholder="Actions" id="actions11"
														class="form-select form-select-solid actions dropbtn1"
														data-hide-search="true" onchange="myFunctions();">
														<option value="">Actions</option>
														<option value="copy">Copy</option>
														<option value="csv">CSV</option>
														<option value="pdf">PDF</option>
														<option value="print">PRINT</option>
													</select>
												</div>
												<div class="col-lg-4 col-sm-12 col-md-6">
													<div class="dropdown1">
														<button
															class="form-select form-select-solid actions dropbtn1">Customize
															Columns</button>
														<div class="dropdown-content1">
															<a class="toggle-vis" data-column="0">Pay Id</a> <a
																class="toggle-vis" data-column="1">Email</a> <a
																class="toggle-vis" data-column="2">Business Name</a> <a
																class="toggle-vis" data-column="3">Status</a> <a
																class="toggle-vis" data-column="4">Registration Date</a> 
																<!-- <a	class="toggle-vis" data-column="5">Action</a> -->

														</div>
													</div>
												</div>
											</div>	
								<div class="row my-3 align-items-center">				
	<table width="100%" align="left" cellpadding="0" cellspacing="0" class="formbox">		
		<tr>	
			
		<td align="left" style="padding:10px;">
        <div class="scrollD">
			<div class="row g-9 mb-8">
				<div class="table-responsive dataTables_wrapper dt-bootstrap4 no-footer">
	<table id="datatable" class="display table table-striped table-row-bordered gy-5 gs-7" cellspacing="0" width="100%">
		<thead>
			<tr class="boxheadingsmall fw-bold fs-6 text-gray-800">
				<th>Pay Id</th>
				<th>Email</th>
				<th>Business Name </th>
				<th>Status</th>
				<th>UserType</th>
				<th>Mobile</th>
				<th>Registration Date</th>
				<th>Pay Id</th>
			</tr>
		</thead>
	</table>
	</div>
	</div>
    </div>
    </td>
    </tr>
    </table>
    </div> </div> </div> </div> </div> </div>
	<script type="text/javascript">
	$(document).ready(function() {
		$(function() {
		renderTable();
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
		                body: function ( data, column, row, node ) {
		                    // Strip $ from salary column to make it numeric
		                    if(column == 6){
		                    	
		                    }
		                    return column === 0 ? "'"+data : column === 1 ? data.replace("&#x40;", "@") : data;
		                }
		            }
		        }
		    };
			$('#datatable').dataTable({
				dom : 'BTftlpi',
				
				buttons : [
				$.extend( true, {}, buttonCommon, {
					extend : 'copyHtml5',
					exportOptions : {
						columns: [':visible']
					}
				} ), 
				$.extend( true, {}, buttonCommon, {
					extend : 'csvHtml5',
					exportOptions : {
						columns: [':visible']
					}
				} ), 
				{
					extend : 'pdfHtml5',
					title : 'Merchant Subuser List',
					orientation: 'landscape',
					exportOptions : {
						columns: [':visible']
					},
					customize: function (doc) {
					    doc.defaultStyle.alignment = 'center';
     					doc.styles.tableHeader.alignment = 'center';
					  }
				}, {
					extend : 'print',
					title : 'Merchant Subuser List',
					exportOptions : {
						columns: [':visible']
					}
				},{
					extend : 'colvis',
					//collectionLayout: 'fixed two-column',
					columns : [0, 1, 2, 3, 4, 5, 6]
				}],				
				"ajax" : {
					"url" : "merchantSubUserList",
					"type" : "POST",
					"data" : generatePostData
				},
				"bProcessing" : true,
				"bLengthChange" : true,
				"bAutoWidth" : false,
				"iDisplayLength" : 10,
				"order": [[ 5, "desc" ]],
				"aoColumns" : [ {
					"mData" : "payId"
				}, {
					"mData" : "emailId"
				}, 
				{
					"mData" : "businessName"
				},{
					"mData" : "status"
				},{
					"mData" : "userType"
				},	{
					"mData" : "mobile"
				},{
					"mData" : "registrationDate"
				},{
					"data" : null,
					"visible" : false,
					"className" : "displayNone",
					"mRender" : function(row) {
			              return "\u0027" + row.payId;
			       }
				} ]
			});
		}
		function reloadTable() {
			var tableObj = $('#datatable');
			var table = tableObj.DataTable();
			table.ajax.reload();
		}
		function generatePostData() {
			var token = document.getElementsByName("token")[0].value;
			var emailId = null;
			if(null != document.getElementById("merchant")){
				emailId = document.getElementById("merchant").value;
			}else{
				emailId = 'ALL';
			}
			var obj = {				
					token : token,
					emailId : emailId,
			};

			return obj;
		}
	</script>
	</div>
	<script type="text/javascript">
			$('a.toggle-vis').on('click', function(e) {
				debugger
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
		<script type="text/javascript">
		function myFunctions() {
			debugger
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
	</script>
	<%-- <script src="../assets/plugins/custom/datatables/datatables.bundle.js"></script> --%>
 </body>
</html>