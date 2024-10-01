<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="org.owasp.esapi.ESAPI"%>
<%@page import="com.pay10.commons.util.Amount"%>
<%@page import="com.pay10.commons.util.Currency"%>
<%@page import="com.pay10.commons.util.FieldType"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.Locale"%>
<%@page import="java.net.URL"%>
<%@page import="org.apache.commons.lang.time.DateUtils"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width,initial-scale=1">
<link rel="icon" href="../image/98x98.png">
<link rel="icon" href="favicon.png">
<title>PG Intent Page</title>
<script src="../js/jquery.min.js"></script>
<link rel="stylesheet" href="../css/intentupi.css">
<style type="text/css">
#header {
	padding: 15px 20px;
}

.validCardError {
	visibility: hidden;
}

.marginTop {
	margin-bottom: 20px;
}

.opacityMob {
	opacity: 0.1;
}

input[type=text].password-autocomplete-disabler {
	position: absolute !important;
	left: -10000px !important;
	top: -10000px !important;
}

.pointerEventNone {
	pointer-events: none !important;
	background: #ccc !important;
	border: 2px solid #ccc !important;
}

.loader1 {
	display: none;
}

.loader2 {
	display: none;
}

.radioError {
	color: red;
	margin: 0;
}

.radioErrorDiv {
	min-height: 17px;
	font-size: 12px;
}

.marginBtm0 {
	margin-bottom: 0 !important;
}

.autoDebitP {
	padding-left: 17px;
	color: grey;
	font-size: 11px;
	margin-bottom: 0;
}

#sidebar dl dd big {
	font-size: 12px;
}

.alert {
	padding: 15px;
	margin-bottom: 20px;
	border: 1px solid transparent;
	border-radius: 4px;
}

.alert-danger {
	color: #a94442;
	background-color: #f2dede;
	border-color: #ebccd1;
}
</style>
<script type="text/javascript" src="../js/intentupi.js"></script>
<script type="text/javascript">
var tabhide=0;
var timeInterval;
var timer = setInterval(function() {
	timeInterval -= 1000;
	window.status = time(timeInterval);
	if (timeInterval <= 0) {
		clearInterval(timer);
		if(xhrUpi){
			xhrUpi.abort();
		}
		top.location = "sessionTimeout";
	}
}, 1000);

function time(ms) {
	var t = '';
	var sec = Math.floor(ms / 1000);
	ms = ms % 1000;

	var min = Math.floor(sec / 60);
	sec = sec % 60;
	t = two(sec);	

	var hr = Math.floor(min / 60);
	min = min % 60;
	t = two(min) + ":" + t;

	document.getElementById("lblSessionTime").innerText = t;
	return "You session will timeout in " + t + " minutes.";
} 

function two(x) {
	return ((x > 9) ? "" : "0") + x
}

function updateCheckBoxValue(){
	document.getElementById('cardsaveflag').value = document.getElementById('cardsaveflag1').checked;
}
	
function displayDefault(){
	paymentOptionString = "<s:property value='%{#session.SUPPORTED_PAYMENT_TYPE}'/>";
	merchantLogo = "<s:property value='%{#session.PAY_ID}'/>";
	document.getElementById("radioError").style.display = "none";		
}
function checkAquirerType(){
	// var aquirerTypeGet = "<%=session.getAttribute("ACQUIRER_TYPE")%>";
	//	retryPayment = false;
	//	if(aquirerTypeGet != "null" && retryPayment){
	//	}else if(aquirerTypeGet != "null" && !retryPayment){
	//		top.location = "txncancel";
	//	}
	var isRetry = "<s:property value='%{#session.IS_RETRY}'/>";
	var retryMessageText = "<s:property value='%{#session.RETRY_MESSAGE}'/>";
	if(isRetry==="Y"){
		document.getElementById('retryMessage').innerHTML = retryMessageText;
		document.getElementById('retryMessage').className = "alert alert-danger"
	}
}

</script>
</head>
<body class="body loader_disabled"
	OnLoad="setInterval(1); displayDefault(); pageInfo(); checkAquirerType(); loaderClassRemove();"
	oncontextmenu="return false;">
	<%
		java.util.Date date = new java.util.Date();
		String servletDate = String.valueOf(date);
		String sessionTime = (String) session.getAttribute("sessionCreationTime");

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date endDate = format.parse(sessionTime);
		endDate = DateUtils.addMinutes(endDate, 15);
		long diff = endDate.getTime() - date.getTime();
		long diffMinutes = diff / (60 * 1000) % 60;
	%>
	<script>
		timeInterval = '<%=diff%>';
	</script>
	<%
		
	%>

	<noscript>
		<style type="text/css">
.loader {
	display: none;
	background: rgba(0, 0, 0, 0.9);
}

.loader.loaderNoscripts, .loader .approvedNotification {
	display: block;
}

.approvedNotificationRedirect {
	display: block;
}
#upi-sbmt{
   	border-radius: 5px;
    font-weight: 500;
    font-size: 20px;
    line-height: 29px;
    /* text-align: center; */
    color: #fff;
    border: none;
    padding: 5px;
    width: 60%;
}
</style>
		<div class="loader loaderNoscripts">
			<div class="loaderInner" align="center"></div>
			<div id="approvedNotification" class="approvedNotification">
				<h3>Please enable javascript in your browser settings</h3>
			</div>
		</div>
	</noscript>

	<div class="csls_loader-wrapper active-loader d-f f-jcc f-aic">
		<img src="../image/loader.gif" alt="">
	</div>
	<!-- /.csls_loader-wrapper -->

	<div class="loader loader1" id="loading">
		<div class="loaderInner" align="center"></div>
		<div id="approvedNotification" class="approvedNotification">
			<h3>Do not refresh this page or press back button or close the window</h3>
			<p><b>Note : Please wait It will take little longer time to redirect you to merchant website..</b></p>
			<!-- <div>
				<button class="cancelBtn" onclick="myCancelAction(true)">Cancel</button>
			</div> -->
		</div>
	</div>

	<div class="loader loader1" id="loadingRedirect">
		<div id="approvedNotificationRedirect" class="approvedNotification">
			<h3>Redirecting to UPI service...</h3>
			<p>Do not refresh this page or press back button</p>

		</div>
	</div>



	<div class="loader loader2" id="loading2">
		<div class="loaderInner" align="center"></div>
	</div>
	<div class="loader loader1" id="deleteCnfBox">
		<div class="delete_cnf">
			<p>
				Do you want to delete this saved card <strong class="selectedCard">xxxx
					xxxxx xxxx</strong> ?
			</p>
			<div>
				<button class="yes_delete" onclick="deleteSaveCard(true)">Yes</button>
				<button class="no_delete" onclick="deleteSaveCard(false)">No</button>
			</div>
		</div>
	</div>


	<section class="csls_wrapper">
		<div class="csls_wrapper-inner">

			<div class="csls_container d-f f-jcb">
				<div class="csls_sidebar-section">
					<span class="close-btn" onclick="classRemove()">Close</span>
					<div id="sidebar">
						<div class="PhoneOrderSumry">
							<div>
								<span>Order ID</span><span><%=ESAPI.encoder().encodeForHTML((String) session.getAttribute(FieldType.ORDER_ID.getName()))%></span>
							</div>
							<div>
								<span>Amount Payable</span><span id="amountPayablePhone"></span>
							</div>
						</div>
						<div class="orderSum clearfix">
							<div class="rightOrder">
								<!--<img class="right-dot" src="../image/right-dot.jpg"/>-->
								<div class="midSec">
									<%
										String currencyCodeAlpha = "";
										String formattedAmount = "";
										String formattedCCTotalAmount = "";
										String formattedAmountTotal = "";
										String formattedDCTotalAmount = "";
										String ccSurchagreAmount = "";
										String dcSurchagreAmount = "";
										String buyerValue = "";
										String nbSurchagreAmount = "";
										String wlSurchagreAmount = "";
										String upSurchagreAmount = "";
										String adSurchagreAmount = "";
										String pcSurchagreAmount = "";
										String formattedNBTotalAmount = "";
										String formattedWLTotalAmount = "";
										String formattedUPTotalAmount = "";
										String formattedADTotalAmount = "";
										String formattedPCTotalAmount = "";
										String custPhone = "";
										
										try {
											buyerValue = ESAPI.encoder()
													.encodeForHTML((String) session.getAttribute(FieldType.CUST_NAME.getName()));
											String currencyCodeNumeric = (String) session.getAttribute(FieldType.CURRENCY_CODE.getName());
											String amount = (String) session.getAttribute(FieldType.AMOUNT.getName());

											currencyCodeAlpha = Currency.getAlphabaticCode(currencyCodeNumeric);

											if (amount.equals("null")) {
												response.sendRedirect("error");

											}
											formattedAmount = ESAPI.encoder().encodeForHTML(Amount.toDecimal(amount, currencyCodeNumeric));
											DecimalFormat df = new DecimalFormat("0.00");

											formattedCCTotalAmount = df.format(session.getAttribute(FieldType.CC_TOTAL_AMOUNT.getName()));

											formattedDCTotalAmount = df.format(session.getAttribute(FieldType.DC_TOTAL_AMOUNT.getName()));

											ccSurchagreAmount = df.format(session.getAttribute(FieldType.CC_SURCHARGE.getName()));

											dcSurchagreAmount = df.format(session.getAttribute(FieldType.DC_SURCHARGE.getName()));
											
											nbSurchagreAmount = df.format(session.getAttribute(FieldType.NB_SURCHARGE.getName()));
											
											wlSurchagreAmount = df.format(session.getAttribute(FieldType.WL_SURCHARGE.getName()));
											
											upSurchagreAmount = df.format(session.getAttribute(FieldType.UP_SURCHARGE.getName()));
											
											pcSurchagreAmount = df.format(session.getAttribute(FieldType.PC_SURCHARGE.getName()));
											
											adSurchagreAmount = df.format(session.getAttribute(FieldType.AD_SURCHARGE.getName()));
											
											formattedNBTotalAmount = df.format(session.getAttribute(FieldType.NB_TOTAL_AMOUNT.getName()));
											
											formattedWLTotalAmount = df.format(session.getAttribute(FieldType.WL_TOTAL_AMOUNT.getName()));
											
											formattedUPTotalAmount = df.format(session.getAttribute(FieldType.UP_TOTAL_AMOUNT.getName()));
											
											formattedPCTotalAmount = df.format(session.getAttribute(FieldType.PC_TOTAL_AMOUNT.getName()));
											
											formattedADTotalAmount = df.format(session.getAttribute(FieldType.AD_TOTAL_AMOUNT.getName()));
											
											custPhone = (String)session.getAttribute(FieldType.CUST_PHONE.getName());
											

										} catch (NullPointerException nPException) {
											response.sendRedirect("sessionTimeout");
										} catch (Exception exception) {

											response.sendRedirect("error");
										}
									%>
									<input type="hidden" id="ccsurchargeAmount"
										name="ccsurchargeAmount" value=<%=ccSurchagreAmount%>>
									<input type="hidden" id="ccsurchargeTotal"
										name="ccsurchargeTotal" value=<%=formattedCCTotalAmount%>>
									<input type="hidden" id="dcsurchargeAmount"
										name="ccsurchargeAmount" value=<%=dcSurchagreAmount%>>
									<input type="hidden" id="dcsurchargeTotal"
										name="dcsurchargeTotal" value=<%=formattedDCTotalAmount%>>
									<input type="hidden" id="nbsurchargeAmount"
										name="nbsurchargeAmount" value=<%=nbSurchagreAmount%>>
									<input type="hidden" id="nbsurchargeTotal"
										name="nbsurchargeTotal" value=<%=formattedNBTotalAmount%>>
									<input type="hidden" id="wlsurchargeAmount"
										name="wlsurchargeAmount" value=<%=wlSurchagreAmount%>>
									<input type="hidden" id="wlsurchargeTotal"
										name="wlsurchargeTotal" value=<%=formattedWLTotalAmount%>>
									<input type="hidden" id="upsurchargeAmount"
										name="upsurchargeAmount" value=<%=upSurchagreAmount%>>
									<input type="hidden" id="upsurchargeTotal"
										name="upsurchargeTotal" value=<%=formattedUPTotalAmount%>>
									<input type="hidden" id="pcsurchargeAmount"
										name="pcsurchargeAmount" value=<%=pcSurchagreAmount%>>
									<input type="hidden" id="pcsurchargeTotal"
										name="pcsurchargeTotal" value=<%=formattedPCTotalAmount%>>
									<input type="hidden" id="adsurchargeAmount"
										name="adsurchargeAmount" value=<%=adSurchagreAmount%>>
									<input type="hidden" id="adsurchargeTotal"
										name="adsurchargeTotal" value=<%=formattedADTotalAmount%>>
									<input type="hidden" id="currencyCode" name="currencyCode"
										value=<%=currencyCodeAlpha%>>

									<div class="summary bordered" id="middleSecSummary">
										<div class="summary-header">
											<small id="totalAmountName">Amount payable</small>
											<p class="summary-amount" id="totalAmount"></p>
										</div>
										<div class="summary-list">
											<div class="summary-list-item" id="surchargeInfo">
												<div class="d-f f-jcb">
													<small>Amount</small>
													<div id="innerAmount">
														₹
														<%=formattedAmount%></div>
												</div>
												<div class="d-f f-jcb">
													<small id="surchargeName">Con. Fee</small>
													<div id="surcharge"></div>
												</div>
											</div>
											<div class="summary-list-item">
												<div>
													<small>Order ID</small>
													<div id="orderId" name="orderId"><%=ESAPI.encoder().encodeForHTML((String) session.getAttribute(FieldType.ORDER_ID.getName()))%></div>
												</div>
											</div>
											<div class="summary-list-item">
												<div>
													<small id="buyer">Buyer</small>
													<div id="customerName" name="customerName"><%=ESAPI.encoder().encodeForHTML((String) session.getAttribute(FieldType.CUST_NAME.getName()))%></div>
												</div>
											</div>
											<div class="summary-list-item">
											<div>
											      <small id="moreInfo">Surcharge Amount + GST</small>
											      <div id="surchargeAmount" name="surchargeAmount"></div>	 
											</div>
										  </div>
										</div>
									</div>
								</div>
							</div>
						</div>
						<div class="orderfootDetails desktopOrderFoot"
							id="orderfootDetails2">2.00 % + 2.00 Applicable Taxes for
							all domestic Credit Cards NIL,For all Domestic Debit Cards
							transaction Amount upto Rs. 2,000. 2.00 % + 2.00 Applicable
							Taxes,For all Domestic Debit Cards transaction Amount above Rs.
							2,000. NIL,For all Domestic Debit Cards transaction Amount upto
							Rs. 2,000. 2.00 % + 2.00 Applicable Taxes,For all Domestic Debit
							Cards transaction Amount above Rs. 2,000.</div>
					</div>
					<!-- sidebar closeed -->
				</div>
				<!-- /.csls-sidebar -->
				<div class="csls_main-section">
					<div id="retryMessage" style="margin-bottom: 0"></div>
					<div class="csls_main-header">
						<div id="header" class="clearfix">
							<%
								String urlTmp = request.getRequestURL().toString();
								URL url = new URL(urlTmp);
								String webUrl = url.getHost();
							%>

							<%
								if (webUrl.equals("pg.zebrapay.in")) {
							%>
							<span class="logo" id="logoHeader" style="display: none;">
								<img src="../image/PP_ZebraPay_Logo.jpg" alt="Zebrapay Logo"
								title="Zebrapay Logo">
							</span>

							<%
								} else {
							%>
							<%-- <span class="logo" style="display: none;"> 
							<img src="../image/280x40.png" alt="pay10 Logo" title="pay10 Logo">
							</span> --%>
							<span class="logo" style="text-align: center;">
								<img src="../image/new-payment-page-logo.png" alt="pay10 Logo" title="pay10 Logo" width="77" height="47"/>
							</span>
							<%
								}
							%>
							<input type="hidden" value="<%=servletDate%>" id="unique" /> <span
								class="logo-ssn"> <input type="hidden" id="sessionTime"
								value=${sessionCreationTime}> <strong> <span>Your
										Session will expire in :</span> <s:label name="lblSessionTime"
										id="lblSessionTime"></s:label>
							</strong>
							</span> <span class="hamburger-icon"> <img
								src="../image/hamburger.png" onclick="classAdded()" alt="">
							</span> <span class="logoPhone" style="display: none;"> <img
								src="../image/280x40.png" id="cashlessoLogo"
								alt="Pay10" title="Pay10">
							</span>
						</div>
					</div>
					<!-- /.csls_main-header -->
					<div class="csls_payments-tabs">
						<div id="paymentTabs">
							<ul class="paymentNavs clearfix" id="paymentNavs">
							</ul>
						</div>
					</div>
					<!-- /.csls_payments-tabs -->
					<div class="csls_payments-box">
						<div class="paymentSections">
										<div id="saveCard" class="tabBox saveCardBox hideBox">
								<s:form autocomplete="off" name="exCard" method="post"
									target="_parent" action="pay" id="exCard">
									

									<ul class="savedCards clearfix">
										<small id="cardSupportedEX" class="text-danger text-left"></small>
										<s:if test="#session.TOKEN=='NA'"></s:if>
										<s:else>
											
										</s:else>
									</ul>
		

									<div id="radioErrorDiv" class="radioErrorDiv">
										<p class="text-danger1" id="radioError">Please Select at
											least one Saved Card</p>
										<p class="text-danger1" id="cvvErrorSav">Please Enter CVV
											Number</p>
										<p class="text-danger1" id="invalidCvvErrorSav">Invalid
											CVV</p>
									</div>

									<div class="mob-btn marginBtm0">
										<input type="button" name="exSubmit" theme="simple"
											id="exSubmit" value="Pay" class="payment-btn "
											onclick="saveCardPay()">
									</div>

									<div class="saveDetailsReturn d-f f-jcc mt2">
										<a href="#" onclick="myCancelAction();" key="ReturnToMerchant"
											id="ccCancelButton" class="float-save" name="ccCancelButton"
											theme="simple" ondragstart="return false;"
											ondrop="return false;">Return to merchant</a>
									</div>

									<small class="CTANote" style="display: none;"> </small>

									<s:hidden name="customToken" id="customToken1"
										value="%{#session.customToken}"></s:hidden>
								</s:form>

							</div>

							<div id="debitWithPin" class="tabBox mob-bottom hideBox">
								
							</div>

						

							<div id="upi" class="tabBox upiBox hideBox">
								<div class="upi-img d-f f-aic">
									<img src="../image/upi.png" alt="/"> <img
										src="../image/googlePay.png" alt="/"> <img
										src="../image/phonepay.png" alt="/"> <img
										src="../image/paytm.png" alt="/">
								</div>
								<!-- /.upi-img -->
								<div class="vpaSection">
									<%-- <input type="text" name="VPA" id="vpaCheck" placeholder=" "
										class="inputField" onkeyup="isValidVpa(); enableButton();"
										onkeydown=" return restrictKeyVpa(event,this);"
										onfocusout="isValidVpaOnFocusOut();" oncopy="return false"
										onpaste="return false" ondrop="return false">
									<span class="placeHolderText placeHolderTextVPA">Virtual
										Payment Address</span> --%>
										<input type="hidden" name="VPA" id="vpaCheck"
										oncopy="return false"
										onpaste="return false" ondrop="return false" value=<%=custPhone%>>
								</div>
								<div class="resultDiv">
									<p class="red1" id="red1">Invalid VPA</p>
									<p class="red1" id="enterVpa">Please Enter VPA</p>
								</div>
								<label class="vpaPara">VPA is a unique Payment address
									that can be linked to a person's bank account to make payments.</label>

								<div class="CTA mob-btn" id="pay-now" style="text-align: center;">

									<input type="button" class="payment-btn" id="upi-sbmt"
										value="Pay" onclick="submitUpiForm()">
									<div>

										<small id="errorBox" class="text-danger"></small>
									</div>
								</div>
<!-- 								<p class="returnMerchand"> -->
<!-- 									<a href="#" onclick="myCancelAction();" key="ReturnToMerchant" -->
<!-- 										id="ccCancelButton1" name="ccCancelButton" theme="simple" -->
<!-- 										ondragstart="return false;" ondrop="return false;">Return -->
<!-- 										to merchant</a> -->
<!-- 								</p> -->
							</div>


							<!-- <div id="iMudra" class="tabBox iMudraBox hideBox">
								<form id="imudraFormId" name="imudraFormName" action="pay"
									target="_parent" method="post" autocomplete="off">
									<div class="radio-list wallet"></div>

									<input type="hidden" name="paymentType" value="WL"> <input
										type="hidden" name="mopType" value="APWL"> <input
										type="hidden" id="amountImudra" name="amount" value="">

									<div class="CTA mob-btn">
										<input type="button" id="iMudraBtn" value="pay now"
											class="payment-btn" onclick="submitImudra()">
									</div>
									<p class="returnMerchand margin0">
										<a href="#" onclick="myCancelAction();" key="ReturnToMerchant"
											id="ADCancelButton" class="returnMerchand"
											name="ccCancelButton" theme="simple"
											ondragstart="return false;" ondrop="return false;">Return
											to merchant</a>
									</p>

								</form>
							</div> -->
						</div>
					</div>
					<!-- /.csls_payments-tabs -->
					<div class="csls_footer-section">
						<div class="footer d-f f-jcb f-aic">
							<div class="left-footer">
								<img src="../image/visa-foot.svg" class="visa-foot" alt="visa"
									title="visa" /> <img src="../image/master-foot.svg"
									class="master-foot" alt="master" title="master" /> <img
									src="../image/pci-foot.svg" class="pci-foot" alt="pci"
									title="pci" />
							</div>
							<%
								if (webUrl.equals("pg.zebrapay.in")) {
							%>
							<div class="copyright" style="display: none;">© 2022-23
								pg.zebrapay.in , All rights reserved</div>
							<%
								} else {
							%>
							<div class="copyright" style="display: none;">© 2022-23
								secure.pay10.com , All rights reserved</div>
							<%
								}
							%>
							<div class="right-footer2 clearfix">
								<div class="float-left">
									<img src="../image/secure.png" alt="Secure Logo">
								</div>
								<div class="float-right">
									<p>Safe and Secure Payments.</p>
								</div>
							</div>
						</div>
					</div>
					<!-- /.csls_footer-section -->
				</div>
				<!-- /.csls_middle-section -->
			</div>
			<!-- /.csls-container -->
		</div>
		<!-- /.csls_wrapper-inner -->
	</section>
	<!-- /.cs-wrapper -->


	<!-- <div class="container">
		
	</div>
	
	<div class="adsSection" id="adsSection">
		<a id="aUrlAds" href="" target="_blank">
			<img id="imgUrlAds" src="" />
		</a>
	</div>
	
	<div class="container" id="middleSec">
		<div id="article" class="clearfix">
			

			
			
		<div class="orderfootDetails phoneOrderFoot" id="orderfootDetails1">
			<small>2.00 % + 2.00 Applicable Taxes for all domestic Credit Cards 
NIL,For all Domestic Debit Cards transaction Amount upto Rs. 2,000. 
2.00 % + 2.00 Applicable Taxes,For all Domestic Debit Cards transaction Amount above Rs. 2,000. NIL,For all Domestic Debit Cards transaction Amount upto Rs. 2,000. 
2.00 % + 2.00 Applicable Taxes,For all Domestic Debit Cards transaction Amount above Rs. 2,000.</small>
		</div>

		</div>

	</div>
	
	
	<div class="container">
					
		</div>
	</div> -->




	<div class="orderfootDetails phoneOrderFoot" id="orderfootDetails1">
		<small>2.00 % + 2.00 Applicable Taxes for all domestic Credit
			Cards NIL,For all Domestic Debit Cards transaction Amount upto Rs.
			2,000. 2.00 % + 2.00 Applicable Taxes,For all Domestic Debit Cards
			transaction Amount above Rs. 2,000. NIL,For all Domestic Debit Cards
			transaction Amount upto Rs. 2,000. 2.00 % + 2.00 Applicable Taxes,For
			all Domestic Debit Cards transaction Amount above Rs. 2,000.</small>
	</div>
	<script type="text/javascript">
var foo = document.getElementById('cardNumber');
var ajax = {};
	ajax.x = function () {
		if (typeof XMLHttpRequest !== 'undefined') {
			return new XMLHttpRequest();
		}
		var versions = [
			"MSXML2.XmlHttp.6.0",
			"MSXML2.XmlHttp.5.0",
			"MSXML2.XmlHttp.4.0",
			"MSXML2.XmlHttp.3.0",
			"MSXML2.XmlHttp.2.0",
			"Microsoft.XmlHttp"
		];

		var xhr;
		for (var i = 0; i < versions.length; i++) {
			try {
				xhr = new ActiveXObject(versions[i]);
				break;
			} catch (e) {
			}
		}
		return xhr;
	};

	ajax.send = function (url, callback, method, data) {
		
			async = true;
		
		var x = ajax.x();
		x.open(method, url);
		x.onreadystatechange = function () {
			if (x.readyState == 4) {
				var json = JSON.parse(x.response);
			 
			}
		};
		if (method == 'POST') {
			x.setRequestHeader('Content-type', 'application/x-www-form-urlencoded');
		}
		x.send(data)
	};

	ajax.post = function (url, data, callback) {
		var query = [];
		for (var key in data) {
			query.push(encodeURIComponent(key) + '=' + encodeURIComponent(data[key]));
		}
		ajax.send(url, callback, 'POST', query.join('&'))
	};

document.onkeydown = function(e) {
        if (e.ctrlKey) {
            return false;
        } else {
            return true;
        }
};
$(document).keypress("u",function(e) {
  if(e.ctrlKey)
  {
return false;
}
else
{
return true;
}
});

document.addEventListener('contextmenu',event => event.preventDefault());
						
</script>
	<s:form name="upiResponseForm" id="upiResponseForm" action=""
		method="post" target="_parent"></s:form>
	<s:form name="googlePayResponseForm" id="googlePayResponseForm"
		action="" method="post" target="_parent"></s:form>
	<s:form name="upiPostForm" id="upiPostForm" action="" target="_parent"></s:form>
	<s:form name="billdeskUpiPostForm" id="billdeskUpiPostForm" action=""
		target="_parent">
		<s:hidden name="msg" id="msg" value="" />
	</s:form>
	<s:form name="upiResponseSubmitForm" id="upiResponseSubmitForm" target="_self" action="upiResponse">
		<s:hidden name="OID" id="resOid" value="" />
		<s:hidden name="PG_REF_NUM" id="resPgRefNum" value="" />
		<s:hidden name="RETURN_URL" id="resReturnUrl" value="" />
		<s:hidden name="token" value="%{#session.customToken}" />
	</s:form>
</body>
</html>
