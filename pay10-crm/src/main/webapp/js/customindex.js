var currentBoxNumber = 0;
$(".form-control").keyup(function (event) {
    if (event.keyCode == 13) {
        textboxes = $("input.form-control");
        currentBoxNumber = textboxes.index(this);
        console.log(textboxes.index(this));
        if (textboxes[currentBoxNumber + 1] != null) {
            nextBox = textboxes[currentBoxNumber + 1];
            nextBox.focus();
            nextBox.select();
            event.preventDefault();
            return false;
        }
    }
});
function refresh() {
	
	$(".errorMessage").hide();
	$("#firstTabCaptcha").hide();
	document.getElementById('captcha').value = '';

}
function refreshOtp() {
	// generateCaptcha();
	$("#enterCaptcha").hide();
	$("#secondTabCaptcha").hide();
	document.getElementById('captchaOtp').value = '';
	// $("#refreshOtpCaptcha").hide();
}

$('#emailId').keyup(function(){
    str = $(this).val()
    str = str.replace(/\s/g,'')
    $(this).val(str)
});

$('#userLoginId').keyup(function(){
    str = $(this).val()
    str = str.replace(/\s/g,'')
    $(this).val(str)
});

function isValidEmail(inputId){
    var emailexp = /^[A-Za-z0-9+_.-]+@(.+)$/;;
    var emailElement = document.getElementById(inputId);
    var emailValue = emailElement.value;
    if (emailValue.trim() !== "") {
        if (emailValue.match(emailexp)) {
            return true;
        } else {
            return false;
        }
    } else {
        return false;
    }
}
var emailId = document.getElementById('emailId'),
	errorEmail = document.getElementsByClassName('errorEmail')[0],
	passwordRejex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!@,_+/=]).{8,32}$/g,
	password = document.getElementById('password'),
	errorPassword = document.getElementsByClassName('errorPassword')[0],
	errorCaptcha = document.getElementsByClassName('errorCaptcha')[0],
	errorEmptyCaptcha = document.getElementsByClassName('errorEmptyCaptcha')[0],
	captcha = document.getElementById('captcha');
	
function validateMyForm(e) {
	var passwordVal = password.value,
		captchaVal = captcha.value;

	if(!emailId.value || emailId.value.length<6 || !isValidEmail('emailId')){
		errorEmail.style.display = "block";
		e.preventDefault();
	}else{
		errorEmail.style.display = "none";
	}

	if(!passwordVal.match(passwordRejex)){
		errorPassword.style.display = "block";
		e.preventDefault();
	}else{
		errorPassword.style.display = "none";
	}

	if(captcha.value.length>=1 && captcha.value.length<6){
		errorCaptcha.style.display = "block";
		errorEmptyCaptcha.style.display = "none";
		e.preventDefault();
	}else if(captcha.value.length==6){
		errorEmptyCaptcha.style.display = "none";
		errorCaptcha.style.display = "none";
	}else{
		if(captcha.value.length == 0){
			$(".errorMessage").hide();
			errorEmptyCaptcha.style.display = "block";
			e.preventDefault();
		}
		errorCaptcha.style.display = "none";
	}
}

function emailOnBlur(){
	$('#wwerr_emailId').remove();
	if(!emailId.value || emailId.value.length<6 || !isValidEmail('emailId')){
		errorEmail.style.display = "block";
	}else{
		errorEmail.style.display = "none";
	}
}
function captchaOnBlur(){
	$('#wwerr_captcha').remove();
	if(captcha.value.length>=1 && captcha.value.length<6 ){
		errorCaptcha.style.display = "block";
		errorEmptyCaptcha.style.display = "none";		
	}else if(captcha.value.length==6){
		errorEmptyCaptcha.style.display = "none";
		errorCaptcha.style.display = "none";
	}else{
		if(captcha.value.length == 0){
			$(".errorMessage").hide();
			errorEmptyCaptcha.style.display = "block";
		}
		errorCaptcha.style.display = "none";
	}
}
function passwordOnBlur(){
	$('#wwerr_emailId').remove();
	var passwordVal = password.value;
	if(!passwordVal.match(passwordRejex)){
		errorPassword.style.display = "block";

	}else{
		errorPassword.style.display = "none";
	}
}
function checkOtp(){
  var otpElement = document.getElementById("otp");
      var otpError   = document.getElementById("errorOtp")
      if( !otpElement.value.length === 0)return;
      otpError.style.display = 'none';
	var regex = /^[0-9]+$/;
    var key = String.fromCharCode(event.charCode ? event.which : event.charCode);
    if (!regex.test(key)) {
        event.preventDefault();
        return true;
    }
}
function emailCheck(){
	 var emailReg = /^[_A-Za-z0-9-]+(\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\.[A-Za-z0-9-]+)*(\.[_A-Za-z0-9-]+)$/;
	    var emailElement = document.getElementById("userLoginId");
	    var emailValue = emailElement.value;
	    if (emailValue.trim() !== "") {
	        if (!emailValue.match(emailReg)) {
	            document.getElementById('otpEmailError').style.display = "block";
	            return false;
	        } else {
	            document.getElementById('otpEmailError').style.display = "none";
	            return true;
	        }
	    } 
}

function generatePassOtp(){
	img = document.getElementById("captchaImage");
  img.src = "../Captcha.jpg/" + Math.random();
  $('#emailId').val($("#userLoginId").val());
}
function generateCaptcha(){
		img = document.getElementById("captchaImageOtp");
		img.src = "../Captcha.jpg/" + Math.random();
    $('#userLoginId').val($("#emailId").val());
	 document.getElementById("wwerr_captchaOtp").style.display = "none"

	}
	$("#refreshOtpCaptcha").click(function(){
		  generateCaptcha();
		});

function removeOtpErr(){
	var otpValues = document.getElementById("otp").value;
	if(otpValues != null || otpValues != "" || otpValues.length >= 6){
		document.getElementById("errorOtp").style.display = "none";
	}
}


function checkOtpValidation(event){
	var emailIdVal = document.getElementById("userLoginId").value;
	var otpVal = document.getElementById("otp").value;
	var otpCaptcha = document.getElementById("captchaOtp").value;
	    document.getElementById("otpEmailError").style.display = "none";
		document.getElementById("errorOtp").style.display = "none";
		document.getElementById("enterCaptcha").style.display = "none";
	if (emailIdVal == null || emailIdVal == "" || !isValidEmail('userLoginId')){
		document.getElementById("otpEmailError").style.display = "block";
		event.preventDefault();
	}else if(otpVal == null || otpVal == "" || otpVal.length<5){
		document.getElementById("errorOtp").style.display = "block";
		event.preventDefault();
	}else if(otpCaptcha == null || otpCaptcha == ""){
		document.getElementById("enterCaptcha").style.display = "block";
		event.preventDefault();
	}else{
		document.getElementById("otpEmailError").style.display = "none";
		document.getElementById("errorOtp").style.display = "none";
		document.getElementById("enterCaptcha").style.display = "none";
	}
}

function isNumberKey(evt){
	var charCode = (evt.which) ? evt.which : event.keyCode
	if (charCode > 31 && (charCode < 48 || charCode > 57))
		return false;
}