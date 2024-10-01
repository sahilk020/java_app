
$(document).ready(function () {

	// Initialize select2
	// $("#merchant").select2();
	// $(document).ready(function () {
	// 	$("#datepicker").datepicker();
	// 	$(window).bind('scroll',function () {
	// 		$("#ui-datepicker-div").hide();        
	// 	});
	
	// });

	$(function () {
		$("#dateFrom").datepicker({
			prevText: "click for previous months",
			nextText: "click for next months",
			showOtherMonths: true,
			dateFormat: 'dd-mm-yy',
			selectOtherMonths: false,
			maxDate: new Date()
		});
		$("#dateTo").datepicker({
			prevText: "click for previous months",
			nextText: "click for next months",
			showOtherMonths: true,
			dateFormat: 'dd-mm-yy',
			selectOtherMonths: false,
			maxDate: new Date()
		});
	});

	$(function () {
		var today = new Date();
		$('#dateTo').val($.datepicker.formatDate('dd-mm-yy', today));
		$('#dateFrom').val($.datepicker.formatDate('dd-mm-yy', today));
		statistics();
		/*renderTable();*/
	});
	

});

//to show new loader --harpreet
$.ajaxSetup({
	global: false,
	beforeSend: function () {
		toggleAjaxLoader();
	},
	complete: function () {
		toggleAjaxLoader();
	}
});

var currentPeriod = "day";
$(document).ready(function () {
	document.getElementById("loading").style.display = "none";
	handleChange();

});
var expanded = false;

function showCheckboxes(e) {
  var checkboxes = document.getElementById("checkboxes");
  if (!expanded) {
    checkboxes.style.display = "block";
    expanded = true;
  } else {
    checkboxes.style.display = "none";
    expanded = false;
  }

 
   e.stopPropagation();

}
function showCheckboxes1(e) {
	var checkboxes1 = document.getElementById("checkboxes1");
	if (!expanded) {
	  checkboxes1.style.display = "block";
	  expanded = true;
	} else {
	  checkboxes1.style.display = "none";
	  expanded = false;
	}
  
   
	 e.stopPropagation();
  
  }
  function showCheckboxes2(e) {
	var checkboxes2 = document.getElementById("checkboxes2");
	if (!expanded) {
	  checkboxes2.style.display = "block";
	  expanded = true;
	} else {
	  checkboxes2.style.display = "none";
	  expanded = false;
	}
  
   
	 e.stopPropagation();
  
  }
  function showCheckboxes3(e) {
	var checkboxes3 = document.getElementById("checkboxes3");
	if (!expanded) {
	  checkboxes3.style.display = "block";
	  expanded = true;
	} else {
	  checkboxes3.style.display = "none";
	  expanded = false;
	}
  
   
	 e.stopPropagation();
  
  }

var allSelectedAquirer;
function getCheckBoxValue(){
	var allInputCheckBox = document.getElementsByClassName("myCheckBox");
		 
		 var allSelectedAquirer = [];
		 for(var i=0; i<allInputCheckBox.length; i++){
			 
			 if(allInputCheckBox[i].checked){
				 allSelectedAquirer.push(allInputCheckBox[i].value);	
			 }
		 }
		 document.getElementById('selectBox').setAttribute('title', allSelectedAquirer.join());
		 
		 if(allSelectedAquirer.join().length>28){
			 var res = allSelectedAquirer.join().substring(0,27);
			 document.querySelector("#selectBox option").innerHTML = res+'...............';
		 }else if(allSelectedAquirer.join().length==0){
			 document.querySelector("#selectBox option").innerHTML = 'ALL';
		 }else{
			 document.querySelector("#selectBox option").innerHTML = allSelectedAquirer.join();
		 }
}
function getCheckBoxValue1(){
	var allInputCheckBox = document.getElementsByClassName("myCheckBox1");
		 
		 var allSelectedAquirer = [];
		 for(var i=0; i<allInputCheckBox.length; i++){
			 
			 if(allInputCheckBox[i].checked){
				 allSelectedAquirer.push(allInputCheckBox[i].value);	
			 }
		 }
		 document.getElementById('selectBox1').setAttribute('title', allSelectedAquirer.join());
		 
		 if(allSelectedAquirer.join().length>28){
			 var res = allSelectedAquirer.join().substring(0,27);
			 document.querySelector("#selectBox1 option").innerHTML = res+'...............';
		 }else if(allSelectedAquirer.join().length==0){
			 document.querySelector("#selectBox1 option").innerHTML = 'ALL';
		 }else{
			 document.querySelector("#selectBox1 option").innerHTML = allSelectedAquirer.join();
		 }
}
function getCheckBoxValue2(){
	var allInputCheckBox = document.getElementsByClassName("myCheckBox2");
		 
		 var allSelectedAquirer = [];
		 for(var i=0; i<allInputCheckBox.length; i++){
			 
			 if(allInputCheckBox[i].checked){
				 allSelectedAquirer.push(allInputCheckBox[i].value);	
			 }
		 }
		 document.getElementById('selectBox2').setAttribute('title', allSelectedAquirer.join());
		 
		 if(allSelectedAquirer.join().length>28){
			 var res = allSelectedAquirer.join().substring(0,27);
			 document.querySelector("#selectBox2 option").innerHTML = res+'...............';
		 }else if(allSelectedAquirer.join().length==0){
			 document.querySelector("#selectBox2 option").innerHTML = 'ALL';
		 }else{
			 document.querySelector("#selectBox2 option").innerHTML = allSelectedAquirer.join();
		 }
}
function getCheckBoxValue3(){
	var allInputCheckBox = document.getElementsByClassName("myCheckBox3");
		 
		 var allSelectedAquirer = [];
		 for(var i=0; i<allInputCheckBox.length; i++){
			 
			 if(allInputCheckBox[i].checked){
				 allSelectedAquirer.push(allInputCheckBox[i].value);	
			 }
		 }
		 document.getElementById('selectBox3').setAttribute('title', allSelectedAquirer.join());
		 
		 if(allSelectedAquirer.join().length>28){
			 var res = allSelectedAquirer.join().substring(0,27);
			 document.querySelector("#selectBox3 option").innerHTML = res+'...............';
		 }else if(allSelectedAquirer.join().length==0){
			 document.querySelector("#selectBox3 option").innerHTML = 'ALL';
		 }else{
			 document.querySelector("#selectBox3 option").innerHTML = allSelectedAquirer.join();
		 }
}
$(document).ready(function(){
	$(document).click(function(){
		expanded = false;
		$('#checkboxes').hide();
	});
	$('#checkboxes').click(function(e){
		e.stopPropagation();
	});

	$(document).click(function(){
		expanded = false;
		$('#checkboxes1').hide();
	});
	$('#checkboxes1').click(function(e){
		e.stopPropagation();
	});
	$(document).click(function(){
		expanded = false;
		$('#checkboxes2').hide();
	});
	$('#checkboxes2').click(function(e){
		e.stopPropagation();
	});
	$(document).click(function(){
		expanded = false;
		$('#checkboxes3').hide();
	});
	$('#checkboxes3').click(function(e){
		e.stopPropagation();
	});
});


function handleChange() {
if(!document.getElementById("snapshotPermission").getAttribute("data-permission").includes('View Snapshot'))return;
	lineChart();
	//statisticsSettled();
	var currentDate = new Date();
	var first = currentDate.getDate();
	if (currentPeriod == 'day')
		var last = currentDate.getDate() + 1;
	if (currentPeriod == 'weak')
		var last = first - 6;
	if (currentPeriod == 'month')
		var last = first - 30;
	if (currentPeriod == 'year')
		var last = first - 365;
	var dateFrom = new Date(currentDate.setDate(first));
	var dateTo = new Date(currentDate.setDate(last));
	if (currentPeriod == 'day')
		statistics(dateFrom, dateTo);
		 else
		 statistics(dateTo, dateFrom);
	if(currentPeriod == 'custom'){
	var currentDate = new Date();
		//var first = $("#dateFrom").val();
		//var last = $("#dateTo").val();
		var dateFrom = document.getElementById("dateFrom").value;
		var dateTo = document.getElementById("dateTo").value;
		var dateFrom2 = ddmm_to_mmdd(dateFrom);
		var dateTo2	=	ddmm_to_mmdd(dateTo);
		var dateFrom = ddmm_to_yymm(dateFrom);
		var dateTo	=	ddmm_to_yymm(dateTo);
	
		const date1 = new Date(dateFrom);
		const date2 = new Date(dateTo);
		

		//const date1 = new Date(ddmm_to_mmdd(first));
		//const date2 = new Date(ddmm_to_mmdd(last));


		//Find difference between two dates endp
		if (date1 > date2) {
			
			//$('#loader-wrapper').hide();
			alert('From date must be before the to date');
			//$('#customDateModal').hide();
			$('#dateFrom').focus();
			$('#buttonCustom').addClass('btnActive');
			//statistics(date1, date2);
			return false;
			
		}

		 //console.log(date1)
		 //console.log(date2)
	
			statistics(date1, date2);
		
	}
		
	
	

}
function getMonthlyData() {
	$('#threeMonth').removeClass('btnActive');
	$('#oneMonth').addClass('btnActive');
	$('.monthlyGet').removeClass('btnActive');
	lineChart();

}
function getThreeMonthData() {
	
	$('#oneMonth').removeClass('btnActive');
	$('#threeMonth').addClass('btnActive');
	$('.monthlyGet').removeClass('btnActive');
	monthlylineChart();
}
function getCustomMonthData() {
	$('.monthlyGet').addClass('btnActive');
	$('#oneMonth').removeClass('btnActive');
	$('#threeMonth').removeClass('btnActive');
	customMonthlyLineChart();
}
function getHourlyData() {
	$('.todayDataGet').addClass('btnActive');

	var dateFromTime = "00:00";
	var dateToTime = "23:59";
	var regex = /(\d+)\:(\d+)/;

	dateFromParts = dateFromTime.match(regex)
	dateToParts = dateToTime.match(regex);

	dateFromHours = dateFromParts[1] < 13 ? parseInt(dateFromParts[1], 10) : parseInt(dateFromParts[1], 10) + 12;
	dateToHours = dateFromParts[1] < 13 ? parseInt(dateToParts[1], 10) : parseInt(dateToParts[1], 10) + 12;
	const date1 = new Date(dateFromHours);
	const date2 = new Date(dateToHours);
	if (date1 > date2) {
		
		alert('From time must be before  to time');
		$('#timepicker').focus();
		return false;
	} else {
		todayLineChart();
		$("#showToday").show();
	}

}
function getPaymentTypeData() {
	$('.paymentDataGet').addClass('btnActive');
	var dateFrom = document.getElementById('dateFromPie').value.split('-'),
				dateTo = document.getElementById('dateToPie').value.split('-'),
				myDateFrom = new Date(dateFrom[2], dateFrom[1], dateFrom[0]), //Year, Month, Date  
				myDateTo = new Date(dateTo[2], dateTo[1], dateTo[0]), //Year, Month, Date 
				oneDay = 24*60*60*1000; // hours*minutes*seconds*milliseconds


		   if (myDateTo >= myDateFrom) {  
				  var diffDays = Math.round(Math.abs((myDateFrom.getTime() - myDateTo.getTime())/(oneDay)));
				  if(diffDays>31){
					alert('No. of days can not be more than 31');
					//$('#dateFromPie').focus();
				  }else{
					  
					statisticsPie();
					statisticsPieMop();
	$("#showPie").show();

						 
						
				  }
		   }else {  
				alert("'Date From' must be before the 'Date To'.");
				$('#dateFromPie').focus();
		   } 
	
}
function getSettledData() {
	$('.settledDataGet').addClass('btnActive');
	var dateFrom = document.getElementById("dateFromSettlement").value;
	var dateTo = document.getElementById("dateToSettlement").value;
	var dateFrom = ddmm_to_yymm(dateFrom);
	var dateTo	=	ddmm_to_yymm(dateTo);

	const date1 = new Date(dateFrom);
	const date2 = new Date(dateTo);
	const diffTime = Math.abs(date2 - date1);
	const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

	//Find difference between two dates endp
	if (date1 > date2) {

		alert('From date must be before the to date');
		$('#dateFromSettlement').focus();
		return false;
	}else{
		statisticsSettled();
        $("#showSettled").show();
	}
	

}
function getHitsData() {
	$('.funnelDataGet').addClass('btnActive');
	var dateFrom = document.getElementById('dateFromFunnel').value.split('-'),
	dateTo = document.getElementById('dateToFunnel').value.split('-'),
	myDateFrom = new Date(dateFrom[2], dateFrom[1], dateFrom[0]), //Year, Month, Date  
	myDateTo = new Date(dateTo[2], dateTo[1], dateTo[0]), //Year, Month, Date 
	oneDay = 24*60*60*1000; // hours*minutes*seconds*milliseconds


if (myDateTo >= myDateFrom) {  
	  var diffDays = Math.round(Math.abs((myDateFrom.getTime() - myDateTo.getTime())/(oneDay)));
	  if(diffDays>31){
		alert('No. of days can not be more than 31');
		//$('#dateFromPie').focus();
	  }else{
		  
		statisticsFunnel();
		$("#showFunnel").show();
	
	  }
}else {  
	alert("'Date From' must be before the 'Date To'.");
	$('#dateFromFunnel').focus();
} 
	
}




$(document).ready(function () {

	$(' button').click(function () {
		$(' button').removeClass('btnActive');
		$(this).addClass('btnActive');
	});
	$('#buttonCustom').click(function () {
		$('#customDateModal').show();

	});
	$("#customButton").click(function (env) {
		$('#buttonCustom').addClass('btnActive');
		currentPeriod = "custom";
		var currentDate = new Date();
		
		var dateFrom = document.getElementById("dateFrom").value;
		var dateTo = document.getElementById("dateTo").value;
		var dateFrom2 = ddmm_to_mmdd(dateFrom);
		var dateTo2	=	ddmm_to_mmdd(dateTo);
		var dateFrom = ddmm_to_yymm(dateFrom);
		var dateTo	=	ddmm_to_yymm(dateTo);
	
		const date1 = new Date(dateFrom);
		const date2 = new Date(dateTo);
		

		
		if (date1 > date2) {
		
			alert('From date must be before the to date');
		
			$('#dateFrom').focus();
			$('#buttonCustom').addClass('btnActive');
			return false;
		}

		statistics(date1, date2);

	});

	$("#buttonDay").click(function (env) {
	    if(!$("#currency").val()){
            console.log("Currency not selected")
            return;
        }
		$('#customDateModal').hide();

		currentPeriod = "day";
		var currentDate = new Date();
		var first = currentDate.getDate();
		var last = currentDate.getDate();
		var dateFrom = new Date(currentDate.setDate(first));
		var dateTo = new Date(currentDate.setDate(last));

		statistics(dateFrom, dateTo);


	});


	$("#buttonWeekly").click(function (env) {
	    if(!$("#currency").val()){
            console.log("Currency not selected")
            return;
        }
		$('#customDateModal').hide();
		currentPeriod = "weak";
		var currentDate = new Date();
		var first = currentDate.getDate();
		var last = first - 6;
		var dateTo = new Date(currentDate.setDate(first));
		var dateFrom = new Date(currentDate.setDate(last));
		statistics(dateFrom, dateTo);


	});

	$("#buttonMonthly").click(function (env) {
	    if(!$("#currency").val()){
            console.log("Currency not selected")
            return;
        }
		$('#customDateModal').hide();
		currentPeriod = "month";
		var currentDate = new Date();
		var first = currentDate.getDate();
		var last = first - 30;
		var dateTo = new Date(currentDate.setDate(first));
		var dateFrom = new Date(currentDate.setDate(last));
		statistics(dateFrom, dateTo);

	});


	$("#buttonYearly").click(function (env) {
	    if(!$("#currency").val()){
            console.log("Currency not selected")
            return;
        }
		$('#customDateModal').hide();
		currentPeriod = "year";
		var currentDate = new Date();
		var first = currentDate.getDate();
		var last = first - 365;
		var dateTo = new Date(currentDate.setDate(first));
		var dateFrom = new Date(currentDate.setDate(last));

		statistics(dateFrom, dateTo);

	});

});



function statistics(dateFrom, dateTo) {

	if(!document.getElementById("snapshotPermission").getAttribute("data-permission").includes('View Snapshot'))return;
	document.getElementById("dvTotalSuccess").innerHTML = "<img src='../image/loading_horizon.gif' width='20' height='16'>";
	document.getElementById("dvTotalFailed").innerHTML = "<img src='../image/loading_horizon.gif' width='20' height='16'>";
	document.getElementById("dvTotalRefunded").innerHTML = "<img src='../image/loading_horizon.gif' width='20' height='16'>";
	document.getElementById("dvRefundedAmount").innerHTML = "<img src='../image/loading_horizon.gif' width='20' height='16'>";
	document.getElementById("dvApprovedAmount").innerHTML = "<img src='../image/loading_horizon.gif' width='20' height='16'>";
	//document.getElementById("dvTotalRejected").innerHTML = "<img src='../image/loading_horizon.gif' width='20' height='16'>";
	//document.getElementById("dvTotalDropped").innerHTML = "<img src='../image/loading_horizon.gif' width='20' height='16'>";
	document.getElementById("dvTotalCancelled").innerHTML = "<img src='../image/loading_horizon.gif' width='20' height='16'>";
	document.getElementById("dvTotalFraud").innerHTML = "<img src='../image/loading_horizon.gif' width='20' height='16'>";
	document.getElementById("dvTotalSettledAmount").innerHTML = "<img src='../image/loading_horizon.gif' width='20' height='16'>";
	//document.getElementById("dvTotalInvalid").innerHTML = "<img src='../image/loading_horizon.gif' width='20' height='16'>";


	var token = document.getElementsByName("token")[0].value;
	$
		.ajax({
			url: "statisticsActionCapture",
			timeout: 0,
			type: "POST",
			data: {
				dateFrom: dateFrom,
				dateTo: dateTo,
				emailId: document.getElementById("merchant").value,
				currency: document.getElementById("currency").value,
				token: token,
				"struts.token.name": "token",
			},
			success: function (data) {
				//document.getElementById("dvTotalSuccess").innerHTML = data.statistics.totalSuccess;
				document.getElementById("dvTotalSuccess").innerHTML = inrFormat(data.statistics.totalSuccess);
				//document.getElementById("dvTotalSuccess").innerHTML = inrFormat(data.statistics.totalSuccess);
				//console.log('data.statistics.approvedAmount) : ' + data.statistics.approvedAmount);
				document.getElementById("dvApprovedAmount").innerHTML = inrFormat(data.statistics.approvedAmount);

				// document.getElementById("dvApprovedAmount").innerHTML = data.statistics.approvedAmount

				statisticsRefund(dateFrom, dateTo);
				//statisticsAll(dateFrom,dateTo);
			},
			error: function (data) {

			}
		});

}



function statisticsRefund(dateFrom, dateTo) {
	if(!document.getElementById("snapshotPermission").getAttribute("data-permission").includes('View Snapshot'))return;
	var token = document.getElementsByName("token")[0].value;
	$
		.ajax({
			url: "statisticsActionRefund",
			timeout: 0,
			type: "POST",
			data: {
				dateFrom: dateFrom,
				dateTo: dateTo,
				emailId: document.getElementById("merchant").value,
				currency: document.getElementById("currency").value,
				token: token,
				"struts.token.name": "token",
			},
			success: function (data) {
				//document.getElementById("dvTotalRefunded").innerHTML =  data.statistics.totalRefunded;	
				document.getElementById("dvTotalRefunded").innerHTML = inrFormat(data.statistics.totalRefunded);
				document.getElementById("dvRefundedAmount").innerHTML = inrFormat(data.statistics.refundedAmount);
				statisticsAll(dateFrom,dateTo);
			},
			error: function (data) {

			}
		});

}


function statisticsAll(dateFrom, dateTo) {
	if(!document.getElementById("snapshotPermission").getAttribute("data-permission").includes('View Snapshot'))return;
	var token = document.getElementsByName("token")[0].value;
	$
		.ajax({
			url: "statisticsAction",
			timeout: 0,
			type: "POST",
			data: {
				dateFrom: dateFrom,
				dateTo: dateTo,
				emailId: document.getElementById("merchant").value,
				currency: document.getElementById("currency").value,
				token: token,
				"struts.token.name": "token",
			},
			success: function (data) {

				document.getElementById("dvTotalFailed").innerHTML = inrFormat(data.statistics.totalFailed);
				//document.getElementById("dvTotalRejected").innerHTML = data.statistics.totalRejectedDeclined;
				//document.getElementById("dvTotalDropped").innerHTML = data.statistics.totalDropped;
				document.getElementById("dvTotalCancelled").innerHTML = inrFormat(data.statistics.totalCancelled);
				document.getElementById("dvTotalFraud").innerHTML = inrFormat(data.statistics.totalFraud);
				//document.getElementById("dvTotalInvalid").innerHTML = data.statistics.totalInvalid;

				// Below method for calculating the Total Settled Amount 
				statisticsSettledAmount(dateFrom,dateTo);
			},
			error: function (data) {

			}
		});

}

function statisticsSettledAmount(dateFrom,dateTo) {
	if(!document.getElementById("snapshotPermission").getAttribute("data-permission").includes('View Snapshot'))return;
	   var token = document.getElementsByName("token")[0].value;
	   $
			   .ajax({
				   url : "statisticsSettledAmountAction",
				   timeout : 0,
				   type : "POST",
				   data : {
					   dateFrom : dateFrom,
					   dateTo : dateTo,
					   emailId : document.getElementById("merchant").value,
					   currency : document.getElementById("currency").value,
					   token : token,
					   "struts.token.name" : "token",
				   },
				   success : function(data) {
					   
				   document.getElementById("dvTotalSettledAmount").innerHTML = inrFormat(data.statistics.totalSettledAmount);

				   },
				   error : function(data) {
		   
	   }
			   });

}


function lineChart() {
	
	if(!document.getElementById("monthlyPermission").getAttribute("data-permission").includes('View Monthly Transactions'))return;

	var acquirer = [];
	var inputElements = document.getElementsByName('acquirer');
	for (var i = 0; inputElements[i]; ++i) {
		if (inputElements[i].checked) {
			acquirer.push(inputElements[i].value);

		}
	}
	var acquirerString = acquirer.join();
	document.getElementById("loading").style.display = "block";
	var token = document.getElementsByName("token")[0].value;
	var	paymentMethods = document.getElementById("paymentMethods").value;
	var	mopType = document.getElementById("mopType").value;
	var transactionType = document.getElementById("transactionTypeMonthly").value;
	var acquirerString = acquirer.join();

	if(paymentMethods==''){
		paymentMethods='ALL'
	}
	if(transactionType==''){
		transactionType='ALL'
	}

	if(acquirerString==''){
		acquirerString='ALL'
	}
	// create the loading chart
	var chart = new Highcharts.chart({
		chart: {
			title: {
				text: 'Monthly Transaction',

			},
			renderTo: 'colouredRoundedLineChart'
		},
		xAxis: {
			title: {
				text: 'Date',
				x: -20
			}

		}
	});
	chart.setTitle({ text: "Fetching data.." });
	chart.showLoading();

	var month = new Date().getMonth() + 1;
	var year = new Date().getFullYear();
	var date = new Date().getDate();
	var dateFrom = year + '-' +  ((('' + month).length < 2) ? ('0' + month) : month)   + '-01' ;
	var dateTo = year + '-' + ((('' + month).length < 2) ? ('0' + month) : month) + '-' + date  ;

	$.ajax({
		url: "lineChartAction",
		timeout: 0,
		type: "POST",
		data: {
			emailId: document.getElementById("merchant").value,
			currency: document.getElementById("currency").value,
			dateFrom: dateFrom,
			dateTo: dateTo,
			mopType : mopType,
			transactionType : transactionType,
			paymentType : paymentMethods,
			acquirer : acquirerString,
			token: token,
			"struts.token.name": "token",
		},
		success: function (data) {
			var today = new Date();
			var initDate = new Date(today.getFullYear(),today.getMonth() ,01);
			//	var initDate = new Date(today.getMonth()+ 1 +'-01-'+today.getFullYear());
				$('#dateToMonth').val($.datepicker.formatDate('dd-mm-yy', today));
				$('#dateFromMonth').val($.datepicker.formatDate('dd-mm-yy', initDate));
		
			
			document.getElementById("loading").style.display = "none";
			var a = [];
			var b = [];
			var c = [];
			var d = [];
			//var e = [];
			var dateArray = [];
			var pieChartList = data.pieChart;
			// const date1 = new Date(document.getElementById("dateFromMonth").value);
			// const date2 = new Date(document.getElementById("dateToMonth").value);
			// const diffTime = Math.abs(date2 - date1);
			// const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

			for (var i = new Date(dateFrom).getDate() - 1; i < new Date(dateTo).getDate(); i++) {
				var piechart = pieChartList[i];
				var success = parseInt(piechart.totalSuccess);
				var refund = parseInt(piechart.totalRefunded);
				var failled = parseInt(piechart.totalFailed);
				var cancelled = parseInt(piechart.totalCancelled);
				//var timeout = parseInt(piechart.totalTimeouts);
				//var error = parseInt(piechart.totalErrors);
				a.push(success);
				b.push(refund);
				c.push(failled);
				d.push(cancelled);
				//d.push(timeout);
				//e.push(error);
				var txnDateMonth = new Date(piechart.txndate).toLocaleString('default', { month: 'short' })
				dateArray.push((piechart.txndate).split('-')[2] + '-' + txnDateMonth + '-' + (piechart.txndate).split('-')[0]);

			}

			for (var i = new Date(dateFrom).getDate(); i <= new Date(dateTo).getDate(); i++) {
				dateArray.push(i.toString());
			}
			//console.log(dateArray);
			//return;
			$(function () {
				var colors = ['#2BB88E', '#2369AE', '#DF2938', '#064E67'];
				$('#colouredRoundedLineChart').highcharts(
					{
						title: {
							text: 'Monthly Transaction',
							x: -20
							//center
						},

						allButtonsEnabled: true,
						subtitle: {
							text: '',
							x: -20
						},
						xAxis: {
							title: {
								text: 'Date'
							},
							categories: dateArray
						},
						yAxis: {
							title: {
								text: 'Number of Transactions'
							},
							plotLines: [{
								value: 0,
								width: 1,
								color: '#808080'
							}]
						},
						credits: {
							enabled: false
						},
						tooltip: {
							valueSuffix: ''
						},
						exporting: {
							sourceWidth: 700,
							sourceHeight: 400,
							scale: 1 
						},
						legend: {
							layout: 'vertical',
							align: 'right',
							verticalAlign: 'middle',
							borderWidth: 0
						},
						series: [{
							name: 'Total Success',
							data: a,
							color: colors[0]
						}, {
							name: 'Total Refunded',
							data: b,
							color: colors[1]
						}, {
							name: 'Total Failed',
							data: c,
							color: colors[2]
						},
						 {
							name: 'Total Cancelled',
							data: d,
							color: colors[3]
						},
						// {
						//	name: 'Errors',
						//	data: e,
						//	color: colors[4]
					//	}
						],
						responsive: {
							rules: [{
								condition: {
									maxWidth: 500
								},
								chartOptions: {
									legend: {
										align: 'center',
										verticalAlign: 'bottom',
										layout: 'horizontal'
									}
								}
							}]
						}
					});
			});

		},
		error: function (data) {
			document.getElementById("loading").style.display = "none";
		}
	});

}
function monthlylineChart() {
	if(!document.getElementById("monthlyPermission").getAttribute("data-permission").includes('View Monthly Transactions'))return;
	//document.getElementById("loading").style.display = "block";
	var token = document.getElementsByName("token")[0].value;

	// create the loading chart
	var chart = new Highcharts.chart({
		chart: {
			title: {
				text: 'Monthly Transaction',

			},
			renderTo: 'colouredRoundedLineChart'
		},
		xAxis: {
			title: {
				text: 'Date',
				x: -20
			}

		}
	});
	chart.setTitle({ text: "Fetching data.." });
	chart.showLoading();

	var currentMonth = new Date().getMonth() + 1;
	var currentYear = new Date().getFullYear();
	var prevMonthDate = new Date().setMonth(currentMonth - 3);
	var prevMonth = new Date(prevMonthDate).getMonth() + 1;
	var prevYear = new Date(prevMonthDate).getFullYear();
	var currentDate = new Date().getDate();

	var dateFrom = prevYear + '-' + ((('' + prevMonth).length < 2) ? ('0' + prevMonth) : prevMonth) + '-01';
	var dateTo = currentYear + '-' + ((('' + currentMonth).length < 2) ? ('0' + currentMonth) : currentMonth) + '-' + ((('' + currentDate).length < 2) ? ('0' + currentDate) : currentDate);

	$.ajax({
		url: "monthlyLineChartAction",
		timeout: 0,
		type: "POST",
		data: {
			emailId: document.getElementById("merchant").value,
			currency: document.getElementById("currency").value,
			dateFrom: dateFrom,
			dateTo: dateTo,
			token: token,
			"struts.token.name": "token",
		},
		success: function (data) {
			var a = [];
			var b = [];
			var c = [];
			var d = [];
			var e = [];
			var dateArray = [];
			var pieChartList = data.pieChart;

			//Find difference between two dates start

			const date1 = new Date(dateFrom);
			const date2 = new Date(dateTo);
			const diffTime = Math.abs(date2 - date1);
			const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

			//Find difference between two dates end

			for (var i = 0; i < diffDays + 1; i++) {
				var piechart = pieChartList[i];
				if (piechart == undefined) break;
				var success = parseInt(piechart.totalSuccess);
				var refund = parseInt(piechart.totalRefunded);
				var failled = parseInt(piechart.totalFailed);
			//	var timeout = parseInt(piechart.totalTimeouts);
			//	var error = parseInt(piechart.totalErrors);
				a.push(success);
				b.push(refund);
				c.push(failled);
			//	d.push(timeout);
			//	e.push(error);
				var txnDateMonth = new Date(piechart.txndate).toLocaleString('default', { month: 'short' })
				dateArray.push((piechart.txndate != undefined) ? (txnDateMonth + '-' + (piechart.txndate).split('-')[0]) : piechart.txndate);
				//	console.log(dateArray);

			}

			$(function () {
				$('#colouredRoundedLineChart').highcharts(
					{
						title: {
							text: 'Monthly Transaction',
							x: -20
							//center
						},

						allButtonsEnabled: true,
						subtitle: {
							text: '',
							x: -20
						},
						xAxis: {
							title: {
								text: 'Date'
							},
							categories: dateArray
						},
						yAxis: {
							title: {
								text: 'Number of Transactions'
							},
							plotLines: [{
								value: 0,
								width: 1,
								color: '#808080'
							}]
						},
						credits: {
							enabled: false
						},
						tooltip: {
							valueSuffix: ''
						},
						legend: {
							layout: 'vertical',
							align: 'right',
							verticalAlign: 'middle',
							borderWidth: 0
						},
						series: [{
							name: 'Total Success',
							data: a
						}, {
							name: 'Total Refunded',
							data: b
						}, {
							name: 'Total Failed',
							data: c
						}
						//,{
						//	name: 'Timeout',
						//	data: d
						//}, {
						//	name: 'Errors',
						//	data: e
						//}
						
						],
						responsive: {
							rules: [{
								condition: {
									maxWidth: 500
								},
								chartOptions: {
									legend: {
										align: 'center',
										verticalAlign: 'bottom',
										layout: 'horizontal'
									}
								}
							}]
						}
					});
			});

		},
		error: function (data) {
			document.getElementById("loading").style.display = "none";
		}
	});

}

function ddmm_to_mmdd(date){
	var dateArr =date.split('-');
	return dateArr[1]+'-'+dateArr[0]+'-'+dateArr[2];
}

function ddmm_to_yymm(date){
	var dateArr =date.split('-');
	return dateArr[2]+'-'+dateArr[1]+'-'+dateArr[0];
}


function customMonthlyLineChart() {
	if(!document.getElementById("monthlyPermission").getAttribute("data-permission").includes('View Monthly Transactions'))return;
	//Find difference between two dates start
	var acquirer = [];
	var inputElements = document.getElementsByName('acquirer');
	for (var i = 0; inputElements[i]; ++i) {
		if (inputElements[i].checked) {
			acquirer.push(inputElements[i].value);

		}
	}
	var acquirerString = acquirer.join();
	var	paymentMethods = document.getElementById("paymentMethods").value;
	var	mopType = document.getElementById("mopType").value;
	var transactionType = document.getElementById("transactionTypeMonthly").value;
	var acquirerString = acquirer.join();

	if(paymentMethods==''){
		paymentMethods='ALL'
	}
	if(transactionType==''){
		transactionType='ALL'
	}

	if(acquirerString==''){
		acquirerString='ALL'
	}
	var dateFrom = document.getElementById("dateFromMonth").value;
	var dateTo = document.getElementById("dateToMonth").value;
	var dateFrom2 = ddmm_to_mmdd(dateFrom);
	var dateTo2	=	ddmm_to_mmdd(dateTo);
	var dateFrom = ddmm_to_yymm(dateFrom);
	var dateTo	=	ddmm_to_yymm(dateTo);

	const date1 = new Date(dateFrom);
	const date2 = new Date(dateTo);
	const diffTime = Math.abs(date2 - date1);
	const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

	//Find difference between two dates endp
	if (date1 > date2) {
		//$('#loader-wrapper').hide();
		alert('From date must be before the to date');
		$('#dateFromMonth').focus();
		return false;
	}
	if (diffDays > 30) {
		alert("No. of days can not be more than 31'");
		return;
	}

	// var dateFromDate = dateFrom.split('-')[1];
	// var dateFromMonth = dateFrom.split('-')[0];
	// var dateFromYear = dateFrom.split('-')[2];

	// var dateToDate = dateTo.split('-')[1];
	// var dateToMonth = dateTo.split('-')[0];
	// var dateToYear = dateTo.split('-')[2];



	// dateFrom = dateFromYear + '-' + ((('' + dateFromMonth).length < 2) ? ('0' + dateFromMonth) : dateFromMonth) + '-' + ((('' + dateFromDate).length < 2) ? ('0' + dateFromDate) : dateFromDate);
	// dateTo = dateToYear + '-' + ((('' + dateToMonth).length < 2) ? ('0' + dateToMonth) : dateToMonth) + '-' + ((('' + dateToDate).length < 2) ? ('0' + dateToDate) : dateToDate);



	document.getElementById("loading").style.display = "block";
	var token = document.getElementsByName("token")[0].value;

	// create the loading chart
	var chart = new Highcharts.chart({
		chart: {
			title: {
				text: 'Monthly Transaction',

			},
			renderTo: 'colouredRoundedLineChart'
		},
		xAxis: {
			title: {
				text: 'Date',
				x: -20
			}

		}
	});
	chart.setTitle({ text: "Fetching data.." });
	chart.showLoading();

	$.ajax({
		url: "customMonthlyLineChartAction",
		timeout: 0,
		type: "POST",
		data: {
			emailId: document.getElementById("merchant").value,
			currency: document.getElementById("currency").value,
			dateFrom: dateFrom,
			dateTo: dateTo,
			mopType : mopType,
			transactionType : transactionType,
			paymentType : paymentMethods,
			acquirer : acquirerString,
			token: token,
			"struts.token.name": "token",
		},
		success: function (data) {
			document.getElementById("loading").style.display = "none";
			var a = [];
			var b = [];
			var c = [];
			var d = [];
			var e = [];
			var dateArray = [];
			var pieChartList = data.pieChart;

			for (var i = 0; i < diffDays + 1; i++) {
				var piechart = pieChartList[i];
				if (piechart == undefined) break;
				var success = parseInt(piechart.totalSuccess);
				var refund = parseInt(piechart.totalRefunded);
				var failled = parseInt(piechart.totalFailed);
				var cancelled = parseInt(piechart.totalCancelled);
				//var timeout = parseInt(piechart.totalTimeouts);
				//var error = parseInt(piechart.totalErrors);
				a.push(success);
				b.push(refund);
				c.push(failled);
				d.push(cancelled);
			//	e.push(error);
				var txnDateMonth = new Date(piechart.txndate).toLocaleString('default', { month: 'short' })
				dateArray.push((piechart.txndate).split('-')[2] + '-' + txnDateMonth + '-' + (piechart.txndate).split('-')[0]);
				//console.log(dateArray);
			}



			$(function () {
				var colors = ['#2BB88E', '#2369AE', '#DF2938',  '#064E67'];
				$('#colouredRoundedLineChart').highcharts(
					{
						title: {
							text: 'Monthly Transaction',
							x: -20
							//center
						},

						allButtonsEnabled: true,
						subtitle: {
							text: '',
							x: -20
						},
						xAxis: {
							title: {
								text: 'Date'
							},
							categories: dateArray
						},
						yAxis: {
							title: {
								text: 'Number of Transactions'
							},
							plotLines: [{
								value: 0,
								width: 1,
								color: '#808080'
							}]
						},
						credits: {
							enabled: false
						},
						tooltip: {
							valueSuffix: ''
						},
						exporting: {
							sourceWidth: 700,
							sourceHeight: 400,
							scale: 1 
						},
						legend: {
							layout: 'vertical',
							align: 'right',
							verticalAlign: 'middle',
							borderWidth: 0
						},
						series: [{
							name: 'Total Success',
							data: a,
							color: colors[0]
						}, {
							name: 'Total Refunded',
							data: b,
							color: colors[1]
						}, {
							name: 'Total Failed',
							data: c,
							color: colors[2]
						}, 
						{
							name: 'Total Cancelled',
							data: d,
							color: colors[3]
						},
						// {
						//	name: 'Errors',
						//	data: e,
						//	color: colors[4]
						//}
						],
						responsive: {
							rules: [{
								condition: {
									maxWidth: 500
								},
								chartOptions: {
									legend: {
										align: 'center',
										verticalAlign: 'bottom',
										layout: 'horizontal'
									}
								}
							}]
						}
					});
			});

		},
		error: function (data) {
			document.getElementById("loading").style.display = "none";
		}
	});

}

function todayLineChart() {
	if(!document.getElementById("monthlyPermission").getAttribute("data-permission").includes('View Hourly Transactions'))return;
	document.getElementById("loading").style.display = "block";
	var token = document.getElementsByName("token")[0].value;
	var acquirer = [];
	var inputElements = document.getElementsByName('acquirerHourly');
	for (var i = 0; inputElements[i]; ++i) {
		if (inputElements[i].checked) {
			acquirer.push(inputElements[i].value);

		}
	}
	var acquirerString = acquirer.join();
	var	paymentMethods = document.getElementById("paymentMethodsHourly").value;
	var	mopType = document.getElementById("mopTypeHourly").value;
	var transactionType = document.getElementById("transactionTypeHourly").value;
	var acquirerString = acquirer.join();

	if(paymentMethods==''){
		paymentMethods='ALL'
	}
	if(transactionType==''){
		transactionType='ALL'
	}

	if(acquirerString==''){
		acquirerString='ALL'
	}
	// create the loading chart
	var chart = new Highcharts.chart({
		chart: {
			title: {
				text: "Today's Transaction",

			},
			renderTo: 'graph'
		},
		xAxis: {
			title: {
				text: 'Date',
				x: -20
			}

		}
	});
	chart.setTitle({ text: "Fetching data.." });
	chart.showLoading();

	var dateFrom = new Date();
	var dateTo = new Date();
	var dateFromTime = "00:00";
	var dateToTime = "23:59";
	var today = new Date();
	var time = today.getHours() + ":" + today.getMinutes() + ":" + today.getSeconds();
	var selectDateHourly = document.getElementById('selectDateHourly').value;
	var selectDateHourly = ddmm_to_yymm(selectDateHourly);
	var regex = /(\d+)\:(\d+)/;

	dateFromParts = dateFromTime.match(regex)
	dateToParts = dateToTime.match(regex);

	dateFromHours = parseInt(dateFromParts[1], 10);
	dateToHours = parseInt(dateToParts[1], 10);

	const date1 = new Date(dateFromHours);
	const date2 = new Date(dateToHours);


	//Find difference between two dates endp
	if (date1 > date2) {
		//$('#loader-wrapper').hide();
		alert('From time must be before  to time');
		$('#timepicker').focus();
		return false;
	}


	// dateFromMinutes = parseInt(dateFromParts[2], 10);
	// dateToMinutes = parseInt(dateToParts[2], 10);

	// dateFrom.setHours(dateFromHours);
	// dateFrom.setMinutes(dateFromMinutes);

	// dateTo.setHours(dateToHours);
	// dateTo.setMinutes(dateToMinutes);
// if(selectDateHourly == (dateFrom.getFullYear()) + '-' + ((('' + (dateFrom.getMonth() + 1)).length < 2) ? ('0' + (dateFrom.getMonth() + 1)) : (dateFrom.getMonth() + 1)) + '-' + ((('' + dateFrom.getDate()).length < 2) ? ('0' + dateFrom.getDate()) : dateFrom.getDate())){
// 	dateFrom = (dateFrom.getFullYear()) + '-' + ((('' + (dateFrom.getMonth() + 1)).length < 2) ? ('0' + (dateFrom.getMonth() + 1)) : (dateFrom.getMonth() + 1)) + '-' + ((('' + dateFrom.getDate()).length < 2) ? ('0' + dateFrom.getDate()) : dateFrom.getDate()) + ' ' + dateFromTime + ':00';
// 	dateTo = (dateTo.getFullYear()) + '-' + ((('' + (dateTo.getMonth() + 1)).length < 2) ? ('0' + (dateTo.getMonth() + 1)) : (dateTo.getMonth() + 1)) + '-' + ((('' + dateTo.getDate()).length < 2) ? ('0' + dateTo.getDate()) : dateTo.getDate()) + ' ' + time ;
	
// }else{
// 	dateFrom = (dateFrom.getFullYear()) + '-' + ((('' + (dateFrom.getMonth() + 1)).length < 2) ? ('0' + (dateFrom.getMonth() + 1)) : (dateFrom.getMonth() + 1)) + '-' + ((('' + dateFrom.getDate()).length < 2) ? ('0' + dateFrom.getDate()) : dateFrom.getDate()) + ' ' + dateFromTime + ':00';
// 	dateTo = (dateTo.getFullYear()) + '-' + ((('' + (dateTo.getMonth() + 1)).length < 2) ? ('0' + (dateTo.getMonth() + 1)) : (dateTo.getMonth() + 1)) + '-' + ((('' + dateTo.getDate()).length < 2) ? ('0' + dateTo.getDate()) : dateTo.getDate()) + ' ' + dateToTime + ':59';
	
// }
if(selectDateHourly == (dateFrom.getFullYear()) + '-' + ((('' + (dateFrom.getMonth() + 1)).length < 2) ? ('0' + (dateFrom.getMonth() + 1)) : (dateFrom.getMonth() + 1)) + '-' + ((('' + dateFrom.getDate()).length < 2) ? ('0' + dateFrom.getDate()) : dateFrom.getDate())){
	dateFrom = selectDateHourly + ' ' + dateFromTime + ':00';
	dateTo = selectDateHourly + ' ' + time
}
else{
	dateFrom =selectDateHourly + ' ' + dateFromTime + ':00';
	dateTo = selectDateHourly + ' ' + dateToTime + ':59';
	
}
	// console.log(dateFrom);
	//console.log(dateTo);
	//return;

	$.ajax({
		url: "todaysLineChartAction",
		timeout: 0,
		type: "POST",
		data: {
			emailId: document.getElementById("merchant").value,
			currency: document.getElementById("currency").value,
			dateFrom: dateFrom,
			dateTo: dateTo,
			selectDateHourly : selectDateHourly,
			mopType : mopType,
			transactionType : transactionType,
			paymentType : paymentMethods,
			acquirer : acquirerString,
			token: token,
			"struts.token.name": "token",
		},
		success: function (data) {
			document.getElementById("loading").style.display = "none";
			var a = [];
			var b = [];
			var c = [];
			var d = [];

			var pieChartList = data.pieChart;
			for (var i = 0; i < pieChartList.length-1; i++) {
				var piechart = pieChartList[i];
				var success = parseInt(piechart.totalSuccess);
				var refund = parseInt(piechart.totalRefunded);
				var failled = parseInt(piechart.totalFailed);
				var cancelled = parseInt(piechart.totalCancelled);

				a.push(success);
				b.push(refund);
				c.push(failled);
				d.push(cancelled);
			}

			var timeArray = [];

			for (var j = dateFromHours ; j < dateToHours + 1; j++) {
				timeArray.push(j+1 + ':00');
			}

			var colors = ['#2BB88E', '#2369AE', '#DF2938','#064E67'];
			$('#graph').highcharts(
				
				{
					title: {
						text: "Hourly Transaction",
						x: -20
						//center
					},

					allButtonsEnabled: true,
					subtitle: {
						text: '',
						x: -20
					},
					xAxis: {
						title: {
							text: 'Hours'
						},
						categories: timeArray
					},
					yAxis: {
						title: {
							text: 'Number of Transactions'
						},
						plotLines: [{
							value: 0,
							width: 1,
							color: '#808080'
						}]
					},
					credits: {
						enabled: false
					},
					tooltip: {
						valueSuffix: ''
					},
					exporting: {
						sourceWidth: 700,
						sourceHeight: 400,
						scale: 1 
					},
					legend: {
						layout: 'vertical',
						align: 'right',
						verticalAlign: 'middle',
						borderWidth: 0
					},
					series: [{
						name: 'Total Success',
						data: a,
						color: colors[0]
					}, {
						name: 'Total Refunded',
						data: b,
						color: colors[1]
					}, {
						name: 'Total Failed',
						data: c,
						color: colors[2]
					},
					{
						name: 'Total Cancelled',
						data: d,
						color: colors[3]
					}],
					responsive: {
						rules: [{
							condition: {
								maxWidth: 500
							},
							chartOptions: {
								legend: {
									align: 'center',
									verticalAlign: 'bottom',
									layout: 'horizontal'
								}
							}
						}]
					}
				});

		},
		error: function (data) {
			document.getElementById("loading").style.display = "none";
		}
	});

}




function intChart(val1, val2, val3, val4, val5) {
	Highcharts.chart('chartBox', {
		chart: {
			plotBackgroundColor: null,
			plotBorderWidth: null,
			plotShadow: false,
			type: 'pie'
		},
		title: {
			text: 'Payment Types comparison'
		},
		tooltip: {
			pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
		},
		accessibility: {
			point: {
				valueSuffix: '%'
			}
		},
		exporting: {
			sourceWidth: 700,
			sourceHeight: 400,
			scale: 1 
		},
		plotOptions: {
			pie: {
		// 		borderWidth: 0,
        // borderColor: null,
				allowPointSelect: true,
				cursor: 'pointer',
				dataLabels: {
					enabled: true,
					format: '<b>{point.name}</b>: {point.percentage:.1f} %'
            
				},
				showInLegend: true
			}
		},
		series: [{
			name: 'Share',
			colorByPoint: true,
			data: [{
				name: 'Credit Card',
				y: parseFloat(val1),
				//sliced: false,
				//selected: true
			}, {
				name: 'Debit Card',
				y: parseFloat(val2)
			}, {
				name: 'UPI',
				y: parseFloat(val3)
			}, {
				name: 'Wallet',
				y: parseFloat(val4)
			}, {
				name: 'Net Banking',
				y: parseFloat(val5)
			}]
			
		}]
	}, function (chart) { // on complete
		if (val1==0.00) {
			chart.renderer.text('No Data Available', 160, 200)
			.css({
				color: '#4572A7',
				fontSize: '12px'
			})
			.add();
		}
		else {chart.renderer.text('', 160, 200)
			.css({
				color: '#4572A7',
				fontSize: '16px'
			})
			.add();
		}
	});
}


function intChartPaymentWise(val1, val2, val3, val4, val5) {
	Highcharts.chart('chartBox', {
		chart: {
			plotBackgroundColor: null,
			plotBorderWidth: null,
			plotShadow: false,
			type: 'pie'
		},
		title: {
			text: 'Payment Types comparison'
		},
		tooltip: {
			pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
		},
		accessibility: {
			point: {
				valueSuffix: '%'
			}
		},
		plotOptions: {
			pie: {
				allowPointSelect: true,
				cursor: 'pointer',
				dataLabels: {
					enabled: true,
					format: '<b>{point.name}</b>: {point.percentage:.1f} %'
				},
				showInLegend: true
			}
		},
		series: [{
			name: 'Share',
			colorByPoint: true,
			data: [{
				name: 'Success',
				y: parseFloat(val1),
				sliced: false,
				selected: true
			}, {
				name: 'Cancelled',
				y: parseFloat(val3)
			},  {
				name: 'Failed',
				y: parseFloat(val2)
			}, 
			
			//{
			//	name: 'Fraud',
			//	y: parseFloat(val5)
			//},
			{
				name: 'Invalid',
				y: parseFloat(val4)
			}
			// , {
			// 	name: 'Dropped',
			// 	y: parseFloat(val6)
			// }, {
			// 	name: 'Rejected',
			// 	y: parseFloat(val7)
			// }
		]
		}]
	}, function (chart) { 

		chart.renderer.text('No Data Available', 160, 200)
			.css({
				color: '#4572A7',
				fontSize: '12px',
				textAlign: 'center'
			})
			.add();

	});
}
function intChartMopWise(val1, val2, val3, val4, val5) {
	Highcharts.chart('chartBox1', {
		chart: {
			plotBackgroundColor: null,
			plotBorderWidth: null,
			plotShadow: false,
			type: 'pie'
		},
		title: {
			text: 'Mop Types comparison'
		},
		tooltip: {
			pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
		},
		accessibility: {
			point: {
				valueSuffix: '%'
			}
		},
		plotOptions: {
			pie: {
				allowPointSelect: true,
				cursor: 'pointer',
				dataLabels: {
					enabled: true,
					format: '<b>{point.name}</b>: {point.percentage:.1f} %'
				},
				showInLegend: true
			}
		},
		series: [{
			name: 'Share',
			colorByPoint: true,
			data: [{
				name: 'Success',
				y: parseFloat(val1),
				sliced: false,
				selected: true
			},  {
				name: 'Cancelled',
				y: parseFloat(val3)
			},{
				name: 'Failed',
				y: parseFloat(val2)
			},
			//{
			//	name: 'Fraud',
			//	y: parseFloat(val5)
			//},  
			
			{
				name: 'Invalid',
				y: parseFloat(val4)
			}, 
			// {
			// 	name: 'Dropped',
			// 	y: parseFloat(val6)
			// }, {
			// 	name: 'Rejected',
			// 	y: parseFloat(val7)
			// }
		]
		}]
	}, function (chart) { 

		chart.renderer.text('No Data Available', 160, 200)
			.css({
				color: '#4572A7',
				fontSize: '12px',
				textAlign: 'center'
			})
			.add();

	});
}

// function selectMop(){
// 	if(mopType == "ALL"){
// 		document.getElementById("moppie").style.display = "none";
// 	}
// 	else{
// 		document.getElementById("moppie").style.display = "block";
// 	}
// 	//$('#moppie').attr(display,"block");
	
// }
function statisticsPie() {
	if(!document.getElementById("monthlyPermission").getAttribute("data-permission").includes('View Payment type Comparison'))return;

	var acquirer = [];
	var inputElements = document.getElementsByName('acquirerPie');
	for (var i = 0; inputElements[i]; ++i) {
		if (inputElements[i].checked) {
			acquirer.push(inputElements[i].value);

		}
	}
	var acquirerString = acquirer.join();
	
	var	paymentMethods = document.getElementById("paymentMethodsPie").value;
	var	mopType = document.getElementById("mopTypePie").value;
	var transactionType = document.getElementById("transactionTypePayment").value;
	var acquirerString = acquirer.join();

	// var emailId = document.getElementById("merchant").value;
	var merchantEmailId = document.getElementById("merchant").value;
	var dateFrom = document.getElementById("dateFromPie").value;
	var dateTo = document.getElementById("dateToPie").value;
	//var paymentMethods = "ALL";


	if (merchantEmailId == '' || merchantEmailId == 'ALL MERCHANTS') {
		merchantEmailId = 'ALL'
	}
	if(paymentMethods==''){
		paymentMethods='ALL'
	}
	if(transactionType==''){
		transactionType='ALL'
	}

	if (acquirer == '') {
		acquirer = 'ALL'
	}

	document.getElementById("loading").style.display = "block";
	var token = document.getElementsByName("token")[0].value;
	$
		.ajax({
			url: "analyticsDataAction",
			timeout: 0,
			type: "POST",
			data: {
				paymentMethods: paymentMethods,
				dateFrom: dateFrom,
				dateTo: dateTo,
				//merchantEmailId: "ALL",
				merchantEmailId : merchantEmailId,
				token: token,
				mopType : "ALL",
			transactionType : transactionType,
			paymentMethods : paymentMethods,
			acquirer : acquirerString,
				//acquirer : 'ALL',
			},
		
			success: function (data) {
				document.getElementById("loading").style.display = "none";



				if (paymentMethods=='ALL'){
					intChartPaymentWise("0.00","0.00","0.00","0.00","0.00","0.00","0.00");
					intChart(data.analyticsData.CCTxnPercent,data.analyticsData.DCTxnPercent,data.analyticsData.UPTxnPercent,data.analyticsData.WLTxnPercent,data.analyticsData.NBTxnPercent);
				}
				else{
					intChart("0.00","0.00","0.00","0.00","0.00");
					intChartPaymentWise(data.analyticsData.capturedPercent,data.analyticsData.failedPercent,data.analyticsData.cancelledPercent,
					data.analyticsData.invalidPercent);
				}
				document.getElementById("loading").style.display = "none"
				
				},
				error : function(data) {
				document.getElementById("loading").style.display = "none"
				}
		});

}
function statisticsPieMop() {
	if(!document.getElementById("monthlyPermission").getAttribute("data-permission").includes('View Payment type Comparison'))return;
	var acquirer = [];
	var inputElements = document.getElementsByName('acquirerPie');
	for (var i = 0; inputElements[i]; ++i) {
		if (inputElements[i].checked) {
			acquirer.push(inputElements[i].value);

		}
	}
	var acquirerString = acquirer.join();
	
	var	paymentMethods = document.getElementById("paymentMethodsPie").value;
	var	mopType = document.getElementById("mopTypePie").value;
	var transactionType = document.getElementById("transactionTypePayment").value;
	var acquirerString = acquirer.join();

	// var emailId = document.getElementById("merchant").value;
	var merchantEmailId = document.getElementById("merchant").value;
	var dateFrom = document.getElementById("dateFromPie").value;
	var dateTo = document.getElementById("dateToPie").value;
	//var paymentMethods = "ALL";


	if (merchantEmailId == '' || merchantEmailId == 'ALL MERCHANTS') {
		merchantEmailId = 'ALL'
	}
	if(paymentMethods==''){
		paymentMethods='ALL'
	}
	if(transactionType==''){
		transactionType='ALL'
	}

	if (acquirer == '') {
		acquirer = 'ALL'
	}

	document.getElementById("loading").style.display = "block";
	var token = document.getElementsByName("token")[0].value;
	$
		.ajax({
			url: "analyticsDataAction",
			timeout: 0,
			type: "POST",
			data: {
				paymentMethods: paymentMethods,
				dateFrom: dateFrom,
				dateTo: dateTo,
				merchantEmailId : merchantEmailId,
				token: token,
				mopType : mopType,
			transactionType : transactionType,
			acquirer : acquirerString			},
		
			success: function (data) {
				document.getElementById("loading").style.display = "none";



				if (mopType=='ALL'){
					intChartMopWise("0.00","0.00","0.00","0.00","0.00","0.00","0.00");
					document.getElementById("moppie").style.display = "none";
					//$("#moppie").removeClass("beforethemop");
				}
				else{
					
					//intChartMopWise("5.00","9.00","60.00","4.00","22.00");
					intChartMopWise(data.analyticsData.capturedPercent,data.analyticsData.failedPercent,data.analyticsData.cancelledPercent,
						data.analyticsData.invalidPercent);
						document.getElementById("moppie").style.display = "block";
						//$("#moppie").addClass("beforethemop");
				}
				document.getElementById("loading").style.display = "none"
				
				},
				error : function(data) {
				document.getElementById("loading").style.display = "none"
				}
		});

}



function hitsChart(_hitsChart) {
	
	var colors = ['#2369AE', '#2BB88E', '#DF2938', '#FCB414','#064E67'];
	var series = [];
	if(_hitsChart.totalTxnCount != undefined && _hitsChart.totalTxnCount > 0){
		series = [{
			name: 'Count',
			colorByPoint: true,
			data: [{
				name: 'Hits',
				y: parseFloat(_hitsChart.totalTxnCount),
				sliced: false,
				selected: true,
				color: colors[0],
				
				

			}, {
				name: 'Success',
				y: parseFloat(_hitsChart.successTxnCount),
				color: colors[1]
			},  {
				name: 'Failed',
				y: parseFloat(_hitsChart.failedTxnCount),
				color: colors[2]
			}, 
			// {
			// 	name: 'Timeout',
			// 	y: parseFloat(_hitsChart.timeoutTxnCount),
			// 	color: colors[3]
			// },
			{
				name: 'Cancelled',
				y: parseFloat(_hitsChart.cancelledTxnCount),
				color: colors[4]
			}, {
				name: 'Invalid',
				y: parseFloat(_hitsChart.invalidTxnCount),
				color: colors[5]
			}]
		}]
	}
	Highcharts.setOptions({
    lang: {
      decimalPoint: '.',
      thousandsSep: ','
    }
});
	Highcharts.chart('funnel', {
		chart: {
			plotBackgroundColor: null,
			plotBorderWidth: null,
			plotShadow: false,
			type: 'funnel'
		},
		title: {
			text: 'Hits Vs Captured'
		},
		exporting: {
			sourceWidth: 700,
			sourceHeight: 400,
			scale: 1 
		},
		tooltip: {
			//valueSuffix: ''
			//pointFormat: '<span>{point.y:.f}</span>',
			pointFormatter: function() {
				//console.log(this.y);
				return inrFormat(this.y)  ;
			  }
		  },
		  
		
			accessibility: {
				point: {
					valueSuffix: ''
				}
			},
		plotOptions: {
			series: {
				allowPointSelect: true,
				cursor: 'pointer',
				dataLabels: {
					enabled: true,
				//format: '<b>{point.name}</b> ({point.y:,.0f})',
				pointFormatter: function() {
					//console.log(this.y);
					return "<span style='color:{point.color}'></span> " + this.point.name + "( <b>" + inrFormat(this.y ) +""+ "</b> )<br/>";
					//return inrFormat(this.y)  ;
				  },
                softConnector: true

				},
				center: ['40%', '50%'],
            neckWidth: '30%',
            neckHeight: '25%',
            width: '80%',
				showInLegend: true
			}
		},
		
		series: series
	}, function (chart) { // on complete
		if (_hitsChart.totalTxnCount==0.00) {
			chart.renderer.text('No Data Available', 235, 200)
			.css({
				color: '#4572A7',
				fontSize: '16px'
			})
			.add();
		}
		else{
		chart.renderer.text('', 230, 120)
			.css({
				color: '#4572A7',
				fontSize: '16px',
				textAlign:'center',
			})
			.add();
		}

	});
}


// function yymm_to_ddmm(date){
// 	var dateArr =date.split('-');
// 	return dateArr[1]+'-'+dateArr[0]+'-'+dateArr[2];
// }
function statisticsFunnel() {

	if(!document.getElementById("monthlyPermission").getAttribute("data-permission").includes('View Hits Vs Captured'))return;
	//var acquirerString = acquirer.join();
	// var emailId = document.getElementById("merchant").value;
	var merchantEmailId = document.getElementById("merchant").value;
	var currency = document.getElementById("currency").value;
	//var dateFrom = document.getElementById("dateFromFunnel").value;
	//var dateTo = document.getElementById("dateToFunnel").value;
	var dateFrom = document.getElementById("dateFromFunnel").value;
	var dateTo = document.getElementById("dateToFunnel").value;
	var dateFrom2 = ddmm_to_mmdd(dateFrom);
	var dateTo2	=	ddmm_to_mmdd(dateTo);
	//var dateFrom3 = ddmm_to_yymm(dateFrom);
	//var dateTo3	=	ddmm_to_yymm(dateTo);


	const date1 = new Date(dateFrom2);
	const date2 = new Date(dateTo2);
	const diffTime = Math.abs(date2 - date1);
	const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

	//Find difference between two dates endp
	if (date1 > date2) {
		//$('#loader-wrapper').hide();
		alert('From date must be before the to date');
		$('#dateFromFunnel').focus();
		return false;
	}
	if (diffDays > 30) {
		alert("No. of days can not be more than 31'");
		return;
	}


	var funnelChatData = "ALL";
	var acquirer = [];
	var inputElements = document.getElementsByName('acquirerFunnel');
	for (var i = 0; inputElements[i]; ++i) {
		if (inputElements[i].checked) {
			acquirer.push(inputElements[i].value);

		}
	}
	var acquirerString = acquirer.join();
	var	paymentMethods = document.getElementById("paymentMethodsFunnel").value;
	var	mopType = document.getElementById("mopTypeFunnel").value;
	var transactionType = document.getElementById("transactionTypeFunnel").value;
	var acquirerString = acquirer.join();

	if(paymentMethods==''){
		paymentMethods='ALL'
	}
	if(transactionType==''){
		transactionType='ALL'
	}

	if(acquirerString==''){
		acquirerString='ALL'
	}

	if (merchantEmailId == '' || merchantEmailId == 'ALL MERCHANTS') {
		merchantEmailId = 'ALL'
	}
	if (funnelChatData == '') {
		funnelChatData = 'ALL'
	}

	document.getElementById("loading").style.display = "block";
	var token = document.getElementsByName("token")[0].value;
	$
		.ajax({	
			url: "funnelChartDataAction",
			timeout: 0,
			type: "POST",
			data: {

				merchantEmailId: merchantEmailId,
				currency: currency,
				token: token,
				dateFrom: dateFrom,
				dateTo: dateTo,
				mopType : mopType,
			transactionType : transactionType,
			paymentType : paymentMethods,
			acquirer : acquirerString,
				

			},
			success: function (data) {
				document.getElementById("loading").style.display = "none";



				if (funnelChatData == 'ALL') {
					hitsChart(data.funnelChatData);

				}
				else {
				}
				//document.getElementById("loading").style.display = "none"

			},
			error: function (data) {
				document.getElementById("loading").style.display = "none"
			}
		});

}
function settledAmount(data) {
	Highcharts.setOptions({
		lang: {
		  decimalPoint: '.',
		  thousandsSep: ','
		}
	});
	//console.log(data);
	var chart = Highcharts.chart('settlement', {

		title: {
			text: 'Settlement'
		},
		exporting: {
			sourceWidth: 700,
			sourceHeight: 400,
			scale: 1 
		},

		xAxis: {
			title: {
				text: 'Date'
			},
			categories: data[0]
			
		},
		yAxis: {
			title: {
				text: 'Amount'
			},
		},
		tooltip: {
			//valueSuffix: ''
			pointFormat: '<span>{point.y:,.2f}</span>',
			pointFormatter: function() {
				return inrFormat(this.y)  ;
			  }
		  },
		  

		series: [{
			type: 'column',
			//colorByPoint: true,
			data: data[1],
			color: "#2369AE",
			showInLegend: false
		}]


	});

}

// settelmentData = [121,232,546]
// settelmentCatg = ["Mon","Tue","Wed"]

function statisticsSettled() {
	if(!document.getElementById("monthlyPermission").getAttribute("data-permission").includes('View Settlement'))return;
	var merchantEmailId = document.getElementById("merchant").value;
	var currency = document.getElementById("currency").value;
	var dateFrom = document.getElementById("dateFromSettlement").value;
	var dateTo = document.getElementById("dateToSettlement").value;
	var dateFrom = ddmm_to_yymm(dateFrom);
	var dateTo	=	ddmm_to_yymm(dateTo);

	const date1 = new Date(dateFrom);
	const date2 = new Date(dateTo);
	const diffTime = Math.abs(date2 - date1);
	const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

	//Find difference between two dates endp
	if (date1 > date2) {

		alert('From date must be before the to date');
		$('#dateFromSettlement').focus();
		return false;
	}
	if (diffDays > 365) {
		alert("No. of days can not be more than 365'");
		return;
	}
	// var dateFrom = document.getElementById("dateFromSettlement").value;
	// var dateTo = document.getElementById("dateToSettlement").value;
	var settlementData = "ALL";
	var acquirer = [];
	var inputElements = document.getElementsByName('acquirersettled');
	for (var i = 0; inputElements[i]; ++i) {
		if (inputElements[i].checked) {
			acquirer.push(inputElements[i].value);

		}
	}
	var acquirerString = acquirer.join();
	var	paymentMethods = document.getElementById("paymentMethodsSettled").value;
	//var	mopType = document.getElementById("mopType").value;
	//var transactionType = document.getElementById("transactionType").value;
	var acquirerString = acquirer.join();

	if(paymentMethods==''){
		paymentMethods='ALL'
	}
	// if(transactionType==''){
	// 	transactionType='ALL'
	// }

	if(acquirerString==''){
		acquirerString='ALL'
	}
	if (merchantEmailId == '' || merchantEmailId == 'ALL MERCHANTS') {
		merchantEmailId = 'ALL'
	}
	if (settlementData == '') {
		settlementData = 'ALL'
	}



	document.getElementById("loading").style.display = "block";
	var token = document.getElementsByName("token")[0].value;
	$
		.ajax({
			url: "settlementChartAction",
			timeout: 0,
			type: "POST",
			data: {

				dateFrom: dateFrom,
				dateTo: dateTo,
				currency: currency,
				token: token,
				merchantEmailId: merchantEmailId,
				//mopType : mopType,
			//transactionType : transactionType,
			paymentType : paymentMethods,
			acquirer : acquirerString,
			},
			success: function (data) {
				document.getElementById("loading").style.display = "none";



				if (settlementData == 'ALL') {
					settledAmount(data.settlementData);

				}
				else {
				}
				document.getElementById("loading").style.display = "none"

			},
			error: function (data) {
				document.getElementById("loading").style.display = "none"
			}
		});

}

function arrangeAcord() {
	//ajax call 
	$.ajax({
		type: "POST",
		dataType: "JSON",
		url: 'dashBoardPreferenceSetAction',
		//timeout : 0,
		data: {
			emailId: document.getElementById("merchant").value,
			token: token

		},
		success: function (data) {
			var acord = [];
			var order = data.preferenceSetConstant;  // [{order:0, id: "card3"},{order:1, id: "card5"},{order:2, id: "card1"},{order:3, id: "card2"},{order:4, id: "card4"}];

			$.each(order, function (k, v) {
				acord.push($("#" + v.id));
				$("#" + v.id).remove();
			});
			//$("#sortable").html("");
			$.each(acord, function (k, v) {
				$("#sortable").append(v);
			});
			
		}
	});



}

$(function () {
	$("#sortable").sortable({
		start: function (event, ui) {
		},
		receive: function (event, ui) {
		},
		stop: function (event, ui) {
			var IDs = [];
			// setTimeout(function () {
				$("#sortable").find("div.card_graph").each(function (i) { IDs.push({ order: i, id: this.id }); });				
				var token = document.getElementsByName("token")[0].value;
				
				$.ajax({
					type: "POST",
					url: 'changeDashBoardPreferenceSetAction',
					timeout: 0,
					data: {
						emailId: document.getElementById("merchant").value,
						token: token,
						preferenceSetConstant: JSON.stringify(IDs),
					},
					success: function (data) {


					}
				})
	
				
			// }, 1000);
	

				
		}
	});
});


$(function () {
	$("#sortable").sortable();
	$("#sortable").disableSelection();
 
	// setTimeout(function () {
	$.ajax({
		type: "POST",
		url: 'dashBoardPreferenceSetAction',
		data: {
			emailId: document.getElementById("merchant").value,
			token: document.getElementsByName("token")[0].value,
		},
		success: function (data) {
			var acord = [];
			var order = [];
			if(data.preferenceSetConstant !=null)
			var order = JSON.parse(data.preferenceSetConstant); 
			//[{order:0, id: "card3"},{order:1, id: "card5"},{order:2, id: "card1"},{order:3, id: "card2"},{order:4, id: "card4"}];
			if (order.length>0) {
				$.each(order, function (k, v) {
					acord.push($("#" + v.id));
					$("#" + v.id).remove();
				});
				//$("#sortable").html("");
				$.each(acord, function (k, v) {
					$("#sortable").append(v);
				});
				$( "#sortable" ).sortable({
					axis: 'y',
					containment: "parent",
					// cursorAt: { left: 5 },
					helper: "clone",
					// opacity: 1,
					tolerance: "pointer",
					cursor: "move",
					distance: 5,
					
				 })
				//$("#dateFromMonth").datepicker();
			}
			$(function () {
				$("#dateFromMonth").datepicker({
					prevText: "click for previous months",
					nextText: "click for next months",
					showOtherMonths: true,
					dateFormat: 'dd-mm-yy',
					selectOtherMonths: false,
					maxDate: new Date()
				});
				$("#dateToMonth").datepicker({
					prevText: "click for previous months",
					nextText: "click for next months",
					showOtherMonths: true,
					dateFormat: 'dd-mm-yy',
					selectOtherMonths: false,
					maxDate: new Date()
				});
			});
		
			$(function () {
				var today = new Date();
				
				var initDate = new Date(today.getFullYear(),today.getMonth() ,01);
				$('#dateToMonth').val($.datepicker.formatDate('dd-mm-yy', today));
				$('#dateFromMonth').val($.datepicker.formatDate('dd-mm-yy', initDate));
		
				lineChart();
		
			});

			$(function () {
				$("#dateFromSettlement").datepicker({
					prevText: "click for previous months",
					nextText: "click for next months",
					showOtherMonths: true,
					dateFormat: 'dd-mm-yy',
					selectOtherMonths: true,
					maxDate: new Date()
				});
				$("#dateToSettlement").datepicker({
					prevText: "click for previous months",
					nextText: "click for next months",
					showOtherMonths: true,
					dateFormat: 'dd-mm-yy',
					selectOtherMonths: true,
					maxDate: new Date()
				});
			});
		
			$(function () {
				var today = new Date();
				$('#dateToSettlement').val($.datepicker.formatDate('dd-mm-yy', today));
				$('#dateFromSettlement').val($.datepicker.formatDate('dd-mm-yy', today));
				statisticsSettled();
		
			});
			$(function () {
				$("#selectDateHourly").datepicker({
					prevText: "click for previous months",
					nextText: "click for next months",
					showOtherMonths: true,
					dateFormat: 'dd-mm-yy',
					selectOtherMonths: true,
					maxDate: new Date()
				});
				
			});
		
			$(function () {
				var today = new Date();
				$('#selectDateHourly').val($.datepicker.formatDate('dd-mm-yy', today));
				
				todayLineChart();
		
			});
			$(".main-panel").scroll(function() {
				//timepickerEl.hide();
				//$(".ui-timepicker-container").hide();
				$("#ui-datepicker-div").hide();
			
				//$('.timepicker').blur();
			  });
			  
			  $(window).resize(function() {
				//timepickerEl.hide();
				//$(".ui-timepicker-container").hide();
				$("#ui-datepicker-div").hide();
				
				//$('.timepicker').blur();
			  });
			
			$(function () {
				$("#dateFromPie").datepicker({
					prevText: "click for previous months",
					nextText: "click for next months",
					showOtherMonths: true,
					dateFormat: 'dd-mm-yy',
					selectOtherMonths: false,
					maxDate: new Date()
				});
				$("#dateToPie").datepicker({
					prevText: "click for previous months",
					nextText: "click for next months",
					showOtherMonths: true,
					dateFormat: 'dd-mm-yy',
					selectOtherMonths: false,
					maxDate: new Date()
				});
			});
		
			$(function () {
				var today = new Date();
				$('#dateToPie').val($.datepicker.formatDate('dd-mm-yy', today));
				$('#dateFromPie').val($.datepicker.formatDate('dd-mm-yy', today));
				statisticsPie();
				statisticsPieMop();
		
			});
		
		
			$(function () {
				$("#dateFromFunnel").datepicker({
					prevText: "click for previous months",
					nextText: "click for next months",
					showOtherMonths: true,
					dateFormat: 'dd-mm-yy',
					selectOtherMonths: false,
					maxDate: new Date()
				});
				$("#dateToFunnel").datepicker({
					prevText: "click for previous months",
					nextText: "click for next months",
					showOtherMonths: true,
					dateFormat: 'dd-mm-yy',
					selectOtherMonths: false,
					maxDate: new Date()
				});
			});
		
			$(function () {
				var today = new Date();
				$('#dateToFunnel').val($.datepicker.formatDate('dd-mm-yy', today));
				$('#dateFromFunnel').val($.datepicker.formatDate('dd-mm-yy', today));
				//statistics();
				statisticsFunnel();
				/*renderTable();*/
			});

		
		
			
		}
	})
	

// },1000);

});










