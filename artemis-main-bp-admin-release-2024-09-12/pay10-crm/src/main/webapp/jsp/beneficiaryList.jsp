<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Beneficiary list</title>
<link href="../css/default.css" rel="stylesheet" type="text/css" />
<script src="../js/jquery.minshowpop.js"></script>
<script src="../js/jquery.formshowpop.js"></script>
<script src="../js/commanValidate.js"></script>
<link href="../css/default.css" rel="stylesheet" type="text/css" />
<link rel="stylesheet" type="text/css" media="all"
	href="../css/daterangepicker-bs3.css" />
<link href="../css/Jquerydatatableview.css" rel="stylesheet" />
<link href="../css/jquery-ui.css" rel="stylesheet" />
<link href="../css/Jquerydatatable.css" rel="stylesheet" />
<script src="../js/jquery.min.js" type="text/javascript"></script>
<script src="../js/core/popper.min.js"></script>
<script src="../js/core/bootstrap-material-design.min.js"></script>
<script src="../js/plugins/perfect-scrollbar.jquery.min.js"></script>
<!-- Control Center for Material Dashboard: parallax effects, scripts for the example pages etc -->
<script src="../js/material-dashboard.js?v=2.1.0" type="text/javascript"></script>
<script src="../js/moment.js" type="text/javascript"></script>
<script src="../js/daterangepicker.js" type="text/javascript"></script>
<script src="../js/jquery.dataTables.js"></script>
<script src="../js/jquery-ui.js"></script>
<script src="../js/commanValidate.js"></script>
<script src="../js/jquery.popupoverlay.js"></script>
<script src="../js/dataTables.buttons.js" type="text/javascript"></script>
<script src="../js/pdfmake.js" type="text/javascript"></script>

<script src="../js/jszip.min.js" type="text/javascript"></script>
<script src="../js/vfs_fonts.js" type="text/javascript"></script>
<script src="../js/buttons.colVis.min.js" type="text/javascript"></script>

<link href="../css/select2.min.css" rel="stylesheet" />
<script src="../js/jquery.select2.js" type="text/javascript"></script>
<script src="../js/beneficiarylist.js" type="text/javascript"></script>
<script>
	$(document).ready(function () {
		document.getElementById("loadingInner").style.display = "none";
	$("#merchantPayId").select2();
	});
</script>
<script type="text/javascript">
	$(document).ready(function() {

	
    
	
document.getElementById("beneficiaryListDiv").style.display="none";

 $('#submit').click(function(event){
   
   
   document.getElementById("beneficiaryListDiv").style.display="block";
   reloadTable();	   
  });
  
   $(function() {			
	   renderTable();
	   ;
   });


});

	
	
	// $("#submit").click(function(env) {
	// $('#loader-wrapper').show();
	// 		reloadTable();
	// });
	// $(function() {
	//  		renderTable();
	//  	});
	function editBeneficiary(data) {

		var answer = confirm("Are you sure you want to edit acquirer details?");
		if (answer != true) {

			return false;
		} else {

			var id = data.parentElement.parentElement.cells[0].innerText;
			document.getElementById("param").value = "EDIT";
			document.getElementById("frmId").value = id;
			document.getElementById("frmEditBeneficiary").submit();
		}

	}

	function removeBeneficiary(data) {

		var answer = confirm("Are you sure you want to remove acquirer details?");
		if (answer != true) {

			return false;
		} else {
			var id = data.parentElement.parentElement.cells[0].innerText;
			document.getElementById("param").value = "EDIT";
			document.getElementById("frmId").value = id;
			document.getElementById("frmEditBeneficiary").submit();
		}

	}

	function renderTable() {
		aaData = [];
		var acquirer = document.getElementById("acquirer").value;
		//var merchantPayId = document.getElementById("merchantPayId").value;
		var table = new $.fn.dataTable.Api('#beneficiaryListResultDataTable');
		var token = document.getElementsByName("token")[0].value;

		var buttonCommon = {
			exportOptions : {
				format : {
					body : function(data, column, row, node) {
						// Strip $ from salary column to make it numeric
						return column === 0 ? "'" + data : (column === 1 ? "'"
								+ data : data);
					}
				}
			}
		};

		$('#beneficiaryListResultDataTable')
				.dataTable(
						{
							"footerCallback" : function(row, data, start, end,
									display) {
								var api = this.api(), data;

							},
							"columnDefs" : [ {
								className : "dt-body-right",
								"targets" : [ 0, 1, 2, 3, 4, 5, 6 , 7]
							} ],

							order : [ [ 0, 'asc' ] ],

							dom : 'BTrftlpi',
							buttons : [
									$.extend(true, {}, buttonCommon, {
										extend : 'copyHtml5',
										exportOptions : {
											columns : [ 0, 1, 2, 3, 4, 5, 6, 7 ]
										},
									}),
									$.extend(true, {}, buttonCommon, {
										extend : 'csvHtml5',
										title : 'Beneficiary List_' + getCurrentTimeStamp(),
										exportOptions : {

											columns : [ 0, 1, 2, 3, 4, 5, 6, 7 ]
										},
									}),
									{
										extend : 'pdfHtml5',
										orientation : 'landscape',
										pageSize : 'legal',
										//footer : true,
										title : 'Beneficiary List_' + getCurrentTimeStamp(),
										exportOptions : {
											columns : [ 0, 1, 2, 3, 4, 5, 6, 7 ]
										},
										customize : function(doc) {
											doc.defaultStyle.alignment = 'center';
											doc.styles.tableHeader.alignment = 'center';
										}
									}, 
									// {
									// 	extend : 'print',
										
									// 	title : 'Beneficiary List',
									// 	exportOptions : {
									// 		columns : [ 0, 1, 2, 3, 4, 5, 6, 7 ]
									// 	}
									// },
									 {
										extend : 'colvis',
										columns : [ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10,11,12,13,14,15]
									} ],
                         
							"ajax" : {

								"url" : "beneficiarySearchAction",
								"timeout" : 0,
								"type" : "POST",
								"data" : function(d) {
									return generatePostData(d);
								}
							},
							"fnDrawCallback" : function() {

								$("#submit").removeAttr("disabled");
								$('#loader-wrapper').hide();
							},
							"searching" : false,
							"ordering" : false,
							"processing" : true,
							"serverSide" : false,
							"paging" : true,
							"pageLength" : 10,
							"paginationType" : "full_numbers",
							"lengthMenu": [[10, 25, 50], [10, 25, 50]],
							"order" : [ [ 2, "asc" ] ],

							"columnDefs" : [ {
								"type" : "html-num-fmt",
								"targets" : 4,
								"orderable" : true,
								"targets" : [ 0, 1, 2, 3, 4, 5, 6 ]
							} ],

							"columns" : [
									{
										"data" : "id",
										"className" : "id text-class",
										"visible" : false
										// "render": function (data, type, row, meta) {
										// 	return meta.row + meta.settings._iDisplayStart + 1;
										// }
									},
			
									{
										"data" : "merchantBusinessName",
										"className" : "merchant_business_name text-class"
									},
									{
										"data" : "acquirer",
										"className" : "acquirer text-class"
									},
									
									{
										"data" : "merchantProvidedId",
										"className" : "merchant_Provided_Id text-class"
									},
									{
										"data" : "merchantProvidedName",
										"className" : "merchant_Provided_Name text-class"
									},
									{
										"data" : "beneAccountNo",
										"className" : "bene_Account_No text-class"
									},
									{
										"data" : "aadharNo",
										"className" : "aadhar_number text-class",
										"visible" : false
									},
									{
										"data" : "ifscCode",
										"className" : "ifsc_Code text-class"
									},
									{
										"data" : "paymentType",
										"className" : "payment_Type text-class",
										"visible" : false
									},

									{
										"data" : "bankName",
										"className" : "bank_Name text-class",
										"visible" : false
									},
									{
										"data" : "currencyCd",
										"className" : " currency_Cd text-class"
									},
									{
										"data" : "emailId",
										"className" : " email_Id text-class"
									},
									{
										"data" : "mobileNo",
										"className" : "mobile_No text-class"
									},
									{
										"data" : "address1",
										"className" : "address_1 text-class",
										"visible" : false
									},
									{
										"data" : "address2",
										"className" : "address_2 text-class",
										"visible" : false
									},
									{
										"data" : "beneExpiryDate",
										"className" : "bene_Expiry_Date text-class"
									},
									{
										"data" : "status",
										"className" : "text-class"
									},
									{
										"data" : null,
										"className" : "center",
										"orderable" : false,
										"mRender" : function(row) {
											if (row.status == "ACTIVE" || row.status == "INACTIVE") {
												aaData.push(row);
												return '<button  data-toggle="modal" data-target="#popupForBene" onclick="showPopBene('+ row.id +')"  value="Edit" class="btn btn-info btn-xs">EDIT</button>';

											}else{
												return 'NA';
											}

										}
									} ]
						});

	}

//<button onclick="btnAction()" id="actionBtn" name="modify"  value="MODIFY" class="btn btn-info btn-xs">Modify</button><button onclick="btnAction()" id="actionBtn" name="disable" value="DISABLE" class="btn btn-info btn-xs">Disable</button>	
	function btnAction(val){
	let tableIndex = $(val).parent().parent().index();
		
			var row = val;
			var cells = val.parentElement.parentElement.cells;
			var beneAccountNo = $('#bene_Account_No').val();
			var id = $('#id').val();
			var beneType = $('#bene_Type').val();
			var ifscCode = $('#ifsc_Code').val();
			var aadharNo = $('#aadhar_No').val();
			var beneExpiryDate = $('#bene_Expiry_Date').val();
			var beneName = $('#merchant_Provided_Name').val();
			var merchantProvidedId = $('#merchant_Provided_Id').val();
			var paymentType = $('#payment_type').val();
			var merchantBusinessName = $('#merchant_business_name').val();
			var currencyCd = $('#currency_Cd').val();
			var bankName = $('#bank_Name').val();
			var mobileNo = $('#mobile_No').val();
			var emailId = $('#email_Id').val();
			var address1 = $('#address_1').val();
			var address2 = $('#address_2').val();
			var acquirer = document.getElementById("acquirer").value;
			var merchantPayId = document.getElementById("merchantPayId").value;
			var frmId = "1032200818155144";
			var action = $(val).val();
			var token = document.getElementsByName("token")[0].value;
			var acquirer = $('.acquirer').eq(tableIndex+2).html();
			//document.getElementById("modifyBtn").disabled = true;
			//document.getElementById("disableBtn").disabled = true;
		//	$('#loader-wrapper').show();
			document.getElementById("loadingInner").style.display = "block";
		$.ajax({
			url : "editBeneficiary",
			type : "POST",
			data : {
				"bankName": bankName,
				"ifscCode":ifscCode,
				"mobileNo": mobileNo,
				"emailId" :emailId,
				"address1" :address1,
				"address2" :address2,
				"aadharNo" : aadharNo,
				"beneAccountNo" : beneAccountNo,
				"beneType" : 'V',
				"beneName" : beneName,
				"beneExpiryDate" : beneExpiryDate,
				"merchantProvidedId": merchantProvidedId,
				"currencyCd" : currencyCd,
				"paymentType" : paymentType,
				"merchantBusinessName" : merchantBusinessName,
				"id":id,
				frmId : frmId,
				token:token,
				acquirer:acquirer,
				merchantPayId: merchantPayId,
				action:action,

			    "struts.token.name": "token",
			},
			success : function(data) {
				// var message = data;
				//  alert(message);
				document.getElementById("listAction").innerHTML=data.response;
				document.getElementById("loadingInner").style.display = "none";
			//$('#actionOnBeneList').modal('show');
			setTimeout(function(){
				$('#actionOnBeneList').modal('show');
			
			
		},100)
		$('#popupForBene').modal('hide');
			//window.location.reload();	
			//$('#loader-wrapper').hide();
			//document.getElementById("modifyBtn").disabled = false;
			//document.getElementById("disableBtn").disabled = false;
           
				 
			},
			error: function(data) {
				document.getElementById("loadingInner").style.display = "none";
			//$('#loader-wrapper').hide();
			//document.getElementById("modifyBtn").disabled = false;
			//document.getElementById("disableBtn").disabled = false;
           
        }
		});
		 
	}
		
	
	function reloadTable() {
		$("#submit").attr("disabled", true);
		var tableObj = $('#beneficiaryListResultDataTable');
		var table = tableObj.DataTable();
		table.ajax.reload();
	}

	function generatePostData(d) {
		var token = document.getElementsByName("token")[0].value;
		var beneficiaryCd = document.getElementsByName("beneficiaryCd")[0].value.trim();
		var beneName =   document.getElementsByName("beneName")[0].value.trim();
		var merchantPayId = document.getElementById("merchantPayId").value;
		var acquirer = document.getElementById("acquirer").value;
		var paymentType = document.getElementById("paymentType").value;
		var statusType = document.getElementById("statusType").value;
		var phone = document.getElementById("phone").value;
		var emailId = document.getElementById("emailId").value;
	
		if (acquirer == '') {
			acquirer = 'ALL'
		}
		if(merchantPayId==''){
			merchantPayId='ALL'
		}

		if(paymentType==''){
			paymentType='ALL'
		}
		if(beneficiaryCd==''){
			beneficiaryCd='ALL'
		}
		if(beneName==''){
			beneName='ALL'
		}
		if(phone==''){
			phone='ALL'
		}
		if(emailId==''){
			emailId='ALL'
		}
		if(statusType==''){
			statusType='ALL'
			}
		var obj = {
			acquirer : acquirer,
			merchantPayId : merchantPayId,
			paymentType : paymentType,
			beneName :beneName,
			beneficiaryCd : beneficiaryCd,
			statusType : statusType,
			draw : d.draw,
			phone:phone,
			emailId : emailId,
			length : d.length,
			start : d.start,
			token : token,
			"struts.token.name" : "token",
		};

		return obj;
	}
	function valdBenCode() {
	var beneCodeElement = document.getElementById("beneficiaryCd");
	var value = beneCodeElement.value.trim();
	if (value.length > 0) {
		var benCode = beneCodeElement.value;
		var benCodeExp = /^[a-zA-Z0-9]{1,32}$/;
		if (!benCode.match(benCodeExp)) {
			document.getElementById('invalid-benCode').innerHTML = "Please enter valid Benefiary Code";
			
			return false;
		} else {
			document.getElementById('invalid-benCode').innerHTML = "";
			return true;
		}
	} else {
		beneCodeElement.focus();
        document.getElementById('invalid-benCode').innerHTML = "";
		return true;
	}
}
function valdBenName() {
	var beneNameElement = document.getElementById("beneName");
	var value = beneNameElement.value.trim();
	if (value.length > 0) {
		var benName = beneNameElement.value;
		var benNameExp = /^[a-zA-Z\b\s]{1,255}$/;
		if (!benName.match(benNameExp)) {
			document.getElementById('invalid-benName').innerHTML = "Please enter valid Benefiary Name";
			
			return false;
		} else {
			document.getElementById('invalid-benName').innerHTML = "";
			return true;
		}
	} else {
		beneNameElement.focus();
        document.getElementById('invalid-benName').innerHTML = "";
		return true;
	}
}
function valdPayId() {
	var payIdElement = document.getElementById("payId");
	var value = payIdElement.value.trim();
	if (value.length > 0) {
		var payId = payIdElement.value;
		var payIdExp = /^[0-9]{10,20}$/;
		if (!payId.match(payIdExp)) {
			document.getElementById('invalid-payId').innerHTML = "Please enter valid Pay Id";
			
			return false;
		} else {
			document.getElementById('invalid-payId').innerHTML = "";
			return true;
		}
	} else {
		payIdElement.focus();
        document.getElementById('invalid-payId').innerHTML = "";
		return true;
	}
}
var aaData = [];
var modifyPopupIndex = 0;
function showPopBene(id){
	modifyPopupIndex = id;
	var benerows = customFilter(aaData,id.toString());
//	console.log(benerows);
	if(benerows.merchantProvidedId != null){
		//$("#td_code").html(benerows.merchantProvidedId);
		$("#td_code").html("<input style='border:none' onkeyup='FieldValidator.valdBenCode(false);_ValidateField();' type='text' id='merchant_Provided_Id'  value="+benerows.merchantProvidedId+"><br><span id='merchant_Provided_IdErr' class=''></span>");
	}else{
		$("#td_code").html("<input style='border:none' onkeyup='FieldValidator.valdBenCode(false);_ValidateField();' type='text' id='merchant_Provided_Id'  value='Not Available'><br><span id='merchant_Provided_IdErr' class=''></span>");
	}
	
	if(benerows.merchantProvidedName != null){
		//$("#td_name").html(benerows.merchantProvidedName);
		$("#td_name").html("<input style='border:none' onkeyup='FieldValidator.valdBeneName(false);_ValidateField();' type='text' id='merchant_Provided_Name'  value="+benerows.merchantProvidedName+"><br><span id='merchant_Provided_NameErr' class=''></span>");
	}else{
		$("#td_name").html("<input style='border:none' onkeyup='FieldValidator.valdBeneName(false);_ValidateField();' type='text' id='merchant_Provided_Name'  value='Not Available'><br><span id='merchant_Provided_NameErr' class=''></span>");
	}

	if(benerows.beneAccountNo != null){
	//	$("#td_accNo").html(benerows.beneAccountNo);
		$("#td_accNo").html("<input style='border:none' onkeyup='FieldValidator.valdBenAcc(false);_ValidateField();' type='text' id='bene_Account_No'  value="+benerows.beneAccountNo+"><br><span id='bene_Account_NoErr' class=''></span>");
	}else{
		$("#td_accNo").html("<input style='border:none' onkeyup='FieldValidator.valdBenAcc(false);_ValidateField();' type='text' id='bene_Account_No'   value='Not Available'><br><span id='bene_Account_NoErr' class=''></span>");
	}
	
	if(benerows.ifscCode != null){
		//$("#td_ifsc").html(benerows.ifscCode);
		$("#td_ifsc").html("<input style='border:none' onkeyup='FieldValidator.valdIfsc(false);_ValidateField();' type='text' id='ifsc_Code'  value="+benerows.ifscCode+"><br><span id='ifsc_CodeErr' class=''></span>");
	}else{
		$("#td_ifsc").html("<input style='border:none' onkeyup='FieldValidator.valdIfsc(false);_ValidateField();' type='text' id='ifsc_Code'  value='Not Available'><br><span id='ifsc_CodeErr' class=''></span>");
	}
	
	if(benerows.beneType != null){
		
		$("#td_beneType").html("<input style='border:none'  type='text' id='bene_Type'  value='V'><br><span id='bene_TypeErr' class=''></span>");
	}else{
		$("#td_beneType").html("<input style='border:none'  type='text' id='bene_Type'  value='V'><br><span id='bene_TypeErr' class=''></span>");
	}

	if(benerows.merchantBusinessName != null){
		
		$("#td_merchantBusinessName").html("<input style='border:none' type='text' id='merchant_business_name'  value="+benerows.merchantBusinessName+">");
	}else{
		$("#td_merchantBusinessName").html("<input style='border:none' type='text' id='merchant_business_name'  value='Not Available'>");
	}

	if(benerows.id != null){
		$("#td_Id").html("<input style='border:none' type='text' id='cust_id'  value="+benerows.id+">");
		
	}else{
		$("#td_Id").html("<input style='border:none' type='text' id='cust_id'  value='Not Available'>");
	}

	if(benerows.bankName != null){
	
		$("#td_bankName").html("<input style='border:none' onkeyup='FieldValidator.valdBankName();_ValidateField(false);' type='text' id='bank_Name'  value="+benerows.bankName+"><br><span id='bank_NameErr' class=''></span>");
	}else{
		$("#td_bankName").html("<input style='border:none' onkeyup='FieldValidator.valdBankName();_ValidateField(false);' type='text' id='bank_Name'  value='Not Available'><br><span id='bank_NameErr' class=''></span>");
	}

	if(benerows.currencyCd != null){
	
		$("#td_curCode").html("<input style='border:none' type='text' id='currency_Cd'  value="+benerows.currencyCd+">");
	}else{
		$("#td_curCode").html("<input style='border:none' type='text' id='currency_Cd'  value='Not Available'>");
	}
	
	if(benerows.beneExpiryDate != null){
	
		$("#td_expiryDate").html("<input style='border:none'  type='text' id='bene_Expiry_Date'  value="+benerows.beneExpiryDate+"><br><span id='invalid-date' style='color:red'></span>");
	}else{
		$("#td_expiryDate").html("<input style='border:none'  type='text' id='bene_Expiry_Date'  value='Not Available'><br><span id='invalid-date' style='color:red'></span>");
	}
	if(benerows.emailId != null ){
		$("#td_emailId").html("<input style='border:none' onkeyup='checkEmailId()'  type='text' id='email_Id'  value="+benerows.emailId+"><br><span id='invalid-id' style='color:red'></span>");
		
	}else{
		$("#td_emailId").html("<input style='border:none' onkeyup='checkEmailId()'  type='text' id='email_Id'  value='Not Available'><br><span id='invalid-id' style='color:red'></span>");
	}
	if(benerows.mobileNo != null ){
	
		$("#td_mobileNo").html("<input style='border:none' onkeyup='valdPhoneNo()' onkeypress='return checkInput(event);' type='text' id='mobile_No'  value="+benerows.mobileNo+"><br><span id='invalid-phone' style='color:red'></span>");
	}else{
		$("#td_mobileNo").html("<input style='border:none' onkeyup='valdPhoneNo()' onkeypress='return checkInput(event);' type='text' id='mobile_No'  value='Not Available'><br><span id='invalid-phone' style='color:red'></span>");
	}
	if(benerows.address1 != null){
		//$("#td_address1").html(benerows.address1);
		$("#td_address1").html("<input style='border:none' onkeyup='valdAddress()' type='text' id='address_1'  value="+benerows.address1+"><br><span id='invalid-add' style='color:red'></span>");
	}else{
		$("#td_address1").html("<input style='border:none' onkeyup='valdAddress()' type='text' id='address_1'  value='Not Available'><br><span id='invalid-add' style='color:red'></span>");
	}
	if(benerows.address2 != null){
		//$("#td_address2").html(benerows.address2);
		$("#td_address2").html("<input style='border:none' onkeyup='valdAddress1()' type='text' id='address_2'  value="+benerows.address2+"><br><span id='invalid-add1' style='color:red'></span>");
	}else{
		$("#td_address2").html("<input style='border:none' onkeyup='valdAddress1()' type='text' id='address_2'  value='Not Available'><br><span id='invalid-add1' style='color:red'></span>");
	}
	if(benerows.aadharNo != null){
		//$("#td_aadhar").html(benerows.aadharNo);
		$("#td_aadhar").html("<input style='border:none' type='text' onkeyup='valdAadhar()' id='aadhar_No'  value="+benerows.aadharNo+"><br><span id='invalid-aadhar' style='color:red'></span>");
	}else{
		$("#td_aadhar").html("<input style='border:none' type='text' onkeyup='valdAadhar()' id='aadhar_No'  value='Not Available'><br><span id='invalid-aadhar' style='color:red'></span>");
	}
	

	


	if(benerows.status == "ACTIVE"){
		
		// document.getElementById("verifyBtn").disabled = true;
		document.getElementById("enableBtn").disabled = true;
		document.getElementById("modifyBtn").disabled = false;
		document.getElementById("disableBtn").disabled = false;

		document.getElementById("currency_Cd").setAttribute("readonly", true);
		document.getElementById("bene_Expiry_Date").setAttribute("readonly", true);
		document.getElementById("merchant_business_name").setAttribute("readonly", true);
		//document.getElementById("cust_id").setAttribute("readonly", true);
		document.getElementById("merchant_Provided_Id").setAttribute("readonly", true);
		document.getElementById("merchant_Provided_Name").removeAttribute("readonly");
		document.getElementById("bene_Account_No").setAttribute("readonly", true);
		document.getElementById("ifsc_Code").setAttribute("readonly", true);
		document.getElementById("bene_Type").setAttribute("readonly", true);
		
		document.getElementById("bank_Name").removeAttribute("readonly");
		
		document.getElementById("email_Id").removeAttribute("readonly");
		document.getElementById("mobile_No").removeAttribute("readonly");
		document.getElementById("address_1").removeAttribute("readonly");
		document.getElementById("address_2").removeAttribute("readonly");
		document.getElementById("aadhar_No").removeAttribute("readonly");
		}
		if(benerows.status == "INACTIVE" || benerows.status == 'REJECTED'){
		// document.getElementById("verifyBtn").disabled = false;
		document.getElementById("enableBtn").disabled = false;
		document.getElementById("modifyBtn").disabled = true;
		document.getElementById("disableBtn").disabled = true;

		document.getElementById("merchant_Provided_Id").setAttribute("readonly", true);
		document.getElementById("merchant_Provided_Name").setAttribute("readonly", true);
		document.getElementById("bene_Account_No").setAttribute("readonly", true);
		document.getElementById("ifsc_Code").setAttribute("readonly", true);
		document.getElementById("bene_Type").setAttribute("readonly", true);
		document.getElementById("merchant_business_name").setAttribute("readonly", true);
		//document.getElementById("cust_id").setAttribute("readonly", true);
		document.getElementById("bank_Name").setAttribute("readonly", true);
		document.getElementById("currency_Cd").setAttribute("readonly", true);
		document.getElementById("bene_Expiry_Date").setAttribute("readonly", true);
		document.getElementById("email_Id").setAttribute("readonly", true);
		document.getElementById("mobile_No").setAttribute("readonly", true);
		document.getElementById("address_1").setAttribute("readonly", true);
		document.getElementById("address_2").setAttribute("readonly", true);
		document.getElementById("aadhar_No").setAttribute("readonly", true);
		}
}


function customFilter(data,val){
	var toreturn;
$.each( data, function( key, value ) {
if(value.id==val){
	toreturn = value;
}
});
return toreturn;
}

</script>
<style type="text/css">
.error-text {
	color: #a94442;
	font-weight: bold;
	background-color: #f2dede;
	list-style-type: none;
	text-align: center;
	list-style-type: none;
	margin-top: 10px;
}

.error-text li {
	list-style-type: none;
}

#response {
	color: green;
}

.errorMessage {
	display: none;
}

.errorInpt {
	font: 400 11px arial;
	color: red;
	display: none;
	margin-left: 7px;
}

.fixHeight {
	height: 64px;
}

.adduT {
	margin-bottom: 0 !important;
}

.btnSbmt {
	padding: 5px 10px !important;
	margin-right: 26px !important;
}

.actionMessage {
	border: 1px solid transparent;
	border-radius: 0 !important;
	width: 100% !important;
	margin: 0 !important;
}
</style>
<style type="text/css">
.cust {
	width: 24% !important;
	margin: 0 5px !important; /*font: bold 10px arial !important;*/
}

.samefnew {
	width: 24% !important;
	margin: 0 5px !important;
	/*font: bold 10px arial !important;*/
}

 #actionBtn {
	padding: 3px 7px !important;
	font-size: 12px !important;
} 

.samefnew-btn {
	width: 15%;
	float: left;
	font: bold 11px arial;
	color: #333;
	line-height: 22px;
	margin-left: 5px;
}
/*tr td.my_class{color:#000 !important; cursor: default !important; text-decoration: none;}*/
tr td.my_class {
	cursor: pointer;
}

tr td.my_class:hover {
	cursor: pointer !important;
}

tr th.my_class:hover {
	color: #fff !important;
}

.cust .form-control, .samefnew .form-control {
	margin: 0px !important;
	width: 100%;
}

.select2-container {
	width: 100% !important;
}

.clearfix:after {
	display: block;
	visibility: hidden;
	line-height: 0;
	height: 0;
	clear: both;
	content: '.';
}

#popup {
	position: fixed;
	top: 0px;
	left: 0px;
	background: rgba(0, 0, 0, 0.7);
	width: 100%;
	height: 100%;
	z-index: 999;
	display: none;
}

.innerpopupDv {
	width: 600px;
	margin: 80px auto;
	/* background: #fff; */
	padding: 3px 10px;
	border-radius: 10px;
}

.btn-custom {
	margin-top: 5px;
	height: 27px;
	border: 1px solid #5e68ab;
	background: #5e68ab;
	padding: 5px;
	font: bold 12px Tahoma;
	color: #fff;
	cursor: pointer;
	border-radius: 5px;
}

#loader-wrapper .loader-section.section-left, #loader-wrapper .loader-section.section-right
	{
	background: rgba(225, 225, 225, 0.6) !important;
	width: 50% !important;
}

.invoicetable {
	float: none;
}

.innerpopupDv h2 {
	font-size: 12px;
	padding: 5px;
}

.text-class {
	text-align: center !important;
}

.odd {
	background-color: #e6e6ff !important;
}
.nav-pills {
    border: 0;
    border-radius: 3px;
    padding: 0 0px !important;
}
.card .card-body+.card-footer, .card .card-footer {
    padding: 0;
    padding-top: 10px;
    margin: 0px !important;
    border-radius: 0;
    justify-content: space-between;
    align-items: center;
}
.textFL_merch {
    border: 1px solid #c0c0c0;
    background: #fff;
    padding: 8px;
    width: 100%;
    color: #000;
    border-radius: 3px;
}

.textFL_merch:hover {
    border: 1px solid #d5d0a3;
    padding: 8px;
    width: 100%;
    border-radius: 3px;
}
	.textFL_merch_invalid {
    
    background: #d55252;
    color: #ffffff;
    padding: 8px;
    width: 100%;
    
    border-radius: 1px;
}
.addbene {
  color: #2457a3;
  font-weight: 500;
  text-align: center;
}
#loading {width: 100%;height: 100%;top: 0px;left: 0px;position: fixed;display: block; z-index: 99}
#loading-image {position: absolute;top: 40%;left: 55%;z-index: 100; width:10%;} 

#loadingInner {width: 100%;height: 100%;top: 0px;left: 0px;position: fixed;display: block; z-index: 99}
#loading-image-inner {position: absolute;top: 33%;left: 48%;z-index: 100; width:5%;} 
</style>
</head>
<body>
	<div id="loader-wrapper" style="width: 100%; height: 100%; display:none;">
		<div id="loader"></div>
	  </div>
	<table width="100%" border="0" cellspacing="0" cellpadding="0"
		class="txnf">
		<tr>
			<td align="left"><h2>Beneficiaries List</h2></td>
		</tr>

		<s:if test="%{responseObject.responseCode=='000'}">
			<tr>
				<td align="left" valign="top"><div id="saveMessage">
						<s:actionmessage class="success success-text" />
					</div></td>
			</tr>

		</s:if>
		<s:else>
			<div class="error-text">
				<s:actionmessage />
			</div>
		</s:else>

		<tr>
			<td align="left">
				<div class="container">
					<div class="col-md-12">
					<div class="form-group col-md-3 txtnew col-sm-3 col-xs-4">
						<label>Merchant</label>
						<s:select name="merchantPayId" headerKey="ALL" headerValue="Select Merchant"
										class="input-control merchantPayId"  id="merchantPayId"
										list="listMerchant" listKey="payId" listValue="businessName"
										autocomplete="off" />
					</div>
					<div class="form-group col-md-3 txtnew col-sm-3 col-xs-4">
						<label>Nodal Acquirer</label>
						<s:select headerKey="ALL" headerValue="Select Nodal Acquirer"
							class="input-control"
							list="@com.pay10.commons.util.AcquirerTypeNodal@values()"
							listValue="name" listKey="code" name="acquirer" id="acquirer"
							autocomplete="off" value="" />
					</div>
					<div class="form-group col-md-3 txtnew col-sm-3 col-xs-4">
						<label>Beneficiary Code:</label>
						<s:textfield name="beneficiaryCd" id="beneficiaryCd"
								class="input-control" autocomplete="off" onkeyup="valdBenCode()"
								onkeypress="return checkInput(event);"
								value="%{nodal.beneficiaryCode}" />
								<span id="invalid-benCode" style="color:red;"></span>
					</div>
				
				
					<div class="form-group col-md-3 txtnew col-sm-3 col-xs-4">
						<label>Beneficiary Name:</label>
						<s:textfield name="beneName" id="beneName" class="input-control"
						autocomplete="off" onkeypress="return checkName(event);" onkeyup="valdBenName()"
						value="%{nodal.beneName}" />
						<span id="invalid-benName" style="color:red;"></span>
					</div>
				</div>
					<div class="col-md-12">	
					<div class="form-group col-md-3 txtnew col-sm-3 col-xs-4">
						<label>Payment Type:</label>
						<s:select headerKey="ALL" headerValue="Select Payment Type"
							class="input-control"
							list="@com.pay10.commons.util.NodalPaymentTypesYesBankFT3@values()"
							listValue="name" listKey="code" name="paymentType" id="paymentType"
							autocomplete="off" value="" />
					
					</div>
					<div class="form-group col-md-3 txtnew col-sm-3 col-xs-4">
						<label>Status Type</label><br>
						<s:select headerKey="ALL" headerValue="Select Status"
							class="input-control"
							list="@com.pay10.commons.util.AccountStatus@values()"
							listValue="name" listKey="code" name="statusType" id="statusType"
							autocomplete="off" value="" />
				
  
					</div>
					<div class="form-group col-md-3 txtnew col-sm-3 col-xs-4">
						<label> Email :</label><br>
						<div class="txtnew">
							<s:textfield id="emailId" class="input-control"
								name="emailId" type="text" value="" autocomplete="off"
								onkeypress="checkEmailIdSearch();" ondrop="return false;" onKeyDown="if(event.keyCode === 32)return false;" ></s:textfield>
								<span id='invalidid' style='color:red'></span>
							</div>
						
					</div>
					<div class="form-group col-md-3 txtnew col-sm-3 col-xs-4">
						<label> Phone :</label><br>
						<div class="txtnew">
							<s:textfield id="phone" class="input-control noSpace"
								name="phone" type="text" value="" autocomplete="off"
								onkeypress="valdPhoneNoSearch(this);" onKeyDown="if(event.keyCode === 32)return false;" ondrop="return false;" minlength="10"  maxlength="16"></s:textfield>
								<span id='invalidphone' style='color:red'></span>
							</div>
						
					</div>
					</div>
					<div class="col-md-12">
					<div class="form-group col-md-3 txtnew col-sm-3 col-xs-4">
						
							<input type="button" id="submit" value="Search"
							
								class="btn btn-primary  mt-4 submit_btn">
								
						
					</div>
				</div>
				</div>
			</td>
		</tr>

		<tr>
			<td align="left" valign="top">&nbsp;</td>
		</tr>
	</table>

	<div id="beneficiaryListDiv" style="overflow: scroll !important;">
		<table id="mainTable" width="100%" border="0" align="center"
			cellpadding="0" cellspacing="0" class="txnf">

			<tr>
				<td colspan="5" align="left"><h2>&nbsp;</h2></td>
			</tr>
			<tr>
				<td align="left" style="padding: 10px;">
					<div class="scrollD">
						<table id="beneficiaryListResultDataTable" class=""
							cellspacing="0" width="100%">
							<thead>
								<tr class="boxheadingsmall">
									<th style='text-align: center; text-decoration: none !important;'>Id</th>
									<th style='text-align: center'>Merchant Name</th> 
									<th style='text-align: center'>Acquirer</th> 
									<th style='text-align: center'>Beneficiary Code</th>
									<th style='text-align: center'>Beneficiary Name</th>
									<th style='text-align: center'>Beneficiary Name</th>
									<th style='text-align: center'>Aadhar Number</th>
									<th style='text-align: center'>IFSC Code</th>
									<th style='text-align: center'>Payment Type</th>
									<!-- <th style='text-align: center'>Bene Type</th> -->
									<th style='text-align: center'>Bank Name</th>
									<th style='text-align: center'>Currency Code</th>
									<th style='text-align: center'>Email ID</th>
									<th style='text-align: center'>Mobile Number</th>
									<th style='text-align: center'>Address 1</th>
									<th style='text-align: center'>Address 2</th>
									<th style='text-align: center'>Expiry Date</th>
									<th style='text-align: center'>Status</th>
									<th style='text-align: center'>Action</th>

								</tr>
							</thead>
							<tfoot>
								<tr class="boxheadingsmall">
									<th></th>
									<th></th>
									<th></th>
									<th></th>
									<th></th>
									<th></th>
									<th></th>
									<th></th>
									<th></th>
									<th></th>
									<th></th>
									<th></th>
									<th></th>
									<th></th>
									<th></th>
									<th></th>
									<th></th>
									<th></th>

								</tr>
							</tfoot>
						</table>
					</div>
				</td>
			</tr>

		</table>
	</div>


	<div class="modal" id="popupForBene" style="overflow-y: auto;">
		<!-- <div > -->
				<!--<div class="wrapper " style="max-height: 590px;"> -->
    
					<div id="loadingInner" display="none">
						<img id="loading-image-inner" src="../image/sand-clock-loader.gif"
							alt="BUSY..." />
					</div>
					<!-- Navbar -->
				   
					<!-- End Navbar -->
					<div class=" innerpopupDv" >
    
    
					<!-- Navbar -->
				   
					<!-- End Navbar -->
					<!-- <div class="content"> -->
					  <div class="container-fluid">
					   
						<div class="row">
						  
						  
							<div class="card ">
							  <div class="card-header ">
								<h4 class="card-title" id="cardTitle" >Beneficiary Details
								  <!-- <small class="description">Vertical Tabs</small> -->
								</h4>
								<button style="position: absolute;
								left: 92%;top:5px;border: none;
								background: none;" data-dismiss="modal"  id="closeBtn1"><img style="width:30px" src="../image/red_cross.jpg"></button>
							  </div>
							  <div class="card-body ">
								<div class="row">
									<div class="col-md-12 ml-auto mr-auto">
								 
								 
									<div class="tab-content">
									  <div class="tab-pane active" id="link110">
										
										<!-- <h4 style="margin-left:10px;color: #4c55a0 !important;font-weight:600"> Information</h4> -->
										<button class="btn btn-primary" style="margin-left:25px" type="button" data-toggle="collapse" data-target="#collapseExample" aria-expanded="false" aria-controls="collapseExample">
											See the Instructions
										  </button>
										  <div class="collapse" id="collapseExample">
											<div class="card card-body">
												<div style="text-align: left; font-weight: bold;color:black">
													<ul>
													
														<li>Beneficiary Expiry Date format: YYYY-MM-DD</li>
														<li>Mandatory Fields:  Beneficiary Code, Beneficiary Name,Currency,Bank Name, IFSC Code, Beneficiary Account Number</li>
													</ul>
												</div>
											</div>
										  </div>
											   <div class="card-body">
											   
												   <table width="100%" class="invoicetable">
													<tr >
													
														<!-- <th  align="left" valign="middle">Id</th> -->
														<th  align="left" valign="middle"> Code<span
															style="color: red; margin-left: 3px;">*</span></th>
															<th  align="left" valign="middle"> Bene Name<span
																style="color: red; margin-left: 3px;">*</span></th>
													  </tr>
													  <tr id="sec5">
														
														<!-- <td align="left" valign="middle"  id="td_Id"></td> -->
														<td align="left" valign="middle"  id="td_code"> </td>		
														<td align="left" valign="middle"  id="td_name"></td>												
													  </tr>				
													
													 <tr>
														<th  align="left" valign="middle"> Bene Type<span
															style="color: red; margin-left: 3px;">*</span></th>
													   <th  align="left" valign="middle">Bene Account No.<span
														style="color: red; margin-left: 3px;">*</span></th>
													 
													  
													  </tr>
													 
													   <tr id="sec2">
													    <td align="left" valign="middle"  id="td_beneType"></td>
													   <td align="left" valign="middle"  id="td_accNo"></td>
													  
													   
													 </tr>
													 <tr>
														<th  align="left" valign="middle">IFSC Code<span
															style="color: red; margin-left: 3px;">*</span></th>
														<th  align="left" valign="middle">Bank Name<span
															style="color: red; margin-left: 3px;">*</span></th>
													
													   
													   </tr>
														<tr id="sec3">
															<td align="left" valign="middle"  id="td_ifsc"></td>
														<td align="left" valign="middle"  id="td_bankName"></td>
														
														
													  </tr>
													 <tr >
														<th  align="left" valign="middle">Merchant Name<span
															style="color: red; margin-left: 3px;">*</span></th>
														<th  align="left" valign="middle">Currency Code<span
															style="color: red; margin-left: 3px;">*</span></th>
													
													  </tr>
													  
													  <tr id="sec4">
														<td align="left" valign="middle"  id="td_merchantBusinessName"></td>
														<td align="left" valign="middle"  id="td_curCode"></td>
																	
													  </tr>
													  <tr >
														<tr >
															<th  align="left" valign="middle">Expiry Date</th>
															<th  align="left" valign="middle">Aadhar Number</th>
															
														  </tr>
														  
														  <tr id="sec4">
															<td align="left" valign="middle"  id="td_expiryDate"></td>	

															<td align="left" valign="middle"  id="td_aadhar"></td>
																				
														  </tr>
														  <tr >
															<th  align="left" valign="middle">Email Id</th>	
														<th  align="left" valign="middle">Mobile Number</th>
														
				
													  </tr>
													  <tr id="sec5">
													
														<td align="left" valign="middle" id="td_emailId"></td>
														<td align="left" valign="middle" id="td_mobileNo"></td>		
																	
													  </tr>
													  <tr >
														<th  align="left" valign="middle">Address 1</th>
														<th  align="left" valign="middle">Address 2</th>
													
													  </tr>
													  <tr id="sec6">
													
														<td align="left" valign="middle" id="td_address1"></td>
														<td align="left" valign="middle" id="td_address2"></td>	
																	
													  </tr>
													  
													
											   </table>
											  
												 <!-- <label for="cardMask" class="bmd-label-floating">Card Mask</label>
												 <input type="email" class="form-control" id="cardMask"> -->
											   
											   </div>
											  
											 
											
										   
									  </div>
									
									 
									</div>
									<div class="card-footer ">
									  <!-- <button class="btn btn-success" onclick="btnAction(this)"  name="verify"  value="VERIFY"  id="verifyBtn" ><i class="fa fa-check-square-o"></i> VERIFY<div class="ripple-container"></div></button> -->
									  <button class="btn btn-warning" onclick="btnAction(this)"  name="modify"  value="MODIFY" id="modifyBtn"><i class="fa fa-pencil-square-o fa-5x"></i> MODIFY<div class="ripple-container"></div></button>
									  <button class="btn btn-danger" onclick="btnAction(this)"  name="disable"  value="DISABLE" id="disableBtn"> <i class="material-icons">block</i>  DISABLE<div class="ripple-container"></div></button>
									   <button class="btn btn-info" onclick="btnAction(this)"  name="enable"  value="ENABLE" id="enableBtn"><i class="fa fa-thumbs-up fa-5x"></i> ENABLE<div class="ripple-container"></div></button>
									  <!-- <button type="submit" class="btn btn-fill btn-rose" id="closeBtn">Close</button> -->
									</div>
									<!-- <div style="text-align: center;"><button class="btn btn-danger" id="closeBtn">Close</button></div>	 -->
								  
								</div>
							  </div>
							</div>
						  </div>
						</div>
						
					  </div>
					<!-- </div> -->
				   
				  
				</div>
										  
		</div>

	<div class="modal" id="popup" style="overflow-y: auto;">
		<!-- <div > -->
				<!--<div class="wrapper " style="max-height: 590px;"> -->
    
    
					<!-- Navbar -->
				   
					<!-- End Navbar -->
					<div class=" innerpopupDv" >
						
    
					<!-- Navbar -->
				   
					<!-- End Navbar -->
					<!-- <div class="content"> -->
					  <div class="container-fluid">
					   
						<div class="row">
						  
						  
							<div class="card ">
							  <div class="card-header ">
								<h4 class="card-title" id="cardTitle" >Actions for Beneficiary Details
								  <!-- <small class="description">Vertical Tabs</small> -->
								</h4>
								<button style="position: absolute;
								left: 95%;
                                top: 3px;border: none;
								background: none;" class="close" data-dismiss="modal"  id="closeBtn1"><img style="width:20px" src="../image/red_cross.jpg"></button>
							  </div>
							  <div class="card-body ">
								
								<div class="row">
									<div class="col-md-12 ml-auto mr-auto">
								  
									<!--
											  color-classes: "nav-pills-primary", "nav-pills-info", "nav-pills-success", "nav-pills-warning","nav-pills-danger"
										  -->
									<ul class="nav nav-pills nav-pills-rose nav-pills-icons flex-row"  role="tablist">
									  <li class="nav-item" id="listitem">
										<a class="nav-link card-header-success" data-toggle="tab" onclick="btnAction()"  name="verify"  value="VERIFY"  role="tablist">
											<i class="fa fa-check-square-o"></i> Verify
										</a>
									  </li>
									  <li class="nav-item" id="listitem">
										<a class="nav-link card-header-warning" data-toggle="tab" onclick="btnAction()"  name="modify"  value="MODIFY"  role="tablist">
											<i class="fa fa-pencil-square-o fa-5x"></i> Modify
										</a>
									  </li>
									  <li class="nav-item" id="listitem">
										<a class="nav-link card-header-danger" onclick="btnAction()"  name="disable"  value="DISABLE" data-toggle="tab"  role="tablist">
										  <i class="material-icons">block</i> Disable
										</a>
									  </li>
									  <li class="nav-item" id="listitem">
										<a class="nav-link card-header-rose" onclick="btnAction()"  name="enable"  value="ENABLE" data-toggle="tab"  role="tablist">
										  <!-- <i class="material-icons">person</i> -->
										  <i class="fa fa-thumbs-up fa-5x"></i>Enable
										
										</a>
									  </li>
									</ul>
								 
								  
								</div>
							  </div>
							</div>
						  </div>
						</div>
						
					  </div>
					<!-- </div> -->
				   
				  
				</div>
										  
		</div>
		<div class="modal fade" id="actionOnBeneList" role="dialog">
			<div class="modal-dialog">
		
			  <!-- Modal content-->
			  <div class="modal-content">
				<div class="modal-header">
		
				</div>
				<div class="modal-body">
				  <p class="addbene" id="listAction"></p>
				</div>
				<div class="modal-footer" id="modal_footer">
				  <button type="button" class="btn btn-primary btn-round mt-4 submit_btn" data-dismiss="modal">Ok</button>
				</div>
			  </div>
		
			</div>
		  </div>

	<s:form action="editBeneficiaryAction" id="frmEditBeneficiary">
		<s:hidden name="token" value="%{#session.customToken}"></s:hidden>
		<s:hidden name="frmId" id="frmId" value=""></s:hidden>
		<s:hidden name="param" id="param" value=""></s:hidden>
	</s:form>
</body>
</html>