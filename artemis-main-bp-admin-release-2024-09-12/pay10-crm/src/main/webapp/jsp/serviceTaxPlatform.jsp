<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="/struts-tags" prefix="s"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Local Tax Rate Platform</title>




	<link rel="shortcut icon" href="assets/media/images/paylogo.svg" />
		<!--begin::Fonts-->
		<link rel="stylesheet" href="https://fonts.googleapis.com/css?family=Inter:300,400,500,600,700" />
		<!--end::Fonts-->
		<!--begin::Vendor Stylesheets(used by this page)-->
		<link href="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.css" rel="stylesheet" type="text/css" />
		<link href="../assets/plugins/custom/datatables/datatables.bundle.css" rel="stylesheet" type="text/css" />

		<!--end::Vendor Stylesheets-->
		<!--begin::Global Stylesheets Bundle(used by all pages)-->
		<link href="../assets/plugins/global/plugins.bundle.css" rel="stylesheet" type="text/css" />
		<link href="../assets/css/style.bundle.css" rel="stylesheet" type="text/css" />
		<link href="../assets/css/custom/custom-style.css" rel="stylesheet" type="text/css" />

		<script src="../assets/plugins/global/plugins.bundle.js"></script>
		<script src="../assets/js/scripts.bundle.js"></script>
		<link href="../css/select2.min.css" rel="stylesheet" />
		<script src="../js/jquery.select2.js" type="text/javascript"></script>
		
<script type="text/javascript">

$(document).ready(function() {	
	
	   $('#businessType').change(function(event){
		//   $('#loader-wrapper').show();
		   var businessType = document.getElementById("businessType").value;
		   

		   if( businessType==null||businessType=="" ){
			   document.getElementById("datatable").style.display="none";
			  return false;
			  
			 }
		   else{
			   document.getElementById("datatable").style.display="block";
			   
		   }
		   document.getElementById("serviceTaxDetailsForm").submit();
		   
	   });
	   
	   var cancelButton = document.getElementById("cancelBtn1");
	   cancelButton.disabled = true;

});

var editMode;

function editCurrentRow(divId,curr_row,ele){
	
	 var cancelButton = document.getElementById("cancelBtn1");
	   cancelButton.disabled = false;
	   
	var div = document.getElementById(divId);

	var table = div.getElementsByTagName("table")[0];

	var businessType = document.getElementById("businessType").value;
	var userType = "<s:property value='%{#session.USER.UserType.name()}'/>";
	var loginEmailId = "<s:property value='%{#session.USER.EmailId}'/>";
	
	var rows = table.rows;
	var currentRowNum = Number(curr_row);
	var currentRow = rows[currentRowNum];
	var cells = currentRow.cells;
	var cell0 = cells[0];
	var cell1 = cells[1];
	var cell2 = cells[2];
	var cell3 =  cells[3];
	
	var cell0Val = cell0.innerText.trim();
	var cell1Val = cell1.innerText.trim();
	var cell2Val = cell2.innerText.trim();
	var cell3Val = cell3.innerText.trim();

	if(ele.value=="Edit"){
		if(editMode) 
		{
				alert('Please edit the current row to proceed');
				return;
		}
		ele.value="save";
		ele.className ="btn btn-success btn-xs";
		cell2.innerHTML = "<input type='number' id='cell2Val'   class='serviceTaxPlatfrom' min='1' step='0.0' value="+cell2Val+"></input>";
		editMode = true;
	}
	else{
		var businessType = cell1Val;
		var serviceTax = document.getElementById('cell2Val').value;
		var status = cell3Val;
		
		
		cell1.innerHTML = businessType;
		cell2.innerHTML = serviceTax;
		cell3.innerHTML = status;
		editMode = false;
		$('#loader-wrapper').show();

		ele.value="Edit";
		ele.className ="btn btn-info btn-xs";
		var stl=parseInt(serviceTax);
		
		
		if(serviceTax=="")
		{

			serviceTax="0.00";
			cell2.innerHTML = serviceTax;
			
		}
		
		if(serviceTax <= -1) {
		
				alert('Please enter valid value, negative value not accepted in service tax');
				window.location.reload();
		
			return;
		}
		
		if(serviceTax=="") {
			
			alert('Please enter valid value, Blank value not accepted in service tax');
            window.location.reload();
	
		  return;
	    }
		var token  = document.getElementsByName("token")[0].value;
		/* document.getElementById("loading").style.display = "block"; */
		$.ajax({
			type: "POST",
			url:"editServiceTax",
			data:{"businessType":cell1Val, "serviceTax":serviceTax, "status":status, "userType":userType, "loginEmailId":loginEmailId, "token":token,"struts.token.name": "token",},
			success:function(data){
		/* document.getElementById("loading").style.display = "none"; */
				var response = ((data["Invalid request"] != null) ? (data["Invalid request"].response[0]) : (data.response));
				if(null!=response){
					alert(response);			
				}
				//TODO....clean values......using script to avoid page refresh

				window.location.reload();
		    },
			error:function(data){
				/* document.getElementById("loading").style.display = "none"; */
				alert("Network error, Local tax rate may not be saved");
			}
		});
	}
}


function cancel(curr_row,ele){
	var parentEle = ele.parentNode;
	
	if(editMode){
		$('#loader-wrapper').show();
	 	window.location.reload();
	}
}


</script>

<!-- search -->
<script type="text/javascript">
	$(document).ready(function () {
	$("#businessType").select2();
		});
</script>
<style>
.product-spec input[type=text] {
	width: 35px;
}
.boxtext{
	margin-bottom: 15px !important;
}
.btn:focus{
		outline: 0 !important;
		
}

#loading {
	width: 100%;
	height: 100%;
	top: 0px;
	left: 0px;
	position: fixed;
	display: block;
	z-index: 99
}

#loading-image {
	position: absolute;
	top: 45%;
	left: 50%;
	z-index: 100;
	width: 15%;
}

</style>
</head>
<body>
<!-- footer div-->
<div class="content d-flex flex-column flex-column-fluid" id="kt_content">
<div class="toolbar" id="kt_toolbar">
							<!--begin::Container-->
							<div id="kt_toolbar_container" class="container-fluid d-flex flex-stack">
								<!--begin::Page title-->
								<div data-kt-swapper="true" data-kt-swapper-mode="prepend" data-kt-swapper-parent="{default: '#kt_content_container', 'lg': '#kt_toolbar_container'}" class="page-title d-flex align-items-center me-3 flex-wrap mb-5 mb-lg-0 lh-1">
									<!--begin::Title-->
									<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">Local Tax Rate</h1>
									<!--end::Title-->
									<!--begin::Separator-->
									<span class="h-20px border-gray-200 border-start mx-4"></span>
									<!--end::Separator-->
									<!--begin::Breadcrumb-->
									<ul class="breadcrumb breadcrumb-separatorless fw-semibold fs-7 my-1">
										<!--begin::Item-->
										<li class="breadcrumb-item text-muted">
											<a href="home" class="text-muted text-hover-primary">Dashboard</a>
										</li>
										<!--end::Item-->
										<!--begin::Item-->
										<li class="breadcrumb-item">
											<span class="bullet bg-gray-200 w-5px h-2px"></span>
										</li>
										<!--end::Item-->
                                        <!--begin::Item-->
										<li class="breadcrumb-item text-muted">Merchant Billing</li>
										<!--end::Item-->
										<!--begin::Item-->
										<li class="breadcrumb-item">
											<span class="bullet bg-gray-200 w-5px h-2px"></span>
										</li>
										<!--end::Item-->
										<!--begin::Item-->
										<li class="breadcrumb-item text-dark">Local Tax Rate</li>
										<!--end::Item-->
									</ul>
									<!--end::Breadcrumb-->
								</div>
								<!--end::Page title-->
								
							</div>
							<!--end::Container-->
						</div>


<!--  <div id="loading" style="text-align: center;">
		<img id="loading-image" style="width:70px;height:70px;" src="../image/sand-clock-loader.gif" alt="Sending SMS..." />
	</div>  --> 

	     <!-- <div id="loader-wrapper" style="width: 70%; height: 70%; display:none;">
		  <div id="loader"></div>
	    </div>  --> 
<s:actionmessage class="error error-new-text" />

	<s:form id="serviceTaxDetailsForm" action="serviceTaxPlatformAction"
		method="post">
		
                <!--begin::Container-->

                <div id="kt_content_container" class="container-xxl">

                    <div class="row my-5">

                        <div class="col">

                            <div class="card">

                                <div class="card-body">
								  <div class="row">
									
										  <div class="col-sm-6 col-lg-3">
											<label style="float: left;">Select Industry : </label>
											<br><br>
											<div class="txtnew">
												<s:select headerKey=""
											headerValue="Select Industry" name="businessType" 
								id="businessType" list="industryCategoryList" class="form-select form-select-solid" autocomplete="off" style="margin-left:-3% !important;" />
											</div>
										  </div>
										 
									 
									 
									
									
									
								  </div>
								  </div>
								  </div>
								  </div>
								  </div>
								  </div>
								  
							
							 
							
						<!-- <tr>
							<td align="left">
								<div class="container">
									<div class="form-group col-md-4 txtnew col-sm-4 col-xs-6">
									
									<s:select headerKey=""
											headerValue="Select Industry" name="businessType" 
								id="businessType" list="industryCategoryList" class="form-control" autocomplete="off" style="margin-left:-3% !important;" />
								</div>
								</div>
							</td>
						</tr> -->
						
						
					 <div id="kt_content_container" class="container-xxl">

                    <div class="row my-5">

                        <div class="col">

                            <div class="card">

                                <div class="card-body">
								  <div class="row">
								  
							<td align="left"><div id="datatable" class="">
									<s:iterator value="serviceTaxData" status="pay">
										<br>
										<span class="text-primary" id = "test">
										<strong>
										<s:property
												value="key" />
										</strong>
										</span> 
										<br>
										<div class="scrollD">
											<div id="<s:property value="key" />Div">
												<table width="100%" border="0" align="center"
													class="product-spec">
													<tr class="boxheading">
														<th width="5%" height="25" valign="middle"
															style="display: none">Payment</th>
														<th width="4%" align="left" valign="middle">Industry Type</th>	
														<th width="6%" align="left" valign="middle">Local Tax Rate</th>
														<th width="4%" align="left" valign="middle">Status</th>
															 <th width="5%" align="left" valign="middle">Update</th>
														<th width="2%" align="left" valign="middle"
															style="display: none">id</th>
														<th width="5%" align="left" valign="middle"><span
															id="cancelLabel">Cancel</span></th>
													</tr>
													<s:iterator value="value" status="itStatus">
														<tr class="boxtext">
															<td align="left" valign="middle" style="display: none"><s:property
																	value="businessType" /></td>
															<td align="left" valign="middle"><s:property
																	value="businessType" /></td>
															<td align="left" valign="middle"><s:property
																	value="serviceTax" /></td>
																<td align="left" valign="middle"><s:property
																	value="status" /></td>
																
																	<td align="left" valign="middle"><div>
																	<s:textfield id="edit%{#itStatus.count}" name="editServiceTax" value="Edit"
																		type="button"
																		onclick="editCurrentRow('%{key +'Div'}','%{#itStatus.count}', this)"
																		class="btn btn-primary" disabled="true" autocomplete="off"></s:textfield>

																	<%-- <s:textfield id="cancelBtn%{#itStatus.count}"
																		value="Cancel" type="button"
																		onclick="cancel('%{#itStatus.count}',this)"
																		style="display:none" autocomplete="off"></s:textfield> --%>
																</div></td>
															<td align="center" valign="middle" style="display: none"><s:property
																	value="id" /></td>
															<td align="left" valign="middle"><s:textfield
																	id="cancelBtn%{#itStatus.count}" value="Cancel"
																	type="button"
																	onclick="cancel('%{#itStatus.count}',this)"
																	class="btn btn-primary btn-xs" autocomplete="off"></s:textfield></td>
														</tr>
													</s:iterator>
												</table>
											</div>
										</div>
									</s:iterator>
								</div>
								</td>
								
								</div>
								</div>
								</div>
								</div>
								</div>
								</div>
								
															
						
						
					
		
		</div>
		<s:hidden name="token" value="%{#session.customToken}"></s:hidden>
		
	</s:form>
	<script type="text/javascript">
	$(document).ready(function() {
		if (window.location.href.includes("serviceTaxPlatformAction")) {
			var menuAccess = document.getElementById("menuAccessByROLE").value;
			var accessMap = JSON.parse(menuAccess);
			var access = accessMap["serviceTaxPlatform"];
			if (access.includes("Update")) {
				var editBtn = document.getElementsByName("editServiceTax")[0];
				editBtn.disabled=false;
			}
		}
	});
	</script>
</body>
</html>