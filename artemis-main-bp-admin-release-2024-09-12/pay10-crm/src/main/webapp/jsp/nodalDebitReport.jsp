<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Add Nodal Debit Amount</title>
<<!--------CSS Stylesheet------->
<link href="../css/select2.min.css" rel="stylesheet" />
<link href="../css/default.css" rel="stylesheet" type="text/css" />
<link href="../css/Jquerydatatableview.css" rel="stylesheet" />
<link href="../css/Jquerydatatable.css" rel="stylesheet" />

<!--------JS Script----------------->
<script src="../js/jquery.min.js"></script>
<script src="../js/jquery-ui.js"></script>

<!----Calender MultiDates------>
<link rel="stylesheet" type="text/css" href="../css/jquery-ui.css"></link>
<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.10.1/jquery.min.js"></script>
<script src="../js/jquery-ui.js"></script>
<script src="../js/jquery-ui-1.11.1.js"></script>
<script src="../js/jquery-ui.multidatespicker.js"></script>
<script src="../js/jquery.dataTables.js"></script>

<script type="text/javascript">
$(document).ready(function(){
 document.getElementById("loading").style.display = "none";
 
  // Initialize select2
  //$("#merchant").select2();
});
</script>
	
<style>
.download-btn {
	background-color:green;
	display: block;
    width: 100%;
    height: 30px;
    padding: 3px 4px;
    font-size: 14px;
    line-height: 1.42857143;
    color: #fff;
    border: 1px solid #ccc;
    border-radius: 4px;
	margin-top:25px;
}
.form-control{
	margin-left: 5px !important;
	width: 95% !important;
}
.padding10{
	padding: 10px;
}
.disabled {
    color:#fff;
	border-color: #a0a0a0;
	background-color: #a0a0a0;
}
#wwctrl_nodalType {
	margin-top: 8% !important;
}
#loading {width: 100%;height: 100%;top: 0px;left: 0px;position: fixed;display: block; z-index: 99}
#loading-image {position: absolute;top: 50%;left: 52%;z-index: 100; width:10%;} 

</style>
</head>

<body>
    <div id="loading" style="text-align: center;">
		<img id="loading-image" style="width:70px;height:70px;" src="../image/sand-clock-loader.gif" alt="Sending SMS..." />
	</div>
	
		<table width="100%" align="left" cellpadding="0" cellspacing="0" class="txnf">
			<tr>
				<td align="left"><h2>Add Nodal Debit Amount</h2>
					<div class="container">
					
						<div class="row padding10" style="margin-top:20px;">
							<div class="form-group col-md-4 txtnew col-sm-4 col-xs-6">
								<label for="merchant" style="margin-left: 2px;" class="aboveHead">Merchant:</label> <br />
								<s:if test="%{#session.USER.UserType.name()=='ADMIN' || #session.USER.UserType.name()=='SUBADMIN' ||    #session.USER_TYPE.name()=='SUPERADMIN'}">
									<s:select name="merchant" class="form-control" id="merchant" list="merchantList"
										headerKey="Select Merchant" headerValue="Select Merchant" listKey="emailId" listValue="businessName" autocomplete="off" />
								</s:if>
								<s:else>
									<s:select name="merchant" class="form-control" id="merchant"
										headerKey="" headerValue="ALL" list="merchantList"
										listKey="emailId" listValue="businessName" autocomplete="off" />
								</s:else>
							</div>


							<div class="form-group  col-md-4 col-sm-4 txtnew  col-xs-6">
									<label for="aquirer" >Acquirer</label> <br />
										 <s:select list="@com.pay10.commons.util.AcquirerTypeUI@values()" 
										 headerKey="Select Acquirer" headerValue="Select Acquirer"
										 name="acquirer" id="acquirer" value="name" class="form-control"/>
							</div>
						

							<div class="form-group col-md-4 txtnew col-sm-4 col-xs-6">
							   <label for="payoutDate" style="margin-left: 2px;" class="aboveHead">Initiate Payout Date:</label> <br />
							   <s:textfield type="text" readonly="true" id="initiatePayoutDate" name="initiatePayoutDate"
								class="form-control" autocomplete="off"/>
						    </div>
						
							   <input type="hidden" name="nodalType" value="nodalSettlement"/>
						</div>
						
						<div>
							<div class="form-group col-md-2 col-sm-3 txtnew col-xs-6 col-md-offset-5">
									<button id="viewDetails" class="download-btn" onclick="showOtherDetails()">View</button>
							</div>
						</div>
					</div>
				</td>
			</tr>
			
			<tr>
				<td align="left">
					<div class="container" id="belowDiv" style="display:none;">
						<div class="row padding10" style="margin-top:20px;">
							<div class="form-group col-md-4 txtnew col-sm-offset-1 col-xs-6">
								<label for="captureDate" style="margin-left: 2px;" class="aboveHead">Capture Date:</label> <br />
							   <s:textfield type="text" readonly="true" id="captureDate" name="captureDate"
								class="form-control" autocomplete="off" value="23/05/2019"/>
							</div>

							<div class="form-group  col-md-4 col-sm-offset-2 txtnew  col-xs-6">
									<label for="payoutDate" style="margin-left: 2px;" class="aboveHead">Payout Amount:</label> <br />
							   <s:textfield type="text" readonly="true" id="payoutAmount" name="payoutAmount" class="form-control" autocomplete="off" value="20,239"/>
							</div>
					
							<div class="form-group col-md-4 txtnew col-sm-offset-4 col-xs-6">
							   <label for="nodalAmount" style="margin-left: 2px; margin-top:10px;" class="aboveHead">Amount:</label> <br />
							   <s:textfield type="text" id="nodalDebitAmount" name="nodalDebitAmount" class="form-control" autocomplete="off"
							   placeholder="Please Enter Amount" onkeypress="return isNumberKey(event,this)"/>
						    </div>
						
						</div>
						
						<div>
							<div class="form-group col-md-2 col-sm-3 txtnew col-xs-6 col-md-offset-5">
									<button id="submitDetails" class="download-btn" >Submit</button>
							</div>
						</div>
						
					</div>

				</td>
			</tr>
		</table>

<script type="text/javascript">
function showOtherDetails(){
	document.getElementById("belowDiv").style.display = "block";
}
</script>

<!--------AJAX CALL TO VIEW DETAILS---->
<script type="text/javascript">
$(function() {
	$('#viewDetails').on('click', function() {
	     var merchant = document.getElementById("merchant").value;
			 if(merchant == "" || merchant== "Select Merchant"){
				 alert("Please select Merchant");
				 document.getElementById("belowDiv").style.display= "none";
				 return false;
			 }
			 
		 var acquirer = document.getElementById("acquirer").value;
		    if(acquirer == "" || acquirer== "Select Acquirer"){
				 alert("Please select Acquirer");
				 document.getElementById("belowDiv").style.display= "none";
				 return false;
			 }
			 
		 var initiatePayoutDate = document.getElementById("initiatePayoutDate").value;
			 if(initiatePayoutDate == "" || initiatePayoutDate== null){
					 alert("Please select Initiate Payout Date");
					 document.getElementById("belowDiv").style.display= "none";
					 return false;
			 }
		 var token  = document.getElementsByName("token")[0].value;
		 document.getElementById("belowDiv").style.display= "block";
                           $.ajax({
                                       "url": "abc.JSON",
                                       "type": "POST",
                                       "data": {
										   "merchant": merchant,
                                           "acquirer": acquirer,
                                           "initiatePayoutDate": initiatePayoutDate,
										   "token" : token,
                                        },
										success:function(data){
											alert("Success");
										},
										error:function(data) {
											alert("Unable to fetch data");
										}
                                });
         
	});
});
</script>

<!---------AJAX CALL TO SUBMIT DATA------>
<script type="text/javascript">
$(function() {
	$('#submitDetails').on('click', function() {
	     var captureDate = document.getElementById("captureDate").value;
		 var payoutAmount = document.getElementById("payoutAmount").value;
		 var nodalDebitAmount = document.getElementById("nodalDebitAmount").value;
			 if(nodalDebitAmount == "" || nodalDebitAmount== null){
					 alert("Please enter Nodal Debit Amount");
					 return false;
			 }
		 var token  = document.getElementsByName("token")[0].value;
                           $.ajax({
                                       "url": "abc.JSON",
                                       "type": "POST",
                                       "data": {
										   "captureDate": captureDate,
                                           "payoutAmount": payoutAmount,
                                           "nodalDebitAmount": nodalDebitAmount,
										   "token" : token,
                                        },
										success:function(data){
											alert("Success");
										},
										error:function(data) {
											alert("Failed To Submit Data");
										}
                                });
         
	});
});
</script>
<!-----------------DatePicker-------------------->
<script type="text/javascript">
$(document).ready(function(){
			
			$("#initiatePayoutDate").datepicker({
				prevText : "click for previous months",
				nextText : "click for next months",
				showOtherMonths : true,
				dateFormat : 'dd-mm-yy',
				selectOtherMonths : false,
				maxDate : new Date()
			});
});		
</script>

<!----------Validation to Enter Amount--------->
<script>
function isNumberKey(evt, element) {
  var charCode = (evt.which) ? evt.which : event.keyCode
  if (charCode > 31 && (charCode < 44 || charCode > 57) && !(charCode == 46 || charCode == 8))
    return false;
  else {
    var len = $(element).val().length;
    var index = $(element).val().indexOf('.');
    if (index > 0 && charCode == 46) {
      return false;
    }
    if (index > 0) {
      var CharAfterdot = (len + 1) - index;
      if (CharAfterdot > 3) {
        return false;
      }
    }

  }
  var typedChar = String.fromCharCode(charCode);

    // Allow numeric characters
    if (/\d/.test(typedChar)) {
        return;
    }

    // Allow the minus sign (-) if the user enters it first
    if (typedChar == "-" && this.value == "") {
        return;
    }
  return true;
}
</script>



</body>
</html>