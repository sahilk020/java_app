<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@taglib prefix="s" uri="/struts-tags"%>
<html dir="ltr" lang="en-US">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Loading...</title>
<script type="text/javascript" src="../js/jquery.min.js"></script>
<script src="../js/core/popper.min.js"></script>
<script src="../js/core/bootstrap-material-design.min.js"></script>
<script src="../js/plugins/perfect-scrollbar.jquery.min.js"></script>
<!-- Control Center for Material Dashboard: parallax effects, scripts for the example pages etc -->
<script src="../js/material-dashboard.js?v=2.1.0" type="text/javascript"></script>
<link href="../css/Jquerydatatableview.css" rel="stylesheet" />
<link href="../css/Jquerydatatable.css" rel="stylesheet" />
<link href="../css/default.css" rel="stylesheet" type="text/css" />
<link href="../css/jquery-ui.css" rel="stylesheet" />
<script src="../js/jquery.dataTables.js"></script>
<script type="text/javascript" src="../js/dataTables.buttons.js"></script>
<script type="text/javascript" src="../js/pdfmake.js"></script>
<link href="../fonts/css/font-awesome.min.css" rel="stylesheet">
<link href="../css/bootstrap.min.css" rel="stylesheet">
<script  src="../js/bootstrap.min.js"></script>

<style type="text/css">
#pbar_outerdiv { cursor: pointer; }

/* Spinner Wrapper */
.loader {
    width: 100vw;
    height: 100vh;
    background: #fff;
    position: fixed;
    top: 0;
    left: 0;
}

.loader-inner {
    position: absolute;
    top: 40%;
    left: 50%;
    transform: translate(-50%, -50%);
}


/* Spinner */
.lds-roller {
    display: inline-block;
    position: relative;
    width: 64px;
    height: 64px;
}
.lds-roller div {
    animation: lds-roller 1.2s cubic-bezier(0.5, 0, 0.5, 1) infinite;
    transform-origin: 32px 32px;
}
.lds-roller div:after {
    content: " ";
    display: block;
    position: absolute;
    width: 6px;
    height: 6px;
    border-radius: 50%;
    background: #333;
    margin: -3px 0 0 -3px;
}
.lds-roller div:nth-child(1) {
    animation-delay: -0.036s;
}
.lds-roller div:nth-child(1):after {
    top: 50px;
    left: 50px;
}
.lds-roller div:nth-child(2) {
    animation-delay: -0.072s;
}
.lds-roller div:nth-child(2):after {
    top: 54px;
    left: 45px;
}
.lds-roller div:nth-child(3) {
    animation-delay: -0.108s;
}
.lds-roller div:nth-child(3):after {
    top: 57px;
    left: 39px;
}
.lds-roller div:nth-child(4) {
    animation-delay: -0.144s;
}
.lds-roller div:nth-child(4):after {
    top: 58px;
    left: 32px;
}
.lds-roller div:nth-child(5) {
    animation-delay: -0.18s;
}
.lds-roller div:nth-child(5):after {
    top: 57px;
    left: 25px;
}
.lds-roller div:nth-child(6) {
    animation-delay: -0.216s;
}
.lds-roller div:nth-child(6):after {
    top: 54px;
    left: 19px;
}
.lds-roller div:nth-child(7) {
    animation-delay: -0.252s;
}
.lds-roller div:nth-child(7):after {
    top: 50px;
    left: 14px;
}
.lds-roller div:nth-child(8) {
    animation-delay: -0.288s;
}
.lds-roller div:nth-child(8):after {
    top: 45px;
    left: 10px;
}
@keyframes lds-roller {
    0% {
        transform: rotate(0deg);
    }
    100% {
        transform: rotate(360deg);
    }
}

body {
    min-height: 100vh;

    background-color: #de6262;
    background-image: linear-gradient(147deg, #de6262 0%, #ffb88c 100%);
}

p.lead {
    text-shadow: 1px 1px 0 rgba(0, 0, 0, 0.1)
}

.btn-outline-light:hover, .btn-outline-light:focus {
    color: #de6262 !important;
  
}

</style>

</head>
<body style="padding-top: 20%;">
	<script type="text/javascript">
		$( window ).load(function() {
			var order_id = '<s:property value="orderId"/>';
			var order_token = '<s:property value="token"/>';
			verifyUpiResponseReceived(order_id,order_token);
		});
		
		function sleep(delay) {
		    var start = new Date().getTime();
		    while (new Date().getTime() < start + delay);
		}
		
		var reqCount = 0;
		function verifyUpiResponseReceived(pgRefNum,token) {
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

		            if (trans == "Sent to Bank" && reqCount < 15) {
		                    sleep(10000);
		                    reqCount = reqCount + 1;
		                verifyUpiResponseReceived(pgRefNum);
		            } else {
						debugger
		               // disable loading
		                var form = document.getElementById("upiResponseForm");
		                form.action = myMap.RETURN_URL;
		                    for (key in myMap) {
		                        form.innerHTML += ('<input type="hidden" name="' + key + '" value="' + myMap[key] + '">');
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
	</script>
<!-- 	<div id="pbar_outerdiv"
		style="width: 500px; height: 50px; margin-left:25%; border: 1px solid grey; z-index: 1; position: relative; border-radius: 5px; -moz-border-radius: 5px;">
		<div id="pbar_innerdiv"
			style="background-color: lightblue; z-index: 2; height: 100%; width: 0%;"></div>
		<div id="pbar_innertext"
			style="z-index: 3; position: absolute; top: 0; left: 0; height: 100%; color: black; font-weight: bold; text-align: center;">0&nbsp;s</div>
	</div> -->
	
	<div class="loader text-center">

    <div class="loader-inner">
	<div class="logo">
    <img src="https://pay.pay10.com/LOGOANDBANNER/OAGG001070/pay10logo.png" height="125" style="margin-left: 10%;">
	</div>
        <!-- Animated Spinner -->
        <div class="lds-roller mb-3">
            <div></div>
            <div></div>
            <div></div>
            <div></div>
            <div></div>
            <div></div>
            <div></div>
            <div></div>
        </div>
        
        <!-- Spinner Description Text [For Demo Purpose]-->
        <h4 class="text-uppercase font-weight-bold">Loading</h4>
		<%-- <strong class="countdown text-dark font-weight-bold"> </strong> --%>
        <p class="font-italic text-muted">Do not refresh this page or press back button or close the window</p>
<p>Note : Please wait it will take littile longer time to redirect you to merchant website.  </p>
    </div>
</div>
	
	
	
	<s:form name="upiResponseForm" id="upiResponseForm" action=""
		method="post" target="_parent"></s:form>

	<script type="text/javascript">
		var timer = 0, timeTotal = 300000, timeCount = 20, timeStart = 0, cFlag;
		function updateProgress(percentage) {
			var x = (percentage / timeTotal) * 100, y = x.toFixed(3);
			var totalSec = (percentage / 1000);
			var min = parseInt(totalSec / 60);
			var sec = parseInt(totalSec % 60);
			var hr = parseInt(min / 60);
			min = parseInt(min % 60);
			$('#pbar_innerdiv').css("width", x + "%");
			$('#pbar_innertext').css("left", x + "%").text(
					min + ":" + sec + "");
		}

		function animateUpdate() {
			var perc = new Date().getTime() - timeStart;
			if (perc < timeTotal) {
				updateProgress(perc);
				timer = setTimeout(animateUpdate, timeCount);
			} else {
				updateProgress(timeTotal);
			}
		}

		$(document).ready(function() {
				if (cFlag == undefined) {
					clearTimeout(timer);
					cFlag = true;
					timeStart = new Date().getTime();
					animateUpdate();
				} else if (!cFlag) {
					cFlag = true;
					animateUpdate();
				} else {
					clearTimeout(timer);
					cFlag = false;
				}
		});
	</script>
<script>

function startTimer(duration, display) {
debugger
    var timer = duration, minutes, seconds;
    setInterval(function () {
        minutes = parseInt(timer / 60, 10);
        seconds = parseInt(timer % 60, 10);

        minutes = minutes < 10 ? "0" + minutes : minutes;
        seconds = seconds < 10 ? "0" + seconds : seconds;

        display.textContent = minutes + ":" + seconds;

        if (--timer < 0) {
            timer = 0;
        }
    }, 1000);
}

window.onload = function () {
    var fiveMinutes = 60 * 5,
        display = document.querySelector('.countdown');
    startTimer(fiveMinutes, display);
};

</script>
</body>
</html>