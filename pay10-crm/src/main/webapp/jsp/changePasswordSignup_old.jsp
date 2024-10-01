<%@ taglib uri="/struts-tags" prefix="s"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta name="viewport" content="width=device-width, initial-scale=1" />
<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
<title>Change Password</title>
<!-- <link rel="stylesheet" type="text/css" href="https://fonts.googleapis.com/css?family=Roboto:300,400,500,700|Roboto+Slab:400,700|Material+Icons" />
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/font-awesome/latest/css/font-awesome.min.css"> -->
<!-- CSS Files -->
<link href="../fonts/css/font-awesome.min.css" rel="stylesheet">
<link rel="stylesheet" type="text/css"
		href="../css/material-icons.css" />
	  <link rel="stylesheet" href="../css/material-font-awesome.min.css">
<link href="../css/material-dashboard.css?v=2.1.0" rel="stylesheet" />
<link href="../css/profile-page.css" rel="stylesheet" />
<script type="text/javascript" src="../js/jquery.js"></script>
<script type="text/javascript" src="../js/jquery.easing.js"></script>
<script type="text/javascript" src="../js/jquery.dimensions.js"></script>
<script type="text/javascript" src="../js/jquery.accordion.js"></script>
<script src="../js/core/popper.min.js"></script>
<script src="../js/core/bootstrap-material-design.min.js"></script>
<script src="../js/plugins/perfect-scrollbar.jquery.min.js"></script>
<!-- Control Center for Material Dashboard: parallax effects, scripts for the example pages etc -->
<script src="../js/material-dashboard.js?v=2.1.0" type="text/javascript"></script>
<script>
	if (self == top) {
		var theBody = document.getElementsByTagName('body')[0];
		theBody.style.display = "block";
	} else {
		top.location = self.location;
	}
</script>
<script type="text/javascript">
	jQuery().ready(function() {
		// simple accordion
		jQuery('#list1a').accordion();
		jQuery('#list1b').accordion({
			autoheight : false
		});

		// second simple accordion with special markup
		jQuery('#navigation').accordion({
			active : false,
			header : '.head',
			navigation : true,
			event : 'click',
			fillSpace : false,
			animated : 'easeslide'
		});
	});
</script>
<style>
	.active_tab {
    background-color: #496cb6;
    color: #fff;
}
</style>
</head>
<body >
	<div class="wrapper " style="background-color: white;">
		<div class="sidebar" data-color="rose" data-background-color="black">
			<!--
		Tip 1: You can change the color of the sidebar using: data-color="purple | azure | green | orange | danger"

		Tip 2: you can also add an image using data-image tag
	-->
	<%@ include file="/jsp/headerForMenu.jsp"%> 
			<div class="sidebar-wrapper">

				<ul class="nav" id='MenuUl'>
					
						<li class="nav-item " id='home'><s:a action='home'
								class="nav-link">
								<i class="material-icons">person</i>
								<p>My Profile</p>
							</s:a></li>
						<li class="nav-item " id='transactionSearch'><s:a
								action='passwordChangeSignUp' class="nav-link active_tab">
								<i class="material-icons">search</i>
								<p>Change Password</p>
							</s:a></li>
				
			</div>

		</div>
		<div class="main-panel" >
			<!-- Navbar -->
			<nav
				class="navbar navbar-expand-lg navbar-transparent navbar-absolute fixed-top "
				style="background-color: #d7dadd !important; position: relative;">
				<div class="container-fluid">
					<div class="navbar-wrapper">
						<div class="navbar-minimize">
							<button id="minimizeSidebar"
								class="btn btn-just-icon btn-white btn-fab btn-round">
								<i
									class="material-icons text_align-center visible-on-sidebar-regular">more_vert</i>
								<i
									class="material-icons design_bullet-list-67 visible-on-sidebar-mini">view_list</i>
							</button>
						</div>
						<a class="navbar-brand" >Change Password</a>
					</div>
					<button class="navbar-toggler" type="button" data-toggle="collapse"
						aria-controls="navigation-index" aria-expanded="false"
						aria-label="Toggle navigation">
						<span class="sr-only">Toggle navigation</span> <span
							class="navbar-toggler-icon icon-bar"></span> <span
							class="navbar-toggler-icon icon-bar"></span> <span
							class="navbar-toggler-icon icon-bar"></span>
					</button>
					<div class="collapse navbar-collapse justify-content-end">
						<form class="navbar-form">
							<!-- <div class="input-group no-border">
							  <input type="text" value="" class="form-control" placeholder="Search...">
							  <button type="submit" class="btn btn-white btn-round btn-just-icon">
								<i class="material-icons">search</i>
								<div class="ripple-container"></div>
							  </button>
							</div> -->
						</form>
						<ul class="navbar-nav">
							<!-- <li class="nav-item">
							  <a class="nav-link" href="#pablo">
								<i class="material-icons">dashboard</i>
								<p class="d-lg-none d-md-block">
								  Stats
								</p>
							  </a>
							</li> -->


							<li class="nav-item dropdown"><a class="nav-link"
								href="#pablo" id="navbarDropdownProfile" data-toggle="dropdown"
								aria-haspopup="true" aria-expanded="false"> <i
									class="fa fa-user fa-fw"></i><b id="welcomeHeading">Logout User </a>
								<div class="dropdown-menu dropdown-menu-right"
									aria-labelledby="navbarDropdownProfile">
									<s:a action="logout" id="logout"> Logout</s:a>
									<!-- <a class="dropdown-item" href="#">Profile</a>
								<a class="dropdown-item" href="#">Settings</a> -->
									<!-- <div class="dropdown-divider"></div> -->

								</div></li>
						</ul>
					</div>
				</div>
			</nav>



		
 
		 <!-- <table width="98%" border="0"
								cellspacing="0" cellpadding="0">
							
								<tr>
									<td align="left" valign="top" class="borderbottomgrey">&nbsp;</td>
								</tr>
								<tr>
									<td align="left" valign="top">&nbsp;</td>
								</tr>
								<tr>
									<td align="left" valign="top"> -->
										<div class="container">
											<div class="row" style="margin-top: 20px;">
										<em><strong>Password
												Criteria:</strong> Password must be minimum 6 and maximum 32
											characters long, with special characters (! @ , _ + / =) , at
											least one upper case and one lower case alphabet. Your new
											password must not be the same as any of your last four
											passwords.</em>
										<!-- </td>
								</tr>
								<tr>
									<td align="left" valign="top">&nbsp;</td>
								</tr>
								<tr>
									<td align="left" valign="top"> -->
										<div class="col-lg-4 col-md-6 col-sm-8 ml-auto mr-auto">
										<s:form
											action="changePasswordSignup" autocomplete="off">
											<!-- <table width="100%" border="0" align="center" cellpadding="0"
												cellspacing="0">

												<tr>
													<td align="center">&nbsp;</td>
													<td height="10" align="center"> -->
														<div class="card card-login card-hidden">
															<div class="card-header card-header-rose text-center">
															  <h4 class="card-title">Change Password</h4>
														
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
																
																  
																  <s:textfield
																  name="oldPassword" type="password" placeholder="Old Password"
																  cssClass="form-control"  autocomplete="off"/>
																  
																</div>
															  </span>
															  <span class="bmd-form-group">
																<div class="input-group">
																  <div class="input-group-prepend">
																	  <span class="input-group-text">
																		  <i class="material-icons">lock_outline</i>
																		</span>
																  </div>
																  <s:textfield name="newPassword" placeholder="New Password"
																  type="password" cssClass="form-control"  autocomplete="off"/>
																</div>
															  </span>
															  <span class="bmd-form-group">
																<div class="input-group">
																  <div class="input-group-prepend">
																	  <span class="input-group-text">
																		  <i class="material-icons">lock_outline</i>
																		</span>
																  </div>
																  <s:textfield
																  name="confirmNewPassword" type="password" placeholder="Confirm Password"
																  cssClass="form-control"  autocomplete="off"/>
																  
																</div>
															  </span>
															</div>
															<div class="card-footer justify-content-center">
																<s:submit
																		value="Save" class="btn btn-success" />
																
															</div>
															
															
													
													</div>
														
										</s:form>
	 </div>
	 </div>
	 </div>
</body>
</html>