<%@taglib prefix="s" uri="/struts-tags"%>
<link rel="icon" href="../image/98x98.png">
<!-- <link href="../css/bootstrap.min.css" rel="stylesheet"> -->
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
<script type="text/javascript">
	jQuery().ready(
		function () {
			// simple accordion
			jQuery('#list1a').accordion();
			jQuery('#list1b').accordion({
				autoheight: false
			});

			// second simple accordion with special markup
			//Accodrian collapse and merge code
			jQuery('#navigation').accordion({
				active: true,
				header: '.head',
				navigation: true,
				event: 'click',
				fillSpace: false,
				animated: 'easeslide',
				collapsible: true
			});

			jQuery('#sub-accordian').accordion({
				active: true,
				header: '.head2',
				navigation: true,
				event: 'click',
				fillSpace: false,
				animated: 'easeslide',
				collapsible: true
			});
			jQuery('#sub-accordian2').accordion({
				active: true,
				header: '.head3',
				navigation: true,
				event: 'click',
				fillSpace: false,
				animated: 'easeslide',
				collapsible: true
			});

			/* 	var notificationVal = document
						.getElementsByName("notification")[0].value;
				var notificationSpan = document
						.getElementById("notificationSpan");
				notificationSpan.innerHTML = notificationVal; */
			/*  myFunction(); */

		});

	$("#accordion > li > div").click(function () {

		if (false == $(this).next().is(':visible')) {
			$('#accordion ul').slideUp(300);
		}
		$(this).next().slideToggle(300);
	});

	function myFunction() {
		var token = document.getElementsByName("token")[0].value;
		var payId = '<s:property value="#session.USER.payId" />';
		var dropdown = document.getElementById("dropdownMenu");
		/* if($('#dropdownMenu > option[value!=""]').length == 0) { */
		//dropdown contains no non-null options
		$('#dropdownMenu').val('');
		$.ajax({
			"url": "notificationHandler",
			"type": "POST",
			"data": {
				payId: payId,
				token: token,

			},
			success: function (data) {
				/* dropdown.selectedIndex = -1; */
				var notificationMessage = data.message;
				/* var notificationSpan=	data.count;
				var notificationCount=	document.getElementById("notificationSpan");
				notificationCount.innerHTML=notificationSpan; */
				var jsArr = new Array();
				var unreadId = new Array();
				/* var notification = document
						.getElementById("notificationMessage");
				notification.innerHTML = notificationMessage; */
				jsArr = data.messageList;
				unreadId = data.unreadNotification;
				var dropdown = document.getElementById("dropdownMenu");
				/* var size = dropdown.length;
				var value = dropdown.innerHTML;
				/* var length = dropdown.length; */
				/* 	var length2 = dropdown.option.length; */
				/* for (var i = 0; i < length; i++) {
					
					     selectobject.remove(i);
					  } */
				/*  select.options[i] = null; */



				for (var i = 0; i <= jsArr.length; i++) {

					var option = document.createElement("option");
					// set the text that displays for the option
					option.innerHTML = jsArr[i];
					// add the option to your select dropdown
					dropdown.appendChild(option);

				}
				readNotification(unreadId);
				/* window.location.reload(); */


			},
			error: function (data) {
				alert("Something went wrong, so please try again.");
			}
		});
		/* } */
	}
	function readNotification(unreadId) {
		$.ajax({
			"url": "readNotification",
			"type": "POST",
			"data": {
				message: JSON.stringify(unreadId),
				response: "test"
			},
			success: function (data) {
				var response = data.response;

				alert(response);

				/* var notificationVal = document
		  .getElementsByName("notification")[0].value; */
				var notificationSpan = document
					.getElementById("notificationSpan");
				notificationSpan.innerHTML = 0;
			},
			error: function (data) {

				alert("Something went wrong, so please try again.");
			}
		});
	}
</script>
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

						<li class="nav-item  " id="home">
							<s:a action='home' class="nav-link">
								<i class="material-icons">dashboard</i>
								<p> Dashboard </p>
							</s:a>

						</li>
						<div class="card_sidebar">
							<li class="nav-item " id="headingOne">
								<a style="cursor: pointer" class="nav-link" data-toggle="collapse"
									data-target="#timeline" href="#timeline" id='a-timeline'>
									<i class="material-icons">timeline</i>
									<p> Analytics
										<b class="caret"></b>
									</p>
								</a>

								<div class="collapse" id="timeline" aria-labelledby="headingOne"
									data-parent="#accordionExample">
									<ul class="nav">
										<li class="nav-item " id="analyticsPerfomanceReport">
											<s:a class="nav-link" action='analyticsPerfomanceReport'>
												<span class="sidebar-mini"> PR </span>
												<span class="sidebar-normal"> Performance Report </span>
											</s:a>
										</li>
										<li class="nav-item " id="analyticsRevenue">
											<s:a action='analyticsRevenue' class="nav-link">
												<span class="sidebar-mini"> RR </span>
												<span class="sidebar-normal"> Revenue Report </span>
											</s:a>
										</li>

									</ul>
								</div>
							</li>
						</div>
										
						<div class="card_sidebar">
							<li class="nav-item " id="headingTwo">
								<a class="nav-link" data-toggle="collapse" data-target="#merchantsetup"
									href="#merchantsetup" id="a-merchantsetup">
									<i class="material-icons">account_balance_wallet</i>
									<p> Merchant Setup
										<b class="caret"></b>
									</p>
								</a>
								<div class="collapse" id="merchantsetup" aria-labelledby="headingTwo"
									data-parent="#accordionExample">
									<ul class="nav">

										<li class="nav-item" id="merchantCrmSignup">
											<s:a action='merchantCrmSignup' class="nav-link">
												<span class="sidebar-mini"> US </span>
												<span class="sidebar-normal"> User Registration </span>
											</s:a>
										</li>
										
										<li class="nav-item " id="addCompanyProfile">
											<s:a action='addCompanyProfile' class="nav-link">
												<span class="sidebar-mini"> ACP </span>
												<span class="sidebar-normal">Add Company Profile</span>
											</s:a>
										</li>
										
										<li class="nav-item " id="merchantList">
											<s:a action='merchantList' class="nav-link">
												<span class="sidebar-mini"> MA </span>
												<span class="sidebar-normal"> Merchant Account </span>
											</s:a>
										</li>
										<li class="nav-item " id="ruleEngine">
											<s:a action='ruleEngine' class="nav-link">
												<span class="sidebar-mini"> RE </span>
												<span class="sidebar-normal"> Rule Engine </span>
											</s:a>
										</li>
										<li class="nav-item " id="routerConfigurationAction">
											<s:a action='routerConfigurationAction' class="nav-link">
												<span class="sidebar-mini"> SR </span>
												<span class="sidebar-normal"> Smart Router </span>
											</s:a>
										</li>
										<li class="nav-item " id="smartRouterAudit">
											<s:a action='smartRouterAudit' class="nav-link">
												<span class="sidebar-mini"> SRAT </span>
												<span class="sidebar-normal"> Smart Router Audit Trail </span>
											</s:a>
										</li>
										<li class="nav-item " id="ruleEngineAudit">
											<s:a action='ruleEngineAudit' class="nav-link">
												<span class="sidebar-mini"> REAT </span>
												<span class="sidebar-normal"> Rule Engine Audit Trail </span>
											</s:a>
										</li>

									</ul>
								</div>
							</li>
						</div>

						<div class="card_sidebar">
							<li class="nav-item " id="headingThree">
								<a class="nav-link" data-toggle="collapse" data-target="#merchantbilling"
									href="#merchantbilling" id="a-merchantbilling">
									<i class="material-icons">account_balance_wallet</i>
									<p> Merchant Billing
										<b class="caret"></b>
									</p>
								</a>
								<div class="collapse" id="merchantbilling" aria-labelledby="headingThree"
									data-parent="#accordionExample">
									<ul class="nav">
										<li class="nav-item " id="mopSetUpAction">
											<s:a action='mopSetUpAction' class="nav-link">
												<span class="sidebar-mini"> MP </span>
												<span class="sidebar-normal"> Merchant Mapping </span>
											</s:a>
										</li>
										<li class="nav-item " id="serviceTaxPlatform">
											<s:a action='serviceTaxPlatform' class="nav-link">
												<span class="sidebar-mini"> GST </span>
												<span class="sidebar-normal"> GST </span>
											</s:a>
										</li>
										<li class="nav-item " id="chargingPlatform">
											<s:a action='chargingPlatform' class="nav-link">
												<span class="sidebar-mini"> TS </span>
												<span class="sidebar-normal"> TDR Setting </span>
											</s:a>
										</li>
										<li class="nav-item " id="surchargePlatform">
											<s:a action='surchargePlatform' class="nav-link">
												<span class="sidebar-mini"> SS </span>
												<span class="sidebar-normal"> Surcharge Setting </span>
											</s:a>
										</li>
										<li class="nav-item " id="pendingRequest">
											<s:a action='pendingRequest' class="nav-link">
												<span class="sidebar-mini"> PR </span>
												<span class="sidebar-normal"> Pending Request </span>
											</s:a>
										</li>
									</ul>
								</div>
							</li>
						</div>
						
						
						<div class="card_sidebar">
							<li class="nav-item " id="headingSeventeen">
								<a class="nav-link" data-toggle="collapse" data-target="#reseller" href="#reseller"
									id="a-reseller">
									<i class="material-icons">list_alt</i>
									<p> Reseller Setup
										<b class="caret"></b>
									</p>
								</a>
								<div class="collapse" id="reseller" aria-labelledby="headingSeventeen"
									data-parent="#accordionExample">
									<ul class="nav">
										<li class="nav-item " id="resellerList">
											<s:a action='resellerList' class="nav-link">
												<span class="sidebar-mini"> RL </span>
												<span class="sidebar-normal"> Reseller List </span>
											</s:a>
										</li>
										<li class="nav-item " id="resellerMerchantList">
											<s:a action='resellerMerchantList' class="nav-link">
												<span class="sidebar-mini"> RM </span>
												<span class="sidebar-normal"> Reseller Merchants </span>
											</s:a>
										</li>

									</ul>
								</div>
							</li>
						</div>
						
						
						
						<div class="card_sidebar">
							<li class="nav-item " id="headingFour">
								<a class="nav-link" data-toggle="collapse" data-target="#viewconfig" href="#viewconfig"
									id="a-viewconfig">
									<i class="material-icons">list_alt</i>
									<p> View Configuration
										<b class="caret"></b>
									</p>
								</a>
								<div class="collapse" id="viewconfig" aria-labelledby="headingFour"
									data-parent="#accordionExample">
									<ul class="nav">
										<li class="nav-item " id="viewSurchargeReport">
											<s:a action='viewSurchargeReport' class="nav-link">
												<span class="sidebar-mini"> VS </span>
												<span class="sidebar-normal"> View Surcharge </span>
											</s:a>
										</li>
										<li class="nav-item " id="smartRouterConfig">
											<s:a action='smartRouterConfig' class="nav-link">
												<span class="sidebar-mini"> SRC </span>
												<span class="sidebar-normal"> Smart Router Configuration </span>
											</s:a>
										</li>

									</ul>
								</div>
							</li>
						</div>
						<div class="card_sidebar">
							<li class="nav-item  " id="transactionSearchAdmin">
								<s:a action='transactionSearchAdmin' class="nav-link">
									<i class="material-icons">search</i>
									<p> Search Payments </p>
								</s:a>

							</li>
						</div>

						<div class="card_sidebar">
							<li class="nav-item " id="headingFive">
								<a class="nav-link" data-toggle="collapse" data-target="#transReports"
									href="#transReports" id="a-transReports">
									<i class="material-icons">bar_chart</i>
									<p> Transaction Reports
										<b class="caret"></b>
									</p>
								</a>
								<div class="collapse" id="transReports" aria-labelledby="headingFive"
									data-parent="#accordionExample">
									<ul class="nav">
										<li class="nav-item " id="saleTransactionSearch">
											<s:a action='saleTransactionSearch' class="nav-link">
												<span class="sidebar-mini"> SC </span>
												<span class="sidebar-normal"> Sale Captured </span>
											</s:a>
										</li>
										<li class="nav-item " id="refundTransactionSearch">
											<s:a action='refundTransactionSearch' class="nav-link">
												<span class="sidebar-mini"> RC </span>
												<span class="sidebar-normal"> Refund Captured </span>
											</s:a>
										</li>
										<li class="nav-item " id="settledTransactionSearch">
											<s:a action='settledTransactionSearch' class="nav-link">
												<span class="sidebar-mini"> S </span>
												<span class="sidebar-normal"> Settled Report</span>
											</s:a>
										</li>
										<li class="nav-item " id="SmartRouter">
											 <s:a action='SmartRouter' class="nav-link">
												<span class="sidebar-mini"> SR </span>
												<span class="sidebar-normal"> SmartRouter Report </span>
											</s:a>
										</li>
										<li class="nav-item " id="summaryReportsAdmin">
											<s:a action='summaryReportsAdmin' class="nav-link">
												<span class="sidebar-mini"> SR </span>
												<span class="sidebar-normal"> Summary Report </span>
											</s:a>
										</li>
										<li class="nav-item " id="downloadTransactionsReport">
											<s:a action='downloadTransactionsReport' class="nav-link">
												<span class="sidebar-mini"> DR </span>
												<span class="sidebar-normal"> Download Report </span>
											</s:a>
										</li>

									</ul>
								</div>
							</li>
						</div>
						
						<div class="card_sidebar">	
					 <li class="nav-item " id="headingFourteen">
						<a class="nav-link" data-toggle="collapse" data-target="#quickPay" href="#quickPay" id="a-quickPay">
							<i class="material-icons">apps</i>
							<p> Quick Pay Invoice
								<b class="caret"></b>
							</p>
						</a>
						<div class="collapse" id="quickPay" aria-labelledby="headingFourteen"
						data-parent="#accordionExample">
							<ul class="nav">
								<li class="nav-item " id="singleInvoicePage">
									<s:a action="singleInvoicePage" class="nav-link">
										<span class="sidebar-mini"> SI </span>
										<span class="sidebar-normal"> Single Invoice </span>
									</s:a>
								</li>
								<li class="nav-item " id="promotionalInvoicePage">
									<s:a action="promotionalInvoicePage" class="nav-link">
										<span class="sidebar-mini"> PI </span>
										<span class="sidebar-normal"> Promotional Invoice </span>
									</s:a>
								</li>
								 
								<li class="nav-item " id="invoiceSearch">
									<s:a action="invoiceSearch" class="nav-link">
										<span class="sidebar-mini"> IS </span>
										<span class="sidebar-normal"> Invoice Search </span>
									</s:a>
								</li>
							</ul>
						</div>
					</li> 
						</div>
				
						
						<div class="card_sidebar">
							<li class="nav-item " id="headingSix">
								<a class="nav-link" data-toggle="collapse" data-target="#batchOperation"
									href="#batchOperation" id="a-batchOperation">
									<i class="material-icons">apps</i>
									<p> Batch Operation
										<b class="caret"></b>
									</p>
								</a>
								<div class="collapse" id="batchOperation" aria-labelledby="headingSix"
									data-parent="#accordionExample">
									<ul class="nav">
										<li class="nav-item " id="manageBinRange">
											<s:a action="manageBinRange" class="nav-link">
												<span class="sidebar-mini"> MBR </span>
												<span class="sidebar-normal"> Manage Bin Ranges </span>
											</s:a>
										</li>
										<li class="nav-item " id="verifyTransactionData">
											<s:a action="verifyTransactionData" class="nav-link">
												<span class="sidebar-mini"> VTD </span>
												<span class="sidebar-normal"> Verify Transaction Data </span>
											</s:a>
										</li>
									</ul>
								</div>
							</li>
						</div>

						<div class="card_sidebar">
							<li class="nav-item " id="headingSeven">
								<a class="nav-link" data-toggle="collapse" data-target="#fraudPrevention"
									href="#fraudPrevention" id="a-fraudPrevention">
									<i class="material-icons">location_disabled</i>
									<p> Fraud Prevention
										<b class="caret"></b>
									</p>
								</a>
								<div class="collapse" id="fraudPrevention" aria-labelledby="headingSeven"
									data-parent="#accordionExample">
									<ul class="nav">
										<li class="nav-item " id="adminRestrictions">
											<s:a action="adminRestrictions" class="nav-link">
												<span class="sidebar-mini"> R </span>
												<span class="sidebar-normal"> Restrictions</span>
											</s:a>
										</li>
										<li class="nav-item " id="merchantFpsAccessAction">
											<s:a action="merchantFpsAccessAction" class="nav-link">
												<span class="sidebar-mini"> FPS </span>
												<span class="sidebar-normal"> Merchant FPS </span>
											</s:a>
										</li>
									</ul>
								</div>
							</li>
						</div>
						<div class="card_sidebar">
							<li class="nav-item " id="headingEight">
								<a class="nav-link" data-toggle="collapse" data-target="#manageUsers"
									href="#manageUsers" id="a-manageUsers">
									<i class="material-icons">people</i>
									<p> Manage Users
										<b class="caret"></b>
									</p>
								</a>
								<div class="collapse" id="manageUsers" aria-labelledby="headingEight"
									data-parent="#accordionExample">
									<ul class="nav">
										<li class="nav-item " id="addSubAdmin">
											<s:a action="addSubAdmin" class="nav-link">
												<span class="sidebar-mini"> ASA </span>
												<span class="sidebar-normal"> Add Sub-Admin </span>
											</s:a>
										</li>
										<li class="nav-item " id="searchSubAdmin">
											<s:a action="searchSubAdmin" class="nav-link">
												<span class="sidebar-mini"> SAL </span>
												<span class="sidebar-normal"> Sub-Admin List </span>
											</s:a>
										</li>
										<li class="nav-item " id="merchantSubUsers">
											<s:a action='merchantSubUsers' class="nav-link">
												<span class="sidebar-mini"> MSU </span>
												<span class="sidebar-normal"> Merchant Sub-Users </span>
											</s:a>
										</li>
										<li class="nav-item " id="addAgent">
											<s:a action="addAgent" class="nav-link">
												<span class="sidebar-mini"> AA </span>
												<span class="sidebar-normal"> Add Agent </span>
											</s:a>
										</li>
										<li class="nav-item " id="searchAgent">
											<s:a action="searchAgent" class="nav-link">
												<span class="sidebar-mini"> AL </span>
												<span class="sidebar-normal"> Agent List </span>
											</s:a>
										</li>

									</ul>
								</div>
							</li>
						</div>
						
						<!-- Chargeback -->
						<div class="card_sidebar">
							<li class="nav-item " id="headingOne">
								<a style="cursor: pointer" class="nav-link" data-toggle="collapse"
									data-target="#chargeback" href="#chargeback" id='a-chargeback'>
									<i class="material-icons">list_alt</i>
									<p> Chargeback
										<b class="caret"></b>
									</p>
								</a>

								<div class="collapse" id="chargeback" aria-labelledby="headingOne"
									data-parent="#accordionExample">
									<ul class="nav">
										<li class="nav-item " id="createChargebackPage">
											<s:a class="nav-link" action='createChargebackPage'>
												<span class="sidebar-mini"> CC </span>
												<span class="sidebar-normal"> Create Chargeback</span>
											</s:a>
										</li>
										<li class="nav-item " id="viewChargeback">
											<s:a action='viewChargeback' class="nav-link">
												<span class="sidebar-mini"> VC </span>
												<span class="sidebar-normal"> View Chargeback </span>
											</s:a>
										</li>

									</ul>
								</div>
							</li>
						</div>
						<!-- Chargeback ends -->
						
						
						<div class="card_sidebar">
							<li class="nav-item " id="headingEight">
								<a class="nav-link" data-toggle="collapse" data-target="#manageAquirer"
									href="#manageAquirer" id="a-manageAquirer">
									<i class="material-icons">people</i>
									<p> Manage Aquirers
										<b class="caret"></b>
									</p>
								</a>
								<div class="collapse" id="manageAquirer" aria-labelledby="headingEight"
									data-parent="#accordionExample">
									<ul class="nav">
										<li class="nav-item " id="addAcquirer">
											<s:a action="addAcquirer" class="nav-link">
												<span class="sidebar-mini"> AA </span>
												<span class="sidebar-normal"> Add Aquirers </span>
											</s:a>
										</li>

										<li class="nav-item " id="searchAcquirer">
											<s:a action="searchAcquirer" class="nav-link">
												<span class="sidebar-mini"> AL </span>
												<span class="sidebar-normal"> Aquirers List </span>
											</s:a>
										</li>

									</ul>
								</div>
							</li>
						</div>
						<div class="card_sidebar">
							<li class="nav-item " id="headingNine">
								<a class="nav-link" data-toggle="collapse" data-target="#accessAgent"
									href="#accessAgent" id="a-accessAgent">
									<i class="material-icons">person</i>
									<p> Agent Access
										<b class="caret"></b>
									</p>
								</a>
								<div class="collapse" id="accessAgent" aria-labelledby="headingNine"
									data-parent="#accordionExample">
									<ul class="nav">
										<li class="nav-item " id="agentSearch">
											<s:a action='agentSearch' class="nav-link">
												<span class="sidebar-mini"> AS </span>
												<span class="sidebar-normal"> Agent Search</span>
											</s:a>
										</li>
									</ul>
								</div>
							</li>
						</div>
						<div class="card_sidebar">
							<li class="nav-item " id="headingTen">
								<a class="nav-link" data-toggle="collapse" data-target="#accountsFinance"
									href="#accountsFinance" id="a-accountsFinance">
									<i class="material-icons">monetization_on</i>
									<p> Accounts & Finance
										<b class="caret"></b>
									</p>
								</a>
								<div class="collapse" id="accountsFinance" aria-labelledby="headingTen"
									data-parent="#accordionExample">
									<ul class="nav">
										<li class="nav-item " id="misReports">
											<s:a action='misReports' class="nav-link">
												<span class="sidebar-mini"> MIS </span>
												<span class="sidebar-normal"> MIS Report</span>
											</s:a>
										</li>
										<li class="nav-item " id="merchantRecoReports">
											<s:a action='merchantRecoReports' class="nav-link">
												<span class="sidebar-mini"> MRR </span>
												<span class="sidebar-normal"> Merchant Reco Report</span>
											</s:a>
										</li>

										<li class="nav-item " id="mprUploadAction">
                                        	<s:a action='displayUploadUI' class="nav-link">
                                        	    <span class="sidebar-mini"> RFU </span>
                                        	    <span class="sidebar-normal"> Reco File Upload</span>
                                        	    </s:a>
                                        	</li>


										<li class="nav-item " id="monthlyInvoiceReports">
											<s:a action='monthlyInvoiceReports' class="nav-link">
												<span class="sidebar-mini"> MIR </span>
												<span class="sidebar-normal"> Monthly Invoice Report</span>
											</s:a>
										</li>
									</ul>
								</div>
							</li>
						</div>
						<%-- <div class="card_sidebar">
							<li class="nav-item " id="headingEleven">
								<a class="nav-link" data-toggle="collapse" data-target="#nodalSettlement"
									href="#nodalSettlement" id="a-nodalSettlement">
									<i class="material-icons">timeline</i>
									<p> Nodal Settlement
										<b class="caret"></b>
									</p>
								</a>
								<div class="collapse" id="nodalSettlement" aria-labelledby="headingEleven"
									data-parent="#accordionExample">
									<ul class="nav">
										<li class="nav-item " id="addBeneficiary">
											<s:a action='addBeneficiary' class="nav-link">
												<span class="sidebar-mini"> AB </span>
												<span class="sidebar-normal"> Add Beneficiary</span>
											</s:a>
										</li>
										<li class="nav-item " id="beneficiaryList">
											<s:a action='beneficiaryList' class="nav-link">
												<span class="sidebar-mini"> BL </span>
												<span class="sidebar-normal"> Beneficiary List</span>
											</s:a>
										</li>
										<li class="nav-item " id="nodalTransactions">
											<s:a action='nodalTransactions' class="nav-link">
												<span class="sidebar-mini"> AFT </span>
												<span class="sidebar-normal"> Automated Fund Transfer</span>
											</s:a>
										</li>
										<li class="nav-item " id="nodalTransactionsHistory"> 
											<s:a action='nodalTransactionsHistory' class="nav-link"
												>
												<span class="sidebar-mini"> TH </span>
												<span class="sidebar-normal"> Transaction history</span>
											</s:a>
										</li>
										<!-- <li class="nav-item " id="ledger">
											<s:a action='ledger' class="nav-link">
												<span class="sidebar-mini"> LN </span>
												<span class="sidebar-normal"> Ledger Nodal</span>
											</s:a>
										</li> -->
									</ul>
								</div>
							</li>
						</div> --%>
						<div class="card_sidebar">
							<li class="nav-item " id="headingFifteen">
								<a class="nav-link" data-toggle="collapse" data-target="#transferFunds"
									href="#transferFunds" id="a-transferFunds">
									<i class="material-icons">money</i>
									<p>Merchant Payout
										<b class="caret"></b>
									</p>
								</a>
								<div class="collapse" id="transferFunds" aria-labelledby="headingFifteen"
									data-parent="#accordionExample">
									<ul class="nav">
										
										<li class="nav-item " id="merchantNodalTransactions">
											<s:a action='merchantNodalTransactions' class="nav-link">
												<span class="sidebar-mini"> P </span>
												<span class="sidebar-normal"> Payout</span>
											</s:a>
										</li>
										<li class="nav-item " id="nodalTransactionsHistory"> 
											<s:a action='nodalTransactionsHistory' class="nav-link"
												>
												<span class="sidebar-mini"> TH </span>
												<span class="sidebar-normal"> Transaction history</span>
											</s:a>
										</li>
										
									</ul>
								</div>
							</li>
						</div>
						<div class="card_sidebar">
							<li class="nav-item " id="headingTwelve">
								<a class="nav-link" data-toggle="collapse" data-target="#notifyEngine"
									href="#notifyEngine" id="a-notifyEngine">
									<i class="material-icons">message</i>
									<p> Notification Engine
										<b class="caret"></b>
									</p>
								</a>
								<div class="collapse" id="notifyEngine" aria-labelledby="headingTwelve"
									data-parent="#accordionExample">
									<ul class="nav">
										<li class="nav-item " id="txnSmsSender">
											<s:a action='txnSmsSender' class="nav-link">
												<span class="sidebar-mini"> TS </span>
												<span class="sidebar-normal"> Transaction SMS</span>
											</s:a>
										</li>
										<li class="nav-item " id="schedularSettings">
											<s:a action='schedularSettings' class="nav-link">
												<span class="sidebar-mini"> SS </span>
												<span class="sidebar-normal"> Schedular Settings</span>
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
										<li class="nav-item " id="adminProfile">
											<s:a action="adminProfile" class="nav-link">
												<span class="sidebar-mini"> MP </span>
												<span class="sidebar-normal"> My Profile</span>
											</s:a>
										</li>
										<li class="nav-item " id="loginHistoryRedirect">
											<s:a action="loginHistoryRedirect" class="nav-link">
												<span class="sidebar-mini"> LH </span>
												<span class="sidebar-normal"> Login History</span>
											</s:a>
										</li>
										<li class="nav-item  " id="passwordChange">
											<s:a action='passwordChange' class="nav-link">
												<span class="sidebar-mini"> CP </span>
												<span class="sidebar-normal"> Change Password</span>
											</s:a>
										</li>
										<li class="nav-item  " id="merchantResetPassword">
											<s:a action='merchantResetPasswordPageAction' class="nav-link">
												<span class="sidebar-mini"> RMP </span>
												<span class="sidebar-normal">Reset Merchant password</span>
											</s:a>
										</li>
									</ul>
								</div>
							</li>
						</div>
						<div class="card_sidebar">
							<li class="nav-item " id="headingThirteen">
								<a class="nav-link" data-toggle="collapse" data-target="#auditTrail" href="#auditTrail"
									id="a-auditTrail">
									<i class="material-icons">Audit Trail</i>
									<p> Audit Trail
										<b class="caret"></b>
									</p>
								</a>
								<div class="collapse" id="auditTrail" aria-labelledby="headingThirteen"
									data-parent="#accordionExample">
									<ul class="nav">
										<li class="nav-item " id="auditTrailReport">
											<s:a action="auditTrailsReport" class="nav-link">
												<span class="sidebar-mini"> AR </span>
												<span class="sidebar-normal"> Audit Trail Report</span>
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
						<!-- <a class="navbar-brand" href="#">Dashboard</a> -->
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
								<a class="nav-link" href="#pablo" id="navbarDropdownProfile" data-toggle="dropdown"
									aria-haspopup="true" aria-expanded="false">
									<i class="fa fa-user fa-fw"></i><b id="welcomeHeading">Welcome <s:property value="%{#session.USER.businessName}" />
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
					  
					  
					  <div class="row" >
						  <div class="col-md-12 text-left" id="breadcrump" style="margin-top:5px;margin-left:20px;"> 
							  <a href="home" class="newredtxt">
								  <i class="material-icons" style="height: 30px;">home</i>Home</a> | <a href="javascript:window.history.back();" class="newredtxt">
									  <i class="material-icons" style="height: 30px;">arrow_back</i>Back</a>
									</div>
								</div> 
<!-- 	
					  <div class="content"> -->
						<div class="content">
						  <div class="container-fluid">
			<decorator:body />
			</div>
			</div>
			<!-- </div>  -->
            <%@ include file="/jsp/footer.jsp"%> 
        </div>
	</div>
	</body>
	<script>
		var currentUrl = window.location.href.split('/');
		$('#MenuUl > li').removeClass('active');
		if (currentUrl.includes("home")) {
			$('#home').addClass('active');
		} else if (currentUrl.includes("addSubAdmin") || currentUrl.includes("searchSubAdmin") || currentUrl.includes("merchantSubUsers") || currentUrl.includes("addAgent") || currentUrl.includes("searchAgent")) {
			$('#manageUsers').addClass('show');
			$('#a-manageUsers').attr('aria-expanded', true);
			if (currentUrl.includes("addSubAdmin")) {
				$('#addSubAdmin').addClass('active');
			} else if (currentUrl.includes("searchSubAdmin")) {
				$('#searchSubAdmin').addClass('active');
			} else if (currentUrl.includes("merchantSubUsers")) {
				$('#merchantSubUsers').addClass('active');
			} else if (currentUrl.includes("addAgent")) {
				$('#addAgent').addClass('active');
			} else if (currentUrl.includes("home")) {
				$('#searchAgent').addClass('active');
			} else if (currentUrl.includes("searchAgent")) {
				$('#searchAgent').addClass('active');
			}
		} else if (currentUrl.includes("adminProfile") || currentUrl.includes("loginHistoryRedirect") || currentUrl.includes("passwordChange")) {
			$('#myAccount').addClass('show');
			$('#a-myAccount').attr('aria-expanded', true);
			if (currentUrl.includes("adminProfile")) {
				$('#adminProfile').addClass('active');
			} else if (currentUrl.includes("loginHistoryRedirect")) {
				$('#loginHistoryRedirect').addClass('active');
			} else if (currentUrl.includes("home")) {
				$('#passwordChange').addClass('active');
			} else if (currentUrl.includes("passwordChange")) {
				$('#passwordChange').addClass('active');
			}
		} else if (currentUrl.includes("saleTransactionSearch") || currentUrl.includes("refundTransactionSearch") || currentUrl.includes("settledTransactionSearch") || currentUrl.includes("summaryReportsAdmin") || currentUrl.includes("downloadTransactionsReport")) {
			$('#transReports').addClass('show');
			$('#a-transReports').attr('aria-expanded', true);
			if (currentUrl.includes("saleTransactionSearch")) {
				$('#saleTransactionSearch').addClass('active');
			} else if (currentUrl.includes("refundTransactionSearch")) {
				$('#refundTransactionSearch').addClass('active');
			} else if (currentUrl.includes("settledTransactionSearch")) {
				$('#settledTransactionSearch').addClass('active');
			} else if (currentUrl.includes("summaryReportsAdmin")) {
				$('#summaryReportsAdmin').addClass('active');
			} else if (currentUrl.includes("home")) {
				$('#downloadTransactionsReport').addClass('active');
			} else if (currentUrl.includes("downloadTransactionsReport")) {
				$('#downloadTransactionsReport').addClass('active');
			}
		} else if (currentUrl.includes("analyticsPerfomanceReport") || currentUrl.includes("analyticsRevenue")) {
			$('#timeline').addClass('show');
			$('#a-timeline').attr('aria-expanded', true);
			if (currentUrl.includes("analyticsPerfomanceReport")) {
				$('#analyticsPerfomanceReport').addClass('active');
			} else if (currentUrl.includes("home")) {
				$('#analyticsRevenue').addClass('active');
			} else if (currentUrl.includes("analyticsRevenue")) {
				$('#analyticsRevenue').addClass('active');
			}
		} else if (currentUrl.includes("merchantCrmSignup") || currentUrl.includes("addCompanyProfile") || currentUrl.includes("merchantList") || currentUrl.includes("ruleEngine") || currentUrl.includes("routerConfigurationAction") || currentUrl.includes("smartRouterAudit") || currentUrl.includes("ruleEngineAudit")) {
			$('#merchantsetup').addClass('show');
			$('#a-merchantsetup').attr('aria-expanded', true);
			if (currentUrl.includes("merchantCrmSignup")) {
				$('#merchantCrmSignup').addClass('active');
			} else if (currentUrl.includes("addCompanyProfile")) {
				$('#addCompanyProfile').addClass('active');
			} else if (currentUrl.includes("merchantList")) {
				$('#merchantList').addClass('active');
			} else if (currentUrl.includes("ruleEngine")) {
				$('#ruleEngine').addClass('active');
			} else if (currentUrl.includes("routerConfigurationAction")) {
				$('#routerConfigurationAction').addClass('active');
			} else if (currentUrl.includes("smartRouterAudit")) {
				$('#smartRouterAudit').addClass('active');
			} else if (currentUrl.includes("home")) {
				$('#ruleEngineAudit').addClass('active');
			} else if (currentUrl.includes("ruleEngineAudit")) {
				$('#ruleEngineAudit').addClass('active');
			}
		} else if (currentUrl.includes("mopSetUpAction") || currentUrl.includes("serviceTaxPlatform") || currentUrl.includes("chargingPlatform") || currentUrl.includes("surchargePlatform") || currentUrl.includes("pendingRequest")) {
			$('#merchantbilling').addClass('show');
			$('#a-merchantbilling').attr('aria-expanded', true);
			if (currentUrl.includes("mopSetUpAction")) {
				$('#mopSetUpAction').addClass('active');
			} else if (currentUrl.includes("serviceTaxPlatform")) {
				$('#serviceTaxPlatform').addClass('active');
			} else if (currentUrl.includes("chargingPlatform")) {
				$('#chargingPlatform').addClass('active');
			} else if (currentUrl.includes("surchargePlatform")) {
				$('#surchargePlatform').addClass('active');
			} else if (currentUrl.includes("home")) {
				$('#pendingRequest').addClass('active');
			} else if (currentUrl.includes("pendingRequest")) {
				$('#pendingRequest').addClass('active');
			}
		} else if (currentUrl.includes("viewSurchargeReport") || currentUrl.includes("smartRouterConfig")) {
			$('#viewconfig').addClass('show');
			$('#a-viewconfig').attr('aria-expanded', true);
			if (currentUrl.includes("viewSurchargeReport")) {
				$('#viewSurchargeReport').addClass('active');
			} else if (currentUrl.includes("home")) {
				$('#smartRouterConfig').addClass('active');
			} else if (currentUrl.includes("smartRouterConfig")) {
				$('#smartRouterConfig').addClass('active');
			}
		} else if (currentUrl.includes("resellerList") || currentUrl.includes("resellerMerchantList")) {
			$('#reseller').addClass('show');
			$('#a-reseller').attr('aria-expanded', true);
			if (currentUrl.includes("resellerList")) {
				$('#resellerList').addClass('active');
			} else if (currentUrl.includes("home")) {
				$('#resellerMerchantList').addClass('active');
			} else if (currentUrl.includes("resellerMerchantList")) {
				$('#resellerMerchantList').addClass('active');
			}
		}else if (currentUrl.includes("transactionSearchAdmin")) {
			if (currentUrl.includes("transactionSearchAdmin")) {
				$('#transactionSearchAdmin').addClass('active');
			}
		} else if (currentUrl.includes("manageBinRange") || currentUrl.includes("verifyTransactionData") ) {
			$('#batchOperation').addClass('show');
			$('#a-batchOperation').attr('aria-expanded', true);
			if (currentUrl.includes("manageBinRange")) {
				$('#manageBinRange').addClass('active');
			} else if (currentUrl.includes("home")) {
				$('#verifyTransactionData').addClass('active');
			} else if (currentUrl.includes("verifyTransactionData")) {
				$('#verifyTransactionData').addClass('active');
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
		}else if (currentUrl.includes("adminRestrictions") || currentUrl.includes("merchantFpsAccessAction")) {
			$('#fraudPrevention').addClass('show');
			$('#a-fraudPrevention').attr('aria-expanded', true);
			if (currentUrl.includes("adminRestrictions")) {
				$('#adminRestrictions').addClass('active');
			}else if (currentUrl.includes("home")){
				$('#merchantFpsAccessAction').addClass('active');
			}else if(currentUrl.includes("merchantFpsAccessAction")){
				$('#merchantFpsAccessAction').addClass('active');
			}
		}else if (currentUrl.includes("addAcquirer") || currentUrl.includes("searchAcquirer")) {
			$('#manageAquirer').addClass('show');
			$('#a-manageAquirer').attr('aria-expanded', true);
			if (currentUrl.includes("addAcquirer")) {
				$('#addAcquirer').addClass('active');
			} else if (currentUrl.includes("home")) {
				$('#searchAcquirer').addClass('active');
			} else if (currentUrl.includes("searchAcquirer")) {
				$('#searchAcquirer').addClass('active');
			}
		} else if (currentUrl.includes("agentSearch")) {
			$('#accessAgent').addClass('show');
			$('#a-accessAgent').attr('aria-expanded', true);
			if (currentUrl.includes("agentSearch")) {
				$('#agentSearch').addClass('active');
			}<!-- added by vijay -->
		} else if (currentUrl.includes("misReports") || currentUrl.includes("merchantRecoReports")|| currentUrl.includes("recofileUpload")  || currentUrl.includes("monthlyInvoiceReports")) {
			$('#accountsFinance').addClass('show');
			$('#a-accountsFinance').attr('aria-expanded', true);
			if (currentUrl.includes("misReports")) {
				$('#misReports').addClass('active');
			} else if (currentUrl.includes("home")) {
				$('#merchantRecoReports').addClass('active');
			} else if (currentUrl.includes("merchantRecoReports")) {
				$('#merchantRecoReports').addClass('active');
			} else if (currentUrl.includes("recofileUpload")) {
             				$('#recofileUpload').addClass('active');
             			} else if (currentUrl.includes("monthlyInvoiceReports")) {
				$('#monthlyInvoiceReports').addClass('active');
			}
		}  else if ( currentUrl.includes("merchantNodalTransactions")  || currentUrl.includes("nodalTransactionsHistory")) {
			$('#transferFunds').addClass('show');
			$('#a-transferFunds').attr('aria-expanded', true);
			 if (currentUrl.includes("merchantNodalTransactions")) {
				$('#merchantNodalTransactions').addClass('active');
			} else if (currentUrl.includes("home")) {
				$('#nodalTransactionsHistory').addClass('active');
			}else if (currentUrl.includes("nodalTransactionsHistory")) {
				$('#nodalTransactionsHistory').addClass('active');
			}
		} else if (currentUrl.includes("txnSmsSender")) {
			$('#notifyEngine').addClass('show');
			$('#a-notifyEngine').attr('aria-expanded', true);
			if (currentUrl.includes("txnSmsSender")) {
				$('#txnSmsSender').addClass('active');
			}
		}

		else if (currentUrl.includes("auditTrailsReport")) {
			$('#auditTrail').addClass('show');
			$('#a-auditTrail').attr('aria-expanded', true);
			if (currentUrl.includes("txnSmsSender")) {
				$('#auditTrail').addClass('active');
			}
		}
		// Adding chargeback nav bar functionality
		else if(currentUrl.includes("createChargebackPage") || currentUrl.includes("viewChargeback")) {
			$('#chargeback').addClass('show');
			$('#a-chargeback').attr('aria-expanded', true);
			if(currentUrl.includes("createChargebackPage")) {
				$('#createChargebackPage').addClass('active');
			} /* else if(currentUrl.includes("home")) {
				$('#viewChargeback').addClass('active');
			}  */else if(currentUrl.includes("viewChargeback")) {
				$('#viewChargeback').addClass('active');
			} else if(currentUrl.includes("home")) {
				$('#uploadCbFile').addClass('active');
			}
		}
	</script>
	




    
    