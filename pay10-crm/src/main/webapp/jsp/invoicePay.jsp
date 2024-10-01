<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@taglib prefix="s" uri="/struts-tags"%>
<%@ page import="org.owasp.esapi.ESAPI"%>
<%@page import="com.pay10.commons.util.PropertiesManager"%>
<html>
<head>
<title>Pay Invoice</title>
<meta http-equiv="X-UA-Compatible" content="IE=edge" />
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" />
<link href="../css/default.css" rel="stylesheet" type="text/css" />
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
		var enablePayNow= '<s:property value="%{enablePay}" />';
		if(enablePayNow== "TRUE") {
			document.getElementById('INVOICE_PAY_BTN').disabled = false;
			document.getElementById('lblMsg').style.display= "none";
		}	
		else {
			document.getElementById('INVOICE_PAY_BTN').disabled = true;
			document.getElementById('lblMsg').style.display= "block";			
		}
	};
	
	 function submitForm() {
     
		 var payURL = document.getElementById('payUrl').innerHTML;
		 var salt = '<s:property value="%{salt}" />';
		 var CUST_PHONE = document.getElementById('CUST_PHONE').value;
		 var CUST_EMAIL = document.getElementById('CUST_EMAIL').value;
		 var PAY_ID = document.getElementById('PAY_ID').value;
		 var ORDER_ID = document.getElementById('ORDER_ID').value;
		 var AMOUNT = document.getElementById('AMOUNT').value;
		 var TXNTYPE = document.getElementById('TXNTYPE').value;
		 var CUST_NAME = document.getElementById('CUST_NAME').value;
		 var CUST_STREET_ADDRESS1 = document.getElementById('CUST_STREET_ADDRESS1').value;
		 var CUST_ZIP = document.getElementById('CUST_ZIP').value;
		 var PRODUCT_DESC = document.getElementById('PRODUCT_DESC').value;
		 var CURRENCY_CODE = document.getElementById('CURRENCY_CODE').value;
		 var RETURN_URL = document.getElementById('RETURN_URL').value;
		 
		
		
		 
		
		 
		 var ReqArray = {CUST_PHONE:CUST_PHONE, CUST_EMAIL:CUST_EMAIL,PAY_ID:PAY_ID, ORDER_ID:ORDER_ID, AMOUNT:AMOUNT, TXNTYPE:TXNTYPE, CUST_NAME:CUST_NAME,
		 CUST_STREET_ADDRESS1:CUST_STREET_ADDRESS1, CUST_ZIP:CUST_ZIP, PRODUCT_DESC:PRODUCT_DESC, CURRENCY_CODE:CURRENCY_CODE, RETURN_URL:RETURN_URL};
		 
		 var abc = Object.entries(ReqArray).sort();
		 var i;
		 var inputString = "";
		 
			for (i = 0; i < Object.entries(abc).length; i = i + 1){
			var element = Object.values(abc)[i][0];
				inputString += "~";
				inputString += element;
				inputString += "="
				inputString += Object.values(abc)[i][1];
			}
			inputString = inputString.substr(1);
			inputString += salt;
			var hash = Sha256.hash(inputString).toUpperCase();
			document.getElementById("HASH").value = hash;
			
			if(CUST_PHONE != "" && CUST_EMAIL != ""){
				var form = document.forms[0];
				form.action = payURL;
		        form.submit();
				}else{
					if(CUST_PHONE == ""){
						alert("please enter Phone Number");
					}else{
						alert("please enter email Number");
					}
					
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

        // constants [�4.2.2]
        const K = [
            0x428a2f98, 0x71374491, 0xb5c0fbcf, 0xe9b5dba5, 0x3956c25b, 0x59f111f1, 0x923f82a4, 0xab1c5ed5,
            0xd807aa98, 0x12835b01, 0x243185be, 0x550c7dc3, 0x72be5d74, 0x80deb1fe, 0x9bdc06a7, 0xc19bf174,
            0xe49b69c1, 0xefbe4786, 0x0fc19dc6, 0x240ca1cc, 0x2de92c6f, 0x4a7484aa, 0x5cb0a9dc, 0x76f988da,
            0x983e5152, 0xa831c66d, 0xb00327c8, 0xbf597fc7, 0xc6e00bf3, 0xd5a79147, 0x06ca6351, 0x14292967,
            0x27b70a85, 0x2e1b2138, 0x4d2c6dfc, 0x53380d13, 0x650a7354, 0x766a0abb, 0x81c2c92e, 0x92722c85,
            0xa2bfe8a1, 0xa81a664b, 0xc24b8b70, 0xc76c51a3, 0xd192e819, 0xd6990624, 0xf40e3585, 0x106aa070,
            0x19a4c116, 0x1e376c08, 0x2748774c, 0x34b0bcb5, 0x391c0cb3, 0x4ed8aa4a, 0x5b9cca4f, 0x682e6ff3,
            0x748f82ee, 0x78a5636f, 0x84c87814, 0x8cc70208, 0x90befffa, 0xa4506ceb, 0xbef9a3f7, 0xc67178f2 ];

        // initial hash value [�5.3.3]
        const H = [
            0x6a09e667, 0xbb67ae85, 0x3c6ef372, 0xa54ff53a, 0x510e527f, 0x9b05688c, 0x1f83d9ab, 0x5be0cd19 ];

        // PREPROCESSING [�6.2.1]

        msg += String.fromCharCode(0x80);  // add trailing '1' bit (+ 0's padding) to string [�5.1.1]

        // convert string msg into 512-bit blocks (array of 16 32-bit integers) [�5.2.1]
        const l = msg.length/4 + 2; // length (in 32-bit integers) of msg + '1' + appended length
        const N = Math.ceil(l/16);  // number of 16-integer (512-bit) blocks required to hold 'l' ints
        const M = new Array(N);     // message M is N�16 array of 32-bit integers

        for (let i=0; i<N; i++) {
            M[i] = new Array(16);
            for (let j=0; j<16; j++) { // encode 4 chars per integer (64 per block), big-endian encoding
                M[i][j] = (msg.charCodeAt(i*64+j*4+0)<<24) | (msg.charCodeAt(i*64+j*4+1)<<16)
                        | (msg.charCodeAt(i*64+j*4+2)<< 8) | (msg.charCodeAt(i*64+j*4+3)<< 0);
            } // note running off the end of msg is ok 'cos bitwise ops on NaN return 0
        }
        // add length (in bits) into final pair of 32-bit integers (big-endian) [�5.1.1]
        // note: most significant word would be (len-1)*8 >>> 32, but since JS converts
        // bitwise-op args to 32 bits, we need to simulate this by arithmetic operators
        const lenHi = ((msg.length-1)*8) / Math.pow(2, 32);
        const lenLo = ((msg.length-1)*8) >>> 0;
        M[N-1][14] = Math.floor(lenHi);
        M[N-1][15] = lenLo;


        // HASH COMPUTATION [�6.2.2]

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
     * Rotates right (circular right shift) value x by n positions [�3.2.4].
     * @private
     */
    static ROTR(n, x) {
        return (x >>> n) | (x << (32-n));
    }


    /**
     * Logical functions [�4.1.2].
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
<script>
  
</script>

</head>
<body onload="formLoad();">

<s:if test="hasActionMessages()">
    <s:actionmessage/>
</s:if>

<form   method="post">
	<div id="payUrl" style="display:none"><%=new PropertiesManager().getSystemProperty("invoicePaymentLink")%></div>
	<br /><table width="70%" align="center" border="0" cellspacing="0" cellpadding="0">
<tr>
					  <td align="center" valign="middle" height="40"><img src="../image/IRCT IPAY.png" width="220" /></td>
  </tr>
  <tr>
			<td align="center" valign="top"><table width="100%" border="0" align="center" cellpadding="0" cellspacing="0" class="formbox">				
					<tr>
						<td colspan="5" align="left" valign="middle" class="boxheadingsmall" style="color:#fff"
							height="30">Review Information and Pay</td>
					</tr>
					<tr>
						<td height="30" colspan="5" align="left" valign="middle">&nbsp;&nbsp;&nbsp;&nbsp;<strong>Customer Billing Information</strong></td>
			  </tr>
					<tr>
						<td colspan="5" align="center" valign="top"><table width="97%" border="0" cellpadding="0" cellspacing="0" class="invoicetable">
							   
<tr>
									<th width="20%" height="25" align="left" valign="middle"><strong>Invoice no</strong></th>
									<th width="20%" align="left" valign="middle"><strong>Name</strong></th>
                  <th width="20%" align="left" valign="middle"><strong>City</strong></th>
                  <th height="25" align="left" valign="middle"><strong><span>State</span></strong></th>
								
						  </tr>
								<tr>
                  <s:set var="invoiceType" value="%{invoice.invoiceType}"/>
                  <s:if test="%{#invoiceType=='PROMOTIONAL PAYMENT'}">
                    <td align="left" valign="middle"><input id="invoiceNo" placeholder="Please Enter Invoice Number" name="invoiceNo" /></td>
                    <td align="left" valign="middle"><input id="CUST_NAME" placeholder="Please Enter Customer Name" name="CUST_NAME" required="true"/></td>
                    
                   </s:if>
                   <s:else>
                  <td height="25" align="left" valign="middle"><s:property value="%{invoice.invoiceNo}"  /></td>
                  <td align="left" valign="middle"><s:property value="%{invoice.name}" /></td>
                   </s:else>
                   <s:if test="%{#invoiceType=='PROMOTIONAL PAYMENT'}">
                    <td align="left" valign="middle"><input id="city" placeholder="Please Enter Invoice Number" name="city" /></td>
                    <td align="left" valign="middle"><input id="state" placeholder="Please Enter Customer Name" name="state" required="true"/></td>
                    
                   </s:if>
                   <s:else>
                  <td align="left" valign="middle"><s:property value="%{invoice.city}" /></td>
                  <td height="25" align="left" valign="middle"><s:property value="%{invoice.state}" /></td>
                </s:else>
								</tr>
								<tr>
                  <th width="20%" align="left" valign="middle"><strong>Country</strong></th>
								  <th align="left" valign="middle"><strong>Zip</strong></th>
								  <th align="left" valign="middle"><strong><span>Phone</span></strong></th>
								  <th align="left" valign="middle"><strong><span>Email</span></strong></th>
						  </tr>
								<tr>
                  <s:set var="invoiceType" value="%{invoice.invoiceType}"/>
                  <s:if test="%{#invoiceType=='PROMOTIONAL PAYMENT'}">
                    <td align="left" valign="middle"><input id="Country" placeholder="Please Enter Country" name="Country" /></td>
                    <td align="left" valign="middle"><input id="zip" placeholder="Please Enter Zip Code" name="zip" required="true"/></td>
                    
                   </s:if>
                   <s:else>
                  <td align="left" valign="middle"><s:property value="%{invoice.country}" /></td>
                  
                  <td align="left" valign="middle"><s:property value="%{invoice.zip}"/></td>
                </s:else> 
					<s:if test="%{#invoiceType=='PROMOTIONAL PAYMENT'}">
            <td align="left" valign="middle"><input id="CUST_PHONE" placeholder="Please Enter Phone" onclick="validPhone()" name="CUST_PHONE" minlength="8" maxlength="13" required="true"/></td>
            <td align="left" valign="middle"><input id="CUST_EMAIL" placeholder="Please Enter Email" name="CUST_EMAIL" required="true"/></td>
            <!-- PleaseEnterMobile*: <s:textfield id="CUST_PHONE" name="CUST_PHONE" maxlength="10" required="true"/>
					 PleaseEnterEmail*: <s:textfield id="CUST_EMAIL" name="CUST_EMAIL" required="true"/> -->
					 </s:if>
					<s:else> 
                  <td align="left" valign="middle"><s:property value="%{invoice.phone}" /></td>
                 
                  <td align="left" valign="middle"><s:property value="%{invoice.email}" /></td>
					</s:else>       
						  </tr>
								<tr>
								  <th height="25" colspan="4" align="left" valign="middle"><span><strong>Address</strong></span></th>
						  </tr>
								<tr>
                  <s:set var="invoiceType" value="%{invoice.invoiceType}"/>
                  <s:if test="%{#invoiceType=='PROMOTIONAL PAYMENT'}">
                    <td align="left" valign="middle"><input id="address" placeholder="Please Enter Country" name="address" /></td>
                  </s:if>
                  <s:else>
                  <td height="25" colspan="4" align="left" valign="middle"><s:property value="%{invoice.address}" /></td>
                </s:else>
						    </tr>
								</table>
			  </td></tr>
					<tr>
					  <td height="10" colspan="5" align="left" valign="middle"></td>
			  </tr>
					<tr>
						<td height="30" colspan="5" align="left" valign="middle">&nbsp;&nbsp;&nbsp;&nbsp; <strong>Product Information</strong></td>
			  </tr>
                    <tr>
						<td align="center" valign="top"><table width="97%" border="0" cellpadding="0" cellspacing="0" class="invoicetable">
  <tr>
         <th width="22%" height="25" align="left" valign="middle"> Name</th>
         <th colspan="2" align="left" valign="middle"> Description</th>
         <th width="12%" align="left" valign="middle"><span>Quantity</span></th>
         <th width="12%" align="left" valign="middle"><span>Gst%</span></th>
         <th width="16%" align="left" valign="middle"><span>GstAmount</span></th>
         <th width="16%" align="left" valign="middle"><span>Amount</span></th>
  </tr>

  <tr>
         <td height="25" align="left" valign="middle"><div class="txtnew">
           <s:property value="%{invoice.productName}" />
         </div></td>
         <td colspan="2" align="left" valign="middle"><div class="txtnew">
           <s:property value="%{invoice.productDesc}" />
         </div></td>
         <td align="left" valign="middle"><div class="txtnew">
           <s:property value="%{invoice.quantity}" />
         </div></td>
          <td align="left" valign="middle"><div class="txtnew">
           <s:property value="%{invoice.gst+'%'}" />
         </div></td>
         <td align="left" valign="middle"><div class="txtnew">
           <s:property value="getFormatted('{0,number,##0.00}','(invoice.amount)*(invoice.gst/100)')"/>
         </div></td>
         <td align="left" valign="middle"><div class="txtnew">
          <s:property value="%{invoice.amount}" />
        </div></td>
  </tr>
  <tr>
    <td height="25" align="left" valign="middle">&nbsp;</td>
    <td width="23%" align="left" valign="middle">&nbsp;</td>
    <td width="27%" align="left" valign="middle">&nbsp;</td>
    <td align="left" valign="middle">&nbsp;</td>
    <td align="left" valign="middle">&nbsp;</td>
    <td align="left" valign="middle">&nbsp;</td>
    <th width="25%" align="left" valign="middle">Service Charge</th>
  </tr>
  <tr>
    <td height="25" align="left" valign="middle">&nbsp;</td>
    <td align="left" valign="middle">&nbsp;</td>
    <td align="right" valign="middle">&nbsp;</td>
    <td align="right" valign="middle">&nbsp;</td>
    <td align="right" valign="middle">&nbsp;</td>
    <td align="right" valign="middle">&nbsp;</td>
    <td align="left" valign="middle"><div class="txtnew">
          <s:property value="%{invoice.serviceCharge}" />
    </div></td>
  </tr> 
  <tr>
    <th height="25" align="left" valign="middle">All prices are in</th>
    <th align="left" valign="middle">Expire in days</th>
    <th align="left" valign="middle">Expire in hours</th>
    <th align="right" valign="middle">&nbsp;</th>
    <th align="right" valign="middle">&nbsp;</th>
    <th align="right" valign="middle">&nbsp;</th>
    <th align="left" valign="middle">Total Amount</th>
  </tr>
  <tr>
    <td height="25" align="left" valign="middle"><div class="txtnew">
      <s:property value="%{currencyName}" />
    </div></td>
    <td align="left" valign="middle"><div class="txtnew">
      <s:property value="%{invoice.expiresDay}" />
    </div></td>
    <td align="left" valign="middle"><div class="txtnew">
      <s:property value="%{invoice.expiresHour}" />
    </div></td>
    <td align="right" valign="middle">&nbsp;</td>
    <td align="right" valign="middle">&nbsp;</td>
    <td align="right" valign="middle">&nbsp;</td>
    <td align="left" valign="middle"><div class="txtnew">
      <s:property value="%{invoice.totalAmount}" />
    </div></td>
  </tr>
      </table></td>
					</tr>
                    <tr>
						<td align="center" valign="top"><br /><table width="100%" border="0"
						cellpadding="0" cellspacing="0">
						<tr>
						<td width="15%" align="left" valign="middle"></td>	
							<td width="5%" align="right" valign="middle">
							<table width="100%" border="0" cellpadding="0" cellspacing="0">
							<tr><td align="center" valign="middle">
                <s:submit id="INVOICE_PAY_BTN"   class="btn btn-success btn-md btn-block"
									value="Pay" onclick="javascript:submitForm()" >
                </s:submit>
				
 
              </td></tr>
								<tr><td>&nbsp;</td></tr>
							<tr><td align="right" valign="middle"><s:label id="lblMsg" class="redsmalltext" value="Link has been expired"></s:label></td></tr>
							</table>
								</td>
							<td width="3%" align="left" valign="middle"></td>
							<td width="15%" align="left" valign="middle"></td>	
						</tr>
					</table><br /></td>
                    </tr>	
                    
                   <tr>
                     <s:hidden id="PAY_ID" name="PAY_ID" value="%{invoice.payId}"/>
					 <s:hidden id="ORDER_ID" name="ORDER_ID" value="%{invoice.invoiceNo}"/>
					 <s:hidden id="AMOUNT" name="AMOUNT" value="%{totalamount}"/>
					 <s:hidden id="TXNTYPE" name="TXNTYPE" value="SALE"/>
					 <s:hidden id="CUST_NAME" name="CUST_NAME" value="%{invoice.name}"/>
					 <s:hidden id="CUST_STREET_ADDRESS1" name="CUST_STREET_ADDRESS1" value="%{invoice.address}"/>
					 <s:hidden id="CUST_ZIP" name="CUST_ZIP" value="%{invoice.zip}"/>
					 <!-- <s:set var="invoiceType" value="%{invoice.invoiceType}"/> -->
					<!-- <s:if test="%{#invoiceType=='PROMOTIONAL PAYMENT'}"> -->
					<!-- PleaseEnterMobile*: <s:textfield id="CUST_PHONE" name="CUST_PHONE" maxlength="10" required="true"/>
					 PleaseEnterEmail*: <s:textfield id="CUST_EMAIL" name="CUST_EMAIL" required="true"/> -->
					<!-- </s:if>
					<s:else> -->
					 <s:hidden  id="CUST_PHONE" name="CUST_PHONE" value="%{invoice.phone}"/>
					 <s:hidden id="CUST_EMAIL" name="CUST_EMAIL" value="%{invoice.email}"/>
				   	 <!-- </s:else> -->
					  <s:hidden id="PRODUCT_DESC" name="PRODUCT_DESC" value="%{invoice.productDesc}"/>
					 <s:hidden id="CURRENCY_CODE" name="CURRENCY_CODE" value="%{invoice.currencyCode}"/>
					 <s:hidden id="RETURN_URL" name="RETURN_URL" value="%{invoice.returnUrl}"/>
					 <s:hidden id="HASH" name="HASH" />
					
                   </tr>				
	</table></td>
		</tr>
	</table><br />

</form>
<s:hidden name="token" value="%{#session.customToken}"></s:hidden>
</body>
</html>