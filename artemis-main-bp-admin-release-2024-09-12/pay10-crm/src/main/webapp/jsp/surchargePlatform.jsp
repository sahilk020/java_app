<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="/struts-tags" prefix="s"%>
<html>
<head>
<title>Surcharge Platform</title>

<!-- Added By Sweety -->
<meta charset="utf-8" />

<link href="../assets/css/style.bundle.css" rel="stylesheet"
	type="text/css" />
<script src="../assets/plugins/global/plugins.bundle.js"></script>
<script src="../assets/js/scripts.bundle.js"></script>

<!-- Ended -->

<style type="text/css">
</style>

<script type="text/javascript">
$(document).ready(function(){
 
  // Initialize select2
  $("#merchants").select2();
});
</script>

<script type="text/javascript">
$(document).ready(function() {
	$('.card-list-toggle').on('click', function(){
		$(this).toggleClass('active');
		$(this).next('.card-list').slideToggle();
	});
	 //var paymentVal = document.getElementById("paymentType").value;
	 
	$('#paymentType').change(function(event){
		   /* if(paymentVal == "Payment Type"){
			   $('#loader-wrapper').hide();
		   }
		    else{
				$('#loader-wrapper').show();
			} */
		   var merchants = $("select#merchants").val();
		   var acquirer = document.getElementById("paymentType").value;
		   var emailId = merchants;
		   var paymentType = acquirer;

		   if(merchants==null ||merchants=="" || acquirer==null||acquirer==""){
			   document.getElementById("datatable").style.display="none";
			  // document.getElementById("datatable1").style.display="none";
			  // document.getElementById("datatable2").style.display="none";
			  return false;
			  
			 }
		   else{
			   document.getElementById("datatable").style.display="block";
			  // document.getElementById("datatable1").style.display="none";
			   //document.getElementById("datatable2").style.display="none";     
			   
		   }
		   document.getElementById("surchargedetailform").submit();
	});

   $('#merchants').change(function(event){
	   var acquirer = document.getElementById("paymentType").value;
	   var merchants = $("select#merchants").val();
	   var emailId = merchants;
	   var paymentType = acquirer;
	   
	   if(merchants==null ||merchants=="" || acquirer==null||acquirer==""){
		   document.getElementById("datatable").style.display="none";
		  // document.getElementById("datatable1").style.display="none";
		  // document.getElementById("datatable2").style.display="none";
		  return false;
		  
		 }
	   else{
		   document.getElementById("datatable").style.display="block";
		   //document.getElementById("datatable1").style.display="block";
		   //document.getElementById("datatable2").style.display="none";
	   }
	   document.getElementById("surchargedetailform").submit();	
    });
    
		var btnArray = document.getElementsByName("cancelBtn");
		//cancelBtnArray;
			for (var i=0;i<btnArray.length ; i++){
				//if ((btnArray[i].id).indexof('cancelBtn') !== -1){
				var cancelBtnCurrent = btnArray[i];
				cancelBtnCurrent.disabled = true;
				//}
	}

});

var editMode;

function editCurrentRow(divId,curr_row,ele,cancelBtnId){
	 var cancelButton = document.getElementById(cancelBtnId);
	   cancelButton.disabled = false;
	var userType = "<s:property value='%{#session.USER.UserType.name()}'/>";
	var div = document.getElementById(divId);

	var table = div.getElementsByTagName("table")[0];

	var merchantId = document.getElementById("merchants").value;
	var paymentType = document.getElementById("paymentType").value;
	var rows = table.rows;
	var currentRowNum = Number(curr_row);
	var currentRow = rows[currentRowNum];
	var cells = currentRow.cells;
	var cell0 = cells[0];
	var cell1 = cells[1];
	var cell2 = cells[2];
	var cell3 = cells[3]; 

	var cell4 =  cells[4];
	var cell5 =  cells[5];
	var cell6 =  cells[6];
	
	var cell0Val = cell0.innerText.trim();
	var cell1Val = cell1.innerText.trim();
	var cell2Val = cell2.innerText.trim();
	var cell3Val = cell3.innerText.trim();
	var cell4Val = cell4.innerText.trim();
	var cell5Val = cell5.innerText.trim();
	var cell6Val = cell6.innerText.trim();
	
	if(ele.value=="Edit"){
		if(editMode) 
		{
				alert('Please edit the current row to proceed');
				return;
		}
		ele.value="save";
		ele.className ="btn btn-primary btn-xs";
		cell2.innerHTML = "<input type='number' id='cell2Val'   class='chargingplatform' min='0' step='0.0' value="+cell2Val+" onChange =hideradio() ></input>";
		cell3.innerHTML = "<input type='number' id='cell3Val'   class='chargingplatform' min='0' step='0.0' value="+cell3Val+" onChange = hideradio()></input>";
		cell4.innerHTML = "<input type='number' id='cell4Val'   class='chargingplatform' min='0' step='0.0' value="+cell4Val+"></input>";

		//Added by Sweety
	    if(cell2Val>0 && cell3Val>0){
	    show();
        }
		editMode = true;
	}
	else{
		var surchargePercentage = document.getElementById('cell2Val').value;
		var surchargeAmount = document.getElementById('cell3Val').value;
		var minTransactionAmount = document.getElementById('cell4Val').value;
		
		if (minTransactionAmount == '' || minTransactionAmount < 0){
			alert('Blank / Negative values not allowed for Min Transaction amount.');
			return false;
		}
		if (surchargePercentage == '' || surchargePercentage < 0){
			alert('Blank / Negative values not allowed for Surcharge Percentage.');
			return false;
		}
		if (surchargeAmount == '' || surchargeAmount < 0){
			alert('Blank / Negative values not allowed for Surcharge Amount.');
			return false;
		}
		
		var paymentsRegion = cell5Val;
		var userType = "<s:property value='%{#session.USER.UserType.name()}'/>";
		var loginEmailId = "<s:property value='%{#session.USER.EmailId}'/>";
		cell2.innerHTML = surchargePercentage;
		cell3.innerHTML = surchargeAmount;
		var merchantId = document.getElementById("merchants").value;
		var paymentType = document.getElementById("paymentType").value;
		//Added by Sweety
		var isHigher = $("input[name='isHigher']:checked").val();
		editMode = false;
		if(surchargePercentage>0 && surchargeAmount>0 && isHigher==null){
			editMode = true;
			ele.className ="btn btn-success btn-xs";
			cell2.innerHTML = "<input type='number' id='cell2Val'   class='chargingplatform' min='0' step='0.0' value="+surchargePercentage+" onChange =hideradio() ></input>";
			cell3.innerHTML = "<input type='number' id='cell3Val'   class='chargingplatform' min='0' step='0.0' value="+surchargeAmount+" onChange = hideradio()></input>";
			alert("Please select one radio button");
			$('#loader-wrapper').hide();
			return false;
			

		}
		$('#loader-wrapper').show();

		ele.value="Edit";
		ele.className ="btn btn-primary btn-xs";		
		var token  = document.getElementsByName("token")[0].value;
		$.ajax({
			type: "POST",
			url:"editSurchargeDetail",
			data:{"emailId":merchantId,"minTransactionAmount":minTransactionAmount,"paymentType":paymentType,"surchargePercentage":surchargePercentage, "surchargeAmount":surchargeAmount, "isHigher":isHigher,"userType":userType, "loginEmailId":loginEmailId,"paymentsRegion":paymentsRegion,"token":token,"struts.token.name": "token",},
			success:function(data){
				var response = ((data["Invalid request"] != null) ? (data["Invalid request"].response[0]) : (data.response));
				if(null!=response){
					alert(response);			
				}
				//TODO....clean values......using script to avoid page refresh
				window.location.reload();
		    },
			error:function(data){
				window.location.reload();
				alert("Invalid Input , surcharge data not saved");
			},
			input:function(data){
				alert("Invalid Input , please correct and try again");
			}
		});
	}
}

function editSurchargeCurrentRow(divId,curr_row,ele,cancelBtnId){
	var cancelButton = document.getElementById(cancelBtnId);
	   cancelButton.disabled = false;
	var userType = "<s:property value='%{#session.USER.UserType.name()}'/>";
	var loginUserEmailId = "<s:property value='%{#session.USER.EmailId}'/>";
	var div = document.getElementById(divId);

	var table = div.getElementsByTagName("table")[0];

	var merchantId = document.getElementById("merchants").value;
	var paymentType = document.getElementById("paymentType").value;
	
	var acq = divId;
	var acquirer =  acq.substring(0, acq.length - 3);
	
	var rows = table.rows;
	var currentRowNum = Number(curr_row);
	var currentRow = rows[currentRowNum];
	var cells = currentRow.cells;
	var cell0 = cells[0];
	var cell1 = cells[1];
	var cell2 = cells[2];
	var cell3 = cells[3];
	var cell4 =  cells[4].children[0];
	var cell5 =  cells[4].children[1];
	var cell6 =  cells[5].children[0];
	var cell7 =  cells[5].children[1];
	
	var cell8 =  cells[6].children[0];
	var cell9 =  cells[6].children[1];
	var cell10 =  cells[7].children[0];
	var cell11 =  cells[7].children[1];
	
	var cell12 =  cells[8];
	var cell13 =  cells[9];
	
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
	var cell12Val = cell12.querySelector('input[type=checkbox]').checked;
	var cell13Val = cell13.innerText.trim();

	if(ele.value=="Edit"){
		if(editMode) 
		{
				alert('Please edit the current row to proceed');
				return;
		}
		ele.value="save";
		ele.className ="btn btn-primary btn-xs";
		cell4.innerHTML = "<input type='number' id='cell4Val'   class='chargingplatform' min='0' step='0.0' value="+cell4Val+"></input>";
		cell5.innerHTML = "<input type='number' id='cell5Val'   class='chargingplatform' min='0' step='0.0' value="+cell5Val+"></input>";
		cell6.innerHTML = "<input type='number' id='cell6Val'   class='chargingplatform' min='0' step='0.0' value="+cell6Val+"></input>";
		cell7.innerHTML = "<input type='number' id='cell7Val'   class='chargingplatform' min='0' step='0.0' value="+cell7Val+"></input>";
		
		cell8.innerHTML = "<input type='number' id='cell8Val'   class='chargingplatform' min='0' step='0.0' value="+cell8Val+"></input>";
		cell9.innerHTML = "<input type='number' id='cell9Val'   class='chargingplatform' min='0' step='0.0' value="+cell9Val+"></input>";
		cell10.innerHTML = "<input type='number' id='cell10Val'   class='chargingplatform' min='0' step='0.0' value="+cell10Val+"></input>";
		cell11.innerHTML = "<input type='number' id='cell11Val'   class='chargingplatform' min='0' step='0.0' value="+cell11Val+"></input>";
		
		cell12.innerHTML = "";
		if(cell12Val){
			cell12.innerHTML = "<input type='checkbox' id='cell12Val' checked='true'></input>";
		}else{
		    cell12.innerHTML = "<input type='checkbox' id='cell12Val'></input>";
		}
		editMode = true;
	}
	else{
		var bankSurchargePercentageOnCommercial = document.getElementById('cell4Val').value;
		var bankSurchargePercentageOffCommercial = document.getElementById('cell5Val').value;
		var bankSurchargeAmountOnCommercial = document.getElementById('cell6Val').value;
		var bankSurchargeAmountOffCommercial = document.getElementById('cell7Val').value;
		var bankSurchargePercentageOnCustomer = document.getElementById('cell8Val').value;
		var bankSurchargePercentageOffCustomer = document.getElementById('cell9Val').value;
		var bankSurchargeAmountOnCustomer = document.getElementById('cell10Val').value;
		var bankSurchargeAmountOffCustomer = document.getElementById('cell11Val').value;
		
		if (bankSurchargePercentageOnCommercial == '' || bankSurchargePercentageOnCommercial < 0 
				|| bankSurchargePercentageOffCommercial == '' || bankSurchargePercentageOffCommercial < 0 
				|| bankSurchargeAmountOnCommercial == '' || bankSurchargeAmountOnCommercial < 0 
				|| bankSurchargeAmountOffCommercial == '' || bankSurchargeAmountOffCommercial < 0 
				|| bankSurchargePercentageOnCustomer == '' || bankSurchargePercentageOnCustomer < 0 
				|| bankSurchargePercentageOffCustomer == '' || bankSurchargePercentageOffCustomer < 0 
				|| bankSurchargeAmountOnCustomer == '' || bankSurchargeAmountOnCustomer < 0 
				|| bankSurchargeAmountOffCustomer == '' || bankSurchargeAmountOffCustomer < 0 ){
			
			alert ('Blank / Negative values are not allowed.');
			return false;
			
		}
		var userType = "<s:property value='%{#session.USER.UserType.name()}'/>";
		cell4.innerHTML = bankSurchargePercentageOnCommercial;
		cell5.innerHTML = bankSurchargePercentageOffCommercial;
		cell6.innerHTML = bankSurchargeAmountOnCommercial;
		cell7.innerHTML = bankSurchargeAmountOffCommercial;
		
		cell8.innerHTML = bankSurchargePercentageOnCustomer;
		cell9.innerHTML = bankSurchargePercentageOffCustomer;
		cell10.innerHTML = bankSurchargeAmountOnCustomer;
		cell11.innerHTML = bankSurchargeAmountOffCustomer;
		
		var merchantId = document.getElementById("merchants").value;
		var paymentType = document.getElementById("paymentType").value;
		editMode = false;
		$('#loader-wrapper').show();

		ele.value="Edit";
		ele.className ="btn btn-primary btn-xs";		
		var token  = document.getElementsByName("token")[0].value;
		$.ajax({
			type: "POST",
			url:"editSurchargeMappingDetail",
			data:{"emailId":merchantId, "paymentType":paymentType, "bankSurchargePercentageOnCommercial":bankSurchargePercentageOnCommercial, "bankSurchargePercentageOnCustomer":bankSurchargePercentageOnCustomer,
			"bankSurchargePercentageOffCommercial":bankSurchargePercentageOffCommercial,"bankSurchargePercentageOffCustomer":bankSurchargePercentageOffCustomer,
				"bankSurchargeAmountOnCommercial":bankSurchargeAmountOnCommercial,"bankSurchargeAmountOnCustomer":bankSurchargeAmountOnCustomer,
				"bankSurchargeAmountOffCommercial":bankSurchargeAmountOffCommercial,"bankSurchargeAmountOffCustomer":bankSurchargeAmountOffCustomer,"allowOnOff":cell12Val,"allowOnOff":cell12Val,"acquirer":acquirer,
				"paymentsRegion":cell1Val,"merchantIndustryType":cell3Val,"status":cell13Val,"mopType":cell2Val,"userType":userType,"loginUserEmailId":loginUserEmailId,"token":token,"struts.token.name": "token",},
			success:function(data){
				var response = ((data["Invalid request"] != null) ? (data["Invalid request"].response[0]) : (data.response));
				if(null!=response){
					alert(response);			
				}
				//TODO....clean values......using script to avoid page refresh
				window.location.reload();
		    },
			error:function(data){
				window.location.reload();
				alert("Invalid Input , surcharge data not saved");
			}
		});
	}
}

function cancel(curr_row,ele){
	var parentEle = ele.parentNode;
	
	if(editMode){
		$('#loader-wrapper').show();
	 	window.location.reload();
	}
}
</script>

<script type="text/javascript">

$(window).on("load", function(){

	/*if($("#paymentType").value == undefined){
		$(".surcharge-bank").removeClass('active');
	}*/
});

$(document).ready(function(){
  	if($("#paymentType").value != ""){
		$(".surcharge-bank").addClass('active');
	}
		
  	$("#paymentType").on("change", function(){
		if(this.value == ""){
			$(".surcharge-bank").removeClass('active');
		}
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
	font-size: 14px;
	color: #2c2c2c !important;
	background-color: #eaeaea !important;
	border: 1px solid #eaeaea !important;
	border-radius: 5px;
}

.btn-primary.active {
	background-color: #496cb6 !important;
	border-color: #496cb6 !important;
	color: #ffffff !important;
	border-radius: 5px;
}

.uper-input {
	width: 98% !important;
	margin-left: -10px !important;
}

#onOffheading {
	white-space: nowrap;
	width: 100%;
}

#merchantCustomer {
	white-space: nowrap;
	width: 100%;
}

#merchantFc {
	white-space: nowrap;
	width: 100%;
}

label {
	font-size: 14px;
	font-weight: 500;
	color: grey;
}

.col-sm-6.col-lg-3 {
	margin-bottom: 10px;
}

#radioButton {
	padding-top: 27px;
}

table.product-spec th {
	font-size: 14px;
	padding: 8px;
	background: #fbfcfd;
	color: #262424;
}
.label{
color:black;
}
</style>
</head>
<body>

<!-- 	<div id="loader-wrapper"
		style="width: 100%; height: 100%; display: none;">
		<div id="loader"></div>
	</div> -->

	<s:actionmessage class="error error-new-text" />

	<!-- <div style="overflow: scroll !important;"> -->
	<!-- 	<table width="100%" border="0" cellspacing="0" cellpadding="0"
				class="txnf" style="margin-top: 1%;"> -->

	<!-- 	<tr>
					<td align="center" valign="top">
						<table width="98%" border="0" cellspacing="0" cellpadding="0"> -->


	<!-- Added By Sweety -->

	<!--begin::Root-->
	<div class="d-flex flex-column flex-root">
		<!--begin::Page-->
		<div class="page d-flex flex-row flex-column-fluid">
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
								Surcharge Setting</h1>
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
								<li class="breadcrumb-item text-dark">Surcharge Setting</li>
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
						<s:form id="surchargedetailform" action="surchargePlatformAction"
							method="post">
							<div class="row my-5">
								<div class="col">
									<div class="card">
										<div class="card-body">
											<!--begin::Input group-->
											<div class="row g-9 mb-8">
												<!--begin::Col-->
												<div class="col-md-3 fv-row">
													<label
														class="d-flex align-items-center fs-6 fw-bold mb-2 label">
														<span class="">Select User</span>
													</label>
													<s:select headerKey="-1" headerValue="Select User"
														list="#{'1':'Merchant'}" id="user" name="user" value="1"
														class="form-select form-select-solid" autocomplete="off" />

												</div>
							
												<div class="col-md-3 fv-row">
													<label
														class="d-flex align-items-center fs-6 fw-bold mb-2 label">
														<span class="">Merchant</span>
													</label>
													<s:select headerValue="Select Merchant" headerKey=""
														name="emailId" class="form-select form-select-solid"
														id="merchants" list="listMerchant" listKey="emailId"
														listValue="businessName" autocomplete="off" />
												</div>


												<div class="col-md-3 fv-row">
													<label
														class="d-flex align-items-center fs-6 fw-bold mb-2 label">
														<span class="">Payment Method</span>
													</label>
													<!--end::Label-->
													<s:select class="form-select form-select-solid"
														headerKey="" headerValue="Payment Type"
														list="@com.pay10.commons.util.PaymentType@values()"
														listKey="code" listValue="name" name="paymentType"
														id="paymentType" autocomplete="off" />
												</div>

												<div class="col-md-2 fv-row">
													<div class="textnew" id="radioButton">
														<input type="radio" name="isHigher" id="higher"
															value="true"><label for="Higher">Higher</label><br>
														<br> <input type="radio" name="isHigher" id="lower"
															value="false"><label for="Lower">Lower</label><br>

													</div>
												</div>
											</div>
								
				
		

		<!-- End By Sweety -->
		
				<tr>
					<td align="left">
						<div id="datatable" class="scrollD table table-striped table-row-bordered gy-5 gs-7">
							<s:iterator value="aaData" status="pay">
								<br>
								<div class="text-primary card-list-toggle"
									style="margin-bottom: 10px;">
									<strong style="color: #262424; display:none"><s:property
											value="key" /></strong><br>
								</div>
								<div class="scrollD card-list">
									<div id="<s:property value="key" />Div">
										<table width="100%" border="0" align="center"
											class="product-spec">
											<tr class="boxheading fw-bold fs-6 text-gray-800" style="color: #202f4b;">
												<th width="5%" height="25" valign="middle"
													style="display: none">Payment</th>
												<th width="6%" align="left" valign="middle">GST</th>
												<th width="8%" align="left" valign="middle">Surcharge %</th>
												<th width="7%" align="left" valign="middle">Surcharge
													FC</th>
												<th width="10%" align="left" valign="middle">Minimum
													Transaction Amount</th>
												<th width="10%" align="left" valign="middle">Region</th>
												<th width="10%" align="left" valign="middle"
													style="display: none !important">IsHigher</th>
												<th width="5%" align="left" valign="middle">Update</th>
												<th width="2%" align="left" valign="middle"
													style="display: none">id</th>
												<th width="5%" align="left" valign="middle"><span
													id="cancelLabel">Cancel</span></th>
											</tr>
											<s:iterator value="value" status="itStatus">
												<tr class="boxtext">
													<td align="left" valign="middle" style="display: none">
														<s:property value="paymentType" />
													</td>
													<td align="left" valign="middle"><s:property
															value="serviceTax" /></td>
													<td align="left" valign="middle"><s:property
															value="surchargePercentage" /></td>
													<td align="left" valign="middle"><s:property
															value="surchargeAmount" /></td>
													<td align="left" valign="middle"><s:property
															value="minTransactionAmount" /></td>
													<td align="left" valign="middle" class="paymentRegion">
														<s:property value="paymentsRegion" />
													</td>
													<td align="left" valign="middle"
														style="display: none !important" class="checkValue">
														<s:property value="isHigher" />

													</td>
													<td align="left" valign="middle"><div>
															<s:textfield id="edit%{#itStatus.count}"
																name="editSurcharge" disabled="true" value="Edit"
																type="button"
																onclick="editCurrentRow('%{key +'Div'}','%{#itStatus.count}', this,'cancelBtn%{key +#itStatus.count}')"
																class="btn btn-primary btn-xs" autocomplete="off"></s:textfield>
														</div></td>
													<td align="left" valign="middle" style="display: none">
														<s:property value="id" />
													</td>
													<td align="left" valign="middle"><s:textfield
															id="cancelBtn%{key +#itStatus.count}" value="Cancel"
															type="button" name="cancelBtn"
															onclick="cancel('%{#itStatus.count}',this)"
															class="btn btn-danger btn-xs" autocomplete="off">
														</s:textfield></td>
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
									</div>
								</div>

							</div>
			<s:hidden name="token" value="%{#session.customToken}"></s:hidden>
			</s:form>
			
		</div>
	</div>

	<script>
$( "#bankSurcharge" ).click(function(){
		var merchants = $("select#merchants").val();
		var paymentType = document.getElementById("paymentType").value;
		if(merchants==null ||merchants=="" || paymentType==null||paymentType==""){
		   document.getElementById("datatable").style.display="none";
		   document.getElementById("datatable1").style.display="none";
		   document.getElementById("datatable2").style.display="none";
		   $(".surcharge-bank").addClass("active");
		   $(".surcharge-report").removeClass("active");
		   return false;
		 }
	   else{
		   document.getElementById("datatable").style.display="block";
		   document.getElementById("datatable1").style.display="none";
		   document.getElementById("datatable2").style.display="block";
		   $(".surcharge-bank").addClass("active");
           $(".surcharge-report").removeClass("active");
	   }
});

$( "#surchargeReport" ).click(function(){
		var merchants = $("select#merchants").val();
		var paymentType = document.getElementById("paymentType").value;
		if(merchants==null ||merchants=="" || paymentType==null||paymentType==""){
		   document.getElementById("datatable").style.display="none";
		   document.getElementById("datatable1").style.display="none";
		   document.getElementById("datatable2").style.display="none";
		   $(".surcharge-report").addClass("active");
           $(".surcharge-bank").removeClass("active");
		   return false;
		 }
	   else{
		   document.getElementById("datatable").style.display="none";
		   document.getElementById("datatable1").style.display="block";
		   document.getElementById("datatable2").style.display="none";
		   $(".surcharge-report").addClass("active");
           $(".surcharge-bank").removeClass("active");
	   }
});

$(document).ready(function() {
	if (window.location.href.includes("surchargePlatformAction")) {
		var menuAccess = document.getElementById("menuAccessByROLE").value;
		var accessMap = JSON.parse(menuAccess);
		var access = accessMap["surchargePlatform"];
		if (access.includes("Add") || access.includes("Update")) {
			var editBtns = document.getElementsByName("editSurcharge");
			for (var i=0; i<editBtns.length; i++) {
				var editBtn = editBtns[i];
				editBtn.disabled=false;
			}
		}
	}
});

//Added by sweety
$(document).ready(function() {
	$('#radioButton').hide();
});

function show(){
	$('#radioButton').show();
	var isHigh= document.getElementsByClassName('checkValue');
	var paymentRegion =document.getElementsByClassName('paymentRegion');
	var domestic= paymentRegion[0].innerText.trim();
	var international= paymentRegion[1].innerText.trim();
	var highELement= isHigh[0].innerText.trim();
	var lowELement= isHigh[1].innerText.trim();
	 console.log(highELement);
	 console.log(lowELement);
	 console.log(domestic,international);
	if(highELement=="true" && highELement != null && highELement !='' && domestic=="DOMESTIC"){
		 $("#higher").prop('checked',true);
		 console.log(highELement);
		}
	else{
		 $("#lower").prop('checked',true);
		}
	/* if(lowELement=="false" && lowELement != null && lowELement !='' && international=="INTERNATIONAL"){
		 $("#higher").prop('checked',true);
		 console.log(highELement);
		}
	else{
		 $("#lower").prop('checked',true);
		} */
}

function hideradio(){
	var cell2Val = $('#cell2Val').val();
	var cell3Val = $('#cell3Val').val();
	if(cell2Val>0 && cell3Val >0)
	{
	$('#radioButton').show();
	}else
		{
		$('#higher').attr("checked", false);
		$('#lower').attr("checked", false);
		$('#radioButton').hide();
		}
	
	
	
}


</script>

</body>
</html>