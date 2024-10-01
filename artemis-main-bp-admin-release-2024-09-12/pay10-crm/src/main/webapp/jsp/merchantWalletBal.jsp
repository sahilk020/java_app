<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="/struts-tags" prefix="s"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.Locale"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.text.Format"%>

<html dir="ltr" lang="en-US">

<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Merchant Wallet Balance</title>
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
<link href="../css/select2.min.css" rel="stylesheet" />
<script src="../js/jquery.select2.js" type="text/javascript"></script>

<style type="text/css">
.dt-buttons {
	margin-top: 35px !important;
}

.svg-icon {
	margin-top: 1vh !important;
}
</style>
<style>
.dt-buttons {
	display: none;
}
</style>


<script type="text/javascript">
	var value = $("#colorPattern td").text();
	if (value < 0) {
		$("#colorPattern td").addClass("red");
	}
</script>

<style>
@media ( min-width : 992px) {
	.col-lg-3 {
		max-width: 30% !important;
	}
}
</style>
</head>

<body>
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
				<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">Merchant Wallet Balance</h1>
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
					<li class="breadcrumb-item text-muted">Payout</li>
					<!--end::Item-->
					<!--begin::Item-->
					<li class="breadcrumb-item"><span
						class="bullet bg-gray-200 w-5px h-2px"></span></li>
					<!--end::Item-->
					<!--begin::Item-->
					<li class="breadcrumb-item text-dark">Merchant Wallet Balance</li>
					<!--end::Item-->
				</ul>
				<!--end::Breadcrumb-->
			</div>
			<!--end::Page title-->

		</div>
		<!--end::Container-->
	</div>

	<div style="overflow: auto !important;">
		<div id="kt_content_container" class="container-xxl">
			<div class="row my-5">
				<div class="col">
					<div class="card">
						<div class="card-body">
							<!--begin::Input group-->
							<div class="row g-9 mb-8">
								<div class="row g-9 mb-8 justify-content-end">
									<div class="row g-9 mb-8">
										<div>
											<table id="example"
												class="table table-striped table-row-bordered gy-5 gs-7 "
												style="width: 100%">
												<thead>
													<tr class="boxheadingsmall fw-bold fs-6 text-gray-800">
														<th class="col">PayId</th>
														<th scope="col">Merchant Name</th>
														<th scope="col">Final Balance</th>
													</tr>

												</thead>
												<tfoot>
													<tr class="fw-bold fs-6 text-gray-800">
														<th class="min-w-90px"></th>
														<th class="min-w-90px"></th>
														<th class="min-w-90px"></th>
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
			</div>
		</div>

	</div>

<script type="text/javascript">

$(document).ready(function () {
  $(function () {
    renderTable();
  });
});

function renderTable() {
    $("#example").DataTable().destroy();
    var token = document.getElementsByName("token")[0].value;
    var buttonCommon = {
      exportOptions: {
        format: {
          body: function (data, column, row, node) {
            return data;
          }
        }
      }
    };
    $('#example').DataTable({
      dom: 'BTftlpi',
      'columnDefs': [{
        'searchable': false
      }],

      "ajax": {
        "url": "txnReport",
        "type": "POST",
        "data": generatePostData(),
      },
      "bProcessing": true,
      "bLengthChange": true,
      "bAutoWidth": false,
      "iDisplayLength": 10,
      "order": [[0, "asc"]],
      "aoColumns": [
        {
          "mData": "payId"
        },
        {
          "mData": "merchantName"
        },
        {
          "mData": "walletBalance"
        }
      
        ]
    });
  }

function generatePostData() {
	var obj = {}
	return obj;
}

</script>
	</body>

</html>