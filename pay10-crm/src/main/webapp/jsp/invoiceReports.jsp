<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@taglib prefix="s" uri="/struts-tags"%>
<html>
<head>
<title>Invoice Report</title>
<link href="../css/Jquerydatatableview.css" rel="stylesheet" />
<link href="../css/Jquerydatatable.css" rel="stylesheet" />
<link href="../css/default.css" rel="stylesheet" type="text/css" />
<link href="../css/jquery-ui.css" rel="stylesheet" />
<script src="../js/jquery.min.js"></script>
<script src="../js/jquery.dataTables.js"></script>
<script src="../js/jquery-ui.js"></script>
<script type="text/javascript" src="../js/moment.js"></script>
<script type="text/javascript" src="../js/daterangepicker.js"></script>
<script src="../js/jquery.popupoverlay.js"></script>
<script type="text/javascript" src="../js/dataTables.buttons.js"></script>
<script type="text/javascript" src="../js/pdfmake.js"></script>
<script type="text/javascript" src="../js/summaryReport.js"></script>
<link href="../css/loader.css" rel="stylesheet" type="text/css" />
<link
	href="../css/select2.min.css"
	 />
<script
	src="../js/select2.min.js"></script>
<script type="text/javascript">

</script>

<!-------------------ajax call on button click--------------->
<script type="text/javascript">
	function invoiceReport(){
		 var merchant = document.getElementById("merchant").value;
		 var year = document.getElementById("year1").value;
		 var month = document.getElementById("month1").value;
		  
	
		 
		 var token  = document.getElementsByName("token")[0].value;
			$.ajax({
				type: "POST",
				url:"gstSaleReportAction",
				data:
					 {"merchant":merchant, "year":year, "month":month,"token":token,"struts.token.name": "token",},
				success:function(response){
					alert("successfully done");
				},
				error:function(data){
					alert("wrong data");
				}
			});
	 }
	
</script>

<style>
.dataTables_wrapper {
	position: relative;
	clear: both;
	*zoom: 1;
	zoom: 1;
	margin-top: -30px;
}

.input-control {
    display: block;
    width: 108%;
    height: 28px;
    padding: 3px 4px;
    font-size: 14px;
    line-height: 1.42857143;
    color: #555;
    background-color: #fff;
    background-image: none;
    border: 1px solid #ccc;
    border-radius: 4px;
	margin-left:5px;
}
.button-control {
	margin-top:20px;
    display: block;
    width: 90%;
    height: 28px;
    padding: 3px 4px;
    font-size: 14px;
    line-height: 1.42857143;
    color: #555;
    background-color: #fff;
    background-image: none;
    border: 1px solid #ccc;
    border-radius: 4px;
	margin-left:5px;
	background-color: #3a9b33;
	color: white;
}

table.dataTable thead th {
    padding: 5px 7px !important;
}
.cust{
	width: 24% !important;
}
</style>
</head>
<body>
	<table id="mainTable" width="100%" border="0" align="center"
		cellpadding="0" cellspacing="0" class="txnf">
		<tr>
			<td colspan="5" align="left"><h2>Invoice Reports</h2></td>
		</tr>
		<tr>
			<td colspan="5" align="left" valign="top"><div class="MerchBx">

					<div class="form-group col-md-2 col-sm-3 col-xs-6 txtnew">


										<div class="txtnew ">
											<label for="merchant">Merchant:</label><br />
											<s:if
												test="%{#session.USER.UserType.name()=='ADMIN' || #session.USER.UserType.name()=='SUBADMIN' || #session.USER_TYPE.name()=='SUPERADMIN'}">
												<s:select name="merchant" class="input-control" id="merchant"
													headerKey="" headerValue="ALL" list="merchantList"
													listKey="emailId" listValue="businessName" autocomplete="off" />
											</s:if>
											<s:else>
												<s:select name="merchant" class="form-control" id="merchant"
													headerKey="" headerValue="ALL" list="merchantList"
													listKey="emailId" listValue="businessName" autocomplete="off" />
											</s:else>
										</div>

									</div>
					
					<div class="form-group  col-md-2 col-sm-4 txtnew  col-xs-6">
									<label for="year" >Year:</label> <br />
									<select id="year1" class="input-control" onchange="yearChange()">
									 </select>
									
								</div>
								
								
								<div class="form-group  col-md-2 col-sm-4 txtnew  col-xs-6">
									<label for="month" >Month:</label> <br />
									<select id="month1" class="input-control">
									 </select>
									
								</div>
								
					
					<div class="form-group  col-md-2 col-sm-4 txtnew  col-xs-6 ">
									<button class="button-control" id="invoiceBtn" onclick="invoiceReport()">Submit</button>
									
								</div>
		
					
					
				</div></td>
		</tr>
		<tr>
			<td colspan="5" align="left"><h2>&nbsp;</h2></td>
		</tr>
		

	</table>
	
	
	
<script>
///-------------------------------------------GETTING MONTHS OF YEAR-----------------------------------------------------/////
var selectYear = document.getElementById("year1");
var selectMonth = document.getElementById("month1");
var currentYear = new Date().getFullYear(); //getting current year 5
var currentMonth = new Date().getMonth(); //getting current month month 5

function init(){
var start = 2018,
	options = "";
	for(var year = start ; year <=currentYear; year++){
	  options += "<option>"+ year +"</option>";
	}
	selectYear.innerHTML = options;
	yearChange();
}
function yearChange(){
	var afterChangeYear = selectYear.value;
	var monthNames = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October","November", "December"];
	
	if(afterChangeYear != currentYear){
		currentMonth = 11;
	}else{
		currentMonth = new Date().getMonth();
	}
	
	$('#month1').empty();
	for (var i = 0; i <= currentMonth; i++){
           $('#month1').append('<option value="'+i+'">'+monthNames[i]+'</option>');
	}
}
init();
</script>

	

	
</body>
</html>