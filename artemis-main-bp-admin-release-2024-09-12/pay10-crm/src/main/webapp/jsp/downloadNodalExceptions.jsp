<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Download Nodal Exceptions Report</title>

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
  $("#merchant").select2();
});
</script>
	
<style>
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
	margin-top:15px;
}
.form-control{
	margin-left: 0!important;
	width: 80% !important;
}
.padding10{
	padding: 10px;
}
.markedCheck{
	color: black;
	font-size:13px;
}
</style>

</head>
<body>

<table width="100%" align="left" cellpadding="0" cellspacing="0" class="txnf">
		<tr>
			<td align="left"><h2>Download Nodal Exceptions</h2>
				<div class="container">
					<div class="row padding10">
						<div class="form-group col-md-4 txtnew col-sm-3 col-xs-6">
							<label for="merchant" style="margin-left: 2px;">Merchant:</label> <br />
							<s:if
								test="%{#session.USER.UserType.name()=='ADMIN' || #session.USER.UserType.name()=='SUBADMIN'  || #session.USER.UserType.name()=='SUBADMIN' || #session.USER_TYPE.name()=='SUPERADMIN'}">
								<s:select name="merchant" class="form-control" id="merchant"
									headerKey="ALL" headerValue="ALL" list="merchantList"
									listKey="payId" listValue="businessName" autocomplete="off" />
							</s:if>
							<s:else>
								<s:select name="merchant" class="form-control" id="merchant"
									headerKey="" headerValue="" list="merchantList"
									listKey="payId" listValue="businessName" autocomplete="off" />
							</s:else>
						</div>

						<div class="form-group  col-md-4 col-sm-3 txtnew col-xs-6">
							<label for="dateFrom">Capture Date From:</label> <br />
							<s:textfield type="text" readonly="true" id="dateFrom" 
								name="dateFrom" class="form-control" autocomplete="off"/>
						</div>
					
						<div class="form-group  col-md-4 col-sm-3 txtnew col-xs-6">
							<label for="dateTo">Capture Date To:</label> <br />
							<s:textfield type="text" readonly="true" id="dateTo" name="dateTo" 
								class="form-control" autocomplete="off"/>
						</div>
					
					</div>
					
					<div class="row"><div class="form-group col-md-2 col-sm-3 txtnew col-xs-6 col-md-offset-5">
						<button class="download-btn" id="downloadReport" onclick="checkDiv()">Download</button>
					</div></div>
					
				</div>
	
			</td>
		</tr>
		<tr>
			<td align="left" style="border-bottom: 1px solid #eaeaea;">&nbsp;</td>
		</tr>
		<tr>
		</tr>
</table>

<!-----------------DatePicker-------------------->
<script type="text/javascript">

	$(document).ready(function() {
		
		$(function() {
			$("#dateFrom").datepicker({
				prevText : "click for previous months",
				nextText : "click for next months",
				showOtherMonths : true,
				dateFormat : 'dd/mm/yy',
				selectOtherMonths : false,
				maxDate : new Date()
			});
			$("#dateTo").datepicker({
				prevText : "click for previous months",
				nextText : "click for next months",
				showOtherMonths : true,
				dateFormat : 'dd/mm/yy',
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

<script type="text/javascript">
function checkDiv(){
	
	var merchant = document.getElementById("merchant").value;
	
	if(merchant == "" || merchant== "Select Merchant"){
				 alert("Please select Merchant");
				 document.getElementById("anotherDiv").style.display= "none";
				 return false;
			 }
	
	var dateFrom = document.getElementById("dateFrom").value;
	
	if(dateFrom == "" || dateFrom== "Select Date" ){
				 alert("Please select date from ");
				 document.getElementById("anotherDiv").style.display= "none";
				 return false;
			 }
			 
	var dateTo = document.getElementById("dateTo").value;
	
	if(dateTo == "" || dateTo== "Select Date" ){
				 alert("Please select date to");
				 document.getElementById("anotherDiv").style.display= "none";
				 return false;
			 }
			 
			document.getElementById("merchantPayIdForm").value = merchant;
			document.getElementById("captureDateFromForm").value = dateFrom;
			document.getElementById("captureDateToForm").value = dateTo;     
			document.getElementById("downloadReportAction").submit();
	
	
}
</script>

<form id="downloadReportAction" name="downloadReportActionName" action="downloadNodalExceptionsReport" >

	<s:hidden name="token" value="%{#session.customToken}"></s:hidden>
	<s:hidden name="merchantPayId" id ="merchantPayIdForm"  value= "" ></s:hidden>
	<s:hidden name="captureDateFrom" id ="captureDateFromForm" value=""></s:hidden>
	<s:hidden name="captureDateTo" id ="captureDateToForm" value=""></s:hidden>
	
</form>
</body>
</html>