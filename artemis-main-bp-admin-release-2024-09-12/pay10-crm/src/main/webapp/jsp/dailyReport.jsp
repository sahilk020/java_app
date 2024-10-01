<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="/struts-tags" prefix="s"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Daily Transaction Report</title>
<!--------StyleSheet------>
<link href="../css/default.css" rel="stylesheet" type="text/css" />
<link href="../css/jquery-ui.css" rel="stylesheet" />

<!-------JavaScript------->
<script src="../js/jquery.min.js" type="text/javascript"></script>
<script src="../js/jquery-ui.js"></script>

<script type="text/javascript">
	$(document).ready(function(){
		document.getElementById("loading").style.display = "none";
	});
</script>

<script type="text/javascript"> 	
function dailyReportData(){  
   
	var requestDate = document.getElementById("currentDate").value;
	if (requestDate== "" || requestDate == null){
		alert("Please Enter Capture Date");
		return false;
	}
	var token  = document.getElementsByName("token")[0].value;
	document.getElementById("submit").style.disabled = true;
	document.getElementById("loading").style.display = "block"	
   
    $.ajax({
			"url": "downloadDailyReportAction",
			"timeout" : "0",
			"type": "POST",
			"data": {
				  "currentDate":requestDate,
				   token:token,
				  "struts.token.name": "token"
				},
			"success": function(response) {
				document.getElementById("submit").style.disabled = false;
				document.getElementById("loading").style.display = "none"	
				var responseDate=response.responseMessage;
				if(responseDate=="There is no Data Available")
				{
					document.getElementById("successmsg").style.display = "none";
					document.getElementById("errormsg").style.display = "block";
					document.getElementById("errormsg").innerHTML = responseDate;
				}	
				else
				{
					document.getElementById("errormsg").style.display = "none";
					document.getElementById("successmsg").style.display = "block";
				    document.getElementById("successmsg").innerHTML = responseDate;
				}
				//alert(response.responseMessage);
			},
			"error": function (data) {
				document.getElementById("submit").style.disabled = false;
				document.getElementById("loading").style.display = "none"
				document.getElementById("errormsg").innerHTML = response.responseMessage;
                //alert(response.response)				
			}
	});
};
</script>

<style>
.download-btn {
	background-color:#496cb6;
	display: block;
    width: 70%;
    height: 30px;
    padding: 3px 4px;
    font-size: 16px;
    line-height: 1.42857143;
    color: #fff;
    border: 1px solid #ccc;
    border-radius: 4px;
	margin-top:20px;
}
.txnf h2{
	margin:30px !important;
	font-size: 25px !important;
}
.form-control{
	margin-left: 0px !important;
}
label{
	font-weight: 700 !important;
}
#loading {width: 100%;height: 100%;top: 0px;left: 0px;position: fixed;display: block; z-index: 99}
#loading-image {position: absolute;top: 40%;left: 55%;z-index: 100; width:10%;}

.errorCls{
	color: #a94442;
    background-color: #f2dede;
    border-color: #ebccd1;
    border-radius: 4px;
    text-align: center;
    list-style-type: none;
    font-size: 14px;
}
.successCls{
	color: #339966;
    background-color: #d9f2e6;
    border-color: #ebccd1;
    border-radius: 4px;
    text-align: center;
    list-style-type: none;
    font-size: 14px;
}
</style>

</head>
<body>
	<div id="loading" style="text-align: center;">
		<img id="loading-image" style="width:70px;height:70px;" src="../image/sand-clock-loader.gif" alt="Sending SMS..." />
	</div>
	
	<table width="100%" border="0" cellspacing="0" cellpadding="0" class="txnf" style="margin-top:1%;">
		<tr>
			<td width="21%">
				<h2>Daily Transaction Report</h2>
			</td>
		</tr>
			
		<tr>
			<td align="center" valign="top">
				<table width="98%" border="0" cellspacing="0" cellpadding="0">
					<tr>
							<td align="left">
								<div class="container">
									<div class="form-group col-md-3 txtnew col-sm-2 col-xs-3">
											<label>Capture Date</label>
											<s:textfield type="text" readonly="true" id="currentDate"
											name="currentDate" class="form-control" autocomplete="off"/>
									</div>
								
								</div>
								
								<div class="form-group col-md-3 txtnew col-sm-2 col-xs-3 col-md-offset-5">
									<button class="download-btn" id="submit" onClick="dailyReportData()">Submit</button>
								</div>
									
									
									<tr>
									<div>
								      <td id="successmsg" class="successCls" style="display:none;"></td>
									  <td id="errormsg" class="errorCls" style="display:none;"></td>
								    </div>
									</tr>
							</td>
						</tr>
				</table>
			</td>
		</tr>
	</table>
		
<script type="text/javascript">
	$(document).ready(function() {
		$(function() {
			$("#currentDate").datepicker({
				prevText : "click for previous months",
				nextText : "click for next months",
				showOtherMonths : true,
				dateFormat : 'dd-mm-yy',
				selectOtherMonths : false,
				maxDate: '-1d'
			});
			
		});
		/* $(function() {
			var today = new Date();
			$('#currentDate').val($.datepicker.formatDate('dd-mm-yy', today));
		}); */
	});		
</script>
      
</body>
</html>