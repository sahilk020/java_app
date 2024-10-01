<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<meta name="viewport" content="width=device-width, initial-scale=1" />
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
		<title>Merchant Profile</title>
		<link href="../css/default.css" rel="stylesheet" type="text/css" />
		<script src="../js/jquery.minshowpop.js"></script>
		<script src="../js/jquery.formshowpop.js"></script>
		<script src="../js/commanValidate.js"></script>
		<link href="../css/editTenant.css" rel="stylesheet" type="text/css" />
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

	<script src="../js/editTenantDetails.js"></script>
	</head>
	<body >
	
			<div class="content " style="background-color: white;min-height: 100%;">
					<div class="content">
						<div class="container-fluid">
						
								<table class="table98 padding0">
							<table class="table98 padding0" style="width:100%">
								<tr>
									<td align="center">&nbsp;</td>
									<td height="10" align="center">
										<ul  id="tabs" class="nav nav-tabs" style="border-bottom:none;" data-tabs="tabs"
										style="border-bottom: none;">
											<li  data-toggle="tab" class="active"><a href="#PGSettings">PG Settings</a></li>
											<li data-toggle="tab"><a href="#CompanyDetails">Company Details</a></li>
											<li data-toggle="tab"><a href="#BankDetails">Bank Details</a></li>
											<li data-toggle="tab"><a href="#kycDetails">KYC</a></li>
											<!-- <li data-toggle="tab"><a href="#DocumentsUploads">Upload Documents</a></li> -->
											<!--    <li><a href="#LogoUpload">Upload Logo</a></li>        -->      
										</ul>
									</td>
								</tr>
								<tr>
									<td align="center">&nbsp;</td>
									<td height="10" align="center">
										<!-- <s:form action="" autocomplete="off" name="form1"> -->
											<!-- <div id="my-tab-content1" class="tab-content"> -->
											<section id="PGSettings" class="tab-content active">
												<div>
													<br/>
													<button class="btn btn-primary" type="button" data-toggle="collapse" data-target="#collapseExample" aria-expanded="false" aria-controls="collapseExample">
														See the Instructions
													  </button>
													  <div class="collapse" id="collapseExample">
														<div class="card card-body">
															<div style="text-align: left; font-weight: bold;color:black">
																<ul>
																	<li>File should be in PNG format only.</li>
																	<li>You need to upload all 10 images.</li>
																	<li>Please Upload the images as per the defined dimension below for each images.</li>
																	<li>Also, we have provided a download link where you can check the exact location of the Image where you want to upload.</li>
																	<li>Note, Dimention should be in Width * Height below.</li>
																	<li>1. At first Place you need to upload Image For header Logo with dimension 66*46 .</li>
																	<li>2. At Second Place you need to upload Image For header Text with dimension 148*46.</li>
																	<li>3. At Third Place you need to upload Image For Footer Logo with dimension 191*40.</li>
																	<li>4. At Fourth Place you need to upload Image For Logout Page Logo with dimension 239*57</li>
																	<li>5. At Fifth Place you need to upload Image For Thank You Page Logo with dimension 128*41</li>
																	<li>6. At Sixth Place you need to upload Image For Invoice Receipt Company Logo with dimension 98*98</li>
																	<li>7. At Seventh Place you need to upload Image For Invoice Receipt Company Text Logo  with dimension 85*29</li>
																	<li>8. At Eighth Place you need to upload Image For Payment Page Logo with dimension 280*40</li>
																	<li>10. At Nineth Place you need to upload Image For CRM Index Page Logo with dimension 214*46</li>
										
																</ul>
															</div>
														</div>
													  </div>
													<div  >
														<table class=" profilepage product-specbigstripes">
															<tr>
															<td>	<label>Status</label><br /></td>
																			<td><s:select class="textFL_merch" headerValue="ALL"
																					list="@com.pay10.commons.util.UserStatusType@values()"
																					id="status" name="userStatus"
																					value="%{companyProfile.tenantStatus}" /></td>	
															</tr>
															<tr>
																<td><label>Active Acquirer</label></td>
                                                                <td>
																	<div class="col-sm-12 col-lg-12">
																		<s:if
														test="%{#session.USER.UserType.name()=='ADMIN'|| #session.USER.UserType.name()=='SUBADMIN' || #session.USER_TYPE.name()=='SUPERADMIN'}">
																
														<label>Acquirer :</label><br> 
																	  <div class="txtnew">
																		<div class="selectBox" id="selectBox1" onclick="showCheckboxes(event,1)" title="ALL">
																		  <select class="input-control">
																			<option>ALL</option>
																		  </select>
																		  <div class="overSelect"></div>
																		</div>
																		<div id="checkboxes1" onclick="getCheckBoxValue(1)">
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
																		<div id="checkboxes1" onclick="getCheckBoxValue(1)">
																		   <s:checkboxlist headerKey="ALL" headerValue="ALL" list="@com.pay10.commons.util.AcquirerTypeUI@values()" 
																						id="acquirer" class="myCheckBox1" listKey="code"
																		   listValue="code" name="acquirer" value="acquirer" />
																		</div>
																	  </div>
															
												
													</s:else>
																	</div>
																</td>
															</tr>
															<tr>
																<td><label>PG URL</label></td>
                                                                <td><s:textfield type="text" class="textFL_merch" id="pgUrl" minlength="8"  maxlength="300"
																	value="%{companyProfile.pgUrl}" name="pgUrl" autocomplete="off" onkeyup="FieldValidator.valdPgUrl(false);_ValidateField();" /></td>
																	<span id="pgUrlErr" class="invocspan"></span>
															</tr>
															<tr>
																<td><label>Logo Upload</label></td>
																<td>
																	
																	
																	  <div class="col-sm-12">
																		<div  id="docErr">Upload CRM header Icon Logo here<span style="color:red;">*</span>
																			<span style="float: right;" >
																				<a href="../image/HeaderLogoForTextandIcon.png" download>
																				<i class="fa fa-download"></i>
																			  </a></span></div>
																		<div class="col-md-12 fileUpload btn btn-orange">
																		  <img src="https://image.flaticon.com/icons/svg/136/136549.svg" class="icon">
																		  <span class="upl" id="upload">Upload document</span>
																		  
																			<form enctype="multipart/form-data" method="post" class="whitelabelForm" id="headerIconlogo" >
																				<div style="display:flex">											
																		
																			
																		  <input type="file" name="fileName" class="upload up whitelabelUploadImage companyHeaderIcon" accept="image/png" id="up" onchange="readURL(this);return CheckDimension(this);" />
																		  <input type="hidden"  value="HEADERICONLOGO" class="whitelabelFormKey"   />
																		  <span style="display:none;"  id='width'></span>
                                                                          <span  style="display:none;" id='height'></span>
																				</div>
																		</form>
																		
																		</div><!-- btn-orange -->
																		<span id="imageHeaderIcon"></span>
																	  </div>
																	  <div class="col-sm-12">
																		<div  id="docErr">Upload CRM header Text logo here<span style="color:red;">*</span>
																			<span style="float: right;">
																			<a href="../image/HeaderLogoForTextandIcon.png" download>
																			<i class="fa fa-download"></i>
																		  </a></span>
																		</div>
																		<div class="col-md-12 fileUpload btn btn-orange">
																		  <img src="https://image.flaticon.com/icons/svg/136/136549.svg" class="icon">
																		  <span class="upl" id="upload1">Upload document</span>
																		  
																			<form enctype="multipart/form-data" method="post" class="whitelabelForm" id="headerTextlogo" >
																				<div style="display:flex">											
																		
								
																		  <input type="file" name="fileName" class="upload up whitelabelUploadImage companyHeaderText" accept="image/png" id="up" onchange="readURL(this);return CheckDimension(this);" />
																		  <input type="hidden"  value="HEADERTEXTLOGO" class="whitelabelFormKey"   />
																		  <span style="display:none;"  id='width'></span>
                                                                          <span  style="display:none;" id='height'></span>
																				</div>
																		</form>
																		</div><!-- btn-orange -->
																		<span id="imageHeaderText"></span>
																	  </div>
																	  <div class="col-sm-12">
																		<div  id="docErr">Upload CRM Footer Logo here<span style="color:red;">*</span>
																			<span style="float: right;">
																				<a href="../image/footerDimension.png" download>
																				<i class="fa fa-download"></i>
																			  </a></span></div>
																		<div class="col-md-12 fileUpload btn btn-orange">
																		  <img src="https://image.flaticon.com/icons/svg/136/136549.svg" class="icon">
																		  <span class="upl" id="upload2">Upload document</span>
																		  
																			<form enctype="multipart/form-data" method="post"  class="whitelabelForm" id="footerLogo" >
																				<div style="display:flex">		
																													
																		  <input type="file" name="fileName" class="upload up whitelabelUploadImage companyFooter" id="up" accept="image/png" onchange="readURL(this);return CheckDimension(this);" />
																		  <input type="hidden"  value="FOOTERLOGO"   class="whitelabelFormKey"  />
																		  <span style="display:none;"  id='width'></span>
                                                                          <span  style="display:none;" id='height'></span>  
																				</div>
																		</form>
																		
																		</div><!-- btn-orange -->
																		<span id="imageFotter"></span>
																	  </div>
																	  <div class="col-sm-12">
																		<div  id="docErr">Upload CRM Logout Logo here<span style="color:red;">*</span>
																			<span style="float: right;">
																				<a href="../image/LogoutPageLogo.png" download>
																				<i class="fa fa-download"></i>
																			  </a></span></div>
																		<div class="col-md-12 fileUpload btn btn-orange">
																		  <img src="https://image.flaticon.com/icons/svg/136/136549.svg" class="icon">
																		  <span class="upl" id="upload3">Upload document</span>
																		  
																			<form enctype="multipart/form-data" method="post" id="logoutLogo" name="logoutLogo"  >
																				<div style="display:flex">											
																		  <input type="file" name="fileName" accept="image/png" class="upload up whitelabelUploadImage companyLogout" id="up" onchange="readURL(this);return CheckDimension(this);" />
																		   <input type="hidden"  value="LOGOUTLOGO"   class="whitelabelFormKey"  />
																				</div>
																		</form>
																		</div><!-- btn-orange -->
																		<span id="imageLogout"></span>
																	  </div>
																	  <div class="col-sm-12">
																		<div  id="docErr">Upload CRM Thank You Page Logo here<span style="color:red;">*</span>
																			<span style="float: right;">
																				<a href="../image/ThankyouPageLogo.png" download>
																				<i class="fa fa-download"></i>
																			  </a></span></div>
																		<div class="col-md-12 fileUpload btn btn-orange">
																		  <img src="https://image.flaticon.com/icons/svg/136/136549.svg" class="icon">
																		  <span class="upl" id="upload4">Upload document</span>
																		  
																			<form enctype="multipart/form-data" method="post" id="thankyouLogo"  class="whitelabelForm" >
																				<div style="display:flex">											
																		  <input type="file"  name="fileName" accept="image/png" class="upload up whitelabelUploadImage companyThankyou" id="up" onchange="readURL(this);return CheckDimension(this);" />
																		   <input type="hidden"  value="THANKYOULOGO"   class="whitelabelFormKey"  />
																		 
																				</div>
																		</form>
																		</div><!-- btn-orange -->
																		<span id="imageThankYou"></span>
																	  </div>
																	  <div class="col-sm-12">
																		<div  id="docErr">Upload CRM Invoice Receipt Company Logo here<span style="color:red;">*</span>
																			<span style="float: right;">
																				<a href="../image/InvoiceReceiptTextandIconLogo.png" download>
																				<i class="fa fa-download"></i>
																			  </a></span></div>
																		<div class="col-md-12 fileUpload btn btn-orange">
																		  <img src="https://image.flaticon.com/icons/svg/136/136549.svg" class="icon">
																		  <span class="upl" id="upload5">Upload document</span>
																		  
																			<form enctype="multipart/form-data" method="post" id="receipIconlogo"   class="whitelabelForm" >
																				<div style="display:flex">											
																		  <input type="file" name="fileName" accept="image/png" class="upload up whitelabelUploadImage companyReceiptLogo" id="up" onchange="readURL(this);return CheckDimension(this);" />
																		  <input type="hidden"  value="RECEIPTICONLOGO"   class="whitelabelFormKey"  />
																				</div>
																		</form>
																		</div><!-- btn-orange -->
																		<span id="imageReceiptIcon"></span>
																	  </div>
																	  <div class="col-sm-12">
																		<div  id="docErr">Upload CRM Invoice Receipt Company name Text Logo here<span style="color:red;">*</span>
																			<span style="float: right;">
																				<a href="../image/InvoiceReceiptTextandIconLogo.png" download>
																				<i class="fa fa-download"></i>
																			  </a></span></div>
																		<div class="col-md-12 fileUpload btn btn-orange">
																		  <img src="https://image.flaticon.com/icons/svg/136/136549.svg" class="icon">
																		  <span class="upl" id="upload6">Upload document</span>
																		  
																			<form enctype="multipart/form-data" method="post" id="receipTextlogo"  class="whitelabelForm">
																				<div style="display:flex">											
																		  <input type="file" name="fileName" accept="image/png" class="upload up whitelabelUploadImage companyReceiptText"  id="up" onchange="readURL(this);return CheckDimension(this);" />
																		  <input type="hidden"  value="RECEIPTTEXTLOGO"   class="whitelabelFormKey"  />
																				</div>
																		</form>
																		</div><!-- btn-orange -->
																		<span id="imageReceiptText"></span>
																	  </div>
																	  <div class="col-sm-12">
																		<div  id="docErr">Upload Payment page Logo here<span style="color:red;">*</span>
																			<span style="float: right;">
																				<a href="../image/PaymentPageLogo.png" download>
																				<i class="fa fa-download"></i>
																			  </a></span></div>
																		<div class="col-md-12 fileUpload btn btn-orange">
																		  <img src="https://image.flaticon.com/icons/svg/136/136549.svg" class="icon">
																		  <span class="upl" id="upload7">Upload document</span>
																		  
																			<form enctype="multipart/form-data" method="post" id="pglogo"  class="whitelabelForm">
																				<div style="display:flex">											
																		  <input type="file" name="fileName" accept="image/png" class="upload up whitelabelUploadImage companyPg" id="up" onchange="readURL(this);return CheckDimension(this);" />
																		  <input type="hidden"  value="PGLOGO"   class="whitelabelFormKey"  />
																				</div>
																		</form>
																		</div><!-- btn-o  <input type="submit" class="btn btn-info btn-xs"  id="btnBulkLogoUpload" disabled onclick="bulkLogoUpload('PGLOGO','pglogo')"> 
																			<input type="button" value="Delete" class="btn btn-info btn-xs"  id="btnBulkLogoDelete" disabled onclick="deleteBulkImage('PGLOGO','pglogo')"> 
																		  range -->
																		  <span id="imagePgLogo"></span>
																	  </div>
																	  
																	  <div class="col-sm-12">
																		<div  id="docErr">Upload CRM Index Page Logo Here<span style="color:red;">*</span>
																			<span style="float: right;">
																				<a href="../image/amex-card.png" download>
																				<i class="fa fa-download"></i>
																			  </a></span></div>
																		<div class="col-md-12 fileUpload btn btn-orange">
																		  <img src="https://image.flaticon.com/icons/svg/136/136549.svg" class="icon">
																		  <span class="upl" id="upload8">Upload document</span>
																		  
																			<form enctype="multipart/form-data" method="post" id="crmLogo"  class="whitelabelForm">
																				<div style="display:flex">										
																				
																		  <input type="file" name="fileName" accept="image/png" class="upload up whitelabelUploadImage companyCrm" id="up" onchange="readURL(this);return CheckDimension(this);" />
																		  <input type="hidden"  value="CRMLOGO"   class="whitelabelFormKey"  />
																		  </div> 
																		</form>
																		</div><!-- btn-orange -->
																		<span id="imageCrmLogo"></span>
																	  </div>
																	  
																	  <div class="col-md-12">

																		<input type="submit" class="btn btn-info btn-xs"  id="btnBulkLogoUpload" disabled onclick="uploadWhiteLabelDocs()">
																		<input type="button" value="Delete" class="btn btn-info btn-xs"  id="btnBulkLogoDelete" disabled onclick="deleteBulkImage()"> 
																	  
																	  </div>
																</td>
																
															</tr>
															<tr>
																<td height="30" align="left"></td>
																<td align="left" style="float:left;">
																	<s:submit id="pgbtnsave"  value="Save" onclick="saveTenantEditDetails()" class="btn btn-success size"> </s:submit>
																</td>
															</tr>
														</table>
													</div>
												</div>
											</section>
											<!--  </div> -->
											<section id="CompanyDetails" class="tab-content hide">
												<div>
													<br/>
													<div  >
														<table class=" profilepage product-specbigstripes">
															<tr>
																<td width="18%" height="30" align="left" valign="middle" ><label>Tenant Number:<span style="color:red;">*</span></label></td>
																<td width="82%" align="left" >
																	<s:textfield type="text" class="textFL_merch" id="tanentNo"
																	name="tanentNo" value="%{companyProfile.tenantNumber}" 
																	onkeyup="this.value = this.value.toUpperCase();FieldValidator.valdTanentNo(false);_ValidateField();"  maxlength="10" minlength="10" /><!--onkeypress="return ValidateInvoiceNo(event)"-->
																<span id="tanentNoErr" class="invocspan"></span>
																</td>
															</tr>
															<tr>
																<td height="30" align="left" valign="middle" ><label>Company Name.:<span style="color:red;">*</span></label></td>
																<td align="left" >
																	<s:textfield type="text" maxlength="256" minlength="2" class="textFL_merch" id="cname" onkeyup="FieldValidator.valdCompName(false);_ValidateField()"
																	value="%{companyProfile.companyName}" name="cname" autocomplete="off" onkeypress="if(event.keyCode === 32)return true;" />
																
																	<span id="cnameErr" class="invocspan"></span>
																</td>
															</tr>
															<tr>
																<td height="30" align="left" valign="middle" ><label>Phone:<span style="color:red;">*</span></label></td>
																<td align="left" >
																	<s:textfield type="text" id="phone" name="phone" maxlength="13" minlength="8"
																	value="%{companyProfile.mobile}" class="textFL_merch" autocomplete="off" onkeyup="FieldValidator.valdPhoneNo(false);_ValidateField();" onkeypress="return isNumberKey(event)"/>
																
																<span id="phoneErr" class="invocspan"></span>
																</td>
															</tr>
															<tr>
																<td height="30" align="left" valign="middle" ><label>Email Id:<span style="color:red;">*</span></label></td>
																<td align="left" >
																	<s:textfield type="text" id="emailId" name="email" maxlength="100" minlength="8" 
																	value="%{companyProfile.emailId}" class="textFL_merch" readonly="true"  onkeyup="FieldValidator.valdEmail(false);_ValidateField();"
																			autocomplete="off" />
															
																	<span id="emailIdErr" class="invocspan"></span>
																</td>
															</tr>
															<tr>
																<td height="30" align="left" valign="middle" ><label>City:<span style="color:red;">*</span></label></td>
																<td align="left" >
																	<s:textfield type="text" maxlength="100" minlength="2"  class="textFL_merch" id="city" onkeyup="FieldValidator.valdCity(false);_ValidateField();"
																	value="%{companyProfile.city}"	name="city" autocomplete="off" onkeypress="if(event.keyCode === 32)return true;return lettersOnly(event,this);"/>
														
																<span id="cityErr" class="invocspan"></span>
																	</td>
															</tr>
															<tr>
																<td height="30" align="left" valign="middle" ><label>State:<span style="color:red;">*</span></label></td>
																<td align="left" >
																	<s:select list="@com.pay10.commons.util.States@values()" name="state" id="state" cssClass="input-control" value="defaultState" autocomplete="off" listKey="name" listValue="name"></s:select>
															
																</td>
															</tr>
															<tr>
																<td height="30" align="left" valign="middle" ><label>Country:<span style="color:red;">*</span></label></td>
																<td align="left" >
																	<s:select list="@com.pay10.commons.util.BinCountryMapperType@values()" name="country" id="country" cssClass="input-control" value="defaultCountry" autocomplete="off" listKey="name" listValue="name"></s:select>
																	
																</td>
															</tr>
															<tr>
																<td height="30" align="left" valign="middle" ><label>Postal Code:<span style="color:red;">*</span></label></td>
																<td align="left" >
																	<s:textfield type="text" class="textFL_merch" id="zip" minlength="6"  maxlength="10"
																	value="%{companyProfile.postalCode}"	name="zip" autocomplete="off" onkeyup="FieldValidator.valdZip(false);_ValidateField();" onkeypress="return isNumberKey(event)" />
													
																<span id="zipErr" class="invocspan"></span></td>
															</tr>
															<tr>
																<td height="30" align="left" valign="middle" ><label>HSNSAC Code:<span style="color:red;">*</span></label></td>
																<td align="left" >
																	<s:textfield type="text" class="textFL_merch" id="tanNumber" minlength="4"  maxlength="8"
																	value="%{companyProfile.hsnSacCode}"	name="tanNumber" autocomplete="off" onkeyup="FieldValidator.valdTanNumber(false);_ValidateField();"  />	
																	<span id="tanNumberErr" class="invocspan"></span>
															</tr>
															<tr>
																<td height="30" align="left" valign="middle" ><label>TAN Number<span style="color:red;">*</span></label></td>
																<td align="left" >
																<s:textfield type="text" class="textFL_merch" id="hsnSac" minlength="2"  maxlength="30"
																value="%{companyProfile.tanNumber}" name="hsnSac" autocomplete="off" onkeyup="FieldValidator.valdHsnSac(false);_ValidateField();" onkeypress="return isNumberKey(event)" />
																
																<span id="hsnSacErr" class="invocspan"></span></td>
															</tr>
															<tr>
																<td height="30" align="left" valign="middle" ><label>CIN:<span style="color:red;">*</span></label></td>
																<td align="left" >
																	<s:textfield type="text" class="textFL_merch" id="cin" minlength="2"  maxlength="30"
																	value="%{companyProfile.cin}"	name="cin" autocomplete="off" onkeyup="FieldValidator.valdCin(false);_ValidateField();"  />
																	
																	<span id="cinErr" class="invocspan"></span>
																</td>
															</tr>
															<tr>
																<td height="30" align="left" valign="middle" ><label>Company GST No:<span style="color:red;">*</span></label></td>
																<td align="left" >
																	<s:textfield type="text" class="textFL_merch" id="comGst" minlength="1"  maxlength="100"
																	value="%{companyProfile.companyGstNo}" name="comGst" autocomplete="off" onkeyup="FieldValidator.valdCompGst(false);_ValidateField();" onkeypress="if(event.keyCode === 32)return true;" />
																
																<span id="comGstErr" class="invocspan"></span></td>
															</tr>
															<tr>
																<td height="30" align="left" valign="middle" ><label>Website URL:<span style="color:red;">*</span></label></td>
																<td align="left" >
																	<s:textfield type="text" class="textFL_merch" id="websiteUrl" minlength="8"  maxlength="300"
																	value="%{companyProfile.companyUrl}"	onkeyup="FieldValidator.valdWebsiteUrl(false);_ValidateField();"  name="websiteUrl" autocomplete="off" />	</div>
																<span id="websiteUrlErr" class="invocspan"></span></td>
															</tr>
															<tr>
																<td height="30" align="left" valign="middle" ><label>Address:<span style="color:red;">*</span></label></td>
																<td align="left" >
																	<s:textfield type="text"  maxlength="150" minlength="3" class="textFL_merch" id="address" onkeypress="if(event.keyCode === 13)return false;" onkeyup="FieldValidator.valdAddress(false);_ValidateField();"
																	value="%{companyProfile.address}"	name="address" />
																
																<span id="addressErr" class="invocspan"></span>	</td>
															</tr>
															<tr>
																<td height="30" align="left"></td>
																<td align="left" style="float:left;">
																	<s:submit id="companybtnsave"  value="Save" onclick="saveTenantEditDetails()" class="btn btn-success size"> </s:submit>
																</td>
															</tr>
														</table>
													</div>
												</div>
											</section>
											<section id="BankDetails" class="tab-content hide">
												<div>
													<br />
													<div  >
														<table class=" profilepage product-specbigstripes">
															<tr>
																<td width="18%" height="30" align="left" valign="middle" ><label>Bank Name:<span style="color:red;">*</span></label></td>
																<td width="82%" align="left">
																	<s:textfield name="bankName" id="bankName" class="input-control textFL_merch"
																	autocomplete="off"  onkeyup="FieldValidatorBank.valdBankName();_ValidateBankField(false);"
																	value="%{companyProfile.bankName}" /><!--onkeypress="return checkName(event);"-->
																	<span id="bankNameErr" class="invocspan"></span></td>
															</tr>
															<tr>
																<td height="30" align="left" valign="middle" ><label>IFSC Code:<span style="color:red;">*</span>&nbsp;</label></td>
																<td align="left">
																	<s:textfield name="ifscCode" id="ifscCode" class="input-control textFL_merch"
																	autocomplete="off"  onkeyup="this.value = this.value.toUpperCase();FieldValidatorBank.valdIfsc(false);_ValidateBankField();"
																	value="%{companyProfile.ifscCode}" />
																	<span id="ifscCodeErr" class="invocspan"></span></td>
															</tr>
															<tr>
																<td height="30" align="left" valign="middle" ><label>Acc Holder Name:<span style="color:red;">*</span>&nbsp;&nbsp;</label></td>
																<td align="left">
																	<s:textfield type="text" class="textFL_merch" id="accHolderName" onkeyup="FieldValidatorBank.valdAccName(false);_ValidateBankField();"
																	value="%{companyProfile.accHolderName}" name="accHolderName" autocomplete="off" minlength="4"  maxlength="200" onkeypress="if(event.keyCode === 32)return true;" />
																	<span id="accHolderNameErr" class="invocspan"></span>	</td>
															</tr>
															<tr>
																<td height="30" align="left" valign="middle" ><label>Currency:<span style="color:red;">*</span>&nbsp;&nbsp;</label></td>
																<td align="left">
																	<s:textfield type="text" name="currencyCode" id="currencyCode" maxlength="3" minlength="3" 
																	value="%{companyProfile.currency}"
																	class="input-control" onchange="FieldValidatorBank.valdCurrCode(false);_ValidateBankField();"
																	autocomplete="off" onkeypress="return isNumberKey(event)"  style="height: 33px!important;" />
															<span id="currencyCodeErr" class="invocspan"></span></td>
															</tr>
															<tr>
																<td height="30" align="left" valign="middle" ><label>Branch Name:<span style="color:red;">*</span>&nbsp;&nbsp;</label></td>
																<td align="left">
																	<s:textfield type="text" class="textFL_merch" id="branchName" onkeyup="FieldValidatorBank.valdBranch(false);_ValidateBankField();"
																	value="%{companyProfile.branchName}"	name="branchName" autocomplete="off" minlength="3" onkeypress="if(event.keyCode === 32)return true;"  maxlength="200" />
																	<span id="branchNameErr" class="invocspan"></span></td>
															</tr>
															<tr>
																<td height="30" align="left" valign="middle" ><label>Pan Card:<span style="color:red;">*</span>&nbsp;&nbsp;</label></td>
																<td align="left">
																	<s:textfield type="text" class="textFL_merch" id="panCard" onkeyup="this.value = this.value.toUpperCase();FieldValidatorBank.valdPan(false);_ValidateBankField();"
																	value="%{companyProfile.panCard}"	name="panCard" autocomplete="off" minlength="10"  maxlength="10" />
																	<span id="panCardErr" class="invocspan"></span></td>
															</tr>
															<tr>
																<td height="30" align="left" valign="middle" ><label>Name on PAN Card:<span style="color:red;">*</span>&nbsp;&nbsp;</label></td>
																<td align="left">
																	<s:textfield type="text" class="textFL_merch" id="panName" onkeyup="FieldValidatorBank.valdPanName(false);_ValidateBankField();"
																	value="%{companyProfile.panName}"	name="panName" autocomplete="off" minlength="2"  maxlength="30" onkeypress="if(event.keyCode === 32)return true;" />
																	<span id="panNameErr" class="invocspan"></span></td>
															</tr>
															<tr>
																<td height="30" align="left" valign="middle" ><label>Account No.:<span style="color:red;">*</span>&nbsp;&nbsp;</label></td>
																<td align="left">
																	<s:textfield type="text" class="textFL_merch" id="accountNo" onkeyup="FieldValidatorBank.valdAccountNo(false);_ValidateBankField();"
																	value="%{companyProfile.accountNo}"	name="accountNo" autocomplete="off" minlength="6"  maxlength="200" />
																	<span id="accountNoErr" class="invocspan"></span></td>
															</tr>
															<tr>
																<td height="30" align="left"></td>
																<td align="left" style="float:left;">
																	<s:submit id="bankbtnsave" value="Save" onclick="saveTenantEditDetails()" class="btn btn-success size"> </s:submit>
																</td>
															</tr>
														</table>
													</div>
												</div>
											</section>
											<section id="kycDetails" class="tab-content hide">
												<div>
													<br />
													<div>
														<!-- <table class=" profilepage product-specbigstripes">
															<tr>
																<td> -->
																	<s:submit  value="Save" id="kycbtnsave" onclick="saveTenantEditDetails()" class="btn btn-success size"> </s:submit>
																	<h4>Documents Details </h4>
																	<label>Details of all Director</label><br>
															<form>
																<div class="col-sm-3 col-md-3">
																	<label>Name<span style="color:red;">*</span></label><br>
																	<input type="text" placeholder="Name" class="textFL_merch" id="kycName" onkeyup="FieldValidatorKYC.valdKYCName(false);_ValidateKYCField();"
																	 name="kycName" autocomplete="off" minlength="6"  maxlength="200" />
																	<span id="kycNameErr" class="invocspan"></span>
															   </div>
															   <div class="col-sm-3 col-md-3">
																   <label>PAN Number<span style="color:red;">*</span></label><br>
																	<input type="text"  placeholder="PAN Number" class="textFL_merch" id="panCardKYC" onkeyup="this.value = this.value.toUpperCase();FieldValidatorKYC.valdPanKYC(false);_ValidateKYCField();"
																	 name="panCardKYC" autocomplete="off" minlength="6"  maxlength="200" />
																	<span id="panCardKYCErr" class="invocspan"></span>
																</div>
																<div class="col-sm-3 col-md-3">
																	<label>Aadhar No.<span style="color:red;">*</span></label><br>
																	<input type="text" id="aadhar" placeholder="Aadhar" class="textFL_merch"  onkeyup="FieldValidatorKYC.valdAadhar(false);_ValidateKYCField();"
																	 name="aadhar" autocomplete="off" minlength="6"  maxlength="200" />
																	<span id="aadharErr" class="invocspan"></span>
																</div>
																<div class="col-sm-3 col-md-3">
																	<label>Mobile No.<span style="color:red;">*</span></label><br>
																	<input type="text"  placeholder="Mobile" class="textFL_merch" id="mobileNokyc" onkeyup="FieldValidatorKYC.valdKycPhoneNo(false);_ValidateKYCField();"
																	 name="mobileNokyc" autocomplete="off" minlength="6"  maxlength="200" />
																	<span id="mobileNokycErr" class="invocspan"></span>
																</div>
																<div class="col-sm-3 col-md-3">
																	<label>Email Id<span style="color:red;">*</span></label><br>
																	<input type="text"  placeholder="Email Address" class="textFL_merch" id="emailIdkyc" onkeyup="FieldValidatorKYC.kycEmailId(false);_ValidateKYCField();"
																	 name="emailIdkyc" autocomplete="off" minlength="6"  maxlength="200" />
																	<span id="emailIdkycErr" class="invocspan"></span>
																</div>
																<div class="col-sm-3 col-md-3">
																	
																<input type="button" id="addDirector" class="add-row btn btn-success size" value="Add Director">
																</div>
															</form>
															<table>
																<thead>
																	<tr>
																		<!-- <th>Select</th> -->
																		<th>Name</th>
																		<th>PAN</th>
																		<th>Aadhar</th>
																		<th>Mobile</th>
																		<th>Email</th>
																	</tr>
																</thead>
																<tbody id="details">
																	<tr>
																		<!-- <td></td> -->
																		<td ></td>
																		<td ></td>
																		<td ></td>
																		<td ></td>
																		<td ></td>
																	</tr>
																</tbody>
															</table>
															<!-- <button type="button" class="btn btn-success size delete-row">Delete Director</button> -->
														
														
																
														<!-- </td>
														</tr> -->
															<!-- <tr>
																<td valign="bottom" colspan="2" align="center">
																	<table class="table98 padding0">
																		<tr>
																			<td width="40%">
																				<s:submit  value="Save" class="btn btn-success size"> </s:submit>
																			</td>
																			<td width="58%"></td>
																		</tr>
																	</table> -->
														<!-- </table> -->
														<div class="col-md-12">
															<span style="color:red;font-size: 12px;">Please upload documents only in 'pdf', 'jpg', 'jpeg' & 'png'  format & File size should be in between : 100KB - 2MB.</span>
															<div class="card ">
															  <div class="card-header card-header-rose card-header-text">
																<div class="card-text" >
																  <h4 class="card-title">Company Documents</h4>
																 
																</div>
															  </div>
															  <div class="card-body ">
																
																<div class="col-md-4 col-sm-4">
																	<h4 class="title">PAN Image</h4>
																	<!-- <div class="docErr1">Please upload valid file</div> -->
																	<div class="fileUpload1 btn btn-orange">
																	  <img src="https://image.flaticon.com/icons/svg/136/136549.svg" class="icon">
																	  <span class="upl1" id="upload1">Upload document</span>
																	  
																		<form enctype="multipart/form-data" method="post" class="whitelabelDirectorForm" id="companyPanName" >
																			<div style="display:flex">											<!-- <input type="hidden"   value="IPRULE" name="file1" /> -->
																	
																		<!-- <input type="hidden"   value="IPRULE" name="file1" /> -->
																	  <input type="file" name="fileName" class="upload1 up1" id="up1" onchange="readURL1(this);" />
																	  <input type="submit" class="btn btn-info btn-xs"  id="btnBulkPanUpload" disabled onclick="bulkLogoUpload('COMPANYPANIMAGE','companyPanName',this)"> 
																	  <input type="button" value="Delete" class="btn btn-info btn-xs"  id="btnBulkPanDelete" disabled onclick="deleteDirectorImage('COMPANYPANIMAGE','companyPanName')"> 
																			</div>
																	</form>
																	</div><!-- btn-orange -->
																  </div>
																  <div class="col-md-4 col-sm-4">
																	<h4 class="title">TAN Image</h4>
																	<!-- <div class="docErr1">Please upload valid file</div> -->
																	<div class="fileUpload1 btn btn-orange">
																	  <img src="https://image.flaticon.com/icons/svg/136/136549.svg" class="icon">
																	  <span class="upl1" id="upload1">Upload document</span>
																	  
																		<form enctype="multipart/form-data" method="post" class="whitelabelDirectorForm" id="companyTanImage" >
																			<div style="display:flex">											<!-- <input type="hidden"   value="IPRULE" name="file1" /> -->
																	
																		<!-- <input type="hidden"   value="IPRULE" name="file1" /> -->
																	  <input type="file" name="fileName" class="upload1 up1" id="up1" onchange="readURL1(this);" />
																	  <input type="submit" class="btn btn-info btn-xs"  id="btnBulkTanUpload" disabled onclick="bulkLogoUpload('COMPANYTANIMAGE','companyTanImage')"> 
																	  <input type="button" value="Delete" class="btn btn-info btn-xs"  id="btnBulkTanDelete" disabled onclick="deleteDirectorImage('COMPANYTANIMAGE','companyTanImage')"> 
																			</div>
																	</form>
																	</div><!-- btn-orange -->
																  </div>
																  <div class="col-md-4 col-sm-4">
																	<h4 class="title">GST Image</h4>
																	<!-- <div class="docErr1">Please upload valid file</div> -->
																	<div class="fileUpload1 btn btn-orange">
																	  <img src="https://image.flaticon.com/icons/svg/136/136549.svg" class="icon">
																	  <span class="upl1" id="upload1">Upload document</span>
																	  
																		<form enctype="multipart/form-data" method="post" class="whitelabelDirectorForm" id="companyGstImage" >
																			<div style="display:flex">											<!-- <input type="hidden"   value="IPRULE" name="file1" /> -->
																	
																		<!-- <input type="hidden"   value="IPRULE" name="file1" /> -->
																	  <input type="file" name="fileName1" class="upload1 up1" id="up1" onchange="readURL1(this);" />
																	  <input type="submit" class="btn btn-info btn-xs"  id="btnBulkGstUpload" disabled onclick="bulkLogoUpload('COMPANYGSTIMAGE','companyGstImage')"> 
																	  <input type="button" value="Delete" class="btn btn-info btn-xs"  id="btnBulkGstDelete" disabled onclick="deleteDirectorImage('COMPANYGSTIMAGE','companyGstImage')"> 
																			</div>
																	</form>
																	</div><!-- btn-orange -->
																  </div>
																  <div class="col-md-4 col-sm-4">
																	<h4 class="title">AOA Image</h4>
																	<!-- <div class="docErr1">Please upload valid file</div> -->
																	<div class="fileUpload1 btn btn-orange">
																	  <img src="https://image.flaticon.com/icons/svg/136/136549.svg" class="icon">
																	  <span class="upl1" id="upload1">Upload document</span>
																	  
																		<form enctype="multipart/form-data" method="post" class="whitelabelDirectorForm" id="companyAoaImage" >
																			<div style="display:flex">											<!-- <input type="hidden"   value="IPRULE" name="file1" /> -->
																	
																		<!-- <input type="hidden"   value="IPRULE" name="file1" /> -->
																	  <input type="file" name="fileName1" class="upload1 up1" id="up1" onchange="readURL1(this);" />
																	  <input type="submit" class="btn btn-info btn-xs"  id="btnBulkAoaUpload" disabled onclick="bulkLogoUpload('COMPANYAOAIMAGE','companyAoaImage')"> 
																	  <input type="button" value="Delete" class="btn btn-info btn-xs"  id="btnBulkAoaDelete" disabled onclick="deleteDirectorImage('COMPANYAOAIMAGE','companyAoaImage')"> 
																			</div>
																	</form>
																	</div><!-- btn-orange -->
																  </div>
														  </div>
														  </div>
														  </div>
														  
													</div>
												</div>
											</section>
								
											<s:hidden name="token" value="%{#session.customToken}"></s:hidden>
										<!-- </s:form> -->
										
									</td>
								</tr>
								<tr>
									<td>&nbsp;</td>
									<td>&nbsp;</td>
								</tr>
							</table>
						</td>
					</tr>
					<tr>
						<td align="left" valign="top">&nbsp;</td>
					</tr>
					<tr>
						<td align="left" valign="top">&nbsp;</td>
					</tr>
				</table>
				</div>
				
				
				
				
			</div>
			<div class="modal fade" id="logoUploadDocs" role="dialog">
				<div class="modal-dialog">
			
				  <!-- Modal content-->
				  <div class="modal-content">
					<div class="modal-header">
			
					</div>
					<div class="modal-body">
					  <p class="addbene" id="listLogo"></p>
					</div>
					<div class="modal-footer" id="modal_footer">
					  <button type="button" class="btn btn-primary btn-round mt-4 submit_btn" data-dismiss="modal">Ok</button>
					</div>
				  </div>
			
				</div>
			  </div>
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
		</div>
			</div>
			<div class="clear"></div>
			
		</div>
		
		<!-- <script type="text/javascript"> jQuery(document).ready(function ($) {  $('#tabs').tab(); });</script>
		
	 -->

		<script>
			var directorList =[];
			$(document).ready(function(){
				$(".add-row").click(function(){
					var name = $("#kycName").val();
					var email = $("#emailIdkyc").val();
					var aadhar = $("#aadhar").val();
					var pan = $("#panCardKYC").val();
					var mobile = $("#mobileNokyc").val();
					var director = {directorname:name, directorEmail:email, directorAadharNumber:aadhar,directorpan:pan,directorMobileNumber:mobile}
					directorList.push(director);
					var markup = "<tr><td>" + name + "</td><td>" + pan + "</td><td>" + aadhar + "</td><td>" + mobile + "</td><td>" + email + "</td></tr>";
					$("table tbody#details").append(markup);
				});
				
				// Find and remove selected table rows
				// $(".delete-row").click(function(){
				// 	$("table tbody").find('input[name="record"]').each(function(){
				// 		if($(this).is(":checked")){
				// 			$(this).parents("tr").remove();
				// 		}
				// 	});
				// });
			});    
		</script>
	
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