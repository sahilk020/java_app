<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="/struts-tags" prefix="s"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>View Surcharge Report</title>



<link rel="shortcut icon" href="assets/media/images/paylogo.svg" />
		<!--begin::Fonts-->
		<link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Inter:300,400,500,600,700" />
		<!--end::Fonts-->
		<!--begin::Vendor Stylesheets(used by this page)-->
		<!-- <link href="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.css" rel="stylesheet"
						type="text/css" /> -->
		<link href="../assets/plugins/custom/datatables/datatables.bundle.css" rel="stylesheet" type="text/css" />
		<!--end::Vendor Stylesheets-->
		<!--begin::Global Stylesheets Bundle(used by all pages)-->
		<link href="../assets/plugins/global/plugins.bundle.css" rel="stylesheet" type="text/css" />
		<link href="../assets/css/style.bundle.css" rel="stylesheet" type="text/css" />

		<script src="../assets/plugins/global/plugins.bundle.js"></script>
		<script src="../assets/js/scripts.bundle.js"></script>
		<script src="../js/jquery.popupoverlay.js"></script>


		<link href="../css/select2.min.css" rel="stylesheet" />
		<script src="../js/jquery.select2.js" type="text/javascript"></script>
		<script src="../js/html2canvas.min.js"></script>


<%-- <link href="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.css" rel="stylesheet" type="text/css" />
<link href="../assets/plugins/custom/datatables/datatables.bundle.css" rel="stylesheet" type="text/css" />
<link href="../assets/plugins/global/plugins.bundle.css" rel="stylesheet" type="text/css" />
<link href="../assets/css/style.bundle.css" rel="stylesheet" type="text/css" />
<link href="../assets/css/custom/custom-style.css" rel="stylesheet" type="text/css" />

<!-- <link href="../css/jquery-ui.css" rel="stylesheet" />
<link href="../css/default.css" rel="stylesheet" type="text/css" /> -->

<script type="text/javascript" src="../js/jquery.min.js"></script>
<script src="../js/core/popper.min.js"></script>
<script src="../js/core/bootstrap-material-design.min.js"></script>
<script src="../js/plugins/perfect-scrollbar.jquery.min.js"></script>
<!-- Control Center for Material Dashboard: parallax effects, scripts for the example pages etc -->
<script src="../js/material-dashboard.js?v=2.1.0" type="text/javascript"></script>
<script src="../js/jquery.popupoverlay.js"></script>

<!-- <link rel="stylesheet" type="text/css" href="../css/popup.css" />
<link href="../css/bootstrap-toggle.min.css" rel="stylesheet"> -->

<script src="../js/bootstrap-toggle.min.js"></script>
<!-- <link href="../css/select2.min.css" rel="stylesheet" /> -->
<script src="../js/jquery.select2.js" type="text/javascript"></script>
<!--  loader scripts -->
<script src="../js/loader/modernizr-2.6.2.min.js"></script>
<script src="../js/loader/main.js"></script>

<!-- <link rel="stylesheet" href="../css/loader/normalize.css" />
<link rel="stylesheet" href="../css/loader/main.css" />
<link rel="stylesheet" href="../css/loader/customLoader.css" /> -->
<!--------PDF scripts----->
<script src="../js/html2canvas.min.js"></script>
<script src="../js/jspdf.debug.js"></script>
<script src="../assets/plugins/global/plugins.bundle.js"></script>
<script src="../assets/js/scripts.bundle.js"></script> --%>

<script type="text/javascript">
	$(document).ready(function(){
		/* document.getElementById("loading").style.display = "none"; */
		
		 $("#merchants").select2();
	});
</script>
<style>

div#wwctrl_acquirer {
    margin: 0;
    padding: 0;
    box-sizing:inherit;
    font-family: "Poppins", sans-serif;
    width: 249px;
}

.switch {
  position: relative;
  display: inline-block;
  width: 30px;
  height: 17px;
}
.switch input {display:none;}
.slider {
  position: absolute;
  cursor: pointer;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #ccc;
  -webkit-transition: .4s;
  transition: .4s;
}
.slider:before {
  position: absolute;
  content: "";
  height: 13px;
  width: 13px;
  left: 2px;
  bottom: 2px;
  background-color: white;
  -webkit-transition: .4s;
  transition: .4s;
}
input:checked + .slider {
  background-color: #2196F3;
}
input:focus + .slider {
  box-shadow: 0 0 1px #2196F3;
}
input:checked + .slider:before {
  -webkit-transform: translateX(13px);
  -ms-transform: translateX(13px);
  transform: translateX(13px);
}
/* Rounded sliders */
.slider.round {
  border-radius: 17px;
}

.slider.round:before {
  border-radius: 50%;
}
.mycheckbox{
     /* Your style here */
 }
.switch {
  display: table-cell;
  vertical-align: middle;
  padding: 10px;
}
input.cmn-toggle-jwr:checked + label:after {
  margin-left: 1.5em;
}
table .toggle.btn {
    min-width: 48px;
    min-height: 28px;
}
table .btn {
    /* margin-bottom: 4px; */
    /* margin-right: 5px; */
    /* padding: 1px 12px;
    font-size: 11px; */
}
table .toggle-off.btn {
    padding: 0;
	margin:0;
}
.download-btn {
	background-color:#496cb6;
	display: block;
    width: 70%;
    height: 30px;
    padding: 3px 4px;
    font-size: 14px;
    line-height: 1.42857143;
    color: #fff;
    border: 1px solid #ccc;
    border-radius: 4px;
	margin-top:30px;
}
.disabled {
    color:#fff;
	border-color: #a0a0a0;
	background-color: #a0a0a0;
}

</style>

<script type="text/javascript">
var expanded = false;
function showCheckboxes(e) {
  var checkboxes = document.getElementById("checkboxes");
  if (!expanded) {
    checkboxes.style.display = "block";
    expanded = true;
  } else {
    checkboxes.style.display = "none";
    expanded = false;
  }
   e.stopPropagation();

}

var totalSelectedAcquirer;
function getCheckBoxValue(){
	    var allInputCheckBox = document.getElementsByClassName("myCheckBox");
  		
  		var allSelectedAquirer = [];
  		for(var i=0; i<allInputCheckBox.length; i++){
  			
  			if(allInputCheckBox[i].checked){
  				allSelectedAquirer.push(allInputCheckBox[i].value);	
  			}
  		}
		var acquirerString = allSelectedAquirer.join();
		
	    document.getElementById("acquirerType").value=acquirerString;
  		document.getElementById('selectBox').setAttribute('title', allSelectedAquirer.join());
  		if(allSelectedAquirer.join().length>28){
  			var res = allSelectedAquirer.join().substring(0,27);
  			document.querySelector("#selectBox option").innerHTML = res+'...............';
  		}else if(allSelectedAquirer.join().length==0){
  			document.querySelector("#selectBox option").innerHTML = 'Select Acquirer';
  		}else{
  			document.querySelector("#selectBox option").innerHTML = allSelectedAquirer.join();
  		}
		totalSelectedAcquirer = allSelectedAquirer.join();
}
</script>
<script type="text/javascript">
$(document).ready(function(){
	$(document).click(function(){
		expanded = false;
		$('#checkboxes').hide();
	});
	$('#checkboxes').click(function(e){
		e.stopPropagation();
	});
});
</script>


<style>
button#submitBtn {
margin-left: -141px;
    margin-bottom: -37px;
}

.product-spec input[type=text] {
	width: 35px;
}
.btn-tab{
	width: 17%;
	padding: 6px;
	font-size: 15px;
	color: #2c2c2c!important;
	background-color: #eaeaea!important;
	border: 1px solid #eaeaea!important;
	border-radius: 5px;
}
.btn-primary.active{
	background-color: #496cb6!important;
    border-color: #496cb6!important;
    color: #ffffff!important;
    border-radius: 5px;
	}
.uper-input{
	width: 105% !important;
	margin-left: -10px !important;
}
.noRuleMessage{
	color: #a94442; 
	background-color: #f2dede;
	border-color: #ebccd1; 
	border-radius: 4px; 
	text-align: center; 
	list-style-type: none; 
	font-size: 14px;
}

.overSelect {
    position: absolute;
	left: 0;
	right: 0;
	top: 0;
	bottom: 0;
}
.selectBox{
	  position:relative;
				
	}
/* label{
	color: #333;
	font-size: 13px;
	font-weight:400 !important;
} */
#checkboxes {
	  display: none;
	  border: 1px #dadada solid;
	  height:200px;
	  overflow-y: scroll;
	  position:absolute;
	  background:#fff;
	  z-index:1;
	  margin-left:5px;
	  width: 285px;
	}
#checkboxes label {
	width: 74%;
}
#checkboxes input {
	width:18%;
}
/* input[type=checkbox]{
    margin: 4px -8px 0 !important;
} */
.pdfButton{
	display: block;
    width: 13%;
	float:right;
    height: 30px;
    padding: 3px 4px;
    font-size: 14px;
    line-height: 1.42857143;
    background-color: #6b9ae1;
    border: 1px solid #ccc;
    border-radius: 4px;
    margin-top: 30px;
	color:white;
}
.noRuleMessage{
	color: #a94442; 
	background-color: #f2dede;
	border-color: #ebccd1; 
	border-radius: 4px; 
	text-align: center; 
	list-style-type: none; 
	font-size: 14px;
}
#loading {width: 100%;height: 100%;top: 0px;left: 0px;position: fixed;display: block; z-index: 99}
#loading-image {position: absolute;top: 40%;left: 55%;z-index: 100; width:10%;}

#submitBtn{
    margin-top: 23px;

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
									<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">View Surcharge</h1>
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
										<li class="breadcrumb-item text-muted">View Configuration</li>
										<!--end::Item-->
										<!--begin::Item-->
										<li class="breadcrumb-item">
											<span class="bullet bg-gray-200 w-5px h-2px"></span>
										</li>
										<!--end::Item-->
										<!--begin::Item-->
										<li class="breadcrumb-item text-dark">View Surcharge</li>
										<!--end::Item-->
									</ul>
									<!--end::Breadcrumb-->
								</div>
								<!--end::Page title-->
								
							</div>
							<!--end::Container-->
						</div>
						

	<!-- <div id="loading" style="text-align: center;">
		<img id="loading-image" style="width:70px;height:70px;" src="../image/sand-clock-loader.gif" alt="Sending SMS..." />
	</div> -->
	
<s:actionmessage class="error error-new-text" />

<div class="post d-flex flex-column-fluid" id="kt_post">
                <!--begin::Container-->
 <div id="kt_content_container" class="container-xxl">
 
<s:form id="viewSurchargePlatformAction" action="viewSurchargePlatformAction" method="post">
		<table width="100%" border="0" cellspacing="0" cellpadding="0" class="txnf" style="margin-top:1%;">
	
			<tr>
				<!-- <td width="21%"><h2 style="margin-bottom:4%;">View Surcharge</h2></td> -->
			</tr>
			
			<tr>
				<td align="center" valign="top">
				<!-- <table width="98%" border="0" cellspacing="0" cellpadding="0"> -->
				
					 <div class="row my-5">
                        <div class="col">
                            <div class="card">
                                <div class="card-body">
					
								<!-- <div class="col-md-12">
									<div class="card "> -->
									 <!--  <div class="card-header card-header-rose card-header-text">
										<div class="card-text">
										  <h4 class="card-title">View Surcharge</h4>
										</div>
									  </div> -->
									  <!-- <div class="card-body "> -->
								<!-- <div class="container-fluid d-flex flex-stack" id="kt_toolbar_container"> -->
							<div class="row g-9 mb-8">
									<div class="col-md-3 fv-row">
									<label class="d-flex align-items-center fs-6 fw-bold mb-2">
									<span class="">Merchant</span></label>
						<s:if test="%{#session.USER.UserType.name()=='ADMIN' || #session.USER.UserType.name()=='SUBADMIN' || #session.USER_TYPE.name()=='SUPERADMIN'}">
											<s:select headerValue="Select Merchant" headerKey=""
												name="emailId" class="form-select form-select-solid" id="merchants"
												list="listMerchant" listKey="emailId"
												listValue="businessName" autocomplete="off" />
										</s:if>
										<s:else>
											   <s:select headerValue="Select Merchant" headerKey=""
												name="emailId" class="form-select form-select-solid" id="merchants"
												list="listMerchant1" listKey="emailId"
												listValue="businessName" autocomplete="off" />
										</s:else>
									</div>
									
									
									<div class="col-md-3 fv-row">
										<label class="d-flex align-items-center fs-6 fw-bold mb-2">
										<span class="">Acquirer</span></label>
											
												<div class="selectBox" id="selectBox" onclick="showCheckboxes(event)" title="dummy Title">
												  <select class="form-select form-select-solid" style="margin-top: 5px;">
													<option>Select Acquirer</option>
												  </select>
												<div class="overSelect"></div>
												</div>
												<div id="checkboxes" onclick="getCheckBoxValue()">
												   <s:checkboxlist headerKey="ALL" headerValue="ALL" list="@com.pay10.commons.util.AcquirerTypeUI@values()" 
												id="acquirer" class="myCheckBox" name="acquirer" value="acquirer" listValue="name" listKey="code"
													/>
												</div>
											
											
										<input type="hidden" id="acquirerType" name="acquirerType"/>
									</div>
									
									<div class="col-md-3 fv-row">
										<button class="btn btn-primary submit_btn" id="submitBtn"  onclick="checkSelectedValues(event)">Submit</button>
									</div>
								</div>
									
								
									
								</div>
								</div>
								</div>
								</div>
							
						
						<tr>
							<td align="left">							
								<div id="datatable2" class="scrollD" style="margin-top:-20px;">	
									<br>	
									<s:if test="%{surchargeMapData.isEmpty()}">
										<tr>
											<td colspan="4" class="noRuleMessage" id="errorMessage">No Surcharge Found !!</td>
										</tr>
									</s:if>
									<s:else>
									
										<s:iterator value="surchargeMapData" status="pay">
										
											<br>
											<div class="text-primary card-list-toggle" id = "test" style="margin-bottom:15px; text-decoration:underline;"><strong><s:property
													value="key" /></strong></div>
											<div class="scrollD card-list">
												<div id="<s:property value="key" />Div">
													<table width="100%" border="0" align="center" class="product-spec" id="surchargeTable">
														<tr class="boxheading">
															<th width="4%" align="left" valign="middle">Region</th>	
															<th width="6%" align="left" valign="middle">Payment Type</th>
															<th width="4%" align="left" valign="middle">Mop Type</th>
															<th width="6%" align="left" valign="middle">Bank %(Commercial)</th>
															<th width="6%" align="left" valign="middle">Bank FC(Commercial)</th>
															<th width="5%" align="left" valign="middle">Bank %(Consumer)</th>
															<th width="3%" align="left" valign="middle">Bank FC(Consumer)</th>
															
														</tr>
														<s:iterator value="value" status="itStatus">
														<tr class="boxtext">
																
																<td align="left" valign="middle">
																	<s:property value="paymentsRegion" />
																</td>
																
																<td align="left" valign="middle">
																	<s:property value="paymentType" />
																</td>
																<td align="left" valign="middle">
																	<s:property value="mopType" />
																</td>
																
																
																
																<td align="left" valign="middle" class="nomarpadng">
																	<div title="Surcharge on us">
																		&nbsp;
																		<s:property value="bankSurchargePercentageOnCommercial" />
																	</div>
																	<div class="cellborder" title="Surcharge off us">
																		&nbsp;
																		<s:property value="bankSurchargePercentageOffCommercial" />
																	</div>
																</td>
																		
																<td align="left" valign="middle" class="nomarpadng">
																	<div title="Surcharge on us">
																		&nbsp;
																		<s:property value="bankSurchargeAmountOnCommercial" />
																	</div>
																	<div class="cellborder" title="Surcharge off us">
																		&nbsp;
																		<s:property value="bankSurchargeAmountOffCommercial" />
																	</div>
																</td>
																	
																<td align="left" valign="middle" class="nomarpadng">
																	<div title="Surcharge on us">
																		&nbsp;
																	   <s:property value="bankSurchargePercentageOnCustomer" />
																	</div>
																	<div class="cellborder" title="Surcharge off us">
																		&nbsp;
																		<s:property value="bankSurchargePercentageOffCustomer" />
																	</div>
																</td>
																		
																<td align="left" valign="middle" class="nomarpadng">
																	<div title="Surcharge on us">
																		&nbsp;
																		<s:property value="bankSurchargeAmountOnCustomer" />
																	</div>
																	<div class="cellborder" title="Surcharge off us">
																		&nbsp;
																		<s:property value="bankSurchargeAmountOffCustomer" />
																	</div>
																</td>
														
															
															</tr>
														</s:iterator>
													</table>
												</div>
												
											</div>
										</s:iterator>
									</s:else>
								</div>
							</td>
						</tr>
						<div id="editor"></div>
                          
				<!-- </table> -->
				</td>
			</tr>
			
		</table>
	</s:form>
	</div>
	</div>
        <div class="form-group  txtnew ">
				<button id="generatePdf" class="pdfButton">Generate PDF</button>
		</div>
		</div>
		
<script>
	var obj = {
		 orientation: 'l',
		 unit: 'pt',
		 format: 'a4'
	}
	var options = {'dim' : 
	        {'h' : 10, 'w' : 50}
	};
    var pdf = new jsPDF(obj);
	pdf.setFontSize(2);
	//pdf.addPage(2,100);
    var specialElementHandlers = {
        '#editor': function (element, renderer) {
            return true;
        }
    };
	
    $('#generatePdf').click(function () {
		pdf.html($('#datatable2').html(), {x:15,y:15,options,callback:function(pdf){pdf.save('surchargeView.pdf')}});
     
    });
</script>

<script>
var surchargeTable = document.getElementById("surchargeTable");
if(surchargeTable == null){
	document.getElementById("generatePdf").style.display = "none";
}else{
	document.getElementById("datatable2").style.display = "block";
	document.getElementById("generatePdf").style.display = "block";
	/* document.getElementById("loading").style.display = "none"; */
}
</script>

<script>
var merchantVal = document.getElementById("merchants").value;
if ((merchantVal == "Select Merchant") || (merchantVal == "")){
	document.getElementById("errorMessage").style.display = "none";
}

<!---------Function on Button Click------->
function checkSelectedValues(event){
	var merchantsVal = document.getElementById("merchants").value;
	var acquirerValues = totalSelectedAcquirer;
		if(merchantsVal == "" || merchantsVal == null || merchantsVal == "Select Merchant"){
			alert("Please select merchant");
			event.preventDefault();
		} else if(acquirerValues == "" || acquirerValues ==  null ){
			alert("Please select acquirer");
			event.preventDefault();
		}
		return true;
		/* document.getElementById("loading").style.display = "block"; */
}
</script>

</body>
</html>