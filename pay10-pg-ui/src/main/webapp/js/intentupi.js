var arr = [],
    paymentOptionString,
    adsImgUrl,
    adsImglinkUrl,
    merchantLogo,
    isIE = /* @cc_on!@ */false || !!document.documentMode,
    checkApiCallToGpayVal,
    checkDbEntryVal,
    cardNumber,
    pageInfoObj,
    merchantCurrencyCode,
    tempCardBin = "",
    alreadyPopulated = false,
    ccCopy = "",
    dcCopy = "",
    pcCopy = "",
    upiCopy = "",
    autoDebitCopy = "",
    iMudraCopy = "",
    netBankingCopy = "",
    xhrUpi,
    xhrUpiPay,                               
    vpaOldval = null,
    currentTokenIdSaveCard,
    oldValue,
    oldCursor,
    regexCardNo = new RegExp(/^\d{0,19}$/g),
    keyCodesBack,
    netBankLabel = 'Select Bank';

var paymentMapping = {
    bank: [{ "name": "	Axis Bank"	,"code":	1005	},{ "name": "	Bank Of Bahrain And Kuwait"	, "code":	1043	},{ "name": "	Bank Of India"	,"code":	1009	},{ "name": "	Bank Of Maharashtra"	,"code":	1064	},{ "name": "	Canara Bank	","code":	1055	},{ "name": "	Central Bank Of India"	,"code":	1063	},{ "name": "	Citi Bank"	,"code":	1010	},{ "name": "	City Union Bank"	, "code":	1060	},{ "name": "	DCB Bank	", "code":	1040	},{ "name": "	DCB Bank Corporate"	,"code":	1292	},{ "name": "	Deutsche Bank"	,"code":	1026	},{ "name": "	Dhanlakshmi Bank"	,"code":	1070	},{ "name": "	Federal Bank	","code":	1027	},{ "name": "	Hdfc Bank"	,"code":	1004	},{ "name": "	Icici Bank	","code":	1013	},{ "name": "	Indian Bank	","code":	1069	},{ "name": "	Indian Overseas Bank	", "code":	1049	},{ "name": "	Indusind Bank	","code":	1054	},{ "name": "	IDBI Bank"	, "code":	1003	},{ "name": "	Jammu And Kashmir Bank	","code":	1041	},{ "name": "	Karnataka Bank"	, "code":	1032	},{ "name": "	Karur Vysya Bank"	,"code":	1048	},{ "name": "	Kotak Mahindra Bank	","code":	1012	},{ "name": "	Punjab & Sind Bank"	,"code":	1296	},{ "name": "	Punjab National Bank"	, "code":	1002	},{ "name": "	South Indian Bank	","code":	1045	},{ "name": "	State Bank Of India	","code":	1030	},{ "name": "	Tamilnad Mercantile Bank	","code":	1065	},{ "name": "	Union Bank Of India"	,"code":	1038	},{ "name": "	Yes Bank	","code":	1001	},{ "name": "	Bharat Co-Op Bank	","code":	2003	},{ "name": "	Dena Bank	","code":	2004	},{ "name": "	Karur Vysya - Corporate Netbanking	", "code":	2007	},{ "name": "	Nainital Bank"	,"code":	2010	},{ "name": "	Punjab And Maharashtra Co-operative Bank	","code":	2011	},{ "name": "	Shamrao Vithal Co-operative Bank	","code":	2015	},{ "name": "	Union Bank - Corporate"	,"code":	2017	},{ "name": "	Bank of Baroda - Corporate	","code":	1092	},{ "name": "	Bank of Baroda Retail Accounts"	, "code":	1093	},{ "name": "	Catholic Syrian Bank	","code":	1094	},{ "name": "	Punjab National Bank Corporate"	,"code":	1096	},{ "name": "	Standard Chartered Bank	","code":	1097	},{ "name": "	Axis Bank Corporate "	, "code":	1099	},{ "name": "	ICICI Bank Corporate	","code":	1100	},{ "name": "	PNB Bank Corporate","code":	1101	},{ "name": "	HSBC Bank	","code":	1102	},{ "name": "	UCO Bank	","code":	1103	},{ "name": "	COSMOS Bank	","code":	1104	},{ "name": "	SaraSwat Bank"	,"code":	1056	},{ "name": "	Equitas Small Finance Bank"	, "code":	1106	},{ "name": "	IDFC FIRST Bank"	, "code":	1107	},{ "name": "	Janata Sahakari Bank Pune"	, "code":	1072	},{ "name": "	Tamil Nadu State Co-operative Bank	","code":	1201	},{ "name": "	NKGSB Co op Bank	", "code":	1202	},{ "name": "	TJSB Bank"	,"code":	1203	},{ "name": "	Kalyan Janata Sahakari Bank"	,"code":	1204	},{ "name": "	Mehsana urban Co-op Bank	", "code":	1205	},{ "name": "	Bandhan Bank	","code":	1206	},{ "name": "	Digibank by DBS"	,"code":	1207	},{ "name": "	Bassien Catholic Coop Bank"	,"code":	1208	},{ "name": "	The Kalupur Commercial Co-Operative Bank	","code":	1209	},{ "name": "	Suryoday Small Finance Bank	","code":	1211	},{ "name": "	ESAF Small Finance Bank"	,"code":	1212	},{ "name": "	North East Small Finance Bank	","code":	1214	},{ "name": "	RBL Bank Limited Corporate	","code":	1216	},{ "name": "	AU small finance bank"	,"code":	1220	},{ "name": "	Utkarsh Small Finance bank	","code":	1312	},{ "name": "	The Surat Peopleâ€™s Co-operative Bank"	,"code":	1313	},{ "name": "	Gujarat State Co-operative Bank	", "code":	1314	},{ "name": "	HSBC Retail	", "code":	1315	}],
    wallet: [{"code":"PPL","name":"Paytm Wallet"},{"code":"MWL","name":"Mobikwik Wallet"},{"code":"OLAWL","name":"OlaMoney Wallet"},{"code":"MMWL","name":"MatchMove Wallet"},{"code":"APWL","name":"AmazonPay Wallet"},{"code":"AWL","name":"Airtel Pay Wallet"},{"code":"FCWL","name":"FreeCharge Wallet"},{"code":"GPWL","name":"Google Pay Wallet"},{"code":"ICWL","name":"Itz Cash Wallet"},{"code":"JMWL","name":"Jio Money Wallet"},{"code":"MPWL","name":"M Pesa Wallet"},{"code":"OXWL","name":"Oxyzen Wallet"},{"code":"PPWL","name":"Phone Pe Wallet"},{"code":"SBWL","name":"Sbi Buddy Wallet"},{"code":"ZCWL","name":"Zip Cash Wallet"},{"code":"YBWL","name":"Yesbank Wallet"},{"code":"ICC","name":"ICC CashCard"},{"code":"PCH","name":"Pay World Money"},{"code":"DCW","name":"DCB Cippy"}]
};
var bankArray = ['1005', '1004', '1013', '1012', '1030', '1001'];

var amexFlag = false; {
    history.pushState(null, null, 'surchargePaymentPage.jsp');
    window.addEventListener('popstate', function (event) {
        history.pushState(null, null, 'surchargePaymentPage.jsp');
    });
}
Date.prototype.isValid = function () {
    return this.getTime() === this.getTime();
}
function mask(value) {
    var output = [];
    for (var i = 0; i < value.length; i++) {
        if (i !== 0 && i % 4 === 0) {
            output.push(" ");
        }
        output.push(value[i]);
    }
    return output.join("");
}
function unmask(value) {
    var output = value.replace(new RegExp(/[^\d]/, 'g'), '');
    return output;
}
function checkSeparator(position, interval) {
    return Math.floor(position / (interval + 1));
}
function keydownHandler(e) {
    var el = e.target;
    oldValue = el.value;
    oldCursor = el.selectionEnd;
    keyCodesBack = e.keyCode;
}
function inputHandler(e) {
    if (!isIE) {
        var el = e.target,
            newCursorPosition,
            newValue = unmask(el.value);

        if (newValue.match(regexCardNo)) {
            newValue = mask(newValue);
            newCursorPosition = oldCursor - checkSeparator(oldCursor, 4) + checkSeparator(oldCursor + (newValue.length - oldValue.length), 4) + (unmask(newValue).length - unmask(oldValue).length);

            if (newValue !== "") {
                el.value = newValue;
                if (keyCodesBack == 8 && newCursorPosition == 5) {
                    el.setSelectionRange(4, 4);
                    return false;
                } else if (keyCodesBack == 8 && newCursorPosition == 10) {
                    el.setSelectionRange(9, 9);
                    return false;
                } else if (keyCodesBack == 8 && newCursorPosition == 15) {
                    el.setSelectionRange(14, 14);
                    return false;
                } else if (keyCodesBack == 8 && newCursorPosition == 20) {
                    el.setSelectionRange(19, 19);
                    return false;
                }
            } else {
                el.value = "";
            }
        } else {
            el.value = oldValue;
            newCursorPosition = oldCursor;
        }
        el.setSelectionRange(newCursorPosition, newCursorPosition);
    }
}
function initExpCard() {
    var maskedInput = document.getElementById('paymentDate');
    if (maskedInput.addEventListener) {
        maskedInput.addEventListener('keyup', function (e) {
            handleValueChange(e);
        }, false);
    } else if (maskedInput.attachEvent) {
        maskedInput.attachEvent("onkeyup", function (e) {
            e.target = e.srcElement;
            handleValueChange(e);
        });
    }
}
function handleValueChange(e) {
    switch (e.keyCode) {
        case 20:
        case 17:
        case 18:
        case 16:
        case 37:
        case 38:
        case 39:
        case 40:
        case 9:
            return;
    }
    document.getElementById('paymentDate').value = handleCurrentValue(e);
    enterCardNum();

}
function handleCurrentValue(e) {
    var isCharsetPresent = e.target.getAttribute('data-charset'),
        placeholder = isCharsetPresent || e.target.getAttribute('data-placeholder'),
        value = e.target.value,
        l = placeholder.length,
        newValue = '',
        maskedNumber = 'XdDmMyY9',
        maskedLetter = '_',
        i,
        j,
        isInt,
        isLetter,
        strippedValue;

    strippedValue = isCharsetPresent ? value.replace(/\W/g, "") : value.replace(/\D/g, "");
    for (i = 0, j = 0; i < l; i++) {
        var x =
            isInt = !isNaN(parseInt(strippedValue[j]));
        isLetter = strippedValue[j] ? strippedValue[j].match(/[A-Z]/i) : false;
        matchesNumber = maskedNumber.indexOf(placeholder[i]) >= 0;
        matchesLetter = maskedLetter.indexOf(placeholder[i]) >= 0;

        if ((matchesNumber && isInt) || (isCharsetPresent && matchesLetter && isLetter)) {

            newValue += strippedValue[j++];

        } else if ((!isCharsetPresent && !isInt && matchesNumber) || (isCharsetPresent && ((matchesLetter && !isLetter) || (matchesNumber && !isInt)))) {
            return newValue;
        } else {
            newValue += placeholder[i];
        }

        if (strippedValue[j] == undefined) {
            break;
        }
    }
    if (e.target.getAttribute('data-valid-example')) {
        return validateProgress(e, newValue);
    }
    return newValue;
}
function validateProgress(e, value) {
    var validExample = e.target.getAttribute('data-valid-example'),
        pattern = new RegExp(e.target.getAttribute('pattern')),
        placeholder = e.target.getAttribute('data-placeholder'),
        l = value.length,
        testValue = '';

    if (l == 1 && placeholder.toUpperCase().substr(0, 2) == 'MM') {
        if (value > 1 && value < 10) {
            value = '0' + value;
        }
        return value;
    }
    for (i = l; i >= 0; i--) {
        testValue = value + validExample.substr(value.length);
        if (pattern.test(testValue)) {
            return value;
        } else {
            value = value.substr(0, value.length - 1);
        }
    }
    return value;
}
function numOnly(event, Element) {
    var key = event.keyCode,
        spaceKey = 32,
        leftKey = 37,
        rightKey = 39,
        deleteKey = 46,
        backspaceKey = 8,
        tabKey = 9,
        maxlengthCheck = Number(Element.getAttribute('maxlength'));

    if (event.key == "!" || event.key == "@" || event.key == "#" || event.key == "$" || event.key == "%" || event.key == "^" || event.key == "&" || event.key == "*" || event.key == "(" || event.key == ")") {
        return false;
    }
    if (maxlengthCheck) {
        if (Element.value.length == maxlengthCheck) {
            if (key == backspaceKey || key == tabKey || key == leftKey || key == rightKey || key == deleteKey) {
                return true;
            } else {
                return false;
            }
        }
    }
    return ((key >= 48 && key <= 57) || (key >= 96 && key <= 105) || key == backspaceKey || key == tabKey || key == leftKey || key == rightKey || key == deleteKey);
}
function googlePayNumCheck(getThis) {
    var googlePayNumLength = getThis.value.length,
        googlePayBtn = document.getElementById('googlePayBtn');

    document.getElementById('googlePayEnterPhone').style.display = "none";
    document.getElementById('googlePayInvalidNo').style.display = "none";
    document.getElementById('googlePayNum').classList.remove('redLine');

    if (googlePayNumLength >= 10) {
        googlePayBtn.classList.add("payActive");
    } else {
        googlePayBtn.classList.remove("payActive");
    }
}
function checkPhoneNo(element) {
    var phoneNoLength = (element.value).trim().length;
    if (phoneNoLength) {
        if (phoneNoLength == 10) {
            document.getElementById('googlePayInvalidNo').style.display = "none";
            document.getElementById('googlePayNum').classList.remove('redLine');
        } else {
            document.getElementById('googlePayInvalidNo').style.display = "block";
            document.getElementById('googlePayNum').classList.add('redLine');
        }
    } else {
        document.getElementById('googlePayEnterPhone').style.display = "block";
        document.getElementById('googlePayNum').classList.add('redLine');
    }
}
function deleteButton(key, element) {
    var deletCardNo = element.parentNode.querySelector('.saveCardNum').value;
    document.querySelector('.selectedCard').innerHTML = deletCardNo;
    currentTokenIdSaveCard = key;
    document.getElementById('deleteCnfBox').style.display = "block";
}
function deleteSaveCard(saveThisCard) {
    if (saveThisCard) {
        var token = document.getElementsByName("customToken")[0].value,
            data = new FormData(),
            xhr = new XMLHttpRequest();
        data.append('tokenId', currentTokenIdSaveCard);
        data.append('token', token);
        xhr.open('POST', 'deletecard', true);
        xhr.onload = function () {
            if (xhr.status === 200) {
                var deleteThisSaveCard = document.getElementById('tokenId' + currentTokenIdSaveCard).parentNode.parentNode.parentNode.parentNode;
				deleteThisSaveCard.parentNode.removeChild(deleteThisSaveCard);
                if (!document.getElementsByClassName('saveCardDetails').length) {
					location.reload(true);
                    pageInfo();
                    document.getElementById('saveCard').classList.add("hideBox");
                } else {
					location.reload(true);
                    handleClick(document.querySelectorAll('.visaRadio input')[0]);
                }
				
            } else {
                alert('An error occurred!');
            }
        }
        xhr.send(data);
    }
    document.getElementById('deleteCnfBox').style.display = "none";
}
function isCharacterKeyWithSpace(event) {
    var k;
    document.all ? k = event.keyCode : k = event.which;
    return ((k > 64 && k < 91) || (k > 96 && k < 123) || (k == 8) || (k == 32));
}
function isNumberKey(evt) {
    var charCode = (evt.which) ? evt.which : event.keyCode;
    var c = evt.keyCode;
    var ctrlDown = evt.ctrlKey || evt.metaKey // Mac support
    if (charCode > 31 && (charCode < 48 || charCode > 57) && !ctrlDown) {
        return false;
    } else {
        return true;
    }
}
function handleClick(myRadio) {

    var saveCardDetails = document.getElementsByClassName('saveCardDetails'),
        visaRadio = document.querySelectorAll('.visaRadio input'),
        savDetailsCvv = document.getElementsByClassName('savDetailsCvv');

    for (var i = 0; i < saveCardDetails.length; i++) {
        saveCardDetails[i].classList.remove("selectedSaveDetails");
        savDetailsCvv[i].disabled = true;
        savDetailsCvv[i].value = "";
        visaRadio[i].checked = false;
    }
    myRadio.closest(".saveCardDetails").classList.add("selectedSaveDetails");
    document.querySelectorAll(".selectedSaveDetails .savDetailsCvv")[0].disabled = false;
    document.querySelectorAll('.selectedSaveDetails .visaRadio input')[0].checked = true;
    document.getElementById('exSubmit').classList.remove("active");
    document.getElementById('exSubmit').classList.add("inactive");

    var selectedLi = document.getElementsByClassName('selectedSaveDetails')[0],
        paymentType = document.querySelectorAll(".selectedSaveDetails .text-muted")[0].innerText.trim().replace(/\s/g, '');
    if (paymentType == "CreditCard") {
        paymentType = 'creditCard';
        document.getElementById('orderfootDetails1').innerHTML = ccCopy;
        document.getElementById('orderfootDetails2').innerHTML = ccCopy;
    } else if (paymentType == "DebitCard") {
        paymentType = 'debitCard';
        document.getElementById('orderfootDetails1').innerHTML = dcCopy;
        document.getElementById('orderfootDetails2').innerHTML = dcCopy;
    } else if (paymentType == "PrepaidCard") {
        paymentType = 'prepaidCard';
        document.getElementById('orderfootDetails1').innerHTML = pcCopy;
        document.getElementById('orderfootDetails2').innerHTML = pcCopy;
    }
    addConvenienceFee(paymentType);
}
function saveCardPay() {

    var cvvMaxlength = document.getElementById("cvvNumber").maxLength;
    var selectedSaveDetailsCvvVal = document.querySelectorAll('.selectedSaveDetails .savDetailsCvv')[0].value;

    if (selectedSaveDetailsCvvVal) {
        if (selectedSaveDetailsCvvVal.length < cvvMaxlength) {
            document.getElementById('invalidCvvErrorSav').style.display = "block";
            return false;
        } else {
            var orderTotalAmount = document.getElementById('totalAmount').innerHTML;
            var currentTokenId = document.querySelectorAll(".selectedSaveDetails .visaRadio")[0].firstElementChild.value;
            var saveMobImg = document.querySelectorAll(".selectedSaveDetails .saveMobImg")[0].getAttribute('alt').toUpperCase();
            var savedCardNumber = document.querySelectorAll(".selectedSaveDetails .saveCardNum")[0].value;
            document.getElementById('currentCvvInput').value = selectedSaveDetailsCvvVal;
            document.getElementById('orderTotalAmountInput').value = orderTotalAmount;
            document.getElementById('currentTokenIdInput').value = currentTokenId;
            document.getElementById('savedCardMopType').value = saveMobImg;
            document.getElementById('savedCardNumber').value = savedCardNumber;

            document.getElementById('exSubmit').disabled = true;
            document.getElementById('exSubmit').classList.remove("active");
            document.getElementById('exSubmit').classList.add("inactive");
            document.getElementById('loading2').style.display = "block";
            document.getElementById("exCard").submit();
        }
    } else {
        document.getElementById('cvvErrorSav').style.display = 'block';
    }
}

function showPlaceholder(element) {
    if (!element.value) {
        element.parentElement.parentElement.parentElement.children[0].style.display = "block";
    }
}
function enableExButton() {
    document.getElementById('cvvErrorSav').style.display = "none"
    document.getElementById('invalidCvvErrorSav').style.display = "none"
    cvvMaxLength = document.getElementById("cvvNumber").maxLength;	
    var selectedSaveDetailsCvvVal = document.querySelectorAll('.selectedSaveDetails .savDetailsCvv')[0].value;
    if (selectedSaveDetailsCvvVal.length == cvvMaxLength) {
        document.getElementById('exSubmit').classList.add("active");
        document.getElementById('exSubmit').classList.remove("inactive");
    } else {
        document.getElementById('exSubmit').classList.remove("active");
        document.getElementById('exSubmit').classList.add("inactive");
    }
}
function saveCardCheckCvv(inputElement) {
    var cvvErrorSav = document.getElementById('cvvErrorSav'),
        invalidCvvErrorSav = document.getElementById('invalidCvvErrorSav');
    var cvvMaxLength = document.getElementById("cvvNumber").maxLength;
    if (inputElement.value) {
        if (inputElement.value.length == cvvMaxLength) {
            invalidCvvErrorSav.style.display = "none";
        } else {
            invalidCvvErrorSav.style.display = "block";
        }
    } else {
        cvvErrorSav.style.display = "block";
    }
}
function showStuff(currentElement, paymentType) {
	tempCardBin="";
    var tabBoxAry = document.getElementsByClassName('tabBox'),
        getCurrentDataId = currentElement.getAttribute('data-id'),
        tabLi = document.getElementsByClassName('tabLi'),
        orderfootDetails1 = document.getElementById('orderfootDetails1'),
        orderfootDetails2 = document.getElementById('orderfootDetails2');
    for (i = 0; i < tabBoxAry.length; i++) {
        tabBoxAry[i].classList.add("hideBox");
    }
    for (j = 0; j < tabLi.length; j++) {
        tabLi[j].classList.remove("activeLi");
    }
    document.getElementById(getCurrentDataId).classList.remove("hideBox");
    currentElement.classList.add("activeLi");
    document.getElementById('debit_cards').style.display = "none";
    document.getElementById('credit_cards').style.display = "none";
    document.getElementById('prepaid_cards').style.display = "none";

    switch (paymentType) {
        case "saveCard":
            var checkPaymentTypeInSaveCard = document.querySelector('.selectedSaveDetails .text-muted').innerText.trim().replace(/\s/g, '').toLowerCase();
            if (checkPaymentTypeInSaveCard == 'creditcard') {
                orderfootDetails1.innerHTML = ccCopy;
                orderfootDetails2.innerHTML = ccCopy;
            } else if (checkPaymentTypeInSaveCard == 'debitcard') {
                orderfootDetails1.innerHTML = dcCopy;
                orderfootDetails2.innerHTML = dcCopy;
            } else if (checkPaymentTypeInSaveCard == 'prepaidcard') {
                orderfootDetails1.innerHTML = pcCopy;
                orderfootDetails2.innerHTML = pcCopy;
            }
            break;
        case "creditCard":
        	var surcharge= document.getElementById('ccsurchargeAmount').value;
      	    document.getElementById('surchargeAmount').innerHTML=surcharge;
            orderfootDetails1.innerHTML = ccCopy;
            orderfootDetails2.innerHTML = ccCopy;
            document.getElementById('credit_cards').style.display = "block";
            break;
        case "debitCard":
        	var surcharge= document.getElementById('dcsurchargeAmount').value;
     	    document.getElementById('surchargeAmount').innerHTML=surcharge;
            orderfootDetails1.innerHTML = dcCopy;
            orderfootDetails2.innerHTML = dcCopy;
            document.getElementById('debit_cards').style.display = "block";
            break;
        case "prepaidCard":
        	var surcharge= document.getElementById('pcsurchargeAmount').value;
      	    document.getElementById('surchargeAmount').innerHTML=surcharge;
        	orderfootDetails1.innerHTML = pcCopy;
            orderfootDetails2.innerHTML = pcCopy;
            document.getElementById('prepaid_cards').style.display = "block";
            break;
        case "upi":
        	var surcharge= document.getElementById('upsurchargeAmount').value;
      	    document.getElementById('surchargeAmount').innerHTML=surcharge;
        	orderfootDetails1.innerHTML = upiCopy;
            orderfootDetails2.innerHTML = upiCopy;
            break;
        case "googlePay":
        	document.getElementById('surchargeAmount').innerHTML="0.00";
            orderfootDetails1.innerHTML = upiCopy;
            orderfootDetails2.innerHTML = upiCopy;
            break;
        case "autoDebit":
        	var surcharge= document.getElementById('adsurchargeAmount').value;
      	    document.getElementById('surchargeAmount').innerHTML=surcharge;
            orderfootDetails1.innerHTML = autoDebitCopy;
            orderfootDetails2.innerHTML = autoDebitCopy;
            break;
        case "iMudra":
        	var surcharge= document.getElementById('wlsurchargeAmount').value;
      	    document.getElementById('surchargeAmount').innerHTML=surcharge;
            orderfootDetails1.innerHTML = iMudraCopy;
            orderfootDetails2.innerHTML = iMudraCopy;
            break;
        case "netBanking":
        	var surcharge= document.getElementById('nbsurchargeAmount').value;
      	    document.getElementById('surchargeAmount').innerHTML=surcharge;
            orderfootDetails1.innerHTML = netBankingCopy;
            orderfootDetails2.innerHTML = netBankingCopy;
            break;
    }
    addConvenienceFee(paymentType);
    formReset();
}
function myCancelAction(param) {
    if (param) {
        document.querySelector('#approvedNotification .cancelBtn').disabled = true;
    }
    if (xhrUpiPay) {
        xhrUpiPay.abort();
    }
    if (xhrUpi) {
        xhrUpi.abort();
    }
    top.location = "txncancel";
}
function upiSubmit(upiNameProvided, upiNumberProvided, paymentType, mopType, amount, currencyCode) {
    var token = document.getElementsByName("customToken")[0].value,
        status = "",
        pgRefNum = "",
        responseCode = "",
        responseMessage = "",
        txnType = "",
        pgRefNum = "",
        redirectURL = "",
        data = new FormData(),
        myMap = new Map();
    data.append('token', token);
    data.append('vpa', upiNumberProvided);
    data.append('upiCustName', upiNameProvided);
    data.append('paymentType', paymentType);
    data.append('mopType', mopType);
    data.append('amount', amount);
    data.append('currencyCode', currencyCode);
    xhrUpiPay = new XMLHttpRequest();
    xhrUpiPay.open('POST', 'upiintentPay', true);
    xhrUpiPay.onload = function () {
        var obj = JSON.parse(this.response);
        document.getElementById('loading').style.display = "block";
        document.getElementById('approvedNotification').style.display = "block";
		
        if (null != obj) {
            transactionStatus = obj.transactionStatus;
            pgRefNum = obj.pgRefNum;
            responseCode = obj.responseCode;
            responseMessage = obj.responseMessage;
            txnType = obj.txnType;
            pgRefNum = obj.pgRefNum;
            msg = obj.msg;
            redirectURL = obj.redirectURL;
            myMap = obj.responseFields;
            
        }
        if (responseCode == "366") {
            document.getElementById('approvedNotification').style.display = "none";
            document.getElementById('loading').style.display = "none";
            document.getElementById('red1').style.display = "block";
            document.getElementById('vpaCheck').classList.add("redLine");
            document.getElementById('upi-sbmt').classList.remove("payActive");
            return false;
        }
        // Response code means instead on a collect request , form post has to
		// be sent
        // Response code means instead on a collect request , form post has to
		// be sent

     
        else if (responseCode == "000") {
            if (transactionStatus == "Sent to Bank") {
				window.location = redirectURL;
                verifyUpiResponseReceived(pgRefNum);
            } else {
                document.getElementById('approvedNotification').style.display = "none";
                document.getElementById("loading").style.display = "none";
                var form = document.getElementById("upiResponseForm");
                form.action = myMap.RETURN_URL;
                for (key in myMap) {
                    form.innerHTML += ('<input type="hidden" name="' + key + '" value="' + myMap[key] + '">');
                }
                document.getElementById("upiResponseForm").submit();
            }
        } else {
            document.getElementById('approvedNotification').style.display = "none";
            document.getElementById("loading").style.display = "none";
            var form = document.getElementById("upiResponseForm");
            form.action = myMap.RETURN_URL;

            if (myMap.encdata) {
                form.innerHTML += ('<input type="hidden" name="encdata" value="' + myMap.encdata + '">');
            } else {
                for (key in myMap) {
                    form.innerHTML += ('<input type="hidden" name="' + key + '" value="' + myMap[key] + '">');
                }
            }
            document.getElementById("upiResponseForm").submit();
        }
    };
    xhrUpiPay.send(data);
}
function sleep(delay) {
    var start = new Date().getTime();
    while (new Date().getTime() < start + delay);
}
var reqCount = 0;
function verifyUpiResponseReceived(pgRefNum) {
    var token = document.getElementsByName("customToken")[0].value,
        data = new FormData();

    data.append('token', token);
    data.append('pgRefNum', pgRefNum);

    xhrUpi = new XMLHttpRequest();
    xhrUpi.open('POST', 'verifyIntentUpiResponse', true);
    xhrUpi.onload = function () {

        if (this == null) {
            sleep(10000);
            verifyUpiResponseReceived(pgRefNum);
        }
        var obj = JSON.parse(this.response);
        if (null != obj) {
            var field = "";
            var myMap = new Map();
            var trans = "";
            trans = obj.transactionStatus;
            myMap = obj.responseFields;

            if (trans == "Sent to Bank") {
                if (reqCount == 0) {
                    sleep(10000);
                    reqCount = reqCount + 1;
                } else {
                    sleep(10000);
                }
                verifyUpiResponseReceived(pgRefNum);
            } else {
                document.getElementById('approvedNotification').style.display = "none";
                document.getElementById("loading").style.display = "none";
                var form = document.getElementById("upiResponseForm");
                form.action = myMap.RETURN_URL;
				
                if (myMap.encdata) {
                    form.innerHTML += ('<input type="hidden" name="encdata" value="' + myMap.encdata + '">');
                } else {
                    for (key in myMap) {
                        form.innerHTML += ('<input type="hidden" name="' + key + '" value="' + myMap[key] + '">');
                    }
                }
                document.getElementById("upiResponseForm").submit();
            }
        } else {
            sleep(10000);
            verifyUpiResponseReceived(pgRefNum);
        }
    };
    xhrUpi.send(data);
}

function pageInfo() {
    var obj = JSON.parse(paymentOptionString.replace(/&quot;/g, '"'));
    obj.ads = false;
    if (obj.ccMopTypes) {
        obj.ccMopTypes = obj.ccMopTypes.split(',');
    }
    if (obj.dcMopTypes) {
        obj.dcMopTypes = obj.dcMopTypes.split(',');
    }
    if (obj.pcMopTypes) {
        obj.pcMopTypes = obj.pcMopTypes.split(',');
    }
    if (obj.nbMopType) {
        obj.nbMopType = obj.nbMopType.split(',');
    }
    merchantCurrencyCode = obj.currencyCode;
    pageInfoObj = obj;
    if (window.self == window.top) {
     $(".logo").show();
     $(".copyright").show();

}

    if (window.self != window.top) {
        if (!obj.iframeOpt) {
        
            var htmlElement = document.getElementsByTagName('html')[0];
            htmlElement.parentNode.removeChild(htmlElement);
            return false;
        }else{
            
            $("#header").show();
            

        }
    }
    if (Number(obj.paymentSlab)) {
        dcCopy = "Charges Applicable:\
                        No charges applicable on transaction amount upto INR 1 Lakh.\
                        0.9% plus Applicable Taxes for all transactions above INR 1 Lakh.";
    }
    if (obj.ads) {
        document.getElementById('aUrlAds').href = obj.adsImglinkUrl;
        document.getElementById('imgUrlAds').src = obj.adsImgUrl
        document.getElementById('adsSection').style.display = "block"
    }
    var mainCTAInfo = document.getElementById('mainCTAInfo');
    var divSaveCard = document.getElementById('divSaveCard');
    var surchargeInfo = document.getElementById('surchargeInfo');
    if (obj.express_pay) {
        mainCTAInfo.style.display = "block";
        divSaveCard.style.display = "block";
    } else {
    }
    if (obj.isSurcharge) {
        surchargeInfo && (surchargeInfo.style.display = 'flex');
    } else {
        surchargeInfo && (surchargeInfo.style.display = 'none');
    }
    var customerName = document.getElementById('customerName').innerHTML.toLowerCase();
    if (customerName == 'null') {
        document.getElementById('buyer').style.display = 'none';
        document.getElementById('customerName').style.display = 'none';
    }
    var collectTotalMob = '';
    var collectSecondMob = '';
    var collectThirdMob = '';
    var collectNetbankingMob = '';

    for (var ccMop = 0; ccMop < obj.ccMopTypes.length; ccMop++) {
        var currentMobType = obj.ccMopTypes[ccMop].toLowerCase();
        collectTotalMob += '<img  src="../image/' + currentMobType + '.png" alt="' + currentMobType + '" id="' + currentMobType + 'cc" class="ccMobIcon">'
    }
    for (var dcMop = 0; dcMop < obj.dcMopTypes.length; dcMop++) {
        var currentMobType = obj.dcMopTypes[dcMop].toLowerCase();
        collectSecondMob += '<img  src="../image/' + currentMobType + '.png" alt="' + currentMobType + '" id="' + currentMobType + 'dc" class="dcMobIcon">'
    }
    for (var pcMop = 0; pcMop < obj.pcMopTypes.length; pcMop++) {
        var currentMobType = obj.pcMopTypes[pcMop].toLowerCase();
        collectThirdMob += '<img  src="../image/' + currentMobType + '.png" alt="' + currentMobType + '" id="' + currentMobType + 'pc" class="pcMobIcon">'
    }

    
    var allLi = "";
    if (obj.upi) {
        allLi += '<li class="tabLi" id="upiLi" onclick="showStuff(this, ' + "'upi'" + ')" data-id="upi">UPI</li>';
    }
    if (obj.upi && obj.googlePay) {
        allLi += '<li class="tabLi" id="gpayLi" onclick="showStuff(this, ' + "'googlePay'" + ')" data-id="googlePay"><img src="../image/gpay.png"/></li>';
    }
    document.getElementById('paymentNavs').innerHTML = allLi;

    cardNumber = document.querySelector('.cardNumber');
    conditionOnIeBrowser();
    formReset();

    var checkScreenWidth = window.matchMedia("(max-width: 680px)");
    // mediaQueryJs(checkScreenWidth);
    // windowWidthCount();
    //initExpCard();



    if (obj.upi) {
        document.getElementById('upiLi').classList.add("activeLi");
        document.getElementById('upi').classList.remove("hideBox");
        addConvenienceFee('upi');
        document.getElementById('orderfootDetails1').innerHTML = upiCopy;
        document.getElementById('orderfootDetails2').innerHTML = upiCopy;
        document.getElementById("loading2").style.display = "none";
        return false;
    }
    
};
function defaultSelectedCard() {
    document.getElementsByClassName('saveCardDetails')[0].classList.add('selectedSaveDetails');
    document.querySelectorAll('.selectedSaveDetails .visaRadio input')[0].checked = true;
    document.querySelectorAll('.selectedSaveDetails .savDetailsCvv')[0].disabled = true;
    var paymentType = document.querySelectorAll(".selectedSaveDetails .text-muted")[0].innerText.trim().replace(/\s/g, '');
    if (paymentType == "CreditCard") {
        document.getElementById('orderfootDetails1').innerHTML = ccCopy;
        document.getElementById('orderfootDetails2').innerHTML = ccCopy;
        addConvenienceFee('creditCard');
    } else if (paymentType == "DebitCard") {
        document.getElementById('orderfootDetails1').innerHTML = dcCopy;
        document.getElementById('orderfootDetails2').innerHTML = dcCopy;
        addConvenienceFee('debitCard');
    } else if (paymentType == "PrepaidCard") {
        document.getElementById('orderfootDetails1').innerHTML = pcCopy;
        document.getElementById('orderfootDetails2').innerHTML = pcCopy;
        addConvenienceFee('prepaidCard');
    }
}
function removeEnterCardMsg() {
    if (document.querySelector('.cardNumber').value.length > 0) {
        document.getElementById('emptyCardNumber').style.display = "none";
    }
    checkErrorMsgShowOrNot();
}
function addConvenienceFee(paymentType) {
	console.log(document.querySelector('#totalAmount'));
    var surcharge = document.querySelector("#surcharge"),
        surchargeName = document.getElementById('surchargeName'),

        currencyCode = '₹',
        surcharge = document.getElementById('surcharge'),
        totalAmountName = document.getElementById('totalAmountName'),
        totalAmount = document.getElementById('totalAmount'),

        amount = document.querySelector("#innerAmount").textContent,
        innerAmount = document.getElementById('innerAmount').innerHTML.trim().slice(1).trim();
		
    if (paymentType == "saveCard") {
        var checkPaymentTypeInSaveCard = document.querySelector('.selectedSaveDetails .text-muted').innerText.trim().replace(/\s/g, '').toLowerCase();
        if (checkPaymentTypeInSaveCard == "creditcard") {
            paymentType = 'creditCard';
            document.getElementById('exSubmit').classList.add('inactive');
        } else if (checkPaymentTypeInSaveCard == "debitcard") {
            paymentType = 'debitCard';
        } else if (checkPaymentTypeInSaveCard == "prepaidcard") {
            paymentType = 'prepaidCard';
        }
    }
    switch (paymentType) {
        case "creditCard":
            surcharge.textContent = currencyCode + ' ' + pageInfoObj.surcharge_cc;
            totalAmount.textContent = currencyCode + ' ' + (Number(pageInfoObj.surcharge_cc) + Number(innerAmount)).toFixed(2);
            document.getElementById('confirm-purchase').value = 'Pay ' + currencyCode + ' ' + (Number(pageInfoObj.surcharge_cc) + Number(innerAmount)).toFixed(2);
            document.getElementById('exSubmit').value = 'Pay ' + currencyCode + ' ' + (Number(pageInfoObj.surcharge_cc) + Number(innerAmount)).toFixed(2);
            document.getElementById('confirm-purchase').classList.add('inactive');
            var creditSurcharge= document.getElementById('ccsurchargeAmount').value;
      	    document.getElementById('surchargeAmount').innerHTML=creditSurcharge;
            break;
        case "debitCard":
            surcharge.textContent = currencyCode + ' ' + pageInfoObj.surcharge_dc;
            totalAmount.textContent = currencyCode + ' ' + (Number(pageInfoObj.surcharge_dc) + Number(innerAmount)).toFixed(2);
            document.getElementById('confirm-purchase').value = 'Pay ' + currencyCode + ' ' + (Number(pageInfoObj.surcharge_dc) + Number(innerAmount)).toFixed(2);
            document.getElementById('exSubmit').value = 'Pay ' + currencyCode + ' ' + (Number(pageInfoObj.surcharge_dc) + Number(innerAmount)).toFixed(2);
            document.getElementById('confirm-purchase').classList.add('inactive');
            var debitSurcharge= document.getElementById('dcsurchargeAmount').value;
      	    document.getElementById('surchargeAmount').innerHTML=debitSurcharge;
            break;
        case "prepaidCard":
            surcharge.textContent = currencyCode + ' ' + pageInfoObj.surcharge_pc;
            totalAmount.textContent = currencyCode + ' ' + (Number(pageInfoObj.surcharge_pc) + Number(innerAmount)).toFixed(2);
            document.getElementById('confirm-purchase').value = 'Pay ' + currencyCode + ' ' + (Number(pageInfoObj.surcharge_pc) + Number(innerAmount)).toFixed(2);
            document.getElementById('exSubmit').value = 'Pay ' + currencyCode + ' ' + (Number(pageInfoObj.surcharge_pc) + Number(innerAmount)).toFixed(2);
            document.getElementById('confirm-purchase').classList.add('inactive');
            var prepaidSurcharge= document.getElementById('pcsurchargeAmount').value;
      	    document.getElementById('surchargeAmount').innerHTML=prepaidSurcharge;
            break;
        case "upi":
            surcharge.textContent = currencyCode + ' ' + pageInfoObj.surcharge_up;
            totalAmount.textContent = currencyCode + ' ' + (Number(pageInfoObj.surcharge_up) + Number(innerAmount)).toFixed(2);
            document.getElementById('upi-sbmt').value = 'Pay ' + currencyCode + ' ' + (Number(pageInfoObj.surcharge_up) + Number(innerAmount)).toFixed(2);
            //document.getElementById('upi-sbmt').classList.add('inactive');
            var upiSurcharge= document.getElementById('upsurchargeAmount').value;
      	    document.getElementById('surchargeAmount').innerHTML=upiSurcharge;
            break;
        case "googlePay":
            surcharge.textContent = currencyCode + ' ' + pageInfoObj.surcharge_up;
            totalAmount.textContent = currencyCode + ' ' + (Number(pageInfoObj.surcharge_up) + Number(innerAmount)).toFixed(2);
            document.getElementById('googlePayBtn').value = 'Pay ' + currencyCode + ' ' + (Number(pageInfoObj.surcharge_up) + Number(innerAmount)).toFixed(2);
            break;
        case "autoDebit":
            surcharge.textContent = currencyCode + ' ' + pageInfoObj.surcharge_ad;
            totalAmount.textContent = currencyCode + ' ' + (Number(pageInfoObj.surcharge_ad) + Number(innerAmount)).toFixed(2);
            document.getElementById('autoDebitBtn').value = 'Pay ' + currencyCode + ' ' + (Number(pageInfoObj.surcharge_ad) + Number(innerAmount)).toFixed(2);
            var adSurcharge= document.getElementById('adsurchargeAmount').value;
      	    document.getElementById('surchargeAmount').innerHTML=adSurcharge;
            break;
        case "iMudra":
            var walletsArr = pageInfoObj.wlMopTypes.split(',');
            surcharge.textContent = currencyCode + ' ' + pageInfoObj.surcharge_wl;
            totalAmount.textContent = currencyCode + ' ' + (Number(pageInfoObj.surcharge_wl) + Number(innerAmount)).toFixed(2);
            document.getElementById('iMudraBtn').value = 'Pay ' + currencyCode + ' ' + (Number(pageInfoObj.surcharge_wl) + Number(innerAmount)).toFixed(2);
            document.getElementById('iMudraBtn').classList.add('inactive');
            var r_object = {class:'wallet', cta:'iMudraBtn',input:"#imudraFormId input[name='mopType']"}
            createRadioListItems(r_object, walletsArr);
            var iMudraSurcharge= document.getElementById('wlsurchargeAmount').value;
      	    document.getElementById('surchargeAmount').innerHTML=iMudraSurcharge;
            break;
        case "netBanking":
            surcharge.textContent = currencyCode + ' ' + pageInfoObj.surcharge_nb;
            totalAmount.textContent = currencyCode + ' ' + (Number(pageInfoObj.surcharge_nb) + Number(innerAmount)).toFixed(2);
            document.getElementById('netBankingBtn').value = 'Pay ' + currencyCode + ' ' + (Number(pageInfoObj.surcharge_nb) + Number(innerAmount)).toFixed(2);
            document.getElementById('netBankingBtn').classList.add('inactive');
            var netBankingSurcharge= document.getElementById('nbsurchargeAmount').value;
      	    document.getElementById('surchargeAmount').innerHTML=netBankingSurcharge;
            break;
    }
    
    surchargeName.style.display = 'block';
    surcharge.style.display = 'block';
    totalAmountName.style.display = 'block';
    totalAmount.style.display = 'block';
    totalAmount.style.fontWeight = '600';

    var getTotalAmount = document.getElementById("totalAmount").textContent;
    document.getElementById('amountPayablePhone').innerHTML = document.getElementById('totalAmount').innerHTML;
}

function createRadioListItems(obj, list) {
    var _listEl = document.querySelector('.radio-list.'+obj.class);
    if (list.length > 0 && _listEl && _listEl.childElementCount == 0 ) {
        document.querySelector(obj.input).value = null;
        list.forEach(function (item) {
            var _filtered = paymentMapping[obj.class].find(function(y) { return y.code == item});
            if(_filtered && ((obj.class == 'wallet') || (obj.class == 'bank' && bankArray.includes(item)))) {
            var _item = document.createElement('div');
            _item.classList.add('radio-list-item');
            var _control = document.createElement('div');
            _control.classList.add("custom-radio");
            _control.classList.add("custom-control");
            var _label = document.createElement('label');
            _label.classList.add('custom-control-label');
            _label.setAttribute('for',item);
            var _radio = document.createElement('input');
            _radio.type = 'radio';
            _radio.name = obj.class;
            _radio.value = item;
            _radio.id = item;
            _radio.classList.add('custom-control-input');
            var _img = document.createElement('img');
            _img.src = "../image/"+obj.class+"/" + item + ".png";
            _img.classList.add('wallet-logo');
            if(_filtered){
                _img.alt = _filtered.name;
            }
            _radio.onchange = function (evt) {
                if(obj.cta){
                    document.getElementById(obj.cta).classList.remove('inactive');
                    document.getElementById(obj.cta).classList.add('active');
                }
                if(obj.class == 'bank') {
                    document.getElementById('bankDropdownMenuButton').innerHTML = _filtered.name;
                    document.getElementById("netbankingList").value = evt.target.defaultValue;
                }
                obj.input && (document.querySelector(obj.input).value = evt.target.defaultValue);
            }
            _item.appendChild(_control);
            _control.appendChild(_radio);
            _control.appendChild(_label);
            _label.appendChild(_img);
            _listEl.appendChild(_item);
            }
        });
    }
}

function CheckExpiry() {
    var today = new Date(),
        someday = new Date(),
        paymentDate = document.getElementById('paymentDate'),
        paymentDateVal = paymentDate.value;

    document.getElementById("emptyExpiry").style.display = 'none';
    document.getElementById("validExpDate").style.display = 'none';
    paymentDate.classList.remove("redLine");

    if (paymentDateVal) {
        if (paymentDateVal.length < 5) {
            document.getElementById("validExpDate").style.display = 'block';
            paymentDate.classList.add("redLine");
            return false;
        } else if (paymentDateVal.length == 5) {
            var exMonth = paymentDateVal.split('/')[0];
            var exYear = paymentDateVal.split('/')[1];
            someday.setFullYear(20 + exYear, exMonth, 1);
            if (someday > today && someday.isValid() && exMonth < 13 && exMonth > 0 && exMonth.length == 2 && exYear.length == 2) {
                return true;
            } else {
                document.getElementById("validExpDate").style.display = 'block';
                paymentDate.classList.add("redLine");
                return false;
            }
        }
    } else {
        document.getElementById("emptyExpiry").style.display = 'block';
        paymentDate.classList.add("redLine");
    }
}
function CheckExpiryBoolean() {
    var today = new Date(),
        someday = new Date(),
        paymentDate = document.getElementById('paymentDate'),
        paymentDateVal = paymentDate.value;

    if (!paymentDateVal) {
        return false;
    } else if (paymentDateVal.length < 5) {
        return false;
    } else if (paymentDateVal.length == 5) {
        var exMonth = paymentDateVal.split('/')[0];
        var exYear = paymentDateVal.split('/')[1];
        someday.setFullYear(20 + exYear, exMonth, 1);
        if (someday > today && someday.isValid() && exMonth < 13 && exMonth > 0 && exMonth.length == 2 && exYear.length == 2) {
            return true;
        } else {
            return false;
        }
    }
}
function removeMmDdError() {
    var today = new Date(),
        someday = new Date(),
        paymentDate = document.getElementById('paymentDate'),
        paymentDateVal = paymentDate.value;
    document.getElementById("emptyExpiry").style.display = 'none';
    document.getElementById("validExpDate").style.display = 'none';
    document.getElementById('paymentDate').classList.remove("redLine");
    if (!paymentDateVal) {
        return false;
    } else if (paymentDateVal.length < 5) {
        return false;
    } else if (paymentDateVal.length == 5) {
        var exMonth = paymentDateVal.split('/')[0];
        var exYear = paymentDateVal.split('/')[1];
        var cvvValue = (document.getElementById('cvvNumber').value).trim();
        someday.setFullYear(20 + exYear, exMonth, 1);
        if (someday > today && someday.isValid() && exMonth < 13 && exMonth > 0 && exMonth.length == 2 && exYear.length == 2) {
            return true;
        } else {
            return false;
        }
    }
}
function CheckExpiryOnBlur() {
    var today = new Date();
    var someday = new Date();
    var paymentDate = document.getElementById('paymentDate'),
        paymentDateVal = paymentDate.value;
    document.getElementById("emptyExpiry").style.display = 'none';
    document.getElementById("validExpDate").style.display = 'none';
    paymentDate.classList.remove("redLine");

    if (paymentDateVal) {
        if (paymentDateVal.length < 5) {
            document.getElementById("validExpDate").style.display = 'block';
            paymentDate.classList.add("redLine");
        } else if (paymentDateVal.length == 5) {
            var exMonth = paymentDateVal.split('/')[0];
            var exYear = paymentDateVal.split('/')[1];
            someday.setFullYear(20 + exYear, exMonth, 1);
            if (someday > today && someday.isValid() && exMonth < 13 && exMonth > 0 && exMonth.length == 2 && exYear.length == 2) {
                return true;
            } else {
                document.getElementById("validExpDate").style.display = 'block';
                paymentDate.classList.add("redLine");
            }
        }
    } else {
        document.getElementById("emptyExpiry").style.display = 'block';
        paymentDate.classList.add("redLine");
    }
}
function checkCvv() {
    var cvvNumber = document.getElementById('cvvNumber');
    var cvvNumberLength = cvvNumber.value.length;
    var maxLength = document.getElementById('cvvNumber').maxLength;
    if (cvvNumber.value && cvvNumberLength == maxLength) {
        return true;
    } else {
        return false;
    }
}
function checkCvvFocusOut() {
    var cvvNumber = document.getElementById('cvvNumber'),
        cvvNumberLength = cvvNumber.value.length,
        maxLength = document.getElementById('cvvNumber').maxLength;

    document.getElementById('cvvValidate').style.display = "none";
    document.getElementById('emptyCvv').style.display = 'none';
    cvvNumber.classList.remove("redLine");

    if (cvvNumber.value) {
        if (cvvNumberLength == maxLength) {
            document.getElementById('cvvValidate').style.display = "none";
            document.getElementById('emptyCvv').style.display = 'none';
            cvvNumber.classList.remove("redLine");
        } else {
            document.getElementById('cvvValidate').style.display = "block";
            cvvNumber.classList.add("redLine");
        }
    } else {
        document.getElementById('emptyCvv').style.display = 'block';
        cvvNumber.classList.add("redLine");
    }
}
function removeCvvError() {
    var cvvNumberLength = document.getElementById('cvvNumber').value.length;
    var maxLength = document.getElementById('cvvNumber').maxLength;
    document.getElementById('cvvValidate').style.display = "none";
    document.getElementById('emptyCvv').style.display = 'none';
    document.getElementById('cvvNumber').classList.remove("redLine");

    if (cvvNumberLength == maxLength) {
        return true;
    } else {
        return false;
    }
}
function checkFirstLetter() {
    var inputVal = document.querySelector('.cardNumber').value,
        firstDigit = Number(inputVal.substr(0, 1));
    if (inputVal != '') {
        if (firstDigit == 2 || firstDigit == 3 || firstDigit == 4 || firstDigit == 5 || firstDigit == 6 || firstDigit == 8) {
            document.getElementById("checkStartNo").style.display = 'none';
            checkErrorMsgShowOrNot();
            return true;
        } else {
            document.getElementById("emptyCardNumber").style.display = 'none';
            document.getElementById("checkStartNo").style.display = 'block';
            document.getElementById('validCardCheck').style.display = "none";
            document.getElementById('notSupportedCard').style.display = "none";
            checkErrorMsgShowOrNot();
            return false;
        }
    } else {
        document.getElementById("emptyCardNumber").style.display = 'block';
        document.getElementById('validCardCheck').style.display = "none";
        document.getElementById('notSupportedCard').style.display = "none";
        document.getElementById("checkStartNo").style.display = 'none';
        checkErrorMsgShowOrNot();
        return false;
    }
}
function checkFirstLetterBooleanVal() {
    var inputVal = document.querySelector('.cardNumber').value,
        firstDigit = Number(inputVal.substr(0, 1));
    if (inputVal != '') {
        if (firstDigit == 2 || firstDigit == 3 || firstDigit == 4 || firstDigit == 5 || firstDigit == 6 || firstDigit == 8) {
            return true;
        } else {
            return false;
        }
    } else {
        return false;
    }
}
function checkCardSupported() {
    var containCard = document.querySelector('.cardNumber').value.replace(/\s/g, "").length,
        checkStartNo = document.getElementById('checkStartNo');
    if (containCard >= 9) {
        if (checkMopTypeValidForUser() == false) {
            if (checkStartNo.style.display == "none") {
                document.getElementById('notSupportedCard').style.display = 'block';
            }

            document.getElementById('validCardCheck').style.display = 'none';
            checkErrorMsgShowOrNot();
            return false;
        } else {
            document.getElementById('notSupportedCard').style.display = 'none';
            checkErrorMsgShowOrNot();
            return true;
        }
    }
}
function checkLuhn(element) {
	removeEnterCardMsg();
    var cardvalue = element.value,
        ipt = cardvalue.replace(/\s/g, ''),
        checkStartNo = document.getElementById("checkStartNo");

    if (ipt.length == '') {
        document.getElementById('validCardCheck').style.display = 'none';
        document.getElementById('notSupportedCard').style.display = "none";
        document.getElementById("checkStartNo").style.display = 'none';
        checkErrorMsgShowOrNot();
        return false;
    }	
    if (ipt.length >= 9 && checkStartNo.style.display == 'none') {
        if (!checkMopTypeValidForUser()) {
            document.getElementById('validCardCheck').style.display = 'none';
            document.getElementById('notSupportedCard').style.display = "block";
            document.getElementById("checkStartNo").style.display = 'none';
            checkErrorMsgShowOrNot();
            return false;
        }
    }
    if (ipt.length < 13) {
        document.getElementById('validCardCheck').style.display = 'block';
        document.getElementById('notSupportedCard').style.display = "none";
        document.getElementById("checkStartNo").style.display = 'none';
        checkErrorMsgShowOrNot();
        return false;
    }
    var substr = ipt.substring(0, 6);
    var sum = 0;
    var cnumber = ipt.replace(/\s/g, '');
    var numdigits = cnumber.length;
    var parity = numdigits % 2;
    for (var i = 0; i < numdigits; i++) {
        var digit = parseInt(cnumber.charAt(i))
        if (i % 2 == parity)
            digit *= 2;
        if (digit > 9)
            digit -= 9;
        sum += digit;
    }
    var result = ((sum % 10) == 0);
    if (ipt.length >= 13 && result) {
        document.getElementById('validCardCheck').style.display = 'none';
        document.getElementById('confirm-purchase').disabled = false;
    }
    else {
        document.getElementById('validCardCheck').style.display = 'block';
        document.getElementById('notSupportedCard').style.display = "none";
        document.getElementById("checkStartNo").style.display = 'none';
        document.getElementById('confirm-purchase').disabled = false;
    }
    checkErrorMsgShowOrNot();
    return result;
}
function checkLuhnBooleanVal() {
	removeEnterCardMsg();
    var cardNumber = document.querySelector('.cardNumber');
    var cardvalue = cardNumber.value;
    var ipt = cardvalue.replace(/\s/g, '');
    var substr = ipt.substring(0, 6);
    var sum = 0;
    var cnumber = ipt.replace(/\s/g, '');
    var numdigits = cnumber.length;
    var parity = numdigits % 2;
    for (var i = 0; i < numdigits; i++) {
        var digit = parseInt(cnumber.charAt(i))
        if (i % 2 == parity)
            digit *= 2;
        if (digit > 9)
            digit -= 9;
        sum += digit;
    }
    var booleanResultOfLuhn = ((sum % 10) == 0);
    if (ipt.length < 13) {
        return false;
    }
    return booleanResultOfLuhn;
}
function checkPaymentTypeAndSelectedTab(mopType) {
// tempCardBin = "";
    var activeLiText = (document.getElementsByClassName('activeLi')[0].innerText).trim().toLowerCase();
    if (mopType == "CC" && pageInfoObj.creditCard) {
        if (activeLiText == 'debit card' || activeLiText == 'prepaid card') {
            if (document.getElementById('debitLi')) {
                document.getElementById('debitLi').classList.remove("activeLi");
            }
            if (document.getElementById('prepaidLi')) {
                document.getElementById('prepaidLi').classList.remove("activeLi");
            }
            document.getElementById('creditLi').classList.add("activeLi");
            document.getElementById('orderfootDetails1').innerHTML = ccCopy;
            document.getElementById('orderfootDetails2').innerHTML = ccCopy;
            document.getElementById('debit_cards').style.display = "none";
            document.getElementById('prepaid_cards').style.display = "none";
            document.getElementById('credit_cards').style.display = "block";
            clearFieldOnTabSwitch();
        }
    }
    if (mopType == "DC" && pageInfoObj.debitCard) {
        if (activeLiText == 'credit card' || activeLiText == 'prepaid card') {
            if (document.getElementById('creditLi')) {
                document.getElementById('creditLi').classList.remove("activeLi");
            }
            if (document.getElementById('prepaidLi')) {
                document.getElementById('prepaidLi').classList.remove("activeLi");
            }
            document.getElementById('debitLi').classList.add("activeLi");
            document.getElementById('orderfootDetails1').innerHTML = dcCopy;
            document.getElementById('orderfootDetails2').innerHTML = dcCopy;
            document.getElementById('credit_cards').style.display = "none";
            document.getElementById('prepaid_cards').style.display = "none";
            document.getElementById('debit_cards').style.display = "block";
            clearFieldOnTabSwitch();
        }
    }
    if (mopType == "PC" && pageInfoObj.prepaidCard) {
        if (activeLiText == 'debit card' || activeLiText == 'credit card') {
            if (document.getElementById('creditLi')) {
                document.getElementById('creditLi').classList.remove("activeLi");
            }
            if (document.getElementById('debitLi')) {
                document.getElementById('debitLi').classList.remove("activeLi");
            }
            document.getElementById('prepaidLi').classList.add("activeLi");
            document.getElementById('orderfootDetails1').innerHTML = pcCopy;
            document.getElementById('orderfootDetails2').innerHTML = pcCopy;
            document.getElementById('credit_cards').style.display = "none";
            document.getElementById('debit_cards').style.display = "none";
            document.getElementById('prepaid_cards').style.display = "block";
            clearFieldOnTabSwitch();
        }
    }
}
function clearFieldOnTabSwitch() {
// document.getElementById("paymentDate").value = "";
    document.getElementById("cvvNumber").value = "";
// document.getElementById("cardName").value = "";
    document.getElementById('validExpDate').style.display = 'none';
    document.getElementById('cvvValidate').style.display = 'none';
    document.getElementById('nameError').style.display = 'none';
    document.getElementById('emptyCardNumber').style.display = 'none';
    document.getElementById('emptyExpiry').style.display = 'none';
    document.getElementById('emptyCvv').style.display = 'none';
    document.getElementById("paymentDate").classList.remove("redLine");
    document.getElementById("cvvNumber").classList.remove("redLine");
    document.getElementById("cardName").classList.remove("redLine");
    document.getElementById("cardsaveflag1").checked = false;
}

function clearFieldsOnSaveCards() {
    document.getElementById("paymentDate").value = "";
    document.getElementById("cvvNumber").value = "";
    document.getElementById("cardName").value = "";
    document.getElementById('validExpDate').style.display = 'none';
    document.getElementById('cvvValidate').style.display = 'none';
    document.getElementById('nameError').style.display = 'none';
    document.getElementById('emptyCardNumber').style.display = 'none';
    document.getElementById('emptyExpiry').style.display = 'none';
    document.getElementById('emptyCvv').style.display = 'none';
    document.getElementById("paymentDate").classList.remove("redLine");
    document.getElementById("cvvNumber").classList.remove("redLine");
    document.getElementById("cardName").classList.remove("redLine");
    document.getElementById("cardsaveflag1").checked = false;
}


function binCheck(event) {
    var substr = document.querySelector('.cardNumber').value.replace(/\s/g, "").substring(0, 9),
        token = document.getElementsByName("customToken")[0].value,
        returnByBean = false,
        inputLength = document.querySelector('.cardNumber').value.length,
        cardNumber = document.querySelector('.cardNumber');

    var data = new FormData();
    data.append('token', token);
    data.append('bin', substr);
    var xhr = new XMLHttpRequest();
    xhr.open('POST', 'binResolver', true);
    xhr.onload = function () {
        var obj = JSON.parse(this.response);
        var inptLnth = document.querySelector('.cardNumber').value.length;

        document.getElementById("mopTypeCCDiv2").value = obj.mopType;
        switch (obj.paymentType) {
            case "DC":
                document.getElementById("paymentType2").value = "DC";
                addConvenienceFee('debitCard');
                break;
            case "CC":
                document.getElementById("paymentType2").value = "CC";
                addConvenienceFee('creditCard');
                break;
            case "PC":
                document.getElementById("paymentType2").value = "PC";
                addConvenienceFee('prepaidCard');
                break;
        }
        checkPaymentTypeAndSelectedTab(obj.paymentType);
        var activeLiTabId = document.getElementsByClassName("activeLi")[0].getAttribute('id');

        switch (activeLiTabId) {
            case "creditLi":
                getPaymentTypeFromUi = "CC";
                break;
            case "debitLi":
                getPaymentTypeFromUi = "DC";
                break;
            case "prepaidLi":
                getPaymentTypeFromUi = "PC";
                break;
        }

        if (obj.mopType != null && obj.paymentType != null && obj.paymentType == getPaymentTypeFromUi) {
            document.getElementById('notSupportedCard').style.display = 'none';
            var ccMobIcon = document.getElementsByClassName('ccMobIcon');
            var dcMobIcon = document.getElementsByClassName('dcMobIcon');
            var pcMobIcon = document.getElementsByClassName('pcMobIcon');
            mopTypeIconShow(obj.mopType);
            for (mobIconElement = 0; mobIconElement < ccMobIcon.length; mobIconElement++) {
                ccMobIcon[mobIconElement].classList.add("opacityMob");
            }
            for (mobIconElement = 0; mobIconElement < dcMobIcon.length; mobIconElement++) {
                dcMobIcon[mobIconElement].classList.add("opacityMob");
            }
            for (mobIconElement = 0; mobIconElement < pcMobIcon.length; mobIconElement++) {
                pcMobIcon[mobIconElement].classList.add("opacityMob");
            }
            if (document.getElementById(obj.mopType.toLowerCase() + 'cc') != null) {
                document.getElementById(obj.mopType.toLowerCase() + 'cc').classList.remove("opacityMob");
                document.getElementById(obj.mopType.toLowerCase() + 'cc').classList.add("activeMob");
            }
            if (document.getElementById(obj.mopType.toLowerCase() + 'dc') != null) {
                document.getElementById(obj.mopType.toLowerCase() + 'dc').classList.remove("opacityMob");
                document.getElementById(obj.mopType.toLowerCase() + 'dc').classList.add("activeMob");
            }
            if (document.getElementById(obj.mopType.toLowerCase() + 'pc') != null) {
                document.getElementById(obj.mopType.toLowerCase() + 'pc').classList.remove("opacityMob");
                document.getElementById(obj.mopType.toLowerCase() + 'pc').classList.add("activeMob");
            }

            var cardNumberElement = document.querySelector('.cardNumber');
            if (checkFirstLetterBooleanVal() && checkLuhnBooleanVal() && CheckExpiryBoolean() && checkCvv() && nameCheckKeyUp()) {
                document.getElementById('confirm-purchase').classList.add("active");
                document.getElementById('confirm-purchase').classList.remove("inactive");
            } else {
                document.getElementById('confirm-purchase').classList.add("inactive");
                document.getElementById('confirm-purchase').classList.remove("active");
            }
            returnByBean = true;
        } else {
            if (document.getElementById('checkStartNo').style.display == "block") {
                document.getElementById('notSupportedCard').style.display = 'none';
                document.getElementById('validCardCheck').style.display = 'none';
            } else {
                document.getElementById('notSupportedCard').style.display = 'block';
                document.getElementById('validCardCheck').style.display = 'none';
                document.getElementById("checkStartNo").style.display = 'none';
            }
            mopTypeIconShow('bc');
            checkErrorMsgShowOrNot();
            var ccMobIcon = document.getElementsByClassName('ccMobIcon');
            var dcMobIcon = document.getElementsByClassName('dcMobIcon');
            var pcMobIcon = document.getElementsByClassName('pcMobIcon');
            for (mobIconElement = 0; mobIconElement < ccMobIcon.length; mobIconElement++) {
                ccMobIcon[mobIconElement].classList.add("opacityMob");
                ccMobIcon[mobIconElement].classList.remove("activeMob");
            }
            for (mobIconElement = 0; mobIconElement < dcMobIcon.length; mobIconElement++) {
                dcMobIcon[mobIconElement].classList.add("opacityMob");
                dcMobIcon[mobIconElement].classList.remove("activeMob");
            }
            for (mobIconElement = 0; mobIconElement < pcMobIcon.length; mobIconElement++) {
                pcMobIcon[mobIconElement].classList.add("opacityMob");
                pcMobIcon[mobIconElement].classList.remove("activeMob");
            }
            returnByBean = false;
        }
        checkErrorMsgShowOrNot();
    };
    xhr.send(data);
    return returnByBean;
}
function nameCheck() {
    var cardName = document.getElementById('cardName'),
        getName = cardName.value,
        nameError = document.getElementById('nameError');
    if (getName) {
        cardName.classList.remove("redLine");
        nameError.style.display = 'none';
    } else {
        cardName.classList.add("redLine");
        nameError.style.display = 'block';
    }
}
function nameCheckKeyUp() {
    var getName = (document.getElementById('cardName').value).trim();
    var cardName = document.getElementById('cardName');
    if (getName.length > 0) {
        document.getElementById('nameError').style.display = 'none';
        cardName.classList.remove("redLine");
        return true;
    } else {
        return false;
    }
}
function decideBinCheck(newBin, event) {
    if (tempCardBin == newBin && newBin.length > 8) {

    } else {
        binCheck(event);
        tempCardBin = newBin;
    }
}
function enterCardNum(event) {
    var inputLength = document.querySelector('.cardNumber').value.replace(/\s/g, '').length;
	var input = document.querySelector('.cardNumber').value.replace(/\s/g, '');
	
	if(inputLength != 0 && (input.charAt(0) == "0" || input.charAt(0) == 0 )){
		document.getElementById('cardNumber').value = '';
	}
	
	if (inputLength == 16 || inputLength == 13 || inputLength == 19) {

			inputLength = 9;
	}
	
    if (inputLength < 9) {
        document.getElementById('emptyCardNumber').style.display = "none";
        document.getElementById('notSupportedCard').style.display = "none";
        document.getElementById('checkStartNo').style.display = "none";
        checkErrorMsgShowOrNot();
        mopTypeIconShow("bc");
        var ccMobIcon = document.getElementsByClassName('ccMobIcon');
        var dcMobIcon = document.getElementsByClassName('dcMobIcon');
        for (mobIconElement = 0; mobIconElement < ccMobIcon.length; mobIconElement++) {
            ccMobIcon[mobIconElement].classList.remove("opacityMob");
            ccMobIcon[mobIconElement].classList.remove("activeMob");
        }
        for (mobIconElement = 0; mobIconElement < dcMobIcon.length; mobIconElement++) {
            dcMobIcon[mobIconElement].classList.remove("opacityMob");
            dcMobIcon[mobIconElement].classList.remove("activeMob");
        }
        alreadyPopulated = false;
    }
    if (inputLength == 9 && !alreadyPopulated) {
        alreadyPopulated = true;
        tempCardBin = document.querySelector('.cardNumber').value.replace(/\s/g, '');
        binCheck(event);
    }
    if (alreadyPopulated) {
        decideBinCheck(document.querySelector('.cardNumber').value.replace(/\s/g, "").substring(0, 9), event);
    }
    var currentInputValue = document.querySelector('.cardNumber').value,
        cardName = document.getElementById('cardName').value,
        cardNumberElement = document.getElementsByClassName('pField masked')[0];

    if (checkFirstLetter() && CheckExpiryBoolean() && checkCvv() && nameCheckKeyUp() && checkMopTypeValidForUser() && checkLuhn(cardNumberElement)) {
        document.getElementById('confirm-purchase').classList.add("active");
        document.getElementById('confirm-purchase').classList.remove("inactive");


    } else {
        document.getElementById('confirm-purchase').classList.remove("active");
        document.getElementById('confirm-purchase').classList.add("inactive");
    }
	
}
function enterCardNumRmvErrMsg() {
    if (checkFirstLetter()) {
        document.getElementById('validCardCheck').style.display = "none";
    }
    checkErrorMsgShowOrNot();
}
function checkFields(e, element) {
    var cvvNumber = document.getElementById('cvvNumber'),
        cardNumber = document.querySelector('.cardNumber'),
        cardName = document.getElementById('cardName'),
        paymentDate = document.getElementById('paymentDate'),
        cvvMaxLength = document.getElementById('cvvNumber').maxLength;
        cardNumberElement = document.getElementsByClassName('userCardNumber')[0];
    if (!checkFirstLetter()) {
        document.getElementById('emptyCardNumber').style.display = "block";
        document.getElementById('validCardCheck').style.display = "none";
        document.getElementById('notSupportedCard').style.display = "none";
        document.getElementById('checkStartNo').style.display = "none";
        checkErrorMsgShowOrNot();
    }
    if (!checkLuhn(cardNumberElement)) {
        document.getElementById('validCardCheck').style.display = "block";
        document.getElementById('notSupportedCard').style.display = "none";
        document.getElementById('checkStartNo').style.display = "none";
        checkErrorMsgShowOrNot();
    }
    if (!checkMopTypeValidForUser()) {
        document.getElementById('validCardCheck').style.display = "none";
        document.getElementById('notSupportedCard').style.display = "block";
        document.getElementById('checkStartNo').style.display = "none";
        checkErrorMsgShowOrNot();
    }
    if (cvvNumber.value.length != cvvMaxLength) {
        document.getElementById('emptyCvv').style.display = 'none';
        document.getElementById('cvvValidate').style.display = "block";
        cvvNumber.classList.add("redLine");
    }
    if (cvvNumber.value.length == '') {
        document.getElementById('cvvValidate').style.display = "none";
        document.getElementById('emptyCvv').style.display = 'block';
        cvvNumber.classList.add("redLine");
    }
    if (!cardName.value) {
        document.getElementById('nameError').style.display = 'block';
        cardName.classList.add("redLine");
    }
    if (!CheckExpiry()) {
        var paymentDate = document.getElementById('paymentDate'),
            emptyExpiry = document.getElementById('emptyExpiry'),
            validExpDate = document.getElementById('validExpDate');
        if (paymentDate.value) {
            emptyExpiry.style.display = 'none';
            validExpDate.style.display = 'block';
        } else {
            emptyExpiry.style.display = 'block';
            validExpDate.style.display = 'none';
        }
        paymentDate.classList.add("redLine");
    }
    document.getElementById('setExpiryMonth').value = document.getElementById('paymentDate').value.split('/')[0];
    document.getElementById('setExpiryYear').value = '20' + document.getElementById('paymentDate').value.split('/')[1];
    document.getElementById('cardNumber').value = document.querySelector('.cardNumber').value;

    if (checkFirstLetter() && checkLuhn(cardNumberElement) && cvvNumber.value.length == cvvMaxLength && cardName.value && CheckExpiry()) {
        document.getElementById('loading2').style.display = "block";
        document.getElementById('confirm-purchase').classList.add("pointerEventNone");
        document.getElementById('confirm-purchase').disabled = true;
        document.getElementById('creditCard').submit();
    }
}
function formReset() {
   

    //document.getElementById("vpaCheck").value = "";
    document.getElementById('red1').style.display = 'none';
    document.getElementById('enterVpa').style.display = 'none';
    document.getElementById('upi-sbmt').classList.remove("payActive");
    document.getElementById("vpaCheck").classList.remove("redLine");

    var radioList = document.querySelectorAll('.custom-control-input');
    radioList.forEach(function (x) {
        if (x.type == 'radio') {
            x.checked = false;
        }
    });
    
   
}

function submitUpiForm() {
    if (document.getElementById('vpaCheck').classList.contains('redLine')) {
        return false;
    } else if (isValidVpaOnFocusOut()) {
        document.getElementById("loading").style.display = "block"
        var upiNameProvided = 'dummy';
        var upiNumberProvided = document.getElementById("vpaCheck").value;
        var paymentType = "UP";
        var mopType = "UP";
        var amount = document.getElementById('totalAmount').innerHTML;
        var currencyCode = merchantCurrencyCode;
        var token = document.getElementsByName("customToken")[0].value;
        vpaOldval = upiNumberProvided;
        document.getElementById('upi-sbmt').classList.remove("payActive");
        upiSubmit(upiNameProvided, upiNumberProvided, paymentType, mopType, amount, currencyCode)
    } else {
        isValidVpaOnFocusOut();
    }
}
function restrictKeyVpa(event, Element) {
    var key = event.keyCode,
        spaceKey = 32,
        leftKey = 37,
        rightKey = 39,
        deleteKey = 46,
        backspaceKey = 8,
        tabKey = 9,
        point = 190,
        subtract = 189,
        subtractMoz = 173;
    if (event.key == "!" || event.key == "#" || event.key == "$" || event.key == "%" || event.key == "^" || event.key == "&" || event.key == "*" || event.key == "(" || event.key == ")" || event.key == ">" || event.key == "_") {
        return false;
    }
    return ((key >= 48 && key <= 57) || (key >= 33 && key <= 39) || (key >= 65 && key <= 90) || (key >= 96 && key <= 105) || key == backspaceKey || key == tabKey || key == leftKey || key == rightKey || key == deleteKey || key == point || key == subtract || key == subtractMoz || key == 12 || key == 40 || key == 45 || key == 109 || key == 110);
}
function isValidVpaBoolean() {
    var vpaRegex = /[A-Za-z0-9][A-Za-z0-9.-]$/;
    var vpaElement = document.getElementById("vpaCheck");
    var vpaValue = vpaElement.value.trim();
    return vpaRegex.test(vpaValue);
}
function isValidVpa() {
    var vpaCheck = document.getElementById('vpaCheck');
    vpaCheck.classList.remove("redLine");
    document.getElementById('red1').style.display = 'none';
    document.getElementById('enterVpa').style.display = 'none';
    return isValidVpaBoolean();
}
function isValidVpaOnFocusOut() {
    var flag = null;
    var vpaElement = document.getElementById("vpaCheck");
    var vpaValue = (vpaElement.value).trim();
    if (!vpaValue) {
        document.getElementById('enterVpa').style.display = "block";
        document.getElementById('red1').style.display = "none";
        vpaElement.classList.add("redLine");
        flag = false;
    }
    
    if(isValidVpaBoolean()){
        flag = true;
    } else {
        vpaElement.classList.add("redLine");
        document.getElementById('red1').style.display = "block";
        document.getElementById('enterVpa').style.display = "none";
        flag = false;
    }
    if(flag){
        document.getElementById('upi-sbmt').classList.remove('inactive');
        document.getElementById('upi-sbmt').classList.add('active');
    } else {
        document.getElementById('upi-sbmt').classList.remove('active');
        document.getElementById('upi-sbmt').classList.add('inactive');
    }
    return flag;
}

function enableButton() {
    var upiSbmtBtn = document.getElementById('upi-sbmt');
    if (isValidVpaBoolean()) {
        upiSbmtBtn.classList.remove("inactive");
        upiSbmtBtn.classList.add("active");
    } else {
        upiSbmtBtn.classList.remove("active");
        upiSbmtBtn.classList.add("inactive");
    }
}

function checkMopTypeValidForUser() {

    var cardNumber = document.querySelector('.cardNumber'),
        cardNumberVal = cardNumber.value.replace(/\s/g, '');
    var flagForMobCheck = false;
    var activeDivId = document.getElementsByClassName('activeLi')[0].id;
    if (cardNumberVal.length < 9) {
        return false;
    }
    if (activeDivId === 'creditLi') {
        var ccMobIcon = document.getElementsByClassName('ccMobIcon');
        for (elemnt = 0; elemnt < ccMobIcon.length; elemnt++) {
            for (var checkMop = 0; checkMop < ccMobIcon[elemnt].classList.length; checkMop++) {
                if (ccMobIcon[elemnt].classList[checkMop] == 'activeMob') {
                    flagForMobCheck = true;
                    break;
                }
            }
        }
    } else if (activeDivId === 'debitLi') {
        var dcMobIcon = document.getElementsByClassName('dcMobIcon');
        for (elemnt = 0; elemnt < dcMobIcon.length; elemnt++) {
            for (var checkMop1 = 0; checkMop1 < dcMobIcon[elemnt].classList.length; checkMop1++) {
                if (dcMobIcon[elemnt].classList[checkMop1] == 'activeMob') {
                    flagForMobCheck = true;
                    break;
                }
            }
        }
    } else if (activeDivId === 'prepaidLi') {
        var pcMobIcon = document.getElementsByClassName('pcMobIcon');
        for (elemnt = 0; elemnt < pcMobIcon.length; elemnt++) {
            for (var checkMop2 = 0; checkMop2 < pcMobIcon[elemnt].classList.length; checkMop2++) {
                if (pcMobIcon[elemnt].classList[checkMop2] == 'activeMob') {
                    flagForMobCheck = true;
                    break;
                }
            }
        }
    }
    return flagForMobCheck;
}
function autoDebitFn() {
    var innerAmountValue = document.getElementById('innerAmount').innerHTML.replace("INR", "").trim();
    document.getElementById('amountAD').value = innerAmountValue;
    document.getElementById('autoDebitBtn').classList.add('deactvAutoDebit');
    document.getElementById('payAutoDebit').submit();
}
function submitImudra() {
    document.getElementById('loading').style.display = "block";
    var innerAmountValue = document.getElementById('innerAmount').innerHTML.replace("INR", "").trim();
    document.getElementById('amountImudra').value = innerAmountValue;
    document.getElementById('iMudraBtn').classList.remove('active');
    document.getElementById('iMudraBtn').classList.add('inactive');
    document.getElementById('iMudraBtn').disabled = true;
    document.getElementById('imudraFormId').submit();
}
function toolTipCvv(actionWithCvv) {
    document.getElementById('whatIsCvv').style.display = actionWithCvv;
}
function mopTypeIconShow(getMopType) {
    var getMopTypeLowerCase = getMopType.toLowerCase();
    document.getElementById('userMoptypeIcon').src = "../image/" + getMopTypeLowerCase + ".png";
    if(getMopTypeLowerCase==="ax"){
		document.getElementById("cvvNumber").maxLength = 4;
	}else if(getMopTypeLowerCase==="bc"){
		document.getElementById("cvvNumber").maxLength = 3;
	}else{
		document.getElementById("cvvNumber").maxLength = 3;
		if(document.getElementById("cvvNumber").value.length==4){
			document.getElementById("cvvNumber").value = document.getElementById("cvvNumber").value.substring(0,3);
		}
	}
}
function submitGooglePayForm() {
    var googlePayNum = (document.getElementById('googlePayNum').value).trim();
    var token = document.getElementsByName("customToken")[0].value;
    document.getElementById('googlePayPhoneNo').value = googlePayNum;
    document.getElementById('googlePayNum').classList.remove('redLine');
    document.getElementById('googlePayInvalidNo').style.display = "none";
    document.getElementById('googlePayEnterPhone').style.display = "none";
    if (googlePayNum) {
        if (googlePayNum.length == 10) {

        } else {
            document.getElementById('googlePayInvalidNo').style.display = "block";
            document.getElementById('googlePayNum').classList.add('redLine');
            return false;
        }
    } else {
        document.getElementById('googlePayEnterPhone').style.display = "block";
        document.getElementById('googlePayNum').classList.add('redLine');
        return false;
    }
    document.getElementById('loading').style.display = "block";
    var data = new FormData();
    data.append('vpaPhone', "+91" + googlePayNum);
    data.append('mopType', 'GP');
    data.append('paymentType', 'UP');
    data.append('token', token);
    var xhttp = new XMLHttpRequest();
    xhttp.open("POST", "upiPay", true);
    xhttp.onload = function () {
        var obj = JSON.parse(this.response);
        document.getElementById('loading').style.display = "block";
        document.getElementById('approvedNotification').style.display = "block";

        if (null != obj) {
            transactionStatus = obj.transactionStatus;
            pgRefNum = obj.pgRefNum;
            responseCode = obj.responseCode;
            responseMessage = obj.responseMessage;
            txnType = obj.txnType;
            pgRefNum = obj.pgRefNum;
            myMap = obj.responseFields;
        }
        if (responseCode == "366") {
            document.getElementById('approvedNotification').style.display = "none";
            document.getElementById('loading').style.display = "none";
            alert('Please enter a valid VPA');
            document.getElementById('googlePayNum').value = '';
            document.getElementById('googlePayBtn').classList.remove("payActive");
            return false;
        }
        else if (responseCode == "017") {
            document.getElementById('approvedNotification').style.display = "none";
            document.getElementById('loading').style.display = "none";
            alert('We are unable to reach GPay servers, please try again later!');
            document.getElementById('googlePayNum').value = '';
            document.getElementById('googlePayBtn').classList.remove("payActive");
            return false;
        }
        else if (responseCode == "000") {
            if (transactionStatus == "Sent to Bank") {
                checkApiCallToGpayVal = setTimeout(checkApiCallToGpay(pgRefNum), 60000);
                checkDbEntryVal = setTimeout(checkDbEntry(pgRefNum), 5000);
            } else {

                document.getElementById('approvedNotification').style.display = "none";
                document.getElementById("loading").style.display = "none";
                var form = document.getElementById("googlePayResponseForm");
                form.action = myMap.RETURN_URL;

                for (key in myMap) {
                    form.innerHTML += ('<input type="hidden" name="' + key + '" value="' + myMap[key] + '">');
                }
                document.getElementById("googlePayResponseForm").submit();
            }
        }
        else {
            document.getElementById('approvedNotification').style.display = "none";
            document.getElementById("loading").style.display = "none";
            var form = document.getElementById("googlePayResponseForm");
            form.action = myMap.RETURN_URL;

            if (myMap.encdata) {
                form.innerHTML += ('<input type="hidden" name="encdata" value="' + myMap.encdata + '">');
            } else {
                for (key in myMap) {
                    form.innerHTML += ('<input type="hidden" name="' + key + '" value="' + myMap[key] + '">');
                }
            }
            document.getElementById("googlePayResponseForm").submit();
        }
    };
    xhttp.send(data);
}
function checkErrorMsgShowOrNot() {
    var checkStartNoDisplay = document.getElementById('checkStartNo').style.display,
        validCardCheckDisplay = document.getElementById('validCardCheck').style.display,
        emptyCardNumberDisplay = document.getElementById('emptyCardNumber').style.display,
        notSupportedCardDisplay = document.getElementById('notSupportedCard').style.display,
        cardNumber = document.querySelector('.cardNumber');

    if (checkStartNoDisplay == "block" || validCardCheckDisplay == "block" || emptyCardNumberDisplay == "block" || notSupportedCardDisplay == "block") {
        cardNumber.classList.add("redLine");
    } else {
        cardNumber.classList.remove("redLine");
    }
}
function checkDbEntry(gpRefNum) {
    return function () {
        var token = document.getElementsByName("customToken")[0].value;
        var data = new FormData();
        data.append('token', token);
        data.append('pgRefNum', gpRefNum);
        var xhttp = new XMLHttpRequest();
        xhttp.open("POST", "verifyUpiResponse", true);
        xhttp.onload = function () {
            var reponseCheckDbEntry = JSON.parse(this.response);

            if (null != reponseCheckDbEntry) {

                var myMapGpayResponse = reponseCheckDbEntry.responseFields;
            }
            if (reponseCheckDbEntry.transactionStatus == "Sent to Bank") {
                var checkDbEntryVal = setTimeout(checkDbEntry(pgRefNum), 5000);
            } else {
                if (checkDbEntryVal) {
                    clearTimeout(checkDbEntryVal);
                }
                if (checkApiCallToGpayVal) {
                    clearTimeout(checkApiCallToGpayVal);
                }
                document.getElementById('approvedNotification').style.display = "none";
                document.getElementById("loading").style.display = "none";
                var form = document.getElementById("googlePayResponseForm");
                form.action = myMapGpayResponse.RETURN_URL;
                if (myMapGpayResponse.encdata) {
                    form.innerHTML += ('<input type="hidden" name="encdata" value="' + myMapGpayResponse.encdata + '">');
                } else {
                    for (key in myMapGpayResponse) {
                        form.innerHTML += ('<input type="hidden" name="' + key + '" value="' + myMapGpayResponse[key] + '">');
                    }
                }
                document.getElementById("googlePayResponseForm").submit();
            }
        }
        xhttp.send(data);
    }
}
function checkApiCallToGpay(gpRefNum) {
    return function () {
        var token1 = document.getElementsByName("customToken")[0].value;
        var data1 = new FormData();
        data1.append('token', token1);
        data1.append('pgRefNum', gpRefNum);
        var xhttp1 = new XMLHttpRequest();
        xhttp1.open("POST", "verifyUpiGpayResponse", true);
        xhttp1.onload = function () {
            var reponseGpayApiCall = JSON.parse(this.response),
                txnResponseCode = reponseGpayApiCall.responseCode;

            if (null != reponseGpayApiCall) {

                var myMapGpayResponse = reponseGpayApiCall.responseFields;
            }
            if (txnResponseCode == 003 || txnResponseCode == 004 || txnResponseCode == 007) {
                if (checkDbEntryVal) {
                    clearTimeout(checkDbEntryVal);
                }
                if (checkApiCallToGpayVal) {
                    clearTimeout(checkApiCallToGpayVal);
                }
                document.getElementById('approvedNotification').style.display = "none";
                document.getElementById("loading").style.display = "none";
                var form = document.getElementById("googlePayResponseForm");
                form.action = myMapGpayResponse.RETURN_URL;
                if (myMapGpayResponse.encdata) {
                    form.innerHTML += ('<input type="hidden" name="encdata" value="' + myMapGpayResponse.encdata + '">');
                } else {
                    for (key in myMapGpayResponse) {
                        form.innerHTML += ('<input type="hidden" name="' + key + '" value="' + myMapGpayResponse[key] + '">');
                    }
                }
                document.getElementById("googlePayResponseForm").submit();
            } else if (txnResponseCode == 006 || !txnResponseCode) {

                checkApiCallToGpayVal = setTimeout(checkApiCallToGpay(pgRefNum), 60000);

            } else if (txnResponseCode == 000) {
                if (checkApiCallToGpayVal) {
                    clearTimeout(checkApiCallToGpayVal);
                }
            }
        }
        xhttp1.send(data1);
    }
}
function alphabetEnterPhone(element) {
    if ((element.value).trim()) {
        element.value = element.value.replace(/[^a-zA-Z ]/g, '').replace(/ +/g, ' ');;
    } else {
        element.value = element.value.replace(/[^a-zA-Z]/g, '');
    }
}
function numberEnterPhone(element) {
    if (window.matchMedia("(max-width: 680px)")) {
        var elementValue = element.value;
        if (!(/^[0-9]+$/.test(elementValue))) {
            element.value = elementValue.replace(/[^0-9]/g, "");
        }
    }
}
function conditionOnIeBrowser() {
    if (isIE) {
        var savDetailsCvv = document.getElementsByClassName('savDetailsCvv');
        document.getElementById('cvvNumber').type = "password";
        for (var i = 0; i < savDetailsCvv.length; i++) {
            savDetailsCvv[i].type = "password";
        }
    }
}
function mediaQueryJs(x) {
    if (x.matches) {
        document.getElementById('cvvNumber').type = "text";
    }
}
// function windowWidthCount() {
// if (window.matchMedia("(max-width: 1250px)")) {
// var wndw90 = (window.innerWidth * 90) / 100;
// document.getElementsByClassName('container')[0].style.width = wndw90 + 'px';
// document.getElementsByClassName('container')[1].style.width = wndw90 + 'px';
// document.getElementsByClassName('container')[2].style.width = wndw90 + 'px';
// }
// }
function bankImageClicked(clicked_id) {
    var optionsCount = document.getElementById("netbankingList").options;
    for (var i = 0; n = optionsCount.length; i < n, i++) {
        if (optionsCount[i].text == clicked_id) {
            optionsCount[i].selected = true;
            // document.getElementById(clicked_id).classList.add("activeBankLogo");
            document.getElementById("netBankingBtn").classList.add("active");
            document.getElementById("netBankingBtn").disabled = false;
            document.getElementById("bankErr").style.display = "none";
            bankElement.classList.remove("redLine");
            break;
        }
    }
}

var bankElement
function isValidBankName() {
	
    bankElement = document.getElementById("netbankingList");
    var bankVal = bankElement.value;
    if (bankVal == "Select Bank") {
        document.getElementById("bankErr").style.display = "block";
        bankElement.classList.add("redLine");
    }
    else {
        var setDropdownValue = document.getElementById("netbankingList").value;
        document.getElementById("nbMopType").value = setDropdownValue;
        var innerAmountValue = document.getElementById('innerAmount').innerHTML.replace("INR", "").trim();
        document.getElementById('amountNetBanking').value = innerAmountValue;
        document.getElementById('loading').style.display = "block";
        document.getElementById('netBankingBtn').classList.remove('active');
        document.getElementById('netBankingBtn').classList.add('inactive');
        document.getElementById('netBankingBtn').disabled = true;
        document.getElementById('netBankingFormId').submit();
    }
}
function submitNetbanking() {
    if (document.getElementById('netbankingList').classList.contains('redLine')) {
		if(isValidBankName()){
			
		}else{
		    return false;
		}
    } else if (isValidBankName()) {

    }
}

function classAdded(){
    document.querySelector(".csls_sidebar-section").classList.add("show-sidebar");
    document.querySelector("body").classList.add("body-overlay");
}

function classRemove(){
    document.querySelector(".csls_sidebar-section").classList.remove("show-sidebar");
    document.querySelector("body").classList.remove("body-overlay");
}

function loaderClassRemove(){
    setTimeout(function(e){
        document.querySelector(".csls_loader-wrapper").classList.remove("active-loader");
    }, 1000);
}
