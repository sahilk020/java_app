<%@page import="com.pay10.crm.actionBeans.TransactionStatusBean"%>
<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="/struts-tags" prefix="s"%>
<html dir="ltr" lang="en-US">

<head>
<title>Acquirer Nodal Mapping</title>
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
<script src="../js/loader/main.js"></script>
<script src="../assets/plugins/global/plugins.bundle.js"></script>
<script src="../assets/js/scripts.bundle.js"></script>
<script
	src="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.js"></script>
<script src="../assets/plugins/custom/datatables/datatables.bundle.js"></script>
<script src="../assets/js/widgets.bundle.js"></script>
<script src="../assets/js/custom/widgets.js"></script>
<script src="../assets/js/custom/apps/chat/chat.js"></script>
<script src="../assets/js/custom/utilities/modals/upgrade-plan.js"></script>
<script src="../assets/js/custom/utilities/modals/create-app.js"></script>
<script src="../assets/js/custom/utilities/modals/users-search.js"></script>
<script src="../js/commanValidate.js"></script>
<style>
.textarea {
	resize: none;
	vertical-align: middle;
	/* width: 350px !important; */
}

.height {
	height: 100px !important;
}

input:focus, textarea:focus, select:focus {
	outline: none !important;
}

.msgfailed {
	color: red;
	font-size: 30px;
	border: 2px solid #ff6666;
	padding: 2px 24px 2px 20px;
	border-radius: 10px;
}

.msgSuccess {
	color: rgb(13, 184, 90);
	font-size: 30px;
	border: 2px solid rgb(13, 184, 90);
	padding: 2px 24px 2px 20px;
	border-radius: 10px;
}

div#txnResultDataTable_wrapper {
	margin-top: 10px !important;
}

.bold {
	color: black !important;
	font-weight: bold !important;
}

.dt-buttons.btn-group.flex-wrap {
	display: none;
}
</style>
<script>
	$(document).ready(function() {

		$(".adminMerchants").select2();

	});
</script>
</head>

<body id="kt_body"
	class="header-fixed header-tablet-and-mobile-fixed toolbar-enabled toolbar-fixed toolbar-tablet-and-mobile-fixed aside-enabled aside-fixed"
	style="-kt-toolbar-height: 55px; - -kt-toolbar-height-tablet-and-mobile: 55px">
	<div class="content d-flex flex-column flex-column-fluid"
		id="kt_content">
		<div class="toolbar" id="kt_toolbar">
			<!--begin::Container-->
			<div id="kt_toolbar_container"
				class="container-fluid d-flex flex-stack">
				<div data-kt-swapper="true" data-kt-swapper-mode="prepend"
					data-kt-swapper-parent="{default: '#kt_content_container', 'lg': '#kt_toolbar_container'}"
					class="page-title d-flex align-items-center me-3 flex-wrap mb-5 mb-lg-0 lh-1">
					<!--begin::Title-->
					<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">
						Acquirer Nodal Mapping
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
							<li class="breadcrumb-item text-muted">Liability Management</li>
							<!--end::Item-->
							<!--begin::Item-->
							<li class="breadcrumb-item"><span
								class="bullet bg-gray-200 w-5px h-2px"></span></li>
							<!--end::Item-->
							<!--begin::Item-->
							<li class="breadcrumb-item text-dark">Acquirer Nodal Mapping</li>
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
											<span class="required">Acquirer</span>
										</label>
										<s:select headerKey="" headerValue="Please Select bank"
											list="@com.pay10.commons.util.AcquirerTypeUI@values()"
											id="acquirer"
											class="form-select form-select-solid adminMerchants"
											name="acquirer" value="acquirer" listValue="name"
											listKey="code" />
									</div>
									<div class="col-lg-4 my-2">
										<label class="d-flex align-items-center fs-6 fw-bold mb-2">
											<span class="required">Nodal Bank List</span>
										</label>
										<s:select headerKey="" headerValue="Please Select bank"
											list="bankLists" id="bank"
											class="form-select form-select-solid adminMerchants"
											name="name" listValue="bankName" listKey="accountNumber" />
									</div>

									<div class="col-lg-4 my-2">
										<label class="d-flex align-items-center fs-6 fw-bold mb-2">
											<span class="">&nbsp</span>
										</label>
										<button type="button" class="btn btn-primary" id="done"
											disabled="disabled" name="done" onclick="done()">Submit</button>
									</div>

								</div>
							</div>
						</div>
					</div>
				</div>

				<div class="card">
					<div class="card-body ">

						<div class="row">
							<div class="col-lg-12">

								<table id="txnResultDataTable"
									class="table table-striped table-row-bordered gy-5 gs-7 dataTable no-footer">
									<thead>
										<tr>
											<th class="bold">Acquirer</th>
											<th class="bold">Nodal Account Number</th>
											<th class="bold">Nodal Name</th>
											<th class="bold">Nodal Ifsc</th>
											<th class="bold">Create At</th>
											<th class="bold">Create By</th>
											<th class="bold">Updated At</th>
											<th class="bold">Updated By</th>
											<th class="bold">Start Date</th>
											<th class="bold">Endate</th>
											<th class="bold">status</th>
<!-- 											<th class="bold">activeFlag</th> -->
											<th class="bold">Action</th>
										</tr>
									</thead>
									<tfoot>
										<tr class="fw-bold fs-6 text-gray-800">
											<th></th>
											<th></th>
											<th></th>
											<th></th>
											<th></th>
											<th></th>
											<th></th>
											<th></th>
											<th></th>
											<th></th>
<!-- 											<th></th> -->
											<th></th>
											<th></th>
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
	<script src="../assets/plugins/custom/datatables/datatables.bundle.js"></script>
	<script type="text/javascript">
		function generatePostData(d) {
			debugger
			var token = document.getElementsByName("token")[0].value;

			var obj = {
				draw : d.draw,
				length : d.length,
				start : d.start,
				token : token,
				"struts.token.name" : "token",
			};

			return obj;
		}

		$('#txnResultDataTable')

				.dataTable(

						{
							"columnDefs" : [ {
								className : "dt-body-right",
								"targets" : [ 1, 2, 3, 4, 5, 6, 7, 8, 9 ,10,11]
							} ],

							dom: 'BTftlpi',
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
										title : 'Search Transactions',
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
										exportOptions : {
											columns : [ 0, 1, 2, 3, 4, 5, 6, 7,
													8, 9, 10, 11, 12, 13, 14,
													15, 16, 17, 18, 19, 20 ]
										}
									},
									{
										extend : 'csv',
										exportOptions : {
											columns : [ 0, 1, 2, 3, 4, 5, 6, 7,
													8, 9, 10, 11, 12, 13, 14,
													15, 16, 17, 18, 19 ]
										}
									},
									{
										extend : 'pdf',
										exportOptions : {
											columns : [ 0, 1, 2, 3, 4, 5, 6, 7,
													8, 9, 10, 11, 12, 13, 14,
													15, 16, 17, 18, 19, 20, 21,
													22 ]
										}
									}, 'colvis', 'excel', 'print', ],
							scrollY : true,
							scrollX : true,
							searchDelay : 500,
							processing : false,
							destroy : true,
							serverSide : true,
							order : [ [ 5, 'desc' ] ],
							stateSave : true,
							'columnDefs': [{
					               "searchable": true, "targets": 0 
				            }],

							"ajax" : {

								"url" : "AcquirerNodalMappingList",
								"type" : "POST",
								"timeout" : 0,
								"data" : function(d) {
									return generatePostData(d);
								}
							},
							"searching" : false,
							"ordering" : false,
							"processing" : true,
							"serverSide" : true,
							"paginationType" : "full_numbers",
							"lengthMenu" : [ [ 10, 25, 50, 100 ],
									[ 10, 25, 50, 100 ] ],
							"order" : [ [ 2, "desc" ] ],

							"columnDefs" : [ {
								"type" : "html-num-fmt",
								"targets" : 4,
								"orderable" : true,
								"targets" : [ 0, 1, 2, 3, 4, 5, 6, 7,8,9,10 ]
							} ],

							"columns" : [

							{
								"data" : "acquirer",
								"className" : "txnId my_class1 text-class",
							}, {
								"data" : "nodal",
								"className" : "payId text-class"

							}, {
								"data" : "bankName",
								"className" : "payId text-class"

							}, {
								"data" : "ifscCode",
								"className" : "payId text-class"

							}, {
								"data" : "createAt",
								"className" : "text-class",
								 render: function(data, type, row){
						                if(type === "sort" || type === "type"){
						                    return data!=null?data:"N/A";
						                }
						                return data!=null?moment(data).format("YYYY-MM-DD HH:mm:ss"):"N/A";
						            }
							}, {
								"data" : "createBy",
								"className" : "text-class"
							}, {
								"data" : "updatedAt",
								"className" : "orderId text-class",
								 render: function(data, type, row){
						                if(type === "sort" || type === "type"){
						                    return data!=null?data:"N/A";
						                }
						                return data!=null?moment(data).format("YYYY-MM-DD HH:mm:ss"):"N/A";
						            }
							}, {
								"data" : "updatedBy",
								"className" : "orderId text-class"
							}, {
								"data" : "startAt",
								"className" : "mopType text-class", 
								render: function(data, type, row){
					                if(type === "sort" || type === "type"){
					                    return data!=null?data:"N/A";
					                }
					                return data!=null?moment(data).format("YYYY-MM-DD HH:mm:ss"):"N/A";
					            },
							}, {
								"data" : "endDate",
								"className" : "txnType text-class",
								 render: function(data, type, row){
						                if(type === "sort" || type === "type"){
						                    return data!=null?data:"N/A";
						                }
						                return data!=null?moment(data).format("YYYY-MM-DD HH:mm:ss"):"N/A";
						            },
							}, {
								"data" : "status",
								"className" : "txnType text-class",
							} , {
								"data" : null,
										'render' : function(data, type, full,
												meta) {
													if(data.status!="Approved"){
													return '<h1><button class="btn btn-primary btn-xs" disabled="disabled" name="Approve" id="Approve" onclick="approve('+"'"+data.sno+"'"+ ')">Approve</button></h1>';
													}else{
														return "";
													}
										}
							}]

						});

		function done() {
			var acquirer = $("#acquirer").val();
			var nodal = $("#bank").val();

			if (acquirer == "" || acquirer == null || acquirer == undefined) {
				alert("Please Select Acquirer");

				return false;
			}

			if (nodal == "" || nodal == null || nodal == undefined) {
				alert("Please Select Nodal Bank");
				return false;
			}

			var userType = "<s:property value='%{#session.USER.emailId}'/>";
			var token = document.getElementsByName("token")[0].value;
			
			data = {
					"acquirer" : acquirer,
					"nodal" : nodal,
					"userType" : userType,
					"token" : token,
					"struts.token.name" : "token"
				}
			
			
			$
			.ajax({
				type : "POST",
				url : "AcquirerNodalMapped",
				data : data,
				timeout : 0,
				success : function(responseData, status) {

					alert(responseData.response);

					window.location.reload();
				},
				error : function(data) {
					console
							.log("Network error, Acquirer nodal mapping may not be saved");
				}
			});

		}
		function approve(id) {
			

			var userType = "<s:property value='%{#session.USER.emailId}'/>";
			var token = document.getElementsByName("token")[0].value;
			
			data = {
					"id" : id,
					"userType" : userType,
					"token" : token,
					"struts.token.name" : "token"
				}
			
			
			$
			.ajax({
				type : "POST",
				url : "ApproveAcquirerNodalMapped",
				data : data,
				timeout : 0,
				success : function(responseData, status) {

					alert(responseData.response);

					window.location.reload();
				},
				error : function(data) {
					console
							.log("Network error, Acquirer nodal mapping may not be saved");
				}
			});

		}
		
		function enableBaseOnAccess() {
			setTimeout(function () {
				if ($('#AcquirerNodalMapping').hasClass("active")) {
					var menuAccess = document.getElementById("menuAccessByROLE").value;
					var accessMap = JSON.parse(menuAccess);
					var access = accessMap["AcquirerNodalMapping"];
					if (access.includes("Submit")) {
						var edits = document.getElementsByName("done");
						
						for (var i = 0; i < edits.length; i++) {
							var edit = edits[i];
							edit.disabled = false;
						}
						
					}
					
					if (access.includes("Approve")) {
						
						var edits1 = document.getElementsByName("Approve");
						
						for (var i = 0; i < edits1.length; i++) {
							var edit1 = edits1[i];
							edit1.disabled = false;
						}
					}
					
				}
			}, 500);
		}
		$('#txnResultDataTable').on('draw.dt', function () {
			enableBaseOnAccess();
		});
		enableBaseOnAccess();
		
	</script>
</body>

</html>