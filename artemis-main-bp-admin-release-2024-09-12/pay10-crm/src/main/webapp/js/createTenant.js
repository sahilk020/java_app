

function _ValidateField(){
    setTimeout(function(){

   
    var _ValidCompanyDetails = false;


   
        if( 
        (document.getElementById("tanentNo").value && document.getElementById("tanentNo").className != "textFL_merch_invalid") 
        && (document.getElementById("cname").value && document.getElementById("cname").className != "textFL_merch_invalid") 
        && (document.getElementById("emailId").value && document.getElementById("emailId").className != "textFL_merch_invalid") 
        && (document.getElementById("phone").value && document.getElementById("phone").className != "textFL_merch_invalid") 
        &&  (document.getElementById("city").value && document.getElementById("city").className != "textFL_merch_invalid")
		&&  (document.getElementById("state").value && document.getElementById("state").className != "textFL_merch_invalid")
		&&  (document.getElementById("zip").value && document.getElementById("zip").className != "textFL_merch_invalid")
		&&  (document.getElementById("pgUrl").value && document.getElementById("pgUrl").className != "textFL_merch_invalid")
		&&  (document.getElementById("websiteUrl").value && document.getElementById("websiteUrl").className != "textFL_merch_invalid")
		&&  (document.getElementById("address").value && document.getElementById("address").className != "textFL_merch_invalid")
		&&  (document.getElementById("comGst").value && document.getElementById("comGst").className != "textFL_merch_invalid")
		&&  (document.getElementById("ifscCode").value && document.getElementById("ifscCode").className != "textFL_merch_invalid")
		&&  (document.getElementById("bankName").value && document.getElementById("bankName").className != "textFL_merch_invalid")
		&&  (document.getElementById("accHolderName").value && document.getElementById("accHolderName").className != "textFL_merch_invalid")
		&&  (document.getElementById("cin").value && document.getElementById("cin").className != "textFL_merch_invalid")
		&&  (document.getElementById("branchName").value && document.getElementById("branchName").className != "textFL_merch_invalid")
		&&  (document.getElementById("panCard").value && document.getElementById("panCard").className != "textFL_merch_invalid")
		&&  (document.getElementById("panName").value && document.getElementById("panName").className != "textFL_merch_invalid")
        &&  (document.getElementById("accountNo").value && document.getElementById("accountNo").className != "textFL_merch_invalid")
        &&  (document.getElementById("hsnSac").value && document.getElementById("hsnSac").className != "textFL_merch_invalid")
        &&  (document.getElementById("tanNumber").value && document.getElementById("tanNumber").className != "textFL_merch_invalid")
        ){
            
            _ValidCompanyDetails = true;
        }else{
            _ValidCompanyDetails = false;
        }		


    if( _ValidCompanyDetails){
        document.getElementById("btnSave").disabled = false;
    }else{
        document.getElementById("btnSave").disabled = true;
    }
},200);
}


class FieldValidator {
    constructor(x) {
        //this.x = x;
    }


    static valdTanentNo(errMsgFlag) {
        // var invoiceexp = /^[0-9a-zA-Z-/\_]+$/;;
        //var invoiceexp = /^[0-9a-zA-Z\b\_/@(),.\-]+$/;
        var tanentexp = /^[A-Za-z]{6}[0-9]{4}/;
        var tanentElement = document.getElementById("tanentNo");
        var tanentValue = tanentElement.value;
        //debugger;
        if (tanentValue.trim() != "") {
            if (!tanentValue.match(tanentexp)) {
                FieldValidator.addFieldError("tanentNo", "Enter valid Tanent no.", errMsgFlag);

                //this._ValidateField();
                return false;
            } else {

                //this._ValidateField();
                FieldValidator.removeFieldError('tanentNo');
                return true;
            }
        } else {

            //this._ValidateField();
            FieldValidator.addFieldError("tanentNo", "Please enter Invoice No.", errMsgFlag);
            return false;
        }
    }
    static valdHsnSac(errMsgFlag) {
        // var invoiceexp = /^[0-9a-zA-Z-/\_]+$/;;
        //var invoiceexp = /^[0-9a-zA-Z\b\_/@(),.\-]+$/;
        var tanentexp =  /^[0-9]{4,8}$/;
        var tanentElement = document.getElementById("hsnSac");
        var tanentValue = tanentElement.value;
        //debugger;
        if (tanentValue.trim() != "") {
            if (!tanentValue.match(tanentexp)) {
                FieldValidator.addFieldError("hsnSac", "Enter valid HSNSac Code.", errMsgFlag);

                //this._ValidateField();
                return false;
            } else {

                //this._ValidateField();
                FieldValidator.removeFieldError('hsnSac');
                return true;
            }
        } else {

            //this._ValidateField();
            FieldValidator.addFieldError("hsnSac", "Please enter HSNSac Code.", errMsgFlag);
            return false;
        }
    }
    static valdTanNumber(errMsgFlag) {
        // var invoiceexp = /^[0-9a-zA-Z-/\_]+$/;;
        //var invoiceexp = /^[0-9a-zA-Z\b\_/@(),.\-]+$/;
        var tanentexp = /^[0-9a-zA-Z\b\:/-\s\.]{2,30}$/;
        var tanentElement = document.getElementById("tanNumber");
        var tanentValue = tanentElement.value;
        //debugger;
        if (tanentValue.trim() != "") {
            if (!tanentValue.match(tanentexp)) {
                FieldValidator.addFieldError("tanNumber", "Enter valid TAN Number ", errMsgFlag);

                //this._ValidateField();
                return false;
            } else {

                //this._ValidateField();
                FieldValidator.removeFieldError('tanNumber');
                return true;
            }
        } else {

            //this._ValidateField();
            FieldValidator.addFieldError("tanNumber", "Please enter TAN", errMsgFlag);
            return false;
        }
	}
	static valdCin(errMsgFlag) {
        // var invoiceexp = /^[0-9a-zA-Z-/\_]+$/;;
        //var invoiceexp = /^[0-9a-zA-Z\b\_/@(),.\-]+$/;
        var tanentexp = /^[0-9a-zA-Z\b\:/-\s\.]{2,30}$/;
        var tanentElement = document.getElementById("cin");
        var tanentValue = tanentElement.value;
        //debugger;
        if (tanentValue.trim() != "") {
            if (!tanentValue.match(tanentexp)) {
                FieldValidator.addFieldError("cin", "Enter valid CIN ", errMsgFlag);

                //this._ValidateField();
                return false;
            } else {

                //this._ValidateField();
                FieldValidator.removeFieldError('cin');
                return true;
            }
        } else {

            //this._ValidateField();
            FieldValidator.addFieldError("cin", "Please enter CIN", errMsgFlag);
            return false;
        }
	}
	static valdAccName(errMsgFlag){
		var tanentexp = /^[a-zA-Z ]{4,200}$/;
        var tanentElement = document.getElementById("accHolderName");
        var tanentValue = tanentElement.value;
        //debugger;
        if (tanentValue.trim() != "") {
            if (!tanentValue.match(tanentexp)) {
                FieldValidator.addFieldError("accHolderName", "Enter valid Account Name ", errMsgFlag);

                //this._ValidateField();
                return false;
            } else {

                //this._ValidateField();
                FieldValidator.removeFieldError('accHolderName');
                return true;
            }
        } else {

            //this._ValidateField();
            FieldValidator.addFieldError("accHolderName", "Please enter Account Name", errMsgFlag);
            return false;
        }

	}
	static valdBranch(errMsgFlag){
		var tanentexp = /^[0-9a-zA-Z ]{3,200}$/;
        var tanentElement = document.getElementById("branchName");
        var tanentValue = tanentElement.value;
        //debugger;
        if (tanentValue.trim() != "") {
            if (!tanentValue.match(tanentexp)) {
                FieldValidator.addFieldError("branchName", "Enter valid Branch Name ", errMsgFlag);

                //this._ValidateField();
                return false;
            } else {

                //this._ValidateField();
                FieldValidator.removeFieldError('branchName');
                return true;
            }
        } else {

            //this._ValidateField();
            FieldValidator.addFieldError("branchName", "Please enter Branch Name", errMsgFlag);
            return false;
        }

	}
	
	static valdBankName(errMsgFlag) {
        var bankNameexp = /^[a-zA-Z\b\,-\s\_.]{1,250}$/;
        var bankNameElement = document.getElementById("bankName");
        var value = bankNameElement.value.trim();
        if (value.length > 0) {
            if (!(value).match(bankNameexp)) {
                FieldValidator.addFieldError('bankName', "Enter valid Bank Name", errMsgFlag);
                this.valdname = true;
                // this._ValidateField();
                return false;
            } else {
                this.valdname = false;
                // this._ValidateField();
                FieldValidator.removeFieldError('bankName');
                return true;
            }
        } else {
            this.valdname = true;
            // this._ValidateField();
            FieldValidator.addFieldError("bankName", "Please enter Bank Name", errMsgFlag)
            // FieldValidator.removeFieldError('name');
            return true;
        }
	}
	static valdIfsc(errMsgFlag) {
        var ifscElement = document.getElementById("ifscCode");
        var value = ifscElement.value.trim();
        if (value.length > 0) {
            var ifsc = ifscElement.value;
            var ifscexp = /^[A-Z]{4}0[A-Z0-9]{6}$/;
            if (!ifsc.match(ifscexp)) {
                FieldValidator.addFieldError("ifscCode", "Please enter valid IFSC Code", errMsgFlag);
                return false;
            } else {
                FieldValidator.removeFieldError('ifscCode');
                return true;
            }
        } else {
            FieldValidator.removeFieldError('ifscCode');
            return true;
        }
    }
	static valdCompGst(errMsgFlag) {
        // var invoiceexp = /^[0-9a-zA-Z-/\_]+$/;;
        //var invoiceexp = /^[0-9a-zA-Z\b\_/@(),.\-]+$/;
        var tanentexp = /^[ A-Za-z0-9]*$/
        var tanentElement = document.getElementById("comGst");
        var tanentValue = tanentElement.value;
        //debugger;
        if (tanentValue.trim() != "") {
            if (!tanentValue.match(tanentexp)) {
                FieldValidator.addFieldError("comGst", "Enter valid Company GST ", errMsgFlag);

                //this._ValidateField();
                return false;
            } else {

                //this._ValidateField();
                FieldValidator.removeFieldError('comGst');
                return true;
            }
        } else {

            //this._ValidateField();
            FieldValidator.addFieldError("comGst", "Please enter GST", errMsgFlag);
            return false;
        }
	}


    static valdPhoneNo(errMsgFlag) {
        var phoneElement = document.getElementById("phone");
        var value = phoneElement.value.trim();
        if (value.length > 0) {
            var phone = phoneElement.value;
            var phoneexp = /^[0-9]{8,13}$/;
            if (!phone.match(phoneexp)) {
                FieldValidator.addFieldError("phone", "Enter valid phone no.", errMsgFlag);
                //this._ValidateField();
                return false;
            } else {
                //this._ValidateField();
                FieldValidator.removeFieldError('phone');
                return true;
            }
        } else {
            //this._ValidateField();
            FieldValidator.addFieldError("phone", "Please enter Phone No.", errMsgFlag);
            //FieldValidator.removeFieldError('phone');
            return true;
        }
	}
	static valdAccountNo(errMsgFlag) {
        var phoneElement = document.getElementById("accountNo");
        var value = phoneElement.value.trim();
        if (value.length > 0) {
            var phone = phoneElement.value;
            var phoneexp = /^[0-9]{6,200}$/;
            if (!phone.match(phoneexp)) {
                FieldValidator.addFieldError("accountNo", "Enter valid account no.", errMsgFlag);
                //this._ValidateField();
                return false;
            } else {
                //this._ValidateField();
                FieldValidator.removeFieldError('accountNo');
                return true;
            }
        } else {
            //this._ValidateField();
            FieldValidator.addFieldError("accountNo", "Please enter account No.", errMsgFlag);
            //FieldValidator.removeFieldError('phone');
            return true;
        }
    }

    static valdProductName(errMsgFlag) {
        var productNameElement = document.getElementById("productName");
        var value = productNameElement.value.trim();
        if (value.length > 0) {
            var productName = productNameElement.value;
            var regex = /^[ A-Za-z0-9]*$/;
            if (!productName.match(regex)) {
                //this._ValidateField();
                FieldValidator.addFieldError("productName", "Enter valid product name", errMsgFlag);
                return false;
            } else {
                //this._ValidateField();
                FieldValidator.removeFieldError('productName');
                return true;
            }
        } else {
            //this._ValidateField();
            FieldValidator.addFieldError("productName", "Please enter Product Name.", errMsgFlag);
            //FieldValidator.removeFieldError('productName');
            return true;
        }
    }

    static valdPan(errMsgFlag) {
        var productDescElement = document.getElementById("panCard");
        var value = productDescElement.value.trim();
        if (value.length > 0) {
            var productDesc = productDescElement.value;
            var regex = /^[A-Z]{5}[0-9]{4}[A-Z]{1}/;
            if (!productDesc.match(regex)) {
                FieldValidator.addFieldError("panCard", "Enter valid PAN Card", errMsgFlag);
                return false;
            } else {
                FieldValidator.removeFieldError('panCard');
                return true;
            }
        } else {
            FieldValidator.removeFieldError('panCard');
            return true;
        }
	}
	
    static valdPanName(errMsgFlag) {
        var productDescElement = document.getElementById("panName");
        var value = productDescElement.value.trim();
        if (value.length > 0) {
            var productDesc = productDescElement.value;
            var regex = /^[a-zA-Z ]{2,30}$/;
            if (!productDesc.match(regex)) {
                FieldValidator.addFieldError("panName", "Enter valid PAN Name ", errMsgFlag);
                return false;
            } else {
                FieldValidator.removeFieldError('panName');
                return true;
            }
        } else {
            FieldValidator.removeFieldError('panName');
            return true;
        }
	}
	

    static valdCurrCode(errMsgFlag) {
        var currencyCodeElement = document.getElementById("currencyCode");
        if (currencyCodeElement.value == "Select Currency") {
            FieldValidator.addFieldError("currencyCode", "Select Currency Type", errMsgFlag)
            return false;
        } else {
            FieldValidator.removeFieldError('currencyCode');
            return true;
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

    

    //valdiating the amount of the product
  
   

    static valdEmail(errMsgFlag) {
        var emailRegex =  /^([A-Za-z0-9_\-\.\+])+\@([A-Za-z0-9_\-\+])+\.([A-Za-z]{2,4})$/;
       // var emailRegex = /^[_A-Za-z0-9-]+(\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\.[A-Za-z0-9-]+)*(\.[_A-Za-z0-9-]+)$/;
        //var emailRegex = /[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-zA-Z0-9](?:[a-z0-9-]*[a-zA-Z0-9])?\.)+[a-zA-Z0-9](?:[a-z0-9-]*[a-zA-Z0-9])?/;
        var element = document.getElementById("emailId");
        var value = element.value.trim();
        if (value.length > 0) {
            if (!value.match(emailRegex)) {
                FieldValidator.addFieldError('emailId', "Enter valid email address", errMsgFlag);
                //this._ValidateField();
                return false;
            } else {
                FieldValidator.removeFieldError('emailId');
                //this._ValidateField();
                return true;
            }
        } else {
            //this._ValidateField();
            FieldValidator.addFieldError("emailId", "Please Enter Email Id", errMsgFlag)
            //FieldValidator.removeFieldError('emailId');

            return true;
        }
    }

    static valdCompName(errMsgFlag) {
        var nameRegex = /^[0-9a-zA-Z\b\:@;#()/-\s\.]{2,256}$/;
        var element = document.getElementById("cname");
        var value = element.value.trim();
        if (value.length > 0) {
            if (!(value).match(nameRegex)) {
                FieldValidator.addFieldError('cname', "Enter valid company name", errMsgFlag);
               
                //this._ValidateField();
                return false;
            } else {
               
                //this._ValidateField();
                FieldValidator.removeFieldError('cname');
                return true;
            }
        } else {
            
            //this._ValidateField();
            FieldValidator.addFieldError("cname", "Please Enter Name", errMsgFlag)
            //FieldValidator.removeFieldError('name');
            return true;
        }
    }
    static valdCountry(errMsgFlag) {
        var nameRegex = /^[a-zA-Z ]{2,100}$/;
        var element = document.getElementById("country");
        var value = element.value.trim();
        if (value.length > 0) {
            if (!(value).match(nameRegex)) {
                FieldValidator.addFieldError('country', "Enter valid country", errMsgFlag);
                return false;
            } else {
                FieldValidator.removeFieldError('country');
                return true;
            }
        } else {
            FieldValidator.removeFieldError('country');
            return true;
        }
    }
    static valdCity(errMsgFlag) {
        var nameRegex = /^[a-zA-Z ]{2,100}$/;
        var element = document.getElementById("city");
        var value = element.value.trim();
        if (value.length > 0) {
            if (!(value).match(nameRegex)) {
                FieldValidator.addFieldError('city', "Enter valid city", errMsgFlag);
                return false;
            } else {
                FieldValidator.removeFieldError('city');
                return true;
            }
        } else {
            FieldValidator.removeFieldError('city');
            return true;
        }
    }
    static valdState(errMsgFlag) {
        var nameRegex = /^[a-zA-Z ]{2,100}$/;
        var element = document.getElementById("state");
        var value = element.value.trim();
        if (value.length > 0) {
            if (!(value).match(nameRegex)) {
                FieldValidator.addFieldError('state', "Enter valid state", errMsgFlag);
                return false;
            } else {
                FieldValidator.removeFieldError('state');
                return true;
            }
        } else {
            FieldValidator.removeFieldError('state');
            return true;
        }
    }

    static valdZip(errMsgFlag) {
        var zipRegex = "^[a-zA-Z0-9]{6,10}$";
        var element = document.getElementById("zip");
        var value = element.value.trim();
        if (value.length > 0) {
            if (!(value).match(zipRegex)) {
                FieldValidator.addFieldError('zip', 'Enter valid zip code', errMsgFlag);
                return false;
            } else {
                FieldValidator.removeFieldError('zip');
                return true;
            }
        } else {
            FieldValidator.removeFieldError('zip');
            return true;
        }
    }

    static valdWebsiteUrl(errMsgFlag) {
        var urlRegex = "^(https?|http?|www.?)[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        var element = document.getElementById("websiteUrl");
        var value = element.value.trim();

        if (value.length > 0) {
            if (!(value).match(urlRegex)) {
                FieldValidator.addFieldError('websiteUrl', "Enter valid return url", errMsgFlag);
                return false;
            } else {
                FieldValidator.removeFieldError('websiteUrl');
                return true;
            }
        } else {
            FieldValidator.removeFieldError('websiteUrl');
            return true;
        }
	}
	static valdPgUrl(errMsgFlag) {
        var urlRegex = "^(https?|http?|www.?)[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        var element = document.getElementById("pgUrl");
        var value = element.value.trim();

        if (value.length > 0) {
            if (!(value).match(urlRegex)) {
                FieldValidator.addFieldError('pgUrl', "Enter valid return url", errMsgFlag);
                return false;
            } else {
                FieldValidator.removeFieldError('pgUrl');
                return true;
            }
        } else {
            FieldValidator.removeFieldError('pgUrl');
            return true;
        }
    }

    static valdAddress(errMsgFlag) {
        var addRegex = /^([a-zA-Z0-9()-/.,@;:#\r\n\s]){3,150}$/; //-/() .,@;:# \r\n
        var element = document.getElementById("address");
        var value = element.value.trim();
        if (value.length > 0) {
            if (!(addRegex).test(value)) {
                FieldValidator.addFieldError('address', "Enter valid address", errMsgFlag);
                return false;
            } else {
                FieldValidator.removeFieldError('address');
                return true;
            }
        } else {
            FieldValidator.removeFieldError('address');
            return true;
        }

    }

   


    static valdAllFields() {

        var flag = FieldValidator.valdMerchant(true);


        
            flag = flag && FieldValidator.valdTanentNo(true);
            flag = flag && FieldValidator.valdHsnSac(true);
			flag = flag && FieldValidator.valdCin(true);
			falg = falg && FieldValidator.valdCompGst(true);
            flag = flag && FieldValidator.valdCompName(true);
            flag = flag && FieldValidator.valdCity(true);
            flag = flag && FieldValidator.valdAddress(true);
            flag = flag && FieldValidator.valdCountry(true);
            flag = flag && FieldValidator.valdState(true);
            flag = flag && FieldValidator.valdZip(true);
            flag = flag && FieldValidator.valdPhoneNo(true);
            flag = flag && FieldValidator.valdEmail(true);
            flag = flag && FieldValidator.valdWebsiteUrl(true);
            flag = flag && FieldValidator.valdAccountNo(true);
            flag = flag && FieldValidator.valdBranch(true);
            flag = flag && FieldValidator.valdPan(true);
            flag = flag && FieldValidator.valdCurrCode(true);
			flag = flag && FieldValidator.valdPanName(true);
			flag = flag && FieldValidator.valdIfsc(true);
			flag = flag && FieldValidator.valdBankName(true);
			flag = flag && FieldValidator.valdPgUrl(true);
            flag = flag && FieldValidator.valdAccName(true);
            falg = falg && FieldValidator.valdTanNumber(true);
			
            
        if (flag) {
            document.forms["frmTanent"].submit();
        }
    }

    //to show error in the fields
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








function isNumberKey(evt) {
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
function saveTenant() {

    let tenantNumber = document.getElementById("tanentNo").value;
    let companyName = document.getElementById("cname").value;
    let companyUrl = document.getElementById("websiteUrl").value;
    let pgUrl = document.getElementById("pgUrl").value;
    let ifscCode = document.getElementById("ifscCode").value;
    let bankName = document.getElementById("bankName").value;
    let panName = document.getElementById("panName").value;
    let cin = document.getElementById("cin").value;
    let currency = document.getElementById("currencyCode").value;
    let hsnSacCode = document.getElementById("hsnSac").value;
    let payId = document.getElementById("merchantPayId").value;
    // let expiryDate = document.getElementById("expiryexpiryDateDate").value;
    let companyGstNo = document.getElementById('comGst').value;
    let tanNumber = document.getElementById("tanNumber").value;
    let accHolderName = document.getElementById("accHolderName").value;
    let mobile = document.getElementById("phone").value;
    let emailId = document.getElementById("emailId").value;
    let city = document.getElementById("city").value;
    let state = document.getElementById("state").value;
    let country = document.getElementById("country").value;
    let postalCode = document.getElementById("zip").value;
    let address = document.getElementById("address").value;
    let panCard = document.getElementById("panCard").value;
    let branchName = document.getElementById("branchName").value;
    let accountNo = document.getElementById("accountNo").value;

    if (payId == null || payId.trim() == "ALL" || payId.trim() == "") {
        alert("Select atleast one Merchant");
        return false;
    }

    var token = document.getElementsByName("token")[0].value;

    document.getElementById("btnSave").disabled = true;
    $('#loader-wrapper').show();
    $.ajax({
        type: "POST",
        timeout: 0,
        url: "saveTenantDetails",
        data: {
            "tenantNumber": tenantNumber,
            "tanNumber" :tanNumber,
            "companyName" : companyName,
            "ifscCode": ifscCode,
            "cin": cin,
            "currency": currency,
            "hsnSacCode": hsnSacCode,
            "branchName": branchName,
            "accHolderName": accHolderName,
            "bankName": bankName,
            "panCard" : panCard,
            "panName": panName,
            "companyUrl": companyUrl,
            "pgUrl": pgUrl,
            "payId": payId,
            "companyGstNo": companyGstNo,
            "country":country,
            "mobile": mobile,
            "emailId": emailId,
            "city": city,
            "state": state,
            "postalCode" : postalCode,
            "address" : address,
            "accountNo" : accountNo,
            "token": token,
            "struts.token.name": "token",
        },
        success: function(data) {
            //console.log(data);
            document.getElementById("tenadd").innerHTML=data.response;
            $('#modalAddTenant').modal('show');
            $('#loader-wrapper').hide();
            document.getElementById("btnSave").disabled = false;

        },
        error: function(data) {
            $('#modalAddTenantError').modal('show');
            $('#loader-wrapper').hide();
            document.getElementById("btnSave").disabled = false;
        }
    });

}



