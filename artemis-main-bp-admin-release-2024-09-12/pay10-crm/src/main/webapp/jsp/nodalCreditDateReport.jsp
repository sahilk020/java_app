<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
    <%@taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Nodal Credit Date Report</title>
<!--------CSS Stylesheet------------>
<link href="../css/Jquerydatatableview.css" rel="stylesheet" />
<link href="../css/Jquerydatatable.css" rel="stylesheet" />
<link href="../css/default.css" rel="stylesheet" type="text/css" />
<link href="../css/jquery-ui.css" rel="stylesheet" />

<!--------JS Script----------------->
<script src="../js/jquery.min.js"></script>
<script src="../js/jquery.dataTables.js"></script>
<script src="../js/jquery-ui.js"></script>
<script src="../js/jquery.popupoverlay.js"></script> 
<script type="text/javascript" src="../js/dataTables.buttons.js"></script>  
<script type="text/javascript" src="../js/pdfmake.js"></script>

<!--  loader scripts -->
<script src="../js/loader/modernizr-2.6.2.min.js"></script>
<script src="../js/loader/main.js"></script>
<link rel="stylesheet" href="../css/loader/normalize.css" />
<link rel="stylesheet" href="../css/loader/main.css" />
<link rel="stylesheet" href="../css/loader/customLoader.css" />

<script type="text/javascript">
$(document).ready(function(){
 document.getElementById("loading").style.display = "none";

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
var selectedMerchants;
var checkValName;

function getCheckBoxValue(){
	 var allInputCheckBox = document.getElementsByClassName("myCheckBox");
  		
  		var allSelectedMerchant = [];
  		for(var i=0; i<allInputCheckBox.length; i++){
  			
  			if(allInputCheckBox[i].checked){
  				allSelectedMerchant.push(allInputCheckBox[i].value);	
  			}
  		}

  		document.getElementById('selectBox').setAttribute('title', allSelectedMerchant.join());
  		if(allSelectedMerchant.join().length>28){
  			var res = allSelectedMerchant.join().substring(0,27);
  			document.querySelector("#selectBox option").innerHTML = res+'...............';
  		}else if(allSelectedMerchant.join().length==0){
  			document.querySelector("#selectBox option").innerHTML = 'ALL';
			
  		}else{
  			document.querySelector("#selectBox option").innerHTML = allSelectedMerchant.join();
			
  		}
		selectedMerchants = allSelectedMerchant.join();
}

</script>

<script>
var checkBoxVal;

$(document).ready(function () {
$(".radioBtn").change(function () {

   checkBoxVal = $('.radioBtn:checked').val();
    //alert(checkBoxVal);
});
});
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
	$('#Download').prop('disabled',true).addClass('disabled');

});
</script>


<script type="text/javascript">
$(function() {
	
	 table = $('#smartRouterAuditReportDatatable').DataTable({
		
		dom: 'BTrftlpi',
	               destroy : true,
	               buttons : [
							{
								extend : 'pdfHtml5',
								orientation : 'landscape',
								title : 'Smart Router Report',
								exportOptions : {
									columns : [':visible']
								}
							},
							{
								extend : 'print',
								title : 'Smart Router Report',
								exportOptions : {
									columns : [':visible']
								}
							}
						],
				"searching": false,
				"paging": true,
                "lengthMenu": [ [10, 25, 50, 100, -1], [10, 25, 50, 100, "All"] ],
                "pagingType": "full_numbers",
                "pageLength": 10,
	});
	
    $('#submitDetails').on('click', function() { 
	     var merchant = document.getElementById("merchant").value;
		   if(merchant == "" || merchant == "Select Merchant"){
			   alert("Please Select Merchant");
			   return false;
		   }
		 var acquirer = document.getElementById("acquirer").value;
		    if(acquirer == "" || acquirer == "Select Acquirer"){
			   alert("Please Select Acquirer");
			   return false;
		   }
		 var paymentMethod = document.getElementById("paymentMethod").value;
		    if(paymentMethod == "" || paymentMethod == "Select Payment"){
			   alert("Please Select Payment Method");
			   return false;
		   }
		 var selectedDate = document.getElementById("nodalDate").value;
		    if(selectedDate == "" || selectedDate == null){
			   alert("Please Select Date");
			   return false;
		   }
		
		 var token = document.getElementsByName("token")[0].value;
         document.getElementById("loading").style.display = "block";
         table = $('#smartRouterAuditReportDatatable').DataTable({
			 "columnDefs": [
				        {
							"footerCallback" : function(row, data, start, end,
									display) {
								var api = this.api(), data;

								// Remove the formatting to get integer data for summation
								var intVal = function(i) {
									return typeof i === 'string' ? i.replace(
											/[\,]/g, '') * 1
											: typeof i === 'number' ? i : 0;
								};

							},
							
							
							"className": "dt-center", 
						"targets": "_all"
						}
				],
            dom: 'BTrftlpi',			
		               buttons : [
							{
								extend : 'pdfHtml5',
								orientation : 'landscape',
								title : 'Smart Router Report',
								exportOptions : {
									columns : [':visible']
								}
							},
							{
								extend : 'print',
								title : 'Smart Router Report',
								exportOptions : {
									columns : [':visible']
								}
							}
						],
			"searching": false,
			"paging": true,
            "lengthMenu": [ [10, 25, 50, 100, -1], [10, 25, 50, 100, "All"] ],
            "pagingType": "full_numbers",
            "pageLength": 10,
			destroy: true,                                  
			ajax: function (data, callback, settings) {
                           $.ajax({
                                       "url": "viewNodalReport",
                                       "timeout" : 0,
                                       "type": "POST",
                                       "data": {
                                    	   "merchantEmailId" : merchant,
											"acquirer" : acquirer,
											"paymentType" : paymentMethod,
											//"checkboxValue" : checkboxValue,
											"selectedDate" : selectedDate,
                                           "token": token,
                                        },
                                        success:function(data){
										 document.getElementById("loading").style.display = "none";
                                         callback(data);
										 
                                        },
										error:function(data) {
											  document.getElementById("loading").style.display = "none";
										  }
                                        });
                                  },
				"columnDefs": [
										{
										"className": "dt-center", 
										"targets": "_all"
								}],
				  
		"columns": [
			 { "data": "payId" },
				{ "data": "paymentType" },
	            { "data": "type" },
				{ "data": "acquirer" },
	            { "data": "nodalDate" },
				{ "data": "saleCount" },
				{ "data": "saleAmount" },
				{ "data": "refundCount" },
				{ "data": "refundAmount" },
				{ "data": "captureDate" },
				{ "data": "saleAmount" }
		        ]
        });
    });
});
</script>

<style>
.randomDisable{
  cursor: not-allowed;
  border: none;
  background-color: #c0d4f2;
  color: #666666;
}
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
  border-radius: 5px;
  border: 1px #dadada solid;
  height:auto;
  overflow-y: scroll;
  position:Absolute;
  background:#fff;
  z-index:1;
  margin-left:2px;
  margin-right:10px;
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
	margin-top:5px;
}
.form-control{
	margin-left: 0!important;
	width: 100% !important;
}
.padding10{
	padding: 10px;
}
.disabled {
    color:#fff;
	border-color: #a0a0a0;
	background-color: #a0a0a0;
}
.OtherList label {
    vertical-align: middle;
    display: block;
    color: #333;
    margin-bottom: 8px;
    margin-left: 3%;
	font-size:13px;
	font-weight:600;
}
.OtherList input {
    vertical-align: top;
    float: left;
    margin-left: 10px !important;
}
#wwctrl_nodalType {
	margin-top: 8% !important;
}
#loading {width: 100%;height: 100%;top: 0px;left: 0px;position: fixed;display: block; z-index: 99}
#loading-image {position: absolute;top: 50%;left: 52%;z-index: 100; width:10%;} 
.actionMessage li {
    color: black !important;
    background-color: #ccebda !important;
    border-color: #ccebda !important;
}
</style>


</head>
<body>
    <div id="loading" style="text-align: center;">
		<img id="loading-image" style="width:70px;height:70px;" src="../image/sand-clock-loader.gif" alt="Sending SMS..." />
	</div>
	
    <table width="100%" align="left" cellpadding="0" cellspacing="0" class="txnf">
		<tr>
			<td align="left"><h2>Nodal Transaction Report</h2>
				<div class="container">
					<div class="row padding10" style="margin-top:20px;">
							<div class="form-group col-md-3 txtnew col-sm-4 col-xs-6">
								<label for="merchant" style="margin-left: 2px;" class="aboveHead">Merchant:</label> <br />
								<s:if test="%{#session.USER.UserType.name()=='ADMIN' || #session.USER.UserType.name()=='SUBADMIN' ||    #session.USER_TYPE.name()=='SUPERADMIN'}">
									<s:select name="merchant" class="form-control" id="merchant" list="merchantList"
										headerKey="Select Merchant" headerValue="Select Merchant" listKey="emailId" listValue="businessName" autocomplete="off" />
								</s:if>
								<s:else>
									<s:select name="merchant" class="form-control" id="merchant"
										headerKey="" headerValue="ALL" list="merchantList"
										listKey="payId" listValue="businessName" autocomplete="off" />
								</s:else>
							</div>


							<div class="form-group  col-md-3 col-sm-4 txtnew  col-xs-6">
									<label for="aquirer" >Acquirer</label> <br />
										 <s:select list="@com.pay10.commons.util.AcquirerTypeUI@values()" 
										 headerKey="Select Acquirer" headerValue="Select Acquirer"
										 name="acquirer" id="acquirer" value="name" class="form-control"/>
							</div>
						
							<div class="form-group  col-md-3 col-sm-4 txtnew  col-xs-6">
								<label for="payment" >Payment Type:</label> <br />
									<select  name="paymentMethod" id="paymentMethod" autocomplete="off" class="form-control">
										<option>Select Payment</option>
										<option>CC/DC</option>
										<option>Wallet</option>
										<option>UPI</option>
									</select>
							</div>
							
							<div class="form-group col-md-3 txtnew col-sm-4 col-xs-6">
							   <label for="merchant" style="margin-left: 2px;" class="aboveHead">Nodal Date:</label> <br />
							   <s:textfield type="text" readonly="true" id="nodalDate" name="nodalDate"
								class="form-control" autocomplete="off"/>
						    </div>
							
							<div>
								<div class="form-group col-md-2 col-sm-3 txtnew col-xs-6 col-md-offset-5">
									   <s:submit id="submitDetails" name="btnEditUser" value="Submit"
										type="button" class="download-btn"></s:submit>
								</div>
							</div>
						
						</div>
                     </div>
			</td>
		
		        <div class="scrollD">
					<table id="smartRouterAuditReportDatatable" align="center" class="display" cellspacing="0" width="100%">
							<thead>
								<tr class="boxheadingsmall" style="font-size: 11px;">
									<th style="text-align:center;" data-orderable="false">Merchant</th>
									<th style="text-align:center;" data-orderable="false">Payment Method</th>
									<th style="text-align:center;" data-orderable="false">Type</th>
									<th style="text-align:center;" data-orderable="false">Acquirer</th>
									<th style="text-align:center;" data-orderable="false">Nodal Date</th>
									<th style="text-align:center;" data-orderable="false">Sale Count</th>
									<th style="text-align:center;" data-orderable="false">Sale Amount</th>
									<th style="text-align:center;" data-orderable="false">Refund Count</th>
									<th style="text-align:center;" data-orderable="false">Refund Amount</th>
									<th style="text-align:center;" data-orderable="false">Transaction Date(s)</th>
									<th style="text-align:center;" data-orderable="false">Amount</th>
								</tr>
							</thead>
							
							<tbody id="tableContent">
									
							</tbody>
							
							<tfoot>
							<tr class="boxheadingsmall">
								<th></th>
								<th></th>
								<th></th>
								<th></th>
								<th></th>
								<th></th>
								<th></th>
								<th></th>
								<th></th>
								<th></th>
								<th></th>
							</tr>
						</tfoot>
					</table>
				</div>
			<tr>
				<td align="left" style="border-bottom: 1px solid #eaeaea;">&nbsp;</td>
			</tr>
		</tr>
			
	</table>
<!-----------------DatePicker-------------------->
<script type="text/javascript">
	   function handleChange() {
			var transFrom = $.datepicker.parseDate('dd-mm-yy', $('#nodalDate').val());
			
     }
	$(document).ready(function() {
		$(function() {
			$("#nodalDate").datepicker({
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
			$('#nodalDate').val($.datepicker.formatDate('dd-mm-yy', today));
			
		});		
	});	
</script>



</body>
</html>