// $(function() {
//     $("#expiryDate").datepicker({
//         prevText: "click for previous months",
//         nextText: "click for next months",
//         showOtherMonths: true,
//         dateFormat: 'dd-mm-yy',
//         selectOtherMonths: true,
//         minDate: new Date()
//     });

// });

// $(function() {
//     var today = new Date();
//     $('#expiryDate').val($.datepicker.formatDate('dd-mm-yy', today));

// });
$(document).ready(
function() {
    $('input:file').change(function() {
        if ($(this).val()) {
            $('input:submit').attr('disabled', false);
        }
    });
});
$(document).ready(function() {

    $('table.csv').DataTable({
        dom: 'B',
        buttons: [{
            extend: 'csv',
            text: 'GET SAMPLE CSV',
            filename: 'BATCH_UPLOAD',
        }]
    });
    
    var myframe = document.getElementById("output_frame");
	myframe.onload = function() {
		var iframeDocument = myframe.contentDocument || myframe.contentWindow.document; // get access to DOM inside the iframe
		var content = iframeDocument.textContent || iframeDocument.body.textContent; // get text of iframe
		var json = JSON.parse(content);
		//console.log(json);
		if (json) {
			// process the json here
			$('#batchFileUploadError').html(json.response);
			
			
			var resultJSON = JSON.parse(json.response); 
//			var resultJSON = JSON.parse('[{"beneficiaryCode":"shubham","paymentType":"NEFT","responseMessage":"SUCCESS","status":"ACTIVE","reason":""},{"beneficiaryCode":"rohit","paymentType":"NEFT","responseMessage":"FAILED","status":"INACTIVE","reason":"ABC"}]');
			let resutlTableTbody = $('#addBeneResultCsv');
			resutlTableTbody.html('');
			$.each(resultJSON, function(index, value){
			    let resultTableTr = $('<tr/>');
			    let resultTableTd1 = $('<td/>').html(index + 1);
			    let resultTableTd2 = $('<td/>').html(value.beneficiaryCode);
			    let resultTableTd3 = $('<td/>').html(value.paymentType);
			    let resultTableTd4 = $('<td/>').html(value.responseMessage);
			    let resultTableTd5 = $('<td/>').html(value.status);
			    let resultTableTd6 = $('<td/>').html(value.reason);
			    
			    resultTableTr.append(resultTableTd1);
			    resultTableTr.append(resultTableTd2);
			    resultTableTr.append(resultTableTd3);
			    resultTableTr.append(resultTableTd4);
			    resultTableTr.append(resultTableTd5);
			    resultTableTr.append(resultTableTd6);
			    resutlTableTbody.append(resultTableTr);
			});
			
			loadResultCsvTable();
			setTimeout(function() {
				$('#batchFileUploadError').html("");
			}, 100)
			//console.log(json);
		}
		$("#batchFile").val('');
	}
	
	$("#btnBatchUpload").on('click', function() {
        let acquirer = document.getElementById("acquirer").value;
        let payId = document.getElementById("merchantPayId").value;
        if (acquirer == null || acquirer.trim() == "ALL" || acquirer.trim() == "") {
            alert("Please Select Nodal  Aquirer !");
            return false;
        }
        if (payId == null || payId.trim() == "ALL" || payId.trim() == "") {
            alert("Select atleast one Merchant");
            return false;
        }
		$('#fileUploadMerchantPayId').val($("#merchantPayId").val());
		$('#fileUploadAcquirer').val($("#acquirer").val());
		document.getElementById("batchAddBene").submit();
	    var content=$("iframe").contents().find('body').html();
	    validateFile();
	 });
});

function loadResultCsvTable() {
	$('table.resultcsv').DataTable({
        dom: 'B',
        buttons: [{
            extend: 'csv',
            text: 'Click here to download Result',
            filename: 'Add Beneficiary Result',
        }]
    });
}
var fileTypes = ['csv'];
// acceptable file types
function readURL(input) {
    if (input.files && input.files[0]) {
        var extension = input.files[0].name.split('.').pop().toLowerCase()
          , // file
        // extension
        // from
        // input
        // file
        isSuccess = fileTypes.indexOf(extension) > -1;
        // is extension in
        // acceptable types

        if (isSuccess) {
            // yes
            var reader = new FileReader();
            reader.onload = function(e) {
                if (extension == 'csv') {
                    $(input).closest('.fileUpload').find(".icon").attr('src', 'https://www.cortechslabs.com/wp-content/uploads/2017/06/CSV-icon-new.png');

                }
                else {
                    // debugger;
                    // console.log('here=>'+$(input).closest('.uploadDoc').length);
                    document.getElementById("batchFile").value = "";
                    // $("#up").replaceWith($("#up").val('').clone(true));
                    $(input).closest('.uploadDoc').find(".docErr").slideUp('slow');

                }
            }

            reader.readAsDataURL(input.files[0]);
        } else {
            // debugger;
            // console.log('here=>'+$(input).closest('.uploadDoc').find(".docErr").length);
            $(input).closest('.uploadDoc').find(".docErr").fadeIn();
            document.getElementById("batchFile").value = "";
            setTimeout(function() {
                $('.docErr').fadeOut('fast');

            }, 9000);
        }
    }
}
$(document).ready(function() {

    $(document).on('change', '.up', function() {
        var id = $(this).attr('id');
        /*
										 * gets the filepath and filename from
										 * the input
										 */
        var profilePicValue = $(this).val();
        var fileNameStart = profilePicValue.lastIndexOf('\\');
        /*
																 * finds the end
																 * of the
																 * filepath
																 */
        profilePicValue = profilePicValue.substr(fileNameStart + 1).substring(0, 20);
        /*
																						 * isolates
																						 * the
																						 * filename
																						 */
        // var profilePicLabelText = $(".upl"); /* finds the label text */
        if (profilePicValue != '') {
            // console.log($(this).closest('.fileUpload').find('.upl').length);
            $(this).closest('.fileUpload').find('.upl').html(profilePicValue);
            /*
																				 * changes
																				 * the
																				 * label
																				 * text
																				 */
        }
    });

    $(document).on("click", "a.btn-check", function() {
        if ($(".uploadDoc").length > 1) {
            $(this).closest(".uploadDoc").remove();
        } else {
            alert("You have to upload at least one document.");
        }
    });
});


// console.log("Loading bene js");

$(document).ready(function() {
    _ValidateField();
    // document.getElementById("btnEditUser").disabled = true;
    $('#acquirer').change(function(event) {
        var acquirer = document.getElementById("acquirer").value;
        if (acquirer == "ALL" || acquirer == "") {
            // document.getElementById("btnEditUser").disabled = true;
            return false;
        } else {// document.getElementById("btnEditUser").disabled = false;
        }
    });
});

function _ValidateField() {
    setTimeout(function() {

        var _ValidBeneficiaryInfo = false;

        if ((document.getElementById("beneficiaryCd").value  && document.getElementById("beneficiaryCd").className != "textFL_merch_invalid") 
        && (document.getElementById("beneName").value && document.getElementById("beneName").className != "textFL_merch_invalid")  
        && (document.getElementById("beneAccountNo").value && document.getElementById("beneAccountNo").className != "textFL_merch_invalid") 
        && (document.getElementById("ifscCode").value && document.getElementById("ifscCode").className != "textFL_merch_invalid") 
        && (document.getElementById("bankName").value && document.getElementById("bankName").className != "textFL_merch_invalid")
        ) {
            _ValidBeneficiaryInfo = true;

        } else {
            _ValidBeneficiaryInfo = false;

        }
        if (_ValidBeneficiaryInfo) {
            document.getElementById("btnEditUser").disabled = false;
        } else {
            document.getElementById("btnEditUser").disabled = true;
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

    static valdBenName(errMsgFlag) {
        var beneNameElement = document.getElementById("beneName");
        var value = beneNameElement.value.trim();
        if (value.length > 0) {
            var benName = beneNameElement.value;
            var benNameExp = /^[a-zA-Z\b\s]{1,255}$/;
            if (!benName.match(benNameExp)) {
                FieldValidator.addFieldError("beneName", "Enter valid valid Benefiary Name", errMsgFlag);
                document.getElementById('invalid-benName').innerHTML = "Please enter valid Beneficiary Name";
                // this._ValidateField();
                return false;
            } else {
                // this._ValidateField();
                FieldValidator.removeFieldError('beneName');
                document.getElementById('invalid-benName').innerHTML = "";
                return true;
            }
        } else {
            // this._ValidateField();
            FieldValidator.addFieldError("beneName", "Please enter Benefiary Name", errMsgFlag);
            document.getElementById('invalid-benName').innerHTML = "";
            // FieldValidator.removeFieldError('phone');
            return true;
        }
    }

    static valdBenAcc(errMsgFlag) {
        var benAccElement = document.getElementById("beneAccountNo");
        var value = benAccElement.value.trim();
        if (value.length > 0) {
            var benAcc = benAccElement.value;
            var benAccexp = /^[a-zA-Z0-9]{1,32}$/;
            if (!benAcc.match(benAccexp)) {
                // this._ValidateField();
                FieldValidator.addFieldError("beneAccountNo", "Enter  valid Beneficiary Account Number", errMsgFlag);
                document.getElementById('invalid-benAcc').innerHTML = "Please enter valid Beneficiary Account";
                return false;
            } else {
                // this._ValidateField();
                FieldValidator.removeFieldError('beneAccountNo');
                document.getElementById('invalid-benAcc').innerHTML = "";
                return true;
            }
        } else {
            // this._ValidateField();
            FieldValidator.addFieldError("beneAccountNo", "Please enter Beneficiary Account Number.", errMsgFlag);
            document.getElementById('invalid-benAcc').innerHTML = "";
            // FieldValidator.removeFieldError('productName');
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
                document.getElementById('invalid-ifsc').innerHTML = "Please enter valid IFSC Code";
                return false;
            } else {
                FieldValidator.removeFieldError('ifscCode');
                document.getElementById('invalid-ifsc').innerHTML = "";
                return true;
            }
        } else {
            FieldValidator.removeFieldError('ifscCode');
            document.getElementById('invalid-ifsc').innerHTML = "";
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

    // static valdTxnLimit(errMsgFlag) {
    // var txnexp = /^[0-9]{1,8}$/;
    // var txnElement = document.getElementById("transactionLimit");
    // var value = txnElement.value.trim();
    // if (value.length > 0) {
    // if (!value.match(txnexp)) {
    // FieldValidator.addFieldError('transactionLimit', "Enter valid Transaction
    // Limit", errMsgFlag);

    // return false;
    // } else {
    // FieldValidator.removeFieldError('transactionLimit');

    // return true;
    // }
    // } else {

    // FieldValidator.addFieldError("transactionLimit", "Please enter
    // Transaction Limit", errMsgFlag)

    // return true;
    // }
    // }
    static valdBankName(errMsgFlag) {
        var bankNameexp = /^[a-zA-Z\b\s]{1,128}$/;
        var bankNameElement = document.getElementById("bankName");
        var value = bankNameElement.value.trim();
        if (value.length > 0) {
            if (!(value).match(bankNameexp)) {
                FieldValidator.addFieldError('bankName', "Enter valid Bank Name", errMsgFlag);
                document.getElementById('invalid-bankName').innerHTML = "Please enter valid Bank Name";
                this.valdname = true;
                // this._ValidateField();
                return false;
            } else {
                this.valdname = false;
                // this._ValidateField();
                FieldValidator.removeFieldError('bankName');
                document.getElementById('invalid-bankName').innerHTML = "";
                return true;
            }
        } else {
            this.valdname = true;
            // this._ValidateField();
            FieldValidator.addFieldError("bankName", "Please enter Bank Name", errMsgFlag)
            document.getElementById('invalid-bankName').innerHTML = "";
            // FieldValidator.removeFieldError('name');
            return true;
        }
    }

    static valdAllFields() {

        var flag = FieldValidator.valdMerchant(true);

        flag = flag && FieldValidator.valdBenCode(true);
        flag = flag && FieldValidator.valdBenName(true);
        flag = flag && FieldValidator.valdIfsc(true);
        // flag = flag && FieldValidator.valdTxnLimit(true);
        flag = flag && FieldValidator.valdBenAcc(true);
        flag = flag && FieldValidator.valdBankName(true);
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
// $(function() {
//     $('#btnEditUser').bind('click', function(){
       
            
           
//     });
    
function isDate(expiryDate)
{
    var currVal = expiryDate;
    if(currVal == '')
        return false;
    
    var rxDatePattern = /^(\d{4})(\/|-)(\d{1,2})(\/|-)(\d{1,2})$/; //Declare Regex
    var dtArray = currVal.match(rxDatePattern); // is format OK?
    
    if (dtArray == null) 
        return false;
    
    //Checks for mm/dd/yyyy format.
    dtMonth = dtArray[3];
    dtDay= dtArray[5];
    dtYear = dtArray[1];        
    
    if (dtMonth < 1 || dtMonth > 12) 
        return false;
    else if (dtDay < 1 || dtDay> 31) 
        return false;
    else if ((dtMonth==4 || dtMonth==6 || dtMonth==9 || dtMonth==11) && dtDay ==31) 
        return false;
    else if (dtMonth == 2) 
    {
        var isleap = (dtYear % 4 == 0 && (dtYear % 100 != 0 || dtYear % 400 == 0));
        if (dtDay> 29 || (dtDay ==29 && !isleap)) 
                return false;
    }
    return true;
}





function valdExpiryDate() {
    var expDateElement = document.getElementById("expiryDate");
    var dateFrom = new Date();
    dateFrom = (dateFrom.getFullYear()) + '-' + ((('' + (dateFrom.getMonth() + 1)).length < 2) ? ('0' + (dateFrom.getMonth() + 1)) : (dateFrom.getMonth() + 1)) + '-' + ((('' + dateFrom.getDate()).length < 2) ? ('0' + dateFrom.getDate()) : dateFrom.getDate());
    //console.log(dateFrom);
    var txtVal =  $('#expiryDate').val();
    // if(){
    //     alert('Invalid Date');
    //     return false;
    // }
    var value = expDateElement.value.trim();
    if (value.length > 0) {
        var expiry = expDateElement.value;
        var pattern =  /^(\d{4})(\/|-)(\d{2})(\/|-)(\d{2})$/;
      //  var pattern = /^([0-9]{4})-([0-9]{2})-([0-9]{2})$/
        if (!expiry.match(pattern ) || expiry < dateFrom || !isDate(expiry)) {
            document.getElementById("btnEditUser").disabled = true;
            document.getElementById('invalid-date').innerHTML = "Please enter valid Expiry Date";
            return false;
        } else {
           
            document.getElementById('invalid-date').innerHTML = "";
            document.getElementById("btnEditUser").disabled = false;
            return true;
        }
    } else {
       
        document.getElementById('invalid-date').innerHTML = "";
        document.getElementById("btnEditUser").disabled = false;
        return true;
    }
    
}
function checkEmailId() {

    var emailexp = /^[_A-Za-z0-9-]+(\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\.[A-Za-z0-9-]+)*(\.[_A-Za-z0-9-]+)$/;
    var emailElement = document.getElementById("emailId");
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
    var phoneElement = document.getElementById("mobileNo");
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
    var addElement = document.getElementById("address1");
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
    var addElement = document.getElementById("address2");
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
    var aadharElement = document.getElementById("aadhar");
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



function validateFile() {
    let completeFileName = $('#batchFile').val();

    // If file is empty then return true.
    if (completeFileName == '' || completeFileName == undefined) {
        console.log("Empty file name");
        return true;
    }

    let fileNameArr = completeFileName.split("\\");
    let fileName = fileNameArr[fileNameArr.length - 1];

    // Validate filename.
    var validFilename = /^[a-zA-Z0-9_.() -]+$/i.test(fileName);
    if (!validFilename) {
        $("#batchFile").val('');
        $('#fileUpload').val('')
        alert("Invalid file name");
        return false;
    }

    // Validate file length or size.
    if (!(fileName.length >= 5 && fileName.length <= 50)) {
        $("#batchFile").val('');
        $('#fileUpload').val('')
        alert("Invalid Filename length.");
        return false;
    }

    // Validate file extension.
    var validExtension = /\.csv$/i.test(fileName);
    if (!validExtension) {
        $("#batchFile").val('');
        $('#fileUpload').val('')
        alert("Invalid File extension");
        return false;
    }

    // Validate file size.
    let fileSize = ($("#batchFile")[0].files[0].size / 1024 );
    if (fileSize > 1) {
        $("#batchFile").val('');
        $('#fileUpload').val('')
        alert("Invalid File size");
        return false;
    }

    return true;

}
$('#btnEditUser').click(function(event) {

    event.preventDefault();

    FieldValidator.valdAllFields();
});

function ddmm_to_yymm(date) {
    var dateArr = date.split('-');
    return dateArr[2] + '-' + dateArr[1] + '-' + dateArr[0];
}
function autoLoadDetails(){
    var token = document.getElementsByName("token")[0].value;
    var payId = document.getElementById("merchantPayId").value;
 
    //$('#loader-wrapper').show();
    $.ajax({
        type: "POST",
        timeout: 0,
        url: "beneficiaryMerchantAccDetailsFromPayid",
        data: {
            "payId": payId,
            "token": token,
            "struts.token.name": "token",
        },
        success: function(data) {
        document.getElementById("beneName").value = data.beneficiaryName;
        document.getElementById("beneAccountNo").value = data.accountNo;
        document.getElementById("address1").value = data.address1;
        document.getElementById("bankName").value = data.bankName;
        document.getElementById("emailId").value = data.emailId;
        document.getElementById("ifscCode").value = data.ifsc;
        document.getElementById("mobileNo").value = data.mobileNo;
        document.getElementById("merchantPayId").value = data.payId;
            // $('#loader-wrapper').hide();
           },
        error: function(data) {
            
             //$('#loader-wrapper').hide();
           
        }
    }); 
}
function saveBeneficiary() {

    let beneficiaryCd = document.getElementById("beneficiaryCd").value;
    let beneName = document.getElementById("beneName").value;
    let beneAccountNo = document.getElementById("beneAccountNo").value;
    let ifscCode = document.getElementById("ifscCode").value;
    let bankName = document.getElementById("bankName").value;
    let aadharNo = document.getElementById("aadhar").value;
   // let beneType = document.getElementById("beneType").value;
    let currencyCode = document.getElementById("currencyCode").value;
    let acquirer = document.getElementById("acquirer").value;
    let payId = document.getElementById("merchantPayId").value;
    // let expiryDate = document.getElementById("expiryexpiryDateDate").value;
    let expiryDate = document.getElementById('expiryDate').value;
   // let expiryDate = ddmm_to_yymm(expiryDateFormat);
    // let transactionLimit = document.getElementById("transactionLimit").value;
    let mobileNo = document.getElementById("mobileNo").value;
    let emailId = document.getElementById("emailId").value;
    let address1 = document.getElementById("address1").value;
    let address2 = document.getElementById("address2").value;

    let status = "ACTIVE";
    
    

    if (acquirer == null || acquirer.trim() == "ALL" || acquirer.trim() == "") {
        alert("Please Select Nodal  Aquirer !");
        return false;
    }
    if (payId == null || payId.trim() == "ALL" || payId.trim() == "") {
        alert("Select atleast one Merchant");
        return false;
    }
    

    var token = document.getElementsByName("token")[0].value;

    document.getElementById("btnEditUser").disabled = true;
    $('#loader-wrapper').show();
    $.ajax({
        type: "POST",
        timeout: 0,
        url: "beneficiarySaveAction",
        data: {
            "beneficiaryCd": beneficiaryCd,
            "ifscCode": ifscCode,
            "beneType": "V",
            "currencyCode": currencyCode,
            "acquirer": acquirer,
            "status": status,
            "beneName": beneName,
            "bankName": bankName,
            "aadharNo" : aadharNo,
            "beneAccountNo": beneAccountNo,
            "payId": payId,
            "expiryDate": expiryDate,
            // "transactionLimit":transactionLimit,
            "mobileNo": mobileNo,
            "emailId": emailId,
            "address1": address1,
            "address2": address2,
            "token": token,
            "struts.token.name": "token",
        },
        success: function(data) {
            //console.log(data);
            document.getElementById("benadd").innerHTML=data.response;
            $('#modalAddBene').modal('show');
            $('#loader-wrapper').hide();
            document.getElementById("btnEditUser").disabled = false;

        },
        error: function(data) {
           // console.log(data);
            $('#modalAddBeneError').modal('show');
           // console.log("Failed to save beneficiary.");
            $('#loader-wrapper').hide();
            document.getElementById("btnEditUser").disabled = false;
        }
    });

}
