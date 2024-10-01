
<%@page import="com.pay10.crm.action.ForwardAction"%>
<%@page import="com.pay10.commons.user.User"%>
<%@page import="com.pay10.commons.util.Constants"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@page import="com.pay10.commons.util.Currency"%>
<%@page import="com.pay10.commons.util.Amount"%>
<%@ page import="org.owasp.esapi.ESAPI"%>
<%@page import="com.pay10.commons.util.FieldType"%>
<%@page import="org.slf4j.Logger"%>
<%@page import="org.slf4j.LoggerFactory"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Super Admin Profile</title>
<link href="../css/default.css" rel="stylesheet" type="text/css" />
<!-- <script src="../js/jquery.min.js"></script> -->
<!-- <script src="../js/jquery.min.js" type="text/javascript"></script> -->
<script src="../js/jquery.min.js"></script>
<!-- <script src="../js/jquery.min.js" type="text/javascript"></script> -->

<script type="text/javascript" src="../js/jquery.js"></script>
<script type="text/javascript" src="../js/jquery.easing.js"></script>
<script type="text/javascript" src="../js/jquery.dimensions.js"></script>
<script type="text/javascript" src="../js/jquery.accordion.js"></script>
<script src="../js/core/popper.min.js"></script>
<script src="../js/core/bootstrap-material-design.min.js"></script>
<script src="../js/plugins/perfect-scrollbar.jquery.min.js"></script>
<!-- Control Center for Material Dashboard: parallax effects, scripts for the example pages etc -->
<script src="../js/material-dashboard.js?v=2.1.0" type="text/javascript"></script>


	<!-- <script>
		jQuery(document).ready(function($) {
			$('#tabs').tab();
		});
	</script> -->
	
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
						payId : payId,
						defaultCurrency : document
								.getElementById("defaultCurrency").value,
						token : token
					},
					success : function(data) {
						var responseDiv = document.getElementById("response");
						responseDiv.innerHTML = data.response;
						responseDiv.style.display = "block";
						 var responseData = data.response;
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
.product-specbigstripes tr td{color:#555;}
.btn:focus{
		outline: 0 !important;
	}
</style>
</head>
<body>
<div class="error-text"><s:actionmessage/></div>
	<table width="100%" border="0" align="center" cellpadding="0"
		cellspacing="0">
		<tr>
			<td height="10" align="center">
				<ul id="tabs" class="nav nav-tabs" data-tabs="tabs"
					style="border-bottom: none;">
						<s:if test="%{#session.USER.UserType.name()=='SUBADMIN'}">
					<li class="active"><a href="#MyPersonalDetails"
						data-toggle="tab">My Personal Details</a></li>
						</s:if>
						<s:else>
						<li class="active"><a href="#MyPersonalDetails"
						data-toggle="tab">My Personal Details</a></li>
				<li><a href="#MyContactDetails" data-toggle="tab">My
							Contact Details</a></li>
							</s:else>
				</ul>
			</td>
			<td align="center">&nbsp;</td>
		</tr>

		<tr>
			<td height="10" align="center">
				<div id="my-tab-content" class="tab-content">
					<s:if test="%{#session.USER.UserType.name()=='SUBADMIN'}">
					<div class="tab-pane active" id="MyPersonalDetails">
						<br>
						<br>
						 <div style="display:none"  id = "response"></div>
						<div>
							<%
								    Logger logger = LoggerFactory.getLogger("Profile Admin");
									String currencyCodeAlpha = "";
									try {
										User sessionUser = (User) session
												.getAttribute(Constants.USER.getValue());
										String currencyNumeric = sessionUser.getDefaultCurrency();
										currencyCodeAlpha = Currency
												.getAlphabaticCode(currencyNumeric);
									} catch (Exception exception) {
										logger.error("Exception on Profile Admin page " + exception);
									}
							%>
							<table width="" align="center" class="product-specbigstripes" style="margin-bottom:38px;">
								
									<td height="30" align="left" valign="middle" class="greytdbg" width="20%"><strong>Email
											ID:</strong></td>
									<td align="left" class="greytdbg" width="20%"><s:property
											value="#session.USER.emailId" /></td>
								</tr>
								<tr>
									<td height="30" align="left" width="20%"><strong>First
											Name:</strong></td>
									<td align="left" width="20%"><s:property
											value="#session.USER.firstName" /></td>
								</tr>
								<tr>
									<td height="30" align="left" class="greytdbg" width="20%"><strong>Last Name:</strong></td>
									<td align="left" class="greytdbg" width="20%"><s:property
											value="#session.USER.lastName" /></td>
								</tr>
<s:if test="%{#session.USER.UserType.name()=='MERCHANT' || #session.USER_TYPE.name()=='SUBUSER'}">
								<tr>
									<td height="30" align="left" width="20%"><strong>Company
											Name:</strong></td>
									<td align="left" width="20%"><s:property
											value="#session.USER.companyName" /></td>
								</tr>
								<tr>
									<td height="30" align="left" class="borderbtmleftradius greytdbg" width="20%"><strong>Business
											Name:</strong></td>
									<td align="left" class="borderbtmrightradius greytdbg" width="20%"><s:property
											value="#session.USER.businessName" /></td>
								</tr>
								</s:if>
								<tr>
									<td height="30" align="left" class="" width="20%"><strong>Default
											Currency:</strong></td>
									<td align="left" class="" width="20%" height="30" style="padding: 0px!important;">
										<table>
											<tr>
											<td style="border: none !important;" height="30" width="20%">
											<s:select name="defaultCurrency" id="defaultCurrency"
														list="currencyMap" style="width:100px; display:inline;"
														class="form-control"/>
											</td>
											<td style="border: none !important;" height="30" ><input
													type="button" id="btnSave" name="btnSave"
													class="btn btn-success btn-md" value="Submit"
													onclick="sendDefaultCurrency()" style="display: inline;">
											</td>
											</tr>


										</table>
									</td>
								</tr>
							<tr>
									<td width="20%" height="25" align="left" valign="middle"
										class="greytdbg"><strong>Mobile:</strong></td>
									<td width="20%" align="left" class="greytdbg"><s:property
											value="#session.USER.mobile" /></td>
								</tr>
							</table>
						</div>

					</div>
					</s:if>
					<s:else>
	<div class="tab-pane active" id="MyPersonalDetails">
						<br>
						<br>
						 <div style="display:none"  id = "response"></div>
						<div>
							<%
								    Logger logger = LoggerFactory.getLogger("Profile Admin");
									String currencyCodeAlpha = "";
									try {
										User sessionUser = (User) session
												.getAttribute(Constants.USER.getValue());
										String currencyNumeric = sessionUser.getDefaultCurrency();
										currencyCodeAlpha = Currency
												.getAlphabaticCode(currencyNumeric);
									} catch (Exception exception) {
										logger.error("Exception on Profile Admin page " + exception);
									}
							%>
							<table width="" align="center" class="product-specbigstripes" style="margin-bottom:38px;">
								<tr>
								<s:if test="%{#session.USER.UserType.name()=='MERCHANT' || #session.USER_TYPE.name()=='SUBUSER'}">
								<tr>
								<td width="20%" height="25" align="left"
										class="greytdbg borderleftradius"><strong>Business
											name:</strong></td>
									<td width="20%" align="left" class="greytdbg borderrightradius"><s:property
											value="#session.USER.businessName" /></td>
											</tr>
							<tr>
									<td height="30" align="left" class="greytdbg" width="20%"><strong>Company
											Name:</strong></td>
									<td align="left" class="greytdbg" width="20%"><s:property
											value="#session.USER.companyName" /></td>
								</tr>
								</s:if>
							<s:elseif test="%{#session.USER.UserType.name()=='SUPERADMIN' || #session.USER.UserType.name()=='SUBSUPERADMIN'}">
								<tr>
									<td height="30" align="left" valign="middle" class="greytdbg" width="20%"><strong>Email
											ID:</strong></td>
									<td align="left" class="greytdbg" width="20%"><s:property
											value="#session.USER.emailId" /></td>
								</tr>
								<tr>
									<td height="30" align="left" width="20%"><strong>First
											Name:</strong></td>
									<td align="left" width="20%"><s:property
											value="#session.USER.firstName" /></td>
								</tr>
								<tr>
									<td height="30" align="left" class="greytdbg" width="20%"><strong>Last Name:</strong></td>
									<td align="left" class="greytdbg" width="20%"><s:property
											value="#session.USER.lastName" /></td>
								</tr>
	
								
								<tr>
									<td height="30" align="left" class="" width="20%"><strong>Default
											Currency:</strong></td>
									<td align="left" class="" width="20%" height="30" style="padding: 0px!important;">
										<table>
											<tr>
											<td style="border: none !important;" height="30">
											<s:select name="defaultCurrency" id="defaultCurrency"
														list="currencyMap" style="width:100px; display:inline;"
														class="form-control"/></td>
												<td style="border: none !important;" height="30"><input
													type="button" id="btnSave" name="btnSave"
													class="btn  btn-block btn-primary" value="Submit"
													onclick="sendDefaultCurrency()" >
												</td>
											</tr>


										</table>
									</td>
								</tr>
							</s:elseif>
							</table>
						</div>

					</div>
				<div class="tab-pane" id="MyContactDetails">
						<br>
						<br>
						<div>
							<table width="60%" align="center" border="0" cellspacing="0"
								cellpadding="0" class="product-specbigstripes" style="margin-bottom:38px;">

								<tr>
									<td width="20%" height="25" align="left" valign="middle"
										class="greytdbg borderleftradius"><strong>Mobile:</strong></td>
									<td width="20%" align="left" class="greytdbg borderrightradius"><s:property
											value="#session.USER.mobile" /></td>
								</tr>
								<tr>
									<td height="25" align="left" valign="middle" width="20%"><strong>Telephone
											No.:</strong></td>
									<td align="left" width="20%"><s:property
											value="#session.USER.telephoneNo" /></td>
								</tr>
								<tr>
									<td height="25" align="left" valign="middle" class="greytdbg" width="20%"><strong>Address:</strong></td>
									<td align="left" class="greytdbg" width="20%"><s:property
											value="#session.USER.address" /></td>
								</tr>
								<tr>
									<td height="25" align="left" valign="middle" width="20%"><strong>City:</strong></td>
									<td align="left" width="20%"><s:property value="#session.USER.city" /></td>
								</tr>
								<tr>
									<td height="25" align="left" valign="middle" class="greytdbg" width="20%"><strong>State:</strong></td>
									<td align="left" class="greytdbg" width="20%"><s:property
											value="#session.USER.state" /></td>
								</tr>
								<tr>
									<td height="25" align="left" valign="middle" width="20%"><strong>Country:</strong></td>
									<td align="left" width="20%"><s:property
											value="#session.USER.country" /></td>
								</tr>
								<tr>
									<td height="25" align="left" valign="middle"
										class="greytdbg" width="20%"><strong>Postal
											Code:</strong></td>
									<td align="left" class="greytdbg" width="20%"><s:property
											value="#session.USER.postalCode" /></td>
								</tr>

							</table>
						</div>
					</div>
					
					 </s:else>
				</div>
		</tr>
	</table>
	
	<script src="../js/bootstrap.min.js"></script>
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
		jQuery(document).ready(function($) {
			$('#tabs').tab();
		});
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
	</body>
</html>
