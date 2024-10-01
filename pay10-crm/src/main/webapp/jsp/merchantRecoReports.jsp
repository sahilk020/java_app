<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib prefix="s" uri="/struts-tags"%>
<html>
<head>

<style>
.nav {
	margin-bottom: 18px;
    margin-left: 0;
	list-style: none;
}
.nav > li > a {
	display: block;
}
.nav-tabs{
	 *zoom: 1;
}
.nav-tabs:before,
.nav-tabs:after {
	 display: table;
	 content: "";
}
.nav-tabs:after {
	clear: both;
}
.nav-tabs > li {
	float: left;
}
.nav-tabs > li > a {
	padding-right: 12px;
	padding-left: 12px;
	margin-right: 2px;
	line-height: 14px;
}
.nav-tabs {
	border-bottom: 1px solid #ddd;
}
.nav-tabs > li {
	margin-bottom: -1px;
}
.nav-tabs > li > a {
	padding-top: 8px;
	padding-bottom: 8px;
	line-height: 18px;
	border: 1px solid transparent;
	-webkit-border-radius: 4px 4px 0 0;
	 -moz-border-radius: 4px 4px 0 0;
	border-radius: 4px 4px 0 0;
}
.nav-tabs > li > a:hover {
	border-color: #eeeeee #eeeeee #dddddd;
}
.nav-tabs > .active > a,
.nav-tabs > .active > a:hover {
	color: #555555;
	cursor: default;
	background-color: #ffffff;
	border: 1px solid #ddd;
	border-bottom-color: transparent;
}
li {
	line-height: 18px;
}
.tab-content.active{
	display: block;
}
.tab-content.hide{
	display: none;
}
.nav-tabs>li>a:hover{border-top: 0px solid transparent;}	
.uploadButton{
    border: none;
	background-color: #496cb6;
    border-radius: 5px;
    width: 25%;
    font-size: 18px;
	color:white;
}
.heading{
   text-align: center;
    color: black;
    font-weight: bold;
    font-size: 22px;
}
.txtnew label {
    /* display: inline-block; */
    /* max-width: 100%; */
    display: block;
    text-align: left;
}
.merchantForm-control {
	width: 165% !important;
	margin-left: 0 !important;
}
.form-control{
	margin-left: 0 !important;
	width: 100% !important;
}
.select2-container--default {
    display: block !important;
}
.select2.select2-container.select2-container--default{
	width: auto !important;
}
.select2-container .select2-selection--single .select2-selection__rendered{
	padding-left: 0px !important;
    padding-right: 50px !important;
}
#ui-datepicker-div{
		z-index: 10 !important;
	}
</style>

<title>Merchant Reco Report</title>
<!-- <link href="../css/default.css" rel="stylesheet" type="text/css" />
<link rel="stylesheet" type="text/css" media="all" href="../css/daterangepicker-bs3.css" /> -->
<!-- <link href="../css/Jquerydatatableview.css" rel="stylesheet" />
<link href="../css/Jquerydatatable.css" rel="stylesheet" /> -->

<!-- <link href="../css/jquery-ui.css" rel="stylesheet" /> -->
<script src="../js/jquery.min.js"></script>
<script src="../js/core/popper.min.js"></script>
<script src="../js/core/bootstrap-material-design.min.js"></script>
<script src="../js/plugins/perfect-scrollbar.jquery.min.js"></script>
<!-- Control Center for Material Dashboard: parallax effects, scripts for the example pages etc -->
<script src="../js/material-dashboard.js?v=2.1.0" type="text/javascript"></script>
<script type="text/javascript" src="../js/moment.js"></script>
<script type="text/javascript" src="../js/daterangepicker.js"></script>
<script src="../js/jquery.dataTables.js"></script>
<script src="../js/jquery-ui.js"></script>
<script src="../js/commanValidate.js"></script>

<script src="../js/jquery.popupoverlay.js"></script>
<script type="text/javascript" src="../js/dataTables.buttons.js"></script>
<script type="text/javascript" src="../js/pdfmake.js"></script>
<script src="../js/jszip.min.js" type="text/javascript"></script>
<script src="../js/vfs_fonts.js" type="text/javascript"></script>
<script src="../js/buttons.colVis.min.js" type="text/javascript"></script>
<script type="text/javascript" src="../js/summaryReport.js"></script>
<!-- <link href="../css/loader.css" rel="stylesheet" type="text/css" /> -->
<script src="../js/select2.min.js"></script>
<!-- <link href="../css/select2.min.css" rel="stylesheet" /> -->
<script src="../js/jquery.select2.js" type="text/javascript"></script>
<!--  loader scripts -->
<script src="../js/loader/modernizr-2.6.2.min.js"></script>
<script src="../js/loader/main.js"></script>
<!-- <link rel="stylesheet" href="../css/loader/normalize.css" />
<link rel="stylesheet" href="../css/loader/main.css" />
<link rel="stylesheet" href="../css/loader/customLoader.css" /> -->

<link href="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.css" rel="stylesheet" type="text/css" />
<link href="../assets/plugins/custom/datatables/datatables.bundle.css" rel="stylesheet" type="text/css" />
<link href="../assets/plugins/global/plugins.bundle.css" rel="stylesheet" type="text/css" />
<link href="../assets/css/style.bundle.css" rel="stylesheet" type="text/css" />
<link href="../assets/css/custom/custom-style.css" rel="stylesheet" type="text/css" />
<script src="../assets/js/scripts.bundle.js"></script>
<script type="text/javascript">
$(document).ready(function(){
 
  // Initialize select2
  $("#merchant").select2();
  $(".merchantForm-control").select2();
  $("#deltaMerchant").select2();
  $("#validationMerchant").select2();
  $("#merchantPayId").select2();
});
</script>
	
<script>
	$(document).ready(function() {
		$('.nav-tabs > li > a').click(function(event){
		event.preventDefault();//stop browser to take action for clicked anchor

		//get displaying tab content jQuery selector
		var active_tab_selector = $('.nav-tabs > li.active > a').attr('href');

		//find actived navigation and remove 'active' css
		var actived_nav = $('.nav-tabs > li.active');
		actived_nav.removeClass('active');

		//add 'active' css into clicked navigation
		$(this).parents('li').addClass('active');

		//hide displaying tab content
		$(active_tab_selector).removeClass('active');
		$(active_tab_selector).addClass('hide');

		//show target tab content
		var target_tab_selector = $(this).attr('href');
		$(target_tab_selector).removeClass('hide');
		$(target_tab_selector).addClass('active');
	     });
	  });
</script>
	
	
</head>
<body id="mainBody">

	<!-- <h2 class="pageHeading">Merchant  Reco  Report</h2> -->
	<br>
	<br>
	<div class="toolbar" id="kt_toolbar">
		<!--begin::Container-->
		<div id="kt_toolbar_container" class="container-fluid d-flex flex-stack">
			<!--begin::Page title-->
			<div data-kt-swapper="true" data-kt-swapper-mode="prepend" data-kt-swapper-parent="{default: '#kt_content_container', 'lg': '#kt_toolbar_container'}" class="page-title d-flex align-items-center me-3 flex-wrap mb-5 mb-lg-0 lh-1">
				<!--begin::Title-->
				<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">Merchant Reco Report</h1>
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
					<li class="breadcrumb-item text-muted">Accounts & Finance</li>
					<!--end::Item-->
					<!--begin::Item-->
					<li class="breadcrumb-item">
						<span class="bullet bg-gray-200 w-5px h-2px"></span>
					</li>
					<!--end::Item-->
					<!--begin::Item-->
					<li class="breadcrumb-item text-dark">Merchant Reco Report</li>
					<!--end::Item-->
				</ul>
				<!--end::Breadcrumb-->
			</div>
			<!--end::Page title-->
			
		</div>
		<!--end::Container-->
	</div>
	 <table class="table98 padding0">
        
        <tr>
          <td align="center">&nbsp;</td>
          <td height="10" align="center">
        <ul class="nav nav-tabs" style="border-bottom:none;">
        <li class="active"><a href="#SaleUpload">Sale Upload</a></li>
        <li><a href="#RefundUpload">Refund Upload</a></li>
        <li><a href="#DeltaRefund">Delta Refund</a></li>
        <li><a href="#RefundValidation">Refund Validation</a></li> 
        <li><a href="#saleDownload">Sale Download</a></li>  		
    </ul>
    </td>
        </tr>
 
  <tr>
          <td align="center">&nbsp;</td>
          <td height="10" align="center">
    
	<!----------------------------FIRST TAB CONTENT------------------------->
      <section id="SaleUpload" class="tab-content active">
        <div>
            <br/>
			<form id="saleform" name="saleform" method="post" action="saleUploadAction" enctype="multipart/form-data"> 
            <table class="table98 padding0 profilepage">
            
				<div class="col-md-12">
					<div class="card ">
					  <div class="card-header card-header-rose card-header-text">
						<div class="card-text">
						  <h4 class="card-title">Sale Upload</h4>
						</div>
					  </div>
					  <div class="card-body ">
						<div class="container">
						  <div class="row">
							
								  <div class="col-sm-6 col-lg-3">
									<label style="float: left;" class="d-flex align-items-center fs-6 fw-semibold mb-2">Merchant :</label><br>
									<div class="txtnew">
										<s:if
							test="%{#session.USER.UserType.name()=='ADMIN' || #session.USER.UserType.name()=='SUBADMIN'  || #session.USER.UserType.name()=='SUBADMIN' || #session.USER_TYPE.name()=='SUPERADMIN'}">
							<s:select name="merchant" class="form-select form-select-solid" id="merchant"
								headerKey="ALL" headerValue="Select Merchant" list="merchantList"
								listKey="payId" listValue="businessName" onchange="handleChange();" autocomplete="off" />
						</s:if>
						<s:else>
							<s:select name="merchant" class="form-select form-select-solid" id="merchant"
								headerKey="" headerValue="" list="merchantList"
								listKey="payId" listValue="businessName" onchange="handleChange();" autocomplete="off" />
						</s:else>
					</div>
							
					
								  </div>
								  <div class="col-sm-6 col-lg-3">
									<label style="float: left;" class="d-flex align-items-center fs-6 fw-semibold mb-2">Currency:</label><br>
									
									<s:select name="currency" id="currency" headerValue="Select Currency"
								headerKey="ALL" list="currencyMap" class="form-select form-select-solid"/>
								  </div>
								  <!-- <div class="col-sm-6 col-lg-3">
									<label>Currency :</label><br>
									<div class="txtnew">
										<s:select name="currency" id="currency" headerValue="ALL"
								headerKey="ALL" list="currencyMap" class="input-control" />
									</div>
								</div> -->
								<div class="col-sm-6 col-lg-3">
									<label style="float: left;" class="d-flex align-items-center fs-6 fw-semibold mb-2">Date From:</label><br>
									<div class="txtnew">
										<s:textfield type="text" readonly="true" id="dateFrom"
							name="dateFrom" class="form-select form-select-solid"  
							autocomplete="off" />
									</div>

								</div>
								<div class="col-sm-6 col-lg-3">
									<label style="float: left;" class="d-flex align-items-center fs-6 fw-semibold mb-2">Date To: </label><br>
									<div class="txtnew">
										<s:textfield type="text" readonly="true" id="dateTo" name="dateTo"
							class="form-control form-control-solid"  autocomplete="off" />
									</div>
								</div>
								<div class="col-sm-6 col-lg-4" style="display: inline-flex;">
									
										<input name="file" id="file"  type="file" accept="text/*"  style="margin-top:25px" class="form-control form-control-solid">
										<button class="btn btn-primary  mt-4 submit_btn"  id="submit">Upload</button>
									  
								</div>
								
							 
							  
							  <div id="errors" style="color:red;"></div>
					<div id="success" style="color:green;"></div>
					<tr><td align="center" valign="top">
				<s:actionmessage class="success success-text" />
						
							
						  </div>
						  </div>
						  
					
					  </div>
					</div>
				  </div>                    
            </table>
		</form>  
            </div>
        </section>
       
	   <!----------------------------SECOND TAB CONTENT------------------------->
       <section id="RefundUpload" class="tab-content hide">
        <div>
			<br/>
			<form id="refundform" name="refundform" method="post" action="refundUploadAction" enctype="multipart/form-data">
             <table class="table98 padding0 profilepage">
				<div class="col-md-12">
					<div class="card ">
					  <div class="card-header card-header-rose card-header-text">
						<div class="card-text">
						  <h4 class="card-title">Refund Upload</h4>
						</div>
					  </div>
					  <div class="card-body ">
						<div class="container">
						  <div class="row">
							 
								  <div class="col-sm-6 col-lg-3">
									<label style="float: left;" class="d-flex align-items-center fs-6 fw-semibold mb-2">Merchant :</label><br>
									<div class="txtnew">
									
									<s:if
									test="%{#session.USER.UserType.name()=='ADMIN' || #session.USER.UserType.name()=='SUBADMIN'  || #session.USER.UserType.name()=='SUBADMIN' || #session.USER_TYPE.name()=='SUPERADMIN'}">
									<s:select name="merchant" class="form-select form-select-solid" id="merchant"
										headerKey="ALL" headerValue="Select Merchant" list="merchantList"
										listKey="payId" listValue="businessName" onchange="handleChange();" autocomplete="off" />
								</s:if>
								<s:else>
									<s:select name="merchant" class="form-select form-select-solid" id="merchant"
										headerKey="ALL" headerValue="ALL" list="merchantList"
										listKey="payId" listValue="businessName" onchange="handleChange();" autocomplete="off" />
								</s:else>
							</div>
					
								  </div>
								  <div class="col-sm-6 col-lg-3">
									<label style="float: left;" class="d-flex align-items-center fs-6 fw-semibold mb-2">Currency:</label><br>
									
									<s:select name="currency" id="currency" headerValue="Select Currency"
								headerKey="ALL" list="currencyMap" class="form-select form-select-solid" />
								  </div>
								  <!-- <div class="col-sm-6 col-lg-3">
									<label>Currency :</label><br>
									<div class="txtnew">
										<s:select name="currency" id="currency" headerValue="ALL"
								headerKey="ALL" list="currencyMap" class="input-control" />
									</div>
								</div> -->
								<div class="col-sm-6 col-lg-3">
									<label style="float: left;" class="d-flex align-items-center fs-6 fw-semibold mb-2">Date From:</label><br>
									<s:textfield type="text" readonly="true" id="refundDateFrom"
									name="dateFrom" class="form-control form-control-solid"  
									autocomplete="off"/>
									</div>

								
								<div class="col-sm-6 col-lg-3">
									<label style="float: left;" class="d-flex align-items-center fs-6 fw-semibold mb-2">Date To: </label><br>
									<div class="txtnew">
										<s:textfield type="text" readonly="true" id="refundDateTo" name="dateTo"
							class="form-control form-control-solid" autocomplete="off"/>
									</div>
								</div>
								<div class="col-sm-6 col-lg-3" style="display: inline-flex;">
									
										<input name="file" id="file"  type="file" accept="text/*" style="margin-top:25px" >
										<button class="btn btn-primary  mt-4 submit_btn"  id="submit">Upload</button>
									  
								</div>
								
							 
							  
							  <div id="errors" style="color:red;"></div>
					<div id="success" style="color:green;"></div>
					<tr><td align="center" valign="top">
				<s:actionmessage class="success success-text" />
						
							
						  </div>
						  </div>
						  
					
					  </div>
					</div>
				  </div>         
              
            </table>
		</form> 
        </div>
        </section>
		
		<!----------------------------THIRD TAB CONTENT------------------------->
        <section id="DeltaRefund" class="tab-content hide">
        <div>
            <br/>
            <form id="deltaRefund" name="deltaRefund" method="post" action="deltaRefundAction">
                <table class="table98 padding0 profilepage">
                    <div class="col-md-12">
						<div class="card ">
						  <div class="card-header card-header-rose card-header-text">
							<div class="card-text">
							  <h4 class="card-title">Delta Upload</h4>
							</div>
						  </div>
						  <div class="card-body ">
							<div class="container">
							  <div class="row">
								
									  <div class="col-sm-6 col-lg-3">
										<label style="float: left;" class="d-flex align-items-center fs-6 fw-semibold mb-2">Merchant :</label><br>
										<div class="txtnew">
										<s:if
							test="%{#session.USER.UserType.name()=='ADMIN' || #session.USER.UserType.name()=='SUBADMIN'  || #session.USER.UserType.name()=='SUBADMIN' || #session.USER_TYPE.name()=='SUPERADMIN'}">
							<s:select name="deltaMerchant" class="form-control form-control-solid" id="deltaMerchant"
								headerKey="Select Merchant" headerValue="Select Merchant" list="merchantList"
								listKey="payId" listValue="businessName" onchange="handleChange();"  autocomplete="off" />
						</s:if>
						<s:else>
							<s:select name="deltaMerchant" class="form-control form-control-solid" id="deltaMerchant"
								headerKey="Select Merchant" headerValue="Select Merchant" list="merchantList"
								listKey="payId" listValue="businessName" onchange="handleChange();" autocomplete="off" />
						</s:else>
						</div>
								
						
									  </div>
									  <div class="col-sm-6 col-lg-col-sm-6 col-lg-33col-sm-6 col-lg-3">
										<label style="float: left;" class="d-flex align-items-center fs-6 fw-semibold mb-2">Currency:</label><br>
										
										<s:select name="deltaCurrency" id="deltaCurrency" headerValue="ALL"
								headerKey="ALL" list="currencyMap" class="form-control form-control-solid"/>
									  </div>
									  <!-- <div class="col-sm-6 col-lg-3">
										<label>Currency :</label><br>
										<div class="txtnew">
											<s:select name="currency" id="currency" headerValue="ALL"
									headerKey="ALL" list="currencyMap" class="input-control" />
										</div>
									</div> -->
									<div class="col-sm-6 col-lg-3">
										<label style="float: left;" class="d-flex align-items-center fs-6 fw-semibold mb-2">Date From:</label><br>
										<s:textfield type="text" readonly="true" id="deltaDateFrom"
							name="deltaDateFrom" class="form-control form-control-solid"  
							autocomplete="off" />
										</div>
	
									
									<div class="col-sm-6 col-lg-3">
										<label style="float: left;" class="d-flex align-items-center fs-6 fw-semibold mb-2">Date To: </label><br>
										<div class="txtnew">
											<s:textfield type="text" readonly="true" id="deltaDateTo" name="deltaDateTo"
							class="form-select form-select-solid"  autocomplete="off" />
										</div>
									</div>
									<input type="hidden" id="deltaRefundButtonIdentifier" name="deltaRefundButtonIdentifier" value="">
									
									
								  
							
									<div class="col-sm-6 col-lg-3">
									<button  class="btn btn-primary  mt-4 submit_btn" id="deltaDownloadBasic" onClick='submitDeltaRefundBasicForm()' >Download with Basic</button sty>
									</div>
									<div class="col-sm-6 col-lg-3">
									<button  class="btn btn-primary  mt-4 submit_btn" id="deltaDownloadRRN" onClick='submitDeltaRefundRRNForm()' >Download with RRN</button>
								  </div>
							
								  
								  
							
								
							  </div>
							  </div>
							  
						
						  </div>
						</div>
					  </div>
                 
                </table>
            </form>
        </div>   
        </section>
		
		
		<!----------------------------FOURTH TAB CONTENT------------------------->
        <section id="RefundValidation" class="tab-content hide">  
        <div>
            <br />
            <form id="refundValidation" name="refundValidation" method="post" action="refundValidationAction">
             <table class="table98 padding0 profilepage">
				<div class="col-md-12">
					<div class="card ">
					  <div class="card-header card-header-rose card-header-text">
						<div class="card-text">
						  <h4 class="card-title">Refund Validation</h4>
						</div>
					  </div>
					  <div class="card-body ">
						<div class="container">
						  <div class="row">
						
								  <div class="col-sm-6 col-lg-3">
									<label style="float: left;" class="d-flex align-items-center fs-6 fw-semibold mb-2">Merchant :</label><br>
									<div class="txtnew">
										<s:if
										test="%{#session.USER.UserType.name()=='ADMIN' || #session.USER.UserType.name()=='SUBADMIN'  || #session.USER.UserType.name()=='SUBADMIN' || #session.USER_TYPE.name()=='SUPERADMIN'}">
										<s:select name="validationMerchant" class="form-select form-select-solid" id="validationMerchant"
											headerKey="Select Merchant" headerValue="Select Merchant" list="merchantList"
											listKey="payId" listValue="businessName" onchange="handleChange();"   autocomplete="off" />
									</s:if>
									<s:else>
										<s:select name="validationMerchant" class="form-select form-select-solid" id="validationMerchant"
											headerKey="Select Merchant" headerValue="Select Merchant" list="merchantList"
											listKey="payId" listValue="businessName" onchange="handleChange();" autocomplete="off" />
									</s:else>
					</div>
							
					
								  </div>
								  <div class="col-sm-6 col-lg-3">
									<label style="float: left;" class="d-flex align-items-center fs-6 fw-semibold mb-2">Currency:</label><br>
									
									<s:select name="validationCurrency" id="validationCurrency" headerValue="ALL"
								headerKey="ALL" list="currencyMap" class="form-select form-select-solid" />
								  </div>
								  <!-- <div class="col-sm-6 col-lg-3">
									<label>Currency :</label><br>
									<div class="txtnew">
										<s:select name="currency" id="currency" headerValue="ALL"
								headerKey="ALL" list="currencyMap" class="input-control" />
									</div>
								</div> -->
								<div class="col-sm-6 col-lg-3">
									<label style="float: left;" class="d-flex align-items-center fs-6 fw-semibold mb-2">Date From:</label><br>
									<s:textfield type="text" readonly="true" id="validationDateFrom"
							name="validationDateFrom" class="form-control form-control-solid"
							autocomplete="off" />
									</div>

								
								<div class="col-sm-6 col-lg-3">
									<label style="float: left;" class="d-flex align-items-center fs-6 fw-semibold mb-2">Date To: </label><br>
									<div class="txtnew">
										<s:textfield type="text" readonly="true" id="validationDateTo" name="validationDateTo"
							class="form-control form-control-solid"  autocomplete="off" />
									</div>
								</div>
								<input type="hidden" id="refundValidationButtonIdentifier" name="refundValidationButtonIdentifier" value="">
								
								
							
								<div class="col-sm-6 col-lg-3">
								<button  class="btn btn-primary  mt-4 submit_btn" id="refundValidationDownloadBasic" onClick='submitRefundValidationBasicForm() '>Download with Basic</button sty>
								</div>
								<div class="col-sm-6 col-lg-3">
								<button  class="btn btn-primary  mt-4 submit_btn" id="refundValidationDownloadRRN" onClick='submitRefundValidationRRNForm()' >Download with RRN</button>
							  </div>
							
							  
							  
						
							
						  </div>
						  </div>
						  
					
					  </div>
					</div>
				  </div>
		
		</div>

              </table>
              </form>
        </div>  
        </section>      
        
		
		
		<!----------------------------FIFTH TAB CONTENT------------------------->
		
		<section id="saleDownload" class="tab-content hide">  
        <div>
            <br />
            <form id="saleDownload" name="saleDownload" method="post" action="downloadSettlementReportAction">
             <table class="table98 padding0 profilepage">
              
					<div class="col-md-12">
						<div class="card ">
						  <div class="card-header card-header-rose card-header-text">
							<div class="card-text">
							  <h4 class="card-title">Sale Download</h4>
							</div>
						  </div>
						  <div class="card-body ">
							<div class="container">
							  <div class="row">
							
									  <div class="col-sm-6 col-lg-3">
										<label style="float: left;" class="d-flex align-items-center fs-6 fw-semibold mb-2">Merchant :</label><br>
										
											<s:if
							test="%{#session.USER.UserType.name()=='ADMIN' || #session.USER.UserType.name()=='SUBADMIN'  || #session.USER.UserType.name()=='SUBADMIN' || #session.USER_TYPE.name()=='SUPERADMIN'}">
							<s:select name="merchantPayId" class="form-select form-select-solid" id="merchantPayId"
								headerKey="ALL" headerValue="Select Merchant" list="merchantList"
								listKey="payId" listValue="businessName" onchange="handleChange();" autocomplete="off" />
						</s:if>
						<s:else>
							<s:select name="merchantPayId" class="form-select form-select-solid" id="merchantPayId"
								headerKey="ALL" headerValue="ALL" list="merchantList"
								listKey="payId" listValue="businessName" onchange="handleChange();" autocomplete="off" />
						</s:else>
					
								
						
									  </div>
									  <div class="col-sm-6 col-lg-3">
										<label style="float: left;" class="d-flex align-items-center fs-6 fw-semibold mb-2">Currency:</label><br>
										
										<s:select name="currency" id="currency" headerValue="ALL"
								headerKey="ALL" list="currencyMap" class="form-control form-control-solid"/>
									  </div>
									  <!-- <div class="col-sm-6 col-lg-3">
										<label>Currency :</label><br>
										<div class="txtnew">
											<s:select name="currency" id="currency" headerValue="ALL"
									headerKey="ALL" list="currencyMap" class="form-control form-control-solid" />
										</div>
									</div> -->
									<div class="col-sm-6 col-lg-3">
										<label style="float: left;" class="d-flex align-items-center fs-6 fw-semibold mb-2">Sale Date:</label><br>
										<s:textfield type="text" readonly="true" id="saleDate"
										name="saleDate" class="form-control form-control-solid"  
										autocomplete="off"/>
										</div>
	
									
									<!-- <div class="col-sm-6 col-lg-3">
										<label>Date To: </label><br>
										<div class="txtnew">
											<s:textfield type="text" readonly="true" id="validationDateTo" name="validationDateTo"
								class="input-control" onchange="handleChange();" autocomplete="off" />
										</div>
									</div>
									<input type="hidden" id="refundValidationButtonIdentifier" name="refundValidationButtonIdentifier" value=""> -->
									
									
							
									<div class="col-sm-6 col-lg-3">
									<button class="btn btn-primary  mt-4 submit_btn" >Download </button >
									</div>
									
								
								  
								  
							
								
							  </div>
							  </div>
							  
						
						  </div>
						</div>
					  </div>
				</div>
				
	         	

              </table>
              </form>
        </div>  
        </section>  
		
		
		
    
      </td>
    </table>
<script language="javascript" type="text/javascript">
	function submitDeltaRefundBasicForm() {
	    document.getElementById("deltaRefundButtonIdentifier").value = "BASIC";
	    var merchant = document.getElementById("deltaMerchant").value;
	    if(merchant == "Select Merchant") {
			 alert("Select a merchant !!");
			 return;
		 }
         $("#deltaRefund").submit();
	}
	
    function submitDeltaRefundRRNForm() {
	    document.getElementById("deltaRefundButtonIdentifier").value = "RRN";
	    var merchant = document.getElementById("deltaMerchant").value;
	    if(merchant == "Select Merchant") {
			 alert("Select a merchant !!");
			 return;
		 }
         $("#deltaRefund").submit();
    } 
    
    function submitRefundValidationBasicForm() {
	    document.getElementById("refundValidationButtonIdentifier").value = "BASIC";
	    var merchant = document.getElementById("validationMerchant").value;
	    if(merchant == "Select Merchant") {
			 alert("Select a merchant !!");
			 return;
		 }
	     $("#refundValidationAction").submit();
	}
    
    function submitRefundValidationRRNForm() {
	    document.getElementById("refundValidationButtonIdentifier").value = "RRN";
	    var merchant = document.getElementById("validationMerchant").value;
	    if(merchant == "Select Merchant") {
			 alert("Select a merchant !!");
			 return;
		 }
	     $("#refundValidationAction").submit();
    } 
</script>	
<script type="text/javascript">
	 function handleChange() {
     }
	$(document).ready(function() {
		
		$(function() {
			$("#validationDateFrom").datepicker({
				prevText : "click for previous months",
				nextText : "click for next months",
				showOtherMonths : true,
				dateFormat : 'dd-mm-yy',
				selectOtherMonths : false,
				maxDate : new Date()
			});
			$("#validationDateTo").datepicker({
				prevText : "click for previous months",
				nextText : "click for next months",
				showOtherMonths : true,
				dateFormat : 'dd-mm-yy',
				selectOtherMonths : false,
				maxDate : new Date()
			});
		});
		$(function() {
			var today = new Date();
			$('#validationDateTo').val($.datepicker.formatDate('dd-mm-yy', today));
			$('#validationDateFrom').val($.datepicker.formatDate('dd-mm-yy', today));
			

		});
	});
	
			
</script>
	
<script type="text/javascript">
	 function handleChange() {
     }
	$(document).ready(function() {
		
		$(function() {
			$("#deltaDateFrom").datepicker({
				prevText : "click for previous months",
				nextText : "click for next months",
				showOtherMonths : true,
				dateFormat : 'dd-mm-yy',
				selectOtherMonths : false,
				maxDate : new Date()
			});
			$("#deltaDateTo").datepicker({
				prevText : "click for previous months",
				nextText : "click for next months",
				showOtherMonths : true,
				dateFormat : 'dd-mm-yy',
				selectOtherMonths : false,
				maxDate : new Date()
			});
		});
		$(function() {
			var today = new Date();
			$('#deltaDateTo').val($.datepicker.formatDate('dd-mm-yy', today));
			$('#deltaDateFrom').val($.datepicker.formatDate('dd-mm-yy', today));
			

		});
	});
	
			
</script>

<script type="text/javascript">
	 function handleChange() {
     }
	$(document).ready(function() {
		
		$(function() {
			$("#refundDateFrom").datepicker({
				prevText : "click for previous months",
				nextText : "click for next months",
				showOtherMonths : true,
				dateFormat : 'dd-mm-yy',
				selectOtherMonths : false,
				maxDate : new Date()
			});
			$("#refundDateTo").datepicker({
				prevText : "click for previous months",
				nextText : "click for next months",
				showOtherMonths : true,
				dateFormat : 'dd-mm-yy',
				selectOtherMonths : false,
				maxDate : new Date()
			});
		});
		$(function() {
			var today = new Date();
			$('#refundDateTo').val($.datepicker.formatDate('dd-mm-yy', today));
			$('#refundDateFrom').val($.datepicker.formatDate('dd-mm-yy', today));
			

		});
	});
	
			
</script>

<script type="text/javascript">
	    function handleChange() {
			
     }

	$(document).ready(function() {
		
		$(function() {
			$("#dateFrom").datepicker({
				prevText : "click for previous months",
				nextText : "click for next months",
				showOtherMonths : true,
				dateFormat : 'dd-mm-yy',
				selectOtherMonths : false,
				maxDate : new Date()
			});
			$("#dateTo").datepicker({
				prevText : "click for previous months",
				nextText : "click for next months",
				showOtherMonths : true,
				dateFormat : 'dd-mm-yy',
				selectOtherMonths : false,
				maxDate : new Date()
			});
		});
		$(function() {
			var today = new Date();
			$('#dateTo').val($.datepicker.formatDate('dd-mm-yy', today));
			$('#dateFrom').val($.datepicker.formatDate('dd-mm-yy', today));
			

		});
	});
	
			
</script>




<script type="text/javascript">
	    function handleChange() {
			
     }

	$(document).ready(function() {
		
		$(function() {
			$("#saleDate").datepicker({
				prevText : "click for previous months",
				nextText : "click for next months",
				showOtherMonths : true,
				dateFormat : 'dd-mm-yy',
				selectOtherMonths : false,
				maxDate : new Date()
			});
			/* $("#saleDateTo").datepicker({
				prevText : "click for previous months",
				nextText : "click for next months",
				showOtherMonths : true,
				dateFormat : 'dd-mm-yy',
				selectOtherMonths : false,
				maxDate : new Date()
			}); */
		});
		$(function() {
			var today = new Date();
			/* $('#saleDateTo').val($.datepicker.formatDate('dd-mm-yy', today)); */
			$('#saleDate').val($.datepicker.formatDate('dd-mm-yy', today));
			

		});
	});
	
			
</script>

<script type="text/javascript">
		$("#form").submit(function(e) {
		  e.preventDefault();
		  var checkUploadFileExtension =  $('input[type=file]').val().replace(/C:\\fakepath\\/i, '').split('.').pop();

		  if(checkUploadFileExtension === 'txt') {
			$('#success').empty().text('File Uploaded Successfully');
			$('#errors').empty().text('');
             return false;
		  } else {
			$('#errors').empty().text('Please Choose Any Text File');
			$('#success').empty().text('');
             return false;
		  }
		});
</script>

<script type="text/javascript">
		$("#form2").submit(function(e) {
		  e.preventDefault();
		  var checkUploadFileExtension =  $('input[type=file]').val().replace(/C:\\fakepath\\/i, '').split('.').pop();

		  if(checkUploadFileExtension === 'txt') {
			$('#success').empty().text('File Uploaded Successfully');
			$('#errors').empty().text('');
             return false;
		  } else {
			$('#errors').empty().text('Please Choose Any Text File');
			$('#success').empty().text('');
             return false;
		  }
		});
</script>

</body>
</html>