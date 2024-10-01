$(document).ready(function() {
	loadMerchantDocuments();	
});

function loadMerchantDocuments() {
	let docJson = $('#onBoardList').val();
//	$('#onBoardList').val('');

	let organisationType = $('#organisationType').val();

	let orgKeyName = "";
	if ((organisationType == 'Proprietship') || (organisationType == "Individual")) {
		$('#proIndOrg').addClass("showTables");
		orgKeyName = "proIndOrg";
	} else if ((organisationType == "Private Limited") || (organisationType == "Public Limited")) {
		$('#pvtPubLtdOrg').addClass("showTables");
		orgKeyName = "pvtPubLtdOrg";
	} else if ((organisationType == "Partnership") || (organisationType == "LLP")) {
		$('#parLlpOrg').addClass("showTables");
		orgKeyName = "parLlpOrg";
	} else if ((organisationType == "NGO") || (organisationType == "Educational Institutes") || (organisationType == "Trust") || (organisationType == "Society")) {
		$('#tseigngoOrg').addClass("showTables");
		orgKeyName = "tseigngoOrg";
	} else if ((organisationType == "Freelancer")) {
		$('#freelancersOrg').addClass("showTables");
		orgKeyName = "freelancersOrg";
	} else {
		console.log("Invalid organisation type : " + organisationType);
		return;
	}
	let payId = $('#payId').val();
	
	try{
		docJson = decodeHtml(docJson);
		docJson = JSON.parse(docJson);
	}
	catch(err){
		console.log("No file uploaded...");
		docJson = undefined;
	}
	
	if(docJson == undefined){
		return;
	}
	let orgValue = docJson[orgKeyName];
	
	if(orgValue == undefined){
		console.log("undefined organisation value");
		return;
	}
	
	$.each(orgValue, function(key, value) {
		let isKeyExists = $('#'+key).length;
		if(isKeyExists == 0){
			let count = key.split('DirDoc');
			if(orgKeyName == 'pvtPubLtdOrg'){
				createDirectorRowsPvtPubLtdOrg(orgKeyName, count[1]);
			} else if(orgKeyName == 'parLlpOrg'){
				createDirectorRowsparLlpOrg(orgKeyName, count[1]);
			} else if(orgKeyName == 'proIndOrg'){
				createDirectorRowsproIndOrg(orgKeyName, count[1]);
			} else if(orgKeyName == 'tseigngoOrg'){
				createDirectorRowstseigngoOrg(orgKeyName, count[1]);
			} 
		}

		$('#' + key).val(value.filename);
		let aTag = '<a class="downloadMerchantFile" href="merchantDocFileDownload?fileName='+ value.completefilename + '&payId=' + payId+ '"><i class="fa fa-download" aria-hidden="true"></i></a>';
		let inputField = $('#' + key);
		inputField.parent().append(aTag);
	});
	
	// show download all button.
//	let payId = $('#payId').val();
let downloadAllButton = "<a href='merchantDocZipFileDownload?orgKeyName="+ orgKeyName +"&payId="+ payId +"'><button type='button' class='btn btn-primary'><i class='fa fa-download' style='color:white;padding-right:14px;' aria-hidden='true'></i>Download All<button></a>"
$('#merchantDownloadAllDocs').append(downloadAllButton);
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
			td2Label.append('<i class="fa fa-download" id="materialSendIconUplaod" aria-hidden="true"></i>');
		
			td2.append(td2Input);
			tr.append(td1).append(td2)
			tableObj.append(tr);
		}
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
			td2Label.append('<i class="fa fa-download" id="materialSendIconUplaod" aria-hidden="true"></i>');
		
			td2.append(td2Input);
			tr.append(td1).append(td2)
			tableObj.append(tr);
		}
	}

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
			td2Label.append('<i class="fa fa-download" id="materialSendIconUplaod" aria-hidden="true"></i>');
		
			td2.append(td2Input);
			tr.append(td1).append(td2)
			tableObj.append(tr);
		}
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
			td2Label.append('<i class="fa fa-download" id="materialSendIconUplaod" aria-hidden="true"></i>');
		
			td2.append(td2Input);
			tr.append(td1).append(td2)
			tableObj.append(tr);
		}
	}
}

function decodeHtml(html) {
	let areaElement = document.createElement("textarea");
	areaElement.innerHTML = html;
	return areaElement.value;
}
function hideAndShowForAuto(event){
	  var autoRefund = document.getElementById("autoRefund").checked;
	  if(autoRefund==true){
			document.getElementById("auto").checked=autoRefund

			document.getElementById("autoRefundHideAndShow").style.display = "Block";


	  }else
		  {
			document.getElementById("auto").checked=autoRefund

				document.getElementById("autoRefundHideAndShow").style.display = "none";

		  }
}
