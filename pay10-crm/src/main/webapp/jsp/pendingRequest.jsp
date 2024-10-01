<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
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
	top: 45%;
	left: 50%;
	z-index: 100;
	width: 15%;
}

/* Style the tab */
div.tab {
	overflow: hidden;
	border: 1px solid #ccc;
	background-color: #f1f1f1;
}

/* Style the buttons inside the tab */
div.tab button {
	background-color: inherit;
	float: left;
	border: none;
	outline: none;
	cursor: pointer;
	padding: 14px 29px;
	transition: 0.3s;
	font-size: 14px;
	margin-bottom: 0px;
}

/* Change background color of buttons on hover */
div.tab button:hover {
	/*  background-color: #ddd; */
	/* background: #496cb6; */
	background: #f78000;
	/* color: #f78000; */
color: #ffffff; 
}

/* Create an active/current tablink class */
div.tab button.active {
	/* background-color: #ccc; */
	/* background: #496cb6; */
	background: #f78000;
	color: #ffffff;
}

/* Style the tab content */
.tabcontent {
	display: none;
	border: 0px solid #ccc;
	border-top: none;
}

.txnf {
	border: 0px solid #e4e4e4 !important;
	margin: 15px 0 0 !important;
}

.text-primary-pending {
	font-size: 19px;
	color: #428bca;
}

.otp-btn-fact {
	color: #428bca;
	width: 169px;
	height: 25px;
}

.otp-txt {
	width: 169px;
	height: 25px;
}


#UserProfile {
	overflow: scroll;
}
</style>
<title>Pending Request</title>



	<link rel="shortcut icon" href="assets/media/images/paylogo.svg" />
		<!--begin::Fonts-->
		<link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Inter:300,400,500,600,700" />
		<!--end::Fonts-->
		<!--begin::Vendor Stylesheets(used by this page)-->
		<link href="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.css" rel="stylesheet" type="text/css" />
		<link href="../assets/plugins/custom/datatables/datatables.bundle.css" rel="stylesheet" type="text/css" />

		<!--end::Vendor Stylesheets-->
		<!--begin::Global Stylesheets Bundle(used by all pages)-->
		<link href="../assets/plugins/global/plugins.bundle.css" rel="stylesheet" type="text/css" />
		<link href="../assets/css/style.bundle.css" rel="stylesheet" type="text/css" />
		<link href="../assets/css/custom/custom-style.css" rel="stylesheet" type="text/css" />

		<script src="../assets/plugins/global/plugins.bundle.js"></script>
		<script src="../assets/js/scripts.bundle.js"></script>
		<link href="../css/select2.min.css" rel="stylesheet" />
		<script src="../js/jquery.select2.js" type="text/javascript"></script>





<%-- 
<link href="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.css" rel="stylesheet" type="text/css" />
<link href="../assets/plugins/custom/datatables/datatables.bundle.css" rel="stylesheet" type="text/css" />
<link href="../assets/plugins/global/plugins.bundle.css" rel="stylesheet" type="text/css" />
<link href="../assets/css/style.bundle.css" rel="stylesheet" type="text/css" />
<link href="../assets/css/custom/custom-style.css" rel="stylesheet" type="text/css" />


<!-- <link href="../css/Jquerydatatableview.css" rel="stylesheet" />
<link href="../css/Jquerydatatable.css" rel="stylesheet" />
<link rel="stylesheet" href="../css/loader.css">
<link href="../css/default.css" rel="stylesheet" type="text/css" />
<link href="../css/jquery-ui.css" rel="stylesheet" /> -->

<script type="text/javascript" src="../js/jquery.min.js"></script>
<script src="../js/jquery.js"></script>
<script src="../js/jquery.dataTables.js"></script>
<script src="../js/jquery-ui.js"></script>

<script type="text/javascript" src="../js/moment.js"></script>
<script type="text/javascript" src="../js/daterangepicker.js"></script>

<!-- <link href="../css/loader.css" rel="stylesheet" type="text/css" /> -->

<script src="../js/jquery.popupoverlay.js"></script>
<script type="text/javascript" src="../js/dataTables.buttons.js"></script>
<script type="text/javascript" src="../js/pdfmake.js"></script>

<!-- <link href="../fonts/css/font-awesome.min.css" rel="stylesheet"> -->

<script src="../js/messi.js"></script>
<!-- <link href="../css/messi.css" rel="stylesheet" /> -->
<script src="../js/commanValidate.js"></script>

<script src="../js/core/popper.min.js"></script>
<script src="../js/core/bootstrap-material-design.min.js"></script>
<script src="../js/plugins/perfect-scrollbar.jquery.min.js"></script>
<!-- Control Center for Material Dashboard: parallax effects, scripts for the example pages etc -->
<script src="../js/material-dashboard.js?v=2.1.0" type="text/javascript"></script>
<script src="../assets/js/scripts.bundle.js"></script> --%>



<script type="text/javascript">
	
	$(document).ready(function() {
		document.getElementById("loading").style.display = "none";
		document.getElementById("loadingInnermerchSurchargeAccept").style.display = "none";
		document.getElementById("loadingInnermerchSurchargeReject").style.display = "none";
		document.getElementById("loadingInnerbankSurchargeAccept").style.display = "none";
		document.getElementById("loadingInnerbankSurchargeReject").style.display = "none";
		document.getElementById("loadingInnergstAccept").style.display = "none";
		document.getElementById("loadingInnergstReject").style.display = "none";
		document.getElementById("loadingInnersmartRouterAccept").style.display = "none";
		document.getElementById("loadingInnergsmartRouterReject").style.display = "none";
		

		$('.tabcontent').each(function() {
		    var dataRows = this.getElementsByClassName("boxtext")
		    if(dataRows.length <1 ){
		        $(this).empty();
		    }
		});	
		
		var userType = "<s:property value='%{#session.USER.UserType.name()}'/>";
		var permission = "<s:property value='%{#session.USER_PERMISSION}'/>";
	

	if (permission.includes("Create Surcharge") || userType == 'ADMIN'){

			document.getElementsByName("surchargeBtn").forEach(elem => {
				elem.disabled = false;
			});
	}

	else{
		
			document.getElementsByName("surchargeBtn").forEach(elem => {
				elem.disabled = true;
			});
	}

	if (permission.includes("Create Service Tax") || userType == 'ADMIN' ){

		document.getElementsByName("gstBtn").forEach(elem => {
				elem.disabled = false;
			});
	}

	else{

			document.getElementsByName("gstBtn").forEach(elem => {
				elem.disabled = true;
			});
	}

	if (permission.includes("Smart Router") || userType == 'ADMIN' ){

			document.getElementsByName("smartRouterBtn").forEach(elem => {
				elem.disabled = false;
			});

	}

	else{

			document.getElementsByName("smartRouterBtn").forEach(elem => {
				elem.disabled = true;
			});
	}	


		
	});
	
	function generateOtp(param) {
		
		
		
		document.getElementById("loadingInnermerchSurchargeAccept").style.display = "none";
		document.getElementById("loadingInnermerchSurchargeReject").style.display = "none";
		document.getElementById("loadingInnerbankSurchargeAccept").style.display = "none";
		document.getElementById("loadingInnerbankSurchargeReject").style.display = "none";
		document.getElementById("loadingInnergstAccept").style.display = "none";
		document.getElementById("loadingInnergstReject").style.display = "none";
		document.getElementById("loadingInnersmartRouterAccept").style.display = "none";
		document.getElementById("loadingInnergsmartRouterReject").style.display = "none";
		
		
		var dataParam = param;
		var idArray = '';
		var userType = "<s:property value='%{#session.USER.UserType.name()}'/>";
		var permission = "<s:property value='%{#session.USER_PERMISSION}'/>";
		
		
		var isGranted = false;
	   
		document.getElementById("otpMerchSurValaccept").value = '';
		document.getElementById("otpMerchSurValreject").value = '';
		document.getElementById("otpBankSurValaccept").value = '';
		document.getElementById("otpBankSurValreject").value = '';
		document.getElementById("otpgstaccept").value = '';
		document.getElementById("otpgstreject").value = '';
		document.getElementById("otpsmartRouteraccept").value = '';
		document.getElementById("otpsmartRouterreject").value = '';
		
		if (dataParam == "surchargeDetails" ){
			
			isGranted =  permission.includes("Create Surcharge");
			var row = document.getElementById("merchantSurchargeTable").rows;
			
			for (i = 1; i < row.length; i++) { 
				 
				 var id = document.getElementById("merchantSurchargeTable").rows[i].cells[0].innerText;
				 var checkbox = document.getElementById("merchSurChkBox"+i).checked;
				 
				 if (checkbox){
					 idArray = idArray+id+",";
				 }
				 
				}
				
		}
		
		
		if (dataParam == "surcharge"){
			
			 isGranted =  permission.includes("Create Surcharge");
			var row = document.getElementById("bankSurchargeTable").rows;
		
		for (i = 1; i < row.length; i++) { 
			 
			 var paymentType = document.getElementById("bankSurchargeTable").rows[i].cells[1].innerText;
			  var payId = document.getElementById("bankSurchargeTable").rows[i].cells[3].innerText;
			   var mopType = document.getElementById("bankSurchargeTable").rows[i].cells[4].innerText;
			    var acquirer = document.getElementById("bankSurchargeTable").rows[i].cells[5].innerText;
				 var paymentsRegion = document.getElementById("bankSurchargeTable").rows[i].cells[6].innerText;
				  
				  var dataCombo = paymentType+","+payId+","+mopType+","+acquirer+","+paymentsRegion;
			 var checkbox = document.getElementById("bankSurChkBox"+i).checked;
			 
			 if (checkbox){
				 idArray = idArray+dataCombo+";";
			 }
			 
			}
			
		}
		
		if (dataParam == "smartRouter"){
		
		 isGranted =  permission.includes("Smart Router");
		 var row = document.getElementById("smartRouterTable").rows;
			
				for (i = 1; i < row.length; i++) { 
				 
				 var id = document.getElementById("smartRouterTable").rows[i].cells[0].innerText;
				 var checkbox = document.getElementById("smartRouterChkBox"+i).checked;
				 
				 if (checkbox){
					 idArray = idArray+id+",";
				 }
				 
				}
		}
		
		if (dataParam == "gst"){
			
			 isGranted =  permission.includes("Create Service Tax");
				var row = document.getElementById("gstTable").rows;
			
				for (i = 1; i < row.length; i++) { 
				 
				 var id = document.getElementById("gstTable").rows[i].cells[0].innerText;
				 var checkbox = document.getElementById("gstChkBox"+i).checked;
				 
				 if (checkbox){
					 idArray = idArray+id+",";
				 }
				 
				}
			
		}
		
		
		if (idArray == ''){
			
			alert("Select Atleast one Request to generate OTP");
			return false;
		}
		
		
		if (userType == "ADMIN" || isGranted){
		}
		else{
			alert("Permission denied ! Only Admin can approve/reject pending requests");
			return;
		}
		var token = document.getElementsByName("token")[0].value;
		
		
		document.getElementById("loadingInnermerchSurchargeAccept").style.display = "block";
		document.getElementById("loadingInnermerchSurchargeReject").style.display = "block";
		document.getElementById("loadingInnerbankSurchargeAccept").style.display = "block";
		document.getElementById("loadingInnerbankSurchargeReject").style.display = "block";
		document.getElementById("loadingInnergstAccept").style.display = "block";
		document.getElementById("loadingInnergstReject").style.display = "block";
		document.getElementById("loadingInnersmartRouterAccept").style.display = "block";
		document.getElementById("loadingInnergsmartRouterReject").style.display = "block";
		
		 $.ajax({
			type: "POST",
			url:"generateVerificationOtp",
			timeout : 0,
			data:{"dataParam":dataParam,"idArray":idArray,"token":token,"struts.token.name": "token",},
			success:function(data){
				document.getElementById("loadingInnermerchSurchargeAccept").style.display = "none";
		document.getElementById("loadingInnermerchSurchargeReject").style.display = "none";
		document.getElementById("loadingInnerbankSurchargeAccept").style.display = "none";
		document.getElementById("loadingInnerbankSurchargeReject").style.display = "none";
		document.getElementById("loadingInnergstAccept").style.display = "none";
		document.getElementById("loadingInnergstReject").style.display = "none";
		document.getElementById("loadingInnersmartRouterAccept").style.display = "none";
		document.getElementById("loadingInnergsmartRouterReject").style.display = "none";
				var response = ((data["Invalid request"] != null) ? (data["Invalid request"].response[0]) : (data.response));
				if(null!=response){
					alert(response);			
				}
				
		    },
			error:function(data){
				document.getElementById("loadingInnermerchSurchargeAccept").style.display = "none";
		document.getElementById("loadingInnermerchSurchargeReject").style.display = "none";
		document.getElementById("loadingInnerbankSurchargeAccept").style.display = "none";
		document.getElementById("loadingInnerbankSurchargeReject").style.display = "none";
		document.getElementById("loadingInnergstAccept").style.display = "none";
		document.getElementById("loadingInnergstReject").style.display = "none";
		document.getElementById("loadingInnersmartRouterAccept").style.display = "none";
		document.getElementById("loadingInnergsmartRouterReject").style.display = "none";
				alert("Network error, OTP not sent");
			}
		    
		}); 
		
	}
	
	
	function merchantSurchargeApproveReject(operation) {
		
		var idArray = '';
		var otp = document.getElementById("otpMerchSurVal"+operation).value;
		
		if (otp == ''){
			
			alert("Please enter correct OTP to submit request");
			return false;
		}
		
		if (otp.length != 6 ){
			
			alert("Please enter correct OTP to submit request");
			return false;
		}
		
		if (otp.includes('=') || otp.includes('+')  || otp.includes('-')){
			
			alert("Only Numbers are allowed in OTP");
			return false;
		}
		
		
		var row = document.getElementById("merchantSurchargeTable").rows;
		
		for (i = 1; i < row.length; i++) { 
			 
			 var id = document.getElementById("merchantSurchargeTable").rows[i].cells[0].innerText;
			 var checkbox = document.getElementById("merchSurChkBox"+i).checked;
			 
			 if (checkbox){
				 idArray = idArray+id+",";
			 }
			 
			}
				
		if (idArray == ''){
	
			alert("Please select atleast one request before submission");
			return false;
		}
				
		var token = document.getElementsByName("token")[0].value;
		var userType = "<s:property value='%{#session.USER.UserType.name()}'/>";
		var permission = "<s:property value='%{#session.USER_PERMISSION}'/>";
		var isGranted = permission.includes("Create Surcharge");
		
		if (userType == "ADMIN" || isGranted){
		}
		else{
			alert("Permission denied ! Only Admin can approve/reject pending requests");
			return;
		}
		
		
		
		document.getElementById("loading").style.display = "block";
		 $.ajax({
			type: "POST",
			url:"merchantSurchargeApproveReject",
			timeout : 0,
			data:{"otp":otp,"idArray":idArray,"operation":operation,"token":token,"struts.token.name": "token",},
			success:function(data){
				document.getElementById("loading").style.display = "none";
				var response = ((data["Invalid request"] != null) ? (data["Invalid request"].response[0]) : (data.response));
				if(null!=response){
					alert(response);			
				}
			
				window.location.reload();
		    },
			error:function(data){
				document.getElementById("loading").style.display = "none";
				alert("Network error, surcharge may not be saved");
			}
		    
		}); 
	}
	
					
	
	function gstApproveReject(operation) {
		
		var idArray = '';
		var otp = document.getElementById("otpgstVal"+operation).value;
		
		if (otp == ''){
			
			alert("Please enter correct OTP to submit request");
			return false;
		}
		
		if (otp.length != 6 ){
			
			alert("Please enter correct OTP to submit request");
			return false;
		}
		
		
		if (otp.includes('=') || otp.includes('+')  || otp.includes('-')){
			
			alert("Only Numbers are allowed in OTP");
			return false;
		}
		
		var row = document.getElementById("gstTable").rows;
		
		for (i = 1; i < row.length; i++) { 
			 
			 var id = document.getElementById("gstTable").rows[i].cells[0].innerText;
			 var checkbox = document.getElementById("gstChkBox"+i).checked;
			 
			 if (checkbox){
				 idArray = idArray+id+",";
			 }
			 
			}
				
		if (idArray == ''){
	
			alert("Please select atleast one request before submission");
			return false;
		}
				
		var token = document.getElementsByName("token")[0].value;
		var userType = "<s:property value='%{#session.USER.UserType.name()}'/>";
		var permission = "<s:property value='%{#session.USER_PERMISSION}'/>";
		var isGranted = permission.includes("Create Service Tax");
		
		if (userType == "ADMIN" || isGranted){
		}
		else{
			alert("Permission denied ! Only Admin / Authorized Sub-Admin can approve/reject pending requests");
			return;
		}
		
		
		
		document.getElementById("loading").style.display = "block";
		 $.ajax({
			type: "POST",
			url:"approveRejectGst",
			timeout : 0,
			data:{"otp":otp,"idArray":idArray,"operation":operation,"token":token,"struts.token.name": "token",},
			success:function(data){
				document.getElementById("loading").style.display = "none";
				var response = ((data["Invalid request"] != null) ? (data["Invalid request"].response[0]) : (data.response));
				if(null!=response){
					alert(response);			
				}
			
				window.location.reload();
		    },
			error:function(data){
				document.getElementById("loading").style.display = "none";
				alert("Network error, GST may not be saved");
			}
		    
		}); 
	}
	
	
	
	
function smartRouterApproveReject(operation)  {
	
	var idArray = '';
		var otp = document.getElementById("otpsmartRouterVal"+operation).value;
		
		if (otp == ''){
			
			alert("Please enter correct OTP to submit request");
			return false;
		}
		
		if (otp.length != 6 ){
			
			alert("Please enter correct OTP to submit request");
			return false;
		}
		
		
		if (otp.includes('=') || otp.includes('+')  || otp.includes('-')){
			
			alert("Only Numbers are allowed in OTP");
			return false;
		}
		
		var row = document.getElementById("smartRouterTable").rows;
		
		for (i = 1; i < row.length; i++) { 
			 
			 var id = document.getElementById("smartRouterTable").rows[i].cells[0].innerText;
			 var checkbox = document.getElementById("smartRouterChkBox"+i).checked;
			 
			 if (checkbox){
				 idArray = idArray+id+",";
			 }
			 
			}
				
		if (idArray == ''){
	
			alert("Please select atleast one request before submission");
			return false;
		}
				
		var token = document.getElementsByName("token")[0].value;
		var userType = "<s:property value='%{#session.USER.UserType.name()}'/>";
		var permission = "<s:property value='%{#session.USER_PERMISSION}'/>";
		var isGranted = permission.includes("Smart Router");
		
		if (userType == "ADMIN" || isGranted){
		}
		else{
			alert("Permission denied ! Only Admin / Authorized Sub-Admin can approve/reject pending requests");
			return;
		}	
		
		document.getElementById("loading").style.display = "block";
		 $.ajax({
			type: "POST",
			url:"approveRejectSmartRouter",
			timeout : 0,
			data:{"otp":otp,"idArray":idArray,"operation":operation,"token":token,"struts.token.name": "token",},
			success:function(data){
				document.getElementById("loading").style.display = "none";
				var response = ((data["Invalid request"] != null) ? (data["Invalid request"].response[0]) : (data.response));
				if(null!=response){
					alert(response);			
				}
			
				window.location.reload();
		    },
			error:function(data){
				document.getElementById("loading").style.display = "none";
				alert("Network error, GST may not be saved");
			}
		    
		}); 
}

function bankSurchargeApproveReject(operation) {
		
		var idArray = '';
		var otp = document.getElementById("otpBankSurVal"+operation).value;
		
		if (otp == ''){
			
			alert("Please enter correct OTP to submit request");
			return false;
		}
		
		if (otp.length != 6 ){
			
			alert("Please enter correct OTP to submit request");
			return false;
		}
		
		if (otp.includes('=') || otp.includes('+')  || otp.includes('-')){
			
			alert("Only Numbers are allowed in OTP");
			return false;
		}
		
		var row = document.getElementById("bankSurchargeTable").rows;
		
		for (i = 1; i < row.length; i++) { 
			 
			 var paymentType = document.getElementById("bankSurchargeTable").rows[i].cells[1].innerText;
			  var payId = document.getElementById("bankSurchargeTable").rows[i].cells[3].innerText;
			   var mopType = document.getElementById("bankSurchargeTable").rows[i].cells[4].innerText;
			    var acquirer = document.getElementById("bankSurchargeTable").rows[i].cells[5].innerText;
				 var paymentsRegion = document.getElementById("bankSurchargeTable").rows[i].cells[6].innerText;
				  
				  var dataCombo = paymentType+","+payId+","+mopType+","+acquirer+","+paymentsRegion;
			 var checkbox = document.getElementById("bankSurChkBox"+i).checked;
			 
			 if (checkbox){
				 idArray = idArray+dataCombo+";";
			 }
			 
			}
				
		if (idArray == ''){
	
			alert("Please select atleast one request before submission");
			return false;
		}
				
		var token = document.getElementsByName("token")[0].value;
		var userType = "<s:property value='%{#session.USER.UserType.name()}'/>";
		var permission = "<s:property value='%{#session.USER_PERMISSION}'/>";
		var isGranted = permission.includes("Create Surcharge");
		
		if (userType == "ADMIN" || isGranted){
		}
		else{
			alert("Permission denied ! Only Authorized User can approve/reject pending requests");
			return;
		}
		
		
		document.getElementById("loading").style.display = "block";
		 $.ajax({
			type: "POST",
			url:"approveRejectBankSurcharge",
			timeout : 0,
			data:{"otp":otp,"idArray":idArray,"operation":operation,"token":token,"struts.token.name": "token",},
			success:function(data){
				document.getElementById("loading").style.display = "none";
				var response = ((data["Invalid request"] != null) ? (data["Invalid request"].response[0]) : (data.response));
				if(null!=response){
					alert(response);			
				}
			
				window.location.reload();
		    },
			error:function(data){
				document.getElementById("loading").style.display = "none";
				alert("Network error, surcharge may not be updated");
			}
		    
		}); 
	}
	


	
</script>
</head>
<body id="mainBody">
<div class="content d-flex flex-column flex-column-fluid" id="kt_content">
<div class="toolbar" id="kt_toolbar">
							<!--begin::Container-->
							<div id="kt_toolbar_container" class="container-fluid d-flex flex-stack">
								<!--begin::Page title-->
								<div data-kt-swapper="true" data-kt-swapper-mode="prepend" data-kt-swapper-parent="{default: '#kt_content_container', 'lg': '#kt_toolbar_container'}" class="page-title d-flex align-items-center me-3 flex-wrap mb-5 mb-lg-0 lh-1">
									<!--begin::Title-->
									<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">Pending Requests</h1>
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
										<li class="breadcrumb-item text-muted">Merchant Billing</li>
										<!--end::Item-->
										<!--begin::Item-->
										<li class="breadcrumb-item">
											<span class="bullet bg-gray-200 w-5px h-2px"></span>
										</li>
										<!--end::Item-->
										<!--begin::Item-->
										<li class="breadcrumb-item text-dark">Pending Requests</li>
										<!--end::Item-->
									</ul>
									<!--end::Breadcrumb-->
								</div>
								<!--end::Page title-->
								
							</div>
							<!--end::Container-->
						</div>

	<div id="loading">
		<img id="loading-image" src="../image/loader_Inner.gif"
			alt="Loading..." />
	</div>

	<!-- <h2 class="pageHeading">Pending Requests</h2> -->

	<div class="tab">

		<button class="tablinks"
			onclick="selectTab(event, 'MerchantSurcharge')">Merchant
			Surcharge</button>
		<button class="tablinks" onclick="selectTab(event, 'BankSurcharge')">Bank
			Surcharge</button>
		<button class="tablinks" onclick="selectTab(event, 'ServiceTax')">GST</button>
		<button class="tablinks" onclick="selectTab(event, 'SmartRouter')">Smart
			Router</button>
	</div>


	<script>
	
function selectTab(evt, opName) {
    var i, tabcontent, tablinks;
    tabcontent = document.getElementsByClassName("tabcontent");
    for (i = 0; i < tabcontent.length; i++) {
        tabcontent[i].style.display = "none";
    }
    tablinks = document.getElementsByClassName("tablinks");
    for (i = 0; i < tablinks.length; i++) {
        tablinks[i].className = tablinks[i].className.replace(" active", "");
    }
    document.getElementById(opName).style.display = "block";
    evt.currentTarget.className += " active";
}

function checkUserPermission(){
	var permission = "<s:property value='%{#session.USER_PERMISSION.EmailId}'/>";
}

var surchargeDetailsOperationVal = '';

$('#submitBtn').click(function() {
     
});

$('#submit').click(function(){
     /* when the submit button in the modal is clicked, submit the form */
    alert('submitting');
   // $('#formfield').submit();
});

</script>

	<div class="modal fade" id="merchSurchargeAccept" tabindex="-1"
		role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		<div id="loadingInnermerchSurchargeAccept" display="none">
			<img id="loading-image" src="../image/loader_Inner.gif"
				alt="Sending OTP..." />
		</div>
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header" align="center">Confirm Merchant
					Surcharge Approval</div>
				<div class="modal-body">
					Please generate an OTP and submit.


					<table class="table">
						<tr>
							<th>Generate OTP</th>
							<td id="otpMerchSurBtn">
								<button class="otp-btn-fact" name="surchargeDetails"
									id="surchargeDetailsaccept" onClick='generateOtp(this.name)'>
									Generate OTP</button>
							</td>
						</tr>
						<tr>
							<th>Enter OTP</th>
							<td id="otpMerchSuraccept"><input id="otpMerchSurValaccept"
								class="otp-txt" type="number" name="merchSurchargeOtp" min="6"
								max="6" name="otpMerchSuraccept"></td>
						</tr>
					</table>

				</div>

				<div align="center">
					<button type="button" name="updatePendingBtn" disabled="true" class="btn btn-lg btn-success"
						onClick='merchantSurchargeApproveReject("accept")'>Submit</button>
					<button type="button" class="btn btn-lg btn-danger"
						data-dismiss="modal">Cancel</button>
				</div>
			</div>
		</div>
	</div>






	<div class="modal fade" id="merchSurchargeReject" tabindex="-1"
		role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		<div id="loadingInnermerchSurchargeReject" display="none">
			<img id="loading-image" src="../image/loader_Inner.gif"
				alt="Sending OTP..." />
		</div>
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header" align="center">Confirm Merchant
					Surcharge Rejection</div>
				<div class="modal-body">
					Please generate an OTP and submit.


					<table class="table">
						<tr>
							<th>Generate OTP</th>
							<td id="otpMerchSurBtnreject">
								<button class="otp-btn-fact" name="surchargeDetails"
									id="surchargeDetailsreject" onClick='generateOtp(this.name)'>
									Generate OTP</button>
							</td>
						</tr>
						<tr>
							<th>Enter OTP</th>
							<td id="otpMerchSurreject"><input id="otpMerchSurValreject"
								class="otp-txt" type="number" name="merchSurchargeOtpreject"
								min="6" max="6" name="otpMerchSurreject"></td>
						</tr>
					</table>

				</div>

				<div align="center">
					<button type="button" class="btn btn-lg btn-success" name="updatePendingBtn" disabled="true"
						onClick='merchantSurchargeApproveReject("reject")'>Submit</button>
					<button type="button" class="btn btn-lg btn-danger"
						data-dismiss="modal">Cancel</button>
				</div>
			</div>
		</div>
	</div>


	<div class="modal fade" id="bankSurchargeAccept" tabindex="-1"
		role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		<div id="loadingInnerbankSurchargeAccept" display="none">
			<img id="loading-image" src="../image/loader_Inner.gif"
				alt="Sending OTP..." />
		</div>
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header" align="center">Confirm Bank
					Surcharge Approval</div>
				<div class="modal-body">
					Please generate an OTP and submit.


					<table class="table">
						<tr>
							<th>Generate OTP</th>
							<td id="otpBankSurBtn">
								<button class="otp-btn-fact" name="surcharge"
									id="surchargeaccept" onClick='generateOtp(this.name)'>
									Generate OTP</button>
							</td>
						</tr>
						<tr>
							<th>Enter OTP</th>
							<td id="otpBankSuraccept"><input id="otpBankSurValaccept"
								class="otp-txt" type="number" name="bankSurchargeOtp" min="6"
								max="6" name="otpBankSuraccept"></td>
						</tr>
					</table>

				</div>

				<div align="center">
					<button type="button" class="btn btn-lg btn-success" name="updatePendingBtn" disabled="true"
						onClick='bankSurchargeApproveReject("accept")'>Submit</button>
					<button type="button" class="btn btn-lg btn-danger"
						data-dismiss="modal">Cancel</button>
				</div>
			</div>
		</div>
	</div>






	<div class="modal fade" id="bankSurchargeReject" tabindex="-1"
		role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		<div id="loadingInnerbankSurchargeReject" display="none">
			<img id="loading-image" src="../image/loader_Inner.gif"
				alt="Sending OTP..." />
		</div>
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header" align="center">Confirm Bank
					Surcharge Rejection</div>
				<div class="modal-body">
					Please generate an OTP and submit.


					<table class="table">
						<tr>
							<th>Generate OTP</th>
							<td id="otpBankSurBtnreject">
								<button class="otp-btn-fact" name="surcharge"
									id="surchargereject" onClick='generateOtp(this.name)'>
									Generate OTP</button>
							</td>
						</tr>
						<tr>
							<th>Enter OTP</th>
							<td id="otpBankSurreject"><input id="otpBankSurValreject"
								class="otp-txt" type="number" name="bankSurchargeOtpreject"
								min="6" max="6" name="otpBankSurreject"></td>
						</tr>
					</table>

				</div>

				<div align="center">
					<button type="button" class="btn btn-lg btn-success" name="updatePendingBtn" disabled="true"
						onClick='bankSurchargeApproveReject("reject")'>Submit</button>
					<button type="button" class="btn btn-lg btn-danger"
						data-dismiss="modal">Cancel</button>
				</div>
			</div>
		</div>
	</div>


	<div class="modal fade" id="gstAccept" tabindex="-1" role="dialog"
		aria-labelledby="myModalLabel" aria-hidden="true">
		<div id="loadingInnergstAccept" display="none">
			<img id="loading-image" src="../image/loader_Inner.gif"
				alt="Sending OTP..." />
		</div>
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header" align="center">Confirm GST Approval
				</div>
				<div class="modal-body">
					Please generate an OTP and submit.


					<table class="table">
						<tr>
							<th>Generate OTP</th>
							<td id="otpgstBtn">
								<button class="otp-btn-fact" name="gst" id="gstaccept" 
									onClick='generateOtp(this.name)'>Generate OTP</button>
							</td>
						</tr>
						<tr>
							<th>Enter OTP</th>
							<td id="otpgstaccept"><input id="otpgstValaccept"
								class="otp-txt" type="number" name="gstOtp" min="6" max="6"
								name="otpBankSuraccept"></td>
						</tr>
					</table>

				</div>

				<div align="center">
					<button type="button" class="btn btn-lg btn-success" name="updatePendingBtn" disabled="true"
						onClick='gstApproveReject("accept")'>Submit</button>
					<button type="button" class="btn btn-lg btn-danger"
						data-dismiss="modal">Cancel</button>
				</div>
			</div>
		</div>
	</div>






	<div class="modal fade" id="gstReject" tabindex="-1" role="dialog"
		aria-labelledby="myModalLabel" aria-hidden="true">
		<div id="loadingInnergstReject" display="none">
			<img id="loading-image" src="../image/loader_Inner.gif"
				alt="Sending OTP..." />
		</div>
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header" align="center">Confirm GST Rejection
				</div>
				<div class="modal-body">
					Please generate an OTP and submit.


					<table class="table">
						<tr>
							<th>Generate OTP</th>
							<td id="otpgstBtnreject">
								<button class="otp-btn-fact" name="gst" id="gstreject"
									onClick='generateOtp(this.name)'>Generate OTP</button>
							</td>
						</tr>
						<tr>
							<th>Enter OTP</th>
							<td id="otpgstreject"><input id="otpgstValreject"
								class="otp-txt" type="number" name="gstOtpreject" min="6"
								max="6" name="otpgstreject"></td>
						</tr>
					</table>

				</div>

				<div align="center">
					<button type="button" class="btn btn-lg btn-success" name="updatePendingBtn" disabled="true"
						onClick='gstApproveReject("reject")'>Submit</button>
					<button type="button" class="btn btn-lg btn-danger"
						data-dismiss="modal">Cancel</button>
				</div>
			</div>
		</div>
	</div>


	<div class="modal fade" id="smartRouterAccept" tabindex="-1"
		role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		<div id="loadingInnersmartRouterAccept" display="none">
			<img id="loading-image" src="../image/loader_Inner.gif"
				alt="Sending OTP..." />
		</div>
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header" align="center">Confirm Smart Router
					Approval</div>
				<div class="modal-body">
					Please generate an OTP and submit.


					<table class="table">
						<tr>
							<th>Generate OTP</th>
							<td id="otpsmartRouterBtn">
								<button class="otp-btn-fact" name="smartRouter"
									id="smartRouteraccept" onClick='generateOtp(this.name)'>
									Generate OTP</button>
							</td>
						</tr>
						<tr>
							<th>Enter OTP</th>
							<td id="otpsmartRouteraccept"><input
								id="otpsmartRouterValaccept" class="otp-txt" type="number"
								name="smartRouterOtp" min="6" max="6"
								name="otpsmartRouteraccept"></td>
						</tr>
					</table>

				</div>

				<div align="center">
					<button type="button" class="btn btn-lg btn-success" name="updatePendingBtn" disabled="true"
						onClick='smartRouterApproveReject("accept")'>Submit</button>
					<button type="button" class="btn btn-lg btn-danger"
						data-dismiss="modal">Cancel</button>
				</div>
			</div>
		</div>
	</div>






	<div class="modal fade" id="smartRouterReject" tabindex="-1"
		role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
		<div id="loadingInnergsmartRouterReject" display="none">
			<img id="loading-image" src="../image/loader_Inner.gif"
				alt="Sending OTP..." />
		</div>
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header" align="center">Confirm Smart Router
					Rejection</div>
				<div class="modal-body">
					Please generate an OTP and submit.


					<table class="table">
						<tr>
							<th>Generate OTP</th>
							<td id="otpsmartRouterBtnreject">
								<button class="otp-btn-fact" name="smartRouter"
									id="smartRouterreject" onClick='generateOtp(this.name)'>
									Generate OTP</button>
							</td>
						</tr>
						<tr>
							<th>Enter OTP</th>
							<td id="otpsmartRouterreject"><input
								id="otpsmartRouterValreject" class="otp-txt" type="number"
								name="smartRouterOtpreject" min="6" max="6"
								name="otpsmartRouterreject"></td>
						</tr>
					</table>

				</div>

				<div align="center">
					<button type="button" class="btn btn-lg btn-success" name="updatePendingBtn" disabled="true"
						onClick='smartRouterApproveReject("reject")'>Submit</button>
					<button type="button" class="btn btn-lg btn-danger"
						data-dismiss="modal">Cancel</button>
				</div>
			</div>
		</div>
	</div>

	<s:form id="pendingDetailsForm" action="pendingDetailsFormAction"
		method="post">

		<table width="100%" border="0" cellspacing="0" cellpadding="0"
			class="">
			<tr>
				<td align="center" valign="top"><table width="100%" border="0"
						cellspacing="0" cellpadding="0">
						<tr>
							<br>
							<br>
							<td align="left"><div id="MerchantSurcharge"
									class="tabcontent">
									<s:iterator value="surchargeMerchantData" status="pay">
										<span class="text-primary-pending"><s:property
												value="key" /></span>
										<div id="<s:property value="key" />Div">
											<table id="merchantSurchargeTable" width="100%" border="0"
												align="center" class="product-spec">
												<tr class="boxheading">
													<th width="8%" align="left" valign="middle">Id</th>
													<th width="8%" align="left" valign="middle">Requested
														By</th>
													<th width="8%" align="left" valign="middle">Merchant
														Name</th>
													<th width="8%" align="left" valign="middle">Payment
														Region</th>
													<th width="8%" align="left" valign="middle">pay Id</th>
													<th width="8%" align="left" valign="middle">Payment
														Type</th>
													<th width="8%" align="left" valign="middle">Surcharge
														%</th>
													<th width="7%" align="left" valign="middle">Surcharge
														FC</th>
													<th width="7%" align="left" valign="middle">Min Txn
														Amount FC</th>
													<th width="7%" align="left" valign="middle">Status</th>


													<th width="2%" align="left" valign="middle">Select</th>
												</tr>
												<s:iterator value="value" status="itStatus">
													<tr class="boxtext">
														<td align="left" valign="middle"><s:property
																value="id" /></td>
														<td align="left" valign="middle"><s:property
																value="requestedBy" /></td>
														<td align="left" valign="middle"><s:property
																value="merchantName" /></td>
														<td align="left" valign="middle"><s:property
																value="paymentsRegion" /></td>
														<td align="left" valign="middle"><s:property
																value="payId" /></td>
														<td align="left" valign="middle"><s:property
																value="paymentType" /></td>
														<td align="left" valign="middle"><s:property
																value="surchargePercentage" /></td>
														<td align="left" valign="middle"><s:property
																value="surchargeAmount" /></td>
														<td align="left" valign="middle"><s:property
																value="minTransactionAmount" /></td>
														<td align="left" valign="middle"><s:property
																value="status" /></td>
														<td align="center" valign="middle"><s:textfield
																id="merchSurChkBox%{#itStatus.count}" value=""
																type="checkbox"></s:textfield></td>

													</tr>
												</s:iterator>
											</table>
										</div>
									</s:iterator>
									<br>
									<div align="center" valign="middle">
										<input type="button" name="surchargeBtn" value="Approve"
											data-toggle="modal" data-target="#merchSurchargeAccept"
											class="btn btn-lg btn-success" /> <input type="button"
											name="surchargeBtn" value="Reject" data-toggle="modal"
											data-target="#merchSurchargeReject"
											class="btn btn-lg btn-danger" />
									</div>

								</div></td>
						</tr>
					</table></td>
			</tr>
		</table>

		<table width="100%" border="0" cellspacing="0" cellpadding="0"
			class="">
			<tr>
				<td align="center" valign="top"><table width="100%" border="0"
						cellspacing="0" cellpadding="0">
						<tr>
							<td align="left">
								<div id="BankSurcharge" class="tabcontent">
									<s:iterator value="surchargePGData" status="pay">
										<br>
										<span class="text-primary-pending" id="test"><s:property
												value="key" /></span>
										<br>
										<div class="scrollD">
											<div id="<s:property value="key" />Div">
												<table width="100%" border="0" align="center"
													class="product-spec" id="bankSurchargeTable">
													<tr class="boxheading">

														<th width="4%" align="left" valign="middle">Requested
															By</th>
														<th width="4%" align="left" valign="middle">Payment
															Type</th>
														<th width="6%" align="left" valign="middle">Merchant
															Name</th>
														<th width="6%" align="left" valign="middle">Pay Id</th>
														<th width="4%" align="left" valign="middle">MOP Type</th>
														<th width="4%" align="left" valign="middle">Acquirer</th>
														<th width="4%" align="left" valign="middle">Region</th>
														<th width="4%" align="left" valign="middle">Bank
															%(Consumer)</th>
														<th width="4%" align="left" valign="middle">Bank
															%(Commercial)</th>
														<th width="4%" align="left" valign="middle">Bank
															FC(Consumer)</th>
														<th width="4%" align="left" valign="middle">Bank
															FC(Commercial)</th>
														<th width="4%" align="left" valign="middle">OnUs/OffUs</th>
														<th width="4%" align="left" valign="middle">Status</th>
														<th width="2%" align="left" valign="middle">Select</th>

													</tr>
													<s:iterator value="value" status="itStatus">
														<tr class="boxtext">


															<td align="left" valign="middle"><s:property
																	value="requestedBy" /></td>
															<td align="left" valign="middle"><s:property
																	value="paymentType" /></td>
															<td align="left" valign="middle"><s:property
																	value="merchantName" /></td>
															<td align="left" valign="middle"><s:property
																	value="payId" /></td>
															<td align="left" valign="middle"><s:property
																	value="mopType" /></td>
															<td align="left" valign="middle"><s:property
																	value="acquirerName" /></td>
															<td align="left" valign="middle"><s:property
																	value="paymentsRegion" /></td>
															<td align="left" valign="middle" class="nomarpadng"><div
																	title="Surcharge on us">
																	&nbsp;
																	<s:property value="bankSurchargePercentageOnCustomer" />
																</div>
																<div class="cellborder"
																	title="Customer Surcharge off us">
																	&nbsp;
																	<s:property value="0" />
																</div></td>

															<td align="left" valign="middle" class="nomarpadng"><div
																	title="Surcharge on us">
																	&nbsp;
																	<s:property value="bankSurchargePercentageOnCommercial" />
																</div>
																<div class="cellborder"
																	title="Commercial Surcharge off us">
																	&nbsp;
																	<s:property value="0" />
																</div></td>

															<td align="left" valign="middle" class="nomarpadng"><div
																	title="Surcharge on us">
																	&nbsp;
																	<s:property value="bankSurchargeAmountOnCustomer" />
																</div>
																<div class="cellborder"
																	title="Customer Surcharge off us">
																	&nbsp;
																	<s:property value="0" />
																</div></td>

															<td align="left" valign="middle" class="nomarpadng"><div
																	title="Surcharge on us">
																	&nbsp;
																	<s:property value="bankSurchargeAmountOnCommercial" />
																</div>
																<div class="cellborder"
																	title="Commercial Surcharge off us">
																	&nbsp;
																	<s:property value="0" />
																</div></td>

															<td align="center" valign="middle"><s:checkbox
																	disabled="true" name="allowOnOff" value="allowOnOff"
																	onclick="return false" /></td>

															<td align="left" valign="middle"><s:property
																	value="status" /></td>
															<td align="center" valign="middle"><s:textfield
																	id="bankSurChkBox%{#itStatus.count}" value=""
																	type="checkbox"></s:textfield></td>

														</tr>
													</s:iterator>
												</table>
											</div>
										</div>
									</s:iterator>
									<br>
									<div align="center" valign="middle">
										<input type="button" name="surchargeBtn" value="Approve"
											data-toggle="modal" data-target="#bankSurchargeAccept"
											class="btn btn-lg btn-success" /> <input type="button"
											name="surchargeBtn" value="Reject" data-toggle="modal"
											data-target="#bankSurchargeReject"
											class="btn btn-lg btn-danger" />
									</div>
								</div>
							</td>
						</tr>
					</table></td>
			</tr>
		</table>

		<table width="100%" border="0" cellspacing="0" cellpadding="0"
			class="">
			<tr>
				<td align="left"><div id="ServiceTax" class="tabcontent">
						<s:iterator value="serviceTaxData" status="pay">
							<span class="text-primary-pending"><s:property
									value="key" /></span>
							<div id="<s:property value="key" />Div">
								<table width="100%" border="0" align="center"
									class="product-spec" id="gstTable">
									<tr class="boxheading">
										<th width="4%" align="left" valign="middle">Id</th>
										<th width="4%" align="left" valign="middle">Business Type</th>
										<th width="6%" align="left" valign="middle">GST</th>
										<th width="6%" align="left" valign="middle">Requested By</th>
										<th width="4%" align="left" valign="middle">Status</th>
										<th width="2%" align="left" valign="middle">Select</th>
									</tr>
									<s:iterator value="value" status="itStatus">
										<tr class="boxtext">
											<td align="left" valign="middle"><s:property value="id" /></td>

											<td align="left" valign="middle"><s:property
													value="businessType" /></td>
											<td align="left" valign="middle"><s:property
													value="serviceTax" /></td>
											<td align="left" valign="middle"><s:property
													value="requestedBy" /></td>
											<td align="left" valign="middle"><s:property
													value="status" /></td>

											<td align="center" valign="middle"><s:textfield
													id="gstChkBox%{#itStatus.count}" value="" type="checkbox"></s:textfield></td>
										</tr>
									</s:iterator>
								</table>
							</div>
						</s:iterator>
						<br>
						<div align="center" valign="middle">
							<input type="button" name="gstBtn" value="Approve" 
								data-toggle="modal" data-target="#gstAccept"
								class="btn btn-lg btn-success" />
							<input type="button"
								name="gstBtn" value="Reject" data-toggle="modal" 
								data-target="#gstReject" class="btn btn-lg btn-danger" />
						</div>
					</div></td>
			</tr>
		</table>


		<table width="100%" border="0" cellspacing="0" cellpadding="0"
			class="">
			<tr>
				<td align="left"><div id="SmartRouter" class="tabcontent">
						<s:iterator value="smartRouterData" status="pay">
							<span class="text-primary-pending" id="test"><s:property
									value="key" /></span>
							<div id="<s:property value="key" />Div">
								<table width="100%" border="0" align="center"
									class="product-spec" id="smartRouterTable">
									<tr class="boxheading">
										<th width="4%" align="left" valign="middle">Identifier</th>
										<th width="4%" align="left" valign="middle">Merchant</th>
										<th width="4%" align="left" valign="middle">Region</th>
										<th width="4%" align="left" valign="middle">Type</th>
										<th width="6%" align="left" valign="middle">Min Txn</th>
										<th width="6%" align="left" valign="middle">Max Txn</th>
										<th width="3%" align="left" valign="middle">PaymentType</th>
										<th width="3%" align="left" valign="middle">MopType</th>
										<th width="15%" align="left" valign="middle">Acquirer-Load</th>
										<th width="3%" align="left" valign="middle">Status</th>
										<th width="2%" align="left" valign="middle">Select</th>

									</tr>
									<s:iterator value="value" status="itStatus">
										<tr class="boxtext">

											<td align="left" valign="middle"><s:property
													value="identifier" /></td>
											<td align="left" valign="middle"><s:property
													value="merchantName" /></td>
											<td align="left" valign="middle"><s:property
													value="paymentsRegion" /></td>
											<td align="left" valign="middle"><s:property
													value="cardHolderType" /></td>
											<td align="left" valign="middle"><s:property
													value="minAmount" /></td>
											<td align="left" valign="middle"><s:property
													value="maxAmount" /></td>
											<td align="left" valign="middle"><s:property
													value="paymentType" /></td>
											<td align="left" valign="middle"><s:property
													value="mopType" /></td>
											<td align="left" valign="middle"><s:property
													value="loadPercent" /></td>
											<td align="left" valign="middle"><s:property
													value="status" /></td>
											<td align="center" valign="middle"><s:textfield
													id="smartRouterChkBox%{#itStatus.count}" value=""
													type="checkbox"></s:textfield></td>

										</tr>
									</s:iterator>
								</table>
							</div>
						</s:iterator>
						<br>
						<div align="center" valign="middle">
							<input type="button" name="smartRouterBtn" value="Approve"
								data-toggle="modal" data-target="#smartRouterAccept"
								class="btn btn-lg btn-success" /> <input type="button"
								name="smartRouterBtn" value="Reject" data-toggle="modal"
								data-target="#smartRouterReject" class="btn btn-lg btn-danger" />
						</div>
					</div></td>
			</tr>
		</table>
</div>

		<s:hidden name="token" value="%{#session.customToken}"></s:hidden>
	</s:form>
<script type="text/javascript">
	$(document).ready(function() {
		if ($('#pendingRequest').hasClass("active")) {
			var menuAccess = document.getElementById("menuAccessByROLE").value;
			var accessMap = JSON.parse(menuAccess);
			var access = accessMap["pendingRequest"];
			if (access.includes("Add") || access.includes("Update")) {
				var editBtns = document.getElementsByName("updatePendingBtn");
				for (var i=0; i<editBtns.length; i++) {
					var editBtn = editBtns[i];
					editBtn.disabled=false;
				}
			}
		}
	});
</script>
</body>
</html>
`
