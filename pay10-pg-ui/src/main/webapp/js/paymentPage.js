var arr = [];
var arrD = [];
var arrEX = [];
var arrEXinput = [];
var CheckedRadioCredit;
var CheckedRadioDebit;
var selectcorrectcardtype;
var cvvlen3;
var cvvlen4;
var cvvlenDD3;
var paybuttonflag = false;

var amexFlag = false;

{
    history.pushState(null, null, 'paymentPage.jsp');
    window.addEventListener('popstate', function(event) {
        history.pushState(null, null, 'paymentPage.jsp');
    });
}
function getCreditForm() {

	document.getElementById("cardNumber").placeholder = document
			.getElementById("cardPlaceHolderCC").value;
	cvvlen3 = document.getElementById("cvvTextImage").value;
	cvvlen4 = document.getElementById("cvvTextImageAX").value;
	var form = document.getElementById('creditCard');
	var elements = form.elements;
	var counter = 0;
	for (var i = 0; i < elements.length; i++) {
		if (elements[i].type.toLowerCase() == 'radio') {
			arr[counter] = elements[i]
			counter++;
		}
	}

}
function isNumberKey(evt) //for both cc/dc
{
    var charCode = (evt.which) ? evt.which : event.keyCode

    if (charCode > 31 && (charCode < 48 || charCode > 57))
        return false;

    return true;
}

function validateCredit() {
	var checked = false;
	for (k = 0; k < arr.length; k++) {
		if (arr[k].checked == true) {
			checked = true;
			CheckedRadioCredit = arr[k].value;
			break;
		}
	}
	if (CheckedRadioCredit == 'AX') {
		var cvvTextAX = document.getElementById('cvvtext');
		cvvTextAX.innerHTML = cvvlen4;
		cvvTextAX.className = "transparent";
		document.getElementById('cvvNumber').value = "";
		document.getElementById('cvvNumber').maxLength = 4;
	} else {
		var cvvText = document.getElementById('cvvtext');
		cvvText.innerHTML = cvvlen3;
		cvvText.className = "transparent";
		document.getElementById('cvvNumber').value = "";
		document.getElementById('cvvNumber').maxLength = 3;
	}

	/*
	 * if (checked==false) { var cardTypeMessage =
	 * document.getElementById("cardTypeMsg").value;
	 * document.getElementById("radioValidate").innerHTML = cardTypeMessage;
	 * return false; }else{ document.getElementById("radioValidate").innerHTML =
	 * ""; return true; }
	 */

}
function handlePaste(){
	return false;
}
function enablePayButton() {

	var cardNumber = document.forms["creditcard-form"]["cardNumber"].value;
	var ccExpiryMonth = document.forms["creditcard-form"]["ccExpiryMonth"].value;
	var ccExpiryYear = document.forms["creditcard-form"]["ccExpiryYear"].value;
	var cvvNumber = document.forms["creditcard-form"]["cvvNumber"].value;
	var cardName = document.forms["creditcard-form"]["cardName"].value;
	if (cardNumber.length <= 13) {
			document.getElementById('confirm-purchase').classList.remove("btn-active");
			document.getElementById('confirm-purchase').disabled = true;
			document.getElementById('confirm-purchase').className += ' payment-btn';
			// alert("cardNumber must be filled out");
			
			return false;
		}
	else if (!CheckExpiry()) {
		document.getElementById('confirm-purchase').classList.remove("btn-active");
			document.getElementById('confirm-purchase').disabled = true;
			document.getElementById('confirm-purchase').className += ' payment-btn';
		return false;
	} else if ((cardNumber == "" )|| (ccExpiryMonth == '' || ccExpiryMonth == "MM" )|| (ccExpiryYear == '' ||ccExpiryYear == "YY")
			|| (cvvNumber == "") || (cardName == "")||(paybuttonflag == true)) {
	
			document.getElementById('confirm-purchase').classList.remove("btn-active");
			document.getElementById('confirm-purchase').disabled = true;
			document.getElementById('confirm-purchase').className += ' payment-btn';
		return false;
	} else if ((cardNumber != '') && (ccExpiryMonth != '' || ccExpiryMonth != "MM" )&& (ccExpiryYear != '' ||ccExpiryYear != "YY")
			&& cvvNumber != '' && cardName != ''||(paybuttonflag == false)) {
				document.getElementById("errorBox").innerHTML="";
		document.getElementById("name-error").innerHTML = "";
			document.getElementById("mo-error").innerHTML = "";
			document.getElementById("yy-error").innerHTML = "";
			document.getElementById("cvv-error").innerHTML = "";
		
		document.getElementById('confirm-purchase').disabled = false;
		document.getElementById('confirm-purchase').className = "btn-active";
		
	}

}

function binCheck(bin,tabhide) {
	var token = document.getElementsByName("customToken")[0].value;
	var data = new FormData();
	data.append('token',token);
	data.append('bin', bin);
	var xhr = new XMLHttpRequest();
	xhr.open('POST', 'binResolver', true);
	xhr.onload = function() {
		var obj = JSON.parse(this.response);
		if(null!=obj){
			document.getElementById('paymentType2').value = obj.paymentType;
			document.getElementById('mopTypeCCDiv2').value = obj.mopType;
			document.getElementById('issuerBankName2').value = obj.issuerBankName;
			document.getElementById('issuerCountry2').value = obj.issuerCountry;
			mops =  document.getElementsByName('mopImageCC');
			banks =  document.getElementsByName('bankname');
			for(i=0;i<mops.length; i++){
				element = mops[i];
				if(element.id!=obj.mopType){
					element.className += ' transparent';
				}
			}
			for(i=0;i<banks.length; i++){
				element = banks[i];
				if(element.id!=obj.issuerBankName){
					element.className += ' active';
				}
				else{
				element.className += ' active';
				}
				if(tabhide==3){
				if(obj.issuerBankName="HDFC Bank"){
					
					document.getElementById("divCvv").style.display = "none";

				}
					
				
				}
			}
	surchargeAmount();
			//hide extra mopes
		}else{
			document.getElementById("errorBox").innerHTML = "This card is not supported, please use another card !!";
		}
	};
	xhr.send(data);
	
}

function surchargeAmount(){
var finalvalue;
var surchargeTotal;
		
			
		var paymenttype  = document.getElementById("paymentType2").value;
		
		if(paymenttype=="CC"){
			
		}else if(paymenttype== "DC"){
			
			
					}
					else if(paymenttype== ""){
		paybuttonflag = true;
						
	document.getElementById("demo").innerHTML = "Please Enter Valid Card number";
					}
		
	}



function deleteButton(key) {
	var id= "tokenId"+key;
	var element = document.getElementById(id);
	if (element.checked) {
	if(confirm("Are you sure you want to delete Save Card??")){
	var token = document.getElementsByName("customToken")[0].value;
	
	var data = new FormData();
	data.append('tokenId', key);
	data.append('token',token);
	var xhr = new XMLHttpRequest();
	xhr.open('POST', 'deletecard', true);
	xhr.onload = function() {
		
		
  if (xhr.status === 200) {
 location.reload(true); 
  } else {
    alert('An error occurred!');
  }	
	};
	xhr.send(data);

	}
	else {
		
	}
	}
}

function submitButton() {
	if (enablePayButton()) {
		document.getElementById('confirm-purchase').disabled = false;
		document.getElementById('confirm-purchase').className += 'btn-active';
	} else {
		document.getElementById('confirm-purchase').disabled = true;
		document.getElementById('confirm-purchase').className += 'payment-btn';
	}

}

function checkLuhn(input,tabhide) { // same for cc/dc
		var ipv = input.value;
		//var ipt = ipv.replace(/\s/g,'');
var cardvalue =document.getElementById("cardNumber").value;

		var ipt = cardvalue.replace(/\s/g,'');
var substr=ipt.substring(0, 6);
	if (ipt.length == 0|| ipt.length < 6) {
		document.getElementById('MC').classList.remove("transparent");
		document.getElementById('VI').classList.remove("transparent");
			banks =  document.getElementsByName('bankname');
document.getElementById("demo").innerHTML = " ";
paybuttonflag=false;
    for(i=0;i<banks.length; i++){
				element = banks[i];
				
					element.className = "";
				
			}

	}
	if (ipt.length >= 6) {
		
		binCheck(substr,tabhide);
		
	}
	if (ipt.length <= 13) {
			
		return;
	}
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
	// alert(result);
	if (result == false) {
		if (input.id == "cardNumber" && document.getElementById("demo") != null) {
			var entrValidcardnumber = document.getElementById("cardNumber").value;
			document.getElementById("demo").innerHTML = "Please Enter Valid Card number";
			return false;
		}
	} else {
		try {
			document.getElementById("demo").innerHTML = "";
		} catch (err) {
		}
		return true;
	}

}

function CheckExpiry() {
	var today, someday;
	var exMonth = document.getElementById("ccExpiryMonth").value;
	var exYear = document.getElementById("ccExpiryYear").value;
	today = new Date();
	someday = new Date();
	someday.setFullYear(exYear, exMonth, 1);
	if (document.getElementById('ccExpiryYear').selectedIndex != 0) {
		if (someday < today) {
			var entrValidexpirydate = document
					.getElementById("validExpiryDate").value;
			document.getElementById("chkExpiry").innerHTML = "Please enter Valid expiry date";
			document.getElementById('confirm-purchase').classList.remove("btn-active");
			document.getElementById('confirm-purchase').disabled = true;
			document.getElementById('confirm-purchase').className += ' payment-btn';
			return false;

		} else {
			document.getElementById("chkExpiry").innerHTML = "";
			return true;
		}

	}

}
function Show() // same for cc/dc
{
	x = event.clientX + document.body.scrollLeft; // get the mouse left
	// position
	y = event.clientY + document.body.scrollTop + 35; // get the mouse top
	// position
	cvvtext.style.display = "block"; // display the pop-up
	cvvtext.style.left = x; // set the pop-up's left
	cvvtext.style.top = y; // set the pop-up's top
}
function Hide() // same for cc/dc
{
	cvvtext.style.display = "none"; // hide the pop-up

}
function isCharacterKey(event) {
	var k;
	document.all ? k = event.keyCode : k = event.which;
	return ((k > 64 && k < 91) || (k > 96 && k < 123) || (k == 8));
}
function noSpace(event, inputName) { // same for cc/dc
	var str, str0, cardName;
	cardName = inputName.id
	str = inputName.value;
	str0 = str.charAt(0);
	if (str0 == "") {
		cardName.value = "";
		// return false;
	} else {
		if (event.charCode == 32) {
			if (cardName == "cardName") {
				document.getElementById('cardName').value = str + ' ';
			} else if (cardName == "dccardName") {
				document.getElementById('dccardName').value = str + ' ';
			}
		}
	}
}

function submitCCform() {
	document.getElementById('ccSubmit').disabled = true;
	document.getElementById('ccSubmit').className = "btn-orange disabled";
	return true;
}

function getExForm() {
    if (document.getElementById("exCard") != null) {
        var form = document.getElementById('exCard');
        var elements = form.elements;
        var counter = 0;
        var counterCvv = 0;
        var counterDelButton = 0;
        for (var i = 0; i < elements.length; i++) {
            if (elements[i].type.toLowerCase() == 'radio') {
                arrEX[counter] = elements[i]
                counter++;
            }
            if (elements[i].type.toLowerCase() == 'password') {
                arrEXinput[counterCvv] = elements[i]
                counterCvv++;
            }
            if (elements[i].type.toLowerCase() == 'button') {
                arrEXdelButton[counterDelButton] = elements[i]
                counterDelButton++;
            }
        }
    }
}

function handleClick(myRadio) {
	document.getElementById('exSubmit').classList.remove("btn-active");
	document.getElementById('exSubmit').disabled = true;
			document.getElementById('exSubmit').className += ' payment-btn float ';
    var divName = 'hideCVV' + myRadio.value;
    var cvvTextbox = 'cvvNumber' + myRadio.value;
    var cancelButton = 'deleteButton' + myRadio.value;
    var exCardNumberstr = 'exCardNumber' + myRadio.value;
    var delbutton = 'deleteButton' + myRadio.value;

    for (j = 0; j < arrEX.length; j++) {

        if (myRadio === arrEX[j]) {
            document.getElementById('cvvNumber' + myRadio.value).disabled = false;
			  document.getElementById('deleteButton' + myRadio.value).disabled = false;
          
          
        } else {
            arrEXinput[j].value = "";
            arrEXinput[j].disabled = true;
         
        }
    }
    if ((document.getElementById('exCardNumber' + myRadio.value).value).substr(0, 2) == 37 || (document.getElementById('exCardNumber' + myRadio.value).value).substr(0, 2) == 34) {
        document.getElementById('cvvNumber' + myRadio.value).maxLength = 4;
        amexFlag = true;
		document.getElementById('exSubmit').disabled = false;
		document.getElementById('exSubmit').className = "btn-active float ";
    } else {
        document.getElementById('cvvNumber' + myRadio.value).maxLength = 3;
        amexFlag = false;
    }
}

function enableExButton(v) {
	if (document.getElementById("cardSupportedEX").innerHTML == "This card is no longer supported"){
		document.getElementById('exSubmit').disabled = true;
		document.getElementById('exSubmit').className += ' payment-btn float ';
	}
	else{
    if (arrEX == "MS") {
        document.getElementById('exSubmit').disabled = false;
        document.getElementById('exSubmit').className = "btn-active float ";
    } else {

        if (amexFlag == true) {
            if (v.value.length >= 4) {
                document.getElementById('exSubmit').disabled = false;
                document.getElementById('exSubmit').className = "btn-active float ";
            }
            else{
            	document.getElementById('exSubmit').disabled = true;
            	document.getElementById('exSubmit').className += ' payment-btn float ';
            }
        } else {
            if (v.value.length >= 3) {
                document.getElementById('exSubmit').disabled = false;
                document.getElementById('exSubmit').className = "btn-active float ";
            }
            else{
				document.getElementById('exSubmit').classList.remove("btn-active");
            	document.getElementById('exSubmit').disabled = true;
            	document.getElementById('exSubmit').className += ' payment-btn float ';
            }
        }
    }
  }
}
function submitEXform() {
	
    document.getElementById('exSubmit').classList.remove("btn-active float");
	document.getElementById('exSubmit').disabled = true;
			document.getElementById('exSubmit').className += ' payment-btn float';
    document.getElementById('exCancelButton').style.display = "none";
    return true;
}
function disableEnterPress(e) {
    var key;
    if (window.event)
        key = window.event.keyCode; //IE
    else
        key = e.which; //firefox    
    return (key != 13);
}

function showStuff(id,tab) {
	var tabname = document.getElementsByClassName(" active1");
	for(var i=0;i<tabname.length;i++){
element = tabname[i];
	element.classList.remove("active1");
			
				}
	var tabex= document.getElementById("tabs-1");
	var tabnb = document.getElementById("tabs-4");
	var tabccdc=document.getElementById("tabs-2");
	var tabdcwp =	document.getElementById("tabs-3");
	var tab1 = document.getElementsByName("tabs-1").toString();
	var tab2 = document.getElementsByName("tabs-2").toString();
    var tab3 = document.getElementsByName("tabs-3").toString();
    var tab4 = document.getElementsByName("tabs-4").toString();
    var tab5 = document.getElementsByName("tabs-5").toString();
//	document.getElementById(id).style.display = "block";
	//var string = tab1.toString().replace(/div#+/i, ''); 

var sstab1 = 'tabs-1';
var sstab2 = 'tabs-2';
var sstab3 = 'tabs-3';
var sstab4 = 'tabs-4';
var sstab5 = 'tabs-5';
		if(sstab1==id){
			document.getElementById("tabs2").style.display = "none";
            document.getElementById("tabs4").style.display = "none";
            document.getElementById("tabs5").style.display = "none";
			document.getElementById("tabs1").style.display = "block";

			document.getElementById("sd").className += ' active1';
			
					
		}
		if(sstab5==id){
			document.getElementById("tabs2").style.display = "none";
            document.getElementById("tabs4").style.display = "none";
			//document.getElementById("tabs1").style.display = "none";
			document.getElementById("tabs5").style.display = "block";
			document.getElementById("upi").className += ' active1';
			
					
		}
		if(sstab2==id){

            if(tab==2){
				document.getElementById("ccdc").className += ' active1';
				
					document.getElementById('MC').classList.remove("transparent");
					document.getElementById('VI').classList.remove("transparent");

				if( document.getElementById("cardNumber").value!=""){

document.getElementById("cardNumber").value="";
placehold =document.getElementById("cardNumber").getAttribute("data-placeholder"); 

 document.getElementById("cardNumber").removeAttribute('data-placeholder');
document.getElementById("cardNumber").removeAttribute("data-original-placeholder");

placeholder="XXXX XXXX XXXX XXXX";
document.getElementById("cardNumber").setAttribute("data-placeholder",placeholder);
document.getElementById("cardNumber").setAttribute("data-original-placeholder",placeholder);
document.getElementById("cardNumber").setAttribute("data-placeholder",placeholder);



}
					
					document.getElementById("demo").innerHTML ="";
					document.getElementById("ccExpiryMonth").value="";
					document.getElementById("ccExpiryYear").value="";
					document.getElementById("divCvv").style.display = "block";
					document.getElementById("cvvNumber").value="";
					document.getElementById("cardName").value="";
					document.getElementById("divCvv").style.display = "block";
							
			
								
					document.getElementById("divSaveCard").style.display="block";
				    document.getElementById("tabimages").style.display = "none";
				
				 tabhide=2;
			}
			if(tab==3){
					document.getElementById("dcwp").className += ' active1';
				
				banks =  document.getElementsByName('bankname');
					for(i=0;i<banks.length; i++){
		/* 	element = banks[i];
			document.getElementById(element.id).classList.remove("transparent"); 
			 */
			 /****Static Code*****/
			 document.getElementById("SBI").classList.remove("transparent"); 
			document.getElementById("ICICI").classList.remove("transparent"); 
			document.getElementById("AXIS").classList.remove("transparent"); 
			
			document.getElementById("HDFC Bank").classList.remove("transparent");  
			}
					document.getElementById("tabimages").style.display = "block";
					document.getElementById('MC').classList.remove("transparent");
					document.getElementById('VI').classList.remove("transparent");
	
	
			mops =  document.getElementsByName('mopImageCC');
				for(i=0;i<mops.length; i++){
					element = mops[i];
				document.getElementById(element.id).classList.remove("transparent");
		}
					document.getElementById("cardNumber").value="";
						document.getElementById("ccExpiryMonth").value="";
							document.getElementById("ccExpiryYear").value="";
								document.getElementById("cvvNumber").value="";
									document.getElementById("cardName").value="";
					
				document.getElementById("tabimages").style.display = "block";
					document.getElementById("divSaveCard").style.display="none";
				
					 tabhide=3;
				

			}

		document.getElementById("tabs2").style.display = "block";
		document.getElementById("tabs5").style.display = "none";
			
		
		if(tabex!=null){
		document.getElementById("tabs1").style.display = "none";
	
		}
		
			
			if(tabnb!=null){
			
			document.getElementById("tabs4").style.display = "none";
			}
		}
		 if(sstab4==id){
			
            document.getElementById("tabs4").style.display = "block";
				document.getElementById("nb").className += ' active1';
				if(tabex!=null){
					
			document.getElementById("tabs1").style.display = "none";
				}			
			if(tabccdc!=null){
			
			document.getElementById("tabs2").style.display = "none";
			}
			
		} 
	

}

function displayDefault(){
var buyerValue = document.getElementById("customerName").innerHTML;
var tab1 = document.getElementById("tabs-1");
var tab2 = document.getElementById("tabs-2");
if(buyerValue=="null"||buyerValue==""||buyerValue==null||buyerValue.trim()==""){
		
		document.getElementById("buyer").style.display = "none";
		document.getElementById("customerName").style.display = "none";
	}
	if(null==tab1){
		document.getElementById("tabs-2").style.display = "block";
				document.getElementById('ccdc').className += ' active1';
					
	    document.getElementById("tabimages").style.display = "none";
		document.getElementById("tabs2").style.display = "block";
		document.getElementById("dbpin_note").style.display="none";
		document.getElementById("ccdc_note").style.display="block";
		
	}else if(null==tab1 && null==tab2){
		document.getElementsByName("tabs-4").style.display = "block";
		document.getElementById('nb').className += ' active1';
	}else {
			document.getElementById("tabs-1").style.display = "block";
		document.getElementById('sd').className += ' active1';
	}
}


	function myCancelAction(){
	   if(confirm("Are you sure you want to return to merchant?")){
     window.location = "txncancel";
	   }else{}
    }


function alphabeticOnly() {
    var nameRegex = /^[a-zA-Z\s\b]+$/;
    var nameElement = document.getElementById("nameCheck");
    var nameValue = nameElement.value;
    if (nameValue.trim() != "" && nameValue.length >= 2) {
        if (!nameValue.match(nameRegex)) {
            document.getElementById('red2').style.display = "block";
			return false;
        } else {
			document.getElementById('red2').style.display = "none";
            return true;
        }
    }else{
    	document.getElementById('red2').style.display = "none";
    	return false;
    }
}
 
 function isValidVpa() {
    var vpaRegex = /^[A-Za-z0-9.-]*@[A-Za-z]{2,}$/;
    var vpaElement = document.getElementById("vpaCheck");
    var vpaValue = vpaElement.value;
    if (vpaValue.trim() !== "") {
        if (!vpaValue.match(vpaRegex)) {
            document.getElementById('red1').style.display = "block";
			return false;
        } else {
			document.getElementById('red1').style.display = "none";
            return true;
        }
    }else{
    	document.getElementById('red1').style.display = "none";
        return false;
    }
}

function enableButton() {
	var checkVpaValid = isValidVpa(),
		checkAlphabetOnly = alphabeticOnly(),
		upiSbmtBtn = document.getElementById('upi-sbmt');
	
	if(checkVpaValid && checkAlphabetOnly){
		upiSbmtBtn.classList.add("payActive");
		upiSbmtBtn.disabled = false;
	}else{
		upiSbmtBtn.classList.remove("payActive");
		upiSbmtBtn.disabled = true;
	}
}