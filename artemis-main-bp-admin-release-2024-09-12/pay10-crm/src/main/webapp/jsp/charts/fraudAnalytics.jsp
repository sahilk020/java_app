<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="/struts-tags" prefix="s"%>
<html dir="ltr" lang="en-US">
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.Locale"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.text.Format"%>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>FRM Line Chart</title>
<link href="../assets/css/style.bundle.css" rel="stylesheet"
	type="text/css" />
<link href="../assets/css/custom/custom-style.css" rel="stylesheet"
	type="text/css" />
	


<script type="text/javascript" src="../js/jquery.min.js"></script>
<script src="../js/core/popper.min.js"></script>
<script src="../js/plugins/perfect-scrollbar.jquery.min.js"></script>
<!-- Control Center for Material Dashboard: parallax effects, scripts for the example pages etc -->
<script src="../assets/plugins/global/plugins.bundle.js"></script>
<script src="../assets/js/scripts.bundle.js"></script>
<script type="text/javascript" src="../js/sweetalert.js"></script>
<script type="text/javascript" src="../js/offus.js"></script>
<script type="text/javascript" src="../js/bootstrap4.min.js"></script>
<script type="text/javascript" src="../js/canvasChart/canvas.min.js"></script> 
<script type="text/javascript" src="../js/canvasChart/multiSeriesLineChartDataPopulate.js"></script>




<%
DecimalFormat d = new DecimalFormat("0.00");
Date d1 = new Date();
SimpleDateFormat df = new SimpleDateFormat("dd-MM-YYYY");
String currentDate = df.format(d1);
%>



<script type="text/javascript">
	$(function() {
		var start=null; 
		var end=null;
		const dateRange = '<%=(String) request.getAttribute("dateRange")%>';
		if(dateRange!='null'){
			//07/13/2022 - 07/15/2022
			const a=dateRange.split(" ");
			const startdate=a[0].split("/");
			const enddate=a[2].split("/");
			start= moment().set({'month':parseInt(startdate[0])-1,'date':parseInt(startdate[1]),'year':parseInt(startdate[2])});
			end = moment().set({'month':parseInt(enddate[0])-1,'date':parseInt(enddate[1]),'year':parseInt(enddate[2])});	

		}else{
			start= moment().subtract(29, 'days');
			end = moment();
		}
		

		function cb(start, end) {
			$('#reportrange span').html(
					start.format('DD/MM/YYYY') + ' - '
							+ end.format('DD/MM/YYYY'));
		}

		$('#reportrange').daterangepicker(
				{
					startDate : start,
					endDate : end,
					maxDate: new Date(),
					dateLimit : {
						days : 30
					},

					ranges : {
						'Today' : [ moment(), moment() ],
						'Yesterday' : [ moment().subtract(1, 'days'),
								moment().subtract(1, 'days') ],
						'Last 7 Days' : [ moment().subtract(6, 'days'),
								moment() ],
						'Last 30 Days' : [ moment().subtract(29, 'days'),
								moment() ],
						'This Month' : [ moment().startOf('month'),
								moment().endOf('month') ],
						'Last Month' : [
								moment().subtract(1, 'month').startOf('month'),
								moment().subtract(1, 'month').endOf('month') ]
					}
				}, cb);

		cb(start, end);

	});



	$(function() {
		var start=null; 
		var end=null;
		
		const dateRange = '<%=(String) request.getAttribute("dateRange")%>';
		if(dateRange!='null'){
			//07/13/2022 - 07/15/2022
			const a=dateRange.split(" ");
			const startdate=a[0].split("/");
			const enddate=a[2].split("/");
			start= moment().set({'month':parseInt(startdate[0])-1,'date':parseInt(startdate[1]),'year':parseInt(startdate[2])});
			end = moment().set({'month':parseInt(enddate[0])-1,'date':parseInt(enddate[1]),'year':parseInt(enddate[2])});	

		}else{
			start= moment().subtract(29, 'days');
			end = moment();
		}
		

		function cb(start, end) {
			$('#reportrange span').html(
					start.format('DD/MM/YYYY') + ' - '
							+ end.format('DD/MM/YYYY'));

							$('#reportrange1 span').html(
					start.format('DD/MM/YYYY') + ' - '
							+ end.format('DD/MM/YYYY'));

							$('#reportrange2 span').html(
					start.format('DD/MM/YYYY') + ' - '
							+ end.format('DD/MM/YYYY'));
		}

		$('#reportrange').daterangepicker(
				{
					startDate : start,
					endDate : end,
					maxDate: new Date(),
					dateLimit : {
						days : 30
					},

					ranges : {
						'Today' : [ moment(), moment() ],
						'Yesterday' : [ moment().subtract(1, 'days'),
								moment().subtract(1, 'days') ],
						'Last 7 Days' : [ moment().subtract(6, 'days'),
								moment() ],
						'Last 30 Days' : [ moment().subtract(29, 'days'),
								moment() ],
						'This Month' : [ moment().startOf('month'),
								moment().endOf('month') ],
						'Last Month' : [
								moment().subtract(1, 'month').startOf('month'),
								moment().subtract(1, 'month').endOf('month') ]
					}
				}, cb);
				$('#reportrange1').daterangepicker(
				{
					startDate : start,
					endDate : end,
					maxDate: new Date(),
					dateLimit : {
						days : 30
					},

					ranges : {
						'Today' : [ moment(), moment() ],
						'Yesterday' : [ moment().subtract(1, 'days'),
								moment().subtract(1, 'days') ],
						'Last 7 Days' : [ moment().subtract(6, 'days'),
								moment() ],
						'Last 30 Days' : [ moment().subtract(29, 'days'),
								moment() ],
						'This Month' : [ moment().startOf('month'),
								moment().endOf('month') ],
						'Last Month' : [
								moment().subtract(1, 'month').startOf('month'),
								moment().subtract(1, 'month').endOf('month') ]
					}
				}, cb);
				$('#reportrange2').daterangepicker(
				{
					startDate : start,
					endDate : end,
					maxDate: new Date(),
					dateLimit : {
						days : 30
					},

					ranges : {
						'Today' : [ moment(), moment() ],
						'Yesterday' : [ moment().subtract(1, 'days'),
								moment().subtract(1, 'days') ],
						'Last 7 Days' : [ moment().subtract(6, 'days'),
								moment() ],
						'Last 30 Days' : [ moment().subtract(29, 'days'),
								moment() ],
						'This Month' : [ moment().startOf('month'),
								moment().endOf('month') ],
						'Last Month' : [
								moment().subtract(1, 'month').startOf('month'),
								moment().subtract(1, 'month').endOf('month') ]
					}
				}, cb);
		cb(start, end);

	});



	
</script>
<script>

function fetchStatusWiseChart(chartType){
	debugger
	var merchant=$('#merchantPayId').val();
	if(merchant==''|| merchant==null){
		   error_merchant_pay_id = 'please select merchant';
           $('#error_merchant_pay_id').text(error_merchant_pay_id);
           $('#merchant_pay_id').addClass('error');
      return false;
		}
	  else {
          error_merchant_pay_id = '';
            $('#error_merchant_pay_id').text(error_merchant_pay_id);
            $('#merchant_pay_id').removeClass('error');
        
	var obj;
	var chartName='Transactions By';

 if(chartType=='merchantPaymentType'){
	obj= {
		        
				"paymentType"      : $("#paymentMethods").val(),
				"range"  : $("#reportrange2").val(),
				"type"  :"PAYMENT_TYPE_CHART",
				"payId" : merchant
				
			  }

			  chartName =chartName+' MERCHANT PAYMENT TYPE CHART';
}



	//populateData("Pay10 Transaction Chart By Status")
	

	var id = '<s:property value="id"/>';
	var urls = new URL(window.location.href);
	var domain = urls.origin;
	$.ajax({
	type : "POST",
    url: domain+"/crmws/dashboard/fraudlineChartData",
     data: JSON.stringify(obj),
     contentType: "application/json",
     success : function(data) {
         
    	 populateFraudData(data,chartName);
      }  
	   }); 
	  }
	$('#dLabel').css('display','none');

}
		

	
	</script>

<style>
.error {
	font-family: "Times New Roman";
	width: 100%;
	margin-top: 8px;
	color:red;
}
@media ( min-width : 992px) {
	.col-lg-3 {
		max-width: 30% !important;
	}
}
.tab-content>.tab-pane{
border:unset !important;
}
.nav-tabs .nav-item .nav-link.active {
    background-color: rgb(68 97 138) !important;
}
.nav-tabs .nav-item .nav-link{
	background-color:#202F4Bbf !important;
}
.nav-tabs .nav-item .nav-link.active {
    background-color:#202F4B !important;
    color: #262424;
}

#fetch{
 margin-top:15px;
 }
 #fetch1{
 margin-top:15px;
 }
 #fetch2{
 margin-top:15px;
 }

</style>
</head>
<body id="mainBody">
	<div style="overflow: scroll !important;">
		<div id="loader-wrapper"
			style="width: 100%; height: 100%; display: none;"></div>
			<!--begin::Root-->
		<div class="d-flex flex-column flex-root">
		<!--begin::Content-->
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
						<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">
							FRM Analytics</h1>
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
							<li class="breadcrumb-item text-muted">Fraud Prevention</li>
							<!--end::Item-->
							<!--begin::Item-->
							<li class="breadcrumb-item"><span
								class="bullet bg-gray-200 w-5px h-2px"></span></li>
							<!--end::Item-->
							<!--begin::Item-->
							<li class="breadcrumb-item text-dark">FRM Analytics</li>
							<!--end::Item-->
						</ul>
						<!--end::Breadcrumb-->
					</div>
					<!--end::Page title-->

				</div>
			</div>
			<!--end::Container-->
			<!-- <div class="card-body ">
				<div class="container">
					<div class="row"> -->
			<div class="post d-flex flex-column-fluid" id="kt_post">
				<!--begin::Container-->
				<div id="kt_content_container" class="container-xxl">
							<div class="row my-5">
		<div class="col">
		<div class="card">		
		<div class="card-body ">
			
		<div class="row">
			<div class="col-12 col-md-12 col-lg-12">
			
			  <div class="tab-content mt-3">
			
			  <div id="mopwise" role="tabpanel" aria-labelledby="mopwise-tab">
				<form id="files2" method="post" enctype="multipart/form-data">

					<div class="row">
						<div class="col-lg-3 col">
							<label>Select Date </label><br>
							<div
								style="border: 1px solid #f9c14a59; background: #fff; cursor: pointer; font-size: 14px; position: relative; display: inline-flex;">
								<input type="text" id="reportrange2" name="dateRange" class="form-control form-control-solid ps-12">
								<!-- <i class="fa fa-calendar mt-1"></i>&nbsp; <i
									class="fa fa-caret-down mt-1"></i> --> <span id="dateRange"
									style="font-size: 14px; color: black; margin-top: 5px;">
								</span>
							</div>
						</div>
						
						<div class="col-sm-6 col-lg-3">
								
							<label for="">Payment Type</label> <b>*</b><br />
								<div class="form-group">
									<s:select class="form-select form-select-solid"
										list="@com.pay10.commons.util.PaymentTypeUI@values()" listKey="code" listValue="name"
										id="paymentMethods" headerKey="All" headerValue="ALL" data-control="select2"/>
								</div>
							  </div>
							  
				
							
								<div class="col-sm-6 col-lg-3">
																	<label>Merchant <b>*</b></label><br>
																	<div class="form-group">
																		<s:select name="merchantPayId"
																			class="form-select form-select-solid"
																			id="merchantPayId" headerKey=""
																			headerValue="Select Merchant" list="listMerchants"
																			listKey="payId" listValue="businessName"
																			data-control="select2" />
																			<span id="error_merchant_pay_id" class="error"></span>
																	</div>

																</div>
                              								
							 
							  <div class="col mt-2">
								<button type="button" class="btn btn-primary submitbtn" onclick="fetchStatusWiseChart('merchantPaymentType')";
									id="fetch2">FETCH</button>


							</div>
					</div>

				</form>
						  </div>
						  <div class="dropdown keep-open" id="white-bg" style="color: white;margin-bottom: -28px;z-index: 9999;position: absolute;;display:none">
    <!-- Dropdown Button -->
    <button id="dLabel" role="button" href="#" data-toggle="dropdown" data-target="#" class="btn btn-primary" style="
    height: 20px;
    box-shadow : unset !important;
    color: white;
    background: white;
">
       <i class="fa fa-filter"></i>
    </button>
</div>
						  <div id="chartContainer" style="height: 500px; width: 100%; margin-top: 10px;"></div>

			 
			</div>     
			</div>
		</div>
	  </div>
	</div>
	</div>
</div>
				</div>
			</div>
</body>
</html>
