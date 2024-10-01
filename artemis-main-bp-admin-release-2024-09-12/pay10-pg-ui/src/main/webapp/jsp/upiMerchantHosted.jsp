<%@taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>UPI Merchant Hosted</title>

    <link rel="stylesheet" href="../css/bootstrap.min.css">
	<link rel="stylesheet" href="../css/common-style.css">
	<link rel="stylesheet" href="../css/loader-animation.css">
	<link rel="stylesheet" href="../css/surchargePaymentPage.css">
	<link rel="stylesheet" href="../css/style.css">
</head>

<style>
.loader2 {
  border: 16px solid #f3f3f3;
  border-radius: 50%;
  border-top: 16px solid #3498db;
  width: 120px;
  height: 120px;
  -webkit-animation: spin 2s linear infinite;
  animation: spin 2s linear infinite;
  margin:auto;
  left:0;
  right:0;
  top:-18%;
  bottom:0;
  position:fixed;
}

@-webkit-keyframes spin {
  0% { -webkit-transform: rotate(0deg); }
  100% { -webkit-transform: rotate(360deg); }
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

</style>
<body >

<div class="loader2">
</div>
    <div class="loader" id="loading" style="display:block;" >
		<div class="w-100 vh-100 d-flex justify-content-center align-items-center flex-column">
			
			<div id="approvedNotification" class="approvedNotification" style="display: block;">
				<h3 class="lang" data-key="upiApprovalText">Do not refresh this page or press back button or close the window</h3>
				<p class="lang" data-key="upiStopRefresh"><b>Note : Please wait It will take little longer time to redirect you to merchant website</b></p>
			</div>
		</div>
    </div>
    
	<s:textfield type="hidden" value="%{#session.PG_REF_NUM}" id="PG_REF_NUM" />
	<s:hidden name="customToken" id="customToken1" value="%{#session.customToken}"></s:hidden>

	<s:form name="upiResponseSubmitForm" id="upiResponseSubmitForm" target="_self" action="upiResponse">
		<s:hidden name="OID" id="resOid" value="" />
		<s:hidden name="PG_REF_NUM" id="resPgRefNum" value="" />
		<s:hidden name="RETURN_URL" id="resReturnUrl" value="" />
		<s:hidden name="token" value="%{#session.customToken}" />
	</s:form>
	
	<script>
		var startWorker = function(obj) {
			var token = document.getElementsByName("customToken")[0].value;
			if(window.Worker) {
				worker = new Worker('../js/upi-response.js');
				
				if(obj.requestType == "UPI") {
					worker.postMessage({
						_value: obj.pgRefNum,
						token: token,
						requestType: obj.requestType
					});
			
					worker.onmessage = function(e) {
						if(e.data == "cancel") {
							top.location = "txncancel";
						} else {
							document.getElementById("resPgRefNum").value = e.data.PG_REF_NUM;
							document.getElementById("resReturnUrl").value = e.data.RETURN_URL;
							document.getElementById("upiResponseSubmitForm").submit();
							// submitUPIResponseForm(e.data);
						}
					};
				}
			}
		}

		startWorker({
			pgRefNum: document.getElementById("PG_REF_NUM").value,
			requestType: "UPI"
		});
	</script>
</body>
</html>