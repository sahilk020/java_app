<!DOCTYPE html>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@taglib prefix="decorator"
  uri="http://www.opensymphony.com/sitemesh/decorator"%>
<%@taglib prefix="page" uri="http://www.opensymphony.com/sitemesh/page"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/core" prefix = "c" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>
<html lang="en">
<head>
<base href="">
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="refresh" content="905;url=redirectLogin" />
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="viewport"
  content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
<title><decorator:title default="Pay10 Payments" /></title>
<link rel="icon" href="../image/98x98.png">
<link href="../assets/google-fonts/fonts-google.css" />
<!-- CSS Files -->
<link rel="shortcut icon" href="../assets/media/images/paylogo.svg" />
<!--begin::Vendor Stylesheets(used by this page)-->
<link
  href="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.css"
  rel="stylesheet" type="text/css" />
<link href="../assets/plugins/custom/datatables/datatables.bundle.css"
  rel="stylesheet" type="text/css" />
<!--end::Vendor Stylesheets-->
<!--begin::Global Stylesheets Bundle(used by all pages)-->
<link href="../assets/plugins/global/plugins.bundle.css"
  rel="stylesheet" type="text/css" />
<link href="../assets/css/style.bundle.css" rel="stylesheet"
  type="text/css" />
<link href="../assets/css/custom/custom-style.css" rel="stylesheet"
  type="text/css" />
<decorator:head />



<!--  loader scripts -->
<script>
    document.addEventListener("DOMContentLoaded", function() {
        console.log("DOM Loaded"); 	document.querySelectorAll("input[type=number]").forEach(function(item){
        item.addEventListener("keypress", function (evt) {
            if(evt.key=='e' || evt.key=='E' || evt.key=='+' || evt.key=='-'){
                evt.preventDefault();
            }
        });
      });
    });
</script>
<style>
  .container {
  position: relative;
  height: 300px; /* You can adjust this height as needed */
}

.bottom-div {
  position: absolute;
  bottom: 10px;
  left: 50%;
  transform: translateX(-50%);
}
</style>
</head>

<body id="kt_body" class="header-fixed header-tablet-and-mobile-fixed toolbar-enabled toolbar-fixed toolbar-tablet-and-mobile-fixed aside-enabled aside-fixed" style="--kt-toolbar-height:55px;--kt-toolbar-height-tablet-and-mobile:55px">
  <!-- <div class="container body"> -->
  <s:set var="tokenS" value="%{#session.customToken}" />
  <s:hidden id="token" name="token" value="%{tokenS}"></s:hidden>

  <!-- <div id="fade"></div> -->
  <div class="d-flex flex-column flex-root">
    <div class="page d-flex flex-row flex-column-fluid">
      <div id="kt_aside" class="aside aside-dark aside-hoverable"
        data-kt-drawer="true" data-kt-drawer-name="aside"
        data-kt-drawer-activate="{default: true, lg: false}"
        data-kt-drawer-overlay="true"
        data-kt-drawer-width="{default:'200px', '300px': '250px'}"
        data-kt-drawer-direction="start"
        data-kt-drawer-toggle="#kt_aside_mobile_toggle">
        <!--begin::Brand-->
        <div class="aside-logo flex-column-auto" id="kt_aside_logo">
          <!--begin::Logo-->
          <a href="home"> <img alt="Logo"
            src="../assets/media/images/paylogo1.svg" class="h-50px logo" />
          </a>
          <!--end::Logo-->
          <!--begin::Aside toggler-->
          <div id="kt_aside_toggle"
            class="btn btn-icon w-auto px-0 btn-active-color-primary aside-toggle me-n2"
            data-kt-toggle="true" data-kt-toggle-state="active"
            data-kt-toggle-target="body" data-kt-toggle-name="aside-minimize">
            <!--begin::Svg Icon | path: icons/duotune/arrows/arr079.svg-->
            <span class="svg-icon svg-icon-1 rotate-180"> <svg
                width="24" height="24" viewBox="0 0 24 24" fill="none"
                xmlns="http://www.w3.org/2000/svg">
                  <path opacity="0.5"
                  d="M14.2657 11.4343L18.45 7.25C18.8642 6.83579 18.8642 6.16421 18.45 5.75C18.0358 5.33579 17.3642 5.33579 16.95 5.75L11.4071 11.2929C11.0166 11.6834 11.0166 12.3166 11.4071 12.7071L16.95 18.25C17.3642 18.6642 18.0358 18.6642 18.45 18.25C18.8642 17.8358 18.8642 17.1642 18.45 16.75L14.2657 12.5657C13.9533 12.2533 13.9533 11.7467 14.2657 11.4343Z"
                  fill="currentColor" />
                  <path
                  d="M8.2657 11.4343L12.45 7.25C12.8642 6.83579 12.8642 6.16421 12.45 5.75C12.0358 5.33579 11.3642 5.33579 10.95 5.75L5.40712 11.2929C5.01659 11.6834 5.01659 12.3166 5.40712 12.7071L10.95 18.25C11.3642 18.6642 12.0358 18.6642 12.45 18.25C12.8642 17.8358 12.8642 17.1642 12.45 16.75L8.2657 12.5657C7.95328 12.2533 7.95328 11.7467 8.2657 11.4343Z"
                  fill="currentColor" />
                </svg>
            </span>
            <!--end::Svg Icon-->
          </div>
          <!--end::Aside toggler-->
        </div>
        <!--begin::Aside menu-->
        <div class="aside-menu flex-column-fluid">
          <div class="" id="kt_aside_menu_wrapper"
            data-kt-scroll="true"
            data-kt-scroll-activate="{default: false, lg: true}"
            data-kt-scroll-height="auto"
            data-kt-scroll-dependencies="#kt_aside_logo, #kt_aside_footer"
            data-kt-scroll-wrappers="#kt_aside_menu" data-kt-scroll-offset="0">
            <!--begin::Menu-->
            <div
              class="menu menu-column menu-title-gray-800 menu-state-title-primary menu-state-icon-primary menu-state-bullet-primary menu-arrow-gray-500"
              id="#kt_aside_menu" data-kt-menu="true">
              <!--begin:Menu item-->
              <div class="menu-item here show menu-accordion">
                <s:a action='home' class="menu-link active" id="home">
                  <span class="menu-icon"> <i class="bi bi-grid fs-3"></i>
                  </span>
                  <s:if
                    test="%{#session.USER.UserType.name()=='ADMIN' || #session.USER_TYPE.name()=='SUPERADMIN'}">
                    <p class="page-headerr">Admin Dashboard</p>
                  </s:if>

                  <s:elseif test="%{#session.USER.UserType.name()=='SUBADMIN'}">
                    <s:if test="%{#session.USER.UserGroup.group=='Operations'}">
                      <p class="page-headerr" style="font-size: 12px;">
                        <s:property value='%{#session.USER.UserGroup.group}' />
                        Admin Dashboard
                      </p>
                    </s:if>
                    <s:if test="%{#session.USER.UserGroup.group=='Sub Admin'}">
                      <p class="page-headerr" style="font-size: 12px;">
                        <%-- <s:property value = '%{#session.USER.UserGroup.group}'/> --%>
                        Sub Admin Dashboard
                      </p>
                    </s:if>
                    <s:else>
                      <p class="page-headerr">Sub Admin Dashboard</p>
                    </s:else>
                  </s:elseif>
                  <s:elseif test="%{#session.USER.UserGroup.group=='SMA' || #session.USER.UserGroup.group=='MA' || #session.USER.UserGroup.group=='Agent'}">
                    <p class="page-headerr">Reseller Dashboard</p>
                  </s:elseif>

                  <s:else>
                    <span class="menu-title"> Merchant Dashboard </span>
                  </s:else>
                </s:a>
              </div>
              <s:if test="%{#session.USER.UserType.name()=='ADMIN'}">
                <div class="menu-item here show menu-accordion">
                  <s:a action="roleList" class="menu-link" id="roleList">
                    <span class="menu-icon"> <i
                      class="bi bi-card-list fs-3"></i>
                    </span>
                    <span class="menu-title"> Role List </span>
                  </s:a>
                </div>
              </s:if>
              <s:hidden id="menuTree" value="%{#session.ACCESSIBLE_MENUS_JSON}"></s:hidden>
              <s:hidden id="menuAccessByROLE"
                value="%{#session.ACCESSIBLE_PERMISSION}"></s:hidden>
              <s:iterator value="%{#session.ACCESSIBLE_MENUS}">
              <s:if test="%{needToShowInProfile == false && active == true}">
              <div data-kt-menu-trigger="click"
                  class="menu-item menu-accordion"
                  id="<s:property value = 'id'/>">
                  <s:set var="actionMainMenu" value="actionName"></s:set>
                  <span class="menu-link"> <span class="menu-icon">
                      <i class="<s:property value = 'icon'/>"></i>
                  </span> <span class="menu-title"><s:property value='menuName' /></span>
                    <span class="menu-arrow"></span>
                  </span>
                  <div class="menu-sub menu-sub-accordion">
                    <s:iterator value="subMenus">
                      <s:if test="%{needToShowInProfile == false}">
                      <div class="menu-item">
						<c:set var = "actualId" value = "${actionName}"/>
						<c:set var = "generatedId" value = "${fn:replace(actualId, '?', '')}"/>
						<c:set var = "generatedId" value = "${fn:replace(generatedId, '=', '')}"/>
                        <a id="${actualId}" class="menu-link"
                          href='<s:property value = "actionName"/>'> <span
                          class="menu-bullet"> <span class="bullet bullet-dot"></span>
                        </span> <span class="menu-title"> <s:property
                              value="menuName" />
                        </span>
                        </a>
                      </div>
                      </s:if>
                    </s:iterator>
                  </div>
                </div>
                </s:if>
              </s:iterator>

            </div>
            <div class="container">
              <div class="bottom-div">
               <strong>Version: <strong style="text-align: center;" id="version"></strong></strong>
              </div>
            </div>
          </div>
        </div>
       
      </div>
      
      
      <!--Start Show Session Expire Warning Popup here -->

    <div class="modal fade" id="session-expire-warning-modal" aria-hidden="true" data-keyboard="false" data-backdrop="static" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">

        <div class="modal-dialog modal-dialog-centered" role="document">

            <div class="modal-content">

                <div class="modal-header">                 

                    <h4 class="modal-title">Session Expire Warning</h4>

                </div>

                <div class="modal-body">

                    Your session will expire in <span id="seconds-timer"></span> seconds. Do you want to extend the session?

                </div>

                <div class="modal-footer">

                    <button id="btnOk" type="button" class="btn btn-default"  style="padding: 6px 12px; margin-bottom: 0; font-size: 14px; font-weight: normal; border: 1px solid transparent; border-radius: 4px;  background-color: #428bca; color: #FFF;">Ok</button>


                    <button id="btnLogoutNow" type="button" class="btn btn-default"  onclick="sessionExpiredRedirect()" style="padding: 6px 12px; margin-bottom: 0; font-size: 14px; font-weight:normal; border: 1px solid transparent; border-radius: 4px;  background-color: #428bca; color: #FFF;">Logout now</button>

                </div>

            </div>

        </div>

    </div>

    <!--End Show Session Expire Warning Popup here -->

    <!--Start Show Session Expire Popup here -->

    <div class="modal fade" id="session-expired-modal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">

        <div class="modal-dialog" role="document">

            <div class="modal-content">

                <div class="modal-header">

                    <h4 class="modal-title">Session Expired</h4>

                </div>

                <div class="modal-body">

                    Your session is expired.

                </div>

                <div class="modal-footer">

                    <button id="btnExpiredOk" onclick="sessionExpiredRedirect()" type="button" class="btn btn-primary" data-dismiss="modal" style="padding: 6px 12px; margin-bottom: 0; font-size: 14px; font-weight: normal; border: 1px solid transparent; border-radius: 4px; background-color: #428bca; color: #FFF;">Ok</button>

                </div>

            </div>

        </div>

    </div>
      <div class="wrapper d-flex flex-column flex-row-fluid" id="kt_wrapper">
      <%@ include file="/jsp/headerForMenu.jsp"%>
      <script>
    var hostUrl = "../assets/";
  </script>
  <!--begin::Scrolltop-->
    <div id="kt_scrolltop" class="scrolltop" data-kt-scrolltop="true">
      <!--begin::Svg Icon | path: icons/duotune/arrows/arr066.svg-->
      <span class="svg-icon">
        <svg width="24" height="24" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <rect opacity="0.5" x="13" y="6" width="13" height="2" rx="1" transform="rotate(90 13 6)" fill="currentColor" />
          <path d="M12.5657 8.56569L16.75 12.75C17.1642 13.1642 17.8358 13.1642 18.25 12.75C18.6642 12.3358 18.6642 11.6642 18.25 11.25L12.7071 5.70711C12.3166 5.31658 11.6834 5.31658 11.2929 5.70711L5.75 11.25C5.33579 11.6642 5.33579 12.3358 5.75 12.75C6.16421 13.1642 6.83579 13.1642 7.25 12.75L11.4343 8.56569C11.7467 8.25327 12.2533 8.25327 12.5657 8.56569Z" fill="currentColor" />
        </svg>
      </span>
      <!--end::Svg Icon-->
    </div>
    <!--end::Scrolltop-->
      <decorator:body />
      <%@ include file="/jsp/footer.jsp"%>
      </div>
    </div>
  </div>
  <div id="custom_notifications" class="custom-notifications dsp_none">
    <ul class="list-unstyled notifications clearfix"
      data-tabbed_notifications="notif-group">
    </ul>
    <div class="clearfix"></div>
    <div id="notif-group" class="tabbed_notifications"></div>
  </div>
</body>
<script type="text/javascript">
  var currentUrl = window.location.href.split('/');
  $('.menu-item > a').removeClass('active');
  if (currentUrl.includes("home")) {
    $('#home').addClass('active');
  }
  if (currentUrl.includes("roleList")) {
    $('#roleList').addClass('active');
  }
  var menuTree = document.getElementById("menuTree").value;
  var menus = new Array(JSON.parse(menuTree))[0];
  for (var i = 0; i < menus.length; i++) {
    var menu = menus[i];
    var subMenus = JSON.parse(JSON.stringify(menu["subMenus"]));
    for (var j = 0; j < subMenus.length; j++) {
      var submenu = subMenus[j];
      var actionName = submenu["actionName"];
	  if (actionName.includes("?")) {
		  actionName = actionName.replace("?", "");
	  }
      var parentId = submenu["parentId"];

	  if (currentUrl[currentUrl.length - 1].includes("?")) {
		currentUrl = currentUrl[currentUrl.length - 1].split("?").join("");
	  }
      if (currentUrl.includes(actionName)) {
        $('#' + parentId).addClass('show');
		var subAction = submenu["actionName"];
		if (subAction.includes("?")) {
		  subAction = subAction.replace("?", "");
		  subAction = subAction.replace("=", "");
		}
        $('#' + subAction).addClass('active');
      }
    }
  }
</script>

<script>

var sessServerAliveTime = 10 * 60 * 2;
var sessionTimeout = 900 * 1000;
var sessLastActivity;
var idleTimer, remainingTimer;
var isTimout = false;

var sess_intervalID, idleIntervalID;
var sess_lastActivity;
var timer;
var isIdleTimerOn = false;
localStorage.setItem('sessionSlide', 'isStarted');

function sessPingServer() {
    if (!isTimout) {
        //$.ajax({
        //    url: '/Admin/SessionTimeout',
        //    dataType: "json",
        //    async: false,
        //    type: "POST"
        //});

        return true;
    }
}

function sessServerAlive() {

    sess_intervalID = setInterval('sessPingServer()', sessServerAliveTime);
}

function initSessionMonitor() {
    $(document).bind('keypress.session', function (ed, e) {
        sessKeyPressed(ed, e);
    });
    $(document).bind('mousedown keydown', function (ed, e) {

        sessKeyPressed(ed, e);
    });
    sessServerAlive();
    startIdleTime();
}

$(window).scroll(function (e) {
    localStorage.setItem('sessionSlide', 'isStarted');
    startIdleTime();
});

function sessKeyPressed(ed, e) {

    var target = ed ? ed.target : window.event.srcElement;
    var sessionTarget = $(target).parents("#session-expire-warning-modal").length;

    if (sessionTarget != null && sessionTarget != undefined) {
        if (ed.target.id != "btnSessionExpiredCancelled" && ed.target.id != "btnSessionModal" && ed.currentTarget.activeElement.id != "session-expire-warning-modal" && ed.target.id != "btnExpiredOk"
             && ed.currentTarget.activeElement.className != "modal fade modal-overflow in" && ed.currentTarget.activeElement.className != 'modal-header'
    && sessionTarget != 1 && ed.target.id != "session-expire-warning-modal") {
            localStorage.setItem('sessionSlide', 'isStarted');
            startIdleTime();
        }
    }
}




function startIdleTime() {

    stopIdleTime();
    localStorage.setItem("sessIdleTimeCounter", $.now());
    idleIntervalID = setInterval('checkIdleTimeout()', 1000);
    isIdleTimerOn = false;
}

var sessionExpired = document.getElementById("session-expired-modal");
function sessionExpiredClicked(evt) {

	var domain = urls.origin;
	   url =domain+"/crm/jsp/logout"
	   
	    window.location = url;}

sessionExpired.addEventListener("click", sessionExpiredClicked, false);
function stopIdleTime() {

    clearInterval(idleIntervalID);
    clearInterval(remainingTimer);
}

function checkIdleTimeout() {
     // $('#sessionValue').val() * 60000;
     

     
    var idleTime = (parseInt(localStorage.getItem('sessIdleTimeCounter')) + (sessionTimeout)); 
    if ($.now() > idleTime + 60000) {
        $("#session-expire-warning-modal").modal('hide');
        $("#session-expired-modal").modal('show');
        clearInterval(sess_intervalID);
        clearInterval(idleIntervalID);

        $('.modal-backdrop').css("z-index", parseInt($('.modal-backdrop').css('z-index')) + 100);
        $("#session-expired-modal").css('z-index', 2000);
        $('#btnExpiredOk').css('background-color', '#428bca');
        $('#btnExpiredOk').css('color', '#fff');

        isTimout = true;

        sessLogOut();

    }
    else if ($.now() > idleTime) {

        ////var isDialogOpen = $("#session-expire-warning-modal").is(":visible");
        if (!isIdleTimerOn) {
            ////alert('Reached idle');
            localStorage.setItem('sessionSlide', false);
            countdownDisplay();

            $('.modal-backdrop').css("z-index", parseInt($('.modal-backdrop').css('z-index')) + 500);
            $('#session-expire-warning-modal').css('z-index', 1500);
            $('#btnOk').css('background-color', '#428bca');
            $('#btnOk').css('color', '#fff');
            $('#btnSessionExpiredCancelled').css('background-color', '#428bca');
            $('#btnSessionExpiredCancelled').css('color', '#fff');
            $('#btnLogoutNow').css('background-color', '#428bca');
            $('#btnLogoutNow').css('color', '#fff');

            $("#seconds-timer").empty();
            $("#session-expire-warning-modal").modal('show');

            isIdleTimerOn = true;
        }
    }
}

$("#btnSessionExpiredCancelled").click(function () {
    $('.modal-backdrop').css("z-index", parseInt($('.modal-backdrop').css('z-index')) - 500);
});

$("#btnOk").click(function () {

    $("#session-expire-warning-modal").modal('hide');
    $('.modal-backdrop').css("z-index", parseInt($('.modal-backdrop').css('z-index')) - 500);
    startIdleTime();
    clearInterval(remainingTimer);
    localStorage.setItem('sessionSlide', 'isStarted');
});

$("#btnLogoutNow").click(function () {
    localStorage.setItem('sessionSlide', 'loggedOut');
    var urls = new URL(window.location.href);
	var domain = urls.origin;
   url =domain+"/crm/jsp/logout"
   
    window.location = url;
    sessLogOut();
    $("#session-expired-modal").modal('hide');

});
$('#session-expired-modal').on('shown.bs.modal', function () {
    $("#session-expire-warning-modal").modal('hide');
    $(this).before($('.modal-backdrop'));
    $(this).css("z-index", parseInt($('.modal-backdrop').css('z-index')) + 1);
});

$("#session-expired-modal").on("hidden.bs.modal", function () {
	var domain = urls.origin;
	   url =domain+"/crm/jsp/logout"
	   
	    window.location = url;});
$('#session-expire-warning-modal').on('shown.bs.modal', function () {
    $("#session-expire-warning-modal").modal('show');
    $(this).before($('.modal-backdrop'));
    $(this).css("z-index", parseInt($('.modal-backdrop').css('z-index')) + 1);
});

function countdownDisplay() {

    var dialogDisplaySeconds = 60;

    remainingTimer = setInterval(function () {
        if (localStorage.getItem('sessionSlide') == "isStarted") {
            $("#session-expire-warning-modal").modal('hide');
            startIdleTime();
            clearInterval(remainingTimer);
        }
        else if (localStorage.getItem('sessionSlide') == "loggedOut") {         
            $("#session-expire-warning-modal").modal('hide');
            $("#session-expired-modal").modal('show');
        }
        else {

            $('#seconds-timer').html(dialogDisplaySeconds);
            dialogDisplaySeconds -= 1;
        }
    }
    , 1000);
};


function sessionExpiredRedirect(){

var urls = new URL(window.location.href);

	var domain = urls.origin;
	   url =domain+"/crm/jsp/logout"
	   
	    window.location = url;
}
version();
function version(){

	  var urls = new URL(window.location.href);
	  var domain = urls.origin;
	  var baseUrl = domain + "/crmws/VersionControl/";

	  $
			.ajax({
				url: baseUrl + "getVersion",
				type: "POST",
				headers: {
					"Content-Type": "application/json"
				},
				timeout: 0,
				success: function(responseData, status) {
					$("#version").text(responseData.respmessage);
				},
				error: function(data) {
					alert("Network error, Version Not Fetched");
				}
			});
	}





function sessLogOut() {
   // $.ajax({
   //     url: 'Logout.html',
   //     dataType: "json",
  //      async: false,
  //      type: "POST"
 //   });
//         var urls = new URL(window.location.href);

// 	var domain = urls.origin;
// 	   url =domain+"/crm/jsp/logout"
	   
// 	    window.location = url;
	   }

</script>

</html>