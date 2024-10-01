
$(document).ready(function(){
	
  // Initialize select2
  $(".merchantPayId").select2();
  
	  
});

function _ValidateField(){
	setTimeout(function(){
    //console.log(document.getElementById("radio1").checked)
    //console.log(document.getElementById("radio2").checked)
    //console.log("Working");
    var _ValidProductInfo = false;
    var _ValidCustInfo = false;

    //console.log(this.activeTab);
   
        if( 
        (document.getElementById("invoiceNo").value && document.getElementById("invoiceNo").className != "textFL_merch_invalid") 
        && (document.getElementById("proInvExpiresDay").value && document.getElementById("proInvExpiresDay").className != "textFL_merch_invalid")
        && (document.getElementById("proInvAmount").value && document.getElementById("proInvAmount").className != "textFL_merch_invalid")
		&& (document.getElementById("proInvProductName").value && document.getElementById("proInvProductName").className != "textFL_merch_invalid")
		&& (document.getElementById("proInvTotalAmount").value && document.getElementById("proInvTotalAmount").className != "textFL_merch_invalid")
	
        ){
            _ValidCustInfo = true;
            _ValidProductInfo = true;
        }else{
            _ValidCustInfo = false;
            _ValidProductInfo = false;
        }		
    

    // if(
    //     (document.getElementById("productName").value && document.getElementById("productName").className != "textFL_merch_invalid")
    //     && (document.getElementById("expiresDay").value && document.getElementById("expiresDay").className != "textFL_merch_invalid")
    //     && (document.getElementById("amount").value && document.getElementById("amount").className != "textFL_merch_invalid")
    // ){
    //     _ValidProductInfo = true;
    // }else{
    //     _ValidProductInfo = false;
    // }

    if( _ValidProductInfo && _ValidCustInfo){
        document.getElementById("btnSave").disabled = false;
    }else if( _ValidProductInfo){
        document.getElementById("btnSave").disabled = false;
    }else{
        document.getElementById("btnSave").disabled = true;
	}
},200);
}

		
	function changeCurrencyMap() {
		var token = document.getElementsByName("token")[0].value;
		var emailId = document.getElementById("merchant").value;
		$.ajax({
			url : 'setMerchantCurrency',
			type : 'post',
			data : {
				emailId : emailId,
				currency : document.getElementById("proInvCurrencyCode").value,
				token : token
			},
			success : function(data) {
				var dataValue = data.currencyMap;
				var currenyMapDropDown = document.getElementById("proInvCurrencyCode");
				var test = "";
				var parseResponse = '<select>';
				for (index in dataValue) {
					var key = dataValue[index];
					parseResponse += "<option value = "+index+">" + key + "</option> ";
				
				}
				parseResponse += '</select>';
				test += key;
				currenyMapDropDown.innerHTML = parseResponse;
			},
			error : function(data) {
				alert("Something went wrong, so please try again.");
			}
		});
	}
	class FieldValidator{
		constructor(x){
			//this.x = x;
		}

		
		
	

		static valdInvoiceNo(errMsgFlag) {
			// var invoiceexp = /^[0-9a-zA-Z-/\_]+$/;;
			//var invoiceexp = /^[0-9a-zA-Z\b\_/@(),.\-]+$/;
			var invoiceexp = /^[0-9a-zA-Z\b\_-\s\+.]{1,45}$/;
			var invoiceElement = document.getElementById("invoiceNo");
			var invoiceValue = invoiceElement.value;
			//debugger;
			if (invoiceValue.trim() != "") {
				if (!invoiceValue.match(invoiceexp)) {
					FieldValidator.addFieldError("invoiceNo", "Enter valid Invoice no.", errMsgFlag);
	
					//this._ValidateField();
					return false;
				} else {
	
					//this._ValidateField();
					FieldValidator.removeFieldError('invoiceNo');
					return true;
				}
			} else {
	
				//this._ValidateField();
				FieldValidator.addFieldError("invoiceNo", "Please enter Invoice No.", errMsgFlag);
				return false;
			}
		}

	
		
		static valdProductName(errMsgFlag){
			var productNameElement = document.getElementById("proInvProductName"); 
			var value = productNameElement.value.trim();
			if (value.length>0) {
				var productName = productNameElement.value;
				var regex = /^[ A-Za-z0-9]*$/;
				if (!productName.match(regex)) {
					//this._ValidateField();
					FieldValidator.addFieldError("proInvProductName", "Enter valid product name", errMsgFlag);
					return false;
				}else{
					//this._ValidateField();
					FieldValidator.removeFieldError('proInvProductName');
					return true;
				}
			}else{
				//this._ValidateField();
				FieldValidator.addFieldError("proInvProductName", "Please enter Product Name.", errMsgFlag);
				//FieldValidator.removeFieldError('productName');
			return true;
			}	
		}
		
		static valdProductDesc(errMsgFlag){
			var productDescElement = document.getElementById("proInvpPoductDesc");
			var value = productDescElement.value.trim(); 
			if ( value.length>0) {
				var productDesc = productDescElement.value;
				var regex = /^[ A-Za-z0-9]*$/;
				if (!productDesc.match(regex)) {
					FieldValidator.addFieldError("proInvpPoductDesc", "Enter valid product description", errMsgFlag);
					return false;
				}else{
					FieldValidator.removeFieldError('proInvpPoductDesc');
					return true;
				}
			}else{FieldValidator.removeFieldError('proInvpPoductDesc');
			return true;	
			}	
		}
		
		
	
		
		static valdExpDayAndHour(errMsgFlag){
			var expDayElement = document.getElementById("proInvExpiresDay");
			var expHorElement = document.getElementById("proExpiresHour");
			var days = expDayElement.value.trim();
			var hors = expHorElement.value.trim();
			//console.log("Length : "+days.length)
			//console.log("Days : "+days)
			if (days.length > 0 && parseInt(days)>=0) {
				if(parseInt(days) > 31){
					FieldValidator.addFieldError("proInvExpiresDay", "Enter valid no. of days (Max:31)", errMsgFlag);
					//this._ValidateField();
					return false;
				}else{
					FieldValidator.removeFieldError('proInvExpiresDay');
					//this._ValidateField();
					if(hors.length > 0 && parseInt(hors)>=0){

						if(parseInt(hors) > 24 || parseInt(hors)<0){
							FieldValidator.addFieldError("proExpiresHour", "Enter valid no. of hours (Max:24)", errMsgFlag);
							return false;
						}
							else if(parseInt(days)==0 && parseInt(hors)==0){
								FieldValidator.addFieldError("proInvExpiresDay", "Enter valid no. of days (Max:31) or hours (Max:24)", errMsgFlag);
								//this._ValidateField();
								return false;
						}

						FieldValidator.removeFieldError('proExpiresHour');
						return true;
					}
					else{
						FieldValidator.addFieldError("proExpiresHour", "Enter valid no. of hours", errMsgFlag);
						return false;
					}
				}
			}else{
				FieldValidator.addFieldError("proInvExpiresDay", "Enter valid no. of days", errMsgFlag);
				//this._ValidateField();
				return false;
			}
		}
		
		static valdMerchant(errMsgFlag){
			var element =document.getElementById("merchant") 
			if ((element) != null) {
				if (element.value != "Select Merchant") {
					FieldValidator.removeFieldError('merchant');
					return true;
				} else {
					FieldValidator.addFieldError("merchant", "Select Merchant", errMsgFlag)
					return false;
				}
			}else{
				return true;
			}
		}
		
		static valdSrvcChrg(errMsgFlag){
			var element = document.getElementById('proInvServiceCharge');
			var value = parseFloat(element.value.trim());
			
			if(element.value.indexOf(".") > -1){
				var index = element.value.indexOf(".");
				if((element.value.substr(index, element.value.length)).length>3){
					FieldValidator.addFieldError("proInvServiceCharge", "Enter valid Service Charge", errMsgFlag)
					return false;
				}
			}
			if(parseFloat(value)>= parseFloat(0)){
				FieldValidator.removeFieldError('proInvServiceCharge');
				return true;
			}else{
				FieldValidator.addFieldError("proInvServiceCharge", "Enter valid Service Charge", errMsgFlag)
				return false;
			}
		}
		
			static valdGst(errMsgFlag){
			var element = document.getElementById('proInvServiceCharge');
			var value = parseFloat(element.value.trim());
			
			if(element.value.indexOf(".") > -1){
				var index = element.value.indexOf(".");
				if((element.value.substr(index, element.value.length)).length>3){
					FieldValidator.addFieldError("proInvServiceCharge", "Enter valid Service Charge", errMsgFlag)
					return false;
				}
			}
			if(parseFloat(value)>= parseFloat(0)){
				FieldValidator.removeFieldError('proInvServiceCharge');
				return true;
			}else{
				FieldValidator.addFieldError("proInvServiceCharge", "Enter valid Service Charge", errMsgFlag)
				return false;
			}
		}
		
		//valdiating the amount of the product
		
	
		
        static valdCurrCode(errMsgFlag) {
            var currencyCodeElement = document.getElementById("proInvCurrencyCode");
            if (currencyCodeElement.value == "Select Currency") {
                FieldValidator.addFieldError("proInvCurrencyCode", "Select Currency Type", errMsgFlag)
                return false;
            } else {
                FieldValidator.removeFieldError('proInvCurrencyCode');
                return true;
            }
    
		}
		
	
		
		
		static valdReturnUrl(errMsgFlag){
			var urlRegex = "^(https?|http?|www.?)[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
			var element = document.getElementById("proInvReturnUrl");
			var value = element.value.trim();
			
			if(value.length	>0){
				if(!(value).match(urlRegex)){
					FieldValidator.addFieldError('proInvReturnUrl', "Enter valid return url", errMsgFlag);
					return false;
				}else{FieldValidator.removeFieldError('proInvReturnUrl');
				return true;
				}
			}else{FieldValidator.removeFieldError('proInvReturnUrl');
			return true;
			}	
		}
		
		
		
		static valdQty(errMsgFlag){
			var qtyRgx = "^([1-9][0-9]+|[1-9])$";
			var element = document.getElementById("proInvQuantity");
			var value = element.value.trim();
			if(value==0){
				FieldValidator.addFieldError('proInvQuantity', "Enter valid quantity", errMsgFlag);
				return false;
			}
			if(value.length	>0){
				if(!(value).match(qtyRgx)){
					FieldValidator.addFieldError('proInvQuantity', "Enter valid quantity", errMsgFlag);
					return false;
				}else{FieldValidator.removeFieldError('proInvQuantity');
				return true;
				}
			}else{FieldValidator.addFieldError('proInvQuantity',"Enter valid quantity", errMsgFlag);
			return false;
			}	
		}
		
		
		static valdAllFields(){
			
			var flag = FieldValidator.valdMerchant(true);

			flag = flag && FieldValidator.valdInvoiceNo(true);
            flag = flag && FieldValidator.valdReturnUrl(true);
            flag = flag && FieldValidator.valdProductName(true);
            flag = flag && FieldValidator.valdProductDesc(true);
            flag = flag && FieldValidator.valdExpDayAndHour(true);
            flag = flag && FieldValidator.valdCurrCode(true);
            flag = flag && FieldValidator.valdQty(true);
			//flag = flag && FieldValidator.valdAmount(true);
			//flag = flag && FieldValidator.valdTotalAmount(true);
			//console.log("radio1 is pressed");
			
			//flag = flag && FieldValidator.valdSrvcChrg(true);
			//flag = flag && FieldValidator.valdRecptMobileNo(true);
			//flag = flag && FieldValidator.valdRecptMsg(true);
			//submitting form
			if(flag){
			document.forms["frmInvoice"].submit();}
            }
		
		//to show error in the fields
		static addFieldError(fieldId, errMsg, errMsgFlag){
			var errSpanId = fieldId+"Err";
			var elmnt = document.getElementById(fieldId);
			elmnt.className = "textFL_merch_invalid";
			elmnt.focus();
			if(errMsgFlag){
				document.getElementById(errSpanId).innerHTML = errMsg;
			}
		}
		
		// to remove the error 
		static removeFieldError(fieldId){
			var errSpanId = fieldId+"Err";
			document.getElementById(errSpanId).innerHTML = "";
			document.getElementById(fieldId).className = "textFL_merch";
		}
	}

	function sum() {
	    var amounts = document.getElementById('proInvAmount').value;
	    if(amounts == "") {
	    	amounts = "0.00";
	    }
		var gstVal = document.getElementById('proInvGst').value;
		if(serviceChargeVal == "" || serviceChargeVal == "0") {
	    	serviceChargeVal = "0";
	    }
 
	    var serviceChargeVal = document.getElementById('proInvServiceCharge').value;
	    if(serviceChargeVal == "" || serviceChargeVal == ".") {
	    	serviceChargeVal = "0";
	    }
		 
		
		var gstCal = parseFloat(amounts)+ parseFloat(parseFloat(amounts)*parseFloat(gstVal))/100;
		
		
		
	    var Quantity = document.getElementById('proInvQuantity').value;
	    var result =  parseFloat(Quantity)* parseFloat(gstCal);	

	    if (!isNaN(result)) {
	       document.getElementById('proInvTotalAmount').value =parseFloat(result + parseFloat(serviceChargeVal)).toFixed(2);
		   valdTotalAmount();
		   valdAmount();
	    }
	}
	function valdAmount() {
		setTimeout(function(){
		var element = document.getElementById('proInvAmount');
		var value = parseFloat(element.value.trim());
		if(value < 1 || value > 9999999.99 || (value.length < 1 || value.length > 10)){
			//document.getElementById("totalAmount").className = "textFL_merch_invalid"
			document.getElementById('proInvAmountErr').innerHTML = "Please enter Valid Amount";
			$('#proInvAmount').addClass('textFL_merch_invalid');
			$('#proInvAmount').removeClass('textFL_merch');
			//document.getElementById("totalAmount").className = "textFL_merch_invalid";
			totalAmountErr
			//FieldValidator.addFieldError("totalAmount", "Enter valid amount", errMsgFlag)
			   return false;
		   } 
		if (element.value.indexOf(".") > -1) {
			var index = element.value.indexOf(".");
			if ((element.value.substr(index, element.value.length)).length > 3) {
				document.getElementById('proInvAmountErr').innerHTML = " enter valid amount";
				$('#proInvAmount').addClass('textFL_merch_invalid');
				$('#proInvAmount').removeClass('textFL_merch');
				//this._ValidateField();
				//FieldValidator.addFieldError("amount", "Enter valid amount", errMsgFlag)
				return false;
			}
		}
		if (!value < parseFloat(value)) { // change here for custom amount
			//this._ValidateField();
			//FieldValidator.removeFieldError('amount');
			document.getElementById('proInvAmountErr').innerHTML = "";
				$('#proInvAmount').removeClass('textFL_merch_invalid');
				$('#proInvAmount').addClass('textFL_merch');
			return true;
		} else {
			//this._ValidateField();
			document.getElementById('proInvAmountErr').innerHTML = "Please enter Amount";
			$('#proInvAmount').addClass('textFL_merch_invalid');
			$('#proInvAmount').removeClass('textFL_merch');
			//FieldValidator.addFieldError("amount", "Enter amount", errMsgFlag)
			return false;
		}
	},100);
	}
	function valdTotalAmount() {

		// static valdTotalAmount() {
			var element = document.getElementById('proInvTotalAmount');
			var value = parseFloat(element.value.trim());
			//console.log("working");
		   if(value < 1 || value > 9999999.99 || (value.length < 1 || value.length > 10)){
			//document.getElementById("totalAmount").className = "textFL_merch_invalid"
		//	document.getElementById('proInvTotalAmountErr').innerHTML = "Total Amount is Invalid";
			$('#proInvTotalAmount').addClass('textFL_merch_invalid');
			$('#proInvTotalAmount').removeClass('textFL_merch');
			//document.getElementById("totalAmount").className = "textFL_merch_invalid";
			
			//FieldValidator.addFieldError("totalAmount", "Enter valid amount", errMsgFlag)
			   return false;
		   } 
			if (element.value.indexOf(".") > -1) {
				var index = element.value.indexOf(".");
				if ((element.value.substr(index, element.value.length)).length > 3) {
					//this._ValidateField();
					document.getElementById('proInvTotalAmountErr').innerHTML = " enter valid amount";
					$('#proInvTotalAmount').addClass('textFL_merch_invalid');
					$('#proInvTotalAmount').removeClass('textFL_merch');
					//document.getElementById("totalAmount").className = "textFL_merch_invalid"
					//FieldValidator.addFieldError("totalAmount", "Enter valid amount", errMsgFlag)
					return false;
				}
			}
			if (!value < parseFloat(value)) { // change here for custom amount
				//this._ValidateField();
				document.getElementById('proInvTotalAmountErr').innerHTML = "";
				$('#proInvTotalAmount').removeClass('textFL_merch_invalid');
				$('#proInvTotalAmount').addClass('textFL_merch');
			   // FieldValidator.removeFieldError('totalAmount');
				return true;
			} else {
				//this._ValidateField();
				//document.getElementById('proInvTotalAmountErr').innerHTML = "Please enter valid Amount";
				$('#proInvTotalAmount').addClass('textFL_merch_invalid');
				$('#proInvTotalAmount').removeClass('textFL_merch');
			   // document.getElementById("totalAmount").className = "textFL_merch_invalid"
				//FieldValidator.addFieldError("totalAmount", "Enter amount", errMsgFlag)
				return false;
			}
		// }
	
	}
	
	function gstReverseSet() {
		setTimeout(function(){
		var totalAmounts = document.getElementById('proInvTotalAmount').value;
		if (totalAmounts == "") {
			totalAmounts = "0.00";
		}
		var amtQuantity = document.getElementById('proInvQuantity').value;
		if (amtQuantity == "" || amtQuantity == ".") {
			amtQuantity = "0.00";
		}
		var gstAmounts = document.getElementById('proInvGst').value;
		if (gstAmounts == "28") {
			gstAmounts = "1.28";
		} else if (gstAmounts == "18") {
			gstAmounts = "1.18";
		} else if (gstAmounts == "12") {
			gstAmounts = "1.12";
		} else if (gstAmounts == "5") {
			gstAmounts = "1.05";
		} else {
			gstAmounts = "1";
		}
		if (!isNaN(totalAmounts)) {
		  
			document.getElementById('proInvAmount').value = parseFloat((totalAmounts/(gstAmounts*amtQuantity)).toFixed(2)) ;
			
		}
	}, 100);
	
	}
	
			

	$(document).ready(function() {
		document.getElementById("btnSave").disabled = true;	  
		document.getElementById('crBtn').style="display:none";
		if(window.location.pathname.includes("savePromotionalInvoice")){
			document.getElementById("btnSave").disabled = false;
			
			var urlCopyLink= $('#promoLink').val();
			  
			if(urlCopyLink == "")
			{ 
			document.getElementById('promoLink').style="display:none";
			document.getElementById('copyBtn').style="display:none";
			document.getElementById('btnSave').style="display:block";
			document.getElementById('crBtn').style="display:none";
			}
			else
			{
			document.getElementById('promoLink').style="display:block";
			document.getElementById('copyBtn').style="display:block";
			document.getElementById('btnSave').style="display:none";
			document.getElementById('crBtn').style="display:block";
			//console.log("580");
			
			var ArrStr = '<s:property value="actionMessages"/>';
			var str = ArrStr.toString();
			// if(str.match(/Promotional/g)){
			// document.getElementById('div1').style.display ='block';
			// document.getElementById('proInvoiceHide').style.display ='none';
			// document.getElementById('proNameHide').style.display ='none';
			// document.getElementById('proEmailHide').style.display ='none';
			// document.getElementById('proPhoneHide').style.display ='none';
			// document.getElementById('proCityHide').style.display ='none';
			// document.getElementById('proCountryHide').style.display ='none';
			// document.getElementById('proStateHide').style.display ='none';
			// document.getElementById('proAddressHide').style.display ='none';
			// document.getElementById('proZipHide').style.display ='none';
			// }
			 
			
			}
			
		}
		copyBtn.disabled = !document.queryCommandSupported('copy');
		document.getElementById("copyBtn").addEventListener("click", function(event){
			var copiedLink = document.getElementById('promoLink');
			copiedLink.select();
			document.execCommand('copy');
		});

		
		$('#btnSave').click(function(event) {
			event.preventDefault();
			// Validate file. If invalid then return.
			if(!validateFile()){
				return false;
			}
			FieldValidator.valdAllFields();
		});

		$('#merchant').change(function() {
			changeCurrencyMap();
			$('#spanMerchant').hide();
			$('#currencyCodeloc').hide();
		});

		$('#proInvServiceCharge').on('keyup', function() {
			if (this.value[0] === '.') {
				this.value = '0' + this.value;
			}
		});
		
		$(document).ready(function() {
		    $('#example').DataTable( {
		        dom: 'B',
		        buttons: [
		            'csv'
		        ]
		    });
		});
		
		
			
	});

    $("body").on("click", "#btnSave", function () {
        var allowedFiles = [".csv"];
        var fileUpload = $("#fileUpload");
        var lblError = $("#lblError");
        var regex = new RegExp("([a-zA-Z0-9\s_\\.\-:])+(" + allowedFiles.join('|') + ")$");
        if (!regex.test(fileUpload.val().toLowerCase())) {
            lblError.html("Please upload files having extensions: <b>" + allowedFiles.join(', ') + "</b> only.");
            return false;
        }
        lblError.html('');
        return true;
    });
    
  
	
	$(function(){
		
  $("input[name='proInvServiceCharge']").on('input', function (e) {
    $(this).val($(this).val().replace(/[^0-9]/g, ''));
  });
  $("input[name='proInvQuantity']").on('input', function (e) {
    $(this).val($(this).val().replace(/[^0-9]/g, ''));
  });
  $("input[name='amount']").on('input', function (e) {
    $(this).val($(this).val().replace(/[^0-9.]/g, ''));
  });
  $("input[name='totalAmount']").on('input', function (e) {
	$(this).val($(this).val().replace(/[^0-9.]/g, ''));
});
});

    



function isNumberKey(evt){
    var charCode = (evt.which) ? evt.which : event.keyCode
    if (charCode > 31 && (charCode < 48 || charCode > 57))
        return false;
    return true;
}
function isNumberKeyAmount(evt) {
    var charCode = (evt.which) ? evt.which : event.keyCode
    if (charCode > 31 && (charCode < 48 || charCode > 57))
        return false;
    return true;
}

	var specialKeys = new Array();
	specialKeys.push(8); //Backspace
	specialKeys.push(9); //Tab
	specialKeys.push(46); //Delete
	specialKeys.push(36); //Home
	specialKeys.push(35); //End
	specialKeys.push(37); //Left
	specialKeys.push(39); //Right
	function IsAlphaNumeric(e) {
		var keyCode = e.keyCode == 0 ? e.charCode : e.keyCode;
		var ret = ((keyCode >= 48 && keyCode <= 57) || (keyCode >= 65 && keyCode <= 90) || (keyCode >= 97 && keyCode <= 122) || (specialKeys.indexOf(e.keyCode) != -1 && e.charCode != e.keyCode));
		return ret;
	}

	function lettersOnly(e, t) {
            try {
                if (window.event) {
                    var charCode = window.event.keyCode;
                }
                else if (e) {
                    var charCode = e.which;
                }
                else { return true; }
                if ((charCode > 64 && charCode < 91) || (charCode > 96 && charCode < 123) || charCode == 8 || charCode == 32)
                    return true;
                else
                    return false;
            }
            catch (err) {
                alert(err.Description);
            }
        }



	function validate() {
	$("#file_error").html("");
	$(".demoInputBox").css("border-color","#F0F0F0");
	var file_size = $('#file')[0].files[0].size;
	if(file_size>2097152) {
		$("#file_error").html("File size is greater than 2MB");
		$(".demoInputBox").css("border-color","#FF0000");
		return false;
	} 
	return true;
}

function validateFile(){
	let completeFileName = $('#fileCSV').val();
	
	// If file is empty then return true.
	if(completeFileName == '' || completeFileName == undefined){
		console.log("Empty file name");
		return true;
	}
	
	let fileNameArr = completeFileName.split("\\");
	let fileName = fileNameArr[fileNameArr.length -1];	
	
	// Validate filename.
	var validFilename = /^[a-zA-Z0-9_.() -]+$/i.test(fileName);
	if(!validFilename){
		$("#fileCSV").val('');
		$('#fileUpload').val('')
		alert("Invalid file name");
		return false;
	}

	// Validate file length or size.
	if(!(fileName.length >=5 && fileName.length <= 50)){
		$("#fileCSV").val('');
		$('#fileUpload').val('')
		alert("Invalid Filename length.");
		return false;
	}

	// Validate file extension.
	var validExtension = /\.csv$/i.test(fileName);
	if(!validExtension){
		$("#fileCSV").val('');
		$('#fileUpload').val('')
		alert("Invalid File extension");
		return false;
	}
	
	// Validate file size.
	let fileSize = ($("#fileCSV")[0].files[0].size / (1024 * 1024));
	if(fileSize > 2){
		$("#fileCSV").val('');
		$('#fileUpload').val('')
		alert("Invalid File size");
		return false;
	}
	
	return true;

}
//Added By Sweety

function disableInvoiceNo(checked, tncDisable) {
	var text=$("#messageText1").text();
	
	if(checked){
		if (tncDisable) {
			document.getElementById("tnc").disabled = true;
		}
		$('#invoiceNo').attr('readonly', true);
		
	}
	else{
		document.getElementById("tnc").disabled = false;
		$('#invoiceNo').attr('readonly', false);
	}
	var x = document.getElementById("invoiceNo").readOnly;
	if(x){
		
		addInvoiceNo(x,text);
	}
	
}

function addInvoiceNo(x,text){
	var invoiceNo= $('#invoiceNo').val();
	text = text.replace("#invoiceNo", invoiceNo);
	     $("#messageText").text(text);	
}
	

 
 
