<!DOCTYPE html
  PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@taglib prefix="s" uri="/struts-tags"%>
<%@ page import="org.owasp.esapi.ESAPI"%>
<%@page import="com.pay10.commons.util.PropertiesManager"%>
<%@page import="com.pay10.commons.util.CurrencyNumber"%>
<html>

<head>
<title>BestPay</title>
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<link rel="icon" href="../image/98x98.png">
	<meta name="viewport"
		content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" />

	<!-- CSS Files -->
	<!-- <link href="../css/material-dashboard.css?v=2.1.0" rel="stylesheet" /> -->
	<link href="../css/paymentLink.css" rel="stylesheet" type="text/css" />
	<link href="../css/bootstrap.min.css" rel="stylesheet" type="text/css" />
	<script src="../js/jquery.min.js"></script>

	<script>

$(document).ready(function() {
	let dropdown = document.querySelector('#dropdown');

	let avgGrade = document.getElementById('abhi').value;
	var keyValuePairs = avgGrade.slice(1, -1).split(', ');

	// Create a new Map

	
					var s="";

	// Iterate through the key-value pairs and add them to the Map
	keyValuePairs.forEach(pair => {
	    var data = pair.split('=');

		 let option = document.createElement('option');
		  option.text = data[0];
		  option.value = data[1];
		  s += '<option value="' +  data[1] + '">'
			+ data[0]
			+ '</option>';

	    
	});
		document.getElementById("CURRENCY_CODE").innerHTML=s;


	// Output the Map

});

</script>
	<script>


    function formLoad() {
      var messageDiv =  document.getElementById('actionMessageDiv');
     // console.log(messageDiv.innerText);
      if(messageDiv.innerText.trim()=="SUCCESS"){
        

    	  messageDiv.style.display = "none";
      }else{
        messageDiv.style.display = "block";
      }
      var enablePayNow = '<s:property value="%{enablePay}" />';
      if (enablePayNow == "TRUE") {
        document.getElementById('INVOICE_PAY_BTN').disabled = false;
      }
      else {
        document.getElementById('INVOICE_PAY_BTN').disabled = true;
        document.getElementById('INVOICE_PAY_BTN').style.display = "none";

      }
      
     
    };

  </script>

	<script>
	var multiplier;
	function autoPop() {
		document.getElementById("ORDER_ID").value = "CASH"
				+ String(new Date().getTime()); //	Autopopulating orderId
		
				
	};
	
	/* <script type="text/javascript"> */
    function submitForm() {
		debugger
		var form = document.forms[0];
		inputElements = form.getElementsByClassName("signuptextfield");
			
		var Name =  document.getElementById('CUST_NAME').value;
	    var phone =  document.getElementById('CUST_PHONE').value;
	    var email =  document.getElementById('CUST_EMAIL').value;
	    var amount =  document.getElementById('AMOUNT').value;
		var Username=/^[a-zA-Z]+(([',. -][a-zA-Z ])?[a-zA-Z]*)*$/;
		var Email= /^[\w-\.]+@([\w-]+\.)+[\w-]{2,4}$/;
		var Number= /^\d+$/;
		if (Name == "" && phone == "" && email == "" && amount == "") {
		 $('#errorMsgName').text('Please enter name.');
		 $('#errorMsgMobile').text('Please enter Mobile Number.');
		 $('#errorMsgEmialid').text('Please enter your email Id.');
		 $('#errorMsgAmount').text('Please enter Amount.');

		 $("#errorMsgName").show();
		 $("#errorMsgMobile").show();
		 $("#errorMsgEmialid").show();
		 $("#errorMsgAmount").show();
		 setTimeout(function () {
			 $("#errorMsgName").hide();
			 $("#errorMsgMobile").hide();
			 $("#errorMsgEmialid").hide();
			 $("#errorMsgAmount").hide();
			}, 5000); 
		}
		else if (!Username.test(Name)) {
					 $('#errorMsgName').text('Please enter name in characters only .');
				        document.getElementById('CUST_NAME').focus();
				        $('#errorMsgName').show();
						setTimeout(function () {
							$('#errorMsgName').hide();
						}, 4000);
						return false;
				}
				else if (!Number.test(phone) || phone.length < 8 || phone.length > 15) {
					 $('#errorMsgMobile').text('Please enter valid 8 to 15 digit Phone Number .');
				        document.getElementById('CUST_PHONE').focus();
				        $('#errorMsgMobile').show();
						setTimeout(function () {
							$('#errorMsgMobile').hide();
						}, 4000);
						return false;
				}
				else if (!Email.test(email)) {
					 $('#errorMsgEmialid').text('Please enter valid email Id.');
				        document.getElementById('CUST_EMAIL').focus();
				        $("#errorMsgEmialid").show();
						setTimeout(function () {
							$("#errorMsgEmialid").hide();
						}, 4000);
						return false;
				}
				else if (amount=="") {
					 $('#errorMsgAmount').text('Please enter Amount.');
				        document.getElementById('AMOUNT').focus();
				        $("#errorMsgAmount").show();
						setTimeout(function () {
							$("#errorMsgAmount").hide();
						}, 4000);
						return false;
				}
// 				else if (!Number.test(amount) || parseInt(amount)==0 || amount=="") {
// 					 $('#errorMsgAmount').text('Please enter Amount.');
// 				        document.getElementById('AMOUNT').focus();
// 				        $("#errorMsgAmount").show();
// 						setTimeout(function () {
// 							$("#errorMsgAmount").hide();
// 						}, 4000);
// 						return false;
// 				}
			else{			
			var valueArray = new Array();
			var sortedArray = new Array();
			var nameArray = [];
			for(i=0;i<inputElements.length;i++){
			   valueArray[inputElements[i].name]  = inputElements[i].value;
			   nameArray[i] =  inputElements[i].name;
			}
			nameArray.sort();
			var inputString = "";
			debugger
			var curr=$("#CURRENCY_CODE").val();
			
			var urls = new URL(window.location.href);
	          var domain = urls.origin;
	          
	         var  urlString= domain + "/crmws/CurrencyNumber/currency/";
			
	        
	          
			//  $.get(url, function(data, status){
			// 	 multiplier=data;
			// 	  });


				  $.ajax({
            type: "GET",
            url: urlString + curr,
            // url : "http://localhost:8080/crmws/commentlist?caseId="+ caseId,
            success: function (data, status) {
				console.log(data);
				multiplier =data;

            
				debugger
			console.log(multiplier);
			for(j=0;j<nameArray.length;j++){
				var element = nameArray[j];
				if(element == "AMOUNT"){
					
					inputString += "~";
					inputString += element;
					inputString += "="
					inputString += valueArray[element] * multiplier;
					
					document.getElementById("AMOUNT").value = valueArray[element] * multiplier;
				}else{
					inputString += "~";
					inputString += element;
					inputString += "="
					inputString += valueArray[element];
				}	
			}
			inputString = inputString.substr(1);
			inputString += document.getElementById("hashkey").value;

			var hash = Sha256.hash(inputString).toUpperCase();
			document.getElementById("HASH").value = hash;
			var paymentActionUrl = document.getElementById("paymentActionUrl").value;
            form.action = paymentActionUrl;
            form.submit();
			},
            error: function (status) {
            }
          });	  
			
		}
    }
	
	class Sha256 {

    /**
     * Generates SHA-256 hash of string.
     *
     * @param   {string} msg - (Unicode) string to be hashed.
     * @param   {Object} [options]
     * @param   {string} [options.msgFormat=string] - Message format: 'string' for JavaScript string
     *   (gets converted to UTF-8 for hashing); 'hex-bytes' for string of hex bytes ('616263' = 'abc') .
     * @param   {string} [options.outFormat=hex] - Output format: 'hex' for string of contiguous
     *   hex bytes; 'hex-w' for grouping hex bytes into groups of (4 byte / 8 character) words.
     * @returns {string} Hash of msg as hex character string.
     */
    static hash(msg, options) {
        const defaults = { msgFormat: 'string', outFormat: 'hex' };
        const opt = Object.assign(defaults, options);

        // note use throughout this routine of 'n >>> 0' to coerce Number 'n' to unsigned 32-bit integer

        switch (opt.msgFormat) {
            default: // default is to convert string to UTF-8, as SHA only deals with byte-streams
            case 'string':   msg = utf8Encode(msg);       break;
            case 'hex-bytes':msg = hexBytesToString(msg); break; // mostly for running tests
        }

        // constants [§4.2.2]
        const K = [
            0x428a2f98, 0x71374491, 0xb5c0fbcf, 0xe9b5dba5, 0x3956c25b, 0x59f111f1, 0x923f82a4, 0xab1c5ed5,
            0xd807aa98, 0x12835b01, 0x243185be, 0x550c7dc3, 0x72be5d74, 0x80deb1fe, 0x9bdc06a7, 0xc19bf174,
            0xe49b69c1, 0xefbe4786, 0x0fc19dc6, 0x240ca1cc, 0x2de92c6f, 0x4a7484aa, 0x5cb0a9dc, 0x76f988da,
            0x983e5152, 0xa831c66d, 0xb00327c8, 0xbf597fc7, 0xc6e00bf3, 0xd5a79147, 0x06ca6351, 0x14292967,
            0x27b70a85, 0x2e1b2138, 0x4d2c6dfc, 0x53380d13, 0x650a7354, 0x766a0abb, 0x81c2c92e, 0x92722c85,
            0xa2bfe8a1, 0xa81a664b, 0xc24b8b70, 0xc76c51a3, 0xd192e819, 0xd6990624, 0xf40e3585, 0x106aa070,
            0x19a4c116, 0x1e376c08, 0x2748774c, 0x34b0bcb5, 0x391c0cb3, 0x4ed8aa4a, 0x5b9cca4f, 0x682e6ff3,
            0x748f82ee, 0x78a5636f, 0x84c87814, 0x8cc70208, 0x90befffa, 0xa4506ceb, 0xbef9a3f7, 0xc67178f2 ];

        // initial hash value [§5.3.3]
        const H = [
            0x6a09e667, 0xbb67ae85, 0x3c6ef372, 0xa54ff53a, 0x510e527f, 0x9b05688c, 0x1f83d9ab, 0x5be0cd19 ];

        // PREPROCESSING [§6.2.1]

        msg += String.fromCharCode(0x80);  // add trailing '1' bit (+ 0's padding) to string [§5.1.1]

        // convert string msg into 512-bit blocks (array of 16 32-bit integers) [§5.2.1]
        const l = msg.length/4 + 2; // length (in 32-bit integers) of msg + ‘1’ + appended length
        const N = Math.ceil(l/16);  // number of 16-integer (512-bit) blocks required to hold 'l' ints
        const M = new Array(N);     // message M is N×16 array of 32-bit integers

        for (let i=0; i<N; i++) {
            M[i] = new Array(16);
            for (let j=0; j<16; j++) { // encode 4 chars per integer (64 per block), big-endian encoding
                M[i][j] = (msg.charCodeAt(i*64+j*4+0)<<24) | (msg.charCodeAt(i*64+j*4+1)<<16)
                        | (msg.charCodeAt(i*64+j*4+2)<< 8) | (msg.charCodeAt(i*64+j*4+3)<< 0);
            } // note running off the end of msg is ok 'cos bitwise ops on NaN return 0
        }
        // add length (in bits) into final pair of 32-bit integers (big-endian) [§5.1.1]
        // note: most significant word would be (len-1)*8 >>> 32, but since JS converts
        // bitwise-op args to 32 bits, we need to simulate this by arithmetic operators
        const lenHi = ((msg.length-1)*8) / Math.pow(2, 32);
        const lenLo = ((msg.length-1)*8) >>> 0;
        M[N-1][14] = Math.floor(lenHi);
        M[N-1][15] = lenLo;


        // HASH COMPUTATION [§6.2.2]

        for (let i=0; i<N; i++) {
            const W = new Array(64);

            // 1 - prepare message schedule 'W'
            for (let t=0;  t<16; t++) W[t] = M[i][t];
            for (let t=16; t<64; t++) {
                W[t] = (Sha256.s1(W[t-2]) + W[t-7] + Sha256.s0(W[t-15]) + W[t-16]) >>> 0;
            }

            // 2 - initialise working variables a, b, c, d, e, f, g, h with previous hash value
            let a = H[0], b = H[1], c = H[2], d = H[3], e = H[4], f = H[5], g = H[6], h = H[7];

            // 3 - main loop (note '>>> 0' for 'addition modulo 2^32')
            for (let t=0; t<64; t++) {
                const T1 = h + Sha256.S1(e) + Sha256.Ch(e, f, g) + K[t] + W[t];
                const T2 =     Sha256.S0(a) + Sha256.Maj(a, b, c);
                h = g;
                g = f;
                f = e;
                e = (d + T1) >>> 0;
                d = c;
                c = b;
                b = a;
                a = (T1 + T2) >>> 0;
            }

            // 4 - compute the new intermediate hash value (note '>>> 0' for 'addition modulo 2^32')
            H[0] = (H[0]+a) >>> 0;
            H[1] = (H[1]+b) >>> 0;
            H[2] = (H[2]+c) >>> 0;
            H[3] = (H[3]+d) >>> 0;
            H[4] = (H[4]+e) >>> 0;
            H[5] = (H[5]+f) >>> 0;
            H[6] = (H[6]+g) >>> 0;
            H[7] = (H[7]+h) >>> 0;
        }

        // convert H0..H7 to hex strings (with leading zeros)
        for (let h=0; h<H.length; h++) H[h] = ('00000000'+H[h].toString(16)).slice(-8);

        // concatenate H0..H7, with separator if required
        const separator = opt.outFormat=='hex-w' ? ' ' : '';

        return H.join(separator);

        /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  */

        function utf8Encode(str) {
            try {
                return new TextEncoder().encode(str, 'utf-8').reduce((prev, curr) => prev + String.fromCharCode(curr), '');
            } catch (e) { // no TextEncoder available?
                return unescape(encodeURIComponent(str)); // monsur.hossa.in/2012/07/20/utf-8-in-javascript.html
            }
        }

        function hexBytesToString(hexStr) { // convert string of hex numbers to a string of chars (eg '616263' -> 'abc').
            const str = hexStr.replace(' ', ''); // allow space-separated groups
            return str=='' ? '' : str.match(/.{2}/g).map(byte => String.fromCharCode(parseInt(byte, 16))).join('');
        }
    }



    /**
     * Rotates right (circular right shift) value x by n positions [§3.2.4].
     * @private
     */
    static ROTR(n, x) {
        return (x >>> n) | (x << (32-n));
    }


    /**
     * Logical functions [§4.1.2].
     * @private
     */
    static S0(x) { return Sha256.ROTR(2,  x) ^ Sha256.ROTR(13, x) ^ Sha256.ROTR(22, x); }
    static S1(x) { return Sha256.ROTR(6,  x) ^ Sha256.ROTR(11, x) ^ Sha256.ROTR(25, x); }
    static s0(x) { return Sha256.ROTR(7,  x) ^ Sha256.ROTR(18, x) ^ (x>>>3);  }
    static s1(x) { return Sha256.ROTR(17, x) ^ Sha256.ROTR(19, x) ^ (x>>>10); }
    static Ch(x, y, z)  { return (x & y) ^ (~x & z); }          // 'choice'
    static Maj(x, y, z) { return (x & y) ^ (x & z) ^ (y & z); } // 'majority'

}


/* - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -  */

if (typeof module != 'undefined' && module.exports) module.exports = Sha256; // = export default Sha256
</script>
	<style>
.body-main {
	background: #ffffff;
	padding: 20px 30px !important;
	position: relative;
	box-shadow: 0 1px 21px #808080;
	font-size: 10px;
	margin-top: 6%;
}

@media ( max-width :991px) {
	.text-align {
		text-align: center !important;
	}
}
</style>
</head>

<body onload="formLoad();">
	<div id="actionMessageDiv" style="display: none;">
		<s:if test="hasActionMessages()">
			<s:actionmessage />
		</s:if>
	</div>
	<form method="post">
		<div class="container">
			<div class="row">
				<div class="col-lg-12  body-main">

					<div class="row row-centre">
						<div class="col-lg-4 col-md-4 col-sm-12 col text-align">
							<img src="../image/new-payment-page-logo.png"
								alt="BestPay Logo" title="BestPay Logo" class="image1" style="width: 212px !important;">
								<p>api.bpgate.net</p>
						</div>
						<!-- <div class="col-lg-4">
								<img class="img" alt="Invoice Template" src="../image/98x98.png" />
							</div> -->
						<div class="col-lg-8 col-md-8 col-sm-12 coln text-align">
							<h2 class="payment-title">Merchant Payment Page</h2>
						</div>
					</div>
					<div class="row">
						<div class="col-md-4 col-sm-12 form-group">
							<label for="">Name<span class="star-color">*</span></label>
							<s:textfield id="CUST_NAME" name="CUST_NAME"
								cssClass="signuptextfield" autocomplete="off" />
							<span id="errorMsgName" class="error"></span>
						</div>
						<div class="col-md-4 col-sm-12 form-group">
							<label for="">Phone<span class="star-color">*</span></label>
							<s:textfield id="CUST_PHONE" name="CUST_PHONE"
								cssClass="signuptextfield" autocomplete="off" maxlength="15" />
							<span id="errorMsgMobile" class="error"></span>
						</div>
						<div class="col-md-4 col-sm-12 form-group">
							<label for="">Email<span class="star-color">*</span></label>
							<s:textfield id="CUST_EMAIL" name="CUST_EMAIL"
								cssClass="signuptextfield" autocomplete="off" />
							<span id="errorMsgEmialid" class="error"></span>
						</div>
					</div>
					<div class="row">

						<div class="col-md-4 col-sm-12 form-group">
							<label for="">Address<span class="star-color"></span></label>
							<s:textfield id="CUST_STREET_ADDRESS1"
								name="CUST_STREET_ADDRESS1" cssClass="signuptextfield"
								autocomplete="off" />
						</div>
						<div class="col-md-4 col-sm-12 form-group">
							<label for="">Customer Id<span class="star-color"></span></label>
							<s:textfield id="CUST_ID" name="CUST_ID"
								cssClass="signuptextfield" autocomplete="off" />
						</div>
						<div class="col-md-4 col-sm-12 form-group">
							<label for="">Total Amount<span class="star-color">*</span></label>
							<s:textfield id="AMOUNT" name="AMOUNT" cssClass="signuptextfield"
								autocomplete="off" />
							<span id="errorMsgAmount" class="error"></span>
						</div>
					</div>
					<div class="row">

						<div class="col-md-4 col-sm-12 form-group">
							<label for="">Currency Code<span class="star-color"></span></label>
							<%-- 										<s:textfield id="CURRENCY_CODE" name="CURRENCY_CODE" --%>
							<%-- 											cssClass="signuptextfield" value="%{mpl.currencyCode}" --%>
							<%-- 											autocomplete="off"/> --%>
							<select id="CURRENCY_CODE" name="CURRENCY_CODE"
								class="signuptextfield">
							</select>
						</div>
						<div class="col-md-4 col-sm-12 form-group">
							<label for="">Order Id<span class="star-color"></span></label>
							<s:textfield id="ORDER_ID" name="ORDER_ID"
								cssClass="signuptextfield" autocomplete="off"
								value="%{mpl.orderId}" readonly="true" />
						</div>
						<div class="col-md-4 col-sm-12 form-group">
							<label for="">Transaction Type<span class="star-color"></span></label>
							<s:textfield id="TXNTYPE" name="TXNTYPE"
								cssClass="signuptextfield" autocomplete="off" value="SALE"
								readonly="true" />
						</div>
					</div>
					<div class="row m-0">
						<div class="col-md-4 col-sm-12 form-group">
							<label for="">Return Url<span class="star-color"></span></label>
							<s:textfield id="RETURN_URL" name="RETURN_URL"
								cssClass="signuptextfield" autocomplete="off"
								value="%{mpl.returnUrl}" readonly="true" />
						</div>
						<div class="col-md-4 col-sm-12 form-group">
							<label for="">Pay Id<span class="star-color"></span></label>
							<s:textfield id="PAY_ID" name="PAY_ID" cssClass="signuptextfield"
								autocomplete="off" value="%{mpl.payId}" readonly="true" />
						</div>
						<!-- <div class="txtnew">
										<s:submit id="INVOICE_PAY_BTN"
											class="btn btn-success btn-md btn-block" value="Pay"
											onclick="submitForm();" style="width:20%">
										</s:submit>
									</div> -->
						<div class="col-md-4 col-sm-12 form-group">
							<label class="d-flex align-items-center fs-6 fw-bold mb-2">
								<span class="required">Payment Region</span>
							</label>
							<s:select
								list="@com.pay10.commons.user.AccountCurrencyRegions@values()"
								id="PAYMENTS_REGION" cssClass="signuptextfield"
								name="PAYMENTS_REGION" value="PAYMENTS_REGION"
								listValue="name()" listKey="name()" />
						</div>
					</div>
					<div class="row">
						<div class="col-md-12 col-sm-12 form-group">
							<div class="txtnew">
								<input type="button" id="INVOICE_PAY_BTN"
									class="btn btn-success btn-md" value="Pay"
									onclick="submitForm()">
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<s:hidden id="HASH" name="HASH" value="" />

		</div>
	</form>
	<s:hidden id="hashkey" name="salt" value="%{mpl.saltKey}" />
	<s:hidden id="paymentActionUrl" name="paymentActionUrl"
		value="%{mpl.paymentActionUrl}" />
	<s:hidden name="token" value="%{#session.customToken}"></s:hidden>
	<s:hidden id="abhi" value="%{mpl.currencies}"></s:hidden>

</body>
</html>
