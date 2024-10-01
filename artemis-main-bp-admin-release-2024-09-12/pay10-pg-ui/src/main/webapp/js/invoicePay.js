function _ValidateField(){
	
    //console.log(document.getElementById("radio1").checked)
    //console.log(document.getElementById("radio2").checked)
    //console.log("Working");

    var _ValidCustInfo = false;

    //console.log(this.activeTab);
   
        if( 
        
        (document.getElementById("CUST_NAME").value && document.getElementById("CUST_NAME").className != "textFL_merch_invalid") 
        && (document.getElementById("CUST_EMAIL").value && document.getElementById("CUST_EMAIL").className != "textFL_merch_invalid") 
		&& (document.getElementById("CUST_PHONE").value && document.getElementById("CUST_PHONE").className != "textFL_merch_invalid") 
		// && (document.getElementById("CUST_ZIP").value && document.getElementById("CUST_ZIP").className != "textFL_merch_invalid") 
		// && (document.getElementById("city").value && document.getElementById("city").className != "textFL_merch_invalid") 
		// && (document.getElementById("country").value && document.getElementById("country").className != "textFL_merch_invalid") 
		// && (document.getElementById("state").value && document.getElementById("state").className != "textFL_merch_invalid") 
		// && (document.getElementById("address").value && document.getElementById("address").className != "textFL_merch_invalid") 
        ){
            _ValidCustInfo = true;
            
        }else{
            _ValidCustInfo = false;
            
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

    if(  _ValidCustInfo){
     //   document.getElementById("INVOICE_PAY_BTN").disabled = false;
    }else{
      //  document.getElementById("INVOICE_PAY_BTN").disabled = true;
    }
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
        function isNumberKey(evt){
    var charCode = (evt.which) ? evt.which : event.keyCode
    if (charCode > 31 && (charCode < 48 || charCode > 57))
        return false;
    return true;
}
// function valdEmail(){
// 	var emailRegex = /^[_A-Za-z0-9-]+(\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\.[A-Za-z0-9-]+)*(\.[_A-Za-z0-9-]+)$/;
		
// 		var element = document.getElementById("CUST_EMAIL");
// 		var value = element.value.trim();
// 		if(value.length>0){
// 			if(!value.match(emailRegex)){
// 				document.getElementById('emailIdErr').innerHTML = "Enter valid email address";
				
			
// 			return false;
// 			}else{
// 				document.getElementById('emailIdErr').innerHTML = "";

			
// 			return true;
// 			}
// 		}
// 	}
class FieldValidator {
    constructor(x) {
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



    static valdPhoneNo(errMsgFlag) {
        var phoneElement = document.getElementById("CUST_PHONE");
        var value = phoneElement.value.trim();
        if (value.length > 0) {
            var phone = phoneElement.value;
            var phoneexp = /^[0-9]{8,13}$/;
            if (!phone.match(phoneexp)) {
                FieldValidator.addFieldError("CUST_PHONE", "Enter valid phone no.", errMsgFlag);
                //this._ValidateField();
                return false;
            } else {
                //this._ValidateField();
                FieldValidator.removeFieldError('CUST_PHONE');
                return true;
            }
        } else {
            //this._ValidateField();
            FieldValidator.addFieldError("CUST_PHONE", "Please enter Phone No.", errMsgFlag);
            //FieldValidator.removeFieldError('phone');
            return true;
        }
    }

    

    
    
    

    

    
    

    static valdEmail(errMsgFlag) {
        var emailRegex = /^[_A-Za-z0-9-]+(\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\.[A-Za-z0-9-]+)*(\.[_A-Za-z0-9-]+)$/;
        //var emailRegex = /[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-zA-Z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-zA-Z0-9](?:[a-z0-9-]*[a-zA-Z0-9])?\.)+[a-zA-Z0-9](?:[a-z0-9-]*[a-zA-Z0-9])?/;
        var element = document.getElementById("CUST_EMAIL");
        var value = element.value.trim();
        if (value.length > 0) {
            if (!value.match(emailRegex)) {
                FieldValidator.addFieldError('CUST_EMAIL', "Enter valid email address", errMsgFlag);
                //this._ValidateField();
                return false;
            } else {
                FieldValidator.removeFieldError('CUST_EMAIL');
                //this._ValidateField();
                return true;
            }
        } else {
            //this._ValidateField();
            FieldValidator.addFieldError("CUST_EMAIL", "Please Enter Email Id", errMsgFlag)
            //FieldValidator.removeFieldError('emailId');

            return true;
        }
    }

    static valdName(errMsgFlag) {
        var nameRegex = /^[a-zA-Z ]{2,50}$/;
        var element = document.getElementById("CUST_NAME");
        var value = element.value.trim();
        if (value.length > 0) {
            if (!(value).match(nameRegex)) {
                FieldValidator.addFieldError('CUST_NAME', "Enter valid name", errMsgFlag);
                this.valdname = true;
                //this._ValidateField();
                return false;
            } else {
                this.valdname = false;
                //this._ValidateField();
                FieldValidator.removeFieldError('CUST_NAME');
                return true;
            }
        } else {
            this.valdname = true;
            //this._ValidateField();
            FieldValidator.addFieldError("CUST_NAME", "Please Enter Name", errMsgFlag)
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
        var element = document.getElementById("CUST_ZIP");
        var value = element.value.trim();
        if (value.length > 0) {
            if (!(value).match(zipRegex)) {
                FieldValidator.addFieldError('CUST_ZIP', 'Enter valid zip code', errMsgFlag);
                return false;
            } else {
                FieldValidator.removeFieldError('CUST_ZIP');
                return true;
            }
        } else {
            FieldValidator.removeFieldError('CUST_ZIP');
            return true;
        }
    }



    static valdAddress(errMsgFlag) {
        var addRegex = /^[a-zA-Z0-9 -/() .,@;:# \r\n]{3,150}$/; //-/() .,@;:# \r\n
        var element = document.getElementById("address");
        var value = element.value.trim();
        if (value.length > 0) {
            if (!(value).match(addRegex)) {
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

    


    

    //to show error in the fields
    static addFieldError(fieldId, errMsg, errMsgFlag) {
        var errSpanId = fieldId + "Err";
        var elmnt = document.getElementById(fieldId);
        elmnt.className = "textFL_merch_invalid form-control";
        elmnt.focus();
        if (errMsgFlag) {
            document.getElementById(errSpanId).innerHTML = errMsg;
        }
    }

    // to remove the error 
    static removeFieldError(fieldId) {
        var errSpanId = fieldId + "Err";
        document.getElementById(errSpanId).innerHTML = "";
        document.getElementById(fieldId).className = "textFL_merch form-control";
    }
}

  
