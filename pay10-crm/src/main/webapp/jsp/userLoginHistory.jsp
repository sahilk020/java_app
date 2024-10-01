<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Login History</title>
<base href="../"/>
		<title>BPGATE</title>
		<meta charset="utf-8" />
		<meta name="viewport" content="width=device-width, initial-scale=1" />
		<link rel="shortcut icon" href="../assets/media/images/paylogo.svg" />
		<!--begin::Fonts-->
		<link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Inter:300,400,500,600,700" />
		<!--end::Fonts-->
		<!--begin::Vendor Stylesheets(used by this page)-->
		<link href="../assets/plugins/custom/datatables/datatables.bundle.css" rel="stylesheet" type="text/css" />
		<!--end::Vendor Stylesheets-->
		<!--begin::Global Stylesheets Bundle(used by all pages)-->
		<link href="../assets/plugins/global/plugins.bundle.css" rel="stylesheet" type="text/css" />
		<link href="../assets/css/style.bundle.css" rel="stylesheet" type="text/css" />
		<link href="../assets/css/custom/custom-style.css" rel="stylesheet" type="text/css" />
		<script src="../js/jquery.js"></script>
		<script src="../js/jquery.min.js"></script>

<style>
/* .displayNone {
	display: none;
}
.dataTables_wrapper .dataTables_filter input{
	display: none !important;
}
.dataTables_wrapper .dataTables_filter label{
	display: none !important;
}
table.dataTable.display tbody tr.odd {
    background-color: #e6e6ff !important;
}
table.dataTable.display tbody tr.odd > .sorting_1{
	 background-color: #e6e6ff !important;
} */

.dt-buttons.btn-group.flex-wrap {
    display: none !important;
}
#kt_datatable_vertical_scroll_filter {
    display: none !important;
}
</style>


</head>
<body id="kt_body" class="header-fixed header-tablet-and-mobile-fixed toolbar-enabled toolbar-fixed toolbar-tablet-and-mobile-fixed aside-enabled aside-fixed" style="--kt-toolbar-height:55px;--kt-toolbar-height-tablet-and-mobile:55px">
	 <s:token />
	<div class="content d-flex flex-column flex-column-fluid" id="kt_content">
						<!--begin::Toolbar-->
						<div class="toolbar" id="kt_toolbar">
							<!--begin::Container-->
							<div id="kt_toolbar_container" class="container-fluid d-flex flex-stack">
								<!--begin::Page title-->
								<div data-kt-swapper="true" data-kt-swapper-mode="prepend" data-kt-swapper-parent="{default: '#kt_content_container', 'lg': '#kt_toolbar_container'}" class="page-title d-flex align-items-center me-3 flex-wrap mb-5 mb-lg-0 lh-1">
									<!--begin::Title-->
									<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">User Login History</h1>
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
										<li class="breadcrumb-item text-dark">User Login History</li>
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
                 <!-- added admin and subadmin condition just for testing ,to be removed -->                         

				<s:if test="%{#session.USER.UserType.name()=='ADMIN' || #session.USER.UserType.name()=='MERCHANT'  || #session.USER_TYPE.name()=='SUPERADMIN'}">
				<div class="position-relative col-lg-4 col-sm-12 col-md-6">
				<s:select name="merchants" class="form-select form-select-solid actions" id="merchant"
							headerKey="ALL USERS" headerValue="ALL USERS" listKey="emailId"
							listValue="emailId" list="merchantList" autocomplete="off" onchange="reloadTable();"
							 />
				</div>
				</s:if>
				<s:elseif test="%{#session.USER.UserType.name()=='SUBUSER'}">

				<div class="position-relative col-lg-4 col-sm-12 col-md-6">
				<s:select name="merchants" class="form-select form-select-solid actions" id="merchant" listKey="emailId"
							listValue="emailId" list="merchantList" autocomplete="off" onchange="reloadTable();"
							/>
				</div>
				</s:elseif>


                                            <div class="col-lg-4 col-sm-12 col-md-6">
                                              <select name="currency" data-control="select2" data-placeholder="Actions"
                                                class="form-select form-select-solid actions" data-hide-search="true">
                                                <option value="">Actions</option>
                                                <option value="copy">Copy</option>
                                                <option value="csv">CSV</option>
                                                <option value="pdf">PDF</option>
                                              </select>
                                            </div>
											<div class="col-lg-4 col-sm-12 col-md-6">
												<select name="currency" data-control="select2" data-placeholder="Customize Columns"
												  class="form-select form-select-solid actions" data-hide-search="true">
												  <option value="">Customize Columns</option>
												  <option value="businessname">Business Name</option>
												  <option value="emailid"> Email Id</option>
												  <option value="ipaddress">IP Address</option>
												  <option value="browser">Browser</option>
												  <option value="os">OS</option>
												  <option value="status">Status</option>
												  <option value="date">Date</option>
												</select>
											</div>
                                          </div>
                                          <div class="row g-9 mb-8">
                                            <table id="kt_datatable_vertical_scroll"
                                              class="table table-striped table-row-bordered gy-5 gs-7">
                                                <thead>
                                                    <tr class="fw-bold fs-6 text-gray-800">
                                                        <th class="min-w-90px">Business Name</th>
                                                        <th scope="col">Email ID</th>
                                                        <th scope="col">IP Address</th>
                                                        <th scope="col">Browser</th>
                                                        <th scope="col">OS</th>
                                                        <th scope="col">Status</th>
                                                        <th scope="col">Date</th>
                                                        <th scope="col">Failed Login Reason</th>
                                                    </tr>
                                                </thead>

                                            </table>
                                          </div>
                                        </div>
                                      </div>
                                    </div>
                                </div>
							</div>
							<!--end::Container-->
						</div>
						<!--end::Post-->
					</div>

		<!--begin::Global Javascript Bundle(used by all pages)-->
		<script src="../assets/plugins/global/plugins.bundle.js"></script>
		<script src="../assets/js/scripts.bundle.js"></script>
		<!--end::Global Javascript Bundle-->
		<!--begin::Vendors Javascript(used by this page)-->
		<script src="../assets/plugins/custom/datatables/datatables.bundle.js"></script>
		<!--end::Vendors Javascript-->
		<!--end::Javascript-->


<script>
"use strict";


function generatePostData(a) {
		var token = document.getElementsByName("token")[0].value;
		console.log("DATA");
		var merchantEmailId = document.getElementById("merchant").value;
		console.log("MERCHANT ID "+merchantEmailId);
		var obj = {
		    emailId : merchantEmailId,
			draw :   a.draw,
			length : a.length,
			start : a.start,
			token : token,
			"struts.token.name" : "token",
		}
		return obj;
	}

     // Class definition
     var KTDatatablesServerSide = function () {
         // Shared variables
         var table;
         var dt;
         var filterPayment;

         // Private functions
         var initDatatable = function () {
             dt = $("#kt_datatable_vertical_scroll").DataTable({
     			dom: 'Brtip',

       buttons: [
       'copy', 'csv', 'excel', 'pdf', 'print'
       ],
       			scrollY: true,
     			scrollX: true,
                 searchDelay: 500,
                 processing: true,
                 serverSide:false,
                 order: [[5, 'desc']],
                 stateSave: true,
     			
                 select: {
                     style: 'multi',
                     selector: 'td:first-child input[type="checkbox"]',
                     className: 'row-selected'
                 },
                  ajax: {
                     url: "loginHistorySubUserAction",
     				 /* type: 'remote', */
     				 method:'POST',
     				 data:function(d) {
 						return generatePostData(d);
 					}
     				
                  },
                 columns: [
                     { data: 'businessName' },
                     { data: 'emailId' },
                     { data: 'ip' },
                     { data: 'browser' },
                     { data: 'os' },
                     { data: 'status' },
                     { data: 'timeStamp' },
     				{ data: 'failureReason' }
                 ],
                
                 
             });

             table = dt.$;

             // Re-init functions on every table re-draw -- more info: https://datatables.net/reference/event/draw
             dt.on('draw', function () {
                 initToggleToolbar();
                 toggleToolbars();
                 //handleDeleteRows();
                 KTMenu.createInstances();
     			document.getElementById("kt_datatable_vertical_scroll_wrapper").style.marginTop = "14px";
     			//document.getElementsByClassName(".dt-buttons.btn-group.flex-wrap").style.display="none";
     			//document.getElementById("kt_datatable_vertical_scroll_filter").style.display="none";

             });
         }

         // Search Datatable --- official docs reference: https://datatables.net/reference/api/search()
        /*  var handleSearchDatatable = function () {
             const filterSearch = document.querySelector('[data-kt-docs-table-filter="search"]');
             filterSearch.addEventListener('keyup', function (e) {
                 dt.search(e.target.value).draw();
             });
         }
 */
         // Filter Datatable
        /*  var handleFilterDatatable = () => {
             // Select filter options
             filterPayment = document.querySelectorAll('[data-kt-docs-table-filter="payment_type"] [name="payment_type"]');
             const filterButton = document.querySelector('[data-kt-docs-table-filter="filter"]');

            
         } */

         // Delete customer
        /*  var handleDeleteRows = () => {
             // Select all delete buttons
             const deleteButtons = document.querySelectorAll('[data-kt-docs-table-filter="delete_row"]');

             deleteButtons.forEach(d => {
                 // Delete button on click
                 d.addEventListener('click', function (e) {
                     e.preventDefault();

                     // Select parent row
                     const parent = e.target.closest('tr');

                 })
             });
         } */

         // Reset Filter
        /*  var handleResetForm = () => {
             // Select reset button
             const resetButton = document.querySelector('[data-kt-docs-table-filter="reset"]');

             // Reset datatable
             resetButton.addEventListener('click', function () {
                 // Reset payment type
                 filterPayment[0].checked = true;

                 // Reset datatable --- official docs reference: https://datatables.net/reference/api/search()
                 dt.search('').draw();
             });
         } */

         // Init toggle toolbar
         var initToggleToolbar = function () {
             // Toggle selected action toolbar
             // Select all checkboxes
             const container = document.querySelector('#kt_datatable_vertical_scroll');
             const checkboxes = container.querySelectorAll('[type="checkbox"]');

             // Select elements
             const deleteSelected = document.querySelector('[data-kt-docs-table-select="delete_selected"]');

             // Toggle delete selected toolbar
             checkboxes.forEach(c => {
                 // Checkbox on click event
                 c.addEventListener('click', function () {
                     setTimeout(function () {
                         toggleToolbars();
                     }, 50);
                 });
             });

             
         }

         // Toggle toolbars
         var toggleToolbars = function () {
             // Define variables
             const container = document.querySelector('#kt_datatable_vertical_scroll');
             const toolbarBase = document.querySelector('[data-kt-docs-table-toolbar="base"]');
             const toolbarSelected = document.querySelector('[data-kt-docs-table-toolbar="selected"]');
             const selectedCount = document.querySelector('[data-kt-docs-table-select="selected_count"]');

             // Select refreshed checkbox DOM elements
             const allCheckboxes = container.querySelectorAll('tbody [type="checkbox"]');

             // Detect checkboxes state & count
             let checkedState = false;
             let count = 0;

             // Count checked boxes
             allCheckboxes.forEach(c => {
                 if (c.checked) {
                     checkedState = true;
                     count++;
                 }
             });
         }

         // Public methods
         return {
             init: function () {
                 initDatatable();
                 //handleSearchDatatable();
                 initToggleToolbar();
                 //handleFilterDatatable();
                 //handleDeleteRows();
                 //handleResetForm();
             },
             rel: function () {
            	 var tableObj = $('#kt_datatable_vertical_scroll');
        		 var table = tableObj.DataTable();
        		 table.destroy();   
            	 initDatatable();
                 //handleSearchDatatable();
                 initToggleToolbar();
                 //handleFilterDatatable();
                 //handleDeleteRows();
                 //handleResetForm();
             }
         }
     }();

     KTDatatablesServerSide.init();

     

     </script>
     
     <script type="text/javascript">
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


     function reloadTable() {
    	 KTDatatablesServerSide.rel();
    		}   	

</script>	

</body>
</html>