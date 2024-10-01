<%@page import="java.util.List"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="/struts-tags" prefix="s"%>
<html dir="ltr" lang="en-US">

<head>
<title>Application</title>

<link rel="shortcut icon" href="assets/media/images/paylogo.svg" />
<link rel="stylesheet"
	href="https://fonts.googleapis.com/css?family=Inter:300,400,500,600,700" />
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
<script type="text/javascript">
	$(document).ready(function() {
     var arrayData = [];
     $.ajax({

   		type : "GET",
   		url : "allApplicationList",
   		success : function(response) {
          var res = response.aaData;
   		   arrayData = res;
         renderTable(arrayData);

           }
        });

	});
</script>
<script>
	function add(id,name,url) {
		$("#id").val(id);
		$("#name").val(name);
		$("#url").val(url);
		$('#updateApplication').submit();
	}
</script>
</head>

<body>
	<div class="content d-flex flex-column flex-column-fluid"
		id="kt_content">

		<div class="toolbar" id="kt_toolbar">
			<!--begin::Container-->
			<div id="kt_toolbar_container"
				class="container-fluid d-flex flex-stack">
				<!--begin::Page title-->
				<div data-kt-swapper="true" data-kt-swapper-mode="prepend"
					data-kt-swapper-parent="{default: '#kt_content_container', 'lg': '#kt_toolbar_container'}"
					class="page-title d-flex align-items-center me-3 flex-wrap mb-5 mb-lg-0 lh-1">
					<!--begin::Title-->
					<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">Application</h1>
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
						<li class="breadcrumb-item text-muted">Shield</li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item"><span
							class="bullet bg-gray-200 w-5px h-2px"></span></li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item text-dark">Application</li>
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
				<!--begin::Input group-->

					<div class="row g-12 mb-8">
						<s:form action="updateApplication" id="updateApplication">
						<s:hidden name="id" id="id" />
						<s:hidden name="name" id="name" />
						<s:hidden name="url" id="url" />
						</s:form>

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
													<th class="bold">ID</th>
													<th class="bold">Name</th>
													<th class="bold">URL</th>
													<th class="bold">Action</th>
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
		function renderTable(data) {

			var buttonCommon = {
				exportOptions : {
					format : {

					}
				}
			};

			$('#datatable')
					.dataTable(
							{
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
                                        'data' : 'url'
                                       },
										{
											"mData" : null,
											"sClass" : "center",
											"mRender" : function(data) {
												var classforEdit ="btn btn-sm btn-primary";
												return '<a data-toggle="tooltip" data-placement="top" data-original-title="Edit"  id="ignore" style="margin-top: 2px;margin-left: 2px;" class="'
														+ classforEdit
														+ '" onclick=\'getData('
														+  JSON.stringify(data)
														+ ')\'>Edit</a>';

											}
										} ]
							});
		}
	</script>
<script type="text/javascript">

function getData(data) {
	var id=data.id;
	var name=data.name;
	var url=data.url;
	console.log("id..."+id+"...name..."+name+"url...."+url)
	add(id,name,url);
}

</script>
	<style>
.dt-buttons {
	display: none;
}
</style>
</body>

</html>