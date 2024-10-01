
    if (self == top) {
      var theBody = document.getElementsByTagName('body')[0];
      theBody.style.display = "block";
    } else {
      top.location = self.location;
    }
  
    $('#emailId').keyup(function () {
      str = $(this).val()
      str = str.replace(/\s/g, '')
      $(this).val(str)
    });

    function generateCaptcha() {
      img = document.getElementById("captchaImageOtp");
      img.src = "../Captcha.jpg/" + Math.random();

    }

    function isValidEmail(inputId) {
      var emailexp = /^[A-Za-z0-9+_.-]+@(.+)$/;
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

    function removeOtpErr() {
      var otpValues = document.getElementById("otp").value;
      if (otpValues != null || otpValues != "" || otpValues.length >= 5) {
        document.getElementById("otpError").style.display = "none";
      }
    }

    function checkOtp() {
      var regex = /^[0-9]+$/;
      var key = String.fromCharCode(event.charCode ? event.which : event.charCode);
      if (!regex.test(key)) {
        event.preventDefault();
        return true;
      }
    }

    function checkOtpValidation(event) {
      var emailIdVal = document.getElementById("emailId").value;
      var otpVal = document.getElementById("otp").value;
      var otpCaptcha = document.getElementById("captcha").value;
      document.getElementById("emailError").style.display = "none";
      document.getElementById("otpError").style.display = "none";
      document.getElementById("enterCaptcha").style.display = "none";
      if (emailIdVal == null || emailIdVal == "" || !isValidEmail('emailId')) {
        document.getElementById("emailError").style.display = "block";
        event.preventDefault();
      } else if (otpVal == null || otpVal == "" || otpVal.length < 5) {
		$(".errorMessage").hide();  
        document.getElementById("otpError").style.display = "block";
		if (otpCaptcha == null || otpCaptcha == "") {
			document.getElementById("enterCaptcha").style.display = "block";
		}
        event.preventDefault();
      } else if (otpCaptcha == null || otpCaptcha == "") {
		$(".errorMessage").hide();
        document.getElementById("enterCaptcha").style.display = "block";
        event.preventDefault();
      } else {
        document.getElementById("emailError").style.display = "none";
        document.getElementById("otpError").style.display = "none";
        document.getElementById("enterCaptcha").style.display = "none";
      }
    }
    function emailCheck() {
      var emailReg = /^[_A-Za-z0-9-]+(\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\.[A-Za-z0-9-]+)*(\.[_A-Za-z0-9-]+)$/;
      var emailElement = document.getElementById("emailId");
      var emailValue = emailElement.value;
      if (emailValue.trim() !== "") {
        if (!emailValue.match(emailReg)) {
          document.getElementById('emailError').style.display = "block";
		  document.getElementById("Btnaccescd").click();
          return false;
        } else {
          document.getElementById('emailError').style.display = "none";
          return true;
        }
      }
    }
 