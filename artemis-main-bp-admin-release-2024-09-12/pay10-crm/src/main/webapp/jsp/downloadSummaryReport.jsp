<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Download Summary Report</title>

<link href="../css/Jquerydatatableview.css" rel="stylesheet" />
<link href="../css/Jquerydatatable.css" rel="stylesheet" />
<link href="../css/default.css" rel="stylesheet" type="text/css" />
<link href="../css/jquery-ui.css" rel="stylesheet" />
<script src="../js/jquery.min.js"></script>
<script src="../js/jquery.dataTables.js"></script>
<script src="../js/jquery-ui.js"></script>
<script src="../js/jquery.popupoverlay.js"></script> 
<script type="text/javascript" src="../js/dataTables.buttons.js"></script>  
<script type="text/javascript" src="../js/pdfmake.js"></script>
<link href="../css/select2.min.css" rel="stylesheet" />
<script src="../js/jquery.select2.js" type="text/javascript"></script>

<script type="text/javascript">
$(document).ready(function(){
 
  // Initialize select2
  $("#merchants").select2();
});
</script>

<script>
 var expanded = false;

function showCheckboxes(e) {
  var checkboxes = document.getElementById("checkboxes");
  if (!expanded) {
    checkboxes.style.display = "block";
    expanded = true;
  } else {
    checkboxes.style.display = "none";
    expanded = false;
  }
   e.stopPropagation();

}

function getCheckBoxValue(){
	 var allInputCheckBox = document.getElementsByClassName("myCheckBox");
  		
  		var allSelectedAquirer = [];
  		for(var i=0; i<allInputCheckBox.length; i++){
  			
  			if(allInputCheckBox[i].checked){
  				allSelectedAquirer.push(allInputCheckBox[i].value);	
  			}
  		}

  		document.getElementById('selectBox').setAttribute('title', allSelectedAquirer.join());
  		if(allSelectedAquirer.join().length>28){
  			var res = allSelectedAquirer.join().substring(0,27);
  			document.querySelector("#selectBox option").innerHTML = res+'...............';
  		}else if(allSelectedAquirer.join().length==0){
  			document.querySelector("#selectBox option").innerHTML = 'ALL';
  		}else{
  			document.querySelector("#selectBox option").innerHTML = allSelectedAquirer.join();
  		}
}
</script>
<script type="text/javascript">
$(document).ready(function(){
	$(document).click(function(){
		expanded = false;
		$('#checkboxes').hide();
	});
	$('#checkboxes').click(function(e){
		e.stopPropagation();
	});


});
</script>

<style>
  .divalignment{
	  margin-top: -30px !important;
  }
  
  .case-design{
	  text-decoration:none;
	  cursor: default !important;
  }
  .my_class:hover{
	  color: white !important;
  }
 .multiselect {
    width: 170px;
	display:block;
	margin-left:-4px;	
 }
  .selectBox {
  position: relative;
 }

#checkboxes {
  display: none;
  border: 1px #dadada solid;
  height:300px;
  overflow-y: scroll;
  position:Absolute;
  background:#fff;
  z-index:1;
  margin-left:5px;
}

#checkboxes label {
  width: 74%;
}
#checkboxes input {
  width:18%;

}
.selectBox select {
  width: 95%;
  
}
.overSelect {
  position: absolute;
  left: 0;
  right: 0;
  top: 0;
  bottom: 0;
}
.download-btn {
	background-color:#496cb6;
	display: block;
    width: 100%;
    height: 30px;
    padding: 3px 4px;
    font-size: 14px;
    line-height: 1.42857143;
    color: #fff;
    border: 1px solid #ccc;
    border-radius: 4px;
	margin-top:30px;
}
.form-control{
	margin-left: 0!important;
	width: 100% !important;
}
.padding10{
	padding: 10px;
}
.OtherList input{
    vertical-align: top;
    float: left;
    margin-left: 10px !important;
}
.OtherList label{
     vertical-align: middle;
    display: block;
    font-weight: 700;
    color: #333;
    margin-bottom:8px;
}
</style>


</head>
<body>
<form id="downloadSummaryReportAction" name="downloadSummaryReportAction" action="downloadSummaryReportAction">
<table width="100%" align="left" cellpadding="0" cellspacing="0" class="txnf">
		<tr>
			<td align="left"><h2 style="margin-bottom:5%;">Download Summary Report</h2>
			
			<div class="container">

							        <div class="form-group col-md-2 col-sm-3 col-xs-6 txtnew" style = "display :none">
										<div class="txtnew">
											<label for="acquirer">PG REF NUM:</label><br />
												<s:textfield id="pgRefNum" class="form-control"
												name="pgRefNum" type="text" value="" autocomplete="off"
												onkeypress="javascript:return isNumber (event)" maxlength="16"></s:textfield>
										</div>
									</div>
									
									<div class="form-group col-md-2 col-sm-3 col-xs-6 txtnew">
										<div class="txtnew">
											<label for="acquirer">Merchant</label><br />
											<s:if
												test="%{#session.USER.UserType.name()=='ADMIN' || #session.USER.UserType.name()=='SUBADMIN'  || #session.USER.UserType.name()=='SUBADMIN' || #session.USER_TYPE.name()=='SUPERADMIN'}">
												<s:select name="merchantPayId" class="form-control" id="merchants"
													headerKey="ALL" headerValue="ALL" list="merchantList"
													listKey="payId" listValue="businessName"  autocomplete="off" />
											</s:if>
											<s:else>
												<s:select name="merchantPayId" class="form-control" id="merchants"
													headerKey="" headerValue="" list="merchantList"
													listKey="payId" listValue="businessName" autocomplete="off" />
											</s:else>
										</div>
									</div>

									<div class="form-group  col-md-2 col-sm-4 txtnew  col-xs-6">
										<div class="txtnew">
												<label for="account">Acquirer</label><br />					
											<div class="multiselect">
												<div class="selectBox" id="selectBox" onclick="showCheckboxes(event)">
												  <select class="form-control">
													<option>ALL</option>
												  </select>
												  <div class="overSelect"></div>
												</div>
											<div id="checkboxes" onclick="getCheckBoxValue()">
											   <s:checkboxlist headerKey="ALL" headerValue="ALL" list="@com.pay10.commons.util.AcquirerTypeUI@values()" 
																	listValue="name" listKey="code" 
																		id="acquirer" name="acquirer" class="myCheckBox" value="acquirer" />
											</div>
										  </div>
											
										</div>
									</div>

								<div class="form-group col-md-2 col-sm-3 col-xs-6 txtnew">
									<label for="paymentMethod">Payment Method</label> <br />
								 <s:select name="paymentMethods" id="paymentMethods" headerValue="ALL"
										  headerKey="ALL" list="@com.pay10.commons.util.PaymentType@values()"
											listValue="name" listKey="code" 
										  class="form-control" />
								</div>
								
								
								<div class="form-group  col-md-2 col-sm-4 txtnew  col-xs-6">
									<label for="currency">Currency</label> <br />
									<s:select name="currency" id="currency" headerValue="ALL"
										headerKey="ALL" list="currencyMap" class="form-control"/>
								</div>

								<div class="form-group  col-md-2 col-sm-3 txtnew col-xs-6">
									<label for="dateFrom">Settled Date From</label> <br />
									<s:textfield type="text" readonly="true" id="dateFrom"
										name="dateFrom" class="form-control" autocomplete="off"
										onchange="handleChange();" />
								</div>
								<div class="form-group  col-md-2 col-sm-3 txtnew col-xs-6">
									<label for="dateTo">Settled Date To</label> <br />
									<s:textfield type="text" readonly="true" id="dateTo"
										name="dateTo" class="form-control" onchange="handleChange();"
										autocomplete="off" />
								</div>
								<div class="form-group  col-md-2 col-sm-3 txtnew col-xs-6">
									<label for="transactionRegion" >Transaction Region</label> <br />
										<s:select headerKey="ALL" headerValue="ALL" class="form-control"
										list="#{'INTERNATIONAL':'International','DOMESTIC':'Domestic'}" name="paymentsRegion" id = "paymentsRegion" />
								</div>
								<div class="form-group  col-md-2 col-sm-3 txtnew col-xs-6">
									<label for="cardHolderType" >Card Holder Type</label> <br />
										<s:select headerKey="ALL" headerValue="ALL" class="form-control"
										list="#{'CONSUMER':'Consumer','COMMERCIAL':'Commercial'}" name="cardHolderType" id = "cardHolderType" />
								</div>
								
								<div class="form-group  col-md-2 col-sm-3 txtnew col-xs-6">
								<label for="transactionType" >Transaction Type</label> <br />
										<s:select headerKey="ALL" headerValue="ALL" class="form-control"
										list="#{'SALE':'SALE','REFUND':'REFUND'}" name="transactionType" id = "transactionType" />
								</div>
								
								<div class="form-group col-md-2 col-sm-3 col-xs-6 txtnew">
									<label for="mopType">Mop Type</label> <br />
								 <s:select name="mopType" id="mopType" headerValue="ALL"
										  headerKey="ALL" list="@com.pay10.commons.util.MopTypeUI@values()"
										listValue="name" listKey="code" class="form-control"/>
								</div>

				
					
								<div class="row">
								  <div class="txtnew">
									<button class="btn btn-primary  mt-4 submit_btn">Download</button>
								  </div>
								</div>
					
					
			</div>

			</td>
		</tr>
		
		<tr>
			<td align="left" style="border-bottom: 1px solid #eaeaea;">&nbsp;</td>
		</tr>
		<tr>
		</tr>
	</table>
</form>

<script type="text/javascript">
	   function handleChange() {
			var transFrom = $.datepicker.parseDate('dd-mm-yy', $('#dateFrom').val());
			var transTo = $.datepicker.parseDate('dd-mm-yy', $('#dateTo').val());
			if (transFrom == null || transTo == null) {
				alert('Enter date value');
				return false;
			}

			if (transFrom > transTo) {
				alert('From date must be before the to date');
				$('#dateFrom').focus();
				return false;
			}
			if (transTo - transFrom > 7 * 86400000) {
				alert('No. of days can not be more than 7');
				$('#dateFrom').focus();
				return false;
			}
     }
	$(document).ready(function() {
		
		$(function() {
			$("#dateFrom").datepicker({
				prevText : "click for previous months",
				nextText : "click for next months",
				showOtherMonths : true,
				dateFormat : 'dd-mm-yy',
				selectOtherMonths : false,
				maxDate : new Date()
			});
			$("#dateTo").datepicker({
				prevText : "click for previous months",
				nextText : "click for next months",
				showOtherMonths : true,
				dateFormat : 'dd-mm-yy',
				selectOtherMonths : false,
				maxDate : new Date()
			});
		});
		$(function() {
			var today = new Date();
			$('#dateTo').val($.datepicker.formatDate('dd-mm-yy', today));
			$('#dateFrom').val($.datepicker.formatDate('dd-mm-yy', today));	

		});		
	});	
</script>



</body>
</html>