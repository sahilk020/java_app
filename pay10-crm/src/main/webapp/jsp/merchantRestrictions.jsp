																<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="/struts-tags" prefix="s"%>
<html>

<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">

	<title>Merchant Fraud Prevention System</title>

	<!----------CSS---------------->
	<link rel="stylesheet" href="../css/fonts.css">
	<link rel="stylesheet" href="../css/default.css" rel="stylesheet">
	<link rel="stylesheet" href="../css/fraudPrevention.css" rel="stylesheet">
	<!-- <link rel="stylesheet" href="../css/bootstrap.min.css" rel="stylesheet" /> -->
	<link href="../css/jquery-ui.css" rel="stylesheet" />
	<!-- <link href="../css/ui.theme.css" rel="stylesheet"> -->
	<link href="../css/bootstrap-timepicker.min.css" rel="stylesheet" />
	<link href="../css/bootstrap-datetimepicker-standalone.min.css" rel="stylesheet" />
	<link href="../css/bootstrap-tagsinput.css" rel="stylesheet" />
	<link href="../css/Jquerydatatableview.css" rel="stylesheet" />
	<link href="../css/Jquerydatatable.css" rel="stylesheet" />
	<link href="../css/popup.css" rel="stylesheet" />
	<link href="../css/select2.min.css" rel="stylesheet" />
	<!--------------JAVASCRIPT--------------->
	<script src="../js/jquery.js"></script>
	<script type="text/javascript" src="../js/jquery.min.js"></script>
	<script src="../js/jquery.dataTables.js"></script>
	<script type="text/javascript" src="../js/dataTables.buttons.js"></script>
	<script src="../js/core/popper.min.js"></script>
	<script src="../js/core/bootstrap-material-design.min.js"></script>
	<script src="../js/plugins/perfect-scrollbar.jquery.min.js"></script>
	<!-- Control Center for Material Dashboard: parallax effects, scripts for the example pages etc -->
	<script src="../js/material-dashboard.js?v=2.1.0" type="text/javascript"></script>
	<!-- <script src="../js/bootstrap.min.js" type="text/javascript"></script> -->
	<script src="../js/moment-with-locales.js"></script>
	<script src="../js/bootstrap-datetimepicker.js"></script>
	<script src="../js/jquery.popupoverlay.js"></script>
	<script src="../js/jquery-ui.js"></script>
	<script src="../js/bootstrap-tagsinput.min.js"></script>
	<script src="../js/merchantFraudtype.js"></script>
	<script src="../js/bootstrap-timepicker.min.js"></script>
	<script src="../js/jquery.select2.js" type="text/javascript"></script>
<script>
// 		$("#myModal").on("hidden.bs.modal", function () {
// 			debugger;
// 			document.getElementById("ipAddress").value = "";
// });

	//alert("working")
  

$(document).on('paste', '#search_ipAddListBody,#search_wlIpAddListBody,#search_issuerCountryListBody,#search_emailListBody,#search_cardBinListBody,#search_phoneListBody,#search_cardNoListBody', function(e) {
  e.preventDefault();
  // prevent copying action
  //alert(e.originalEvent.clipboardData.getData('Text'));
  var withoutSpaces = e.originalEvent.clipboardData.getData('Text');
  withoutSpaces = withoutSpaces.replace(/\s+/g, '');
  $(this).val(withoutSpaces);
  // you need to use val() not text()
});
$(document).ready(function(){
    $('input#minTransactionAmount').blur(function(){
		
        var num = parseFloat($(this).val());
        var cleanNum = num.toFixed(2);
        $(this).val(cleanNum);
        if(num/cleanNum < 1){
            $('#error').text('Please enter only 2 decimal places, we have truncated extra points');
            }
        });

    $('input#txnAmountVelocity').blur(function(){
        var num = parseFloat($(this).val());
        var cleanNum = num.toFixed(2);
        $(this).val(cleanNum);
        if(num/cleanNum < 1){
            $('#error2').text('Please enter only 2 decimal places, we have truncated extra points');
            }
		});
		



	});

	$(document).on('keyup', 'input[name=minTransactionAmount]', function() {
  var _this = $(this);
 var min = parseInt(_this.attr('min')) || ""; // if min attribute is not defined, 1 is default
  var max = parseInt(_this.attr('max')) || 9999999999.99; // if max attribute is not defined, 100 is default
  var val = parseInt(_this.val()) || (min - 1); // if input char is not a number the value will be (min - 1) so first condition will be true
  if (val < min)
    _this.val(min);
  if (val > max)
    _this.val(max);
});
$(document).on('keyup', 'input[name=maxTransactionAmount]', function() {
  var _this = $(this);
  var min = parseInt(_this.attr('min')) || ""; // if min attribute is not defined, 1 is default
  var max = parseInt(_this.attr('max')) || 9999999999.99; // if max attribute is not defined, 100 is default
  var val = parseInt(_this.val()) || (min - 1); // if input char is not a number the value will be (min - 1) so first condition will be true
  if (val < min)
    _this.val(min);
  if (val > max)
    _this.val(max);
});
	
	$(document).ready(function(){
    $('input#maxTransactionAmount').blur(function(){
        var num = parseFloat($(this).val());
        var cleanNum = num.toFixed(2);
        $(this).val(cleanNum);
        if(num/cleanNum < 1){
            $('#error').text('Please enter only 2 decimal places, we have truncated extra points');
            }
        });
	});
	
	
$(document).click(function (e) {
	
    if ($(e.target).is('#myModal')) {

		$('#tagipAddress').tagsinput('removeAll');
		$('#tagipAddress .tag.label.label-info').remove();
		if($('#alwaysOnFlag1').prop('checked')){
			$('#alwaysOnFlag1').trigger( "click" );
			var dvTimebased1 = document.getElementById("dvTimebased1");
			dvTimebased1.style.display =  "block";
			
		}
				$('#myModal').modal('hide');
		
    }

});
$(document).click(function (e) {
	
    if ($(e.target).is('#myModal1')) {

		$('#tagwhiteListIpAddress').tagsinput('removeAll');
		$('#tagwhiteListIpAddress .tag.label.label-info').remove();
		if($('#alwaysOnFlag2').prop('checked')){
			$('#alwaysOnFlag2').trigger( "click" );
			var dvTimebased2 = document.getElementById("dvTimebased2");
			dvTimebased2.style.display =  "block";
			
		}
				$('#myModal1').modal('hide');
		
    }

});
$(document).click(function (e) {
	
    if ($(e.target).is('#myModal3')) {

		$('#tagnegativeBin').tagsinput('removeAll');
		$('#tagnegativeBin .tag.label.label-info').remove();
		if($('#alwaysOnFlag3').prop('checked')){
			$('#alwaysOnFlag3').trigger( "click" );
			var dvTimebased3 = document.getElementById("dvTimebased3");
			dvTimebased3.style.display =  "block";
			
		}
				$('#myModal3').modal('hide');
		
    }

});
$(document).click(function (e) {
	
    if ($(e.target).is('#myModal5')) {

		$('#tagemail').tagsinput('removeAll');
		$('#tagemail .tag.label.label-info').remove();
		if($('#alwaysOnFlag5').prop('checked')){
			$('#alwaysOnFlag5').trigger( "click" );
			var dvTimebased5 = document.getElementById("dvTimebased5");
			dvTimebased5.style.display =  "block";
			
		}
				$('#myModal5').modal('hide');
		
    }

});
$(document).click(function (e) {
	
    if ($(e.target).is('#myModal9')) {

		$('#tagphone').tagsinput('removeAll');
		$('#tagphone .tag.label.label-info').remove();
		if($('#alwaysOnFlag9').prop('checked')){
			$('#alwaysOnFlag9').trigger( "click" );
			var dvTimebased9 = document.getElementById("dvTimebased9");
			dvTimebased9.style.display =  "block";
			
		}
				$('#myModal9').modal('hide');
		
    }

});


function tagsAddRule(){

	$("#tagipAddress").css("display","block");
	$("#tagtagipAddress").remove();
	$('#tagipAddress .tag.label.label-info').remove();
	$("#tagwhiteListIpAddress").css("display","block");
	$("#tagtagwhiteListIpAddress").remove();
	$('#tagwhiteListIpAddress .tag.label.label-info').remove();
	$("#tagnegativeBin").css("display","block");
	$("#tagtagnegativeBin").remove();
	$('#tagnegativeBin .tag.label.label-info').remove();
	$("#tagemail").css("display","block");
	$("#tagtagemail").remove();
	$('#tagemail .tag.label.label-info').remove();
	$("#tagphone").css("display","block");
	$("#tagtagphone").remove();
	$('#tagphone .tag.label.label-info').remove();
	
}

	$(document).ready(function(){
		
	
		
$("#blockedRules").click(function() {
  
  var lable = $("#blockedRules").text().trim();

  if(lable == "Blocked") {
	$("#blockedRules").text("Show");
	$(".myText").hide();
  }
  else {
	$("#blockedRules").text("Blocked");
	$(".myText").show();
  }
   
 });
	var preferenceSetOBJ = [];
	var preferenceSetconst = document.getElementById("preferenceSet").value;
	var payId = document.getElementById("payId").value;
	
	if(preferenceSetconst ){
		
		

			preferenceSetOBJ = JSON.parse(preferenceSetconst.replace(/&quot;/g,'"'));
			for(index =0;index<preferenceSetOBJ.length;index++){
				//console.log(preferenceSetOBJ[index]);
				$('#card'+preferenceSetOBJ[index]).show();
							
				}


	}else{
		$('#noRuleSelection').show();
	}


});
</script>


	<script type="text/javascript">

		$(document).ready(function () {
			$.ajaxSetup({
				beforeSend: function () {
					jQuery('body').toggleClass('loaded');
				},
				complete: function () {
					jQuery('body').toggleClass('loaded');
				}
			});
		});
	</script>

	<script type="text/javascript">

		$(function () {
			$(".dateActiveFrom").datepicker({
				numberOfMonths: 1,
				dateFormat: 'dd/mm/yy',
				selectOtherMonths: false,
				minDate: new Date(),
				onSelect: function (selected) {
					$(".dateActiveTo").datepicker("option", "minDate", selected)
				}
			});
			$(".dateActiveTo").datepicker({
				numberOfMonths: 1,
				dateFormat: 'dd/mm/yy',
				selectOtherMonths: false,
				minDate: new Date(),
				onSelect: function (selected) {
					$(".dateActiveFrom").datepicker("option", "maxDate", selected)
				}
			});
		});  
	</script>

	<script type="text/javascript">
		function TimePickerCtrl($) {
			var startTime = $('.startTime').datetimepicker({
				format: 'HH:mm:ss'
			});

			var endTime = $('.endTime').datetimepicker({
				format: 'HH:mm:ss',
				minDate: startTime.data("DateTimePicker").date()
			});

			function setMinDate() {
				return endTime
					.data("DateTimePicker").minDate(
						startTime.data("DateTimePicker").date()
					)
					;
			}

			var bound = false;
			function bindMinEndTimeToStartTime() {

				return bound || startTime.on('dp.change', setMinDate);
			}

			endTime.on('dp.change', () => {
				bindMinEndTimeToStartTime();
				bound = true;
				setMinDate();
			});
		}

		$(document).ready(TimePickerCtrl);
	</script>

	<script type="text/javascript">
		function ShowHideDiv1(alwaysOnFlag1) {
			var dvTimebased1 = document.getElementById("dvTimebased1");
			dvTimebased1.style.display = alwaysOnFlag1.checked ? "none" : "block";
			$('#alwaysOnFlag11').text($('#alwaysOnFlag1').val());
			$("#alwaysOnFlag1").on('change', function () {
				if ($(this).is(':checked')) {
					$(this).attr('value', 'true');
				} else {
					$(this).attr('value', 'false');
				}
				$('#alwaysOnFlag11').text($('#alwaysOnFlag1').val());
			});
		}
		function ShowHideDiv2(alwaysOnFlag2) {
			var dvTimebased2 = document.getElementById("dvTimebased2");
			dvTimebased2.style.display = alwaysOnFlag2.checked ? "none" : "block";
			$('#alwaysOnFlag22').text($('#alwaysOnFlag2').val());
			$("#alwaysOnFlag2").on('change', function () {
				if ($(this).is(':checked')) {
					$(this).attr('value', 'true');
				} else {
					$(this).attr('value', 'false');
				}
				$('#alwaysOnFlag22').text($('#alwaysOnFlag2').val());
			});
		}
		function ShowHideDiv3(alwaysOnFlag3) {
			var dvTimebased3 = document.getElementById("dvTimebased3");
			dvTimebased3.style.display = alwaysOnFlag3.checked ? "none" : "block";
			$('#alwaysOnFlag33').text($('#alwaysOnFlag3').val());
			$("#alwaysOnFlag3").on('change', function () {
				if ($(this).is(':checked')) {
					$(this).attr('value', 'true');
				} else {
					$(this).attr('value', 'false');
				}
				$('#alwaysOnFlag33').text($('#alwaysOnFlag3').val());
			});
		}
		function ShowHideDiv4(alwaysOnFlag4) {
			var dvTimebased4 = document.getElementById("dvTimebased4");
			dvTimebased4.style.display = alwaysOnFlag4.checked ? "none" : "block";
			$('#alwaysOnFlag44').text($('#alwaysOnFlag4').val());
			$("#alwaysOnFlag4").on('change', function () {
				if ($(this).is(':checked')) {
					$(this).attr('value', 'true');
				} else {
					$(this).attr('value', 'false');
				}
				$('#alwaysOnFlag44').text($('#alwaysOnFlag4').val());
			});
		}
		function ShowHideDiv5(alwaysOnFlag5) {
			var dvTimebased5 = document.getElementById("dvTimebased5");
			dvTimebased5.style.display = alwaysOnFlag5.checked ? "none" : "block";
			$('#alwaysOnFlag55').text($('#alwaysOnFlag5').val());
			$("#alwaysOnFlag5").on('change', function () {
				if ($(this).is(':checked')) {
					$(this).attr('value', 'true');
				} else {
					$(this).attr('value', 'false');
				}
				$('#alwaysOnFlag55').text($('#alwaysOnFlag5').val());
			});
		}
		function ShowHideDiv6(alwaysOnFlag6) {
			var dvTimebased6 = document.getElementById("dvTimebased6");
			dvTimebased6.style.display = alwaysOnFlag6.checked ? "none" : "block";
			$('#alwaysOnFlag66').text($('#alwaysOnFlag6').val());
			$("#alwaysOnFlag6").on('change', function () {
				if ($(this).is(':checked')) {
					$(this).attr('value', 'true');
				} else {
					$(this).attr('value', 'false');
				}
				$('#alwaysOnFlag66').text($('#alwaysOnFlag6').val());
			});
		}
		function ShowHideDiv7(alwaysOnFlag7) {
			var dvTimebased7 = document.getElementById("dvTimebased7");
			dvTimebased7.style.display = alwaysOnFlag7.checked ? "none" : "block";
			$('#alwaysOnFlag77').text($('#alwaysOnFlag7').val());
			$("#alwaysOnFlag7").on('change', function () {
				if ($(this).is(':checked')) {
					$(this).attr('value', 'true');
				} else {
					$(this).attr('value', 'false');
				}
				$('#alwaysOnFlag77').text($('#alwaysOnFlag7').val());
			});
		}
		function ShowHideDiv9(alwaysOnFlag9) {
			var dvTimebased9 = document.getElementById("dvTimebased9");
			dvTimebased9.style.display = alwaysOnFlag9.checked ? "none" : "block";
			$('#alwaysOnFlag99').text($('#alwaysOnFlag9').val());
			$("#alwaysOnFlag9").on('change', function () {
				if ($(this).is(':checked')) {
					$(this).attr('value', 'true');
				} else {
					$(this).attr('value', 'false');
				}
				$('#alwaysOnFlag99').text($('#alwaysOnFlag9').val());
			});
		}
	</script>
	<script type="text/javascript">
		$(document).ready(function () {
			//toggle the component with class accordion_body

			$(".accordion_head").click(function () {
				if ($('.ListBody').is(':visible')) {
					$(".ListBody").slideUp(300);
					$(".plusminus").text('+');
				}
				if ($(this).next(".ListBody").is(':visible')) {
					$(this).next(".ListBody").slideUp(300);
					$(this).children(".plusminus").text('+');
				} else {
					$(this).next(".ListBody").slideDown(300);
					$(this).children(".plusminus").text('-');
				}
			});
		});
	</script>
	<script type="text/javascript">
		function isNumberKey(evt) {
			var charCode = (evt.which) ? evt.which : event.keyCode
			if (charCode > 31 && (charCode < 48 || charCode > 57))
				return false;
			return true;
		}
		function isNumberKeyAmount(evt) {
		const charCode = (event.which) ? event.which : event.keyCode;
          if (charCode > 31 &&  (charCode < 48 || charCode > 57) && charCode!=46 ) {
            return false;
			return true;
          }
		}





	</script>
</head>

<body>

	<!--<div id="dialogBox" title="Fraud rule added successfully"></div>-->
	<table width="100%" border="0" cellspacing="0" cellpadding="0" class="txnf">
		

		
									
								
				</table>
			</td>
		</tr>
	</table>
		<s:hidden name="token" value="%{#session.customToken}"></s:hidden>

	<!-- Dynamically generated content of the restriction page for Admin module -->
	
	
	<!--------------------------------------------------------------------------------------------------------------------->
		<div id="myModal" class="modal fade" tabindex="-1" role="dialog" data-backdrop="static"  aria-labelledby="myModalLabel" style="display: none !important">
				<div class="modal-dialog" style="width: 600px;">

					<!-- Modal content-->
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" id="fraudRuleModalClose" data-dismiss="modal">&times;</button>
							<!-- <h4 class="modal-title">Modal Header</h4> -->
						  </div>
						<div id="1" class="modal-body">
							<table class="detailbox table98" cellpadding="20">
							
										<tr>
											<th colspan="2" width="16%" height="30" align="left"
												style="background-color: #496cb6; color: #ffffff; border-top-right-radius: 13px !important;font-size: 16px;">Block Customer IPv4 Address</th>
										</tr>
										<tr>
											<td colspan="2" height="30" align="left">
												<p>
													Enter the IPv4 address and IPv6 address you wish to block.<br>
													<br> e.g. 192.168.100.1<br>
													<br> Once added, all transactions from the IP address will be blocked.
												</p>
											</td>
										</tr>
										<tr>
											<td width="7%">
												<label for="alwaysOnFlag1">
													<input type="checkbox" name="alwaysOnFlag1" id="alwaysOnFlag1" value="false" onclick="ShowHideDiv1(this)" />
													<input type="hidden" name="alwaysOnFlag11" id="alwaysOnFlag11" />
												</label>
											</td>	
											
											<td width="30%">
												<label for="always" style="font-size:16px;">Always</label>
											</td>
										</tr>	
										<tr>
											<td width="8%"><label for="">IP Address <span style="color:red;">*</span></label></td>							
											<td width="31.8%"><span class="" id="validate_err" ></span>
												<input id="ipAddress" type="text" placeholder="192.168.100.1" maxlength="15"
												name="ipAddress" value="" class="form-control ipAddress" data-role="tagsinput" />
											</td>															
										</tr>
																<!-- New Html Code Time Based Fraud Rule-->
										
							<table class="detailbox table98" cellpadding="20" id="dvTimebased1"  >
								<tr>
									<td width="7.37%">
									  <label for="IPdateFrom">Start Date<span style="color:red;">*</span></label>
									</td>
									<td width="30%">
										<div class="startdatepicker date t-ip">
										<input class="form-control wid dateActiveFrom" type="text" id="dateActiveFrom" name="dateActiveFrom" placeholder="DD/MM/YYYY" autocomplete="off" readonly="readonly" />
										<span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span></span>
										</div>
									</td>
								</tr>

								<tr>
									<td width="7.37%">
									  <label for="IPdateTo">End Date<span style="color:red;">*</span></label>
									</td>
									<td width="30%">
										<div class="expiredatepicker date t-ip">
										<input class="form-control wid dateActiveTo" type="text" id="dateActiveTo" name="dateActiveTo" placeholder="DD/MM/YYYY" autocomplete="off" readonly="readonly"/>
										<span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span></span>
										</div>
									</td>
								</tr>

								<tr>
									<td width="7.37%">
									  <label for="startTime" class="">Start Time <span style="color:red;">*</span></label>
									</td>
									<td width="30%">
										<div class="t-ip date">
										<input type="text" class="form-control wid startTime" id="startTime" name="startTime" placeholder="HH:MM:SS" autocomplete="off" />
										<span class="input-group-addon" style="padding: 5px 12px;">
										<span class="glyphicon glyphicon-time"></span>
										</span>
										</div>
									</td>
								</tr>
								
								<tr>
									<td width="7.37%"> 
									  <label for="endTime" class="">End Time<span style="color:red;">*</span></label>
									</td>
									<td width="30%"> 
										<div class="t-ip date">
										<input type="text" class="form-control wid endTime" id="endTime" name ="endTime" placeholder="HH:MM:SS" autocomplete="off"    />
										<span class="input-group-addon" style="padding: 5px 12px;">
										<span class="glyphicon glyphicon-time"></span>
										</span>
										</div>
									</td>
								</tr>
								
								<tr>
									<td width="7.37%">
									   <label for="weeks">Weeks <span style="color:red;">*</span></label>
									</td>
									<td width="30%">
										<ul id="repeatDays" class="checklist-week" data-name="repeatDays">
											<li>
												<label>
													<input type="checkbox" name="repeatDays" id="repeatDays0"  value="3" class="ip4ClassWeeks">
													<span>SUN</span>
												</label>
											</li>
											<li>
												<label>
													<input type="checkbox" name="repeatDays" id="repeatDays1" value="MON" class="ip4ClassWeeks">
													<span>MON</span>
												</label>
											</li>
											<li>
												<label>
													<input type="checkbox" name="repeatDays" id="repeatDays2" value="TUE" class="ip4ClassWeeks">
													<span>TUE</span>
												</label>
											</li>
											<li>
												<label>
													<input type="checkbox" name="repeatDays" id="repeatDays3" value="WED" class="ip4ClassWeeks">
													<span>WED</span>
												</label>
											</li>
											<li>
												<label>
													<input type="checkbox" name="repeatDays" id="repeatDays4" value="THU" class="ip4ClassWeeks">
													<span>THU</span>
												</label>
											</li>
											<li>
												<label>
													<input type="checkbox" name="repeatDays" id="repeatDays5" value="FRI" class="ip4ClassWeeks">
													<span>FRI</span>
												</label>
											</li>
											<li>
												<label>
													<input type="checkbox" name="repeatDays" id="repeatDays6" value="SAT" class="ip4ClassWeeks">
													<span>SAT</span>
												</label>
											</li>
										</ul>
									</td>
								</tr>
							</table>
										<tr>
											<td colspan="2" >
												<div style="display: flex;">
												<button type="submit" value="Block" id="blockIp"
													onclick="ajaxFraudRequest(ipAddressS)" class="btn btn-success btn-sm btn-block" style="margin-left:38%;width:21%;height:100%;margin-top:1%;">Block</button>
													<button type="button" class="btn btn-success btn-sm btn-block" style="width:21%;height:100%;margin-top:1%;" data-dismiss="modal">Close</button>
												</div></td>
										</tr>
										<tr>
											<td>Note: If Always True Flag is checked then Date, Time, Week does not need to be filled but if it is not then all the required fields will have to be filled. </td>
										</tr>
							</table>
						</div>
					</div>
				</div>
		</div>
	
	<!------------------------------------------------------------------------------------------------------------------->
		<div id="myModal1" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
			<div class="modal-dialog" style="width: 600px;">

					<!-- Modal content-->
				<div class="modal-content">
					<div class="modal-header">
						<button type="button" class="close" id="fraudRuleModalClose" data-dismiss="modal">&times;</button>
						<!-- <h4 class="modal-title">Modal Header</h4> -->
					  </div>
					<div id="1" class="modal-body"
							>

						<table class="detailbox table98" cellpadding="20">
								<tr>
									<th colspan="2" width="16%" height="30" align="left"
										style="background-color: #496cb6; color: #ffffff; border-top-right-radius: 13px !important;font-size: 16px;"> White List IPv4 and IPv6 Address</th>
								</tr>
								<tr>
									<td colspan="2" height="30" align="left">
										<p>
											Enter the IPv4 address and IPv6 address you wish to whitelist .<br>
											<br> e.g. 192.168.100.1<br>
											<br> Once added, all transactions from the IP address will be Allowed

										</p>
									</td>
								</tr>
								<tr>
									<td width="8%"><label for="alwaysOnFlag2">
								   <input type="checkbox" name="alwaysOnFlag2" id="alwaysOnFlag2" value="false" onclick="ShowHideDiv2(this)" />
									<input type="hidden" name="alwaysOnFlag22" id="alwaysOnFlag22" /></label>
									</td>
									
									<td width="30%"><label for="always" style="font-size:16px;">Always</label></td>
								</tr>	
								<tr>
									<td width="7%"><label for="">IP Address <span style="color:red;">*</span></label></td>
									<td width="31.8%">
									<span class="" id="validate_err1"></span>
									<input type="text" id="whiteListIpAddress" placeholder="192.168.100.1" maxlength="15"
										name="whiteListIpAddress" class="form-control whiteListIpAddress" data-role="tagsinput" />
									</td>
								</tr>						
								<!-- New Html Code Time Based Fraud Rule-->
								
					<table class="detailbox table98" cellpadding="20" id="dvTimebased2"  >
						<tr>
							<td width="7.37%">
								<label for="WIPdateFrom">Start Date<span style="color:red;">*</span></label>
							</td>
							<td width="30%">
								<div class="startdatepicker date t-ip">
									<input class="form-control wid dateActiveFrom" type="text" id="dateActiveFrom1" name="dateActiveFrom" placeholder="DD/MM/YYYY" autocomplete="off" readonly="readonly"/>
									<span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span></span>
								</div>
							</td>
						</tr>

						<tr>
							<td width="7.37%">
								<label for="WIPdateTo">End Date<span style="color:red;">*</span></label>
							</td>
							<td width="30%">
								<div class="expiredatepicker date t-ip">
									<input class="form-control wid dateActiveTo" type="text" id="dateActiveTo1" name="dateActiveTo" placeholder="DD/MM/YYYY" autocomplete="off" readonly="readonly"/>
									<span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span></span>
								</div>
							</td>
						</tr>

						<tr>
							<td width="7.37%">
								<label for="WIPstartTime" class="">Start Time<span style="color:red;">*</span></label>
							</td>
							<td width="30%">
								<div class=" t-ip startTime">
									<input type="text" class="form-control wid startTime" id="startTime1" name="startTime" placeholder="HH:MM:SS" autocomplete="off"  />
									<span class="input-group-addon" style="padding: 5px 12px;">
									  <span class="glyphicon glyphicon-time"></span>
									</span>
								</div>
							</td>
						</tr>
						
						<tr>
							<td width="7.37%"> <label for="WIPendTime" class="">End Time<span style="color:red;">*</span></label></td>
							<td width="30%"> 
								<div class="date t-ip endTime">
									<input type="text" class="form-control wid endTime" id="endTime1" name ="endTime" placeholder="HH:MM:SS" autocomplete="off"   />
									<span class="input-group-addon" style="padding: 5px 12px;">
									<span class="glyphicon glyphicon-time"></span>
									</span>
								</div>
							</td>
						</tr>
						
						<tr>
							<td width="7.37%"><label for="weeks">Weeks <span style="color:red;">*</span>*</label></td>
							<td width="30%">
								<ul id="repeatDays" class="checklist-week" data-name="whitelistrepeatDays">
									<li>
										<label>
											<input type="checkbox" name="repeatDays" id="repeatDays7"  value="SUN" class="whiteListClass">
											<span>SUN</span>
										</label>
									</li>
									
									<li>
										<label>
											<input type="checkbox" name="repeatDays" id="repeatDays8" value="MON" class="whiteListClass">
											<span>MON</span>
										</label>
									</li>
									
									<li>
										<label>
											<input type="checkbox" name="repeatDays" id="repeatDays9" value="TUE" class="whiteListClass">
											<span>TUE</span>
										</label>
									</li>
									
									<li>
										<label>
											<input type="checkbox" name="repeatDays" id="repeatDays10" value="WED" class="whiteListClass">
											<span>WED</span>
										</label>
									</li>
									
									<li>
										<label>
											<input type="checkbox" name="repeatDays" id="repeatDays11" value="THU" class="whiteListClass">
											<span>THU</span>
										</label>
									</li>
									
									<li>
										<label>
											<input type="checkbox" name="repeatDays" id="repeatDays12" value="FRI" class="whiteListClass">
											<span>FRI</span>
										</label>
									</li>
									
									<li>
										<label>
											<input type="checkbox" name="repeatDays" id="repeatDays13" value="SAT" class="whiteListClass">
											<span>SAT</span>
										</label>
									</li>
								</ul>
							</td>
						</tr>
					</table>
								<tr>
									<td colspan="2">
										<div style="display: flex;">
										<button type="submit" value="Block" id="blockWhiteIp"
											onclick="ajaxFraudRequest(whiteListIpAddressS);" class="btn btn-success btn-sm btn-block" style="margin-left:38%;width:21%;height:100%;margin-top:1%;">Whitelist IP</button>
											<button type="button" class="btn btn-success btn-sm btn-block" style="width:21%;height:100%;margin-top:1%;" data-dismiss="modal">Close</button>
										</div>
										</td>
								</tr>
								<tr>
									<td>Note: If Always True Flag is checked then Date, Time, Week does not need to be filled but if it is not then all the required fields will have to be filled. </td>
								</tr>
						</table>
					</div>
				</div>
			</div>
		</div>
	
	
	<!------------------------------------------------------------------------------------------------------------------------------->
	
	<div id="myModal2" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
		<div class="modal-dialog modal-lg">

			<!-- Modal content-->
			<div class="modal-content" id="5"> 
				<div class="modal-header">
					<button type="button" class="close" id="fraudRuleModalClose" data-dismiss="modal">&times;</button>
				 <h4 class="modal-title colored-header">Block Card Issuer Country</h4> 
				  </div>
				<!-- <div class="modal-header colored-header"></div> -->
					<div class="modal-body" style="overflow:auto;max-height:520px;">
						<ul id="issuerCountry" class="checklist" data-name="country">
							<li><label><input type="checkbox" name="country" value="Antigua Barbuda" class="issuerClass"><span>Antigua Barbuda</span></label></li>
							<li><label><input type="checkbox" name="country" value="Argentina" class="issuerClass"><span>Argentina</span></label></li>
							<li><label><input type="checkbox" name="country" value="Armenia" class="issuerClass"><span>Armenia</span></label></li>
							<li><label><input type="checkbox" name="country" value="Australia" class="issuerClass"><span>Australia</span></label></li>
							<li><label><input type="checkbox" name="country" value="Austria" class="issuerClass"><span>Austria</span></label></li>
							<li><label><input type="checkbox" name="country" value="Azerbaijan" class="issuerClass"><span>Azerbaijan</span></label></li>
							<li><label><input type="checkbox" name="country" value="Bahrain" class="issuerClass"><span>Bahrain</span></label></li>
							<li><label><input type="checkbox" name="country" value="Belgium" class="issuerClass"><span>Belgium</span></label></li>
							<li><label><input type="checkbox" name="country" value="Botswana" class="issuerClass"><span>Botswana</span></label></li>
							<li><label><input type="checkbox" name="country" value="Brazil" class="issuerClass"><span>Brazil</span></label></li>
							<li><label><input type="checkbox" name="country" value="Brunei Darussalam" class="issuerClass"><span>Brunei Darussalam  </span></label></li>
							<li><label><input type="checkbox" name="country" value="Cambodia" class="issuerClass"><span>Cambodia</span></label></li>
							<li><label><input type="checkbox" name="country" value="Cameroon" class="issuerClass"><span>Cameroon</span></label></li>
							<li><label><input type="checkbox" name="country" value="Canada" class="issuerClass"><span>Canada</span></label></li>
							<li><label><input type="checkbox" name="country" value="Chile" class="issuerClass"><span>Chile</span></label></li>
							<li><label><input type="checkbox" name="country" value="China" class="issuerClass"><span>China</span></label></li>
							<li><label><input type="checkbox" name="country" value="Colombia" class="issuerClass"><span>Colombia</span></label></li>
							<li><label><input type="checkbox" name="country" value="Croatia" class="issuerClass"><span>Croatia</span></label></li>
							<li><label><input type="checkbox" name="country" value="Cuba" class="issuerClass"><span>Cuba</span></label></li>
							<li><label><input type="checkbox" name="country" value="Cyprus" class="issuerClass"><span>Cyprus</span></label></li>
							<li><label><input type="checkbox" name="country" value="Czech Republic" class="issuerClass"><span>Czech Republic</span></label></li>
							<li><label><input type="checkbox" name="country" value="Denmark" class="issuerClass"><span>Denmark</span></label></li>
							<li><label><input type="checkbox" name="country" value="Egypt" class="issuerClass"><span>Egypt</span></label></li>
							<li><label><input type="checkbox" name="country" value="Estonia" class="issuerClass"><span>Estonia</span></label></li>
							<li><label><input type="checkbox" name="country" value="Ethiopia" class="issuerClass"><span>Ethiopia</span></label></li>
							<li><label><input type="checkbox" name="country" value="Fiji" class="issuerClass"><span>Fiji</span></label></li>
							<li><label><input type="checkbox" name="country" value="Finland" class="issuerClass"><span>Finland</span></label></li>
							<li><label><input type="checkbox" name="country" value="France" class="issuerClass"><span>France</span></label></li>
							<li><label><input type="checkbox" name="country" value="Germany" class="issuerClass"><span>Germany</span></label></li>
							<li><label><input type="checkbox" name="country" value="Ghana" class="issuerClass"><span>Ghana</span></label></li>
							<li><label><input type="checkbox" name="country" value="Greece" class="issuerClass"><span>Greece</span></label></li>
							<li><label><input type="checkbox" name="country" value="Hong Kong" class="issuerClass"><span>Hong Kong</span></label></li>
							<li><label><input type="checkbox" name="country" value="Hungary" class="issuerClass"><span>Hungary</span></label></li>
							<li><label><input type="checkbox" name="country" value="Iceland" class="issuerClass"><span>Iceland</span></label></li>
							<li><label><input type="checkbox" name="country" value="India" class="issuerClass"><span>India</span></label></li>
							<li><label><input type="checkbox" name="country" value="Indonesia" class="issuerClass"><span>Indonesia</span></label></li>
							<li><label><input type="checkbox" name="country" value="Ireland" class="issuerClass"><span>Ireland</span></label></li>
							<li><label><input type="checkbox" name="country" value="Italy" class="issuerClass"><span>Italy</span></label></li>
							<li><label><input type="checkbox" name="country" value="Jamaica" class="issuerClass"><span>Jamaica</span></label></li>
							<li><label><input type="checkbox" name="country" value="Japan" class="issuerClass"><span>Japan</span></label></li>
							<li><label><input type="checkbox" name="country" value="Jordan" class="issuerClass"><span>Jordan</span></label></li>
							<li><label><input type="checkbox" name="country" value="Kazakhstan" class="issuerClass"><span>Kazakhstan</span></label></li>
							<li><label><input type="checkbox" name="country" value="Kenya" class="issuerClass"><span>Kenya</span></label></li>
							<li><label><input type="checkbox" name="country" value="Kuwait" class="issuerClass"><span>Kuwait</span></label></li>
							<li><label><input type="checkbox" name="country" value="Kyrgyzstan" class="issuerClass"><span>Kyrgyzstan</span></label></li>
							<li><label><input type="checkbox" name="country" value="Laos" class="issuerClass"><span>Laos</span></label></li>
							<li><label><input type="checkbox" name="country" value="Latvia" class="issuerClass"><span>Latvia</span></label></li>
							<li><label><input type="checkbox" name="country" value="Lebanon" class="issuerClass"><span>Lebanon</span></label></li>
							<li><label><input type="checkbox" name="country" value="Libya" class="issuerClass"><span>Libya</span></label></li>
							<li><label><input type="checkbox" name="country" value="Liechtenstein" class="issuerClass"><span>Liechtenstein</span></label></li>
							<li><label><input type="checkbox" name="country" value="Lithuania" class="issuerClass"><span>Lithuania</span></label></li>
							<li><label><input type="checkbox" name="country" value="Luxembourg" class="issuerClass"><span>Luxembourg</span></label></li>
							<li><label><input type="checkbox" name="country" value="Madagascar" class="issuerClass"><span>Madagascar</span></label></li>
							<li><label><input type="checkbox" name="country" value="Maldives" class="issuerClass"><span>Maldives</span></label></li>
							<li><label><input type="checkbox" name="country" value="Malaysia" class="issuerClass"><span>Malaysia</span></label></li>
							<li><label><input type="checkbox" name="country" value="Malta" class="issuerClass"><span>Malta</span></label></li>
							<li><label><input type="checkbox" name="country" value="Mauritius" class="issuerClass"><span>Mauritius</span></label></li>
							<li><label><input type="checkbox" name="country" value="Monaco" class="issuerClass"><span>Monaco</span></label></li>
							<li><label><input type="checkbox" name="country" value="Mongolia" class="issuerClass"><span>Mongolia</span></label></li>
							<li><label><input type="checkbox" name="country" value="Morocco" class="issuerClass"><span>Morocco</span></label></li>
							<li><label><input type="checkbox" name="country" value="Mozambique" class="issuerClass"><span>Mozambique</span></label></li>
							<li><label><input type="checkbox" name="country" value="Myanmar (Burma)" class="issuerClass"><span>Myanmar (Burma)</span></label></li>
							<li><label><input type="checkbox" name="country" value="Namibia" class="issuerClass"><span>Namibia</span></label></li>
							<li><label><input type="checkbox" name="country" value="Nepal" class="issuerClass"><span>Nepal</span></label></li>
							<li><label><input type="checkbox" name="country" value="Netherlands" class="issuerClass"><span>Netherlands</span></label></li>
							<li><label><input type="checkbox" name="country" value="New Zealand" class="issuerClass"><span>New Zealand</span></label></li>
							<li><label><input type="checkbox" name="country" value="Nigeria" class="issuerClass"><span>Nigeria</span></label></li>
							<li><label><input type="checkbox" name="country" value="Norway" class="issuerClass"><span>Norway</span></label></li>
							<li><label><input type="checkbox" name="country" value="Oman" class="issuerClass"><span>Oman</span></label></li>
							<li><label><input type="checkbox" name="country" value="Pakistan" class="issuerClass"><span>Pakistan</span></label></li>
							<li><label><input type="checkbox" name="country" value="Peru" class="issuerClass"><span>Peru</span></label></li>
							<li><label><input type="checkbox" name="country" value="Philippines" class="issuerClass"><span>Philippines</span></label></li>
							<li><label><input type="checkbox" name="country" value="Poland" class="issuerClass"><span>Poland</span></label></li>
							<li><label><input type="checkbox" name="country" value="Portugal" class="issuerClass"><span>Portugal</span></label></li>
							<li><label><input type="checkbox" name="country" value="Qatar" class="issuerClass"><span>Qatar</span></label></li>
							<li><label><input type="checkbox" name="country" value="Russia" class="issuerClass"><span>Russia</span></label></li>
							<li><label><input type="checkbox" name="country" value="Singapore" class="issuerClass"><span>Singapore</span></label></li>
							<li><label><input type="checkbox" name="country" value="Slovakia" class="issuerClass"><span>Slovakia</span></label></li>
							<li><label><input type="checkbox" name="country" value="Slovenia" class="issuerClass"><span>Slovenia</span></label></li>
							<li><label><input type="checkbox" name="country" value="South Africa" class="issuerClass"><span>South Africa</span></label></li>
							<li><label><input type="checkbox" name="country" value="South Korea" class="issuerClass"><span>South Korea</span></label></li>
							<li><label><input type="checkbox" name="country" value="Spain" class="issuerClass"><span>Spain</span></label></li>
							<li><label><input type="checkbox" name="country" value="Sri Lanka" class="issuerClass"><span>Sri Lanka</span></label></li>
							<li><label><input type="checkbox" name="country" value="Sweden" class="issuerClass"><span>Sweden</span></label></li>
							<li><label><input type="checkbox" name="country" value="Switzerland" class="issuerClass"><span>Switzerland</span></label></li>
							<li><label><input type="checkbox" name="country" value="Taiwan" class="issuerClass"><span>Taiwan</span></label></li>
							<li><label><input type="checkbox" name="country" value="Tajikistan" class="issuerClass"><span>Tajikistan</span></label></li>
							<li><label><input type="checkbox" name="country" value="Tanzania" class="issuerClass"><span>Tanzania</span></label></li>
							<li><label><input type="checkbox" name="country" value="Thailand" class="issuerClass"><span>Thailand</span></label></li>
							<li><label><input type="checkbox" name="country" value="Tunisia" class="issuerClass"><span>Tunisia</span></label></li>
							<li><label><input type="checkbox" name="country" value="Turkey" class="issuerClass"><span>Turkey</span></label></li>
							<li><label><input type="checkbox" name="country" value="Turkmenistan" class="issuerClass"><span>Turkmenistan</span></label></li>
							<li><label><input type="checkbox" name="country" value="United Arab Emirates" class="issuerClass"><span>United Arab Emirates</span></label></li>
							<li><label><input type="checkbox" name="country" value="Ukraine" class="issuerClass"><span>Ukraine</span></label></li>
							<li><label><input type="checkbox" name="country" value="United Kingdom" class="issuerClass"><span>United Kingdom</span></label></li>
							<li><label><input type="checkbox" name="country" value="UK" class="issuerClass"><span>UK</span></label></li>
							<li><label><input type="checkbox" name="country" value="United States of America" class="issuerClass"><span>United States of America</span></label></li>
							<li><label><input type="checkbox" name="country" value="USA" class="issuerClass"><span>USA</span></label></li>
							<li><label><input type="checkbox" name="country" value="Uzbekistan" class="issuerClass"><span>Uzbekistan</span></label></li>
							<li><label><input type="checkbox" name="country" value="Vietnam" class="issuerClass"><span>Vietnam</span></label></li>
							<li><label><input type="checkbox" name="country" value="Zimbabwe" class="issuerClass"><span>Zimbabwe</span></label></li>
						</ul>
					</div>
								<div class="modal-footer" style="display: flex;">
									
									<button type="submit" value="Block"  id="blockIssuer" onclick="ajaxFraudRequest(issuerCountry)" class="btn btn-success btn-sm">Block</button>
									<button type="button" class="btn btn-success btn-sm " style="margin-left: 5px;" data-dismiss="modal">Close</button>
								</div>
							</div>
				</div>
			</div>
	
	
	<!--------------------------------------------------------------------------------------------------------------------------->
	
		<div id="myModal3" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
				<div class="modal-dialog" style="width: 600px;">

					<!-- Modal content-->
					<div class="modal-content">
						<div class="modal-header">
							<button type="button" class="close" id="fraudRuleModalClose" data-dismiss="modal">&times;</button>
						 <!-- <h4 class="modal-title colored-header">Block Card Issuer Country</h4>  -->
						  </div>
						<div class="modal-body"
							>
							<form name="myForm">
							<table class="detailbox table98" cellpadding="20">
								<tr>
									<th colspan="2" width="16%" height="30" align="left"
										style="background-color: #496cb6; color: #ffffff; border-top-right-radius: 13px !important;">Block Card Bin Range</th>
								</tr>
								<tr>
									<td colspan="2" height="30" align="left"><p>
											Enter the first 6 digits of the card number you wish to block;<br>
											<br> Once added, all transactions within that card range
											will be blocked.
										</p></td>
								</tr>
								<tr>
								<td width="8%"><label for="alwaysOnFlag3">
							   <input type="checkbox" name="alwaysOnFlag3" id="alwaysOnFlag3" value="false" onclick="ShowHideDiv3(this)" />
								<input type="hidden" name="alwaysOnFlag33" id="alwaysOnFlag33" /></label>
								</td>
								
									<td width="30%"><label for="always" style="font-size:16px;">Always</label></td>
								</tr>
								<tr >
									<td width="7%"><label>Card range <span style="color:red;">*</span></label></td>
									<td width="30%">
									<!-- <span class="" id="validate_crd" style="font-size: 10px;"></span>
									<input id="negativeBin" type="number" minlength="6" maxlength="6"  onkeypress="return isNumberKey(event)"  placeholder="6-digit bin range" name="negativeBin" 
									class="form-control" data-role="tagsinput" /> -->
			
									<span class="" id="validate_crd" style="font-size: 10px;"></span>
									<input id="negativeBin" type="text" minlength="6" maxlength="6" placeholder="6-digit bin range" name="negativeBin"
									class="form-control" data-role="tagsinput" />
									<span class="" id="validate_crdL" style="font-size: 10px;"></span>
									</td>
								</tr>
								<!-- New Html Code Time Based Fraud Rule-->
								
					<table class="detailbox table98" cellpadding="20" id="dvTimebased3"  >
						<tr>
						<td width="7%">
						<label for="CBdateFrom">Start Date<span style="color:red;">*</span></label></td>
						<td width="30%">
						<div class="startdatepicker date t-ip">
						<input class="form-control wid dateActiveFrom" type="text" id="dateActiveFrom4" name="dateActiveFrom" placeholder="DD/MM/YYYY" autocomplete="off" readonly="readonly"/>
						<span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span></span>
						</div>
						</td>
						</tr>

						<tr>
						<td width="7%">
						<label for="CBdateTo">End Date<span style="color:red;">*</span></label></td>
						<td width="30%">
						<div class="expiredatepicker date t-ip">
						<input class="form-control wid dateActiveTo" type="text" id="dateActiveTo4" name="dateActiveTo" placeholder="DD/MM/YYYY" autocomplete="off" readonly="readonly"/>
						<span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span></span>
						</div>
						</td>
						</tr>

						<tr>
						<td width="7%">
						<label for="CBstartTime" class="">Start Time<span style="color:red;">*</span></label>
						</td>
						<td width="30%">
						
						<div class=" t-ip startTime">
						<input type="text" class="form-control wid startTime" id="startTime4" name="startTime" placeholder="HH:MM:SS" autocomplete="off"  />
						<span class="input-group-addon" style="padding: 5px 12px;">
						<span class="glyphicon glyphicon-time"></span>
						</div>
						</span>
						
						</td>
						</tr>
						<tr>
						<td width="7%"> <label for="CBendTime" class="">End Time<span style="color:red;">*</span></label></td>
						<td width="30%"> <div class=" t-ip endTime">
						<input type="text" class="form-control wid endTime" id="endTime4" name ="endTime" placeholder="HH:MM:SS" autocomplete="off"   />
						<span class="input-group-addon" style="padding: 5px 12px;">
						<span class="glyphicon glyphicon-time"></span>
						</span>
						</div></td>
						</tr>
						<tr>
						<td width="7%"><label for="weeks">Weeks <span style="color:red;">*</span></label></td>
						<td width="30%">
						<ul id="repeatDays" class="checklist-week" data-name="repeatDays">
						<li><label><input type="checkbox" name="repeatDays"  value="SUN" id="repeatDays14" class="binRangeClass"><span>SUN</span></label></li>
						<li><label><input type="checkbox" name="repeatDays" value="MON" id="repeatDays15" class="binRangeClass"><span>MON</span></label></li>
						<li><label><input type="checkbox" name="repeatDays" value="TUE" id="repeatDays16" class="binRangeClass"><span>TUE</span></label></li>
						<li><label><input type="checkbox" name="repeatDays" value="WED" id="repeatDays17" class="binRangeClass"><span>WED</span></label></li>
						<li><label><input type="checkbox" name="repeatDays" value="THU" id="repeatDays18" class="binRangeClass"><span>THU</span></label></li>
						<li><label><input type="checkbox" name="repeatDays" value="FRI" id="repeatDays19" class="binRangeClass"><span>FRI</span></label></li>
						<li><label><input type="checkbox" name="repeatDays" value="SAT" id="repeatDays20" class="binRangeClass"><span>SAT</span></label></li>
						</ul>
						</td>
						</tr>
					</table>

								<tr>
								<td colspan="2">
									<div style="display: flex;">
									<button type="submit" value="Block"
											onclick="ajaxFraudRequest(negativeBinS)" id="blockCardRange" style="margin-left:38%;width:21%;height:100%;margin-top:1%;" class="btn btn-success btn-sm btn-block">Block</button>
											<button type="button" class="btn btn-success btn-sm btn-block" style="width:21%;height:100%;margin-top:1%;" data-dismiss="modal">Close</button>	
										</div>
										</td>
								</tr>
								<tr>
									<td>Note: If Always True Flag is checked then Date, Time, Week does not need to be filled but if it is not then all the required fields will have to be filled. </td>
								</tr>
							</table>
							</form>
						</div>
					</div>
				</div>
		</div>
	

	<!-------------------------------------------------------------------------------------------------------------------------->
	
		<div id="myModal5" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
			<div class="modal-dialog" style="width: 600px;">
					<!-- Modal content-->
				<div class="modal-content" >
					<div class="modal-header">
						<button type="button" class="close" id="fraudRuleModalClose" data-dismiss="modal">&times;</button>
					 <!-- <h4 class="modal-title colored-header">Block Card Issuer Country</h4>  -->
					  </div>
					<div id="5" class="modal-body" >
					
					<table class="detailbox table98" cellpadding="20">
							<tr>
								<th colspan="2" width="16%" height="30" align="left"
								style="background-color: #496cb6; color: #ffffff; border-top-right-radius: 13px !important;font-size: 16px;">Block Customer Email Address</th>
							</tr>
										
						<tr>
							<td width="8%"><label for="alwaysOnFlag4">
								<input type="checkbox" name="alwaysOnFlag4" id="alwaysOnFlag4" value="false" onclick="ShowHideDiv4(this)" />
								<input type="hidden" name="alwaysOnFlag44" id="alwaysOnFlag44" /></label>
							</td>
							<td width="31.8%"><label for="always" style="font-size:16px;">Always</label></td>
						</tr>
						<tr>
							<td width="7%"><label for="">Email Address <span style="color:red;">*</span></label></td>							
							<td width="30%">
							<span class="" id="validate_email" style="font-size: 11px;"></span>
							<input id="email" type="email" name="email" placeholder="user@domain.xyz" class="form-control" data-role="tagsinput" />
							</td>															
						</tr>
						
						<!-- New Html Code Time Based Fraud Rule-->
								
					<table class="detailbox table98" cellpadding="20" id="dvTimebased4"  >
						<tr>
							<td width="7.35%">
								<label for="emaildateFrom">Start Date<span style="color:red;">*</span></label></td>
							<td width="30%">
								<div class="startdatepicker date t-ip">
									<input class="form-control wid dateActiveFrom" type="text" id="dateActiveFrom3" name="dateActiveFrom" placeholder="DD/MM/YYYY" autocomplete="off" readonly="readonly"/>
									<span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span></span>
								</div>
							</td>
						</tr>

						<tr>
							<td width="7.35%">
								<label for="emaildateTo">End Date<span style="color:red;">*</span></label>
							</td>
							<td width="30%">
								<div class="expiredatepicker date t-ip">
									<input class="form-control wid dateActiveTo" type="text" id="dateActiveTo3" name="dateActiveTo" placeholder="DD/MM/YYYY" autocomplete="off" readonly="readonly"/>
									<span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span></span>
								</div>
							</td>
						</tr>

						<tr>
							<td width="7.35%">
								<label for="EmailstartTime" class="">Start Time<span style="color:red;">*</span></label>
							</td>
							<td width="30%">
							
								<div class=" t-ip startTime">
									<input type="text" class="form-control wid startTime" id="startTime3" name="startTime" placeholder="HH:MM:SS" autocomplete="off" />
									<span class="input-group-addon" style="padding: 5px 12px;">
									<span class="glyphicon glyphicon-time"></span>
									</span>
								</div>
								
							</td>
						</tr>
						
						<tr>
							<td width="7.35%"> 
								<label for="EmailendTime" class="">End Time<span style="color:red;">*</span></label>
							</td>
							<td width="30%"> 
							    <div class="t-ip endTime">
									<input type="text" class="form-control wid endTime" id="endTime3" name ="endTime" placeholder="HH:MM:SS" autocomplete="off" />
									<span class="input-group-addon" style="padding: 5px 12px;">
									<span class="glyphicon glyphicon-time"></span>
									</span>
								</div>
							</td>
						</tr>
						
						<tr>
							<td width="7.35%"><label for="weeks">Weeks <span style="color:red;">*</span></label></td>
							<td width="30%">
							<ul id="repeatDays" class="checklist-week" data-name="repeatDays">
								<li>
									<label>
										<input type="checkbox" name="repeatDays" id="repeatDays21"   value="SUN" class="emailAddClass">
										<span>SUN</span>
									</label>
								</li>
								<li>
									<label>
										<input type="checkbox" name="repeatDays" id="repeatDays22" value="MON" class="emailAddClass">
										<span>MON</span>
									</label>
								</li>
								<li>
									<label>
										<input type="checkbox" name="repeatDays" id="repeatDays23" value="TUE" class="emailAddClass">
										<span>TUE</span>
									</label>
								</li>
								<li>
									<label>
										<input type="checkbox" name="repeatDays" id="repeatDays24" value="WED" class="emailAddClass">
										<span>WED</span>
									</label>
								</li>
								<li>
									<label>
										<input type="checkbox" name="repeatDays" id="repeatDays25" value="THU" class="emailAddClass">
										<span>THU</span>
									</label>
								</li>
								<li>
									<label>
										<input type="checkbox" name="repeatDays" id="repeatDays26" value="FRI" class="emailAddClass">
										<span>FRI</span>
									</label>
								</li>
								<li>
									<label>
										<input type="checkbox" name="repeatDays" id="repeatDays27" value="SAT" class="emailAddClass">
										<span>SAT</span>
									</label>
								</li>
							</ul>
							</td>
						</tr>
					</table>

							<tr>
								<td colspan="2">
									<div style="display: flex;">
									<button type="submit" value="Block" id="blockEmail" onclick="ajaxFraudRequest(emailS)" class="btn btn-success btn-sm btn-block" 
									style="margin-left:38%;width:21%;height:100%;margin-top:1%;">Block</button>
									<button type="button" class="btn btn-success btn-sm btn-block" style="width:21%;height:100%;margin-top:1%;" data-dismiss="modal">Close</button>
								</div>
							</td>
							</tr>
							<tr>
								<td>Note: If Always True Flag is checked then Date, Time, Week does not need to be filled but if it is not then all the required fields will have to be filled. </td>
							</tr>
						</table>
					</div>
				</div>
			</div>
		</div>
	
     <!--------------------------------------------------------------------------------------------------------------------------->
	 
			<div id="myModal8" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">	
				<div class="modal-dialog" style="width: 400px;">

					<!-- Modal content-->
					<div class="modal-content"
						>
						<div class="modal-header">
							<button type="button" class="close" id="fraudRuleModalClose" data-dismiss="modal">&times;</button>
						 <!-- <h4 class="modal-title colored-header">Block Card Issuer Country</h4>  -->
						  </div>
						<div id="7" class="modal-body"
							>

							<table class="detailbox table98" cellpadding="20">
								<tr>
									<th colspan="2" width="16%" height="30" align="left"
										style="background-color: #496cb6; color: #ffffff; border-top-right-radius: 13px !important;">Limit Transaction Amount</th>
								</tr>
								<tr>
									<td width="15"><label for ="">Currency <span style="color:red;">*</span></label></td>
									<td width="30%"><s:select name="currency" class="form-control" id="currency" list="currencyMap"/></td>
								</tr>
								
								<tr>
									<td width="15%"><label for="">Minimum Amount Limit<span style="color:red;">*</span></label></td>
									<td width="30%"><input type="number" id="minTransactionAmount" placeholder="10" onpaste="return false" name="minTransactionAmount" class="form-control"  min="" max="9999999999.99" step="0.01" onkeypress="return isNumberKeyAmount(event)" onkeyup="checkTransactionVal()"/></td>
									<label id="error" style="color:red"></label>
								</tr>
								<tr>
									<td width="15%"><label for="">Maximum Amount Limit <span style="color:red;">*</span></label></td>
									<td width="30%">
									<span id="amountError" style="color:red; display:none;">Please enter valid Max Amount</span>
									<input type="number" id="maxTransactionAmount" placeholder="110" name="maxTransactionAmount" onpaste="return false" class="form-control"  min="" max="9999999999.99" step="0.01" onkeypress="return isNumberKeyAmount(event)" onkeyup="checkTransactionVal()" /></td>
									
									<label id="error" style="color:red"></label>
								</tr>
								<tr>
									<td colspan="2">
										<div style="display: flex;">
									<button type="submit" value="Block" id="blockTxnAmt"  onclick="ajaxFraudRequest(transactionAmount)" class="btn btn-success btn-sm btn-block" style="margin-left:38%;width:21%;height:100%;margin-top:1%;">Block</button>
									<button type="button" class="btn btn-success btn-sm btn-block" style="width:21%;height:100%;margin-top:1%;" data-dismiss="modal">Close</button>	
								</div>
							</td>
								</tr>
							</table>
						</div>
					</div>
				</div>
			</div>
	
	<!------------------------------------------------------------------------------------------------------------------------->
	<!--Modal for block Phone Number-->
	<div id="myModal9" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
		<div class="modal-dialog" style="width: 600px;">
				<!-- Modal content-->
			<div class="modal-content" >
				<div class="modal-header">
					<button type="button" class="close" id="fraudRuleModalClose" data-dismiss="modal">&times;</button>
				 <!-- <h4 class="modal-title colored-header">Block Card Issuer Country</h4>  -->
				  </div>
				<div id="5" class="modal-body" >
				
				<table class="detailbox table98" cellpadding="20">
						<tr>
							<th colspan="2" width="16%" height="30" align="left"
							style="background-color: #496cb6; color: #ffffff; border-top-right-radius: 13px !important;font-size: 16px;">Block Customer Phone Number</th>
						</tr>
									
					<tr>
						<td width="8%"><label for="alwaysOnFlag9">
							<input type="checkbox" name="alwaysOnFlag9" id="alwaysOnFlag9" value="false" onclick="ShowHideDiv9(this)" />
							<input type="hidden" name="alwaysOnFlag99" id="alwaysOnFlag99" /></label>
						</td>
						<td width="31.8%"><label for="always" style="font-size:16px;">Always</label></td>
					</tr>
					<tr>
						<td width="7%"><label for="">Phone Number <span style="color:red;">*</span></label></td>							
						<td width="30%">
						<span class="" id="validate_phone" style="font-size: 11px;"></span>
						<input id="phone" type="text" name="phone" placeholder="Enter Phone Number" maxlength="8" minlength="13" class="form-control" data-role="tagsinput" value="" />
						</td>															
					</tr>
					
					<!-- New Html Code Time Based Fraud Rule-->
							
				<table class="detailbox table98" cellpadding="20" id="dvTimebased9"  >
					<tr>
						<td width="7.35%">
							<label for="phonedateFrom">Start Date<span style="color:red;">*</span></label></td>
						<td width="30%">
							<div class="startdatepicker date t-ip">
								<input class="form-control wid dateActiveFrom" type="text" id="dateActiveFrom9" name="dateActiveFrom" placeholder="DD/MM/YYYY" autocomplete="off" readonly="readonly"/>
								<span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span></span>
							</div>
						</td>
					</tr>

					<tr>
						<td width="7.35%">
							<label for="phonedateTo">End Date<span style="color:red;">*</span></label>
						</td>
						<td width="30%">
							<div class="expiredatepicker date t-ip">
								<input class="form-control wid dateActiveTo" type="text" id="dateActiveTo9" name="dateActiveTo" placeholder="DD/MM/YYYY" autocomplete="off" readonly="readonly"/>
								<span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span></span>
							</div>
						</td>
					</tr>

					<tr>
						<td width="7.35%">
							<label for="phonestartTime" class="">Start Time<span style="color:red;">*</span></label>
						</td>
						<td width="30%">
							<div class=" t-ip startTime">
								<input type="text" class="form-control wid startTime" id="startTime9" name="startTime" placeholder="HH:MM:SS" autocomplete="off" />
								<span class="input-group-addon" style="padding: 5px 12px;">
								<span class="glyphicon glyphicon-time"></span>
								</span>
							</div>
						</td>
					</tr>
					
					<tr>
						<td width="7.35%"> 
							<label for="phoneendTime" class="">End Time<span style="color:red;">*</span></label>
						</td>
						<td width="30%"> 
							<div class="  t-ip endTime">
								<input type="text" class="form-control wid endTime" id="endTime9" name ="endTime" placeholder="HH:MM:SS" autocomplete="off" />
								<span class="input-group-addon" style="padding: 5px 12px;">
								<span class="glyphicon glyphicon-time"></span>
								</span>
							</div>
						</td>
					</tr>
					
					<tr>
						<td width="7.35%"><label for="weeks">Weeks <span style="color:red;">*</span></label></td>
						<td width="30%">
						<ul id="repeatDays" class="checklist-week" data-name="repeatDays">
							<li>
								<label>
									<input type="checkbox" name="repeatDays" id="repeatDays28"  value="SUN" class="phoneAddClass">
									<span>SUN</span>
								</label>
							</li>
							<li>
								<label>
									<input type="checkbox" name="repeatDays" id="repeatDays29" value="MON" class="phoneAddClass">
									<span>MON</span>
								</label>
							</li>
							<li>
								<label>
									<input type="checkbox" name="repeatDays" id="repeatDays30" value="TUE" class="phoneAddClass">
									<span>TUE</span>
								</label>
							</li>
							<li>
								<label>
									<input type="checkbox" name="repeatDays" id="repeatDays31" value="WED" class="phoneAddClass">
									<span>WED</span>
								</label>
							</li>
							<li>
								<label>
									<input type="checkbox" name="repeatDays" id="repeatDays32" value="THU" class="phoneAddClass">
									<span>THU</span>
								</label>
							</li>
							<li>
								<label>
									<input type="checkbox" name="repeatDays" id="repeatDays33" value="FRI" class="phoneAddClass">
									<span>FRI</span>
								</label>
							</li>
							<li>
								<label>
									<input type="checkbox" name="repeatDays"  id="repeatDays34" value="SAT" class="phoneAddClass">
									<span>SAT</span>
								</label>
							</li>
						</ul>
						</td>
					</tr>
				</table>

						<tr>
							<td colspan="2">
								<div style="display: flex;">
								<button type="submit" value="Block" id="blockPhone" onclick="ajaxFraudRequest(phoneS)" class="btn btn-success btn-sm btn-block" 
								style="margin-left:38%;width:21%;height:100%;margin-top:1%;">Block</button>
								<button type="button" class="btn btn-success btn-sm btn-block" style="width:21%;height:100%;margin-top:1%;" data-dismiss="modal">Close</button>
							</div>
						</td>
						</tr>
						<tr>
							<td>Note: If Always True Flag is checked then Date, Time, Week does not need to be filled but if it is not then all the required fields will have to be filled. </td>
						</tr>
					</table>
				</div>
			</div>
		</div>
	</div>
		
			
	<!-------------------------------------------------------------------------------------------------------------------------->
	<div id="myModal0" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">
		<div class="modal-dialog" style="width: 600px;">
				<!-- Modal content-->
				<div class="modal-content"
					>
					<div class="modal-header">
						<button type="button" id="fraudRuleModalClose" class="close" data-dismiss="modal">&times;</button>
					 <!-- <h4 class="modal-title colored-header">Block Card Issuer Country</h4>  -->
					  </div>
					<div id="6" class="modal-body"
					>

						<table class="detailbox table98" cellpadding="20">
							<tr>
								<th colspan="3" width="16%" height="30" align="left"
									style="background-color: #496cb6; color: #ffffff; border-top-right-radius: 13px !important;">
									 Block Card No. </th>
							</tr>
							
							<tr>
								<td width="7%" ><label for="">Sample card no.</label></td>
								<td width="30%"><span style="font: normal 15px Arial">411111</span>
								<span style="font: normal 10px Arial">******</span>
								<span style="font: normal 15px Arial">1111</span>
								</td>
							</tr>
							<tr>
								<td width="7%"><label for="alwaysOnFlag6">
								   <input type="checkbox" name="alwaysOnFlag6" id="alwaysOnFlag6" value="false" onclick="ShowHideDiv6(this)" />
									<input type="hidden" name="alwaysOnFlag66" id="alwaysOnFlag66" /></label>
								</td>
								
									<td width="30%"><label for="always" style="font-size:16px;">Always</label></td>
							</tr>	
							<tr>
								<td width="7%"><label for="">Card Number <span style="color:red;">*</span></label></td>
								<td  style="display: flex;
								flex-flow: nowrap;
								width: 93% !important;">
									<input id="cardIntialDigits" placeholder="Initial 6-digits of card" type="text" maxlength="6" minlength="6" name="negativeCard" class="form-control sample_cn" onkeypress="return isNumberKey(event)" onblur="makeCardMaskini()" />
									<input id="cardLastDigits" placeholder="Last 4-digits of card" type="text" maxlength="4" minlength="4" name="negativeCard" class="form-control" style="width:48%!important; position:relative; left:10px;" onkeypress="return isNumberKey(event)" onblur="makeCardMasklst()" />
									<input type="hidden" id="negativeCard" name="negativeCard" value="">
									<span class="" id="validate_crdIn" style="font-size: 10px;"></span>
									<span class="" id="validate_crdL" style="font-size: 10px;"></span>
								</td>
							</tr>
							<!-- New Html Code Time Based Fraud Rule-->
							
				<table class="detailbox table98" cellpadding="20" id="dvTimebased6"  >
					<tr>
						<td width="7%">
							<label for="carddateFrom">Start Date <span style="color:red;">*</span></label>
						</td>
						<td width="30%">
							<div class="date t-ip">
							<input class="form-control wid dateActiveFrom" type="text" id="dateActiveFrom5" name="dateActiveFrom" placeholder="DD/MM/YYYY" autocomplete="off" readonly="readonly"/>
							<span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span></span>
							</div>
						</td>
					</tr>

					<tr>
						<td width="7%">
							<label for="carddateTo">End Date <span style="color:red;">*</span></label>
						</td>
						<td width="30%">
							<div class="date t-ip">
							<input class="form-control wid dateActiveTo" type="text" id="dateActiveTo5" name="dateActiveTo" placeholder="DD/MM/YYYY" autocomplete="off" readonly="readonly"/>
							<span class="input-group-addon"><span class="glyphicon glyphicon-calendar"></span></span>
							</div>
						</td>
					</tr>

					<tr>
						<td width="7%">
							<label for="CardstartTime" class="">Start Time <span style="color:red;">*</span></label>
						</td>
						<td width="30%">
							<div class=" t-ip">
							<input type="text" class="form-control wid startTime" id="startTime5" name="startTime" placeholder="HH:MM:SS" autocomplete="off" />
							<span class="input-group-addon" style="padding: 5px 12px;">
							<span class="glyphicon glyphicon-time"></span>
							</span>
							</div>
						</td>
					</tr>
					
					<tr>
						<td width="7%"> 
							<label for="CardendTime" class="">End Time <span style="color:red;">*</span></label>
						</td>
						<td width="30%"> 
							<div class=" t-ip">
							<input type="text" class="form-control wid endTime" id="endTime5" name ="endTime" placeholder="HH:MM:SS" autocomplete="off" />
							<span class="input-group-addon" style="padding: 5px 12px;">
							<span class="glyphicon glyphicon-time"></span>
							</span>
							</div>
						</td>
					</tr>
					
					<tr>
						<td width="7%"><label for="weeks">Weeks <span style="color:red;">*</span></label></td>
						<td width="30%">
							<ul id="repeatDays" class="checklist-week" data-name="repeatDays">
								<li><label><input type="checkbox" name="repeatDays" id="repeatDays35" value="SUN" class="blockCardClass"><span>SUN</span></label>
								</li>
								<li><label><input type="checkbox" name="repeatDays" id="repeatDays36" value="MON" class="blockCardClass"><span>MON</span></label>
								</li>
								<li><label><input type="checkbox" name="repeatDays" id="repeatDays37" value="TUE" class="blockCardClass"><span>TUE</span></label>
								</li>
								<li><label><input type="checkbox" name="repeatDays" id="repeatDays38" value="WED" class="blockCardClass"><span>WED</span></label>
								</li>
								<li><label><input type="checkbox" name="repeatDays" id="repeatDays39" value="THU" class="blockCardClass"><span>THU</span></label>
								</li>
								<li><label><input type="checkbox" name="repeatDays" id="repeatDays40" value="FRI" class="blockCardClass"><span>FRI</span></label>
								</li>
								<li><label><input type="checkbox" name="repeatDays" id="repeatDays41" value="SAT" class="blockCardClass"><span>SAT</span></label>
								</li>
							</ul>
						</td>
					</tr>
				</table>
							<tr>
					
								<td colspan="2">
									<div style="display: flex;">
									<button type="submit" value="Block" id="blockCardNo" onclick="ajaxFraudRequest(negativeCardS)" class="btn btn-success btn-sm btn-block" id="negativeCradBtn" 
									style="margin-left:38%;width:21%;height:100%;margin-top:1%;">Block</button>
									<button type="button" class="btn btn-success btn-sm btn-block" style="width:21%;height:100%;margin-top:1%;" data-dismiss="modal">Close</button>	
								</div>
								</td>
							</tr>
							<tr>
								<td>Note: If Always True Flag is checked then Date, Time, Week does not need to be filled but if it is not then all the required fields will have to be filled. </td>
							</tr>

						</table>
					</div>
				</div>
			</div>
		</div>
	<!---->
	<div id="myModal11" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel">	
		<div class="modal-dialog" style="width: 400px;">

			<!-- Modal content-->
			<div class="modal-content"
				>
				<div class="modal-header">
					<button type="button" class="close" id="fraudRuleModalClose" data-dismiss="modal">&times;</button>
				 <!-- <h4 class="modal-title colored-header">Block Card Issuer Country</h4>  -->
				  </div>
				<div id="7" class="modal-body"
					>

					<table class="detailbox table98" cellpadding="20">
						<tr>
							<th colspan="2" width="16%" height="30" align="left"
								style="background-color: #496cb6; color: #ffffff; border-top-right-radius: 13px !important;">Limit Transaction Amount</th>
						</tr>
						<tr>
							<td width="15"><label for ="">Currency <span style="color:red;">*</span></label></td>
							<td width="30%"><s:select name="currency" class="form-control" id="currency" list="currencyMap"/></td>
						</tr>
						<tr>
							<td width="15"><label for ="">User Identifier <span style="color:red;">*</span></label></td>
							<td><div class="col-sm-12 col-lg-10">
								
								<div class="OtherList" style="margin-left:10px; margin-top:5px;">
									<div>
										<div id="wwgrp_userType" class="wwgrp">
                                   <div id="wwctrl_userType" class="wwctrl">
                            <form id="userIdentifier">
       							<input type="radio" name="userIdentifier"  value="Email" checked="checked"><label for="emailoption">Email</label><br>
								<input type="radio" name="userIdentifier"  value="Customer Id"><label for="custidoption">Customer ID</label><br>
								<input type="radio" name="userIdentifier"  value="Phone"><label for="phoneoption">Phone</label>
							</form>
						</div> </div>

									</div>
								</div>
							</div></td>
						</tr>
						
						<tr>
							<td width="15%"><label for="">Transaction Amount<span style="color:red;">*</span></label></td>
							<td width="30%"><input type="number" id="txnAmountVelocity" placeholder="10" name="maxTransactionAmount" class="form-control" min="" max="9999999999.99" step="0.01" onkeypress="return isNumberKeyAmount(event)" onkeyup="checkTransactionVal()"/></td>
							<label id="error2" style="color:red"></label>
						</tr>
						
						<tr>
							<td colspan="2">
								<div style="display: flex;">
							<button type="submit" value="Block" id="blockTxnAmtVel"  onclick="ajaxFraudRequest(transactionAmountVelocity)" class="btn btn-success btn-sm btn-block" style="margin-left:38%;width:21%;height:100%;margin-top:1%;">Block</button>
							<button type="button" class="btn btn-success btn-sm btn-block" style="width:21%;height:100%;margin-top:1%;" data-dismiss="modal">Close</button>	
						</div>
					</td>
						</tr>
					</table>
				</div>
			</div>
		</div>
	</div>
	<!--End Modal here-->
		
		<div class="col-md-12">
			<div class="card ">
			  <div class="card-header card-header-rose card-header-text">
				<div class="card-text">
				  <h4 class="card-title">Merchant-- Fraud Prevention System</h4>
				</div>
			  </div>
			  <div class="card-body ">
				<div class="container">
				
		<div   class="accordion md-accordion" id="accordionEx1" role="tablist" aria-multiselectable="true">
			<s:hidden name="preferenceSet" value="%{preferenceSet}" id="preferenceSet">
			</s:hidden>
			<s:hidden name="activateMerchantMgmt" value="%{activateMerchantMgmt}" id="activateMerchantMgmt">
			</s:hidden>
			<s:hidden name="payId" value="%{payId}" id="payId">
			</s:hidden>
			<div class="alert alert-danger" role="alert" id="noRuleSelection" style="display: none;">
				You are not assigned any fraud prevention rules! Please contact support team  for a demo and getting permissions for the same.
				You can add custom rules  as per your business requirements to minimise frauds and chargebacks on your website.
			  </div>
			<div class="card card_graph"   id="cardcb1" style="display: none;"><!--onclick="monthlycardupdateTabState(this)"-->
				<div class="card-header " role="tab" id="headingTwo1">
					<a class="collapsed" >
						<div class="card-header card-header-icon card-header-danger" id="cardHeaderPosition">
						<div class="card-icon" id="cardIcon">
							<i class="material-icons" id="materialIcons">block</i>
						  </div> 
						 <h4 class="card-title">Block IP Addresses<i style="float: right; color: #496cb6;font-size: 30px;"  data-toggle="collapse" data-parent="#accordionEx1" href="#collapseTwo1"
							aria-expanded="false" aria-controls="collapseTwo1" class="fa fa-angle-down rotate-icon"></i>
						   
						  </h4>
						  </div>
					  <!-- <h5 class="mb-0">
						<strong>Monthly Transaction</strong> <i style="float: right; color: #496cb6;font-size: 30px;" data-toggle="collapse" data-parent="#accordionEx1" href="#collapseTwo1"
						aria-expanded="false" aria-controls="collapseTwo1" class="fa fa-angle-down rotate-icon"></i>
					  </h5> -->
					</a>
				  </div>
				  <div id="collapseTwo1" class="collapse active " role="tabpanel" >
<div class="card-body">
		<div class="row">
		<div class="col-md-12 col-xs-12">
			
				<div class="cards">
				  <div class="card-header card-header-icon card-header-info">
					<!-- <div class="card-icon" id="cardIcon">
					  <i class="material-icons" id="materialIcons">multiline_chart</i>
					</div> -->
					<!-- <h4 class="card-title">Monthly Transaction
					 
					</h4><br>
					<br> -->
					<div class="row" style="align-items: flex-end;">
						<div class="bkn">
						
							<div class="adduT">
								<table>
								  <tr>
									<!-- <td id="ipAddListBodyMsg" style="display:block">No blocked IP addresses</td>
									<td style="width:6%"><div class="adduT" style="text-align: right;padding: 14px 0 0 0;"> -->
										
										<div class="col-md-2">
									<input type="submit" name="remittSubmit" value="Add Rule" onclick="tagsAddRule()" id="popupButton" class="btn btn-success btn-md" 
									data-toggle="modal"  data-target="#myModal"/>
								</div>
								<div class="col-md-3" style="margin-top: 10px;">
									<input   type='search' class="input-control "  id="search_ipAddListBody" onkeypress="if(event.keyCode === 32)return false;"  placeholder="Search IP Address">
								</div>
								<div class="col-md-2">
									<input class='btn btn-success btn-md' id="searchResult"   type='submit' value='Search' onclick="searchData('ipAddListBody','BLOCK_IP_ADDRESS')">
								
								</div>
								<div class="col-md-2">
									<input class='btn btn-success btn-md bulkDeleteBtn'  type='submit' value='Bulk Delete' onclick="bulkDeleteFraudRule('ipAddListBody','BLOCK_IP_ADDRESS')">
								</div>
									</div></td>
								  </tr>
								</table>
							
							</div>
							
							<div>
								<!-- <p class="accordion_head"><b>Blocked IP Addresses List</b><span class="plusminus">+</span></p>  -->
								<div class="scrollD" style="overflow-x: scroll!important;">
									
									<table id="ipAddListBody" class="display" align="center" cellspacing="0" width="100%" style="text-align:center;">
										
										<thead>
											<tr class="boxheadingsmall" style="font-size: 11px;">
												<!-- <th style='text-align: center'>Merchant</th> -->
												<th style='text-align: center'>IP Address</th>
												<!-- <th style='text-align: center'>Name</th> -->
												<th style='text-align: center'>Start Date</th>
												<th style='text-align: center'>End Date</th>
												<th style='text-align: center'>Start Time</th>
												<th style='text-align: center'>End Time</th>
												<th style='text-align: center'>Weeks</th>
												<th style='text-align: center'>Action</th>
												<th style='text-align: center'>	<input class="form-check-input" type="checkbox" id="select_all" style="margin-right:10px;" value="" onclick="selectall()">
													<span class="form-check-sign">
													  <span class="check"></span>Action/Select All 
												
													</span></th>
												
											</tr>
										</thead>
									</table>
								</div>

								<div class="card ">
									<div class="card-header card-header-rose card-header-text">
									  <div class="card-text">
										<h4 class="card-title">Add Bulk Rules for IP Address</h4>
									  </div>
									</div>
									<div class="card-body ">
								<div class="container">
									
									<div class="row it">
									<div class="col-sm-offset-1 col-sm-10" id="one">
									<p>
									Please upload documents only in 'CSV' format.
									</p><br>
									<div class="row">
									  <div class="col-sm-offset-4 col-sm-4 form-group">
										
									  </div><!--form-group-->
									</div><!--row-->
									<div id="uploader">
									<div class="row uploadDoc">
										<div class="col-sm-4 ">
											<table
												id="example" class="csv" style="display: none;">
												<thead>
													<tr>
														<!-- <th>Merchant</th> -->
														<td>IP Address</td>
														<!-- <th>Name</th> -->
														<td>Start Date</td>
														<td>End Date</td>
														<td>Start Time</td>
														<td>End Time</td>
														<td>Week Days</td>
														<td>Always On</td>
													</tr>
													
												</thead>
												<tbody>
													<tr>
														<!-- <th>Merchant</th> -->
													<td>192.44.44.88</td>
													<td></td>
													<td></td>
													<td></td>
													<td></td>
													<td></td>
													<td>TRUE</td>
													</tr>

													<tr>
														<!-- <th>Merchant</th> -->
													<td>192.44.44.88</td>
													<td>13/09/2020</td>
													<td>25/09/2020</td>
													<td>11:51:41</td>
													<td>11:51:42</td>
													<td>MON TUE WED </td>
													<td>FALSE</td>
													</tr>
												</tbody>
												
											</table></div>
									  <div class="col-sm-6">
										<div class="docErr">Please upload valid file</div><!--error-->
										<div class="fileUpload btn btn-orange">
										  <img src="https://image.flaticon.com/icons/svg/136/136549.svg" class="icon">
										  <span class="upl" id="upload">Upload document</span>
										  
											<form enctype="multipart/form-data" method="post" id="ipBulkUpload" >
												<div style="display:flex">											<!-- <input type="hidden"   value="IPRULE" name="file1" /> -->
										
											<!-- <input type="hidden"   value="IPRULE" name="file1" /> -->
										  <input type="file" name="fileName" class="upload up" id="up" onchange="readURL(this);" />
										  <input type="submit" class="btn btn-info btn-xs"  id="btnBulkUpload" disabled onclick="bulkUpload('BLOCK_IP_ADDRESS','ipBulkUpload')"> 
												</div>
										</form>
										</div><!-- btn-orange -->
									  </div><!-- col-3 -->
									 
									  Note: If Always True Flag is checked then Date, Time, Week does not need to be filled but if it is not then all the required fields will have to be filled
									</div><!--row-->
									</div><!--uploader-->
									<div class="text-center">
									<!-- <a class="btn btn-new"><i class="fa fa-plus"></i> Add new</a> -->
									<!-- <button type="button"  id="btnBulkUpload" onclick="bulkUpload()"><a class="btn btn-next"><i class="fa fa-upload"></i> Bulk Upload</a></button> -->
									</div>
									</div><!--one-->
									</div><!-- row -->
									</div>
									</div>
								</div><!-- container -->
								<!-- <table id="ipAddListBody" class="display dataTable no-footer ListBody" aria-describedby="invoiceDataTable_info" role="grid" style="display:none;">
									<tbody>
									
									</tbody>
								</table> -->
							</div>
							
							<div class="clear"></div>
						</div>
					</div>
				  </div>
				  
				</div>
			  
		
		</div> 
		</div>
		</div>
				  </div> 	
			</div>
			
			<div class="card card_graph"  id="cardcb2"  style="display: none;"><!---->

				<!-- Card header -->
				<div class="card-header" role="tab" id="headingTwo2">
				  <a class="collapsed" >
					<div class="card-header card-header-icon card-header-success" id="cardHeaderPosition">
						<div class="card-icon" id="cardIcon">
						  <i class="material-icons" id="materialIcons">check</i>
						</div>
						<h4 class="card-title">White List IP addresses<i style="float: right; color: #496cb6;font-size: 30px;" data-toggle="collapse" data-parent="#accordionEx1" href="#collapseTwo21"
							aria-expanded="false" aria-controls="collapseTwo21" class="fa fa-angle-down rotate-icon"></i>
						 
						</h4>
						</div>
					<!-- <h5 class="mb-0">
					<strong>Today's Transaction </strong> <i style="float: right; color: #496cb6;font-size: 30px;" data-toggle="collapse" data-parent="#accordionEx1" href="#collapseTwo21"
					aria-expanded="false" aria-controls="collapseTwo21" class="fa fa-angle-down rotate-icon"></i>
					</h5> -->
				  </a>
				</div>
				<div id="collapseTwo21" class="collapse" role="tabpanel" >
<div class="card-body">
<div class="row">
				<div class="col-md-12 col-xs-12">
					
						<div class="cards">
						  <div class="card-header card-header-icon card-header-info" >
							<!-- <div class="card-icon" id="cardIcon">
							  <i class="material-icons" id="materialIcons">multiline_chart</i>
							</div>
							<h4 class="card-title">Today Transaction (Hourly)
							 
							</h4><br><br> -->
							<div class="row" style="align-items: flex-end;">
								<div class="bkn">
								
									<div class="adduT">
										<table>
										  <tr>
											<!-- <td id="wlIpAddListBodyMsg" style="display:block">No White List IP addresses</td> -->
											
											
											<!-- <td style="width:6%"><div class="adduT" style="text-align: right;padding: 14px 0 0 0;"> -->
												<div class="col-md-2">
											<input type="submit" name="remittSubmit" value="Add Rule" onclick="tagsAddRule()" id="popupButtonWIp" class="btn btn-success btn-md" 
											data-toggle="modal" data-target="#myModal1"/>
										</div>
											<div class="col-md-3" style="margin-top: 10px;">
												<input   type='search' class="input-control" id="search_wlIpAddListBody" onkeypress="if(event.keyCode === 32)return false;" placeholder="Search Whitelist IP">
											</div>
											<div class="col-md-2">
											<input class='btn btn-success btn-md' id="searchResult1"   type='submit' value='Search' onclick="searchData('wlIpAddListBody','WHITE_LIST_IP_ADDRESS')">
											</div>
											<div class="col-md-2">
												<input class='btn btn btn-success btn-md bulkDeleteBtn'   type='submit' value='Bulk Delete' onclick="bulkDeleteFraudRule()">
											</div>
											</div></td>
											
										  </tr>
										</table>
									</div>
									
									<div>
										<!-- <p class="accordion_head"><b>Blocked White List IP Addresses</b><span class="plusminus">+</span></p>  -->
										<div class="scrollD" style="overflow-x: scroll!important;">
											<table id="wlIpAddListBody" class="display" align="center" cellspacing="0" width="100%" style="text-align:center;">
												<thead>
													<tr class="boxheadingsmall" style="font-size: 11px;">
														<!-- <th style='text-align: center'>Merchant</th> -->
														<th style='text-align: center'>Whitelist IP Address</th>
														<!-- <th style='text-align: center'>Name</th> -->
														<th style='text-align: center'>Start Date</th>
														<th style='text-align: center'>End Date</th>
														<th style='text-align: center'>Start Time</th>
														<th style='text-align: center'>End Time</th>
														<th style='text-align: center'>Weeks</th>
														<th style='text-align: center'>Action</th>
														
													<th style='text-align: center'>	<input class="form-check-input" type="checkbox" id="select_all1" style="margin-right:10px;" value="" onclick="selectall()">
														<span class="form-check-sign">
														  <span class="check"></span>Action/Select All 
													
														</span>
													</th>
														
													</tr>
												</thead>
											</table>
										</div>
										<div class="card ">
											<div class="card-header card-header-rose card-header-text">
											  <div class="card-text">
												<h4 class="card-title">Add Bulk Rules for Whitelist IP Address</h4>
											  </div>
											</div>
											<div class="card-body ">
										
										<div class="container">
											<div class="row it">
											<div class="col-sm-offset-1 col-sm-10" id="one">
											<p>
											Please upload documents only in 'CSV' format.
											</p><br>
											<div class="row">
											  <div class="col-sm-offset-4 col-sm-4 form-group">
												
											  </div><!--form-group-->
											</div><!--row-->
											<div id="uploader">
											<div class="row uploadDoc">
												<div class="col-sm-4 ">
													<table
														id="example" class="csv1" style="display: none;">
														<thead>
															<tr>
																<!-- <th>Merchant</th> -->
																<th>Whitelist IP Address</th>
																<!-- <th>Name</th> -->
																<th>Start Date</th>
																<th>End Date</th>
																<th>Start Time</th>
																<th>End Time</th>
																<th>Week Days</th>
																<th>Always On</th>
															</tr>
														</thead>
														<tbody>
															<tr>
																<!-- <th>Merchant</th> -->
															<td>192.11.55.66</td>
															<td></td>
															<td></td>
															<td></td>
															<td></td>
															<td></td>
															<td>TRUE</td>
															</tr>
		
															<tr>
																<!-- <th>Merchant</th> -->
															<td>192.11.55.66</td>
															<td>13/09/2020</td>
															<td>25/09/2020</td>
															<td>11:51:41</td>
															<td>11:51:42</td>
															<td>MON TUE WED </td>
															<td>FALSE</td>
															</tr>
														</tbody>
													</table></div>
											  <div class="col-sm-6">
												<div class="docErr">Please upload valid file</div><!--error-->
												<div class="fileUpload btn btn-orange">
												  <img src="https://image.flaticon.com/icons/svg/136/136549.svg" class="icon">
												  <span class="upl" id="upload">Upload document</span>
												  <form enctype="multipart/form-data" method="post" id="wlIpBulkUpload" >
													<div style="display:flex">											<!-- <input type="hidden"   value="IPRULE" name="file1" /> -->
											
												<!-- <input type="hidden"   value="IPRULE" name="file1" /> -->
											  <input type="file" name="fileName" class="upload up" id="up" onchange="readURL(this);" />
											  <input type="submit" class="btn btn-info btn-xs"  id="btnBulkUpload" disabled onclick="bulkUpload('WHITE_LIST_IP_ADDRESS','wlIpBulkUpload')"> 
													</div>
											</form>
												  <!-- <input type="file" class="upload up" id="up" onchange="readURL(this);" /> -->
												</div><!-- btn-orange -->
											  </div><!-- col-3 -->
											  Note: If Always True Flag is checked then Date, Time, Week does not need to be filled but if it is not then all the required fields will have to be filled
											
											</div><!--row-->
											</div><!--uploader-->
											<div class="text-center">
											<!-- <a class="btn btn-new"><i class="fa fa-plus"></i> Add new</a> -->
											<!-- <button type="button" id="btnBulkUpload" onclick="bulkUpload()"><button type="button" id="btnBulkUpload" onclick="bulkUpload()"><a class="btn btn-next"><i class="fa fa-upload"></i> Bulk Upload</a></button></button> -->
											</div>
											</div><!--one-->
											</div><!-- row -->
											</div>
											</div></div>
										<!-- <table id="wlIpAddListBody" class="display dataTable no-footer ListBody" aria-describedby="invoiceDataTable_info" role="grid" style="display:none;">
											<tbody>
											
											</tbody>
										</table> -->
									</div>
									
									<div class="clear"></div>
								</div>
								
								 
								  
							  </div>
						  </div>
						  
						</div>
					  
				
				</div> 
				</div> 
				</div>
				</div>
			</div>
			<div class="card card_graph"   id="cardcb3" style="display: none;"><!---->

				<!-- Card header -->
				<div class="card-header" role="tab" id="headingTwo2">
				  <a class="collapsed" >
					<div class="card-header card-header-icon card-header-rose" id="cardHeaderPosition">
						<div class="card-icon" id="cardIcon">
						  <i class="material-icons" id="materialIcons">flag</i>
						</div>
						<h4 class="card-title"> Block Issuer Countries
							<i style="float: right; color: #496cb6;font-size: 30px;" data-toggle="collapse" data-parent="#accordionEx1" href="#collapseTwo25"
							aria-expanded="false" aria-controls="collapseTwo25" class="fa fa-angle-down rotate-icon"></i>
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
					<h4 class="card-title"> Payment Type Comparison
					 
					</h4><br><br> -->
					<div class="row" style="align-items: flex-end;">
						
						<div class="bkn">
						
							<div class="adduT">
							   <table>
								  <tr>
									<!-- <td id="issuerCountryListBodyMsg" style="display:block">No blocked issuer countries</td>
									<td style="width:6%">
										<div class="adduT" style="text-align: right;padding: 14px 0 0 0;">
																			 -->
																			 <div class="col-md-2">
											<input type="submit" name="remittSubmit" value="Add Rule" id="popupButton2" class="btn btn-success btn-md" data-toggle="modal" data-target="#myModal2"/>
										</div>
										<div class="col-md-3" style="margin-top: 10px;">
												<input   type='search' class="input-control" id="search_issuerCountryListBody" onkeypress="if(event.keyCode === 32)return false;" placeholder="Search Issuer Country">
											</div>
											<div class="col-md-2">
											<input class='btn btn-success btn-md' id="searchResult2"   type='submit' value='Search' onclick="searchData('issuerCountryListBody','BLOCK_CARD_ISSUER_COUNTRY')">
										
										</div>
										<div class="col-md-2">
											<input class='btn btn-success btn-md bulkDeleteBtn'   type='submit' value='Bulk Delete' onclick="bulkDeleteFraudRule()">
										</div>
									</div>
									</td>
								  </tr>
								</table>
							</div> 
							<div id="issLst">
								<!-- <p class="accordion_head"><b>Blocked issuer countries List</b><span class="plusminus">+</span></p> -->
								<div class="scrollD" style="overflow-x: scroll!important;">
									<table class="display" id="issuerCountryListBody" align="center" cellspacing="0" width="100%" style="text-align:center;">
										<thead>
											<tr class="boxheadingsmall" style="font-size: 11px;">
												<!-- <th style='text-align: center'>Merchant</th> -->
												<th style='text-align: center'>Issuer Country</th>
												<th style='text-align: center'>Action</th>
												<th style='text-align: center'>	<input class="form-check-input" type="checkbox" id="select_all2" style="margin-right:10px;" value="" onclick="selectall()">
													<span class="form-check-sign">
													  <span class="check"></span>Action/Select All 
												
													</span>
												</th>
											
												
											</tr>
										</thead>
									</table>
								</div>
								<!-- <div class="card ">
									<div class="card-header card-header-rose card-header-text">
									  <div class="card-text">
										<h4 class="card-title">Add Bulk Rules for Issuer Country</h4>
									  </div>
									</div>
									<div class="card-body ">
								
								<div class="container">
									<div class="row it">
									<div class="col-sm-offset-1 col-sm-10" id="one">
									<p>
									Please upload documents only in 'CSV' format.
									</p><br>
									<div class="row">
									  <div class="col-sm-offset-4 col-sm-4 form-group">
										
									  </div>
									</div>
									<div id="uploader">
									<div class="row uploadDoc">
										<div class="col-sm-4 ">
											<table
												id="example" class="csv" style="display: none;">
												<thead>
													<tr>
													
												<th >Issuer Country</th>
													</tr>
												</thead>
											</table></div>
									  <div class="col-sm-6">
										<div class="docErr">Please upload valid file</div>
										<div class="fileUpload btn btn-orange">
										  <img src="https://image.flaticon.com/icons/svg/136/136549.svg" class="icon">
										  <span class="upl" id="upload">Upload document</span>
										  <form enctype="multipart/form-data" method="post" id="issuerCountryBulkUpload" >
											<div style="display:flex">										
									
									
									  <input type="file" name="fileName" class="upload up" id="up" onchange="readURL(this);" />
									  <input type="submit" class="btn btn-info btn-xs"  id="btnBulkUpload" onclick="bulkUpload('BLOCK_CARD_ISSUER_COUNTRY','issuerCountryBulkUpload')"> 
											</div>
									</form>
										 
										</div>
									  </div>
									  Note: If Always True Flag is checked then Date, Time, Week does not need to be filled but if it is not then all the required fields will have to be filled
									
									</div>
									</div>
									<div class="text-center">
								
									</div>
									</div>
									</div>
									</div>
									</div></div> -->
								
								<!-- <table id="issuerCountryListBody"  class="display dataTable no-footer ListBody" aria-describedby="invoiceDataTable_info" role="grid" style="display: none;">
									<tbody>
										
									 
									</tbody>
								</table> -->
							
							</div>
								<div class="clear"></div> 
						</div>
					 
					 
					  </div>
				  </div>
				 
			  
		
		</div> 
			</div> 
			</div>
			</div>
		</div>
			</div>
		
			<div class="card card_graph" id="cardcb4"  style="display: none;"><!--onclick="monthlycardupdateTabState(this)"-->
				<div class="card-header " role="tab" id="headingTwo1">
					<a class="collapsed" >
						<div class="card-header card-header-icon card-header-info" id="cardHeaderPosition">
						<div class="card-icon" id="cardIcon">
							<i class="material-icons" id="materialIcons">unsubscribe</i>
						  </div> 
						 <h4 class="card-title">Block Email Addresses<i style="float: right; color: #496cb6;font-size: 30px;" data-toggle="collapse" data-parent="#accordionEx1" href="#collapseFive"
							aria-expanded="false" aria-controls="collapseFive" class="fa fa-angle-down rotate-icon"></i>
						   
						  </h4>
						  </div>
					  <!-- <h5 class="mb-0">
						<strong>Monthly Transaction</strong> <i style="float: right; color: #496cb6;font-size: 30px;" data-toggle="collapse" data-parent="#accordionEx1" href="#collapseSix"
						aria-expanded="false" aria-controls="collapseSix" class="fa fa-angle-down rotate-icon"></i>
					  </h5> -->
					</a>
				  </div>
				  <div id="collapseFive" class="collapse active " role="tabpanel" >
<div class="card-body">
		<div class="row">
		<div class="col-md-12 col-xs-12">
			
				<div class="cards">
				  <div class="card-header card-header-icon card-header-info">
					<!-- <div class="card-icon" id="cardIcon">
					  <i class="material-icons" id="materialIcons">multiline_chart</i>
					</div> -->
					<!-- <h4 class="card-title">Monthly Transaction
					 
					</h4><br>
					<br> -->
					<div class="row" style="align-items: flex-end;">
						<div class="bkn">
							
							<div class="adduT">
								   <table>
								  <tr>
									<!-- <td id="emailListBodyMsg" style="display:block">No blocked Email Addresses</td>
									<td style="width:6%"><div class="adduT" style="text-align: right; padding: 14px 0 0 0;"> -->
										<div class="col-md-2">
									<input type="submit" name="remittSubmit" value="Add Rule" onclick="tagsAddRule()" id="popupButton5" class="btn btn-success btn-md" 
									data-toggle="modal" data-target="#myModal5"/>
										</div>
										<div class="col-md-3" style="margin-top: 10px;">
										<input   type='search' class="input-control" id="search_emailListBody" onkeypress="if(event.keyCode === 32)return false;" placeholder="Search Email Address">
									</div>
									<div class="col-md-2">
									<input class='btn btn-success btn-md' id="searchResult3"   type='submit' value='Search' onclick="searchData('emailListBody','BLOCK_EMAIL_ID')">
									</div>
									<div class="col-md-2">
										<input class='btn btn-success btn-md bulkDeleteBtn'   type='submit' value='Bulk Delete' onclick="bulkDeleteFraudRule()">
									</div>
								</div></td>
								  </tr>
								</table>
							</div>
							
							<div>
							   <!-- <p class="accordion_head"><b>Blocked Email Address List</b><span class="plusminus">+</span></p> -->
							   <div class="scrollD" style="overflow-x: scroll!important;">
								<table id="emailListBody" class="display" align="center" cellspacing="0" width="100%" style="text-align:center;">
									<thead>
										<tr class="boxheadingsmall" style="font-size: 11px;">
											<!-- <th style='text-align: center'>Merchant</th> -->
											<th style='text-align: center'>Email Address</th>
											<!-- <th style='text-align: center'>Name</th> -->
											<th style='text-align: center'>Start Date</th>
											<th style='text-align: center'>End Date</th>
											<th style='text-align: center'>Start Time</th>
											<th style='text-align: center'>End Time</th>
											<th style='text-align: center'>Weeks</th>
											<th style='text-align: center'>Action</th>
											<th style='text-align: center'>	<input class="form-check-input" type="checkbox" id="select_all3" style="margin-right:10px;" value="" onclick="selectall()">
												<span class="form-check-sign">
												  <span class="check"></span>Action/Select All 
											
												</span>
											</th>
										<!-- <th style='text-align: center'></th> -->
											
										</tr>
									</thead>
								</table>
							</div>
							<div class="card ">
								<div class="card-header card-header-rose card-header-text">
								  <div class="card-text">
									<h4 class="card-title">Add Bulk Rules for Email Address</h4>
								  </div>
								</div>
								<div class="card-body ">
							<div class="container">
								<div class="row it">
								<div class="col-sm-offset-1 col-sm-10" id="one">
								<p>
								Please upload documents only in 'CSV' format.
								</p><br>
								<div class="row">
								  <div class="col-sm-offset-4 col-sm-4 form-group">
									
								  </div><!--form-group-->
								</div><!--row-->
								<div id="uploader">
								<div class="row uploadDoc">
									<div class="col-sm-4 ">
										<table
											id="example" class="csv2" style="display: none;">
											<thead>
												<tr>
													<!-- <th>Merchant</th> -->
													<th>Email Address</th>
													<!-- <th>Name</th> -->
													<th>Start Date</th>
													<th>End Date</th>
													<th>Start Time</th>
													<th>End Time</th>
													<th>Week Days</th>
														<th>Always On</th>
												</tr>
											</thead>
											<tbody>
												<tr>
													<!-- <th>Merchant</th> -->
												<td>abc@gmail.com</td>
												<td></td>
												<td></td>
												<td></td>
												<td></td>
												<td></td>
												<td>TRUE</td>
												</tr>

												<tr>
													<!-- <th>Merchant</th> -->
												<td>abc@gmail.com</td>
												<td>13/09/2020</td>
												<td>25/09/2020</td>
												<td>11:51:41</td>
												<td>11:51:42</td>
												<td>MON TUE WED </td>
												<td>FALSE</td>
												</tr>
											</tbody>
										</table></div>
								  <div class="col-sm-6">
									<div class="docErr">Please upload valid file</div><!--error-->
									<div class="fileUpload btn btn-orange">
									  <img src="https://image.flaticon.com/icons/svg/136/136549.svg" class="icon">
									  <span class="upl" id="upload">Upload document</span>
									  <form enctype="multipart/form-data" method="post" id="emailBulkUpload" >
										<div style="display:flex">											<!-- <input type="hidden"   value="IPRULE" name="file1" /> -->
								
									<!-- <input type="hidden"   value="IPRULE" name="file1" /> -->
								  <input type="file" name="fileName" class="upload up" id="up" onchange="readURL(this);" />
								  <input type="submit" class="btn btn-info btn-xs"  id="btnBulkUpload" disabled onclick="bulkUpload('BLOCK_EMAIL_ID','emailBulkUpload')"> 
										</div>
								</form>
									  <!-- <input type="file" class="upload up" id="up" onchange="readURL(this);" /> -->
									</div><!-- btn-orange -->
								  </div><!-- col-3 -->
								 
								  Note: If Always True Flag is checked then Date, Time, Week does not need to be filled but if it is not then all the required fields will have to be filled
								</div><!--row-->
								</div><!--uploader-->
								<div class="text-center">
								<!-- <a class="btn btn-new"><i class="fa fa-plus"></i> Add new</a> -->
								<!-- <button type="button" id="btnBulkUpload" onclick="bulkUpload()"><a class="btn btn-next"><i class="fa fa-upload"></i> Bulk Upload</a></button> -->
								</div>
								</div><!--one-->
								</div><!-- row -->
								</div>
								</div></div>

							   <!-- <div  class=" dataTable no-footer ListBody" aria-describedby="invoiceDataTable_info" role="grid" style="display:none;overflow:inherit !important;" >
								<input type="text" id="search" placeholder="Type to search">
							<table id="emailListBody">
									<tbody>
									
									</tbody>
								</table>
							</div> -->
							
								
						</div>
				</div>
					</div>
				  </div>
				  
				</div>
			  
		
		</div> 
		</div>
		</div>
				  </div> 	
			</div>
			
			<div class="card card_graph"   id="cardcb5" style="display: none;"><!--onclick="monthlycardupdateTabState(this)"-->
				<div class="card-header " role="tab" id="headingTwo1">
					<a class="collapsed" >
						<div class="card-header card-header-icon card-header-warning" id="cardHeaderPosition">
						<div class="card-icon" id="cardIcon">
							<i class="fa fa-inr fa-2x"></i>
						  </div> 
						 <h4 class="card-title">Limit Transaction Amount<i style="float: right; color: #496cb6;font-size: 30px;" data-toggle="collapse" data-parent="#accordionEx1" href="#collapseSeven"
							aria-expanded="false" aria-controls="collapseSeven" class="fa fa-angle-down rotate-icon"></i>
						   
						  </h4>
						  </div>
					  <!-- <h5 class="mb-0">
						<strong>Monthly Transaction</strong> <i style="float: right; color: #496cb6;font-size: 30px;" data-toggle="collapse" data-parent="#accordionEx1" href="#collapseEight"
						aria-expanded="false" aria-controls="collapseEight" class="fa fa-angle-down rotate-icon"></i>
					  </h5> -->
					</a>
				  </div>
				  <div id="collapseSeven" class="collapse active " role="tabpanel" >
<div class="card-body">
		<div class="row">
		<div class="col-md-12 col-xs-12">
			
				<div class="cards">
				  <div class="card-header card-header-icon card-header-info">
					<!-- <div class="card-icon" id="cardIcon">
					  <i class="material-icons" id="materialIcons">multiline_chart</i>
					</div> -->
					<!-- <h4 class="card-title">Monthly Transaction
					 
					</h4><br>
					<br> -->
					<div class="row" style="align-items: flex-end;">
						<div class="bkn">
							
								<div class="adduT">
									<table>
										<tr>
										 <!-- <td id="txnAmountListBodyMsg" style="display:block">No blocked Transactional Amount</td>
											  <td style="width:6%"><div class="adduT" style="text-align: right; padding: 14px 0 0 0;"> -->
											<input type="submit" name="remittSubmit" value="Add Rule" id="popupButton8" class="btn btn-success btn-md" 
											data-toggle="modal" data-target="#myModal8"/>
											<!-- <input class='btn btn-success btn-md' id="searchResult"  type='submit' value='Search' onclick="searchData('txnAmountListBody','BLOCK_TXN_AMOUNT')"> -->
											  </div></td>
										</tr>
									</table>
								</div>
								<div>
									<!-- <p class="accordion_head"><b>Blocked Transactional Amount List</b><span class="plusminus">+</span></p> -->
									<div class="scrollD" style="overflow-x: scroll!important;">
										<table class="display" id="txnAmountListBody" align="center" cellspacing="0" width="100%" style="text-align:center;">
											<thead>
												<tr class="boxheadingsmall" style="font-size: 11px;">
													<!-- <th style='text-align: center'>Merchant</th> -->
													<th style='text-align: center'>Currency</th>
													<th style='text-align: center'>Min Amount</th>
													<th style='text-align: center'>Max Amount</th>
													<th style='text-align: center'>Action</th>
											

													
												</tr>
											</thead>
										</table>
									</div>
		
									
		
									<!-- <table id="txnAmountListBody" class="display dataTable no-footer ListBody" aria-describedby="invoiceDataTable_info" role="grid" style="display:none;">
									<tbody>
	
								 </tbody>
									</table> -->
								</div>
								<div class="clear"></div>
						</div>
					</div>
				  </div>
				  
				</div>
			  
		
		</div> 
		</div>
		</div>
				  </div> 	
			</div>
			<div class="card card_graph"  id="cardcb6" style="display: none;"><!--onclick="monthlycardupdateTabState(this)"-->
				<div class="card-header " role="tab" id="headingTwo1">
					<a class="collapsed" >
						<div class="card-header card-header-icon card-header-warning" id="cardHeaderPosition">
						<div class="card-icon" id="cardIcon">
							<i class="material-icons" id="materialIcons">payment</i>
						  </div> 
						 <h4 class="card-title">Block Card Ranges<i style="float: right; color: #496cb6;font-size: 30px;" data-toggle="collapse" data-parent="#accordionEx1" href="#collapseEight"
							aria-expanded="false" aria-controls="collapseEight" class="fa fa-angle-down rotate-icon"></i>
						   
						  </h4>
						  </div>
					  <!-- <h5 class="mb-0">
						<strong>Monthly Transaction</strong> <i style="float: right; color: #496cb6;font-size: 30px;" data-toggle="collapse" data-parent="#accordionEx1" href="#collapseFive"
						aria-expanded="false" aria-controls="collapseTwo1" class="fa fa-angle-down rotate-icon"></i>
					  </h5> -->
					</a>
				  </div>
				  <div id="collapseEight" class="collapse active " role="tabpanel" >
<div class="card-body">
		<div class="row">
		<div class="col-md-12 col-xs-12">
			
				<div class="cards">
				  <div class="card-header card-header-icon card-header-info">
					<!-- <div class="card-icon" id="cardIcon">
					  <i class="material-icons" id="materialIcons">multiline_chart</i>
					</div> -->
					<!-- <h4 class="card-title">Monthly Transaction
					 
					</h4><br>
					<br> -->
					<div class="row" style="align-items: flex-end;">
						<div class="bkn">
							
								<div class="adduT">
									<table>
									<tr>
									<!-- <td id="cardBinListBodyMsg" style="display:block">No blocked card ranges</td>
									<td style="width:6%">
										<div class="adduT" style="text-align: right;padding: 14px 0 0 0;"> -->
											<div class="col-md-2">
										<input type="submit" name="remittSubmit" value="Add Rule" onclick="tagsAddRule()" id="popupButton3" class="btn btn-success btn-md" 
										data-toggle="modal" data-target="#myModal3"/>
											</div>
										<div class="col-md-3" style="margin-top: 10px;">
											<input   type='search' class="input-control" id="search_cardBinListBody" onkeypress="if(event.keyCode === 32)return false;" placeholder="Search Card Bin Range">
										</div>
										<div class="col-md-2">
										<input class='btn btn-success btn-md' id="searchResult4"   type='submit' value='Search' onclick="searchData('cardBinListBody','BLOCK_CARD_BIN')">
										</div>
										<div class="col-md-2">
											<input class='btn btn-success btn-md bulkDeleteBtn'  type='submit' value='Bulk Delete' onclick="bulkDeleteFraudRule()">
										</div>
										</div>
									</td>
									</tr>						  
									</table>
								</div>
							<div>
								<!-- <p class="accordion_head"><b>Blocked Card Range List</b><span class="plusminus">+</span></p>  -->
								<div class="scrollD" style="overflow-x: scroll!important;">
									<table  class="display" id="cardBinListBody" align="center" cellspacing="0" width="100%" style="text-align:center;">
										<thead>
											<tr class="boxheadingsmall" style="font-size: 11px;">
												<!-- <th style='text-align: center'>Merchant</th> -->
												<th style='text-align: center'>Card Range</th>
												<!-- <th style='text-align: center'>Name</th> -->
												<th style='text-align: center'>Start Date</th>
												<th style='text-align: center'>End Date</th>
												<th style='text-align: center'>Start Time</th>
												<th style='text-align: center'>End Time</th>
												<th style='text-align: center'>Weeks</th>
												<th style='text-align: center'>Action</th>
												<th style='text-align: center'>	<input class="form-check-input" type="checkbox" id="select_all4" style="margin-right:10px;" value="" onclick="selectall()">
													<span class="form-check-sign">
													  <span class="check"></span>Action/Select All 
												
													</span>
												</th>
											<!-- <th style='text-align: center'></th> -->
												
											</tr>
										</thead>
									</table>
								</div>
	
								<div class="card ">
									<div class="card-header card-header-rose card-header-text">
									  <div class="card-text">
										<h4 class="card-title">Add Bulk Rules for Block Cards</h4>
									  </div>
									</div>
									<div class="card-body ">
								<div class="container">
									<div class="row it">
									<div class="col-sm-offset-1 col-sm-10" id="one">
									<p>
									Please upload documents only in 'CSV' format.
									</p><br>
									<div class="row">
									  <div class="col-sm-offset-4 col-sm-4 form-group">
										
									  </div><!--form-group-->
									</div><!--row-->
									<div id="uploader">
									<div class="row uploadDoc">
										<div class="col-sm-4 ">
											<table
												id="example" class="csv3" style="display: none;">
												<thead>
													<tr>
														<!-- <th>Merchant</th> -->
														<th>Card Range</th>
														<!-- <th>Name</th> -->
														<th>Start Date</th>
														<th>End Date</th>
														<th>Start Time</th>
														<th>End Time</th>
														<th>Week Days</th>
														<th>Always On</th>
													</tr>
												</thead>
												<tbody>
													<tr>
														<!-- <th>Merchant</th> -->
													<td>456785</td>
													<td></td>
													<td></td>
													<td></td>
													<td></td>
													<td></td>
													<td>TRUE</td>
													</tr>

													<tr>
														<!-- <th>Merchant</th> -->
													<td>456785</td>
													<td>13/09/2020</td>
													<td>25/09/2020</td>
													<td>11:51:41</td>
													<td>11:51:42</td>
													<td>MON TUE WED </td>
													<td>FALSE</td>
													</tr>
												</tbody>
											</table></div>
									  <div class="col-sm-6">
										<div class="docErr">Please upload valid file</div><!--error-->
										<div class="fileUpload btn btn-orange">
										  <img src="https://image.flaticon.com/icons/svg/136/136549.svg" class="icon">
										  <span class="upl" id="upload">Upload document</span>
										  <form enctype="multipart/form-data" method="post" id="cardRangeBulkUpload" >
											<div style="display:flex">											<!-- <input type="hidden"   value="IPRULE" name="file1" /> -->
									
										<!-- <input type="hidden"   value="IPRULE" name="file1" /> -->
									  <input type="file" name="fileName" class="upload up" id="up" onchange="readURL(this);" />
									  <input type="submit" class="btn btn-info btn-xs"  id="btnBulkUpload" disabled onclick="bulkUpload('BLOCK_CARD_BIN','cardRangeBulkUpload')"> 
											</div>
									</form>
										  <!-- <input type="file" class="upload up" id="up" onchange="readURL(this);" /> -->
										</div><!-- btn-orange -->
									  </div><!-- col-3 -->
									  Note: If Always True Flag is checked then Date, Time, Week does not need to be filled but if it is not then all the required fields will have to be filled
									
									</div><!--row-->
									</div><!--uploader-->
									<div class="text-center">
									<!-- <a class="btn btn-new"><i class="fa fa-plus"></i> Add new</a> -->
									<!-- <button type="button" id="btnBulkUpload" onclick="bulkUpload()"><a class="btn btn-next"><i class="fa fa-upload"></i> Bulk Upload</a></button> -->
									</div>
									</div><!--one-->
									</div><!-- row -->
									</div>
									</div></div>
								<!-- <table id="cardBinListBody" class="display dataTable no-footer ListBody" aria-describedby="invoiceDataTable_info" role="grid" style="display: none;">
								<tbody>
	
	
								</tbody>
								</table> -->
							</div>
							<div class="clear"></div>
						</div>
					</div>
					
				  </div>
				  
				</div>
			  
		
		</div> 
		</div>
		</div>
				  </div> 	
			</div>
			<!--Add new rule from here-->
			<div class="card card_graph" id="cardcb7"  style="display: none;"><!--onclick="monthlycardupdateTabState(this)"-->
				<div class="card-header " role="tab" id="headingTwo1">
					<a class="collapsed" >
						<div class="card-header card-header-icon card-header-info" id="cardHeaderPosition">
						<div class="card-icon" id="cardIcon">
							<i class="material-icons" id="materialIcons">app_blocking</i>
						  </div> 
						 <h4 class="card-title">Block Phone Number<i style="float: right; color: #496cb6;font-size: 30px;" data-toggle="collapse" data-parent="#accordionEx1" href="#collapseNine"
							aria-expanded="false" aria-controls="collapseNine" class="fa fa-angle-down rotate-icon"></i>
						   
						  </h4>
						  </div>
					  <!-- <h5 class="mb-0">
						<strong>Monthly Transaction</strong> <i style="float: right; color: #496cb6;font-size: 30px;" data-toggle="collapse" data-parent="#accordionEx1" href="#collapseSix"
						aria-expanded="false" aria-controls="collapseSix" class="fa fa-angle-down rotate-icon"></i>
					  </h5> -->
					</a>
				  </div>
				  <div id="collapseNine" class="collapse active " role="tabpanel" >
<div class="card-body">
		<div class="row">
		<div class="col-md-12 col-xs-12">
			
				<div class="cards">
				  <div class="card-header card-header-icon card-header-info">
					<!-- <div class="card-icon" id="cardIcon">
					  <i class="material-icons" id="materialIcons">multiline_chart</i>
					</div> -->
					<!-- <h4 class="card-title">Monthly Transaction
					 
					</h4><br>
					<br> -->
					<div class="row" style="align-items: flex-end;">
						<div class="bkn">
							
							<div class="adduT">
								   <table>
								  <tr>
									<!-- <td id="emailListBodyMsg" style="display:block">No blocked Email Addresses</td>
									<td style="width:6%"><div class="adduT" style="text-align: right; padding: 14px 0 0 0;"> -->
										<div class="col-md-2">
									<input type="submit" name="remittSubmit" value="Add Rule" onclick="tagsAddRule()" id="popupButton9" class="btn btn-success btn-md" 
									data-toggle="modal" data-target="#myModal9"/>
										</div>
										<div class="col-md-3" style="margin-top: 10px;">
										<input   type='search' class="input-control" id="search_phoneListBody" onkeypress="if(event.keyCode === 32)return false;" placeholder="Search Phone Number">
									</div>
									<div class="col-md-2">
									<input class='btn btn-success btn-md' id="searchResult5"   type='submit' value='Search' onclick="searchData('phoneListBody','BLOCK_PHONE_NUMBER')">
									</div>
									<div class="col-md-2">
										<input class='btn btn-success btn-md bulkDeleteBtn'  type='submit' value='Bulk Delete' onclick="bulkDeleteFraudRule('phoneListBody','BLOCK_PHONE_NUMBER')">
									</div>
								</div></td>
								  </tr>
								</table>
							</div>
							
							<div>
							   <!-- <p class="accordion_head"><b>Blocked Email Address List</b><span class="plusminus">+</span></p> -->
							   <div class="scrollD" style="overflow-x: scroll!important;">
								<table id="phoneListBody" class="display" align="center" cellspacing="0" width="100%" style="text-align:center;">
									<thead>
										<tr class="boxheadingsmall" style="font-size: 11px;">
											<!-- <th style='text-align: center'>Merchant</th> -->
											<th style='text-align: center'>Phone Number</th>
											<!-- <th style='text-align: center'>Name</th> -->
											<th style='text-align: center'>Start Date</th>
											<th style='text-align: center'>End Date</th>
											<th style='text-align: center'>Start Time</th>
											<th style='text-align: center'>End Time</th>
											<th style='text-align: center'>Weeks</th>
											<th style='text-align: center'>Action</th>
											<th style='text-align: center'>	<input class="form-check-input" type="checkbox" id="select_all5" style="margin-right:10px;" value="" onclick="selectall()">
												<span class="form-check-sign">
												  <span class="check"></span>Action/Select All 
											
												</span>
											</th>
										<!-- <th style='text-align: center'></th> -->
											
										</tr>
									</thead>
								</table>
							</div>
							<div class="card ">
								<div class="card-header card-header-rose card-header-text">
								  <div class="card-text">
									<h4 class="card-title">Add Bulk Rules for Phone Number</h4>
								  </div>
								</div>
								<div class="card-body ">
							<div class="container">
								<div class="row it">
								<div class="col-sm-offset-1 col-sm-10" id="one">
								<p>
								Please upload documents only in 'CSV' format.
								</p><br>
								<div class="row">
								  <div class="col-sm-offset-4 col-sm-4 form-group">
									
								  </div><!--form-group-->
								</div><!--row-->
								<div id="uploader">
								<div class="row uploadDoc">
									<div class="col-sm-4 ">
										<table
											id="example" class="csv4" style="display: none;">
											<thead>
												<tr>
													<!-- <th>Merchant</th> -->
													<th>Phone Number</th>
													<!-- <th>Name</th> -->
													<th>Start Date</th>
													<th>End Date</th>
													<th>Start Time</th>
													<th>End Time</th>
													<th>Week Days</th>
														<th>Always On</th>
												</tr>
											</thead>
											<tbody>
												<tr>
													<!-- <th>Merchant</th> -->
												<td>9988998899</td>
												<td></td>
												<td></td>
												<td></td>
												<td></td>
												<td></td>
												<td>TRUE</td>
												</tr>

												<tr>
													<!-- <th>Merchant</th> -->
												<td>9988998899</td>
												<td>13/09/2020</td>
												<td>25/09/2020</td>
												<td>11:51:41</td>
												<td>11:51:42</td>
												<td>MON TUE WED </td>
												<td>FALSE</td>
												</tr>
											</tbody>
										</table></div>
								  <div class="col-sm-6">
									<div class="docErr">Please upload valid file</div><!--error-->
									<div class="fileUpload btn btn-orange">
									  <img src="https://image.flaticon.com/icons/svg/136/136549.svg" class="icon">
									  <span class="upl" id="upload">Upload document</span>
									  <form enctype="multipart/form-data" method="post" id="phoneBulkUpload" >
										<div style="display:flex">											<!-- <input type="hidden"   value="IPRULE" name="file1" /> -->
								
									<!-- <input type="hidden"   value="IPRULE" name="file1" /> -->
								  <input type="file" name="fileName" class="upload up" id="up" onchange="readURL(this);" />
								  <input type="submit" class="btn btn-info btn-xs"  disabled id="btnBulkUpload" onclick="bulkUpload('BLOCK_PHONE_NUMBER','phoneBulkUpload')"> 
										</div>
								</form>
									  <!-- <input type="file" class="upload up" id="up" onchange="readURL(this);" /> -->
									</div><!-- btn-orange -->
								  </div><!-- col-3 -->
								  Note: If Always True Flag is checked then Date, Time, Week does not need to be filled but if it is not then all the required fields will have to be filled
								
								</div><!--row-->
								</div><!--uploader-->
								<div class="text-center">
								<!-- <a class="btn btn-new"><i class="fa fa-plus"></i> Add new</a> -->
								<!-- <button type="button" id="btnBulkUpload" onclick="bulkUpload()"><a class="btn btn-next"><i class="fa fa-upload"></i> Bulk Upload</a></button> -->
								</div>
								</div><!--one-->
								</div><!-- row -->
								</div>
								</div></div>

							   <!-- <div  class=" dataTable no-footer ListBody" aria-describedby="invoiceDataTable_info" role="grid" style="display:none;overflow:inherit !important;" >
								<input type="text" id="search" placeholder="Type to search">
							<table id="emailListBody">
									<tbody>
									
									</tbody>
								</table>
							</div> -->
							
								
						</div>
				</div>
					</div>
				  </div>
				  
				</div>
			  
		
		</div> 
		</div>
		</div>
				  </div> 	
			</div>
		
			<div class="card card_graph"  id="cardcb8" style="display: none;"><!--onclick="monthlycardupdateTabState(this)"-->
				<div class="card-header " role="tab" id="headingTwo1">
					<a class="collapsed" >
						<div class="card-header card-header-icon card-header-success" id="cardHeaderPosition">
						<div class="card-icon" id="cardIcon">
							<i class="fa fa-credit-card-alt"></i>
						  </div> 
						 <h4 class="card-title"> Block Card Mask<i style="float: right; color: #496cb6;font-size: 30px;" data-toggle="collapse" data-parent="#accordionEx1" href="#collapseTen"
							aria-expanded="false" aria-controls="collapseTen" class="fa fa-angle-down rotate-icon"></i>
						   
						  </h4>
						  </div>
					  <!-- <h5 class="mb-0">
						<strong>Monthly Transaction</strong> <i style="float: right; color: #496cb6;font-size: 30px;" data-toggle="collapse" data-parent="#accordionEx1" href="#collapseFour"
						aria-expanded="false" aria-controls="collapseFour" class="fa fa-angle-down rotate-icon"></i>
					  </h5> -->
					</a>
				  </div>
				  <div id="collapseTen" class="collapse active " role="tabpanel" >
<div class="card-body">
		<div class="row">
		<div class="col-md-12 col-xs-12">
			
				<div class="cards">
				  <div class="card-header card-header-icon card-header-info">
					<!-- <div class="card-icon" id="cardIcon">
					  <i class="material-icons" id="materialIcons">multiline_chart</i>
					</div> -->
					<!-- <h4 class="card-title">Monthly Transaction
					 
					</h4><br>
					<br> -->
					<div class="row" style="align-items: flex-end;">
						<div class="bkn">
							
							<div class="adduT">
								   <table>
								  <tr>
									<!-- <td id="emailListBodyMsg" style="display:block">No blocked Email Addresses</td>
									<td style="width:6%"><div class="adduT" style="text-align: right; padding: 14px 0 0 0;"> -->
										<div class="col-md-2">
											<input type="submit" name="remittSubmit" value="Add Rule" onclick="tagsAddRule()" id="popupButton0" class="btn btn-success btn-md" 
											data-toggle="modal" data-target="#myModal0"/>	
										</div>
										<div class="col-md-3" style="margin-top: 10px;">
										<input   type='search' class="input-control" id="search_cardNoListBody" onkeypress="if(event.keyCode === 32)return false;" placeholder="Search Card Number">
									</div>
									<div class="col-md-2">
									<input class='btn btn-success btn-md' id="searchResult6"   type='submit' value='Search' onclick="searchData('cardNoListBody','BLOCK_CARD_NO')">
									</div>
									<div class="col-md-2">
										<input class='btn btn-success btn-md bulkDeleteBtn'   type='submit' value='Bulk Delete' onclick="bulkDeleteFraudRule()">
									</div>
								</div></td>
								  </tr>
								</table>
							</div>

							<div>
								<div class="scrollD" style="overflow-x: scroll!important;">
									<table id="cardNoListBody" class="display" align="center" cellspacing="0" width="100%" style="text-align:center;">
										<thead>
											<tr class="boxheadingsmall" style="font-size: 11px;">
												<!-- <th style='text-align: center'>Merchant</th> -->
												<th style='text-align: center'>Card No.</th>
												<!-- <th style='text-align: center'>Allowed Transaction</th> -->
												<th style='text-align: center'>Start Date</th>
												<th style='text-align: center'>End Date</th>
												<th style='text-align: center'>Start Time</th>
												<th style='text-align: center'>End Time</th>
												<th style='text-align: center'>Weeks</th>
												<th style='text-align: center'>Action</th>
												<th style='text-align: center'>	<input class="form-check-input" type="checkbox" id="select_all6" style="margin-right:10px;" value="" onclick="selectall()">
													<span class="form-check-sign">
													  <span class="check"></span>Action/Select All 
												
													</span>
												</th>
											<!-- <th style='text-align: center'></th> -->
												
											</tr>
										</thead>
									</table>
								</div>
	
								<div class="card ">
									<div class="card-header card-header-rose card-header-text">
									  <div class="card-text">
										<h4 class="card-title">Add Bulk Rules for Card Mask</h4>
									  </div>
									</div>
									<div class="card-body ">
								<div class="container">
									<div class="row it">
									<div class="col-sm-offset-1 col-sm-10" id="one">
									<p>
									Please upload documents only in 'CSV' format.
									</p><br>
									<div class="row">
									  <div class="col-sm-offset-4 col-sm-4 form-group">
										
									  </div><!--form-group-->
									</div><!--row-->
									<div id="uploader">
									<div class="row uploadDoc">
										<div class="col-sm-4 ">
											<table
												id="example" class="csv5" style="display: none;">
												<thead>
													<tr>
														<th >Card No.</th>
														<!-- <th >Allowed Transaction</th> -->
														<th>Start Date</th>
														<th>End Date</th>
														<th>Start Time</th>
														<th>End Time</th>
														<th>Week Days</th>
															<th>Always On</th>
													</tr>
												</thead>
												<tbody>
													<tr>
														<!-- <th>Merchant</th> -->
													<td>401200******3714</td>
													<td></td>
													<td></td>
													<td></td>
													<td></td>
													<td></td>
													<td>TRUE</td>
													</tr>

													<tr>
														<!-- <th>Merchant</th> -->
													<td>401200******3714</td>
													<td>13/09/2020</td>
													<td>25/09/2020</td>
													<td>11:51:41</td>
													<td>11:51:42</td>
													<td>MON TUE WED </td>
													<td>FALSE</td>
													</tr>
												</tbody>
											</table></div>
									  <div class="col-sm-6">
										<div class="docErr">Please upload valid file</div><!--error-->
										<div class="fileUpload btn btn-orange">
										  <img src="https://image.flaticon.com/icons/svg/136/136549.svg" class="icon">
										  <span class="upl" id="upload">Upload document</span>
										  <form enctype="multipart/form-data" method="post" id="cardMaskBulkUpload" >
											<div style="display:flex">											<!-- <input type="hidden"   value="IPRULE" name="file1" /> -->
									
										<!-- <input type="hidden"   value="IPRULE" name="file1" /> -->
									  <input type="file" name="fileName" class="upload up" id="up" onchange="readURL(this);" />
									  <input type="submit" class="btn btn-info btn-xs" disabled  id="btnBulkUpload" onclick="bulkUpload('BLOCK_CARD_NO','cardMaskBulkUpload')"> 
											</div>
									</form>
										  <!-- <input type="file" class="upload up" id="up" onchange="readURL(this);" /> -->
										</div><!-- btn-orange -->
									  </div><!-- col-3 -->
									  Note: If Always True Flag is checked then Date, Time, Week does not need to be filled but if it is not then all the required fields will have to be filled
									
									</div><!--row-->
									</div><!--uploader-->
									<div class="text-center">
									<!-- <a class="btn btn-new"><i class="fa fa-plus"></i> Add new</a> -->
									<!-- <button type="button" id="btnBulkUpload" onclick="bulkUpload()"><a class="btn btn-next"><i class="fa fa-upload"></i> Bulk Upload</a></button> -->
									</div>
									</div><!--one-->
									</div><!-- row -->
									</div>
									</div></div>
							</div>

					</div>
				  </div>
				  
				</div>
			  
		
		</div> 
		</div>
		</div>
				  </div> 	
			</div>
		</div>
		<div class="card card_graph"   id="cardcb9" style="display: none;"><!--onclick="monthlycardupdateTabState(this)"-->
			<div class="card-header " role="tab" id="headingTwo1">
				<a class="collapsed" >
					<div class="card-header card-header-icon card-header-rose" id="cardHeaderPosition">
					<div class="card-icon" id="cardIcon">
						<i class="fa fa-inr fa-2x"></i>
					  </div> 
					 <h4 class="card-title">Limit Transaction Amount Velocity<i style="float: right; color: #496cb6;font-size: 30px;" data-toggle="collapse" data-parent="#accordionEx1" href="#collapseEleven"
						aria-expanded="false" aria-controls="collapseEleven" class="fa fa-angle-down rotate-icon"></i>
					   
					  </h4>
					  </div>
				  <!-- <h5 class="mb-0">
					<strong>Monthly Transaction</strong> <i style="float: right; color: #496cb6;font-size: 30px;" data-toggle="collapse" data-parent="#accordionEx1" href="#collapseEight"
					aria-expanded="false" aria-controls="collapseEight" class="fa fa-angle-down rotate-icon"></i>
				  </h5> -->
				</a>
			  </div>
			  <div id="collapseEleven" class="collapse active " role="tabpanel" >
<div class="card-body">
	<div class="row">
	<div class="col-md-12 col-xs-12">
		
			<div class="cards">
			  <div class="card-header card-header-icon card-header-info">
				<!-- <div class="card-icon" id="cardIcon">
				  <i class="material-icons" id="materialIcons">multiline_chart</i>
				</div> -->
				<!-- <h4 class="card-title">Monthly Transaction
				 
				</h4><br>
				<br> -->
				<div class="row" style="align-items: flex-end;">
					<div class="bkn">
						
							<div class="adduT">
								<table>
									<tr>
									 <!-- <td id="txnAmountListBodyMsg" style="display:block">No blocked Transactional Amount</td>
										  <td style="width:6%"><div class="adduT" style="text-align: right; padding: 14px 0 0 0;"> -->
										  <input type="submit" name="remittSubmit" value="Add Rule" id="popupButton11" class="btn btn-success btn-md" 
										data-toggle="modal" data-target="#myModal11"/>
										<!-- <input class='btn btn-success btn-md' id="searchResult"  type='submit' value='Search' onclick="searchData('txnAmountListBody','BLOCK_TXN_AMOUNT')"> -->
										  </div></td>
									</tr>
								</table>
							</div>
							<div>
								<!-- <p class="accordion_head"><b>Blocked Transactional Amount List</b><span class="plusminus">+</span></p> -->
								<div class="scrollD" style="overflow-x: scroll!important;">
									<table class="display" id="txnAmountVelocityListBody" align="center" cellspacing="0" width="100%" style="text-align:center;">
										<thead>
											<tr class="boxheadingsmall" style="font-size: 11px;">
												<!-- <th style='text-align: center'>Merchant</th> -->
												<th style='text-align: center'>Currency</th>
												<th style='text-align: center'>Transaction Amount</th>
												<th style='text-align: center'>User Identifier</th>
												<th style='text-align: center'>Action</th>
										

												
											</tr>
										</thead>
									</table>
								</div>
	
								
	
								<!-- <table id="txnAmountListBody" class="display dataTable no-footer ListBody" aria-describedby="invoiceDataTable_info" role="grid" style="display:none;">
								<tbody>

							 </tbody>
								</table> -->
							</div>
							<div class="clear"></div>
					</div>
				</div>
			  </div>
			  
			</div>
		  
	
	</div> 
	</div>
	</div>
			  </div> 	
		</div>
			<!--New Rule End here-->
			
			
				</div>
  
  </div>
  </div>
  </div>
  </div>
	

  
<script>
	$(function () {
		$('#myModal').on('hidden.bs.modal', function () {
			$('#tagipAddress').tagsinput('removeAll');
			$('#tagipAddress').val('');
			// var optionName = 'repeatDays';
			// $('input[name="' + optionName+ '"]:checked').each(function () {
			// 	$(this).val(false)
			// });

			$('.modal-body').find('input, label, ul, li, span').val('');
			document.getElementById("validate_err").style.display = "none";
			document.getElementById("validate_err1").style.display = "none";

			//To remove checked week days
			$('.ip4ClassWeeks').each(function () { $(this).prop('checked', false) });
		});

		$('#myModal1').on('hidden.bs.modal', function () {
			$('.modal-body').find('input, label, ul, li, span').val('');
			document.getElementById("validate_err1").style.display = "none";
			document.getElementById("validate_err").style.display = "none";

			//To remove checked week days
			$('.whiteListClass').each(function () { $(this).prop('checked', false) });
		});

		$('#myModal2').on('hidden.bs.modal', function () {
			$('.modal-body').find('ul, li').val('');
			//To remove checked countries
			$('.issuerClass').each(function () { $(this).prop('checked', false) });
		});

		$('#myModal3').on('hidden.bs.modal', function () {
			$('.modal-body').find('input, label, ul, li, span').val('');
			document.getElementById("validate_crd").style.display = "none";

			//To remove checked week days
			$('.binRangeClass').each(function () { $(this).prop('checked', false) });
		});

		$('#myModal5').on('hidden.bs.modal', function () {
			$('.modal-body').find('input, label, ul, li, span').val('');
			document.getElementById("validate_email").style.display = "none";

			//To remove checked week days
			$('.emailAddClass').each(function () { $(this).prop('checked', false) });
		});
		
		$('#myModal9').on('hidden.bs.modal', function () {
			$('.modal-body').find('input, label, ul, li, span').val('');
			document.getElementById("validate_phone").style.display = "none";

			//To remove checked week days
			$('.phoneAddClass').each(function () { $(this).prop('checked', false) });
		});
		$('#myModal0').on('hidden.bs.modal', function () {
			$('.modal-body').find('input, label, ul, li, span').val('');
			document.getElementById("validate_crdInpre").style.display = "none";
			document.getElementById("validate_crdLpre").style.display = "none";
			
			//To remove checked week days
			 $('.limitNoClass').each(function() { $(this).prop('checked', false) });
	});


		$('#myModal8').on('hidden.bs.modal', function () {
			$('.modal-body').find('input, select').val('');
		});
		$('#myModal11').on('hidden.bs.modal', function () {
			$('.modal-body').find('input, select').val('');
		});

	});

</script>

<!------Validation to check min and max values in Transaction Amount(IP-40)-----> 
<script>
	function checkTransactionVal() {
		var minimumAmt = document.getElementById("minTransactionAmount").value;
		var maximumAmt = document.getElementById("maxTransactionAmount").value;

		var minimumInt = parseInt(minimumAmt);
		var maximumInt = parseInt(maximumAmt);

		if (minimumInt != "" && maximumInt != "") {
			if (minimumInt > maximumInt) {
				document.getElementById("amountError").style.display = "block";
				return false;
			} else {
				document.getElementById("amountError").style.display = "none";
			}
		}
	
	}
</script>

<!-----------For Logout Dropdown(IP-32)-------------->
<script>
		$('#minTransactionAmount').keypress(function(e){ 
	if (this.value.length == 0 && e.which == 48 ){
	 return false;
	 }
  });
  $('#maxTransactionAmount').keypress(function(e){ 
	if (this.value.length == 0 && e.which == 48 ){
	 return false;
	 }
  });
  $('#perCardTransactionAllowed').keypress(function(e){ 
	if (this.value.length == 0 && e.which == 48 ){
	 return false;
	 }
  });
  $('#txnAmountVelocity').keypress(function(e){ 
	if (this.value.length == 0 && e.which == 48 ){
	 return false;
	 }
  });
  

	$(".dropdown").click(function () {
		var elementVal = document.getElementById("openDropdown");
		if (elementVal.style.display == 'none') {
			elementVal.style.display = 'block';
		}
		else if (elementVal.style.display == "") {
			elementVal.style.display = 'block';
		}
		else {
			elementVal.style.display = 'none';
		}
	});
</script>

<!----------To hide logout popup on BLUR------->
<script>
	$(document).click(function (e) {
		if (!$(e.target).is("#openDropdown")) {
			if ($('#openDropdown').is(':visible')) {
				document.getElementById("openDropdown").style.display = "none";
			}
		}
	});
</script>

</body>
</html>