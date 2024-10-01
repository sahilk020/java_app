<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>View Chargeback</title>


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






<%-- <link href="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.css" rel="stylesheet" type="text/css" />
<link href="../assets/plugins/custom/datatables/datatables.bundle.css" rel="stylesheet" type="text/css" />
<link href="../assets/plugins/global/plugins.bundle.css" rel="stylesheet" type="text/css" />
<link href="../assets/css/style.bundle.css" rel="stylesheet" type="text/css" />
<link href="../assets/css/custom/custom-style.css" rel="stylesheet" type="text/css" />

<!-- 
<link href="../css/Jquerydatatableview.css" rel="stylesheet" />
<link href="../css/Jquerydatatable.css" rel="stylesheet" />
<link href="../css/default.css" rel="stylesheet" type="text/css" />
<link href="../css/jquery-ui.css" rel="stylesheet" /> -->

<script src="../js/jquery.min.js"></script>
<script src="../js/commanValidate.js"></script>
<script src="../js/core/popper.min.js"></script>
<script src="../js/core/bootstrap-material-design.min.js"></script>
<script src="../js/plugins/perfect-scrollbar.jquery.min.js"></script>
<!-- Control Center for Material Dashboard: parallax effects, scripts for the example pages etc -->
<script src="../js/material-dashboard.js?v=2.1.0" type="text/javascript"></script>
<script src="../js/jquery.dataTables.js"></script>
<script src="../js/jquery-ui.js"></script>
<script src="../js/jquery.popupoverlay.js"></script> 
<script type="text/javascript" src="../js/dataTables.buttons.js"></script>  
<script type="text/javascript" src="../js/pdfmake.js"></script>
<!-- 
<link href="../css/select2.min.css" rel="stylesheet" /> -->

<script src="../js/jquery.select2.js" type="text/javascript"></script>
<script src="../assets/js/scripts.bundle.js"></script> --%>
<script type="text/javascript">
$(document).ready(function(){
 
  // Initialize select2
  $(".adminMerchants").select2();
});
</script>


<script type="text/javascript">
function handleChange() {
	$('#loader-wrapper').show();
	reloadTable();
}

	$(document).ready(function() {
		
		$(function() {
			/* $("#dateFrom").datepicker({
				prevText : "click for previous months",
				nextText : "click for next months",
				showOtherMonths : true,
				dateFormat : 'dd-mm-yy',
				selectOtherMonths : true,
				maxDate : new Date()
			}); */
			$("#dateFrom").flatpickr({
                maxDate: new Date(),
                dateFormat: "d-m-Y",
                defaultDate: "today",
                
            });
			$("#dateTo").flatpickr({
                maxDate: new Date(),
                dateFormat: "d-m-Y",
                defaultDate: "today",
                maxDate: new Date()
            });

			/* 
			$("#dateTo").datepicker({
				prevText : "click for previous months",
				nextText : "click for next months",
				showOtherMonths : true,
				dateFormat : 'dd-mm-yy',
				selectOtherMonths : true,
				maxDate : new Date()
			}); */
		});


		
		$(function() {
			var today = new Date();
			$('#dateTo').val($.datepicker.formatDate('dd-mm-yy', today));
			$('#dateFrom').val($.datepicker.formatDate('dd-mm-yy', today));
			renderTable();

		});
		
		
			$(function() {
			var datepick = $.datepicker;
			var table = $('#chargebackDataTable').DataTable();
			$('#chargebackDataTable tbody').on('click', '.my_class', function() {
				submitForm(table, this);
				
			});
		});
	});
	
	function submitForm(table, index) {
		var rowIndex = table.cell(index).index().row;
		var cbId = table.cell(rowIndex, 1).data();
		document.getElementById('cbId').value = cbId;
		document.viewChargebackDetails.submit();
	}
	
	function renderTable() {
		var table = new $.fn.dataTable.Api('#chargebackDataTable');
		$.ajaxSetup({
			global : false,
			beforeSend : function() {
				toggleAjaxLoader();
			},
			complete : function() {
				toggleAjaxLoader();
			}
		});
		var transFrom = $.datepicker.parseDate('dd-mm-yy', $('#dateFrom').val());
		var transTo = $.datepicker.parseDate('dd-mm-yy', $('#dateTo').val());
		if (transFrom == null || transTo == null) {
			alert('Enter date value');
			return false;
		}

		if (transFrom > transTo) {
			alert('From date must be before the to date');
			$('#dateFrom').focus();
			return false;
		}
		if (transTo - transFrom > 31 * 86400000) {
			alert('No. of days can not be more than 31');
			$('#dateFrom').focus();
			return false;
		}
		var token = document.getElementsByName("token")[0].value;
       	$('#chargebackDataTable').DataTable({
			dom : 'BTftlpi',
			buttons : [ {
				extend : 'copyHtml5',
				exportOptions : {
					columns : [ ':visible' ]
				}
			}, {
				extend : 'csvHtml5',
				title : 'Chargeback_' + getCurrentTimeStamp().replace(" ",""),
				exportOptions : {
					columns : [ ':visible' ]
				}
			}, {
				extend : 'pdfHtml5',
				title : 'Chargeback_' + getCurrentTimeStamp().replace(" ",""),
                orientation: 'landscape',
				exportOptions : {
					columns : [ ':visible' ]
				}
			}, 
			// Disabled print button.
			/* {extend : 'print',title : 'Chargeback_' + getCurrentTimeStamp().replace(" ",""),orientation: 'landscape',exportOptions : columns : [':visible']}, */{
				extend : 'colvis',
				//           collectionLayout: 'fixed two-column',
				columns : [ 0, 1, 2, 3, 4, 5, 6,7,8,9]
			} ],
			"ajax" : {
				type : "POST",
				url : "viewChargebackAction",
				data :function (d){
					return generatePostData(d);
				}
			},
			
			    "processing": false,
		        "paginationType": "full_numbers", 
		        "lengthMenu": [[10, 25, 50, -1], [10, 25, 50, "All"]],
		        "order" : [ [ 1, "desc" ] ],
		        "columnDefs": [
		            { "visible": false, "targets": 1 }
		          ],
			"columns" : [
			{
				"data" : "caseId",
				//"width" : '14%',
				"className" : " my_class text-class",
				"width": "4%"
			}, {
				"data" : "id",
				"className" : "text-class"
			}, {
				"data" : "businessName",
				"className" : "text-class"
			}, {
				"data" : "orderId",
				"className" : "text-class"
				//"width" : '14%'
			},  {
				"data" : "pg_ref_num",
				"className" : "text-class"
				
				//"width" : '14%'
			}, {
				"data" : "chargebackType",
				"className" : "text-class"
				//"width" : '13%'
			}, {
				"data" : "chargebackStatus",
				"className" : "text-class"
				//"width" : '10%'
			},{
				"data" : "amount",
				"className" : "text-class",
				"render" : function(data){
									return inrFormat(data);
								}
			
			}, {
				"data" : "targetDate",
				"className" : "text-class"
			}, {
				"data" : "createDate",
				"className" : "text-class",
				"render" : function(data){
					let y = x = data.split('T')[0].split('-');
					return y[2] + '-'+ y[1] + '-' + y[0];
				}
			} ]
		});
/*  popup=dtRender;
dtRender.on( 'xhr', function () {
var data = dtRender.ajax.json();
dataSet=JSON.stringify(data);
} );  */
	}
	function generatePostData(d) {
		var token = document.getElementsByName("token")[0].value;
		var obj = {
				payId : document.getElementById("merchant").value,
				chargebackType : document.getElementById("chargebackType").value,
				chargebackStatus : document.getElementById("chargebackStatus").value,
				dateTo : document.getElementById("dateTo").value,
				dateFrom : document.getElementById("dateFrom").value,
				
			//draw : d.draw,
			//length :d.length,
			//start : d.start, 
			token : token,
			"struts.token.name" : "token",
		};
		return obj;
	}
	
	function reloadTable() {
		var datepick = $.datepicker;
		var transFrom = $.datepicker.parseDate('dd-mm-yy', $('#dateFrom').val());
		var transTo = $.datepicker.parseDate('dd-mm-yy', $('#dateTo').val());
		if (transFrom == null || transTo == null) {
			alert('Enter date value');
			return false;
		}

		if (transFrom > transTo) {
			alert('From date must be before the to date');
			$('#loader-wrapper').hide();
			$('#dateFrom').focus();
			return false;
		}
		if (transTo - transFrom > 31 * 86400000) {
			alert('No. of days can not be more than 31');
			$('#loader-wrapper').hide();
			$('#dateFrom').focus();
			return false;
		}

		var tableObj = $('#chargebackDataTable');
		var table = tableObj.DataTable();
		table.ajax.reload();
	}
	
</script>

<style>
  .divalignment{
	  margin-top: -38px !important;
  }
  
  .case-design{
	  text-decoration:none;
	  cursor: default !important;
  }
  .my_class {
    color: #0040ff !important;
    text-decoration: none !important;
    cursor: pointer;
    *cursor: hand;
}
tr td.my_class{
	cursor: pointer;
	text-decoration: none !important;
}
tr td.my_class:hover{
	cursor: pointer !important;
	text-decoration: none !important;
}

tr th.my_class:hover{
	color: #fff !important;
}
  .my_class:hover{
	  color: #2196f3  !important;
	      cursor: pointer !important;
  }
  .my_class.sorting_1{
  cursor:pointer !important;
  }
  /* .txnf {
    margin: 20px 0px 0px 0px !important;
} */
table.dataTable.display tbody tr.odd {
    background-color: #e6e6ff !important;
}
table.dataTable.display tbody tr.odd > .sorting_1{
	 background-color: #e6e6ff !important;
}
/* #chargebackDataTable_wrapper{
overflow-x: scroll !important;
} */
.txnf {
	border: none !important;
}
table.dataTable thead .sorting:before, table.dataTable thead .sorting_asc:before, table.dataTable thead .sorting_desc:before, table.dataTable thead .sorting_asc_disabled:before, table.dataTable thead .sorting_desc_disabled:before {
  
    content: none !important;
}
table.dataTable thead .sorting:after, table.dataTable thead .sorting_asc:after, table.dataTable thead .sorting_desc:after, table.dataTable thead .sorting_asc_disabled:after, table.dataTable thead .sorting_desc_disabled:after {
 
    content: none !important;
}


</style>


</head>
<body>


<div class="content d-flex flex-column flex-column-fluid" id="kt_content">

<div class="toolbar" id="kt_toolbar">
							<!--begin::Container-->
							<div id="kt_toolbar_container" class="container-fluid d-flex flex-stack">
								<!--begin::Page title-->
								<div data-kt-swapper="true" data-kt-swapper-mode="prepend" data-kt-swapper-parent="{default: '#kt_content_container', 'lg': '#kt_toolbar_container'}" class="page-title d-flex align-items-center me-3 flex-wrap mb-5 mb-lg-0 lh-1">
									<!--begin::Title-->
									<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">Chargeback Case</h1>
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
										<li class="breadcrumb-item text-muted">Chargeback</li>
										<!--end::Item-->
										<!--begin::Item-->
										<li class="breadcrumb-item">
											<span class="bullet bg-gray-200 w-5px h-2px"></span>
										</li>
										<!--end::Item-->
										<!--begin::Item-->
										<li class="breadcrumb-item text-dark">Chargeback Case</li>
										<!--end::Item-->
									</ul>
									<!--end::Breadcrumb-->
								</div>
								<!--end::Page title-->
								
							</div>
							<!--end::Container-->
						</div>



	 <div id="loader-wrapper" style="width: 100%; height: 100%; display:none;">
		<div id="loader"></div>
	  </div> 
	  <div class="post d-flex flex-column-fluid" id="kt_post">
	<div id="kt_content_container" class="container-xxl">
	<div style="overflow-x: scroll;">
<!-- <form> -->
<table width="100%" align="left" cellpadding="0" cellspacing="0"
		class="txnf">
		<!-- 
		<div class="col-md-12">
			<div class="card ">
				<div class="card-header card-header-rose card-header-text">
					<div class="card-text">
						<h4 class="card-title">Chargeback Case</h4>
					</div>
				</div>
				<div class="card-body ">
					<div class="container">
						<div class="row"> -->
	<div class="row my-5">
	<div class="col">
	<div class="card">
	<div class="card-body">
	<div class="container">
						<div class="row">

							<div class="col-sm-4 col-lg-4">
								<label class="d-flex align-items-center fs-6 fw-semibold mb-2">Merchant</label><br>
								<div class="txtnew">
									<s:if test="%{#session.USER.UserType.name()=='ADMIN' || #session.USER.UserType.name()=='SUBADMIN'  || #session.USER.UserType.name()=='SUBADMIN' || #session.USER_TYPE.name()=='SUPERADMIN'}">
								<s:select name="merchant" id="merchant" class="form-select form-select-solid"
									headerKey="ALL" headerValue="ALL" list="merchantList"
									listKey="payId" listValue="businessName" autocomplete="off" onchange="handleChange();" />
							</s:if>
							 <!-- class="input-control adminMerchants" -->
							<s:else>
								<s:select name="merchant" class="input-control" id="merchant" list="merchantList"
									listKey="payId" listValue="businessName" autocomplete="off" onchange="handleChange();"/>
							</s:else>
								</div>
								
							</div>
							<div class="col-sm-4 col-lg-4">
								<label class="d-flex align-items-center fs-6 fw-semibold mb-2">Chargeback Type</label><br>
								<div class="txtnew">
									<s:select headerKey="ALL" headerValue="ALL" id="chargebackType"
									name="chargebackType" class="form-select form-select-solid"
									list="@com.pay10.crm.chargeback_new.util.ChargebackType@values()"
									listKey="code" listValue="name" onchange="handleChange();"  autocomplete="off" 
									/>
								</div>
							</div>
							<div class="col-sm-4 col-lg-4">
								<label class="d-flex align-items-center fs-6 fw-semibold mb-2">Chargeback Status</label><br>
								<div class="txtnew">
									<s:select headerKey="ALL" headerValue="ALL" id="chargebackStatus"
									name="chargebackStatus" class="form-select form-select-solid"
									list="@com.pay10.crm.chargeback_new.util.CaseStatus@values()"
									listKey="code" listValue="name" onchange="handleChange();"  autocomplete="off"
									 />
								</div>
							</div>
							
							<div class="col-sm-4 col-lg-4">
											<!-- <div class="col-md-4 fv-row"> -->
                                                    <label class="d-flex align-items-center fs-6 fw-semibold mb-2">
                                                        <span class="">Creation Date From</span>
                                                    </label>
                                                    <!--end::Label-->
                                                    <div class="position-relative d-flex align-items-center">
                                                        <!--begin::Icon-->
                                                        <!--begin::Svg Icon | path: icons/duotune/general/gen014.svg-->
                                                        <span class="svg-icon svg-icon-2 position-absolute mx-4">
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
                                                            placeholder="Select a date" name="dateFrom" id="dateFrom"
                                                            type="text" readonly="readonly">
                                                        <!--end::Datepicker-->
                                                    </div>
                                                </div>
                                                
                                                
                                                
							<div class="col-sm-4 col-lg-4">
											<!-- <div class="col-md-4 fv-row"> -->
                                                    <label class="d-flex align-items-center fs-6 fw-semibold mb-2">
                                                        <span class="">Creation Date To</span>
                                                    </label>
                                                    <!--end::Label-->
                                                    <div class="position-relative d-flex align-items-center">
                                                        <!--begin::Icon-->
                                                        <!--begin::Svg Icon | path: icons/duotune/general/gen014.svg-->
                                                        <span class="svg-icon svg-icon-2 position-absolute mx-4">
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
                                                            placeholder="Select a date" name="dateTo" id="dateTo"
                                                            type="text" readonly="readonly">
                                                        <!--end::Datepicker-->
                                                    </div>
                                                </div>
							
							
							
							<%-- <div class="col-sm-6 col-lg-3">
								<label class="d-flex align-items-center fs-6 fw-semibold mb-2">Creation Date From</label><br>
								<div class="txtnew">
									<s:textfield type="text" readonly="true" id="dateFrom"
							name="dateFrom" class="input-control" onchange="handleChange();" 
							autocomplete="off" />
								</div>

							</div>
                      
                      
                      
                      
                      
							<div class="col-sm-6 col-lg-3" >
								<label class="d-flex align-items-center fs-6 fw-semibold mb-2">Creation Date To</label><br>
								<div class="txtnew" >
									<s:textfield type="text" readonly="true" id="dateTo" name="dateTo"
							class="input-control" onchange="handleChange();" autocomplete="off" />
								</div>
							</div> --%>
							
						</div>
					</div>
				</div>
			</div>
		</div>
		</div>
		
		
		<!-- <tr>
			<td align="left">&nbsp;</td>
		</tr> -->
		<tr>
			<td align="left" >
			<div class="scrollD">
					<table width="100%" align="left" cellpadding="0" cellspacing="0" class="txnf">

		<tr>
			<td align="left" colspan="3" >
	<div class="row my-5">
	<div class="col">
	<div class="card">
	<div class="card-body">
	<div class="container">
						<div class="row">
            <div class="scrollD">
            <div class="row g-9 mb-8 justify-content-end">
                                            <div class="col-lg-4 col-sm-12 col-md-6">
                                              <select name="currency" data-control="select2" data-placeholder="Actions"
                                                class="form-select form-select-solid actions" data-hide-search="true">
                                                <option value="">Actions</option>
                                                <option value="copy">Copy</option>
                                                <option value="csv" >CSV</option>
                                                 <option value="pdf">PDF</option>		
                                                
                                                
                                              </select>
                                            </div>
                                            <div class="col-lg-4 col-sm-12 col-md-6">
                                              <select name="currency" data-control="select2" data-placeholder="Customize Columns"
                                                class="form-select form-select-solid actions" data-hide-search="true">
                                               <option value="">Customize Columns</option>
                                                <option value="">Case ID</option>
                                                <option value="">Chargeback Id</option>
                                                <option value="">Merchant Name</option>
                                                <option value="">Order Id</option>
                                                 <option value="">PG REF NUM</option>
                                                 <option value="">Chargeback Type</option>
                                                 <option value="">Chargeback Status</option>
                                                  <option value="">Merchant Amount</option>
                                                   <option value="">Due Date</option> 
                                                                                               
                                              	 <option value="">Creation Date</option>
                                           
                                                                                                
                                              </select>
                                            </div>
                                          </div>
            <!-- class="display table" -->
				<table id="chargebackDataTable" class="table table-striped table-row-bordered gy-5 gs-7" cellspacing="0" width="100%">
					<thead>
						<!-- <tr class="boxheadingsmall"> -->
						<tr class="fw-bold fs-6 text-gray-800">
						
							<!-- <th class="case-design my_class" style="color: white !important;">Case ID</th>  -->
							<th class="case-design my_class">Case ID</th> 
							<th>Chargeback Id</th>
							<th>Merchant Name</th>
							<th>Order Id</th>
							<th>PG REF NUM</th>
							<th>Chargeback Type</th>
							<th>Chargeback Status</th>
							<th>Merchant Amount</th>
							<th>Due Date</th>
							<th>Creation Date</th>
						</tr>
					</thead>
				</table>
                </div>
                </div>
                </div>
                </div>
                </div>
                </div>
                </div>
                </td>
		</tr>
</table>
				</div></td>
		</tr>
	</table>
	</div>
	</div>
	</div>
<!-- </form> -->
<s:form name="viewChargebackDetails" action="viewChargebackDetailsAction">
		<s:hidden name="Id" id="cbId" value="" />
		
		<s:hidden name="token" value="%{#session.customToken}" />
	</s:form>
	</div>
</body>
</html>