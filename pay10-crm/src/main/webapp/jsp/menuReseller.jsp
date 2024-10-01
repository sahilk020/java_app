<%@taglib prefix="s" uri="/struts-tags"%>
<!-- <script src="../js/core/jquery.min.js"></script> -->

<!-- <link href="../css/bootstrap.min.css" rel="stylesheet"> -->
<link href="../fonts/css/font-awesome.min.css" rel="stylesheet">
<link href="../css/default.css" rel="stylesheet">
<link href="../css/custom.css" rel="stylesheet">
<!-- <link rel="stylesheet" href="../css/navigation.css"> -->
<!-- <link rel="stylesheet" href="../css/loader.css"> -->
<link href="../css/welcomePage.css" rel="stylesheet">
<script type="text/javascript" src="../js/jquery.js"></script>
<script type="text/javascript" src="../js/jquery.easing.js"></script>
<script type="text/javascript" src="../js/jquery.dimensions.js"></script>

<!-- <script type="text/javascript" src="../js/jquery.accordion.js"></script> -->
<script>
	$.noConflict();
	// Code that uses other library's $ can follow here.
</script>
<!-- <script type="text/javascript">
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
</script> -->

<style>
	#navigation a.selected #arrow {
		color: white;
		-webkit-transform: rotate(180deg);
		-moz-transform: rotate(180deg);
		-o-transform: rotate(180deg);
		-ms-transform: rotate(180deg);
		transform: rotate(180deg);
	}

	#navigation li li a {
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

	@media (max-width: 990px) {
		#welcomeHeading {
			color: white;
		}
	}

	@media(max-width:990px) {
		.navbar-toggler:not(:disabled):not(.disabled) {
			cursor: pointer;
			position: absolute;
			right: 5px;
		}
	}

	@media(max-width:990px) {

		.caret,
		.sidebar a {
			color: white;
		}
	}

	@media(max-width:990px) {
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
		<div class="sidebar" data-color="rose" data-background-color="black">
			<!--
        Tip 1: You can change the color of the sidebar using: data-color="purple | azure | green | orange | danger"

        Tip 2: you can also add an image using data-image tag
    -->
	<%@ include file="/jsp/headerForMenu.jsp"%> 
			<div class="sidebar-wrapper">


				<ul class="nav" id='MenuUl'>
					<div class="accordion" id="accordionExample">
						<li class="nav-item " id='home'><s:a action='home'
								class="nav-link">
								<i class="material-icons">dashboard</i>
								<p>Dashboard</p>
							</s:a></li>
						<li class="nav-item " id='transactionSearchReseller'><s:a
								action='transactionSearchReseller' class="nav-link">
								<i class="material-icons">search</i>
								<p>Search Payments</p>
							</s:a></li>

						
						<div class="card_sidebar">
							<li class="nav-item " id="headingOne"><a class="nav-link"
								data-toggle="collapse" data-target="#transReports"
								href="#transReports" id='a-transReports'> <i
									class="material-icons">bar_chart</i>
									<p>
										Transaction Reports <b class="caret"></b>
									</p>
							</a>
								<div class="collapse" id="transReports"
									aria-labelledby="headingOne" data-parent="#accordionExample">
									<ul class="nav">
										<li class="nav-item " id="saleTransactionSearch"><s:a
												action='saleTransactionSearch' class="nav-link">
												<span class="sidebar-mini"> SC </span>
												<span class="sidebar-normal"> Sale Captured </span>
											</s:a></li>
										<li class="nav-item " id="refundTransactionSearch"><s:a
												action='refundTransactionSearch' class="nav-link">
												<span class="sidebar-mini"> RC </span>
												<span class="sidebar-normal"> Refund Captured </span>
											</s:a></li>
										<li class="nav-item " id="settledTransactionSearch"><s:a
												action='settledTransactionSearch' class="nav-link">
												<span class="sidebar-mini"> ST </span>
												<span class="sidebar-normal"> Settled Report</span>
											</s:a></li>
										<li class="nav-item " id="downloadTransactionsReport"><s:a
												action='downloadTransactionsReport' class="nav-link">
												<span class="sidebar-mini"> DR </span>
												<span class="sidebar-normal"> Download Report </span>
											</s:a></li>

									</ul>
								</div></li>
						</div>

						<%-- <div class="card_sidebar">
							<li class="nav-item " id="headingTwo"><a class="nav-link"
								data-toggle="collapse" data-target="#manageUsers"
								href="#manageUsers" id='a-manageUsers'> <i
									class="material-icons">people</i>
									<p>
										Manage Users <b class="caret"></b>
									</p>
							</a>
								<div class="collapse" id="manageUsers"
									aria-labelledby="headingTwo" data-parent="#accordionExample">
									<ul class="nav">
										<li class="nav-item " id='addUser'><s:a action="addUser"
												class="nav-link">
												<span class="sidebar-mini"> AU </span>
												<span class="sidebar-normal"> Add User </span>
											</s:a></li>
										<li class="nav-item " id='searchUser'><s:a
												action="searchUser" class="nav-link">
												<span class="sidebar-mini"> UL </span>
												<span class="sidebar-normal"> User List </span>
											</s:a></li>
										<li class="nav-item " id='loginHistoryRedirectUser'><s:a
												action='loginHistoryRedirectUser' class="nav-link">
												<span class="sidebar-mini"> ULH </span>
												<span class="sidebar-normal"> User Login History </span>
											</s:a></li>


									</ul>
								</div></li>
						</div> --%>

	
						<div class="card_sidebar">
							<li class="nav-item " id="headingThree"><a class="nav-link"
								data-toggle="collapse" data-target="#myAccount"
								href="#myAccount" id="a-myAccount"> <i
									class="material-icons">person</i>
									<p>
										My Account <b class="caret"></b>
									</p>
							</a>
								<div class="collapse" id="myAccount"
									aria-labelledby="headingThree" data-parent="#accordionExample">
									<ul class="nav">
										<li class="nav-item " id='merchantProfile'><s:a
												action="merchantProfile" class="nav-link">
												<span class="sidebar-mini"> MP </span>
												<span class="sidebar-normal"> My Profile</span>
											</s:a></li>
										<li class="nav-item " id="loginHistoryRedirect"><s:a
												action="loginHistoryRedirect" class="nav-link">
												<span class="sidebar-mini"> LH </span>
												<span class="sidebar-normal"> Login History</span>
											</s:a></li>
										<li class="nav-item " id="passwordChange"><s:a
												action='passwordChange' class="nav-link">
												<span class="sidebar-mini"> CP </span>
												<span class="sidebar-normal"> Change Password</span>
											</s:a></li>
									</ul>
								</div></li>
						</div>
					</div>
				</ul>
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
						<!-- <a class="navbar-brand" href="#">Dashboard</a> -->
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
									class="fa fa-user fa-fw"></i><b id="welcomeHeading">Welcome <s:property
											value="%{#session.USER.businessName}" /></a>
								<div class="dropdown-menu dropdown-menu-right"
									aria-labelledby="navbarDropdownProfile">
									<s:a action="logout"> Logout</s:a>
									<!-- <a class="dropdown-item" href="#">Profile</a>
								<a class="dropdown-item" href="#">Settings</a> -->
									<!-- <div class="dropdown-divider"></div> -->

								</div></li>
						</ul>
					</div>
				</div>
			</nav>


			<div class="row">
				<div class="col-md-12 text-left" id="breadcrump"
					style="margin-top: 5px; margin-left: 20px;">
					<a href="home" class="newredtxt"><i class="material-icons"
						style="height: 30px;">home</i>Home</a> | <a
						href="javascript:window.history.back();" class="newredtxt"><i
						class="material-icons" style="height: 30px;">arrow_back</i>Back</a>
				</div>
			</div>

			<div class="content">
				<div class="content">
					<div class="container-fluid">
						<decorator:body />
					</div>
				</div>
				<%@ include file="/jsp/footer.jsp"%>
			</div>
			
		</div>
	</div>

</body>

<script>
	var currentUrl = window.location.href.split('/');
	$('#MenuUl > li').removeClass('active');
	if (currentUrl.includes("home")) {
		$('#home').addClass('active');
	} else if (currentUrl.includes("agentSearch")) {
		$('#agentSearch').addClass('active');
	} else if (currentUrl.includes("transactionSearchReseller")) {
		$('#transactionSearchReseller').addClass('active');
	} else if (currentUrl.includes("addUser")
			|| currentUrl.includes("searchUser")
			|| currentUrl.includes("loginHistoryRedirectUser")) {
		$('#manageUsers').addClass('show');
		$('#a-manageUsers').attr('aria-expanded', true);
		if (currentUrl.includes("addUser")) {
			$('#addUser').addClass('active');
		} else if (currentUrl.includes("searchUser")) {
			$('#searchUser').addClass('active');
		} else if (currentUrl.includes("transactionSearch")) {
			$('#loginHistoryRedirectUser').addClass('active');
		} else if (currentUrl.includes("loginHistoryRedirectUser")) {
			$('#loginHistoryRedirectUser').addClass('active');
		}
	} else if (currentUrl.includes("merchantProfile")
			|| currentUrl.includes("loginHistoryRedirect")
			|| currentUrl.includes("passwordChange")) {
		$('#myAccount').addClass('show');
		$('#a-myAccount').attr('aria-expanded', true);
		if (currentUrl.includes("merchantProfile")) {
			$('#merchantProfile').addClass('active');
		} else if (currentUrl.includes("loginHistoryRedirect")) {
			$('#loginHistoryRedirect').addClass('active');
		} else if (currentUrl.includes("transactionSearch")) {
			$('#passwordChange').addClass('active');
		} else if (currentUrl.includes("passwordChange")) {
			$('#passwordChange').addClass('active');
		}
	} else if (currentUrl.includes("saleTransactionSearch")
			|| currentUrl.includes("refundTransactionSearch")
			|| currentUrl.includes("settledTransactionSearch")
			|| currentUrl.includes("summaryReports")
			|| currentUrl.includes("downloadTransactionsReport")) {
		$('#transReports').addClass('show');
		$('#a-transReports').attr('aria-expanded', true);
		if (currentUrl.includes("saleTransactionSearch")) {
			$('#saleTransactionSearch').addClass('active');
		} else if (currentUrl.includes("refundTransactionSearch")) {
			$('#refundTransactionSearch').addClass('active');
		} else if (currentUrl.includes("settledTransactionSearch")) {
			$('#settledTransactionSearch').addClass('active');
		} else if (currentUrl.includes("summaryReports")) {
			$('#summaryReports').addClass('active');
		} else if (currentUrl.includes("transactionSearch")) {
			$('#downloadTransactionsReport').addClass('active');
		} else if (currentUrl.includes("downloadTransactionsReport")) {
			$('#downloadTransactionsReport').addClass('active');
		}
	} else if (currentUrl.includes("singleInvoicePage") || currentUrl.includes("promotionalInvoicePage") || currentUrl.includes("invoiceSearch")) {
			$('#quickPay').addClass('show');
			$('#a-quickPay').attr('aria-expanded', true);
			if (currentUrl.includes("singleInvoicePage")) {
				$('#singleInvoicePage').addClass('active');
			} else if (currentUrl.includes("promotionalInvoicePage")) {
				$('#promotionalInvoicePage').addClass('active');
			} else if (currentUrl.includes("home")) {
				$('#invoiceSearch').addClass('active');
			} else if (currentUrl.includes("invoiceSearch")) {
				$('#invoiceSearch').addClass('active');
			}
	}else if (currentUrl.includes("addBeneficiary") || currentUrl.includes("beneficiaryList") || currentUrl.includes("merchantnodalTransactions") || currentUrl.includes("nodalTransactionsHistory")) {
			$('#nodalSettlement').addClass('show');
			$('#a-nodalSettlement').attr('aria-expanded', true);
			if (currentUrl.includes("addBeneficiary")) {
				$('#addBeneficiary').addClass('active');
			} else if (currentUrl.includes("beneficiaryList")) {
				$('#beneficiaryList').addClass('active');
			} else if (currentUrl.includes("merchantnodalTransactions")) {
				$('#merchantnodalTransactions').addClass('active');
			} else if (currentUrl.includes("home")) {
				$('#nodalTransactionsHistory').addClass('active');
			}else if (currentUrl.includes("nodalTransactionsHistory")) {
				$('#nodalTransactionsHistory').addClass('active');
			}
		}else if (currentUrl.includes("merchantRestrictionAction")) {
			$('#fraudPrevention').addClass('show');
			$('#a-fraudPrevention').attr('aria-expanded', true);
			if (currentUrl.includes("merchantRestrictionAction")) {
				$('#merchantRestrictionAction').addClass('active');
			}
	}else if (currentUrl.includes("monthlyInvoiceReports")) {
		$('#accountsFinance').addClass('show');
		$('#a-accountsFinance').attr('aria-expanded', true);
		if (currentUrl.includes("monthlyInvoiceReports")) {
			$('#monthlyInvoiceReports').addClass('active');
		}
	}
</script>

<!-- /. WRAPPER  -->
<!-- JS Scripts-->
<!-- jQuery Js -->




