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
						<script src="../js/SbiOtpPage.js"></script>
			
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


.text-color {
  color: #ef0a0a;
}
.card {
  width: 350px;
  padding: 12%;
  border-radius: 20px;
  background: #f5f5f5;
  border: none;
  position: relative;
  text-align:center;
  margin-top:70px
}
.container {
  height: 100vh;
}
.mobile-text {
  color: #46158d;
  font-size: 15px;
}
.form-control {
  margin-right: 12px;
}
.form-control:focus {
  color: #495057;
  background-color: #d4efdf;
  border-color: #c0392b;
  outline: 0;
  box-shadow: none;
}
.cursor {
  cursor: pointer;
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

 <div class="d-flex justify-content-center align-items-center continer">
 
      <div class="card py-5 px-3">
        <div class="m-0"> 
        <div class="custom-logo">
        
								<img  src="../image/new-payment-page-logo.png" alt="pay10 Logo" title="pay10 Logo" class="image1">
								<p>secure.pay10.com</p>

								<img  src="../image/ru.png" alt="pay10 Logo" title="pay10 Logo" class="image1">

							</div>
   </div>
 
        <h5 class="m-0">Please enter your One Time Password (OTP) sent to your registered Mobile No</h5>
        <span class="mobile-text"
          >

         For Resend the OTP? 
    	  <span class="font-weight-bold text-color cursor" onclick="optResend()">Resend
    	  </span></span>          
          <b></b>
        </span>
        <div class="d-flex flex-row mt-5" style="
    margin: 16px 0px;
    display: inline-flex;
">

										
  <input type="text" class="form-control" onpast="false" maxlength="6"	onkeypress="return event.charCode >= 48 && event.charCode <= 57"	autocomplete="off" id="Otp"/> 
      
										<input type="button" id="ResendOTP"
											class="btn btn-success btn-md" onclick="optSubmit()"  value="Verify">
									
								
         
        </div>
        
        	
        <div class="text-center mt-5">
          <span class="d-block mobile-text" id="countdown"></span>
          
          <br>
          											<div id="error" class="error" style="
    position: inherit;
    display: block;
"></div>	
          
         
        </div>
      </div>
    </div>
	

		<s:hidden id="PostUrl" name="PostUrl" value="%{PostUrl}" />
	
			<form  method="post">    
					<s:hidden id="OrderId" name="OrderId" value="%{orderId}" />

				
			</form>
		
		
</html>