<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Statistics Report</title>

<link href="../css/Jquerydatatableview.css" rel="stylesheet" />
<link href="../css/Jquerydatatable.css" rel="stylesheet" />
<link href="../css/default.css" rel="stylesheet" type="text/css" />
<link href="../css/jquery-ui.css" rel="stylesheet" />
<link href="../css/select2.min.css" rel="stylesheet" />
<script src="../js/jquery.min.js"></script>
<script src="../js/jquery.dataTables.js"></script>
<script src="../js/jquery-ui.js"></script>
<script src="../js/jquery.popupoverlay.js"></script> 
<script type="text/javascript" src="../js/dataTables.buttons.js"></script>  
<script type="text/javascript" src="../js/pdfmake.js"></script>
<script src="../js/jquery.select2.js" type="text/javascript"></script>

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
	margin-top:30px;
}
.form-control{
	margin-left: 0!important;
	width: 100% !important;
}
.padding10{
	padding: 10px;
}
.sorting {
    background: none !important;
}
.subHeadingCss {
	background-color: #496cb6 !important;
	color:white !important;
	text-align: center !important;
}
table.dataTable thead .sorting_asc {
    background: none !important;
}
.tableClass{
	border-bottom:1px solid black;
}



.myTable{
margin:0 auto;
text-align: center;
}
.myTable .tRow{
border:1px solid #73879C;
display: flex;
border-bottom: none;   
}
.myTable .tRow:last-child{
border-bottom: 1px solid #73879C;
}
.myTable .tRow .tColmn{
border-right: 1px solid #73879C;
flex-basis: 100%;

}

.myTable .tRow .tColmn:last-child{
border-right: none;
}
.myTable .tRow .tColmn .tabl{
    display: table; 
    height: 100%;
    width: 100%;
}
.myTable .tRow .tColmn .tablInr{
    display: table-cell; vertical-align: middle;
}
.myTable .tRow .tColmn .subColmn{
border-bottom: 1px solid #f1f1f1;
} 
.myTable .tRow .tColmn .subColmn:last-child{
border-bottom: none;
}
.tableHeading{
	text-align: center;
    background: #496cb6;
    color: #fff;
    text-transform: capitalize;
}


.tableHeading table{
	table-layout: fixed;
	border-collapse: collapse;
	border-bottom: none;
}
.tableHeading th, .tableHeading td {
  border: 1px solid #225bb1;
  border-collapse: collapse;
  padding: 5px 0;
}
.tableHeading td{
	border-bottom: none;
}
.txn_report{
	padding: 0 10px;
}
#loading {
	width: 100%;
	height: 100%;
	top: 0px;
	left: 0px;
	position: 
	fixed;display: 
	block; 
	z-index: 99;
	background: rgb(0,0,0,0.8);
	display: none;
}
#loading-image {
	position: absolute;
	top: 50%;
	left: 50%;
	z-index: 100; 
	width:135px;
	height:135px;
	margin-top: -68px;
	margin-left: -68px;
}


</style>

</head>
<body>
	<div id="loading" style="text-align: center;">
		<img id="loading-image" style="width:70px;height:70px;" src="../image/sand-clock-loader.gif" alt="Sending SMS..." />
	</div>

    <table width="100%" align="left" cellpadding="0" cellspacing="0" class="txnf">
		<tr>
			<td align="left"><h2>Statistics Report</h2>
				<div class="container">

					<div class="row padding10">
						<div class="form-group col-md-3 txtnew col-sm-4 col-xs-6">
							<label for="merchant" style="margin-left: 2px;" class="aboveHead">Select Merchant</label> <br />
								<s:select name="merchants" class="form-control" id="selectMerchant" headerKey="All" headerValue="All"
		listKey="payId" listValue="businessName" list="merchantList" autocomplete="off"/>
						</div>
						<div class="form-group col-md-3 txtnew col-sm-4 col-xs-6">
							<label for="datefrom" style="margin-left: 2px;" class="aboveHead">Date From:</label> <br />
								<s:textfield type="text" readonly="true" id="dateFrom"
								name="dateFrom" class="form-control" onchange="handleChange();" 
								autocomplete="off"/>
						</div>
						
						<div class="form-group col-md-3 txtnew col-sm-4 col-xs-6">
							<label for="dateto" style="margin-left: 2px;" class="aboveHead">Date To:</label> <br />
								<s:textfield type="text" readonly="true" id="dateTo" name="dateTo"
								class="form-control" onchange="handleChange();" autocomplete="off"/>
						</div>

						<div class="form-group  col-md-3 col-sm-4 txtnew  col-xs-6">
								<label for="aquirer" class="aboveHead">Acquirer</label> <br />
								<div class="txtnew">
									<s:select headerKey="Select Acquirer" headerValue="Select Acquirer" class="form-control"   list="@com.pay10.commons.util.AcquirerTypeUI@values()" 
									listValue="name" listKey="code" id="acquirer" name="acquirer" value="acquirer" />
								</div>
						</div>
					
						<div class="form-group  col-md-3 col-sm-4 txtnew  col-xs-6">
							<label for="paymentType" class="aboveHead">Payment Type:</label> <br />
							   <select class="form-control" style="margin-left: 2px !important; width:95% !important;" id="paymentType" name="paymentType">
									<option>Select Payment</option>
									<option>CC-DC</option>
									<option>UPI</option>
									<option>PPI</option>
							   </select>
						</div>
					
					</div>
					
					<div class="row">
					    <div class="form-group col-md-2 col-sm-3 txtnew col-xs-6 col-md-offset-5">
						  <button class="download-btn" id="searchBtn">Search</button>
					    </div>
					</div>
				<!-- table start here-->
				<div class="txn_report">
					<div class="tableHeading">
						<table style="width:100%">
						  <tr>
						  	<td style="border-bottom: 1px solid transparent;">
						  		<span style="position: relative; top: 11px;">Merchant</span>
						  	</td>
							<td colspan="3">Capture</td>
							<td colspan="3">Settled</td>
							<td colspan="3">Nodal</td>
							<td colspan="2">Pending</td>
						  </tr>
						   <tr>
						   	<td></td>
						   	<td>Txn Date</td>
							<td>No Of Txn</td>
							<td>Amount</td>
							<td>No Of Txn</td>
							<td>Amount</td>
							<td>Date</td>
							<td>No Of Txn</td>
							<td>Amount</td>
							<td>Date</td>
							<td>No Of Txn</td>
							<td>Amount</td>
						  </tr>
						</table>
					</div>
					<div id="result" class="myTable"></div>
					
				</div>
				</div>
			</td>
			
			<table>
		    <tr>
				

						
			</tr>
			
		    </table>
		</tr>
		
		<tr>
			<td align="left" style="border-bottom: 1px solid #eaeaea;">&nbsp;</td>
		</tr>
		
	</table>


	   <!-----------------DatePicker-------------------->
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
	if (transTo - transFrom > 31 * 86400000) {
		alert('No. of days can not be more than 31');
		$('#dateFrom').focus();
		return false;
	}
	return true;
}

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


$(function() {
function tableInit(pageNo){
	document.getElementById('loading').style.display="block";
	var alldata = {
		dateFrom: document.getElementById("dateFrom").value,
		dateTo:document.getElementById("dateTo").value,
		acquirer:document.getElementById('acquirer').value,
		paymentType:document.getElementById('paymentType').value,
		merchant:document.getElementById('selectMerchant').value
	}
	
	
	$.ajax({
		url: "statisticsReportAction",
		type : "POST",
		timeout : 0,
		data : alldata,
		success: function(response){
			document.getElementById('loading').style.display="none";
	        createTable(JSON.parse(response.strJson).response)	
		},
		error: function(xhr, textStatus, errorThrown){
	       alert('request failed for statisticsReportAction');
		   document.getElementById('loading').style.display="none";
	    }
	}); 
}


function createTable(getResponse) {
	var tableStructue = '';
	for(i=0; i<getResponse.length; i++){
	    
	    tableStructue += '<div class="tRow">\
	    					<div class="tColmn"><div class="tabl"><div class="tablInr">'+getResponse[i].merchant+'</div></div></div>\
	                        <div class="tColmn"><div class="tabl"><div class="tablInr">'+getResponse[i].txnDate+'</div></div></div>\
	                        <div class="tColmn"><div class="tabl"><div class="tablInr">'+getResponse[i].CapNoTxn+'</div></div></div>\
	                        <div class="tColmn"><div class="tabl"><div class="tablInr">'+getResponse[i].CapAmount+'</div></div></div>'

	    tableStructue += '<div class="tColmn">'
	                    for(j=0; j<getResponse[i].SettleNoTxn.length; j++){
	                           tableStructue += '<div class="subColmn">'+getResponse[i].SettleNoTxn[j]+'</div>'
	                    }
	    tableStructue += '</div>'

	    tableStructue += '<div class="tColmn">'
	                    for(k=0; k<getResponse[i].SettleAmount.length; k++){
	                            tableStructue += '<div class="subColmn">'+getResponse[i].SettleAmount[k]+'</div>'
	                    }
	    tableStructue += '</div>'

	    tableStructue += '<div class="tColmn">'
	                    for(l=0; l<getResponse[i].SettleDate.length; l++){
	                            tableStructue += '<div class="subColmn">'+getResponse[i].SettleDate[l]+'</div>'
	                    }
	    tableStructue += '</div>'  
		
		tableStructue += '<div class="tColmn">'
	                    for(m=0; m<getResponse[i].NodalNoTxn.length; m++){
	                           tableStructue += '<div class="subColmn">'+getResponse[i].NodalNoTxn[m]+'</div>'
	                    }
	    tableStructue += '</div>'

	    tableStructue += '<div class="tColmn">'
	                    for(n=0; n<getResponse[i].NodalAmount.length; n++){
	                            tableStructue += '<div class="subColmn">'+getResponse[i].NodalAmount[n]+'</div>'
	                    }
	    tableStructue += '</div>'

	    tableStructue += '<div class="tColmn">'
	                    for(p=0; p<getResponse[i].NodalDate.length; p++){
	                            tableStructue += '<div class="subColmn">'+getResponse[i].NodalDate[p]+'</div>'
	                    }
	    tableStructue += '</div>'

			tableStructue +=    '<div class="tColmn"><div class="tabl"><div class="tablInr">'+getResponse[i].PendingNoTxn+'</div></div></div>\
	                        <div class="tColmn"><div class="tabl"><div class="tablInr">'+getResponse[i].PendingAmount+'</div></div></div>'
	    tableStructue += '</div>'; 
	}
	document.getElementById('result').innerHTML='';
	document.getElementById('result').innerHTML=tableStructue;

}

//tableInit();

$('#searchBtn').click(function(){
	if(!handleChange()){
		return false;
	}
	if(!$('#selectMerchant').val()){
		alert('please select Merchant')
		return false;
	}
	if(document.getElementById('acquirer').value == "Select Acquirer" ){
		alert('please select Acquirer')
		return false;
	}
	if(document.getElementById('paymentType').value == "Select Payment"){
		alert('please select Payment Type')
		return false;
	}
	tableInit();
});

});

$(document).ready(function(){
	$("#selectMerchant").select2();
});		
	
</script>


</body>
</html>