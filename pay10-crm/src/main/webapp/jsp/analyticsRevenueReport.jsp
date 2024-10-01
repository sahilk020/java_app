<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<meta name="viewport" content="width=device-width, initial-scale=1" />
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
<title>Revenue Report</title>
<!-- stylesheet -->
<!-- <link href="../css/jquery-ui.css" rel="stylesheet" />
<link href="../css/select2.min.css" rel="stylesheet" />

<link href="../assets/css/style.bundle.css" rel="stylesheet"
	type="text/css" />
<link href="../assets/css/custom/custom-style.css" rel="stylesheet"
	type="text/css" /> -->

<!-- javascripts -->
<script src="../js/jquery-1.9.0.js"></script>

<script src="../js/jquery-ui.min.js" type="text/javascript"></script>
<script src="../js/jquery.select2.js" type="text/javascript"></script>
	<script src="../assets/plugins/global/plugins.bundle.js"></script>
					<script src="../assets/js/scripts.bundle.js"></script>
					<style>
							#checkboxes {
	display: none;
	border: 1px #DADADA solid;
	height:180px;
	overflow-y: scroll;
	position:absolute;
	background:#fff;
	z-index:1;
	padding: 10px;
	 width: 285px;
}
#checkboxes label {
  width: 74%;
}
#checkboxes input {
  width:18%;
}
div#checkboxes1{
position: absolute;
    overflow-y: auto;
    border: 1px solid #dadada;
    display: block;
    height: 180px;
    background: #ffff;
    z-index: 1;
    padding: 10px;
    width: 31%;
}
					</style>
<script type="text/javascript">
	function getMopType(value, id) {
		debugger
		var merchantemail = document.getElementById("merchant").value;
		var paytype=value;
		$.ajax({
			type : "GET",
			url : "GetMoptype",
			timeout : 0,
			data : {
				"merchantemail":merchantemail,
				"payment":paytype,
				"struts.token.name": "token",
			  },
			success : function(data) {
			  debugger
			var mopresult = [];

			  mopresult = data.moplist;
			  /* var mopdiv ="";
				   for(var i = 0; i < mopresult.length; i++){

					 mopdiv=mopdiv+" <option value='"+mopresult[i].uiName+"'>"+mopresult[i].name+"</option>"


				   } */
				   var count=0;
				   $('#'+id).html("");
				   const countriesDropDown = document.getElementById(id);
				   for (let key in mopresult) {
					if(count==0){
					   let option = document.createElement("option");
						option.setAttribute('value', "ALL");

						let optionText = document.createTextNode("All");
						option.appendChild(optionText);

						countriesDropDown.appendChild(option);
						count++

					}
					  let option = document.createElement("option");
					  option.setAttribute('value', data.moplist[key].code);

					  let optionText = document.createTextNode(data.moplist[key].name);
					  option.appendChild(optionText);

					  countriesDropDown.appendChild(option);
					}
				   let option = document.createElement("option");
				   option.setAttribute('value', "OT");

				   let optionText = document.createTextNode("Others");
				   option.appendChild(optionText);

				   countriesDropDown.appendChild(option);


				  // document.getElementById("getid").innerHTML=mopdiv;

				  // const select = document.querySelector('select');
				  // select.options.add(new Option("+mopresult[i].name+", "+mopresult[i].uiName+"))

			}
		});
	}
</script>
<script type="text/javascript">
$(document).ready(function(){
	/* document.getElementById("loading").style.display = "none"; */


			statisticsAction();


		//acquirer checkbox click
		$(document).click(function(){
		expanded = false;
		$('#checkboxes').hide();
		});
		$('#checkboxes').click(function(e){
			e.stopPropagation();
		});
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
var allSelectedAquirer;
function getCheckBoxValue(){
	 var allInputCheckBox = document.getElementsByClassName("myCheckBox");

  		allSelectedAquirer = [];
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

function statisticsAction() {

		var acquirer=[];
		var inputElements = document.getElementsByName('acquirer');
		for(var i=0; inputElements[i]; ++i){
		  if(inputElements[i].checked){
			   acquirer.push( inputElements[i].value);

		  }
		}
			var merchantEmailId = document.getElementById("merchant").value;
			var dateFrom = document.getElementById("dateFrom").value;
			var dateTo = document.getElementById("dateTo").value;
			var	paymentMethods = document.getElementById("paymentMethods").value;
			var	mopType = document.getElementById("mopType").value;
			//var currency = document.getElementById("currency").value;
			var paymentsRegion = document.getElementById("paymentsRegion").value;
			var cardHolderType = document.getElementById("cardHolderType").value;
			//var pgRefNum = document.getElementById("pgRefNum").value;
			var transactionType = document.getElementById("transactionType").value;
			var acquirerString = acquirer.join();

			if(merchantEmailId==''){
				merchantEmailId='ALL'
			}
			if(paymentMethods==''){
				paymentMethods='ALL'
			}

			if(paymentsRegion==''){
				paymentsRegion='ALL'
			}
			if(cardHolderType==''){
				cardHolderType='ALL'
			}
			if(transactionType==''){
				transactionType='ALL'
			}

			if(acquirerString==''){
				acquirerString='ALL'
			}


	var token = document.getElementsByName("token")[0].value;
	/* document.getElementById("loading").style.display = "block" */
	$
			.ajax({
				url : "summaryReportCountAction",
				timeout : 0,
				type : "POST",
				data : {
					paymentMethods : paymentMethods,
					dateFrom : document.getElementById("dateFrom").value,
					dateTo : document.getElementById("dateTo").value,
					merchantEmailId : merchantEmailId,
					mopType : mopType,
					transactionType : transactionType,
					paymentsRegion : paymentsRegion,
					cardHolderType : cardHolderType,
					acquirer : acquirerString,
					draw : 1,
					length :1,
					start : 1,
					token : token,
				},
				success : function(data) {
				/* document.getElementById("loading").style.display = "none"; */
				//document.getElementById("dvMerchantName").innerHTML = data.transactionCountSearch.merchantName;
				//document.getElementById("dvAcquirerName").innerHTML = data.transactionCountSearch.acquirer;
				//document.getElementById("dvPaymentMethod").innerHTML = data.transactionCountSearch.paymentMethod;
				document.getElementById("dvSaleSettledCount").innerHTML = inrFormat(data.transactionCountSearch.saleSettledCount);
				document.getElementById("dvSaleSettledAmount").innerHTML = inrFormat(data.transactionCountSearch.saleSettledAmount);
				document.getElementById("dvPgSaleSurcharge").innerHTML = inrFormat(data.transactionCountSearch.pgSaleSurcharge);
				document.getElementById("dvAcquirerSaleSurcharge").innerHTML = inrFormat(data.transactionCountSearch.acquirerSaleSurcharge);
				document.getElementById("dvPgSaleGst").innerHTML = inrFormat(data.transactionCountSearch.pgSaleGst);
				document.getElementById("dvAcquirerSaleGst").innerHTML = inrFormat(data.transactionCountSearch.acquirerSaleGst);

				document.getElementById("dvRefundSettledCount").innerHTML = inrFormat(data.transactionCountSearch.refundSettledCount);
				document.getElementById("dvRefundSettledAmount").innerHTML = inrFormat(data.transactionCountSearch.refundSettledAmount);
				document.getElementById("dvPgRefundSurcharge").innerHTML = inrFormat(data.transactionCountSearch.pgRefundSurcharge);
				document.getElementById("dvAcquirerRefundSurcharge").innerHTML = inrFormat(data.transactionCountSearch.acquirerRefundSurcharge);
				document.getElementById("dvPgRefundSurcharge").innerHTML = inrFormat(data.transactionCountSearch.pgRefundSurcharge);
				document.getElementById("dvPgRefundGst").innerHTML = inrFormat(data.transactionCountSearch.pgRefundGst);
				document.getElementById("dvAcquirerRefundGst").innerHTML = inrFormat(data.transactionCountSearch.acquirerRefundGst);
				//document.getElementById("dvTotalMerchantAmount").innerHTML = data.transactionCountSearch.totalMerchantAmount;
				document.getElementById("dvMerchantSaleSettledAmount").innerHTML = inrFormat(data.transactionCountSearch.merchantSaleSettledAmount);
				document.getElementById("dvMerchantRefundSettledAmount").innerHTML = inrFormat(data.transactionCountSearch.merchantRefundSettledAmount);
				document.getElementById("dvTotalProfit").innerHTML = inrFormat(data.transactionCountSearch.totalProfit);

				},
				error : function(data) {
					/* document.getElementById("loading").style.display = "none"; */
	}
			});

}


</script>
<script>
	function inrFormat(temp) { // nStr is the input string
    // temp = 1000000.00
    if(temp == 0){
        return 0;
    }
    //console.log(" Hiiii " + temp);
    if(temp == undefined || temp == null || temp == ''){
        return 0;
    }
    numArr = temp.toString().split('.');
    nStr = numArr[0];
    nStr += '';
    x = nStr.split('.');
    x1 = x[0];
    x2 = x.length > 1 ? '.' + x[1] : '';
    var rgx = /(\d+)(\d{3})/;
    var z = 0;
    var len = String(x1).length;
    var num = parseInt((len/2)-1);

     while (rgx.test(x1))
     {
       if(z > 0)
       {
         x1 = x1.replace(rgx, '$1' + ',' + '$2');
       }
       else
       {
         x1 = x1.replace(rgx, '$1' + ',' + '$2');
         rgx = /(\d+)(\d{2})/;
       }
       z++;
       num--;
       if(num == 0)
       {
         break;
       }
     }
     let result = x1 + x2 ;
    //  console.log('' + num[1])
     if(numArr[1] != undefined){
        result += '.' + numArr[1];
     }
    // console.log(result);
    return result;
}
</script>
<style>
		.card-stats .card-header.card-header-icon i {
    font-size: 20px !important;

}
.card [class*="card-header-"] .card-icon, .card [class*="card-header-"] .card-text {
   padding: 0px !important;

}
#cardIcon{
	padding: 15px !important;
}
#materialIcons{
	font-size: 36px !important;
}


@media (min-width: 1496px)
{
.card.card-stats {
    min-height: 81px !important;
}
}
#ui-datepicker-div{
	z-index: 10 !important;
}
@media (min-width: 1300px)
{
.card.card-stats {
    min-height: 81px !important;
}
}
@media (min-width: 992px)
{
.card.card-stats {
    min-height: 100px !important;
}
}

#submit{
margin-top: 25px;
}
	.overSelect {
					position: absolute;
					left: 0;
					right: 0;
					top: 0;
					bottom: 0;
				}
				.selectBox{
				position:relative;
				}
</style>
</head>

<body>
	<div style="overflow: auto !important;">
		<div class="content d-flex flex-column flex-column-fluid" id="kt_content">
			<!--begin::Toolbar-->
		<div class="toolbar" id="kt_toolbar">
			<!--begin::Container-->
			<div id="kt_toolbar_container" class="container-fluid d-flex flex-stack">
				<!--begin::Page title-->
				<div data-kt-swapper="true" data-kt-swapper-mode="prepend" data-kt-swapper-parent="{default: '#kt_content_container', 'lg': '#kt_toolbar_container'}" class="page-title d-flex align-items-center me-3 flex-wrap mb-5 mb-lg-0 lh-1">
					<!--begin::Title-->
					<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">Revenue Report</h1>
					<!--end::Title-->
					<!--begin::Separator-->
					<span class="h-20px border-gray-200 border-start mx-4"></span>
					<!--end::Separator-->
					<!--begin::Breadcrumb-->
					<ul class="breadcrumb breadcrumb-separatorless fw-semibold fs-7 my-1">
						<!--begin::Item-->
						<li class="breadcrumb-item text-muted">
							<a href="home" class="text-muted text-hover-primary">Dashboard</a>
						</li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item">
							<span class="bullet bg-gray-200 w-5px h-2px"></span>
						</li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item text-muted">Analytics</li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item">
							<span class="bullet bg-gray-200 w-5px h-2px"></span>
						</li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item text-dark">Revenue Report</li>
						<!--end::Item-->
					</ul>
					<!--end::Breadcrumb-->
				</div>
				<!--end::Page title-->

			</div>
			<!--end::Container-->
		</div>
		<!--end::Toolbar-->
		<div class="content d-flex flex-column flex-column-fluid" id="kt_content">
			<div class="post d-flex flex-column-fluid" id="kt_post">
				<!--begin::Container-->
				<div id="kt_content_container" class="container-xxl">

					<div class="row my-5">
						<div class="col">
							<div class="card">
								<div class="card-body">
									<!--begin::Input group-->
									<div class="row g-9 mb-8">

					  <div class="col-md-4 fv-row">
							<label class="d-flex align-items-center fs-6 fw-bold mb-2">Date From</label>

							<s:textfield type="text" id="dateFrom" name="dateFrom" class="form-select form-select-solid" autocomplete="off" readonly="true" />

					  </div>
					  <div class="col-md-4 fv-row">
						<label class="d-flex align-items-center fs-6 fw-bold mb-2">Date To</label>
							<s:textfield type="text" id="dateTo" name="dateTo" class="form-select form-select-solid" autocomplete="off" readonly="true" />

					  </div>
					  <div class="col-md-4 fv-row">
						<label class="d-flex align-items-center fs-6 fw-bold mb-2">Acquirer</label>
						<div class="txtnew">
							<div class="selectBox" id="selectBox" onclick="showCheckboxes(event)" title="dummy Title">
							  <select class="form-select form-select-solid">
								<option>ALL</option>
							  </select>
							  <div class="overSelect"></div>
							</div>
							<div id="checkboxes" onclick="getCheckBoxValue()">
							   <s:checkboxlist headerKey="ALL" headerValue="ALL" list="@com.pay10.commons.util.AcquirerTypeUI@values()"
													id="acquirer" class="myCheckBox"	name="acquirer" value="acquirer"  listValue="name" listKey="code"
													/>
							</div>
						  </div>
					</div>
					<div class="col-md-4 fv-row">
						<label class="d-flex align-items-center fs-6 fw-bold mb-2">Payment Method</label>

							<s:select headerKey="" headerValue="ALL" class="form-select form-select-solid"
								list="@com.pay10.commons.util.PaymentTypeUI@values()"
								listValue="name" listKey="code" name="paymentMethods"
								id="paymentMethods" autocomplete="off" value="" onchange="getMopType(this.value,'mopType')"  />


					</div>

					<div class="col-md-4 fv-row">
						<label class="d-flex align-items-center fs-6 fw-bold mb-2">Merchant</label>

							<s:if
								test="%{#session.USER.UserType.name()=='ADMIN' || #session.USER.UserType.name()=='SUBADMIN' || #session.USER_TYPE.name()=='SUPERADMIN'}">
								<s:select name="merchant" class="form-select form-select-solid" id="merchant"
									headerKey="" headerValue="ALL" list="merchantList"
									listKey="emailId" listValue="businessName" autocomplete="off" />
							</s:if>
							<s:else>
								<s:select name="merchant" class="form-select form-select-solid" id="merchant"
									headerKey="" headerValue="ALL" list="merchantList"
									listKey="emailId" listValue="businessName" autocomplete="off" />
							</s:else>


					</div>
					<div class="col-md-4 fv-row">
						<label class="d-flex align-items-center fs-6 fw-bold mb-2">MOP Type</label>

						<s:select name="mopType" id="mopType" headerValue="ALL"
						headerKey="ALL" list="@com.pay10.commons.util.MopTypeUI@values()"
						listValue="name" listKey="code" class="form-select form-select-solid" />
					  </div>




					<div class="col-md-4 fv-row">
					  <label class="d-flex align-items-center fs-6 fw-bold mb-2">Card Holder Type</label>

						<s:select headerKey="ALL" headerValue="ALL" class="form-select form-select-solid"
										list="#{'COMMERCIAL':'Commercial','CONSUMER':'Consumer'}" name="cardHolderType" id = "cardHolderType" />
					</div>
				  <div class="col-md-4 fv-row">
					  <label class="d-flex align-items-center fs-6 fw-bold mb-2">Txn Type</label>
					  <s:select headerKey="ALL" headerValue="ALL" class="form-select form-select-solid"
					  list="#{'SALE':'SALE'}" name="transactionType" id = "transactionType" />

				  </div>
				  <div class="col-md-4 fv-row">
					  <label class="d-flex align-items-center fs-6 fw-bold mb-2">Txn Region </label>

						<s:select headerKey="ALL" headerValue="ALL" class="form-select form-select-solid"
							list="#{'DOMESTIC':'Domestic','INTERNATIONAL':'International'}" name="paymentsRegion" id = "paymentsRegion" />

				  </div>


				<div class="col-md-4 fv-row">


						<input type="button" id="submit" disabled="disabled" value="Submit"
							class="btn btn-primary">


				</div>


			  </div>
			  </div>


		  </div>
		</div>
	  </div>
	  <!-- <div class="btnSec">
 		<button style="border-radius:5px !important; margin-bottom:15px;">Submit</button>
	 </div> -->
	 <div class="row my-5">
		<div class="col">
						   <div class="card">
							   <div class="card-body">
	<div class="row" style="margin-top: 2%;">


	   <div class="col-lg-4 col-md-8 col-sm-8">
		   <div class="card card-stats">
			 <div class="card-header card-header-danger card-header-icon">
			   <div class="card-icon">
				 <!-- <i class="material-icons">thumb_up</i> -->
				 <i class="fa fa-chart-simple fa-5x"></i>
			   </div>
			   <p class="card-category">Sale Settlement Count:</p>
			   <h3 class="card-title" id="dvSaleSettledCount"><s:property value="%{statistics.totalSuccess}"/></h3>
			 </div>
			 <!-- <div class="card-footer">
			   <div class="stats">
				   <i class="fa fa-thumbs-up">Sale Settlement Count</i>

			   </div>
			 </div> -->
		   </div>
		 </div>
		 <!-- <div class="col-lg-4 col-md-8 col-sm-8">
		   <div class="card card-stats">
			 <div class="card-header card-header-warning card-header-icon">
			   <div class="card-icon">
				  <i class="material-icons">thumb_up</i> 
				 <i class="fa fa-rotate-left fa-5x"></i>
			   </div>
			   <p class="card-category">Refund Settlement Count:</p>
			   <h3 class="card-title" id="dvRefundSettledCount"><s:property value="%{statistics.totalSuccess}"/></h3>
			 </div>
			 <div class="card-footer">
			   <div class="stats">
				   <i class="fa fa-thumbs-up">Refund Settlement Count</i>

			   </div>
			 </div> 
		   </div>
		 </div> -->
		 <div class="col-lg-4 col-md-8 col-sm-8">
		   <div class="card card-stats">
			 <div class="card-header card-header-rose card-header-icon">
			   <div class="card-icon">
				 <!-- <i class="material-icons">thumb_up</i> -->
				 <i class="fa fa-arrow-trend-up fa-5x"></i>
			   </div>
			   <p class="card-category">Total Profit:</p>
			   <h3 class="card-title" id="dvTotalProfit"><s:property value="%{statistics.totalSuccess}"/></h3>
			 </div>
			 <!-- <div class="card-footer">
			   <div class="stats">
				   <i class="fa fa-thumbs-up">Total Profit</i>

			   </div>
			 </div> -->
		   </div>
		 </div>
	</div>
	</div>
	</div>
	</div>
   </div>

    	<!-- <div class="row" style="padding: 10px 0">

 		<div class="col-sm-3"><h2 style="font-size:17px !important;">Sale Settlement Count:</h2></div>
 		<div class="col-sm-1"><h2><p id = "dvSaleSettledCount" class="media-heading" style="margin-left:-100% !important; font-size:17px !important;"></p></h2></div>

		<div class="col-sm-3"><h2 style="font-size:17px !important;">Refund Settlement Count:</h2></div>
 		<div class="col-sm-1"><h2><p id = "dvRefundSettledCount" class="media-heading" style="margin-left:-100% !important; font-size:17px !important;"></p></h2></div>

		<div class="col-sm-3"><h2 style="font-size:17px !important;">Total Profit:</h2></div>
 		<div class="col-sm-1"><h2><p id = "dvTotalProfit" class="media-heading" style="margin-left:-100% !important; font-size:17px !important;"></p></h2></div>


 	</div> -->

	<!--<div class="row" style="padding: 10px 0">
		<div class="col-sm-3 "><h2>Total Profit</h2></div>
 		<div class="col-sm-1"><h2><p id = "dvTotalProfit" class="media-heading"></p></h2></div>
	</div>-->
	 <div class="row my-5">
		<div class="card ">
		  <div class="card-header card-header-rose card-header-text">
			<div class="card-text" id="cardIcon">
			  <h4 class="card-title">Sale Settlement Transaction</h4>
			</div>
		  </div>
		  <div class="card-body">
 	<div class="saleSettlementSec" id="saleSettlementSec">
 		<!-- <h3>Sale Settlement Transaction</h3> -->
 		<div class="row">
 			<div class="col-sm-4">
 				<div class="boxSec">
 					<h4><p id = "dvSaleSettledAmount" class="media-heading"></p></h4>
 					<p>Sale Settlement Amount</p>
 				</div>
 			</div>
 			<div class="col-sm-4">
 				<div class="boxSec">
 					<h4><p id = "dvMerchantSaleSettledAmount" class="media-heading"></p></h4>
 					<p>Merchant Sale Settlement Amount</p>
 				</div>
 			</div>
 			<div class="col-sm-4">
 				<div class="boxSec">
 					<h4><p id = "dvPgSaleSurcharge" class="media-heading"></p></h4>
 					<p>Total Surcharge</p>
 				</div>
 			</div>
 		</div>
 		<div class="row">
 			<div class="col-sm-4">
 				<div class="boxSec">
 					<h4><p id = "dvPgSaleGst" class="media-heading"></p></h4>
 					<p>Total GST</p>
 				</div>
 			</div>
 			<div class="col-sm-4">
 				<div class="boxSec">
 					<h4><p id = "dvAcquirerSaleSurcharge" class="media-heading"></p></h4>
 					<p>Total Surcharge Acquirer</p>
 				</div>
 			</div>
 			<div class="col-sm-4">
 				<div class="boxSec">
 					<h4><p id = "dvAcquirerSaleGst" class="media-heading"></p></h4>
 					<p>Total GST Acquirer</p>
 				</div>
 			</div>
 		</div>
	 </div>
	</div>
	 </div>
	</div>

	<!-- <div class="col">
		<div class="col-md-12">
			<div class="card ">
			<div class="card-header card-header-rose card-header-text">
				<div class="card-text" id="cardIcon">
				<h4 class="card-title">Refund Settlement Transaction</h4>
				</div>
			</div>
			<div class="card-body">
				<div class="refundSettlementSec" id="refundSettlementSec">
					 <h3>Refund Settlement Transaction</h3> 
					<div class="row">
						<div class="col-sm-4">
							<div class="boxSec">
								<h4><p id = "dvRefundSettledAmount" class="media-heading"></p></h4>
								<p>Refund Settlement Amount</p>
							</div>
						</div>
						<div class="col-sm-4">
							<div class="boxSec">
								<h4><p id = "dvMerchantRefundSettledAmount" class="media-heading"></p></h4>
								<p>Merchant Refund Settlement Amount</p>
							</div>
						</div>
						<div class="col-sm-4">
							<div class="boxSec">
								<h4><p id = "dvPgRefundSurcharge" class="media-heading"></p></h4>
								<p>Total Surcharge</p>
							</div>
						</div>
					</div>
					<div class="row">
						<div class="col-sm-4">
							<div class="boxSec">
								<h4><p id = "dvPgRefundGst" class="media-heading"></p></h4>
								<p>Total GST</p>
							</div>
						</div>
						<div class="col-sm-4">
							<div class="boxSec">
								<h4><p id = "dvAcquirerRefundSurcharge" class="media-heading"></p></h4>
								<p>Total Surcharge(Acquirer)</p>
							</div>
						</div>
						<div class="col-sm-4">
							<div class="boxSec">
								<h4><p id = "dvAcquirerRefundGst" class="media-heading"></p></h4>
								<p>Total GST(Acquirer)</p>
							</div>
						</div>
					</div>
				 </div>
			 </div>
		 </div>
	 </div>
</div> -->

				</div>
				<!--end::Container-->
			</div>
		</div>
		</div>
	</div>

 <script type="text/javascript">
$(document).ready(function(){
  // Initialize select2
  getMopType('ALL','mopType');
  $("select.form-select").select2();
  //button click
  $('#submit').click(function(){
  		var dateFrom = document.getElementById('dateFrom').value.split('-'),
  			dateTo = document.getElementById('dateTo').value.split('-'),
	  		myDateFrom = new Date(dateFrom[2], dateFrom[1], dateFrom[0]), //Year, Month, Date  
	   	 	myDateTo = new Date(dateTo[2], dateTo[1], dateTo[0]), //Year, Month, Date 
			oneDay = 24*60*60*1000; // hours*minutes*seconds*milliseconds


	   if (myDateTo >= myDateFrom) {  
	          var diffDays = Math.round(Math.abs((myDateFrom.getTime() - myDateTo.getTime())/(oneDay)));
	          if(diffDays>31){
	          	alert('No. of days can not be more than 31');
	          }else{
	          		statisticsAction();
	          }
	   }else {  
	        alert("'Date From' must be before the 'Date To'.");
	   }  
  });
});
$(document).ready(function() {
	if ($('#analyticsRevenue').hasClass("active")) {
		var menuAccess = document.getElementById("menuAccessByROLE").value;
		var accessMap = JSON.parse(menuAccess);
		var access = accessMap["analyticsRevenue"];
		if (access.includes("View")) {
			$("#submit").removeAttr("disabled");
		}
	}
	
});
</script>
					<script
						src="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.js"></script>
				
					<script>
					
					$("#dateTo").flatpickr({
						maxDate: new Date(),
						dateFormat: 'd-m-Y',
						defaultDate: "today"
					});
					$("#dateFrom").flatpickr({
						maxDate: new Date(),
						dateFormat: 'd-m-Y',
						defaultDate: "today"
					});
	</script>
</body>
</html>