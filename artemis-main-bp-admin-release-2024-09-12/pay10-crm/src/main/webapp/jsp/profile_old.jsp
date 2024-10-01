<%@page import="com.pay10.commons.util.SaltFactory"%>
<%@page import="com.pay10.commons.user.User"%>
<%@page import="com.pay10.commons.util.Currency"%>
<%@page import="com.pay10.commons.util.Amount"%>
<%@page import="com.pay10.commons.util.FieldType"%>
<%@page import="org.slf4j.Logger"%>
<%@page import="org.slf4j.LoggerFactory"%>
<%@page import="com.pay10.commons.util.Constants"%>
<%@page import="com.pay10.commons.util.PropertiesManager"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Merchant Profile</title>
<link href="../css/default.css" rel="stylesheet" type="text/css" />
<script src="../js/jquery-1.11.0.min.js"></script>
<!-- <script src="../js/jquery.min.js" type="text/javascript"></script> -->
<script src="../js/core/popper.min.js"></script>
<script src="../js/core/bootstrap-material-design.min.js"></script>
<script src="../js/plugins/perfect-scrollbar.jquery.min.js"></script>
<!-- Control Center for Material Dashboard: parallax effects, scripts for the example pages etc -->
<script src="../js/material-dashboard.js?v=2.1.0" type="text/javascript"></script>
<script src="../js/bootstrap.min.js"></script>
<script type="text/javascript">
$(document).ready(function() {
	   var userType = "<s:property value='%{#session.USER.UserGroup.group}'/>";
	      console.log(userType)
	      if(userType=='Reseller'){
	    	  $('#businessTypeTr').hide();
	    	  $('#paymentLinkTr').hide();
	        }
	      else{
	    	  $('#businessTypeTr').show();
	    	  $('#paymentLinkTr').show();
		      }
	    
});
</script>
<script type="text/javascript">
	function sendDefaultCurrency() {
		var token = document.getElementsByName("token")[0].value;
		var dropDownOption = document.getElementById("defaultCurrency").options;
		var dropDown = document.getElementById("defaultCurrency").options.selectedIndex;
		var payId = '<s:property value="#session.USER.payId" />';
		$
				.ajax({
					url : 'setDefaultCurrency',
					type : 'post',
					data : {
						defaultCurrency : document
								.getElementById("defaultCurrency").value,
						token : token
					},
					success : function(data) {
				
						var responseDiv = document.getElementById("response");
						responseDiv.innerHTML = data.response;
						responseDiv.style.display = "block";
						 if(responseData == null){
							 responseDiv.innerHTML = "Operation not successfull, please try again later!!"
									responseDiv.style.display = "block";
							 responseDiv.className = "error error-new-text";
							 event.preventDefault();
						 }
						var currencyDropDown = document
								.getElementById("defaultCurrency");
						 responseDiv.className = "success success-text";
					},
					error : function(data) {
						var responseDiv = document.getElementById("response");
						responseDiv.innerHTML = "Error updating default currency please try again later!!"
						responseDiv.style.display = "block";
						responseDiv.className = "error error-new-text";
					}
				});
	}
</script>
<style type="text/css">.error-text{color:#a94442;font-weight:bold;background-color:#f2dede;list-style-type:none;text-align:center;list-style-type: none;margin-top:10px;
}.error-text li { list-style-type:none; }
#response{color:green;}

table.product-specbigstripes .borderbtmleftradius {
    border-bottom-left-radius: 0px !important;
}
table.product-specbigstripes .borderbtmrightradius {
     border-bottom-right-radius: 0px !important;
}
</style>
</head>

<body>
<div class="error-text"><s:actionmessage/></div>
	<table class="table">

		<tr>
			<td height="10" align="center">
				<ul id="tabClick" class="nav nav-tabs" data-tabs="tabs"
					style="border-bottom: none;">
					<s:if test="%{#session.USER.UserType.name()=='SUBUSER'}">
					<li class="tabss"><a href="#MyPersonalDetails"
						data-toggle="tab">My Personal Details</a></li>
						</s:if>
						<s:else>
					<li class="tabss "><a href="#MyPersonalDetails"
						data-toggle="tab">My Personal Details</a></li>
					 <li class="tabss"><a href="#MyContactDetails" data-toggle="tab">My
							Contact Details</a></li>
					<li class="tabss"><a href="#MyBankDetails" data-toggle="tab">My Bank
							Details</a></li>
					<li class="tabss"><a href="#MyBusinessDetails" data-toggle="tab">My
							Business Details</a></li>
					<s:if test="%{#session.USER.UserType.name()=='MERCHANT'}">
						<li class="tabss"><a href="#Integration" data-toggle="tab">Integration</a></li>
					</s:if>
			</s:else>
					<!--<li><a href="#DocumentsUploads" data-toggle="tab">Documents Uploads</a></li> -->
				
				</ul>

				<div id="my-tab-content" class="tab-content">
				<s:if test="%{#session.USER.UserType.name()=='SUBUSER'}">
					<div class="tab-pane active" id="MyPersonalDetails">
						<br> <br>
						<div>
							<%
								Logger logger = LoggerFactory.getLogger("Profile");
									String currencyCodeAlpha = "";
									try {
										User sessionUser = (User) session
												.getAttribute(Constants.USER.getValue());
										String currencyNumeric = sessionUser.getDefaultCurrency();
										currencyCodeAlpha = Currency
												.getAlphabaticCode(currencyNumeric);
									} catch (Exception exception) {
										logger.error("Exception on profile page " + exception);
									}
							%>
							<div style="display:none"  id ="response"></div>
							<table class="product-specbigstripes">
								<tr>
									<td width="30%" height="25" align="left"
										class="greytdbg borderleftradius"><strong>Business
											name:</strong></td>
									<td width="70%" align="left" class="greytdbg borderrightradius"><s:property
											value="#session.USER.businessName" /></td>
								<tr>
									<td height="30" align="left" valign="middle"><strong>Email
											ID:</strong></td>
									<td align="left"><s:property value="#session.USER.emailId" /></td>
								</tr>
								<tr>
									<td height="30" align="left" class="greytdbg"><strong>First
											Name:</strong></td>
									<td align="left" class="greytdbg"><s:property
											value="#session.USER.firstName" /></td>
								</tr>
								<tr>
									<td height="30" align="left"><strong>Last Name:</strong></td>
									<td align="left"><s:property
											value="#session.USER.lastName" /></td>
								</tr>

								<tr>
									<td height="30" align="left" class="greytdbg"><strong>Company
											Name:</strong></td>
									<td align="left" class="greytdbg"><s:property
											value="#session.USER.companyName" /></td>
								</tr>
								<tr>
									<td height="30" align="left" class="borderbtmleftradius"><strong>Business
											Type:</strong></td>
									<td align="left" class="borderbtmrightradius"><s:property
											value="#session.USER.businessType" /></td>
								</tr>
								<tr>
									<td height="30" align="left" class="greytdbg"><strong>Mobile:</strong></td>
									<td align="left" class="greytdbg"><s:property
											value="#session.USER.mobile" /></td>
								</tr>
								<tr>
									<td height="30" align="left" class="borderbtmleftradius"><strong>Default
											Currency:</strong></td>
									<td align="left" class="borderbtmrightradius">
										<table>
											<tr>
												<td style="border: none !important;" height="30"><s:select
														name="defaultCurrency" id="defaultCurrency"
														list="currencyMap" style="width:100px; display:inline;"	class="form-control" /></td><br>
												<td style="border: none !important;" height="30"><input
													type="button" id="btnSave" name="btnSave"
													class="btn btn-block btn-primary" value="Submit"
													onclick="sendDefaultCurrency()" style="padding: 5px;
													font-size: 11px;
													width: 60px;" >
												</td>
											</tr>

										</table>
									</td>
								</tr>
					
							</table>
						</div>
						<br> <br>
						
					</div>
					</s:if>
					<s:else>
								<div class="tab-pane active" id="MyPersonalDetails">
						<br> <br>
							<div style="display:none"  id = "response"></div>
						<div>
							<%
								Logger logger = LoggerFactory.getLogger("Profile");
									String currencyCodeAlpha = "";
									try {
										User sessionUser = (User) session
												.getAttribute(Constants.USER.getValue());
										String currencyNumeric = sessionUser.getDefaultCurrency();
										currencyCodeAlpha = Currency
												.getAlphabaticCode(currencyNumeric);
									} catch (Exception exception) {
										logger.error("Exception on profile page " + exception);
									}
							%>
						
							<table class="product-specbigstripes">
								<tr>
									<td width="30%" height="25" align="left"
										class="greytdbg borderleftradius"><strong>Business
											name:</strong></td>
									<td width="70%" align="left" class="greytdbg borderrightradius"><s:property
											value="#session.USER.businessName" /></td>
								<tr>
									<td height="30" align="left" valign="middle"><strong>Email
											ID:</strong></td>
									<td align="left"><s:property value="#session.USER.emailId" /></td>
								</tr>
								<tr>
									<td height="30" align="left" class="greytdbg"><strong>First
											Name:</strong></td>
									<td align="left" class="greytdbg"><s:property
											value="#session.USER.firstName" /></td>
								</tr>
								<tr>
									<td height="30" align="left"><strong>Last Name:</strong></td>
									<td align="left"><s:property
											value="#session.USER.lastName" /></td>
								</tr>

								<tr>
									<td height="30" align="left" class="greytdbg"><strong>Company
											Name:</strong></td>
									<td align="left" class="greytdbg"><s:property
											value="#session.USER.companyName" /></td>
								</tr>
								<tr id="businessTypeTr">
									<td height="30" align="left" class="borderbtmleftradius"><strong>Business
											Type:</strong></td>
									<td align="left" class="borderbtmrightradius"><s:property
											value="#session.USER.businessType" /></td>
								</tr>
								<tr>
									<td height="30" align="left" class="borderbtmleftradius"><strong>Default
											Currency:</strong></td>
									<td align="left" class="borderbtmrightradius">
										<table>
											<tr>
											<s:if test="%{#session.USER.UserType.name()=='RESELLER'}">
												<td style="border: none !important;" height="30">
													<s:select name="defaultCurrency" disabled ="true" id="defaultCurrency"
														list="currencyMap" style="width:100px; display:inline;"	class="form-control" />
														</td><br>
												<td style="border: none !important;" height="30">
													<input type="button" id="btnSave" name="btnSave"  disabled ="true"
													class="btn  btn-block btn-primary" value="Submit"
													onclick="sendDefaultCurrency()" style="padding: 5px;
													font-size: 11px;
													width: 60px;">
												
												</td>
												</s:if>
												<s:else>
												<td style="border: none !important;" height="30">
													<s:select name="defaultCurrency" id="defaultCurrency"
														list="currencyMap" style="width:100px; display:inline;"	class="form-control" />
															</td>
												<td style="border: none !important;" height="30">
													<input type="button" id="btnSave" name="btnSave"
													class="btn  btn-block btn-primary" value="Submit"
													onclick="sendDefaultCurrency()" style="padding: 5px;
													font-size: 11px;
													width: 60px;">
												</s:else>
											</tr>
	

										</table>
									</td>
								</tr>
					
							</table>
						</div>
						<br> <br>
					</div>
					<div class="tab-pane" id="MyContactDetails">
						<br> <br>
						<div>
							<table class="product-specbigstripes">

								<tr>
									<td width="30%" height="25" align="left" valign="middle"
										class="greytdbg borderleftradius"><strong>Mobile:</strong></td>
									<td width="70%" align="left" class="greytdbg borderrightradius"><s:property 
											value="#session.USER.mobile" /></td>
								</tr>
								<tr>
									<td height="25" align="left" valign="middle"><strong>Telephone
											No.:</strong></td>
									<td align="left"><s:property
											value="#session.USER.telephoneNo" /></td>
								</tr>
								<tr>
									<td height="25" align="left" valign="middle" class="greytdbg"><strong>Address:</strong></td>
									<td align="left" class="greytdbg"><s:property
											value="#session.USER.address" /></td>
								</tr>
								<tr>
									<td height="25" align="left" valign="middle"><strong>City:</strong></td>
									<td align="left"><s:property value="#session.USER.city" /></td>
								</tr>
								<tr>
									<td height="25" align="left" valign="middle" class="greytdbg"><strong>State:</strong></td>
									<td align="left" class="greytdbg"><s:property
											value="#session.USER.state" /></td>
								</tr>
								<tr>
									<td height="25" align="left" valign="middle"><strong>Country:</strong></td>
									<td align="left"><s:property value="#session.USER.country" /></td>
								</tr>
								<tr>
									<td height="25" align="left" valign="middle"
										class="greytdbg borderbtmleftradius"><strong>Postal
											Code:</strong></td>
									<td align="left" class="greytdbg borderbtmrightradius"><s:property
											value="#session.USER.postalCode" /></td>
								</tr>

							</table>
						</div>
						<br> <br>
					</div> 
				<div class="tab-pane" id="MyBankDetails">
						<br> <br>
						<div>
							<table class="product-specbigstripes">
								<tr>
									<td width="30%" height="30" align="left" valign="middle"
										class="greytdbg borderleftradius"><strong>Bank
											Name:</strong></td>
									<td width="70%" align="left" valign="middle"
										class="greytdbg borderrightradius"><s:property
											value="#session.USER.bankName" /></td>
								</tr>
								<tr>
									<td height="30" align="left" valign="middle"><strong>IFSC
											Code:&nbsp;</strong></td>
									<td align="left" valign="middle"><s:property
											value="#session.USER.ifscCode" />&nbsp;</td>
								</tr>
								<tr>
									<td height="30" align="left" valign="middle" class="greytdbg"><strong>Acc
											Holder Name:&nbsp;&nbsp;</strong></td>
									<td align="left" valign="middle" class="greytdbg"><s:property
											value="#session.USER.accHolderName" /></td>
								</tr>
								<tr>
									<td height="30" align="left" valign="middle"><strong>Currency:&nbsp;&nbsp;</strong></td>
									<td align="left" valign="middle"><s:property
											value="#session.USER.currency" /></td>
								</tr>
								<tr>
									<td height="30" align="left" valign="middle" class="greytdbg"><strong>Branch
											Name:&nbsp;&nbsp;</strong></td>
									<td align="left" valign="middle" class="greytdbg"><s:property
											value="#session.USER.branchName" /></td>
								</tr>
								<tr>
									<td height="30" align="left" valign="middle"><strong>Pan
											Card:&nbsp;&nbsp;</strong></td>
									<td align="left" valign="middle"><s:property
											value="#session.USER.panCard" /></td>
								</tr>
								<tr>
									<td height="30" align="left" valign="middle"
										class="greytdbg borderbtmleftradius"><strong>Account
											No.:&nbsp;&nbsp;</strong></td>
									<td align="left" valign="middle"
										class="greytdbg borderbtmrightradius"><s:property
											value="#session.USER.accountNo" /></td>
								</tr>

							</table>
						</div>
						<br> <br>
					</div> 
					<div class="tab-pane" id="MyBusinessDetails">
						<br> <br>
						<div>
							<table class="product-specbigstripes">
							
							<tr id="paymentLinkTr">
									<td width="40%" height="30" align="left" valign="middle"
										class="greytdbg borderleftradius"><strong>Merchant Payment link:&nbsp;&nbsp;</strong></td>
									<td width="60%" align="left" valign="middle"
										class="greytdbg borderrightradius"><s:property
											value="#session.USER.paymentLink" /></td>
								</tr>
								
								<tr>
									<td width="40%" height="30" align="left" valign="middle"
										class="greytdbg borderleftradius"><strong>Organisation
											Type:&nbsp;&nbsp;</strong></td>
									<td width="60%" align="left" valign="middle"
										class="greytdbg borderrightradius"><s:property
											value="#session.USER.organisationType" /></td>
								</tr>


								<tr>
									<td height="30" align="left" valign="middle"><strong>Website
											URL:&nbsp;&nbsp;</strong></td>
									<td align="left" valign="middle" class="profilepage"><s:property
											value="#session.USER.website" /></td>
								</tr>
								<tr>
									<td height="30" align="left" valign="middle" class="greytdbg"><strong>Multicurrency
											Payments Required?:&nbsp;&nbsp;</strong></td>
									<td align="left" valign="middle" class="greytdbg"><s:property
											value="#session.USER.multiCurrency" /></td>
								</tr>

								<tr>
									<td height="30" align="left" valign="middle"><strong>
											Business Model:&nbsp;&nbsp;</strong></td>
									<td align="left" valign="middle"><s:property
											value="#session.USER.businessModel" /></td>
								</tr>




								<tr>
									<td height="30" align="left" valign="middle" class="greytdbg"><strong>Operation
											Address:&nbsp;&nbsp;</strong></td>
									<td align="left" valign="middle" class="greytdbg"><s:property
											value="#session.USER.operationAddress" /></td>
								</tr>
								<tr>
									<td height="30" align="left" valign="middle"><strong>Operation
											Address State:&nbsp;&nbsp;</strong></td>
									<td align="left" valign="middle"><s:property
											value="#session.USER.operationState" /></td>
								</tr>
								<tr>
									<td height="30" align="left" valign="middle" class="greytdbg"><strong>Operation
											Address City:&nbsp;&nbsp;</strong><strong></strong></td>
									<td align="left" valign="middle" class="greytdbg"><s:property
											value="#session.USER.operationCity" /></td>
								</tr>
								<tr>
									<td height="30" align="left" valign="middle"><strong>Operation
											Address Pincode:&nbsp;&nbsp;</strong><strong></strong></td>
									<td align="left" valign="middle"><s:property
											value="#session.USER.operationPostalCode" /></td>
								</tr>
								<tr>
									<td height="30" align="left" valign="middle" class="greytdbg"><strong>Date
											of Establishment</strong>:<strong>&nbsp;&nbsp;</strong><strong></strong></td>
									<td align="left" valign="middle" class="greytdbg"><s:property
											value="#session.USER.dateOfEstablishment" /></td>
								</tr>
								<tr>
									<td height="30" align="left" valign="top"><strong>
											CIN:&nbsp;&nbsp;</strong></td>
									<td align="left" valign="top"><s:property
											value="#session.USER.cin" /></td>
								</tr>
								<tr>
									<td height="30" align="left" valign="top" class="greytdbg"><strong>
											PAN</strong>:<strong>&nbsp;&nbsp;</strong><strong></strong></td>
									<td align="left" valign="top" class="greytdbg"><s:property
											value="#session.USER.pan" /></td>
								</tr>
								<tr>
									<td height="30" align="left" valign="top"><strong>Name
											on PAN Card:&nbsp;&nbsp;</strong></td>
									<td align="left" valign="top"><s:property
											value="#session.USER.panName" /></td>
								</tr>
								<tr>
									<td height="30" align="left" valign="middle" class="greytdbg"><strong>Expected
											number of transaction:&nbsp;&nbsp;</strong></td>
									<td align="left" valign="middle" class="greytdbg"><s:property
											value="#session.USER.noOfTransactions" /></td>
								</tr>
								<tr>
									<td height="30" align="left" valign="middle"><strong>Expected
											amount of transaction:&nbsp;&nbsp;</strong></td>
									<td align="left" valign="middle"><s:property
											value="#session.USER.amountOfTransactions" /></td>
								</tr>
								<tr>
									<td height="30" align="left" valign="middle" class="greytdbg"><strong>Disable/Enable
											Transaction Email:&nbsp;&nbsp;</strong></td>
									<td align="left" valign="middle" class="greytdbg"><s:property
											value="#session.USER.transactionEmailerFlag" /></td>
								</tr>
								<tr>
									<td height="30" align="left" valign="middle"
										class="borderbtmleftradius"><strong>TransactionEmail:&nbsp;&nbsp;</strong></td>
									<td align="left" valign="middle" class="borderbtmrightradius"><s:property
											value="#session.USER.transactionEmailId" /></td>
								</tr>
							</table>

						</div>
						<br> <br>
					</div> 
					</s:else>
					<div class="tab-pane" id="Integration">
						<br> <br>
						<div>
							<table class="product-specbigstripes">
								<tr>
									<td width="30%" height="30" align="left" valign="middle"
										class="greytdbg borderleftradius"><strong>Pay
											Id:</strong></td>
									<td width="70%" align="left" valign="middle"
										class="greytdbg borderrightradius"><s:property
											value="#session.USER.payId" /></td>
								</tr>


								<tr>
									<td height="30" align="left" valign="middle"><strong>Salt:&nbsp;</strong></td>
									<td align="left" valign="middle"><%=SaltFactory.getSaltProperty((User) session
						.getAttribute("USER"))%>&nbsp;</td>
								</tr>
								<tr>
									<td height="30" align="left" valign="middle" class="greytdbg"><strong>Request
										url:&nbsp;&nbsp;</strong></td>
								<td align="left" valign="middle" class="greytdbg" style="word-break: break-all;"><%=new PropertiesManager()
					.getSystemProperty("RequestURL")%></td>
								</tr>

								
								
							</table>
						</div>
						<br> <br>
					</div>
				</div>

			</td>
		</tr>
	</table>
	
<script>
	;(function ($, window, document, undefined) {

	    var pluginName = "metisMenu",
	        defaults = {
	            toggle: true
	        };
	        
	    function Plugin(element, options) {
	        this.element = element;
	        this.settings = $.extend({}, defaults, options);
	        this._defaults = defaults;
	        this._name = pluginName;
	        this.init();
	    }

	    Plugin.prototype = {
	        init: function () {

	            var $this = $(this.element),
	                $toggle = this.settings.toggle;

	            $this.find('li.active').has('ul').children('ul').addClass('collapse in');
	            $this.find('li').not('.active').has('ul').children('ul').addClass('collapse');

	            $this.find('li').has('ul').children('a').on('click', function (e) {
	                e.preventDefault();

	                $(this).parent('li').toggleClass('active').children('ul').collapse('toggle');

	                if ($toggle) {
	                    $(this).parent('li').siblings().removeClass('active').children('ul.in').collapse('hide');
	                }
	            });
	        }
	    };

	    $.fn[ pluginName ] = function (options) {
	        return this.each(function () {
	            if (!$.data(this, "plugin_" + pluginName)) {
	                $.data(this, "plugin_" + pluginName, new Plugin(this, options));
	            }
	        });
	    };

	})(jQuery, window, document);
</script>
<script>

(function ($) {
"use strict";
var mainApp = {

    initFunction: function () {
        /*MENU 
        ------------------------------------*/
        $('#main-menu').metisMenu();
		
        $(window).bind("load resize", function () {
            if ($(this).width() < 768) {
                $('div.sidebar-collapse').addClass('collapse')
            } else {
                $('div.sidebar-collapse').removeClass('collapse')
            }
        });

 
    },

    initialization: function () {
        mainApp.initFunction();

    }

}
// Initializing ///

$(document).ready(function () {
    mainApp.initFunction();
});

}(jQuery));
</script>
	<script>
		$(document).on(
				'change',
				'.btn-file :file',
				function() {
					var input = $(this), numFiles = input.get(0).files ? input
							.get(0).files.length : 1, label = input.val()
							.replace(/\\/g, '/').replace(/.*\//, '');
					input.trigger('fileselect', [ numFiles, label ]);
				});

		$(document)
				.ready(
						function() {
							$('.btn-file :file')
									.on(
											'fileselect',
											function(event, numFiles, label) {

												var input = $(this).parents(
														'.input-group').find(
														':text'), log = numFiles > 1 ? numFiles
														+ ' files selected'
														: label;

												if (input.length) {
													input.val(log);
												} else {
													if (log)
														alert(log);
												}

											});
						});
	</script>
<script>
window.onload=(event)=>{
	var current = document.getElementsByClassName("tabss");

      current[0].classList.add('active');
  

}
var selector ='.nav li';
$(selector).on('click',function(){
	$(selector).removeClass('active');
	$(this).addClass('active');
})

</script>
</body>
</html>