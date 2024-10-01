<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib prefix="s" uri="/struts-tags"%>
<html dir="ltr" lang="en-US">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>User group List</title>
<script type="text/javascript" src="../js/jquery.min.js"></script>
<script src="../assets/plugins/global/plugins.bundle.js"></script>
<script src="../assets/js/scripts.bundle.js"></script>
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
    // document.querySelector('.buttons-excel').click();
    // document.querySelector('.buttons-print').click();
}
</script>

</head>
<body>

	<div style="overflow: scroll !important;">
	<!-- Added By Sweety -->

		<!--begin::Root-->
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
									User Group List</h1>
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
									<li class="breadcrumb-item text-muted">Manage User</li>
									<!--end::Item-->
									<!--begin::Item-->
									<li class="breadcrumb-item"><span
										class="bullet bg-gray-200 w-5px h-2px"></span></li>
									<!--end::Item-->
									<!--begin::Item-->
									<li class="breadcrumb-item text-dark">User Group List
										</li>
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
                        <div class="row g-9 mb-8 justify-content-end">
                          <div class="col-lg-4 col-sm-12 col-md-4">
                            <select name="actions" id="actions11" data-control="select2" data-placeholder="Actions"
                              class="form-select form-select-solid actions" data-hide-search="true" onchange="myFunction();">
                              <option value="">Actions</option>
                              <option value="copy">Copy</option>
                              <option value="csv">CSV</option>
                              <option value="pdf">PDF</option>
                            </select>
                          </div>
	   <div class="col-lg-4 col-sm-12 col-md-4">
                        <div class="dropdown1">
                           <button class="form-select form-select-solid actions dropbtn1">Customize Columns</button>
                            <div class="dropdown-content1">

                             <a class="toggle-vis" data-column="0">Sr No</a>
                               <a class="toggle-vis" data-column="1">Group Name</a>
                              <a class="toggle-vis" data-column="2">Description</a>
                           </div>
                           </div>
                           </div>
                           </div>
                           
                         
		 <!-- <table width="100%" align="left" cellpadding="0" cellspacing="0">
			<tr>
				<td align="left" style="padding: 15px;"> -->
					 <div class="row g-9 mb-8">
						<table id="userGroupDatatable" class="display table table-striped table-row-bordered gy-5 gs-7" cellspacing="0" width="100%">
							<thead>
								<tr class="fw-bold fs-6 text-gray-800 ">
									<th>Sr no</th>
									<th>Group Name</th>
									<th>Description</th>
								</tr>
							</thead>
						</table>
					</div>
				<!-- </td>
			</tr>
		</table> -->
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
		                    return data;
		                }
		            }
		        }
		    };
			$('#userGroupDatatable').dataTable({
				
				dom : 'BTftlpi',
				
				'columnDefs':[{ 
                    'searchable':false, 
                    'targets' :[2] 
                }],

				buttons : [
				$.extend( true, {}, buttonCommon, {
					extend : 'copy',
					exportOptions : {
					//	columns: [0,1,2]
				columns: [':visible']
					}
				} ),
				$.extend( true, {}, buttonCommon, {
					extend : 'csv',
					exportOptions : {
						//columns: [0,1,2]
						columns: [':visible']
					}
				} ),
				{
					extend : 'pdf',
					title : 'UserGroup List',
					orientation: 'landscape',
					exportOptions : {
					//	columns: [0,1,2]
						columns: [':visible']
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
					columns : [0, 1, 2]
				}],			
				"ajax" : {
					"url" : "userGroupDetailsAction",
					"type" : "POST",
					"data" : generatePostData
				},
				"bProcessing" : true,
				"bLengthChange" : true,
				"bAutoWidth" : false,
				"iDisplayLength" : 10,
				"order": [[ 0, "asc" ]],
				"aoColumns" : [ {
					"mData" : null,
					render: (data, type, row, meta) => meta.row + 1
				},{
					"mData" : "group"
				}, {
					"mData" : "description"
				}]
			});
			
		}
		function reloadTable() {
			var tableObj = $('#userGroupDatatable');
			var table = tableObj.DataTable();
			table.ajax.reload();
		}
		function generatePostData() {
			var token = document.getElementsByName("token")[0].value;
			var obj = {				
					token : token,
			};

			return obj;
		}
	
	</script>
	 <script src="../assets/plugins/custom/datatables/datatables.bundle.js"></script>
		<script type="text/javascript">
				$('a.toggle-vis').on('click', function (e) {
					debugger
					e.preventDefault();
					table = $('#userGroupDatatable').DataTable();
					// Get the column API object
					var column1 = table.column($(this).attr('data-column'));
					// Toggle the visibility
					column1.visible(!column1.visible());
					if ($(this)[0].classList[1] == 'activecustom') {
						$(this).removeClass('activecustom');
					}
					else {
						$(this).addClass('activecustom');
					}
				});

			</script>
	<style>
	.dt-buttons{
	display:none;
	}
	</style>

</body>
</html>