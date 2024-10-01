/**
 * Shaiwal
 */
function passCheck() {
    var passexp = /^(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[!@,_+\=]).{5,32}$/;
    var passwordElement = document.getElementById("password");
    var passwordValue = passwordElement.value;
    if (passwordValue.trim() !== "") {
        if (!passwordValue.match(passexp)) {
            passwordElement.focus();
            document.getElementById('error2').innerHTML = "Please Enter Valid Password.";
            return false;
        } else {
            document.getElementById('error2').innerHTML = "";
            return true;
        }
    } else {
        passwordElement.focus();
        document.getElementById('error2').innerHTML = "Password can't be blank";
        return true;
    }
}

function emailCheck() {
    var emailexp =/^[A-Za-z0-9+_.-]+@(.+)$/;
    var emailElement = document.getElementById("emailId");
    var emailValue = emailElement.value;
    if (emailValue.trim() !== "") {
        if (!emailValue.match(emailexp)) {
            emailElement.focus();
            document.getElementById('error2').innerHTML = "Please Enter Valid Email Id.";
            return false;
        } else {
            document.getElementById('error2').innerHTML = "";
            return true;
        }
    } else {
        emailElement.focus();
        document.getElementById('error2').innerHTML = "Email Id can't be blank";
        return true;
    }
}

function callMerchantEnv(){
	var url=window.location.href;

	if (url.search("secure.pay10.com")>1 )
	{
		document.getElementById("merchantEnviroment").innerHTML="This is the Staging Enviroment";
		document.getElementById("merchantEnviroment").className="Staging Staging-text";
		}
	else 
	{
		document.getElementById("merchantEnviroment").innerHTML="";
		}
	}