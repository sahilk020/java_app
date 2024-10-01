
$(document).ready(
function() {
    $('input:file').change(function() {
        if ($(this).val()) {
            $('input:submit').attr('disabled', false);
        }
    });
});
$(document).ready(function() {

    $('table.csv').DataTable({
        dom: 'B',
        buttons: [{
            extend: 'csv',
            text: 'GET SAMPLE CSV',
            filename: 'NODAL_BATCH_UPLOAD',
        }]
    });
    
    var myframe = document.getElementById("output_frame");
	myframe.onload = function() {
		var iframeDocument = myframe.contentDocument || myframe.contentWindow.document; // get access to DOM inside the iframe
		var content = iframeDocument.textContent || iframeDocument.body.textContent; // get text of iframe
		var json = JSON.parse(content);
		//console.log(json);
		if (json) {
			// process the json here
			$('#batchFileUploadError').html(json.response);
			
			
			var resultJSON = JSON.parse(json.response); 
//			var resultJSON = JSON.parse('[{"beneficiaryCode":"shubham","paymentType":"NEFT","responseMessage":"SUCCESS","status":"ACTIVE","reason":""},{"beneficiaryCode":"rohit","paymentType":"NEFT","responseMessage":"FAILED","status":"INACTIVE","reason":"ABC"}]');
			let resutlTableTbody = $('#addBeneResultCsv');
			resutlTableTbody.html('');
			$.each(resultJSON, function(index, value){
			    let resultTableTr = $('<tr/>');
			    let resultTableTd1 = $('<td/>').html(index + 1);
			    let resultTableTd2 = $('<td/>').html(value.beneficiaryCode);
			    let resultTableTd3 = $('<td/>').html(value.paymentType);
			    let resultTableTd4 = $('<td/>').html(value.responseMessage);
			    let resultTableTd5 = $('<td/>').html(value.status);
			    let resultTableTd6 = $('<td/>').html(value.reason);
			    
			    resultTableTr.append(resultTableTd1);
			    resultTableTr.append(resultTableTd2);
			    resultTableTr.append(resultTableTd3);
			    resultTableTr.append(resultTableTd4);
			    resultTableTr.append(resultTableTd5);
			    resultTableTr.append(resultTableTd6);
			    resutlTableTbody.append(resultTableTr);
			});
			
			loadResultCsvTable();
			setTimeout(function() {
				$('#batchFileUploadError').html("");
			}, 100)
			//console.log(json);
		}
		$("#batchFile").val('');
	}
	
	$("#btnBatchUpload").on('click', function() {
    
		$('#fileUploadMerchantPayId').val($("#merchantPayId").val());
		$('#fileUploadAcquirer').val($("#acquirer").val());
		document.getElementById("batchAddBene").submit();
	    var content=$("iframe").contents().find('body').html();
	    validateFile();
	 });
});

function loadResultCsvTable() {
	$('table.resultcsv').DataTable({
        dom: 'B',
        buttons: [{
            extend: 'csv',
            text: 'Click here to download Result',
            filename: 'Add Beneficiary Result',
        }]
    });
}
var fileTypes = ['csv'];
// acceptable file types
function readURL(input) {
    if (input.files && input.files[0]) {
        var extension = input.files[0].name.split('.').pop().toLowerCase()
          , // file
        // extension
        // from
        // input
        // file
        isSuccess = fileTypes.indexOf(extension) > -1;
        // is extension in
        // acceptable types

        if (isSuccess) {
            // yes
            var reader = new FileReader();
            reader.onload = function(e) {
                if (extension == 'csv') {
                    $(input).closest('.fileUpload').find(".icon").attr('src', 'https://www.cortechslabs.com/wp-content/uploads/2017/06/CSV-icon-new.png');

                }
                else {
                    // debugger;
                    // console.log('here=>'+$(input).closest('.uploadDoc').length);
                    document.getElementById("batchFile").value = "";
                    // $("#up").replaceWith($("#up").val('').clone(true));
                    $(input).closest('.uploadDoc').find(".docErr").slideUp('slow');

                }
            }

            reader.readAsDataURL(input.files[0]);
        } else {
            // debugger;
            // console.log('here=>'+$(input).closest('.uploadDoc').find(".docErr").length);
            $(input).closest('.uploadDoc').find(".docErr").fadeIn();
            document.getElementById("batchFile").value = "";
            setTimeout(function() {
                $('.docErr').fadeOut('fast');

            }, 9000);
        }
    }
}
$(document).ready(function() {

    $(document).on('change', '.up', function() {
        var id = $(this).attr('id');
        /*
										 * gets the filepath and filename from
										 * the input
										 */
        var profilePicValue = $(this).val();
        var fileNameStart = profilePicValue.lastIndexOf('\\');
        /*
																 * finds the end
																 * of the
																 * filepath
																 */
        profilePicValue = profilePicValue.substr(fileNameStart + 1).substring(0, 20);
        /*
																						 * isolates
																						 * the
																						 * filename
																						 */
        // var profilePicLabelText = $(".upl"); /* finds the label text */
        if (profilePicValue != '') {
            // console.log($(this).closest('.fileUpload').find('.upl').length);
            $(this).closest('.fileUpload').find('.upl').html(profilePicValue);
            /*
																				 * changes
																				 * the
																				 * label
																				 * text
																				 */
        }
    });

    $(document).on("click", "a.btn-check", function() {
        if ($(".uploadDoc").length > 1) {
            $(this).closest(".uploadDoc").remove();
        } else {
            alert("You have to upload at least one document.");
        }
    });
});


// console.log("Loading bene js");

function validateFile() {
    let completeFileName = $('#batchFile').val();

    // If file is empty then return true.
    if (completeFileName == '' || completeFileName == undefined) {
        console.log("Empty file name");
        return true;
    }

    let fileNameArr = completeFileName.split("\\");
    let fileName = fileNameArr[fileNameArr.length - 1];

    // Validate filename.
    var validFilename = /^[a-zA-Z0-9_.() -]+$/i.test(fileName);
    if (!validFilename) {
        $("#batchFile").val('');
        $('#fileUpload').val('')
        alert("Invalid file name");
        return false;
    }

    // Validate file length or size.
    if (!(fileName.length >= 5 && fileName.length <= 50)) {
        $("#batchFile").val('');
        $('#fileUpload').val('')
        alert("Invalid Filename length.");
        return false;
    }

    // Validate file extension.
    var validExtension = /\.csv$/i.test(fileName);
    if (!validExtension) {
        $("#batchFile").val('');
        $('#fileUpload').val('')
        alert("Invalid File extension");
        return false;
    }

    // Validate file size.
    let fileSize = ($("#batchFile")[0].files[0].size / 1024 );
    if (fileSize > 1) {
        $("#batchFile").val('');
        $('#fileUpload').val('')
        alert("Invalid File size");
        return false;
    }

    return true;

}
function checkEmailIdSearch() {

    var emailexp = /^[_A-Za-z0-9-]+(\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\.[A-Za-z0-9-]+)*(\.[_A-Za-z0-9-]+)$/;
    var emailElement = document.getElementById("emailId");
    var emailValue = emailElement.value;
    if (emailValue.trim() !== "") {
        if (!emailValue.match(emailexp)) {
            document.getElementById('invalidid').innerHTML = "Please enter valid Email";

            return false;
        } else {
            document.getElementById('invalidid').innerHTML = "";

            return true;
        }
    } else {
        emailElement.focus();
        document.getElementById('invalidid').innerHTML = "";

        return true;
    }
}
function valdPhoneNoSearch() {
    var phoneElement = document.getElementById("mobile");
    var value = phoneElement.value.trim();
    if (value.length > 0) {
        var phone = phoneElement.value;
        var phoneexp = /^[0-9]{10,16}$/;
        if (!phone.match(phoneexp)) {
            document.getElementById('invalidphone').innerHTML = "Please enter valid Phone";

            return false;
        } else {
            document.getElementById('invalidphone').innerHTML = "";
            return true;
        }
    } else {
        phoneElement.focus();
        document.getElementById('invalidphone').innerHTML = "";
        return true;
    }
}