<!DOCTYPE html>
<html lang="en">

<head>
	<meta charset="UTF-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>Finance Dashboard</title>
	<link href="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.css" rel="stylesheet" type="text/css" />
	<link href="../assets/plugins/custom/datatables/datatables.bundle.css" rel="stylesheet" type="text/css" />
	<link href="../assets/plugins/global/plugins.bundle.css" rel="stylesheet" type="text/css" />
	<link href="../assets/css/style.bundle.css" rel="stylesheet" type="text/css" />
	<link href="../assets/css/custom/custom-style.css" rel="stylesheet" type="text/css" />
	<script src="../js/loader/main.js"></script>
	<script src="../assets/plugins/global/plugins.bundle.js"></script>
	<script src="../assets/js/scripts.bundle.js"></script>
	<script src="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.js"></script>
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
	<style>
		.dt-buttons.btn-group.flex-wrap {
			display: none;
		}

		.container{
			width: 300px;
			background-color: #3a91c0;

		}
		.cards{
			width: auto;
			height: 100%;
 			border-radius: 10px;
		 	background-color: #234b7a;
	   		-webkit-transition:  box-shadow .6s ease-out;

		}
	/*	.card-custom {
			background-color: #fbda96;
			border-radius: 10px;
			box-shadow: 6px 8px 10px 1px #d5a857;
			height: 100%;
			justify-content: center;
			cursor: pointer;
		}
*/
		.svg-icon {
			margin-top: 1vh !important;
		}

		 .cards-header{
	 			height: 70px;
				background-color: #234b7a;
				margin: 5px;
			 	border-radius: 10px;
				width: auto;


		 }

		 .card-success{
			height: auto;
			width: 100%;
			text-align: center;
	 	 	text-justify:auto;
			font-weight: 700;
 			font-size: 24px;
		 }

		.cards-header-title{
			color: #ffffff;
	 		font-weight: 700;
		 	height: 60px;
		 	background-color: #234b7a;
			border-radius: 10px;
			margin: 2px;
			color:#ffffff;
		 	padding: 5px 5px 5px 5px;

		}


		.card-body{
		  	height: auto;
	 	 	padding: 10px;
		 	font-weight: 800;
			font-size: 24px;
			position: relative;
			text-align: justify;
			background-color: #ffffff;
			color: #181C32;
			max-width: 100%;
	 		height: 150px;
			border-bottom-left-radius: 10px;
			border-bottom-right-radius: 10px;
			box-shadow: 6px 8px 10px 1px #ccc6c6;
			background: linear-gradient(to top, #dfe9f3 0%, white 100%); 	}

		#rnsCount ,
		#forceCaptureCount,
		#exceptionCount,
		#successFailedDiv{
			left:50%;
			position: absolute;
			top:50%;
			Transform: translate(-50%, -50%);

		}

		.cards:hover{box-shadow: 1px 8px 20px rgb(100, 97, 97);
    -webkit-transition:  box-shadow .4s ease-in;
		}

	 @media  screen and (max-width:600px) {
		.cards{
			width: 70%;
			display: block;
			margin-bottom: 50px;
		}

	 }

	 @media  screen and (max-width:1200px) {
		.cards{
			width: 90%;
			display: block;
			margin-bottom: 50px;
		}

	 }
	 @media  screen and (max-width:980px) {
		.cards{
			width:80%;
			display: block;
			margin-bottom: 50px;
		}

	 }
		.modal {
			top: 110px;
/* 			left: 80px; */
/* 			--bs-modal-width: 70%; */
			left: 8%;
   			--bs-modal-width: 78%;
		}

		.close {
			padding: 0px 10px;
			background-color: orange;
		}

		.margin-right {
			margin-right: 4%;
		}
	</style>
	<script>
		function exceptionCount() {
			// $.post("GetExceptionCount", {
			// 	date: $("#dateFrom").val()
			// }, function (data, status) {
			// 	$("#exceptionCount").text(data.count.exceptionCount);
			// });

			function generatePostData(d) {
				debugger
				var token = document.getElementsByName("token")[0].value;
				var date = document.getElementById("dateFrom").value;
				var t = "Exception";

				var obj = {
					t: t,
					date: date,
					draw: d.draw,
					length: d.length,
					start: d.start,
					token: token,
					"struts.token.name": "token",
				};

				return obj;
			}

			$('#txnResultDataTable1')
				.dataTable(

					{

						"columnDefs": [{
							className: "dt-body-right",
							"targets": [1, 2, 3, 4, 5, 6, 7, 8, 9,
								10, 11, 12, 13, 14, 15, 16, 17, 18,
								19, 20]
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
									columns: [0, 1, 2, 3, 4, 5,
										6, 7, 8, 9, 10, 11, 12,
										13, 14, 15, 16, 17, 18,
										19, 20]
								}
							},
							{
								extend: 'csv',
								exportOptions: {
									columns: [0, 1, 2, 3, 4, 5,
										6, 7, 8, 9, 10, 11, 12,
										13, 14, 15, 16, 17, 18,
										19, 20]
								}
							},
							{
								extend: 'pdf',
								exportOptions: {
									columns: [0, 1, 2, 3, 4, 5,
										6, 7, 8, 9, 10, 11, 12,
										13, 14, 15, 16, 17, 18,
										19, 20]
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

							"url": "DashboardReportForException",
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
							"targets": [0, 1, 2, 3, 4, 5, 6, 7, 8, 9,
								10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20]
						}],

						"columns": [

							{
								"data": "tXN_ID",
								"className": "txnId my_class1 text-class",
								"width": "2%"
							},
							{
								"data": "rECO_TXNTYPE",
								"className": "payId text-class"

							},
							{
								"data": "fILE_NAME",
								"className": "text-class"
							},
							{
								"data": "fILE_LINE_NO",
								"className": "text-class"
							},
							{
								"data": "fILE_LINE_DATA",
								"className": "orderId text-class"
							},
							{
								"data": "dB_TXN_ID",
								"className": "orderId text-class"
							},
							{
								"data": "dB_TXNTYPE",
								"className": "mopType text-class"
							},
							{
								"data": "dB_OID",
								"className": "text-class"
							}, {
								"data": "dB_ACQ_ID",
								"className": "txnType text-class"
							}, {
								"data": "dB_ORIG_TXN_ID",
								"className": "status text-class"
							}, {
								"data": "dB_ORIG_TXNTYPE",
								"className": "text-class"
							}, {
								"data": "dB_AMOUNT",
								"className": "text-class"
							}, {
								"data": "dB_PG_REF_NUM",
								"className": "text-class"

							}, {
								"data": "dB_ORDER_ID",
								"className": "text-class"
							}, {
								"data": "dB_PAY_ID",
								"className": "text-class"
							},
							{
								"data": "dB_ACQUIRER_TYPE",
								"className": "text-class"
							}, {
								"data": "cREATE_DATE",
								"className": "txnType text-class"
							}, {
								"data": "uPDATE_DATE",
								"className": "status text-class"
							}, {
								"data": "rESPONSE_CODE",
								"className": "text-class"
							}, {
								"data": "rESPONSE_MESSAGE",
								"className": "text-class"
							}, {
								"data": "dB_USER_TYPE",
								"className": "text-class"

							}, {
								"data": "rECO_EXCEPTION_STATUS",
								"className": "text-class"
							}]

					});
		}

		$.get("GetDashboard", function (data, status) {
			$("#rnsCount").text(data.count.rnsCount);
			$("#forceCaptureCount").text(data.count.forceCaptureCount);
			$("#exceptionCount").text(data.count.exceptionCount);

			var rns=data.count.rnsCount;
			var force=data.count.forceCaptureCount;
			var failed=data.count.exceptionCount;
			var success=rns+force;
			var failedd=failed;

			$("#totalSuccessCount").text(success);
			$("#totalfailedCount").text(failedd);

		});

		function showModal(type) {
			debugger
			$(".modal-title").text(type);

			if (type != "Exception") {

				function generatePostData(d) {
					debugger
					var token = document.getElementsByName("token")[0].value;
					var t = type;

					var obj = {
						t: t,
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
							"footerCallback": function (row, data, start,
								end, display) {
								var api = this.api(), data;

								// Remove the formatting to get integer data for summation
								var intVal = function (i) {
									return typeof i === 'string' ? i
										.replace(/[\,]/g, '') * 1
										: typeof i === 'number' ? i : 0;
								};

								// Total over all pages
								total = api.column(10).data().reduce(
									function (a, b) {
										return intVal(a) + intVal(b);
									}, 0);

								// Total over this page
								pageTotal = api.column(10, {
									page: 'current'
								}).data().reduce(function (a, b) {
									return intVal(a) + intVal(b);
								}, 0);

								// Update footer
								$(api.column(10).footer()).html(
									''
									+ inrFormat(pageTotal
										.toFixed(2)
										+ ' ' + ' '));

								// Total over all pages
								total = api.column(11).data().reduce(
									function (a, b) {
										return intVal(a) + intVal(b);
									}, 0);

								// Total over this page
								pageTotal = api.column(11, {
									page: 'current'
								}).data().reduce(function (a, b) {
									return intVal(a) + intVal(b);
								}, 0);

								// Update footer
								$(api.column(11).footer()).html(
									''
									+ inrFormat(pageTotal
										.toFixed(2)
										+ ' ' + ' '));

							},
							"columnDefs": [{
								className: "dt-body-right",
								"targets": [1, 2, 3, 4, 5, 6, 7, 8, 9,
									10, 11, 12, 13, 14, 15]
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
										columns: [0, 1, 2, 3, 4, 5,
											6, 7, 8, 9, 10, 11, 12,
											13, 14, 15, 16, 17, 18,
											19, 20]
									}
								},
								{
									extend: 'csv',
									exportOptions: {
										columns: [0, 1, 2, 3, 4, 5,
											6, 7, 8, 9, 10, 11, 12,
											13, 14, 15, 16, 17, 18,
											19]
									}
								},
								{
									extend: 'pdf',
									exportOptions: {
										columns: [0, 1, 2, 3, 4, 5,
											6, 7, 8, 9, 10, 11, 12,
											13, 14, 15, 16, 17, 18,
											19, 20, 21, 22]
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

								"url": "DashboardReport",
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
								"targets": [0, 1, 2, 3, 4, 5, 6, 7, 8, 9,
									10, 11, 12, 13, 14, 15, 16, 17]
							}],

							"columns": [

								{
									"data": "transactionIdString",
									"className": "txnId my_class1 text-class",
									"width": "2%"
								},
								{
									"data": "pgRefNum",
									"className": "payId text-class"

								},
								{
									"data": "merchants",
									"className": "text-class"
								},
								{
									"data": "dateFrom",
									"className": "text-class"
								},
								{
									"data": "orderId",
									"className": "orderId text-class"
								},
								{
									"data": "refundOrderId",
									"className": "orderId text-class"
								},
								{
									"data": "mopType",
									"className": "mopType text-class"
								},
								{
									"data": "paymentMethods",
									"render": function (data, type,
										full) {
										return full['paymentMethods']
											+ ' ' + '-' + ' '
											+ full['mopType'];
									},
									"className": "text-class"
								}, {
									"data": "txnType",
									"className": "txnType text-class",
								}, {
									"data": "status",
									"className": "status text-class"
								}, {
									"data": "amount",
									"className": "text-class",
									"render": function (data) {
										return inrFormat(data);
									}
								}, {
									"data": "totalAmount",
									"className": "text-class",
									"visible": false,
									"render": function (data) {
										return inrFormat(data);
									}
								}, {
									"data": "payId",
									"visible": false

								}, {
									"data": "customerEmail",
									"className": "text-class"
								}, {
									"data": "customerPhone",
									"className": "text-class"
								}, {
									data: "acquirerType"
								}, {
									data: "ipaddress"
								}, {
									data: "cardMask"
								}, {
									data: "rrn"
								}, {
									data: "splitPayment"
								}]

						});
				$("#NotExceptionTable").show();
				$("#ExceptionTable").hide();

			} else {
				function generatePostData(d) {
					debugger
					var token = document.getElementsByName("token")[0].value;
					var date = document.getElementById("dateFrom").value;
					var t = type;

					var obj = {
						t: t,
						date: date,
						draw: d.draw,
						length: d.length,
						start: d.start,
						token: token,
						"struts.token.name": "token",
					};

					return obj;
				}

				$('#txnResultDataTable1')
					.dataTable(

						{

							"columnDefs": [{
								className: "dt-body-right",
								"targets": [1, 2, 3, 4, 5, 6, 7, 8, 9,
									10, 11, 12, 13, 14, 15, 16, 17, 18,
									19, 20]
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
										columns: [0, 1, 2, 3, 4, 5,
											6, 7, 8, 9, 10, 11, 12,
											13, 14, 15, 16, 17, 18,
											19, 20]
									}
								},
								{
									extend: 'csv',
									exportOptions: {
										columns: [0, 1, 2, 3, 4, 5,
											6, 7, 8, 9, 10, 11, 12,
											13, 14, 15, 16, 17, 18,
											19, 20]
									}
								},
								{
									extend: 'pdf',
									exportOptions: {
										columns: [0, 1, 2, 3, 4, 5,
											6, 7, 8, 9, 10, 11, 12,
											13, 14, 15, 16, 17, 18,
											19, 20]
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

								"url": "DashboardReportForException",
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
								"targets": [0, 1, 2, 3, 4, 5, 6, 7, 8, 9,
									10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20]
							}],

							"columns": [

								{
									"data": "tXN_ID",
									"className": "txnId my_class1 text-class",
									"width": "2%"
								},
								{
									"data": "rECO_TXNTYPE",
									"className": "payId text-class"

								},
								{
									"data": "fILE_NAME",
									"className": "text-class"
								},
								{
									"data": "fILE_LINE_NO",
									"className": "text-class"
								},
								{
									"data": "fILE_LINE_DATA",
									"className": "orderId text-class"
								},
								{
									"data": "dB_TXN_ID",
									"className": "orderId text-class"
								},
								{
									"data": "dB_TXNTYPE",
									"className": "mopType text-class"
								},
								{
									"data": "dB_OID",
									"className": "text-class"
								}, {
									"data": "dB_ACQ_ID",
									"className": "txnType text-class"
								}, {
									"data": "dB_ORIG_TXN_ID",
									"className": "status text-class"
								}, {
									"data": "dB_ORIG_TXNTYPE",
									"className": "text-class"
								}, {
									"data": "dB_AMOUNT",
									"className": "text-class"
								}, {
									"data": "dB_PG_REF_NUM",
									"className": "text-class"

								}, {
									"data": "dB_ORDER_ID",
									"className": "text-class"
								}, {
									"data": "dB_PAY_ID",
									"className": "text-class"
								},
								{
									"data": "dB_ACQUIRER_TYPE",
									"className": "text-class"
								}, {
									"data": "cREATE_DATE",
									"className": "txnType text-class"
								}, {
									"data": "uPDATE_DATE",
									"className": "status text-class"
								}, {
									"data": "rESPONSE_CODE",
									"className": "text-class"
								}, {
									"data": "rESPONSE_MESSAGE",
									"className": "text-class"
								}, {
									"data": "dB_USER_TYPE",
									"className": "text-class"

								}, {
									"data": "rECO_EXCEPTION_STATUS",
									"className": "text-class"
								}]

						});
				$("#NotExceptionTable").hide();
				$("#ExceptionTable").show();
			}
			$(".modal").show();
		}
		function closeModal() {
			$(".modal").hide();
		}

		$(document).ready(function(){

			var speed = 1;

 				function incEltNbr(id) {
 		 elt = document.getElementById(id);
 		 endNbr = Number(document.getElementById(id).innerHTML);
 		 incNbrRec(0, endNbr, elt);
			}

 			function incNbrRec(i, endNbr, elt) {
 		 if (i <= endNbr) {
 		   elt.innerHTML = i;
 		   setTimeout(function() {
 		     incNbrRec(i + 1, endNbr, elt);
 		   }, speed);
 		 }
			}

		/*Animation on Number load */
			incEltNbr("totalSuccessCount");
			incEltNbr("totalfailedCount");
			incEltNbr("rnsCount");
			incEltNbr("forceCaptureCount");
			incEltNbr("exceptionCount");

		});
	</script>
</head>

<body id="kt_body"
	class="header-fixed header-tablet-and-mobile-fixed toolbar-enabled toolbar-fixed toolbar-tablet-and-mobile-fixed aside-enabled aside-fixed"
	style="-kt-toolbar-height: 55px; -kt-toolbar-height-tablet-and-mobile: 55px">

	<div class="content d-flex flex-column flex-column-fluid" id="kt_content">
		<div class="toolbar" id="kt_toolbar">
			<!--begin::Container-->
			<div id="kt_toolbar_container" class="container-fluid d-flex flex-stack">
				<div data-kt-swapper="true" data-kt-swapper-mode="prepend"
					data-kt-swapper-parent="{default: '#kt_content_container', 'lg': '#kt_toolbar_container'}"
					class="page-title d-flex align-items-center me-3 flex-wrap mb-5 mb-lg-0 lh-1">
					<!--begin::Title-->
					<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">
						Finance Dashboard</h1>
					<!--end::Title-->
					<!--begin::Separator-->
					<span class="h-20px border-gray-200 border-start mx-4"></span>
					<!--end::Separator-->
					<!--begin::Breadcrumb-->
					<ul class="breadcrumb breadcrumb-separatorless fw-semibold fs-7 my-1">
						<!--begin::Item-->
						<li class="breadcrumb-item text-muted"><a href="home"
								class="text-muted text-hover-primary">Dashboard</a></li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item"><span class="bullet bg-gray-200 w-5px h-2px"></span></li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item text-muted">Analytics</li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item"><span class="bullet bg-gray-200 w-5px h-2px"></span></li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item text-dark">Finance Dashboard</li>
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
						<div class="row my-3 align-items-center d-flex justify-content-around">
							<div class="col-lg-3">
								<div class="cards">
									<div class="card-header">
										<h4 class="cards-header-title" id="heading">TOTAL</h4>
									</div>
									<div class="card-body">
										<div class="card-success" id="successFailedDiv">
											<text >Success: <span id="totalSuccessCount"></span></text>
											</br>
											<text >Failed: <span id="totalfailedCount"></span></text>
										</div>
									</div>
								</div>
							</div>
							<div class="col-lg-3">
								<div class="cards" onclick="showModal('RNS')">
									<div class="card-header">
										<h4 class="cards-header-title">RNS COUNT</h4>
									</div>
						 			<div class="card-body">
										<text class="card-body-custom" id="rnsCount"></text>
									</div>
								</div>
							</div>
							<div class="col-lg-3">
								<div class="cards" onclick="showModal('Force Capture')">
									<div class="card-header">
										<h4 class="cards-header-title">FORCE CAPTURE
											COUNT</h4>
									</div>
									<div class="card-body">
										<text class="card-body-custom" id="forceCaptureCount"></text>
									</div>
								</div>
							</div>
							<div class="col-lg-3">
								<div class="cards" onclick="showModal('Exception')">
									<div class="card-header">
										<h3 class="cards-header-title">EXCEPTION
										COUNT</h3>
									</div>
									<div class="card-body">
										<text class="card-body-custom" id="exceptionCount"></text>
									</div>
								</div>
							</div>
						</div>
			 		</div>
				</div>
			</div>
		</div>


	<div class="modal" tabindex="-1" role="dialog">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title">Modal title</h5>
					<button type="button" class="close" data-dismiss="modal" aria-label="Close" onclick="closeModal()">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<div class="modal-body" id="NotExceptionTable">
					<div class="row my-3 align-items-center d-flex">

						<div class="col-lg-3 my-2 margin-right">
							<label class="d-flex align-items-center fs-6 fw-bold mb-2">
								<span class="">&nbsp;</span>
							</label>
							<button type="button" class="btn btn-primary" id="done1"
								onclick="downloadReport()">Download</button>
							<!--end::Datepicker-->
						</div>
					</div>
					<div class="row g-9 mb-8 justify-content-end">
						<div class="col-lg-4 col-sm-12 col-md-6">
							<select name="currency" data-placeholder="Actions" id="actions11"
								class="form-select form-select-solid actions" data-hide-search="true"
								onchange="myFunction();">
								<option value="">Actions</option>
								<option value="copy">Copy</option>
								<option value="csv">CSV</option>
								<option value="pdf">PDF</option>
							</select>
						</div>
						<div class="col-lg-4 col-sm-12 col-md-6">
							<div class="dropdown1">
								<button class="form-select form-select-solid actions dropbtn1">Customize
									Columns</button>
								<div class="dropdown-content1">
									<a class="toggle-vis" data-column="0">Txn Id</a> <a class="toggle-vis"
										data-column="1">Pg ref no</a> <a class="toggle-vis" data-column="2">Merchant</a>
									<a class="toggle-vis" data-column="3">Date</a> <a class="toggle-vis"
										data-column="4">Order Id</a> <a class="toggle-vis" data-column="5">Refund Order
										Id</a> <a class="toggle-vis" data-column="6">Mop Type</a> <a class="toggle-vis"
										data-column="7">Payment Method</a> <a class="toggle-vis" data-column="8">Txn
										Type</a> <a class="toggle-vis" data-column="9">Status</a> <a class="toggle-vis"
										data-column="10">Base Amount</a> <a class="toggle-vis" data-column="11">Total
										Amount</a> <a class="toggle-vis" data-column="12">Pay Id</a> <a
										class="toggle-vis" data-column="13">Customer Email</a> <a class="toggle-vis"
										data-column="14">Customer Ph Number</a> <a class="toggle-vis"
										data-column="15">Udf 4</a>
								</div>
							</div>
						</div>
					</div>
					<div class="row g-9 mb-8">
						<div class="table-responsive">
							<table id="txnResultDataTable"
								class="table table-striped table-row-bordered gy-5 gs-7 dataTable no-footer">
								<thead>
									<tr class="fw-bold fs-6 text-gray-800">

										<th class="min-w-90px">Txn Id</th>
										<th class="min-w-90px">Pg Ref Num</th>
										<th class="min-w-90px">Merchant</th>
										<th class="min-w-90px">Date</th>
										<th class="min-w-90px">Order Id</th>
										<th class="min-w-90px">Refund Order Id</th>
										<th class="min-w-90px">Mop Type</th>
										<th class="min-w-90px">Payment Method</th>
										<th class="min-w-90px">Txn Type</th>
										<th class="min-w-90px">Status</th>
										<th class="min-w-90px">Base Amount</th>
										<th class="min-w-90px">Total Amount</th>
										<th class="min-w-90px">Pay ID</th>
										<th class="min-w-90px">Customer Email</th>
										<th class="min-w-90px">Customer Ph Number</th>
										<th class="min-w-90px">Acquirer Type</th>
										<th class="min-w-90px">Ip Address</th>
										<th class="min-w-90px">card Mask</th>
										<th class="min-w-90px">RRN Number</th>
										<th class="min-w-90px">SplitPayment</th>


									</tr>
								</thead>
								<tfoot>
									<tr class="fw-bold fs-6 text-gray-800">

										<th class="min-w-90px"></th>
										<th class="min-w-90px"></th>
										<th class="min-w-90px"></th>
										<th class="min-w-90px"></th>
										<th class="min-w-90px"></th>
										<th class="min-w-90px"></th>
										<th class="min-w-90px"></th>
										<th class="min-w-90px"></th>
										<th class="min-w-90px"></th>
										<th class="min-w-90px"></th>
										<th class="min-w-90px"></th>
										<th class="min-w-90px"></th>
										<th class="min-w-90px"></th>
										<th class="min-w-90px"></th>
										<th class="min-w-90px"></th>
										<th class="min-w-90px"></th>
										<th class="min-w-90px"></th>
										<th class="min-w-90px"></th>
										<th class="min-w-90px"></th>
										<th class="min-w-90px"></th>


									</tr>
								</tfoot>
							</table>
						</div>
					</div>

				</div>



				<div class="modal-body" id="ExceptionTable">
					<div class="row my-3 align-items-center d-flex">
						<div class="col-lg-3 my-2 ">
							<label class="d-flex align-items-center fs-6 fw-bold mb-2">
								<span class="required">Date</span>
							</label>
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
							<input class="form-control form-control-solid ps-12 flatpickr-input"
								placeholder="Select a date" name="dateFrom" id="dateFrom" type="text">
							<!--end::Datepicker-->
						</div>
						<div class="col-lg-3 my-2 ">
							<label class="d-flex align-items-center fs-6 fw-bold mb-2">
								<span class="">&nbsp;</span>
							</label>
							<button type="button" class="btn btn-primary" id="done"
								onclick="exceptionCount()">Search</button>
							<!--end::Datepicker-->
						</div>
						<div class="col-lg-3 my-2 ">
							<label class="d-flex align-items-center fs-6 fw-bold mb-2">
								<span class="">&nbsp;</span>
							</label>
							<button type="button" class="btn btn-primary" id="done"
								onclick="downloadException()">Download</button>
							<!--end::Datepicker-->
						</div>
					</div>
					<div class="row g-9 mb-8 justify-content-end">
						<div class="col-lg-4 col-sm-12 col-md-6">
							<select name="currency" data-placeholder="Actions" id="actions111"
								class="form-select form-select-solid actions" data-hide-search="true"
								onchange="myFunction1();">
								<option value="">Actions</option>
								<option value="copy">Copy</option>
								<option value="csv">CSV</option>
								<option value="pdf">PDF</option>
							</select>
						</div>
						<div class="col-lg-4 col-sm-12 col-md-6">
							<div class="dropdown1">
								<button class="form-select form-select-solid actions dropbtn1">Customize
									Columns</button>
								<div class="dropdown-content1">
									<a class="toggle-vis" data-column="0">Txn Id</a> <a class="toggle-vis"
										data-column="1">RECO_TXNTYPE</a> <a class="toggle-vis"
										data-column="2">FILE_NAME</a> <a class="toggle-vis"
										data-column="3">FILE_LINE_NO</a> <a class="toggle-vis"
										data-column="4">FILE_LINE_DATA</a> <a class="toggle-vis"
										data-column="5">DB_TXN_ID</a> <a class="toggle-vis" data-column="6">Mop Type</a>
								</div>
							</div>
						</div>
					</div>
					<div class="row g-9 mb-8">
						<div class="table-responsive">
							<table id="txnResultDataTable1"
								class="table table-striped table-row-bordered gy-5 gs-7 dataTable no-footer">
								<thead>
									<tr class="fw-bold fs-6 text-gray-800">

										<th class="min-w-90px">Txn Id</th>
										<th class="min-w-90px">RECO_TXNTYPE</th>
										<th class="min-w-90px">FILE_NAME</th>
										<th class="min-w-90px">FILE_LINE_NO</th>
										<th class="min-w-90px">FILE_LINE_DATA</th>
										<th class="min-w-90px">DB_TXN_ID</th>
										<th class="min-w-90px">DB_TXNTYPE</th>
										<th class="min-w-90px">DB_OID</th>
										<th class="min-w-90px">DB_ACQ_ID</th>
										<th class="min-w-90px">DB_ORIG_TXN_ID</th>
										<th class="min-w-90px">DB_ORIG_TXNTYPE</th>
										<th class="min-w-90px">DB_AMOUNT</th>
										<th class="min-w-90px">DB_PG_REF_NUM</th>
										<th class="min-w-90px">DB_ORDER_ID</th>
										<th class="min-w-90px">DB_PAY_ID</th>
										<th class="min-w-90px">DB_ACQUIRER_TYPE</th>
										<th class="min-w-90px">CREATE_DATE</th>
										<th class="min-w-90px">UPDATE_DATE</th>
										<th class="min-w-90px">RESPONSE_CODE</th>
										<th class="min-w-90px">RESPONSE_MESSAGE</th>
										<th class="min-w-90px">DB_USER_TYPE</th>
										<th class="min-w-90px">RECO_EXCEPTION_STATUS</th>

									</tr>
								</thead>
								<tfoot>
									<tr class="fw-bold fs-6 text-gray-800">

										<th class="min-w-90px"></th>
										<th class="min-w-90px"></th>
										<th class="min-w-90px"></th>
										<th class="min-w-90px"></th>
										<th class="min-w-90px"></th>
										<th class="min-w-90px"></th>
										<th class="min-w-90px"></th>
										<th class="min-w-90px"></th>
										<th class="min-w-90px"></th>
										<th class="min-w-90px"></th>
										<th class="min-w-90px"></th>
										<th class="min-w-90px"></th>
										<th class="min-w-90px"></th>
										<th class="min-w-90px"></th>
										<th class="min-w-90px"></th>
										<th class="min-w-90px"></th>
										<th class="min-w-90px"></th>
										<th class="min-w-90px"></th>
										<th class="min-w-90px"></th>
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
	<form action="DownloadReportRNSandForceCapture" id="drrf">
		<input type="hidden" name="typeof" id="typeof" >
	</form>

	<form action="DownloadReportException" id="dre">
		<input type="hidden" name="type" id="type" >
		<input type="hidden" name="dFrom" id="dFrom" >
	</form>
	<script src="../assets/plugins/custom/datatables/datatables.bundle.js"></script>
	<script>
		function downloadException() {
			var type = $(".modal-title").text();
			var date = $("#dateFrom").val();
			
			$("#type").val(type);
			$("#dFrom").val(date);
			
			if(date!=''){
			$("#dre").submit();
			}else{
				alert("Please Select Date");
			}
		}
		function downloadReport() {
			
			var type = $(".modal-title").text();
			$("#typeof").val(type);
			$("#drrf").submit();
		}


		$('a.toggle-vis').on('click', function (e) {
			debugger
			e.preventDefault();
			table = $('#txnResultDataTable').DataTable();
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
		function myFunction() {
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



		$('a.toggle-vis').on('click', function (e) {
			debugger
			e.preventDefault();
			table = $('#txnResultDataTable1').DataTable();
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
		function myFunction1() {
			var x = document.getElementById("actions111").value;
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
		$("#dateFrom").flatpickr({
			maxDate: new Date(),
			dateFormat: 'Y-m-d',
			defaultDate: "today"
		});

	</script>
</body>

</html>