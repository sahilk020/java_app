<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<%@taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Automated Fund Transfer</title>
<link href="../css/default.css" rel="stylesheet" type="text/css" />
<script src="../js/jquery.minshowpop.js"></script>
<script src="../js/jquery.formshowpop.js"></script>
<script src="../js/commanValidate.js"></script>
<link href="../css/default.css" rel="stylesheet" type="text/css" />
<link rel="stylesheet" type="text/css" media="all" href="../css/daterangepicker-bs3.css" />
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

<!--  loader scripts -->
<script src="../js/loader/modernizr-2.6.2.min.js"></script>
<script src="../js/loader/main.js"></script>
<link rel="stylesheet" href="../css/loader/normalize.css" />
<link rel="stylesheet" href="../css/loader/main.css" />
<link rel="stylesheet" href="../css/loader/customLoader.css" />

<link href="../css/select2.min.css" rel="stylesheet" />
<script src="../js/jquery.select2.js" type="text/javascript"></script>
<script src="../js/nodalFundTransfer.js"></script>
<script>
	$(document).ready(function () {
		document.getElementById("loadingInner").style.display = "none";
	$("#merchantPayId").select2();
	});
</script>
<script type="text/javascript">


	$(document).ready(function() {
	// document.getElementById("beneficiaryListDiv").style.display="none";
	  $('#submit').click(function(event){
	//	document.getElementById("beneficiaryListDiv").style.display="block";
		reloadTable();	 
	   });
	   
		$(function() {			
			renderTable();
			;
		});
		/* $(function(){
			var table = $('#beneficiaryListResultDataTable').DataTable();
			$('#beneficiaryListResultDataTable').on('click', 'td.my_class1', function() {
				var rowIndex = table.cell(this).index().row;
				var rowData = table.row(rowIndex).data();
				//popup().show();	
			});
		}); */


	});
	function checkNeft(id){
	var element = document.getElementById(id);
    var value = parseFloat(element.value.trim());

    if(value < 1 || value > 200000 || (value.length < 1 || value.length > 6)){
		document.getElementById(id).nextElementSibling.innerHTML = "Amount cannot exceed Rs. 2 lakhs";
		//document.getElementById("payBtn").disabled = true;	
	//alert("Amount can not be exceed by 2 lack")
       
           return false;
	}
	else{
		document.getElementById(id).nextElementSibling.innerHTML = "";
		//document.getElementById("payBtn").disabled = false;
	}

	}
	function checkIMPS(id){
	var element = document.getElementById(id);
    var value = parseFloat(element.value.trim());

    if(value < 1 || value > 200000 || (value.length < 1 || value.length > 6)){
		document.getElementById(id).nextElementSibling.innerHTML = "Amount cannot exceed Rs. 2 lakhs";
	//	document.getElementById("payBtn").disabled = true;	
	//alert("Amount can not be exceed by 2 lack")
       
           return false;
	}
	else{
		document.getElementById(id).nextElementSibling.innerHTML = "";
		//document.getElementById("payBtn").disabled = false;
	}

	}

	
	function checkRTGS(id){
		
	var element = document.getElementById(id);
    var value = parseFloat(element.value.trim());

	if(value < 200000 || (value.length < 6 )){
		
		document.getElementById(id).nextElementSibling.innerHTML = "The minimum amount to be transferred through RTGS is Rs. 2 lakh.";	
		//document.getElementById("payBtn").disabled = true;
	//alert("Amount should be greater than or equal to 2 lakh in RTGS Payment")
       
           return false;
	}
	else{
		document.getElementById(id).nextElementSibling.innerHTML = "";
	//	document.getElementById("payBtn").disabled = false;
	}
	

	}
	// function checkComments(id){

	// 	var aadharElement = document.getElementById("id");
  
    //     var aadharexp = /^[0-9a-zA-Z\b\_/@(),.\-]+$/;
    //     if (!aadharexp) {
	// 		document.getElementById(id).nextElementSibling.innerHTML = "Please Enter Valid Comment";
           
    //         return false;
    //     } else {
	// 		document.getElementById(id).nextElementSibling.innerHTML = "";
            
    //         return true;
    //     }
    

	// }
	// function validateComments(val){
		
	// 	let tableIndex = $(val).parent().parent().index();
		
	// 	var row = val;
	//    var cells = val.parentElement.parentElement.cells;
	//    let paymentType = $(".nodal_Payment_Type option:selected").eq(tableIndex).val();
	  
	// 	if(paymentType == "R" || paymentType == 'N' || paymentType == "FT" || paymentType == "IMPS"){
	// 		checkComments(val.id);
		
	// 	}

		
	// }
	
	
	function _validatePayments(val){
		
		let tableIndex = $(val).parent().parent().index();
		//console.log(tableIndex);
		var row = val;
	   var cells = val.parentElement.parentElement.cells;
	   let paymentType = $(".nodal_Payment_Type option:selected").eq(tableIndex).val();
	   console.log(paymentType);
		if(paymentType == "R"){
		checkRTGS(val.id);
		
		}
		if(paymentType == "N"){
			checkNeft(val.id);
			
		}
		if(paymentType == "IMPS"){
			checkIMPS(val.id);
			
		}
		
		
	}



	function startPayment(val,getindex) {
		var merchantProvidedId = $('#sec5 td').eq(0).text();
		var comments = $('#sec7 td').eq(1).text();
		var paymentType = $('#sec4 td').eq(1).text();
		var acquirer = $('#sec3 td').eq(0).text();
		var merchantPayId = $('#sec4 td').eq(2).text();
		var amount = $('#sec7 td').eq(0).text();
			

			if (comments == null || comments == ""){
				// TODO Validate Comments.
				alert ("Please Enter Comments");
				return false;
			}
			
			if (paymentType == "Please select" || paymentType == ""){
				alert ("Please Select Payment Type");
				return false;
			}
			// Validate data on keypress.
			/* if(paymentType == "NEFT"){
				checkNeft(amountindex);
			}
			if(paymentType == "RTGS"){
				checkRTGS(amountindex);
			}
			if(paymentType == "IMPS"){
				checkIMPS(amountindex);
			} */
			
			if (amount == null || amount == ""){
				alert ("Please Enter Amount");
				return false;
			}
			
			var token  = document.getElementsByName("token")[0].value;
			
			 //$('#loader-wrapper').show();
			 //document.getElementById("payBtn"+getindex).disabled = true;
			 document.getElementById("loadingInner").style.display = "block";
			 $('#popup').hide();
		$.ajax({
			type: "POST",
			timeout : 0,
			url:"initiateNodalTransaction",
			data:{
				"merchantProvidedId":merchantProvidedId,
				"comments":comments,
				"paymentType":paymentType,
				"acquirer":acquirer,
				"merchantPayId":merchantPayId,
				"amount":amount,
				"token":token,
				"struts.token.name": "token"
				},
			success:function(data){
				console.log(data);
				//var response = ((data["Invalid request"] != null) ? (data["Invalid request"].response[0]) : (data.response));
				if(data.response != null && data.response != undefined){
					var response = data.response;
					document.getElementById("statusPayout").innerHTML=data.response;
					$('#payoutStatus').modal('show');
                    //document.getElementById("payBtn"+getindex).disabled = false;
				}
				document.getElementById("loadingInner").style.display = "none";
		    },
			error:function(data){
				alert("Nodal Payout failed.");
				document.getElementById("loadingInner").style.display = "none";
                //document.getElementById("payBtn"+getindex).disabled = false;
			}
		});
			
		};

		function popup(val,getindex) {
			console.log("Loading confirmation popup.");
			var table = $('#beneficiaryListResultDataTable').DataTable();
			let tableIndex = $(val).parent().parent().index();
			$('#beneficiaryListResultDataTable').off("click");
			$('#beneficiaryListResultDataTable').on('click', 'td.my_class1', function() {
			var rowIndex = table.cell(this).index().row;
			var rowData = table.row(rowIndex).data();
			console.log(rowData);
			var merchantProvidedCode = rowData.merchantProvidedId ;
			var paymentType = $(".nodal_Payment_Type option:selected").eq(tableIndex).val();
			var payId = rowData.merchantPayId ;
			var acquirer = rowData.acquirer ;
			var comments = $('.comments_class').eq(tableIndex).val().trim();
			var amount = $('.amounts_class').eq(tableIndex).val().trim();
			$('.comments_class').eq(tableIndex).val("");
			$('.amounts_class').eq(tableIndex).val("");
			
			var token = document.getElementsByName("token")[0].value;
			var myData = {
				token : token,
				"struts.token.name" : "token",
				"merchantProvidedCode" : merchantProvidedCode,
				"paymentType" : paymentType,
				"payId": payId,
				"acquirer" : acquirer,
				"amount" : amount,
				"comments" : comments

			}
			$.ajax({
			    	url: "getBeneficiaryDetails",
			    	timeout : 0,
			    	type : "POST",
			    	data :myData,
			    	success: function(responseObj){
						if(responseObj.response == "" && responseObj.response != undefined){
							$('#sec1 td').eq(0).text(responseObj.nodalAccountNumber ? responseObj.nodalAccountNumber : 'Not Available');
							$('#sec2 td').eq(0).text(responseObj.nodalAccountHolderName ? responseObj.nodalAccountHolderName : 'Not Available');
							$('#sec3 td').eq(0).text(responseObj.acquirer ? responseObj.acquirer : 'Not Available');
	
							$('#sec4 td').eq(0).text(responseObj.merchantName ? responseObj.merchantName : 'Not Available');
							$('#sec4 td').eq(1).text(responseObj.paymentType ? responseObj.paymentType : 'Not Available');
							$('#sec4 td').eq(2).text(responseObj.payId ? responseObj.payId : 'Not Available');
	
							$('#sec5 td').eq(0).text(responseObj.beneficiaryName ? responseObj.merchantProvidedCode : 'Not Available');
							$('#sec5 td').eq(1).text(responseObj.beneficiaryIfscCode ? responseObj.beneficiaryAccountNumber : 'Not Available');
	
							$('#sec6 td').eq(0).text(responseObj.beneficiaryName ? responseObj.beneficiaryName : 'Not Available');
							$('#sec6 td').eq(1).text(responseObj.beneficiaryIfscCode ? responseObj.beneficiaryIfscCode : 'Not Available');
	
							$('#sec7 td').eq(0).text(responseObj.amount ? responseObj.amount : 'Not Available');
							$('#sec7 td').eq(1).text(responseObj.comments ? responseObj.comments : 'Not Available');
							
							$('#popup').show();
						}
						else {
							document.getElementById("statusPayout").innerHTML=responseObj.response;
							$('#payoutStatus').modal('show');
						}
			    	},
			    	error: function(xhr, textStatus, errorThrown){
				       alert('request failed');
				    }
			});
 
		});
		
	};
		
	function renderTable() {
		var getindex = 0;
		 var acquirer = document.getElementById("acquirer").value;

		var table = new $.fn.dataTable.Api('#beneficiaryListResultDataTable');
	
		var token = document.getElementsByName("token")[0].value;

		
		 var buttonCommon = {
        exportOptions: {
            format: {
                body: function ( data, column, row, node ) {
                    // Strip $ from salary column to make it numeric
                    return column === 0 ? "'"+data : (column === 1 ? "'" + data: data);
                }
            }
        }
    }; 
		// Values can be : alert (Default), throw, none
		$.fn.dataTable.ext.errMode = 'none'; // This will print error in the console rather than showing alert in browser.
		$('#beneficiaryListResultDataTable').on('error.dt', function (e, settings, techNote, message) {
			console.log(message)
	        alert('Failed to load beneficiaries for nodal payout');
			$("#submit").removeAttr("disabled");
			$('#loader-wrapper').hide();
	    }).dataTable(
						{
							
							"footerCallback" : function(row, data, start, end, display) {
								var api = this.api(), data;
								// Remove the formatting to get integer data for summation
								var intVal = function(i) {
									return typeof i === 'string' ? i.replace(/[\,]/g, '') * 1: typeof i === 'number' ? i : 0;
								};

								// Total over all pages
								total = api.column(1).data().reduce(
										function(a, b) {
											return intVal(a) + intVal(b);
										}, 0);

								// Total over this page
								pageTotal = api.column(1, {
									page : 'current'
								}).data().reduce(function(a, b) {
									return intVal(a) + intVal(b);
								}, 0);

								// Update footer
								//$(api.column(10).footer()).html(
									//	'' + inrFormat(pageTotal.toFixed(2) + ' ' + ' '));
										
										
								// Total over all pages
								total = api.column(1).data().reduce(
										function(a, b) {
											return intVal(a) + intVal(b);
										}, 0);

								// Total over this page
								pageTotal = api.column(1, {
									page : 'current'
								}).data().reduce(function(a, b) {
									return intVal(a) + intVal(b);
								}, 0);

								// Update footer
								//$(api.column(11).footer()).html(
									//	'' + inrFormat(pageTotal.toFixed(2) + ' ' + ' '));
										
								
							},
							"columnDefs": [{ 
								className: "dt-body-right",
								"targets": [0, 1, 2, 3, 4, 5, 6]
							}],
								dom : 'BTrftlpi',
								buttons : [
										$.extend( true, {}, buttonCommon, {
											extend: 'copyHtml5',											
											extend: 'copyHtml5',
											exportOptions: {
												columns: [ 0, ':visible' ]
											}
										} ),
									$.extend( true, {}, buttonCommon, {
											extend: 'csvHtml5',
											title : 'Beneficiary List_' + getCurrentTimeStamp(),
											exportOptions : {
												
												columns : [0, 1, 2, 3, 4, 5, 6]
											},
										} ),
									{
										extend : 'pdfHtml5',
										orientation : 'landscape',
										pageSize: 'legal',
										//footer : true,
										title : 'Beneficiary List_' + getCurrentTimeStamp(),
										exportOptions : {
											columns: [0, 1, 2, 3, 4, 5, 6]
										},
										customize: function (doc) {
										    doc.defaultStyle.alignment = 'center';
					     					doc.styles.tableHeader.alignment = 'center';
										  }
									},
									// {
									// 	extend : 'print',
									// 	//footer : true,
									// 	title : 'Beneficiary List',
									// 	exportOptions : {
									// 		columns : [0, 1, 2, 3, 4, 5, 6]
									// 	}
									// },
									{
										extend : 'colvis',
										columns : [0, 1, 2, 3, 4, 5, 6, 7,8,9,10,11]
									} ],

							"ajax" :{
								
								"url" : "beneficiaryNodalSearch",
								"timeout" : 0,
								"type" : "POST",
								"data": function (d){
									return generatePostData(d);
								}
							},
							"fnDrawCallback" : function() {

									 $("#submit").removeAttr("disabled");
									 $('#loader-wrapper').hide();
							},
							"bProcessing" : true,
							"bDestroy" : true,
							"bLengthChange" : true,
							"iDisplayLength" : 10,
							"searching" : false,
							"ordering" : false,
							"processing" : true,
							"serverSide" : true,
							"paging" : true,
						    "paginationType": "full_numbers", 
						        "lengthMenu": [[10, 25, 50], [10, 25, 50]],
								//"order" : [ [ 2, "asc" ] ],
						       
						        "columnDefs": [
						            {
						            "type": "html-num-fmt", 
						            "targets": 4,
						            "orderable": true, 
						            "targets": [0,1,2,3,4,5,6]
						            }
						        ], 

 
							"columns" : [
							{
								"data" : "id",
										"className" : "id text-class",
										"visible" : false
										// "render": function (data, type, row, meta) {
										// 	return meta.row + meta.settings._iDisplayStart + 1;
										// }
									},
							// 	{
							// 	"data" : "custId",
							// 	"className" : "cust_Id text-class"
							// },
							{
										"data" : "merchantBusinessName",
										"className" : "merchant_Business_Name text-class"
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
								"data" : "ifscCode",
								"className" : "ifsc_Code text-class"
							},
							{
								"data" : "bankName",
								"className" : "bank_Name text-class",
								"visible" : false
							},
							{
										"data" : "aadharNo",
										"className" : "aadhar_number text-class",
										"visible" : false
									},
						
							// {
							// 	"data" : "beneType",
							// 	"className" : "bene_Type text-class"
							// },
							
							{
								"data" : "currencyCd",
								"className" : " currency_Cd text-class"
							},
							{
								"data" : "acquirer",
								"className" : "acquirer text-class",
								//"visible" : false
							},
							{
								"data" : "beneExpiryDate",
								"className" : "bene_Expiry_Date text-class"
							},
							// {
							// 	"data" : "merchantPayId",
							// 	"className" : "merchant_Pay_Id text-class",
							// 	//"visible" : false
							// },
							
							// {
							// 	"data" : "beneficiaryCd",
							// 	"visible" : false,
							// 	"className" : "beneficiary_Code text-class"
								
							// },
							
							{
								"data" : null,
								"className" : " center",
								"width" : '4%',
								"orderable" : false,
								"mRender" : function(row) {
											if (true) {
												return createElement(row);
											} else {
												return "";
											}
									},
									
							},{
								"data" : null,
								"className" : "center",
								"width" : '6%',
								"orderable" : false,
								"mRender" : function(row) {
									
											if (true) {
												return '<input type="text"  class="amounts_class text-class" onkeyup="_validatePayments(this)"  id="amount'+getindex +'" name="amount"  onpaste="return false"    onkeypress="return isNumberKey(event,this);"><span id="errormessage"></span>';
											} else {
												return "";
											}
								    }
							},{
								"data" : null,
								"className" : "center",
								"width" : '8%',
								"orderable" : false,
								"mRender" : function(row) {
									
											if (true) {
												return '<input type="text" minlength="2" maxlength="150" name="comments" onkeypress="return checkInputComment(event);"   class="comments_class text-class" id="comments'+getindex +'" ><span id="errormessage1" style="color:red;"></span>';
											} else {
												return "";
											}
								    }
							},{
								"data" : null,
								"className" : "center my_class1",
								"width" : '4%',
								"orderable" : false,
								"mRender" : function(row) {
									
											if (true) {
												return '<button class="btn btn-info btn-xs btn-block" class="payBtn_class" id="payBtn'+getindex +'" onclick="popup(this,'+getindex++ +')"  >Pay</button>';
											} else {
												return "";
											}
								    }
							},
							]
						});
						
		
			
		
	}
	function checkPaymentAmount(){
		//var amount = $('.amounts_class').eq(tableIndex).val().trim();
		//console.log(amount);
		
		//document.getElementById("amount"+getindex).value = "";
		//console.log(document.getElementById("amount"+getindex).value);
		//$(".amounts_class").val("");
		//$("#errormessage").value("");
		//document.getElementById("errormessage").value = "";
		
	}

	
	function createElement(row) {
		var obj = JSON.parse(row.actions.toString());
		var div = $('<div/>');
		var selectTag = $('<select/>').addClass('nodal_Payment_Type').attr("onchange","checkPaymentAmount()").append('<option value="1" selected= selected>Select Payment</option>');
		var menu = $('#nodalPaymentType').clone();
		$.each(obj,function(index,data){
			var nodalPaymentTypeKey = "";
			if(data == "NEFT"){
				nodalPaymentTypeKey = "N";
			} else if(data == "RTGS"){
				nodalPaymentTypeKey = "R";
			} else if(data == "IMPS"){
				nodalPaymentTypeKey = "IMPS";
			} else if(data == "FT"){
				nodalPaymentTypeKey = "FT";
			}
			var optiontag = $('<option/>').val(nodalPaymentTypeKey).html(data);
			selectTag.append(optiontag);
		});
		div.append(selectTag);
		return div.html();
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
		var mobile = document.getElementById("mobile").value;
		var emailId = document.getElementById("emailId").value;
		//var statusType = document.getElementById("statusType").value;
		if(acquirer==''){
			acquirer='ALL'
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
		// if(emailId==''){
		// 	emailId='ALL'
		// }
		// if(mobile==''){
		// 	mobile='ALL'
		// }
		// if(statusType==''){
		// 	statusType='ALL'
		// 	}
		
		var obj = {
			acquirer : acquirer,
			merchantPayId : merchantPayId,
			paymentType : paymentType,
			beneName :beneName,
			beneficiaryCd : beneficiaryCd,
			statusType : "",
			 mobile:mobile,
			 emailId : emailId,
			draw : d.draw,
			length :d.length,
			start : d.start, 
			token : token,
			"struts.token.name" : "token",
		};

		return obj;
	}
	
function isNumberKey(evt, element) {
  var charCode = (evt.which) ? evt.which : event.keyCode
  if (charCode > 31 && (charCode < 48 || charCode > 57) && !(charCode == 46 || charCode == 8))
    return false;
  else {
    var len = $(element).val().length;
    var index = $(element).val().indexOf('.');
    if (index > 0 && charCode == 46) {
      return false;
    }
    if (index > 0) {
      var CharAfterdot = (len + 1) - index;
      if (CharAfterdot > 3) {
        return false;
      }
    }

  }
  return true;
}
function valdBenCode() {
	var beneCodeElement = document.getElementById("beneficiaryCd");
	var value = beneCodeElement.value.trim();
	if (value.length > 0) {
		var benCode = beneCodeElement.value;
		var benCodeExp = /^[a-zA-Z0-9]{1,32}$/;
		if (!benCode.match(benCodeExp)) {
			document.getElementById('invalid-benCode').innerHTML = "Please enter valid Beneficiary Code";
			$("#submit").attr("disabled", true);
			return false;
		} else {
			document.getElementById('invalid-benCode').innerHTML = "";
			$("#submit").attr("disabled", false);
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
		var benNameExp = /^[a-zA-Z\b-&'\s]{1,255}$/;
		if (!benName.match(benNameExp)) {
			document.getElementById('invalid-benName').innerHTML = "Please enter valid Beneficiary Name";
			$("#submit").attr("disabled", true);
			return false;
		} else {
			document.getElementById('invalid-benName').innerHTML = "";
			$("#submit").attr("disabled", false);
			return true;
		}
	} else {
		beneNameElement.focus();
        document.getElementById('invalid-benName').innerHTML = "";
		$("#submit").attr("disabled", false);
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



	
</script>

<style type="text/css">.error-text{color:#a94442;font-weight:bold;background-color:#f2dede;list-style-type:none;text-align:center;list-style-type: none;margin-top:10px;
}.error-text li { list-style-type:none; }
#response{color:green;}
.errorMessage{
  display: none;
}
.errorInpt{
      font: 400 11px arial ;
      color: red;
      display: none;
      margin-left: 7px;
}
.fixHeight{
  height: 64px;
}
.adduT{
  margin-bottom: 0 !important;
}
.btnSbmt{
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
.cust {width: 24%!important; margin:0 5px !important; /*font: bold 10px arial !important;*/}
.samefnew{
	width: 24%!important;
    margin: 0 5px !important;
    /*font: bold 10px arial !important;*/
}
/* .btn {padding: 3px 7px!important; font-size: 12px!important; } */
.samefnew-btn{
    width: 15%;
    float: left;
    font: bold 11px arial;
    color: #333;
    line-height: 22px;
    margin-left: 5px;
}
/*tr td.my_class{color:#000 !important; cursor: default !important; text-decoration: none;}*/
tr td.my_class{
	cursor: pointer;
}
tr td.my_class:hover{
	cursor: pointer !important;
}

tr th.my_class:hover{
	color: #fff !important;
}

.cust .form-control, .samefnew .form-control{
	margin:0px !important;
	width: 100%;
}
.select2-container{
	width: 100% !important;
}
.clearfix:after{
	display: block;
	visibility: hidden;
	line-height: 0;
	height: 0;
	clear: both;
	content: '.';
}
#popup{
	position: fixed;
	top:0px;
	left: 0px;
	background: rgba(0,0,0,0.7);
	width: 100%;
	height: 100%;
	z-index:999; 
	display: none;
}
.innerpopupDv{
	    width: 600px;
    margin: 80px auto;
    background: #fff;
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
#loader-wrapper .loader-section.section-left, #loader-wrapper .loader-section.section-right{
	background: rgba(225,225,225,0.6) !important;
	width: 50% !important;
}
.invoicetable{
	float: none;
}
.innerpopupDv h2{
	    font-size: 12px;
    padding: 5px;
}
.text-class{
	text-align: center !important;
}
.odd{
	background-color: #e6e6ff !important;
}
.hideElement{
	display: none;
}
#errormessage{
	color: red;
    font-size: 10px;
 
}
#errormessage1{
	color: red;
    font-size: 10px;
 
}
#loading {width: 100%;height: 100%;top: 0px;left: 0px;position: fixed;display: block; z-index: 99}
#loading-image {position: absolute;top: 40%;left: 55%;z-index: 100; width:10%;} 

#loadingInner {width: 100%;height: 100%;top: 0px;left: 0px;position: fixed;display: block; z-index: 99}
#loading-image-inner {position: absolute;top: 33%;left: 48%;z-index: 100; width:5%;} 
.sitemessage {
  display: inline-block;
  white-space: nowrap;
  animation: floatText 15s infinite linear;
  padding-left: 100%; /*Initial offset*/
}
.sitemessage:hover {
  animation-play-state: paused;
}
@keyframes floatText {
  to {
    transform: translateX(-100%);
  }
}
</style>
</head>
<body>

<div id="loader-wrapper" style="width: 100%; height: 100%; display:none;">
	  <div id="loader"></div>
	</div>
	<div id="loadingInner" display="none">
		<img id="loading-image-inner" src="../image/sand-clock-loader.gif"
			alt="BUSY..." />
	</div>
	<div class="modal" id="popup" style="overflow-y: auto;">
		<!-- <div > -->
				<!--<div class="wrapper " style="max-height: 590px;"> -->
    
    
					<!-- Navbar -->
				   
					<!-- End Navbar -->
					<div class="content innerpopupDv" >
    
    
					<!-- Navbar -->
				   
					<!-- End Navbar -->
					<!-- <div class="content"> -->
					  <div class="container-fluid">
					   
						<div class="row">
						  
						  
							<div class="card ">
							  <div class="card-header ">
								
								<button style="position: absolute;
								left: 93%;border: none;
								background: none;"  id="closeBtn1"><img style="width:20px" src="../image/red_cross.jpg"></button>
							  </div>
							  <div class="card-body ">
								<div class="row">
									<div class="col-md-12 ml-auto mr-auto">
								  <div class="col-lg-4 col-md-6">
									<!--
											  color-classes: "nav-pills-primary", "nav-pills-info", "nav-pills-success", "nav-pills-warning","nav-pills-danger"
										  -->
									<ul class="nav nav-pills nav-pills-rose nav-pills-icons flex-column"  role="tablist">
									  <li class="nav-item" id="listitem">
										<a class="nav-link active" data-toggle="tab" href="#link110" role="tablist">
										  <i class="material-icons">people</i> Nodal Details
										</a>
									  </li>
									  <li class="nav-item" id="listitem">
										<a class="nav-link" data-toggle="tab" href="#link111" role="tablist">
										  <i class="material-icons">money</i> Beneficiary Transaction Details
										</a>
									  </li>
									  
									</ul>
								  </div>
								  <div class="col-md-8">
									<div class="tab-content">
									  <div class="tab-pane active" id="link110">
										
										<h4 style="margin-left:10px;color: #4c55a0 !important;font-weight:600"></h4>
											   <div class="card-body">
											   
												   <table width="100%" class="invoicetable">				
													<tr>
														<th  height="25" colspan="4" align="left" valign="middle">Account Number</th>
													   
													   </tr>
													   <tr id="sec1">
														<td height="25" colspan="4" align="left" valign="middle"></td>
													   
													  </tr>
													  <tr>
														<th  height="25" colspan="4" align="left" valign="middle">Account Holder Name</th>
													   
													   </tr>
													   <tr id="sec2">
														<td height="25" colspan="4" align="left" valign="middle"></td>
													   
													  </tr>
													  <tr>
														<th  height="25" colspan="4" align="left" valign="middle">Acquirer</th>
													   
													   </tr>
													   <tr id="sec3">
														<td height="25" colspan="4" align="left" valign="middle"></td>
													   
													  </tr>
													
													
											   </table>
											  
												 <!-- <label for="cardMask" class="bmd-label-floating">Card Mask</label>
												 <input type="email" class="form-control" id="cardMask"> -->
											   
											   </div>
											 
											 
											 
											
										   
									  </div>
									  <div class="tab-pane" id="link111">
										
										  
										 
										 <h4 style="margin-left:10px;color: #4c55a0 !important;font-weight:600"></h4>
											<div class="card-body">
											
												<table width="100%" class="invoicetable">
												
												  
												  <tr>
													<th  align="left" valign="middle">Merchant Name</th>
													<th  align="left" valign="middle">Payment Type</th>
													<th  align="left" valign="middle" style="display: none">Merchant PayID</th>
												   
												   </tr>
												
												   <tr id="sec4">
													<td align="left" valign="middle"></td>
													<td align="left" valign="middle"></td>
												   <td align="left" valign="middle" style="display: none;"></td>
												  </tr>
												  <tr>
													<th  align="left" valign="middle">Beneficiary Code</th>
													<th  align="left" valign="middle">Beneficiary Account</th>
												   
												   </tr>
													<tr id="sec5">
													<td align="left" valign="middle"></td>
													<td align="left" valign="middle"></td>
													
												  </tr>
												  <tr>
													<th align="left" valign="middle">Beneficiary Name</th>
													<th align="left" valign="middle">Beneficiary IFSC</th>
													
													</tr>
												  <tr id="sec6">
													<td align="left" valign="middle"></td>
													<td align="left" valign="middle"></td>
													
													</tr>
													<tr>
														<th align="left" valign="middle">Amount</th>
														<th align="left" valign="middle">Comments</th>
														
														</tr>
													  <tr id="sec7">
														<td align="left" valign="middle"></td>
														<td align="left" valign="middle"></td>
														
														</tr>
													
													
													<!-- <tr>
													  <th height="25" colspan="4" align="left" valign="middle"><span>Address</span></th>
			  
													</tr>
													<tr id="address6">
													  <td height="25" colspan="4" align="left" valign="middle"></td>
			  
													</tr> -->
											</table>
					
											  <!-- <label for="cardMask" class="bmd-label-floating">Card Mask</label>
											  <input type="email" class="form-control" id="cardMask"> -->
											
											</div>
										 
										  
										  
										 
										</div>
									 
									</div>
									<div class="card-footer " style="float: right;">
									  <button class="btn btn-danger" id="closeBtn">Close<div class="ripple-container"></div></button>
									  <button class="btn btn-danger" id="confirmBtn">Confirm Payout<div class="ripple-container"></div></button>
									  <!-- <button type="submit" class="btn btn-fill btn-rose" id="closeBtn">Close</button> -->
									</div>
									<!-- <div style="text-align: center;"><button class="btn btn-danger" id="closeBtn">Close</button></div>	 -->
								  </div>
								</div>
							  </div>
							</div>
						  </div>
						</div>
						
					  </div>
					<!-- </div> -->
				   
				  
				</div>
										  
		</div>

		<div class="sitemessage">
			"As per RBI mandate, the beneficiary account would be credited on the basis of account number and IFSC only. In the event of any mismatch between the account number and IFSC, Pay10 would not be responsible."
		 </div>	
	<div class="card ">
		<div class="card-header card-header-rose card-header-text">
			<div class="card-text">
				<h4 class="card-title">Ledger Nodal</h4>
			</div>
		</div>
		<div class="card-body ">

			<div class="container" style="display: grid;">
				<!-- <div class="col-sm-6 col-md-6"></div> -->
				<div class="col-sm-6 col-md-6">
					<input type="button" id="getNodalBalance"
						value="Check Available Balance" class="btn btn-primary submit_btn">
				</div>
				<div class="col-sm-6 col-md-6">
					<input type="text" class="input-control" style="margin-top: 10px;"
						value="" id="nodalBalance" readonly>
						<span id="nodalBalanceError" style="color:red;"></span>
				</div>

			</div>
		</div>
	</div>	
<table width="100%" border="0" cellspacing="0" cellpadding="0" class="txnf">
  <tr>
    <td align="left"><h2>Automated Fund Transfer Payout</h2></td>
  </tr>
  <tr>
	  <td>
		<div class="col-sm-3 col-lg-3">
			<button class="btn btn-primary" type="button" data-toggle="modal" data-target="#instruction">
				Instructions
				 </button>
			
		</div> 
	

	<div class="modal fade" id="instruction" role="dialog">
		<div class="modal-dialog">
	
		  <!-- Modal content-->
		  <div class="modal-content">
			<div class="modal-header">
	
			</div>
			<div class="modal-body">
				<ul>
					<li>Transfer to Other Accounts within YesBank through IMPS/RTGS/NEFT/FT</li>
					<li>Fund Transfer to Other Bank Account through IMPS/RTGS/NEFT</li>
					<li>FT -   1rs. to no limit</li>
					<li>NEFT - 1rs. upto 2 lakhs</li>
					<li>RTGS - Above 2 lakhs No Limit</li>
					<li>IMPS - 1rs. upto 2 lakhs</li>
				</ul>
			</div>
			<div class="modal-footer" id="modal_footer">
			  <button type="button" class="btn btn-primary btn-round mt-4 submit_btn" data-dismiss="modal">Ok</button>
			</div>
		  </div>
	
		</div>
	  </div>

	  </td>
  </tr>
  
  
  <s:if test="%{responseObject.responseCode=='000'}">
   <tr>
    <td align="left" valign="top"><div id="saveMessage">
        <s:actionmessage class="success success-text" />
      </div></td>
  </tr>
  
  </s:if>
<s:else><div class="error-text"><s:actionmessage/></div></s:else>

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
									    <s:select headerKey="ALL" headerValue="Select Nodal Acquirer" class="input-control"
										list="@com.pay10.commons.util.AcquirerTypeNodal@values()"
										listValue="name" listKey="code" name="acquirer"
										id="acquirer" autocomplete="off" value=""/>
								</div>
								<div class="form-group col-md-3 txtnew col-sm-3 col-xs-4">
									<label>Beneficiary Code:</label>
									<s:textfield name="beneficiaryCd" id="beneficiaryCd"
											class="input-control" autocomplete="off" onkeyup="valdBenCode()" ondrop="return false;" 
											onpaste="valdBenCode()" onkeypress="return checkInput(event);"
											value="%{nodal.beneficiaryCode}" />
											<span id="invalid-benCode" style="color:red;"></span>
								</div>
							
							
								<div class="form-group col-md-3 txtnew col-sm-3 col-xs-4">
									<label>Beneficiary Name:</label>
									<s:textfield name="beneName" id="beneName" class="input-control"
									autocomplete="off"  onkeyup="valdBenName()" ondrop="return false;" onpaste="valdBenName()"  onblur="valdBenName()"
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
										<s:textfield id="mobile" class="input-control noSpace"
											name="mobile" type="text" value="" autocomplete="off"
											onkeypress="valdPhoneNoSearch(this);" onKeyDown="if(event.keyCode === 32)return false;" ondrop="return false;" minlength="10"  maxlength="16"></s:textfield>
											<span id='invalidphone' style='color:red'></span>
										</div>
									
								</div>
								<!-- <div class="form-group col-md-3 txtnew col-sm-3 col-xs-4">
									<label>Status Type</label><br>
									<s:select headerKey="ALL" headerValue="Select Status"
									class="input-control"
									list="@com.pay10.commons.util.AccountStatus@values()"
									listValue="name" listKey="code" name="statusType" id="statusType"
									autocomplete="off" value="" />
								</div> -->
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


<div id = "beneficiaryListDiv" style="overflow:scroll !important;">
	<table id="mainTable" width="100%" border="0" align="center"
		cellpadding="0" cellspacing="0" class="txnf">
		

		<tr>
			<td colspan="5" align="left"><h2>&nbsp;</h2></td>
		</tr>
		<tr>
			<td align="left" style="padding: 10px;">
				<div class="scrollD">
					<table id="beneficiaryListResultDataTable" class="" cellspacing="0"
						width="100%">
						<thead>
							<tr class="boxheadingsmall">
								<th
										style='text-align: center; text-decoration: none !important;'>Id</th>
							
							 <th style='text-align: center'>Merchant Name</th> 
								<!-- <th style='text-align: center;text-decoration:none!important;' class="hideElement">Nodal Acc No</th> -->
								<th style='text-align: center'>Beneficiary Code</th>
								<th style='text-align: center'>Beneficiary Name</th>
								<th style='text-align: center'>Beneficiary Account No.</th>
								
								<th style='text-align: center;'>IFSC Code</th>	
								<!-- <th style='text-align: center'>Bene Type</th> -->
								<th style='text-align: center'>Bank Name</th>
								<th style='text-align: center'>Aadhar Number</th>								
								<th style='text-align: center'>Currency Code</th>	
								<th style='text-align: center'>Acquirer</th>	
								<th style='text-align: center'>Expiry Date</th>	
								<th style='text-align: center'>Payment Type</th>
								<!-- <th style='text-align: center'>Merchant Pay ID</th>	
								 -->
								
							
								<!-- <th style='text-align: center' class="hideElement">Beneficiary Code</th> -->
								<th style='text-align: center'>Amount</th>								
								<th style='text-align: center'>Comments</th>
								<th style='text-align: center'>Pay</th>
								<!-- <th class="hideElement" style='text-align: center;text-decoration:none!important;'>Nodal Acc No</th> -->
								
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
								<!-- <th></th>		 -->
							
								<th></th>				
								<th></th>
								<th></th>
								<th ></th>
								
	
							</tr>
						</tfoot>
					</table>
				</div>
			</td>
		</tr>

	</table>
  </div>
  <div class="card ">
	<div class="card-header card-header-rose card-header-text">
	  <div class="card-text">
		<h4 class="card-title">Batch Upload</h4>
	  </div>
	</div>
	<div class="card-body ">
<div class="container">
	
	<div class="row it">
	<div class="col-sm-offset-1 col-sm-10" id="one">
	<!-- <p>
	Please upload documents only in 'CSV' format.
	</p> -->
	<button class="btn btn-primary" style="float: right;" type="button" data-toggle="collapse" data-target="#collapseExample" aria-expanded="false" aria-controls="collapseExample">
		See the Instructions
	  </button>
	  <div class="collapse" id="collapseExample">
		<div class="card card-body">
			<div style="text-align: left; font-weight: bold;color:black">
				<ul>
					<li>File should be in CSV format.</li>
					<li>File size must not exceed 500 KB.</li>
					<li>File name length should be 5-50 characters.</li>
					<li>File name can contain alphabets, digits, round brackets, hyphen, underscore, dot and space.</li>
					<li>File should contain 1-100 records.</li>
					<li>Mandatory Fields: Payment Type, Amount, comment, Beneficiary Code and Currency Code</li>
				</ul>
			</div>
		</div>
	  </div>
	<div class="row">
	  <div class="col-sm-offset-4 col-sm-4 form-group">
		
	  </div><!--form-group-->
	</div><!--row-->
	<div id="uploader">
	<div class="row uploadDoc" style="height: 200px;">
		<div class="col-sm-4 ">
			<table
				id="example" class="csv" style="display: none;">
				<thead>
					<tr>
						<!-- <th>Id</th> -->
						<th>Payment Type</th>
						<th>Beneficiary Code</th>
						<th>Amount</th>

						<th>Comment</th>

						<th>Currency Code</th>

						<!-- <th>Beneficiary Name</th> -->
						<!-- <th>Bank Name</th>
						<th>Beneficiary Account Number</th>
						<th>Beneficiary Expiry Date</th>
						<th>Mobile Number</th> -->
						<!-- <th>Email Id</th>
						<th>Address 1</th>
						<th>Address 2</th>
						<th>Aadhar Number</th>
						<th>IFSC Code</th>
						<th>Currency Code</th> -->
						<!-- <th>Payment Type</th> -->
					</tr>
				</thead>
				<tbody>
					<tr>
					<td>NEFT</td>
					<td>ABCXYZ</td>
					<td>100</td>
					<!-- <td>2020-01-20</td>
					<td>911234567890</td>
					<td>B123@YES.COM</td>
					<td>A BC DEF</td>
					<td>A BC DEFgh</td>
					<td>123456789312</td> -->
					<td>Testing</td>
					<td>INR</td>
					</tr>

					
				</tbody>
			
				
			</table></div>
			
			
			
			<div class="col-sm-6 col-lg-6" style="border-radius: 8px;
			background-color: #dedbdb;">
				<form id="batchAddBene" name="batchAddBeneFile" target="output_frame" method="post" action="batchBeneUpload" enctype="multipart/form-data">
<!-- 							<input type="file" accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" /> -->
					<!-- <label>Please Select File :</label> -->
					<input name="fileUpload" id="batchFile" type="file" accept=".csv" style="margin-top: 25px">
					<input class="hideElement" name = "payId" id="fileUploadMerchantPayId">
					<input class="hideElement" name = "acquirer" id="fileUploadAcquirer">
				  <input type="submit" class="btn btn-info btn-xs" style="position: absolute;
		  left: 162px;
		  top: 75px;"  id="btnBatchUpload" disabled > 
		  
			
					<!-- <input class="hideElement" name = "timestamp" id="cbFileUploadTimeStamp" value="">
					<input class="hideElement"name="usertype" value='<s:property value="%{#session.USER.UserType.name()}"/>'>
					<input class="hideElement" name="username" value='<s:property value = "%{#session.USER.firstName}"/>'>
					<input class="hideElement" name="message" value="">
					<input class="hideElement" name="responseCode" value=""> -->
					
				</form>
				<iframe  class="hideElement" name="output_frame" src="" id="output_frame" width="XX" height="YY">
			</iframe> 
			</div>
			<div class="col-sm-2 ">
				<table
					id="example" class="resultcsv" style="display: none;">
					<thead>
						<tr>
							<th>Id</th>
							<th>Beneficiary code</th>
							<th>Payment type</th>
							<th>Response Message</th>
							<th>Status</th>
							<th>Reason</th>
						</tr>
					</thead>
					<tbody id="addBeneResultCsv">
					
					</tbody>	
				</table>
				
				</div>
			<span id="batchFileUploadError" style="display: none;" class="invocspan"></span>
	 
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
</div>
  <div class="modal fade" id="payoutStatus" role="dialog">
	<div class="modal-dialog">

	  <!-- Modal content-->
	  <div class="modal-content">
		<div class="modal-header">

		</div>
		<div class="modal-body">
		  <p class="payout" id="statusPayout"></p>
		</div>
		<div class="modal-footer" id="modal_footer">
		  <button type="button" class="btn btn-primary btn-round mt-4 submit_btn" data-dismiss="modal">Ok</button>
		</div>
	  </div>

	</div>
  </div>
  <!-- <s:select headerKey="ALL" headerValue="Select Payment Type" class="input-control" style="display: none;"
										list="@com.pay10.commons.util.NodalPaymentTypesYesBankFT3@values()"
										listValue="name" listKey="code" name="nodalPaymentType"
										id="nodalPaymentType" autocomplete="off" value=""/> -->
										<script type="text/javascript">
											$('#getNodalBalance').click(function() {
												$('#nodalBalanceError').html("");
												$('#nodalBalance').val("");
												//$('#loader-wrapper').show();
												document.getElementById("loadingInner").style.display = "block";	
												$("#getNodalBalance").prop("disabled", true);
												$.ajax({
													url : "yesBankFT3NodalBalance",
													type : "post",
													data : {
														acquirer : "YESBANKFT3"
													},
													success : function(data) {
													//	$('#loader-wrapper').hide();
														document.getElementById("loadingInner").style.display = "none";
														$("#getNodalBalance").prop("disabled", false);
														if(data.response.toLowerCase() == 'success'){
															$('#nodalBalance').val(data.currencyCode + " " +data.balance);	
														}else {
															$('#nodalBalanceError').html("Unable to fetch balance. Please try again later.");
														}
														
													},
													error : function(error) {
														//$('#loader-wrapper').hide();
														document.getElementById("loadingInner").style.display = "none";
														$("#getNodalBalance").prop("disabled", false);
														//console.log(error);
														$('#nodalBalanceError').html("Unable to fetch balance. Please try again later.");
													}
												});
											});
										</script>
										<script>
											$(document).ready(function(){
										
											$('#closeBtn').click(function(){
												$('#popup').hide();
											});
											$('#closeBtn1').click(function(){
												$('#popup').hide();
											});
											
											$('#confirmBtn').click(function(){
												startPayment();
											});
											
										
										});
										function checkInputComment(event,getindex) {	
											    var regex = /^[0-9{1}a-zA-Z{1}\b\+:@,.\-]+$/;	
											    var key = String.fromCharCode(!event.charCode ? event.which	
											            : event.charCode);	
											    if (!regex.test(key)) {	
													event.preventDefault();	
													//document.getElementById("payBtn"+getindex).disabled = true;
											        return false;	
												}	
												
											}
										</script>

</body>
</html>