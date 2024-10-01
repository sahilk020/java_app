<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">

<title>Change Password</title>
<link rel="stylesheet" type="text/css" href="https://fonts.googleapis.com/css?family=Roboto:300,400,500,700|Roboto+Slab:400,700|Material+Icons" />
  <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/latest/css/font-awesome.min.css">
  <!-- CSS Files -->
  <link href="../css/material-dashboard.css?v=2.1.0" rel="stylesheet" />
<link href="../css/default.css" rel="stylesheet" type="text/css" />

<script src="../js/jquery.minshowpop.js"></script>
<script src="../js/jquery.formshowpop.js"></script>
<script src="../js/commanValidate.js"></script> 
<script src="../js/loader/modernizr-2.6.2.min.js"></script>
<script src="../js/core/popper.min.js"></script>
<script src="../js/core/bootstrap-material-design.min.js"></script>
<script src="../js/plugins/perfect-scrollbar.jquery.min.js"></script>
<!-- Control Center for Material Dashboard: parallax effects, scripts for the example pages etc -->
<script src="../js/material-dashboard.js?v=2.1.0" type="text/javascript"></script>

<style type="text/css">
/* Manual css s:actionmessage for this page */
.errorMessage {
	font: normal 11px arial;
	color: #ff0000;
	display: block;
	margin: -15px 0px 3px 0px;
	padding: 0px 0px 0px 0px;
}

.error2 {
	color: red;
}
#mismatchmsg{
	font-size: 16px;
    color: #496cb6;
    text-align: center;
    font-weight: 500;
}
.none{
	display: none !important;
}
body{
	background: none !important;
}
#loading {width: 100%;height: 100%;top: 0px;left: 0px;position: fixed;display: block; z-index: 99}
#loading-image {position: absolute;top: 40%;left: 55%;z-index: 100; width:10%;} 

#loadingInner {width: 100%;height: 100%;top: 0px;left: 0px;position: fixed;display: block; z-index: 99}
#loading-image-inner {position: absolute;top: 33%;left: 48%;z-index: 100; width:5%;} 


/* #loader-wrapper{
	display: none;
} */
</style>
<script>
  $(document).ready(function(){    
	document.getElementById("loadingInner").style.display = "none"; 	 
      $("#submit").click(function(e){    	 
          e.preventDefault();
		  
		  	 $.ajaxSetup({
 	            global: false,
 	            beforeSend: function () {
 	            	toggleAjaxLoader();
 	            },
 	            complete: function () {
 	            	toggleAjaxLoader();
 	            }
			 });
			 document.getElementById("loadingInner").style.display = "block";
			 $('#loader').addClass('block');
  $.ajax({type: "POST",

		url : 'changeExpirePassword',
		
		data : {
			token : document.getElementsByName("token")[0].value,
			oldPassword : document.getElementById('oldPassword').value,
			newPassword : document.getElementById('newPassword').value,
			confirmNewPassword : document.getElementById('confirmNewPassword').value,
			emailId :'${emailId}'
		},
		success : function(data){
			
			var responsedata= data.response;
			var jsonObj=data["Invalid request"];
			
			if(jsonObj!=null){
			var oldpass=	jsonObj['oldPassword'];
			var newpass= jsonObj['newPassword'];
			var confirmpass= jsonObj['confirmNewPassword'];
			
			if(oldpass!=null){
			document.getElementById("erroroldpass").innerHTML=oldpass;
		
			}
				if(newpass!=null){
			document.getElementById("errornewpass").innerHTML=newpass;
			
			}
			if(confirmpass!=null){
			document.getElementById("errorconfirmpass").innerHTML=confirmpass;
			
			} 
			if(newPassword.value!=confirmNewPassword.value || confirmNewPassword.value!=newPassword.value){
				$('#mismatchpassword').modal('show');
				$('#loader').addClass('none');
				document.getElementById("loadingInner").style.display = "none";
				oldPassword.value = null;
			newPassword.value = null;
			confirmNewPassword.value = null;
				
				$('#loader').addClass('none');
				document.getElementById("loadingInner").style.display = "none";
			}
			
		}
		
			
			if(responsedata){
				if(responsedata == "Password has been successfully changed, login to continue"){
					document.getElementById("invalidvalue").innerHTML=data.response;
					 $('#invalidValues').modal('show');
					 $('#loader').addClass('none');
					 document.getElementById("loadingInner").style.display = "none";
				//alert(responsedata);
				fields = "";
				oldPassword.value = null;
			newPassword.value = null;
			confirmNewPassword.value = null;
			setTimeout(function() {
				window.location = "loginResult";
			}, 300)
				
			}
				fields = "";
				oldPassword.value = null;
			newPassword.value = null;
			confirmNewPassword.value = null;
				document.getElementById("errorforwronginput").innerHTML=data.response;
					$('#modaltosendErrorMsg').modal('show');
					$('#loader').addClass('none');
					document.getElementById("loadingInner").style.display = "none";
				//alert(responsedata);
				
			}
			
			else if(newPassword.value == 0  || confirmNewPassword.value == 0 || oldPassword.value == 0){
				$('#nullNewConfirPassword').modal('show');
				$('#loader').addClass('none');
				document.getElementById("loadingInner").style.display = "none";
				oldPassword.value = null;
			newPassword.value = null;
			confirmNewPassword.value = null;
				fields = "";
			} else if(oldPassword.value!=oldpass.value){
				$('#mismatchpassword').modal('show');
				document.getElementById("loadingInner").style.display = "none";
				oldPassword.value = null;
			newPassword.value = null;
			confirmNewPassword.value = null;
				fields = "";
			} 
		},
		error : function(data){
			alert('Something went wrong!');
			document.getElementById("loadingInner").style.display = "none";
		}
	});
	});
});
</script>
<script>
	 $(document).ready(function() {
        $(document).on('keyup', '#oldPassword', function(e){
         return !(e.keyCode == 32);
      });
	  $(document).on('keyup', '#newPassword', function(e){
         return !(e.keyCode == 32);
      });
	  $(document).on('keyup', '#confirmNewPassword', function(e){
         return !(e.keyCode == 32);
      });
    });
</script>

	 <script type="text/javascript">
        $(document).ready(function() {
            $('#submit').attr("disabled", true);
			var fields = "#oldPassword, #newPassword, #confirmNewPassword";

$(fields).on('keyup', function() {
    if (allFilled()) {
        $('#submit').removeAttr('disabled');
    } else {
        $('#submit').attr('disabled', 'disabled');
    }
});

function allFilled() {
    var filled = true;
    $(fields).each(function() {
        if ($(this).val() == '') {
            filled = false;
        }
    });
    return filled;
}
        });
		</script>
		<script>
		function refresh(){
			oldPassword.value = null;
			newPassword.value = null;
			confirmNewPassword.value = null;

		}
		</script>






<!-- <div id="loading" style="text-align: center;">
		<img id="loading-image" style="width:70px;height:70px;" src="../image/sand-clock-loader.gif" alt="Sending SMS..." />
	</div> -->
	<div id="loader-wrapper" style="width: 100%; height: 100%; display:none;">
		<div id="loader"></div>
	  </div>
	  <div id="loadingInner" display="none">
		  <img id="loading-image-inner" src="../image/sand-clock-loader.gif"
			  alt="BUSY..." />
			  
	  </div>
<div class="container">
	<div class="row" style="margin-top: 20px;">
		<center style="width: 100%;"><h2>Your password has expired. Please create new password to continue.</h2></center>
		<em><strong>Password
			Criteria:</strong> Password must be minimum 8 and maximum 32 characters
			long, with special characters (! @ , _ + / =) , at least one
			upper case and one lower case alphabet and at least one numeric digit. Your new password must
			not be the same as any of your last four passwords.</em>
	  <div class="col-lg-4 col-md-6 col-sm-8 ml-auto mr-auto">
		  <s:form id="resetPassword"  autocomplete="off">
					  
		  <div class="card card-login card-hidden">
			<div class="card-header card-header-rose text-center">
			  <h4 class="card-title">Change Password</h4>
			  <s:actionmessage class="success success-text" theme="simple"/>
			  <!-- <div class="social-line">
			   
				<a href="https://www.pay10.com/#" class="btn btn-just-icon btn-link btn-white">
				  <i class="fa fa-linkedin"></i>
				</a>
			  </div> -->
			</div>
			

			<div class="card-body ">
			 
			  <span class="bmd-form-group">
				<div class="input-group">
				  <div class="input-group-prepend">
					  <span class="input-group-text">
						  <i class="material-icons">lock_outline</i>
						</span> 
				  </div>
				  <span id="errorchangepass" style="font-size: 11px;
				  color: red;
				  position: absolute;
				  top: 29px;"></span>
				  
				  <s:textfield name="oldPassword" id ="oldPassword" placeholder="Old Password" type="password" autocomplete="off" 
				  class="form-control" /><span  class="error2" id="erroroldpass"></span>
				</div>
				
			  </span>
			  <span class="bmd-form-group">
				<div class="input-group">
				  <div class="input-group-prepend">
					  <span class="input-group-text">
						  <i class="material-icons">lock_outline</i>
						</span>
				  </div>
				  <s:textfield name="newPassword" id="newPassword" type="password" placeholder="New Password" autocomplete="off"
				  class="form-control" /><span  class="error2"  id="errornewpass"></span>
				</div>
				
			  </span>
			  <span class="bmd-form-group">
				<div class="input-group">
				  <div class="input-group-prepend">
					  <span class="input-group-text">
						  <i class="material-icons">lock_outline</i>
						</span>
				  </div>
				  <s:textfield name="confirmNewPassword" id="confirmNewPassword" placeholder="Confirm New Password" type="password" autocomplete="off"
													class="form-control" /><span class="error2" id="errorconfirmpass"></span>
				</div>
			  </span>
			  
			</div>
			<div class="card-footer justify-content-center">
				<s:submit
				id="submit" value="Submit"
				class="btn btn-primary btn-round mt-4"
				></s:submit>
				
			</div>
		  </div>
		  <s:token />
		</s:form>
	  </div>
	</div>
  </div>
 <!--Start Modal for Invalid user alert-->
 <div class="modal fade" id="invalidValues" role="dialog">
    <div class="modal-dialog">

      <!-- Modal content-->
      <div class="modal-content">
        <div class="modal-header">

        </div>
        <div class="modal-body">
          <p class="enter_otp" id="invalidvalue"></p>
        </div>
        <div class="modal-footer" id="modal_footer">
          <button type="button" class="btn btn-primary btn-round mt-4 submit_btn" data-dismiss="modal">Ok</button>
        </div>
      </div>

    </div>
  </div>
  <!--End Modal to Invalid user alert-->
<!--Start Modal to generate Otp error-->
<div class="modal fade" id="modaltosendErrorMsg" role="dialog">
    <div class="modal-dialog">

      <!-- Modal content-->
      <div class="modal-content">
        <div class="modal-header">

        </div>
        <div class="modal-body">
          <p class="enter_otp" id="errorforwronginput"></p>
        </div>
        <div class="modal-footer" id="modal_footer">
          <button type="button" class="btn btn-primary btn-round mt-4 submit_btn" data-dismiss="modal">Ok</button>
        </div>
      </div>

    </div>
  </div>
  <!--End Modal to generate Otp error-->
   <!--Start Modal to generate Otp error-->
   <div class="modal fade" id="mismatchpassword" role="dialog">
    <div class="modal-dialog">

      <!-- Modal content-->
      <div class="modal-content">
        <div class="modal-header">

        </div>
        <div class="modal-body">
          <p id="mismatchmsg">Please enter valid password.</p>
        </div>
        <div class="modal-footer" id="modal_footer">
          <button type="button" class="btn btn-primary btn-round mt-4 submit_btn" onclick="refresh()" data-dismiss="modal">Ok</button>
        </div>
      </div>

    </div>
  </div>
  <!--End Modal to generate Otp error-->
  <div class="modal fade" id="nullNewConfirPassword" role="dialog">
    <div class="modal-dialog">

      <!-- Modal content-->
      <div class="modal-content">
        <div class="modal-header">

        </div>
        <div class="modal-body">
          <p id="mismatchmsg">Please Enter all Mandotory Fields</p>
        </div>
        <div class="modal-footer" id="modal_footer">
          <button type="button" class="btn btn-primary btn-round mt-4 submit_btn" onclick="refresh()" data-dismiss="modal">Ok</button>
        </div>
      </div>

    </div>
  </div>

  <!-- <div class="modal fade" id="oldPassMismatch" role="dialog">
    <div class="modal-dialog">

   
      <div class="modal-content">
        <div class="modal-header">

        </div>
        <div class="modal-body">
          <p id="mismatchmsg">Please Enter the Valid Old Password</p>
        </div>
        <div class="modal-footer" id="modal_footer">
          <button type="button" class="btn btn-primary btn-round mt-4 submit_btn" onclick="refresh()" data-dismiss="modal">Ok</button>
        </div>
      </div>

    </div>
  </div> -->
  


</head>
<body>

</body>
</html>