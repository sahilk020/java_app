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
 var bankList= [{ "name": "Axis Bank", "code": 1005 }, { "name": "IDFC NETBANKING", "code": 1316 }, { "name": "Jammu And Kashmir Bank", "code": 1041 }, { "name": "Fincare Bank", "code": 1222 }, { "name": "DBS Bank Ltd", "code": 1300 }, { "name": "DCB Bank - Personal", "code": 1301 }, { "name": "Shivalik Bank", "code": 1310 }, { "name": "AU Small Finance", "code": 1311 }, { "name": "Lakshmi Vilas Bank - Corporate ", "code": 1303 }, { "name": "Lakshmi Vilas Bank - Corporate Netbanking", "code": 2008 }, { "name": "Bank of India - Corporate", "code": 1302 }, { "name": "Punjab National Bank - Corporate", "code": 1304 }, { "name": "State Bank of India - Corporate", "code": 1305 }, { "name": "Union Bank of India - Corporate", "code": 1306 }, { "name": "Dhanlaxmi Bank Corporate", "code": 1307 }, { "name": "ICICI Corporate Netbanking", "code": 1308 }, { "name": "Ratnakar Corporate Banking", "code": 1309 }, { "name": "Shamrao Vithal Co op Bank - Corporate", "code": 1217 }, { "name": "Zoroastrian Co-op Bank", "code": 1219 }, { "name": "Punjab National Bank - Retail Net Banking", "code": 2013 }, { "name": "Laxmi Vilas Bank - Retail Net Banking", "code": 1095 }, { "name": "Karnataka Gramina Bank", "code": 1221 }, { "name": "Varachha Co-operative Bank Limited", "code": 1213 }, { "name": "Thane Bharat Sahakari Bank Ltd", "code": 1210 }, { "name": "Syndicate Bank", "code": 1098 }, { "name": "YES BANK CB", "code": 1022 }, { "name": "Bank Of Bahrain And Kuwait", "code": 1043 }, { "name": "Bank Of India", "code": 1009 }, { "name": "Bank Of Maharashtra", "code": 1064 }, { "name": "Canara Bank", "code": 1055 }, { "name": "Bank Of Baroda Corporate", "code": 1092 },{ "name": "Bank Of Baroda Retail", "code": 1093 },{ "name": "Catholic Syrian Bank", "code": 1094 }, { "name": "Standard Chartered Bank", "code": 1097 }, { "name": "Axis Corporate Bank", "code": 1099 }, { "name": "Punjab National Bank Corporate Accounts", "code": 1096 }, { "name": "Central Bank of India", "code": 1063 }, { "name": "CitiBank", "code": 1010 }, { "name": "City Union Bank", "code": 1060 }, { "name": "DCB Bank", "code": 1040 }, { "name": "DCB Bank Corporate", "code": 1292 }, { "name": "Deutsche Bank", "code": 1026 }, { "name": "Dhanlaxmi Bank", "code": 1070 }, { "name": "Federal Bank", "code": 1027 }, { "name": "Hdfc Bank", "code": 1004 }, { "name": "ICICI Corporate Bank", "code": 1100 }, { "name": "Icici Bank", "code": 1013 }, { "name": "PNB Corporate Bank", "code": 1101 }, { "name": "HSBC Bank", "code": 1102 }, { "name": "Indian Bank", "code": 1069 }, { "name": "Indian Overseas Bank", "code": 1049 }, { "name": "Indusind Bank", "code": 1054 }, { "name": "IDBI Bank", "code": 1003 }, { "name": "Karnatka Bank Ltd", "code": 1032 }, { "name": "KarurVysya Bank", "code": 1048 }, { "name": "Kotak Bank", "code": 1012 }, { "name": "Punjab and Sind Bank", "code": 1296 }, { "name": "Punjab National Bank", "code": 1002 }, { "name": "RBL Bank Limited", "code": 1053 }, { "name": "South Indian Bank", "code": 1045 }, { "name": "State Bank Of India", "code": 1030 }, { "name": "Tamilnad Mercantile Bank", "code": 1065 }, { "name": "UCO Bank", "code": 1103 }, { "name": "Union Bank Of India", "code": 1038 }, { "name": "COSMOS Bank", "code": 1104 },  { "name": "SaraSwat Bank", "code": 1056}, { "name": "Yes Bank", "code": 1001 }, { "name": "Bharat Co-Op Bank", "code": 2003 }, { "name": "Dena Bank", "code": 2004 }, { "name": "Karur Vysya - Corporate Netbanking", "code": 2007 }, { "name": "Nainital Bank", "code": 2010 }, { "name": "Punjab And Maharashtra Co-operative Bank Limited", "code": 2011 }, { "name": "Equitas Bank", "code": 1106 }, { "name": "Shamrao Vithal Co-operative Bank Ltd", "code": 2015 }, { "name": "IDFC FIRST Bank Limited", "code": 1107 }, { "name": "Union Bank - Corporate Netbanking", "code": 2017 },  { "name": "Janata Sahakari Bank Pune", "code": 1072},   { "name": "Tamil Nadu State Co-operative Bank", "code": 1201}, { "name": "NKGSB Co op Bank", "code": 1202}, { "name": "TJSB Bank", "code": 1203}, { "name": "Kalyan Janata Sahakari Bank", "code": 1204}, { "name": "Mehsana urban Co-op Bank", "code": 1205}, { "name": "Bandhan Bank", "code": 1206}, { "name": "Digibank by DBS", "code": 1207}, { "name": "Bassien Catholic Coop Bank", "code": 1208}, { "name": "The Kalupur Commercial Co-Operative Bank", "code": 1209}, { "name": "Suryoday Small Finance Bank", "code": 1211},{ "name": "ESAF Small Finance Bank", "code": 1212},{ "name": "North East Small Finance Bank Ltd", "code": 1214},{ "name": "RBL Bank Limited Corporate Banking", "code": 1216}, { "name": "AU small finance bank", "code": 1220}, {"name":"Utkarsh Small Finance bank", "code":1312},{"name":"The Surat People’s Co-operative Bank Limited", "code":1313},{"name":"Gujarat State Co-operative Bank Limited", "code":1314},{"name":"HSBC Retail Netbanking", "code":1315}];
 var bankSorted=bankList.sort((a,b) => (a.name > b.name) ? 1 : ((b.name > a.name) ? -1 : 0))
 var paymentMapping = {
		 bank: bankSorted,
         wallet: [{"code":"PPL","name":"Paytm Wallet"},{"code":"MWL","name":"Mobikwik Wallet"},{"code":"OLAWL","name":"OlaMoney Wallet"},{"code":"MMWL","name":"MatchMove Wallet"},{"code":"APWL","name":"AmazonPay Wallet"},{"code":"AWL","name":"Airtel Pay Wallet"},{"code":"FCWL","name":"FreeCharge Wallet"},{"code":"GPWL","name":"Google Pay Wallet"},{"code":"ICWL","name":"Itz Cash Wallet"},{"code":"JMWL","name":"Jio Money Wallet"},{"code":"MPWL","name":"M Pesa Wallet"},{"code":"OXWL","name":"Oxyzen Wallet"},{"code":"PPWL","name":"Phone Pe Wallet"},{"code":"SBWL","name":"Sbi Buddy Wallet"},{"code":"ZCWL","name":"Zip Cash Wallet"},{"code":"YBWL","name":"Yesbank Wallet"},{"code":"ICC","name":"ICC CashCard"},{"code":"PCH","name":"Pay World Money"},{"code":"DCW","name":"DCB Cippy"}]
};
// var paymentMapping = {
// 		 bank: [{ "name": "Axis Bank", "code": 1005 }, { "name": "Fincare Bank", "code": 1222 }, { "name": "DBS Bank Ltd", "code": 1300 }, { "name": "DCB Bank - Personal", "code": 1301 }, { "name": "Shivalik Bank", "code": 1310 }, { "name": "AU Small Finance", "code": 1311 }, { "name": "Lakshmi Vilas Bank - Corporate ", "code": 1303 }, { "name": "Lakshmi Vilas Bank - Corporate Netbanking", "code": 2008 }, { "name": "Bank of India - Corporate", "code": 1302 }, { "name": "Punjab National Bank - Corporate", "code": 1304 }, { "name": "State Bank of India - Corporate", "code": 1305 }, { "name": "Union Bank of India - Corporate", "code": 1306 }, { "name": "Dhanlaxmi Bank Corporate", "code": 1307 }, { "name": "ICICI Corporate Netbanking", "code": 1308 }, { "name": "Ratnakar Corporate Banking", "code": 1309 }, { "name": "Shamrao Vithal Co op Bank - Corporate", "code": 1217 }, { "name": "Zoroastrian Co-op Bank", "code": 1219 }, { "name": "Punjab National Bank - Retail Net Banking", "code": 2013 }, { "name": "Laxmi Vilas Bank - Retail Net Banking", "code": 1095 }, { "name": "Karnataka Gramina Bank", "code": 1221 }, { "name": "Varachha Co-operative Bank Limited", "code": 1213 }, { "name": "Thane Bharat Sahakari Bank Ltd", "code": 1210 }, { "name": "Syndicate Bank", "code": 1098 }, { "name": "YES BANK CB", "code": 1022 }, { "name": "Bank Of Bahrain And Kuwait", "code": 1043 }, { "name": "Bank Of India", "code": 1009 }, { "name": "Bank Of Maharashtra", "code": 1064 }, { "name": "Canara Bank", "code": 1055 }, { "name": "Bank Of Baroda Corporate", "code": 1092 },{ "name": "Bank Of Baroda Retail", "code": 1093 },{ "name": "Catholic Syrian Bank", "code": 1094 }, { "name": "Standard Chartered Bank", "code": 1097 }, { "name": "Axis Corporate Bank", "code": 1099 }, { "name": "Punjab National Bank Corporate Accounts", "code": 1096 }, { "name": "Central Bank of India", "code": 1063 }, { "name": "CitiBank", "code": 1010 }, { "name": "City Union Bank", "code": 1060 }, { "name": "DCB Bank", "code": 1040 }, { "name": "DCB Bank Corporate", "code": 1292 }, { "name": "Deutsche Bank", "code": 1026 }, { "name": "Dhanlaxmi Bank", "code": 1070 }, { "name": "Federal Bank", "code": 1027 }, { "name": "Hdfc Bank", "code": 1004 }, { "name": "ICICI Corporate Bank", "code": 1100 }, { "name": "Icici Bank", "code": 1013 }, { "name": "PNB Corporate Bank", "code": 1101 }, { "name": "HSBC Bank", "code": 1102 }, { "name": "Indian Bank", "code": 1069 }, { "name": "Indian Overseas Bank", "code": 1049 }, { "name": "Indusind Bank", "code": 1054 }, { "name": "IDBI Bank", "code": 1003 }, { "name": "Jammu And Kashmir Bank", "code": 1041 }, { "name": "Karnatka Bank Ltd", "code": 1032 }, { "name": "KarurVysya Bank", "code": 1048 }, { "name": "Kotak Bank", "code": 1012 }, { "name": "Punjab and Sind Bank", "code": 1296 }, { "name": "Punjab National Bank", "code": 1002 }, { "name": "RBL Bank Limited", "code": 1053 }, { "name": "South Indian Bank", "code": 1045 }, { "name": "State Bank Of India", "code": 1030 }, { "name": "Tamilnad Mercantile Bank", "code": 1065 }, { "name": "UCO Bank", "code": 1103 }, { "name": "Union Bank Of India", "code": 1038 }, { "name": "COSMOS Bank", "code": 1104 },  { "name": "SaraSwat Bank", "code": 1056}, { "name": "Yes Bank", "code": 1001 }, { "name": "Bharat Co-Op Bank", "code": 2003 }, { "name": "Dena Bank", "code": 2004 }, { "name": "Karur Vysya - Corporate Netbanking", "code": 2007 }, { "name": "Nainital Bank", "code": 2010 }, { "name": "Punjab And Maharashtra Co-operative Bank Limited", "code": 2011 }, { "name": "Equitas Bank", "code": 1106 }, { "name": "Shamrao Vithal Co-operative Bank Ltd", "code": 2015 }, { "name": "IDFC FIRST Bank Limited", "code": 1107 }, { "name": "Union Bank - Corporate Netbanking", "code": 2017 },  { "name": "Janata Sahakari Bank Pune", "code": 1072},   { "name": "Tamil Nadu State Co-operative Bank", "code": 1201}, { "name": "NKGSB Co op Bank", "code": 1202}, { "name": "TJSB Bank", "code": 1203}, { "name": "Kalyan Janata Sahakari Bank", "code": 1204}, { "name": "Mehsana urban Co-op Bank", "code": 1205}, { "name": "Bandhan Bank", "code": 1206}, { "name": "Digibank by DBS", "code": 1207}, { "name": "Bassien Catholic Coop Bank", "code": 1208}, { "name": "The Kalupur Commercial Co-Operative Bank", "code": 1209}, { "name": "Suryoday Small Finance Bank", "code": 1211},{ "name": "ESAF Small Finance Bank", "code": 1212},{ "name": "North East Small Finance Bank Ltd", "code": 1214},{ "name": "RBL Bank Limited Corporate Banking", "code": 1216}, { "name": "AU small finance bank", "code": 1220}, {"name":"Utkarsh Small Finance bank", "code":1312},{"name":"The Surat People’s Co-operative Bank Limited", "code":1313},{"name":"Gujarat State Co-operative Bank Limited", "code":1314},{"name":"HSBC Retail Netbanking", "code":1315}],
//     wallet: [{"code":"PPL","name":"Paytm Wallet"},{"code":"MWL","name":"Mobikwik Wallet"},{"code":"OLAWL","name":"OlaMoney Wallet"},{"code":"MMWL","name":"MatchMove Wallet"},{"code":"APWL","name":"AmazonPay Wallet"},{"code":"AWL","name":"Airtel Pay Wallet"},{"code":"FCWL","name":"FreeCharge Wallet"},{"code":"GPWL","name":"Google Pay Wallet"},{"code":"ICWL","name":"Itz Cash Wallet"},{"code":"JMWL","name":"Jio Money Wallet"},{"code":"MPWL","name":"M Pesa Wallet"},{"code":"OXWL","name":"Oxyzen Wallet"},{"code":"PPWL","name":"Phone Pe Wallet"},{"code":"SBWL","name":"Sbi Buddy Wallet"},{"code":"ZCWL","name":"Zip Cash Wallet"},{"code":"YBWL","name":"Yesbank Wallet"},{"code":"ICC","name":"ICC CashCard"},{"code":"PCH","name":"Pay World Money"},{"code":"DCW","name":"DCB Cippy"}]
// };
var bankArray = ['1005', '1004', '1013', '1012', '1030', '1001'];

var amexFlag = false; {
    history.pushState(null, null, 'irctcPaymentPage.jsp');
    window.addEventListener('popstate', function (event) {
        history.pushState(null, null, 'irctcPaymentPage.jsp');
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
    document.getElementById('radioError').style.display = "none";
    document.getElementById('cvvErrorSav').style.display = "none";
    document.getElementById('invalidCvvErrorSav').style.display = "none";
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
    document.getElementById('radioError').style.display = "none";
    document.getElementById('invalidCvvErrorSav').style.display = "none";
    document.getElementById('cvvErrorSav').style.display = 'none';
    var selectedSaveDetailsLength = document.getElementsByClassName('selectedSaveDetails').length;
    if (!selectedSaveDetailsLength) {
        document.getElementById('radioError').style.display = "block";
        return false;
    }
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

function BackMobileView(){
    let innerwidth = window.innerWidth
    if(innerwidth < 991){
    document.getElementById('paymentTabs').style.display = 'block';
    document.getElementById('backarrow').style.display = 'none';

    document.getElementById('pricesectionmobile').style.display = 'block';

    document.getElementById('debitWithPin').style.display = 'none';
    document.getElementById('upi').style.display = 'none';
    document.getElementById('iMudra').style.display = 'none';
    document.getElementById('netBanking').style.display = 'none';
    document.getElementById('paymentSections').style.display = 'none';
    document.getElementById('mobileviewFirst').style.display = 'block';

    }
    else{
        document.getElementById('debitWithPin').style.display = 'block';  
        document.getElementById('mobileviewFirst').style.display = 'block';
        document.getElementById('upi').style.display = 'block';
        document.getElementById('iMudra').style.display = 'block';
        document.getElementById('netBanking').style.display = 'block';

    }
}
var screenGlobalSize;
var paymentTypeGlobal;
var currentelementGlobal;
window.addEventListener('resize', function(event) {
    let innerwidth = window.innerWidth;
    // if(innerwidth<990){
     if(innerwidth<screenGlobalSize-10 || innerwidth>screenGlobalSize+10){
         screenGlobalSize=innerwidth;
         BackMobileView();
         showStuff(currentelementGlobal, paymentTypeGlobal)
            if(innerwidth<990){

     }
     else{
    }
     }
   
});

function showStuff(currentElement, paymentType) {
    debugger
    currentelementGlobal=currentElement;
    paymentTypeGlobal=paymentType;
    document.getElementById('vpaCheck1').value = '';
       document.getElementById('expirymonth').value = '';
            document.getElementById('expiryyear').value = '';
            document.getElementById('setbankname').innerHTML = 'Bank Name';
            document.getElementById('upiid').innerHTML = '***********';
            document.getElementById('cardholdercvv').innerHTML = '0000 0000 0000 0000';
            document.getElementById('cardholdername').innerHTML = 'Card Holder Name';
            document.getElementById('expdatemonth').innerHTML = 'MM';
            document.getElementById('expdateyear').innerHTML = 'YY';


    let innerwidth = window.innerWidth
    if(innerwidth < 991){
        document.getElementById('backarrow').style.display = 'block';
        document.getElementById('paymentTabs').style.display = 'none';
        document.getElementById('pricesectionmobile').style.display = 'block';
        document.getElementById('paymentSections').style.display = 'block';
        document.getElementById('mobileviewFirst').style.display = 'block';


        if(paymentType=="creditCard"){
            document.getElementById('paymentSections').style.display = 'block';
            document.getElementById('debitWithPin').style.display = 'block';
            document.getElementById('upi').style.display = 'none';
            document.getElementById('iMudra').style.display = 'none';
            document.getElementById('netBanking').style.display = 'none';
        }
        if(paymentType=="debitCard"){
            document.getElementById('paymentSections').style.display = 'block';

            document.getElementById('debitWithPin').style.display = 'block';
            document.getElementById('upi').style.display = 'none';
            document.getElementById('iMudra').style.display = 'none';
            document.getElementById('netBanking').style.display = 'none';
        }
        if(paymentType=="upi"){
            document.getElementById('paymentSections').style.display = 'block';

            document.getElementById('debitWithPin').style.display = 'none';
            document.getElementById('upi').style.display = 'block';
            document.getElementById('iMudra').style.display = 'none';
            document.getElementById('netBanking').style.display = 'none';
        }
        if(paymentType=="iMudra"){
            document.getElementById('paymentSections').style.display = 'block';

            document.getElementById('debitWithPin').style.display = 'none';
            document.getElementById('upi').style.display = 'none';
            document.getElementById('iMudra').style.display = 'block';
            document.getElementById('netBanking').style.display = 'none';
        }
        if(paymentType=="netBanking"){
            document.getElementById('paymentSections').style.display = 'block';

            document.getElementById('debitWithPin').style.display = 'none';
            document.getElementById('upi').style.display = 'none';
            document.getElementById('iMudra').style.display = 'none';
            document.getElementById('netBanking').style.display = 'block';  
        }
        if(paymentType=="QRCODE"){
            document.getElementById('paymentSections').style.display = 'block';

            document.getElementById('debitWithPin').style.display = 'none';
            document.getElementById('upi').style.display = 'none';
            document.getElementById('iMudra').style.display = 'none';
            document.getElementById('netBanking').style.display = 'none'; 
        }
    }
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
              document.getElementById('surchargeAmountMob').innerHTML=surcharge;

            orderfootDetails1.innerHTML = ccCopy;
            orderfootDetails2.innerHTML = ccCopy;
            document.getElementById('credit_cards').style.display = "block";
            document.getElementById('netBankingSidebar').style.display = "none";
            document.getElementById('walletSidebar').style.display = "none";
            document.getElementById('upiSidebar').style.display = "none";
            document.getElementById('creditDebitSidebar').style.display = "block";
            document.getElementById('QRSidebar').style.display = "none";

           break;
        case "debitCard":
        	var surcharge= document.getElementById('dcsurchargeAmount').value;
     	    document.getElementById('surchargeAmount').innerHTML=surcharge;
            orderfootDetails1.innerHTML = dcCopy;
            orderfootDetails2.innerHTML = dcCopy;
            document.getElementById('debit_cards').style.display = "block";
            document.getElementById('netBankingSidebar').style.display = "none";
            document.getElementById('walletSidebar').style.display = "none";
            document.getElementById('upiSidebar').style.display = "none";
            document.getElementById('creditDebitSidebar').style.display = "block";
            document.getElementById('QRSidebar').style.display = "none";

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
            document.getElementById('netBankingSidebar').style.display = "none";
            document.getElementById('walletSidebar').style.display = "none";
            document.getElementById('upiSidebar').style.display = "block";
            document.getElementById('creditDebitSidebar').style.display = "none";
            document.getElementById('QRSidebar').style.display = "none";

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
            document.getElementById('netBankingSidebar').style.display = "none";
            document.getElementById('walletSidebar').style.display = "block";
            document.getElementById('upiSidebar').style.display = "none";
            document.getElementById('creditDebitSidebar').style.display = "none";
            document.getElementById('QRSidebar').style.display = "none";

            break;
        case "netBanking":
        	var surcharge= document.getElementById('nbsurchargeAmount').value;
      	    document.getElementById('surchargeAmount').innerHTML=surcharge;
            orderfootDetails1.innerHTML = netBankingCopy;
            orderfootDetails2.innerHTML = netBankingCopy;
            document.getElementById('netBankingSidebar').style.display = "block";
            document.getElementById('walletSidebar').style.display = "none";
            document.getElementById('upiSidebar').style.display = "none";
            document.getElementById('creditDebitSidebar').style.display = "none";
            document.getElementById('QRSidebar').style.display = "none";

            break;
        case "QRCODE":
        	var surcharge= document.getElementById('nbsurchargeAmount').value;
      	    document.getElementById('surchargeAmount').innerHTML=surcharge;
            
            document.getElementById('netBankingSidebar').style.display = "none";
            document.getElementById('walletSidebar').style.display = "none";
            document.getElementById('upiSidebar').style.display = "none";
            document.getElementById('creditDebitSidebar').style.display = "none";
            document.getElementById('QRSidebar').style.display = "block";

            submitQRForm() 

            break;
    }
    addConvenienceFee(paymentType);
    formReset();
    document.getElementById("setbanknameWalletImg").src = "../image/bank/selectwallet.png";
    document.getElementById("walletSideBarById").innerHTML = "0000000000";

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
    xhrUpiPay.open('POST', 'upiPay', true);
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
        if (responseCode == "398") {
        	
			document.getElementById('loading').style.display = "none";
			document.getElementById('approvedNotification').style.display = "none";
			document.getElementById('loadingRedirect').style.display = "block";
			document.getElementById('approvedNotificationRedirect').style.display = "block";
        	document.getElementById('upi-sbmt').classList.remove("payActive");
            document.getElementById('upiPostForm').action = "upiRedirect";
            document.getElementById('upiPostForm').submit();
            return false;
        }
     
        else if (responseCode == "000") {
            if (transactionStatus == "Sent to Bank") {
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
function QRSubmit(upiNameProvided, upiNumberProvided, paymentType, mopType, amount, currencyCode) {
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
	document.getElementById('loading').style.display = "block";
    document.getElementById("QRFooter").style.display = "none";

    data.append('token', token);
    data.append('upiCustName', upiNameProvided);
    data.append('paymentType', paymentType);
    data.append('mopType', mopType);
    data.append('amount', amount);
    data.append('currencyCode', currencyCode);
    xhrUpiPay = new XMLHttpRequest();
    xhrUpiPay.open('POST', 'upiPay', true);
    xhrUpiPay.onload = function () {
        var obj = JSON.parse(this.response);
      //  document.getElementById('loading').style.display = "block";
       // document.getElementById('approvedNotification').style.display = "block";

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
            QRSTRING = obj.QRSTRING;
                  
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
        if (responseCode == "398") {
        	
			document.getElementById('loading').style.display = "none";
			document.getElementById('approvedNotification').style.display = "none";
			document.getElementById('loadingRedirect').style.display = "block";
			document.getElementById('approvedNotificationRedirect').style.display = "block";
        	document.getElementById('upi-sbmt').classList.remove("payActive");
            document.getElementById('upiPostForm').action = "upiRedirect";
            document.getElementById('upiPostForm').submit();
            return false;
        }
        else if (responseCode == "000") {
        	document.getElementById('loading').style.display = "none";
        	
            if (transactionStatus == "Sent to Bank") {
            	QRdata="  <img src='data:image/png;base64,"+ QRSTRING+" ' alt='Sorry Abhi' style='text-align:center'>"

			document.getElementById("QRIMG").innerHTML=QRdata;
            	document.getElementById('loadingRedirectqr').style.display = "block";
            	document.getElementById('approvedNotificationRedirectqr').style.display = "block";
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
    xhrUpi.open('POST', 'verifyUpiResponse', true);
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
//                if (reqCount == 0) {
//                    sleep(10000);
//                    reqCount = reqCount + 1;
//                } else {
//                    sleep(10000);
//                }
//                verifyUpiResponseReceived(pgRefNum);
                	setInterval(() => {
                    	if (reqCount == 0) {
                            //sleep(10000);
                            reqCount = reqCount + 1;
                        } else {
                            //sleep(10000);
                        }
                    	 verifyUpiResponseReceived(pgRefNum);
    				}
                	, 10000);
            } else {
                document.getElementById('approvedNotification').style.display = "none";
                document.getElementById("loading").style.display = "none";
                var form = document.getElementById("upiResponseForm");
                form.action = myMap.RETURN_URL;

                if (myMap.encdata) {
                    form.innerHTML += ('<input type="hidden" name="ENCDATA" value="' + myMap.encdata + '">');
                    form.innerHTML += ('<input type="hidden" name="PAY_ID" value="' + obj.payId + '">');
                    
                } else {
                    for (key in myMap) {
                        form.innerHTML += ('<input type="hidden" name="' + key + '" value="' + myMap[key] + '">');
                    }
                }
                document.getElementById("upiResponseForm").submit();
            }
        } else {
            //sleep(10000);
//            verifyUpiResponseReceived(pgRefNum);
        	  setInterval(() => {
              	verifyUpiResponseReceived(pgRefNum);
  			}, 10000);
        }
    };
    xhrUpi.send(data);
}

function pageInfo() {
    screenGlobalSize = window.innerWidth;

    // let innerwidth = window.innerWidth;
 
    //         if(innerwidth<990){
    //             document.getElementById('mobileviewSecond2').style.display = 'block';

    //  }
    //  else{
    //     document.getElementById('mobileviewSecond2').style.display = 'none';
    // }
    document.getElementById('netBankingSidebar').style.display = "none";
    document.getElementById('walletSidebar').style.display = "none";
    document.getElementById('upiSidebar').style.display = "none";
    document.getElementById('creditDebitSidebar').style.display = "block";
    document.getElementById('QRSidebar').style.display = "none";

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
        mainCTAInfo.style.display = "none";
        divSaveCard.style.display = "none";
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
    var b_object = {class:'bank', cta:'netBankingBtn',input:"#netBankingFormId input[name='mopType']"}
    if (obj.nbMopType.length > 0) {
        createRadioListItems(b_object, obj.nbMopType);
        var _selectNetBanking = document.getElementById('netbankingList');
       var result= paymentMapping[b_object.class].filter(function(item){
        
            if(obj.nbMopType.indexOf((item.code).toString()) != -1){
                var bankListItem = document.createElement('a');
                       bankListItem.classList.add('dropdown-item');
                       bankListItem.innerHTML = item.name;
                       bankListItem.setAttribute('value',item.code);
                       _selectNetBanking.appendChild(bankListItem);   
            }
          });
            
        //   result.forEach(function(_bank) {
        //     var _netBank = paymentMapping[b_object.class].find(function(y) { return y.code == _bank});
        //     if(_netBank){
        //        var bankListItem = document.createElement('a');
        //        bankListItem.classList.add('dropdown-item');
              
        //        bankListItem.innerHTML = _netBank.name;
        //        bankListItem.setAttribute('value',_bank);
        //        _selectNetBanking.appendChild(bankListItem);
        //     }
        // });

        var bankBtn = document.getElementById('bankDropdownMenuButton');
        var bankRadioList = document.querySelectorAll('#netBankingFormId .custom-control-input');
	    
        bankBtn.addEventListener('click', function(event){
		var actualBankselected = _selectNetBanking.value;
		var child = _selectNetBanking.lastElementChild;  
        while (child) { 
            _selectNetBanking.removeChild(child); 
            child = _selectNetBanking.lastElementChild; 
        } 		
        	
         paymentMapping[b_object.class].filter(function(item){
            debugger
                if(obj.nbMopType.indexOf((item.code).toString()) != -1){
                    var bankListItem = document.createElement('a');
                           bankListItem.classList.add('dropdown-item');
                           bankListItem.innerHTML = item.name;
                           bankListItem.setAttribute('value',item.code);
                           _selectNetBanking.appendChild(bankListItem);
   
                }
              });
		// obj.nbMopType.forEach(function(_bank) {
        //     var _netBank = paymentMapping[b_object.class].find(function(y) { return y.code == _bank});
        //     if(_netBank){
        //        var bankListItem = document.createElement('a');
        //        bank2ListItem.classList.add('dropdown-item');
        //        bankListItem.setAttribute('value',_bank);
		// 	   if(actualBankselected == _netBank.code){
		// 		bankListItem.setAttribute("style", "background-color: #6694e9;");
		// 	   }
        //        bankListItem.innerHTML = _netBank.name;
        //        _selectNetBanking.appendChild(bankListItem);
        //     }
        // });
			event.stopPropagation();
			_selectNetBanking.classList.toggle('show');

        });

        _selectNetBanking.addEventListener('click', function (event) {
            document.getElementById("setbankname").innerHTML = event.target.innerText;
			var selectedBank = event.target.getAttribute('value');
			if(selectedBank == null){
				return false;
			}
            if (selectedBank) {
                document.querySelector(b_object.input).setAttribute('value', selectedBank);
                _selectNetBanking.value= selectedBank;
            }
            bankBtn.innerHTML = event.target.innerText;
            document.getElementById("netBankingBtn").classList.add("active");
            document.getElementById("netBankingBtn").classList.remove("inactive");
            document.getElementById("netBankingBtn").disabled = false;
            bankRadioList.forEach(function (x) {
				if(x.value == selectedBank){
					x.checked = true;
				} else {
                    x.checked = false;
                }
            });
        });

        window.onclick = function(event) {
            if (!event.target.matches('.dropdown-toggle')) {
                _selectNetBanking.classList.remove('show')
            }
        }
    }
    
    var allLi = "";
    if (pageInfoObj.express_pay && document.getElementsByClassName('saveCardDetails').length) {
        allLi += '<li class="tabLi" id="saveCardLi" onclick="showStuff(this, ' + "'saveCard'" + ')" data-id="saveCard"><img src="../assets/images/IconsBackground.svg" alt="credit card"><a>Saved Cards</a><div class="itemarrow"><img src="../assets/images/Shape.png" alt="Item Arrow" /></div></li>';
    }
    if (obj.creditCard) {
        var creditCard = "creditCard";
        allLi += '<li class="tabLi" id="creditLi" onclick="showStuff(this, ' + "'creditCard'" + ')" data-id="debitWithPin"><img src="../assets/images/IconsBackground.svg" alt="credit card"><a> Credit Card</a><div class="itemarrow"><img src="../assets/images/Shape.png" alt="Item Arrow" /></div></li>';
        document.getElementById('credit_cards').innerHTML = collectTotalMob;
    }
    if (obj.debitCard) {
        var debitCard = 'debitCard';
        allLi += '<li class="tabLi" id="debitLi" onclick="showStuff(this, ' + "'debitCard'" + ')" data-id="debitWithPin"> <img src="../assets/images/Icons Background(1).svg" alt="Debit card"><a>Debit Card</a><div class="itemarrow"><img src="../assets/images/Shape.png" alt="Item Arrow" /></div></li>';
        document.getElementById('debit_cards').innerHTML = collectSecondMob;
        if (obj.creditCard) {
            document.getElementById('debit_cards').style.display = "none";
        }
    }
    if (obj.prepaidCard) {
        var prepaidCard = "prepaidCard";
        allLi += '<li class="tabLi" id="prepaidLi" onclick="showStuff(this, ' + "'prepaidCard'" + ')" data-id="debitWithPin"><a>Prepaid Card</a><div class="itemarrow"><img src="../assets/images/Shape.png" alt="Item Arrow" /></div></li>';
        document.getElementById('prepaid_cards').innerHTML = collectThirdMob;
        if (obj.creditCard || obj.debitCard) {
            document.getElementById('prepaid_cards').style.display = "none";
        }
    }
    if (obj.upi) {
        allLi += '<li class="tabLi" id="upiLi" onclick="showStuff(this, ' + "'upi'" + ')" data-id="upi"><img src="../assets/images/Icons Background (2).svg" alt="UPI"><a>UPI Payment </a><div class="itemarrow"><img src="../assets/images/Shape.png" alt="Item Arrow" /></div></li>';
    }
    if (obj.upi && obj.googlePay) {
        allLi += '<li class="tabLi" id="gpayLi" onclick="showStuff(this, ' + "'googlePay'" + ')" data-id="googlePay"><a><img src="../image/gpay.png"/></a><div class="itemarrow"><img src="../assets/images/Shape.png" alt="Item Arrow" /></div></li>';
    }
    if (obj.autoDebit) {
        allLi += '<li class="tabLi" id="autoDebitLi" onclick="showStuff(this,' + "'autoDebit'" + ')" data-id="autoDebit"><a>auto Debit</a><div class="itemarrow"><img src="../assets/images/Shape.png" alt="Item Arrow" /></div></li>';
    }
    if (obj.iMudra) {
        allLi += '<li class="tabLi" id="iMudraLi" onclick="showStuff(this,' + "'iMudra'" + ')" data-id="iMudra"><img src="../assets/images/Icons Background (3).svg" alt="Wallet"><a>Wallet</a><div class="itemarrow"><img src="../assets/images/Shape.png" alt="Item Arrow" /></div></li>';
    }
    if (obj.netBanking) {
        allLi += '<li class="tabLi" id="netBankingLi" onclick="showStuff(this,' + "'netBanking'" + ')" data-id="netBanking"> <img src="../assets/images/Icons Background (4).svg" alt="Net Banking"><a>Net Banking</a><div class="itemarrow"><img src="../assets/images/Shape.png" alt="Item Arrow" /></div></li>';
    }
    if (obj.QRCODE) {
        allLi += '<li class="tabLi" id="QRCODELi" onclick="showStuff(this,' + "'QRCODE'" + ')" data-id="QRCODE"> <img src="../assets/images/Icons Background (4).svg" alt="QRCODE"><a>QRCODE</a><div class="itemarrow"><img src="../assets/images/Shape.png" alt="Item Arrow" /></div></li>';
    }
    document.getElementById('paymentNavs').innerHTML = allLi;

    cardNumber = document.querySelector('.cardNumber');
    conditionOnIeBrowser();
    formReset();

    var checkScreenWidth = window.matchMedia("(max-width: 680px)");
    // mediaQueryJs(checkScreenWidth);
    // windowWidthCount();
    initExpCard();

    if (obj.express_pay && document.getElementsByClassName('saveCardDetails').length) {
        document.getElementById('saveCardLi').classList.add("activeLi");
        document.getElementById('saveCard').classList.remove("hideBox");
        document.getElementById("loading2").style.display = "none";
        defaultSelectedCard();
        document.querySelectorAll(".selectedSaveDetails .savDetailsCvv")[0].disabled = false;
        return false;
    }
    if (obj.creditCard) {
        document.getElementById('creditLi').classList.add("activeLi");
        document.getElementById('debitWithPin').classList.remove("hideBox");
        addConvenienceFee('creditCard');
        document.getElementById('orderfootDetails1').innerHTML = ccCopy;
        document.getElementById('orderfootDetails2').innerHTML = ccCopy;
        document.getElementById("loading2").style.display = "none";
        return false;
    }
    if (obj.debitCard) {
        document.getElementById('debitLi').classList.add("activeLi");
        document.getElementById('debitWithPin').classList.remove("hideBox");
        addConvenienceFee('debitCard');
        document.getElementById('orderfootDetails1').innerHTML = dcCopy;
        document.getElementById('orderfootDetails2').innerHTML = dcCopy;
        document.getElementById("loading2").style.display = "none";
        return false;
    }
    if (obj.prepaidCard) {
        document.getElementById('prepaidLi').classList.add("activeLi");
        document.getElementById('debitWithPin').classList.remove("hideBox");
        addConvenienceFee('prepaidCard');
        document.getElementById('orderfootDetails1').innerHTML = pcCopy;
        document.getElementById('orderfootDetails2').innerHTML = pcCopy;
        document.getElementById("loading2").style.display = "none";
        return false;
    }
    if (obj.upi) {
        document.getElementById('upiLi').classList.add("activeLi");
        document.getElementById('upi').classList.remove("hideBox");
        addConvenienceFee('upi');
        document.getElementById('orderfootDetails1').innerHTML = upiCopy;
        document.getElementById('orderfootDetails2').innerHTML = upiCopy;
        document.getElementById("loading2").style.display = "none";
        return false;
    }
    if (obj.autoDebit) {
        document.getElementById('autoDebitLi').classList.add("activeLi");
        document.getElementById('autoDebit').classList.remove("hideBox");
        addConvenienceFee('autoDebit');
        document.getElementById('orderfootDetails1').innerHTML = autoDebitCopy;
        document.getElementById('orderfootDetails2').innerHTML = autoDebitCopy;
        document.getElementById("loading2").style.display = "none";
        return false;
    }
    if (obj.iMudra) {
        document.getElementById('iMudraLi').classList.add("activeLi");
        document.getElementById('iMudra').classList.remove("hideBox");
        addConvenienceFee('iMudra');
        document.getElementById('orderfootDetails1').innerHTML = iMudraCopy;
        document.getElementById('orderfootDetails2').innerHTML = iMudraCopy;
        document.getElementById("loading2").style.display = "none";
        return false;
    }
    if (obj.netBanking) {
        document.getElementById('netBankingLi').classList.add("activeLi");
        document.getElementById('netBanking').classList.remove("hideBox");
        addConvenienceFee('netBanking');
        document.getElementById('orderfootDetails1').innerHTML = netBankingCopy;
        document.getElementById('orderfootDetails2').innerHTML = netBankingCopy;
        document.getElementById("loading2").style.display = "none";
        return false;
    }
    if (obj.QRCODE) {
        document.getElementById('QRCODELi').classList.add("activeLi");
        document.getElementById('QRCODE').classList.remove("hideBox");
        addConvenienceFee('QRCODE');
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
        document.getElementById('innerAmountMob').innerHTML=	innerAmount;
       // document.getElementById('mobileViewTotalAmount').innerHTML=	innerAmount;
        document.getElementById('BaseAmount').innerHTML=	innerAmount;

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
            document.getElementById('upi-sbmt').classList.add('inactive');
            var upiSurcharge= document.getElementById('upsurchargeAmount').value;
      	    document.getElementById('surchargeAmount').innerHTML=upiSurcharge;
            break;
        case "QRCODE":
            surcharge.textContent = currencyCode + ' ' + pageInfoObj.surcharge_qr;
            totalAmount.textContent = currencyCode + ' ' + (Number(pageInfoObj.surcharge_qr) + Number(innerAmount)).toFixed(2);
            document.getElementById('upi-sbmt').value = 'Pay ' + currencyCode + ' ' + (Number(pageInfoObj.surcharge_qr) + Number(innerAmount)).toFixed(2);
            document.getElementById('upi-sbmt').classList.add('inactive');
            var upiSurcharge= document.getElementById('qrsurchargeAmount').value;
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
    // document.getElementById('totalAmountPayBox').innerHTML=document.getElementById('totalAmount').innerHTML;
}
var i = 0;
var walletMethodSelect=false;
var walletsidebarSelectedImg="../image/bank/selectwallet.png";
function createRadioListItems(obj, list) {
    debugger
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
            _control.classList.add("custom-control1");
            _control.id=i++;
           _item.onclick = function(event) {
               
          //  _control.classList.add("custom-radio1");
            }
              _item.onclick = function(event) {
                
                walletMethodSelect=true;
                var _img = document.createElement('img');
                _img.src = "../image/"+obj.class+"/" + item + ".png";
                document.getElementById('walletpaymentMethodErr').style.display = "none";
                walletsidebarSelectedImg=_img.src;
               // document.getElementById("setbanknameWallet").innerHTML=" ";
          //  _control.classList.add("custom-radio1");
            }
                // _control.classList.add(i+"custom-radio-active");
               // alert(this.id);
             //  if()
           //  var radios = document.getElementsByClassName('custom-control-input');
             
            //  Array.from(radios).find(radio => {if(radio.checked){
            //     this.classList.add("custom-radio1");
            //  }else{
            //      radio.classList.remove("custom-radio1");
            //  }
            // });
             //var selected = Array.from(radios).find(radio => radio.checked);
           
           
 
           
               
           //  };
           
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
            _img.classList.add('wallet-logo1');
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
                    // document.getElementById('setbankname').text = _filtered.name;
                    let bankname = document.getElementById('bankDropdownMenuButton').innerHTML;
                    if(bankname !=''){
                      document.getElementById("setbankname").innerHTML = bankname;
                    }
                    else{
                      document.getElementById("setbankname").innerHTML = 'Bank Name'
                    }
                    document.getElementById("netbankingList").value = evt.target.defaultValue;
                }
                if(obj.class=="wallet"){
                    document.getElementById("setbanknameWalletImg").src = walletsidebarSelectedImg;

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
    document.getElementById('paymentDate').value=document.getElementById('expirymonth').value+"/"+document.getElementById('expiryyear').value;
    var today = new Date(),
        someday = new Date(),
        paymentDate = document.getElementById('paymentDate'),
        paymentDateVal = paymentDate.value;

    document.getElementById("emptyExpiry").style.display = 'none';
    document.getElementById("validExpDate").style.display = 'none';
    paymentDate.classList.remove("redLine");

    if (paymentDateVal || paymentDateVal!="/") {
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

    if (!paymentDateVal ||  paymentDateVal=="/") {
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
    
        document.getElementById('paymentDate').value=document.getElementById('expirymonth').value+"/"+document.getElementById('expiryyear').value;

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
    if(document.getElementById("cardsaveflag1")){
                document.getElementById("cardsaveflag1").checked = false;
    }

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
    if(document.getElementById("cardsaveflag1")){
                document.getElementById("cardsaveflag1").checked = false;
    }
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
    if (getName.length > 1) {
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
    if (cardName.value.length < 2) {
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
    document.getElementById('setExpiryMonth').value = document.getElementById('expirymonth').value;
    document.getElementById('setExpiryYear').value = '20' + document.getElementById('expiryyear').value;
    document.getElementById('cardNumber').value = document.querySelector('.cardNumber').value;

    if (checkFirstLetter() && checkLuhn(cardNumberElement) && cvvNumber.value.length == cvvMaxLength && cardName.value.length > 1 && CheckExpiry()) {
        document.getElementById('loading2').style.display = "block";
        document.getElementById('confirm-purchase').classList.add("pointerEventNone");
        document.getElementById('confirm-purchase').disabled = true;
        document.getElementById('creditCard').submit();
    }
}
function formReset() {
    document.getElementById('userMoptypeIcon').src = "../image/bc.png"
    document.querySelector('.cardNumber').value = "";
    document.getElementById("paymentDate").value = "";
    document.getElementById("cvvNumber").value = "";
    document.getElementById("cardName").value = "";
    document.getElementById('checkStartNo').style.display = 'none';
    document.getElementById('validCardCheck').style.display = 'none';
    document.getElementById('notSupportedCard').style.display = 'none';
    document.getElementById('validExpDate').style.display = 'none';
    document.getElementById('cvvValidate').style.display = 'none';
    document.getElementById('nameError').style.display = 'none';
    document.getElementById('emptyCardNumber').style.display = 'none';
    document.getElementById('emptyExpiry').style.display = 'none';
    document.getElementById('emptyCvv').style.display = 'none';
    document.getElementById('confirm-purchase').classList.remove("active");

    document.querySelector('.cardNumber').classList.remove("redLine");
    document.getElementById("paymentDate").classList.remove("redLine");
    document.getElementById("cvvNumber").classList.remove("redLine");
    document.getElementById("cardName").classList.remove("redLine");
    document.getElementById("cvvNumber").maxLength = 4;
    var ccMobIcon = document.getElementsByClassName('ccMobIcon');
    var dcMobIcon = document.getElementsByClassName('dcMobIcon');

    for (element = 0; element < ccMobIcon.length; element++) {
        ccMobIcon[element].classList.remove("opacityMob");
    }
    for (element = 0; element < dcMobIcon.length; element++) {
        dcMobIcon[element].classList.remove("opacityMob");
    }
    document.getElementById("vpaCheck").value = "";
    document.getElementById('red1').style.display = 'none';
    document.getElementById('enterVpa').style.display = 'none';
    document.getElementById('upi-sbmt').classList.remove("payActive");
    document.getElementById("vpaCheck").classList.remove("redLine");

    document.getElementById('googlePayNum').value = "";
    document.getElementById('googlePayInvalidNo').style.display = "none";
    document.getElementById('googlePayEnterPhone').style.display = "none";
    document.getElementById('googlePayNum').classList.remove('redLine');
    document.getElementById('googlePayBtn').classList.remove("payActive");

    document.getElementById('radioError').style.display = 'none';
    document.getElementById('cvvErrorSav').style.display = 'none';
    document.getElementById('invalidCvvErrorSav').style.display = 'none';
    var radioList = document.querySelectorAll('.custom-control-input');
    radioList.forEach(function (x) {
        if (x.type == 'radio') {
            x.checked = false;
        }
    });
    
    var visaRadio = document.querySelectorAll('.visaRadio input'),
        savDetailsCvv = document.getElementsByClassName('savDetailsCvv'),
        saveCardDetails = document.getElementsByClassName('saveCardDetails');
        document.getElementById('exSubmit').classList.remove("active");
        document.getElementById('exSubmit').classList.add("inactive");
        if(document.getElementById("cardsaveflag1")){
            document.getElementById("cardsaveflag1").checked = false;
            }
    for (var i = 0; i < visaRadio.length; i++) {
        savDetailsCvv[i].value = '';
        savDetailsCvv[i].disabled = true;
        visaRadio[i].checked = false;
        saveCardDetails[i].classList.remove("selectedSaveDetails");
    }
    if (document.getElementsByClassName('saveCardDetails').length) {
        saveCardDetails[0].classList.add("selectedSaveDetails");
        savDetailsCvv[0].disabled = false;
        visaRadio[0].checked = true;
    }
    document.getElementById("netbankingList").value = netBankLabel;
    document.getElementById('bankDropdownMenuButton').innerHTML = netBankLabel;
    document.getElementById("netBankingBtn").classList.remove("active");
    document.getElementById("bankErr").style.display = "none";
    document.getElementById("netbankingList").classList.remove("redLine");
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
function submitQRForm() {
     
        //document.getElementById("loading").style.display = "block"
        var upiNameProvided = "";
        var upiNumberProvided = "";
        var paymentType = "QR";
        var mopType = "QR";
        var amount = document.getElementById('totalAmount').innerHTML;
        var currencyCode = merchantCurrencyCode;
        var token = document.getElementsByName("customToken")[0].value;
        QRSubmit(upiNameProvided, upiNumberProvided, paymentType, mopType, amount, currencyCode)
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
    var vpaRegex = /[A-Za-z0-9][A-Za-z0-9.-]*@[A-Za-z]{2,}$/;
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
  var mobile= document.getElementById('vpaCheck1').value;
  if(!walletMethodSelect){
    document.getElementById('walletpaymentMethodErr').style.display = "block";
  }
  else if(mobile.length<10){
    document.getElementById('mobileErr').style.display = "block";
  }
  else{
    
    document.getElementById('loading').style.display = "block";
    var innerAmountValue = document.getElementById('innerAmount').innerHTML.replace("INR", "").trim();
    document.getElementById('amountImudra').value = innerAmountValue;
    document.getElementById('iMudraBtn').classList.remove('active');
    document.getElementById('iMudraBtn').classList.add('inactive');
    document.getElementById('mobileErr').style.display = "none";
    document.getElementById('walletpaymentMethodErr').style.display = "none";

    document.getElementById('iMudraBtn').disabled = true;
    document.getElementById('imudraFormId').submit();
  }
}
function toolTipCvv(actionWithCvv) {
    document.getElementById('whatIsCvv').style.display = actionWithCvv;
}
function mopTypeIconShow(getMopType) {
    var getMopTypeLowerCase = getMopType.toLowerCase();
    document.getElementById('userMoptypeIcon').src = "../image/" + getMopTypeLowerCase + ".png";
    document.getElementById('userMoptypeIconSidebar').src = "../image/" + getMopTypeLowerCase + ".png";

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


function formatAndSetCcNumber(){
    debugger
    const inputVal = document.getElementById("cardNumber").value.replace(/ /g, ""); 
    let inputNumbersOnly = inputVal.replace(/\D/g, ""); 
  
    if (inputNumbersOnly.length > 19) {
      inputNumbersOnly = inputNumbersOnly.substr(0, 19);
    }
    const splits = inputNumbersOnly.match(/.{1,4}/g);
  
    let spacedNumber = "";
    if (splits) {
      spacedNumber = splits.join(" "); // Join all the splits with an empty space
    }
  
    spacedNumber !== '' ? document.getElementById("cardNumber").value = spacedNumber : document.getElementById("cardNumber").value = '';
    if(inputNumbersOnly.length>8){
        inputNumbersOnly !== '' ? document.getElementById("cardholdercvv").innerHTML =   creditCardMask(inputNumbersOnly) : document.getElementById("cardholdercvv").innerHTML = '0000 0000 0000 0000 000';
    }
    else if(inputNumbersOnly.length==0){
        document.getElementById("cardholdercvv").innerHTML =    '0000 0000 0000 0000 000';
            }
    else{
        document.getElementById("cardholdercvv").innerHTML =   inputNumbersOnly;
   
    }
    };
  
  function formatAndSetCcNumber2(){
    const inputVal = document.getElementById("cardNumber").value.replace(/ /g, "");
    let inputNumbersOnly = inputVal.replace(/\D/g, ""); 
  
    if (inputNumbersOnly.length === 20 || inputNumbersOnly.length === 21) {
      inputNumbersOnly = inputNumbersOnly.substr(0, 19);
    }
  
    const splits = inputNumbersOnly
    let spacedNumber = "";
    if (splits) {
      spacedNumber = splits.join(" "); 
    }
    spacedNumber !== '' ? document.getElementById("cardNumber").value = spacedNumber : document.getElementById("cardNumber").value = '';
    // spacedNumber !== '' ? document.getElementById("cardholdercvv").innerHTML = spacedNumber : document.getElementById("cardholdercvv").innerHTML = '0000 0000 0000 0000 000';
    if(inputNumbersOnly.length>8){
        inputNumbersOnly !== '' ? document.getElementById("cardholdercvv").innerHTML =   creditCardMask(inputNumbersOnly) : document.getElementById("cardholdercvv").innerHTML = '0000 0000 0000 0000 000';
       }
       else if(inputNumbersOnly.length==0){
   document.getElementById("cardholdercvv").innerHTML =    '0000 0000 0000 0000 000';
       }
       else{
          document.getElementById("cardholdercvv").innerHTML =   inputNumbersOnly;
      
       }
}
  
  const formatCvvNumber = () => {
    const inputVal = document.getElementById("cvvNumber").value.replace(/ /g, "");
    let inputNumbersOnly = inputVal.replace(/\D/g, ""); 
  
    if (inputNumbersOnly.length > 4) {
      inputNumbersOnly = inputNumbersOnly.substr(0, 4);
    }
    const splits = inputNumbersOnly.match(/.{1,4}/g);
    let spacedNumber = "";
    if (splits) {
      spacedNumber = splits.join("  ");
    }
    spacedNumber !== '0000' ? document.getElementById("cvvNumber").value = spacedNumber : document.getElementById("cvvNumber").value =''
  }
  
  const formatMonth = () => {
    const inputVal = document.getElementById("expirymonth").value.replace(/ /g, "");
    let inputNumbersOnly = inputVal.replace(/\D/g, "");
  
    if (inputNumbersOnly.length > 2) {
      inputNumbersOnly = inputNumbersOnly.substr(0, 2);
    }
    const splits = inputNumbersOnly.match(/.{1,4}/g);
    let spacedNumber = "";
    if (splits) {
      spacedNumber = splits.join("  ");
    }
    const d = new Date();
    let mnth = d.getMonth() + 1
    let twodigityear = d.getFullYear().toString().substr(-2)
    if(twodigityear === document.getElementById("expiryyear").value){
      if(spacedNumber.length === 2){
        if(parseInt(spacedNumber) < mnth ){
            document.getElementById("expirymonth").value = ''
            document.getElementById("expdatemonth").innerHTML = 'MM'
            return false
        }
      }else if(parseInt(spacedNumber) !== 1 && parseInt(spacedNumber) !== 0 ){
        if(parseInt(spacedNumber) < mnth ){
            document.getElementById("expirymonth").value = ''
            document.getElementById("expdatemonth").innerHTML = 'MM'
          return false
        }
      }
  
      if(spacedNumber <= 12){
        spacedNumber !== '' ? document.getElementById("expirymonth").value =spacedNumber : document.getElementById("expirymonth").value =''
        spacedNumber !== '' ? document.getElementById("expdatemonth").innerHTML =spacedNumber : document.getElementById("expdatemonth").innerHTML ='MM'
      }else{
        document.getElementById("expirymonth").value =''
        document.getElementById("expdatemonth").innerHTML = 'MM'
      }
    }else{
      if(spacedNumber <= 12){
        spacedNumber !== '' ? document.getElementById("expirymonth").value =spacedNumber : document.getElementById("expirymonth").value =''
        spacedNumber !== '' ? document.getElementById("expdatemonth").innerHTML =spacedNumber : document.getElementById("expdatemonth").innerHTML ='MM'
      }else{
        document.getElementById("expirymonth").value =''
        document.getElementById("expdatemonth").innerHTML = 'MM'
      }
    }
  }
  const formatYear = () => {
    const inputVal = document.getElementById("expiryyear").value.replace(/ /g, "");
    let inputNumbersOnly = inputVal.replace(/\D/g, "");
  
    if (inputNumbersOnly.length > 2) {
      inputNumbersOnly = inputNumbersOnly.substr(0, 2);
    }
    const splits = inputNumbersOnly.match(/.{1,4}/g);
    let spacedNumber = "";
    if (splits) {
      spacedNumber = splits.join("  ");
    }
    const d = new Date();
    if(spacedNumber.length === 2){
      let year = d.getFullYear().toString().substr(-2)
      if(parseInt(year) > spacedNumber){
        spacedNumber =''
      }
      else{
        let mnth = d.getMonth() + 1
        if(document.getElementById("expirymonth").value < mnth){
            // document.getElementById("expirymonth").value =''
            document.getElementById("expdateyear").innerHTML ='YY'
        }
      }
    }
    spacedNumber !== ''? document.getElementById("expiryyear").value= spacedNumber : document.getElementById("expiryyear").value=''
    spacedNumber !== ''? document.getElementById("expdateyear").innerHTML= spacedNumber : document.getElementById("expdateyear").innerHTML='YY'
  }
  
  const setname = ()=>{
  
    if(document.getElementById("cardName").value !== ''){
      let inputNumbersOnly = document.getElementById("cardName").value.replace(/[!\@\^\_\&\/\\#,\|+()$~%.'":*?<>{}\d]/g, '');
      let inputVal = inputNumbersOnly; 
      if (inputVal.length > 45) {
        inputVal = inputVal.substr(0, 45);
      }
      document.getElementById("cardName").value =inputVal
      document.getElementById("cardholdername").innerHTML =inputVal
    }else{
        document.getElementById("cardName").value = ''
        document.getElementById("cardholdername").innerHTML = 'Card Holder Name'
    }
  }
  
  const obscureEmail = (email) => {
    
    const [name, domain] = email.split('@');
    return `${(name[0]?name[0]:'') + (name[1]?name[1]:'')}${new Array(name.length).join('*')}@${domain?domain:""}`;
  };
  
 
  const getupid = ()=>{
    let upid = document.getElementById("vpaCheck").value
    if(upid !=''){
        
      document.getElementById("upiid").innerHTML =obscureEmail(upid);
    }
    else{
      document.getElementById("upiid").innerHTML = '**********'
    }
  }
  const getbankid = ()=>{
    let upid = document.getElementById("selectbankid").value
    if(upid !=''){
      document.getElementById("bankid").innerHTML = upid
    }
  }
  
  const phoneformat = ()=>{
    const inputVal = document.getElementById('vpaCheck1').value.replace(/ /g, ""); 
    let inputNumbersOnly = inputVal.replace(/\D/g, ""); 
    if(inputNumbersOnly<10){
    document.getElementById('mobileErr').style.display = "block";
    }
    else{
        document.getElementById('mobileErr').style.display = "none";   
    }
    document.getElementById('vpaCheck1').value = inputNumbersOnly 
    // document.getElementById('vpaCheck1').value?document.getElementById('vpaCheck1').value:"000000000000"
    let phone=document.getElementById('vpaCheck1').value;
    // if(inputVal.length>4){
        document.getElementById("walletSideBarById").innerHTML=document.getElementById('vpaCheck1').value?document.getElementById('vpaCheck1').value:"000000000000";
      document.getElementById("walletSideBarById").innerHTML=phone? obscurePhone(phone):"000000000000";
//    }
//    else{
//     document.getElementById('vpaCheck1').value = inputNumbersOnly 

//    }
  };
  
 
  const obscurePhone= (mobile) => {
    const mob = mobile;
    if(mob.length>4){
    return `${(mob[0]?mob[0]:'') + (mob[1]?mob[1]:'')}${new Array(mob.length-3).join('*')}${(mob[8]?mob[8]:'') + (mob[9]?mob[9]:'')}`;
  }
else{
    return mobile;
}
};
  const obscureCard= (card) => {
    const cardDebitCredit = card;
  };
  function creditCardMask(number, character = "*"){
    number = number.replace(/[^0-9]+/g, ''); /*ensureOnlyNumbers*/
    var l = number.length;
    return number.substring(0,6) + character.repeat(l-8) + number.substring(l-4,l);
  }
  
  function doMaskNumber(e){
    document.querySelector("#maskedNumber").innerHTML = creditCardMask(document.querySelector("#credit-card").value);
  }

  function monthautoset() {
	    let output = document.getElementById("expirymonth").value.toString();
	    if(output=="00" && output.length==2){
	        document.getElementById("expirymonth").value="";
	    }
	    if(output=="0" && output.length==1){
	        document.getElementById("expirymonth").value="";
	    }
	    if(output=="0" && output.length==1){
	        document.getElementById("expirymonth").value="";
	    }
	    if(output=="0" && output.length==1){
	        document.getElementById("expirymonth").value="";
	    }
	    if(output!="0" && output.length==1){
	        document.getElementById("expirymonth").value="0"+ document.getElementById("expirymonth").value;
	    }
	  }
  
//   const getbankname = ()=>{
//     let bankname = document.getElementById('bankDropdownMenuButton').innerHTML;
//     if(bankname !=''){
//       document.getElementById("setbankname").innerHTML = bankname
//     }
//     else{
//       document.getElementById("setbankname").innerHTML = 'Bank Name'
//     }
//   }

