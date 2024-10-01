<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="/struts-tags" prefix="s"%>
<html dir="ltr" lang="en-US">
<head>
<title>Search Payment</title>
<link rel="stylesheet" type="text/css"
  href="../css/material-icons.css" />
<link rel="stylesheet" href="../css/material-font-awesome.min.css">
<!-- CSS Files -->
<!-- <link href="../css/material-dashboard.css" rel="stylesheet" /> -->
<link href="../css/default.css" rel="stylesheet" type="text/css" />
<link rel="stylesheet" type="text/css" media="all" href="../css/daterangepicker-bs3.css" />
<link href="../css/Jquerydatatableview.css" rel="stylesheet" />
<link href="../css/jquery-ui.css" rel="stylesheet" />
<link href="../css/Jquerydatatable.css" rel="stylesheet" />
<script src="../js/jquery.min.js" type="text/javascript"></script>
<script src="../js/core/popper.min.js"></script>
<script src="../js/core/bootstrap-material-design.min.js"></script>
<script src="../js/plugins/perfect-scrollbar.jquery.min.js"></script>
<!-- Control Center for Material Dashboard: parallax effects, scripts for the example pages etc -->
<script src="../js/material-dashboard.js?v=2.1.0" type="text/javascript"></script>
<script src="../js/moment.js" type="text/javascript"></script>
<script src="../js/daterangepicker.js" type="text/javascript"></script>
<script src="../js/jquery.dataTables.js"></script>
<script src="../js/jquery-ui.js"></script>
<script src="../js/commanValidate.js"></script>
<script src="../js/jquery.popupoverlay.js"></script>
<script src="../js/dataTables.buttons.js" type="text/javascript"></script>
<script src="../js/pdfmake.js" type="text/javascript"></script>
<script src="../js/jszip.min.js" type="text/javascript"></script>
<script src="../js/vfs_fonts.js" type="text/javascript"></script>
<script src="../js/buttons.colVis.min.js" type="text/javascript"></script>

<link href="../css/select2.min.css" rel="stylesheet" />
<script src="../js/jquery.select2.js" type="text/javascript"></script>
<!--  loader scripts -->
<script src="../js/loader/modernizr-2.6.2.min.js"></script>
<script src="../js/loader/main.js"></script>
<link rel="stylesheet" href="../css/loader/normalize.css" />
<link rel="stylesheet" href="../css/loader/main.css" />
<link rel="stylesheet" href="../css/loader/customLoader.css" />

<script src="../js/transactionresultSuperAdmin.js"></script>

<link rel="stylesheet" href="../css/transactionResultSuperAdmin.css">

<script>
function downloadReport(){

	var	merchantPayId = document.getElementById("merchant").value;
	var tenantId = document.getElementById("companyName").value;
	var	transactionType = document.getElementById("transactionType").value;
	var	paymentType = document.getElementById("selectBox3").title;
	var status = document.getElementById("selectBox4").title;
	var currency = document.getElementById("currency").value;
	var dateFrom = document.getElementById("dateFrom").value;
	var dateTo = document.getElementById("dateTo").value;
    var acquirer = document.getElementById("selectBox1").title;
	
	var transactionId = document.getElementById("pgRefNum").value;
	var	orderId = document.getElementById("orderId").value;
	var	customerEmail = document.getElementById("customerEmail").value;
	var	customerPhone = document.getElementById("custPhone").value;		
	var	mopType = document.getElementById("selectBox2").title;
	
	
		if(merchantPayId==''){
			merchantPayId='ALL'
		}
		if(tenantId==''){
			tenantId='ALL'
		}
		if(transactionType==''){
			transactionType='ALL'
		}
		if(paymentType==''){
			paymentType='ALL'
		}
		if(status==''){
			status='ALL'
		}
		if(currency==''){
			currency='ALL'
		}
		if(mopType==''){
			mopType='ALL'
		}
		if(acquirer==''){
			acquirer='ALL'
		}
		
	dateFrom += " 00:00";	
	dateTo += " 23:59";
	document.getElementById("merchantPayIdH").value = merchantPayId;
	document.getElementById("tenantIdH").value = tenantId;
	document.getElementById("transactionTypeH").value = transactionType;
	document.getElementById("paymentTypeH").value = paymentType;
	document.getElementById("statusH").value = status;
	document.getElementById("currencyH").value = currency;
	document.getElementById("dateFromH").value = dateFrom;
	document.getElementById("dateToH").value = dateTo;
	document.getElementById("acquirerH").value = acquirer;
	
	document.getElementById("transactionIdH").value = transactionId;
	document.getElementById("orderIdH").value = orderId;
	document.getElementById("customerEmailH").value = customerEmail;
	document.getElementById("customerPhoneH").value = customerPhone;
	document.getElementById("mopTypeH").value = mopType;

	document.getElementById("downloadPaymentReport").submit();
}

</script>

<style type="text/css">
.selectBox {
	position: relative;
}
.selectBox select {
	width: 95%;
}
#checkboxes1 {
	display: none;
	border: 1px #dadada solid;
	height:300px;
	overflow-y: scroll;
	position:Absolute;
	background:#fff;
	z-index:1;
	margin-left:5px;
}

#checkboxes1 label {
  width: 74%;
}
#checkboxes1 input {
  width:18%;

}

#checkboxes2 {
	display: none;
	border: 1px #dadada solid;
	height:300px;
	overflow-y: scroll;
	position:Absolute;
	background:#fff;
	z-index:1;
	margin-left:5px;
}

#checkboxes2 label {
  width: 74%;
}
#checkboxes2 input {
  width:18%;

}

#checkboxes3 {
	display: none;
	border: 1px #dadada solid;
	height:300px;
	overflow-y: scroll;
	position:Absolute;
	background:#fff;
	z-index:1;
	margin-left:5px;
}

#checkboxes3 label {
  width: 74%;
}
#checkboxes3 input {
  width:18%;

}

#checkboxes4 {
	display: none;
	border: 1px #dadada solid;
	height:300px;
	overflow-y: scroll;
	position:Absolute;
	background:#fff;
	z-index:1;
	margin-left:5px;
}

#checkboxes4 label {
  width: 74%;
}
#checkboxes4 input {
  width:18%;

}

.overSelect {
  position: absolute;
  left: 0;
  right: 0;
  top: 0;
  bottom: 0;
}
#cardTitle{
	display: inline-flex;
	font-size: 20px;
	font-weight: 500;

}
.nav-pills .nav-item .nav-link {
	font-size: 16px !important;
	font-weight:100 !important;

}
button.dt-button, div.dt-button, a.dt-button {
 
    font-size: 14px;
    
}
#totalTxnsAmount th{
	color:#496cb6;
}
#totalSettledTxnsAmount th{
	color:#496cb6;
}




</style>

</head>
<body id="mainBody">
    <div id="loader-wrapper" style="width: 100%; height: 100%; display:none;">
	  <div id="loader"></div>
    </div>
	
	<div class="modal" id="popup" style="overflow-y: auto;">
		<!-- <div > -->
				<!--<div class="wrapper " style="max-height: 590px;"> -->
    
    
					<!-- Navbar -->
				   
					<!-- End Navbar -->
					<div class="content innerpopupDv" >
    
    
					<!-- Navbar -->
				   
					<!-- End Navbar -->
					<!-- <div class="content"> -->
					  <div class="container-fluid">
					   
						<div class="row">
						  
						  
							<div class="card ">
							  <div class="card-header ">
								<h4 class="card-title" id="cardTitle" >Customer Transaction Information
								  <!-- <small class="description">Vertical Tabs</small> -->
								</h4>
								<button style="position: absolute;
								left: 93%;border: none;
								background: none;"  id="closeBtn1"><img style="width:20px" src="../image/red_cross.jpg"></button>
							  </div>
							  <div class="card-body ">
								<div class="row">
									<div class="col-md-12 ml-auto mr-auto">
								  <div class="col-lg-4 col-md-6">
									<!--
											  color-classes: "nav-pills-primary", "nav-pills-info", "nav-pills-success", "nav-pills-warning","nav-pills-danger"
										  -->
									<ul class="nav nav-pills nav-pills-rose nav-pills-icons flex-column"  role="tablist">
									  <li class="nav-item" id="listitem">
										<a class="nav-link active" data-toggle="tab" href="#link110" role="tablist">
										  <i class="material-icons">money</i> Transaction Information
										</a>
									  </li>
									  <li class="nav-item" id="listitem">
										<a class="nav-link" data-toggle="tab" href="#link111" role="tablist">
										  <i class="material-icons">payment</i> Payment Information
										</a>
									  </li>
									  <li class="nav-item" id="listitem">
										<a class="nav-link" data-toggle="tab" href="#link112" role="tablist">
										  <i class="material-icons">person</i> Customer Information
										</a>
									  </li>
									</ul>
								  </div>
								  <div class="col-md-8">
									<div class="tab-content">
									  <div class="tab-pane active" id="link110">
										
										<h4 style="margin-left:10px;color: #4c55a0 !important;font-weight:600">Transaction Information</h4>
											   <div class="card-body">
											   
												   <table width="100%" class="invoicetable">				
													 <tr>
													   <th  align="left" valign="middle">Amount</th>
													   <th  align="left" valign="middle">Status</th>
													  
													  </tr>
												   
													  <tr id="sec11">
													   <td align="left" valign="middle"></td>
													   <td align="left" valign="middle"></td>
													  
													 </tr>
													 <tr>
													   <th  align="left" valign="middle">Order Id</th>
													   <th  align="left" valign="middle">PG Ref No.</th>
													  
													  </tr>
													   <tr id="sec12">
													   <td align="left" valign="middle"></td>
													   <td align="left" valign="middle"></td>
													   
													 </tr>
													 <tr>
														<th  align="left" valign="middle">ARN No.</th>
														<th  align="left" valign="middle">RRN No.</th>
													   
													   </tr>
														<tr id="sec13">
														<td align="left" valign="middle"></td>
														<td align="left" valign="middle"></td>
														
													  </tr>
													 <tr id="sec14h">
														<th  align="left" valign="middle">ACQ ID.</th>
														<th  align="left" valign="middle">Total Refunded Amount</th>
				
													  </tr>
													  <tr id="sec14">
														<td align="left" valign="middle"></td>
														<td align="left" valign="middle"></td>				
													  </tr>
													  <tr id="sec16h">
														<th  align="left" valign="middle">IP Address.</th>
														<th  align="left" valign="middle">SALE Amount.</th>
													  </tr>
													  <tr id="sec16">
														<td align="left" valign="middle"></td>
														<td align="left" valign="middle"></td>														
													  </tr>
													
											   </table>
											  
												 <!-- <label for="cardMask" class="bmd-label-floating">Card Mask</label>
												 <input type="email" class="form-control" id="cardMask"> -->
											   
											   </div>
											   <div id="accordion" role="tablist">
												<div class="card-collapse">
												  <div class="card-header" role="tab" id="headingOne">
													<h5 class="mb-0">
													  <a id="infotabs" style="display: inline-flex;width: 100%;" data-toggle="collapse" href="#collapseThree" aria-expanded="true" aria-controls="collapseOne" class="collapsed">
														<h4 style="margin-left:10px;color: #4c55a0 !important;font-weight:600;width: 100%;">Trail</h4>
														<i class="material-icons">keyboard_arrow_down</i>
													  </a>
													</h5>
												  </div>
												  <div id="collapseThree" class="collapse show" role="tabpanel" aria-labelledby="headingOne" data-parent="#accordion" >
													<div class="card-body">
												
												<table width="100%" class="table table-responsive invoicetable" id="transactionDetails">				
												 <tr>
												 
												   <th  align="left" valign="middle">Order ID</th>
												   <th  align="left" valign="middle">Transaction Type</th>
												   <th  align="left" valign="middle">Date</th>
												   <th  align="left" valign="middle">Status</th>
	
												   
												  </tr>
											   
												  <tr id="sec15">
												   <td align="left" valign="middle"></td>
												   <td align="left" valign="middle"></td>
												   <td align="left" valign="middle"></td>
  												   <td align="left" valign="middle"></td>
												 </tr>
												 <tr id="sec15">
												   <td align="left" valign="middle"></td>
												   <td align="left" valign="middle"></td>
												   <td align="left" valign="middle"></td>
   												   <td align="left" valign="middle"></td>
												 </tr>
												 
												  
										   </table>
										   
													</div>
												  </div>
												</div>
												</div>
											 
											 
											
										   
									  </div>
									  <div class="tab-pane" id="link111">
										
										  
										 
										 <h4 style="margin-left:10px;color: #4c55a0 !important;font-weight:600">Payment Information</h4>
											<div class="card-body">
											
												<table width="100%" class="invoicetable">
												<tr>
													<th  height="25" colspan="4" align="left" valign="middle">Card Holder Name</th>
												   
												   </tr>
												   <tr id="sec3chn">
													<td height="25" colspan="4" align="left" valign="middle"></td>
												   
												  </tr>
												  
												  <tr>
													<th  align="left" valign="middle">Card Mask</th>
													<th  align="left" valign="middle">Issuer</th>
												   
												   </tr>
												
												   <tr id="sec3">
													<td align="left" valign="middle"></td>
													<td align="left" valign="middle"></td>
												   
												  </tr>
												  <tr>
													<th  align="left" valign="middle">Acquirer</th>
													<th  align="left" valign="middle">MopType</th>
												   
												   </tr>
													<tr id="sec4">
													<td align="left" valign="middle"></td>
													<td align="left" valign="middle"></td>
													
												  </tr>
												  <tr>
													<th align="left" valign="middle">PG TDR</th>
													<th align="left" valign="middle">PG GST</th>
													
													</tr>
												  <tr id="sec5">
													<td align="left" valign="middle"></td>
													<td align="left" valign="middle"></td>
													
													</tr>
													<tr>
													  <th align="left" valign="middle">Acquirer TDR</th>
													<th align="left" valign="middle">Acquirer GST</th>
													 
													
													</tr>
												  <tr id="sec6">
													<td align="left" valign="middle"></td>
													<td align="left" valign="middle"></td>
													 
													
													</tr>
													<tr>
													  <th height="25" colspan="4" align="left" valign="middle"><span>Acquirer Response</span></th>
			  
													</tr>
													<tr id="address5">
													  <td height="25" colspan="4" align="left" valign="middle"></td>
			  
													</tr>
													<!-- <tr>
													  <th height="25" colspan="4" align="left" valign="middle"><span>Address</span></th>
			  
													</tr>
													<tr id="address6">
													  <td height="25" colspan="4" align="left" valign="middle"></td>
			  
													</tr> -->
											</table>
					
											  <!-- <label for="cardMask" class="bmd-label-floating">Card Mask</label>
											  <input type="email" class="form-control" id="cardMask"> -->
											
											</div>
										 
										  
										  
										 
										</div>
									  <div class="tab-pane" id="link112">
										
										  <div id="accordion" role="tablist">
											<div class="card-collapse">
											  <div class="card-header" role="tab" id="headingOne">
												<h5 class="mb-0">
												  <a id="infotabs" style="display: inline-flex;
												  width: 100%;" data-toggle="collapse" href="#collapseOne" aria-expanded="true" aria-controls="collapseOne" class="collapsed">
												  <h4 style="margin-left:10px;color: #4c55a0 !important;font-weight:600;width: 100%;">Billing Address</h4>
													<i class="material-icons">keyboard_arrow_down</i>
												  </a>
												</h5>
											  </div>
											  <div id="collapseOne" class="collapse show" role="tabpanel" aria-labelledby="headingOne" data-parent="#accordion" style="">
												<div class="card-body">
												
													  <table width="100%"  class="invoicetable">				
														<tr>
														  <th  align="left" valign="middle">Name</th>
														  <th  align="left" valign="middle">Phone No</th>
														 
														 </tr>
													  
														 <tr id="sec1">
														  <td align="left" valign="middle"></td>
														  <td align="left" valign="middle"></td>
														 
														</tr>
														<tr>
														  <th align="left" valign="middle">City</th>
														  <th align="left" valign="middle">State</th>
														 
														 </tr>
														  <tr id="sec2">
														  <td align="left" valign="middle"></td>
														  <td align="left" valign="middle"></td>
														 
														</tr>
														<tr>
													   
														  <th align="left" valign="middle">Country</th>
														  <th align="left" valign="middle">Zip</th>
														 </tr>
														  <tr id="sec7">
														  
														  <td align="left" valign="middle"></td>
														  <td align="left" valign="middle"></td>
														</tr>
														<tr>
														   <th height="25" colspan="4" align="left" valign="middle"><span>Address1</span></th>
														  </tr>
														<tr id="address1">
														   <td height="25" colspan="4" align="left" valign="middle"></td>
														  </tr>
														  <tr>
														   <th height="25" colspan="4" align="left" valign="middle"><span>Address2</span></th>
														  </tr>
														<tr id="address2">
														   <td height="25" colspan="4" align="left" valign="middle"></td>
														  </tr>
													   
														
												  </table>
													
												</div>
											  </div>
											</div>
											<div class="card-collapse">
											  <div class="card-header" role="tab" id="headingTwo">
												<h5 class="mb-0">
												  <a  id="infotabs" style="display: inline-flex;
												  width: 100%;" class="collapsed" data-toggle="collapse" href="#collapseTwo" aria-expanded="false" aria-controls="collapseTwo">
													<h4 style="margin-left:10px;color: #4c55a0 !important;font-weight:600;width:100%">Shipping Address</h4>
													<i class="material-icons">keyboard_arrow_down</i>
												  </a>
												</h5>
											  </div>
											  <div id="collapseTwo" class="collapse" role="tabpanel" aria-labelledby="headingTwo" data-parent="#accordion">
												<div class="card-body">
												 
													  <table width="100%"  class="invoicetable">				
														<tr>
														  <th  align="left" valign="middle">Name</th>
														  <th  align="left" valign="middle">Phone No</th>
														 
														 </tr>
													  
														 <tr id="sec8">
														  <td align="left" valign="middle"></td>
														  <td align="left" valign="middle"></td>
														 
														</tr>
														<tr>
														  <th align="left" valign="middle">City</th>
														  <th align="left" valign="middle">State</th>
														 
														 </tr>
														  <tr id="sec9">
														  <td align="left" valign="middle"></td>
														  <td align="left" valign="middle"></td>
														 
														</tr>
														<tr>
													   
														  <th align="left" valign="middle">Country</th>
														  <th align="left" valign="middle">Zip</th>
														 </tr>
														  <tr id="sec10">
														  
														  <td align="left" valign="middle"></td>
														  <td align="left" valign="middle"></td>
														</tr>
														<tr>
														   <th height="25" colspan="4" align="left" valign="middle"><span>Address1</span></th>
														  </tr>
														<tr id="address3">
														   <td height="25" colspan="4" align="left" valign="middle"></td>
														  </tr>
														  <tr>
														   <th height="25" colspan="4" align="left" valign="middle"><span>Address2</span></th>
														  </tr>
														<tr id="address4">
														   <td height="25" colspan="4" align="left" valign="middle"></td>
														  </tr>
													   
														
												  </table>
													
												</div>
											  </div>
											</div>
										   
										  </div>
			  
			  
									   
										
									  </div>
									</div>
									<div class="card-footer " style="float: right;">
									  <button class="btn btn-danger" id="closeBtn">Close<div class="ripple-container"></div></button>
									  <!-- <button type="submit" class="btn btn-fill btn-rose" id="closeBtn">Close</button> -->
									</div>
									<!-- <div style="text-align: center;"><button class="btn btn-danger" id="closeBtn">Close</button></div>	 -->
								  </div>
								</div>
							  </div>
							</div>
						  </div>
						</div>
						
					  </div>
					<!-- </div> -->
				   
				  
				</div>
										  
		</div>
	<!-- </div> -->

	
   <div style="overflow:scroll !important;">
	<div class="col-md-12">
		<div class="card ">
		  <div class="card-header card-header-rose card-header-text">
			<div class="card-text">
			  <h4 class="card-title">Search Payments</h4>
			</div>
		  </div>
		  <div class="card-body ">
			<div class="container">
			  <div class="row">
				<div class="col-sm-6 col-lg-3">
					<label>Company Name</label><br>
					<div class="txtnew">
					
							<s:select name="companyName" class="input-control " id="companyName"
								headerKey="" headerValue="ALL" list="companyList"
								listKey="tenantId" listValue="companyName" autocomplete="off" />
						
						
					</div>
				</div>
				 
					  <div class="col-sm-6 col-lg-3" >
						<label>PG REF Number :</label><br>
						<div class="txtnew">
							<s:textfield id="pgRefNum" class="input-control"
				name="pgRefNum" type="text" value="" autocomplete="off" ondrop="return false;"
				maxlength="16" onblur="validPgRefNum();" onKeyDown="if(event.keyCode === 32)return false;" onpaste="removeSpaces(this);"></s:textfield>
		</div>
		<span id="validRefNo" style="color:red; display:none;">Please Enter 16 Digit PG Ref No.</span>
		<span id="validValue" style="color:red; display:none;">Please Enter Valid PG Ref No.</span>
		
					  </div>
					  <div class="col-sm-6 col-lg-3" >
						<label>Order ID:</label><br>
						
						<div class="txtnew">
							<s:textfield id="orderId" class="input-control" name="orderId"
								type="text" value="" autocomplete="off" 
								onKeyDown="validateOrderIdvalue(this);" onkeypress="return validateOrderId(event);"  ondrop="return false;" onpaste="validateOrderIdvalue(this);" maxlength="50"></s:textfield>
						</div>
						<span id="validOrderIdValue" style="color:red; display:none;">Please Enter Valid OrderId.</span>
					  </div>

					  <div class="col-sm-6 col-lg-3">
						<label>Customer Email :</label><br>
						<div class="txtnew">
							<s:textfield id="customerEmail" class="input-control"
								name="customerEmail" type="text" value="" autocomplete="off"
								onblur="validateCustomerEmail(this);" ondrop="return false;" onKeyDown="if(event.keyCode === 32)return false;" onpaste="removeSpaces(this);"></s:textfield>
						</div>
						<span id="validEamilValue" style="color:red; display:none;">Please Enter Valid Email.</span>
					</div>
					<div class="col-sm-6 col-lg-3">
						<label>Customer Phone :</label><br>
						<div class="txtnew">
							<s:textfield id="custPhone" class="input-control noSpace"
								name="custPhone" type="text" value="" autocomplete="off"
								  onblur="validateCustomerPhone(this);" onKeyDown="if(event.keyCode === 32)return false;" ondrop="return false;" onpaste="removeSpaces(this);"  maxlength="14"></s:textfield>
						</div>
						<span id="validPhoneValue" style="color:red; display:none;">Please Enter Valid Phone No.</span>
					</div>
					<div class="col-sm-6 col-lg-3">
						<label>Merchant</label><br>
						<div class="txtnew">
							<s:if
								test="%{#session.USER.UserType.name()=='ADMIN' || #session.USER.UserType.name()=='SUBADMIN' || #session.USER_TYPE.name()=='SUPERADMIN'}">
								<s:select name="merchant" class="input-control adminMerchants" id="merchant"
									headerKey="" headerValue="ALL" list="merchantList"
									listKey="emailId" listValue="businessName" autocomplete="off" />
							</s:if>
							<s:else>
								<s:select name="merchant" class="input-control" id="merchant" list="merchantList"
									listKey="emailId" listValue="businessName" autocomplete="off" />
							</s:else>
						</div>
					</div>
					<div class="col-sm-6 col-lg-3">
						<label>Payment Method</label><br>
						<div class="txtnew">
		
							<div class="selectBox " id="selectBox3" onclick="showCheckboxes(event,3)" title="ALL">
										<select class="input-control">
											<option>ALL</option>
										</select>
										<div class="overSelect"></div>
									</div>
									<div id="checkboxes3" class="CloseOnClick" onclick="getCheckBoxValue(3)">
									
								<s:checkboxlist headerKey="ALL" headerValue="ALL" class="myCheckBox3"
									list="@com.pay10.commons.util.PaymentTypeUI@values()"
									listValue="name" listKey="code" name="paymentMethod"
									id="paymentMethod" autocomplete="off" value="paymentMethod" />
							
									</div>
									
	
							</div>

					</div>
				 
				  
					<div class="col-sm-6 col-lg-3">
					  <label>Mop Type</label><br>
					  <div class="txtnew">
		
						<div class="selectBox " id="selectBox2" onclick="showCheckboxes(event,2)" title="ALL">
							<select class="input-control">
								<option>ALL</option>
							</select>
							<div class="overSelect"></div>
						</div>
						<div id="checkboxes2" class="CloseOnClick" onclick="getCheckBoxValue(2)">
						
						<s:checkboxlist name="mopType" id="mopType" headerValue="ALL"
				headerKey="ALL" list="@com.pay10.commons.util.MopTypeUI@values()"
				listValue="name" listKey="uiName" class="myCheckBox2" value="mopType"/>
				
						</div>

				</div>
					</div>
					<div class="col-sm-6 col-lg-3" >
					  <label>Transaction Type</label><br>
					  
					  <div class="txtnew">
						<s:select headerKey="" headerValue="ALL" class="input-control"
							list="txnTypelist"
							listValue="name" listKey="code" name="transactionType"
							id="transactionType" autocomplete="off" value="name" />
					</div>
					</div>
					<div class="col-sm-6 col-lg-3">
					  <label>Status</label><br>
					  <div class="txtnew">
					  
					  <div class="selectBox " id="selectBox4" onclick="showCheckboxes(event,4)" title="ALL">
										<select class="input-control">
											<option>ALL</option>
										</select>
										<div class="overSelect"></div>
					  </div>
					  <div id="checkboxes4" class="CloseOnClick" onclick="getCheckBoxValue(4)">
						<s:checkboxlist headerKey="ALL" headerValue="ALL" list="@com.pay10.commons.util.StatusTypeUI@values()" name="status" id="status" value="status" listKey="code" listValue="name" class="myCheckBox4" />
					  </div>					   
					</div>
				  </div>
				  <div class="col-sm-6 col-lg-3">
					  <label>Currency</label><br>
					  <div class="txtnew">
						<s:select name="currency" id="currency" headerValue="ALL"
							headerKey="" list="currencyMap" class="input-control" />
					</div>

				  </div>
				  <div class="col-sm-6 col-lg-3">
					  <label>Date From: </label><br>
					  <div class="txtnew">
						<s:textfield type="text"   id="dateFrom" name="dateFrom" class="input-control" autocomplete="off"  onkeyup="checkFromDate()" maxlength="10" onblur="compareDate()"/>
						<span id="dateError" style="color:red; display:none;">Please Enter Valid Date </span>
						<span id="showErr1" style="color:red; display:none;">Please Don't Enter Future Date</span>
					</div>

				  </div>
				  <div class="col-sm-6 col-lg-3">
					  <label>Date To :</label><br>
					  <div class="txtnew">
						<s:textfield type="text" id="dateTo" name="dateTo" class="input-control" autocomplete="off" 
						onkeyup="checkToDate()" maxlength="10" onblur="compareDate()"/>
						<span id="dateError1" style="color:red; display:none;">Please Enter Valid Date </span>
						<span id="showErr2" style="color:red; display:none;">Please Don't Enter Future Date</span>
					</div>

				  </div>
			
					<div class="col-sm-6 col-lg-3">
						<s:if
		test="%{#session.USER.UserType.name()=='ADMIN'|| #session.USER.UserType.name()=='SUBADMIN' || #session.USER_TYPE.name()=='SUPERADMIN'}">
				
		<label>Acquirer :</label><br> 
					  <div class="txtnew">
						<div class="selectBox " id="selectBox1" onclick="showCheckboxes(event,1)" title="ALL">
						  <select class="input-control">
							<option>ALL</option>
						  </select>
						  <div class="overSelect"></div>
						</div>
						<div id="checkboxes1" class="CloseOnClick" onclick="getCheckBoxValue(1)">
						   <s:checkboxlist headerKey="ALL" headerValue="ALL" list="@com.pay10.commons.util.AcquirerTypeUI@values()" 
										id="acquirer" class="myCheckBox1" listKey="code"
						   listValue="name" name="acquirer" value="acquirer" />
						</div>
					  </div>
			
				
	</s:if>
	<s:else>
		<label>Acquirer :</label><br>
					  <div class="txtnew">
						<div class="selectBox" id="selectBox1" onclick="showCheckboxes(event,1)" title="ALL">
						  <select class="input-control">
							<option>ALL</option>
						  </select>
						  <div class="overSelect"></div>
						</div>
						<div id="checkboxes1" class="CloseOnClick" onclick="getCheckBoxValue(1)">
						   <s:checkboxlist headerKey="ALL" headerValue="ALL" list="@com.pay10.commons.util.AcquirerTypeUI@values()" 
										id="acquirer" class="myCheckBox1" listKey="code"
						   listValue="code" name="acquirer" value="acquirer" />
						</div>
					  </div>
			

	</s:else>
					</div>

					<div class="col-sm-6 col-lg-3" style="display: inline-flex;">

						<div class="txtnew" style="margin-right: 15px;">
							<input type="button" id="submit" value="View"
							
								class="btn btn-primary  mt-4 submit_btn">
								
						</div>
						<!-- <div class="txtnew">
							<input type="button" id="download" value="Download"
								class="btn btn-primary  mt-4 submit_btn" onclick="downloadReport()">
								
						</div> -->
					
		</div>
		<div class="col-sm-6 col-lg-3" style="display: inline-flex;">

			<!-- <div class="txtnew" style="margin-right: 15px;">
				<input type="button" id="submit" value="View"
				
					class="btn btn-primary  mt-4 submit_btn">
					
			</div> -->
			<div class="txtnew">
				<input type="button" id="download" value="Download"
					class="btn btn-primary  mt-4 submit_btn" onclick="downloadReport()">
					
			</div>
		
</div>
		
				
				
				
			  </div>
			  </div>
			  
		
		  </div>
		</div>
	  </div>
	<table id="mainTable" width="100%" border="0" align="center"
		cellpadding="0" cellspacing="0" class="txnf">
		
		<tr>
		
			<td colspan="5" align="left" valign="top">
				<div class="MerchBx">
				
					
					
				</div>

			</td>
		</tr>
	</table>
		<tr>
			<td colspan="5" align="left"><h2>&nbsp;</h2></td>
		</tr>
		<tr>
			<td align="left" style="padding: 10px;">
				<div class="scrollD">
					<table id="txnResultDataTable" class="display nowrap" cellspacing="0"
						width="100%">
						<thead>
							<tr class="boxheadingsmall">
								<th style='text-align: center' class="my_class">Txn Id</th>
								<th style='text-align: center'>Pg Ref Num</th>
								<th style='text-align: center'>Merchant</th>
								<th style='text-align: center'>Date</th>
								<th style='text-align: center'>Order Id</th>
								<th style='text-align: center'>Refund Order Id</th>
								<th style='text-align: center'>Mop Type</th>
								<th style='text-align: center'>Payment Method</th>
								<th style="text-align:center;">Acquirer TDR/SC</th>
								<th style="text-align:center;">Acquirer GST</th>
								<th style="text-align:center;">PG TDR/SC</th>
								<th style="text-align:center;">PG GST</th>
								<th style='text-align: center'>Txn Type</th>
								<th style='text-align: center'>Status</th>
								
								<th style='text-align: center' >Base Amount</th>
							 <th style='text-align: center' >Total Amount</th>
							<th style='text-align: center' >Pay ID</th>
							 <th style='text-align: center' >Customer Email</th>
							 <th style='text-align: center' >Customer Ph Number</th>								
							
								<th>Acquirer Type</th>
								<th></th>
							</tr>
						</thead>
						<tfoot>
							<tr class="boxheadingsmall">
								<th></th>
								<th></th>
								<th></th>
								<th></th>
								<th></th>
								<th></th>
								<th></th>
								<th></th>
								<th></th>
								<th></th>
								<th></th>
								<th></th>
								<th></th>
								<th></th>
								<th></th>
								<th></th>

								
								<th></th>
							 <th></th>
								<th></th>
								<th></th>
								<th></th>
														
								
							
							</tr>
						</tfoot>
					</table>
				</div>
			</td>
		</tr>


	
		<s:form id="downloadPaymentReport" name="downloadPaymentsReportActionAuperAdmin" action="downloadPaymentsReportActionAuperAdmin">
		<s:hidden name="currency" id="currencyH" value="" />
		<s:hidden name="dateFrom" id="dateFromH" value="" />
		<s:hidden name="dateTo" id="dateToH" value="" />
		<s:hidden name="merchantPayId" id="merchantPayIdH" value="" />
		<s:hidden name="tenantId" id="tenantIdH" value="" />
		<s:hidden name="transactionType" id="transactionTypeH" value="" />
		<s:hidden name="paymentType" id="paymentTypeH" value="" />		
		<s:hidden name="status" id="statusH" value="" />
		<s:hidden name="acquirer" id="acquirerH" value="" />
		<s:hidden name="transactionId" id="transactionIdH" value="" />
		<s:hidden name="orderId" id="orderIdH" value="" />
		<s:hidden name="customerEmail" id="customerEmailH" value="" />		
		<s:hidden name="customerPhone" id="customerPhoneH" value="" />
		<s:hidden name="mopType" id="mopTypeH" value="" />
		<s:hidden name="token" value="%{#session.customToken}" />		
	</s:form>
	
  </div>
  
  <div class="card-body">	
	<br>	
	<div class="card ">	
		<div class="card-header card-header-rose card-header-text">	
		  <div class="card-text">	
			<h4 class="card-title">Initiated </h4>	
		  </div>	
		</div>													
		<div class="card-body ">	
			<div class="container">	
			  <div class="row">	
					 
	<!-- <label>Initiated :</label><br>		
												 -->	
	<table width="100%" class="table table-responsive" id="totalTxnsAmount">																 												  	
	</table>	
	</div>	
	</div>	
	</div>	
	</div>	
	<br>	
	<div class="card ">	
		<div class="card-header card-header-rose card-header-text">	
		  <div class="card-text">	
			<h4 class="card-title">Settled </h4>	
		  </div>	
		</div>		
		<div class="card-body ">	
			<div class="container">	
			  <div class="row">	
					 
	<!-- <label>Settled :</label><br>												 -->	
	<table width="100%" class="table table-responsive" id="totalSettledTxnsAmount">																 												  	
	</table>	
	</div>	
	</div>	
	</div>	
	</div>	
	<br>	
	<s:a action='settledTransactionSearch' class="btn btn-primary  mt-4 submit_btn">Settled Report	
	<!-- <label style="display:inline-block;margin-right:10px;"></label> -->	
	</s:a>	
	

<!-- <label style="display:inline-block;margin-right:90px;">Settled Dashboard</label> -->

</div>
		
		<s:form name="chargeback" action="chargebackAction">
		<s:hidden name="orderId" id="orderIdc" value="" />
		<s:hidden name="payId" id="payIdc" value="" />
		
		<s:hidden name="txnId" id="txnIdc" value="" />
		<s:hidden name="token" value="%{#session.customToken}" />
	</s:form>
	
	
	<s:form name="refundDetails" action="refundConfirmAction">
		<s:hidden name="orderId" id="orderIdr" value="" />
		<s:hidden name="payId" id="payIdr" value="" />
		<s:hidden name="transactionId" id="txnIdr" value="" />
		<s:hidden name="token" value="%{#session.customToken}" />
	</s:form>
	
	
	
 


<script src="../js/customtransaction.js"></script>
<!-- <script src="../js/core/popper.min.js"></script>

<script src="../js/core/bootstrap-material-design.min.js"></script>
<script src="../js/plugins/perfect-scrollbar.jquery.min.js"></script> -->
<script>
$(document).ready(function(){
	
	$(document).click(function(){
		expanded = false;
		$('#checkboxes1').hide();
		$('#checkboxes2').hide();
		$('#checkboxes3').hide();
		$('#checkboxes4').hide();		
	});
	$('#checkboxes1').click(function(e){
		e.stopPropagation();
	});
	$('#checkboxes2').click(function(e){
		e.stopPropagation();
	});
	$('#checkboxes3').click(function(e){
		e.stopPropagation();
	});
	$('#checkboxes4').click(function(e){
		e.stopPropagation();
	});
	$('#closeBtn').click(function(){
		$('#popup').hide();
	});
	$('#closeBtn1').click(function(){
		$('#popup').hide();
	});
	

});

var expanded = false;

function showCheckboxes(e,uid) {
	$(".CloseOnClick").hide();
  var checkboxes = document.getElementById("checkboxes"+uid);
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
</body>
</html>