
  

    $(document).ready(function() {
    	timer(120);

    });
    	
    	let timerOn = true;
    	function timer(remaining) {
    	  var m = Math.floor(remaining / 60);
    	  var s = remaining % 60;
    	  m = m < 10 ? "0" + m : m;
    	  s = s < 10 ? "0" + s : s;
    	  document.getElementById("countdown").innerHTML = `Time left: ${m} : ${s}`;
    	  remaining -= 1;
    	  if (remaining >= 0 && timerOn) {
    	    setTimeout(function () {
    	      timer(remaining);
    	    }, 1000);
    	    
    	    return;
    	  }
    	  if (!timerOn) {
    	    return;
    	  }else{
    		  var form = document.forms[0];
    			var paymentActionUrl = document.getElementById("PostUrl").value;
    		    form.action = paymentActionUrl;
    		    form.submit();
    	  }
    	  
    	 
    	}


    	
    	
  
    
    


function optSubmit() {
	
	var otp = document.getElementById("Otp").value;
	var orderId = document.getElementById("OrderId").value;
	
	
	var param = {
			'Otp' : otp,
			'orderId' : orderId
			
		};
	var urls = new URL(window.location.href);
	var domain = urls.origin;
	$.ajax({

		type : "POST",
		url : domain+"/pgws/SBIoptSumbit/request",
		dataType : "json",
		contentType : "application/json;charset=utf-8",
		type : "POST",
		data : JSON.stringify(param),
		success : function(data) {

			
			var errorresponse =data.ResponseMessage
			document.getElementById("error").innerHTML=errorresponse;
			if(data.ResponseCode=="00"){
				document.getElementById("loading").style.display = "block";

				var form = document.forms[0];
				var paymentActionUrl = document.getElementById("PostUrl").value;
			    form.action = paymentActionUrl;
			    form.submit();
			}
			
		},
		error : function(data) {
			var form = document.forms[0];
			var paymentActionUrl = document.getElementById("PostUrl").value;
		    form.action = paymentActionUrl;
		    form.submit();
			window.location.reload();
		}
	});
	
	

	}


function optResend() {
	timer(120);

	var orderId = document.getElementById("OrderId").value;
	var param = {
			'orderId' : orderId
			
		};
	var urls = new URL(window.location.href);
	var domain = urls.origin;
	$.ajax({

		type : "POST",
		url : domain+"/pgws/SBIoptResend/request",
		dataType : "json",
		contentType : "application/json;charset=utf-8",
		type : "POST",
		data : JSON.stringify(param),
		success : function(data) {
			

			var errorresponse =data.ResponseMessage
			document.getElementById("error").innerHTML=errorresponse;
			if(data.ResponseCode=="0"){
				
			}
			else{
				var form = document.forms[0];
				var paymentActionUrl = document.getElementById("PostUrl").value;
			    form.action = paymentActionUrl;
			    form.submit();
			}
			
			

		},
		error : function(data) {
			var form = document.forms[0];
			var paymentActionUrl = document.getElementById("PostUrl").value;
		    form.action = paymentActionUrl;
		    form.submit();
			window.location.reload();
		}
	});
	
	

	}
