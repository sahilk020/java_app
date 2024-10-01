/**
 * @author shubhamchauhan 
 */

$(document).ready(function() {
	setInterval(function(){
		if($('#chargebackStatusTd').html() == 'Closed'){
			return;
		}
		getChargebackChat();
	}, 5000);
	if ($(document.getElementById("caseStatus")).length) {
		$(caseStatus).find("option").eq(0).remove();
	}

	$('#materialSendIcon').on('click', function() {
		if ($('#chargebackAddMessage').val() != undefined
				&& $('#chargebackAddMessage').val() != '') {
			addMessageToChat();
		}
	});
	
	let userType = $('#cbUserType').val();
	console.log('user type : ' + userType)
	if(userType == "MERCHANT" || userType == "SUBUSER"){
		let chargebackStatus = $('#caseStatus').val();
		
		if(chargebackStatus == 'Closed'){
			$('#chargebackStatusTd').html(chargebackStatus);
			$('#chargebackAddMessageSpan').remove();
			$('#materialSendIconUplaod').remove();
		}
		else{
			$('#caseStatus').children().each(function(index, item){
				if(item.value == "Closed"){
					item.remove();
				}
				else if(item.value != "Accepted"){
					$('#caseStatus').children().eq(index).attr('disabled','true');
				}
			});
		}
	}
	
	$("#cbFile").on('change', function() {

		let completeFileName = $('#cbFile').val();
		let fileNameArr = completeFileName.split("\\");
		let fileName = fileNameArr[fileNameArr.length -1];	
		console.log(fileName);
		
		let fileSize = ($("#cbFile")[0].files[0].size / (1024 * 1024));
		if(fileSize > 10){
			$("#cbFile").val('');
			alert("Invalid File size");
			return;
		}
		
		let isValidFileName = validateFileName(fileName);
		let isValidFileExtensionPdf = validatePdfFileExtension(fileName);
		let isValidFileExtensionJpeg = validateJpegFileExtension(fileName);
		let isValidFileExtensionJpg = validateJpgFileExtension(fileName);
		let isValidFileExtensionPng = validatePngFileExtension(fileName);
		
		console.log("Is valid file name : " + isValidFileName);
		
		if(!(fileName.length >=1 && fileName.length <= 50)){
			alert("Invalid Filename length.");
			$("#cbFile").val('');
			return;
		}
		
		if(!isValidFileName){
			alert("Invalid FileName.");
			$("#cbFile").val('');
			return;
		}
		
		if(!isValidFileExtensionPdf  && !isValidFileExtensionJpeg  && !isValidFileExtensionJpg  && !isValidFileExtensionPng){
			alert("Invalid File extension.");
			$("#cbFile").val('');
			return;
		}
		

		$('#cbFileUploadTimeStamp').val(getCurrentTimeStamp());
	    document.getElementById("saleform").submit();
	    $("#cbFile").val('');
	    getChargebackChat();
	  });
	getChargebackChat();
	$('#chargebackAddMessage').val('');
	// $('#cbNetAmount').html('100000.00');
	$('#cbAuthorizedAmount').html(inrFormat($('#cbAuthorizedAmount').html()));
	$('#cbCapturedAmount').html(inrFormat($('#cbCapturedAmount').html()));
	$('#cbChargebackAmount').html(inrFormat($('#cbChargebackAmount').html()));
	$('#cbNetAmount').html(inrFormat($('#cbNetAmount').html()));
	
});

function getChargebackChat() {
	let cbId = $('#chargebackId').val();
	$.ajax({
		url: "getchargebackchat",
		type: "get",
		data : {
			cbId: cbId
		},
		success: function(data) {
			
			let oldChatLen = $('#cbOldChatLength').val();
			let newChat = JSON.parse(data.cbChat);
			if((oldChatLen == undefined) || (oldChatLen == '') || (oldChatLen != newChat.length)){
				$('#chargebackChatHidden').val(data.cbChat);
				displayChat();
			}
			
			
		}, error: function(){
			console.log("Failed to get chat...");
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
		let outDiv = $('<div/>').addClass("chargebackChatBoxMessages").attr('tabindex', '-1');
		let div = $("<div/>");
		let pTag = $("<pre/>");
		let spanTag = $("<span/>").addClass('time-right');
		let chatOwner = value.usertype;

		if (chatOwner == 'MERCHANT' || chatOwner == "SUBUSER") {
			div.addClass("merchantMsg");
		} else if (chatOwner == 'ADMIN' || chatOwner == "SUBADMIN") {
			div.addClass("adminMsg");
		}
		if(value.message != '' || value.filename != ''){
			if(value.filename != '' && value.filename != undefined){
				div.addClass("filedownload");
				let fileName = value.filename;
				if(fileName.length > 15){
					fileName = fileName.substring(0,12) + '...';
				}
				let tag = fileName + '<a href="cbFileDownload?fileName='+value.completefilename+'&cbId='+cbId+'"><i class="fa fa-download" aria-hidden="true"></i></a>';
				let tooltip = $('<span/>').addClass('tooltiptext').append(value.filename);
				pTag.html(tag).append(tooltip);
				spanTag.html(value.timestamp + " - " + value.username);
				div.append(pTag).append(spanTag);
			}else{
				pTag.html(value.message);
				spanTag.html(value.timestamp + " - " + value.username);
				div.append(pTag).append(spanTag);
			}				
			outDiv.append(div);
			chargebackChatbox.append(outDiv);
		}
	});
	 $('#cbOldChatLength').val(chatObj.length);
	 $('#chargeBackChatBox').append('<li class="right clearfix" style="display:none;">Hallo</li>');
	  // ajax request deleted
	  setTimeout(updateChat(), 1000);
	 function updateChat() {
		  var height = document.getElementById('chargeBackChatBox').scrollHeight; - $('#chargeBackChatBox').height(); 
		  $('#chargeBackChatBox').scrollTop(height);
		}
	/* if($(".chargebackChatBoxMessages").length > 0)
		 $(".chargebackChatBoxMessages").last()[0].scrollIntoView({ behavior: 'smooth', block: 'nearest'});*/
}

function submitForm() {
	// update form
	let cbId = $('#chargebackId').val();
	
	let cbStatus = $('#caseStatus').val();
	$.ajax({
		url: "updateChargeback",
		type: "POST" ,
		data : {
			cbId: cbId,
			cbStatus : cbStatus
		},
		success: function(data) {
			if(data.responseCode == 200){
				$('#chargebackUpdateSuccess').modal('show');
				$('#cbUpdateDate').html(data.updateDate);
			}
		}, error: function () {
			$('#chargebackUpdateFail').modal('show');
			console.log("Failed to update chargeback...");
		}
	});
	
}

Number.prototype.padLeft = function(base, chr) {
	let len = (String(base || 10).length - String(this).length) + 1;
	return len > 0 ? new Array(len).join(chr || '0') + this : this;
}

function addMessageToChat() {
	let message = $('#chargebackAddMessage').val();
//	let chatObj = JSON.parse(chat);
	
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
	
	let cbId = $('#chargebackId').val();
	
	let cbFile = undefined ;
	
	$.ajax({
		url : 'cbAddMessage',
		type : "POST",
		data : {
			message : messageObject,
			cbId : cbId,
			cbFile : cbFile
		},
		success : function(data) {
			if(data.responseCode == 200){
				$('#chargebackAddMessage').val('');
				getChargebackChat();
			}else{
				alert("unable to add message");
			}
		},
		error : function() {
			console.log("Added message unsuccessfully...");
		}
	});
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
