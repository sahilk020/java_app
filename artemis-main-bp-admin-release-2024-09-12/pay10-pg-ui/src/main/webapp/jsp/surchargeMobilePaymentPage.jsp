var arr = [],
    paymentOptionString,
    adsImgUrl,
    adsImglinkUrl,
    merchantLogo,
    isIE = /*@cc_on!@*/false || !!document.documentMode,
    isIOS = /iPad|iPhone|iPod/.test(navigator.userAgent) && !window.MSStream,
    checkApiCallToGpayVal,
    checkDbEntryVal,
    cardNumber,
    pageInfoObj,
    merchantCurrencyCode, 
    tempCardBin = "",
    alreadyPopulated = false,
    ccCopy = "Charges Applicable : 1.8 % plus Applicable Taxes for all domestic Credit Cards.",
    dcCopy = "Charges Applicable : No charges applicable on transaction amount upto INR 100000. 0.9% plus Applicable Taxes for all transactions above INR 100000.",
    pcCopy = "Charges Applicable : 1.8 % plus Applicable Taxes for all domestic Prepaid Cards.",
    upiCopy = "Charges Applicable : No charges Applicable on transaction amount upto INR 2000. INR 10 plus Applicable Taxes for all transactions above INR 2000.",
    googlePayCopy = "Charges Applicable : No charges Applicable on transaction amount upto INR 2000. INR 10 plus Applicable Taxes for all transactions above INR 2000.",
    autoDebitCopy = "Charges Applicable : PG Charges INR 90 plus applicable taxes.",
    iMudraCopy = "Charges Applicable : INR 10 plus Applicable Taxes.",
    xhrUpi,
    xhrUpiPay,
    oldValue,
    oldCursor,
    regexCardNo = new RegExp(/^\d{0,19}$/g),
    keyCodesBack;

var amexFlag = false; {
    history.pushState(null, null, 'surchargeMobilePaymentPage.jsp');
    window.addEventListener('popstate', function(event) {
        history.pushState(null, null, 'surchargeMobilePaymentPage.jsp');
    });
}
Date.prototype.isValid = function () {
    return this.getTime() === this.getTime();
}

function mask(value) {
  var output = [];
    for(var i = 0; i < value.length; i++) {
      if(i !== 0 && i % 4 === 0) {
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
  var el = e.target,
      newCursorPosition,
      newValue = unmask(el.value);
  
  if(newValue.match(regexCardNo)) {
    newValue = mask(newValue);
    newCursorPosition = oldCursor - checkSeparator(oldCursor, 4) + checkSeparator(oldCursor + (newValue.length - oldValue.length), 4) + (unmask(newValue).length - unmask(oldValue).length);
    
    if(newValue !== "") {
      el.value = newValue;
      if(keyCodesBack == 8 && newCursorPosition == 5){
        el.setSelectionRange(4, 4);
        return false;
      }else if(keyCodesBack == 8 && newCursorPosition == 10){
        el.setSelectionRange(9, 9);
        return false;
      }else if(keyCodesBack == 8 && newCursorPosition ==15){
        el.setSelectionRange(14, 14);
        return false;
      }else if(keyCodesBack == 8 && newCursorPosition ==20){
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
  
function numOnly(event,Element) {
    var key = event.keyCode,
      spaceKey = 32,
      leftKey =37,
      rightKey = 39,
      deleteKey = 46,
      backspaceKey = 8,
      tabKey = 9,
      maxlengthCheck = Number(Element.getAttribute('maxlength'));
      
    if(event.key == "!" || event.key == "@" || event.key == "#" || event.key == "$" || event.key == "%" || event.key == "^" || event.key == "&" || event.key == "*" || event.key == "(" || event.key == ")"){
      return false;
    }
    if(maxlengthCheck){
      if(Element.value.length==maxlengthCheck){   
              if(key == backspaceKey || key == tabKey || key == leftKey || key == rightKey || key == deleteKey){
                  return true;
              }else{
                  return false;
              }
      }
    }
    return ((key >= 48 && key <= 57) || (key >= 96 && key <= 105) || key == backspaceKey || key == tabKey || key == leftKey || key == rightKey || key == deleteKey);
  }; 

function initExpCard() {
    var maskedInput = document.getElementById('paymentDate');
    if (maskedInput.addEventListener) {
      maskedInput.addEventListener('keyup', function(e) {
      handleValueChange(e);
      }, false); 
    } else if (maskedInput.attachEvent) {
        maskedInput.attachEvent("onkeyup", function(e) {
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
      case  9:
        return;
      }
    document.getElementById('paymentDate').value = handleCurrentValue(e);
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
  if (e.target.getAttribute('data-valid-example')){
    return validateProgress(e, newValue);
  }
  return newValue;
}

function validateProgress(e, value){
  var validExample = e.target.getAttribute('data-valid-example'),
      pattern = new RegExp(e.target.getAttribute('pattern')),
      placeholder = e.target.getAttribute('data-placeholder'),
      l = value.length, 
      testValue = '';


  if (l == 1 && placeholder.toUpperCase().substr(0,2) == 'MM'){
    if(value > 1 && value < 10) {
      value = '0' + value;
    }
    return value;
  }
  
  for ( i = l; i >= 0; i--){
    testValue = value + validExample.substr(value.length);
    if (pattern.test(testValue)) {
      return value;
    } else {
      value = value.substr(0, value.length-1);
    }
  }
  return value;
}

function googlePayNumCheck(getThis){
 var googlePayNumLength = (getThis.value).trim().length,
     googlePayBtn = document.getElementById('googlePayBtn');
     duplctBtn_gpay = document.getElementById('duplctBtn_gpay');

    document.getElementById('googlePayEnterPhone').style.display="none";
    document.getElementById('googlePayInvalidNo').style.display="none";
    document.getElementById('googlePayNum').classList.remove('redLine');

     if(googlePayNumLength == 10){
         googlePayBtn.classList.add("payActive");
         duplctBtn_gpay.classList.add("payActive");
     }else{
         googlePayBtn.classList.remove("payActive");
         duplctBtn_gpay.classList.remove("payActive");
     }
}
function checkPhoneNo(element){
    var phoneNoLength = (element.value).trim().length;
     if(phoneNoLength){
        if(phoneNoLength == 10){
            document.getElementById('googlePayInvalidNo').style.display="none";
            document.getElementById('googlePayNum').classList.remove('redLine');
        }else{
            document.getElementById('googlePayInvalidNo').style.display="block";
            document.getElementById('googlePayNum').classList.add('redLine');
        }
     }else{
        document.getElementById('googlePayEnterPhone').style.display="block";
        document.getElementById('googlePayNum').classList.add('redLine');
     }
}
var currentTokenIdSaveCard;
function deleteButton(key, element) {
    var deletCardNo = element.parentNode.querySelector('.saveCardNum').value;
    document.querySelector('.selectedCard').innerHTML = deletCardNo;
    currentTokenIdSaveCard = key;
    document.getElementById('deleteCnfBox').style.display="block";
}
function deleteSaveCard(saveThisCard){
    if(saveThisCard){
        var token = document.getElementsByName("customToken")[0].value,
            data = new FormData(),
            xhr = new XMLHttpRequest();
        data.append('tokenId', currentTokenIdSaveCard);
        data.append('token', token);
        xhr.open('POST', 'deletecard', true);
        xhr.onload = function() {
            if (xhr.status === 200) {
                var deleteThisSaveCard = document.getElementById('tokenId'+currentTokenIdSaveCard).parentNode.parentNode.parentNode.parentNode;
                    deleteThisSaveCard.parentNode.removeChild(deleteThisSaveCard);
                    if(!document.getElementsByClassName('saveCardDetails').length){
                        pageInfo();
                        document.getElementById('saveCard').classList.add("hideBox");
                    }else{
                        handleClick(document.getElementsByClassName('visaRadio')[0]);
                    }
            } else {
                alert('An error occurred!');
            }
        }
        xhr.send(data);
    }
    document.getElementById('deleteCnfBox').style.display="none";

}

function isCharacterKey(event) {
    var k;
    document.all ? k = event.keyCode : k = event.which;
    return ((k > 64 && k < 91) || (k > 96 && k < 123) || (k == 8));
}

function isCharacterKeyWithSpace(event) {
    var k;
    document.all ? k = event.keyCode : k = event.which;
    return ((k > 64 && k < 91) || (k > 96 && k < 123) || (k == 8) || (k == 32));
}

function handleClick(myRadio) {  
    document.getElementById('radioError').style.display="none";
    document.getElementById('cvvErrorSav').style.display="none";
    document.getElementById('invalidCvvErrorSav').style.display="none";
    var saveCardDetails = document.getElementsByClassName('saveCardDetails'),
        visaRadio = document.getElementsByClassName('visaRadio'),
        savDetailsCvv = document.getElementsByClassName('savDetailsCvv'),
        cvvPlaceholder = document.getElementsByClassName('cvvPlaceholder');
       
        for(var i=0; i
y(false);
    var selectedSaveDetailsLength = document.getElementsByClassName('selectedSaveDetails').length;
    if(!selectedSaveDetailsLength){
        document.getElementById('radioError').style.display="block";
        return false;
    }
    var selectedSaveDetailsCvvVal = document.querySelectorAll('.selectedSaveDetails .savDetailsCvv')[0].value;

    if(selectedSaveDetailsCvvVal){
        if(selectedSaveDetailsCvvVal.length<3){
            document.getElementById('invalidCvvErrorSav').style.display="block";
            return false;
        }else{
            document.getElementById('loading2').style.display="block";           
            var orderTotalAmount = document.getElementById('totalAmount').innerHTML;
            var currentTokenId = document.querySelectorAll(".selectedSaveDetails .visaRadio")[0].value;
            var saveMobImg = document.querySelectorAll(".selectedSaveDetails .saveMobImg")[0].getAttribute('alt').toUpperCase();
            var savedCardNumber = document.querySelectorAll(".selectedSaveDetails .saveCardNum")[0].value;
            document.getElementById('currentCvvInput').value = selectedSaveDetailsCvvVal;
            document.getElementById('orderTotalAmountInput').value = orderTotalAmount;
            document.getElementById('currentTokenIdInput').value = currentTokenId;
            document.getElementById('savedCardMopType').value = saveMobImg;
            document.getElementById('savedCardNumber').value = savedCardNumber;
            
            document.getElementById('exSubmit').disabled = true;
            document.getElementById('exSubmit').classList.remove("payActive");
            document.getElementById('duplctBtn_saveCard').disabled = true;
            document.getElementById('duplctBtn_saveCard').classList.remove("payActive");
            document.getElementById("exCard").submit();
        }
    }else{
        document.getElementById('cvvErrorSav').style.display = 'block';
    }
}
function hidePlaceholder(element) {
    element.parentElement.parentElement.parentElement.children[0].style.display="none";
}
function showPlaceholder(element){
    if(!element.value){
         element.parentElement.parentElement.parentElement.children[0].style.display="block";
    }
}
function saveCardCheckCvv(inputElement){
    var cvvErrorSav = document.getElementById('cvvErrorSav'),
        invalidCvvErrorSav = document.getElementById('invalidCvvErrorSav');
    if(inputElement.value){
        if(inputElement.value.length>=3){
            invalidCvvErrorSav.style.display="none";
        }else{
            invalidCvvErrorSav.style.display="block";
        }
    }else{
        cvvErrorSav.style.display="block";
    }
}
function enableExButton() {
    document.getElementById('cvvErrorSav').style.display="none"
    document.getElementById('invalidCvvErrorSav').style.display="none"
    var selectedSaveDetailsCvvVal = document.querySelectorAll('.selectedSaveDetails .savDetailsCvv')[0].value;
    if(selectedSaveDetailsCvvVal.length==3){
        document.getElementById('exSubmit').classList.add("payActive");
        document.getElementById('duplctBtn_saveCard').classList.add("payActive");
    }else{
        document.getElementById('exSubmit').classList.remove("payActive");
        document.getElementById('duplctBtn_saveCard').classList.remove("payActive");
    }
}

function showStuff(currentElement, paymentType) {
    var tabBoxAry = document.getElementsByClassName('tabBox'),
        getCurrentDataId = currentElement.getAttribute('data-id'),
        tabLi = document.getElementsByClassName('tabLi'),
        paymentBtnDuplct = document.getElementsByClassName('payment-btn-duplct'),
        orderfootDetails1 = document.getElementById('orderfootDetails1');
        
   
    for(i=0; i<tabBoxAry.length; i++){
        tabBoxAry[i].classList.add("hideBox");
    }
    for(j=0; j<tabLi.length; j++){
        tabLi[j].classList.remove("activeLi");
    }
    
    for(jj=0; jj<paymentBtnDuplct.length; jj++){
        paymentBtnDuplct[jj].style.display="none";
    }
    
    document.getElementById(getCurrentDataId).classList.remove("hideBox");
    document.getElementById('debit_cards').style.display = "none";  
    document.getElementById('credit_cards').style.display = "none";
    document.getElementById('prepaid_cards').style.display = "none";
   
   switch(paymentType) {
    case "saveCard":
        var checkPaymentTypeInSaveCard = document.querySelector('.selectedSaveDetails .text-muted').innerText.trim().replace(/\s/g,'').toLowerCase();
        document.getElementById('saveCardLi').classList.add("activeLi");
        document.getElementById('duplctBtn_saveCard').style.display="block";
        if(checkPaymentTypeInSaveCard == 'creditcard'){
            document.getElementById('orderfootDetails1').innerHTML = ccCopy;
        }else if(checkPaymentTypeInSaveCard == 'debitcard'){
            document.getElementById('orderfootDetails1').innerHTML = dcCopy;
        }else if(checkPaymentTypeInSaveCard == 'prepaidcard'){
            document.getElementById('orderfootDetails1').innerHTML = pcCopy;
        }
        break;
     case "creditCard":
        orderfootDetails1.innerHTML = ccCopy;
        document.getElementById('credit_cards').style.display = "block"; 
        document.getElementById('duplctBtn_cc_dc').style.display="block";
        document.querySelectorAll('.duplicateCreditLi')[0].classList.add('activeLi');
        creditCardTop.style.display="block";
        debitCardTop.style.display="none";
        prepaidCardTop.style.display="none";
        creditCardBottom.style.display="none";
        if(pageInfoObj.debitCard){
            debitCardBottom.style.display="block";
        }else{
            debitCardBottom.style.display="none";
        }
        if(pageInfoObj.prepaidCard){
            prepaidCardBottom.style.display="block";
        }else{
            prepaidCardBottom.style.display="none";
        }
        break;
    case "debitCard":
        orderfootDetails1.innerHTML = dcCopy;
        document.getElementById('debit_cards').style.display = "block";
        document.getElementById('duplctBtn_cc_dc').style.display="block";
        document.querySelectorAll('.duplicateDebitLi')[0].classList.add('activeLi');
        debitCardTop.style.display="block";
        prepaidCardTop.style.display="none";
        creditCardBottom.style.display="none";
        debitCardBottom.style.display="none";
        if(pageInfoObj.prepaidCard){
            prepaidCardBottom.style.display="block";
        }else{
            prepaidCardBottom.style.display="none";
        }
        if(pageInfoObj.creditCard){
            creditCardTop.style.display="block";
        }else{
            creditCardTop.style.display="none";
        }
        break;
    case "prepaidCard":
        orderfootDetails1.innerHTML = pcCopy;
        document.getElementById('prepaid_cards').style.display = "block";
        document.getElementById('duplctBtn_cc_dc').style.display="block";
        document.querySelectorAll('.duplicatePrepaidLi')[0].classList.add('activeLi');
        prepaidCardTop.style.display="block";
        creditCardBottom.style.display="none";
        debitCardBottom.style.display="none";
        prepaidCardBottom.style.display="none";
        if(pageInfoObj.debitCard){
            debitCardTop.style.display="block";
        }else{
            debitCardTop.style.display="none";
        }
        if(pageInfoObj.creditCard){
            creditCardTop.style.display="block";
        }else{
            creditCardTop.style.display="none";
        }              
         break;
    case "upi":
        orderfootDetails1.innerHTML = upiCopy;
        document.getElementById('upiLi').classList.add("activeLi");
        document.getElementById('duplctBtn_upi').style.display="block"; 
        break;
    case "googlePay":
        orderfootDetails1.innerHTML = upiCopy;
        document.getElementById('gpayLi').classList.add("activeLi");
        document.getElementById('duplctBtn_gpay').style.display="block";
        break;

    case "autoDebit":
        orderfootDetails1.innerHTML = autoDebitCopy;
        document.getElementById('autoDebitLi').classList.add("activeLi");
        document.getElementById('duplctBtn_autoDebit').style.display="block"; 
        break;
    case "iMudra":
        orderfootDetails1.innerHTML = iMudraCopy;
        document.getElementById('iMudraLi').classList.add("activeLi");
        document.getElementById('duplctBtn_iMudra').style.display="block"; 
        break;
    }
    addConvenienceFee(paymentType);
    scrollPageTop(paymentType);
    formReset();
}

function myCancelAction(param) {
    if(param){
        document.querySelector('#approvedNotification .cancelBtn').disabled = true;
    }
    if(xhrUpi){
      xhrUpi.abort();
    }
    if(xhrUpiPay){
      xhrUpiPay.abort();
    }
    top.location = "txncancel";
}

function upiSubmit(upiNameProvided,upiNumberProvided,paymentType,mopType,amount,currencyCode) {
        var token = document.getElementsByName("customToken")[0].value,
            status = "",
            pgRefNum = "",
            responseCode = "",
            responseMessage = "",
            txnType = "",
            pgRefNum = "",
            data = new FormData(),
            myMap = new Map();
        data.append('token',token);
        data.append('vpa', upiNumberProvided);
        data.append('upiCustName', upiNameProvided);
        data.append('paymentType', paymentType);
        data.append('mopType', mopType);
        data.append('amount', amount);
        data.append('currencyCode', currencyCode);
        
        var xhrUpiPay = new XMLHttpRequest();
        xhrUpiPay.open('POST', 'upiPay', true);
        xhrUpiPay.onload = function() {
                var obj = JSON.parse(this.response);
                hideShowOrderSummery(false);
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
               
                if(responseCode == "366"){
                    document.getElementById('approvedNotification').style.display = "none";
                    document.getElementById('loading').style.display = "none";
                    document.getElementById('red1').style.display="block";
                    document.getElementById('vpaCheck').classList.add("redLine");
                    document.getElementById('upi-sbmt').classList.remove("payActive");
                    document.getElementById('duplctBtn_upi').classList.remove("payActive");
                    return false;
                }
                
                else if (responseCode == "000") { 
                    if (transactionStatus == "Sent to Bank") {
                        verifyUpiResponseReceived(pgRefNum);
                    }else{
                         var form = document.getElementById("upiResponseForm");
                         form.action = myMap.RETURN_URL;
                         
                         for(key in myMap){
                            form.innerHTML += ('<input type="hidden" name="'+key+'" value="'+ myMap[key] +'">');
                         }
                         document.getElementById("upiResponseForm").submit();
                    }
                }else{
                     var form = document.getElementById("upiResponseForm");
                     form.action = myMap.RETURN_URL;

                     if(myMap.encdata){
                            form.innerHTML += ('<input type="hidden" name="encdata" value="'+myMap.encdata+'">');
                     }else{
                        for(key in myMap){
                            form.innerHTML += ('<input type="hidden" name="'+key+'" value="'+ myMap[key] +'">');
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
function verifyUpiResponseReceived(pgRefNum){
        var token = document.getElementsByName("customToken")[0].value,
            data = new FormData();

        data.append('token',token);
        data.append('pgRefNum', pgRefNum);
        
        xhrUpi = new XMLHttpRequest();
        xhrUpi.open('POST', 'verifyUpiResponse', true);
        xhrUpi.onload = function() {
            
        if (this == null){ 
            sleep(10000);
            verifyUpiResponseReceived(pgRefNum);
        }        
        var obj = JSON.parse(this.response);
                
                if(null!=obj){
                        var field = "";
                        var myMap = new Map();
                        var trans = "";
                        trans = obj.transactionStatus;
                        myMap = obj.responseFields; 
                        
                        if (trans == "Sent to Bank"){     
                            if(reqCount == 0){
                                 sleep(10000);
                                 reqCount = reqCount +1;
                            }else{
                                 sleep(10000);
                            }
                            verifyUpiResponseReceived(pgRefNum);
                        }else{
                              document.getElementById('approvedNotification').style.display = "none";  
                              document.getElementById("loading").style.display = "none";
                              var form = document.getElementById("upiResponseForm");
                              form.action = myMap.RETURN_URL;
                              
                              if(myMap.encdata){
                                    form.innerHTML += ('<input type="hidden" name="encdata" value="'+myMap.encdata+'">');
                              }else{
                                for(key in myMap){
                                    form.innerHTML += ('<input type="hidden" name="'+key+'" value="'+ myMap[key] +'">');
                                }
                              }
                              document.getElementById("upiResponseForm").submit();
                        }
                }else{
                     sleep(10000);
                     verifyUpiResponseReceived(pgRefNum);
                }
        };
        xhrUpi.send(data);
}

function isNumberKey(evt){ 
     var charCode = (evt.which) ? evt.which : event.keyCode;
     var c = evt.keyCode;
     var ctrlDown = evt.ctrlKey||evt.metaKey 
     if (charCode > 31 && (charCode < 48 || charCode > 57) && !ctrlDown ){
        return false;
    }else{
        return true;
    }
}

function pageInfo() {
    var obj = JSON.parse(paymentOptionString.replace(/&quot;/g,'"'));
    obj.ads = false;
    if(obj.ccMopTypes){
        obj.ccMopTypes = obj.ccMopTypes.split(',');
    }
    if(obj.dcMopTypes){
        obj.dcMopTypes = obj.dcMopTypes.split(',');
    }
    if(obj.pcMopTypes){
        obj.pcMopTypes = obj.pcMopTypes.split(',');
    }
    merchantCurrencyCode = obj.currencyCode;
    
    pageInfoObj = obj;   
   
    if(window.self != window.top){
        if(!obj.iframeOpt){
            var htmlElement =  document.getElementsByTagName('html')[0];
            htmlElement.parentNode.removeChild(htmlElement);
            return false;
        }
    }
  
    if(Number(obj.paymentSlab)){
            dcCopy = "Charges Applicable:\
                        No charges applicable on transaction amount upto INR 1 Lakh.\
                        0.9% plus Applicable Taxes for all transactions above INR 1 Lakh.";
    }

       
        if(obj.ads){
            document.getElementById('aUrlAds').href = obj.adsImglinkUrl;
            document.getElementById('imgUrlAds').src = obj.adsImgUrl;
            document.getElementById('adsSection').style.display = "block";
            document.getElementById('adsSection').classList.add("marginTop44");
        }else{
            document.getElementById('MidSecPg').classList.add("marginTop44");
        }
        

       
        var mainCTAInfo = document.getElementById('mainCTAInfo');
        var divSaveCard = document.getElementById('divSaveCard');
        if(obj.express_pay){
            mainCTAInfo.style.display = "block";
            divSaveCard.style.display = "block";
        }else{
            mainCTAInfo.style.display = "none";
            divSaveCard.style.display = "none";
        }

        
        var customerName = document.getElementById('customerName').innerHTML.toLowerCase();
        if(customerName == 'null'){
            document.getElementById('buyer').style.display = 'none';
            document.getElementById('customerName').style.display = 'none';
        }

      
        var collectTotalMob = '';
        var collectSecondMob = '';
        var collectThirdMob = '';
        
        for(var ccMop=0; ccMop<obj.ccMopTypes.length; ccMop++){
            var currentMobType = obj.ccMopTypes[ccMop].toLowerCase();
            collectTotalMob += '<img  src="../image/'+currentMobType+'.png" alt="'+currentMobType+'" id="'+currentMobType+'cc" class="ccMobIcon">'
           
        }
         
        for(var dcMop=0; dcMop<obj.dcMopTypes.length; dcMop++){
            var currentMobType = obj.dcMopTypes[dcMop].toLowerCase();
            collectSecondMob += '<img  src="../image/'+currentMobType+'.png" alt="'+currentMobType+'" id="'+currentMobType+'dc" class="dcMobIcon">'
           
        }

        for(var pcMop=0; pcMop<obj.pcMopTypes.length; pcMop++){
            var currentMobType = obj.pcMopTypes[pcMop].toLowerCase();
            collectThirdMob += '<img  src="../image/'+currentMobType+'.png" alt="'+currentMobType+'" id="'+currentMobType+'pc" class="pcMobIcon">'
           
        }
     
        
        var saveCardMain = document.getElementById('saveCardMain'),
            creditCardTop = document.getElementById('creditCardTop'),
            debitCardTop = document.getElementById('debitCardTop'),
            prepaidCardTop = document.getElementById('prepaidCardTop'),
            creditCardBottom = document.getElementById('creditCardBottom'),
            debitCardBottom = document.getElementById('debitCardBottom'),
            prepaidCardBottom = document.getElementById('prepaidCardBottom'),
            mainUpi = document.getElementById('mainUpi'),
            mainGooglePay = document.getElementById('mainGooglePay'),
            mainAutoDebit = document.getElementById('mainAutoDebit');

        if(obj.express_pay && document.getElementsByClassName('saveCardDetails').length){
            saveCardMain.innerHTML = '<div class="tabLi" id="saveCardLi" onclick="showStuff(this, '+"'saveCard'"+')" data-id="saveCard"><span class="circle"></span><span>Saved Cards</span></div>';
        }
        if(obj.creditCard){
            var creditCard = "creditCard";
               creditCardTop.innerHTML = '<div class="tabLi duplicateCreditLi" id="creditLi" onclick="showStuff(this, '+"'creditCard'"+')" data-id="debitWithPin"><span class="circle"></span><span>Credit Card</span></div>';
               creditCardBottom.innerHTML = '<div class="tabLi" id="creditLi" onclick="showStuff(this, '+"'creditCard'"+')" data-id="debitWithPin"><span class="circle"></span><span>Credit Card</span></div>';
               document.getElementById('credit_cards').innerHTML = collectTotalMob;
        }
        if(obj.debitCard){
            var debitCard = 'debitCard';
                debitCardTop.innerHTML = '<div class="tabLi duplicateDebitLi" id="debitLi" onclick="showStuff(this, '+"'debitCard'"+')" data-id="debitWithPin"><span class="circle"></span><span>Debit Card</span></div>';
                debitCardBottom.innerHTML = '<div class="tabLi" id="debitLi" onclick="showStuff(this, '+"'debitCard'"+')" data-id="debitWithPin"><span class="circle"></span><span>Debit Card</span></div>';
                document.getElementById('debit_cards').innerHTML = collectSecondMob;
                if(obj.creditCard){
                  document.getElementById('debit_cards').style.display = "none";
                }
        }
        if(obj.prepaidCard){
            var prepaidCard = 'prepaidCard';
                prepaidCardTop.innerHTML = '<div class="tabLi duplicatePrepaidLi" id="prepaidLi" onclick="showStuff(this, '+"'prepaidCard'"+')" data-id="debitWithPin"><span class="circle"></span><span>Prepaid Card</span></div>';
                prepaidCardBottom.innerHTML = '<div class="tabLi" id="prepaidLi" onclick="showStuff(this, '+"'prepaidCard'"+')" data-id="debitWithPin"><span class="circle"></span><span>Prepaid Card</span></div>';
                document.getElementById('prepaid_cards').innerHTML = collectThirdMob;
                if(obj.creditCard || obj.debitCard){
                  document.getElementById('prepaid_cards').style.display = "none";
                }
        }
        
        if(obj.upi){
            mainUpi.innerHTML = '<div class="tabLi" id="upiLi" onclick="showStuff(this, '+"'upi'"+')" data-id="upi"><span class="circle"></span><span>UPI</span></div>';
        }
        if(obj.upi && obj.googlePay){
             mainGooglePay.innerHTML = '<div class="tabLi" id="gpayLi" onclick="showStuff(this, '+"'googlePay'"+')" data-id="googlePay"><span class="circle"></span><span><img src="../image/gpay-mob.png"/></span></div>';
        }
        if(obj.autoDebit){
            mainAutoDebit.innerHTML = '<div class="tabLi" id="autoDebitLi" onclick="showStuff(this,'+"'autoDebit'"+')" data-id="autoDebit"><span class="circle"></span><span>auto Debit</span></div>';
        }
        if(obj.iMudra){
            mainImudra.innerHTML = '<div class="tabLi" id="iMudraLi" onclick="showStuff(this,'+"'iMudra'"+')" data-id="iMudra"><span class="circle"></span><span>IRCTC iMudra</span></div>';
        }

        cardNumber = document.querySelector('.cardNumber');
        formReset();
        initExpCard();
        

        
        if(obj.express_pay && document.getElementsByClassName('saveCardDetails').length){
            document.getElementById('saveCardLi').classList.add("activeLi");
            document.getElementById('saveCard').classList.remove("hideBox");
            document.getElementById("loading2").style.display = "none";
            defaultSelectedCard();
            document.querySelectorAll(".selectedSaveDetails .savDetailsCvv")[0].disabled = false;
            
            creditCardTop.style.display="none";
            debitCardTop.style.display="none";
            prepaidCardTop.style.display="none";
            if(pageInfoObj.creditCard){
                creditCardBottom.style.display="block";
            }else{
                creditCardBottom.style.display="none";
            }
            if(pageInfoObj.debitCard){
                debitCardBottom.style.display="block";
            }else{
                debitCardBottom.style.display="none";
            }
            if(pageInfoObj.prepaidCard){
                prepaidCardBottom.style.display="block";
            }else{
                prepaidCardBottom.style.display="none";
            }
            
            document.getElementById('duplctBtn_saveCard').style.display="block";
            return false;
        }else{
            if(document.getElementById('saveCardLi')){
                document.getElementById('saveCardLi').style.display = "none";
            }
            
            if(pageInfoObj.creditCard){
                creditCardTop.style.display="block";
            }else{
                creditCardTop.style.display="none";
            }
            if(pageInfoObj.debitCard){
                debitCardTop.style.display="block";
            }else{
                debitCardTop.style.display="none";
            }
            if(pageInfoObj.prepaidCard){
                prepaidCardTop.style.display="block";
            }else{
                prepaidCardTop.style.display="none";
            }
        }
        if(obj.creditCard){
            document.getElementById('creditLi').classList.add("activeLi");
            document.getElementById('debitWithPin').classList.remove("hideBox");
            addConvenienceFee('creditCard');
            document.getElementById('orderfootDetails1').innerHTML = ccCopy;
            document.getElementById('duplctBtn_cc_dc').style.display="block";
            debitCardTop.style.display="none";
            prepaidCardTop.style.display="none";
            creditCardBottom.style.display="none";
            if(obj.debitCard){
                debitCardBottom.style.display="block";
            }
            if(obj.prepaidCard){
                prepaidCardBottom.style.display="block";
            }
            document.getElementById("loading2").style.display = "none";
            return false;
        }
        if(obj.debitCard){
            document.getElementById('debitLi').classList.add("activeLi");
            document.getElementById('debitWithPin').classList.remove("hideBox");
            addConvenienceFee('debitCard');
            document.getElementById('orderfootDetails1').innerHTML = dcCopy;
            document.getElementById('duplctBtn_cc_dc').style.display="block";
            creditCardTop.style.display="none";
            prepaidCardTop.style.display="none";
            creditCardBottom.style.display="none";
            debitCardBottom.style.display="none";
            if(obj.prepaidCard){
                prepaidCardBottom.style.display="block";
            }
            document.getElementById("loading2").style.display = "none";
            return false;
        }
        if(obj.prepaidCard){
            document.getElementById('prepaidLi').classList.add("activeLi");
            document.getElementById('debitWithPin').classList.remove("hideBox");
            addConvenienceFee('prepaidCard');
            document.getElementById('orderfootDetails1').innerHTML = pcCopy;
            document.getElementById('duplctBtn_cc_dc').style.display="block";
            creditCardTop.style.display="none";
            debitCardTop.style.display="none";
            creditCardBottom.style.display="none";
            debitCardBottom.style.display="none";
            prepaidCardBottom.style.display="none";
            document.getElementById("loading2").style.display = "none";
            return false;
        }
        if(obj.upi){
            document.getElementById('upiLi').classList.add("activeLi");
            document.getElementById('upi').classList.remove("hideBox");
            addConvenienceFee('upi');
            document.getElementById('orderfootDetails1').innerHTML = upiCopy;
            document.getElementById("loading2").style.display = "none";
            document.getElementById('duplctBtn_upi').style.display="block";
            return false;
        }
        if(obj.upi && obj.googlePay){
            document.getElementById('gpayLi').classList.add("activeLi");
            document.getElementById('googlePay').classList.remove("hideBox");
            addConvenienceFee('googlePay');
            document.getElementById('orderfootDetails1').innerHTML = googlePayCopy;
            document.getElementById("loading2").style.display = "none";
            document.getElementById('duplctBtn_gpay').style.display="block";
            return false;
        }
        if(obj.autoDebit){
            document.getElementById('autoDebitLi').classList.add("activeLi");
            document.getElementById('autoDebit').classList.remove("hideBox");
            addConvenienceFee('autoDebit');
            document.getElementById('orderfootDetails1').innerHTML = autoDebitCopy;
            document.getElementById("loading2").style.display = "none";
            document.getElementById('duplctBtn_autoDebit').style.display="block";
            return false;
        }
        if(obj.iMudra){
            document.getElementById('iMudraLi').classList.add("activeLi");
            document.getElementById('iMudra').classList.remove("hideBox");
            addConvenienceFee('iMudra');
            document.getElementById('orderfootDetails1').innerHTML = iMudraCopy;
            document.getElementById("loading2").style.display = "none";
            document.getElementById('duplctBtn_iMudra').style.display="block";
            return false;
        }
    }
function defaultSelectedCard(){
    document.getElementsByClassName('saveCardDetails')[0].classList.add('selectedSaveDetails');
    document.querySelectorAll('.selectedSaveDetails .visaRadio ')[0].checked = true;
    document.querySelectorAll('.selectedSaveDetails .savDetailsCvv')[0].disabled = true;
    var paymentType = document.querySelectorAll(".selectedSaveDetails .text-muted")[0].innerText.trim().replace(/\s/g,'');
        if(paymentType == "CreditCard"){
            document.getElementById('orderfootDetails1').innerHTML = ccCopy;
            addConvenienceFee('creditCard');
        }else if( paymentType == "DebitCard"){
            document.getElementById('orderfootDetails1').innerHTML = dcCopy;
            addConvenienceFee('debitCard');
        }else if( paymentType == "PrepaidCard"){
            document.getElementById('orderfootDetails1').innerHTML = pcCopy;
            addConvenienceFee('prepaidCard');
        }
}   
function removeEnterCardMsg() {
    if(document.querySelector('.cardNumber').value.length>0){
        document.getElementById('emptyCardNumber').style.display="none";
    }
    checkErrorMsgShowOrNot();
}

function addConvenienceFee(paymentType){
    var surcharge = document.querySelector("#surcharge big"),
        totalAmount = document.querySelector("#totalAmount big"),
        surchargeName = document.getElementById('surchargeName'),    
        currencyCode = '₹',  
        surcharge = document.getElementById('surcharge'),
        totalAmountName = document.getElementById('totalAmountName'),
        totalAmount = document.getElementById('totalAmount'),
        amount =  document.querySelector("#amount big").textContent,
        innerAmount = document.getElementById('innerAmount').innerHTML.split(' ')[1];

    if(paymentType =="saveCard"){
        var checkPaymentTypeInSaveCard = document.querySelector('.selectedSaveDetails .text-muted').innerText.trim().replace(/\s/g,'').toLowerCase();
            if(checkPaymentTypeInSaveCard == "creditcard"){
                paymentType = 'creditCard';
            }else if( checkPaymentTypeInSaveCard == "debitcard"){
                paymentType = 'debitCard';
            }if( checkPaymentTypeInSaveCard == "prepaidcard"){
                paymentType = 'prepaidCard';
            }    
    }
        
    switch(paymentType){
    case "creditCard":
        surcharge.textContent = currencyCode + ' ' + pageInfoObj.surcharge_cc;
        totalAmount.textContent = currencyCode +  ' ' + (Number(pageInfoObj.surcharge_cc)+Number(innerAmount)).toFixed(2);
        document.getElementById('confirm-purchase').value = 'Pay '+currencyCode +  ' ' + (Number(pageInfoObj.surcharge_cc)+Number(innerAmount)).toFixed(2);
        document.getElementById('duplctBtn_cc_dc').innerHTML = 'Pay '+currencyCode +  ' ' + (Number(pageInfoObj.surcharge_cc)+Number(innerAmount)).toFixed(2);
        document.getElementById('exSubmit').value = 'Pay '+currencyCode +  ' ' + (Number(pageInfoObj.surcharge_cc)+Number(innerAmount)).toFixed(2);
        document.getElementById('duplctBtn_saveCard').innerHTML = 'Pay '+currencyCode +  ' ' + (Number(pageInfoObj.surcharge_cc)+Number(innerAmount)).toFixed(2);
        break;
    case "debitCard":
        surcharge.textContent = currencyCode + ' ' + pageInfoObj.surcharge_dc;
        totalAmount.textContent = currencyCode + ' ' + (Number(pageInfoObj.surcharge_dc)+Number(innerAmount)).toFixed(2);
        document.getElementById('confirm-purchase').value = 'Pay '+currencyCode + ' ' + (Number(pageInfoObj.surcharge_dc)+Number(innerAmount)).toFixed(2);
        document.getElementById('duplctBtn_cc_dc').innerHTML = 'Pay '+currencyCode + ' ' + (Number(pageInfoObj.surcharge_dc)+Number(innerAmount)).toFixed(2);
        document.getElementById('exSubmit').value = 'Pay '+currencyCode + ' ' + (Number(pageInfoObj.surcharge_dc)+Number(innerAmount)).toFixed(2);
        document.getElementById('duplctBtn_saveCard').innerHTML = 'Pay '+currencyCode + ' ' + (Number(pageInfoObj.surcharge_dc)+Number(innerAmount)).toFixed(2);
        break;
    case "prepaidCard":
        surcharge.textContent = currencyCode + ' ' + pageInfoObj.surcharge_pc;
        totalAmount.textContent = currencyCode + ' ' + (Number(pageInfoObj.surcharge_pc)+Number(innerAmount)).toFixed(2);
        document.getElementById('confirm-purchase').value = 'Pay '+currencyCode + ' ' + (Number(pageInfoObj.surcharge_pc)+Number(innerAmount)).toFixed(2);
        document.getElementById('duplctBtn_cc_dc').innerHTML = 'Pay '+currencyCode + ' ' + (Number(pageInfoObj.surcharge_pc)+Number(innerAmount)).toFixed(2);
        document.getElementById('exSubmit').value = 'Pay '+currencyCode + ' ' + (Number(pageInfoObj.surcharge_pc)+Number(innerAmount)).toFixed(2);
        document.getElementById('duplctBtn_saveCard').innerHTML = 'Pay '+currencyCode + ' ' + (Number(pageInfoObj.surcharge_pc)+Number(innerAmount)).toFixed(2);
        break;
    case "upi":
        surcharge.textContent = currencyCode + ' ' + pageInfoObj.surcharge_up;
        totalAmount.textContent = currencyCode + ' ' + (Number(pageInfoObj.surcharge_up)+Number(innerAmount)).toFixed(2);
        document.getElementById('upi-sbmt').value = 'Pay '+currencyCode + ' ' + (Number(pageInfoObj.surcharge_up)+Number(innerAmount)).toFixed(2);
        document.getElementById('duplctBtn_upi').innerHTML = 'Pay '+currencyCode + ' ' + (Number(pageInfoObj.surcharge_up)+Number(innerAmount)).toFixed(2);
        break;
    case "googlePay":
        surcharge.textContent = currencyCode + ' ' + pageInfoObj.surcharge_up;
        totalAmount.textContent = currencyCode + ' ' + (Number(pageInfoObj.surcharge_up)+Number(innerAmount)).toFixed(2);
        document.getElementById('googlePayBtn').value = 'Pay '+currencyCode + ' ' + (Number(pageInfoObj.surcharge_up)+Number(innerAmount)).toFixed(2);
        document.getElementById('duplctBtn_gpay').innerHTML = 'Pay '+currencyCode + ' ' + (Number(pageInfoObj.surcharge_up)+Number(innerAmount)).toFixed(2);
        break;
    case "autoDebit":
        surcharge.textContent = currencyCode + ' ' + pageInfoObj.surcharge_ad;
        totalAmount.textContent = currencyCode + ' ' + (Number(pageInfoObj.surcharge_ad)+Number(innerAmount)).toFixed(2);
        document.getElementById('autoDebitBtn').value = 'Pay '+currencyCode + ' ' + (Number(pageInfoObj.surcharge_ad)+Number(innerAmount)).toFixed(2);
        document.getElementById('duplctBtn_autoDebit').innerHTML = 'Pay '+currencyCode + ' ' + (Number(pageInfoObj.surcharge_ad)+Number(innerAmount)).toFixed(2);
        break;
    case "iMudra":
        surcharge.textContent = currencyCode + ' ' + pageInfoObj.surcharge_wl;
        totalAmount.textContent = currencyCode + ' ' + (Number(pageInfoObj.surcharge_wl)+Number(innerAmount)).toFixed(2);
        document.getElementById('iMudraBtn').value = 'Pay '+currencyCode + ' ' + (Number(pageInfoObj.surcharge_wl)+Number(innerAmount)).toFixed(2);
        document.getElementById('duplctBtn_iMudra').innerHTML = 'Pay '+currencyCode + ' ' + (Number(pageInfoObj.surcharge_wl)+Number(innerAmount)).toFixed(2);
        break;
    }
    
    surchargeName.style.display = 'block';
    surcharge.style.display = 'block';
    totalAmountName.style.display = 'block';
    totalAmount.style.display = 'block';
    totalAmount.style.fontWeight = '500';
    var getTotalAmount = document.getElementById("totalAmount").textContent;
    document.getElementById('amountPayablePhone').innerHTML = document.getElementById('totalAmount').innerHTML;
}

function numAllowPhone(e){
    e.target.value = e.target.value.replace(/[^\da-z]/g, '').replace(/[^\dA-Z]/g, '').trim();
}

function CheckExpiry() {
    var today = new Date(), 
        someday = new Date(),
        paymentDate = document.getElementById('paymentDate'),
        paymentDateVal = paymentDate.value;

    document.getElementById("emptyExpiry").style.display = 'none';
    document.getElementById("validExpDate").style.display = 'none';
    paymentDate.classList.remove("redLine");
    
    if(paymentDateVal){
        if (paymentDateVal.length<5){
        document.getElementById("validExpDate").style.display = 'block';
        paymentDate.classList.add("redLine");
        return false;
        }else if (paymentDateVal.length==5) {
            var exMonth = paymentDateVal.split('/')[0];
            var exYear = paymentDateVal.split('/')[1];
            someday.setFullYear(20+exYear, exMonth, 1);
            if (someday > today && someday.isValid() && exMonth<13 && exMonth>0 && exMonth.length == 2 && exYear.length ==2) { 
                return true;
            } else {
               document.getElementById("validExpDate").style.display = 'block';
               paymentDate.classList.add("redLine");
                return false;
            }
        }
    }else{
        document.getElementById("emptyExpiry").style.display = 'block';
        paymentDate.classList.add("redLine");
    }
}

function CheckExpiryBoolean() {
    var today = new Date(), 
        someday = new Date(),
        paymentDate = document.getElementById('paymentDate'),
        paymentDateVal = paymentDate.value;

    if(!paymentDateVal){
        return false;
    }else if (paymentDateVal.length<5){
        return false;
    }else if (paymentDateVal.length==5) {
        var exMonth = paymentDateVal.split('/')[0];
        var exYear = paymentDateVal.split('/')[1];
        someday.setFullYear(20+exYear, exMonth, 1);
        if (someday > today && someday.isValid() && exMonth<13 && exMonth>0 && exMonth.length == 2 && exYear.length ==2) {
            return true;
        } else {
            return false;
        }
    }
 }
 function removeMmDdError(){
    var today = new Date(),
        someday = new Date(),
        paymentDate = document.getElementById('paymentDate'),
        paymentDateVal = paymentDate.value;
        document.getElementById("emptyExpiry").style.display = 'none';
        document.getElementById("validExpDate").style.display = 'none';
        document.getElementById('paymentDate').classList.remove("redLine");
        if(!paymentDateVal){
            return false;
        }else if (paymentDateVal.length<5){
            return false;
        }else if (paymentDateVal.length==5){
            var exMonth = paymentDateVal.split('/')[0];
            var exYear = paymentDateVal.split('/')[1];
            var cvvValue = (document.getElementById('cvvNumber').value).trim();
            someday.setFullYear(20+exYear, exMonth, 1);
            if (someday > today && someday.isValid() && exMonth<13 && exMonth>0 && exMonth.length == 2 && exYear.length ==2) {
                if(!cvvValue || cvvValue.length<3){
                    document.getElementById('cvvNumber').focus();
                }
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
    
    if(paymentDateVal){
        if (paymentDateVal.length<5){
        document.getElementById("validExpDate").style.display = 'block';
        paymentDate.classList.add("redLine");
        }else if (paymentDateVal.length==5) {
            var exMonth = paymentDateVal.split('/')[0];
            var exYear = paymentDateVal.split('/')[1];
            someday.setFullYear(20+exYear, exMonth, 1);
            if (someday > today && someday.isValid() && exMonth<13 && exMonth>0 && exMonth.length == 2 && exYear.length ==2) { 
                return true;
            } else {
               document.getElementById("validExpDate").style.display = 'block';
               paymentDate.classList.add("redLine");
            }
        }
    }else{
        document.getElementById("emptyExpiry").style.display = 'block';
        paymentDate.classList.add("redLine");
    }
}
function checkCvv(){
    var cvvNumber = document.getElementById('cvvNumber');  
    var cvvNumberLength = cvvNumber.value.length;
    var maxLength = 3;
    if(cvvNumber.value && cvvNumberLength == maxLength){
        return true;      
    }else{
        return false;
    }
}
function checkCvvFocusOut(){
    var cvvNumber = document.getElementById('cvvNumber'),  
        cvvNumberLength = cvvNumber.value.length,
        maxLength = 3;

    document.getElementById('cvvValidate').style.display = "none";
    document.getElementById('emptyCvv').style.display = 'none';
    cvvNumber.classList.remove("redLine");

    if(cvvNumber.value){
        if(cvvNumberLength == maxLength){
            document.getElementById('cvvValidate').style.display = "none";
            document.getElementById('emptyCvv').style.display = 'none';
           cvvNumber.classList.remove("redLine"); 
        }else{
            document.getElementById('cvvValidate').style.display = "block";
            cvvNumber.classList.add("redLine"); 
        }
    }else{
        document.getElementById('emptyCvv').style.display = 'block';
        cvvNumber.classList.add("redLine");
    }
}
function removeCvvError(){
    var cvvNumberLength = document.getElementById('cvvNumber').value.length;
    var maxLength = 3;
    document.getElementById('cvvValidate').style.display = "none";
    document.getElementById('emptyCvv').style.display = 'none';
    document.getElementById('cvvNumber').classList.remove("redLine");
    
    if(cvvNumberLength == maxLength){
        document.getElementById('cardName').focus();
        return true;
    }else{
        return false;
    }
}
function checkFirstLetter(){
    var inputVal = document.querySelector('.cardNumber').value,
        firstDigit = Number(inputVal.substr(0, 1));
    if(inputVal != ''){
            if(firstDigit==3 || firstDigit==4 || firstDigit==5 || firstDigit==6){
                document.getElementById("checkStartNo").style.display = 'none';
                checkErrorMsgShowOrNot();
                return true;
            }else{
                document.getElementById("emptyCardNumber").style.display = 'none';
                document.getElementById("checkStartNo").style.display = 'block';
                document.getElementById('validCardCheck').style.display="none";
                document.getElementById('notSupportedCard').style.display="none";
                checkErrorMsgShowOrNot();
               return false;
            }
    }else{
        document.getElementById("emptyCardNumber").style.display = 'block';
        document.getElementById('validCardCheck').style.display="none";
        document.getElementById('notSupportedCard').style.display="none";
        document.getElementById("checkStartNo").style.display = 'none';
        checkErrorMsgShowOrNot();
        return false;
    }
}
function checkFirstLetterBooleanVal(){
     var inputVal = document.querySelector('.cardNumber').value,
         firstDigit = Number(inputVal.substr(0, 1));
     if(inputVal != ''){
         if(firstDigit==3 || firstDigit==4 || firstDigit==5 || firstDigit==6){
             return true;
         }else{
            return false;
         }
     }else{
         return false;
     }
}

function checkCardSupported(){
    var containCard = document.querySelector('.cardNumber').value.replace(/\s/g, "").length,
        checkStartNo = document.getElementById('checkStartNo');
    if(containCard >=9){
        if(checkMopTypeValidForUser() == false){
            if(checkStartNo.style.display == "none"){
                document.getElementById('notSupportedCard').style.display = 'block';
            }
         
         document.getElementById('validCardCheck').style.display = 'none';
         checkErrorMsgShowOrNot();
         return false;
        }else{
            document.getElementById('notSupportedCard').style.display = 'none';
            checkErrorMsgShowOrNot();
            return true;
        }
    }
}

function checkLuhn(element) {
        var cardvalue = element.value,
            ipt = cardvalue.replace(/\s/g, ''),
            checkStartNo = document.getElementById("checkStartNo");
        
        if(ipt.length == ''){
            document.getElementById('validCardCheck').style.display = 'none';
            document.getElementById('notSupportedCard').style.display="none";
            document.getElementById("checkStartNo").style.display = 'none';
            checkErrorMsgShowOrNot();
            return false;
        }
        if(ipt.length >= 9 && checkStartNo.style.display == 'none'){
            if(!checkMopTypeValidForUser()){
                document.getElementById('validCardCheck').style.display = 'none';
                document.getElementById('notSupportedCard').style.display="block";
                document.getElementById("checkStartNo").style.display = 'none';
                checkErrorMsgShowOrNot();
                return false;
            }
        }
        if(ipt.length < 13){
            document.getElementById('validCardCheck').style.display = 'block';
            document.getElementById('notSupportedCard').style.display="none";
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
        if(ipt.length >= 13 && result){
            document.getElementById('validCardCheck').style.display = 'none';           
        }
        else{
            document.getElementById('validCardCheck').style.display = 'block';
            document.getElementById('notSupportedCard').style.display="none";
            document.getElementById("checkStartNo").style.display = 'none';   
        }

        checkErrorMsgShowOrNot();
        return result;
}
function checkLuhnBooleanVal() {
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
        if(ipt.length<13){
            return false;
        }
        return booleanResultOfLuhn;
}

function checkPaymentTypeAndSelectedTab(mopType){
    var activeLiText = (document.getElementsByClassName('activeLi')[0].innerText).trim().toLowerCase();
    if(mopType == "CC" && pageInfoObj.creditCard){
        if(activeLiText == 'debit card' || activeLiText == 'prepaid card'){
            if(document.getElementById('debitLi')){
                document.getElementById('debitLi').classList.remove("activeLi");
            }
            if(document.getElementById('prepaidLi')){
                document.getElementById('prepaidLi').classList.remove("activeLi");
            }

            document.getElementById('orderfootDetails1').innerHTML = ccCopy;
            document.querySelectorAll('.duplicateCreditLi')[0].classList.add('activeLi');
            document.getElementById('debit_cards').style.display = "none";
            document.getElementById('prepaid_cards').style.display = "none";
            document.getElementById('credit_cards').style.display = "block";

            creditCardTop.style.display="block";
            debitCardTop.style.display="none";
            prepaidCardTop.style.display="none";
            creditCardBottom.style.display="none";
            if(pageInfoObj.debitCard){
                debitCardBottom.style.display="block";
            }else{
                debitCardBottom.style.display="none";
            }
            if(pageInfoObj.prepaidCard){
                prepaidCardBottom.style.display="block";
            }else{
                prepaidCardBottom.style.display="none";
            }

            clearFieldOnTabSwitch();  
        }
    }

    if(mopType == "DC" && pageInfoObj.debitCard){
        if(activeLiText == 'credit card' || activeLiText == 'prepaid card'){
            if(document.getElementById('creditLi')){
                document.getElementById('creditLi').classList.remove("activeLi");
            }
            if(document.getElementById('prepaidLi')){
                document.getElementById('prepaidLi').classList.remove("activeLi");
            }
            document.getElementById('debitLi').classList.add("activeLi");
            document.getElementById('orderfootDetails1').innerHTML = dcCopy;
            document.querySelectorAll('.duplicateDebitLi')[0].classList.add('activeLi');
            document.getElementById('credit_cards').style.display = "none";
            document.getElementById('prepaid_cards').style.display = "none";
            document.getElementById('debit_cards').style.display = "block";

            debitCardTop.style.display="block";
            prepaidCardTop.style.display="none";
            creditCardBottom.style.display="none";
            debitCardBottom.style.display="none";
            if(pageInfoObj.prepaidCard){
                prepaidCardBottom.style.display="block";
            }else{
                prepaidCardBottom.style.display="none";
            }
            if(pageInfoObj.creditCard){
                creditCardTop.style.display="block";
            }else{
                creditCardTop.style.display="none";
            }
            clearFieldOnTabSwitch();
        }
    }
    if(mopType == "PC" && pageInfoObj.prepaidCard){
        if(activeLiText == 'debit card' || activeLiText == 'credit card'){
            if(document.getElementById('creditLi')){
                document.getElementById('creditLi').classList.remove("activeLi");
            }
            if(document.getElementById('debitLi')){
                document.getElementById('debitLi').classList.remove("activeLi");
            }
           
            document.getElementById('orderfootDetails1').innerHTML = pcCopy;

            document.querySelectorAll('.duplicatePrepaidLi')[0].classList.add('activeLi');
            document.getElementById('credit_cards').style.display = "none";
            document.getElementById('debit_cards').style.display = "none";
            document.getElementById('prepaid_cards').style.display = "block";

            prepaidCardTop.style.display="block";
            creditCardBottom.style.display="none";
            debitCardBottom.style.display="none";
            prepaidCardBottom.style.display="none";
            if(pageInfoObj.debitCard){
                debitCardTop.style.display="block";
            }else{
                debitCardTop.style.display="none";
            }
            if(pageInfoObj.creditCard){
                creditCardTop.style.display="block";
            }else{
                creditCardTop.style.display="none";
            }

            clearFieldOnTabSwitch();
        }
    }
}

function clearFieldOnTabSwitch(){
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
        xhr.onload = function() { 
            var obj = JSON.parse(this.response);
            var inptLnth = document.querySelector('.cardNumber').value.length;
            
            
            document.getElementById("mopTypeCCDiv2").value = obj.mopType;
           switch(obj.paymentType) {
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

            if (obj.mopType != null &&  obj.paymentType != null && obj.paymentType == getPaymentTypeFromUi){
                document.getElementById('notSupportedCard').style.display = 'none';
                var ccMobIcon = document.getElementsByClassName('ccMobIcon');
                var dcMobIcon = document.getElementsByClassName('dcMobIcon');
                var pcMobIcon = document.getElementsByClassName('pcMobIcon');
                mopTypeIconShow(obj.mopType);
                for(mobIconElement=0; mobIconElement<ccMobIcon.length; mobIconElement++){
                    ccMobIcon[mobIconElement].classList.add("opacityMob");
                }
                for(mobIconElement=0; mobIconElement<dcMobIcon.length; mobIconElement++){
                    dcMobIcon[mobIconElement].classList.add("opacityMob");
                }
                for(mobIconElement=0; mobIconElement<pcMobIcon.length; mobIconElement++){
                    pcMobIcon[mobIconElement].classList.add("opacityMob");
                }
                if(document.getElementById(obj.mopType.toLowerCase()+'cc')!=null){
                    document.getElementById(obj.mopType.toLowerCase()+'cc').classList.remove("opacityMob");
                    document.getElementById(obj.mopType.toLowerCase()+'cc').classList.add("activeMob");
                }
                if(document.getElementById(obj.mopType.toLowerCase()+'dc')!=null){
                    document.getElementById(obj.mopType.toLowerCase()+'dc').classList.remove("opacityMob");
                    document.getElementById(obj.mopType.toLowerCase()+'dc').classList.add("activeMob");
                }
                if(document.getElementById(obj.mopType.toLowerCase()+'pc')!=null){
                    document.getElementById(obj.mopType.toLowerCase()+'pc').classList.remove("opacityMob");
                    document.getElementById(obj.mopType.toLowerCase()+'pc').classList.add("activeMob");
                }
                
                var cardNumberElement = document.querySelector('.cardNumber'); 
                if(checkFirstLetterBooleanVal() && checkLuhnBooleanVal() && CheckExpiryBoolean() && checkCvv() && nameCheckKeyUp()){
                    document.getElementById('confirm-purchase').classList.add("actvBtnCreditDebit");
                    document.getElementById('duplctBtn_cc_dc').classList.add("actvBtnCreditDebit");
                }else{
                    document.getElementById('confirm-purchase').classList.remove("actvBtnCreditDebit");
                    document.getElementById('duplctBtn_cc_dc').classList.remove("actvBtnCreditDebit");
                }
                
                returnByBean = true;
            } else {
                if(document.getElementById('checkStartNo').style.display == "block"){
                    document.getElementById('notSupportedCard').style.display = 'none';
                    document.getElementById('validCardCheck').style.display = 'none';
                }else{
                    document.getElementById('notSupportedCard').style.display = 'block';
                    document.getElementById('validCardCheck').style.display = 'none';
                    document.getElementById("checkStartNo").style.display = 'none';
                }
                mopTypeIconShow('bc');
                checkErrorMsgShowOrNot();
                var ccMobIcon = document.getElementsByClassName('ccMobIcon');
                var dcMobIcon = document.getElementsByClassName('dcMobIcon');
                var pcMobIcon = document.getElementsByClassName('pcMobIcon');
                for(mobIconElement=0; mobIconElement<ccMobIcon.length; mobIconElement++){
                    ccMobIcon[mobIconElement].classList.add("opacityMob");
                    ccMobIcon[mobIconElement].classList.remove("activeMob");
                }
                for(mobIconElement=0; mobIconElement<dcMobIcon.length; mobIconElement++){
                    dcMobIcon[mobIconElement].classList.add("opacityMob");
                    dcMobIcon[mobIconElement].classList.remove("activeMob");
                }
                for(mobIconElement=0; mobIconElement<pcMobIcon.length; mobIconElement++){
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
function nameCheck(){
    var cardName = document.getElementById('cardName'),
        getName = (cardName.value).trim(),
        nameError = document.getElementById('nameError');   
    if(getName){
        cardName.classList.remove("redLine");
        nameError.style.display = 'none';
    }else{
        cardName.classList.add("redLine");
        nameError.style.display = 'block';
    }
}
function nameCheckKeyUp(){
    var getName = (document.getElementById('cardName').value).trim();
    var cardName = document.getElementById('cardName');
    if(getName.length > 0){
        document.getElementById('nameError').style.display = 'none';
        cardName.classList.remove("redLine");
        return true;
    }else{
        return false;
    }
}

function decideBinCheck(newBin , event){    
    if (tempCardBin == newBin && newBin.length > 8 ){
        
    }else{
        binCheck(event);
        tempCardBin = newBin;
    } 
}

function enterCardNum(event){
    var inputLength = document.querySelector('.cardNumber').value.replace(/\s/g, '').length;
    if (inputLength < 9){
        document.getElementById('emptyCardNumber').style.display="none";
        document.getElementById('notSupportedCard').style.display="none";
        document.getElementById('checkStartNo').style.display="none";
        checkErrorMsgShowOrNot();
        mopTypeIconShow("bc");
        var ccMobIcon = document.getElementsByClassName('ccMobIcon');
        var dcMobIcon = document.getElementsByClassName('dcMobIcon');
        for(mobIconElement=0; mobIconElement<ccMobIcon.length; mobIconElement++){
                    ccMobIcon[mobIconElement].classList.remove("opacityMob");
                    ccMobIcon[mobIconElement].classList.remove("activeMob");
                }
        for(mobIconElement=0; mobIconElement<dcMobIcon.length; mobIconElement++){
                    dcMobIcon[mobIconElement].classList.remove("opacityMob");
                    dcMobIcon[mobIconElement].classList.remove("activeMob");
                }
        alreadyPopulated = false;
    }
    
    if (inputLength == 9 && !alreadyPopulated){ 
        alreadyPopulated = true;
        tempCardBin = document.querySelector('.cardNumber').value.replace(/\s/g,'');
        binCheck(event);
    }
    
    if (alreadyPopulated){
         decideBinCheck(document.querySelector('.cardNumber').value.replace(/\s/g, "").substring(0, 9), event);
    }
   
    var currentInputValue = document.querySelector('.cardNumber').value,
        cardName = document.getElementById('cardName').value,
        cardNumberElement = document.getElementsByClassName('pField masked')[0];
    
    if(checkFirstLetter()  && CheckExpiryBoolean() && checkCvv() && nameCheckKeyUp() && checkMopTypeValidForUser() && checkLuhn(cardNumberElement)){  
        document.getElementById('confirm-purchase').classList.add("actvBtnCreditDebit");    
        document.getElementById('duplctBtn_cc_dc').classList.add("actvBtnCreditDebit");    
    }else{
        document.getElementById('confirm-purchase').classList.remove("actvBtnCreditDebit");
        document.getElementById('duplctBtn_cc_dc').classList.remove("actvBtnCreditDebit");
    }
}

function enterCardNumRmvErrMsg(){
        if(checkFirstLetter()){
            document.getElementById('validCardCheck').style.display="none";
        }
        checkErrorMsgShowOrNot();       
}

function checkFields(e, element){
    var cvvNumber = document.getElementById('cvvNumber'),
        cardNumber = document.querySelector('.cardNumber'),
        cardName = document.getElementById('cardName'),
        paymentDate = document.getElementById('paymentDate'),
        cardNumberElement = document.getElementsByClassName('userCardNumber')[0];

    hideShowOrderSummery(false);
    if(!checkFirstLetter()){
        document.getElementById('emptyCardNumber').style.display="block"; 
        document.getElementById('validCardCheck').style.display="none";
        document.getElementById('notSupportedCard').style.display="none";
        document.getElementById('checkStartNo').style.display="none";
        checkErrorMsgShowOrNot();
    }
    if(!checkLuhn(cardNumberElement)){
        document.getElementById('validCardCheck').style.display="block";
        document.getElementById('notSupportedCard').style.display="none";
        document.getElementById('checkStartNo').style.display="none";
        checkErrorMsgShowOrNot();
    }
    if(!checkMopTypeValidForUser()){
        document.getElementById('validCardCheck').style.display="none";
        document.getElementById('notSupportedCard').style.display="block";
        document.getElementById('checkStartNo').style.display="none";
        checkErrorMsgShowOrNot();
    }
    if(cvvNumber.value.length != 3){
        document.getElementById('emptyCvv').style.display = 'none';
        document.getElementById('cvvValidate').style.display = "block";
        cvvNumber.classList.add("redLine");
    }
    if(cvvNumber.value.length == ''){
        document.getElementById('cvvValidate').style.display = "none";
        document.getElementById('emptyCvv').style.display = 'block';
        cvvNumber.classList.add("redLine");
    }
    if(!(cardName.value).trim()){
        document.getElementById('nameError').style.display = 'block';
        cardName.classList.add("redLine");
    }
    if(!CheckExpiry()){
        var paymentDate = document.getElementById('paymentDate'),
            emptyExpiry = document.getElementById('emptyExpiry'),
            validExpDate = document.getElementById('validExpDate');
        if(paymentDate.value){
            emptyExpiry.style.display = 'none';
            validExpDate.style.display = 'block';
        }else{
            emptyExpiry.style.display = 'block';
            validExpDate.style.display = 'none';
        }
        paymentDate.classList.add("redLine");
    }
     
    document.getElementById('setExpiryMonth').value = document.getElementById('paymentDate').value.split('/')[0];
    document.getElementById('setExpiryYear').value = '20'+document.getElementById('paymentDate').value.split('/')[1];
    document.getElementById('cardNumber').value = document.querySelector('.cardNumber').value;

    if(checkFirstLetter() && checkLuhn(cardNumberElement) && cvvNumber.value.length == 3 && (cardName.value).trim() && CheckExpiry()){
        document.getElementById('loading2').style.display="block";
        document.getElementById('confirm-purchase').classList.add("pointerEventNone");
        document.getElementById('confirm-purchase').disabled = true;
        document.getElementById('duplctBtn_cc_dc').classList.add("pointerEventNone");
        document.getElementById('duplctBtn_cc_dc').disabled = true;
        document.getElementById('creditCard').submit(); 
    }
}
function submitCreditCardForm(event){
    checkFields(event, document.getElementById('creditCard'));
}

function formReset(){
    
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
    document.getElementById('confirm-purchase').classList.remove("actvBtnCreditDebit");
    document.getElementById('duplctBtn_cc_dc').classList.remove("actvBtnCreditDebit");
    document.querySelector('.cardNumber').classList.remove("redLine"); 
    document.getElementById("paymentDate").classList.remove("redLine");
    document.getElementById("cvvNumber").classList.remove("redLine");
    document.getElementById("cardName").classList.remove("redLine");
    
    var ccMobIcon = document.getElementsByClassName('ccMobIcon');
    var dcMobIcon = document.getElementsByClassName('dcMobIcon');
    
    for(element=0; element<ccMobIcon.length; element++){
            ccMobIcon[element].classList.remove("opacityMob");
    }
    for(element=0; element<dcMobIcon.length; element++){
                dcMobIcon[element].classList.remove("opacityMob");
        }   
        
  
    document.getElementById("vpaCheck").value = "";
    document.getElementById('red1').style.display = 'none';
    document.getElementById('enterVpa').style.display = 'none';
    document.getElementById('upi-sbmt').classList.remove("payActive");
    document.getElementById('duplctBtn_upi').classList.remove("payActive");
    document.getElementById("vpaCheck").classList.remove("redLine");

    document.getElementById('googlePayNum').value = "";
    document.getElementById('googlePayInvalidNo').style.display = "none";
    document.getElementById('googlePayEnterPhone').style.display="none";
    document.getElementById('googlePayNum').classList.remove('redLine');
    document.getElementById('googlePayBtn').classList.remove("payActive");
    document.getElementById('duplctBtn_gpay').classList.remove("payActive");
    
    document.getElementById('cvvErrorSav').style.display = 'none';
    document.getElementById('radioError').style.display = 'none';
    document.getElementById('invalidCvvErrorSav').style.display = 'none';
    var visaRadio = document.getElementsByClassName('visaRadio'),
        savDetailsCvv = document.getElementsByClassName('savDetailsCvv'),
        saveCardDetails = document.getElementsByClassName('saveCardDetails'),
        cvvPlaceholder = document.getElementsByClassName('cvvPlaceholder');
    document.getElementById('exSubmit').classList.remove("payActive");
    document.getElementById('duplctBtn_saveCard').classList.remove("payActive");
    document.getElementById("cardsaveflag1").checked = false;
    for(var i=0; i<visaRadio.length; i++){
        savDetailsCvv[i].value = '';
        savDetailsCvv[i].disabled = true;
        visaRadio[i].checked = false;
        saveCardDetails[i].classList.remove("selectedSaveDetails");
        cvvPlaceholder[i].style.display="block";
    }
    if(document.getElementsByClassName('saveCardDetails').length){
        saveCardDetails[0].classList.add("selectedSaveDetails");
        savDetailsCvv[0].disabled = false;
        visaRadio[0].checked = true;
    }
}

function submitUpiForm(){
    hideShowOrderSummery(false);
    if(document.getElementById('vpaCheck').classList.contains('redLine')){
        return false;
    }else if(isValidVpaOnFocusOut()){
        document.getElementById("loading").style.display = "block"
        var upiNameProvided = 'dummy';
        var upiNumberProvided = document.getElementById("vpaCheck").value;
        var paymentType = "UP";
        var mopType = "UP";
        var amount = document.getElementById('totalAmount').innerHTML;
        var currencyCode = merchantCurrencyCode;
        var token = document.getElementsByName("customToken")[0].value;
        document.getElementById('upi-sbmt').classList.remove("payActive");
        document.getElementById('duplctBtn_upi').classList.remove("payActive");
        upiSubmit(upiNameProvided,upiNumberProvided,paymentType,mopType,amount,currencyCode)
        
    }else{
        isValidVpaOnFocusOut();
    }  
}

function restrictKeyVpa(event,Element) {
    var key = event.keyCode,
      spaceKey = 32,
      leftKey =37,
      rightKey = 39,
      deleteKey = 46,
      backspaceKey = 8,
      tabKey = 9,
      point = 190,
      subtract=189,
      subtractMoz=173;
    if(event.key == "!" || event.key == "#" || event.key == "$" || event.key == "%" || event.key == "^" || event.key == "&" || event.key == "*" || event.key == "(" || event.key == ")" || event.key == ">" || event.key == "_" ){
      return false;
    }
    return ((key >= 48 && key <= 57) || (key >= 33 && key <= 39) || (key >= 65 && key <= 90) || (key >= 96 && key <= 105) || key == backspaceKey || key == tabKey || key == leftKey || key == rightKey || key == deleteKey || key == point || key == subtract || key == subtractMoz || key == 12 || key == 40 || key == 45 || key == 109 || key == 110);
}

function isValidVpa(){
    var vpaRegex = /[A-Za-z0-9][A-Za-z0-9.-]*@[A-Za-z]{2,}$/;
    var vpaElement = document.getElementById("vpaCheck");
    var vpaValue = vpaElement.value.trim();
    var vpaCheck = document.getElementById('vpaCheck');
    vpaCheck.classList.remove("redLine");
    document.getElementById('red1').style.display = 'none';
    document.getElementById('enterVpa').style.display = 'none';
    if (!vpaValue.match(vpaRegex)){
        return false;
    } else {
        return true;
    }
}
function isValidVpaBoolean(){
    var vpaRegex = /[A-Za-z0-9][A-Za-z0-9.-]*@[A-Za-z]{2,}$/;
    var vpaElement = document.getElementById("vpaCheck");
    var vpaValue = vpaElement.value.trim();
    if (!vpaValue.match(vpaRegex)){
        return false;
    } else {
        return true;
    }
}
function isValidVpaOnFocusOut(){
    var vpaRegex = /[A-Za-z0-9][A-Za-z0-9.-]*@[A-Za-z]{2,}$/;
    var vpaElement = document.getElementById("vpaCheck");
    var vpaValue = (vpaElement.value).trim();
    document.getElementById('red1').style.display = "none";
    document.getElementById('enterVpa').style.display = "none";
    vpaElement.classList.remove("redLine");
    if(!vpaValue){
        document.getElementById('enterVpa').style.display = "block";
        vpaElement.classList.add("redLine");
        return false;
    }
    if (!vpaValue.match(vpaRegex)) {
        vpaElement.classList.add("redLine");
        document.getElementById('red1').style.display = "block";
        return false;
    }
    return true;
}

function enableButton() {
    var upiSbmtBtn = document.getElementById('upi-sbmt');
    var duplctBtnUpi = document.getElementById('duplctBtn_upi');
    var checkVpaError = document.getElementById('vpaCheck').classList.contains('redLine');
    if(isValidVpaBoolean() && !checkVpaError){
        upiSbmtBtn.classList.add("payActive");
        duplctBtnUpi.classList.add("payActive");
    }else{
        upiSbmtBtn.classList.remove("payActive");
        duplctBtnUpi.classList.remove("payActive");
    }
}
function nameAvailable(){
    var nameCheck = (document.getElementById('nameCheck').value).trim(),
        nameCheckLength = nameCheck.length;
    if(nameCheckLength>=1){
        document.getElementById('nameCheck').classList.remove("redLine");
        document.getElementById('red2').style.display = 'none'
        document.getElementById('EnterNameVpa').style.display = 'none'
    }
}
function checkMopTypeValidForUser(){
    var cardNumber = document.querySelector('.cardNumber'),
        cardNumberVal = cardNumber.value.replace(/\s/g, '');
    var flagForMobCheck = false;
    var activeDivId = document.getElementsByClassName('activeLi')[0].id;
    if(cardNumberVal.length < 9){
        return false;
    }
    if(activeDivId==='creditLi'){
         var ccMobIcon = document.getElementsByClassName('ccMobIcon');
         for(elemnt=0;  elemnt<ccMobIcon.length; elemnt++){
            for(var checkMop=0; checkMop<ccMobIcon[elemnt].classList.length; checkMop++){
                if(ccMobIcon[elemnt].classList[checkMop] == 'activeMob'){
                    flagForMobCheck = true;
                    break;
                }
            }
        }
    }else if(activeDivId==='debitLi'){
        var dcMobIcon = document.getElementsByClassName('dcMobIcon');
        for(elemnt=0;  elemnt<dcMobIcon.length; elemnt++){
            for(var checkMop1=0; checkMop1<dcMobIcon[elemnt].classList.length; checkMop1++){
                if(dcMobIcon[elemnt].classList[checkMop1] == 'activeMob'){
                    flagForMobCheck = true;
                    break;
                }
            }
        }
    }else if(activeDivId==='prepaidLi'){
        var pcMobIcon = document.getElementsByClassName('pcMobIcon');
        for(elemnt=0;  elemnt<pcMobIcon.length; elemnt++){
            for(var checkMop2=0; checkMop2<pcMobIcon[elemnt].classList.length; checkMop2++){
                if(pcMobIcon[elemnt].classList[checkMop2] == 'activeMob'){
                    flagForMobCheck = true;
                    break;
                }
            }
        }
    }
    return flagForMobCheck;
}
function autoDebitFn(){
    var innerAmountValue = document.getElementById('innerAmount').innerHTML.replace("INR", "").trim();
    document.getElementById('amountAD').value = innerAmountValue;
    document.getElementById('autoDebitBtn').classList.add('deactvAutoDebit');
    document.getElementById('payAutoDebit').submit();
}
function iMudraSubmit(){
    document.getElementById('loading').style.display="block";
    var innerAmountValue = document.getElementById('innerAmount').innerHTML.replace("INR", "").trim();
    document.getElementById('amountImudra').value = innerAmountValue;
    document.getElementById('iMudraBtn').classList.add('deactvImudra');
    document.getElementById('iMudraBtn').disabled = true;
    document.getElementById('duplctBtn_iMudra').classList.add('deactvImudra');
    document.getElementById('duplctBtn_iMudra').disabled = true;
    hideShowOrderSummery(false);
    document.getElementById('payImudra').submit();
}
function toolTipCvv(actionWithCvv){
    document.getElementById('whatIsCvvMainDiv').style.display=actionWithCvv;
}
function mopTypeIconShow(getMopType){
    var getMopTypeLowerCase = getMopType.toLowerCase();
    document.getElementById('userMoptypeIcon').src = "../image/"+getMopTypeLowerCase+".png";
}

function submitGooglePayForm(){
        var googlePayNum = (document.getElementById('googlePayNum').value).trim();
        var token = document.getElementsByName("customToken")[0].value;
        document.getElementById('googlePayPhoneNo').value = googlePayNum;
        hideShowOrderSummery(false);
        if(googlePayNum){
            if(googlePayNum.length == 10){
                document.getElementById('googlePayInvalidNo').style.display="none";
                document.getElementById('googlePayNum').classList.remove('redLine');
            }else{
                document.getElementById('googlePayInvalidNo').style.display="block";
                document.getElementById('googlePayNum').classList.add('redLine');
                return false;
            }
         }else{
            document.getElementById('googlePayEnterPhone').style.display="block";
            document.getElementById('googlePayNum').classList.add('redLine');
            return false;
         }
        document.getElementById('loading').style.display = "block";
        var data = new FormData();
        data.append('vpaPhone', "+91"+googlePayNum);
        data.append('mopType', 'GP');
        data.append('paymentType', 'UP');
        data.append('token', token);
        var xhttp = new XMLHttpRequest();
        xhttp.open("POST", "upiPay", true);
        xhttp.onload = function() {
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
               
                if(responseCode == "366"){
                    document.getElementById('approvedNotification').style.display = "none";
                    document.getElementById('loading').style.display = "none";
                    alert('Please enter a valid VPA');
                    document.getElementById('googlePayNum').value = '';
                    document.getElementById('googlePayBtn').classList.remove("payActive");
                    document.getElementById('duplctBtn_gpay').classList.remove("payActive");
                    return false;
                }
            else if(responseCode == "017"){
                        document.getElementById('approvedNotification').style.display = "none";
                        document.getElementById('loading').style.display = "none";
                        alert('We are unable to reach GPay servers, please try again later!');
                        document.getElementById('googlePayNum').value = '';
                        document.getElementById('googlePayBtn').classList.remove("payActive");
                        document.getElementById('duplctBtn_gpay').classList.remove("payActive");
                        return false;
                    }
                
                else if (responseCode == "000") { 
                    if (transactionStatus == "Sent to Bank") {
                        checkApiCallToGpayVal = setTimeout(checkApiCallToGpay(pgRefNum), 60000);
                        checkDbEntryVal = setTimeout(checkDbEntry(pgRefNum), 5000);            
                    }else{
                        
                         document.getElementById('approvedNotification').style.display = "none";
                         document.getElementById("loading").style.display = "none";
                         var form = document.getElementById("googlePayResponseForm");
                         form.action = myMap.RETURN_URL;
                         
                         for(key in myMap){
                            form.innerHTML += ('<input type="hidden" name="'+key+'" value="'+ myMap[key] +'">');
                         }
                         document.getElementById("googlePayResponseForm").submit();
                    }
                }else{
                     document.getElementById('approvedNotification').style.display = "none";
                     document.getElementById("loading").style.display = "none";
                     var form = document.getElementById("googlePayResponseForm");
                     form.action = myMap.RETURN_URL;
                     
                     if(myMap.encdata){
                        form.innerHTML += ('<input type="hidden" name="encdata" value="'+myMap.encdata+'">');
                     }else{
                        for(key in myMap){
                            form.innerHTML += ('<input type="hidden" name="'+key+'" value="'+ myMap[key] +'">');
                        }
                     }
                     document.getElementById("googlePayResponseForm").submit();
                }
        };
        xhttp.send(data);
}
function checkErrorMsgShowOrNot(){
    var checkStartNoDisplay = document.getElementById('checkStartNo').style.display,
        validCardCheckDisplay = document.getElementById('validCardCheck').style.display,
        emptyCardNumberDisplay = document.getElementById('emptyCardNumber').style.display,
        notSupportedCardDisplay = document.getElementById('notSupportedCard').style.display,
        cardNumber = document.querySelector('.cardNumber');

        if(checkStartNoDisplay == "block" || validCardCheckDisplay == "block" || emptyCardNumberDisplay == "block" || notSupportedCardDisplay == "block"){
            cardNumber.classList.add("redLine");
        }else{
            cardNumber.classList.remove("redLine");
        }
}
function checkDbEntry(gpRefNum){
        return function(){
            var token = document.getElementsByName("customToken")[0].value;
            var data = new FormData();
            data.append('token',token);
            data.append('pgRefNum', gpRefNum);
            var xhttp = new XMLHttpRequest();
            xhttp.open("POST", "verifyUpiResponse", true);
            xhttp.onload = function() {
                    var reponseCheckDbEntry = JSON.parse(this.response);

                    if (null != reponseCheckDbEntry) {
                   
                   var myMapGpayResponse = reponseCheckDbEntry.responseFields;
                }
                    
                    if(reponseCheckDbEntry.transactionStatus == "Sent to Bank"){
                         var checkDbEntryVal = setTimeout(checkDbEntry(pgRefNum), 5000);
                    }else{
                        if(checkDbEntryVal){
                            clearTimeout(checkDbEntryVal);
                        }
                        if(checkApiCallToGpayVal){
                            clearTimeout(checkApiCallToGpayVal);
                        }
                         document.getElementById('approvedNotification').style.display = "none";  
                          document.getElementById("loading").style.display = "none";
                          var form = document.getElementById("googlePayResponseForm");
                          form.action = myMapGpayResponse.RETURN_URL;
                          if(myMapGpayResponse.encdata){
                                form.innerHTML += ('<input type="hidden" name="encdata" value="'+myMapGpayResponse.encdata+'">');
                          }else{
                            for(key in myMapGpayResponse){
                                form.innerHTML += ('<input type="hidden" name="'+key+'" value="'+ myMapGpayResponse[key] +'">');
                            }
                          }
                        document.getElementById("googlePayResponseForm").submit();
                    }
            }
            xhttp.send(data);
        }
}

function checkApiCallToGpay(gpRefNum){
        return function(){
            var token1 = document.getElementsByName("customToken")[0].value;
            var data1 = new FormData();
            data1.append('token',token1);
            data1.append('pgRefNum', gpRefNum);
            var xhttp1 = new XMLHttpRequest();
            xhttp1.open("POST", "verifyUpiGpayResponse", true);
            xhttp1.onload = function() {
                    var reponseGpayApiCall = JSON.parse(this.response),
                        txnResponseCode = reponseGpayApiCall.responseCode; 
            
                        if (null != reponseGpayApiCall) {
                       
                       var myMapGpayResponse = reponseGpayApiCall.responseFields;
                    }
                    if(txnResponseCode == 003 || txnResponseCode == 004 || txnResponseCode == 007){
                            
                            if(checkDbEntryVal){
                                    clearTimeout(checkDbEntryVal);
                            }
                            if(checkApiCallToGpayVal){
                                clearTimeout(checkApiCallToGpayVal);
                            }
                          document.getElementById('approvedNotification').style.display = "none";  
                          document.getElementById("loading").style.display = "none";
                          var form = document.getElementById("googlePayResponseForm");
                          form.action = myMapGpayResponse.RETURN_URL;
                          if(myMapGpayResponse.encdata){
                                form.innerHTML += ('<input type="hidden" name="encdata" value="'+myMapGpayResponse.encdata+'">');
                          }else{
                            for(key in myMapGpayResponse){
                                form.innerHTML += ('<input type="hidden" name="'+key+'" value="'+ myMapGpayResponse[key] +'">');
                            }
                          }
                          
                        document.getElementById("googlePayResponseForm").submit();
                    }else if(txnResponseCode == 006 || !txnResponseCode){
            
                        checkApiCallToGpayVal = setTimeout(checkApiCallToGpay(pgRefNum), 60000);
    
                    }else if(txnResponseCode == 000){ 
                            if(checkApiCallToGpayVal){
                                clearTimeout(checkApiCallToGpayVal);
                            }
                    }      
            }
            xhttp1.send(data1);
        }
    }

function alphabetEnterPhone(element){
    if((element.value).trim()){
        element.value = element.value.replace(/[^a-zA-Z ]/g, '').replace(/ +/g, ' ');;
    }else{
        element.value = element.value.replace(/[^a-zA-Z]/g, '');
    }
}
function numberEnterPhone(element){
    if(window.matchMedia("(max-width: 680px)")){
        var elementValue = element.value;
        if(!(/^[0-9]+$/.test(elementValue))){
            element.value = elementValue.replace(/[^0-9]/g, "");
        }
    }
}
function numberEnterCvv(element){
    if(window.matchMedia("(max-width: 680px)")){
        var elementValue = element.value;
        if(!(/^[0-9]+$/.test(elementValue))){
            element.value = elementValue.replace(/[^0-9/]/g, "");
        }
    }
}
function validVpaInsert(element){
    if(window.matchMedia("(max-width: 680px)")){
        var elementValue = element.value,
            vpaRegex = /[A-Za-z0-9][A-Za-z0-9.-]*@[A-Za-z]{2,}$/;
        if(!(vpaRegex.test(elementValue))){
            element.value = elementValue.replace(/[^a-zA-Z0-9\-.@]/g, "");
        }
    }
}
function hideShowOrderSummery(param){
    var orderSum = document.getElementById('orderSum');
    if(param){
        orderSum.style.display="block";
    }else{
        orderSum.style.display="none";
    }
}
function stopBubbling(e){
    e.stopPropagation();
}
function hideCvv(){
    document.getElementById('whatIsCvvMainDiv').style.display="none";
}
function scrollTo(c,e,d){d||(d=easeOutCuaic);var a=document.documentElement;
if(0===a.scrollTop){var b=a.scrollTop;++a.scrollTop;a=b+1===a.scrollTop--?a:document.body}
b=a.scrollTop;0>=e||("object"===typeof b&&(b=b.offsetTop),
"object"===typeof c&&(c=c.offsetTop),function(a,b,c,f,d,e,h){
function g(){0>f||1<f||0>=d?a.scrollTop=c:(a.scrollTop=b-(b-c)*h(f),
f+=d*e,setTimeout(g,e))}g()}(a,b,c,0,1/e,20,d))};
function easeOutCuaic(t){t--;return t*t*t+1;}

function scrollPageTop(paymentType){
   switch(paymentType) {
    case "saveCard":
        scrollTo(document.getElementById('saveCard').offsetTop-100, 500);   
        break;
    case "creditCard":
        scrollTo(document.getElementById('debitWithPin').offsetTop-100, 500);   
        break;
    case "debitCard":
        scrollTo(document.getElementById('debitWithPin').offsetTop-100, 500);
        break;
    case "prepaidCard":
        scrollTo(document.getElementById('debitWithPin').offsetTop-100, 500);
        break;
    case "upi":
        scrollTo(document.getElementById('upi').offsetTop-100, 500); 
        break;
    case "googlePay":
        scrollTo(document.getElementById('googlePay').offsetTop-100, 500); 
        break;

    case "autoDebit":
        scrollTo(document.getElementById('autoDebit').offsetTop-100, 500); 
        break;
    }   
}
function onFocusScrollTop(){
    if(!isIOS){
        scrollTo(document.getElementById('debitWithPin').offsetTop-65, 500);
    }   
}

