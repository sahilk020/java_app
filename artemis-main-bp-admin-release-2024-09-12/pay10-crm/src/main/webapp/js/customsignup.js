
	function checkOtpValidation(event) {
		var emailIdVal = document.getElementById("emailId").value;
		var otpVal = document.getElementById("otp").value;
		//var otpCaptcha = document.getElementById("captcha").value;
		document.getElementById("emailError").style.display = "none";
		document.getElementById("otpError").style.display = "none";
	//	document.getElementById("enterCaptcha").style.display = "none";
		if (emailIdVal == null || emailIdVal == "" || !isValidEmail('emailId')) {
		  document.getElementById("emailError").style.display = "block";
		  event.preventDefault();
		} else if (otpVal == null || otpVal == "" || otpVal.length < 5) {
		  document.getElementById("otpError").style.display = "block";
		  event.preventDefault();
		} 
		// else if (otpCaptcha == null || otpCaptcha == "") {
		//   document.getElementById("enterCaptcha").style.display = "block";
		//   event.preventDefault();
		// } 
		else {
		  document.getElementById("emailError").style.display = "none";
		  document.getElementById("otpError").style.display = "none";
		  //document.getElementById("enterCaptcha").style.display = "none";
		}
	  }
  
			  $(document).ready(function () {
				  $().ready(function () {
					  $sidebar = $('.sidebar');
	  
					  $sidebar_img_container = $sidebar.find('.sidebar-background');
	  
					  $full_page = $('.full-page');
	  
					  $sidebar_responsive = $('body > .navbar-collapse');
	  
					  window_width = $(window).width();
	  
					  fixed_plugin_open = $('.sidebar .sidebar-wrapper .nav li.active a p').html();
	  
					  if (window_width > 767 && fixed_plugin_open == 'Dashboard') {
						  if ($('.fixed-plugin .dropdown').hasClass('show-dropdown')) {
							  $('.fixed-plugin .dropdown').addClass('open');
						  }
	  
					  }
	  
					  $('.fixed-plugin a').click(function (event) {
						  // Alex if we click on switch, stop propagation of the event, so the dropdown will not be hide, otherwise we set the  section active
						  if ($(this).hasClass('switch-trigger')) {
							  if (event.stopPropagation) {
								  event.stopPropagation();
							  } else if (window.event) {
								  window.event.cancelBubble = true;
							  }
						  }
					  });
	  
					  $('.fixed-plugin .active-color span').click(function () {
						  $full_page_background = $('.full-page-background');
	  
						  $(this).siblings().removeClass('active');
						  $(this).addClass('active');
	  
						  var new_color = $(this).data('color');
	  
						  if ($sidebar.length != 0) {
							  $sidebar.attr('data-color', new_color);
						  }
	  
						  if ($full_page.length != 0) {
							  $full_page.attr('filter-color', new_color);
						  }
	  
						  if ($sidebar_responsive.length != 0) {
							  $sidebar_responsive.attr('data-color', new_color);
						  }
					  });
	  
					  $('.fixed-plugin .background-color .badge').click(function () {
						  $(this).siblings().removeClass('active');
						  $(this).addClass('active');
	  
						  var new_color = $(this).data('background-color');
	  
						  if ($sidebar.length != 0) {
							  $sidebar.attr('data-background-color', new_color);
						  }
					  });
	  
					  $('.fixed-plugin .img-holder').click(function () {
						  $full_page_background = $('.full-page-background');
	  
						  $(this).parent('li').siblings().removeClass('active');
						  $(this).parent('li').addClass('active');
	  
	  
						  var new_image = $(this).find("img").attr('src');
	  
						  if ($sidebar_img_container.length != 0 && $('.switch-sidebar-image input:checked').length != 0) {
							  $sidebar_img_container.fadeOut('fast', function () {
								  $sidebar_img_container.css('background-image', 'url("' + new_image + '")');
								  $sidebar_img_container.fadeIn('fast');
							  });
						  }
	  
						  if ($full_page_background.length != 0 && $('.switch-sidebar-image input:checked').length != 0) {
							  var new_image_full_page = $('.fixed-plugin li.active .img-holder').find('img').data('src');
	  
							  $full_page_background.fadeOut('fast', function () {
								  $full_page_background.css('background-image', 'url("' + new_image_full_page + '")');
								  $full_page_background.fadeIn('fast');
							  });
						  }
	  
						  if ($('.switch-sidebar-image input:checked').length == 0) {
							  var new_image = $('.fixed-plugin li.active .img-holder').find("img").attr('src');
							  var new_image_full_page = $('.fixed-plugin li.active .img-holder').find('img').data('src');
	  
							  $sidebar_img_container.css('background-image', 'url("' + new_image + '")');
							  $full_page_background.css('background-image', 'url("' + new_image_full_page + '")');
						  }
	  
						  if ($sidebar_responsive.length != 0) {
							  $sidebar_responsive.css('background-image', 'url("' + new_image + '")');
						  }
					  });
	  
					  $('.switch-sidebar-image input').change(function () {
						  $full_page_background = $('.full-page-background');
	  
						  $input = $(this);
	  
						  if ($input.is(':checked')) {
							  if ($sidebar_img_container.length != 0) {
								  $sidebar_img_container.fadeIn('fast');
								  $sidebar.attr('data-image', '#');
							  }
	  
							  if ($full_page_background.length != 0) {
								  $full_page_background.fadeIn('fast');
								  $full_page.attr('data-image', '#');
							  }
	  
							  background_image = true;
						  } else {
							  if ($sidebar_img_container.length != 0) {
								  $sidebar.removeAttr('data-image');
								  $sidebar_img_container.fadeOut('fast');
							  }
	  
							  if ($full_page_background.length != 0) {
								  $full_page.removeAttr('data-image', '#');
								  $full_page_background.fadeOut('fast');
							  }
	  
							  background_image = false;
						  }
					  });
	  
					  $('.switch-sidebar-mini input').change(function () {
						  $body = $('body');
	  
						  $input = $(this);
	  
						  if (md.misc.sidebar_mini_active == true) {
							  $('body').removeClass('sidebar-mini');
							  md.misc.sidebar_mini_active = false;
	  
							  $('.sidebar .sidebar-wrapper, .main-panel').perfectScrollbar();
	  
						  } else {
	  
							  $('.sidebar .sidebar-wrapper, .main-panel').perfectScrollbar('destroy');
	  
							  setTimeout(function () {
								  $('body').addClass('sidebar-mini');
	  
								  md.misc.sidebar_mini_active = true;
							  }, 300);
						  }
	  
						  // we simulate the window Resize so the charts will get updated in realtime.
						  var simulateWindowResize = setInterval(function () {
							  window.dispatchEvent(new Event('resize'));
						  }, 180);
	  
						  // we stop the simulation of Window Resize after the animations are completed
						  setTimeout(function () {
							  clearInterval(simulateWindowResize);
						  }, 1000);
	  
					  });
				  });
			  });
	  
			  $(document).ready(function () {
				  md.checkFullPageBackgroundImage();
			  });
	  
			  if (self == top) {
				  var theBody = document.getElementsByTagName('body')[0];
				  theBody.style.display = "block";
			  } else {
				  top.location = self.location;
			  }
	  
			  $(document).ready(function () {
	  
				  var fields = {
	  
					  password: {
						  tooltip: "Password must be minimum 8 and <br> maximum 32 characters long, with <br> special characters (! @ , _ + / =) , <br> at least one uppercase, one <br>lower case alphabet and one <br>numeric number.",
						  position: 'right',
						  backgroundColor: "#6ad0f6",
						  color: '#FFFFFF'
					  },
					  //confirmPassword : {
					  //tooltip: "Password must be minimum 8 and <br> maximum 32 characters long, with <br> special characters (! @ , //_ + / =) , <br> at least one uppercase, one <br>lower case alphabet and one <br>numeric number.",
					  //position: 'right',
					  //backgroundColor: "#6ad0f6",
					  //color: '#FFFFFF'
					  //}
				  };
	  
				  //Include Global Color 
				  $("#formname").formtoolip(fields, { backgroundColor: "#000000", color: "#FFFFFF", fontSize: 14, padding: 10, borderRadius: 5 });
	  
			  });
		  
			  var industryCategory = document.getElementById('industryCategory'),
				  errorCategory = document.getElementsByClassName('errorCategory')[0],
				  businessName = document.getElementById('businessName'),
				  errorBusninessName = document.getElementsByClassName('errorBusninessName')[0],
				  emailId = document.getElementById('emailId'),
				  errorEmail = document.getElementsByClassName('errorEmail')[0],
				  mobileRegex = /^[6789]\d{9}/,
				  mobile = document.getElementById('mobile'),
				  errorPhone = document.getElementsByClassName('errorPhone')[0],
				  passwordRejex = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!@,_+/=]).{8,32}$/g,
				  password = document.getElementById('password'),
				  errorPassword = document.getElementsByClassName('errorPassword')[0],
				  confirmPassword = document.getElementById('confirmPassword'),
				  passwordNotMatch = document.getElementsByClassName('passwordNotMatch')[0];
				//   errorCaptcha = document.getElementsByClassName('errorCaptcha')[0],
				//   errorEmptyCaptcha = document.getElementsByClassName('errorEmptyCaptcha')[0],
				//   captcha = document.getElementById('captcha');
	  
	  
			  function validateMyForm(e) {
				  //showSubcategory();
				  var checkCategory = false,
					  checkBusinessName = false,
					  checkEmailId = false,
					  checkPhone = false,
					  checkPassword = false,
					  checkConfirmPass = false,
					  mobileVal = mobile.value,
					  passwordVal = password.value,
					  userType = document.getElementById('userRoleType').value;
	  
				  if (userType == "merchant") {
					  if (!industryCategory.value) {
						  errorCategory.style.display = "block";
						  checkCategory = false;
						  e.preventDefault();
					  } else {
						  errorCategory.style.display = "none";
						  checkCategory = true;
					  }
				  }
	  
				  if (!businessName.value || businessName.value.length < 2) {
					  errorBusninessName.style.display = "block";
					  checkBusinessName = false;
					  e.preventDefault();
				  } else {
					  errorBusninessName.style.display = "none";
					  checkBusinessName = true;
				  }
	  
				  if (!emailId.value || emailId.value.length < 6 || !isValidEmail('emailId')) {
					  errorEmail.style.display = "block";
					  checkEmailId = false;
					  e.preventDefault();
				  } else {
					  errorEmail.style.display = "none";
					  checkEmailId = true;
				  }
	  
				  if (!mobileVal.match(mobileRegex)) {
					  errorPhone.style.display = "block";
					  checkPhone = false;
					  e.preventDefault();
				  } else {
					  errorPhone.style.display = "none";
					  checkPhone = true;
				  }
	  
				  if (!passwordVal.match(passwordRejex)) {
					  errorPassword.style.display = "block";
					  checkPassword = false;
					  e.preventDefault();
				  } else {
					  errorPassword.style.display = "none";
					  checkPassword = true;
				  }
	  
				  if (password.value) {
					  if (password.value != confirmPassword.value || !confirmPassword.value) {
						  passwordNotMatch.style.display = "block";
						  checkConfirmPass = false;
						  e.preventDefault();
					  } else {
						  passwordNotMatch.style.display = "none";
						  checkConfirmPass = true;
					  }
				  }
	  
			// 	  if(captcha.value.length>=1 && captcha.value.length<6){
			// 	  errorCaptcha.style.display = "block";
			// 	  errorEmptyCaptcha.style.display = "none";
			// 	  e.preventDefault();
			//   }else if(captcha.value.length==6){
			// 	  errorEmptyCaptcha.style.display = "none";
			// 	  errorCaptcha.style.display = "none";
			//   }else{
			// 	  if(captcha.value.length == 0){
			// 		  errorEmptyCaptcha.style.display = "block";
			// 		  e.preventDefault();
			// 	  }
			// 	  errorCaptcha.style.display = "none";
			//   }
	  
				  /*if(checkCategory && checkBusinessName && checkEmailId && checkPhone && checkPassword && checkConfirmPass){
					  document.getElementById('formname').submit();	
				  }*/
	  
	  
			  }
	  
			  function categoryChange() {
	  
				  if (industryCategory.value) {
	  
					  errorCategory.style.display = "none";
				  } else {
					  errorCategory.style.display = "block";
	  
				  }
			  }
			  function categoryOnBlur(){
				  if(!industryCategory.value || industryCategory.value == "")
				  {
					  errorCategory.style.display = "block"	
				  }else{
					  errorCategory.style.display = "none";
				  }
			  }
			  function businessNameonBlur() {
				  if (!businessName.value || businessName.value.length < 2) {
					  errorBusninessName.style.display = "block";
				  } else {
					  errorBusninessName.style.display = "none";
				  }
			  }
	  
			  function emailOnBlur() {
				  if (!emailId.value || emailId.value.length < 6 || !isValidEmail('emailId')) {
					  errorEmail.style.display = "block";
				  } else {
					  errorEmail.style.display = "none";
				  }
			  }
	  
			  function phoneNoOnBlur(e) {
				  var mobileVal = document.getElementById("mobile").value;
				  if (!mobileVal.match(mobileRegex)) {
					  errorPhone.style.display = "block";
					  checkPhone = false;
					  e.preventDefault();
				  } else {
					  errorPhone.style.display = "none";
					  checkPhone = true;
				  }
			  }
	  
			  function passwordOnBlur() {
				  var passwordVal = password.value;
				  if (!passwordVal.match(passwordRejex)) {
					  errorPassword.style.display = "block";
				  } else {
					  errorPassword.style.display = "none";
				  }
			  }
	  
			  function confirmPasswordBlur() {
				  if (password.value) {
					  if (password.value != confirmPassword.value || !confirmPassword.value) {
						  passwordNotMatch.style.display = "block";
					  } else {
						  passwordNotMatch.style.display = "none";
					  }
				  }
			  }
			  function emailCheck(){
		  var emailReg = /^[A-Za-z0-9+_.-]+@(.+)$/;
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
  
//   function captchaOnBlur(){
// 	  $('#wwerr_captcha').remove();
// 	  if(captcha.value.length>=1 && captcha.value.length<6 ){
// 		  errorCaptcha.style.display = "block";
// 		  errorEmptyCaptcha.style.display = "none";		
// 	  }else if(captcha.value.length==6){
// 		  errorEmptyCaptcha.style.display = "none";
// 		  errorCaptcha.style.display = "none";
// 	  }else{
// 		  if(captcha.value.length == 0){
// 			  errorEmptyCaptcha.style.display = "block";
// 		  }
// 		  errorCaptcha.style.display = "none";
// 	  }
//   }
  
  function isNumberKey(evt){
	  var charCode = (evt.which) ? evt.which : event.keyCode
	  if (charCode > 31 && (charCode < 48 || charCode > 57))
		  return false;
  }
  $("input").on("keypress", function(e) {
	  if (e.which === 32 && !this.value.length)
		  e.preventDefault();
  });
  
	  
  