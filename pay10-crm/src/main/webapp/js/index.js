$(function() {
	$('#password').on('keypress', function(e) {
		if (e.which == 32){
			return false;
		}
	});
	$('#captchaOtp').on('keypress', function(e) {
		if (e.which == 32){
			return false;
		}
	});
});

function genrateOTP()
 {	
     $(".redvalid").hide();

   var emailId=document.getElementById("userLoginId").value;
   if(emailId == null || emailId == ""){
     //event.preventDefault();
     $('#modaltogenerateotp').modal('show');
	   //alert("Please enter email Id to generate OTP")
   }
   else{
	    document.getElementById("generateOtpBtn").classList.add("inactiveLink");
	        $.ajax({				
				    "url": "otpAction",
				    "type": "POST",
				    "data": {
						
						 "emailId":emailId,
					     "struts.token.name": "token",
					},
					        success:function(data){
									if(data.response == "Invalid User"){
                    document.getElementById("invaliduser").innerHTML=data.response;
                     $('#invalidusermodal').modal('show');
										//alert(data.response);
										document.getElementById("dataValue").style.color = "red";
										document.getElementById("dataValue").innerHTML=data.response;
										document.getElementById("generateOtpBtn").classList.remove("inactiveLink");
									}
									else{
                    document.getElementById("otpnumber").innerHTML=data.response;
                    $('#modaltosendOTP').modal('show');
										//alert(data.response);
										var timeleft = 30;
										var otpTimer = setInterval(function(){
										  document.getElementById("dataValue").style.color = "green";
										  document.getElementById("dataValue").style.display = "block";
										  document.getElementById("dataValue").innerHTML=data.response;
										  document.getElementById("otpMsg").innerHTML = "Next"+ ' ' + timeleft + " seconds remaining to regenerate OTP";
										  document.getElementById("otpMsg").style.display = "block";
										  document.getElementById("generateOtpBtn").classList.add("inactiveLink");
										  timeleft --;
										  if(timeleft <= 0){
											clearInterval(otpTimer);
											document.getElementById("otpMsg").style.display = "none";
											document.getElementById("generateOtpBtn").classList.remove("inactiveLink");
											document.getElementById("generateOtpBtn").innerHTML = "Resend OTP";
											document.getElementById("dataValue").style.display = "none";
										  }
										}, 1000);
									}	 
                                },
							error:function(data) {
								document.getElementById("dataValue").innerHTML=data.response;
						        document.getElementById("generateOtpBtn").classList.remove("inactiveLink");  
					            }
		        });
   }		
}

$(function() {
	$(document).ready(
			function() {	
				refresh();				
			}
		);
	function refresh (){
		img = document.getElementById("captchaImage");
		//img.src = "../Captcha.jpg/" + Math.random();
	}	
	  $('body').on('keydown', '#emailId', function(e) {
		    if (e.which === 32 &&  e.target.selectionStart === 0) {
		      return false;
		    }  
		  });

	  $('body').on('keydown', '#password', function(e) {
		    if (e.which === 32 &&  e.target.selectionStart === 0) {
		      return false;
		    }  
		  });
	  $('body').on('keydown', '#captcha', function(e) {
		    if (e.which === 32 &&  e.target.selectionStart === 0) {
		      return false;
		    }  
		  });
	  $("#Btnaccescd").click(function(){
		  //generateCaptcha();
		  refresh();
			
		});
});

	if (self == top) {
		var theBody = document.getElementsByTagName('body')[0];
		if (theBody != null) {
			theBody.style.display = "block";
		}
	} else {
		top.location = self.location;
	}

	$(document).ready(function() {
		$('.nav-tabs > li > a').click(function(event){
		event.preventDefault();//stop browser to take action for clicked anchor

		//get displaying tab content jQuery selector
		var active_tab_selector = $('.nav-tabs > li.active > a').attr('href');

		//find actived navigation and remove 'active' css
		var actived_nav = $('.nav-tabs > li.active');
		actived_nav.removeClass('active');

		//add 'active' css into clicked navigation
		$(this).parents('li').addClass('active');

		//hide displaying tab content
		$(active_tab_selector).removeClass('active');
		$(active_tab_selector).addClass('hide');

		//show target tab content
		var target_tab_selector = $(this).attr('href');
		$(target_tab_selector).removeClass('hide');
		$(target_tab_selector).addClass('active');
	     });
	  });
