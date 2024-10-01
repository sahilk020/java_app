<!-- author shubhamchauhan -->

<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Create Chargeback</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="refresh" content="905;url=redirectLogin" />
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<meta name="viewport"
	content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
<title>Merchant Mapping</title>
<!-- <link href="../css/default.css" rel="stylesheet" type="text/css" />
<link rel="stylesheet" type="text/css" media="all"
	href="../css/daterangepicker-bs3.css" />
<link href="../css/Jquerydatatableview.css" rel="stylesheet" />
<link href="../css/Jquerydatatable.css" rel="stylesheet" /> -->

<!-- <link href="../css/jquery-ui.css" rel="stylesheet" /> -->
<script src="../js/jquery.min.js"></script>
<script src="../js/core/popper.min.js"></script>
<script src="../js/core/bootstrap-material-design.min.js"></script>
<script src="../js/plugins/perfect-scrollbar.jquery.min.js"></script>
<!-- Control Center for Material Dashboard: parallax effects, scripts for the example pages etc -->
<script src="../js/material-dashboard.js?v=2.1.0" type="text/javascript"></script>
<script src="../js/jquery.dataTables.js"></script>
<script src="../js/jquery-ui.js"></script>
<script src="../js/commanValidate.js"></script>
<script type="text/javascript" src="../js/moment.js"></script>
<script type="text/javascript" src="../js/daterangepicker.js"></script>
<script src="../js/jquery.popupoverlay.js"></script>
<script type="text/javascript" src="../js/dataTables.buttons.js"></script>
<script type="text/javascript" src="../js/pdfmake.js"></script>
<script src="../js/jszip.min.js" type="text/javascript"></script>
<script src="../js/vfs_fonts.js" type="text/javascript"></script>
<script src="../js/buttons.colVis.min.js" type="text/javascript"></script>
<script type="text/javascript" src="../js/summaryReport.js"></script>
<!-- <link href="../css/loader.css" rel="stylesheet" type="text/css" /> -->
<script src="../js/select2.min.js"></script>
<!-- <link href="../css/select2.min.css" rel="stylesheet" /> -->
<script src="../js/jquery.select2.js" type="text/javascript"></script>

<link href="../assets/plugins/custom/fullcalendar/fullcalendar.bundle.css" rel="stylesheet" type="text/css" />
<link href="../assets/plugins/custom/datatables/datatables.bundle.css" rel="stylesheet" type="text/css" />
<link href="../assets/plugins/global/plugins.bundle.css" rel="stylesheet" type="text/css" />
<link href="../assets/css/style.bundle.css" rel="stylesheet" type="text/css" />
<link href="../assets/css/custom/custom-style.css" rel="stylesheet" type="text/css" />

<!-- Chargeback helper files -->
<link href="../css/chargeback.css" rel="stylesheet">
<script type="text/javascript" src="../js/chargeback.js"></script>
<script src="../assets/js/scripts.bundle.js"></script>
<script type="text/javascript">
	$(document).ready(function() {
		setInterval(function(){ getChargebackTempChat() }, 5000);
		$(function() {
			$("#dateFrom").datepicker({
				prevText : "click for previous months",
				nextText : "click for next months",
				showOtherMonths : true,
				dateFormat : 'dd-mm-yy',
				selectOtherMonths : true,
				maxDate : new Date()
			});
			$("#dateTo").datepicker({
				prevText : "click for previous months",
				nextText : "click for next months",
				showOtherMonths : true,
				dateFormat : 'dd-mm-yy',
				selectOtherMonths : false,
				beforeShow: function() {
        $(this).datepicker('option', 'minDate', $('#dateFrom').val());
        if ($('#dateFrom').val() === '') $(this).datepicker('option', 'minDate', 0);                             
     }
			});
			
			
		});

		$(function() {
			var today = new Date();
			$('#dateTo').val($.datepicker.formatDate('dd-mm-yy', today));
			$('#dateFrom').val($.datepicker.formatDate('dd-mm-yy', today));
			//statisticsAction();
		});

		$("#cbFile").on('change', function() {
			let txnIndex = $('.isRadioButtonClicked').parent().parent().index();
			let payId = $('.cbpayid').eq(txnIndex).html();
			if(payId == undefined || txnIndex == -1){
				alert("Select any transaction");
				 $("#cbFile").val('');
				return false;
			}
			
			let fileSize = ($("#cbFile")[0].files[0].size / (1024 * 1024));
			if(fileSize > 10){
				$("#cbFile").val('');
				alert("Invalid File size");
				return;
			}
			
			let completeFileName = $('#cbFile').val();
			let fileNameArr = completeFileName.split("\\");
			let fileName = fileNameArr[fileNameArr.length -1];	
			console.log(fileName);
			
			let isValidFileName = validateFileName(fileName);
			let isValidFileExtensionPdf = validatePdfFileExtension(fileName);
			let isValidFileExtensionJpeg = validateJpegFileExtension(fileName);
			let isValidFileExtensionJpg = validateJpgFileExtension(fileName);
			let isValidFileExtensionPng = validatePngFileExtension(fileName);
			
			console.log("Is valid file name : " + isValidFileName);
			
			if(!(fileName.length >=1 && fileName.length <= 50)){
				alert("Invalid Filename length.");
				return;
			}
			
			if(!isValidFileName){
				alert("Invalid FileName.");
				return;
			}
			
			if(!isValidFileExtensionPdf  && !isValidFileExtensionJpeg  && !isValidFileExtensionJpg  && !isValidFileExtensionPng){
				alert("Invalid File extension.");
				return;
			}
			
			$('#cbFileUploadTimeStamp').val(getCurrentTimeStamp());
			$('#cbPayId').val(payId);
		    document.getElementById("saleform").submit();
		    var content=$("iframe").contents().find('body').html();

		    $("#cbFile").val('');
		    getChargebackTempChat();
		  });
	});
	
	
</script>
</head>
<body>
	<input type="hidden" value="<s:property value='%{#session.USER.firstName}'/>" id="cbUsername">
	<input type="hidden" value="<s:property value='%{#session.USER.UserType.name()}'/>" id="cbUserType">
	
	<div class="toolbar" id="kt_toolbar">
		<!--begin::Container-->
		<div id="kt_toolbar_container" class="container-fluid d-flex flex-stack">
			<!--begin::Page title-->
			<div data-kt-swapper="true" data-kt-swapper-mode="prepend" data-kt-swapper-parent="{default: '#kt_content_container', 'lg': '#kt_toolbar_container'}" class="page-title d-flex align-items-center me-3 flex-wrap mb-5 mb-lg-0 lh-1">
				<!--begin::Title-->
				<h1 class="d-flex align-items-center text-dark fw-bold my-1 fs-3">Create Chargeback</h1>
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
					<li class="breadcrumb-item text-muted">Chargeback</li>
					<!--end::Item-->
					<!--begin::Item-->
					<li class="breadcrumb-item">
						<span class="bullet bg-gray-200 w-5px h-2px"></span>
					</li>
					<!--end::Item-->
					<!--begin::Item-->
					<li class="breadcrumb-item text-dark">Create Chargeback</li>
					<!--end::Item-->
				</ul>
				<!--end::Breadcrumb-->
			</div>
			<!--end::Page title-->
			
		</div>
		<!--end::Container-->
	</div>
	<div class="content flex-column" id="kt_content">
	<div class="chargebackMainDiv">
		<div class="chargebackHeader">
			<div class="searchTransactionDiv">
				<div class="col-md-12">
					<div class="card ">
						<div class="card-header card-header-rose card-header-text">
							<div class="card-text">
								<!-- <h4 class="card-title">Search Transaction</h4> -->
							</div>
						</div>
						<div class="card-body ">
							<div class="container">
								<div class="row">

									<div class="col-sm-6 col-lg-3">
										<label class="d-flex align-items-center fs-6 fw-semibold mb-2">PG REF NUM</label><br>
										<div class="txtnew">
											<s:textfield id="chargebackPgRefNum" class="form-control form-control-solid"
												name="pgRefNum" type="text" value="" autocomplete="off"
												onkeypress="javascript:return isNumber (event)" maxlength="16" onblur="validateSearchButton()" ></s:textfield>
										</div>
										<span id="validRefNo" style="color:red; display:none;">Please Enter 16 Digit PG Ref No.</span>
										<span id="validValue" style="color:red; display:none;">Please Enter Valid PG Ref No.</span>
									</div>
									<div class="col-sm-6 col-lg-3">
										<label class="d-flex align-items-center fs-6 fw-semibold mb-2">ACQ ID</label><br>
										<div class="txtnew">
										<s:textfield id="chargebackAcqId" class="form-control form-control-solid" name="chargebackAcqId"
												type="text" value="" autocomplete="off"
												onkeypress="return isAlphaNumeric(event);" onblur="validateSearchButton()"></s:textfield>
										</div>
										<span id="validAcqId" style="color:red; display:none;">Please Enter 16 Digit PG Ref No.</span>
										<span id="validAcqValue" style="color:red; display:none;">Please Enter Valid Acq ID.</span>
									</div>
									<div class="col-sm-6 col-lg-3">
										<label class="d-flex align-items-center fs-6 fw-semibold mb-2">Order Id</label><br>
										<div class="txtnew">
											<s:textfield id="chargebackOrderId" class="form-control form-control-solid" name="orderId"
												type="text" value="" autocomplete="off"
												onkeypress="return orderIdCharacters(event);" onblur="validateSearchButton()"
												></s:textfield>
										</div>
										<span id="validOrderId" style="color:red; display:none;">Please Enter Valid Order ID.</span>
										<span id="validOrderValue" style="color:red; display:none;">Please Enter Valid Order ID.</span>
									</div>
									<div class="col-sm-6 col-lg-3">
										<label class="d-flex align-items-center fs-6 fw-semibold mb-2">Customer Email Id</label><br>
										<div class="txtnew">
											<s:textfield id="chargebackEmailId" class="form-control form-control-solid"
												name="customerEmail" type="text" value="" autocomplete="off"
												onblur="validateSearchButton();"></s:textfield>
										</div>
										<span id="validEmailId" style="color:red; display:none;">Please Enter Valid Email ID.</span>
										<span id="validEmailValue" style="color:red; display:none;">Please Enter Valid Email ID.</span>
									</div>

									<div class="col-sm-6 col-lg-3" style="display: inline-flex;">
										<div class="txtnew" style="margin-right: 15px;">
											<input type="button" id="chargebackSearchButton"
												value="Search" class="btn btn-primary  mt-4 submit_btn">
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
		<div class="chargebackSearchTableDiv" id="chargebackContentColor">
			<div class="scrollD">
				<div class="row g-9 mb-8">
					<div class="table-responsive dataTables_wrapper dt-bootstrap4 no-footer">
				<table class="table table-responsive table table-striped table-row-bordered gy-5 gs-7" cellspacing="0" width="100%" style="text-align: center;">
					<thead>
						<tr id="chargebackSearchTableTheadTr" class="boxheadingsmall fw-bold fs-6 text-gray-800">
							<th customindex = "0">&nbsp&nbsp&nbsp</th>
							<th>PG REF NUM</th>
							<th>Merchant Name</th>
							<th>Merchant Id</th>
							<!-- Ask for the position in table -->
							<th>Order Id</th>
							<th>Acquirer Id</th>
							<th>Txn Status</th>
							<th>Customer Email Id</th>
							<th>Customer Phone</th>
							<th>Merchant Amount</th>
							<th class="hideElement">Transaction Id</th>
							<th class="hideElement">Transaction Date</th>
							<th class="hideElement">PG TDR SC</th>
							<th class="hideElement" scope="col">Acquirer TDR SC</th>
							<th class="hideElement" scope="col">PG GST</th>
							<th class="hideElement" scope="col">Acquirer GST</th>
							<th class="hideElement">Currency</th>
							<th class="hideElement" scope="col">Payment Type</th>
							<th class="hideElement">MOP</th>
							<th class="hideElement" scope="col">Case Id</th>
							<th class="hideElement" scope="col">Card Mask Number</th>
							<th class="hideElement">ResMsg</th>
							<th class="hideElement">ResCod</th>
							<th class="hideElement" scope="col">Card Issuer Bank</th>
							<th class="hideElement" scope="col">Card Issuer Country</th>
							<th class="hideElement" scope="col">Customer Country</th>
							<th class="hideElement">Customer IP</th>
							
						</tr>
					</thead>
					<tbody id="chargebackSearchTableTBody">
					</tbody>
				</table>
				</div>
				</div>
			</div>
		</div>
		<div class="col-md-12">
			<div class="card ">
				<div class="card-header card-header-rose card-header-text">
					<div class="card-text">
						<br>
						<h4 class="card-title">Chargeback Type Details</h4>
					</div>
				</div>
				<div class="card-body ">
					<div class="container">
						<div class="row">

							<div class="col-sm-6 col-lg-3">
								<label class="d-flex align-items-center fs-6 fw-semibold mb-2">Chargeback Type</label><br>
								<div class="txtnew">
									<select id="chargebackType" class="form-control form-control-solid">
										<option value="Charge Back">Charge Back</option>
										<option value="Pre Arbitration">Pre Arbitration</option>
										<option value="Fraud Disputes">Fraud Disputes</option>
									</select>
								</div>

							</div>
							<div class="col-sm-6 col-lg-3">
								<label for="chargebackCreationDate" class="d-flex align-items-center fs-6 fw-semibold mb-2">Creation Date</label><br>

								<div class="txtnew">
									<input type="text" id="dateFrom" name="dateFrom"
										class="form-control form-control-solid" autocomplete="off" readonly="true" />
								</div>
							</div>
							<div class="col-sm-6 col-lg-3">
								<label for="chargebackTargetDate" class="d-flex align-items-center fs-6 fw-semibold mb-2">Due Date</label><br>
								<div class="txtnew">
									<input type="text" id="dateTo" name="dateTo"
										class="form-control form-control-solid" autocomplete="off" readonly="true" />
								</div>
							</div>

							<div class="col-sm-6 col-lg-3">
								<label for="chargebackTargetDate" class="d-flex align-items-center fs-6 fw-semibold mb-2">Case ID</label><br>
								<div class="txtnew">
									<s:textfield id="chargebackCaseId" class="form-control form-control-solid" name="chargebackCaseId"
										type="text" value="" autocomplete="off"		onkeypress="return orderIdCharacters(event);" onblur="validateSearchButton()"></s:textfield>
								</div>
								<span id="validCaseId" style="color:red; display:none;">Please Enter Valid Case ID.</span>
								<span id="validCaseValue" style="color:red; display:none;">Please Enter Valid Case ID.</span>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>

		
		<div class="chargebackFooter">
			<input type="hidden" id="chargebackChatHidden">
			<div style="display: flex; width: 100%; ">
				<div class="col-sm-12 col-lg-12">
					<label class="d-flex align-items-center fs-6 fw-semibold mb-2">Chatbox</label>
					<div id="chargeBackChatBox" class="chargebackChatBoxClass">

					</div>
					<span><textarea id="chargebackAddMessage" 
						placeholder="Enter message here..." /></textarea> <i class="material-icons"
						style="cursor: pointer;" id="materialSendIcon">send</i></span>
					<label for="cbFile" class="material-icons" style="cursor: pointer;position: absolute;right: 60px;font-size: 30px;bottom: 10px;"><i class="fa fa-upload" id="materialSendIconUplaod" aria-hidden="true"></i></label>
				</div>
			</div>
			<div>
				<div style="text-align: left;">
					<ul>
						<li>File formats allowed : .jpg, .jpeg, .png, .pdf</li>
						<li>Number of files uploaded should not be greater than 10.</li>
						<li>Sum of size of all files should not exceed 10MB.</li>
						<li>Characters allowed in file name : dot, space, underscore, round brackets, alphabets and digits.</li>
						<li>File name length should be in range : 1 to 50</li>
					</ul>
				</div>
				<div style="text-align: center;font-weight: bold;" id="cbErrorField"></div>
			</div>
			
			<div class="col-sm-12 col-lg-12" style="text-align: center">
				<input type="button" value="Create" id="createChargebackButton"
					class="btn btn-primary  mt-4 submit_btn">
				<div class="modal fade" id="chargebackErrorFieldModal" role="dialog">
					<div class="modal-dialog">

						<!-- Modal content-->
						<div class="modal-content">
							<div class="modal-header"></div>
							<div class="modal-body">
								<p class="enter_otp" id="chargebackErrorField"></p>
							</div>
							<div class="modal-footer" id="modal_footer">
								<button type="button"
									class="btn btn-primary btn-round mt-4 submit_btn"
									data-dismiss="modal">Ok</button>
							</div>
						</div>

					</div>
				</div>
				<!-- <label id="chargebackErrorField"></label> -->
				<input type="button" value="Create New" id="reloadCreateChargeback"
					class="btn btn-primary  mt-4 submit_btn">
				<!-- <input type="button" value="Failure Graph" id="createFailureGraph"
					class="btn btn-primary  mt-4 submit_btn"> -->

			</div>
		</div>
	
		
		<!--  onsubmit="uploadFile()" -->
<form id="saleform" name="saleform" target="output_frame" method="post" action="tempCbFileUpload" enctype="multipart/form-data">
<!-- 		change action name  -->
		<input name="fileUpload" id="cbFile" type="file" accept="*" style="margin-top: 25px">
		<input class="hideElement" name = "payId" id="cbPayId">
		<input class="hideElement" name = "timestamp" id="cbFileUploadTimeStamp" value="">
		<input class="hideElement"name="usertype" value='<s:property value="%{#session.USER.UserType.name()}"/>'>
		<input class="hideElement" name="username" value='<s:property value = "%{#session.USER.firstName}"/>'>
		<input class="hideElement" name="message" value="">
		<input class="hideElement" name="responseCode" value="">
	</form>
	<iframe  class="hideElement" name="output_frame" src="" id="output_frame" width="XX" height="YY">
		</iframe> 

	</div>
	<s:token />
	
	<script>
    var myframe = document.getElementById("output_frame");
    myframe.onload = function() {
        var iframeDocument = myframe.contentDocument || myframe.contentWindow.document; // get access to DOM inside the iframe
        var content = iframeDocument.textContent || iframeDocument.body.textContent; // get text of iframe
        var json = JSON.parse(content);

        if (json) {
            // process the json here
            $('#cbErrorField').html(json.responseMessage);
            setTimeout(function(){
            	$('#cbErrorField').html("");
            }, 5000)
            //console.log(json);
        }
    }
</script>

</body>
</html>