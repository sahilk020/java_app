<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="/struts-tags" prefix="s"%>
<html dir="ltr" lang="en-US">

<head>
<title>WebStore Details</title>


<link rel="shortcut icon" href="assets/media/images/paylogo.svg" />
<link rel="stylesheet"
	href="https://fonts.googleapis.com/css?family=Inter:300,400,500,600,700" />

<link href="../assets/plugins/custom/datatables/datatables.bundle.css"
	rel="stylesheet" type="text/css" />
<link href="../assets/plugins/global/plugins.bundle.css"
	rel="stylesheet" type="text/css" />
<link href="../assets/css/style.bundle.css" rel="stylesheet"
	type="text/css" />
<link href="../assets/css/custom/custom-style.css" rel="stylesheet"
	type="text/css" />
<script src="../js/jquery.min.js" type="text/javascript"></script>


<style>
.bold {
	font-weight: bolder !important;
	color: black !important;
}

div#datatable_wrapper {
	margin-top: 12px;
}

td.dataTables_empty {
	display: none !important;
}

button.btn.btn-success {
	margin-right: 20px !important;
}

button.btn.btn-secondary {
	background-color: #dfd2c8 !important;
}

#statusResponse {
	text-align: center;
	font-weight: bolder;
	font-size: 16px;
}
</style>
</head>

<body>
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
						WebStore List</h1>
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
						<li class="breadcrumb-item text-muted">WebStore</li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item"><span
							class="bullet bg-gray-200 w-5px h-2px"></span></li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item text-dark">WebStore List</li>
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
				<div class="card">
					<div class="card-body">

						<%-- <s:form action="" id="formname">
							<s:token /> --%>
						<div class="row g-12 mb-8">
							<div class="col-md-3 fv-row">
								<!--begin::Label-->
								<label class="d-flex align-items-center fs-6 fw-semibold mb-2">
									<span class="">Merchant</span>
								</label>
								<div class="col-auto my-2">
								<s:if test="%{#session.USER.UserGroup.group =='Merchant'}">
									<s:select name="payId" id="payId" headerKey=""
										headerValue="Select Merchant"
										class="form-select form-select-solid merchantPayId"
										listKey="uuId" listValue="businessName" list="merchants"
										data-control="select2" onchange="getUserId(this.value)" />
										</s:if>
										<s:else>
									<s:select name="payId" id="payId" headerKey=""
										headerValue="Select Merchant"
										class="form-select form-select-solid merchantPayId"
										listKey="uuId" listValue="businessName" list="merchants"
										data-control="select2" onchange="getUserId(this.value)" />
										</s:else>
								</div>

							</div>

							<div class="col-md-3 fv-row">
								<!--begin::Label-->
								<label class="d-flex align-items-center fs-6 fw-semibold mb-2">
									<span class="">User Id</span>
								</label>
								<div class="col-auto my-2" id="UserIdSelect">
									<s:select name="userid" id="u_id" headerKey=""
										headerValue="Select UserId"
										class="form-select form-select-solid merchantPayId"
										listKey="uuId" listValue="name" list="merchants"
										data-control="select2" />
								</div>

							</div>
							<div class="col-md-3">
								<button class="btn btn-primary" onclick="view()"
									style="margin-top: 25px;">View</button>
							</div>
						</div>

						<%-- 	<div class="col-md-3"
							style="margin-left: 750px; margin-top: -35px;">
							<button type="button" id="search_payment_submit"
								onclick="download()" class="btn btn-primary">
								<span class="indicator-label">DOWNLOAD</span> <span
									class="indicator-progress">Please wait... <span
									class="spinner-border spinner-border-sm align-middle ms-2"></span>
							</button>
						</div> --%>
						<%-- </s:form> --%>
					</div>

				</div>
				<div class="row my-5">
					<div class="col">
						<div class="card">
							<div class="card-body">
								<div class="row my-3 align-items-center">


									<div class="col-lg-12 fv-row">

										<table
											class="table table-striped table-row-bordered gy-5 gs-7"
											id="datatable">
											<thead>
												<tr>

													<th class="bold">Id</th>
													<th class="bold">Name</th>
													<th class="bold">Email</th>
													<th class="bold">mobile</th>
													<th class="bold">address</th>
													<th class="bold">state</th>
													<th class="bold">city</th>
													<th class="bold">amount</th>
													<th class="bold">status</th>
													<th class="bold">order_id</th>
													<th class="bold">created_at</th>
													<th class="bold">updated_at</th>
													
												</tr>
											</thead>
											<tbody>

											</tbody>
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
	

	<script
		src="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.js"></script>
	<script src="../assets/plugins/global/plugins.bundle.js"></script>
	<script src="../assets/js/scripts.bundle.js"></script>
	<script src="../assets/plugins/custom/datatables/datatables.bundle.js"></script>
	<script>
		function getUserId(uuid) {
			
			var urls = new URL(window.location.href);
			var domain = urls.origin;
			const arrayData = [];
			var data = {
				uuid : uuid
			}
			$
					.ajax({

						type : "POST",
						data : JSON.stringify(data),
						contentType : "application/json",
						url : domain + "/crmws/webStoreApi?type=FETCH_USER",
						success : function(response) {
							debugger
                              console.log(response.response)
                            
							var s = '<option value="">Select User Id</option>';
							var res = JSON.parse(response.response);
							for (var i = 0; i < res.length; i++) {
                                  if(res[i].role=='merchant'){
                                      continue;
                                  }
								s += '<option value="' + res[i].id + '">'
										+ res[i].name + '</option>';
							}

							document.getElementById("UserIdSelect").style.display = "block";

							$("#u_id").html(s);

						}

					});
		}
		
		function view() {
      
			var urls = new URL(window.location.href);
			var domain = urls.origin;
			const arrayData = [];
			var data = {
				uuid : $('#payId').val(),
				user_id : $('#u_id').val(),

			}
			
			$.ajax({

				type : "POST",
				data : JSON.stringify(data),
				contentType : "application/json",
				url : domain + "/crmws/webStoreApi?type=ORDER",
				success : function(response) {
					var res = JSON.parse(response.response);
					const map = new Map(Object.entries(res));
					map.forEach(function(value, key) {
					
						arrayData.push(value);
					})

					renderTable(arrayData);

				}

			});
		}
	
		function renderTable(data) {

			var buttonCommon = {
				exportOptions : {
					format : {

					}
				}
			};

			$("#datatable").DataTable().destroy();
			$('#datatable').dataTable({
				'data' : data,

				dom : 'BTrftlpi',

				"scrollY" : true,
				"scrollX" : true,
				'columns' : [


				{
					"data" : "id",

				},

				{
					'data' : 'name'
				},

				{
					'data' : 'email'
				}, {
					'data' : 'mobile'
				},

				{
					'data' : 'address'
				}, {
					'data' : 'state'
				},  {
					'data' : 'city'
				},{
					'data' : 'amount'
				}, {
					'data' : 'status'
				}, {
					'data' : 'order_id'
				}, {
					'data' : 'created_at'
				}, {
					'data' : 'updated_at'
				} ]
			});
		}
	</script>
	<style>
.dt-buttons {
	display: none;
}
</style>
</body>

</html>