<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Download Payments Report</title>

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

<link rel="stylesheet" type="text/css" href="../css/bootstrap.min.css">
<link rel="stylesheet" type="text/css" href="../css/bootstrap-datetimepicker.css">
<script type="text/javascript" src="../js/bootstrap.min.js"></script>
<script type="text/javascript" src="../js/moment-with-locales.js"></script>
<script type="text/javascript" src="../js/bootstrap-datetimepicker.min.js"></script>

<script type="text/javascript">
$(document).ready(function(){
 
  // Initialize select2
  $(".adminMerchants").select2();
  //$("#status").select2();
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
    width: 210px;
	display:block;
	margin-left:-20px;	
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
	width: 104% !important;
}
.padding10{
	padding: 10px;
}
.form-second{
	display: block;
    width: 112%;
    height: 28px;
    padding: 3px 4px;
    font-size: 14px;
    line-height: 1.42857143;
    color: #555;
    background-color: #fff;
    background-image: none;
    border: 1px solid #ccc;
    border-radius: 4px;
    -webkit-box-shadow: inset 0 1px 1px rgba(0,0,0,.075);
    box-shadow: inset 0 1px 1px rgba(0,0,0,.075);
    -webkit-transition: border-color ease-in-out .15s,-webkit-box-shadow ease-in-out .15s;
    -o-transition: border-color ease-in-out .15s,box-shadow ease-in-out .15s;
    transition: border-color ease-in-out .15s,box-shadow ease-in-out .15s;
}
.textnew2{
	font-family: 'Open Sans', sans-serif;
    font-size: 13px;
    font-weight: 400;
    color: #333;
    text-decoration: none;
	margin-left: 1%; 
}
.select2-container{
	width: 255px !important;
}

<!---Date time picker css--->
.bootstrap-datetimepicker-widget td.old, .bootstrap-datetimepicker-widget td.new{
	opacity: .35 !important;
	color:none !important;
    background-image: none !important;
}
.table-condensed>tbody>tr>td{
	padding: 0px !important;
}
.bootstrap-datetimepicker-widget.dropdown-menu{
	padding: 0px !important;
	margin: 0px !important;
	width: 116% !important;
	margin-left: 5px !important;
	top: 53.508px !important;
}
.bootstrap-datetimepicker-widget.dropdown-menu.top:after {
    border-left: none !important;
    border-right: none !important;
    border-top: none !important;
    bottom: none !important;
    left: none !important; 
}
.bootstrap-datetimepicker-widget.dropdown-menu.bottom:after{
	top: 0px !important;
}
.picker-switch{
	border: 1px solid #aed0ea !important;
    background: #deedf7 url(../image/ui-bg_highlight-soft_100_deedf7_1x100.png) 50% 50% repeat-x !important;
    color: #222222 !important;
    font-weight: bold !important;
    font-size: 11px !important;
}
.dow{
    font-size :11px !important;
}
.glyphicon{
	font-size:12px !important;
}
.day old{
	opacity: .35 !important;
    color: none !important;
    background-image: none !important;
}
.day old weekend{
	opacity: .35 !important;
    color: none !important;
    background-image: none !important;
}
.bootstrap-datetimepicker-widget td.day{
	border: 1px solid #aed0ea !important;
    background: #d7ebf9 url(../image/ui-bg_glass_80_d7ebf9_1x400.png) 50% 50% repeat-x !important;
    font-weight: bold !important;
    color: #2779aa !important;
    font-size: 11px !important;
    height: 20px !important;
}
.bootstrap-datetimepicker-widget td.active{
	font-weight: bold !important;
    font-size: 11px !important;
    height: 20px !important;
    line-height: 20px !important;
	border: 1px solid #f9dd34;
    background: #ffef8f url(../image/ui-bg_highlight-soft_25_ffef8f_1x100.png) 50% top repeat-x !important;
    color: #363636 !important;
}
.bootstrap-datetimepicker-widget a[data-action]{
	color: #337ab7 !important;
	padding: 0px !important;
}
.bootstrap-datetimepicker-widget td{
	width: 10px !important;
}
</style>


</head>
<body>
<form id="downloadPaymentsReportAction" name="downloadPaymentsReportAction" action="downloadPaymentsReportAction">
<table width="100%" align="left" cellpadding="0" cellspacing="0" class="txnf">
		<tr>
			<td align="left"><h2 style="margin-bottom: 4% !important;">Download Payments Report</h2>
			
			<div class="container">

							<div class="form-group  col-md-3 col-sm-4 txtnew  col-xs-6">
							<label for="merchantPayId" >Merchant</label> <br />
							<s:if
								test="%{#session.USER.UserType.name()=='ADMIN'|| #session.USER.UserType.name()=='SUBADMIN' || #session.USER_TYPE.name()=='SUPERADMIN'}">
								<s:select name="merchantPayId" class="form-control adminMerchants" id="merchantPayId"
									headerKey="ALL" headerValue="ALL" list="merchantList"
									listKey="payId" listValue="businessName" autocomplete="off" />
							</s:if>
							<s:else>
								<s:select name="merchantPayId" class="form-control" id="merchantPayId" list="merchantList"
									listKey="payId" listValue="businessName" autocomplete="off" />
							</s:else>
						</div>

					<div class="form-group col-md-2 textnew2 col-sm-2 col-xs-4" style="margin-left: -0.2% !important;">
						
						<label for="aquirer" >Payment Method</label> <br />
						<div class="textnew2">
							<s:select headerKey="ALL" headerValue="ALL" class="form-second"
								list="@com.pay10.commons.util.PaymentType@values()"
								listValue="name" listKey="code" name="paymentType"
								id="paymentType" autocomplete="off" value="" />
						</div>
					</div>
					

					<div class="form-group col-md-2 textnew2 col-sm-2 col-xs-4">
						<label for="aquirer">Transaction Type</label> <br />
						<div class="textnew2">
							<s:select headerKey="ALL" headerValue="ALL" class="form-second"
								list="txnTypelist"
								listValue="name" listKey="code" name="transactionType"
								id="transactionType" autocomplete="off" value="name"/>
					    </div>
					</div>
				
					<div class="form-group col-md-2 textnew2 col-sm-2 col-xs-4">
						<label for="aquirer" >Status</label> <br />
						<div class="textnew2">
							<s:select headerKey="ALL" headerValue="ALL" class="form-second"
								list="lst" name="status" id="status" value="name" listKey="name"
								listValue="name" autocomplete="off" style="width:130% !important;"/>
						</div>
					</div>

					<div class="form-group  col-md-2 col-sm-3 textnew2 col-xs-6">
						<label for="aquirer" style="margin-left:28px;">Transaction Region</label> <br />
						<div class="textnew2">
							<s:select headerKey="ALL" headerValue="ALL" class="form-second"
								list="#{'INTERNATIONAL':'International','DOMESTIC':'Domestic'}" name="paymentsRegion" id = "paymentsRegion" style="margin-left:30px !important;"/>
						</div>
					</div>

                    <s:if
						test="%{#session.USER.UserType.name()=='ADMIN'|| #session.USER.UserType.name()=='SUBADMIN' || #session.USER_TYPE.name()=='SUPERADMIN'}">
								<div class="form-group  col-md-3 col-sm-4 txtnew  col-xs-6">
								<label for="aquirer" >Acquirer</label> <br />
									  <div>
										<div class="selectBox" id="selectBox" onclick="showCheckboxes(event)" title="dummy Title">
										  <select class="form-control">
											<option>ALL</option>
										  </select>
										  <div class="overSelect"></div>
										</div>
										<div id="checkboxes" onclick="getCheckBoxValue()">
										   <s:checkboxlist headerKey="ALL" headerValue="ALL" list="@com.pay10.commons.util.AcquirerTypeUI@values()" 
														id="acquirer" class="myCheckBox" listKey="code"
								           listValue="name" name="acquirer" value="acquirer" />
										</div>
									  </div>
							
					            </div>
					</s:if>
					<s:else>
							<div class="form-group  col-md-3 col-sm-4 txtnew  col-xs-6" style="display:none; margin-left:-10px;">
								<label for="aquirer" >Acquirer</label> <br />
									  <div>
										<div class="selectBox" id="selectBox" onclick="showCheckboxes(event)" title="dummy Title">
										  <select class="form-control">
											<option>ALL</option>
										  </select>
										  <div class="overSelect"></div>
										</div>
										<div id="checkboxes" onclick="getCheckBoxValue()">
										   <s:checkboxlist headerKey="ALL" headerValue="ALL" list="@com.pay10.commons.util.AcquirerTypeUI@values()" 
														id="acquirer" class="myCheckBox" listKey="code"
								           listValue="code" name="acquirer" value="acquirer" />
										</div>
									  </div>
							
					            </div>
					</s:else>
					
					
					<div class="form-group col-md-2 textnew2 col-sm-2 col-xs-4" style="margin-left: -0.1% !important;">
						<label for="email" >Currency:</label> <br />
						<div class="textnew2">
						<s:select name="currency" id="currency" headerValue="ALL"
								headerKey="ALL" list="currencyMap" class="form-second" />
						</div>
					</div>

				
					<div class="form-group  col-md-2 col-sm-3 textnew2 col-xs-6">
						<label for="dateFrom" >Date From:</label> <br />
						<div class="textnew2">
						<s:textfield type="text" readonly="true" id="dateFrom" name="dateFrom" class="form-second" 
							autocomplete="off"/>
						</div>
					</div>
					
					<div class="form-group  col-md-2 col-sm-3 textnew2 col-xs-6">
						<label for="dateTo" >Date To:</label> <br />
						<div class="textnew2">
						<s:textfield type="text" readonly="true" id="dateTo" name="dateTo" class="form-second"  autocomplete="off" />
						</div>
					</div>
					
					<div class="row">
					   <div class="form-group col-md-2 col-sm-3 txtnew col-xs-6 col-md-offset-5">
						<button class="download-btn">Download</button>
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

<!------Calender with Time code---Shefali------->
<script type="text/javascript">
	$(document).ready(function() {
		var date1 = new Date();
		date1.setHours(0,0);
		
		var date2 = new Date();
		date2.setHours(23,59);
		
		$(function() {
			$("#dateFrom").datetimepicker({
				ignoreReadonly: true,
				useStrict: false,
				keepOpen: true,
				//debug:true,
				minDate: "01/01/2018",
				format: 'DD-MM-YYYY HH:mm',
				useCurrent: false,
				maxDate: new Date(),
				defaultDate: date1
			});
		});
		
		$(function() {
			$("#dateTo").datetimepicker({
				ignoreReadonly: true,
				useStrict: false,
				keepOpen: true,
				minDate: "01/01/2018",
				maxDate: date2,
				format: 'DD-MM-YYYY HH:mm',
				useCurrent: false,
				defaultDate: date2
			});
		});
		
	});	
</script>

<!-----------For Logout Dropdown(IP-32)-------------->
<script>
$(".dropdown").click(function(){
	var elementVal = document.getElementById("openDropdown");
    if(elementVal.style.display == 'none'){
         elementVal.style.display = 'block';
        }
	else if (elementVal.style.display == ""){
		 elementVal.style.display = 'block';
	    }
        else{
         elementVal.style.display = 'none';
    }
});
</script>

<!----------To hide logout popup on BLUR------->
<script>
$(document).click(function(e) {
    if (!$(e.target).is("#openDropdown")) {
        if ($('#openDropdown').is(':visible')) {
            document.getElementById("openDropdown").style.display = "none";
        }
    }
});
</script>

</body>
</html>