/**
 * Neeraj
 * 
 */
// ///////// invoice page///////////

function sum() {
	var txtFirstNumberValue = document.getElementById('amount').value;
	if (txtFirstNumberValue == "") {
		txtFirstNumberValue = "0.00";
	}
	var txtSecondNumberValue = document.getElementById('serviceCharge').value;
	if (txtSecondNumberValue == "" || txtSecondNumberValue == ".") {
		txtSecondNumberValue = "0.00";
	}
	var txtQuantity = document.getElementById('quantity').value;
	var result = parseInt(txtQuantity) * parseFloat(txtFirstNumberValue);
	if (!isNaN(result)) {
		document.getElementById('totalAmount').value = (result + parseFloat(txtSecondNumberValue))
				.toFixed(2);
	}
}

function isDecimal(inputtxt) {
	var decimal = /^[-+]?[0-9]+\.[0-9]+$/;
	if (inputtxt.value.match(decimal)) {
		return true;
	} else {
		return false;
	}
}
function setDecimalValue() {
	var index = document.getElementById("serviceCharge").value.indexOf('.');
	if (index == '-1') {
		document.getElementById("serviceCharge").value = "";
		document.getElementById("serviceCharge").value = '0';
	}
}

function isNumber1(evt) {
	var iKeyCode = (evt.which) ? evt.which : evt.keyCode
	if (iKeyCode != 46 && iKeyCode > 31 && (iKeyCode < 48 || iKeyCode > 57)) {
		return false;
	} else {
		var len = document.getElementById("amount").value.length;
		var index = document.getElementById("amount").value.indexOf('.');

		if (index > 0 && iKeyCode == 46) {
			return false;
		}
		if (index >= 0) {
			var CharAfterdot = (len + 1) - index;
			if (CharAfterdot > 3) {

			}
		}
	}
	return true;
}

function emailCheckjs() {
	//var emailexp = /^([A-Za-z0-9_\-\.\+])+\@([A-Za-z0-9_\-\+])+\.([A-Za-z]{2,4})$/;
    var emailexp = /^[A-Za-z0-9+_.-]+@(.+)$/;

	var emailElement = document.getElementById("emailId");
	var emailValue = emailElement.value;
	if (emailValue.trim() !== "") {
		if (!emailValue.match(emailexp)) {
			document.getElementById('error2').innerHTML = "Please enter valid Email Id.";
			return false;
		} else {
			document.getElementById('error2').innerHTML = "";
			return true;
		}
	} else {
		document.getElementById('error2').innerHTML = "Please enter Email";
		return false;
	}
}

function isValidEmail() {
	var emailexp = /^[A-Za-z0-9+_.-]+@(.+)$/;
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
		document.getElementById('invalid-id').innerHTML = "Please enter Email";
		return true;
	}
}

function Validate(event) {
	var regex = /^[0-9a-zA-Z\b\_@&-]+$/;
	var key = String.fromCharCode(!event.charCode ? event.which
			: event.charCode);
	if (!regex.test(key)) {
		event.preventDefault();
		return false;
	}
}
function ValidateInvoiceNo(event) {
	// var regex = /^[0-9a-zA-Z\b\_/@(),.\-]+$/;
	var regex = /^[0-9a-zA-Z\b\_-\s\+.]+$/;
	var key = String.fromCharCode(!event.charCode ? event.which
			: event.charCode);
	if (!regex.test(key)) {

		// alert("Please enter Valid Invoice Number")
		event.preventDefault();
		return false;
	}
}

function validateOrderId(event) {
	// var regex = /^[0-9a-zA-Z\b\_-\s\+?.*?]+$/;
	var regex = /^[0-9a-zA-Z\b\_-\s\+.]+$/;
	var key = String.fromCharCode(!event.charCode ? event.which
			: event.charCode);
	if (!regex.test(key)) {
		event.preventDefault();
		return false;
	}
}

function validateRefundOrderId(event) {
	// var regex = /^[0-9a-zA-Z\b\_-\s\+?.*?]+$/;
	var regex = /^[0-9a-zA-Z\b\_-\s\+.]+$/;
	var key = String.fromCharCode(!event.charCode ? event.which
			: event.charCode);
	if (!regex.test(key)) {
		event.preventDefault();
		return false;
	}
}

function ValidateBussinessName(event) {
	var regex = /^[0-9a-zA-Z \_@&-]+$/;
	var key = String
			.fromCharCode(event.charCode ? event.which : event.charCode);
	if (!regex.test(key)) {
		event.preventDefault();
		return true;
	}
}

function lettersOnly(e, t) {
	try {
		if (window.event) {
			var charCode = window.event.keyCode;
		} else if (e) {
			var charCode = e.which;
		} else {
			return true;
		}
		if ((charCode > 64 && charCode < 91)
				|| (charCode > 96 && charCode < 123) || charCode == 8)
			return true;
		else
			return false;
	} catch (err) {
		alert(err.Description);
	}
}
function lettersSpaceOnly(e, t) {
	try {
		if (window.event) {
			var charCode = window.event.keyCode;
		} else if (e) {
			var charCode = e.which;
		} else {
			return true;
		}
		if ((charCode > 64 && charCode < 91)
				|| (charCode > 96 && charCode < 123) || charCode == 32
				|| (charCode == 8 || charCode == 46))
			return true;
		else
			return false;
	} catch (err) {
		alert(err.Description);
	}
}

// //////////// invoice Search///////////////////////
function emailCheckSerachInvoice() {
	var reg = /^[A-Za-z0-9+_.-]+@(.+)$/;
	// var emailElement = document.getElementById("customerEmail");
	var emailValue = emailElement.value;
	if (emailValue.trim() !== "") {
		if (!emailValue.match(emailexp)) {
			emailElement.focus();
			document.getElementById('error2').innerHTML = "Please enter valid Email";
			return false;
		} else {
			document.getElementById('error2').innerHTML = "";
			return true;
		}
	} else {
		emailElement.focus();
		document.getElementById('error2').innerHTML = "Please enter Email";
		return true;
	}
}

// ////////transactionResult//////////////////

function validateEmail(emailField) {
	var reg = /^[A-Za-z0-9+_.-]+@(.+)$/;
	if (emailField.value !== "") {
		if (reg.test(emailField.value) == false) {
			alert('Invalid Email Address');
			return false;
		}
	}

	return true;

}
function validatePhone(phoneField) {
	var phreg = /^[0-9_]+$/;
	if (phoneField.value !== "") {
		if (phreg.test(phoneField.value) == false) {
			alert('Invalid Phone Number');
			return false;
		}
	}

	return true;

}
function invoiceNoCheck() {
	var invoiceexp = /^[0-9a-zA-Z\b\_/@(),.\-]+$/;
	var invoiceElement = document.getElementById("invoiceNo");
	var invoiceValue = invoiceElement.value;
	if (invoiceValue.trim() !== "") {
		if (!invoiceValue.match(invoiceexp)) {
			invoiceElement.focus();
			document.getElementById('erro').innerHTML = "Please enter valid Invoice No.";
			return false;
		} else {
			document.getElementById('erro').innerHTML = "";
			return true;
		}
	} else {
		invoiceElement.focus();
		document.getElementById('erro').innerHTML = "Please enter Invoice No.";
		return false;
	}
}

function invoicePhoneCheck() {
	var phoneexp = /^[0-9_]+$/;
	;
	var phoneElement = document.getElementById("phone");
	var phoneValue = phoneElement.value;
	if (phoneValue.trim() !== "") {
		if (!phoneValue.match(phoneexp)) {
			phoneElement.focus();
			document.getElementById('err').innerHTML = "Please enter valid Phone No.";
			return false;
		} else {
			document.getElementById('err').innerHTML = "";
			return true;
		}
	} else {
		phoneElement.focus();
		document.getElementById('err').innerHTML = "Please enter Phone No.";
		return false;
	}
}

function invoiceNameCheck() {
	var nameexp = /^[a-zA-Z ]*$/;
	var nameElement = document.getElementById("name");
	var nameValue = nameElement.value;
	if (nameValue.trim() !== "") {
		if (!nameValue.match(nameexp)) {
			nameElement.focus();
			document.getElementById('errrrr').innerHTML = "Please enter valid Name.";
			return false;
		} else {
			document.getElementById('errrrr').innerHTML = "";
			return true;
		}
	} else {
		nameElement.focus();
		document.getElementById('errrrr').innerHTML = "Please enter Name";
		return false;
	}
}

function validateName() {
	var currencyCode = document.f1.currencyCode.value;
	var amount = document.f1.amount.value;
	var status = false;

	if (currencyCode < 1) {
		document.getElementById("currencyCodeloc").innerHTML = "Select currencyCode Type";
		status = false;
	} else {
		document.getElementById("currencyCodeloc").innerHTML = "";
		status = true;
	}
	if (amount < 1) {
		document.getElementById("amountloc").innerHTML = "Enter Amount";
		status = false;
	} else {
		document.getElementById("amountloc").innerHTML = "";
		status = true;
	}
	return status;
}
function passCheck() {
	var passexp = /^(?=[a-zA-Z0-9!@,_+/=]{5,32}$)(?=.*?[a-z])(?=.*?[A-Z])(?=.*?[0-9]).*/;
	var passwordElement = document.getElementById("password");
	var passwordValue = passwordElement.value;
	if (passwordValue.trim() !== "") {
		if (!passwordValue.match(passexp)) {
			passwordElement.focus();
			document.getElementById('wrong-password').innerHTML = "Please enter valid Password.";
			return false;
		} else {
			document.getElementById('wrong-password').innerHTML = "";
			return true;
		}
	} else {
		passwordElement.focus();
		document.getElementById('wrong-password').innerHTML = "Please enter Password";
		return true;
	}
}
function isAlphaNumeric(e) { // Alphanumeric only
	var k;
	document.all ? k = e.keycode : k = e.which;
	return ((k > 47 && k < 58) || (k > 64 && k < 91) || (k > 96 && k < 123) || k == 32);
}

function isAlphaNumericUnderScore(e) { // Alphanumeric only
	var k;
	document.all ? k = e.keycode : k = e.which;
	return ((k > 47 && k < 58) || (k > 64 && k < 91) || (k > 96 && k < 123) || k == 95);
}

function ValidateAlpha(evt) {
	var keyCode = (evt.which) ? evt.which : evt.keyCode
	if ((keyCode < 65 || keyCode > 90) && (keyCode < 97 || keyCode > 123)
			&& keyCode != 32)

		return false;
	return true;
}

function ValidateMerchantAccountSetup(event) {
	var regex = /^[0-9a-zA-Z\_]+$/;
	var key = String
			.fromCharCode(event.charCode ? event.which : event.charCode);
	if (!regex.test(key)) {
		event.preventDefault();
		return true;
	}
}

function emailCheckMerchantAccountSetup() {
	var emailexp = /^[A-Za-z0-9+_.-]+@(.+)$/;
	var emailElement = document.getElementById("transactionEmailId");
	var emailValue = emailElement.value;
	if (emailValue.trim() !== "") {
		if (!emailValue.match(emailexp)) {
			// emailElement.focus();
			document.getElementById('error2').innerHTML = "Please enter valid Email";
			return false;
		} else {
			document.getElementById('error2').innerHTML = "";
			return true;
		}
	} else {
		// emailElement.focus();
		document.getElementById('error2').innerHTML = "Please enter Email";
		return false;
	}
}

var isShift = false;

var seperator = "/";

function DateFormat(txt, keyCode)

{

	if (keyCode == 16)

		isShift = true;

	// Validate that its Numeric

	if (((keyCode >= 48 && keyCode <= 57) || keyCode == 8 ||

	keyCode <= 37 || keyCode <= 39 ||

	(keyCode >= 96 && keyCode <= 105)) && isShift == false)

	{

		if ((txt.value.length == 2 || txt.value.length == 5) && keyCode != 8)

		{

			txt.value += seperator;

		}

		return true;

	}

	else {
		return false;
	}

}

function validateExpireyDay(evt) {
	var theEvent = evt || window.event;
	var key = theEvent.keyCode || theEvent.which;
	key = String.fromCharCode(key);
	var regex = /[0-9]|\./;
	if (!regex.test(key)) {
		theEvent.returnValue = false;
		if (theEvent.preventDefault)
			theEvent.preventDefault();
	}
}
function ValidateMerchant() {
	if (document.getElementById("merchant") != null) {
		if (document.getElementById("merchant").selectedIndex == 0) {
			document.getElementById('spanMerchant').innerHTML = "Please select merchant";
		} else {
			document.getElementById('spanMerchant').innerHTML = "";
		}
	}

}
function setDecimalLimit() {
	var txtSecondNumberValue = document.getElementById('serviceCharge').value;
	if (txtSecondNumberValue != "") {
		if (txtSecondNumberValue != ".") {
			document.getElementById('serviceCharge').value = parseFloat(
					txtSecondNumberValue).toFixed(2);
		}
	}
}
function onlyCharater() {
	if ((event.keyCode > 64 && event.keyCode < 91)
			|| (event.keyCode > 96 && event.keyCode < 123)
			|| event.keyCode == 8)
		return true;
	else {
		return false;
	}
}

// *********validation for ticking*******
function isValidTicketEmail() {
	var emailReg = /^[_A-Za-z0-9-]+(\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\.[A-Za-z0-9-]+)*(\.[_A-Za-z0-9-]+)$/;
	var emailElement = document.getElementById("email");
	var emailValue = emailElement.value;
	if (emailValue.trim() !== "") {
		if (!emailValue.match(emailReg)) {
			document.getElementById('emailValid').innerHTML = "Please enter valid Email";
			return false;
		} else {
			document.getElementById('emailValid').innerHTML = "";
			return true;
		}
	}
}

function isValidTicketMobileNo() {
	var phoneReg = /^[0-9_]+$/;
	var phoneElement = document.getElementById("mobileNo");
	var phoneValue = phoneElement.value;
	if (phoneValue.trim() !== "") {
		if (!phoneValue.match(phoneReg)) {
			document.getElementById('phoneValid').innerHTML = "Please enter valid Phone";
			return false;
		} else {
			document.getElementById('phoneValid').innerHTML = "";
			return true;
		}
	}

}
function isCharacterKey(event) {
	var k;
	document.all ? k = event.keyCode : k = event.which;
	return ((k > 64 && k < 91) || (k > 96 && k < 123) || (k == 8));
}
function noSpace(event, inputName) { // same for cc/dc
	var str, str0, name;
	name = inputName.id
	str = inputName.value;
	str0 = str.charAt(0);
	if (str0 == "") {
		name.value = "";
		// return false;
	} else {
		if (event.charCode == 32) {
			if (name == "fname") {
				document.getElementById('fname').value = str + ' ';
			} else if (name == "lname") {
				document.getElementById('lname').value = str + ' ';
			}
		}
	}
}

function alphanumeric(event) {
	var regex = /^[0-9A-Z]+$/;
	var key = String.fromCharCode(!event.charCode ? event.which
			: event.charCode);
	if (!regex.test(key)) {
		event.preventDefault();
		return false;
	}
}

function isNumberKey(evt) {
	var charCode = (evt.which) ? evt.which : event.keyCode
	if (charCode > 31 && (charCode < 48 || charCode > 57))
		return false;
	return true;
}
// note call this function - onkeypress="return isNumberKey(event)"
function isNumber(evt) {
	var iKeyCode = (evt.which) ? evt.which : evt.keyCode
	if (iKeyCode = 46 && iKeyCode > 31 && (iKeyCode < 48 || iKeyCode > 57))
		return false;

	return true;
}
// note: call this function onkeypress="javascript:return isNumber (event)";
// isNumber function for number and point entry in input.

// Method to get current time stamp.
function getCurrentTimeStamp() {
	let d = new Date, dformat = [ d.getDate().padLeft(),
			(d.getMonth() + 1).padLeft(), d.getFullYear() ].join('/')
			+ ' '
			+ [ d.getHours().padLeft(), d.getMinutes().padLeft(),
					d.getSeconds().padLeft() ].join(':');

	return dformat;
}

// Helper method for getCurrentTimeStamp()
Number.prototype.padLeft = function(base, chr) {
	var len = (String(base || 10).length - String(this).length) + 1;
	return len > 0 ? new Array(len).join(chr || '0') + this : this;
}
// comma separation method in INR Start
function inrFormat(temp) { // nStr is the input string
	// temp = 1000000.00
	if (temp == 0) {
		return 0;
	}
	// console.log(" Hiiii " + temp);
	if (temp == undefined || temp == null || temp == '') {
		return 0;
	}
	numArr = temp.toString().split('.');
	nStr = numArr[0];
	nStr += '';
	x = nStr.split('.');
	x1 = x[0];
	x2 = x.length > 1 ? '.' + x[1] : '';
	var rgx = /(\d+)(\d{3})/;
	var z = 0;
	var len = String(x1).length;
	var num = parseInt((len / 2) - 1);

	while (rgx.test(x1)) {
		if (z > 0) {
			x1 = x1.replace(rgx, '$1' + ',' + '$2');
		} else {
			x1 = x1.replace(rgx, '$1' + ',' + '$2');
			rgx = /(\d+)(\d{2})/;
		}
		z++;
		num--;
		if (num == 0) {
			break;
		}
	}
	let result = x1 + x2;
	// console.log('' + num[1])
	if (numArr[1] != undefined) {
		result += '.' + numArr[1];
	}
	// console.log(result);
	return result;
}
// End here
