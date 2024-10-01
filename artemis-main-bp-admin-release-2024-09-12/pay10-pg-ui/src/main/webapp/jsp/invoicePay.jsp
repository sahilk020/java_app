<!DOCTYPE html
  PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@taglib prefix="s" uri="/struts-tags"%>
<%@ page import="org.owasp.esapi.ESAPI"%>
<%@page import="com.pay10.commons.util.PropertiesManager"%>
<html>

<head>
  <title>BPGATE</title>
  <meta http-equiv="X-UA-Compatible" content="IE=edge" />
  <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no" />
  <link rel="stylesheet" type="text/css"
    href="https://fonts.googleapis.com/css?family=Roboto:300,400,500,700|Roboto+Slab:400,700|Material+Icons" />
  <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/latest/css/font-awesome.min.css">
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
      var tncStatus1='<s:property value="invoice.tncStatus"/>';
      if(tncStatus1=="true")
		{
		$('#abc').show();

		//document.getElementById('INVOICE_PAY_BTN').style.display = "none";

		}if(tncStatus1=="false"){

			$('#abc').hide();
			//$('#INVOICE_PAY_BTN').show();
			}
     
    };

  </script>
  <script>
$(document).ready(function(){
	var invoiceNo='<s:property value="invoice.invoiceNo"/>';
	var mytext=$("#payText").text();
	mytext = mytext.replace("#invoiceNo", invoiceNo);
    $("#payText").text(mytext);
});
</script>
<script>
function submitForm(){
	debugger
	var tncStatus1='<s:property value="invoice.tncStatus"/>';
	var tnc=document.getElementById('tnc').checked;

	if (tncStatus1=="true" && tnc==false) {
		$('#errorTnc').text('Please select checkbox.');
		$("#errorTnc").show();
		setTimeout(function() {
			$("#errorTnc").hide();
		}, 5000);
		return false;
	}
	else {
		document.getElementById("singlePage").submit();
	}

}
</script>
<style>

  label{
    color: #212121 !important;
    font-size: 16px;
  }
  #INVOICE_PAY_BTN{
    width: 250px;
    float: unset;
  }
  div#wwctrl_INVOICE_PAY_BTN{ 
    text-align: center !important;
  }
  .d-none{
    display: none !important;
  }
  .body-main {
    background: #ffffff;
     margin-top: unset;
     margin-bottom: unset;
    padding: 40px 30px !important;
    position: relative;
    box-shadow: 0 1px 21px #808080;
    font-size: 10px;
}
.invoice-no{
    margin-top:30px;
    color: black !important;
  }
@media(max-width:991px){
  .invoice-no {
    margin-right: 52px;
     margin-top: unset;
    color: black !important;
    font-size: 14px !important;
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
  
  <form method="post" action="payInvoice" id="singlePage">

    

    <div class="container">
			<div class="row">
				<div class="col-lg-12  body-main">
				
          <div class="row row-centre">
            <div class="col-lg-4 col-md-3 col-sm-12 col ">
              <img  src="../image/new-payment-page-logo.png" alt="pay10 Logo" title="pay10 Logo" class="image1">
              <p>secure.pay10.com</p>
            </div>
            <!-- <div class="col-lg-4">
              <img class="img" alt="Invoice Template" src="../image/98x98.png" />
            </div> -->
            <div class="col-lg-6 col-md-7 col-sm-12">
              <h2  class="payment-title">Review Information and Pay</h2>
            </div>
            <div class="col-lg-2 col-md-2 col-sm-12 col-xs-12">
              <label for="" class="invoice-no">Invoice No&nbsp;: <s:property value="%{invoice.invoiceNo}"  /></label>
            </div>
          </div>
							<div class="row">
								<!-- <div class="col-lg-4 col-md-4 col-sm-12 col-xs-4 form-group">
									<label for="">Invoice no</label><br>
									<span class="data-value"> <s:property value="%{invoice.invoiceNo}" /></span>
								</div> -->
								<div class="col-lg-4 col-md-4 col-sm-4 col-xs-6 form-group">
									<label for="">Name</label><br>
                  <span class="data-value"> <s:property value="%{invoice.name}" /></span>
								</div>
								<div class="col-lg-4 col-md-4 col-sm-4 col-xs-6 form-group">
									<label for="">Phone</label><br>
                  <span class="data-value"> <s:property value="%{invoice.phone}" /></span>
								</div>
                <div class="col-lg-4 col-md-4 col-sm-4 col-xs-12 form-group">
									<label for="">Email</label><br>
									<span class="data-value">   <s:property value="%{invoice.email}" /></span>
								</div>
							</div>
							<div class="row">
								<!-- <div class="col-lg-4 col-md-4 col-sm-12 col-xs-4 form-group">
									<label for="">Email</label><br>
									<span class="data-value">   <s:property value="%{invoice.email}" /></span>
								</div> -->
								<div class="col-lg-4 col-md-4 col-sm-4 col-xs-4 form-group">
									<label for="">City</label><br>
                  <span class="data-value">  
                    <s:if	test="%{invoice.city !=null && invoice.city != ''}">
                    <s:property value="%{invoice.city}" /> </s:if>
                    <s:else>Not applicable</s:else>
                  </span>
								</div>
								<div class="col-lg-4 col-md-4 col-sm-4 col-xs-4 form-group">
									<label for="">State</label><br>
                  <span class="data-value">
                    <s:if
                    test="%{invoice.state !=null && invoice.state != ''}">
                    <s:property value="%{invoice.state}" />
                  </s:if>
                  <s:else>Not applicable</s:else>
								</div>
                <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4 form-group">
									<label for="">Country</label><br>
									<span class="data-value">
                    <s:if
                          test="%{invoice.country !=null && invoice.country != ''}">
                          <s:property value="%{invoice.country}" />
                        </s:if>
                        <s:else>Not applicable</s:else>
                    </span>
								</div>
							</div>
              <div class="row">
								<!-- <div class="col-lg-4 col-md-4 col-sm-4 col-xs-4 form-group">
									<label for="">Country</label><br>
									<span class="data-value">
                    <s:if
                          test="%{invoice.country !=null && invoice.country != ''}">
                          <s:property value="%{invoice.country}" />
                        </s:if>
                        <s:else>Not applicable</s:else>
                    </span>
								</div> -->
								<div class="col-lg-4 col-md-4 col-sm-4 col-xs-4 form-group">
									<label for="">Zip</label><br>
                  <span class="data-value"> 
                    <s:if
                    test="%{invoice.zip !=null && invoice.zip != ''}">
                    <s:property value="%{invoice.zip}" />
                  </s:if>
                  <s:else>Not applicable</s:else>
                  </span>
								</div>
								<div class="col-lg-4 col-md-4 col-sm-4 col-xs-4 form-group">
									<!-- <label for="">Phone</label><br>
                  <span class="data-value"> <s:property value="%{invoice.phone}" /></span> -->
                  <label for="">Address</label><br>
									<span class="data-value">
                    <s:if
                          test="%{invoice.address !=null && invoice.address != ''}">
                        <s:property value="%{invoice.address}" />
                      </s:if>
                      <s:else>Not applicable</s:else>
                    </span>
								</div>
							</div>
							<!-- <div class="row">
								<div class="col-lg-12 col-md-12 col-sm-4 col-xs-12 form-group">
									<label for="">Address</label><br>
									<span class="data-value">
                    <s:if
                          test="%{invoice.address !=null && invoice.address != ''}">
                        <s:property value="%{invoice.address}" />
                      </s:if>
                      <s:else>Not applicable</s:else>
                    </span>
								</div>
						
							</div> -->
              <div class="row">
								<div class="col-lg-4 col-md-4 col-sm-4 col-xs-4 form-group">
									<label for="">Name</label><br>
									<span class="data-value"> 
                    <s:property value="%{invoice.productName}" />
                                    </span>
								</div>
								<div class="col-lg-4 col-md-4 col-sm-4 col-xs-4 form-group">
									<label for="">Description</label><br>
                  <span class="data-value"> 
                    <s:if
                    test="%{invoice.productDesc !=null && invoice.productDesc != ''}">
                    <s:property value="%{invoice.productDesc}" />
                  </s:if>
                  <s:else>Not applicable</s:else>
                  </span>
								</div>
								<div class="col-lg-4 col-md-4 col-sm-4 col-xs-4 form-group">
									<label for="">Gst%</label><br>
                  <span class="data-value"> 
                    <s:property value="%{invoice.gst+'%'}" />
                                    </span>

								</div>
							</div>
							<div class="row">
								<div class="col-lg-4 col-md-4 col-sm-4 col-xs-4 form-group">
									<label for="">All prices are in</label><br>
									<span class="data-value"> 
                    <s:property value="%{currencyName}" />
                  </span>
								</div>
								<div class="col-lg-4 col-md-4 col-sm-4 col-xs-4 form-group">
                                    <label for="">Payment Region</label><br>
                                    <span class="data-value">
                                        <s:property value="%{invoice.region}" />
                                     </span>
                                </div>

								<div class="col-lg-4 col-md-4 col-sm-4 col-xs-4 form-group">
									<label for="">Quantity</label><br>
                  <span class="data-value"> 
                    <s:property value="%{invoice.quantity}" />
                  </span>
								</div>
								<div class="col-lg-4 col-md-4 col-sm-4 col-xs-4 form-group">
									<label for="">GstAmount</label><br>
                  <span class="data-value"> 
                    <s:property value="getFormatted('{0,number,##0.00}','(invoice.amount)*(invoice.gst/100)')" />
                  </span>

								</div>
							</div>
              <div class="row">
								<div class="col-lg-4 col-md-4 col-sm-4 col-xs-4 form-group">
									<label for="">Expires on</label><br>
									<span class="data-value"> 
                    <s:property value="%{invoice.expiryTime}" />                  </span>
								</div>
								<div class="col-lg-4 col-md-4 col-sm-4 col-xs-4 form-group">
									<label for=""></label><br>
                  <span class="data-value"> 
                    <!-- <s:property value="%{invoice.quantity}" /> -->
                  </span>
								</div>
								<div class="col-lg-4 col-md-4 col-sm-4 col-xs-4 form-group">
									<label for="">Amount</label><br>
                  <span class="data-value"> 
                    <s:property value="%{invoice.amount}" />
                  </span>

								</div>
							</div>
              <div class="row">
								<div class="col-lg-4 col-md-4 col-sm-4 col-xs-4 form-group">
								
								</div>
								<div class="col-lg-4 col-md-4 col-sm-4 col-xs-4 form-group">
								
								</div>
								<div class="col-lg-4 col-md-4 col-sm-4 col-xs-4 form-group">
									<label for="" style="color:rgb(255, 0, 0) !important;">Total Amount</label><br>
                  <span class="data-value"> 
                    <s:property value="%{invoice.totalAmount}" />
                  </span>

								</div>
							</div>
								<div class="row" id="abc" style="display: inline-flex;">
							<s:checkbox id="tnc"
																	name="customerConsent" value="%{invoice.customerConsent}"
																	style="  margin-top:0px;height:20px"></s:checkbox> &nbsp; &nbsp;
															<label for="">I agree to <a href="#" target=_self
																onClick="pop()">Terms & Conditions</a></label> <span
																class="data-value">
															</span>
															<span id="errorTnc"
								class="error" style="margin-top:20px;"></span>

														</div>
              <div class="row">
								<div class="col-md-12 col-sm-12 form-group" style="text-align: center;margin-top:20px;">
								
                    <input type="button" id="
" class="btn btn-success btn-md text-centre" value="Pay"
                     onclick="submitForm();" >
                     <!-- style="margin-left: 400px;" -->

									
								
								</div>
							</div>
				</div>
			</div>
										<s:hidden id="HASH" name="HASH" value="" />

		</div>



    <div class="row d-none">
      <div class="col-sm-10">
      <s:hidden id="PAY_ID" name="PAY_ID" value="%{invoice.payId}" />
      <s:hidden id="ORDER_ID" name="ORDER_ID" value="%{invoice.invoiceNo}" />
      <s:hidden id="AMOUNT" name="AMOUNT" value="%{totalamount}" />
      <s:hidden id="TXNTYPE" name="TXNTYPE" value="SALE" />


      <s:hidden id="CUST_NAME" name="CUST_NAME" value="%{invoice.name}" />
      <s:hidden id="CUST_STREET_ADDRESS1" name="CUST_STREET_ADDRESS1" value="%{invoice.address}" />
      <s:hidden id="CUST_ZIP" name="CUST_ZIP" value="%{invoice.zip}" />
      <s:hidden id="state" name="state" value="%{invoice.state}" />
      <s:hidden id="country" name="country" value="%{invoice.country}" />
      <s:hidden id="city" name="city" value="%{invoice.city}" />

      <s:hidden id="CUST_PHONE" name="CUST_PHONE" value="%{invoice.phone}" />
      <s:hidden id="CUST_EMAIL" name="CUST_EMAIL" value="%{invoice.email}" />

      <s:hidden id="PRODUCT_DESC" name="PRODUCT_DESC" value="%{invoice.productDesc}" />
      <s:hidden id="CURRENCY_CODE" name="CURRENCY_CODE" value="%{invoice.currencyCode}" />
      <s:hidden id="RETURN_URL" name="RETURN_URL" value="%{invoice.returnUrl}" />
      <s:hidden id="INVOICE_ID" name="INVOICE_ID" value="%{invoice.invoiceId}" />

    </div>
  </div>

	<s:hidden name="token" value="%{#session.customToken}"></s:hidden>
  </form>
  <script type="text/javascript">
/* $(document).ready(function(){
	$("#INVOICE_PAY_BTN").hide();

}); */

/* function Accept(){
    var tnc= document.getElementById('tnc').checked;
    if(tnc==true){
    	$("#INVOICE_PAY_BTN").show();
    }
    else{
    	$("#INVOICE_PAY_BTN").hide();
        }
}
 */
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
        <h5 class="modal-title">Terms & Conditions for Payment</h5>
        <button type="button" class="close" data-dismiss="modal" onclick="cancelbutton()" aria-label="Close">
          <span aria-hidden="true">&times;</span>
        </button>
      </div>
      <div class="modal-body">
        <p id="payText"> <s:property value="%{invoice.payText}" /></p>
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