<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="/struts-tags" prefix="s"%>
<html>
<head>
<meta http-equiv="cache-control" content="no-cache">
<!-- tells browser not to cache -->
<meta http-equiv="expires" content="0">
<!-- says that the cache expires 'now' -->
<meta http-equiv="pragma" content="no-cache">
<!-- says not to use cached stuff, if there is any -->
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Charging Platform</title>


<link rel="shortcut icon" href="assets/media/images/paylogo.svg" />
<!--begin::Fonts-->
<link rel="stylesheet"
	href="https://fonts.googleapis.com/css?family=Inter:300,400,500,600,700" />
<!--end::Fonts-->
<!--begin::Vendor Stylesheets(used by this page)-->
<link
	href="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.css"
	rel="stylesheet" type="text/css" />
<link href="../assets/plugins/custom/datatables/datatables.bundle.css"
	rel="stylesheet" type="text/css" />

<!--end::Vendor Stylesheets-->
<!--begin::Global Stylesheets Bundle(used by all pages)-->
<link href="../assets/plugins/global/plugins.bundle.css"
	rel="stylesheet" type="text/css" />
<link href="../assets/css/style.bundle.css" rel="stylesheet"
	type="text/css" />
<link href="../assets/css/custom/custom-style.css" rel="stylesheet"
	type="text/css" />

<script src="../assets/plugins/global/plugins.bundle.js"></script>
<script src="../assets/js/scripts.bundle.js"></script>
<link href="../css/select2.min.css" rel="stylesheet" />
<script src="../js/jquery.select2.js" type="text/javascript"></script>

<style type="text/css">
.card-list-toggle {
	cursor: pointer;
	padding: 8px 12px;
	border: 1px solid #496cb6;
	position: relative;
	background: linear-gradient(60deg, #ffd602, #ff2121);
}

.card-list-toggle:before {
	position: absolute;
	right: 10px;
	top: 7px;
	content: "\f078";
	font-family: 'FontAwesome';
	font-size: 15px;
}

.card-list-toggle.active:before {
	content: "\f077";
}

.card-list {
	display: none;
}

.btn:focus {
	outline: 0 !important;
}

.sub-headingCls {
	margin-top: 20px;
	margin-bottom: 0px;
	font-size: 12px;
	font-weight: 700;
	color: #0271bb;
}

.col-sm-6.col-lg-3 {
	margin-bottom: 10px;
}

tr td div input {
    width: 55px;
    margin: 3px;
}
</style>

<!-- search -->
<script type="text/javascript">
	$(document).ready(function () {
	$("#merchants").select2();
	});
</script>
<script type="text/javascript">
$(document).ready(function(){
 
  // Initialize select2
 /*  $("#merchants").select2(); */
});
</script>

<script type="text/javascript">
$(document).ready(function() {
	$('.card-list-toggle').on('click', function(){
		$(this).toggleClass('active');
		$(this).next('.card-list').slideToggle();
	});

	

	$('#merchants').change(function(event){
		 /*   $('#loader-wrapper').show(); */
		 
		    var merchants = document.getElementById("merchants").value;
			var urls = new URL(window.location.href);
			var domain = urls.origin;
		    
			 $.ajax({
					type : "GET",
					url : domain+"/crmws/acquirer/getMappedAcquirer",
					timeout : 0,
					data : {

						"emailId" : merchants,

					},
					
					success : function(data, status) {
						var acquirer= JSON.stringify(data);
					    var acquirerList= JSON.parse(acquirer);
						//var s = '<option value="">Select acquirer</option>';
						var s;
						for (var i = 0; i<acquirerList.length; i++) {
						
							s += '<option value="' + acquirerList[i] + '">' + acquirerList[i]
									+ '</option>';
						}
						document.getElementById("acquirerDropdown").style.display = "block";

						$("#acquirer").html(s);
					}
				});
		    var paymentRegion = $("select#paymentRegion").val();
			   var cardHolderType = $("select#cardHolderType").val();
		  	   if(acquirer==null||acquirer==""){
		  		  
				   $('#loader-wrapper').hide();
		  		   return false;
		  	   }
			   else if (merchants==null ||merchants==""){
				   
				   $('#loader-wrapper').hide();
		  		   return false;
			   }
			   else if (paymentRegion==null ||paymentRegion==""){
				  
				   $('#loader-wrapper').hide();
		  		   return false;
			   }
			   else if (cardHolderType==null ||cardHolderType==""){
				   $('#loader-wrapper').hide();
		  		   return false;
			   }
		   document.getElementById("chargingdetailform").submit();	
	    });

   $('#acquirer').change(function(event){
	  /*  $('#loader-wrapper').show(); */
	   var acquirer = document.getElementById("acquirer").value;
	    var merchants = document.getElementById("merchants").value;
	    var paymentRegion = $("select#paymentRegion").val();
		   var cardHolderType = $("select#cardHolderType").val();
	  	   if(acquirer==null||acquirer==""){
	  		  
			  /*  $('#loader-wrapper').hide(); */
	  		   return false;
	  	   }
		   else if (merchants==null ||merchants==""){
			  
			 /*   $('#loader-wrapper').hide(); */
	  		   return false;
		   } 
		   else if (paymentRegion==null ||paymentRegion==""){
			   
			/*    $('#loader-wrapper').hide(); */
	  		   return false;
		   }
		   else if (cardHolderType==null ||cardHolderType==""){
			  /*  $('#loader-wrapper').hide(); */
	  		   return false;
		   }
	   document.getElementById("chargingdetailform").submit();	
    });

   $('#paymentRegion').change(function(event){
	  /*  $('#loader-wrapper').show(); */
	   var acquirer = document.getElementById("acquirer").value;
	    var merchants = document.getElementById("merchants").value;
	    var paymentRegion = $("select#paymentRegion").val();
		   var cardHolderType = $("select#cardHolderType").val();
	  	   if(acquirer==null||acquirer==""){
	  		   
			  /*  $('#loader-wrapper').hide(); */
	  		   return false;
	  	   }
		   else if (merchants==null ||merchants==""){
			   
			   /* $('#loader-wrapper').hide(); */
	  		   return false;
		   } 
		   else if (paymentRegion==null ||paymentRegion==""){
			  
			 /*   $('#loader-wrapper').hide(); */
	  		   return false;
		   }
		   else if (cardHolderType==null ||cardHolderType==""){
			  /*  $('#loader-wrapper').hide(); */
	  		   return false;
		   }
	   document.getElementById("chargingdetailform").submit();	
    });

   $('#cardHolderType').change(function(event){
	/* 	$('#loader-wrapper').show(); */
		   var merchants = $("select#merchants").val();
		   var acquirer = $("select#acquirer").val();
		   var paymentRegion = $("select#paymentRegion").val();
		   var cardHolderType = $("select#cardHolderType").val();
		   if(acquirer==null ||acquirer=="" ||merchants==null ||merchants==""||paymentRegion==null ||paymentRegion==""||cardHolderType==null ||cardHolderType==""){
			   $('#loader-wrapper').hide();
			   return false;
			}
		   document.getElementById("chargingdetailform").submit();
	});
   
   $('#updateServiceTax').click(function(){
       $('#popup').popup('show');
  });
});

var editMode;
function editCurrentRow(divId,curr_row,ele){
	
	//var nextElement =  ele.parentNode.parentNode.parentNode.parentNode;
	var div = document.getElementById(divId);

	var table = div.getElementsByTagName("table")[0];

	var merchantId = document.getElementById("merchants").value;
	var acquirer = document.getElementById("acquirer").value;
	var region = "<s:property value='paymentRegion' />";
	var merchantType = "<s:property value='cardHolderType' />";
	var rows = table.rows;
	var currentRowNum = Number(curr_row);
	var currentRow = rows[currentRowNum];
	var cells = currentRow.cells;
	var cell0 = cells[0];
	var cell1 = cells[1];
	var cell2 = cells[2];
	var cell3 = cells[3]; //+3 ignoring the first three columns of the table
	var cell4 = cells[4].children[0];
	var cell5 = cells[4].children[1];
	var cell6 = cells[5].children[0];
	var cell7 = cells[5].children[1];
	var cell8 = cells[6].children[0];
	var cell9 = cells[6].children[1];
	var cell10 = cells[7].children[0];
	var cell11 = cells[7].children[1];
	var cell12 = cells[8].children[0];
	var cell13 = cells[8].children[1];
	var cell14 = cells[9].children[0];
	var cell15 = cells[9].children[1];
	var cell16 = cells[10];
	var cell17 = cells[11];
	var cell18 = cells[12];
	var cell19 = cells[13];
	var cell20 = cells[14];
	var cell21 = cells[15];

	var cell0Value = cell0.innerText.trim();
	var cell1Value = cell1.innerText.trim();
	var cell2Value = cell2.innerText.trim();
	var cell3Value = cell3.innerText.trim();
	var cell4Value = cell4.innerText.trim();
	var cell5Value = cell5.innerText.trim();
	var cell6Value = cell6.innerText.trim();
	var cell7Value = cell7.innerText.trim();
	var cell8Value = cell8.innerText.trim();
	var cell9Value = cell9.innerText.trim();
	var cell10Value = cell10.innerText.trim();
	var cell11Value = cell11.innerText.trim();
	var cell12Value = cell12.innerText.trim();
	var cell13Value = cell13.innerText.trim();
	var cell14Value = cell14.innerText.trim();
	var cell15Value = cell15.innerText.trim();
	var cell16Value = cell16.innerText.trim();
	var cell17Value = cell17.innerText.trim();
	var cell18Value = cell18.innerText.trim();
	var cell19Value = cell19.innerText.trim();
	var cell20Value = cell20.innerText.trim();
	var cell21Value = cell21.innerText.trim();

	var allowFc  = cell18.querySelector('input[type=checkbox]').checked;
	var id = cell20Value;		

	if(ele.value=="edit"){
		if(editMode) 
		{
			alert('Please edit the current row to proceed');
			return;
		}
		ele.value="save";
		ele.className ="btn btn-primary btn-xs";

		// Domestic Consumer
		if (region == 'DOMESTIC' && merchantType == 'CONSUMER') {
			cell4.innerHTML = "<input type='number' id='cell4Val'   onkeypress='return isPositiveNumber(event)' class='chargingplatform'              step='0.0' value="+cell4Value+" disabled></input>";
			cell5.innerHTML = "<input type='number' id='cell5Val'   onkeypress='return isPositiveNumber(event)' class='chargingplatform' min='0'      step='0.0' value="+cell5Value+" disabled></input>";
			cell6.innerHTML = "<input type='number' id='cell6Val'   onkeypress='return isPositiveNumber(event)' class='chargingplatform' min='0'      step='0.0' value="+cell6Value+" disabled></input>";
			cell7.innerHTML = "<input type='number' id='cell7Val'   onkeypress='return isPositiveNumber(event)' class='chargingplatform' min='0'      step='0.0' value="+cell7Value+" disabled></input>";
			cell8.innerHTML = "<input type='number' id='cell8Val'   onkeypress='return isPositiveNumber(event)' class='chargingplatform' min='0'      onkeyup='mykey()' step='0.0' value="+cell8Value+"></input>";
			cell9.innerHTML = "<input type='number' id='cell9Val'   onkeypress='return isPositiveNumber(event)'  class='chargingplatform' min='0'     onkeyup='Function()' step='0.0' value="+cell9Value+"></input>";
			cell10.innerHTML = "<input type='number' id='cell10Val' onkeypress='return isPositiveNumber(event)' class='chargingplatform' min='0'      onkeyup='Fixcharge1()'step='0.0' value="+cell10Value+"></input>";		
			cell11.innerHTML = "<input type='number' id='cell11Val' onkeypress='return isPositiveNumber(event)' class='chargingplatform' min='0'      onkeyup='Fixcharge2()'step='0.0' value="+cell11Value+"></input>";
			cell12.innerHTML = "<input type='number' id='cell12Val' onkeypress='return isPositiveNumber(event)' class='chargingplatform' min='0'      onkeyup='mykey()' step='0.0' value="+cell12Value+" ></input>";
			cell13.innerHTML = "<input type='number' id='cell13Val' onkeypress='return isPositiveNumber(event)' class='chargingplatform' min='0'      onkeyup='Function()'  step='0.0' value="+cell13Value+"></input>";
			cell14.innerHTML = "<input type='number' id='cell14Val' onkeypress='return isPositiveNumber(event)' class='chargingplatform' min='0'      onkeyup='Fixcharge1()' step='0.0' value="+cell14Value+" ></input>";
			cell15.innerHTML = "<input type='number' id='cell15Val' onkeypress='return isPositiveNumber(event)' class='chargingplatform' min='0'      onkeyup='Fixcharge2()' step='0.0' value="+cell15Value+"></input>";
		}
		
		// Domestic Commercial
		if (region == 'DOMESTIC' && merchantType == 'COMMERCIAL') {
			cell4.innerHTML = "<input type='number' id='cell16Val'  onkeypress='return isPositiveNumber(event)'  class='chargingplatform' min='0'  step='0.0' value="+cell4Value+"></input>";
			cell5.innerHTML = "<input type='number' id='cell17Val'  onkeypress='return isPositiveNumber(event)'  class='chargingplatform' min='0' step='0.0' value="+cell5Value+"></input>";
			cell6.innerHTML = "<input type='number' id='cell18Val'  onkeypress='return isPositiveNumber(event)'  class='chargingplatform' min='0' step='0.0' value="+cell6Value+"></input>";
			cell7.innerHTML = "<input type='number' id='cell19Val'  onkeypress='return isPositiveNumber(event)'  class='chargingplatform' min='0' step='0.0' value="+cell7Value+"></input>";
			cell8.innerHTML = "<input type='number' id='cell20Val'  onkeypress='return isPositiveNumber(event)'  class='chargingplatform' min='0' step='0.0' value="+cell8Value+"></input>";
			cell9.innerHTML = "<input type='number' id='cell21Val'  onkeypress='return isPositiveNumber(event)'  class='chargingplatform' min='0' step='0.0' value="+cell9Value+"></input>";
			cell10.innerHTML = "<input type='number' id='cell22Val' onkeypress='return isPositiveNumber(event)'  class='chargingplatform' min='0' step='0.0' value="+cell10Value+"></input>";		
			cell11.innerHTML = "<input type='number' id='cell23Val' onkeypress='return isPositiveNumber(event)'  class='chargingplatform' min='0' step='0.0' value="+cell11Value+"></input>";
			cell12.innerHTML = "<input type='number' id='cell24Val' onkeypress='return isPositiveNumber(event)'  class='chargingplatform' min='0' step='0.0' value="+cell12Value+"></input>";
			cell13.innerHTML = "<input type='number' id='cell25Val' onkeypress='return isPositiveNumber(event)'  class='chargingplatform' min='0' step='0.0' value="+cell13Value+"></input>";
			cell14.innerHTML = "<input type='number' id='cell26Val' onkeypress='return isPositiveNumber(event)'  class='chargingplatform' min='0' step='0.0' value="+cell14Value+"></input>";
			cell15.innerHTML = "<input type='number' id='cell27Val' onkeypress='return isPositiveNumber(event)'  class='chargingplatform' min='0' step='0.0' value="+cell15Value+"></input>";
		}		
		// International Consumer
		if (region === 'INTERNATIONAL' && merchantType === 'CONSUMER') {
			cell4.innerHTML = "<input type='number' id='cell28Val'  onkeypress='return isPositiveNumber(event)' class='chargingplatform' min='0'  step='0.0' value="+cell4Value+"></input>";
			cell5.innerHTML = "<input type='number' id='cell29Val'  onkeypress='return isPositiveNumber(event)' class='chargingplatform' min='0' step='0.0' value="+cell5Value+"></input>";
			cell6.innerHTML = "<input type='number' id='cell30Val'  onkeypress='return isPositiveNumber(event)' class='chargingplatform' min='0' step='0.0' value="+cell6Value+"></input>";
			cell7.innerHTML = "<input type='number' id='cell31Val'  onkeypress='return isPositiveNumber(event)' class='chargingplatform' min='0' step='0.0' value="+cell7Value+"></input>";
			cell8.innerHTML = "<input type='number' id='cell32Val'  onkeypress='return isPositiveNumber(event)' class='chargingplatform' min='0' step='0.0' value="+cell8Value+"></input>";
			cell9.innerHTML = "<input type='number' id='cell33Val'  onkeypress='return isPositiveNumber(event)' class='chargingplatform' min='0' step='0.0' value="+cell9Value+"></input>";
			cell10.innerHTML = "<input type='number' id='cell34Val' onkeypress='return isPositiveNumber(event)' class='chargingplatform' min='0' step='0.0' value="+cell10Value+"></input>";		
			cell11.innerHTML = "<input type='number' id='cell35Val' onkeypress='return isPositiveNumber(event)' class='chargingplatform' min='0' step='0.0' value="+cell11Value+"></input>";
			cell12.innerHTML = "<input type='number' id='cell36Val' onkeypress='return isPositiveNumber(event)' class='chargingplatform' min='0' step='0.0' value="+cell12Value+"></input>";
			cell13.innerHTML = "<input type='number' id='cell37Val' onkeypress='return isPositiveNumber(event)' class='chargingplatform' min='0' step='0.0' value="+cell13Value+"></input>";
			cell14.innerHTML = "<input type='number' id='cell38Val' onkeypress='return isPositiveNumber(event)' class='chargingplatform' min='0' step='0.0' value="+cell14Value+"></input>";
			cell15.innerHTML = "<input type='number' id='cell39Val' onkeypress='return isPositiveNumber(event)' class='chargingplatform' min='0' step='0.0' value="+cell15Value+"></input>";
		}	    
		// International Commercial
		if (region === 'INTERNATIONAL' && merchantType === 'COMMERCIAL') {
			cell4.innerHTML = "<input type='number' id='cell40Val'  onkeypress='return isPositiveNumber(event)' class='chargingplatform' min='0'  step='0.0' value="+cell4Value+"></input>";
			cell5.innerHTML = "<input type='number' id='cell41Val'  onkeypress='return isPositiveNumber(event)' class='chargingplatform' min='0' step='0.0' value="+cell5Value+"></input>";
			cell6.innerHTML = "<input type='number' id='cell42Val'  onkeypress='return isPositiveNumber(event)' class='chargingplatform' min='0' step='0.0' value="+cell6Value+"></input>";
			cell7.innerHTML = "<input type='number' id='cell43Val'  onkeypress='return isPositiveNumber(event)' class='chargingplatform' min='0' step='0.0' value="+cell7Value+"></input>";
			cell8.innerHTML = "<input type='number' id='cell44Val'  onkeypress='return isPositiveNumber(event)' class='chargingplatform' min='0' step='0.0' value="+cell8Value+"></input>";
			cell9.innerHTML = "<input type='number' id='cell45Val'  onkeypress='return isPositiveNumber(event)' class='chargingplatform' min='0' step='0.0' value="+cell9Value+"></input>";
			cell10.innerHTML = "<input type='number' id='cell46Val' onkeypress='return isPositiveNumber(event)' class='chargingplatform' min='0' step='0.0' value="+cell10Value+"></input>";		
			cell11.innerHTML = "<input type='number' id='cell47Val' onkeypress='return isPositiveNumber(event)' class='chargingplatform' min='0' step='0.0' value="+cell11Value+"></input>";
			cell12.innerHTML = "<input type='number' id='cell48Val' onkeypress='return isPositiveNumber(event)' class='chargingplatform' min='0' step='0.0' value="+cell12Value+"></input>";
			cell13.innerHTML = "<input type='number' id='cell49Val' onkeypress='return isPositiveNumber(event)' class='chargingplatform' min='0' step='0.0' value="+cell13Value+"></input>";
			cell14.innerHTML = "<input type='number' id='cell50Val' onkeypress='return isPositiveNumber(event)' class='chargingplatform' min='0' step='0.0' value="+cell14Value+"></input>";
			cell15.innerHTML = "<input type='number' id='cell51Val' onkeypress='return isPositiveNumber(event)' class='chargingplatform' min='0' step='0.0' value="+cell15Value+"></input>";
		}
		cell16.innerHTML = "<input type='text' id='cell52Val' onkeypress='return isPositiveNumber(event)' class='chargingplatform' min='0' step='0.0' value="+cell16Value+" readonly></input>";
		cell17.innerHTML = "<input type='number' id='cell53Val' onkeypress='return isPositiveNumber(event)' class='chargingplatform' min='0' step='0.0' value="+cell17Value+"></input>";
		
		cell18.innerHTML = "";
		if(allowFc){
			cell18.innerHTML = "<input type='checkbox' id='cell54Val' checked='true'></input>";
		}else{
		    cell18.innerHTML = "<input type='checkbox' id='cell54Val'></input>";
		}
		editMode = true;
	}
	else{
		

		var pgTDR, pgTDRAFC, pgFixCharge, pgFixChargeAFC, bankTDR, bankTDRAFC, bankFixCharge, bankFixChargeAFC, merchantTDR, merchantTDRAFC, merchantFixCharge, merchantFixChargeAFC;
		var pgTDRDomComm, pgTDRAFCDomComm, pgFixChargeDomComm, pgFixChargeAFCDomComm, bankTDRDomComm, bankTDRAFCDomComm, bankFixChargeDomComm, bankFixChargeAFCDomComm, merchantTDRDomComm, merchantTDRAFCDomComm, merchantFixChargeDomComm, merchantFixChargeAFCDomComm; 
		var pgTDRIntCons, pgTDRAFCIntCons, pgFixChargeIntCons, pgFixChargeAFCIntCons, bankTDRIntCons, bankTDRAFCIntCons, bankFixChargeIntCons, bankFixChargeAFCIntCons, merchantTDRIntCons, merchantTDRAFCIntCons, merchantFixChargeIntCons, merchantFixChargeAFCIntCons; 
		var pgTDRIntComm, pgTDRAFCIntComm, pgFixChargeIntComm, pgFixChargeAFCIntComm, bankTDRIntComm, bankTDRAFCIntComm, bankFixChargeIntComm, bankFixChargeAFCIntComm, merchantTDRIntComm, merchantTDRAFCIntComm, merchantFixChargeIntComm, merchantFixChargeAFCIntComm;

		//For Domestic Consumer
		if (region == 'DOMESTIC' && merchantType == 'CONSUMER') {
			var pgTDR = parseFloatWithDecimal(document.getElementById('cell4Val').value,2);
			var pgTDRAFC=parseFloatWithDecimal(document.getElementById('cell5Val').value,2);
			var pgFixCharge = parseFloatWithDecimal(document.getElementById('cell6Val').value,2);
			var pgFixChargeAFC =  parseFloatWithDecimal(document.getElementById('cell7Val').value,2);
			var bankTDR = parseFloatWithDecimal(document.getElementById('cell8Val').value,2);
			var bankTDRAFC =parseFloatWithDecimal(document.getElementById('cell9Val').value,2);
			var bankFixCharge = parseFloatWithDecimal(document.getElementById('cell10Val').value,2);
			var bankFixChargeAFC = parseFloatWithDecimal(document.getElementById('cell11Val').value,2);
			var merchantTDR = parseFloatWithDecimal(document.getElementById('cell12Val').value,2);
			var merchantTDRAFC = parseFloatWithDecimal(document.getElementById('cell13Val').value,2);
			var merchantFixCharge = parseFloatWithDecimal(document.getElementById('cell14Val').value,2);
			var merchantFixChargeAFC = parseFloatWithDecimal(document.getElementById('cell15Val').value,2);
		}


		//For Domestic Commercial
		if (region == 'DOMESTIC' && merchantType == 'COMMERCIAL') { 
			var pgTDRDomComm = parseFloatWithDecimal(document.getElementById('cell16Val').value,2);
			var pgTDRAFCDomComm=parseFloatWithDecimal(document.getElementById('cell17Val').value,2);
			var pgFixChargeDomComm = parseFloatWithDecimal(document.getElementById('cell18Val').value,2);
			var pgFixChargeAFCDomComm = parseFloatWithDecimal(document.getElementById('cell19Val').value,2);
			var bankTDRDomComm = parseFloatWithDecimal(document.getElementById('cell20Val').value,2);
			var bankTDRAFCDomComm = parseFloatWithDecimal(document.getElementById('cell21Val').value,2);
			var bankFixChargeDomComm =parseFloatWithDecimal(document.getElementById('cell22Val').value,2);
			var bankFixChargeAFCDomComm = parseFloatWithDecimal(document.getElementById('cell23Val').value,2);
			var merchantTDRDomComm = parseFloatWithDecimal(document.getElementById('cell24Val').value,2);
			var merchantTDRAFCDomComm = parseFloatWithDecimal(document.getElementById('cell25Val').value,2);
			var merchantFixChargeDomComm = parseFloatWithDecimal(document.getElementById('cell26Val').value,2);
			var merchantFixChargeAFCDomComm = parseFloatWithDecimal(document.getElementById('cell27Val').value,2);
		}

		//For International Consumer
		if (region === 'INTERNATIONAL' && merchantType === 'CONSUMER') {
			var pgTDRIntCons = parseFloatWithDecimal(document.getElementById('cell28Val').value,2);
			var pgTDRAFCIntCons =parseFloatWithDecimal(document.getElementById('cell29Val').value,2);
			var pgFixChargeIntCons = parseFloatWithDecimal(document.getElementById('cell30Val').value,2);
			var pgFixChargeAFCIntCons = parseFloatWithDecimal(document.getElementById('cell31Val').value,2);
			var bankTDRIntCons = parseFloatWithDecimal(document.getElementById('cell32Val').value,2);
			var bankTDRAFCIntCons = parseFloatWithDecimal(document.getElementById('cell33Val').value,2);
			var bankFixChargeIntCons = parseFloatWithDecimal(document.getElementById('cell34Val').value,2);
			var bankFixChargeAFCIntCons = parseFloatWithDecimal(document.getElementById('cell35Val').value,2);
			var merchantTDRIntCons = parseFloatWithDecimal(document.getElementById('cell36Val').value,2);
			var merchantTDRAFCIntCons = parseFloatWithDecimal(document.getElementById('cell37Val').value,2);
			var merchantFixChargeIntCons = parseFloatWithDecimal(document.getElementById('cell38Val').value,2);
			var merchantFixChargeAFCIntCons = parseFloatWithDecimal(document.getElementById('cell39Val').value,2);
		}
		
		//For International Commercial
		if (region === 'INTERNATIONAL' && merchantType === 'COMMERCIAL') { 
			var pgTDRIntComm = parseFloatWithDecimal(document.getElementById('cell40Val').value,2);
			var pgTDRAFCIntComm =parseFloatWithDecimal(document.getElementById('cell41Val').value,2);
			var pgFixChargeIntComm = parseFloatWithDecimal(document.getElementById('cell42Val').value,2);
			var pgFixChargeAFCIntComm = parseFloatWithDecimal(document.getElementById('cell43Val').value,2);
			var bankTDRIntComm = parseFloatWithDecimal(document.getElementById('cell44Val').value,2);
			var bankTDRAFCIntComm = parseFloatWithDecimal(document.getElementById('cell45Val').value,2);
			var bankFixChargeIntComm = parseFloatWithDecimal(document.getElementById('cell46Val').value,2);
			var bankFixChargeAFCIntComm = parseFloatWithDecimal(document.getElementById('cell47Val').value,2);
			var merchantTDRIntComm = parseFloatWithDecimal(document.getElementById('cell48Val').value,2);
			var merchantTDRAFCIntComm = parseFloatWithDecimal(document.getElementById('cell49Val').value,2);
			var merchantFixChargeIntComm = parseFloatWithDecimal(document.getElementById('cell50Val').value,2);
			var merchantFixChargeAFCIntComm = parseFloatWithDecimal(document.getElementById('cell51Val').value,2);
		}
		var serviceTax = document.getElementById('cell52Val').value;
		var fixChargeLimit = parseFloatWithDecimal(document.getElementById('cell53Val').value,2);
		
		if (fixChargeLimit == null || fixChargeLimit == '') {
			fixChargeLimit = "0.0";
		}
		
		//validation	
		if(parseFloat(serviceTax) == 0.0)
			{
				alert("Please enter GST before saving TDR");
				return false;
			}

   
		// Validation Disabled temporarily	
		/* 	
		 else if(parseFloat(bankTdr) != 0.0 || parseFloat(merchantTdr) != 0.0 || parseFloat(serviceTax) != 0.0 ){
					if	(((parseFloat(pgTdr) + parseFloat(bankTdr))).toFixed(2) != parseFloat(merchantTdr).toFixed(2))
				{
						alert("Please enter proper TDR values before saving");
						return false;
				}
			}

		  else if(parseFloat(bankTdr) == "" || parseFloat(bankTdrAFC) ||(((parseFloat(pgTdr) + parseFloat(bankTdr))).toFixed(2) < 0) || (((parseFloat(pgTdrAFC) + parseFloat(bankTdrAFC))).toFixed(2) < 0) )
			 {
				alert('Blank / Negative values not allowed for TDR Amount.');
				return false;
			 }
			 
			//Validation For FC 
			else if(((parseFloat(pgFc) + parseFloat(bankFc))).toFixed(2) != parseFloat(merchantFc).toFixed(2))
			{
				alert("Please enter proper TDR values before saving");
				return false;
			}

		  else if(parseFloat(bankFcAFC) ||(((parseFloat(pgFc) + parseFloat(bankFc))).toFixed(2) < 0) || (((parseFloat(pgFcAFC) + parseFloat(bankFcAFC))).toFixed(2) < 0) )
			{
				alert('Blank / Negative values not allowed for TDR Amount.');
				return false;
			}
			*/ 


		// Domestic Consumer	
		if (region == 'DOMESTIC' && merchantType == 'CONSUMER') {
			cell4.innerHTML = pgTDR;
			cell5.innerHTML = pgTDRAFC;
			cell6.innerHTML = pgFixCharge;
			cell7.innerHTML = pgFixChargeAFC;
			cell8.innerHTML = bankTDR;
			cell9.innerHTML = bankTDRAFC;
			cell10.innerHTML = bankFixCharge;
			cell11.innerHTML = bankFixChargeAFC;
			cell12.innerHTML = merchantTDR;
			cell13.innerHTML = merchantTDRAFC;
			cell14.innerHTML = merchantFixCharge;
			cell15.innerHTML = merchantFixChargeAFC;
		}

		// Domestic Commercial
		if (region == 'DOMESTIC' && merchantType == 'COMMERCIAL') {
			cell4.innerHTML = pgTDRDomComm;
			cell5.innerHTML = pgTDRAFCDomComm;
			cell6.innerHTML = pgFixChargeDomComm;
			cell7.innerHTML = pgFixChargeAFCDomComm;
			cell8.innerHTML = bankTDRDomComm;
			cell9.innerHTML = bankTDRAFCDomComm;
			cell10.innerHTML = bankFixChargeDomComm;
			cell11.innerHTML = bankFixChargeAFCDomComm;
			cell12.innerHTML = merchantTDRDomComm;
			cell13.innerHTML = merchantTDRAFCDomComm;
			cell14.innerHTML = merchantFixChargeDomComm;
			cell15.innerHTML = merchantFixChargeAFCDomComm;
		}
		
		// International Consumer
		if (region === 'INTERNATIONAL' && merchantType === 'CONSUMER') {
			cell4.innerHTML = pgTDRIntCons;
			cell5.innerHTML = pgTDRAFCIntCons;
			cell6.innerHTML = pgFixChargeIntCons;
			cell7.innerHTML = pgFixChargeAFCIntCons;
			cell8.innerHTML = bankTDRIntCons;
			cell9.innerHTML = bankTDRAFCIntCons;
			cell10.innerHTML = bankFixChargeIntCons;
			cell11.innerHTML = bankFixChargeAFCIntCons;
			cell12.innerHTML = merchantTDRIntCons;
			cell13.innerHTML = merchantTDRAFCIntCons;
			cell14.innerHTML = merchantFixChargeIntCons;
			cell15.innerHTML = merchantFixChargeAFCIntCons;
		}

		// International Commercial
		if (region === 'INTERNATIONAL' && merchantType === 'COMMERCIAL') {
			cell4.innerHTML = pgTDRIntComm;
			cell5.innerHTML = pgTDRAFCIntComm;
			cell6.innerHTML = pgFixChargeIntComm;
			cell7.innerHTML = pgFixChargeAFCIntComm;
			cell8.innerHTML = bankTDRIntComm;
			cell9.innerHTML = bankTDRAFCIntComm;
			cell10.innerHTML = bankFixChargeIntComm;
			cell11.innerHTML = bankFixChargeAFCIntComm;
			cell12.innerHTML = merchantTDRIntComm;
			cell13.innerHTML = merchantTDRAFCIntComm;
			cell14.innerHTML = merchantFixChargeIntComm;
			cell15.innerHTML = merchantFixChargeAFCIntComm;
		}
		
		cell16.innerHTML = serviceTax;
		cell17.innerHTML = fixChargeLimit;
		editMode = false;
// 		$('#loader-wrapper').show();
		var userType = "<s:property value='%{#session.USER.UserType.name()}'/>";
		var loginUserEmailId = "<s:property value='%{#session.USER.EmailId}'/>";
		ele.value="edit";
		ele.className ="btn btn-primary btn-xs";		
		var token  = document.getElementsByName("token")[0].value;
		$.ajax({
			type: "POST",
			url:"editChargingDetail",
			timeout : 0,
			data:{"id":id,"emailId":merchantId, "acquirer":acquirer, "paymentType":cell0Value, "mopType":cell2Value, "transactionType":cell3Value, "pgTDR":pgTDR,
				   "pgTDRAFC":pgTDRAFC, "pgFixCharge":pgFixCharge, "pgFixChargeAFC":pgFixChargeAFC, "bankTDR":bankTDR, "bankTDRAFC":bankTDRAFC,
				   "bankFixCharge":bankFixCharge, "bankFixChargeAFC":bankFixChargeAFC, "merchantTDR":merchantTDR, "merchantTDRAFC":merchantTDRAFC,
				   "merchantFixCharge":merchantFixCharge,  "merchantFixChargeAFC":merchantFixChargeAFC, "merchantServiceTax":serviceTax,"pgTDRDomComm":pgTDRDomComm,
				   "pgTDRAFCDomComm":pgTDRAFCDomComm, "pgFixChargeDomComm":pgFixChargeDomComm, "pgFixChargeAFCDomComm":pgFixChargeAFCDomComm, "bankTDRDomComm":bankTDRDomComm, "bankTDRAFCDomComm":bankTDRAFCDomComm,
				   "bankFixChargeDomComm":bankFixChargeDomComm, "bankFixChargeAFCDomComm":bankFixChargeAFCDomComm, "merchantTDRDomComm":merchantTDRDomComm, "merchantTDRAFCDomComm":merchantTDRAFCDomComm,
				   "merchantFixChargeDomComm":merchantFixChargeDomComm,  "merchantFixChargeAFCDomComm":merchantFixChargeAFCDomComm,"pgTDRIntCons":pgTDRIntCons,
				   "pgTDRAFCIntCons":pgTDRAFCIntCons, "pgFixChargeIntCons":pgFixChargeIntCons, "pgFixChargeAFCIntCons":pgFixChargeAFCIntCons, "bankTDRIntCons":bankTDRIntCons, "bankTDRAFCIntCons":bankTDRAFCIntCons,
				   "bankFixChargeIntCons":bankFixChargeIntCons, "bankFixChargeAFCIntCons":bankFixChargeAFCIntCons, "merchantTDRIntCons":merchantTDRIntCons, "merchantTDRAFCIntCons":merchantTDRAFCIntCons,
				   "merchantFixChargeIntCons":merchantFixChargeIntCons,  "merchantFixChargeAFCIntCons":merchantFixChargeAFCIntCons,"pgTDRIntComm":pgTDRIntComm,
				   "pgTDRAFCIntComm":pgTDRAFCIntComm, "pgFixChargeIntComm":pgFixChargeIntComm, "pgFixChargeAFCIntComm":pgFixChargeAFCIntComm, "bankTDRIntComm":bankTDRIntComm, "bankTDRAFCIntComm":bankTDRAFCIntComm,
				   "bankFixChargeIntComm":bankFixChargeIntComm, "bankFixChargeAFCIntComm":bankFixChargeAFCIntComm, "merchantTDRIntComm":merchantTDRIntComm, "merchantTDRAFCIntComm":merchantTDRAFCIntComm,
				   "merchantFixChargeIntComm":merchantFixChargeIntComm,  "merchantFixChargeAFCIntComm":merchantFixChargeAFCIntComm, "merchantServiceTax":cell16.innerHTML,"fixChargeLimit":fixChargeLimit,
				   "allowFixCharge":allowFc, "token":token,"struts.token.name": "token",  "currency":cell1Value ,"userType":userType, "loginUserEmailId":loginUserEmailId, "paymentRegion" : region, "cardHolderType" : merchantType},
			success:function(data){
				var response = ((data["Invalid request"] != null) ? (data["Invalid request"].response[0]) : (data.response));
				if(null!=response){
					alert(response);			
				}
				cells[14].innerHTML = data["id"];
				
				//TODO....clean values......using script to avoid page refresh
				//location = window.location
				//location = self.location
				//window.location.reload(true);
// 				$('#loader-wrapper').hide();
		    },
			error:function(data){
				alert("Network error, charging detail may not be saved");
			}
		});
	}
}


function editAllRows(divId,curr_row,ele){
	
	var table = document.getElementById('editAllTable');

	var merchantId = document.getElementById("merchants").value;
	var acquirer = document.getElementById("acquirer").value;
	var rows = table.rows;
	var currentRowNum = Number(curr_row);
	var currentRow = rows[currentRowNum];
	var cells = currentRow.cells;
	var cell0 = cells[0];
	var cell1 = cells[1];
	var cell2 = cells[2];
	var cell3 = cells[3]; //+3 ignoring the first three columns of the table

	var cell4 =  cells[4].children[0];
	var cell5 =  cells[4].children[1];
	var cell6 =  cells[5].children[0];
	var cell7 =  cells[5].children[1];
	var cell8 =  cells[6].children[0];
	var cell9 =  cells[6].children[1];
	var cell10 = cells[7].children[0];
	var cell11 = cells[7].children[1];
	var cell12 = cells[8].children[0];
	var cell13 = cells[8].children[1];

	var cell14 = cells[9].children[0];
	var cell15 = cells[9].children[1];;
	
	var cell16 = cells[10];
	var cell17 = cells[11];
	var cell18 = cells[12];

	var cell0Val = cell0.innerText.trim();
	var cell1Val = cell1.innerText.trim();
	var cell2Val = cell2.innerText.trim();
	var cell3Val = cell3.innerText.trim();
	var cell4Val = cell4.innerText.trim();
	var cell5Val = cell5.innerText.trim();
	var cell6Val = cell6.innerText.trim();
	var cell7Val = cell7.innerText.trim();
	var cell8Val = cell8.innerText.trim();
	var cell9Val = cell9.innerText.trim();
	var cell10Val = cell10.innerText.trim();
	var cell11Val = cell11.innerText.trim();
	var cell12Val = cell12.innerText.trim();
	var cell13Val = cell13.innerText.trim();
	var cell14Val = cell14.innerText.trim();
	var cell15Val = cell15.innerText.trim();
	var cell16Val = cell16.innerText.trim();
	var cell17Val = cell17.innerText.trim();
	
	var cell18Val = cell18.querySelector('input[type=checkbox]').checked;
	var id = cells[14].innerText.trim();

	if(ele.value==="Edit All"){
		if(editMode) 
		{
				alert('Please edit the current row to proceed');
				return;
		}
		ele.value="Save All";
		ele.className ="btn btn-primary btn-xs";
     	cell4.innerHTML = "<input type='number' id='cell4Val'  onkeypress='return isPositiveNumber(event)'   class='chargingplatform'  min='0'  step='0.0' value="+cell4Val+" disabled></input>";
    	cell5.innerHTML = "<input type='number' id='cell5Val'  onkeypress='return isPositiveNumber(event)'  class='chargingplatform'  min='0'  step='0.0' class='chargingplatform' value="+cell5Val+" disabled></input>";
		cell6.innerHTML = "<input type='number' id='cell6Val'  onkeypress='return isPositiveNumber(event)'  class='chargingplatform'  min='0'  step='0.0' value="+cell6Val+" disabled></input>";
		cell7.innerHTML = "<input type='number' id='cell7Val'  onkeypress='return isPositiveNumber(event)'  class='chargingplatform'  min='0'  step='0.0' value="+cell7Val+" disabled></input>";
		cell8.innerHTML = "<input type='number' id='cell8Val'  onkeypress='return isPositiveNumber(event)'  onkeyup='Add()' class='chargingplatform'  min='0'  step='0.0' value="+cell8Val+"></input>";
		cell9.innerHTML = "<input type='number' id='cell9Val'  onkeypress='return isPositiveNumber(event)'  onkeyup='Added()' class='chargingplatform'  min='0'  step='0.0' value="+cell9Val+"></input>";
		cell10.innerHTML = "<input type='number' id='cell10Val' onkeypress='return isPositiveNumber(event)'  onkeyup='FixchargeNB1()' class='chargingplatform'  min='0'  step='0.0' value="+cell10Val+"></input>";		
		cell11.innerHTML = "<input type='number' id='cell11Val'  onkeypress='return isPositiveNumber(event)' onkeyup='FixchargeNB()'class='chargingplatform'  min='0'  step='0.0' value="+cell11Val+"></input>";
		cell12.innerHTML = "<input type='number' id='cell12Val'  onkeypress='return isPositiveNumber(event)' onkeyup='Add()' class='chargingplatform'  min='0'  step='0.0' value="+cell12Val+"></input>";
		cell13.innerHTML = "<input type='number' id='cell13Val' onkeypress='return isPositiveNumber(event)'  onkeyup='Added()' class='chargingplatform'  min='0'  step='0.0' value="+cell13Val+"></input>";
		cell14.innerHTML = "<input type='number' id='cell14Val' onkeypress='return isPositiveNumber(event)'  onkeyup='FixchargeNB1()' class='chargingplatform'  min='0'  step='0.0' value="+cell14Val+"></input>";
		cell15.innerHTML = "<input type='number' id='cell15Val' onkeypress='return isPositiveNumber(event)'  onkeyup='FixchargeNB()' class='chargingplatform'  min='0'  step='0.0' value="+cell15Val+"></input>";
		cell16.innerHTML = "<input type='number' id='cell16Val' onkeypress='return isPositiveNumber(event)' class='chargingplatform'  min='0'  step='0.0' value="+cell16Val+"></input>";
		cell17.innerHTML = "<input type='number' id='cell17Val' onkeypress='return isPositiveNumber(event)' class='chargingplatform'  min='0'  step='0.0' value="+cell17Val+"></input>";

		cell18.innerHTML = "";
		if(cell18Val){
			cell18.innerHTML = "<input type='checkbox' id='cell18Val' checked='true'></input>";
		}else{
		    cell18.innerHTML = "<input type='checkbox' id='cell18Val'></input>";
		}
		editMode = true;
	}
	else{
		
		var txnType = cell3Val;
		//For TDR
		var pgTdr = parseFloatWithDecimal(document.getElementById('cell4Val').value,2);
		var pgTdrOff= parseFloatWithDecimal(document.getElementById('cell5Val').value,2);
		var bankTdr =  parseFloatWithDecimal(document.getElementById('cell8Val').value,2);
		var bankTdrOff =  parseFloatWithDecimal(document.getElementById('cell9Val').value,2);
		var merchantTdr =  parseFloatWithDecimal(document.getElementById('cell12Val').value,2);
		var merchantTdrOff =  parseFloatWithDecimal(document.getElementById('cell13Val').value,2);
		//For FC
		var pgFc =  parseFloatWithDecimal(document.getElementById('cell6Val').value,2);
		var pgFcOff =  parseFloatWithDecimal(document.getElementById('cell7Val').value,2);
		var bankFc =  parseFloatWithDecimal(document.getElementById('cell10Val').value,2);
		var bankFcOff =  parseFloatWithDecimal(document.getElementById('cell11Val').value,2);
		var merchantFc =  parseFloatWithDecimal(document.getElementById('cell14Val').value,2);
		var merchantFcOff =  parseFloatWithDecimal(document.getElementById('cell15Val').value,2);
		var serviceTax = document.getElementById('cell16Val').value;
		var serviceTaxOff =  parseFloatWithDecimal(document.getElementById('cell17Val').value,2);

		if (serviceTaxOff == null || serviceTaxOff == '') {
			document.getElementById('cell17Val').value = "0.0";
		}
		
		//validation
			if(parseFloat(bankTdr) != 0.0 || parseFloat(merchantTdr) != 0.0 || parseFloat(serviceTax) != 0.0 ){
					if	(((parseFloat(pgTdr) + parseFloat(bankTdr))).toFixed(2) != parseFloat(merchantTdr).toFixed(2))
				{
						alert("Please enter proper TDR values before saving");
						return false;
				}
			}
		/* 	else if(parseFloat(bankTdr) != "" || parseFloat(bankTdrOff) ||(((parseFloat(pgTdr) + parseFloat(bankTdr))).toFixed(2) < 0) || (((parseFloat(pgTdrOff) + parseFloat(bankTdrOff))).toFixed(2) < 0) )
				{
					alert('Blank / Negative values not allowed for TDR Amount.');
					return false;
				} */
			
		//Validation For FC 
			else if(parseFloat(bankFc) != 0.0 || parseFloat(merchantFc) != 0.0 || (((parseFloat(pgFc) + parseFloat(bankFc))).toFixed(2) != parseFloat(merchantFc).toFixed(2)))
			{
				alert("Please enter proper TDR values before saving");
				return false;
			}

	/* 	  else if(parseFloat(bankFc) != "" || parseFloat(bankFcOff) ||(((parseFloat(pgFc) + parseFloat(bankFc))).toFixed(2) < 0) || (((parseFloat(pgFcOff) + parseFloat(bankFcOff))).toFixed(2) < 0) )
			{
				alert('Blank / Negative values not allowed for TDR Amount.');
				return false;
			} */
			
		cell4.innerHTML = pgTdr;
		cell5.innerHTML =  pgTdrOff;
		cell6.innerHTML = pgFc;
		cell7.innerHTML = pgFcOff;
		cell8.innerHTML = bankTdr;
		cell9.innerHTML = bankTdrOff;
		cell10.innerHTML = bankFc;
		cell11.innerHTML = bankFcOff;
		cell12.innerHTML = merchantTdr;
		cell13.innerHTML = merchantTdrOff;
		cell14.innerHTML = merchantFc;
		cell15.innerHTML = merchantFcOff;
		cell16.innerHTML = serviceTax;
		cell17.innerHTML =  serviceTaxOff;
		var userType = "<s:property value='%{#session.USER.UserType.name()}'/>";
		editMode = false;
		

		ele.value="Edit All";
		ele.className ="btn btn-primary btn-xs";	
		var token  = document.getElementsByName("token")[0].value;

		$.ajax({
			type: "POST",
			timeout : 0,
			url:"editAllChargingDetail",
			data:{"id":id,"emailId":merchantId, "transactionType":txnType, "acquirer":acquirer, "paymentType":cell0Val, "pgTDR":pgTdr,
				   "pgTDRAFC":pgTdrOff, "pgFixCharge":pgFc, "pgFixChargeAFC":pgFcOff, "bankTDR":bankTdr, "bankTDRAFC":bankTdrOff,
				   "bankFixCharge":bankFc, "bankFixChargeAFC":bankFcOff, "merchantTDR":merchantTdr, "merchantTDRAFC":merchantTdrOff,
				   "merchantFixCharge":merchantFc,  "merchantFixChargeAFC":merchantFcOff, "merchantServiceTax":serviceTax,"fixChargeLimit":serviceTaxOff,
				   "allowFixCharge":cell18Val, "token":token,"struts.token.name": "token",  "currency":cell1Val , "userType":userType},
			success:function(data){
				var response = ((data["Invalid request"] != null) ? (data["Invalid request"].response[0]) : (data.response));
				if(null!=response){
					alert(response);			
				}
				window.location.reload();
		    },
			error:function(data){
				alert("Network error, charging detail may not be saved");
			}
		});
	}
}


function cancel(curr_row,ele){
	var parentEle = ele.parentNode;
	
	if(editMode){
// 		$('#loader-wrapper').show();
	 	window.location.reload();
	}
}

function updateServiceTax(){
	var newValue = document.getElementById('newServiceTaxValue').value;
	var token  = document.getElementsByName("token")[0].value;
	var button = document.getElementById('updateserviceTaxButton');
	
	$.ajax({
		type: "POST",
		timeout : 0,
		url:"updateServiceTax",
		data:{"newServiceTax":newValue, "token":token},
		success:function(data){
			var response = ((data["Invalid request"] != null) ? (data["Invalid request"].response[0]) : (data.response));
			if(null!=response){
				alert(response);			
			}
			$('#popup').popup('hide');
	    },
		error:function(data){
			alert("Network error, charging detail may not be saved");
			$('#popup').popup('hide');
		}
	});
}
</script>
<script>
function Function(){
	//var ab = document.getElementById("cell5Val").value;
    var val= document.getElementById("cell9Val").value;
	 /* if(ab<0){
		  
		  var reseu=1;
		  document.getElementById("cell5Val").value=reseu.toFixed(1);
	  } */
	 if(val<0){
		  
		  var resee=1;
		  document.getElementById("cell9Val").value=resee.toFixed(1);
	  }
	var  p = document.getElementById("cell13Val").value;
	var g = document.getElementById("cell9Val").value;
	  var result1=  parseFloatWithDecimal(p, 2)-parseFloatWithDecimal(g, 2);
  if(!isNaN(result1)){
		  document.getElementById("cell5Val").value =result1.toFixed(2);
}
}
</script>
<script>
function mykey() {
	//var ab = document.getElementById("cell4Val").value;
    var val= document.getElementById("cell8Val").value;
	 /* if(ab<0){
		  var reseu=1;
		  document.getElementById("cell4Val").value=reseu.toFixed(1);
	  } */
	 if(val<0){
		  
		  var resee=1;
		  document.getElementById("cell8Val").value=resee.toFixed(1);
	  }
	  var x = document.getElementById("cell12Val").value;

	  var y = document.getElementById("cell8Val").value;
	  
	  var result= parseFloatWithDecimal(x, 2)-parseFloatWithDecimal(y, 2);
	  if(!isNaN(result)){
		  
		  document.getElementById("cell4Val").value =result.toFixed(2);

	  }
}
</script>
<script>
function Add() {
	//var ab = document.getElementById("cell4Val").value;
    var val= document.getElementById("cell8Val").value;
	/*  if(ab<0){
		  
		  var reseu=1;
		  document.getElementById("cell4Val").value=reseu.toFixed(1);
	  } */
	 if(val<0){
		  
		  var resee=1;
		  document.getElementById("cell8Val").value=resee.toFixed(1);
	  }
	  var x = document.getElementById("cell12Val").value;
	  var y = document.getElementById("cell8Val").value;
	  var result=parseFloatWithDecimal(x, 2)-parseFloatWithDecimal(y, 2);
	  if(!isNaN(result)){
		  document.getElementById("cell4Val").value =result.toFixed(2);
	  }
	}
</script>
<script>
function Added() {
	//var ab = document.getElementById("cell5Val").value;
    var val= document.getElementById("cell9Val").value;
	/*  if(ab<0){
		  
		  var reseu=1;
		  document.getElementById("cell5Val").value=reseu.toFixed(2);
	  } */
	 if(val<0){
		  
		  var resee=1;
		  document.getElementById("cell9Val").value=resee.toFixed(1);
	  }
	  var x = document.getElementById("cell13Val").value;
	  var y = document.getElementById("cell9Val").value;
	  var result=  parseFloatWithDecimal(x, 2)-parseFloatWithDecimal(y, 2);
	  if(!isNaN(result)){
		  
		  document.getElementById("cell5Val").value =result.toFixed(2);
	  }
	}
</script>
<script>
function Fixcharge1() {
	//var ab = document.getElementById("cell6Val").value;
    var val= document.getElementById("cell10Val").value;
	/*  if(ab<0){
		  
		  var reseu=1;
		  document.getElementById("cell6Val").value=reseu.toFixed(1);
	  } */
	 if(val<0){
		  
		  var resee=1;
		  document.getElementById("cell10Val").value=resee.toFixed(1);
	  }
	  var c6 = document.getElementById("cell14Val").value;
	  var c10 = document.getElementById("cell10Val").value;
	  var result=  parseFloatWithDecimal(c6, 2)-parseFloatWithDecimal(c10, 2);
	  if(!isNaN(result)){
		  
		  document.getElementById("cell6Val").value =result.toFixed(2);
	  }
	}
</script>
<script>
function Fixcharge2() {
	//var abc = document.getElementById("cell7Val").value;
    var vall= document.getElementById("cell11Val").value;
	/*  if(abc<0){
		  
		  var reses=1;
		  document.getElementById("cell7Val").value=reses.toFixed(1);
	  } */
	 if(vall<0){
		  
		  var reseul=1;
		  document.getElementById("cell11Val").value=reseul.toFixed(1);
	  }
	  var c7 = document.getElementById("cell15Val").value;
	  var c11 = document.getElementById("cell11Val").value;
	  var result=  parseFloatWithDecimal(c7, 2)-parseFloatWithDecimal(c11, 2);
	  if(!isNaN(result)){
		  
		  document.getElementById("cell7Val").value =result.toFixed(2);
	  }
	}
</script>
<script>
function FixchargeNB1() {
	//var ab = document.getElementById("cell6Val").value;
    var val= document.getElementById("cell10Val").value;
	/*  if(ab<0){
		  
		  var reseu=1;
		  document.getElementById("cell6Val").value=reseu.toFixed(1);
	  } */
	 if(val<0){
		  
		  var resee=1;
		  document.getElementById("cell10Val").value=resee.toFixed(1);
	  }
	  var c6 = document.getElementById("cell14Val").value;
	  var c10 = document.getElementById("cell10Val").value;
	  var result= parseFloatWithDecimal(c6, 2)-parseFloatWithDecimal(c10, 2);
	  if(!isNaN(result)){
		  
		  document.getElementById("cell6Val").value =result.toFixed(2);
		 
		  
	  }
	}
</script>
<script>
function FixchargeNB() {
	//var abc = document.getElementById("cell7Val").value;
    var vall= document.getElementById("cell11Val").value;
	/*  if(abc<0){
		  
		  var reses=1;
		  document.getElementById("cell7Val").value=reses.toFixed(1);
	  } */
	 if(vall<0){
		  
		  var reseul=1;
		  document.getElementById("cell11Val").value=reseul.toFixed(1);
	  }
	  var c7 = document.getElementById("cell15Val").value;
	  var c11 = document.getElementById("cell11Val").value;
	  var result=  parseFloatWithDecimal(c7, 2)-parseFloatWithDecimal(c11, 2);
	  if(!isNaN(result)){
		  
		  document.getElementById("cell7Val").value =result.toFixed(2);
	  }
	}

function parseFloatWithDecimal(str,val) {
    str = str.toString();
    str = str.slice(0, (str.indexOf(".")) + val + 1); 
    return Number(str);   
}


</script>
<style>
.product-spec input[type=text] {
	width: 35px;
}

.btn {
	text-transform: capitalize;
}

tr td div input {
	width: 100%;
}

button
:not
 
(
:disabled
 
),
[
type
=
button
]
:not
 
(
:disabled
 
),
[
type
=
reset
]
:not

	
(
:disabled
 
),
[
type
=
submit
]
:not
 
(
:disabled
 
)
{
width
:
 
100%;
}
.form-control {
	display: block;
	width: 100% !important;
	height: 28px;
	padding: 3px 4px;
	font-size: 14px;
	line-height: 1.42857143;
	color: #555;
	background-color: #fff;
	background-image: none;
	border: 1px solid #ccc;
	border-radius: 4px;
	-webkit-box-shadow: inset 0 1px 1px rgba(0, 0, 0, .075);
	box-shadow: inset 0 1px 1px rgba(0, 0, 0, .075);
	-webkit-transition: border-color ease-in-out .15s, -webkit-box-shadow
		ease-in-out .15s;
	-o-transition: border-color ease-in-out .15s, box-shadow ease-in-out
		.15s;
	transition: border-color ease-in-out .15s, box-shadow ease-in-out .15s;
}
</style>
</head>
<body>

	<div class="content d-flex flex-column flex-column-fluid"
		id="kt_content">
		<div class="toolbar" id="kt_toolbar">
			<!--begin::Container-->
			<div id="kt_toolbar_container"
				class="container-fluid d-flex flex-stack">
				<!--begin::Page title-->
				<div data-kt-swapper="true" data-kt-swapper-mode="prepend"
					data-kt-swapper-parent="{default: '#kt_content_container', 'lg': '#kt_toolbar_container'}"
					class="page-title d-flex align-items-center me-3 flex-wrap mb-5 mb-lg-0 lh-1">
					<!--begin::Title-->
					<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">Charging
						Platform</h1>
					<!--end::Title-->
					<!--begin::Separator-->
					<span class="h-20px border-gray-200 border-start mx-4"></span>
					<!--end::Separator-->
					<!--begin::Breadcrumb-->
					<ul
						class="breadcrumb breadcrumb-separatorless fw-semibold fs-7 my-1">
						<!--begin::Item-->
						<li class="breadcrumb-item text-muted"><a href="home"
							class="text-muted text-hover-primary">Dashboard</a></li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item"><span
							class="bullet bg-gray-200 w-5px h-2px"></span></li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item text-muted">Merchant Billing</li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item"><span
							class="bullet bg-gray-200 w-5px h-2px"></span></li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item text-dark">Charging Platform</li>
						<!--end::Item-->
					</ul>
					<!--end::Breadcrumb-->
				</div>
				<!--end::Page title-->

			</div>
			<!--end::Container-->
		</div>




		<!--     <div id="loader-wrapper" style="width: 100%; height: 100%; display:none;"> -->
		<!-- 	  <div id="loader"></div> -->
		<!--     </div> -->
		<s:actionmessage class="error error-new-text" />
		<div style="overflow: scroll !important;">
			<table width="100%" border="0" cellspacing="0" cellpadding="0">
				<tr>
					<td>
						<div>
							<!-- <input type="button" value="Update Service Tax" id="updateServiceTax" class="btn btn-success btn-md" style="display: inline;margin-top:1%;width:16%;margin-left:1%;font-size: 15px;margin-bottom:1%;"></input> -->
							<div id="popup" style="display: none;">
								<div class="modal-dialog" style="width: 400px;">

									<!-- Modal content-->
									<div class="modal-content"
										style="background-color: transparent; border-radius: 13px; -webkit-box-shadow: 0px 0px 0px 0px; -moz-box-shadow: 0px 0px 0px 0px; box-shadow: 0px 0px 0px 0px; box-shadow: 0px;">
										<div id="1" class="modal-body"
											style="background-color: #ffffff; border-radius: 13px; -webkit-box-shadow: 0px 0px 0px 0px; -moz-box-shadow: 0px 0px 0px 0px; box-shadow: 0px 0px 0px 0px; box-shadow: 0px;">

											<table class="detailbox table98" cellpadding="20"
												class="table table-striped table-row-bordered gy-5 gs-7">
												<tr>
													<td align="left" valign="top">
														<div class="button-position2">
															<button class="popup_close closepopupbtn"></button>
														</div>
													</td>
												</tr>
												<tr>
													<th colspan="2" width="16%" height="30" align="left"
														style="background-color: #496cb6; color: #ffffff; border-top-right-radius: 13px !important;">Update
														Service Tax</th>
												</tr>
												<tr>
													<td width="7%">New Service Tax Value</td>
													<td width="30%"><input id="newServiceTaxValue"
														type="text" placeholder="Value without % symbol"
														maxlength="15" name="ipAddress" value=""
														class="form-control" /></td>
												</tr>
												<tr>
													<td colspan="2"><input type="submit" value="Update"
														id="updateserviceTaxButton" onclick="updateServiceTax()"
														class="btn btn-success btn-sm"
														style="margin-left: 38%; width: 21%; height: 100%; margin-top: 1%;" /></td>
												</tr>

											</table>
										</div>
									</div>
								</div>
							</div>
					</td>
				</tr>
			</table>
			<div class="post d-flex flex-column-fluid" id="kt_post">
				<!--begin::Container-->
				<div id="kt_content_container" class="container-xxl">
					<s:form id="chargingdetailform" action="chargingPlatformAction"
						method="post">

						<table width="100%" border="0" cellspacing="0" cellpadding="0"
							class="table table-striped table-row-bordered gy-5 gs-7" style="margin-top: 1%;">
							<tr>

								<!-- <td width="21%"><h2 style="margin-bottom: 15px;" >Charging Platform</h2></td> -->
							</tr>

							<div class="row my-5">
								<div class="col">
									<div class="card">
										<div class="card-body">
											<!--begin::Input group-->
											<div class="row g-9 mb-8">

												<!-- <div class="col-sm-6 col-lg-3">
								<label style="float: left;">Select User : </label><br>
								<div class="txtnew">
									<s:select headerKey="-1" headerValue="Select User"
									list="#{'1':'Merchant'}" id="user" name="user" value="1"
									class="input-control" autocomplete="off" style="margin-left: -11px;"/>
								</div>
							  </div> -->
												<div class="col-md-3 fv-row">
													<label class="d-flex align-items-center fs-6 fw-bold mb-2">
														<span class="">Select Merchant</span>
													</label>
													<s:select headerValue="Select Merchant" headerKey=""
														name="emailId" class="form-select form-select-solid"
														id="merchants" list="listMerchant" listKey="emailId"
														listValue="businessName" autocomplete="off"
														style="margin-left: -4px;" />

												</div>
												<div class="col-md-3 fv-row" id="acquirerDropdown">
													<label class="d-flex align-items-center fs-6 fw-bold mb-2">
														<span class="">Select Acquirer</span>
													</label>
													<s:select class="form-select form-select-solid"
														list="acquirerList" name="acquirer" id="acquirer"
														autocomplete="off" />

												</div>

												<div class="col-md-3 fv-row">
													<label class="d-flex align-items-center fs-6 fw-bold mb-2">
														<span class="">Select Region</span>
													</label>
													<s:select class="form-select form-select-solid"
														headerKey="" headerValue="Select Region"
														list="@com.pay10.commons.user.AccountCurrencyRegions@values()"
														name="paymentRegion" id="paymentRegion" autocomplete="off" />
												</div>

												<div class="col-md-3 fv-row">
													<label
														class="d-flex align-items-center fs-6 fw-semibold mb-2">
														<span class="">Select Type</span>
													</label>
													<s:select class="form-select form-select-solid"
														headerKey="" headerValue="Select Type"
														list="@com.pay10.commons.user.CardHolderTypes@values()"
														name="cardHolderType" id="cardHolderType"
														autocomplete="off" />
												</div>

											</div>
										</div>
									</div>
								</div>
							</div>
							<tr>

								<td align="center" valign="top">
									<div class="row my-5">
										<div class="col">
											<div class="card">
												<div class="card-body">
													<table width="98%" border="0" cellspacing="0"
														cellpadding="0">

														<tr>

															<td align="left">

																<div id="datatable" class="scrollD">
																	<s:iterator value="aaData" status="pay">
																		<br>
																		<div class=" card-list-toggle">
																			<strong><s:property value="key" /></strong>
																		</div>
																		<s:if test='key.equals("Net Banking")'>
																			<div class="sub-headingCls">Update All</div>
																			<div id="divAll" class="scrollD">
																				<table width="100%" border="0" align="center"
																					class="table table-striped table-row-bordered gy-5 gs-7"
																					id="editAllTable">
																					<tr class="boxheading fw-bold fs-6 text-gray-800">
																						<th width="5%" align="left" valign="middle"
																							style="display: none">Payment</th>
																						<th width="7%" align="left" valign="middle">Currency</th>
																						<th width="4%" align="left" valign="middle">Mop</th>
																						<th width="8%" align="left" valign="middle">Transaction</th>
																						<th width="7%" align="left" valign="middle">PG
																							TDR(%)</th>
																						<th width="6%" align="left" valign="middle">PG
																							FC</th>
																						<th width="7%" align="left" valign="middle">Bank
																							TDR(%)</th>
																						<th width="6%" align="left" valign="middle">Bank
																							FC</th>
																						<th width="9%" align="left" valign="middle">Merchant
																							TDR(%)</th>
																						<th width="5%" align="left" valign="middle">Merchant
																							FC</th>
																						<th width="7%" align="left" valign="middle">Merchant
																							GST</th>
																						<th width="6%" align="left" valign="middle">FC
																							Limit</th>
																						<th width="6%" align="left" valign="middle">Allow
																							FC</th>
																						<th width="5%" align="left" valign="middle">Update</th>
																						<th width="2%" align="left" valign="middle"
																							style="display: none">id</th>
																						<th width="5%" align="left" valign="middle">Cancel</th>
																					</tr>
																					<tr class="boxtext">
																						<td rowspan="2" align="left" valign="middle"
																							style="display: none">NET_BANKING</td>

																						<td width="60" rowspan="2" align="left"
																							valign="middle">356</td>

																						<td width="310" rowspan="2" align="left"
																							valign="middle">ALL BANKS</td>

																						<td width="70" rowspan="2" align="left"
																							valign="middle">SALE</td>

																						<td width="50" align="left" valign="middle">
																							<div title="TDR below fix charge">&nbsp;
																								0.0</div>
																							<div class="cellborder"
																								title="TDR above fix charge">&nbsp; 0.0</div>
																						</td>

																						<td width="32" align="left" valign="middle">
																							<div title="TDR below fix charge">&nbsp;
																								0.0</div>
																							<div class="cellborder"
																								title="TDR above fix charge">&nbsp; 0.0</div>
																						</td>

																						<td width="32" align="left" valign="middle">
																							<div title="TDR below fix charge">&nbsp;
																								0.0</div>
																							<div class="cellborder"
																								title="TDR above fix charge">&nbsp; 0.0</div>
																						</td>

																						<td width="58" align="left" valign="middle">
																							<div title="TDR below fix charge">&nbsp;
																								0.0</div>
																							<div class="cellborder"
																								title="TDR above fix charge">&nbsp; 0.0</div>
																						</td>

																						<td width="55" align="left" valign="middle">
																							<div title="TDR below fix charge">&nbsp;
																								0.0</div>
																							<div class="cellborder"
																								title="TDR above fix charge">&nbsp; 0.0</div>
																						</td>

																						<td width="60" align="left" valign="middle">
																							<div title="TDR below fix charge">&nbsp;
																								0.0</div>
																							<div class="cellborder"
																								title="TDR above fix charge">&nbsp; 0.0</div>
																						</td>

																						<td width="70" rowspan="2" align="left"
																							valign="middle">0.0</td>

																						<td width="30" rowspan="2" align="left"
																							valign="middle">0.0</td>

																						<td width="40" rowspan="2" align="left"
																							valign="middle"><s:checkbox
																								name="allowFixCharge" value="allowFixCharge"
																								onclick="clickOnOff" /></td>

																						<td width="40" rowspan="2" align="left"
																							valign="middle"><div>
																								<s:textfield id="edit" name="editTDR"
																									value="Edit All" type="button"
																									onclick="editAllRows('%{key +'Div'}',1, this)"
																									class="btn btn-primary btn-xs" disabled="true"
																									autocomplete="off"></s:textfield>

																								<s:textfield id="cancelBtn" value="cancel"
																									type="button" onclick="cancel(this)"
																									style="display:none" autocomplete="off"></s:textfield>
																							</div></td>

																						<td rowspan="2" align="left" valign="middle"
																							style="display: none"><s:property value="id" /></td>

																						<td rowspan="2" align="left" valign="middle"><s:textfield
																								id="cancelBtn%{#itStatus.count}" value="cancel"
																								type="button"
																								onclick="cancel('%{#itStatus.count}',this)"
																								class="btn btn-danger btn-xs" autocomplete="off"></s:textfield>
																						</td>
																					</tr>
																				</table>
																			</div>
																			<div class="sub-headingCls">Update individual
																				Bank</div>
																			<!-- For netbanking only -->
																		</s:if>


																		<div class="scrollD">
																			<div id="<s:property value="key" />Div">
																				<table width="100%" border="0" align="center"
																					class="table table-striped table-row-bordered gy-5 gs-7">
																					<tr class="boxheading fw-bold fs-6 text-gray-800">
																						<th width="5%" height="25" valign="middle"
																							style="display: none">Payment</th>
																						<th width="6%" align="left" valign="middle">Currency</th>
																						<th width="5%" align="left" valign="middle">Mop</th>
																						<th width="7%" align="left" valign="middle">Transaction</th>

																						<s:if
																							test='paymentRegion.equals("DOMESTIC") && cardHolderType.equals("CONSUMER")'>
																							<th width="7%" align="left" valign="middle">PG
																								TDR Dom Cons(%)</th>
																							<th width="6%" align="left" valign="middle">PG
																								FC Dom Cons</th>
																							<th width="7%" align="left" valign="middle">Bank
																								TDR Dom Cons(%)</th>
																							<th width="6%" align="left" valign="middle">Bank
																								FC Dom Cons</th>
																							<th width="9%" align="left" valign="middle">Merch
																								TDR Dom Cons(%)</th>
																							<th width="8%" align="left" valign="middle">Merch
																								FC Dom Cons</th>
																						</s:if>
																						<s:if
																							test='paymentRegion.equals("DOMESTIC") && cardHolderType.equals("COMMERCIAL")'>
																							<th width="7%" align="left" valign="middle">PG
																								TDR Dom Comm(%)</th>
																							<th width="6%" align="left" valign="middle">PG
																								FC Dom Comm</th>
																							<th width="7%" align="left" valign="middle">Bank
																								TDR Dom Comm(%)</th>
																							<th width="6%" align="left" valign="middle">Bank
																								FC Dom Comm</th>
																							<th width="9%" align="left" valign="middle">Merch
																								TDR Dom Comm(%)</th>
																							<th width="8%" align="left" valign="middle">Merch
																								FC Dom Comm</th>
																						</s:if>
																						<s:if
																							test='paymentRegion.equals("INTERNATIONAL") && cardHolderType.equals("CONSUMER")'>
																							<th width="7%" align="left" valign="middle">PG
																								TDR Int Cons(%)</th>
																							<th width="6%" align="left" valign="middle">PG
																								FC Int Cons</th>
																							<th width="7%" align="left" valign="middle">Bank
																								TDR Int Cons(%)</th>
																							<th width="6%" align="left" valign="middle">Bank
																								FC Int Cons</th>
																							<th width="9%" align="left" valign="middle">Merch
																								TDR Int Cons(%)</th>
																							<th width="8%" align="left" valign="middle">Merch
																								FC Int Cons</th>
																						</s:if>

																						<s:if
																							test='paymentRegion.equals("INTERNATIONAL") && cardHolderType.equals("COMMERCIAL")'>
																							<th width="7%" align="left" valign="middle">PG
																								TDR Int Comm(%)</th>
																							<th width="6%" align="left" valign="middle">PG
																								FC Int Comm</th>
																							<th width="7%" align="left" valign="middle">Bank
																								TDR Int Comm(%)</th>
																							<th width="6%" align="left" valign="middle">Bank
																								FC Int Comm</th>
																							<th width="9%" align="left" valign="middle">Merch
																								TDR Int Comm(%)</th>
																							<th width="8%" align="left" valign="middle">Merch
																								FC Int Comm</th>
																						</s:if>

																						<th width="10%" align="left" valign="middle">Merchant
																							GST</th>
																						<th width="6%" align="left" valign="middle">FC
																							Limit</th>
																						<th width="6%" align="left" valign="middle">Allow
																							FC</th>
																						<th width="5%" align="left" valign="middle">Update</th>
																						<th width="2%" align="left" valign="middle"
																							style="display: none">id</th>
																						<th width="5%" align="left" valign="middle"><span
																							id="cancelLabel">Cancel</span></th>
																					</tr>
																					<s:iterator value="value" status="itStatus">
																						<tr class="boxtext">
																							<td align="left" valign="middle"
																								style="display: none"><s:property
																									value="paymentType" /></td>
																							<td align="left" valign="middle"><s:property
																									value="currency" /></td>
																							<td align="left" valign="middle"><s:property
																									value="mopType" /></td>
																							<td align="left" valign="middle"><s:property
																									value="transactionType" /></td>

																							<s:if
																								test='paymentRegion.equals("DOMESTIC") && cardHolderType.equals("CONSUMER")'>

																								<td align="left" valign="middle"
																									class="nomarpadng"><div
																										title="PG TDR below fix charge Dom Cons">
																										&nbsp;
																										<s:property value="pgTDR" />
																									</div>
																									<div class="cellborder"
																										title="PG TDR above fix charge Dom Cons">
																										&nbsp;
																										<s:property value="pgTDRAFC" />
																									</div></td>
																								<td align="left" valign="middle"
																									class="nomarpadng"><div
																										title="PG FC below fix charge Dom Cons">
																										&nbsp;
																										<s:property value="pgFixCharge" />
																									</div>

																									<div class="cellborder"
																										title="PG FC above fix charge Dom Cons">
																										&nbsp;
																										<s:property value="pgFixChargeAFC" />
																									</div></td>
																								<td align="left" valign="middle"
																									class="nomarpadng"><div
																										title="Bank TDR below fix charge Dom Cons">
																										&nbsp;
																										<s:property value="bankTDR" />
																									</div>

																									<div class="cellborder"
																										title="Bank TDR above fix charge Dom Cons">
																										&nbsp;
																										<s:property value="bankTDRAFC" />
																									</div></td>
																								<td align="left" valign="middle"
																									class="nomarpadng"><div
																										title="Bank FC below fix charge Dom Cons">
																										&nbsp;
																										<s:property value="bankFixCharge" />
																									</div>

																									<div class="cellborder"
																										title="Bank FC above fix charge Dom Cons">
																										&nbsp;
																										<s:property value="bankFixChargeAFC" />
																									</div></td>
																								<td align="left" valign="middle"
																									class="nomarpadng"><div
																										title="Merch TDR below fix charge Dom Cons">
																										&nbsp;
																										<s:property value="merchantTDR" />
																									</div>

																									<div class="cellborder"
																										title="Merch TDR above fix charge Dom Cons">
																										&nbsp;
																										<s:property value="merchantTDRAFC" />
																									</div></td>
																								<td align="left" valign="middle"
																									class="nomarpadng"><div
																										title="Merch FC below fix charge Dom Cons">
																										&nbsp;
																										<s:property value="merchantFixCharge" />
																									</div>

																									<div class="cellborder"
																										title="Merch FC above fix charge Dom Cons">
																										&nbsp;
																										<s:property value="merchantFixChargeAFC" />
																									</div></td>
																							</s:if>

																							<s:if
																								test='paymentRegion.equals("DOMESTIC") && cardHolderType.equals("COMMERCIAL")'>
																								<td align="left" valign="middle"
																									class="nomarpadng"><div
																										title="PG TDR below fix charge Dom Comm">
																										&nbsp;
																										<s:property value="pgTDRDomComm" />
																									</div>
																									<div class="cellborder"
																										title="PG TDR above fix charge Dom Comm">
																										&nbsp;
																										<s:property value="pgTDRAFCDomComm" />
																									</div></td>
																								<td align="left" valign="middle"
																									class="nomarpadng"><div
																										title="PG FC below fix charge Dom Comm">
																										&nbsp;
																										<s:property value="pgFixChargeDomComm" />
																									</div>

																									<div class="cellborder"
																										title="PG FC above fix charge Dom Comm">
																										&nbsp;
																										<s:property value="pgFixChargeAFCDomComm" />
																									</div></td>
																								<td align="left" valign="middle"
																									class="nomarpadng"><div
																										title="Bank TDR below fix charge Dom Comm">
																										&nbsp;
																										<s:property value="bankTDRDomComm" />
																									</div>

																									<div class="cellborder"
																										title="Bank TDR above fix charge Dom Comm">
																										&nbsp;
																										<s:property value="bankTDRAFCDomComm" />
																									</div></td>
																								<td align="left" valign="middle"
																									class="nomarpadng"><div
																										title="Bank FC below fix charge Dom Comm">
																										&nbsp;
																										<s:property value="bankFixChargeDomComm" />
																									</div>

																									<div class="cellborder"
																										title="Bank FC above fix charge Dom Comm">
																										&nbsp;
																										<s:property value="bankFixChargeAFCDomComm" />
																									</div></td>
																								<td align="left" valign="middle"
																									class="nomarpadng"><div
																										title="Merch TDR below fix charge Dom Comm">
																										&nbsp;
																										<s:property value="merchantTDRDomComm" />
																									</div>

																									<div class="cellborder"
																										title="Merch TDR above fix charge Dom Comm">
																										&nbsp;
																										<s:property value="merchantTDRAFCDomComm" />
																									</div></td>
																								<td align="left" valign="middle"
																									class="nomarpadng"><div
																										title="Merch FC below fix charge Dom Comm">
																										&nbsp;
																										<s:property value="merchantFixChargeDomComm" />
																									</div>

																									<div class="cellborder"
																										title="Merch FC above fix charge Dom Comm">
																										&nbsp;
																										<s:property
																											value="merchantFixChargeAFCDomComm" />
																									</div></td>
																							</s:if>

																							<s:if
																								test='paymentRegion.equals("INTERNATIONAL") && cardHolderType.equals("CONSUMER")'>
																								<td align="left" valign="middle"
																									class="nomarpadng"><div
																										title="PG TDR below fix charge Int Cons">
																										&nbsp;
																										<s:property value="pgTDRIntCons" />
																									</div>
																									<div class="cellborder"
																										title="PG TDR above fix charge Int Cons">
																										&nbsp;
																										<s:property value="pgTDRAFCIntCons" />
																									</div></td>
																								<td align="left" valign="middle"
																									class="nomarpadng"><div
																										title="PG FC below fix charge Int Cons">
																										&nbsp;
																										<s:property value="pgFixChargeIntCons" />
																									</div>

																									<div class="cellborder"
																										title="PG FC above fix charge Int Cons">
																										&nbsp;
																										<s:property value="pgFixChargeAFCIntCons" />
																									</div></td>
																								<td align="left" valign="middle"
																									class="nomarpadng"><div
																										title="Bank TDR below fix charge Int Cons">
																										&nbsp;
																										<s:property value="bankTDRIntCons" />
																									</div>

																									<div class="cellborder"
																										title="Bank TDR above fix charge Int Cons">
																										&nbsp;
																										<s:property value="bankTDRAFCIntCons" />
																									</div></td>
																								<td align="left" valign="middle"
																									class="nomarpadng"><div
																										title="Bank FC below fix charge Int Cons">
																										&nbsp;
																										<s:property value="bankFixChargeIntCons" />
																									</div>

																									<div class="cellborder"
																										title="Bank FC above fix charge Int Cons">
																										&nbsp;
																										<s:property value="bankFixChargeAFCIntCons" />
																									</div></td>
																								<td align="left" valign="middle"
																									class="nomarpadng"><div
																										title="Merch TDR below fix charge Int Cons">
																										&nbsp;
																										<s:property value="merchantTDRIntCons" />
																									</div>

																									<div class="cellborder"
																										title="Merch TDR above fix charge Int Cons">
																										&nbsp;
																										<s:property value="merchantTDRAFCIntCons" />
																									</div></td>
																								<td align="left" valign="middle"
																									class="nomarpadng"><div
																										title="Merch FC below fix charge Int Cons">
																										&nbsp;
																										<s:property value="merchantFixChargeIntCons" />
																									</div>

																									<div class="cellborder"
																										title="Merch FC above fix charge Int Cons">
																										&nbsp;
																										<s:property
																											value="merchantFixChargeAFCIntCons" />
																									</div></td>
																							</s:if>

																							<s:if
																								test='paymentRegion.equals("INTERNATIONAL") && cardHolderType.equals("COMMERCIAL")'>
																								<td align="left" valign="middle"
																									class="nomarpadng"><div
																										title="PG TDR below fix charge Int Comm">
																										&nbsp;
																										<s:property value="pgTDRIntComm" />
																									</div>
																									<div class="cellborder"
																										title="PG TDR above fix charge Int Comm">
																										&nbsp;
																										<s:property value="pgTDRAFCIntComm" />
																									</div></td>
																								<td align="left" valign="middle"
																									class="nomarpadng"><div
																										title="PG FC below fix charge Int Comm">
																										&nbsp;
																										<s:property value="pgFixChargeIntComm" />
																									</div>

																									<div class="cellborder"
																										title="PG FC above fix charge Int Comm">
																										&nbsp;
																										<s:property value="pgFixChargeAFCIntComm" />
																									</div></td>
																								<td align="left" valign="middle"
																									class="nomarpadng"><div
																										title="Bank TDR below fix charge Int Comm">
																										&nbsp;
																										<s:property value="bankTDRIntComm" />
																									</div>

																									<div class="cellborder"
																										title="Bank TDR above fix charge">
																										&nbsp;
																										<s:property value="bankTDRAFCIntComm" />
																									</div></td>
																								<td align="left" valign="middle"
																									class="nomarpadng"><div
																										title="Bank FC below fix charge Int Comm">
																										&nbsp;
																										<s:property value="bankFixChargeIntComm" />
																									</div>

																									<div class="cellborder"
																										title="Bank FC above fix charge Int Comm">
																										&nbsp;
																										<s:property value="bankFixChargeAFCIntComm" />
																									</div></td>
																								<td align="left" valign="middle"
																									class="nomarpadng"><div
																										title="Merch TDR below fix charge Int Comm">
																										&nbsp;
																										<s:property value="merchantTDRIntComm" />
																									</div>

																									<div class="cellborder"
																										title="Merch TDR above fix charge Int Comm">
																										&nbsp;
																										<s:property value="merchantTDRAFCIntComm" />
																									</div></td>
																								<td align="left" valign="middle"
																									class="nomarpadng"><div
																										title="Merch FC below fix charge Int Comm">
																										&nbsp;
																										<s:property value="merchantFixChargeIntComm" />
																									</div>

																									<div class="cellborder"
																										title="Merch FC above fix charge Int Comm">
																										&nbsp;
																										<s:property
																											value="merchantFixChargeAFCIntComm" />
																									</div></td>
																							</s:if>
																							<td align="center" valign="middle"><div>
																									<s:property value="merchantServiceTax" />
																								</div></td>
																							<td align="center" valign="middle"><div>
																									<s:property value="fixChargeLimit" />
																								</div></td>
																							<td align="center" valign="middle"><s:checkbox
																									name="allowFixCharge" value="allowFixCharge"
																									onclick="return false" /></td>
																							<td align="center" valign="middle"><div>
																									<s:textfield id="edit%{#itStatus.count}"
																										name="editTDR" value="edit" type="button"
																										disabled="true"
																										onclick="editCurrentRow('%{key +'Div'}','%{#itStatus.count}', this)"
																										class="btn btn-primary" autocomplete="off"></s:textfield>

																									<s:textfield id="cancelBtn%{#itStatus.count}"
																										value="cancel" type="button"
																										onclick="cancel('%{#itStatus.count}',this)"
																										style="display:none" autocomplete="off"></s:textfield>
																								</div></td>
																							<td align="center" valign="middle"
																								style="display: none"><s:property
																									value="id" /></td>
																							<td align="center" valign="middle"><s:textfield
																									id="cancelBtn%{#itStatus.count}" value="cancel"
																									type="button"
																									onclick="cancel('%{#itStatus.count}',this)"
																									class="btn btn-danger btn-xs"
																									autocomplete="off"></s:textfield></td>
																						</tr>
																					</s:iterator>
																				</table>
																			</div>
																		</div>
																	</s:iterator>
																</div>
																</div>
																</div>
																</div>
																</div>
															</td>
														</tr>
													</table>
												</div>
											</div>
										</div>
									</div>

								</td>
							</tr>
						</table>
				</div>



				<s:hidden name="token" value="%{#session.customToken}"></s:hidden>
				</s:form>
			</div>
		</div>

	</div>
	<script type="text/javascript">
	$(document).ready(function() {
		if (window.location.href.includes("chargingPlatformAction")) {
			var menuAccess = document.getElementById("menuAccessByROLE").value;
			var accessMap = JSON.parse(menuAccess);
			var access = accessMap["chargingPlatform"];
			if (access.includes("Add") || access.includes("Update")) {
				var editBtns = document.getElementsByName("editTDR");
				for (var i=0; i<editBtns.length; i++) {
					var editBtn = editBtns[i];
					editBtn.disabled=false;
				}
			}
		}
	});

	function isPositiveNumber(evt) {
		var charCode = (evt.which) ? evt.which : event.keyCode
		if (charCode > 31 && (charCode < 48 || charCode > 57) && charCode !=46) {
			return false;
		}
		return true;
	}
	
</script>
</body>
</html>