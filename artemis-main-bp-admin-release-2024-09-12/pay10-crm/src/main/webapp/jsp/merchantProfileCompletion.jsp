<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
		<meta name="viewport" content="width=device-width, initial-scale=1" />
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
		<title>Merchant Profile</title>
		<link href="../fonts/css/font-awesome.min.css" rel="stylesheet">
<link rel="stylesheet" type="text/css"
		href="../css/material-icons.css" />
	  <link rel="stylesheet" href="../css/material-font-awesome.min.css">
  <!-- CSS Files -->
  <link href="../css/material-dashboard.css?v=2.1.0" rel="stylesheet" />
		<link href="../css/profile-page.css" rel="stylesheet" />
		<link href="../css/default.css" rel="stylesheet" type="text/css" />
		<link href="../css/custom.css" rel="stylesheet" type="text/css" />
		<link href="../css/merchantProfileCompletion.css" rel="stylesheet" type="text/css" />
		
		<script type="text/javascript" src="../js/jquery.js"></script>
		<script type="text/javascript" src="../js/jquery.easing.js"></script>
		<script type="text/javascript" src="../js/jquery.dimensions.js"></script>
		<script type="text/javascript" src="../js/jquery.accordion.js"></script>
		<script type="text/javascript" src="../js/commanValidate.js"></script>
		<script type="text/javascript" src="../js/merchantProfileCompletion.js"></script>
		<script src="../js/core/popper.min.js"></script>
<script src="../js/core/bootstrap-material-design.min.js"></script>
<script src="../js/plugins/perfect-scrollbar.jquery.min.js"></script>
<!-- Control Center for Material Dashboard: parallax effects, scripts for the example pages etc -->
<script src="../js/material-dashboard.js?v=2.1.0" type="text/javascript"></script>

		<script>
			if (self == top) {
				var theBody = document.getElementsByTagName('body')[0];
				theBody.style.display = "block";
			} else {34
				top.location = self.location;
			}
		</script>
		<script type="text/javascript">
			jQuery().ready(function(){
				// simple accordion
				jQuery('#list1a').accordion();
				jQuery('#list1b').accordion({
					autoheight: false
				});
				
				// second simple accordion with special markup
				jQuery('#navigation').accordion({
					active: false,
					header: '.head',
					navigation: true,
					event: 'click',
					fillSpace: false,
					animated: 'easeslide'
				});
			});
		</script>
		<script type="text/javascript">
			function showDivs(prefix,chooser) {
			        for(var i=0;i<chooser.options.length;i++) {
			                var div = document.getElementById(prefix+chooser.options[i].value);
			                div.style.display = 'none';
			        }
			 
					var selectedvalue = chooser.options[chooser.selectedIndex].value;
			 
					if(selectedvalue == "PL")
					{
						displayDivs(prefix,"PL");
					}
					else if(selectedvalue == "PF")
					{
						displayDivs(prefix,"PF");
					}
					else if(selectedvalue == "PR")
					{
						displayDivs(prefix,"PR");
					}
					else if(selectedvalue == "CSA")
					{
						displayDivs(prefix,"CSA");
					}
					else if(selectedvalue == "LLL")
					{
						displayDivs(prefix,"LLL");
					}
					else if(selectedvalue == "RI")
					{
						displayDivs(prefix,"RI");
					}
					else if(selectedvalue == "AP")
					{
						displayDivs(prefix,"AP");
					}
					else if(selectedvalue == "T")
					{
						displayDivs(prefix,"T");
					}
			 
			}
			 
			function displayDivs(prefix,suffix) {
			 
			        var div = document.getElementById(prefix+suffix);
			        div.style.display = 'block';
			}
			 
			window.onload=function() {
			  document.getElementById('select1').value='a';//set value to your default
			}
			
		</script>
		<script>
			var _validFileExtensions = [".jpg", ".pdf", ".png"];    
			function Validate(oForm) {
			    var arrInputs = oForm.getElementsByTagName("input");
			    for (var i = 0; i < arrInputs.length; i++) {
			        var oInput = arrInputs[i];
			        if (oInput.type == "file") {
			            var sFileName = oInput.value;
			            if (sFileName.length > 0) {
			                var blnValid = false;
			                for (var j = 0; j < _validFileExtensions.length; j++) {
			                    var sCurExtension = _validFileExtensions[j];
			                    if (sFileName.substr(sFileName.length - sCurExtension.length, sCurExtension.length).toLowerCase() == sCurExtension.toLowerCase()) {
			                        blnValid = true;
			                        break;
			                    }
			                }
			                
			                if (!blnValid) {
			                    alert("Sorry, " + sFileName + " is invalid, allowed extensions are: " + _validFileExtensions.join(", "));
			                    return false;
			                }
			            }
			        }
			    }
			  
			    return true;
			}
			
			
			
		</script>
		<script type="text/javascript">
			function onlyalphabate(element, AlertMessage){
				var regexp = /^[a-zA-Z]+$/; 
				if(element.value.match(regexp)) { 
				alert("Letter Validation: Successful."); 
				return true; 
				}
				else{ 
				alert(AlertMessage); 
				element.focus(); 
				return false; 
				}
			}
		</script>
		
	</head>
	<body >
		<div class="wrapper " style="background-color: white;">
			<div class="sidebar" data-color="rose" data-background-color="black">
				<!--
			Tip 1: You can change the color of the sidebar using: data-color="purple | azure | green | orange | danger"
	
			Tip 2: you can also add an image using data-image tag
		-->
				<!-- <div class="logo">
					<a href="home" class="simple-text logo-mini"> <img
						src="../image/66x46.png">
					</a> <a href="home" class="simple-text logo-normal"> <img
						src="../image/148x46.png">
	
	
					</a>
				</div> -->
				<%@ include file="/jsp/headerForMenu.jsp"%> 
				<div class="sidebar-wrapper">
	
					<ul class="nav" id='MenuUl'>
						
							<li class="nav-item " id='home'><s:a action='home'
									class="nav-link active_tab">
									<i class="material-icons">person</i>
									<p>My Profile</p>
								</s:a></li>
							<li class="nav-item " id='transactionSearch'><s:a
									action='passwordChangeSignUp' class="nav-link">
									<i class="material-icons">search</i>
									<p>Change Password</p>
								</s:a></li>
					
				</div>
	
			</div>
			<div class="main-panel" >
				<!-- Navbar -->
				<nav
					class="navbar navbar-expand-lg navbar-transparent navbar-absolute fixed-top "
					style="background-color: #d7dadd !important; position: relative;">
					<div class="container-fluid">
						<div class="navbar-wrapper">
							<div class="navbar-minimize">
								<button id="minimizeSidebar"
									class="btn btn-just-icon btn-white btn-fab btn-round">
									<i
										class="material-icons text_align-center visible-on-sidebar-regular">more_vert</i>
									<i
										class="material-icons design_bullet-list-67 visible-on-sidebar-mini">view_list</i>
								</button>
							</div>
							<a class="navbar-brand" >Merchant Profile</a>
						</div>
						<button class="navbar-toggler" type="button" data-toggle="collapse"
							aria-controls="navigation-index" aria-expanded="false"
							aria-label="Toggle navigation">
							<span class="sr-only">Toggle navigation</span> <span
								class="navbar-toggler-icon icon-bar"></span> <span
								class="navbar-toggler-icon icon-bar"></span> <span
								class="navbar-toggler-icon icon-bar"></span>
						</button>
						<div class="collapse navbar-collapse justify-content-end">
							<form class="navbar-form">
								<!-- <div class="input-group no-border">
								  <input type="text" value="" class="form-control" placeholder="Search...">
								  <button type="submit" class="btn btn-white btn-round btn-just-icon">
									<i class="material-icons">search</i>
									<div class="ripple-container"></div>
								  </button>
								</div> -->
							</form>
							<ul class="navbar-nav">
								<!-- <li class="nav-item">
								  <a class="nav-link" href="#pablo">
									<i class="material-icons">dashboard</i>
									<p class="d-lg-none d-md-block">
									  Stats
									</p>
								  </a>
								</li> -->
	
	
								<li class="nav-item dropdown"><a class="nav-link"
									href="#pablo" id="navbarDropdownProfile" data-toggle="dropdown"
									aria-haspopup="true" aria-expanded="false"> <i
										class="fa fa-user fa-fw"></i><b id="welcomeHeading">Welcome <s:property value="%{user.businessName}" /></a>
									<div class="dropdown-menu dropdown-menu-right"
										aria-labelledby="navbarDropdownProfile">
										<s:a action="logout" id="logout"> Logout</s:a>
										<!-- <a class="dropdown-item" href="#">Profile</a>
									<a class="dropdown-item" href="#">Settings</a> -->
										<!-- <div class="dropdown-divider"></div> -->
	
									</div></li>
							</ul>
						</div>
					</div>
				</nav>
	
	
				
	
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
											<li  data-toggle="tab" class="active"><a href="#MyPersonalDetails">My Personal Details</a></li>
											<li data-toggle="tab"><a href="#MyContactDetails">My Contact Details</a></li>
											<li data-toggle="tab"><a href="#MyBankDetails">My Bank Details</a></li>
											<li data-toggle="tab"><a href="#MyBusinessDetails">My Business Details</a></li>
											<li data-toggle="tab"><a href="#DocumentsUploads">Upload Documents</a></li>
											<!--    <li><a href="#LogoUpload">Upload Logo</a></li>        -->      
										</ul>
									</td>
								</tr>
								<tr>
									<td align="center">&nbsp;</td>
									<td height="10" align="center">
										<s:form action="newMerchantSaveAction" autocomplete="off" name="form1">
											<!-- <div id="my-tab-content1" class="tab-content"> -->
											<section id="MyPersonalDetails" class="tab-content active">
												<div>
													<br/>
													<div  >
														<table class=" profilepage product-specbigstripes">
															<tr>
																<td width="19%" height="25" align="left" ><label>Business name:</label></td>
																<td width="81%" align="left">
																	<s:textfield name="businessName"  id="businessName"  cssClass="input-control" type="text" value="%{user.businessName}" readonly="true" autocomplete="off"></s:textfield>
																	<s:hidden id="payId" name="payId" value="%{user.payId}"/>
																</td>
															</tr>
															<tr>
																<td height="30" align="left" ><label>Email ID:</label></td>
																<td align="left">
																	<s:textfield name="emailId" id="emailId" class="input-control" type="text" value="%{user.emailId}" readonly="true" autocomplete="off"></s:textfield>
																</td>
															</tr>
															<tr>
																<td height="30" align="left" ><label>First Name:</label></td>
																<td align="left">
																	<s:textfield name="firstName" id="firstName"  class="input-control" type="text" value="%{user.firstName}" autocomplete="off" onkeypress="return lettersOnly(event,this);"></s:textfield>
																</td>
															</tr>
															<tr>
																<td height="30" align="left" ><label>Last Name:</label></td>
																<td align="left">
																	<s:textfield name="lastName" id="lastName"  class="input-control" type="text" value="%{user.lastName}" autocomplete="off" onkeypress="return lettersOnly(event,this);"></s:textfield>
																</td>
															</tr>
															
															<tr>
																<td height="30" align="left" ><label>Company Name:</label></td>
																<td align="left">
																	<s:textfield name="companyName" class="input-control" id="companyName" type="text" value="%{user.companyName}" autocomplete="off" onkeypress="return lettersSpaceOnly(event, this);"></s:textfield>
																</td>
															</tr>
															
															<tr>
																<td height="30" align="left"></td>
																<td align="left" style="float:left;">
																	<s:submit  value="Save" class="btn btn-success size"> </s:submit>
																</td>
															</tr>
														</table>
													</div>
												</div>
											</section>
											<!--  </div> -->
											<section id="MyContactDetails" class="tab-content hide">
												<div>
													<br/>
													<div  >
														<table class=" profilepage product-specbigstripes">
															<tr>
																<td width="18%" height="30" align="left" valign="middle" ><label>Mobile:</label></td>
																<td width="82%" align="left" >
																	<s:textfield name="mobile" id="mobile" class="input-control" type="text" value="%{user.mobile}" autocomplete="off" onkeypress="javascript:return isNumber (event)"></s:textfield>
																</td>
															</tr>
															<tr>
																<td height="30" align="left" valign="middle" ><label>Telephone No.:</label></td>
																<td align="left" >
																	<s:textfield name="telephoneNo" id="telephoneNo" class="input-control" type="text" value="%{user.telephoneNo}" autocomplete="off" onkeypress="javascript:return isNumber (event)"></s:textfield>
																</td>
															</tr>
															<tr>
																<td height="30" align="left" valign="middle" ><label>Address:</label></td>
																<td align="left" >
																	<s:textfield name="address" id="address" class="input-control" type="text" value="%{user.address}" autocomplete="off"></s:textfield>
																</td>
															</tr>
															<tr>
																<td height="30" align="left" valign="middle" ><label>City:</label></td>
																<td align="left" >
																	<s:textfield name="city" id="city" class="input-control" type="text" value="%{user.city}" autocomplete="off" onkeypress="return lettersSpaceOnly(event, this);"></s:textfield>
																</td>
															</tr>
															<tr>
																<td height="30" align="left" valign="middle" ><label>State:</label></td>
																<td align="left" >
																	<s:select list="@com.pay10.commons.util.States@values()" name="state" id="state" cssClass="input-control" value="defaultState" autocomplete="off" listKey="name" listValue="name"></s:select>
																</td>
															</tr>
															<tr>
																<td height="30" align="left" valign="middle" ><label>Country:</label></td>
																<td align="left" >
																	<s:select list="@com.pay10.commons.util.BinCountryMapperType@values()" name="country" id="country" cssClass="input-control" value="defaultCountry" autocomplete="off" listKey="name" listValue="name"></s:select>
																</td>
															</tr>
															<tr>
																<td height="30" align="left" valign="middle" ><label>Postal Code:</label></td>
																<td align="left" >
																	<s:textfield name="postalCode" id="postalCode" class="input-control"  type="text" value="%{user.postalCode}" autocomplete="off" onkeypress="javascript:return isNumber (event)"></s:textfield>
																</td>
															</tr>
															<tr>
																<td height="30" align="left"></td>
																<td align="left" style="float:left;">
																	<s:submit  value="Save" class="btn btn-success size"> </s:submit>
																</td>
															</tr>
														</table>
													</div>
												</div>
											</section>
											<section id="MyBankDetails" class="tab-content hide">
												<div>
													<br />
													<div  >
														<table class=" profilepage product-specbigstripes">
															<tr>
																<td width="18%" height="30" align="left" valign="middle" ><label>Bank Name:</label></td>
																<td width="82%" align="left">
																	<s:textfield name="bankName" class="input-control" id="bankName" type="text" value="%{user.bankName}" autocomplete="off" onkeypress="return lettersSpaceOnly(event, this);"></s:textfield>
																</td>
															</tr>
															<tr>
																<td height="30" align="left" valign="middle" ><label>IFSC Code:&nbsp;</label></td>
																<td align="left">
																	<s:textfield name="ifscCode" id="ifscCode" class="input-control" type="text" value="%{user.ifscCode}" autocomplete="off" onkeypress="return IsAlphaNumeric(event);"></s:textfield>
																</td>
															</tr>
															<tr>
																<td height="30" align="left" valign="middle" ><label>Acc Holder Name:&nbsp;&nbsp;</label></td>
																<td align="left">
																	<s:textfield name="accHolderName" id="accHolderName" class="input-control" type="text" value="%{user.accHolderName}" autocomplete="off" onkeypress="return lettersSpaceOnly(event, this);"></s:textfield>
																</td>
															</tr>
															<tr>
																<td height="30" align="left" valign="middle" ><label>Currency:&nbsp;&nbsp;</label></td>
																<td align="left">
																	<s:textfield name="currency" id="currency" type="text" class="input-control" value="%{user.currency}" autocomplete="off" onkeypress="return IsAlphaNumeric(event);"></s:textfield>
																</td>
															</tr>
															<tr>
																<td height="30" align="left" valign="middle" ><label>Branch Name:&nbsp;&nbsp;</label></td>
																<td align="left">
																	<s:textfield name="branchName" id="branchName" type="text" class="input-control" value="%{user.branchName}" autocomplete="off" onkeypress="return lettersSpaceOnly(event, this);"></s:textfield>
																</td>
															</tr>
															<tr>
																<td height="30" align="left" valign="middle" ><label>Pan Card:&nbsp;&nbsp;</label></td>
																<td align="left">
																	<s:textfield name="panCard" id="panCard" type="text" class="input-control" value="%{user.panCard}" autocomplete="off" onkeypress="return IsAlphaNumeric(event);"></s:textfield>
																</td>
															</tr>
															<tr>
																<td height="30" align="left" valign="middle" ><label>Account No.:&nbsp;&nbsp;</label></td>
																<td align="left">
																	<s:textfield name="accountNo" id="accountNo" class="input-control" type="text" value="%{user.accountNo}" autocomplete="off" onkeypress="javascript:return isNumber (event)"></s:textfield>
																</td>
															</tr>
															<tr>
																<td height="30" align="left"></td>
																<td align="left" style="float:left;">
																	<s:submit  value="Save" class="btn btn-success size"> </s:submit>
																</td>
															</tr>
														</table>
													</div>
												</div>
											</section>
											<section id="MyBusinessDetails" class="tab-content hide">
												<div>
													<br />
													<div>
														<table class=" profilepage product-specbigstripes">
															<tr>
																<td colspan="2" >
																	<s:submit  value="Save" class="btn btn-success size"> </s:submit>
																</td>
																<td width="58%"></td>
															</tr>
															<tr>
																<td width="32%" height="50" align="left" valign="middle" ><label >Organisation Type:&nbsp;&nbsp;</label></td>
																<td width="68%" align="left" class="text1">
																	<s:select headerKey="" headerValue="Select Title" cssClass="input-control" list="#{'Proprietship':'Proprietship','Individual':'Individual','Partnership':'Partnership','Private Limited':'Private Limited','Public Limited':'Public Limited','LLP':'LLP','NGO':'NGO','Educational Institutes':'Educational Institutes','Trust':'Trust','Society':'Society','Freelancer':'Freelancer'}"
																		name="organisationType" id="organisationType" value="%{user.organisationType}" autocomplete="off"/>
																</td>
															</tr>
															<tr>
																<td height="50" align="left" valign="middle" ><label>Website URL:&nbsp;&nbsp;</label></td>
																<td align="left" class="text1">
																	<s:textfield name="website" id="website" class="input-control" type="text" value="%{user.website}" autocomplete="off"></s:textfield>
																</td>
															</tr>
															<tr>
																<td height="50" align="left" valign="middle" ><label>Multicurrency Payments Required?:&nbsp;&nbsp;</label></td>
																<td align="left" class="text1">
																	<s:select headerKey=""
																		headerValue="Select" cssClass="input-control"
																		list="#{'YES':'YES','NO':'NO'}"
																		name="multiCurrency" id="multiCurrency" value="%{user.multiCurrency}" autocomplete="off"/>
																</td>
															</tr>
															<tr>
																<td height="50" align="left" valign="middle" ><label> Business Model:&nbsp;&nbsp;</label></td>
																<td align="left" valign="middle">
																	<s:textfield name="businessModel" class="input-control" id="businessModel" type="text" value="%{user.businessModel}" autocomplete="off"></s:textfield>
																	<span class="redsmalltext">Please give a brief explanation of your business model and future plans (Essential for startups)</span>
																</td>
															</tr>
															<tr>
																<td height="50" align="left" valign="middle" ><label>Operation Address:&nbsp;&nbsp;</label></td>
																<td align="left" class="text1">
																	<s:textfield name="operationAddress" class="input-control" id="operationAddress" type="text" value="%{user.operationAddress}" autocomplete="off"></s:textfield>
																</td>
															</tr>
															<tr>
																<td height="50" align="left" valign="middle" ><label>Operation Address State:&nbsp;&nbsp;</label></td>
																<td align="left" class="text1">
																	<s:select name="operationState" id="operationState" type="text" cssClass="input-control"  autocomplete="off" list="@com.pay10.commons.util.States@values()" value="defaultOperationState" listKey="name" listValue="name" ></s:select>
																</td>
															</tr>
															<tr>
																<td height="50" align="left" valign="middle" ><label>Operation Address City:&nbsp;&nbsp;</label><label></label></td>
																<td align="left" class="text1">
																	<s:textfield name="operationCity" id="operationCity" type="text" class="input-control" value="%{user.operationCity}" autocomplete="off" onkeypress="return lettersSpaceOnly(event, this);"></s:textfield>
																</td>
															</tr>
															<tr>
																<td height="50" align="left" valign="middle" ><label>Operation Address Pincode:&nbsp;&nbsp;</label><label></label></td>
																<td align="left" class="text1">
																	<s:textfield name="operationPostalCode" id="operationPostalCode" class="input-control" type="text" value="%{user.operationPostalCode}" autocomplete="off" onkeypress="javascript:return isNumber (event)"></s:textfield>
																</td>
															</tr>
															<tr>
																<td height="50" align="left" valign="middle" ><label>Date of Establishment</label>:<label>&nbsp;&nbsp;</label><label></label></td>
																<td align="left" class="text1">
																	<s:textfield name="dateOfEstablishment" id="dateOfEstablishment" class="input-control" type="text" value="%{user.dateOfEstablishment}" autocomplete="off"></s:textfield>
																</td>
															</tr>
															<tr>
																<td height="50" align="left" valign="top" ><label> CIN:&nbsp;&nbsp;</label></td>
																<td align="left" class="text1">
																	<s:textfield name="cin" id="cin" type="text" class="input-control" value="%{user.cin}" autocomplete="off"></s:textfield>
																	<span class="redsmalltext">Mandatory for Companies</span>
																</td>
															</tr>
															<tr>
																<td height="50" align="left" valign="top" ><label> PAN</label>:<label>&nbsp;&nbsp;</label><label></label></td>
																<td align="left" class="text1">
																	<s:textfield name="pan" id="pan" class="input-control" type="text" value="%{user.pan}" autocomplete="off" onkeypress="return IsAlphaNumeric(event);"></s:textfield>
																	<span class="redsmalltext">Mandatory for Companies</span>
																</td>
															</tr>
															<tr>
																<td height="50" align="left" valign="top" ><label>Name on PAN Card:&nbsp;&nbsp;</label></td>
																<td align="left">
																	<s:textfield name="panName" class="input-control" id="panName" type="text" value="%{user.panName}" autocomplete="off" onkeypress="return lettersSpaceOnly(event, this);"></s:textfield>
																	<span class="redsmalltext">Mandatory for Companies</span>
																</td>
															</tr>
															<tr>
																<td height="50" align="left" valign="middle" ><label>Expected number of transaction:</label></td>
																<td align="left" class="text1">
																	<s:textfield name="noOfTransactions" id="noOfTransactions" class="input-control" type="text" value="%{user.noOfTransactions}" autocomplete="off" onkeypress="javascript:return isNumber (event)"></s:textfield>
																</td>
															</tr>
															<tr>
																<td height="50" align="left" valign="middle" ><label>Expected amount of transaction:&nbsp;&nbsp;</label></td>
																<td align="left" class="text1">
																	<s:textfield name="amountOfTransactions" class="input-control" id="amountOfTransactions" type="text" value="%{user.amountOfTransactions}" autocomplete="off" onkeypress="javascript:return isNumber (event)"></s:textfield>
																</td>
															</tr>
															<tr>
																<td height="50" align="left" valign="middle" ><label>Disable/Enable Transaction Email:</label></td>
																<td align="left" class="text1">
																	<s:checkbox name="transactionEmailerFlag" value="%{user.transactionEmailerFlag}" autocomplete="off"/>
																</td>
															</tr>
															<tr>
																<td height="50" align="left" valign="middle" ><label>TransactionEmail:&nbsp;&nbsp;</label></td>
																<td align="left" class="text1">
																	<s:textfield name="transactionEmailId" class="input-control" id="transactionEmailId" type="text" value="%{user.transactionEmailId}" autocomplete="off" ></s:textfield>
																</td>
															</tr>
															<tr>
																<td height="50" align="left" valign="middle" ><label>Merchant Gst No:&nbsp;&nbsp;</label></td>
																<td align="left" class="text1">
																	<s:textfield id="merchantGstNo" type="text" class="input-control"
																		name="merchantGstNo"
																		value="%{user.merchantGstNo}"
																		OnKeypress="javascript:return isAlphaNumeric(event,this.value)" autocomplete="off" ></s:textfield>
																</td>
															</tr>
															<tr>
																<td valign="bottom" colspan="2" align="center">
																	<table class="table98 padding0">
																		<tr>
																			<td width="40%">
																				<s:submit  value="Save" class="btn btn-success size"> </s:submit>
																			</td>
																			<td width="58%"></td>
																		</tr>
																	</table>
														</table>
													</div>
												</div>
											</section>
											<section id="DocumentsUploads" class="tab-content hide"><br>
												<p style="color:#f44336">Note: Important Instruction please read carefully</p>
															<div   class="accordion md-accordion" id="accordionEx1" role="tablist" aria-multiselectable="true">
																<div id="sortable">
																<div class=" upload_instructions"   id="uploadPvtPubLtdOrg"><!--onclick="updateTabState(this)"-->
																	<div class="card-header" role="tab" id="headingTwo1">
																		<a class="collapsed" >
																		  <h5 class="mb-0" style="color:#496cb6" >
																			<strong>Document Name for Private Limited / Public Limited </strong> <i style="float: right; color: #496cb6;font-size: 30px;" data-toggle="collapse" data-parent="#accordionEx1" href="#collapseTwo1"
																			aria-expanded="false" aria-controls="collapseTwo1" class="fa fa-angle-down rotate-icon"></i>
																		  </h5>
																		</a>
																	  </div>
																	  <div id="collapseTwo1" class="collapse" role="tabpanel" >
												  <div class="card-body">
															<div class="row">
															<div class="col-md-12 col-xs-12">
																
																	<div class="cards">
																	  <div class="card-header card-header-icon card-header-info">
																		<div style="text-align: left; font-weight: bold;color:black">
																			<ul>
																				<li>File format should be .jpg, .png, .jpeg, .pdf</li>
																				<li>File size should be between 100KB - 2MB</li>
																				<li>File name length should be 5-50 characters</li>
																				<li>File name can contain alphabets, digits, round brackets, hyphen, underscore, dot and space</li>
																			</ul>
																		</div>
																		
																	  </div>
																	  <div class="card-body panel-body scrollD">
																		
																		<table class=" profilepage product-specbigstripes">
																			
																			<tr>
																				<td height="50" align="left" valign="middle" ><label>Articles Of Association (AOA)&nbsp;&nbsp;</label></td>
																				<td align="left" class="text1">
																					Signed by any one Authorised Signatory
																				</td>
																			</tr>
																			<tr>
																				<td height="50" align="left" valign="middle" ><label>Memorandum Of Association (MOA) &nbsp;&nbsp;</label></td>
																				<td align="left" class="text1">
																					Signed by any one Authorised Signatory
																				</td>
																			</tr>
																			<tr>
																				<td height="50" align="left" valign="middle" ><label> Board resolution (Sample Attached)&nbsp;&nbsp;</label></td>
																				<td align="left" valign="middle">
																					Signed by two Authorised Signatory
																				</td>
																			</tr>
																			<tr>
																				<td height="50" align="left" valign="middle" ><label>List of Directors from MCA (Sample Attached)&nbsp;&nbsp;</label></td>
																				<td align="left" class="text1">
																					Signed by any one Authorised Signatory</td>
																			</tr>
																			<tr>
																				<td height="50" align="left" valign="middle" ><label>Certification Of Incorporation :&nbsp;&nbsp;</label></td>
																				<td align="left" class="text1">
																					Signed by any one Authorised Signatory</td>
																			</tr>
																			<tr>
																				<td height="50" align="left" valign="middle" ><label>PAN of the Company. &nbsp;&nbsp;</label><label></label></td>
																				<td align="left" class="text1">
																					Signed by any one Authorised Signatory	</td>
																			</tr>
																			<tr>
																				<td height="50" align="left" valign="middle" ><label>GST Registration Certificate&nbsp;&nbsp;</label><label></label></td>
																				<td align="left" class="text1">
																					Signed by any one Authorised Signatory</td>
																			</tr>
																			<tr>
																				<td height="50" align="left" valign="middle" ><label>Undertaking of product/services to be sold (Sample Attached)</label>:<label>&nbsp;&nbsp;</label><label></label></td>
																				<td align="left" class="text1">
																					Signed by any one Authorised Signatory</td>
																			</tr>
																			<tr>
																				<td height="50" align="left" valign="top" ><label>Current Address proof of the Company (Bank Statement/Utility Bill/Rent Agreement)&nbsp;&nbsp;</label></td>
																				<td align="left" class="text1">
																					Signed by any one Authorised Signatory</td>
																			</tr>
																			<tr>
																				<td height="50" align="left" valign="top" ><label>Certificate of Commencement of Business (for public limited companies)</label>:<label>&nbsp;&nbsp;</label><label></label></td>
																				<td align="left" class="text1">
																					Signed by any one Authorised Signatory</td>
																			</tr>
																			
																			<tr>
																				<td height="50" align="left" valign="middle" ><label>Service Agreement with PG, Agreement shall be signed printed on stamp Value Rs. 300/-</label></td>
																				<td align="left" class="text1">
																					self attested
																				</td>
																			</tr>
																			<tr>
																				<td height="50" align="left" valign="middle" ><label>Cancellation Cheque (in which account fund to be settled)&nbsp;&nbsp;</label></td>
																				<td align="left" class="text1">
																					Signed by any one Authorised Signatory	</td>
																			</tr>
																			<tr>
																				<td height="50" align="left" valign="top" ><label>PAN card copy of Director's &nbsp;&nbsp;</label></td>
																				<td align="left">
																					Signed by any one Authorised Signatory</td>
																			</tr>
																			<tr>
																				<td height="50" align="left" valign="top" ><label>Address Proof of  Director's (Aadhar Card, Pan card, Passport/DL/Voter Card)&nbsp;&nbsp;</label></td>
																				<td align="left">
																					Signed by any one Authorised Signatory</td>
																			</tr>
																			
																		
																		
																			<tr>
																		
																				
																		</table>
																		
																	  </div>
																	</div>
																  
															
															</div> 
															</div>
															</div>
																	  </div> 	
																</div>
											
																<div class=" upload_instructions"  id="uploadParLlpOrg" ><!---->
											
																	<!-- Card header -->
																	<div class="card-header" role="tab" id="headingTwo2">
																	  <a class="collapsed" >
																		<h5 class="mb-0" style="color:#496cb6">
																		<strong>Document Name for Partnership Deed/LLP</strong> <i style="float: right; color: #496cb6;font-size: 30px;" data-toggle="collapse" data-parent="#accordionEx1" href="#collapseTwo21"
																		aria-expanded="false" aria-controls="collapseTwo21" class="fa fa-angle-down rotate-icon"></i>
																		</h5>
																	  </a>
																	</div>
																	<div id="collapseTwo21" class="collapse" role="tabpanel" >
												  <div class="card-body">
											<div class="row">
																	<div class="col-md-12 col-xs-12">
																		
																			<div class="cards">
																			  <div class="card-header card-header-icon card-header-info">
																				<div style="text-align: left; font-weight: bold;color:black">
																					<ul>
																						<li>File format should be .jpg, .png, .jpeg, .pdf</li>
																						<li>File size should be between 100KB - 2MB</li>
																						<li>File name length should be 5-50 characters</li>
																						<li>File name can contain alphabets, digits, round brackets, hyphen, underscore, dot and space</li>
																					</ul>
																				</div>
																				
																			  </div>
																			  <div class="card-body panel-body scrollD">
																				
																				<table class=" profilepage product-specbigstripes">
																			
																					<tr>
																						<td height="50" align="left" valign="middle" ><label>Partnership Deed / LLP Partnership Deed &nbsp;&nbsp;</label></td>
																						<td align="left" class="text1">
																							Signed by any one Authorised Signatory
																						</td>
																					</tr>
																					<tr>
																						<td height="50" align="left" valign="middle" ><label>Certification Of Incorporation &nbsp;&nbsp;</label></td>
																						<td align="left" class="text1">
																							
																						</td>
																					</tr>
																					<tr>
																						<td height="50" align="left" valign="middle" ><label>PAN of the Firm.&nbsp;&nbsp;</label></td>
																						<td align="left" valign="middle">
																							Signed by any one Authorised Signatory
																						</td>
																					</tr>
																					<tr>
																						<td height="50" align="left" valign="middle" ><label>GST Registration Certificate&nbsp;&nbsp;</label></td>
																						<td align="left" class="text1">
																							Signed by any one Authorised Signatory</td>
																					</tr>
																					<tr>
																						<td height="50" align="left" valign="middle" ><label>Undertaking of product/services to be sold (Sample Attached)&nbsp;&nbsp;</label></td>
																						<td align="left" class="text1">
																							Signed by any one Authorised Signatory</td>
																					</tr>
																					<tr>
																						<td height="50" align="left" valign="middle" ><label>Current Address proof of the Firm (Bank Statement/Utility Bill/Rent Agreement).&nbsp;&nbsp;</label><label></label></td>
																						<td align="left" class="text1">
																							Signed by any one Authorised Signatory	</td>
																					</tr>
																				
																					<tr>
																						<td height="50" align="left" valign="middle" ><label>Service Agreement with PG, Agreement shall be signed printed on stamp Value Rs. 300/-</label>:<label>&nbsp;&nbsp;</label><label></label></td>
																						<td align="left" class="text1">
																							Signed by any one Authorised Signatory</td>
																					</tr>
																					<tr>
																						<td height="50" align="left" valign="top" ><label>Cancellation Cheque (in which account fund to be settled)&nbsp;&nbsp;</label></td>
																						<td align="left" class="text1">
																							</td>
																					</tr>
																					<tr>
																						<td height="50" align="left" valign="middle" ><label>PAN Card copy of Partner's&nbsp;&nbsp;</label><label></label></td>
																						<td align="left" class="text1">
																							Self attested</td>
																					</tr>
																					<tr>
																						<td height="50" align="left" valign="middle" ><label>Address proof Copy of Partner(Aadhar Card, Pan card, Passport/DL/Voter Card)&nbsp;&nbsp;</label><label></label></td>
																						<td align="left" class="text1">
																							Self attested</td>
																					</tr>
																					
																				
																				
																					
																				
																				
																					<tr>
																				
																						
																				</table>
																			  </div>
																			</div>
																		  
																	
																	</div> 
																	</div> 
																	</div>
																	</div>
																</div>
																<div class=" upload_instructions"  id="uploadProIndOrg"><!---->
											
																	<!-- Card header -->
																	<div class="card-header" role="tab" id="headingTwo2">
																	  <a class="collapsed" >
																		<h5 class="mb-0" style="color:#496cb6">
																		  <strong>Document Name for Proprietorship / Individual</strong><i style="float: right; color: #496cb6;font-size: 30px;" data-toggle="collapse" data-parent="#accordionEx1" href="#collapseTwo25"
																		  aria-expanded="false" aria-controls="collapseTwo25" class="fa fa-angle-down rotate-icon"></i>
																		</h5>
																	  </a>
																	</div>
																	<div id="collapseTwo25" class="collapse" role="tabpanel" >
												  <div class="card-body">
															<div class="row">
																<div class="col-md-12 col-xs-12">
																	
																		<div class="cards">
																		  <div class="card-header card-header-icon card-header-rose">
																			
																			<div style="text-align: left; font-weight: bold;color:black">
																				<ul>
																					<li>File format should be .jpg, .png, .jpeg, .pdf</li>
																					<li>File size should be between 100KB - 2MB</li>
																					<li>File name length should be 5-50 characters</li>
																					<li>File name can contain alphabets, digits, round brackets, hyphen, underscore, dot and space</li>
																				</ul>
																			</div>
																		  </div>
																		  <div class="card-body panel-body scrollD">
																			
																			<table class=" profilepage product-specbigstripes">
																			
																				<tr>
																					<td height="50" align="left" valign="middle" ><label>Shop Establishment Certificate / MSME Registration Certificate/ Trade License etc&nbsp;&nbsp;</label></td>
																					<td align="left" class="text1">
																						Signed by Proprietor
																					</td>
																				</tr>
																				<tr>
																					<td height="50" align="left" valign="middle" ><label>GST Registration Certificate &nbsp;&nbsp;</label></td>
																					<td align="left" class="text1">
																						Signed by Proprietor
																					</td>
																				</tr>
																				<tr>
																					<td height="50" align="left" valign="middle" ><label>Undertaking of product/services to be sold (Sample Attached)&nbsp;&nbsp;</label></td>
																					<td align="left" valign="middle">
																						Signed by Proprietor
																					</td>
																				</tr>
																				<tr>
																					<td height="50" align="left" valign="middle" ><label>Current Address proof of bussiness Address (Bank Statement/Utility Bill/Rent Agreement)&nbsp;&nbsp;</label></td>
																					<td align="left" class="text1">
																						Signed by Proprietor</td>
																				</tr>
																				
																				<tr>
																					<td height="50" align="left" valign="middle" ><label>Service Agreement with PG, Agreement shall be signed printed on stamp Value Rs. 300/-&nbsp;&nbsp;</label><label></label></td>
																					<td align="left" class="text1">
																						Signed by Proprietor	</td>
																				</tr>
																				<tr>
																					<td height="50" align="left" valign="middle" ><label>Cancellation Cheque (in which account fund to be settled)&nbsp;&nbsp;</label><label></label></td>
																					<td align="left" class="text1">
																						Signed by Proprietor</td>
																				</tr>
																				<tr>
																					<td height="50" align="left" valign="middle" ><label>Last 3 Month's Current account Bank statement</label>:<label>&nbsp;&nbsp;</label><label></label></td>
																					<td align="left" class="text1">
																						Signed by Proprietor</td>
																				</tr>
																				<tr>
																					<td height="50" align="left" valign="middle" ><label>PAN card copy of  Proprietor &nbsp;&nbsp;</label></td>
																					<td align="left" class="text1">
																						Signed by Proprietor</td>
																				</tr>
																				<tr>
																					<td height="50" align="left" valign="middle" ><label>Address copy of Proprietor  (Aadhar Card,Passport/DL/Voter Card)&nbsp;&nbsp;</label></td>
																					<td align="left" class="text1">
																						Signed by Proprietor</td>
																				</tr>
																			
																				<tr>
																			
																					
																			</table>
																		  </div>
																		</div>
																	  
																
																</div>
															
															
														
																</div> 
																</div>
																</div>
															</div>
																<div class=" upload_instructions"  id="uploadTseigngoOrg"><!---->
											
																	<!-- Card header -->
																	<div class="card-header" role="tab" id="headingTwo2">
																	  <a class="collapsed" >
																		<h5 class="mb-0" style="color:#496cb6">
																		  <strong>Trust / Society / Education Institute / Government/ NGO </strong><i style="float: right; color: #496cb6;font-size: 30px;" data-toggle="collapse" data-parent="#accordionEx1" href="#collapseTwo22"
																		  aria-expanded="false" aria-controls="collapseTwo22" class="fa fa-angle-down rotate-icon"></i>
																		</h5>
																	  </a>
																	</div>
																	<div id="collapseTwo22" class="collapse" role="tabpanel" >
												  <div class="card-body">	
																<div class="row">
																	<div class="col-md-12 col-xs-12">
																		
																			<div class="cards">
																			  <div class="card-header card-header-icon card-header-warning">
																				<div style="text-align: left; font-weight: bold;color:black">
																					<ul>
																						<li>File format should be .jpg, .png, .jpeg, .pdf</li>
																				<li>File size should be between 100KB - 2MB</li>
																				<li>File name length should be 5-50 characters</li>
																				<li>File name can contain alphabets, digits, round brackets, hyphen, underscore, dot and space</li>
																					</ul>
																				</div>
																				
																			
																			  </div>
																			  <div class="card-body panel-body scrollD">
																				
																				<table class=" profilepage product-specbigstripes">
																			
																					<tr>
																						<td height="50" align="left" valign="middle" ><label>Offices -Utility  Bills or Rental Agreement if premises are on rent &nbsp;&nbsp;</label></td>
																						<td align="left" class="text1">
																							Company seal and signed by authorized signatories
																						</td>
																					</tr>
																					<tr>
																						<td height="50" align="left" valign="middle" ><label>Company PAN card  &nbsp;&nbsp;</label></td>
																						<td align="left" class="text1">
																							Company seal and signed by authorized signatories
																						</td>
																					</tr>
																					<tr>
																						<td height="50" align="left" valign="middle" ><label>Certificate issued under Companies Act or registration with Charity Commissioners office&nbsp;&nbsp;</label></td>
																						<td align="left" class="text1">
																							Company seal and signed by authorized signatories</td>
																					</tr>
																					<tr>
																						<td height="50" align="left" valign="middle" ><label>Trust Deed / Memorandum Of Understanding / Society Deed / Government Certificate etc&nbsp;&nbsp;</label><label></label></td>
																						<td align="left" class="text1">
																							Company seal and signed by authorized signatories	</td>
																					</tr>
																					<tr>
																						<td height="50" align="left" valign="middle" ><label>Resolution Deed on organization's letterhead&nbsp;&nbsp;</label><label></label></td>
																						<td align="left" class="text1">
																							Signed by two Authorised Signatory</td>
																					</tr>
																					<tr>
																						<td height="50" align="left" valign="middle" ><label>2 years ITR with audited balance sheet OR 1 year current account statement </label>:<label>&nbsp;&nbsp;</label><label></label></td>
																						<td align="left" class="text1">
																							Company seal and signed by authorized signatories</td>
																					</tr>
																					<tr>
																						<td height="50" align="left" valign="middle" ><label>In case of new establishment 6 month account statements of signing authority with one undertaking  </label>:<label>&nbsp;&nbsp;</label><label></label></td>
																						<td align="left" class="text1">
																							Self Attested and with company seal</td>
																					</tr>
																					<tr>
																						<td height="50" align="left" valign="middle" ><label>Bank Account Details on your company letter head</label>:<label>&nbsp;&nbsp;</label><label></label></td>
																						<td align="left" class="text1">
																							Company seal and signed by authorized signatories</td>
																					</tr>
																					<tr>
																						<td height="50" align="left" valign="middle" ><label>Voided check</label>:<label>&nbsp;&nbsp;</label><label></label></td>
																						<td align="left" class="text1">
																							</td>
																					</tr>
																					<tr>
																						<td height="50" align="left" valign="middle" ><label>Service Agreement with PG, Agreement shall be signed printed on stamp Value Rs. 300/- </label>:<label>&nbsp;&nbsp;</label><label></label></td>
																						<td align="left" class="text1">
																							Signed by any one Authorised Signatory</td>
																					</tr>
																					<tr>
																						<td height="50" align="left" valign="middle" ><label>Pan card copy of Trustee&nbsp;&nbsp;</label></td>
																						<td align="left" valign="middle">
																							Self Attested and with company seal
																						</td>
																					</tr>
																					<tr>
																						<td height="50" align="left" valign="middle" ><label>Address Proof copy of Trustee (Aadhar Card, Pan card, Passport/DL/Voter Card)&nbsp;&nbsp;</label></td>
																						<td align="left" class="text1">
																							Self Attested and with company seal</td>
																					</tr>
																					
																				
																					
																				
																					<tr>
																				
																						
																				</table>
																			  </div>
																			</div>
																		  
																	
																	</div> 
																	</div>
																	
											
																</div>
																</div>
																	</div></div>
											
																	<div class=" upload_instructions"  id="uploadFreelancersOrg"><!---->
											
																		<!-- Card header -->
																		<div class="card-header" role="tab" id="headingTwo2">
																		  <a class="collapsed" >
																			<h5 class="mb-0" style="color:#496cb6">
																			  <strong>Freelancers</strong> <i style="float: right; color: #496cb6;font-size: 30px;" class="fa fa-angle-down rotate-icon" data-toggle="collapse" data-parent="#accordionEx1" href="#collapseTwo23"
																			  aria-expanded="false" aria-controls="collapseTwo23"></i>
																			</h5>
																		  </a>
																		</div>
																		<div id="collapseTwo23" class="collapse" role="tabpanel" >
													  <div class="card-body">
																<div class="row">
																	<div class="col-md-12 col-xs-12">
																		
																		<div class="cards">
																		  <div class="card-header card-header-icon card-header-success">
																	
																			<div style="text-align: left; font-weight: bold;color:black">
																				<ul>
																					<li>File format should be .jpg, .png, .jpeg, .pdf</li>
																				<li>File size should be between 100KB - 2MB</li>
																				<li>File name length should be 5-50 characters</li>
																				<li>File name can contain alphabets, digits, round brackets, hyphen, underscore, dot and space</li>
																				</ul>
																			</div>
																		  </div>
																		  <div class="card-body panel-body scrollD">
																			
																			<table class=" profilepage product-specbigstripes">
																			
																				<tr>
																					<td height="50" align="left" valign="middle" ><label>Aadhar Card&nbsp;&nbsp;</label></td>
																					<td align="left" class="text1">
																						
																					</td>
																				</tr>
																				<tr>
																					<td height="50" align="left" valign="middle" ><label>Pan Card &nbsp;&nbsp;</label></td>
																					<td align="left" class="text1">
																						
																					</td>
																				</tr>
																				<tr>
																					<td height="50" align="left" valign="middle" ><label> Driving License/Passport&nbsp;&nbsp;</label></td>
																					<td align="left" valign="middle">
																						
																					</td>
																				</tr>
																				<tr>
																					<td height="50" align="left" valign="middle" ><label>Address Proof : Nothing old than 3 months - Utility Bill/Bank Statement/Insurance Cover&nbsp;&nbsp;</label></td>
																					<td align="left" class="text1">
																						</td>
																				</tr>
																				<tr>
																					<td height="50" align="left" valign="middle" ><label>Cancellation Cheque (in which account fund to be settled) :&nbsp;&nbsp;</label></td>
																					<td align="left" class="text1">
																						</td>
																				</tr>
																				<tr>
																					<td height="50" align="left" valign="middle" ><label>Service Agreement with PG, Agreement shall be signed printed on stamp Value Rs. 300/- &nbsp;&nbsp;</label><label></label></td>
																					<td align="left" class="text1">
																						Signed by any one Authorised Signatory	</td>
																				</tr>
																				
																			
																				<tr>
																			
																					
																			</table>
																		  </div>
																		</div>
																	  
																
																</div> 
																</div>
																</div>
																</div>
																</div>
																</div>
												<div>
													<br/>
													 <div>
														<input type="hidden" id="onBoardList"  value='<s:property value="%{user.onBoardDocListString}"/>'/>
														<input type="hidden" id="merchantOnBoardActivePage"value='<s:property value="%{activePage}"/>'/>
														<input type="hidden" id="prevOrgType" value='<s:property value="%{user.organisationType}"/>'/> 
														<div id="uploadedDocs" class="uploadDocsDiv"> 
															<div class="uploadDocsHeader" style="display:flex">
    															<div style="width: 100%;font-size: 20px;font-weight: bold;color:black">
    																<p>Organisation type : <s:property value="%{user.organisationType}"/></p>
    															</div>
																
															</div>
															<div class="uploadDocsBody">
																<div class="uploadDocsTables" id="pvtPubLtdOrg"> <!-- For private and public limited -->
																	<div class="udtDiv">
																		<table class="udtTable">
																			<tr>
																				<td class="uploadDocTableTd">Articles Of Association (AOA)</td>
																				<td class="uploadDocTableTd">
																					<input type="text" readonly="readonly" class="merchantDocName" id="pvtltdAOA">
																					<label for="merchantOnboardFileUpload" class="material-icons fileUploadLabel"  doctype="pvtltdAOA" style="cursor: pointer;">
																						<i class="fa fa-upload" id="materialSendIconUplaod" aria-hidden="true"></i>
																					</label>
																				</td>
																			</tr>
																			
																			<tr>
																				<td class="uploadDocTableTd">Memorandum Of Association (MOA)</td>
																				<td class="uploadDocTableTd">
																					<input type="text" readonly="readonly" class="merchantDocName" id="pvtltdMOA">
																					<label for="merchantOnboardFileUpload" class="material-icons fileUploadLabel"  doctype="pvtltdMOA" style="cursor: pointer;">
																						<i class="fa fa-upload" id="materialSendIconUplaod" aria-hidden="true"></i>
																					</label>
																				</td>
																			</tr>
																			
																			<tr>
																				<td class="uploadDocTableTd">Board resolution (Sample Attached)</td>
																				<td class="uploadDocTableTd">
																					<input type="text" readonly="readonly" class="merchantDocName" id="pvtltdBR">
																					<label for="merchantOnboardFileUpload" class="material-icons fileUploadLabel" class="material-icons fileUploadLabel" doctype="pvtltdBR" style="cursor: pointer;">
																						<i class="fa fa-upload" id="materialSendIconUplaod" aria-hidden="true"></i>
																					</label>
																				</td>
																			</tr>
																			
																			<tr>
																				<td class="uploadDocTableTd">List of Directors from MCA (Sample Attached)</td>
																				<td class="uploadDocTableTd">
																					<input type="text" readonly="readonly" class="merchantDocName" id="pvtltdLDMCA">
																					<label for="merchantOnboardFileUpload" class="material-icons fileUploadLabel"  doctype="pvtltdLDMCA" style="cursor: pointer;">

																						<i class="fa fa-upload" id="materialSendIconUplaod" aria-hidden="true"></i>
																					</label>
																				</td>
																			</tr>
																			
																			<tr>
																				<td class="uploadDocTableTd">Certification Of Incorporation</td>
																				<td class="uploadDocTableTd">
																					<input type="text" readonly="readonly" class="merchantDocName" id="pvtltdCOI">
																					<label for="merchantOnboardFileUpload" class="material-icons fileUploadLabel"  doctype="pvtltdCOI" style="cursor: pointer;">

																						<i class="fa fa-upload" id="materialSendIconUplaod" aria-hidden="true"></i>
																					</label>
																				</td>
																			</tr>
																			
																			<tr>
																				<td class="uploadDocTableTd">PAN of the Company.</td>
																				<td class="uploadDocTableTd">
																					<input type="text" readonly="readonly" class="merchantDocName" id="pvtltdPANOC">
																					<label for="merchantOnboardFileUpload" class="material-icons fileUploadLabel"  doctype="pvtltdPANOC" style="cursor: pointer;">

																						<i class="fa fa-upload" id="materialSendIconUplaod" aria-hidden="true"></i>
																					</label>
																				</td>
																			</tr>
																			
																			<tr>
																				<td class="uploadDocTableTd">GST Registration Certificate</td>
																				<td class="uploadDocTableTd">
																					<input type="text" readonly="readonly" class="merchantDocName" id="pvtltdGSTRC">
																					<label for="merchantOnboardFileUpload" class="material-icons fileUploadLabel"  doctype="pvtltdGSTRC" style="cursor: pointer;">

																						<i class="fa fa-upload" id="materialSendIconUplaod" aria-hidden="true"></i>
																					</label>
																				</td>
																			</tr>
																			
																			<tr>
																				<td class="uploadDocTableTd">Undertaking of product/services to be sold (Sample Attached)</td>
																				<td class="uploadDocTableTd">
																					<input type="text" readonly="readonly" class="merchantDocName" id="pvtltdUPSS">
																					<label for="merchantOnboardFileUpload" class="material-icons fileUploadLabel"  doctype="pvtltdUPSS" style="cursor: pointer;">

																						<i class="fa fa-upload" id="materialSendIconUplaod" aria-hidden="true"></i>
																					</label>
																				</td>
																			</tr>
																			
																			<tr>
																				<td class="uploadDocTableTd">Current Address proof of the Company (Bank Statement/Utility Bill/Rent Agreement)</td>
																				<td class="uploadDocTableTd">
																					<input type="text" readonly="readonly" class="merchantDocName" id="pvtltdCAPOC">
																					<label for="merchantOnboardFileUpload" class="material-icons fileUploadLabel"  doctype="pvtltdCAPOC" style="cursor: pointer;">

																						<i class="fa fa-upload" id="materialSendIconUplaod" aria-hidden="true"></i>
																					</label>
																				</td>
																			</tr>
																			
																			<tr>
																				<td class="uploadDocTableTd">Certificate of Commencement of Business (for public limited companies)</td>
																				<td class="uploadDocTableTd">
																					<input type="text" readonly="readonly" class="merchantDocName" id="pvtltdCCOB">
																					<label for="merchantOnboardFileUpload" class="material-icons fileUploadLabel"  doctype="pvtltdCCOB" style="cursor: pointer;">

																						<i class="fa fa-upload" id="materialSendIconUplaod" aria-hidden="true"></i>
																					</label>
																				</td>
																			</tr>
																			
																			<tr>
																				<td class="uploadDocTableTd">Service Agreement with PG</td>
																				<td class="uploadDocTableTd">
																					<input type="text" readonly="readonly" class="merchantDocName" id="pvtltdSADINERO">
																					<label for="merchantOnboardFileUpload" class="material-icons fileUploadLabel"  doctype="pvtltdSADINERO" style="cursor: pointer;">

																						<i class="fa fa-upload" id="materialSendIconUplaod" aria-hidden="true"></i>
																					</label>
																				</td>
																			</tr>
																			
																			<tr>
																				<td class="uploadDocTableTd">Cancellation Cheque (in which account fund to be settled)</td>
																				<td class="uploadDocTableTd">
																					<input type="text" readonly="readonly" class="merchantDocName" id="pvtltdCANCHQE">
																					<label for="merchantOnboardFileUpload" class="material-icons fileUploadLabel"  doctype="pvtltdCANCHQE" style="cursor: pointer;">

																						<i class="fa fa-upload" id="materialSendIconUplaod" aria-hidden="true"></i>
																					</label>
																				</td>
																			</tr>
																			
																			<tr>
																				<td colspan="2" class="uploadDocTableTd"><button type="button" class="btn btn-success" id="pvtPubLtdOrgDirDocRows" value="add rows" orgKey = "pvtPubLtdOrg" count='0'>+</button>KYC of all Directors (Aadhar Card, Pan card, Passport/DL/Voter Card)</td>
																				<!-- <td class="uploadDocTableTd">
																					<button type="button" class="btn btn-success" style="float: right;" id="pvtPubLtdOrgDirDocRows" value="add rows" orgKey = "pvtPubLtdOrg" count='0'>+ Add Rows</button>
																				</td> -->
																			</tr>
																				
																			
																		</table>
																	</div>
																</div>
																<div class="uploadDocsTables" id="parLlpOrg"> <!-- Partnership deed -->
																	<div class="udtDiv">
																		<table class="udtTable">
																			<tr>
																				<td class="uploadDocTableTd">Partnership Deed</td>
																				<td class="uploadDocTableTd">
																					<input type="text" readonly="readonly" class="merchantDocName" id="pardeePD">
																					<label for="merchantOnboardFileUpload" class="material-icons fileUploadLabel"  doctype="pardeePD" style="cursor: pointer;">

																						<i class="fa fa-upload" id="materialSendIconUplaod" aria-hidden="true"></i>
																					</label>
																				</td>
																			</tr>
																			
																			<tr>
																				<td class="uploadDocTableTd">Certification Of Incorporation </td>
																				<td class="uploadDocTableTd">
																					<input type="text" readonly="readonly" class="merchantDocName" id="pardeeCOI">
																					<label for="merchantOnboardFileUpload" class="material-icons fileUploadLabel"  doctype="pardeeCOI" style="cursor: pointer;">

																						<i class="fa fa-upload" id="materialSendIconUplaod" aria-hidden="true"></i>
																					</label>
																				</td>
																			</tr>
																			
																			<tr>
																				<td class="uploadDocTableTd">PAN of the Firm</td>
																				<td class="uploadDocTableTd">
																					<input type="text" readonly="readonly" class="merchantDocName" id="pardeePANOF">
																					<label for="merchantOnboardFileUpload" class="material-icons fileUploadLabel"  doctype="pardeePANOF" style="cursor: pointer;">

																						<i class="fa fa-upload" id="materialSendIconUplaod" aria-hidden="true"></i>
																					</label>
																				</td>
																			</tr>
																			
																			<tr>
																				<td class="uploadDocTableTd">GST Registration Certificate</td>
																				<td class="uploadDocTableTd">
																					<input type="text" readonly="readonly" class="merchantDocName" id="pardeeGSTRC">
																					<label for="merchantOnboardFileUpload" class="material-icons fileUploadLabel"  doctype="pardeeGSTRC" style="cursor: pointer;">

																						<i class="fa fa-upload" id="materialSendIconUplaod" aria-hidden="true"></i>
																					</label>
																				</td>
																			</tr>
																			
																			<tr>
																				<td class="uploadDocTableTd">Undertaking of product/services to be sold (Sample Attached)</td>
																				<td class="uploadDocTableTd">
																					<input type="text" readonly="readonly" class="merchantDocName" id="pardeeUPSS">
																					<label for="merchantOnboardFileUpload" class="material-icons fileUploadLabel"  doctype="pardeeUPSS" style="cursor: pointer;">

																						<i class="fa fa-upload" id="materialSendIconUplaod" aria-hidden="true"></i>
																					</label>
																				</td>
																			</tr>
																			
																			<tr>
																				<td class="uploadDocTableTd">Current Address proof of the Company (Bank Statement/Utility Bill/Rent Agreement)</td>
																				<td class="uploadDocTableTd">
																					<input type="text" readonly="readonly" class="merchantDocName" id="pardeeCAPOC">
																					<label for="merchantOnboardFileUpload" class="material-icons fileUploadLabel"  doctype="pardeeCAPOC" style="cursor: pointer;">

																						<i class="fa fa-upload" id="materialSendIconUplaod" aria-hidden="true"></i>
																					</label>
																				</td>
																			</tr>
																			
																			<tr>
																				<td class="uploadDocTableTd">Service Agreement with PG</td>
																				<td class="uploadDocTableTd">
																					<input type="text" readonly="readonly" class="merchantDocName" id="pardeeSADINERO">
																					<label for="merchantOnboardFileUpload" class="material-icons fileUploadLabel"  doctype="pardeeSADINERO" style="cursor: pointer;">

																						<i class="fa fa-upload" id="materialSendIconUplaod" aria-hidden="true"></i>
																					</label>
																				</td>
																			</tr>
																			
																			<tr>
																				<td class="uploadDocTableTd">Cancellation Cheque (in which account fund to be settled)</td>
																				<td class="uploadDocTableTd">
																					<input type="text" readonly="readonly" class="merchantDocName" id="pardeeCANCHQE">
																					<label for="merchantOnboardFileUpload" class="material-icons fileUploadLabel"  doctype="pardeeCANCHQE" style="cursor: pointer;">

																						<i class="fa fa-upload" id="materialSendIconUplaod" aria-hidden="true"></i>
																					</label>
																				</td>
																			</tr>
																			
																			<tr>
																				<td colspan="2" class="uploadDocTableTd"><button type="button" id="parLlpOrgDirDocRows"  class="btn btn-success" value="add rows" orgKey = "parLlpOrg" count='0'>+</button>KYC of all Partners (Aadhar Card, Pan card, Passport/DL/Voter Card)</td>
																				<!-- <td class="uploadDocTableTd">
																					
																				</td> -->
																			</tr>
																			
																		</table>
																	</div>
																</div>
																<div class="uploadDocsTables" id="proIndOrg"> <!-- Proprietorship and individual-->
																	<div class="udtDiv">
																		<table class="udtTable">
																			<tr>
																				<td class="uploadDocTableTd">Shop Establishment Certificate / MSME Registration Certificate/ Trade License etc</td>
																				<td class="uploadDocTableTd">
																					<input type="text" readonly="readonly" class="merchantDocName" id="proshpSEC_MSME_TL">
																					<label for="merchantOnboardFileUpload" class="material-icons fileUploadLabel"  doctype="proshpSEC_MSME_TL" style="cursor: pointer;">

																						<i class="fa fa-upload" id="materialSendIconUplaod" aria-hidden="true"></i>
																					</label>
																				</td>
																			</tr>
																			<tr>
																				<td class="uploadDocTableTd">GST Registration Certificate</td>
																				<td class="uploadDocTableTd">
																					<input type="text" readonly="readonly" class="merchantDocName" id="proshpGSTRC">
																					<label for="merchantOnboardFileUpload" class="material-icons fileUploadLabel"  doctype="proshpGSTRC" style="cursor: pointer;">

																						<i class="fa fa-upload" id="materialSendIconUplaod" aria-hidden="true"></i>
																					</label>
																				</td>
																			</tr>
																			<tr>
																				<td class="uploadDocTableTd">Undertaking of product/services to be sold (Sample Attached)</td>
																				<td class="uploadDocTableTd">
																					<input type="text" readonly="readonly" class="merchantDocName" id="proshpUPSS">
																					<label for="merchantOnboardFileUpload" class="material-icons fileUploadLabel"  doctype="proshpUPSS" style="cursor: pointer;">

																						<i class="fa fa-upload" id="materialSendIconUplaod" aria-hidden="true"></i>
																					</label>
																				</td>
																			</tr>
																			<tr>
																				<td class="uploadDocTableTd">Current Address proof of bussiness Address (Bank Statement/Utility Bill/Rent Agreement)</td>
																				<td class="uploadDocTableTd">
																					<input type="text" readonly="readonly" class="merchantDocName" id="proshpCAPBA">
																					<label for="merchantOnboardFileUpload" class="material-icons fileUploadLabel"  doctype="proshpCAPBA" style="cursor: pointer;">

																						<i class="fa fa-upload" id="materialSendIconUplaod" aria-hidden="true"></i>
																					</label>
																				</td>
																			</tr>

																			<tr>
																				<td class="uploadDocTableTd">Service Agreement with PG</td>
																				<td class="uploadDocTableTd">
																					<input type="text" readonly="readonly" class="merchantDocName" id="proshpSADINERO">
																					<label for="merchantOnboardFileUpload" class="material-icons fileUploadLabel"  doctype="proshpSADINERO" style="cursor: pointer;">

																						<i class="fa fa-upload" id="materialSendIconUplaod" aria-hidden="true"></i>
																					</label>
																				</td>
																			</tr>
																			<tr>
																				<td class="uploadDocTableTd">Cancellation Cheque (in which account fund to be settled)</td>
																				<td class="uploadDocTableTd">
																					<input type="text" readonly="readonly" class="merchantDocName" id="proshpCANCHQE">
																					<label for="merchantOnboardFileUpload" class="material-icons fileUploadLabel"  doctype="proshpCANCHQE" style="cursor: pointer;">

																						<i class="fa fa-upload" id="materialSendIconUplaod" aria-hidden="true"></i>
																					</label>
																				</td>
																			</tr>
																			<tr>
																				<td class="uploadDocTableTd">Last 3 Month's Current account Bank statement</td>
																				<td class="uploadDocTableTd">
																					<input type="text" readonly="readonly" class="merchantDocName" id="proshpCABS">
																					<label for="merchantOnboardFileUpload" class="material-icons fileUploadLabel"  doctype="proshpCABS" style="cursor: pointer;">

																						<i class="fa fa-upload" id="materialSendIconUplaod" aria-hidden="true"></i>
																					</label>
																				</td>
																			</tr>
																			
																			<tr>
																				<td colspan="2" class="uploadDocTableTd"><button type="button" id="proIndOrgDirDocRows"  class="btn btn-success" value="add rows" orgKey = "proIndOrg" count='0'>+</button>KYC of all Proprietor (Aadhar Card, Pan card, Passport/DL/Voter Card)</td>
																				<!-- <td class="uploadDocTableTd">
																					
																				</td> -->
																			</tr>
																			
																		</table>
																	</div>
																</div>
																<div class="uploadDocsTables" id="tseigngoOrg"> <!-- Trust / Society / Education Institute / Government -->
																	<div class="udtDiv">
																		<table class="udtTable ">
<!-- 																			<tr>
																				<td class="uploadDocTableTd">PAN card copy of all the Trustees</td>
																				<td class="uploadDocTableTd">
																					<input type="text" readonly="readonly" class="merchantDocName" id="tseigPANCT">
																					<label for="merchantOnboardFileUpload" class="material-icons fileUploadLabel"  doctype="tseigPANCT" style="cursor: pointer;">

																						<i class="fa fa-upload" id="materialSendIconUplaod" aria-hidden="true"></i>
																					</label>
																				</td>
																			</tr>
 -->																			
																			<!-- <tr>
																				<td class="uploadDocTableTd">Passport Copy with address page OR Driving License OR Election Card OR Aadhar Card of all the Trustees</td>
																				<td class="uploadDocTableTd">
																					<input type="text" readonly="readonly" class="merchantDocName" id="tseigPCDLECACOT">
																					<label for="merchantOnboardFileUpload" class="material-icons fileUploadLabel"  doctype="tseigPCDLECACOT" style="cursor: pointer;">

																						<i class="fa fa-upload" id="materialSendIconUplaod" aria-hidden="true"></i>
																					</label>
																				</td>
																			</tr> -->
																			
																			<tr>
																				<td class="uploadDocTableTd">Offices -Utility  Bills or Rental Agreement if premises are on rent</td>
																				<td class="uploadDocTableTd">
																					<input type="text" readonly="readonly" class="merchantDocName" id="tseigOFCUBRA">
																					<label for="merchantOnboardFileUpload" class="material-icons fileUploadLabel"  doctype="tseigOFCUBRA" style="cursor: pointer;">

																						<i class="fa fa-upload" id="materialSendIconUplaod" aria-hidden="true"></i>
																					</label>
																				</td>
																			</tr>
																			
																			<tr>
																				<td class="uploadDocTableTd">Company PAN card</td>
																				<td class="uploadDocTableTd">
																					<input type="text" readonly="readonly" class="merchantDocName" id="tseigCOMPAN">
																					<label for="merchantOnboardFileUpload" class="material-icons fileUploadLabel"  doctype="tseigCOMPAN" style="cursor: pointer;">

																						<i class="fa fa-upload" id="materialSendIconUplaod" aria-hidden="true"></i>
																					</label>
																				</td>
																			</tr>
																			
																			<tr>
																				<td class="uploadDocTableTd">Certificate issued under Companies Act or registration with Charity Commissioners office</td>
																				<td class="uploadDocTableTd">
																					<input type="text" readonly="readonly" class="merchantDocName" id="tseigCICARCCO">
																					<label for="merchantOnboardFileUpload" class="material-icons fileUploadLabel"  doctype="tseigCICARCCO" style="cursor: pointer;">

																						<i class="fa fa-upload" id="materialSendIconUplaod" aria-hidden="true"></i>
																					</label>
																				</td>
																			</tr>
																			
																			<tr>
																				<td class="uploadDocTableTd">Trust Deed / Memorandum Of Understanding / Society Deed / Government Certificate etc</td>
																				<td class="uploadDocTableTd">
																					<input type="text" readonly="readonly" class="merchantDocName" id="tseigTDMOUSDGC">
																					<label for="merchantOnboardFileUpload" class="material-icons fileUploadLabel"  doctype="tseigTDMOUSDGC" style="cursor: pointer;">

																						<i class="fa fa-upload" id="materialSendIconUplaod" aria-hidden="true"></i>
																					</label>
																				</td>
																			</tr>
																			
																			<tr>
																				<td class="uploadDocTableTd">Resolution Deed on organization's letterhead</td>
																				<td class="uploadDocTableTd">
																					<input type="text" readonly="readonly" class="merchantDocName" id="tseigRDOL">
																					<label for="merchantOnboardFileUpload" class="material-icons fileUploadLabel"  doctype="tseigRDOL" style="cursor: pointer;">

																						<i class="fa fa-upload" id="materialSendIconUplaod" aria-hidden="true"></i>
																					</label>
																				</td>
																			</tr>
																			
																			<tr>
																				<td class="uploadDocTableTd">2 years ITR with audited balance sheet OR 1 year current account statement</td>
																				<td class="uploadDocTableTd">
																					<input type="text" readonly="readonly" class="merchantDocName" id="tseig2YRITR">
																					<label for="merchantOnboardFileUpload" class="material-icons fileUploadLabel"  doctype="tseig2YRITR" style="cursor: pointer;">

																						<i class="fa fa-upload" id="materialSendIconUplaod" aria-hidden="true"></i>
																					</label>
																				</td>
																			</tr>
																			
																			<tr>
																				<td class="uploadDocTableTd">In case of new establishment 6 month account statements of signing authority with one undertaking</td>
																				<td class="uploadDocTableTd">
																					<input type="text" readonly="readonly" class="merchantDocName" id="tseigASOU">
																					<label for="merchantOnboardFileUpload" class="material-icons fileUploadLabel"  doctype="tseigASOU" style="cursor: pointer;">

																						<i class="fa fa-upload" id="materialSendIconUplaod" aria-hidden="true"></i>
																					</label>
																				</td>
																			</tr>
																			
																			<tr>
																				<td class="uploadDocTableTd">Bank Account Details on your company letter head</td>
																				<td class="uploadDocTableTd">
																					<input type="text" readonly="readonly" class="merchantDocName" id="tseigBADCLH">
																					<label for="merchantOnboardFileUpload" class="material-icons fileUploadLabel"  doctype="tseigBADCLH" style="cursor: pointer;">

																						<i class="fa fa-upload" id="materialSendIconUplaod" aria-hidden="true"></i>
																					</label>
																				</td>
																			</tr>
																			
																			<tr>
																				<td class="uploadDocTableTd">Voided check</td>
																				<td class="uploadDocTableTd">
																					<input type="text" readonly="readonly" class="merchantDocName" id="tseigVOIDCHQE">
																					<label for="merchantOnboardFileUpload" class="material-icons fileUploadLabel"  doctype="tseigVOIDCHQE" style="cursor: pointer;">

																						<i class="fa fa-upload" id="materialSendIconUplaod" aria-hidden="true"></i>
																					</label>
																				</td>
																			</tr>
																			
																			<tr>
																				<td class="uploadDocTableTd">Service Agreement with PG</td>
																				<td class="uploadDocTableTd">
																					<input type="text" readonly="readonly" class="merchantDocName" id="tseigSADINERO">
																					<label for="merchantOnboardFileUpload" class="material-icons fileUploadLabel"  doctype="tseigSADINERO" style="cursor: pointer;">

																						<i class="fa fa-upload" id="materialSendIconUplaod" aria-hidden="true"></i>
																					</label>
																				</td>
																			</tr>
																			
																			<tr>
																				<td colspan="2" class="uploadDocTableTd"><button type="button"  id="tseigngoOrgDirDocRows" class="btn btn-success" value="add rows" orgKey = "tseigngoOrg" count='0'>+</button>KYC of all Trustees (Aadhar Card, Pan card, Passport/DL/Voter Card)</td>
																				<!-- <td class="uploadDocTableTd">
																					
																				</td> -->
																			</tr>
																			
																		</table>
																	</div>
																</div>
																<div class="uploadDocsTables" id="freelancersOrg"> <!-- Freelancers -->
																	<div class="udtDiv">
																		<table class="udtTable">
																			<tr>
																				<td class="uploadDocTableTd">Aadhar Card</td>
																				<td class="uploadDocTableTd">
																					<input type="text" readonly="readonly" class="merchantDocName" id="frelanAC">
																					<label for="merchantOnboardFileUpload" class="material-icons fileUploadLabel"  doctype="frelanAC" style="cursor: pointer;">

																						<i class="fa fa-upload" id="materialSendIconUplaod" aria-hidden="true"></i>
																					</label>
																				</td>
																			</tr>
																			
																			<tr>
																				<td class="uploadDocTableTd">Pan Card</td>
																				<td class="uploadDocTableTd">
																					<input type="text" readonly="readonly" class="merchantDocName" id="frelanPC">
																					<label for="merchantOnboardFileUpload" class="material-icons fileUploadLabel"  doctype="frelanPC" style="cursor: pointer;">

																						<i class="fa fa-upload" id="materialSendIconUplaod" aria-hidden="true"></i>
																					</label>
																				</td>
																			</tr>
																			
																			<tr>
																				<td class="uploadDocTableTd">Driving License/Passport</td>
																				<td class="uploadDocTableTd">
																					<input type="text" readonly="readonly" class="merchantDocName" id="frelanDLORPASS">
																					<label for="merchantOnboardFileUpload" class="material-icons fileUploadLabel"  doctype="frelanDLORPASS" style="cursor: pointer;">

																						<i class="fa fa-upload" id="materialSendIconUplaod" aria-hidden="true"></i>
																					</label>
																				</td>
																			</tr>
																			
																			<tr>
																				<td class="uploadDocTableTd">Address Proof : Nothing old than 3 months - Utility Bill/Bank Statement/Insurance Cover</td>
																				<td class="uploadDocTableTd">
																					<input type="text" readonly="readonly" class="merchantDocName" id="frelanAP">
																					<label for="merchantOnboardFileUpload" class="material-icons fileUploadLabel"  doctype="frelanAP" style="cursor: pointer;">

																						<i class="fa fa-upload" id="materialSendIconUplaod" aria-hidden="true"></i>
																					</label>
																				</td>
																			</tr>
																			
																			<tr>
																				<td class="uploadDocTableTd">Cancellation Cheque (in which account fund to be settled)</td>
																				<td class="uploadDocTableTd">
																					<input type="text" readonly="readonly" class="merchantDocName" id="frelanCANCHQE">
																					<label for="merchantOnboardFileUpload" class="material-icons fileUploadLabel"  doctype="frelanCANCHQE" style="cursor: pointer;">

																						<i class="fa fa-upload" id="materialSendIconUplaod" aria-hidden="true"></i>
																					</label>
																				</td>
																			</tr>
																			
																			<tr>
																				<td class="uploadDocTableTd">Service Agreement with PG</td>
																				<td class="uploadDocTableTd">
																					<input type="text" readonly="readonly" class="merchantDocName" id="frelanSADINERO">
																					<label for="merchantOnboardFileUpload" class="material-icons fileUploadLabel"  doctype="frelanSADINERO" style="cursor: pointer;">

																						<i class="fa fa-upload" id="materialSendIconUplaod" aria-hidden="true"></i>
																					</label>
																				</td>
																			</tr>
																			
																			
																		</table>
																	</div>
																</div>
															</div>
															<div class="uploadDocsFooterDiv">
																
																																
																<div class="responseMsg">
																	<s:property value="%{responseMsg}" />
																</div>
															</div>

															</div>
															</div>
														</div>
													</div>
												</div>
											</section>
											<s:hidden name="token" value="%{#session.customToken}"></s:hidden>
										</s:form>
										<form id="merchantDocUploadForm" name="merchantDocUploadForm"  method="post" action="newMerchantFileUpload" enctype="multipart/form-data">
											<input name="fileUpload"  id=merchantOnboardFileUpload type="file" accept="*" style="opacity: 0;pointer-events: none;">
											<input name="organisationtype" id="orgType" type="text" value="" style="opacity: 0;pointer-events: none;">
											<input name="fileNameKey" id="fileNameKey" type="text" value="" style="opacity: 0;pointer-events: none;">
											<input name="fileDescription" id="fileDescription" type="text" value="" style="opacity: 0;pointer-events: none;">
											<s:hidden name="token" value="%{#session.customToken}"></s:hidden>
											<input style="display: none;" type="submit"  value="Save" class="btn btn-success size"/>  
										</form>
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
				</div>
				
				<%@ include file="/jsp/footer.jsp"%>
			</div>
		</div>
			</div>
			<div class="clear"></div>
			
		</div>
		<script type="text/javascript"> jQuery(document).ready(function ($) {  $('#tabs').tab(); });</script>
		<script src="../js/bootstrap.min.js"></script>
		<script>$(document).on('change', '.btn-file :file', function() {
			var input = $(this),
			    numFiles = input.get(0).files ? input.get(0).files.length : 1,
			    label = input.val().replace(/\\/g, '/').replace(/.*\//, '');
			input.trigger('fileselect', [numFiles, label]);
			});
			
			$(document).ready( function() {
			  $('.btn-file :file').on('fileselect', function(event, numFiles, label) {
			      
			      var input = $(this).parents('.input-group').find(':text'),
			          log = numFiles > 1 ? numFiles + ' files selected' : label;
			      
			      if( input.length ) {
			          input.val(log);
			      } else {
			          if( log ) alert(log);
			      }
			      
			  });
			});
		</script>
		<script type="text/javascript">
			var specialKeys = new Array();
			specialKeys.push(8); //Backspace
			specialKeys.push(9); //Tab
			specialKeys.push(46); //Delete
			specialKeys.push(36); //Home
			specialKeys.push(35); //End
			specialKeys.push(37); //Left
			specialKeys.push(39); //Right
			function IsAlphaNumeric(e) {
			    var keyCode = e.keyCode == 0 ? e.charCode : e.keyCode;
			    var ret = ((keyCode >= 48 && keyCode <= 57) || (keyCode >= 65 && keyCode <= 90) || (keyCode >= 97 && keyCode <= 122) || (specialKeys.indexOf(e.keyCode) != -1 && e.charCode != e.keyCode));
			    return ret;
			}
		</script>
			<!-- <script>
				var currentUrl = window.location.href.split('/');
				$('#MenuUl > li').removeClass('active');
				 if (currentUrl.includes("home")|| currentUrl.includes("passwordChangeSignUp")) {
					$('#myAccount').addClass('show');
					$('#a-myAccount').attr('aria-expanded', true);
					if (currentUrl.includes("home")) {
						$('#home').addClass('active');
					} else if (currentUrl.includes("home")) {
						$('#passwordChangeSignUp').addClass('active');
					} else if (currentUrl.includes("passwordChangeSignUp")) {
						$('#passwordChangeSignUp').addClass('active');
					}
				} 
			</script>
		 -->
	</body>
</html>