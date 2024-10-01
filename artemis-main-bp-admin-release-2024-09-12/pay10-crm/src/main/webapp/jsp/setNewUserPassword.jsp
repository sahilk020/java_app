<%@ taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	<meta name="viewport"
		content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0" />
	<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
	<title>Set Password</title>
	<link rel="stylesheet" type="text/css" href="../css/material-icons.css" />
	<link rel="stylesheet" href="../css/material-font-awesome.min.css">
		<!-- CSS Files -->
		<link href="../css/material-dashboard.css?v=2.1.0" rel="stylesheet" />
		<!-- <link href="../css/default.css" rel="stylesheet" type="text/css" /> -->
		<script src="../js/core/jquery.min.js"></script>
		<script src="../js/jquery.minshowpop.js"></script>
		<script src="../js/jquery.formshowpop.js"></script>

		<style>
.body_color {
	background: #202F4B;
}
/* #wwctrl_newPassword span{
    top: -25px !important;
    left: 98px !important;
    position: absolute;
    
    z-index: 9;
  } */
.errorMessage {
	position: absolute;
	top: 181px;
}

.wwctrl span {
	top: -19px;
	left: 90px;
	position: absolute;
	/* opacity: 0; */
	/* display: none; */
	z-index: 3;
}
/* .signupbox {
    margin: 130px auto 0; */
</style>

		<script>
    $(document).ready(function(){
      $("#submit").click(function(e){
          e.preventDefault();
		var url_string = window.location.href;
		var url = new URL(url_string);
		var id = url.searchParams.get("id");
        var token  = document.getElementsByName("token")[0].value;
        var payId = document.getElementById("payId").value;
        var newPassword = document.getElementById("newPassword").value;
        var confirmNewPassword = document.getElementById("confirmNewPassword").value;
        $.ajax({type: "POST",
                url: "createPasswordAction",
                data: { "payId": payId,"id": id,"newPassword" : newPassword,"confirmNewPassword" : confirmNewPassword,"token":token,"struts.token.name": "token",},
                success:function(data){
                	var response = ((data["Invalid request"] != null) ? (data["Invalid request"].response[0]) : (data.response));
                	if(null!=response){
    					alert(response);			
    				}
                	if(data.errorCode == "314")
                		window.location.href = "index";
        }});
      });
    });
    </script>
		<script>
        $(document).ready(function(){
          
          var fields = {
              
              newPassword : {
                tooltip: "Password must be minimum 8 and <br> maximum 32 characters long, with <br> special characters (! @ , _ + / =) , <br> at least one uppercase and  one <br>lower case alphabet.",
                position: 'right',
                backgroundColor: "#6ad0f6",
                color: '#FFFFFF',
                },
              //color : {
                //tooltip: "This is for your cover color~~~ <a href='#'>here</a>"
                //},
              //text : {
                //tooltip: "Please provide your comment here."
                //}
              };
          
          //Include Global Color 
          $("#resetPassword").formtoolip(fields, { backgroundColor: "#000000" , color : "#FFFFFF", fontSize : 14, padding : 10, borderRadius :  5, zindex: 9, width : 71, display: "none"});
            
          });
  </script>
</head>
<body class="off-canvas-sidebar body_color">

	<div class="wrapper wrapper-full-page">
		<div class="page-header login-page header-filter" filter-color="black">

			<!--   you can change the color of the filter page using: data-color="blue | purple | green | orange | red | rose " -->
			<div class="container">
				<div class="row">
					<div class="col-lg-4 col-md-6 col-sm-8 ml-auto mr-auto">
						<s:form id="resetPassword" autocomplete="off">
							<s:token />
							<div class="card card-login card-hidden">
								<div class="card-header card-header-rose text-center">
									<h4 class="card-title">Set Your Password</h4>
									<div class="social-line">

										<a href="#" class="btn btn-just-icon btn-link btn-white">
											<i class="fa fa-linkedin"></i>
										</a>
									</div>
								</div>
								<s:hidden value="%{#session.payId}" name="payId" id="payId" />
								<s:hidden value="%{emailId}" name="emailId" id="emailId" />

								<div class="card-body ">

									<span class="bmd-form-group">
										<div class="input-group">
											<div class="input-group-prepend">
												<span class="input-group-text"> <i
													class="material-icons">lock_outline</i>
												</span>
											</div>
											<input type="password" name="newPassword"
												style='display: none'></input>
											<s:textfield placeholder="New Password " type="password"
												name="newPassword" id="newPassword" class="form-control"
												autocomplete="off" maxlength="32" />
										</div>
									</span> <span class="bmd-form-group">
										<div class="input-group">
											<div class="input-group-prepend">
												<span class="input-group-text"> <i
													class="material-icons">lock_outline</i>
												</span>
											</div>
											<s:textfield placeholder="Confirm Password " type="password"
												name="confirmNewPassword" id="confirmNewPassword"
												class="form-control" autocomplete="off" maxlength="32" />
										</div>
									</span>

								</div>
								<div class="card-footer justify-content-center">
									<input type="button" id="submit" value="Submit"
										class="btn btn-primary btn-round mt-4">
								</div>
							</div>
						</s:form>
					</div>
				</div>
			</div>

		</div>
	</div>
	<script src="../js/core/popper.min.js"></script>
	<script src="../js/core/bootstrap-material-design.min.js"></script>
	<script src="../js/plugins/perfect-scrollbar.jquery.min.js"></script>
	<!--  Google Maps Plugin    -->
	<!-- <script src="https://maps.googleapis.com/maps/api/js?key=YOUR_KEY_HERE"></script>  -->
	<!-- Chartist JS -->
	<script src="../js/plugins/chartist.min.js"></script>
	<!--  Notifications Plugin    -->
	<script src="../js/plugins/bootstrap-notify.js"></script>
	<!-- Control Center for Material Dashboard: parallax effects, scripts for the example pages etc -->
	<script src="../js/material-dashboard.js?v=2.1.0"
		type="text/javascript"></script>
	<!-- Material Dashboard DEMO methods, don't include it in your project! -->

	<script>
    $(document).ready(function() {
      $().ready(function() {
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

        $('.fixed-plugin a').click(function(event) {
          // Alex if we click on switch, stop propagation of the event, so the dropdown will not be hide, otherwise we set the  section active
          if ($(this).hasClass('switch-trigger')) {
            if (event.stopPropagation) {
              event.stopPropagation();
            } else if (window.event) {
              window.event.cancelBubble = true;
            }
          }
        });

        $('.fixed-plugin .active-color span').click(function() {
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

        $('.fixed-plugin .background-color .badge').click(function() {
          $(this).siblings().removeClass('active');
          $(this).addClass('active');

          var new_color = $(this).data('background-color');

          if ($sidebar.length != 0) {
            $sidebar.attr('data-background-color', new_color);
          }
        });

        $('.fixed-plugin .img-holder').click(function() {
          $full_page_background = $('.full-page-background');

          $(this).parent('li').siblings().removeClass('active');
          $(this).parent('li').addClass('active');


          var new_image = $(this).find("img").attr('src');

          if ($sidebar_img_container.length != 0 && $('.switch-sidebar-image input:checked').length != 0) {
            $sidebar_img_container.fadeOut('fast', function() {
              $sidebar_img_container.css('background-image', 'url("' + new_image + '")');
              $sidebar_img_container.fadeIn('fast');
            });
          }

          if ($full_page_background.length != 0 && $('.switch-sidebar-image input:checked').length != 0) {
            var new_image_full_page = $('.fixed-plugin li.active .img-holder').find('img').data('src');

            $full_page_background.fadeOut('fast', function() {
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

        $('.switch-sidebar-image input').change(function() {
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

        $('.switch-sidebar-mini input').change(function() {
          $body = $('body');

          $input = $(this);

          if (md.misc.sidebar_mini_active == true) {
            $('body').removeClass('sidebar-mini');
            md.misc.sidebar_mini_active = false;

            $('.sidebar .sidebar-wrapper, .main-panel').perfectScrollbar();

          } else {

            $('.sidebar .sidebar-wrapper, .main-panel').perfectScrollbar('destroy');

            setTimeout(function() {
              $('body').addClass('sidebar-mini');

              md.misc.sidebar_mini_active = true;
            }, 300);
          }

          // we simulate the window Resize so the charts will get updated in realtime.
          var simulateWindowResize = setInterval(function() {
            window.dispatchEvent(new Event('resize'));
          }, 180);

          // we stop the simulation of Window Resize after the animations are completed
          setTimeout(function() {
            clearInterval(simulateWindowResize);
          }, 1000);

        });
      });
    });
  </script>
	<script>
    $(document).ready(function() {
      md.checkFullPageBackgroundImage();
      setTimeout(function() {
        // after 1000 ms we add the class animated to the login/register card
        $('.card').removeClass('card-hidden');
      }, 700);
    });
  </script>


</body>
</html>