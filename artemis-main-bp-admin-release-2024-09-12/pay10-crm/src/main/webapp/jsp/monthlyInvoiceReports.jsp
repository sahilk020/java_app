<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Monthly Invoice Report</title>

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

<script type="text/javascript">
$(document).ready(function(){
 
 			$('#downloadReport').click(function() {	

		var month = document.getElementById("month").value;
		var year = document.getElementById("year").value;
		
		if(month == ''){
			alert("Please select the Month");
			return false;
		}else if(year == ''){
			alert("Please select the Year");
			return false;
		}else{
				$('#loader-wrapper').show();
				$('#errorMessage').hide(); 					
				document.getElementById("monthlyInvoiceReport").submit();			
		}
		
	      });

		<s:if test="%{#session.USER.UserType.name()=='MERCHANT'}">
			$("#merchantDropdown").hide();
		</s:if>
  // Initialize select2
  $("#merchant").select2();
  $("#currency").select2();
  $("#month").select2();
  $("#year").select2();
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
  border-radius: 5px;
  border: 1px #dadada solid;
  height:300px;
  overflow-y: scroll;
  position:Absolute;
  background:#fff;
  z-index:1;
  margin-left:2px;
  margin-right:10px;
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
.overSelect {
  position: absolute;
  left: 0;
  right: 0;
  top: 0;
  bottom: 0;
}
.download-btn {
	background-color:#2b6dd1;
	display: block;
    width: 100%;
    height: 30px;
    padding: 3px 4px;
    font-size: 14px;
    line-height: 1.42857143;
    color: #fff;
    border: 1px solid #ccc;
    border-radius: 4px;
	margin-top:30px;
}
.form-control{
	margin-left: 0!important;
	width: 100% !important;
}
.padding10{
	padding: 10px;
}
.disableBtn{
	background-color:#cccccc;
	color: black;
}
.error-text{color:#a94442;font-weight:bold;background-color:#f2dede;list-style-type:none;text-align:center;list-style-type: none;
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
									<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">Monthly Invoice Report</h1>
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
										<li class="breadcrumb-item text-dark">Monthly Invoice Report</li>
										<!--end::Item-->
									</ul>
									<!--end::Breadcrumb-->
								</div>
								<!--end::Page title-->
								
							</div>
							<!--end::Container-->
						</div>

<form id="monthlyInvoiceReport" name="monthlyInvoiceReport" action="monthlyInvoiceReport">
<!-- <table width="100%" align="left" cellpadding="0" cellspacing="0" class="txnf">
		<tr> -->
			<td align="left">
				<!-- <h2>MIS Report</h2> -->
				<div class="post d-flex flex-column-fluid" id="kt_post">
			<div id="kt_content_container" class="container-xxl">				
				<div class="row my-5">
					<div class="col">
						<div class="card">							  
							  <div class="card-body ">
				<div class="col-md-12">
					<div class="card ">
					 
					  <div class="card-body ">
				<div class="container">
					<div class="row padding10">
						<div class="col-sm-6 col-lg-3" id="merchantDropdown">
							<label class="d-flex align-items-center fs-6 fw-semibold mb-2" for="merchant" style="margin-left: 2px;">Merchant:</label> <br />
							<s:if
								test="%{#session.USER.UserType.name()=='ADMIN' || #session.USER.UserType.name()=='SUBADMIN'  || #session.USER.UserType.name()=='SUBADMIN' || #session.USER_TYPE.name()=='SUPERADMIN'}">
								<s:select name="payId" class="form-select form-select-solid" id="merchant"
									 list="merchantList"
									listKey="payId" listValue="businessName" autocomplete="off" />
							</s:if>
							<s:else>
								<s:select name="payId" class="form-select form-select-solid" id="merchant"
									 list="merchantList"
									listKey="payId" listValue="businessName" autocomplete="off" />
							</s:else>
						</div>
					
					<div class="col-sm-6 col-lg-3">
						<label class="d-flex align-items-center fs-6 fw-semibold mb-2" for="email" >Currency:</label> <br />
							<s:if
								test="%{#session.USER.UserType.name()=='ADMIN' || #session.USER.UserType.name()=='SUBADMIN'  || #session.USER.UserType.name()=='SUBADMIN' || #session.USER_TYPE.name()=='SUPERADMIN'}">
						<s:select name="currency" id="currency" headerValue="ALL"
								headerKey="ALL" list="currencyMap" class="form-select form-select-solid" />
							</s:if>
							<s:else>
						<s:select name="currency" id="currency" 
								 list="currencyMap" class="input-control" />
							</s:else>
						

					</div>
					
					<div class="col-sm-6 col-lg-3">
						<label class="d-flex align-items-center fs-6 fw-semibold mb-2" for="dateTo" >Month</label> <br />
						<div id="monthSelect">
						<select class="form-select form-select-solid" onchange="handleChange();" id="month" name="month">
							
						</select>
						</div>
					</div>
					
					<div class="col-sm-6 col-lg-3">
						<label class="d-flex align-items-center fs-6 fw-semibold mb-2" for="dateTo" >Year</label> <br />
						<select class="form-select form-select-solid" onchange="handleChange();" id="year" name="year">

						</select>
					</div>
					
					</div>
					<div class="row" align="right">
						<div class="col-sm-6 col-lg-3"></div>
						<div class="col-sm-6 col-lg-3"></div>
						<div class="col-sm-6 col-lg-3"></div>
						<div class="col-sm-6 col-lg-3">
						<button  class="btn btn-primary  mt-4 submit_btn" id="downloadReport">Download</button>
					</div></div>
				
					<div class="error-text" id="errorMessage"><s:actionmessage/></div>
				</div>
				</div>
				</div>
				</div>
				</div></div></div></div></div></div>
			</td>
		</tr>
		<tr>
			<td align="left" style="border-bottom: 1px solid #eaeaea;">&nbsp;</td>
		</tr>
		<tr>
		<!-- </tr>
	</table> -->
</form>
</div>

<script type="text/javascript">

var monthInitDomVal="";

	   function handleChange() {
				
		   $("#downloadReport").attr("disabled", false);
		   $("#downloadReport").removeClass("disableBtn");
		   
			var monthVal = document.getElementById('month').value;
			var yearVal = document.getElementById('year').value;
			
			if(yearVal != null && yearVal != '' && yearVal != ""){
				if(monthVal != null && monthVal != '' && monthVal != ""){
							var startMon = 0;
							var endMon = new Date().getMonth();
							var currentYear = new Date().getFullYear();
							if(yearVal != currentYear){
								endMon = 12;
							}
							monthValueGenarator(startMon,endMon,(monthVal<(endMon+1) ? monthVal : -1),yearVal);							

				}else{
							var startMon = 0;
							var endMon = new Date().getMonth();
							if(yearVal != (new Date().getFullYear())){
								endMon = 12;
							}
							monthValueGenarator(startMon,endMon,-1,yearVal);
				}
			}		
		
     }

function monthValueGenarator(startMon , endMon, monSelectVal,yearVal){
	
		const monthNames = ["January", "February", "March", "April", "May", "June",
						"July", "August", "September", "October", "November", "December"
					    ];

		var optionsMon = "";
		optionsMon += "<option value=\"\" selected disabled>Select Month</option>";
		for(var mon = startMon ; mon <endMon; mon++){
			if((mon+1) == monSelectVal){
				optionsMon += "<option value="+(mon+1)+" selected>"+ monthNames[mon] +"</option>";
			}else{
			optionsMon += "<option value="+(mon+1)+">"+ monthNames[mon] +"</option>";				
			}

		}

		if(yearVal == -1){
			document.getElementById("month").innerHTML = optionsMon;				
		}else{
			document.getElementById("monthSelect").innerHTML = monthInitDomVal + optionsMon + "</select>";	
		}

}

	$(document).ready(function() {	
	     monthInitDomVal = "<select class=\"input-control\" onchange=\"handleChange();\" id=\"month\" name=\"month\">"
		var start = 2019;
		var end = new Date().getFullYear();
		var options = "";
		options += "<option value=\"\" selected disabled>Select Year</option>";
		for(var year = start ; year <=end; year++){
			options += "<option value="+year+">"+ year +"</option>";
		}
		document.getElementById("year").innerHTML = options;
		var startMon = 0;
		var endMon = 12;
		monthValueGenarator(startMon,endMon,-1,-1);					  
	});	
</script>



</body>
</html>