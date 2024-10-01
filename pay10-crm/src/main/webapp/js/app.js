var app = angular.module('myApp', []);

app.controller('loginCtrl', function ($scope) {
    // $scope.word = /^[a-z]+[a-z0-9._]+@[a-z]+\.[a-z.]{2,5}$/;


    $scope.refresh = function () {
        $scope.captchafirsttab = "";
        $(".errorMessage").hide();
        $("#firstTabCaptcha").hide();

    }
    $scope.refreshOtp = function () {
        generateCaptcha();
        $("#enterCaptcha").hide();
        $("#secondTabCaptcha").hide();
        // $("#refreshOtpCaptcha").hide();
    }

    // $scope.emailPass = function () {
    //      var emailElement = document.getElementById("emailId");
    //     var emailIdError = document.getElementById("invalidemail")
    //     if (!emailElement.value.length === 0 ) return;
    //     emailIdError.style.display = 'none';

    // }
    $scope.captchaPass = function () {
        var captchaElement = document.getElementById("captcha");
        var captchaError = document.getElementById("firstTabCaptcha")
        if (!captchaElement.value.length === 0) return;
        captchaError.style.display = 'none';

    }
    $scope.passwordPass = function () {
        var passElement = document.getElementById("password");
        var passError = document.getElementById("errorPassword")
        if (!passElement.value.length === 0) return;
        passError.style.display = 'none';

    }
    
});