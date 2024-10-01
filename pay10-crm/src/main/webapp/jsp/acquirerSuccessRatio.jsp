<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="/struts-tags" prefix="s"%>
<html dir="ltr" lang="en-US">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title> Acquirer Success Ratio</title>




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
<script src="../js/jquery.select2.js" type="text/javascript"></script>
<script type="text/javascript" src="../js/sweetalert.js"></script>
				<link rel="stylesheet" href="../css/sweetalert.css">
<style>
.mytable thead {
	background: linear-gradient(60deg, #f7a600, #f71d00);
	color: #fff;
}


	.selectBox {
					position: relative;
				}

				.selectBox select {
					width: 95%;
				}

				#checkboxes1 {
    display: none;
    border: 1px #eababa solid;
    height: 130px;
    overflow-y: scroll;
    position: Absolute;
    background: #edf5f9;
    z-index: 1;
    margin-left: 1px;
    width: 234px;}


				#checkboxes1 label {
					width: 74%;
				}

				#checkboxes1 input {
					width: 18%;

				}

				span.clockpicker-span-hours.text-primary {
					color: #999999 !important;
				}

				span.clockpicker-span-minutes.text-primary {
					color: #999999 !important;
				}

				button.btn.btn-sm.btn-default.clockpicker-button.am-button {
					padding: 0.40625rem 0.25rem !important;
					line-height: 0.5;
				}

				button.btn.btn-sm.btn-default.clockpicker-button.pm-button {
					padding: 0.40625rem 0.25rem !important;
					line-height: 0.5;
				}
.switch {
	position: relative;
	display: inline-block;
	width: 60px;
	height: 34px;
}

.switch input {
	opacity: 0;
	width: 0;
	height: 0;
}

.slider {
	position: absolute;
	cursor: pointer;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	background-color: #e9ac0c;
	-webkit-transition: .4s;
	transition: .4s;
}

.slider:before {
	position: absolute;
	content: "";
	height: 26px;
	width: 26px;
	left: 4px;
	bottom: 4px;
	background-color: white;
	-webkit-transition: .4s;
	transition: .4s;
}

input:checked+.slider {
	background-color: #28231D;
}

input:focus+.slider {
	box-shadow: 0 0 1px #2196F3;
}

input:checked+.slider:before {
	-webkit-transform: translateX(26px);
	-ms-transform: translateX(26px);
	transform: translateX(26px);
}

/* Rounded sliders */
.slider.round {
	border-radius: 34px;
}

.slider.round:before {
	border-radius: 50%;
}
</style>
 <style>
        h1 {
            color: green;
        }
               
        /* toggle in label designing */
        .toggle {
            position : relative ;
            display : inline-block;
            width : 80px;
            height : 33px;
            background-color: #e01717e0;
            border-radius: 30px;
            border: 1px solid  #0a0000;
        }
               
        /* After slide changes */
        .toggle:after {
            content: '';
            position: absolute;
            width: 30px;
            height: 30px;
            border-radius: 50%;
            background-color: #f7f7f7;
            top: 1px;
            left: 1px;
            transition:  all 0.5s;
        }
               
        /* Toggle text */
        p {
                font-size: 19px;

        }
               
        /* Checkbox checked effect */
        .checkbox:checked + .toggle::after {
            left : 49px;
        }
               
        /* Checkbox checked toggle label bg color */
        .checkbox:checked + .toggle {
            background-color: #2e9e0763;
        }
               
        /* Checkbox vanished */
        .checkbox {
            display : none;
        }
    </style>




<%-- 
<link href="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.css" rel="stylesheet" type="text/css" />
<link href="../assets/plugins/custom/datatables/datatables.bundle.css" rel="stylesheet" type="text/css" />
<link href="../assets/plugins/global/plugins.bundle.css" rel="stylesheet" type="text/css" />
<link href="../assets/css/style.bundle.css" rel="stylesheet" type="text/css" />
<link href="../assets/css/custom/custom-style.css" rel="stylesheet" type="text/css" />

<!-- 
<link href="../css/Jquerydatatableview.css" rel="stylesheet" />
<link href="../css/Jquerydatatable.css" rel="stylesheet" />
<link href="../css/default.css" rel="stylesheet" type="text/css" />
<link href="../css/jquery-ui.css" rel="stylesheet" /> -->

<script type="text/javascript" src="../js/jquery.min.js"></script>
<script src="../js/jquery.js"></script>
<script src="../js/jquery.dataTables.js"></script>
<script src="../js/jquery-ui.js"></script>
<script type="text/javascript" src="../js/moment.js"></script>
<script type="text/javascript" src="../js/daterangepicker.js"></script>

<script src="../js/jquery.popupoverlay.js"></script>
<script type="text/javascript" src="../js/dataTables.buttons.js"></script>
<script type="text/javascript" src="../js/pdfmake.js"></script>
<script src="../js/core/popper.min.js"></script>
<script src="../js/core/bootstrap-material-design.min.js"></script>
<script src="../js/plugins/perfect-scrollbar.jquery.min.js"></script>
<!-- Control Center for Material Dashboard: parallax effects, scripts for the example pages etc -->
<script src="../js/material-dashboard.js?v=2.1.0" type="text/javascript"></script>
<script src="../assets/js/scripts.bundle.js"></script> --%>
<style>
#loading {
	width: 100%;
	height: 100%;
	top: 0px;
	left: 0px;
	position: fixed;
	display: block;
	z-index: 99
}

#loading-image {
	position: absolute;
	top: 40%;
	left: 55%;
	z-index: 100;
	width: 10%;
}

.dt-buttons.btn-group.flex-wrap {
    display: none;
}
table.dataTable.display tbody tr.odd {
    background-color: #e6e6ff !important;
}
table.dataTable.display tbody tr.odd > .sorting_1{
	 background-color: #e6e6ff !important;
}
table.display td.center{
	text-align: left !important;
}
.btn:focus{
		outline: 0 !important;
	}
</style>
<script type="text/javascript">


	
	
function decodeVal(text){	
	  return $('<div/>').html(text).text();
	}
	
	
	

//  $(document).ready(
		
//  		function() {
 			
//  			document.getElementById("loading").style.display = "none";

 			
//  			document.getElementById("autoRefund").checked=false ;
//  		});	
							
// 			populateDataTable();
// 			//enableBaseOnAccess();
// 			$("#submit").click(
// 					function(env) {
// 						/* var table = $('#authorizeDataTable')
// 								.DataTable(); */
// 						$('#searchAgentDataTable').empty();
						

// 						populateDataTable();

// 					});
// 		});	
					


function populateDataTable() {	
	debugger
	var time1="AM";
var dateFrom=	document.getElementById("dateFrom").value ;
var acquirer = document.getElementById("selectBox1") ? document.getElementById
		("selectBox1").title : '';
var autoRefund = document.getElementById("autoRefund").checked;
if(autoRefund){
	time1="PM"
}
for(var i=0;i<=11;i++){
document.getElementsByClassName("time")[i].innerHTML = time1;
}
if(acquirer=="ALL"){
	acquirer="";
}

debugger
	
	var token  = document.getElementsByName("token")[0].value;
	
	   var urls = new URL(window.location.href);
    var domain = urls.origin;
	$('#searchAgentDataTable')
			.DataTable(
					{
						dom : 'BTftlpi',
						buttons : [ {
							extend : 'copyHtml5',
							exportOptions : {
								columns : [':visible :not(:last-child)']
							}
						}, {
							extend : 'csvHtml5',
							title : 'Acquirer List',
							exportOptions : {
								columns : [':visible :not(:last-child)']
							}
						}, {
							extend : 'pdfHtml5',
							title : 'Acquirer List',
							orientation : 'landscape',
							exportOptions : {
								columns : [':visible :not(:last-child)']
							},
							customize: function (doc) {
							    doc.defaultStyle.alignment = 'center';
		     					doc.styles.tableHeader.alignment = 'center';
							  }
						}, {
							extend : 'print',
							title : 'Acquirer List',
							orientation : 'landscape',
							exportOptions : {
								columns : [':visible :not(:last-child)']
							}
						},{
							extend : 'colvis',
							//           collectionLayout: 'fixed two-column',
							columns : [ 0, 1, 2,3]
						}],
						"ajax" : {
							"url" : domain+"/crmws/AcquirerSuccess/Ratio?time1="+time1+"&acquirer="+acquirer+"&dateFrom="+dateFrom,
							"type" : "POST",
							"data" : {
						
									token:token,
								    "struts.token.name": "token",
									}
						},
						"bProcessing" : true,
						"bLengthChange" : true,
						"bDestroy" : true,
						"iDisplayLength" : 10,
						"order" : [ [ 0, "desc" ] ],
						"aoColumns" : [										
										{
											"mData" : "acquirer",
											"sWidth" : '25%',
										},
										
										{
											"mData": null,
											"sClass": "center",
											"bSortable": false,
											"mRender": function (data) {
												if(Number(data.a1201)>50.00){
												return '<h1><button  style="width: 63px;height: 26px;background:green;font-size: 14px; border: 1px solid white;" " name="merchantEdit" id="merchantEdit" > '+data.a1201+'%</button></h1>';
												}
												else if(Number(data.a1201)<50.00&&Number(data.a1201)>=0){
													return '<h1><button  style="width: 63px;height: 26px;background:red;font-size: 14px; border: 1px solid white;" name="merchantEdit" id="merchantEdit" > '+data.a1201+'%</button></h1>';

												}
												else {
													return '<h1><button  style="width: 63px;height: 26px;background:pink;font-size: 14px; border: 1px solid white; " name="merchantEdit" id="merchantEdit" > NA</button></h1>';

												}
												
											}
										},
										
										{
											"mData": null,
											"sClass": "center",
											"bSortable": false,
											"mRender": function (data) {
												if(Number(data.a0102)>50.00){
												return '<h1><button  style="width: 63px;height: 26px;background:green;font-size: 14px; border: 1px solid white;"" " name="merchantEdit" id="merchantEdit" > '+data.a0102+'%</button></h1>';
												}
												else if(Number(data.a0102)<50.00&&Number(data.a0102)>=0){
													return '<h1><button  style="width: 63px;height: 26px;background:red;font-size: 14px;border: 1px solid white;"" " name="merchantEdit" id="merchantEdit" > '+data.a0102+'%</button></h1>';

												}
												else {
													return '<h1><button  style="width: 63px;height: 26px;background:pink;font-size: 14px;border: 1px solid white;"" " name="merchantEdit" id="merchantEdit" > NA</button></h1>';

												}
												
											}
										},
										
										{
											"mData": null,
											"sClass": "center",
											"bSortable": false,
											"mRender": function (data) {
												if(Number(data.a0203)>50.00){
												return '<h1><button  style="width: 63px;height: 26px;background:green;font-size: 14px;border: 1px solid white;"" " name="merchantEdit" id="merchantEdit" > '+data.a0203+'%</button></h1>';
												}
												else if(Number(data.a0203)<50.00&&Number(data.a0203)>=0){
													return '<h1><button  style="width: 63px;height: 26px;background:red;font-size: 14px;border: 1px solid white;"" " name="merchantEdit" id="merchantEdit" > '+data.a0203+'%</button></h1>';

												}
												else {
													return '<h1><button  style="width: 63px;height: 26px;background:pink;font-size: 14px;border: 1px solid white;"" " name="merchantEdit" id="merchantEdit" > NA</button></h1>';

												}
												
											}
										},
										{
											"mData": null,
											"sClass": "center",
											"bSortable": false,
											"mRender": function (data) {
												if(Number(data.a0304)>50.00){
												return '<h1><button  style="width: 63px;height: 26px;background:green;font-size: 14px;border: 1px solid white;"" " name="merchantEdit" id="merchantEdit" > '+data.a0304+'%</button></h1>';
												}
												else if(Number(data.a0304)<50.00&&Number(data.a0304)>=0){
													return '<h1><button  style="width: 63px;height: 26px;background:red;font-size: 14px;border: 1px solid white;"" " name="merchantEdit" id="merchantEdit" > '+data.a0304+'%</button></h1>';

												}
												else {
													return '<h1><button  style="width: 63px;height: 26px;background:pink;font-size: 14px;border: 1px solid white;"" " name="merchantEdit" id="merchantEdit" > NA</button></h1>';

												}
												
											}
										},
										{
											"mData": null,
											"sClass": "center",
											"bSortable": false,
											"mRender": function (data) {
												if(Number(data.a0405)>50.00){
												return '<h1><button  style="width: 63px;height: 26px;background:green;font-size: 14px;border: 1px solid white;" " name="merchantEdit" id="merchantEdit" > '+data.a0405+'%</button></h1>';
												}
												else if(Number(data.a0405)<50.00&&Number(data.a0405)>=0){
													return '<h1><button  style="width: 63px;height: 26px;background:red;font-size: 14px; border: 1px solid white;"" " name="merchantEdit" id="merchantEdit" > '+data.a0405+'%</button></h1>';

												}
												else {
													return '<h1><button  style="width: 63px;height: 26px;background:pink;font-size: 14px;border: 1px solid white;"" " name="merchantEdit" id="merchantEdit" > NA</button></h1>';

												}
												
											}
										},
										{
											"mData": null,
											"sClass": "center",
											"bSortable": false,
											"mRender": function (data) {
												if(Number(data.a0506)>50.00){
												return '<h1><button  style="width: 63px;height: 26px;background:green;font-size: 14px;border: 1px solid white;"" " name="merchantEdit" id="merchantEdit" > '+data.a0506+'%</button></h1>';
												}
												else if(Number(data.a0506)<50.00&&Number(data.a0506)>=0){
													return '<h1><button  style="width: 63px;height: 26px;background:red;font-size: 14px;border: 1px solid white;"" " name="merchantEdit" id="merchantEdit" > '+data.a0506+'%</button></h1>';

												}
												else {
													return '<h1><button  style="width: 63px;height: 26px;background:pink;font-size: 14px;border: 1px solid white;"" " name="merchantEdit" id="merchantEdit" > NA</button></h1>';

												}
												
											}
										},
										{
											"mData": null,
											"sClass": "center",
											"bSortable": false,
											"mRender": function (data) {
												if(Number(data.a0607)>50.00){
												return '<h1><button  style="width: 63px;height: 26px;background:green;font-size: 14px;border: 1px solid white;"" " name="merchantEdit" id="merchantEdit" > '+data.a0607+'%</button></h1>';
												}
												else if(Number(data.a0607)<50.00&&Number(data.a0607)>=0){
													return '<h1><button  style="width: 63px;height: 26px;background:red;font-size: 14px;border: 1px solid white;"" " name="merchantEdit" id="merchantEdit" > '+data.a0607+'%</button></h1>';

												}
												else {
													return '<h1><button  style="width: 63px;height: 26px;background:pink;font-size: 14px;border: 1px solid white;"" " name="merchantEdit" id="merchantEdit" > NA</button></h1>';

												}
												
											}
										},
										{
											"mData": null,
											"sClass": "center",
											"bSortable": false,
											"mRender": function (data) {
												if(Number(data.a0708)>50.00){
												return '<h1><button  style="width: 63px;height: 26px;background:green;font-size: 14px;border: 1px solid white;"" " name="merchantEdit" id="merchantEdit" > '+data.a0708+'%</button></h1>';
												}
												else if(Number(data.a0708)<50.00&&Number(data.a0708)>=0){
													return '<h1><button  style="width: 63px;height: 26px;background:red;font-size: 14px;border: 1px solid white;"" " name="merchantEdit" id="merchantEdit" > '+data.a0708+'%</button></h1>';

												}
												else {
													return '<h1><button  style="width: 63px;height: 26px;background:pink;font-size: 14px;border: 1px solid white;"" " name="merchantEdit" id="merchantEdit" > NA</button></h1>';

												}
												
											}
										},
										{
											"mData": null,
											"sClass": "center",
											"bSortable": false,
											"mRender": function (data) {
												if(Number(data.a0809)>50.00){
												return '<h1><button  style="width: 63px;height: 26px;background:green;font-size: 14px;border: 1px solid white;"" " name="merchantEdit" id="merchantEdit" > '+data.a0809+'%</button></h1>';
												}
												else if(Number(data.a0809)<50.00&&Number(data.a0809)>=0){
													return '<h1><button  style="width: 63px;height: 26px;background:red;font-size: 14px;border: 1px solid white;"" " name="merchantEdit" id="merchantEdit" > '+data.a0809+'%</button></h1>';

												}
												else {
													return '<h1><button  style="width: 63px;height: 26px;background:pink;font-size: 14px;border: 1px solid white;"" " name="merchantEdit" id="merchantEdit" > NA</button></h1>';

												}
												
											}
										},
										{
											"mData": null,
											"sClass": "center",
											"bSortable": false,
											"mRender": function (data) {
												if(Number(data.a0910)>50.00){
												return '<h1><button  style="width: 63px;height: 26px;background:green;font-size: 14px;border: 1px solid white;"" " name="merchantEdit" id="merchantEdit" > '+data.a0910+'%</button></h1>';
												}
												else if(Number(data.a0910)<50.00&&Number(data.a0910)>=0){
													return '<h1><button  style="width: 63px;height: 26px;background:red;font-size: 14px;border: 1px solid white;"" " name="merchantEdit" id="merchantEdit" > '+data.a0910+'%</button></h1>';

												}
												else {
													return '<h1><button  style="width: 63px;height: 26px;background:pink;font-size: 14px;border: 1px solid white;"" " name="merchantEdit" id="merchantEdit" > NA</button></h1>';

												}
												
											}
										},
										
										{
											"mData": null,
											"sClass": "center",
											"bSortable": false,
											"mRender": function (data) {
												if(Number(data.a1011)>=50.00){
												return '<h1><button  style="width: 63px;height: 26px;background:green;font-size: 14px;border: 1px solid white;"" " name="merchantEdit" id="merchantEdit" > '+data.a1011+'%</button></h1>';
												}
												else if(Number(data.a1011)<50.00&&Number(data.a1011)>=0){
													return '<h1><button  style="width: 63px;height: 26px;background:red;font-size: 14px;border: 1px solid white;"" " name="merchantEdit" id="merchantEdit" > '+data.a1011+'%</button></h1>';

												}
												else {
													return '<h1><button  style="width: 63px;height: 26px;background:pink;font-size: 14px;border: 1px solid white;"" " name="merchantEdit" id="merchantEdit" > NA</button></h1>';

												}
												
											}
										},
										{
											"mData": null,
											"sClass": "center",
											"bSortable": false,
											"mRender": function (data) {
												if(Number(data.a1112)>50.00){
												return '<h1><button  style="width: 63px;height: 26px;background:green;font-size: 14px;border: 1px solid white;"" " name="merchantEdit" id="merchantEdit" > '+data.a1112+'%</button></h1>';
												}
												else if(Number(data.a1112)<50.00&&Number(data.a1112)>=0){
													return '<h1><button  style="width: 63px;height: 26px;background:red;font-size: 14px;border: 1px solid white;"" " name="merchantEdit" id="merchantEdit" > '+data.a1112+'%</button></h1>';

												}
												else {
													return '<h1><button  style="width: 63px;height: 26px;background:pink;font-size: 14px;border: 1px solid white;"" " name="merchantEdit" id="merchantEdit" >NA</button></h1>';

												}
												
											}
										}
										]
					});

	 $(function() {

		var table = $('#searchAgentDataTable').DataTable();
		$('#searchAgentDataTable tbody')
				.on(
						'click',
						'td',
						function() {

							   var urls = new URL(window.location.href);
							    var domain = urls.origin;
							debugger
							var columnVisible = table.cell(this).index().columnVisible;
							var rowIndex = table.cell(this).index().row;
							var row = table.row(rowIndex).data();

							 var updateby =document.getElementById("email").value

							var id = table.cell(rowIndex, 6).data();
							 
							   
								swal({
									title: "Are you sure want to Delete this Rule?",
									type: "warning",
									showCancelButton: true,
									confirmButtonColor: "#DD6B55",
									confirmButtonText: "Yes, Delete it!",
									closeOnConfirm: false
									}, function (isConfirm) {
										if (!isConfirm) return;		
							    $.ajax({
									type : "POST",
									url : domain+"/crmws/AcquirerSwitch/deleteAcquirerDownTime",
									timeout : 0,
									data : {
										"updateBy":updateby,
										"id":id
										
										
									},
									success : function(data) {
										var response = data.response;
										swal({
										 title: 'Deleted Successful!',
										 type: "success"
										}, function(){
											window.location.reload();
										}); 
									},
									error : function(data) {
										window.location.reload();
									}
							    });
							
							})
	

													
						});
	});
}
</script>
<script type="text/javascript">
	function MM_openBrWindow(theURL, winName, features) { //v2.0
		window.open(theURL, winName, features);
	}

	function displayPopup() {
		document.getElementById('light3').style.display = 'block';
		document.getElementById('fade3').style.display = 'block';
	}
</script>

</head>
<body>

<div class="content d-flex flex-column flex-column-fluid"
					id="kt_content">

<div class="toolbar" id="kt_toolbar">
							<!--begin::Container-->
							<div id="kt_toolbar_container" class="container-fluid d-flex flex-stack">
								<!--begin::Page title-->
								<div data-kt-swapper="true" data-kt-swapper-mode="prepend" data-kt-swapper-parent="{default: '#kt_content_container', 'lg': '#kt_toolbar_container'}" class="page-title d-flex align-items-center me-3 flex-wrap mb-5 mb-lg-0 lh-1">
									<!--begin::Title-->
									<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">Acquirer Success Ratio</h1>
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
										<li class="breadcrumb-item text-muted">Manage Acquirers</li>
										<!--end::Item-->
										<!--begin::Item-->
										<li class="breadcrumb-item">
											<span class="bullet bg-gray-200 w-5px h-2px"></span>
										</li>
										<!--end::Item-->
										<!--begin::Item-->
										<li class="breadcrumb-item text-dark">Acquirer Success Ratio</li>
										<!--end::Item-->
									</ul>
									<!--end::Breadcrumb-->
								</div>
								<!--end::Page title-->
								
							</div>
							<!--end::Container-->
						</div>



 <div class="row my-5">
 
 <div id="loading" style="text-align: center;">
			<img id="loading-image" style="width: 70px; height: 70px;"
				src="../image/sand-clock-loader.gif" alt="Sending SMS..." />
		</div>
       <div class="col">	
		<div class="card ">
			<div class="card-body ">
				<div class="container">
					<div class="row">
						<s:form id="resellerPayoutForm">
												<input type="hidden" name="email" id="email" value="<s:property value='%{#session.USER.emailId}'/>" />		
						
						<div style="
    display: inline-flex;
">				
							<div class="col-md-6 fv-row">
								<label	class="d-flex align-items-center fs-6 fw-bold mb-2">
									<span class="">Acquirer :</span>
										</label>
											<!--end::Label-->
										<div class="txtnew">
															<div class="selectBox" id="selectBox1"
																onclick="showCheckboxes(event,1)" title="ALL">
																<select class="form-select form-select-solid">
																	<option>ALL</option>
																</select>
																<div class="overSelect"></div>
														
															<div id="checkboxes1" onclick="getCheckBoxValue(1)">
																<s:checkboxlist headerKey="ALL" headerValue="ALL"
																	list="@com.pay10.commons.util.AcquirerTypeUI@values()"
																	id="acquirer" class="myCheckBox1" listKey="code"
																	listValue="name" name="acquirer" value="acquirer" />
															</div>
																</div>
														</div>

										</div>
							<div class="col-md-8 fv-row" style="padding-left: 70px;">
								<label	class="d-flex align-items-center fs-6 fw-bold mb-2">
									<span class="">Date :</span>
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
															placeholder="Select a date" name="dateTo" id="dateFrom"
															type="text" readonly="readonly">
														<!--end::Datepicker-->
													</div>

										</div>
										
										<div class="col-md-8 fv-row" style="padding-left: 70px;">
										<label	class="d-flex align-items-center fs-6 fw-bold mb-2">
									<span class="">TimeSlot :</span>
										</label>
																				<label	class="d-flex align-items-center fs-6 fw-bold mb-2">
										
																			<span style="margin: 10px;">AM</span>
										
								<label class="switch"> <input
																									type="checkbox" id="autoRefund"
																										> <span
																										class="slider round"></span>
																									</label>	
																																											<span style="
    margin: 10px;
">PM </span></label>
																									
									

										</div>
										</div>	
										<div class="col-md-6 fv-row">
										<button type="button" id="search_payment_submit"
															onclick="SearchData()" class="btn btn-primary">search</button></div>	
						</s:form>
					</div>
				</div>
			</div>
		</div>
		</div>
		</div>
		
		
<div class="post d-flex flex-column-fluid" id="kt_post">

			<div id="kt_content_container" class="container-xxl">
				
				<div class="row my-5">
					<div class="col">
						<div class="card">
							<div class="card-body">
	<table width="100%" align="left" cellpadding="0" cellspacing="0"
		class="txnf">
		<tr>
			<td align="left"><s:actionmessage /></td>
		</tr>
		<!-- <tr>
			<td align="left"><h2>Acquirer List</h2></td>
		</tr> -->
		<tr>
		
		
		
		<div class="row g-9 mb-8 justify-content-end">
												
												
												</div>
											</div>
		
		
		
			<td align="left"><table width="100%" border="0" align="center"
					cellpadding="0" cellspacing="0">
					<tr>
						<td colspan="5" align="left" valign="top">&nbsp;</td>
					</tr>
					<tr>
						<td colspan="5" align="center" valign="top"><table
								width="100%" border="0" cellpadding="0" cellspacing="0">
							</table></td>
					</tr>
				</table></td>
		</tr>
		<tr>
			<td align="left" style="padding: 10px;">
				<div class="scrollD">
					<table width="100%" border="0" cellpadding="0" cellspacing="0"
						id="searchAgentDataTable"  class="table table-striped table-row-bordered gy-5 gs-7">
						<!-- class="display" -->
						<thead>
							<!-- <tr class="boxheadingsmall"> -->
							<tr class="fw-bold fs-6 text-gray-800">
															<th>Acquirer</th>
							
								<th>12:00-01:00<span class="time"></span></th>
								<th>01:00-02:00<span class="time"></span></th>
								<th>02:00-03:00<span class="time"></span></th>
								<th>03:00-04:00<span class="time"></span></th>
								<th>04:00-05:00<span class="time"></span></th>
								<th>05:00-06:00<span class="time"></span></th>
								<th>06:00-07:00<span class="time"></span></th>
								<th>07:00-08:00<span class="time"></span></th>
								<th>08:00-09:00<span class="time"></span></th>
								<th>09:00-10:00<span class="time"></span></th>
								<th>10:00-11:00<span class="time"></span></th>
								<th>11:00-12:00<span class="time"></span></th>
								
								
																<th></th>
								
								<th></th>
							</tr>
						</thead>
					</table>
				</div>
			</td>
		</tr>
	</table>
	</div>
	</div>
	</div>
	</div>
	</div>
	</div>
	

	</div>
<script type="text/javascript">
$('#searchAgentDataTable').on( 'draw.dt', function () {
} );

</script>
<script type="text/javascript">

function SearchData(){
	debugger
	populateDataTable()}
			$('a.toggle-vis').on('click', function(e) {
				/* debugger */
				e.preventDefault();
				table = $('#searchAgentDataTable').DataTable();
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

function hideAndShowForAuto(event){
	  var autoRefund = document.getElementById("autoRefund").checked;
	  if(autoRefund==true){
			document.getElementById("auto").checked=autoRefund

			document.getElementById("autoRefundHideAndShow").style.display = "Block";


	  }else
		  {
			document.getElementById("auto").checked=autoRefund

				document.getElementById("autoRefundHideAndShow").style.display = "none";

		  }
}
$(document).ready(function () {
	document.getElementById("loading").style.display = "none";

	$(function () {
		$("#dateFrom").flatpickr({
			maxDate: new Date(),
			dateFormat: "Y-m-d",
			defaultDate: "today",
			defaultDate: "today",
		});
		$("#dateTo").flatpickr({
			maxDate: new Date(),
			dateFormat: "d-m-Y",
			defaultDate: "today",
			maxDate: new Date()
		});
		$("#kt_datatable_vertical_scroll").DataTable({
			scrollY: true,
			scrollX: true

		});
	});
});

function getCheckBoxValue(uid) {
	var allInputCheckBox = document.getElementsByClassName("myCheckBox" + uid);

	var allSelectedAquirer = [];
	for (var i = 0; i < allInputCheckBox.length; i++) {

		if (allInputCheckBox[i].checked) {
			allSelectedAquirer.push(allInputCheckBox[i].value);
		}
	}
	var test = document.getElementById('selectBox' + uid);
	document.getElementById('selectBox' + uid).setAttribute('title', allSelectedAquirer.join());
	if (allSelectedAquirer.join().length > 28) {
		var res = allSelectedAquirer.join().substring(0, 27);
		document.querySelector("#selectBox" + uid + " option").innerHTML = res + '...............';
	} else if (allSelectedAquirer.join().length == 0) {
		document.querySelector("#selectBox" + uid + " option").innerHTML = 'ALL';
	} else {
		document.querySelector("#selectBox" + uid + " option").innerHTML = allSelectedAquirer.join();
	}
}


document.addEventListener('click', function handleClickOutsideBox(event) {
	const box = document.getElementById("checkboxes1");

  if (!box.contains(event.target)) {
    box.style.display = 'none';
  }
});
function showCheckboxes(e, uid) {
// 	var checkboxes = document.getElementById("checkboxes" + uid);
	
	var element = document.getElementById("checkboxes1");
    element.style.display = (element.style.display == 'none') ? 'block' : 'none';
// 	debugger
// 		if (uid == 1) {
// 			document.getElementById("checkboxes1").style.display = "block";
			
// 		}
// 		 else {
// 		checkboxes.style.display = "none";
// 		expanded = false;
// 	}
	
	
	e.stopPropagation();

}

		function myFunctions() {
			debugger
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

			
		}
	</script>
	<script src="../assets/plugins/custom/datatables/datatables.bundle.js"></script>

</body>
</html>