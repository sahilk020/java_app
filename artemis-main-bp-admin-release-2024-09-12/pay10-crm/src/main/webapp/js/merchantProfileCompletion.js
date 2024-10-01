/**
 * author: shubhamchauhan
 * 
 *  To do later : 
 *  Remove unnecessary parseInt() used for converting count variable.
 */
var pageReloaded = false;
$(document).ready(function() {
	setTimeout(function(){
		$(".responseMsg").html('');
	},5000);
	bindFileUploadEvents();
	
	$("#merchantOnboardFileUpload").on('change', function() {
		let completeFileName = $('#merchantOnboardFileUpload').val();
		let fileNameArr = completeFileName.split("\\");
		let fileName = fileNameArr[fileNameArr.length -1];	
		
		let isValidFileName = validateFileName(fileName);
		let isValidFileExtensionPdf = validatePdfFileExtension(fileName);
		let isValidFileExtensionJpeg = validateJpegFileExtension(fileName);
		let isValidFileExtensionJpg = validateJpgFileExtension(fileName);
		let isValidFileExtensionPng = validatePngFileExtension(fileName);
		
		
		if(!(fileName.length >=5 && fileName.length <= 50)){
			$("#merchantOnboardFileUpload").val('');
			alert("Invalid Filename length.");
			return;
		}
		
		if(!isValidFileName){
			$("#merchantOnboardFileUpload").val('');
			alert("Invalid FileName...");
			return;
		}
		
		if(!isValidFileExtensionPdf  && !isValidFileExtensionJpeg  && !isValidFileExtensionJpg  && !isValidFileExtensionPng){
			$("#merchantOnboardFileUpload").val('');
			alert("Invalid File extension");
			return;
		}
		
		let fileSize = ($("#merchantOnboardFileUpload")[0].files[0].size / (1024 * 1024));
		if(fileSize > 2){
			$("#merchantOnboardFileUpload").val('');
			alert("Invalid File size");
			return;
		}
	
		
		document.getElementById("merchantDocUploadForm").submit();
		$("#merchantOnboardFileUpload").val('');
		$('#orgType').val('');
		$('#fileNameKey').val('');
		$('#fileDescription').val('');

	});
	
	$('#parLlpOrgDirDocRows').on('click', function(){
		let orgKey = $(this).attr('orgKey');
		let tableObj = $('#parLlpOrg table tbody');
		let count = parseInt($('#'+orgKey+'DirDocRows').attr('count'));
		
		if(count >= 10){
			alert("Cannot add more than 10 partners")
			return;
		}
		
		let isEmpty = true;
		
		if(count > 0){
			let className = orgKey+'DirDocName'+(count-1);
			let data = $('.' + className);
			let dataLen = data.length;
			for(let i = 0; i < dataLen; ++i){
				if(data.eq(i).val() != ''){
					isEmpty = false;
				}
			}
		}
		
		if(isEmpty && count > 0){
			alert("Fill previous Details for partner.");
			return;
		}
		
		let docName = ["Pan card of Partner : ", "Address Proof of Partner : "];
		let docAttrName = ["PanCard", "AddressProof"];
		
		for(let i = 0; i < docName.length; ++i){
			let tr = $('<tr/>');
			let td1 = $('<td/>').addClass('uploadDocTableTd').html(docName[i] + (parseInt(count) + 1));
			let td2 = $('<td/>').addClass('uploadDocTableTd');
			let td2Input = $('<input/>').attr('readonly', 'readonly').addClass('merchantDocName parLlpOrgDirDocName'+count ).attr('id','parLlpOrg'+count + docAttrName[i] + 'DirDoc' + count);
		
			let td2Label = $('<label/>').attr('for', 'merchantOnboardFileUpload').addClass('material-icons fileUploadLabel').attr('doctype','parLlpOrg'+count + docAttrName[i] + 'DirDoc' + count).css('cursor','pointer');
			td2Label.append('<i class="fa fa-upload" id="materialSendIconUplaod" aria-hidden="true"></i>');
		
			td2.append(td2Input).append(td2Label);
			tr.append(td1).append(td2)
			tableObj.append(tr);
		}
		
		// shift add button row to last.
		tableObj.append($(this).parent().parent());
		$('#'+orgKey+'DirDocRows').attr('count', parseInt(count)+1);
		if((parseInt(count) + 1) == 10 ){
			$(this).parent().parent().remove();
		}
		unbindFileUploadEvents();
		bindFileUploadEvents();
	});

	$('#proIndOrgDirDocRows').on('click', function(){
		let orgKey = $(this).attr('orgKey');
		let tableObj = $('#proIndOrg table tbody');
		let count = parseInt($('#'+orgKey+'DirDocRows').attr('count'));
		
		if(count >= 10){
			alert("Cannot add more than 10 proprietors")
			return;
		}
		
		let isEmpty = true;

		if(count > 0){
			let className = orgKey+'DirDocName'+(count-1);
			let data = $('.' + className);
			let dataLen = data.length;
			for(let i = 0; i < dataLen; ++i){
				if(data.eq(i).val() != ''){
					
					isEmpty = false;
				}
			}
		}
		
		if(isEmpty && count > 0 && pageReloaded){
			alert("Fill previous Details for Proprietor.");
			return;
		}
		let docName = ["Pan card of Proprietor : ", "Address Proof of Proprietor : "];
		let docAttrName = ["PanCard", "AddressProof"];
		
		for(let i = 0; i < docName.length; ++i){
			let tr = $('<tr/>');
			let td1 = $('<td/>').addClass('uploadDocTableTd').html(docName[i] + (parseInt(count) + 1));
			let td2 = $('<td/>').addClass('uploadDocTableTd');
			let td2Input = $('<input/>').attr('readonly', 'readonly').addClass('merchantDocName proIndOrgDirDocName'+count ).attr('id','proIndOrg'+count + docAttrName[i] + 'DirDoc' + count);
		
			let td2Label = $('<label/>').attr('for', 'merchantOnboardFileUpload').addClass('material-icons fileUploadLabel').attr('doctype','proIndOrg'+count + docAttrName[i] + 'DirDoc' + count).css('cursor','pointer');
			td2Label.append('<i class="fa fa-upload" id="materialSendIconUplaod" aria-hidden="true"></i>');
		
			td2.append(td2Input).append(td2Label);
			tr.append(td1).append(td2)
			tableObj.append(tr);
		}
		
		// shift add button row to last.
		tableObj.append($(this).parent().parent());
		$('#'+orgKey+'DirDocRows').attr('count', parseInt(count)+1);
		if((parseInt(count) + 1) == 10 ){
			$(this).parent().parent().remove();
		}
		unbindFileUploadEvents();
		bindFileUploadEvents();
	});

	$('#tseigngoOrgDirDocRows').on('click', function(){
		let orgKey = $(this).attr('orgKey');
		let tableObj = $('#tseigngoOrg table tbody');
		let count = parseInt($('#'+orgKey+'DirDocRows').attr('count'));
		
		if(count >= 10){
			alert("Cannot add more than 10 trustees")
			return;
		}
		
		let isEmpty = true;

		if(count > 0){
			let className = orgKey+'DirDocName'+(count-1);
			let data = $('.' + className);
			let dataLen = data.length;
			for(let i = 0; i < dataLen; ++i){
				if(data.eq(i).val() != ''){
					isEmpty = false;
				}
			}
		}
		
		if(isEmpty && count > 0){
			alert("Fill previous Details for Trustee.");
			return;
		}
		
		let docName = ["Pan card of Trustee : ", "Address Proof of Trustee : "];
		let docAttrName = ["PanCard", "AddressProof"];
		
		for(let i = 0; i < docName.length; ++i){
			let tr = $('<tr/>');
			let td1 = $('<td/>').addClass('uploadDocTableTd').html(docName[i] + (parseInt(count) + 1));
			let td2 = $('<td/>').addClass('uploadDocTableTd');
			let td2Input = $('<input/>').attr('readonly', 'readonly').addClass('merchantDocName tseigngoOrgDirDocName'+count ).attr('id','tseigngoOrg'+count + docAttrName[i] + 'DirDoc' + count);
		
			let td2Label = $('<label/>').attr('for', 'merchantOnboardFileUpload').addClass('material-icons fileUploadLabel').attr('doctype','tseigngoOrg'+count + docAttrName[i] + 'DirDoc' + count).css('cursor','pointer');
			td2Label.append('<i class="fa fa-upload" id="materialSendIconUplaod" aria-hidden="true"></i>');
		
			td2.append(td2Input).append(td2Label);
			tr.append(td1).append(td2)
			tableObj.append(tr);
		}
		
		// shift add button row to last.
		tableObj.append($(this).parent().parent());
		$('#'+orgKey+'DirDocRows').attr('count', parseInt(count)+1);
		if((parseInt(count) + 1) == 10 ){
			$(this).parent().parent().remove();
		}
		unbindFileUploadEvents();
		bindFileUploadEvents();
	});

	
	
	$('#pvtPubLtdOrgDirDocRows').on('click', function(){
		let orgKey = $(this).attr('orgKey');
		let tableObj = $('#pvtPubLtdOrg table tbody');
		let count = parseInt($('#'+orgKey+'DirDocRows').attr('count'));

		if(count >= 10){
			alert("Cannot add more than 10 directors")
			return;
		}
		
		let isEmpty = true;

		if(count > 0){
			let className = orgKey+'DirDocName'+(count-1);
			let data = $('.' + className);
			let dataLen = data.length;
			for(let i = 0; i < dataLen; ++i){
				if(data.eq(i).val() != ''){
					isEmpty = false;
				}
			}
		}
		
		if(isEmpty && count > 0){
			alert("Fill previous Details for director.");
			return;
		}
		
		let docName = ["Pan card of Director : ", "Address proof of Director : "];
		let docAttrName = ["PanCard", "AddressProof"];
		
		for(let i = 0; i < docName.length; ++i){
			let tr = $('<tr/>');
			let td1 = $('<td/>').addClass('uploadDocTableTd').html(docName[i] + (parseInt(count) + 1));
			let td2 = $('<td/>').addClass('uploadDocTableTd');
			let td2Input = $('<input/>').attr('readonly', 'readonly').addClass('merchantDocName pvtPubLtdOrgDirDocName'+count ).attr('id','pvtltd'+count + docAttrName[i] + 'DirDoc' + count);
		
			let td2Label = $('<label/>').attr('for', 'merchantOnboardFileUpload').addClass('material-icons fileUploadLabel').attr('doctype','pvtltd'+count + docAttrName[i] + 'DirDoc' + count).css('cursor','pointer');
			td2Label.append('<i class="fa fa-upload" id="materialSendIconUplaod" aria-hidden="true"></i>');
		
			td2.append(td2Input).append(td2Label);
			tr.append(td1).append(td2)
			tableObj.append(tr);
		}
		
		// shift add button row to last.
		tableObj.append($(this).parent().parent());
		$('#'+orgKey+'DirDocRows').attr('count', parseInt(count)+1);
		if((parseInt(count) + 1) == 10 ){
			$(this).parent().parent().remove();
		}
		unbindFileUploadEvents();
		bindFileUploadEvents();
	});

	
	
	$('.nav-tabs > li > a').click(function(event) {
		event.preventDefault();// stop browser to take action for clicked anchor
		
		// return if organisation type is blank and selected tab is uploaded document.
		let organisationType = $('#organisationType').val();
		let prevOrgType = $('#prevOrgType').val();
		let selectedTab = $(this).parents('li').html();
		if((organisationType != prevOrgType || prevOrgType == undefined || prevOrgType == '') && selectedTab.includes("DocumentsUploads")){
			setTimeout(function(){
				$('#tabs').children().eq('4').removeClass('active');
			},0);
			alert("Select organisation type in business details tab and save it.");
			return;
		}
		
		// get displaying tab content jQuery selector
		var active_tab_selector = $('.nav-tabs > li.active > a').attr('href');

		// find actived navigation and remove 'active' css
		var actived_nav = $('.nav-tabs > li.active');
		actived_nav.removeClass('active');

		// add 'active' css into clicked navigation
		$(this).parents('li').addClass('active');

		// hide displaying tab content
		$(active_tab_selector).removeClass('active');
		$(active_tab_selector).addClass('hide');

		// show target tab content
		var target_tab_selector = $(this).attr('href');
		$(target_tab_selector).removeClass('hide');
		$(target_tab_selector).addClass('active');

		if($('.active').html().includes("DocumentsUploads")){
			populateUploadDocs();
			openFileUploadTable();
			pageReloaded = true;
		}
	});
	
	let activePage = $('#merchantOnBoardActivePage').val();
	if (activePage == "DocumentsUploads") {
		$('.nav-tabs > li').eq(4).children().click();
	}
	
		
	
	// document.ready ends
});


function unbindFileUploadEvents(){
	$('.fileUploadLabel').unbind('click');
}
function bindFileUploadEvents(){
	$('.fileUploadLabel').on('click', function(){
		let orgType = $('.showTables').attr('id');
		let fileNameKey = $(this).attr('doctype');
		let fileDescription = $(this).parent().parent().children().eq(0).html();
		
		$('#orgType').val(orgType);
		$('#fileNameKey').val(fileNameKey);
		$('#fileDescription').val(fileDescription);
		$("#merchantOnboardFileUpload").val('');
	});
}

function openFileUploadTable(){
	// pvtPubLtdOrg, parLlpOrg, proIndOrg, tseigngoOrg, freelancersOrg
	let organisationType = $('#prevOrgType').val();

	$('.uploadDocsTables').removeClass("showTables");
	$('.upload_instructions').removeClass("showTablesInstructions");
	if(organisationType == 'Proprietship'){
		$('#proIndOrg').addClass("showTables");
		$('#uploadProIndOrg').addClass("showTablesInstructions");
	}else if(organisationType == "Individual"){
		$('#proIndOrg').addClass("showTables");
		$('#uploadProIndOrg').addClass("showTablesInstructions");
	}else if(organisationType == "Private Limited"){
		$('#pvtPubLtdOrg').addClass("showTables");
		$('#uploadPvtPubLtdOrg').addClass("showTablesInstructions");
	}else if(organisationType == "Public Limited"){
		$('#pvtPubLtdOrg').addClass("showTables");
		$('#uploadPvtPubLtdOrg').addClass("showTablesInstructions");
	}else if(organisationType == "Partnership"){
		$('#parLlpOrg').addClass("showTables");
		$('#uploadParLlpOrg').addClass("showTablesInstructions");
	}else if(organisationType == "LLP"){
		$('#parLlpOrg').addClass("showTables");
		$('#uploadParLlpOrg').addClass("showTablesInstructions");
	}else if(organisationType == "NGO"){
		$('#tseigngoOrg').addClass("showTables");
		$('#uploadTseigngoOrg').addClass("showTablesInstructions");
	}else if(organisationType == "Educational Institutes"){
		$('#tseigngoOrg').addClass("showTables");
		$('#uploadTseigngoOrg').addClass("showTablesInstructions");
	}else if(organisationType == "Trust"){
		$('#tseigngoOrg').addClass("showTables");
		$('#uploadTseigngoOrg').addClass("showTablesInstructions");
	}else if(organisationType == "Society"){
		$('#tseigngoOrg').addClass("showTables");
		$('#uploadTseigngoOrg').addClass("showTablesInstructions");
	}else if(organisationType == "Freelancer"){
		$('#freelancersOrg').addClass("showTables");
		$('#uploadFreelancersOrg').addClass("showTablesInstructions");
	}else{
		alert("invalid organisation type.");
	}
	
}

function decodeHtml(html) {
	let areaElement = document.createElement("textarea");
	areaElement.innerHTML = html;
	return areaElement.value;
}

function populateUploadDocs() {
	let list = $('#onBoardList').val();
	$('#onBoardList').val('');
	if (list == undefined || list == '') {
		list = '[]';
	}
	list = decodeHtml(list);
	let js = JSON.parse(list);
	$.each(js, function(orgKey, orgValue) {
		
		const ordered = {};
		Object.keys(orgValue).sort().forEach(function(key) {
		  ordered[key] = orgValue[key];
		});
		
		$.each(ordered, function(key, value) {
			let isKeyExists = $('#'+key).length;
			if(isKeyExists == 0){
				let count = key.split('DirDoc');
				
				$('#'+orgKey+'DirDocRows').attr('count', parseInt(count[1]));
				if(orgKey == 'pvtPubLtdOrg'){
					createDirectorRowsPvtPubLtdOrg(orgKey, count[1]);
				} else if(orgKey == 'parLlpOrg'){
					createDirectorRowsparLlpOrg(orgKey, count[1]);
				} else if(orgKey == 'proIndOrg'){
					createDirectorRowsproIndOrg(orgKey, count[1]);
				} else if(orgKey == 'tseigngoOrg'){
					createDirectorRowstseigngoOrg(orgKey, count[1]);
				} 
				
			}
			
			$('#'+key).val(value.filename);
		});
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

function createDirectorRowsproIndOrg(orgKey, count) {
	let tableObj = $('#' + orgKey + ' table tbody');
	count = parseInt(count);
	let temp = count;
	for (count = 0; count <= temp; ++count) {
		
		if($('#proIndOrg' + count + 'PanCardDirDoc' + count).length > 0){
			continue;
		}
		
		let docName = ["Pan card for Proprietor : ", "Address Proof for Proprietor : "];
		let docAttrName = ["PanCard", "AddressProof"];
		
		for(let i = 0; i < docName.length; ++i){
			let tr = $('<tr/>');
			let td1 = $('<td/>').addClass('uploadDocTableTd').html(docName[i] + (parseInt(count) + 1));
			let td2 = $('<td/>').addClass('uploadDocTableTd');
			let td2Input = $('<input/>').attr('readonly', 'readonly').addClass('merchantDocName ' + orgKey + 'DirDocName'+count ).attr('id',orgKey +count + docAttrName[i] + 'DirDoc' + count);
		
			let td2Label = $('<label/>').attr('for', 'merchantOnboardFileUpload').addClass('material-icons fileUploadLabel').attr('doctype',orgKey +count + docAttrName[i] + 'DirDoc' + count).css('cursor','pointer');
			td2Label.append('<i class="fa fa-upload" id="materialSendIconUplaod" aria-hidden="true"></i>');
		
			td2.append(td2Input).append(td2Label);
			tr.append(td1).append(td2)
			tableObj.append(tr);
		}
	
		// shift add button row to last.
		tableObj.append($('#proIndOrgDirDocRows').parent().parent());
		$('#'+orgKey+'DirDocRows').attr('count', parseInt(count)+1);
		if((parseInt(count) + 1) == 10 ){
			$('#proIndOrgDirDocRows').parent().parent().remove();
		}
		unbindFileUploadEvents();
		bindFileUploadEvents();
		
	}
}


function createDirectorRowsPvtPubLtdOrg(orgKey, count) {
	let tableObj = $('#pvtPubLtdOrg table tbody');
	count = parseInt(count);
	let temp = count;
	for (count = 0; count <= temp; ++count) {
		
		if($('#pvtltd' + count + 'PanCardDirDoc' + count).length > 0){
			continue;
		}
		
		let docName = ["Pan card for director : ", "Address Proof for director : "];
		let docAttrName = ["PanCard", "AddressProof"];
		
		for(let i = 0; i < docName.length; ++i){
			let tr = $('<tr/>');
			let td1 = $('<td/>').addClass('uploadDocTableTd').html(docName[i] + (parseInt(count) + 1));
			let td2 = $('<td/>').addClass('uploadDocTableTd');
			let td2Input = $('<input/>').attr('readonly', 'readonly').addClass('merchantDocName pvtPubLtdOrgDirDocName'+count ).attr('id','pvtltd'+count + docAttrName[i] + 'DirDoc' + count);
		
			let td2Label = $('<label/>').attr('for', 'merchantOnboardFileUpload').addClass('material-icons fileUploadLabel').attr('doctype','pvtltd'+count + docAttrName[i] + 'DirDoc' + count).css('cursor','pointer');
			td2Label.append('<i class="fa fa-upload" id="materialSendIconUplaod" aria-hidden="true"></i>');
		
			td2.append(td2Input).append(td2Label);
			tr.append(td1).append(td2)
			tableObj.append(tr);
		}
		
		// shift add button row to last.
		tableObj.append($('#pvtPubLtdOrgDirDocRows').parent().parent());
		$('#'+orgKey+'DirDocRows').attr('count', parseInt(count)+1);
		
		if((parseInt(count) + 1) == 10 ){
			$('#pvtPubLtdOrgDirDocRows').parent().parent().remove();
		}
		unbindFileUploadEvents();
		bindFileUploadEvents();
	}

}

function createDirectorRowsparLlpOrg(orgKey, count) {
	let tableObj = $('#' + orgKey + ' table tbody');
	count = parseInt(count);
	let temp = count;
	for (count = 0; count <= temp; ++count) {
		if($('#parLlpOrg' + count + 'PanCardDirDoc' + count).length > 0){
			continue;
		}
		
		
		let docName = ["Pan card for Partner : ", "Address Proof for Partner : "];
		let docAttrName = ["PanCard", "AddressProof"];
		
		for(let i = 0; i < docName.length; ++i){
			let tr = $('<tr/>');
			let td1 = $('<td/>').addClass('uploadDocTableTd').html(docName[i] + (parseInt(count) + 1));
			let td2 = $('<td/>').addClass('uploadDocTableTd');
			let td2Input = $('<input/>').attr('readonly', 'readonly').addClass('merchantDocName ' + orgKey + 'DirDocName'+count ).attr('id',orgKey +count + docAttrName[i] + 'DirDoc' + count);
		
			let td2Label = $('<label/>').attr('for', 'merchantOnboardFileUpload').addClass('material-icons fileUploadLabel').attr('doctype',orgKey +count + docAttrName[i] + 'DirDoc' + count).css('cursor','pointer');
			td2Label.append('<i class="fa fa-upload" id="materialSendIconUplaod" aria-hidden="true"></i>');
		
			td2.append(td2Input).append(td2Label);
			tr.append(td1).append(td2)
			tableObj.append(tr);
		}
		// shift add button row to last.
		tableObj.append($('#parLlpOrgDirDocRows').parent().parent());
		$('#'+orgKey+'DirDocRows').attr('count', parseInt(count)+1);
		if((parseInt(count) + 1) == 10 ){
			$('#parLlpOrgDirDocRows').parent().parent().remove();
		}
		unbindFileUploadEvents();
		bindFileUploadEvents();
	}

}

function createDirectorRowstseigngoOrg(orgKey, count) {
	let tableObj = $('#' + orgKey + ' table tbody');
	count = parseInt(count);
	let temp = count;
	for (count = 0; count <= temp; ++count) {
		
		if($('#tseigngoOrg' + count + 'PanCardDirDoc' + count).length > 0){
			continue;
		}
		
		let docName = ["Pan card for Trustee : ", "Address Proof for Trustee : "];
		let docAttrName = ["PanCard", "AddressProof"];
		
		for(let i = 0; i < docName.length; ++i){
			let tr = $('<tr/>');
			let td1 = $('<td/>').addClass('uploadDocTableTd').html(docName[i] + (parseInt(count) + 1));
			let td2 = $('<td/>').addClass('uploadDocTableTd');
			let td2Input = $('<input/>').attr('readonly', 'readonly').addClass('merchantDocName ' + orgKey + 'DirDocName'+count ).attr('id',orgKey +count + docAttrName[i] + 'DirDoc' + count);
		
			let td2Label = $('<label/>').attr('for', 'merchantOnboardFileUpload').addClass('material-icons fileUploadLabel').attr('doctype',orgKey +count + docAttrName[i] + 'DirDoc' + count).css('cursor','pointer');
			td2Label.append('<i class="fa fa-upload" id="materialSendIconUplaod" aria-hidden="true"></i>');
		
			td2.append(td2Input).append(td2Label);
			tr.append(td1).append(td2)
			tableObj.append(tr);
		}
		
		// shift add button row to last.
		tableObj.append($('#tseigngoOrgDirDocRows').parent().parent());
		$('#'+orgKey+'DirDocRows').attr('count', parseInt(count)+1);
		if((parseInt(count) + 1) == 10 ){
			$('#tseigngoOrgDirDocRows').parent().parent().remove();
		}
		unbindFileUploadEvents();
		bindFileUploadEvents();
	}
}

