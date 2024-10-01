<%@taglib prefix="s" uri="/struts-tags"%>
<link href="../fonts/css/font-awesome.min.css" rel="stylesheet">
<link href="../css/default.css" rel="stylesheet">
<link href="../css/custom.css" rel="stylesheet">
<link rel="stylesheet" href="../css/navigation.css">
<link href="../css/welcomePage.css" rel="stylesheet">
<script type="text/javascript" src="../js/jquery.js"></script>

<script type="text/javascript" src="../js/jquery.easing.js"></script>
<script type="text/javascript" src="../js/jquery.dimensions.js"></script>
<script type="text/javascript" src="../js/jquery.accordion.js"></script>
<!--<link rel="stylesheet" href="../css/loader.css">-->

<script>
	$.noConflict();
	// Code that uses other library's $ can follow here.
</script>


<style>
	#navigation a.selected #arrow{
	   color: white;
	   -webkit-transform: rotate(180deg);
	   -moz-transform: rotate(180deg);
	   -o-transform: rotate(180deg);
	   -ms-transform: rotate(180deg);
	   transform: rotate(180deg);
   }
   
   #navigation li li a{
	   border-radius: 6px !important;
	   margin-left: -30px !important;
   }
   .scrollbar {
	 overflow-y: scroll;
	 background: #496cb6;
   }
   /* width */
   ::-webkit-scrollbar {
	 width: 10px;
   }
   
   
   /* Track */
   ::-webkit-scrollbar-track {
	 box-shadow: inset 0 0 5px grey; 
	 border-radius: 10px;
   }
	
   /* Handle */
   ::-webkit-scrollbar-thumb {
	 background: #496cb6; 
	 border-radius: 10px;
   }
   
   /* Handle on hover */
   ::-webkit-scrollbar-thumb:hover {
	 background: #496cb6; 
   }
   .dropdown-menu {
		  padding: 10 0 !important;
	   position: absolute;
	   top: 100%;
	   left: 0;
	   z-index: 1000;
	   display: none;
	   float: left;
	   min-width: 10rem;
	   padding: 0.5rem 0;
	   margin: 0.125rem 0 0;
	   font-size: 1rem;
	   color: #212529;
	   text-align: center !important;
	   list-style: none;
	   background-color: #ffffff;
	   background-clip: padding-box;
	   border: 1px solid rgba(0, 0, 0, 0.15);
	   border-radius: 0.25rem;
	   box-shadow: 0 2px 2px 0 rgba(0, 0, 0, 0.14), 0 3px 1px -2px rgba(0, 0, 0, 0.2), 0 1px 5px 0 rgba(0, 0, 0, 0.12);
   }
   @media (max-width: 990px){
	   #welcomeHeading {
		 color:white;
	 }
	 }
	 @media(max-width:990px){
	   .navbar-toggler:not(:disabled):not(.disabled) {
	   cursor: pointer;
	   position: absolute;
	   right: 5px;
   }
	 }
	 @media(max-width:990px){
	   
   .caret, .sidebar a{
	   color:white;
	 }
   }
   @media(max-width:990px){
	   .dropdown-menu:after {
	   border-bottom: 6px solid #999999;
	   border-left: 6px solid transparent;
	   border-right: 6px solid transparent;
	   content: "";
	   display: inline-block;
	   right: 46%;
	   position: absolute;
	   top: 44px;
   }
   }
   .card_sidebar {
		   margin-bottom: 10px;
		   margin-top: 10px;
		   border-radius: 6px;
	   }
   </style>

<div id="fade"></div>
<!-- <div id="modal" class="lodinggif">
	<img id="loader" src="../image/sand-clock-loader.gif" />
</div> -->

<body class="">
	<div class="wrapper ">
		<div class="sidebar" data-color="rose" data-background-color="black" >
			<!--
        Tip 1: You can change the color of the sidebar using: data-color="purple | azure | green | orange | danger"

        Tip 2: you can also add an image using data-image tag
    -->
	<%@ include file="/jsp/headerForMenu.jsp"%> 
			<div class="sidebar-wrapper">


				<ul class="nav" id='MenuUl'>
					<div class="accordion" id="accordionExample">
					
			
					<!-- <li class="nav-item "  id='home'>
						<s:a action='home' class="nav-link" >
							<i class="material-icons">dashboard</i>
							<p>Dashboard</p>
						</s:a>

					</li> -->
				
				
					   <div class="card_sidebar">
						<li class="nav-item " id='transactionSearchSuperAdmin'>
							<s:a action='transactionSearchSuperAdmin' class="nav-link" >
								<i class="material-icons">search</i>
								<p> Search Payment </p>
							</s:a>
	
						</li>
						<li class="nav-item " id="headingOne">
                            <a class="nav-link" data-toggle="collapse" data-target="#viewAdminSetup" href="#viewAdminSetup" id='a-viewAdminSetup'>
                                <i class="material-icons">person</i>
                                <p> Manage Users
                                    <b class="caret"></b>
                                </p>
                            </a>
                            <div class="collapse" id="viewAdminSetup" aria-labelledby="headingOne"
							data-parent="#accordionExample">
                                <ul class="nav">
									<li class="nav-item " id="adminSignup">
                                        <s:a action='adminSignup' class="nav-link">
                                            <span class="sidebar-mini">CA </span>
                                            <span class="sidebar-normal">Create Admin </span>
                                        </s:a>
									</li>
                                    <li class="nav-item " id="viewAdmin">
                                        <s:a action='viewAdmin' class="nav-link">
                                            <span class="sidebar-mini"> AL </span>
                                            <span class="sidebar-normal"> Admin List </span>
                                        </s:a>
                                    </li>
                                   
									<li class="nav-item " id="addSubSuperAdmin">
										<s:a action="addSubSuperAdmin" class="nav-link">
											<span class="sidebar-mini"> ASSA </span>
											<span class="sidebar-normal"> Add Sub Super Admin </span>
										</s:a>
									</li>
									<li class="nav-item " id="searchSubSuperAdmin">
										<s:a action="searchSubSuperAdmin" class="nav-link">
											<span class="sidebar-mini"> SSAL </span>
											<span class="sidebar-normal">Sub Super Admin List </span>
										</s:a>
									</li>
    
                                </ul>
							</div>
							
							
                        </li>
						
						</div>
						<div class="card_sidebar">
							
										 
							<li class="nav-item " id="headingTwo">
								<a class="nav-link" data-toggle="collapse" data-target="#createTenant" href="#createTenant" id='a-createTenant'>
									<i class="material-icons">people</i>
									<p> Whitelabel Setup
										<b class="caret"></b>
									</p>
								</a>
								<div class="collapse" id="createTenant" aria-labelledby="headingTwo"
								data-parent="#accordionExample">
									<ul class="nav">
										<li class="nav-item " id="createTenantPage">
											<s:a action='createTenantPage' class="nav-link">
												<span class="sidebar-mini"> CT </span>
												<span class="sidebar-normal"> Create Tanent </span>
											</s:a>
										</li>
										<li class="nav-item " id="tenantListPage">
											<s:a action='tenantListPage' class="nav-link">
												<span class="sidebar-mini">TL </span>
												<span class="sidebar-normal">Tenant List </span>
											</s:a>
										</li>
									  
		
									</ul>
								</div>
								
								
							</li>
							
							</div>
					
							<div class="card_sidebar">
								<li class="nav-item " id="headingThirteen">
									<a class="nav-link" data-toggle="collapse" data-target="#myAccount" href="#myAccount"
										id="a-myAccount">
										<i class="material-icons">person</i>
										<p> My Account
											<b class="caret"></b>
										</p>
									</a>
									<div class="collapse" id="myAccount" aria-labelledby="headingThirteen"
										data-parent="#accordionExample">
										<ul class="nav">
											<li class="nav-item " id="superAdminProfile">
												<s:a action="superAdminProfile" class="nav-link">
													<span class="sidebar-mini"> MP </span>
													<span class="sidebar-normal"> My Profile</span>
												</s:a>
											</li>
											<li class="nav-item " id="loginHistoryRedirectSuperAdmin">
												<s:a action="loginHistoryRedirectSuperAdmin" class="nav-link">
													<span class="sidebar-mini"> LH </span>
													<span class="sidebar-normal"> Login History</span>
												</s:a>
											</li>
											<li class="nav-item  " id="passwordChange">
												<s:a action='passwordChangeSuperAdmin' class="nav-link">
													<span class="sidebar-mini"> CP </span>
													<span class="sidebar-normal"> Change Password</span>
												</s:a>
											</li>
										</ul>
									</div>
								</li>
							</div>
					
					
						
				</div>
				</ul>
			
			</div>

		</div>
		<div class="main-panel" >
			<!-- Navbar -->
			<nav class="navbar navbar-expand-lg navbar-transparent navbar-absolute fixed-top "
				style="background-color: #d7dadd !important;position: relative;">
				<div class="container-fluid">
					<div class="navbar-wrapper">
						<div class="navbar-minimize">
							<button id="minimizeSidebar" class="btn btn-just-icon btn-white btn-fab btn-round">
								<i class="material-icons text_align-center visible-on-sidebar-regular">more_vert</i>
								<i class="material-icons design_bullet-list-67 visible-on-sidebar-mini">view_list</i>
							</button>
						</div>
						<!-- <a class="navbar-brand" href="#">Dashboard</a>-->
					</div>
					<button class="navbar-toggler" type="button" data-toggle="collapse" aria-controls="navigation-index"
						aria-expanded="false" aria-label="Toggle navigation">
						<span class="sr-only">Toggle navigation</span>
						<span class="navbar-toggler-icon icon-bar"></span>
						<span class="navbar-toggler-icon icon-bar"></span>
						<span class="navbar-toggler-icon icon-bar"></span>
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
							
							
							<li class="nav-item dropdown">
							  <a class="nav-link" href="#pablo" id="navbarDropdownProfile" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
								<i
									class="fa fa-user fa-fw"></i><b id="welcomeHeading">Welcome <s:property
										value="%{#session.USER.businessName}" />
							  </a>
							  <div class="dropdown-menu dropdown-menu-right" aria-labelledby="navbarDropdownProfile">
								<s:a action="logout"> Logout</s:a>
								<!-- <a class="dropdown-item" href="#">Profile</a>
								<a class="dropdown-item" href="#">Settings</a> -->
								<!-- <div class="dropdown-divider"></div> -->
								
							  </div>
							</li>
						  </ul>
                          </div>
                        </div>
					  </nav>
					  
					  
					  <div class="row" ><div class="col-md-12 text-left" id="breadcrump" style="margin-top:5px;margin-left:20px;"> <a href="home" class="newredtxt"><i class="material-icons" style="height: 30px;">home</i>Home</a> | <a href="javascript:window.history.back();" class="newredtxt"><i class="material-icons" style="height: 30px;">arrow_back</i>Back</a></div></div> 
	
					  <div class="content">
						<div class="content">
						  <div class="container-fluid">
			<decorator:body />
			</div>
			</div>
			</div> 
            <%@ include file="/jsp/footer.jsp"%> 
        </div>
	</div>
              
</body>

<script>
	var currentUrl = window.location.href.split('/');
	$('#MenuUl > li').removeClass('active');
	if(currentUrl.includes("transactionSearchSuperAdmin")) {
		$('#transactionSearchSuperAdmin').addClass('active');
	}else if(currentUrl.includes("adminSignup") || currentUrl.includes("viewAdmin") || currentUrl.includes("addSubSuperAdmin") || currentUrl.includes("searchSubSuperAdmin")) {
		$('#viewAdminSetup').addClass('show');
		$('#a-viewAdminSetup').attr('aria-expanded', true);
		if (currentUrl.includes("adminSignup")) {
				$('#adminSignup').addClass('active');
			} else if (currentUrl.includes("viewAdmin")) {
				$('#viewAdmin').addClass('active');
			} else if (currentUrl.includes("addSubSuperAdmin")) {
				$('#addSubSuperAdmin').addClass('active');
			} else if (currentUrl.includes("transactionSearchSuperAdmin")) {
				$('#searchSubSuperAdmin').addClass('active');
			} else if (currentUrl.includes("searchSubSuperAdmin")) {
				$('#searchSubSuperAdmin').addClass('active');
			}
		
	}else if (currentUrl.includes("createTenantPage") || currentUrl.includes("tenantListPage")) {
			$('#createTenant').addClass('show');
			$('#a-createTenant').attr('aria-expanded', true);
			if (currentUrl.includes("createTenantPage")) {
				$('#createTenantPage').addClass('active');
			}  else if (currentUrl.includes("transactionSearchSuperAdmin")) {
				$('#tenantListPage').addClass('active');
			} else if (currentUrl.includes("tenantListPage")) {
				$('#tenantListPage').addClass('active');
			}
		}else if (currentUrl.includes("superAdminProfile") || currentUrl.includes("loginHistoryRedirectSuperAdmin") || currentUrl.includes("passwordChangeSuperAdmin")) {
			$('#myAccount').addClass('show');
			$('#a-myAccount').attr('aria-expanded', true);
			if (currentUrl.includes("superAdminProfile")) {
				$('#superAdminProfile').addClass('active');
			} else if (currentUrl.includes("loginHistoryRedirectSuperAdmin")) {
				$('#loginHistoryRedirectSuperAdmin').addClass('active');
			} else if (currentUrl.includes("transactionSearchSuperAdmin")) {
				$('#passwordChange').addClass('active');
			} else if (currentUrl.includes("passwordChangeSuperAdmin")) {
				$('#passwordChange').addClass('active');
			}
		} 
</script>
