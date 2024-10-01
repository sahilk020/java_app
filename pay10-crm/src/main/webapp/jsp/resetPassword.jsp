<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
    <title>Reset Password</title>
    <link rel="icon" href="../image/98x98.png">
    <link rel="canonical" href="https://keenthemes.com/metronic" />
    <!--begin::Fonts-->
    <link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Poppins:300,400,500,600,700" />
    <!--end::Fonts-->
    <!--begin::Page Custom Styles(used by this page)-->
    <link href="../assets/css/pages/login/classic/login-4.css" rel="stylesheet" type="text/css" />
    <!--end::Page Custom Styles-->
    <!--begin::Global Theme Styles(used by all pages)-->
    <link href="../assets/plugins/global/plugins.bundle.css" rel="stylesheet" type="text/css" />
    <link href="../assets/plugins/custom/prismjs/prismjs.bundle.css" rel="stylesheet" type="text/css" />
    <link href="../assets/css/style.bundle.css" rel="stylesheet" type="text/css" />
    <!-- <link href="../fonts/css/font-awesome.min.css" rel="stylesheet">

          <link rel="stylesheet" href="../css/material-font-awesome.min.css">


    <link rel="stylesheet" type="text/css"
      href="../css/material-icons.css" />


      <link href="../css/material-dashboard.css?v=2.1.0" rel="stylesheet" /> -->
    <!-- <link href="../css/default.css" rel="stylesheet" type="text/css" /> -->
    <script src="../js/core/jquery.min.js"></script>
    <script src="../js/jquery.minshowpop.js"></script>
    <script src="../js/jquery.formshowpop.js"></script>
     <script src="../js/aes.js"></script>

    <style>
        /* .body_color{
          background: url(../image/back.jpg) top center no-repeat;
        } */
        /* #wwctrl_newPassword span{
          top: -25px !important;
          left: 98px !important;
          position: absolute;

          z-index: 9;
        } */
        .errorMessage{
            position: absolute;
            top: 181px;
        }
        .wwctrl span{
            top: -19px ;
            left: 90px;
            position: absolute;
            /* opacity: 0; */
            /* display: none; */
            z-index: 3;
        }
        #loading {width: 100%;height: 100%;top: 0px;left: 0px;position: fixed;display: block; z-index: 99}
        #loading-image {position: absolute;top: 40%;left: 55%;z-index: 100; width:10%;}

        #loadingInner {width: 100%;height: 100%;top: 0px;left: 0px;position: fixed;display: block; z-index: 99}
        #loading-image-inner {position: absolute;top: 33%;left: 48%;z-index: 100; width:5%;}
        /* .signupbox {
            margin: 130px auto 0; */
    </style>

    <script>
        $(document).ready(function(){
            document.getElementById("loadingInner").style.display = "none";
            $("#submit").click(function(e){
                e.preventDefault();
                var url_string = window.location.href;
                var url = new URL(url_string);
                var id = url.searchParams.get("id");
                var token  = document.getElementsByName("token")[0].value;
                var payId = document.getElementById("payId").value;
                //var newPassword = document.getElementById("newPassword").value;
                //var confirmNewPassword = document.getElementById("confirmNewPassword").value;
                //document.getElementById("loading").style.display = "block";
                document.getElementById("loadingInner").style.display = "block";
                $('#submit').attr("disabled", true);

                var newP=$('#newPassword').val();
    			var conP=$('#confirmNewPassword').val();
    		
    			var newPass=CryptoJS.AES.encrypt(newP, 'thisisaverysecretkey');
    				
    			var confirmNewPass=CryptoJS.AES.encrypt(conP, 'thisisaverysecretkey');
    			//console.log(confirmNewPass.toString());
    			
    			newPass=document.getElementById('newPassword').value=newPass;
    			confirmNewPass=document.getElementById('confirmNewPassword').value=confirmNewPass; 

    			newPass=newPass.toString();
    			confirmNewPass=confirmNewPass.toString();

                $.ajax({type: "POST",
                    url: "resetPasswordAction",
                    data: { "payId": payId,"id": id,"newPassword" : newPass,"confirmNewPassword" : confirmNewPass,"token":token,"struts.token.name": "token",},
                    success:function(data){
                        // document.getElementById("loading").style.display = "none";
                        document.getElementById("loadingInner").style.display = "none";
                        $('#submit').attr("disabled", false);
                        var response = ((data["Invalid request"] != null) ? (data["Invalid request"].response[0]) : (data.response));
                        if(null!=response){
                            alert(response);
                        }
                        if(data.errorCode == "314")
                            window.location.href = "index";
                        //document.getElementById("loading").style.display = "none";
                        document.getElementById("loadingInner").style.display = "none";
                        $('#submit').attr("disabled", false);
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
<body  class="off-canvas-sidebar body_color" >

<div id="loader-wrapper" style="width: 100%; height: 100%; display:none;">
    <div id="loader"></div>
</div>
<div id="loadingInner" display="none">
    <img id="loading-image-inner" src="../image/sand-clock-loader.gif"
         alt="BUSY..." />
</div>
<div class="wrapper wrapper-full-page">
    <div class="page-header login-page header-filter" filter-color="black" >

        <!--   you can change the color of the filter page using: data-color="blue | purple | green | orange | red | rose " -->
        <!-- <div class="container">
          <div class="row">
            <div class="col-lg-4 col-md-6 col-sm-8 ml-auto mr-auto"> -->
        <div class="d-flex flex-column flex-root">
            <!--begin::Login-->
            <div class="login login-4 login-signin-on d-flex flex-row-fluid" id="kt_login">
                <div class="d-flex flex-center flex-row-fluid bgi-size-cover bgi-position-top bgi-no-repeat" style="background-image: url('../assets/media/bg/bg-3.jpg');">
                    <div class="login-form text-center p-7 position-relative overflow-hidden">
                        <s:form id="resetPassword"  autocomplete="off">
                            <s:token/>
                            <!-- <div class="card card-login card-hidden"> -->
                            <div class="d-flex flex-center mb-15">
                                <a href="#">
                                    <img src="../image/Pay10_Logo.png"  style="width: 440px;" class="max-h-75px" alt="" />
                                </a>
                            </div>
                            <div class="mb-20">
                                <h3>Reset Password</h3>
                                <!-- <div class="text-muted font-weight-bold">Enter your details to create your account</div> -->
                            </div>
                            <s:hidden  value="%{#session.payId}" name="payId" id="payId" />
                            <s:hidden  value="%{emailId}" name="emailId" id="emailId" />

                            <!-- <div class="card-body "> -->

                            <!-- <span class="bmd-form-group"> -->
                            <div class="form-group mb-5">
                                <!-- <div class="input-group-prepend">
                                    <span class="input-group-text">
                                        <i class="material-icons">lock_outline</i>
                                      </span>
                                </div> -->
                                <input type="password" name="newPassword" style='display: none'></input>
                                <s:textfield  placeholder="New Password " type="password" name="newPassword" id="newPassword" class="form-control h-auto form-control-solid py-4 px-8" autocomplete="off" maxlength="32"/>
                            </div>
                            <!-- </span> -->
                            <div class="form-group mb-5">
                                <!-- <div class="input-group"> -->
                                <!-- <div class="input-group-prepend">
                                    <span class="input-group-text">
                                        <i class="material-icons">lock_outline</i>
                                      </span>
                                </div> -->
                                <s:textfield placeholder="Confirm Password " type="password" name="confirmNewPassword" id="confirmNewPassword" class="form-control h-auto form-control-solid py-4 px-8"  autocomplete="off" maxlength="32"/>
                            </div>
                            <!-- </span> -->

                            <!-- </div> -->
                            <!-- <div class="card-footer justify-content-center"> -->
                            <input type="button"  id="submit" value="Submit" class="btn btn-primary btn-round mt-4">
                            <!-- </div> -->
                            <!-- </div> -->
                        </s:form>
                    </div>
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
<script src="../js/material-dashboard.js?v=2.1.0" type="text/javascript"></script>
<script>var HOST_URL = "https://preview.keenthemes.com/metronic/theme/html/tools/preview";</script>
<!--begin::Global Config(global config for global JS scripts)-->
<script>var KTAppSettings = { "breakpoints": { "sm": 576, "md": 768, "lg": 992, "xl": 1200, "xxl": 1200 }, "colors": { "theme": { "base": { "white": "#ffffff", "primary": "#1BC5BD", "secondary": "#E5EAEE", "success": "#1BC5BD", "info": "#6993FF", "warning": "#FFA800", "danger": "#F64E60", "light": "#F3F6F9", "dark": "#212121" }, "light": { "white": "#ffffff", "primary": "#1BC5BD", "secondary": "#ECF0F3", "success": "#C9F7F5", "info": "#E1E9FF", "warning": "#FFF4DE", "danger": "#FFE2E5", "light": "#F3F6F9", "dark": "#D6D6E0" }, "inverse": { "white": "#ffffff", "primary": "#ffffff", "secondary": "#212121", "success": "#ffffff", "info": "#ffffff", "warning": "#ffffff", "danger": "#ffffff", "light": "#464E5F", "dark": "#ffffff" } }, "gray": { "gray-100": "#F3F6F9", "gray-200": "#ECF0F3", "gray-300": "#E5EAEE", "gray-400": "#D6D6E0", "gray-500": "#B5B5C3", "gray-600": "#80808F", "gray-700": "#464E5F", "gray-800": "#1B283F", "gray-900": "#212121" } }, "font-family": "Poppins" };</script>
<!--end::Global Config-->
<!--begin::Global Theme Bundle(used by all pages)-->
<!-- <script src="../assets/plugins/global/plugins.bundle.js"></script> -->
<script src="../assets/plugins/custom/prismjs/prismjs.bundle.js"></script>
<script src="../assets/js/scripts.bundle.js"></script>
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