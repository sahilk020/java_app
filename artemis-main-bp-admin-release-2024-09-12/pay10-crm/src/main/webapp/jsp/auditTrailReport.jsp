<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="/struts-tags" prefix="s"%>
<html dir="ltr" lang="en-US">
<head>
<title>AuditTrail Report</title>

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

<script type="text/javascript">
$(document).ready(function(){
  $(".adminEmailId").select2();
  
  
  });
</script>

<script type="text/javascript">
	       $(document).ready(function () {

                	                   $(function () {
                        $("#dateFrom").flatpickr({
                            maxDate: new Date(),
                            dateFormat: "d-m-Y",
                            defaultDate: "today",
                            defaultDate: "today",
                        });
                        $("#dateTo").flatpickr({
                            maxDate: new Date(),
                            dateFormat: "d-m-Y",
                            defaultDate: "today",
                            maxDate: new Date()
                        });
                        
                    });
				});

                   function convert(str) {
                       var date = new Date(str), mnth = ("0" + (date.getMonth() + 1))
                           .slice(-2), day = ("0" + date.getDate()).slice(-2);
                       //return [date.getFullYear(), mnth, day].join("-");
                       return [day, mnth, date.getFullYear()].join("-");
                   }

              
                   </script>
                              
                  
<script type="text/javascript">
	$(document).ready(function() {
		$(function() {
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

			$("#submit").click();

		});

	});

	function submit(){
		populateDataTable1();
	}
	var table="";
 	function populateDataTable1() {
       	debugger

   		//var monthVal = parseInt(new Date().getMonth())+1;
   		var emailId = document.getElementById("emailId").value;
   		//var table = new $.fn.dataTable.Api('#auditTrailResultDataTable');
   		 table=$("#auditTrailResultDataTable").DataTable(

   				{
                       dom: 'Brtip',
                       buttons : [
							{
								extend : 'print',
								exportOptions : {
									columns : ':visible'
								}
							},
							{
								extend : 'pdfHtml5',
								orientation : 'landscape',
								pageSize : 'legal',
								//footer : true,
								title : 'Payout Transaction',
								exportOptions : {
									columns : [ ':visible' ]
								},
								customize : function(doc) {
									doc.defaultStyle.alignment = 'center';
									doc.styles.tableHeader.alignment = 'center';
									doc.defaultStyle.fontSize = 8;
								}
							},
							{
								extend : 'copy',
								/* exportOptions : {
									columns : [ 0, 1, 2, 3, 4, 5, 6]
								} */
							exportOptions : {
								columns: [':visible']
							}
							},
							{
								extend : 'csv',
								/* exportOptions : {
									columns : [ 0, 1, 2, 3, 4, 5, 6]
								} */
							exportOptions : {
								columns: [':visible']
							}
							},
							{
								extend : 'pdf',
								exportOptions : {
									columns : [ 0, 1, 2, 3, 4, 5, 6]
								}
							}, 'colvis', 'excel', 'print', ],

                       scrollY: true,
                       scrollX: true,
                       searchDelay: 500,
                       processing: false,
                       // serverSide: true,
                       order: [[1, 'desc']],
                       stateSave: true,
                       dom: 'Brtipl',
                       paging: true,
                       "ajax": {
                           "url": "auditTrailsReportDetailsAction",
                           "type": "POST",
                           "data": function (d) {
                               return generatePostData(d);
                          }
                       },

                       "bProcessing": true,
                       "bLengthChange": true,
                       "bDestroy": true,

                       "aoColumns":[
   								{data:"emailId"},
   								{data:"firstName"},
   								{data:"actionMessageByAction.actionMessage"},
   								{data:"browser"},
   								{data:"ip"},
   								{data:"os"},
   								{data:"timestamp"},
   								{data:"id",class:"d-none"},
   								{data:"payload",class:"d-none"},
//    								{data:null,
// 									render:function(data){
// 									return '<button type="button" data-toggle="modal" data-target="#diffViewModal" class="btn w-100 w-md-100 btn-primary">View Detail</button>';
// 									}
// 								},

                           ]

   				});

//    		$(document).ready(function() {
//             table = $('#auditTrailResultDataTable').DataTable();
//             $('#auditTrailResultDataTable tbody').on('click','td',function(){
//                 var rows = table.rows();
//                 var columnVisible = table.cell(this).index().columnVisible;
//                 var rowIndex = table.cell(this).index().row;
//                 var id = table.cell(rowIndex, 7).data();
//                 var payload = table.cell(rowIndex, 8).data();
//                 document.getElementById("diffId").value = id;
//                 document.getElementById("payload").value = payload;
//                 findDiff(id,payload);
//             });
//         });
 	}

// 	function findDiff(id1,payload1){
// 		var id = document.getElementById("diffId").value?document.getElementById("diffId").value:id1;
// 		var payload = document.getElementById("payload").value?document.getElementById("payload").value:payload1;
// 		var diffUrl = document.getElementById("diffApiUrl").value;
// 		var diffPdfUrl = document.getElementById("diffPdfApiUrl").value;
// 		let isPdf = payload.split(",").length > 150;
// 		if (isPdf) {
// 			window.open(diffPdfUrl + id);
// 			return;
// 		}

// 		console.log("url : diffUrl + id : " +diffUrl + id)
// 		$.ajax({
// 			type : "GET",
// 			url : diffUrl + id,
// 			success : function(data, status) {
// 				document.getElementById("innerDiv").innerHTML = data;
// 				$('#diffViewModal').modal('toggle');

// 			},
// 			error : function(status) {
// 				//alert("Network error please try again later!!");
// 				/* document.getElementById("loadingInner").style.display = "none";
// 				return false; */
// 			}
// 		});
// 	}

		function closePopUp(){
			$('#diffViewModal').modal('toggle');
		}
	function generatePostData(d) {
		var token = document.getElementsByName("token")[0].value;
		var obj = {
			emailId : document.getElementById("emailId").value,
			dateFrom : document.getElementById("dateFrom").value,
			dateTo : document.getElementById("dateTo").value,
			draw : d.draw,
			length :d.length,
			start : d.start,
			token : token,
			"struts.token.name" : "token",
		};
		return obj;
	}
</script>


<style>
	.dt-buttons.btn-group.flex-wrap {
	display:none;
	}
	ins {
		text-decoration: none;
		background-color: #d4fcbc;
		overflow-wrap: break-word;
	}

	del {
		text-decoration: line-through;
		background-color: #fbb6c2;
		overflow-wrap: break-word;
		color: #555;
	}
	button.close {
    background: white;
    border: none;
    font-size: x-large;
}
	</style>
</head>
<body id="mainBody">
	<div class="content flex-column" id="kt_content">
	   <div class="toolbar" id="kt_toolbar">
		  <!--begin::Container-->
		  <div id="kt_toolbar_container" class="container-fluid d-flex flex-stack">
			 <!--begin::Page title-->
			 <div data-kt-swapper="true" data-kt-swapper-mode="prepend" data-kt-swapper-parent="{default: '#kt_content_container', 'lg': '#kt_toolbar_container'}" class="page-title d-flex align-items-center me-3 flex-wrap mb-5 mb-lg-0 lh-1">
				<!--begin::Title-->
				<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">AuditTrail Report</h1>
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
				   <li class="breadcrumb-item text-muted">Audit Trail</li>
				   <!--end::Item-->
				   <!--begin::Item-->
				   <li class="breadcrumb-item">
					  <span class="bullet bg-gray-200 w-5px h-2px"></span>
				   </li>
				   <!--end::Item-->
				   <!--begin::Item-->
				   <li class="breadcrumb-item text-dark">AuditTrail Report</li>
				   <!--end::Item-->
				</ul>
				<!--end::Breadcrumb-->
			 </div>
			 <!--end::Page title-->
		  </div>
		  <!--end::Container-->
	   </div>
	   <div class="post d-flex flex-column-fluid" id="kt_post">
		  <div id="kt_content_container" class="container-xxl">
			 <div class="modal fade" id="diffViewModal" data-backdrop="static" data-keyboard="false" tabindex="-1" aria-labelledby="staticBackdropLabel" aria-hidden="true">
				<div class="modal-dialog">
				   <div class="modal-content">
					<div class="modal-header">
						<h5 class="modal-title">View Diff</h5>
						<button type="button" class="close " data-dismiss="modal" aria-label="Close" onclick="closePopUp()">
						  <span aria-hidden="true">&times;</span>
						</button>
					  </div>
					  <div class="modal-body" style="word-break: break-word;">
						 <h8>Notes: Green color indicate that new changes added</h8>
						 <br/>
						 <h8>Notes: Red color indicate that field updated</h8>
						 <input type="hidden" id="diffId" />
						 <input type="hidden" id="payload" />
						 <div id="innerDiv" style="background-color: lightgrey; text-align: center;"></div>
					  </div>
					  <div align="center" style="padding: 7px;">
						 <button type="button" class="btn btn-lg btn-danger" id="btnRefundCancel" class="close" data-dismiss="modal" aria-label="Close"  onclick="closePopUp()">Close</button>
					  </div>
				   </div>
				</div>
			 </div>
			 <s:hidden name="diffApiUrl" id="diffApiUrl" value="%{diffApiUrl}" />
			 <s:hidden name="diffPdfApiUrl" id="diffPdfApiUrl" value="%{diffPdfApiUrl}" />
				<div class="col">
				   <div class="card">
					  <div class="card-body">
						 <div class="MerchBx">
							<div class="row">
							   <div class="col-md-12">
								  <div class="card">
									 <div class="card-body">
										<div class="container">
										   <div class="row">
											  <div class="col-sm-12 col-md-3 col-lg-3">
												 <label class="d-flex align-items-center fs-6 fw-semibold mb-2">Users Email </label>
												 <s:select name="emailId" class="form-select form-select-solid "
													id="emailId" headerKey="" headerValue="ALL" list="users"
													listKey="emailId" listValue="emailId" autocomplete="off" />
											  </div>
											  <!-- <date> -->
											  <div class="col-sm-12 col-md-3 col-lg-3">
												 <label class="d-flex align-items-center fs-6 fw-semibold mb-2">
												 <span class="">Audit Trail From</span>
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
											  <div class="col-sm-12 col-md-3 col-lg-3">
												 <label class="d-flex align-items-center fs-6 fw-semibold mb-2">
												 <span class="">Audit Trail To</span>
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
											  <div class="col-sm-12 col-md-3 col-lg-3">
												 <button type="button" id="submit" value="Submit"
													class="btn btn-primary w-100 w-md-100  mt-7 submit_btn" onclick="submit()">Submit</button>
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
							<div class="row my-5">
							   <div class="col">
								  <div class="card">
									 <div class="card-body">
										<div class="scrollD">
										   <div class="row g-9 mb-8 justify-content-end">
											  <div class="col-lg-4 col-sm-12 col-md-6">
												 <select name="currency" data-control="select2"
												 data-hide-search="true"
													data-placeholder="Actions" id="actions11"
													class="form-select form-select-solid "
													onchange="myFunctions();">
													<option value="">Actions</option>
													<option value="copy">Copy</option>
													<option value="csv">CSV</option>
													
												<!-- 	<option value="print">PRINT</option> -->
												 </select>
											  </div>
											  <div class="col-lg-4 col-sm-12 col-md-6">
												 <div class="dropdown1">
													<button
													   class="form-select form-select-solid actions dropbtn1">Customize
													Columns</button>
													<div class="dropdown-content1">
													   <a	class="toggle-vis" data-column="0">Email Id</a>
													   <a class="toggle-vis" data-column="1">First Name</a>
													   <a class="toggle-vis" data-column="2">Action</a>
													   <a class="toggle-vis" data-column="3">Browser</a>
													   <a class="toggle-vis" data-column="4">IP</a>
													   <a class="toggle-vis" data-column="5">OS</a>
													   <a class="toggle-vis" data-column="6">Date time</a>
													  <!--  <a class="toggle-vis" data-column="7">Action</a> -->
													</div>
												 </div>
											  </div>
										   </div>
										   <table id="auditTrailResultDataTable" class="table table-striped table-row-bordered gy-5 gs-7" cellspacing="0" width="100%">
											  <thead>
												 <tr class=" fw-bold fs-6 text-gray-800">
													<th style='text-align: left'>Email Id</th>
													<th style='text-align: left'>First Name</th>
													<th style='text-align: left'>Action</th>
													<th style='text-align: left'>Browser</th>
													<th style='text-align: left'>IP</th>
													<th style='text-align: left'>OS</th>
													<th style='text-align: left'>Date time</th>
													<th style='text-align: left'></th>
													<th style='text-align: left'></th>
<!-- 													<th style='text-align: center'>Action</th> -->
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
	   </div>
	</div>

	<script type="text/javascript">
	   $('a.toggle-vis').on('click', function(e) {
		   e.preventDefault();
		   table = $('#auditTrailResultDataTable').DataTable();
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
		 
		}
	</script>
	<script type="text/javascript">
$(document).ready(function(){
  $("#emailId").select2();
  
  
  });
</script>
	<script src="../assets/plugins/custom/datatables/datatables.bundle.js"></script>
 </body>
</html>
