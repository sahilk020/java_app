<!DOCTYPE html
  PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@taglib prefix="s" uri="/struts-tags"%>
<%@ page import="org.owasp.esapi.ESAPI"%>
<%@page import="com.pay10.commons.util.PropertiesManager"%>

<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">

<html>

<head>
<title>BPGATE</title>
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<link rel="icon" href="../image/98x98.png">
<meta name="viewport"
	content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" />

	<!-- CSS Files -->
	<!-- <link href="../css/material-dashboard.css?v=2.1.0" rel="stylesheet" /> -->
	<link href="../css/paymentLink.css" rel="stylesheet" type="text/css" />
	<link href="../css/bootstrap.min.css" rel="stylesheet" type="text/css" />
	
	<script type="text/javascript" src="../js/jquery.min.js"></script>
			<script src="../js/kotakOtpPage.js"></script>
			<script type="text/javascript" src="../js/jquery.min.js"></script>
			
	


	

	
<style>

.loader1 {
	display: none;
}

.loader2 {
	display: none;
}
.body-main {
    background: #ffffff;
    padding: 20px 30px !important;
    position: relative;
    box-shadow: 0 1px 21px #808080;
    font-size: 10px;
	margin-top: 6%;
}

body {
  font-family: sans-serif;
  display: grid;
  height: 100vh;
  place-items: center;
}

.base-timer {
  position: relative;
  width: 70px;
  height: 70px;
}

.base-timer__svg {
  transform: scaleX(-1);
}

.base-timer__circle {
  fill: none;
  stroke: none;
}

.base-timer__path-elapsed {
  stroke-width: 7px;
  stroke: grey;
}

.base-timer__path-remaining {
  stroke-width: 7px;
  stroke-linecap: round;
  transform: rotate(90deg);
  transform-origin: center;
  transition: 1s linear all;
  fill-rule: nonzero;
  stroke: currentColor;
}

.base-timer__path-remaining.green {
  color: rgb(65, 184, 131);
}

.base-timer__path-remaining.orange {
  color: orange;
}

.base-timer__path-remaining.red {
  color: red;
}

.base-timer__label {
  position: absolute;
  width: 70px;
  height: 70px;
  top: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 19px;
}


@media(max-width:991px){
.text-align{
	text-align: center !important;
}
}
</style>
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


<body >
	
		<div class="container">
			<div class="row">
				<div class="col-lg-12  body-main">
				
						<div class="row row-centre">
							<div class="col-lg-4 col-md-4 col-sm-12 col text-align">
								<img  src="../image/new-payment-page-logo.png" alt="pay10 Logo" title="pay10 Logo" class="image1">
								<p>secure.pay10.com</p>

							</div>
							<!-- <div class="col-lg-4">
								<img class="img" alt="Invoice Template" src="../image/98x98.png" />
							</div> -->
							<div class="col-lg-8 col-md-8 col-sm-12 coln text-align">
								<h2 class="payment-title">PAY10 OTP PAGE</h2>
							</div>
						</div>
							<div class="row">
								<div class="col-md-4 col-sm-12 form-group">
									<label for="">Please Enter your Otp<span class="star-color">*</span></label>
										<s:textfield id="Otp" name="CUST_NAME"
									maxlength="6"	onkeypress="return event.charCode >= 48 && event.charCode <= 57"	cssClass="signuptextfield" autocomplete="off" />
											<div id="errorMsgName" class="error"></div>	
																	<div  style="color: red;font-size: 13px;" id="error" class="invalid-feedback" ></div>	
											
								</div>
																<div class="col-md-4 col-sm-12 form-group">
																<div  style="color: blue;font-size: 13px;"  > *Please verify the Otp Before Page expire</div>	
																											<div id="app"></div>
																
								</div>
								
								
								
							</div>
							
							
							
							<div class="row">
								<div class="col-md-4 col-sm-12 form-group">
									<div class="txtnew">
										<input type="button" id="otpverfiy"
											class="btn btn-success btn-md" onclick="optSubmit()" value="Verify OTP">
									
									</div>
								</div>
								<div class="col-md-4 col-sm-12 form-group">
									<div class="txtnew">
										<input type="button" id="ResendOTP"
											class="btn btn-success btn-md"  onclick="optResend()" value="Resend OTP">
									
									</div>
								</div>
							</div>
				</div>
			</div>

		</div>
		<s:hidden id="PostUrl" name="PostUrl" value="%{PostUrl}" />
	
			<form  method="post">    
					<s:hidden id="OrderId" name="OrderId" value="%{orderId}" />

				
			</form>
		
		
</html>