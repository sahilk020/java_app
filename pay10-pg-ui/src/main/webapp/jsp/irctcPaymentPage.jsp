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
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<link rel="icon" href="../image/98x98.png">
<!-- <link rel="icon" href="favicon.png"> -->
<title>PG Payment Page</title>

<script src="../js/jquery.min.js"></script>
<script src="../js/bootstrap5.js"></script>
<link rel="stylesheet" href="../css/irctcSurchargePaymentPage.css">
<link rel="stylesheet" href="../css/bootstrap5.css">
<script src="../js/google.js"></script>
<script type="text/javascript">
	function googleTranslateElementInit() {
			new google.translate.TranslateElement({ pageLanguage: 'en', includedLanguages: "hi,en,as,bn,bho,doi,gu,mai,ml,mr,mni-Mtei,lus,or,pa,sa,sd,ta,te,ur" }, 'google_translate_element');
	}
	


	setTimeout(() => {
		var	source = document.querySelector('.goog-te-combo');  
		source.addEventListener('change', function (event) {  
			setTimeout(() => {
				if(event.target.value=="bho"){
			$('#header strong font')[4].style.marginLeft="50px";
			}
			if(event.target.value=="doi"){
			$('#header strong font')[4].style.marginLeft="30px";
			}
			if(event.target.value=="mai"){
			$('#header strong font')[4].style.marginLeft="40px";
			}
			if(event.target.value=="lus"){
			$('#header strong font')[4].style.marginLeft="40px";
			}
			debugger
		}, 2000);
       }); 
	   $('#header strong font')[4].style.marginLeft="60px";
	   googleTranslateElementInit();
}, 2000);
</script>

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
.border-corner{
	border-radius: 4px;
}
.custom-control1 {
        position: relative !important;
        z-index: 1 !important;
        display: block !important;
        min-height: 1rem !important;
       text-align: center;
	   border: 1px solid #ddd;
	   border-radius: 4px;
	   cursor: pointer;

}

.img-responsive-img{
	height: 0.5rem !important;
	width: 155px !important;
}
.custom-control-input:focus~.custom-control-label::before {
    box-shadow: none;
}
.tnccheck{
    margin-left: 0.35vw!important;
    margin-top: 0.3vw!important;
}
div#wwgrp_cardsaveflag1 {
    display: inline-block !important;
    margin-left: 10px;
}
.modal {
  overflow: hidden;
}

.modal-body {
  height: 400px;
  overflow: auto;
}
#QRIMG{text-align:center;}
/* .abc{
display: inline-flex !important;
} */
.qrcodeLoader .approvedNotification {
    text-align: center;
    position: absolute;
    right: 43px !important;
    top: 24% !important;
    width: 100%;
    color: #fff;
    display: none;
}

iframe#\:1\.container{
	visibility: hidden !important;
}
.VIpgJd-ZVi9od-aZ2wEe-wOHMyf-ti6hGc{
	display: none;
}
.goog-te-gadget .goog-te-combo {
    display: block;
    padding: 0.375rem 0.75rem;
    border-radius: 0.25rem;
    padding: 3px;
	margin-left: 40px;
	font-size: 10px;
    border: none;
	border-bottom: 1px solid black !important;
}
.skiptranslate.goog-te-gadget{
top: 23px;
    position: absolute;
    display: inline;
    width: 20px;
}
.goog-te-combo option[value='af']{
	display: none;
}
label#lblSessionTime{
	width:100px;
	font-size: 12px !important;
}
.logo-ssn {
    text-align: left !important;
}
</style>
<script type="text/javascript" src="../js/irctcSurchargePaymentPage.js"></script>
<script type="text/javascript">
var tabhide=0;
var timeInterval;
var timer = setInterval(function() {
	debugger
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
	document.getElementById("lblSessionTimeMobile").innerText = t;
	//document.getElementById("lblSessionTimeMobilesecond").innerText = t;

	return "You session will timeout in " + t + " minutes.";
	// return "You session will timeout in " + t + " minutes.";
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

<script type="text/javascript">
$(document).ready(function(){
	
	
	var payid='<%= session.getAttribute(FieldType.PAY_ID.getName()) %>';
	var invoiceNo='<%= session.getAttribute(FieldType.INVOICE_NO.getName()) %>';
	$.post("/crm/jsp/promotionalInvoiceMessage",{
	payid:payid,
		},function(result){
	
	if(result.payMessage!=null&&result.payMessage!=""){
        var payMessage=result.payMessage;
        payMessage = payMessage.replace("#invoiceNo", invoiceNo);
        if(payMessage.includes("Invoice no.null")){
        	var start=payMessage.indexOf("(as");
        	var end=payMessage.indexOf(")",start);
        	var partMessage=payMessage.substring(start, end+1);
        	payMessage=payMessage.replace(partMessage,"");
          }
		$("#payMessage").text(payMessage);
	}

	
if(result.tncStatus==true){
	debugger
	var consentdiv = document.getElementsByClassName('abc');
	for(var i=0; i<consentdiv.length; i++) { 
		consentdiv[i].style.display = "inline-flex";
		}
	var elements = document.getElementsByClassName('tnccheck');
	for(var i=0; i<elements.length; i++) { 
	  elements[i].checked = true;
	  elements[i].disabled=true;
	}
	//$("#tnc").prop("disabled",true);
	
}else{
	var consentdiv = document.getElementsByClassName('abc');
	for(var i=0; i<consentdiv.length; i++) { 
		consentdiv[i].style.display = "none";
		}
}

});
	
});
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
		endDate = DateUtils.addMinutes(endDate,7);
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

</style>
		<div class="loader loaderNoscripts">
			<div class="loaderInner" align="center"></div>
			<div id="approvedNotification" class="approvedNotification">
				<h3>Please enable javascript in your browser settings</h3>
			</div>
		</div>
	</noscript>

	<div class="csls_loader-wrapper active-loader d-f f-jcc f-aic" style="display: none;">
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

<div class="loader loader1 qrcodeLoader" id="loadingRedirectqr">
		<div id="approvedNotificationRedirectqr" class="approvedNotification">
			<h3> QRCODE service...</h3>
			<p>Do not refresh this page or press back button</p>
<div id="QRIMG"  ></div>
<div id="QRfot">
											<img src="../assets/images/Frame 32.svg" alt="Pay10 Logo"style="
    margin-top: 44px;
" />

		</div>
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

    <!------- Mobile Home Page ------->
    <!-- <div class="mob-container" id="mobhome">
        <section class="mob-header">
            <div class="header-topbar mx-3 pt-3 pb-1 d-flex align-items-center justify-content-between">
                <div class="col logo-wrap d-flex align-items-center flex-fill">
					
                    <div class="logo">
                        <img src="../assets/images/Mask Group.svg" alt="Pay10 Logo">
                    </div>
                    <div class="logo-title">
                        <span>Miriyam Saree Store</span>
                    </div>
                </div>
                <div class="col logo-time d-flex justify-content-end flex-fill">01:59 min</div>
            </div>
            <div class="price-section mx-3 pt-3">
                <div class="price-wrap">
                    <div class="lists d-flex justify-content-between py-2">
                        <span>Total Amount</span>
                        <div class="price">
                            <img src="../assets/images/Vector.svg" alt="Rupees">
                            <span>1650.00</span>
                        </div>
                    </div>
                    <div class="lists d-flex justify-content-between py-2">
                        <div class="product">
                            <span>Product</span>
                            <img src="../assets/images/Vectorarrow.svg" alt="Arrow">
                        </div>
                        <div class="price">
                            <span>3 items</span>
                        </div>
                    </div>
                    <div class="lists d-flex justify-content-between py-2">
                        <span>Order Number</span>
                        <div class="price">
                            <span>123456789</span>
                        </div>
                    </div>
                    <div class="lists d-flex justify-content-between py-2">
                        <span>Shipping & Handling</span>
                        <div class="price">
                            <img src="../assets/images/Vector.svg" alt="Rupees">
                            <span>25.00</span>
                        </div>
                    </div>
                    <div class="lists d-flex justify-content-between py-2">
                        <span>GST</span>
                        <div class="price">
                            <img src="../assets/images/Vector.svg" alt="Rupees">
                            <span>05.00</span>
                        </div>
                    </div>
                    <div class="lists d-flex justify-content-between py-2">
                        <span>Other Charges</span>
                        <div class="price">
                            <img src="../assets/images/Vector.svg" alt="Rupees">
                            <span>15.00</span>
                        </div>
                    </div>
                    <div class="lists d-flex justify-content-end py-2">
                        <div class="totalprice d-flex align-items-center">
                            <img src="../assets/images/Vector.svg" alt="Rupees">
                            <span>1695.00</span>
                        </div>
                    </div>
                </div>
            </div>
        </section>

        <section class="mobmenus">
            <div class="menu-wrap mx-3 pt-3">
                <ul class="m-0 p-0 list-unstyled">
                    <li onclick="getmobpage('creditPage','paycredit','credit-menu')" class="d-flex justify-content-between py-3 align-items-center" >
                        <div class="menu-item d-flex align-items-center">
                            <img src="../assets/images/IconsBackground.svg" alt="Credit Card" />
                            <div class="itemname" >Credit Card</div>
                        </div>
                        <div class="itemarrow">
                            <img src="../assets/images/Shape.png" alt="Item Arrow" />
                        </div>
                    </li>
                    <li onclick="getmobpage('debitPage','paycredit','debit-menu')" class="d-flex justify-content-between py-3 align-items-center" >
                        <div class="menu-item d-flex align-items-center">
                            <img src="../assets/images/Icons Background(1).svg" alt="Debit Card" />
                            <div class="itemname" >Debit Card</div>
                        </div>
                        <div class="itemarrow">
                            <img src="../assets/images/Shape.png" alt="Item Arrow" />
                        </div>
                    </li>
                    <li onclick="getmobpage('upiPage','payupi','upi-menu')" class="d-flex justify-content-between py-3 align-items-center">
                        <div class="menu-item d-flex align-items-center">
                            <img src="../assets/images/Icons Background (2).svg" alt="UPI" />
                            <div class="itemname" >UPI</div>
                        </div>
                        <div class="itemarrow">
                            <img src="../assets/images/Shape.png" alt="Item Arrow" />
                        </div>
                    </li>
                    <li onclick="getmobpage('walletPage','paywallet','wallet-menu')" class="d-flex justify-content-between py-3 align-items-center">
                        <div class="menu-item d-flex align-items-center">
                            <img src="../assets/images/Icons Background (3).svg" alt="wallet" />
                            <div class="itemname" >Wallet</div>
                        </div>
                        <div class="itemarrow">
                            <img src="../assets/images/Shape.png" alt="Item Arrow" />
                        </div>
                    </li>
                    <li onclick="getmobpage('netbankingPage','paynetbanking','netbanking-menu')" class="d-flex justify-content-between py-3 align-items-center">
                        <div class="menu-item d-flex align-items-center">
                            <img src="../assets/images/Icons Background (4).svg" alt="Net Banking" />
                            <div class="itemname" >Net Banking</div>
                        </div>
                        <div class="itemarrow">
                            <img src="../assets/images/Shape.png" alt="Item Arrow" />
                        </div>
                    </li>
                    <li onclick="getmobpage('emiPage','','emi-menu')" class="d-flex justify-content-between py-3 align-items-center">
                        <div class="menu-item d-flex align-items-center">
                            <img src="../assets/images/Icons Background (5).svg" alt="EMI" />
                            <div class="itemname" >EMI</div>
                        </div>
                        <div class="itemarrow">
                            <img src="../assets/images/Shape.png" alt="Item Arrow" />
                        </div>
                    </li>
                    <li onclick="getmobpage('bnplPage','','credit-menu')" class="d-flex justify-content-between py-3 align-items-center">
                        <div class="menu-item d-flex align-items-center">
                            <img src="../assets/images/Bnpl icon.svg" alt="BNPL" />
                            <div class="itemname" >Book Now Pay Later (BNPL)</div>
                        </div>
                        <div class="itemarrow">
                            <img src="../assets/images/Shape.png" alt="Item Arrow" />
                        </div>
                    </li>
                </ul>
            </div>
        </section> 

        <section class="mob-footer mt-5 pb-3">
            <div class="footer-wrap mx-3 pt-3 pb-1 d-flex justify-content-center">
                <img src="../assets/images/Frame 32.svg" alt="Pay10 Logo" />
            </div>
        </section>
    </div> -->
	<section class="csls_wrapper" id="allcontents">
		<div class="container ">
			<div class="csls_container d-f f-jcb">
			
				<!-- /.csls-sidebar -->
				<div class="csls_main-section">
					<div id="retryMessage" style="margin-bottom: 0"></div>
					
					
			
					<div class="row">
						<div class="col-lg-9 mobile-view-header">
							<section class="mob-header" id="mobileviewFirst">
								<div class="header-topbar mx-3 pt-3 pb-1 d-flex align-items-center justify-content-between">
									<div class="col logo-wrap d-flex align-items-center flex-fill">
														<!-- <div class="logo">
											<img src="../assets/images/Mask Group.svg" alt="Pay10 Logo">
										</div>
										<div class="logo-title">
											<span>Miriyam Saree Store</span>
										</div> -->
										<%!String webUrl="";%>
										<%
										if (webUrl.equals("pg.zebrapay.in")) {
									%>
									<span class="logo" id="logoHeaderMob" style="display: none;">
										<img src="../image/PP_ZebraPay_Logo.jpg" alt="Zebrapay Logo"
										title="Zebrapay Logo">
									</span>
		
									<%
										} else {
									%>
									<span class="logo">
										<img src="../image/new-payment-page-logo.png" alt="pay10 Logo" title="pay10 Logo" width="77" height="47"/>
									</span>
									<%
										}
									%>
		
									</div>
									<div class="col logo-time d-flex justify-content-end flex-fill notranslate"><s:label name="lblSessionTimeMobile"
										id="lblSessionTimeMobile"></s:label>&nbsp; min</div>
								</div>
								<div class="price-section mx-3 pt-3" id="pricesectionmobile">
									<div class="price-wrap">
										<div class="lists d-flex justify-content-between py-2">
											<!-- <span id="totalAmountName">Amount payable</span> -->
											<div class="price">
												<!-- <img src="../assets/images/Vector.svg" alt="Rupees"> -->
												<!-- <span  id="totalAmount"></span> -->
											</div>
										</div>
										<div class="lists d-flex justify-content-between py-2">
											<span >Buyer</span>
											<div class="price">
												<!-- <img src="../assets/images/Vector.svg" alt="Rupees"> -->
												<span  id="customerNam"><%=ESAPI.encoder().encodeForHTML((String) session.getAttribute(FieldType.CUST_NAME.getName()))%></span>
											</div>
										</div>
										
										<!-- <div class="lists d-flex justify-content-between py-2">
											<div class="product">
												<span>Product</span>
												<img src="../assets/images/Vectorarrow.svg" alt="Arrow">
											</div>
											<div class="price">
												<span>3 items</span>
											</div>
										</div> -->
										<div class="lists d-flex justify-content-between py-2">
											<span>Order Id</span>
											<div class="price">
												<span><%=ESAPI.encoder().encodeForHTML((String) session.getAttribute(FieldType.ORDER_ID.getName()))%></span>
											</div>
										</div>
										
										<div class="lists d-flex justify-content-between py-2">
											<span >Surcharge Amount + GST</span>
											<div class="price">
												<!-- <img src="../assets/images/Vector.svg" alt="Rupees"> -->
												<span  id="surchargeAmountMob">0.00</span>
											</div>
										</div>
										<!-- <div class="lists d-flex justify-content-between py-2">
											<span>Shipping & Handling</span>
											<div class="price">
												<img src="../assets/images/Vector.svg" alt="Rupees">
												<span>25.00</span>
											</div>
										</div> -->
										<div class="lists d-flex justify-content-between py-2">
											<!-- <span id="moreInfo">Surcharge Amount + GST</span> -->
											<div class="price">
												<!-- <img src="../assets/images/Vector.svg" alt="Rupees"> -->
												<!-- <span id="moreInfo" id="surchargeAmount" name="surchargeAmount">0.00</span> -->
											</div>
										</div>
										<!-- <div class="lists d-flex justify-content-between py-2">
											<span>Other Charges</span>
											<div class="price">
												<img src="../assets/images/Vector.svg" alt="Rupees">
												<span>15.00</span>
											</div>
										</div> -->
										<div class="lists d-flex justify-content-end py-2">
											
											<div class="">
												<img src="../assets/images/Vector.svg" alt="Rupees">
												<span id="innerAmountMob">0.00</span>
											</div>
										</div>
									</div>
								</div>
								<a id="backarrow" onclick="BackMobileView();" style="display: none;">
									<img src="../assets/images/chevron-left.svg" alt="Back Arrow" >Back
								</a>
							</section>
					
							<div class="csls_main-header">
								<div id="header" class="clearfix">
									<%
										String urlTmp = request.getRequestURL().toString();
										URL url = new URL(urlTmp);
										webUrl = url.getHost();
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
									<span class="logo">
										<img src="../image/new-payment-page-logo.png" alt="pay10 Logo" title="pay10 Logo" width="77" height="47"/>
									</span>
									<%
										}
									%>
									<input type="hidden" value="<%=servletDate%>" id="unique" /> <span
										class="logo-ssn"> <input type="hidden" id="sessionTime"
										value=${sessionCreationTime}> <strong class="notranslate">
											 <span>Your
												Session will expire in :</span> <s:label name="lblSessionTime"
												id="lblSessionTime"></s:label> min &nbsp; <span id="google_translate_element"></span>
									</strong>
									</span>
									 <!-- <span class="hamburger-icon">  
										<img src="../image/hamburger.png" onclick="classAdded()" alt="">
									</span>  -->
									<span class="logoPhone" style="display: none;"> <img
										src="../image/280x40.png" id="cashlessoLogo"
										alt="Pay10" title="Pay10">
									</span>
								</div>
							</div>
							<div class="row">
								<div class="col-lg-3">
									<div class="csls_payments-tabs">
										<div id="paymentTabs">
											<ul class="paymentNavs clearfix" id="paymentNavs">
											</ul>
										</div>
									</div>
								</div>
								<div class="col-lg-9">
							<!-- /.csls_payments-tabs -->
							<div class="csls_payments-box conent-wrap">
								<div class="paymentSections" id="paymentSections">
									<div id="saveCard" class="tabBox saveCardBox hideBox">
										<s:form autocomplete="off" name="exCard" method="post"
											target="_parent" action="pay" id="exCard">
											<input type="text" style="display: none" />
											<input type="password" style="display: none">
											<input type="hidden" name="paymentType"
												id="saveCardPaymentType" value="EX" />
		
											<ul class="savedCards clearfix">
												<small id="cardSupportedEX" class="text-danger text-left"></small>
												<s:if test="#session.TOKEN=='NA'"></s:if>
												<s:else>
													<s:iterator value="#session.TOKEN" status="incrementer">
														<li class="saveCardDetails">
															<div>
																<div class="card-dtls">
																	<div class="row1 clearfix">
																		<div class="custom-radio custom-control visaRadio">
																			<input class="custom-control-input" name="visa-radio"
																				type="radio" id="tokenId<s:property value="key"/>"
																				onclick="('<s:property value="%{value.mopType}"/>');handleClick(this);"
																				value="<s:property value="key" />"> <label
																				class="custom-control-label"
																				for='tokenId<s:property value="key"/>'> </label>
																		</div>
																		<!-- custom-radio -->
																		<span class="m-top"> <s:textfield
																				autocomplete="off" id="exCardNumber%{key}"
																				cssClass="form-control1 transparent-input saveCardNum"
																				value="%{value.cardMask}" theme="simple"
																				readonly="true" />
																		</span> <a href="#" id="deleteButton<s:property value="key" />"
																			onclick="deleteButton('<s:property value="key" />', this)"
																			class="close"> <img src="../image/close.png"
																			alt="Close Icon" width="13" height="13" />
																		</a>
																	</div>
																	<!-- row1 -->
																	<div class="row2">
																		<div class="savedCvv">
																			<s:textfield type="password" id="ccvvNumber%{key}"
																				maxlength="3" onblur="showPlaceholder(this)"
																				onfocusout="saveCardCheckCvv(this)" autocomplete="off"
																				onkeypress="return isNumberKey(event);"
																				onkeyup="enableExButton(this);"
																				class="pField savDetailsCvv change saveCardCVV" disabled="true"
																				placeholder="CVV" aria-placeholder="CVV" />
																			<input type="text" name="cvv" style="display: none;">
																		</div>
																		<small class="text-muted"> <s:if
																				test="%{value.paymentType=='CC'}">Credit Card</s:if> <s:if
																				test="%{value.paymentType=='DC'}">Debit Card</s:if> <s:if
																				test="%{value.paymentType=='PC'}">Prepaid Card</s:if>
																		</small> <label class="card-logo"
																			for="tokenId<s:property value="key" />"> <img
																			class="saveMobImg"
																			src="../image/${value.mopType.toLowerCase()}.png"
																			alt="${value.mopType.toLowerCase()}">
																		</label>
																	</div>
																	<!-- row2 -->
																</div>
																<!-- card-details -->
															</div>
														</li>
														<!-- save card details -->
													</s:iterator>
												</s:else>
											</ul>
											<input type="hidden" name="cvvNumber" value=""
												id="currentCvvInput">
											<input type="hidden" name="amount" value=""
												id="orderTotalAmountInput">
											<input type="hidden" name="tokenId" value=""
												id="currentTokenIdInput">
											<input type="hidden" name="mopType" value=""
												id="savedCardMopType">
											<input type="hidden" name="cardNumber" value=""
												id="savedCardNumber">
		
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
												<!-- <a href="#" onclick="myCancelAction();" key="ReturnToMerchant"
													id="ccCancelButton" class="float-save" name="ccCancelButton"
													theme="simple" ondragstart="return false;"
													ondrop="return false;">Return to merchant</a> -->
											</div>
		
											<small class="CTANote" style="display: none;"> </small>
		
											<s:hidden name="customToken" id="customToken1"
												value="%{#session.customToken}"></s:hidden>
										</s:form>
		
									</div>
		
									<div id="debitWithPin" class="tabBox mob-bottom hideBox form-fields px-4 py-3">
									
										<s:form name="creditcard-form" method="post" target="_parent"
											action="pay" id="creditCard" class="row">
											<input type="text" style="display: none" />
											<input type="password" style="display: none">
											<input type="text" style="display: none" />
											<input type="password" style="display: none">
											<div class="cardSection">
												<div class="allCreditCard" id="credit_cards"></div>
												<div class="allCreditCard" id="debit_cards"></div>
												<div class="allCreditCard" id="prepaid_cards"></div>

												<div class="cardNumber1 mrgnbtn11" id="divCardNumber">
													<s:set var="valid cardnumber" value="getText('CardNumber')" />
													<s:hidden id="cardPlaceHolderCC" value="%{cardNumberTextCC}" />
													<s:hidden id="BROWSERDEVICECARD" name="BROWSER_DEVICE" value="" />
													<s:hidden id="validCardDetail" value="%{valid cardnumber}" />
													<s:hidden id="paymentType2" name="paymentType" value="" />
													<s:hidden id="mopTypeCCDiv2" name="mopType" value="" />
													<s:hidden id="issuerBankName2" name="issuerBankName" value="" />
													<s:hidden id="issuerCountry2" name="issuerCountry" value="" />
													<s:hidden id="cardHolderTypeId" name="cardHolderType"
														value="" />
													<s:hidden id="paymentsRegionId" name="paymentsRegion" value="" />
													
														
														<s:hidden id="browserLanguage" name="browserLanguage" value="" />
														<s:hidden id="browserScreenHeight" name="browserScreenHeight" value="" />
														<s:hidden id="browserColorDepth" name="browserColorDepth" value="" />
														<s:hidden id="browserJavaEnabled" name="browserJavaEnabled" value="" />
														<s:hidden id="browserScreenWidth" name="browserScreenWidth" value="" />
														<s:hidden id="browserTZ" name="browserTZ" value="" />
														<div class="col-12">
															<label for="cardnumber" class="form-label m-0">Card Number</label>
															<div class="form-text mb-2">Enter the 13 to 19 digit card number, on your card</div>
															<input type="tel" class="userCardNumber cardNumber pField masked inputField form-control text-center" id="cardNumber" 
															 placeholder="0000 0000 0000 0000 000" onkeyup="enterCardNum(event); enterCardNumRmvErrMsg();formatAndSetCcNumber();"
															onfocusout="checkLuhn(this, event);" onblur="checkCardSupported(event);formatAndSetCcNumber2();" name="cardNumber"
															onkeydown="keydownHandler(event); removeEnterCardMsg();" onpaste="return false;" oninput="inputHandler(event)"
															maxlength="23" inputmode="numeric">
															<div class="resultDiv">
																<p id="checkStartNo" class="text-danger1">Invalid Card
																	Number</p>
																<p id="validCardCheck" class="text-danger1">Invalid Card
																	Number</p>
																<p id="emptyCardNumber" class="text-danger1">Please
																	Enter Card Number</p>
																<p id="notSupportedCard" class="text-danger1">Card Not
																	Supported</p>
															</div>
														
															<div class="userMoptype">
																<img id="userMoptypeIcon" src="../image/bc.png" />
															</div>
															</div>
															<div class="col-12">
																<div class="row justify-content-between align-items-center flex-wrap">
																	<div class="col-xl-8 col-lg-12">
																		<label for="cvvnumber" class="col-form-label">CVV</label>
																		<p class="form-text">
																			Enter 3 or 4 digit number on your card( front side for amex, back side for others). Security code for Amex.
																		</p>
																	</div>
																	<div class="col-xl-4 col-lg-12 cvv" id="divCvv">
																		<input autocomplete="nope" class="pField inputField form-control text-center"
																			type="password" inputmode="numeric" name="cvvNumber"
																			value="" id="cvvNumber" placeholder="***" onchange="formatCvvNumber()" onkeypress="return isNumberKey(event)"
																			onkeyup="enterCardNum(); removeCvvError(); numberEnterPhone(this);formatCvvNumber();"
																			onfocusout="checkCvvFocusOut();" onpaste="return false"
																			maxlength="4" ondrop="return false">
																		<div class="cvvValidate">
																			<p id="cvvValidate" class="text-danger1 ">Invalid CVV</p>
																			<p id="emptyCvv" class="text-danger1">Please Enter CVV</p>
																		</div>
																	</div>
																</div>
															</div>
															<div class="col-12">
																<div class="row justify-content-between align-items-center flex-wrap">
																	<div class="col-xl-8 col-lg-12">
																		<label for="expirydate" class="col-form-label">Expiry</label>
																		<p class="form-text">
																			Enter the expiry date on the card
																		</p>
																	</div>
																	<div class="col-xl-4 col-lg-12 validity" id="validity">
																		<div class="row">
																			<div class="col">
																				<input type="text" id="expirymonth" 
																				onchange="formatMonth()" class="text-center form-control inputField validityDateCard" pattern="(1[0-2]|0[1-9])"
																				placeholder="MM" onkeyup="enterCardNum(); removeMmDdError();formatMonth();"
																				onkeydown="return numOnly(event,this);" maxlength="2" minlength="2" onblur="CheckExpiryOnBlur();monthautoset();">
																			</div>
																			<div class="col">
																				<input type="text" id="expiryyear"
																				onchange="formatYear()"  class="text-center form-control inputField validityDateCard" placeholder="YY" 
																				onkeyup="enterCardNum(); removeMmDdError();formatYear();" maxlength="2"
                                                                                      onkeydown="return numOnly(event,this);" onblur="CheckExpiryOnBlur();">
																					  <input type="hidden" name="expiryYear" value=""
                                                                                      id="setExpiryYear"> <input type="hidden"
                                                                                      name="expiryMonth" value="" id="setExpiryMonth">
																					  
																					  <input type="hidden" id="paymentDate" name="ExpirationDate"
                                                                                      inputmode="numeric" 
                                                                                    
                                                                                      class="inputField validityDateCard"
                                                                                      onkeyup="enterCardNum(); removeMmDdError();"
                                                                                      onkeydown="return numOnly(event,this);"
                                                                                      onblur="CheckExpiryOnBlur()" maxlength="5">
																					  
																			</div>
																			
																		</div>
																		<div class="row">
																			<div class="col-lg-12">
																		<div class="validExpDate">
																			<p id="validExpDate" class="text-danger1 ">Invalid Expiry Date</p>
																			<p id="emptyExpiry" class="text-danger1">Please Enter
																				Expiry</p>
																				</div>
																				</div>
																		</div>
																	</div>
																</div>
															</div>
													
		
		
															 <!-- <div class="validity" id="validity">
                                                                                <input type="text" id="paymentDate" name="ExpirationDate"
                                                                                      inputmode="numeric" placeholder=" "
                                                                                      data-placeholder="MM/YY" pattern="(1[0-2]|0[1-9])\/\d\d"
                                                                                      data-valid-example="11/18"
                                                                                      class="inputField validityDateCard"
                                                                                      onkeyup="enterCardNum(); removeMmDdError();"
                                                                                      onkeydown="return numOnly(event,this);"
                                                                                      onblur="CheckExpiryOnBlur()" maxlength="5"
                                                                                      onpaste="return false" ondrop="return false">
                                                                                <span class="placeHolderText placeHolderTextExpDate">MM/YY</span>
                                                                                <input type="hidden" name="expiryYear" value=""
                                                                                      id="setExpiryYear"> <input type="hidden"
                                                                                      name="expiryMonth" value="" id="setExpiryMonth">
                                                                          </div>  -->

		
		
		
		
		
													<!-- <input id="cardNumber" name="cardNumber"
														class="userCardNumber cardNumber pField masked inputField"
														type="tel" value="" placeholder=" "
														onkeyup="enterCardNum(event); enterCardNumRmvErrMsg();"
														onfocusout="checkLuhn(this, event);"
														onblur="checkCardSupported(event);"
														onkeydown="keydownHandler(event); removeEnterCardMsg();"
														onpaste="return false;" oninput="inputHandler(event)" 
														maxlength="23" inputmode="numeric">-->
														 <!-- <span
														class="placeHolderText placeHolderTextCardNum">Card
														Number</span> -->
													
													<!-- <div class="resultDiv">
														<p id="checkStartNo" class="text-danger1">Invalid Card
															Number</p>
														<p id="validCardCheck" class="text-danger1">Invalid Card
															Number</p>
														<p id="emptyCardNumber" class="text-danger1">Please
															Enter Card Number</p>
														<p id="notSupportedCard" class="text-danger1">Card Not
															Supported</p>
													</div> -->
													<div class="col-12" id="divName">
														<s:set var="NameonCard" value="getText('NameonCard')" />
														<label for="holdername" class="col-form-label m-0">Card Holder Name</label>
														<div class="form-text mb-2">Enter name on the card</div>
														<input type="text" class="pField inputField text-center text-uppercase form-control"  onKeyup="nameCheckKeyUp(); alphabetEnterPhone(this); enterCardNum();setname();" 
														onchange="setname()" onkeydown="alphabetEnterPhone(this);"  maxlength="45" placeholder="Card Holder Name"
														 name="cardName" id="cardName" onkeypress="return isCharacterKeyWithSpace(event);" 
													  onfocusout="nameCheck()" oncopy="return false" onpaste="return false" ondrop="return false">
														<div class="resultDiv"> 
															<p id="nameError" class="text-danger1">Please Enter Name</p> 
															<p id="invalidError" class="text-danger1">Invalid Name</p> </div>
														  </div>
													
												</div>
<!-- 		
												<div id="divName"> 
													<s:set var="NameonCard" value="getText('NameonCard')" />
													 <input class="pField inputField" type="text" name="cardName" id="cardName" onkeypress="return isCharacterKeyWithSpace(event);" 
													 onKeyup="nameCheckKeyUp(); alphabetEnterPhone(this); enterCardNum();" onkeydown="alphabetEnterPhone(this);" placeholder=" "
													  onfocusout="nameCheck()" oncopy="return false" onpaste="return false" ondrop="return false">
													   <span class="placeHolderText placeHolderTextNameOnCard">Name on Card</span> 
													   <div class="resultDiv"> 
														   <p id="nameError" class="text-danger1">Please Enter Name</p> 
														   <p id="invalidError" class="text-danger1">Invalid Name</p> </div>
														 </div> -->
												<!-- <div class="clearfix validityMain">
													<div class="validity" id="validity">
														<input type="text" id="paymentDate" name="ExpirationDate"
															inputmode="numeric" placeholder=" "
															data-placeholder="MM/YY" pattern="(1[0-2]|0[1-9])\/\d\d"
															data-valid-example="11/18"
															class="inputField validityDateCard"
															onkeyup="enterCardNum(); removeMmDdError();"
															onkeydown="return numOnly(event,this);"
															onblur="CheckExpiryOnBlur()" maxlength="5"
															onpaste="return false" ondrop="return false">
														<span class="placeHolderText placeHolderTextExpDate">MM/YY</span>
														<input type="hidden" name="expiryYear" value=""
															id="setExpiryYear"> <input type="hidden"
															name="expiryMonth" value="" id="setExpiryMonth">
													</div> -->
		
		
													<!-- <div class="cvv" id="divCvv"> -->
														<!-- <input autocomplete="nope" class="pField inputField"
															type="password" inputmode="numeric" name="cvvNumber"
															value="" placeholder=" " id="cvvNumber"
															onkeypress="return isNumberKey(event)"
															onkeyup="enterCardNum(); removeCvvError(); numberEnterPhone(this)"
															onfocusout="checkCvvFocusOut();" onpaste="return false"
															maxlength="3" ondrop="return false">
														<span class="placeHolderText placeHolderTextCvv">CVV</span> -->
														<!-- <img class="info" onmouseover="toolTipCvv('block')" onmouseout="toolTipCvv('none')" src="../image/info.png"/> -->
														<!-- <img class="info" src="../image/cvv-icon.png" alt="/">
														<div class="whatIsCvv" id="whatIsCvv">
															<h2>What is CVV?</h2>
															<div class="clearfix">
																<div class="float-left">
																	<img src="../image/cardCvv.png" />
																</div>
																<p class="float-right">The CVV number is the last
																	three digits on the back of your card</p>
															</div>
															<img class="leftAerrow" src="../image/left-aerrow.png" />
														</div> -->
		
													<!-- </div> -->
		
												<!-- </div> -->
												<div class="resultDiv mrgnbtn11 clearfix">
													<div class="validExpDate">
														<p id="validExpDate" class="text-danger1 ">Invalid Date</p>
														<p id="emptyExpiry" class="text-danger1">Please Enter
															Expiry</p>
													</div>
		
													<!-- <div class="cvvValidate">
														<p id="cvvValidate" class="text-danger1 ">Invalid CVV</p>
														<p id="emptyCvv" class="text-danger1">Please Enter CVV</p>
													</div> -->
												</div>
		
		
												
		
												<%-- <div id="divSaveCard">
													<input type="" name="cardsaveflag1"
														id="cardsaveflag1" onchange=updateCheckBoxValue() style="display: none;"> <span
														class="chk-save"></span>
												</div> --%>
												
												<div class="pRow col-12" id="divSaveCard">
								<s:if test="%{#session.EXPRESS_PAY_FLAG == true}">
								<span class="chk-save">Save Cards Securely for future Payments.</span>
								<s:checkbox name="cardsaveflag" checked="checked"
									id="cardsaveflag1" class="cardsaveflag11"/>
								</s:if>
							</div>
												
		
											</div>
											<div class="padding10" id="mainCTAInfo" style="display: none;">
												<small class="CTAInfo" id="CTAInfo">
												</small>
											</div>
											<div class="csls_footer-section">
												<div class="footer d-f f-jcb f-aic">
													<div class="left-footer">
															<img src="../assets/images/Frame38.svg" class="rupay-foot" alt="rupay"
																title="rupay" />
														<img src="../image/visa-foot.svg" class="visa-foot" alt="visa"
															title="visa" /> <img src="../image/master-foot.svg"
															class="master-foot" alt="master" title="master" /> <a href="./coc.pdf" target="_blank"><img
															src="../image/pci-foot.svg" class="pci-foot" alt="pci"
															title="pci" /></a>
													</div>
													
													<div class="right-footer2 clearfix">
														<div class="float-left">
															<img src="../assets/images/ShieldCheck.svg" alt="Secure Logo" class="secure-shield">
														</div>
														<div class="float-right">
															<p>Safe and Secure Payments.</p>
														</div>
													</div>
												</div>
											</div>
											<div class="row px-4 py-4 w-100">
												<div class="col-lg-12 ">
													<%
													if (webUrl.equals("pg.zebrapay.in")) {
												%>
												<div class="copyright notranslate" style="display: none;">© 2019-21
													pg.zebrapay.in. All rights reserved</div>
												<%
													} else {
												%>
												<div class="copyright notranslate" style="display: none;">© 2019-21
													secure.pay10.com. All rights reserved</div>
												<%
													}
												%>
												</div>
											</div>
												<div class="row abc">
									<s:checkbox id="tnc" class="tnccheck"
																	name="tncStatus"   disabled="true"
																	style="margin-top:0px; height:20px;   font-weight:500; font-size:14px;"></s:checkbox>
															<label for=""> &nbsp; &nbsp;I agree to <a href="#" target=_self
																onClick="pop()">Terms & Conditions</a></label> <span
																class="data-value"> 
															</span>

														</div>
								
											<div class="CTA mob-btn" id="pay-now">
		
												<input type="button" class="payment-btn inactive" id="confirm-purchase"
													onclick="checkFields(event, this);" value="pay">
												<div>
													<small id="errorBox" class="text-danger"></small>
												</div>
											</div>
										
											
		
		
											<%
												String currencyCodeAlpha1 = "";
													String formattedAmount1 = "";
													try {
														String currencyCodeNumeric1 = (String) session.getAttribute(FieldType.CURRENCY_CODE.getName());
														String amount1 = (String) session.getAttribute(FieldType.AMOUNT.getName());
														if (amount1.equals("null")) {
															response.sendRedirect("error");
														}
														currencyCodeAlpha1 = Currency.getAlphabaticCode(currencyCodeNumeric1);
														formattedAmount1 = ESAPI.encoder().encodeForHTML(Amount.toDecimal(amount1, currencyCodeNumeric1));
													} catch (NullPointerException nPException) {
														response.sendRedirect("sessionTimeout");
													} catch (Exception exception) {
		
														response.sendRedirect("error");
													}
											%>
		
		
											<input type="hidden" id="amount" name="amount"
												value=<%=formattedAmount1%>>
											<s:hidden name="customToken" id="customToken"
												value="%{#session.customToken}"></s:hidden>
											<!-- <input type="hidden" id="cardsaveflag" name="cardsaveflag"
												value="false"> -->

												<!-- return to merchant -->
												<!-- <p class="returnMerchand">
													<a href="#" onclick="myCancelAction();" key="ReturnToMerchant"
														id="ccCancelButton" name="ccCancelButton" theme="simple"
														ondragstart="return false;" ondrop="return false;">Return
														to merchant</a>
												</p> -->
										</s:form>
										
									</div>
		
									<div id="upi" class="tabBox upiBox hideBox">

														<hidden id="BROWSERDEVICE"  name="BROWSERDEVICEupi" value="" />
										<div class="containers overflow-hidden upi-img d-f f-aic">
											<div class="row gy-2">
												<div class="col-4 mb-2">
											  <div class="border d-flex justify-content-center align-items-center border-corner">
													<img src="../assets/images/Frame 55.svg" alt="Paytm">
												</div>
											  </div>
												<div class="col-4 mb-2">
											  <div class="border d-flex justify-content-center align-items-center border-corner">
													<img src="../assets/images/Frame 58.svg" alt="PhonePe">
												</div>
											  </div>
												<div class="col-4 mb-2">
											  <div class="border d-flex justify-content-center align-items-center border-corner">
													<img src="../assets/images/Frame 56.svg" alt="AmazonPay">
												</div>
											  </div>
												<div class="col-4 mb-2">
											  <div class="border d-flex justify-content-center align-items-center border-corner">
													<img src="../assets/images/Frame 57.svg" alt="Gpay">
												</div>
											  </div>
												<div class="col-4 mb-2">
											  <div class="border d-flex justify-content-center align-items-center border-corner">
													<img src="../assets/images/image58.svg" alt="Mobikwik">
												</div>
											  </div>
												<div class="col-4 mb-2">
											  <div class="border d-flex justify-content-center align-items-center border-corner">
													<img src="../assets/images/Frame 59.svg" alt="Freecharge">
												</div>
											  </div>
											</div>
										</div>


										<!-- <div class="upi-img d-f f-aic">
											<img src="../image/upi.png" alt="/"> <img
												src="../image/googlePay.png" alt="/"> 
												<img
												src="../image/phonepay.png" alt="/"> <img
												src="../image/paytm.png" alt="/">
										</div> -->
										<!-- /.upi-img -->
										<div class="row">
										<div class="vpaSection col-lg-12">
											<label class="col-form-label">UPI id</label>

												<p for="holdername" class="form-text">Virtual Payment Address</p>
												<input type="text" class=" text-center form-control" onkeyup="isValidVpa(); enableButton();getupid();"
												onkeydown=" return restrictKeyVpa(event,this);" name="VPA" id="vpaCheck" onchange="getupid()"
												onfocusout="isValidVpaOnFocusOut();" oncopy="return false"
												onpaste="return false" ondrop="return false" maxlength="45">
												<div class="resultDiv">
													<p class="red1" id="red1">Invalid VPA</p>
													<p class="red1" id="enterVpa">Please Enter VPA</p>
												</div>
										</div>
										<!-- <div class="resultDiv">
											<p class="red1" id="red1">Invalid VPA</p>
											<p class="red1" id="enterVpa">Please Enter VPA</p>
										</div> -->
										<!-- <label class="vpaPara">VPA is a unique Payment address
											that can be linked to a person's bank account to make payments.</label> -->
											<div class="col-12  mt-2">
											<p class="vpaPara form-text">
												VPA is a unique payment address that can be linked to a persons
												bank account to make payments
											</p>
										</div>
									
										<div class="csls_footer-section">
											<div class="footer d-f f-jcb f-aic">
												<div class="left-footer">
														<img src="../assets/images/Frame38.svg" class="rupay-foot" alt="rupay"
																title="rupay" />
													<img src="../image/visa-foot.svg" class="visa-foot" alt="visa"
														title="visa" /> <img src="../image/master-foot.svg"
														class="master-foot" alt="master" title="master" />  <a href="./coc.pdf" target="_blank"><img
														src="../image/pci-foot.svg" class="pci-foot" alt="pci"
														title="pci" /></a>
												</div>
												
												<div class="right-footer2 clearfix">
													<div class="float-left">
														<img src="../assets/images/ShieldCheck.svg" alt="Secure Logo" class="secure-shield">
													</div>
													<div class="float-right">
														<p>Safe and Secure Payments.</p>
													</div>
												</div>
											</div>
										</div>
										<div class="row px-4 py-4 w-100">
											<div class="col-lg-12 ">
												<%
												if (webUrl.equals("pg.zebrapay.in")) {
											%>
											<div class="copyright" style="display: none;">© 2019-21
												pg.zebrapay.in. All rights reserved</div>
											<%
												} else {
											%>
											<div class="copyright" style="display: none;">© 2019-21
												secure.pay10.com. All rights reserved</div>
											<%
												}
											%>
											</div>
										</div>
									</div>
									
									<div class="row abc">
									<s:checkbox id="tnc" class="tnccheck"
																	name="tncStatus"   disabled="true"
																	style="margin-top:10px;    font-weight:500; font-size:14px;"></s:checkbox>
															<label for="">&nbsp; &nbsp;I agree to <a href="#" target=_self
																onClick="pop()">Terms & Conditions</a></label> <span
																class="data-value"> 
															</span>

														</div>
									
										<div class="CTA mob-btn" id="pay-now">
		
											<input type="button" class="payment-btn" id="upi-sbmt"
												value="Pay" onclick="submitUpiForm()">
											<div>
		
												<small id="errorBox" class="text-danger"></small>
											</div>
										</div>
										<p class="returnMerchand">
											<!-- <a href="#" onclick="myCancelAction();" key="ReturnToMerchant"
												id="ccCancelButton1" name="ccCancelButton" theme="simple"
												ondragstart="return false;" ondrop="return false;">Return
												to merchant</a> -->
										</p>
									</div>
		<div id="QRCODE" class="tabBox QRCODEBox hideBox">
		
  
							
		</div>
									<div id="googlePay" class="tabBox googlePayBox hideBox">
										<form action="upiPay" target="_parent" method="get"
											id="googlePayForm">
											<div class="googlePayDiv">
												<input type="number" name="googlePay" class="inputField"
													id="googlePayNum" placeholder=" "
													onkeydown="return numOnly(event,this);"
													onkeyup="googlePayNumCheck(this);"
													onblur="checkPhoneNo(this)" onpaste="return false"
													maxlength="10"> <span
													class="placeHolderText placeHolderTextgooglePay">Enter
													Phone No</span>
											</div>
											<div class="resultDiv">
												<p class="red1" id="googlePayInvalidNo">Invalid Phone
													Number</p>
												<p class="red1" id="googlePayEnterPhone">Please Enter
													Phone Number</p>
											</div>
		
											<input type="hidden" id="googlePayPhoneNo" name="vpaPhone"
												value=""> <input type="hidden" id="googlePayMobtype"
												name="mopType" value="GP"> <input type="hidden"
												id="googlePayPaymentType" name="paymentType" value="UP">
		
		
											<div>
												<input type="button" id="googlePayBtn" value="pay now"
													class="payment-btn" onclick="submitGooglePayForm()">
											</div>
											<div class="googlePayReturnMerchant">
												<!-- <a href="#" onclick="myCancelAction();" key="ReturnToMerchant"
													id="ccCancelButton" class="float-save" name="ccCancelButton"
													theme="simple" ondragstart="return false;"
													ondrop="return false;">Return to merchant</a> -->
											</div>
										</form>
								
									</div>

									<div id="autoDebit" class="tabBox autoDebitBox hideBox">
										<form action="pay" target="_parent" method="get"
											id="payAutoDebit">
											<div class="autoDebitInner">
												<label> <input type="radio" name="" checked="true">
													<span>Powered By autodebit.in</span>
												</label>
											</div>
		
											<input type="hidden" id="mobTypeAD" name="mopType" value="AD">
											<input type="hidden" id="paymentTypeAd" name="paymentType"
												value="AD"> <input type="hidden" id="amountAD"
												name="amount" value="100">
		
											<div>
												<input type="button" id="autoDebitBtn" value="pay now"
													class="payment-btn-autoDebit" onclick="autoDebitFn()">
											</div>
											<p class="returnMerchand margin0">
												<!-- <a href="#" onclick="myCancelAction();" key="ReturnToMerchant"
													id="ADCancelButton" class="returnMerchand"
													name="ccCancelButton" theme="simple"
													ondragstart="return false;" ondrop="return false;">Return
													to merchant</a> -->
											</p>
										</form>
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
		
									<div id="iMudra" class="tabBox iMudraBox hideBox">
										<div class="form-fields px-4 py-3">
										<form id="imudraFormId" name="imudraFormName" action="pay"
											target="_parent" method="post" autocomplete="off" class="row">
											<div class="row w-100">
												<div class="col-12">
													<label for="cvvnumber" class="col-form-label">Select Payment Method</label>
													<div class="radio-list wallet"></div>
													<div class="resultDiv">
														
														<p class="red1" id="walletpaymentMethodErr">Please select payment method</p>
													</div>
												</div>
											</div>
											<input type="hidden" id="BROWSERDEVICEWL" value="" name="BROWSER_DEVICE"> 
											<input type="hidden" name="paymentType" value="WL"> <input
												type="hidden" name="mopType" value="APWL"> <input
												type="hidden" id="amountImudra" name="amount" value="">
												<div class="col-12">
													<div class="row justify-content-between align-items-center flex-wrap vpaSection">
														<div class="col-12">
															<label for="cvvnumber" class="col-form-label">Phone Number</label>
															<p class="form-text">
																Enter your phone number here
															</p>
														</div>
														<div class="row">
															<div class="col-12">
																<input type="text"  name="mobile_number" id="vpaCheck1" maxlength="10" class="form-control text-center inputField" placeholder="0000000000" onkeyup="phoneformat()"
																onblur="phoneformat()" oncopy="return false" onpaste="return false" ondrop="return false">
																<div class="resultDiv">
																	<p class="red1" id="red1">Invalid Mobile Number</p>
																	<p class="red1" id="mobileErr">Please Enter valid Mobile Number</p>
																</div>
															</div>
															<div class="col-12  mt-2">
																<p class="form-text vpaPara">
																	Mobile number is a unique identifier that can be linked to a persons wallet number to make payments
																</p>
															</div>
														</div>
													</div>
												</div>
												<div class="csls_footer-section">
													<div class="footer d-f f-jcb f-aic">
														<div class="left-footer">
																<img src="../assets/images/Frame38.svg" class="rupay-foot" alt="rupay"
																title="rupay" />
															<img src="../image/visa-foot.svg" class="visa-foot" alt="visa"
																title="visa" /> <img src="../image/master-foot.svg"
																class="master-foot" alt="master" title="master" />  <a href="./coc.pdf" target="_blank"><img
																src="../image/pci-foot.svg" class="pci-foot" alt="pci"
																title="pci" /></a>
														</div>
														
														<div class="right-footer2 clearfix">
															<div class="float-left">
																<img src="../assets/images/ShieldCheck.svg" alt="Secure Logo" class="secure-shield">
															</div>
															<div class="float-right">
																<p>Safe and Secure Payments.</p>
															</div>
														</div>
													</div>
												</div>
													<div class="row px-4 py-4 w-100">
														<div class="col-lg-12 ">
															<%
															if (webUrl.equals("pg.zebrapay.in")) {
														%>
														<div class="copyright" style="display: none;">© 2019-21
															pg.zebrapay.in. All rights reserved</div>
														<%
															} else {
														%>
														<div class="copyright" style="display: none;">© 2019-21
															secure.pay10.com. All rights reserved</div>
														<%
															}
														%>
														</div>
													</div>
													<!-- <div class="vpaSection">
														<input type="text" name="mobile_number" id="vpaCheck"
															placeholder=" " class="inputField" oncopy="return false"
															onpaste="return false" ondrop="return false">
														<span class="placeHolderText placeHolderTextVPA">Enter
															Mobile Number</span>
													</div>
													<div class="resultDiv">
														<p class="red1" id="red1">Invalid Mobile Number</p>
														<p class="red1" id="enterVpa">Please Enter Mobile Number</p>
													</div>
													<label class="vpaPara">Mobile Number is a unique
														identifier that can be linked to a person's wallet number to
														make payments.</label> -->

		<div class="row abc">
									<s:checkbox id="tnc" class="tnccheck"
																	name="tncStatus"   disabled="true"
																	style="margin-top:10px;    font-weight:500; font-size:14px;"></s:checkbox>
															<label for="">&nbsp; &nbsp;I agree to <a href="#" target=_self
																onClick="pop()">Terms & Conditions</a></label> <span
																class="data-value"> 
															</span>

														</div>
														
												<div class="CTA mob-btn">
													<input type="button" id="iMudraBtn" value="pay now"
													class="payment-btn" onclick="submitImudra()">
												</div>
												<p class="returnMerchand margin0">
													<!-- <a href="#" onclick="myCancelAction();" key="ReturnToMerchant"
														id="ADCancelButton" class="returnMerchand"
														name="ccCancelButton" theme="simple"
														ondragstart="return false;" ondrop="return false;">Return
														to merchant</a> -->
												</p>
											</form>
										</div>
								</div>
									
									<div id="netBanking" class="tabBox hideBox">
										<form id="netBankingFormId" name="netBankingFormName"
											action="pay" target="_parent" method="post" autocomplete="off">
											<input type="hidden" name="paymentType" value="NB"> <input
												type="hidden" name="mopType" id="nbMopType" value="">
											<input type="hidden" id="amountNetBanking" name="amount"
												value="">
											<input type="hidden" id="BROWSERDEVICENB" name="BROWSER_DEVICE"	value="">
											<ul class="nbSection" id="dynamicImageList">
											</ul>
											<div class="row w-100">
												<div class="col-12">
													<label for="cvvnumber" class="col-form-label"></label>
													<div class="radio-list bank"></div>
													<!-- <div class="resultDiv">
														
														<p class="red1" id="walletpaymentMethodErr">Please select payment method</p>
													</div> -->
												</div>
											</div>
											<!-- <div class="radio-list bank"></div> -->
											<div class="form-fields py-3 pRow">
												<form class="row">
													<div class="col-12">
														<div class="row justify-content-between align-items-center flex-wrap">
															<div class="col-12">
																<label for="cvvnumber" class="col-form-label">Select Bank</label>
																<p class="form-text">
																	Find your bankfrom the list below
																</p>
																<div class="dropdown">
																	<button onChange="changeBankOption()"
																		class="btn btn-block dropdown-toggle" type="button"
																		id="bankDropdownMenuButton" data-toggle="dropdown"
																		aria-haspopup="true" aria-expanded="false">Select
																		bank</button>
																	<div class="dropdown-menu w-100"
																		aria-labelledby="bankDropdownMenuButton"
																		id="netbankingList"></div>
																</div>
																<div class="resultDivNb">
																	<p class="red1" id="bankErr">Please Select Bank</p>
																</div>
															</div>
														
														
														<div class="csls_footer-section mt-4">
															<div class="footer d-f f-jcb f-aic">
																<div class="left-footer">
																		<img src="../assets/images/Frame38.svg" class="rupay-foot" alt="rupay"
																title="rupay" />
																	<img src="../image/visa-foot.svg" class="visa-foot" alt="visa"
																		title="visa" /> <img src="../image/master-foot.svg"
																		class="master-foot" alt="master" title="master" />  <a href="./coc.pdf" target="_blank"><img
																		src="../image/pci-foot.svg" class="pci-foot" alt="pci"
																		title="pci" /></a>
																</div>
																
																<div class="right-footer2 clearfix">
																	<div class="float-left">
																		<img src="../assets/images/ShieldCheck.svg" alt="Secure Logo" class="secure-shield">
																	</div>
																	<div class="float-right">
																		<p>Safe and Secure Payments.</p>
																	</div>
																</div>
															</div>
														</div>
														<div class="row px-4 py-4 w-100">
															<div class="col-lg-12 ">
																<%
																if (webUrl.equals("pg.zebrapay.in")) {
															%>
															<div class="copyright" style="display: none;">© 2019-21
																pg.zebrapay.in. All rights reserved</div>
															<%
																} else {
															%>
															<div class="copyright" style="display: none;">© 2019-21
																secure.pay10.com. All rights reserved</div>
															<%
																}
															%>
															</div>
														</div>
													</div>
												</div>
												<!-- <div class="pRow">
													<div class="mb-3">
														<div class="dropdown">
															<button onChange="changeBankOption()"
																class="btn btn-block dropdown-toggle" type="button"
																id="bankDropdownMenuButton" data-toggle="dropdown"
																aria-haspopup="true" aria-expanded="false">Select
																bank</button>
															<div class="dropdown-menu w-100"
																aria-labelledby="bankDropdownMenuButton"
																id="netbankingList"></div>
														</div>
													</div>
												</div>
												<div class="resultDivNb">
													<p class="red1" id="bankErr">Please Select Bank</p>
												</div> -->
													<div class="row abc">
									<s:checkbox id="tnc" class="tnccheck"
																	name="tncStatus"   disabled="true"
																	style="margin-top:10px;    font-weight:500; font-size:14px;"></s:checkbox>
															<label for="">&nbsp; &nbsp;I agree to <a href="#" target=_self
																onClick="pop()">Terms & Conditions</a></label> <span
																class="data-value"> 
															</span>

														</div>
												<div class="CTA mob-btn" id="pay-now">
													<input type="button" class="payment-btn" id="netBankingBtn"
														onclick="submitNetbanking()" value="pay">
												</div>
		
												<p class="returnMerchand margin0">
													<!-- <a href="#" onclick="myCancelAction();" key="ReturnToMerchant"
														id="ADCancelButton" class="returnMerchand"
														name="ccCancelButton" theme="simple"
														ondragstart="return false;" ondrop="return false;">Return
														to merchant</a> -->
												</p>
											</form>
								
								</div>
								
									</div>

	<div id="emi" class="tabBox hideBox">
        <form id="emiFormId" name="emiFormName"
          action="pay" target="_parent" method="post" autocomplete="off">
          <input type="hidden" name="paymentType" value="EMI"> <input
            type="hidden" name="mopType" id="emiMopType" value="EMI">
          <input type="hidden" id="tenureLength" name="tenureLength"
            value="">

          <ul class="nbSection" id="dynamicImageList">
          </ul>
          <div class="row w-100">
            <div class="col-12">
              <label for="cvvnumber" class="col-form-label"></label>
              <div class="radio-list bank"></div>
              <!-- <div class="resultDiv">

                <p class="red1" id="walletpaymentMethodErr">Please select payment method</p>
              </div> -->
            </div>
          </div>
          <!-- <div class="radio-list bank"></div> -->
          <div class="form-fields py-3 pRow">
            <form class="row">
              <div class="col-12">
                <div class="row justify-content-between align-items-center flex-wrap">
                  <div class="col-12">
                    <label for="cvvnumber" class="col-form-label">Select Bank</label>
                    <p class="form-text">
                      Find your bankfrom the list below
                    </p>
					<input type="hidden" id="tenureDataAll" value='<s:property value="%{#session.emiBankAsJson}" />' />
                    <div class="dropdown">
						<select class="btn-block dropdown-toggle" id="emiBankId" name="emiBankId"
						onchange="showTenures(this.value);">
							<option value="">Select Bank</option>
							<s:iterator value="%{#session.emiBanks}">
								<option value='<s:property value="bank"/>'><s:property value="bank"/></option>
							</s:iterator>
						</select>
                    </div>
                  </div>
				  <div class="col-12">
					<div class="col-12 mb-4">
						<table  class="table" id="tenureTable" style="display: none;">
							<thead>
								<tr>
									<th scope="col">&nbsp; &nbsp;</th>
									<th scope="col">Tenure</th>
									<th scope="col">Bank Interest Rate</th>
									<th scope="col">Emi Amount</th>
									<th scope="col">Total Interest</th>
									<th scope="col">Principal Amount</th>
									<th scope="col">Discount</th>
								</tr>
							</thead>
							<tbody id="tenureTBody"></tbody>
						</table>
					</div>
				  </div>


                <div class="csls_footer-section mt-4">
                  <div class="footer d-f f-jcb f-aic">
                    <div class="left-footer">
                        <img src="../assets/images/Frame38.svg" class="rupay-foot" alt="rupay"
                    title="rupay" />
                      <img src="../image/visa-foot.svg" class="visa-foot" alt="visa"
                        title="visa" /> <img src="../image/master-foot.svg"
                        class="master-foot" alt="master" title="master" /> <img
                        src="../image/pci-foot.svg" class="pci-foot" alt="pci"
                        title="pci" />
                    </div>

                    <div class="right-footer2 clearfix">
                      <div class="float-left">
                        <img src="../assets/images/ShieldCheck.svg" alt="Secure Logo" class="secure-shield">
                      </div>
                      <div class="float-right">
                        <p>Safe and Secure Payments.</p>
                      </div>
                    </div>
                  </div>
                </div>
                <div class="row px-4 py-4 w-100">
                  <div class="col-lg-12 ">
                    <%
                    if (webUrl.equals("pg.zebrapay.in")) {
                  %>
                  <div class="copyright" style="display: none;">© 2019-21
                    pg.zebrapay.in. All rights reserved</div>
                  <%
                    } else {
                  %>
                  <div class="copyright" style="display: none;">© 2019-21
                    secure.pay10.com. All rights reserved</div>
                  <%
                    }
                  %>
                  </div>
                </div>
              </div>
            </div>
            <div class="CTA mob-btn" id="pay-now">
              <input type="button" class="payment-btn" id="emiBtn"
                onclick="submitEmi()" value="pay">
            </div>

            <p class="returnMerchand margin0">
              <!-- <a href="#" onclick="myCancelAction();" key="ReturnToMerchant"
                id="ADCancelButton" class="returnMerchand"
                name="ccCancelButton" theme="simple"
                ondragstart="return false;" ondrop="return false;">Return
                to merchant</a> -->
            </p>
        </form>
    </div>
</div>


									<section class="mob-footer pb-3">
										<div class="footer-wrap mx-3  pb-1 d-flex justify-content-center">
<img src="../assets/images/Frame 32.svg" id="QRFooter" style="
    display: block;
" alt="Pay10 Logo" />										</div>
									</section>
								</div>
							</div>
							<!-- /.csls_payments-tabs -->
								</div>


							</div>

					<!-- /.csls_main-header -->

					<!-- /.csls_footer-section -->
						</div>
						<div class="col-lg-3 pay-section">
							<div class="websection" id="paycredit">
								<div class="top-box"></div>
								<div class="card" id="creditDebitSidebar">
									<div class="card-body p-0">
										<div class="d-flex align-items-start flex-column">
											<div class="mb-auto p-3 w-100">
												<div class="row ">
													<div class="body-title">
														<img src="../assets/images/Frame 33.svg" alt="sim">
														<img src="../assets/images/Frame 34.svg" alt="wifi">
													</div>
												</div>
											</div>
											<div class="card-image w-100">
												<img src="../assets/images/Vector1.png" alt="">
											</div>
											<div class="px-3 ">
												<div class="card-details">
													<p class="holder-name text-uppercase word-break" id="cardholdername">Card Holder Name</p>
													<p class="holder-cvv" id="cardholdercvv">0000 0000 0000 0000</p>
												</div>
											</div>
											<div class="px-3 mt-4 w-100">
												<div class="row ">
													<div class="body-title">
														<div id="expdate">
															<span id="expdatemonth">MM</span>/
															<span id="expdateyear">YY</span>
														</div>
														<img id="userMoptypeIconSidebar" src="../image/bc.png" />

													</div>
												</div>
											</div>
										</div>
									</div>
								</div>
								<div class="card" id="QRSidebar">
									<div class="card-body p-0">
										<div class="d-flex align-items-start flex-column">


											<div class="card-image bankimage w-100">
												<!-- <img src="../assets/images/Rectangle 200.svg" alt=""> -->
												<span >QR CODE</span>

											</div>
										</div>
									</div>
								</div>
								<div class="card" id="upiSidebar">
									<div class="card-body p-0">
										<div class="d-flex align-items-start flex-column">
											<div class="mb-auto p-3 w-100">
												<div class="row ">
													<div class="body-title">
														 <img src="../assets/images/Frame 54.svg" alt="UPI">
													</div>
												</div>
											</div>
											<div class="card-image w-100">
												<img src="../assets/images/Vector1.png" alt="">
											</div>
											<div class="card-image bankimage w-100">
												<!-- <img src="../assets/images/Rectangle 200.svg" alt=""> -->
												<span id="upiid" class="word-break">**********</span></h4>

											</div>
											<div class="px-3 mt-4">
												<div class="card-details d-flex">
													<!-- <span id="upiid" class="word-break">**********</span> -->
													<span id="bankid"></span>
												</div>
											</div>
											<div class="px-3 mt-4 w-100">
												<div class="row ">
													<div class="card-footer-logo">
														<img src="../assets/images/Frame 39.svg" alt="wifi">
													</div>
												</div>
											</div>
										</div>
									</div>
								</div>
								<div class="card" id="emiSidebar">
									<div class="card-body p-0">
										<div class="d-flex align-items-start flex-column">
											<div class="mb-auto p-3 w-100">
												<div class="row ">
													<div class="body-title">
													 <img src="../assets/images/bank.png" alt="EMI" id="emiImgIcon">
													</div>
												</div>
											</div>
											<div class="card-image w-100">
												<img src="../assets/images/Vector1.png" alt="">
											</div>
											<div class="card-image bankimage w-100">
												<!-- <img src="../assets/images/Frame 50.svg" alt=""> -->
											<h4><span id="setEmiBankname">Bank Name</span></h4>

											</div>
											<div class="px-3 mt-4">
												<div class="card-details d-flex">
													<!-- <span id="setbankname">Bank Name</span> -->
												</div>
											</div>
											<div class="px-3 mt-4 w-100">
												<div class="row  float-right">
													<div class="col card-footer-text">
												EMI
												</div>
												</div>
											</div>
										</div>
									</div>
								</div>
								<div class="card" id="walletSidebar">
									<div class="card-body p-0">
										<div class="d-flex justify-content-between h-100 align-items-start flex-column">
											<div class="mb-auto p-3 w-100">
												<div class="row ">
													<div class="body-title">
														 <img src="../assets/images/purse.png" alt="UPI" id="walletImgIcon">
													</div>
												</div>
											</div>
											<div class="card-image wcard-image w-100">
												<img src="../assets/images/Vector1.png" alt="">
											</div>
											<div class="card-image wcard-image qrcode w-100">
												<!-- <h4><span id="setbanknameWallet">Payment Method</span></h4>	 -->
											<img src="" alt="" id="setbanknameWalletImg" class="img-responsive-img"> 
											</div>
											<div class="px-3 ">
												<div class="card-details">
													<p class="" id="walletSideBarById">000000000000</p>
												</div>
											</div>
											<div class="px-3 mt-3 w-100">
												<div class="row float-right">
													<div class="col card-footer-text">
														Wallet
													</div>
												</div>
											</div>
										</div>
									</div>
								</div>
								<div class="card" id="netBankingSidebar">
									<div class="card-body p-0">
										<div class="d-flex align-items-start flex-column">
											<div class="mb-auto p-3 w-100">
												<div class="row ">
													<div class="body-title">
													 <img src="../assets/images/bank.png" alt="UPI" id="netBankingImgIcon">
													</div>
												</div>
											</div>
											<div class="card-image w-100">
												<img src="../assets/images/Vector1.png" alt="">
											</div>
											<div class="card-image bankimage w-100">
												<!-- <img src="../assets/images/Frame 50.svg" alt=""> -->
											<h4><span id="setbankname">Bank Name</span></h4>	

											</div>
											<div class="px-3 mt-4">
												<div class="card-details d-flex">
													<!-- <span id="setbankname">Bank Name</span> -->
												</div>
											</div>
											<div class="px-3 mt-4 w-100">
												<div class="row  float-right">
													<div class="col card-footer-text">
												Net-Banking 
												</div>
												</div>
											</div>
										</div>
									</div>
								</div>
								<div class="paymentlist px-3">
									<ul class="p-0 notranslate">
										<!-- <li class="d-flex justify-content-between py-2">
											<span class="item">Item Total</span>
											<div class="values">
												<img src="../assets/images/rupees.svg" alt="Pay10 Logo">
												<span class="value">1650.00</span>
											</div>
										</li> -->
										<li class="d-flex justify-content-between py-2">
											<span class="item" id="buyer">Buyer</span>
											<div class="values">
												<!-- <img src="../assets/images/rupees.svg" alt="Pay10 Logo"> -->
												<span class="value" id="customerName" name="customerName"><%=ESAPI.encoder().encodeForHTML((String) session.getAttribute(FieldType.CUST_NAME.getName()))%></span>
											</div>
										</li>
										
										<li class="d-flex justify-content-between py-2">
											<span class="item">Order ID</span>&nbsp;
											<span class="value"><%=ESAPI.encoder().encodeForHTML((String) session.getAttribute(FieldType.ORDER_ID.getName()))%></span>
										</li>
										<li class="d-flex justify-content-between py-2">
											<span class="item">Base Amount</span>&nbsp;
											<span class="value" id ="BaseAmount"><%=ESAPI.encoder().encodeForHTML((String) session.getAttribute(FieldType.AMOUNT.getName()))%></span>
										</li>
										<!-- <li class="d-flex justify-content-between py-2">
											<span class="item">GCT</span>
											<div class="values">
												<img src="../assets/images/rupees.svg" alt="Pay10 Logo">
												<span class="value">25.00</span>
											</div>
										</li> -->
										<li class="d-flex justify-content-between py-2">
											<span class="item" id="moreInfo">Surcharge Amount + GST</span>
											<div class="values">
											<img src="../assets/images/rupees.svg" alt="Pay10 Logo">
												<span class="value" id="surchargeAmount" name="surchargeAmount">0.00</span>
											</div>
										</li>
										<!-- <li class="d-flex justify-content-between py-2">
											<span class="item">SGST</span>
											<div class="values">
												<img src="../assets/images/rupees.svg" alt="Pay10 Logo">
												<span class="value">05.00</span>
											</div>
										</li> -->
										<!-- <li class="d-flex justify-content-between py-2">
											<span class="item">Shipping & Handling</span>
											<div class="values">
												<img src="../assets/images/rupees.svg" alt="Pay10 Logo">
												<span class="value">15</span>
											</div>
										</li> -->
									</ul>
								</div>
								<div class="total-price w-100">
									<span class="item" id="totalAmountName">You have to pay</span>
									<div class="values">
										<!-- <img src="../assets/images/Vector.svg" alt="Pay10 Logo"> -->
										<span class="value" id="totalAmount">0.00</span>
										<!-- <span class="value" id="totalAmountPayBox">0.00</span> -->

									</div>
								</div>
							</div>
							
							<!-- <div class="pay-section websection" id="payupi">
								<div class="top-box"></div>
								<div class="card">
									<div class="card-body p-0">
										<div class="d-flex align-items-start flex-column">
											<div class="mb-auto p-3 w-100">
												<div class="row ">
													<div class="body-title">
														<img src="../assets/images/Frame 54.svg" alt="UPI">
													</div>
												</div>
											</div>
											<div class="card-image w-100">
												<img src="../assets/images/Vector1.png" alt="">
											</div>
											<div class="card-image qrcode w-100">
												<img src="../assets/images/Rectangle 200.svg" alt="">
											</div>
											<div class="px-3 mt-4">
												<div class="card-details d-flex">
													<span id="upiid">**********</span>
													<span id="bankid">@okupiid</span>
												</div>
											</div>
											<div class="px-3 mt-4 w-100">
												<div class="row ">
													<div class="card-footer-logo">
														<img src="../assets/images/Frame 39.svg" alt="wifi">
													</div>
												</div>
											</div>
										</div>
									</div>
								</div>
								<div class="paymentlist px-3">
									<ul class="p-0">
										<li class="d-flex justify-content-between py-2">
											<span class="item">Item Total</span>
											<div class="values">
												<img src="../assets/images/rupees.svg" alt="Pay10 Logo">
												<span class="value">1650.00</span>
											</div>
										</li>
										<li class="d-flex justify-content-between py-2">
											<span class="item">Product</span>
											<span class="value">3 Items</span>
										</li>
										<li class="d-flex justify-content-between py-2">
											<span class="item">Order Number</span>
											<span class="value">123456</span>
										</li>
										<li class="d-flex justify-content-between py-2">
											<span class="item">GCT</span>
											<div class="values">
												<img src="../assets/images/rupees.svg" alt="Pay10 Logo">
												<span class="value">25.00</span>
											</div>
										</li>
										<li class="d-flex justify-content-between py-2">
											<span class="item">SGST</span>
											<div class="values">
												<img src="../assets/images/rupees.svg" alt="Pay10 Logo">
												<span class="value">05.00</span>
											</div>
										</li>
										<li class="d-flex justify-content-between py-2">
											<span class="item">Shipping & Handling</span>
											<div class="values">
												<img src="../assets/images/rupees.svg" alt="Pay10 Logo">
												<span class="value">15</span>
											</div>
										</li>
									</ul>
								</div>
								<div class="total-price w-100">
									<span class="item">You have to pay</span>
									<div class="values">
										<img src="../assets/images/Vector.svg" alt="Pay10 Logo">
										<span class="value">1650.00</span>
									</div>
								</div>
							</div>
							<div class="pay-section websection" id="paywallet">
								<div class="top-box"></div>
								<div class="card">
									<div class="card-body p-0">
										<div class="d-flex justify-content-between h-100 align-items-start flex-column">
											<div class="mb-auto p-3 w-100">
												<div class="row ">
													<div class="body-title">
														<img src="../assets/images/Frame 54.svg" alt="UPI">
													</div>
												</div>
											</div>
											<div class="card-image wcard-image w-100">
												<img src="../assets/images/Vector1.png" alt="">
											</div>
											<div class="card-image wcard-image qrcode w-100">
												<img src="../assets/images/Rectangle 200.svg" alt="">
											</div>
											<div class="px-3 mt-5 w-100">
												<div class="row ">
													<div class="card-footer-logo">
														<img src="../assets/images/Frame 39.svg" alt="wifi">
													</div>
												</div>
											</div>
										</div>
									</div>
								</div>
								<div class="paymentlist px-3">
									<ul class="p-0">
										<li class="d-flex justify-content-between py-2">
											<span class="item">Item Total</span>
											<div class="values">
												<img src="../assets/images/rupees.svg" alt="Pay10 Logo">
												<span class="value">1650.00</span>
											</div>
										</li>
										<li class="d-flex justify-content-between py-2">
											<span class="item">Product</span>
											<span class="value">3 Items</span>
										</li>
										<li class="d-flex justify-content-between py-2">
											<span class="item">Order Number</span>
											<span class="value">123456</span>
										</li>
										<li class="d-flex justify-content-between py-2">
											<span class="item">GCT</span>
											<div class="values">
												<img src="../assets/images/rupees.svg" alt="Pay10 Logo">
												<span class="value">25.00</span>
											</div>
										</li>
										<li class="d-flex justify-content-between py-2">
											<span class="item">SGST</span>
											<div class="values">
												<img src="../assets/images/rupees.svg" alt="Pay10 Logo">
												<span class="value">05.00</span>
											</div>
										</li>
										<li class="d-flex justify-content-between py-2">
											<span class="item">Shipping & Handling</span>
											<div class="values">
												<img src="../assets/images/rupees.svg" alt="Pay10 Logo">
												<span class="value">15</span>
											</div>
										</li>
									</ul>
								</div>
								<div class="total-price w-100">
									<span class="item">You have to pay</span>
									<div class="values">
										<img src="../assets/images/Vector.svg" alt="Pay10 Logo">
										<span class="value">1650.00</span>
									</div>
								</div>
							</div> -->
							<!-- <div class="pay-section websection" id="paynetbanking">
								<div class="top-box"></div>
								<div class="card">
									<div class="card-body p-0">
										<div class="d-flex align-items-start flex-column">
											<div class="mb-auto p-3 w-100">
												<div class="row ">
													<div class="body-title">
														<img src="../assets/images/Frame 54.svg" alt="UPI">
													</div>
												</div>
											</div>
											<div class="card-image w-100">
												<img src="../assets/images/Vector1.png" alt="">
											</div>
											<div class="card-image bankimage w-100">
												<img src="../assets/images/Frame 50.svg" alt="">
											</div>
											<div class="px-3 mt-4">
												<div class="card-details d-flex">
													<span id="setbankname">Bank Name</span>
												</div>
											</div>
											<div class="px-3 mt-4 w-100">
												<div class="row ">
													<div class="card-footer-logo">
														<img src="../assets/images/Frame 39.svg" alt="wifi">
													</div>
												</div>
											</div>
										</div>
									</div>
								</div>
								<div class="paymentlist px-3">
									<ul class="p-0">
										<li class="d-flex justify-content-between py-2">
											<span class="item">Item Total</span>
											<div class="values">
												<img src="../assets/images/rupees.svg" alt="Pay10 Logo">
												<span class="value">1650.00</span>
											</div>
										</li>
										<li class="d-flex justify-content-between py-2">
											<span class="item">Product</span>
											<span class="value">3 Items</span>
										</li>
										<li class="d-flex justify-content-between py-2">
											<span class="item">Order Number</span>
											<span class="value">123456</span>
										</li>
										<li class="d-flex justify-content-between py-2">
											<span class="item">GCT</span>
											<div class="values">
												<img src="../assets/images/rupees.svg" alt="Pay10 Logo">
												<span class="value">25.00</span>
											</div>
										</li>
										<li class="d-flex justify-content-between py-2">
											<span class="item">SGST</span>
											<div class="values">
												<img src="../assets/images/rupees.svg" alt="Pay10 Logo">
												<span class="value">05.00</span>
											</div>
										</li>
										<li class="d-flex justify-content-between py-2">
											<span class="item">Shipping & Handling</span>
											<div class="values">
												<img src="../assets/images/rupees.svg" alt="Pay10 Logo">
												<span class="value">15</span>
											</div>
										</li>
									</ul>
								</div>
								<div class="total-price w-100">
									<span class="item">You have to pay</span>
									<div class="values">
										<img src="../assets/images/Vector.svg" alt="Pay10 Logo">
										<span class="value">1650.00</span>
									</div>
								</div>
							</div> -->





							<div class="csls_sidebar-section" style="display: none;">
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
													String formattedQRTotalAmount = "";
													String formattedAmountTotal = "";
													String formattedDCTotalAmount = "";
													String ccSurchagreAmount = "";
													String dcSurchagreAmount = "";
													String qrSurchagreAmount = "";
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
														formattedQRTotalAmount = df.format(session.getAttribute(FieldType.QR_TOTAL_AMOUNT.getName()));

			
														formattedDCTotalAmount = df.format(session.getAttribute(FieldType.DC_TOTAL_AMOUNT.getName()));
			
														ccSurchagreAmount = df.format(session.getAttribute(FieldType.CC_SURCHARGE.getName()));
			
														dcSurchagreAmount = df.format(session.getAttribute(FieldType.DC_SURCHARGE.getName()));
														
														nbSurchagreAmount = df.format(session.getAttribute(FieldType.NB_SURCHARGE.getName()));
														qrSurchagreAmount = df.format(session.getAttribute(FieldType.QR_SURCHARGE.getName()));

														
														wlSurchagreAmount = df.format(session.getAttribute(FieldType.WL_SURCHARGE.getName()));
														
														upSurchagreAmount = df.format(session.getAttribute(FieldType.UP_SURCHARGE.getName()));
														
														pcSurchagreAmount = df.format(session.getAttribute(FieldType.PC_SURCHARGE.getName()));
														
														adSurchagreAmount = df.format(session.getAttribute(FieldType.AD_SURCHARGE.getName()));
														
														formattedNBTotalAmount = df.format(session.getAttribute(FieldType.NB_TOTAL_AMOUNT.getName()));
														
														formattedWLTotalAmount = df.format(session.getAttribute(FieldType.WL_TOTAL_AMOUNT.getName()));
														
														formattedUPTotalAmount = df.format(session.getAttribute(FieldType.UP_TOTAL_AMOUNT.getName()));
														
														formattedPCTotalAmount = df.format(session.getAttribute(FieldType.PC_TOTAL_AMOUNT.getName()));
														
														formattedADTotalAmount = df.format(session.getAttribute(FieldType.AD_TOTAL_AMOUNT.getName()));
														
														
			
			
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
												<input type="hidden" id="qrsurchargeTotal"
													name="qrsurchargeTotal" value=<%=formattedQRTotalAmount%>>
													<input type="hidden" id="qrsurchargeAmount"
													name="qrsurchargeAmount" value=<%=qrSurchagreAmount%>>
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
														<!-- <small id="totalAmountName">Amount payable</small>
														<p class="summary-amount" id="totalAmount"></p> -->
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
																<!-- <small >Buyer</small>
																<div  name="customerName"><%=ESAPI.encoder().encodeForHTML((String) session.getAttribute(FieldType.CUST_NAME.getName()))%></div> -->
															</div>
														</div>
														<div class="summary-list-item">
														<div>
															  <!-- <small id="moreInfo">Surcharge Amount + GST</small>
															  <div id="surchargeAmount" name="surchargeAmount"></div>	  -->
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
						</div>
						
					</div>
				
				
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
	
	<!--on checkbox modal click -->
<div class="modal" id="basicModal" tabindex="-1" role="dialog" aria-labelledby="basicModal" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <h4 class="modal-title" id="myModalLabel"> Save your card for future transactions!</h4>
<!--         <button type="button" class="close" data-dismiss="modal" aria-label="Close" onclick="popUpClose()"> -->
<%--           <span aria-hidden="true">&times;</span> --%>
<!--         </button> -->
      </div>
      <div class="modal-body">
        	<p>	Benefits of saving your card :- </p>
        	
        	<p>	<b>	* Convenient and faster payments :</b> Future orders can be placed without entering card details.</p>
        	<p>	<b>	* Security Guaranteed : </b> Actual card details shall not be stored but the card network encrypts the card information and encrypted card information is kept for future transactions.</p>
        		<p>Your consent is mandatory, as per RBI norms, for saving your card and to save you from the hassles of entering the card details, every time you process any transaction.</p>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-primary" data-dismiss="modal" onclick="popUpClose()">ok</button>
      </div>
    </div>
  </div>
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
document.getElementById("setbanknameWalletImg").src = "../image/bank/selectwallet.png";
    document.getElementById("walletSideBarById").innerHTML = "0000000000";	
    
    const checkbox = document.getElementById('cardsaveflag1')
    checkbox.addEventListener('change', (event) => {
      if (event.currentTarget.checked) {
    	  $('#basicModal').show();
    	    } else {
      }
    });
    
    function popUpClose(){
        $('#basicModal').hide();	
    }
    
    
    
    
    
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
	<!-- <script>
        document.addEventListener("DOMContentLoaded", ready);
        function ready() {
            localStorage.removeItem('currentpage')
            localStorage.removeItem('currentpaymentsection')
            let innerwidth = window.innerWidth
            if(innerwidth < 1200){
                document.getElementById('mobhome').style.display = 'block'
                document.getElementById('allcontents').style.display = 'none'
                var mobilesection = document.getElementsByClassName("mobilesection"); 
                for(var i = 0; i < mobilesection.length; i++){
                    mobilesection[i].style.display = "flex"; 
                }
                var websection = document.getElementsByClassName("websection"); 
                for(var i = 0; i < websection.length; i++){
                    websection[i].style.display = "none"; 
                }            
            }
            else{
                document.getElementById('mobhome').style.display = 'none'
                document.getElementById('allcontents').style.display = 'block'

                var mobilesection = document.getElementsByClassName("mobilesection"); 
                for(var i = 0; i < mobilesection.length; i++){
                    mobilesection[i].style.display = "none"; 
                }
                var websection = document.getElementsByClassName("websection"); 
                for(var i = 0; i < websection.length; i++){
                    websection[i].style.display = "flex"; 
                }
                var innerpage = document.getElementsByClassName("innerpage"); 
                for(var i = 0; i < innerpage.length; i++){
                    innerpage[i].style.display = "none"; 
                }
                var paysection = document.getElementsByClassName("pay-section"); 
                for(var i = 0; i < paysection.length; i++){
                    p//aysection[i].style.display = "none"; 
                }
                let currentPage = localStorage.getItem('currentpage')
                if(currentPage){
                    document.getElementById(currentPage).style.display = 'block'
                }else{
                    document.getElementById('creditPage').style.display = 'block'
                    document.getElementById('paycredit').style.display = 'flex'
                }
            }
        }
        window.onresize = function(){
            let innerwidth = window.innerWidth
            if(innerwidth < 1200){
                var mobilesection = document.getElementsByClassName("mobilesection"); 
                for(var i = 0; i < mobilesection.length; i++){
                    mobilesection[i].style.display = "flex"; 
                }
                var websection = document.getElementsByClassName("websection"); 
                for(var i = 0; i < websection.length; i++){
                    websection[i].style.display = "none"; 
                } 
            }
            else{
                document.getElementById('mobhome').style.display = 'none'
                document.getElementById('allcontents').style.display = 'block'
                var mobilesection = document.getElementsByClassName("mobilesection"); 
                for(var i = 0; i < mobilesection.length; i++){
                    mobilesection[i].style.display = "none"; 
                }
                var websection = document.getElementsByClassName("websection"); 
                for(var i = 0; i < websection.length; i++){
                    websection[i].style.display = "flex"; 
                }
                var innerpage = document.getElementsByClassName("innerpage"); 
                for(var i = 0; i < innerpage.length; i++){
                    innerpage[i].style.display = "none"; 
                }
                var paysection = document.getElementsByClassName("pay-section"); 
                for(var i = 0; i < paysection.length; i++){
                    paysection[i].style.display = "none"; 
                }
                let currentPage = localStorage.getItem('currentpage')
                let currentpaymentsection = localStorage.getItem('currentpaymentsection')
                
                if(currentPage){
                    document.getElementById(currentPage).style.display = 'block'
                }else{
                    document.getElementById('creditPage').style.display = 'block'
                }
                if(currentpaymentsection){
                    document.getElementById(currentpaymentsection).style.display = 'flex'
                }else{
                    document.getElementById('paycredit').style.display = 'flex'
                }
            }
        }

        const loadinnerpage = (e,page,paymentsection)=>{
            var activemenus = document.getElementsByClassName("limenu-item"); 
            for(var i = 0; i < activemenus.length; i++){
                if(activemenus[i].classList.contains("active")){
                    activemenus[i].classList.remove('active')
                }
            }
            e.classList.add("active")
            localStorage.setItem('currentpage',page)
            localStorage.setItem('currentpaymentsection',paymentsection)
            var innerpage = document.getElementsByClassName("innerpage"); 
            for(var i = 0; i < innerpage.length; i++){
                innerpage[i].style.display = "none"; 
            }
            document.getElementById(page).style.display = 'block'
            var paysection = document.getElementsByClassName("pay-section"); 
            for(var i = 0; i < paysection.length; i++){
                paysection[i].style.display = "none"; 
            }
            document.getElementById(paymentsection).style.display = 'flex'
        }
        const getmobpage = (page,paymentsection,currentmenu)=>{
            document.getElementById('allcontents').style.display = 'block'
            var mobilesection = document.getElementsByClassName("mobilesection"); 
            for(var i = 0; i < mobilesection.length; i++){
                mobilesection[i].style.display = "flex"; 
            }
            var innerpage = document.getElementsByClassName("innerpage"); 
            for(var i = 0; i < innerpage.length; i++){
                innerpage[i].style.display = "none"; 
            }
            document.getElementById('mobhome').style.display = 'none'
            localStorage.setItem('currentpage',page)
            localStorage.setItem('currentpaymentsection',paymentsection)
            document.getElementById(page).style.display = 'block'
            var activemenus = document.getElementsByClassName("limenu-item");
            for(var i = 0; i < activemenus.length; i++){
                if(activemenus[i].classList.contains("active")){
                    activemenus[i].classList.remove('active')
                }
            }
            let newmenu = document.getElementsByClassName(currentmenu)
            for(var i = 0; i < newmenu.length; i++){
                newmenu[i].classList.add('active')
            }
        }
    </script> -->
    <script type="text/javascript">
    
	function pop(){
		document.getElementById("saleRequest").style.display = "block";
	}

	function cancelbutton(){
		document.getElementById("saleRequest").style.display = "none";
	}
	</script>
    
    <div class="modal" tabindex="-1" role="dialog" id="saleRequest">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-header">
        <h5 class="modal-title">Terms & Conditions</h5>
        <button type="button" class="close" data-dismiss="modal" onclick="cancelbutton()" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body">
        <p id="payMessage"> </p>
      </div>
     <!--  <div class="modal-footer">
        <button type="button" class="btn btn-primary">Save changes</button>
        <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
      </div> -->
        <div class="modal-footer">
          <button type="button" class="btn btn-default" data-dismiss="modal"  onclick="cancelbutton()">Close</button>
        </div>
    </div>
  </div>
</div>	
  
    
</body>
</html>