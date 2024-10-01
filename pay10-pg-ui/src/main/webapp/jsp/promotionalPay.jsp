<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@taglib prefix="s" uri="/struts-tags"%>
<%@ page import="org.owasp.esapi.ESAPI"%>
<%@page import="com.pay10.commons.util.PropertiesManager"%>
<html>
<head>
<title>BPGATE</title>
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<meta name="viewport"
	content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" />
<link rel="stylesheet" type="text/css"
	href="https://fonts.googleapis.com/css?family=Roboto:300,400,500,700|Roboto+Slab:400,700|Material+Icons" />
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/font-awesome/latest/css/font-awesome.min.css">
	<!-- CSS Files -->
	<link rel="icon" href="../image/98x98.png">
		<!-- CSS Files -->
		<!-- <link href="../css/material-dashboard.css?v=2.1.0" rel="stylesheet" /> -->
		<link href="../css/paymentLink.css" rel="stylesheet" type="text/css" />
		<link href="../css/bootstrap.min.css" rel="stylesheet" type="text/css" />
		<script src="../js/jquery.min.js"></script>

		<script>
			if (self == top) {
				var theBody = document.getElementsByTagName('body')[0];
				theBody.style.display = "block";
			} else {
				top.location = self.location;
			}
		</script>
		<script>
			function formLoad() {
				// $("#INVOICE_PAY_BTN").addClass('disabled');

				// if(CUST_PHONE.value >= 1 && CUST_EMAIL.value >= 1){
				//   $("#INVOICE_PAY_BTN").removeClass('disabled');
				// }
				var messageDiv = document.getElementById('actionMessageDiv');
				// console.log(messageDiv.innerText);
				if (messageDiv.innerText.trim() == "SUCCESS") {

					messageDiv.style.display = "none";
				} else {
					messageDiv.style.display = "block";
				}
				var enablePayNow = '<s:property value="%{enablePay}" />';
				
				var tncStatus1='<s:property value="invoice.tncStatus"/>';
				var payid='<s:property value="invoice.payId"/>';
				if(enablePayNow== "TRUE") {
					document.getElementById('INVOICE_PAY_BTN').disabled = false;
					 
				}	
				else {
					document.getElementById('INVOICE_PAY_BTN').disabled = true;
					document.getElementById('INVOICE_PAY_BTN').style.display = "none";			 			
				}
				if(tncStatus1=="true")
				{
				$('#abc').show();
			
				//document.getElementById('INVOICE_PAY_BTN').style.display = "none";
				
				}if(tncStatus1=="false"){
					
					$('#abc').hide();
					//$('#INVOICE_PAY_BTN').show();
					}

				// var messageDiv =  document.getElementById('actionMessageDiv');
				//   if(messageDiv.innerText=="SUCCESS"){
				// 	  messageDiv.style.display = "none";
				//   }
			};
			function submitForm() {

				var CUST_PHONE = document.getElementById('CUST_PHONE').value;
				var CUST_EMAIL = document.getElementById('CUST_EMAIL').value;

				var CUST_NAME = document.getElementById('CUST_NAME').value;
			
				if (CUST_PHONE != "" && CUST_EMAIL != "" && CUST_NAME != "") {
					debugger;
					var form = document.forms[0];
					form.action = payURL;
					form.submit();
				} else {
					if (CUST_EMAIL == "") {

						document.getElementById('CUST_EMAILErr').innerHTML = "Please enter valid Email Id.";
					}
					if (CUST_NAME == "") {
						document.getElementById('CUST_NAMEErr').innerHTML = "Please enter Name.";
					}
					if (CUST_PHONE == "") {
						document.getElementById('CUST_PHONEErr').innerHTML = "Please enter Phone.";
					}
					return false;

				}
			}
		</script>

		<script src="../js/invoicePay.js"></script>
		<!-- <link href="../css/invoicePay.css" rel="stylesheet" type="text/css" /> -->
		<style>
label {
	color: #212121 !important;
	font-size: 16px;
}

#INVOICE_PAY_BTN {
	width: 250px;
	float: unset;
}

div#wwctrl_INVOICE_PAY_BTN {
	text-align: center !important;
}

.d-none {
	display: none !important;
}

.row-centre {
	margin-bottom: 10px;
}

.payment-title {
	margin-top: 30px;
}

.invoice-no {
	margin-top: 35px;
	color: black !important;
}

#CUST_ZIP {
	height: 71px;
}

#CUST_STREET_ADDRESS1 {
	height: 71px;
}

@media ( max-width :991px) {
	.invoice-no {
		margin-right: 52px;
		margin-top: unset;
		color: black !important;
		font-size: 14px !important;
	}
	.row-centre {
		margin-bottom: unset;
	}
	.payment-title {
		margin-right: unset;
		text-align: center;
	}
}

@media ( max-width :320px) {
	label {
		font-size: 12px !important;
	}
	#INVOICE_PAY_BTN {
		width: 218px !important;
	}
}

@media ( max-width :240px) {
	label {
		font-size: 10px !important;
	}
	#INVOICE_PAY_BTN {
		width: 180px !important;
	}
	.payment-title {
		font-size: 11px;
	}
	.invoice-no {
		font-size: 10px !important;
	}
	.modal-body {
		font-size: 14px;
	}
}

.modal {
	overflow: hidden;
	font-size: 14px;
}

.modal-body {
	height: 400px;
	overflow: auto;
}
}
</style>
		<script>
			function submitForm() {
				var form = document.forms[0];
				inputElements = form.getElementsByClassName("signuptextfield");

				var Name = document.getElementById('CUST_NAME').value;
				var phone = document.getElementById('CUST_PHONE').value;
				var email = document.getElementById('CUST_EMAIL').value;
				var Username = /^[a-zA-Z]+(([',. -][a-zA-Z ])?[a-zA-Z]*)*$/;
				var Email = /^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$/;
				var Number = /^\d+$/;
				var tncStatus1='<s:property value="invoice.tncStatus"/>';
				var tnc=document.getElementById('tnc').checked;
				
				if (Name == "" && phone == "" && email == "") {
					$('#errorMsgName').text('Please enter name.');
					$('#errorMsgMobile').text('Please enter Mobile Number.');
					$('#errorMsgEmialid').text('Please enter your email Id.');
					//$('#errorTnc').text('Please select checkbox.');
					$("#errorMsgName").show();
					$("#errorMsgMobile").show();
					$("#errorMsgEmialid").show();
					//$("#errorTnc").show();
					setTimeout(function() {
						$("#errorMsgName").hide();
						$("#errorMsgMobile").hide();
						$("#errorMsgEmialid").hide();
						$("#errorTnc").hide();
					}, 5000);

					if(tncStatus1=="true" && tnc==false){
						$('#errorTnc').text('Please select checkbox.');
						$("#errorTnc").show();
						setTimeout(function() {
							$("#errorTnc").hide();
						}, 5000);
						}
				} else if (!Username.test(Name)) {
					$('#errorMsgName').text(
							'Please enter name in characters only .');
					document.getElementById('CUST_NAME').focus();
					$('#errorMsgName').show();
					setTimeout(function() {
						$('#errorMsgName').hide();
					}, 4000);
					return false;
				} else if (!Number.test(phone) || phone.length != 10) {
					$('#errorMsgMobile').text(
							'Please enter valid 10 digit Phone Number .');
					document.getElementById('CUST_PHONE').focus();
					$('#errorMsgMobile').show();
					setTimeout(function() {
						$('#errorMsgMobile').hide();
					}, 4000);
					return false;
				} else if (!Email.test(email)) {
					$('#errorMsgEmialid').text('Please enter valid email Id.');
					document.getElementById('CUST_EMAIL').focus();
					$("#errorMsgEmialid").show();
					setTimeout(function() {
						$("#errorMsgEmialid").hide();
					}, 4000);
					return false;
				}
				else if(tncStatus1=="true" && tnc==false){
				
					$('#errorTnc').text('Please select checkbox.');
					document.getElementById('errorTnc').focus();
					$("#errorTnc").show();
					setTimeout(function() {
						$("#errorTnc").hide();
					}, 4000);
					return false;
					
			}
				else {
					document.getElementById("promotionalPage").submit();
				}
			}
			function pop() {
				document.getElementById("saleRequest").style.display = "block";
			}

			function cancelbutton() {
				document.getElementById("saleRequest").style.display = "none";
			}

			/* function Accept() {
				var tnc = document.getElementById('tnc').checked;
				if (tnc == true) {
					$("#INVOICE_PAY_BTN").show();
				} else {
					$("#INVOICE_PAY_BTN").hide();
				}
			} */
		</script>
		  <script>
$(document).ready(function(){
	var invoiceNo='<s:property value="invoice.invoiceNo"/>';
	var mytext=$("#payText").text();
	mytext = mytext.replace("#invoiceNo", invoiceNo);
    $("#payText").text(mytext);
});
</script>
		
</head>
<body onload="formLoad();">
	<div id="actionMessageDiv" style="display: none;">
		<s:if test="hasActionMessages()">
			<s:actionmessage />
		</s:if>
	</div>
	<form method="post" action="payPromoInvoice" id="promotionalPage">
		<div id="payUrl" style="display: none"><%=new PropertiesManager().getSystemProperty("invoicePaymentLink")%></div>
		<div class="container">
			<div class="row body-main">
				<div class="col-lg-12">
					<div class="row row-centre">
						<div class="col-lg-4 col-md-3 col-sm-12 col ">
							<img src="../image/new-payment-page-logo.png" alt="pay10 Logo"
								title="pay10 Logo" class="image1"><br> <small
									class="logo-heading">secure.pay10.com</small>
						</div>

						<!-- <div class="col-lg-4">
								<img class="img" alt="Invoice Template" src="../image/98x98.png" />
							</div> -->
						<div class="col-lg-6 col-md-7 col-sm-12">
							<h2 class="payment-title">Review Information and Pay</h2>
						</div>
						<div class="col-lg-2 col-md-2 col-sm-12 col-xs-12">
							<label for="" class="invoice-no">Invoice No&nbsp;: <s:property
									value="%{invoice.invoiceNo}" /></label>
						</div>
					</div>
					<div class="row">
						<div class="col-lg-4 col-md-4 col-sm-12 col-xs-12 form-group">
							<label for="">Name</label><br> <input id="CUST_NAME"
								minlength="2" maxlength="32" class="form-control textFL_merch"
								onkeyup="FieldValidator.valdName(false);_ValidateField();"
								onkeypress="if(event.keyCode === 32)return true;return lettersOnly(event,this);"
								placeholder=" Enter Name" name="name" /> <span
								id="errorMsgName" class="error"></span>
						</div>
						<div class="col-lg-4 col-md-4 col-sm-12 col-xs-12 form-group">
							<label for="">Phone</label><br> <input id="CUST_PHONE"
								placeholder=" Enter Phone" class="form-control textFL_merch"
								onkeyup="FieldValidator.valdPhoneNo(false);_ValidateField();"
								onkeypress="return isNumberKey(event);" name="phoneNo"
								minlength="8" maxlength="13" /> <span id="errorMsgMobile"
								class="error"></span>
						</div>
						<div class="col-lg-4 col-md-4 col-sm-12 col-xs-12 form-group">
							<label for="">Email</label><br> <input id="CUST_EMAIL"
								placeholder=" Enter Email " class="form-control textFL_merch"
								onkeyup="FieldValidator.valdEmail(false);_ValidateField();"
								name="emailId" />
								</td> <span id="errorMsgEmialid" class="error"></span>
						</div>
					</div>
					<div class="row">
						<div class="col-lg-4 col-md-4 col-sm-12 col-xs-12 form-group">
							<label for="">City</label><br> <input id="city"
								placeholder=" Enter City" class="form-control textFL_merch"
								name="city" maxlength="100" minlength="2"
								onkeyup="FieldValidator.valdCity(false);_ValidateField();"
								onkeypress="if(event.keyCode === 32)return true;return lettersOnly(event,this);" />
						</div>
						<div class="col-lg-4 col-md-4 col-sm-12 col-xs-12 form-group">
							<label for="">State</label><br> <input id="state"
								placeholder=" Enter State " class="form-control textFL_merch"
								maxlength="100" minlength="2"
								onkeyup="FieldValidator.valdState(false);_ValidateField();"
								onkeypress="if(event.keyCode === 32)return true;return lettersOnly(event,this);"
								name="state" />
						</div>
						<div class="col-lg-4 col-md-4 col-sm-12 col-xs-12 form-group">
							<label for="">Country</label><br> <input id="country"
								placeholder=" Enter Country" class="form-control textFL_merch"
								maxlength="100" minlength="2"
								onkeyup="FieldValidator.valdCountry(false);_ValidateField();"
								onkeypress="if(event.keyCode === 32)return true;return lettersOnly(event,this);"
								name="country" />
						</div>
					</div>
					<div class="row">

						<div class="col-lg-4 col-md-4 col-sm-12 col-xs-12 form-group">
							<label for="">Zip</label><br> <input id="CUST_ZIP"
								placeholder="Enter Zip Code" class="form-control textFL_merch"
								minlength="6" maxlength="10"
								onkeyup="FieldValidator.valdZip(false);_ValidateField();"
								onkeypress="return isNumberKey(event)" name="zip" />
						</div>
						<div class="col-lg-8 col-md-8 col-sm-12 col-xs-12 form-group">
							<label for="">Address</label><br> <input
								class="form-control textFL_merch"
								onkeyup="FieldValidator.valdAddress(false);_ValidateField();"
								onkeypress="if(event.keyCode === 13)return false;" type="text"
								id="CUST_STREET_ADDRESS1" maxlength="150" minlength="3"
								name="custAddress">
						</div>
					</div>
					<div class="row">
						<div class="col-lg-4 col-md-4 col-sm-4 col-xs-4 form-group">
							<label for="">Name</label><br> <span class="data-value">
									<s:property value="%{invoice.productName}" />
							</span>
						</div>
						<div class="col-lg-4 col-md-4 col-sm-4 col-xs-5 form-group">
							<label for="">Description</label><br> <span
								class="data-value"> <s:property
										value="%{invoice.productDesc}" />
							</span>
						</div>
						<div class="col-lg-4 col-md-4 col-sm-4 col-xs-3 form-group">
							<label for="">Gst%</label><br> <span class="data-value">
									<s:property value="%{invoice.gst+'%'}" />
							</span>
						</div>
					</div>
					<div class="row">
						<div class="col-lg-4 col-md-4 col-sm-4 col-xs-4 form-group">
							<label for="">All prices are in</label><br> <span
								class="data-value"> <s:property value="%{currencyName}" />
							</span>
						</div>
                        <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4 form-group">
                            <label for="">Payment Region</label><br> <span
                                class="data-value"> <s:property value="%{invoice.region}" />
                            </span>
                        </div>
						<div class="col-lg-4 col-md-4 col-sm-4 col-xs-5 form-group">
							<label for="">Quantity</label><br> <span class="data-value">
									<s:property value="%{invoice.quantity}" />
							</span>
						</div>
						<div class="col-lg-4 col-md-4 col-sm-4 col-xs-3 form-group">
							<label for="">GstAmount</label><br> <span class="data-value">
									<s:property
										value="getFormatted('{0,number,##0.00}','(invoice.amount)*(invoice.gst/100)')" />
							</span>
						</div>
					</div>
					<div class="row">
						<div class="col-lg-4 col-md-4 col-sm-4 col-xs-4 form-group">
							<label for="">Expires on</label><br> <span
								class="data-value"> <s:property
										value="%{invoice.expiryTime}" />
							</span>
						</div>
						<div class="col-lg-4 col-md-4 col-sm-4 col-xs-5 form-group">
							<label for=""></label><br> <span class="data-value">
									<!-- <s:property value="%{invoice.quantity}" /> -->
							</span>
						</div>
						<div class="col-lg-4 col-md-4 col-sm-4 col-xs-3 form-group">
							<label for="">Amount</label><br> <span class="data-value">
									<s:property value="%{invoice.amount}" />
							</span>
						</div>
					</div>
					<div class="row">
						<div class="col-lg-4 col-md-4 col-sm-4 col-xs-4 form-group">

						</div>
						<div class="col-lg-4 col-md-4 col-sm-4 col-xs-5 form-group">

						</div>
						<div class="col-lg-4 col-md-4 col-sm-4 col-xs-3 form-group">
							<label for="" style="color: rgb(255, 0, 0) !important;">Total
								Amount</label><br> <span class="data-value"> <s:property
										value="%{invoice.totalAmount}" />
							</span>
						</div>
					</div>

					<div id="abc"
						class="col-lg-4 col-md-4 col-sm-4 col-xs-3 form-group"
						style="display: inline-flex;">
						<s:checkbox id="tnc" name="customerConsent"
							value="%{customerConsent}"
							style="margin-top:0px;height:20px;    font-weight:500; font-size:14px;">
							</s:checkbox>
						
						&nbsp; &nbsp; <label for="">I agree to <a href="#"
							target=_self onClick="pop()">Terms & Conditions</a></label> <span
							class="data-value"> </span>
								<span id="errorTnc" class="error" style="margin-top:20px;"></span>

					</div>
					<div class="modal" tabindex="-1" role="dialog" id="saleRequest">
						<div class="modal-dialog" role="document">
							<div class="modal-content">
								<div class="modal-header">
									<h5 class="modal-title">Terms & Conditions for Payment</h5>
									<button type="button" class="close" data-dismiss="modal"
										onclick="cancelbutton()" aria-label="Close">
										<span aria-hidden="true">&times;</span>
									</button>
								</div>
								<div class="modal-body">
									<p id="payText">
										<s:property value="%{invoice.payText}" />
									</p>
								</div>
								<div class="modal-footer">
									<button type="button" class="btn btn-default"
										data-dismiss="modal" onclick="cancelbutton()">Close</button>
								</div>
								<!--  <div class="modal-footer">
        <button type="button" class="btn btn-primary">Save changes</button>
        <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
      </div> -->
							</div>
						</div>
					</div>
					<div class="row">
						<div class="col-md-12 col-sm-12 form-group text-center">
							<input type="button" id="INVOICE_PAY_BTN"
								class="btn btn-success btn-md" value="Pay"
								onclick="submitForm();"> <!-- <s:submit id="INVOICE_PAY_BTN"   action="payPromoInvoice" class="btn btn-success btn-md" value="Pay" onclick="return submitForm()"></s:submit>
								 -->
						</div>
					</div>
				</div>
			</div>
		</div>
		<s:hidden name="token" value="%{#session.customToken}"></s:hidden>
	</form>

</body>
</html>