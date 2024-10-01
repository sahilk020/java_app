<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Merchant Automated Fund Transfer</title>


<script src="../js/jquery.minshowpop.js"></script>
<script src="../js/jquery.formshowpop.js"></script>
<script src="../js/commanValidate.js"></script>
<link href="../css/Jquerydatatableview.css" rel="stylesheet" />
<link href="../css/jquery-ui.css" rel="stylesheet" />
<link href="../css/Jquerydatatable.css" rel="stylesheet" />
<script src="../js/jquery.min.js" type="text/javascript"></script>
<script src="../js/core/popper.min.js"></script>
<script src="../js/core/bootstrap-material-design.min.js"></script>
<script src="../js/plugins/perfect-scrollbar.jquery.min.js"></script>
<!-- Control Center for Material Dashboard: parallax effects, scripts for the example pages etc -->
<script src="../js/material-dashboard.js?v=2.1.0" type="text/javascript"></script>
<script src="../js/moment.js" type="text/javascript"></script>
<script src="../js/daterangepicker.js" type="text/javascript"></script>
<script src="../js/jquery.dataTables.js"></script>
<script src="../js/jquery-ui.js"></script>
<script src="../js/commanValidate.js"></script>
<script src="../js/jquery.popupoverlay.js"></script>
<script src="../js/dataTables.buttons.js" type="text/javascript"></script>
<script src="../js/pdfmake.js" type="text/javascript"></script>

<script src="../js/jszip.min.js" type="text/javascript"></script>
<script src="../js/vfs_fonts.js" type="text/javascript"></script>
<script src="../js/buttons.colVis.min.js" type="text/javascript"></script>

<!--  loader scripts -->
<script src="../js/loader/modernizr-2.6.2.min.js"></script>
<script src="../js/loader/main.js"></script>
<link rel="stylesheet" href="../css/loader/normalize.css" />
<link rel="stylesheet" href="../css/loader/main.css" />
<link rel="stylesheet" href="../css/loader/customLoader.css" />

<link href="../css/select2.min.css" rel="stylesheet" />
<script src="../js/jquery.select2.js" type="text/javascript"></script>
<script src="../assets/js/scripts.bundle.js"></script>
<style>
	table.invoicetable {
    border: 1px solid #ffffff;
    border-bottom: 1px solid #dedede;
    background: #fff !important;
    margin: 5px;
}
</style>

<script>

$(document).ready(function() {
	document.getElementById("payBtn").disabled = true;
	
	 document.getElementById("beneficiaryListDiv").style.display="none";
	  $('#viewButtonPG').click(function(event){
		var acquirer = document.getElementById("acquirer1").value;
		if (acquirer == null || acquirer.trim() == "ALL" || acquirer.trim() == "") {
            alert("Please Select Nodal  Acquirer !");
            return false;
        }
		document.getElementById("beneficiaryListDiv").style.display="block";
		reloadTable();	 
	   });
	   
		$(function() {			
			renderTable();
			;
		});
		/* $(function(){
			var table = $('#beneficiaryListResultDataTable').DataTable();
			$('#beneficiaryListResultDataTable').on('click', 'td.my_class1', function() {
				var rowIndex = table.cell(this).index().row;
				var rowData = table.row(rowIndex).data();
				//popup().show();	
			});
		}); */


	});
	$(document).ready(function () {
		//nodalDetails();
		
		$("#payId").select2();
		
		document.getElementById("loadingInner").style.display = "none";
		
									
	});
	$(document).ready(function(){
		$("#amount").on("input", function() {
        if (/^0/.test(this.value)) {
          this.value = this.value.replace(/^0/, "")
        }
	  })
	  
	 
    $('input#amount').blur(function(){
		
        var num = parseFloat($(this).val());
        var cleanNum = num.toFixed(2);
        $(this).val(cleanNum);
        if(num/cleanNum < 1){
            $('#error').text('Please enter only 2 decimal places, we have truncated extra points');
            }
		});
		
 
		



	});

	



function getNodalBalance(){
	document.getElementById("nodalBalance").innerHTML = "<img src='../image/loading_horizon.gif' width='20' height='16'>";
	$('#nodalBalanceError').html("");
												$('#nodalBalance').val("");
												//$('#loader-wrapper').show();
												document.getElementById("loadingInner").style.display = "block";	
												//$("#getNodalBalance").prop("disabled", true);
												$.ajax({
													url : "yesBankFT3NodalBalance",
													type : "post",
													data : {
														acquirer : "YESBANKFT3"
													},
													success : function(data) {
													//	$('#loader-wrapper').hide();
														document.getElementById("loadingInner").style.display = "none";
														//$("#getNodalBalance").prop("disabled", false);
														if(data.response.toLowerCase() == 'success'){
															//$('#nodalBalance').val(data.currencyCode + " " +data.balance);	
															document.getElementById("nodalBalance").innerHTML = data.currencyCode + " " +inrFormat(data.balance);
														}else {
															$('#nodalBalanceError').html("Unable to fetch balance. Please try again later.");
														}
														
													},
													error : function(error) {
														//$('#loader-wrapper').hide();
														document.getElementById("loadingInner").style.display = "none";
														//$("#getNodalBalance").prop("disabled", false);
														//console.log(error);
														$('#nodalBalanceError').html("Unable to fetch balance. Please try again later.");
													}
												});

}

	function isNumberKey(evt, element) {
  var charCode = (evt.which) ? evt.which : event.keyCode
  if (charCode > 31 && (charCode < 48 || charCode > 57) && !(charCode == 46 || charCode == 8))
    return false;
  else {
    var len = $(element).val().length;
    var index = $(element).val().indexOf('.');
    if (index > 0 && charCode == 46) {
      return false;
    }
    if (index > 0) {
      var CharAfterdot = (len + 1) - index;
      if (CharAfterdot > 3) {
        return false;
      }
    }

  }
  return true;
}
	
	function checkamount(obj){
	
		
	var num = parseFloat($(obj).val());
	var cleanNum = num.toFixed(2);
	$(obj).val(cleanNum);
	if(num/cleanNum < 1){
		$('#error').text('Please enter only 2 decimal places, we have truncated extra points');
		}
	
}
function checkInitial(){
$(".amounts_class").on("input", function() {
	if (/^0/.test(this.value)) {
	  this.value = this.value.replace(/^0/, "")
	}
  })
}

// 	function validatePayButton(){
//     let isValidAmount = _validatePayments();
//     let isValidComment = ValidateComments();

//     if(isValidAmount && isValidComment){
//         document.getElementById("payBtn").disabled = false; 
//     }else{
//         document.getElementById("payBtn").disabled = true;
//     }
// }

function _ValidateBankField() {
    setTimeout(function() {

        var _ValidBankInfo = false;

        if ((document.getElementById("amount").value  && document.getElementById("amount").className != "textFL_merch_invalid") 
        && (document.getElementById("comments").value && document.getElementById("comments").className != "textFL_merch_invalid")  
 
        ) {
            _ValidBankInfo = true;

        } else {
            _ValidBankInfo = false;

        }
        if (_ValidBankInfo) {
            document.getElementById("payBtn").disabled = false;
        } else {
            document.getElementById("payBtn").disabled = true;
        }
    }, 200);
}

class FieldValidator {
    constructor(x) {
    }

    static valdBenCode(errMsgFlag) {
        // var invoiceexp = /^[0-9a-zA-Z-/\_]+$/;;
        // var invoiceexp = /^[0-9a-zA-Z\b\_/@(),.\-]+$/;
        var benCodeExp = /^[a-zA-Z0-9]{1,32}$/;
        var beneCodeElement = document.getElementById("beneficiaryCd");
        var benCode = beneCodeElement.value;
        // debugger;
        if (benCode.trim() != "") {
            if (!benCode.match(benCodeExp)) {
                FieldValidator.addFieldError("beneficiaryCd", "Enter valid Benefiary Code.", errMsgFlag);
                document.getElementById('invalid-benCode').innerHTML = "Please enter valid Beneficiary Code";
                return false;
            } else {

                FieldValidator.removeFieldError('beneficiaryCd');
                document.getElementById('invalid-benCode').innerHTML = "";
                return true;
            }
        } else {

            FieldValidator.addFieldError("beneficiaryCd", "Please enter Benefiary Code.", errMsgFlag);
            document.getElementById('invalid-benCode').innerHTML = "";
            return false;
        }
	}
	
	static checkNeft(errMsgFlag){
	var element = document.getElementById("amount");
    var value = parseFloat(element.value.trim());
	if(value.length == 0){
            return true;
        }else{
    if(value < 1 || value > 999999999 || (value.length < 1 )){
		FieldValidator.addFieldError("amount", "Enter valid amount", errMsgFlag);
		document.getElementById("errormessage").innerHTML = "Minimum-1rs. and Maximum amount-up to 999999999 for NEFT transactions";
			
		
           return false;
	}
	else{
		FieldValidator.removeFieldError('amount');
		document.getElementById("errormessage").innerHTML = "";
		
		
	}

	}
}
static checkIMPS(errMsgFlag){
	var element = document.getElementById('amount');
    var value = parseFloat(element.value.trim());
	if(value.length == 0){
            return true;
        }else{
    if(value < 1 || value > 200000 || (value.length < 1 || value.length > 6)){
		FieldValidator.addFieldError("amount", "Enter valid amount", errMsgFlag);
		document.getElementById("errormessage").innerHTML = "Minimum-1rs. and Maximum amount-up to 2 lakhs for IMPS  transactions";
	
       
           return false;
	}
	else{
		FieldValidator.removeFieldError('amount');
		document.getElementById("errormessage").innerHTML = "";
		
	}

	}
}

	
	static checkRTGS(errMsgFlag){
		
	var element = document.getElementById("amount");
    var value = parseFloat(element.value.trim());
	if(value.length == 0){
            return true;
        }else{
	if(value < 200000 || value > 999999999 || (value.length < 6 )){
		FieldValidator.addFieldError("amount", "Enter valid amount", errMsgFlag);
		document.getElementById("errormessage").innerHTML = "Minimum- Above 2 lakhs and Maximum 999999999 Crore accepted for RTGS transactions";	
		
	
           return false;
	}
	else{
		FieldValidator.removeFieldError('amount');
		document.getElementById("errormessage").innerHTML = "";
		
	
	}
	
		}
	}
	static ValidateComments(errMsgFlag,val){
		
		var commentElement = document.getElementById("comments");
		var value = commentElement.value.trim();
         var comment = commentElement.value;
		var commentExp = /^[0-9a-zA-Z\b\s/@,.+:\-]+$/;
		if (value.length > 0) { 
        if(value.length == 0){
            return true;
        }else{
   
		
		if(!comment.match(commentExp)){
			FieldValidator.addFieldError("comments", "Enter valid comments", errMsgFlag);
			document.getElementById("errorComment").innerHTML = "Please Enter Valid Comments";
			
			return false;
		}else{
			FieldValidator.removeFieldError('comments');
			document.getElementById("errorComment").innerHTML = "";
			
			return true;
		}
	}
	}else {
      //  phoneElement.focus();
        document.getElementById('errorComment').innerHTML = "";
     
        return true;
    }
		
		}

		static _validatePayments(errMsgFlag,val){
		
		let tableIndex = $(val).parent().parent().index();
		//console.log(tableIndex);
		var row = val;
	   var cells = val.parentElement.parentElement.cells;
	   let paymentType = $(".nodal_Payment_Type option:selected").eq(tableIndex).val();
	//   console.log(paymentType);
		if(paymentType == "R"){
			FieldValidator.checkRTGS();
	//	validateBankDetailsPayButton();
		//checkInputComment(val.id);
		
		}
		if(paymentType == "N"){
			FieldValidator.checkNeft();
		//	validateBankDetailsPayButton();
		//	checkInputComment(val.id);
			
		}
		if(paymentType == "IMPS"){
			FieldValidator.checkIMPS();
			//validateBankDetailsPayButton();
			//checkInputComment(val.id);
			
		}
		
		
	}

	static valdMerchant(errMsgFlag) {
        var element = document.getElementById("merchant")
        if ((element) != null) {
            if (element.value != "Select Merchant") {
                FieldValidator.removeFieldError('merchant');
                return true;
            } else {
                FieldValidator.addFieldError("merchant", "Select Merchant", errMsgFlag)
                return false;
            }
        } else {
            return true;
        }
    }
	

	
	

  

   
  
    static valdAllFields() {

        var flag = FieldValidator.valdMerchant(true);

        flag = flag && FieldValidator.ValidateComments(true);
		flag = flag && FieldValidator._validatePayments(true);
		flag = flag && FieldValidator.checkRTGS(true);
		flag = flag && FieldValidator.checkIMPS(true);
		flag = flag && FieldValidator.checkNeft(true);
        
    }

    // to show error in the fields
    static addFieldError(fieldId, errMsg, errMsgFlag) {
        var errSpanId = fieldId + "Err";
        var elmnt = document.getElementById(fieldId);
        elmnt.className = "textFL_merch_invalid";
        elmnt.focus();
        if (errMsgFlag) {
            document.getElementById(errSpanId).innerHTML = errMsg;
        }
    }

    // to remove the error
    static removeFieldError(fieldId) {
        var errSpanId = fieldId + "Err";
        document.getElementById(errSpanId).innerHTML = "";
        document.getElementById(fieldId).className = "textFL_merch";
    }
}
 




   



	


	

	
	

  

 
 



function handleChange() {
	getNodalBalance();
	var token = document.getElementsByName("token")[0].value;
	var payId = document.getElementById('payId').value;
	var acquirer = document.getElementById('acquirer').value;
	
	if (acquirer == null || acquirer.trim() == "ALL" || acquirer.trim() == "") {
            alert("Please Select Nodal  Aquirer !");
            return false;
        }
        if (payId == null || payId.trim() == "ALL" || payId.trim() == "") {
            alert("Select atleast one Merchant");
            return false;
        }
		document.getElementById("loadingInner").style.display = "block";
	$
		.ajax({
			url: "getMerchantPayoutAccountDetails",
			timeout: 0,
			type: "POST",
			data: {
				payId: payId,
				acquirer: acquirer,
				token: token,
				"struts.token.name": "token",
			},
			success: function (data) {
				
				document.getElementById("loadingInner").style.display = "none";
				var payId = document.getElementById('payId').value;
	          var acquirer = document.getElementById('acquirer').value;
				
				var result = data;
            if(result!=null){
                var errorFieldMap = data["Invalid request"];
                    if(errorFieldMap !=null){
						$("#currentAcc").hide();
					   $("#virtualAcc").hide();
					   //$("#MyTabSelector").de();
                        var error ;
                        for(key in errorFihieldMap){
                            (error!=null)?( error+','+key):(error=key);
                        }
                        $('#loader-wrapper').hide();
						document.getElementById("loadingInner").style.display = "none";
                        //document.getElementById("benadd").innerHTML= errorFieldMap;
                        var errorjson = JSON.stringify(errorFieldMap);
                        var errorstr = '';
                        $.each($.parseJSON(errorjson), function(k, v) {
                            //console.log(k + ' is ' + v);
                            errorstr +=    k + ' : ' + v + ' , ';
                           // console.log(resultstr);
                        });
						document.getElementById('invalidMessageStr').innerHTML = errorstr + '' + 'Please select a merchant';
						$("#modalInvalidStr").modal('show');
						$("#ShowBankAndVirtualData").hide();
						document.getElementById("loadingInner").style.display = "none";
                       // alert(errorstr);
                        //console.log(JSON.stringify(errorFieldMap));
                    }else{
						if(data.response != ""){
							document.getElementById("invalidRequest").innerHTML=data.response;
                     $('#detailsNotFound').modal('show');
					 $("#ShowBankAndVirtualData").hide();
							//alert(data.response);
							$("#currentAcc").hide();
					   $("#virtualAcc").hide();
					 //  $("#MyTabSelector").hide();
						}else{
						var responseObj =  JSON.parse(data.aaData[0]);
						$("#currentAcc").show();
					   $("#virtualAcc").show(); 
					   $("#ShowBankAndVirtualData").show();
					   //$("#MyTabSelector").show();
			//	console.log(responseObj);
				    $('#sec1c td').eq(0).text(responseObj.currentAccountNumber ? responseObj.currentAccountNumber : 'Not Available');
					$('#sec1c td').eq(1).text(responseObj.currentAccountIfscCode ? responseObj.currentAccountIfscCode : 'Not Available');
					
					$('#sec11c td').eq(0).text(responseObj.beneficiaryAccountNumber ? responseObj.beneficiaryAccountNumber : 'Not Available');
                    $('#sec11c td').eq(1).text(responseObj.beneficiaryCode ? responseObj.beneficiaryCode : 'Not Available');
                    
                    $('#sec2c td').eq(0).text(responseObj.currentAccountBankName ? responseObj.currentAccountBankName : 'Not Available');
					$('#sec2c td').eq(1).text(responseObj.currentAccountHolderName ? responseObj.currentAccountHolderName : 'Not Available');
					
					$('#sec3c td').eq(0).text(responseObj.currentAccountMerchantEmailId ? responseObj.currentAccountMerchantEmailId : 'Not Available');

					$('#sec5c td').eq(0).text(payId ? payId : 'Not Available');
					$('#sec1v0 td').eq(0).text(inrFormat(responseObj.virtualAccountBalance) ? 'INR' + ' ' + inrFormat(responseObj.virtualAccountBalance) : '0.00');
					$('#sec1v td').eq(0).text(responseObj.virtualAccountNumber ? responseObj.virtualAccountNumber : 'Not Available');
					$('#sec1v td').eq(1).text(responseObj.virtualAccountIfscCode ? responseObj.virtualAccountIfscCode : 'Not Available');

					$('#sec11v td').eq(0).text(responseObj.beneficiaryAccountNumber ? responseObj.beneficiaryAccountNumber : 'Not Available');
                    $('#sec11v td').eq(1).text(responseObj.beneficiaryCode ? responseObj.beneficiaryCode : 'Not Available');
                    
                    $('#sec2v td').eq(0).text(responseObj.virtualAccountBankName ? responseObj.virtualAccountBankName : 'Not Available');
					$('#sec2v td').eq(1).text(responseObj.currentAccountHolderName ? responseObj.currentAccountHolderName : 'Not Available');
					
					$('#sec3v td').eq(0).text(responseObj.currentAccountMerchantEmailId ? responseObj.currentAccountMerchantEmailId : 'Not Available');

					// 
					
					// var $el = $("#paymentTypeVC");
					// 		$el.empty(); 
					// 		$.each(responseObj.paymentType, function(value,key) {
					// 		$el.append($("<option></option>")
					// 			.attr("value", value).text(key));
					// 		});

							var $el = $("#paymentType");
							$el.empty(); // remove old options
							$.each(responseObj.paymentType, function(value,key) {
							$el.append($("<option></option>")
								.attr("value", value).text(key));
							});

					}
				}
				}else{
                        
                    }
				
				
				
			},
			error: function (data) {
				alert("Request Failed")

			}
		});

 }
//  function validateBankDetailsPayButton(){
//     let isValidNeft = checkNeft();
// 	let isValidRtgs = checkRTGS();
// 	let isValidImps = checkIMPS();
// 	let isValidBankComment = ValidateComments();
    

//     if((isValidRtgs || isValidNeft || isValidImps ) && isValidBankComment){
//         document.getElementById("payBtn").disabled = false; 
//     }else{
//         document.getElementById("payBtn").disabled = true;
//     }
// }


	function checkNeftPG(id){
	var element = document.getElementById(id);
    var value = parseFloat(element.value.trim());

    if(value < 1 || value > 999999999 || (value.length < 1 )){
		document.getElementById(id).nextElementSibling.innerHTML = "Minimum-1rs. and Maximum amount-up to 999999999 for NEFT transactions";
			
	//alert("Amount can not be exceed by 2 lack")
       
           return false;
	}
	else{
		document.getElementById(id).nextElementSibling.innerHTML = "";
		//document.getElementById("payBtn"+getindex).disabled = false;
	}

	}
	function checkIMPSPG(id){
	var element = document.getElementById(id);
    var value = parseFloat(element.value.trim());

    if(value < 1 || value > 200000 || (value.length < 1 || value.length > 6)){
		document.getElementById(id).nextElementSibling.innerHTML = "Minimum-1rs. and Maximum amount-up to 2 lakhs for IMPS  transactions";
	//	document.getElementById("payBtn").disabled = true;	
	//alert("Amount can not be exceed by 2 lack")
       
           return false;
	}
	else{
		document.getElementById(id).nextElementSibling.innerHTML = "";
		//document.getElementById("payBtn").disabled = false;
	}

	}

	
	function checkRTGSPG(id){
		
	var element = document.getElementById(id);
    var value = parseFloat(element.value.trim());

	if(value < 200000 || value > 999999999 || (value.length < 6 )){
		
		document.getElementById(id).nextElementSibling.innerHTML = "Minimum- Above 2 lakhs and Maximum 9999999999 Crore accepted for RTGS transactions";	
		//document.getElementById("payBtn").disabled = true;
	//alert("Amount should be greater than or equal to 2 lakh in RTGS Payment")
       
           return false;
	}
	else{
		document.getElementById(id).nextElementSibling.innerHTML = "";
	//	document.getElementById("payBtn").disabled = false;
	}
	

	}
		

		

	function _validatePaymentsPG(val){
		
		let tableIndex = $(val).parent().parent().index();
		//console.log(tableIndex);
		var row = val;
	   var cells = val.parentElement.parentElement.cells;
	   let paymentType = $(".nodal_Payment_TypePG option:selected").eq(tableIndex).val();
	//   console.log(paymentType);
		if(paymentType == "R"){
		checkRTGSPG(val.id);
		//checkInputComment(val.id);
		
		}
		if(paymentType == "N"){
			checkNeftPG(val.id);
		//	checkInputComment(val.id);
			
		}
		if(paymentType == "IMPS"){
			checkIMPSPG(val.id);
			//checkInputComment(val.id);
			
		}
		
		
	}
	function checkInitial(){
	$(".amounts_class").on("input", function() {
		if (/^0/.test(this.value)) {
		  this.value = this.value.replace(/^0/, "")
		}
	  })
}
function checkamount(obj){
	
		
	var num = parseFloat($(obj).val());
	var cleanNum = num.toFixed(2);
	$(obj).val(cleanNum);
	if(num/cleanNum < 1){
		$('#error').text('Please enter only 2 decimal places, we have truncated extra points');
		}
	
}
	
	function callSearchResult(){
		$('#nodalSwitch').hide();
		$("#pgSwitch").show();
		
	}
	function callNodalResult(){
		$('#nodalSwitch').show();
		$("#pgSwitch").hide();
	}

	function startPaymentPG(val,getindex) {
		var merchantProvidedId = $('#sec5PG td').eq(0).text();
		var comments = $('#sec7PG td').eq(1).text();
		var paymentType = $('#sec4PG td').eq(1).text();
		var acquirer = $('#sec3PG td').eq(0).text();
		var merchantPayId = $('#sec4PG td').eq(2).text();
		var amount = $('#sec7PG td').eq(0).text();
			

			if (comments == null || comments == ""){
				// TODO Validate Comments.
				alert ("Please Enter Comments");
				return false;
			}
			
			if (paymentType == "Please select" || paymentType == ""){
				alert ("Please Select Payment Type");
				return false;
			}
			// Validate data on keypress.
			/* if(paymentType == "NEFT"){
				checkNeft(amountindex);
			}
			if(paymentType == "RTGS"){
				checkRTGS(amountindex);
			}
			if(paymentType == "IMPS"){
				checkIMPS(amountindex);
			} */
			
			if (amount == null || amount == ""){
				alert ("Please Enter Amount");
				return false;
			}
			
			var token  = document.getElementsByName("token")[0].value;
			
			 //$('#loader-wrapper').show();
			 //document.getElementById("payBtn"+getindex).disabled = true;
			 document.getElementById("loadingInner").style.display = "block";
			 $('#popupPG').hide();
		$.ajax({
			type: "POST",
			timeout : 0,
			url:"initiatePgNodalTransaction",
			data:{
				"merchantProvidedId":merchantProvidedId,
				"comments":comments,
				"paymentType":paymentType,
				"acquirer":acquirer,
				//"merchantPayId":merchantPayId,
				"amount":amount,
				"token":token,
				"struts.token.name": "token"
				},
			success:function(data){
				console.log(data);
				//var response = ((data["Invalid request"] != null) ? (data["Invalid request"].response[0]) : (data.response));
				if(data.response != null && data.response != undefined){
					var response = data.response;
					document.getElementById("statusPayout").innerHTML=data.response;
					$('#payoutStatus').modal('show');
                    //document.getElementById("payBtn"+getindex).disabled = false;
				}
				document.getElementById("loadingInner").style.display = "none";
		    },
			error:function(data){
				alert("Nodal Payout failed.");
				document.getElementById("loadingInner").style.display = "none";
                //document.getElementById("payBtn"+getindex).disabled = false;
			}
		});
			
		};

		function popupPG(val,getindex) {
			console.log("Loading confirmation popup.");
			var table = $('#beneficiaryListResultDataTable').DataTable();
			let tableIndex = $(val).parent().parent().index();
			$('#beneficiaryListResultDataTable').off("click");
			$('#beneficiaryListResultDataTable').on('click', 'td.my_class1', function() {
			var rowIndex = table.cell(this).index().row;
			var rowData = table.row(rowIndex).data();
			console.log(rowData);
			var merchantProvidedCode = rowData.merchantProvidedId ;
			var paymentType = $(".nodal_Payment_TypePG option:selected").eq(tableIndex).val();
			var payId = rowData.merchantPayId ;
			var acquirer = rowData.acquirer ;
			var comments = $('.comments_classPG').eq(tableIndex).val().trim();
			var amount = $('.amounts_classPG').eq(tableIndex).val().trim();
			$('.comments_classPG').eq(tableIndex).val("");
			$('.amounts_classPG').eq(tableIndex).val("");
			
			var token = document.getElementsByName("token")[0].value;
			var myData = {
				token : token,
				"struts.token.name" : "token",
				"merchantProvidedCode" : merchantProvidedCode,
				"paymentType" : paymentType,
				"payId": payId,
				"acquirer" : acquirer,
				"amount" : amount,
				"comments" : comments

			}
			$.ajax({
			    	url: "getBeneficiaryDetails",
			    	timeout : 0,
			    	type : "POST",
			    	data :myData,
			    	success: function(responseObj){
						if(responseObj.response == "" && responseObj.response != undefined){
							$('#sec1PG td').eq(0).text(responseObj.nodalAccountNumber ? responseObj.nodalAccountNumber : 'Not Available');
							$('#sec2PG td').eq(0).text(responseObj.nodalAccountHolderName ? responseObj.nodalAccountHolderName : 'Not Available');
							$('#sec3PG td').eq(0).text(responseObj.acquirer ? responseObj.acquirer : 'Not Available');
	
							$('#sec4PG td').eq(0).text(responseObj.merchantName ? responseObj.merchantName : 'Not Available');
							$('#sec4PG td').eq(1).text(responseObj.paymentType ? responseObj.paymentType : 'Not Available');
							$('#sec4PG td').eq(2).text(responseObj.payId ? responseObj.payId : 'Not Available');
	
							$('#sec5PG td').eq(0).text(responseObj.beneficiaryName ? responseObj.merchantProvidedCode : 'Not Available');
							$('#sec5PG td').eq(1).text(responseObj.beneficiaryIfscCode ? responseObj.beneficiaryAccountNumber : 'Not Available');
	
							$('#sec6PG td').eq(0).text(responseObj.beneficiaryName ? responseObj.beneficiaryName : 'Not Available');
							$('#sec6PG td').eq(1).text(responseObj.beneficiaryIfscCode ? responseObj.beneficiaryIfscCode : 'Not Available');
	
							$('#sec7PG td').eq(0).text(responseObj.amount ? responseObj.amount : 'Not Available');
							$('#sec7PG td').eq(1).text(responseObj.comments ? responseObj.comments : 'Not Available');
							
							$('#popupPG').show();
						}
						else {
							document.getElementById("statusPayout").innerHTML=responseObj.response;
							$('#payoutStatus').modal('show');
						}
			    	},
			    	error: function(xhr, textStatus, errorThrown){
				       alert('request failed');
				    }
			});
 
		});
		
	};
		
	function renderTable() {
		var getindex = 0;
		 var acquirer = document.getElementById("acquirer").value;

		var table = new $.fn.dataTable.Api('#beneficiaryListResultDataTable');
	
		var token = document.getElementsByName("token")[0].value;

		
		 var buttonCommon = {
        exportOptions: {
            format: {
                body: function ( data, column, row, node ) {
                    // Strip $ from salary column to make it numeric
                    return column === 0 ? "'"+data : (column === 1 ? "'" + data: data);
                }
            }
        }
    }; 
		// Values can be : alert (Default), throw, none
		$.fn.dataTable.ext.errMode = 'none'; // This will print error in the console rather than showing alert in browser.
		$('#beneficiaryListResultDataTable').on('error.dt', function (e, settings, techNote, message) {
			console.log(message)
	        alert('Failed to load beneficiaries for nodal payout');
			$("#viewButtonPG").removeAttr("disabled");
			$('#loader-wrapper').hide();
	    }).dataTable(
						{
							
							"footerCallback" : function(row, data, start, end, display) {
								var api = this.api(), data;
								// Remove the formatting to get integer data for summation
								var intVal = function(i) {
									return typeof i === 'string' ? i.replace(/[\,]/g, '') * 1: typeof i === 'number' ? i : 0;
								};

								// Total over all pages
								total = api.column(1).data().reduce(
										function(a, b) {
											return intVal(a) + intVal(b);
										}, 0);

								// Total over this page
								pageTotal = api.column(1, {
									page : 'current'
								}).data().reduce(function(a, b) {
									return intVal(a) + intVal(b);
								}, 0);

								// Update footer
								//$(api.column(10).footer()).html(
									//	'' + inrFormat(pageTotal.toFixed(2) + ' ' + ' '));
										
										
								// Total over all pages
								total = api.column(1).data().reduce(
										function(a, b) {
											return intVal(a) + intVal(b);
										}, 0);

								// Total over this page
								pageTotal = api.column(1, {
									page : 'current'
								}).data().reduce(function(a, b) {
									return intVal(a) + intVal(b);
								}, 0);

								// Update footer
								//$(api.column(11).footer()).html(
									//	'' + inrFormat(pageTotal.toFixed(2) + ' ' + ' '));
										
								
							},
							"columnDefs": [{ 
								className: "dt-body-right",
								"targets": [0, 1, 2, 3, 4, 5, 6]
							}],
								dom : 'BTrftlpi',
								buttons : [
										$.extend( true, {}, buttonCommon, {
											extend: 'copyHtml5',											
											extend: 'copyHtml5',
											exportOptions: {
												columns: [ 0, ':visible' ]
											}
										} ),
									$.extend( true, {}, buttonCommon, {
											extend: 'csvHtml5',
											title : 'Beneficiary List_' + getCurrentTimeStamp(),
											exportOptions : {
												
												columns : [0, 1, 2, 3, 4, 5, 6]
											},
										} ),
									{
										extend : 'pdfHtml5',
										orientation : 'landscape',
										pageSize: 'legal',
										//footer : true,
										title : 'Beneficiary List_' + getCurrentTimeStamp(),
										exportOptions : {
											columns: [0, 1, 2, 3, 4, 5, 6]
										},
										customize: function (doc) {
										    doc.defaultStyle.alignment = 'center';
					     					doc.styles.tableHeader.alignment = 'center';
										  }
									},
									// {
									// 	extend : 'print',
									// 	//footer : true,
									// 	title : 'Beneficiary List',
									// 	exportOptions : {
									// 		columns : [0, 1, 2, 3, 4, 5, 6]
									// 	}
									// },
									{
										extend : 'colvis',
										columns : [0, 1, 2, 3, 4, 5, 6, 7,8,9,10,11]
									} ],

							"ajax" :{
								
								"url" : "beneficiaryNodalSearch",
								"timeout" : 0,
								"type" : "POST",
								"data": function (d){
									return generatePostData(d);
								}
							},
							"fnDrawCallback" : function() {

									 $("#viewButtonPG").removeAttr("disabled");
									 $('#loader-wrapper').hide();
							},
							"bProcessing" : true,
							"bDestroy" : true,
							"bLengthChange" : true,
							"iDisplayLength" : 10,
							"searching" : false,
							"ordering" : false,
							"processing" : true,
							"serverSide" : true,
							"paging" : true,
						    "paginationType": "full_numbers", 
						        "lengthMenu": [[10, 25, 50], [10, 25, 50]],
								//"order" : [ [ 2, "asc" ] ],
						       
						        "columnDefs": [
						            {
						            "type": "html-num-fmt", 
						            "targets": 4,
						            "orderable": true, 
						            "targets": [0,1,2,3,4,5,6]
						            }
						        ], 

 
							"columns" : [
							// {
							// 	"data" : "id",
							// 			"className" : "id text-class",
							// 			"visible" : false
									
							// 		},
							// 	{
							// 	"data" : "custId",
							// 	"className" : "cust_Id text-class"
							// },
							// {
							// 			"data" : "merchantBusinessName",
							// 			"className" : "merchant_Business_Name text-class"
							// 		},
							 {
								"data" : "merchantProvidedId",
								"className" : "merchant_Provided_Id text-class"
							}, 
							{
								"data" : "merchantProvidedName",
								"className" : "merchant_Provided_Name text-class",
								"visible" : false
							},
							{
								"data" : "beneAccountNo",
								"className" : "bene_Account_No text-class"
							},
							{
								"data" : "ifscCode",
								"className" : "ifsc_Code text-class"
							},
							{
								"data" : "bankName",
								"className" : "bank_Name text-class",
								"visible" : false
							},
							// {
							// 			"data" : "aadharNo",
							// 			"className" : "aadhar_number text-class",
							// 			"visible" : false
							// 		},
						
							// {
							// 	"data" : "beneType",
							// 	"className" : "bene_Type text-class"
							// },
							
							{
								"data" : "currencyCd",
								"className" : " currency_Cd text-class",
								"visible" : false
							},
							{
								"data" : "acquirer",
								"className" : "acquirer text-class",
								//"visible" : false
							},
							{
								"data" : "emailId",
								"className" : "emailId text-class",
								"visible" : false
							},
							{
								"data" : "mobileNo",
								"className" : "mobileNo text-class",
								"visible" : false
							},
							// {
							// 	"data" : "beneExpiryDate",
							// 	"className" : "bene_Expiry_Date text-class"
							// },
							// {
							// 	"data" : "merchantPayId",
							// 	"className" : "merchant_Pay_Id text-class",
							// 	//"visible" : false
							// },
							
							// {
							// 	"data" : "beneficiaryCd",
							// 	"visible" : false,
							// 	"className" : "beneficiary_Code text-class"
								
							// },
							
							{
								"data" : null,
								"className" : " center",
								"width" : '4%',
								"orderable" : false,
								"mRender" : function(row) {
											if (true) {
												return createElement(row);
											} else {
												return "";
											}
									},
									
							},{
								"data" : null,
								"className" : "center",
								"width" : '6%',
								"orderable" : false,
								"mRender" : function(row) {
									
											if (true) {
												return '<input type="number"  class="amounts_classPG text-class" placeholder="0.00" min="" max="9999999999.99"  step="0.01" onkeyup="_validatePaymentsPG(this);checkInitial(this)"   onblur="checkamount(this)" id="amountPG'+getindex +'" name="amount"     onkeypress="return isNumberKey(event,this);"><span style="color:red" id="errormessagePG"></span>';
											} else {
												return "";
											}
								    }
							},{
								"data" : null,
								"className" : "center",
								"width" : '8%',
								"orderable" : false,
								"mRender" : function(row) {
									
											if (true) {
												return '<input type="text" minlength="2" maxlength="150" name="comments"  onkeyup="ValidateCommentsPG(commentsPG'+getindex +')"  class="comments_classPG text-class" id="commentsPG'+getindex +'" ><span id="errormessage1" style="color:red;"></span>';
											} else {
												return "";
											}
								    }
							},{
								"data" : null,
								"className" : "center my_class1",
								"width" : '4%',
								"orderable" : false,
								"mRender" : function(row) {
									
											if (true) {
												return '<button class="btn btn-info btn-xs btn-block" class="payBtn_class" id="payBtn'+getindex +'" onclick="popupPG(this,'+getindex++ +')"  >Pay</button>';
											} else {
												return "";
											}
								    }
							},
							{
								"data" : "beneType",
								"className" : "beneType text-class",
								"visible" : false
							},
							]
						});
						
		
			
		
	}
	function createElement(row) {
		var obj = JSON.parse(row.actions.toString());
		var div = $('<div/>');
		var selectTag = $('<select/>').addClass('nodal_Payment_TypePG').append('<option value="1" selected= selected>Select Payment</option>');
		var menu = $('#nodalPaymentType').clone();
		$.each(obj,function(index,data){
			var nodalPaymentTypeKey = "";
			if(data == "NEFT"){
				nodalPaymentTypeKey = "N";
			} else if(data == "RTGS"){
				nodalPaymentTypeKey = "R";
			} else if(data == "IMPS"){
				nodalPaymentTypeKey = "IMPS";
			} else if(data == "FT"){
				nodalPaymentTypeKey = "FT";
			}
			var optiontag = $('<option/>').val(nodalPaymentTypeKey).html(data);
			selectTag.append(optiontag);
		});
		div.append(selectTag);
		return div.html();
	}



	function reloadTable() {
		$("#viewButtonPG").attr("disabled", true);
		var tableObj = $('#beneficiaryListResultDataTable');
		var table = tableObj.DataTable();
		table.ajax.reload();
	}

	function generatePostData(d) {
		var token = document.getElementsByName("token")[0].value;
		var acquirer = document.getElementById("acquirer1").value;
		// var statusType = document.getElementById("statusType").value;
		if(acquirer==''){
			acquirer='ALL'
		}
		
		var obj = {
			acquirer : acquirer,
			beneficiaryType: "PG",
			statusType: "Active",
			draw : d.draw,
			length :d.length,
			start : d.start, 
			token : token,
			"struts.token.name" : "token",
		};

		return obj;
	}
	

	
</script>



<style type="text/css">
	.cust {width: 24%!important; margin:0 5px !important; /*font: bold 10px arial !important;*/}
	.samefnew{
		width: 24%!important;
		margin: 0 5px !important;
		/*font: bold 10px arial !important;*/
	}
	/* .btn {padding: 3px 7px!important; font-size: 12px!important; } */
	.samefnew-btn{
		width: 15%;
		float: left;
		font: bold 11px arial;
		color: #333;
		line-height: 22px;
		margin-left: 5px;
	}
	/*tr td.my_class{color:#000 !important; cursor: default !important; text-decoration: none;}*/
	tr td.my_class{
		cursor: pointer;
	}
	tr td.my_class:hover{
		cursor: pointer !important;
	}
	
	tr th.my_class:hover{
		color: #fff !important;
	}
	
	.cust .form-control, .samefnew .form-control{
		margin:0px !important;
		width: 100%;
	}
	.select2-container{
		width: 100% !important;
	}
	.clearfix:after{
		display: block;
		visibility: hidden;
		line-height: 0;
		height: 0;
		clear: both;
		content: '.';
	}
	#popup{
		position: fixed;
		top:0px;
		left: 0px;
		background: rgba(0,0,0,0.7);
		width: 100%;
		height: 100%;
		z-index:999; 
		display: none;
	}
	.innerpopupDv{
			width: 600px;
		margin: 80px auto;
		background: #fff;
		padding: 3px 10px;
		border-radius: 10px;
	}
	.btn-custom {
		margin-top: 5px;
		height: 27px;
		border: 1px solid #5e68ab;
		background: #5e68ab;
		padding: 5px;
		font: bold 12px Tahoma;
		color: #fff;
		cursor: pointer;
		border-radius: 5px;
	}
	#loader-wrapper .loader-section.section-left, #loader-wrapper .loader-section.section-right{
		background: rgba(225,225,225,0.6) !important;
		width: 50% !important;
	}
	.invoicetable{
		float: none;
	}
	.innerpopupDv h2{
			font-size: 12px;
		padding: 5px;
	}
	.text-class{
		text-align: center !important;
	}
	.odd{
		background-color: #e6e6ff !important;
	}
	.hideElement{
		display: none;
	}
	#errormessage{
		color: red;
		font-size: 10px;
	 
	}
	#errormessage1{
		color: red;
		font-size: 10px;
	 
	}
	#loading {width: 100%;height: 100%;top: 0px;left: 0px;position: fixed;display: block; z-index: 99}
	#loading-image {position: absolute;top: 40%;left: 55%;z-index: 100; width:10%;} 
	
	#loadingInner {width: 100%;height: 100%;top: 0px;left: 0px;position: fixed;display: block; z-index: 99}
	#loading-image-inner {position: absolute;top: 33%;left: 48%;z-index: 100; width:5%;} 
	.sitemessage {
	  display: inline-block;
	  white-space: nowrap;
	  animation: floatText 15s infinite linear;
	  padding-left: 100%; /*Initial offset*/
	}
	.sitemessage:hover {
	  animation-play-state: paused;
	}
	@keyframes floatText {
	  to {
		transform: translateX(-100%);
	  }
	}
	li.ui-state-default.ui-state-hidden[role=tab]:not(.ui-tabs-active) {
    display: none;
}
.ui-state-active a, .ui-state-active a:link, .ui-state-active a:visited {
    color: red !important;
    text-decoration: none;
}
.ui-state-active, .ui-widget-content .ui-state-active, .ui-widget-header .ui-state-active {
    border: 1px solid #2694e8;
    background: #ffffff 50% 50% repeat-x !important;
    font-weight: bold;
    color: #496cb6;
}
.ui-state-default a, .ui-state-default a:link, .ui-state-default a:visited {
    color: #ffffff !important;
    text-decoration: none;
}
.ui-state-default, .ui-widget-content .ui-state-default, .ui-widget-header .ui-state-default {
    border: 1px solid #aed0ea;
	background: #ccc 50% 50% repeat-x !important;
   
   
    font-weight: bold;
    color: white;
    
}
.ui-state-active, .ui-widget-content .ui-state-active, .ui-widget-header .ui-state-active {
  
	background-color: #202f4b !important;
    
}
.ui-state-default, .ui-widget-content .ui-state-default, .ui-widget-header .ui-state-default {
   
    font-size: 18px !important;
    height: 45px !important;
    border-radius: 5% !important;
}
.ui-widget-header {
    border:none !important;
     background-color: #ffffff; 
    color: white; 
    font-weight: bold; 
     font-size: 14px;
}
.ui-tabs .ui-tabs-nav .ui-tabs-anchor {
    float: left;
    padding: 10px 5px !important;
    font-size: 14px;
    text-decoration: none;
}
#loading {width: 100%;height: 100%;top: 0px;left: 0px;position: fixed;display: block; z-index: 99}
#loading-image {position: absolute;top: 40%;left: 55%;z-index: 100; width:10%;} 

#loadingInner {width: 100%;height: 100%;top: 0px;left: 0px;position: fixed;display: block; z-index: 99}
#loading-image-inner {position: absolute;top: 33%;left: 48%;z-index: 100; width:5%;} 
.form-control{
	margin-left:0px !important;
	border: 1px solid #496cb6 !important;
}

table.dataTable tbody th, table.dataTable tbody td.merchant_Provided_Id  {
    padding: 4px 7px;
    font-size: 14px;
    word-break: break-word;
}
table.dataTable tbody th, table.dataTable tbody td.merchant_Provided_Name   {
  padding: 4px 7px;
  font-size: 14px;
  word-break: break-word;
}
	</style>


</head>
<body>

<div id="loader-wrapper" style="width: 100%; height: 100%; display:none;">
	  <div id="loader"></div>
	</div>
	<div id="loadingInner" display="none">
		<img id="loading-image-inner" src="../image/sand-clock-loader.gif"
			alt="BUSY..." />
	</div>

	<div class="modal" id="popupPG" style="overflow-y: auto;">
		<!-- <div > -->
				<!--<div class="wrapper " style="max-height: 590px;"> -->
    
    
					<!-- Navbar -->
				   
					<!-- End Navbar -->
					<div class=" innerpopupDv" >
    
    
					<!-- Navbar -->
				   
					<!-- End Navbar -->
					<!-- <div class="content"> -->
					  <div class="container-fluid">
					   
						<div class="row">
						  
						  
							<div class="card ">
							  <div class="card-header ">
								
								<button style="position: absolute;
								left: 93%;border: none;
								background: none;"  id="closeBtn1PG"><img style="width:20px" src="../image/red_cross.jpg"></button>
							  </div>
							  <div class="card-body ">
								<div class="row">
									<div class="col-md-12 ml-auto mr-auto">
								  <div class="col-lg-4 col-md-6">
									<!--
											  color-classes: "nav-pills-primary", "nav-pills-info", "nav-pills-success", "nav-pills-warning","nav-pills-danger"
										  -->
									<ul class="nav nav-pills nav-pills-rose nav-pills-icons flex-column"  role="tablist">
									  <li class="nav-item" id="listitem">
										<a class="nav-link active" data-toggle="tab" href="#link110PG" role="tablist">
										  <i class="material-icons">people</i> Nodal Details
										</a>
									  </li>
									  <li class="nav-item" id="listitem">
										<a class="nav-link" data-toggle="tab" href="#link111PG" role="tablist">
										  <i class="material-icons">money</i> Beneficiary Transaction Details
										</a>
									  </li>
									  
									</ul>
								  </div>
								  <div class="col-md-8">
									<div class="tab-content">
									  <div class="tab-pane active" id="link110PG">
										
										<h4 style="margin-left:10px;color: #4c55a0 !important;font-weight:600"></h4>
											   <div class="card-body">
											   
												   <table width="100%" class="invoicetable">				
													<tr>
														<th  height="25" colspan="4" align="left" valign="middle">Account Number</th>
													   
													   </tr>
													   <tr id="sec1PG">
														<td height="25" colspan="4" align="left" valign="middle"></td>
													   
													  </tr>
													  <tr>
														<th  height="25" colspan="4" align="left" valign="middle">Account Holder Name</th>
													   
													   </tr>
													   <tr id="sec2PG">
														<td height="25" colspan="4" align="left" valign="middle"></td>
													   
													  </tr>
													  <tr>
														<th  height="25" colspan="4" align="left" valign="middle">Acquirer</th>
													   
													   </tr>
													   <tr id="sec3PG">
														<td height="25" colspan="4" align="left" valign="middle"></td>
													   
													  </tr>
													
													
											   </table>
											  
												 <!-- <label for="cardMask" class="bmd-label-floating">Card Mask</label>
												 <input type="email" class="form-control" id="cardMask"> -->
											   
											   </div>
											 
											 
											 
											
										   
									  </div>
									  <div class="tab-pane" id="link111PG">
										
										  
										 
										 <h4 style="margin-left:10px;color: #4c55a0 !important;font-weight:600"></h4>
											<div class="card-body">
											
												<table width="100%" class="invoicetable">
												
												  
												  <tr>
													<th  align="left" valign="middle">Merchant Name</th>
													<th  align="left" valign="middle">Payment Type</th>
													<th  align="left" valign="middle" style="display: none">Merchant PayID</th>
												   
												   </tr>
												
												   <tr id="sec4PG">
													<td align="left" valign="middle"></td>
													<td align="left" valign="middle"></td>
												   <td align="left" valign="middle" style="display: none;"></td>
												  </tr>
												  <tr>
													<th  align="left" valign="middle">Beneficiary Code</th>
													<th  align="left" valign="middle">Beneficiary Account</th>
												   
												   </tr>
													<tr id="sec5PG">
													<td align="left" valign="middle"></td>
													<td align="left" valign="middle"></td>
													
												  </tr>
												  <tr>
													<th align="left" valign="middle">Beneficiary Name</th>
													<th align="left" valign="middle">Beneficiary IFSC</th>
													
													</tr>
												  <tr id="sec6PG">
													<td align="left" valign="middle"></td>
													<td align="left" valign="middle"></td>
													
													</tr>
													<tr>
														<th align="left" valign="middle">Amount</th>
														<th align="left" valign="middle">Comments</th>
														
														</tr>
													  <tr id="sec7PG">
														<td align="left" valign="middle"></td>
														<td align="left" valign="middle"></td>
														
														</tr>
													
													
													<!-- <tr>
													  <th height="25" colspan="4" align="left" valign="middle"><span>Address</span></th>
			  
													</tr>
													<tr id="address6">
													  <td height="25" colspan="4" align="left" valign="middle"></td>
			  
													</tr> -->
											</table>
					
											  <!-- <label for="cardMask" class="bmd-label-floating">Card Mask</label>
											  <input type="email" class="form-control" id="cardMask"> -->
											
											</div>
										 
										  
										  
										 
										</div>
									 
									</div>
									<div class="card-footer " style="float: right;">
									  <button class="btn btn-danger" id="closeBtnPG">Close<div class="ripple-container"></div></button>
									  <button class="btn btn-success" id="confirmBtnPG">Confirm Payout<div class="ripple-container"></div></button>
									  <!-- <button type="submit" class="btn btn-fill btn-rose" id="closeBtn">Close</button> -->
									</div>
									<!-- <div style="text-align: center;"><button class="btn btn-danger" id="closeBtn">Close</button></div>	 -->
								  </div>
								</div>
							  </div>
							</div>
						  </div>
						</div>
						
					  </div>
					<!-- </div> -->
				   
				  
				</div>
										  
		</div>

	


	<div id='MyTabSelector' >
		<ul>
		  <li><a href="#nodalSettlement" onclick="callNodalResult()">Nodal Settlement</a></li>
		  <li><a href="#pgSettlement" onclick="callSearchResult()">PG Settlement</a></li>
		  <!-- <li><a href="#tabs-3">Tab 2</a></li> -->
		</ul>
		<div class="row" id="nodalSwitch" >

						
			<div class="col-lg-3 col-md-6 col-sm-6">
				<div class="page-headerr">
					<label>Merchant<span
						style="color: red; margin-left: 3px;">*</span></label>
					<s:select name="payId" data-control="select2"
												class="form-select form-select-solid"
								id="payId" headerKey="ALL" headerValue="Select Merchant"
								listKey="payId" listValue="businessName"
								list="merchantList" autocomplete="off"  />
					</div>			
				
			</div>
			<div class="col-lg-3 col-md-6 col-sm-6">
				<div class="page-headerr">
				<label>Nodal Acquirer<span
					style="color: red; margin-left: 3px;">*</span></label>
				<s:select data-control="select2"
												class="form-select form-select-solid"
					list="@com.pay10.commons.util.AcquirerTypeNodal@values()"
					listValue="name" listKey="code" name="acquirer" id="acquirer" headerKey="ALL" headerValue="Select Nodal Acquirer "
					autocomplete="off" value="" />
				</div>
			</div>
			
			<div class="col-sm-2 col-lg-2">
				<button type="button" id="viewButton" onclick="handleChange()" class="btn btn-primary btn-round mt-4 submit_btn" >View</button>
			  </div>
		</div>
		<div class="row" id="pgSwitch" style="display: none;">
	
							
			<div class="col-lg-3 col-md-6 col-sm-6">
				<div class="page-headerr">
				<label>Nodal Acquirer<span
					style="color: red; margin-left: 3px;">*</span></label>
				<s:select data-control="select2"
												class="form-select form-select-solid"
					list="@com.pay10.commons.util.AcquirerTypeNodal@values()"
					listValue="name" listKey="code" name="acquirer1" id="acquirer1" headerKey="ALL " headerValue="Select Nodal Acquirer "
					autocomplete="off" value="" />
				</div>
			</div>
			
			<div class="col-sm-2 col-lg-2">
				<button type="button" id="viewButtonPG"  class="btn btn-primary btn-round mt-4 submit_btn" >View</button>
			  </div>
		</div>
		<div id="nodalSettlement" >
			<div class="row">
				<div class="col-md-12" id="ShowBankAndVirtualData" style="display: none;">
					<div class="col-md-6" >
						<div class="card ">
							<div class="card-header card-header-rose card-header-icon">
							  <div class="card-icon">
								<i class="material-icons">mail_outline</i>
							  </div>
							  <h4 class="card-title" style="font-size: 16px;
							  color: #0271bb;
							  font-weight: 500;">Bank Account Details</h4>
							   <!-- <div class="show_nodal_available_balance" style="display: inline-flex;">
								<h4 class="card-title" style="font-size: 16px;
								color: #0271bb;
								font-weight: 500;" id="getNodalBalance" >Available Balance : </h4>
							
								
								
								</div> -->
							</div>
				
							<div class="card-body ">
								<table width="100%" class="invoicetable">		
									<tr>
										<th  colspan="2" style="text-align: center;" align="left" valign="middle">Available Balance</th>
										
									   </tr>
									
									   <tr >
										<td colspan="2" style="text-align: center;" align="left" valign="middle" id="nodalBalance"><s:property value="%{statistics.nodalBalance}"/></td>
									
									   
									  </tr>			
									<tr>
									  <th  align="left" valign="middle">Current Account Number</th>
									  <th  align="left" valign="middle">IFSC Code</th>
									 
									 </tr>
								  
									 <tr id="sec1c">
									  <td align="left" valign="middle"></td>
									  <td align="left" valign="middle"></td>
									 
									</tr>
									<tr>
										<th  align="left" valign="middle">Beneficiary Account Number</th>
										<th  align="left" valign="middle">Beneficiary Code</th>
									   
									   </tr>
									
									   <tr id="sec11c">
										<td align="left" valign="middle"></td>
										<td align="left" valign="middle"></td>
									   
									  </tr>
									<tr>
									  <th  align="left" valign="middle">Bank Name</th>
									  <th  align="left" valign="middle">Account Holder Name</th>
									 
									 </tr>
									  <tr id="sec2c">
									  <td align="left" valign="middle"></td>
									  <td align="left" valign="middle"></td>
									  
									</tr>
									<tr>
										<th  align="left" valign="middle">Email Id</th>
										<th  align="left" valign="middle">Payment Type</th>
									   
									   </tr>
										<tr id="sec3c">
										<td align="left" valign="middle"></td>
										<td align="left" valign="middle">
											<select name="paymentType" id="paymentType" class="input-control nodal_Payment_Type" autocomplete="off">
												
										</select>
									</td>
										
									  </tr>
									  <tr>
										<th  align="left" valign="middle">Amount</th>
										<th  align="left" valign="middle">Comment</th>
									   
									   </tr>
										<tr id="sec4c">
											
											<td align="left" valign="middle"><input type="number" placeholder="Enter Amount"   class="form-control amounts_class " min="" max="9999999999.99"  step="0.01" onkeyup="_ValidateBankField();FieldValidator._validatePayments(false,this);checkInitial(this)" onblur="checkamount(this)" id="amount" name="amount"     onkeypress="return isNumberKey(event,this);"><br><span id="amountErr" class=""></span><span id="errormessage" style="color:red;"></span></td>
										<td align="left" valign="middle"><input type="text" placeholder="Enter Comment" minlength="2" maxlength="120" name="comments"  onkeyup="_ValidateBankField();FieldValidator.ValidateComments(false);"    class="form-control comments_class " id="comments" ><br><span id="commentsErr" class=""></span><span id="errorComment" style="color:red;font-size: 10px;"></span></td>
										
									  </tr>
									  <tr style="display: none;">
										<th  align="left" valign="middle">Pay Id</th>
										
									   
									   </tr>
										<tr id="sec5c" style="display: none;">
											<td align="left" valign="middle"></td>
										
										
									  </tr>
									
							  </table>
							 
							</div>
							<div class="card-footer text-right">
								<button class="btn btn-primary btn-round mt-4 submit_btn" class="payBtn_class" id="payBtn" onclick="popup()">Pay</button>
							 
							</div>
				
					  
							
						
						
						  </div>
					</div>
				
				</div>
			</div>
			
		</div>
		<div id="pgSettlement">
			<div id = "beneficiaryListDiv" style="overflow:scroll !important;">
				<table id="mainTable" width="100%" border="0" align="center"
					cellpadding="0" cellspacing="0" class="txnf">
					
			
					<tr>
						<td colspan="5" align="left"><h2>&nbsp;</h2></td>
					</tr>
					<tr>
						<td align="left" style="padding: 10px;">
							<div class="scrollD">
								<table id="beneficiaryListResultDataTable" class="" cellspacing="0"
									width="100%">
									<thead>
										<tr class="boxheadingsmall">
											<!-- <th
													style='text-align: center; text-decoration: none !important;'>Id</th>
										
										 <th style='text-align: center'>Merchant Name</th>  -->
											<!-- <th style='text-align: center;text-decoration:none!important;' class="hideElement">Nodal Acc No</th> -->
											<th style='text-align: center'>Beneficiary Code</th>
											<th style='text-align: center'>Beneficiary Name</th>
											<th style='text-align: center'>Beneficiary Account No.</th>
											
											<th style='text-align: center;'>IFSC Code</th>	
											<!-- <th style='text-align: center'>Bene Type</th> -->
											<th style='text-align: center'>Bank Name</th>
											<!-- <th style='text-align: center'>Aadhar Number</th>								 -->
											<th style='text-align: center'>Currency Code</th>	
											<th style='text-align: center'>Acquirer</th>
											<th style='text-align: center'>Email Id</th>
											<th style='text-align: center'>Phone Number</th>	
											<!-- <th style='text-align: center'>Expiry Date</th>	 -->
											<th style='text-align: center'>Payment Type</th>
											<!-- <th style='text-align: center'>Merchant Pay ID</th>	
											 -->
											
										
											<!-- <th style='text-align: center' class="hideElement">Beneficiary Code</th> -->
											<th style='text-align: center'>Amount</th>								
											<th style='text-align: center'>Comments</th>
											<th style='text-align: center'>Pay</th>
											<th style='text-align: center' style="display: none;">Bene Type</th>
											<!-- <th class="hideElement" style='text-align: center;text-decoration:none!important;'>Nodal Acc No</th> -->
											
										</tr>
									</thead>
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
											<!-- <th></th>		 -->
										
											<th></th>				
											<th></th>
											<th></th>
											<th ></th>
											<th ></th>
				
										</tr>
									</tfoot>
								</table>
							</div>
						</td>
					</tr>
			
				</table>
			  </div>
		</div>
		
	  </div>

	


<div class="modal fade" id="detailsNotFound" role="dialog">
    <div class="modal-dialog">

      <!-- Modal content-->
      <div class="modal-content">
        <div class="modal-header">

        </div>
        <div class="modal-body">
          <p class="enter_otp" id="invalidRequest"></p>
        </div>
        <div class="modal-footer" id="modal_footer">
          <button type="button" class="btn btn-primary btn-round mt-4 submit_btn" data-dismiss="modal">Ok</button>
        </div>
      </div>

    </div>
  </div>


<div class="modal" id="popup" style="overflow-y: auto;">
	<!-- <div > -->
			<!--<div class="wrapper " style="max-height: 590px;"> -->


				<!-- Navbar -->
			   
				<!-- End Navbar -->
				<div class=" innerpopupDv" >


				<!-- Navbar -->
			   
				<!-- End Navbar -->
				<!-- <div class="content"> -->
				  <div class="container-fluid">
				   
					<div class="row">
					  
					  
						<div class="card ">
						  <div class="card-header ">
							
							<button style="position: absolute;
							left: 93%;border: none;
							background: none;"  id="closeBtn1"><img style="width:20px" src="../image/red_cross.jpg"></button>
						  </div>
						  <div class="card-body ">
							<div class="row">
								<div class="col-md-12 ml-auto mr-auto">
							  <div class="col-lg-4 col-md-6">
								<!--
										  color-classes: "nav-pills-primary", "nav-pills-info", "nav-pills-success", "nav-pills-warning","nav-pills-danger"
									  -->
								<ul class="nav nav-pills nav-pills-rose nav-pills-icons flex-column"  role="tablist">
								  <li class="nav-item" id="listitem">
									<a class="nav-link active" data-toggle="tab" href="#link110" role="tablist">
									  <i class="material-icons">people</i> Nodal Details
									</a>
								  </li>
								  <li class="nav-item" id="listitem">
									<a class="nav-link" data-toggle="tab" href="#link111" role="tablist">
									  <i class="material-icons">money</i> Beneficiary Transaction Details
									</a>
								  </li>
								  
								</ul>
							  </div>
							  <div class="col-md-8">
								<div class="tab-content">
								  <div class="tab-pane active" id="link110">
									
									<h4 style="margin-left:10px;color: #4c55a0 !important;font-weight:600"></h4>
										   <div class="card-body">
										   
											   <table width="100%" class="invoicetable">				
												<tr>
													<th  height="25" colspan="4" align="left" valign="middle">Account Number</th>
												   
												   </tr>
												   <tr id="sec1">
													<td height="25" colspan="4" align="left" valign="middle"></td>
												   
												  </tr>
												  <tr>
													<th  height="25" colspan="4" align="left" valign="middle">Account Holder Name</th>
												   
												   </tr>
												   <tr id="sec2">
													<td height="25" colspan="4" align="left" valign="middle"></td>
												   
												  </tr>
												  <tr>
													<th  height="25" colspan="4" align="left" valign="middle">Acquirer</th>
												   
												   </tr>
												   <tr id="sec3">
													<td height="25" colspan="4" align="left" valign="middle"></td>
												   
												  </tr>
												
												
										   </table>
										  
											 <!-- <label for="cardMask" class="bmd-label-floating">Card Mask</label>
											 <input type="email" class="form-control" id="cardMask"> -->
										   
										   </div>
										 
										 
										 
										
									   
								  </div>
								  <div class="tab-pane" id="link111">
									
									  
									 
									 <h4 style="margin-left:10px;color: #4c55a0 !important;font-weight:600"></h4>
										<div class="card-body">
										
											<table width="100%" class="invoicetable">
											
											  
											  <tr>
												<th  align="left" valign="middle">Merchant Name</th>
												<th  align="left" valign="middle">Payment Type</th>
												<th  align="left" valign="middle" style="display: none">Merchant PayID</th>
											   
											   </tr>
											
											   <tr id="sec4">
												<td align="left" valign="middle"></td>
												<td align="left" valign="middle"></td>
											  
											  </tr>
											  <tr>
												<th  align="left" valign="middle">Beneficiary Code</th>
												<th  align="left" valign="middle">Beneficiary Account</th>
											   
											   </tr>
												<tr id="sec5">
												<td align="left" valign="middle"></td>
												<td align="left" valign="middle"></td>
												
											  </tr>
											  <tr>
												<th align="left" valign="middle">Beneficiary Name</th>
												<th align="left" valign="middle">Beneficiary IFSC</th>
												
												</tr>
											  <tr id="sec6">
												<td align="left" valign="middle"></td>
												<td align="left" valign="middle"></td>
												
												</tr>
												<tr>
													<th align="left" valign="middle">Amount</th>
													<th align="left" valign="middle">Comments</th>
													
													</tr>
												  <tr id="sec7">
													<td align="left" valign="middle"></td>
													<td align="left" valign="middle"></td>
													
													</tr>
													<tr>
														<th  align="left" valign="middle" style="display: none">Merchant PayID</th>
												
														
														</tr>
													  <tr id="sec8">
														<td align="left" valign="middle" style="display: none;"></td>
														
														
														</tr>
												
											
										</table>
										</div>
									 
									</div>
								 
								</div>
								<div class="card-footer " style="float: right;">
								  <button class="btn btn-danger" id="closeBtn">Close<div class="ripple-container"></div></button>
								  <button class="btn btn-success" id="confirmBtn">Confirm Payout<div class="ripple-container"></div></button>
								  <!-- <button type="submit" class="btn btn-fill btn-rose" id="closeBtn">Close</button> -->
								</div>
								<!-- <div style="text-align: center;"><button class="btn btn-danger" id="closeBtn">Close</button></div>	 -->
							  </div>
							</div>
						  </div>
						</div>
					  </div>
					</div>
					
				  </div>
				<!-- </div> -->
			   
			  
			</div>
									  
	</div>


	
	<div class="modal fade" id="modalInvalidStr" role="dialog">
		<div class="modal-dialog">
	
		  <!-- Modal content-->
		  <div class="modal-content">
			<div class="modal-header">
	
			</div>
			<div class="modal-body">
			  <p class="addbene" id="invalidMessageStr"></p>
			</div>
			<div class="modal-footer" id="modal_footer">
			  <button type="button" class="btn btn-primary btn-round mt-4 submit_btn" data-dismiss="modal">Ok</button>
			</div>
		  </div>
	
		</div>
	  </div>
	  <div class="modal fade" id="payoutStatus" role="dialog">
		<div class="modal-dialog">
	
		  <!-- Modal content-->
		  <div class="modal-content">
			<div class="modal-header">
	
			</div>
			<div class="modal-body">
			  <p class="payout" id="statusPayout"></p>
			</div>
			<div class="modal-footer" id="modal_footer">
			  <button type="button" class="btn btn-primary btn-round mt-4 submit_btn" data-dismiss="modal">Ok</button>
			</div>
		  </div>
	
		</div>
	  </div>
	<script>
		$(document).ready(function(){
	
		$('#closeBtn').click(function(){
			$('#popup').hide();
		});
		$('#closeBtn1').click(function(){
			$('#popup').hide();
		});
		$('#closeBtnPG').click(function(){
			$('#popupPG').hide();
		});
		$('#closeBtn1PG').click(function(){
			$('#popupPG').hide();
		});
		
		
		$('#confirmBtn').click(function(){
			startPayment();
		});
		$('#confirmBtnPG').click(function(){
			startPaymentPG();
		});
		
		
	
	});


	function startPayment(val,getindex) {
		//debugger;
		var merchantProvidedId = $('#sec5 td').eq(0).text();
		var comments = $('#sec7 td').eq(1).text();
		var paymentType = $('#sec4 td').eq(1).text();
		var amount = $('#sec7 td').eq(0).text();
		var merchantPayId = $('#sec8 td').eq(0).text();
		
			

			if (comments == null || comments == ""){
				// TODO Validate Comments.
				alert ("Please Enter Comments");
				return false;
			}
			
			if (paymentType == "Please select" || paymentType == ""){
				alert ("Please Select Payment Type");
				return false;
			}
		
			if (amount == null || amount == ""){
				alert ("Please Enter Amount");
				return false;
			}
			// if(!passwordVal.match(passwordRejex)){
			// 	document.getElementById(id).nextElementSibling.innerHTML = "Please Enter Valid Comments";
			// 	checkPassword  = false;
				
			// }else{
			// 	document.getElementById(id).nextElementSibling.innerHTML = "";
			// 	checkPassword  = true;
			// }
			
			var token  = document.getElementsByName("token")[0].value;
			
			 //$('#loader-wrapper').show();
			 //document.getElementById("payBtn"+getindex).disabled = true;
			 document.getElementById("loadingInner").style.display = "block";
			 $('#popup').hide();
		$.ajax({
			type: "POST",
			timeout : 0,
			url:"initiateNodalTransaction",
			data:{
				"merchantProvidedId":merchantProvidedId,
				"comments":comments,
				"paymentType":paymentType,
				"amount":amount,
				"token":token,
				"merchantPayId":merchantPayId,
				"struts.token.name": "token"
				},
			success:function(data){
				
				//console.log(data);
				var response = ((data["Invalid request"] != null) ? (data["Invalid request"].response[0]) : (data.response));
				if(data.response != null && data.response != undefined){
					var response = data.response;
					document.getElementById("statusPayout").innerHTML=data.response;
					$('#payoutStatus').modal('show');
                    //document.getElementById("payBtn"+getindex).disabled = false;
				}
				document.getElementById("loadingInner").style.display = "none";
		    },
			error:function(data){
				alert("Nodal Payout failed.");
				document.getElementById("loadingInner").style.display = "none";
                //document.getElementById("payBtn"+getindex).disabled = false;
			}
		});
			
		};
	function popup(val,getindex) {
			console.log("Loading confirmation popup.");
			//debugger;
		
			var merchantProvidedCode = $('#sec11c td').eq(1).text();
			
			
			var payId = document.getElementById("payId").value;
		    var acquirer = document.getElementById("acquirer").value;
			var comments =  document.getElementById("comments").value;
			var amount = document.getElementById("amount").value;
			var paymentType = document.getElementById("paymentType").value;
			  $('#amount').val("");
			 $('#comments').val("");
			
			var token = document.getElementsByName("token")[0].value;
			var myData = {
				token : token,
				"struts.token.name" : "token",
				 "merchantProvidedCode" : merchantProvidedCode,
				"paymentType" : paymentType,
				"payId": payId,
			    "acquirer" : acquirer,
				"amount" : amount,
				"comments" : comments

			}
			$.ajax({
			    	url: "getBeneficiaryDetails",
			    	timeout : 0,
			    	type : "POST",
			    	data :myData,
			    	success: function(responseObj){
						if(responseObj.response == "" && responseObj.response != undefined){
							$('#sec1 td').eq(0).text(responseObj.nodalAccountNumber ? responseObj.nodalAccountNumber : 'Not Available');
							$('#sec2 td').eq(0).text(responseObj.nodalAccountHolderName ? responseObj.nodalAccountHolderName : 'Not Available');
							$('#sec3 td').eq(0).text(responseObj.acquirer ? responseObj.acquirer : 'Not Available');
	
							$('#sec4 td').eq(0).text(responseObj.merchantName ? responseObj.merchantName : 'Not Available');
							$('#sec4 td').eq(1).text(responseObj.paymentType ? responseObj.paymentType : 'Not Available');
							$('#sec8 td').eq(0).text(responseObj.payId ? responseObj.payId : 'Not Available');
	
							$('#sec5 td').eq(0).text(responseObj.merchantProvidedCode ? responseObj.merchantProvidedCode : 'Not Available');
							$('#sec5 td').eq(1).text(responseObj.beneficiaryIfscCode ? responseObj.beneficiaryAccountNumber : 'Not Available');
	
							$('#sec6 td').eq(0).text(responseObj.beneficiaryName ? responseObj.beneficiaryName : 'Not Available');
							$('#sec6 td').eq(1).text(responseObj.beneficiaryIfscCode ? responseObj.beneficiaryIfscCode : 'Not Available');
	
							$('#sec7 td').eq(0).text(responseObj.amount ? responseObj.amount : 'Not Available');
							$('#sec7 td').eq(1).text(responseObj.comments ? responseObj.comments : 'Not Available');

							
							
							$('#popup').show();
						}
						else {
							document.getElementById("statusPayout").innerHTML=responseObj.response;
							$('#payoutStatus').modal('show');
						}
			    	},
			    	error: function(xhr, textStatus, errorThrown){
				       alert('request failed');
				    }
			});
 
		// });
		
	};


	
		
	</script>
	<script>
		$("#MyPersonalDetails").click(function(){
			$("#currentAcc").show();
		    $("#virtualAcc").hide();
			
		})
		$("#MyContactDetails").click(function(){
			$("#currentAcc").hide();
		    $("#virtualAcc").show();
			
		})
	</script>
		<script src="../js/bootstrap.min.js"></script>
		<script>
			(function ($) {
    $.fn.disableTab = function (tabIndex, hide) {

        // Get the array of disabled tabs, if any
        var disabledTabs = this.tabs("option", "disabled");

        if ($.isArray(disabledTabs)) {
            var pos = $.inArray(tabIndex, disabledTabs);

            if (pos < 0) {
                disabledTabs.push(tabIndex);
            }
        }
        else {
            disabledTabs = [tabIndex];
        }

        this.tabs("option", "disabled", disabledTabs);

        if (hide === true) {
            $(this).find('li:eq(' + tabIndex + ')').addClass('ui-state-hidden');
        }

        // Enable chaining
        return this;
    };

    $.fn.enableTab = function (tabIndex) {
                $(this).find('li:eq(' + tabIndex + ')').removeClass('ui-state-hidden');
        this.tabs("enable", tabIndex);
        return this;
        
        /* Old way, not really necessary

        // Get the array of disabled tabs, if any
        var disabledTabs = this.tabs("option", "disabled");

        var pos = $.inArray(tabIndex, disabledTabs);

        // If the tab we want is in the disabled list, remove it
        if (pos > -1) {
            disabledTabs.splice(pos);

            // Remove the hidden class just in case
            $(this).find('li:eq(' + tabIndex + ')').removeClass('ui-state-hidden');

            // Set the list of disabled tabs, without the one we just enabled
            this.tabs("option", "disabled", disabledTabs);
        }

        // Enable chaining
        return this;
        */
    };


})(jQuery);
$('#MyTabSelector').tabs();

		</script>
</body>
</html>