<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Download Transactions Report</title>

<link href="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.css" rel="stylesheet" type="text/css" />
<link href="../assets/plugins/custom/datatables/datatables.bundle.css" rel="stylesheet" type="text/css" />
<link href="../assets/plugins/global/plugins.bundle.css" rel="stylesheet" type="text/css" />
<link href="../assets/css/style.bundle.css" rel="stylesheet" type="text/css" />
<link href="../assets/css/custom/custom-style.css" rel="stylesheet" type="text/css" />

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

<script type="text/javascript">
	$(document).ready(function() {

		// Initialize select2
		$(".adminMerchants").select2();
	});
</script>

<script type="text/javascript">

	$(document).ready(function() {
		$(function() {
			var today = new Date();
			//$('#kt_datepicker_2').val($.datepicker.formatDate('dd-mm-yy', today));
			//$('#kt_datepicker_1').val($.datepicker.formatDate('dd-mm-yy', today));
			var transFrom = flatpickr("#kt_datepicker_1", {}).selectedDates[0];
			//var transFrom = $.flatpickr.parseDate('dd-mm-yy', $('#kt_datepicker_1').val());
			var transTo = flatpickr("#kt_datepicker_2", {}).selectedDates[0];
			//var transTo = $.datepicker.parseDate('dd-mm-yy', $('#kt_datepicker_2').val());
			var dateFrom=convert(transFrom);
			var dateTo=convert(transTo);
			//$("#kt_datepicker_1").val(dateFrom);
			//$("#kt_datepicker_2").val(dateTo);
			$("#kt_datepicker_1").flatpickr({
		showOtherMonths: true,
					dateFormat: 'Y-m-d',
					selectOtherMonths: false,
					defaultDate: "today",
					maxDate: new Date()
		});
	$("#kt_datepicker_2").flatpickr({
		showOtherMonths: true,
					dateFormat: 'Y-m-d',
					selectOtherMonths: false,
					defaultDate: "today",
					maxDate: new Date()
		});
		});

		$(function() {
			var datepick = $.datepicker;
			var table = $('#chargebackDataTable').DataTable();
			$('#chargebackDataTable tbody').on('click', 'td', function() {
				submitForm(table, this);

			});
		});
	});
	
	
	
</script>

<script>
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


function getCheckBoxValue(uid){
	 var allInputCheckBox = document.getElementsByClassName("myCheckBox"+uid);

  		var allSelectedAquirer = [];
  		for(var i=0; i<allInputCheckBox.length; i++){

  			if(allInputCheckBox[i].checked){
  				allSelectedAquirer.push(allInputCheckBox[i].value);
  			}
  		}
		var test = document.getElementById('selectBox'+uid);
  		document.getElementById('selectBox'+uid).setAttribute('title', allSelectedAquirer.join());
  		if(allSelectedAquirer.join().length>28){
  			var res = allSelectedAquirer.join().substring(0,27);
  			document.querySelector("#selectBox"+uid+" option").innerHTML = res+'...............';
  		}else if(allSelectedAquirer.join().length==0){
  			document.querySelector("#selectBox"+uid+" option").innerHTML = 'ALL';
  		}else{
  			document.querySelector("#selectBox"+uid+" option").innerHTML = allSelectedAquirer.join();
  		}
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
  .divalignment{
	  margin-top: -30px !important;
  }
  
  .case-design{
	  text-decoration:none;
	  cursor: default !important;
  }
  .my_class:hover{
	  color: white !important;
  }
 .multiselect {
    width: 210px;
	display:block;
	margin-left:-20px;	
 }
  .selectBox {
  position: relative;
 }

#checkboxes {
  display: none;
  border: 1px #dadada solid;
  height:300px;
  overflow-y: scroll;
  position:Absolute;
  background:#fff;
  z-index:1;
  margin-left:5px;
}

#checkboxes label {
  width: 74%;
}
#checkboxes input {
  width:18%;

}
.selectBox select {
  width: 95%;
  
}

.OtherList input{
    vertical-align: top;
    float: left;
    margin-left: 10px !important;
}
.OtherList label{
     vertical-align: middle;
	display: block;
	padding-bottom: 10px;

}

/* .btn {
	padding: 3px 7px!important; 
	font-size: 12px!important; 
} */

/*tr td.my_class{color:#000 !important; cursor: default !important; text-decoration: none;}*/
tr td.my_class{
	cursor: pointer;
}
tr td.my_class:hover{
	cursor: pointer !important;
}
tr th.my_class:hover{
	color: #fff !important;
}

.select2-container{
	width: 100% !important;
}
.clearfix:after{
	display: block;
	visibility: hidden;
	line-height: 0;
	height: 0;
	clear: both;
	content: '.';
}
#popup{
	position: fixed;
	top:0px;
	left: 0px;
	background: rgba(0,0,0,0.7);
	width: 100%;
	height: 100%;
	z-index:999; 
	display: none;
}
.innerpopupDv{
	width: 600px;
    margin: 80px auto;
    background: #fff;
    padding: 3px 10px;
    border-radius: 10px;
}
.btn-custom {
    margin-top: 5px;
    height: 27px;
    border: 1px solid #5e68ab;
    background: #5e68ab;
    padding: 5px;
    font: bold 12px Tahoma;
    color: #fff;
    cursor: pointer;
    border-radius: 5px;
}
#loader-wrapper .loader-section.section-left, #loader-wrapper .loader-section.section-right{
	background: rgba(225,225,225,0.6) !important;
	width: 50% !important;
}
.invoicetable{
	float: none;
}
.innerpopupDv h2{
	font-size: 12px;
    padding: 5px;
}

.submit-btn {
	background-color:#496cb6;
	display: block;
    width: 100%;
    height: 30px;
    padding: 3px 4px;
    font-size: 14px;
    line-height: 1.42857143;
    color: #fff;
    border: 1px solid #ccc;
    border-radius: 4px;
	margin-top:15px;
	margin-bottom: -20px;
}
.content{
	min-height:600px;
}
#acquirerBox{
display:none;
}
.input-control{
	margin-left: unset !important;
}
</style>
<script>
	    function handleChange() {
		var transFrom = document.getElementById('kt_datepicker_1').value;
		var transTo =document.getElementById('kt_datepicker_2').value;
		var dateFrom=new Date(Date.parse(transFrom));
		var dateTo=new Date(Date.parse(transTo));

			if (dateFrom == null || dateTo == null) {
				alert('Enter date value');
				return false;
			}

			if (dateFrom > dateTo) {
				alert('From date must be before the to date');
				$('#kt_datepicker_1').focus();
				$("#kt_datepicker_2").flatpickr({
		showOtherMonths: true,
					dateFormat: 'Y-m-d',
					selectOtherMonths: false,
					defaultDate: 'today',
					maxDate: new Date()
		});

				return false;
			}
			if (dateTo - dateFrom > 31 * 86400000) {
				alert('No. of days can not be more than 31');
				$('#kt_datepicker_1').focus();
				$("#kt_datepicker_2").flatpickr({
		showOtherMonths: true,
					dateFormat: 'Y-m-d',
					selectOtherMonths: false,
					defaultDate: 'today',
					maxDate: new Date()
		});

		return false;
		}
		}
		</script>


</head>
<body>
	<div class="content d-flex flex-column flex-column-fluid" id="kt_content">
		<!--begin::Toolbar-->
		<div class="toolbar" id="kt_toolbar">
			<!--begin::Container-->
			<div id="kt_toolbar_container" class="container-fluid d-flex flex-stack">
				<!--begin::Page title-->
				<div data-kt-swapper="true" data-kt-swapper-mode="prepend" data-kt-swapper-parent="{default: '#kt_content_container', 'lg': '#kt_toolbar_container'}" class="page-title d-flex align-items-center me-3 flex-wrap mb-5 mb-lg-0 lh-1">
					<!--begin::Title-->
					<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">Download Report</h1>
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
						<li class="breadcrumb-item text-muted">Transaction Reports</li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item">
							<span class="bullet bg-gray-200 w-5px h-2px"></span>
						</li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item text-dark">Download Report</li>
						<!--end::Item-->
					</ul>
					<!--end::Breadcrumb-->
				</div>
				<!--end::Page title-->

			</div>
			<!--end::Container-->
		</div>
		<!--end::Toolbar-->
		<!--begin::Post-->
		<div class="post d-flex flex-column-fluid" id="kt_post">
			<!--begin::Container-->
			<div id="kt_content_container" class="container-xxl">
				<form action="downloadTransactionsReportAction" class="form mb-15" method="post" id="downloadTransactionsReportAction">
					<div class="row my-5">
						<div class="col">
							<div class="card">
								<div class="card-body">
									<!--begin::Input group-->
									<div class="row g-9 mb-8">
										<!--begin::Col-->
										<div class="col-md-4 fv-row">
											<label class="d-flex align-items-center fs-6 fw-bold mb-2">
												<span class="">Channel</span>
											</label>
											<!--end::Label-->
											<!-- <select name="currency" data-control="select2" data-placeholder="All" class="form-select form-select-solid select2-hidden-accessible" data-hide-search="true" data-select2-id="select2-data-13-u89f" tabindex="-1" aria-hidden="true" data-kt-initialized="1">
												<option value="ALL" data-select2-id="select2-data-15-gj6i">ALL</option>
											</select><span class="select2 select2-container select2-container--bootstrap5" dir="ltr" data-select2-id="select2-data-14-ibnz" style="width: 100%;"><span class="selection"><span class="select2-selection select2-selection--single form-select form-select-solid" role="combobox" aria-haspopup="true" aria-expanded="false" tabindex="0" aria-disabled="false" aria-labelledby="select2-currency-6v-container" aria-controls="select2-currency-6v-container"><span class="select2-selection__rendered" id="select2-currency-6v-container" role="textbox" aria-readonly="true" title="ALL">ALL</span><span class="select2-selection__arrow" role="presentation"><b role="presentation"></b></span></span></span><span class="dropdown-wrapper" aria-hidden="true"></span></span> -->

											<!-- <s:select name="currency" id="currency" headerValue="ALL"
											headerKey="ALL" list="currencyMap" class="form-select form-select-solid adminMerchants" /> -->
											<select name="Channel" id="Channel"
														class="form-select form-select-solid adminMerchants"
														onchange="getPaymentType()">
														<option value="ALL">All</option>
														<option value="FIAT">Fiat</option>
														<option value="Crypto">Crypto</option>
													</select>

										</div>
										<div class="col-md-4 fv-row">
											<label class="d-flex align-items-center fs-6 fw-bold mb-2">
												<span class="">Payment Method</span>
											</label>
											<!--end::Label-->
											<!-- <select name="paymentmethod" data-control="select2" data-placeholder="Payment Method" class="form-select form-select-solid select2-hidden-accessible" data-hide-search="true" data-select2-id="select2-data-4-8lvr" tabindex="-1" aria-hidden="true" data-kt-initialized="1">
												<option value="" data-select2-id="select2-data-6-qmkd">Payment Method</option>
												<option value="ALL">ALL</option>
												<option value="CC">Credit Card</option>
												<option value="DC">Debit Card</option>
												<option value="NB">Net Banking</option>
												<option value="EM">EMI</option>
												<option value="WL">Wallet</option>
												<option value="RP">Recurring Payment</option>
												<option value="EX">Express Pay</option>
												<option value="DP">Debit Card With Pin</option>
												<option value="UP">UPI</option>
												<option value="AD">AutoDebit</option>
												<option value="PC">Prepaid Card</option>
												<option value="QR">QR CODE</option>
											</select><span class="select2 select2-container select2-container--bootstrap5" dir="ltr" data-select2-id="select2-data-5-xvnj" style="width: 100%;"><span class="selection"><span class="select2-selection select2-selection--single form-select form-select-solid" role="combobox" aria-haspopup="true" aria-expanded="false" tabindex="0" aria-disabled="false" aria-labelledby="select2-paymentmethod-0c-container" aria-controls="select2-paymentmethod-0c-container"><span class="select2-selection__rendered" id="select2-paymentmethod-0c-container" role="textbox" aria-readonly="true" title="Payment Method"><span class="select2-selection__placeholder">Payment Method</span></span><span class="select2-selection__arrow" role="presentation"><b role="presentation"></b></span></span></span><span class="dropdown-wrapper" aria-hidden="true"></span></span> -->

											<!-- <s:select headerKey="ALL" headerValue="ALL" class="form-select form-select-solid adminMerchants"
											list="@com.pay10.commons.util.PaymentType@values()"
											listValue="name" listKey="code" name="paymentType"
											id="paymentType" autocomplete="off" value="" /> -->

											<select name="paymentType" id="paymentType"
														class="form-select form-select-solid adminMerchants">

													</select>
										</div>
										<div class="col-md-4 fv-row">
											<label class="d-flex align-items-center fs-6 fw-bold mb-2">
												<span class="">Transaction Region</span>
											</label>
											<!--end::Label-->
											<!-- <select name="paymentmethod" data-control="select2" data-placeholder="Transaction Region" class="form-select form-select-solid select2-hidden-accessible" data-hide-search="true" data-select2-id="select2-data-7-xi92" tabindex="-1" aria-hidden="true" data-kt-initialized="1">
												<option value="ALL" data-select2-id="select2-data-9-i2ku">ALL</option>
												<option value="INTERNATIONAL">International</option>
												<option value="DOMESTIC">Domestic</option>
											</select><span class="select2 select2-container select2-container--bootstrap5" dir="ltr" data-select2-id="select2-data-8-qjda" style="width: 100%;"><span class="selection"><span class="select2-selection select2-selection--single form-select form-select-solid" role="combobox" aria-haspopup="true" aria-expanded="false" tabindex="0" aria-disabled="false" aria-labelledby="select2-paymentmethod-p3-container" aria-controls="select2-paymentmethod-p3-container"><span class="select2-selection__rendered" id="select2-paymentmethod-p3-container" role="textbox" aria-readonly="true" title="ALL">ALL</span><span class="select2-selection__arrow" role="presentation"><b role="presentation"></b></span></span></span><span class="dropdown-wrapper" aria-hidden="true"></span></span> -->

											<s:select headerKey="ALL" headerValue="ALL" class="form-select form-select-solid adminMerchants"
											list="#{'INTERNATIONAL':'International','DOMESTIC':'Domestic'}" name="paymentsRegion" id = "paymentsRegion" />

										</div>
									</div>
									<div class="row g-9 mb-8">
										<!--begin::Col-->
										<div class="col-md-4 fv-row">
											<label class="d-flex align-items-center fs-6 fw-bold mb-2">
												<span class="">Transaction Type</span>
											</label>
											<!--end::Label-->
											<!-- <select name="paymentmethod" data-control="select2" data-placeholder="Transaction Region" class="form-select form-select-solid select2-hidden-accessible" data-hide-search="true" data-select2-id="select2-data-10-xujz" tabindex="-1" aria-hidden="true" data-kt-initialized="1">
												<option value="ALL" data-select2-id="select2-data-12-1cht">ALL</option>
												<option value="SALE">SALE</option>
												<option value="REFUND">REFUND</option>
											</select><span class="select2 select2-container select2-container--bootstrap5" dir="ltr" data-select2-id="select2-data-11-tdol" style="width: 100%;"><span class="selection"><span class="select2-selection select2-selection--single form-select form-select-solid" role="combobox" aria-haspopup="true" aria-expanded="false" tabindex="0" aria-disabled="false" aria-labelledby="select2-paymentmethod-oc-container" aria-controls="select2-paymentmethod-oc-container"><span class="select2-selection__rendered" id="select2-paymentmethod-oc-container" role="textbox" aria-readonly="true" title="ALL">ALL</span><span class="select2-selection__arrow" role="presentation"><b role="presentation"></b></span></span></span><span class="dropdown-wrapper" aria-hidden="true"></span></span> -->

											<s:select headerKey="ALL" headerValue="ALL" class="form-select form-select-solid adminMerchants"
											list="txnTypelist"
											listValue="name" listKey="code" name="transactionType"
											id="transactionType" autocomplete="off" value="name" />

										</div>

										<div class="col-md-4 fv-row" id="acquirerBox">
										<s:if
                                        													test="%{#session.USER.UserType.name()=='ADMIN'|| #session.USER.UserType.name()=='SUBADMIN' || #session.USER_TYPE.name()=='SUPERADMIN'}">
                                        											<label class="d-flex align-items-center fs-6 fw-bold mb-2">
                                        												<span class="">Acquirer</span>
                                        											</label>
                                        											<!--end::Label-->

                                                                                    <div id="checkboxes" onclick="getCheckBoxValue(1)">
                                        											<s:select headerKey="ALL" headerValue="ALL" class="form-select form-select-solid myCheckBox1"
                                        											list="@com.pay10.commons.util.AcquirerTypeUI@values()"
                                        											listValue="name" listKey="code" name="acquirer"
                                        											id="acquirer" autocomplete="off" value="acquirer"
                                        											onclick="showCheckboxes(event,1)"/>
                                        											</div>
                                                                                    </s:if>
												                                    <s:else>
												                                    </s:else>
                                        										</div>


										<div class="col-md-4 fv-row">
											<label class="d-flex align-items-center fs-6 fw-bold mb-2">
												<span class="">Date From</span>
											</label>
											<!--end::Label-->
											<div class="position-relative d-flex align-items-center">
												<!--begin::Icon-->
												<!--begin::Svg Icon | path: icons/duotune/general/gen014.svg-->
												<span class="svg-icon svg-icon-2 position-absolute mx-4">
													<svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
														<path opacity="0.3" d="M21 22H3C2.4 22 2 21.6 2 21V5C2 4.4 2.4 4 3 4H21C21.6 4 22 4.4 22 5V21C22 21.6 21.6 22 21 22Z" fill="currentColor"></path>
														<path d="M6 6C5.4 6 5 5.6 5 5V3C5 2.4 5.4 2 6 2C6.6 2 7 2.4 7 3V5C7 5.6 6.6 6 6 6ZM11 5V3C11 2.4 10.6 2 10 2C9.4 2 9 2.4 9 3V5C9 5.6 9.4 6 10 6C10.6 6 11 5.6 11 5ZM15 5V3C15 2.4 14.6 2 14 2C13.4 2 13 2.4 13 3V5C13 5.6 13.4 6 14 6C14.6 6 15 5.6 15 5ZM19 5V3C19 2.4 18.6 2 18 2C17.4 2 17 2.4 17 3V5C17 5.6 17.4 6 18 6C18.6 6 19 5.6 19 5Z" fill="currentColor"></path>
														<path d="M8.8 13.1C9.2 13.1 9.5 13 9.7 12.8C9.9 12.6 10.1 12.3 10.1 11.9C10.1 11.6 10 11.3 9.8 11.1C9.6 10.9 9.3 10.8 9 10.8C8.8 10.8 8.59999 10.8 8.39999 10.9C8.19999 11 8.1 11.1 8 11.2C7.9 11.3 7.8 11.4 7.7 11.6C7.6 11.8 7.5 11.9 7.5 12.1C7.5 12.2 7.4 12.2 7.3 12.3C7.2 12.4 7.09999 12.4 6.89999 12.4C6.69999 12.4 6.6 12.3 6.5 12.2C6.4 12.1 6.3 11.9 6.3 11.7C6.3 11.5 6.4 11.3 6.5 11.1C6.6 10.9 6.8 10.7 7 10.5C7.2 10.3 7.49999 10.1 7.89999 10C8.29999 9.90003 8.60001 9.80003 9.10001 9.80003C9.50001 9.80003 9.80001 9.90003 10.1 10C10.4 10.1 10.7 10.3 10.9 10.4C11.1 10.5 11.3 10.8 11.4 11.1C11.5 11.4 11.6 11.6 11.6 11.9C11.6 12.3 11.5 12.6 11.3 12.9C11.1 13.2 10.9 13.5 10.6 13.7C10.9 13.9 11.2 14.1 11.4 14.3C11.6 14.5 11.8 14.7 11.9 15C12 15.3 12.1 15.5 12.1 15.8C12.1 16.2 12 16.5 11.9 16.8C11.8 17.1 11.5 17.4 11.3 17.7C11.1 18 10.7 18.2 10.3 18.3C9.9 18.4 9.5 18.5 9 18.5C8.5 18.5 8.1 18.4 7.7 18.2C7.3 18 7 17.8 6.8 17.6C6.6 17.4 6.4 17.1 6.3 16.8C6.2 16.5 6.10001 16.3 6.10001 16.1C6.10001 15.9 6.2 15.7 6.3 15.6C6.4 15.5 6.6 15.4 6.8 15.4C6.9 15.4 7.00001 15.4 7.10001 15.5C7.20001 15.6 7.3 15.6 7.3 15.7C7.5 16.2 7.7 16.6 8 16.9C8.3 17.2 8.6 17.3 9 17.3C9.2 17.3 9.5 17.2 9.7 17.1C9.9 17 10.1 16.8 10.3 16.6C10.5 16.4 10.5 16.1 10.5 15.8C10.5 15.3 10.4 15 10.1 14.7C9.80001 14.4 9.50001 14.3 9.10001 14.3C9.00001 14.3 8.9 14.3 8.7 14.3C8.5 14.3 8.39999 14.3 8.39999 14.3C8.19999 14.3 7.99999 14.2 7.89999 14.1C7.79999 14 7.7 13.8 7.7 13.7C7.7 13.5 7.79999 13.4 7.89999 13.2C7.99999 13 8.2 13 8.5 13H8.8V13.1ZM15.3 17.5V12.2C14.3 13 13.6 13.3 13.3 13.3C13.1 13.3 13 13.2 12.9 13.1C12.8 13 12.7 12.8 12.7 12.6C12.7 12.4 12.8 12.3 12.9 12.2C13 12.1 13.2 12 13.6 11.8C14.1 11.6 14.5 11.3 14.7 11.1C14.9 10.9 15.2 10.6 15.5 10.3C15.8 10 15.9 9.80003 15.9 9.70003C15.9 9.60003 16.1 9.60004 16.3 9.60004C16.5 9.60004 16.7 9.70003 16.8 9.80003C16.9 9.90003 17 10.2 17 10.5V17.2C17 18 16.7 18.4 16.2 18.4C16 18.4 15.8 18.3 15.6 18.2C15.4 18.1 15.3 17.8 15.3 17.5Z" fill="currentColor"></path>
													</svg>
												</span>
												<!--end::Svg Icon-->
												<!--end::Icon-->
												<!--begin::Datepicker-->
												<input class="form-control form-control-solid ps-12 flatpickr-input" placeholder="Select a date" name="dateFrom" id="kt_datepicker_1" type="text" readonly="readonly" onchange="handleChange()">
												<!--end::Datepicker-->
											</div>
										</div>

										<div class="col-md-4 fv-row">
											<label class="d-flex align-items-center fs-6 fw-bold mb-2">
												<span class="">Date To</span>
											</label>
											<!--end::Label-->
											<div class="position-relative d-flex align-items-center">
												<!--begin::Icon-->
												<!--begin::Svg Icon | path: icons/duotune/general/gen014.svg-->
												<span class="svg-icon svg-icon-2 position-absolute mx-4">
													<svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
														<path opacity="0.3" d="M21 22H3C2.4 22 2 21.6 2 21V5C2 4.4 2.4 4 3 4H21C21.6 4 22 4.4 22 5V21C22 21.6 21.6 22 21 22Z" fill="currentColor"></path>
														<path d="M6 6C5.4 6 5 5.6 5 5V3C5 2.4 5.4 2 6 2C6.6 2 7 2.4 7 3V5C7 5.6 6.6 6 6 6ZM11 5V3C11 2.4 10.6 2 10 2C9.4 2 9 2.4 9 3V5C9 5.6 9.4 6 10 6C10.6 6 11 5.6 11 5ZM15 5V3C15 2.4 14.6 2 14 2C13.4 2 13 2.4 13 3V5C13 5.6 13.4 6 14 6C14.6 6 15 5.6 15 5ZM19 5V3C19 2.4 18.6 2 18 2C17.4 2 17 2.4 17 3V5C17 5.6 17.4 6 18 6C18.6 6 19 5.6 19 5Z" fill="currentColor"></path>
														<path d="M8.8 13.1C9.2 13.1 9.5 13 9.7 12.8C9.9 12.6 10.1 12.3 10.1 11.9C10.1 11.6 10 11.3 9.8 11.1C9.6 10.9 9.3 10.8 9 10.8C8.8 10.8 8.59999 10.8 8.39999 10.9C8.19999 11 8.1 11.1 8 11.2C7.9 11.3 7.8 11.4 7.7 11.6C7.6 11.8 7.5 11.9 7.5 12.1C7.5 12.2 7.4 12.2 7.3 12.3C7.2 12.4 7.09999 12.4 6.89999 12.4C6.69999 12.4 6.6 12.3 6.5 12.2C6.4 12.1 6.3 11.9 6.3 11.7C6.3 11.5 6.4 11.3 6.5 11.1C6.6 10.9 6.8 10.7 7 10.5C7.2 10.3 7.49999 10.1 7.89999 10C8.29999 9.90003 8.60001 9.80003 9.10001 9.80003C9.50001 9.80003 9.80001 9.90003 10.1 10C10.4 10.1 10.7 10.3 10.9 10.4C11.1 10.5 11.3 10.8 11.4 11.1C11.5 11.4 11.6 11.6 11.6 11.9C11.6 12.3 11.5 12.6 11.3 12.9C11.1 13.2 10.9 13.5 10.6 13.7C10.9 13.9 11.2 14.1 11.4 14.3C11.6 14.5 11.8 14.7 11.9 15C12 15.3 12.1 15.5 12.1 15.8C12.1 16.2 12 16.5 11.9 16.8C11.8 17.1 11.5 17.4 11.3 17.7C11.1 18 10.7 18.2 10.3 18.3C9.9 18.4 9.5 18.5 9 18.5C8.5 18.5 8.1 18.4 7.7 18.2C7.3 18 7 17.8 6.8 17.6C6.6 17.4 6.4 17.1 6.3 16.8C6.2 16.5 6.10001 16.3 6.10001 16.1C6.10001 15.9 6.2 15.7 6.3 15.6C6.4 15.5 6.6 15.4 6.8 15.4C6.9 15.4 7.00001 15.4 7.10001 15.5C7.20001 15.6 7.3 15.6 7.3 15.7C7.5 16.2 7.7 16.6 8 16.9C8.3 17.2 8.6 17.3 9 17.3C9.2 17.3 9.5 17.2 9.7 17.1C9.9 17 10.1 16.8 10.3 16.6C10.5 16.4 10.5 16.1 10.5 15.8C10.5 15.3 10.4 15 10.1 14.7C9.80001 14.4 9.50001 14.3 9.10001 14.3C9.00001 14.3 8.9 14.3 8.7 14.3C8.5 14.3 8.39999 14.3 8.39999 14.3C8.19999 14.3 7.99999 14.2 7.89999 14.1C7.79999 14 7.7 13.8 7.7 13.7C7.7 13.5 7.79999 13.4 7.89999 13.2C7.99999 13 8.2 13 8.5 13H8.8V13.1ZM15.3 17.5V12.2C14.3 13 13.6 13.3 13.3 13.3C13.1 13.3 13 13.2 12.9 13.1C12.8 13 12.7 12.8 12.7 12.6C12.7 12.4 12.8 12.3 12.9 12.2C13 12.1 13.2 12 13.6 11.8C14.1 11.6 14.5 11.3 14.7 11.1C14.9 10.9 15.2 10.6 15.5 10.3C15.8 10 15.9 9.80003 15.9 9.70003C15.9 9.60003 16.1 9.60004 16.3 9.60004C16.5 9.60004 16.7 9.70003 16.8 9.80003C16.9 9.90003 17 10.2 17 10.5V17.2C17 18 16.7 18.4 16.2 18.4C16 18.4 15.8 18.3 15.6 18.2C15.4 18.1 15.3 17.8 15.3 17.5Z" fill="currentColor"></path>
													</svg>
												</span>
												<!--end::Svg Icon-->
												<!--end::Icon-->
												<!--begin::Datepicker-->
												<input class="form-control form-control-solid ps-12 flatpickr-input" placeholder="Select a date" name="dateTo" id="kt_datepicker_2" type="text" readonly="readonly" onchange="handleChange()">
												<!--end::Datepicker-->
											</div>
										</div>
									</div>
									<div class="row g-9 mb-8">
										<!--begin::Col-->

									</div>
									<div class="col-md-4 fv-row">
										<s:if test="%{#session.USER.UserType.name()=='ADMIN' || #session.USER.UserType.name()=='SUBADMIN'  || #session.USER.UserType.name()=='SUBADMIN' || #session.USER_TYPE.name()=='SUPERADMIN'}">
										<label class="d-flex align-items-center fs-6 fw-bold mb-2">
											<span class="">Merchant</span>
										</label>
									</s:if>
										<!-- <select name="merchant" data-control="select2" data-placeholder="Ravi Kiran" class="form-select form-select-solid select2-hidden-accessible" data-select2-id="select2-data-1-vk4n" tabindex="-1" aria-hidden="true" data-kt-initialized="1">
											<option value="Ravi Kiran" data-select2-id="select2-data-3-krpc">Ravi Kiran</option>
										</select><span class="select2 select2-container select2-container--bootstrap5" dir="ltr" data-select2-id="select2-data-2-skoj" style="width: 100%;"><span class="selection"><span class="select2-selection select2-selection--single form-select form-select-solid" role="combobox" aria-haspopup="true" aria-expanded="false" tabindex="0" aria-disabled="false" aria-labelledby="select2-merchant-gb-container" aria-controls="select2-merchant-gb-container"><span class="select2-selection__rendered" id="select2-merchant-gb-container" role="textbox" aria-readonly="true" title="Ravi Kiran">Ravi Kiran</span><span class="select2-selection__arrow" role="presentation"><b role="presentation"></b></span></span></span><span class="dropdown-wrapper" aria-hidden="true"></span></span> -->

										<s:if test="%{#session.USER.UserType.name()=='ADMIN' || #session.USER.UserType.name()=='SUBADMIN'  || #session.USER.UserType.name()=='SUBADMIN' || #session.USER_TYPE.name()=='SUPERADMIN'}">
											<s:select name="merchantPayId" class="form-select form-select-solid adminMerchants" id="merchantPayId"
												headerKey="ALL" headerValue="ALL" list="merchantList"
												listKey="payId" listValue="businessName" autocomplete="off" />
										</s:if>
										<s:else>
											<!-- <s:select name="merchantPayId" class="form-select form-select-solid" id="merchantPayId" list="merchantList"
												listKey="payId" listValue="businessName" autocomplete="off" /> -->
												<s:select name="merchantPayId" class="form-select form-select-solid adminMerchants d-none" id="merchantPayId"
												list="merchantList"
												listKey="payId" listValue="businessName" autocomplete="off" />
										</s:else>

									</div>
									<br>
									<div class="row g-9 mb-8">
										<div class="d-flex flex-column fv-row">
											<!--begin::Checkbox-->
											<div class="form-check form-check-custom form-check-solid mb-5">
												<!--begin::Input-->
												<input class="form-check-input me-3" name="reportType" type="radio" value="saleCaptured" id="sales_captured_report" checked>
												<!--end::Input-->

												<!--begin::Label-->
												<label class="form-check-label" for="sales_captured_report">
													<div class="fw-semibold text-gray-800">Sales
														Captured Report</div>
												</label>
												<!--end::Label-->
											</div>
											<!--end::Checkbox-->

											<!--begin::Checkbox-->
											<div class="form-check form-check-custom form-check-solid mb-5">
												<!--begin::Input-->
												<input class="form-check-input me-3" name="reportType" type="radio" value="refundCaptured" id="refund_captured_report">
												<!--end::Input-->

												<!--begin::Label-->
												<label class="form-check-label" for="refund_captured_report">
													<div class="fw-semibold text-gray-800">Refund
														Captured Report</div>
												</label>
												<!--end::Label-->
											</div>
											<!--end::Checkbox-->

											<!--begin::Checkbox-->
											<div class="form-check form-check-custom form-check-solid">
												<!--begin::Input-->
												<input class="form-check-input me-3" name="reportType" type="radio" value="settled" id="settled_report">
												<!--end::Input-->

												<!--begin::Label-->
												<label class="form-check-label" for="settled_report">
													<div class="fw-semibold text-gray-800">Settled
														Report</div>
												</label>
												<!--end::Label-->
											</div>
											<!--end::Checkbox-->
										</div>
									</div>

									<div class="row g-9 mb-8">
										<div class="col-md-4 fv-row d-flex justify-content-start align-items-end">
												<button type="submit" id="save_promotional" class="btn w-sm-100 w-md-100 btn-primary">
													<span class="indicator-label">Download</span>
													<span class="indicator-progress">Please wait...
														<span class="spinner-border spinner-border-sm align-middle ms-2"></span></span>
												</button>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</form>
			</div>
			<!--end::Container-->
		</div>
		<!--end::Post-->
	</div>

	<script>
$(document).ready(function(){
	$("#reportTypesaleCaptured").attr('checked', 'checked');

});

</script>
<script>
	function radiochange(e){
		var radioevent=e.value;
		if(radioevent=='settled'){
		$("#acquirerBox").css('display','block');
		}
		else{
		$("#acquirerBox").css('display','none');
		}

};

</script>

<%-- <script src="../assets/plugins/global/plugins.bundle.js"></script>
<script src="../assets/js/scripts.bundle.js"></script> --%>
<!--end::Global Javascript Bundle-->
<!--begin::Vendors Javascript(used by this page)-->
<%-- <script src="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.js"></script> --%>
<script src="https://cdn.amcharts.com/lib/5/index.js"></script>
<script src="https://cdn.amcharts.com/lib/5/xy.js"></script>
<script src="https://cdn.amcharts.com/lib/5/percent.js"></script>
<script src="https://cdn.amcharts.com/lib/5/radar.js"></script>
<script src="https://cdn.amcharts.com/lib/5/themes/Animated.js"></script>
<script src="https://cdn.amcharts.com/lib/5/map.js"></script>
<script src="https://cdn.amcharts.com/lib/5/geodata/worldLow.js"></script>
<script src="https://cdn.amcharts.com/lib/5/geodata/continentsLow.js"></script>
<script src="https://cdn.amcharts.com/lib/5/geodata/usaLow.js"></script>
<script src="https://cdn.amcharts.com/lib/5/geodata/worldTimeZonesLow.js"></script>
<script src="https://cdn.amcharts.com/lib/5/geodata/worldTimeZoneAreasLow.js"></script>

<!--end::Vendors Javascript-->
<!--begin::Custom Javascript(used by this page)-->
<%-- <script src="../assets/js/widgets.bundle.js"></script> --%>
<%-- <script src="../assets/js/custom/widgets.js"></script>
<script src="../assets/js/custom/apps/chat/chat.js"></script>
<script src="../assets/js/custom/utilities/modals/upgrade-plan.js"></script>
<script src="../assets/js/custom/utilities/modals/create-app.js"></script>
<script src="../assets/js/custom/utilities/modals/users-search.js"></script> --%>
<!--end::Custom Javascript-->
<!--end::Javascript-->
<script>
	$("#kt_datepicker_1").flatpickr({
		showOtherMonths: true,
					dateFormat: 'Y-m-d',
					selectOtherMonths: false,
					defaultDate: "today",
					maxDate: new Date()
		});
	$("#kt_datepicker_2").flatpickr({
		showOtherMonths: true,
					dateFormat: 'Y-m-d',
					selectOtherMonths: false,
					defaultDate: "today",
					maxDate: new Date()
		});

  function convert(str) {
  var date = new Date(str),
    mnth = ("0" + (date.getMonth() + 1)).slice(-2),
    day = ("0" + date.getDate()).slice(-2);
  //return [date.getFullYear(), mnth, day].join("-");
  return [day,mnth,date.getFullYear() ].join("-");
}
var urls = new URL(window.location.href);
				var domain = urls.origin;

				var channel = $("#Channel").val();



				$.ajax({
					url: domain + "/crmws/PaymentMethod/PaymentType/"+channel,
					//url: "http://localhost:8081/PaymentMethod/PaymentType/" + channel,
					type: 'GET',

					contentType: "application/json",
					success: function (data) {
						const selectElement = document.getElementById('paymentType');
						const option = document.createElement('option');
						option.value = "ALL";
						option.text = "ALL";
						selectElement.appendChild(option);

						if (data.respmessage == "Successfully") {
							var data = data.multipleResponse;
							Object.keys(data).forEach(key => {
								const value = data[key];

								console.log(key + "\t" + value);

								const option = document.createElement('option');
								option.value = key;
								option.text = value;
								selectElement.appendChild(option);

							}
							);
						}
					},
					error: function (data, textStatus, jqXHR) {


					}
				});
				
				function getPaymentType() {
					debugger
					var channel = $("#Channel").val();



					const selectElement = document.getElementById('paymentType');

					// Remove all child elements (options) from the select tag
					while (selectElement.firstChild) {
						selectElement.removeChild(selectElement.firstChild);
					}

					$.ajax({
						url: domain + "/crmws/PaymentMethod/PaymentType/"+channel,
						//url: "http://localhost:8081/PaymentMethod/PaymentType/" + channel,
						type: 'GET',

						contentType: "application/json",
						success: function (data) {
							const selectElement = document.getElementById('paymentType');
							if (data.respmessage == "Successfully") {
								var data = data.multipleResponse;
								const option = document.createElement('option');
								option.value = "ALL";
									option.text = "ALL";
									selectElement.appendChild(option);
								Object.keys(data).forEach(key => {
									const value = data[key];

									console.log(key + "\t" + value);

									const option = document.createElement('option');
									option.value = key;
									option.text = value;
									selectElement.appendChild(option);

								}
								);
							}
						},
						error: function (data, textStatus, jqXHR) {


						}
					});

					
				}
</script>


</body>
</html>