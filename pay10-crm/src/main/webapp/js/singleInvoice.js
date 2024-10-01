
$(document).ready(function () {

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
        && (document.getElementById("name").value && document.getElementById("name").className != "textFL_merch_invalid") 
        && (document.getElementById("emailId").value && document.getElementById("emailId").className != "textFL_merch_invalid") 
        && (document.getElementById("phone").value && document.getElementById("phone").className != "textFL_merch_invalid") 
        && (document.getElementById("expiresDay").value && document.getElementById("expiresDay").className != "textFL_merch_invalid")
        && (document.getElementById("amount").value && document.getElementById("amount").className != "textFL_merch_invalid")
        &&  (document.getElementById("productName").value && document.getElementById("productName").className != "textFL_merch_invalid")
        &&  (document.getElementById("totalAmount").value && document.getElementById("totalAmount").className != "textFL_merch_invalid")
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
        url: 'setMerchantCurrency',
        type: 'post',
        data: {
            emailId: emailId,
            currency: document.getElementById("currencyCode").value,
            token: token
        },
        success: function (data) {
            var dataValue = data.currencyMap;
            var currenyMapDropDown = document.getElementById("currencyCode");
            var test = "";
            var parseResponse = '<select>';
            for (index in dataValue) {
                var key = dataValue[index];
                parseResponse += "<option value = " + index + ">" + key + "</option> ";

            }
            parseResponse += '</select>';
            test += key;
            currenyMapDropDown.innerHTML = parseResponse;
        },
        error: function (data) {
            alert("Something went wrong, so please try again.");
        }
    });
}
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

    static valdProductDesc(errMsgFlag) {
        var productDescElement = document.getElementById("productDesc");
        var value = productDescElement.value.trim();
        if (value.length > 0) {
            var productDesc = productDescElement.value;
            var regex = /^[ A-Za-z0-9]*$/;
            if (!productDesc.match(regex)) {
                FieldValidator.addFieldError("productDesc", "Enter valid product description", errMsgFlag);
                return false;
            } else {
                FieldValidator.removeFieldError('productDesc');
                return true;
            }
        } else {
            FieldValidator.removeFieldError('productDesc');
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
    

    static valdExpDayAndHour(errMsgFlag) {
        var expDayElement = document.getElementById("expiresDay");
        var expHorElement = document.getElementById("expiresHour");
        var days = expDayElement.value.trim();
        var hors = expHorElement.value.trim();
        //console.log("Length : "+days.length)
        //console.log("Days : "+days)
        if (days.length > 0 && parseInt(days) >= 0) {
            if (parseInt(days) > 31) {
                FieldValidator.addFieldError("expiresDay", "Enter valid no. of days (Max:31)", errMsgFlag);
                //this._ValidateField();
                return false;
            } else {
                FieldValidator.removeFieldError('expiresDay');
                //this._ValidateField();
                if (hors.length > 0 && parseInt(hors) >= 0) {

                    if (parseInt(hors) > 24 || parseInt(hors) < 0) {
                        FieldValidator.addFieldError("expiresHour", "Enter valid no. of hours (Max:24)", errMsgFlag);
                        return false;
                    }
                    else if (parseInt(days) == 0 && parseInt(hors) == 0) {
                        FieldValidator.addFieldError("expiresDay", "Enter valid no. of days (Max:31) or hours (Max:24)", errMsgFlag);
                        //this._ValidateField();
                        return false;
                    }

                    FieldValidator.removeFieldError('expiresHour');
                    return true;
                }
                else {
                    FieldValidator.addFieldError("expiresHour", "Enter valid no. of hours", errMsgFlag);
                    return false;
                }
            }
        } else {
            FieldValidator.addFieldError("expiresDay", "Enter valid no. of days", errMsgFlag);
            //this._ValidateField();
            return false;
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

    static valdSrvcChrg(errMsgFlag) {
        var element = document.getElementById('serviceCharge');
        var value = parseFloat(element.value.trim());

        if (element.value.indexOf(".") > -1) {
            var index = element.value.indexOf(".");
            if ((element.value.substr(index, element.value.length)).length > 3) {
                FieldValidator.addFieldError("serviceCharge", "Enter valid Service Charge", errMsgFlag)
                return false;
            }
        }
        if (parseFloat(value) >= parseFloat(0)) {
            FieldValidator.removeFieldError('serviceCharge');
            return true;
        } else {
            FieldValidator.addFieldError("serviceCharge", "Enter valid Service Charge", errMsgFlag)
            return false;
        }
    }

    static valdGst(errMsgFlag) {
        var element = document.getElementById('serviceCharge');
        var value = parseFloat(element.value.trim());

        if (element.value.indexOf(".") > -1) {
            var index = element.value.indexOf(".");
            if ((element.value.substr(index, element.value.length)).length > 3) {
                FieldValidator.addFieldError("serviceCharge", "Enter valid Service Charge", errMsgFlag)
                return false;
            }
        }
        if (parseFloat(value) >= parseFloat(0)) {
            FieldValidator.removeFieldError('serviceCharge');
            return true;
        } else {
            FieldValidator.addFieldError("serviceCharge", "Enter valid Service Charge", errMsgFlag)
            return false;
        }
    }

    //valdiating the amount of the product
  
   

    static valdEmail(errMsgFlag) {
       // var emailRegex =  /^([A-Za-z0-9_\-\.\+])+\@([A-Za-z0-9_\-\+])+\.([A-Za-z]{2,4})$/;
        var emailRegex =  /^([A-Za-z0-9_\-\.\+])+\@([A-Za-z0-9_\-\+])+\.([A-Za-z]{2,10})$/;
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

    static valdName(errMsgFlag) {
        var nameRegex = /^[a-zA-Z ]{2,50}$/;
        var element = document.getElementById("name");
        var value = element.value.trim();
        if (value.length > 0) {
            if (!(value).match(nameRegex)) {
                FieldValidator.addFieldError('name', "Enter valid name", errMsgFlag);
                this.valdname = true;
                //this._ValidateField();
                return false;
            } else {
                this.valdname = false;
                //this._ValidateField();
                FieldValidator.removeFieldError('name');
                return true;
            }
        } else {
            this.valdname = true;
            //this._ValidateField();
            FieldValidator.addFieldError("name", "Please Enter Name", errMsgFlag)
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

    static valdReturnUrl(errMsgFlag) {
        var urlRegex = "^(https?|http?|www.?)[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        var element = document.getElementById("returnUrl");
        var value = element.value.trim();

        if (value.length > 0) {
            if (!(value).match(urlRegex)) {
                FieldValidator.addFieldError('returnUrl', "Enter valid return url", errMsgFlag);
                return false;
            } else {
                FieldValidator.removeFieldError('returnUrl');
                return true;
            }
        } else {
            FieldValidator.removeFieldError('returnUrl');
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

    static valdQty(errMsgFlag) {
        var qtyRgx = "^([1-9][0-9]+|[1-9])$";
        var element = document.getElementById("quantity");
        var value = element.value.trim();
        if (value == 0) {
            FieldValidator.addFieldError('quantity', "Enter valid quantity", errMsgFlag);
            return false;
        }
        if (value.length > 0) {
            if (!(value).match(qtyRgx)) {
                FieldValidator.addFieldError('quantity', "Enter valid quantity", errMsgFlag);
                return false;
            } else {
                FieldValidator.removeFieldError('quantity');
                return true;
            }
        } else {
            FieldValidator.addFieldError('quantity', "Enter valid quantity", errMsgFlag);
            return false;
        }
    }


    static valdAllFields() {

        var flag = FieldValidator.valdMerchant(true);


        
            flag = flag && FieldValidator.valdInvoiceNo(true);
            flag = flag && FieldValidator.valdName(true);
            flag = flag && FieldValidator.valdCity(true);
            flag = flag && FieldValidator.valdAddress(true);
            flag = flag && FieldValidator.valdCountry(true);
            flag = flag && FieldValidator.valdState(true);
            flag = flag && FieldValidator.valdZip(true);
            flag = flag && FieldValidator.valdPhoneNo(true);
            flag = flag && FieldValidator.valdEmail(true);
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
        if (flag) {
            document.forms["frmInvoice"].submit();
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

function sum() {
    var amounts = document.getElementById('amount').value;
    if (amounts == "") {
        amounts = "0.00";
    }
    var gstVal = document.getElementById('gst').value;
    if (serviceChargeVal == "" || serviceChargeVal == "0") {
        serviceChargeVal = "0";
    }

    var serviceChargeVal = document.getElementById('serviceCharge').value;
    if (serviceChargeVal == "" || serviceChargeVal == ".") {
        serviceChargeVal = "0";
    }


    var gstCal = parseFloat(amounts) + parseFloat(parseFloat(amounts) * parseFloat(gstVal)) / 100;



    var Quantity = document.getElementById('quantity').value;
    var result = parseFloat(Quantity) * parseFloat(gstCal);

    if (!isNaN(result)) {
        document.getElementById('totalAmount').value = parseFloat(result + parseFloat(serviceChargeVal)).toFixed(2);
        valdTotalAmount();
        valdAmount();
    }
}

function valdAmount() {
    setTimeout(function(){
    var element = document.getElementById('amount');
    var value = parseFloat(element.value.trim());

    if(value < 1 || value > 9999999.99 || (value.length < 1 || value.length > 10)){
        //document.getElementById("totalAmount").className = "textFL_merch_invalid"
        document.getElementById('amountErr').innerHTML = "Please enter Valid Amount";
        $('#amount').addClass('textFL_merch_invalid');
        $('#amount').removeClass('textFL_merch');
        //document.getElementById("totalAmount").className = "textFL_merch_invalid";
        
        //FieldValidator.addFieldError("totalAmount", "Enter valid amount", errMsgFlag)
           return false;
    }
    
        
    if (element.value.indexOf(".") > -1) {
        var index = element.value.indexOf(".");
        if ((element.value.substr(index, element.value.length)).length > 3) {
            document.getElementById('amountErr').innerHTML = " enter valid amount";
            $('#amount').addClass('textFL_merch_invalid');
            $('#amount').removeClass('textFL_merch');
            //this._ValidateField();
            //FieldValidator.addFieldError("amount", "Enter valid amount", errMsgFlag)
            return false;
        }
    }
    if (!value < parseFloat(value)) { // change here for custom amount
        //this._ValidateField();
        //FieldValidator.removeFieldError('amount');
        document.getElementById('amountErr').innerHTML = "";
            $('#amount').removeClass('textFL_merch_invalid');
            $('#amount').addClass('textFL_merch');
        return true;
    } else {
        //this._ValidateField();
        document.getElementById('amountErr').innerHTML = "Please enter Amount";
        $('#amount').addClass('textFL_merch_invalid');
        $('#amount').removeClass('textFL_merch');
        //FieldValidator.addFieldError("amount", "Enter amount", errMsgFlag)
        return false;
    }
},100); 
}

function valdTotalAmount() {

    // static valdTotalAmount() {
        var element = document.getElementById('totalAmount');
        var value = parseFloat(element.value.trim());
       // console.log("working");
       if(value < 1 || value > 9999999.99 || (value.length < 1 || value.length > 10)){

        $('#totalAmount').addClass('textFL_merch_invalid');
        $('#totalAmount').removeClass('textFL_merch');
        
           return false;
       } 
        if (element.value.indexOf(".") > -1) {
            var index = element.value.indexOf(".");
            if ((element.value.substr(index, element.value.length)).length > 3) {
               
                document.getElementById('totalAmountErr').innerHTML = " enter valid amount";
                $('#totalAmount').addClass('textFL_merch_invalid');
                $('#totalAmount').removeClass('textFL_merch');
                
                return false;
            }
        }
        if (!value < parseFloat(value)) { // change here for custom amount
            
            document.getElementById('totalAmountErr').innerHTML = "";
            $('#totalAmount').removeClass('textFL_merch_invalid');
            $('#totalAmount').addClass('textFL_merch');
           // FieldValidator.removeFieldError('totalAmount');
            return true;
        } else {
           
            $('#totalAmount').addClass('textFL_merch_invalid');
            $('#totalAmount').removeClass('textFL_merch');
          
            return false;
        }
    // }

}

function gstReverseSet() {
    setTimeout(function(){
    var totalAmounts = document.getElementById('totalAmount').value;
    if (totalAmounts == "") {
        totalAmounts = "0.00";
    }
    var amtQuantity = document.getElementById('quantity').value;
    if (amtQuantity == "" || amtQuantity == ".") {
        amtQuantity = "0.00";
    }
    var gstAmounts = document.getElementById('gst').value;
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
      
        document.getElementById('amount').value = parseFloat((totalAmounts / (gstAmounts*amtQuantity)).toFixed(2)) ;
        
    }
}, 100);

}


$(document).ready(function () {
    document.getElementById("btnSave").disabled = true;
    document.getElementById('crBtn').style = "display:none";
    if(window.location.pathname.includes("saveSingleInvoice")){
        document.getElementById("btnSave").disabled = false;

        var urlCopyLink = $('#promoLink').val();

        if (urlCopyLink == "") {
            document.getElementById('promoLink').style = "display:none";
            document.getElementById('copyBtn').style = "display:none";
            document.getElementById('btnSave').style = "display:block";
            document.getElementById('crBtn').style = "display:none";
        }
        else {
            document.getElementById('promoLink').style = "display:block";
            document.getElementById('copyBtn').style = "display:block";
            document.getElementById('btnSave').style = "display:none";
            document.getElementById('crBtn').style = "display:block";
            //console.log("580");

            var ArrStr = '<s:property value="actionMessages"/>';
            var str = ArrStr.toString();
            if (str.match(/Promotional/g)) {
                document.getElementById('div1').style.display = 'block';
                document.getElementById('proInvoiceHide').style.display = 'none';
                document.getElementById('proNameHide').style.display = 'none';
                document.getElementById('proEmailHide').style.display = 'none';
                document.getElementById('proPhoneHide').style.display = 'none';
                document.getElementById('proCityHide').style.display = 'none';
                document.getElementById('proCountryHide').style.display = 'none';
                document.getElementById('proStateHide').style.display = 'none';
                document.getElementById('proAddressHide').style.display = 'none';
                document.getElementById('proZipHide').style.display = 'none';
            }


        }

    }
    copyBtn.disabled = !document.queryCommandSupported('copy');
    document.getElementById("copyBtn").addEventListener("click", function (event) {
        var copiedLink = document.getElementById('promoLink');
        copiedLink.select();
        document.execCommand('copy');
    });


    $('#btnSave').click(function (event) {
        event.preventDefault();

        FieldValidator.valdAllFields();
    });

    $('#merchant').change(function () {
        changeCurrencyMap();
        $('#spanMerchant').hide();
        $('#currencyCodeloc').hide();
    });

    $('#serviceCharge').on('keyup', function () {
        if (this.value[0] === '.') {
            this.value = '0' + this.value;
        }
    });

    



});



$(function () {

    $("input[name='serviceCharge']").on('input', function (e) {
        $(this).val($(this).val().replace(/[^0-9]/g, ''));
    });
    $("input[name='quantity']").on('input', function (e) {
        $(this).val($(this).val().replace(/[^0-9]/g, ''));
    });
    $("input[name='amount']").on('input', function (e) {
        $(this).val($(this).val().replace(/[^0-9.]/g, ''));
    });
    $("input[name='totalAmount']").on('input', function (e) {
        $(this).val($(this).val().replace(/[^0-9.]/g, ''));
    });
});



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


$(document).on(
    'change',
    '.btn-file :file',
    function () {
        var input = $(this), numFiles = input.get(0).files ? input
            .get(0).files.length : 1, label = input.val()
                .replace(/\\/g, '/').replace(/.*\//, '');
        input.trigger('fileselect', [numFiles, label]);
    });

$(document)
    .ready(
        function () {
            $('.btn-file :file')
                .on(
                    'fileselect',
                    function (event, numFiles, label) {

                        var input = $(this).parents(
                            '.input-group').find(
                                ':text'), log = numFiles > 1 ? numFiles
                                    + ' files selected'
                                    : label;

                        if (input.length) {
                            input.val(log);
                        } else {
                            if (log)
                                alert(log);
                        }

                    });
        });

//Added By Sweety
function disableInvoiceNo(checked, tncDisable) {
	var text=$("#messageText1").text();
	if(checked){
		if (tncDisable) {
			document.getElementById("tnc").disabled = true;
		}
		$('#invoiceNo').attr('readonly', true);
	}else{
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



