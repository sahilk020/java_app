<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="org.owasp.esapi.ESAPI"%>
<%@page import="com.pay10.commons.util.Amount"%>
<%@page import="com.pay10.commons.util.Currency"%>
<%@page import="com.pay10.commons.util.FieldType"%>
<%@taglib prefix="s" uri="/struts-tags"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width,initial-scale=1">
<link rel="icon" href="../image/favicon.png">
<title>IRCTC Pay</title>
<link rel="stylesheet" href="../css/paymentPage.css">
<script type="text/javascript" src="../js/PaymentCreditForm.js"></script>

<script type="text/javascript">
    var tabhide=0;
	
    var timeout = '<%=5 * 60 * 1000%>';

	var timer = setInterval(function() {
		timeout -= 1000;
		window.status = time(timeout);
		if (timeout == 0) {
			clearInterval(timer);
			alert('Your session has been expired !');
			window.location = "sessionTimeout";
		}
	}, 1000);

	function two(x) {
		return ((x > 9) ? "" : "0") + x
	}

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

		document.getElementById("lblSessionTime").innerText = "Your Session will expire in "
				+ t;
		return "You session will timeout in " + t + " minutes.";
	}
		
    function checkExMop(checkedValue){
		var checkExMopType = "<s:property  value="#session.CARD_PAYMENT_TYPE_MOP" />"
		if (checkedValue == "VI" && (checkExMopType.indexOf("VISA") < 0 )){
			document.getElementById('cardSupportedEX').innerHTML = "This card is no longer supported";
		}
		else if (checkedValue == "MC" && (checkExMopType.indexOf("MASTER") < 0 )){
			document.getElementById('cardSupportedEX').innerHTML = "This card is no longer supported";
			}
		else if (checkedValue == "MS" && (checkExMopType.indexOf("MAESTRO") < 0 )){
			document.getElementById('cardSupportedEX').innerHTML = "This card is no longer supported";
			}
		else if (checkedValue == "AX" && (checkExMopType.indexOf("AMEX") < 0 )){
			document.getElementById('cardSupportedEX').inne
			rHTML = "This card is no longer supported";
			}
		else if (checkedValue == "DN" && (checkExMopType.indexOf("DINERS") < 0 )){
			document.getElementById('cardSupportedEX').innerHTML = "This card is no longer supported";
			}
		else if (checkedValue == "RU" && (checkExMopType.indexOf("RUPAY") < 0 )){
			document.getElementById('cardSupportedEX').innerHTML = "This card is no longer supported";
			}
		else{
			document.getElementById('cardSupportedEX').innerHTML = "";
		}
	}
	

</script>

<style>
.tab5 input[type='text']{
    border:none;
     background: none;
     width: 70%;
     padding: 3px;
     border-bottom: 2px solid #ddd;
     box-shadow: none;
     margin-bottom: 13px;
}
.tab5 label{
	color: #999;
}
.red1, .red2{
	color: red;
	display:none;
  
}
.payActive{
	background: #0C84fC;
	border-color: #0C84fC;
}


</style>

</head>
<body OnLoad="getExForm();setInterval(1); displayDefault();getExForm(); ">
	<!-- header -->
	<div class="container">
		<div id="header" class="clearfix">
				<span class="logo"><img src="../image/irctcipay.png"" alt="IRCTCpay"></span>
   				  <span class="logo-mth">Select Payment Method</span> 
					<span class="logo-ssn">
					  <strong><s:label name="lblSessionTime" id="lblSessionTime"></s:label></strong>
				    </span>
				
			</div>
	</div>
	<!-- mid section -->
	<div class="container midSec">
		<div id="article" class="clearfix">
			
			<!-- slidbar -->
			
			
			
			<div id="sidebar" class="order-length parent-div">
			<h2 class="align-order">Order Summary</h2>
			<dl>
				<dt id ="buyer">Buyer</dt>
				<dd id="customerName" name="customerName"><%=ESAPI.encoder().encodeForHTML((String) session.getAttribute(FieldType.CUST_NAME.getName()))%></dd>
				<dt><%=ESAPI.encoder().encodeForHTML((String) session.getAttribute(FieldType.INDUSTRY_ID.getName()))%></dt>
				<td align="left" valign="middle"></td>
				<dd id="orderId" name="orderId"><%=ESAPI.encoder().encodeForHTML((String) session.getAttribute(FieldType.ORDER_ID.getName()))%></dd>
				
<%
					String currencyCodeAlpha1 = "";
					String formattedAmount1 = "";
					try {
						String currencyCodeNumeric1 = (String) session.getAttribute(FieldType.CURRENCY_CODE.getName());
						String amount1 = (String) session.getAttribute(FieldType.AMOUNT.getName());

						currencyCodeAlpha1 = Currency.getAlphabaticCode(currencyCodeNumeric1);
						formattedAmount1 = ESAPI.encoder().encodeForHTML(
														Amount.toDecimal(amount1, currencyCodeNumeric1));

					} catch (NullPointerException nPException) {
						response.sendRedirect("sessionTimeout");
					} catch (Exception exception) {

						response.sendRedirect("error");
					}
				%>
				<dt>Amount Payable</dt>
				<dd id="amount" name="">
				<big><%=currencyCodeAlpha1%> <%=formattedAmount1%></big>
			
				</dd>
			</dl>
		</div>
			
			
			
			
			

			<div id="paymentTabs">
				<ul class="paymentNavs">
				    <s:if test="%{#session.EXPRESS_PAY_FLAG == true}">
					<s:if test="%{#session.TOKEN == 'NA'}"></s:if>
					<s:else>
				       <li id="tabs-1"  onclick="showStuff('tabs-1',1)"><a id="sd" href="#">Saved Details <span> > </span></a></li>
					</s:else>
					</s:if> 
					
				
					<s:iterator value="#session.PAYMENT_TYPE_MOP">							
						<s:if test="%{key=='CC'}">
							<s:set var="checkMopCC" value="true" />
						</s:if>
						<s:if test="%{key=='DC'}">
							<s:set var="checkMopDC" value="true" />
						</s:if>
							<s:if test="%{key=='NB'}">
						<s:set var="checkMopNB" value="true" />
							</s:if>
						<s:if test="%{key=='DP'}">
							<s:set var="checkMopDP" value="true" />
						</s:if>
					</s:iterator>

					<s:if test="%{#checkMopCC == true && #checkMopDC == true}" >
						<li id="tabs-2" onclick="showStuff('tabs-2',2)"><a id="ccdc" href="#">Credit / Debit Card </a></li>						
					</s:if>
					
					<s:elseif test="%{#checkMopCC == true && #checkMopDC != true}">
					     <li id="tabs-2" onclick="showStuff('tabs-2',2)"><a id="ccdc" href="#">Credit Card <span> > </span></a></li>
					</s:elseif>
					
					<s:elseif test="%{#checkMopCC != true && #checkMopDC == true}">
						<li id="tabs-2" onclick="showStuff('tabs-2',2)"><a id="ccdc" href="#">Debit Card <span> > </span></a></li>
					</s:elseif>
					
					<s:if test="%{key=='NB'}">
					    <li id="tabs-4" onclick="showStuff('tabs-4',4)"><a id="nb" href="#">Net Banking <span> > </span></a></li>
					</s:if>
					
					<s:if test="%{key=='DP'}">
				 	    <li id="tabs-3" onclick="showStuff('tabs-2',3)"><a id="dcwp" href="#">Debit Card with pin<span> > </span></a></li>
					</s:if>
					<li id="tabs-5" onclick="showStuff('tabs-5',5)"><a id="upi" href="#">UPI<span></span></a></li>
				</ul>
			</div>
			
			
			<!-- paymenttabs -->
	<div class="paymentSections">
		<s:if test="%{#session.EXPRESS_PAY_FLAG == true}">
		<s:if test="%{#session.TOKEN == 'NA'}"></s:if>
		<s:else> 
			<div id="tabs1" style="display: block;">
				<ul class="savedCards">
					<s:form autocomplete="off" name="exCard" method="post" action="pay" id="exCard" onsubmit="return submitEXform();" >
						<input type="hidden" name="paymentType" value="EX" />
							<small id="cardSupportedEX" class="text-danger text-left"></small>
								<s:iterator value="#session.TOKEN" status="incrementer">
						<li>
										<!-- <div> -->
										
                                        <div class="card-dtls"> 
										<input type="radio" name="tokenId"
											id="tokenId<s:property value="key" />"
											onclick="('<s:property value="%{value.mopType}"/>');handleClick(this);"
											value="<s:property value="key" />"> 
											<s:if test="%{value.mopType=='VI'}">
												<label for="tokenId<s:property value="key" />"><i></i>
													<img src="../image/visa.png" alt="Visa"></label>
												<s:hidden id="mopType" name="mopType" value="%{value.mopType}" />
														 <s:hidden id="issuerBankName" name ="issuerBankName" value="%{value.cardIssuerBank}"/> 
								 <s:hidden id="issuerCountry" name ="issuerCountry" value="%{value.cardIssuerCountry}"/>
												<span class="m-top"> <s:textfield name="cardNumber"
														autocomplete="off" id="exCardNumber%{key}"
														cssClass="form-control1 transparent-input"
														value="%{value.cardMask}" theme="simple" readonly="true" /></span>
 <small class="text-muted"><s:if test="%{value.paymentType=='CC'}">Credit Card</s:if>
    <s:else>Debit Card</s:else> <s:if test="%{value.cardIssuerBank !=null}"  > | <s:property value="%{value.cardIssuerBank}" /> </s:if>     </small>
    
												
								
												<div class="savedCvv">
												         <s:textfield type="password"  name="cvvNumber" id="cvvNumber%{key}" maxlength="3" autocomplete="off" onkeypress="return isNumberKey(event);return disableEnterPress(event);" onkeyup="enableExButton(this);"   class="pField" placeholder="CVV" disabled="true" />         
												</div>
												

												<a href="#" id="deleteButton<s:property value="key" />"
												action="deletecard"	onclick="deleteButton(<s:property value="key" />)"
													class="close"><img src="../image/close.png" alt="Close Icon" width="13" height="13" /></a>
												
											</s:if> 
											
										
                                       
											<s:if test="%{value.mopType=='MC'}">
												<label for="tokenId<s:property value="key" />"><i></i>
													<img src="../image/mastercard.png" alt="Master Card"></label>

												<span class="m-top"> <s:textfield name="cardNumber"
														autocomplete="off" id="exCardNumber%{key}"
														cssClass="form-control1 transparent-input"
														value="%{value.cardMask}" theme="simple" readonly="true" /></span>
                                                <s:hidden id="mopType" name="mopType" value="%{value.mopType}" />
												 <s:hidden id="issuerBankName" name ="issuerBankName" value="%{value.cardIssuerBank}"/> 
								 <s:hidden id="issuerCountry" name ="issuerCountry" value="%{value.cardIssuerCountry}"/>
												<small class="text-muted"><s:if test="%{value.paymentType=='CC'}">Credit Card</s:if>
    <s:else>Debit Card</s:else> <s:if test="%{value.cardIssuerBank !=null}"  > | <s:property value="%{value.cardIssuerBank}" /> </s:if>     </small>
    
													
												<div class="savedCvv">
													<s:textfield type="password"  name="cvvNumber" id="cvvNumber%{key}" maxlength="3" autocomplete="off" onkeypress="return isNumberKey(event);return disableEnterPress(event);" onkeyup="enableExButton(this);"   class="pField" placeholder="CVV" disabled="true" /> 
												</div>
												<a href="#" id="deleteButton<s:property value="key" />"
													onclick="deleteButton(<s:property value="key" />)"
													class="close"><img src="../image/close.png" alt="Close Icon" width="13" height="13" /></a>

											</s:if>
											</div>
											<!-- </div> -->
											</li>
									</s:iterator>
									
									<div class="CTA mob-btn">
											<s:submit key="Pay"	name="exSubmit" theme="simple"
											id="exSubmit" disabled="true"  class="payment-btn float" value="PAY"></s:submit>
											</div>
											<a href="#" onclick="myCancelAction();" key="ReturnToMerchant" id="ccCancelButton1" class="float-save" name="ccCancelButton" theme="simple">Return to merchant</a>
											<s:hidden name="customToken" id="customToken1" value="%{#session.customToken}"></s:hidden>
								</s:form>
							</ul>

						</div>
						</s:else>
						</s:if>
					
				<div id="tabs2" style="display: none;" class="mob-bottom">


					<s:form autocomplete="off" name="creditcard-form" method="post"
						action="pay" id="creditCard" onsubmit="return enablePayButton();">

						<div class="cardSection" >
							<ul class="nbSection" id="tabimages">	
							<li><img src="../image/hdfc.jpg" id="HDFC Bank"  class=" " alt="HDFC Bank" name="bankname"></li>
							<li><img src="../image/sbi.jpg" id="SBI" alt="SBI" class=" "  name="bankname"></li>
							<li><img src="../image/axis.jpg"  id="AXIS" class=" " alt="AXIS" name="bankname"></li>
							<li><img src="../image/icici.jpg" id="ICICI" class=" " alt="ICICI" name="bankname"></li>
							</ul>

						<div class="clearfix"></div>
							<div class="pRow" id="divCardNumber" >
								<label class="pLabel">Card Number</label>
									
								<s:set var="valid cardnumber" value="getText('CardNumber')" />
								<s:hidden id="cardPlaceHolderCC" value="%{cardNumberTextCC}" />
								<s:hidden id="validCardDetail" value="%{valid cardnumber}" />
								
								<input class="pField masked" type="text" name="cardNumber" value="" placeholder="XXXX XXXX XXXX XXXX" id="cardNumber"
									onkeypress="return isNumberKey(event)" onblur="checkLuhn(this,tabhide); enablePayButton()"
									onpaste ="return handlePaste()" maxlength="19" autocomplete="nope" readonly onfocus="this.removeAttribute('readonly');" pattern="[3-6][0-9 ]{15,19}" data-exception="cardNumber" style="padding:0px !important;">
								<div>
									<small id="demo" class="text-danger"></small>
								</div>
								
								 <s:hidden id="paymentType2" name="paymentType" value=""  />
								 <s:hidden id="mopTypeCCDiv2" name="mopType" value=""  />
								 <s:hidden id="issuerBankName2" name ="issuerBankName" value=""/> 
								 <s:hidden id="issuerCountry2" name ="issuerCountry" value=""/>	
                            
						    </div>
							
							<div class="pRow-right" id="credit_cards">
                              <img src="../image/visa-small.png" alt="Visa" class="cardType" id="VI" name="mopImageCC">
							  <img src="../image/mastercard-small.png" alt="Master Card" class="cardType" id="MC" name="mopImageCC"> 
							</div>
							<div class="clearfix"></div>
							<div class="validity" id="">
								<div class="pRow">
									<label class="pLabel">Valid Through</label>

									<s:set var="valid expiry"
										value="getText('Entervalidexpirydate')" />
									<s:hidden id="validExpiryDate" value="%{valid expiry}" />
									<small id="chkExpiry" class="text-danger"
										style="position: absolute; margin-top: 30px;"></small>

									<spam id="divExpiryMonth"> <s:select   headerKey=""
										headerValue="MM" cssClass="pField"
										list="#{'01':'01','02':'02','03':'03','04':'04','05':'05','06':'06','07':'07','08':'08','09':'09','10':'10','11':'11','12':'12'}"
										name="ExpiryMonth" id="ccExpiryMonth" value="0"
										onchange="CheckExpiry(); enablePayButton()" /> </spam>
									<div>
										<small id="mo-error" class="text-danger"></small>
									</div>
									<spam id="divExpiryYear"> <s:select headerKey=""
										headerValue="YY" cssClass="pField"
									
										list="#{'2018':'2018','2019':'2019','2020':'2020','2021':'2021','2022':'2022','2023':'2023','2024':'2024','2025':'2025','2026':'2026','2027':'2027','2028':'2028','2029':'2029','2030':'2030','2031':'2031','2032':'2032','2033':'2033','2034':'2034','2035':'2035','2036':'2036','2037':'2037','2038':'2038','2039':'2039','2040':'2040','2041':'2041','2042':'2042','2043':'2043','2044':'2044','2045':'2045','2046':'2046','2047':'2047','2048':'2048','2049':'2049'}"
										name="ExpiryYear" id="ccExpiryYear" value="0"
										onchange="CheckExpiry(); enablePayButton()" /></spam>
									<div>
										<small id="yy-error" class="text-danger"></small>
									</div>
								</div>
							</div>
							<div class="cvv">
								<div class="pRow" id="divCvv">
									<s:set var="cvv textimage" value="getText('CVVlen3image')" />
									<s:set var="cvv textimageAX" value="getText('CVVlen4image')" />

									<s:hidden id="cvvTextImage" value="%{cvv textimage}" />
									<s:hidden id="cvvTextImageAX" value="%{cvv textimageAX}" />

									<label class="pLabel">CVV</label>
										
										<input class="pField" type="text" name="cvvNumber" value="" placeholder="CVV" id="cvvNumber"
									     onkeypress="return isNumberKey(event)" maxlength="3"  autocomplete="nope" readonly onfocus="this.removeAttribute('readonly');">
									
									<span onMouseOut="Hide()" onMouseOver="Show()" onMouseMove="Show()" class="cvvcard">&nbsp;</span>
									<div id="cvvtext"></div>
									<div>
										<small id="cvv-error" class="text-danger"></small>
									</div>
								</div>
							</div>
							<div class="clearfix"></div>
							<div class="pRow" id="divName">
								<s:set var="NameonCard" value="getText('NameonCard')" />
								<label class="pLabel" style="margin-top:-16px;">Name on Card</label> <input class="pField"
									type="text" name="cardName" id="cardName"
									onkeypress="noSpace(event,this);return isCharacterKey(event);"
									onKeyup="return enablePayButton()"
									 placeholder="Name">
								<div>
									<small id="name-error" class="text-danger"></small>
								</div>
							</div>

							<div class="pRow" id="divSaveCard">
								<s:if test="%{#session.EXPRESS_PAY_FLAG == true}">
								<s:checkbox name="cardsaveflag" checked="checked"
									id="cardsaveflag1" /><span class="chk-save">Save Cards</span>
								</s:if>
							</div>

						</div>

						<div class="CTA mob-btn" id="pay-now">

							<input type="submit" class="payment-btn" id="confirm-purchase"
								value="Pay">					
							<div>
									
								<small id="errorBox" class="text-danger"></small>
							</div>
						</div>														
						<a href="#" onclick="myCancelAction();" key="ReturnToMerchant" id="ccCancelButton1" class="float1" name="ccCancelButton" theme="simple">Return to merchant</a>	</br>
						<small class="CTAInfo">Your cards details will be securely
							saved for faster payments.</br> your CVV will not be stored. 
						</small>
						<%
									String currencyCodeAlpha = "";
												String formattedAmount = "";
												try {
													String currencyCodeNumeric = (String) session
															.getAttribute(FieldType.CURRENCY_CODE.getName());
													String amount = (String) session.getAttribute(FieldType.AMOUNT.getName());
													if(amount.equals("null")){
														response.sendRedirect("error");
													}												
													currencyCodeAlpha = Currency.getAlphabaticCode(currencyCodeNumeric);
								formattedAmount = ESAPI.encoder().encodeForHTML(
														Amount.toDecimal(amount, currencyCodeNumeric));
												} catch (NullPointerException nPException) {
													response.sendRedirect("sessionTimeout");
												} catch (Exception exception) {

													response.sendRedirect("error");
												}
								%>
						<input type="hidden" id="amount" name="amount"
							value=<%=formattedAmount%>>
							
							<s:hidden name="customToken" id="customToken2" value="%{#session.customToken}"></s:hidden>
					</s:form>
				</div>
           
                   <div id="tabs4" style="display: none;" class="mob-bottom">
                     <ul class="nbSection">					 
                            <li><img src="../image/hdfc.jpg" id=""  alt="HDFC" /></li>
							<li><img src="../image/sbi.jpg"  id=""  alt="SBI"  /></li>
							<li><img src="../image/axis.jpg"  id="" alt="AXIS" /></li>
							<li><img src="../image/icici.jpg" id="" alt="ICICI" /></li>
                     </ul>
                     <div class="clearfix"></div><br>
                     <div class="pRow">
                         <label class="pLabel">Select Bank</label>
                         <select class="pField" name="Bank" id="" >
                            <option>Select Bank</option>
							<option value="../image/hdfc.jpg">HDFC Bank</option>
							<option value="../image/axis.jpg">Axis Bank</option>
							<option value="../image/icici.jpg">ICICI Bank</option>
							<option value="../image/sbi.jpg">SBI Bank</option>
                            <option value="Allahabad Bank">Allahabad Bank</option>
                            <option value="Andhra Bank">Andhra Bank</option>
                            <option value="Bank of Baroda">Bank of Baroda</option>
                            <option value="Bank of India">Bank of India</option>
                            <option value="Bank of Maharashtra">Bank of Maharashtra</option>
                            <option value="Canara Bank">Canara Bank</option>
                            <option value="Central Bank of India">Central Bank of India</option>
                            <option value="Corporation Bank">Corporation Bank</option>
                            <option value="Dena Bank">Dena Bank</option>
                            <option value="Indian Bank">Indian Bank</option>
                            <option value="Indian Overseas Bank">Indian Overseas Bank</option>
                            <option value="IDBI Bank">IDBI Bank</option>
                            <option value="Oriental Bank of Commerce">Oriental Bank of Commerce</option>
                            <option value="Punjab & Sindh Bank">Punjab & Sindh Bank</option>
                            <option value="Punjab National Bank">Punjab National Bank</option>
                            <option value="State Bank of India">State Bank of India</option>
                            <option value="Syndicate Bank">Syndicate Bank</option>
                            <option value="UCO Bank">UCO Bank</option>
                            <option value="Union Bank of India">Union Bank of India</option>
                            <option value="United Bank of India">United Bank of India</option>
                            <option value="Vijaya Bank">Vijaya Bank</option>
                            <option value="Axis Bank">Axis Bank</option>
                            <option value="Bandhan Bank">Bandhan Bank</option>
                            <option value="City Union Bank">City Union Bank</option>
                            <option value="DCB Bank">DCB Bank</option>
                            <option value="Dhanlaxmi Bank">Dhanlaxmi Bank</option>
                            <option value="Federal Bank">Federal Bank</option>
                            <option value="HDFC Bank">HDFC Bank</option>
                            <option value="ICICI Bank">ICICI Bank</option>
                            <option value="IDFC Bank">IDFC Bank</option>
                            <option value="IndusInd Bank">IndusInd Bank</option>
                            <option value="Jammu and Kashmir Bank">Jammu and Kashmir Bank</option>
                            <option value="Karnataka Bank">Karnataka Bank</option>
                            <option value="Karur Vysya Bank">Karur Vysya Bank</option>
                            <option value="Kotak Mahindra Bank">Kotak Mahindra Bank</option>
                            <option value="Lakshmi Vilas Bank">Lakshmi Vilas Bank</option>
                            <option value="RBL Bank">RBL Bank</option>
                            <option value="South Indian Bank">South Indian Bank</option>
                            <option value="Tamilnad Mercantile Bank">Tamilnad Mercantile Bank</option>
                            <option value="YES Bank">YES Bank</option> 
                         </select>
                     </div>
                     <small class="CTAInfo-nb">You will be redirected to a secure bank site to complete payment.<br> After the transaction is successful, you'll return to your account.</small>
                     <div class="CTA-nb">
                         <button type="button" class="payment-btn">Pay</button>
                     </div>
					<a href="#" onclick="myCancelAction();" key="ReturnToMerchant" id="ccCancelButton4" class="float1-nb" name="ccCancelButton" theme="simple">Return to merchant</a>	
                 </div>

                 <div id="tabs5" class="tab5" style="display: none;">
                 	<label>VPA</label><br>
                 	  <input type="text" name="VPA" id="vpaCheck" onkeyup="isValidVpa(); enableButton();"><br>
                 	<p class="red1" id="red1">please enter  valid VPA</p>
					
                 	<label>Name</label><br>
                 	<input type="text" name="Name" id="nameCheck" onkeyup="alphabeticOnly(); enableButton();">
                 	<p class="red2" id="red2">please enter  valid Name</p>
					
					
                 	<div class="CTA mob-btn" id="pay-now">

							<input type="submit" class="payment-btn" id="upi-sbmt" value="Pay" disabled>					
							<div>
									
								<small id="errorBox" class="text-danger"></small>
							</div>
						</div>
						<p>														
						<a href="#" onclick="myCancelAction();" key="ReturnToMerchant" id="ccCancelButton1" class="float1" name="ccCancelButton" theme="simple">Return to merchant</a>
					</p>
                 </div>




			</div>
			<!-- paymentsection -->


		</div>
		
	</div>
	<!-- /.container -->
	<!-- footer -->
	<div class="container">
		<div class="footer">
			<div class="left-footer">
				<img src="../image/mastercard-securecode.png"
					alt="Master Secure Card"> <img
					src="../image/verified-by-visa.png" alt="Verified By Visa"> <img
					src="../image/download.png" alt="Download">
			</div>
			<div class="right-footer">
				<div class="safe-footer">
					<span>Safe and Secure Payments.</span>
					 <small class="powered">Powered by</small>
			<img src="../image/280x40.png" alt="Pay10 Logo" class="paylogo">
				</div>
				</div>
			
		</div>
	</div>
<script type="text/javascript">
var masking = {
  // User defined Values
  creditCardId : 'cardNumber', 
  maskedNumber : 'X',
  
  // object re: credit cards
  cardMasks :  {
                3: {
                  'placeholder' : 'XXX XXXXXX XXXXX',
                  'pattern' : '3\[47\]\\d \\d{6} \\d{5}',
                  'regex': /^3[47]/,
                  'regLength' : 2
                  
                },
                4: {
                  'placeholder' : 'XXXX XXXX XXXX XXXX',
                  'pattern' : '4\\d{3} \\d{4} \\d{4} \\d{4}',
                  'regex' : /^4/,
                  'regLength' : 1
                },
                5: {
                  'placeholder' : 'XXXX XXXX XXXX XXXX',
                  'pattern' : '5\[1-5\]\\d{2} \\d{4} \\d{4} \\d{4}',
                  'regex' : /^5[1-5]/,
                  'regLength' : 2
                },
                6: {
                  'placeholder' : 'XXXX XXXX XXXX XXXX',
                  'pattern' : '(6011 \\d\\d|6221 2\[6-9\]|6221 3\\d|622\[2-8\] \\d\\d|6229 \[01\]\\d|6229 2\[0-5\]|6226 4\[4-9\]|65\\d\\d \\d\\d)\\d{2} \\d{4} \\d{4}',
                  'regex' : /^(6011|6221 2[6-9]|6221 3|622[2-8]|6229 [01]|6229 2[0-5]|6226 4[4-9]|65)/,
                  'regLength' : 7
                }
            },

  init: function () {
    masking.createShell(document.getElementById(masking.creditCardId));
    masking.activateMasking(document.getElementById(masking.creditCardId));
  },
  
  // replaces each masked input with a shall containing the input and it's mask.
  // this should be credit card render in react
  createShell : function (input) {
    var text = '', 
     placeholder = input.getAttribute('placeholder');

    input.setAttribute('data-placeholder', placeholder);
    input.setAttribute('data-original-placeholder', placeholder);
    input.removeAttribute('placeholder');

    text = '<span class="shell">' +
      '<span aria-hidden="true" id="' + input.id + 
      'Mask""><i></i>' + placeholder + '</span>' + 
      input.outerHTML +
      '</span>';

    input.outerHTML = text;
  },

  setValueOfMask : function (value, placeholder) {
    return "<i>" + value + "</i>" + placeholder.substr(value.length);
  },
  
  // add event listeners. only did IE9+. Do we need attach Event?
  activateMasking : function (input) {
        input.addEventListener('keyup', function(e) {
          masking.handleValueChange(e);
        }, false); 
        input.addEventListener('blur', function(e) {
          masking.handleBlur(e);
        }, false);
        input.addEventListener('focus', function(e) {
          masking.handleFocus(e);
        }, false);
  },
  
  handleValueChange : function (e) {
    var id = e.target.getAttribute('id'),
        currentMaskValue = document.querySelector('#' + id + 'Mask i'),
        currentInputValue = e.target.value = e.target.value.replace(/\D/g, "");
  
    // if there is no correct mask or if value hasn't changed, move on
    if(!currentMaskValue  || currentInputValue == currentMaskValue.innerHTML) {
      return; 
    } 
    
    // If value is empty, not a number or out of range, remove any current cc type selection
    if (!currentInputValue || currentInputValue[0] < 3 || currentInputValue[0] > 6) {
      e.target.value = '';
      masking.returnToDefault(e);
      return;
    } 
    
    // everything is right in the universe
    masking.setCreditCardType(e, currentInputValue[0]);

  },

  setCreditCardType : function (e, firstDigit) {
    var cc = masking.cardMasks[firstDigit],
        mask = document.getElementById(e.target.id + 'Mask');
    
      // set the credit card class
      e.target.parentNode.parentNode.classList.add(cc.card);

      // set the credit card regex
      e.target.setAttribute('pattern', cc.pattern);
    
      // set the credit card pattern
      e.target.setAttribute('data-placeholder', cc.placeholder);
    
      // handle the current value
      e.target.value = masking.handleCurrentValue(e);
    
      // set the inputmask
      mask.innerHTML = masking.setValueOfMask(e.target.value, cc.placeholder);
  },
  
  returnToDefault : function(e) {
    
      var input = e.target,
          placeholder = input.getAttribute('data-original-placeholder');
    
      // set original placeholder
      input.setAttribute('data-placeholder', placeholder);
      document.getElementById(e.target.id + 'Mask').innerHTML = "<i></i>" + placeholder;
    
      // remove possible credit card classes
     input.parentNode.parentNode.classList.remove('error'); 
     for (var i = 3; i <= 6; i++) {
        e.target.parentNode.parentNode.classList.remove(masking.cardMasks[i].card);
     } 
    
    // make sure value is empty
    input.value = '';
  },
  
  handleFocus : function (e) {
    var parentLI = e.target.parentNode.parentNode;
    parentLI.classList.add('focus');
  },

  handleBlur : function (e) {
    var parentLI = e.target.parentNode.parentNode,
        currValue = e.target.value,
        pattern, mod10, mod11 = true;
      
      // if value is empty, remove label parent class
      if(currValue.length == 0) {
        parentLI.classList.remove('focus');
      } else {
        pattern = new RegExp(e.target.getAttribute('pattern'));
        if (mod11 && currValue.replace(/\D/g,'').length == 16) {
            
        }
        if (currValue.match(pattern) && masking.testMod10(currValue)) {
          parentLI.classList.remove('error');
        } else {
          parentLI.classList.add('error');
        }
      }
  },
  
  testMod10: function(value) {
    var strippedValue = value.replace(/\D/g,''), // numbers only
        len = strippedValue.length, // 15 or amex, all others 16
        digit = strippedValue[len-1], // tester digit
        i, myCheck,
        total = 0,
        temp;  
     for (i = 2; i <= len; i++) {
        if(i % 2 == 0) {
          temp = strippedValue[len-i] * 2;
          if(temp >= 10) {
            total += 1 + (temp % 10);
          } else {
            total += temp * 1;
          }
        } else {
          total += strippedValue[len-i] * 1;
        }
     }
      myCheck = (10 - (total % 10)) % 10;
     return ((myCheck + 1) % 10) == digit;
  }, 
  
  testMod11: function (value) {
     var strippedValue = value.replace(/\D/g,''), // numbers only
        len = strippedValue.length, // usually 16
        digit = strippedValue[len-1], // tester digit
        testDigits = strippedValue.substr(0, len - 1), // 15 or 12 digits
        i, myCheck,
        total = 0,
        temp;
    
    for (i = len - 1; i > 0;) {
        temp = Number(testDigits[--i]);
        if (i % 2 == 0) {
            temp *= 2;
        }
 
        if (temp > 9) {
            temp -= 9;
        }
 
        total += temp;
       
    }
   
    
    /*
       if card number is 16 digit, then fetch first 15 digits (card15) and last digit is check-digit
       else if card number is 13 digit, then fetch first 12 digits (card12) and last digit is check-digit
       as we don't have 13 digit card numbers, we're only doing 16 test.*/
  
    
    myCheck = (10 - (total % 10)) % 10;
    myCheck = (myCheck + 1) % 10;
   
    var  PAN = '' + testDigits + myCheck;
    if( myCheck == digit && PAN == strippedValue) {
      return true;
    } else {
      return false;
    }
  },
  
  // tests whether there is an error in the credit card number at a specifi
  testRegExProgression: function (e, value) {
    var cc = masking.cardMasks[value[0]];
    if (value.length >= cc.regLength) {
      if (!cc.regex.test(value) && !e.target.parentNode.parentNode.classList.contains('error')) {
        // show error message instead
        e.target.parentNode.parentNode.classList.add('error');
        masking.errorOnKeyEntry('You have an error in your credit card number', e);
      }
    } else {
      // remove error notfications if they deleted the excess characters
      e.target.parentNode.parentNode.classList.remove('error');
    }
    
  },

  handleCurrentValue : function (e) {
    var placeholder = e.target.getAttribute('data-placeholder'),
        value = e.target.value, l = placeholder.length, newValue = '', 
        i, j, isInt, strippedValue;
    
    // strip special characters
    strippedValue = value.replace(/\D/g, "");
    
    for (i = 0, j = 0; i < l; i++) {
        var x = 
        isInt = !isNaN(parseInt(strippedValue[j]));
        matchesNumber = masking.maskedNumber.indexOf(placeholder[i]) >= 0;

        if (matchesNumber && isInt) {
            newValue += strippedValue[j++];
        } else if (!isInt && matchesNumber)  {
            // masking.errorOnKeyEntry(); // write your own error handling function
            return newValue; 
        } else {
            newValue += placeholder[i];
        } 
        // break if no characters left and the pattern is non-special character
        if (strippedValue[j] == undefined) { 
          break;
        }
    }
    masking.testRegExProgression(e, newValue);
    
    return newValue;
  },

  errorOnKeyEntry : function (message, e) {
   
  }
}
masking.init();
var foo = document.getElementById('cardNumber')
</script>
</body>
</html>
