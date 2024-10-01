<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Create Tenant</title>
<link href="../css/default.css" rel="stylesheet" type="text/css" />
<script src="../js/jquery.minshowpop.js"></script>
<script src="../js/jquery.formshowpop.js"></script>
<script src="../js/commanValidate.js"></script>
<link href="../css/default.css" rel="stylesheet" type="text/css" />

<link href="../css/jquery-ui.css" rel="stylesheet" />
<link href="../css/Jquerydatatable.css" rel="stylesheet" />
<script src="../js/jquery.min.js" type="text/javascript"></script>
<script src="../js/core/popper.min.js"></script>
<script src="../js/core/bootstrap-material-design.min.js"></script>
<script src="../js/plugins/perfect-scrollbar.jquery.min.js"></script>
<!-- Control Center for Material Dashboard: parallax effects, scripts for the example pages etc -->
<script src="../js/material-dashboard.js?v=2.1.0" type="text/javascript"></script>
<script src="../js/moment.js" type="text/javascript"></script>
<script src="../js/jquery.dataTables.js"></script>
<script src="../js/jquery-ui.js"></script>
<script src="../js/commanValidate.js"></script>
<script src="../js/dataTables.buttons.js" type="text/javascript"></script>
<!--  loader scripts -->
<link rel="stylesheet" href="../css/loader/normalize.css" />
<link rel="stylesheet" href="../css/loader/main.css" />
<link rel="stylesheet" href="../css/loader/customLoader.css" />

<link href="../css/select2.min.css" rel="stylesheet" />
<script src="../js/jquery.select2.js" type="text/javascript"></script>
<script src="../js/createTenant.js" type="text/javascript"></script>

<script>
	$(document).ready(function () {

// Initialize select2
//$("#merchantPayId").select2();


});
</script>
<style>
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
</style>
</head>
<body>
	<div id="loader-wrapper"
		style="width: 100%; height: 100%; display: none;">
		<div id="loader"></div>
	</div>
	
	<!-- <div class="row">
		<div class="col-md-12">
			<div class="col-sm-6 col-lg-3">
				<div class=" txtnew">
					<s:if test="%{#session.USER.UserType.name()=='ADMIN' || #session.USER.UserType.name()=='SUBADMIN'  || #session.USER.UserType.name()=='SUBADMIN' || #session.USER_TYPE.name()=='SUPERADMIN'}">
		<label>Select Merchant:</label>
		
			<s:select name="merchantPayId" class="input-control merchantPayId" id="merchantPayId"
				  list="merchantList"
				listKey="payId" listValue="businessName" autocomplete="off" />
		</s:if>
		<s:else> 
			<label> Merchant:</label>
			<s:select name="merchantPayId" class="input-control" id="merchantPayId" 
			list="merchantList" listKey="payId" listValue="businessName" autocomplete="off" />
			</s:else>
	</div> <span id="merchantErr" class="invocspan"></span>
</div>
</div>
</div> -->
	<table width="100%" border="0" align="center" cellpadding="0"
	cellspacing="0" class="txnf">
	<tr>
		<td align="left">
			<div style="display: none" id="response"></div>
			
		</td>
		
	</tr>
	<tr>
		<button class="btn btn-primary" type="button" data-toggle="collapse" data-target="#collapseExample" aria-expanded="false" aria-controls="collapseExample">
			See the Instructions
		  </button>
		  <div class="collapse" id="collapseExample">
			<div class="card card-body">
				<div style="text-align: left; font-weight: bold;color:black">
					<ul>
						<li>All fields are Mandatory.</li>
						<li>TAN Number should be like first 6 alphabets and last 4 is Number.</li>
						<li>IFSC Code should contain first 4 Alpha, then 0 , last 6 letters can be number or alphabets.</li>
						<li>PAN Number should contain first 5 Alpha , next 4 number , last 1 Alpha like : ABCDE1234F.</li>
						<li>Once you have added all correct details you are able to click on create button for creating a Tenant details.</li>
						</ul>
				</div>
			</div>
		  </div>
	</tr>

  <s:if test="%{responseObject.responseCode=='000'}">
   <tr>
    <td align="left" valign="top"><div id="saveMessage">
        <s:actionmessage class="success success-text" />
      </div></td>
  </tr>
  
  </s:if>
<s:else><div class="error-text"><s:actionmessage/></div></s:else>

	<tr>
		
		<td colspan="5" align="left" valign="top">
		
				<div class="col-md-12">
					<div class="card ">
					  <div class="card-header card-header-rose card-header-text">
						<div class="card-text" >
						  <h4 class="card-title">Company Details</h4>
						</div>
					  </div>
					  <div class="card-body ">
						<div class="container">
						  <div class="row">
							  <div class="col-md-12" style="margin-bottom:30px">
			
					<div id="proInvoiceHide" >
					<div class="productIn" >
						<label>Tenant Number<span style="color:red;">*</span></label> <br />
						<div class="txtnew">
							<s:textfield type="text" class="textFL_merch" id="tanentNo"
								name="tanentNo" value="" 
								onkeyup="this.value = this.value.toUpperCase();FieldValidator.valdTanentNo(false);_ValidateField();"  maxlength="10" minlength="10" /><!--onkeypress="return ValidateInvoiceNo(event)"-->
							<span id="tanentNoErr" class="invocspan"></span>
						</div>
					</div>
				</div>
				<div id="proNameHide">
					<div class="productIn">
						<label>Company Name<span style="color:red;">*</span></label><br />
						<div class="txtnew">
							<s:textfield type="text" maxlength="256" minlength="2" class="textFL_merch" id="cname" onkeyup="FieldValidator.valdCompName(false);_ValidateField()"
							value="" name="cname" autocomplete="off" onkeypress="if(event.keyCode === 32)return true;" />
						</div>
						<span id="cnameErr" class="invocspan"></span>
					</div>
				</div>
				<div id="proPhoneHide">
					<div class="productIn">
						<label>Phone<span style="color:red;">*</span></label><br />
						<div class="txtnew">
							<s:textfield type="text" id="phone" name="phone" maxlength="13" minlength="8"
							value="%{invoice.phone}" class="textFL_merch" autocomplete="off" onkeyup="FieldValidator.valdPhoneNo(false);_ValidateField();" onkeypress="return isNumberKey(event)"/>
						</div>
						<span id="phoneErr" class="invocspan"></span>
					</div>
				</div>
				<div id="proEmailHide">
					<div class="productIn">
						<label>Email Id<span style="color:red;">*</span></label><br />
						<div class="txtnew">
							<s:textfield type="text" id="emailId" name="email" maxlength="100" minlength="8" 
								value="%{invoice.email}" class="textFL_merch"  onkeyup="FieldValidator.valdEmail(false);_ValidateField();"
								autocomplete="off" />
						</div>
						<span id="emailIdErr" class="invocspan"></span>
					</div>
				</div>
				<div id="proCityHide">
					<div class="productIn">
						<label>City<span style="color:red;">*</span></label><br />
						<div class="txtnew">
							<s:textfield type="text" maxlength="100" minlength="2"  class="textFL_merch" id="city" onkeyup="FieldValidator.valdCity(false);_ValidateField();"
							value="%{invoice.city}"	name="city" autocomplete="off" onkeypress="if(event.keyCode === 32)return true;return lettersOnly(event,this);"/>
						</div>
						<span id="cityErr" class="invocspan"></span>
					</div>
				</div>
				<div id="proStateHide">
					<div class="productIn">
						<label>State<span style="color:red;">*</span></label><br />
						<div class="txtnew">
							<s:select list="@com.pay10.commons.util.States@values()" name="state" style="height:35px;" id="state" cssClass="input-control" value="defaultState" autocomplete="off" listKey="name" listValue="name"></s:select>
																</div>
						<span id="stateErr" class="invocspan"></span>
					</div>
				</div>
				
					<div id="proCountryHide">
					<div class="productIn">
						<label>Country<span style="color:red;">*</span></label><br />
						<div class="txtnew">
							<s:select
		list="@com.pay10.commons.util.BinCountryMapperType@values()" name="country" id="country" listKey="name"
		listValue="name" class="input-control" style="height:35px;" value="defaultCountry" maxlength="100" minlength="2"  >
		</s:select>
							<!-- <s:textfield type="text" id="country" name="country" onkeyup="FieldValidator.valdCountry(false)"
								class="textFL_merch" autocomplete="off" onkeypress="return lettersOnly(event,this);"/> -->
						</div>
						<span id="countryErr" class="invocspan"></span>
					</div>
					</div>

					<div id="proZipHide">
					<div class="productIn">

						<label>Pin Code<span style="color:red;">*</span></label>	<br />
						<div class="txtnew">
							<s:textfield type="text" class="textFL_merch" id="zip" minlength="6"  maxlength="10"
							value="%{invoice.zip}"	name="zip" autocomplete="off" onkeyup="FieldValidator.valdZip(false);_ValidateField();" onkeypress="return isNumberKey(event)" />
						</div>
						<span id="zipErr" class="invocspan"></span>
					</div>
					</div>
				
					<div class="productIn">
						<label>HSNSAC Code<span style="color:red;">*</span></label><br />
						<div class="txtnew">
							<s:textfield type="text" class="textFL_merch" id="hsnSac" minlength="4"  maxlength="8"
							value=""	name="hsnSac" autocomplete="off" onkeyup="FieldValidator.valdHsnSac(false);_ValidateField();" onkeypress="return isNumberKey(event)" />
						</div>
						<span id="hsnSacErr" class="invocspan"></span>
					</div>

					<div class="productIn">
					<label>TAN Number<span style="color:red;">*</span></label><br />
					
						<div class="txtnew">
							<s:textfield type="text" class="textFL_merch" id="tanNumber" minlength="2"  maxlength="30"
							value=""	name="tanNumber" autocomplete="off" onkeyup="FieldValidator.valdTanNumber(false);_ValidateField();"  />
						</div>
						<span id="tanNumberErr" class="invocspan"></span>
					</div>
					
					<div class="productIn">
							<label>CIN<span style="color:red;">*</span></label><br />
						<div class="txtnew">
							<s:textfield type="text" class="textFL_merch" id="cin" minlength="2"  maxlength="30"
							value="%{invoice.zip}"	name="cin" autocomplete="off" onkeyup="FieldValidator.valdCin(false);_ValidateField();"  />
						</div>
						<span id="cinErr" class="invocspan"></span>
					</div>

					<div class="productIn">
						<label>Company GST No.<span style="color:red;">*</span></label><br />
						<div class="txtnew">
							<s:textfield type="text" class="textFL_merch" id="comGst" minlength="1"  maxlength="100"
							value="" name="comGst" autocomplete="off" onkeyup="FieldValidator.valdCompGst(false);_ValidateField();" onkeypress="if(event.keyCode === 32)return true;" />
						</div>
						<span id="comGstErr" class="invocspan"></span>
					</div>
					<div class="productIn">
						<label>Website Url<span style="color:red;">*</span></label><br />
						<div class="txtnew">
							<s:textfield type="text" class="textFL_merch" id="websiteUrl" minlength="8"  maxlength="300"
							value=""	onkeyup="FieldValidator.valdWebsiteUrl(false);_ValidateField();"  name="websiteUrl" autocomplete="off" />	</div>
						<span id="websiteUrlErr" class="invocspan"></span>
					</div>
					<div class="productIn">
						<label>PG Url<span style="color:red;">*</span></label><br />
						<div class="txtnew">
							<s:textfield type="text" class="textFL_merch" id="pgUrl" minlength="8"  maxlength="300"
							value="" name="pgUrl" autocomplete="off" onkeyup="FieldValidator.valdPgUrl(false);_ValidateField();" />
						</div>
						<span id="pgUrlErr" class="invocspan"></span>
					</div>
					<div class="productIn">
						<label>Address<span style="color:red;">*</span></label><br />
						<div class="txtnew">
							<s:textfield type="text"  maxlength="150" minlength="3" class="textFL_merch" id="address" onkeypress="if(event.keyCode === 13)return false;" onkeyup="FieldValidator.valdAddress(false);_ValidateField();"
							value="%{invoice.address}"	name="address" />
						</div>
						<span id="addressErr" class="invocspan"></span>
					</div>
				
				
				
				
				</div>
				</div>
				</div>
				</div>
				</div>
				</div>
				
				
				
			</td>

	<tr>
		<td colspan="5" align="left" valign="top">&nbsp;</td>
	</tr>
	<!-- <tr>
		<td colspan="5" align="left" valign="top"><h3>Product Information</h3></td>
	</tr> -->
	<tr>
		<td colspan="5" align="left" valign="top"><div class="">
				<div class="col-md-12">
					<div class="card ">
					  <div class="card-header card-header-rose card-header-text">
						<div class="card-text">
						  <h4 class="card-title">Company Bank Details</h4>
						</div>
					  </div>
					  <div class="card-body ">
						<div class="container">
						  <div class="row">
							  <div class="col-md-12" >
				
					<div class="productIn">
						<label>Bank Name<span style="color:red;">*</span></label></label><br />
						<div class="txtnew">
							<s:textfield type="text" name="bankName" id="bankName" class="textFL_merch"
							autocomplete="off"  onkeyup="FieldValidator.valdBankName();_ValidateField(false);"
							value="" /><!--onkeypress="return checkName(event);"-->
							<span id="bankNameErr" class="invocspan"></span>
						</div>
					</div>
					<div class="productIn">
						<label>IFSC Code<span style="color:red;">*</span></label><br />
						<div class="txtnew">
							<s:textfield type="text" name="ifscCode" id="ifscCode" class="textFL_merch"
								autocomplete="off"  onkeyup="this.value = this.value.toUpperCase();FieldValidator.valdIfsc(false);_ValidateField();"
								value="" />
								<span id="ifscCodeErr" class="invocspan"></span>
						</div>
					</div>
					<div class="productIn">
						<label>Account Holder Name<span style="color:red;">*</span></label><br />
						<div class="txtnew">
							<s:textfield type="text" class="textFL_merch" id="accHolderName" onkeyup="FieldValidator.valdAccName(false);_ValidateField();"
							value="" name="accHolderName" autocomplete="off" minlength="4"  maxlength="200" onkeypress="if(event.keyCode === 32)return true;" />
							<span id="accHolderNameErr" class="invocspan"></span>
						</div>
					</div>
					<div class="productIn">
						<label> Currency<span style="color:red;">*</span></label><br />
						<div class="txtnew">
							<s:select name="currencyCode" id="currencyCode" maxlength="3" minlength="3" 
								list="currencyMap" listKey="key" listValue="value"
								class="input-control" onchange="FieldValidator.valdCurrCode(false);_ValidateField();"
								autocomplete="off" onkeypress="return isNumberKey(event)"  style="height: 33px!important;" /></div>
						<span id="currencyCodeErr" class="invocspan"></span>
					</div>
				</div>
				<div class="col-md-12" >
					<div class="productIn">
						<label>Branch Name<span style="color:red;">*</span></label><br />
						<div class="txtnew">
							<s:textfield type="text" class="textFL_merch" id="branchName" onkeyup="FieldValidator.valdBranch(false);_ValidateField();"
							value=""	name="branchName" autocomplete="off" minlength="3" onkeypress="if(event.keyCode === 32)return true;"  maxlength="200" />
							<span id="branchNameErr" class="invocspan"></span>
						</div>
					</div>
					<div class="productIn">
						<label>PAN Card<span style="color:red;">*</span></label><br />
						<div class="txtnew">
							<s:textfield type="text" class="textFL_merch" id="panCard" onkeyup="this.value = this.value.toUpperCase();FieldValidator.valdPan(false);_ValidateField();"
							value=""	name="panCard" autocomplete="off" minlength="10"  maxlength="10" />
							<span id="panCardErr" class="invocspan"></span>
						</div>
					</div>
					<div class="productIn">
						<label>Name on PAN<span style="color:red;">*</span></label><br />
						<div class="txtnew">
							<s:textfield type="text" class="textFL_merch" id="panName" onkeyup="FieldValidator.valdPanName(false);_ValidateField();"
							value=""	name="panName" autocomplete="off" minlength="2"  maxlength="30" onkeypress="if(event.keyCode === 32)return true;" />
							<span id="panNameErr" class="invocspan"></span>
						</div>
					</div>
					<div class="productIn">
						<label>Account No.<span style="color:red;">*</span></label><br />
						<div class="txtnew">
							<s:textfield type="text" class="textFL_merch" id="accountNo" onkeyup="FieldValidator.valdAccountNo(false);_ValidateField();"
							value=""	name="accountNo" autocomplete="off" minlength="6"  maxlength="200" />
							<span id="accountNoErr" class="invocspan"></span>
						</div>
					</div>
					
					
				
				
				</div>
				</div>
				</div>
				</div>
				</div>
				</div>
				
				
			</td>
	</tr>
	<tr>
		<td align="center" valign="top"><table width="100%" border="0"
				cellpadding="0" cellspacing="0">
				<tr>
					<td width="15%" align="left" valign="middle"></td>
					<td width="5%" align="right" valign="middle"><s:submit
							id="btnSave" name="fileName" class="btn btn-primary  mt-4 submit_btn" 
							disabled="true"
							value="Create" onclick="saveTenant()" >
						</s:submit></td>
					<td width="3%" align="left" valign="middle"></td>
					<td width="15%" align="left" valign="middle"></td>
				</tr>
				</tr>
			</table>

			<div class="modal fade" id="modalAddTenant" role="dialog">
				<div class="modal-dialog">
			
				  <!-- Modal content-->
				  <div class="modal-content">
					<div class="modal-header">
			
					</div>
					<div class="modal-body">
					  <p class="addbene" id="tenadd"></p>
					</div>
					<div class="modal-footer" id="modal_footer">
					  <button type="button" class="btn btn-primary btn-round mt-4 submit_btn" data-dismiss="modal">Ok</button>
					</div>
				  </div>
			
				</div>
			  </div>
			  <div class="modal fade" id="modalAddTenantError" role="dialog">
				<div class="modal-dialog">
			
				  <!-- Modal content-->
				  <div class="modal-content">
					<div class="modal-header">
			
					</div>
					<div class="modal-body">
					  <p class="addbene" id="otpnumber">Failed to save Tenant</p>
					</div>
					<div class="modal-footer" id="modal_footer">
					  <button type="button" class="btn btn-primary btn-round mt-4 submit_btn" data-dismiss="modal">Ok</button>
					</div>
				  </div>
			
				</div>
			  </div>
		
</body>
</html>