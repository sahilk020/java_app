
$(document).ready(function(){

  document.getElementById("loadingInner").style.display = "none";
});
    function genrateOTP() {
      var emailId = document.getElementById("emailId").value.trim();
      if (emailId == null || emailId == "") {
        event.preventDefault();
        $('#modaltogenerateotpReset').modal('show');
        //alert("Please enter email Id to generate OTP")
      }

      else {
        document.getElementById("generateOtpBtn").classList.add("inactiveLink");
        $.ajax({
          "url": "forgetPinAction",
          "type": "POST",
          "data": {

            "emailId": emailId,
            "struts.token.name": "token",
          },
          success: function (data) {
            if (data.response == "Invalid User") {
              document.getElementById("invaliduser").innerHTML = data.response;
              $('#invalidusermodalReset').modal('show');
              //alert(data.response);
              document.getElementById("dataValue").innerHTML = data.response;
              document.getElementById("generateOtpBtn").classList.remove("inactiveLink");
            }
            else {
              document.getElementById("otpnumber").innerHTML = data.response;
              $('#modaltosendOTPReset').modal('show');
              //	alert(data.response);
              var timeleft = 30;
              var otpTimer = setInterval(function () {
                document.getElementById("dataValue").style.display = "block";
                document.getElementById("dataValue").innerHTML = data.response;
                document.getElementById("otpMsg").innerHTML ="Next"+ ' ' + timeleft + " seconds remaining to regenerate OTP";
                document.getElementById("otpMsg").style.display = "block";
                document.getElementById("generateOtpBtn").classList.add("inactiveLink");
                timeleft--;
                if (timeleft <= 0) {
                  clearInterval(otpTimer);
                  document.getElementById("otpMsg").style.display = "none";
                  document.getElementById("generateOtpBtn").classList.remove("inactiveLink");
                  document.getElementById("generateOtpBtn").innerHTML = "Resend OTP";
                  document.getElementById("dataValue").style.display = "none";
                }
              }, 1000);
            }
          },
          error: function (data) {
            document.getElementById("dataValue").innerHTML = data.response;
            document.getElementById("generateOtpBtn").classList.remove("inactiveLink");
          }
        });
      }

    }


function genrateResetPasswordLink() {
  document.getElementById("loadingInner").style.display = "block";

  var emailId = document.getElementById("emailId").value.trim();
  if (emailId == null || emailId == "") {
    event.preventDefault();
    $('#modaltogenerateotpReset').modal('show');
    document.getElementById("loadingInner").style.display = "none";
    //alert("Please enter email Id to generate OTP")
  } else {
    $.ajax({
      type: "POST",
      url: "resetPasswordEmailAction",
      data: {"emailId": emailId, "struts.token.name": "token",},
      success: function (data) {

        document.getElementById("loadingInner").style.display = "none";
        // document.getElementById("loading").style.display = "none";
        $('#submit').attr("disabled", false);
        $('#generateResetPasswordLink').removeAttr("href");
        $('#generateResetPasswordLink').prop("onclick", null).off("click");
        var response = ((data["Invalid request"] != null) ? (data["Invalid request"].response[0]) : (data.response));
        if (null != response) {
          alert(response);

          document.getElementById("loadingInner").style.display = "none";
        }
        if (data.errorCode == "314")
          window.location.href = "index";
        //document.getElementById("loading").style.display = "none";
        $('#submit').attr("disabled", false);
      }
    });

  }
}


    $(function () {
      $(document).ready(
        function () {
          refresh();
        }
      );
      // function refresh() {
      //   img = document.getElementById("captchaImage");
      //   img.src = "../Captcha.jpg/" + Math.random();
      // }
      $('body').on('keydown', '#emailId', function (e) {
        if (e.which === 32 && e.target.selectionStart === 0) {
          return false;
        }
      });
      // $('body').on('keydown', '#captcha', function (e) {
      //   if (e.which === 32 && e.target.selectionStart === 0) {
      //     return false;
      //   }
      // });
      $("#Btnaccescd").click(function () {
        refresh();
      });
    });
 