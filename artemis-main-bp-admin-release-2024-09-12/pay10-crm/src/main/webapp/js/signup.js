
/* var mobAuto = document.getElementById("mobile").value;
var passwordAuto = document.getElementById("password").value;
if(mobAuto !== "" || passwordAuto !== ""){
	document.getElementById("mobile").value = "";
	document.getElementById("password").value = "";
}
 */

// function refresh() {
	

// 	document.getElementById('captcha').value = '';

// }
$(function() {
	
	$('#emailId').on('keypress', function(e) {
		if (e.which == 32){
			return false;
		}
	});
	$('#password').on('keypress', function(e) {
		if (e.which == 32){
			return false;
		}
	});
	$('#confirmPassword').on('keypress', function(e) {
		if (e.which == 32){
			return false;
		}
	});
	$('#otp').on('keypress', function(e) {
		if (e.which == 32){
			return false;
		}
	});
	// $('#captcha').on('keypress', function(e) {
	// 	if (e.which == 32){
	// 		return false;
	// 	}
	// });
	// $("#Btnaccescd").click(function(){
		
	// 	refresh();
		  
	//   });
	  $('#mobile').keypress(function(e){ 
		if (this.value.length == 0 && e.which == 48 ){
		   return false;
		}
	 });
	 
});
 
$(document).ready(function () {
    $('#mobile').attr('autocomplete', 'false');
	$('#password').attr('autocomplete', 'false');
});




$(function() {
	
		$(document).ready(
			function() {	
				//refresh();	
showSubcategory();	
			document.getElementById("otpError").style.display = "none";
			}
		);
	// function refresh (){
	// 	img = document.getElementById("captchaImage");
	// 	img.src = "../Captcha.jpg/" + Math.random();
	// }	  
		//    $("#Btnaccescd").click(function(){
		// 	refresh();
		// });
	});


$(function() {
	
	if ($("#userRoleType").val() == "merchant") {
		$("#tdIndustryType").show();
		var industry = document.getElementById("industryCategory").value;
		if(industry){
			$("#subcategorydiv").show();
			
		}else{
			$(".errorCategory").hide();
		}
	
	}
	else {
		$("#tdIndustryType").hide();
		$("#subcategorydiv").hide();
	}
	
	
	$("#userRoleType").change(function() {
		if ($(this).val() == "merchant") {
			$("#tdIndustryType").show();
		var category = document.getElementById("industryCategory").value;
		
		if(category){
			$("#subcategorydiv").show();
		}else{
			$(".errorCategory").hide();
			
		}
		
		} 
		else {
			$("#tdIndustryType").hide();
			$("#subcategorydiv").hide();
			$('.errorCategory').hide();
		}
	});

	$("#industryCategory").change(function() {
		var industry = this.value;
		var token  = document.getElementsByName("token")[0].value;
		if(!industry){
			$("#subcategorydiv").hide();
			var subCategoryText = document.getElementById("subcategory");
            var subcategory = document.getElementById("subcategory").value;			
			subCategoryText.value = "";
			return false;
		}
			
		$.ajax({
			type : "POST",
			url : "industrySubCategory",
			data : {
				industryCategory : industry,
				token:token,
				"struts.token.name": "token"
			},
			success : function(data, status) {
				var subCategoryListObj = data.subCategories;
				var subCategoryList = subCategoryListObj[0].split(',');
				var radioDiv = document.getElementById("radiodiv");
				radioDiv.innerHTML = "";
				for(var i=0; i<subCategoryList.length; i++){
					var subcategory = subCategoryList[i];
					var radioOption = document.createElement("INPUT");
					radioOption.setAttribute("type", "radio");
					radioOption.setAttribute("value", subcategory);
					radioOption.setAttribute("name", "subcategory");
					var labelS = document.createElement("SPAN");
					labelS.innerHTML = subcategory;
					radioDiv.appendChild(radioOption);
					radioDiv.appendChild(labelS);
				}
				$('#popup').popup({
						'blur':false,
						'escape':false
						}
					).popup('show');
			},
			error : function(status) {
				return false;
				//alert("Please Select Category");
			}
		});
	});
});


function genrateOTP()
 {
	$(".redvalid").hide();
   var mobile=document.getElementById("mobile").value.trim();
   if(mobile == null || mobile == ""){
	   event.preventDefault();
	   $("#modaltogenerateotpSignUp").modal('show');
	  // alert("Please enter mobile number to generate OTP");
	   return false;
   }
   else{
	   document.getElementById("generateOtpBtn").classList.add("inactiveLink");
	   $.ajax({				
				    "url": "generateSignupOTP",
				    "type": "POST",
				    "data": {
						
						 "mobile":mobile,
					     "struts.token.name": "token",
					},
					        success:function(data){
									if(data.response == "Invalid User"){
										document.getElementById("invaliduser").innerHTML=data.response;
                                       $('#invaliduserSignUpmodal').modal('show');
										//alert(data.response);
										document.getElementById("dataValue").innerHTML=data.response;
										document.getElementById("generateOtpBtn").classList.remove("inactiveLink");
									}
									else{
										document.getElementById("otpnumber").innerHTML=data.response;
                                         $('#modaltosendOTPSignUp').modal('show');
										//alert(data.response);
										var timeleft = 30;
										var otpTimer = setInterval(function(){
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


function loadSubcategory(){
	var industry = document.getElementById("industryCategory").value;
	var token  = document.getElementsByName("token")[0].value;
	
	$.ajax({
		type : "POST",
		url : "industrySubCategory",
		data : {
			industryCategory : industry,
			token:token,
			"struts.token.name": "token"
		},
		success : function(data, status) {
			var subCategoryListObj = data.subCategories;
			var subCategoryList = subCategoryListObj[0].split(',');
			var radioDiv = document.getElementById("radiodiv");
			radioDiv.innerHTML = "";
			for(var i=0; i<subCategoryList.length; i++){
				var subcategory = subCategoryList[i];
				var radioOption = document.createElement("INPUT");
				radioOption.setAttribute("type", "radio");
				radioOption.setAttribute("value", subcategory);
				radioOption.setAttribute("name", "subcategory");
				var labelS = document.createElement("SPAN");
				labelS.innerHTML = subcategory;
				radioDiv.appendChild(radioOption);
				radioDiv.appendChild(labelS);
			}
			
		},
		error : function(status) {
		}
	});
}

function selectSubcategory(){
	var checkedRadio = $('input[name="subcategory"]:checked').val();
	if(null==checkedRadio){
		document.getElementById("radioError").innerHTML = "Please select a subcategory";
		return false;
	}
	document.getElementById("radioError").innerHTML = "";
	var subCategoryDiv = document.getElementById("subcategorydiv");
	var subCategoryText = document.getElementById("subcategory");
    var subcategoryDiv= document.getElementById("subcategorydiv");
	subCategoryText.value = checkedRadio;
	subCategoryDiv.style.display = "block";
	$('#popup').popup('hide');
	//validation for required field
}
function showSubcategory(){
	var subcategory = document.getElementById("subcategory").value;
		var subcategoryDiv= document.getElementById("subcategorydiv");
	if(subcategory==''||subcategory== null){

subcategoryDiv.style.display = "none";
	}
	else{		
	subcategoryDiv.style.display = "block";
	
	}
}

$(document).ready(function(){
	loadSubcategory();
	$("#subcategory").click(function() {
	$('#popup').popup({
						'blur':false,
						'escape':false
						}
					).popup('show');
	});
});

