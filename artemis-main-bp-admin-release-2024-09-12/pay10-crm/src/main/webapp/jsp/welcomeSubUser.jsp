<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html>
<html lang="en">

<head>
	<title>Dashboard</title>
	<link href="../css/default.css" rel="stylesheet" type="text/css" />
	<link rel="stylesheet" type="text/css" media="all" href="../css/daterangepicker-bs3.css" />
	<link rel="stylesheet" href="../css/jquery-timepicker.css">
	<link href="../css/Jquerydatatableview.css" rel="stylesheet" />
	<link href="../css/jquery-ui.css" rel="stylesheet" />
	<link href="../css/Jquerydatatable.css" rel="stylesheet" />
	<script src="../js/jquery.min.js" type="text/javascript"></script>
	<script src="../js/core/popper.min.js"></script>
	<script src="../js/core/bootstrap-material-design.min.js"></script>
	<script src="../js/plugins/perfect-scrollbar.jquery.min.js"></script>

	<script src="../js/material-dashboard.js?v=2.1.0" type="text/javascript"></script>
	<script src="../js/moment.js" type="text/javascript"></script>
	<script src="../js/daterangepicker.js" type="text/javascript"></script>

	<script src="../js/jquery.dataTables.js"></script>
	<script src="../js/jquery-ui.js"></script>
	<script src="../js/commanValidate.js"></script>
	<script src="../js/jquery.popupoverlay.js"></script>
	<script src="../js/dataTables.buttons.js" type="text/javascript"></script>
	<script src="../js/highstock.js"></script>
	
	<script src="../js/funnel.js"></script>
	<script src="../js/exporting.js"></script>
	<!-- searchable select option -->
	<script src="../js/select2.min.js"></script>

	<link href="../css/select2.min.css" rel="stylesheet" />

	<script src="../js/welcomeSubUser.js"></script>

	<script src="../js/jqueryTimepicker.js"></script>

	
	<style>
		.card-stats .card-header.card-header-icon i {
			font-size: 20px !important;

		}

		.card [class*="card-header-"] .card-icon,
		.card [class*="card-header-"] .card-text {
			padding: 0px !important;

		}

		#cardIcon {
			padding: 15px !important;
		}

		#materialIcons {
			font-size: 36px !important;
		}

		@media (min-width: 1496px) {
			.card.card-stats {
				min-height: 74px !important;
			}
		}

		@media (min-width: 1300px) {
			.card.card-stats {
				min-height: 90px !important;
			}
		}

		@media (min-width: 992px) {
			.card.card-stats {
				min-height: 90px !important;
			}
		}

		.card-title {
			font-size: 18px;
			font-weight: 400;
		}

		.highcharts-axis-labels.highcharts-xaxis-labels>text {
			color: #000000 !important;
			cursor: default;
			font-size: 11px;
			font-weight: normal;
			font-family: Open Sans;
			fill: #000000 !important;
		}

		.highcharts-credits {
			display: none;
		}
		.clockpicker-popover .popover-title span {
    cursor: pointer;
    color: grey !important;
}

		.card-body.panel-body {
			overflow-x: scroll;
		}

		::-webkit-scrollbar {
			-webkit-appearance: none;
		}

		::-webkit-scrollbar:vertical {
			width: 12px;
		}

		::-webkit-scrollbar:horizontal {
			height: 12px;
		}

		::-webkit-scrollbar-thumb {
			background-color: rgba(0, 0, 0, .5);
			border-radius: 10px;
			border: 2px solid #ffffff;
		}

		::-webkit-scrollbar-track {
			border-radius: 10px;
			background-color: #ffffff;
		}
		#getDataButton{
			/* padding: 5px 15px;
	border-radius: 30px; */
	position: absolute;
    top: -40px;
		}
		#customButton{
			padding: 5px 15px;
    border-radius: 30px;
		}
		button, html input[type="button"], input[type="reset"], input[type="submit"] {
    -webkit-appearance: button;
    cursor: pointer;
    background-color: #496cb6;
    color: white;
	border: 1px solid #496cb6;
		}
		.ui-timepicker-container.ui-timepicker-standard{
			z-index: 10000000  !important;
		}
		.showGraph{
			display:block;
		}
	</style>
</head>

<body onload="handleChange();" ng-app="myApp" style="margin:0px; padding:0px;">
	<div id="loading" style="text-align: center;">
		<img id="loading-image" style="width:70px;height:70px;" src="../image/sand-clock-loader.gif" alt="Sending SMS..." />
	</div>
	<div id="page-inner">


		<div class="row">
			<!-- <div class="col-md-1 col-xs-12">&nbsp;</div> -->
			<div class="col-md-12 col-xs-12">
                           <h1 class="page-headerr">
                            Dashboard
                        </h1>
					</div>
					
                </div>
					
					<div class="row">

						
                    <div class="col-lg-3 col-md-6 col-sm-6">
                        <div class="page-headerr">
							<s:select name="merchants" class="input-control" id="merchant"
								listKey="emailId" listValue="businessName" list="merchantList"
								autocomplete="off" onchange="handleChange();" />
						</div>			
                        
					</div>
					
                    <div class="col-lg-2 col-md-4 col-sm-4">
                        <div class="page-headerr" style="border-bottom: none;">
							<s:select name="currency" id="currency" list="currencyMap"
								class="input-control" onchange="handleChange();" />
						</div>		
                        
                    </div> 
                </div>
				
				
				<!-- /. ROW  -->
				<div id="snapshotPermission" data-permission="${sessionScope.USER_PERMISSION}"></div>
		<s:if test="%{#session['USER_PERMISSION'].contains('View Snapshot') }">	
				<div class="row">
					<div class="col-md-12" style="text-align: center;">
						
					  <button class="btn btn-primary btnActive" id="buttonDay">Day<div class="ripple-container"></div></button>
					  <button class="btn btn-primary" id="buttonWeekly">Week<div class="ripple-container"></div></button>
					  <button class="btn btn-primary" id="buttonMonthly">Month<div class="ripple-container"></div></button>
					  <button class="btn btn-primary" id="buttonYearly">Year<div class="ripple-container"></div></button>
					  <button class="btn btn-primary" id="buttonCustom">Custom<div class="ripple-container"></div></button>
				
					</div>
				  </div> 
				  <!-- <div class="modal fade" id="customDateModal" role="dialog">
					<div class="modal-dialog">
				
				
					  <div class="modal-content">
						<div class="modal-header">
				
						</div>
						<div class="modal-body">
							<div class="row" id="custom" >
								<div class="col-md-12" >
								  <div class="col-sm-6 col-lg-6" >
									  <label>Date From</label><br>
									  <div class="txtnew">
										  <s:textfield type="text" id="dateFrom" name="dateFrom" class="input-control" autocomplete="off" readonly="true" />
									  </div> 
								  </div>
									<div class="col-sm-6 col-lg-6" >
									  <label>Date To</label><br>
									  
									  <div class="txtnew">
										  <s:textfield type="text" id="dateTo" name="dateTo" class="input-control" autocomplete="off" readonly="true" 
										  />
									  </div>
									</div>
								</div>
							</div>
						</div>
						<div class="modal-footer" id="modal_footer">
						  <button type="button" id="customButton" class="btn btn-primary btn-round mt-4 submit_btn" data-dismiss="modal">View</button>
						</div>
					  </div>
				
					</div>
				  </div> -->
				 
                  <div class="row">
					<div class="col-md-12" id="customDateModal"  style="display: none;">
						<div class="col-sm-6 col-lg-6" >

						</div>
					  <div class="col-sm-2 col-lg-2" >
						  <label>Date From </label><br>
						  <div class="txtnew">
							  <s:textfield type="text" id="dateFrom" name="dateFrom" class="input-control" autocomplete="off" readonly="true" />
						  </div> 
					  </div>
						<div class="col-sm-2 col-lg-2" >
						  <label>Date To</label><br>
						  
						  <div class="txtnew">
							  <s:textfield type="text" id="dateTo" name="dateTo" class="input-control" autocomplete="off" readonly="true" 
							  />
						  </div>
						</div>
						<div class="col-sm-2 col-lg-2">
						  <button type="button" id="customButton" class="btn btn-primary btn-round mt-4 submit_btn" data-dismiss="modal">View</button>
						</div>
					</div>
				  </div>
                

				<div class="row" style="margin-top: 2%;">
					

					<div class="col-lg-3 col-md-6 col-sm-6">
						<div class="card card-stats">
						  <div class="card-header card-header-success card-header-icon">
							<div class="card-icon">
							  <!-- <i class="material-icons">thumb_up</i> -->
							  <i class="fa fa-thumbs-up fa-5x"></i>
							</div>
							<p class="card-category">Total Sale</p>
							
						  </div>
						  <div class="card-footer">
							<div class="stats">
								<h3 class="card-title" id="dvTotalSuccess"><s:property value="%{statistics.totalSuccess}"/></h3>
							 
							</div>
						  </div>
						</div>
					  </div>


                  
					

					<div class="col-lg-3 col-md-6 col-sm-6">
						<div class="card card-stats">
						  <div class="card-header card-header-rose card-header-icon">
							<div class="card-icon">
								<i class="fa fa-inr fa-5x"></i>
							</div>
							<p class="card-category">Sale Amount</p>
						
						  </div>
						  <div class="card-footer">
							<div class="stats">
								<h3 class="card-title" id="dvApprovedAmount" ><s:property  value="%{statistics.approvedAmount}"/></h3>
							
							</div>
						  </div>
						</div>
					  </div>
                
					
					<div class="col-lg-3 col-md-6 col-sm-6">
						<div class="card card-stats">
						  <div class="card-header card-header-info card-header-icon">
							<div class="card-icon">
								<i class="fa fa-reply fa-5x"></i>
							</div>
							<p class="card-category">Total Refunded</p>
							
						  </div>
						  <div class="card-footer">
							<div class="stats">
								<h3 class="card-title" id="dvTotalRefunded"><s:property value="%{statistics.totalRefunded}"/></h3>
							  
							</div>
						  </div>
						</div>
					  </div>
                 
					
					<div class="col-lg-3 col-md-6 col-sm-6">
						<div class="card card-stats">
						  <div class="card-header card-header-warning card-header-icon">
							<div class="card-icon">
								<i class="fa fa-reply fa-5x"></i>
							</div>
							<p class="card-category">Refunded Amount</p>
							
						  </div>
						  <div class="card-footer">
							<div class="stats">
							<h3 class="card-title" id="dvRefundedAmount"><s:property value="%{statistics.refundedAmount}"/></h3>
							  
							</div>
						  </div>
						</div>
					  </div>
					  <div class="col-lg-3 col-md-6 col-sm-6">
						<div class="card card-stats">
						  <div class="card-header card-header-success card-header-icon">
							<div class="card-icon">
								<i class="fa fa-inr fa-5x"></i>
							</div>
							<p class="card-category">Settled Amount</p>
							
						  </div>
						  <div class="card-footer">
							<div class="stats">
								<h3 class="card-title" id="dvTotalSettledAmount">
							
									<s:property value="%{statistics.totalSettledAmount}"/>
								</h3>
							  
							</div>
						  </div>
						</div>
					  </div>

					  <div class="col-lg-3 col-md-6 col-sm-6">
						<div class="card card-stats">
						  <div class="card-header card-header-danger card-header-icon">
							<div class="card-icon">
								 <i class="material-icons">cancel</i>
							</div>
							<p class="card-category">Total Cancelled </p>
							
						  </div>
						  <div class="card-footer">
							<div class="stats">
								<h3 class="card-title" id="dvTotalCancelled">
								
									<s:property value="%{statistics.totalCancelled}"/>
								</h3>
							  
							</div>
						  </div>
						</div>
					  </div>

					  <div class="col-lg-3 col-md-6 col-sm-6">
						<div class="card card-stats">
						  <div class="card-header card-header-danger card-header-icon">
							<div class="card-icon">
								<i class="fa fa-thumbs-down fa-5x"></i>
							</div>
							<p class="card-category">Total Failed </p>
							
						  </div>
						  <div class="card-footer">
							<div class="stats">
								<h3 class="card-title" id="dvTotalFailed">
								
									<s:property value="%{statistics.totalFailed}"/>
								</h3>
							  
							</div>
						  </div>
						</div>
					  </div>

					  <div class="col-lg-3 col-md-6 col-sm-6">
						<div class="card card-stats">
						  <div class="card-header card-header-warning card-header-icon">
							<div class="card-icon">
								<i class="fa fa-user-secret fa-5x"></i>
								<!-- <i class="material-icons">vpn_lock</i> -->
							</div>
							<p class="card-category">Total Fraud</p>
							
						  </div>
						  <div class="card-footer">
							<div class="stats">
								<h3 class="card-title" id="dvTotalFraud">
								
									<s:property value="%{statistics.totalFraud}"/>
								</h3>
							  
							</div>
						  </div>
						</div>
					  </div>
 
                    
                </div>
              </s:if>  
				<div   class="accordion md-accordion" id="accordionEx1" role="tablist" aria-multiselectable="true">
					<div id="sortable">

					<div id="monthlyPermission" data-permission="${sessionScope.USER_PERMISSION}"></div>
					
					<s:if test="%{#session['USER_PERMISSION'].contains('View Monthly Transactions') }">
					<div class="card card_graph"   id="monthlycard"><!--onclick="monthlycardupdateTabState(this)"-->
						<div class="card-header" role="tab" id="headingTwo1">
							<a class="collapsed" >
								<div class="card-header card-header-icon card-header-info" id="cardHeaderPosition">
									<div class="card-icon" id="cardIcon">
									  <i class="material-icons" id="materialIcons">multiline_chart</i>
									</div>
									<h4 class="card-title">Monthly Transactions
										<i style="float: right; color: #496cb6;font-size: 30px;" data-toggle="collapse" data-parent="#accordionEx1" href="#collapseTwo1"
										aria-expanded="false" aria-controls="collapseTwo1" class="fa fa-angle-down rotate-icon"></i>
									</h4>
									</div>
							  <!-- <h5 class="mb-0">
								<strong>Monthly Transaction </strong> <i style="float: right; color: #496cb6;font-size: 30px;" data-toggle="collapse" data-parent="#accordionEx1" href="#collapseTwo1"
								aria-expanded="false" aria-controls="collapseTwo1" class="fa fa-angle-down rotate-icon"></i>
							  </h5> -->
							</a>
						  </div>
						  <div id="collapseTwo1" class="collapse active show" role="tabpanel" >
      <div class="card-body">
                <div class="row">
				<div class="col-md-12 col-xs-12">
					
						<div class="cards">
						  <div class="card-header card-header-icon card-header-info">
							<!-- <div class="card-icon" id="cardIcon">
							  <i class="material-icons" id="materialIcons">multiline_chart</i>
							</div>
							<h4 class="card-title">Monthly Transaction
							 
							</h4><br>
							<br> -->
							<div class="row" style="align-items: flex-end;">
							<div class="col-sm-1 col-lg-1" >
								<button type="button" onclick="getMonthlyData()" id="oneMonth" class="btn-btn-primary btnActive" >1M</button>
								<!-- <button type="button" onclick="getThreeMonthData()" id="threeMonth" class="btn-btn-primary" >3M</button> -->
								
							</div>
							
							<div class="col-sm-3 col-lg-3" >
								<label>Date From</label><br>
								<div class="txtnew">
									<s:textfield type="text" id="dateFromMonth" name="dateFromMonth" class="input-control" autocomplete="off" readonly="true" />
								</div> 
							</div>
							  <div class="col-sm-3 col-lg-3" >
								<label>Date To</label><br>
								
								<div class="txtnew">
									<s:textfield type="text" id="dateToMonth" name="dateToMonth" class="input-control" autocomplete="off" readonly="true" 
									/>
								</div>
							  </div>
							  <div class="col-sm-3 col-lg-3">
								<label>TXN Type</label><br>
								<s:select headerKey="SALE" headerValue="SALE" class="input-control"
								list="#{'REFUND':'REFUND'}" name="transactionType" id = "transactionTypeMonthly" />
		  
							</div>
							  
							<div class="col-sm-2 col-lg-2" >
								
							</div>
							
							<div class="col-sm-1 col-lg-1" >
								
							</div>
							<div class="col-sm-3 col-lg-3">
								<label>Payment Method</label><br>
								<div class="txtnew">
									<s:select headerKey="" headerValue="ALL" class="input-control"
										list="@com.pay10.commons.util.PaymentTypeUI@values()"
										listValue="name" listKey="code" name="paymentMethod"
										id="paymentMethods" autocomplete="off" value=""  />
								</div>
	
							</div>
							<div class="col-sm-3 col-lg-3">
								<label>Mop Type</label><br>
								
								<s:select name="mopType" id="mopType" headerValue="ALL"
								headerKey="ALL" list="@com.pay10.commons.util.MopTypeUI@values()"
								listValue="name" listKey="uiName" class="input-control" value="mopType" />
							  </div>
							
							 
							  <div class="col-sm-3 col-lg-3">
								  <button type="button" onclick="getCustomMonthData()" style="position: absolute;
								  top: -40px;
								  left: 48px;"  class="btn btn-primary monthlyGet">View</button>
							  </div>
							</div>
						  </div>
						  <div class="card-body panel-body scrollD">
							
							<div id="colouredRoundedLineChart" style="width:900px; height: 400px; margin: 0 auto" ></div>
							
						  </div>
						</div>
					  
				
				</div> 
				</div>
				</div>
						  </div> 	
					</div>
				</s:if>
				<s:if test="%{#session['USER_PERMISSION'].contains('View Hourly Transactions') }">
					<div class="card card_graph"  id="todayCard" ><!---->

						<!-- Card header -->
						<div class="card-header" role="tab" id="headingTwo2">
						  <a class="collapsed" >
							<div class="card-header card-header-icon card-header-success" id="cardHeaderPosition">
								<div class="card-icon" id="cardIcon">
								  <i class="material-icons" id="materialIcons">show_chart</i>
								</div>
								<h4 class="card-title">Hourly Transactions
									<i style="float: right; color: #496cb6;font-size: 30px;" data-toggle="collapse" data-parent="#accordionEx1" href="#collapseTwo21"
									aria-expanded="false" aria-controls="collapseTwo21" id="showToday" class="fa fa-angle-down rotate-icon"></i>
								</h4>
								</div>
							<!-- <h5 class="mb-0">
							<strong>Today's Transaction  </strong> <i style="float: right; color: #496cb6;font-size: 30px;" data-toggle="collapse" data-parent="#accordionEx1" href="#collapseTwo21"
							aria-expanded="false" aria-controls="collapseTwo21" class="fa fa-angle-down rotate-icon"></i>
							</h5> -->
						  </a>
						</div>
						<div id="collapseTwo21" class="collapse" role="tabpanel" >
      <div class="card-body">
<div class="row">
						<div class="col-md-12 col-xs-12">
							
								<div class="cards">
								  <div class="card-header card-header-icon card-header-info">
									<!-- <div class="card-icon" id="cardIcon">
									  <i class="material-icons" id="materialIcons">multiline_chart</i>
									</div>
									<h4 class="card-title">Today Transaction (Hourly)
									 
									</h4><br><br> -->
									<div class="row" style="align-items: flex-end;">
										
										<!-- <div class="col-sm-3 col-lg-3" >
											<label>Time From </label><br>
											<div class="txtnew">
												<input id="timepicker" name="timefrom" value="00:00" autocomplete="off" readonly="true"  class="timepicker input-control"/>
	
											</div> 
										</div>
										  <div class="col-sm-3 col-lg-3" >
											<label>Time To</label><br>
											
											<div class="txtnew">
												<input id="timepickerb" name="timeto" value="00:00" autocomplete="off" readonly="true"  class="timepicker input-control" />
											</div>
										  </div> -->
										  <div class="col-sm-3 col-lg-3" >
											<label>Select Date </label><br>
											<div class="txtnew">
												<s:textfield type="text" id="selectDateHourly" name="selectDateHourly" class="input-control" autocomplete="off" readonly="true" />
											</div> 
										</div>
										<div class="col-sm-3 col-lg-3">
											<label>TXN Type</label><br>
											<s:select headerKey="SALE" headerValue="SALE" class="input-control"
											list="#{'REFUND':'REFUND'}" name="transactionType" id = "transactionTypeHourly" />
					  
										</div>
										
										  <div class="col-sm-3 col-lg-3">
											<label>Payment Method</label><br>
											<div class="txtnew">
												<s:select headerKey="" headerValue="ALL" class="input-control"
													list="@com.pay10.commons.util.PaymentTypeUI@values()"
													listValue="name" listKey="code" name="paymentMethod"
													id="paymentMethodsHourly" autocomplete="off" value=""  />
											</div>
				
										</div>
										<div class="col-sm-3 col-lg-3">
											<label>Mop Type</label><br>
											
											<s:select name="mopType" id="mopTypeHourly" headerValue="ALL"
											headerKey="ALL" list="@com.pay10.commons.util.MopTypeUI@values()"
											listValue="name" listKey="uiName" class="input-control" value="mopType" />
										  </div>
										

										  <div class="col-sm-3 col-lg-3">
											<button type="button" style="margin-top: 20px;" onclick="getHourlyData()" class="btn btn-primary todayDataGet">View</button>
										</div>
									  </div>
								  </div>
								  <div class="card-body panel-body scrollD"  >
									
									<div id="graph" style="width:900px; height: 400px; margin: 0 auto" ></div>
								  </div>
								</div>
							  
						
						</div> 
						</div> 
						</div>
						</div>
					</div>
					</s:if>
					<s:if test="%{#session['USER_PERMISSION'].contains('View Payment type Comparison') }">
					<div class="card card_graph"  id="paymentCompareCard"><!---->

						<!-- Card header -->
						<div class="card-header" role="tab" id="headingTwo2">
						  <a class="collapsed" >
							<div class="card-header card-header-icon card-header-rose" id="cardHeaderPosition">
								<div class="card-icon" id="cardIcon">
								  <i class="material-icons" id="materialIcons">pie_chart</i>
								</div>
								<h4 class="card-title"> Payment Type Comparison
									<i style="float: right; color: #496cb6;font-size: 30px;" data-toggle="collapse" data-parent="#accordionEx1" href="#collapseTwo25"
									aria-expanded="false" aria-controls="collapseTwo25" id="showPie" class="fa fa-angle-down rotate-icon"></i>
								</h4>
								</div>
							<!-- <h5 class="mb-0">
							  <strong>Payment Comparison </strong><i style="float: right; color: #496cb6;font-size: 30px;" data-toggle="collapse" data-parent="#accordionEx1" href="#collapseTwo25"
							  aria-expanded="false" aria-controls="collapseTwo25" class="fa fa-angle-down rotate-icon"></i>
							</h5> -->
						  </a>
						</div>
						<div id="collapseTwo25" class="collapse" role="tabpanel" >
      <div class="card-body">
				<div class="row">
					
				
				
				
			
					<div class="col-md-12 col-xs-12">
						
						<div class="cards">
						  <div class="card-header card-header-icon card-header-rose">
							<!-- <div class="card-icon" id="cardIcon">
							  <i class="material-icons" id="materialIcons">pie_chart</i>
							</div>
							<h4 class="card-title"> Payment Method Comparison
							 
							</h4><br><br> -->
							<div class="row" style="align-items: flex-end;">
								
							<div class="col-sm-3 col-lg-3" >
								<label>Date From </label><br>
								<div class="txtnew">
									<s:textfield type="text" id="dateFromPie" name="dateFromPie" class="input-control" autocomplete="off" readonly="true" />
								</div> 
							</div>
							  <div class="col-sm-3 col-lg-3" >
								<label>Date To</label><br>
								
								<div class="txtnew">
									<s:textfield type="text" id="dateToPie" name="dateToPie" class="input-control" autocomplete="off" readonly="true" 
									/>
								</div>
							  </div>
							  <div class="col-sm-3 col-lg-3">
								<label>TXN Type</label><br>
								<s:select headerKey="SALE" headerValue="SALE" class="input-control"
								list="#{'REFUND':'REFUND'}" name="transactionType" id = "transactionTypePayment" />
		  
							</div>
							  <div class="col-sm-3 col-lg-3">
								<label>Payment Method</label><br>
								<div class="txtnew">
									<s:select headerKey="" headerValue="ALL" class="input-control"
										list="@com.pay10.commons.util.PaymentTypeUI@values()"
										listValue="name" listKey="code" name="paymentMethod"
										id="paymentMethodsPie" autocomplete="off" value=""  />
								</div>
	
							</div>
							
							<div class="col-sm-3 col-lg-3">
								<label>Mop Type</label><br>
								
								<s:select name="mopType" id="mopTypePie" headerValue="ALL"
								headerKey="ALL" list="@com.pay10.commons.util.MopTypeUI@values()"
								listValue="name" listKey="uiName" class="input-control" value="mopType" />
							  </div>
							
							  <div class="col-sm-3 col-lg-3"   >
								<button type="button" id="getDataButton" onclick="getPaymentTypeData()" class="btn btn-primary paymentDataGet">View</button>
							  </div>
							  </div>
						  </div>
						  <div class="col-md-12 col-xs-12" style="display: flex;">
							<div class="col-md-6 col-xs-6 card-body panel-body "  style="margin:auto" >
							  
							  <div class="chartBox " id="chartBox" >
							</div>
						  </div>
						  <div class=" col-md-6 col-xs-6 card-body panel-body  " id="moppie" style="display: none;margin:auto" >
							  
							  <div class="chartBox1" id="chartBox1" >
							</div>
						  </div>
					  </div>
					  
				
				</div> 
					</div> 
					</div>
					</div>
				</div>
					</div>
				</s:if>
				<s:if test="%{#session['USER_PERMISSION'].contains('View Hits Vs Captured') }">
					<div class="card card_graph"  id="hitsCard"><!---->

						<!-- Card header -->
						<div class="card-header" role="tab" id="headingTwo2">
						  <a class="collapsed" >
							<div class="card-header card-header-icon card-header-warning" id="cardHeaderPosition">
								<div class="card-icon" id="cardIcon">
								  <i class="material-icons" id="materialIcons">filter_list</i>
								</div>
								<h4 class="card-title">Hits Vs Captured
									<i style="float: right; color: #496cb6;font-size: 30px;" id="showFunnel" data-toggle="collapse" data-parent="#accordionEx1" href="#collapseTwo22"
									aria-expanded="false" aria-controls="collapseTwo22" class="fa fa-angle-down rotate-icon"></i>
								</h4>
								</div>
							<!-- <h5 class="mb-0">
							  <strong>Hits Vs Captured  </strong><i style="float: right; color: #496cb6;font-size: 30px;" data-toggle="collapse" data-parent="#accordionEx1" href="#collapseTwo22"
							  aria-expanded="false" aria-controls="collapseTwo22" class="fa fa-angle-down rotate-icon"></i>
							</h5> -->
						  </a>
						</div>
						
						<div id="collapseTwo22" class="collapse" role="tabpanel" >
      <div class="card-body">	
					<div class="row">
						<div class="col-md-12 col-xs-12">
							
								<div class="cards">
								  <div class="card-header card-header-icon card-header-warning">
									<!-- <div class="card-icon" id="cardIcon">
									  <i class="material-icons" id="materialIcons">clear_all</i>
									</div>
									<h4 class="card-title">Hits Vs Captured
									 
									</h4>
									<br><br> -->
								<div class="row" style="align-items: flex-end;">
									
									<div class="col-sm-3 col-lg-3" >
										<label>Date From</label><br>
										<div class="txtnew">
											<s:textfield type="text" id="dateFromFunnel" name="dateFromFunnel" class="input-control" autocomplete="off" readonly="true" />
										</div> 
									</div>
									  <div class="col-sm-3 col-lg-3" >
										<label>Date To</label><br>
										
										<div class="txtnew">
											<s:textfield type="text" id="dateToFunnel" name="dateToFunnel" class="input-control" autocomplete="off" readonly="true" 
											/>
										</div>
									  </div>
									  <div class="col-sm-3 col-lg-3">
										<label>TXN Type</label><br>
										<s:select headerKey="SALE" headerValue="SALE" class="input-control"
										list="#{'REFUND':'REFUND'}" name="transactionType" id = "transactionTypeFunnel" />
				  
									</div>
								
									
									
									  <div class="col-sm-3 col-lg-3">
										<label>Payment Method</label><br>
										<div class="txtnew">
											<s:select headerKey="" headerValue="ALL" class="input-control"
												list="@com.pay10.commons.util.PaymentTypeUI@values()"
												listValue="name" listKey="code" name="paymentMethod"
												id="paymentMethodsFunnel" autocomplete="off" value=""  />
										</div>
			
									</div>
									
									<div class="col-sm-3 col-lg-3">
										<label>Mop Type</label><br>
										
										<s:select name="mopType" id="mopTypeFunnel" headerValue="ALL"
										headerKey="ALL" list="@com.pay10.commons.util.MopTypeUI@values()"
										listValue="name" listKey="uiName" class="input-control" value="mopType" />
									  </div>
									  <div class="col-sm-3 col-lg-3">
										<button type="button" style="margin-top:20px;" onclick="getHitsData()" class="btn btn-primary funnelDataGet">View</button>
									</div>
									  </div>
								  </div>
								  <div class="card-body panel-body scrollD"  >
									
									<div id="funnel" style="min-width: 410px; max-width: 600px; height: 400px; margin: 0 auto">
								  </div>
								</div>
							  
						
						</div> 
						</div>
						

					</div>
					</div>
						</div></div>
					</s:if>
					<s:if test="%{#session['USER_PERMISSION'].contains('View Settlement') }">
						<div class="card card_graph"  id="settlementCard" ><!---->

							<!-- Card header -->
							<div class="card-header" role="tab" id="headingTwo2">
							  <a class="collapsed" >
								<div class="card-header card-header-icon card-header-success" id="cardHeaderPosition">
									<div class="card-icon" id="cardIcon">
									  <i class="material-icons" id="materialIcons">equalizer</i>
									</div>
									<h4 class="card-title">Settlement 
										<i style="float: right; color: #496cb6;font-size: 30px;" id="showSettled" class="fa fa-angle-down rotate-icon" data-toggle="collapse" data-parent="#accordionEx1" href="#collapseTwo23"
										aria-expanded="false" aria-controls="collapseTwo23"></i>
									</h4>
								<!-- <h5 class="mb-0">
								  <strong>Settlement Bar Chart</strong> <i style="float: right; color: #496cb6;font-size: 30px;" class="fa fa-angle-down rotate-icon" data-toggle="collapse" data-parent="#accordionEx1" href="#collapseTwo23"
								  aria-expanded="false" aria-controls="collapseTwo23"></i>
								</h5> -->
							  </a>
							</div>
							<div id="collapseTwo23" class="collapse" role="tabpanel" >
		  <div class="card-body">
					<div class="row">
						<div class="col-md-12 col-xs-12">
							
							<div class="cards">
							  <!-- <div class="card-header card-header-icon card-header-success">
								<div class="card-icon" id="cardIcon">
								  <i class="material-icons" id="materialIcons">equalizer</i>
								</div>
								<h4 class="card-title">Settlement
								 
								</h4>
								<br><br> -->
									<div class="row" style="align-items: flex-end;">
										
								<div class="col-sm-3 col-lg-3" >
									<label>Date From </label><br>
									<div class="txtnew">
										<s:textfield type="text" id="dateFromSettlement" name="dateFromSettlement" class="input-control" autocomplete="off" readonly="true" />
									</div> 
								</div>
								  <div class="col-sm-3 col-lg-3" >
									<label>Date To</label><br>
									
									<div class="txtnew">
										<s:textfield type="text" id="dateToSettlement" name="dateToSettlement" class="input-control" autocomplete="off" readonly="true" 
										/>
									</div>
								  </div>
								
								
								  <div class="col-sm-3 col-lg-3">
									<label>Payment Method</label><br>
									<div class="txtnew">
										<s:select headerKey="" headerValue="ALL" class="input-control"
											list="@com.pay10.commons.util.PaymentTypeUI@values()"
											listValue="name" listKey="code" name="paymentMethod"
											id="paymentMethodsSettled" autocomplete="off" value=""  />
									</div>
		
								</div>
								  <div class="col-sm-3 col-lg-3">
									<button id="getDataButton" type="button" onclick="getSettledData()" class="btn btn-primary settledDataGet">View</button>
								</div>
								  </div>
							  </div>
							  <div class="card-body panel-body scrollD"  >
								
								<div id="settlement" style="width:900px; height: 400px; margin: 0 auto" >
							  </div>
							</div>
						  
					
					</div> 
					</div>
					</div>
					</div>
					</div>
				</s:if>
				
				</div>
				</div>
				

	 
</body>
</html>


<!-- <div class="col-md-6 col-xs-6" >
						
	<div class="cards">
	  <div class="card-header card-header-icon card-header-rose">
		<div class="card-icon" id="cardIcon">
		  <i class="material-icons" id="materialIcons">insert_chart</i>
		</div>
		<h4 class="card-title">Failure Reason
		 
		</h4><br><br>
		<div class="row" style="align-items: flex-end;">
			
		<div class="col-sm-3 col-lg-3" >
			<label>Date From</label><br>
			<div class="txtnew">
				<s:textfield type="text" id="dateFromPie" name="dateFro	mPie" class="input-control" autocomplete="off" readonly="true" />
			</div> 
		</div>
		  <div class="col-sm-3 col-lg-3" >
			<label>Date To</label><br>
			
			<div class="txtnew">
				<s:textfield type="text" id="dateToPie" name="dateToPie" class="input-control" autocomplete="off" readonly="true" 
				/>
			</div>
		  </div>
		  </div>
	  </div>
	  <div class="card-body panel-body scrollD">
		
		<div id="container" style="width:100%; height:auto;"></div>
	  </div>
	</div>
  

</div> -->