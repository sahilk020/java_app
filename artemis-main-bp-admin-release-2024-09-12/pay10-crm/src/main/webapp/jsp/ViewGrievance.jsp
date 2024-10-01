<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="/struts-tags" prefix="s"%>
<html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta name="_csrf" content="your-csrf-token-value">
<meta name="_csrf_header" content="your-csrf-header-name">
<title>GRS View</title>
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
<link rel="stylesheet"
	href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0-beta3/css/all.min.css">

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
<script src="../js/stomp.min.js"></script>
<script src="../js/sockjs.min.js"></script>

<style type="text/css">
.btn-primary {
	background-color: #007bff;
	color: white;
	border: none;
	padding: 10px 15px;
	border-radius: 5px;
	cursor: pointer;
}

.btn-primary img {
	width: 20px;
	margin-right: 5px;
}

.svg-icon {
	margin-top: 1vh !important;
}

.dt-buttons.btn-group.flex-wrap {
	display: none;
}
</style>
<style>
ul li {
	font-size: 14px;
}

#header {
	/* background: linear-gradient(45deg, #fa3f47, #fdca43); */
	padding: 10px;
	display: flex;
	justify-content: space-between;
	align-items: center;
	/* ADDED */
	font-family: Arial, sans-serif;
	margin: 0;
	padding: 0;
}

#header img {
	width: 161px;
	height: 85px;
	margin-left: 36px;
}

#chatContainer {
	display: flex;
	flex-direction: column;
	margin: 20px;
}

#messages {
	flex: 1;
	background-color: #f1f1f1;
	padding: 10px;
	overflow-y: auto;
}

#messages li {
	display: flex;
	align-items: center;
	margin-bottom: 10px;
}

#messages .download-icon {
	margin-left: 10px;
	cursor: pointer;
}

#messageInput {
	padding: 5px;
	border: 2px solid #ccc;
	border-radius: 15px;
	margin: 10px 10px 10px 10px;
}

#fileUploadContainer, #uploadForm, #documentList {
	display: block;
	/* max-width: 1000px; */
	margin: 0 auto;
	padding: 10px;
	border-radius: 20px;
	border: 4px solid #f8a01d;
}

#fileUploadContainer h2, #uploadForm h2, #documentList h2 {
	margin-top: 0;
}

/* #fileInput, #fileInputAPI {
	margin-top: 10px;
} */

/* Additional styles for chat messages */
.my-class {
	margin: 5px 7px;
	/* background-color: #007bff; */
	color: #fff;
	padding: 5px 10px;
	border-radius: 10px;
	font-size: 12px;
}

.my-class1 {
	margin: 1px;
	background-color: #ccc;
	padding: 2px 5px;
	border-radius: 5px;
	font-size: 12px;
}

.chat-container {
	text-align: center;
	/* max-width: 1000px; */
	margin: 15px 15px 15px 15px;
	margin: 0 auto;
	border: 1px solid #ccc;
	padding: 10px;
	max-height: 750px;
	border: 1px solid #ccc;
	border-radius: 5px 0 0 5px;
}

.chat-box {
	display: flex;
	flex-direction: column;
	max-height: 410px;
	overflow-y: auto;
}

.message {
	display: inline-block;
	padding: 5px 10px;
	margin: 5px;
	border-radius: 5px;
	font-size: 12px;
	font-weight: 500;
}

.user-message {
	background-color: #efefef;
	align-self: flex-end;
	font-size: 12px;
	text-align: start;
	font-weight: bold;
}

.bot-message {
	background: linear-gradient(45deg, #fa3f47, #fdca43);
	align-self: center !important;
	font-size: 11px;
	font-weight: bold;
}

.admin-message {
	background-color: #d1ecf1;
	align-self: flex-start;
	text-align: start;
	font-weight: bold;
}

.input-container {
	display: flex;
	margin-top: 10px;
}

.input-container input {
	flex: 1;
	padding: 5px;
	border: 1px solid #ccc;
	border-radius: 5px 0 0 5px;
}

.input-container button {
	padding: 5px 10px;
	background-color: #007bff;
	border: none;
	color: white;
	border-radius: 0 5px 5px 0;
}

.b-colour {
	background: linear-gradient(45deg, #fa3f47, #fdca43);
	margin: 10px 10px 10px;
}

.message img.my-class {
	cursor: pointer;
	margin-left: 10px;
	width: 20px;
	/* Specify the width and height for the icon */
	height: 20px;
	border-radius: 50%;
	/* Make the icon circular */
	/* border: 1px solid #007bff; */
	/* Add a border around the icon */
	padding: 2px;
	/* Add padding to create some space between the border and the icon */
}

/* Add styles for the new class */
.message.my-class1 {
	display: flex;
	align-items: center;
}

.custom-file-button {
	display: inline-block;
	padding: 5px 12px;
	background-color: #4CAF50;
	color: white;
	cursor: pointer;
	border-radius: 4px;
}

/* Hide the default file input */
#fileInput, #fileInputAPI {
	display: none;
}

.div-class {
	margin-bottom: 20px;
	border-radius: 20px;
	border: 4px solid #f8a01d;
}

.room-div {
	margin: 10px 120px;
}

h2 {
	border: 4px solid #f8a01d;
	/* border-radius: 2px; */
	margin: -11px 0px 0px 0px;
	border-top: 0px;
	border: 0px 0px 0px 0px;
	border-right: 0px;
	border-left: 0px;
	padding: 25px;
	text-align: center;
}

.document-list-icon-class {
	padding: 3px 0px 0px 12px;
}

ul li {
	text-decoration: underline;
}

.message-timestamp-start {
	font-size: 9px;
	color: #007bff;
	padding: 8px 0px 0px 0px;
	text-align: start;
}

.message-timestamp-end {
	font-size: 9px;
	color: #007bff;
	padding: 8px 0px 0px 0px;
	text-align: end;
}

#documentList ul li {
	font-size: 16px;
}

#documentList ul li span img {
	height: 30px;
	width: 30px;
}

.message-text {
	font-size: 16px;
}

.textDecoration {
	text-decoration: none !important;
}

.sender {
	background-color: #efefef;
	align-self: flex-end;
	font-size: 12px;
	text-align: start;
}

#descriptionTextArea {
	width: 100%;
}

.d-flex>.btn {
	margin-right: 0.5rem;
}

#uploadButton {
	background-color: #007bff;
	color: #fff;
	border: none;
	border-radius: 5px;
	padding: 9px 9px;
	font-size: 16px;
	cursor: pointer;
	margin-top: 0.5rem;
}

#uploadButton i {
	margin-right: 5px;
	margin-left: 6px;
}
</style>
<script type="text/javascript">
				$(document).ready(function () {
					$(".adminMerchants").select2();
				});
			</script>
</head>

<body id="kt_body"
	class="header-fixed header-tablet-and-mobile-fixed toolbar-enabled toolbar-fixed toolbar-tablet-and-mobile-fixed aside-enabled aside-fixed"
	style="-kt-toolbar-height: 55px; -kt-toolbar-height-tablet-and-mobile: 55px">
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
					<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">GRS
						View</h1>
					<!--end::Title-->
					<!--begin::Separator-->
					<span class="h-20px border-gray-200 border-start mx-4"></span>
					<!--end::Separator-->
					<!--begin::Breadcrumb-->
					<ul
						class="breadcrumb breadcrumb-separatorless fw-semibold fs-7 my-1">
						<!--begin::Item-->
						<li class="breadcrumb-item text-muted textDecoration"><a
							href="home" class="text-muted text-hover-primary">Dashboard</a></li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item textDecoration"><span
							class="bullet bg-gray-200 w-5px h-2px"></span></li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item text-muted textDecoration">Grievance
							Redressal System</li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item textDecoration"><span
							class="bullet bg-gray-200 w-5px h-2px"></span></li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item text-dark textDecoration">GRS View</li>
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
				<div class="row my-5">
					<div class="col">
						<div class="card">
							<div class="card-body">

								<div class="row my-3 align-items-center">
									<div class="col-md-4 fv-row">
										<label class="d-flex align-items-center fs-6 fw-semibold mb-2">
											<span class="">Title</span>
										</label> <input class="form-control form-control-solid ajay"
											name="grs.grsTittle" id="grsTittle"
											value="<s:property value='%{grs.grsTittle}'/>" type="text">
									</div>
									<div class="col-md-4 fv-row">
										<label class="d-flex align-items-center fs-6 fw-semibold mb-2">
											<span class="">GRS ID</span>
										</label> <input class="form-control form-control-solid ajay"
											name="grs.GrsId" id="GrievanceId"
											value="<s:property value='%{grs.GrsId}'/>" type="text">
									</div>
									<div class="col-md-4 fv-row">
										<label class="d-flex align-items-center fs-6 fw-semibold mb-2">
											<span class="">Merchant Name</span>
										</label> <input class="form-control form-control-solid ajay"
											name="grs.merchantName" id="grs.merchantName"
											value="<s:property value='%{grs.merchantName}'/>" type="text">
									</div>
								</div>

								<div class="row my-3 align-items-center">
									<div class="col-md-4 fv-row">
										<label class="d-flex align-items-center fs-6 fw-semibold mb-2">
											<span class="">PG REF NUM</span>
										</label> <input class="form-control form-control-solid ajay"
											name="grs.PgrefNum" id="grs.PgrefNum"
											value="<s:property value='%{grs.PgrefNum}'/>" type="text">
									</div>
									<div class="col-md-4 fv-row">
										<label class="d-flex align-items-center fs-6 fw-semibold mb-2">
											<span class="">Transaction Amount</span>
										</label> <input class="form-control form-control-solid ajay"
											name="grs.amount" id="grs.amount"
											value="<s:property value='%{grs.amount}'/>" type="text">
									</div>
									<div class="col-md-4 fv-row">
										<label class="d-flex align-items-center fs-6 fw-semibold mb-2">
											<span class="">Order Id</span>
										</label> <input class="form-control form-control-solid ajay"
											name="grs.orderId" id="grs.orderId"
											value="<s:property value='%{grs.orderId}'/>" type="text">
									</div>
								</div>

								<div class="row my-3 align-items-center">
									<div class="col-md-4 fv-row">
										<label class="d-flex align-items-center fs-6 fw-semibold mb-2">
											<span class="">Date Of Transaction</span>
										</label> <input class="form-control form-control-solid ajay"
											name="grs.txnDate" id="grs.txnDate"
											value="<s:property value='%{grs.txnDate}'/>" type="text">
									</div>
									<div class="col-md-4 fv-row">
										<label class="d-flex align-items-center fs-6 fw-semibold mb-2">
											<span class="">Mode of Payment</span>
										</label> <input class="form-control form-control-solid ajay"
											name="grs.mopType" id="grs.mopType"
											value="<s:property value='%{grs.mopType}'/>" type="text">
									</div>
									<div class="col-md-4 fv-row">
										<label class="d-flex align-items-center fs-6 fw-semibold mb-2">
											<span class="">Customer Name</span>
										</label> <input class="form-control form-control-solid ajay"
											name="grs.customerName" id="grs.customerName"
											value="<s:property value='%{grs.customerName}'/>" type="text">
									</div>
								</div>

								<div class="row my-3 align-items-center">
									<div class="col-md-4 fv-row">
										<label class="d-flex align-items-center fs-6 fw-semibold mb-2">
											<span class="">Customer Phone</span>
										</label> <input class="form-control form-control-solid ajay"
											name="grs.customerPhone" id="grs.customerPhone"
											value="<s:property value='%{grs.customerPhone}'/>"
											type="text">
									</div>
								</div>
								<div class="row my-3 align-items-center">
									<div class="col-md-12 fv-row">
										<s:if
											test="%{#session.USER.UserType.name()=='ADMIN' || #session.USER.UserType.name()=='SUBADMIN' || #session.USER_TYPE.name()=='SUPERADMIN'|| #session.USER_TYPE.name()=='RESELLER'}">
											<button type="button" class="btn btn-primary"
												id="closeGrievance" onclick="closeGrievance()">Close</button>
										</s:if>
										<s:else>
											<button type="button" class="btn btn-primary"
												id="reOpenGrivance" onclick="reOpenGrivance()">Re
												Open</button>
										</s:else>
									</div>
								</div>
							</div>
						</div>
					</div>
					<!-- Issue History Section -->
					<div class="row my-5">
						<div class="card">
							<div class="card-body">
								<!-- Issue History Heading -->
								<div class="row my-3 align-items-center">
									<div class="col-md-12 fv-row">
										<h1 class="my-4">Issue History</h1>
									</div>
								</div>

								<!-- Description, Upload, Save, and Cancel -->
								<div class="row my-3 align-items-center">
									<div class="col-md-8 fv-row">
										<textarea class="form-control form-control-solid "
											id="descriptionTextArea" rows="5" placeholder="Add a comment"></textarea>
										<span id="documentError" class="error"></span>
									</div>
									<div class="col-md-4 d-flex flex-column align-items-start">
										<div class="d-flex">
											<button type="button" class="btn btn-primary me-2"
												id="saveDescription" onclick="saveDescription()">Save</button>
											<button type="button" class="btn btn-secondary"
												id="cancelDescription">Cancel</button>
										</div>
										<div class="d-flex align-items-center mb-2">
											<input type="file"
												class="form-control form-control-solid me-2"
												id="uploadFileNew" style="display: none;"
												accept=".jpeg, .jpg, .png, .pdf">
											<button type="button" id="uploadButton"
												class="btn btn-primary">
												<i class="fas fa-upload"></i>
											</button>
										</div>


									</div>
								</div>

								<div class="card-body">

									<div id="dataContainer"></div>
									<div id="paginationControls" class="my-3"></div>

								</div>


							</div>


						</div>
					</div>
				</div>
			</div>
		</div>


		<!-- <div class="post d-flex flex-column-fluid" id="kt_post">
			begin::Container
			<div id="kt_content_container" class="container-xxl">
				<div class="row my-5">
					<div class="col">
						<div class="card">
							<div class="card-body">
								begin::Input group

								<div class="row my-3 align-items-center">
									<div class="col-12">
										<div id="documentList">
											<h2>Document List</h2>
											<ul id="document-list"></ul>
										</div>



									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div> -->
		<!-- <div class="post d-flex flex-column-fluid" id="kt_post">
			begin::Container
			<div id="kt_content_container" class="container-xxl">
				<div class="row my-5">
					<div class="col">
						<div class="card">
							<div class="card-body">
								begin::Input group

								<div class="row my-3 align-items-center">
									<div class="col-12">

										<div id="chat-container" class="chat-container div-class mt-5">
											<div>
												<h2>Chat Support</h2>
												 Heading inside the chat box

												Chat messages and input fields can go here
											</div>

											<div class="chat-box" id="chatBox"></div>
											<div class="input-container">
												<input type="text" id="messageInput"
													placeholder="Type your message..." />
												<button id="sendButton" class="b-colour"
													onclick="sendMessage()">
													<i class="fas fa-paper-plane"></i> Send
												</button>
												<input type="file" id="fileInput" /> <label for="fileInput"
													class="custom-file-button b-colour"> <i
													class="fas fa-upload"></i> Upload file
											</div>
										</div>


									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div> -->



	</div>


	<script>
				document.getElementById('uploadButton').addEventListener('click', function () {
					document.getElementById('uploadFileNew').click();
				});
			</script>

	<script type="text/javascript">

				$(".ajay").prop('disabled', true);

				var status = "<s:property value='%{grs.status}'/>";


				var user = "<s:property value='%{#session.USER.UserType.name()}'/>";

				if (user != "MERCHANT") {

					if (status == "CLOSED") {
						$("#closeGrievance").hide();
					}

				}


				if (status == "CLOSED") {
					$("#reOpenGrivance").show();

				} else {
					$("#reOpenGrivance").hide();
				}

				function closeModal() {
					$("#myModal").hide();
				}
				$("#dateFrom").flatpickr({
					maxDate: new Date(),
					dateFormat: 'Y-m-d',
					defaultDate: "today"
				});
				$("#dateTo").flatpickr({
					maxDate: new Date(),
					dateFormat: 'Y-m-d',
					defaultDate: "today"
				});
				function convert(str) {
					var date = new Date(str), mnth = ("0" + (date.getMonth() + 1))
						.slice(-2), day = ("0" + date.getDate()).slice(-2);
					//return [date.getFullYear(), mnth, day].join("-");
					return [day, mnth, date.getFullYear()].join("-");
				}

				function reloadTable() {
					dateFrom = document.getElementById("dateFrom").value;
					dateTo = document.getElementById("dateTo").value;
					var transFrom = convert(dateFrom);
					var transTo = convert(dateTo);
					var transFrom1 = new Date(Date.parse(document
						.getElementById("dateFrom").value));
					var transTo1 = new Date(Date.parse(document
						.getElementById("dateTo").value));
					if (transFrom1 == null || transTo1 == null) {
						alert('Enter date value');
						return false;
					}

					if (transFrom1 > transTo1) {
						alert('From date must be before the to date');
						$('#loader-wrapper').hide();
						$('#dateFrom').focus();
						return false;
					}
					if (transTo1 - transFrom1 > 31 * 86400000) {
						alert('No. of days can not be more than 31');
						$('#loader-wrapper').hide();
						$('#dateFrom').focus();
						return false;
					} else {
						//dataTable();
						var tableObj = $('#txnResultDataTable');
						var table = tableObj.DataTable();
						table.ajax.reload();
					}
				}
				function generatePostData(d) {

					var token = document.getElementsByName("token")[0].value;
					var payId = document.getElementById("merchant").value;
					var status = document.getElementById("status").value;

					var dateFrom = document.getElementById("dateFrom").value;
					var dateTo = document.getElementById("dateTo").value;

					var obj = {
						merchant: payId,
						dateFrom: dateFrom,
						status: status,
						dateTo: dateTo,
						draw: d.draw,
						length: d.length,
						start: d.start,
						token: token,
						"struts.token.name": "token",
					};

					return obj;
				}


				$('#txnResultDataTable')

					.dataTable(

						{

							"columnDefs": [{
								className: "dt-body-right",
								"targets": [1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
									11, 12, 13, 14, 15, 16, 17, 18, 19]
							}],

							dom: 'Brtipl',
							buttons: [
								{
									extend: 'print',
									exportOptions: {
										columns: ':visible'
									}
								},
								{
									extend: 'pdfHtml5',
									orientation: 'landscape',
									pageSize: 'legal',
									//footer : true,
									title: 'Search Transactions',
									exportOptions: {
										columns: [':visible']
									},
									customize: function (doc) {
										doc.defaultStyle.alignment = 'center';
										doc.styles.tableHeader.alignment = 'center';
										doc.defaultStyle.fontSize = 8;
									}
								},
								{
									extend: 'copy',
									exportOptions: {
										columns: [0, 1, 2, 3, 4, 5, 6, 7,
											8, 9, 10, 11, 12, 13, 14,
											15, 16, 17, 18, 19, 20]
									}
								},
								{
									extend: 'csv',
									exportOptions: {
										columns: [0, 1, 2, 3, 4, 5, 6, 7,
											8, 9, 10, 11, 12, 13, 14,
											15, 16, 17, 18, 19]
									}
								},
								{
									extend: 'pdf',
									exportOptions: {
										columns: [0, 1, 2, 3, 4, 5, 6, 7,
											8, 9, 10, 11, 12, 13, 14,
											15, 16, 17, 18, 19, 20, 21,
											22]
									}
								}, 'colvis', 'excel', 'print',],
							scrollY: true,
							scrollX: true,
							searchDelay: 500,
							processing: false,
							destroy: true,
							serverSide: true,
							order: [[5, 'desc']],
							stateSave: true,

							"ajax": {

								"url": "GRSViewList",
								"type": "POST",
								"timeout": 0,
								"data": function (d) {
									return generatePostData(d);
								}
							},
							"fnDrawCallback": function () {
								$("#submit").removeAttr("disabled");
								$('#loader-wrapper').hide();
							},
							"searching": false,
							"ordering": false,
							"processing": true,
							"serverSide": true,
							"paginationType": "full_numbers",
							"lengthMenu": [[10, 25, 50, 100],
							[10, 25, 50, 100]],
							"order": [[2, "desc"]],

							"columnDefs": [{
								"type": "html-num-fmt",
								"targets": 4,
								"orderable": true,
								"targets": [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10,
									11, 12, 13, 14, 15, 16, 17, 18]
							}],

							"columns": [

								{
									"data": "pgrefNum",
									"className": "txnId my_class1 text-class",
									"width": "2%"
								},
								{
									"data": "grsId",
									"className": "payId text-class"

								},
								{
									"data": "merchantName",
									"className": "text-class"
								},
								{
									"data": "grsTittle",
									"className": "text-class"
								},
								{
									"data": "amount",
									"className": "orderId text-class"
								},
								{
									"data": "totalAmount",
									"className": "orderId text-class"
								},
								{
									"data": "status",
									"className": "mopType text-class"
								},
								{
									"data": "orderId",
									"className": "text-class"

								},
								{
									"data": "createdDate",
									"className": "text-class"
								},
								{
									"data": "createdBy",
									"className": "text-class"
								},
								{
									"data": "txnDate",
									"className": "text-class"
								},
								{
									"data": "paymentMethod",
									"className": "text-class"
								},
								{
									"data": "mopType",
									"className": "text-class"
								},
								{
									"data": "payId",
									"className": "text-class"
								},
								{
									"data": "customerName",
									"className": "text-class"
								},
								{
									"data": "customerPhone",
									"className": "text-class"
								},
								{
									"data": "updatedBy",
									"className": "text-class"
								},
								{
									"data": "updatedAt",
									"className": "text-class"
								},
								{
									"data": null,
									'render': function (data, type, full,
										meta) {
										return '<i class="fa" style="color: red;cursor:grab; font-size: 30px;" onclick="openGrs('
											+ data.grsId
											+ ')">&#xf044;</i>';
									}
								}]
						});
			</script>
	<script>

				var stompClient;
				var username;
				var roomId;
				var usertype;
				//var baseUrl='/';
				var baseUrl = 'https://chat.pay10.com/';
				function connectToChat() {
					roomId = document.getElementById('GrievanceId').value;
					const csrfToken = document.querySelector("meta[name='_csrf']").getAttribute("content");
					const csrfHeader = document.querySelector("meta[name='_csrf_header']").getAttribute("content");

					const headers = {};
					headers[csrfHeader] = csrfToken;
					var socket = new SockJS(baseUrl + 'chat', null, { headers: headers });

					stompClient = Stomp.over(socket);
					stompClient
						.connect(
							{},
							function (frame) {
								username = "<s:property value='%{#session.USER.emailId}'/>";
								usertype = "<s:property value='%{#session.USER.UserGroup.group}'/>";
								stompClient.send("/app/chat/" + roomId
									+ "/addUser", {}, JSON.stringify({
										senderType: usertype,
										sender: username,
										content: username
											+ " has joined the room."

									}));


								getDocumentByRoomId();
								getChatHistoryByRoomId();
								const userDestination = '/user/private-messages';
								stompClient.subscribe(userDestination, function (message) {
									var message = JSON
										.parse(response.body);
									if (message.chatType == 'Document') {
										showMessageDocument(message.sender, message.content, message.fileUrl, message.senderType, message.timestamp);

									} else {
										showMessage(message.sender, message.content, message.senderType, message.timestamp);
									}
								});


								stompClient.subscribe('/topic/' + roomId,
									function (response) {
										var message = JSON
											.parse(response.body);
										if (message.chatType == 'Document') {
											showMessageDocument(message.sender, message.content, message.fileUrl, message.senderType, message.timestamp);

										} else {
											showMessage(message.sender, message.content, message.senderType, message.timestamp);
										}


									});


								// Add the following code to receive chat history
								stompClient
									.subscribe(
										'/user/queue/history',
										function (response) {
											var chatHistory = JSON
												.parse(response.body);
											for (var i = 0; i < chatHistory.length; i++) {
												showMessage(chatHistory[i].sender
													, chatHistory[i].content, chatHistory[i].senderType);
											}
										});
								stompClient.subscribe('/topic/' + roomId + '/documents', function (response) {
									var document = JSON.parse(response.body);
									showDocumentUploadMessage(document.filename);
								});
								document.getElementById('documentList').style.display = 'block';
								document.getElementById('chat-container').style.display = 'block';


							});
				}

				function sendMessage() {
					var user = "<s:property value='%{#session.USER.UserType.name()}'/>";
					if (user != "MERCHANT") {
						if (status == "NEW" || status == "REOPENED") {
							$("#reOpenGrivance").show();



							var grdID = $("#GrievanceId").val();
							var userEmailId = "<s:property value='%{#session.USER.emailId}'/>";
							var obj = {
								"grievanceRedressalSystemId": grdID,
								"userEmailId": userEmailId
							};
							var urls = new URL(window.location.href);
							var domain = urls.origin;
							$.ajax({
								url: domain + "/crmws/GRS/inProcess",
								//url:"http://localhost:8080/GRS/closeGrievance",
								type: 'POST',
								data: JSON.stringify(obj),
								contentType: "application/json",
								success: function (data) {
									console.log(data.respmessage);

								},
								error: function (data, textStatus, jqXHR) {
									console.log(data.respmessage);

								}
							});

						}
					}
					var messageInput = document.getElementById('messageInput');
					var message = messageInput.value.trim();
					if (message && stompClient) {
						stompClient.send("/app/chat/"
							+ roomId
							+ "/sendMessage", {}, JSON.stringify({
								senderType: usertype,
								sender: username,
								content: message
							}));
						messageInput.value = '';
					}
				}

				function sendCloseReopenMsgToAll(type) {
					if (type && stompClient) {
						const message = "This Conversation is " + type + ".";
						stompClient.send("/app/chat/"
							+ roomId
							+ "/sendMessage", {}, JSON.stringify({
								senderType: "BOT",
								sender: username,
								content: message
							}));
						messageInput.value = '';
					}
				}

				function showMessageDocument(senderType, message, fileurl, sender, timeStampB) {
					const timestamp = formatDateTime(timeStampB);
					const chatBox = document.getElementById('chatBox');
					const userInput = document.getElementById('userInput');
					const sendButton = document.getElementById('sendButton');
					// Create a div for the timestamp
					const timestampElement = document.createElement('div');
					timestampElement.textContent = timestamp;
					const messageSenderContainer = document.createElement('div');
					messageSenderContainer.textContent = senderType + ":";
					messageSenderContainer.classList.add('message');
					const messageContainer = document.createElement('div');

					messageContainer.textContent = message;
					messageContainer.classList.add('message');
					messageSenderContainer.appendChild(messageContainer);
					let img = new Image(20, 20);
					img.src = '../image/cloud-arrow-down-solid.svg';
					img.addEventListener("click", function () {
						downloadfile(fileurl);
					})
					img.classList.add("my-class");
					messageSenderContainer.appendChild(img);
					messageSenderContainer.classList.add("my-class1");
					if (sender === usertype) {
						timestampElement.classList.add('message-timestamp-end');
						messageSenderContainer.classList.add('user-message');
						messageSenderContainer.appendChild(timestampElement);
					} else if (sender === 'BOT') {
						messageSenderContainer.classList.add('bot-message');
					}
					else {
						timestampElement.classList.add('message-timestamp-start');
						messageSenderContainer.classList.add('admin-message');
						messageSenderContainer.appendChild(timestampElement);
					}

					chatBox.appendChild(messageSenderContainer);
					chatBox.scrollTop = chatBox.scrollHeight;
				}




				function showMessage(senderType, message, sender, timeStampB) {
					const chatBox = document.getElementById('chatBox');
					const userInput = document.getElementById('userInput');
					const sendButton = document.getElementById('sendButton');
					const timestamp = formatDateTime(timeStampB);// Get the current timestamp

					// Create a div for the timestamp
					const timestampElement = document.createElement('div');
					timestampElement.textContent = timestamp;

					const messageSenderContainer = document.createElement('div');
					messageSenderContainer.textContent = senderType + ":";
					messageSenderContainer.classList.add('message');

					const messageContainer = document.createElement('div');
					messageContainer.textContent = message;
					//   messageContainer.classList.add('message');
					messageContainer.classList.add('message');
					messageSenderContainer.appendChild(messageContainer);

					if (sender === usertype) {

						timestampElement.classList.add('message-timestamp-end');
						messageSenderContainer.classList.add('user-message');

						messageSenderContainer.appendChild(timestampElement);
					} else if (sender === 'BOT') {
						messageSenderContainer.classList.add('bot-message');
					}
					else {
						timestampElement.classList.add('message-timestamp-start');
						messageSenderContainer.classList.add('admin-message');
						messageSenderContainer.appendChild(timestampElement);

					}
					chatBox.appendChild(messageSenderContainer);

					chatBox.scrollTop = chatBox.scrollHeight;
				}
				function uploadDocument() {
					var fileInput = document.getElementById('fileInput');
					var file = fileInput.files[0];

					if (file && stompClient) {
						var reader = new FileReader();
						reader.onload = function (e) {
							var documentData = e.target.result;
							stompClient.send("/app/chat/" + roomId + "/uploadDocument", {}, JSON.stringify({
								senderType: usertype,
								sender: username,
								filename: file.name,
								documentData: documentData
							}));
							fileInput.value = '';
						};

						reader.readAsBinaryString(file);
					}
				}

				// fileInput.addEventListener('change', uploadFile);

				function uploadFile() {
					var file = fileInput.files[0];
					if (file && stompClient) {
						var reader = new FileReader();
						reader.onload = function (e) {
							var fileData = e.target.result;
							var filename = file.name;
							var contentType = file.type;

							// Convert the fileData to a base64-encoded string
							var base64Data = btoa(fileData);

							// Send the base64-encoded data along with filename and contentType
							stompClient.send("/app/uploadFile/" + roomId + "/uploadDocument", {}, JSON.stringify({
								filename: filename,
								contentType: contentType,
								senderType: usertype,
								sender: username,
								data: base64Data
							}));
							fileInput.value = '';
						};
						reader.readAsBinaryString(file);
					}
				}

				function downloadfile(filePath) {
					// Define the API endpoint URL
					const apiUrl = baseUrl + 'api/document/download/';

					// Send the AJAX request
					$.ajax({
						url: apiUrl,
						method: 'POST', // or 'GET' depending on your API
						contentType: 'application/json',
						data: JSON.stringify({ filePath: filePath }),
						xhrFields: {
							responseType: 'blob',
						},
						success: function (blob) {
							// Create a temporary link element to trigger the download
							const url = URL.createObjectURL(blob);
							const a = document.createElement('a');
							a.href = url;
							a.download = filePath.substring(filePath.lastIndexOf('/') + 1);
							document.body.appendChild(a);
							a.click();
							URL.revokeObjectURL(url);
						},
						error: function (error) {
							console.error('Error while downloading:', error);
						},
					});
				}

				function getDocumentByRoomId() {
					setInterval(function () {
						$.ajax({
							url: baseUrl + 'api/document/' + roomId + '/history',
							method: 'GET',
							dataType: 'json',
							success: function (data) {
								const documentList = $('#document-list');
								documentList.empty(); // Clear the list before populating

								data.forEach(doc => {
									const listItem = $('<li>').text(doc.filename);
									const downloadIcon = $('<span>').html('<img width="20" height="20" src="../image/cloud-arrow-down-solid.svg" class="document-list-icon-class">');
									downloadIcon.click(() => {
										downloadfile(doc.fileUrl);
									});
									listItem.append(downloadIcon);
									documentList.append(listItem);
								});
							},
							error: function (error) {
								console.error('Error fetching documents:', error);
							}
						});
					}, 1000);
				}
				function getChatHistoryByRoomId() {
					$
						.ajax({
							url: baseUrl + 'api/chat/'
								+ roomId
								+ '/history', // Replace {roomId} with the actual room ID
							method: 'GET',
							success: function (
								responseHistory) {
								// Handle the successful response
								console
									.log(responseHistory); // Assuming the response is an array of chat messages
								// Render the chat history on the UI as needed
								/* var chatHistory = JSON
										.parse(responseHistory); */
								for (var i = 0; i < responseHistory.length; i++) {
									if (responseHistory[i].chatType == 'Document') {
										showMessageDocument(responseHistory[i].sender, responseHistory[i].content, responseHistory[i].fileUrl, responseHistory[i].senderType, responseHistory[i].timestamp);
									} else {
										showMessage(responseHistory[i].sender

											, responseHistory[i].content, responseHistory[i].senderType, responseHistory[i].timestamp);

									}


								}
							},
							error: function (
								error) {
								// Handle the error
								console
									.log(error);
							}
						});

				}
				function uploadFileAPI() {
					/*  var formData = new FormData();
					 formData.append('fileAPI', $('#fileInputAPI')[0].files[0]);
		  */
					var file = fileInputAPI.files[0];
					var reader = new FileReader();
					reader.onload = function (e) {
						var fileData = e.target.result;
						var filename = file.name;
						var contentType = file.type;

						// Convert the fileData to a base64-encoded string
						var base64Data = btoa(fileData);

						// Send the base64-encoded data along with filename and contentType

						$.ajax({
							url: baseUrl + 'api/document/upload/', 
							type: 'POST',
							contentType: 'application/json',
							data: JSON.stringify({
								filename: filename,
								contentType: contentType,
								senderType: usertype,
								sender: username,
								data: base64Data,
								roomId: roomId
							}),
							success: function (response) {
								alert("File Uploaded Successfully")
							},
							error: function (error) {
								alert("File  Not Uploaded Successfully : " + error.responseText);
							}
						});
						fileInputAPI.value = '';
					};
					reader.readAsBinaryString(file);

				}
				function formatDateTime(dateTimeString) {
					const date = new Date(dateTimeString);
					const year = date.getFullYear();
					const month = String(date.getMonth() + 1).padStart(2, '0');
					const day = String(date.getDate()).padStart(2, '0');
					const hours = String(date.getHours()).padStart(2, '0');
					const minutes = String(date.getMinutes()).padStart(2, '0');
					const seconds = String(date.getSeconds()).padStart(2, '0');
					const ampm = hours >= 12 ? 'PM' : 'AM';
					const formattedDateTime = day + '-' + month + '-' + year + ' ' + hours + ':' + minutes + ':' + seconds + ' ' + ampm;
					return formattedDateTime;
				}
				connectToChat();
			</script>
	<script type="text/javascript">
				function createGRS() {
					$("#myModal").show();

					const timestamp = new Date().getTime().toString();

					// Generate a random number between 100000 and 999999 (6-digit number)
					const randomNum = Math.floor(Math.random() * 900000) + 100000;

					// Concatenate the timestamp and random number to get an 18-digit unique number
					const uniqueNumber = timestamp + randomNum;

					$("#grsId").val(uniqueNumber);
					$("#gd").val("");
					$("#documentError").text("");
					$("#grsTittle").val("");
				}
				function closeGrievance() {

					var grdID = $("#GrievanceId").val();
					var userEmailId = "<s:property value='%{#session.USER.emailId}'/>";
					var obj = {
						"grievanceRedressalSystemId": grdID,
						"userEmailId": userEmailId
					};
					var urls = new URL(window.location.href);
					var domain = urls.origin;
					$.ajax({
						url: domain + "/crmws/GRS/closeGrievance",
						//url:"http://localhost:8080/GRS/closeGrievance",
						type: 'POST',
						data: JSON.stringify(obj),
						contentType: "application/json",
						success: function (data) {
							alert(data.respmessage);
							location.reload();

						},
						error: function (data, textStatus, jqXHR) {
							alert(data.respmessage);

						}
					});
				}

				function reOpenGrivance() {

					var grdID = $("#GrievanceId").val();
					var userEmailId = "<s:property value='%{#session.USER.emailId}'/>";
					var obj = {
						"grievanceRedressalSystemId": grdID,
						"userEmailId": userEmailId
					};
					var urls = new URL(window.location.href);
					var domain = urls.origin;
					$.ajax({
						url: domain + "/crmws/GRS/reOpenGrievance",
						//url:"http://localhost:8080/GRS/reOpenGrievance",
						type: 'POST',
						data: JSON.stringify(obj),
						contentType: "application/json",
						success: function (data) {
							alert(data.respmessage);
							location.reload();

						},
						error: function (data, textStatus, jqXHR) {
							alert(data.respmessage);

						}
					});
				}
				function submitGRS() {
					var grsDesc = $("#gd").val();
					var grsTittle = $("#grsTittle").val();
					var grdID = $("#grsId").val();

					if (grsDesc == "" || grsDesc == null || grsDesc == undefined) {
						$("#documentError").text("Grievance Description is Mandatory");
						return false;
					}

					if (grsTittle == "" || grsTittle == null || grsTittle == undefined) {
						$("#documentError").text("Grievance Tittle is Mandatory");
						return false;
					}

					var userEmailId = "<s:property value='%{#session.USER.emailId}'/>";
					var obj = {
						"grievanceRedressalSystemDescription": grsDesc,
						"grievanceRedressalSystemTittle": grsTittle,
						"grievanceRedressalSystemId": grdID,
						"userEmailId": userEmailId
					};

					var urls = new URL(window.location.href);
					var domain = urls.origin;
					$.ajax({
						url: domain + "/crmws/GRS/SaveGrievanceOther",
						//url:"http://localhost:8080/GRS/SaveGrievanceOther",
						type: 'POST',
						data: JSON.stringify(obj),
						contentType: "application/json",
						success: function (data) {
							$("#documentError").text(data.respmessage);

						},
						error: function (data, textStatus, jqXHR) {
							$("#documentError").text(data.respmessage);

						}
					});

				}
				function openGrs(grsId) {
					var url = new URL(window.location.href).origin
						+ "/crm-merchant/jsp/ViewGRS?grsId=" + grsId;

					window.open(url);

				}
			</script>
	<div class="modal" tabindex="-1" role="dialog" id="myModal"
		style="top: 30vh; left: 5%;">
		<div class="modal-dialog modal-lg" role="document"
			style="box-shadow: 2px 2px 2px 2px #f9b562 !important;">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title">Create Grievance</h5>
					<button type="button" class="close" data-dismiss="modal"
						aria-label="Close" onclick="closeModal()">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<div class="modal-body">
					<label class="d-flex align-items-center fs-6 fw-semibold mb-2">
						<span class="">Title</span>
					</label> <input type="text" id="grsTittle" name="grsTittle"
						style="min-width: 100%;"> <label
						class="d-flex align-items-center fs-6 fw-semibold mb-2"
						style="margin-top: 10px;"> <span class="">Grievance
							Description</span>
					</label>
					<textarea name="gd" id="gd"
						style="min-width: 100%; height: 80px !important;" maxlength="500"></textarea>

					<span>GRS ID : </span><input type="text" id="grsId" name="grsId"
						style="width: 87%; text-align: center;" disabled> <span
						id="documentError" class="error"></span>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-primary" onclick="submitGRS()">Submit</button>
					<button type="button" class="btn btn-secondary"
						data-dismiss="modal" onclick="closeModal()">Close</button>
				</div>
			</div>
		</div>


	</div>

	<script type="text/javascript">
				$(document).ready(function () {
					const itemsPerPage = 5; 
					let currentPage = 1;
					let data = []; 

					function fetchData() {
						var grdID = $("#GrievanceId").val();
						var urls = new URL(window.location.href);
						var domain = urls.origin;

						$.ajax({
							url: domain + "/crmws/GRS/getDescHistory?GRS_ID=" + grdID,
							method: 'GET',
							dataType: 'json',
							success: function (response) {
								data = response;
								renderPage(currentPage);
								setupPagination();
							},
							error: function (xhr, status, error) {
								console.error('Error fetching data:', error);
							}
						});
					}
				
					function downloadBase64File(base64String, fileName) {
				
						if (!base64String) {
							console.error('Base64 string is empty or undefined.');
							return;
						}

				
						if (!fileName) {
							console.error('Filename is empty or undefined.');
							return;
						}

			
						const fileExtension = fileName.split('.').pop().toLowerCase();
						const mimeType = getMimeType(fileExtension);

				
						const link = document.createElement('a');
						link.href = 'data:' + mimeType + ';base64,' + base64String;
						link.download = fileName;

						
						document.body.appendChild(link);

						
						link.click();

				
						document.body.removeChild(link);
					}

					function getMimeType(extension) {
					
						const mimeTypes = {
							'txt': 'text/plain',
							'pdf': 'application/pdf',
							'png': 'image/png',
							'jpg': 'image/jpeg',
							'jpeg': 'image/jpeg',
							'gif': 'image/gif',
							'html': 'text/html',
							'css': 'text/css',
							'js': 'application/javascript',
							'csv': 'text/csv',
							'xml': 'application/xml',
							'zip': 'application/zip',
							'mp4': 'video/mp4',
							'webm': 'video/webm',
							'mp3': 'audio/mpeg',
							'wav': 'audio/wav',
							'ico': 'image/x-icon'
					
						};

					
						return mimeTypes[extension] || 'application/octet-stream';
					}
					function createCard(item) {
				
						
						const cardDiv = document.createElement('div');
						cardDiv.className = 'card my-3';

						const cardBodyDiv = document.createElement('div');
						cardBodyDiv.className = 'card-body';

						const rowDiv = document.createElement('div');
						rowDiv.className = 'row';

						const colMd8Div = document.createElement('div');
						colMd8Div.className = 'col-md-8';

						const heading = document.createElement('h6');
						heading.className = 'd-flex align-items-center fs-6 fw-semibold mb-2';
						heading.textContent = item.createdBy;

						colMd8Div.appendChild(heading);
						rowDiv.appendChild(colMd8Div);

						const colMd2Div = document.createElement('div');
						colMd2Div.className = 'col-md-2';

						const datePara = document.createElement('p');
						datePara.textContent = item.createDate;

						colMd2Div.appendChild(datePara);
						rowDiv.appendChild(colMd2Div);

						cardBodyDiv.appendChild(rowDiv);

						const descriptionPara = document.createElement('p');
						descriptionPara.textContent = item.description;

						cardBodyDiv.appendChild(descriptionPara);
						if (item.filename != null && item.filename != "") {
						
							const downloadButton = document.createElement('button');
							downloadButton.className = 'btn btn-primary';

						
							const downloadIcon = document.createElement('i');
							downloadIcon.className = 'fas fa-download'; 
							downloadIcon.style.marginRight = '5px';

						
							downloadButton.appendChild(downloadIcon);
							downloadButton.appendChild(document.createTextNode('Download'));

					
							downloadButton.addEventListener('click', () => {
							
								downloadBase64File(item.file, item.filename || 'default_filename.ext');

								// 					const base64String = 'aGVsbG8gaGkNCg=='; // Base64 for "Hello, World!"
								// const fileName = 'example.txt';
								// downloadBase64File(base64String, fileName);
							});
							cardBodyDiv.appendChild(downloadButton);
						}
						
						cardDiv.appendChild(cardBodyDiv);

						return cardDiv;
					}

					function renderPage(page) {
						const startIndex = (page - 1) * itemsPerPage;
						const endIndex = Math.min(startIndex + itemsPerPage, data.length);
						const container = document.getElementById('dataContainer');
						container.innerHTML = ''; 

					
						for (let i = startIndex; i < endIndex; i++) {
							const card = createCard(data[i]);
							container.appendChild(card);
						}
					}

					function setupPagination() {
						const paginationContainer = document.getElementById('paginationControls');
						const totalPages = Math.ceil(data.length / itemsPerPage);
						paginationContainer.innerHTML = ''; 

						if (totalPages > 1) {
					
							if (currentPage > 1) {
								const prevButton = document.createElement('button');
								prevButton.textContent = 'Previous';
								prevButton.className = 'btn btn-secondary me-2';
								prevButton.onclick = () => {
									currentPage--;
									renderPage(currentPage);
									setupPagination();
								};
								paginationContainer.appendChild(prevButton);
							}

						
							for (let i = 1; i <= totalPages; i++) {
								const pageButton = document.createElement('button');
								pageButton.textContent = i;
								pageButton.className = 'btn btn-secondary me-2';
								pageButton.onclick = () => {
									currentPage = i;
									renderPage(currentPage);
									setupPagination();
								};
								if (i === currentPage) {
									pageButton.disabled = true;
								}
								paginationContainer.appendChild(pageButton);
							}

						
							if (currentPage < totalPages) {
								const nextButton = document.createElement('button');
								nextButton.textContent = 'Next';
								nextButton.className = 'btn btn-secondary me-2';
								nextButton.onclick = () => {
									currentPage++;
									renderPage(currentPage);
									setupPagination();
								};
								paginationContainer.appendChild(nextButton);
							}
						}
					}

					
					fetchData();
				});
			</script>

	<script type="text/javascript">
				var status = "<s:property value='%{grs.status}'/>";
				if (status == "CLOSED") {
					document.getElementById("sendButton").disabled = true;
					document.getElementById("fileInput").disabled = true;
				}
				function getBase64(file) {
					return new Promise((resolve, reject) => {
						const reader = new FileReader();
						reader.onloadend = () => {
							resolve(reader.result.split(',')[1]);
						};
						reader.onerror = reject;
						reader.readAsDataURL(file); 
					});
				}

				async function saveDescription() {
					var grsDesc = $("#descriptionTextArea").val();
					var grdID = $("#GrievanceId").val();

					if (grsDesc == "" || grsDesc == null || grsDesc == undefined) {
						$("#documentError").text("Grievance Description is Mandatory");
						return false;
					}

					var userEmailId = "<s:property value='%{#session.USER.businessName}'/>";
					var fileData = "";
					var fileName = "";
					var fileInput = $("#uploadFileNew")[0];
					if (fileInput.files.length > 0) {
						const file = fileInput.files[0];
						fileData = await getBase64(file);
						fileName = file.name;


					}
					var obj = {
						"description": grsDesc,
						"grsId": grdID,
						"createdBy": userEmailId,
						"filename": fileName,
						"file": fileData
					};

					var urls = new URL(window.location.href);
					var domain = urls.origin;
					$.ajax({
						url: domain + "/crmws/GRS/SaveDescription",
						type: 'POST',
						data: JSON.stringify(obj),
						contentType: "application/json",
						success: function (data) {

							alert("Description Saved Successfully");
							location.reload();


						},
						error: function (data, textStatus, jqXHR) {
							$("#documentError").text(data.respmessage);

						}
					});

				}



			</script>
</body>

</html>