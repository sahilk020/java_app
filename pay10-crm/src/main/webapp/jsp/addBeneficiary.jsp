<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Add Beneficiary</title>
<link href="../css/default.css" rel="stylesheet" type="text/css" />
<script src="../js/jquery.minshowpop.js"></script>
<script src="../js/jquery.formshowpop.js"></script>
<script src="../js/commanValidate.js"></script>
<link href="../css/default.css" rel="stylesheet" type="text/css" />
<link rel="stylesheet" type="text/css" media="all"
	href="../css/daterangepicker-bs3.css" />
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
<!--  loader scripts -->
<%-- <script src="../js/loader/modernizr-2.6.2.min.js"></script>
<script src="../js/loader/main.js"></script> --%>
<link rel="stylesheet" href="../css/loader/normalize.css" />
<!-- <link rel="stylesheet" href="../css/loader/main.css" /> -->
<link rel="stylesheet" href="../css/loader/customLoader.css" />

<link href="../css/select2.min.css" rel="stylesheet" />
<script src="../js/jquery.select2.js" type="text/javascript"></script>

<script src="../js/addBeneficiary.js"></script>
<script>
	$(document).ready(function () {
    	$("#merchantPayId").select2();
	});
</script>
<style type="text/css">
.error-text {
	color: #a94442;
	font-weight: bold;
	background-color: #f2dede;
	list-style-type: none;
	text-align: center;
	list-style-type: none;
	margin-top: 10px;
}

.error-text li {
	list-style-type: none;
}

#response {
	color: green;
}

.errorMessage {
	display: none;
}

.errorInpt {
	font: 400 11px arial;
	color: red;
	display: none;
	margin-left: 7px;
}

.fixHeight {
	height: 64px;
}

.adduT {
	margin-bottom: 0 !important;
}

.addu {
	height: 625px !important;
}

.btnSbmt {
	padding: 5px 10px !important;
	margin-right: 26px !important;
}

.actionMessage {
	border: 1px solid transparent;
	border-radius: 0 !important;
	width: 100% !important;
	margin: 0 !important;
}
</style>
<style type="text/css">
.cust {
	width: 24% !important;
	margin: 0 5px !important; /*font: bold 10px arial !important;*/
}

.samefnew {
	width: 24% !important;
	margin: 0 5px !important;
	/*font: bold 10px arial !important;*/
}
/* .btn {padding: 3px 7px!important; font-size: 12px!important; } */
.samefnew-btn {
	width: 15%;
	float: left;
	font: bold 11px arial;
	color: #333;
	line-height: 22px;
	margin-left: 5px;
}
/*tr td.my_class{color:#000 !important; cursor: default !important; text-decoration: none;}*/
tr td.my_class {
	cursor: pointer;
}

tr td.my_class:hover {
	cursor: pointer !important;
}

tr th.my_class:hover {
	color: #fff !important;
}

.cust .form-control, .samefnew .form-control {
	margin: 0px !important;
	width: 100%;
}

.select2-container {
	width: 100% !important;
}

.clearfix:after {
	display: block;
	visibility: hidden;
	line-height: 0;
	height: 0;
	clear: both;
	content: '.';
}

#popup {
	position: fixed;
	top: 0px;
	left: 0px;
	background: rgba(0, 0, 0, 0.7);
	width: 100%;
	height: 100%;
	z-index: 999;
	display: none;
}

.innerpopupDv {
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

#loader-wrapper .loader-section.section-left, #loader-wrapper .loader-section.section-right
	{
	background: rgba(225, 225, 225, 0.6) !important;
	width: 50% !important;
}

.invoicetable {
	float: none;
}

.innerpopupDv h2 {
	font-size: 12px;
	padding: 5px;
}

.text-class {
	text-align: center !important;
}

.odd {
	background-color: #e6e6ff !important;
}
.bmd-label-floating{
	margin-left: 5px !important;
}
.dataTables_wrapper {
    margin-top: 25px !important;
}

.textFL_merch {
    border: 1px solid #c0c0c0;
    background: #fff;
    padding: 8px;
    width: 100%;
    color: #000;
    border-radius: 3px;
}

.textFL_merch:hover {
    border: 1px solid #d5d0a3;
    padding: 8px;
    width: 100%;
    border-radius: 3px;
}
	.textFL_merch_invalid {
    border: 1px solid #c0c0c0;
    background: #fff;
    padding: 8px;
    width: 100%;
    border-color: #FF0000;
    border-radius: 1px;
}
.addbene {
  color: #2457a3;
  font-weight: 500;
  text-align: center;
}
.hideElement{
	display: none;
}
</style>
</head>
<body>
	<div id="loader-wrapper" style="width: 100%; height: 100%; display:none;">
		<div id="loader"></div>
	  </div>
	<table width="100%" border="0" cellspacing="0" cellpadding="0"
		class="txnf">
		<!-- <tr>
    <td align="left"><h2>Add New Beneficiary</h2></td>
  </tr> -->



		<s:if test="%{responseObject.responseCode=='000'}">
			<tr>
				<td align="left" valign="top"><div id="saveMessage">
						<s:actionmessage class="success success-text" />
					</div></td>
			</tr>

		</s:if>
		<s:else>
			<div class="error-text">
				<s:actionmessage />
			</div>
		</s:else>

		<!-- <tr>
			<td align="left">

				<div class="container">
					<div class="form-group col-md-3 txtnew col-sm-3 col-xs-4">

						<s:select class="input-control"
							list="@com.pay10.commons.util.AcquirerTypeNodal@values()"
							listValue="name" listKey="code" name="acquirer" id="acquirer"
							autocomplete="off" value="" />
					</div>
				</div>
			</td>
			<td> -->
				<div class="row">
					<div class=" col-md-3  col-lg-3">
					 
						
					</div>
					<div class=" col-md-3  col-lg-3">
						<label>Nodal Acquirer<span
							style="color: red; margin-left: 3px;">*</span></label>
						<s:select class="input-control"
							list="@com.pay10.commons.util.AcquirerTypeNodal@values()"
							listValue="name" listKey="code" name="acquirer" id="acquirer" headerKey="ALL " headerValue="Select Nodal Acquirer "
							autocomplete="off" value="" />
					</div>
						<div class="col-sm-3 col-lg-3">
							<div class=" txtnew">
								<s:if
									test="%{#session.USER.UserType.name()=='ADMIN' || #session.USER.UserType.name()=='SUBADMIN'  || #session.USER.UserType.name()=='SUBADMIN' || #session.USER_TYPE.name()=='SUPERADMIN'}">
									<label>Select Merchant<span
										style="color: red; margin-left: 3px;">*</span></label>

									<s:select name="merchantPayId" headerKey="ALL " headerValue="Select Merchant"
										class="input-control merchantPayId"  id="merchantPayId" onchange="autoLoadDetails()"
										list="listMerchant" listKey="payId" listValue="businessName"
										autocomplete="off" />
								</s:if>
								<s:else>
									<label> Merchant</label>
									<s:select name="merchantPayId" class="input-control"
										id="merchantPayId" list="listMerchant" listKey="payId"
										listValue="businessName" autocomplete="off" />
								</s:else>
							</div>
							<span id="merchantErr" class=""></span>
						</div>
						<!-- <div class="col-sm-3 col-lg-3">
							<button class="btn btn-primary" type="button" data-toggle="modal" data-target="#instruction">
								Instructions
								 </button>
							
						</div>  -->
					</div>
			
					<!-- <div class="modal fade" id="instruction" role="dialog">
						<div class="modal-dialog">
					
						  <div class="modal-content">
							<div class="modal-header">
					
							</div>
							<div class="modal-body">
								<ul>
									<li>Payment types available for Fund transfer from YES BANK to OTHER : NEFT, RTGS, IMPS</li>
									<li>Payment types available for Fund transfer from YES BANK to Yes Bank : NEFT, RTGS, IMPS, FT</li>
									<li>FT - 0 to no limit (same bank account transfer)</li>
									<li>NEFT - upto 2 Lac</li>
									<li>RTGS - above 2 Lac</li>
									<li>IMPS - upto 2 Lac</li>
								</ul>
							</div>
							<div class="modal-footer" id="modal_footer">
							  <button type="button" class="btn btn-primary btn-round mt-4 submit_btn" data-dismiss="modal">Ok</button>
							</div>
						  </div>
					
						</div>
					  </div> -->

		
				<div class="card " style="width: 50%; margin: 60px auto;">
					<div class="card-header card-header-rose card-header-icon">
						<div class="card-icon">
							<i class="material-icons">mail_outline</i>
						</div>
						<h4 class="card-title" style="color: #0271bb; font-weight: 500;">Add Beneficiary</h4>
					</div>
					<div class="card-body ">
						<div class="form-group">
							<label class="bmd-label-floating"> Beneficiary Code<span
								style="color: red; margin-left: 3px;">*</span></label>
							<s:textfield name="beneficiaryCd" id="beneficiaryCd"
								class=" textFL_merch" autocomplete="off" ondrop="return false;" onkeyup="FieldValidator.valdBenCode(false);_ValidateField();"
								onkeypress="return checkInput(event);"
								value="%{nodal.beneficiaryCode}" />
								<span id="beneficiaryCdErr" class=""></span>
								<span id="invalid-benCode" style="color:red;"></span>
						</div>

						<div class="form-group">
							<label class="bmd-label-floating"> Beneficiary Name<span
								style="color: red; margin-left: 3px;">*</span></label>
							<s:textfield name="beneName" id="beneName" class=" textFL_merch"
								autocomplete="off" onkeypress="return checkName(event);" ondrop="return false;" onkeyup="FieldValidator.valdBenName(false);_ValidateField();"
								value="%{nodal.beneName}" />
								<span id="beneNameErr" class=""></span>
								<span id="invalid-benName" style="color:red;"></span>
						</div>
						<div class="form-group">
							<label class="bmd-label-floating"> Beneficiary Account No<span
								style="color: red; margin-left: 3px;">*</span></label>
							<s:textfield name="beneAccountNo" id="beneAccountNo"
								class=" textFL_merch" autocomplete="off"
								onkeypress="return Validate(event);" ondrop="return false;" onkeyup="FieldValidator.valdBenAcc(false);_ValidateField();"
								value="%{nodal.beneAccountNumber}" />
								<span id="beneAccountNoErr" class=""></span>
								<span id="invalid-benAcc" style="color:red;"></span>
						</div>
						<div class="form-group">
							<label class="bmd-label-floating"> IFSC Code<span
								style="color: red; margin-left: 3px;">*</span></label>
							<s:textfield name="ifscCode" id="ifscCode" class=" textFL_merch"
								autocomplete="off" onkeypress="return checkInput(event);" ondrop="return false;" onkeyup="this.value = this.value.toUpperCase();FieldValidator.valdIfsc(false);_ValidateField();"
								value="%{nodal.ifscCode}" />
								<span id="ifscCodeErr" class=""></span>
								<span id="invalid-ifsc" style="color:red;"></span>
						</div>
						<div class="form-group">
							<label class="bmd-label-floating"> Bank Name<span
								style="color: red; margin-left: 3px;">*</span></label>
							<s:textfield name="bankName" id="bankName" class=" textFL_merch"
								autocomplete="off"  onkeyup="FieldValidator.valdBankName();_ValidateField(false);" ondrop="return false;"
								value="%{nodal.bankName}" /><!--onkeypress="return checkName(event);"-->
								<span id="bankNameErr" class=""></span>
								<span id="invalid-bankName" style="color:red;"></span>
						</div>

						<div class="form-group">
							<label class="bmd-label-floating"> Aadhar Number</label>
							<s:textfield name="aadhar" id="aadhar" class=" textFL_merch"
								autocomplete="off" onkeyup="valdAadhar()" ondrop="return false;" onpaste="return false;"
								value="" /><!--onkeypress="return checkName(event);"-->
								<span id="invalid-aadhar" style="color:red;"></span>
								
						</div>
						
						<!-- <div class="form-group">
							<label class="bmd-label-floating"> Beneficiary Type<span
								style="color: red; margin-left: 3px;">*</span></label>
							<s:select class="input-control"
								list="@com.pay10.commons.util.BeneficiaryTypes@values()"
								listValue="name" listKey="code" id="beneType" autocomplete="off"
								value="%{nodal.beneType}"
								 />
						</div> -->
						<div class="form-group">
							<label class="bmd-label-floating"> Currency Code<span
								style="color: red; margin-left: 3px;">*</span></label>
							<s:select class="textFL_merch" 
								list="@com.pay10.commons.util.CurrencyTypes@values()"
								listValue="code" listKey="code" id="currencyCode"
								autocomplete="off" value="%{nodal.currencyCode}"
								 />
						</div>
						<div class="form-group">
							<label class="bmd-label-floating">Expiry Date</label>
							<!-- <s:textfield name="expiryDate" id="expiryDate" class="input-control"
								autocomplete="off" onkeypress="return checkName(event);"
								value="%{nodal.expiryDate}" />
								<div class="txtnew"> -->
									<s:textfield type="text" ondrop="return false;" id="expiryDate" name="expiryDate" placeholder="2020-01-31" class=" textFL_merch" onkeyup="valdExpiryDate()"  	 autocomplete="off"  />
									<span id='invalid-date' style='color:red'></span>
									</div> 
						
						<!-- <div class="form-group">
							<label class="bmd-label-floating">Transaction Limit<span
								style="color: red; margin-left: 3px;">*</span></label>
							<s:textfield  name="transactionLimit" id="transactionLimit" class="input-control textFL_merch"
								autocomplete="off"  onkeypress="return checkInput(event);" onkeyup="FieldValidator.valdTxnLimit(false);_ValidateField();"
								value="%{nodal.transactionLimit}" />
								<span id="transactionLimitErr" class="invocspan"></span>
								
						</div> -->
						<div class="form-group">
							<label class="bmd-label-floating">Mobile Number<span
								style="color: red; margin-left: 3px;"></span></label>
							<s:textfield name="mobileNo" id="mobileNo" class="textFL_merch"
								autocomplete="off"  onkeypress="return checkInput(event);" onkeyup="valdPhoneNo()"
								value="%{nodal.mobileNo}" ondrop="return false;" />
								<span id="invalid-phone" style="color:red;"></span>
						</div>
						<div class="form-group">
							<label class="bmd-label-floating">Email Id<span
								style="color: red; margin-left: 3px;"></span></label>
							<s:textfield  ondrop="return false;" name="emailId" id="emailId" class="textFL_merch" minlength="3" maxlength="128"
								autocomplete="off" onkeyup="checkEmailId()" 
								value="%{nodal.emailId}" />
								<span id="invalid-id" style="color:red;"></span>
						</div>
						<div class="form-group">
							<label class="bmd-label-floating">Address 1<span
								style="color: red; margin-left: 3px;"></span></label>
							<s:textfield name="address1" ondrop="return false;" id="address1" class="textFL_merch"
								autocomplete="off" onkeyup="valdAddress()"
								value="%{nodal.address1}" />
								<span id="invalid-add" style="color:red;"></span>
						</div>
						<div class="form-group">
							<label class="bmd-label-floating">Address 2<span
								style="color: red; margin-left: 3px;"></span></label>
							<s:textfield name="address2" ondrop="return false;" id="address2" class="textFL_merch"
								autocomplete="off"  onkeyup="valdAddress1()"
								value="%{nodal.address2}" />
								<span id="invalid-add1" style="color:red;"></span>
						</div>
					</div>
						<div class="card-footer text-right">

							<s:submit id="btnEditUser" name="btnEditUser" value="Save"
								type="button" class="btn  btn-block btn-primary"
								onclick="saveBeneficiary()">
							</s:submit>
						</div>
						<s:hidden name="token" value="%{#session.customToken}"></s:hidden>
					</div>
				




				</div>


			
	</table>


	<div class="card ">
		<div class="card-header card-header-rose card-header-text">
		  <div class="card-text">
			<h4 class="card-title">Batch Upload</h4>
		  </div>
		</div>
		<div class="card-body ">
	<div class="container">
		
		<div class="row it">
		<div class="col-sm-offset-1 col-sm-10" id="one">
		<!-- <p>
		Please upload documents only in 'CSV' format.
		</p> -->
		<button class="btn btn-primary" type="button" data-toggle="collapse" data-target="#collapseExample" aria-expanded="false" aria-controls="collapseExample">
			See the Instructions
		  </button>
		  <div class="collapse" id="collapseExample">
			<div class="card card-body">
				<div style="text-align: left; font-weight: bold;color:black">
					<ul>
						<li>File should be in CSV format.</li>
						<li>File size must not exceed 500 KB.</li>
						<li>File name length should be 5-50 characters.</li>
						<li>File name can contain alphabets, digits, round brackets, hyphen, underscore, dot and space.</li>
						<li>File should contain 1-100 records.</li>
                        <li>Beneficiary Expiry Date format: YYYY-MM-DD</li>
                        <li>Mandatory Fields: Beneficiary Code, Beneficiary Name,Currency, Bank Name, IFSC Code, Beneficiary Account Number</li>
					</ul>
				</div>
			</div>
		  </div>
		<div class="row">
		  <div class="col-sm-offset-4 col-sm-4 form-group">
			
		  </div><!--form-group-->
		</div><!--row-->
		<div id="uploader">
		<div class="row uploadDoc" style="height: 200px;">
			<div class="col-sm-4 ">
				<table
					id="example" class="csv" style="display: none;">
					<thead>
						<tr>
							<!-- <th>Id</th> -->
							<th>Beneficiary Code</th>
							<th>Beneficiary Name</th>
							<th>Bank Name</th>
							<th>Beneficiary Account Number</th>
							<th>Beneficiary Expiry Date</th>
							<th>Mobile Number</th>
							<th>Email Id</th>
							<th>Address 1</th>
							<th>Address 2</th>
							<th>Aadhar Number</th>
							<th>IFSC Code</th>
							<th>Currency Code</th>
							<!-- <th>Payment Type</th> -->
						</tr>
					</thead>
					<tbody>
						<tr>
						<td>BENE123</td>
						<td>ABCXYZ</td>
						<td>Yes Bank</td>
						<td>567778899987</td>
						<td>2020-01-20</td>
						<td>911234567890</td>
						<td>B123@YES.COM</td>
						<td>A BC DEF</td>
						<td>A BC DEFgh</td>
						<td>123456789312</td>
						<td>SBIN0000121</td>
						<td>INR</td>
						</tr>

						
					</tbody>
				
					
				</table></div>
				
				
				
				<div class="col-sm-6 col-lg-6" style="border-radius: 8px;
				background-color: #dedbdb;">
					<form id="batchAddBene" name="batchAddBeneFile" target="output_frame" method="post" action="batchBeneUpload" enctype="multipart/form-data">
<!-- 							<input type="file" accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" /> -->
						<!-- <label>Please Select File :</label> -->
						<input name="fileUpload" id="batchFile" type="file" accept=".csv" style="margin-top: 25px">
						<input class="hideElement" name = "payId" id="fileUploadMerchantPayId">
						<input class="hideElement" name = "acquirer" id="fileUploadAcquirer">
					  <input type="submit" class="btn btn-info btn-xs" style="position: absolute;
			  left: 162px;
			  top: 75px;"  id="btnBatchUpload" disabled > 
			  
				
						<!-- <input class="hideElement" name = "timestamp" id="cbFileUploadTimeStamp" value="">
						<input class="hideElement"name="usertype" value='<s:property value="%{#session.USER.UserType.name()}"/>'>
						<input class="hideElement" name="username" value='<s:property value = "%{#session.USER.firstName}"/>'>
						<input class="hideElement" name="message" value="">
						<input class="hideElement" name="responseCode" value=""> -->
						
					</form>
					<iframe  class="hideElement" name="output_frame" src="" id="output_frame" width="XX" height="YY">
				</iframe> 
				</div>
				<div class="col-sm-2 ">
					<table
						id="example" class="resultcsv" style="display: none;">
						<thead>
							<tr>
								<th>Id</th>
								<th>Beneficiary code</th>
								<th>Payment type</th>
								<th>Response Message</th>
								<th>Status</th>
								<th>Reason</th>
							</tr>
						</thead>
						<tbody id="addBeneResultCsv">
						
						</tbody>	
					</table>
					
					</div>
				<span id="batchFileUploadError" style="display: none;" class="invocspan"></span>
		 
		</div><!--row-->
		</div><!--uploader-->
		<div class="text-center">
		<!-- <a class="btn btn-new"><i class="fa fa-plus"></i> Add new</a> -->
		<!-- <button type="button"  id="btnBulkUpload" onclick="bulkUpload()"><a class="btn btn-next"><i class="fa fa-upload"></i> Bulk Upload</a></button> -->
		</div>
		</div><!--one-->
		</div><!-- row -->
		</div>
		</div>
	</div>
	<div class="modal fade" id="modalAddBene" role="dialog">
		<div class="modal-dialog">
	
		  <!-- Modal content-->
		  <div class="modal-content">
			<div class="modal-header">
	
			</div>
			<div class="modal-body">
			  <p class="addbene" id="benadd"></p>
			</div>
			<div class="modal-footer" id="modal_footer">
			  <button type="button" class="btn btn-primary btn-round mt-4 submit_btn" data-dismiss="modal">Ok</button>
			</div>
		  </div>
	
		</div>
	  </div>
	  <div class="modal fade" id="modalAddBeneError" role="dialog">
		<div class="modal-dialog">
	
		  <!-- Modal content-->
		  <div class="modal-content">
			<div class="modal-header">
	
			</div>
			<div class="modal-body">
			  <p class="addbene" id="otpnumber">Failed to save beneficiary</p>
			</div>
			<div class="modal-footer" id="modal_footer">
			  <button type="button" class="btn btn-primary btn-round mt-4 submit_btn" data-dismiss="modal">Ok</button>
			</div>
		  </div>
	
		</div>
	  </div>
	
	
	<script>
			function Validate(event) {
			var regex = /^\d+$/;
			var key = String.fromCharCode(!event.charCode ? event.which
					: event.charCode);
			if (!regex.test(key)) {
				event.preventDefault();
				return false;
			}
		}
		function checkInput(event) {
			var regex = /^[0-9a-zA-Z\b]+$/;
			var key = String.fromCharCode(!event.charCode ? event.which
					: event.charCode);
			if (!regex.test(key)) {
				event.preventDefault();
				return false;
			}
		}
		function checkName(event) {
			var regex = /^[a-zA-Z \b]+$/;
			var key = String.fromCharCode(!event.charCode ? event.which
					: event.charCode);
			if (!regex.test(key)) {
				event.preventDefault();
				return false;
			}
		}
	</script>

</body>
</html>