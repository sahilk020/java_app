
$(document).ready(
	function () {
		$('input:file').change(
			function () {
				if ($(this).val()) {
					$('input:submit').attr('disabled', false);
				}
			}
		);
	});
$(document).ready(function () {
	$('#select_all').click();
	$('#select_all').click();
	$('#select_all1').click();
	$('#select_all1').click();
	$('#select_all2').click();
	$('#select_all2').click();
	$('#select_all3').click();
	$('#select_all3').click();
	$('#select_all4').click();
	$('#select_all4').click();
	$('#select_all5').click();
	$('#select_all5').click();
	$('#select_all6').click();
	$('#select_all6').click();
	$('table.csv').DataTable({
		dom: 'B',
		buttons: [
			{
				extend: 'csv',
				text: 'GET SAMPLE CSV',
				filename: 'IP_ADDRESS',
			}]
	});
	$('table.csv1').DataTable({
		dom: 'B',
		buttons: [
			{
				extend: 'csv',
				text: 'GET SAMPLE CSV',
				filename: 'WHITELISTIP_ADDRESS',
			}]
	});
	$('table.csv2').DataTable({
		dom: 'B',
		buttons: [
			{
				extend: 'csv',
				text: 'GET SAMPLE CSV',
				filename: 'EMAIL_ADDRESS',
			}]
	});
	$('table.csv3').DataTable({
		dom: 'B',
		buttons: [
			{
				extend: 'csv',
				text: 'GET SAMPLE CSV',
				filename: 'CARD_RANGE',
			}]
	});
	$('table.csv4').DataTable({
		dom: 'B',
		buttons: [
			{
				extend: 'csv',
				text: 'GET SAMPLE CSV',
				filename: 'PHONE_NUMBER',
			}]
	});
	$('table.csv5').DataTable({
		dom: 'B',
		buttons: [
			{
				extend: 'csv',
				text: 'GET SAMPLE CSV',
				filename: 'CARD_MASK',
			}]
	});
});

// function validateTxnAmount(){
// 	alert("hii");
// 	if(minTransactionAmount.val == maxTransactionAmount.val){
// 		alert("value Can not be same")
// 	}
// }
var fileTypes = ['csv'];  //acceptable file types
function readURL(input) {
	if (input.files && input.files[0]) {
		var extension = input.files[0].name.split('.').pop().toLowerCase(),  //file extension from input file
			isSuccess = fileTypes.indexOf(extension) > -1;  //is extension in acceptable types

		if (isSuccess) { //yes
			var reader = new FileReader();
			reader.onload = function (e) {
				if (extension == 'csv') {
					$(input).closest('.fileUpload').find(".icon").attr('src', 'https://www.cortechslabs.com/wp-content/uploads/2017/06/CSV-icon-new.png');

				}

				else {
					//debugger;
					//console.log('here=>'+$(input).closest('.uploadDoc').length);
					$(input).val('');
					document.getElementById("up").value = "";
					//$("#up").replaceWith($("#up").val('').clone(true));
					$(input).closest('.uploadDoc').find(".docErr").slideUp('slow');

				}
			}

			reader.readAsDataURL(input.files[0]);
		}
		else {
			//debugger;
			//console.log('here=>'+$(input).closest('.uploadDoc').find(".docErr").length);
			$(input).val('');
			$(input).closest('.uploadDoc').find(".docErr").fadeIn();
			document.getElementById("up").value = "";
			setTimeout(function () {
				$('.docErr').fadeOut('slow');

			}, 9000);
		}
	}
}
$(document).ready(function () {

	$(document).on('change', '.up', function () {
		var id = $(this).attr('id'); /* gets the filepath and filename from the input */
		var profilePicValue = $(this).val();
		var fileNameStart = profilePicValue.lastIndexOf('\\'); /* finds the end of the filepath */
		profilePicValue = profilePicValue.substr(fileNameStart + 1).substring(0, 20); /* isolates the filename */
		//var profilePicLabelText = $(".upl"); /* finds the label text */
		if (profilePicValue != '') {
			//console.log($(this).closest('.fileUpload').find('.upl').length);
			$(this).closest('.fileUpload').find('.upl').html(profilePicValue); /* changes the label text */
		}
	});


	$(document).on("click", "a.btn-check", function () {
		if ($(".uploadDoc").length > 1) {
			$(this).closest(".uploadDoc").remove();
		} else {
			alert("You have to upload at least one document.");
		}
	});
});
/*Code end here for csv Upload */


/*Declare variable for each rule  */
var numberOfTransaction = 'BLOCK_NO_OF_TXNS';
var transactionAmount = 'BLOCK_TXN_AMOUNT';
var ipAddressS = 'BLOCK_IP_ADDRESS';
var emailS = 'BLOCK_EMAIL_ID';
var issuerCountry = 'BLOCK_CARD_ISSUER_COUNTRY';
var negativeBinS = 'BLOCK_CARD_BIN';
var whiteListIpAddressS = 'WHITE_LIST_IP_ADDRESS';
var phoneS = "BLOCK_PHONE_NUMBER";
var negativeCardS = 'BLOCK_CARD_NO';
var transactionAmountVelocity = 'BLOCK_TXN_AMOUNT_VELOCITY';

/* ENd here */
/*Code start for delete single rule table for each rule */
function deleteFraudRule(ruleId) {
	var confirmationFlag = confirm("Do you want to delete this rule");
	if (!confirmationFlag) {
		return false;
	}
	$.ajaxSetup({
		beforeSend: function () {
			jQuery('body').toggleClass('loaded');
		},
		complete: function () {
			jQuery('body').toggleClass('loaded');
		}
	});

	$.ajax({
		url: 'deleteFraudRule',
		type: 'post',
		data: {
			token: document.getElementsByName("token")[0].value,
			payId: fraudFieldValidate('payId', null), //TODO for merchant module
			ruleId: ruleId,
		},
		success: function (data) {

			if ((data.response) != null) {
				alert(data.response);
				window.location.reload();
			} else {
				alert("Try again, Something went wrong!")
			}
		},
		error: function (data) {
			alert(data.response);
		}
	});
}
/*Code end here for  delete single rule table for each rule */
/*Code start for Bulk search of table for each rule */
var lastSearchKey = "";
function searchData(inputId, ruleId) {

	//first will get table Id,then Rule ID
	var searchKey = '';
	$('input[type=search]').each(function () {

		if ($("#search_" + inputId).val()) {
			searchKey = $("#search_" + inputId).val();
		}
		
		

	});
	if(lastSearchKey != "" || searchKey != ""){
	$.ajax({
		url: 'rulesSearchAction',
		type: 'post',
		data: {
			token: document.getElementsByName("token")[0].value,
			rule: ruleId,
			payId: fraudFieldValidate('payId', null),
			searchString: searchKey

		},
		success: function (data) {
			if (data.fraudRuleList.length > 0) {
				$('#select_all').prop("checked", false);
				$('#select_all1').prop("checked", false);
				$('#select_all2').prop("checked", false);
				$('#select_all3').prop("checked", false);
				$('#select_all4').prop("checked", false);
				$('#select_all5').prop("checked", false);
				$('#select_all6').prop("checked", false);
				$('#select_all7').prop("checked", false);
				$('#select_all8').prop("checked", false);
				
				lastSearchKey = searchKey;


				let blockIpAddData = [];
				let whiteListIpAddData = [];
				let issuerCountryAddData = [];
				let emailListAddData = [];
				let txnAmtAddData = [];
				let cardRangeAddData = [];
				let phoneListAddData = [];
				let cardMaskAddData = [];
				let txnAmtVelocityAddData = [];
				for (var i = 0; i < data.fraudRuleList.length; i++) {
					if (data.fraudRuleList[i].fraudType == 'BLOCK_IP_ADDRESS') {
						data.fraudRuleList[i]['deleteBtn'] = "<input class='btn btn-info btn-xs deleteBtnForRule' style='margin-left:10px'  type='submit' value='Delete' onclick='deleteFraudRule(" + data.fraudRuleList[i]['id'] + ")'/>"
						data.fraudRuleList[i]['isSelected'] = "<input type='checkbox' onclick='checkboxclickcheck()' class='checkbox' name='BLOCK_IP_ADDRESS" + i + "'  value='" + data.fraudRuleList[i]['id'] + "'>"
						blockIpAddData.push(data.fraudRuleList[i]);
					}
					else if (data.fraudRuleList[i].fraudType == 'WHITE_LIST_IP_ADDRESS') {
						data.fraudRuleList[i]['deleteBtnForWL'] = "<input class='btn btn-info btn-xs deleteBtnForRule' style='margin-left:10px'  type='submit' value='Delete' onclick='deleteFraudRule(" + data.fraudRuleList[i]['id'] + ")'/>"
						data.fraudRuleList[i]['isSelectedWL'] = "<input type='checkbox' onclick='checkboxclickcheck1()' class='checkbox1' name='WHITE_LIST_IP_ADDRESS" + i + "'  value='" + data.fraudRuleList[i]['id'] + "'>"
						whiteListIpAddData.push(data.fraudRuleList[i]);
					}
					else if (data.fraudRuleList[i].fraudType == 'BLOCK_CARD_ISSUER_COUNTRY') {
						data.fraudRuleList[i]['deleteBtnForIssuerCountry'] = "<input class='btn btn-info btn-xs deleteBtnForRule' style='margin-left:10px'  type='submit' value='Delete' onclick='deleteFraudRule(" + data.fraudRuleList[i]['id'] + ")'/>"
						data.fraudRuleList[i]['isSelectedIssuerCountry'] = "<input type='checkbox' onclick='checkboxclickcheck2()' class='checkbox2' name='BLOCK_CARD_ISSUER_COUNTRY" + i + "'  value='" + data.fraudRuleList[i]['id'] + "'>"
						issuerCountryAddData.push(data.fraudRuleList[i]);
					}
					else if (data.fraudRuleList[i].fraudType == 'BLOCK_EMAIL_ID') {
						data.fraudRuleList[i]['deleteBtnEmail'] = "<input class='btn btn-info btn-xs deleteBtnForRule' style='margin-left:10px'  type='submit' value='Delete' onclick='deleteFraudRule(" + data.fraudRuleList[i]['id'] + ")'/>"
						data.fraudRuleList[i]['isSelectedEmail'] = "<input type='checkbox' onclick='checkboxclickcheck3()' class='checkbox3' name='BLOCK_EMAIL_ID" + i + "'  value='" + data.fraudRuleList[i]['id'] + "'>"
						emailListAddData.push(data.fraudRuleList[i]);
					}
					else if (data.fraudRuleList[i].fraudType == 'BLOCK_TXN_AMOUNT') {
						data.fraudRuleList[i]['deleteBtnTxnAmt'] = "<input class='btn btn-info btn-xs deleteBtnForRule' style='margin-left:10px'  type='submit' value='Delete' onclick='deleteFraudRule(" + data.fraudRuleList[i]['id'] + ")'/>"
						txnAmtAddData.push(data.fraudRuleList[i]);
					}
					else if (data.fraudRuleList[i].fraudType == 'BLOCK_CARD_BIN') {
						data.fraudRuleList[i]['deleteBtnCardRange'] = "<input class='btn btn-info btn-xs deleteBtnForRule' style='margin-left:10px'  type='submit' value='Delete' onclick='deleteFraudRule(" + data.fraudRuleList[i]['id'] + ")'/>"
						data.fraudRuleList[i]['isSelectedCardRange'] = "<input type='checkbox' onclick='checkboxclickcheck4()' class='checkbox4' name='BLOCK_CARD_BIN" + i + "'  value='" + data.fraudRuleList[i]['id'] + "'>"
						cardRangeAddData.push(data.fraudRuleList[i]);
					}
					else if (data.fraudRuleList[i].fraudType == 'BLOCK_PHONE_NUMBER') {
						data.fraudRuleList[i]['deleteBtnPhone'] = "<input class='btn btn-info btn-xs deleteBtnForRule' style='margin-left:10px'  type='submit' value='Delete' onclick='deleteFraudRule(" + data.fraudRuleList[i]['id'] + ")'/>"
						data.fraudRuleList[i]['isSelectedPhone'] = "<input type='checkbox' onclick='checkboxclickcheck5()' class='checkbox5' name='BLOCK_PHONE_NUMBER" + i + "'  value='" + data.fraudRuleList[i]['id'] + "'>"
						phoneListAddData.push(data.fraudRuleList[i]);
					}
					else if (data.fraudRuleList[i].fraudType == 'BLOCK_CARD_NO') {
						data.fraudRuleList[i]['deleteBtnCardNumber'] = "<input class='btn btn-info btn-xs deleteBtnForRule'  style='margin-left:10px'  type='submit' value='Delete' onclick='deleteFraudRule(" + data.fraudRuleList[i]['id'] + ")'/>"
						data.fraudRuleList[i]['isSelectedCardNumber'] = "<input type='checkbox' onclick='checkboxclickcheck6()' class='checkbox6' name='BLOCK_CARD_NO" + i + "'  value='" + data.fraudRuleList[i]['id'] + "'>"
						cardMaskAddData.push(data.fraudRuleList[i]);
					}
					else if (data.fraudRuleList[i].fraudType == 'BLOCK_TXN_AMOUNT_VELOCITY') {
						data.fraudRuleList[i]['deleteBtnTxnAmtVelocity'] = "<input class='btn btn-info btn-xs deleteBtnForRule' style='margin-left:10px'  type='submit' value='Delete' onclick='deleteFraudRule(" + data.fraudRuleList[i]['id'] + ")'/>"
						txnAmtVelocityAddData.push(data.fraudRuleList[i]);
					}
				}
				if (ruleId == 'BLOCK_IP_ADDRESS') {
					$('#ipAddListBody').DataTable().clear().destroy();
					generateBlockIdAddressTable(blockIpAddData);
				} else if (ruleId == 'WHITE_LIST_IP_ADDRESS') {
					$('#wlIpAddListBody').DataTable().clear().destroy();
					generateBlockWLIdAddressTable(whiteListIpAddData);
				} else if (ruleId == 'BLOCK_CARD_ISSUER_COUNTRY') {
					$('#issuerCountryListBody').DataTable().clear().destroy();
					generateBlockIssuerCountryTable(issuerCountryAddData);
				} else if (ruleId == 'BLOCK_EMAIL_ID') {
					$('#emailListBody').DataTable().clear().destroy();
					generateBlockEmailAddressTable(emailListAddData);
				} else if (ruleId == 'BLOCK_TXN_AMOUNT') {
					$('#txnAmountListBody').DataTable().clear().destroy();
					generateBlockTxnAmtTable(txnAmtAddData);
				} else if (ruleId == 'BLOCK_CARD_BIN') {
					$('#cardBinListBody').DataTable().clear().destroy();
					generateBlockCardRangeTable(cardRangeAddData);
				} else if (ruleId == 'BLOCK_PHONE_NUMBER') {
					$('#phoneListBody').DataTable().clear().destroy();
					generateBlockPhoneNumberTable(phoneListAddData);
				} else if (ruleId == 'BLOCK_CARD_NO') {
					$('#cardNoListBody').DataTable().clear().destroy();
					generateBlockCardMaskTable(cardMaskAddData);
				} else if (ruleId == 'BLOCK_TXN_AMOUNT_VELOCITY') {
					$('#txnAmountVelocityListBody').DataTable().clear().destroy();
					generateBlockTxnAmtVelocityTable(txnAmtVelocityAddData);
				}

			}
			else {
				alert("There are no results that match your request. Please try again.");
			}
		},

	})
	}
}
/*Code end here for Bulk upload of table for each rule */
/*Code start for Bulk delete of table for each rule */

function checkboxclickcheck() {
if ($('.checkbox:checked').length >= 2) {
	$('.bulkDeleteBtn').prop('disabled', false);
	$('.deleteBtnForRule').prop('disabled', true);
} else {
	$('.bulkDeleteBtn').prop('disabled', true);
	$('.deleteBtnForRule').prop('disabled', false);
}
}
function checkboxclickcheck1() {
	if ($('.checkbox1:checked').length >= 2) {
		$('.bulkDeleteBtn').prop('disabled', false);
		$('.deleteBtnForRule').prop('disabled', true);
	} else {
		$('.bulkDeleteBtn').prop('disabled', true);
		$('.deleteBtnForRule').prop('disabled', false);
	}
}
function checkboxclickcheck2() {
	if ($('.checkbox2:checked').length >= 2) {
		$('.bulkDeleteBtn').prop('disabled', false);
		$('.deleteBtnForRule').prop('disabled', true);
	} else {
		$('.bulkDeleteBtn').prop('disabled', true);
		$('.deleteBtnForRule').prop('disabled', false);
	}
}
function checkboxclickcheck3() {
	if ($('.checkbox3:checked').length >= 2) {
		$('.bulkDeleteBtn').prop('disabled', false);
		$('.deleteBtnForRule').prop('disabled', true);
	} else {
		$('.bulkDeleteBtn').prop('disabled', true);
		$('.deleteBtnForRule').prop('disabled', false);
	}
}
function checkboxclickcheck4() {
	if ($('.checkbox4:checked').length >= 2) {
		$('.bulkDeleteBtn').prop('disabled', false);
		$('.deleteBtnForRule').prop('disabled', true);
	} else {
		$('.bulkDeleteBtn').prop('disabled', true);
		$('.deleteBtnForRule').prop('disabled', false);
	}
}
function checkboxclickcheck5() {
	if ($('.checkbox5:checked').length >= 2) {
		$('.bulkDeleteBtn').prop('disabled', false);
		$('.deleteBtnForRule').prop('disabled', true);
	} else {
		$('.bulkDeleteBtn').prop('disabled', true);
		$('.deleteBtnForRule').prop('disabled', false);
	}
}
function checkboxclickcheck6() {
	if ($('.checkbox6:checked').length >= 2) {
		$('.bulkDeleteBtn').prop('disabled', false);
		$('.deleteBtnForRule').prop('disabled', true);
	} else {
		$('.bulkDeleteBtn').prop('disabled', true);
		$('.deleteBtnForRule').prop('disabled', false);
	}
}
						function selectall() {
							$('#select_all').on('click', function () {
								if (this.checked) {

									$('.checkbox').each(function () {
										this.checked = true;
									});
									$('.bulkDeleteBtn').prop('disabled', false);
		                            $('.deleteBtnForRule').prop('disabled', true);
								} else {

									$('.checkbox').each(function () {
										this.checked = false;

									});
									$('.bulkDeleteBtn').prop('disabled', true);
		                            $('.deleteBtnForRule').prop('disabled', false);
								}
							});
							$('.checkbox').on('click', function () {

								if ($('.checkbox:checked').length == $('.checkbox').length) {

									$('#select_all').prop('checked', true);
									// $('#select_all1').prop('checked', true);
									// $('#select_all2').prop('checked', true);
									// $('#select_all3').prop('checked', true);
									// $('#select_all4').prop('checked', true);
									// $('#select_all5').prop('checked', true);
									// $('#select_all6').prop('checked', true);
								} else {
									$('#select_all').prop('checked', false);
									// $('#select_all1').prop('checked', false);
									// $('#select_all2').prop('checked', false);
									// $('#select_all3').prop('checked', false);
									// $('#select_all4').prop('checked', false);
									// $('#select_all5').prop('checked', false);
									// $('#select_all6').prop('checked', false);
								}
							});
							$('#select_all1').on('click', function () {
								if (this.checked) {

									$('.checkbox1').each(function () {
										this.checked = true;
									});
									$('.bulkDeleteBtn').prop('disabled', false);
		                            $('.deleteBtnForRule').prop('disabled', true);
								} else {

									$('.checkbox1').each(function () {
										this.checked = false;

									});
									$('.bulkDeleteBtn').prop('disabled', true);
		                            $('.deleteBtnForRule').prop('disabled', false);
								}
							});
							$('.checkbox1').on('click', function () {
	
								if ($('.checkbox1:checked').length == $('.checkbox1').length) {

									
									$('#select_all1').prop('checked', true);
									
								} else {
									
									$('#select_all1').prop('checked', false);
								
								}
							});
							$('#select_all2').on('click', function () {
								if (this.checked) {

									$('.checkbox2').each(function () {
										this.checked = true;
									});
									$('.bulkDeleteBtn').prop('disabled', false);
		                            $('.deleteBtnForRule').prop('disabled', true);
								} else {

									$('.checkbox2').each(function () {
										this.checked = false;

									});
									$('.bulkDeleteBtn').prop('disabled', true);
		                            $('.deleteBtnForRule').prop('disabled', false);
								}
							});
							$('.checkbox2').on('click', function () {
	
								if ($('.checkbox2:checked').length == $('.checkbox2').length) {

									
									$('#select_all2').prop('checked', true);
									
								} else {
									
									$('#select_all2').prop('checked', false);
								
								}
							});
							$('#select_all3').on('click', function () {
								if (this.checked) {

									$('.checkbox3').each(function () {
										this.checked = true;
									});
									$('.bulkDeleteBtn').prop('disabled', false);
		                            $('.deleteBtnForRule').prop('disabled', true);
								} else {

									$('.checkbox3').each(function () {
										this.checked = false;

									});
									$('.bulkDeleteBtn').prop('disabled', true);
		                            $('.deleteBtnForRule').prop('disabled', false);
								}
							});
							$('.checkbox3').on('click', function () {
	
								if ($('.checkbox3:checked').length == $('.checkbox3').length) {

									
									$('#select_all3').prop('checked', true);
									
								} else {
									
									$('#select_all3').prop('checked', false);
								
								}
							});
							$('#select_all4').on('click', function () {
								if (this.checked) {

									$('.checkbox4').each(function () {
										this.checked = true;
									});
									$('.bulkDeleteBtn').prop('disabled', false);
		                            $('.deleteBtnForRule').prop('disabled', true);
								} else {

									$('.checkbox4').each(function () {
										this.checked = false;

									});
									$('.bulkDeleteBtn').prop('disabled', true);
		                            $('.deleteBtnForRule').prop('disabled', false);
								}
							});
							$('.checkbox4').on('click', function () {
	
								if ($('.checkbox4:checked').length == $('.checkbox4').length) {

									
									$('#select_all4').prop('checked', true);
									
								} else {
									
									$('#select_all4').prop('checked', false);
								
								}
							});
							$('#select_all5').on('click', function () {
								if (this.checked) {

									$('.checkbox5').each(function () {
										this.checked = true;
									});
									$('.bulkDeleteBtn').prop('disabled', false);
		                            $('.deleteBtnForRule').prop('disabled', true);
								} else {

									$('.checkbox5').each(function () {
										this.checked = false;

									});
									$('.bulkDeleteBtn').prop('disabled', true);
		                            $('.deleteBtnForRule').prop('disabled', false);
								}
							});
							$('.checkbox5').on('click', function () {
	
								if ($('.checkbox5:checked').length == $('.checkbox5').length) {

									
									$('#select_all5').prop('checked', true);
									
								} else {
									
									$('#select_all5').prop('checked', false);
								
								}
							});
							$('#select_all6').on('click', function () {
								if (this.checked) {

									$('.checkbox6').each(function () {
										this.checked = true;
									});
									$('.bulkDeleteBtn').prop('disabled', false);
		                            $('.deleteBtnForRule').prop('disabled', true);
								} else {

									$('.checkbox6').each(function () {
										this.checked = false;

									});
									$('.bulkDeleteBtn').prop('disabled', true);
		                            $('.deleteBtnForRule').prop('disabled', false);
								}
							});
							$('.checkbox6').on('click', function () {
	
								if ($('.checkbox6:checked').length == $('.checkbox6').length) {

									
									$('#select_all6').prop('checked', true);
									
								} else {
									
									$('#select_all6').prop('checked', false);
								
								}
							});

						}
						function bulkDeleteFraudRule() {
							var confirmationFlag = confirm("Are you Sure you want to delete the selected Rule");
							if (!confirmationFlag) {
								return false;
							}



							//	var ids = [];
							//var ids = new Array();
							var arr = new Array();
							//debugger;
							$(".checkbox:checked").each(function () {
								// 		if(".checkbox:checked.length" >= 2){
								// 			alert("hii");
								//  $("#bulkDeleteBtn").attr("disabled", true)
								// 		}else{
								// 			$("#bulkDeleteBtn").attr("disabled", false)
								// 		}
								arr.push($(this).val());

							});
							$(".checkbox1:checked").each(function () {
								arr.push($(this).val());

							});
							$(".checkbox2:checked").each(function () {
								arr.push($(this).val());

							});
							$(".checkbox3:checked").each(function () {
								arr.push($(this).val());

							});
							$(".checkbox4:checked").each(function () {
								arr.push($(this).val());

							});
							$(".checkbox5:checked").each(function () {
								arr.push($(this).val());

							});
							$(".checkbox6:checked").each(function () {
								arr.push($(this).val());

							});

							if (arr == "") {
								alert("no selected rule");
							} else {
								ids = arr.join();
								$.ajax({
									type: "POST",
									url: "bulkRulesDeleteAction",
									data: {
										token: document.getElementsByName("token")[0].value,
										payId: fraudFieldValidate('payId', null), //TODO for merchant module
										ruleIdList: ids,
									},

									success: function (data) {
										if ((data.response) != null) {
											alert(data.response);
											window.location.reload();
										}
										else {
											alert("Try again, Something went wrong!")
										}
									}
								});

							}

							return false
						}
						/*Code end here for Bulk delete of table for each rule */
						/*Code start for Bulk upload of table for each rule */
						function bulkUpload(ruleId, formId) {
							event.preventDefault();
							var form = $('#' + formId)[0];
							var data = new FormData(form);
							// debugger;
							data.append("payId", fraudFieldValidate('payId', null));
							data.append("rule", ruleId);
							data.append("fileName", document.getElementsByName("fileName")[0].value);
							data.append("token", document.getElementsByName("token")[0].value);

							//s data.append("path", path);	

							return $.ajax({
								type: "POST",
								enctype: 'multipart/form-data',
								url: 'bulkRulesAddAction',
								data: data,
								processData: false,
								contentType: false,
								cache: false,
								async: true,
								success: function (data) {
									if (data.response == null) {
										alert("Invalid CSV file, please try again ");
									} else {
										alert(data.response);
									}
									//alert(data.response);
									window.location.reload();
								},
								error: function (e) {
								}
							});



						}
						/*Code end here for Bulk upload of table for each rule */


						var issuerCoutries = [];
						var userCoutries = [];


						/*Code start for datatable entries of table for each rule */
						var ipRuleColumns = [
							// { "data": "payId" },
							{ "data": "ipAddress" },
							{ "data": "dateActiveFrom" },
							{ "data": "dateActiveTo" },
							{ "data": "startTime" },
							{ "data": 'endTime' },
							{ "data": 'repeatDays' },
							{ "data": "deleteBtn" },
							{ "data": "isSelected" }
						];
						var wlIpRuleColumns = [
							// { "data": "payId" },
							{ "data": "whiteListIpAddress" },
							{ "data": "dateActiveFrom" },
							{ "data": "dateActiveTo" },
							{ "data": "startTime" },
							{ "data": 'endTime' },
							{ "data": 'repeatDays' },
							{ "data": "deleteBtnForWL" },
							{ "data": "isSelectedWL" }

						];
						var issuerCountryColumns = [
							// { "data": "payId" },
							{ "data": "issuerCountry" },
							{ "data": "deleteBtnForIssuerCountry" },
							{ "data": "isSelectedIssuerCountry" }
						];

						var emailRuleColumns = [
							// { "data": "payId" },
							{ "data": "email" },
							{ "data": "dateActiveFrom" },
							{ "data": "dateActiveTo" },
							{ "data": "startTime" },
							{ "data": 'endTime' },
							{ "data": 'repeatDays' },
							{ "data": "deleteBtnEmail" },
							{ "data": "isSelectedEmail" }
						];

						var txnAmountColumns = [
							// { "data": "payId" },
							{ "data": "currency" },
							{ "data": "minTransactionAmount" },
							{ "data": "maxTransactionAmount" },
							{ "data": "deleteBtnTxnAmt" }

						];
						var cardBinColumns = [
							// { "data": "payId" },
							{ "data": "negativeBin" },
							{ "data": "dateActiveFrom" },
							{ "data": "dateActiveTo" },
							{ "data": "startTime" },
							{ "data": 'endTime' },
							{ "data": 'repeatDays' },
							{ "data": "deleteBtnCardRange" },
							{ "data": "isSelectedCardRange" }
						];

						var phoneRuleColumns = [
							// { "data": "payId" },
							{ "data": "phone" },
							{ "data": "dateActiveFrom" },
							{ "data": "dateActiveTo" },
							{ "data": "startTime" },
							{ "data": 'endTime' },
							{ "data": 'repeatDays' },
							{ "data": "deleteBtnPhone" },
							{ "data": "isSelectedPhone" }
						]

						var cardNoColumns = [
							// { "data": "payId" },
							{ "data": "negativeCard" },
							{ "data": "dateActiveFrom" },
							{ "data": "dateActiveTo" },
							{ "data": "startTime" },
							{ "data": 'endTime' },
							{ "data": 'repeatDays' },
							{ "data": "deleteBtnCardNumber" },
							{ "data": "isSelectedCardNumber" }
						]

						var txnAmountVelocityColumns = [
							// { "data": "payId" },
							{ "data": "currency" },
							{ "data": "maxTransactionAmount" },
							{ "data": "userIdentifier" },
							{ "data": "deleteBtnTxnAmtVelocity" }

						]


						function fetchFraudRuleList(payIdValue, ruleId) {
							$.ajax({
								url: 'ruleListAction',
								type: 'post',
								data: {
									token: document.getElementsByName("token")[0].value,
									payId: fraudFieldValidate('payId', null),
									ruleId: ruleId,
								},
							}).done(function (data) {
								let blockIpAddData = [];
								let whiteListIpAddData = [];
								let issuerCountryAddData = [];
								let emailListAddData = [];
								let txnAmtAddData = [];
								let cardRangeAddData = [];
								let phoneListAddData = [];
								let cardMaskAddData = [];
								let txnAmtVelocityAddData = [];

								for (var i = 0; i < data.fraudRuleList.length; i++) {
									if (data.fraudRuleList[i].fraudType == 'BLOCK_IP_ADDRESS') {
										data.fraudRuleList[i]['deleteBtn'] = "<input class='btn btn-info btn-xs deleteBtnForRule' style='margin-left:10px'  type='submit' value='Delete' onclick='deleteFraudRule(" + data.fraudRuleList[i]['id'] + ")'/>"
										data.fraudRuleList[i]['isSelected'] = "<input type='checkbox' onclick='checkboxclickcheck()' class='checkbox' name='BLOCK_IP_ADDRESS" + i + "'  value='" + data.fraudRuleList[i]['id'] + "'>"
										blockIpAddData.push(data.fraudRuleList[i]);
									}
									else if (data.fraudRuleList[i].fraudType == 'WHITE_LIST_IP_ADDRESS') {
										data.fraudRuleList[i]['deleteBtnForWL'] = "<input class='btn btn-info btn-xs deleteBtnForRule' style='margin-left:10px'  type='submit' value='Delete' onclick='deleteFraudRule(" + data.fraudRuleList[i]['id'] + ")'/>"
										data.fraudRuleList[i]['isSelectedWL'] = "<input type='checkbox' onclick='checkboxclickcheck1()' class='checkbox1' name='WHITE_LIST_IP_ADDRESS" + i + "'  value='" + data.fraudRuleList[i]['id'] + "'>"
										whiteListIpAddData.push(data.fraudRuleList[i]);
									}
									else if (data.fraudRuleList[i].fraudType == 'BLOCK_CARD_ISSUER_COUNTRY') {
										data.fraudRuleList[i]['deleteBtnForIssuerCountry'] = "<input class='btn btn-info btn-xs deleteBtnForRule' style='margin-left:10px'  type='submit' value='Delete' onclick='deleteFraudRule(" + data.fraudRuleList[i]['id'] + ")'/>"
										data.fraudRuleList[i]['isSelectedIssuerCountry'] = "<input type='checkbox' onclick='checkboxclickcheck2()' class='checkbox2' name='BLOCK_CARD_ISSUER_COUNTRY" + i + "'  value='" + data.fraudRuleList[i]['id'] + "'>"
										issuerCountryAddData.push(data.fraudRuleList[i]);
									}
									else if (data.fraudRuleList[i].fraudType == 'BLOCK_EMAIL_ID') {
										data.fraudRuleList[i]['deleteBtnEmail'] = "<input class='btn btn-info btn-xs deleteBtnForRule' style='margin-left:10px'  type='submit' value='Delete' onclick='deleteFraudRule(" + data.fraudRuleList[i]['id'] + ")'/>"
										data.fraudRuleList[i]['isSelectedEmail'] = "<input type='checkbox' onclick='checkboxclickcheck3()' class='checkbox3' name='BLOCK_EMAIL_ID" + i + "'  value='" + data.fraudRuleList[i]['id'] + "'>"
										emailListAddData.push(data.fraudRuleList[i]);
									}
									else if (data.fraudRuleList[i].fraudType == 'BLOCK_TXN_AMOUNT') {
										data.fraudRuleList[i]['deleteBtnTxnAmt'] = "<input class='btn btn-info btn-xs deleteBtnForRule' style='margin-left:10px'  type='submit' value='Delete' onclick='deleteFraudRule(" + data.fraudRuleList[i]['id'] + ")'/>"
										txnAmtAddData.push(data.fraudRuleList[i]);
									}
									else if (data.fraudRuleList[i].fraudType == 'BLOCK_CARD_BIN') {
										data.fraudRuleList[i]['deleteBtnCardRange'] = "<input class='btn btn-info btn-xs deleteBtnForRule' style='margin-left:10px'  type='submit' value='Delete' onclick='deleteFraudRule(" + data.fraudRuleList[i]['id'] + ")'/>"
										data.fraudRuleList[i]['isSelectedCardRange'] = "<input type='checkbox' onclick='checkboxclickcheck4()' class='checkbox4' name='BLOCK_CARD_BIN" + i + "'  value='" + data.fraudRuleList[i]['id'] + "'>"
										cardRangeAddData.push(data.fraudRuleList[i]);
									}
									else if (data.fraudRuleList[i].fraudType == 'BLOCK_PHONE_NUMBER') {
										data.fraudRuleList[i]['deleteBtnPhone'] = "<input class='btn btn-info btn-xs deleteBtnForRule' style='margin-left:10px'  type='submit' value='Delete' onclick='deleteFraudRule(" + data.fraudRuleList[i]['id'] + ")'/>"
										data.fraudRuleList[i]['isSelectedPhone'] = "<input type='checkbox' onclick='checkboxclickcheck5()' class='checkbox5' name='BLOCK_PHONE_NUMBER" + i + "'  value='" + data.fraudRuleList[i]['id'] + "'>"
										phoneListAddData.push(data.fraudRuleList[i]);
									}
									else if (data.fraudRuleList[i].fraudType == 'BLOCK_CARD_NO') {
										data.fraudRuleList[i]['deleteBtnCardNumber'] = "<input class='btn btn-info btn-xs deleteBtnForRule'  style='margin-left:10px'  type='submit' value='Delete' onclick='deleteFraudRule(" + data.fraudRuleList[i]['id'] + ")'/>"
										data.fraudRuleList[i]['isSelectedCardNumber'] = "<input type='checkbox' onclick='checkboxclickcheck6()' class='checkbox6' name='BLOCK_CARD_NO" + i + "'  value='" + data.fraudRuleList[i]['id'] + "'>"
										cardMaskAddData.push(data.fraudRuleList[i]);
									}
									else if (data.fraudRuleList[i].fraudType == 'BLOCK_TXN_AMOUNT_VELOCITY') {
										data.fraudRuleList[i]['deleteBtnTxnAmtVelocity'] = "<input class='btn btn-info btn-xs deleteBtnForRule' style='margin-left:10px'  type='submit' value='Delete' onclick='deleteFraudRule(" + data.fraudRuleList[i]['id'] + ")'/>"
										txnAmtVelocityAddData.push(data.fraudRuleList[i]);
									}
								}
								generateBlockIdAddressTable(blockIpAddData);
								generateBlockWLIdAddressTable(whiteListIpAddData);
								generateBlockIssuerCountryTable(issuerCountryAddData);
								generateBlockEmailAddressTable(emailListAddData);
								generateBlockTxnAmtTable(txnAmtAddData);
								generateBlockCardRangeTable(cardRangeAddData);
								generateBlockPhoneNumberTable(phoneListAddData);
								generateBlockCardMaskTable(cardMaskAddData);
								generateBlockTxnAmtVelocityTable(txnAmtVelocityAddData);


							})
						}

						function generateBlockIdAddressTable(blockIpAddData) {
							$('#ipAddListBody').dataTable({
								"aaData": blockIpAddData,
								"columns": ipRuleColumns,
								"searching": false,
								"processing": true,
								"paginationType": "full_numbers",
								buttons: [
									{
										extend: 'csv',
										title: 'Data export'
									}
								]
							})
						}
						function generateBlockWLIdAddressTable(whiteListIpAddData) {
							$('#wlIpAddListBody').dataTable({
								"aaData": whiteListIpAddData,
								"columns": wlIpRuleColumns,
								"searching": false,
								"processing": true,
								"paginationType": "full_numbers",
							})
						}
						function generateBlockIssuerCountryTable(issuerCountryAddData) {
							$('#issuerCountryListBody').dataTable({
								"aaData": issuerCountryAddData,
								"columns": issuerCountryColumns,
								"searching": false,
								"processing": true,
								"paginationType": "full_numbers",
							})
						}
						function generateBlockEmailAddressTable(emailListAddData) {
							$('#emailListBody').dataTable({
								"aaData": emailListAddData,
								"columns": emailRuleColumns,
								"searching": false,
								"processing": true,
								"paginationType": "full_numbers",
							})
						}
						function generateBlockTxnAmtTable(txnAmtAddData) {
							$('#txnAmountListBody').dataTable({
								"aaData": txnAmtAddData,
								"columns": txnAmountColumns,
								"searching": false,
								"paging": false,
								"ordering": false,
								"info": false
							});

							$("#popupButton8").attr("disabled", txnAmtAddData.length ? true : false);

						}
						function generateBlockCardRangeTable(cardRangeAddData) {
							$('#cardBinListBody').dataTable({
								"aaData": cardRangeAddData,
								"columns": cardBinColumns,
								"searching": false,
								"processing": true,
								"paginationType": "full_numbers",
							})
						}
						function generateBlockPhoneNumberTable(phoneListAddData) {
							$('#phoneListBody').dataTable({
								"aaData": phoneListAddData,
								"columns": phoneRuleColumns,
								"searching": false,
								"processing": true,
								"paginationType": "full_numbers",
							})
						}
						function generateBlockCardMaskTable(cardMaskAddData) {
							$('#cardNoListBody').dataTable({
								"aaData": cardMaskAddData,
								"columns": cardNoColumns,
								"searching": false,
								"processing": true,
								"paginationType": "full_numbers",
							})
						}
						function generateBlockTxnAmtVelocityTable(txnAmtVelocityAddData) {
							$('#txnAmountVelocityListBody').dataTable({
								"aaData": txnAmtVelocityAddData,
								"columns": txnAmountVelocityColumns,
								"searching": false,
								"paging": false,
								"ordering": false,
								"info": false
							})
							$("#popupButton11").attr("disabled", txnAmtVelocityAddData.length ? true : false);
						}

						/*Code end here for datatable entries of table for each rule */


						function clearFraudRules(tableNames) {
							var index = 0;
							while (index < 10) {
								for (var i = 2; ; i++) {
									var element = document.getElementById(tableNames[index]).childNodes[1].childNodes[i];
									if (element != null) {
										element.innerHTML = '';
									} else {
										break;
									}
								}
								index++;
							}
						}

						function showDialog(data) {
							$("#dialogBox").dialog({
								modal: true,
								draggable: false,
								resizable: false,
								position: ['center', 'center'],
								show: 'blind',
								hide: 'blind',
								width: 328,
								height: 72,
								buttons: [
									{
										text: "Ok, Proceed",
										click: function () {
											$(this).dialog("close");
										}
									},
								],
								open: function () {
									$("#dialogBox").css("overflow", "hidden");
								},
								dialogClass: 'ui-dialog-osx',
							});
						}

						$(document).ready(function () {
							//by default fraud rules for ALL MERCHANTS will be displayed
							fetchFraudRuleList('ALL');
							$('#payId').change(function (event) {
								clearFraudRules([ipRuleColumns[0], emailRuleColumns[0], issuerCountryColumns[0], txnAmountColumns[0], cardBinColumns[0], wlIpRuleColumns[0], phoneRuleColumns[0], cardNoColumns[0], txnAmountVelocityColumns[0]]);
								fetchFraudRuleList('payId');
							});
						});

                        
						function makeCardMaskini() {

							var element = document.getElementById('negativeCard');
							var initialDigits = document.getElementById('cardIntialDigits').value;
							var lastDigits = document.getElementById('cardLastDigits').value;
							value = element.value;
							element.value = initialDigits + "******" + lastDigits;

							if (initialDigits != "" && initialDigits != null) {
								if (initialDigits.length == 6) {
									var arra = element.value.split("");
									if (arra[0] == "2" ||arra[0] == "3" || arra[0] == "4" || arra[0] == "5" || arra[0] == "6") {
										$('#validate_crdIn').text('Valid Card Length');
										document.getElementById("validate_crdIn").classList.add("cardIniSuccess");
										document.getElementById("validate_crdIn").classList.remove("cardIniError");
										document.getElementById("validate_crdIn").style.display = "block";
										document.getElementById("negativeCradBtn").disabled = false;
										return true;
									}
									else {
										$('#validate_crdIn').text('Enter Card Number Starts With 2,3,4,5 only of 6 digits length');
										document.getElementById("validate_crdIn").classList.add("cardIniError");
										document.getElementById("validate_crdIn").classList.remove("cardIniSuccess");
										document.getElementById("validate_crdIn").style.display = "block";
										document.getElementById("negativeCradBtn").disabled = true;
										return false;
									}
								}
								else {
									$('#validate_crdIn').text('Enter Card Length 6 digit Number Only');
									document.getElementById("validate_crdIn").classList.remove("cardIniSuccess");
									document.getElementById("validate_crdIn").classList.add("cardIniError");
									document.getElementById("validate_crdIn").style.display = "block";
									document.getElementById("negativeCradBtn").disabled = true;
									return false;
								}
							}
							else {
								$('#validate_crdIn').text('Enter Card Length 6 digit Number Only');
								document.getElementById("validate_crdIn").classList.remove("cardIniSuccess");
								document.getElementById("validate_crdIn").classList.add("cardIniError");
								document.getElementById("validate_crdIn").style.display = "block";
								return false;
							}
						}

						function makeCardMasklst() {

							var element = document.getElementById('negativeCard');
							var initialDigits = document.getElementById('cardIntialDigits').value;
							var lastDigits = document.getElementById('cardLastDigits').value;
							value = element.value;
							if (lastDigits.length == 4) {
								element.value = initialDigits + "******" + lastDigits;
								$('#validate_crdL').text('Valid Card Length');
								document.getElementById("validate_crdL").classList.add("cardLstSuccess");
								document.getElementById("validate_crdL").classList.remove("cardLstError");
								document.getElementById("validate_crdL").style.display = "block";
								return true;
							}
							else {
								$('#validate_crdL').text('Enter Card Length 4 digit Number Only');
								document.getElementById("validate_crdL").classList.add("cardLstError");
								document.getElementById("validate_crdL").classList.remove("cardLstSuccess");
								document.getElementById("validate_crdL").style.display = "block";
								return false;
							}
						}
						function iniCardMask() {

							var element = document.getElementById('prenegativeCard');
							var preinitialDigits = document.getElementById('precardIntialDigits').value;
							var prelastDigits = document.getElementById('precardLastDigits').value;
							value = element.value;
							element.value = preinitialDigits + "******" + prelastDigits;

							if (preinitialDigits != "" || preinitialDigits != null) {
								if (preinitialDigits.length == 6) {
									var arra = element.value.split("");
									if (arra[0] == "3" || arra[0] == "4" || arra[0] == "5" || arra[0] == "6") {
										$('#validate_crdInpre').text('Valid Card Length');
										document.getElementById("validate_crdInpre").classList.add("cardIniSuccess");
										document.getElementById("validate_crdInpre").classList.remove("cardIniError");
										document.getElementById("validate_crdInpre").style.display = "block";
										document.getElementById("percardTransactionBtn").disabled = false;
										return true;
									}
									else {
										$('#validate_crdInpre').text('Enter Card Number Starts With 3,4,5 only of 6 digits length');
										document.getElementById("validate_crdInpre").classList.add("cardIniError");
										document.getElementById("validate_crdInpre").classList.remove("cardIniSuccess");
										document.getElementById("validate_crdInpre").style.display = "block";
										document.getElementById("percardTransactionBtn").disabled = true;
										return false;
									}
								}
								else {
									$('#validate_crdInpre').text('Enter Card Length 6 digit Number Only');
									document.getElementById("validate_crdInpre").classList.remove("cardIniSuccess");
									document.getElementById("validate_crdInpre").classList.add("cardIniError");
									document.getElementById("validate_crdInpre").style.display = "block";
									document.getElementById("percardTransactionBtn").disabled = true;
									return false;
								}
							}
							else {
								$('#validate_crdInpre').text('Enter Card Length 6 digit Number Only');
								document.getElementById("validate_crdInpre").classList.remove("cardIniSuccess");
								document.getElementById("validate_crdInpre").classList.add("cardIniError");
								document.getElementById("validate_crdInpre").style.display = "block";
								document.getElementById("percardTransactionBtn").disabled = true;
								return false;
							}
						}

						function lastCardMask() {

							var element = document.getElementById('prenegativeCard');
							var preinitialDigits = document.getElementById('precardIntialDigits').value;
							var prelastDigits = document.getElementById('precardLastDigits').value;
							value = element.value;
							element.value = preinitialDigits + "******" + prelastDigits;

							if (prelastDigits.length == 4) {
								$('#validate_crdLpre').text('Valid Card Length');
								document.getElementById("validate_crdLpre").classList.add("cardLstSuccess");
								document.getElementById("validate_crdLpre").classList.remove("cardLstError");
								document.getElementById("validate_crdLpre").style.display = "block";
								//document.getElementById("percardTransactionBtn").disabled = false;
								return true;
							}
							else {
								$('#validate_crdLpre').text('Enter Card Length 4 digit Number Only');
								document.getElementById("validate_crdLpre").classList.add("cardLstError");
								document.getElementById("validate_crdLpre").classList.remove("cardLstSuccess");
								document.getElementById("validate_crdLpre").style.display = "block";
								//document.getElementById("percardTransactionBtn").disabled = true;
								return false;
							}
						}

						function hideAllPopups() {
							$('#popup').popup('hide');
							$('#popupWip').popup('hide');
							$('#popup2').popup('hide');
							$('#popup3').popup('hide');
							$('#popup4').popup('hide');
							$('#popup5').popup('hide');
							$('#popup6').popup('hide');
							$('#popup7').popup('hide');
							$('#popup8').popup('hide');
							$('#popup9').popup('hide');
							$('#popup10').popup('hide');
							$('#popup11').popup('hide');
						}

						function getFieldValue(fieldName,start=0,end=7) {
							var element = document.getElementById(fieldName);

							if (element.tagName == 'INPUT') {
								var fieldValue = element.value;
								var finalValue = (fieldValue != '') ? fieldValue : ''; //TODO regex		
								return finalValue;
								validateFieldValue(finalValue, fraudType);
							} else if (element.tagName == 'SELECT') {
								var fieldValue = element.options[element.selectedIndex].value;
								var finalValue = (!fieldValue.match('SELECT')) ? fieldValue : '';
								return finalValue;
							} else if (element.tagName == 'UL') {
								var optionName = element.getAttribute('data-name');
								var finalValue = [];
								if(optionName == 'country'){
									$('input[name="' + optionName + '"]:checked').each(function (_Index) {
										finalValue.push($(this).val());
									});
								}else{
									for(var i =start; i<end; i++){
										if($('#repeatDays'+i).is(":checked")){
											var day = '';
											if(i == 0 || i == 7 || i == 14 || i == 21 || i == 28 || i == 35){
												day = 'SUN';
											}else if(i == 1 || i == 8 || i == 15 || i == 22 || i == 29 || i == 36){
												day = 'MON';
											}else if(i == 2 || i == 9 || i == 16 || i == 23 || i == 30 || i == 37){
												day = 'TUE';
											}else if(i == 3 || i == 10 || i == 17 || i == 24 || i == 31 || i == 38){
												day = 'WED';
											}else if(i == 4 || i == 11 || i == 18 || i == 25 || i == 32 || i == 39){
												day = 'THU';
											}else if(i == 5 || i == 12 || i == 19 || i == 26 || i == 33 || i == 40){
												day = 'FRI';
											}else if(i == 6 || i == 13 || i == 20 || i == 27 || i == 34 || i == 41){
												day = 'SAT';
											}
											finalValue.push(day);
										}
									}
								}
								
								return finalValue;
							} else if (element.tagName == 'DIV') {
								var optionName = element.getAttribute('data-name');
								var finalValue = [];
								$('input[name="' + optionName + '"]').each(function () {
									finalValue.push($(this).val());
								});
								return finalValue;
							}
						}


						function fraudFieldValidate(fieldName, fraudType) {
							if (fieldName == 'payId') {
								return getFieldValue(fieldName);
							}
							switch (fraudType) {
								case whiteListIpAddressS: {
									if (ajaxValidationFlag) {
										break;
									}

									var alwaysOnFlag = getFieldValue("alwaysOnFlag2");

									if (fieldName == "whiteListIpAddress") {

										var whiteListIpAddress = getFieldValue("whiteListIpAddress");
										if (whiteListIpAddress != '' && whiteListIpAddress != null) {
											ajaxValidationFlag = false;
											return whiteListIpAddress;
										} else {
											ajaxValidationFlag = true;
											break;
										}

									} else if (fieldName == "dateActiveFrom") {
										if (alwaysOnFlag == "true") {
											break;
										}
										else {
											var dateActiveFrom = getFieldValue("dateActiveFrom1");
											if (dateActiveFrom != '' && dateActiveFrom != null) {
												ajaxValidationFlag = false;
												return dateActiveFrom;
											} else {
												ajaxValidationFlag = true;
												break;
											}
										}

									} else if (fieldName == "dateActiveTo") {
										if (alwaysOnFlag == "true") {
											break;
										}
										else {
											var dateActiveTo = getFieldValue("dateActiveTo1");
											if (dateActiveTo != '' && dateActiveTo != null) {
												ajaxValidationFlag = false;
												return dateActiveTo;
											} else {
												ajaxValidationFlag = true;
												break;
											}
										}

									} else if (fieldName == "startTime") {
										if (alwaysOnFlag == "true") {
											break;
										}
										else {
											var startTime = getFieldValue("startTime1");
											if (startTime != '' && startTime != null) {
												ajaxValidationFlag = false;
												return startTime;
											} else {
												ajaxValidationFlag = true;
												break;
											}
										}

									} else if (fieldName == "endTime") {
										if (alwaysOnFlag == "true") {
											break;
										}
										else {

											var endTime = getFieldValue("endTime1");
											if (endTime != '' && endTime != null) {
												ajaxValidationFlag = false;
												return endTime;
											} else {
												ajaxValidationFlag = true;
												break;
											}
										}

									} else if (fieldName == "repeatDays") {
										if (alwaysOnFlag == "true") {
											break;
										}
										else {
											var value = getFieldValue(fieldName,7,13).join(",");
											if (value != '' && value != null) {
												ajaxValidationFlag = false;
												return value;
											} else {
												ajaxValidationFlag = true;
												break;
											}
										}
									} else if (fieldName == "alwaysOnFlag") {

										return alwaysOnFlag;

									}
								} break;

								case ipAddressS: {
									if (ajaxValidationFlag) {
										break;
									}

									var alwaysOnFlag = getFieldValue("alwaysOnFlag1");

									if (fieldName == "ipAddress") {

										var ipAddress = getFieldValue("ipAddress");
										if (ipAddress != '' && ipAddress != null) {
											ajaxValidationFlag = false;
											return ipAddress;
										} else {
											ajaxValidationFlag = true;
											break;
										}

									} else if (fieldName == "dateActiveFrom") {

										if (alwaysOnFlag == "true") {
											break;
										}
										else {
											var dateActiveFrom = getFieldValue("dateActiveFrom");
											if (dateActiveFrom != '' && dateActiveFrom != null) {
												ajaxValidationFlag = false;
												return dateActiveFrom;
											} else {
												ajaxValidationFlag = true;
												break;
											}
										}

									} else if (fieldName == "dateActiveTo") {

										if (alwaysOnFlag == "true") {
											break;
										}
										else {
											var dateActiveTo = getFieldValue("dateActiveTo");
											if (dateActiveTo != '' && dateActiveTo != null) {
												ajaxValidationFlag = false;
												return dateActiveTo;
											} else {
												ajaxValidationFlag = true;
												break;
											}

										}
									} else if (fieldName == "startTime") {

										if (alwaysOnFlag == "true") {
											break;
										}
										else {

											var startTime = getFieldValue("startTime");
											if (startTime != '' && startTime != null) {
												ajaxValidationFlag = false;
												return startTime;
											} else {
												ajaxValidationFlag = true;
												break;
											}
										}


									} else if (fieldName == "endTime") {

										if (alwaysOnFlag == "true") {
											break;
										}
										else {

											var endTime = getFieldValue("endTime");
											if (endTime != '' && endTime != null) {
												ajaxValidationFlag = false;
												return endTime;
											} else {
												ajaxValidationFlag = true;
												break;
											}
										}

									} else if (fieldName == "repeatDays") {

										if (alwaysOnFlag == "true") {
											break;
										}
										else {
											var value = getFieldValue(fieldName,0,6).join(",");
											if (value != '' && value != null) {
												ajaxValidationFlag = false;
												return value;
											} else {
												ajaxValidationFlag = true;
												break;
											}

										}
									}
									else if (fieldName == "alwaysOnFlag") {

										return alwaysOnFlag;
									}
								} break;
								case emailS: {
									if (ajaxValidationFlag) {
										break;
									}

									var alwaysOnFlag = getFieldValue("alwaysOnFlag4");

									if (fieldName == "email") {
										var email = getFieldValue("email");
										if (email != '' && email != null) {
											ajaxValidationFlag = false;
											return email;
										} else {
											ajaxValidationFlag = true;
											break;
										}

									} else if (fieldName == "dateActiveFrom") {

										if (alwaysOnFlag == "true") {
											break;
										}
										else {
											var dateActiveFrom = getFieldValue("dateActiveFrom3");
											if (dateActiveFrom != '' && dateActiveFrom != null) {
												ajaxValidationFlag = false;
												return dateActiveFrom;
											} else {
												ajaxValidationFlag = true;
												break;
											}
										}

									} else if (fieldName == "dateActiveTo") {
										if (alwaysOnFlag == "true") {
											break;
										}
										else {
											var dateActiveTo = getFieldValue("dateActiveTo3");
											if (dateActiveTo != '' && dateActiveTo != null) {
												ajaxValidationFlag = false;
												return dateActiveTo;
											} else {
												ajaxValidationFlag = true;
												break;
											}
										}

									} else if (fieldName == "startTime") {
										if (alwaysOnFlag == "true") {
											break;
										}
										else {
											var startTime = getFieldValue("startTime3");
											if (startTime != '' && startTime != null) {
												ajaxValidationFlag = false;
												return startTime;
											} else {
												ajaxValidationFlag = true;
												break;
											}
										}

									} else if (fieldName == "endTime") {
										if (alwaysOnFlag == "true") {
											break;
										}
										else {
											var endTime = getFieldValue("endTime3");
											if (endTime != '' && endTime != null) {
												ajaxValidationFlag = false;
												return endTime;
											} else {
												ajaxValidationFlag = true;
												break;
											}
										}

									} else if (fieldName == "repeatDays") {

										if (alwaysOnFlag == "true") {
											break;
										}
										else {
											var value = getFieldValue(fieldName,21,27).join(",");
											if (value != '' && value != null) {
												ajaxValidationFlag = false;
												return value;
											} else {
												ajaxValidationFlag = true;
												break;
											}
										}
									} else if (fieldName == "alwaysOnFlag") {

										return alwaysOnFlag;

									}
								} break;
								case issuerCountry: {
									if (fieldName == "issuerCountry") {
										var value = getFieldValue(fieldName).join(",");
										//alert(value);
										if (value != '' && value != null) {
											ajaxValidationFlag = false;
											return value;
										} else {
											ajaxValidationFlag = true;
											break;
										}
									}
								} break;
								case numberOfTransaction: {

									if (fieldName == "minutesTxnLimit") {
										var minutesTxnLimit = getFieldValue("minutesTxnLimit");
										if (minutesTxnLimit != '' && minutesTxnLimit != null) {
											ajaxValidationFlag = false;
											return minutesTxnLimit;
										} else {
											ajaxValidationFlag = true;
											break;
										}
									} else if (fieldName == "perCardTransactionAllowed") {
										var perCardTransactionAllowed = getFieldValue("perCardTransactionAllowedvelo");
										if (perCardTransactionAllowed != '' && perCardTransactionAllowed != null) {
											ajaxValidationFlag = false;
											return perCardTransactionAllowed;
										} else {
											ajaxValidationFlag = true;
											break;
										}
									}
								} break;
								case transactionAmount: {
									if (fieldName == "minTransactionAmount" || fieldName == "maxTransactionAmount" || fieldName == "currency") {
										var minAmount = getFieldValue("minTransactionAmount");
										var maxAmount = getFieldValue("maxTransactionAmount");
										var currency = getFieldValue("currency");
										var value = getFieldValue(fieldName);
										if ((minAmount.trim() != null && maxAmount.trim() != null) && (parseFloat(minAmount.trim()) <= parseFloat(maxAmount.trim()))) {
											ajaxValidationFlag = false;
											return value;
										} else {
											ajaxValidationFlag = true;
											break;
										}
									}
								} break;
								case negativeBinS:{
									if (ajaxValidationFlag){
										break;
									}
									var alwaysOnFlag = getFieldValue("alwaysOnFlag3");
									
										if(fieldName == "negativeBin"){
											
											var negativeBin = getFieldValue("negativeBin");
											if(negativeBin !='' && negativeBin!=null){
														ajaxValidationFlag = false;
														return negativeBin;
													}else{
														ajaxValidationFlag = true;
														break;
													}
											
										}else if(fieldName == "dateActiveFrom"){
											if (alwaysOnFlag == "true"){
												break;
											}
											else{
											var dateActiveFrom = getFieldValue("dateActiveFrom4");
											if(dateActiveFrom !='' && dateActiveFrom!=null){
														ajaxValidationFlag = false;
														return dateActiveFrom;
													}else{
														ajaxValidationFlag = true;
														break;
													}
											}
											
										}else if(fieldName == "dateActiveTo"){
											
											if (alwaysOnFlag == "true"){
												break;
											}
											else{
											var dateActiveTo = getFieldValue("dateActiveTo4");
											if(dateActiveTo !='' && dateActiveTo!=null){
														ajaxValidationFlag = false;
														return dateActiveTo;
													}else{
														ajaxValidationFlag = true;
														break;
													}
											}
											
										}else if(fieldName == "startTime"){
											if (alwaysOnFlag == "true"){
												break;
											}
											else{
												
											var startTime = getFieldValue("startTime4");
											if(startTime !='' && startTime!=null){
														ajaxValidationFlag = false;
														return startTime;
													}else{
														ajaxValidationFlag = true;
														break;
													}
											}
											
										}else if(fieldName == "endTime"){
											if (alwaysOnFlag == "true"){
												break;
											}
											else{
											var endTime = getFieldValue("endTime4");
											if(endTime !='' && endTime!=null){
														ajaxValidationFlag = false;
														return endTime;
													}else{
														ajaxValidationFlag = true;
														break;
													}
											}
										}else if(fieldName == "repeatDays"){
											if (alwaysOnFlag == "true"){
												break;
											}
											else{
													var value = getFieldValue(fieldName,14,20).join(",");
													if (value != '' && value != null) {
														ajaxValidationFlag = false;
														return value;
													} else {
														ajaxValidationFlag = true;
														break;
													}
											}
										}else if(fieldName == "alwaysOnFlag"){
											
											return alwaysOnFlag;	
											
										}
								}break;
								case phoneS: {
									if (ajaxValidationFlag) {
										break;
									}

									var alwaysOnFlag = getFieldValue("alwaysOnFlag9");

									if (fieldName == "phone") {
										var phone = getFieldValue("phone");
										if (phone != '' && phone != null) {
											ajaxValidationFlag = false;
											return phone;
										} else {
											ajaxValidationFlag = true;
											break;
										}

									} else if (fieldName == "dateActiveFrom") {

										if (alwaysOnFlag == "true") {
											break;
										}
										else {
											var dateActiveFrom = getFieldValue("dateActiveFrom9");
											if (dateActiveFrom != '' && dateActiveFrom != null) {
												ajaxValidationFlag = false;
												return dateActiveFrom;
											} else {
												ajaxValidationFlag = true;
												break;
											}
										}

									} else if (fieldName == "dateActiveTo") {
										if (alwaysOnFlag == "true") {
											break;
										}
										else {
											var dateActiveTo = getFieldValue("dateActiveTo9");
											if (dateActiveTo != '' && dateActiveTo != null) {
												ajaxValidationFlag = false;
												return dateActiveTo;
											} else {
												ajaxValidationFlag = true;
												break;
											}
										}

									} else if (fieldName == "startTime") {
										if (alwaysOnFlag == "true") {
											break;
										}
										else {
											var startTime = getFieldValue("startTime9");
											if (startTime != '' && startTime != null) {
												ajaxValidationFlag = false;
												return startTime;
											} else {
												ajaxValidationFlag = true;
												break;
											}
										}

									} else if (fieldName == "endTime") {
										if (alwaysOnFlag == "true") {
											break;
										}
										else {
											var endTime = getFieldValue("endTime9");
											if (endTime != '' && endTime != null) {
												ajaxValidationFlag = false;
												return endTime;
											} else {
												ajaxValidationFlag = true;
												break;
											}
										}

									} else if (fieldName == "repeatDays") {

										if (alwaysOnFlag == "true") {
											break;
										}
										
										else {
											var value = getFieldValue(fieldName,28,34).join(",");
											if (value != '' && value != null) {
												ajaxValidationFlag = false;
												return value;
											} else {
												ajaxValidationFlag = true;
												break;
											}
										}
									} else if (fieldName == "alwaysOnFlag") {

										return alwaysOnFlag;

									}
								} break;
								case negativeCardS: {
									if (ajaxValidationFlag) {
										break;
									}
									var alwaysOnFlag = getFieldValue("alwaysOnFlag6");

									if (fieldName == "negativeCard") {

										var negativeCard = getFieldValue("negativeCard");
										if (negativeCard != '' && negativeCard != null) {
											ajaxValidationFlag = false;
											return negativeCard;
										} else {
											ajaxValidationFlag = true;
											break;
										}

									} else if (fieldName == "dateActiveFrom") {
										if (alwaysOnFlag == "true") {
											break;
										}
										else {
											var dateActiveFrom = getFieldValue("dateActiveFrom5");
											if (dateActiveFrom != '' && dateActiveFrom != null) {
												ajaxValidationFlag = false;
												return dateActiveFrom;
											} else {
												ajaxValidationFlag = true;
												break;
											}
										}

									} else if (fieldName == "dateActiveTo") {
										if (alwaysOnFlag == "true") {
											break;
										}
										else {
											var dateActiveTo = getFieldValue("dateActiveTo5");
											if (dateActiveTo != '' && dateActiveTo != null) {
												ajaxValidationFlag = false;
												return dateActiveTo;
											} else {
												ajaxValidationFlag = true;
												break;
											}
										}

									} else if (fieldName == "startTime") {
										if (alwaysOnFlag == "true") {
											break;
										}
										else {
											var startTime = getFieldValue("startTime5");
											if (startTime != '' && startTime != null) {
												ajaxValidationFlag = false;
												return startTime;
											} else {
												ajaxValidationFlag = true;
												break;
											}
										}
									} else if (fieldName == "endTime") {
										if (alwaysOnFlag == "true") {
											break;
										}
										else {

											var endTime = getFieldValue("endTime5");
											if (endTime != '' && endTime != null) {
												ajaxValidationFlag = false;
												return endTime;
											} else {
												ajaxValidationFlag = true;
												break;
											}
										}

									} else if (fieldName == "repeatDays") {
										if (alwaysOnFlag == "true") {
											break;
										}
										else {
											var value = getFieldValue(fieldName,35,41).join(",");
											if (value != '' && value != null) {
												ajaxValidationFlag = false;
												return value;
											} else {
												ajaxValidationFlag = true;
												break;
											}
										}
									} else if (fieldName == "alwaysOnFlag") {

										return alwaysOnFlag;

									}
								} break;
								case transactionAmountVelocity: {
									if (fieldName == "maxTransactionAmount" || fieldName == "currency" || fieldName == "userIdentifier") {
										//var txnAmount = getFieldValue("txnAmountVelocity");
										//var currency = getFieldValue("currency");
										var value = getFieldValue(fieldName == "maxTransactionAmount" ? "txnAmountVelocity" : fieldName);
										if (value) {
											ajaxValidationFlag = false;
											return value;
										} else {
											ajaxValidationFlag = true;
											break;
										}
									}
								}


							}
						}
						function ajaxFraudRequest(fraudType) {
							ajaxValidationFlag = false;
							var alwaysOnFlag = fraudFieldValidate('alwaysOnFlag', fraudType);
							var phone = fraudFieldValidate('phone', fraudType);
							var dateActiveFrom = fraudFieldValidate('dateActiveFrom', fraudType);
							var dateActiveTo = fraudFieldValidate('dateActiveTo', fraudType);
							var startTime = fraudFieldValidate('startTime', fraudType);
							var endTime = fraudFieldValidate('endTime', fraudType);
							var repeatDays = fraudFieldValidate('repeatDays', fraudType);
							var issuerCountry = fraudFieldValidate('issuerCountry', fraudType);
							var whiteListIpAddress = fraudFieldValidate('whiteListIpAddress', fraudType);
							var ipAddress = fraudFieldValidate('ipAddress', fraudType);
							var token = document.getElementsByName("token")[0].value;
							var payId = fraudFieldValidate('payId', fraudType);
							var email = fraudFieldValidate('email', fraudType);
							var negativeBin = fraudFieldValidate('negativeBin', fraudType);
							var negativeCard = fraudFieldValidate('negativeCard', fraudType);
							var currency = fraudFieldValidate('currency', fraudType);
							var minTransactionAmount = fraudFieldValidate('minTransactionAmount', fraudType);
							var maxTransactionAmount = fraudFieldValidate('maxTransactionAmount', fraudType);
							var perCardTransactionAllowed = fraudFieldValidate('perCardTransactionAllowed', fraudType);
							var minutesTxnLimit = fraudFieldValidate('minutesTxnLimit', fraudType);
							var perCardTransactionAllowed = fraudFieldValidate('perCardTransactionAllowed', fraudType);
							if(fraudType == "BLOCK_TXN_AMOUNT_VELOCITY"){
								var userIdentifier = $('input[name=userIdentifier]:checked', '#userIdentifier').val();
								}
							//var userIdentifier =  fraudFieldValidate('userIdentifier',fraudType);
							//var userIdentifier = $('input[name=userIdentifier]:checked', '#userIdentifier').val();
							//var txnAmountVelocity = fraudFieldValidate('txnAmountVelocity',fraudType);

							if ((fraudType != "BLOCK_CARD_ISSUER_COUNTRY") && (fraudType != "BLOCK_TXN_AMOUNT") && (fraudType != "BLOCK_TXN_AMOUNT_VELOCITY")) {
								if (alwaysOnFlag != "true") {
									debugger;
									if ((dateActiveFrom == "" || dateActiveFrom == null) || (dateActiveTo == "" || dateActiveTo == null) || (startTime == "" || startTime == null) || (endTime == "" || endTime == null) || (repeatDays == null || repeatDays == "")) {
										alert("Please Enter Valid Values");
										//window.location.reload();
										return false;
									}
									else {
									}
								}
							}
							if (fraudType == "BLOCK_TXN_AMOUNT") {
								if (minTransactionAmount == maxTransactionAmount) {
									alert("Min and Max amount can not be same");
									return false;
								}
								


							}
							if (fraudType == "BLOCK_TXN_AMOUNT") {
								if (minTransactionAmount < 1 ) {
									alert("Min amount can not be less than 1");
									return false;
								}
								


							}
							if (fraudType == "BLOCK_TXN_AMOUNT") {
								if (maxTransactionAmount < 1 ) {
									alert("Min amount can not be less than 1");
									return false;
								}
								


							}
						
							
					

							if (!ajaxValidationFlag) {
								$("#blockIp").attr("disabled", true);
								$("#blockWhiteIp").attr("disabled", true);
								$("#blockIssuer").attr("disabled", true);
								$("#blockCardRange").attr("disabled", true);
								$("#blockEmail").attr("disabled", true);
								$("#blockCardNo").attr("disabled", true);
								$("#blockTxnAmt").attr("disabled", true);
								$("#blockPhone").attr("disabled", true);
								$("#blockTxnAmtVel").attr("disabled", true);


								var ajaxRequest = $.ajax({
									url: 'addFraudRule',
									type: 'post',
									data: {
										whiteListIpAddress: whiteListIpAddress,
										ipAddress: ipAddress,
										token: token,
										payId: payId,
										issuerCountry: issuerCountry,
										email: email,
										negativeBin: negativeBin,
										dateActiveFrom: dateActiveFrom,
										dateActiveTo: dateActiveTo,
										startTime: startTime,
										endTime: endTime,
										repeatDays: repeatDays,
										alwaysOnFlag: alwaysOnFlag,
										negativeCard: negativeCard,
										currency: currency,
										minTransactionAmount: minTransactionAmount,
										maxTransactionAmount: maxTransactionAmount,
										fraudType: fraudType,
										perCardTransactionAllowed: perCardTransactionAllowed,
										minutesTxnLimit: minutesTxnLimit,
										phone: phone,
										userIdentifier: userIdentifier,
										//txnAmountVelocity:txnAmountVelocity,


									},
									success: function (data) {
										$("#blockIp").attr("disabled", false);
										$("#blockWhiteIp").attr("disabled", false);
										$("#blockIssuer").attr("disabled", false);
										$("#blockCardRange").attr("disabled", false);
										$("#blockEmail").attr("disabled", false);
										$("#blockCardNo").attr("disabled", false);
										$("#blockTxnAmt").attr("disabled", false);
										$("#blockPhone").attr("disabled", false);
										$("#blockTxnAmtVel").attr("disabled", false);
										hideAllPopups();
										var result = data;
										if (result != null) {
											var errorFieldMap = data["Invalid request"];
											if (errorFieldMap != null) {
												var error;
												for (key in errorFieldMap) {
													(error != null) ? (error + ',' + key) : (error = key);
												}
												alert('Please provide valid value');
											}

											/* if(data.responseCode == '342'){
											  alert("Fraud Rule added successfully");
											  window.location.reload();	
												}
											
											else if(data.responseCode == '340'){
											  alert("Fraud rule already exist");
											  window.location.reload();	
												} */

											else {
												$("#blockIp").attr("disabled", false);
												$("#blockWhiteIp").attr("disabled", false);
												$("#blockIssuer").attr("disabled", false);
												$("#blockCardRange").attr("disabled", false);
												$("#blockEmail").attr("disabled", false);
												$("#blockCardNo").attr("disabled", false);
												$("#blockTxnAmt").attr("disabled", false);
												$("#blockPhone").attr("disabled", false);
												$("#blockTxnAmtVel").attr("disabled", false);
												//TODO
												alert(data.responseMsg);
												window.location.reload();
												//alert(data.responseMsg);
											}
											showDialog(data);
										} else {

										}
									},
									error: function (data) {
										alert('Try Again, Soemthing went wrong! ');
									}
								});
							}
							//else (startTime || endTime || repeatDays == ""){
							//alert('Please Enter Valid Values');
							//}

							//else{
							// alert('Please provide correct values');
							// return false;
							//}
						}

