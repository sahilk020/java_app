/**
 * @author shubhamchauhan
 */
$(document).ready(function() {
	bindChargebackEvents();
	displayChat();
	validateCreateButton();
});

function bindChargebackEvents() {
	let chat = '[]';
	$('#chargebackChatHidden').val(chat);
	$('#chargebackSearchButton').on('click',function() {
				let pgRefNum = $('#chargebackPgRefNum').val();
				let acqId = $('#chargebackAcqId').val();
				let orderId = $('#chargebackOrderId').val();
				let emailId = $('#chargebackEmailId').val();
				if ((pgRefNum == "" || pgRefNum == undefined)
						&& (acqId == "" || acqId == undefined)
						&& (orderId == "" || orderId == undefined)
						&& (emailId == "" || emailId == undefined)) {
					setTimeout(function(){
						alert("Fill atleast 1 Field.");
					},100);
					return;
				}
				$('#chargebackSearchTableTBody').html('');
				// Clear fields.
				document.getElementById("chargebackType").selectedIndex=0;
				$('#chargebackCaseId').val('');
				getChargebackTempChat();
				$('#chargebackAddMessage').val('');
				loadChargebackTable(pgRefNum, acqId, orderId, emailId, $('#chargebackType').val());
				validateCreateButton();
			});

	$('#chargebackType').on('change', function() {
		let val = $(this).val();

		if (!($('.chargebackRowClass').hasClass('isRadioButtonClicked'))) {
			console.log("Select atleast 1 transaction");
			return;
		}
		let txnIndex = $('.isRadioButtonClicked').parent().parent().index();
		let responseCode = $(".cbresponsecode").eq(txnIndex).html();
		let responseMsg = $(".cbresponsemessage").eq(txnIndex).html();
		let caseId = $(".cbcaseid").eq(txnIndex).html();

		if (val == 'Charge Back' || val == "Fraud Disputes") {
			if (caseId == undefined || caseId == '') {
				return;
			}
			
//			$('#chargebackCaseId').val('');
//			$('#chargebackCaseId').removeAttr('readonly');
			validateCreateButton();
			alert(responseMsg);
		}
		// If pre arbitration is selected then show case id as read only.
		else if (val == 'Pre Arbitration') {
			if (caseId == undefined || caseId == '') {
				return;
			}
			$('#chargebackCaseId').val(caseId);
			$('#chargebackCaseId').attr('readonly', "readonly");
			validateCreateButton();
			alert(responseMsg);
		}
	});

	$('#materialSendIcon').on('click', function() {
		if ($('#chargebackAddMessage').val() != undefined
				&& $('#chargebackAddMessage').val() != '') {
			addMessageToChat();
		}
	});

	$('#createChargebackButton').on('click', function() {
		$('#createChargebackButton').addClass('disabled');
		let chatMessage = $('#chargebackAddMessage').val();
		if ($('#chargebackAddMessage').val() != undefined
				&& $('#chargebackAddMessage').val() != '') {
			if (confirm("Do you want to put this message into chatbox")) {
				addMessageToChat();
			}
		}

		// Call api for creating chargeback.
		let targetDate = $('#dateTo').val();
		let cbRaiseDate = $('#dateFrom').val(); // Date given by
												// bank.
		let chargebackType = $('#chargebackType').val();
		let caseId = $('#chargebackCaseId').val();
		let chatMessages = $('#chargebackChatHidden').val();

		let txnIndex = $('.isRadioButtonClicked').parent().parent().index();
		if(txnIndex == undefined || txnIndex < 0){
			alert("Txn not selected or Invalid Txn");
			$('#createChargebackButton').removeClass('disabled');
			return;
		}
		let pgRefNum = $('.cbpgrefnum').eq(txnIndex).html();
		let businessName = $('.cbbusinessname').eq(txnIndex).html();
		let payId = $('.cbpayid').eq(txnIndex).html(); // Merchant Id change to pay id.
		let orderId = $('.cborderid').eq(txnIndex).html();
		let acqId = $('.cbacqid').eq(txnIndex).html();
		let cbStatus = $('.cbstatus').eq(txnIndex).html();
		let custEmail = $('.cbemail').eq(txnIndex).html();
		let custPhone = $('.cbcustomerphone').eq(txnIndex).html();
		let amount = $('.cbamount').eq(txnIndex).html();
		let transactionId = $('.cbtransactionstring').eq(txnIndex).html();
		let txnDate = $('.cbcreatedate').eq(txnIndex).html();

		let pgTdr = $('.cbpgtdrsc').eq(txnIndex).html();
		let acqTdr = $('.cbacquirertdrcalculate').eq(txnIndex).html();
		let pgGst = $('.cbpggst').eq(txnIndex).html();
		let acqGst = $('.cbacquirergst').eq(txnIndex).html();
		let currencyCode = $('.cbcurrency').eq(txnIndex).html();
		let paymentType = $('.cbpaymentmethods').eq(txnIndex).html();
		let mopType = $('.cbmoptype').eq(txnIndex).html();
		let cardMask = $('.cbcardmask').eq(txnIndex).html();
		let internalCardIssuerBank = $('.cbinternalcardissuerbank').eq(txnIndex).html();
		let internalCardIssuerCountry = $('.cbinternalcardissuercountry').eq(txnIndex).html();
		let custCountry = $('.cbcustomercountry').eq(txnIndex).html();
		let internalCustIp = $('.cbinternalcustip').eq(txnIndex).html();


		if (pgRefNum == undefined || pgRefNum == ''){
			$('#createChargebackButton').removeClass('disabled');
			alert("Invalid PG REF NUM");
			
			return;
		}
		
		if (businessName == undefined || businessName == ''){
			$('#createChargebackButton').removeClass('disabled');
			alert("Invalid Business Name");
			return;
		}
		
		if (payId == undefined || payId == ''){
			$('#createChargebackButton').removeClass('disabled');
			alert("Invalid Pay ID");
			return;
		}
		if (orderId == undefined || orderId == ''){
			$('#createChargebackButton').removeClass('disabled');
			alert("Invalid Order ID");
			return;
		}
		if (cbStatus == undefined || cbStatus == ''){
			$('#createChargebackButton').removeClass('disabled');
			alert("Invalid Txn Status");
			return;
		}
		if (amount == undefined || amount == ''){
			$('#createChargebackButton').removeClass('disabled');
			alert("Invalid Amount");
			return;
		}
		if (transactionId == undefined || transactionId == ''){
			$('#createChargebackButton').removeClass('disabled');
			alert("Invalid Txn ID");
			return;
		}
		if (txnDate == undefined || txnDate == ''){
			$('#createChargebackButton').removeClass('disabled');
			alert("Invalid Txn Date");
			return;
		}
		
		if (pgTdr == undefined || pgTdr == ''){
			$('#createChargebackButton').removeClass('disabled');
			alert("Invalid PG TDR");
			return;
		}
		if (acqTdr == undefined || acqTdr == ''){
			$('#createChargebackButton').removeClass('disabled');
			alert("Invalid Acquirer TDR");
			return;
		}

		if (pgGst == undefined || pgGst == ''){
			$('#createChargebackButton').removeClass('disabled');
			alert("Invalid PG GST");
			return;
		}
		
		if (acqGst == undefined || acqGst == ''){
			$('#createChargebackButton').removeClass('disabled');
			alert("Invalid ACQ GST");
			return;
		}
		if (currencyCode == undefined || currencyCode == ''){
			$('#createChargebackButton').removeClass('disabled');
			alert("Invalid Currency Code");
			return;
		}
		if (paymentType == undefined || paymentType == ''){
			$('#createChargebackButton').removeClass('disabled');
			alert("Invalid Payment Type");
			return;
		}
		if (mopType == undefined || mopType == ''){
			$('#createChargebackButton').removeClass('disabled');
			alert("Invalid Mop Type");
			return;
		}
		
		if(cardMask == undefined){
			cardMask = '';
		}

		$.ajax({
			type : 'POST',
			url : 'createchargeback',
			data : {
				targetDate : targetDate,
				cbRaiseDate : cbRaiseDate, // Date given by bank.
				chargebackType : chargebackType,
				transactionId : transactionId,
				caseId : caseId,
				chargebackChat : chatMessages,
				txnDate : txnDate,
				pgRefNum : pgRefNum,
				businessName : businessName,
				payId : payId,
				orderId : orderId,
				acqId : acqId,
				cbStatus : cbStatus,
				custEmail : custEmail,
				custPhone : custPhone,
				amount : amount,
				pgTdr : pgTdr,
				acqTdr : acqTdr,
				pgGst : pgGst,
				acqGst : acqGst,
				currencyCode : currencyCode,
				paymentType : paymentType,
				mopType : mopType,
				cardMask : cardMask,
				internalCardIssuerBank : internalCardIssuerBank, 
				internalCardIssuerCountry : internalCardIssuerCountry,
				custCountry : custCountry,
				internalCustIp : internalCustIp
			},
			success : function(data) {
				if (data.responseCode > 100 && data.responseCode <= 110) {
					$('#chargebackErrorField').html(data.responseMessage);
					$('#chargebackErrorFieldModal').modal('show');
				} else if (data.responseCode == 200) {
					$('#createChargebackButton').remove();
					$('#reloadCreateChargeback').show();
					$('#chargebackErrorField').html(data.responseMessage);
					
					clearTempFolder(payId);
					// Clear fields.
					$('#chargebackPgRefNum').val('');
					$('#chargebackAcqId').val('');
					$('#chargebackOrderId').val('');
					$('#chargebackEmailId').val('');
					$('#chargebackCaseId').val('');
					$('#chargebackSearchTableTBody').html('');
					document.getElementById("chargebackType").selectedIndex=0;
					$('#chargeBackChatBox').html(''); // clear chargeback chat.
					//getChargebackTempChat();
					$('#chargebackAddMessage').val('');
					// clear error fields.
					document.getElementById("validOrderValue").style.display = "none";
					document.getElementById("validEmailValue").style.display= "none";
					document.getElementById("validValue").style.display = "none";
					document.getElementById("validAcqValue").style.display = "none";
					// clear case id error.
					document.getElementById("validCaseValue").style.display = "none";
					document.getElementById("chargebackSearchButton").disabled = false;

					$('#chargebackErrorFieldModal').modal('show');
				}else{
					$('#chargebackErrorField').html("Unable to create chargeback");
					$('#chargebackErrorFieldModal').modal('show');
				}
				$('#createChargebackButton').removeClass('disabled');
			},
			error : function() {
				console.log("Failed to create chargeback...");
				$('#createChargebackButton').removeClass('disabled');
			}
		});
	});

	$('#reloadCreateChargeback').on('click', function() {
		location.reload();
	});

	$('#chargebackCaseId').on('keyup', function(e) {
		validateCreateButton();
	});
	
	$('#materialSendIconUplaod').on('click', function(){
		$('#cbErrorField').html('');
	});
	
	// Failure Graph button
	/*$('#createFailureGraph').on('click', function(){
		
		let transFrom = $.datepicker.parseDate('dd-mm-yy', $('#dateFrom').val());
		let transTo = $.datepicker.parseDate('dd-mm-yy', $('#dateTo').val());
		if (transFrom == null || transTo == null) {
			alert('Enter date value');
			return false;
		}

		console.log("Trans from : " + transFrom);
		console.log("Trans to : " + transTo);
		if (transFrom > transTo) {
			$('#loader-wrapper').hide();
			alert('From date must be before the to date');
			$('#dateFrom').focus();
			return false;
		}
		
		let dateFrom = $('#dateFrom').val();
		let dateTo = $('#dateTo').val();
		
		$.ajax({
			type: 'GET',
			url : 'getfailuredata',
			data: {
				dateFrom : dateFrom,
				dateTo : dateTo,
				
			},
			success: function(data){
				console.log("Successfully fetched graph data.");
				console.log(data);
			},
			error: function(error){
				console.log('Failed to get data.');
			}
		});
	});*/

}

function getChargebackTempChat(){
	let txnIndex = $('.isRadioButtonClicked').parent().parent().index();
	let payId = $('.cbpayid').eq(txnIndex).html();
	if(payId == undefined || txnIndex == -1){
		return;
	}
	$.ajax({
		url : "getTempCbChat",
		type : "get",
		data : {
			payId : payId
		},
		success : function(data){
			$('#chargebackChatHidden').val(data.cbTempChat);
			displayChat();
		},
		error : function(){
			console.log("failed to load chargeback chat...");
		}
	});
}

function validateCreateButton() {

	let validRow = false;

	if ($('.chargebackRowClass').hasClass('isRadioButtonClicked')) {
/*		let rowObj = $('.isRadioButtonClicked').parent().parent();
		let pgRefNum = rowObj.children().eq(1).html();
		let payId = rowObj.children().eq(3).html(); // Merchant Id change to pay id.
		let orderId = rowObj.children().eq(4).html();
		let acqId = rowObj.children().eq(5).html();
		let custEmail = rowObj.children().eq(7).html();
		let custPhone = rowObj.children().eq(8).html();
		let amount = rowObj.children().eq(9).html();
		let transactionId = rowObj.children().eq(10).html();
		let txnDate = rowObj.children().eq(11).html();

		let pgTdr = rowObj.children().eq(12).html();
		let acqTdr = rowObj.children().eq(13).html();
		let pgGst = rowObj.children().eq(14).html();
		let acqGst = rowObj.children().eq(15).html();
		let currencyCode = rowObj.children().eq(16).html();
		let paymentType = rowObj.children().eq(17).html();
		let mopType = rowObj.children().eq(18).html();*/
		validRow = true;
		/*if ((pgRefNum != undefined && pgRefNum != '')
				&& (payId != undefined && payId != '')
				&& (orderId != undefined && orderId != '')
				&& (acqId != undefined && acqId != '')
				&& (amount != undefined && amount != '')
				&& (transactionId != undefined && transactionId != '')
				&& (txnDate != undefined && txnDate != '')) {
			validRow = true;
		}*/
	}

	let validData = false;
	let targetDate = $('#dateTo').val();
	let cbRaiseDate = $('#dateFrom').val(); // Date given by bank.
	let chargebackType = $('#chargebackType').val();
	let caseId = validateCaseId();

	if ((targetDate != undefined && targetDate != '')
			&& (cbRaiseDate != undefined && cbRaiseDate != '')
			&& (chargebackType != undefined && chargebackType != '')
			&& (caseId)) {
		validData = true;
	}

	if (validRow && validData) {
		$('#createChargebackButton').removeClass('disabled');
	} else {
		$('#createChargebackButton').addClass('disabled');
	}
}

function loadChargebackTable(pgRefNum, acqId, orderId, emailId, cbType) {
	// Get Transaction list...
	$('#chargebackSearchButton').attr('disabled','true');
	$.ajax({
		type : "GET",
		url : "loadTransactionsForCB",
		data : {
			pgRefNum : pgRefNum,
			acqId : acqId,
			orderId : orderId,
			emailId : emailId,
			cbType : cbType
		},
		success : function(data) {
			if (data.responsecode == 200) {
				let result = data.aaData;
				if (result.length == 0) {
					let tbody = $('#chargebackSearchTableTBody');
					let tr = $('<tr/>');
					let td = $('<td/>').attr('colspan', '10').css({
						'background' : '#e6e6ff',
						'text-align' : 'center',
						'font-size' : '14px'
					}).html('No transaction found');
					tr.append(td);
					tbody.append(tr);
				} else {
					populateChargebackTransactionTable(result);
					if (result.length == 1 && emailId == '') {
						if (result[0].responseMessage != "") {
							setTimeout(function(){
								alert(result[0].responseMessage);
							},100);
						}
					}
				}
			} else {
				alert("Unable to load transactions");
			}
			$('#chargebackSearchButton').removeAttr('disabled');
		},
		error : function(error) {
			alert("Unable to load transactions");
			$('#chargebackSearchButton').removeAttr('disabled');

		}
	});
}

function addMessageToChat() {
	let message = $('#chargebackAddMessage').val();
	let name = $('#cbUsername').val();
	let owner = $('#cbUserType').val();

	// Replace \ while adding messages with \\.
	message = message.replace(/[\\]/g, "\\\\"); 
	// Replace ' while adding messages with \'.
	message = message.replace(/[']/g, "\\'"); 
	// Replace new line with \n.
	message = message.replace(/[\n]/g, '\\n');
	let messageObject = "{'usertype' : '" + owner + "', 'message' : '"
			+ message + "', 'username' : '" + name + "', 'timestamp' : '"
			+ getCurrentTimeStamp() + "', 'filename' : '', 'completefilename' : '', 'filesize' : ''}";
	
	let txnIndex = $('.isRadioButtonClicked').parent().parent().index();
	let payId = $('.cbpayid').eq(txnIndex).html();
	if(payId == undefined || txnIndex == -1){
		alert("Select atleast 1 transaction");
		return;
	}
	let cbFile = undefined ;
	$.ajax({
		url : 'cbTempAddMessage',
		type : "POST",
		data : {
			message : messageObject,
			payId : payId,
			cbFile : cbFile
		},
		success : function(data) {
			if(data.responseCode == 200){
				$('#chargebackAddMessage').val('');
				getChargebackTempChat();
			}else{
				alert("Unable to add message");
			}
		},
		error : function() {
			alert("Unable to add message");
		}
	});
}

function displayChat() {
	let chat = $('#chargebackChatHidden').val();
	let cbId = $('#chargebackId').val();
	let chatObj = JSON.parse(chat);

	let chargebackChatbox = $("#chargeBackChatBox");
	chargebackChatbox.html('');
	$.each(chatObj, function(index, value) {
		let outDiv = $('<div/>').addClass("chargebackChatBoxMessages").attr(
				'tabindex', '-1');
		let div = $("<div/>");
		let pTag = $("<pre/>");
		let spanTag = $("<span/>").addClass('time-right');
		let chatOwner = value.usertype;

		if (chatOwner == 'MERCHANT') {
			div.addClass("merchantMsg");
		} else if (chatOwner == 'ADMIN' || chatOwner == "SUBADMIN") {
			div.addClass("adminMsg");
		}
		if (value.message != '' || value.filename != '') {
			if (value.filename != '' && value.filename != undefined) {
				div.addClass("filedownload");
				// let tag = value.filename + '<a
				// href="/crm/jsp/cbFileDownload?fileName='+value.completefilename+'&cbId='+cbId+'"><i
				// class="fa fa-download" aria-hidden="true"></i></a>';
				let tag = value.filename;
				pTag.html(tag);
				spanTag.html(value.timestamp + " - " + value.username);
				div.append(pTag).append(spanTag);
			} else {
				pTag.html(value.message);
				spanTag.html(value.timestamp + " - " + value.username);
				div.append(pTag).append(spanTag);
			}
			outDiv.append(div);
			chargebackChatbox.append(outDiv);
		}
	});
	$('#chargeBackChatBox').append(
			'<li class="right clearfix" style="display:none;">Hallo</li>');
	// ajax request deleted
	setTimeout(updateChat(), 1000);
	function updateChat() {
		var height = document.getElementById('chargeBackChatBox').scrollHeight;
		-$('#chargeBackChatBox').height();
		$('#chargeBackChatBox').scrollTop(height);
	}
	// if($(".chargebackChatBoxMessages").length > 0)
	// $(".chargebackChatBoxMessages").last()[0].scrollIntoView({ behavior:
	// 'smooth', block: 'nearest'});
}

function bindCbRadioButtonEvents() {
	$('.chargebackRowClass').on('click', function() {
		let txnIndex = $(this).parent().parent().index();
		let selectedTxnIndex = $('.isRadioButtonClicked').parent().parent().index();
		
		if(txnIndex == selectedTxnIndex){
			return;
		}
		
		$('.isRadioButtonClicked').removeClass('isRadioButtonClicked');
		$(this).addClass('isRadioButtonClicked');
		
		// Clear fields.
		$('#chargebackCaseId').val('');
		$('#chargebackCaseId').removeAttr('readonly');
//		getChargebackTempChat();
		$('#chargeBackChatBox').html(''); // clear chargeback chat
		$('#chargebackAddMessage').val('');
		
		// remove temp files for chargeback.
		let payId = $('.cbpayid').eq(txnIndex).html();
		if(payId != undefined && txnIndex != -1){
			clearTempFolder(payId);
		}
		
		let caseId = $('.cbcaseid').eq(txnIndex).html();
		if (caseId != undefined && caseId != '') {
			$('#chargebackCaseId').val(caseId);
			$('#chargebackCaseId').attr('readonly', "readonly");
		}
		validateCreateButton();
	});
}

function clearTempFolder(payId){
	$.ajax({
		type: "get",
		url : "clearTempFolder",
		data : {
			payId : payId
		},
		success: function(data){
		},
		error: function(error){
			console.log("Failed to clean temp folder.");
		}
	});
}
function orderIdCharacters(event) {
	var regex = /^[0-9a-zA-Z\b\-+._ ]+$/;
var key = String.fromCharCode(!event.charCode ? event.which : event.charCode);
	if (!regex.test(key)) {
		event.preventDefault();
		return false;
	}
}

function validateOrderId() {
	document.getElementById("chargebackOrderId").value = document.getElementById("chargebackOrderId").value.trim();
	var orderId = document.getElementById("chargebackOrderId").value;
	var regex = /^[0-9a-zA-Z\b\-+._ ]+$/;
	if (orderId != "") {
		if (!regex.test(orderId) || orderId.length > 100) {
			document.getElementById("validOrderValue").style.display = "block";
			return false;
		} else {
			document.getElementById("validOrderValue").style.display = "none";
			return true;
		}
	} else {
		document.getElementById("validOrderValue").style.display = "none";
		return true;
	}
}

function validateCaseId() {
	document.getElementById("chargebackCaseId").value = document.getElementById("chargebackCaseId").value.trim();
	var orderId = document.getElementById("chargebackCaseId").value;
	var regex = /^[0-9a-zA-Z\b\-+._ ]+$/;
	
	if($('.chargebackRowClass').length <= 0){
		return true;
	}
	
	if (orderId != "") {
		if (!regex.test(orderId) || orderId.length > 100) {
			document.getElementById("validCaseValue").style.display = "block";
			return false;
		} else {
			document.getElementById("validCaseValue").style.display = "none";
			return true;
		}
	} else {
		document.getElementById("validCaseValue").style.display = "block";
		return false;
	}
}

function validateChargebackEmail() {
	document.getElementById("chargebackEmailId").value = document.getElementById("chargebackEmailId").value.trim();
	let emailField = document.getElementById("chargebackEmailId").value ;
	var regex = /^([A-Za-z0-9_\-\.\+])+\@([A-Za-z0-9_\-\+])+\.([A-Za-z]{2,4})$/;
	if(emailField != ""){
		if(!regex.test(emailField)) {
			document.getElementById("validEmailValue").style.display= "block";
			return false;
        }
		else {
			document.getElementById("validEmailValue").style.display= "none";
			return true;
		 }
	}
	else {
	    document.getElementById("validEmailValue").style.display= "none";
	    return true;
    }
}

function validPgRefNum() {
	var pgRefValue = document.getElementById("chargebackPgRefNum").value;
	var regex = /^[0-9\b]{16}$/;
	if (pgRefValue.trim() != "") {
		if (!regex.test(pgRefValue)) {
			document.getElementById("validValue").style.display = "block";
			return false;
		} else {
			document.getElementById("validValue").style.display = "none";
			return true;
		}
	} else {
		document.getElementById("validValue").style.display = "none";
		return true;
	}
}

function validAcqId() {
	document.getElementById("chargebackAcqId").value = removeSpaces(document.getElementById("chargebackAcqId").value);
	var acqIdValue = document.getElementById("chargebackAcqId").value;
	var regex = /^[a-zA-Z0-9\b]+$/;
	if (acqIdValue.trim() != "") {
		if (!regex.test(acqIdValue) || acqIdValue.length > 100) {
			document.getElementById("validAcqValue").style.display = "block";
			return false;
		} else {
			document.getElementById("validAcqValue").style.display = "none";
			return true;
		}
	} else {
		document.getElementById("validAcqValue").style.display = "none";
		return true;
	}
}

function validateSearchButton(){
	let isPgRefNumValid = validPgRefNum();
	let isAcqIdValid = validAcqId();
	let isOrderIdValid = validateOrderId();
	let isEmailIdValid = validateChargebackEmail();
	
	if(isPgRefNumValid && isAcqIdValid && isOrderIdValid && isEmailIdValid){
		document.getElementById("chargebackSearchButton").disabled = false;
	}else{
		document.getElementById("chargebackSearchButton").disabled = true;
	}
}

function removeSpaces(string) {
	//return string.split(' ').join('');
	string = string.split(' ').join('');
	string = string.split('\t').join('');
    return string;


}

function populateChargebackTransactionTable(data) {
	let tbody = $('#chargebackSearchTableTBody');
	let tr;
	let td;
	let columnsLength = $('#chargebackSearchTableTheadTr th').length;
	let i = 0;
	$.each(data,function(dataKey, value) {
		tr = $('<tr/>');
		tr.addClass('chargebackTransactionRow');
		++i;

		let radioButton = '<input type="radio" class="chargebackRowClass" id="chargebackRow'+ (i) + '" name="select">';
		let col1Td = $('<td/>').html(radioButton);
		let col2Td = $('<td/>').addClass('cbpgrefnum').html(value.pgRefNum);
		let col3Td = $('<td/>').addClass('cbbusinessname').html(value.businessName);
		let col4Td = $('<td/>').addClass('cbpayid').html(value.payId);
		let col5Td = $('<td/>').addClass('cborderid').html(value.orderId);
		let col6Td = $('<td/>').addClass('cbacqid').html(value.acqId);
		let col7Td = $('<td/>').addClass('cbstatus').html(value.status);
		let col8Td = $('<td/>').addClass('cbemail').html(value.customerEmail);
		let col9Td = $('<td/>').addClass('cbcustomerphone').html(value.customerPhone);
		let col10Td = $('<td/>').addClass('cbamount').html(value.amount);
		let col11Td = $('<td/>').addClass('hideElement cbtransactionstring').html(value.transactionIdString);
		let col12Td = $('<td/>').addClass('hideElement cbcreatedate').html(value.createDate);
		let col13Td = $('<td/>').addClass('hideElement cbpgtdrsc').html(value.pgTdrSc);
		let col14Td = $('<td/>').addClass('hideElement cbacquirertdrcalculate').html(value.acquirerTdrCalculate);
		let col15Td = $('<td/>').addClass('hideElement cbpggst').html(value.pgGst);
		let col16Td = $('<td/>').addClass('hideElement cbacquirergst').html(value.acquirerGst);
		let col17Td = $('<td/>').addClass('hideElement cbcurrency').html(value.currency);
		let col18Td = $('<td/>').addClass('hideElement cbpaymentmethods').html(value.paymentMethods);
		let col19Td = $('<td/>').addClass('hideElement cbmoptype').html(value.mopType);
		let col20Td = $('<td/>').addClass('hideElement cbcaseid').html(value.caseId);
		let col21Td = $('<td/>').addClass('hideElement cbcardmask').html(value.cardMask);
		let col22Td = $('<td/>').addClass('hideElement cbresponsecode').html(value.responseCode);
		let col23Td = $('<td/>').addClass('hideElement cbresponsemessage').html(value.responseMessage);
		let col24Td = $('<td/>').addClass('hideElement cbinternalcardissuerbank').html(value.internalCardIssusserBank);
		let col25Td = $('<td/>').addClass('hideElement cbinternalcardissuercountry').html(value.internalCardIssusserCountry);
		let col26Td = $('<td/>').addClass('hideElement cbcustomercountry').html(value.customerCountry);
		let col27Td = $('<td/>').addClass('hideElement cbinternalcustip').html(value.internalCustIP);

		tr.append(col1Td);
		tr.append(col2Td);
		tr.append(col3Td);
		tr.append(col4Td);
		tr.append(col5Td);
		tr.append(col6Td);
		tr.append(col7Td);
		tr.append(col8Td);
		tr.append(col9Td);
		tr.append(col10Td);
		tr.append(col11Td);
		tr.append(col12Td);
		tr.append(col13Td);
		tr.append(col14Td);
		tr.append(col15Td);
		tr.append(col16Td);
		tr.append(col17Td);
		tr.append(col18Td);
		tr.append(col19Td);
		tr.append(col20Td);
		tr.append(col21Td);
		tr.append(col22Td);
		tr.append(col23Td);
		tr.append(col24Td);
		tr.append(col25Td);
		tr.append(col26Td);
		tr.append(col27Td);
		tbody.append(tr);

	});
	bindCbRadioButtonEvents();
}

function validateFileName (fileName){
	var validFilename = /^[a-zA-Z0-9_.() -]+$/i.test(fileName);
	return validFilename;
}

function validatePdfFileExtension (fileName){
	var validExtension = /\.pdf$/i.test(fileName);
	return validExtension;
}

function validateJpgFileExtension (fileName){
	var validExtension = /\.jpg$/i.test(fileName);
	return validExtension;
}

function validateJpegFileExtension (fileName){
	var validExtension = /\.jpeg$/i.test(fileName);
	return validExtension;
}

function validatePngFileExtension (fileName){
	var validExtension = /\.png$/i.test(fileName);
	return validExtension;
}

