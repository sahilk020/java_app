<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="/struts-tags" prefix="s"%>
<html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Router Configuration Platform</title>
<!-- <link href="../css/Jquerydatatableview.css" rel="stylesheet" />
<link href="../css/jquery-ui.css" rel="stylesheet" />
<link href="../css/Jquerydatatable.css" rel="stylesheet" />
<link href="../css/default.css" rel="stylesheet" type="text/css" /> -->
<!-- <script type="text/javascript" src="../js/jquery.min.js"></script>
<script src="../js/core/popper.min.js"></script>
<script src="../js/core/bootstrap-material-design.min.js"></script>
<script src="../js/plugins/perfect-scrollbar.jquery.min.js"></script> -->
<!-- Control Center for Material Dashboard: parallax effects, scripts for the example pages etc -->
<!-- <script src="../js/material-dashboard.js?v=2.1.0" type="text/javascript"></script>
<script src="../js/jquery.popupoverlay.js"></script> -->
<!-- <link rel="stylesheet" type="text/css" href="../css/popup.css" />
<link href="../css/bootstrap-toggle.min.css" rel="stylesheet"> -->
<!-- <script src="../js/bootstrap-toggle.min.js"></script> -->
<!-- <link href="../css/select2.min.css" rel="stylesheet" /> -->
<!-- <script src="../js/jquery.select2.js" type="text/javascript"></script> -->
<!--  loader scripts -->
<!-- <script src="../js/loader/modernizr-2.6.2.min.js"></script>
<script src="../js/loader/main.js"></script> -->
<!-- <link rel="stylesheet" href="../css/loader/normalize.css" />
<link rel="stylesheet" href="../css/loader/main.css" />
<link rel="stylesheet" href="../css/loader/customLoader.css" /> -->

<!-- <link href="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.css" rel="stylesheet" type="text/css" />
<link href="../assets/plugins/custom/datatables/datatables.bundle.css" rel="stylesheet" type="text/css" />
<link href="../assets/plugins/global/plugins.bundle.css" rel="stylesheet" type="text/css" />
<link href="../assets/css/style.bundle.css" rel="stylesheet" type="text/css" />
<script src="../assets/plugins/global/plugins.bundle.js"></script>
<script src="../assets/js/scripts.bundle.js"></script> -->


<link
	href="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.css"
	rel="stylesheet" type="text/css" />
<link href="../assets/plugins/custom/datatables/datatables.bundle.css"
	rel="stylesheet" type="text/css" />
<link href="../assets/plugins/global/plugins.bundle.css"
	rel="stylesheet" type="text/css" />
<link href="../assets/css/style.bundle.css" rel="stylesheet"
	type="text/css" />
<link href="../assets/css/custom/custom-style.css" rel="stylesheet"
	type="text/css" />
<!-- <script src="../js/loader/main.js"></script> -->
<script src="../assets/plugins/global/plugins.bundle.js"></script>
<script src="../assets/js/scripts.bundle.js"></script>
<script
	src="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.js"></script>
<script src="../assets/plugins/custom/datatables/datatables.bundle.js"></script>
<script src="../assets/js/widgets.bundle.js"></script>
<script src="../assets/js/custom/widgets.js"></script>
<script src="../assets/js/custom/apps/chat/chat.js"></script>
<script src="../assets/js/custom/utilities/modals/upgrade-plan.js"></script>
<script src="../assets/js/custom/utilities/modals/create-app.js"></script>
<script src="../assets/js/custom/utilities/modals/users-search.js"></script>
<script src="../js/commanValidate.js"></script>


<style type="text/css">
</style>

<style>
.switch {
	position: relative;
	display: inline-block;
	width: 30px;
	height: 17px;
}

.switch input {
	display: none;
}

.slider {
	position: absolute;
	cursor: pointer;
	top: 0;
	left: 0;
	right: 0;
	bottom: 0;
	background-color: #ccc;
	-webkit-transition: .4s;
	transition: .4s;
}

.slider:before {
	position: absolute;
	content: "";
	height: 13px;
	width: 13px;
	left: 2px;
	bottom: 2px;
	background-color: white;
	-webkit-transition: .4s;
	transition: .4s;
}

input:checked+.slider {
	background-color: #2196F3;
}

input:focus+.slider {
	box-shadow: 0 0 1px #2196F3;
}

input:checked+.slider:before {
	-webkit-transform: translateX(13px);
	-ms-transform: translateX(13px);
	transform: translateX(13px);
}

/* Rounded sliders */
.slider.round {
	border-radius: 17px;
}

.slider.round:before {
	border-radius: 50%;
}

.mycheckbox {
	/* Your style here */
	
}

.em1-0 {
	font-size: 1em;
}

.em4-0 {
	font-size: 4em;
}

.switch {
	display: table-cell;
	vertical-align: middle;
	padding: 10px;
}

.cmn-toggle {
	position: absolute;
	margin-left: -9999px;
	visibility: hidden;
}

.cmn-toggle+label {
	display: block;
	position: relative;
	cursor: pointer;
	outline: none;
	-webkit-user-select: none;
	-moz-user-select: none;
	-ms-user-select: none;
	user-select: none;
}

input.cmn-toggle-jwr+label {
	width: 3em;
	height: 1.5em;
	background-color: #dddddd;
	-webkit-border-radius: 1.5em;
	-moz-border-radius: 1.5em;
	-ms-border-radius: 1.5em;
	-o-border-radius: 1.5em;
	border-radius: 1.5em;
	overflow: hidden;
}

input.cmn-toggle-jwr+label:before, input.cmn-toggle-jwr+label:after {
	display: block;
	position: absolute;
	top: 1px;
	left: 1px;
	bottom: 1px;
	content: "";
}

input.cmn-toggle-jwr+label:before {
	right: 1px;
	background-color: #f1f1f1;
	-webkit-border-radius: 1.5em;
	-moz-border-radius: 1.5em;
	-ms-border-radius: 1.5em;
	-o-border-radius: 1.5em;
	border-radius: 1.5em;
	-webkit-transition: background 0.3s;
	-moz-transition: background 0.3s;
	-o-transition: background 0.3s;
	transition: background 0.3s;
	font-size: 0.75em;
	content: "off";
	text-align: right;
	padding: 0.25em 0.35em;
}

input.cmn-toggle-jwr+label:after {
	width: 1.5em;
	width: calc(1.5em - 2px);
	background-color: #fff;
	-webkit-border-radius: 100%;
	-moz-border-radius: 100%;
	-ms-border-radius: 100%;
	-o-border-radius: 100%;
	border-radius: 100%;
	-webkit-box-shadow: 1px 0 3px rgba(0, 0, 0, 0.1);
	-moz-box-shadow: 1px 0 3px rgba(0, 0, 0, 0.1);
	box-shadow: 1px 0 3px rgba(0, 0, 0, 0.1);
	-webkit-transition: margin 0.3s;
	-moz-transition: margin 0.3s;
	-o-transition: margin 0.3s;
	transition: margin 0.3s;
}

input.cmn-toggle-jwr:checked+label:before {
	background-color: #8ce196;
	content: "on";
	text-align: left;
}

input.cmn-toggle-jwr:checked+label:after {
	margin-left: 1.5em;
}

table .toggle.btn {
	min-width: 48px;
	min-height: 28px;
}

table .btn {
	/* margin-bottom: 4px; */
	/* margin-right: 5px; */
	/* padding: 1px 12px;
    font-size: 11px; */
	
}

table .toggle-off.btn {
	padding: 0;
	margin: 0;
}

/* label{
	font-size: 14px;
    font-weight: 500;
    color: grey;
}
.col-sm-6.col-lg-3{
	margin-bottom:10px;
} */
.disabled {
	cursor: not-allowed;
	color: #fff;
	border-color: #a0a0a0;
	background-color: #a0a0a0;
}

#loading {
	width: 100%;
	height: 100%;
	top: 0px;
	left: 0px;
	position: fixed;
	display: block;
	z-index: 99
}

#loading-image {
	position: absolute;
	top: 40%;
	left: 55%;
	z-index: 100;
	width: 10%;
}

.btn {
	margin: 0px 2px 0px 2px !important;
	position: unset !important;
	border: 1px solid transparent !important;
	padding: 0px 10px !important;
	font-size: 1rem !important;
	line-height: 1.5 !important;
	transition: color 0.15s ease-in-out, background-color 0.15s ease-in-out,
		border-color 0.15s ease-in-out, box-shadow 0.15s ease-in-out
		!important;
}
</style>

<script type="text/javascript">
			function sendId(id){
				//$(""+id+"")[19].children[0].children[0].children[0].checked;
			var aa=	$('#'+id+'Div  table tbody td')[19].children[0].children[0].children[4].checked;;
			
			if(aa){
				
			
				var data=$('#'+id+'Div table tr').length;
				
				for(var i=0 ; i<data;i++){
					$('#'+id+'Div  table tbody tr')[i].children[16].style.display="table-cell";
					$('#'+id+'Div  table tbody tr')[i].children[17].style.display="table-cell";

				}
				

			
			
			}
			else{
				
				var a=$('#'+id+'Div table tbody th')[16].children[0];	
				var b=$('#'+id+'Div table tbody th')[17].children[0];
				var c=$('#'+id+'Div table tbody td')[19].children[0].children[0];
				var data=$('#'+id+'Div table tr').length;
				
				for(var i=0; i<data;i++){
					$('#'+id+'Div  table tbody tr')[i].children[16].style.display="none";
					$('#'+id+'Div  table tbody tr')[i].children[17].style.display="none";

				}
				
			}
			
			}
				$(document).ready(function () {
					//document.getElementById("loading").style.display = "none"
					// Initialize select2
					$("#merchantName").select2();
					$("#paymentMethods").select2();
					$("#cardHolderType").select2();
					$("#currencyMethods").select2();

				});
				$(window).on('load', function() {
					var paymentMethodValue= document.getElementById("paymentMethods").value;
					if(paymentMethodValue!='UP'){
						debugger
						$('.vpaCountHeader').hide();
						$('.vpaCount').hide();
					}
					else{
						$('.vpaCountHeader').show();
						$('.vpaCount').show();	
					}
					})
			</script>

<script type="text/javascript">
				$(document).ready(function () {

					$('#Download').prop('disabled', true).addClass('disabled');

					$('.card-list-toggle').on('click', function () {
						$(this).toggleClass('active');
						$(this).next('.card-list').slideToggle();
					});

					$('#cardHolderType').change(function (event) {

						var val1 = document.getElementById("merchantName");
						var merchantName = val1.options[val1.selectedIndex].text;

						var val2 = document.getElementById("paymentMethods");
						var paymentMethod = val2.options[val2.selectedIndex].text;

						var val3 = document.getElementById("cardHolderType");
						var cardHolderType = val3.options[val3.selectedIndex].text;
						
						var val4 = document.getElementById("currencyMethods");
						var currency = val4.options[val4.selectedIndex].text;

						if (merchantName != "Select Merchant" && paymentMethod != "Select Payment Type" && cardHolderType != "Select Payment region" && currency != "Select Currency") {

							$('#Download').prop('disabled', false).removeClass('disabled');
							event.stopPropagation();
							return false;
						}
						else {

							$('#Download').prop('disabled', true).addClass('disabled');
							event.stopPropagation();
							return false;
						}


					});
					
					$('#paymentMethods').change(function (event) {

						var val1 = document.getElementById("merchantName");
						var merchantName = val1.options[val1.selectedIndex].text;

						var val2 = document.getElementById("paymentMethods");
						var paymentMethod = val2.options[val2.selectedIndex].text;
 						
						var val3 = document.getElementById("cardHolderType");
						var cardHolderType = val3.options[val3.selectedIndex].text;
						
						var val4 = document.getElementById("currencyMethods");
						var currency = val4.options[val4.selectedIndex].text;

						if (merchantName != "Select Merchant" && paymentMethod != "Select Payment Type" && cardHolderType != "Select Payment region"  && currency != "Select Currency") {

							$('#Download').prop('disabled', false).removeClass('disabled');
							event.stopPropagation();
							return false;
						}
						else {

							$('#Download').prop('disabled', true).addClass('disabled');
							event.stopPropagation();
							return false;
						}
					});

					$('#merchantName').change(function (event) {

						var val1 = document.getElementById("merchantName");
						var merchantName = val1.options[val1.selectedIndex].text;

						var val2 = document.getElementById("paymentMethods");
						var paymentMethod = val2.options[val2.selectedIndex].text;

						var val3 = document.getElementById("cardHolderType");
						var cardHolderType = val3.options[val3.selectedIndex].text;

						var val4 = document.getElementById("currencyMethods");
						var currency = val4.options[val4.selectedIndex].text;

						if (merchantName != "Select Merchant" && paymentMethod != "Select Payment Type" && cardHolderType != "Select Payment region"  && currency != "Select Currency") {

							$('#Download').prop('disabled', false).removeClass('disabled');
							event.stopPropagation();
							return false;
						}
						else {

							$('#Download').prop('disabled', true).addClass('disabled');
							event.stopPropagation();
							return false;
						}
					});

					$('#currencyMethods').change(function (event) {

						var val1 = document.getElementById("merchantName");
						var merchantName = val1.options[val1.selectedIndex].text;

						var val2 = document.getElementById("paymentMethods");
						var paymentMethod = val2.options[val2.selectedIndex].text;

						var val3 = document.getElementById("cardHolderType");
						var cardHolderType = val3.options[val3.selectedIndex].text;

						var val4 = document.getElementById("currencyMethods");
						var currency = val4.options[val4.selectedIndex].text;

						if (merchantName != "Select Merchant" && paymentMethod != "Select Payment Type" && cardHolderType != "Select Payment region" && currency != "Select Currency") {

							$('#Download').prop('disabled', false).removeClass('disabled');
							event.stopPropagation();
							return false;
						}
						else {

							$('#Download').prop('disabled', true).addClass('disabled');
							event.stopPropagation();
							return false;
						}
					});

					var cancelButton = document.getElementById("cancelBtn1");
					if (cancelButton != null) {
						cancelButton.disabled = true;
					}


					var btnArray = document.getElementsByName("cancelBtn");
					//cancelBtnArray;
					for (var i = 0; i < btnArray.length; i++) {
						//if ((btnArray[i].id).indexof('cancelBtn') !== -1){
						var cancelBtnCurrent = btnArray[i];
						cancelBtnCurrent.disabled = true;
						//}
					}

				});

				var editMode;

				function saveDetails(val) {
					debugger
					//document.getElementById("saveBtnFirst").disabled =true;
					//document.getElementById("defaultBtnSecnd").disabled = true;
					//document.getElementById("loading").style.display = "block"
					var routerType=null;
					var tet = val.parentNode.children[0].id;
					var identifier = tet.slice(0, -3);

					var loadCheck = "0";
					var rowLength = val.parentNode.children[0].children[0].rows.length;
					var cellLength = val.parentNode.children[0].children[0].rows[1].cells.length;
					var rowData = "";
					for (var i = 1; i < rowLength; i++) {

						var acquirer = val.parentNode.children[0].children[0].rows[i].cells[1].innerText;
						var status = val.parentNode.children[0].children[0].rows[i].cells[2].children[0].children[0].checked;
						var description = val.parentNode.children[0].children[0].rows[i].cells[3].innerText;
						var currency = val.parentNode.children[0].children[0].rows[i].cells[4].innerText;
						var mode = 'MANUAL';
						var paymentType = val.parentNode.children[0].children[0].rows[i].cells[6].innerText;
// 						var mopType = val.parentNode.children[0].children[0].rows[i].cells[7].innerText;
						var allowedFailureCount = val.parentNode.children[0].children[0].rows[i].cells[8].children[0].children[0].children[0].value;
						//var alwaysOn = val.parentNode.children[0].children[0].rows[i].cells[8].children[0].children[0].children[0].children[0].checked;
						var alwaysOn = true;
						var loadPercentage = val.parentNode.children[0].children[0].rows[i].cells[9].children[0].children[0].children[0].value;
						//var priority = val.parentNode.children[0].children[0].rows[i].cells[10].innerText;
                        
						var priority = val.parentNode.children[0].children[0].rows[i].cells[10].children[0].children[0].children[0].value;
						var failedCount = val.parentNode.children[0].children[0].rows[i].cells[11].children[0].children[0].children[0].value;
						var retryMinutes = val.parentNode.children[0].children[0].rows[i].cells[12].children[0].children[0].children[0].value;
                        
						var minAmount = val.parentNode.children[0].children[0].rows[i].cells[13].children[0].children[0].children[0].value;
                        
						var maxAmount = val.parentNode.children[0].children[0].rows[i].cells[14].children[0].children[0].children[0].value;
                       
						var totalTxn = val.parentNode.children[0].children[0].rows[i].cells[15].children[0].children[0].children[0].value;
                       
                        

					
						
					var minAmountRout;
					var maxAmountRout
					
					if(routerType==null){
						 
						if(val.parentNode.children[0].children[0].rows[i].cells[20].children[0].children[0].children[0].value==null||val.parentNode.children[0].children[0].rows[i].cells[21].children[0].children[0].children[0].value==""){
							 routerType = val.parentNode.children[0].children[0].rows[i].cells[21].children[0].children[0].children[0].value;
							
	
						}
						else{
							 routerType = val.parentNode.children[0].children[0].rows[i].cells[22].children[0].children[0].children[0].value;
							 

						}
					}
					
					
					if(routerType=="AmountBase"){
						
					if(i==1){
						
						 minAmountRout = val.parentNode.children[0].children[0].rows[i].cells[16].children[0].children[0].children[0].value;
						 maxAmountRout = val.parentNode.children[0].children[0].rows[i].cells[17].children[0].children[0].children[0].value;
						
						 if(maxAmountRout==null|| maxAmountRout==""||maxAmountRout==0){
alert("please enter the TxnMixAmount")
							 return false;
						 }
						
							
							if(minAmountRout==null|| minAmountRout==""){
								alert("please enter the TxnMinAmount")
								 return false;								}
							 if (Number(minAmountRout)>Number(maxAmountRout)){
								 alert("Max Amount should be Greater than MinAmount")
								 								 return false;
								 							 }
					
					}
						else{
							for(j=1;j<i;j++){
								compareMax = val.parentNode.children[0].children[0].rows[j].cells[17].children[0].children[0].children[0].value;
								
								
								 compareMin = val.parentNode.children[0].children[0].rows[j].cells[16].children[0].children[0].children[0].value;
								
								 var boolmin=between(val.parentNode.children[0].children[0].rows[i].cells[16].children[0].children[0].children[0].value, compareMin, compareMax)
								
								 var boolmax=between(val.parentNode.children[0].children[0].rows[i].cells[17].children[0].children[0].children[0].value, compareMin, compareMax)
							
							var min =val.parentNode.children[0].children[0].rows[i].cells[16].children[0].children[0].children[0].value;
								
										 
							var max =val.parentNode.children[0].children[0].rows[i].cells[17].children[0].children[0].children[0].value;
								 
								 var boolmindata=between( compareMin, min,max);
								 var boolmaxdata=between( compareMax,min,max);

								 
								 if(boolmin||boolmax||boolmindata||boolmaxdata){
							alert("please put the proper amount range"+minAmountRout +" between "+maxAmountRout);
 
								return false;	
								 }

							}
							 minAmountRout = val.parentNode.children[0].children[0].rows[i].cells[16].children[0].children[0].children[0].value;
							 maxAmountRout = val.parentNode.children[0].children[0].rows[i].cells[17].children[0].children[0].children[0].value;	
							 if(maxAmountRout==null|| maxAmountRout==""||maxAmountRout==0){
								 alert("please enter the TxnMixAmount")
								 return false;
								}
								 								
								if(minAmountRout==null|| minAmountRout==""){
								 	alert("please enter the TxnMinAmount")
								 return false;								}	
								
								if (Number(minAmountRout)>Number(maxAmountRout)){
									 alert("Max Amount should be Greater than MinAmount")
									 								 return false;
									 							 }
							 
						}}else{
							 minAmountRout = val.parentNode.children[0].children[0].rows[i].cells[16].children[0].children[0].children[0].value;
							 maxAmountRout = val.parentNode.children[0].children[0].rows[i].cells[17].children[0].children[0].children[0].value;
						if(minAmountRout==null|| minAmountRout==""||maxAmountRout==null|| maxAmountRout==""){
							 minAmountRout="10";
							maxAmountRout="10";}
						}
						
						if (retryMinutes == '') {
							retryMinutes = 0;
						}

						if (allowedFailureCount == '') {
							allowedFailureCount = 0;
						}

						if (loadPercentage == '') {
							loadPercentage = 0;
						}

						if (retryMinutes % 1 != 0) {
							alert("Decimal values are not allowed !")
							//document.getElementById("loading").style.display = "none";
							return false;
						}

						if (allowedFailureCount % 1 != 0) {
							alert("Decimal values are not allowed !")
							//document.getElementById("loading").style.display = "none";
							return false;
						}

						if(routerType==null){
							alert("Please Select the Router Type !")
							return false;


						}
						
						
						if (loadPercentage % 1 != 0) {
							alert("Decimal values are not allowed !")
							//document.getElementById("loading").style.display = "none";
							return false;
						}
						

						if (minAmountRout < 0 ) {
							alert("min Amount should not be in negative value  !")
							//document.getElementById("loading").style.display = "none";
							return false;
						}
						if (vpaCount < 0|| vpaCount == 0 ) {
							alert("Vpa Count should be greater than zero !")
							//document.getElementById("loading").style.display = "none";
							return false;
						}


						if (retryMinutes < 0 || retryMinutes == 0) {
							alert("Retry time should be atleast one minute !")
							//document.getElementById("loading").style.display = "none";
							return false;
						}

						if (allowedFailureCount <= 0) {
							alert("Allowed failure count should be greater than zero !")
							//document.getElementById("loading").style.display = "none";
							return false;
						}

						if ((loadPercentage < 0) || (loadPercentage == "")) {
							alert("Load percentage should be greater than or equal to zero !")
							//document.getElementById("loading").style.display = "none";
							return false;
						}

						if (priority <= 0) {
							alert("Priority should be greater than zero !")
							//document.getElementById("loading").style.display = "none";
							return false;
						}

						if (failedCount <= 0 || failedCount > 100) {
							alert("Failed Count should be greater than zero & less than 100 !")
							//document.getElementById("loading").style.display = "none";
							return false;
						}

						if (maxAmount <= 0 || maxAmount > 10000000) {
							alert("Max Amount should be greater than zero and less than 1CR !")
							//document.getElementById("loading").style.display = "none";
							return false;
						}
						
						if (totalTxn <= 0 || totalTxn > 10000000) {
							alert("Total Txn Amount should be greater than zero and less than 1CR !")
							//document.getElementById("loading").style.display = "none";
							return false;
						}

						if (status == true) {
							loadCheck = parseInt(loadCheck) + parseInt(loadPercentage);
						}
						
						
						//var rowElement = acquirer.trim()+","+status+","+description.trim()+","+mode.trim()+","+paymentType.trim()+","+mopType.trim()+","+allowedFailureCount.trim()+","+alwaysOn+","+loadPercentage.trim()+","+priority.trim()+","+retryMinutes.trim()+","+minAmount.trim()+","+maxAmount.trim();
						var rowElement = acquirer.trim() + "," + status + "," + description.trim() + "," + mode.trim() + "," + paymentType.trim() + "," + allowedFailureCount.trim() + "," + alwaysOn + "," + loadPercentage.trim() + "," + priority.trim() + "," + retryMinutes.trim() + "," + minAmount.trim() + "," + maxAmount.trim() + "," + totalTxn.trim() + "," + failedCount.trim()+","+maxAmountRout.trim()+","+minAmountRout.trim()+","+routerType.trim();

						if (rowData == "") {
							rowData = rowElement + ";";
						}
						else {
							rowData = rowData + rowElement + ";";
						}
					}

					/*if (parseInt(loadCheck) > 100){
						alert('Total load percentage sum is greater than 100 % , please enter correct values');
						document.getElementById("loading").style.display = "none";
						return false;
					}

					if (parseInt(loadCheck) < 100){
						alert('Total load percentage sum is lesser than 100 % , please enter correct values');
						document.getElementById("loading").style.display = "none";
						return false;
					}*/
					
					
				

					var token = document.getElementsByName("token")[0].value;

					$.ajax({
						type: "POST",
						url: "editRouterConfiguration",
						timeout: 0,
						data: { "routerConfig": rowData, "identifier": identifier, "mode": "MANUAL", "token": token, "struts.token.name": "token", },
						success: function (data) {
							var response = ((data["Invalid request"] != null) ? (data["Invalid request"].response[0]) : (data.response));
							if (null != response) {
								alert(response);
								document.getElementById("saveBtnFirst").disabled = false;
								//document.getElementById("defaultBtnSecnd").disabled = false;
								//document.getElementById("loading").style.display = "none"

							}
							alert("Smart Routing applied successfully");
							window.location.reload();

						},
						error: function (data) {
							window.location.reload();
							alert("Invalid Input , router rules not updated");
							document.getElementById("saveBtnFirst").disabled = false;
							//document.getElementById("defaultBtnSecnd").disabled = false;
							//document.getElementById("loading").style.display = "none"
						}
					});

				}

				function defaultModeEnable(val) {
					//document.getElementById("saveBtnFirst").disabled =true;
					//document.getElementById("defaultBtnSecnd").disabled = true;
					//document.getElementById("loading").style.display = "block"
					

					var tet = val.parentNode.children[0].id;
					var identifier = tet.slice(0, -3);

					var loadCheck = "0";
					var rowLength = val.parentNode.children[0].children[0].rows.length;
					var cellLength = val.parentNode.children[0].children[0].rows[1].cells.length;
					var rowData = "";
					for (var i = 1; i < rowLength; i++) {


						var acquirer = val.parentNode.children[0].children[0].rows[i].cells[1].innerText;
						var status = val.parentNode.children[0].children[0].rows[i].cells[2].children[0].children[0].children[0].children[0].checked;
						var description = val.parentNode.children[0].children[0].rows[i].cells[3].innerText;
						var mode = 'MANUAL';
						var paymentType = val.parentNode.children[0].children[0].rows[i].cells[5].innerText;
						var mopType = val.parentNode.children[0].children[0].rows[i].cells[6].innerText;
						var allowedFailureCount = val.parentNode.children[0].children[0].rows[i].cells[7].children[0].children[0].children[0].value;
						var alwaysOn = val.parentNode.children[0].children[0].rows[i].cells[8].children[0].children[0].children[0].children[0].checked;
						var loadPercentage = val.parentNode.children[0].children[0].rows[i].cells[9].children[0].children[0].children[0].value;
						//var priority = val.parentNode.children[0].children[0].rows[i].cells[10].innerText;

						var priority = val.parentNode.children[0].children[0].rows[i].cells[10].children[0].children[0].children[0].value;
						var retryMinutes = val.parentNode.children[0].children[0].rows[i].cells[11].children[0].children[0].children[0].value;
						var minAmount = val.parentNode.children[0].children[0].rows[i].cells[12].children[0].children[0].children[0].value;
						var maxAmount = val.parentNode.children[0].children[0].rows[i].cells[13].children[0].children[0].children[0].value;


						if (retryMinutes == '') {
							retryMinutes = 0;
						}

						if (allowedFailureCount == '') {
							allowedFailureCount = 0;
						}

						if (loadPercentage == '') {
							loadPercentage = 0;
						}


						if (retryMinutes % 1 != 0) {
							alert("Decimal values are not allowed , enter retry time again!")
							//document.getElementById("loading").style.display = "none";
							return false;
						}

						if (allowedFailureCount % 1 != 0) {
							alert("Decimal values are not allowed , enter allowed failure count again!")
							//document.getElementById("loading").style.display = "none";
							return false;
						}

						if (loadPercentage % 1 != 0) {
							alert("Decimal values are not allowed , enter Load percentage again!")
							//document.getElementById("loading").style.display = "none";
							return false;
						}

						if (retryMinutes < 0 || retryMinutes == 0) {
							alert("Retry time should be atleast one minute !")
							//document.getElementById("loading").style.display = "none";
							return false;
						}

						if (allowedFailureCount <= 0) {
							alert("Allowed failure count should be greater than zero !")
							//document.getElementById("loading").style.display = "none";
							return false;
						}

						if (loadPercentage < 0) {
							alert("Load percentage should be greater than or equal to zero !")
							//document.getElementById("loading").style.display = "none";
							return false;
						}

						if (priority <= 0) {
							alert("Priority should be greater than or equal to zero !")
							//document.getElementById("loading").style.display = "none";
							return false;
						}
						loadCheck = parseInt(loadCheck) + parseInt(loadPercentage);
						var rowElement = acquirer.trim() + "," + status + "," + description.trim() + "," + mode.trim() + "," + paymentType.trim() + "," + mopType.trim() + "," + allowedFailureCount.trim() + "," + alwaysOn + "," + loadPercentage.trim() + "," + priority.trim() + "," + retryMinutes.trim() + "," + minAmount.trim() + "," + maxAmount.trim();


						if (rowData == "") {
							rowData = rowElement + ";";
						}
						else {
							rowData = rowData + rowElement + ";";
						}
					}


					var token = document.getElementsByName("token")[0].value;
					//document.getElementById("loading").style.display = "block"
					$.ajax({
						type: "POST",
						url: "editRouterConfiguration",
						timeout: 0,
						data: { "routerConfig": rowData, "identifier": identifier, "mode": "AUTO", "token": token, "struts.token.name": "token", },
						success: function (data) {
							var response = ((data["Invalid request"] != null) ? (data["Invalid request"].response[0]) : (data.response));
							if (null != response) {
								alert(response);
								document.getElementById("saveBtnFirst").disabled = false;
								//document.getElementById("defaultBtnSecnd").disabled = false;
								//document.getElementById("loading").style.display = "none"
							}

							window.location.reload();
						},
						error: function (data) {
							window.location.reload();
							alert("Invalid Input , router rules not updated");
							document.getElementById("saveBtnFirst").disabled = false;
							//    document.getElementById("defaultBtnSecnd").disabled = false;
							//document.getElementById("loading").style.display = "none";
						}
					});

				}

				function cancel(curr_row, ele) {
					var parentEle = ele.parentNode;

					if (editMode) {
						window.location.reload();
					}
				}

			</script>
<script type="text/javascript">



				$(document).ready(function () {

					if ($("#paymentType").value != "") {
						$(".surcharge-bank").addClass('active');
					}

					$("#paymentType").on("change", function () {
						if (this.value == "") {
							$(".surcharge-bank").removeClass('active');
						}
					});
				});
			</script>
<script type="text/javascript">
				$(document).ready(function () {
					$(".surcharge-bank").click(function () {
						$("#datatable2").show();
						$(".surcharge-bank").addClass("active");
						$(".surcharge-report").removeClass("active");
					});
					$(".surcharge-report").click(function () {
						$("#datatable2").show();
						$(".surcharge-report").addClass("active");
						$(".surcharge-bank").removeClass("active");
					});
				});

			
			</script>

<style>
.product-spec input[type=text] {
	width: 35px;
}

.btn-tab {
	width: 17%;
	padding: 6px;
	font-size: 15px;
	color: #2c2c2c !important;
	background-color: #eaeaea !important;
	border: 1px solid #eaeaea !important;
	border-radius: 5px;
}

.btn-primary.active {
	background-color: #2b6dd1 !important;
	border-color: #2b6dd1 !important;
	color: #ffffff !important;
	border-radius: 5px;
}

.uper-input {
	width: 105% !important;
	margin-left: -10px !important;
}

.noRuleMessage {
	color: #a94442;
	background-color: #f2dede;
	border-color: #ebccd1;
	border-radius: 4px;
	text-align: center;
	list-style-type: none;
	font-size: 14px;
}

.card-list-toggle {
	cursor: pointer;
	padding: 8px 12px;
	border: 1px solid #f3794f;
	position: relative;
	background: linear-gradient(60deg, #589eff, #0d0738);
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

#Download {
	width: 93%;
	height: 33px;
}

input#flexSwitch20x30 {
	border: 2px solid black;
}
</style>
</head>

<body>

	<s:set var="countq" value="1" />

	<!-- <div id="loading" style="text-align: center;">
		<img id="loading-image" style="width:70px;height:70px;" src="../image/sand-clock-loader.gif" alt="Sending SMS..." />
	</div> -->
	<!--begin::Content-->
	<div class="content d-flex flex-column flex-column-fluid"
		id="kt_content">
		<!--begin::Toolbar-->
		<div class="toolbar" id="kt_toolbar">
			<!--begin::Container-->
			<div id="kt_toolbar_container"
				class="container-fluid d-flex flex-stack">
				<!--begin::Page title-->
				<div data-kt-swapper="true" data-kt-swapper-mode="prepend"
					data-kt-swapper-parent="{default: '#kt_content_container', 'lg': '#kt_toolbar_container'}"
					class="page-title d-flex align-items-center me-3 flex-wrap mb-5 mb-lg-0 lh-1">
					<!--begin::Title-->
					<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">
						Smart Router Payout</h1>
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
						<li class="breadcrumb-item text-muted">Merchant Setup</li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item"><span
							class="bullet bg-gray-200 w-5px h-2px"></span></li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item text-dark">Smart Router Payout</li>
						<!--end::Item-->
					</ul>
					<!--end::Breadcrumb-->
				</div>
				<!--end::Page title-->

			</div>
			<!--end::Container-->
		</div>
		<!--end::Toolbar-->
		<!--begin::Post-->
		<div class="post d-flex flex-column-fluid" id="kt_post">
			<!--begin::Container-->
			<div id="kt_content_container" class="container-xxl">
				<div class="row my-5">
					<div class="col">
						<div class="card">
							<div class="card-body">
								<div class="row my-3">
									<div class="col-md-12">


										<s:actionmessage class="success success-text" />

										<s:form id="routerConfigurationForm"
											action="routerConfigurationActionMerchantPayout" method="post">

											<!-- STart row here -->
											<div class="row">

												<div class="col-sm-6 col-lg-3">
													<label
														class="d-flex align-items-center fs-6 fw-semibold mb-2">Merchant
														Name:</label><br>
													<s:select headerKey="" headerValue="Select Merchant"
														name="merchantName" id="merchantName" list="merchantList"
														class="form-select form-select-solid" autocomplete="off" />
												</div>
												<div class="col-sm-6 col-lg-3">
													<label
														class="d-flex align-items-center fs-6 fw-semibold mb-2">Channel
														Type:</label><br>

													<s:select headerKey="Select PaymentType"
														headerValue="Select Payment Type"
														class="form-select form-select-solid"
														list="#{'FIAT':'Fiat','CRYPTO':'Crypto'}"
														name="paymentMethod"
														id="paymentMethods" autocomplete="off" />
												</div>

												<div class="col-sm-6 col-lg-3">
													<label
														class="d-flex align-items-center fs-6 fw-semibold mb-2">Card
														Holder Type:</label><br>
													<s:select headerKey="Select Payment region"
														headerValue="Select Payment region"
														class="form-select form-select-solid"
														list="#{'COMMERCIAL':'Commercial','CONSUMER':'Consumer'}"
														name="cardHolderType" id="cardHolderType" />
												</div>

												<div class="col-sm-6 col-lg-3">
													<label
														class="d-flex align-items-center fs-6 fw-semibold mb-2">Currency
														:</label><br>

												<%-- 	<s:select headerKey="Select Currency"
														headerValue="Select Currency"
														class="form-select form-select-solid"
														list="@com.pay10.commons.util.CurrencyTypes@values()"
														listValue="code" listKey="name" name="currencyMethod"
														id="currencyMethods" autocomplete="off" /> --%>
														
														<s:select headerKey="Select Currency"
														headerValue="Select Currency"
														class="form-select form-select-solid"
														list="currencyMapList"
														name="currencyMethod"
														id="currencyMethods" autocomplete="off" />
														
												</div>

												<div class="col-sm-6 col-lg-3">

													<div class="mt-14">
														<button class="btn mx-5 btn-primary" id="Download"
															type="button">Submit</button>
													</div>
												</div>


												<table width="100%" border="0" cellspacing="0"
													cellpadding="0"
													class="txnf table table-striped table-row-bordered gy-5 gs-7 dataTable no-footer"
													style="margin-top: 1%;">

													<tr>
														<!-- <td width="21%"><h2 style="margin-bottom:5%;">Router Configuration</h2></td> -->
													</tr>

													<tr class="fw-bold fs-6 text-gray-800">
														<td align="center" valign="top">
															<!-- <table width="98%" border="0" cellspacing="0" cellpadding="0">

															<tr>
																<td align="left"> -->

														</td>
													</tr>

													<tr class="fw-bold fs-6 text-gray-800">

														<td align="left">
															<div id="datatable2" class="scrollD">
																<br>
																<s:if test="%{routerRuleData.isEmpty()}">
																	<tr>
																		<td colspan="4" class="noRuleMessage">No Rule
																			Found !!</td>
																	</tr>
																</s:if>
																<s:else>

																	<s:iterator value="routerRuleData" status="pay">

																		<br>
																		<div onclick="sendId('<s:property value="key" />')"
																			class=" card-list-toggle" id="test">
																			<strong> <s:property value="key" />
																			</strong>
																		</div>
																		<div class="scrollD card-list">
																			<div id="<s:property value="key" />Div">
																				<table width="100%" border="0" align="center"
																					class="product-spec m-2 ml-2 table table-striped table-row-bordered gy-5 gs-7 dataTable no-footer">
																					<tr class="boxheading fw-bold fs-6 text-gray-800">

																						<th width="5%" height="25" valign="middle"
																							style="display: none">id</th>
																						<th width="4%" align="left" valign="middle">Acquirer
																						</th>
																						<th width="6%" align="left" valign="middle">Status
																						</th>
																						<th width="4%" align="left" valign="middle">
																							Description</th>

																						<th width="4%" align="left" valign="middle">
																							Currency</th>
																						<!-- <th width="6%" align="left" valign="middle">Mode</th> -->
																						<th width="6%" align="left" valign="middle"
																							style="display: none;">Mode</th>
																						<th width="6%" align="left" valign="middle">Channel
																							Type</th>
<!-- 																						<th width="5%" align="left" valign="middle">Mop -->
<!-- 																							Type</th> -->
																						<th width="3%" align="left" valign="middle"
																							style="display: none">Allowed Fail count</th>
																						<!-- <th width="4%" align="left" valign="middle">Always On</th>
																								 <th width="3%" align="left" valign="middle" style="display:none">Load %</th>
																								 <th width="3%" align="left" valign="middle">Priority</th>
																								 <th width="3%" align="left" valign="middle" style="display:none">Retry Time</th>
																								 <th width="4%" align="left" valign="middle" style="display:none">Min Txn</th>
																								 <th width="4%" align="left" valign="middle">Max Txn</th> -->
																						<th width="3%" align="left" valign="middle">RemainTxn(%)
																						</th>
																						<th width="3%" align="left" valign="middle"
																							style="display: none">Load %</th>
																						<th width="3%" align="left" valign="middle">Priority
																						</th>
																						<th width="5%" align="left" valign="middle">Failed
																							Count</th>
																						<th width="3%" align="left" valign="middle"
																							style="display: none">Retry Time</th>
																						<th width="3%" align="left" valign="middle"
																							style="display: none">Min Txn</th>
																						<th width="3%" align="left" valign="middle">Remain
																							Amt</th>

																						<th width="3%" align="left" valign="middle">Total
																							Amt</th>
																						<th align="left" valign="middle">

																							TxnMinAmount</th>
																						<th align="left" valign="middle">
																							TxnMaxAmount</th>


																						<th width="3%" align="left" valign="middle"
																							class="vpaCountHeader">Vpa Count</th>
																						<th width="3%" align="left" valign="middle">Routing
																							Option</th>


																					</tr>
																					<s:iterator value="value" status="itStatus">
																						<tr class="boxtext">
																							<td align="left" valign="middle"
																								style="display: none"><s:property
																									value="merchant" /></td>
																							<td align="left" valign="middle"><s:property
																									value="acquirer" /></td>
																							<td align="center" valign="middle">
																								<div
																									class="form-check form-check-success form-check-solid">
																									<s:if test="%{currentlyActive}">
																										<input class="form-check-input h-20px w-30px"
																											type="checkbox"
																											checked="<s:property value ='currentlyActive'/>"
																											id="flexSwitch20x30" name="currentlyActive" />
																									</s:if>
																									<s:else>
																										<input class="form-check-input h-20px w-30px"
																											type="checkbox" id="flexSwitch20x30"
																											name="currentlyActive" />
																									</s:else>

																									<!-- <s:checkbox name="currentlyActive" class="form-check-input" value="currentlyActive" data-toggle="toggle"/> -->
																									<!-- <s:checkbox name="currentlyActive" class="form-check-input h-20px w-30px" value="currentlyActive" data-toggle="toggle"/> -->
																									<label class="form-check-label"
																										for="kt_flexSwitchCustomDefault_1_1">

																									</label>
																								</div>

																							</td>
																							<td align="left" valign="middle"><s:property
																									value="statusName" /></td>

																							<td align="left" valign="middle"><s:property
																									value="currencyName" /></td>
																							<!-- <td align="left" valign="middle"> -->
																							<td align="left" valign="middle"
																								style="display: none;"><s:property
																									value="mode" /></td>
																							<td align="left" valign="middle"><s:property
																									value="channel" /></td>
<%-- 																							<td align="left" valign="middle"><s:property --%>
<%-- 																									value="mopTypeName" /></td> --%>
																							<td align="left" valign="middle"
																								style="display: none"><s:textfield
																									style="width:60px; border:1px solid #595959; height: 10px;margin-bottom: 5px;"
																									class="form-control form-control-solid" min="0"
																									name="allowedFailureCount" type='number'
																									value="%{allowedFailureCount}" /></td>
																							<td align="left" valign="middle">
																								<%-- <s:checkbox
																								name="alwaysOn"
																								value="alwaysOn"
																								data-toggle="toggle" />
																						</td> --%> <s:textfield
																									style="width:60px; border:1px solid #595959; height: 10px;margin-bottom: 5px;"
																									class="form-control form-control-solid"
																									readonly="true" name="txnCountsPer" type='text'
																									value="%{(maxAmount*100)/totalTxn}" />
																							</td>
																							<td align="left" valign="middle"
																								style="display: none"><s:textfield
																									type='number'
																									style="width:60px; border:1px solid #595959; height: 10px;margin-bottom: 5px;"
																									class="form-control form-control-solid" min="0"
																									max="100" name="loadPercentage"
																									value="%{loadPercentage}" /></td>
																							<td align="left" valign="middle"><s:textfield
																									type='number'
																									style="width:60px; border:1px solid #595959; height: 10px;margin-bottom: 5px;"
																									class="form-control form-control-solid" min="0"
																									max="100" name="priority" value="%{priority}" />
																							</td>
																							<%-- <s:property
																							value="priority" />
																						</td> --%>
																							<td align="left" valign="middle"><s:textfield
																									type='number'
																									style="width:60px; border:1px solid #595959; height: 10px;margin-bottom: 5px;"
																									class="form-control form-control-solid" min="0"
																									name="failedCount" value="%{failedCount}" /></td>
																							<td align="left" valign="middle"
																								style="display: none"><s:textfield
																									type='number'
																									style="width:60px; border:1px solid #595959; height: 10px;margin-bottom: 5px;"
																									class="form-control form-control-solid" min="0"
																									name="retryMinutes" value="%{retryMinutes}" />
																							</td>
																							<td align="left" valign="middle"
																								style="display: none"><s:textfield
																									readonly="true"
																									style="width:100px; border:1px solid #595959; height: 10px;margin-bottom: 5px;"
																									name="minTxn" value="%{minAmount}"
																									class="form-control form-control-solid" /></td>
																							<td align="left" valign="middle"><s:textfield
																									style="width:100px; border:1px solid #595959; height: 10px;margin-bottom: 5px;"
																									name="minTxn" value="%{maxAmount}"
																									class="form-control form-control-solid" /></td>
																							<td align="left" valign="middle"><s:textfield
																									style="width:100px; border:1px solid #595959; height: 10px;margin-bottom: 5px;"
																									name="totalTxn" value="%{totalTxn}"
																									class="form-control form-control-solid" /></td>

																							<td align="left" valign="middle"><s:textfield
																									style="width:100px; border:1px solid #595959; height: 10px;margin-bottom: 5px;"
																									name="totalTxn" value="%{routerMinAmount}"
																									type='number' min="0"
																									class="form-control form-control-solid" /></td>

																							<td align="left" valign="middle"><s:textfield
																									style="width:100px; border:1px solid #595959; height: 10px;margin-bottom: 5px;"
																									name="totalTxn" value="%{routerMixAmount}"
																									type='number' min="0"
																									class="form-control form-control-solid" /></td>


																							<td align="left" valign="middle" class='vpaCount'>
																								<s:textfield
																									style="width:100px; border:1px solid #595959; height: 10px;margin-bottom: 5px;"
																									name="totalTxn" value="%{vpaCount}" min="0"
																									type='number'
																									class="form-control form-control-solid" />
																							</td>

																							<s:if test="%{#itStatus.index==0}">



																								<td align="left" valign="middle"><s:radio
																										name="%{id}" id="1" class=""
																										style="margin-right: 12px,width: 141px;"
																										value="%{routingType}" list="routingTypeDto"
																										listKey="name" listValue="name"
																										autocomplete="off"
																										onclick="setRouterType(this,'%{key}')" /></td>
																								<td style="display: none;"><s:textfield
																										name="" id="%{id}" value="%{routingType}"
																										class="form-control form-control-solid" /></td>


																							</s:if>


																						</tr>
																					</s:iterator>
																				</table>
																			</div>

																			<input id="saveBtnFirst" name="saveBtnFirst"
																				type="button" 
																				class="btn btn-primary" value="Save" align="center"
																				onClick=saveDetails(this)
																				style="margin: 10px !important;"></input>

																		</div>
																	</s:iterator>
																</s:else>
															</div>
														</td>
													</tr>
												</table>

												<s:hidden name="token" value="%{#session.customToken}"></s:hidden>
										</s:form>
										<!-- End row here -->
									</div>



								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	</div>




	<!--end::Content-->

	<script type="text/javascript">
		
			$("#Download").click(function() {
				var paymentMethodValue= document.getElementById("paymentMethods").value;
					if(paymentMethodValue!='UP'){
						$('.l').hide();
						$('.vpaCount').hide();
					}
					else{
						$('.vpaCountHeader').show();
						$('.vpaCount').show();	
					}
			$("#routerConfigurationForm").submit();
			});

				$(document).ready(function () {
					$('.card-list-toggle').on('click', function () {
						enbaleButtonAsPerAccess();
					});
				});
				function enbaleButtonAsPerAccess() {
					var menuAccess = document.getElementById("menuAccessByROLE").value;
					var accessMap = JSON.parse(menuAccess);
					var access = accessMap["routerConfigurationActionPayout"];
					if (access.includes("Update")) {
						var saveBtns = document.getElementsByName("saveBtnFirst");
						for (var i = 0; i < saveBtns.length; i++) {
							var btn = saveBtns[i];
							btn.disabled = false;
						}
					}
				}
				
				function setRouterType(element,id) {
					
					debugger
					var name = element.name;
				var value = element.value;
				$("input[name='"+element.name+"']").val(element.value);
					document.getElementsByName(name).value=value;
					document.getElementById(name).value=value;

					var name1 = document.getElementsByName(element.name).value;
					
					var aa=	$('#'+id+'Div  table tbody td')[20].children[0].children[0].children[4].checked;
					if(aa){
						var a=$('#'+id+'Div table tbody th')[17].children[0];	
						var data=$('#'+id+'Div table tr').length;
						for(var i=0 ; i<data;i++){
							$('#'+id+'Div  table tbody tr')[i].children[17].style.display="table-cell";
							$('#'+id+'Div  table tbody tr')[i].children[18].style.display="table-cell";

						}
						
// 						$('#'+id+'Div  table tbody tr')[0].children[16].style.display="block";
// 						$('#'+id+'Div  table tbody tr')[0].children[17].style.display="block";

// 						$('#'+id+'Div  table tbody tr')[1].children[16].style.display="block";
// 						$('#'+id+'Div  table tbody tr')[1].children[17].style.display="block";

// 						$('#'+id+'Div  table tbody tr')[2].children[16].style.display="block";
// 						$('#'+id+'Div  table tbody tr')[2].children[17].style.display="block";


					}
					else{
						var data=$('#'+id+'Div table tr').length;

						for(var i=0; i<data;i++){
							$('#'+id+'Div  table tbody tr')[i].children[17].style.display="none";
							$('#'+id+'Div  table tbody tr')[i].children[18].style.display="none";

						}
						
						
					}
					
					
				}
				
				function between(x, min, max) {
					
					var nmin = Number(min)
					var nx = Number(x)
					var nmix = Number(max)
	if(nx >= nmin && nx <= nmix) {
	return true;
}
					  return false;
					}
				
		
			</script>
</body>

</html>