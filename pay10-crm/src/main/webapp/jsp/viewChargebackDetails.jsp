<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>View Chargeback</title>
<script src="../js/jquery.min.js"></script>
<script src="../js/core/popper.min.js"></script>
<script src="../js/commanValidate.js"></script>
<script src="../js/core/bootstrap-material-design.min.js"></script>
<script src="../js/plugins/perfect-scrollbar.jquery.min.js"></script>
<!-- Control Center for Material Dashboard: parallax effects, scripts for the example pages etc -->
<script src="../js/material-dashboard.js?v=2.1.0" type="text/javascript"></script>
<script src="../js/jquery-ui.js"></script>

<script type="text/javascript" src="../js/viewChargebackDetails.js"></script>

<style>
.form-border {
	border: 1px solid #ddd !important;
}

table.dataTable.display tbody tr.odd {
	background-color: #e6e6ff !important;
}

table.dataTable.display tbody tr.odd>.sorting_1 {
	background-color: #e6e6ff !important;
}

.hideElement {
	display: none;
}

#materialSendIcon {
	position: absolute;
	right: 15px;
	bottom: 25px;
}

.chargebackChatBoxMessages p {
	margin: 0px;
	color: #000000;
	font-weight: 400;
	font-size: 18px;
}

.time-right {
	font-size: 14px;
	color: black;
	font-weight: 400;
}

.adminMsg {
	border-color: #ccc !important;
	background-color: #cac5fa !important;
	text-align: left;
	width: max-content;
	max-width: 500px;
	border: 2px solid #dedede;
	border-radius: 8px;
	padding: 20px;
}

.merchantMsg {
	width: max-content;
	border: 2px solid #dedede;
	background-color: #f6b4b4;
	float: right;
	border-radius: 8px;
	padding: 20px;
	width: max-content;
	max-width: 500px;
}

#chargebackTriggerMessagee:hover {
	background-color: #496cb6;
	border-color: #496cb6;
}

.chargebackChatBoxClass {
	/* width: max-content; */
	min-height: 225px;
	height: 225px;
	overflow: auto;
	border: 1px solid #999999;
	padding: 10px;
	padding-top: 0px;
	/*  min-width: 150px; */
	border-radius: 10px;
}

.chargebackChatBoxMessages {
	border-radius: 5px;
	padding: 10px;
	margin: 10px 0;
	min-height: auto;
	margin-bottom: 0px;
	margin-top: 0px;
	width: 100%;
	float: right;
}

.chargebackChatBoxMessages.adminMsg {
	float: left;
}

.chargebackChatBoxMessages.merchantMsg {
	float: right;
}

#chargebackTriggerMessage {
	color: #fff;
	background: linear-gradient(60deg, #425185, #4a9b9b);
	/* background-color: #4a9b9b; */
	border-color: #496cb6;
	box-shadow: 0 2px 2px 0 #4a9b9b, 0 3px 1px -2px #496cb6, 0 1px 5px 0
		#496cb6;
	border: 0;
	text-transform: uppercase;
}

#chargebackTriggerMessagee {
	color: #fff;
	background: linear-gradient(60deg, #425185, #4a9b9b);
	/* background-color: #4a9b9b; */
	border-color: #496cb6;
	box-shadow: 0 2px 2px 0 #4a9b9b, 0 3px 1px -2px #496cb6, 0 1px 5px 0
		#496cb6;
	border: 0;
	text-transform: uppercase;
}

#chargebackAddMessage {
	width: 100%;
	padding-right: 100px;
	min-height: 50px;
	border-radius : 10px;
}

.col-sm-6.col-lg-3 {
	margin-bottom: 10px;
	margin-top: 20px;
}

#materialSendIcon {
	position: absolute;
	right: 25px;
	bottom: 52px;
	font-size: 30px;
	color: #000000;
}

#materialSendIconUplaod {
	bottom: 52px;
	position: absolute;
	right: 65px;
	font-size: 30px;
}

.filedownload p {
	font-size: 12px;
	margin: 0px 5px;
}

.filedownload a {
	margin: 0px 5px;
}

.filedownload {
/* 	border-radius: 50px; */
/* 	height: 27px; */
	padding: 1px 5px;
}

#cbFile {
	display: none;
}

#wwgrp_caseStatus {
	display: inline-block;
}

.chargebackChatBoxMessages pre {
	font-size: 16px;
	font-style: oblique;
}

.filedownload pre {
	font-size: 13px;
	font-weight: bold;
}

.chargebackChatBoxMessages span {
	font-size: 10px;
	float: right;
}

/* .filedownload {
  position: relative;
  display: inline-block;
  border-bottom: 1px dotted black;
} */
.filedownload {
	display: inline-block;
	position: relative;
	border-bottom: 1px dotted #666;
	text-align: left;
}

.filedownload h3 {
	margin: 12px 0;
}

.filedownload .tooltiptext {
	min-width: 200px;
	max-width: 400px;
	top: -10px;
	left: 50%;
	transform: translate(-30%, -100%);
	padding: 10px 20px;
	color: #ffffff;
	background-color: #009cdc;
	font-weight: normal;
	font-size: 14px;
	border-radius: 8px;
	position: absolute;
	z-index: 99999999;
	box-sizing: border-box;
	box-shadow: 0 1px 8px rgba(0, 0, 0, 0.5);
	display: none;
}

.filedownload:hover .tooltiptext {
	display: block;
}

.filedownload .tooltiptext i {
	position: absolute;
	top: 100%;
	left: 30%;
	margin-left: -15px;
	width: 30px;
	height: 15px;
	overflow: hidden;
}

.filedownload .tooltiptext i::after {
	content: '';
	position: absolute;
	width: 15px;
	height: 15px;
	left: 50%;
	transform: translate(-50%, -50%) rotate(45deg);
	background-color: #009cdc;
	box-shadow: 0 1px 8px rgba(0, 0, 0, 0.5);
}
.bold-weight{
	font-weight: 600;
}
.txtnew{
	font-size: 16px !important;
}
@media only screen and (max-width: 768px) {
.adminMsg {

	max-width: 300px;

}

.merchantMsg {
	
	max-width: 300px;
}
}
.addfildn
{
	margin: 25px 0 12px 0 !important;
}
/* .filedownload .tooltiptext {
  visibility: hidden;
  width: 120px;
  background-color: black;
  color: #fff;
  text-align: center;
  border-radius: 6px;
  padding: 5px 0;


  position: absolute;
  z-index: 1;
}

.filedownload:hover .tooltiptext {
  visibility: visible;
} */
</style>
</head>
<body>
	<input type="hidden" value="<s:property value='%{#session.USER.firstName}'/>" id="cbUsername">
	<input type="hidden" value="<s:property value='%{#session.USER.UserType.name()}'/>" id="cbUserType">
	
	<s:hidden name="caseId" id="caseId" value="%{chargeback.caseId}" />
	<s:hidden name="id" id="chargebackId" value="%{chargeback.id}" />
	<table width="100%" border="0" align="center" cellpadding="0"
		cellspacing="0" class="txnf">
		<tr>
			<td></td>
		</tr>
		<tr>
			<td align="left"><h2>
					Case ID :
					<s:property value="chargeback.caseId" />
				</h2></td>
		</tr>

		<tr>
			<td>
				<div id="saveMessage"></div>
			</td>
		</tr>
		<tr>
			<td colspan="3" align="left" valign="top"><div class="adduT">
			<div class="">
				<table width="100%" border="0" cellspacing="0" cellpadding="0"
					class="product-spec">
					<tr>
						<th height="30" colspan="2" align="left">Chargeback Details</th>
					</tr>
					
					<tr>
						<td height="30" class="bold-weight" align="left" width="46%">Case Id</td>
						<td align="left" class="txtnew"><s:property
							value="chargeback.caseId" /></td>
					</tr>
					<tr>
						<td height="30" align="left" class="bold-weight" bgcolor="#F2F2F2">Chargeback Id</td>
						<td align="left" class="txtnew"><s:property
							value="chargeback.id" /></td>
					</tr>

					
					
					<tr>
						<td height="30" align="left" class="bold-weight" bgcolor="#F2F2F2">Chargeback
							Status</td>
							<td align="left" bgcolor="#F2F2F2" 
							id="chargebackStatusTd"><s:select headerKey="ALL"
								headerValue="ALL" id="caseStatus" name="caseStatus"
								class="textFL_merch"
								list="@com.pay10.crm.chargeback_new.util.CaseStatus@values()"
								listKey="code" listValue="name" autocomplete="off"
								value="chargeback.chargebackStatus" /> <input type="button"
							id="btnSave" name="btnSave" class="btn btn-success btn-md disabled"
							value="Submit" onclick="submitForm()"></td>
					</tr>
					<tr>
						<td height="30" align="left" class="bold-weight">Chargeback Type</td>
						<td align="left" class="txtnew"><s:property
							value="chargeback.chargebackType" /></td>
					</tr>

					
					<tr>
						<td height="30" align="left" class="bold-weight" bgcolor="#F2F2F2">Due Date</td>
						<td align="left" bgcolor="#F2F2F2"><s:property
							value="dueDate" /></td>
							</td>
					</tr>
					<tr>
						<td height="30" align="left" class="bold-weight">Last Updated Date</td>
						<td align="left" class="txtnew" id="cbUpdateDate"><s:property
							value="updateDate" /></td>
					</tr>
					
				
					
				
				</table>
				<div class="clear"></div>
			</div>



		
		</tr>
		<tr>

			<td colspan="3" align="left" valign="top"><div class="adduT">
					<div class="">
						<table width="100%" border="0" cellspacing="0" cellpadding="0"
							class="product-spec">
							<tr>
								<th height="30" colspan="2" align="left">Order Details</th>
							</tr>
							<tr>
								<td height="30" align="left" class="bold-weight" width="46%">Order Id</td>
								<td align="left" class="txtnew"><s:property
										value="chargeback.orderId" /></td>
							</tr>
							<tr>
								<td height="30" class="bold-weight" align="left" bgcolor="#F2F2F2">Chargeback Raise Date</td>
								<td align="left" bgcolor="#F2F2F2"><s:property
										value="createDate" /></td>
							</tr>
							
							<tr>
								<td height="30" class="bold-weight" align="left" bgcolor="#F2F2F2">Chargeback Registration Date</td>
								<td align="left" bgcolor="#F2F2F2"><s:property
										value="registrationDate" /></td>
							</tr>
							<tr>
								<td height="30" class="bold-weight" align="left">Merchant Id</td>
								<td align="left" class="txtnew"><s:property
										value="chargeback.payId" /></td>
							</tr>
							<tr>
								<td height="30" class="bold-weight" align="left" bgcolor="#F2F2F2">Card Number
									Mask</td>
								<td align="left" bgcolor="#F2F2F2"><s:if
										test="%{chargeback.cardNumber !=null && chargeback.cardNumber != ''}">
										<s:property value="chargeback.cardNumber" />
									</s:if> <s:else>Not applicable</s:else></td>
							</tr>
							<tr>
								<td height="30" class="bold-weight" align="left">Payment Method</td>
								<td align="left" class="txtnew"><s:property
										value="chargeback.paymentType" />&nbsp;(<s:property
										value="chargeback.mopType" />)</td>
							</tr>
							<tr>
								<td height="30" class="bold-weight" align="left">Card Issuer Info</td>
								<td align="left" class="txtnew">
								<s:if test="%{chargeback.internalCardIssusserBank !=null}">
									<s:property value="chargeback.internalCardIssusserBank" />
									<s:if test="%{chargeback.internalCardIssusserCountry !=null}">
										&nbsp;(<s:property value="chargeback.internalCardIssusserCountry" />)
									</s:if>
								</s:if> 
								<s:else>Not applicable</s:else></td>
							</tr>
							<tr>
								<td height="30" class="bold-weight" align="left" bgcolor="#F2F2F2">Customer
									Email Id</td>
								<td align="left" bgcolor="#F2F2F2"><s:property
										value="chargeback.custEmail" /></td>
							</tr>
							<tr>
								<td height="30" class="bold-weight" align="left">IP Address</td>
								<td align="left" class="txtnew"><s:property
										value="chargeback.internalCustIP" /></td>
							</tr>
							<tr>
								<td height="30" class="bold-weight" align="left" class='greytdbg'>Country</td>
								<td class='greytdbg' align="left"><s:property
										value="chargeback.internalCustCountryName" /></td>
							</tr>
						</table>
						<div class="clear"></div>
					</div>

					<div class="">
						<table width="100%" border="0" cellspacing="0" cellpadding="0"
							class="product-spec">
							<tr>
								<th colspan="2" height="30" align="left" bgcolor="#eef8ff">Amount
									Summary</th>
							</tr>
							<tr>
								<td height="30" class="bold-weight" align="left" width="46%">Currency</td>
								<td align="left" class="txtnew"><s:property
										value="chargeback.currencyNameCode" /></td>
							</tr>
							<tr>
								<td height="30" class="bold-weight" align="left" bgcolor="#F2F2F2">Authorized
									Amount [A]</td>
								<td align="left" id="cbAuthorizedAmount" bgcolor="#F2F2F2"><s:property
										value="chargeback.authorizedAmount"  /></td>


							</tr>
							<tr>
								<td height="30" class="bold-weight" align="left">Captured Amount [B]</td>
								<td align="left" class="txtnew" id="cbCapturedAmount"><s:property
										value="chargeback.capturedAmount" /></td>
							<tr>
								<td height="30" class="bold-weight" align="left" bgcolor="#F2F2F2">Chargeback
									Amount [C]</td>
								<td align="left" id="cbChargebackAmount" bgcolor="#F2F2F2"><s:property
										value="chargeback.chargebackAmount" /></td>
							</tr>
							<tr>
								<td height="30" class="bold-weight" align="left" bgcolor="#F2F2F2">TDR (<s:property
										value="chargeback.merchantTDR" />% of B + D) [F]

								</td>
								<td align="left"  bgcolor="#F2F2F2"><s:property
										value="chargeback.tdr" /></td>

							</tr>
							<tr>
								<td height="30" align="left" class="bold-weight">Service Tax (<s:property
										value="chargeback.percentecServiceTax" />% of F) [G]

								</td>
								<td align="left"  class="txtnew"><s:property
										value="chargeback.serviceTax" /></td>

							</tr>
							<tr>
								<td height="30" class="bold-weight" align="left" bgcolor="#F2F2F2">Net Amount[B
									- (D+F+G)]</td>
								<td align="left" id="cbNetAmount" bgcolor="#F2F2F2"><s:property
										value="chargeback.netAmount" /></td>
							</tr>
							<tr>
								<td height="30" class="bold-weight" align="left">Refunded Amount</td>
								<td align="left" class="txtnew"><s:property
										value="chargeback.refundedAmount" /></td>
							</tr>
						</table>
					</div>

				</div>
			</td>
		</tr>
		<tr>
			<td colspan="3" align="left" valign="top"><div class="adduT">
					<div class="clear"></div>
				</div></td>
		</tr>

	</table>
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td align="left" valign="middle"><div class="indent">
					<table width="100%" border="0" cellspacing="0" cellpadding="7"
						class="formboxRR" style="display: none;">
						<tr>
							<td align="left" valign="top">&nbsp;</td>
						</tr>

						<div class="addfildn">
							<!-- Add chat box -->
							<s:textfield type="text" name="chargebackChatHidden"
								value="" id="chargebackChatHidden"
								class="fl_input hideElement" readonly="true" autocomplete="off" />
							<div style="display: flex; width: 100%;">
								<div class="col-sm-12 col-lg-12">
									<label>Chatbox</label>
									<div id="chargeBackChatBox" class="chargebackChatBoxClass"></div>
									<span id="chargebackAddMessageSpan"> <textarea
											id="chargebackAddMessage" placeholder="Enter message here..." />
										</textarea> <i class="material-icons" style="cursor: pointer;"
										id="materialSendIcon">send</i>
									</span> <label for="cbFile" class="material-icons"
										style="cursor: pointer;"><i class="fa fa-upload"
										id="materialSendIconUplaod" aria-hidden="true"></i></label>

									<!--  onsubmit="uploadFile()" -->
									<form id="saleform" name="saleform" target="output_frame" method="post" action="cbFileUpload" enctype="multipart/form-data">
										<input name="fileUpload" id="cbFile" type="file" accept="*" style="margin-top: 25px"> 
										<input class="hideElement"name="usertype" value='<s:property value="%{#session.USER.UserType.name()}"/>'>
										<input class="hideElement" name="username" value='<s:property value = "%{#session.USER.firstName}"/>'>
										<input class="hideElement" name="message" value=""> 
										<input class="hideElement" name="cbId" value='<s:property value="chargeback.id" />'> 
										<input class="hideElement" name="timestamp" id="cbFileUploadTimeStamp" value="">
									</form>
									<input class="hideElement" id="cbOldChatLength" value="">
									<iframe class="hideElement" name="output_frame" src=""
										id="output_frame" width="XX" height="YY"> </iframe>
								</div>
							</div>
								<div>
									<div style="text-align: left;">
										<ul>
											<li>File formats allowed : .jpg, .jpeg, .png, .pdf</li>
											<li>Number of files uploaded should not be greater than 10.</li>
											<li>Sum of size of all files should not exceed 10MB.</li>
											<li>Characters allowed in file name : dot, space, underscore, round brackets, alphabets and digits.</li>
											<li>File name length should be in range : 1 to 50</li>
										</ul>
									</div>
									<div style="text-align: center;font-weight: bold;" id="cbErrorField"></div>
								</div>
						</div>

					</table>
				</div></td>
		</tr>
	</table>

	<div class="modal fade" id="chargebackUpdateSuccess" role="dialog">
		<div class="modal-dialog">

			<!-- Modal content-->
			<div class="modal-content">
				<div class="modal-header"></div>
				<div class="modal-body">
					<p class="enter_otp">Chargeback Update Sucessfully</p>
				</div>
				<div class="modal-footer" id="modal_footer">
					<button type="button"
						class="btn btn-primary btn-round mt-4 submit_btn"
						data-dismiss="modal">Ok</button>
				</div>
			</div>

		</div>
	</div>
	<div class="modal fade" id="chargebackUpdateFail" role="dialog">
		<div class="modal-dialog">

			<!-- Modal content-->
			<div class="modal-content">
				<div class="modal-header"></div>
				<div class="modal-body">
					<p class="enter_otp">Failed to Update Chargeback</p>
				</div>
				<div class="modal-footer" id="modal_footer">
					<button type="button"
						class="btn btn-primary btn-round mt-4 submit_btn"
						data-dismiss="modal">Ok</button>
				</div>
			</div>

		</div>
	</div>
								
								
								<script>
    var myframe = document.getElementById("output_frame");
    myframe.onload = function() {
        var iframeDocument = myframe.contentDocument || myframe.contentWindow.document; // get access to DOM inside the iframe
        var content = iframeDocument.textContent || iframeDocument.body.textContent; // get text of iframe
        var json = JSON.parse(content);

        if (json) {
            // process the json here
            $('#cbErrorField').html(json.responseMessage);
            setTimeout(function(){
            	$('#cbErrorField').html("");
            }, 5000)
            //console.log(json);
        }
    }

    $(document).ready(function() {
		if (window.location.href.includes("viewChargebackDetailsAction")) {
			var menuAccess = document.getElementById("menuAccessByROLE").value;
			var accessMap = JSON.parse(menuAccess);
			var access = accessMap["viewChargeback"];
			if (access.includes("Update")) {
				$("#btnSave").removeClass("disabled");
			} else {
				$("#btnSave").remove();
			}
		}
	});
</script>
</body>
</html>