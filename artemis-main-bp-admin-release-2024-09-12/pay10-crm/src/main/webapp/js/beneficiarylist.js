function _ValidateField() {
    setTimeout(function() {

        var _ValidBeneficiaryInfo = false;

        if ((document.getElementById("merchant_Provided_Id").value  && document.getElementById("merchant_Provided_Id").className != "textFL_merch_invalid") 
        && (document.getElementById("merchant_Provided_Name").value && document.getElementById("merchant_Provided_Name").className != "textFL_merch_invalid") 
        && (document.getElementById("bene_Account_No").value && document.getElementById("bene_Account_No").className != "textFL_merch_invalid") 
        && (document.getElementById("ifsc_Code").value && document.getElementById("ifsc_Code").className != "textFL_merch_invalid") 
        && (document.getElementById("bank_Name").value && document.getElementById("bank_Name").className != "textFL_merch_invalid")
       
        ) {
            _ValidBeneficiaryInfo = true;

        } else {
            _ValidBeneficiaryInfo = false;

        }
        if (_ValidBeneficiaryInfo) {
            document.getElementById("modifyBtn").disabled = false;
            document.getElementById("disableBtn").disabled = false;
        } else {
            document.getElementById("modifyBtn").disabled = true;
            document.getElementById("disableBtn").disabled = false;
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
        var beneCodeElement = document.getElementById("merchant_Provided_Id");
        var benCode = beneCodeElement.value;
        // debugger;
        if (benCode.trim() != "") {
            if (!benCode.match(benCodeExp)) {
                FieldValidator.addFieldError("merchant_Provided_Id", "Enter valid Benefiary Code.", errMsgFlag);

                return false;
            } else {

                FieldValidator.removeFieldError('merchant_Provided_Id');
                return true;
            }
        } else {

            FieldValidator.addFieldError("merchant_Provided_Id", "Please enter Benefiary Code.", errMsgFlag);
            return false;
        }
    }

    static valdBeneName(errMsgFlag) {
        var beneNameElement = document.getElementById("merchant_Provided_Name");
        var value = beneNameElement.value.trim();
        if (value.length > 0) {
            var benName = beneNameElement.value;
            var benNameExp = /^[a-zA-Z\b\s]{1,255}$/;
            if (!benName.match(benNameExp)) {
                FieldValidator.addFieldError("merchant_Provided_Name", "Enter valid valid Benefiary Name", errMsgFlag);
                // this._ValidateField();
                return false;
            } else {
                // this._ValidateField();
                FieldValidator.removeFieldError('merchant_Provided_Name');
                return true;
            }
        } else {
            // this._ValidateField();
            FieldValidator.addFieldError("merchant_Provided_Name", "Please enter Benefiary Name", errMsgFlag);
            // FieldValidator.removeFieldError('phone');
            return true;
        }
    }

    static valdBenAcc(errMsgFlag) {
        var benAccElement = document.getElementById("bene_Account_No");
        var value = benAccElement.value.trim();
        if (value.length > 0) {
            var benAcc = benAccElement.value;
            var benAccexp = /^[a-zA-Z0-9]{1,32}$/;
            if (!benAcc.match(benAccexp)) {
                // this._ValidateField();
                FieldValidator.addFieldError("bene_Account_No", "Enter  valid Beneficiary Account Number", errMsgFlag);
                return false;
            } else {
                // this._ValidateField();
                FieldValidator.removeFieldError('bene_Account_No');
                return true;
            }
        } else {
            // this._ValidateField();
            FieldValidator.addFieldError("bene_Account_No", "Please enter Beneficiary Account Number.", errMsgFlag);
            // FieldValidator.removeFieldError('productName');
            return true;
        }
    }

    static valdIfsc(errMsgFlag) {
        var ifscElement = document.getElementById("ifsc_Code");
        var value = ifscElement.value.trim();
        if (value.length > 0) {
            var ifsc = ifscElement.value;
            var ifscexp =  /^[A-Z]{4}0[A-Z0-9]{6}$/;
            if (!ifsc.match(ifscexp)) {
                FieldValidator.addFieldError("ifsc_Code", "Please enter valid IFSC Code", errMsgFlag);
                return false;
            } else {
                FieldValidator.removeFieldError('ifsc_Code');
                return true;
            }
        } else {
            FieldValidator.removeFieldError('ifsc_Code');
            return true;
        }
    }
    static valdBeneType(errMsgFlag) {
        var beneTypeElement = document.getElementById("bene_Type");
        var value = beneTypeElement.value.trim();
        if (value != 1) {
            var benType = beneTypeElement.value;
            var benTypeExp = /^[A-Za-z]$/;
            if (!benType.match(benTypeExp)) {
                FieldValidator.addFieldError("bene_Type", "Enter valid valid Benefiary Type", errMsgFlag);
               //document.getElementById('invalid-beneType').innerHTML = "We accept Beneficiary Type  D, V or O only";
                return false;
            } else {
               
                FieldValidator.removeFieldError('bene_Type');
                return true;
            }
        } else {
           
            FieldValidator.addFieldError("bene_Type", "Please enter Benefiary Type", errMsgFlag);
            
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


    static valdBankName(errMsgFlag) {
        var bankNameexp = /^[a-zA-Z\b\,-\s\_.]{1,250}$/;
        var bankNameElement = document.getElementById("bank_Name");
        var value = bankNameElement.value.trim();
        if (value.length > 0) {
            if (!(value).match(bankNameexp)) {
                FieldValidator.addFieldError('bank_Name', "Enter valid Bank Name", errMsgFlag);
                this.valdname = true;
                // this._ValidateField();
                return false;
            } else {
                this.valdname = false;
                // this._ValidateField();
                FieldValidator.removeFieldError('bank_Name');
                return true;
            }
        } else {
            this.valdname = true;
            // this._ValidateField();
            FieldValidator.addFieldError("bank_Name", "Please enter Bank Name", errMsgFlag)
            // FieldValidator.removeFieldError('name');
            return true;
        }
    }

    static valdAllFields() {

        var flag = FieldValidator.valdMerchant(true);

        flag = flag && FieldValidator.valdBenCode(true);
        flag = flag && FieldValidator.valdBeneName(true);
        flag = flag && FieldValidator.valdIfsc(true);
        // flag = flag && FieldValidator.valdTxnLimit(true);
        flag = flag && FieldValidator.valdBenAcc(true);
        flag = flag && FieldValidator.valdBankName(true);
        //falg = flag && FieldValidator.valdBeneType(true);
        //flag = flag && FieldValidator.valdAadhar(true);
        // if (flag) {
        // document.forms["frmInvoice"].submit();
        // }
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
function valdExpiryDate() {
    var expDateElement = document.getElementById("bene_Expiry_Date");
    var value = expDateElement.value.trim();
    if (value.length > 0) {
        var expiry = expDateElement.value;
        var pattern = /^([0-9]{4})-([0-9]{2})-([0-9]{2})$/
        if (!expiry.match(pattern)) {
            document.getElementById('invalid-date').innerHTML = "Please enter valid Expiry Date";
            return false;
        } else {
           
            document.getElementById('invalid-date').innerHTML = "";
            return true;
        }
    } else {
       
        document.getElementById('invalid-date').innerHTML = "";
        
        return true;
    }
    
}
function checkEmailId() {

    var emailexp = /^[_A-Za-z0-9-]+(\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\.[A-Za-z0-9-]+)*(\.[_A-Za-z0-9-]+)$/;
    var emailElement = document.getElementById("email_Id");
    var emailValue = emailElement.value;
    if (emailValue.trim() !== "") {
        if (!emailValue.match(emailexp)) {
            document.getElementById('invalid-id').innerHTML = "Please enter valid Email";

            return false;
        } else {
            document.getElementById('invalid-id').innerHTML = "";

            return true;
        }
    } else {
        emailElement.focus();
        document.getElementById('invalid-id').innerHTML = "";

        return true;
    }
}
function valdPhoneNo() {
    var phoneElement = document.getElementById("mobile_No");
    var value = phoneElement.value.trim();
    if (value.length > 0) {
        var phone = phoneElement.value;
        var phoneexp = /^[0-9]{10,16}$/;
        if (!phone.match(phoneexp)) {
            document.getElementById('invalid-phone').innerHTML = "Please enter valid Phone";

            return false;
        } else {
            document.getElementById('invalid-phone').innerHTML = "";
            return true;
        }
    } else {
        phoneElement.focus();
        document.getElementById('invalid-phone').innerHTML = "";
        return true;
    }
}
function valdAddress() {
    var addElement = document.getElementById("address_1");
    var value = addElement.value.trim();
    if (value.length > 0) {
        var add = addElement.value;
        var addexp = /^([a-zA-Z0-9()-/,_'\'\r\n\s]){1,128}$/;
        if (!add.match(addexp)) {
            document.getElementById('invalid-add').innerHTML = "Please enter valid Address";

            return false;
        } else {
            document.getElementById('invalid-add').innerHTML = "";
            return true;
        }
    } else {
        addElement.focus();
        document.getElementById('invalid-add').innerHTML = "";
        return true;
    }
}
function valdAddress1() {
    var addElement = document.getElementById("address_2");
    var value = addElement.value.trim();
    if (value.length > 0) {
        var add = addElement.value;
        var addexp = /^([a-zA-Z0-9()-/,_'\'\r\n\s]){1,128}$/;
        if (!add.match(addexp)) {
            document.getElementById('invalid-add1').innerHTML = "Please enter valid Address";

            return false;
        } else {
            document.getElementById('invalid-add1').innerHTML = "";
            return true;
        }
    } else {
        addElement.focus();
        document.getElementById('invalid-add1').innerHTML = "";
        return true;
    }
}
function valdAadhar() {
    var aadharElement = document.getElementById("aadhar_No");
    var value = aadharElement.value.trim();
    if (value.length > 0) {
        var aadhar = aadharElement.value;
        var aadharexp = /^[0-9]{12,12}$/;
        if (!aadhar.match(aadharexp)) {
            document.getElementById('invalid-aadhar').innerHTML = "Please enter valid Aadhar Number";
           
            return false;
        } else {
            document.getElementById('invalid-aadhar').innerHTML = "";
            return true;
        }
    } else {
        document.getElementById('invalid-aadhar').innerHTML = "";
        return true;
    }
}

function checkInput(event) {
    var regex = /^[0-9a-zA-Z\b]+$/;
    var key = String.fromCharCode(!event.charCode ? event.which
            : event.charCode);
    if (!regex.test(key)) {
        event.preventDefault();
        return false;
    }
}

function checkEmailIdSearch() {

    var emailexp = /^[_A-Za-z0-9-]+(\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\.[A-Za-z0-9-]+)*(\.[_A-Za-z0-9-]+)$/;
    var emailElement = document.getElementById("emailId");
    var emailValue = emailElement.value;
    if (emailValue.trim() !== "") {
        if (!emailValue.match(emailexp)) {
            document.getElementById('invalidid').innerHTML = "Please enter valid Email";

            return false;
        } else {
            document.getElementById('invalidid').innerHTML = "";

            return true;
        }
    } else {
        emailElement.focus();
        document.getElementById('invalidid').innerHTML = "";

        return true;
    }
}
function valdPhoneNoSearch() {
    var phoneElement = document.getElementById("phone");
    var value = phoneElement.value.trim();
    if (value.length > 0) {
        var phone = phoneElement.value;
        var phoneexp = /^[0-9]{10,16}$/;
        if (!phone.match(phoneexp)) {
            document.getElementById('invalidphone').innerHTML = "Please enter valid Phone";

            return false;
        } else {
            document.getElementById('invalidphone').innerHTML = "";
            return true;
        }
    } else {
        phoneElement.focus();
        document.getElementById('invalidphone').innerHTML = "";
        return true;
    }
}

