<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Chargeback</title>

<link href="../css/Jquerydatatableview.css" rel="stylesheet" />
<link href="../css/Jquerydatatable.css" rel="stylesheet" />
<link href="../css/default.css" rel="stylesheet" type="text/css" />
<link href="../css/jquery-ui.css" rel="stylesheet" />

<script src="../js/jquery.min.js" type="text/javascript"></script>
<script src="../js/jquery.dataTables.js" type="text/javascript"></script>
<script src="../js/jquery-ui.js" type="text/javascript"></script>
<script src="../js/jquery.popupoverlay.js" type="text/javascript"></script>
<script src="../js/dataTables.buttons.js" type="text/javascript"></script>
<script src="../js/pdfmake.js" type="text/javascript"></script>
<link href="../css/loader.css" rel="stylesheet" type="text/css" />

<script>
$(document).ready(function() {

	$(function() {
		var dateToday = new Date(); 
		$("#targetDate").datepicker({
			prevText : "click for previous months",
			nextText : "click for next months",
			showOtherMonths : true,
			dateFormat : 'dd-mm-yy',
			selectOtherMonths : true,
			minDate: dateToday,
			
		});
	
	});

});


$(document).ready( function () {
	updateFormEnabled();
});

function updateFormEnabled(){

	if (verifyAdSettings()) {
        $('#chargebackSubmit').attr('disabled', false);
    } else {
        $('#chargebackSubmit').attr('disabled', true);
    }
  }
  
function verifyAdSettings() {
	  var chargeBackTyp = document.getElementById("chargebackType").value;
	 // var chargeBackStats = document.getElementById("chargebackStatus").value; 
	  var targtDate = document.getElementById("targetDate").value;
	  
    if (chargeBackTyp != '' && targtDate != '') {
        return true;
    } else {
        return false
    }
}

$(document).ready(function() {
	$("form#files").submit(function(){

	    var formData = new FormData($(this)[0]);

	    $.ajax({
	        url:'saveChargebackAction',
	        type: 'POST',
	        data: formData,
	        async: false,
	        success: function (data) {	        	
	        	alert("Successfully created." );
	        	location.reload();
	    		//document.getElementById("saveMessage").innerHTML="Comments added successfully.";
	        },
	        error: function (data) {
	        	alert("Chargeback already raised." );
	        	location.reload();
	        },
	        cache: false,
	        contentType: false,
	        processData: false
	    });

	    return false;
	});
	});



/* function submitForm(){
	$.ajax({
		url : 'saveChargebackAction',
		type : 'post',
		data : {
			txnId : '<s:property value ="%{txnId}"/>',
			orderId : '<s:property value="transDetails.orderId" />',
			payId :'<s:property	value="transDetails.payId" />', 
			chargebackType: document.getElementById("chargebackType").value,
			chargebackStatus: document.getElementById("chargebackStatus").value,
			targetDate: document.getElementById("targetDate").value,
			comments: document.getElementById("comments").value,
			//documentUpload: document.getElementById("documentUpload").value,
		},
		success : function(data) {
		
		
		alert("Comments added successfully.");
	//	location.reload();
		document.getElementById("saveMessage").innerHTML="Comments added successfully.";
			
		},
		error : function(data) {
			alert("Something went wrong, so please try again.");
		} 
	});
}
*/

</script>
<style type="text/css">
.textFL_merch{
	resize: none;
}
.typeCss{
	    margin-top: 26px;
    display: block;
}
.targetDateCss{
    margin-top: 26px;
    display: block;
}
.dateTargetInput{
	 margin-top: 26px;
}
</style>
</head>
<body>
<s:form id="files" method="post" enctype="multipart/form-data">
<table width="100%" border="0" align="center" cellpadding="0"
		cellspacing="0" class="formbox">
		
		<tr>
							<td align="left" valign="bottom" height="30"><div id="saveMessage">
									<s:actionmessage class="success success-text" />
								</div></td>
						</tr>
<tr>

			<td colspan="3" align="left" valign="top"><div class="adduT">
					<div class="readp">
						<table width="100%" border="0" cellspacing="0" cellpadding="0"
							class="product-spec">
							<tr>
								<th height="30" colspan="2" align="left">Order Details</th>
							</tr>
							<tr>
								<td width="33%" height="30" align="left">Order ID:</td>
								<td width="66%" align="left" class="txtnew"><s:property
										value="transDetails.orderId" />
										
										<s:hidden name="txnId" value="%{txnId}"  />
											<s:hidden name="orderId" value="%{transDetails.orderId}"/>
										<s:hidden name="payId" 	value="%{transDetails.payId}" />
										</td>
							</tr>
							<tr>
								<td height="30" align="left" bgcolor="#F2F2F2">Date:</td>
								<td align="left" bgcolor="#F2F2F2"><s:property
										value="transDetails.createDate" />
																			</td>
							</tr>
							<tr>
								<td height="30" align="left">Merchant Pay ID:</td>
								<td align="left" class="txtnew"><s:property
										value="transDetails.payId" />
									
										</td>
							</tr>
							<tr>
								<td height="30" align="left" bgcolor="#F2F2F2">Card Number
									Mask:</td>
								<td align="left" bgcolor="#F2F2F2"><s:if
										test="%{transDetails.cardNumber !=null}">
										
									</s:if>
									<s:else>Not applicable</s:else>
								
									</td>
							</tr>
							<tr>
								<td height="30" align="left">Payment Method:</td>
								<td align="left" class="txtnew"><s:property
										value="transDetails.paymentType" />&nbsp;(<s:property
										value="transDetails.mopType" />)
										
										</td>
							</tr>
							<tr>
								<td height="30" align="left">Card Issuer Info :</td>
								<td align="left" class="txtnew"><s:if
										test="%{transDetails.internalCardIssusserBank !=null}">
										
										<s:if
											test="%{transDetails.internalCardIssusserCountry !=null}">&nbsp;(<s:property
												value="transDetails.internalCardIssusserCountry" />)</s:if>
									</s:if>
									<s:else>Not applicable</s:else>
								
									</td>
							</tr>
							<tr>
								<td height="30" align="left" bgcolor="#F2F2F2">Customer
									Email:</td>
								<td align="left" bgcolor="#F2F2F2"><s:property
										value="transDetails.custEmail" />
									
										</td>
							</tr>
							<tr>
								<td height="30" align="left">IP Address:</td>
								<td align="left" class="txtnew"><s:property
										value="transDetails.internalCustIP" />
										
										</td>
							</tr>
							<tr>
								<td height="30" align="left" class='greytdbg'>Country</td>
								<td class='greytdbg' align="left"><s:property
										value="transDetails.internalCustCountryName" />
									
										</td>
							</tr>
						</table>
						<div class="clear"></div>
					</div>

					<div class="readp">
						<table width="100%" border="0" cellspacing="0" cellpadding="0"
							class="product-spec">
							<tr>
								<th colspan="2" height="30" align="left" bgcolor="#eef8ff">Amount
									Summary</th>
							</tr>
							<tr>
								<td width="49%" height="30" align="left">Currency:</td>
								<td width="50%" align="left" class="txtnew"><s:property
										value="transDetails.currencyNameCode" />
									
										</td>
							</tr>
							<tr>
								<td height="30" align="left" bgcolor="#F2F2F2">Authorized
									Amount [A]</td>
								<td align="left" bgcolor="#F2F2F2"><s:property
										value="transDetails.authorizedAmount" />
									
										</td>


							</tr>
							<tr>
								<td height="30" align="left">Captured Amount [B]</td>
								<td align="left" class="txtnew"><s:property
										value="transDetails.capturedAmount" />
										
										</td>
							<tr>
								<td height="30" align="left" bgcolor="#F2F2F2">Chargeback
									Amount [C]</td>
								<td align="left" bgcolor="#F2F2F2"><s:property
										value="transDetails.chargebackAmount" />
										
										</td>
							</tr>
							<tr>
								<td height="30" align="left">Fixed Txn Fee [D]</td>
								<td align="left" class="txtnew"><s:property
										value="transDetails.fixedTxnFee" />
										
										</td>
							</tr>
							<tr>
								<td height="30" align="left" bgcolor="#F2F2F2">TDR (<s:property
										value="transDetails.merchantTDR" />% of B + D) [F]
										
								</td>
								<td align="left" bgcolor="#F2F2F2"><s:property
										value="transDetails.tdr" />
										
										</td>
								
							</tr>
							<tr>
								<td height="30" align="left">Service Tax (<s:property
										value="transDetails.percentecServiceTax" />% of F) [G]
										
								</td>
								<td align="left" class="txtnew"><s:property
										value="transDetails.serviceTax" />
										
										</td>
										
							</tr>
							<tr>
								<td height="30" align="left" bgcolor="#F2F2F2">Net Amount[B
									- (D+F+G)]
									</td>
								<td align="left" bgcolor="#F2F2F2"><s:property
										value="transDetails.netAmount" />
									
										</td>
							</tr>
							<tr>
								<td height="30" align="left">Refunded Amount</td>
								<td align="left" class="txtnew"><s:property
										value="transDetails.refundedAmount" />
									
										</td>
							</tr>
						</table>
					</div>
					
				</div></td>
		</tr>
		
		<tr>
	  
		<td>
		<div class="wd70" style="width:100%; margin-left:10px;">
		<table width="96%" border="0" cellpadding="0" cellspacing="0" class='greyroundtble' style="margin-bottom:20px;">
		  <tr>
		  <td height="80" colspan="3" align="left" valign="bottom" class="txtnew">
		   <table width="97%" border="0" cellspacing="0" cellpadding="0." style="margin-top:16px;margin-left:36px;">
						    <tr>		
		<td style="width:4%">
			<span class="typeCss">Type :</span> 	<td width="21%"><s:select label="" name="chargebackType" onchange="updateFormEnabled();"
									class="form-control" id="chargebackType" headerKey=""
									headerValue="Select "
									list="@com.pay10.crm.chargeback.util.ChargebackType@values()"
									listKey="code" listValue="name" autocomplete="off"/></td>	
		<%-- <td style="width: 26%;"><s:select headerKey="ALL" headerValue="ALL" id="chargebackType"
									name="chargebackType" class="form-control"
									list="@com.pay10.chargeback.utils.ChargebackType@values()"
									listKey="code" listValue="name"  autocomplete="off" /> </td> --%>
		<%-- <td style="width:5%">Status :</td>
		<td width="21%"><s:select label="" name="chargebackStatus" onchange="updateFormEnabled();"
									class="form-control" id="chargebackStatus" headerKey=""
									headerValue="Select "
									list="@com.pay10.chargeback.utils.ChargebackStatus@values()"
									listKey="code" listValue="name" autocomplete="off"/></td>	
		<td style="width: 28%;"> <s:select headerKey="ALL" headerValue="ALL" id="chargebackStatus"
									name="chargebackStatus" class="form-control"
									list="@com.pay10.chargeback.utils.ChargebackStatus@values()"
									listKey="code" listValue="name"  autocomplete="off" /> </td> --%>
		<td style="width:8%"><span class="targetDateCss">Target Date :</span></td>
		<td style="width:28%"><s:textfield type="text" readonly="true" id="targetDate" onchange="updateFormEnabled();"
							name="targetDate" class="form-control dateTargetInput" 
							autocomplete="off" /></td>	
		<td>
		  </tr>
		      <tr>
				<td colspan="6">
				<table width="95%" border="0" cellspacing="0" cellpadding="0"
							class="product-spec" style="margin-left:1px;
    margin-top: 11px;margin-bottom:32px;">
    <tr>
							<th height="30" colspan="2" align="left">Add Comment</th>
							</tr>
							
							 <tr><td colspan="6">
		
	
		 <s:file name="image" id ="image" />
		</td></tr>
							
							 <tr><td colspan="6">
		
		<s:textarea type="text" class="textFL_merch" id="comments" 
										name="comments" autocomplete="off" cols="10" rows="6" />
		
		<s:submit class="btn btn-success" align="left" id="chargebackSubmit"  name="chargebackSubmit"
												value="Submit" style="margin-top: 15px;
    margin-left: 11px"></s:submit>
		</td></tr>
		
		
							</table>
				</td>				
				
  </tr>	
					     </table>
		</td>		
					 								
		</table>
		</div>
						</td>															
		</tr>
		
</table>
</s:form>
</body>
</html>