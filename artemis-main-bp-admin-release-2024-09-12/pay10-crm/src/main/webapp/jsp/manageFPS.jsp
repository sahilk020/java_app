<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="/struts-tags" prefix="s"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">

<title>Merchant FPS Access</title>

<!----------CSS---------------->
<link rel="stylesheet" href="../css/fraudPrevention.css" rel="stylesheet">
<!-- <link rel="stylesheet" href="../css/bootstrap.min.css" rel="stylesheet" /> -->
<link href="../css/jquery-ui.css" rel="stylesheet" />
<link href="../css/ui.theme.css" rel="stylesheet">
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
<script src="../js/core/popper.min.js"></script>
<script src="../js/core/bootstrap-material-design.min.js"></script>
<script src="../js/plugins/perfect-scrollbar.jquery.min.js"></script>
<!-- Control Center for Material Dashboard: parallax effects, scripts for the example pages etc -->
<script src="../js/material-dashboard.js?v=2.1.0" type="text/javascript"></script>

<script src="../js/moment-with-locales.js"></script>
<script src="../js/bootstrap-datetimepicker.js"></script>
<script src="../js/jquery.popupoverlay.js"></script>
<script src="../js/jquery-ui.js"></script>
<script src="../js/bootstrap-tagsinput.min.js"></script>
<script src="../js/bootstrap-timepicker.min.js"></script> 
<script src="../js/jquery.select2.js" type="text/javascript"></script>
<script>
		$(document).ready(function () {
			$("#merchant").select2();
		});
	</script>
<script type="text/javascript">
	$(document).ready(function(){
		
		var preferenceSetOBJ = [];
		var preferenceSetconst = document.getElementById("preferenceSet").value;
		
		if(preferenceSetconst){
			
				preferenceSetOBJ = JSON.parse(preferenceSetconst.replace(/&quot;/g,'"'));
				$('.checkbox').prop('checked', false);
				var counter = 0;
				for(index =0;index<preferenceSetOBJ.length;index++){
					$('#'+preferenceSetOBJ[index]).prop('checked', true);
					counter++;
				}
				if(counter == 9){
					$('#select_all').prop('checked',true);	
				}
				else{
					$('#select_all').prop('checked',false);
				}
				$('#merchantManagement').prop('checked',true);				
		}else{
			$('#select_all').prop('checked',false);
			$('.checkbox').prop('checked', false);
			$('#merchantManagement').prop('checked',false);
		}

		
		$('#select_all').on('click',function(){
			if(this.checked){
				$('#merchantManagement').prop('checked',true);
				$('.checkbox').each(function(){
					this.checked = true;
				});
			}else{
				$('#merchantManagement').prop('checked',false);
				 $('.checkbox').each(function(){
					this.checked = false;
					
				});
			}
		});
		$('#merchantManagement').on('click',function(){
			if(this.checked){
				//$('#select_all').prop('checked',true);
				// $('.checkbox').each(function(){
				// 	this.checked = true;
				// });
			}else{
				$('#select_all').prop('checked',false);
				 $('.checkbox').each(function(){
					this.checked = false;
					
				});
			}
		})
		
		
		$('.checkbox').on('click',function(){
			if($('.checkbox:checked').length == $('.checkbox').length){
            $('#select_all').prop('checked',true);
        }else{
            $('#select_all').prop('checked',false);
        }
			var checked_any = false;
			$.each($("input[name='ruleForMerchant']:checked"), function(){
				checked_any = true;
			});
			if(checked_any){
				$('#merchantManagement').prop('checked',true);
			}else{
				$('#merchantManagement').prop('checked',false);
			}
	

		});
		
	});
	</script>
	<script>
		function setMerchantRules(id){
			var payId = id;//document.getElementById("merchant").value;
			var activateMerchantMgmt = document.getElementById("merchantManagement");
 
			//alert(document.getElementById("merchantManagement").checked);
			if (activateMerchantMgmt.checked == true){
			
				activateMerchantMgmt ="true"
				
			} else {
			activateMerchantMgmt = "false"
			}
			//ajax call
			$.ajax({
				type:"POST",
				url :'changemerchantFpsAction',
				data:{
					payId: payId,
					changeMerchant: true,
					token: document.getElementsByName("token")[0].value,
					//activateMerchantMgmt : document.getElementById("merchantManagement").prop('checked' ? true : false)
				},
				success :function(data){
					preferenceSetconst = data.preferenceSet;
					if(preferenceSetconst){
						preferenceSetOBJ = JSON.parse(preferenceSetconst.replace(/&quot;/g,'"'));
						$('.checkbox').prop('checked', false);
						var counter = 0;
						for(index =0;index<preferenceSetOBJ.length;index++){
							$('#'+preferenceSetOBJ[index]).prop('checked', true);
							counter++;
						}
						if(counter == 9){
							$('#select_all').prop('checked',true);
						}
						else{
							$('#select_all').prop('checked',false);
						}
					$('#merchantManagement').prop('checked',true);
					}else{
						$('#select_all').prop('checked',false);
						$('.checkbox').prop('checked', false);
						$('#merchantManagement').prop('checked',false);
					}
				}
			})
		}
		function setRules(){
			var payId = document.getElementById("merchant").value;
			var preferenceSet;
			var preferenceSetOBJ = [];
			
			$.each($("input[name='ruleForMerchant']:checked"), function(){
                preferenceSetOBJ.push(this.id);
            });
			if(preferenceSetOBJ.length != 0){
				preferenceSet = JSON.stringify(preferenceSetOBJ);
			}else{
				preferenceSet = "";
			}
			var activateMerchantMgmt = document.getElementById("merchantManagement");
 
 // alert(document.getElementById("merchantManagement").checked);
  if (activateMerchantMgmt.checked == true){
  
	activateMerchantMgmt ="true"
    
  } else {
	activateMerchantMgmt = "false"
  }
			// var activateMerchantMgmt = document.getElementById("merchantManagement");
			// if($("input[name='activatemanagement']:checked"))
			// {
			// 	activateMerchantMgmt ="true"  
			// } 
			// else{
			// 	activateMerchantMgmt = "false"
			// }
			$.ajax({
		type: "POST",
		dataType: "JSON",
		url: 'changemerchantFpsAccessAction',
		
		data: {
			payId: payId,
			preferenceSet : preferenceSet,
			activateMerchantMgmt : activateMerchantMgmt,
			token: document.getElementsByName("token")[0].value,

		},
		success: function (data) {
			var checked_any = false;
			
			$.each($("input[name='ruleForMerchant']:checked"), function(){
				checked_any = true;
			});
			if(checked_any){
				$('#ruleSet').modal('show');
				
			}else{
				$('#ruleNotSet').modal('show');
				
			}
		}
	});
			
	}</script>
</head>
<body>
	<div id="loader-wrapper" style="width: 100%; height: 100%; display:none;">
		<div id="loader"></div>
	  </div>
	 
		<div class="col-md-12">
			<div class="card ">
			  <div class="card-header card-header-rose card-header-text">
				<div class="card-text">
				  <h4 class="card-title">Merchant FPS Access</h4>
				</div>
			  </div>
			  <div class="card-body ">
				<div class="container">
				  <div class="row">
					 
						<div class="col-sm-6 col-lg-3">
							<label>Select Merchant</label><br>
							<div class="txtnew">
								<div class="txtnew">

									<s:if
								test="%{#session.USER.UserType.name()=='ADMIN' || #session.USER.UserType.name()=='SUBADMIN'  || #session.USER.UserType.name()=='SUBADMIN' || #session.USER_TYPE.name()=='SUPERADMIN'}">
								<s:select name="payId" class="input-control" id="merchant"
									 list="merchantList" onchange="setMerchantRules(this.value)"
									listKey="payId" listValue="businessName" autocomplete="off" />
							</s:if>
							<s:else>
								<s:select name="payId" class="input-control" id="merchant"
									headerKey="" headerValue="" list="merchantList"
									listKey="payId" listValue="businessName" autocomplete="off" />
							</s:else>
									
				
							  </div>
							</div>
						</div>
					
					
				  </div>
				  <!-- <div class="col-md-12"> -->
					<div class="form-check">
						<label class="form-check-label">
						  <input class="form-check-input" id="merchantManagement" name="activatemanagement" type="checkbox" value=""> Activate Merchant Management
						  <span class="form-check-sign">
							<span class="check"></span>
						  </span>
						</label>
					  </div>
					  <div class="col-md-8">
						<div class="card">
						  <div class="card-header card-header-rose card-header-icon">
							<div class="card-icon">
							  <i class="material-icons">assignment</i>
							</div>
							<h4 class="card-title">Rules to Activate</h4>
						  </div>
						  <div class="card-body">
							<div class="table-responsive">
							  <table class="table table-striped">
								<thead>
								  <tr>
									<th class="text-center" style="width:100px">S.No</th>
									<th style="display: flex;"><input class="form-check-input" type="checkbox" id="select_all" value="" style="margin-left:2px;">
										<span class="form-check-sign">
										  <span class="check"></span>
										</span><span style="margin-left:20px;">Select All </span>
										  
										</th>
									<th>Rule Name</th>
									
								  </tr>
								</thead>
								<tbody>
								  <tr>
									<td class="text-center">1</td>
									<td>
									  <div class="form-check">
										<label class="form-check-label">
										  <input class="form-check-input checkbox" name="ruleForMerchant" id="cb1" type="checkbox" value="1" >
										  <span class="form-check-sign">
											<span class="check"></span>
										  </span>
										</label>
									  </div>
									</td>
									<td>Blocked IP Addresses</td>
									
								  </tr>
								  <tr>
									<td class="text-center">2</td>
									<td>
									  <div class="form-check">
										<label class="form-check-label">
										  <input class="form-check-input checkbox" name="ruleForMerchant" id="cb2" type="checkbox" value="2" >
										  <span class="form-check-sign">
											<span class="check"></span>
										  </span>
										</label>
									  </div>
									</td>
									<td>WhiteList IP Addresses</td>
									
								  </tr>
								  <tr>
									<td class="text-center">3</td>
									<td>
									  <div class="form-check">
										<label class="form-check-label">
										  <input class="form-check-input checkbox" name="ruleForMerchant" id="cb3" type="checkbox" value="3" >
										  <span class="form-check-sign">
											<span class="check"></span>
										  </span>
										</label>
									  </div>
									</td>
									<td>Blocked Issuer Countries</td>
									
								  </tr>
								  <tr>
									<td class="text-center">4</td>
									<td>
									  <div class="form-check">
										<label class="form-check-label">
										  <input class="form-check-input checkbox" name="ruleForMerchant" id="cb4" type="checkbox" value="4">
										  <span class="form-check-sign">
											<span class="check"></span>
										  </span>
										</label>
									  </div>
									</td>
									<td>Blocked Email Addresses</td>
									
								  </tr>
								  <tr>
									<td class="text-center">5</td>
									<td>
									  <div class="form-check">
										<label class="form-check-label">
										  <input class="form-check-input checkbox" name="ruleForMerchant" id="cb5" type="checkbox" value="5">
										  <span class="form-check-sign">
											<span class="check"></span>
										  </span>
										</label>
									  </div>
									</td>
									<td>Blocked Transactional Amount</td>
									
								  </tr>
								  <tr>
									<td class="text-center">6</td>
									<td>
									  <div class="form-check">
										<label class="form-check-label">
										  <input class="form-check-input checkbox" name="ruleForMerchant" id="cb6" type="checkbox" value="6">
										  <span class="form-check-sign">
											<span class="check"></span>
										  </span>
										</label>
									  </div>
									</td>
									<td>Blocked Card Ranges</td>
									
								  </tr>
								  <tr>
									<td class="text-center">7</td>
									<td>
									  <div class="form-check">
										<label class="form-check-label">
										  <input class="form-check-input checkbox" name="ruleForMerchant" id="cb7" type="checkbox" value="7">
										  <span class="form-check-sign">
											<span class="check"></span>
										  </span>
										</label>
									  </div>
									</td>
									<td>Blocked Phone Number</td>
									
								  </tr>
								  <tr>
									<td class="text-center">8</td>
									<td>
									  <div class="form-check">
										<label class="form-check-label">
										  <input class="form-check-input checkbox" name="ruleForMerchant" id="cb8" type="checkbox" value="8">
										  <span class="form-check-sign">
											<span class="check"></span>
										  </span>
										</label>
									  </div>
									</td>
									<td>Blocked Card Mask</td>
									
								  </tr>
								  <tr>
									<td class="text-center">9</td>
									<td>
									  <div class="form-check">
										<label class="form-check-label">
										  <input class="form-check-input checkbox" name="ruleForMerchant" id="cb9" type="checkbox" value="9">
										  <span class="form-check-sign">
											<span class="check"></span>
										  </span>
										</label>
									  </div>
									</td>
									<td>Blocked Transaction Amount Velocity</td>
									
								  </tr>
								 
								 
								</tbody>
							  </table>
							  <button type="button" onclick="setRules()" disabled="disabled" id="addId" class="btn btn-success">Submit</button>
							</div>
						  </div>
						</div>
					  </div>
				  </div>
				  <!-- </div> -->
				  
			
			  </div>
			</div>
		  </div>
		
			

	
		<s:hidden name="preferenceSet" value="%{preferenceSet}" id="preferenceSet"></s:hidden>	


 <!--Start Modal for Successfully set the rule-->
 <div class="modal fade" id="ruleSet" role="dialog">
    <div class="modal-dialog">

      <!-- Modal content-->
      <div class="modal-content">
        <div class="modal-header">

        </div>
        <div class="modal-body">
          <p class="enter_otp" id="invaliduser" style="color:green">Rule Permission Set Successfully</p>
        </div>
        <div class="modal-footer" id="modal_footer">
          <button type="button" class="btn btn-primary btn-round mt-4 submit_btn" data-dismiss="modal">Ok</button>
        </div>
      </div>

    </div>
  </div>
  <!--End Modal -->
   <!--Start Modal for Successfully set the rule-->
   <div class="modal fade" id="ruleNotSet" role="dialog">
    <div class="modal-dialog">

      <!-- Modal content-->
      <div class="modal-content">
        <div class="modal-header">

        </div>
        <div class="modal-body">
          <p class="enter_otp" id="invaliduser">Please select atleast one Permission</p>
        </div>
        <div class="modal-footer" id="modal_footer">
          <button type="button" class="btn btn-primary btn-round mt-4 submit_btn" data-dismiss="modal">Ok</button>
        </div>
      </div>

    </div>
  </div>
  <!--End Modal -->

<script type="text/javascript">
	$(document).ready(function() {
		if ($('#merchantFpsAccessAction').hasClass("active")) {
			var menuAccess = document.getElementById("menuAccessByROLE").value;
			var accessMap = JSON.parse(menuAccess);
			var access = accessMap["merchantFpsAccessAction"];
			if (access.includes("Add")) {
				$("#addId").removeAttr("disabled");
			}
		}
	});
</script>
</body>
</html>