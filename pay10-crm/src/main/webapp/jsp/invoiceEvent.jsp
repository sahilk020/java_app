<!DOCTYPE html
	PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@taglib prefix="s" uri="/struts-tags"%>
<html>

<head>
<title>Invoice Event</title>
<!-- <link href="../assets/css/default.css" rel="stylesheet" type="text/css" />
<link href="../fonts/css/font-awesome.min.css" rel="stylesheet" />
<script type="text/javascript" src="../assets/js/jquery.min.js"></script>

<script src="../js/core/popper.min.js"></script>
<script src="../js/core/bootstrap-material-design.min.js"></script>
<script src="../js/plugins/perfect-scrollbar.jquery.min.js"></script>
<!-- Control Center for Material Dashboard: parallax effects, scripts for the example pages etc -->
<!-- <script src="../js/material-dashboard.js?v=2.1.0" type="text/javascript"></script>
<script src="../js/jquery.dataTables.js"></script>
<script type="text/javascript" src="../js/dataTables.buttons.js"></script>
<script src="../js/commanValidate.js"></script>

<link href="../css/select2.min.css" rel="stylesheet" />
<script src="../js/jquery.select2.js" type="text/javascript"></script>
<script src="../js/promotionalInvoice.js" type="text/javascript"></script>

<link href="../css/invoice.css" rel="stylesheet" /> -->

<link
	href="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.css"
	rel="stylesheet" type="text/css" />
<link href="../assets/plugins/custom/datatables/datatables.bundle.css"
	rel="stylesheet" type="text/css" />
<link href="../assets/plugins/global/plugins.bundle.css"
	rel="stylesheet" type="text/css" />
<link href="../assets/css/style.bundle.css" rel="stylesheet"
	type="text/css" />
<link href="../assets/css/custom/custom-style.css" rel="stylesheet"
	type="text/css" />

<script src="../assets/plugins/global/plugins.bundle.js"></script>
<script src="../assets/js/scripts.bundle.js"></script>
<script
	src="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.js"></script>
<script src="../assets/plugins/custom/datatables/datatables.bundle.js"></script>
<script src="../assets/js/widgets.bundle.js"></script>
<script src="../assets/js/custom/widgets.js"></script>
<script src="../assets/js/custom/apps/chat/chat.js"></script>
<script src="../assets/js/custom/utilities/modals/upgrade-plan.js"></script>
<script src="../assets/js/custom/utilities/modals/create-app.js"></script>
<script src="../assets/js/custom/utilities/modals/users-search.js"></script>
<script src="../js/promotionalInvoice.js" type="text/javascript"></script>
<style>
div#message {
	/* border: 2px solid green; */
	padding: 12px 30px 0px 30px;
	border-radius: 10px;
}

#mymsg{
    color: black;
    font-size: large;
    margin-bottom: 15px;
    }
/* #myid {
	margin-bottom: 15px;
} */
#message ul li{
 list-style: none !important;
}
.error{
color:red;
}
.dt-buttons.btn-group.flex-wrap {
    display: none;
}
</style>
<script type="text/javascript">
$(document).ready(function() {
	checkedState();
	//generateInvoiceNum();
	$('#aa').dataTable({
		dom: 'Brtipl',
		
				
	"scrollY": true,
	"scrollX": true,	
	
});
});
</script>

<script type="text/javascript">

	$(document).ready(function() {
		
		$("#tnc").change(function() {
			var ttnncc = document.getElementById("tnc").checked;
			
			if (ttnncc == true) {
				//$("#btnSave").prop('disabled', false);
				$("#save_promotional").show();
			} else {
				//$("#btnSave").prop('disabled', true);
				$("#save_promotional").hide();
			}

		});


		var ttnnccread = document.getElementById("tnc").checked;
	
		disableInvoiceNo(ttnnccread, true);
		
	});
</script>

<script type="text/javascript">
			debugger
			$(document).ready(function () {
				$('[data-toggle="tooltip"]').tooltip();	
				document.getElementById('crBtn').style = "display:none";
				if (window.location.pathname.substr(9, window.location.pathname.length) == "savePromotionalInvoice") {
					debugger
					//var urlCopyLink = $('#promoLink').val();
					var ArrStr = '<s:property value="actionMessages"/>';
					if (ArrStr == 'undefined' || ArrStr == null || ArrStr == '' || ArrStr == "[]") {
							$("#myid").hide();
					} else {
							$("#myid").show();
					}
					
				} else {
					//var urlCopyLink = $('#promoLink').val();
					var ArrStr = '<s:property value="actionMessages"/>';
					if (ArrStr == 'undefined' || ArrStr == null || ArrStr == '' || ArrStr == "[]") {
					    $("#myid").hide();
					} else {
							$("#myid").show();
					}
					
				}
				$('#aa').dataTable({
					dom: 'Brtipl',
					
							
				"scrollY": true,
				"scrollX": true,	
				
			});
				
			});

			
			var file = "";
			function getfilename(event) {
				debugger
				file = event;
				if (event.accept == '.csv') {
					document.getElementById('filename').innerHTML = event.files[0].name;
					document.getElementById('filename').style.fontSize = '13px';
				}
				
			}
			function download() {
                var csv = 'CUST_NAME,CUST_MOBILE,CUST_EMAIL,AMOUNT,PRODUCT NAME,QUANTITY,GST,CURRENCY,PAYMENT REGION \n \n SAMPLE DATA EXAMPLE \n \n Full Name, XXXXXXXXXX,xyz@gmail.com,1000 or 100.00,Product Name,10,0 or 18,INR,DOMESTIC';				var hiddenElement = document.createElement('a');
				hiddenElement.href = 'data:text/csv;charset=utf-8,' + encodeURI(csv);
				hiddenElement.target = '_blank';
				hiddenElement.download = 'Invoice Event.csv';
				hiddenElement.click();
			}
			
		</script>

</head>
<body>
	<div class="content d-flex flex-column flex-column-fluid"
		id="kt_content"  style="overflow: scroll !important;">
		
		<!--begin::Toolbar-->
		<div class="toolbar" id="kt_toolbar">
			<!--begin::Container-->
			<div id="kt_toolbar_container"
				class="container-fluid d-flex flex-stack">
				<!--begin::Page title-->
				<div data-kt-swapper="true" data-kt-swapper-mode="prepend"
					data-kt-swapper-parent="{default: '#kt_content_container', 'lg': '#kt_toolbar_container'}"
					class="page-title d-flex align-items-center me-3 flex-wrap mb-5 mb-lg-0 lh-1">
					<!--begin::Title-->
					<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">
						Promotional Invoice</h1>
					<!--end::Title-->
					<!--begin::Separator-->
					<span class="h-20px border-gray-200 border-start mx-4"></span>
					<!--end::Separator-->
					<!--begin::Breadcrumb-->
					<ul
						class="breadcrumb breadcrumb-separatorless fw-semibold fs-7 my-1">
						<!--begin::Item-->
						<li class="breadcrumb-item text-muted"><a href="home"
							class="text-muted text-hover-primary">Dashboard</a></li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item"><span
							class="bullet bg-gray-200 w-5px h-2px"></span></li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item text-muted">Quick Pay Invoice</li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item"><span
							class="bullet bg-gray-200 w-5px h-2px"></span></li>
						<!--end::Item-->
						<!--begin::Item-->
						<li class="breadcrumb-item text-dark">Promotional Invoice</li>
						<!--end::Item-->
					</ul>
					<!--end::Breadcrumb-->
				</div>
				<!--end::Page title-->
			</div>
			<!--end::Container-->
		</div>
		<!--end::Toolbar-->
		<!--begin::Post-->
		<div class="post d-flex flex-column-fluid" id="kt_post">
			<!--begin::Container-->
			<div id="kt_content_container" class="container-xxl">
				<form name="f1" action="savePromotionalInvoice" id='frmInvoice'
					class="form mb-15" method="post" enctype="multipart/form-data"
					autocomplete="off">
					<div class="row my-5">
						<div class="col">
							<div class="card">
								<div class="card-body">
									<div class="row my-3 align-items-center">
										<div id="myid">
										
									</div>
									<div id="mymsg">
									<div id="message" colspan="3" align="center">
											<s:actionmessage id="msg" />
										</div>
									</div>
										<s:if
											test="%{#session.USER.UserType.name()=='ADMIN' || #session.USER.UserType.name()=='SUBADMIN'  || #session.USER.UserType.name()=='SUBADMIN' || #session.USER_TYPE.name()=='SUPERADMIN'}">
											<div class="col-auto my-2 merchant-text">
												<p class="text-center m-0 w-100">
													<b>Merchant</b>
												</p class="text-sm-center m-0">
											</div>
											<div class="col-md-3 fv-row">
												<s:select name="payId"  data-control="select2" onchange="javascript: $('#invoiceNo').val('');checkedState();"
													class="form-select form-select-solid"
													id="merchantPayId" list="merchantList" listKey="payId"
													listValue="businessName" autocomplete="off" />
											</div>
										</s:if>
										<s:else>
										<s:select name="payId" class="input-control"
											id="merchantPayId" list="merchantList" listKey="payId"
											listValue="businessName" autocomplete="off"
											style="display: none;" />
											</s:else>
									</div>
									
									<input type="hidden" id="myPayId"/>
								
									<div class="row">
										<div class="col-12">
											<span class="fw-semibold text-gray-600 fs-6 mb-8 d-block">
												File should be in csv format<br> File size must not
													exceed 2MB<br>File name length should be 5-50
														characters.<br> File should contain 1-10000 records<br>
																File name can contain alphabets, digits, round brackets,
																hyphen,underscore, dot & space 
											</span>
										</div>
									</div>
									<div class="row mb-4 align-items-start"
										style="column-gap: 20px;">
										<div class="col-lg-3 col-md-5 col-sm-12 my-2 file-group p-0 ">
											<div class="file-input">
												<s:file name="fileName" id="file-input" accept=".csv"
													class="file-input__input" onchange="getfilename(this)" />
												 <label
													class="file-input__label" for="file-input"> <img
													src="../assets/media/images/folder-svg.svg" alt=""> <span
														class="m-0 blackspan" id="filename"></span> <span>Browse</span></label>
											</div>
											<span id="fileCSVErr" class="error"></span>
										</div>
										<div class="col-lg-3 col-md-5 col-sm-12 p-0"
											onclick="download()">
											<a type="button" id="csvdownload"
												class="btn-hover-rise my-2 download-btn "> <span
												class="bluespan">CSV</span> <span class="blackspan">Sample
													csv file</span>
											</a>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
					<div class="row my-5">
						<div class="col">
							
							
						</div>
					</div>
					<div class="row my-5">
						<div class="col">
							
							<div class="card">
								<div class="card-body">
									<!--begin::Input group-->
									
									<div class="row g-9 mb-8">
										<!--begin::Col-->
										<div class="col-md-4 fv-row">
											<label
												class="d-flex align-items-center fs-6 fw-semibold mb-2">
												<span class="required">Expire in Hours</span>
											</label>
											<!--end::Label-->
											<s:textfield type="text"
												class="form-control form-control-solid" id="proExpiresHour"
												name="expiresHour" autocomplete="off"
												value="%{invoice.expiresHour}" minlength="1" maxlength="2"
												onkeypress="return isNumberKey(event)"
												/>
												<span id="expiryHrs" class="error"></span>
										</div> 
										
									</div>
									
									<div class="row g-9 mb-8">
										
										<div class="col-md-6 fv-row" style="display: none;">
											
										</div>
									</div>

									<div class="row g-9 mb-8 mt-4" style="display: block;">
										
									</div>
									<div class="row g-9 mb-8 justify-content-md-center">
										
										<!-- Added by Sweety -->
										<div id="abc">
										 
											<div style="display: inline-flex; margin-left: 19px;"><s:checkbox
													type="checkbox" name="merchantConsent" id="tnc"
													onclick="javascript: disableInvoiceNo(this.checked);"
													value="%{merchantConsent}"></s:checkbox>&nbsp; I accept all
												&nbsp;<a onclick="modalShow();">
													Terms and conditions*</a></div>
										
										</div>
										<div
											class=" col-md-12 fv-row d-flex justify-content-md-center">
											<button type="button" id="save_promotional"
												class="btn w-lg-25 w-md-25 w-25 btn-primary">Save & Send</button>
										</div>
									</div>
									<s:hidden id="invoiceType" name="invoiceType"></s:hidden>
									<s:hidden name="token" value="%{#session.customToken}"></s:hidden>
									<s:token name="requestToken" />

								</div>
							</div>
						</div>
					</div>
				</form>
				<div class="row my-5">
					<div class="col">
						<div class="card">
							<div class="card-body">
								<div class="row g-9 mb-8">


									<table id="aa"
										class="table table-striped table-row-bordered gy-5 gs-7 dataTable no-footer">
										<thead>
											<tr class="fw-bold fs-6 text-gray-800">
												<th scope="col">NAME</th>
												<th scope="col">MOBILE</th>
												<th scope="col">EMAIL</th>
												<th scope="col">AMOUNT</th>
												<th scope="col">PRODUCT NAME</th>
												<th scope="col">QUANTITY</th>
												<th scope="col">GST</th>
												<th class="col-md-2">STATUS</th>
											</tr>
										</thead>
										<tbody>
											<s:iterator value="listInvoices">

												<tr>
													<td scope="col"><s:property value="name" /></td>
													<td scope="col"><s:property value="phone" /></td>
													<td scope="col"><s:property value="email" /></td>
													<td scope="col"><s:property value="amount" /></td>
													<td scope="col"><s:property value="productName" /></td>
													<td scope="col"><s:property value="quantity" /></td>
													<td scope="col"><s:property value="gst" /></td>
													<td class="col-md-2"><s:property value="status" /></td>
												</tr>
											</s:iterator>
										</tbody>
									</table>

								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
			
			<!--end::Container-->
		</div>
	
		
		
		<!--end::Post-->
	</div>
	<script>
			$(document).on('change', '.btn-file :file',
				function () {
					var input = $(this), numFiles = input.get(0).files ? input
						.get(0).files.length : 1, label = input.val()
							.replace(/\\/g, '/').replace(/.*\//, '');
					input.trigger('fileselect', [numFiles, label]);
				});

			$(document).ready(function () {
				$('.btn-file :file').on('fileselect',
					function (event, numFiles, label) {
						var input = $(this).parents('.input-group').find(':text'), log = numFiles > 1 ? numFiles + ' files selected' : label;
						if (input.length) {
							input.val(log);
						} else {
							if (log)
								alert(log);
						}
					});
			});
		</script>

		<script>
	
			function isNumberKey(evt) {
				var charCode = (evt.which) ? evt.which : event.keyCode
				if (charCode > 31 && (charCode < 48 || charCode > 57))
					return false;
				return true;
			}
			
		</script>
	
	<!-- End New Validation code here -->

<script>
function checkedState() {
debugger
	var payid = $("#merchantPayId").val();
	var invoiceNo=$("#invoiceNo").val();

	  $.ajax({

	    type: "POST",
	    url: "promotionalInvoiceMessage",

	    data: {
	    	"payid" : payid
	    },
	    success: function(result) {
		    
	     if (result.tncStatus==true) {
			$("#abc").show();
			$("#save_promotional").hide();
		} else {
			$("#abc").hide();
			$("#save_promotional").show();
			
		}
		if (result.invoiceMessage != null
				&& result.invoiceMessage != "") {
			$("#messageText").text(result.invoiceMessage);
			$("#messageText1").text(result.invoiceMessage);
			var test=$("#messageText").text();
			test = test.replace("#invoiceNo", invoiceNo);
	        $("#messageText").text(test);
			
		} else {
			$("#messageText").text(
					"There is no Terms and condition");
		}

	    }
	  });
	}
function generateInvoiceNum(){
	var invoiceNo=$("#invoiceNo").val();
	if (invoiceNo && invoiceNo !== null && invoiceNo !== '') {
		return;
	}
	var payid = $("#merchantPayId").val();
 	var date=new Date();
 date= date.getMilliseconds();
	$.post("getInvoiceNumber", {
		payid : payid,
	}, function(result) {
	  var businessName=result.businessName;
	  var invoiceNumber= businessName.concat("-pay10","-",date);
	  document.getElementById("invoiceNo").value=invoiceNumber;
	  $('#invoiceNo').attr('readonly', true);
	});
}
</script>

<div class="modal" id="myModal" role="dialog">
	<input type="hidden" id="messageText1"/>
		<div class="modal-dialog">

			<!-- Modal content-->
			<div class="modal-content">
				<div class="modal-header">
					<button type="button" class="close" data-dismiss="modal" onclick="hideModal();">&times;</button>
					<h4 class="modal-title">Terms & Conditions for Invoice Payment</h4>
				</div>
				<div class="modal-body">
					<p id="messageText"></p>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal" onclick="hideModal();">Close</button>
				</div>
			</div>

		</div>
	</div>
	<script>
function modalShow(){
	 $('#myModal').show();
	 } 

	 function hideModal(){
	 $('#myModal').hide();	
	 }
</script>

<script>
$(document).ready(function(){
	
	$("#save_promotional").click(function(){

		var text;

		  if (confirm("Are you sure you want to upload this file.") == true) {

			  var file=$('#file-input').val();
				var expirehours=$("#proExpiresHour").val();
				if(file==''||file==null){
					document.getElementById('filename').innerHTML = '';
					 $("#fileCSVErr").text("Please upload csv file for invoice generation");
					  setTimeout(() => {
						  $("#fileCSVErr").text("");
						  }, 3500);
					
				}else if(expirehours==0||expirehours<0){
					  $("#expiryHrs").text("Please enter Hours of expiry");
						
					  setTimeout(() => {
						  $("#expiryHrs").text("");
						  }, 3500);
						
				}else{
					$("#mymsg").show();
					$("#frmInvoice").submit();
				}

			   
		  }else{
			alert("You press cancel button");
			}
		 
		
		
	});	
});

</script>
</body>

</html>